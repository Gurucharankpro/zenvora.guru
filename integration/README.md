# Soulbound Army 1.5.0 — living realms preview

Experimental Minecraft 1.20.1 / Forge 47.3+ binary integration. The original
Forge development project was not supplied. Build/test fixtures are not the
Minecraft engine, and this candidate has not been started in Forge.

This release preserves all 48 profiles from 1.4.0, adds the Eclipse acrobat
The 69 Pose and his Inverted Circus in Moonveil Hollow, and deduplicates the
repeated Soul Devourer decor archive by its exact SHA-256. The name refers to
an armoured acrobat's handstand/orbiting number glyphs. Five combat moves and
all ten supplied animation clips reuse the native creature/army/bestiary system.

Ten additional waystations generate across all ten Echo Realms biomes. Each
contains six different decor designs; collectively they use every one of the
60 designs exactly once, alongside existing display ruins and placements.
Regional stone palettes, supported wall/ceiling/floor props, roof lights and
supply chests make them useful exploration stops. They are deliberately spacious
static structures, not populated NPC towns. Sunken Kingdom uses the ocean-floor
heightmap. New landmarks require unexplored chunks.

Fifteen native advancements add a localized tutorial path: entering Echo Realms,
ten biome visits, three decor-collection goals and visiting all ten biomes.
Rewards are one-time vanilla advancement XP plus bread, torches and emeralds;
the first Sunken Kingdom visit also grants five minutes of water breathing.
Rewards contain no revoke/schedule loop. The tree appears in the vanilla
advancements screen. Inventory collection goals require an inventory change.
These supplement the existing quest system rather than replacing its UI.

Totals: 49 creature profiles plus the separate Soul Devourer; 55 egg designs;
100 added blocks/items including 60 static decor props; 34 structure/tree sets;
15 new advancements. Decor remains one anchor block per item with oversized
visual models, without functional seats/storage or physical multiblock behavior.
The new egg uses separate generated RGBA artwork; older atlas cells are stable.
Template entities are cleared; chest loot is retained and NBT targets 3465.

Legacy legendary encounter freedom, selective Villager Knight removal, aquatic
routing and pinned old worldgen placement salts remain. Client/server both need
the new JAR, protocol 34-living-realms. Both player and server must replace the
old mod; do not install two versions simultaneously.

Build with JDK 21 tooling, Python 3.12+, curl, the original JAR and fifteen
unique extracted source packs (duplicate archives count once):

    bash integration/build.sh ORIGINAL.jar INPUTS_DIR BUILD_DIR

The exact original SHA-256 is enforced by PatchMod.java. Dependencies are pinned.
Java bytecode targets 17 using -source/-target; the trimmed JDK has no ct.sym,
so real Java 17 runtime/API compatibility remains unverified. Only production
helpers are shipped, never fixture API classes.

The checks execute actual catalogue/animation code, inspect bytecode references,
exercise encounter/migration logic in simulated worlds, verify resources/NBT,
regional decor coverage, tutorial reward references and previous release assets.
The previous 1.4.0 candidate is compared when available in BUILD_DIR/dist.

Official Forge downloads remain excluded by the enforced environment network
policy (earlier attempts returned HTTP 403). Outstanding live checks: client and
server startup, worldgen/advancement codec acceptance, actual structure terrain
placement/lighting, rendering, collisions, combat, old-world migration and restart.
Use a disposable world or backup for candidate testing. Passing local tests does
not certify a bug-free game or completion of a full visual/gameplay overhaul.
