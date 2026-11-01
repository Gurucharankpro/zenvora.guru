# Native Forge repair and focused QA source

## New content work in progress

Additional production source is in qa/src/repair/java/dev/shadowsovereign/content.
It has NOT completed native compilation: official Forge/Minecraft downloads are
blocked by the current runtime allowlist. The concrete network draft is saved
and requires user publication. Do not ship these classes or claim them verified
until real ForgeGradle compilation/reobfuscation succeeds. No new distribution
JAR has been built. Existing native tests below belong to the supplied reviewed
release, not this new content update.

The new overlay uses the exact reviewed JAR, preserves its mod version and the
reviewed OpenMythics/X1Hooks class bytes, appends profiles/registry IDs, and merges
new habitats into the existing shared placement set. The separate build entry
point is /workspace/soulbound-current/content-update/source/build_content.sh.
Sources/tests/assets and pending validation are retained there. The original
fixture build path is retired; it cannot generate a release overlay anymore.

## Reviewed release reproduction and QA

This is a small real-API repair/QA project for the reviewed1.8.1 binary, not the full original mod source. Editable production changes:qa/src/repair/java/dev/shadowsovereign/expansion/OpenMythics.java and X1Hooks.java. Native tests/client scripts are inqa/src/main and must never enter the gameplay JAR.

Read../NEXT-AGENT-PROMPT-AR.md, ../downloads/TEST-REPORT.md and../downloads/REVIEWED-MANIFEST.json. The current reviewed JAR hash is2b9f00f36e7d1228fe31838daf6c147b5f5c719211d3f8467fedac88eba4da26.

Use Java17 and ForgeGradle6.0.54. Publish the current JAR inqa/localrepo aslocal.soulbound:army:1.8.1-reviewed2 with a matching minimal POM. Copyqa/gradle.properties.example if needed. Run./gradlew runGameTestServer inqa for four focused tests, or add-PqaNamespace=soulboundsiteqa for the separate native natural-dojo case. runClient-Psmoke runs a scripted isolated QA world; -PqaSave=<QA save name> resumes a test copy. A working display server is needed. Never point the script at a personal save. Never run concurrent Gradle builds in the same output directory.

./gradlew repairJar reobfRepairJar compiles the two replacement helpers against real Minecraft/Forge/mod APIs and produces a small SRG runtime overlay. This does not rebuild the entire mod. Overlay onto the correct new baseline and preserve all other entries. Do not compile releases against the historical invented-ABI fixture classes underintegration.

Historical reproduction of the exact reviewed artifact usesrepairs/build_reviewed.py: it expects the pre-review original SHAbeeedcd097ebbfc8dc212f34b52d6fd3f6d09504aacced44bfd249aa7a16cf3d. That original is retained in Git history atcommit96c9590a96da3b0020d1081bc15f966196306298 underdownloads/ai. Its rebuild_ai_mod.py can reconstruct it. Put it atnative-review/original-1.8.1.jar, publish the original Maven coordinate1.8.1, build the helper with-PqaArtifact=1.8.1, then run the overlay script. The pin intentionally rejects unrelated future inputs; do not apply it blindly to later releases.

The overlay script documents the exact placement-predicate, atlas and shared-habitat corrections. For future new content, update its generators accordingly rather than replaying the old upstream generators over a reviewed JAR. Tests/logs and screenshots document what was actually validated. No full mod source ZIP is provided.
