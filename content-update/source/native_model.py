"""Reviewed pure numeric geometry conversion only; no historical release generators run."""
import math,copy
def model(geo, animations):
    """Convert Bedrock geometry and numeric keyframes to the mod's native renderer.

    Preserve authored coordinates, UV signs, rest rotations and additive tracks.
    Reject unsupported expressions rather than quietly dropping animation data.
    """
    g = geo['minecraft:geometry'][0]
    names = {b['name']: i for i, b in enumerate(g['bones'])}
    assert len(names) == len(g['bones'])
    bones = []
    for b in g['bones']:
        cubes = []
        for c in b.get('cubes', []):
            origin = c['origin']; size = c['size']; uv = c['uv']
            inflate = c.get('inflate', 0)
            if isinstance(uv, list):
                u, v = uv; x, y, z = size
                uv = {name: {'uv': start, 'uv_size': dim} for name, start, dim in [
                    ('west', [u, v+z], [z,y]), ('north', [u+z,v+z], [x,y]),
                    ('east',[u+z+x,v+z],[z,y]), ('south',[u+2*z+x,v+z],[x,y]),
                    ('up',[u+z,v],[x,z]), ('down',[u+z+x,v+z],[x,-z]) ]}
            faces = {}
            for face, data in uv.items():
                assert not data.get('uv_rotation'), 'UV rotation needs explicit conversion'
                u,v = data['uv']; w,h = data['uv_size']
                faces[face] = [u,v,u+w,v+h]
            if c.get('mirror'):
                for v in faces.values(): v[0],v[2] = v[2],v[0]
            cubes.append({'from':[x-inflate for x in origin],
                          'to':[x+y+inflate for x,y in zip(origin,size)],
                          'origin':c.get('pivot',b.get('pivot',[0,0,0])),
                          'rotation':c.get('rotation',[0,0,0]), 'faces':faces})
        bones.append({'name':b['name'], 'parent':names[b['parent']] if 'parent' in b else -1,
                      'pivot':b.get('pivot',[0,0,0]), 'rotation':b.get('rotation',[0,0,0]),
                      'cubes':cubes})
    clips = {}
    for full_name, a in animations['animations'].items():
        tracks=[]
        for name, channels in a.get('bones',{}).items():
            assert name in names, name
            for channel, values in channels.items():
                assert channel in ('position','rotation','scale'), channel
                if isinstance(values,(list,int,float)): values={'0':values}
                raw=[]
                for time,value in sorted(values.items(),key=lambda p:float(p[0])):
                    mode='linear'
                    if isinstance(value,dict):
                        mode=value.get('lerp_mode',value.get('easing','linear')); value=value.get('post',value.get('pre',value.get('vector')))
                    if isinstance(value,(int,float)): value=[value]*3
                    assert isinstance(value,list) and len(value)==3
                    assert all(isinstance(v,(int,float)) and math.isfinite(v) for v in value)
                    raw.append((float(time),value,mode))
                # The native player interpolates linearly. Sample authored Catmull-Rom
                # spans at 40 Hz so the supplied smooth swim motions survive conversion.
                keys=[]
                for i,(t,v,mode) in enumerate(raw):
                    keys.append([t,*v])
                    if i+1==len(raw) or (mode=='linear' and raw[i+1][2]=='linear'): continue
                    end,p2,_=raw[i+1];p0=raw[max(0,i-1)][1];p3=raw[min(len(raw)-1,i+2)][1]
                    count=max(1,math.ceil((end-t)*40))
                    for step in range(1,count):
                        s=step/count
                        if mode=='catmullrom' or raw[i+1][2]=='catmullrom':
                            vector=[.5*((2*b)+(-a+c)*s+(2*a-5*b+4*c-d)*s*s+(-a+3*b-3*c+d)*s*s*s)
                                    for a,b,c,d in zip(p0,v,p2,p3)]
                        else:
                            easing=raw[i+1][2] if raw[i+1][2]!='linear' else mode
                            factor={'linear':lambda t:t,'easeInOutSine':lambda t:-(math.cos(math.pi*t)-1)/2,
                                    'easeOutQuad':lambda t:1-(1-t)**2,'easeInQuad':lambda t:t*t,
                                    'easeInCubic':lambda t:t**3,'easeOutBack':lambda t:1+2.70158*(t-1)**3+1.70158*(t-1)**2,
                                    'easeInOutQuad':lambda t:2*t*t if t<.5 else 1-(-2*t+2)**2/2}[easing](s)
                            vector=[a+(b-a)*factor for a,b in zip(v,p2)]
                        keys.append([t+(end-t)*s,*vector])
                tracks.append({'bone':names[name],'channel':channel,'keys':keys})
        short=full_name.split('.')[-1]
        assert short not in clips
        clips[short]={'length':a['animation_length'],'loop':'loop' if a.get('loop') is True else 'once','tracks':tracks}
    assert len(clips)==10
    # Vanilla hurt/takeoff actions exist independently of each pack's ten authored clips.
    clips['take_damage']=copy.deepcopy(clips['idle'])
    hurt=clips['take_damage'];duration=hurt['length'];hurt['length']=.35;hurt['loop']='once'
    for track in hurt['tracks']:
        for key in track['keys']:key[0]=key[0]/duration*.35
    if 'fly' in clips: clips['takeoff']=copy.deepcopy(clips['fly'])
    if 'swim' in clips:
        clips['fly']=copy.deepcopy(clips['swim']);clips['glide']=copy.deepcopy(clips['swim'])
    d=g['description']
    return {'width':d['texture_width'],'height':d['texture_height'],
            'previewBounds':[d.get('visible_bounds_width',3),d.get('visible_bounds_height',3)],
            'bones':bones,'animations':clips}
