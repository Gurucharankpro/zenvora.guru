# Soulbound Army 1.6.0 learning paths — experimental candidate

Adds exactly 500 advancement lessons while preserving all prior goals/resources.
300 block lessons (discover/place/stock), 60 biome landmark tasks, 40 regional
readiness goals and 100 recipe/equipment lessons. All have localized explanation,
one-time XP, no toast/chat flood; ten stage milestones also award supply loot.
Rare decor reserves require two items. Inventory tasks need an inventory change;
recipe tasks observe recipe-book unlocking. Parent links order the display, not
completion eligibility. New goals appear in vanilla advancements, not the native
story planner. No reward revoke/schedule loop is used.

The native story planner catalogue has 60 goals. An existing combined 500 across
other systems was not verified. The previous JAR had 40 vanilla advancements;
this one has 540, including 500 under the separate learning namespace. No claim
of exactly 1000 total quests, or 500 scripted story scenarios, is made.

Also retains the ten regional waystations using all 60 decor designs, fifteen
journey quests and rewards, Eclipse acrobat/circus, legendary encounter freedom,
retired Knight cleanup and all prior additions. Total 49 profiles plus separate
Devourer, 55 egg designs, 100 added blocks and 34 structure/tree sets.

Passed: 24 resource test groups; 38 simulated encounter + 10 migration assertions;
actual catalogue and animation reader; all 49 previous-release profiles retained;
all prior resource assets retained (additive locale/metadata changes excluded);
500 unique IDs, translation keys, parent and milestone-loot references verified.
Generated class stacks and actual mod member links pass; fixture classes excluded.
Split archive CRC/directory offsets and complete extracted JAR hash verified.

NOT RUN: actual Forge startup, advancement/recipe/structure codecs, 500-goal UI
performance, game rendering, combat, multiplayer, save migration and engine API
linkage. Official Forge destinations remain excluded under the environment's
network policy; previous requests returned 403. Fixtures are not the game engine.
Use a disposable world/backup. Client/server both require protocol 35-learning-paths.
This is a bounded learning/exploration update, not a full gameplay/visual rewrite.

Artifact: soulbound-army-1.20.1-1.6.0-learning-paths-preview.jar
SHA256: ee6846be17e0c6dc0cb7086e7d46c896d511f5f22e0792e53c90c22fd9b20e06

## Build and validation

```text
Prepared 12 profiles, 60 blocks, 22 structure sets
Imported 40 native decorative props with two supported, spaced themed ruins.
Added 10 regional decorated waystations, all 60 designs, 15 one-time tutorial quests and supply rewards.
Added exactly 500 additive, localized, one-time learning advancement quests (300 block, 60 landmark, 40 readiness, 100 crafting).
Prepared knight removal, compacted profile IDs and 55 unique egg designs.
warning: [options] system modules path not set in conjunction with -source 17
1 warning
PASS: 38 open-encounter behavioral assertions against explicit ABI/world fixtures; Forge runtime is not exercised.
PASS: 10 retirement/missing-mapping assertions against explicit API/NBT fixtures.
Wrote /workspace/mod-integration/dist/soulbound-army-1.20.1-1.6.0-learning-paths-preview.jar (4086 entries); patched method stack verification passed.
PASS: all 9 generated expansion classes / 38 methods passed bytecode stack/local checks (not game API linkage).
PASS: every mod-owned class reference resolves after retirement/relocation.
PASS: 102 helper references match public members of the actual patched mod (Minecraft/Forge members remain unverified).
PASS: aquatic IDs 41/44 route correctly; boss-site edit vetoes removed while foreign-plot and quest-shrine checks remain.
PASS: 49 matching IDs/profiles; all 37 surviving original profiles unchanged; 39 added combat clips loaded by the real animation reader.
PASS: 100 added block factories, including 60 explicitly noncolliding/nonoccluding decor props.
PASS: all 49 profiles from the previous 1.5.0 candidate retain their exact definitions/order.
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
Ran 24 tests in 35.118s

OK
ee6846be17e0c6dc0cb7086e7d46c896d511f5f22e0792e53c90c22fd9b20e06  /workspace/mod-integration/dist/soulbound-army-1.20.1-1.6.0-learning-paths-preview.jar
Resource output: /workspace/mod-integration/resources.tCzVSD
```

## Archive validation

```text
  adding: soulbound-army-1.20.1-1.6.0-learning-paths-preview.jar (deflated 3%)
PASS: 4 standard split ZIP volumes, CRC and reassembled JAR SHA-256 verified.
JAR SHA-256: ee6846be17e0c6dc0cb7086e7d46c896d511f5f22e0792e53c90c22fd9b20e06
Soulbound-Army-1.6.0.z01: 10485760 bytes
Soulbound-Army-1.6.0.z02: 10485760 bytes
Soulbound-Army-1.6.0.z03: 10485760 bytes
Soulbound-Army-1.6.0.zip: 3862957 bytes
```
