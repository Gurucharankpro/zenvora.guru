# Soulbound Army handoff rules

- Read review/README.md and downloads/TEST-REPORT.md before editing.
- Current canonical artifact is1.8.1-reviewed, SHA2562b9f00f36e7d1228fe31838daf6c147b5f5c719211d3f8467fedac88eba4da26, unless the user supplies a newer version. Do not substitute an old source build.
- Minecraft1.20.1, Forge47.3+, Java17; preserve modid, registry identities and saves.
- This repository contains binary delivery, CFR reading references and a focused native Forge repair/QA project. It is not the original complete mod source. Historical integration fixture builds are not engine validation.
- Follow NEXT-AGENT-PROMPT-AR.md. Preserve the corrected placement predicates, X1 sprite atlas, quiet bounded FX, full-health retreat lifecycle and cross-type shared guardian spacing.
- Compile production changes against actual Minecraft/Forge/mod APIs. Do not ship code compiled against invented ABI fixtures. Inspect actual descriptors before ASM injection.
- Test only new/affected behavior; no unrelated whole-history suites. Distinguish static/resource checks, native tests and actual client launches. Zero tests is not a pass.
- Native proof and limits are in native-review/docs/VALIDATION-1.8.1.md. QA classes must never enter the gameplay JAR.
- Never run concurrent Gradle builds against the same output directory.
- Preserve armies, ownership, equipment, progression and reward claims. Never erase player structures/regions to retrofit generation.
- Deliver a real distribution JAR with verified SHA and complete split volumes. No full source ZIP unless requested.
