# Verification report

## Automated checks run (all executed in the build environment)
```
PASS  geo texture_width/height = 128x128
PASS  texture is exactly 128x128 PNG (mode RGBA)
PASS  glowmask 128x128
PASS  all 302 cubes have per-face UVs inside the 128x128 sheet
PASS  every bone has a pivot (98 bones)
PASS  exactly 10 animations in required order: idle, walk, run, bell_slam, sonic_wave_attack, chain_sweep, resonance_burst, intimidation, summoned_arrival, death
PASS  idle: all animated bones exist; 1861 keyframes on 89 bones; length 4.00s; loop=True
PASS  idle: keyframes within animation_length (max t=4.00)
PASS  walk: all animated bones exist; 783 keyframes on 92 bones; length 1.20s; loop=True
PASS  walk: keyframes within animation_length (max t=1.20)
PASS  run: all animated bones exist; 742 keyframes on 92 bones; length 0.70s; loop=True
PASS  run: keyframes within animation_length (max t=0.70)
PASS  bell_slam: all animated bones exist; 1868 keyframes on 93 bones; length 3.00s; loop=False
PASS  bell_slam: keyframes within animation_length (max t=3.00)
PASS  sonic_wave_attack: all animated bones exist; 2064 keyframes on 90 bones; length 3.40s; loop=False
PASS  sonic_wave_attack: keyframes within animation_length (max t=3.40)
PASS  chain_sweep: all animated bones exist; 2035 keyframes on 92 bones; length 3.00s; loop=False
PASS  chain_sweep: keyframes within animation_length (max t=3.00)
PASS  resonance_burst: all animated bones exist; 2552 keyframes on 90 bones; length 4.00s; loop=False
PASS  resonance_burst: keyframes within animation_length (max t=4.00)
PASS  intimidation: all animated bones exist; 2678 keyframes on 88 bones; length 4.00s; loop=False
PASS  intimidation: keyframes within animation_length (max t=4.00)
PASS  summoned_arrival: all animated bones exist; 2113 keyframes on 92 bones; length 4.50s; loop=False
PASS  summoned_arrival: keyframes within animation_length (max t=4.50)
PASS  death: all animated bones exist; 1786 keyframes on 93 bones; length 4.00s; loop=hold_on_last_frame
PASS  death: keyframes within animation_length (max t=4.00)
PASS  bell_slam drives expanding ring geometry on: ground_ring_1, ground_ring_2, ground_ring_3
PASS  sonic_wave_attack drives expanding ring geometry on: mob_ring_1, sonic_ring_1, sonic_ring_2, sonic_ring_3, sonic_ring_4
PASS  chain_sweep drives expanding ring geometry on: mob_ring_1, mob_ring_2, mob_ring_3
PASS  resonance_burst drives expanding ring geometry on: bell_ring_1, bell_ring_2, bell_ring_3, bell_ring_4, mob_ring_1
PASS  intimidation drives expanding ring geometry on: bell_ring_1, bell_ring_2, bell_ring_3, bell_ring_4, mob_ring_1, mob_ring_2, mob_ring_3
PASS  bell_slam animates clapper + chain bones
PASS  sonic_wave_attack animates clapper + chain bones
PASS  chain_sweep animates clapper + chain bones
PASS  resonance_burst animates clapper + chain bones
PASS  bbmodel: 10 animations, 302 cubes, embedded texture=data:image/png;base64,
PASS  NBT size = [31, 45, 31]
PASS  NBT DataVersion 4189 (Minecraft Java 1.21.4)
PASS  all 20871 block entries reference a valid palette index (88 states)
PASS  all block positions inside 31x45x31
PASS  23 block entities (chests with loot tables, banners)
PASS  no duplicate positions
```

## NOT tested (disclosure)
* **Never loaded in Minecraft, GeckoLib or Blockbench.** Files are written to the published formats (Bedrock geo 1.12.0, animation 1.8.0, Blockbench bbmodel 4.10, Java structure NBT), validated structurally only (JSON parse, UV bounds, bone references, NBT round-trip with an independent reader, palette/position bounds).
* Rotation sign conventions (+X tilts forward, Euler order ZYX) were taken from Blockbench/Bedrock behaviour. The renderer used for previews uses the same assumption; if your GeckoLib version differs, the bell may hang slightly off-plumb in the attack poses.
* The `.bbmodel` was written by script from the same data as the geo file; Blockbench may ask to fix minor field differences on first open. The geo/animation JSON is the authoritative export.
* Previews are software renders (flat nearest-neighbour, no real lighting); in-game look will differ. The bell is a stepped octagonal pyramid (Minecraft-style), not a smooth curve.
* Structure: block states (stair facing/shape, banners, chests, wall torches, bells) follow 1.21.4 naming; not placed in a world. Check stair shapes at roof corners and the spiral-stair step orientation after first placement. The ground-level assumption (y=6 flush with terrain) needs flat-ish terrain; the buried foundation has no terrain-adaptation processor.
* Mob placement inside the structure is a documented coordinate only; no entity is embedded.
* Bell/body clipping: the bell dips up to ~4 units below the floor in `run`, `chain_sweep`, `intimidation` and about 1.5 in `bell_slam` (intended dragging/impact); `death` rests it on its side.
