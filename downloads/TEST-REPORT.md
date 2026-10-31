# Soulbound Army 1.3.0 reforged — verification report

Status: experimental candidate. Local checks passed. Minecraft/Forge startup
and gameplay remain untested; zero bugs or full compatibility are not certified.

## Latest requested creatures

- Amin El Nemr: integrated once with model, texture/glow, ten authored clips,
  three native combat moves, egg/bestiary/army support and his Crimson Shadow
  workshop in Echo Realms. Its nine chest block entities contain 42 stacks;
  newer lowercase count tags were converted to 1.20.1 Count Byte tags.
- Legendary Giant Zombie Refai: retained once with the model, ten clips, four
  combat moves and Refai Cursed Stronghold. The latest upload is byte-identical
  to the pack already integrated. This is distinct from the original Refai.
- Full-width/height empty-cell spawn checks pass for all ten added habitats,
  including Refai's 2.4 x 4.4-block body and Amin's 0.6 x 1.95-block body.
  Those checks found the Pangolin's old corner anchor too narrow, so it was
  moved to an open aisle. Actual collision shapes still require Minecraft.

## Continued changes from the previous request

Villager Knight's gameplay registration, assets, voices, village lodges, atlas
entry and localized text are removed. Only its known old missing registry IDs
are ignored; raw army/journal migration removes retired records selectively.
The other 37 original profiles remain identical, with stable entity names.
Shared effects for other creatures are preserved as CrystalCombat. Existing
world buildings are not destructively erased.

The independent legendary encounter helper permits ordinary escape and later
encounters/summons without a fixed mythic adventure party. Boss-site block-edit
vetoes are removed while unrelated plot/shrine protection stays. Guardian UUIDs,
health and matching-owner one-reward validation are preserved. Tick lookups skip
profiles with no active UUID. Client/server protocol is 32-reforged.

All 53 spawn eggs have creature-specific generated art (52 unique atlas cells
and Amin's separate transparent texture), without vanilla tinting. The importer
copies the generated PNGs unchanged. Ocean density modification is now limited
to a verified Sunken Kingdom climate band, preserving other humid inland terrain.
Previous biomes/decor remain included: two added biomes, 60 blocks/items,
20 placed decor props and 20 structure/tree sets.

## Executed validation

- End-to-end build from the exact supplied base and eleven unique source packs.
- 17 resource/integration tests passed.
- 30 encounter assertions on the production helper using explicit world/API
  fixtures; 10 retirement/migration assertions using explicit API/NBT fixtures.
- Actual profile initializer: 47 matching IDs/profiles, all 37 surviving original
  profile definitions unchanged; actual animation reader loaded 29 new moves.
- Nine generated/relocated expansion classes, 38 methods: bytecode stack/local
  verification passed; all mod-owned class references resolve after removal.
- 102 helper member references match the actual mod's public signatures.
- Aquatic routing, exclusive-session routing and block-edit routing verified.
- No Minecraft/Forge compile-time fixture classes were packaged in the JAR.
- Standard split ZIP directory offsets, CRC and complete reassembled JAR SHA-256
  verified. The delivered volumes were not modified by verification.

## Runtime limitation

The official Forge installer download returns HTTP 403 under the environment's
network policy; there is no installed Forge runtime. NOT RUN: client/server
startup, Minecraft/Forge method linkage, registry/worldgen codecs, visual UVs,
real combat/pathfinding, old-world migration or multiplayer. Compile fixtures
are not the engine. The build targets Java 17 bytecode using -source/-target
on JDK 21 because this trimmed JDK lacks ct.sym; Java 17 runtime linkage also
remains to be checked. Use a disposable world or backup for candidate testing.

See integration/README.md for reproduction and the exact remaining game checks.

## Input integrity

Amin upload SHA-256:
d783507d123a3650ab094675f6099e4042de25b144fa38c6226e5a4fc9c9f793
Refai repeated upload SHA-256 (identical to existing pack):
afdb067c9f9192878b33d2ba6eff1695026083dd47f8103739013c7a7f407fe2

## Artifact

File: soulbound-army-1.20.1-1.3.0-reforged-preview.jar
Bytes: 32375483
SHA-256: 8c2bffeb9962443a4f10e8b1421950c47118378e2a5d4ea420da1c247920ffaf

## Captured build output

```text
Prepared 10 profiles, 60 blocks, 20 structure sets
Prepared knight removal, compacted profile IDs and 53 unique egg designs.
warning: [options] system modules path not set in conjunction with -source 17
1 warning
PASS: 30 open-encounter behavioral assertions against explicit ABI/world fixtures; Forge runtime is not exercised.
PASS: 10 retirement/missing-mapping assertions against explicit API/NBT fixtures.
Wrote /workspace/mod-integration/dist/soulbound-army-1.20.1-1.3.0-reforged-preview.jar (3249 entries); patched method stack verification passed.
PASS: all 9 generated expansion classes / 38 methods passed bytecode stack/local checks (not game API linkage).
PASS: every mod-owned class reference resolves after retirement/relocation.
PASS: 102 helper references match public members of the actual patched mod (Minecraft/Forge members remain unverified).
PASS: aquatic IDs 41/44 route correctly; boss-site edit vetoes removed while foreign-plot and quest-shrine checks remain.
PASS: 47 matching IDs/profiles; all 37 surviving original profiles unchanged; 29 added combat clips loaded by the real animation reader.
test_all_eggs_have_unique_authored_art (__main__.IntegrationTests.test_all_eggs_have_unique_authored_art) ... ok
test_amin_loot_survives_120_conversion (__main__.IntegrationTests.test_amin_loot_survives_120_conversion) ... ok
test_block_model_and_texture_references (__main__.IntegrationTests.test_block_model_and_texture_references) ... ok
test_catalogue_alignment_and_surviving_ids (__main__.IntegrationTests.test_catalogue_alignment_and_surviving_ids) ... ok
test_decorations_registered_and_placed (__main__.IntegrationTests.test_decorations_registered_and_placed) ... ok
test_dimension_biomes_and_features_resolve (__main__.IntegrationTests.test_dimension_biomes_and_features_resolve) ... ok
test_explicit_transparent_block_render_layers (__main__.IntegrationTests.test_explicit_transparent_block_render_layers) ... ok
test_knight_content_removed_and_other_temples_preserved (__main__.IntegrationTests.test_knight_content_removed_and_other_temples_preserved) ... ok
test_localization_and_registry_assets (__main__.IntegrationTests.test_localization_and_registry_assets) ... ok
test_model_hierarchies_and_animation_tracks (__main__.IntegrationTests.test_model_hierarchies_and_animation_tracks) ... ok
test_no_newer_or_invented_vanilla_block_ids (__main__.IntegrationTests.test_no_newer_or_invented_vanilla_block_ids) ... ok
test_ocean_density_is_limited_to_sunken_climate (__main__.IntegrationTests.test_ocean_density_is_limited_to_sunken_climate) ... ok
test_preserved_original_classes_and_protocol_change (__main__.IntegrationTests.test_preserved_original_classes_and_protocol_change) ... ok
test_resources_were_packaged_without_loss (__main__.IntegrationTests.test_resources_were_packaged_without_loss) ... ok
test_structure_worldgen_links_and_spawn_clearance (__main__.IntegrationTests.test_structure_worldgen_links_and_spawn_clearance) ... ok
test_templates_palettes_loot_and_bounds (__main__.IntegrationTests.test_templates_palettes_loot_and_bounds) ... ok
test_textures_and_glow_masks (__main__.IntegrationTests.test_textures_and_glow_masks) ... ok

----------------------------------------------------------------------
Ran 17 tests in 18.115s

OK
8c2bffeb9962443a4f10e8b1421950c47118378e2a5d4ea420da1c247920ffaf  /workspace/mod-integration/dist/soulbound-army-1.20.1-1.3.0-reforged-preview.jar
Resource output: /workspace/mod-integration/resources.Yf2I2s
```

## Captured archive verification

```text
  adding: soulbound-army-1.20.1-1.3.0-reforged-preview.jar (deflated 2%)
PASS: 4 standard split ZIP volumes, CRC and reassembled JAR SHA-256 verified.
JAR SHA-256: 8c2bffeb9962443a4f10e8b1421950c47118378e2a5d4ea420da1c247920ffaf
Soulbound-Army-1.3.0.z01: 10485760 bytes
Soulbound-Army-1.3.0.z02: 10485760 bytes
Soulbound-Army-1.3.0.z03: 10485760 bytes
Soulbound-Army-1.3.0.zip: 138631 bytes
```
