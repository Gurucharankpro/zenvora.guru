# 04 — Test Scenarios

All scenarios are **unexecuted design tests** (no implementation existed). Mark each Pass/Fail after integration. "Army" = ≥ 20 companions unless stated. Run every group once on a **dedicated server** and once on a **single-player/integrated** world.

| ID | Area | Setup / Steps | Expected result |
|---|---|---|---|
| F1 | Follow | Walk straight for 30 s with 10 mixed-size companions | None stands within `followClearance` in front; owner never body-blocked; formation shape preserved |
| F2 | Follow | Include a large companion next to small ones | Large one keeps wider spacing; no overlap |
| F3 | Follow | Walk through a 1-wide tunnel 20 blocks long | Single-file queue; no two share a cell; formation restores ≤ a few seconds after exit |
| F4 | Follow | Change formation via existing command mid-walk | New formation applied instantly; no leftover offsets |
| F5 | Follow | Owner stands still 60 s | Companions settle, no jitter/pacing; smart checks drop to idle tier |
| S1 | Stuck | Trap a companion behind a 3-high wall gap | Repath tried ≤ `repathAttempts`; then regroup spot; no teleport when `allowEmergencyTeleport=false`; FAILED shown after ladder |
| S2 | Stuck | Same, teleport enabled, safe spot near owner | Teleports once to a safe cell; cooldown respected |
| S3 | Stuck | Owner above a lava lake, only lava-adjacent cells nearby | No teleport; FAILED + notification |
| S4 | Stuck | Owner inside a 1×1 shaft / under a ceiling of 1 block | Destination rejected (headroom); never inside walls |
| S5 | Stuck | Companion on a boat/minecart/over void edge | No teleport into void/vehicle misuse; no crash |
| S6 | Stuck | Companion under `stay` order and far away | Not recovered/teleported; reported as holding |
| S7 | Stuck | Unload chunk around companion / owner changes dimension | No teleport across dimensions; no exceptions; resumes safely after reload |
| B1 | Build | Toggle building mode on, mine a 3×3 area | Companions clear zone; never stand in front of mined blocks; mining unobstructed |
| B2 | Build | Place blocks rapidly in a line while walking | Zone follows the look target; companions do not block placement |
| B3 | Build | Toggle off | Companions return to formation without teleporting |
| B4 | Build | Explicit attack command during building mode | Engaged companions fight; others keep clear |
| C1 | Combat | Owner attacks one zombie among several | Army prioritises owner's target; explicit commands unchanged |
| C2 | Combat | 20 companions vs 1 enemy | ≤ `maxAttackersPerTarget` engage in melee slots; rest take other targets or support positions |
| C3 | Combat | Two enemies at similar distance | No rapid target flip-flopping (hold ≥ `minTargetHoldTicks`) |
| C4 | Combat | Owner shooting a target through a corridor | Companions avoid the line-of-sight cone when alternatives exist |
| C5 | Combat | Owner below 40 % health attacked by a mob | Nearest `protectCount` idle companions engage the attacker |
| C6 | Combat | Explicit target command on mob A while mob B hits owner | Commanded companions stay on A; uncommanded ones may protect |
| C7 | Combat | Passive/tamed/allied entities nearby | Never targeted |
| P1 | Perf | 100 companions idle near owner | Mean server tick cost of autopilot measurably lower than `followInterval`-only; no entity logic skipped (animations, damage still work) |
| P2 | Perf | 100 companions, owner sprinting | Path requests/tick ≤ `maxPathRequestsPerTick`; nobody starves > `maxStarveTicks` |
| P3 | Perf | Toggle Performance Mode | Intervals/budgets scale as configured; behavior still correct |
| P4 | Perf | Set all budgets to minimum | Degrades gracefully to existing mod behavior; no exceptions |
| M1 | MP | Two owners with armies side by side | Each regroup/building mode affects only its own companions |
| M2 | MP | Forged packet from player A toggling player B's mode / regrouping B's army | Rejected, logged; no state change |
| M3 | MP | Spam regroup/toggle packets | Rate-limited; no lag; owner gets at most one cooldown notice |
| M4 | MP | Owner logs out/in, server restarts | Flags restored; counters reset; no teleport during grace period |
| M5 | MP | Owner dies and respawns / companions follow dimension travel via existing mechanic | No duplicate companions; no orphan queue entries |
| U1 | UI | HUD counters while army changes | Active/stuck/separated counts correct within `statusSyncTicks` |
| U2 | UI | Click Regroup | Staggered regroup; cooldown honored; completion/failed notice aggregated |
| U3 | UI | Failure notifications | Aggregated message, rate-limited, text from lang keys |
| L1 | Save | Save with flags on, reload | Flags persist; old saves without flags load with defaults |
| L2 | Compat | Run with autopilot `enabled=false` | Behavior identical to the original mod (regression baseline) |
| L3 | Compat | Run each feature flag off individually | Only that feature disappears; no residue |
