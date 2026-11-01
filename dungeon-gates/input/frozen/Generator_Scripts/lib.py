import math, random, json, os, io, base64, uuid
from PIL import Image, ImageDraw, ImageFont

MOD = "soulbound"          # placeholder namespace - replace with the real mod id
OUT = "/home/claude/pkg"
DIRS = ["north", "east", "south", "west", "up", "down"]


def W(path, data, js=True):
    p = os.path.join(OUT, path)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    if js:
        with open(p, "w") as f:
            json.dump(data, f, indent=2)
    else:
        with open(p, "w") as f:
            f.write(data)


def SAVE(img, path):
    p = os.path.join(OUT, path)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    img.save(p)


# ---------------------------------------------------------------- painters
def cl(v): return max(0, min(255, int(v)))
def mix(c, k): return (cl(c[0] + k), cl(c[1] + k), cl(c[2] + k), 255)
def A4(c): return tuple(c) + (255,) if len(c) == 3 else tuple(c)


def blank(a=255): return Image.new("RGBA", (16, 16), (0, 0, 0, a)), Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def fill(img, c, r, v=6):
    p = img.load()
    for y in range(16):
        for x in range(16):
            p[x, y] = mix(c, r.randint(-v, v))


def walk(p, r, col, n):
    x, y = r.randint(0, 15), r.randint(0, 15)
    for _ in range(n):
        p[x % 16, y % 16] = A4(col)
        x += r.choice([-1, 0, 1]); y += r.choice([0, 1, 1])


def glow_from(img, g, pred):
    p, q = img.load(), g.load()
    for y in range(16):
        for x in range(16):
            if pred(p[x, y]): q[x, y] = p[x, y]


def P_stone_dark(r):
    i, g = blank(); fill(i, (20, 24, 34), r, 5); p = i.load()
    for _ in range(3): walk(p, r, (52, 104, 148), 14)
    for _ in range(4): walk(p, r, (34, 70, 100), 8)
    for _ in range(6): p[r.randint(0, 15), r.randint(0, 15)] = (150, 205, 235, 255)
    return i, g


def P_stone_blue(r):
    i, g = blank(); fill(i, (30, 46, 80), r, 5); p = i.load()
    for y in range(16):
        for x in range(16):
            if y % 8 == 0 or x % 8 == (0 if (y // 8) % 2 else 4): p[x, y] = (14, 22, 44, 255)
    for _ in range(3): walk(p, r, (70, 120, 180), 8)
    return i, g


def P_black_ice(r):
    i, g = blank(); fill(i, (10, 14, 24), r, 3); p = i.load()
    for y in range(16):
        for x in range(16):
            if (x + y) % 7 == 0: p[x, y] = (30, 58, 92, 255)
            if (x * 3 + y) % 11 == 0: p[x, y] = (18, 34, 58, 255)
    for _ in range(7): p[r.randint(0, 15), r.randint(0, 15)] = (80, 200, 240, 255)
    glow_from(i, g, lambda c: c[:3] == (80, 200, 240))
    return i, g


def P_crystal(r, light=False):
    cols = [(40, 170, 230), (90, 220, 255), (150, 240, 255), (60, 190, 240)]
    if light: cols = [(120, 220, 255), (170, 240, 255), (225, 250, 255), (140, 230, 255)]
    i, g = blank(); p = i.load()
    for y in range(16):
        for x in range(16):
            p[x, y] = A4(cols[((x * 2 + y * 3 + r.randint(0, 1)) // 5) % 4])
    for k in range(16):
        if (k * 3 + 1) % 5 == 0: continue
    for t in range(16):
        p[t, 0] = (20, 120, 190, 255); p[0, t] = (20, 120, 190, 255)
    for _ in range(5): p[r.randint(1, 14), r.randint(1, 14)] = (255, 255, 255, 255)
    g.paste(i)
    return i, g


def P_crystal_light(r): return P_crystal(r, True)


def P_silver(r):
    i, g = blank(); p = i.load()
    for x in range(16):
        k = r.randint(-12, 12)
        for y in range(16): p[x, y] = mix((170, 180, 198), k + r.randint(-4, 4) - (y // 12) * 14)
    for x in range(16): p[x, 0] = (235, 242, 252, 255)
    return i, g


def P_chain(r):
    i, g = blank(); fill(i, (24, 28, 40), r, 3)
    d = ImageDraw.Draw(i)
    for k in range(0, 16, 8):
        d.ellipse([1, k, 14, k + 6], outline=(205, 212, 226, 255))
        d.ellipse([5, k + 4, 10, k + 11], outline=(130, 140, 160, 255))
    return i, g


def P_cloth(r, base=(66, 82, 108)):
    i, g = blank(); p = i.load()
    for y in range(16):
        for x in range(16):
            p[x, y] = mix(base, int(math.sin(x * 0.9) * 9) + ((x + y) % 2) * 4 - y // 6 * 3 + r.randint(-3, 3))
    return i, g


def P_cloth_bg(r): return P_cloth(r, (66, 82, 108))
def P_cloth_db(r): return P_cloth(r, (22, 32, 62))


def P_armor(r):
    i, g = blank(); fill(i, (24, 28, 40), r, 4); p = i.load()
    for t in range(16):
        for q in (0, 15): p[t, q] = (52, 72, 104, 255); p[q, t] = (52, 72, 104, 255)
    for q in ((2, 2), (13, 2), (2, 13), (13, 13)): p[q] = (150, 170, 200, 255)
    for t in range(3, 13): p[t, 8] = (34, 44, 66, 255)
    return i, g


def P_mask(r):
    i, g = blank(); p = i.load()
    for y in range(16):
        for x in range(16):
            k = 150 - y * 8
            p[x, y] = mix((k, k + 6, k + 18), r.randint(-3, 3) + (6 if 3 < x < 6 and 2 < y < 13 else 0))
    for x in range(1, 15): p[x, 5] = (22, 26, 36, 255)
    return i, g


def P_eye(r):
    i, g = blank(); p = i.load()
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - 7.5, y - 7.5)
            p[x, y] = (cl(80 + 175 * (1 - d / 11)), cl(210 + 45 * (1 - d / 11)), 255, 255)
    g.paste(i)
    return i, g


def P_emblem(r):
    i, g = blank(); fill(i, (18, 28, 58), r, 3); d = ImageDraw.Draw(i); q = ImageDraw.Draw(g)
    for dd in (d, q):
        dd.polygon([(7, 1), (13, 7), (7, 14), (2, 7)], outline=(110, 230, 255, 255))
        dd.line([(7, 3), (7, 12)], fill=(190, 250, 255, 255)); dd.line([(3, 7), (12, 7)], fill=(190, 250, 255, 255))
        dd.line([(4, 4), (10, 10)], fill=(110, 230, 255, 255)); dd.line([(10, 4), (4, 10)], fill=(110, 230, 255, 255))
    return i, g


def P_rune(r):
    i, g = P_stone_dark(r); d = ImageDraw.Draw(i); q = ImageDraw.Draw(g)
    for dd in (d, q):
        dd.line([(3, 2), (3, 13)], fill=(120, 235, 255, 255)); dd.line([(3, 7), (9, 3)], fill=(120, 235, 255, 255))
        dd.line([(3, 8), (9, 12)], fill=(120, 235, 255, 255)); dd.line([(12, 2), (12, 13)], fill=(70, 190, 240, 255))
        dd.point([(9, 7), (10, 7)], fill=(220, 252, 255, 255))
    return i, g


def P_frost(r):
    i, g = blank(); fill(i, (200, 225, 240), r, 8); p = i.load()
    for _ in range(8): p[r.randint(0, 15), r.randint(0, 15)] = (255, 255, 255, 255)
    return i, g


def P_plate(r):
    i, g = blank(); fill(i, (26, 40, 78), r, 3); d = ImageDraw.Draw(i); q = ImageDraw.Draw(g)
    d.rectangle([0, 0, 15, 15], outline=(190, 200, 218, 255)); d.rectangle([2, 2, 13, 13], outline=(70, 96, 150, 255))
    d.polygon([(7, 3), (12, 8), (7, 12), (3, 8)], outline=(190, 200, 218, 255))
    for dd in (d, q): dd.rectangle([6, 6, 8, 9], fill=(110, 235, 255, 255))
    return i, g


def P_blade(r):
    i, g = blank(); p = i.load()
    for y in range(16):
        for x in range(16):
            t = x / 15
            p[x, y] = (cl(40 + 200 * t ** 2), cl(150 + 100 * t), 255, 255)
            if (x - y) % 6 == 0: p[x, y] = mix(p[x, y][:3], 25)
    for y in range(16): p[15, y] = (255, 255, 255, 255); p[14, y] = (190, 250, 255, 255)
    g.paste(i)
    return i, g


def P_teeth(r):
    i, g = blank(); fill(i, (225, 228, 222), r, 8); return i, g


def P_chest_crystal(r):
    i, g = P_eye(r); return i, g


def P_marble(r):
    i, g = blank(); fill(i, (222, 232, 242), r, 4); p = i.load()
    for _ in range(3): walk(p, r, (150, 185, 215), 12)
    for t in range(16):
        p[t, 0] = p[t, 15] = p[0, t] = p[15, t] = (130, 190, 230, 255)
    for q in ((7, 7), (8, 7), (7, 8), (8, 8)): p[q] = (70, 180, 235, 255)
    for q in ((3, 3), (12, 3), (3, 12), (12, 12)): p[q] = (90, 190, 240, 255)
    glow_from(i, g, lambda c: c[:3] in ((70, 180, 235), (90, 190, 240)))
    return i, g


def P_polished(r):
    i, g = blank(); fill(i, (18, 24, 40), r, 2); p = i.load()
    for y in range(16):
        for x in range(16):
            if (x + y) % 9 == 0: p[x, y] = (32, 46, 74, 255)
            if x in (0, 15) or y in (0, 15): p[x, y] = (8, 12, 22, 255)
    p[1, 1] = p[14, 1] = (60, 90, 130, 255)
    return i, g


def P_bricks(r):
    i, g = blank(); fill(i, (36, 46, 70), r, 5); p = i.load()
    for y in range(16):
        for x in range(16):
            if y % 4 == 0 or x % 8 == (0 if (y // 4) % 2 else 4): p[x, y] = (14, 18, 30, 255)
    for _ in range(3): walk(p, r, (10, 12, 20), 9)
    for bx, by in ((2, 1), (10, 5), (3, 9), (11, 13)): p[bx, by + 1] = p[bx + 1, by + 1] = (80, 210, 240, 255)
    glow_from(i, g, lambda c: c[:3] == (80, 210, 240))
    return i, g


def P_wood(r):
    i, g = blank(); fill(i, (38, 46, 80), r, 4); p = i.load()
    for x in range(16):
        for y in range(16):
            if y % 5 == 0: p[x, y] = (18, 24, 46, 255)
    return i, g


def P_chest_front(r):
    i, g = P_wood(r); d = ImageDraw.Draw(i)
    d.rectangle([6, 4, 9, 11], fill=(190, 200, 218, 255), outline=(110, 120, 140, 255))
    d.rectangle([7, 6, 8, 8], fill=(110, 235, 255, 255)); return i, g


def P_chest_top(r):
    i, g = P_wood(r); d = ImageDraw.Draw(i)
    for k in range(6):
        a = math.radians(k * 60)
        d.line([(7.5, 7.5), (7.5 + 6 * math.cos(a), 7.5 + 6 * math.sin(a))], fill=(190, 205, 225, 255))
    return i, g


def P_sapphire(r):
    cols = [(20, 50, 150), (40, 90, 210), (90, 150, 255), (30, 70, 180)]
    i, g = blank(); p = i.load()
    for y in range(16):
        for x in range(16): p[x, y] = A4(cols[((x * 3 + y * 2) // 4) % 4])
    for _ in range(4): p[r.randint(1, 14), r.randint(1, 14)] = (230, 245, 255, 255)
    g.paste(i); return i, g


def P_metal_dark(r):
    i, g = blank(); fill(i, (28, 32, 48), r, 3); p = i.load()
    for x in range(16): p[x, 3] = (60, 76, 110, 255); p[x, 12] = (14, 16, 26, 255)
    for q in ((2, 8), (13, 8)): p[q] = (150, 170, 200, 255)
    return i, g


def P_snow_silver(r):
    i, g = P_silver(r); d = ImageDraw.Draw(i)
    for k in range(6):
        a = math.radians(k * 60)
        ex, ey = 7.5 + 6 * math.cos(a), 7.5 + 6 * math.sin(a)
        d.line([(7.5, 7.5), (ex, ey)], fill=(80, 92, 120, 255))
        mx, my = 7.5 + 3.5 * math.cos(a), 7.5 + 3.5 * math.sin(a)
        for s in (-35, 35):
            b = a + math.radians(s)
            d.line([(mx, my), (mx + 2.4 * math.cos(b), my + 2.4 * math.sin(b))], fill=(80, 92, 120, 255))
    return i, g


def P_glass(r):
    i, g = blank(); fill(i, (120, 210, 245), r, 8); p = i.load()
    for t in range(16): p[t, 0] = p[t, 15] = p[0, t] = p[15, t] = (190, 200, 218, 255)
    g.paste(i); return i, g


def P_flame(r):
    i, g = blank(); p = i.load()
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - 7.5, y - 9) / 9
            p[x, y] = (cl(60 + 190 * (1 - d)), cl(130 + 120 * (1 - d)), 255, 255)
    g.paste(i); return i, g


def P_statue_ice(r):
    i, g = blank(); fill(i, (170, 205, 230), r, 7); p = i.load()
    for _ in range(3): walk(p, r, (120, 170, 210), 10)
    return i, g


def P_floor_snow(r):
    i, g = P_marble(r); return i, g


PAINT = {k[2:]: v for k, v in list(globals().items()) if k.startswith("P_")}


def atlas(names, size, seed, tint=(0, 0, 0), fillrest=True):
    cols = size // 16
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0)); gl = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    pos, var = {}, {}
    total = cols * cols if fillrest else len(names)
    for i in range(total):
        n = names[i % len(names)]
        a, b = PAINT[n](random.Random(seed * 1000 + i))
        if tint != (0, 0, 0) and n in ("armor", "stone_dark", "cloth_bg", "plate"):
            q = a.load()
            for y in range(16):
                for x in range(16): q[x, y] = tuple(cl(q[x, y][k] + tint[k]) for k in range(3)) + (255,)
        u, v = (i % cols) * 16, (i // cols) * 16
        img.paste(a, (u, v)); gl.paste(b, (u, v))
        var.setdefault(n, []).append((u, v))
        if n not in pos: pos[n] = (u, v)
    return img, gl, pos, var


# ---------------------------------------------------------------- elements
def dims(f, t, d):
    dx, dy, dz = [abs(t[i] - f[i]) for i in range(3)]
    return {"north": (dx, dy), "south": (dx, dy), "east": (dz, dy), "west": (dz, dy), "up": (dx, dz), "down": (dx, dz)}[d]


def mk(f, t, pos, rot=None, piv=None, name="cube", over=None, bone=None):
    faces = {}
    for d in DIRS:
        u, v = (over or {}).get(d, pos)
        w, h = dims(f, t, d)
        w = max(1, min(16, round(w))); h = max(1, min(16, round(h)))
        faces[d] = [u, v, u + w, v + h]
    return {"from": [round(x, 3) for x in f], "to": [round(x, 3) for x in t], "rot": rot, "pivot": piv,
            "faces": faces, "name": name, "bone": bone}


class M:
    """Creature model builder (pixel units, model faces -Z)."""
    def __init__(s, kx=1, ky=1, kz=1, atl=None):
        s.bones, s.cubes, s.kx, s.ky, s.kz, s.n = [], [], kx, ky, kz, 0
        s.pos, s.var = atl

    def sc(s, v): return [round(v[0] * s.kx, 3), round(v[1] * s.ky, 3), round(v[2] * s.kz, 3)]

    def bone(s, name, parent, pv): s.bones.append((name, parent, s.sc(pv)))

    def pair(s, l, r, pl, pr, pv):
        s.bone(l, pl, pv); s.bone(r, pr, [-pv[0], pv[1], pv[2]])

    def cube(s, bone, o, sz, mat, rot=None, pv=None, mir=None, over=None):
        s._add(bone, o, sz, mat, rot, pv, over)
        if mir:
            o2 = [-(o[0] + sz[0]), o[1], o[2]]
            r2 = [rot[0], -rot[1], -rot[2]] if rot else None
            p2 = [-pv[0], pv[1], pv[2]] if pv else None
            s._add(mir, o2, sz, mat, r2, p2, over)

    def _add(s, bone, o, sz, mat, rot, pv, over):
        s.n += 1
        opts = s.var[mat]; pos = opts[s.n % len(opts)]
        f = s.sc(o); t = s.sc([o[0] + sz[0], o[1] + sz[1], o[2] + sz[2]])
        ov = {d: s.var[m][s.n % len(s.var[m])] for d, m in (over or {}).items()}
        s.cubes.append(mk(f, t, pos, rot, s.sc(pv) if pv else None, f"{bone}_{s.n}", ov, bone))


def geo_json(ident, m, tw, th):
    mnx = min(c["from"][0] for c in m.cubes); mxx = max(c["to"][0] for c in m.cubes)
    mxy = max(c["to"][1] for c in m.cubes)
    bones = []
    for n, p, pv in m.bones:
        b = {"name": n, "pivot": pv}
        if p: b["parent"] = p
        cs = []
        for c in m.cubes:
            if c["bone"] != n: continue
            cd = {"origin": c["from"], "size": [round(c["to"][i] - c["from"][i], 3) for i in range(3)],
                  "uv": {d: {"uv": [c["faces"][d][0], c["faces"][d][1]],
                             "uv_size": [c["faces"][d][2] - c["faces"][d][0], c["faces"][d][3] - c["faces"][d][1]]} for d in DIRS}}
            if c["rot"]: cd["pivot"] = c["pivot"]; cd["rotation"] = c["rot"]
            cs.append(cd)
        if cs: b["cubes"] = cs
        bones.append(b)
    return {"format_version": "1.12.0", "minecraft:geometry": [{"description": {
        "identifier": ident, "texture_width": tw, "texture_height": th,
        "visible_bounding_box_width": round(max(abs(mnx), abs(mxx)) * 2 / 16 + 3, 2),
        "visible_bounding_box_height": round(mxy / 16 + 2, 2),
        "visible_bounding_box_offset": [0, round(mxy / 32, 2), 0]}, "bones": bones}]}


# ---------------------------------------------------------------- animation
def tkey(t):
    s = f"{t:.3f}".rstrip("0")
    return s + "0" if s.endswith(".") else s


def anim_json(ident, anims):
    out = {}
    for name, a in anims.items():
        bd = {}
        CH = {"r": "rotation", "p": "position", "s": "scale"}
        for b, chs in a["b"].items():
            bd[b] = {CH[ch]: {tkey(t): v for t, v in sorted(kf.items())} for ch, kf in chs.items()}
        out[f"animation.{ident}.{name}"] = {"loop": a["loop"], "animation_length": a["L"], "bones": bd}
    return {"format_version": "1.8.0", "animations": out}


def U(): return str(uuid.uuid4())


def bbmodel(name, m_bones, cubes, tex, tw, th, anims=None, java=False, texname="texture"):
    els, ids = [], {}
    for c in cubes:
        u = U(); ids[c["name"]] = u
        cen = c["pivot"] or [(c["from"][i] + c["to"][i]) / 2 for i in range(3)]
        els.append({"name": c["name"], "box_uv": False, "rescale": False, "locked": False, "from": c["from"],
                    "to": c["to"], "autouv": 0, "color": 0, "origin": cen, "rotation": c["rot"] or [0, 0, 0],
                    "faces": {d: {"uv": c["faces"][d], "texture": 0} for d in DIRS}, "type": "cube", "uuid": u})
    outl, gid = [], {}
    if m_bones:
        nodes = {}
        for n, p, pv in m_bones:
            gid[n] = U()
            nodes[n] = {"name": n, "origin": pv, "color": 0, "uuid": gid[n], "export": True, "isOpen": True,
                        "visibility": True, "children": []}
        for n, p, pv in m_bones:
            for c in cubes:
                if c["bone"] == n: nodes[n]["children"].append(ids[c["name"]])
        for n, p, pv in m_bones:
            (nodes[p]["children"] if p else outl).append(nodes[n])
    else:
        outl = [ids[c["name"]] for c in cubes]
    buf = io.BytesIO(); tex.save(buf, "PNG")
    data = {"meta": {"format_version": "4.10", "model_format": "java_block" if java else "free", "box_uv": False},
            "name": name, "model_identifier": name, "visible_box": [1, 1, 0], "variable_placeholders": "",
            "resolution": {"width": tw, "height": th}, "elements": els, "outliner": outl,
            "textures": [{"path": "", "name": texname + ".png", "folder": "", "namespace": "", "id": "0",
                          "particle": False, "render_mode": "default", "render_sides": "auto", "frame_time": 1,
                          "frame_order_type": "loop", "frame_interpolate": False, "visible": True, "mode": "bitmap",
                          "saved": True, "uuid": U(), "relative_path": "",
                          "source": "data:image/png;base64," + base64.b64encode(buf.getvalue()).decode()}]}
    if anims:
        al = []
        for an, a in anims.items():
            ani = {}
            for b, chs in a["b"].items():
                kfs = []
                for ch, kf in chs.items():
                    for t, v in sorted(kf.items()):
                        kfs.append({"channel": {"r": "rotation", "p": "position", "s": "scale"}[ch],
                                    "data_points": [{"x": v[0], "y": v[1], "z": v[2]}], "uuid": U(), "time": t,
                                    "color": -1, "interpolation": "linear"})
                ani[gid[b]] = {"name": b, "type": "bone", "keyframes": kfs}
            lp = {True: "loop", False: "once", "hold_on_last_frame": "hold"}[a["loop"]]
            al.append({"uuid": U(), "name": f"animation.{name}.{an}", "loop": lp, "override": False, "length": a["L"],
                       "snapping": 24, "selected": False, "anim_time_update": "", "blend_weight": "",
                       "start_delay": "", "loop_delay": "", "animators": ani})
        data["animations"] = al
    return data


def java_model(parent, tex, elems, tw, display=None):
    """Convert elements to vanilla JSON block model. UVs normalised to 0-16."""
    k = 16.0 / tw
    out = []
    for e in elems:
        d = {"from": e["from"], "to": e["to"],
             "faces": {f: {"uv": [round(x * k, 3) for x in e["faces"][f]], "texture": "#0"} for f in (e.get("only") or DIRS)}}
        if e["rot"]:
            ax = [i for i in range(3) if e["rot"][i]]
            d["rotation"] = {"origin": e["pivot"], "axis": "xyz"[ax[0]], "angle": e["rot"][ax[0]]}
            if e.get("rescale"): d["rotation"]["rescale"] = True
        if e.get("noshade"): d["shade"] = False
        out.append(d)
    m = {"parent": parent, "textures": {"0": tex, "particle": tex}, "texture_size": [tw, tw], "elements": out}
    if display: m["display"] = display
    return m


# ---------------------------------------------------------------- renderer
FC = {
    "north": lambda f, t: [(t[0], t[1], f[2]), (f[0], t[1], f[2]), (f[0], f[1], f[2]), (t[0], f[1], f[2])],
    "south": lambda f, t: [(f[0], t[1], t[2]), (t[0], t[1], t[2]), (t[0], f[1], t[2]), (f[0], f[1], t[2])],
    "east": lambda f, t: [(t[0], t[1], t[2]), (t[0], t[1], f[2]), (t[0], f[1], f[2]), (t[0], f[1], t[2])],
    "west": lambda f, t: [(f[0], t[1], f[2]), (f[0], t[1], t[2]), (f[0], f[1], t[2]), (f[0], f[1], f[2])],
    "up": lambda f, t: [(f[0], t[1], f[2]), (t[0], t[1], f[2]), (t[0], t[1], t[2]), (f[0], t[1], t[2])],
    "down": lambda f, t: [(f[0], f[1], t[2]), (t[0], f[1], t[2]), (t[0], f[1], f[2]), (f[0], f[1], f[2])],
}
NRM = {"north": (0, 0, -1), "south": (0, 0, 1), "east": (1, 0, 0), "west": (-1, 0, 0), "up": (0, 1, 0), "down": (0, -1, 0)}


def rotp(v, r, pv):
    x, y, z = v[0] - pv[0], v[1] - pv[1], v[2] - pv[2]
    a = math.radians
    c, s = math.cos(a(r[0])), math.sin(a(r[0])); y, z = y * c - z * s, y * s + z * c
    c, s = math.cos(a(r[1])), math.sin(a(r[1])); x, z = x * c + z * s, -x * s + z * c
    c, s = math.cos(a(r[2])), math.sin(a(r[2])); x, y = x * c - y * s, x * s + y * c
    return (x + pv[0], y + pv[1], z + pv[2])


def render(elems, tex, glow=None, yaw=-35, pitch=20, size=512, bg=(16, 22, 36), ss=2, off=(0, 0, 0), shade_floor=False):
    ya, pa = math.radians(yaw), math.radians(pitch)

    def view(v):
        x, y, z = v
        x, z = x * math.cos(ya) + z * math.sin(ya), -x * math.sin(ya) + z * math.cos(ya)
        y, z = y * math.cos(pa) + z * math.sin(pa), -y * math.sin(pa) + z * math.cos(pa)
        return (-x, y, z)

    tp, gp = tex.load(), (glow.load() if glow else None)
    faces = []
    for e in elems:
        f, t = e["from"], e["to"]
        for d in DIRS:
            vs = FC[d](f, t); n = NRM[d]
            if e["rot"]:
                vs = [rotp(v, e["rot"], e["pivot"]) for v in vs]; n = rotp(n, e["rot"], (0, 0, 0))
            vv = [view(v) for v in vs]; nv = view(n)
            if nv[2] >= 0: continue
            faces.append((sum(v[2] for v in vv) / 4, vv, nv, e["faces"][d]))
    faces.sort(key=lambda q: -q[0])
    xs = [v[0] for q in faces for v in q[1]]; ys = [v[1] for q in faces for v in q[1]]
    if not xs: return Image.new("RGB", (size, size), bg or (0, 0, 0))
    S = size * ss
    sc = S * 0.9 / max(max(xs) - min(xs), max(ys) - min(ys), 1)
    cx, cy = (max(xs) + min(xs)) / 2, (max(ys) + min(ys)) / 2
    sp = lambda v: (S / 2 + (v[0] - cx) * sc, S / 2 - (v[1] - cy) * sc)
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0)) if bg is None else Image.new("RGB", (S, S), bg); dr = ImageDraw.Draw(im)
    L = (-0.4, 0.8, -0.5); ln = math.sqrt(sum(x * x for x in L)); L = tuple(x / ln for x in L)
    for _, vv, nv, uv in faces:
        sh = 0.5 + 0.5 * max(0, sum(nv[i] * L[i] for i in range(3)))
        tl, tr, br, bl = vv
        u1, v1, u2, v2 = uv; nu, nv_ = max(1, u2 - u1), max(1, v2 - v1)

        def P(s, t):
            return sp(tuple(tl[i] + s * (tr[i] - tl[i]) + t * (bl[i] - tl[i]) for i in range(3)))
        for j in range(nv_):
            for i in range(nu):
                px_ = tp[u1 + i, v1 + j]
                if px_[3] < 20: continue
                col = tuple(cl(px_[k] * sh) for k in range(3))
                if gp:
                    g = gp[u1 + i, v1 + j]
                    if g[3] > 20: col = tuple(cl(g[k] * 0.55 + col[k] * 0.45 + 40) for k in range(3))
                dr.polygon([P(i / nu, j / nv_), P((i + 1) / nu, j / nv_), P((i + 1) / nu, (j + 1) / nv_), P(i / nu, (j + 1) / nv_)], fill=col)
    return im.resize((size, size), Image.LANCZOS)


def sheet(images, labels, path, cols=3, cell=360):
    rows = (len(images) + cols - 1) // cols
    im = Image.new("RGB", (cols * cell, rows * (cell + 24)), (10, 14, 24)); d = ImageDraw.Draw(im)
    for k, (a, l) in enumerate(zip(images, labels)):
        x, y = (k % cols) * cell, (k // cols) * (cell + 24)
        im.paste(a.resize((cell, cell), Image.LANCZOS), (x, y + 24)); d.text((x + 8, y + 6), l, fill=(150, 230, 255))
    SAVE(im, path)
