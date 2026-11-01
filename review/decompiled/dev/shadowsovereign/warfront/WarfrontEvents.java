/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.OwnableEntity
 *  net.minecraft.world.entity.monster.Slime
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.event.entity.EntityMobGriefingEvent
 *  net.minecraftforge.event.entity.EntityTravelToDimensionEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingConversionEvent$Pre
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingTickEvent
 *  net.minecraftforge.event.entity.living.LivingExperienceDropEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 *  net.minecraftforge.event.level.ExplosionEvent$Detonate
 *  net.minecraftforge.event.server.ServerStoppingEvent
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.warfront;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.network.WarfrontNetwork;
import dev.shadowsovereign.warfront.WarfrontActors;
import dev.shadowsovereign.warfront.WarfrontBuilds;
import dev.shadowsovereign.warfront.WarfrontPlans;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class WarfrontEvents {
    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void join(EntityJoinLevelEvent e) {
        if (!e.getLevel().f_46443_ && WarfrontActors.illusion(e.getEntity()) && !WarfrontActors.valid(e.getEntity())) {
            e.setCanceled(true);
            e.getEntity().m_146870_();
        }
    }

    @SubscribeEvent
    public static void leave(EntityLeaveLevelEvent e) {
        Entity entity = e.getEntity();
        if (!e.getLevel().f_46443_ && WarfrontActors.illusion(entity) && (entity.m_146911_() == Entity.RemovalReason.KILLED || entity.m_146911_() == Entity.RemovalReason.DISCARDED)) {
            ArmyData.get((MinecraftServer)entity.m_20194_()).army((UUID)entity.getPersistentData().m_128342_((String)"SoulboundIllusionOwner")).warfront.illusions.remove(entity.m_20148_());
            ArmyData.get(entity.m_20194_()).m_77762_();
        }
    }

    @SubscribeEvent
    public static void tick(LivingEvent.LivingTickEvent e) {
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        WarfrontBuilds.cleanupSunder(e.getEntity());
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof Mob) {
            Mob m = (Mob)livingEntity;
            WarfrontActors.tick(m);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void protect(LivingAttackEvent e) {
        Mob m;
        AdventureData.Session s;
        Entity npcActor;
        AdventureData.Session s2;
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        if (WarfrontActors.protect(e)) {
            return;
        }
        LivingEntity victim = e.getEntity();
        Entity actor = e.getSource().m_7639_();
        Entity direct = e.getSource().m_7640_();
        if (AdventureWorld.isNpc((Entity)victim) && (s2 = AdventureWorld.session((Entity)victim)) != null && s2.kind == AdventureData.Kind.WARFRONT) {
            ShadowRecord incoming = CompanionProgression.attacker(e.getSource());
            if (incoming != null && !s2.participants.contains(incoming.id)) {
                e.setCanceled(true);
                return;
            }
            if (actor instanceof OwnableEntity && !ShadowService.isShadow(actor)) {
                e.setCanceled(true);
                return;
            }
        }
        Entity entity = npcActor = direct != null && AdventureWorld.isNpc(direct) ? direct : actor;
        if (npcActor != null && AdventureWorld.isNpc(npcActor) && ShadowService.isShadow((Entity)victim) && (s = AdventureWorld.session(npcActor)) != null && s.kind == AdventureData.Kind.WARFRONT && victim.getPersistentData().m_128403_("ShadowSovereignRecord") && !s.participants.contains(victim.getPersistentData().m_128342_("ShadowSovereignRecord"))) {
            e.setCanceled(true);
            return;
        }
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof Mob && AdventureWorld.isNpc((Entity)(m = (Mob)livingEntity))) {
            AdventureData.Session s3 = AdventureWorld.session((Entity)m);
            AdventureData.Npc n = AdventureWorld.npc((Entity)m);
            if (s3 != null && s3.kind == AdventureData.Kind.WARFRONT && n != null && s3.warfront.m_128403_("Marked") && n.id.equals(s3.warfront.m_128342_("Marked")) && s3.warfront.m_128454_("GraceUntil") > m.m_20194_().m_129783_().m_46467_()) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public static void hurt(LivingHurtEvent e) {
        ShadowRecord r;
        Mob m;
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        LivingEntity victim = e.getEntity();
        if (victim instanceof Mob && ShadowService.isShadow((Entity)(m = (Mob)victim)) && ShadowService.valid(m, r = ShadowService.record((Entity)m))) {
            e.setAmount(WarfrontBuilds.ward(m, r, e.getAmount()));
        }
        if (AdventureWorld.isNpc((Entity)victim)) {
            AdventureData.Session s = AdventureWorld.session((Entity)victim);
            if (s == null || s.kind != AdventureData.Kind.WARFRONT) {
                return;
            }
            boolean ranged = e.getSource().m_269533_(DamageTypeTags.f_268524_);
            String adaptation = s.warfront.m_128461_("Adaptation");
            if (victim.f_19797_ % 180 < 60 && (ranged && adaptation.equals("WINDGUARD") || !ranged && adaptation.equals("REPRISAL"))) {
                e.setAmount(e.getAmount() * 0.75f);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void damage(LivingDamageEvent e) {
        Mob m;
        if (e.getEntity().m_9236_().f_46443_ || e.getAmount() <= 0.0f) {
            return;
        }
        LivingEntity victim = e.getEntity();
        if (WarfrontActors.illusion((Entity)victim)) {
            e.setAmount(Math.max(e.getAmount(), victim.m_21223_()));
            return;
        }
        ShadowRecord record = CompanionProgression.attacker(e.getSource());
        Entity source = CompanionProgression.source(e.getSource());
        if (record != null && source != null && (m = ShadowService.active(source.m_20194_(), record)) != null) {
            WarfrontBuilds.sunder(m, record, victim);
        }
        if (AdventureWorld.isNpc((Entity)victim)) {
            AdventureData.Session s = AdventureWorld.session((Entity)victim);
            AdventureData.Npc n = AdventureWorld.npc((Entity)victim);
            if (s != null && s.kind == AdventureData.Kind.WARFRONT && n != null) {
                CompoundTag w = s.warfront;
                w.m_128347_("TotalDamage", w.m_128459_("TotalDamage") + (double)e.getAmount());
                if (e.getSource().m_269533_(DamageTypeTags.f_268524_)) {
                    w.m_128347_("RangedDamage", w.m_128459_("RangedDamage") + (double)e.getAmount());
                }
                if (w.m_128451_("Mission") == 3 && w.m_128403_("Marked") && w.m_128342_("Marked").equals(n.id) && !w.m_128471_("PhaseTwo") && victim.m_21223_() - e.getAmount() <= victim.m_21233_() / 2.0f) {
                    e.setAmount(Math.max(0.0f, victim.m_21223_() - victim.m_21233_() / 2.0f));
                    w.m_128379_("PhaseTwo", true);
                    w.m_128356_("GraceUntil", victim.m_20194_().m_129783_().m_46467_() + 60L);
                    ServerPlayer p = AdventureWorld.owner((Entity)victim);
                    if (p != null) {
                        WarfrontService.tell(p, "The commander changes stance! Reinforcements are coming.");
                    }
                }
            }
        }
        if (victim instanceof Mob && ShadowService.isShadow((Entity)(m = (Mob)victim))) {
            LivingEntity target;
            ShadowRecord r = ShadowService.record((Entity)m);
            ServerPlayer p = ShadowService.owner((Entity)m);
            Entity enemy = e.getSource().m_7639_();
            if (r != null && p != null && WarfrontBuilds.rune(m, r) == WarfrontState.Rune.VIGIL && enemy instanceof LivingEntity && ShadowService.canAttack(p, target = (LivingEntity)enemy)) {
                WarfrontState.Build b = WarfrontService.state(p).build(r.id);
                b.vigilTarget = target.m_20148_();
                b.vigilUntil = WarfrontService.now(p) + (long)(EclipseService.state(p).talent(r.id, EclipseState.Talent.VIGIL_PLUS) ? 200 : 160);
                b.targetDimension = target.m_9236_().m_46472_().m_135782_().toString();
                WarfrontService.dirty(p);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void death(LivingDeathEvent e) {
        ServerPlayer p;
        LivingEntity livingEntity;
        if (WarfrontActors.illusion((Entity)e.getEntity()) && (livingEntity = e.getEntity()) instanceof Slime) {
            Slime slime = (Slime)livingEntity;
            slime.m_7839_(1, false);
        }
        if ((livingEntity = e.getEntity()) instanceof ServerPlayer && WarfrontService.session(p = (ServerPlayer)livingEntity) != null) {
            WarfrontService.fail(p, "You fell in battle.");
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void drops(LivingDropsEvent e) {
        if (WarfrontActors.illusion((Entity)e.getEntity())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void xp(LivingExperienceDropEvent e) {
        if (WarfrontActors.illusion((Entity)e.getEntity())) {
            e.setDroppedExperience(0);
        }
    }

    @SubscribeEvent
    public static void grief(EntityMobGriefingEvent e) {
        if (WarfrontActors.illusion(e.getEntity())) {
            e.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void explosion(ExplosionEvent.Detonate e) {
        Entity direct = e.getExplosion().m_253049_();
        LivingEntity indirect = e.getExplosion().m_252906_();
        if (direct != null && WarfrontActors.illusion(direct) || indirect != null && WarfrontActors.illusion((Entity)indirect)) {
            e.getAffectedBlocks().clear();
            e.getAffectedEntities().clear();
        }
    }

    @SubscribeEvent
    public static void convert(LivingConversionEvent.Pre e) {
        if (WarfrontActors.illusion((Entity)e.getEntity())) {
            e.setCanceled(true);
            e.setConversionTimer(600);
        }
    }

    @SubscribeEvent
    public static void portal(EntityTravelToDimensionEvent e) {
        if (WarfrontActors.illusion(e.getEntity())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void interact(PlayerInteractEvent.EntityInteract e) {
        if (WarfrontActors.illusion(e.getTarget())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void interactSpecific(PlayerInteractEvent.EntityInteractSpecific e) {
        if (WarfrontActors.illusion(e.getTarget())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            WarfrontBuilds.clearTransient(p);
        }
    }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            WarfrontBuilds.clearTransient(p);
        }
    }

    @SubscribeEvent
    public static void server(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
            if (p.f_19797_ % 5 == 0) {
                WarfrontPlans.tick(p);
            }
            if (p.f_19797_ % 20 != 0) continue;
            WarfrontService.reconcile(p);
            WarfrontState w = WarfrontService.state(p);
            if (WarfrontService.session(p) == null && p.getPersistentData().m_128454_("WarfrontViewUntil") <= WarfrontService.now(p) && w.execution == null && !w.builds.values().stream().anyMatch(b -> b.vigilTarget != null && b.vigilUntil > WarfrontService.now(p))) continue;
            WarfrontNetwork.send(p, false, null);
        }
    }

    @SubscribeEvent
    public static void stopping(ServerStoppingEvent e) {
        for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
            WarfrontBuilds.clearTransient(p);
        }
    }
}

