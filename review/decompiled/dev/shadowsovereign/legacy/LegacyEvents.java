/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.legacy;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.legacy.LegacyChallenges;
import dev.shadowsovereign.legacy.LegacyService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class LegacyEvents {
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void killed(LivingDeathEvent e) {
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        LegacyService.killed(e.getEntity(), e.getSource());
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)livingEntity;
            AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
            if (s != null && (s.kind == AdventureData.Kind.GAUNTLET || s.kind == AdventureData.Kind.SIEGE)) {
                LegacyChallenges.fail(p, "You fell in battle");
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void hit(LivingDamageEvent e) {
        if (!e.getEntity().m_9236_().f_46443_) {
            LegacyService.hit(e.getEntity(), e.getSource(), e.getAmount());
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void join(EntityJoinLevelEvent e) {
        Mob mob;
        Entity entity;
        if (e.getLevel().f_46443_ || !((entity = e.getEntity()) instanceof Mob) || !ShadowService.isShadow((Entity)(mob = (Mob)entity))) {
            return;
        }
        AttributeInstance speed = mob.m_21051_(Attributes.f_22279_);
        if (speed != null) {
            speed.m_22120_(LegacyChallenges.FLEET);
        }
    }

    @SubscribeEvent
    public static void leave(EntityLeaveLevelEvent e) {
        Mob m;
        AttributeInstance speed;
        Entity entity;
        if (e.getLevel().f_46443_ || !ShadowService.isShadow(e.getEntity())) {
            return;
        }
        AdventureData data = AdventureData.get(e.getLevel().m_7654_());
        AdventureData.Journal j = data.owners.get(ShadowService.ownerId(e.getEntity()));
        if (j != null) {
            j.legacy.resetWindow();
        }
        if ((entity = e.getEntity()) instanceof Mob && (speed = (m = (Mob)entity).m_21051_(Attributes.f_22279_)) != null) {
            speed.m_22120_(LegacyChallenges.FLEET);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && AdventureService.now(e.getServer()) % 100L == 0L) {
            for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
                LegacyService.achievements(p);
            }
        }
    }
}

