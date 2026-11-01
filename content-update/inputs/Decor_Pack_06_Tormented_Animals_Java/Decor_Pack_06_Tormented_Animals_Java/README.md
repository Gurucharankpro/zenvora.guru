# Decor Pack 06 - The Tormented Animals (Java Edition)

20 static decorative assets for the Soul Bound Dimension mod. **Minecraft Java Edition only.** Namespace: `soulbound_decor`. Assets only: no Java code, no mobs, no Bedrock files.

## Contents
- `blockbench/` - 20 editable `.bbmodel` files (Java Block/Item format, texture embedded).
- `resourcepack/assets/%s/models/block/` - Java block model JSON (one per decoration).
- `resourcepack/assets/%s/models/item/` - item models (parent = block model; display transforms inherited).
- `resourcepack/assets/soulbound_decor/items/` - item definitions for 1.21.4+ (`minecraft:model`). Ignore on older versions.
- `resourcepack/assets/soulbound_decor/textures/block/` - PNG textures (32x32 / 128x128).
- `previews/` - one preview per asset; `overview_sheet.png` - all 20.
- `asset_manifest.json` - machine-readable manifest.

## Integration notes
- All models fit inside one block (0-16 units); no oversized models, so standard Java block JSON is used throughout.
- Front of every animal/statue points **north (-Z)**. Apply blockstate `y` rotation as needed.
- Each model uses one texture, referenced as `#0` and `particle` -> `soulbound_decor:block/<name>`; `texture_size` is set in each JSON.
- UVs are in the 0-16 JSON range, scaled from texture pixels, tile-based (each material owns one tile; faces crop the tile to roughly 1 texel per model unit).
- **Glow**: 'glow' materials are painted bright, not emissive. Real light emission/lighting is for the integration developer.
- **Translucency**: aquarium, tears/water, glass windows use alpha textures; register those blocks with a translucent/cutout render layer.
- **Collision/hitboxes**: not provided; thin parts (antlers, chains, wings) need hand-defined shapes.
- Chandelier (#6) is designed to hang from the block above (top chain at y=16).
- `ambientocclusion` is false in all block models.

## Assets

| # | Mob | Decoration | Key | Tex | Elements | Size (px W x H x D) | Placement |
|---|---|---|---|---|---|---|---|
| 1 | 41 The Forsaken Fawn | Crystal Fawn Memorial | `crystal_fawn_memorial` | 128x128 | 61 | 10.3 x 15.9 x 11.9 | Floor/pedestal statue; front (face) toward north (-Z). |
| 2 | 41 The Forsaken Fawn | Fountain of Lost Tears | `fountain_of_lost_tears` | 32x32 | 76 | 15.2 x 14.0 x 15.2 | Floor fountain, full-block footprint, symmetrical. |
| 3 | 42 The Weeping Tortoise | Miniature Cathedral Shell | `miniature_cathedral_shell` | 128x128 | 67 | 11.6 x 14.6 x 15.1 | Floor decoration; tortoise head faces north (-Z). |
| 4 | 42 The Weeping Tortoise | Bell of Forgotten Souls | `bell_of_forgotten_souls` | 32x32 | 80 | 14.0 x 14.8 x 5.0 | Floor arch; bell swings along X, arch faces north/south. |
| 5 | 43 The Forgotten Moonmoth | Celestial Moonmoth Sculpture | `celestial_moonmoth_sculpture` | 32x32 | 74 | 14.0 x 15.6 x 10.4 | Pedestal statue; wings spread in the X/Y plane, front faces north. |
| 6 | 43 The Forgotten Moonmoth | Moonlit Butterfly Chandelier | `moonlit_butterfly_chandelier` | 32x32 | 79 | 11.7 x 15.6 x 3.4 | Ceiling-hung (attach top chain to block above); crescent in X/Y plane. |
| 7 | 44 The Forsaken River Otter | Otter's Pearl Fountain | `otters_pearl_fountain` | 32x32 | 70 | 15.2 x 15.5 x 15.2 | Floor fountain; otter faces north (-Z). |
| 8 | 44 The Forsaken River Otter | Forgotten Watermill Miniature | `forgotten_watermill_miniature` | 32x32 | 72 | 15.0 x 11.2 x 15.0 | Tabletop/floor miniature; stream runs along Z, wheel on east side. |
| 9 | 45 The Broken Star Owlet | Celestial Owl Observatory Statue | `celestial_owl_observatory_statue` | 32x32 | 53 | 15.2 x 15.6 x 10.0 | Pedestal statue; owl faces north (-Z), halo behind. |
| 10 | 45 The Broken Star Owlet | Astronomer's Star Globe | `astronomers_star_globe` | 32x32 | 118 | 13.7 x 15.9 x 13.7 | Pedestal globe, symmetrical. |
| 11 | 46 The Shattered Pangolin | Porcelain Pangolin Figurine | `porcelain_pangolin_figurine` | 32x32 | 95 | 12.6 x 12.2 x 12.4 | Tabletop figurine; head faces north (-Z). |
| 12 | 46 The Shattered Pangolin | Sapphire Ceramic Bell Tower | `sapphire_ceramic_bell_tower` | 32x32 | 65 | 12.8 x 16.0 x 12.8 | Floor tower, symmetrical, full block height. |
| 13 | 47 The Lonely Red Panda | Sleeping Red Panda Statue | `sleeping_red_panda_statue` | 32x32 | 40 | 13.6 x 5.8 x 13.6 | Floor/tabletop statue; head faces north (-Z). |
| 14 | 47 The Lonely Red Panda | Golden Bamboo Lantern Shrine | `golden_bamboo_lantern_shrine` | 32x32 | 72 | 15.5 x 13.4 x 13.0 | Floor shrine; open front faces north/south. |
| 15 | 48 The Weeping Axolotl | Crystal Axolotl Aquarium | `crystal_axolotl_aquarium` | 32x32 | 56 | 15.0 x 11.8 x 15.0 | Floor tank; contains translucent glass/water faces; axolotl faces north. |
| 16 | 48 The Weeping Axolotl | Broken Heart Pearl Monument | `broken_heart_pearl_monument` | 32x32 | 54 | 11.0 x 16.0 x 6.5 | Floor monument; frame faces north/south. |
| 17 | 49 The Frostbound Snow Leopard | Sleeping Snow Leopard Sculpture | `sleeping_snow_leopard_sculpture` | 32x32 | 57 | 13.0 x 7.0 x 14.3 | Floor statue; head faces north (-Z). |
| 18 | 49 The Frostbound Snow Leopard | Frozen Royal Crystal Throne | `frozen_royal_crystal_throne` | 32x32 | 48 | 12.0 x 16.0 x 12.0 | Floor throne; seat faces north (-Z). |
| 19 | 50 The Last Lullaby | Golden Elephant Music Box | `golden_elephant_music_box` | 32x32 | 60 | 15.6 x 11.2 x 15.2 | Tabletop music box; elephant faces north, crank on east. |
| 20 | 50 The Last Lullaby | Forgotten Dream Carousel | `forgotten_dream_carousel` | 128x128 | 173 | 15.8 x 16.0 x 15.8 | Floor miniature, rotationally symmetric. |

## Verification performed
- All JSON parsed; every texture exists with the stated size; all JSON UVs within 0-16; all .bbmodel UVs within texture resolution.
- Element rotations are single-axis, in 22.5-degree steps up to 45 (Java limit); coordinates within -16..32.
- ZIP integrity tested; exactly 20 assets, 2 per mob (41-50).

## NOT tested - please read
- **None of the files were opened in Blockbench or loaded in Minecraft.** Previews were drawn by a custom software renderer, so in-game lighting, UV orientation and translucency sorting may differ.
- Exact target Minecraft version was not specified; no `pack.mcmeta` is included. The `items/` definitions are only valid for 1.21.4+.
- Textures are procedurally painted tile palettes (stone, crystal, porcelain, fur, etc.), not hand-pixelled per asset; expect to touch up if you want bespoke detail.
- Blockbench `.bbmodel` structure was written to the documented format; if Blockbench complains, the block JSON is the authoritative export.