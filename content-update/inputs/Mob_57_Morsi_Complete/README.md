# MOB 57 - MORSI + MORSI'S MOBILE PHONE SHOP
Al Ostoura faction, Soul Bound Dimension. Asset creation only: one character, one structure, no Java, no mod, no extra mobs.

## IMPORTANT - about the likeness
I could not look at any reference images or footage of the series, and I do not know the actor's face. Morsi is therefore a **stylized Minecraft character built from the traits in your brief** (Egyptian/wheat skin tone, short black hair with a small quiff, thick black brows, large dark expressive eyes, slightly nervous face with light stubble, fitted teal button shirt, dark trousers, white sneakers, slim average build, separate phone). It is **not** a verified likeness of the actor or of the on-screen character. If you have reference stills, repaint the head-front region of `Morsi_Texture.png` (and hair cubes) - the rig (brows, lids, eyes, mouth) will keep working.

## Contents
```
Character/  Morsi.bbmodel  Morsi.geo.json  Morsi.animation.json  Morsi_Texture.png (128x128)  Morsi_Glowmask.png (phone screen)  Morsi_Preview.png
Structure/  Morsi_Mobile_Shop.nbt  _blueprint.json  _layers.txt  _Preview.png  _Facade.png  Morsi_Sign_Bitmap.png  Block_Palette.json
Docs/       source_generators/ (Python used to generate and check everything; not game code)
```

## Character
- `geometry.morsi`, format 1.12.0, **29 bones / 51 cubes**, standard 32-32.5 px human proportions (not villager anatomy). Texture 128x128.
- **Per-face UVs at 2x texel density** (head face = 16x16 texels) for sharper face/clothing pixel art; left/right limbs share UV boxes. The .bbmodel therefore has box UV turned off.
- Articulated: waist, torso, head, upper arm / forearm / hand / fingers / thumb per arm, thigh / shin / foot per leg. Facial rig: `brow_l/r` (rotate), `lid_l/r` (move down 1px to blink), `eye_l/r` (shift for glances), `mouth` (scale). Hair, nose, ears, collar, belt, pocket and watch are separate cubes.
- `phone` is a separate accessory bone on the right hand; it is hidden (scale 0) in every animation except `phone_call` and `summoned_arrival`. In Blockbench's bind pose it is visible.

### Animations (exactly 10, GeckoLib 1.8.0 format, `animation.morsi.<name>`)
| name | length | mode / content |
|---|---|---|
| idle | 4.4s | loop - breathing, glances left then right, blinks, left hand tugs shirt hem |
| walk | 1.1s | loop - relaxed street walk with hip/shoulder sway |
| run | 0.6s | loop - fast nervous sprint, wide-eyed |
| phone_call | 4.0s | once - pulls out phone, raises to ear, talks (mouth + free-hand gestures), lowers |
| argument | 3.6s | once - alternating expressive two-hand gestures, brow/mouth acting |
| punch_attack | 1.5s | once - stance, wind-up, cross strike with step, follow-through, recovery |
| defensive_counter | 1.9s | once - guard, lean-back evade/parry, counter strike, recovery |
| nervous_reaction | 2.6s | once - steps backward with anxious hand gestures and trembling, blinks |
| summoned_arrival | 3.8s | once - pops in (squash/stretch), fumbles phone, checks it and taps, looks around |
| death | 3.4s | hold - stagger, hand to chest, knees buckle, falls onto back, eyes closed (non-graphic); ground contact computed |
Rotations are additive to the bind pose. Death's final root rotation is -88 degrees (lying on the back).

## Structure - MORSI'S MOBILE PHONE SHOP
- `Morsi_Mobile_Shop.nbt`: gzip structure NBT, **31 x 19 x 29** (x = street width, y = height, z = depth; I read your "31x29x19" as width x depth x height, consistent with your earlier briefs), DataVersion **3955 (Minecraft 1.21.1)**, explicit air, 84 vanilla block ids, no custom blocks. Street level surface = y4; basement y1-3.
- **Morsi spawn: (15, 5, 14)** behind the display/cashier counters. Alternative: sidewalk (15, 5, 6).
- Layout (front/street at z=0): asphalt road with lane marks, sidewalk, street lamps, barrels; three buildings: left pharmacy (green awning, name plate), **Morsi's shop in the middle**, right grocery (yellow awning, produce stand). Rear alley (z23-27) with crates, AC unit, lanterns.
- Morsi's shop: oak glass double door, glass shopfront, red/white striped awning, red fascia, glass-front display counters with phones, cashier counter (lectern POS, cash barrel), wall shelves of coloured phone boxes, repair desk (brewing stand, grindstone, lamp, tool trapdoors), customer bench, tiny back office (desk, chair, files, safe chest, rug hiding a trapdoor), narrow stone staircase up; 2nd-floor apartment (living room with sofa, rug, TV, bookshelf, kitchenette; bedroom with bed, desk, chest), balcony with iron railing and flower pots; second narrow staircase to a rooftop terrace with 3 satellite dishes, water tank, seats, laundry line; rooftop billboard sign.
- Hidden basement: trapdoor under the office rug at (11,4,20) -> ladder -> storage room (box shelves, crates, loot chests, lanterns). **Secret exit:** the bookshelf at (15,1..2,22) in the basement's back wall hides a tunnel to a ladder and an iron-trapdoor manhole in the rear alley at (15,4,25) - break the bookshelf.
- Neighbouring shops are walkable (counter, shelves, stairs, upstairs flat, balconies with laundry, roofs with tanks/dishes).
- **Arabic sign "مرسي للمحمول"**: rooftop billboard (31 wide, 5 block-rows). The text was shaped by Raqm (correct Arabic joining) and rasterised to block resolution; each block row is split into half-rows using top/bottom quartz slabs. **Honest note:** at this resolution (about 2-3 blocks per letter) it is stylised and only marginally legible up close - see `Morsi_Sign_Bitmap.png` for the exact bitmap and `Morsi_Mobile_Shop_Facade.png` for the street elevation. Neighbour plates read "صيدلية" and "بقالة" at an even smaller size. For a crisper sign, replace the billboard with a resource-pack font/sign.
- Phones/accessories are block stand-ins: `polished_blackstone_button` (phones), `iron_trapdoor` (tablets/laptops), coloured concrete (phone boxes), lecterns, lamps. Loot tables used: `chests/village/village_plains_house`, `chests/simple_dungeon`.

Block counts:
| block | count |
|---|---|
| minecraft:stone | 2093 |
| minecraft:stone_bricks | 704 |
| minecraft:smooth_sandstone | 580 |
| minecraft:gravel | 457 |
| minecraft:dirt | 360 |
| minecraft:orange_terracotta | 277 |
| minecraft:light_gray_terracotta | 262 |
| minecraft:black_concrete | 212 |
| minecraft:spruce_planks | 202 |
| minecraft:dark_oak_planks | 177 |
| minecraft:red_concrete | 161 |
| minecraft:smooth_stone | 117 |
| minecraft:terracotta | 117 |
| minecraft:white_concrete | 115 |
| minecraft:glass_pane | 86 |
| minecraft:polished_andesite | 75 |
| minecraft:light_gray_concrete | 71 |
| minecraft:cobblestone | 62 |
| minecraft:mossy_stone_bricks | 53 |
| minecraft:quartz_block | 52 |
| minecraft:cracked_stone_bricks | 45 |
| minecraft:iron_bars | 43 |
| minecraft:yellow_concrete | 43 |
| minecraft:coarse_dirt | 42 |
| minecraft:dark_oak_fence | 39 |
| minecraft:quartz_slab | 37 |
| minecraft:white_wool | 36 |
| minecraft:lime_concrete | 33 |
| minecraft:green_concrete | 31 |
| minecraft:barrel | 31 |
| minecraft:stone_brick_wall | 28 |
| minecraft:oak_door | 24 |
| minecraft:quartz_stairs | 20 |
| minecraft:dark_oak_stairs | 18 |
| minecraft:bookshelf | 16 |
| minecraft:red_wool | 14 |
| minecraft:red_carpet | 14 |
| minecraft:orange_concrete | 14 |
| minecraft:stone_brick_stairs | 12 |
| minecraft:oak_fence | 11 |
| minecraft:lantern | 11 |
| minecraft:polished_blackstone_button | 11 |
| minecraft:iron_trapdoor | 11 |
| minecraft:green_wool | 10 |
| minecraft:yellow_wool | 10 |
| minecraft:polished_blackstone_slab | 10 |
| minecraft:light_blue_concrete | 9 |
| minecraft:flower_pot | 9 |
| minecraft:glass | 8 |
| minecraft:chain | 8 |
| minecraft:brown_carpet | 7 |
| minecraft:blue_concrete | 7 |
| minecraft:chest | 6 |
| minecraft:cauldron | 6 |
| minecraft:sea_lantern | 6 |
| minecraft:ladder | 6 |
| minecraft:purple_concrete | 6 |
| minecraft:hay_block | 6 |
| minecraft:smooth_quartz | 5 |
| minecraft:lightning_rod | 5 |
| minecraft:magenta_concrete | 5 |
| minecraft:brown_concrete | 5 |
| minecraft:cyan_concrete | 4 |
| minecraft:candle | 4 |
| minecraft:birch_door | 4 |
| minecraft:oak_slab | 4 |
| minecraft:pumpkin | 4 |
| minecraft:dark_oak_pressure_plate | 3 |
| minecraft:melon | 3 |
| minecraft:green_bed | 2 |
| minecraft:blue_wool | 2 |
| minecraft:cobweb | 2 |
| minecraft:blue_bed | 2 |
| minecraft:dark_oak_slab | 2 |
| minecraft:crafting_table | 2 |
| minecraft:orange_bed | 2 |
| minecraft:dark_oak_trapdoor | 1 |
| minecraft:brewing_stand | 1 |
| minecraft:redstone_lamp | 1 |
| minecraft:grindstone | 1 |
| minecraft:cyan_stained_glass | 1 |
| minecraft:lectern | 1 |
| minecraft:smoker | 1 |

## Verification done / NOT done
Done (`Docs/source_generators/verify.py`): NBT re-read by an independent parser (31x19x29, 17,081 unique positions); block-state properties checked against my own vanilla table; spawn cell/floor/headroom, doors, hatch, ladders, bookshelf and manhole positions; flood-fill connectivity street -> shop -> office -> apartment -> roof, basement via hatch, alley via rear door, neighbours' interiors and upper floors, and tunnel reachable only after the bookshelf is broken; geo bone parents, per-face UV ranges, texture 128x128, exactly 10 animations with valid bones and key times.
**NOT tested** (no Minecraft/Blockbench/GeckoLib here): loading the .nbt in-game and block-id validity on 1.21.1; fence/pane/wall connection shapes (also set explicitly but unverified); opening the .bbmodel in Blockbench (hand-written, rotation x/y and animation position x stored negated per my understanding of Blockbench's Bedrock convention - re-import `Morsi.animation.json` if mirrored); GeckoLib playback and per-face UV handling in your loader; rotation signs for arms/head (checked only in my own renderer, which assumes +X pitches the top forward and Z*Y*X order); the facial-likeness question above. Previews are from my own renderers. The idle animation is dense (991 keys) for smoothness. The flood-fill treats stairs/ladders/air as passable, so it shows routes exist, not that every jump is comfortable.
