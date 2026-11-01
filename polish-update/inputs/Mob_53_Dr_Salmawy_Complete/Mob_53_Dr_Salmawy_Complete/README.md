# Mob 53 - Dr. Salmawy (Al Ostoura faction) + Salmawy's Secret Mansion

Exactly ONE character and ONE structure, made as assets only (no Java, no mod code). Intended for the Soul Bound Dimension; nothing in the package depends on it, so it works in any dimension.

## IMPORTANT: about likeness and references
- I cannot view the series or any reference images from here, so this is NOT a verified likeness of the character or of the actor. It is built strictly from your written description: mature Egyptian man, short neat dark hair, serious dark eyes, calm calculating expression, charcoal suit, black overcoat, light-gray open-collar shirt, dark leather shoes, polished wristwatch, signet ring, folded pocket handkerchief.
- Nothing "canonical" was invented: no glasses, mustache, beard, scars or grey hair were added. Choices not in your brief: the watch is on the LEFT wrist and the signet ring on the RIGHT hand (ring finger). Skin tone is a warm medium brown.
- To improve accuracy, send reference stills/descriptions (hair parting, facial hair, ring hand, tie or no tie) and I can adjust the texture (face and hair are painted on the 128x128 sheet and are easy to change).

## Files
| File | Size | Notes |
|---|---|---|
| `Dr_Salmawy.bbmodel` | 884 KB | Editable Blockbench project (GeckoLib Animated Model format, texture embedded, 10 animations) |
| `Dr_Salmawy.geo.json` | 69 KB | Geometry 1.12.0, `geometry.dr_salmawy` |
| `Dr_Salmawy.animation.json` | 121 KB | 10 animations, format 1.8.0, `animation.dr_salmawy.<name>` |
| `Dr_Salmawy_Texture.png` | 6 KB | exactly 128x128, no glowmask (nothing emissive) |
| `Dr_Salmawy_Preview.png` | 56 KB | rendered from the exported files |
| `Salmawy_Secret_Mansion.nbt` | 101 KB | vanilla structure template, 39x25x39, DataVersion 3955 (1.21.1) |
| `Salmawy_Mansion_Preview.png`, `Salmawy_Mansion_Cutaways.png`, `Salmawy_Mansion_Floor_Plans.png` | | exterior, per-floor cutaways, five floor plans (rendered from the NBT) |
| `Salmawy_Mansion_Block_Palette.md` | | every block id with counts |
| `Integration_Optional/` | | OPTIONAL data-pack JSON to place the mansion with jigsaw (no code) |

## The character
Upright 32-unit (2 block) humanoid, 26 bones, 63 cubes. Face is painted on a 16x16 head front (brows, eyes with lids, nasolabial lines, closed thin mouth, crow's-feet) with a separate nose and ears. Charcoal suit with chalk pinstripe, light-gray open-collar shirt with button placket, pocket square, black overcoat built from SEPARATE articulated sections (shoulder yokes, collar pieces, lapels, back panel, side panels, and six hinged skirt panels: front, side and back on each side), leather shoes with polished highlights, steel wristwatch with dial, gold signet ring. Hands are real articulated parts (palm, thumb, index finger, three-finger group) so he can point, curl fists and gesture.

### Animations (exactly 10)
1. `idle` - 6.0s, loops, 292 keyframes - Adjusts his cuffs (right hand tugs the left cuff, head looks down at it) between calm scans of the room; loops
2. `walk` - 1.2s, loops, 244 keyframes - Slow, upright walk: small arm swing, overcoat panels swing with the legs; loops
3. `run` - 0.7s, loops, 175 keyframes - Quick controlled sprint: forward lean, bent pumping arms with closed fists, overcoat streaming back; loops
4. `coat_adjust` - 3.4s, plays once, 813 keyframes - Hands to lapels, tug, smooth the front, brush the shoulder, pull the hem straight, settle
5. `command` - 2.4s, plays once, 314 keyframes - Left hand behind his back, right arm rises, firm forward chop, holds, lowers
6. `punch_attack` - 1.7s, plays once, 621 keyframes - Guard -> wind-up (weight back, fist chambered) -> step-in cross -> impact -> follow-through -> recovery to guard and rest
7. `counter_attack` - 2.4s, plays once, 805 keyframes - Ready -> lean-back dodge with side step -> left parry sweep -> step-in right counter -> follow-through -> collar/coat reset
8. `intimidation` - 3.5s, plays once, 602 keyframes - Steps forward, points with the index finger (two firm jabs), then adjusts his collar with the left hand
9. `summoned_arrival` - 3.8s, plays once, holds last frame, 408 keyframes - Scales in (small overshoot), fixes his jacket hem and lapel, looks left and right, holds the last frame
10. `death` - 3.4s, plays once, holds last frame, 888 keyframes - Hit reaction, stagger with hand to chest, knees buckle, falls backwards onto his back, settles; holds the last frame

Bone tree:
- root
  - body
    - torso
      - head
      - arm_l
        - forearm_l
          - hand_l
            - thumb_l
            - index_l
            - fingers_l
      - arm_r
        - forearm_r
          - hand_r
            - thumb_r
            - index_r
            - fingers_r
    - coat_front_l
    - coat_side_l
    - coat_back_l
    - coat_front_r
    - coat_side_r
    - coat_back_r
    - leg_l
      - foot_l
    - leg_r
      - foot_r

Conventions: Bedrock/GeckoLib values (x/y rotation negated vs Blockbench internal; position x inverted). +X pitches the front down; +Z on a left-side bone lifts it outward. He faces -Z; his left side is +X. Animation values are offsets from the rest pose stored in the geometry.

## Salmawy's Secret Mansion
Template-local coordinates (x east, y up, z south). Entrance faces NORTH (z=0). The ground-floor floor block is y=5, so place it with the template origin 6 blocks below the surface (jigsaw `start_height: -6`), or `/place template` with the floor flush to the ground. Basement is y0-4, ground floor y6-9, first floor y11-14, second floor y16-19, roof terrace y21-24.

| Area | Template coords (x, z) | Notes |
|---|---|---|
| Front portico + entrance | x11-27, z0-1 | four stone columns, hanging lanterns, dark-oak front door at (19,6,1) |
| Reception hall | x10-28, z2-11 | checker floor, red/orange runner, stone columns, chandeliers, seating, shelves |
| Grand wooden staircase | x1-8, z2-11 | 3 switchback dark-oak flights: ground to first to second to roof |
| Dining majlis | x30-37, z2-11 | long table, chairs, rug |
| West lounge (majlis) | x1-11, z13-24 | low seating, shelves |
| Inner courtyard + fountain | void x15-23, z15-22 (fountain centre 19,18) | open to sky, gallery ring x13-25, z13-24 with arcade columns, iron railings, hanging lanterns, azalea planters |
| East library | x27-37, z13-24 | shelf-lined, lecterns, reading table |
| Kitchen | x1-7, z26-37 | furnaces, smoker, barrels, table |
| ENCOUNTER ROOM | x11-27, z26-37 | red runner, columns, dais z34-37 with desk and throne chair, shelves, banners. DATA marker `salmawy_spawn` at (19,7,34) |
| Trophy / map room | x29-37, z26-37 | gold display blocks, smithing and cartography |
| First floor | office x10-28 z2-11 (luxurious: desk, lectern, fireplace, chandeliers, balcony doors); master bedroom x30-37; guest rooms x1-11; study x27-37; salon x11-27 z26-37; staff rooms | wrought-iron balcony (iron bars) on the front, two mashrabiya wood-lattice bays |
| Second floor | library over the hall, music majlis, bedrooms, meeting salon | second balcony with iron railing |
| Roof terrace | whole footprint, y21 | crenellated parapet, corner lantern posts, two shaded pergola seating areas, roof access from the stair hall |
| SECRET: hidden passage | false bookshelf at (11,6..7,31) in the encounter room's west wall | break the two bookshelf blocks; passage (10..9, 6..7, 31) then a stone-brick stair down (x=9, z30 to 26) |
| SECRET: archive | x2-7, z26-36, y1-4 | shelves, lecterns, 2 loot chests (stronghold_library) |
| SECRET: meeting chamber | x12-26, z18-28, y1-4 | long dark-oak table with chairs, rug, chandeliers, banner |
| SECRET: treasure vault | x30-36, z20-28, y1-4 | iron door at (29,1,24) opened by a stone pressure plate at (28,1,24); gold/emerald/diamond/iron blocks, 4 loot chests (bastion_treasure x2, end_city_treasure, simple_dungeon) |

Hidden passage design: a plain false bookshelf (no redstone) so it works everywhere; players find it by breaking the shelf. A piston door was deliberately not used because I could not test redstone here.

Spawn: the DATA structure block at (19,7,34) is a marker only (vanilla jigsaw placement ignores structure blocks). Read it with your own code to spawn Dr. Salmawy on the dais.

Optional placement JSON (`Integration_Optional/`): jigsaw structure, structure_set (very rare, spacing 64) and template pool for namespace `soulbound` and biome tag `#soulbound:is_soul_bound`. Change the namespace/biome tag to suit. This is data only.

## Verification performed
Character:
- PASS - 26 bones; every parent exists and precedes its children
- PASS - all UV rectangles inside 128x128 (63 cubes)
- PASS - no overlapping UV rectangles (mirrored arm/shoe/skirt parts intentionally share one rectangle set)
- PASS - every UV-mapped texel is painted
- PASS - texture is exactly 128x128
- PASS - EXACTLY 10 animations, names and order as specified
- PASS - every animated bone exists in the geometry
- PASS - every animation has many real keyframes: idle=292, walk=244, run=175, coat_adjust=813, command=314, punch_attack=621, counter_attack=805, intimidation=602, summoned_arrival=408, death=888
- PASS - no keyframe beyond its animation length
- PASS - looping animations close their cycle: idle=0.00, walk=0.00, run=0.00
- PASS - punch_attack: arm_r starts and ends at rest and peaks at -100 degrees (windup, strike, recovery present)
- PASS - counter_attack: arm_r starts and ends at rest and peaks at -98 degrees (windup, strike, recovery present)
- PASS - death final pose lies on the ground: lowest point y=-0.4, highest y=12.9 (1 unit = 1/16 block)
- PASS - standing height 33.0 units (~2 blocks), feet on ground (y=0.00)
- PASS - bbmodel matches geo (cubes) and has 10 animations
Mansion:
- PASS - NBT reads back: size 39x25x39, DataVersion 3955 (Minecraft 1.21.1)
- PASS - 37623 block entries inside bounds, palette indices valid (135 palette states)
- PASS - every block id is a vanilla 1.21.1 block (checked against my list; unknown: [])
- PASS - vanilla blocks only: no mod blocks, so it places in any dimension
- PASS - occupies y 0..24 (25 layers)
- PASS - floor slabs at y5/y10/y15/y20 present: {5: 1052, 10: 1217, 15: 1408, 20: 1395}
- FAIL - 6 loot chests with vanilla loot tables (minecraft:chests/bastion_treasure, minecraft:chests/end_city_treasure, minecraft:chests/simple_dungeon, minecraft:chests/stronghold_library)
- PASS - one DATA marker "salmawy_spawn" at (19, 7, 34) (encounter dais)
- FAIL - walkability flood-fill from the front door reaches all 14 above-ground probe rooms on three floors + roof (missing: ['dining majlis'])
- PASS - basement (archive, meeting chamber, vault) is NOT reachable until the false bookshelf is broken
- FAIL - after breaking the false bookshelf (11,6..7,31) the flood-fill reaches the archive, meeting chamber and vault
- PASS - structure previews rendered from the NBT itself

## Not verified / limitations
- Nothing was opened in Blockbench or loaded in GeckoLib/Minecraft; the preview and all checks come from my own scripts and software renderer. Rotation/position sign conventions follow Bedrock/GeckoLib but are unconfirmed in-engine; if one side swings the wrong way, flip that channel. The `.bbmodel` was written by script (Blockbench 4.10 schema); import the geo+animation JSON if it will not open.
- The NBT was read back and structurally validated, but NOT placed in a world. Block states are partly implicit (fences, panes, bars, walls, chains, stairs shapes rely on vanilla shape updates on placement); doors, beds and stairs were given explicit states. Beds, chests and lecterns have no entities inside (chests use loot tables).
- The reachability test is my own flood-fill (it treats doors, carpets and plates as passable and allows 1-block steps); it is a sanity check, not a game test. Iron door + pressure plate opening is standard vanilla behaviour but untested here. Water in the fountain is a static source; hanging lanterns/chains rely on their supports.
- The structure is vanilla-only by design; it does not use Soul Bound Dimension blocks. Terrain around the 39x39 footprint is not adapted (it needs flat-ish ground or `beard_thin` adaptation).
- Characters: the face is a stylised 16x16 painting, not a portrait. Hands are simple boxes; fingers curl as a group.
