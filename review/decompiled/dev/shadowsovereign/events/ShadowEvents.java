/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.monster.Creeper
 *  net.minecraft.world.entity.monster.Slime
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.event.entity.EntityMobGriefingEvent
 *  net.minecraftforge.event.entity.EntityTravelToDimensionEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingChangeTargetEvent
 *  net.minecraftforge.event.entity.living.LivingConversionEvent$Pre
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingTickEvent
 *  net.minecraftforge.event.entity.living.LivingExperienceDropEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$StartTracking
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 *  net.minecraftforge.event.level.ExplosionEvent$Detonate
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.events;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.WardenCompatibility;
import dev.shadowsovereign.eclipse.EclipseRoles;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.SoulDevourer;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.realm.WildBosses;
import dev.shadowsovereign.sentinel.SentinelService;
import dev.shadowsovereign.soulforge.CompanionMenu;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.warfront.WarfrontBuilds;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class ShadowEvents {
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void death(LivingDeathEvent event) {
        ServerPlayer owner;
        ServerPlayer p;
        LivingEntity victim = event.getEntity();
        if (victim.m_9236_().f_46443_) {
            return;
        }
        CompanionProgression.killed(victim, event.getSource());
        if (!(victim instanceof Mob)) {
            return;
        }
        Mob mob = (Mob)victim;
        if (ShadowService.isShadow((Entity)mob)) {
            ShadowService.shadowDied(mob);
            if (mob instanceof Slime) {
                Slime slime = (Slime)mob;
                slime.m_7839_(1, false);
            }
            return;
        }
        Entity killer = event.getSource().m_7639_();
        if (killer == null) {
            killer = event.getSource().m_7640_();
        }
        ServerPlayer serverPlayer = killer instanceof ServerPlayer ? (p = (ServerPlayer)killer) : (owner = killer != null ? ShadowService.owner(killer) : null);
        if (mob instanceof MythicBeast) {
            MythicBeast knight = (MythicBeast)mob;
            if (false) {
                Object object = owner != null ? owner.m_20148_() : (killer == null ? null : ShadowService.ownerId(killer));
                return;
            }
        }
        if (mob instanceof MythicBeast) {
            MythicBeast beast = (MythicBeast)mob;
            if (mob.getPersistentData().m_128403_("SoulboundMythicHost")) {
                MythicExpedition.defeated(beast, (UUID)(owner != null ? owner.m_20148_() : (killer == null ? null : ShadowService.ownerId(killer))));
                return;
            }
        }
        if (mob instanceof SoulDevourer) {
            SoulDevourer mythic = (SoulDevourer)mob;
            if (mob.getPersistentData().m_128403_("SoulboundMythicHost")) {
                MythicService.defeated(mythic, (UUID)(owner != null ? owner.m_20148_() : (killer == null ? null : ShadowService.ownerId(killer))));
                return;
            }
        }
        if (owner != null && !WildBosses.capture(owner, mob)) {
            ShadowService.recruit(owner, mob);
        } else if (owner == null && killer != null && ShadowService.ownerId(killer) != null && CompanionProgression.attacker(event.getSource()) != null) {
            WildBosses.captureOffline(mob.m_20194_(), ShadowService.ownerId(killer), mob);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void drops(LivingDropsEvent event) {
        if (ShadowService.isShadow((Entity)event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void experience(LivingExperienceDropEvent event) {
        if (ShadowService.isShadow((Entity)event.getEntity())) {
            event.setDroppedExperience(0);
        }
    }

    @SubscribeEvent
    public static void conversion(LivingConversionEvent.Pre event) {
        if (ShadowService.isShadow((Entity)event.getEntity())) {
            event.setCanceled(true);
            event.setConversionTimer(600);
        }
    }

    @SubscribeEvent
    public static void grief(EntityMobGriefingEvent event) {
        if (ShadowService.isShadow(event.getEntity())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void explosion(ExplosionEvent.Detonate event) {
        LivingEntity source;
        Entity direct = event.getExplosion().m_253049_();
        LivingEntity indirect = event.getExplosion().m_252906_();
        Object object = source = direct != null && ShadowService.isShadow(direct) ? direct : indirect;
        if (source == null || !ShadowService.isShadow((Entity)source)) {
            return;
        }
        event.getAffectedBlocks().clear();
        ServerPlayer owner = ShadowService.owner((Entity)source);
        event.getAffectedEntities().removeIf(e -> {
            LivingEntity living;
            return owner == null || !(e instanceof LivingEntity) || !ShadowService.canAttack(owner, living = (LivingEntity)e);
        });
    }

    @SubscribeEvent
    public static void portal(EntityTravelToDimensionEvent event) {
        if (ShadowService.isShadow(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void target(LivingChangeTargetEvent event) {
        if (event.getEntity().m_9236_().f_46443_ || !ShadowService.isShadow((Entity)event.getEntity()) || event.getNewTarget() == null) {
            return;
        }
        ServerPlayer owner = ShadowService.owner((Entity)event.getEntity());
        ShadowRecord r = ShadowService.record((Entity)event.getEntity());
        if (owner == null || r == null || !SentinelService.targetAllowed((Mob)event.getEntity(), r, owner, event.getNewTarget()) || r.order == Order.STAY && !VigilCrown.member(owner, r) && !MythicService.fighting(owner, r) || !ShadowService.canAttack(owner, event.getNewTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void power(LivingHurtEvent event) {
        if (event.getEntity().m_9236_().f_46443_) {
            return;
        }
        ShadowRecord r = CompanionProgression.attacker(event.getSource());
        if (r != null && event.getAmount() > 0.0f) {
            event.setAmount((float)((double)event.getAmount() * r.damageMultiplier() * r.tier.power));
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void bondDefense(LivingHurtEvent event) {
        if (event.getEntity().m_9236_().f_46443_) {
            return;
        }
        ShadowRecord r = ShadowService.record((Entity)event.getEntity());
        if (r != null && event.getAmount() > 0.0f) {
            event.setAmount(CompanionService.incoming(r, event.getAmount()));
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void protect(LivingAttackEvent event) {
        DamageSource damage;
        Mob caster;
        ServerPlayer owner;
        if (event.getEntity().m_9236_().f_46443_) {
            return;
        }
        Entity source = event.getSource().m_7639_();
        Entity direct = event.getSource().m_7640_();
        if (direct != null && ShadowService.isShadow(direct)) {
            source = direct;
        }
        LivingEntity victim = event.getEntity();
        if (source != null && ShadowService.isShadow(source) && ((owner = ShadowService.owner(source)) == null || !ShadowService.canAttack(owner, victim) || source instanceof Mob && !SentinelService.targetAllowed(caster = (Mob)source, ShadowService.record((Entity)caster), owner, victim))) {
            event.setCanceled(true);
            return;
        }
        if (!ShadowService.isShadow((Entity)victim)) {
            return;
        }
        owner = ShadowService.owner((Entity)victim);
        if (source != null && owner != null && ShadowService.allied(owner, source)) {
            event.setCanceled(true);
            return;
        }
        if (((Boolean)ShadowConfig.ENVIRONMENT_PROTECTION.get()).booleanValue() && ((damage = event.getSource()).m_276093_(DamageTypes.f_268722_) || damage.m_276093_(DamageTypes.f_268612_) || damage.m_276093_(DamageTypes.f_268631_) || damage.m_276093_(DamageTypes.f_268468_) || damage.m_276093_(DamageTypes.f_268546_) || damage.m_276093_(DamageTypes.f_268434_) || damage.m_276093_(DamageTypes.f_268671_) || damage.m_276093_(DamageTypes.f_268752_) || damage.m_276093_(DamageTypes.f_268444_) || damage.m_276093_(DamageTypes.f_268613_))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void join(EntityJoinLevelEvent event) {
        Mob mob;
        Projectile projectile;
        Entity shooter;
        Entity entity;
        if (!event.getLevel().f_46443_ && (entity = event.getEntity()) instanceof Projectile && (shooter = (projectile = (Projectile)entity).m_19749_()) != null && ShadowService.isShadow(shooter)) {
            projectile.getPersistentData().m_128362_("ShadowSovereignOwner", ShadowService.ownerId(shooter));
            if (shooter.getPersistentData().m_128403_("ShadowSovereignRecord")) {
                projectile.getPersistentData().m_128362_("ShadowSovereignRecord", shooter.getPersistentData().m_128342_("ShadowSovereignRecord"));
            }
        }
        if (event.getLevel().f_46443_ || !((shooter = event.getEntity()) instanceof Mob) || !ShadowService.isShadow((Entity)(mob = (Mob)shooter))) {
            return;
        }
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (!ShadowService.valid(mob, r)) {
            event.setCanceled(true);
            mob.m_146870_();
            return;
        }
        ShadowService.initialize(mob);
    }

    @SubscribeEvent
    public static void leave(EntityLeaveLevelEvent event) {
        Mob mob;
        Entity entity;
        if (event.getLevel().f_46443_ || !((entity = event.getEntity()) instanceof Mob) || !ShadowService.isShadow((Entity)(mob = (Mob)entity))) {
            return;
        }
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (ShadowService.valid(mob, r)) {
            Creeper c;
            Entity.RemovalReason reason = mob.m_146911_();
            if (reason == Entity.RemovalReason.DISCARDED && mob.m_21223_() > 0.0f && (!(mob instanceof Creeper) || !(c = (Creeper)mob).m_32311_() && c.m_32310_() <= 0)) {
                ShadowService.updateSnapshot(mob, r);
                r.activeEntity = null;
                ArmyData.get(mob.m_20194_()).m_77762_();
            } else if (reason == Entity.RemovalReason.KILLED || reason == Entity.RemovalReason.DISCARDED) {
                ShadowService.shadowDied(mob);
            } else if (mob.m_21223_() > 0.0f) {
                ShadowService.updateSnapshot(mob, r);
            }
        }
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event) {
        Mob mob;
        block11: {
            block10: {
                LivingEntity livingEntity = event.getEntity();
                if (!(livingEntity instanceof Mob)) break block10;
                mob = (Mob)livingEntity;
                if (!mob.m_9236_().f_46443_ && ShadowService.isShadow((Entity)mob) && mob.m_6084_()) break block11;
            }
            return;
        }
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (!ShadowService.valid(mob, r)) {
            mob.m_146870_();
            return;
        }
        WardenCompatibility.tick(mob);
        CompanionProgression.tickHealing(mob, r);
        if (mob.f_19797_ % 5 == 0) {
            EclipseRoles.tick(mob, r);
        }
        if (mob.f_19797_ % 5 == 0) {
            WarfrontBuilds.tick(mob, r);
        }
        if (mob.f_19797_ % 10 == 0) {
            CompanionService.collect(mob, r);
        }
        if (((Boolean)ShadowConfig.ENVIRONMENT_PROTECTION.get()).booleanValue()) {
            mob.m_20095_();
            mob.m_20301_(mob.m_6062_());
        }
        if (mob.f_19797_ % 100 == 0) {
            ShadowService.updateSnapshot(mob, r);
            ServerPlayer owner = ShadowService.owner((Entity)mob);
            mob.m_21530_();
        }
        if (mob.f_19797_ % 15 == 0) {
            ShadowService.particles(mob, 2);
        }
    }

    @SubscribeEvent
    public static void interact(PlayerInteractEvent.EntityInteract event) {
        ShadowEvents.blockInteraction((PlayerInteractEvent)event);
    }

    @SubscribeEvent
    public static void interactSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        ShadowEvents.blockInteraction((PlayerInteractEvent)event);
    }

    private static void blockInteraction(PlayerInteractEvent event) {
        ServerPlayer player;
        MythicBeast cat;
        Entity target;
        if (event instanceof PlayerInteractEvent.EntityInteract) {
            PlayerInteractEvent.EntityInteract e = (PlayerInteractEvent.EntityInteract)event;
            v0 = e.getTarget();
        } else {
            v0 = target = ((PlayerInteractEvent.EntityInteractSpecific)event).getTarget();
        }
        if (!ShadowService.isShadow(target)) {
            return;
        }
        if (target instanceof MythicBeast && (cat = (MythicBeast)target).profile().rideable() && event.getEntity().m_20148_().equals(ShadowService.ownerId((Entity)cat)) && !event.getEntity().m_6144_() && event.getEntity().m_21205_().m_41619_()) {
            return;
        }
        event.setCanceled(true);
        Player player2 = event.getEntity();
        if (player2 instanceof ServerPlayer && (player = (ServerPlayer)player2).m_20148_().equals(ShadowService.ownerId(target))) {
            if (player.m_6144_() && target.getPersistentData().m_128403_("ShadowSovereignRecord")) {
                if (!(player.f_36096_ instanceof CompanionMenu)) {
                    CompanionService.open(player, target.getPersistentData().m_128342_("ShadowSovereignRecord"));
                }
            } else {
                ShadowService.sync(player, true);
            }
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
    }

    @SubscribeEvent
    public static void tracking(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            if (ShadowService.isShadow(event.getTarget())) {
                ShadowNetwork.appearance(player2, event.getTarget());
            }
        }
    }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            SentinelService.changedDimension(player2);
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        Player player;
        if (event.phase == TickEvent.Phase.END && (player = event.player) instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            if (p.f_19797_ % 10 == 0) {
                SentinelService.playerTick(p);
            }
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ShadowService.tickRespawns(event.getServer());
        }
    }
}

