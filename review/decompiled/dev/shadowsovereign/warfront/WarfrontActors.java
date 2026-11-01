/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 */
package dev.shadowsovereign.warfront;

import dev.shadowsovereign.ShadowSovereign;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.WardenCompatibility;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public final class WarfrontActors {
    public static final String OWNER = "SoulboundIllusionOwner";
    public static final String UNTIL = "SoulboundIllusionUntil";

    public static boolean illusion(Entity e) {
        return e.getPersistentData().m_128403_(OWNER);
    }

    public static boolean valid(Entity e) {
        if (e.m_20194_() == null || !WarfrontActors.illusion(e)) {
            return false;
        }
        UUID id = e.getPersistentData().m_128342_(OWNER);
        ServerPlayer p = e.m_20194_().m_6846_().m_11259_(id);
        return p != null && p.m_6084_() && p.m_9236_() == e.m_9236_() && ArmyData.get((MinecraftServer)e.m_20194_()).army((UUID)id).warfront.illusions.getOrDefault(e.m_20148_(), 0L) > e.m_20194_().m_129783_().m_46467_();
    }

    public static boolean illusion(ServerPlayer p, Mob source) {
        WarfrontState w = WarfrontService.state(p);
        w.illusions.entrySet().removeIf(e -> (Long)e.getValue() <= WarfrontService.now(p));
        if (w.illusions.size() >= 2) {
            return false;
        }
        Mob copy = null;
        try {
            Entity entity = source.m_6095_().m_20615_((Level)p.m_284548_());
            if (!(entity instanceof Mob)) {
                return false;
            }
            Mob m = (Mob)entity;
            copy = m;
            m.m_20258_(ShadowService.snapshot(source, true));
            m.m_20084_(UUID.randomUUID());
            m.f_20919_ = 0;
            WardenCompatibility.attach(m);
            m.m_21557_(true);
            m.m_21530_();
            m.m_21153_(1.0f);
            m.m_6710_(null);
            m.m_21553_(false);
            m.m_6593_((Component)Component.m_237113_((String)("Phantom of " + source.m_7755_().getString())));
            m.m_20340_(true);
            Vec3 at = ShadowService.safePosition(m, source.m_20182_(), p.m_284548_());
            if (at == null) {
                return false;
            }
            m.m_7678_(at.f_82479_, at.f_82480_, at.f_82481_, source.m_146908_(), 0.0f);
            long until = WarfrontService.now(p) + 160L;
            m.getPersistentData().m_128362_(OWNER, p.m_20148_());
            m.getPersistentData().m_128356_(UNTIL, until);
            w.illusions.put(m.m_20148_(), until);
            if (!p.m_284548_().m_7967_((Entity)m)) {
                w.illusions.remove(m.m_20148_());
                return false;
            }
            return true;
        }
        catch (RuntimeException ex) {
            if (copy != null) {
                w.illusions.remove(copy.m_20148_());
                copy.m_146870_();
            }
            ShadowSovereign.LOGGER.warn("Cannot create compatible phantom", (Throwable)ex);
            return false;
        }
    }

    public static void clear(ServerPlayer p) {
        WarfrontState w = WarfrontService.state(p);
        for (ServerLevel level : p.f_8924_.m_129785_()) {
            for (UUID id : List.copyOf(w.illusions.keySet())) {
                Entity e = level.m_8791_(id);
                if (e == null) continue;
                e.m_146870_();
            }
        }
        w.illusions.clear();
    }

    public static void tick(Mob mob) {
        if (WarfrontActors.illusion((Entity)mob)) {
            if (!WarfrontActors.valid((Entity)mob)) {
                mob.m_146870_();
                return;
            }
            mob.m_6710_(null);
            mob.m_21573_().m_26573_();
            if (mob.f_19797_ % 10 == 0) {
                ServerPlayer p = mob.m_20194_().m_6846_().m_11259_(mob.getPersistentData().m_128342_(OWNER));
                for (Mob enemy : mob.m_9236_().m_6443_(Mob.class, mob.m_20191_().m_82400_(8.0), e -> !WarfrontActors.illusion((Entity)e) && e instanceof Enemy && ShadowService.canAttack(p, (LivingEntity)e))) {
                    if (AdventureWorld.isNpc((Entity)enemy) && !p.m_20148_().equals(enemy.getPersistentData().m_128342_("SoulboundAdventureOwner")) || !enemy.m_142582_((Entity)mob)) continue;
                    enemy.m_6710_((LivingEntity)mob);
                }
            }
        }
    }

    public static boolean protect(LivingAttackEvent event) {
        Mob enemy;
        boolean allowed;
        LivingEntity victim = event.getEntity();
        Entity source = event.getSource().m_7639_();
        Entity direct = event.getSource().m_7640_();
        if (source != null && WarfrontActors.illusion(source) || direct != null && WarfrontActors.illusion(direct)) {
            event.setCanceled(true);
            return true;
        }
        if (!WarfrontActors.illusion((Entity)victim)) {
            return false;
        }
        if (!WarfrontActors.valid((Entity)victim)) {
            event.setCanceled(true);
            return true;
        }
        ServerPlayer p = victim.m_20194_().m_6846_().m_11259_(victim.getPersistentData().m_128342_(OWNER));
        boolean bl = allowed = source instanceof Mob && !ShadowService.isShadow((Entity)(enemy = (Mob)source)) && ShadowService.canAttack(p, (LivingEntity)enemy) && (!AdventureWorld.isNpc((Entity)enemy) || AdventureWorld.valid((Entity)enemy) && p.m_20148_().equals(enemy.getPersistentData().m_128342_("SoulboundAdventureOwner")));
        if (source != null && AdventureWorld.isNpc(source)) {
            AdventureData.Session s = AdventureWorld.session(source);
            boolean bl2 = allowed = allowed && s != null && s.id.equals(source.getPersistentData().m_128342_("SoulboundAdventureSession")) && s.epoch == source.getPersistentData().m_128451_("SoulboundAdventureEpoch");
        }
        if (!allowed) {
            event.setCanceled(true);
        }
        return true;
    }

    public static boolean allowed(Mob actor, LivingEntity target) {
        ServerPlayer p = AdventureWorld.owner((Entity)actor);
        AdventureData.Session s = AdventureWorld.session((Entity)actor);
        if (p == null || s == null || target.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) > 1600.0) {
            return false;
        }
        if (WarfrontActors.illusion((Entity)target)) {
            return WarfrontActors.valid((Entity)target) && p.m_20148_().equals(target.getPersistentData().m_128342_(OWNER));
        }
        if (target == p) {
            return true;
        }
        if (p.m_20148_().equals(ShadowService.ownerId((Entity)target))) {
            return target.getPersistentData().m_128403_("ShadowSovereignRecord") && s.participants.contains(target.getPersistentData().m_128342_("ShadowSovereignRecord"));
        }
        return AdventureWorld.isNpc((Entity)target) && AdventureWorld.valid((Entity)target) && p.m_20148_().equals(target.getPersistentData().m_128342_("SoulboundAdventureOwner")) && AdventureWorld.npc((Entity)target).role == AdventureData.Role.ESCORT;
    }

    public static void decorate(Mob mob) {
        AdventureData.Session s = AdventureWorld.session((Entity)mob);
        if (s == null || s.kind != AdventureData.Kind.WARFRONT) {
            return;
        }
        AdventureData.Npc npc = AdventureWorld.npc((Entity)mob);
        if (npc != null && npc.role == AdventureData.Role.ESCORT) {
            mob.f_21345_.m_148105_().stream().filter(g -> g.m_26015_() instanceof PrisonerLock).toList().forEach(g -> mob.f_21345_.m_25363_(g.m_26015_()));
            mob.f_21345_.m_25352_(-30, (Goal)new PrisonerLock(mob));
        }
    }

    private static final class PrisonerLock
    extends Goal {
        private final Mob mob;

        PrisonerLock(Mob m) {
            this.mob = m;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        public boolean m_8036_() {
            AdventureData.Session s = AdventureWorld.session((Entity)this.mob);
            return s != null && s.kind == AdventureData.Kind.WARFRONT && !s.warfront.m_128471_("Released");
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            this.mob.m_21573_().m_26573_();
            this.mob.m_6710_(null);
        }
    }
}

