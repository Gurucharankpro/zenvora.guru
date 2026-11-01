# 05 — Asset & Lang Guide

All art is original pixel art in dark blue, cyan, black and subtle purple. **Deviation (intentional):** two status glyphs use warm accents purely for accessibility — `status_failed` (red) and `status_warning` (amber); everything else stays in the palette. Shapes also differ per state, so colour is never the only cue.

## Files
| File | Size | Suggested resource path (`<modid>` = the mod's id) |
|---|---|---|
| `Assets/Icons_32/icon_smart_follow.png` | 32×32 | `assets/<modid>/textures/gui/x4/icon_smart_follow.png` |
| `icon_regroup`, `icon_building_mode`, `icon_combat_assist`, `icon_recovery`, `icon_performance_mode` | 32×32 | same folder |
| `Assets/Icons_64/*_64.png` | 64×64 (nearest-neighbor 2×) | optional hi-res use |
| `Assets/Status_8/status_{active,stuck,separated,recovering,failed,idle,warning}.png` | 8×8 | same folder |
| `Assets/Status_32_preview/*` | 32×32 | previews only |
| `Assets/HUD/hud_panel_64x40.png` | 64×40, 9-slice margin 4 | same folder |
| `hud_panel_header_64x14`, `hud_button_{normal,hover,pressed,disabled}` (48×16, 9-slice margin 3), `hud_toggle_{on,off}` (16×8), `hud_toast_{info,success,failure}` (96×20, 9-slice margin 3), `hud_slot`, `hud_slot_selected` (20×20), `hud_counter_chip` (32×12), `hud_bar_{empty,half,full}` (64×6), `hud_divider` (64×3) | | same folder |
| `Assets/Source/generate_assets.py` | editable source | run `python3 generate_assets.py <out>`; every pixel/palette is defined in code |

Do not scale non-integer; use nearest-neighbor. The 9-slice margins are suggestions — the GUI implementer chooses the blit method.

## Previews
`Previews/*.png` are **concept diagrams** illustrating each rule (labelled as such) — not screenshots of a working feature.

## Suggested lang keys (proposed, not existing)
```json
{
 "x4.autopilot.status.active": "Active: %s",
 "x4.autopilot.status.stuck": "Stuck: %s",
 "x4.autopilot.status.separated": "Separated: %s",
 "x4.autopilot.mode.follow": "Follow",
 "x4.autopilot.mode.building": "Building",
 "x4.autopilot.mode.combat_assist": "Combat Assist",
 "x4.autopilot.mode.performance": "Performance",
 "x4.autopilot.button.regroup": "Regroup",
 "x4.autopilot.notify.regroup_started": "Regrouping the army...",
 "x4.autopilot.notify.regroup_done": "Army regrouped (%s / %s)",
 "x4.autopilot.notify.recovery_failed": "Recovery failed for %s shadows",
 "x4.autopilot.notify.no_safe_spot": "No safe position found",
 "x4.autopilot.notify.cooldown": "Regroup is on cooldown"
}
```
