/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.EntityTeleportEvent$EnderEntity
 */
package dev.shadowsovereign.eclipse;

import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseCamp;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.eclipse.EclipseText;
import dev.shadowsovereign.royal.RoyalMutators;
import dev.shadowsovereign.royal.RoyalRuns;
import dev.shadowsovereign.royal.RoyalService;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public final class EclipsePowers {
    public static boolean reflecting;

    private EclipsePowers() {
    }

    public static Vec3 aim(ServerPlayer p, int range) {
        Vec3 from = p.m_146892_();
        BlockHitResult hit = p.m_9236_().m_45547_(new ClipContext(from, from.m_82549_(p.m_20154_().m_82490_((double)range)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)p));
        Vec3 at = hit.m_82450_();
        return p.m_284548_().m_46805_(BlockPos.m_274446_((Position)at)) && p.m_9236_().m_6857_().m_61937_(BlockPos.m_274446_((Position)at)) ? at : null;
    }

    public static List<LivingEntity> targets(ServerPlayer p, Vec3 center, double radius, int cap) {
        return p.m_9236_().m_6443_(LivingEntity.class, new AABB(center, center).m_82400_(radius), m -> m.m_20238_(center) <= radius * radius && ShadowService.canAttack(p, m) && p.m_142582_((Entity)m)).stream().sorted(Comparator.comparingDouble(m -> m.m_20238_(center))).limit(cap).toList();
    }

    public static boolean cast(ServerPlayer p, EclipseState.Power power) {
        EclipseState e = EclipseService.state(p);
        long now = EclipseService.now(p);
        if (!RoyalMutators.permits(p, power) || !EclipseService.usable(p) || e.wins < power.wins || e.energy < (float)power.cost || now < e.ready.getOrDefault((Object)power, 0L) || EclipseRuns.isBoard(p) || p.m_20159_()) {
            return false;
        }
        Vec3 at = EclipsePowers.aim(p, 24);
        boolean success = false;
        switch (power) {
            case DASH: {
                EntityTeleportEvent.EnderEntity veto;
                Vec3 direction = new Vec3(p.m_20154_().f_82479_, 0.0, p.m_20154_().f_82481_).m_82541_();
                if (direction.m_82556_() < 0.1) {
                    return false;
                }
                Vec3 dest = null;
                double reach = 6.0 + 0.25 * (double)(RoyalService.rank(p, power) - 1);
                for (int i = 1; i <= (int)Math.ceil(reach * 2.0); ++i) {
                    Vec3 next = p.m_20182_().m_82549_(direction.m_82490_(Math.min(reach, (double)i * 0.5)));
                    AABB box = p.m_20191_().m_82383_(next.m_82546_(p.m_20182_()));
                    BlockPos block = BlockPos.m_274446_((Position)next);
                    if (!p.m_284548_().m_46805_(block) || !p.m_9236_().m_6857_().m_61935_(box) || !p.m_9236_().m_45756_((Entity)p, box) || p.m_9236_().m_8055_(block.m_7495_()).m_60812_((BlockGetter)p.m_9236_(), block.m_7495_()).m_83281_() || !p.m_9236_().m_6425_(block).m_76178_()) break;
                    dest = next;
                }
                if (dest == null || !(dest.m_82557_(p.m_20182_()) >= 1.0) || (veto = ForgeEventFactory.onEnderTeleport((LivingEntity)p, (double)dest.f_82479_, (double)dest.f_82480_, (double)dest.f_82481_)).isCanceled()) break;
                Vec3 vec3 = new Vec3(veto.getTargetX(), veto.getTargetY(), veto.getTargetZ());
                if (!(vec3.m_82557_(dest) < 0.01)) break;
                p.m_6021_(dest.f_82479_, dest.f_82480_, dest.f_82481_);
                success = true;
                break;
            }
            case WELL: {
                if (at == null) break;
                e.well = at;
                e.wellUntil = now + 80L;
                success = true;
                break;
            }
            case LANCE: {
                Vec3 start;
                Vec3 line;
                double length;
                if (at == null || !((length = (line = at.m_82546_(start = p.m_146892_())).m_82553_()) > 0.0)) break;
                Vec3 dir = line.m_82541_();
                int accepted = 0;
                for (LivingEntity target : EclipsePowers.targets(p, start.m_82549_(line.m_82490_(0.5)), length / 2.0 + 2.0, 32)) {
                    double t = Math.max(0.0, Math.min(length, target.m_20191_().m_82399_().m_82546_(start).m_82526_(dir)));
                    if (!target.m_20191_().m_82400_(0.35).m_82390_(start.m_82549_(dir.m_82490_(t))) || !EclipsePowers.lance(p, target) || ++accepted < 3) continue;
                    break;
                }
                boolean bl = success = accepted > 0;
                if (!success) break;
                for (int i = 0; i < 20; ++i) {
                    EclipsePowers.particles(p, start.m_82549_(line.m_82490_((double)i / 20.0)), ParticleTypes.f_123810_, 1);
                }
                break;
            }
            case PARRY: {
                e.parryUntil = now + 12L;
                success = true;
                break;
            }
            case STORM: {
                if (at == null) break;
                e.storm = at;
                e.stormUntil = now + 60L;
                success = true;
            }
        }
        if (success) {
            e.energy -= (float)power.cost;
            e.lastCast = now;
            e.ready.put(power, now + (long)power.cooldown);
            EclipseService.dirty(p);
            RoyalRuns.power(p, power);
            EclipseText.tell(p, "cast", EclipseText.c(EclipseText.key(power), new Object[0]));
        }
        return success;
    }

    private static boolean lance(ServerPlayer p, LivingEntity target) {
        boolean accepted;
        RoyalService.casting = EclipseState.Power.LANCE;
        RoyalService.acceptedTarget = null;
        try {
            accepted = target.m_6469_(p.m_269291_().m_269104_((Entity)p, (Entity)p), (float)(6.0 + 0.5 * (double)(RoyalService.rank(p, EclipseState.Power.LANCE) - 1))) || target.m_20148_().equals(RoyalService.acceptedTarget);
        }
        finally {
            RoyalService.casting = null;
            RoyalService.acceptedTarget = null;
        }
        if (accepted && target.m_6084_()) {
            RoyalService.state((ServerPlayer)p).breach.put(target.m_20148_(), EclipseService.now(p) + 60L);
        }
        return accepted;
    }

    public static void particles(ServerPlayer p, Vec3 at, SimpleParticleType type, int count) {
        p.m_284548_().m_8624_(p, (ParticleOptions)type, true, at.f_82479_, at.f_82480_, at.f_82481_, count, 0.25, 0.2, 0.25, 0.01);
    }

    public static void tick(ServerPlayer p) {
        EclipseState e = EclipseService.state(p);
        long now = EclipseService.now(p);
        if (!EclipseService.usable(p)) {
            e.storm = null;
            e.well = null;
            e.parryUntil = 0L;
            e.stormUntil = 0L;
            e.wellUntil = 0L;
            return;
        }
        if (p.f_19797_ % 10 == 0) {
            if (e.well != null && e.wellUntil > now && p.m_20238_(e.well) <= 1600.0) {
                for (LivingEntity m : EclipsePowers.targets(p, e.well, 5.0 + 0.2 * (double)(RoyalService.rank(p, EclipseState.Power.WELL) - 1), 6)) {
                    double dx = m.m_20185_() - e.well.f_82479_;
                    double dz = m.m_20189_() - e.well.f_82481_;
                    m.m_147240_(0.35, dx, dz);
                }
                EclipsePowers.particles(p, e.well, ParticleTypes.f_123760_, 12);
            } else {
                e.well = null;
            }
            if (e.storm != null && e.stormUntil > now && p.m_20238_(e.storm) <= 1600.0) {
                for (LivingEntity m : EclipsePowers.targets(p, e.storm, 4.0, 6)) {
                    m.m_6469_(p.m_269291_().m_269104_((Entity)p, (Entity)p), (float)(2.0 + 0.1 * (double)(RoyalService.rank(p, EclipseState.Power.STORM) - 1)));
                }
                EclipsePowers.particles(p, e.storm.m_82520_(0.0, 2.0, 0.0), ParticleTypes.f_123745_, 16);
            } else {
                e.storm = null;
            }
        }
        EclipseCamp.tickGate(p);
    }
}

