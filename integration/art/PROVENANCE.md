# Spawn-egg artwork

These transparent PNGs were generated for the user's requested unique spawn-egg
textures. They are copied into the mod without raster editing. Model JSON selects
UV regions for the shared atlas. No third-party egg asset pack was imported.

- egg-atlas.png: 1254 × 1254 RGBA, 8 × 8 atlas, 52 authored eggs in row-major
  lexicographic order of surviving creature IDs, excluding the two standalone eggs.
- amin-egg.png: 1254 × 1254 RGBA, separate olive/gold mechanic design reflecting
  Amin's supplied workshop, clothing, keys and watch.

The deterministic cell-to-item mapping is emitted in integration-spec.json
in each local build's resource output. Runtime models embed each assigned UV.

- king-egg.png: separate 1254 x 1254 RGBA golden royal egg, magenta bow tie,
  grinning sun/cheese emblem and purple cape motifs, generated from the supplied
  King appearance. Its addition does not alter any existing atlas UV assignment.
