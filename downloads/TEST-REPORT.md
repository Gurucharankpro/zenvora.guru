# Updated Soulbound Army — actual build and delivery evidence

Artifact: soulbound-army-1.20.1-1.8.1-content.jar
Bytes: 47,067,897
SHA256: aa4e701d103f7418532a0c5e078e5a3ea0b16afd954d0f48c1d8132b5c2879ec

Minecraft 1.20.1 / Forge 47.3+ / Java17, modid shadowsovereign.
Mod version remains 1.8.1-reviewed as requested. Content network protocol is
38-content-x2-x4; client and server must install this same new artifact.

The exact input was the user's reviewed binary, SHA256
2b9f00f36e7d1228fe31838daf6c147b5f5c719211d3f8467fedac88eba4da26.
No old 1.2.0 source build was substituted. The original input remains explicitly
labelled under content-update/base, separate from the updated distribution.

## New content and placement

Three appended creatures (indices51..53): Morsi/mobile shop in Crimson Shadow,
Porcelain Duchess/palace in Moonveil Hollow, Yuta/Rika/academy in Crimson Shadow.
Cursed Bell Ringer's supplied native model/animations and textures already match;
there is no second registration or duplicate guardian. Yuta's second texture is
packed losslessly and its Rika/FX UVs explicitly route to that region.

60 supplied props: Shadow Clan20, Tormented Animals20, Al Ostoura20. Matching
existing/new habitats have supported placements, visual clearance, preserved
solid architecture/loot, distinct crafting recipes and recipe-book unlocks.
Four existing creative categories are retained; there are180 decor designs.
The shared guardian set contains55 sites; the old52 site entries are unchanged.
New buildings fit the retained48/32 grid; no existing player world was moved.
Morsi's newer template is normalized to1.20.1 and Yuta has explicit body-space air.

X2:20 registered particles /182 original sprites, five verified existing-biome
bindings, five structure atmosphere profiles and ten registered sound assets.
The five source biome profiles absent from the base remain unbound; no invented
biomes were created. X4: native server-owned control/AI overlay, settings/status,
client GUI viaU or /soulboundautopilot, and supplied GUI icons. Ownership and
explicit commands remain mandatory; team sharing and regroup-ignore-hold are
restricted off. Emergency teleport is off by default. Runtime behavior, target
arbitration, formation/recovery edges and performance require the next reviewer.

## Executed checks

- GitHub Actions run41306916540 compiled production source against actual
  Minecraft1.20.1/Forge47.3.22 with Java17 and ForgeGradle6.0.54, then reobfuscated
  it. BUILD SUCCESSFUL, six tasks executed. No fake API fixture compiled release code.
- Eight focused resource check groups passed: full supplied geometry/clips,
  texture routing/pixel preservation, template/version/body clearance, shared
  sites/spacing,60 recipes/placements, retained architecture/loot, existing fixes,
  all182 particle sprites and actual biome identities.
- The real catalogue initializers and animation reader retain all51 old profiles
  and align54 IDs/profiles; new move clips exist and impact times fit their clips.
- Final built JAR catalogue/creative classifier checks pass:54 profiles and180 decors.
- 20,705 owned method/handle references resolve. This does not independently prove
  every inherited game API edge; native compilation supplies separate ABI evidence.
- Final distribution CRC and explicit change whitelist pass. Reviewed OpenMythics,
  X1Hooks, mods.toml, the X1 blocks atlas, all500 old lessons and original X1 frames
  are retained byte-for-byte. No engine fixtures or QA classes enter the game JAR.
- Local generator regression checks pass: all500 regenerated lessons equal the
  reviewed corrected resources including IDs/rewards/biome predicates; X1 generation
  preserves all444 original frames and merges their explicit atlas entries.
- Independently downloaded the complete JAR AND all six published phone volumes
  from immutable GitHub commit3421d016ca4c6f4154a6bc2b9fa518af955667f0.
  All published hashes/CRCs pass. Reassembled/extracted JAR equals the complete
  built JAR byte-for-byte. Five volumes are8,388,608 bytes each; last ZIP3,551,094.

## Generator/source repairs

learning_quests.py emits1.20.1 ContextAwarePredicate arrays, preserving named
biomes. x1_assets.py merges explicit block-atlas frame sources without dropping
other sources. Native reviewed OpenMythics/X1Hooks source is retained; the old
fixture release compiler/replay is retired. New generation only overlays this
reviewed input and preserves shared placement and protected fixes.
CONTENT-MANIFEST.json lists actual modified/added entries and output fingerprint.

## Limits

No Minecraft game launch, native GameTest, graphical/audio verification, external
multiplayer or phone/performance benchmark was run for this new content update.
The older baseline's runtime evidence in BASELINE-TEST-REPORT.md belongs to that
baseline, not this output. Build/static checks are not a zero-bug guarantee.
Use an independent test world and the reviewer checklist. Exact CI output is in
content-update/reports/CI-BUILD.log; remote download evidence is REMOTE-VERIFICATION.json.

## Final publication receipt

Final real-API build/publication run41308589367 (source
8ee8328acd95b42a675ce5c1f2c2a67629fbc6ee) also succeeded and produced the same
47,067,897-byte JAR with the SHA above. Its published snapshot was
45c84b950fac67cd5dbaf8d41b8e2fd204fec2ee. The complete JAR and all six fresh
volumes were independently downloaded again from that exact snapshot; all hashes,
CRCs and extracted-byte equality passed. REMOTE-VERIFICATION.json records them.
Publication handles overlapping report commits without rebase/force-push, and
stages a complete fresh archive set before replacing older current downloads.
The intermediate publication conflict was a CI delivery issue, not a game crash.
A real local Git regression verifies preserved unrelated files, old-volume cleanup
and rejection of stale source builds. Runtime source was unchanged by these CI fixes.
