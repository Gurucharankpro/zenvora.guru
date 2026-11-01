# Soulbound Army — reviewed 1.8.1 polish and Singing Glass Desert

Build: **SUCCESS**. Source: `d7e32141e97fb1b56b9ff323a71185bc744ea005`.
Final output SHA256: `bce014dd991d1307ba72ddf2b1568ee2b916677802025855dd0a9252257162f8`.
Minecraft 1.20.1, Forge 47.3.22, Java 17, modid `shadowsovereign`.
Mod version remains `1.8.1-reviewed`; matching client/server protocol is `39-polish-glass-desert`.

This update starts from the latest complete aa4e701d content distribution, repairs
its 63 incorrect LambdaMetafactory bootstrap handles and debug-log minimum, then
applies the new content. The reconstructed repair input SHA is
`f88cac49bae0d10961a277ab2277738892cb5ba4aaaae596fc5b0c17d64edb04`.
The supplied reports describe a b198b947 corrected file, but that binary was not
attached; we do not claim our reconstructed ZIP is byte-identical to it. The
repair changes exactly GloomContent.class and ContentConfig.class, preserving
every other uncompressed entry of the latest complete distribution.

New content:
- Dr. Salmawy and Secret Mansion: Crimson Shadow. Data marker removed from body
  space, dining entrance opened, last basement stair completed; all six supplied
  loot chests retained. No second copy of the optional template is registered.
- Raven Seamstress and Theater of Black Curtains: Moonveil Hollow. All four arms,
  three attached ravens, original UVs and numeric animation tracks are retained.
- Clockwork Widow and Palace of Broken Hours: Moonveil Hollow, underground arena.
  Full 788-cube supplied model and 10 authored clips retained. Empty chests in the
  two new Eclipse habitats receive existing vanilla loot tables.
- 20 Eclipse props: registered, craftable, in the decor tab and conservatively
  placed in matching future habitats. Valid supplied Java exports are used;
  illegal full-scale vanilla model coordinates are not shipped as block models.
  The pack's Pale Broker naming does not replace existing Yuta or invent a mob.
- Singing Glass Desert: appended to Echo Realms' multi-noise source, preserving
  the ten old biome entries. 20 supplied environmental blocks/plants, 4 buildings
  and 4 tree templates, passive Prism Jerboa, glowing crystals, vegetation and
  bounded oases. X2's existing desert sound and two effects are now biome-bound.
  All seven new buildings share the reviewed cross-type structure placement.
- Larger book previews use sampled visible idle-body bounds, excluding hidden FX.
  Combat scale/hitboxes and saved armies are unchanged. Four distinct new egg
  images and 16 bespoke journey badge images, including the new biome visit.
  Fifteen compass placeholders are replaced; the two compass lessons keep their
  meaningful compass icons. Existing 500 lessons and rewards remain byte-identical.

Verification: see [CI log](../polish-update/reports/CI-BUILD.log),
[runtime log](../polish-update/reports/PRODUCTION-SERVER.log),
[native results](../polish-update/reports/PRODUCTION-SMOKE.json),
[result summary](../polish-update/reports/CI-RESULT.json).
Source compilation uses actual Forge/Minecraft APIs. Final obfuscated JAR checks
include Java 17 bootstrap resolution and exact unchanged-entry comparison.
On successful builds, an official isolated Forge server loads the final JAR with
a separate QA mod, checks real block properties/template decodes/entity attributes
and save/reload, locates the new biome, requests generation, flushes and stops.
Graphical client status is recorded separately in CLIENT-SMOKE.json, with actual
Xvfb/Mesa screenshots when it passes. This client uses a ForgeGradle-remapped copy
of the final JAR; the dedicated-server test uses the unmodified production JAR.
No mobile rendering, long combat session or multiplayer test is claimed. Visual quality, performance, structure entrances/rotation across
terrain and complete encounter/recruit/retreat gameplay need in-game review.
Worldgen frequency/terrain figures in the uploaded pack are guidance; the native
implementation uses conservative bounded generation, not a literal new noise
router for every dune or every described formation variant.

## Downloads

- [Complete updated JAR](../downloads/current/soulbound-army-1.20.1-1.8.1-polished.jar?raw=true)
- [All phone volumes, each <=8 MiB](../downloads/current)
- [Exact changed-entry list and fingerprint](../downloads/current/CONTENT-MANIFEST.json)

Download every matching .z01/.z02/.../.zip into one folder, then extract the LAST
.zip with ZArchiver. CRC and extracted JAR SHA are verified against this build.
Replace the previous JAR with this same file on client/server. Do not install the
input under polish-update/base as the new mod. QA mod is never part of delivery.
