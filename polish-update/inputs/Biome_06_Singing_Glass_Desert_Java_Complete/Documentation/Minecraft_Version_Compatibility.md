# Compatibility notes
- Target: **Minecraft Java Edition**. Structure NBT `DataVersion` 3955 (1.21.1). Newer 1.21.x loads older templates via DataFixer; **not tested**.
- Resource files are in `assets/<ns>/...` with placeholder namespace `soulbound` (rename to your mod id). Item definitions in `items/` are for 1.21.4+; `models/item` for older.
- Block models use `render_type` (NeoForge/Forge style); on vanilla/Fabric set the render layer in code (cutout / cutout_mipped / translucent).
- Creature: GeckoLib 4.x style files (`geometry.prism_jerboa` format 1.12.0, animations 1.8.0). Bedrock-style JSON is the GeckoLib format; no Bedrock add-on files are included.
- `.bbmodel` files were written by script (Blockbench 4.10 schema). **They have not been opened in Blockbench**; if one fails to load, import `Prism_Jerboa.geo.json` instead (Blockbench -> File -> Import).
- No sounds, no Java, no registration, no dimension.
