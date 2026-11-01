# Asset verification results
Run by `verify` script after generation (checks performed on the final files in this ZIP).

| Result | Check |
|---|---|
| PASS | manifest lists exactly 20 assets |
| PASS | all 102 listed asset files exist [] |
| PASS | 22 environment textures are all 16x16 PNG |
| PASS | all JSON/bbmodel files parse (90 json, 6 bbmodel) [] |
| PASS | every model->texture, blockstate->model, item->model reference resolves [] |
| PASS | 5 Blockbench projects for the 3D plant assets |
| PASS | creature texture is exactly 128x128 |
| PASS | glow mask is 128x128 |
| PASS | bone hierarchy valid (49 bones, 92 cubes) |
| PASS | all cube UV boxes fit inside 128x128 |
| PASS | no overlapping UV faces (3196 px used of 16384) |
| PASS | exactly the 10 required animations present |
| PASS | all animated bones exist in the geometry [] |
| PASS | idle: 31 animated bones, 851 keyframes, length 4.0s |
| PASS | walk: 26 animated bones, 408 keyframes, length 1.0s |
| PASS | run: 28 animated bones, 450 keyframes, length 0.6s |
| PASS | giant_jump: 30 animated bones, 184 keyframes, length 2.0s |
| PASS | sit: 27 animated bones, 255 keyframes, length 3.0s |
| PASS | sleep: 32 animated bones, 119 keyframes, length 5.0s |
| PASS | crystal_chime_attack: 31 animated bones, 153 keyframes, length 2.4s |
| PASS | prism_shield: 25 animated bones, 157 keyframes, length 4.0s |
| PASS | summoned_arrival: 28 animated bones, 147 keyframes, length 3.0s |
| PASS | death: 30 animated bones, 99 keyframes, length 2.2s |
| PASS | bbmodel has same cube count and 10 animations as geo/animation json |
| PASS | Buried_Mirage_Observatory.nbt: valid gzip NBT, size 31x28x31, 19760 blocks, 37 palette entries, all positions in bounds |
| PASS | Cathedral_of_Glass_Harp.nbt: valid gzip NBT, size 39x32x39, 36210 blocks, 22 palette entries, all positions in bounds |
| PASS | Lost_Caravanserai.nbt: valid gzip NBT, size 43x19x43, 20970 blocks, 48 palette entries, all positions in bounds |
| PASS | Thunderglass_Labyrinth.nbt: valid gzip NBT, size 49x25x49, 32528 blocks, 22 palette entries, all positions in bounds |
| PASS | Glasswood_Tree_Large.nbt: valid gzip NBT, size 15x20x15, 441 blocks, 4 palette entries, all positions in bounds |
| PASS | Glasswood_Tree_Small.nbt: valid gzip NBT, size 15x14x15, 253 blocks, 4 palette entries, all positions in bounds |
| PASS | Mirage_Palm_Short.nbt: valid gzip NBT, size 17x14x17, 63 blocks, 2 palette entries, all positions in bounds |
| PASS | Mirage_Palm_Tall.nbt: valid gzip NBT, size 17x18x17, 73 blocks, 2 palette entries, all positions in bounds |
| PASS | Cathedral_of_Glass_Harp size equals requested (39, 32, 39) |
| PASS | Cathedral_of_Glass_Harp uses vanilla blocks only |
| PASS | Buried_Mirage_Observatory size equals requested (31, 28, 31) |
| PASS | Buried_Mirage_Observatory uses vanilla blocks only |
| PASS | Lost_Caravanserai size equals requested (43, 19, 43) |
| PASS | Lost_Caravanserai uses vanilla blocks only |
| PASS | Thunderglass_Labyrinth size equals requested (49, 25, 49) |
| PASS | Thunderglass_Labyrinth uses vanilla blocks only |
| PASS | approximate walk-path test (script BFS, not in-game): 19 public targets reachable from entrances |
| PASS | no Java code and no Bedrock add-on files |

## Not tested / limitations (disclosed)
- Nothing was loaded in Minecraft Java, Blockbench or GeckoLib. Structures, models and animations are validated structurally only.
- Animation direction signs (rotation X/Y, position) follow Blockbench-Bedrock JSON convention from memory and may need flipping once seen in-engine.
- `.bbmodel` files are schema-written by script and untested in Blockbench; the `.geo.json` can be imported instead.
- Cathedral/Observatory/Caravanserai/Labyrinth hidden areas are sealed by design (need block breaking); their internal passages are not path-tested.
- Structures hold vanilla stand-in blocks; tree templates hold custom ids and cannot load without the mod.
- Block-model glow/light, `render_type` and cutout layers need code registration by the integrating developer.
- Textures are script-painted pixel art; expect an art pass. Environmental assets are 16x16 (the 32x32 option was not used).
- Minecraft versions other than 1.21.1 data format are untested.
