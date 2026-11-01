# Effect Requirements Matrix

Every effect needs a **custom Forge particle registration** (a Java `SimpleParticleType` + client `ParticleProvider`). PNG and JSON files alone do NOT create working particles.

| # | Effect | Biome | Frame size | Frames | Playback | Custom particle reg. | Spawn logic | Secondary spawn | Weather hook | Fog hook |
|---|---|---|---|---|---|---|---|---|---|---|
| 01 | Crimson Ashfall | 01 Crimson Shadow Forest | 32x32 | 8 @ 3t | loop | yes | yes | - | - | - |
| 02 | Raven Spirit Wisps | 01 Crimson Shadow Forest | 32x32 | 12 @ 3t | one-shot | yes | yes | yes | - | - |
| 03 | Lunar Fairy Dust | 02 Moonveil Hollow | 32x32 | 8 @ 3t | loop | yes | yes | - | - | - |
| 04 | Moonflower Glow | 02 Moonveil Hollow | 32x32 | 8 @ 4t | loop | yes | yes | - | - | - |
| 05 | Celestial Stardust | 03 The Shattered Sky | 32x32 | 8 @ 3t | loop | yes | yes | - | - | - |
| 06 | Floating Crystal Fragments | 03 The Shattered Sky | 32x32 | 12 @ 3t | loop | yes | yes | - | - | - |
| 07 | Emerald Spore Clouds | 04 The Eternal Abyss | 64x64 | 8 @ 5t | loop | yes | yes | - | - | - |
| 08 | Abyss Crystal Pulse | 04 The Eternal Abyss | 64x64 | 8 @ 2t | one-shot | yes | yes | - | - | - |
| 09 | Enchanted Pearl Bubbles | 05 The Sunken Kingdom | 32x32 | 8 @ 3t | loop | yes | yes | - | - | - |
| 10 | Ancient Ocean Lights | 05 The Sunken Kingdom | 64x64 | 10 @ 4t | loop | yes | yes | - | - | - |
| 11 | Golden Glass Sandstorm | 06 The Singing Glass Desert | 64x64 | 12 @ 3t | one-shot | yes | yes | - | yes | - |
| 12 | Singing Crystal Resonance | 06 The Singing Glass Desert | 64x64 | 10 @ 2t | one-shot | yes | yes | - | - | - |
| 13 | Blue Storm Sparks | 07 The Thunderfall Highlands | 32x32 | 6 @ 1t | one-shot | yes | yes | - | - | - |
| 14 | Thundercloud Energy | 07 The Thunderfall Highlands | 64x64 | 10 @ 3t | loop | yes | yes | - | yes | - |
| 15 | Floating Dreamseeds | 08 The Dreamroot Canopy | 32x32 | 8 @ 4t | loop | yes | yes | - | - | - |
| 16 | Dream Petal Rain | 08 The Dreamroot Canopy | 32x32 | 12 @ 3t | loop | yes | yes | - | - | - |
| 17 | Silver Mirror Mist | 09 The Mirrorfall Marsh | 128x128 | 8 @ 6t | loop | yes | yes | - | - | yes |
| 18 | Reflection Fragments | 09 The Mirrorfall Marsh | 32x32 | 8 @ 2t | one-shot | yes | yes | - | - | - |
| 19 | Ancient Amber Dust | 10 The Titanbone Caverns | 32x32 | 8 @ 4t | loop | yes | yes | - | - | - |
| 20 | Fossil Resonance Echo | 10 The Titanbone Caverns | 64x64 | 10 @ 2t | one-shot | yes | yes | - | - | - |

Legend
- **Custom particle reg.** - register `x2_living:<name>` (or `<yourmodid>:<name>`) as a particle type and attach a sprite-set provider on the client.
- **Spawn logic** - something must call `addParticle` at the right places/times (block `animateTick`, client tick handler, or a biome JSON `effects.particle` entry for ONE ambient particle per biome).
- **Secondary spawn** - the effect should spawn another effect at the end of its life (Raven Spirit Wisps -> a few Crimson Ashfall motes).
- **Weather hook** - the effect benefits from weather state. Vanilla `Level#isRaining()/isThundering()` is enough for the multipliers in the profiles; a custom sandstorm or a lightning-synced cyan flash needs custom weather logic.
- **Fog hook** - the effect is meant to pair with biome fog; fog colors/density need Forge `ViewportEvent.ComputeFogColor` / `ViewportEvent.RenderFog` (or fog fields in the biome JSON).

Playback modes
- **loop**: sprite index = `(age / ticksPerFrame) % frameCount`. Needs a custom `TextureSheetParticle` tick/sprite call (vanilla `setSpriteFromAge` stretches frames across the lifetime instead).
- **one-shot**: sprite index mapped from `age / lifetime` (vanilla `setSpriteFromAge`). Lifetime in the manifest equals `frames x ticksPerFrame`.
