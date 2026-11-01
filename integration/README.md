# Soulbound Army 1.7.0 — curated content preview

Latest release: Hanan (profile 49, Crimson Shadow / Cairo house), Glass Ronin
(profile 50, Shattered Sky / temple), 20 Butcher props, 20 distinct Devourer II
variants and 20 biome relic props. Earlier Devourer assets are preserved; the
latest archive differs in geometry, namespaces and textures, not just ZIP layout.
Seven added relic sites/courts place all 60 new props, in their themed biomes.
Biome relic specs are implemented as themed landmark structures rather than
free-scatter features or insertions into every originally suggested room.
Total: 51 profiles plus separate Devourer, 57 eggs, 160 added blocks including
120 decor designs, 43 structure/tree sets. All 500 learning lessons and 15 journey
goals remain unchanged. New decor is not appended to those frozen lesson lists.

Four creative tabs reuse existing registry identities: nature -> decor,
creatures -> eggs, workshops -> functional workstations, soulbound_army -> other.
Explicit decor membership overrides names like altar/workbench; nondecor eggs
use native SpawnEggItem recognition; native workshop routing is retained and
Eclipse station blocks and Monolith enchanting table included. Remaining nature/food/material items go to other.
Retired-content filtering remains in effect. No original tab registry is removed.

Glass Ronin alone uses the existing translucent entity RenderType API. Zero scale
FX keys become 0.001 to avoid singular normal scaling. Hanan has no supplied glow
mask; the fallback is transparent. Both eggs use a generated paired RGBA atlas.

Protocol 36-curated-content; version 1.7.0-curated-content-preview. Build script
and review handoff list exact inputs and checks. Runtime Forge startup, UI,
alpha rendering, placement, compatibility and saves remain UNTESTED. No zero-bug
claim is made. See LATEST_INPUTS.json and the delivery AI-REVIEW.md.

## Earlier features and build context

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
the new JAR, protocol 35-learning-paths. Both player and server must replace the
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

## 500 additional learning goals

learning_quests.py adds exactly 500 new native advancements under a separate
learning namespace: 300 block discovery/building/reserve lessons, 60 regional
decoration tasks, 40 biome preparation goals and 100 recipe/equipment lessons.
Each has an English/Arabic explanation and one-time XP. Ten stage milestones
also use the supply-chest loot table. Rare decor reserves require two items;
other building reserves require eight. Toast/chat announcements are suppressed
to avoid a flood of messages. Multiple tabs organize the lessons.

The old story planner catalogue has 60 entries; an alleged old total of 500 was
not established across all game systems. Existing goals and advancements are
preserved; no claim of an exact old/new combined 1000 is made. New lessons use
the vanilla advancement screen, not new entries in QuestPlannerScreen. Parent
links organize display; they do not prevent early completion. Inventory goals
need an inventory change, and recipe goals unlock via the vanilla recipe book.
This is practical block/decor/preparation teaching, not 500 new scripted story
scenarios or a rewrite of every combat/army tutorial.
