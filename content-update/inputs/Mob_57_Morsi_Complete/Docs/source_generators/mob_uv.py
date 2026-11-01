import math
from mob_model import BONES
def box_dims(size): return [int(math.ceil(v)) for v in size]
def pack(W=128,H=128):
    keys={}
    for b in BONES:
        for c in b['cubes']:
            k=c['uvkey']; sz=tuple(c['size']); sc=c.get('sc',2)
            if k in keys: assert keys[k]['size']==sz and keys[k]['sc']==sc,(k)
            else: keys[k]=dict(size=sz,sc=sc)
    items=[]
    for k,v in keys.items():
        w,h,d=box_dims(v['size']); items.append((k,(2*d+2*w)*v['sc'],(d+h)*v['sc']))
    items.sort(key=lambda t:(-t[2],-t[1]))
    x=y=0; rowh=0; pos={}
    for k,rw,rh in items:
        if x+rw>W: x=0; y+=rowh; rowh=0
        pos[k]=(x,y); x+=rw; rowh=max(rowh,rh)
        if y+rh>H: raise Exception('texture overflow at %s (%d,%d)'%(k,x,y))
    return pos,y+rowh
def faces(uv,size,sc=2):
    u,v=uv; w,h,d=box_dims(size)
    return {k:(a*sc if False else a,b,c,e) for k,(a,b,c,e) in dict(
        up=(u+d*sc,v,w*sc,d*sc),down=(u+(d+w)*sc,v,w*sc,d*sc),east=(u,v+d*sc,d*sc,h*sc),north=(u+d*sc,v+d*sc,w*sc,h*sc),
        west=(u+(d+w)*sc,v+d*sc,d*sc,h*sc),south=(u+(2*d+w)*sc,v+d*sc,w*sc,h*sc)).items()}
if __name__=='__main__':
    pos,used=pack(); print(len(pos),'uv boxes; rows used',used)
