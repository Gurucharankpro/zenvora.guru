"""Morsi - Minecraft-style human character. px units, feet y=0, front=-Z, model-left=+X."""
import math
BONES=[]
def bone(name,parent,pivot,rot=(0,0,0)):
    b=dict(name=name,parent=parent,pivot=list(pivot),rotation=list(rot),cubes=[]); BONES.append(b); return b
def cube(b,name,origin,size,mat,uvkey=None,rot=None,pivot=None,sc=2):
    c=dict(name=name,origin=list(origin),size=list(size),mat=mat,uvkey=uvkey or name,sc=sc)
    if rot: c['rotation']=list(rot); c['pivot']=list(pivot)
    b['cubes'].append(c); return c
bone('root',None,(0,0,0))
w=bone('waist','root',(0,12,0))
cube(w,'pelvis',(-4,12,-2),(8,3,4),'trouser')
cube(w,'belt',(-4.5,14,-2.5),(9,1,5),'belt',sc=1)
cube(w,'buckle',(-1,14,-3),(2,1,1),'buckle',sc=1)
for side,sx in (('r',-1),('l',1)):
    l=bone('leg_'+side,'waist',(sx*2,12,0))
    cube(l,'thigh_'+side,(sx*2-2,6,-2),(4,6,4),'trouser',uvkey='thigh')
    s=bone('shin_'+side,'leg_'+side,(sx*2,6,0))
    cube(s,'shin_'+side,(sx*2-2,2,-2),(4,4,4),'trouser_low',uvkey='shin')
    f=bone('foot_'+side,'shin_'+side,(sx*2,2,0))
    cube(f,'shoe_'+side,(sx*2-2,1,-3),(4,1,5),'shoe',uvkey='shoe')
    cube(f,'sole_'+side,(sx*2-2,0,-3),(4,1,5),'sole',uvkey='sole',sc=1)
t=bone('torso','waist',(0,15,0))
cube(t,'chest',(-4,15,-2),(8,9,4),'shirt')
cube(t,'neck',(-2,24,-1.5),(4,1,3),'skin',sc=2)
cube(t,'collar_l',(0.5,23.5,-2.5),(3,2,1),'collar',uvkey='collar',rot=(0,0,-18),pivot=(2,24,-2))
cube(t,'collar_r',(-3.5,23.5,-2.5),(3,2,1),'collar',uvkey='collar',rot=(0,0,18),pivot=(-2,24,-2))
cube(t,'collar_back',(-3,24,1),(6,2,1),'collar',uvkey='collar_b',sc=1)
cube(t,'pocket',(1,19,-2.5),(2,2,1),'pocket',sc=1)
hd=bone('head','torso',(0,25,0))
cube(hd,'head',(-4,25,-4),(8,8,8),'head')
cube(hd,'nose',(-1,27,-5),(2,2,1),'nose')
cube(hd,'ear_l',(4,27,-1),(1,2,2),'ear',uvkey='ear',sc=1)
cube(hd,'ear_r',(-5,27,-1),(1,2,2),'ear',uvkey='ear',sc=1)
h=bone('hair','head',(0,33,0))
cube(h,'hair_top',(-4.5,32,-4.5),(9,2,9),'hair')
cube(h,'hair_fringe',(-4.5,32,-4.5),(9,1,1),'hair_f',uvkey='fringe')
cube(h,'hair_side_l',(4,30,-3),(1,3,6),'hair_s',uvkey='hs',sc=1)
cube(h,'hair_side_r',(-5,30,-3),(1,3,6),'hair_s',uvkey='hs',sc=1)
cube(h,'hair_back',(-4.5,28,3.5),(9,5,1),'hair_b',sc=1)
cube(h,'hair_quiff',(-2.5,34,-3.5),(5,1,3),'hair',uvkey='quiff',rot=(-12,0,0),pivot=(0,34,-2))
for side,sx in (('l',1),('r',-1)):
    x0=0.5 if sx>0 else -3.5
    eb=bone('eye_'+side,'head',(sx*2.5,29.5,-4))
    cube(eb,'eye_'+side,(x0,29,-4.3),(3,1,1),'eye',uvkey='eye_'+side)
    lb=bone('lid_'+side,'head',(sx*2.5,30.5,-4))
    cube(lb,'lid_'+side,(x0-0.25,30,-4.6),(3,1,1),'lid',uvkey='lid_'+side)
    bb=bone('brow_'+side,'head',(sx*2.5,31.5,-4))
    cube(bb,'brow_'+side,(x0-0.25,31,-4.5),(3,1,1),'brow',uvkey='brow_'+side)
mb=bone('mouth','head',(0,26.5,-4))
cube(mb,'mouth',(-1.5,26,-4.4),(3,1,1),'mouth')
# arms (slim, articulated: upper arm / forearm / hand / fingers / thumb)
for side,sx in (('r',-1),('l',1)):
    a=bone('arm_'+side,'torso',(sx*5.5,23,0),rot=(0,0,-sx*(-3)))
    cube(a,'upper_'+side,(sx*5.5-1.5,18,-1.5),(3,5,3),'sleeve',uvkey='upper')
    f=bone('forearm_'+side,'arm_'+side,(sx*5.5,18,0),rot=(-4,0,0))
    cube(f,'fore_'+side,(sx*5.5-1.5,13,-1.5),(3,5,3),'forearm',uvkey='fore')
    cube(f,'watch_'+side,(sx*5.5-1.75,13.5,-1.75),(4,1,4),'watch',uvkey='watch',sc=1) if side=='l' else None
    hb=bone('hand_'+side,'forearm_'+side,(sx*5.5,13,0))
    cube(hb,'palm_'+side,(sx*5.5-1.5,11,-1.5),(3,2,3),'hand',uvkey='palm')
    fb=bone('fingers_'+side,'hand_'+side,(sx*5.5,11,-0.5))
    for i,dx in enumerate((-1.5,-0.5,0.5)):
        cube(fb,'finger%d_%s'%(i,side),(sx*5.5+dx,9,-1.5),(1,2,1),'finger',uvkey='finger',sc=1)
    tb=bone('thumb_'+side,'hand_'+side,(sx*5.5-sx*1.5,12,-1.5))
    cube(tb,'thumb_'+side,(sx*4-0.5,10.5,-2.5),(1,2,1),'finger',uvkey='finger',sc=1)
# phone (separate accessory, child of the right hand)
ph=bone('phone','hand_r',(-5.5,11,-2))
cube(ph,'phone_body',(-7,8,-3),(3,6,1),'phone')
cube(ph,'phone_cam',(-6.5,12.5,-2),(1,1,1),'cam',uvkey='cam')
cube(ph,'phone_cover',(-7.25,8,-2.75),(1,6,1),'case',uvkey='pcase') if False else None
BONE_NAMES=[b['name'] for b in BONES]
def children(n): return [b for b in BONES if b['parent']==n]
