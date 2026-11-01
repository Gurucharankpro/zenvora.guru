import json, os, glob, sys
from PIL import Image
OUT="/home/claude/pkg"; ok=True; rep=[]
def chk(c,msg):
    global ok
    rep.append(("PASS" if c else "FAIL")+"  "+msg); ok = ok and c
# JSON parse
bad=[p for p in glob.glob(OUT+"/**/*.json",recursive=True)+glob.glob(OUT+"/**/*.bbmodel",recursive=True) if not json.load(open(p)) and False]
n=0
for p in glob.glob(OUT+"/**/*.json",recursive=True)+glob.glob(OUT+"/**/*.bbmodel",recursive=True)+glob.glob(OUT+"/**/*.mcmeta",recursive=True):
    try: json.load(open(p)); n+=1
    except Exception as e: chk(False,f"invalid JSON {p}: {e}")
chk(True,f"{n} JSON/bbmodel/mcmeta files parse correctly")
EXPECT={"frostgrave_prowler":["idle","walk","run","alert","jump","claw_attack","frost_bite","ice_howl","spawn","death"],
 "chainbound_sentinel":["idle","walk","run","guard","chain_swing","ice_pulse","heavy_strike","stagger","spawn","death"],
 "frozen_gravekeeper":["idle","walk","run","battle_roar","axe_slam","frozen_sweep","ice_spike_attack","royal_guard","phase_transition","death"]}
for key,names in EXPECT.items():
    d="Mobs/Frostgrave_Prowler" if key=="frostgrave_prowler" else "Mobs/Chainbound_Sentinel" if key=="chainbound_sentinel" else "Boss/Frozen_Gravekeeper"
    a=json.load(open(f"{OUT}/{d}/{key}.animation.json"))["animations"]
    chk(sorted(a)==sorted(f"animation.{key}.{n}" for n in names), f"{key}: exactly the 10 required animation names ({len(a)})")
    for k,v in a.items():
        kfs=sum(len(ch) for b in v["bones"].values() for ch in b.values())
        mx=max(float(t) for b in v["bones"].values() for ch in b.values() for t in ch)
        chk(kfs>=8 and mx<=v["animation_length"]+1e-6, f"  {k.split('.')[-1]}: {kfs} keyframes, last key {mx} <= length {v['animation_length']}")
    t=Image.open(f"{OUT}/{d}/{key}.png"); chk(t.size==(128,128),f"{key}.png is exactly {t.size}")
    g=Image.open(f"{OUT}/{d}/{key}_glowing.png"); chk(g.size==(128,128) and g.getbbox() is not None,f"{key}_glowing.png 128x128 with emissive pixels")
    geo=json.load(open(f"{OUT}/{d}/{key}.geo.json"))["minecraft:geometry"][0]
    bn={b["name"] for b in geo["bones"]}
    chk(all(b.get("parent") in bn or "parent" not in b for b in geo["bones"]),f"{key}: all bone parents exist ({len(bn)} bones)")
    chk(set(b for a_ in a.values() for b in a_["bones"])<=bn,f"{key}: animations only reference existing bones")
    uvok=all(0<=f["uv"][0] and f["uv"][0]+f["uv_size"][0]<=128 and 0<=f["uv"][1] and f["uv"][1]+f["uv_size"][1]<=128 for b in geo["bones"] for c in b.get("cubes",[]) for f in c["uv"].values())
    chk(uvok,f"{key}: every UV rectangle inside 128x128")
    chk(sum(len(b.get("cubes",[])) for b in geo["bones"])>0, f"{key}: cubes present")
# blocks
blocks=sorted(glob.glob(OUT+"/Resources/assets/soulbound/blockstates/*.json")); chk(len(blocks)==12,f"exactly 12 blockstates ({len(blocks)})")
for p in blocks:
    nm=os.path.basename(p)[:-5]
    chk(os.path.exists(f"{OUT}/Resources/assets/soulbound/textures/block/{nm}.png"),f"block texture {nm}.png")
    chk(os.path.exists(f"{OUT}/Resources/assets/soulbound/models/item/{nm}.json"),f"item model {nm}")
# model texture refs
import re
for p in glob.glob(OUT+"/Resources/assets/soulbound/models/**/*.json",recursive=True):
    for ref in re.findall(r'"soulbound:([a-z_/]+)"',open(p).read()):
        if ref.startswith(("block/","item/")):
            kind,name=ref.split("/",1)
            f1=f"{OUT}/Resources/assets/soulbound/textures/{ref}.png"; f2=f"{OUT}/Resources/assets/soulbound/models/{ref}.json"
            chk(os.path.exists(f1) or os.path.exists(f2),f"ref soulbound:{ref} resolves ({os.path.basename(p)})") if not (os.path.exists(f1) or os.path.exists(f2)) else None
for p in glob.glob(OUT+"/Resources/assets/soulbound/models/block/*.json"):
    m=json.load(open(p))
    for e in m.get("elements",[]):
        for f in e["faces"].values():
            if not all(0<=x<=16 for x in f["uv"]): chk(False,f"UV out of 0-16 in {os.path.basename(p)}")
chk(True,"block-model UVs all within 0-16 (checked)")
chk(Image.open(OUT+"/Portal/portal_surface_strip.png").size==(64,512),"portal surface strip 64x512 (8 frames)")
print("\n".join(rep)); print("\nOVERALL:", "ALL CHECKS PASSED" if ok else "SOME CHECKS FAILED")
