/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.EntityTeleportEvent$EnderEntity
 */
package dev.shadowsovereign.sentinel;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.sentinel.SentinelState;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.vigil.VigilCrown;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public final class SentinelService {
    public static boolean internalRecall;

    public static boolean free(ServerPlayer p, ShadowRecord r) {
        AdventureData.Session session = AdventureService.journal((ServerPlayer)p).session;
        return p.m_6084_() && !p.m_5833_() && !VigilCrown.active(p) && (session == null || session.phase == AdventureData.Phase.READY) && !AdventureService.busy(p.f_8924_, p.m_20148_(), r.id);
    }

    public static boolean command(ServerPlayer p, ShadowRecord r, String command) {
        if (!SentinelService.free(p, r)) {
            return false;
        }
        Mob m = ShadowService.active(p.f_8924_, r);
        SentinelState s = r.sentinel;
        switch (command) {
            case "WITHDRAW": {
                return ShadowService.recall(p, r);
            }
            case "SUMMON": {
                return ShadowService.summon(p, r, true);
            }
            case "CLOSE": 
            case "BODYGUARD": 
            case "RETREAT": {
                ShadowService.setOrder(p, r, Order.FOLLOW);
                s.style = SentinelState.Style.valueOf(command);
                break;
            }
            case "NORMAL": {
                ShadowService.setOrder(p, r, Order.FOLLOW);
                break;
            }
            case "RALLY": {
                ShadowService.setOrder(p, r, Order.FOLLOW);
                s.rally = true;
                s.style = SentinelState.Style.CLOSE;
                break;
            }
            case "CEASE_FIRE": {
                s.style = SentinelState.Style.CEASE_FIRE;
                if (m == null) break;
                SentinelService.clear(m);
                break;
            }
            case "SAVE_POST": {
                if (m == null || m.m_9236_() != p.m_9236_()) {
                    return false;
                }
                s.home = m.m_20183_();
                s.homeDimension = m.m_9236_().m_46472_().m_135782_().toString();
                break;
            }
            case "RETURN_POST": {
                ServerLevel w;
                if (s.homeDimension.isBlank() || r.pendingRespawn()) {
                    return false;
                }
                BlockPos savedHome = s.home;
                String savedDimension = s.homeDimension;
                ResourceLocation key = ResourceLocation.m_135820_((String)s.homeDimension);
                ServerLevel serverLevel = w = key == null ? null : p.f_8924_.m_129880_(ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)key));
                if (w == null || !w.m_46805_(s.home)) {
                    return false;
                }
                if (!SentinelService.relocate(p, r, w, Vec3.m_82539_((Vec3i)s.home))) {
                    return false;
                }
                ShadowService.setOrder(p, r, Order.GUARD);
                s.home = savedHome;
                s.homeDimension = savedDimension;
                r.anchor = savedHome;
                r.vigil.postPos = savedHome.m_121878_();
                r.vigil.postDimension = savedDimension;
                break;
            }
            case "PAUSE": {
                if (r.order != Order.PATROL) {
                    return false;
                }
                r.battle.paused = true;
                if (m == null) break;
                SentinelService.clear(m);
                break;
            }
            case "RESUME": {
                if (r.battle.route.size() < 2) {
                    return false;
                }
                ShadowService.setOrder(p, r, Order.PATROL);
                r.battle.paused = false;
                s.waitUntil = 0L;
                break;
            }
            case "PET": {
                return SoulLifeService.pet(p, r);
            }
            case "TALK": {
                return SoulLifeService.talk(p, r);
            }
            case "PRAISE": {
                return SoulLifeService.praise(p, r);
            }
            case "COMFORT": {
                return SoulLifeService.comfort(p, r);
            }
            case "FEED": {
                return SoulLifeService.feed(p, r);
            }
            default: {
                return false;
            }
        }
        GuideService.record(p, "sentinel.command");
        ArmyData.get(p.f_8924_).m_77762_();
        return true;
    }

    public static void onOrder(ServerPlayer p, ShadowRecord r, Order order) {
        r.sentinel.rally = false;
        r.sentinel.style = SentinelState.Style.NORMAL;
        if (order == Order.GUARD) {
            r.sentinel.home = r.anchor;
            r.sentinel.homeDimension = r.dimension;
        }
        if (order != Order.PATROL) {
            r.battle.paused = false;
        }
    }

    public static boolean targetAllowed(Mob mob, ShadowRecord r, ServerPlayer owner, LivingEntity target) {
        if (r == null || target == null) {
            return false;
        }
        if (owner != null && VigilCrown.member(owner, r)) {
            return true;
        }
        SentinelState.Style style = r.sentinel.style;
        if (style == SentinelState.Style.CEASE_FIRE || style == SentinelState.Style.RETREAT) {
            return false;
        }
        if (owner == null) {
            return false;
        }
        if (style == SentinelState.Style.CLOSE && target.m_20280_((Entity)owner) > 16.0) {
            return false;
        }
        return style != SentinelState.Style.BODYGUARD || target == owner.m_21188_() || target == mob.m_21188_();
    }

    public static void clear(Mob m) {
        m.m_6710_(null);
        m.m_21573_().m_26573_();
        m.m_6274_().m_21936_(MemoryModuleType.f_26372_);
        m.m_6274_().m_21936_(MemoryModuleType.f_26370_);
    }

    public static boolean moving(Mob m, ShadowRecord r, ServerPlayer p) {
        if (r.order != Order.FOLLOW || m.m_9236_() != p.m_9236_() || !SentinelService.free(p, r) || m.m_20159_() || m.m_20160_()) {
            return false;
        }
        SentinelState s = r.sentinel;
        long now = GuideService.now(p);
        double d = m.m_20280_((Entity)p);
        if (s.rally && d < 10.0) {
            ShadowService.setOrder(p, r, Order.STAY);
            s.rally = false;
            SentinelService.clear(m);
            return true;
        }
        if (d > 324.0 && now >= s.retryAt) {
            EntityTeleportEvent.EnderEntity event;
            s.retryAt = now + 10L;
            Vec3 safe = SentinelService.safeFollow(m, p);
            if (safe != null && m.m_20182_().m_82557_(safe) > 9.0 && !(event = ForgeEventFactory.onEnderTeleport((LivingEntity)m, (double)safe.f_82479_, (double)safe.f_82480_, (double)safe.f_82481_)).isCanceled() && Double.isFinite(event.getTargetX()) && Double.isFinite(event.getTargetY()) && Double.isFinite(event.getTargetZ())) {
                Vec3 vec3 = new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                if (vec3.m_82557_(safe) < 0.01) {
                    SentinelService.clear(m);
                    m.m_6021_(safe.f_82479_, safe.f_82480_, safe.f_82481_);
                    m.f_19789_ = 0.0f;
                    m.m_20256_(Vec3.f_82478_);
                    ShadowService.updateSnapshot(m, r);
                    return true;
                }
            }
        }
        if (s.style == SentinelState.Style.CLOSE || s.style == SentinelState.Style.RETREAT || s.rally) {
            m.m_6710_(null);
            m.m_6274_().m_21936_(MemoryModuleType.f_26372_);
            if (d > 4.0) {
                if (m.f_19797_ % 5 == 0) {
                    m.m_21573_().m_5624_((Entity)p, Math.min(2.4, 1.3 + p.m_20184_().m_165924_() * 2.0));
                }
            } else {
                m.m_21573_().m_26573_();
            }
            return true;
        }
        return false;
    }

    private static Vec3 safeFollow(Mob m, ServerPlayer p) {
        Vec3 safe = ShadowService.safePosition(m, p.m_20182_(), p.m_284548_());
        if (safe != null) {
            return safe;
        }
        if (!p.m_284548_().m_46805_(p.m_20183_())) {
            return null;
        }
        BlockPos ground = p.m_284548_().m_5452_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p.m_20183_());
        return ShadowService.safePosition(m, Vec3.m_82539_((Vec3i)ground), p.m_284548_());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean relocate(ServerPlayer p, ShadowRecord r, ServerLevel w, Vec3 near) {
        block11: {
            Vec3 safe;
            Mob old;
            block13: {
                block12: {
                    if (r.pendingRespawn()) {
                        return false;
                    }
                    old = ShadowService.active(p.f_8924_, r);
                    if (old == null || old.m_9236_() != w) break block11;
                    safe = ShadowService.safePosition(old, near, w);
                    if (safe == null) {
                        return false;
                    }
                    EntityTeleportEvent.EnderEntity e = ForgeEventFactory.onEnderTeleport((LivingEntity)old, (double)safe.f_82479_, (double)safe.f_82480_, (double)safe.f_82481_);
                    if (e.isCanceled() || !Double.isFinite(e.getTargetX()) || !Double.isFinite(e.getTargetY()) || !Double.isFinite(e.getTargetZ())) break block12;
                    Vec3 vec3 = new Vec3(e.getTargetX(), e.getTargetY(), e.getTargetZ());
                    if (!(vec3.m_82557_(safe) > 0.01)) break block13;
                }
                return false;
            }
            SentinelService.clear(old);
            old.m_6021_(safe.f_82479_, safe.f_82480_, safe.f_82481_);
            old.f_19789_ = 0.0f;
            ShadowService.updateSnapshot(old, r);
            return true;
        }
        boolean wanted = r.sentinel.wanted;
        internalRecall = true;
        try {
            if (r.activeEntity != null) {
                ShadowService.recall(p, r);
            }
        }
        finally {
            internalRecall = false;
        }
        boolean ok = ShadowService.summonAt(p, r, false, w, near);
        r.sentinel.wanted = wanted || ok;
        return ok;
    }

    public static void playerTick(ServerPlayer p) {
        if (!p.m_6084_()) {
            return;
        }
        long now = GuideService.now(p);
        for (ShadowRecord r : GuideService.army((ServerPlayer)p).shadows.values()) {
            SentinelState s = r.sentinel;
            if (!s.wanted || r.pendingRespawn() || !SentinelService.free(p, r)) continue;
            Mob m = ShadowService.active(p.f_8924_, r);
            if (m != null) {
                s.missingSince = 0L;
                if (r.order == Order.FOLLOW && m.m_9236_() != p.m_9236_()) {
                    SentinelService.relocate(p, r, p.m_284548_(), p.m_20182_());
                    continue;
                }
                if (r.order != Order.FOLLOW) continue;
                SentinelService.moving(m, r, p);
                continue;
            }
            if (r.activeEntity != null && r.order == Order.FOLLOW) {
                if (s.missingSince == 0L) {
                    s.missingSince = now;
                    continue;
                }
                if (now - s.missingSince < 40L) continue;
                SentinelService.relocate(p, r, p.m_284548_(), p.m_20182_());
                s.missingSince = 0L;
                continue;
            }
            if (r.activeEntity != null || now < s.retryAt) continue;
            s.retryAt = now + 100L;
            if (r.order == Order.FOLLOW) {
                ShadowService.summon(p, r, false);
                continue;
            }
            ResourceLocation key = ResourceLocation.m_135820_((String)r.dimension);
            ServerLevel w = key == null ? null : p.f_8924_.m_129880_(ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)key));
            if (w == null || !w.m_46805_(s.last)) continue;
            ShadowService.summonAt(p, r, false, w, Vec3.m_82539_((Vec3i)s.last));
        }
    }

    public static void changedDimension(ServerPlayer p) {
        for (ShadowRecord r : GuideService.army((ServerPlayer)p).shadows.values()) {
            if (!r.sentinel.wanted || r.order != Order.FOLLOW || r.pendingRespawn() || !SentinelService.free(p, r)) continue;
            SentinelService.relocate(p, r, p.m_284548_(), p.m_20182_());
        }
    }
}

