/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyBosses;
import dev.shadowsovereign.odyssey.OdysseyCamp;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.odyssey.OdysseyGames;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.odyssey.OdysseyScenery;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.odyssey.OdysseyTrials;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public final class OdysseyRuns {
    public static AdventureData.Session session(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && s.kind == AdventureData.Kind.ODYSSEY ? s : null;
    }

    public static CompoundTag data(ServerPlayer p) {
        AdventureData.Session s = OdysseyRuns.session(p);
        return s == null ? new CompoundTag() : s.journey;
    }

    public static OdysseyCatalog.Mode mode(AdventureData.Session s) {
        return OdysseyCatalog.Mode.read(s.journey.m_128451_("Mode"));
    }

    public static boolean start(ServerPlayer p, int type, int difficulty, List<UUID> ids) {
        List<UUID> chosen;
        ListTag saved;
        OdysseyCatalog.Mode mode = OdysseyCatalog.Mode.read(type);
        OdysseyState state = GuideService.state(p);
        if (mode == null || mode.retired() || difficulty < 0 || difficulty > 2 || !OdysseyRealm.camp(p, p.m_20183_()) || AdventureService.journal((ServerPlayer)p).session != null || VigilCrown.active(p) || !p.m_6084_() || p.m_5833_()) {
            return false;
        }
        if (!mode.peaceful() && p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            return false;
        }
        if (mode == OdysseyCatalog.Mode.MASK_KING && (state.camp.m_128451_("CycleBosses") & 0x1F) != 31) {
            return false;
        }
        if (ids.isEmpty() && !(saved = state.camp.m_128437_("Party", 10)).isEmpty()) {
            ids = saved.stream().map(v -> (CompoundTag)v).filter(v -> v.m_128403_("Id")).map(v -> v.m_128342_("Id")).toList();
        }
        List<UUID> list = ids.isEmpty() ? GuideService.army((ServerPlayer)p).shadows.values().stream().filter(r -> OdysseyRuns.SoulLifeServiceQuiet(p, r)).limit(mode.peaceful() ? 8L : 4L).map(r -> r.id).toList() : (chosen = List.copyOf(ids));
        if ((mode == OdysseyCatalog.Mode.THEATRE || mode == OdysseyCatalog.Mode.COUNCIL) && chosen.size() < 3) {
            return false;
        }
        if ((mode == OdysseyCatalog.Mode.PARCEL || mode == OdysseyCatalog.Mode.COMPANIES) && chosen.size() < 2) {
            return false;
        }
        if (chosen.isEmpty() || chosen.size() > (mode.peaceful() ? 8 : 4) || new HashSet<UUID>(chosen).size() != chosen.size() || chosen.stream().anyMatch(id -> {
            ShadowRecord r = GuideService.army((ServerPlayer)p).shadows.get(id);
            return r == null || !OdysseyRuns.SoulLifeServiceQuiet(p, r);
        })) {
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = AdventureData.Kind.ODYSSEY;
        s.phase = AdventureData.Phase.ACTIVE;
        s.dimension = p.m_9236_().m_46472_().m_135782_().toString();
        s.anchor = OdysseyRealm.arena(p, mode.region());
        s.participants.addAll(chosen);
        CompoundTag n = s.journey;
        n.m_128405_("Mode", type);
        n.m_128405_("Difficulty", difficulty);
        n.m_128356_("Seed", state.seed + (long)state.cycle * 7919L + (long)type * 137L + state.count("win." + mode.id()) * 31L);
        n.m_128405_("Duration", mode.game() ? 3600 : (mode.boss() ? 18000 : 24000));
        n.m_128356_("CampReturn", p.m_20183_().m_121878_());
        n.m_128405_("AHP", 20);
        n.m_128405_("BHP", 20);
        n.m_128405_("Rule", 0);
        AdventureService.journal((ServerPlayer)p).session = s;
        AdventureService.dirty(p);
        return true;
    }

    private static boolean SoulLifeServiceQuiet(ServerPlayer p, ShadowRecord r) {
        Mob m = SoulLifeService.nearby(p, r, 32.0);
        return m != null && m.m_5448_() == null && !m.m_20159_() && !m.m_20160_() && (r.lastDamageAt < 0L || GuideService.now(p) - r.lastDamageAt >= 100L);
    }

    public static boolean controlled(ServerPlayer p, ShadowRecord r) {
        AdventureData.Session s = OdysseyRuns.session(p);
        return s != null && s.phase == AdventureData.Phase.ACTIVE && s.journey.m_128471_("Built") && s.participants.contains(r.id) && OdysseyRuns.mode(s) != null && (OdysseyRuns.mode(s).peaceful() || OdysseyRuns.mode(s) == OdysseyCatalog.Mode.COMPANIES && s.npcs.values().stream().noneMatch(n -> n.role == AdventureData.Role.ENEMY)) && !VigilCrown.active(p);
    }

    public static boolean parked(ServerPlayer p, ShadowRecord r) {
        AdventureData.Session s = OdysseyRuns.session(p);
        return s != null && s.phase != AdventureData.Phase.READY && !s.participants.contains(r.id) && r.order == Order.FOLLOW && r.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) && !VigilCrown.member(p, r);
    }

    public static boolean fighting(ServerPlayer p, ShadowRecord r) {
        if (MythicService.fighting(p, r) || LivingRealm.fighting(p, r)) {
            return true;
        }
        AdventureData.Session s = OdysseyRuns.session(p);
        return s != null && s.phase == AdventureData.Phase.ACTIVE && s.journey.m_128471_("Built") && s.participants.contains(r.id) && OdysseyRuns.mode(s) != null && !OdysseyRuns.mode(s).peaceful() && !VigilCrown.active(p);
    }

    public static boolean moving(Mob m, ShadowRecord r, ServerPlayer p) {
        if (!OdysseyRuns.controlled(p, r)) {
            return false;
        }
        if (OdysseyRuns.mode(OdysseyRuns.session(p)) == OdysseyCatalog.Mode.COMPANIES && m.m_5448_() != null) {
            return false;
        }
        m.m_6710_(null);
        m.m_6274_().m_21936_(MemoryModuleType.f_26372_);
        return true;
    }

    public static List<Mob> allies(ServerPlayer p, AdventureData.Session s) {
        ArrayList<Mob> result = new ArrayList<Mob>();
        for (UUID id : s.participants) {
            ShadowRecord r = GuideService.army((ServerPlayer)p).shadows.get(id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m == null || m.m_9236_() != p.m_9236_() || !m.m_6084_()) continue;
            result.add(m);
        }
        return result;
    }

    public static void move(Mob m, Vec3 at, double speed) {
        if (m.m_20238_(at) > 0.8) {
            m.m_21573_().m_26519_(at.f_82479_, at.f_82480_, at.f_82481_, speed);
        } else {
            m.m_21573_().m_26573_();
        }
    }

    public static BlockPos at(AdventureData.Session s, int x, int y, int z) {
        return s.anchor.m_7918_(x, y, z);
    }

    public static AdventureData.Npc add(ServerPlayer p, AdventureData.Session s, EntityType<? extends Mob> type, AdventureData.Role role, BlockPos at, float hp) {
        AdventureData.Npc n = AdventureWorld.template(p, s, type, role);
        n.pos = at;
        ListTag attributes = n.snapshot.m_128437_("Attributes", 10);
        for (Tag row : attributes) {
            CompoundTag attribute = (CompoundTag)row;
            if (!attribute.m_128461_("Name").equals("minecraft:generic.max_health")) continue;
            attribute.m_128347_("Base", (double)hp);
        }
        n.snapshot.m_128350_("Health", hp);
        n.snapshot.m_128379_("PersistenceRequired", true);
        s.npcs.put(n.id, n);
        AdventureWorld.spawn(p, s, n);
        return n;
    }

    public static void token(ServerPlayer p, AdventureData.Session s, BlockPos at, int index) {
        ItemStack item = new ItemStack((ItemLike)OdysseyContent.TOKEN.get());
        item.m_41784_().m_128362_("OdysseySession", s.id);
        item.m_41784_().m_128405_("Token", index);
        ItemEntity entity = new ItemEntity(p.m_9236_(), (double)at.m_123341_() + 0.5, (double)at.m_123342_() + 0.5, (double)at.m_123343_() + 0.5, item);
        entity.m_266426_(p.m_20148_());
        entity.m_32010_(0);
        entity.getPersistentData().m_128362_("OdysseyScene", s.id);
        p.m_284548_().m_7967_((Entity)entity);
        OdysseyRuns.prop(s, (Entity)entity);
    }

    public static void prop(AdventureData.Session s, Entity entity) {
        ListTag list = s.journey.m_128437_("Props", 10);
        CompoundTag row = new CompoundTag();
        row.m_128362_("Id", entity.m_20148_());
        list.add((Object)row);
        s.journey.m_128365_("Props", (Tag)list);
    }

    public static boolean reserved(ItemEntity item) {
        return item.getPersistentData().m_128403_("OdysseyScene");
    }

    private static void build(ServerPlayer p, AdventureData.Session s) {
        int i;
        CompoundTag n = s.journey;
        int cursor = n.m_128451_("Build");
        int end = Math.min(131072, cursor + 1024);
        int region = OdysseyRuns.mode(s).region();
        Block stone = (new Block[]{Blocks.f_50735_, Blocks.f_152589_, Blocks.f_50472_, Blocks.f_50222_})[region];
        for (i = cursor; i < end; ++i) {
            Block block;
            int x = (i & 0x3F) - 32;
            int y = i >> 12;
            int z = (i >> 6 & 0x3F) - 32;
            BlockPos at = OdysseyRuns.at(s, x, y, z);
            if (!OdysseyRealm.owns(p, at) || !p.m_9236_().m_6857_().m_61937_(at)) {
                OdysseyRuns.finish(p, false);
                return;
            }
            p.m_284548_().m_46745_(at);
            Block block2 = block = y == 0 ? stone : Blocks.f_50016_;
            if (y > 0 && y < 6 && (Math.abs(x) == 31 || Math.abs(z) == 31)) {
                block = y == 3 ? Blocks.f_50210_ : stone;
            }
            p.m_9236_().m_7731_(at, block.m_49966_(), 2);
        }
        n.m_128405_("Build", end);
        if (end < 131072) {
            return;
        }
        n.m_128379_("TerrainReady", true);
        p.m_6021_((double)s.anchor.m_123341_() + 0.5, (double)(s.anchor.m_123342_() + 1), (double)s.anchor.m_123343_() + 24.5);
        i = 0;
        for (UUID id : s.participants) {
            ShadowRecord r = GuideService.army((ServerPlayer)p).shadows.get(id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m == null) continue;
            m.m_8127_();
            m.m_21573_().m_26573_();
            m.m_6027_((double)(s.anchor.m_123341_() - 4 + i++ * 2), (double)(s.anchor.m_123342_() + 1), (double)(s.anchor.m_123343_() + 22));
            m.m_6710_(null);
        }
        OdysseyScenery.decorate(p, s, region);
        GuideService.dirty(p);
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        if (OdysseyRuns.mode(s) == null || OdysseyRuns.mode(s).retired() || !OdysseyRealm.inside(p) || !p.m_6084_()) {
            OdysseyRuns.finish(p, false);
            return;
        }
        CompoundTag n = s.journey;
        if (VigilCrown.active(p)) {
            OdysseyRuns.pause(p);
            return;
        }
        if (!n.m_128471_("TerrainReady")) {
            OdysseyRuns.build(p, s);
            return;
        }
        if (!n.m_128471_("Built")) {
            if (!p.m_284548_().m_143319_(new ChunkPos(s.anchor).m_45588_())) {
                return;
            }
            n.m_128379_("Built", true);
            OdysseyState state = GuideService.state(p);
            state.observe("regions_mask", state.count("regions_mask") | 1L << OdysseyRuns.mode(s).region());
            if (OdysseyRuns.mode(s).boss()) {
                OdysseyBosses.setup(p, s);
            } else if (OdysseyRuns.mode(s).game()) {
                OdysseyGames.setup(p, s);
            } else {
                OdysseyTrials.setup(p, s);
            }
            GuideService.dirty(p);
            return;
        }
        if (p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) > 6400.0) {
            OdysseyRuns.pause(p);
            return;
        }
        if (s.phase != AdventureData.Phase.ACTIVE) {
            return;
        }
        int elapsed = n.m_128451_("Elapsed") + 1;
        n.m_128405_("Elapsed", elapsed);
        n.m_128356_("Clock", GuideService.now(p));
        if (elapsed >= n.m_128451_("Duration")) {
            OdysseyRuns.finish(p, false);
            OdysseyText.tell(p, "time_up", new Object[0]);
            return;
        }
        if (elapsed % 20 == 0) {
            for (AdventureData.Npc npc : s.npcs.values()) {
                if (npc.active == null) {
                    if (AdventureWorld.spawn(p, s, npc)) continue;
                    OdysseyRuns.pause(p);
                    return;
                }
                Entity entity = p.m_284548_().m_8791_(npc.active);
                if (entity instanceof Mob) {
                    Mob actor = (Mob)entity;
                    npc.snapshot = AdventureWorld.snapshot(actor);
                    npc.pos = actor.m_20183_();
                    continue;
                }
                OdysseyRuns.pause(p);
                return;
            }
            if (!OdysseyRuns.mode(s).peaceful() && OdysseyRuns.allies(p, s).isEmpty()) {
                boolean returning = s.participants.stream().map(id -> GuideService.army((ServerPlayer)p).shadows.get(id)).anyMatch(r -> r != null && !r.pendingRespawn());
                if (returning) {
                    OdysseyRuns.pause(p);
                    s.journey.m_128379_("ResumeRequested", true);
                    GuideService.dirty(p);
                } else {
                    OdysseyRuns.finish(p, false);
                    OdysseyText.tell(p, "party_fallen", new Object[0]);
                }
                return;
            }
        }
        if (OdysseyRuns.mode(s).boss()) {
            OdysseyBosses.tick(p, s);
        } else if (OdysseyRuns.mode(s).game()) {
            OdysseyGames.tick(p, s);
        } else {
            OdysseyTrials.tick(p, s);
        }
        if (elapsed % 20 == 0) {
            GuideService.dirty(p);
        }
    }

    public static boolean quick(ServerPlayer p, int nonce) {
        int i;
        int i2;
        AdventureData.Session s = OdysseyRuns.session(p);
        if (s == null || OdysseyRuns.mode(s) == null) {
            return false;
        }
        int action = 0;
        int value = 0;
        OdysseyCatalog.Mode type = OdysseyRuns.mode(s);
        if (type == OdysseyCatalog.Mode.TRAIN && s.journey.m_128471_("Repair")) {
            action = 1;
        }
        if (type == OdysseyCatalog.Mode.LOOP || type == OdysseyCatalog.Mode.ARCHIVIST || type == OdysseyCatalog.Mode.WEAVER || type == OdysseyCatalog.Mode.MASK_KING) {
            int limit = type == OdysseyCatalog.Mode.LOOP ? 3 : 4;
            double best = Double.MAX_VALUE;
            for (i2 = 0; i2 < limit; ++i2) {
                double distance = p.m_20238_(Vec3.m_82512_((Vec3i)OdysseyTrials.marker(s, type == OdysseyCatalog.Mode.LOOP ? i2 : i2 * 2)));
                if (!(distance < best)) continue;
                best = distance;
                value = i2;
            }
        }
        if (type == OdysseyCatalog.Mode.MINE) {
            double best = Double.MAX_VALUE;
            for (i = 0; i < 16; ++i) {
                double distance = p.m_20238_(Vec3.m_82512_((Vec3i)OdysseyTrials.ore(s, i)));
                if (!(distance < best)) continue;
                best = distance;
                value = i;
            }
        }
        if (type == OdysseyCatalog.Mode.MIRRORS) {
            double best = Double.MAX_VALUE;
            for (i = 0; i < 3; ++i) {
                double distance = p.m_20238_(Vec3.m_82512_((Vec3i)OdysseyTrials.mirror(s, i)));
                if (!(distance < best)) continue;
                best = distance;
                value = i;
            }
        }
        if (type == OdysseyCatalog.Mode.CHAIRS) {
            double best = Double.MAX_VALUE;
            ListTag seats = s.journey.m_128437_("Seats", 10);
            for (i2 = 0; i2 < seats.size(); ++i2) {
                Entity entity;
                CompoundTag row = seats.m_128728_(i2);
                Entity entity2 = entity = row.m_128403_("Id") ? p.m_284548_().m_8791_(row.m_128342_("Id")) : null;
                if (entity == null || !(p.m_20280_(entity) < best)) continue;
                best = p.m_20280_(entity);
                value = i2;
            }
        }
        if (type == OdysseyCatalog.Mode.POTATO || type == OdysseyCatalog.Mode.PARCEL) {
            ItemEntity item = OdysseyGames.ball(p, s);
            List<Mob> crew = OdysseyRuns.allies(p, s);
            double best = Double.MAX_VALUE;
            for (int i3 = 0; i3 < crew.size(); ++i3) {
                if (item == null || crew.get(i3) == item.m_20202_() || !(crew.get(i3).m_20280_((Entity)item) < best)) continue;
                value = i3;
                best = crew.get(i3).m_20280_((Entity)item);
            }
        }
        if (type == OdysseyCatalog.Mode.MINE && p.m_20238_(Vec3.m_82512_((Vec3i)OdysseyTrials.marker(s, 5))) <= 16.0) {
            action = 1;
        }
        return OdysseyRuns.input(p, action, value, nonce);
    }

    public static boolean input(ServerPlayer p, int action, int value, int nonce) {
        boolean accepted;
        AdventureData.Session s = OdysseyRuns.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE || !s.journey.m_128471_("Built") || nonce != s.journey.m_128451_("Turn") || p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) > 4096.0) {
            return false;
        }
        boolean bl = OdysseyRuns.mode(s).boss() ? OdysseyBosses.input(p, s, action, value) : (accepted = OdysseyRuns.mode(s).game() ? OdysseyGames.input(p, s, action, value) : OdysseyTrials.input(p, s, action, value));
        if (accepted) {
            s.journey.m_128405_("Turn", s.journey.m_128451_("Turn") + 1);
            GuideService.dirty(p);
        }
        return accepted;
    }

    public static void win(ServerPlayer p, AdventureData.Session s) {
        if (s.phase != AdventureData.Phase.ACTIVE || s.journey.m_128471_("Won")) {
            return;
        }
        s.journey.m_128379_("Won", true);
        s.phase = AdventureData.Phase.READY;
        AdventureWorld.clearEntities(p.f_8924_, s);
        GuideService.dirty(p);
        OdysseyText.tell(p, "run_won", new Object[0]);
    }

    private static void restoreSource(ServerPlayer p, AdventureData.Session s) {
        Entity entity;
        CompoundTag n = s.journey;
        if (!n.m_128441_("SourceItem")) {
            return;
        }
        if (!n.m_128471_("SourceSpawned")) {
            OdysseyCamp.deliver(p, ItemStack.m_41712_((CompoundTag)n.m_128469_("SourceItem")));
            n.m_128473_("SourceItem");
            return;
        }
        if (n.m_128403_("Ball") && (entity = p.m_284548_().m_8791_(n.m_128342_("Ball"))) instanceof ItemEntity) {
            ItemEntity item = (ItemEntity)entity;
            item.m_8127_();
            item.m_32010_(0);
            item.m_32064_();
            item.getPersistentData().m_128473_("OdysseyScene");
            item.m_6021_(p.m_20185_(), p.m_20186_() + 0.3, p.m_20189_());
            item.m_6123_((Player)p);
        }
    }

    public static boolean finish(ServerPlayer p, boolean claim) {
        AdventureData.Session s = OdysseyRuns.session(p);
        if (s == null || claim && s.phase != AdventureData.Phase.READY) {
            return false;
        }
        CompoundTag n = s.journey;
        OdysseyCatalog.Mode mode = OdysseyRuns.mode(s);
        OdysseyState state = GuideService.state(p);
        if (claim && mode != null && state.grant("run:" + String.valueOf(s.id))) {
            int gold;
            String rewardClock;
            GuideService.record(p, "win." + mode.id());
            if (mode.boss()) {
                GuideService.record(p, "boss." + mode.id());
                state.camp.m_128405_("CycleBosses", state.camp.m_128451_("CycleBosses") | 1 << mode.ordinal() - 10);
            }
            if (!state.camp.m_128441_(rewardClock = "SealsAt." + mode.id()) || GuideService.now(p) - state.camp.m_128454_(rewardClock) >= 1200L) {
                state.seals = Math.min(1000000, state.seals + (mode.boss() ? 5 : 2));
                state.camp.m_128356_(rewardClock, GuideService.now(p));
            }
            if (mode == OdysseyCatalog.Mode.PARADE && state.camp.m_128471_("CoronationPending")) {
                state.ended = true;
                state.camp.m_128379_("CoronationPending", false);
                GuideService.record(p, "ending");
            }
            int time = n.m_128451_("Elapsed");
            int n2 = mode == OdysseyCatalog.Mode.LOOP ? 3000 : (mode == OdysseyCatalog.Mode.BAZAAR ? 1400 : (gold = mode == OdysseyCatalog.Mode.TRAIN ? 2400 : 1200));
            int medal = mode == OdysseyCatalog.Mode.BAZAAR ? Math.min(3, n.m_128451_("Purchases")) : (time <= gold ? 3 : (time <= gold * 2 ? 2 : 1));
            state.observe("medal." + mode.id(), medal);
            if (state.grant("first:" + mode.id())) {
                OdysseyCamp.earnArtifact(p, Math.floorMod(mode.ordinal(), 24));
            }
            for (UUID id : s.participants) {
                ShadowRecord r = GuideService.army((ServerPlayer)p).shadows.get(id);
                if (r == null) continue;
                r.odyssey.wins |= 1 << mode.ordinal();
                ArmyData.get(p.f_8924_).m_77762_();
                SoulLifeService.affection(p, r, 2);
                if (r.odyssey.club < 0 || s.participants.stream().map(other -> GuideService.army((ServerPlayer)p).shadows.get(other)).filter(other -> other != null && other.odyssey.club == r.odyssey.club).count() < 2L) continue;
                r.odyssey.clubTrips = Math.min(1000000000, r.odyssey.clubTrips + 1);
                r.odyssey.clubRank = Math.min(3, r.odyssey.clubTrips / 3);
                if (r.odyssey.clubRank > 0) {
                    r.odyssey.gestures |= 1 << r.odyssey.club;
                }
                ArmyData.get(p.f_8924_).m_77762_();
            }
        }
        OdysseyBosses.clearBar(s.id);
        OdysseyRuns.thawProps(p, s);
        for (int slot = 0; slot < p.m_150109_().m_6643_(); ++slot) {
            ItemStack token = p.m_150109_().m_8020_(slot);
            if (!token.m_150930_((Item)OdysseyContent.TOKEN.get()) || !token.m_41782_() || !token.m_41783_().m_128403_("OdysseySession") || !s.id.equals(token.m_41783_().m_128342_("OdysseySession"))) continue;
            p.m_150109_().m_6836_(slot, ItemStack.f_41583_);
        }
        OdysseyRuns.restoreSource(p, s);
        for (Tag row : n.m_128437_("Props", 10)) {
            ItemEntity item;
            CompoundTag t = (CompoundTag)row;
            if (!t.m_128403_("Id")) continue;
            Entity entity = p.m_284548_().m_8791_(t.m_128342_("Id"));
            if (entity != null && !(entity instanceof ItemEntity)) {
                for (Entity rider : List.copyOf(entity.m_20197_())) {
                    rider.m_8127_();
                }
                entity.m_146870_();
                continue;
            }
            if (!(entity instanceof ItemEntity) || (item = (ItemEntity)entity).m_32055_().m_150930_((Item)OdysseyContent.BALL.get()) || item.m_32055_().m_150930_((Item)OdysseyContent.PARCEL.get())) continue;
            item.m_146870_();
        }
        for (ShadowRecord r : GuideService.army((ServerPlayer)p).shadows.values()) {
            Mob m;
            if (!s.participants.contains(r.id) || (m = ShadowService.active(p.f_8924_, r)) == null) continue;
            m.getPersistentData().m_128473_("OdysseyOut");
            m.getPersistentData().m_128473_("OdysseyKickAt");
            m.m_8127_();
            m.m_21573_().m_26573_();
            m.m_6710_(null);
        }
        AdventureWorld.clearEntities(p.f_8924_, s);
        AdventureService.journal((ServerPlayer)p).session = null;
        GuideService.dirty(p);
        if (OdysseyRealm.inside(p) && GuideService.state((ServerPlayer)p).plotReady) {
            p.m_6021_((double)OdysseyRealm.home(p).m_123341_() + 0.5, 81.0, (double)OdysseyRealm.home(p).m_123343_() + 0.5);
            for (UUID id : s.participants) {
                Vec3 at;
                ShadowRecord r = GuideService.army((ServerPlayer)p).shadows.get(id);
                Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
                if (m == null || (at = ShadowService.safePosition(m, p.m_20182_(), p.m_284548_())) == null) continue;
                m.m_6021_(at.f_82479_, at.f_82480_, at.f_82481_);
            }
        }
        return true;
    }

    public static boolean pause(ServerPlayer p) {
        Object object;
        AdventureData.Session s = OdysseyRuns.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE) {
            return false;
        }
        CompoundTag n = s.journey;
        OdysseyRuns.freezeProps(p, s);
        p.m_8127_();
        if (n.m_128403_("Ball") && (object = p.m_284548_().m_8791_(n.m_128342_("Ball"))) instanceof ItemEntity) {
            ItemEntity item = (ItemEntity)object;
            item.m_8127_();
        }
        ListTag poses = new ListTag();
        for (UUID id : s.participants) {
            ShadowRecord r = GuideService.army((ServerPlayer)p).shadows.get(id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m == null) continue;
            CompoundTag pose = new CompoundTag();
            pose.m_128362_("Id", id);
            pose.m_128347_("X", m.m_20185_());
            pose.m_128347_("Y", m.m_20186_());
            pose.m_128347_("Z", m.m_20189_());
            poses.add((Object)pose);
            m.m_8127_();
            m.m_21573_().m_26573_();
        }
        n.m_128365_("CrewPoses", (Tag)poses);
        s.phase = AdventureData.Phase.PAUSED;
        AdventureWorld.clearEntities(p.f_8924_, s);
        GuideService.dirty(p);
        return true;
    }

    public static boolean resume(ServerPlayer p) {
        AdventureData.Session s = OdysseyRuns.session(p);
        if (s == null || OdysseyRuns.mode(s) == null || OdysseyRuns.mode(s).retired() || s.phase != AdventureData.Phase.PAUSED || !OdysseyRealm.inside(p) || !OdysseyRealm.owns(p, s.anchor) || VigilCrown.active(p)) {
            return false;
        }
        if (p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) > 6400.0) {
            if (!OdysseyRealm.camp(p, p.m_20183_())) {
                return false;
            }
            if (s.journey.m_128471_("TerrainReady")) {
                p.m_284548_().m_46745_(s.anchor);
                p.m_6021_((double)s.anchor.m_123341_() + 0.5, (double)(s.anchor.m_123342_() + 1), (double)s.anchor.m_123343_() + 24.5);
                s.journey.m_128379_("ResumeRequested", true);
                GuideService.dirty(p);
                return true;
            }
        }
        if (!p.m_284548_().m_143319_(p.m_146902_().m_45588_())) {
            s.journey.m_128379_("ResumeRequested", true);
            GuideService.dirty(p);
            return true;
        }
        boolean waiting = false;
        for (UUID id : s.participants) {
            ShadowRecord r = GuideService.army((ServerPlayer)p).shadows.get(id);
            if (r == null || r.pendingRespawn()) {
                return false;
            }
            Mob mob = ShadowService.active(p.f_8924_, r);
            if (mob == null && r.activeEntity == null && !ShadowService.summon(p, r, true)) {
                return false;
            }
            mob = ShadowService.active(p.f_8924_, r);
            if (mob != null && mob.m_9236_() == p.m_9236_()) continue;
            waiting = true;
        }
        if (waiting) {
            s.journey.m_128379_("ResumeRequested", true);
            GuideService.dirty(p);
            return true;
        }
        for (Tag row : s.journey.m_128437_("CrewPoses", 10)) {
            ShadowRecord r;
            Mob m;
            CompoundTag pose = (CompoundTag)row;
            if (!pose.m_128403_("Id") || (m = (r = GuideService.army((ServerPlayer)p).shadows.get(pose.m_128342_("Id"))) == null ? null : ShadowService.active(p.f_8924_, r)) == null) continue;
            Vec3 at = new Vec3(pose.m_128459_("X"), pose.m_128459_("Y"), pose.m_128459_("Z"));
            if (!Double.isFinite(at.f_82479_) || !Double.isFinite(at.f_82480_) || !Double.isFinite(at.f_82481_) || !OdysseyRealm.owns(p, BlockPos.m_274446_((Position)at))) continue;
            m.m_20219_(at);
        }
        s.phase = AdventureData.Phase.ACTIVE;
        s.journey.m_128473_("ResumeRequested");
        ++s.epoch;
        OdysseyRuns.thawProps(p, s);
        GuideService.dirty(p);
        return true;
    }

    public static void resumeRequested(ServerPlayer p) {
        AdventureData.Session s = OdysseyRuns.session(p);
        if (s != null && s.phase == AdventureData.Phase.PAUSED && s.journey.m_128471_("ResumeRequested") && (!s.journey.m_128471_("TerrainReady") || p.m_284548_().m_143319_(new ChunkPos(s.anchor).m_45588_())) && GuideService.now(p) % 20L == 0L) {
            OdysseyRuns.resume(p);
        }
    }

    private static void freezeProps(ServerPlayer p, AdventureData.Session s) {
        ListTag frozen = new ListTag();
        for (Tag tag : s.journey.m_128437_("Props", 10)) {
            Mob m;
            Entity entity;
            CompoundTag row = (CompoundTag)tag;
            Entity entity2 = entity = row.m_128403_("Id") ? p.m_284548_().m_8791_(row.m_128342_("Id")) : null;
            if (entity == null) continue;
            CompoundTag pos = new CompoundTag();
            pos.m_128362_("Id", entity.m_20148_());
            pos.m_128347_("X", entity.m_20185_());
            pos.m_128347_("Y", entity.m_20186_());
            pos.m_128347_("Z", entity.m_20189_());
            Vec3 delta = entity.m_20184_();
            pos.m_128347_("DX", delta.f_82479_);
            pos.m_128347_("DY", delta.f_82480_);
            pos.m_128347_("DZ", delta.f_82481_);
            pos.m_128379_("Gravity", entity.m_20068_());
            Entity entity3 = entity.m_20202_();
            if (entity3 instanceof Mob && (m = (Mob)entity3).getPersistentData().m_128403_("ShadowSovereignRecord")) {
                pos.m_128362_("Rider", m.getPersistentData().m_128342_("ShadowSovereignRecord"));
            }
            entity.m_8127_();
            entity.m_20256_(Vec3.f_82478_);
            entity.m_20242_(true);
            frozen.add((Object)pos);
        }
        s.journey.m_128365_("FrozenProps", (Tag)frozen);
    }

    public static void keepFrozen(MinecraftServer server) {
        for (AdventureData.Journal journal : AdventureData.get((MinecraftServer)server).owners.values()) {
            ServerLevel level;
            AdventureData.Session s = journal.session;
            if (s == null || s.kind != AdventureData.Kind.ODYSSEY || s.phase != AdventureData.Phase.PAUSED) continue;
            ResourceLocation key = ResourceLocation.m_135820_((String)s.dimension);
            ServerLevel serverLevel = level = key == null ? null : server.m_129880_(ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)key));
            if (level == null) continue;
            for (Tag tag : s.journey.m_128437_("FrozenProps", 10)) {
                Entity entity;
                CompoundTag pos = (CompoundTag)tag;
                if (!pos.m_128403_("Id") || (entity = level.m_8791_(pos.m_128342_("Id"))) == null) continue;
                entity.m_20256_(Vec3.f_82478_);
                entity.m_20242_(true);
                entity.m_6021_(pos.m_128459_("X"), pos.m_128459_("Y"), pos.m_128459_("Z"));
            }
        }
    }

    private static void thawProps(ServerPlayer p, AdventureData.Session s) {
        for (Tag tag : s.journey.m_128437_("FrozenProps", 10)) {
            ShadowRecord r;
            Mob mob;
            Entity entity;
            CompoundTag pos = (CompoundTag)tag;
            if (!pos.m_128403_("Id") || (entity = p.m_284548_().m_8791_(pos.m_128342_("Id"))) == null) continue;
            entity.m_6021_(pos.m_128459_("X"), pos.m_128459_("Y"), pos.m_128459_("Z"));
            entity.m_20242_(pos.m_128471_("Gravity"));
            entity.m_20334_(pos.m_128459_("DX"), pos.m_128459_("DY"), pos.m_128459_("DZ"));
            if (!pos.m_128403_("Rider") || (mob = (r = GuideService.army((ServerPlayer)p).shadows.get(pos.m_128342_("Rider"))) == null ? null : ShadowService.active(p.f_8924_, r)) == null) continue;
            entity.m_7998_((Entity)mob, true);
            s.journey.m_128362_("Carrier", mob.m_20148_());
        }
        s.journey.m_128473_("FrozenProps");
    }

    public static boolean party(ServerPlayer p, UUID id) {
        if (OdysseyRuns.session(p) != null) {
            return false;
        }
        CompoundTag camp = GuideService.state((ServerPlayer)p).camp;
        ListTag list = camp.m_128437_("Party", 10);
        if (id == null) {
            camp.m_128473_("Party");
            GuideService.dirty(p);
            return true;
        }
        if (!GuideService.army((ServerPlayer)p).shadows.containsKey(id)) {
            return false;
        }
        for (int i = 0; i < list.size(); ++i) {
            if (!list.m_128728_(i).m_128403_("Id") || !list.m_128728_(i).m_128342_("Id").equals(id)) continue;
            list.remove(i);
            camp.m_128365_("Party", (Tag)list);
            GuideService.dirty(p);
            return true;
        }
        if (list.size() >= 8) {
            return false;
        }
        CompoundTag row = new CompoundTag();
        row.m_128362_("Id", id);
        list.add((Object)row);
        camp.m_128365_("Party", (Tag)list);
        GuideService.dirty(p);
        return true;
    }

    public static boolean importChallenge(ServerPlayer p, String code) {
        if (code.length() > 2048) {
            return false;
        }
        String[] parts = code.trim().split(":");
        if (parts.length != 5 || !parts[0].equals("SO115")) {
            return false;
        }
        try {
            int mode = Integer.parseInt(parts[1]);
            int difficulty = Integer.parseInt(parts[3]);
            int rule = Integer.parseInt(parts[4]);
            long seed = Long.parseLong(parts[2]);
            if (OdysseyCatalog.Mode.read(mode) == null || OdysseyCatalog.Mode.read(mode).retired() || difficulty < 0 || difficulty > 2 || rule < 0 || rule > 3) {
                return false;
            }
            CompoundTag c = GuideService.state((ServerPlayer)p).camp;
            c.m_128356_("CustomSeed", seed);
            c.m_128405_("CustomMode", mode);
            c.m_128405_("CustomDifficulty", difficulty);
            c.m_128405_("CustomRule", rule);
            GuideService.dirty(p);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean custom(ServerPlayer p) {
        CompoundTag c = GuideService.state((ServerPlayer)p).camp;
        boolean started = OdysseyRuns.start(p, c.m_128451_("CustomMode"), c.m_128451_("CustomDifficulty"), List.of());
        if (started) {
            CompoundTag n = OdysseyRuns.data(p);
            n.m_128356_("Seed", c.m_128454_("CustomSeed"));
            int rule = c.m_128451_("CustomRule");
            n.m_128405_("Rule", rule);
            if (rule == 1) {
                n.m_128405_("Duration", n.m_128451_("Duration") * 3 / 4);
            }
            if (rule == 2) {
                n.m_128405_("Duration", n.m_128451_("Duration") * 3 / 2);
            }
        }
        return started;
    }
}

