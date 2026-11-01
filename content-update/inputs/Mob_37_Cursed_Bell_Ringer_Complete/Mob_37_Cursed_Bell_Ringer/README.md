# MOB 37 - THE CURSED BELL RINGER (Butcher Villagers / Soul Bound Dimension)

One gigantic corrupted villager + one exclusive structure, **assets only - no Java, no mod.**

## Contents
```
Creature/
  Cursed_Bell_Ringer.bbmodel            editable Blockbench project (Bedrock Entity format, 302 cubes, 98 bones, 10 animations, texture embedded)
  Cursed_Bell_Ringer.geo.json           GeckoLib / Bedrock geometry (format 1.12.0, per-face UVs, 128x128)
  Cursed_Bell_Ringer.animation.json     10 GeckoLib animations (format 1.8.0)
  Cursed_Bell_Ringer_Texture.png        128x128 hand-painted pixel texture
  Cursed_Bell_Ringer_Glowmask.png       emissive layer (eyes, runes, soul core, shockwaves) 128x128
  Cursed_Bell_Ringer_Preview.png        software-rendered 3-angle rest pose
  Cursed_Bell_Ringer_Animation_Sheet.png  5 frames of each of the 10 animations
Structure/
  Cursed_Bell_Tower.nbt                 31x45x31 structure template (Java 1.21.4)
  Cursed_Bell_Tower_Preview.png         isometric view + cutaway
  Cursed_Bell_Tower_Floor_Plans.png     plans of 6 levels, boss position marked
  Cursed_Bell_Tower_Blueprint.txt       layer-by-layer ASCII blueprint (labelled blueprint, secondary to the .nbt)
  Cursed_Bell_Tower_BlockList.csv       every block: x,y,z,full block state, block-entity NBT
  Cursed_Bell_Tower_Block_Palette.md/.json
  Placement_and_Encounter.md            dimensions, placement, boss position, route map
Documentation/VERIFICATION_REPORT.md
```

## The mob
Hunched ~4-block villager: oversized nose, pale-gray skin, icy-blue glowing eyes, tattered black robes (10 swaying hem strips), bronze pauldrons, chain-wrapped arms, dark-iron boots, long 3-segment articulated fingers, cracked ornamental mask (plus a loose swinging shard), five belt bells (one broken) each with its own clapper bone.
The **giant bell** is real geometry (about 50 cubes): crown, loop, 7 stacked octagonal layers, four open windows showing the glowing soul core, hollow flared lip, two raised ridges, and a 3-bone articulated clapper (`clapper_rod -> clapper_joint -> clapper_ball`). It hangs on a 3-link chain (`chain_a1..3`) from the hand.

### Animations (names are `animation.cursed_bell_ringer.<name>`)
`idle` (loop 4s), `walk` (loop 1.2s), `run` (loop 0.7s), `bell_slam` (3.0s), `sonic_wave_attack` (3.4s), `chain_sweep` (3.0s), `resonance_burst` (4.0s), `intimidation` (4.0s), `summoned_arrival` (4.5s), `death` (4.0s, hold on last frame).
Every attack has anticipation, execution, effect, follow-through and recovery keyframes. Shockwaves are **animated geometry** (octagon ring bones scaled outward, vertical rings also translate forward):
`ground_ring_1-3` (slam impact point), `mob_ring_1-3` (around the feet), `bell_ring_1-4` (around the bell), `sonic_ring_1-4` (forward blast), `wisp_1-3` (orbiting soul sparks).
Effect bones are scaled to 0 in every animation that does not use them, **so always keep an animation playing (idle) on the entity**; with no animation active the rings render at rest scale.
The bell's chain is keyed densely so the bell keeps hanging/swinging under gravity-like motion while the arm moves.

### GeckoLib integration (no code supplied)
1. Copy `geo` -> `assets/<ns>/geo/cursed_bell_ringer.geo.json`, `animation` -> `assets/<ns>/animations/cursed_bell_ringer.animation.json`, texture -> `assets/<ns>/textures/entity/cursed_bell_ringer.png`, glowmask -> `.../cursed_bell_ringer_glowmask.png` (use as an emissive layer/ glow renderer).
2. The geometry identifier is `geometry.cursed_bell_ringer`; animation ids are `animation.cursed_bell_ringer.*`.
3. Easing uses GeckoLib 4 keyframe fields (`easing`, `lerp_mode: catmullrom`). Position keyframes only use Y/Z (X is always 0) to avoid sign-convention differences.
4. Open the `.bbmodel` in Blockbench (Bedrock Entity format); use *File > Convert Project* for the GeckoLib format if you have the plugin.

## The tower
See `Structure/Placement_and_Encounter.md`. Place with y=6 at ground level; boss spawn at (13,8,16). Vanilla blocks only (copper stands in for bronze).

## Honest limits
Nothing was loaded in Minecraft, GeckoLib or Blockbench in this build environment - see `Documentation/VERIFICATION_REPORT.md`.
