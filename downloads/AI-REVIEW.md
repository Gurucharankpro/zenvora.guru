# Independent reviewer handoff — X1 on 1.7.0

Read TEST-REPORT.md and X1_INPUT.json first. This is an ASM binary integration,
not the original complete Forge development project. No live game pass is claimed.

1. Reassemble all split volumes and compare the resulting JAR hash. Use a clean
   Forge 1.20.1 / 47.3+ client AND server, Java17; install one mod version only.
2. Audit integration/x1_assets.py, X1Hooks.java, X1HooksTest.java and X1 additions
   in PatchMod.java. Commands are static cosmetic functions executed with server
   command source; inspect trust boundaries, permissions, SNBT and their effect
   only on tagged ItemDisplay entities. Check exact MinecraftServer/Commands
   SRG member linkage against the real runtime. Fixtures cannot establish this.
3. Check gameplay callbacks: summon only on success (including native boolean
   returns separated by labels); extraction only on accepted record; recall
   before removal; bond old/new rank; accepted orders; actual catch-up teleport;
   validated strike guard/trail; positive uncanceled post-armor impact at victim;
   natural guardian fresh/resume branches; valid shadow death. Compare gameplay
   outcomes against a COPY of 1.7.0, not an earlier release.
4. Test ordinary, EPIC, LEGENDARY, CRAZY/CHOSEN and mythic summons; denied summons
   must have no false spawn VFX. Test recall, capture, rank change/no change,
   follow/stay commands, teleport, melee/projectile hits, canceled/zero damage,
   shield, guardian spawn/resume and disappearance. Check 4 sound clips by ear.
5. Validate ItemDisplay models, vertical/ground anchoring, transparency, timing,
   alpha endings and separate instance frame-zero starts. Verify only one weapon
   variant appears, and all effects end. Auras are explicitly finite bursts.
6. Inspect 24-display cap per dimension; scoreboard objectives sbx1_age/sbx1_count
   and storage shadowsovereign:x1. Kill must target only tagged ItemDisplays;
   clocks should not reset old instances. Test custom dimensions, chunk unload,
   reload/restart, player logout, multiple players and command failures. Profile
   command traffic, entity packets, GPU atlas memory and low-end client behavior.
7. The internal x1_visual item must be hidden from all four creative tabs. Check
   old station/decor classification including Monolith enchanting table, all old
   creatures, 500 learning quests, structures, sounds and army persistence.
8. UI badges/widgets, directional rally wave, dome/ring meshes, persistent loops
   and performance-preset UI are NOT integrated. Do not mark them as working.
   Sprite barriers are cosmetic and do not add a new defense power.

Manual visual inspection on a disposable world (operator permission):
execute as @p at @s run function shadowsovereign:x1/event/02
The native server tick initializes objectives automatically. Do not mistake a
manual function demo for proof that native gameplay hooks work.

Reproduce with all previous extracted input packs, plus extract the uploaded X1
archive into INPUTS/X1_Shadow_Awakening preserving internal paths. Run:
bash integration/build.sh ORIGINAL.jar INPUTS BUILD_DIR
Put the published 1.7.0 JAR in BUILD_DIR/dist for preservation comparisons.
Dependencies/base hash are pinned. Compiled API fixtures are never shipped.
Record runtime logs, exact versions, effect IDs and reproduction steps for issues.
