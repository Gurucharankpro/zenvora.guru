import json, uuid, base64, math, numpy as np
from PIL import Image, ImageDraw
import mob_model as M, mob_uv as U, mob_anim as MA, mob_render as R
POS,_=U.pack()
def r(v):
    v=round(v,4); return int(v) if v==int(v) else v
def vec(l): return [r(x) for x in l]
bones=[]
for b in M.BONES:
    o={'name':b['name']}
    if b['parent']: o['parent']=b['parent']
    o['pivot']=vec(b['pivot'])
    if any(b['rotation']): o['rotation']=vec(b['rotation'])
    cs=[]
    for c in b['cubes']:
        fr=U.faces(POS[c['uvkey']],c['size'],c.get('sc',2))
        cc={'origin':vec(c['origin']),'size':vec(c['size']),'uv':{k:{'uv':[u,v],'uv_size':[w,h]} for k,(u,v,w,h) in fr.items()}}
        if 'rotation' in c: cc['pivot']=vec(c['pivot']); cc['rotation']=vec(c['rotation'])
        cs.append(cc)
    if cs: o['cubes']=cs
    bones.append(o)
geo={'format_version':'1.12.0','minecraft:geometry':[{'description':{'identifier':'geometry.morsi','texture_width':128,'texture_height':128,
     'visible_bounds_width':3,'visible_bounds_height':3,'visible_bounds_offset':[0,1,0]},'bones':bones}]}
json.dump(geo,open('Morsi.geo.json','w'),indent=2)
MA.dump('Morsi.animation.json')
def U4(): return str(uuid.uuid4())
png=open('Morsi_Texture.png','rb').read()
gid={b['name']:U4() for b in M.BONES}
elements=[]; eb={b['name']:[] for b in M.BONES}
flip=lambda rot:[-rot[0],-rot[1],rot[2]]
for b in M.BONES:
    for c in b['cubes']:
        fr=U.faces(POS[c['uvkey']],c['size'],c.get('sc',2))
        faces={k:{'uv':[u,v,u+w,v+h],'texture':0} for k,(u,v,w,h) in fr.items()}
        e={'name':c['name'],'box_uv':False,'rescale':False,'locked':False,'light_emission':0,'render_order':'default','allow_mirror_modeling':True,
           'from':vec(c['origin']),'to':vec([c['origin'][i]+c['size'][i] for i in range(3)]),'autouv':0,'color':0,'origin':vec(c.get('pivot',b['pivot'])),
           'faces':faces,'type':'cube','uuid':U4()}
        if 'rotation' in c: e['rotation']=vec(flip(c['rotation']))
        elements.append(e); eb[b['name']].append(e['uuid'])
def group(b):
    g={'name':b['name'],'origin':vec(b['pivot']),'rotation':vec(flip(b['rotation'])),'color':0,'uuid':gid[b['name']],'export':True,'mirror_uv':False,'isOpen':False,'locked':False,'visibility':True,'autouv':0}
    g['children']=list(eb[b['name']])+[group(ch) for ch in M.children(b['name'])]; return g
outliner=[group(b) for b in M.BONES if b['parent'] is None]
anims=[]
for a in MA.ANIMS:
    an={'uuid':U4(),'name':'animation.morsi.'+a.name,'loop':'loop' if a.loop else ('hold' if a.hold else 'once'),'override':False,'length':a.length,'snapping':24,'selected':False,'anim_time_update':'','blend_weight':'','start_delay':'','loop_delay':'','animators':{}}
    for bn,chs in a.d.items():
        kfs=[]
        for ch,lst in chs.items():
            for t,v,ease,lerp in sorted(lst,key=lambda e:e[0]):
                x,y,z=v
                if ch=='rotation': x,y=-x,-y
                if ch=='position': x=-x
                kfs.append({'channel':ch,'data_points':[{'x':x,'y':y,'z':z}],'uuid':U4(),'time':t,'color':-1,'interpolation':'catmullrom' if lerp=='catmullrom' else 'linear'})
        an['animators'][gid[bn]]={'name':bn,'type':'bone','keyframes':kfs}
    anims.append(an)
bb={'meta':{'format_version':'4.10','creation_time':1761800000,'model_format':'bedrock','box_uv':False},'name':'Morsi','model_identifier':'morsi','visible_box':[3,3,1],
    'variable_placeholders':'','variable_placeholder_buttons':[],'bedrock_animation_mode':'entity','timeline_setups':[],'unhandled_root_fields':{},'resolution':{'width':128,'height':128},
    'elements':elements,'outliner':outliner,'textures':[{'path':'','name':'Morsi_Texture.png','folder':'','namespace':'','id':'0','width':128,'height':128,'uv_width':128,'uv_height':128,'particle':False,
    'use_as_default':True,'layers_enabled':False,'sync_to_project':'','render_mode':'default','render_sides':'auto','frame_time':1,'frame_order_type':'loop','frame_order':'','frame_interpolate':False,
    'visible':True,'internal':True,'saved':True,'uuid':U4(),'source':'data:image/png;base64,'+base64.b64encode(png).decode()}],'animations':anims}
json.dump(bb,open('Morsi.bbmodel','w'))
# preview
tex,gl=R.load()
def view(yaw,pitch=6,W=330,H=470,sc=12): return R.render({},tex,gl,yaw=yaw,pitch=pitch,scale=sc,W=W,H=H,hide=['phone'])
def af(n,t,yaw,sc=8,W=330,H=330):
    a=[x for x in MA.ANIMS if x.name==n][0]; return R.render(MA.evaluate(a,t),tex,gl,yaw=yaw,pitch=8,scale=sc,W=W,H=H)
def face_close():
    return R.render({},tex,gl,yaw=0,pitch=2,scale=60,W=330,H=470,hide=['phone'],)
S=Image.new('RGBA',(330*4,30+470+330*2),(26,26,34,255)); d=ImageDraw.Draw(S)
d.text((10,8),'MOB 57 - MORSI (Minecraft-style character) | preview from the included software renderer - not Blockbench / in-game',fill=(230,230,230,255))
for i,im in enumerate((view(0),view(35),view(90),view(180))): S.paste(im,(330*i,30))
sel=[('idle',1.0,25,'idle'),('walk',0.15,60,'walk'),('run',0.1,70,'run'),('phone_call',1.5,35,'phone_call'),('argument',1.15,25,'argument'),('punch_attack',0.64,60,'punch_attack'),('defensive_counter',0.75,25,'defensive_counter'),('nervous_reaction',0.7,25,'nervous_reaction')]
for i,(n,t,y,l) in enumerate(sel):
    im=af(n,t,y); x=330*(i%4); yy=500+330*(i//4); S.paste(im,(x,yy)); d.text((x+8,yy+6),l,fill=(255,255,255,255))
S.convert('RGB').save('Morsi_Preview.png')
# face close-up for review
fc=R.render({},tex,gl,yaw=0,pitch=0,scale=40,W=500,H=500,hide=['phone']); fc.save('face_closeup.png')
print('export ok',len(elements),'cubes',len(bones),'bones')
