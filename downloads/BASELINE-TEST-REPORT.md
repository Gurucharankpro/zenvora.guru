# Soulbound1.8.1 reviewed — observed validation

Upstream:https://github.com/Gurucharankpro/zenvora.guru/tree/ai-review-soulbound-army-1.8.1/review
Commit96c9590a96da3b0020d1081bc15f966196306298.
Exact original JAR:41,857,189 bytes; SHA256beeedcd097ebbfc8dc212f34b52d6fd3f6d09504aacced44bfd249aa7a16cf3d.
Original Java is CFR reference, not a complete Forge project. No older local1.2.0 source tree was substituted. Two existing helpers were converted to official names using Forge's cached mappings, compiled against the real remapped1.8.1 binary/Minecraft/Forge APIs, reobfuscated with ForgeGradle and overlaid onto that exact original JAR. All other entries are preserved except documented resource fixes, version qualifier and independent placement sets.

## Actual production server

181-forge-install.log: official Forge1.20.1/47.3.22 server installed.
181-production-server-original.log: exact original binary reachesDone, but160 placement lessons fail parsing. Its X1 tick commands emit continuous successful-function log messages.
181-production-server-reviewed.log: preliminary repaired exact production binary reachesDone without those lesson errors or repeated function output.
181-production-final.log: final exact binary, including atlas addition, reachesDone(5.189s), runs and saves/stops normally. Offline authentication-key warnings are expected in this isolated environment; they are not mod-loading failures.

## Native engine tests

181-native-tests3.log: **4/4 required tests PASS**, BUILD SUCCESSFUL1m33s. Real Forge classes/worlds, actual connected ServerPlayers, native advancement and placement predicates, live mobs, real damage-safe building and summon calls, native ItemDisplay commands/tick expiry, real structure-placement algorithm. Tests verify:
- All500 learning advancements parse. Wrong block and wrong dimension do not grant a placement task; correct placement does, without repeatedXP.
- A real new guardian starts without exclusive adventure session. A reserve cow summons and blocks can be broken. Boundary retreat clears canonical body/UUID/snapshot; another guardian starts; re-entry has full maximum health; a delayed old body cannot join. Devourer's separate ledger resets too.
- Actual cosmetic functions create effects with cap<=24, expire by150ticks, and preserve an unrelated display.
- All52 legendary habitats occur in exactly one shared set; native positive/negative seeded candidate checks enforce33-chunk minimum separation in an axis.
This run preceded the atlas-only client resource addition; its tested server helper bytecode and advancement/placement resources are identical in the final output.

181-native-site3.log: **1/1 required test PASS**, BUILD SUCCESSFUL1m6s. Native locate/generation produces Glass Ronin's actual rotated dojo in fresh distant Echo Realms chunks; authored altar lies in the building and near its generated surface; its correct live guardian appears inside saved bounds.

## Actual graphical client

181-native-client6.log: **181 NATIVE CLIENT PASS**, BUILD SUCCESSFUL1m40s. Forge development client with the final JAR remapped by ForgeGradle, integrated unpublished offline server, copied independent QA save:
- Real keyboardH event opens native menu.
- Real owner attack kills latest Glass Ronin, grants one soul and collection entry; repeat award fails.
- Native book displays earned3D companion; actual book summon packet creates live owned body.
- World companion/effect smoke; latest six native models render.
- Actual save, disconnect, world reopen retain the same soul/type/collection without duplicate.
- Final client log has no missing-X1-texture warning. Original client logged all444 frames as missing because their directory was absent from the block atlas.
Screenshots retained: h-army, earned-glass-ronin, world-companion-and-fx, latest-six-native-models.

This is an actual Minecraft client launch, not a headless fixture, but it is a Forge development launch, not a separate production launcher installation or phone. Audio backend is unavailable: audible playback is not certified. Existing oversized egg/icon images still produce mipmap warnings; their artwork was preserved. NoFPS/mobile performance benchmark or two separate external multiplayer clients were run. Concurrent embedded server players and an actual integrated-client connection were exercised. Individual visual inspection of every animation frame is not claimed.

## Diagnostics not counted as passes

181-qa-setup.log: initial local remap dependency resolution failure; real Forge tooling repositories/mappings were then configured.
181-native-tests.log: unavailable unproxied metadata fetch; no gameplay pass.
181-native-tests2.log: QA template namespace/resource setup error; not a mod defect or pass.
181-native-site.log: zero registered tests due QA namespace filter; not counted as success. Intermediate concurrent compile output collision was corrected by running Gradle tasks sequentially.
Intermediate native clients: the first used an inadequate key-queue stimulus; later actions ran while an asynchronous H response reopened a paused menu, leaving chunks entity-unready. Spawn guards correctly refused the fixture. The final fixture uses a real keyboard event, closes delayed menu responses and waits for entity readiness. The persisted QA experimental-world dialog was accepted only in the isolated test save. Earlier failed smoke launches are retained as diagnostics, not successful full launches.

## Artifact integrity

Final artifact SHA/bytes and changed/removed entry list:overlay-manifest.json.
audit_reviewed.py: every untouched original entry byte-identical,160 corrected placement predicates,500 retained lessons,444 original PNGs resolved by explicit atlas sources,52 uniquely grouped habitats, no QA classes, ZIP CRC and SHA match.
Original OwnLinkageTest passes20,232 owned method/handle references including private initializers; it explicitly does not prove unknown Minecraft/Forge inheritance edges. Native launches provide separate runtime evidence.
Packaging reconstructs all split volumes, checks CRC and verifies extracted JAR byte equality. No full source ZIP is delivered. Validation was performed locally; subsequent publication is recorded by the branch Git history.
