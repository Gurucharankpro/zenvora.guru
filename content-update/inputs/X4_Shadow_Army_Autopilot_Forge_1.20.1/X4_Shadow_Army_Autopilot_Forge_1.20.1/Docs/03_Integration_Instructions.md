# 03 — Integration Instructions (for the implementing developer)

> No mod source was available while this pack was produced. Nothing here claims knowledge of the mod's class names. **Step 1 is mandatory: inspect the source.**

## Step 1 — Inspect the existing mod (fill this table before coding)
| Question | Where to look | Your finding |
|---|---|---|
| Companion entity class(es) and base class (e.g. a vanilla `PathfinderMob`/`TamableAnimal` subclass?) | `entity` package, entity registry | |
| How ownership is stored (UUID field? NBT key? capability?) | companion class, `readAdditionalSaveData`/`addAdditionalSaveData` | |
| AI: goals / brain / custom controller; which goal handles follow, stay, attack | `registerGoals`, custom goal classes | |
| Formation system: data structure, who computes slots, how often, how assigned | classes named like Formation/Army/Squad | |
| Command system: Brigadier commands, keybinds+packets, item-based commands; state fields for stay/follow/attack/target | `command`, `network`, `client` packages | |
| Existing teleport/regroup logic | search "teleport" in companion/army code | |
| Persistent per-player data (capability, SavedData, player NBT) | `capability`, `data` packages | |
| Existing config spec and registration | `Config*` classes, `ModLoadingContext.registerConfig` calls | |
| Networking channel (`SimpleChannel`?) and packet registration | `network` package | |
| Existing HUD/GUI (overlays, screens, key mappings) and texture folder / modid | `client` package, `assets/<modid>` | |
| Size categories of companions (bounding box dimensions per entity type) | entity type registration | |

## Step 2 — Decide the attachment strategy (least invasive first)
1. **A small `AutopilotManager` (new class) driven from the server tick** (e.g. a Forge server/level tick event — verify the exact event in 1.20.1 for your Forge build) holding per-owner state in memory; it *observes* companions and *requests* actions through the companion's **existing public API** (follow target/slot, navigation, teleport helper). If a needed hook is missing, add the smallest accessor to the existing class rather than copying logic.
2. Where the existing follow/formation code asks "where should I stand?", add one optional adjustment call (`slot → adjusted slot`) guarded by the config flag.
3. Avoid mixins/ASM unless the existing mod already uses them.

Candidate vanilla/Forge pieces *to verify exist in your mappings before use*: `PathNavigation#createPath/moveTo/isDone`, `Entity#getBoundingBox/getBbWidth`, `Level#getBlockState/noCollision`, `BlockPathTypes`, `ForgeConfigSpec`, `SimpleChannel`, `RegisterGuiOverlaysEvent`, `RegisterKeyMappingsEvent`, `EntityTeleportEvent`. These are listed as search leads, not as guaranteed signatures.

## Step 3 — Implementation order (each step shippable and testable)
1. Config spec + master switch (everything off-able, defaults from `02`).
2. Per-owner state + bounded scheduler/budget (`performance`) — with no behavior change yet; verify counters in debug log.
3. Safe-position finder (pure function over `Level`) with unit-style checks via test scenarios.
4. Anti-stuck ladder (repath → regroup → optional teleport → fail).
5. Smart follow spacing / single-file.
6. Building mode toggle + zone.
7. Combat assist (target arbitration only; reuse existing targeting).
8. Status sync + HUD + regroup button + notifications (assets in `Assets/`).
9. Save/load of the three flags.
10. Multiplayer hardening pass (see §7 of the spec) and `04_Test_Scenarios.md`.

## Step 4 — Rules the implementation must keep
- Never skip or override required entity ticking; skip only the autopilot's own checks.
- All work on the server thread; client packets validated (ownership, rate limit).
- No per-tick allocation-heavy scans: reuse per-owner snapshots; bound every search (radius, count).
- Teleport only through the validated safe-position routine and only if config allows.
- Remove companions from caches/queues on death, dismissal, dimension change, owner logout.

## Step 5 — Assets
Copy `Assets/` PNGs into `assets/<modid>/textures/gui/x4/` (see `05_Asset_and_Lang_Guide.md` for suggested names, 9-slice margins, lang keys). Textures are plain transparent PNGs; HUD panels are designed as 9-slice (border 3–4 px).

## Step 6 — Report back
Return a short note listing: classes touched, hooks added, config keys actually implemented, any rule from `01` you changed and why, and the results of `04_Test_Scenarios.md`.
