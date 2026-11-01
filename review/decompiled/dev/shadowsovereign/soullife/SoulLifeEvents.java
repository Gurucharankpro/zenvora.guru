/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingTickEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.server.ServerStoppedEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.soullife;

import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.network.SoulLifeNetwork;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.soullife.SoulLifeState;
import dev.shadowsovereign.soullife.SoulPlay;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class SoulLifeEvents {
    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
            SoulLifeService.tick(p);
            SoulLifeState.Owner o = SoulLifeService.owner(p);
            if (SoulLifeService.now(p) % 20L != 0L || o.viewUntil <= SoulLifeService.now(p)) continue;
            SoulLifeNetwork.send(p, false, 0);
        }
    }

    @SubscribeEvent
    public static void itemJoined(EntityJoinLevelEvent e) {
        ItemEntity item;
        Entity entity;
        if (!e.getLevel().f_46443_ && (entity = e.getEntity()) instanceof ItemEntity && (item = (ItemEntity)entity).getPersistentData().m_128403_("SoulLifeGame")) {
            SoulPlay.queueToy(item);
        }
    }

    @SubscribeEvent
    public static void stopped(ServerStoppedEvent e) {
        SoulPlay.clearToyQueue();
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void living(LivingEvent.LivingTickEvent e) {
        Mob m;
        LivingEntity livingEntity;
        if (e.getEntity().m_9236_().f_46443_ || !((livingEntity = e.getEntity()) instanceof Mob) || !ShadowService.isShadow((Entity)(m = (Mob)livingEntity)) || !m.m_6084_()) {
            return;
        }
        ServerPlayer p = ShadowService.owner((Entity)m);
        ShadowRecord r = ShadowService.record((Entity)m);
        if (p != null && ShadowService.valid(m, r)) {
            SoulLifeService.voidRescue(p, r, m);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void hurt(LivingDamageEvent e) {
        if (e.getAmount() <= 0.0f) return;
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)livingEntity;
            if (!SoulLifeService.owner((ServerPlayer)p).activity.m_128456_()) {
                SoulPlay.cancel(p, true);
                return;
            }
        }
        if (!((livingEntity = e.getEntity()) instanceof Mob)) return;
        Mob m = (Mob)livingEntity;
        ServerPlayer p = ShadowService.owner((Entity)m);
        ShadowRecord r = ShadowService.record((Entity)m);
        if (p == null) return;
        if (r == null) return;
        if (!SoulPlay.controlled(p, r)) return;
        SoulPlay.cancel(p, true);
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            SoulPlay.cancel(p, false);
            SoulLifeState.Owner o = SoulLifeService.owner(p);
            o.lastView = null;
            o.viewUntil = 0L;
            for (ShadowRecord r : SoulLifeService.army((ServerPlayer)p).shadows.values()) {
                r.life.lastPlayer = null;
                r.life.lastMob = null;
                r.life.sampledAt = -1L;
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            SoulPlay.cancel(p, false);
            SoulLifeState.Owner o = SoulLifeService.owner(p);
            o.lastView = null;
            SoulLifeNetwork.send(p, false, 0);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            SoulPlay.cancel(p, false);
            SoulLifeService.owner((ServerPlayer)p).lastView = null;
        }
    }
}

