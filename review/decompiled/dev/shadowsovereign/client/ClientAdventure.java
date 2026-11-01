/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.AdventureScreen;
import dev.shadowsovereign.network.AdventureNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientAdventure {
    public static CompoundTag data = new CompoundTag();
    private static long ticks;
    private static long received;

    private ClientAdventure() {
    }

    public static void accept(AdventureNetwork.View view) {
        Screen screen;
        data = view.data().m_6426_();
        received = ticks;
        Minecraft mc = Minecraft.m_91087_();
        if (view.open() && mc.f_91074_ != null && !(mc.f_91080_ instanceof AdventureScreen)) {
            mc.m_91152_((Screen)new AdventureScreen(null));
        }
        if ((screen = mc.f_91080_) instanceof AdventureScreen) {
            AdventureScreen screen2 = (AdventureScreen)screen;
            screen2.updated();
        }
    }

    public static long now() {
        return data.m_128454_("Time") + ticks - received;
    }

    public static void send(AdventureNetwork.Request request) {
        if (Minecraft.m_91087_().f_91074_ != null) {
            AdventureNetwork.request(request);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ++ticks;
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        data = new CompoundTag();
        ticks = 0L;
        received = 0L;
    }
}

