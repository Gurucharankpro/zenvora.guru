# MOB 61 — Yuta Okkotsu (Jujutsu Kaisen) + Abandoned Jujutsu Academy

First character of the JJK faction (MOBS 61–70) for the Soul Bound Dimension. Asset creation only: no Java code, no standalone mod.

## Files
| File | Purpose |
|---|---|
| `Yuta_Okkotsu.bbmodel` | Editable Blockbench project (Bedrock entity format): 83 bones, 184 cubes, both textures embedded, all 10 animations |
| `Yuta_Okkotsu.geo.json` | Geometry `geometry.yuta_okkotsu` (format 1.12.0, texture 128x128) |
| `Yuta_Okkotsu.animation.json` | 10 animations, `animation.yuta_okkotsu.<name>` (format 1.8.0) |
| `Yuta_Okkotsu_Texture.png` | **128x128** Yuta atlas |
| `Yuta_Okkotsu_Rika_Texture.png` | **128x128** atlas for Rika + all cursed-energy effects (alpha) |
| `*_glowmask.png` | Emissive masks (GeckoLib `_glowmask` naming) for both atlases |
| `Yuta_Okkotsu_Preview.png`, `Yuta_Face_Closeup.png` | Character previews (software-rendered, see "Testing") |
| `Abandoned_Jujutsu_Academy.nbt` | Vanilla structure template, 45x27x45, DataVersion 3465 (1.20.1) |
| `Academy_Preview_*.png`, `Academy_Block_Palette.md`, `Academy_Coordinates.md` | Structure previews, palette, key coordinates |

## One mob, Rika as an attachment
There is exactly one mob. Rika is the bone group `rika_root` (about 45 bones: body, neck, head, jaw, hair, crown, two 3-bone arms, 10 finger chains of 2 segments each, aura shards, orbs) parented to `root`. She is hidden by scale 0 and only scaled up inside `rika_manifestation`, `rika_strike` and `summoned_arrival`.

## Texture routing (needs a render layer)
Yuta bones use `Yuta_Okkotsu_Texture.png`. Every bone starting with `rika_` or `fx_` UV-maps into `Yuta_Okkotsu_Rika_Texture.png`. In GeckoLib, render those bones with a second layer using that texture, ideally translucent and with the glowmask. With a single-texture renderer, Rika and the effects will show garbage UVs. In the .bbmodel the cubes already carry their per-face texture assignment.

## Bones toggled by animation
`katana` (drawn sword, right hand), `sheathed_hilt` (hilt on the saya in the left hand), `fx_blade_aura`, `fx_slash`, `fx_aura`, `fx_heal`, `fx_flash`, `rika_root`. Their default is stored as a scale key at t=0 in every animation, so they never leak between animations. Hidden = scale 0.

## The 10 animations
| Animation | Length | Loop |
|---|---|---|
| idle | 4.0s | yes |
| walk | 1.0s | yes |
| run | 0.6s | yes |
| katana_combo | 2.4s | no — draw, horizontal slash, backhand slash, overhead chop with lunge, recovery, re-sheathe |
| cursed_slash | 2.2s | no — draw, charge, overhead cut that launches `fx_slash` crescent forward, recovery |
| rika_manifestation | 3.2s | no — Yuta raises the ringed hand, Rika rises behind him, roars (jaw open), dissolves |
| rika_strike | 2.4s | no — Rika emerges, right arm winds back, sweeps forward, retreats; Yuta thrusts in sync |
| reverse_cursed_energy | 3.2s | no — crouch, hands to chest, spiralling `fx_heal` energy |
| summoned_arrival | 2.8s | no — flash, materialises with katana, lands, slash, Rika briefly appears, sheathe |
| death | 2.6s | no — recoil, buckle, collapse onto back, energy fades (non-graphic) |

Attacks have wind-up, strike, visible effect, follow-through and recovery keyframes.

## Academy (north = -Z, gate on the north edge)
Ground surface is y=8, underground rooms y=1-7, roof peak y=26. Place the template's corner at the lowest corner of the footprint. Full list in `Academy_Coordinates.md`. Contents: torii-style gatehouse with two-tier roof, three connected wooden buildings (training hall, archive hall, main hall) with hip roofs, two covered corridors, central courtyard, circular arena (radius 8) with crying-obsidian seal, raised sword-training platform, stone lanterns, two ponds and cherry trees, hidden stair behind the altar bookshelf, cursed-energy chamber, underground archive with lecterns holding scroll books, and a treasure vault behind a bookshelf door in the archive. Chests use vanilla loot tables (`stronghold_library`, `bastion_treasure`, `ancient_city`); swap in your own tables as needed. A structure block in DATA mode with metadata `yuta_okkotsu_spawn` marks Yuta's spawn at the arena centre.

## Testing — what was and wasn't verified
Verified by script:
- geo.json, animation.json and .bbmodel are valid JSON; every animated bone exists in the geometry; keyframe times are sorted and within each animation's length; every cube's UV box fits inside 128x128; both textures are exactly 128x128.
- The .nbt was written by a hand-written NBT writer and read back by an independent reader: size 45x27x45, all positions in range, no duplicate positions, all palette indices valid, 85 distinct block ids.
- A walkability search from the gate reaches the arena, platform, all three buildings, the secret stair, chamber, tunnel, underground archive and vault, treating the two bookshelf doors as passable.
- Poses were checked visually with a custom software renderer.

**Not tested:**
- Nothing was opened in Blockbench, GeckoLib or Minecraft. The .bbmodel uses my reconstruction of Blockbench's Bedrock conventions (x-mirrored positions, rotation sign flips); if models or animations look mirrored on import, that conversion is the first suspect.
- GeckoLib sign conventions for animation rotation were reasoned out, not run in-game.
- Block-state strings were checked by eye against 1.20.1 names only; the structure was not loaded in-game. The structure was written with `minecraft:grass`, which was renamed in 1.20.3, so on newer versions use a data fixer or replace it.
- Colour, facial likeness and cursed-energy look were judged only in the software preview; the face is only 8x8 pixels.
- The Yuta glow mask is nearly empty (eye irises and ring gem, 8 pixels).
- Roof curvature is approximated with slab lips and corner tips, not true curved geometry.
