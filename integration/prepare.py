"""Convert supplied creature/biome packs to native Soulbound Army resources.

Usage: PYTHONPATH=<nbtlib installation> python prepare.py --inputs DIR --base BASE.jar --out DIR
The original binary is never edited in place. See README.md for build/test limits.
"""
import argparse
import copy
import gzip
import hashlib
import io
import json
import math
import re
import struct
import zlib
from pathlib import Path
from zipfile import ZipFile

import nbtlib

NS = 'shadowsovereign'
MAPPINGS = {'eternal_abyss': 'abyss_', 'sunken_kingdom': 'sunken_'}


def transparent_png(width=128,height=128):
    def chunk(kind,payload):
        return struct.pack('>I',len(payload))+kind+payload+struct.pack('>I',zlib.crc32(kind+payload))
    return b'\x89PNG\r\n\x1a\n'+chunk(b'IHDR',struct.pack('>IIBBBBB',width,height,8,6,0,0,0))+chunk(b'IDAT',zlib.compress(bytes((width*4+1)*height)))+chunk(b'IEND',b'')


def remap(s):
    for ns, prefix in MAPPINGS.items():
        s = re.sub(rf'{ns}:((?:block|item|chests)/)?([a-z0-9_/]+)',
                   lambda m: f'{NS}:{m[1] or ""}{prefix}{m[2]}', s)
    return s


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


def build(inputs, base_path, out):
    out.mkdir(parents=True,exist_ok=True)
    with ZipFile(base_path) as base:
        def old(n): return json.loads(base.read(f'data/{NS}/{n}.json'))
        def write(path,data):
            p=out/path;p.parent.mkdir(parents=True,exist_ok=True)
            p.write_bytes(data if isinstance(data,bytes) else (json.dumps(data,ensure_ascii=False,indent=2)+'\n').encode())
        def data(path,obj): write(f'data/{NS}/{path}.json',obj)
        def find(name):
            found=list(inputs.rglob(name));assert len(found)==1,(name,found)
            return found[0]
        spec={'profiles':[],'blocks':[],'base_sha256':hashlib.sha256(base_path.read_bytes()).hexdigest()}
        lang={loc:json.loads(base.read(f'assets/{NS}/lang/{loc}.json')) for loc in ['en_us','ar_sa']}
        packs=[('Mokhtar_El_Desouky','mokhtar_el_desouky','مختار الدسوقي',500,18,8,.28,.8,2.1,False,
                [('punch_attack',.55,0,3.6,1),('defensive_counter',.8,5,0,0),('command',1.2,51,10,0)]),
               ('Broken_Star_Owlet','broken_star_owlet','بومة النجمة المكسورة',300,14,4,.3,1.2,1.5,True,
                [('starfall_attack',1.8,47,14,1),('celestial_shield',1.2,5,0,0)]),
               ('Essam_El_Nemr','essam_el_nemr','عصام النمر',600,22,10,.3,.8,2.1,False,
                [('punch_combo',1.15,0,3.6,1),('counter_attack',.8,5,0,0),('power_stomp',1.1,1,7,.9),('intimidation',1,3,8,.4)]),
               ('Lumen_Cub','lumen_cub','شبل اللومن',240,10,4,.3,.85,1.1,False,
                [('playful_jump',.5,0,3.2,1),('crystal_glow',1.2,5,0,0)]),
               ('Pearl_Drakeling','pearl_drakeling','تنين اللؤلؤ الصغير',280,12,4,.28,.85,1.2,False,
                [('bubble_play',1.3,47,12,1),('pearl_glow',1.3,5,0,0)]),
               ('Shattered_Pangolin','shattered_pangolin','آكل النمل المتشظي',360,14,10,.28,1.2,1.2,False,
                [('crystal_burst',2.4,47,14,1),('curl_up',.8,5,0,0),('roll',.4,8,8,1.1)]),
               ('Cursed_Bell_Ringer','cursed_bell_ringer','قارِع الجرس الملعون',900,24,14,.22,2.2,4.0,False,
                [('bell_slam',1.5,0,5,1.2),('sonic_wave_attack',1.7,21,10,1),('chain_sweep',1.5,1,7,.9),('resonance_burst',2.0,1,9,1.1)]),
               ('Weeping_Axolotl','weeping_axolotl','الأكسولوتل الباكي',220,12,4,.28,.9,.7,False,
                [('heart_wave_attack',1.4,47,12,1),('bubble_shield',1.2,5,0,0)]),
               ('Legendary_Giant_Zombie_Refai','legendary_giant_zombie_refai','رفاعي الزومبي العملاق الأسطوري',1200,28,16,.24,2.4,4.4,False,
                [('cleaver_attack',1.6,0,6,1.2),('sweep_attack',1.7,1,8,1),('ground_slam',1.9,1,10,1.25),('chain_rage',2,2,12,.8)]),
               ('Amin_El_Nemr','amin_el_nemr','أمين النمر',480,18,8,.30,.6,1.95,False,
                [('punch_combo',1.3,0,3.8,1),('defensive_counter',1.1,5,0,0),('intimidation',1.5,3,8,.4)]),
               ('King_Of_Corny_Memes','king_of_corny_memes','ملك الميمز البايخة',1100,24,12,.25,2.0,4.2,False,
                [('bad_joke_attack',1.04,21,9,.65),('cheese_meteor',2.02,47,16,1.05),
                 ('royal_facepalm',1.04,5,0,0),('cringe_beam',1.4,6,16,1),('ultimate_punchline',2.3,1,11,1.25)])]
        for index,(source,id,ar,hp,damage,armor,speed,width,height,flies,moves) in enumerate(packs,38):
            geo=json.loads(find(source+'.geo.json').read_text());anim=json.loads(find(source+'.animation.json').read_text())
            native=model(geo,anim);write(f'assets/{NS}/mythic/{id}.json',native)
            write(f'assets/{NS}/textures/entity/{id}.png',find(source+'_Texture.png').read_bytes())
            glows=list(inputs.rglob(source+'_Glowmask.png'))+list(inputs.rglob(source+'_Glow_Mask.png'))
            assert len(glows)<=1,('Ambiguous glow mask',source)
            write(f'assets/{NS}/textures/entity/{id}_glow.png',glows[0].read_bytes() if glows else transparent_png())
            walk,run=('swim','swim_fast') if id=='pearl_drakeling' else ('walk','swim_fast') if id=='weeping_axolotl' else ('walk','fly' if flies else 'run')
            spec['profiles'].append(dict(id=id,index=index,health=hp,damage=damage,armor=armor,speed=speed,
                                         width=width,height=height,scale=.5 if id=='weeping_axolotl' else .7 if id=='shattered_pangolin' else 1,walk=walk,run=run,flies=flies,
                                         rideable=False,aquatic=id in ('pearl_drakeling','weeping_axolotl'),moves=moves))
            title=source.replace('_',' ')
            for loc in lang:
                label=ar if loc=='ar_sa' else title
                lang[loc]['entity.shadowsovereign.'+id]=label
                lang[loc]['item.shadowsovereign.'+id+'_spawn_egg']=('بيضة استدعاء ' if loc=='ar_sa' else '')+label+(' Spawn Egg' if loc=='en_us' else '')
                lang[loc]['grimoire.shadowsovereign.lore.'+id]=('رفيق أسطوري ينضم إلى جيشك بعد هزيمته في موطنه.' if loc=='ar_sa' else 'A mythical companion earned by defeating its guardian in its habitat.')
                lang[loc]['grimoire.shadowsovereign.abilities.'+id]=('قدراته: ' if loc=='ar_sa' else 'Abilities: ')+', '.join(m[0].replace('_',' ') for m in moves)
            write(f'assets/{NS}/models/item/{id}_spawn_egg.json',{'parent':'minecraft:item/template_spawn_egg'})

        # Register every new block in the existing GloomContent registry/maps. All
        # namespace rewrites are also applied to models, structure palettes and loot.
        for namespace,prefix in MAPPINGS.items():
            manifest=next(p for p in inputs.rglob('asset_manifest.json') if isinstance(d:=json.loads(p.read_text()),dict) and d.get('namespace')==namespace)
            assets=json.loads(manifest.read_text())['assets']
            asset_root=manifest.parent/'assets'/namespace
            for p in asset_root.rglob('*'):
                if not p.is_file(): continue
                relative=p.relative_to(asset_root);parts=list(relative.parts)
                if parts[0]=='lang': continue
                parts[-1]=prefix+parts[-1]
                content=remap(p.read_text()).encode() if p.suffix=='.json' else p.read_bytes()
                write(f'assets/{NS}/'+ '/'.join(parts),content)
            for a in assets:
                original=a['id'].split(':')[1];id=prefix+original
                layer=a.get('render_layer','');kind=a.get('model_type','')
                plants={'abyss_grass','hanging_soul_roots','emerald_lantern_flower','void_fern','crystal_thorn','abyss_reed','soul_glow_mushroom','gloomcap_spore','voidshroom_spore'}
                aquatic={'moon_coral','crimson_fan_coral','ghost_coral','spirit_kelp','deepwater_grass','luminous_anemone','pearl_flower','abyssal_reeds','floating_spore_plant','ancient_shell_cluster'}
                category='stone'
                if original in plants: category='plant'
                if original in aquatic: category='aquatic'
                if original.endswith(('_log','_stem')):category='log'
                if original.endswith('_leaves'):category='leaves'
                if original.endswith('_sapling'):category='sapling'
                if layer=='translucent':category='glass'
                spec['blocks'].append(dict(id=id,category=category,light=a.get('suggested_light_level',0),hardness=a.get('suggested_hardness',1.5)))
                if category in ('plant','aquatic','sapling','leaves','glass'):
                    # Explicit Forge model layers also cover HangingRootsBlock,
                    # which is not covered by the base mod's BushBlock filter.
                    model_path=out/f'assets/{NS}/models/block/{id}.json'
                    block_model=json.loads(model_path.read_text())
                    block_model.setdefault('render_type','minecraft:translucent' if category=='glass' else
                                           'minecraft:cutout_mipped' if category=='leaves' else 'minecraft:cutout')
                    write(str(model_path.relative_to(out)),block_model)
                label=a.get('display_name',a.get('name',original.replace('_',' ').title()))
                for loc in lang:
                    lang[loc]['block.shadowsovereign.'+id]=label
                data('loot_tables/blocks/'+id,{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':f'{NS}:{id}'}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
                # Leaves persist until explicit replacement; seagrass uses water as
                # its fluid state and can be planted on the custom seabed blocks.

        decor_manifest=next(p for p in inputs.rglob('asset_manifest.json') if 'Decor_Pack_01' in str(p))
        decor_root=decor_manifest.parent.parent
        decor=json.loads(decor_manifest.read_text())
        for entry in decor:
            id='decor_'+entry['id'];original=entry['id']
            model_text=(decor_root/'Models/Block_JSON'/f'{original}.json').read_text().replace('soulbound_decor:block/','shadowsovereign:block/decor_')
            block_model=json.loads(model_text);block_model['render_type']='minecraft:cutout'
            write(f'assets/{NS}/models/block/{id}.json',block_model)
            write(f'assets/{NS}/models/item/{id}.json',{'parent':f'{NS}:block/{id}'})
            write(f'assets/{NS}/blockstates/{id}.json',{'variants':{f'facing={direction}':{'model':f'{NS}:block/{id}','y':angle} for direction,angle in [('north',0),('east',90),('south',180),('west',270)]}})
            write(f'assets/{NS}/textures/block/{id}.png',(decor_root/'Textures'/f'{original}.png').read_bytes())
            write(f'assets/{NS}/textures/block/{id}_e.png',(decor_root/'Textures/Emissive'/f'{original}_emissive.png').read_bytes())
            spec['blocks'].append(dict(id=id,category='decor',light=entry['suggested_light_level'],hardness=1.5))
            for loc in lang:lang[loc]['block.shadowsovereign.'+id]=entry['title']
            data('loot_tables/blocks/'+id,{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':f'{NS}:{id}'}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
        spec['decor_ids']=['decor_'+a['id'] for a in decor]
        spec['vanilla_1_20_remaps']={'minecraft:short_grass':'minecraft:grass','minecraft:waxed_chiseled_copper':'minecraft:waxed_cut_copper',
            'minecraft:pink_coral':'minecraft:brain_coral','minecraft:pink_coral_block':'minecraft:brain_coral_block','minecraft:pink_coral_fan':'minecraft:brain_coral_fan'}
        # Structure templates: use native Java format, map all custom names and
        # remove marker entities/blocks (habitat catalog supplies encounters).
        structures={}
        for p in inputs.rglob('*.nbt'):
            n=nbtlib.load(p)
            n['DataVersion']=nbtlib.Int(3465)
            for state in n['palette']:
                state['Name']=nbtlib.String(remap(str(state['Name'])))
                state['Name']=nbtlib.String(spec['vanilla_1_20_remaps'].get(str(state['Name']),str(state['Name'])))
                if str(state['Name'])=='minecraft:structure_block':
                    state['Name']=nbtlib.String('minecraft:air');state.pop('Properties',None)
            for b in n['blocks']:
                if 'nbt' in b:
                    if str(b['nbt'].get('id',''))=='minecraft:structure_block': b.pop('nbt')
                    elif 'LootTable' in b['nbt']:
                        b['nbt']['LootTable']=nbtlib.String(remap(str(b['nbt']['LootTable'])))
                    if 'nbt' in b and str(b['nbt'].get('id',''))=='minecraft:banner' and 'patterns' in b['nbt']:
                        # Supplied 1.21 banners contain literal "color"/"pattern"
                        # placeholders. Supply the documented bell-tower motif
                        # using the actual Java 1.20.1 banner NBT schema.
                        b['nbt'].pop('patterns')
                        b['nbt']['Patterns']=nbtlib.List[nbtlib.Compound]([
                            nbtlib.Compound({'Color':nbtlib.Int(color),'Pattern':nbtlib.String(pattern)})
                            for color,pattern in [(3,'mr'),(9,'mc'),(3,'cr'),(9,'bo')]])
                    if 'nbt' in b:
                        for item in b['nbt'].get('Items',[]):
                            if 'count' in item:
                                count=int(item.pop('count'))
                                if not 1<=count<=127:raise ValueError(f'Unsupported item stack size: {count}')
                                item['Count']=nbtlib.Byte(count)
            n['entities']=nbtlib.List[nbtlib.Compound]()
            name=p.stem.lower();buf=io.BytesIO();n.write(buf)
            write(f'data/{NS}/structures/expansion/{name}.nbt',gzip.compress(buf.getvalue(),mtime=0))
            structures[name]=n
        # Sunken pack's authored loot; Eternal pack has none, supply a native table.
        for p in inputs.rglob('loot_tables/*.json'):
            data('loot_tables/chests/sunken_'+p.stem,json.loads(remap(p.read_text())))
        loot={'type':'minecraft:chest','pools':[{'rolls':{'type':'minecraft:uniform','min':2,'max':4},'entries':[
            {'type':'minecraft:item','name':'minecraft:emerald','weight':5,'functions':[{'function':'minecraft:set_count','count':{'type':'minecraft:uniform','min':1,'max':4}}]},
            {'type':'minecraft:item','name':'minecraft:amethyst_shard','weight':4},
            {'type':'minecraft:item','name':'minecraft:gold_ingot','weight':2}]}]}
        # The template reference names are authoritative; create missing Eternal
        # loot IDs individually rather than allowing silent empty chests.
        for n in structures.values():
            for b in n['blocks']:
                table=str(b.get('nbt',{}).get('LootTable',''))
                if table.startswith(NS+':'):
                    target=out/f'data/{NS}/loot_tables/{table.split(":")[1]}.json'
                    if not target.exists():write(str(target.relative_to(out)),loot)

        sites=old('mythic_sites')
        encounters=[('old_desouky_family_house',[10,6,16],'crimson_shadow',-4),
                    ('abandoned_star_library',[13,21,13],'shattered_sky',-5),
                    ('el_nemr_mansion',[21,7,29],'crimson_shadow',-5),
                    ('mushroom_sovereign_sanctuary',[12,3,20],'eternal_abyss',-3),
                    ('pearl_sanctuary',[11,4,20],'sunken_kingdom',-1),
                    # The supplied corner marker is in a narrow room. Use the
                    # open aisle so the 1.2-block-wide body fits the spawn search.
                    ('shattered_porcelain_temple',[20,1,30],'shattered_sky',-5),
                    ('cursed_bell_tower',[13,8,16],'ashen_monolith',-6),
                    ('drowned_glass_palace',[19,8,19],'sunken_kingdom',-6),
                    ('refai_cursed_stronghold',[24,10,33],'crimson_shadow',-9),
                    ('amin_el_nemr_workshop',[18,5,32],'crimson_shadow',-4),
                    ('palace_of_eternal_cringe',[27,6,33],'shattered_sky',-5)]
        # Add all 20 decorations to safe, formerly empty positions in the new
        # dry structures. No chest, spawn pad or existing block is replaced.
        remaining=list(spec['decor_ids']);placements=[]
        for name,altar,biome,offset in encounters[5:]:
            if name in ('drowned_glass_palace','amin_el_nemr_workshop','palace_of_eternal_cringe'):continue
            n=structures[name];size=list(map(int,n['size']));occupied={tuple(map(int,b['pos'])):b for b in n['blocks']}
            budget=7 if name!='refai_cursed_stronghold' else len(remaining)
            for pos,block in sorted(occupied.items()):
                if budget==0 or not remaining:break
                x,y,z=pos
                if y!= -offset or not (2<=x<size[0]-2 and 2<=z<size[2]-2):continue
                if min(x,z,size[0]-x,size[2]-z)>6 or (x-altar[0])**2+(z-altar[2])**2<25:continue
                target=(x,y+1,z);head=(x,y+2,z)
                floor=str(n['palette'][int(block['state'])]['Name'])
                if any(word in floor for word in ('air','water','grass','carpet','slab','stairs','fence','trapdoor','chest','barrel')):continue
                if any(q not in occupied or str(n['palette'][int(occupied[q]['state'])]['Name'])!='minecraft:air' for q in (target,head)):continue
                if any((x-a)**2+(z-c)**2<9 for a,b,c in (p['position'] for p in placements if p['structure']==name)):continue
                id=remaining.pop(0);idx=len(n['palette'])
                n['palette'].append(nbtlib.Compound({'Name':nbtlib.String(f'{NS}:{id}'),'Properties':nbtlib.Compound({'facing':nbtlib.String('north')})}))
                occupied[target]['state']=nbtlib.Int(idx);budget-=1;placements.append({'id':id,'structure':name,'position':list(target)})
            buf=io.BytesIO();n.write(buf);write(f'data/{NS}/structures/expansion/{name}.nbt',gzip.compress(buf.getvalue(),mtime=0))
        if remaining:raise ValueError(f'No safe structure positions for decor: {remaining}')
        spec['decor_placements']=placements
        placed=[]
        for i,(name,altar,biome,offset) in enumerate(encounters,38):
            structure='expansion_'+name
            sites.append({'kind':i,'structure':structure,'altar':altar,'rootOffset':[0,0,0]})
        habitat_names={e[0]:e for e in encounters}
        tree_names={'gloomcap_giant','voidshroom_giant','tidewood_tree','tidewood_tree_small'}
        salts=json.loads(Path(__file__).with_name('structure_salts.json').read_text())
        for name,n in structures.items():
            if name in habitat_names: biome,offset=habitat_names[name][2:]
            elif name in tree_names: biome,offset=('eternal_abyss' if name in ('gloomcap_giant','voidshroom_giant') else 'sunken_kingdom'),-1
            else:biome,offset=('eternal_abyss' if name in ('abyssal_bridge_fortress','abyssal_crystal_cathedral','forgotten_mine') else 'sunken_kingdom'),-1
            sid='expansion_'+name
            st=old('worldgen/structure/shattered_gentlemans_manor')
            st.update(biomes=f'{NS}:{biome}',start_pool=f'{NS}:{sid}',start_height={'absolute':offset})
            if biome=='sunken_kingdom':st['project_start_to_heightmap']='OCEAN_FLOOR_WG'
            data('worldgen/structure/'+sid,st)
            pool=old('worldgen/template_pool/shattered_gentlemans_manor');pool['name']=f'{NS}:{sid}'
            pool['elements'][0]['element']['location']=f'{NS}:expansion/{name}'
            data('worldgen/template_pool/'+sid,pool)
            ss=old('worldgen/structure_set/shattered_gentlemans_manor');ss['structures'][0]['structure']=f'{NS}:{sid}'
            # Preserve previous worlds' placement seeds regardless of archive
            # extraction/directory iteration order or how many packs are added.
            ss['placement'].update(salt=salts[name],spacing=10 if name in tree_names else 32,separation=3 if name in tree_names else 12)
            data('worldgen/structure_set/'+sid,ss)
            placed.append(sid)
        data('mythic_sites',sites)
        dim=old('dimension/echo_realms')
        for biome,t,h in [('eternal_abyss',-.9,.0),('sunken_kingdom',.0,1.5)]:
            entry=copy.deepcopy(dim['generator']['biome_source']['biomes'][0]);entry['biome']=f'{NS}:{biome}'
            entry['parameters'].update(temperature=t,humidity=h);dim['generator']['biome_source']['biomes'].append(entry)
            b=old('worldgen/biome/shattered_sky');b['features']=[[] for _ in range(11)]
            b['effects'].update(fog_color=0x102B27 if biome=='eternal_abyss' else 0x15384D,water_color=0x173D48,water_fog_color=0x102C40,sky_color=0x071921,
                                grass_color=0x27594B,foliage_color=0x327E79)
            b['effects']['particle']={'probability':.003,'options':{'type':'minecraft:spore_blossom_air' if biome=='eternal_abyss' else 'minecraft:bubble'}}
            # Avoid inheriting caves above the custom ocean floor; terrain is
            # already noise-shaped and the new biome uses authored decorations.
            if biome=='sunken_kingdom':b['carvers']={'air':[]}
            flora=([x for x in spec['blocks'] if x['id'].startswith('abyss_') and x['category']=='plant' and x['id']!='abyss_hanging_soul_roots'] if biome=='eternal_abyss' else
                   [x for x in spec['blocks'] if x['category']=='aquatic'])
            for block in flora:
                feature=block['id']+'_patch'
                data('worldgen/configured_feature/'+feature,{'type':'minecraft:random_patch','config':{'tries':24,'xz_spread':5,'y_spread':2,'feature':{
                    'feature':{'type':'minecraft:simple_block','config':{'to_place':{'type':'minecraft:simple_state_provider','state':{'Name':f"{NS}:{block['id']}"}}}},'placement':[]}}})
                data('worldgen/placed_feature/'+feature,{'feature':f'{NS}:{feature}','placement':[{'type':'minecraft:count','count':3},{'type':'minecraft:in_square'},
                     {'type':'minecraft:heightmap','heightmap':'OCEAN_FLOOR_WG' if biome=='sunken_kingdom' else 'WORLD_SURFACE_WG'},{'type':'minecraft:biome'}]})
                b['features'][9].append(f'{NS}:{feature}')
            ore='abyss_emerald_soul_ore' if biome=='eternal_abyss' else 'sunken_tide_crystal_ore'
            data('worldgen/configured_feature/'+ore,{'type':'minecraft:ore','config':{'size':6,'discard_chance_on_air_exposure':0,'targets':[{
                'target':{'predicate_type':'minecraft:tag_match','tag':'minecraft:base_stone_overworld'},'state':{'Name':f'{NS}:{ore}'}}]}})
            data('worldgen/placed_feature/'+ore,{'feature':f'{NS}:{ore}','placement':[{'type':'minecraft:count','count':6},{'type':'minecraft:in_square'},
                 {'type':'minecraft:height_range','height':{'type':'minecraft:uniform','min_inclusive':{'absolute':-48},'max_inclusive':{'absolute':30}}},{'type':'minecraft:biome'}]})
            b['features'][6].append(f'{NS}:{ore}')
            data('worldgen/biome/'+biome,b)
            for loc in lang:lang[loc]['biome.shadowsovereign.'+biome]=('الهاوية الأبدية' if biome=='eternal_abyss' else 'المملكة الغارقة') if loc=='ar_sa' else biome.replace('_',' ').title()
        data('dimension/echo_realms',dim)
        noise=old('worldgen/noise_settings/echo_expanses')
        # The existing dimension uses a shared density function. Lower the floor
        # only in the humidity band dominated by the new ocean biome, retaining
        # its existing sea level and the old terrain elsewhere.
        for key in ('final_density','initial_density_without_jaggedness'):
            density=noise['noise_router'][key]
            noise['noise_router'][key]={'type':'minecraft:range_choice','input':copy.deepcopy(noise['noise_router']['vegetation']),
                                      'min_inclusive':1.0,'max_exclusive':1000000,'when_in_range':{'type':'minecraft:add','argument1':density,'argument2':-.42},'when_out_of_range':copy.deepcopy(density)}
        seq=noise['surface_rule']['sequence']
        for biome,top,under,stone in [('eternal_abyss','abyss_abyss_moss','abyss_abyss_soil','abyss_abyssal_stone'),('sunken_kingdom','sunken_abyssal_sand','sunken_sunken_stone','sunken_sunken_stone')]:
            rule={'type':'minecraft:condition','if_true':{'type':'minecraft:biome','biome_is':[f'{NS}:{biome}']},'then_run':{
                'type':'minecraft:sequence','sequence':[
                    {'type':'minecraft:condition','if_true':{'type':'minecraft:stone_depth','offset':0,'add_surface_depth':False,'secondary_depth_range':0,'surface_type':'floor'},'then_run':{'type':'minecraft:block','result_state':{'Name':f'{NS}:{top}'}}},
                    {'type':'minecraft:condition','if_true':{'type':'minecraft:stone_depth','offset':3,'add_surface_depth':False,'secondary_depth_range':0,'surface_type':'floor'},'then_run':{'type':'minecraft:block','result_state':{'Name':f'{NS}:{under}'}}},
                    {'type':'minecraft:block','result_state':{'Name':f'{NS}:{stone}'}}]}}
            seq.insert(1,rule)
        data('worldgen/noise_settings/echo_expanses',noise)
        # Ground-plant survival and block tools, without replacing base tag values.
        dirt=old('tags/blocks/dirt') if 'data/shadowsovereign/tags/blocks/dirt.json' in base.namelist() else {'replace':False,'values':[]}
        for tag,names in [('dirt',['abyss_abyss_moss','abyss_abyss_soil','sunken_abyssal_sand'])]:
            target=f'data/minecraft/tags/blocks/{tag}.json'
            t=json.loads(base.read(target));t['values']+= [f'{NS}:{n}' for n in names];write(target,t)
        for tag,category in [('base_stone_overworld','stone'),('logs','log'),('logs_that_burn','log'),('leaves','leaves'),('saplings','sapling')]:
            target=f'data/minecraft/tags/blocks/{tag}.json'
            t=json.loads(base.read(target)) if target in base.namelist() else {'replace':False,'values':[]}
            t['values']+=[f"{NS}:{b['id']}" for b in spec['blocks'] if b['category']==category];write(target,t)
        for tag in ['mineable/pickaxe','mineable/axe','mineable/shovel']:
            target=f'data/minecraft/tags/blocks/{tag}.json';t=json.loads(base.read(target))
            category='log' if tag.endswith('axe') and not tag.endswith('pickaxe') else 'stone'
            if tag.endswith('shovel'):names=['abyss_abyss_soil','abyss_abyss_moss','sunken_abyssal_sand']
            else:names=[b['id'] for b in spec['blocks'] if b['category']==category]
            t['values']+=[f'{NS}:{n}' for n in names];write(target,t)
        for loc,entries in lang.items():write(f'assets/{NS}/lang/{loc}.json',entries)
        spec['structures']=placed
        spec['original_profile_count']=38
        write('integration-spec.json',spec)
        return spec


if __name__=='__main__':
    p=argparse.ArgumentParser();p.add_argument('--inputs',type=Path,required=True);p.add_argument('--base',type=Path,required=True);p.add_argument('--out',type=Path,required=True)
    a=p.parse_args();s=build(a.inputs,a.base,a.out)
    print(f"Prepared {len(s['profiles'])} profiles, {len(s['blocks'])} blocks, {len(s['structures'])} structure sets")
