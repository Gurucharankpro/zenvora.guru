from lib import *
import shutil

LANG = {}
RES = f"Resources/assets/{MOD}"
PRE = []   # (label, image)


def has_glow(g): return g.getbbox() is not None


def put_tex(name, img, gl, folder="block"):
    SAVE(img, f"{RES}/textures/{folder}/{name}.png")
    if gl is not None and has_glow(gl): SAVE(gl, f"{RES}/textures/{folder}/{name}_e.png")


def finish(name, title, elems, img, gl, tw, parent="minecraft:block/block", state=None, item_parent=None, order=0):
    """write block model, blockstate, item model, bbmodel, preview for a custom-model block"""
    LANG[f"block.{MOD}.{name}"] = title
    put_tex(name, img, gl)
    W(f"{RES}/models/block/{name}.json", java_model(parent, f"{MOD}:block/{name}", elems, tw, None))
    W(f"{RES}/blockstates/{name}.json", state or {"variants": {"": {"model": f"{MOD}:block/{name}"}}})
    W(f"{RES}/models/item/{name}.json", {"parent": item_parent or f"{MOD}:block/{name}"})
    folder = f"Blocks_And_Decor/{order:02d}_{name}"
    W(f"{folder}/{name}.bbmodel", bbmodel(name, None, elems, img, tw, tw, java=True, texname=name))
    SAVE(img, f"{folder}/{name}.png")
    if gl is not None and has_glow(gl): SAVE(gl, f"{folder}/{name}_e.png")
    pv = render(elems, img, gl, yaw=-35, pitch=25, size=512)
    SAVE(pv, f"{folder}/{name}_preview.png"); PRE.append((title, pv))


def cube_block(name, title, painter, seed, order):
    img, gl = PAINT[painter](random.Random(seed))
    el = [mk([0, 0, 0], [16, 16, 16], (0, 0), name="cube")]
    LANG[f"block.{MOD}.{name}"] = title
    put_tex(name, img, gl)
    W(f"{RES}/models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{MOD}:block/{name}"}})
    W(f"{RES}/blockstates/{name}.json", {"variants": {"": {"model": f"{MOD}:block/{name}"}}})
    W(f"{RES}/models/item/{name}.json", {"parent": f"{MOD}:block/{name}"})
    folder = f"Blocks_And_Decor/{order:02d}_{name}"
    W(f"{folder}/{name}.bbmodel", bbmodel(name, None, el, img, 16, 16, java=True, texname=name))
    SAVE(img, f"{folder}/{name}.png")
    if has_glow(gl): SAVE(gl, f"{folder}/{name}_e.png")
    pv = render(el, img, gl, yaw=-35, pitch=25, size=512)
    SAVE(pv, f"{folder}/{name}_preview.png"); PRE.append((title, pv))


def E(f, t, pos, rot=None, pv=None, over=None, only=None, ns=False):
    e = mk(f, t, pos, rot, pv, over=over)
    if only: e["only"] = only
    if ns: e["noshade"] = True
    return e


def build():
    cube_block("frostgrave_stone", "Frostgrave Stone", "stone_dark", 101, 1)
    cube_block("polished_frostgrave_stone", "Polished Frostgrave Stone", "polished", 102, 2)
    cube_block("cracked_icebound_bricks", "Cracked Icebound Bricks", "bricks", 103, 3)
    cube_block("royal_frost_marble", "Royal Frost Marble", "marble", 104, 4)
    cube_block("glowing_blue_ice_crystal", "Glowing Blue Ice Crystal", "crystal", 105, 5)

    # 6 cluster ------------------------------------------------------------
    img, gl = PAINT["crystal"](random.Random(106)); P0 = (0, 0)

    def crystal(cx, cz, w, h, ax, ang, light=False):
        out = []; y = 0; piv = [cx, 0, cz]
        for k, (wf, hf) in enumerate(((1, .42), (.78, .28), (.52, .2), (.28, .1))):
            ww = w * wf; hh = h * hf
            rot = None
            if ax is not None:
                rot = [0, 0, 0]; rot[ax] = ang
            out.append(E([cx - ww / 2, y, cz - ww / 2], [cx + ww / 2, y + hh, cz + ww / 2], P0, rot, piv if rot else None))
            y += hh
        return out
    el = []
    for a in ((8, 8, 5, 12.5, None, 0), (4, 9, 4, 8, 2, -18), (12, 7, 4, 9.5, 2, 16), (7, 3.5, 4, 7.5, 0, -20),
              (9, 12.5, 3.5, 6.5, 0, 22), (3, 4, 3, 4.5, 2, -30), (13, 13, 3, 5.5, 2, 28)): el += crystal(*a)
    state = {"variants": {"facing=up": {"model": f"{MOD}:block/frozen_crystal_cluster"},
                          "facing=down": {"model": f"{MOD}:block/frozen_crystal_cluster", "x": 180},
                          "facing=north": {"model": f"{MOD}:block/frozen_crystal_cluster", "x": 90},
                          "facing=east": {"model": f"{MOD}:block/frozen_crystal_cluster", "x": 90, "y": 90},
                          "facing=south": {"model": f"{MOD}:block/frozen_crystal_cluster", "x": 90, "y": 180},
                          "facing=west": {"model": f"{MOD}:block/frozen_crystal_cluster", "x": 90, "y": 270}}}
    finish("frozen_crystal_cluster", "Frozen Crystal Cluster", el, img, gl, 16, state=state, order=6)

    # 7 chain column ---------------------------------------------------------
    img, gl, pos, _ = atlas(["polished", "chain", "silver", "rune"], 32, 107)
    p, ch, sv, ru = pos["polished"], pos["chain"], pos["silver"], pos["rune"]
    el = [E([5, 0, 5], [11, 16, 11], p), E([3, 0, 3], [13, 2, 13], sv), E([3.5, 2, 3.5], [12.5, 3, 12.5], sv),
          E([3, 14, 3], [13, 16, 13], sv), E([3.5, 13, 3.5], [12.5, 14, 12.5], sv),
          E([4.6, 3, 4.6], [11.4, 4.5, 11.4], ch), E([4.6, 7.5, 4.6], [11.4, 9, 11.4], ch), E([4.6, 11, 4.6], [11.4, 12.5, 11.4], ch)]
    for (a, b) in (([6.5, 3, 4.4], [9.5, 13, 5]), ([6.5, 3, 11], [9.5, 13, 11.6]), ([4.4, 3, 6.5], [5, 13, 9.5]), ([11, 3, 6.5], [11.6, 13, 9.5])):
        el.append(E(a, b, ch))
    el.append(E([7, 4.5, 4.3], [9, 7.5, 4.6], ru)); el.append(E([7, 9, 4.3], [9, 11, 4.6], ru))
    finish("silver_chain_column", "Silver Chain Column", el, img, gl, 32, order=7)

    # 8 vines ------------------------------------------------------------
    r = random.Random(108); img = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); gl = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); q = img.load(); g = gl.load()
    for x0 in (2, 6, 10, 13):
        x = x0
        for y in range(r.randint(10, 16)):
            q[x % 16, y] = (160, 205, 235, 255); x += r.choice([-1, 0, 0, 1]) if y % 3 == 0 else 0
            if y % 4 == 2:
                for dx in (-1, 1): q[(x + dx) % 16, y] = (205, 235, 250, 255)
        q[x % 16, y] = g[x % 16, y] = (140, 240, 255, 255)
    for _ in range(6): q[r.randint(0, 15), r.randint(0, 12)] = (230, 250, 255, 255)
    side = {"from": [0, 0, 0.8], "to": [16, 16, 0.8], "rot": None, "pivot": None, "name": "side", "only": ["north", "south"], "noshade": True,
            "faces": {d: [0, 0, 16, 16] for d in DIRS}}
    up = {"from": [0, 15.2, 0], "to": [16, 15.2, 16], "rot": None, "pivot": None, "name": "up", "only": ["up", "down"], "noshade": True,
          "faces": {d: [0, 0, 16, 16] for d in DIRS}}
    LANG[f"block.{MOD}.winterveil_vines"] = "Winterveil Vines"
    put_tex("winterveil_vines", img, gl)
    for nm, e in (("winterveil_vines_side", side), ("winterveil_vines_up", up)):
        W(f"{RES}/models/block/{nm}.json", {"parent": "minecraft:block/block", "textures": {"0": f"{MOD}:block/winterveil_vines", "particle": f"{MOD}:block/winterveil_vines"},
                                            "elements": [{"from": e["from"], "to": e["to"], "shade": False, "faces": {d: {"uv": [0, 0, 16, 16], "texture": "#0"} for d in e["only"]}}]})
    mm = lambda n, **k: {"apply": {"model": f"{MOD}:block/{n}", **k}}
    W(f"{RES}/blockstates/winterveil_vines.json", {"multipart": [
        {"when": {"up": "true"}, **mm("winterveil_vines_up")},
        {"when": {"south": "true"}, **mm("winterveil_vines_side")},
        {"when": {"west": "true"}, **mm("winterveil_vines_side", y=90)},
        {"when": {"north": "true"}, **mm("winterveil_vines_side", y=180)},
        {"when": {"east": "true"}, **mm("winterveil_vines_side", y=270)}]})
    W(f"{RES}/models/item/winterveil_vines.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{MOD}:block/winterveil_vines"}})
    folder = "Blocks_And_Decor/08_winterveil_vines"
    pe = [dict(side, rot=[0, 0, 0], pivot=[8, 8, 8]), ]
    def vine_el(e, rot=None, pv=None):
        d = dict(e); d["rot"] = rot; d["pivot"] = pv; return d
    sc = [vine_el(side)] + [vine_el(side, [0, 90, 0], [8, 8, 8]), vine_el(side, [0, 180, 0], [8, 8, 8]), vine_el(side, [0, 270, 0], [8, 8, 8])]
    W(f"{folder}/winterveil_vines.bbmodel", bbmodel("winterveil_vines", None, [vine_el(side), vine_el(up)], img, 16, 16, java=True, texname="winterveil_vines"))
    SAVE(img, f"{folder}/winterveil_vines.png"); SAVE(gl, f"{folder}/winterveil_vines_e.png")
    pv = render(sc, img, gl, yaw=-35, pitch=22, size=512); SAVE(pv, f"{folder}/winterveil_vines_preview.png"); PRE.append(("Winterveil Vines", pv))

    # 9 flower ------------------------------------------------------------
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); gl = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(img); dg = ImageDraw.Draw(gl)
    d.line([(8, 15), (8, 7)], fill=(70, 130, 150, 255)); d.line([(8, 12), (11, 10)], fill=(70, 130, 150, 255)); d.line([(8, 13), (5, 11)], fill=(70, 130, 150, 255))
    d.point([(11, 9), (5, 10)], fill=(130, 215, 235, 255))
    for (x, y) in ((8, 3), (8, 7), (5, 5), (11, 5), (6, 3), (10, 3), (6, 7), (10, 7)):
        for dd in (d, dg): dd.point([(x, y)], fill=(180, 240, 255, 255))
    for (x, y) in ((8, 4), (8, 6), (7, 5), (9, 5)):
        for dd in (d, dg): dd.point([(x, y)], fill=(120, 225, 255, 255))
    for dd in (d, dg): dd.point([(8, 5)], fill=(255, 255, 255, 255))
    LANG[f"block.{MOD}.ghostfrost_flower"] = "Ghostfrost Flower"
    put_tex("ghostfrost_flower", img, gl)
    cross = lambda ang: {"from": [0.8, 0, 8], "to": [15.2, 16, 8], "rot": [0, ang, 0], "pivot": [8, 8, 8], "name": "c", "only": ["north", "south"], "noshade": True, "rescale": True,
                         "faces": {dd_: [0, 0, 16, 16] for dd_ in DIRS}}
    W(f"{RES}/models/block/ghostfrost_flower.json", {"parent": "minecraft:block/cross", "textures": {"cross": f"{MOD}:block/ghostfrost_flower"}})
    W(f"{RES}/blockstates/ghostfrost_flower.json", {"variants": {"": {"model": f"{MOD}:block/ghostfrost_flower"}}})
    W(f"{RES}/models/item/ghostfrost_flower.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{MOD}:block/ghostfrost_flower"}})
    folder = "Blocks_And_Decor/09_ghostfrost_flower"
    W(f"{folder}/ghostfrost_flower.bbmodel", bbmodel("ghostfrost_flower", None, [cross(45), cross(-45)], img, 16, 16, java=True, texname="ghostfrost_flower"))
    SAVE(img, f"{folder}/ghostfrost_flower.png"); SAVE(gl, f"{folder}/ghostfrost_flower_e.png")
    pv = render([cross(45), cross(-45)], img, gl, yaw=-30, pitch=15, size=512); SAVE(pv, f"{folder}/ghostfrost_flower_preview.png"); PRE.append(("Ghostfrost Flower", pv))

    # 10 lantern --------------------------------------------------------------
    img, gl, pos, _ = atlas(["metal_dark", "glass", "flame", "chain"], 32, 110)
    fr, gla, fl, chn = pos["metal_dark"], pos["glass"], pos["flame"], pos["chain"]

    def lantern(dy, hang):
        el = [E([5, dy, 5], [11, dy + 2, 11], fr), E([6, dy + 2, 6], [10, dy + 9, 10], fl),
              E([4.8, dy + 4.2, 4.8], [11.2, dy + 5, 11.2], fr), E([4.8, dy + 7, 4.8], [11.2, dy + 7.8, 11.2], fr),
              E([4.5, dy + 9, 4.5], [11.5, dy + 10, 11.5], gla), E([6, dy + 10, 6], [10, dy + 12, 10], fr), E([7, dy + 12, 7], [9, dy + 13, 9], fr)]
        for (x, z) in ((5, 5), (10, 5), (5, 10), (10, 10)): el.append(E([x, dy + 2, z], [x + 1, dy + 9, z + 1], fr))
        if hang: el.append(E([7.5, dy + 13, 7.5], [8.5, 16, 8.5], chn))
        return el
    LANG[f"block.{MOD}.royal_ice_lantern"] = "Royal Ice Lantern"
    put_tex("royal_ice_lantern", img, gl)
    W(f"{RES}/models/block/royal_ice_lantern.json", java_model("minecraft:block/block", f"{MOD}:block/royal_ice_lantern", lantern(0, False), 32))
    W(f"{RES}/models/block/royal_ice_lantern_hanging.json", java_model("minecraft:block/block", f"{MOD}:block/royal_ice_lantern", lantern(1, True), 32))
    W(f"{RES}/blockstates/royal_ice_lantern.json", {"variants": {"hanging=false": {"model": f"{MOD}:block/royal_ice_lantern"}, "hanging=true": {"model": f"{MOD}:block/royal_ice_lantern_hanging"}}})
    W(f"{RES}/models/item/royal_ice_lantern.json", {"parent": f"{MOD}:block/royal_ice_lantern"})
    folder = "Blocks_And_Decor/10_royal_ice_lantern"; el = lantern(0, False)
    W(f"{folder}/royal_ice_lantern.bbmodel", bbmodel("royal_ice_lantern", None, el, img, 32, 32, java=True, texname="royal_ice_lantern"))
    SAVE(img, f"{folder}/royal_ice_lantern.png"); SAVE(gl, f"{folder}/royal_ice_lantern_e.png")
    pv = render(el, img, gl, yaw=-35, pitch=22, size=512); SAVE(pv, f"{folder}/royal_ice_lantern_preview.png"); PRE.append(("Royal Ice Lantern", pv))

    # 11 statue ---------------------------------------------------------------
    img, gl, pos, _ = atlas(["polished", "statue_ice", "silver", "rune"], 32, 111)
    st, ic, sv, ru = pos["polished"], pos["statue_ice"], pos["silver"], pos["rune"]
    el = [E([3, 0, 3], [13, 2, 13], st), E([4, 2, 4], [12, 3, 12], ru), E([5.5, 3, 6.5], [7.7, 8, 9.5], ic), E([8.3, 3, 6.5], [10.5, 8, 9.5], ic),
          E([5, 8, 6], [11, 13, 10], ic), E([3.5, 11.5, 5.8], [5.5, 13.5, 10.2], sv), E([10.5, 11.5, 5.8], [12.5, 13.5, 10.2], sv),
          E([4, 8, 5.5], [5.5, 11.8, 9], ic), E([10.5, 8, 5.5], [12, 11.8, 9], ic), E([6, 12.5, 6.5], [10, 15.5, 9.5], sv),
          E([6.5, 15.5, 7.5], [7.5, 16, 8.5], sv), E([8.5, 15.5, 7.5], [9.5, 16, 8.5], sv), E([7.2, 15.5, 7.5], [8.8, 16, 8.5], ic),
          E([7.6, 3, 4.6], [8.4, 11, 5.4], sv), E([6.5, 10, 4.4], [9.5, 10.8, 5.6], sv), E([7.1, 11, 4.9], [8.9, 11.8, 5.1], st),
          E([5, 7, 5.6], [11, 8, 10.4], st)]
    state = {"variants": {f"facing={f}": {"model": f"{MOD}:block/frozen_memorial_statue", **({"y": y} if y else {})} for f, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270))}}
    finish("frozen_memorial_statue", "Frozen Memorial Statue", el, img, gl, 32, state=state, order=11)

    # 12 chest -----------------------------------------------------------------
    img, gl, pos, _ = atlas(["wood", "chest_front", "chest_top", "silver", "snow_silver", "metal_dark", "glass", "stone_dark"], 64, 112, fillrest=False)
    wd, cf, ct, sv, ss, md, gz = pos["wood"], pos["chest_front"], pos["chest_top"], pos["silver"], pos["snow_silver"], pos["metal_dark"], pos["glass"]
    el = [E([1, 0, 1], [15, 10, 15], wd, over={"north": cf, "up": md}), E([1, 10, 1], [15, 14, 15], wd, over={"up": ct, "north": wd}),
          E([0.8, 0, 0.8], [2.2, 14.2, 2.2], sv), E([13.8, 0, 0.8], [15.2, 14.2, 2.2], sv), E([0.8, 0, 13.8], [2.2, 14.2, 15.2], sv), E([13.8, 0, 13.8], [15.2, 14.2, 15.2], sv),
          E([0.7, 9.4, 0.7], [15.3, 10.6, 15.3], sv), E([6.8, 7.5, 0.3], [9.2, 12.5, 1.1], ss), E([7.5, 9.2, 0.1], [8.5, 10.4, 0.4], gz),
          E([2.5, 14, 2.5], [13.5, 14.4, 13.5], ss, over={"up": ss})]
    state = {"variants": {f"facing={f}": {"model": f"{MOD}:block/winter_royal_chest", **({"y": y} if y else {})} for f, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270))}}
    finish("winter_royal_chest", "Winter Royal Chest", el, img, gl, 64, state=state, order=12)


def loot_tables():
    names = [k.split(".")[-1] for k in LANG if k.startswith("block.")]
    for n in names:
        W(f"Resources/data/{MOD}/loot_tables/blocks/{n}.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "bonus_rolls": 0,
            "entries": [{"type": "minecraft:item", "name": f"{MOD}:{n}"}], "conditions": [{"condition": "minecraft:survives_explosion"}]}]})
    # dungeon vault chest (trophy handled by boss logic, not by this table)
    W(f"Resources/data/{MOD}/loot_tables/chests/winter_vault.json", {"type": "minecraft:chest", "pools": [
        {"rolls": {"min": 3, "max": 5}, "entries": [
            {"type": "minecraft:item", "weight": 8, "name": "minecraft:iron_ingot", "functions": [{"function": "minecraft:set_count", "count": {"min": 2, "max": 6}}]},
            {"type": "minecraft:item", "weight": 6, "name": "minecraft:diamond", "functions": [{"function": "minecraft:set_count", "count": {"min": 1, "max": 2}}]},
            {"type": "minecraft:item", "weight": 5, "name": "minecraft:blue_ice", "functions": [{"function": "minecraft:set_count", "count": {"min": 2, "max": 5}}]},
            {"type": "minecraft:item", "weight": 4, "name": "minecraft:golden_apple"},
            {"type": "minecraft:item", "weight": 3, "name": f"{MOD}:glowing_blue_ice_crystal", "functions": [{"function": "minecraft:set_count", "count": {"min": 1, "max": 3}}]},
            {"type": "minecraft:item", "weight": 3, "name": f"{MOD}:ghostfrost_flower", "functions": [{"function": "minecraft:set_count", "count": {"min": 1, "max": 3}}]},
            {"type": "minecraft:item", "weight": 2, "name": "minecraft:enchanted_book", "functions": [{"function": "minecraft:enchant_randomly"}]}]}]})


if __name__ == "__main__":
    build(); loot_tables()
    sheet([p for _, p in PRE], [t for t, _ in PRE], "Blocks_And_Decor/ALL_12_BLOCKS_PREVIEW.png", cols=4, cell=300)
    print(len(LANG), "blocks;", sorted(LANG.values()))
    json.dump(LANG, open("/home/claude/gen/lang_blocks.json", "w"))
