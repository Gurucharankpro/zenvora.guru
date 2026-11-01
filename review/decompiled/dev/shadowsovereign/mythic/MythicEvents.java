/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.EvokerFangs
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.server.ServerStoppingEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.expansion.OpenMythics;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.SoulDevourer;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class MythicEvents {
    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer p : server.m_6846_().m_11314_()) {
            MythicService.tick(p);
            if (p.f_19797_ % 40 != 0 || MythicService.state((ServerPlayer)p).viewing < GuideService.now(p)) continue;
            MythicNetwork.send(p, false);
        }
    }

    @SubscribeEvent
    public static void stopping(ServerStoppingEvent e) {
        for (UUID owner : List.copyOf(AdventureData.get((MinecraftServer)e.getServer()).owners.keySet())) {
            MythicService.pause(e.getServer(), owner);
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            MythicService.pause(p.f_8924_, p.m_20148_());
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void attack(LivingAttackEvent e) {
        SoulDevourer m;
        Entity entity;
        if (!e.getEntity().m_9236_().f_46443_ && (entity = e.getSource().m_7639_()) instanceof SoulDevourer && !(m = (SoulDevourer)entity).allowed(e.getEntity())) {
            e.setCanceled(true);
        }
    }

    public static void clearFangs(ServerLevel level, UUID caster) {
        ArrayList<Entity> shots = new ArrayList<Entity>();
        level.m_8583_().forEach(e -> {
            if (e instanceof EvokerFangs && e.getPersistentData().m_128403_("MythicCaster") && caster.equals(e.getPersistentData().m_128342_("MythicCaster"))) {
                shots.add((Entity)e);
            }
        });
        shots.forEach(Entity::m_146870_);
    }

    @SubscribeEvent
    public static void leave(EntityLeaveLevelEvent e) {
        Entity entity;
        if (!e.getLevel().f_46443_ && (entity = e.getEntity()) instanceof SoulDevourer) {
            SoulDevourer m = (SoulDevourer)entity;
            entity = e.getLevel();
            if (entity instanceof ServerLevel) {
                ServerLevel w = (ServerLevel)entity;
                MythicEvents.clearFangs(w, m.m_20148_());
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void join(EntityJoinLevelEvent e) {
        SoulDevourer m;
        Object j;
        EvokerFangs f;
        Entity world;
        Entity actor;
        EvokerFangs owned;
        Entity entity;
        if (!(e.getLevel().f_46443_ || !((entity = e.getEntity()) instanceof EvokerFangs) || !(owned = (EvokerFangs)entity).getPersistentData().m_128403_("MythicCaster") || owned.getPersistentData().m_128403_("SoulboundMythicHost") || !((entity = e.getLevel()) instanceof ServerLevel) || (actor = (world = (ServerLevel)entity).m_8791_(owned.getPersistentData().m_128342_("MythicCaster"))) instanceof SoulDevourer && ShadowService.isShadow(actor) && ShadowService.valid((Mob)((SoulDevourer)actor), ShadowService.record(actor)) && ShadowService.owner(actor) != null)) {
            e.setCanceled(true);
            owned.m_146870_();
            return;
        }
        if (!e.getLevel().f_46443_ && (world = e.getEntity()) instanceof EvokerFangs && (f = (EvokerFangs)world).getPersistentData().m_128403_("SoulboundMythicHost")) {
            j = AdventureData.get(f.m_20194_()).journal(f.getPersistentData().m_128342_("SoulboundMythicHost"));
            if (j.mythic.active == null || !f.getPersistentData().m_128403_("MythicCaster") || !j.mythic.active.equals(f.getPersistentData().m_128342_("MythicCaster")) || OpenMythics.liveSession((AdventureData.Journal)j) == null || OpenMythics.liveSession((AdventureData.Journal)j).phase != AdventureData.Phase.ACTIVE) {
                e.setCanceled(true);
                f.m_146870_();
                return;
            }
        }
        if (e.getLevel().f_46443_ || !((j = e.getEntity()) instanceof SoulDevourer) || ShadowService.isShadow((Entity)(m = (SoulDevourer)((Object)j))) || !m.getPersistentData().m_128403_("SoulboundMythicHost")) {
            return;
        }
        j = AdventureData.get(m.m_20194_()).journal(m.getPersistentData().m_128342_("SoulboundMythicHost"));
        if (j.mythic.status == 2 || !m.m_20148_().equals(j.mythic.active) || OpenMythics.liveSession((AdventureData.Journal)j) == null || OpenMythics.liveSession((AdventureData.Journal)j).kind != AdventureData.Kind.MYTHIC_LAIR || OpenMythics.liveSession((AdventureData.Journal)j).phase != AdventureData.Phase.ACTIVE) {
            e.setCanceled(true);
            m.m_146870_();
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void death(LivingDeathEvent e) {
        UUID owner;
        SoulDevourer m;
        LivingEntity livingEntity;
        if (e.getEntity().m_9236_().f_46443_ || !((livingEntity = e.getEntity()) instanceof SoulDevourer) || ShadowService.isShadow((Entity)(m = (SoulDevourer)livingEntity)) || !m.getPersistentData().m_128403_("SoulboundMythicHost")) {
            return;
        }
        Entity killer = e.getSource().m_7639_();
        if (killer instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)killer;
            v0 = p.m_20148_();
        } else {
            v0 = owner = killer == null ? null : ShadowService.ownerId(killer);
        }
        if (owner == null || !owner.equals(m.getPersistentData().m_128342_("SoulboundMythicHost"))) {
            e.setCanceled(true);
            m.m_21153_(Math.max(1.0f, m.m_21233_() * 0.05f));
        }
    }
}

