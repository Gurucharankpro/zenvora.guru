import json, math
from anim_base import Anim
import mob_model as MM
ANIMS=[]
def A(name,length,**kw):
    a=Anim(name,length,**kw); ANIMS.append(a); return a
S=math.sin; PI=math.pi
def bump(t,t0,w): return math.exp(-((t-t0)/w)**2)
def cm(d): return {k:(v if isinstance(v,dict) else {'v':v,'lerp':'catmullrom'}) for k,v in d.items()}
def sample(a,fn,n,smooth=True):
    pl=[]
    for i in range(n+1):
        t=a.length*i/n; d=fn(t); pl.append((t,cm(d) if smooth else d))
    a.poses(pl)
def merge(*ds):
    o={}
    for d in ds: o.update(d)
    return o
def blink(t,times,w=0.07): return -1.0*max(bump(t,x,w) for x in times)
N0={}
def face(t,blinks=(),brow=(0,0),lids=0.0,mouth=1.0,mopen=0.0):
    """brow=(rz_l, rz_r) additive degrees; lids: extra downward px (droop); mouth: x-scale; mopen: y-scale add"""
    b=blink(t,blinks) if blinks else 0
    return {'lid_l':(0,0,0),'lid_r':(0,0,0),'lid_l.p':(0,min(0,lids+b),0),'lid_r.p':(0,min(0,lids+b),0),
            'brow_l':(0,0,brow[0]),'brow_r':(0,0,brow[1]),'mouth.s':(mouth,1+mopen,1)}
# idle poses reference: arms hang slightly, weight shift
# ============================================================ 1 idle  (looks around, adjusts shirt)
a=A('idle',4.4,loop=True)
def f(t):
    ph=2*PI*t/4.4
    look=22*bump(t,1.0,0.35)-20*bump(t,2.0,0.3)    # glances left then right
    tug=bump(t,3.35,0.28)+0.0
    d={'torso.s':(1+0.01*S(ph*2),1+0.018*S(ph*2-0.5),1+0.01*S(ph*2)),'torso':(0.8*S(ph*2),0.0+look*0.25,0.6*S(ph)),'waist':(0,0,1.2*S(ph)),
       'head':(2*S(ph*2+1)-3*bump(t,2.9,0.3),look,1.5*S(ph)),'root.p':(0,0.12*S(ph*2),0),
       'eye_l.p':(0.45*(look>2)-0.45*(look<-2),0,0),'eye_r.p':(0.45*(look>2)-0.45*(look<-2),0,0),
       'arm_r':(3*S(ph*2+1),0,-1),'arm_l':(-3*S(ph*2),0,1),
       'leg_l':(0,0,-1*S(ph)),'leg_r':(0,0,1*S(ph))}
    # shirt adjustment: left hand grabs hem and tugs twice
    up=bump(t,3.2,0.45)
    d['arm_l']=(-28*up,0,6*up); d['forearm_l']=(-62*up-8*up*S(t*14),0,0); d['hand_l']=(0,0,0); d['fingers_l']=(0,0,-55*up); d['thumb_l']=(0,0,-25*up)
    d['torso']=(d['torso'][0]+3*up,d['torso'][1],d['torso'][2])
    d.update(face(t,blinks=(0.5,2.6,3.9),brow=(3*S(ph*2),-3*S(ph*2+1)),lids=0.0,mouth=1.0))
    d['brow_l']=(0,0,-4*bump(t,1.0,0.3)+2); d['brow_r']=(0,0,4*bump(t,1.0,0.3)-2)
    return d
sample(a,f,44)
# ============================================================ 2 walk
a=A('walk',1.1,loop=True)
def f(t):
    ph=2*PI*t/1.1
    d={'leg_l':(-30*S(ph),0,0),'leg_r':(30*S(ph),0,0),'shin_l':(max(0,38*S(ph+0.6)),0,0),'shin_r':(max(0,-38*S(ph+0.6)),0,0),
       'foot_l':(10*S(ph+1.2),0,0),'foot_r':(-10*S(ph+1.2),0,0),
       'arm_l':(26*S(ph),0,2),'arm_r':(-22*S(ph),0,-2+1*S(ph*2)),'forearm_l':(-18-10*S(ph+0.5),0,0),'forearm_r':(-14+10*S(ph+0.5),0,0),
       'waist':(0,-6*S(ph),2*S(ph*2)),'torso':(2,6*S(ph),-2*S(ph)),'root.p':(0,0.5*math.cos(2*ph)-0.2,0),'head':(-2+1.5*S(ph*2),-5*S(ph),2*S(ph)),
       'fingers_l':(0,0,-18),'fingers_r':(0,0,18),'torso.s':(1,1+0.01*S(ph*2),1)}
    d.update(face(t,blinks=(0.4,),brow=(1,-1)))
    return d
sample(a,f,16)
# ============================================================ 3 run (nervous sprint)
a=A('run',0.6,loop=True)
def f(t):
    ph=2*PI*t/0.6
    d={'leg_l':(-58*S(ph),0,0),'leg_r':(58*S(ph),0,0),'shin_l':(max(0,85*S(ph+0.7)),0,0),'shin_r':(max(0,-85*S(ph+0.7)),0,0),'foot_l':(-14*S(ph),0,0),'foot_r':(14*S(ph),0,0),
       'arm_l':(52*S(ph),0,4),'arm_r':(-52*S(ph),0,-4),'forearm_l':(-78,0,0),'forearm_r':(-78,0,0),'fingers_l':(0,0,-70),'fingers_r':(0,0,70),'thumb_l':(0,0,-30),'thumb_r':(0,0,30),
       'torso':(14,-8*S(ph),0),'waist':(4,7*S(ph),2*S(ph)),'head':(-12,3*S(ph*0.5)+8*S(ph+2),3*S(ph)),'root.p':(0,0.9*abs(S(ph))-0.4,-1.2)}
    d.update(face(t,blinks=(),brow=(-7,7),lids=0.35,mouth=0.8,mopen=0.5))
    return d
sample(a,f,12)
# ============================================================ 4 phone_call
a=A('phone_call',4.0)
PH=lambda e: {'phone.s':(1,1,1)}
UP=merge({'arm_r':(-40,0,-18),'forearm_r':(-128,0,22),'hand_r':(0,0,0),'fingers_r':(0,0,62),'thumb_r':(0,0,20),'phone.s':(1,1,1),'head':(0,8,-9),'torso':(0,4,0),
          'arm_l':(-10,0,4),'forearm_l':(-30,0,0),'fingers_l':(0,0,-20)})
def talk(t0,mo): return {'mouth.s':(1,1+mo,1)}
pl=[(0,merge({'phone.s':(0.01,0.01,0.01)})),(0.25,merge({'arm_r':(-8,0,-6),'forearm_r':(-30,0,0),'phone.s':(1,1,1),'fingers_r':(0,0,40)})),
    (0.7,merge(UP,{'arm_r':(-30,0,-14),'forearm_r':(-100,0,12),'head':(2,4,-4)})),(1.0,UP)]
for i,t in enumerate((1.25,1.55,1.85,2.15,2.45,2.75)):
    pl.append((t,merge(UP,{'head':(1+i%2*2,10-3*(i%2),-9-(i%2)),'mouth.s':(1-0.1*(i%2),1+0.9*((i+1)%2),1),'forearm_r':(-128-2*(i%2),0,22),'torso':(0,4,0),
        'arm_l':(-10-12*(i%2),0,4+6*(i%3==0)),'forearm_l':(-30-25*(i%2),0,0),'brow_l':(0,0,-4*(i%2)),'brow_r':(0,0,4*(i%2))})))
pl+= [(3.2,merge(UP,{'head':(1,6,-6),'mouth.s':(1,1,1)})),(3.55,merge({'arm_r':(-20,0,-10),'forearm_r':(-60,0,0),'phone.s':(1,1,1),'fingers_r':(0,0,40),'head':(0,0,0)})),(4.0,merge({'phone.s':(0.01,0.01,0.01)}))]
a.poses(pl)
for t0 in (0.9,2.3,3.4): a.k('lid_l','position',t0-0.05,(0,0,0)); a.k('lid_l','position',t0,(0,-1,0)); a.k('lid_l','position',t0+0.07,(0,0,0)); a.k('lid_r','position',t0-0.05,(0,0,0)); a.k('lid_r','position',t0,(0,-1,0)); a.k('lid_r','position',t0+0.07,(0,0,0))
# ============================================================ 5 argument
a=A('argument',3.6)
def AR(i):
    s=1 if i%2==0 else -1
    return {'arm_r':(-62+10*s,0,-30+6*s),'forearm_r':(-70-25*s,0,0),'hand_r':(0,0,0),'fingers_r':(0,0,10),'thumb_r':(0,0,0),
            'arm_l':(-48-10*s,0,28-6*s),'forearm_l':(-60+20*s,0,0),'fingers_l':(0,0,-10),
            'torso':(6+3*s,10*s,0),'waist':(0,-4*s,1),'head':(-4+3*s,-12*s,-4*s),'brow_l':(0,0,-14),'brow_r':(0,0,14),'mouth.s':(1.15,1.8 if s>0 else 1.2,1),
            'leg_l':(-4*s,0,0),'leg_r':(4*s,0,0),'root.p':(0,0.2*s,0)}
pl=[(0,N0),(0.35,merge(AR(0),{'arm_r':(-30,0,-14),'forearm_r':(-40,0,0),'arm_l':(-20,0,10),'forearm_l':(-30,0,0),'brow_l':(0,0,-8),'brow_r':(0,0,8)}))]
for i in range(6): pl.append((0.7+0.45*i,AR(i)))
pl.append((3.4-0.2,merge(AR(5),{'arm_r':(-70,0,-8),'forearm_r':(-10,0,0),'fingers_r':(0,0,0),'head':(0,0,0),'brow_l':(0,0,-18),'brow_r':(0,0,18),'torso':(10,0,0)})))
pl.append((3.6,N0)); a.poses(pl)
# ============================================================ 6 punch_attack  (prep -> strike -> follow-through -> recovery)
a=A('punch_attack',1.5)
STANCE={'torso':(6,-16,0),'waist':(0,-10,0),'leg_l':(-14,0,0),'leg_r':(14,0,0),'shin_l':(6,0,0),'shin_r':(10,0,0),'arm_l':(-70,0,14),'forearm_l':(-95,0,0),'fingers_l':(0,0,-80),'thumb_l':(0,0,-30),
        'fingers_r':(0,0,80),'thumb_r':(0,0,30),'head':(6,12,0),'root.p':(0,-0.8,0.5),'brow_l':(0,0,-10),'brow_r':(0,0,10),'mouth.s':(0.85,0.8,1)}
a.poses([(0,N0),(0.3,merge(STANCE,{'arm_r':(-20,0,-12),'forearm_r':(-110,0,0)})),
 (0.5,merge(STANCE,{'torso':(10,-30,0),'waist':(0,-18,0),'arm_r':(24,0,-10),'forearm_r':(-100,0,0),'leg_l':(-8,0,0),'leg_r':(18,0,0),'root.p':(0,-1.3,1.2),'head':(8,20,0)})),
 (0.64,merge(STANCE,{'torso':(14,26,0),'waist':(0,16,0),'arm_r':(-92,0,-2),'forearm_r':(-4,0,0),'arm_l':(-40,0,16),'forearm_l':(-105,0,0),'leg_l':(-46,0,0),'shin_l':(24,0,0),'leg_r':(16,0,0),'root.p':(0,-1.6,-2.5),'head':(8,-10,0),'mouth.s':(1.2,1.6,1)})),
 (0.8,merge(STANCE,{'torso':(14,28,0),'waist':(0,18,0),'arm_r':(-90,0,-2),'forearm_r':(-2,0,0),'arm_l':(-40,0,16),'forearm_l':(-105,0,0),'leg_l':(-46,0,0),'shin_l':(24,0,0),'leg_r':(16,0,0),'root.p':(0,-1.6,-2.6),'head':(8,-12,0)})),
 (1.1,merge(STANCE,{'torso':(10,8,0),'waist':(0,6,0),'arm_r':(-60,0,-8),'forearm_r':(-50,0,0),'leg_l':(-26,0,0),'root.p':(0,-1,-1)})),(1.5,N0)])
for t0 in (1.25,): pass
# ============================================================ 7 defensive_counter
a=A('defensive_counter',1.9)
GUARD={'arm_l':(-75,0,12),'forearm_l':(-100,0,0),'arm_r':(-72,0,-12),'forearm_r':(-100,0,0),'fingers_l':(0,0,-75),'fingers_r':(0,0,75),'thumb_l':(0,0,-30),'thumb_r':(0,0,30),'brow_l':(0,0,-10),'brow_r':(0,0,10)}
a.poses([(0,N0),(0.25,merge(GUARD,{'torso':(4,-6,0),'head':(4,0,0),'leg_l':(-8,0,0),'leg_r':(8,0,0),'root.p':(0,-0.6,0)})),
 (0.5,merge(GUARD,{'torso':(-16,10,10),'waist':(0,6,6),'head':(-6,-14,-8),'root.p':(3.5,-0.4,1.5),'leg_l':(-10,0,-10),'leg_r':(18,0,14),'shin_r':(18,0,0),'arm_l':(-95,0,26),'forearm_l':(-70,0,0),'mouth.s':(1,1.5,1),'lid_l.p':(0,0,0)})),
 (0.75,merge(GUARD,{'torso':(-20,16,12),'waist':(0,8,6),'head':(-8,-20,-8),'root.p':(5,-0.8,2),'leg_l':(-20,0,-12),'leg_r':(24,0,16),'shin_r':(22,0,0),'arm_l':(-100,0,32),'forearm_l':(-50,0,0),'arm_r':(-40,0,-10)})),
 (0.95,merge(GUARD,{'torso':(12,-24,0),'waist':(0,-12,0),'head':(6,12,0),'root.p':(2,-1.4,-0.5),'leg_l':(-18,0,0),'leg_r':(20,0,0),'arm_r':(10,0,-8),'forearm_r':(-100,0,0),'arm_l':(-60,0,18),'forearm_l':(-90,0,0)})),
 (1.12,merge(GUARD,{'torso':(14,28,0),'waist':(0,18,0),'head':(8,-10,0),'root.p':(1,-1.6,-2.4),'leg_l':(-40,0,0),'shin_l':(20,0,0),'leg_r':(14,0,0),'arm_r':(-92,0,-2),'forearm_r':(-4,0,0),'arm_l':(-40,0,16),'forearm_l':(-105,0,0),'mouth.s':(1.2,1.6,1)})),
 (1.35,merge(GUARD,{'torso':(14,28,0),'waist':(0,18,0),'head':(8,-12,0),'root.p':(0.5,-1.6,-2.4),'leg_l':(-40,0,0),'shin_l':(20,0,0),'leg_r':(14,0,0),'arm_r':(-90,0,-2),'forearm_r':(-2,0,0),'arm_l':(-40,0,16),'forearm_l':(-105,0,0)})),
 (1.9,N0)])
# ============================================================ 8 nervous_reaction
a=A('nervous_reaction',2.6)
def NV(k):
    s=1 if k%2==0 else -1
    return {'arm_l':(-58+6*s,0,24+3*s),'forearm_l':(-92-10*s,0,0),'arm_r':(-58-6*s,0,-24-3*s),'forearm_r':(-92+10*s,0,0),'fingers_l':(0,0,-16),'fingers_r':(0,0,16),'thumb_l':(0,0,-6),'thumb_r':(0,0,6),
            'torso':(-8,5*s,2*s),'head':(-3+2*s,-9*s,3*s),'brow_l':(0,0,-18),'brow_r':(0,0,18),'mouth.s':(0.8,1.2,1),'lid_l.p':(0,0.35,0),'lid_r.p':(0,0.35,0)}
a.poses([(0,N0),(0.2,merge(NV(0),{'torso':(-12,0,0),'root.p':(0,0.4,0.6)})),
 (0.45,merge(NV(1),{'leg_l':(16,0,0),'shin_l':(30,0,0),'leg_r':(-12,0,0),'root.p':(0,0.2,1.8)})),
 (0.7,merge(NV(0),{'leg_l':(-4,0,0),'leg_r':(0,0,0),'root.p':(0,-0.2,3.2)})),
 (0.95,merge(NV(1),{'leg_r':(16,0,0),'shin_r':(30,0,0),'leg_l':(-12,0,0),'root.p':(0,0.2,4.4)})),
 (1.2,merge(NV(0),{'root.p':(0,-0.2,5.6)}))]+[(1.35+0.2*i,merge(NV(i),{'root.p':(0,0,5.6),'mouth.s':(0.8,1.2+0.5*(i%2),1)})) for i in range(5)]+[(2.6,merge(NV(1),{'torso':(-2,0,0),'arm_l':(-20,0,10),'forearm_l':(-30,0,0),'arm_r':(-20,0,-10),'forearm_r':(-30,0,0),'root.p':(0,0,5.6)}))])
for t0 in (1.55,2.1): 
    for lid in ('lid_l','lid_r'): a.k(lid,'position',t0-0.04,(0,0.35,0)); a.k(lid,'position',t0,(0,-1,0)); a.k(lid,'position',t0+0.06,(0,0.35,0))
# ============================================================ 9 summoned_arrival
a=A('summoned_arrival',3.8)
PHU={'arm_r':(-58,0,-10),'forearm_r':(-82,0,0),'fingers_r':(0,0,60),'thumb_r':(0,0,25),'phone.s':(1,1,1)}
a.poses([(0,merge({'root.s':(0.02,0.02,0.02),'phone.s':(0.01,0.01,0.01)})),(0.2,merge({'root.s':(1.12,0.7,1.12),'phone.s':(0.01,0.01,0.01),'root.p':(0,0,0)})),(0.4,merge({'root.s':(0.96,1.06,0.96),'phone.s':(0.01,0.01,0.01),'torso':(4,0,0),'head':(4,0,0)})),(0.55,merge({'root.s':(1,1,1),'phone.s':(0.01,0.01,0.01),'head':(0,0,0)})),
 (0.9,merge({'phone.s':(0.01,0.01,0.01),'arm_r':(-4,0,-8),'forearm_r':(-30,0,0),'fingers_r':(0,0,30),'head':(4,-16,0),'brow_l':(0,0,-6),'brow_r':(0,0,6)})),   # fumbles at the pocket
 (1.2,merge({'phone.s':(1,1,1),'arm_r':(-30,0,-14),'forearm_r':(-60,0,0),'fingers_r':(0,0,60),'head':(8,0,0)})),
 (1.6,merge(PHU,{'head':(22,0,0),'torso':(6,0,0),'brow_l':(0,0,-8),'brow_r':(0,0,8)})),
 (1.85,merge(PHU,{'head':(22,0,0),'torso':(6,0,0),'thumb_r':(0,0,10)})),(2.05,merge(PHU,{'head':(22,0,0),'torso':(6,0,0),'thumb_r':(0,0,32)})),(2.25,merge(PHU,{'head':(20,4,0),'torso':(6,0,0),'thumb_r':(0,0,12)})),
 (2.6,merge(PHU,{'arm_r':(-30,0,-10),'forearm_r':(-70,0,0),'head':(0,-28,0),'torso':(0,-10,0),'brow_l':(0,0,-14),'brow_r':(0,0,14)})),
 (3.05,merge(PHU,{'arm_r':(-30,0,-10),'forearm_r':(-70,0,0),'head':(-2,30,0),'torso':(0,10,0),'brow_l':(0,0,-14),'brow_r':(0,0,14)})),
 (3.5,merge({'phone.s':(1,1,1),'arm_r':(-14,0,-8),'forearm_r':(-40,0,0),'fingers_r':(0,0,40),'head':(0,0,0)})),(3.8,merge({'phone.s':(0.01,0.01,0.01)}))])
# ============================================================ 10 death
a=A('death',3.4,hold=True)
a.poses([(0,N0),(0.3,merge({'torso':(-14,0,0),'head':(-14,0,0),'arm_r':(-30,0,-24),'forearm_r':(-40,0,0),'arm_l':(-30,0,24),'forearm_l':(-40,0,0),'brow_l':(0,0,-18),'brow_r':(0,0,18),'mouth.s':(0.9,2.2,1),'root.p':(0,0,1)})),
 (0.7,merge({'torso':(-22,0,6),'head':(-20,0,8),'arm_r':(-70,0,-10),'forearm_r':(-110,0,0),'hand_r':(0,0,0),'fingers_r':(0,0,60),'arm_l':(10,0,30),'leg_l':(-12,0,0),'shin_l':(22,0,0),'leg_r':(10,0,0),'shin_r':(16,0,0),'root.p':(0,-1.5,2.5),'mouth.s':(0.9,2.0,1)})),
 (1.15,merge({'torso':(10,0,8),'head':(12,0,10),'arm_r':(-60,0,-10),'forearm_r':(-100,0,0),'arm_l':(14,0,28),'leg_l':(-55,0,0),'shin_l':(80,0,0),'leg_r':(-30,0,0),'shin_r':(100,0,0),'root.p':(0,-6.5,1.5),'lid_l.p':(0,-0.5,0),'lid_r.p':(0,-0.5,0)})),
 (1.55,merge({'torso':(14,0,10),'head':(18,0,12),'arm_r':(10,0,-20),'forearm_r':(-20,0,0),'arm_l':(14,0,22),'leg_l':(-80,0,0),'shin_l':(110,0,0),'leg_r':(-70,0,0),'shin_r':(110,0,0),'root.p':(0,-9,1),'lid_l.p':(0,-1,0),'lid_r.p':(0,-1,0),'mouth.s':(1,1.2,1)})),
 (2.3,merge({'root':(-70,0,0),'torso':(0,0,0),'head':(10,0,10),'arm_r':(8,0,-22),'forearm_r':(-10,0,0),'arm_l':(10,0,24),'forearm_l':(-12,0,0),'leg_l':(-10,0,-6),'shin_l':(14,0,0),'leg_r':(0,0,6),'shin_r':(10,0,0),'lid_l.p':(0,-1,0),'lid_r.p':(0,-1,0),'brow_l':(0,0,-4),'brow_r':(0,0,4),'mouth.s':(1,1,1)})),
 (2.8,merge({'root':(-86,0,0),'head':(10,0,10),'arm_r':(6,0,-26),'forearm_r':(-8,0,0),'arm_l':(8,0,28),'leg_l':(-4,0,-8),'leg_r':(0,0,8),'lid_l.p':(0,-1,0),'lid_r.p':(0,-1,0)})),
 (3.4,merge({'root':(-88,0,0),'head':(8,0,12),'arm_r':(6,0,-26),'forearm_r':(-8,0,0),'arm_l':(8,0,28),'leg_l':(-4,0,-8),'leg_r':(0,0,8),'lid_l.p':(0,-1,0),'lid_r.p':(0,-1,0)}))])
# ---------------------------------------------------------------- post
for a in ANIMS:
    if 'phone' not in a.d and 'phone.s' not in a.d and not any(k=='phone' for k in a.d): pass
    if a.name not in('phone_call','summoned_arrival') and 'phone' not in a.d: a.k('phone','scale',0.0,(0.01,0.01,0.01))
    for b,chs in a.d.items():
        for ch,lst in chs.items():
            seen={}
            for e in lst: seen[round(e[0],4)]=e
            chs[ch]=list(seen.values())
def evaluate(a,t):
    pose={}
    for b,chs in a.d.items():
        for ch,lst in chs.items():
            lst=sorted(lst,key=lambda e:e[0])
            if t<=lst[0][0]: v=lst[0][1]
            elif t>=lst[-1][0]: v=lst[-1][1]
            else:
                for i in range(len(lst)-1):
                    if lst[i][0]<=t<=lst[i+1][0]:
                        f=(t-lst[i][0])/(lst[i+1][0]-lst[i][0]) if lst[i+1][0]>lst[i][0] else 0; v=[x+(y-x)*f for x,y in zip(lst[i][1],lst[i+1][1])]; break
            pose.setdefault(b,{})[{'rotation':'rot','position':'pos','scale':'scale'}[ch]]=v
    return pose
import mob_render as _R, numpy as _np
def _miny(pose):
    WM=_R.world_matrices(pose); m=1e9
    for b in _R.BONES:
        if b['name']=='phone': continue
        for c in b['cubes']:
            o=_np.array(c['origin'],float); sz=_np.array(c['size'],float)
            for dx in(0,1):
                for dy in(0,1):
                    for dz in(0,1):
                        p=o+sz*_np.array([dx,dy,dz])
                        if 'rotation' in c: p=_R.Rmat(*c['rotation'])@(p-_np.array(c['pivot']))+_np.array(c['pivot'])
                        m=min(m,(WM[b['name']]@_np.append(p,1))[1])
    return m
def ground_fix(a,times):
    a.d.setdefault('root',{}); keys=a.d['root'].setdefault('position',[])
    for t in times:
        base=[e for e in keys if abs(e[0]-t)<1e-6]; keys[:]=[e for e in keys if abs(e[0]-t)>=1e-6]
        keys.append((t,[0,0,0],None,None)); m=_miny(evaluate(a,t)); keys[:]=[e for e in keys if abs(e[0]-t)>=1e-6]
        v=base[0][1] if base else [0,0,0]; keys.append((t,[v[0],round(v[1]-m,3),v[2]],None,None))
_A={x.name:x for x in ANIMS}
ground_fix(_A['death'],[1.15,1.55,2.3,2.8,3.4])
def dump(path='Morsi.animation.json'):
    out={'format_version':'1.8.0','animations':{'animation.morsi.'+a.name:a.json() for a in ANIMS}}
    json.dump(out,open(path,'w'),indent=2); return out
if __name__=='__main__':
    o=dump(); print([(n.split('.')[-1],sum(len(c) for b in v['bones'].values() for c in b.values())) for n,v in o['animations'].items()])
