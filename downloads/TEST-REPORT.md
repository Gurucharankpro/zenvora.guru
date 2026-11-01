# Soulbound Army — actual final-build evidence

Current artifact: `current/soulbound-army-1.20.1-1.8.1-polished.jar`
Bytes: 50,841,760
SHA256: `c918aab576bd027675603dc5e11a6103c11a512749c2201e24beee61fd25df2e`
Minecraft 1.20.1 / Forge 47.3.22 / Java 17 / modid `shadowsovereign`.
Version remains `1.8.1-reviewed`; protocol `39-polish-glass-desert` requires matching client/server JARs.

Source commit: `0a7434cb1533260e9e21682d0017b071a4448ae8`.
Build/runtime Actions run: [41320257250](https://github.com/Gurucharankpro/zenvora.guru/actions/runs/41320257250).
Verified artifact publication: `a8d93f713d18483d54d86d9601d2808c7060763c`.

## Input and preservation

The latest complete aa4e701d distribution was repaired in exactly two class entries:
GloomContent's 63 LambdaMetafactory bootstrap descriptors and ContentConfig's
incompatible debug-log minimum. The reconstructed repair input is
`f88cac49bae0d10961a277ab2277738892cb5ba4aaaae596fc5b0c17d64edb04`. The reports' corrected b198 binary was not attached;
we do not claim byte identity with that absent file. No older source replaced the
latest distribution. The historical 2b9f00f3 artifact is not the final output.

The new output has 57 profiles and 200 decor designs, with no old registry reorder.
Final exact-entry checks protect GloomContent's repaired bytes, OpenMythics,
X1Hooks, mods.toml, X1 atlas and frames, all 500 existing learning files and every
entry outside the explicit manifest. There are 104 modified and
336 added JAR entries, and zero removed entries. Existing rewards
and claim state are preserved; affected journey visit predicates/icons are deliberately corrected.

## Executed checks

- Native production compilation and reobfuscation against actual Minecraft/Forge APIs.
- Nine focused resource/preservation groups: full authored geometry and animation
  clips, texture routing, 20 new prop placements and unique recipes, supported
  guardian space, old architecture/loot preservation, shared structure placement,
  old dimension entries, valid item/block models, journey predicates and icons.
- Final native catalog: all 54 old profiles retained in order, 57 total profiles,
  200 decor entries classified correctly, new attack clips and timings present.
- 20,804 owned method/handle references resolve; 3,977 Java bootstrap/handle
  references checked against the real JDK, including private/synthetic members.
- **Actual dedicated Forge server** started with the final obfuscated gameplay JAR
  and a separate QA mod in an isolated fresh save. Checked 40 block/item pairs,
  17 native NBT template decodes, 792 real palette states and four new entity
  attribute/save/reload paths. No QA or mock Minecraft classes enter delivery.
- **Actual advancement engine:** wrong dimension rejected; ten wrong-biome visits
  rejected; correct desert/dimension and welcome granted; repeated trigger did
  not grant XP again. This uses real ServerPlayer instances and an in-process
  Netty connection; it is not an external multiplayer test.
- **Actual new-biome generation:** located Singing Glass Desert at [1280,-288],
  generated four chunks, flushed the save, then independently decoded saved MCA
  NBT: all four chunks FULL, actual desert biome and custom surface blocks present.
- **Actual graphical client** using ForgeGradle-remapped final JAR, Xvfb/Mesa:
  opened army menu and book, captured the latest legendary soul, summoned it,
  displayed all three new legends and Prism Jerboa, all four new eggs and 16
  badges, saved/disconnected/reopened with the earned soul retained. Five real
  screenshots and client logs are retained. Visual inspection confirms larger book
  portrait, visible models and distinct correctly proportioned egg/badge images.
- **Independent GitHub download verification:** downloaded the full JAR and all
  six phone volumes from the immutable publication above. Every SHA and CRC passed.
  Reassembled/extracted JAR is byte-identical to the full built JAR. First five
  volumes are 8,388,608 bytes each, final ZIP is 7,215,938 bytes; combined archives
  total 49,158,978 bytes. Download all matching volumes into one folder and extract
  the LAST `.zip` using ZArchiver; do not mix older archive parts.

## New content and behavior

See [review/README.md](../review/README.md) for exact placement/use. New content:
Dr. Salmawy/Secret Mansion in Crimson Shadow; Raven Seamstress/Theater and
Clockwork Widow/Palace in Moonveil Hollow; 20 Eclipse props and unique recipes;
Singing Glass Desert in Echo Realms with 20 environmental blocks/plants, four
buildings, four tree templates, Prism Jerboa, bounded crystal/plant/oasis ecology
and existing X2 biome ambience. All seven new buildings use the retained shared
cross-type reviewed placement. Only new chunks receive new generation.

Fixed invalid authored slab names/grindstone properties, blocked mansion passage
and unfinished basement step, empty new Eclipse chest loot, capped oasis water,
book framing and egg proportions. Journey visit criteria now use the actual
1.20.1 player-context predicate format instead of silently ignored legacy fields.
No old claimed reward is reset; no army or player structure is erased.

## Evidence and limits

[CI summary](../polish-update/reports/CI-RESULT.json),
[build log](../polish-update/reports/CI-BUILD.log),
[production smoke](../polish-update/reports/PRODUCTION-SMOKE.json),
[client smoke](../polish-update/reports/CLIENT-SMOKE.json),
[independent verification](../polish-update/reports/INDEPENDENT-VERIFICATION.json),
[exact modified/added file list](current/CONTENT-MANIFEST.json).
All runtime evidence records this source commit and this final JAR SHA.

Not executed: phone rendering/performance, external multiplayer, long-session combat
balance, every encounter/retreat permutation, every natural building entrance,
rotation and terrain combination, or subjective audio review. Static preservation
of retreat/ownership code is not a fresh test of every retreat case. The pack's
terrain/formation design numbers are not all implemented literally; bounded native
worldgen is used. No zero-bug guarantee is made. Test the new and affected behavior
in a separate world using [the Arabic handoff](../review/NEXT-AGENT-PROMPT-AR.md).

Older reports/manifests in downloads/history and native-review/docs describe their
historical artifacts and must not be presented as this build's evidence.
