# Reviewer handoff — Soulbound Army 1.8.1

Read TEST-REPORT.md first. The artifact is an ASM integration into the user's
original Forge mod, not an original full development project. No live game pass
is claimed. Use only the three AI-Mod-1.8.1-part-*.zip files for the <20 MB each,
<50 MB total upload allowance. Standard Python 3/ZIP tools are enough.

1. Extract rebuild_ai_mod.py from any archive next to all three ZIPs and run
   python rebuild_ai_mod.py. For attachments in different folders, pass their
   three complete paths as arguments. The script validates each chunk hash,
   ordering, total length, full SHA-256 and inner JAR CRC. Individual chunks are
   not installable. Expected JAR SHA256:
   beeedcd097ebbfc8dc212f34b52d6fd3f6d09504aacced44bfd249aa7a16cf3d.
2. Part 01 contains integration patch/test source; part 03 contains Java
   decompiled from this exact JAR by CFR 0.152. This reference has inferred
   names, missing Forge dependencies and decompilation limitations; it cannot
   replace the original buildable source. SoulPlay.tick is not fully structured.
   Inspect the JAR bytecode for decisive signature/control-flow questions.
3. Review PatchMod.java's GloomContent factory call and OwnLinkageTest.java.
   Re-run the test against 1.8.0 to see the exact user's missing item(String,int,
   float) overload fail; 1.8.1 must invoke the existing private static food
   factory. Its hunger-zero branch creates a plain Item, hidden from all tabs.
   The previous helper checks missed this native initializer's private call.
4. Boot a clean Forge 1.20.1 / 47.3+ client and dedicated server with Java17,
   installing ONLY the fixed mod version on both. Capture latest.log and any
   crash report. Startup remains untested here; do not infer engine compatibility
   from the fixtures. Inspect actual Minecraft/Forge SRG member linkage too.
5. Exercise extraction, ordinary/EPIC/legendary/mythic summon, denied summon,
   recall, promotion/no promotion, orders, teleport, positive/canceled damage,
   guard and disappearance. Effects must trigger only on accepted actions, start
   at frame zero individually and expire. Inspect static cosmetic commands and
   function codecs, ItemDisplay anchoring/transparency and the four OGG clips.
6. Test escape from legendary encounters, block breaking and summoning other
   owned/legendary mobs; earlier open-encounter behavior remains. Test several
   players, all dimensions, logout, restart, chunk unload/reload and saves. Ensure
   cleanup only kills tagged cosmetic ItemDisplays, never normal actors. Check
   finite clocks and the 24-display cap; profile command/entity/GPU costs.
7. Verify all 51 profiles, 120 decor designs, four creative groups, 57 eggs,
   structures and 500 learning quests remain. UI badges/widgets, persistent
   auras and unsupported meshes are not integrated; do not mark them working.

Manual visual demo (operator, disposable world):
execute as @p at @s run function shadowsovereign:x1/event/02
This demo is not evidence that gameplay hooks work. Record exact versions,
logs and reproduction steps. Rebuild instructions are in integration/README.md;
the pinned original JAR and extracted input packs are required and not included
as duplicate upload artifacts. Include the prior 1.8.0 JAR in BUILD_DIR/dist for
preservation comparisons. Fixtures are never packaged in the gameplay JAR.
