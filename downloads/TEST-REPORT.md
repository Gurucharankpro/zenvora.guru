# 1.7.0 verification — EXPERIMENTAL, NOT FORGE-TESTED

Integrated latest five uploads, identified by LATEST_INPUTS.json. Hanan and Glass
Ronin append at profile slots 49/50. All 49 previous profiles are unchanged.
Hanan's Cairo house -> Crimson Shadow; Ronin's mirror temple -> Shattered Sky.
Templates convert 1.21 DataVersion to 1.20.1 / 3465, clear authoring markers and
entities, and retain chest loot. Full-body spawn-clearance checks pass.

60 additional decor designs: Butcher 20, Devourer II 20, biome relics 20. The
latest Devourer archive is a different edition (102 entries, decor_pack_03
namespace); it is not the earlier 123-entry soul_bound asset pack. Stable old
assets remain; distinct variants get devourer_v2_ IDs. Butcher/relic props have
separate prefixes. All have native block/item/model/state/loot registrations;
seven themed sites distribute them to the relevant biomes. Biome relics are
landmark structures, not free-scatter features or exact suggested room edits.

Four existing creative registry IDs are retained and repurposed as requested:
Decor (nature), Eggs (creatures), Workstations (workshops), Other (soulbound_army).
Explicit decor membership overrides misleading names like workbench/altar.
Native egg recognition and workshop classification remain; Eclipse station
blocks and the Monolith enchanting table are covered. Retired content stays hidden. The tests execute the actual
CreativeGroups class and verify the actual ModCreativeTab wrapper routing.

Glass Ronin's model uses a conditional translucent render-type adapter. Its zero
FX scales become 0.001 to avoid singular normal scaling. Hanan has no source glow
mask and uses a transparent fallback. New eggs use an unchanged generated paired
RGBA atlas; old egg UV assignments remain. No raster art editing was performed.

Totals: 51 profiles plus separate Devourer, 57 eggs, 160 added blocks/items,
120 decor designs, 43 structure/tree sets. All 500 learning + 15 journey goals
are preserved without silently changing IDs, requirements or old translations.

Validation: 25 Python resource test groups; 42 simulated-world encounter and
10 selective-migration assertions. Ten expansion classes / 41 methods stack
checks; all mod-owned class references and 102 helper/public mod API references
resolve. Actual catalogue and animation reader load 51 aligned profiles and 47
new attack clips. All 49 previous profile definitions/order are unchanged.
160 factories, including 120 explicit noncollision/nonocclusion decor blocks,
are inspected. The actual classifier handles all 120 decor IDs plus representative
other/egg/station cases. Compile-time engine fixtures and generated source are
not packaged. Split ZIP CRC/directory/payload SHA validation passed.

Limitations: no Minecraft/Forge startup, actual registry lifecycle, render/UV
inspection, creative UI, alpha behavior, pathfinding, multiplayer or save migration
test. The enforced network policy excludes Forge artifact hosts; previous
installer attempts returned 403. Explicit API fixtures are not the engine.
JDK21 tooling emits Java17 bytecode with -source/-target; missing ct.sym prevents
--release linkage checks. Props are static one-anchor objects, not functional
chairs/workbenches/storage or multiblock physics. No guarantee of zero bugs.
Use a disposable world or backup. Both sides require protocol 36-curated-content.

Artifact: soulbound-army-1.20.1-1.7.0-curated-content-preview.jar
Bytes: 39353995
SHA256: c4fd6a7528b5bbf6fe18d060465b1b9ce0e98aaa917c0f0e82d8cfd4623a871a

## Captured checks

```text
Prepared 14 profiles, 60 blocks, 24 structure sets
Imported 40 native decorative props with two supported, spaced themed ruins.
Added 10 regional decorated waystations, all 60 designs, 15 one-time tutorial quests and supply rewards.
Added exactly 500 additive, localized, one-time learning advancement quests (300 block, 60 landmark, 40 readiness, 100 crafting).
Imported 60 distinct new decor designs with 7 themed sites and four creative group labels.
Prepared knight removal, compacted profile IDs and 57 unique egg designs.
warning: [options] system modules path not set in conjunction with -source 17
1 warning
PASS: 42 open-encounter behavioral assertions against explicit ABI/world fixtures; Forge runtime is not exercised.
PASS: 10 retirement/missing-mapping assertions against explicit API/NBT fixtures.
Wrote /workspace/mod-integration/dist/soulbound-army-1.20.1-1.7.0-curated-content-preview.jar (4492 entries); patched method stack verification passed.
PASS: all 10 generated expansion classes / 41 methods passed bytecode stack/local checks (not game API linkage).
PASS: every mod-owned class reference resolves after retirement/relocation.
PASS: 102 helper references match public members of the actual patched mod (Minecraft/Forge members remain unverified).
PASS: 120 decor classifications plus egg/workstation/other cases; actual creative wrapper routed correctly.
PASS: aquatic IDs 41/44 route correctly; boss-site edit vetoes removed while foreign-plot and quest-shrine checks remain.
PASS: 51 matching IDs/profiles; all 37 surviving original profiles unchanged; 47 added combat clips loaded by the real animation reader.
PASS: 160 added block factories, including 120 explicitly noncolliding/nonoccluding decor props.
PASS: all 49 profiles from the previous 1.6.0 candidate retain their exact definitions/order.
test_all_eggs_have_unique_authored_art (__main__.IntegrationTests.test_all_eggs_have_unique_authored_art) ... ok
test_amin_loot_survives_120_conversion (__main__.IntegrationTests.test_amin_loot_survives_120_conversion) ... ok
test_block_model_and_texture_references (__main__.IntegrationTests.test_block_model_and_texture_references) ... ok
test_catalogue_alignment_and_surviving_ids (__main__.IntegrationTests.test_catalogue_alignment_and_surviving_ids) ... ok
test_decorations_registered_and_placed (__main__.IntegrationTests.test_decorations_registered_and_placed) ... ok
test_dimension_biomes_and_features_resolve (__main__.IntegrationTests.test_dimension_biomes_and_features_resolve) ... ok
test_exactly_500_additive_learning_tasks (__main__.IntegrationTests.test_exactly_500_additive_learning_tasks) ... ok
test_explicit_transparent_block_render_layers (__main__.IntegrationTests.test_explicit_transparent_block_render_layers) ... ok
test_king_effect_bones_are_hidden_during_locomotion (__main__.IntegrationTests.test_king_effect_bones_are_hidden_during_locomotion) ... ok
test_king_palace_chests_and_spawn_registry (__main__.IntegrationTests.test_king_palace_chests_and_spawn_registry) ... ok
test_knight_content_removed_and_other_temples_preserved (__main__.IntegrationTests.test_knight_content_removed_and_other_temples_preserved) ... ok
test_latest_pack_integrity_and_tabs (__main__.IntegrationTests.test_latest_pack_integrity_and_tabs) ... ok
test_localization_and_registry_assets (__main__.IntegrationTests.test_localization_and_registry_assets) ... ok
test_model_hierarchies_and_animation_tracks (__main__.IntegrationTests.test_model_hierarchies_and_animation_tracks) ... ok
test_new_decor_models_use_native_anchor_states (__main__.IntegrationTests.test_new_decor_models_use_native_anchor_states) ... ok
test_new_decor_ruins_have_support_and_visual_clearance (__main__.IntegrationTests.test_new_decor_ruins_have_support_and_visual_clearance) ... ok
test_no_newer_or_invented_vanilla_block_ids (__main__.IntegrationTests.test_no_newer_or_invented_vanilla_block_ids) ... ok
test_ocean_density_is_limited_to_sunken_climate (__main__.IntegrationTests.test_ocean_density_is_limited_to_sunken_climate) ... ok
test_preserved_original_classes_and_protocol_change (__main__.IntegrationTests.test_preserved_original_classes_and_protocol_change) ... ok
test_previous_release_content_preserved (__main__.IntegrationTests.test_previous_release_content_preserved) ... ok
test_regional_waystations_and_tutorial_rewards (__main__.IntegrationTests.test_regional_waystations_and_tutorial_rewards) ... ok
test_resources_were_packaged_without_loss (__main__.IntegrationTests.test_resources_were_packaged_without_loss) ... ok
test_structure_worldgen_links_and_spawn_clearance (__main__.IntegrationTests.test_structure_worldgen_links_and_spawn_clearance) ... ok
test_templates_palettes_loot_and_bounds (__main__.IntegrationTests.test_templates_palettes_loot_and_bounds) ... ok
test_textures_and_glow_masks (__main__.IntegrationTests.test_textures_and_glow_masks) ... ok

----------------------------------------------------------------------
Ran 25 tests in 43.119s

OK
c4fd6a7528b5bbf6fe18d060465b1b9ce0e98aaa917c0f0e82d8cfd4623a871a  /workspace/mod-integration/dist/soulbound-army-1.20.1-1.7.0-curated-content-preview.jar
Resource output: /workspace/mod-integration/resources.Kisq9Z
```

## Archive checks

```text
  adding: soulbound-army-1.20.1-1.7.0-curated-content-preview.jar (deflated 3%)
PASS: 4 standard split ZIP volumes, CRC and reassembled JAR SHA-256 verified.
JAR SHA-256: c4fd6a7528b5bbf6fe18d060465b1b9ce0e98aaa917c0f0e82d8cfd4623a871a
Soulbound-Army-1.7.0.z01: 10485760 bytes
Soulbound-Army-1.7.0.z02: 10485760 bytes
Soulbound-Army-1.7.0.z03: 10485760 bytes
Soulbound-Army-1.7.0.zip: 6736181 bytes
```
