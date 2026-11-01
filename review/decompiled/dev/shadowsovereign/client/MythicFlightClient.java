/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.network.MythicFlightNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class MythicFlightClient {
    private static int ticks;

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        MythicBeast m;
        Entity entity;
        if (e.phase != TickEvent.Phase.END || ++ticks % 4 != 0) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || !((entity = mc.f_91074_.m_20202_()) instanceof MythicBeast) || !(m = (MythicBeast)entity).profile().rideable()) {
            return;
        }
        int up = mc.f_91080_ == null && mc.f_91066_.f_92089_.m_90857_() ? 1 : (mc.f_91080_ == null && mc.f_91074_.m_146909_() > 45.0f ? -1 : 0);
        MythicFlightNetwork.send(up, mc.f_91080_ == null && mc.f_91066_.f_92091_.m_90857_());
        mc.f_91074_.m_5661_((Component)Component.m_237113_((String)("\u2726 " + Math.round(m.stamina()) + "%")), true);
    }
}

