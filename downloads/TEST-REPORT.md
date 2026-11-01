# Soulbound Army 1.8.1 — reported loading crash fix

The user screenshot reports NoSuchMethodError for
GloomContent.item(String,int,float), returning RegistryObject. The 1.8.0 X1
integration injected that nonexistent overload into GloomContent.<clinit>.
The actual registration helper is private static food(String,int,float).
The public item(String) method is a different lookup API. The earlier helper
ABI checks missed this private call inside a patched native initializer.

The patch now asserts the actual static food factory exists and invokes it for
x1_visual. Hunger zero selects plain Item.Properties, so this does not make
the internal effect item edible. The item remains hidden from creative tabs.
No 1.8.0 content, creatures, effects or gameplay hooks were removed.

## Regression evidence

The new OwnLinkageTest scans owned calls and invokedynamic method handles,
including private/synthetic initializer paths. It rejects the actual published
1.8.0 JAR with the exact missing method in the screenshot. The fixed 1.8.1 JAR
passes 20,193 owned references. Unknown Minecraft/Forge inherited edges remain
explicitly outside this check; it is not an engine linkage guarantee.

See known-bad-linkage.log (expected failing regression), BUILD-TEST.log (fixed
build), and PACKAGE-TEST.log (delivery verification). The intentionally injected
X1 fixture-failure warning in BUILD-TEST.log tests cosmetic error isolation.

Executed successfully:

- 28 resource/integration test groups.
- 42 encounter, 10 mapping/migration and 22 X1 routing fixture assertions.
- 20,193 owned method/handle references, including the corrected factory.
- 11 expansion classes / 53 methods: bytecode stack/local checks.
- 115 helper references against actual public mod members.
- All 51 previous creature profiles/order, 120 decor classifications, 57 egg
  designs, four creative categories, 500 learning lessons and old resources.
- Three independent AI ZIP CRC checks, reconstructed JAR hash and inner ZIP CRC.
- Separate standard split-ZIP central directory, CRC and complete JAR hash.

## Exact artifact

Name: soulbound-army-1.20.1-1.8.1-shadow-awakening-fix.jar

Bytes: 41857189

SHA256: beeedcd097ebbfc8dc212f34b52d6fd3f6d09504aacced44bfd249aa7a16cf3d

Version: 1.8.1-shadow-awakening-fix. Protocol stays 37-shadow-awakening.
Install the fixed JAR on client and server; remove the older mod JAR.
Previous source release: 664986964057907453b838cd93b7d53a92aebd7e (1.8.0).
Previous artifact SHA256:
9266750cd68571a6c013ffe05e8fd06355b8aeb1b825b16735ff17612ccc137a.

## Delivery and review

ai/ contains exactly three independently readable ordinary ZIPs. Their ordered
raw binary chunks reconstruct the entire mod with rebuild_ai_mod.py; chunk and
complete artifact hashes are recorded in AI-MANIFEST.json. UPLOAD-SIZES.json
records exact compressed/expanded sizes and enforces strictly below 20,000,000
bytes per upload and 50,000,000 total. Upload only these three ZIPs to the AI.
Part 01 includes integration sources, and part 03 includes Java decompiled by
CFR 0.152 from this exact fixed JAR. It is a reading reference, not the original
complete buildable Forge project. CFR cannot fully structure SoulPlay.tick;
read its bytecode when assessing that method. No reference source is shipped
inside the gameplay JAR. Normal Android/ZArchiver delivery is separately in play/.

## Limits

No actual Forge client/server startup, live rendering, multiplayer, codec/API
execution, save migration or performance run was possible. The enforced network
policy excludes official Forge artifact hosts; previous installer fetches were
denied. Tests use explicit fixtures, not the engine. The fix removes the exact
reported missing call; successful tests do not prove the whole mod bug-free.
Target is Forge Minecraft 1.20.1 / 47.3+, Java 17. Tooling uses JDK21 with Java17
source/target; missing ct.sym prevents --release API validation.

The earlier X1 limits are retained: finite sprite ItemDisplay effects rather
than skeletal creature animations; 24 display cap per dimension; auras are
temporary bursts; unsupported UI widgets and mesh/dome attachments remain unbound.
