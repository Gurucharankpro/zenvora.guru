# P1-1 — THE EMERALD TRIAL GATE  (Level 1, Gate 1/5, difficulty 1/5, emerald)
Complete asset package for Minecraft Java 1.20.1 / Forge 47.x. **Assets and specifications only: no Java code, no mod, no new dimension, no gameplay logic.**

## Content inventory
| Folder | Contents |
|---|---|
| Portal/ | `.bbmodel` (editable), 3 render-layer geos + animation, animated strips (surface 16f, border 16f, distortion 8f), emissive strips, 8 rune glyphs x 4 glow frames, particles (spark 6f, mist 8f, strand 4f), activation burst 12f, entry flash 6f, ground projection, label panel PNG + layout, individual frames, previews. No physical frame/supports: only zero-thickness planes. |
| Dungeon/ | The Forgotten Emerald Sanctuary 64x28x64, 6 rooms. Real `.nbt` (DataVersion 3465): 4 quadrant pieces + FULL piece, in custom-block and vanilla-fallback variants; block palettes; `dungeon_spec.json` (rooms, markers, coordinates, boundaries, assembly); floor plan, 4 cross-sections, isometric cutaways. |
| Blocks_And_Decor/ | 12 environmental assets: textures (16/32/64), block + item models, blockstates, editable .bbmodel, individual previews + overview. |
| Mobs/ | Mossbound Skulker (5 animations) and Crystal Sentinel (5 animations): .bbmodel, geo.json, animation.json, 128x128 texture, glowmask, previews. |
| Boss/ | Emerald Root Warden: 8 animations, same file set. |
| Trophy/ | The Emerald Warden's Crown: .bbmodel, 64x64 texture, item + display models, inventory icons 32/16, previews. |
| Previews/ | Portal scene (PNG + GIF), frames, creature hero/pose sheets, dungeon views, blocks overview, `ASSET_OVERVIEW.png`. |
| Integration/ | `INTEGRATION.md`, `portal_spec.json`, `overworld_placement_spec.json`, `dungeon_spec.json`, `creature_spec.json`, `animation_timing_manifest.json`, `blocks_manifest.json`, `asset_manifest.json` (sha256 of every file), `verification_report.json`. |

Counts: 1 portal, 1 dungeon, 12 blocks/decor, 2 regular mobs, 1 mini-boss, 5+5+8 = 18 animations, 1 trophy. Creature textures are exactly 128x128.

## Dependencies
Minecraft 1.20.1, Forge 47.x, GeckoLib 4.x for the 3 creatures (and optionally the portal layers). Blocks use vanilla model JSON (`render_type: minecraft:cutout` on cutout models).

## What was verified (automated, by script on the generated files — 158/158 checks passed)
PNG sizes/modes and transparency; creature UV boxes inside the 128x128 sheet, non-overlapping and fully painted; bone hierarchy validity; animation counts (5/5/8), no duplicate animations, every animation keyframes at least 5 bones / 20 keyframes; bbmodel element counts match geo; embedded textures; animated-strip frame counts; block model UV/coordinate/rotation ranges and texture/blockstate references; every JSON parses; each `.nbt` is gzip NBT that my reader parses back with block count = volume and valid palette indices, and the custom-variant FULL piece round-trips to the master grid; a BFS over the dungeon grid reaches every spawn marker, the boss spawn, trophy display, return portal anchor, exit position and chest from the arrival point. Full list: `Integration/verification_report.json`.

## NOT tested / unavailable (please read)
* **Nothing was opened in Blockbench, GeckoLib or Minecraft.** The previews are rendered by my own software rasteriser from the generated geo + textures (this also confirms UV mapping consistency, but it is not the game renderer).
* **Animation axis/sign convention is unverified in GeckoLib/Blockbench** (see INTEGRATION.md §2). Poses/keyframes are genuine and authored per bone; direction of some rotations may need an axis flip. Animations inside the `.bbmodel` files use the same numbers; Blockbench may display some rotations with inverted X/Y compared with the JSON.
* Hit/impact timings are authored markers in the manifest, not engine events.
* `.nbt` files were written by my own encoder and read back by my own reader; they follow the vanilla structure-template format (DataVersion 3465, palette + blocks, DATA structure-block markers) but were **not loaded by Minecraft**. Use the vanilla-fallback variant first to rule out custom-block issues. Vanilla fallback states assume `cave_vines_plant`, `amethyst_cluster`, `soul_lantern` properties as listed in `Dungeon/block_palettes.json`.
* Unrelated gameplay needs (chest loot, instance management, spawning mobs, boss fight logic, portal teleport, particles code, label rendering, light levels, sounds) are the integration developer's job. No sounds are supplied.
* Block model `render_type` and the chest model are static; no lid animation. Crystal/lantern emission is by light level only (no emissive block-model layer).
* Dungeon is built from a procedural generator, not hand-sculpted: layout and verification are real, but aesthetics should be checked in-game (the 3D previews are 2D/isometric block renders, not ingame screenshots).
* The dungeon is split into 4 quadrant pieces + one FULL piece rather than per-room pieces; room boundaries are given as coordinates in `dungeon_spec.json`.
* The mini-boss is ~3.4 blocks to the head/crown top, ~3.7 with antler tips; the Skulker is ~1.5–1.6 blocks including ears/horns; the Sentinel ~2.2 blocks.
* Pixel art is procedurally painted (palette-quantised noise + hand-defined patterns), not hand-drawn.
* "Silver ornamental carvings" on the crown are modelled as silver trim; the crown has small floating-looking fragments but they are static in the model (add bob in code if wanted).
* Blockbench projects use the `bedrock` (creatures/portal) and `java_block` (blocks/trophy) formats; GeckoLib's own Blockbench plugin format was not used, so export via the generic Bedrock geometry/animation exporter or use the provided JSON directly.
