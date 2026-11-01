# Decor Pack 07 — Al Ostoura Legacy (Minecraft **Java Edition** only)

20 static decorative assets, exactly 2 per character of MOBS 51–60. Assets only: no Java code, no mobs, no structures, no Bedrock files.

## Folder layout
| Folder | Contents |
|---|---|
| `Models/` | 20 editable Blockbench `.bbmodel` projects (format *Java Block/Item*, texture embedded, parts grouped by name) |
| `Textures/` | 20 PNG textures (64×64, or 128×128 for the 4 centerpieces) |
| `Java_Model_Exports/` | Ready-to-merge resource-pack tree: `assets/soul_bound_dimension/models/block/decor/*.json`, `models/item/*.json`, `textures/block/decor/*.png` |
| `Previews/` | One 512×512 preview per asset + `00_OVERVIEW_all_20_decorations.png` (rendered from the actual model JSON + PNG) |
| `Documentation/` | `asset_manifest.json/.csv/.md`, `ARABIC_TEXT.md`, `VERIFICATION_REPORT.md` |

## Conventions
* **Namespace** `soul_bound_dimension` is a placeholder — search-and-replace it with your mod id (model JSON `textures` entries, item parents, texture folders, `.bbmodel` texture namespace).
* **Model ID** pattern: `soul_bound_dimension:block/decor/<name>`; texture path `soul_bound_dimension:block/decor/<name>` → `assets/soul_bound_dimension/textures/block/decor/<name>.png`. Item models just inherit the block model (`parent`).
* **Orientation:** the *front* of every model faces **NORTH (−Z)**, i.e. default blockstate rotation `y=0` like a furnace. Rotate with blockstate `y=90/180/270`.
* **Origin / scale:** 1 unit = 1 px = 1/16 block; models sit on the floor at y=0 inside X/Z 0–16 (wall items hug the **south** wall, z=14–16, facing north).
* **Texturing:** single-texture per model, `texture_size` declared in each JSON; every UV is inside the texture. Faces without geometry-visible use are omitted. No element rotations are used (all cuboids axis-aligned), so no Blockbench-only features are needed.
* **Render layer:** assets with semi-transparent pixels (glass/tea glasses/clock glass) need the translucent or cutout render layer registered by the code side — see the `translucent` column in the manifest. Everything else is opaque.
* **Emissive/lighting:** red crystals, the zombie eyes and the sign text are painted bright but are not self-lit in vanilla; the integrating developer may add `light_emission`/block light level.
* **All decorations are static.** Collision, hitboxes, placement, light level, rotation states, drops and any interaction are left to the developer implementing registration.

## Oversized models (need special handling)
Models taller/wider than one block are still valid Java block models (elements stay inside the legal −16…32 range), but vanilla block rendering will clip/cull them unless you handle it:
* `refai_memorial_statue` — 14×28×14 units (1.75 blocks tall)
* `twin_brothers_portrait` — 16.4×20.0×3.6 units (1.25 blocks tall)
* `antique_cairo_grandfather_clock` — 10×31×10 units (1.94 blocks tall)
* `el_nemr_golden_lion_throne` — 16×25×14.4 units (1.56 blocks tall)
* `vintage_workshop_machinery` — 16.8×20×13.2 units (1.25 blocks tall)
* `hanan_traditional_vanity` — 15.2×20.6×13.4 units (1.29 blocks tall)
* `giant_zombie_refai_monument` — 16×31.4×16 units (1.96 blocks tall)
* `throne_of_the_undead_legend` — 16.4×31.4×16 units (1.96 blocks tall)

Recommended: register them as a 1×2 multi-block (lower half carries the model, upper half is a hitbox/placeholder) or use an oversized render shape / custom renderer so the part above y=16 is drawn. The complete editable `.bbmodel` is preserved for every one of them; if you prefer to split a model into two stacked 16-unit blocks, cut it in Blockbench at y=16.

## Assets
| # | Model ID | Title | Mob | Tex | Elems | Size X×Y×Z (px/units) | Notes |
|---|---|---|---|---|---|---|---|
| 01 | `soul_bound_dimension:block/decor/refai_legendary_chair` | Refai's Legendary Chair | 51 – Refai El Desouky | 64² | 33 | 15.6×16×13.0 |  |
| 02 | `soul_bound_dimension:block/decor/refai_memorial_statue` | Refai's Memorial Statue | 51 – Refai El Desouky | 64² | 28 | 14×28×14 | OVERSIZED (>1 block) |
| 03 | `soul_bound_dimension:block/decor/nasser_executive_desk` | Nasser's Executive Desk | 52 – Nasser El Desouky | 64² | 40 | 15.4×16.8×14.8 |  |
| 04 | `soul_bound_dimension:block/decor/twin_brothers_portrait` | The Twin Brothers Portrait | 52 – Nasser El Desouky (with Refai) | 128² | 23 | 16.4×20.0×3.6 | OVERSIZED (>1 block) |
| 05 | `soul_bound_dimension:block/decor/salmawy_archive_cabinet` | Salmawy's Secret Archive Cabinet | 53 – Dr. Salmawy | 64² | 28 | 14.4×16×13.2 |  |
| 06 | `soul_bound_dimension:block/decor/black_marble_conference_table` | The Black Marble Conference Table | 53 – Dr. Salmawy | 64² | 23 | 16×16×11.4 |  |
| 07 | `soul_bound_dimension:block/decor/traditional_egyptian_majlis` | Traditional Egyptian Majlis | 54 – Mokhtar El Desouky | 64² | 27 | 16×15.4×16 | translucent pixels |
| 08 | `soul_bound_dimension:block/decor/antique_cairo_grandfather_clock` | Antique Cairo Grandfather Clock | 54 – Mokhtar El Desouky | 64² | 28 | 10×31×10 | OVERSIZED (>1 block) translucent pixels |
| 09 | `soul_bound_dimension:block/decor/el_nemr_golden_lion_throne` | El Nemr's Golden Lion Throne | 55 – Essam El Nemr | 64² | 29 | 16×25×14.4 | OVERSIZED (>1 block) |
| 10 | `soul_bound_dimension:block/decor/el_nemr_family_emblem` | The El Nemr Family Emblem | 55 – Essam El Nemr | 64² | 29 | 17.2×17.8×2.5 |  |
| 11 | `soul_bound_dimension:block/decor/workshop_tool_cabinet` | Egyptian Workshop Tool Cabinet | 56 – Amin El Nemr | 64² | 33 | 14.4×16.2×11.5 |  |
| 12 | `soul_bound_dimension:block/decor/vintage_workshop_machinery` | Vintage Workshop Machinery | 56 – Amin El Nemr | 64² | 34 | 16.8×20×13.2 | OVERSIZED (>1 block) |
| 13 | `soul_bound_dimension:block/decor/mobile_phone_display_counter` | Egyptian Mobile Phone Display Counter | 57 – Morsi | 64² | 39 | 16.4×13.6×12.6 | translucent pixels |
| 14 | `soul_bound_dimension:block/decor/morsi_mobile_shop_sign` | Morsi's Mobile Shop Sign | 57 – Morsi | 64² | 18 | 16.4×13×3.0 |  |
| 15 | `soul_bound_dimension:block/decor/hanan_traditional_vanity` | Hanan's Traditional Vanity | 58 – Hanan | 64² | 50 | 15.2×20.6×13.4 | OVERSIZED (>1 block) translucent pixels |
| 16 | `soul_bound_dimension:block/decor/egyptian_family_tea_set` | Egyptian Family Tea Set | 58 – Hanan | 64² | 34 | 14.8×10×12.8 | translucent pixels |
| 17 | `soul_bound_dimension:block/decor/old_cairo_police_office_desk` | Old Cairo Police Office Desk | 59 – Kamal Mourad | 64² | 50 | 15.4×16.4×14.8 |  |
| 18 | `soul_bound_dimension:block/decor/police_station_wall_clock` | Egyptian Police Station Wall Clock | 59 – Kamal Mourad | 64² | 29 | 16×16.6×3.4 |  |
| 19 | `soul_bound_dimension:block/decor/giant_zombie_refai_monument` | Giant Zombie Refai Monument | 60 – Legendary Giant Zombie Refai | 128² | 53 | 16×31.4×16 | OVERSIZED (>1 block) |
| 20 | `soul_bound_dimension:block/decor/throne_of_the_undead_legend` | Throne of the Undead Legend | 60 – Legendary Giant Zombie Refai | 128² | 52 | 16.4×31.4×16 | OVERSIZED (>1 block) |

Details per asset: `Documentation/asset_manifest.md`.

## Design notes / assumptions
* Character looks for Refai (bald, thick black beard, sunglasses, dark clothes) and Zombie Refai follow the brief. Nasser's portrait look (grey hair, red tie) is an **assumption** — no reference image was available; repaint the `portrait` region of `twin_brothers_portrait.png` if mob 52 looks different.
* The original tiger-head (El Nemr) and the undead emblem are original designs, not copied art.
* Zombie Refai pieces are supernatural only — no gore.

## Testing status
See `Documentation/VERIFICATION_REPORT.md`. **Not tested inside the Minecraft client or the Blockbench GUI** (not available in this environment); files were validated structurally by script and previews were rendered with a custom software renderer, so small visual differences (lighting, AO, texture filtering) in game are expected.
