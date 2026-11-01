import random, math
from PIL import Image, ImageDraw, ImageFont
RNG=random.Random(57)
SX,SY,SZ=31,19,29          # x (street width), y (height), z (depth)
GY=4
B={}
def setb(x,y,z,name,nbt=None,**p):
    if 0<=x<SX and 0<=y<SY and 0<=z<SZ:
        B[(x,y,z)]=(name,{k:(str(v).lower() if isinstance(v,bool) else str(v)) for k,v in p.items()},nbt)
def get(x,y,z): return B.get((x,y,z),('air',{},None))[0]
def fill(x0,y0,z0,x1,y1,z1,name,**p):
    for x in range(min(x0,x1),max(x0,x1)+1):
        for y in range(min(y0,y1),max(y0,y1)+1):
            for z in range(min(z0,z1),max(z0,z1)+1): setb(x,y,z,name,**p)
def air(*a): fill(*a,'air')
def stair(x,y,z,mat,facing,half='bottom'): setb(x,y,z,mat+'_stairs',facing=facing,half=half)
def slab(x,y,z,mat,t='bottom'): setb(x,y,z,mat+'_slab',type=t)
def trap(x,y,z,mat,facing,half='bottom',open_=False): setb(x,y,z,mat+'_trapdoor',facing=facing,half=half,open=open_)
def door(x,y,z,mat,facing,hinge='left',open_=False):
    setb(x,y,z,mat+'_door',facing=facing,half='lower',hinge=hinge,open=open_); setb(x,y+1,z,mat+'_door',facing=facing,half='upper',hinge=hinge,open=open_)
def ladder(x,y0,y1,z,facing):
    for y in range(y0,y1+1): setb(x,y,z,'ladder',facing=facing)
def chest(x,y,z,facing,loot): setb(x,y,z,'chest',nbt={'id':'minecraft:chest','LootTable':loot},facing=facing,type='single')
def barrel(x,y,z,loot=None): setb(x,y,z,'barrel',nbt=({'id':'minecraft:barrel','LootTable':loot} if loot else None),facing='up',open=False)
def pick(*a): return RNG.choice(a)
def r(p): return RNG.random()<p
BOXC=['blue_concrete','red_concrete','orange_concrete','lime_concrete','white_concrete','magenta_concrete','yellow_concrete','cyan_concrete','light_blue_concrete','purple_concrete']
# ------------------------------------------------------------------ arabic text -> bitmap
FONT='/usr/share/fonts/truetype/dejavu/DejaVuSansCondensed-Bold.ttf'
def arabic_bitmap(text,cols,rows,thr=0.33):
    f=ImageFont.truetype(FONT,240,layout_engine=ImageFont.Layout.RAQM)
    im=Image.new('L',(3200,600),0); d=ImageDraw.Draw(im)
    d.text((50,50),text,font=f,fill=255,direction='rtl',language='ar')
    im=im.crop(im.getbbox()).resize((cols,rows),Image.BOX)
    return [[1 if im.getpixel((x,y))>255*thr else 0 for x in range(cols)] for y in range(rows)]
def place_text(grid,xhi,ytop,z,full,slabmat,slabtype_ok=True):
    """grid has 2 half-rows per block row. viewer (street, looking +z) has +x on its left, so col c -> x=xhi-c."""
    cols=len(grid[0]); nrows=len(grid)//2
    for br in range(nrows):
        y=ytop-br
        for c in range(cols):
            top=grid[2*br][c]; bot=grid[2*br+1][c]
            x=xhi-c
            if top and bot: setb(x,y,z,full)
            elif top: slab(x,y,z,slabmat,'top')
            elif bot: slab(x,y,z,slabmat,'bottom')
def ascii_grid(g): return '\n'.join(''.join('#' if v else '.' for v in row) for row in g)
# ------------------------------------------------------------------ ground, street, alley
for x in range(SX):
    for z in range(SZ):
        for y in range(0,GY): setb(x,y,z,pick('stone','dirt','stone','gravel') if y<2 else 'stone')
        setb(x,GY,z,'smooth_stone')
# road z 0..4 (surface y=3), sidewalk z 5..7 (y=4)
for x in range(SX):
    for z in range(0,5):
        setb(x,GY-1,z,'black_concrete'); B.pop((x,GY,z),None)
        if z==2 and x%4<2: setb(x,GY-1,z,'white_concrete')
    for z in range(5,8): setb(x,GY,z,'polished_andesite' if (x+z)%7 else 'stone_bricks')
    setb(x,GY,5,'stone_bricks')
# alley z 23..28
for x in range(SX):
    for z in range(23,28): setb(x,GY,z,pick('gravel','cobblestone','gravel','coarse_dirt'))
# ------------------------------------------------------------------ building shells
BASE={'L':(0,10,'light_gray_terracotta'),'M':(10,20,'smooth_sandstone'),'R':(20,30,'orange_terracotta')}
def shell(x0,x1,wall,floorA,floorB):
    for x in range(x0,x1+1):
        for z in range(8,23):
            on=x in(x0,x1) or z in(8,22)
            for y in range(GY,13):
                if on: 
                    if get(x,y,z) in('air','smooth_stone') or True: setb(x,y,z,wall if y not in(8,12) else 'smooth_sandstone')
                else: setb(x,y,z,'air')
            setb(x,GY,z,floorA if (x+z)%2 else floorB) if not on else None
            if not on: setb(x,8,z,'smooth_stone' if r(0.0) else 'stone_bricks'); 
            if not on: setb(x,12,z,'stone_bricks')
for k,(x0,x1,wall) in BASE.items():
    shell(x0,x1,wall,'white_concrete','light_gray_concrete')
    for x in range(x0,x1+1): 
        for z in range(8,23):
            if x in(x0,x1) or z in(8,22): setb(x,GY,z,'stone_bricks')   # footing course
# parapet + rooftop
for x in range(0,31):
    for z in range(8,23):
        if x in(0,30) or z in(8,22) or x in(10,20):
            setb(x,13,z,'smooth_sandstone' if (x+z)%3 else 'stone_brick_wall') if (x%2==0 or z%2==0 or x in(0,30)) else setb(x,13,z,'smooth_sandstone')
# ------------------------------------------------------------------ MORSI ground floor
def front_shop(x0,x1,doorx,glass):
    # shopfront glass y5..7 at z=8
    for x in range(x0+1,x1):
        for y in (5,6,7): setb(x,y,8,glass)
        setb(x,5,8,'polished_andesite')
    for x in (doorx,doorx+1):
        air(x,5,8,x,7,8)
    door(doorx,5,8,'oak','north','left'); door(doorx+1,5,8,'oak','north','right')
    for x in (doorx,doorx+1): setb(x,7,8,'glass')
    for x in range(x0,x1+1): setb(x,8,8,'smooth_sandstone')
front_shop(10,20,14,'glass_pane')
# shop interior tiles / partitions
for x in range(11,20):
    for z in range(9,22): setb(x,GY,z,'white_concrete' if (x+z)%2 else 'light_gray_concrete')
# back office (x11..15,z17..21) + door
for z in range(17,22): setb(16,5,z,'white_concrete'); [setb(16,y,z,'white_concrete') for y in (5,6,7)]
for x in range(11,17): [setb(x,y,17,'white_concrete') for y in (5,6,7)]
air(13,5,17,13,6,17); door(13,5,17,'oak','south','left')
air(16,5,19,16,6,19); door(16,5,19,'oak','west','left')
for x in range(11,16):
    for z in range(18,22): setb(x,GY,z,'spruce_planks')
# stairs ground -> first floor (x=19)
stair(19,5,20,'stone_brick','north'); stair(19,6,19,'stone_brick','north'); stair(19,7,18,'stone_brick','north')
for z in (18,19,20): air(19,8,z,19,8,z)
setb(18,5,18,'stone_bricks') if False else None
for z in (18,19,20): setb(18,5,z,'oak_fence') if False else None
# back door to alley
air(18,5,22,18,6,22); door(18,5,22,'oak','south','left')
# glass display counter (front row glass, back row concrete) + phones
for x in range(11,16):
    setb(x,5,11,'glass'); setb(x,5,12,'light_gray_concrete')
    if x in(12,14): setb(x,6,12,'polished_blackstone_button',face='floor',facing='north')
    if x in(11,13,15): setb(x,6,12,'iron_trapdoor',facing='north',half='bottom',open=False)
setb(11,6,12,'black_carpet') if False else None
for z in (11,12): setb(10+1,5,z,'light_gray_concrete')
# cashier counter (east) + POS
for x in range(16,20): setb(x,5,11,'quartz_block'); setb(x,5,12,'quartz_block')
setb(18,6,12,'lectern',facing='north',has_book=False); chest(17,6,12,'north','minecraft:chests/village/village_plains_house') if False else barrel(17,6,12)
setb(16,6,12,'polished_blackstone_button',face='floor',facing='north'); setb(19,6,12,'candle',candles=1,lit=False); setb(19,6,11,'flower_pot')
# wall shelves with colourful phone boxes: east wall x=19 z 13..16, back partition z=16 x 14..18
for z in range(13,17):
    for y in (5,6,7): setb(19,y,z,pick(*BOXC) if r(0.9) else 'dark_oak_planks')
for x in range(14,19):
    for y in (5,6,7): setb(x,y,16,pick(*BOXC) if r(0.9) else 'dark_oak_planks')
for z in range(13,17): slab(19,8,z,'dark_oak') if False else None
# wall display (front-left window) phone stands
for (x,z) in((11,9),(12,9),(18,9),(19,9)):
    setb(x,5,z,'quartz_slab',type='bottom'); setb(x,6,z,'polished_blackstone_button',face='floor',facing='north')
# repair desk (west wall) with tools
for z in range(14,17): setb(11,5,z,'spruce_planks'); setb(12,5,z,'spruce_planks')
setb(11,6,14,'brewing_stand'); setb(12,6,15,'lantern'); setb(11,6,16,'grindstone',face='floor',facing='east'); setb(12,6,14,'iron_trapdoor',facing='south',half='bottom',open=False); setb(11,6,15,'redstone_lamp',lit=False)
setb(13,5,15,'dark_oak_stairs',facing='west',half='bottom')
# bench + plant for customers
for x in (17,18,19): stair(x,5,9,'dark_oak','south')
setb(16,5,9,'flower_pot'); setb(15,5,9,'air')
# back office furniture + hidden hatch
setb(12,5,18,'spruce_planks'); setb(13,5,18,'spruce_planks'); setb(14,5,18,'spruce_planks'); setb(13,6,18,'iron_trapdoor',facing='south',half='bottom',open=False); setb(12,6,18,'lantern')
stair(13,5,19,'dark_oak','north'); 
for y in (5,6,7): setb(15,y,21,'bookshelf'); setb(15,y,20,'bookshelf') if y<7 else None
chest(14,5,21,'north','minecraft:chests/village/village_plains_house'); 
setb(11,5,20,'brown_carpet'); setb(11,5,21,'brown_carpet'); setb(12,5,20,'brown_carpet'); setb(12,5,21,'brown_carpet')
# lights
for (x,z) in((13,10),(17,10),(13,15),(17,14)):
    setb(x,8,z,'chain',axis='y') if False else None
    setb(x,7,z,'sea_lantern')
# AC + sign band (fascia): red board with white phone icons
for x in range(10,21):
    setb(x,8,7,'red_concrete'); 
for x in (11,12,16,17,18):
    pass
# ------------------------------------------------------------------ BASEMENT (x11..19, z10..21) + secret tunnel
for x in range(10,21):
    for z in range(10,23):
        for y in range(0,4):
            edge=x in(10,20) or z in(10,22) or y==0
            setb(x,y,z,pick('stone_bricks','stone_bricks','cracked_stone_bricks','mossy_stone_bricks') if edge else 'air')
for x in range(11,20):
    for z in range(11,22): setb(x,0,z,'stone_bricks'); 
# stair to basement: ladder (11,1..3,20), hatch (11,4,20) hidden under rug
ladder(11,1,3,20,'east'); setb(11,GY,20,'dark_oak_trapdoor',facing='east',half='top',open=False)
# storage shelving: boxes along z=21 and x=19, crates, tables
for x in range(12,19):
    if x==15: setb(15,1,21,'air'); setb(15,2,21,'air'); setb(15,3,21,pick(*BOXC)); continue
    for y in (1,2,3): setb(x,y,21,pick(*BOXC) if r(0.85) else 'barrel') if True else None
for z in range(12,21):
    for y in (1,2): setb(19,y,z,pick(*BOXC) if r(0.8) else 'barrel')
for (x,z) in((13,13),(14,13),(13,14),(16,16),(17,16),(12,17)): barrel(x,1,z,'minecraft:chests/village/village_plains_house') if r(0.4) else barrel(x,1,z)
for (x,z) in((16,13),(17,13)): chest(x,1,z,'south','minecraft:chests/simple_dungeon') if x==16 else setb(x,1,z,'chest',nbt={'id':'minecraft:chest','LootTable':'minecraft:chests/village/village_plains_house'},facing='south',type='single')
setb(15,1,13,'crafting_table'); setb(15,1,12,'dark_oak_stairs',facing='south',half='bottom'); setb(14,1,17,'iron_trapdoor',facing='north',half='bottom',open=False)
for (x,z) in((12,12),(18,12),(12,20),(18,20),(15,16)): setb(x,3,z,'lantern',hanging=True)
for (x,z) in((11,12),(11,14)): setb(x,1,z,'cobweb')
# secret tunnel from behind the bookshelf: (15,1..2,22..24) then shaft at (15,y,25)
for z in range(22,26):
    for x in (14,15,16):
        for y in range(0,4): 
            if (x,y,z) not in B or get(x,y,z)!='air': setb(x,y,z,'stone_bricks' if y in(0,3) or x!=15 else 'air')
    for y in (1,2): setb(15,y,z,'air')
setb(15,1,22,'air'); setb(15,2,22,'air')
setb(15,1,22,'bookshelf'); setb(15,2,22,'bookshelf')                  # secret door (break to pass), hides tunnel behind the shelf
for y in (1,2): setb(15,y,21,'air')
for z in (23,24): 
    for y in (1,2): setb(15,y,z,'air')
setb(15,1,24,'candle',candles=2,lit=True); setb(15,2,24,'air')
for y in range(1,4): setb(15,y,25,'ladder',facing='north')
for y in range(0,5): setb(15,y,26,'stone_bricks') if y<4 else None
setb(15,GY,25,'iron_trapdoor',facing='north',half='top',open=False)
for (dx,dz) in((-1,0),(1,0),(0,-1),(0,1)): setb(15+dx,GY,25+dz,'cobblestone') if (15+dx,25+dz)!=(15,26) else None
for y in range(0,4):
    for x in (14,16): setb(x,y,25,'stone_bricks')
# ------------------------------------------------------------------ FIRST FLOOR apartment (Morsi): y9..11
for x in range(11,20):
    for z in range(9,22): setb(x,8,z,'dark_oak_planks' if (x+z)%2 else 'spruce_planks')
for z in (18,19,20): air(19,8,z,19,8,z)
for x in range(11,20):
    for y in (9,10,11): setb(x,y,17,'smooth_sandstone') if x<=16 else None
for z in range(17,22):
    for y in (9,10,11): setb(16,y,z,'smooth_sandstone')
air(13,9,17,13,10,17); door(13,9,17,'birch','south','left'); air(16,9,19,16,10,19); door(16,9,19,'birch','west','left')
# windows front + balcony door + balcony
for x in (12,13,17,18):
    for y in (9,10): setb(x,y,8,'glass_pane')
air(15,9,8,15,10,8); door(15,9,8,'oak','north','left')
for x in range(11,20):
    for z in (6,7): setb(x,8,z,'smooth_sandstone')
for x in range(11,20): setb(x,9,5,'iron_bars')
for z in (6,7): setb(11,9,z,'iron_bars'); setb(19,9,z,'iron_bars')
for x in (12,14,16,18): setb(x,9,7,'flower_pot')
setb(17,9,6,'iron_trapdoor',facing='north',half='bottom',open=False); setb(13,9,6,'barrel',nbt=None,facing='up',open=False)
# living room
for x in range(13,18):
    for z in (11,12,13): setb(x,9,z,'red_carpet')
stair(13,9,10,'dark_oak','south'); stair(14,9,10,'dark_oak','south'); stair(15,9,10,'dark_oak','south'); setb(12,9,10,'dark_oak_stairs',facing='east',half='bottom'); stair(12,9,11,'dark_oak','east')
setb(15,9,12,'dark_oak_slab',type='bottom'); setb(15,10,12,'candle',candles=3,lit=True) 
for x in (13,14,15): 
    for y in (10,11): setb(x,y,16,'black_concrete')
setb(14,10,16,'cyan_stained_glass'); setb(14,9,16,'dark_oak_slab',type='top'); setb(13,9,16,'sea_lantern') if False else None
for y in (9,10,11): setb(11,y,15,'bookshelf'); setb(11,y,14,'bookshelf') if y<11 else None
setb(19,9,13,'smoker',facing='west',lit=False); setb(19,9,14,'cauldron'); setb(19,9,15,'crafting_table'); setb(19,10,15,'flower_pot'); setb(19,9,16,'barrel',facing='up',open=False)
setb(18,11,12,'lantern',hanging=True) if False else None
setb(15,11,13,'chain',axis='y'); setb(15,10,13,'lantern',hanging=True)
# bedroom
for z in (19,20): setb(11,9,z,'blue_bed',facing='east',part='head' if z==19 else 'foot',occupied=False) if False else None
setb(11,9,19,'blue_bed',facing='south',part='foot',occupied=False); setb(11,9,20,'blue_bed',facing='south',part='head',occupied=False)
setb(12,9,20,'barrel',facing='up',open=False); setb(12,10,20,'candle',candles=1,lit=True)
chest(15,9,21,'west','minecraft:chests/village/village_plains_house'); setb(15,9,20,'dark_oak_trapdoor',facing='west',half='bottom',open=True) if False else None
setb(14,9,18,'spruce_planks'); setb(13,9,18,'spruce_planks'); setb(13,10,18,'iron_trapdoor',facing='south',half='bottom',open=False); setb(14,10,18,'lantern')
stair(14,9,19,'dark_oak','north')
for x in (12,13,14): setb(x,9,21,'brown_carpet') if False else None
setb(12,9,21,'brown_carpet'); setb(13,9,21,'brown_carpet'); setb(14,9,21,'brown_carpet')
# stairs to roof x=18 (z20->18)
stair(18,9,20,'stone_brick','north'); stair(18,10,19,'stone_brick','north'); stair(18,11,18,'stone_brick','north')
for z in (18,19,20): air(18,12,z,18,12,z)
setb(17,12,18,'oak_fence'); setb(17,13,18,'oak_fence') if False else None
for z in (18,19,20): setb(17,13,z,'smooth_sandstone') if False else None
# ------------------------------------------------------------------ ROOF TERRACE (Morsi) + satellite dishes
def dish(x,z,fx):
    setb(x,13,z,'iron_bars'); setb(x,14,z,'iron_bars')
    setb(x,15,z,'smooth_quartz'); 
    for f,(dx,dz) in {'north':(0,-1),'south':(0,1),'east':(1,0),'west':(-1,0)}.items():
        stair(x+dx,15,z+dz,'quartz',{'north':'south','south':'north','east':'west','west':'east'}[f],'top')
    setb(x+{'east':2,'west':-2,'north':0,'south':0}[fx],16,z+{'east':0,'west':0,'north':-2,'south':2}[fx],'lightning_rod',facing='up') if False else setb(x,16,z,'lightning_rod',facing='up')
dish(12,19,'north'); dish(16,20,'north'); dish(11,12,'north')
for (x,z) in((18,10),(19,10)): pass
# water tank
fill(17,13,10,18,14,11,'light_gray_concrete'); setb(17,15,10,'iron_trapdoor',facing='north',half='bottom',open=False)
# terrace furniture
for (x,z) in((13,10),(14,10)): stair(x,13,z,'dark_oak','north')
setb(13,13,12,'dark_oak_fence'); setb(13,14,12,'dark_oak_pressure_plate',powered=False); setb(15,13,12,'barrel',facing='up',open=False); setb(11,13,16,'flower_pot'); setb(12,13,16,'flower_pot')
# laundry line
for x in (11,19): setb(x,13,14,'oak_fence'); setb(x,14,14,'oak_fence')
for x in range(12,19): setb(x,15,14,'chain',axis='x'); 
for x,c in ((13,'white_wool'),(14,'blue_wool'),(16,'red_wool'),(17,'white_wool')): setb(x,14,14,c)
# ------------------------------------------------------------------ NEIGHBOURS
def neighbour(x0,x1,kind):
    cx=(x0+x1)//2
    # shopfront
    for x in range(x0+1,x1):
        for y in (5,6,7): setb(x,y,8,'glass_pane' if x not in(cx,cx+1) else 'air')
        setb(x,5,8,'polished_andesite')
    air(cx,5,8,cx,7,8); door(cx,5,8,'oak','north','left'); setb(cx,7,8,'glass')
    for x in range(x0,x1+1): setb(x,8,8,'smooth_sandstone'); setb(x,8,7,'green_concrete' if kind=='L' else 'yellow_concrete')
    # interior floor & stuff
    for x in range(x0+1,x1):
        for z in range(9,22): setb(x,GY,z,'smooth_stone' if kind=='L' else 'terracotta')
    air(cx+0,5,22,cx+0,6,22) ; door(cx,5,22,'oak','south','left') 
    # counter + shelves
    for x in range(x0+2,x0+6): setb(x,5,12,'quartz_block' if kind=='L' else 'dark_oak_planks'); setb(x,6,12,'polished_blackstone_button',face='floor',facing='north') if x%2 else None
    for x in range(x0+1,x1):
        for y in (5,6,7): setb(x,y,21,pick('white_concrete','lime_concrete','light_blue_concrete','white_concrete') if kind=='L' else pick('orange_concrete','red_concrete','yellow_concrete','lime_concrete','barrel'.replace('barrel','brown_concrete')))
    for z in range(13,20): 
        for y in (5,6): setb(x1-1,y,z,pick('white_concrete','lime_concrete') if kind=='L' else pick('hay_block','melon','pumpkin','orange_concrete'))
    chest(x0+2,5,19,'south','minecraft:chests/village/village_plains_house')
    # stairs to upper floor (near x1-1)
    sx=x1-2
    stair(sx,5,20,'stone_brick','north'); stair(sx,6,19,'stone_brick','north'); stair(sx,7,18,'stone_brick','north')
    for z in (18,19,20): air(sx,8,z,sx,8,z)
    # first floor furnishing
    for x in range(x0+1,x1):
        for z in range(9,22):
            if (x,8,z)==(sx,8,19) or (x==sx and z in(18,20)): continue
            setb(x,8,z,'dark_oak_planks' if (x+z)%2 else 'spruce_planks')
    setb(x0+2,9,12,'red_bed',facing='south',part='foot',occupied=False) if False else None
    setb(x0+2,9,14,'green_bed' if kind=='L' else 'orange_bed',facing='south',part='foot',occupied=False); setb(x0+2,9,15,'green_bed' if kind=='L' else 'orange_bed',facing='south',part='head',occupied=False)
    setb(x0+4,9,11,'dark_oak_fence'); setb(x0+4,10,11,'dark_oak_pressure_plate',powered=False); stair(x0+3,9,11,'dark_oak','east'); stair(x0+5,9,11,'dark_oak','west')
    barrel(x0+2,9,19); setb(x0+3,9,19,'bookshelf'); setb(x0+3,10,19,'bookshelf')
    for z in (18,19,20): setb(sx-1,12,z,'stone_bricks') if False else None
    # upper windows + balcony
    for x in (x0+2,x0+3,x1-3,x1-2):
        for y in (9,10): setb(x,y,8,'glass_pane')
    air(cx,9,8,cx,10,8); door(cx,9,8,'oak','north','left')
    for x in range(x0+2,x1-1):
        for z in (6,7): setb(x,8,z,'smooth_sandstone')
        setb(x,9,5,'iron_bars')
    for z in (6,7): setb(x0+2,9,z,'iron_bars'); setb(x1-2,9,z,'iron_bars')
    for x in range(x0+3,x1-2,2): setb(x,9,7,'flower_pot')
    # laundry on balcony line
    setb(x0+3,10,5,'white_wool'); setb(x0+4,10,5,'blue_wool' if kind=='L' else 'red_wool'); setb(x1-4,10,5,'white_wool')
    # awning (striped)
    for x in range(x0,x1+1):
        for z in (6,7):
            if get(x,7,z)=='air': setb(x,7,z,('green_wool' if kind=='L' else 'yellow_wool') if (x//1)%2==0 else 'white_wool')
    # roof: tank + dish
    setb(x0+2,13,10,'light_gray_concrete'); setb(x0+3,13,10,'light_gray_concrete'); setb(x0+2,14,10,'light_gray_concrete'); setb(x0+3,14,10,'light_gray_concrete')
    dish(x1-3,18,'north')
    for z in (18,19,20): air(sx,12,z,sx,12,z)
neighbour(0,10,'L'); neighbour(20,30,'R')
# Morsi awning (red/white)
for x in range(10,21):
    for z in (6,7): setb(x,7,z,'red_wool' if x%2==0 else 'white_wool')
# street props
for (x,z) in((3,5),(27,5),(15,5)):
    pass
for x in (5,24):
    for y in range(5,9): setb(x,y,5,'iron_bars')
    setb(x,9,5,'sea_lantern'); 
setb(8,5,6,'cauldron'); setb(22,5,6,'cauldron'); 
for (x,z) in((2,6),(28,6),(9,6),(21,6)): setb(x,5,z,'barrel',facing='up',open=False)
# produce stand outside grocery
for x in range(24,28): setb(x,5,6,'dark_oak_trapdoor',facing='north',half='bottom',open=False) if False else setb(x,5,6,'oak_slab',type='bottom'); 
for x in range(24,28): setb(x,6,6,pick('melon','pumpkin','hay_block','orange_concrete'))
# tree pits
for x in (3,13,17,27): 
    pass
# ------------------------------------------------------------------ rear alley details
for x in range(0,31):
    setb(x,GY,28,'cobblestone')
    for y in range(5,13): setb(x,y,28,pick('light_gray_terracotta','smooth_sandstone','orange_terracotta') if (x//10)%3==0 else pick('smooth_sandstone','light_gray_terracotta'))
    for y in (7,10): 
        if x%3==1: setb(x,y,28,'glass_pane')
for x in (4,9,21,26): 
    for y in range(5,9): setb(x,y,23,'iron_bars') if False else None
for x in (3,12,22,28): barrel(x,5,24)
for (x,z) in((8,24),(24,26),(5,26)): setb(x,5,z,'cauldron')
for x in (6,19,25):
    for y in (6,7): setb(x,y,23,'iron_trapdoor',facing='south',half='bottom',open=False) if False else None
setb(19,6,23,'iron_trapdoor',facing='south',half='top',open=False)    # AC unit on wall
for z in (23,): pass
for (x,z) in((2,25),(13,26),(18,25)): setb(x,5,z,'cobweb') if False else None
# alley lanterns & clothes line
for x in (2,28): 
    for y in (5,6,7): setb(x,y,26,'oak_fence')
    setb(x,8,26,'lantern')
for x in range(3,28): setb(x,9,26,'chain',axis='x') if False else None
# ------------------------------------------------------------------ SIGN: rooftop billboard
grid=arabic_bitmap('مرسي للمحمول',31,10,thr=0.30)
for x in range(0,31):
    for y in range(12,19): setb(x,y,8,'red_concrete')
for x in range(0,31): setb(x,18,8,'black_concrete'); setb(x,12,8,'black_concrete')
place_text(grid,30,17,7,'quartz_block','quartz')
for x in range(1,31,5):
    for y in range(12,19): setb(x,y,9,'dark_oak_fence') if y>=13 else None
# neighbour name plates (small): green board 'صيدلية', yellow board 'بقالة' on the facade between awning and roof
def plate(x0,x1,word,bg,txt,slabm):
    for x in range(x0,x1+1):
        for y in (9,10,11): setb(x,y,7,bg)
    g=arabic_bitmap(word,x1-x0-1,4); 
    # 4 half-rows -> 2 block rows centred in y 10..11
    place_text(g,x1-1,11,7,txt,slabm)
plate(1,9,'صيدلية','green_concrete','white_concrete','quartz') if False else None
plate_ok=True
def plate2(xlo,xhi,word,bg,txt,sl,rows=3):
    for x in range(xlo,xhi+1):
        for y in (9,10,11): setb(x,y,7,bg)
    g=arabic_bitmap(word,xhi-xlo-1,rows*2)
    place_text(g,xhi-1,11,7-0,txt,sl) if False else None
    cols=len(g[0])
    for br in range(rows):
        y=11-br
        for c in range(cols):
            tp=g[2*br][c]; bt=g[2*br+1][c]; x=xhi-1-c
            if tp and bt: setb(x,y,6,txt)
            elif tp: slab(x,y,6,sl,'top')
            elif bt: slab(x,y,6,sl,'bottom')
plate2(1,9,'صيدلية','green_concrete','white_concrete','quartz')
plate2(21,29,'بقالة','yellow_concrete','black_concrete','polished_blackstone',rows=3)
# remove stray conflicts: keep the awning clear of plates
# ------------------------------------------------------------------ finalize: connections
def finalize():
    solid=lambda n: n not in('air','water','lantern','chain','candle','flower_pot','ladder','cobweb','lightning_rod','sea_lantern') and not n.endswith(('_trapdoor','_door','_slab','_stairs','_carpet','_bed','_button'))
    for (x,y,z),(n,p,nb) in list(B.items()):
        if n.endswith('_fence') or n.endswith('_pane') or n=='iron_bars' or n.endswith('_wall'):
            for d,(dx,dz) in {'north':(0,-1),'south':(0,1),'east':(1,0),'west':(-1,0)}.items():
                nn=get(x+dx,y,z+dz)
                if n.endswith('_fence'): ok=nn.endswith('_fence') or (solid(nn) and not nn.endswith(('_pane','_wall')) and nn!='iron_bars')
                elif n.endswith('_wall'): ok=nn.endswith('_wall') or (solid(nn) and not nn.endswith(('_fence','_pane')))
                else: ok=nn.endswith('_pane') or nn=='iron_bars' or (solid(nn) and not nn.endswith('_fence'))
                p[d]=('low' if ok else 'none') if n.endswith('_wall') else ('true' if ok else 'false')
            if n.endswith('_wall'): p['up']='true'
finalize()
if __name__=='__main__':
    print(len(B)); print(ascii_grid(grid))
