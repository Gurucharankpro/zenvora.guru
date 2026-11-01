# Soulbound Army handoff rules

- Read review/README.md and downloads/TEST-REPORT.md before editing.
- Current distribution is downloads/current/soulbound-army-1.20.1-1.8.1-polished.jar, SHA256 c918aab576bd027675603dc5e11a6103c11a512749c2201e24beee61fd25df2e. Read CURRENT-BASELINE.json for the exact source and runtime evidence. aa4e701d and 2b9f00f3 are historical INPUTS, not current releases. Future updates must analyze and preserve the latest distribution/user-supplied JAR; do not replay old generators over newer content.
- Minecraft1.20.1, Forge47.3+, Java17; preserve modid, registry identities and saves.
- This repository contains binary delivery, CFR reading references and a focused native Forge repair/QA project. It is not the original complete mod source. Historical integration fixture builds are not engine validation.
- Follow review/NEXT-AGENT-PROMPT-AR.md. Preserve the corrected placement predicates, X1 sprite atlas, quiet bounded FX, full-health retreat lifecycle and cross-type shared guardian spacing.
- Compile production changes against actual Minecraft/Forge/mod APIs. Do not ship code compiled against invented ABI fixtures. Inspect actual descriptors before ASM injection.
- Test only new/affected behavior; no unrelated whole-history suites. Distinguish static/resource checks, native tests and actual client launches. Zero tests is not a pass.
- Current native/server and graphical proof and limits are in polish-update/reports/CI-RESULT.json and downloads/TEST-REPORT.md. native-review/docs/VALIDATION-1.8.1.md is historical baseline evidence. QA classes must never enter the gameplay JAR.
- Never run concurrent Gradle builds against the same output directory.
- Preserve armies, ownership, equipment, progression and reward claims. Never erase player structures/regions to retrofit generation.
- Deliver a real distribution JAR with verified SHA and complete split volumes. No full source ZIP unless requested.
- Keep location-trigger player ContextAwarePredicate arrays: vanilla 1.20.1 silently ignores the legacy conditions.location object for minecraft:location. Do not confuse this with placed_block location arrays. Preserve claimed rewards and all 500 learning files.
- In Forge, FakePlayer cannot receive advancements. Positive award tests require an actual ServerPlayer; do not report a rejected fake-player award as a gameplay crash.
