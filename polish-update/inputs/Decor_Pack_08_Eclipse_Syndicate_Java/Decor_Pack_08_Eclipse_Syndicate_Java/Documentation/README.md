# Decor Pack 08 - The Eclipse Syndicate (Minecraft **Java Edition** only)

20 static decorations, two per mob for MOBS 61-70, namespace `soulbound_decor`.
Assets only: no Java code, no mobs, no structures, no Bedrock files.

## Folder layout
| Folder | Contents |
|---|---|
| `Models/` | 20 editable Blockbench `.bbmodel` projects (format: Java Block/Item, texture embedded) |
| `Textures/` | 20 PNG atlases (64x64, or 128x128 for the Four-Handed Contract Monument) |
| `Java_Model_Exports/` | Ready resource-pack layout: `assets/soulbound_decor/{models/block, models/item, textures/block}`; plus `models/custom_fullscale/` for oversize pieces |
| `Previews/` | 20 individual renders, `00_overview_all_20.png`, and `Turnarounds/` (4 angles each) |
| `Documentation/` | `asset_manifest.json` (machine readable) and this README |

## Conventions
- Coordinates: 1 block = 16 px. Model origin is block-centre at floor level (Blockbench `[8,0,8]`). Every model faces **north** (front = -Z); rotate with blockstate `y` rotation.
- One shared texture atlas per model; materials are tiled inside it. All rotations obey Java limits (single axis, -45..45 in 22.5 steps).
- Texture path in models: `soulbound_decor:block/<id>`. Item models simply inherit the block model.
- "Emissive" = parts painted bright for full-bright rendering. Vanilla block models do **not** have per-face emission without a mod/shader; apply it in your renderer if wanted. "Translucent" = textures with alpha < 255 need the translucent render layer.

## IMPORTANT: oversize models
Vanilla Java block models only render elements within **-16..32 px** on each axis (a 3x3x3 block volume centred on the block). Most of these sculptures are intentionally larger than one block. For each one:
- `models/block/<id>.json` is a **uniformly scaled-down** copy that fits vanilla bounds (scale listed below). It works as a normal block/item model but is smaller than the artistic size.
- `models/custom_fullscale/<id>.json` keeps the **true full size**. It is NOT valid as a vanilla block model (it will be clipped); it is for your custom renderer / multiblock split.
- The `.bbmodel` is always the full-size master.
Nothing here claims unsupported geometry works directly as a vanilla block model.

## Asset table
| # | Mob | Title / ID | Size (blocks W x H x D) | Elements | Tex | Vanilla export scale | Placement |
|---|---|---|---|---|---|---|---|
| 1 | 61 The Pale Broker | **The Soul Contract Desk**<br>`soul_contract_desk` | 2.0 x 2.38 x 1.0 | 87 | 64px | 0.8289 | Floor decoration, 2x1 footprint (32x16 px), floating pages/coins reach 2.1 blocks tall. Front faces north. |
| 2 | 61 The Pale Broker | **The Four-Handed Contract Monument**<br>`four_handed_contract_monument` | 2.69 x 3.09 x 1.88 | 78 | 128px | 0.6376 | Large centrepiece, 2x2 footprint, ~3.1 blocks tall. Exceeds one block - multiblock or custom BER. Front faces north. |
| 3 | 62 The Clockwork Widow | **The Widow's Mechanical Clock**<br>`widows_mechanical_clock` | 3.13 x 3.13 x 0.87 | 87 | 64px | 0.6287 | Floor decoration, 1x1 body with 2-block-wide arm span, ~2.9 blocks tall. Exceeds 1x1 block bounds - multiblock/custom BER. Faces north. |
| 4 | 62 The Clockwork Widow | **The Broken Hourglass Throne**<br>`broken_hourglass_throne` | 2.31 x 3.58 x 1.38 | 87 | 64px | 0.5492 | Floor decoration, 2x2 footprint, ~2.6 blocks tall; rotates with facing (default faces north). Needs custom collision. |
| 5 | 63 The Hollow Bastion | **The Hollow Knight Monument**<br>`hollow_knight_monument` | 2.17 x 3.18 x 1.5 | 73 | 64px | 0.6196 | Large statue, 2x2 footprint, ~3.4 blocks tall. Multiblock or custom BER. Front faces north. |
| 6 | 63 The Hollow Bastion | **The Bastion Shield Display**<br>`bastion_shield_display` | 1.56 x 2.73 x 1.5 | 59 | 64px | 0.7225 | Floor display, 2x1 footprint, ~2.8 blocks tall; against a wall or freestanding. Faces north. |
| 7 | 64 The Raven Seamstress | **The Raven Queen's Sewing Table**<br>`raven_queens_sewing_table` | 2.02 x 1.69 x 0.99 | 83 | 64px | 1.0 | Floor decoration, 2x1 footprint, ~2 blocks tall. Faces north. |
| 8 | 64 The Raven Seamstress | **The Thousand Ravens Chandelier**<br>`thousand_ravens_chandelier` | 3.25 x 2.94 x 1.79 | 259 | 64px | 0.6848 | Ceiling-hung chandelier, ~3.4 blocks wide x 2.9 tall; attach point at top centre (y=44px). Needs custom hang rendering / multiblock. Faces north. |
| 9 | 65 The Glass Ronin | **The Crystal Samurai Statue**<br>`crystal_samurai_statue` | 1.79 x 3.51 x 1.62 | 67 | 64px | 0.5604 | Large statue, 2x2 footprint, ~3.4 blocks tall. Needs translucent render layer for amber crystal. Multiblock or custom BER. Faces north. |
| 10 | 65 The Glass Ronin | **The Mirror Blade Shrine**<br>`mirror_blade_shrine` | 2.98 x 2.95 x 1.07 | 87 | 64px | 0.6674 | Floor altar, 2x1 footprint, ~2.7 blocks tall; wide shrine frame. Faces north. |
| 11 | 66 The Spore Alchemist | **The Giant Fungal Experiment**<br>`giant_fungal_experiment` | 1.89 x 3.0 x 1.75 | 122 | 64px | 0.6562 | Floor decoration, 2x2 footprint, ~3.0 blocks tall (multiblock/custom BER). Glowing parts should use emissive/full-bright where supported. Faces north. |
| 12 | 66 The Spore Alchemist | **The Alchemist's Botanical Cabinet**<br>`alchemists_botanical_cabinet` | 1.38 x 2.77 x 0.79 | 81 | 64px | 0.712 | Floor furniture, 1x1 footprint (slightly wide), ~2.1 blocks tall; fits 1x2 block volume. Faces north. |
| 13 | 67 The 67 Boss | **The Legendary 67 Monument**<br>`legendary_67_monument` | 3.45 x 2.61 x 1.25 | 142 | 64px | 0.7531 | Large centrepiece, 3x1 footprint, ~3.6 blocks tall (multiblock/custom BER). Digits face north. |
| 14 | 67 The 67 Boss | **The Six-Seven Royal Throne**<br>`six_seven_royal_throne` | 2.4 x 4.15 x 1.65 | 96 | 64px | 0.4744 | Floor throne, 2x2 footprint, ~3.0 blocks tall; rotates with facing (default north). Custom collision required. |
| 15 | 68 The Porcelain Duchess | **The Porcelain Duchess Statue**<br>`porcelain_duchess_statue` | 1.8 x 3.0 x 1.75 | 56 | 64px | 0.6562 | Large statue, 2x2 footprint, ~3.3 blocks tall (multiblock/custom BER). Faces north. |
| 16 | 68 The Porcelain Duchess | **The Royal Mirror Vanity**<br>`royal_mirror_vanity` | 2.04 x 3.58 x 0.97 | 73 | 64px | 0.5507 | Floor furniture, 2x1 footprint, ~3 blocks tall; back against a wall. Faces north. |
| 17 | 69 The 69 Pose | **The Inverted 69 Sculpture**<br>`inverted_69_sculpture` | 2.81 x 3.16 x 1.0 | 57 | 64px | 0.6225 | Statue, 1x1 footprint, ~3.1 blocks tall (multiblock/custom BER). Faces north. |
| 18 | 69 The 69 Pose | **The Gravity Illusion Monument**<br>`gravity_illusion_monument` | 1.84 x 2.92 x 1.62 | 81 | 64px | 0.6731 | Large abstract centrepiece, 2x2 footprint, ~3.1 blocks tall (multiblock/custom BER). Symmetric; any facing. |
| 19 | 70 The King of Corny Memes | **The Ultimate Cringe Throne**<br>`ultimate_cringe_throne` | 2.69 x 4.76 x 1.75 | 64 | 64px | 0.4134 | Floor throne, 2x2 footprint, ~3.3 blocks tall; rotates with facing (default north). Custom collision required. |
| 20 | 70 The King of Corny Memes | **The King of Corny Memes Statue**<br>`king_of_corny_memes_statue` | 2.25 x 3.79 x 1.75 | 72 | 64px | 0.5198 | Large boss statue, 2x2 footprint, ~3.9 blocks tall (multiblock/custom BER). Faces north. |

## Integration notes for the developer
1. Copy `Java_Model_Exports/assets/soulbound_decor/` into your mod's `assets/`.
2. Register each block/item (not included) and point blockstates at `soulbound_decor:block/<id>`; add `y` rotations for facing.
3. For full-size rendering use the `custom_fullscale` JSON (BER/custom model loader) or split the `.bbmodel` into multiblock parts.
4. Collision/hitboxes are not provided; use the size column.
5. Hanging items (chandelier) attach at the top-centre of the model.
6. Materials with translucent alpha: Soul-glass/crystal pieces (amber, cyan glass, mirrors).

## Verification and honest disclosure
Run in the generation environment: all JSON parses; every texture reference resolves to an existing PNG; texture sizes checked; all names lowercase snake_case; every UV within texture bounds; rotation limits; ZIP integrity test.
**Not tested:** loading in real Blockbench or in a running Minecraft client (neither is available here). The `.bbmodel` files follow the Blockbench 4.10 Java Block format but have not been opened in the app. Previews are rendered by a custom software renderer, not by Minecraft, so lighting differs in-game. Textures are procedurally hand-tuned pixel art generated by script, not painted by hand.
