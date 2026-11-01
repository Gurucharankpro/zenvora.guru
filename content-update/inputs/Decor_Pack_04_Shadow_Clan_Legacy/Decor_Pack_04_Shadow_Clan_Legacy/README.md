# Decor Pack 04 - The Shadow Clan Legacy

Exactly **20** static decorative block-model assets inspired by MOBS 20-30. No Java code, no mobs, no gameplay logic.

## Package layout
```
blockbench/<id>.bbmodel                          editable Blockbench projects (texture embedded)
assets/decor_pack_04/models/block/<id>.json        Minecraft Java block models
assets/decor_pack_04/textures/block/<id>.png      textures (64x64, or 128x128 for centerpieces)
previews/NN_<id>.png                            individual previews
overview_all_20.png                            all 20 on one sheet
```

## Integration notes

- **Namespace:** models reference `decor_pack_04:block/<id>`. If your mod id differs, find/replace `decor_pack_04` in the JSON files and move the `assets/decor_pack_04` folder to `assets/<modid>`.
- **Facing:** every model faces **NORTH (-Z)** with no rotation. Use blockstate `y` rotation (0/90/180/270) for facing variants.
- **Coordinates:** authored in 1/16 block units. Several models extend beyond 0-16 (allowed range is -16..32 in Java 1.14+). Models are centred on x=8, z=8 of the origin block; see the Extent column. Larger ones need either a multi-block structure with a custom render, or a block entity/baked-model renderer, or a plain block with a custom (larger) outline/collision shape.
- **Rotations:** only single-axis rotations in 22.5 degree steps (vanilla-legal).
- **Textures:** one atlas per model, 4x4 grid of tiles; UVs are explicit per face (no box-UV). Render type: solid (no transparency needed except unused tiles).
- **Hanging items** (lantern, banner): attach their top (y=32) to a ceiling block.
- **Emissive candidates** (optional, for the other developer): crystals, eyes, eclipse, moon, Sharingan/Rinnegan faces. Purely cosmetic - no functionality is implied.
- **Suggested collision:** a simple bounding box around the Extent below is enough for all assets.
- `display` transforms are provided for gui/ground/fixed/third-person and auto-scaled; adjust as desired.

## Assets

| # | Asset | Source mob | ID / filename stem | Extent (units X x Y x Z) | ~Blocks | Texture | Cubes |
|---|---|---|---|---|---|---|---|
| 1 | Throne of the Crownless King | MOB 20 - The Crownless Sovereign | `throne_of_the_crownless_king` | 28.0 x 32.1 x 14.0 | 1.7 x 2.0 x 0.9 | 128x128 | 40 |
| 2 | Crimson Raven Monument | MOB 21 - The Crimson Raven | `crimson_raven_monument` | 30.8 x 21.6 x 16.0 | 1.9 x 1.4 x 1.0 | 64x64 | 36 |
| 3 | Six-Armed Shadow Idol | MOB 22 - The Void Weaver | `six_armed_shadow_idol` | 32.9 x 30.3 x 12.0 | 2.1 x 1.9 x 0.8 | 64x64 | 41 |
| 4 | Blood Moon Shrine | MOB 23 - The Blood Moon Monk | `blood_moon_shrine` | 26.0 x 23.5 x 16.3 | 1.6 x 1.5 x 1.0 | 64x64 | 24 |
| 5 | Sharingan Eye Monument | MOB 24 - Itachi Uchiha | `sharingan_eye_monument` | 32.0 x 31.0 x 10.0 | 2.0 x 1.9 x 0.6 | 64x64 | 21 |
| 6 | Itachi's Raven Lantern | MOB 24 - Itachi Uchiha | `itachis_raven_lantern` | 15.0 x 32.0 x 15.0 | 0.9 x 2.0 x 0.9 | 64x64 | 27 |
| 7 | Rinnegan Pillar | MOB 25 - Pain | `rinnegan_pillar` | 16.8 x 32.0 x 16.4 | 1.1 x 2.0 x 1.0 | 128x128 | 20 |
| 8 | Six Paths Ceremonial Altar | MOB 25 - Pain | `six_paths_ceremonial_altar` | 16.3 x 15.2 x 16.3 | 1.0 x 0.9 x 1.0 | 64x64 | 44 |
| 9 | Origami Angel Sculpture | MOB 26 - Konan | `origami_angel_sculpture` | 30.2 x 22.9 x 10.3 | 1.9 x 1.4 x 0.6 | 64x64 | 28 |
| 10 | Thousand Paper Flower Wall | MOB 26 - Konan | `thousand_paper_flower_wall` | 16.8 x 16.8 x 5.5 | 1.0 x 1.0 x 0.3 | 64x64 | 47 |
| 11 | Kamui Spiral Portal Frame | MOB 27 - Obito Uchiha | `kamui_spiral_portal_frame` | 26.7 x 28.3 x 5.2 | 1.7 x 1.8 x 0.3 | 64x64 | 23 |
| 12 | Orange Spiral Mask Relic | MOB 27 - Obito Uchiha | `orange_spiral_mask_relic` | 14.1 x 22.6 x 12.0 | 0.9 x 1.4 x 0.8 | 64x64 | 13 |
| 13 | Madara's War Fan Monument | MOB 28 - Madara Uchiha | `madaras_war_fan_monument` | 26.0 x 29.0 x 5.5 | 1.6 x 1.8 x 0.3 | 64x64 | 14 |
| 14 | Susanoo Guardian Statue | MOB 28 - Madara Uchiha | `susanoo_guardian_statue` | 26.5 x 31.0 x 14.0 | 1.7 x 1.9 x 0.9 | 128x128 | 26 |
| 15 | Clay Dragon Sculpture | MOB 29 - Deidara | `clay_dragon_sculpture` | 17.1 x 24.7 x 30.4 | 1.1 x 1.5 x 1.9 | 128x128 | 39 |
| 16 | Explosive Clay Art Display | MOB 29 - Deidara | `explosive_clay_art_display` | 16.0 x 18.0 x 12.0 | 1.0 x 1.1 x 0.8 | 64x64 | 33 |
| 17 | Crimson Puppet Display | MOB 30 - Sasori | `crimson_puppet_display` | 16.0 x 32.0 x 16.0 | 1.0 x 2.0 x 1.0 | 64x64 | 43 |
| 18 | Scorpion Tail Monument | MOB 30 - Sasori | `scorpion_tail_monument` | 10.8 x 32.0 x 13.7 | 0.7 x 2.0 x 0.9 | 64x64 | 24 |
| 19 | Akatsuki Crimson Cloud Banner | SHARED FACTION | `akatsuki_crimson_cloud_banner` | 21.0 x 31.5 x 2.4 | 1.3 x 2.0 x 0.1 | 64x64 | 21 |
| 20 | The Shadow Council Table | SHARED FACTION | `shadow_council_table` | 33.5 x 21.3 x 33.2 | 2.1 x 1.3 x 2.1 | 64x64 | 75 |

## Per-asset notes

**1. Throne of the Crownless King** (MOB 20 - The Crownless Sovereign) - Enormous black obsidian throne with a broken floating crown, silver chains and violet crystals.  
Bounds: x -6.0..22.0, y 0.0..32.1, z 0.0..14.0. Spans 2 blocks wide, 2 tall. Crown and crystals are purely visual; crystals/rune stripe are good emissive candidates.

**2. Crimson Raven Monument** (MOB 21 - The Crimson Raven) - Black raven statue with spread wings, a red crystal heart and a gothic stone pedestal.  
Bounds: x -7.4..23.4, y 0.0..21.6, z 1.6..17.6. Wingspan exceeds one block; crystal heart is an emissive candidate. Beak faces north.

**3. Six-Armed Shadow Idol** (MOB 22 - The Void Weaver) - Dark stone statue with six arms, a mysterious white mask and six ornamental crescent blades.  
Bounds: x -8.4..24.4, y 0.0..30.3, z 2.0..14.0. Statue is ~1.4 blocks wide. Blades are decorative, thin and non-functional. Mask faces north.

**4. Blood Moon Shrine** (MOB 23 - The Blood Moon Monk) - Circular ceremonial shrine with a giant crimson lunar disc, four glowing eyes and a suspended ritual staff.  
Bounds: x -5.0..21.0, y 0.0..23.5, z -0.1..16.1. Footprint ~1.5 blocks wide; faces north. Moon and eyes are emissive candidates.

**5. Sharingan Eye Monument** (MOB 24 - Itachi Uchiha) - Large blackstone monument with a glowing red Sharingan symbol surrounded by carved raven feathers.  
Bounds: x -8.0..24.0, y 0.0..31.0, z 3.0..13.0. Faces north; ~2 blocks tall. Eye plate (red) is an emissive candidate.

**6. Itachi's Raven Lantern** (MOB 24 - Itachi Uchiha) - Hanging Japanese lantern decorated with black ravens, crimson glass and red magical ornaments.  
Bounds: x 0.5..15.5, y 0.0..32.0, z 0.5..15.5. Hanging decoration: attach the top (y=32) to a ceiling. Glass is opaque in the texture; mark as emissive if desired.

**7. Rinnegan Pillar** (MOB 25 - Pain) - Monumental black stone column with a giant violet Rinnegan eye surrounded by six concentric sculpted rings.  
Bounds: x -0.4..16.4, y 0.0..32.0, z -0.4..16.0. 1 block wide, 2 tall; faces north. Violet rings and eye are emissive candidates.

**8. Six Paths Ceremonial Altar** (MOB 25 - Pain) - Circular altar with six miniature stone pedestals, dark metal rods and a glowing central violet crystal.  
Bounds: x -0.1..16.1, y 0.0..15.2, z -0.1..16.1. Footprint 1 block; ~1 block tall. Crystal and rod beads are emissive candidates.

**9. Origami Angel Sculpture** (MOB 26 - Konan) - Statue of an angelic figure with enormous folded-paper wings, a pale-blue flower and intricate paper geometry.  
Bounds: x -7.1..23.1, y 0.0..22.9, z 2.7..13.0. Faces north; wings sweep behind (south). ~1.5 blocks wide.

**10. Thousand Paper Flower Wall** (MOB 26 - Konan) - Wall panel covered with layered white origami flowers and blue paper butterflies.  
Bounds: x -0.4..16.4, y -0.4..16.4, z 10.5..16.0. Wall decoration 1x1 block; back at south (z=16), front faces north. Place flush against a wall.

**11. Kamui Spiral Portal Frame** (MOB 27 - Obito Uchiha) - Black-and-orange decorative portal frame formed from twisted concentric spiral segments.  
Bounds: x -5.3..21.3, y 0.0..28.3, z 5.4..10.6. Purely visual frame (no portal logic). ~1.5 blocks wide x 1.75 tall; faces north/south.

**12. Orange Spiral Mask Relic** (MOB 27 - Obito Uchiha) - Large sculpture of Obito's spiral mask on an ornate blackstone pedestal.  
Bounds: x 0.9..15.1, y 0.0..22.6, z 2.0..14.0. 1 block footprint, ~1.4 blocks tall; mask faces north.

**13. Madara's War Fan Monument** (MOB 28 - Madara Uchiha) - Giant decorative Gunbai war fan displayed against a dark stone wall with crimson ribbons.  
Bounds: x -5.0..21.0, y 0.0..29.0, z 11.0..16.5. Wall piece 1.5 blocks wide x ~1.7 tall; back at south (z=16); faces north. Fan is decorative only.

**14. Susanoo Guardian Statue** (MOB 28 - Madara Uchiha) - Purple crystalline armored guardian bust with elaborate helmet, shoulders and glowing eyes.  
Bounds: x -5.2..21.2, y 0.0..31.0, z 1.0..15.0. ~1.5 blocks wide, 2 tall; faces north. Eyes and crystal plates are emissive candidates.

**15. Clay Dragon Sculpture** (MOB 29 - Deidara) - Large white clay dragon statue with folded wings, a segmented tail and sculpted scales.  
Bounds: x -0.6..16.6, y 0.0..24.7, z 0.6..31.0. 1 block wide, ~2 blocks long (north-south), ~1.5 tall; head faces north.

**16. Explosive Clay Art Display** (MOB 29 - Deidara) - Artist's table displaying miniature clay birds, spiders, dragons and a carved clay emblem. Purely ornamental.  
Bounds: x 0.0..16.0, y 0.0..18.0, z 2.0..14.0. 1 block footprint, ~1.1 blocks tall; faces north. Ornamental figurines only.

**17. Crimson Puppet Display** (MOB 30 - Sasori) - Articulated-looking wooden marionette suspended inside an ornamental black-and-red exhibition frame.  
Bounds: x 0.0..16.0, y 0.0..32.0, z 0.0..16.0. 1 block wide, 2 tall; faces north. Strings are 0.3-unit thin boxes.

**18. Scorpion Tail Monument** (MOB 30 - Sasori) - Segmented mechanical scorpion-tail sculpture wrapped around an ancient stone pillar.  
Bounds: x 2.6..13.4, y 0.0..32.0, z -0.3..13.4. 1 block footprint, 2 tall; stinger points north. Purely ornamental.

**19. Akatsuki Crimson Cloud Banner** (SHARED FACTION) - Three-dimensional hanging black banner with red cloud emblems, silver trim and ornamental chains.  
Bounds: x -2.5..18.5, y 0.5..32.0, z 6.8..9.2. Hanging decoration; attach chains to ceiling (y=32). Double-sided emblems. 1 block wide, 2 tall.

**20. The Shadow Council Table** (SHARED FACTION) - Circular blackstone meeting table with eleven decorative seats, crimson-cloud engravings, a glowing central eclipse and silver detailing.  
Bounds: x -8.8..24.8, y 0.0..21.3, z -8.9..24.3. ~2x2 blocks footprint (seat ring ~34 units wide), 1 block tall. Leader seat faces south from the north edge. Eclipse is an emissive candidate.

## Honest verification status

- Automatically verified here: all 20 JSON models, 20 bbmodels and 20 PNGs parse/load; UVs are within 0-16; cube coordinates within -16..32; rotations are legal; texture sizes match `texture_size`; ZIP integrity test passed.
- Previews were produced by a custom software renderer that reads the exported JSON + PNG files directly, so they reflect the real exports. They are not in-engine screenshots.
- **NOT tested:** opening the .bbmodel files in Blockbench itself, and loading the models in Minecraft. The .bbmodel is written in the Blockbench 4.x `java_block` project format; if Blockbench complains, use File > Import > Java Block Model on the JSON instead.
- Textures are procedurally generated pixel art (hand-tuned painters, not drawn by hand in an editor); character symbols (Sharingan, Rinnegan, spiral, cloud, etc.) are stylised 16x16 motifs scaled per tile. Touch up in Blockbench/Aseprite if you want finer detail.
- Unused atlas tiles are transparent.