# Soulbound Army — content update on reviewed 1.8.1

**Build status: SUCCESS. Final updated JAR built: true.**

Source commit: `8ee8328acd95b42a675ce5c1f2c2a67629fbc6ee`.
Base: `1.8.1-reviewed`, SHA256
`2b9f00f36e7d1228fe31838daf6c147b5f5c719211d3f8467fedac88eba4da26`.
Output SHA256: `aa4e701d103f7418532a0c5e078e5a3ea0b16afd954d0f48c1d8132b5c2879ec`.

New content: Morsi and mobile shop (Crimson Shadow); Porcelain Duchess and palace
(Moonveil Hollow); Yuta/Rika and academy (Crimson Shadow). Cursed Bell Ringer is
already present and its supplied native model/animations/textures match; no second
registry entry was created. All 51 old profiles retain their order/definitions.
60 new decor designs are registered/craftable and placed in matching habitats.
X2 provides 20 registered effects/182 sprites, five verified existing-biome
bindings, ten registered ambient sound assets and bounded client ambience.
Its five absent biome profiles do not create invented biomes. X4 adds server-owned
controls, follow/recovery/building/combat assistance, config and a scrolling GUI.

Read [the reviewer prompt](NEXT-AGENT-PROMPT-AR.md) and [the actual build log](../content-update/reports/CI-BUILD.log).
Game/render/multiplayer tests have NOT been run for this new update.

## Downloads

- [Complete updated JAR](../downloads/current/soulbound-army-1.20.1-1.8.1-content.jar?raw=true)
- [Phone split volumes, all <=8 MiB](../downloads/current)
- [Output fingerprint and changed entries](../downloads/current/CONTENT-MANIFEST.json)

Download every matching .z01/.z02/.../.zip into one folder and open the LAST .zip
with ZArchiver. The extracted JAR is verified against the built artifact by CRC
and SHA-256. Replace the old JAR; install this same new file on both sides.
Mod version remains 1.8.1-reviewed as requested, content protocol is38-content-x2-x4.
The file in content-update/base is only the input, not the updated mod.

## Navigation

- [Native production/QA project](../native-review)
- [Overlay/resource build source](../content-update/source)
- [Supplied assets](../content-update/inputs)
- [Staged resources](../content-update/resources)

This is a binary overlay and focused native project, not the original complete
Forge source tree. Historical fixture compilation is retired and never used here.
Reviewed OpenMythics/X1Hooks bytecode, the X1 atlas, 500 old lessons, save identities
and the mod version are explicitly protected by final-distribution comparison.
