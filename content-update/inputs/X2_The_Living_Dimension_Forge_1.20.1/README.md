# X2 - The Living Dimension
**Premium Biome Atmosphere Enhancement Pack - Minecraft Java Edition - Forge 1.20.1**

Original hand-crafted pixel-art animated environmental effects for the ten biomes of the Soul Bound Dimension mod. Assets and integration specs only: **no Java, no new biomes/mobs/structures, no standalone mod.**

## Contents
| Folder | What is inside |
|---|---|
| `Animated_Effects/` | 20 folders (`NN_name/`): sprite strip PNG, `*.effect.json` (frame order, timing, scale, lifetime), `*.particle.json` |
| `Textures/` | Drop-in resource tree `assets/x2_living/textures/particle/` (182 PNG frames) and `assets/x2_living/particles/` (20 definitions), plus `spritesheets/` |
| `Previews/` | `ALL_20_EFFECTS_OVERVIEW.png`, per-effect contact sheets (every real frame, labelled) and animated GIFs at the real frame timing |
| `Biome_Profiles/` | 10 JSON atmosphere specifications with Low/Medium/High presets |
| `Structure_Atmospheres/` | 5 JSON placement profiles (temples, castles, dungeons, villages, boss chambers) reusing the 20 effects |
| `Audio/` | 10 original synthesized OGG Vorbis ambient loops (one per biome) + `sounds.json` |
| `Integration/` | Integration guide, requirements matrix, verification checklist, performance settings, master manifest, budget check, validation report |

## The 20 effects
01. **Crimson Ashfall** - biome 01 Crimson Shadow Forest (32x32, 8 frames, 150 ms/frame)
02. **Raven Spirit Wisps** - biome 01 Crimson Shadow Forest (32x32, 12 frames, 150 ms/frame)
03. **Lunar Fairy Dust** - biome 02 Moonveil Hollow (32x32, 8 frames, 150 ms/frame)
04. **Moonflower Glow** - biome 02 Moonveil Hollow (32x32, 8 frames, 200 ms/frame)
05. **Celestial Stardust** - biome 03 The Shattered Sky (32x32, 8 frames, 150 ms/frame)
06. **Floating Crystal Fragments** - biome 03 The Shattered Sky (32x32, 12 frames, 150 ms/frame)
07. **Emerald Spore Clouds** - biome 04 The Eternal Abyss (64x64, 8 frames, 250 ms/frame)
08. **Abyss Crystal Pulse** - biome 04 The Eternal Abyss (64x64, 8 frames, 100 ms/frame)
09. **Enchanted Pearl Bubbles** - biome 05 The Sunken Kingdom (32x32, 8 frames, 150 ms/frame)
10. **Ancient Ocean Lights** - biome 05 The Sunken Kingdom (64x64, 10 frames, 200 ms/frame)
11. **Golden Glass Sandstorm** - biome 06 The Singing Glass Desert (64x64, 12 frames, 150 ms/frame)
12. **Singing Crystal Resonance** - biome 06 The Singing Glass Desert (64x64, 10 frames, 100 ms/frame)
13. **Blue Storm Sparks** - biome 07 The Thunderfall Highlands (32x32, 6 frames, 50 ms/frame)
14. **Thundercloud Energy** - biome 07 The Thunderfall Highlands (64x64, 10 frames, 150 ms/frame)
15. **Floating Dreamseeds** - biome 08 The Dreamroot Canopy (32x32, 8 frames, 200 ms/frame)
16. **Dream Petal Rain** - biome 08 The Dreamroot Canopy (32x32, 12 frames, 150 ms/frame)
17. **Silver Mirror Mist** - biome 09 The Mirrorfall Marsh (128x128, 8 frames, 300 ms/frame)
18. **Reflection Fragments** - biome 09 The Mirrorfall Marsh (32x32, 8 frames, 100 ms/frame)
19. **Ancient Amber Dust** - biome 10 The Titanbone Caverns (32x32, 8 frames, 200 ms/frame)
20. **Fossil Resonance Echo** - biome 10 The Titanbone Caverns (64x64, 10 frames, 100 ms/frame)

## Quick install
1. Copy `Textures/assets/x2_living/` into your mod's `src/main/resources/assets/`.
2. (Optional audio) copy `Audio/assets/x2_living/` the same way.
3. Give the second AI/developer `Integration/INTEGRATION_GUIDE.md` and `EFFECT_REQUIREMENTS_MATRIX.md`.
4. Verify which biomes/structures really exist (`BIOME_VERIFICATION_CHECKLIST.md`) before connecting any profile.

## Honest limits
- The PNGs/JSONs **do not** make particles appear by themselves. Each effect needs a Java `SimpleParticleType` + client provider + a spawner (all 20).
- Biome/structure JSON files are **specifications**; nothing reads them automatically. `biomeRegistryName` / `structureRegistryName` are `null` until verified.
- Fog and weather behavior need Java (fog events, optional custom weather). Details in the guide.
- Audio is synthesized procedurally (numpy + ffmpeg/libvorbis), not recorded; it needs SoundEvent registration to play in game.
- The namespace `x2_living` is a neutral asset namespace; remap to your mod id as described in the guide.

## Performance
Low/Medium/High presets (30/70/120 particles per biome, global cap 150), distance culling, mob-density and boss-fight reduction, no fullscreen overlays, no shaders, all textures 32/64/128 px. Combat visibility rules in `Integration/performance_settings.json`.

See `Integration/VALIDATION_REPORT.md` for the automated verification of every PNG, frame, manifest, audio file and ZIP entry.
