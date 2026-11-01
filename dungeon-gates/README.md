# Dungeon Gates — first chapter (0.1.0)
Independent Minecraft 1.20.1 / Forge47.3.22 / Java17 mod, with GeckoLib4.4.9.
This delivery contains the first TWO uploaded gates only. P1-3, P1-4, P1-5 and P2-1
remain pending their actual packages. They are not registered as empty placeholders.

Install both JARs from the ZIP's mods folder in the same Forge client/server mods
folder. No Soulbound Army dependency; no soul capture, recruitment or army systems.
Start with a separate test world. Gates appear in newly generated suitable normal
Overworld terrain; old chunks are not retroactively modified. Every active gate has
four automatic camera-facing level/number/name/category lines, and entry is by touch.

Emerald Trial enters the actual supplied 64x28x64 sanctuary; Frozen Grave enters a
new connected 64x28x64 crypt authored from all 12 Frozen pack blocks (that ZIP had no
structure NBT). Each has two regular creatures, a mini-boss, locked reward crates,
return rifts and a one-time crown trophy. All supplied models and animation clips
are retained. Real damage, projectile collision, guards, phase transitions and
slows are server-owned. Private player instances share one isolated dimension.

Config: config/dungeongates-common.toml. Separate cellSpawnChance and spacingChunks
control overall frequency; two positive weights select active gate types. Changing
spacing affects future chunks only. Health/damage multipliers are configurable.
Commands: /dgates status; /dgates verify_spawns (admin, reads loaded nearby chunks);
/dgates extract (admin self); /dgates rescue <player> (admin);
/dgates repair rebuilds your active incomplete dungeon (admin);
/dgates cleanup removes ONE completed, claimed and inactive instance, without reusing its slot.
Death returns to the stored safe Overworld position. Reentering the same world gate
reuses that player's persisted instance and does not duplicate the trophy.

Build: Java17, then ./gradlew build (Windows: gradlew.bat build).
To regenerate imported resources: pip install nbtlib==2.0.4 Pillow==11.3.0;
python scripts/assets.py. Uploaded generator scripts are references only, never executed.
Focused QA: ./gradlew qaJar reobfQaJar; scripts/smoke.py runs isolated official Forge
servers on two new normal seeds then an actual Xvfb graphical client. QA JAR is not
installed in the delivered mods folder. Exact executed results are in reports/.
A build success alone does not prove gameplay or natural generation.

Read reports/CI-RESULT.json and PRODUCTION-*.json for actual counts, seeds and test
settings; absence/failure of those files is not a successful game test. Graphical
client uses the same commit's native sources; server uses the final obfuscated JAR.
Phone rendering, long-session balance and external multiplayer require further review.
