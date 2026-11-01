import numpy as np, math
from PIL import Image
from mob_model import BONES
from mob_uv import pack, faces

POS,_=pack()
def Rmat(fx,fy,fz):
    ax,ay,az=math.radians(-fx),math.radians(-fy),math.radians(fz)
    cx,sx=math.cos(ax),math.sin(ax); cy,sy=math.cos(ay),math.sin(ay); cz,sz=math.cos(az),math.sin(az)
    Rx=np.array([[1,0,0],[0,cx,-sx],[0,sx,cx]]); Ry=np.array([[cy,0,sy],[0,1,0],[-sy,0,cy]]); Rz=np.array([[cz,-sz,0],[sz,cz,0],[0,0,1]])
    return Rz@Ry@Rx
BY={b['name']:b for b in BONES}
def bone_matrix(b,pose):
    p=pose.get(b['name'],{})
    rot=np.array(b['rotation'],float)+np.array(p.get('rot',(0,0,0)),float)
    pv=np.array(b['pivot'],float); ps=np.array(p.get('pos',(0,0,0)),float); sc=np.array(p.get('scale',(1,1,1)),float)
    M=np.eye(4); R=Rmat(*rot)@np.diag(sc)
    M[:3,:3]=R; M[:3,3]=pv+ps-R@pv
    return M
def world_matrices(pose):
    out={}
    for b in BONES:
        M=bone_matrix(b,pose)
        out[b['name']]=(out[b['parent']]@M) if b['parent'] else M
    return out
FACES={ # face: ((a,b)->local corner fn)
 'north':lambda a,b,o,s:(o[0]+s[0]*(1-a),o[1]+s[1]*(1-b),o[2]),
 'south':lambda a,b,o,s:(o[0]+s[0]*a,o[1]+s[1]*(1-b),o[2]+s[2]),
 'east': lambda a,b,o,s:(o[0]+s[0],o[1]+s[1]*(1-b),o[2]+s[2]*(1-a)),
 'west': lambda a,b,o,s:(o[0],o[1]+s[1]*(1-b),o[2]+s[2]*a),
 'up':   lambda a,b,o,s:(o[0]+s[0]*(1-a),o[1]+s[1],o[2]+s[2]*(1-b)),
 'down': lambda a,b,o,s:(o[0]+s[0]*(1-a),o[1],o[2]+s[2]*b)}
NORM={'north':(0,0,-1),'south':(0,0,1),'east':(1,0,0),'west':(-1,0,0),'up':(0,1,0),'down':(0,-1,0)}
def render(pose,tex,glow,yaw=0,pitch=12,scale=6,W=420,H=520,cy=24,bg=(38,38,46,255),hide=(),lightdir=(0.4,0.8,-0.5)):
    img=np.zeros((H,W,4),np.uint8); img[:]=bg
    zb=np.full((H,W),1e9)
    WM=world_matrices(pose)
    ay=math.radians(yaw); ap=math.radians(pitch)
    Ry=np.array([[math.cos(ay),0,math.sin(ay)],[0,1,0],[-math.sin(ay),0,math.cos(ay)]])
    Rp=np.array([[1,0,0],[0,math.cos(ap),-math.sin(ap)],[0,math.sin(ap),math.cos(ap)]])
    V=Rp@Ry
    L=np.array(lightdir); L=L/np.linalg.norm(L)
    for b in BONES:
        if b['name'] in hide: continue
        M=WM[b['name']]
        sv=pose.get(b['name'],{}).get('scale')
        for c in b['cubes']:
            o=np.array(c['origin'],float); s=np.array(c['size'],float)
            fr=faces(POS[c['uvkey']],c['size'],c.get('sc',2))
            if 'rotation' in c:
                Rc=Rmat(*c['rotation']); pv=np.array(c['pivot'],float)
            else: Rc=None
            for fn,(u,v,fw,fh) in fr.items():
                pts=[];
                for (a,bb) in ((0,0),(1,0),(1,1),(0,1)):
                    p=np.array(FACES[fn](a,bb,o,s))
                    if Rc is not None: p=Rc@(p-pv)+pv
                    pw=(M@np.append(p,1))[:3]; pts.append(pw)
                n=np.array(NORM[fn],float)
                if Rc is not None: n=Rc@n
                n=M[:3,:3]@n; n/=max(np.linalg.norm(n),1e-9)
                nv=V@n
                if nv[2]>0.02: continue   # back-facing (viewer at -z)
                shade=0.55+0.45*max(0,np.dot(n,L))
                P=[V@p for p in pts]
                sx=[W/2-q[0]*scale for q in P]; sy=[H-40-(q[1]-0)*scale*1.0+ (cy-24)*0 for q in P]; dz=[q[2] for q in P]
                uvs=[(0,0),(1,0),(1,1),(0,1)]
                for tri in ((0,1,2),(0,2,3)):
                    xs=[sx[i] for i in tri]; ys=[sy[i] for i in tri]
                    x0=int(max(0,math.floor(min(xs)))); x1=int(min(W-1,math.ceil(max(xs))))
                    y0=int(max(0,math.floor(min(ys)))); y1=int(min(H-1,math.ceil(max(ys))))
                    if x1<x0 or y1<y0: continue
                    gx,gy=np.meshgrid(np.arange(x0,x1+1)+0.5,np.arange(y0,y1+1)+0.5)
                    (ax_,ay_),(bx_,by_),(cx_,cy_)=[(xs[0],ys[0]),(xs[1],ys[1]),(xs[2],ys[2])]
                    den=(by_-cy_)*(ax_-cx_)+(cx_-bx_)*(ay_-cy_)
                    if abs(den)<1e-9: continue
                    l1=((by_-cy_)*(gx-cx_)+(cx_-bx_)*(gy-cy_))/den
                    l2=((cy_-ay_)*(gx-cx_)+(ax_-cx_)*(gy-cy_))/den
                    l3=1-l1-l2
                    m=(l1>=-0.001)&(l2>=-0.001)&(l3>=-0.001)
                    if not m.any(): continue
                    z=l1*dz[tri[0]]+l2*dz[tri[1]]+l3*dz[tri[2]]
                    ua=l1*uvs[tri[0]][0]+l2*uvs[tri[1]][0]+l3*uvs[tri[2]][0]
                    ub=l1*uvs[tri[0]][1]+l2*uvs[tri[1]][1]+l3*uvs[tri[2]][1]
                    tu=np.clip((u+ua*fw).astype(int),0,127); tv=np.clip((v+ub*fh).astype(int),0,127)
                    col=tex[tv,tu]; gl=glow[tv,tu]
                    ok=m&(col[...,3]>=128)&(z<zb[y0:y1+1,x0:x1+1])
                    if not ok.any(): continue
                    rgb=col[...,:3].astype(float)*shade
                    glm=(gl[...,3]>0)[...,None]
                    rgb=np.where(glm,np.minimum(255,col[...,:3].astype(float)*1.15+25),rgb)
                    sub=img[y0:y1+1,x0:x1+1]; zs=zb[y0:y1+1,x0:x1+1]
                    sub[ok,:3]=rgb[ok].astype(np.uint8); sub[ok,3]=255; zs[ok]=z[ok]
    return Image.fromarray(img,'RGBA')

def load():
    return np.array(Image.open('Morsi_Texture.png').convert('RGBA')),np.array(Image.open('Morsi_Glowmask.png').convert('RGBA'))
if __name__=='__main__':
    tex,gl=load()
    hide=['vines','slam_fx']+[b['name'] for b in BONES if b['name'].startswith(('vine_','spike_','slam_'))]
    ims=[render({},tex,gl,yaw=y,pitch=8,scale=7,W=330,H=420,hide=hide) for y in (0,35,90,180)]
    sheet=Image.new('RGBA',(330*4,420)); 
    for i,im in enumerate(ims): sheet.paste(im,(330*i,0))
    sheet.save('/home/claude/build/test_views.png')
