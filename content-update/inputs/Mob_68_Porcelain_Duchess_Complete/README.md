# MOB 68 - THE PORCELAIN DUCHESS + Palace of the Silent Duchess
Eclipse Syndicate - one original boss mob + one exclusive structure. Assets only (no Java, no mod).

## Contents of this zip
| File | What it is |
|---|---|
| `Porcelain_Duchess.bbmodel` | Editable Blockbench project (Bedrock Entity format, 189 bones / 301 cubes, texture embedded, all 10 animations + sound/particle hook tracks) |
| `Porcelain_Duchess.geo.json` | Bedrock/GeckoLib geometry (`geometry.porcelain_duchess`, format 1.12.0, per-face UV, locators) |
| `Porcelain_Duchess.animation.json` | 10 GeckoLib animations (format 1.8.0) |
| `Porcelain_Duchess_Texture.png` | **128 x 128** RGBA atlas, hand-painted procedural pixel art |
| `Porcelain_Duchess_Texture_glowmask.png` | optional **128 x 128** emissive mask (eyes, ruby heart, crown rubies, halo/beams, FX) - GeckoLib `_glowmask` naming |
| `Porcelain_Duchess_Preview.png` | software render of bind pose (3 views) + 5 combat poses |
| `Palace_Of_Silent_Duchess.nbt` | real Java structure template, **49 x 33 x 49**, DataVersion 3465 (Minecraft 1.20.1) |
| `Palace_Structure_Preview.png` | isometric exterior, cutaway and 4 floor plans |
| `Palace_Block_Palette.md` | full block palette with counts, roles, custom-block substitutions |
| `Palace_Blueprint_Layers.txt` | complete layer-by-layer block-position blueprint (same data as the .nbt) |
| `README.md` | this file |

## The creature
Ivory porcelain queen with cracked-and-gold-repaired face, ruby luminous eyes, silver royal curls + updo, broken gold crown with 5 floating rubies and a glow halo, burgundy Victorian gown (4 tiers x 8 articulated panels, brocade + black lace + gold embroidery, 3 apron panels), gold mechanical joints (wrist, elbow, neck, belt), crimson back bow (2 loops + 2 tails), ruby heart behind a gold ornamental opening, gold folding fan (9 articulated blades), Eclipse Syndicate insignia (silver eye around a black sun) on the tier-2 front gown panel, 6 floating porcelain mirror fragments, 4 silk ribbons (4 segments each), 5 porcelain butterflies with flapping wings. Fingers: 5 per hand, 2 phalanges each (20 bones). No gore anywhere.

Approx. size: 3.6 blocks tall (to crown), skirt radius ~1 block. Suggested hitbox 1.4 x 3.6. Front of the model is **-Z** (Blockbench "north").

### Bone map (for hooks / extra animation)
`root > body > torso > (heart, bow > bow_loop_L/R, bow_tail_L/R, neck > head > (crown > crown_gem_1..5, crown_halo; curl_L1-3, curl_R1-3), arm_R > forearm_R > hand_R > (hand_R_f1a..f5b, fan_root > fan_blade_1..9), arm_L > ...)`
`root > skirt > skirt_core, sk{tier 1-4}_y{0-7} (static yaw) > sk{tier}_p{0-7} (animated panel) > apron_*`
`root > mirror_root > mirror_1..6 > mirror_N_beam` - `root > rb1..4_yaw > rbK_1..4` - `root > bf1..5 > bfN_wL/wR`
FX / props (scale 0 in every animation except the one that uses them): `fx_fan_slash, fx_waltz, fx_shockwave, throne, mirror_N_beam`.
**Important:** these bones are visible in the bind pose (Blockbench shows them). In game keep an animation playing from spawn (idle) so they stay hidden.
Locators: `fan_tip`, `fan_pivot`, `crown_top`, `heart_core`, `mirror_center`, `shockwave_origin`, `throne_seat`.

### Animations (names in the animation file are `animation.porcelain_duchess.<name>`)
| Animation | Length | loop | bones keyed | keyframes |
|---|---|---|---|---|
| `animation.porcelain_duchess.idle` | 6.0 s | True | 137 | 3096 |
| `animation.porcelain_duchess.walk` | 1.2 s | True | 134 | 1558 |
| `animation.porcelain_duchess.run` | 0.8 s | True | 132 | 1524 |
| `animation.porcelain_duchess.porcelain_fan_attack` | 2.4 s | False | 134 | 3768 |
| `animation.porcelain_duchess.mirror_shard_storm` | 3.2 s | False | 134 | 4919 |
| `animation.porcelain_duchess.crimson_ribbon_attack` | 3.0 s | False | 132 | 4270 |
| `animation.porcelain_duchess.royal_waltz` | 4.2 s | False | 132 | 6468 |
| `animation.porcelain_duchess.shattered_coronation` | 4.0 s | False | 135 | 7060 |
| `animation.porcelain_duchess.summoned_arrival` | 5.0 s | False | 137 | 5764 |
| `animation.porcelain_duchess.death` | 4.0 s | hold_on_last_frame | 138 | 4134 |

Every attack has anticipation -> execution (fast "snap" easing) -> follow-through -> recovery, with in-model visible effects: `fan_attack` (gold fan opens, crescent slash arc), `mirror_shard_storm` (6 shards expand, turn, launch in staggered volley with beams, return), `crimson_ribbon_attack` (ribbon raise, two opposing sweeps, 4-ribbon slam + ruby ring), `royal_waltz` (720 degree spin, flared gown, orbiting porcelain shards), `shattered_coronation` (crown rubies rise, mirrors expand, ruby shockwave ring), `summoned_arrival` (assembles from fragments, seated on built-in throne, stands, unfolds fan, bows, throne dissolves), `death` (halo dies, rubies fall, ribbons go limp, mirrors drop, gentle slump - non-graphic, holds last frame).

Hook events (names only - bind them to your own particles/sounds; none are shipped):
| Animation | t (s) | type | name | locator |
|---|---|---|---|---|
| `porcelain_fan_attack` | 0.3 | sound | `duchess_fan_unfold` |  |
| `porcelain_fan_attack` | 0.7 | sound | `duchess_fan_swish` |  |
| `porcelain_fan_attack` | 0.72 | particle | `duchess_fan_slash` | fan_tip |
| `porcelain_fan_attack` | 1 | particle | `duchess_ruby_dust` | fan_tip |
| `mirror_shard_storm` | 0.1 | sound | `duchess_mirror_whirl` |  |
| `mirror_shard_storm` | 1 | sound | `duchess_mirror_launch` |  |
| `mirror_shard_storm` | 1.05 | particle | `duchess_mirror_glint` | mirror_center |
| `mirror_shard_storm` | 1.9 | particle | `duchess_shard_impact` | mirror_center |
| `crimson_ribbon_attack` | 0.15 | sound | `duchess_ribbon_rise` |  |
| `crimson_ribbon_attack` | 1 | sound | `duchess_ribbon_sweep` |  |
| `crimson_ribbon_attack` | 1.65 | sound | `duchess_ribbon_sweep` |  |
| `crimson_ribbon_attack` | 2.33 | sound | `duchess_ribbon_slam` |  |
| `crimson_ribbon_attack` | 1.05 | particle | `duchess_ribbon_trail` | shockwave_origin |
| `crimson_ribbon_attack` | 1.7 | particle | `duchess_ribbon_trail` | shockwave_origin |
| `crimson_ribbon_attack` | 2.35 | particle | `duchess_ruby_burst` | shockwave_origin |
| `royal_waltz` | 0.5 | sound | `duchess_waltz_begin` |  |
| `royal_waltz` | 2 | sound | `duchess_waltz_spin` |  |
| `royal_waltz` | 1 | particle | `duchess_porcelain_fragments` | shockwave_origin |
| `royal_waltz` | 3.3 | particle | `duchess_waltz_finish` | shockwave_origin |
| `shattered_coronation` | 0.1 | sound | `duchess_coronation_charge` |  |
| `shattered_coronation` | 1.8 | sound | `duchess_coronation_burst` |  |
| `shattered_coronation` | 0.8 | particle | `duchess_crown_crystals` | crown_top |
| `shattered_coronation` | 1.82 | particle | `duchess_ruby_shockwave` | shockwave_origin |
| `shattered_coronation` | 2.05 | particle | `duchess_ruby_petals` | crown_top |
| `summoned_arrival` | 0 | sound | `duchess_arrival_chime` |  |
| `summoned_arrival` | 3.4 | sound | `duchess_fan_unfold` |  |
| `summoned_arrival` | 4.3 | sound | `duchess_arrival_bow` |  |
| `summoned_arrival` | 1.3 | particle | `duchess_assemble_burst` | shockwave_origin |
| `death` | 0.1 | sound | `duchess_death_crack` |  |
| `death` | 1.4 | sound | `duchess_mirror_shatter` |  |
| `death` | 2.4 | sound | `duchess_death_settle` |  |
| `death` | 0.3 | particle | `duchess_porcelain_flakes` | crown_top |

### Texture atlas (128x128)
Per-face UVs map into named regions: face (0,0,24x27), hair, curl, gown_a/b, lace, bodice, sleeve, cuff, skirt_t1-t4 (32x16 each), trim, petticoat, gold, ruby, heart, gem, ribbon, bow, crown, mirror, fan_blade, butterfly (transparent corners = cut-out wings), insignia, throne, beam/fx. Face region uses ~3 texels per model pixel for detail; skirts ~4x2. No region overlaps (checked).

## Integrating the creature (GeckoLib, Minecraft Java)
1. Copy (lower-case the names - GeckoLib requires it): `assets/<modid>/geo/porcelain_duchess.geo.json`, `assets/<modid>/animations/porcelain_duchess.animation.json`, `assets/<modid>/textures/entity/porcelain_duchess.png`, and the glow mask as `.../porcelain_duchess_glowmask.png`.
2. Geometry id: `geometry.porcelain_duchess`. Animation keys: `animation.porcelain_duchess.idle`, `.walk`, `.run`, `.porcelain_fan_attack`, `.mirror_shard_storm`, `.crimson_ribbon_attack`, `.royal_waltz`, `.shattered_coronation`, `.summoned_arrival`, `.death`.
3. Loop modes already in the file: idle/walk/run loop; the 6 action animations play once; death holds the last frame.
4. Texture needs cut-out rendering (butterfly wings use transparent pixels) - GeckoLib's default entity cutout is fine.

## The structure - THE PALACE OF THE SILENT DUCHESS
**Size 49 (X) x 33 (Y) x 49 (Z). Origin (0,0,0) = north-west-bottom corner. Entrance faces +Z (south).**
Place with a flat 49 x 49 area: `/place template <modid>:palace_of_silent_duchess <x> <y> <z>` (file goes in `data/<modid>/structures/palace_of_silent_duchess.nbt`). Local y=5 is the garden grass layer, local y=6 the garden walking level - choose placement y = terrain_top - 5. The template includes air, so it clears its volume. Soul Bound Dimension: use the same file from your own structure/jigsaw definition.

### Boss arena - THE LAST WALTZ (structure-local coordinates; add your placement origin)
- Arena centre (feet level): **(24, 9, 17)**, interior radius 10, floor layer y=8, ceiling y=26 (clear height 17).
- **Duchess spawn: X 24.5, Y 11.0, Z 9.5, facing south (yaw 0)** - standing on the throne dais (tier-2 top y=11) in the throne cell. Her model's own throne (summoned_arrival) overlaps the physical golden throne (back x23-25, y11-18 at z8; arms at x23 and x25, z9).
- Player entry: hall door (x23-25, z28-29, y9-13) - 11 blocks from the arena centre; secret mirror doors at x13 and x35, z16-18, y9-11.
- Eclipse emblem: black sun (r 3.3) + gold rays, inside a silver almond eye, centred (24,8,17).
- 8 monumental porcelain columns (2x2, y9-24) at (17,14) (21,10) (27,10) (31,14) (31,20) (27,24) (21,24) (17,20) [NW corner of each 2x2]; 6 giant cracked mirrors on the ring wall (y11-20); rose window behind the throne (north wall z=6, centre (24,18)); 8 suspended ruby crystals; chandelier (centre (24,21,17), radius 4, 20 lit candles); crimson-and-gold sector floor.

### Other rooms (structure-local)
| Room | Bounds (x,y,z min -> max) | Notes |
|---|---|---|
| Palace Towers (x4) | (0,5,3) -> (9,32,13) | NW tower center (5,10); SW (5,36); NE (43,10); SE (43,36). Spiral stair y9->y21, top chamber y21-25, gold roofs to y32 |
| W wing mirror corridor (3 floors) | (10,9,8) -> (12,25,37) | 30-block corridor per floor, carpet runner, gold-framed mirror panels, hanging lanterns |
| E wing mirror corridor (3 floors) | (36,9,8) -> (38,25,37) | 30-block corridor per floor, carpet runner, gold-framed mirror panels, hanging lanterns |
| Secret mirror doors to ballroom | (13,9,16) -> (35,11,18) | Light-gray glass mirrors at x=13 and x=35, z16-18, y9-11 (break the glass or swap for piston doors) |
| Entrance Hall (3 stories) | (18,9,30) -> (30,25,37) | Grand hall: carpet runner, 2 giant porcelain dolls (22,33)&(26,33), flight A (x19-20,z37->32) to F2 gallery, flight B (x29-30,z31->36) to F3 balcony, chandelier |
| F2 North Gallery / F3 Front Balcony | (18,14,30) -> (30,21,37) | Balconies above the ballroom door and above the entrance |
| BOSS ARENA - The Last Waltz (circular ballroom) | (14,9,7) -> (34,25,27) | Center (24,9,17) feet-level, radius 10; 8 porcelain columns r=8; 6 giant cracked mirrors; 8 suspended ruby crystals; chandelier; golden throne dais north; Eclipse emblem in floor y=8 |
| Underground Porcelain Workshop | (24,1,20) -> (38,6,31) | Reached by the secret stair (mirror door west corridor x=13,z27-28). Crafting/smithing benches, kilns, shelves, half-built doll |
| Hidden Royal Treasure Vault | (40,1,21) -> (43,5,31) | Secret mirror door in workshop east wall (39,1-2,24-25); 4 loot chests (woodland_mansion loot table), gold/ruby blocks |
| Dressing chambers (F1 west) | (5,9,15) -> (8,13,31) | 2 chambers 4x8 each: gold-framed mirror, vanity, wardrobes, carpet |
| Royal bedrooms (F2 west) | (5,15,15) -> (8,19,31) | 2 canopy royal bedrooms: red beds, crimson-fence canopy, lanterns |
| Porcelain doll display gallery (F3 west) | (5,21,15) -> (8,25,31) | Glass display cases with miniature porcelain dolls |
| Grand Library (F1 east, 4x17) | (40,9,15) -> (43,13,31) | Bookshelf walls, lecterns, enchanting table, lanterns |
| Antique Music Room (F2 east) | (40,15,15) -> (43,19,31) | Note-block wall, jukebox, black/quartz grand piano, harps |
| Doll display salon (F3 east) | (40,21,15) -> (43,25,31) | Glass cases with miniature dolls |
| Rose gardens + marble fountains | (0,5,39) -> (48,10,48) | Front garden: 2 marble fountains (9,43),(39,43); hedged rose/peony/poppy/tulip beds; side & back beds |
| Royal entrance staircase | (17,5,39) -> (31,12,45) | 3 flights of quartz stairs, 15 wide, red carpet runner x23-25, balustrades, gold-capped lamp pillars |

Layout: ground floor feet y=9, second floor y=15, third floor y=21. Corner towers have 12-step spiral stairs (y9 -> y21) with landings on floor 2 and a top chamber on floor 3. Secret passage: mirror door in the west corridor (x13, z27-28, y9-10) -> stair descending east to the underground workshop -> mirror door (x39, z24-25, y1-2) -> treasure vault (4 chests, `minecraft:chests/woodland_mansion`).

### Custom-block substitutions
Everything is vanilla so the file loads anywhere: porcelain = quartz variants, ruby = red stained glass + redstone block, mirrors = light-gray stained glass / tinted glass, gold = gold block. See `Palace_Block_Palette.md`; to use custom blocks edit the palette `Name` strings in the .nbt (or remap on load).

## Verification performed (in the build sandbox)
### Creature files
```
PASS - texture is exactly 128x128 ((128, 128), mode RGBA)
PASS - glow mask is exactly 128x128
PASS - glow mask has 421 emissive pixels (eyes, heart, rubies, halo/beams)
INFO - texture uses 8479/16384 opaque pixels
PASS - geo.json declares 128x128
PASS - 189 unique bone names
PASS - every bone parent is declared before its child
PASS - 301 cubes
PASS - all per-face UV rectangles inside the 128x128 atlas; cube rotations have pivots
PASS - 6 mirror-fragment bones (mirror_1..6)
PASS - 4 independent ribbons x 4 segments (rbK_yaw roots)
PASS - 9 articulated fan blades
PASS - 32 articulated gown panels (4 tiers x 8)
PASS - 20 finger bones (2 hands x 5 fingers x 2 phalanges)
PASS - 5 floating crown rubies
PASS - 5 porcelain butterflies with flapping wings
PASS - exactly 10 animations, names match the brief
PASS - idle                   len=6.0  loop=True               keyframes=3096  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - idle: mirrors/ribbons/fan/crown/gown all keyframed
PASS - idle: loop seam max difference 0.0000
PASS - walk                   len=1.2  loop=True               keyframes=1558  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - walk: mirrors/ribbons/fan/crown/gown all keyframed
PASS - walk: loop seam max difference 0.0000
PASS - run                    len=0.8  loop=True               keyframes=1524  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - run: mirrors/ribbons/fan/crown/gown all keyframed
PASS - run: loop seam max difference 0.0000
PASS - porcelain_fan_attack   len=2.4  loop=False              keyframes=3768  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - porcelain_fan_attack: mirrors/ribbons/fan/crown/gown all keyframed
PASS - porcelain_fan_attack: 84 distinct key times spanning 0..2.4
PASS - mirror_shard_storm     len=3.2  loop=False              keyframes=4919  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - mirror_shard_storm: mirrors/ribbons/fan/crown/gown all keyframed
PASS - mirror_shard_storm: 85 distinct key times spanning 0..3.2
PASS - crimson_ribbon_attack  len=3.0  loop=False              keyframes=4270  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - crimson_ribbon_attack: mirrors/ribbons/fan/crown/gown all keyframed
PASS - crimson_ribbon_attack: 79 distinct key times spanning 0..3.0
PASS - royal_waltz            len=4.2  loop=False              keyframes=6468  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - royal_waltz: mirrors/ribbons/fan/crown/gown all keyframed
PASS - royal_waltz: 184 distinct key times spanning 0..4.2
PASS - shattered_coronation   len=4.0  loop=False              keyframes=7060  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - shattered_coronation: mirrors/ribbons/fan/crown/gown all keyframed
PASS - shattered_coronation: 130 distinct key times spanning 0..4.0
PASS - summoned_arrival       len=5.0  loop=False              keyframes=5764  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - summoned_arrival: mirrors/ribbons/fan/crown/gown all keyframed
PASS - summoned_arrival: 122 distinct key times spanning 0..5.0
PASS - death                  len=4.0  loop=hold_on_last_frame keyframes=4134  animated: mirrors 6/6, ribbon segs 16/16, fan blades 9/9, crown gems 5/5, gown panels 32/32
PASS - death: mirrors/ribbons/fan/crown/gown all keyframed
INFO - total keyframes across the 10 animations: 42561
PASS - bbmodel has 301 elements (= geo cubes)
PASS - bbmodel has the same 10 animations
PASS - bbmodel animators all reference real bones
PASS - bbmodel embeds the 128x128 texture
ALL CHECKS PASSED
```
### Structure
```
PASS - structure size 49 x 33 x 49 (X x Y x Z)
PASS - walkability flood-fill from garden reaches all 31 key rooms (2781 standing cells reachable); unreachable: []
PASS - 4 towers topped by gold spires at y=32 (5,10) (43,10) (5,36) (43,36)
PASS - 8 porcelain columns at [(31, 20), (27, 24), (21, 24), (17, 20), (17, 14), (21, 10), (27, 10), (31, 14)]
PASS - Eclipse emblem black sun at floor centre (24,8,17); silver eye + gold rays around it
PASS - golden throne present (back x23-25,y11-18,z8; arms x23/x25,z9)
PASS - spawn cell (24,11..15,9) is clear air above the dais
PASS - ballroom contains 169 red stained-glass blocks (ruby crystals, rose window, mirrors)
PASS - ballroom chandelier carries 20 lit candles
PASS - 26 secret mirror-door blocks registered: x13 z16, x13 z17, x13 z18, x13 z27, x13 z28, x35 z16, x35 z17, x35 z18 ...
PASS - nbt DataVersion 3465 (Minecraft 1.20.1), 4 loot chests carrying LootTable
PASS - nbt re-read OK: all 79,233 positions present and palette indices valid
ALL PASSED

TARGET REACHABILITY:
  garden start: (24, 6, 47) reachable
  entrance hall floor: (24, 9, 34) reachable
  ballroom center (arena): (24, 9, 17) reachable
  ballroom throne dais: (24, 11, 10) reachable
  F2 north gallery: (24, 15, 30) reachable
  F3 front balcony: (24, 21, 36) reachable
  exterior balcony: (24, 21, 40) reachable
  W corridor F1: (11, 9, 20) reachable
  W corridor F2: (11, 15, 20) reachable
  W corridor F3: (11, 21, 20) reachable
  E corridor F1: (37, 9, 20) reachable
  E corridor F2: (37, 15, 20) reachable
  E corridor F3: (37, 21, 20) reachable
  W dressing chamber A (F1): (8, 9, 19) reachable
  W dressing chamber B (F1): (8, 9, 28) reachable
  W bedroom A (F2): (8, 15, 19) reachable
  W bedroom B (F2): (8, 15, 28) reachable
  W doll gallery (F3): (8, 21, 19) reachable
  E library north (F1): (42, 9, 19) reachable
  E library south (F1): (42, 9, 28) reachable
  E music room (F2): (42, 15, 18) reachable
  E salon (F3): (40, 21, 18) reachable
  NW tower top (F3): (5, 21, 12) reachable
  NE tower top (F3): (43, 21, 12) reachable
  SW tower top (F3): (5, 21, 34) reachable
  SE tower top (F3): (43, 21, 34) reachable
  NW tower ground: (7, 9, 10) reachable
  SE tower ground: (41, 9, 36) reachable
  secret passage landing: (14, 9, 27) reachable
  underground workshop: (30, 1, 26) reachable
  treasure vault: (41, 1, 24) reachable
```

## What is NOT tested / disclosures
- **Nothing was opened in Blockbench, GeckoLib or Minecraft** (no game or network in the build sandbox). The .bbmodel, .geo.json and .animation.json were generated programmatically, parsed back and cross-checked, and previewed with a custom software renderer that follows the Blockbench/GeckoLib bone convention. Residual risks: (a) rotation-sign / east-west face conventions - if a face looks swapped in Blockbench it is the east/west UV pair (those faces use the same region on nearly every cube); (b) animation easing is baked into dense linear keyframes (idle/walk/run use `catmullrom`), so Blockbench's graph editor will show many keys.
- The .nbt was written and re-read by my own NBT code (gzip, 79,233 blocks, palette valid), not loaded by Minecraft. DataVersion 3465 targets 1.20.1; other versions may need a vanilla "upgrade" (open + resave in a structure block). Stair `facing` values assume vanilla semantics (verified by walkability reasoning only).
- Walkability was checked with a flood-fill model (2-high clearance, 1-block step-ups, up to 3-block drops, doors/glass-secrets passable) - not by a real player. Secret mirrors are 1-block glass: players can break them; swap for piston doors if you want true secrecy.
- Fountain water and flower/plant blocks rely on normal vanilla block updates after placement; hanging lanterns/candles depend on supports that exist in the template.
- Sound/particle event names in the animations are hooks only; no particle or sound assets are included.
- Previews are software renders, not in-game screenshots; the 'hidden' FX bones (throne, rings, beams) are shown scale 0 in them.
- The mob has 189 bones / 301 cubes - heavier than a vanilla mob but fine for a boss.
