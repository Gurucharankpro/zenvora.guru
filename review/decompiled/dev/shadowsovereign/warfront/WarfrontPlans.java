/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.warfront;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseRoles;
import dev.shadowsovereign.sovereign.SovereignService;
import dev.shadowsovereign.sovereign.SovereignState;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class WarfrontPlans {
    private WarfrontPlans() {
    }

    public static boolean save(ServerPlayer p, int slot, List<WarfrontState.Step> steps) {
        if (!p.m_6084_() || p.m_5833_() || slot < 0 || slot >= 3 || steps.size() > 4) {
            return false;
        }
        WarfrontState w = WarfrontService.state(p);
        w.plans.set(slot, new ArrayList<WarfrontState.Step>(steps));
        WarfrontService.dirty(p);
        return true;
    }

    public static boolean arm(ServerPlayer p) {
        if (!p.m_6084_() || p.m_5833_()) {
            return false;
        }
        WarfrontService.state((ServerPlayer)p).placementUntil = WarfrontService.now(p) + 300L;
        WarfrontService.tell(p, "Aim at loaded ground within 32 blocks and press G within 15 seconds to set your rally marker.");
        return true;
    }

    public static boolean place(ServerPlayer p) {
        WarfrontState w = WarfrontService.state(p);
        if (w.placementUntil < WarfrontService.now(p) || !p.m_6084_()) {
            return false;
        }
        Vec3 from = p.m_146892_();
        BlockHitResult hit = p.m_9236_().m_45547_(new ClipContext(from, from.m_82549_(p.m_20154_().m_82490_(32.0)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)p));
        if (hit.m_6662_() != HitResult.Type.BLOCK || hit.m_82434_() != Direction.UP) {
            return false;
        }
        BlockPos at = hit.m_82425_().m_7494_();
        if (!(p.m_284548_().m_46805_(at) && p.m_9236_().m_6857_().m_61937_(at) && p.m_9236_().m_8055_(at).m_60812_((BlockGetter)p.m_9236_(), at).m_83281_())) {
            return false;
        }
        AdventureData.Session s = WarfrontService.session(p);
        if (s != null && at.m_123331_((Vec3i)s.anchor) > 1600.0) {
            return false;
        }
        w.marker = at;
        w.markerDimension = p.m_9236_().m_46472_().m_135782_().toString();
        w.placementUntil = 0L;
        WarfrontService.dirty(p);
        WarfrontService.tell(p, "Rally marker placed.");
        return true;
    }

    public static boolean start(ServerPlayer p, int slot) {
        Vec3 end;
        Vec3 from;
        EntityHitResult hit;
        WarfrontState w = WarfrontService.state(p);
        if (!p.m_6084_() || p.m_5833_() || slot < 0 || slot >= 3 || w.plans.get(slot).isEmpty()) {
            return false;
        }
        List<WarfrontState.Step> steps = w.plans.get(slot);
        if (steps.stream().anyMatch(s -> s == WarfrontState.Step.RALLY || s == WarfrontState.Step.GUARD) && (w.marker == null || !w.markerDimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) || !p.m_284548_().m_46805_(w.marker) || p.m_20238_(Vec3.m_82539_((Vec3i)w.marker)) > 1600.0)) {
            return false;
        }
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        BattleRules.Squad scope = army.selectedSquad;
        AdventureData.Session session = WarfrontService.session(p);
        List<ShadowRecord> eligible = army.shadows.values().stream().filter(r -> scope == BattleRules.Squad.ALL || r.battle.squad == scope).filter(r -> session == null || session.participants.contains(r.id)).filter(r -> {
            Mob m = ShadowService.active(p.f_8924_, r);
            return m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 1024.0;
        }).toList();
        if (eligible.isEmpty()) {
            return false;
        }
        UUID focus = null;
        if (steps.contains((Object)WarfrontState.Step.FOCUS) && (hit = ProjectileUtil.m_37287_((Entity)p, (Vec3)(from = p.m_146892_()), (Vec3)(end = from.m_82549_(p.m_20154_().m_82490_(40.0))), (AABB)p.m_20191_().m_82369_(end.m_82546_(from)).m_82400_(1.0), e -> {
            LivingEntity living;
            return e instanceof LivingEntity && ShadowService.canAttack(p, living = (LivingEntity)e) && p.m_142582_(e);
        }, (double)1600.0)) != null) {
            focus = hit.m_82443_().m_20148_();
        }
        WarfrontPlans.stop(p);
        List<Previous> previous = eligible.stream().map(r -> new Previous(r.id, r.order, r.anchor, r.battle.commandedTarget)).toList();
        w.execution = new Execution(slot, steps, previous, p.m_9236_().m_46472_().m_135782_().toString(), focus, w.marker);
        WarfrontService.tell(p, "Battle Plan " + (slot + 1) + " started for " + previous.size() + " companions.");
        return true;
    }

    public static void stop(ServerPlayer p) {
        WarfrontState w = WarfrontService.state(p);
        Execution run = w.execution;
        if (run == null) {
            return;
        }
        w.execution = null;
        for (Previous old : run.previous) {
            ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(old.id);
            if (r == null) continue;
            r.order = old.order;
            r.anchor = old.anchor;
            r.battle.commandedTarget = old.target;
            Mob m = ShadowService.active(p.f_8924_, r);
            if (m == null) continue;
            m.m_21573_().m_26573_();
            m.m_6710_(null);
        }
        WarfrontService.dirty(p);
    }

    public static void tick(ServerPlayer p) {
        boolean finished;
        Entity focus;
        WarfrontState w = WarfrontService.state(p);
        Execution run = w.execution;
        if (run == null) {
            return;
        }
        if (!p.m_6084_() || !run.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) || run.previous.stream().anyMatch(old -> WarfrontService.a(p, old.id) == null)) {
            WarfrontPlans.stop(p);
            return;
        }
        if (run.index >= run.steps.size()) {
            WarfrontPlans.stop(p);
            return;
        }
        long now = WarfrontService.now(p);
        WarfrontState.Step step = run.steps.get(run.index);
        Entity entity = focus = run.focus == null ? null : p.m_284548_().m_8791_(run.focus);
        if (!run.entered) {
            run.entered = true;
            run.until = now + (long)(step == WarfrontState.Step.FOCUS ? 200 : 100);
            for (Previous old2 : run.previous) {
                ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(old2.id);
                Mob m = WarfrontService.a(p, old2.id);
                if (m == null) continue;
                m.m_6710_(null);
                m.m_21573_().m_26573_();
                r.battle.commandedTarget = null;
                switch (step) {
                    case RALLY: 
                    case GUARD: {
                        r.order = Order.GUARD;
                        r.anchor = run.marker;
                        break;
                    }
                    case REGROUP: {
                        r.order = Order.FOLLOW;
                        break;
                    }
                    case FOCUS: {
                        LivingEntity target;
                        r.order = Order.FOLLOW;
                        if (!(focus instanceof LivingEntity) || !ShadowService.canAttack(p, target = (LivingEntity)focus)) break;
                        r.battle.commandedTarget = target.m_20148_();
                        m.m_6710_(target);
                    }
                }
            }
            WarfrontService.dirty(p);
        }
        boolean bl = finished = now >= run.until;
        if (!(step != WarfrontState.Step.FOCUS || focus != null && focus.m_6084_())) {
            finished = true;
        }
        if (step == WarfrontState.Step.RALLY && run.previous.stream().allMatch(old -> {
            Mob m = WarfrontService.a(p, old.id);
            return m != null && m.m_20238_(Vec3.m_82539_((Vec3i)run.marker)) <= 9.0;
        })) {
            finished = true;
        }
        if (step == WarfrontState.Step.REGROUP && run.previous.stream().allMatch(old -> {
            Mob m = WarfrontService.a(p, old.id);
            return m != null && m.m_20280_((Entity)p) <= 16.0;
        })) {
            finished = true;
        }
        if (finished) {
            ++run.index;
            run.entered = false;
            if (run.index >= run.steps.size()) {
                EclipseRoles.planFinished(p, run.previous.stream().map(Previous::id).toList());
                SovereignService.party(p, run.previous.stream().map(Previous::id).toList(), SovereignState.Goal.PLAN);
                WarfrontPlans.stop(p);
            }
        }
    }

    public static final class Execution {
        public final int slot;
        public final List<WarfrontState.Step> steps;
        public final List<Previous> previous;
        public final String dimension;
        public final UUID focus;
        public final BlockPos marker;
        public int index;
        public long until;
        public boolean entered;

        Execution(int slot, List<WarfrontState.Step> steps, List<Previous> previous, String dim, UUID focus, BlockPos marker) {
            this.slot = slot;
            this.steps = List.copyOf(steps);
            this.previous = List.copyOf(previous);
            this.dimension = dim;
            this.focus = focus;
            this.marker = marker;
        }
    }

    public record Previous(UUID id, Order order, BlockPos anchor, UUID target) {
    }
}

