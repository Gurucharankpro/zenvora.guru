import gzip, struct, json, io, math
from PIL import Image, ImageDraw
import structure as S
DATAVERSION=3955  # Minecraft 1.21.1
# ---------- NBT writer
def _s(b): d=b.encode('utf8'); return struct.pack('>H',len(d))+d
def tag_id(v):
    if isinstance(v,dict): return 10
    if isinstance(v,list): return 9
    if isinstance(v,bool): return 1
    if isinstance(v,int): return 3
    if isinstance(v,str): return 8
    raise TypeError(v)
def payload(v):
    if isinstance(v,dict):
        o=b''
        for k,x in v.items(): o+=bytes([tag_id(x)])+_s(k)+payload(x)
        return o+b'\x00'
    if isinstance(v,list):
        if not v: return bytes([0])+struct.pack('>i',0)
        t=tag_id(v[0]); return bytes([t])+struct.pack('>i',len(v))+b''.join(payload(x) for x in v)
    if isinstance(v,bool): return struct.pack('>b',int(v))
    if isinstance(v,int): return struct.pack('>i',v)
    if isinstance(v,str): return _s(v)
def write_nbt(root,path):
    raw=bytes([10])+_s('')+payload(root)
    with gzip.GzipFile(path,'wb',mtime=0) as f: f.write(raw)
# ---------- build palette / blocks
palette=[]; pidx={}
def state(n,p):
    key=(n,tuple(sorted(p.items())))
    if key not in pidx:
        e={'Name':'minecraft:'+n}
        if p: e['Properties']={k:v for k,v in sorted(p.items())}
        pidx[key]=len(palette); palette.append(e)
    return pidx[key]
blocks=[]
for x in range(S.SX):
    for y in range(S.SY):
        for z in range(S.SZ):
            n,p,nb=S.B.get((x,y,z),('air',{},None))
            b={'pos':[x,y,z],'state':state(n,p)}
            if nb: b['nbt']=nb
            blocks.append(b)
root={'size':[S.SX,S.SY,S.SZ],'entities':[],'blocks':blocks,'palette':palette,'DataVersion':DATAVERSION}
write_nbt(root,'Morsi_Mobile_Shop.nbt')
print('blocks',len(blocks),'palette',len(palette))
# ---------- counts / palette doc
import collections
cnt=collections.Counter(S.B.get((x,y,z),('air',))[0] for x in range(S.SX) for y in range(S.SY) for z in range(S.SZ))
json.dump({'size':[S.SX,S.SY,S.SZ],'data_version':DATAVERSION,'block_counts':dict(cnt.most_common())},open('palette_counts.json','w'),indent=1)
# ---------- blueprint json (non-air blocks, by layer)
bp={'WARNING':'Block-position blueprint generated from the same data as Morsi_Mobile_Shop.nbt. Coordinates are relative to the structure origin (x east, y up, z south). Air omitted.',
    'size':[S.SX,S.SY,S.SZ],'data_version':DATAVERSION,'layers':{}}
for y in range(S.SY):
    L=[]
    for x in range(S.SX):
        for z in range(S.SZ):
            n,p,nb=S.B.get((x,y,z),('air',{},None))
            if n!='air':
                e=[x,z,n]; 
                if p: e.append(p)
                if nb and 'LootTable' in nb: e.append({'LootTable':nb['LootTable']})
                L.append(e)
    bp['layers'][str(y)]=L
json.dump(bp,open('Morsi_Mobile_Shop_blueprint.json','w'),separators=(',',':'))
# ---------- isometric preview
COL={'dirt':(110,80,55),'stone':(120,120,120),'gravel':(130,125,120),'grass_block':(70,115,50),'coarse_dirt':(105,75,50),'podzol':(95,70,40),'rooted_dirt':(100,72,52),'mud':(60,52,50),
 'water':(50,90,160),'clay':(130,140,160),'farmland':(90,60,35),'dirt_path':(150,125,70),'spruce_planks':(120,85,50),'dark_oak_planks':(70,48,24),'spruce_stairs':(120,85,50),'dark_oak_stairs':(70,48,24),
 'spruce_slab':(120,85,50),'dark_oak_slab':(70,48,24),'dark_oak_log':(50,36,20),'stripped_dark_oak_log':(100,75,45),'stripped_spruce_log':(130,100,60),'cobblestone':(105,105,105),'mossy_cobblestone':(85,110,80),
 'stone_bricks':(115,115,115),'mossy_stone_bricks':(95,115,90),'cracked_stone_bricks':(108,108,108),'chiseled_stone_bricks':(120,120,120),'chiseled_deepslate':(60,60,70),'hay_block':(205,170,50),'spruce_fence':(115,80,45),
 'dark_oak_fence':(65,45,22),'wheat':(190,170,70),'carrots':(210,120,30),'potatoes':(120,150,60),'beetroots':(150,50,60),'pumpkin':(215,125,20),'carved_pumpkin':(215,125,20),'jack_o_lantern':(250,190,60),
 'lantern':(255,200,90),'soul_lantern':(90,230,230),'soul_campfire':(80,200,220),'glass_pane':(170,210,225),'iron_bars':(130,130,130),'cobweb':(235,235,235),'bookshelf':(110,80,50),'barrel':(100,70,40),'chest':(150,105,40),
 'lily_pad':(50,130,40),'sugar_cane':(130,190,90),'short_grass':(80,140,60),'fern':(60,120,50),'dead_bush':(120,90,50),'moss_carpet':(70,120,40),'red_carpet':(150,30,30),'brown_bed':(150,60,60)}
COL.update({'gold_block':(250,200,40),'raw_gold_block':(210,150,40),'yellow_terracotta':(200,160,70),'yellow_stained_glass_pane':(240,220,90),'crimson_planks':(120,40,50),'crimson_stairs':(120,40,50),'crimson_slab':(120,40,50),
 'crimson_fence':(115,38,48),'stripped_crimson_stem':(150,50,60),'orange_wool':(230,120,30),'red_wool':(170,40,35),'yellow_wool':(240,200,50),'brown_wool':(110,70,40),'orange_carpet':(230,120,30),'red_carpet':(170,40,35),
 'yellow_carpet':(240,200,50),'brown_carpet':(110,70,40),'stripped_bamboo_block':(205,185,90),'bamboo_block':(150,170,60),'bamboo':(100,170,60),'bamboo_planks':(200,180,90),'bamboo_mosaic':(190,170,85),'shroomlight':(255,170,70),
 'polished_andesite':(135,135,135),'andesite':(130,130,130),'smooth_stone':(150,150,150),'polished_blackstone_bricks':(50,45,55),'gilded_blackstone':(70,60,50),'cracked_polished_blackstone_bricks':(48,44,52),'polished_blackstone':(55,50,60),
 'moss_block':(80,120,40),'tuff':(110,110,100),'deepslate':(70,70,75),'mangrove_roots':(90,70,45),'hanging_roots':(150,110,90),'white_concrete':(230,230,230),'orange_concrete':(240,110,20),'black_concrete':(20,20,25),'red_concrete':(160,30,30),
 'stone_brick_wall':(115,115,115),'bell':(220,170,50),'decorated_pot':(160,80,60),'amethyst_block':(140,90,190),'flowering_azalea':(220,120,200),'bamboo_fence':(190,170,80),'white_stained_glass_pane':(220,230,235),'campfire':(200,120,40),
 'brewing_stand':(150,150,150),'cauldron':(60,60,65),'spruce_planks':(120,85,50),'dark_oak_trapdoor':(70,48,24),'dark_oak_door':(70,48,24),'candle':(250,230,180),'stripped_dark_oak_log':(100,75,45),'yellow_stained_glass':(240,220,90)})
COL.update({'black_concrete':(25,25,30),'white_concrete':(235,235,235),'light_gray_concrete':(150,150,150),'red_concrete':(190,30,30),'green_concrete':(40,130,50),'yellow_concrete':(240,200,30),'orange_concrete':(240,110,20),
 'blue_concrete':(40,60,170),'lime_concrete':(110,190,30),'magenta_concrete':(180,50,170),'cyan_concrete':(20,140,150),'light_blue_concrete':(60,150,210),'purple_concrete':(110,40,160),'smooth_sandstone':(222,205,150),
 'light_gray_terracotta':(150,120,110),'orange_terracotta':(160,85,45),'terracotta':(150,90,65),'quartz_block':(240,236,228),'quartz_slab':(240,236,228),'smooth_quartz':(240,236,228),'quartz_stairs':(240,236,228),
 'red_wool':(170,40,35),'white_wool':(235,235,235),'blue_wool':(50,60,170),'green_wool':(70,110,40),'yellow_wool':(240,200,50),'sea_lantern':(210,240,240),'iron_bars':(120,120,125),'iron_trapdoor':(190,190,195),
 'polished_blackstone_button':(30,28,35),'melon':(120,170,50),'brown_concrete':(110,70,40),'stone_brick_stairs':(120,120,120),'stone_bricks':(120,120,120),'cracked_stone_bricks':(115,115,115),'mossy_stone_bricks':(100,120,95),
 'polished_andesite':(140,140,140),'smooth_stone':(155,155,155),'oak_slab':(160,125,70),'oak_fence':(150,115,65),'birch_door':(200,185,130),'oak_door':(160,125,70),'glass':(190,225,235),'bookshelf':(120,85,50),
 'red_carpet':(170,40,35),'brown_carpet':(110,70,40),'black_carpet':(25,25,30),'blue_bed':(60,80,180),'red_bed':(180,50,50),'green_bed':(50,140,60),'orange_bed':(230,120,30),'lectern':(130,95,55),'cauldron':(60,60,65),
 'lightning_rod':(200,120,70),'smoker':(110,110,115),'crafting_table':(150,110,60),'brewing_stand':(150,150,150),'grindstone':(140,140,145),'redstone_lamp':(150,100,60),'cyan_stained_glass':(60,170,190),'candle':(250,230,180)})
def color(n):
    if n in COL: return COL[n]
    if 'planks' in n or 'log' in n: return (100,70,40)
    return (150,130,150)
NONSOLID=('short_grass','fern','dead_bush','lily_pad','cobweb','ladder','moss_carpet','red_carpet','sugar_cane','wheat','carrots','potatoes','beetroots','glass_pane','iron_bars','chain','water')
NONSOLID=NONSOLID+('bamboo','candle','hanging_roots','flowering_azalea','yellow_stained_glass_pane','white_stained_glass_pane','yellow_stained_glass','bell','chain')
def iso(rot,fn,sc=11):
    W,H=1100,800; im=Image.new('RGB',(W,H),(24,26,36)); d=ImageDraw.Draw(im)
    def tr(x,y,z):
        a,b=(x,z) if rot==0 else (S.SX-1-z,x) if rot==1 else (S.SX-1-x,S.SZ-1-z) if rot==2 else (z,S.SX-1-x)
        return W/2+(a-b)*sc*0.87, 60+(a+b)*sc*0.5-y*sc*1.0+ 330
    items=[]
    for (x,y,z),(n,p,nb) in S.B.items():
        if n=='air': continue
        items.append((x,y,z,n))
    def key(i):
        x,y,z,n=i; a,b=(x,z) if rot==0 else (S.SX-1-z,x) if rot==1 else (S.SX-1-x,S.SZ-1-z) if rot==2 else (z,S.SX-1-x)
        return (a+b,y)
    items.sort(key=key)
    def open_(x,y,z):
        n=S.B.get((x,y,z),('air',))[0]; return n=='air' or n in NONSOLID
    for x,y,z,n in items:
        # skip hidden underground
        if y<S.GY-1 and n in('dirt','stone','gravel'): 
            if not( x in(0,S.SX-1) or z in(0,S.SZ-1)): continue
        c=color(n)
        X,Y=tr(x,y,z); w=sc*0.87; h=sc*0.5
        top=[(X,Y-sc),(X+w,Y-sc+h),(X,Y-sc+2*h),(X-w,Y-sc+h)]
        left=[(X-w,Y-sc+h),(X,Y-sc+2*h),(X,Y+2*h),(X-w,Y+h)]
        right=[(X+w,Y-sc+h),(X,Y-sc+2*h),(X,Y+2*h),(X+w,Y+h)]
        d.polygon(left,fill=tuple(int(v*0.7) for v in c)); d.polygon(right,fill=tuple(int(v*0.5) for v in c)); d.polygon(top,fill=tuple(min(255,int(v*1.05)) for v in c))
    return im
a=iso(0,None); b=iso(2,None)
im=Image.new('RGB',(2200,800)); im.paste(a,(0,0)); im.paste(b,(1100,0))
d=ImageDraw.Draw(im); d.text((10,8),'MORSI MOBILE PHONE SHOP - isometric preview (custom renderer, not in-game) | two opposite isometric angles | 31x29x19 (x,z,y)',fill=(220,230,200))
im.save('Morsi_Mobile_Shop_Preview.png')
# ---------- layered ASCII
SYM={}
def sym(n):
    if n not in SYM: SYM[n]=(('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%&*+=?~^')[len(SYM)] if len(SYM)<70 else '!')
    return SYM[n]
out=[]
for y in range(S.SY):
    out.append('=== LAYER y=%d (rows = z 0..34 north->south, columns = x 0..34 west->east) ==='%y)
    for z in range(S.SZ):
        out.append(''.join('.' if S.B.get((x,y,z),('air',))[0]=='air' else sym(S.B[(x,y,z)][0]) for x in range(S.SX)))
legend=['LEGEND (symbol = minecraft block id; "." = air)']+['%s = %s'%(v,k) for k,v in SYM.items()]
open('Morsi_Mobile_Shop_layers.txt','w').write('\n'.join(legend+['']+out))
