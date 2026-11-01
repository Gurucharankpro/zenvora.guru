# Biome and Structure Verification Checklist

The 10 biome names come from the design brief. They may or may not all exist in the current mod build.

1. Open the mod's worldgen data (`data/<modid>/worldgen/biome/*.json`) and the dimension/biome source files.
2. For each of the 10 profiles in `Biome_Profiles/`, find the real biome registry name. Write it into `biomeRegistryName`.
3. If a biome is NOT implemented, do NOT create it and do NOT wire its effects. Leave the profile unused.
4. Do the same for the 5 structure profiles (`structureRegistryName`). Only apply to structures that already exist.
5. Confirm the 20 effects register without errors (check the particle atlas in the debug log for missing textures).
6. Test each preset (Low/Medium/High) with F3 particle counts, with 0 / 5 / 12 mobs nearby, and during a boss fight.
7. Confirm nothing blocks the crosshair or enemy silhouettes during combat.
