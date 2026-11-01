# Cursed Bell Tower - dimensions, placement, encounter

* File: `Cursed_Bell_Tower.nbt` (gzip NBT structure template, DataVersion 4189 = Java 1.21.4)
* Size: **31 (x) x 45 (y) x 31 (z)**. Coordinates below are relative to the structure's minimum corner (0,0,0).
* North = z 0, entrance faces **south** (z 30). Layers y0-5 are the buried foundation + crypt.
* Placement: align structure **y=6 with the terrain surface layer** (origin = surface Y - 6). Floors/doors are then flush with the ground.
  `/place template` or a structure block needs the file at `data/<namespace>/structure/cursed_bell_tower.nbt` (1.21+ folder name `structure`).

## Mob encounter
* Boss chamber ("Nave"): interior x8-22, z8-22, y7-21 (15 high), floor y6.
* **Boss spawn position: (13, 8, 16)** (feet) on the 5x5 dais (x11-15, z14-18, top y7). Gilded blackstone block at (13,7,16).
* The .nbt cannot contain your custom mob (no Java/data). Spawn it from your own entity registration, e.g. a spawner/command block at (13,8,16) or a datapack structure-set processor.

## Route map
| Feature | Coordinates |
|---|---|
| Grand portal (south) | x13-17, y7-13, z29-30 |
| Entrance hall | x11-19, y7-14, z25-28 |
| Arch into the nave | x12-18, y7-15, z23-24 |
| Spiral stair tower (NE) | ring x17-21, z9-13; core x18-20, z10-12; ground door (16,7-8,11) |
| Spiral stair: ground -> clockwork hall | steps y7..22 (one full turn+); door into hall at (16,23-24,11) |
| Spiral top landing -> east balcony | step (21,29,10), door x22-24, y30-31, z10 |
| Nave gallery stair | x8, y7..14, z22..15 (north-facing) |
| Gallery (broken balcony ring) | y14, 2 blocks wide around the nave |
| West broken balcony | x3-5, y14, z7-11 (door x6-7, y15-16, z9) |
| Clockwork hall | x8-22, y23-28 (gears on walls, pendulum at (14,23-28,19), clock faces on all 4 outer walls) |
| Stair clockwork hall -> bell chamber | x8, y23..29, z22..16 |
| Bell chamber | x8-22, y30-34, open pointed arches on all 4 faces |
| **Giant bell** (copper, soul lantern clapper) | x11-19, y31-37, z11-19, hung from beam at y40 |
| Four small bells | (9,33,9) (21,33,9) (9,33,21) (21,33,21) |
| East broken balcony | x25-27, y29, z8-12 |
| Spire / roof | y35-44, lightning rod tip at (15,44,15) |
| Crypt | x4-26, y1-5, z11-26; stair from nave: (22,6..1,16..21) |
| **Secret passage** | false wall = cracked deepslate bricks at (15,1-2,10); break them -> passage (15,1-2,8-9) |
| **Hidden treasure vault** | x11-19, y1-4, z4-7; chests (14,1,4) (15,1,4) (16,1,4) |
| Other chests | (9,7,9) (21,7,21) nave; (21,23,21) clock hall; (15,2,19) crypt altar |

## Custom assets
None required. The structure uses only vanilla blocks (47 block ids, listed in `Cursed_Bell_Tower_Block_Palette.md`).
Banners carry original symbols built from vanilla patterns (rhombus + circle + cross + border, light-blue/cyan on black).
Loot uses vanilla loot tables (woodland_mansion, simple_dungeon, ancient_city, end_city_treasure, bastion_treasure).
