# Soulbound Army 1.4.0 royal relics — verification report

Status: EXPERIMENTAL CANDIDATE. Local checks passed; there was no Minecraft/Forge
startup or gameplay test. These results do not certify zero bugs.

## Requested additions

- King of Corny Memes: exact supplied 61-bone/117-cube model, 128x128 texture and
  real glow mask, ten authored clips, five native combat moves, egg/bestiary/army
  integration and Palace of Eternal Cringe in Shattered Sky. Guardian index 47,
  dimensions 2.0 x 4.2 blocks, matching habitat spawn [27,6,33]. Its generated egg
  art is separate; all older atlas assignments remain identical.
- Palace: converted DataVersion 3955 -> 3465 for Minecraft 1.20.1; 55 x 38 x 55
  blocks, five retained end_city_treasure chests. Template entities are cleared
  by the existing importer, including three source paintings. The geometry,
  passages, arena and throne remain. Moves reuse existing combat effects rather
  than introducing an untested custom cheese-projectile entity or AI system.
- Hollow Reaper decor: all 20 full models/textures/glow masks, explicit native
  facing states, lighting, render layers, loot, items and Arabic/English labels.
  Its new reliquary ruin generates in Eternal Abyss.
- Soul Devourer decor: all 20 designs with the same native integration. Its new
  relic sanctum generates in Gloomwood. Each ruin provides floor/ceiling/wall
  support and checked visual clearance for its corresponding 20 props.

Both decor packs are static visual props, one native anchor block per item, with
noncollision/nonocclusion properties and 1.5 hardness. The complete oversized
models are legal Java models and retain their authored scale; no physical
multiblock behavior, chairs, storage or additional Hollow Reaper mob is claimed.
The source's 1.21 item files and half-state variants are not imported into these
single-anchor blocks. Supplied emissive masks remain available; light emission
works without a separate fullbright block overlay renderer.

## Compatibility fixes and preserved behavior

- Added the alternate _Glow_Mask.png naming convention; hash verification proves
  King's real uploaded glow mask was included rather than an empty fallback.
- Preserved FX-bone hiding scales across idle/walk/run, and all five attack clips.
- Selected translucent layers for glass/mist props, cutout for other decorations.
- Existing-resource comparison caught old structure placement salts depending on
  directory iteration order. A checked-in mapping now pins all 20 old salts and
  three new unique salts, preventing new packs from shifting previous placement.
- All 47 previous release profiles retain their exact definitions and order.
  Existing artwork, item definitions, templates and worldgen resources are
  byte-identical to the 1.3.0 candidate; additive catalogues/locales/metadata and
  tags are compared separately. Existing encounter-freedom behavior remains.
- New King escape/reentry, concurrent Refai encounter, UUID reload and one-time
  capture paths pass simulated-world assertions on the production helper.
- Villager Knight remains removed with selective saved-record cleanup.

Totals: 48 profile entries (37 surviving original + 11 added), separate Soul
Devourer, 54 unique egg designs, two added biomes, 100 added blocks/items (40 biome
blocks + 60 decor props) and 23 structure/tree sets. All 60 decor designs have
verified template placements. Both client/server need protocol 33-royal-relics.

## Executed checks

- End-to-end build against the exact supplied original JAR and fourteen unique
  source archives; all three latest archive CRC checks passed.
- 22 Python resource/integration test groups passed, including source/data
  references, palette compatibility, chest stacks, full-body habitat clearance,
  decor support/clearance, new model bounds/rotations/UVs, FX hiding, retirement,
  prior-release resource preservation and unchanged Sunken Kingdom climate rules.
- 36 encounter assertions on the production helper using explicit world/API
  fixtures, including six new King checks; 10 retirement/mapping assertions.
- Actual profile initializer and animation reader: 48 matching IDs/profiles,
  37 unchanged surviving original profiles, 47 unchanged previous-release
  profiles and 34 added combat clips.
- Nine generated/relocated classes, 38 methods: stack/local verification passed.
- All mod-owned class references resolve; 102 helper references match actual
  public mod signatures. Minecraft/Forge member linkage is not established.
- 100 added block factories inspected; all 60 decor factories explicitly use
  noncollision/nonocclusion/strength properties. Aquatic/edit/lifecycle routing
  checks passed. No compile-time Minecraft/Forge fixtures entered the output.
- Bash syntax and source whitespace checks passed.
- Four standard split ZIP volumes verified: directory offsets, member CRC and
  SHA-256 of the complete reassembled JAR. Delivered split volumes are unchanged.

## Runtime limitation and remaining validation

The environment's enforced network policy still excludes the official Forge
artifact hosts. Previous official installer requests returned HTTP 403, and no
Forge runtime is installed. No blocked destination was bypassed.
NOT RUN: Minecraft/Forge startup, engine API/registry/worldgen-codec loading,
actual rendering, game collision shapes, combat/pathfinding, old-world migration,
restart or multiplayer. Build helpers use explicit compile/test fixtures, not the
engine. Bytecode targets Java 17 using -source/-target under the trimmed JDK 21;
its missing ct.sym prevents --release 17 linkage checks.

Test this candidate in a disposable world or backup. Existing explored chunks do
not regenerate; new buildings require new chunks. For live testing: start both
client/server, inspect logs/resources and new natural structures; summon/capture
King, flee, break/place blocks and start Refai; inspect each decor placement and
inventory icon, including translucent layers; repeat after chunk unload and
restart. See integration/README.md for reproduction and prior-content checks.

## Latest uploaded input fingerprints

Mob_70_King_Of_Corny_Memes_Complete.zip
SHA-256: 32d6c43242505df0ac102bce0dac0dc534bd97492629d3da8f357a79403b93b0

Decor_Pack_02_Hollow_Reaper.zip
SHA-256: f228ef2b7ea427e086e602322d9bfb4febddd94900393405245d9f10b7fe083a

Decor_Pack_03_Soul_Devourer.zip
SHA-256: fcbecc999b4090e349d77dd8095001787fbf738972f13400372e9a84509fc76a

## Artifact

File: soulbound-army-1.20.1-1.4.0-royal-relics-preview.jar
Bytes: 34347232
SHA-256: bcc06c8f2e9317cc480f86dafd8a0c6a78eebca6c26c3a64939fc04a7147ecae

## Captured build output

```text
Prepared 11 profiles, 60 blocks, 21 structure sets
Imported 40 native decorative props with two supported, spaced themed ruins.
Prepared knight removal, compacted profile IDs and 54 unique egg designs.
warning: [options] system modules path not set in conjunction with -source 17
1 warning
PASS: 36 open-encounter behavioral assertions against explicit ABI/world fixtures; Forge runtime is not exercised.
PASS: 10 retirement/missing-mapping assertions against explicit API/NBT fixtures.
Wrote /workspace/mod-integration/dist/soulbound-army-1.20.1-1.4.0-royal-relics-preview.jar (3506 entries); patched method stack verification passed.
PASS: all 9 generated expansion classes / 38 methods passed bytecode stack/local checks (not game API linkage).
PASS: every mod-owned class reference resolves after retirement/relocation.
PASS: 102 helper references match public members of the actual patched mod (Minecraft/Forge members remain unverified).
PASS: aquatic IDs 41/44 route correctly; boss-site edit vetoes removed while foreign-plot and quest-shrine checks remain.
PASS: 48 matching IDs/profiles; all 37 surviving original profiles unchanged; 34 added combat clips loaded by the real animation reader.
PASS: 100 added block factories, including 60 explicitly noncolliding/nonoccluding decor props.
PASS: all 47 profiles from the previous 1.3.0 candidate retain their exact definitions/order.
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
test_resources_were_packaged_without_loss (__main__.IntegrationTests.test_resources_were_packaged_without_loss) ... ok
test_structure_worldgen_links_and_spawn_clearance (__main__.IntegrationTests.test_structure_worldgen_links_and_spawn_clearance) ... ok
test_templates_palettes_loot_and_bounds (__main__.IntegrationTests.test_templates_palettes_loot_and_bounds) ... ok
test_textures_and_glow_masks (__main__.IntegrationTests.test_textures_and_glow_masks) ... ok

----------------------------------------------------------------------
Ran 22 tests in 27.052s

OK
bcc06c8f2e9317cc480f86dafd8a0c6a78eebca6c26c3a64939fc04a7147ecae  /workspace/mod-integration/dist/soulbound-army-1.20.1-1.4.0-royal-relics-preview.jar
Resource output: /workspace/mod-integration/resources.oYBIwJ
```

## Captured archive verification

```text
  adding: soulbound-army-1.20.1-1.4.0-royal-relics-preview.jar (deflated 3%)
PASS: 4 standard split ZIP volumes, CRC and reassembled JAR SHA-256 verified.
JAR SHA-256: bcc06c8f2e9317cc480f86dafd8a0c6a78eebca6c26c3a64939fc04a7147ecae
Soulbound-Army-1.4.0.z01: 10485760 bytes
Soulbound-Army-1.4.0.z02: 10485760 bytes
Soulbound-Army-1.4.0.z03: 10485760 bytes
Soulbound-Army-1.4.0.zip: 1977966 bytes
```
