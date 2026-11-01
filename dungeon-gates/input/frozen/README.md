# P1-2 — THE FROZEN GRAVE GATE — Art / Model / Animation package
Minecraft Java 1.20.1 · Forge 47.x · GeckoLib 4.x (recommended: geckolib-forge-1.20.1 **4.4.9** — not tested here, verify on your side)

**Scope of this ZIP:** all 3D models, textures, animations, block/decor assets, portal visuals, effect assets, trophy.
**Not included (by request):** any Java code, dungeon NBT structures, sounds.

Namespace used everywhere: `soulbound` (placeholder). To change: replace `soulbound` in all JSON under `Resources/` and rename `Resources/assets/soulbound` + `Resources/data/soulbound`.

## Folders
| Folder | Content |
|---|---|
| `Portal/` | gate `.bbmodel`, `.geo.json`, `.animation.json` (idle, activate), 128x128 texture + `_glowing`, animated surface (8 frames 64x64 + strip + .mcmeta), surface geo/bbmodel, `Effects/` (activation burst, entry vortex, 4 particle textures), `Label/` (background PNG, `label_spec.json`, mockup), preview, inventory icon |
| `Blocks_And_Decor/` | 12 folders (`01_...`–`12_...`): `.bbmodel`, texture, `_e` emissive (where glow), preview + `ALL_12_BLOCKS_PREVIEW.png` |
| `Mobs/` | Frostgrave Prowler, Chainbound Sentinel: `.bbmodel`, `.geo.json`, `.animation.json`, 128x128 PNG, `_glowing.png`, preview, icon, `_animation_timings.json` |
| `Boss/` | Frozen Gravekeeper (same file set) |
| `Trophy/` | The Frozen King's Crown: `.bbmodel`, 64x64 texture, `_e`, preview, inventory icon |
| `Resources/` | ready-to-drop `assets/soulbound/...` (blockstates, block/item models, textures, geo, animations, particles, lang) + `data/soulbound/loot_tables` (12 blocks, vault chest, boss) |
| `Integration/` | `VERIFICATION_REPORT.txt` |
| `Generator_Scripts/` | Python scripts that regenerate everything (`python3 build_creatures.py; build_blocks.py; build_portal_trophy.py`) |

## Creatures (GeckoLib)
Paths: `geo/entity/<id>.geo.json`, `animations/entity/<id>.animation.json`, `textures/entity/<id>.png`, glow layer `<id>_glowing.png` (GeckoLib AutoGlowingGeoLayer convention).
Animation ids are `animation.<id>.<name>`; ids: `frostgrave_prowler`, `chainbound_sentinel`, `frozen_gravekeeper`.
- Prowler: idle, walk, run, alert, jump, claw_attack, frost_bite, ice_howl, spawn, death
- Sentinel: idle, walk, run, guard, chain_swing, ice_pulse, heavy_strike, stagger, spawn, death
- Gravekeeper: idle, walk, run, battle_roar, axe_slam, frozen_sweep, ice_spike_attack, royal_guard, phase_transition, death
Looping: idle/walk/run loop; alert, guard, royal_guard, death are `hold_on_last_frame`; the rest play once.
`*_animation_timings.json` gives windup / hit / recovery seconds that match actual key poses (use for damage timing).
Size targets: Prowler ≈1.6 blocks incl. ears (body 1.4), Sentinel ≈2.3, Gravekeeper ≈3.9 with crown (≈3.7 body).
Extra bones to drive: `chest_crystal` (Sentinel, scaled for ice_pulse), `crown`/`cape`/`cape_low`/`axe` (Gravekeeper), `weapon` (Sentinel).

## Blocks (12)
Frostgrave Stone, Polished Frostgrave Stone, Cracked Icebound Bricks, Royal Frost Marble, Glowing Blue Ice Crystal (cube_all) · Frozen Crystal Cluster (blockstate `facing`) · Silver Chain Column · Winterveil Vines (vine-style multipart: up/north/east/south/west) · Ghostfrost Flower (cross) · Royal Ice Lantern (`hanging`) · Frozen Memorial Statue (`facing`) · Winter Royal Chest (static model, `facing` — a real chest needs a BlockEntity; model is the visual).
`_e.png` files are optional emissive maps (not used by vanilla).

## Portal
Gate ≈ 5.5 blocks wide × 7+ blocks tall (with crown). Surface is a separate model `frozen_grave_portal_surface` (animated strip, `frametime 2`, interpolate).
**Floating label:** `Portal/Label/label_spec.json` is DATA ONLY (4 lines, colors, distance 32, fade 24, billboard). A PNG cannot float in-world; render it from client code (BlockEntityRenderer). `label_MOCKUP_not_ingame.png` is only a mockup.
