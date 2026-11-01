/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.monster.Slime
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingHealEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.eclipse;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.eclipse.EclipseRoles;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.network.EclipseNetwork;
import dev.shadowsovereign.royal.RoyalRuns;
import dev.shadowsovereign.royal.RoyalService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class EclipseEvents {
    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
            EclipseService.tick(p);
            if (EclipseService.now(p) % 20L != 0L || p.getPersistentData().m_128454_("EclipseViewUntil") <= EclipseService.now(p) && !(EclipseService.state((ServerPlayer)p).energy < 100.0f) && EclipseRuns.session(p) == null) continue;
            EclipseNetwork.send(p, false);
        }
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            EclipseService.state((ServerPlayer)p).energyClock = EclipseService.now(p);
            EclipseNetwork.send(p, false);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            EclipseService.clearRuntime(p);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            EclipseService.clearRuntime(p);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void protect(LivingAttackEvent e) {
        AdventureData.Session s;
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        LivingEntity victim = e.getEntity();
        ShadowRecord r = CompanionProgression.attacker(e.getSource());
        if (AdventureWorld.isNpc((Entity)victim) && (s = AdventureWorld.session((Entity)victim)) != null && EclipseRuns.is(s.kind)) {
            if (r != null && !s.participants.contains(r.id)) {
                e.setCanceled(true);
                return;
            }
            if (s.eclipse.m_128454_("GraceUntil") > victim.m_20194_().m_129783_().m_46467_() && s.eclipse.m_128403_("Boss") && AdventureWorld.npc((Entity)victim).id.equals(s.eclipse.m_128342_("Boss"))) {
                e.setCanceled(true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void hurt(LivingHurtEvent e) {
        Mob m;
        LivingEntity saved2;
        ServerPlayer p;
        if (e.getEntity().m_9236_().f_46443_ || e.getAmount() <= 0.0f) {
            return;
        }
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            LivingEntity incoming;
            ServerPlayer p2 = (ServerPlayer)livingEntity;
            livingEntity = e.getSource().m_7639_();
            if (livingEntity instanceof LivingEntity && !((incoming = livingEntity) instanceof Player) && !EclipsePowers.reflecting && ShadowService.canAttack(p2, incoming) && p2.m_20154_().m_82526_(incoming.m_20182_().m_82546_(p2.m_20182_()).m_82541_()) >= 0.5) {
                EclipseState s = EclipseService.state(p2);
                if (s.parryUntil > EclipseService.now(p2)) {
                    LivingEntity target;
                    s.parryUntil = 0L;
                    float saved2 = Math.min(8.0f + 0.5f * (float)(RoyalService.rank(p2, EclipseState.Power.PARRY) - 1), e.getAmount());
                    RoyalRuns.parried(p2, (Entity)incoming);
                    e.setAmount(Math.max(0.0f, e.getAmount() - saved2));
                    Entity attacker = e.getSource().m_7639_();
                    if (attacker instanceof LivingEntity && p2.m_20280_((Entity)(target = (LivingEntity)attacker)) <= 64.0 && p2.m_142582_((Entity)target) && ShadowService.canAttack(p2, target)) {
                        EclipsePowers.reflecting = true;
                        try {
                            target.m_6469_(p2.m_269291_().m_269104_((Entity)p2, (Entity)p2), Math.min(4.0f, saved2));
                        }
                        finally {
                            EclipsePowers.reflecting = false;
                        }
                    }
                }
            }
        }
        ShadowRecord r = CompanionProgression.attacker(e.getSource());
        Entity actor = CompanionProgression.source(e.getSource());
        if (r != null && actor != null && (p = ShadowService.owner(actor)) != null && EclipseRuns.hasCard(p, EclipseState.Card.MARKSMAN) && e.getSource().m_269533_(DamageTypeTags.f_268524_)) {
            e.setAmount(e.getAmount() * 1.1f);
        }
        if ((saved2 = e.getEntity()) instanceof Mob && ShadowService.isShadow((Entity)(m = (Mob)saved2))) {
            float shield = m.getPersistentData().m_128457_("EclipseRoomShield") + m.getPersistentData().m_128457_("EclipsePhialShield");
            if (m.getPersistentData().m_128454_("EclipsePhialUntil") < m.m_20194_().m_129783_().m_46467_()) {
                m.getPersistentData().m_128473_("EclipsePhialShield");
                shield = m.getPersistentData().m_128457_("EclipseRoomShield");
            }
            if (shield > 0.0f) {
                float blocked = Math.min(shield, e.getAmount());
                e.setAmount(e.getAmount() - blocked);
                float room = m.getPersistentData().m_128457_("EclipseRoomShield");
                float take = Math.min(room, blocked);
                m.getPersistentData().m_128350_("EclipseRoomShield", room - take);
                m.getPersistentData().m_128350_("EclipsePhialShield", Math.max(0.0f, m.getPersistentData().m_128457_("EclipsePhialShield") - (blocked - take)));
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void damage(LivingDamageEvent e) {
        Mob m;
        ServerPlayer p;
        if (e.getEntity().m_9236_().f_46443_ || e.getAmount() <= 0.0f) {
            return;
        }
        LivingEntity target = e.getEntity();
        if (target instanceof ServerPlayer) {
            ServerPlayer p2 = (ServerPlayer)target;
            EclipseService.state((ServerPlayer)p2).gateUntil = 0L;
        }
        if (AdventureWorld.isNpc((Entity)target)) {
            AdventureData.Session session = AdventureWorld.session((Entity)target);
            AdventureData.Npc n = AdventureWorld.npc((Entity)target);
            if (session != null && (session.kind == AdventureData.Kind.CRYSTAL || session.kind == AdventureData.Kind.CHRONOPHAGE) && n != null && session.eclipse.m_128403_("Boss") && n.id.equals(session.eclipse.m_128342_("Boss"))) {
                CompoundTag run = session.eclipse;
                int phase = run.m_128451_("BossPhase");
                if (session.kind == AdventureData.Kind.CRYSTAL && phase == 0) {
                    boolean crystals = false;
                    for (int i = 0; i < 3; ++i) {
                        if (!run.m_128403_("Crystal" + i) || !session.npcs.containsKey(run.m_128342_("Crystal" + i))) continue;
                        crystals = true;
                    }
                    if (crystals) {
                        e.setAmount(Math.max(0.0f, Math.min(e.getAmount(), target.m_21223_() - target.m_21233_() * 0.7f)));
                    }
                }
                float threshold = target.m_21233_() * (phase == 0 ? 0.65f : 0.3f);
                if (phase < 2 && target.m_21223_() - e.getAmount() <= threshold) {
                    e.setAmount(Math.max(0.0f, target.m_21223_() - threshold));
                    run.m_128405_("BossPhase", phase + 1);
                    run.m_128356_("GraceUntil", target.m_20194_().m_129783_().m_46467_() + 40L);
                }
            }
        }
        ShadowRecord r = CompanionProgression.attacker(e.getSource());
        Entity actor = CompanionProgression.source(e.getSource());
        if (r != null && actor != null && (p = ShadowService.owner(actor)) != null) {
            Mob mob;
            EclipseState.Metric metric = EclipseService.metric(p, r.id);
            if (metric != null) {
                metric.damage += (double)Math.min(e.getAmount(), target.m_21223_());
            }
            if ((mob = ShadowService.active(p.f_8924_, r)) != null && e.getAmount() < target.m_21223_()) {
                EclipseRoles.hit(p, r, mob, target, !e.getSource().m_269533_(DamageTypeTags.f_268524_));
            }
        }
        if (target instanceof Mob && ShadowService.isShadow((Entity)(m = (Mob)target))) {
            ShadowRecord record = ShadowService.record((Entity)m);
            ServerPlayer p3 = ShadowService.owner((Entity)m);
            if (record != null && p3 != null) {
                EclipseRoles.hurt(p3, record, m);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void heal(LivingHealEvent e) {
        EclipseState.Metric metric;
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        ShadowRecord r = ShadowService.record((Entity)e.getEntity());
        ServerPlayer p = ShadowService.owner((Entity)e.getEntity());
        if (r != null && p != null && (metric = EclipseService.metric(p, r.id)) != null) {
            metric.healing += (double)Math.max(0.0f, Math.min(e.getAmount(), e.getEntity().m_21233_() - e.getEntity().m_21223_()));
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void death(LivingDeathEvent e) {
        Slime slime;
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)livingEntity;
            if (EclipseRuns.session(p) != null) {
                EclipseRuns.fail(p, "defeat");
            }
            EclipseService.clearRuntime(p);
        }
        if ((livingEntity = e.getEntity()) instanceof Slime && AdventureWorld.isNpc((Entity)(slime = (Slime)livingEntity))) {
            slime.m_7839_(1, false);
        }
    }
}

