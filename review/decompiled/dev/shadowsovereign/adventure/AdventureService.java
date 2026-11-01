/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.OwnableEntity
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.adventure;

import dev.shadowsovereign.adventure.AdventureContent;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.warfront.WarfrontPlans;
import dev.shadowsovereign.warfront.WarfrontService;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class AdventureService {
    private AdventureService() {
    }

    public static long now(MinecraftServer server) {
        return server.m_129783_().m_46467_();
    }

    public static AdventureData.Journal journal(ServerPlayer p) {
        AdventureData data = AdventureData.get(p.f_8924_);
        AdventureData.Journal j = data.journal(p.m_20148_());
        boolean changed = false;
        LinkedHashMap<UUID, ShadowRecord> owned = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows;
        for (ShadowRecord r : owned.values()) {
            changed |= j.discoveries.add(r.type);
        }
        if (j.lore.keySet().removeIf(id -> !owned.containsKey(id))) {
            changed = true;
        }
        if (AdventureService.milestones(j)) {
            changed = true;
        }
        if (changed) {
            data.m_77762_();
        }
        return j;
    }

    private static boolean milestones(AdventureData.Journal j) {
        boolean changed = false;
        if (j.discoveries.size() >= 5) {
            changed |= j.titles.add(AdventureData.Title.COLLECTOR);
            changed |= j.trails.add(AdventureData.Trail.AZURE);
        }
        if (j.discoveries.size() >= 15) {
            changed |= j.titles.add(AdventureData.Title.ARCHIVIST);
            changed |= j.trails.add(AdventureData.Trail.AMETHYST);
        }
        if (j.discoveries.size() >= 30) {
            changed |= j.trails.add(AdventureData.Trail.ECHO);
        }
        return changed;
    }

    public static void dirty(ServerPlayer p) {
        AdventureData.get(p.f_8924_).m_77762_();
    }

    public static void tell(ServerPlayer p, String text) {
        ShadowService.tell(p, text);
    }

    public static boolean nearHub(ServerPlayer p) {
        AdventureData.Journal j = AdventureService.journal(p);
        return j.bound && j.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) && p.m_284548_().m_46805_(j.sanctuary) && p.m_284548_().m_8055_(j.sanctuary).m_60713_((Block)AdventureContent.SANCTUARY.get()) && p.m_20238_(Vec3.m_82512_((Vec3i)j.sanctuary)) <= 64.0;
    }

    public static boolean requireHub(ServerPlayer p) {
        if (AdventureService.nearHub(p)) {
            return true;
        }
        AdventureService.tell(p, "Stand within 8 blocks of your active Soul Sanctuary.");
        return false;
    }

    public static UUID hubOwner(MinecraftServer server, String dimension, BlockPos pos) {
        for (Map.Entry<UUID, AdventureData.Journal> e : AdventureData.get((MinecraftServer)server).owners.entrySet()) {
            if (!e.getValue().bound || !e.getValue().dimension.equals(dimension) || !e.getValue().sanctuary.equals((Object)pos)) continue;
            return e.getKey();
        }
        return null;
    }

    public static boolean bind(ServerPlayer p, BlockPos pos) {
        if (!p.m_284548_().m_46805_(pos) || !p.m_9236_().m_8055_(pos).m_60713_((Block)AdventureContent.SANCTUARY.get()) || p.m_20238_(Vec3.m_82512_((Vec3i)pos)) > 64.0) {
            return false;
        }
        String dim = p.m_9236_().m_46472_().m_135782_().toString();
        UUID existing = AdventureService.hubOwner(p.f_8924_, dim, pos);
        if (existing != null && !existing.equals(p.m_20148_())) {
            AdventureService.tell(p, "This Sanctuary belongs to another player.");
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.session != null) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
        }
        j.bound = true;
        j.dimension = dim;
        j.sanctuary = pos.m_7949_();
        AdventureService.dirty(p);
        AdventureService.tell(p, "Soul Sanctuary bound. Your adventure progress stays with you.");
        return true;
    }

    public static boolean busy(MinecraftServer server, UUID owner, UUID companion) {
        AdventureData.Journal j = AdventureData.get((MinecraftServer)server).owners.get(owner);
        if (j == null) {
            return false;
        }
        long now = AdventureService.now(server);
        return j.expeditions.stream().anyMatch(e -> !e.ready && e.end > now && e.members.contains(companion));
    }

    public static boolean dispatch(ServerPlayer p, int tier, List<UUID> ids) {
        if (!AdventureService.requireHub(p) || tier < 0 || tier > 2) {
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.expeditions.size() >= 2 || ids.isEmpty() || ids.size() > 3 || new HashSet<UUID>(ids).size() != ids.size()) {
            AdventureService.tell(p, "Choose 1\u20133 different reserve companions; at most two expeditions can be listed.");
            return false;
        }
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        int level = (new int[]{1, 4, 7})[tier];
        boolean pathfinder = false;
        for (UUID id : ids) {
            ResourceLocation type;
            ShadowRecord r = army.shadows.get(id);
            ResourceLocation resourceLocation = type = r == null ? null : ResourceLocation.m_135820_((String)r.type);
            if (r == null || type == null || r.activeEntity != null || r.pendingRespawn() || r.level() < level || AdventureService.busy(p.f_8924_, p.m_20148_(), id) || ForgeRegistries.ENTITY_TYPES.getValue(type) == null) {
                AdventureService.tell(p, "Every member must be available in reserve, alive, and level " + level + "+.");
                return false;
            }
            pathfinder |= j.lore((UUID)id).relic == AdventureData.Relic.PATHFINDER;
        }
        AdventureData.Expedition e = new AdventureData.Expedition();
        e.tier = tier;
        e.members.addAll(ids);
        e.start = AdventureService.now(p.f_8924_);
        long duration = (new int[]{3600, 7200, 12000})[tier];
        if (pathfinder) {
            duration = duration * 85L / 100L;
        }
        e.end = e.start + duration;
        j.expeditions.add(e);
        AdventureService.dirty(p);
        AdventureService.tell(p, "Expedition dispatched. It continues while the server runs, even if you log out.");
        return true;
    }

    public static boolean claimExpedition(ServerPlayer p, UUID id) {
        AdventureData.Journal j = AdventureService.journal(p);
        AdventureData.Expedition e = j.expeditions.stream().filter(a -> a.id.equals(id)).findFirst().orElse(null);
        if (e == null || AdventureService.now(p.f_8924_) < e.end) {
            return false;
        }
        j.expeditions.remove(e);
        AdventureService.reward(p, j, e.id, "Expedition completed");
        GuideService.record(p, "expedition_claim");
        return true;
    }

    public static boolean cancelExpedition(ServerPlayer p, UUID id) {
        AdventureData.Journal j = AdventureService.journal(p);
        boolean removed = j.expeditions.removeIf(e -> e.id.equals(id));
        if (removed) {
            AdventureService.dirty(p);
            AdventureService.tell(p, "Expedition canceled. Companions are available in reserve; no reward was awarded.");
        }
        return removed;
    }

    public static void beforeRelease(ServerPlayer p, UUID companion) {
        LegacyService.released(p, companion);
        WarfrontPlans.stop(p);
        WarfrontService.state((ServerPlayer)p).builds.remove(companion);
        AdventureData.Journal j = AdventureService.journal(p);
        j.journey.companions.remove(companion);
        j.expeditions.removeIf(e -> e.members.contains(companion));
        j.lore.remove(companion);
        if (companion.equals(j.bond)) {
            j.bond = null;
        }
        if (j.nemesis != null && companion.equals(j.nemesis.companion)) {
            j.nemesis = null;
        }
        AdventureService.dirty(p);
    }

    public static String offeredTarget(ServerPlayer p) {
        String[] pool = new String[]{"minecraft:zombie", "minecraft:skeleton", "minecraft:spider", "minecraft:husk"};
        return pool[Math.floorMod(p.m_20148_().hashCode() + (int)(AdventureService.now(p.f_8924_) / 24000L), pool.length)];
    }

    public static boolean acceptContract(ServerPlayer p, int kind) {
        if (!AdventureService.requireHub(p) || kind < 0 || kind >= AdventureData.ContractKind.values().length) {
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        AdventureData.ContractKind k = AdventureData.ContractKind.values()[kind];
        long cycle = AdventureService.now(p.f_8924_) / 24000L;
        if (j.contracts.size() >= 3 || j.contracts.stream().anyMatch(c -> c.kind == k) || j.claimedOffers.contains(cycle + ":" + k.name())) {
            AdventureService.tell(p, "That daily offer is already active/claimed, or all three slots are occupied.");
            return false;
        }
        AdventureData.Contract c2 = new AdventureData.Contract();
        c2.kind = k;
        c2.target = AdventureService.offeredTarget(p);
        c2.offerCycle = cycle;
        j.contracts.add(c2);
        AdventureService.dirty(p);
        return true;
    }

    public static boolean claimContract(ServerPlayer p, UUID id) {
        AdventureData.Journal j = AdventureService.journal(p);
        AdventureData.Contract c = j.contracts.stream().filter(a -> a.id.equals(id)).findFirst().orElse(null);
        if (c == null || c.progress < c.goal()) {
            return false;
        }
        j.contracts.remove(c);
        j.claimedOffers.add(c.offerCycle + ":" + c.kind.name());
        while (j.claimedOffers.size() > 96) {
            j.claimedOffers.remove(j.claimedOffers.iterator().next());
        }
        AdventureService.reward(p, j, c.id, "Contract completed");
        return true;
    }

    public static boolean abandonContract(ServerPlayer p, UUID id) {
        AdventureData.Journal j = AdventureService.journal(p);
        boolean removed = j.contracts.removeIf(c -> c.id.equals(id));
        if (removed) {
            AdventureService.dirty(p);
        }
        return removed;
    }

    public static void captured(ServerPlayer p, ShadowRecord r) {
        AdventureData.Journal j = AdventureService.journal(p);
        j.discoveries.add(r.type);
        AdventureService.milestones(j);
        for (AdventureData.Contract c : j.contracts) {
            if (c.kind != AdventureData.ContractKind.DIVERSITY || c.progress >= c.goal() || !c.seen.add(r.type)) continue;
            c.progress = Math.min(c.goal(), c.seen.size());
        }
        AdventureService.dirty(p);
    }

    public static UUID damageOwner(DamageSource damage) {
        Entity source = damage.m_7639_();
        if (source instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)source;
            return p.m_20148_();
        }
        Entity companion = CompanionProgression.source(damage);
        if (companion != null) {
            return ShadowService.ownerId(companion);
        }
        if (source != null && AdventureWorld.isNpc(source)) {
            return source.getPersistentData().m_128342_("SoulboundAdventureOwner");
        }
        if (source instanceof OwnableEntity) {
            OwnableEntity pet = (OwnableEntity)source;
            return pet.m_21805_();
        }
        return null;
    }

    public static void killed(LivingEntity victim, DamageSource damage) {
        if (victim.m_9236_().f_46443_ || AdventureWorld.isNpc((Entity)victim) || ShadowService.isShadow((Entity)victim) || !(victim instanceof Mob) || victim.getPersistentData().m_128471_("SoulboundAdventureCounted")) {
            return;
        }
        UUID owner = AdventureService.damageOwner(damage);
        if (owner == null) {
            return;
        }
        victim.getPersistentData().m_128379_("SoulboundAdventureCounted", true);
        AdventureData data = AdventureData.get(victim.m_20194_());
        AdventureData.Journal j = data.journal(owner);
        String type = String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey((Object)victim.m_6095_()));
        for (AdventureData.Contract c : j.contracts) {
            if (c.kind != AdventureData.ContractKind.HUNT || !(victim instanceof Enemy) || !c.target.equals(type) || c.progress >= c.goal()) continue;
            ++c.progress;
        }
        if (j.nemesis != null && !j.nemesis.ready) {
            AdventureData.Nemesis n = j.nemesis;
            if (!n.kin && victim.m_20148_().equals(n.target)) {
                n.ready = true;
            } else if (n.kin && type.equals(n.type) && ++n.progress >= 3) {
                n.ready = true;
            }
        }
        data.m_77762_();
    }

    public static void fallen(Mob companion, DamageSource damage) {
        if (!ShadowService.isShadow((Entity)companion)) {
            return;
        }
        Entity enemy = damage.m_7639_();
        if (!(enemy instanceof Mob) || !(enemy instanceof Enemy) || ShadowService.isShadow(enemy) || AdventureWorld.isNpc(enemy)) {
            return;
        }
        ShadowRecord r = ShadowService.record((Entity)companion);
        if (r == null) {
            return;
        }
        AdventureData data = AdventureData.get(companion.m_20194_());
        AdventureData.Journal j = data.journal(ShadowService.ownerId((Entity)companion));
        if (j.nemesis != null) {
            return;
        }
        AdventureData.Nemesis n = new AdventureData.Nemesis();
        n.companion = r.id;
        n.target = enemy.m_20148_();
        n.type = String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey((Object)enemy.m_6095_()));
        n.dimension = enemy.m_9236_().m_46472_().m_135782_().toString();
        n.last = enemy.m_20183_();
        j.nemesis = n;
        data.m_77762_();
    }

    public static boolean kin(ServerPlayer p) {
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.nemesis == null || j.nemesis.ready) {
            return false;
        }
        j.nemesis.kin = true;
        AdventureService.dirty(p);
        return true;
    }

    public static boolean claimNemesis(ServerPlayer p) {
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.nemesis == null || !j.nemesis.ready) {
            return false;
        }
        UUID id = j.nemesis.id;
        j.nemesis = null;
        j.titles.add(AdventureData.Title.AVENGER);
        j.trails.add(AdventureData.Trail.AMETHYST);
        AdventureService.reward(p, j, id, "Nemesis defeated");
        return true;
    }

    public static boolean dismissNemesis(ServerPlayer p) {
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.nemesis == null) {
            return false;
        }
        j.nemesis = null;
        AdventureService.dirty(p);
        return true;
    }

    public static boolean startBond(ServerPlayer p, UUID id) {
        ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
        if (r == null) {
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        AdventureData.Lore lore = j.lore(id);
        if (lore.stage == 4) {
            AdventureService.tell(p, "That companion has already completed its bond story.");
            return false;
        }
        j.bond = id;
        if (lore.stage == 0) {
            lore.stage = 1;
            lore.killStart = r.totalKills;
            lore.biomes.clear();
        }
        AdventureService.dirty(p);
        return true;
    }

    public static boolean equipRelic(ServerPlayer p, UUID id, int choice) {
        AdventureData.Journal j = AdventureService.journal(p);
        if (!ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.containsKey(id) || choice < 0 || choice >= AdventureData.Relic.values().length || !j.unlocked(AdventureData.Relic.values()[choice])) {
            return false;
        }
        j.lore((UUID)id).relic = AdventureData.Relic.values()[choice];
        AdventureService.dirty(p);
        return true;
    }

    public static boolean equipTrail(ServerPlayer p, UUID id, int choice) {
        AdventureData.Journal j = AdventureService.journal(p);
        if (!ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.containsKey(id) || choice < 0 || choice >= AdventureData.Trail.values().length || !j.trails.contains((Object)AdventureData.Trail.values()[choice])) {
            return false;
        }
        j.lore((UUID)id).trail = AdventureData.Trail.values()[choice];
        AdventureService.dirty(p);
        return true;
    }

    public static boolean equipTitle(ServerPlayer p, UUID id, int choice) {
        AdventureData.Journal j = AdventureService.journal(p);
        if (!ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.containsKey(id) || choice < 0 || choice >= AdventureData.Title.values().length || !j.titles.contains((Object)AdventureData.Title.values()[choice])) {
            return false;
        }
        j.lore((UUID)id).title = AdventureData.Title.values()[choice];
        ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
        Mob mob = ShadowService.active(p.f_8924_, r);
        if (mob != null) {
            ShadowService.updateName(mob, r);
        }
        AdventureService.dirty(p);
        return true;
    }

    public static String title(Mob mob, ShadowRecord r) {
        AdventureData.Journal j = AdventureData.get((MinecraftServer)mob.m_20194_()).owners.get(ShadowService.ownerId((Entity)mob));
        if (j == null) {
            return "";
        }
        AdventureData.Lore l = j.lore.get(r.id);
        return l == null || l.title == AdventureData.Title.NONE ? "" : " [" + AdventureService.pretty(l.title.name()) + "]";
    }

    public static String pretty(String name) {
        StringBuilder b = new StringBuilder();
        for (String s : name.toLowerCase(Locale.ROOT).split("_")) {
            if (!b.isEmpty()) {
                b.append(' ');
            }
            b.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
        }
        return b.toString();
    }

    public static void reward(ServerPlayer p, AdventureData.Journal j, UUID id, String reason) {
        if (!j.reward(id)) {
            return;
        }
        AdventureService.dirty(p);
        AdventureService.tell(p, reason + "! Adventures completed: " + j.completions + ". Relics unlock at 1 / 3 / 5 completions.");
    }

    public static boolean startHunt(ServerPlayer p) {
        if (!AdventureService.requireHub(p)) {
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        long cycle = AdventureService.now(p.f_8924_) / 24000L;
        if (j.cacheCycle == cycle && !j.caches.isEmpty()) {
            AdventureService.tell(p, j.huntClaimed ? "This server-day's Echo Hunt is already complete." : "Your current Echo Hunt is still active.");
            return false;
        }
        List<BlockPos> positions = AdventureWorld.cacheSpots(p.m_284548_(), j.sanctuary, p.m_20148_(), cycle);
        if (positions.size() != 3) {
            AdventureService.tell(p, "Explore/load more safe terrain around your Sanctuary, then try again. No chunks were forced.");
            return false;
        }
        j.caches.clear();
        for (BlockPos pos : positions) {
            AdventureData.Cache c = new AdventureData.Cache();
            c.pos = pos;
            j.caches.add(c);
        }
        j.cacheCycle = cycle;
        j.cacheDimension = j.dimension;
        j.huntClaimed = false;
        AdventureService.dirty(p);
        return true;
    }

    public static boolean claimCache(ServerPlayer p, UUID id) {
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.cacheCycle != AdventureService.now(p.f_8924_) / 24000L || !j.cacheDimension.equals(p.m_9236_().m_46472_().m_135782_().toString())) {
            return false;
        }
        AdventureData.Cache c = j.caches.stream().filter(a -> a.id.equals(id)).findFirst().orElse(null);
        if (c == null || c.claimed || !p.m_284548_().m_46805_(c.pos) || p.m_20238_(Vec3.m_82512_((Vec3i)c.pos)) > 9.0) {
            return false;
        }
        c.claimed = true;
        AdventureService.dirty(p);
        p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123810_, true, (double)c.pos.m_123341_() + 0.5, (double)c.pos.m_123342_() + 0.7, (double)c.pos.m_123343_() + 0.5, 16, 0.4, 0.5, 0.4, 0.02);
        AdventureService.tell(p, "Echo cache found (" + j.caches.stream().filter(a -> a.claimed).count() + " / 3).");
        return true;
    }

    public static boolean claimHunt(ServerPlayer p) {
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.huntClaimed || j.cacheCycle != AdventureService.now(p.f_8924_) / 24000L || j.caches.size() != 3 || j.caches.stream().anyMatch(c -> !c.claimed)) {
            return false;
        }
        j.huntClaimed = true;
        j.trails.add(AdventureData.Trail.AZURE);
        UUID id = UUID.nameUUIDFromBytes((String.valueOf(p.m_20148_()) + ":echo:" + j.cacheCycle).getBytes(StandardCharsets.UTF_8));
        AdventureService.reward(p, j, id, "Echo Hunt completed");
        return true;
    }

    public static void scan(ServerPlayer p) {
        int range;
        AdventureData.Journal j = AdventureService.journal(p);
        if ((j.caches.isEmpty() || j.cacheCycle != AdventureService.now(p.f_8924_) / 24000L) && !AdventureService.startHunt(p)) {
            return;
        }
        if (!j.cacheDimension.equals(p.m_9236_().m_46472_().m_135782_().toString())) {
            AdventureService.tell(p, "Your caches are in " + j.cacheDimension + ".");
            return;
        }
        AdventureData.Cache c = j.caches.stream().filter(a -> !a.claimed && p.m_284548_().m_46805_(a.pos)).min(Comparator.comparingDouble(a -> p.m_20238_(Vec3.m_82512_((Vec3i)a.pos)))).orElse(null);
        if (c == null) {
            AdventureService.tell(p, "No unclaimed loaded cache nearby. Check your journal.");
            return;
        }
        if (AdventureService.claimCache(p, c.id)) {
            return;
        }
        Vec3 delta = Vec3.m_82512_((Vec3i)c.pos).m_82546_(p.m_20182_());
        String direction = (delta.f_82481_ < 0.0 ? "N" : "S") + (delta.f_82479_ < 0.0 ? "W" : "E");
        AdventureService.tell(p, "Echo signal: " + direction + ", " + (int)delta.m_82553_() + " blocks. Follow the soul sparks when nearby.");
        int n = range = AdventureService.hasRelic(p, AdventureData.Relic.SEEKER, 32) ? 40 : 24;
        if (delta.m_82553_() <= (double)range) {
            for (int i = 1; i <= 12; ++i) {
                Vec3 at = p.m_20182_().m_82520_(0.0, 1.0, 0.0).m_82549_(delta.m_82490_((double)i / 12.0));
                p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123746_, true, at.f_82479_, at.f_82480_, at.f_82481_, 1, 0.03, 0.03, 0.03, 0.0);
            }
        }
    }

    public static boolean hasRelic(ServerPlayer p, AdventureData.Relic relic, int range) {
        AdventureData.Journal j = AdventureService.journal(p);
        for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            Mob mob;
            AdventureData.Lore l = j.lore.get(r.id);
            if (l == null || l.relic != relic || (mob = ShadowService.active(p.f_8924_, r)) == null || !mob.m_6084_() || mob.m_9236_() != p.m_9236_() || !(mob.m_20280_((Entity)p) <= (double)(range * range))) continue;
            return true;
        }
        return false;
    }

    public static void trialWon(ServerPlayer p, AdventureData.Session session) {
        Mob mob;
        AdventureData.Journal j = AdventureService.journal(p);
        j.titles.add(AdventureData.Title.RIFTWALKER);
        if (j.bond == null || !session.participants.contains(j.bond)) {
            return;
        }
        AdventureData.Lore l = j.lore(j.bond);
        ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(j.bond);
        Mob mob2 = mob = r == null ? null : ShadowService.active(p.f_8924_, r);
        if (l.stage == 3 && mob != null && mob.m_6084_() && mob.m_9236_() == p.m_9236_() && mob.m_20238_(Vec3.m_82512_((Vec3i)session.anchor)) <= 1024.0) {
            l.stage = 4;
            l.title = AdventureData.Title.BOUND_VANGUARD;
            l.trail = AdventureData.Trail.ECHO;
            j.titles.add(AdventureData.Title.BOUND_VANGUARD);
            j.trails.add(AdventureData.Trail.ECHO);
            ShadowService.updateName(mob, r);
            AdventureService.tell(p, r.name + " completed its Bond Story: Bound Vanguard!");
        }
        AdventureService.dirty(p);
    }

    public static void tick(MinecraftServer server) {
        long time = AdventureService.now(server);
        if (time % 20L != 0L) {
            return;
        }
        AdventureData data = AdventureData.get(server);
        for (Map.Entry<UUID, AdventureData.Journal> pair : List.copyOf(data.owners.entrySet())) {
            UUID owner = pair.getKey();
            AdventureData.Journal j = pair.getValue();
            for (AdventureData.Expedition e : j.expeditions) {
                if (e.ready || time < e.end) continue;
                e.ready = true;
                data.m_77762_();
            }
            ServerPlayer p = server.m_6846_().m_11259_(owner);
            if (p == null || !p.m_6084_()) continue;
            AdventureWorld.tick(p, j);
            if (j.nemesis != null && !j.nemesis.ready && j.nemesis.target != null) {
                for (ServerLevel level : server.m_129785_()) {
                    Entity target = level.m_8791_(j.nemesis.target);
                    if (target == null) continue;
                    if (j.nemesis.last.equals((Object)target.m_20183_())) break;
                    j.nemesis.last = target.m_20183_();
                    j.nemesis.dimension = level.m_46472_().m_135782_().toString();
                    data.m_77762_();
                    break;
                }
            }
            if (time % 100L != 0L) continue;
            AdventureService.tickProgress(p, j);
        }
    }

    public static void tickProgress(ServerPlayer p, AdventureData.Journal j) {
        MinecraftServer server = p.f_8924_;
        AdventureData data = AdventureData.get(p.f_8924_);
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        boolean nearby = army.shadows.values().stream().map(r -> ShadowService.active(server, r)).anyMatch(m -> m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 1024.0);
        String biome = p.m_9236_().m_204166_(p.m_20183_()).m_203543_().map(k -> k.m_135782_().toString()).orElse("");
        if (nearby && !biome.isEmpty()) {
            for (AdventureData.Contract c : j.contracts) {
                if (c.kind != AdventureData.ContractKind.EXPLORER || c.progress >= c.goal() || !c.seen.add(biome)) continue;
                c.progress = Math.min(c.goal(), c.seen.size());
                data.m_77762_();
            }
        }
        if (j.bond != null) {
            ShadowRecord r2 = army.shadows.get(j.bond);
            if (r2 == null) {
                j.bond = null;
                data.m_77762_();
                return;
            }
            AdventureData.Lore l = j.lore(j.bond);
            if (l.stage == 1 && r2.totalKills - l.killStart >= 5L) {
                l.stage = 2;
                AdventureService.tell(p, r2.name + ": bond stage 2 \u2014 discover three biomes together.");
                data.m_77762_();
            }
            Mob mob = ShadowService.active(server, r2);
            if (l.stage == 2 && mob != null && mob.m_6084_() && mob.m_9236_() == p.m_9236_() && mob.m_20280_((Entity)p) <= 1024.0 && !biome.isEmpty() && l.biomes.add(biome)) {
                if (l.biomes.size() >= 3) {
                    l.stage = 3;
                    AdventureService.tell(p, r2.name + ": final bond stage \u2014 win a Rift Trial together.");
                }
                data.m_77762_();
            }
        }
    }
}

