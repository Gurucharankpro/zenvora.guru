# Actual first-release checks

Artifact and fingerprints: [current manifest](DUNGEON-GATES-CURRENT.json).
Source `da4f9c6af052130a0b484c44e11af8772da274e4`; verified binary publication `9267db8ed0af213ca3fbb3638bce4385be142e47`.

Five focused static groups pass: JSON/resources and Minecraft model limits; exact native
geometry/animation-bone references and Gecko4.4.9 `_glowmask` routing; final-NBT BFS
arrival-to-boss-to-return connectivity; models/texture references; sprite frame dimensions.
There are exactly48 authored creature clips (5+5+8+10+10+10).

Production native tests: [seed104729](dungeon-gates/reports/PRODUCTION-104729.json),
[seed20261101](dungeon-gates/reports/PRODUCTION-20261101.json),
[client](dungeon-gates/reports/CLIENT.json), [build](dungeon-gates/reports/CI-BUILD.log).
Real API compilation/reobfuscation includes the separate QA inheritance classpath; javap
checks guard the earlier inherited-method mapping fixture failure. QA classes and engine
fixtures are absent from the shipped JAR; all main classes target Java17 (major61).

| Seed | Candidate chunks | Emerald | Frozen |
|---|---:|---:|---:|
| 104729 | 36 | 6 | 6 |
| 20261101 | 36 | 3 | 5 |

Native operations:39 actual palette states in2 template decodes,6 real entity save/reloads,
2 complete assemblies, actual body collision clearance, and persistent completion hooks.
Worldgen uses existing real terrain without manually placing portals or clearing solid
terrain. It tries9 bounded own-chunk sites, allows a one-block slope and harmless plants,
rejects fluids/hazards/solid obstruction, and uses one shared spacing grid for both types.
Test spacing4/chance1 is deliberately denser than defaults12/0.75; do not claim measured
default density. Bounded counters may include initial spawn generation; per-candidate
occurrences above are the sampled locations. New generation does not rewrite old chunks.

Actual graphical client: automatically rendered both gate labels, contact entry to both
correct private destinations, actual lethal player attacks on both bosses, unlocked
contact return, exactly one trophy each, rejected duplicate reward/XP, actual save-close-
reopen retaining ownership/completion/claims. Four actual screenshots are retained under
[dungeon-gates/reports/screenshots](dungeon-gates/reports/screenshots).
Resource-error release gates reject model/texture parser failures, and Frozen model
colored pixel counts are `[1845, 3258, 3027]`. This fixes the
silent black overlay from the producer's incompatible glow suffix; arbitrary rotations
remain exact via an authored mesh field/loader, not rounded vanilla element rotations.

The isolated client fixture uses creative/invulnerable player and immediate boss kills to
focus state transitions. It is not a survival-balance or complete incoming-damage/48clip
execution test. Phone rendering, external multiplayer/latency, restart while inside,
long play, every administrative recovery edge and subjective audio need further review.
No guarantee of zero bugs is asserted. Do not treat historical failed/cancelled source
runs as this artifact's evidence; matching source/hash fields are required.

Independent verification downloaded the ZIP from immutable GitHub content, checked every
outer entry CRC plus gameplay JAR CRC, extracted JAR SHA/size and Java17 class format,
no QA/fake-engine classes, the complete buildable source, both runtime JARs and source-
bound positive proofs embedded inside the ZIP. See the independent verification JSON.
