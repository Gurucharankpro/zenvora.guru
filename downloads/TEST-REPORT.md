# 1.8.0 Shadow Awakening — verification report

EXPERIMENTAL CANDIDATE; no actual Forge run. Built on the exact published 1.7.0
source commit 965d1a0be2e5abee2def701b2789015395a2577a and compared with its
c4fd6a7528b5bbf6fe18d060465b1b9ce0e98aaa917c0f0e82d8cfd4623a871a JAR.
The reproducible pipeline replays all existing patches against the original
hash-pinned JAR; it does not drop content or start from an earlier release state.
All 51 prior profiles/order, artwork, structures, 500 lessons, journey goals and
four creative classifications remain. Existing sound declarations are preserved.

## What is implemented

The supplied pack is visual artwork, not skeletal creature animations. Selected
original individual PNG frames are copied unchanged: 444 frames, 31 controllers,
15 effect families. Vanilla ItemDisplay entities present ground/vertical quads,
with per-instance frame clocks through CustomModelData, not a global animated
texture cycle. Four original OGG clips are merged into existing sound resources.
The one internal x1_visual item is deliberately hidden from all creative tabs.

Hook mapping (actual original methods inspected, exact bytecode sites checked):

| Effect | Trigger | Placement / qualification |
| --- | --- | --- |
| 01 Extraction | CompanionService.acquired commits soulAcquired=true | Owning player; no repeat on already-acquired record |
| 02 Standard summon | ShadowService.summonAt returns true | Actual successfully spawned body |
| 03 Legendary summon | Same, SoulTier LEGENDARY or above | Actual body; mythic bodies use 14 instead |
| 04 Recall | Recall's native particle/removal path | Body position before removal |
| 05 Promotion | CompanionService.bond, rank increases | Active body or owner; bond rank, not invented soul tiers |
| 06 Rally | Accepted FOLLOW order | Each affected ally; ground/marker only |
| 07 Confirmation | Other accepted orders | Active body or owner |
| 08 Elite aura | EPIC/LEGENDARY summon | Finite one-cycle burst, not persistent aura |
| 09 General-style aura | CRAZY/CHOSEN summon | Finite high-tier burst; no new General rank |
| 10 Teleport arrival | ShadowGoal after actual catch-up teleport | Destination body position |
| 11 Weapon trail | Valid owned mythic melee strike; ordinary positive melee damage | Clip-based slash/thrust/overhead variants; one variant at a time |
| 12 Impact | Lowest-priority uncanceled positive LivingDamageEvent, shadow attacker | Victim position after armor calculation |
| 13 Barrier | Existing effect-5 owned mythic guard strike | Sprite fallback at body; no new defensive ability |
| 14 Boss awakening | Mythic/Devourer army summon; natural mythic guardian spawn/resume | Spawned body; both native resume/fresh branches |
| 15 Disappearance | Valid owned shadow death | Body position; no graphic content |

The server tick advances displays in every loaded dimension. A maximum of 24
visual displays per dimension and <=100 ticks per layer limit accumulation.
Unloaded displays resume their finite timer when loaded again. Cleanup resets
the display score and kills ONLY tagged ItemDisplays. Normal actors, inventories,
health, targeting, combat powers and ownership are not modified by the functions.
Original particles remain. Gameplay success conditions are retained. Commands
use static internal functions and trusted dimension/finite coordinates, not user
text. Cosmetic exceptions are isolated; a linkage failure disables further visual
commands and logs once. The fixture-failure warning in captured tests is an
intentional injected failure test, not evidence of an actual Forge error.

## Scope / limitations

Presentation uses full-bright alpha item quads, not additive/no-depth-write custom
meshes. UI badges/widgets, Blockbench dome/ring meshes and continuous aura loops
remain unbound. Directional rally wave and barrier tile mesh are not in the
default presentation; source weapon variants are selected rather than stacked.
There is no LOW/MEDIUM/HIGH configuration screen or new gameplay ability.
Visuals are temporary world-position effects, not persistent actor-following
attachments. This does not replace creature body animation controllers.

NOT RUN: Forge startup, actual command/function/ItemDisplay codec execution,
server API linkage, shader blending/UVs/brightness, audio listening, multiplayer,
performance/memory profiling, unload/restart behavior and save migration.
The enforced environment policy excludes official Forge artifact hosts; earlier
installer downloads returned HTTP 403. Explicit test fixtures are not the engine.
JDK21 tooling targets Java17 via -source/-target; missing ct.sym prevents --release
API validation. Install matching 1.8.0 on server AND clients (protocol
37-shadow-awakening), and test in a disposable world or backup. No zero-bug claim.

## Executed validation

28 resource/integration groups passed, including model/texture references,
independent frame clocks, finite timers, strict SNBT parsing of display summons,
cleanup guards, sound merging, all previous assets/profiles, existing quests,
creative groups and habitat safety. 42 encounter + 10 migration + 22 X1 routing
assertions passed against explicit command/world fixtures (74 total).
11 expansion classes / 53 methods passed stack/local checks; injected native
methods also passed BasicVerifier. Exact event-hook counts were enforced, and all
mod-owned class references resolve. The additional ABI check covers both encounter
and X1 helpers: 115 references match actual public mod signatures (Minecraft/Forge
members remain unverified). 484 source frame PNG headers match declared canvases.
The standard split ZIP CRC/directory and complete extracted JAR SHA passed.

Artifact: soulbound-army-1.20.1-1.8.0-shadow-awakening-preview.jar
Bytes: 41857199
SHA256: 9266750cd68571a6c013ffe05e8fd06355b8aeb1b825b16735ff17612ccc137a

## Captured build/test output

```text
Prepared 14 profiles, 60 blocks, 24 structure sets
Imported 40 native decorative props with two supported, spaced themed ruins.
Added 10 regional decorated waystations, all 60 designs, 15 one-time tutorial quests and supply rewards.
Added exactly 500 additive, localized, one-time learning advancement quests (300 block, 60 landmark, 40 readiness, 100 crafting).
Imported 60 distinct new decor designs with 7 themed sites and four creative group labels.
Prepared X1: 15 effect families, 31 layers, 444 independently clocked frame models.
Prepared knight removal, compacted profile IDs and 57 unique egg designs.
warning: [options] system modules path not set in conjunction with -source 17
1 warning
PASS: 42 open-encounter behavioral assertions against explicit ABI/world fixtures; Forge runtime is not exercised.
PASS: 10 retirement/missing-mapping assertions against explicit API/NBT fixtures.
Soulbound X1 visual command unavailable: java.lang.IllegalStateException: fixture failure
PASS: 22 X1 visual-routing assertions using explicit command/world fixtures; no Forge execution.
PASS: exact X1 event-hook sites verified: {attack=1, bond=1, command=1, disappearance=1, extraction=1, guardian=2, recall=1, summon=1, teleport=1, tick=1}
Wrote /workspace/mod-integration/dist/soulbound-army-1.20.1-1.8.0-shadow-awakening-preview.jar (5440 entries); patched method stack verification passed.
PASS: all 11 generated expansion classes / 53 methods passed bytecode stack/local checks (not game API linkage).
PASS: every mod-owned class reference resolves after retirement/relocation.
PASS: 102 helper references match public members of the actual patched mod (Minecraft/Forge members remain unverified).
PASS: 120 decor classifications plus egg/workstation/other cases; actual creative wrapper routed correctly.
PASS: aquatic IDs 41/44 route correctly; boss-site edit vetoes removed while foreign-plot and quest-shrine checks remain.
PASS: 51 matching IDs/profiles; all 37 surviving original profiles unchanged; 47 added combat clips loaded by the real animation reader.
PASS: 160 added block factories, including 120 explicitly noncolliding/nonoccluding decor props.
PASS: all 51 profiles from the previous 1.7.0 candidate retain their exact definitions/order.
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
test_x1_display_nbt_and_safe_cleanup (__main__.IntegrationTests.test_x1_display_nbt_and_safe_cleanup) ... ok
test_x1_frame_assets_and_independent_clocks (__main__.IntegrationTests.test_x1_frame_assets_and_independent_clocks) ... ok
test_x1_sound_merge_preserves_prior_audio (__main__.IntegrationTests.test_x1_sound_merge_preserves_prior_audio) ... ok

----------------------------------------------------------------------
Ran 28 tests in 46.348s

OK
9266750cd68571a6c013ffe05e8fd06355b8aeb1b825b16735ff17612ccc137a  /workspace/mod-integration/dist/soulbound-army-1.20.1-1.8.0-shadow-awakening-preview.jar
Resource output: /workspace/mod-integration/resources.toel3D
```

## Additional helper ABI check

```text
PASS: all 11 generated expansion classes / 53 methods passed bytecode stack/local checks (not game API linkage).
PASS: every mod-owned class reference resolves after retirement/relocation.
PASS: 115 helper references match public members of the actual patched mod (Minecraft/Forge members remain unverified).
PASS: 120 decor classifications plus egg/workstation/other cases; actual creative wrapper routed correctly.
PASS: aquatic IDs 41/44 route correctly; boss-site edit vetoes removed while foreign-plot and quest-shrine checks remain.
PASS: 51 matching IDs/profiles; all 37 surviving original profiles unchanged; 47 added combat clips loaded by the real animation reader.
PASS: 160 added block factories, including 120 explicitly noncolliding/nonoccluding decor props.
PASS: all 51 profiles from the previous 1.7.0 candidate retain their exact definitions/order.
```

## Archive verification

```text
  adding: soulbound-army-1.20.1-1.8.0-shadow-awakening-preview.jar (deflated 3%)
PASS: 4 standard split ZIP volumes, CRC and reassembled JAR SHA-256 verified.
JAR SHA-256: 9266750cd68571a6c013ffe05e8fd06355b8aeb1b825b16735ff17612ccc137a
Soulbound-Army-1.8.0.z01: 10485760 bytes
Soulbound-Army-1.8.0.z02: 10485760 bytes
Soulbound-Army-1.8.0.z03: 10485760 bytes
Soulbound-Army-1.8.0.zip: 8996631 bytes
```
