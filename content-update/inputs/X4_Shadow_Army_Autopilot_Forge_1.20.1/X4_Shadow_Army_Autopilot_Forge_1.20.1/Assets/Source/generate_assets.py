"""X4 Shadow Army Autopilot - asset generator (editable source).
Run: python3 generate_assets.py <output_dir>   (needs Pillow). Every pixel is defined here; edit palette/glyph code and re-run."""
import sys, os, math
from PIL import Image, ImageDraw, ImageFont
OUT = sys.argv[1] if len(sys.argv) > 1 else "out"
K=(0,0,0); NAVY=(8,12,30); DB=(16,30,72); MB=(30,70,150); CY=(70,235,255); CY2=(170,250,255); CYD=(30,140,190)
PU=(130,80,200); PUD=(70,40,120); PUL=(176,130,240); WARN=(255,170,60); RED=(255,70,90); GREY=(90,100,130)
FB="/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"; FR="/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
def mk(p): os.makedirs(os.path.join(OUT,p),exist_ok=True); return os.path.join(OUT,p)
def A(c,a=255): return tuple(c)+(a,)

def badge(accent=CYD):
    im=Image.new('RGBA',(32,32),(0,0,0,0)); d=ImageDraw.Draw(im)
    d.rounded_rectangle([0,0,31,31],radius=3,fill=A(K))
    d.rounded_rectangle([1,1,30,30],radius=2,fill=A(accent))
    for y in range(2,30):
        t=(y-2)/27; c=tuple(int(DB[i]*(1-t)+NAVY[i]*t) for i in range(3)); d.line([(2,y),(29,y)],fill=A(c))
    d.line([(3,2),(28,2)],fill=A(MB)); d.line([(2,27),(29,29)],fill=(*PUD,255)) if False else None
    d.line([(2,29),(29,29)],fill=A(PUD)); d.line([(29,3),(29,28)],fill=A((30,24,70)))
    for x,y in ((2,2),(29,2),(2,29),(29,29)): im.putpixel((x,y),A(accent))
    return im,d
def fig(d,x,y,c,eye=CY2):
    d.rectangle([x+1,y,x+3,y+2],fill=A(c)); d.rectangle([x,y+3,x+4,y+7],fill=A(c)); d.point((x+1,y+1),fill=A(eye)); d.point((x+3,y+1),fill=A(eye))
    d.line([(x,y+3),(x+4,y+3)],fill=A(PUL if c==PU else CY2))
def arrow_head(d,x,y,dx,dy,c):
    # small filled triangle pointing along (dx,dy)
    px,py=-dy,dx; d.polygon([(x+dx*2,y+dy*2),(x-dx*1+px*2,y-dy*1+py*2),(x-dx*1-px*2,y-dy*1-py*2)],fill=A(c))

def icon_follow():
    im,d=badge(); d.rectangle([20,5,22,7],fill=A(CY2)); d.rectangle([19,8,23,13],fill=A(CY)); d.line([(19,8),(23,8)],fill=A(CY2))
    for (x,y) in ((10,18),(17,17),(24,20)): pass
    for x,y in ((5,20),(13,22),(21,20)): fig(d,x,y,PU)
    for p in ((9,17),(12,15),(15,13),(17,11)): d.rectangle([p[0],p[1],p[0]+1,p[1]+1],fill=A(CYD))
    d.rectangle([17,10,18,11],fill=A(CY)); return im
def icon_regroup():
    im,d=badge(); d.ellipse([11,11,20,20],outline=A(CY),width=1); d.rectangle([15,15,16,16],fill=A(CY2)); d.ellipse([9,9,22,22],outline=A(CYD))
    for sx,sy in ((1,1),(-1,1),(1,-1),(-1,-1)):
        cx,cy=(5 if sx>0 else 26),(5 if sy>0 else 26)
        d.line([(cx,cy),(cx+sx*5,cy+sy*5)],fill=A(PUL),width=2); arrow_head(d,cx+sx*6,cy+sy*6,sx*.7,sy*.7,CY)
    return im
def icon_build():
    im,d=badge()
    for i in range(5,27,4): d.line([(i,5),(i+1,5)],fill=A(CY)); d.line([(i,26),(i+1,26)],fill=A(CY)); d.line([(5,i),(5,i+1)],fill=A(CY)); d.line([(26,i),(26,i+1)],fill=A(CY))
    d.polygon([(16,9),(22,12),(16,15),(10,12)],fill=A(CY2)); d.polygon([(10,12),(16,15),(16,22),(10,19)],fill=A(MB)); d.polygon([(22,12),(16,15),(16,22),(22,19)],fill=A(PU))
    d.line([(16,15),(16,22)],fill=A(NAVY)); d.point((13,17),fill=A(CY)); d.point((19,17),fill=A(PUL)); return im
def icon_combat():
    im,d=badge(); sh=[(8,6),(24,6),(24,16),(16,26),(8,16)]; d.polygon(sh,fill=A(PUD)); d.line(sh+[sh[0]],fill=A(CY),width=1)
    d.line([(16,9),(16,19)],fill=A(CY2),width=2); d.line([(12,19),(20,19)],fill=A(CY)); d.line([(16,20),(16,23)],fill=A(PUL)); d.point((16,8),fill=A(CY2))
    d.line([(10,8),(10,14)],fill=A(PU)); return im
def icon_recovery():
    im,d=badge(); d.arc([6,6,25,25],start=-60,end=250,fill=A(CY),width=2); arrow_head(d,22,8,1,-.2,CY2)
    d.line([(16,11),(16,20)],fill=A(CY2),width=2); d.line([(11,15),(21,15)],fill=A(CY2),width=2); d.point((16,15),fill=A(PUL)); return im
def icon_perf():
    im,d=badge(); d.arc([5,9,26,30],start=180,end=360,fill=A(CY),width=2); d.arc([8,12,23,27],start=180,end=300,fill=A(PU),width=1)
    for a in range(180,361,45):
        r=math.radians(a); d.line([(15.5+math.cos(r)*10,19.5+math.sin(r)*10),(15.5+math.cos(r)*8,19.5+math.sin(r)*8)],fill=A(CY2))
    d.line([(15,20),(21,13)],fill=A(CY2),width=2); d.ellipse([13,18,17,22],fill=A(PU),outline=A(CY)); d.rectangle([9,24,22,25],fill=A(CYD)); d.rectangle([9,24,16,25],fill=A(CY)); return im
ICONS={"icon_smart_follow":icon_follow,"icon_regroup":icon_regroup,"icon_building_mode":icon_build,"icon_combat_assist":icon_combat,"icon_recovery":icon_recovery,"icon_performance_mode":icon_perf}

def px8(rows,pal):
    im=Image.new('RGBA',(8,8),(0,0,0,0))
    for y,r in enumerate(rows):
        for x,ch in enumerate(r):
            if ch in pal: im.putpixel((x,y),A(pal[ch]))
    return im
STATUS={
 "status_active":px8(["..kkkk..",".kccccc.","kcCCcccm","kcCcccdm","kccccddm","kcccddm.",".kddddm.","..kmmm.."][:8],{'k':K,'c':CY,'C':CY2,'d':CYD,'m':(10,50,70)}),
 "status_stuck":px8(["...kk...","..kppk..",".kpWWpk.","kpWKKWpk","kpWKKWpk",".kpWWpk.","..kppk..","...kk..."],{'k':K,'p':PU,'W':PUL,'K':K}),
 "status_separated":px8([".kkkkk..","k.....k.","k.cc..k.","k.cc..k.","k.....k.",".kkkkk..","......cc","......cc"],{'k':PU,'c':CY}),
 "status_recovering":px8(["..kkkk..",".kc..ck.","kc....ck","k..cc..k","k..cc..k","kc....ck",".kc..ck.","..kkkk.."],{'k':K,'c':CY}),
 "status_failed":px8(["k......k",".k....k.","..k..k..","...kk...","...kk...","..k..k..",".k....k.","k......k"],{'k':RED}),
 "status_idle":px8(["..kkkk..",".kmmmmk.","kmmmmmmk","kmmmmmmk","kmmmmmmk","kmmmmmmk",".kmmmmk.","..kkkk.."],{'k':K,'m':GREY}),
 "status_warning":px8(["...kk...","..kwwk..","..kwwk..",".kwwwwk.",".kwKKwk.","kwwKKwwk","kwwwwwwk",".kkkkkk."],{'k':K,'w':WARN,'K':K}),
}
def nine(w,h,fill=DB,border=CYD,corner=PU):
    im=Image.new('RGBA',(w,h),(0,0,0,0)); d=ImageDraw.Draw(im)
    d.rectangle([0,0,w-1,h-1],fill=A(K)); d.rectangle([1,1,w-2,h-2],fill=A(border))
    for y in range(2,h-2):
        t=(y-2)/max(1,h-5); c=tuple(int(fill[i]*(1-t)+NAVY[i]*t) for i in range(3)); d.line([(2,y),(w-3,y)],fill=A(c))
    d.line([(2,2),(w-3,2)],fill=A(MB)); d.line([(2,h-3),(w-3,h-3)],fill=A((20,14,44)))
    for x,y in ((2,2),(w-3,2),(2,h-3),(w-3,h-3)): im.putpixel((x,y),A(corner))
    return im
def button(state):
    cfg={"normal":(DB,CYD,PU),"hover":(MB,CY,PUL),"pressed":(NAVY,CYD,PUD),"disabled":((22,24,36),GREY,(50,50,60))}[state]
    return nine(48,16,*cfg)
def toggle(on):
    im=Image.new('RGBA',(16,8),(0,0,0,0)); d=ImageDraw.Draw(im); d.rounded_rectangle([0,0,15,7],radius=2,fill=A(K)); d.rounded_rectangle([1,1,14,6],radius=1,fill=A(CY if on else (40,44,70)))
    kx=9 if on else 2; d.rectangle([kx,2,kx+4,5],fill=A(CY2 if on else GREY)); return im
def toast(kind):
    col={"info":CYD,"success":CY,"failure":RED}[kind]; return nine(96,20,DB if kind!="failure" else (60,16,40),col,PU)
def slot(sel):
    return nine(20,20,DB if not sel else MB,CY if sel else CYD,PU)
def bar(frac,w=64):
    im=Image.new('RGBA',(w,6),(0,0,0,0)); d=ImageDraw.Draw(im); d.rectangle([0,0,w-1,5],fill=A(K)); d.rectangle([1,1,w-2,4],fill=A(NAVY)); n=int((w-2)*frac)
    if n>0: d.rectangle([1,1,n,4],fill=A(CY)); d.line([(1,1),(n,1)],fill=A(CY2))
    return im
def counter_chip(): return nine(32,12,DB,CYD,PU)
def divider():
    im=Image.new('RGBA',(64,3),(0,0,0,0)); d=ImageDraw.Draw(im); d.line([(0,1),(63,1)],fill=A(CYD)); d.line([(0,2),(63,2)],fill=A(K)); d.line([(0,0),(63,0)],fill=A(PUD,)) ; return im
def scale(im,k): return im.resize((im.width*k,im.height*k),Image.NEAREST)
def save(im,p): im.save(p)

assets={}
for n,f in ICONS.items(): assets[n]=f()
d_ic=mk("Assets/Icons_32"); d_ic2=mk("Assets/Icons_64"); d_st=mk("Assets/Status_8"); d_st2=mk("Assets/Status_32_preview"); d_h=mk("Assets/HUD")
for n,im in assets.items(): save(im,f"{d_ic}/{n}.png"); save(scale(im,2),f"{d_ic2}/{n}_64.png")
for n,im in STATUS.items(): save(im,f"{d_st}/{n}.png"); save(scale(im,4),f"{d_st2}/{n}_32.png")
hud={"hud_panel_64x40":nine(64,40),"hud_panel_header_64x14":nine(64,14,MB,CY,PUL),"hud_button_normal":button("normal"),"hud_button_hover":button("hover"),"hud_button_pressed":button("pressed"),"hud_button_disabled":button("disabled"),
     "hud_toggle_on":toggle(True),"hud_toggle_off":toggle(False),"hud_toast_info":toast("info"),"hud_toast_success":toast("success"),"hud_toast_failure":toast("failure"),
     "hud_slot":slot(False),"hud_slot_selected":slot(True),"hud_counter_chip":counter_chip(),"hud_bar_empty":bar(0),"hud_bar_half":bar(.5),"hud_bar_full":bar(1),"hud_divider":divider()}
for n,im in hud.items(): save(im,f"{d_h}/{n}.png")

# ------------------------------------------------------------ previews
def font(s,b=True): return ImageFont.truetype(FB if b else FR,s)
def canvas(title,sub):
    im=Image.new('RGBA',(960,540),A(NAVY)); d=ImageDraw.Draw(im)
    for x in range(0,960,30): d.line([(x,90),(x,540)],fill=(14,24,52,255))
    for y in range(90,540,30): d.line([(0,y),(960,y)],fill=(14,24,52,255))
    d.rectangle([0,0,959,84],fill=A((6,8,20))); d.line([(0,84),(959,84)],fill=A(CYD),width=2)
    d.text((24,12),title,font=font(30),fill=A(CY)); d.text((24,54),sub,font=font(15,False),fill=A(PUL))
    d.text((24,516),"CONCEPT DIAGRAM - design illustration, not an in-game screenshot",font=font(12,False),fill=A(GREY)); return im,d
def put(im,icon,xy,k=3): im.alpha_composite(scale(icon,k),xy)
def circ(d,c,r,fill=None,out=None,w=1): d.ellipse([c[0]-r,c[1]-r,c[0]+r,c[1]+r],fill=fill,outline=out,width=w)
def dashed_circle(d,c,r,col):
    for a in range(0,360,12): 
        a1=math.radians(a); a2=math.radians(a+6); d.line([(c[0]+math.cos(a1)*r,c[1]+math.sin(a1)*r),(c[0]+math.cos(a2)*r,c[1]+math.sin(a2)*r)],fill=col,width=2)
def label(d,xy,t,col=CY2,s=14): d.text(xy,t,font=font(s,False),fill=A(col))
def player(d,c): circ(d,c,13,A(CY),A(CY2),2); label(d,(c[0]-14,c[1]+16),"OWNER",CY2,12)
def shadow(d,c,r=9,col=PU): circ(d,c,r,A(col),A(PUL),2); d.point(c,fill=A(CY2))
def p_follow():
    im,d=canvas("1  SMART FOLLOWING","Spacing, formation slots, narrow-passage queueing"); put(im,assets["icon_smart_follow"],(860,12),3)
    P=(480,190); player(d,P); 
    for r in (60,): dashed_circle(d,P,r,A(CYD))
    label(d,(560,150),"min follow distance",CYD,12)
    # formation arc behind player (player faces up)
    for i,a in enumerate(range(-60,61,30)): 
        c=(P[0]+math.sin(math.radians(a))*120,P[1]+math.cos(math.radians(a))*120+10); shadow(d,c)
    big=(480,420); circ(d,big,34,A(PUD),A(PUL),3); dashed_circle(d,big,52,A(CYD)); label(d,(430,462),"LARGE: spacing = body width + margin",PUL,12)
    # corridor
    d.rectangle([40,150,300,170],fill=A((30,40,80))); d.rectangle([40,230,300,250],fill=A((30,40,80))); label(d,(60,255),"narrow passage: single-file queue, no clumping",CY2,12)
    for x in (90,150,210,270): shadow(d,(x,200),7)
    d.text((40,300),"Rules (defaults in Docs):\n- follow ring beyond clearance radius\n- slots come from the EXISTING formation\n- bigger bodies get larger slot spacing\n- idle owner => companions settle, no jitter",font=font(14,False),fill=A(CY2),spacing=6)
    return im
def p_recovery():
    im,d=canvas("2  ANTI-STUCK RECOVERY","Pathfind -> regroup -> (optional) validated teleport"); put(im,assets["icon_recovery"],(860,12),3)
    d.rectangle([380,150,420,470],fill=A((40,44,90))); label(d,(366,476),"obstacle",GREY,12)
    S=(330,310); shadow(d,S); label(d,(290,330),"STUCK",PUL,12); P=(560,200); player(d,P)
    d.line([S,(350,300),(352,260)],fill=A(CYD),width=2); label(d,(120,140),"1. normal path retry (bounded)",CY2,13)
    for a in range(0,360,45):
        pass
    G=(520,330); circ(d,G,9,None,A(CY),2); label(d,(470,350),"2. safe regroup spot",CY,13); d.line([S,G],fill=A(PU),width=2)
    T=(600,250); circ(d,T,9,None,A(WARN),2); label(d,(590,268),"3. teleport target (checked)",WARN,13)
    d.text((40,380),"Safe-position checks:\n[x] 2 blocks of headroom  [x] solid floor\n[x] no lava / fire / cactus / void\n[x] not inside walls  [x] same dimension",font=font(14,False),fill=A(CY2),spacing=6)
    d.text((640,380),"If every step fails:\n -> status = FAILED\n -> owner notification\n -> retry cooldown (no spam)",font=font(14,False),fill=A(RED),spacing=6)
    return im
def p_build():
    im,d=canvas("3  SMART BUILDING MODE","Keep the working area clear, restore formation when toggled off"); put(im,assets["icon_building_mode"],(860,12),3)
    P=(480,300); player(d,P); d.rectangle([380,200,580,380],outline=A(CY),width=2); label(d,(386,180),"WORK ZONE (config radius + look-target)",CY,12)
    d.rectangle([520,230,550,260],fill=A(MB),outline=A(CY2)); label(d,(500,205),"target block",CY2,11)
    for c in ((330,150),(480,130),(640,150),(330,330),(640,420),(420,440),(560,450)): shadow(d,c,8)
    d.line([(330,150),(380,200)],fill=A(PUD),width=1); label(d,(40,120),"Shadows hold at the zone edge",PUL,13)
    d.text((40,400),"Toggle OFF -> companions walk back\nto their normal formation slots.\nExplicit commands still win.",font=font(14,False),fill=A(CY2),spacing=6)
    return im
def p_combat():
    im,d=canvas("4  INTELLIGENT COMBAT ASSISTANCE","Respect commands, spread attackers, keep sight lines open"); put(im,assets["icon_combat_assist"],(860,12),3)
    P=(230,300); player(d,P); E=(660,260); circ(d,E,22,A((120,30,60)),A(RED),3); label(d,(630,292),"TARGET",RED,12)
    d.polygon([P,(E[0]-30,E[1]-110),(E[0]-30,E[1]+110)],fill=(40,80,140,60)); label(d,(330,170),"owner line of sight stays clear",CYD,12)
    for a in range(0,360,60): 
        c=(E[0]+math.cos(math.radians(a))*70,E[1]+math.sin(math.radians(a))*70)
        if a in (180,120,240): c=(E[0]+math.cos(math.radians(a))*70,E[1]+math.sin(math.radians(a))*70*1.9)
        shadow(d,c,8); d.line([c,E],fill=A(PUD),width=1)
    d.text((40,420),"- explicit attack/target command always wins\n- sticky target: switch only with hysteresis\n- attack slots limit crowding on one enemy\n- threat to owner raises priority",font=font(14,False),fill=A(CY2),spacing=6)
    return im
def p_perf():
    im,d=canvas("5  PERFORMANCE OPTIMIZATION","Tiered, bounded update intervals and a per-tick budget"); put(im,assets["icon_performance_mode"],(860,12),3)
    tiers=[("COMBAT / RECOVERY",2,CY),("FOLLOWING (moving)",5,CYD),("IDLE NEAR OWNER",20,PU),("FAR / UNLOADED-ADJACENT",40,PUD)]
    for i,(n,t,c) in enumerate(tiers):
        y=130+i*70; d.text((40,y),n,font=font(15),fill=A(CY2)); d.rectangle([40,y+28,40+t*14,y+48],fill=A(c),outline=A(CY2)); d.text((60+t*14,y+28),f"every ~{t} ticks (default)",font=font(13,False),fill=A(CY2))
    d.text((40,430),"Per-tick budget: at most N companion 'smart' updates and M new path requests per server tick,\nround-robin so nobody starves. Vanilla/mod entity tick is NEVER skipped.",font=font(14,False),fill=A(CY2),spacing=6)
    return im
def p_hud():
    im,d=canvas("6  QUICK RECOVERY & STATUS","Counts, mode, one-click regroup, failure notices"); put(im,assets["icon_regroup"],(860,12),3)
    pan=scale(hud["hud_panel_64x40"].resize((92,56),Image.NEAREST),1); 
    W,H=300,160; panel=nine(W//3,H//3,DB,CYD,PU); panel=scale(panel,3); im.alpha_composite(panel,(60,130))
    d.text((84,142),"SHADOW ARMY",font=font(16),fill=A(CY)); 
    for i,(ic,t) in enumerate((("status_active","Active  24"),("status_stuck","Stuck    2"),("status_separated","Separated 1"))):
        put(im,STATUS[ic],(84,176+i*26),3); d.text((118,178+i*26),t,font=font(15,False),fill=A(CY2))
    put(im,assets["icon_smart_follow"],(84,262),1); d.text((122,268),"Mode: FOLLOW",font=font(14,False),fill=A(PUL))
    b=scale(hud["hud_button_hover"],3); im.alpha_composite(b,(520,140)); d.text((560,152),"REGROUP",font=font(18),fill=A(CY2))
    im.alpha_composite(scale(hud["hud_toast_failure"],4),(430,280)); put(im,STATUS["status_failed"],(444,296),3); d.text((480,296),"Recovery failed for 2 shadows",font=font(14,False),fill=A((255,200,210)))
    im.alpha_composite(scale(hud["hud_toast_success"],4),(430,370)); put(im,STATUS["status_active"],(444,386),3); d.text((480,386),"Army regrouped (22 / 24)",font=font(14,False),fill=A(CY2))
    for i,n in enumerate(("icon_smart_follow","icon_building_mode","icon_combat_assist","icon_performance_mode")):
        im.alpha_composite(scale(hud["hud_slot_selected" if i==0 else "hud_slot"],3),(60+i*70,330)); put(im,assets[n].resize((16,16),Image.NEAREST),(60+i*70+6,336),3)
    label(d,(60,400),"mode quick-select slots (layout is a suggestion)",GREY,12); return im
prev=mk("Previews")
pv=[("01_smart_following",p_follow),("02_anti_stuck_recovery",p_recovery),("03_building_mode",p_build),("04_combat_assist",p_combat),("05_performance",p_perf),("06_hud_status",p_hud)]
ims=[]
for n,f in pv: im=f(); im.convert("RGB").save(f"{prev}/{n}.png"); ims.append(im)
sheet=Image.new('RGB',(960*2,540*3),(0,0,0))
for i,im in enumerate(ims): sheet.paste(im.convert("RGB"),((i%2)*960,(i//2)*540))
sheet.resize((1280,1080),Image.LANCZOS).save(f"{prev}/00_overview_all_features.png")
# icon sheet
sh=Image.new('RGBA',(6*140+20,200),A(NAVY)); dd=ImageDraw.Draw(sh)
for i,(n,im) in enumerate(assets.items()): sh.alpha_composite(scale(im,3),(14+i*140,14)); dd.text((14+i*140,114),n.replace("icon_",""),font=font(11,False),fill=A(CY2))
for i,(n,im) in enumerate(STATUS.items()): sh.alpha_composite(scale(im,4),(14+i*80,150)) if i<7 else None
sh.convert("RGB").save(f"{prev}/07_icon_and_status_sheet.png")
print("assets done")
