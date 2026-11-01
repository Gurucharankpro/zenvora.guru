# Mob 64 — The Raven Seamstress  +  The Theater of Black Curtains
Eclipse Syndicate asset package for the existing Soul Bound Dimension. **Assets only — no Java code, no standalone mod, one mob, one structure.**

## Contents
| Path | What |
|---|---|
| `Mob/Raven_Seamstress.bbmodel` | Editable Blockbench project (Bedrock format, box UV, texture embedded, all 10 animations) |
| `Mob/Raven_Seamstress.geo.json` | GeckoLib/Bedrock geometry `geometry.raven_seamstress` (format 1.12.0, 128×128) |
| `Mob/Raven_Seamstress.animation.json` | 10 animations, ids `animation.raven_seamstress.<name>` |
| `Mob/Raven_Seamstress_Texture.png` | Exactly 128×128 RGBA, procedurally painted pixel art; cutout alpha shapes the feather tips |
| `Mob/Raven_Seamstress_Texture_glowmask.png` | Optional 128×128 glow mask (eyes, threads, rune bands, gems, emblem rim, spectral ravens) |
| `Mob/Raven_Seamstress_Preview.png` | Software-rendered pose sheet (see disclosure) |
| `Structure/Theater_Of_Black_Curtains.nbt` | Vanilla structure-block NBT, 45×43×32, DataVersion 3465 (1.20.1) |
| `Structure/Theater_Structure_Preview.png`, `Theater_Block_Palette.png/.txt`, `Theater_Blueprint.json` | Previews, palette with counts, room bounds + boss spawn |

## Mob
114 bones / 313 cubes. Four arms (`arm_l1 arm_r1 arm_l2 arm_r2`, each upper→fore→hand→fingers, 4 claw cubes), 8 finger threads, 4 needles (`needle_1..4`), embroidery frame, 3 spectral ravens (`raven_1..3`: head, tail, two-part wings — bones of the **same** model), raven mask (brow, cheek, bridge, side plates, 3-segment curved beak, glowing eyes behind the slits), hair chains, folded-wing mantle (3 feather tiers per side), cape, 8-segment asymmetric skirt with feather panels, 8 loose floating feathers. Effect bones that are hidden except in their attack: `web`, `curtain`, `storm_a`, `storm_b` (each animation sets their scale to 0 when unused, so the static pose shows them — play `idle`).
Emblem (silver eye around black sun): chest plate and the embroidery-frame disc.
Animations (all keyframed; attacks have prep → execution → effects → follow-through → recovery): `idle` (loop), `walk` (loop), `run` (loop), `silver_needle_attack`, `raven_swarm`, `thread_binding`, `feather_storm`, `black_curtain`, `summoned_arrival`, `death` (hold on last frame). Arrival uses the `avatar` bone scale; death lays the mob on its side.

## Structure
Origin = min corner (x 0..44, y 0..42, z 0..31); entrance faces −Z. Floor surface y=9 (stage y=12), underground y 1..7.
**Boss spawn: (22, 12, 25)** structure-relative (centre of the stage emblem inlay; add the placed origin; +0.5 on x/z to centre). Boss arena bounds x12–32, y12–31, z22–29.
Rooms (min–max xyz) are in `Theater_Blueprint.json`: porch with 2 raven statues, foyer + mirrored grand stairs, orchestra + balcony levels 2 & 3 + side boxes, giant chandelier, gold-trimmed coffered ceiling (y32), proscenium + black/burgundy curtains, stage, hidden passage behind the back curtain (1-wide gaps at x=10 and x=34), dressing rooms, secret costume workshop (mannequins, suspended puppets), spiral stair to the rigging chamber, underground sewing sanctuary, treasure vault behind a bookshelf door (x37, z19–20), and the large raven sculpture (wingspan x10–34) above the stage.

## Verified
* Texture is 128×128; no UV region overlaps; exactly 10 animations; every animated bone exists; keyframe times ≤ animation length.
* `.nbt` was written by my own writer and re-parsed: size, block and palette counts, and all state/pos indices are in range.
* A walk-path test (step ≤1 up, 2-high clearance, solid floor; doors, carpets, chains and the bookshelf door treated as passable) reaches: foyer, orchestra, boss spawn, both balconies, a side box, dressing room, hidden corridor, costume workshop, rigging chamber, sanctuary and vault.

## NOT tested — please check
* **Nothing was opened in Blockbench, GeckoLib or Minecraft.** The preview renderer is my own and its rotation-sign convention is an assumption (positive X = pitch forward). If arms move the wrong way in-game, flip the sign of the X rotations.
* `.bbmodel` conversion: I negate X/Y rotation (and X position in animations) when writing the project, from memory of Blockbench's Bedrock convention. If the project looks mirrored in Blockbench, trust the `.geo.json`/`.animation.json` and re-export from Blockbench after fixing.
* The texture is procedurally generated pixel art, not hand-painted; it is functional but will want a pass in your paint tool. The silhouette is blocky: feather detail comes from many small cubes and alpha-trimmed tips.
* Target version was not given: `.nbt` uses DataVersion 3465 (1.20.1) with vanilla blocks only. Stairs/doors/panes are placed with explicit states; glass panes may need a block update to connect. Chests and barrels are empty (add loot tables), the secret door is a mineable bookshelf pair, and "hidden passages" are narrow curtain-end gaps, not redstone doors. Sizes/lighting/mob spawns were not play-tested.
* Needles are the only "projectile" geometry; actual damage, hitboxes and spawning belong to your Java side.
