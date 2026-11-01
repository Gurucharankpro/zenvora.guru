# Integration Guide (for the Java developer / second AI)

Target: **Minecraft Java Edition, Forge 1.20.1 only.** This pack contains **assets and specifications only**. No Java, no standalone mod.

## 1. What is real and what is only a specification

| Item | Status |
|---|---|
| 182 transparent PNG frames + 20 horizontal sprite sheets | Real, ready to use |
| `*.particle.json` / `assets/x2_living/particles/*.json` | Real vanilla-format particle definitions (list of frame textures). They only take effect once a matching particle type is registered in Java |
| 20 `*.effect.json` manifests | Real data describing frame order, timing, size, lifetime, motion hints |
| 10 biome JSON profiles, 5 structure profiles, `performance_settings.json` | **Specifications.** Nothing reads them automatically. Your code must read them (or hard-code the values) |
| 10 OGG ambient loops + `sounds.json` | Real audio. Needs SoundEvent registration in Java to be used by biomes |
| Custom particles, fog, weather, structure spawning | **NOT implemented by this pack** - they require Java |

## 2. Installing the textures
Copy `Textures/assets/x2_living/` into your mod's `src/main/resources/assets/x2_living/` (or merge into `assets/<yourmodid>/`).
- Frames: `assets/x2_living/textures/particle/x2_<name>_NN.png`
- Particle definitions: `assets/x2_living/particles/<name>.json` - each lists `"x2_living:x2_<name>_NN"` frames.

**Namespace note.** A particle definition JSON must live in the namespace of the registered ParticleType: `assets/<particleTypeNamespace>/particles/<registryName>.json`. If your mod registers the types as `<yourmodid>:<name>`, move the files from `assets/x2_living/particles/` to `assets/<yourmodid>/particles/`. The texture references inside (`x2_living:x2_...`) can stay as-is, because particle textures are stitched from every namespace's `textures/particle/`. If you would rather move the PNGs too, search/replace `x2_living:` with `<yourmodid>:` in the JSON files.

## 3. Registering a custom particle (what the Java developer must do)
1. Create a `DeferredRegister<ParticleType<?>>` and register 20 `SimpleParticleType`s named after the effect slugs (see manifests, field `particle.registryName`).
2. On the client mod bus, handle `RegisterParticleProvidersEvent` and register a provider for each type that supplies a `SpriteSet`.
3. Implement a particle class extending `TextureSheetParticle`:
   - render type `ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT`
   - quad size, lifetime, gravity and drift from the manifest (`particle.*`)
   - sprite selection: *loop* effects use `(age / ticksPerFrame) % frameCount`; *one-shot* effects use age-over-lifetime
   - fullbright effects (`particle.fullbright = true`): override `getLightColor` to return full light
   - `maxAlpha` is the upper bound for the quad alpha
4. Spawn from client-side code only (`Level#addParticle` / `ClientLevel`), never from the server tick loop for decorative effects.

## 4. Which effects need what
See `EFFECT_REQUIREMENTS_MATRIX.md`. Summary:
- **All 20**: custom Forge particle registration + a spawner.
- **Weather rendering / custom weather logic**: 11 Golden Glass Sandstorm (optional sandstorm weather and wind direction), 14 Thundercloud Energy (flash sync with thunder). The weather *multipliers* in every profile only need vanilla rain/thunder flags.
- **Fog rendering**: biome fog colors/densities in all 10 biome profiles, and 17 Silver Mirror Mist is designed to pair with its fog. Use Forge `ViewportEvent.ComputeFogColor` and `ViewportEvent.RenderFog` (or fog fields in the biome JSON when the biome file is yours to edit). Underwater Sunken Kingdom should use `water_fog_color`.
- **Secondary spawn**: 02 Raven Spirit Wisps.
- **Ambient particle through vanilla biome JSON**: `effects.particle` supports ONE `SimpleParticleType` per biome with a probability. Use it for the primary effect only; spawn the second with a client tick/handler or `animateTick`.

## 5. Atmosphere profiles
- `Biome_Profiles/NN_<biome>.json`: day/night intensity, spawn frequency, fog, speed, lifetime, spawn heights/locations, weather variations, lighting, Low/Medium/High presets.
- `Structure_Atmospheres/NN_<structure>.json`: reuse the same 20 effects. Do not create structures.
- Both contain `biomeRegistryName` / `structureRegistryName` = `null`. Fill in after verification (`BIOME_VERIFICATION_CHECKLIST.md`).
- Formula suggestion: `rate = baseRate x presetMul x dayNightMul x weatherMul x mobMul x combatMul`, stop spawning at `maxParticlesAlive`.

## 6. Performance and combat safety
Read `performance_settings.json`. Highlights: Low/Medium/High caps 30/70/120 particles per biome (global cap 150), at most 2/5/8 large-quad (>= 1 block) particles, spawn radius 12/20/28 blocks, mob density multipliers 0.6/0.35/0.2 (more than 3/6/10 hostile mobs within 16 blocks), boss fight x0.25, large effects disabled in combat, fade within 2.5 blocks of the camera, no fullscreen overlays, no shaders. `particle_budget_check.json` shows the estimated steady-state counts of every preset (all within caps).

## 7. Audio
`Audio/assets/x2_living/sounds/ambient/*.ogg` (mono, 32 kHz, 16 s, built as seamless loops) and `Audio/assets/x2_living/sounds.json`. To use: register matching `SoundEvent`s in Java, then reference them from the biome JSON `effects.ambient_sound` (loop) or play them from client code. Merge `sounds.json` into your own if you already have one. Move into `assets/<yourmodid>/` and replace the `x2_living:` prefix if needed.

## 8. Known limitations
- Particles cannot be additive-blended with vanilla render types; glow is faked by fullbright + soft halos in the sprites.
- Mist (128 px) and cloud/ribbon (64 px) sprites are large quads - keep counts low (see caps).
- Vanilla animated-texture `.mcmeta` does not apply to particles; frames are individual PNGs (and also provided as strips).
- No shaders are used or required. Optional shader packs may brighten emissive particles.
