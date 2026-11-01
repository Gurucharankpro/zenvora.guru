/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityTeleportEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingChangeTargetEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.vigil;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.network.VigilNetwork;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.vigil.VigilCombat;
import dev.shadowsovereign.vigil.VigilContent;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilRuns;
import dev.shadowsovereign.vigil.VigilService;
import dev.shadowsovereign.vigil.VigilState;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class VigilEvents {
    private static boolean sharing;

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
            VigilService.tick(p);
            VigilCombat.tick(p);
            AdventureData.Session run = VigilRuns.session(p);
            if (run != null) {
                VigilRuns.tick(p, run);
            }
            VigilState v = VigilService.state(p);
            if (VigilService.now(p) % 20L != 0L || v.viewUntil <= VigilService.now(p) && !VigilCrown.active(p)) continue;
            VigilNetwork.send(p, false, 0);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void damage(LivingDamageEvent e) {
        if (e.getEntity().m_9236_().f_46443_ || e.getAmount() <= 0.0f) {
            return;
        }
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            LivingEntity attacker;
            ServerPlayer p = (ServerPlayer)livingEntity;
            Entity source = e.getSource().m_7639_();
            if (source instanceof LivingEntity && ShadowService.canAttack(p, attacker = (LivingEntity)source)) {
                VigilState v = VigilService.state(p);
                v.threat = attacker.m_20148_();
                v.lastThreatAt = VigilService.now(p);
                if (p.m_21223_() - e.getAmount() > 0.0f && p.m_21223_() - e.getAmount() < 6.0f) {
                    v.crownRequest = VigilService.now(p) + 1L;
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent(priority=EventPriority.LOW)
    public static void protection(LivingHurtEvent e) {
        Mob buddy;
        Mob mob;
        block13: {
            block12: {
                LivingEntity livingEntity;
                if (e.getEntity().m_9236_().f_46443_ || e.getAmount() <= 0.0f || !((livingEntity = e.getEntity()) instanceof Mob)) break block12;
                mob = (Mob)livingEntity;
                if (!e.getSource().m_269533_(DamageTypeTags.f_268738_)) break block13;
            }
            return;
        }
        ShadowRecord r = ShadowService.record((Entity)mob);
        ServerPlayer p = ShadowService.owner((Entity)mob);
        if (r == null || p == null) {
            return;
        }
        ItemStack shield = mob.m_21206_();
        if (shield.m_150930_((Item)VigilContent.SHIELD.get())) {
            e.setAmount(Math.max(0.0f, e.getAmount() - 2.0f));
            shield.m_41622_(1, (LivingEntity)mob, m -> m.m_21166_(EquipmentSlot.OFFHAND));
            CompanionService.captureEquipment(mob, r);
            VigilService.dirty(p);
        }
        VigilState v = VigilService.state(p);
        if (!sharing && (r.id.equals(v.linkA) || r.id.equals(v.linkB)) && VigilService.ready(p, "link") && (buddy = VigilService.eligible(p, r.id.equals(v.linkA) ? v.linkB : v.linkA)) != null && buddy.m_20280_((Entity)mob) <= 64.0) {
            float share = Math.min(4.0f, e.getAmount() * 0.15f);
            sharing = true;
            try {
                if (buddy.m_6469_(e.getSource(), share)) {
                    e.setAmount(Math.max(0.0f, e.getAmount() - share));
                    v.ready.put("link", VigilService.now(p) + 20L);
                    VigilService.dirty(p);
                }
            }
            finally {
                sharing = false;
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void died(LivingDeathEvent e) {
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)livingEntity;
            if (VigilCrown.active(p)) {
                VigilCrown.stop(p);
            }
            VigilService.cancelJobs(p);
            if (VigilRuns.session(p) != null) {
                VigilRuns.finish(p, false);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public static void target(LivingChangeTargetEvent e) {
        if (e.getEntity().m_9236_().f_46443_ || e.getNewTarget() == null) {
            return;
        }
        ServerPlayer p = ShadowService.owner((Entity)e.getEntity());
        ShadowRecord r = ShadowService.record((Entity)e.getEntity());
        if (p != null && r != null && VigilCrown.member(p, r) && e.getNewTarget().m_20280_((Entity)p) > 25.0) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
                r.vigil.separatedAt = VigilService.now(p);
                r.vigil.separatedWall = System.currentTimeMillis();
            }
            VigilService.dirty(p);
            VigilService.cancelJobs(p);
            VigilService.state((ServerPlayer)p).lastView = null;
            VigilService.state((ServerPlayer)p).viewUntil = 0L;
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            if (VigilCrown.active(p)) {
                VigilCrown.stop(p);
            }
            VigilService.cancelJobs(p);
            VigilService.state((ServerPlayer)p).lastView = null;
        }
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            VigilState v = VigilService.state(p);
            v.lastView = null;
            if (v.watch) {
                v.nextWatch = VigilService.now(p) + (long)v.watchInterval;
            }
            VigilNetwork.send(p, false, 0);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void ferrymanAttack(LivingAttackEvent e) {
        AdventureData.Session s = AdventureWorld.session((Entity)e.getEntity());
        if (s == null || s.kind != AdventureData.Kind.FERRYMAN) {
            return;
        }
        ShadowRecord r = CompanionProgression.attacker(e.getSource());
        if (s.vigil.m_128454_("GraceUntil") > e.getEntity().m_9236_().m_46467_() || r != null && !s.participants.contains(r.id)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void ferrymanDamage(LivingDamageEvent e) {
        AdventureData.Session s = AdventureWorld.session((Entity)e.getEntity());
        if (s == null || s.kind != AdventureData.Kind.FERRYMAN || e.getAmount() <= 0.0f) {
            return;
        }
        int phase = s.vigil.m_128451_("Phase");
        if (phase >= 2) {
            return;
        }
        float threshold = e.getEntity().m_21233_() * (phase == 0 ? 0.65f : 0.35f);
        if (e.getEntity().m_21223_() - e.getAmount() <= threshold) {
            e.setAmount(Math.max(0.0f, e.getEntity().m_21223_() - threshold));
            s.vigil.m_128405_("Phase", phase + 1);
            s.vigil.m_128356_("GraceUntil", e.getEntity().m_9236_().m_46467_() + 40L);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void teleport(EntityTeleportEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Mob)) {
            return;
        }
        Mob m = (Mob)entity;
        ServerPlayer p = ShadowService.owner((Entity)m);
        ShadowRecord r = ShadowService.record((Entity)m);
        if (p == null || r == null || !VigilCrown.member(p, r)) {
            return;
        }
        double margin = Math.max(0.0, 2.5 - (double)m.m_20205_() / 2.0);
        if (Math.abs(e.getTargetX() - p.m_20185_()) > margin || Math.abs(e.getTargetZ() - p.m_20189_()) > margin) {
            e.setCanceled(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent(priority=EventPriority.LOW)
    public static void intercept(LivingDamageEvent e) {
        LivingEntity attacker;
        ServerPlayer p;
        LivingEntity livingEntity;
        if (sharing || e.getAmount() <= 0.0f || !((livingEntity = e.getEntity()) instanceof ServerPlayer) || !VigilCrown.active(p = (ServerPlayer)livingEntity) || e.getSource().m_269533_(DamageTypeTags.f_268738_) || !((livingEntity = e.getSource().m_7639_()) instanceof LivingEntity) || !ShadowService.canAttack(p, attacker = livingEntity)) {
            return;
        }
        Mob guard = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().filter(r -> VigilCrown.member(p, r)).map(r -> ShadowService.active(p.f_8924_, r)).filter(m -> m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && VigilCrown.inside(p, m)).min(Comparator.comparingDouble(m -> m.m_20280_((Entity)attacker))).orElse(null);
        if (guard == null) {
            return;
        }
        float amount = Math.min(6.0f, e.getAmount() * 0.6f);
        sharing = true;
        try {
            if (guard.m_6469_(e.getSource(), amount)) {
                e.setAmount(Math.max(0.0f, e.getAmount() - amount));
            }
        }
        finally {
            sharing = false;
        }
    }
}

