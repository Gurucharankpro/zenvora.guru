/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityTeleportEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.AttackEntityEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.server.ServerStoppedEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.journey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.journey.JourneyActivities;
import dev.shadowsovereign.journey.JourneyCombat;
import dev.shadowsovereign.journey.JourneyService;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.legacy.LegacyService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class JourneyEvents {
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void died(LivingDeathEvent e) {
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        JourneyService.killed(e.getEntity(), e.getSource());
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)livingEntity;
            JourneyView.exit(p);
            AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
            if (s != null && JourneyService.isJourney(s.kind)) {
                JourneyService.fail(p, "Journey ended when you fell. Your companions retain their normal soul revival.");
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public static void damage(LivingHurtEvent e) {
        AdventureData.Session s;
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        Entity source = e.getSource().m_7639_();
        if (source != null && AdventureWorld.isNpc(source) && (s = AdventureWorld.session(source)) != null && (s.kind == AdventureData.Kind.BOSS || s.kind == AdventureData.Kind.RIFT) && !s.journey.m_128471_("Hard")) {
            e.setAmount(e.getAmount() * 0.75f);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void damaged(LivingDamageEvent e) {
        LivingEntity livingEntity;
        if (e.getAmount() > 0.0f && (livingEntity = e.getEntity()) instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)livingEntity;
            JourneyView.exit(p);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void teleport(EntityTeleportEvent e) {
        ServerPlayer player;
        ServerPlayer p;
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        Entity entity = e.getEntity();
        ServerPlayer serverPlayer = p = entity instanceof ServerPlayer ? (player = (ServerPlayer)entity) : ShadowService.owner(e.getEntity());
        if (p == null) {
            return;
        }
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s != null && s.kind == AdventureData.Kind.RALLY && s.phase == AdventureData.Phase.ACTIVE && (e.getEntity() == p || s.participants.contains(ShadowService.record(e.getEntity()) == null ? null : ShadowService.record((Entity)e.getEntity()).id))) {
            JourneyService.fail(p, "Teleporting ends this rally attempt.");
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
            boolean moved;
            Mob m;
            if (!p.m_6084_()) continue;
            JourneyService.ownerTick(p);
            AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
            if (s == null || s.phase != AdventureData.Phase.ACTIVE) continue;
            if (s.kind == AdventureData.Kind.ANGLING) {
                JourneyActivities.tick(p, s);
            }
            if (s.kind != AdventureData.Kind.RALLY || (m = LegacyService.eligible(p, s.journey.m_128342_("Ally"), 1, 32)) == null) continue;
            CompoundTag n = s.journey;
            boolean bl = moved = n.m_128441_("LastPlayer") && (p.m_20183_().m_123331_((Vec3i)BlockPos.m_122022_((long)n.m_128454_("LastPlayer"))) > 64.0 || m.m_20183_().m_123331_((Vec3i)BlockPos.m_122022_((long)n.m_128454_("LastAlly"))) > 64.0);
            if (moved) {
                JourneyService.fail(p, "Teleporting ends this rally attempt.");
                continue;
            }
            n.m_128356_("LastPlayer", p.m_20183_().m_121878_());
            n.m_128356_("LastAlly", m.m_20183_().m_121878_());
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            p.getPersistentData().m_128473_("JourneyView");
        }
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            p.getPersistentData().m_128473_("JourneyView");
        }
    }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            JourneyView.exit(p);
        }
    }

    @SubscribeEvent
    public static void interact(PlayerInteractEvent e) {
        ServerPlayer p;
        Player player = e.getEntity();
        if (player instanceof ServerPlayer && JourneyView.active(p = (ServerPlayer)player) && e.isCancelable()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void attack(AttackEntityEvent e) {
        ServerPlayer p;
        Player player = e.getEntity();
        if (player instanceof ServerPlayer && JourneyView.active(p = (ServerPlayer)player)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void stop(ServerStoppedEvent e) {
        JourneyCombat.clearBars();
    }
}

