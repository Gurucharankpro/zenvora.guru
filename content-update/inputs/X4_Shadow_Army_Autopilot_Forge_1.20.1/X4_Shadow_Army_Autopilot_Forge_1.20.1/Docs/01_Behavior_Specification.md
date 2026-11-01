# 01 — Behavior Specification (design only)

**Status:** DESIGN. The original mod source was not available, so no class, method, field, packet or event of the existing mod is referenced. Wherever this document says **[EXISTING]** the integrator must locate the mod's real system and map to it; **[NEW]** means logic this feature adds. Every number below is a *proposed default* exposed in config (see `02_Configuration_Schema.md`).

## 0. Principles
1. **Wrap, never replace.** Autopilot is an optional layer that nudges *where* a companion goes and *how often* smart checks run. It never replaces the companion entity, its goals/AI, animations, formations or commands.
2. **Explicit player intent always wins.** Any existing command (stay/hold, follow, attack, target, formation change, etc.) suspends the autopilot rule that would contradict it for the affected companions.
3. **Server-authoritative.** All decisions run on the logical server. Clients only display state and send requests.
4. **Fail safe.** If a check cannot prove a position/target is safe, do nothing and report; never guess.
5. **No new damage, mobs, biomes or animations.**

Terminology: *owner* = player a companion belongs to. *Companion* = any [EXISTING] summoned shadow/soldier entity owned by a player. *Slot* = a formation position produced by the [EXISTING] formation system.

## 1. Smart Following
| Rule | Default | Description |
|---|---|---|
| Clearance radius (`followClearance`) | 2.5 blocks | Companions try not to stand within this horizontal distance of the owner while the owner is moving or interacting, so the owner is not body-blocked. |
| Resume distance (`followResume`) | 6 blocks | Beyond this, normal [EXISTING] follow behavior resumes/continues. Autopilot never makes a companion follow *less* than the existing follow range. |
| Size spacing | `bodyWidth * sizeSpacingFactor (1.2) + 0.5` | Spacing between companions uses each entity's bounding-box width ([EXISTING] dimensions), so large creatures get more room than small ones. Spacing is the max of the two neighbours' values. |
| Formation preservation | see §1.1 | Slots come from the [EXISTING] formation. Autopilot only offsets a slot when it violates clearance/size spacing. |
| Narrow passage | trigger: ≤ 2 walkable blocks wide for ≥ 3 blocks of the planned path | Switch affected companions to *single-file*: ordered by distance to owner, each trailing the one ahead at its spacing. No two companions may target the same cell. Restored to formation once the passage ends (≥ `passageExitBlocks` 3). |
| Look-ahead | 0 (disabled) | No predictive steering in v1 (keeps it lightweight). |

### 1.1 Formation-preservation rules
- Never reorder, rename or discard the [EXISTING] formation data. Autopilot reads slots; it does not write the formation.
- A slot adjustment is a *temporary per-companion offset* (≤ `maxSlotOffset` 3 blocks) discarded when the cause disappears.
- Changing formation via the existing command takes effect immediately; all offsets are cleared.
- Companion order inside a formation keeps the existing assignment; single-file ordering is temporary and reverts.
- If the formation system cannot provide a slot for a companion, autopilot falls back to the existing follow behavior for it.

## 2. Anti-Stuck Recovery
### 2.1 Detection (per companion, evaluated at the bounded interval, not every tick)
A companion is *stuck candidate* when ALL hold: autopilot active for it; it currently has a movement goal (follow/formation/regroup, not a deliberate `stay`/hold and not fighting at its attack range); progress < `stuckMinProgress` (0.5 block) over `stuckWindowTicks` (60 ticks); owner within same dimension and loaded chunk. Candidates must persist for `stuckConfirmChecks` (2) consecutive checks to become **STUCK**. Also **SEPARATED** when distance to owner > `separatedDistance` (32 blocks) or different dimension.

### 2.2 Escalation ladder (strictly in order, each step bounded)
1. **Repath** — request a normal path to the current slot through the entity's own [EXISTING] navigation. Max `repathAttempts` (2), with `repathCooldownTicks` (40) between.
2. **Regroup spot** — pick a *safe nearby position* (§2.3) within `regroupSearchRadius` (6) of the companion, closest to the owner-side path, and path to it (max 1 attempt, timeout `regroupTimeoutTicks` 100).
3. **Emergency teleport** — only if `allowEmergencyTeleport` is true, step 1–2 failed, the companion has been STUCK/SEPARATED for ≥ `teleportAfterTicks` (200), the cooldown allows (`teleportCooldownTicks` 600 per companion), and a safe destination exists near the *owner* (§2.3). Uses the existing teleport mechanism of the mod/game if there is one; do not introduce a new movement ability.
4. **Fail** — mark FAILED, notify the owner (rate-limited, §6), keep the companion on its normal behavior, and retry the ladder after `failedRetryTicks` (600).
The ladder resets as soon as progress resumes or the companion reaches its slot.

### 2.3 Safe-position conditions (all required)
- Chunk loaded; inside world border; same dimension as the owner (for owner-side targets).
- Feet and head cells passable and not liquid; floor cell solid with a full upward-facing support (or valid standable block); 2 blocks of headroom, plus width clearance for big companions (use the entity's bounding box collision test against the level).
- None of: lava, fire/soul fire, magma, cactus, sweet berry bush, wither rose, campfire (lit), powder snow, cobweb, void (y below build limit + margin), in or adjacent (1 block) to lava.
- Not inside the owner's hitbox and not in a cell already reserved by another recovering companion this tick.
- No fall > `maxSafeFall` (3 blocks) from the destination to ground.
- Not in claimed/protected areas where the existing mod or other mods would forbid entity entry, if that information is available (best effort; skip if unknown).
If no position passes, the step fails — **never relax these rules**.

## 3. Smart Building Mode
- **Toggle:** one boolean per owner, default off (command/keybind/button, defined by the second AI). Only the owner (or an admin by permission level) can change it.
- **Zone (`buildZone`)** = union of (a) cylinder around the owner of radius `buildRadius` (3.5) and height −1…+3, and (b) a box of `buildTargetMargin` (2) blocks around the block the owner is currently looking at/interacting with within `buildReach` (6). Recomputed at the bounded interval and when the owner's target block changes.
- **Behavior:** companions outside the zone stay outside (hold at nearest safe cell ≥ `buildEdgeMargin` 1 block beyond the zone edge, maintaining formation order). Companions inside the zone move out at normal speed, via normal navigation, without teleporting. Companions never stand on/in the target block cell or the owner's reachable column in front of them.
- **Off-toggle:** clear all building offsets and return to existing formation slots; no teleport.
- **Combat exception:** combat assistance and explicit attack commands override building-mode spacing for the engaged companions only.
- **Auto-off:** optional `buildAutoOffTicks` (0 = never) after the owner is idle (no block interaction); default disabled.

## 4. Intelligent Combat Assistance (no new damage)
**Target selection priority** (first match wins; evaluated only for companions with no explicit order):
1. Explicit command target / attack order from the owner ([EXISTING] command state).
2. Entity the owner is currently attacking (last attacked within `ownerTargetMemoryTicks` 100).
3. Entity currently attacking/targeting the owner (protect owner), nearest first.
4. The companion's current target (stickiness, below).
5. Nearest valid hostile within `assistRange` (16) using the mod's existing hostility/valid-target rules.
Never target the owner, other owned companions, tamed/allied entities, or anything the existing rules exclude.

**Anti-flicker (target stickiness):** keep a target until it is dead/invalid/out of `dropRange`(24) or a candidate with higher priority tier appears; within the same tier switch only if the new candidate is ≥ `switchDistanceAdvantage` (3 blocks) closer and `minTargetHoldTicks` (40) have passed.

**Anti-crowding:** at most `maxAttackersPerTarget` (6, scaled up for tiny enemies / down for big companions by bounding-box size) occupy melee slots around one enemy. Extra companions are assigned the nearest other valid target or hold at `supportDistance` (4) staying in formation. Slots use angular spacing around the enemy.

**Owner line-of-sight:** companions avoid standing in the narrow cone (±`losConeDegrees` 20°) between the owner's eye and the current target within `losConeLength` (12), unless that is the only reachable position.

**Protect owner:** if the owner's health is below `protectHealthFraction` (0.4) or the owner was hurt in the last `protectWindowTicks` (60), the nearest `protectCount` (4) idle companions are assigned to the owner's attacker (tier 3 is promoted above tier 2). No override of explicit commands.

## 5. Performance Optimization
- **Tiered update intervals** (ticks between smart checks per companion): combat/recovery 2, moving-follow 5, idle near owner 20, far/low-activity 40 (see config). Jitter by entity id so checks spread across ticks.
- **Budgets per server tick:** `maxSmartUpdatesPerTick` (24) companion evaluations and `maxPathRequestsPerTick` (4) new path computations globally, `maxPathRequestsPerOwnerPerTick` (2). Work is queued round-robin; no companion can be starved for more than `maxStarveTicks` (200), after which it gets priority.
- **Caches:** per-owner state (owner position, look target, formation snapshot) computed once per interval and shared by that owner's companions. Safe-position search results cached for `safePosCacheTicks` (20).
- **Idle detection:** owner stationary (< 0.1 block/tick) and companion within slot tolerance → move to the idle tier.
- **Never disable required logic:** autopilot only skips its *own* smart checks; the entity's normal tick, goals, animations, damage handling, despawn/ownership logic always run. If the budget is exhausted the companion simply behaves as the existing mod does.
- **Performance Mode toggle** (owner-level): multiplies intervals by `perfModeIntervalMultiplier` (2) and halves budgets; for large armies/servers. Server config can force a minimum mode.
- **Diagnostics:** optional debug counter (evaluations, path requests, skipped by budget) logged at `debugLogIntervalSeconds`; off by default.

## 6. Quick Recovery & Status
Status data per owner (server → client, only to the owner): `active`, `stuck`, `separated`, `recovering`, `failed` counts; `mode` (see below); `buildingMode`, `combatAssist`, `performanceMode` flags; last notification.
- **Army behavior modes** (display/selection only; mapping to existing behaviors is the integrator's job): FOLLOW (default), BUILD, COMBAT_ASSIST, PERFORMANCE are *overlay flags*, not replacements of the existing hold/follow/attack states. Display the existing state plus overlays.
- **Regroup (one click):** server validates owner → for each owned companion not under an explicit hold order (config `regroupIgnoresHold` false by default), start the recovery ladder from step 2 toward the owner/formation slots, staggered by the budget (never all in one tick). Cooldown `regroupCooldownTicks` (40) per owner.
- **Notifications** (rate-limited `notifyCooldownTicks` 100, aggregated: "Recovery failed for N shadows"): regroup started/complete (optional), recovery failed, teleport blocked (no safe spot), budget saturated (only in debug). Provide the lang keys listed in `Docs/05_Asset_and_Lang_Guide.md`.
- Updates to clients every `statusSyncTicks` (20) *only when values changed*, only to the owner.

## 7. Multiplayer ownership checks
- Every autopilot action is keyed by the companion's [EXISTING] owner identifier (UUID). Never act on companions the sender does not own.
- Client→server requests (toggle, regroup, mode select) must: be received on the server thread, come from the sending player (never trust a player id in the payload), pass a permission/ownership test, and be rate-limited (`requestMinIntervalTicks` 5).
- Cross-owner companions are never moved by another owner's regroup/build zone. Optional `teamShareAutopilot` is off by default.
- Offline owner / owner in another dimension: companions fall back to existing behavior; no teleporting across dimensions unless the existing mod already does that.
- Dedicated server and integrated server must behave identically (no client-side authority).

## 8. Save/Load behavior
- Persist only *settings*: per-owner `buildingMode`, `combatAssist`, `performanceMode` (booleans) — in whatever persistence mechanism the mod already uses for owner/companion data (integrator to identify; do not add a parallel system without need). Defaults apply when absent (backward compatible: old saves load with autopilot flags at defaults).
- Do NOT persist transient state (stuck counters, caches, offsets, cooldowns, queued work). Rebuild after load.
- On dimension change, respawn/relog and server restart: counters reset, ladders restart, no teleports on first tick after load (`postLoadGraceTicks` 100).
- Unknown/extra data from later versions must be ignored safely.

## 9. Compatibility with existing commands (rule table)
| Existing player intent | Autopilot behavior |
|---|---|
| Stay / hold position | Companion exempt from following, building-offset, stuck-teleport; only reported as "holding". |
| Follow | Autopilot active. |
| Attack / target command | Command target wins; combat assist only handles *unassigned* companions. |
| Change formation | Offsets cleared; new slots used immediately. |
| Dismiss/despawn/unsummon | Remove from all queues/caches immediately. |
| Any command the integrator cannot classify | Treat as explicit intent → autopilot suspended for that companion until the command completes. |
Config switch `respectExplicitCommands` is **true** and must not be removable via other options.

## 10. Configurable enable/disable
Master `enabled`; per-feature `smartFollow.enabled`, `antiStuck.enabled`, `antiStuck.allowEmergencyTeleport`, `buildingMode.enabled`, `combatAssist.enabled`, `performance.enabled`, `status.enabled`. Disabling a feature must restore existing behavior with no residue.
