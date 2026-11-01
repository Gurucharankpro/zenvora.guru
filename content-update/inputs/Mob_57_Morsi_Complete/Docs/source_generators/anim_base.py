import json, math
class Anim:
    def __init__(s,name,length,loop=False,hold=False):
        s.name,s.length,s.loop,s.hold=name,length,loop,hold; s.d={}
    def k(s,bone,ch,t,vec,ease=None,lerp=None):
        s.d.setdefault(bone,{}).setdefault(ch,[]).append((round(t,4),[round(v,3) for v in vec],ease,lerp))
    def poses(s,plist):
        keys=set()
        for t,p in plist:
            for kk in p: keys.add(kk)
        for kk in keys:
            if kk.endswith('.p'): b,ch,df=kk[:-2],'position',(0,0,0)
            elif kk.endswith('.s'): b,ch,df=kk[:-2],'scale',(1,1,1)
            else: b,ch,df=kk,'rotation',(0,0,0)
            for t,p in plist:
                v=p.get(kk,df); ease=None; lerp=None
                if isinstance(v,dict): ease=v.get('ease'); lerp=v.get('lerp'); v=v['v']
                s.k(b,ch,t,v,ease,lerp)
    def sine(s,bone,ch,amp,period=None,phase=0.0,n=8,base=(0,0,0),cm=True):
        period=period or s.length
        for i in range(n+1):
            t=s.length*i/n; ph=2*math.pi*(t/period)+phase
            v=[base[j]+amp[j]*math.sin(ph) for j in range(3)]
            s.k(bone,ch,t,v,None,'catmullrom' if cm else None)
    def json(s):
        bones={}
        for b,chs in s.d.items():
            bones[b]={}
            for ch,lst in chs.items():
                lst=sorted(lst,key=lambda e:e[0]); out={}
                for t,v,ease,lerp in lst:
                    if ease or lerp:
                        o={'vector':v}
                        if lerp: o['lerp_mode']=lerp
                        if ease: o['easing']=ease
                        out['%g'%t]=o
                    else: out['%g'%t]=v
                bones[b][ch]=out
        o={'animation_length':s.length,'bones':bones}
        if s.loop: o['loop']=True
        elif s.hold: o['loop']='hold_on_last_frame'
        return o


def E(v,ease): return {'v':v,'ease':ease}
