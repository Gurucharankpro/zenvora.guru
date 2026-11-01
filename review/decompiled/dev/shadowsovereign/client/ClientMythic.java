/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.MythicGrimoireScreen;
import dev.shadowsovereign.client.MythicScreen;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.network.MythicNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientMythic {
    public static CompoundTag data = new CompoundTag();
    private static int ticks;

    public static String t(String k, Object ... args) {
        return ModLanguage.user(MythicService.text(k, args).getString());
    }

    public static void open() {
        MythicNetwork.request(MythicNetwork.Action.OPEN);
    }

    public static void accept(MythicNetwork.View v) {
        CompoundTag compoundTag = data = v.data() == null ? new CompoundTag() : v.data();
        if (v.open() && Minecraft.m_91087_().f_91074_ != null) {
            Minecraft.m_91087_().m_91152_(data.m_128471_("GrimoireOpen") ? new MythicGrimoireScreen() : new MythicScreen());
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END && ++ticks % 60 == 0 && (Minecraft.m_91087_().f_91080_ instanceof MythicScreen || Minecraft.m_91087_().f_91080_ instanceof MythicGrimoireScreen)) {
            MythicNetwork.request(MythicNetwork.Action.VIEW);
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
        ticks = 0;
    }
}

