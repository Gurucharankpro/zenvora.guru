/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.SpawnPlacements
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.util.BlockSnapshot
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.EntityTeleportEvent$EnderEntity
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 */
package dev.shadowsovereign.realm;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyCamp;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.RealmContent;
import dev.shadowsovereign.realm.RealmEvents;
import dev.shadowsovereign.realm.RealmState;
import dev.shadowsovereign.realm.RealmTerrain;
import dev.shadowsovereign.realm.WildBosses;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.level.BlockEvent;

public final class LivingRealm {
    public static RealmState state(ServerPlayer p) {
        return AdventureService.journal((ServerPlayer)p).livingRealm;
    }

    public static void dirty(ServerPlayer p) {
        AdventureService.dirty(p);
    }

    public static Component text(String key, Object ... args) {
        return Component.m_237110_((String)("realm.shadowsovereign." + key), (Object[])args);
    }

    public static void tell(ServerPlayer p, String key, Object ... args) {
        p.m_5661_(LivingRealm.text(key, args), false);
    }

    public static AdventureData.Session session(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && s.kind == AdventureData.Kind.LIVING_REALM ? s : null;
    }

    public static List<Mob> crew(ServerPlayer p) {
        ArrayList<Mob> result = new ArrayList<Mob>();
        for (ShadowRecord r : GuideService.army((ServerPlayer)p).shadows.values()) {
            Mob m = ShadowService.active(p.f_8924_, r);
            if (m == null || !m.m_6084_() || m.m_9236_() != p.m_9236_() || !(m.m_20280_((Entity)p) <= 1024.0)) continue;
            result.add(m);
        }
        return result;
    }

    public static boolean available(ServerPlayer p) {
        return p.m_6084_() && !p.m_5833_() && OdysseyRealm.inside(p) && LivingRealm.state((ServerPlayer)p).ready && !VigilCrown.active(p);
    }

    public static boolean close(ServerPlayer p, BlockPos at, double range) {
        return p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= range * range;
    }

    public static boolean discover(ServerPlayer p, int r) {
        if (!(LivingRealm.available(p) && r >= 0 && r <= 3 && LivingRealm.close(p, RealmTerrain.stone(p, r), 6.0) && p.m_9236_().m_8055_(RealmTerrain.stone(p, r)).m_60713_(Blocks.f_50729_))) {
            return false;
        }
        LivingRealm.state((ServerPlayer)p).waystones |= 1 << r;
        LivingRealm.dirty(p);
        return true;
    }

    public static boolean travel(ServerPlayer p, int r) {
        EntityTeleportEvent.EnderEntity event;
        block9: {
            block8: {
                if (!LivingRealm.available(p) || r < 0 || r > 3 || (LivingRealm.state((ServerPlayer)p).waystones & 1 << r) == 0 || AdventureService.journal((ServerPlayer)p).session != null || EclipseService.state((ServerPlayer)p).energy < 5.0f) {
                    return false;
                }
                boolean origin = OdysseyRealm.camp(p, p.m_20183_());
                for (int i = 0; i < 4; ++i) {
                    origin |= (LivingRealm.state((ServerPlayer)p).waystones & 1 << i) != 0 && LivingRealm.close(p, RealmTerrain.stone(p, i), 6.0);
                }
                if (!origin) {
                    return false;
                }
                BlockPos at = RealmTerrain.stone(p, r).m_7918_(0, 0, 3);
                p.m_284548_().m_46745_(at);
                if (!RealmTerrain.passable(p, at)) {
                    return false;
                }
                event = ForgeEventFactory.onEnderTeleport((LivingEntity)p, (double)((double)at.m_123341_() + 0.5), (double)at.m_123342_(), (double)((double)at.m_123343_() + 0.5));
                if (event.isCanceled() || !Double.isFinite(event.getTargetX()) || !Double.isFinite(event.getTargetY()) || !Double.isFinite(event.getTargetZ())) break block8;
                Vec3 vec3 = new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                if (!(vec3.m_82557_(Vec3.m_82539_((Vec3i)at)) > 0.01)) break block9;
            }
            return false;
        }
        List<Mob> travelers = LivingRealm.crew(p).stream().filter(m -> {
            ShadowRecord rec = ShadowService.record((Entity)m);
            return rec != null && rec.order == Order.FOLLOW && !rec.vigil.post;
        }).toList();
        p.f_8906_.m_9774_(event.getTargetX(), event.getTargetY(), event.getTargetZ(), p.m_146908_(), p.m_146909_());
        for (Mob m2 : travelers) {
            EntityTeleportEvent.EnderEntity move;
            Vec3 safe = ShadowService.safePosition(m2, p.m_20182_(), p.m_284548_());
            if (safe == null || (move = ForgeEventFactory.onEnderTeleport((LivingEntity)m2, (double)safe.f_82479_, (double)safe.f_82480_, (double)safe.f_82481_)).isCanceled() || !Double.isFinite(move.getTargetX()) || !Double.isFinite(move.getTargetY()) || !Double.isFinite(move.getTargetZ())) continue;
            Vec3 vec3 = new Vec3(move.getTargetX(), move.getTargetY(), move.getTargetZ());
            if (!(vec3.m_82557_(safe) < 0.01)) continue;
            m2.m_6021_(move.getTargetX(), move.getTargetY(), move.getTargetZ());
        }
        EclipseService.state((ServerPlayer)p).energy -= 5.0f;
        EclipseService.dirty(p);
        return true;
    }

    public static boolean pin(ServerPlayer p, int index) {
        if (index < -1 || index > 23) {
            return false;
        }
        LivingRealm.state((ServerPlayer)p).pinned = index;
        LivingRealm.dirty(p);
        return true;
    }

    public static boolean harvest(ServerPlayer p, int region, int slot) {
        if (!LivingRealm.available(p) || LivingRealm.session(p) != null || region < 0 || region > 3 || slot < 0 || slot > 3) {
            return false;
        }
        BlockPos at = RealmTerrain.node(p, region, slot);
        String key = "Node." + region + "." + slot;
        if (LivingRealm.state((ServerPlayer)p).sites.m_128471_(key) || !LivingRealm.close(p, at, 5.0)) {
            return false;
        }
        Block expected = (new Block[]{Blocks.f_152490_, Blocks.f_152599_, Blocks.f_50377_, Blocks.f_50706_})[region];
        if (!p.m_9236_().m_8055_(at).m_60713_(expected) || !p.m_21205_().m_41735_(expected.m_49966_())) {
            return false;
        }
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(p.m_9236_(), at, p.m_9236_().m_8055_(at), (Player)p);
        if (RealmEvents.veto(event) || !p.m_9236_().m_7471_(at, false)) {
            return false;
        }
        LivingRealm.state((ServerPlayer)p).sites.m_128379_(key, true);
        LivingRealm.state((ServerPlayer)p).sites.m_128405_("HarvestCount", LivingRealm.state((ServerPlayer)p).sites.m_128451_("HarvestCount") + 1);
        p.m_21205_().m_41622_(1, (LivingEntity)p, user -> user.m_21190_(InteractionHand.MAIN_HAND));
        OdysseyCamp.deliver(p, new ItemStack((ItemLike)RealmContent.MATERIALS.get(region).get(), 4));
        if (LivingRealm.state((ServerPlayer)p).contract == 3) {
            LivingRealm.state((ServerPlayer)p).contractProgress += 4;
        }
        LivingRealm.dirty(p);
        return true;
    }

    public static boolean project(ServerPlayer p, int region) {
        if (!LivingRealm.available(p) || LivingRealm.session(p) != null || region < 0 || region > 3 || !LivingRealm.close(p, RealmTerrain.project(p, region), 6.0) || (LivingRealm.state((ServerPlayer)p).projects & 1 << region) != 0) {
            return false;
        }
        Item materials = (Item)RealmContent.MATERIALS.get(region).get();
        if (p.m_150109_().m_18947_(materials) < 8 || p.m_150109_().m_18947_(Items.f_42018_) < 16) {
            return false;
        }
        BlockPos center = RealmTerrain.project(p, region);
        ArrayList<BlockPos> cells = new ArrayList<BlockPos>();
        for (int x = -2; x <= 2; ++x) {
            for (int z = -2; z <= 2; ++z) {
                if (Math.abs(x) != 2 && Math.abs(z) != 2) continue;
                BlockPos at = center.m_7918_(x, 0, z);
                if (!p.m_9236_().m_8055_(at).m_60795_() || !p.m_9236_().m_45933_(null, new AABB(at)).isEmpty()) {
                    return false;
                }
                cells.add(at);
            }
        }
        ArrayList<BlockSnapshot> snapshot = new ArrayList<BlockSnapshot>();
        for (BlockPos at : cells) {
            snapshot.add(BlockSnapshot.create((ResourceKey)p.m_9236_().m_46472_(), (LevelAccessor)p.m_9236_(), (BlockPos)at));
        }
        int placed = 0;
        for (BlockPos at : cells) {
            if (!RealmTerrain.safePut(p.m_284548_(), at, (new Block[]{Blocks.f_50223_, Blocks.f_152510_, Blocks.f_50078_, Blocks.f_50735_})[region].m_49966_())) continue;
            ++placed;
        }
        if (placed != 16) {
            for (BlockSnapshot before : snapshot) {
                before.restore(true, false);
            }
            return false;
        }
        for (BlockSnapshot before : snapshot) {
            if (!RealmEvents.placeVeto(p, before)) continue;
            for (BlockSnapshot restore : snapshot) {
                restore.restore(true, false);
            }
            return false;
        }
        LivingRealm.consume(p, materials, 8);
        LivingRealm.consume(p, Items.f_42018_, 16);
        LivingRealm.state((ServerPlayer)p).projects |= 1 << region;
        LivingRealm.state((ServerPlayer)p).restored |= 1 << region;
        LivingRealm.dirty(p);
        LivingRealm.tell(p, "project_done", LivingRealm.text("region." + region, new Object[0]));
        return true;
    }

    public static void consume(ServerPlayer p, Item item, int count) {
        for (ItemStack stack : p.m_150109_().f_35974_) {
            if (!stack.m_150930_(item)) continue;
            int take = Math.min(count, stack.m_41613_());
            stack.m_41774_(take);
            if ((count -= take) != 0) continue;
            break;
        }
        p.m_150109_().m_6596_();
    }

    public static boolean trade(ServerPlayer p, int region) {
        if (!LivingRealm.available(p) || LivingRealm.session(p) != null || region < 0 || region > 3 || !LivingRealm.close(p, RealmTerrain.stone(p, region), 8.0) || ((LivingRealm.state((ServerPlayer)p).projects | LivingRealm.state((ServerPlayer)p).sites.m_128451_("EscortsDone")) & 1 << region) == 0 || GuideService.now(p) < LivingRealm.state((ServerPlayer)p).sites.m_128454_("TradeAt")) {
            return false;
        }
        if (p.m_150109_().m_18947_(Items.f_42616_) < 2) {
            return false;
        }
        LivingRealm.consume(p, Items.f_42616_, 2);
        OdysseyCamp.deliver(p, new ItemStack((ItemLike)(new Item[]{Items.f_42410_, Items.f_151052_, Items.f_151049_, Items.f_42416_})[region], 4));
        LivingRealm.state((ServerPlayer)p).sites.m_128356_("TradeAt", GuideService.now(p) + 200L);
        LivingRealm.dirty(p);
        return true;
    }

    public static boolean rope(ServerPlayer p) {
        EntityTeleportEvent.EnderEntity event;
        Mob m;
        block8: {
            block7: {
                if (!LivingRealm.available(p) || LivingRealm.session(p) != null) {
                    return false;
                }
                m = LivingRealm.crew(p).stream().filter(v -> !v.m_20159_() && !v.m_20160_() && p.m_142582_((Entity)v) && v.m_20280_((Entity)p) > 9.0).min(Comparator.comparingDouble(v -> v.m_20280_((Entity)p))).orElse(null);
                if (m == null) {
                    return false;
                }
                ShadowRecord r = ShadowService.record((Entity)m);
                if (r == null || r.vigil.post || r.order != Order.FOLLOW) {
                    return false;
                }
                Vec3 at = ShadowService.safePosition(m, p.m_20182_(), p.m_284548_());
                if (at == null) {
                    return false;
                }
                event = ForgeEventFactory.onEnderTeleport((LivingEntity)m, (double)at.f_82479_, (double)at.f_82480_, (double)at.f_82481_);
                if (event.isCanceled() || !Double.isFinite(event.getTargetX()) || !Double.isFinite(event.getTargetY()) || !Double.isFinite(event.getTargetZ())) break block7;
                Vec3 vec3 = new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                if (!(vec3.m_82557_(p.m_20182_()) > 64.0)) break block8;
            }
            return false;
        }
        m.m_6021_(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        m.f_19789_ = 0.0f;
        m.m_21573_().m_26573_();
        return true;
    }

    public static boolean contract(ServerPlayer p, int kind) {
        RealmState s = LivingRealm.state(p);
        if (!LivingRealm.available(p) || kind < -1 || kind > 4) {
            return false;
        }
        if (kind == -1) {
            s.contract = -1;
            s.contractProgress = 0;
            LivingRealm.dirty(p);
            return true;
        }
        if (kind == 4) {
            int target;
            int n = s.contract == 0 ? 4 : (s.contract == 1 ? 1 : (target = s.contract == 2 ? 1 : 8));
            if (s.contract < 0 || s.contractProgress < target) {
                return false;
            }
            GuideService.state((ServerPlayer)p).seals = Math.min(1000000, GuideService.state((ServerPlayer)p).seals + 3);
            s.contract = -1;
            s.contractProgress = 0;
            ++s.contractCycle;
            LivingRealm.dirty(p);
            return true;
        }
        if (s.contract >= 0) {
            return false;
        }
        if (kind == 0 && s.surveyed.cardinality() > 396) {
            return false;
        }
        if (kind == 1 && s.cleared == 4095 || kind == 2 && s.sites.m_128451_("EscortsDone") == 255 || kind == 3 && s.sites.m_128451_("HarvestCount") >= 16) {
            return false;
        }
        s.contract = kind;
        s.contractProgress = 0;
        s.sites.m_128405_("ContractSurveyStart", s.surveyed.cardinality());
        LivingRealm.dirty(p);
        return true;
    }

    public static boolean start(ServerPlayer p, int kind, int index) {
        BlockPos anchor;
        if (!LivingRealm.available(p) || AdventureService.journal((ServerPlayer)p).session != null || kind < 0 || kind > 3 || index < 0 || index >= (kind == 0 ? 12 : 4) || LivingRealm.crew(p).isEmpty()) {
            return false;
        }
        if (kind == 0 && (LivingRealm.state((ServerPlayer)p).cleared & 1 << index) != 0) {
            return false;
        }
        if (kind == 1 || kind == 2) {
            int bit = index + (kind == 2 ? 4 : 0);
            if ((LivingRealm.state((ServerPlayer)p).sites.m_128451_("EscortsDone") & 1 << bit) != 0) {
                return false;
            }
        }
        if (kind == 3 && (LivingRealm.state((ServerPlayer)p).projects != 15 || LivingRealm.state((ServerPlayer)p).waystones != 15 || Integer.bitCount(LivingRealm.state((ServerPlayer)p).cleared) < 4 || LivingRealm.state((ServerPlayer)p).finale > 0)) {
            return false;
        }
        BlockPos blockPos = kind == 0 ? RealmTerrain.ruin(p, index) : (kind == 3 ? OdysseyRealm.home(p).m_7918_(0, 1, -64) : (anchor = kind == 1 ? RealmTerrain.stone(p, index).m_7918_(12, 0, 0) : RealmTerrain.rescue(p, index)));
        if (!LivingRealm.close(p, anchor, 10.0)) {
            return false;
        }
        if (p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = AdventureData.Kind.LIVING_REALM;
        s.phase = AdventureData.Phase.ACTIVE;
        s.anchor = anchor;
        s.dimension = p.m_9236_().m_46472_().m_135782_().toString();
        s.participants.addAll(LivingRealm.crew(p).stream().limit(8L).map(m -> ShadowService.record((Entity)m).id).toList());
        s.journey.m_128405_("Type", kind);
        s.journey.m_128405_("Index", index);
        s.journey.m_128405_("Turn", 0);
        AdventureService.journal((ServerPlayer)p).session = s;
        if (kind == 0) {
            if (index % 3 == 1) {
                for (int x : new int[]{-4, 4}) {
                    RealmTerrain.safePut(p.m_284548_(), anchor.m_7918_(x, 0, 0), Blocks.f_50327_.m_49966_());
                }
            }
            if (index % 3 == 2) {
                for (int x : new int[]{-4, 0, 4}) {
                    RealmTerrain.safePut(p.m_284548_(), anchor.m_7918_(x, 0, 4), Blocks.f_50225_.m_49966_());
                }
            }
        }
        if (kind == 1 || kind == 2) {
            AdventureData.Npc n = AdventureWorld.template(p, s, (EntityType<? extends Mob>)(kind == 1 ? EntityType.f_20494_ : EntityType.f_217014_), AdventureData.Role.ESCORT);
            n.pos = anchor;
            n.snapshot.m_128379_("NoAI", false);
            s.npcs.put(n.id, n);
        }
        LivingRealm.addWave(p, s, kind == 0 ? index / 3 : (kind == 3 ? 3 : index), kind == 3 ? 4 : 2);
        LivingRealm.dirty(p);
        return true;
    }

    private static void addWave(ServerPlayer p, AdventureData.Session s, int region, int count) {
        for (int i = 0; i < count; ++i) {
            EntityType type = (new EntityType[]{EntityType.f_20501_, EntityType.f_20458_, EntityType.f_20479_, EntityType.f_20497_})[region];
            AdventureData.Npc n = AdventureWorld.template(p, s, (EntityType<? extends Mob>)type, AdventureData.Role.ENEMY);
            n.pos = s.anchor.m_7918_(i * 3 - 4, 0, -5);
            s.npcs.put(n.id, n);
        }
    }

    public static boolean resume(ServerPlayer p) {
        AdventureData.Session s = LivingRealm.session(p);
        if (s == null || s.phase != AdventureData.Phase.PAUSED || !LivingRealm.available(p) || !LivingRealm.close(p, s.anchor, 64.0)) {
            return false;
        }
        if (!p.m_284548_().m_143319_(p.m_146902_().m_45588_())) {
            return false;
        }
        s.phase = AdventureData.Phase.ACTIVE;
        ++s.epoch;
        LivingRealm.dirty(p);
        return true;
    }

    public static boolean finish(ServerPlayer p, boolean claim) {
        AdventureData.Session s = LivingRealm.session(p);
        if (s == null || claim && s.phase != AdventureData.Phase.READY) {
            return false;
        }
        if (claim) {
            int kind = s.journey.m_128451_("Type");
            int index = s.journey.m_128451_("Index");
            if (kind == 0) {
                LivingRealm.state((ServerPlayer)p).cleared |= 1 << index;
                if (LivingRealm.state((ServerPlayer)p).contract == 1) {
                    ++LivingRealm.state((ServerPlayer)p).contractProgress;
                }
                OdysseyCamp.deliver(p, new ItemStack((ItemLike)RealmContent.MATERIALS.get(index / 3).get(), 2));
            } else if (kind == 1 || kind == 2) {
                LivingRealm.state((ServerPlayer)p).sites.m_128405_("EscortsDone", LivingRealm.state((ServerPlayer)p).sites.m_128451_("EscortsDone") | 1 << index + (kind == 2 ? 4 : 0));
                if (LivingRealm.state((ServerPlayer)p).contract == 2) {
                    ++LivingRealm.state((ServerPlayer)p).contractProgress;
                }
                OdysseyCamp.deliver(p, new ItemStack((ItemLike)Items.f_42616_, kind == 1 ? 4 : 2));
            } else {
                LivingRealm.state((ServerPlayer)p).finale = 2;
                AdventureService.journal((ServerPlayer)p).titles.add(AdventureData.Title.KINGDOM_KEEPER);
                GuideService.state((ServerPlayer)p).earned.add("living_realm_crown");
                OdysseyCamp.deliver(p, new ItemStack((ItemLike)Items.f_42735_));
                BlockPos at = OdysseyRealm.home(p).m_7918_(0, 1, -10);
                RealmTerrain.safePut(p.m_284548_(), at, Blocks.f_50282_.m_49966_());
                RealmTerrain.safePut(p.m_284548_(), at.m_7494_(), Blocks.f_50682_.m_49966_());
            }
            GuideService.state((ServerPlayer)p).seals = Math.min(1000000, GuideService.state((ServerPlayer)p).seals + 3);
        }
        AdventureWorld.clearEntities(p.f_8924_, s);
        AdventureService.journal((ServerPlayer)p).session = null;
        LivingRealm.dirty(p);
        return true;
    }

    public static boolean quick(ServerPlayer p) {
        if (!LivingRealm.available(p)) {
            return false;
        }
        AdventureData.Session s = LivingRealm.session(p);
        if (s != null) {
            if (s.phase == AdventureData.Phase.PAUSED) {
                return LivingRealm.resume(p);
            }
            if (s.phase == AdventureData.Phase.READY) {
                return LivingRealm.finish(p, true);
            }
            int kind = s.journey.m_128451_("Type");
            if (kind == 0 && s.journey.m_128451_("Index") % 3 == 2 && s.npcs.values().stream().noneMatch(n -> n.role == AdventureData.Role.ENEMY)) {
                for (int i = 0; i < 3; ++i) {
                    if (!LivingRealm.close(p, s.anchor.m_7918_((i - 1) * 4, 0, 4), 3.0)) continue;
                    s.journey.m_128405_("Switches", s.journey.m_128451_("Switches") | 1 << i);
                    LivingRealm.dirty(p);
                    return true;
                }
            }
            return false;
        }
        for (int i = 0; i < 4; ++i) {
            if (LivingRealm.close(p, RealmTerrain.stone(p, i), 5.0)) {
                return LivingRealm.discover(p, i);
            }
            for (int k = 0; k < 4; ++k) {
                if (!LivingRealm.close(p, RealmTerrain.node(p, i, k), 4.0)) continue;
                return LivingRealm.harvest(p, i, k);
            }
            if (!LivingRealm.close(p, RealmTerrain.project(p, i), 5.0)) continue;
            return LivingRealm.project(p, i);
        }
        return false;
    }

    public static boolean buddy(ServerPlayer p, UUID selected) {
        Mob m;
        AdventureData.Session s = LivingRealm.session(p);
        ShadowRecord r = selected == null ? null : GuideService.army((ServerPlayer)p).shadows.get(selected);
        Mob mob = m = r == null ? null : ShadowService.active(p.f_8924_, r);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE || s.journey.m_128451_("Type") != 0 || s.journey.m_128451_("Index") % 3 != 1 || !s.participants.contains(selected) || m == null || m.m_9236_() != p.m_9236_()) {
            return false;
        }
        s.journey.m_128362_("Buddy", selected);
        LivingRealm.dirty(p);
        return true;
    }

    public static boolean fighting(ServerPlayer p, ShadowRecord r) {
        AdventureData.Session s = LivingRealm.session(p);
        return s != null && s.phase == AdventureData.Phase.ACTIVE && s.participants.contains(r.id) && !VigilCrown.active(p);
    }

    public static boolean controlled(ServerPlayer p, ShadowRecord r) {
        AdventureData.Session s = LivingRealm.session(p);
        return s != null && s.phase == AdventureData.Phase.ACTIVE && s.journey.m_128403_("Buddy") && r.id.equals(s.journey.m_128342_("Buddy")) && s.npcs.values().stream().noneMatch(n -> n.role == AdventureData.Role.ENEMY) && !VigilCrown.active(p);
    }

    public static boolean moving(Mob m, ShadowRecord r, ServerPlayer p) {
        if (!LivingRealm.controlled(p, r)) {
            return false;
        }
        m.m_6710_(null);
        BlockPos at = LivingRealm.session((ServerPlayer)p).anchor.m_7918_(-4, 0, 0);
        m.m_21573_().m_26519_((double)at.m_123341_() + 0.5, (double)at.m_123342_(), (double)at.m_123343_() + 0.5, 1.15);
        return true;
    }

    public static void walk(Mob m, BlockPos goal) {
        double dx = (double)goal.m_123341_() + 0.5 - m.m_20185_();
        double dz = (double)goal.m_123343_() + 0.5 - m.m_20189_();
        double x = m.m_20185_();
        double z = m.m_20189_();
        if (Math.abs(dx) > 2.0) {
            x += Math.copySign(Math.min(8.0, Math.abs(dx)), dx);
        } else {
            z += Math.copySign(Math.min(8.0, Math.abs(dz)), dz);
        }
        m.m_21573_().m_26519_(x, (double)goal.m_123342_(), z, 0.85);
    }

    public static void runTick(ServerPlayer p) {
        AdventureData.Session s = LivingRealm.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE) {
            return;
        }
        if (!LivingRealm.available(p) || !LivingRealm.close(p, s.anchor, 96.0)) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            return;
        }
        if (!p.m_284548_().m_143319_(new ChunkPos(s.anchor).m_45588_())) {
            return;
        }
        int elapsed = s.journey.m_128451_("Elapsed") + 1;
        s.journey.m_128405_("Elapsed", elapsed);
        if (elapsed > 18000) {
            LivingRealm.finish(p, false);
            return;
        }
        for (AdventureData.Npc n2 : s.npcs.values()) {
            if (n2.active == null) {
                if (!AdventureWorld.spawn(p, s, n2)) {
                    AdventureWorld.pause(p.f_8924_, p.m_20148_());
                    return;
                }
                s.journey.m_128356_("SpawnsAt", GuideService.now(p));
                continue;
            }
            Entity entity = p.m_284548_().m_8791_(n2.active);
            if (entity instanceof Mob) {
                Mob m = (Mob)entity;
                if (elapsed % 20 == 0) {
                    n2.snapshot = AdventureWorld.snapshot(m);
                }
                n2.pos = m.m_20183_();
                continue;
            }
            if (GuideService.now(p) - s.journey.m_128454_("SpawnsAt") < 40L) {
                s.journey.m_128405_("Elapsed", elapsed - 1);
                return;
            }
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            return;
        }
        int kind = s.journey.m_128451_("Type");
        int index = s.journey.m_128451_("Index");
        boolean enemies = s.npcs.values().stream().anyMatch(n -> n.role == AdventureData.Role.ENEMY);
        if (kind == 1 || kind == 2) {
            BlockPos goal;
            AdventureData.Npc n3 = s.npcs.values().stream().filter(v -> v.role == AdventureData.Role.ESCORT).findFirst().orElse(null);
            if (n3 == null) {
                LivingRealm.finish(p, false);
                return;
            }
            Entity escort = n3.active == null ? null : p.m_284548_().m_8791_(n3.active);
            BlockPos blockPos = goal = kind == 1 ? RealmTerrain.stone(p, index).m_7918_(0, 0, 18) : RealmTerrain.stone(p, index).m_7918_(0, 0, 4);
            if (escort instanceof Mob) {
                Mob m = (Mob)escort;
                s.anchor = m.m_20183_();
                if (!enemies && p.m_20280_((Entity)m) < 576.0) {
                    LivingRealm.walk(m, goal);
                }
                if (!enemies && m.m_20238_(Vec3.m_82512_((Vec3i)goal)) < 16.0) {
                    s.phase = AdventureData.Phase.READY;
                    AdventureWorld.clearEntities(p.f_8924_, s);
                    LivingRealm.tell(p, "activity_ready", new Object[0]);
                }
            }
        } else if (!enemies) {
            int stage;
            boolean solved = true;
            if (kind == 0 && index % 3 == 1) {
                UUID id = s.journey.m_128403_("Buddy") ? s.journey.m_128342_("Buddy") : null;
                ShadowRecord r = id == null ? null : GuideService.army((ServerPlayer)p).shadows.get(id);
                Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
                boolean held = m != null && m.m_20238_(Vec3.m_82512_((Vec3i)s.anchor.m_7918_(-4, 0, 0))) < 9.0 && LivingRealm.close(p, s.anchor.m_7918_(4, 0, 0), 3.0);
                s.journey.m_128405_("Hold", held ? s.journey.m_128451_("Hold") + 1 : 0);
                boolean bl = solved = s.journey.m_128451_("Hold") >= 60;
            }
            if (kind == 0 && index % 3 == 2) {
                boolean bl = solved = s.journey.m_128451_("Switches") == 7;
            }
            if (kind == 3 && (stage = s.journey.m_128451_("Stage")) < 2) {
                s.journey.m_128405_("Stage", stage + 1);
                LivingRealm.addWave(p, s, stage, 3 + stage);
                solved = false;
            }
            if (solved) {
                if (kind == 0 && index % 3 == 1) {
                    RealmTerrain.openGate(p, index);
                }
                s.phase = AdventureData.Phase.READY;
                AdventureWorld.clearEntities(p.f_8924_, s);
                LivingRealm.tell(p, "activity_ready", new Object[0]);
            }
        }
        if (elapsed % 20 == 0) {
            LivingRealm.dirty(p);
        }
    }

    public static void tick(ServerPlayer p) {
        RealmTerrain.tick(p);
        if (OdysseyRealm.inside(p) && LivingRealm.state((ServerPlayer)p).sites.m_128441_("RestoreAt") && GuideService.now(p) >= LivingRealm.state((ServerPlayer)p).sites.m_128454_("RestoreAt")) {
            BlockPos at = BlockPos.m_122022_((long)LivingRealm.state((ServerPlayer)p).sites.m_128454_("Crumble"));
            if (p.m_284548_().m_46805_(at) && p.m_9236_().m_45933_(null, new AABB(at)).isEmpty()) {
                RealmTerrain.safePut(p.m_284548_(), at, Blocks.f_50472_.m_49966_());
                LivingRealm.state((ServerPlayer)p).sites.m_128473_("RestoreAt");
                LivingRealm.state((ServerPlayer)p).sites.m_128473_("Crumble");
                LivingRealm.dirty(p);
            }
        }
        if (!OdysseyRealm.inside(p) || !LivingRealm.state((ServerPlayer)p).ready) {
            return;
        }
        LivingRealm.runTick(p);
        long now = GuideService.now(p);
        if (now % 20L != 0L) {
            return;
        }
        int sector = RealmTerrain.sector(p);
        if (sector >= 0 && !LivingRealm.state((ServerPlayer)p).surveyed.get(sector)) {
            LivingRealm.state((ServerPlayer)p).surveyed.set(sector);
            if (LivingRealm.state((ServerPlayer)p).contract == 0) {
                LivingRealm.state((ServerPlayer)p).contractProgress = LivingRealm.state((ServerPlayer)p).surveyed.cardinality() - LivingRealm.state((ServerPlayer)p).sites.m_128451_("ContractSurveyStart");
            }
            LivingRealm.react(p, "explore");
            LivingRealm.dirty(p);
        }
        WildBosses.observe(p);
        LivingRealm.ecology(p);
        LivingRealm.hazards(p);
        if (LivingRealm.state((ServerPlayer)p).pinned >= 0 && now % 40L == 0L) {
            BlockPos at = RealmTerrain.landmark(p, LivingRealm.state((ServerPlayer)p).pinned);
            p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123810_, true, (double)at.m_123341_() + 0.5, (double)(at.m_123342_() + 2), (double)at.m_123343_() + 0.5, 3, 0.15, 0.6, 0.15, 0.01);
        }
    }

    private static void ecology(ServerPlayer p) {
        Mob mob;
        Vec3 at;
        ServerLevel world;
        block8: {
            block7: {
                BlockPos center;
                if (!p.m_9236_().m_46469_().m_46207_(GameRules.f_46134_) || LivingRealm.session(p) != null || OdysseyRealm.camp(p, p.m_20183_()) || GuideService.now(p) < LivingRealm.state((ServerPlayer)p).ecologyAt) {
                    return;
                }
                LivingRealm.state((ServerPlayer)p).ecologyAt = GuideService.now(p) + 1200L;
                int r = RealmTerrain.region(p, p.m_20183_());
                if (r < 0) {
                    return;
                }
                world = p.m_284548_();
                long count = world.m_6443_(Mob.class, new AABB(center = OdysseyRealm.arena(p, r)).m_82400_(128.0), m -> m.getPersistentData().m_128471_("RealmWildlife")).size();
                if (count >= 8L) {
                    return;
                }
                EntityType[][] types = new EntityType[][]{{EntityType.f_20555_, EntityType.f_20557_, EntityType.f_20517_}, {EntityType.f_20510_, EntityType.f_20520_}, {EntityType.f_20553_, EntityType.f_20549_}, {EntityType.f_20479_, EntityType.f_20501_}};
                EntityType type = types[r][p.m_217043_().m_188503_(types[r].length)];
                at = AdventureWorld.spot(world, type, p.m_20183_(), 20.0, 40.0, GuideService.now(p));
                if (at == null || OdysseyRealm.camp(p, BlockPos.m_274446_((Position)at))) {
                    return;
                }
                Entity entity = type.m_20615_((Level)world);
                if (!(entity instanceof Mob)) break block7;
                mob = (Mob)entity;
                if (SpawnPlacements.m_217074_((EntityType)type, (ServerLevelAccessor)world, (MobSpawnType)MobSpawnType.NATURAL, (BlockPos)BlockPos.m_274446_((Position)at), (RandomSource)world.f_46441_)) break block8;
            }
            return;
        }
        mob.m_20219_(at);
        mob.m_6518_((ServerLevelAccessor)world, world.m_6436_(mob.m_20183_()), MobSpawnType.NATURAL, null, null);
        WildBosses.natural(mob, MobSpawnType.NATURAL, p.m_217043_().m_188503_(100));
        mob.getPersistentData().m_128379_("RealmWildlife", true);
        world.m_7967_((Entity)mob);
        LivingRealm.dirty(p);
    }

    public static void hazards(ServerPlayer p) {
        if (!LivingRealm.available(p) || OdysseyRealm.camp(p, p.m_20183_()) || LivingRealm.session(p) != null) {
            return;
        }
        int r = RealmTerrain.region(p, p.m_20183_());
        if (r < 0) {
            return;
        }
        BlockPos at = OdysseyRealm.arena(p, r).m_7918_(92, 1, 0);
        long now = GuideService.now(p);
        CompoundTag data = LivingRealm.state((ServerPlayer)p).sites;
        if (!LivingRealm.close(p, at, 7.0)) {
            data.m_128473_("HazardWarn");
            return;
        }
        if (!data.m_128441_("HazardWarn")) {
            data.m_128356_("HazardWarn", now);
            LivingRealm.tell(p, "hazard." + r, new Object[0]);
            LivingRealm.react(p, "danger");
            LivingRealm.dirty(p);
            return;
        }
        if (now - data.m_128454_("HazardWarn") < 60L || now < LivingRealm.state((ServerPlayer)p).hazardAt) {
            return;
        }
        LivingRealm.state((ServerPlayer)p).hazardAt = now + 160L;
        data.m_128356_("HazardWarn", now);
        if (r == 0) {
            p.m_5997_(0.5, 0.0, 0.2);
        } else if (r == 1) {
            p.m_6469_(p.m_269291_().m_269425_(), 2.0f);
        } else if (r == 2) {
            p.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 60, 0));
            BlockPos floor = at.m_7495_();
            if (p.m_9236_().m_8055_(floor).m_60713_(Blocks.f_50472_)) {
                p.m_9236_().m_7471_(floor, false);
                data.m_128356_("Crumble", floor.m_121878_());
                data.m_128356_("RestoreAt", now + 100L);
            }
        } else {
            p.m_7292_(new MobEffectInstance(MobEffects.f_19613_, 60, 0));
        }
        LivingRealm.dirty(p);
    }

    private static void react(ServerPlayer p, String event) {
        long now = GuideService.now(p);
        if (now < LivingRealm.state((ServerPlayer)p).voiceAt || !GuideService.state((ServerPlayer)p).welcomes) {
            return;
        }
        List<Mob> crew = LivingRealm.crew(p);
        if (crew.isEmpty()) {
            return;
        }
        LivingRealm.state((ServerPlayer)p).voiceAt = now + 600L;
        ShadowRecord r = ShadowService.record((Entity)crew.get(p.m_217043_().m_188503_(crew.size())));
        LivingRealm.tell(p, "reaction." + event + "." + Math.floorMod(r.life.personality, 3), r.name);
        LivingRealm.dirty(p);
    }
}

