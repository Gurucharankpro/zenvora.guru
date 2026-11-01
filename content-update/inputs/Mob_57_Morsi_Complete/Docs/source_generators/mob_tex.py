import numpy as np, math
from PIL import Image
from mob_model import BONES
from mob_uv import pack, faces
RNG=np.random.default_rng(5757)
def C(h): return tuple(int(h[i:i+2],16) for i in (1,3,5))
RP={'skin':[C('#8a5a3a'),C('#a06c46'),C('#b88055'),C('#cf976a'),C('#e0ad80')],
    'skind':[C('#6e4529'),C('#855536'),C('#9c6743'),C('#b27a50')],
    'hair':[C('#050505'),C('#0e0d10'),C('#19181c'),C('#2a2a30')],
    'shirt':[C('#1b4560'),C('#265a78'),C('#316f92'),C('#4285ad'),C('#5a9bc2')],
    'trouser':[C('#141519'),C('#1d1f25'),C('#272a32'),C('#343843')],
    'shoe':[C('#8e8e92'),C('#b6b6ba'),C('#dcdce0'),C('#f4f4f6')],
    'sole':[C('#b9b19c'),C('#d3cbb6'),C('#e8e2d0')],
    'leather':[C('#2a1a10'),C('#3d281a'),C('#573a26')],
    'metal':[C('#6a7074'),C('#9aa1a5'),C('#c8ced1'),C('#eef2f3')]}
tex=np.zeros((128,128,4),np.uint8); glow=np.zeros((128,128,4),np.uint8); gref=[glow]
def put(img,x,y,col,a=255):
    h,w=img.shape[:2]
    if 0<=x<w and 0<=y<h: img[y,x]=(*col,a)
def g(img,x,y,col): put(img,x,y,col); put(gref[0],x,y,col)
def fill(ramp,h,w,light=0.0,grain=0.5,vert=0.0,horiz=0.0,streak=0.0):
    r=RP[ramp]; n=len(r); out=np.zeros((h,w,4),np.uint8)
    prev=[None]*w
    ys=np.linspace(-1,1,h)[:,None]; xs=np.linspace(-1,1,w)[None,:]
    for y in range(h):
        for x in range(w):
            v=(n-1)/2+light+(RNG.random()-0.5)*2*grain+ys[y,0]*vert+xs[0,x]*horiz
            if streak and prev[x] is not None and RNG.random()<streak: v=prev[x]+(RNG.random()-0.5)*0.6
            prev[x]=v; out[y,x]=(*r[int(np.clip(round(v),0,n-1))],255)
    return out
def hl(img,x0,x1,y,col):
    for x in range(x0,x1+1): put(img,x,y,col)
def vl(img,x,y0,y1,col):
    for y in range(y0,y1+1): put(img,x,y,col)
FL={'up':0.5,'down':-0.8,'north':0.15,'south':-0.3,'east':-0.1,'west':0.0}
def paint(c,face,w,h):
    m=c['mat']; L=FL[face]
    if m=='head': return head(face,w,h)
    if m=='skin': return fill('skin',h,w,L,0.35)
    if m=='nose':
        img=fill('skin',h,w,L+0.2,0.3)
        if face=='north':
            hl(img,0,w-1,h-1,RP['skind'][0]); put(img,0,h-2,RP['skind'][0]); put(img,w-1,h-2,RP['skind'][0]); put(img,1,0,RP['skin'][4]); put(img,1,1,RP['skin'][4])
        if face=='down': put(img,0,0,C('#1a0f0a')); put(img,w-1,0,C('#1a0f0a'))
        return img
    if m=='ear': return fill('skind',h,w,L,0.3)
    if m=='hair': return hair(face,w,h,'top')
    if m=='hair_f': 
        img=hair(face,w,h,'f')
        return img
    if m=='hair_s': return hair(face,w,h,'s')
    if m=='hair_b': return hair(face,w,h,'b')
    if m=='eye':
        img=np.zeros((h,w,4),np.uint8)
        Wt=C('#f3efe6'); W2=C('#d6cfc0'); I=C('#4a2a14'); I2=C('#6a3d1c'); P=C('#050302')
        rows=[[W2,I2,I,I,I2,W2],[Wt,I,P,P,I,Wt]]
        for y in range(h):
            for x in range(w): put(img,x,y,rows[y][x] if face=='north' else RP['skind'][1])
        if face=='north': put(img,3,1,C('#ffffff')); put(img,2,0,C('#fff6e0'))
        return img
    if m=='lid':
        img=fill('skin',h,w,L-0.2,0.25)
        if face=='north': hl(img,0,w-1,h-1,C('#1a0f0a')); put(img,0,0,RP['skind'][1]); put(img,w-1,0,RP['skind'][1])
        return img
    if m=='brow':
        img=fill('hair',h,w,L*0.3,0.5)
        if face=='north':
            for x in range(w): put(img,x,0,RP['hair'][2 if x%2 else 1])
        return img
    if m=='mouth':
        img=np.zeros((h,w,4),np.uint8)
        up=C('#80503c'); lo=C('#99604a'); dk=C('#3a1a14')
        for x in range(w): put(img,x,0,up if face!='north' else (dk if x in(0,5) else up)); put(img,x,1,lo if face!='north' else (dk if x in(0,5) else lo))
        if face=='north': put(img,2,0,C('#6a2e26')); put(img,3,0,C('#6a2e26'))
        return img
    if m=='shirt': return chest(face,w,h)
    if m=='collar':
        img=fill('shirt',h,w,L+0.5,0.25)
        if face in('north','south'): hl(img,0,w-1,h-1,RP['shirt'][0])
        return img
    if m=='pocket':
        img=fill('shirt',h,w,L+0.2,0.2)
        if face=='north': hl(img,0,w-1,0,RP['shirt'][0]); vl(img,0,0,h-1,RP['shirt'][1])
        return img
    if m=='sleeve':
        img=fill('shirt',h,w,L,0.35)
        if face in('north','south','east','west'):
            # shirt sleeve covers top 8 of 10 rows, skin below; hem seam
            for y in range(8,h):
                for x in range(w): put(img,x,y,RP['skin'][2+(x+y)%2])
            hl(img,0,w-1,7,RP['shirt'][0]); hl(img,0,w-1,6,RP['shirt'][3])
            vl(img,0,0,7,RP['shirt'][1])
        if face=='down': return fill('skin',h,w,L,0.3)
        return img
    if m=='forearm':
        img=fill('skin',h,w,L,0.35)
        if face in('north','south','east','west'):
            for y in range(0,h,4):
                for x in range(w):
                    if RNG.random()<0.12: put(img,x,y,RP['skind'][2])
        return img
    if m=='hand': 
        img=fill('skin',h,w,L+0.1,0.3)
        if face in('north','south'): hl(img,0,w-1,h-1,RP['skind'][1])
        return img
    if m=='finger':
        img=fill('skin',h,w,L,0.25)
        if face in('north','south','east','west'): put(img,0,h-1,C('#e8d6c0')) 
        return img
    if m=='watch':
        img=fill('metal',h,w,L,0.2)
        if face in('north','south','east','west'): hl(img,0,w-1,0,C('#15181a')); hl(img,0,w-1,h-1,C('#15181a'))
        if face=='up': 
            for y in range(h):
                for x in range(w): put(img,x,y,C('#101214'))
            put(img,1,1,C('#7ad0ff')); put(img,2,1,C('#7ad0ff')); put(img,1,2,C('#7ad0ff')); put(img,2,2,C('#7ad0ff'))
        return img
    if m in('trouser','trouser_low'):
        img=fill('trouser',h,w,L*0.6,0.35,streak=0.5)
        if face=='north':
            vl(img,w//2,0,h-1,RP['trouser'][3])    # front crease
            if m=='trouser_low': 
                for y in range(h-3,h): 
                    for x in range(w): put(img,x,y,RP['trouser'][0])
            else: hl(img,0,w-1,0,RP['trouser'][0])
        if face in('east','west'): vl(img,0,0,h-1,RP['trouser'][0]); vl(img,w-1,0,h-1,RP['trouser'][0])
        return img
    if m=='belt':
        img=fill('leather',h,w,L,0.3)
        if face in('north','south','east','west'):
            for x in range(0,w,3): put(img,x,0,RP['leather'][2])
        return img
    if m=='buckle': 
        img=fill('metal',h,w,0.8,0.2); 
        if face=='north': 
            for x in range(1,w-1): put(img,x,0,C('#1a1a1a'))
        return img
    if m=='shoe':
        img=fill('shoe',h,w,L,0.3)
        if face in('north','east','west','south'):
            hl(img,0,w-1,0,RP['shoe'][0])
        if face=='up':   # laces + toe cap  (h = depth 10 texels, w=8)
            for y in range(0,5): 
                for x in range(w): put(img,x,y,RP['shoe'][1])
            for y in range(1,6,2): hl(img,1,w-2,y,C('#ffffff'))
            vl(img,w//2,0,5,RP['shoe'][0])
            hl(img,0,w-1,h-1,RP['shoe'][0]); hl(img,0,w-1,h-2,C('#6e6e73'))
        return img
    if m=='sole':
        img=fill('sole',h,w,L,0.2)
        if face=='down': 
            for y in range(0,h,2):
                hl(img,0,w-1,y,C('#8a8272'))
        return img
    if m=='phone': return phone(face,w,h)
    if m=='cam':
        img=np.zeros((h,w,4),np.uint8)
        for y in range(h):
            for x in range(w): put(img,x,y,C('#101114'))
        put(img,0,0,C('#4a5a7a')); return img
    return fill('skin',h,w,L,0.3)
def hair(face,w,h,kind):
    img=fill('hair',h,w,0.2 if face=='up' else -0.2,0.5,streak=0.6)
    if kind=='top' and face=='up':
        for y in range(0,h,3): hl(img,0,w-1,y,RP['hair'][3]) if RNG.random()<0.5 else None
    if kind=='f' and face in('north',):
        for x in range(w):
            if RNG.random()<0.3: put(img,x,h-1,RP['hair'][0])
    return img
def head(face,w,h):
    sk=RP['skin']
    if face=='north':   # 16x16 face
        img=fill('skin',h,w,0.2,0.3,vert=-0.3,horiz=0.0)
        for y in range(h):
            for x in range(w):
                d=abs(x-7.5)/7.5
                if d>0.85: put(img,x,y,RP['skind'][2 if y<8 else 1])
        for y in range(0,4):                  # hair rows (hidden under hair cubes, painted for consistency)
            for x in range(w): put(img,x,y,RP['hair'][1+(x+y)%2])
        # under-eye shadows (tired / nervous)
        for x in list(range(1,7))+list(range(9,15)): put(img,x,8,RP['skind'][2]); 
        # cheek warmth
        for (x,y) in ((2,10),(3,10),(12,10),(13,10),(2,9),(13,9)): put(img,x,y,sk[3])
        # nasolabial / cheek lines
        for (x,y) in ((4,11),(5,12),(11,11),(10,12)): put(img,x,y,RP['skind'][1])
        # stubble (jaw + upper lip), sparse
        for y in range(11,16):
            for x in range(2,14):
                if RNG.random()<(0.28 if y>=13 else 0.16) and abs(x-7.5)>0.5: put(img,x,y,RP['skind'][0])
        for x in range(5,11): put(img,x,11,RP['skind'][1]) if RNG.random()<0.5 else None
        hl(img,3,12,15,RP['skind'][1])
        # chin
        put(img,7,14,sk[4]); put(img,8,14,sk[4])
        return img
    if face=='south':
        img=fill('hair',h,w,-0.2,0.5,streak=0.5)
        for y in range(10,h):
            for x in range(w): put(img,x,y,sk[1+(x+y)%2])
        return img
    if face in('east','west'):
        img=fill('skin',h,w,0.0,0.3)
        for y in range(0,5):
            for x in range(w): put(img,x,y,RP['hair'][1+(x+y)%2])
        for y in range(5,9):
            for x in range(0,3): put(img,x if face=='east' else w-1-x,y,RP['hair'][1])    # sideburn
        for x in range(w): put(img,x,h-1,RP['skind'][1])
        return img
    if face=='up': return fill('hair',h,w,0,0.5)
    return fill('skin',h,w,-0.6,0.3)
def chest(face,w,h):   # 16 x 18
    L=FL[face]
    img=fill('shirt',h,w,L*0.5+0.1,0.3,vert=0.05,streak=0.35)
    for x in range(0,w,4): vl(img,x,0,h-1,RP['shirt'][3]) if face in('north','south') else None   # fine pinstripes
    if face=='north':
        vl(img,7,2,h-1,RP['shirt'][0]); vl(img,8,2,h-1,RP['shirt'][1])
        for y in (4,8,12,16): put(img,8,y,C('#f1efe8')); put(img,7,y,C('#cfcabb'))
        # open collar V skin
        for i in range(4):
            for x in range(7-i,9+i): put(img,x,i-1,RP['skin'][2]) if i>0 else None
        # side seams + underarm shade
        vl(img,0,0,h-1,RP['shirt'][0]); vl(img,w-1,0,h-1,RP['shirt'][0])
        for y in range(12,h): 
            for x in range(1,w-1): 
                if (x*5+y*3)%11==0: put(img,x,y,RP['shirt'][1])    # tension wrinkles near belt
        hl(img,0,w-1,h-1,RP['shirt'][0])
    if face=='south':
        hl(img,0,w-1,4,RP['shirt'][0]); vl(img,7,5,h-1,RP['shirt'][1]); vl(img,8,5,h-1,RP['shirt'][1])
        hl(img,0,w-1,h-1,RP['shirt'][0])
    if face in('east','west'): hl(img,0,w-1,h-1,RP['shirt'][0]); vl(img,0,0,h-1,RP['shirt'][1])
    return img
def phone(face,w,h):
    if face=='north':   # screen 6 x 12 texels : lit
        img=np.zeros((h,w,4),np.uint8)
        for y in range(h):
            for x in range(w): put(img,x,y,C('#0a0b0e'))
        for y in range(1,h-1):
            for x in range(1,w-1):
                v=y/h; put(img,x,y,(int(60+60*v),int(140+60*(1-v)),int(220-40*v)))
        for (x,y,c) in ((1,2,C('#ff6a5a')),(3,2,C('#ffd35a')),(1,5,C('#6aff8a')),(3,5,C('#c08aff')),(1,8,C('#5affd8')),(3,8,C('#ffa05a'))):
            put(img,x,y,c); put(img,x+1,y,c)
        for y in range(1,h-1):
            for x in range(1,w-1): put(gref[0],x,y,img[y,x][:3])
        return img
    if face=='south':
        img=fill('trouser',h,w,-0.5,0.2)
        put(img,1,1,C('#2a2d36')); put(img,w//2,h//2,C('#9aa1a5')); put(img,w//2+1,h//2,C('#9aa1a5'))
        return img
    return fill('metal',h,w,-0.8,0.15)
def build():
    pos,used=pack(); done=set()
    for b in BONES:
        for c in b['cubes']:
            k=c['uvkey']
            if k in done: continue
            done.add(k)
            for fn,(u,v,w,h) in faces(pos[k],c['size'],c.get('sc',2)).items():
                gi=np.zeros((h,w,4),np.uint8); gref[0]=gi
                tex[v:v+h,u:u+w]=paint(c,fn,w,h); glow[v:v+h,u:u+w]=gi
    return pos
if __name__=='__main__':
    build(); Image.fromarray(tex,'RGBA').save('Morsi_Texture.png'); Image.fromarray(glow,'RGBA').save('Morsi_Glowmask.png'); print('ok')
