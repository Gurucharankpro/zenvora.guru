/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.ascension;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.AscensionService;
import dev.shadowsovereign.ascension.SoulService;
import dev.shadowsovereign.network.AscensionNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class AscensionEvents {
    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
            if (SoulService.now(p) % 20L != 0L) continue;
            SoulService.expire(p);
            ArmyData.Army a = ArmyData.get(p.f_8924_).army(p.m_20148_());
            if (a.recruitmentNotice) {
                a.recruitmentNotice = false;
                ArmyData.get(p.f_8924_).m_77762_();
                ShadowService.tell(p, "Recruitment now uses manual soul claiming. Aim at your purple soul and press R (rebindable). Automatic remains in the menu.");
            }
            AscensionNetwork.send(p, false);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void death(LivingDeathEvent e) {
        ServerPlayer p;
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer && AscensionService.session(p = (ServerPlayer)livingEntity) != null) {
            AscensionService.fail(p, "Activity ended when you fell. Your army is safe.");
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public static void damage(LivingHurtEvent e) {
        Entity actor = e.getSource().m_7639_();
        if (actor == null || !AdventureWorld.isNpc(actor)) {
            return;
        }
        AdventureData.Session s = AdventureWorld.session(actor);
        AdventureData.Npc n = AdventureWorld.npc(actor);
        if (s != null && AscensionService.activity(s.kind) && n != null && n.role == AdventureData.Role.ENEMY && !s.journey.m_128471_("Hard")) {
            e.setAmount(e.getAmount() * 0.75f);
        }
    }
}

