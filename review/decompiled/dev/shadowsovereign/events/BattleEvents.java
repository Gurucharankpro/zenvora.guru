/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.living.LivingChangeTargetEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingTickEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.events;

import dev.shadowsovereign.army.BattleService;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.journey.JourneyCombat;
import dev.shadowsovereign.legacy.LegacyChallenges;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class BattleEvents {
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void damage(LivingDamageEvent event) {
        Mob mob;
        LivingEntity livingEntity;
        if (event.getEntity().m_9236_().f_46443_ || !((livingEntity = event.getEntity()) instanceof Mob) || !ShadowService.isShadow((Entity)(mob = (Mob)livingEntity))) {
            return;
        }
        float remaining = BattleService.absorb(mob, event.getAmount());
        remaining = LegacyChallenges.absorb(mob, remaining);
        remaining = JourneyCombat.absorb(mob, remaining);
        event.setAmount(remaining);
        if (remaining > 0.0f) {
            CompanionProgression.damaged(mob);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void tauntTarget(LivingChangeTargetEvent event) {
        Mob mob;
        LivingEntity livingEntity;
        if (event.getEntity().m_9236_().f_46443_ || !((livingEntity = event.getEntity()) instanceof Mob) || ShadowService.isShadow((Entity)(mob = (Mob)livingEntity))) {
            return;
        }
        Mob tank = BattleService.tauntTarget(mob);
        if (tank != null) {
            event.setNewTarget((LivingEntity)tank);
        }
    }

    @SubscribeEvent
    public static void tauntTick(LivingEvent.LivingTickEvent event) {
        Mob mob;
        LivingEntity livingEntity;
        if (!event.getEntity().m_9236_().f_46443_ && (livingEntity = event.getEntity()) instanceof Mob && !ShadowService.isShadow((Entity)(mob = (Mob)livingEntity))) {
            BattleService.tickTaunt(mob);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void ownerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            event.getServer().m_6846_().m_11314_().forEach(BattleService::tickOwner);
        }
    }
}

