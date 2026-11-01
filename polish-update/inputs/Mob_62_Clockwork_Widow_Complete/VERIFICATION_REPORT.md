# Verification Report - MOB 62 The Clockwork Widow + Palace of Broken Hours

Everything below was checked by script on the final files (see `verify_pkg.py` logic: JSON/PNG/NBT parsed back independently from disk).

## Automated checks (all PASS unless listed)
```
PASS - Texture is exactly 128x128 PNG  [(128, 128) RGBA]
PASS - Glow mask is exactly 128x128 PNG  [(128, 128) RGBA, 1951 glowing px]
PASS - Texture has painted content  [12340/16384 px non-transparent]
PASS - geo: format 1.12.0, identifier geometry.clockwork_widow
PASS - geo: texture_width/height 128
PASS - geo: bone names unique  [171 bones]
PASS - geo: every parent exists and precedes child
PASS - geo: all cubes sized >0 and all face UV rects inside 128x128  [788 cubes, 4680 UV faces]
PASS - arm 1: articulated chain (shoulder/elbow/hand + 6 finger bones + cogs + piston)  [13 bones]
PASS - arm 2: articulated chain (shoulder/elbow/hand + 6 finger bones + cogs + piston)  [14 bones]
PASS - arm 3: articulated chain (shoulder/elbow/hand + 6 finger bones + cogs + piston)  [14 bones]
PASS - arm 4: articulated chain (shoulder/elbow/hand + 6 finger bones + cogs + piston)  [13 bones]
PASS - arm 5: articulated chain (shoulder/elbow/hand + 6 finger bones + cogs + piston)  [14 bones]
PASS - arm 6: articulated chain (shoulder/elbow/hand + 6 finger bones + cogs + piston)  [14 bones]
PASS - animation: exactly 10 animations  [idle, walk, run, clock_blade_combo, gear_storm, time_lock, six_arm_assault, broken_hourglass, summoned_arrival, death]
PASS - animation: names match requested list
PASS - animation: every animated bone exists in geometry
PASS - idle: 4.0s, 112 bones, 3074 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - walk: 1.6s, 110 bones, 1288 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - run: 0.8s, 110 bones, 845 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - clock_blade_combo: 2.4s, 103 bones, 2064 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - gear_storm: 3.0s, 105 bones, 1832 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - time_lock: 3.2s, 88 bones, 1616 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - six_arm_assault: 3.6s, 97 bones, 3029 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - broken_hourglass: 4.2s, 112 bones, 2547 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - summoned_arrival: 3.4s, 98 bones, 2423 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - death: 3.0s, 108 bones, 1859 keyframes; arms with multi-key motion [1, 2, 3, 4, 5, 6]
PASS - idle: loop seam continuous (first/last key match, mod 360 for rotation)  [worst seam delta 0.000]
PASS - walk: loop seam continuous (first/last key match, mod 360 for rotation)  [worst seam delta 0.000]
PASS - run: loop seam continuous (first/last key match, mod 360 for rotation)  [worst seam delta 0.000]
PASS - clock_blade_combo: has visible effect bones animated  [fx_burst, fx_hourglass, fx_slash, fx_storm, fx_time_lock, fx_wave, slash_a, slash_b, tl_ground]
PASS - gear_storm: has visible effect bones animated  [fx_burst, fx_hourglass, fx_slash, fx_storm, fx_time_lock, fx_wave, storm_gear1, storm_gear2, storm_gear3, stor]
PASS - time_lock: has visible effect bones animated  [fx_burst, fx_hourglass, fx_slash, fx_storm, fx_time_lock, fx_wave, tl_ground, tl_hands, tl_ring1, tl_ring2, tl]
PASS - six_arm_assault: has visible effect bones animated  [fx_burst, fx_hourglass, fx_slash, fx_storm, fx_time_lock, fx_wave, slash_a, slash_b, tl_ground]
PASS - broken_hourglass: has visible effect bones animated  [fx_burst, fx_hourglass, fx_slash, fx_storm, fx_time_lock, fx_wave, hg_bottom_sand, hg_frame, hg_glass, hg_stre]
PASS - bbmodel: parses; elements == geo cubes; 10 animations; embedded texture  [788 elements]
PASS - nbt: gzip NBT parses; size 47x36x47; DataVersion 3465 (1.20.1)  [61749 blocks, 75 palette entries]
PASS - nbt: only minecraft: namespace blocks (no custom block dependency)
PASS - nbt: all block positions inside bounds
PASS - preview file exists: Palace_Of_Broken_Hours_Preview.png
PASS - preview file exists: Palace_Floorplans.png
PASS - preview file exists: Palace_Sections.png
PASS - preview file exists: Clockwork_Widow_Preview.png
PASS - preview file exists: Animation_Contact_Sheet.png
```

## Structure walkability check (separate script)
A breadth-first search over standing positions (2-high clearance, 1-block step-ups, drops up to 3, iron doors treated as openable) starting at the front steps reached: foyer, grand hall floor, hall balcony ring, L2 ballroom, L2 west corridor, L2 gallery, workshop, engine room, arena dial, **arena boss dais (spawn)**, treasure vault (through the secret door), hidden library (through the secret door), and the top of the NW tower. 2,881 reachable standing cells. This proves room-to-room connectivity in my block model; it does not prove redstone behaviour (see below).

## NOT tested / honest disclosures
1. **Never loaded in Blockbench, GeckoLib, or Minecraft.** I have no Minecraft/Blockbench in this environment. All previews are from my own software renderer built from the same files. The conventions it assumes (rotation = Rz*Ry(-y)*Rx(-x), north face = front, per-face UV orientation viewed from outside) are the Blockbench/GeckoLib conventions as I know them. If any face looks mirrored or a bone rotates the opposite way in-engine, that convention is the first suspect. Open `Clockwork_Widow.bbmodel` in Blockbench and play the animations before wiring code.
2. **`.bbmodel` is script-generated**, not saved by Blockbench. The `.geo.json` and `.animation.json` are the authoritative GeckoLib files. If Blockbench rejects the bbmodel, re-import the geo.json + animation.json + texture instead.
3. Animation position keys on the X axis (used only in the gear-storm radial sweep and symmetric breathing) may be sign-flipped in-engine; the effect is symmetric so it should not be visible.
4. Effect geometry (time rings, gear storm, hourglass, wave, slashes, burst) is real cubes hidden by `scale 0` keyframes in every animation that does not use it. This relies on GeckoLib resetting un-animated bones, and on the controller playing a hiding animation (idle/walk/...) before/after attacks. Use transition length 0 to the attack animations to avoid a 1-tick pop.
5. "Transparent-looking" chest clock face is a painted dark-violet glass look on an opaque face (so it works with the default cutout render type). Lace/veil use 0/255 alpha cut-outs (cutout render type required - GeckoLib default).
6. The glow mask is a separate texture for GeckoLib's `AutoGlowingGeoLayer` (it expects `<texture>_glowmask.png`). Rename on integration (see README).
7. Texture density is mixed (hem/skirt faces are lower texel density than the face and insignia). Everything is hand-coded procedural pixel art, not photographed or AI-image-generated; judge it in-engine.
8. The Widow keeps two small slender human arms (sleeved, hands clasped low) *in addition to* the six brass arms; the brief did not say to remove them. Delete bones `arm_r`, `arm_l` and children if unwanted.
9. Structure: the .nbt was written by my own NBT writer and read back by an independent parser, but **never placed in a world**. Stair corner shapes, pane/fence connections and door/button states were set explicitly but are untested in-game. The secret-door mechanism relies on a button powering the adjacent wall block (standard vanilla door-adjacent-power behaviour) - not simulated.
10. The `minecraft:structure_block` (DATA mode, metadata `clockwork_widow_boss_spawn`) at (23,1,16) is a marker under the dais. If your placement code does not handle data markers it simply stays as a hidden block under the floor. Remove it if you prefer.
11. Exterior silhouette is boxy: four corner towers + stepped roof, limited by the 36-block height budget. Interior is where the detail lives.
12. Weird-but-intentional: boss HP/AI, loot tables, spawners, entity class, renderer class are all out of scope (no Java).
