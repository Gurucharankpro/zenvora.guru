from lib import *
from build_creatures import cyc, S, an, kf, Z
import build_blocks as BB

NP = ["black_ice", "stone_dark", "polished", "marble", "silver", "chain", "crystal", "crystal_light", "rune", "frost", "eye"]
RES = f"Resources/assets/{MOD}"


# ----------------------------------------------------------------------------- portal surface frames
def portal_frames(n=8, SZ=64):
    rng = random.Random(7); flakes = [(rng.randrange(SZ), rng.randrange(SZ), rng.choice([1, 2]), rng.choice([1, 2])) for _ in range(46)]
    frames = []
    for f in range(n):
        im = Image.new("RGBA", (SZ, SZ)); p = im.load(); ph = 2 * math.pi * f / n
        for y in range(SZ):
            for x in range(SZ):
                dx, dy = (x - SZ / 2) / (SZ / 2), (y - SZ / 2) / (SZ / 2)
                r = math.hypot(dx, dy); th = math.atan2(dy, dx)
                v = 0.5 + 0.3 * math.sin(3 * th + 7 * r - ph) + 0.2 * math.sin(2 * th - 11 * r + 2 * ph)
                v += 0.22 * (1 - min(1, r)) + 0.08 * math.sin(2 * math.pi * (x * 3 / SZ) + ph * 2 + y * 0.12)
                v = max(0, min(1, v))
                if v < .5: c = (10 + 60 * v, 40 + 150 * v, 90 + 120 * v)
                else: c = (40 + 330 * (v - .5), 170 + 160 * (v - .5), 235 + 40 * (v - .5))
                edge = min(1, (1 - max(abs(dx), abs(dy))) * 5 + .35)
                p[x, y] = (cl(c[0]), cl(c[1]), cl(c[2]), int(225 * edge))
        for (x0, y0, kx, ky) in flakes:
            x, y = (x0 + 8 * f * kx) % SZ, (y0 + 8 * f * ky) % SZ
            for dx_, dy_ in ((0, 0), (1, 0), (0, 1), (1, 1)) if kx == 2 else ((0, 0),):
                p[(x + dx_) % SZ, (y + dy_) % SZ] = (245, 252, 255, 255)
        frames.append(im)
    return frames


def strip(frames, SZ=64):
    s = Image.new("RGBA", (SZ, SZ * len(frames)))
    for i, fr in enumerate(frames): s.paste(fr, (0, i * SZ))
    return s


def burst_frames(n=8, SZ=64):
    out = []
    for f in range(n):
        im = Image.new("RGBA", (SZ, SZ)); p = im.load(); R = 4 + f * 4.2
        for y in range(SZ):
            for x in range(SZ):
                d = math.hypot(x - 31.5, y - 31.5); a = max(0, 1 - abs(d - R) / 3.2) * (1 - f / n * .85)
                if a > 0: p[x, y] = (cl(120 + 135 * a), 240, 255, int(255 * a))
        for k in range(12):
            a = k * math.pi / 6 + f * .1; rr = R * (.55 + .06 * f)
            x, y = int(31.5 + rr * math.cos(a)), int(31.5 + rr * math.sin(a))
            if 0 <= x < SZ and 0 <= y < SZ: p[x, y] = (255, 255, 255, int(255 * (1 - f / n)))
        out.append(im)
    return out


def vortex_frames(n=8, SZ=64):
    out = []
    for f in range(n):
        im = Image.new("RGBA", (SZ, SZ)); p = im.load(); ph = 2 * math.pi * f / n
        for y in range(SZ):
            for x in range(SZ):
                dx, dy = (x - 31.5) / 32, (y - 31.5) / 32; r = math.hypot(dx, dy); th = math.atan2(dy, dx)
                a = max(0, math.sin(4 * th + 9 * r - ph)) ** 2 * max(0, 1 - r) ** .7
                if a > .02: p[x, y] = (cl(150 + 105 * a), cl(225 + 30 * a), 255, int(255 * min(1, a * 1.2)))
        out.append(im)
    return out


def soft_mist(SZ=16, seed=3):
    r = random.Random(seed); im = Image.new("RGBA", (SZ, SZ)); p = im.load()
    for y in range(SZ):
        for x in range(SZ):
            d = math.hypot(x - 7.5, y - 7.5) / 8; a = max(0, 1 - d) ** 1.6 * (0.75 + r.random() * .25)
            p[x, y] = (150, 240, 255, int(190 * a))
    return im


def pix(rows, col):
    im = Image.new("RGBA", (len(rows[0]), len(rows)));
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch != ".": im.putpixel((x, y), col if ch == "#" else (255, 255, 255, 255))
    return im


# ----------------------------------------------------------------------------- gate model
def tapered(m, bone, cx, cz, y0, direction, w, h, mat, steps=4):
    y = y0
    for k, (wf, hf) in enumerate(((1, .34), (.7, .28), (.45, .22), (.22, .16))[:steps]):
        ww, hh = w * wf, h * hf
        lo, hi = (y, y + hh) if direction > 0 else (y - hh, y)
        m.cube(bone, [cx - ww / 2, lo, cz - ww / 2], [ww, hh, ww], mat)
        y = hi if direction > 0 else lo


def gate_model():
    img, gl, pos, var = atlas(NP, 128, 77)
    m = M(atl=(pos, var)); b = m.bone; c = m.cube
    b("root", None, [0, 0, 0]); b("arch", "root", [0, 48, 0]); b("chains", "root", [0, 90, 7]); b("crown", "root", [0, 101, 0])
    for i in range(1, 5): b(f"icicle_{i}", "root", [(-14, 12, -52, 52)[i - 1], (86, 86, 4, 4)[i - 1], 0])
    for i, (x, y, z) in enumerate(((-52, 60, 2), (52, 70, -3), (-46, 100, 6), (48, 95, 5), (-30, 112, -4), (30, 118, 3)), 1):
        b(f"frag_{i}", "root", [x, y, 0])
    c("root", [-44, 0, -12], [88, 6, 24], "stone_dark"); c("root", [-40, 6, -9], [80, 3, 18], "polished")
    c("root", [-56, 0, -8], [14, 4, 16], "marble", mir="root"); c("root", [-52, 4, -5], [8, 2, 10], "polished", mir="root")
    c("root", [-58, 0.5, -9], [2, 3, 18], "silver", mir="root")
    c("arch", [-42, 9, -8], [20, 6, 16], "stone_dark", mir="arch")
    c("arch", [-40, 15, -7], [16, 68, 14], "black_ice", mir="arch")
    c("arch", [-42, 80, -8], [20, 6, 16], "stone_dark", mir="arch")
    c("arch", [-42, 86, -7], [84, 14, 14], "black_ice"); c("arch", [-42, 99, -8], [84, 3, 16], "stone_dark")
    c("arch", [-31, 74, -6], [11, 11, 12], "black_ice", [0, 0, 45], [-25.5, 79.5, 0], mir="arch")
    for z in (7, -7.4):
        c("arch", [-37, 24, z], [10, 40, .4], "rune", mir="arch")
        c("arch", [-30, 89, z], [60, 8, .4], "rune")
    for i in range(6):
        c("chains", [-39 + 13 * i, 92 - 4 * math.sin(math.pi * i / 5), 7.4], [13, 2.2, 1], "chain", [0, 0, (-1) ** i * 8], [-33 + 13 * i, 93, 7.9])
    c("chains", [-34, 12, 7.4], [3, 72, 1], "chain", mir="chains"); c("chains", [-27, 14, 7.4], [2.2, 68, 1], "silver", mir="chains")
    c("crown", [-24, 101, -5], [48, 4, 10], "silver"); c("crown", [-20, 105, -4], [40, 1.5, 8], "polished")
    for (x, h, tl, mat) in ((-22, 12, -8, "crystal"), (-14, 19, 4, "crystal_light"), (-6, 10, -3, "crystal"), (2, 24, 0, "crystal_light"), (11, 15, 9, "crystal"), (19, 8, 14, "crystal")):
        c("crown", [x, 106.5, -2.5], [5, h, 5], mat, [0, 0, tl], [x + 2.5, 106.5, 0])
    tapered(m, "icicle_1", -14, 0, 86, -1, 9, 34, "crystal_light"); tapered(m, "icicle_2", 12, 0, 86, -1, 8, 26, "crystal")
    tapered(m, "icicle_3", -52, 0, 6, 1, 11, 64, "crystal"); tapered(m, "icicle_4", 52, 0, 6, 1, 10, 56, "crystal_light")
    for i, (x, y, z) in enumerate(((-52, 60, 2), (52, 70, -3), (-46, 100, 6), (48, 95, 5), (-30, 112, -4), (30, 118, 3)), 1):
        c(f"frag_{i}", [x - 2.5, y - 4, z - 2.5], [5, 8 - i % 3, 5], "crystal_light" if i % 2 else "crystal", [20 * i, 30 * i, 15], [x, y, z])
    return m, img, gl


def gate_anims():
    A = {}
    fr = lambda i: f"frag_{i}"
    idle = {}
    for i in range(1, 7):
        idle[fr(i)] = {"p": cyc(4, 8, lambda t, i=i: (0, 3 * S(t + i * .17), 0)), "r": cyc(4, 8, lambda t, i=i: (4 * S(t + i * .1), 18 * S(t * 1 + i * .21), 3 * S(t)))}
    idle["chains"] = {"r": cyc(4, 8, lambda t: (0, 0, 1.5 * S(t)))}
    idle["crown"] = {"p": cyc(4, 8, lambda t: (0, .8 * S(t), 0))}
    A["idle"] = an(4.0, True, idle)
    act = {"crown": {"p": {0: Z, .6: [0, 5, 0], 1.2: [0, 5, 0], 2.5: Z}, "s": {0: [1, 1, 1], .6: [1.06, 1.06, 1.06], 2.5: [1, 1, 1]}},
           "chains": {"r": cyc(2.5, 10, lambda t: (0, 0, 5 * S(t * 4) * (1 - t)))}}
    for i in range(1, 5):
        act[f"icicle_{i}"] = {"s": {0: [1, 1, 1], .5: [1.12, 1.04, 1.12], 1.0: [1, 1, 1], 1.5: [1.08, 1.02, 1.08], 2.5: [1, 1, 1]}}
    for i in range(1, 7):
        sx = -1 if i % 2 else 1
        act[fr(i)] = {"p": {0: Z, .8: [sx * 8, 6, 0], 1.6: [sx * 4, 3, 0], 2.5: Z}, "r": {0: Z, 1.2: [40, 90, 20], 2.5: Z}}
    A["activate"] = an(2.5, False, act)
    return A


def build_portal():
    m, img, gl = gate_model(); A = gate_anims(); key = "frozen_grave_gate"
    W(f"Portal/{key}.geo.json", geo_json(f"geometry.{key}", m, 128, 128))
    W(f"Portal/{key}.animation.json", anim_json(key, A))
    W(f"Portal/{key}.bbmodel", bbmodel(key, m.bones, m.cubes, img, 128, 128, A))
    SAVE(img, f"Portal/{key}.png"); SAVE(gl, f"Portal/{key}_glowing.png")
    fr = portal_frames(); st = strip(fr)
    SAVE(st, "Portal/portal_surface_strip.png"); SAVE(st, f"{RES}/textures/block/frozen_grave_portal_surface.png")
    W(f"{RES}/textures/block/frozen_grave_portal_surface.png.mcmeta", {"animation": {"frametime": 2, "interpolate": True}})
    W("Portal/portal_surface_strip.png.mcmeta", {"animation": {"frametime": 2, "interpolate": True}})
    for i, f in enumerate(fr): SAVE(f, f"Portal/surface_frames/portal_surface_{i}.png")
    # surface model
    uvs = {"north": ([0, 0], [64, 64]), "south": ([0, 0], [64, 64]), "east": ([0, 0], [2, 64]), "west": ([0, 0], [2, 64]), "up": ([0, 0], [48, 2]), "down": ([0, 0], [48, 2])}
    sg = {"format_version": "1.12.0", "minecraft:geometry": [{"description": {"identifier": "geometry.frozen_grave_portal_surface", "texture_width": 64, "texture_height": 64,
          "visible_bounding_box_width": 4, "visible_bounding_box_height": 6, "visible_bounding_box_offset": [0, 3, 0]},
          "bones": [{"name": "surface", "pivot": [0, 9, 0], "cubes": [{"origin": [-24, 9, -1], "size": [48, 77, 2], "uv": {d: {"uv": a, "uv_size": b_} for d, (a, b_) in uvs.items()}}]}]}]}
    W("Portal/frozen_grave_portal_surface.geo.json", sg)
    surf = mk([-24, 9, -1], [24, 86, 1], (0, 0), name="surface", bone="surface")
    for d, (a, b_) in uvs.items(): surf["faces"][d] = [a[0], a[1], a[0] + b_[0], a[1] + b_[1]]
    W("Portal/frozen_grave_portal_surface.bbmodel", bbmodel("frozen_grave_portal_surface", [("surface", None, [0, 9, 0])], [surf], st, 64, 64, texname="portal_surface"))
    # effects
    SAVE(strip(burst_frames()), f"{RES}/textures/effect/portal_activation_burst.png"); SAVE(strip(burst_frames()), "Portal/Effects/portal_activation_burst_strip.png")
    SAVE(strip(vortex_frames()), f"{RES}/textures/effect/portal_entry_vortex.png"); SAVE(strip(vortex_frames()), "Portal/Effects/portal_entry_vortex_strip.png")
    for nm in ("portal_activation_burst", "portal_entry_vortex"):
        W(f"{RES}/textures/effect/{nm}.png.mcmeta", {"animation": {"frametime": 2}}); W(f"Portal/Effects/{nm}_strip.png.mcmeta", {"animation": {"frametime": 2}})
    parts = {"frost_mist": soft_mist(), "frost_snowflake": pix(["...#....", "#..#..#.", ".#.#.#..", "..###...", "#######.", "..###...", ".#.#.#..", "#..#..#."], (230, 248, 255, 255)),
             "frost_spark": pix(["...#...", "..###..", ".#####.", "#######", ".#####.", "..###..", "...#..."], (110, 225, 255, 255)),
             "crystal_shard": pix(["..#.", ".##.", ".##.", "###.", ".##.", ".#.."], (170, 240, 255, 255))}
    for n, im in parts.items():
        SAVE(im, f"{RES}/textures/particle/{n}.png"); SAVE(im, f"Portal/Effects/particles/{n}.png")
        W(f"{RES}/particles/{n}.json", {"textures": [f"{MOD}:{n}"]})
    # label assets
    bg = Image.new("RGBA", (192, 104), (0, 0, 0, 0)); d = ImageDraw.Draw(bg)
    d.rounded_rectangle([0, 0, 191, 103], 10, fill=(6, 18, 32, 205), outline=(90, 220, 255, 255), width=2)
    d.rounded_rectangle([5, 5, 186, 98], 7, outline=(40, 120, 170, 200), width=1)
    for x in (12, 179):
        for y in (12, 91): d.polygon([(x, y - 3), (x + 3, y), (x, y + 3), (x - 3, y)], fill=(150, 240, 255, 255))
    SAVE(bg, f"{RES}/textures/gui/portal_label_bg.png"); SAVE(bg, "Portal/Label/portal_label_bg.png")
    lines = [("LEVEL 1", "#78F0FF", 1.2), ("GATE 2/5", "#C8FAFF", 1.0), ("FROZEN GRAVE", "#5AC8FF", 1.0), ("DIFFICULTY: BEGINNER II", "#96E6FF", 0.7)]
    W("Portal/Label/label_spec.json", {"_note": "DATA ONLY. In-world floating text must be drawn by client rendering code (BlockEntityRenderer); a PNG cannot float by itself.",
        "portal_id": "P1-2", "lines": [{"text": t, "color": c, "scale": s} for t, c, s in lines], "background_texture": f"{MOD}:textures/gui/portal_label_bg.png",
        "background_nine_slice_border_px": 6, "offset_above_gate_blocks": 1.0, "billboard": True, "visible_distance_blocks": 32, "fade_start_blocks": 24,
        "min_readable_distance_blocks": 2, "background_alpha": 0.8, "text_shadow": True})
    mock = Image.new("RGBA", (192, 104)); mock.paste(bg); md = ImageDraw.Draw(mock)
    f1, f2 = ImageFont.load_default(size=16), ImageFont.load_default(size=11)
    for (t, c, s), y, fnt in zip(lines, (10, 30, 50, 74), (f1, f2, f2, ImageFont.load_default(size=9))):
        w_ = md.textlength(t, font=fnt); md.text(((192 - w_) / 2, y), t, fill=c, font=fnt)
    SAVE(mock.resize((576, 312), Image.NEAREST), "Portal/Label/label_MOCKUP_not_ingame.png")
    # preview (gate + surface frame 0)
    surf_el = mk([-24, 9, -.5], [24, 86, .5], (0, 128), name="s"); surf_el["faces"]["north"] = [0, 128, 64, 192]; surf_el["faces"]["south"] = [0, 128, 64, 192]
    for d_ in ("east", "west", "up", "down"): surf_el["faces"][d_] = [0, 128, 1, 129]
    comb = Image.new("RGBA", (128, 192), (0, 0, 0, 0)); comb.paste(img, (0, 0)); comb.paste(fr[3], (0, 128))
    cg = Image.new("RGBA", (128, 192), (0, 0, 0, 0)); cg.paste(gl, (0, 0)); cg.paste(fr[3], (0, 128))
    elems = m.cubes + [surf_el]
    views = [render(elems, comb, cg, yaw=y, pitch=p, size=640) for y, p in ((-28, 14), (0, 6), (35, 14))]
    pv = Image.new("RGB", (1920, 640)); [pv.paste(v, (i * 640, 0)) for i, v in enumerate(views)]
    SAVE(pv, "Portal/frozen_grave_gate_preview.png")
    ic = render(elems, comb, cg, yaw=-25, pitch=12, size=256, bg=None)
    SAVE(ic.resize((64, 64), Image.LANCZOS), f"{RES}/textures/item/frozen_grave_gate.png"); SAVE(ic.resize((64, 64), Image.LANCZOS), "Portal/frozen_grave_gate_inventory_icon.png")
    W(f"{RES}/models/item/frozen_grave_gate.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{MOD}:item/frozen_grave_gate"}})
    BB.LANG[f"block.{MOD}.frozen_grave_gate"] = "The Frozen Grave Gate"
    print("portal", len(m.cubes), "cubes")


# ----------------------------------------------------------------------------- trophy
def build_trophy():
    img, gl, pos, _ = atlas(["silver", "metal_dark", "sapphire", "eye", "snow_silver", "polished", "stone_dark", "crystal_light"], 64, 88, fillrest=False)
    sv, md, sp, eye, ss, po, st, cr = [pos[k] for k in ("silver", "metal_dark", "sapphire", "eye", "snow_silver", "polished", "stone_dark", "crystal_light")]
    E = BB.E
    el = [E([2, 0, 2], [14, 2, 14], st), E([3, 2, 3], [13, 4, 13], md), E([4.5, 4, 4.5], [11.5, 5.2, 11.5], po, over={"north": ss, "south": ss, "east": ss, "west": ss}),
          E([6, 3.9, 6], [10, 4.1, 10], sv),
          E([4, 5.2, 4], [12, 7.2, 5], sv, over={"north": (ss[0] + 4, ss[1] + 7)}), E([5, 2, 2.8], [11, 4, 3], ss, over={"north": (ss[0] + 5, ss[1] + 6)}), E([4, 5.2, 11], [12, 7.2, 12], sv), E([4, 5.2, 5], [5, 7.2, 11], sv),
          E([11, 5.2, 5], [12, 7.2, 8.4], sv),
          E([10.4, 5.2, 9.8], [13.2, 6.2, 12], sv, [0, 25, 0], [11.8, 5.2, 10.9]),
          E([7, 5.2, 7], [9, 10, 9], eye, [0, 45, 0], [8, 7, 8])]
    def spike(cx, cz, h, w, ax, ang, broken=False):
        out = []; y = 7.2; piv = [cx, 7.2, cz]
        for (wf, hf) in ((1, .4), (.7, .3), (.4, .2), (.18, .1)) if not broken else ((1, .4), (.7, .25)):
            ww, hh = w * wf, h * hf; rot = [0, 0, 0]; rot[ax] = ang
            out.append(E([cx - ww / 2, y, cz - ww / 2], [cx + ww / 2, y + hh, cz + ww / 2], sp, rot, piv)); y += hh
        return out
    for a in ((5.2, 5.5, 8.5, 1.8, 2, -14), (8, 4.6, 9, 1.9, 0, 12), (10.8, 5.5, 7.5, 1.7, 2, 14), (5.2, 10.6, 6.5, 1.7, 2, -10), (10.8, 10.6, 6, 1.6, 2, 12, True)):
        el += spike(*a)
    W(f"{RES}/models/item/frozen_kings_crown.json", {**java_model("minecraft:block/block", f"{MOD}:item/frozen_kings_crown", el, 64),
        "display": {"gui": {"rotation": [28, 38, 0], "translation": [0, -1, 0], "scale": [0.82, 0.82, 0.82]},
                    "ground": {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.55, 0.55, 0.55]},
                    "fixed": {"rotation": [0, 180, 0], "translation": [0, -1, 0], "scale": [0.8, 0.8, 0.8]},
                    "thirdperson_righthand": {"rotation": [0, 0, 0], "translation": [0, 3, 1], "scale": [0.55, 0.55, 0.55]},
                    "firstperson_righthand": {"rotation": [0, 45, 0], "translation": [0, 1, 0], "scale": [0.6, 0.6, 0.6]},
                    "head": {"rotation": [0, 0, 0], "translation": [0, 14, 0], "scale": [1.1, 1.1, 1.1]}}})
    SAVE(img, f"{RES}/textures/item/frozen_kings_crown.png"); SAVE(gl, f"{RES}/textures/item/frozen_kings_crown_e.png")
    W("Trophy/frozen_kings_crown.bbmodel", bbmodel("frozen_kings_crown", None, el, img, 64, 64, java=True, texname="frozen_kings_crown"))
    SAVE(img, "Trophy/frozen_kings_crown.png"); SAVE(gl, "Trophy/frozen_kings_crown_e.png")
    pv = [render(el, img, gl, yaw=y, pitch=p, size=520) for y, p in ((-35, 22), (0, 8), (145, 22))]
    sh = Image.new("RGB", (1560, 520)); [sh.paste(v, (i * 520, 0)) for i, v in enumerate(pv)]
    SAVE(sh, "Trophy/frozen_kings_crown_preview.png")
    ic = render(el, img, gl, yaw=-35, pitch=22, size=256, bg=None).resize((64, 64), Image.LANCZOS)
    SAVE(ic, "Trophy/frozen_kings_crown_inventory_icon.png"); SAVE(ic, f"{RES}/textures/item/frozen_kings_crown_icon.png")
    BB.LANG[f"item.{MOD}.frozen_kings_crown"] = "The Frozen King's Crown"
    W(f"Resources/data/{MOD}/loot_tables/entities/frozen_gravekeeper.json", {"type": "minecraft:entity", "pools": [
        {"rolls": 1, "entries": [{"type": "minecraft:item", "name": "minecraft:diamond", "functions": [{"function": "minecraft:set_count", "count": {"min": 2, "max": 4}}]}]}]})
    print("trophy", len(el), "elements")


if __name__ == "__main__":
    BB.LANG.update(json.load(open("/home/claude/gen/lang_blocks.json")))
    build_portal(); build_trophy()
    BB.LANG.update({f"entity.{MOD}.frostgrave_prowler": "Frostgrave Prowler", f"entity.{MOD}.chainbound_sentinel": "Chainbound Sentinel", f"entity.{MOD}.frozen_gravekeeper": "The Frozen Gravekeeper"})
    W(f"{RES}/lang/en_us.json", BB.LANG)
