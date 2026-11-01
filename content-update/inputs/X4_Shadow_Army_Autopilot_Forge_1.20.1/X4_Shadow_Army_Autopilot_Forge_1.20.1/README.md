# X4 — Shadow Army Autopilot (Forge 1.20.1) — Design & Asset Pack

**What this is:** a complete design specification plus real GUI/HUD art for a quality-of-life layer on top of the existing Soulbound Army / Soul Bound Dimension mod.
**What this is NOT:** code. No Java, no standalone mod, nothing here is implemented or tested in-game, and the original mod source was **not available**, so no existing class or method is referenced. Everything marked *requires implementation* below needs the second developer.

## Contents
| Path | What |
|---|---|
| `Docs/01_Behavior_Specification.md` | Spacing, formation preservation, safe recovery, building zone, target priorities, performance budgets, multiplayer ownership checks, save/load, command compatibility, enable/disable |
| `Docs/02_Configuration_Schema.md`, `x4_autopilot-server.toml.example`, `x4_autopilot.schema.json` | 62 proposed config keys with defaults and ranges |
| `Docs/03_Integration_Instructions.md` | Mandatory source-inspection checklist, attachment strategy, implementation order |
| `Docs/04_Test_Scenarios.md` | 38 test scenarios (unexecuted) |
| `Docs/05_Asset_and_Lang_Guide.md` | Asset table, 9-slice notes, suggested lang keys |
| `Assets/Icons_32`, `Icons_64` | 6 feature icons: Smart Follow, Regroup, Building Mode, Combat Assist, Recovery, Performance Mode |
| `Assets/Status_8`, `Status_32_preview` | 7 status indicators (active, stuck, separated, recovering, failed, idle, warning) |
| `Assets/HUD` | 18 HUD pieces: panels, buttons (4 states), toggles, toasts, slots, chip, bars, divider |
| `Assets/Source/generate_assets.py` | Editable source that regenerates every texture and preview |
| `Previews/` | 6 feature concept diagrams, overview, icon sheet |

## Implementation status
| Feature | Design | Assets | Code |
|---|---|---|---|
| Smart following | done | icon | **requires implementation** |
| Anti-stuck recovery | done | icon, status glyphs | **requires implementation** |
| Smart building mode | done | icon | **requires implementation** |
| Combat assistance | done | icon | **requires implementation** |
| Performance budgets | done | icon | **requires implementation** |
| Quick recovery & status HUD | done | HUD set | **requires implementation** |

## Honest limits
- Defaults are proposals; tune after profiling a real army.
- Palette note: red/amber appear only on failure/warning glyphs for readability.
- Previews are concept diagrams, not in-game captures.
- Files were checked for existence, PNG sizes and transparency by script only.
