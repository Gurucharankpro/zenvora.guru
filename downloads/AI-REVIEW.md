# Review handoff for the independent tester

Start with TEST-REPORT.md, LATEST_INPUTS.json and integration/README.md.
This is a binary ASM integration, NOT the original complete Forge source project.
No live game pass or zero-bug certification is claimed.

1. Verify all four split archive hashes; put them together, extract the final ZIP,
   and compare the resulting JAR SHA-256 against TEST-REPORT.md. Install one mod
   version only, on a clean Forge 1.20.1 / 47.3+ client AND dedicated server.
2. Review integration/PatchMod.java: ModCreativeTab category -> legacyCategory +
   CreativeGroups; DevourerModel conditional translucent method; profiles/IDS
   append-only; original Knight removal and encounter routing. Verify class
   frames/types and actual Minecraft/Forge symbols, beyond BasicVerifier.
3. Read compile_helper.py. Its API/world fixtures deliberately simulate behavior;
   they are not a Forge runtime. Run the actual game to validate linkage/registration.
4. Confirm exactly four custom tabs labeled Decor/Eggs/Workstations/Other in both
   English/Arabic. Check Hanan/Ronin eggs; all 120 decor blocks; functional
   Odyssey/Eclipse stations, monolith_enchanting_table and other workshops; ordinary materials/books/tools.
   Decorative clockwork workbench and altars MUST be in Decor, not Workstations.
   Retired content must remain hidden. Existing tab IDs are deliberately retained.
5. Generate NEW chunks: Hanan Cairo house in Crimson Shadow, mirror temple in
   Shattered Sky; Butcher court, Devourer II sanctum, five biome relic sites.
   Inspect model orientation, safe spawn, chest loot, markers, terrain height,
   light and clearance. Sunken structures use OCEAN_FLOOR_WG. Models are oversized
   static anchor props; no functional seating/storage/multiblock is implemented.
6. Observe Glass Ronin alpha and zero-scale FX hiding, all attack/death/arrival
   clips, capture/recall/respawn. Hanan has no supplied emissive mask. New eggs
   select separate halves of an imagegen atlas; inspect UVs at runtime.
7. Start a guardian, summon allies, break/place blocks, flee, start another,
   return; repeat after chunk unload, logout, dimensions and restart. Check UUID
   persistence/reward uniqueness. Existing ownership and unrelated protections stay.
8. Compare a COPY of an old 1.6 world: profile identities, quest progress and all
   500 lessons. Never test migration on the only copy of a valuable save.

Reproduction: requires the original supplied JAR with enforced base SHA and the
previous input packs plus the five latest archives. Extract latest inputs into:
Mob_58_Hanan_Complete, Mob_65_Glass_Ronin_Complete, Decor_Pack_05,
Decor_Pack_03_Variant, Decor_Pack_10. Preserve internal paths. The old Devourer
pack remains in its original folder; DO NOT overwrite it with the new archive.
Run bash integration/build.sh ORIGINAL.jar INPUTS BUILD_DIR. For preservation
checks put the 1.6.0 JAR in BUILD_DIR/dist. Dependency downloads are pinned.
Record exact Forge/JVM version, logs, reproduction coordinates and item IDs for
any failure. Distinguish static fixture tests from actual game results.
