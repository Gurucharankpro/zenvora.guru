# MOB 62 - THE CLOCKWORK WIDOW (Eclipse Syndicate, member 2) + THE PALACE OF BROKEN HOURS

Target: **Minecraft Java 1.20.1, Forge, GeckoLib 4.x** (geo format 1.12.0, animation format 1.8.0). Assets only - no Java, no standalone mod, one mob, one structure.

## Files
| File | What |
|---|---|
| `Clockwork_Widow.bbmodel` | Editable Blockbench project (788 cubes, 171 bones, 10 animations, embedded texture) |
| `Clockwork_Widow.geo.json` | GeckoLib geometry `geometry.clockwork_widow` |
| `Clockwork_Widow.animation.json` | 10 animations `animation.clockwork_widow.<name>` |
| `Clockwork_Widow_Texture.png` | 128x128 texture |
| `Clockwork_Widow_Texture_glowmask.png` | 128x128 glow mask (violet eyes, crystal, time rings, effects) |
| `Clockwork_Widow_Preview.png` | Software-rendered preview + atlas |
| `Animation_Contact_Sheet.png` | Pose strips of all 10 animations |
| `Palace_Of_Broken_Hours.nbt` | Vanilla structure template, 47x36x47, DataVersion 3465 |
| `Palace_Of_Broken_Hours_Preview.png`, `Palace_Floorplans.png`, `Palace_Sections.png` | Structure previews |
| `Palace_Block_Palette.md` | Block palette + counts (no custom blocks) |
| `VERIFICATION_REPORT.md` | What was checked, what was not |

## Mob integration (GeckoLib 4, Forge 1.20.1)
Resource pack / mod resources (replace `modid`):
```
assets/modid/geo/clockwork_widow.geo.json
assets/modid/animations/clockwork_widow.animation.json
assets/modid/textures/entity/clockwork_widow.png                  (rename of Clockwork_Widow_Texture.png)
assets/modid/textures/entity/clockwork_widow_glowmask.png          (rename of ..._Texture_glowmask.png)
```
Animation ids (play these from your AnimationController): `animation.clockwork_widow.idle` (loop), `.walk` (loop), `.run` (loop), `.clock_blade_combo`, `.gear_storm`, `.time_lock`, `.six_arm_assault`, `.broken_hourglass`, `.summoned_arrival`, `.death` (hold_on_last_frame). Short names in the brief map 1:1 to the last segment.

Notes for the integrating developer:
* **Front is -Z (Blockbench north).** Suggested hitbox 0.9 wide x 2.9 tall; model with halo is about 3.3 blocks tall.
* **Effects are geometry.** Bones `fx_time_lock`, `tl_ground`, `fx_storm`, `fx_slash`, `fx_burst`, `fx_hourglass`, `fx_wave` are children of `fx_root` and are scaled to 0 by every animation that does not use them. Always have an animation playing (idle/walk/run) so they stay hidden.
* **Attack timing (animation_length):** blade combo 2.4s (windup 0-0.5, slashes 0.5-1.6, recover 2.4); gear storm 3.0s (windup 0.7, gears orbit 0.8-2.4, collapse by 3.0); time lock 3.2s (rings form 0.5-1.2, hold to 2.6); six-arm assault 3.6s (windup 0.8, strikes 1.05/1.65/2.55, recover 3.6); broken hourglass 4.2s (hourglass 0.4-1.5, pour 1.7-2.4, wave release 2.5-4.0). Use these as damage/projectile trigger times.
* **Glow:** add `AutoGlowingGeoLayer` (it looks for the `_glowmask` texture). Violet eyes, chest crystal, time rings and all fx bones are in the mask.
* **Render type:** cutout (GeckoLib default) - lace, veil and collar use 0/255 alpha.
* **Arms:** `arm1..arm6`: 1/4 = clock-hand blades, 2/5 = floating gears, 3/6 = time rings (1-3 on entity-right/+X, 4-6 on -X). Each has `_shoulder`, `_elbow`, `_hand`, `_f0a/_f0b/_f1a/_f1b/_f2a/_f2b` fingers, `_cog_s`, `_cog_e`, `_piston`, plus `_blade` / `_gear`,`_gear2` / `_ring`,`_ring2`.
* The Widow also has two small human arms (`arm_r`, `arm_l`); delete them in Blockbench if you want only the six.
* Hands on the chest clock (`chest_hour`, `chest_min`), crystal (`chest_crystal`), halo (`halo`, `halo_hour`, `halo_min`, `halo_shard1/2`), hourglass ornaments, pocket watches and curls are all independently animated bones.

## Structure integration
Copy `Palace_Of_Broken_Hours.nbt` to `data/modid/structures/palace_of_broken_hours.nbt` and place it with `StructureTemplate` / your Soul Bound Dimension structure code.
* **Vertical alignment:** template layers y=0..11 are underground foundation; **y=12 is the plaza / ground-floor walking layer**. Place the template so that template y=12 sits at the terrain's first air layer, i.e. `origin.y = firstAirY - 12`. The first 12 layers are carved into the ground.
* Footprint 47x47, entrance on the **south** (+Z) side, steps at x=21..25, z=46.
* **Boss spawn (template-local block coords):** entity feet at **(23.5, 3.0, 16.5)** - on the dais in front of the throne, facing north. Seated spawn variant (for the `summoned_arrival` animation): **(23.5, 3.0, 14.5)**. A DATA-mode structure block with metadata `clockwork_widow_boss_spawn` is buried at (23,1,16) as a marker.
* **Arena:** circular underground chamber centred (23, 2, 23), radius ~12.6, floor-embedded clock dial (hands at about 11:55), twelve golden pillars at radius 10, central black throne to the north at (23,3,13..14), suspended gears, giant pendulum at (23,*,30), violet lighting from crying obsidian and amethyst.
* **Secret passages (iron door + hidden stud, opened by buttons set into clock-ring decorations):**
  1. Arena east wall (36,2,23) -> treasure vault (hidden stud at (35,2,24)).
  2. NW tower, top landing (9,28,8) -> hidden library (stud at (8,28,7)).
* **Route:** front steps -> foyer -> grand hall (west/east stairs to balcony ring) -> L2 ballroom / corridors -> corner-tower spiral staircases; SW tower spiral descends to the underground engine room -> corridor south of the arena.
* No custom blocks. Chests are empty (add loot tables in your own code).

## Rooms (all built with real block placements)
Four clock towers with dials showing different times; huge shattered clock face over the entrance; porch with golden columns; grand hall with ornamental wall gears, giant suspended pendulum with floor track, chandeliers, skylight and balcony ring; long grandfather-clock corridors on two levels; mechanical workshop; abandoned ballroom with patterned floor, broken columns and cobwebs; hidden library; underground clockwork engine room; hidden treasure vault; circular final arena.
