# Dungeon Gates — first two gates (0.1.0)

**[Download the complete ZIP (16.56 MB)](https://raw.githubusercontent.com/Gurucharankpro/zenvora.guru/9267db8ed0af213ca3fbb3638bce4385be142e47/dungeon-gates-downloads/Dungeon_Gates_Forge_1.20.1_Complete_Mod.zip)**

Minecraft Java1.20.1 · Forge47.3.22 · Java17 · GeckoLib4.4.9.
This is a NEW independent mod, modid `dungeongates`. It does not replace or depend
on Soulbound Army. The earlier Soulbound branch is untouched.

The first two uploaded gates are complete: **Emerald Trial** and **Frozen Grave**.
The other four wait for their actual packages; no empty placeholder gates are registered.
Both generate naturally in suitable new Overworld chunks, display automatic four-line
level/number/name/category labels, and enter by touching their floating energy surface.
Each has an isolated private dungeon, two regular species, a mini-boss, working treasure
crates, a locked return path and one-time crown/XP rewards. Six authored mob models and
48 clips,24 supplied blocks/decor, source art, configs and future-integration event are included.
Emerald uses the supplied64x28x64 template. Frozen had no NBT: a connected64x28x64 royal crypt
was authored using its12 supplied blocks. No soul/army system is implemented.

Extract the ZIP, then install BOTH JARs from its `mods/` folder into the Forge client's
and server's mods folders. The `DungeonGates/` folder is the full source project and
contains Arabic instructions. New world generation affects new chunks only.

## Actual verification

| Normal Overworld seed | Candidate chunks requested | Emerald found | Frozen found |
|---|---:|---:|---:|
| 104729 | 36 | 6 | 6 |
| 20261101 | 36 | 3 | 5 |

These are real world-generated gates, not commands/manual portal placement. Test density
was spacing4chunks/chance1; defaults are12/0.75. Dependency chunks were also generated
but are not counted as sampled candidates. Terrain/spacing/hazard checks remained active.

The final obfuscated JAR loaded on an official dedicated Forge server. Native checks
covered real palette properties/template decoding, six entity attribute/save/reload paths,
two actual dungeon assemblies, body clearance and completion hooks. A separate actual
Xvfb/Mesa client at the same source commit verified labels, both touch entries, actual boss
deaths, unlocked returns, one-time trophies and true save-close-reopen. Model/texture log
checks pass; Frozen colored rendering is checked in actual screenshots. The graphical
client uses native source outputs; dedicated servers use the final production JAR.

No phone, external multiplayer, long combat-balance session or exhaustive all48clip test
is claimed. These remain in [the Arabic review checklist](DUNGEON-GATES-REVIEW-AR.md).

[Full test report](DUNGEON-GATES-TEST-REPORT.md) · [Current artifact manifest](DUNGEON-GATES-CURRENT.json) ·
[Independent download/CRC proof](DUNGEON-GATES-DOWNLOAD-VERIFICATION.json) ·
[Source project](dungeon-gates) · [Integration hook](dungeon-gates/INTEGRATION.md).

Gameplay JAR SHA256:
`2f6018facbb2e50d7bf3b08bcbd3209dc865a185dd43f54a436551df3c6e87b5`

ZIP SHA256:
`7b34c4090b7a76a5aa0c7a2d496e563752123c7e673e8dd9add1167a7013f4ea`

Built source: `da4f9c6af052130a0b484c44e11af8772da274e4`. Artifact publication: `9267db8ed0af213ca3fbb3638bce4385be142e47`.
The complete GitHub ZIP was independently downloaded; its recursive ZIP/JAR CRCs,
all fingerprints, source contents and matching runtime evidence were verified.
