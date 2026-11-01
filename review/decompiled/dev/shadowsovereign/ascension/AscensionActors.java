/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 */
package dev.shadowsovereign.ascension;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.AscensionService;
import dev.shadowsovereign.ascension.WardenCompatibility;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public final class AscensionActors {
    public static boolean allowed(Mob actor, LivingEntity target) {
        boolean same;
        AdventureData.Session s = AdventureWorld.session((Entity)actor);
        ServerPlayer p = AdventureWorld.owner((Entity)actor);
        AdventureData.Npc n = AdventureWorld.npc((Entity)actor);
        if (s == null || p == null || n == null || !target.m_6084_()) {
            return false;
        }
        if (s.kind == AdventureData.Kind.HEIST) {
            return false;
        }
        AdventureData.Npc other = AdventureWorld.npc((Entity)target);
        boolean bl = same = AdventureWorld.valid((Entity)target) && AdventureWorld.session((Entity)target) == s;
        return n.role == AdventureData.Role.ALLY ? same && other.role == AdventureData.Role.ENEMY : target == p || p.m_20148_().equals(ShadowService.ownerId((Entity)target)) || same && other.role == AdventureData.Role.ALLY;
    }

    public static boolean protect(LivingAttackEvent event) {
        AdventureData.Npc n;
        AdventureData.Session as;
        LivingEntity v = event.getEntity();
        Entity source = event.getSource().m_7639_();
        Entity direct = event.getSource().m_7640_();
        Entity actor = direct != null && AdventureWorld.isNpc(direct) ? direct : source;
        AdventureData.Session vs = AdventureWorld.session((Entity)v);
        AdventureData.Session session = as = actor == null ? null : AdventureWorld.session(actor);
        if (!(vs != null && AscensionService.activity(vs.kind) || as != null && AscensionService.activity(as.kind))) {
            return false;
        }
        if (AdventureWorld.isNpc((Entity)v) && !AdventureWorld.valid((Entity)v)) {
            event.setCanceled(true);
            return true;
        }
        if (actor != null && AdventureWorld.isNpc(actor)) {
            boolean ok;
            boolean same;
            n = AdventureWorld.npc(actor);
            ServerPlayer owner = AdventureWorld.owner(actor);
            if (as == null || n == null || owner == null || as.phase != AdventureData.Phase.ACTIVE || !actor.getPersistentData().m_128403_("SoulboundAdventureSession") || !as.id.equals(actor.getPersistentData().m_128342_("SoulboundAdventureSession")) || as.epoch != actor.getPersistentData().m_128451_("SoulboundAdventureEpoch")) {
                event.setCanceled(true);
                return true;
            }
            AdventureData.Npc other = AdventureWorld.npc((Entity)v);
            boolean bl = same = AdventureWorld.valid((Entity)v) && vs == as;
            boolean bl2 = n.role == AdventureData.Role.ALLY ? same && other.role == AdventureData.Role.ENEMY : (ok = v == owner || owner.m_20148_().equals(ShadowService.ownerId((Entity)v)) || same && other.role == AdventureData.Role.ALLY);
            if (!ok) {
                event.setCanceled(true);
            }
        }
        if (vs != null) {
            n = AdventureWorld.npc((Entity)v);
            UUID incoming = AdventureService.damageOwner(event.getSource());
            UUID owner = v.getPersistentData().m_128342_("SoulboundAdventureOwner");
            if (source != null && !owner.equals(incoming)) {
                event.setCanceled(true);
            }
            if (n != null && n.role == AdventureData.Role.ALLY && (as != vs || AdventureWorld.npc(actor) == null || AdventureWorld.npc((Entity)actor).role != AdventureData.Role.ENEMY)) {
                event.setCanceled(true);
            }
            if (vs.kind == AdventureData.Kind.HEIST && !event.isCanceled()) {
                vs.journey.m_128405_("Alarm", Math.min(100, vs.journey.m_128451_("Alarm") + 50));
                AdventureData.get(v.m_20194_()).m_77762_();
            }
        }
        return true;
    }

    public static void decorate(Mob m) {
        AdventureData.Session s = AdventureWorld.session((Entity)m);
        if (s == null || !AscensionService.activity(s.kind)) {
            return;
        }
        m.f_21345_.m_148105_().stream().filter(g -> g.m_26015_() instanceof ActivityGoal).toList().forEach(g -> m.f_21345_.m_25363_(g.m_26015_()));
        m.f_21345_.m_25352_(-30, (Goal)new ActivityGoal(m));
        WardenCompatibility.attach(m);
    }

    public static void tick(Mob mob) {
        LivingEntity e2;
        Entity entity;
        AdventureData.Session s = AdventureWorld.session((Entity)mob);
        AdventureData.Npc n = AdventureWorld.npc((Entity)mob);
        ServerPlayer p = AdventureWorld.owner((Entity)mob);
        if (s == null || n == null || p == null || n.role != AdventureData.Role.ALLY) {
            return;
        }
        LivingEntity target = null;
        if (s.journey.m_128403_("Focus") && (entity = p.m_284548_().m_8791_(s.journey.m_128342_("Focus"))) instanceof LivingEntity && AscensionActors.allowed(mob, e2 = (LivingEntity)entity)) {
            target = e2;
        }
        if (target == null) {
            target = s.npcs.values().stream().filter(a -> a.role == AdventureData.Role.ENEMY && a.active != null).map(a -> p.m_284548_().m_8791_(a.active)).filter(e -> e instanceof LivingEntity && e.m_6084_()).map(e -> (LivingEntity)e).min(Comparator.comparingDouble(arg_0 -> ((Mob)mob).m_20280_(arg_0))).orElse(null);
        }
        mob.m_6710_(target);
        if (mob.m_6274_().m_147339_().containsKey(MemoryModuleType.f_26372_)) {
            if (target == null) {
                mob.m_6274_().m_21936_(MemoryModuleType.f_26372_);
            } else {
                mob.m_6274_().m_21879_(MemoryModuleType.f_26372_, target);
            }
        }
    }

    private static final class ActivityGoal
    extends Goal {
        private final Mob mob;

        ActivityGoal(Mob m) {
            this.mob = m;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            AdventureData.Session s = AdventureWorld.session((Entity)this.mob);
            AdventureData.Npc n = AdventureWorld.npc((Entity)this.mob);
            return AdventureWorld.valid((Entity)this.mob) && s != null && (s.kind == AdventureData.Kind.HEIST || n.role == AdventureData.Role.ALLY && this.mob.m_5448_() == null);
        }

        public boolean m_8045_() {
            return this.m_8036_();
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            ServerPlayer p = AdventureWorld.owner((Entity)this.mob);
            AdventureData.Session s = AdventureWorld.session((Entity)this.mob);
            if (p == null || s == null) {
                return;
            }
            if (s.kind == AdventureData.Kind.HEIST) {
                this.mob.m_6710_(null);
                AdventureData.Npc n = AdventureWorld.npc((Entity)this.mob);
                int index = Math.floorMod(n.id.hashCode(), 3);
                Vec3 at = AscensionService.point(s, (index + (int)(p.f_8924_.m_129783_().m_46467_() / 120L) % 2) % 3);
                if (this.mob.f_19797_ % 20 == 0) {
                    this.mob.m_21573_().m_26519_(at.f_82479_, at.f_82480_, at.f_82481_, 0.65);
                }
                return;
            }
            if (this.mob.m_20280_((Entity)p) > 16.0 && this.mob.f_19797_ % 20 == 0) {
                this.mob.m_21573_().m_5624_((Entity)p, 1.0);
            }
        }
    }
}

