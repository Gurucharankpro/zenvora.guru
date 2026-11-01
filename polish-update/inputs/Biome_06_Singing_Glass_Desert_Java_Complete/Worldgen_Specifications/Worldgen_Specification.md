# Worldgen specification - Biome 06 Singing Glass Desert
**Specification only - nothing here implements world generation.** The machine-readable version is `Worldgen_Specification.json`.
The biome must be *added* to the existing Soul Bound Dimension (do not replace its five biomes): add to the dimension's multi-noise biome
source with its own parameter slice and a transition buffer so it borders desert-like neighbours first.

## 1. Visual / environmental settings
Sky `#d9783a` (orange), fog `#b46cc4` (violet), water `#3fd6c8`, sand/grass tint `#b9a2d6`, foliage tint `#d8b64a`. Low-probability lavender/gold dust
particles; sandstorm look is fog/particle only. Any wind-tone *sounds* and mechanics are for the implementing developer.

## 2. Blocks
Surface: violet dune sand (65%) with golden glass sand patches (noise > 0.35). Sub-surface 3-5 sand, then ancient sandstone 4-8; cracked sandstone
30% deeper than 12 blocks below surface; polished mirage stone sparingly on wind-cut faces. Singing crystal ore: ~8 veins/chunk, size 4-8,
8 to 50 blocks below surface.

## 3. Trees
Glasswood: ~0.35/chunk, clustered petrified groves, spacing >= 6, designs Large 40% / Small 60%.
Mirage palm: 6-12 per oasis within 9 blocks of water, 0.12/chunk elsewhere, spacing >= 4, Tall/Short 50/50.

## 4. Vegetation (attempts per chunk)
Whispering grass 18, golden dune fern 4, crystal cactus 2.5, glass thorn bush 1.5, moon sand mushroom 2 (shaded), singing crystal cluster 3 + 1.
Mirage lotus only at oasis edges.

## 5. Formations, dunes, oases
Giant amber crystals 1/2 chunks (h 10-26); glass spires 1/3 chunks (h 14-32); giant arches 1/6 chunks (span 14-24); instrument crystals 1/8 chunks.
Dunes: ridged noise wavelength 48-96, amplitude 6-14 plus ripple. Oases 1 per ~120x120, radius 5-9, in troughs.

## 6. Structures (spawn rules, rarity)
| Structure | Rarity | spacing / separation | adaptation |
|---|---|---|---|
| Caravanserai | uncommon | 22 / 9 | beard_box, slope <= 2 |
| Observatory | rare | 28 / 12 | bury, dune areas |
| Cathedral | very rare | 36 / 16 | beard_box, slope <= 3 |
| Labyrinth | rarest | 44 / 20 | beard_box, slope <= 3 |
Min 12 blocks from other structures; never overlap oases.

## 7. Creature spawn
Prism Jerboa: weight 6, groups 1-3, on sand/sandstone, not inside structures except the documented encounter areas.

## 8. Clearance and performance
See JSON `clearance` and `performance` sections (cap formations/chunk, cap glow decorations ~40/chunk, large NBTs at structure-start only).
