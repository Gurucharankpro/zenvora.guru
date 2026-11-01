import gzip, struct, json, io
from PIL import Image
errs=[];notes=[]
def rd(f):
    def rs(): n=struct.unpack('>H',f.read(2))[0]; return f.read(n).decode()
    def pl(t):
        if t==1: return struct.unpack('>b',f.read(1))[0]
        if t==3: return struct.unpack('>i',f.read(4))[0]
        if t==8: return rs()
        if t==9:
            it=f.read(1)[0]; n=struct.unpack('>i',f.read(4))[0]; return [pl(it) for _ in range(n)]
        if t==10:
            d={}
            while True:
                tt=f.read(1)[0]
                if tt==0: return d
                k=rs(); d[k]=pl(tt)
        raise Exception('tag %d'%t)
    t=f.read(1)[0]; assert t==10; rs(); return pl(10)
nbt=rd(io.BytesIO(gzip.open('Morsi_Mobile_Shop.nbt').read()))
sx,sy,sz=nbt['size']; pal=nbt['palette']; bl=nbt['blocks']
assert (sx,sy,sz)==(31,19,29)
assert len(bl)==sx*sy*sz and len({tuple(b['pos']) for b in bl})==len(bl)
assert all(0<=b['state']<len(pal) for b in bl)
NSEW={'north','east','south','west','waterlogged'}
PROPS={'stairs':{'facing','half','shape','waterlogged'},'slab':{'type','waterlogged'},'log':{'axis'},'hay_block':{'axis'},'door':{'facing','half','hinge','open','powered'},
 'trapdoor':{'facing','half','open','powered','waterlogged'},'ladder':{'facing','waterlogged'},'fence':NSEW,'pane':NSEW,'iron_bars':NSEW,'wall':NSEW|{'up'},'chest':{'facing','type','waterlogged'},'barrel':{'facing','open'},
 'lantern':{'hanging','waterlogged'},'chain':{'axis','waterlogged'},'candle':{'candles','lit','waterlogged'},'bed':{'facing','part','occupied'},'cauldron':set(),'flower_pot':set(),'brewing_stand':{'has_bottle_0','has_bottle_1','has_bottle_2'},
 'lectern':{'facing','has_book','powered'},'smoker':{'facing','lit'},'grindstone':{'face','facing'},'redstone_lamp':{'lit'},'pressure_plate':{'powered'},'button':{'face','facing','powered'},'lightning_rod':{'facing','powered','waterlogged'}}
def fam(n):
    for k in sorted(PROPS,key=len,reverse=True):
        if n.endswith(k): return k
VAL={'facing':{'north','south','east','west','up','down'},'half':{'top','bottom','upper','lower'},'axis':{'x','y','z'},'type':{'top','bottom','double','single','left','right'},'hinge':{'left','right'},'face':{'floor','wall','ceiling'}}
for p in pal:
    n=p['Name'].split(':')[1]; pr=p.get('Properties',{}); f=fam(n)
    if pr and f is None: errs.append('props on unknown family '+n)
    elif f:
        bad=set(pr)-PROPS[f]
        if bad: errs.append('bad props %s on %s'%(bad,n))
    for k,v in pr.items():
        if k in VAL and v not in VAL[k]: errs.append('bad value %s=%s on %s'%(k,v,n))
        if k=='candles' and not 1<=int(v)<=4: errs.append('candles')
notes.append('%d unique block ids, %d palette states'%(len({p['Name'] for p in pal}),len(pal)))
notes.append('loot tables: '+', '.join(sorted({b['nbt']['LootTable'] for b in bl if 'nbt' in b})))
G={tuple(b['pos']):pal[b['state']]['Name'].split(':')[1] for b in bl}
def chk(pos,exp,l):
    if G[pos]!=exp: errs.append('%s: expected %s got %s at %s'%(l,exp,G[pos],pos))
chk((15,5,14),'air','Morsi spawn cell'); chk((15,6,14),'air','spawn headroom'); chk((15,4,14),'light_gray_concrete','spawn floor') if G[(15,4,14)]!='white_concrete' else None
chk((14,5,8),'oak_door','shop door L'); chk((15,5,8),'oak_door','shop door R'); chk((11,4,20),'dark_oak_trapdoor','hidden basement hatch'); chk((11,2,20),'ladder','basement ladder')
chk((15,1,22),'bookshelf','secret door shelf'); chk((15,4,25),'iron_trapdoor','alley manhole'); chk((15,2,25),'ladder','manhole ladder'); chk((18,5,22),'oak_door','rear door')
PASS={'air','ladder','oak_door','birch_door','dark_oak_trapdoor','iron_trapdoor','red_carpet','brown_carpet','candle','lantern','cobweb','flower_pot','black_carpet'}
def passable(n): return n in PASS or n.endswith(('_stairs','_slab','_carpet','_button','_bed'))
def flood(start,extra=()):
    seen={start};q=[start]
    while q:
        x,y,z=q.pop()
        for d in((1,0,0),(-1,0,0),(0,1,0),(0,-1,0),(0,0,1),(0,0,-1)):
            n=(x+d[0],y+d[1],z+d[2])
            if n in seen or n not in G: continue
            if passable(G[n]) or n in extra: seen.add(n);q.append(n)
    return seen
reach=flood((15,5,6))
def rr(p,l): notes.append('%s: %s'%(l,p in reach))
rr((15,5,14),'street -> shop floor'); rr((13,5,19),'-> back office'); rr((15,9,14),'-> Morsi apartment living room'); rr((12,9,19),'-> bedroom'); rr((16,13,16),'-> rooftop terrace')
rr((15,1,15),'-> basement (via hatch/ladder)'); rr((15,5,25),'-> rear alley (via rear door)'); 
PASS.discard('iron_trapdoor')
rb=flood((15,1,15)); notes.append('from basement, tunnel reachable WITHOUT breaking the bookshelf (manhole sealed): %s'%((15,1,24) in rb))
rb2=flood((15,1,15),extra={(15,1,22),(15,2,22)}); notes.append('after breaking the bookshelf: %s ; tunnel -> alley shaft top (15,3,25): %s'%((15,1,24) in rb2,(15,3,25) in rb2))
PASS.add('iron_trapdoor')
rr((5,5,14),'-> pharmacy interior'); rr((25,5,14),'-> grocery interior'); rr((5,9,14),'-> pharmacy upper floor'); rr((25,9,14),'-> grocery upper floor')
mx=max(p[1] for p in G if G[p]!='air'); notes.append('highest built y=%d'%mx)
# sign text presence
q=sum(1 for (x,y,z),n in G.items() if z==7 and 13<=y<=17 and n in('quartz_block','quartz_slab'))
notes.append('sign blocks (white text): %d'%q)
# ---- character
geo=json.load(open('Morsi.geo.json')); g=geo['minecraft:geometry'][0]; bn={b['name'] for b in g['bones']}
for b in g['bones']:
    if 'parent' in b and b['parent'] not in bn: errs.append('geo parent missing')
cubes=[c for b in g['bones'] for c in b.get('cubes',[])]
for c in cubes:
    for fn,f in c['uv'].items():
        (u,v),(w,h)=f['uv'],f['uv_size']
        if u<0 or v<0 or u+w>128 or v+h>128: errs.append('uv out of range %s'%fn)
notes.append('geo: %d bones, %d cubes (per-face UV), tex %dx%d'%(len(bn),len(cubes),g['description']['texture_width'],g['description']['texture_height']))
an=json.load(open('Morsi.animation.json'))['animations']
want=['idle','walk','run','phone_call','argument','punch_attack','defensive_counter','nervous_reaction','summoned_arrival','death']
assert [k.split('.')[-1] for k in an]==want
for k,a in an.items():
    L=a['animation_length']
    for b,chs in a['bones'].items():
        if b not in bn: errs.append('anim bone missing '+b)
        for ch,kf in chs.items():
            if max(float(t) for t in kf)>L+1e-6: errs.append('key beyond length '+k)
    notes.append('%s: %.2fs loop=%s, %d bones, %d keyframes'%(k.split('.')[-1],L,a.get('loop',False),len(a['bones']),sum(len(c) for b in a['bones'].values() for c in b.values())))
t=Image.open('Morsi_Texture.png'); assert t.size==(128,128)
bb=json.load(open('Morsi.bbmodel')); notes.append('bbmodel: %d elements, %d animations'%(len(bb['elements']),len(bb['animations'])))
print('\n'.join(notes)); print('ERRORS:',errs if errs else 'none')
