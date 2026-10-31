# Living Realms 1.5.0 — experimental candidate

Adds The 69 Pose (a masked Eclipse acrobat) and Inverted Circus, without duplicating
the repeated Soul Devourer decor ZIP. Adds ten regional decorated waystations,
one per biome; their 60 placements collectively use all 60 unique decor designs.
Existing display structures remain. Adds regional material palettes, lights and
supply chests, fifteen localized native advancement quests and one-time XP/bread/
torch/emerald rewards. Sunken visit also grants 300 seconds water breathing.

All 48 prior creature profiles and prior artwork/worldgen resources are preserved.
There are now 49 profiles plus the separate Devourer, 55 eggs, 100 added blocks,
34 structure/tree sets and 15 new quests. Props remain static one-anchor items.
Native advancements supplement the old quest UI; this is a bounded exploration
and onboarding update, not a claim that every subsystem has been overhauled.

Validation: 23 resource test groups, 38 simulated-world encounter assertions,
10 migration assertions, actual catalogue/animation reader and previous-profile
comparison. Nine generated classes / 38 methods stack checks and 102 public mod
ABI references pass. Split archive CRC and complete payload SHA verified.

NOT RUN: Forge startup, advancement/worldgen codec loading, actual game rendering,
terrain placement, combat, multiplayer or save migration. Official Forge hosts
remain excluded by network policy; earlier requests returned HTTP 403. Java 17
bytecode is built via trimmed JDK21 -source/-target with explicit API fixtures;
these are not the engine and not shipped. Full runtime linkage remains untested.
Use a disposable world/backup. New structures need unexplored chunks. Matching
client/server version is required: protocol 34-living-realms.

JAR SHA256: eea38d7a3a67a49781e75bca8f4f66e379848305ece5a2e0e57b154fc13f3d8d

## Compilation and catalogue checks

```text
Prepared 12 profiles, 60 blocks, 22 structure sets
Imported 40 native decorative props with two supported, spaced themed ruins.
Added 10 regional decorated waystations, all 60 designs, 15 one-time tutorial quests and supply rewards.
Prepared knight removal, compacted profile IDs and 55 unique egg designs.
warning: [options] system modules path not set in conjunction with -source 17
1 warning
PASS: 38 open-encounter behavioral assertions against explicit ABI/world fixtures; Forge runtime is not exercised.
PASS: 10 retirement/missing-mapping assertions against explicit API/NBT fixtures.
Wrote /workspace/mod-integration/dist/soulbound-army-1.20.1-1.5.0-living-realms-preview.jar (3586 entries); patched method stack verification passed.
PASS: all 9 generated expansion classes / 38 methods passed bytecode stack/local checks (not game API linkage).
PASS: every mod-owned class reference resolves after retirement/relocation.
PASS: 102 helper references match public members of the actual patched mod (Minecraft/Forge members remain unverified).
PASS: aquatic IDs 41/44 route correctly; boss-site edit vetoes removed while foreign-plot and quest-shrine checks remain.
PASS: 49 matching IDs/profiles; all 37 surviving original profiles unchanged; 39 added combat clips loaded by the real animation reader.
PASS: 100 added block factories, including 60 explicitly noncolliding/nonoccluding decor props.
PASS: all 48 profiles from the previous 1.4.0 candidate retain their exact definitions/order.
```

## Resource checks

```text
test_all_eggs_have_unique_authored_art (__main__.IntegrationTests.test_all_eggs_have_unique_authored_art) ... ok
test_amin_loot_survives_120_conversion (__main__.IntegrationTests.test_amin_loot_survives_120_conversion) ... ok
test_block_model_and_texture_references (__main__.IntegrationTests.test_block_model_and_texture_references) ... ok
test_catalogue_alignment_and_surviving_ids (__main__.IntegrationTests.test_catalogue_alignment_and_surviving_ids) ... ok
test_decorations_registered_and_placed (__main__.IntegrationTests.test_decorations_registered_and_placed) ... ok
test_dimension_biomes_and_features_resolve (__main__.IntegrationTests.test_dimension_biomes_and_features_resolve) ... ok
test_explicit_transparent_block_render_layers (__main__.IntegrationTests.test_explicit_transparent_block_render_layers) ... ok
test_king_effect_bones_are_hidden_during_locomotion (__main__.IntegrationTests.test_king_effect_bones_are_hidden_during_locomotion) ... ok
test_king_palace_chests_and_spawn_registry (__main__.IntegrationTests.test_king_palace_chests_and_spawn_registry) ... ok
test_knight_content_removed_and_other_temples_preserved (__main__.IntegrationTests.test_knight_content_removed_and_other_temples_preserved) ... ok
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
Ran 23 tests in 36.408s

OK
```

## Delivery check

```text
  adding: soulbound-army-1.20.1-1.5.0-living-realms-preview.jar (deflated 3%)
PASS: 4 standard split ZIP volumes, CRC and reassembled JAR SHA-256 verified.
JAR SHA-256: eea38d7a3a67a49781e75bca8f4f66e379848305ece5a2e0e57b154fc13f3d8d
Soulbound-Army-1.5.0.z01: 10485760 bytes
Soulbound-Army-1.5.0.z02: 10485760 bytes
Soulbound-Army-1.5.0.z03: 10485760 bytes
Soulbound-Army-1.5.0.zip: 3695710 bytes
```
