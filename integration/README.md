# Soulbound Army 1.4.0 — royal relics preview

Experimental binary integration for the supplied Minecraft 1.20.1 / Forge
47.3+ mod. The original development project was not supplied. This candidate
passes local checks, but has not been started inside Minecraft/Forge.
Base SHA-256: 0bb69723f4e2f7327f02f8aa296a0dc64629b750423bccb90980c452d561af42.

## Content

King of Corny Memes is added at internal index 47, with the supplied 61-bone /
117-cube model, real glow mask, ten animation clips and five native combat moves.
His Palace of Eternal Cringe generates in Shattered Sky: 55 x 38 x 55 blocks,
a 2.0 x 4.2-block guardian spawn at [27,6,33], and five retained treasure chests.
Animations keep the FX-bone scale tracks that hide beams/meteors during ordinary
locomotion. Moves reuse existing sonic/projectile/guard/beam/area effects;
this does not implement a separate cheese-projectile entity or a new AI engine.
Template entities are cleared by the importer, including three source paintings.

Two new decor packs contribute 20 designs each, with their original full Java
models, textures/glow masks, native block items, directional states, loot,
lighting and English/Arabic names. Hollow Reaper's reliquary generates in Eternal
Abyss; the Devourer relic sanctum generates in Gloomwood. Both newly authored
ruins place all 20 corresponding props with floor/ceiling/wall support and room
for their visual extents. They are native static props with one anchor block
per item, including oversized models. No multiblock physics, functional chairs,
containers or new Hollow Reaper creature is claimed. Do not import the source
pack's half-state or 1.21 item files into this one-anchor implementation.

Previous Amin, Refai and all earlier creature additions remain included. All 47
profiles and existing resource assets from the 1.3.0 candidate retain their order
and definitions. A comparison test found structure salts depended on directory
iteration order; structure_salts.json now pins all 20 old values and three new
unique values, so adding/extracting packs cannot shift old structure placement.

Villager Knight is retired: its entity/egg registration, profile, models,
textures, voices, village lodges, atlas entry and localized gameplay text are
removed. There are 48 profiles: 37 surviving original definitions plus eleven
additions. Soul Devourer remains separate from this catalogue.

| Internal index | Added entity | Habitat in Echo Realms |
| --- | --- | --- |
| 37 | Mokhtar El Desouky | Old Desouky Family House / Crimson Shadow |
| 38 | Broken Star Owlet | Abandoned Star Library / Shattered Sky |
| 39 | Essam El Nemr | El Nemr Mansion / Crimson Shadow |
| 40 | Lumen Cub | Mushroom Sovereign Sanctuary / Eternal Abyss |
| 41 | Pearl Drakeling | Pearl Sanctuary / Sunken Kingdom |
| 42 | Shattered Pangolin | Shattered Porcelain Temple / Shattered Sky |
| 43 | Cursed Bell Ringer | Cursed Bell Tower / Ashen Monolith |
| 44 | Weeping Axolotl | Drowned Glass Palace / Sunken Kingdom |
| 45 | Legendary Giant Zombie Refai | Refai Cursed Stronghold / Crimson Shadow |
| 46 | Amin El Nemr | Amin El Nemr Workshop / Crimson Shadow |
| 47 | King of Corny Memes | Palace of Eternal Cringe / Shattered Sky |

Surviving original entity registry names and profile definitions are unchanged.
Internal indices after the removed slot are compacted. Species are reconstructed
from their stable entity registry names, not saved numeric profile indices.
Only known retired missing registry mappings are ignored. Load-time army/journal
cleanup removes that retired species while preserving other creature progress.
Already generated lodge blocks are not destructively erased from existing worlds.
Shared combat effects used by the snow leopard, Last Lullaby and original Refai
are retained under the neutral CrystalCombat helper.

Total added content: two biomes, 100 blocks/items (40 biome blocks and 60 decor
props), and 23 structure/tree sets. All decor props have verified natural-world
template placements and native noncollision/nonocclusion properties with 1.5
hardness. Light levels and translucent/cutout layers are explicit. Supplied
emissive PNGs remain available; vanilla block light works without shader support.
A separate fullbright block overlay renderer is not implemented.

All 54 spawn eggs use generated creature-specific art, without vanilla tinting.
Fifty-two retain their exact previous atlas UV cells; Amin and King each have a
separate generated RGBA texture. The PNGs are copied unchanged. See
art/PROVENANCE.md. New eggs never shift the existing atlas assignments.

All ten authored animations per added creature are converted for the native
renderer. Supported numeric curves are sampled at 40 Hz; existing combat effects
power the new moves. Some noncombat clips have no separate UI trigger.

Compatibility corrections include 1.20.1 block-ID alternatives, valid banner
Patterns NBT and item-stack Count Byte tags. All 42 item stacks in Amin's nine
chests survive conversion from the supplied newer lowercase count encoding.
Templates target DataVersion 3465. Custom content uses shadowsovereign IDs.
The Pangolin encounter anchor is moved from the supplied narrow corner room to
an open aisle, after a full-width/height empty-cell check found no fitting body
within the old spawn search. All eleven added habitats pass the full-body check,
including the 2.4 × 4.4-block Giant Zombie; real collision shapes need the game.
The ocean-floor density adjustment now runs only in a temperature/humidity band
whose nearest biome is Sunken Kingdom; original humid inland terrain is preserved
outside that band. Actual terrain transitions still need in-game inspection.

## Legendary encounter changes

Exclusive AdventureData.Journal.session fights previously left paused state
that blocked other encounters and summons; habitat checks also blocked editing.
OpenMythics replaces those lifecycle paths with per-creature UUID records:

- Walking away clears targeting and the proximity boss bar. The guardian keeps
  its identity and health in the world, without an arena teleport or forced heal.
- Different guardians coexist. An active/unloaded guardian does not lock another
  species, and an unloaded recorded UUID does not trigger a duplicate body.
- Summons use the normal army system; combat views include current companions.
- Old mythic exclusive sessions are cleared without rewards or changes to other
  adventure kinds. Same-owner/matching-UUID/one-reward capture checks remain.
- Boss-habitat edit vetoes are removed; foreign plots and quest shrines retain
  their existing protection.
- Tick lookups skip creatures without an active UUID, avoiding empty records and
  unnecessary entity searches for unvisited profiles.

Client and server must both use this version. Network protocol: 33-royal-relics.

## Reproduce and inspect

Requires JDK 21 build tooling, Python 3.12+, curl and the original JAR. Bytecode
targets Java 17. This environment's trimmed JDK lacks ct.sym, so compilation uses
-source 17 -target 17; Java 17 runtime linkage still needs a real runtime check.
Extract the fourteen unique supplied source ZIPs into separate input directories,
retaining their internal paths. Duplicate Pangolin and Refai uploads count once.

    bash integration/build.sh /path/to/original.jar /path/to/inputs /path/to/build

ASM/Gson downloads use HTTPS and pinned SHA-256 checks; NBT tooling is pinned.
The original base hash is enforced. Inputs are not modified in place.

- prepare.py converts packs, structures, loot, worldgen, models and animation.
- decor_packs.py imports both new decor packs and generates themed ruins.
- polish_assets.py retires resources, compacts catalogue indices and installs art.
- PatchMod.java applies targeted ASM changes and checks method stacks/locals.
- OpenMythics.java, RemovedContent.java and MigrationHooks.java are production
  helpers. compile_helper.py uses explicit API fixtures for compilation/tests;
  fixtures are never shipped in the mod.
- OpenMythicsTest.java: 36 simulated-world behavioral assertions.
- MigrationTest.java: 10 selective/idempotent cleanup and mapping assertions.
- RegistryTest.java: actual profile initializer and animation reader; 48 matching
  IDs, 37 unchanged surviving profiles, 34 added combat clips, 102 public mod ABI
  references, all mod-owned class references, aquatic routing and edit routing.
- Nine generated/relocated expansion classes, 38 methods: stack/local checks.
- verify.py: 22 resource/integration tests, including eggs, retirement, ocean
  climate bounds, full-body habitat clearance, converted chest stacks, references,
  FX visibility and decorated-ruin support/clearance. With the 1.3.0 JAR present
  in the build directory, the checks compare all previous profiles/resources.
  That single comparison test is skipped if the previous artifact is absent.
- package_delivery.py creates standard 10 MiB split ZIP volumes and verifies
  directory offsets, CRC and the SHA-256 of the complete extracted JAR.

These checks do not prove Minecraft/Forge API linkage or actual gameplay.

## Remaining runtime checks

The official Forge installer download returned HTTP 403 under this environment's
network policy. No Forge runtime is installed, so the following were not run:

1. Dedicated server/client startup, registry and worldgen codec loading, API
   linkage, logs, shutdown/restart and matching multiplayer synchronization.
2. New-world biome/structure generation, ocean transitions, underwater spawn and
   navigation, loot, model rendering, egg UVs, animations and combat effects.
3. Fight, summon, break/place blocks, flee and start another guardian encounter;
   repeat after logout, dimension travel, chunk unloading and server restart.
4. Existing-save migration, capture/recall/respawn and army-full pending rewards.

Use a disposable world or backup for these candidate-build checks. Existing
explored chunks do not regenerate. Obtain the original Forge project for ongoing
source-level development when available.
