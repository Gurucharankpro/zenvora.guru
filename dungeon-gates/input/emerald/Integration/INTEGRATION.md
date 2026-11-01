# P1-1 — The Emerald Trial Gate: Integration Instructions (for the second developer/AI)

Target: Minecraft Java 1.20.1, Forge 47.x, GeckoLib 4.x (`software.bernie.geckolib:geckolib-forge-1.20.1`). Assets only — no Java code is included.

## 1. Namespaces / resource layout
Everything under each folder's `assets/` tree is copy-ready into your mod's `src/main/resources/assets/`.
* Mobs, portal, trophy use namespace `soulbound` (change by search-and-replace of `soulbound:` / `assets/soulbound` if your mod id differs).
* The 12 blocks use namespace `soulbound_decor` (same as your Decor Pack 06). Dungeon NBT references `soulbound_decor:*` block ids — register them with exactly these registry names (see `Integration/blocks_manifest.json` for block-state properties).
* Creature resources: geo `assets/soulbound/geo/<id>.geo.json`, animations `assets/soulbound/animations/<id>.animation.json` (names `animation.<id>.<anim>`), texture `assets/soulbound/textures/entity/<id>.png`, optional GeckoLib glow layer `<id>_glowmask.png`.

## 2. Animation sign convention (IMPORTANT, verify first)
Rotations are authored in the Bedrock/Blockbench convention used by GeckoLib JSON: **+X pitches the front (-Z) down, +Y turns right (clockwise from above), +Z right-hand roll**, applied Z·Y·X about each bone pivot. Positions are only used on Y (and tiny Z on the portal label); all lateral movement is rotation or scale, to avoid X-sign ambiguity. All previews were rendered with this convention by my own software renderer, **not by GeckoLib or Blockbench**. Sanity test in game/Blockbench: `crystal_sentinel death` must slump FORWARD; `emerald_root_warden heavy_slam` must raise arms overhead then slam forward. If an axis is mirrored, negate that axis for rotation values in the JSON with a script — geometry itself is unaffected.

## 3. Portal
See `portal_spec.json`. Three animated texture layers (distortion/surface/border) are vertical strips with `.mcmeta`; render them as translucent, double-sided, full-bright layers of `emerald_portal_rift.geo.json`. Runes: `emerald_portal_runes.geo.json` (8 planes) with `idle` animation (20 s orbit). Ground glow: `emerald_portal_ground.geo.json`. GeckoLib animated textures follow the `.mcmeta` next to each PNG (if you render the portal as a block-atlas animated texture instead, use the same strips). Trigger = contact with a 2.6 x 4.1 x 0.9 volume, cooldown 60 ticks. Label: `label_layout.json` + `portal_label_panel.png`.

## 4. Overworld generation
See `overworld_placement_spec.json`. No blocks are generated around the portal.

## 5. Dungeon
* `Dungeon/structures/*.nbt`: 4 quadrant pieces (32x28x32, each loadable in a structure block) + FULL 64x28x64 piece. `structures_vanilla_fallback/` has the identical layout in vanilla blocks so you can test the structure immediately.
* Place pieces at origin+offset from `dungeon_spec.json`, rotation NONE.
* Markers are DATA structure blocks embedded in the NBT (`metadata = "p1_1:<name>"`). Read positions from `dungeon_spec.json` (`markers`), then replace the marker block with floor material. Spawn target = marker + (0,1,0).
* Entities are NOT in the NBT (entity list is empty) — spawn from markers.
* Return portal inactive until boss death. Recovery requirements are listed in `dungeon_spec.json`.
* Use the mod's existing Soul Bound Dimension with an instance grid (no new dimension).

## 6. Creatures
`creature_spec.json` lists bone names, animation names, per-animation length/ticks and event timings (windup end, impact, projectile spawn, shield hold, etc.). `animation_timing_manifest.json` is the same data stripped down. Hit detection should fire at the `impact*` events (ticks = seconds*20). Suggested GeckoLib state mapping is in `geckolib_state_mapping`.
* `emerald_root_warden`: bones `shieldFront` and `orb` are hidden by scale 0.001 in every animation that doesn't show them — keep that animation set intact or hide the bones by code.
* Existing defeat-and-summon mechanics: the assets assume the creature ids `soulbound:mossbound_skulker`, `soulbound:crystal_sentinel`, `soulbound:emerald_root_warden`; hook your existing "defeat → summonable ally" logic to the death event after the `death` animation (`collapse_complete` event). Summoned-arrival animations are NOT included (the brief specified exact animation sets).

## 7. Blocks / trophy
Standard block/item model JSON. Emissive/light levels are suggestions in `blocks_manifest.json`. The chest model is static (no lid animation, no block-entity renderer); loot/inventory is yours. Trophy item model: `assets/soulbound/models/item/emerald_wardens_crown.json` (display transforms for gui/hand/head), plus `emerald_wardens_crown_display.json` (crown on pedestal).
