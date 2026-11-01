/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraftforge.event.TickEvent$Phase
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
package dev.shadowsovereign.adventure;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.AscensionActors;
import dev.shadowsovereign.warfront.WarfrontActors;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class AdventureEvents {
    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void join(EntityJoinLevelEvent event) {
        Mob mob;
        Projectile projectile;
        if (event.getLevel().f_46443_) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof Projectile && (projectile = (Projectile)entity).m_19749_() != null && AdventureWorld.isNpc(projectile.m_19749_())) {
            CompoundTag from = projectile.m_19749_().getPersistentData();
            CompoundTag to = entity.getPersistentData();
            for (String k : List.of("SoulboundAdventureOwner", "SoulboundAdventureSession", "SoulboundAdventureNpc")) {
                if (!from.m_128403_(k)) continue;
                to.m_128362_(k, from.m_128342_(k));
            }
            to.m_128405_("SoulboundAdventureEpoch", from.m_128451_("SoulboundAdventureEpoch"));
            to.m_128359_("SoulboundAdventureRole", from.m_128461_("SoulboundAdventureRole"));
        }
        if (entity instanceof Mob && AdventureWorld.isNpc((Entity)(mob = (Mob)entity))) {
            if (!AdventureWorld.valid((Entity)mob)) {
                event.setCanceled(true);
                mob.m_146870_();
                return;
            }
            AdventureWorld.decorate(mob, AdventureWorld.npc((Entity)mob).role);
        }
    }

    @SubscribeEvent
    public static void leave(EntityLeaveLevelEvent event) {
        Mob mob;
        Entity entity;
        if (!event.getLevel().f_46443_ && (entity = event.getEntity()) instanceof Mob && AdventureWorld.isNpc((Entity)(mob = (Mob)entity))) {
            AdventureWorld.npcLeft(mob);
        }
    }

    @SubscribeEvent
    public static void living(LivingEvent.LivingTickEvent event) {
        Mob mob;
        LivingEntity livingEntity;
        if (!event.getEntity().m_9236_().f_46443_ && (livingEntity = event.getEntity()) instanceof Mob && AdventureWorld.isNpc((Entity)(mob = (Mob)livingEntity))) {
            if (!AdventureWorld.valid((Entity)mob)) {
                mob.m_146870_();
            } else {
                AscensionActors.tick(mob);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void protect(LivingAttackEvent event) {
        DamageSource damage;
        boolean hostileActor;
        Entity actor;
        if (event.getEntity().m_9236_().f_46443_) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (WarfrontActors.protect(event)) {
            return;
        }
        if (AscensionActors.protect(event)) {
            return;
        }
        Entity source = event.getSource().m_7639_();
        Entity direct = event.getSource().m_7640_();
        Entity entity = actor = direct != null && AdventureWorld.isNpc(direct) ? direct : source;
        if (actor != null && AdventureWorld.isNpc(actor)) {
            boolean targetAllowed;
            AdventureData.Session s = AdventureWorld.session(actor);
            CompoundTag data = actor.getPersistentData();
            UUID owner = data.m_128342_("SoulboundAdventureOwner");
            if (s == null || s.phase != AdventureData.Phase.ACTIVE || !data.m_128403_("SoulboundAdventureSession") || !s.id.equals(data.m_128342_("SoulboundAdventureSession")) || s.epoch != data.m_128451_("SoulboundAdventureEpoch")) {
                event.setCanceled(true);
                return;
            }
            boolean bl = targetAllowed = victim.m_20148_().equals(owner) || owner.equals(ShadowService.ownerId((Entity)victim)) || AdventureWorld.isNpc((Entity)victim) && owner.equals(victim.getPersistentData().m_128342_("SoulboundAdventureOwner")) && victim.getPersistentData().m_128461_("SoulboundAdventureRole").equals(AdventureData.Role.ESCORT.name());
            if (!targetAllowed) {
                event.setCanceled(true);
                return;
            }
        }
        if (!AdventureWorld.isNpc((Entity)victim)) {
            return;
        }
        if (!AdventureWorld.valid((Entity)victim)) {
            event.setCanceled(true);
            return;
        }
        UUID owner = victim.getPersistentData().m_128342_("SoulboundAdventureOwner");
        AdventureData.Npc n = AdventureWorld.npc((Entity)victim);
        UUID incoming = AdventureService.damageOwner(event.getSource());
        if (!(source == null || incoming != null && incoming.equals(owner))) {
            event.setCanceled(true);
            return;
        }
        boolean bl = hostileActor = actor != null && AdventureWorld.isNpc(actor) && actor.getPersistentData().m_128461_("SoulboundAdventureRole").equals(AdventureData.Role.ENEMY.name());
        if (n.role == AdventureData.Role.ESCORT && owner.equals(incoming) && !hostileActor) {
            event.setCanceled(true);
            return;
        }
        if (n.role == AdventureData.Role.ENEMY && ((damage = event.getSource()).m_276093_(DamageTypes.f_268631_) || damage.m_276093_(DamageTypes.f_268468_) || damage.m_276093_(DamageTypes.f_268722_) || damage.m_276093_(DamageTypes.f_268612_))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void rescue(LivingDeathEvent event) {
        Mob mob;
        LivingEntity livingEntity;
        if (!event.getEntity().m_9236_().f_46443_ && (livingEntity = event.getEntity()) instanceof Mob && AdventureWorld.isNpc((Entity)(mob = (Mob)livingEntity)) && AdventureWorld.rescue(mob)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void death(LivingDeathEvent event) {
        if (event.getEntity().m_9236_().f_46443_) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (victim instanceof Mob) {
            Mob mob = (Mob)victim;
            if (AdventureWorld.isNpc((Entity)mob)) {
                AdventureWorld.npcDied(mob);
                return;
            }
            if (ShadowService.isShadow((Entity)mob)) {
                AdventureService.fallen(mob, event.getSource());
            }
        }
        AdventureService.killed(victim, event.getSource());
        if (victim instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)victim;
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void drops(LivingDropsEvent event) {
        if (AdventureWorld.isNpc((Entity)event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void xp(LivingExperienceDropEvent event) {
        if (AdventureWorld.isNpc((Entity)event.getEntity())) {
            event.setDroppedExperience(0);
        }
    }

    @SubscribeEvent
    public static void grief(EntityMobGriefingEvent event) {
        if (AdventureWorld.isNpc(event.getEntity())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void explosion(ExplosionEvent.Detonate event) {
        Entity direct = event.getExplosion().m_253049_();
        LivingEntity indirect = event.getExplosion().m_252906_();
        if (direct != null && AdventureWorld.isNpc(direct) || indirect != null && AdventureWorld.isNpc((Entity)indirect)) {
            event.getAffectedBlocks().clear();
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void target(LivingChangeTargetEvent event) {
        Mob mob;
        LivingEntity livingEntity;
        if (!event.getEntity().m_9236_().f_46443_ && (livingEntity = event.getEntity()) instanceof Mob && AdventureWorld.isNpc((Entity)(mob = (Mob)livingEntity)) && event.getNewTarget() != null && !AdventureWorld.allowedTarget(mob, event.getNewTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void portal(EntityTravelToDimensionEvent event) {
        if (AdventureWorld.isNpc(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void conversion(LivingConversionEvent.Pre event) {
        if (AdventureWorld.isNpc((Entity)event.getEntity())) {
            event.setCanceled(true);
            event.setConversionTimer(600);
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
        }
    }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            AdventureService.tick(event.getServer());
        }
    }

    @SubscribeEvent
    public static void stopping(ServerStoppingEvent event) {
        for (UUID owner : List.copyOf(AdventureData.get((MinecraftServer)event.getServer()).owners.keySet())) {
            AdventureWorld.pause(event.getServer(), owner);
        }
    }

    @SubscribeEvent
    public static void interact(PlayerInteractEvent.EntityInteract event) {
        if (AdventureWorld.isNpc(event.getTarget())) {
            ServerPlayer p;
            event.setCanceled(true);
            Player player = event.getEntity();
            if (player instanceof ServerPlayer && (p = (ServerPlayer)player).m_20148_().equals(event.getTarget().getPersistentData().m_128342_("SoulboundAdventureOwner"))) {
                AdventureService.tell(p, "Lost Souls cannot be tamed, bred, traded or recruited. Escort it home and protect it.");
            }
        }
    }

    @SubscribeEvent
    public static void interactSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (AdventureWorld.isNpc(event.getTarget())) {
            event.setCanceled(true);
        }
    }
}

