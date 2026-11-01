/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.Block
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.SoulLifeScreen;
import dev.shadowsovereign.network.SoulLifeNetwork;
import dev.shadowsovereign.portal.SoulGates;
import dev.shadowsovereign.royal.RoyalSync;
import dev.shadowsovereign.soullife.SoulLifeText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientSoulLife {
    public static CompoundTag data = new CompoundTag();
    private static long received;
    private static int ticks;

    public static String t(String key, Object ... args) {
        return ModLanguage.user(SoulLifeText.c(key, args).getString());
    }

    public static long now() {
        return data.m_128454_("Time") + Math.max(0L, ClientWarfront.now() - received);
    }

    public static void accept(SoulLifeNetwork.View view) {
        received = ClientWarfront.now();
        data = RoyalSync.apply(data, view.data());
        if (view.open() && Minecraft.m_91087_().f_91074_ != null) {
            Minecraft.m_91087_().m_91152_((Screen)new SoulLifeScreen(view.page()));
        }
    }

    public static void open(int page) {
        Minecraft m = Minecraft.m_91087_();
        if (m.f_91074_ == null) {
            return;
        }
        if (ClientEclipse.selected != null) {
            SoulLifeNetwork.request(SoulLifeNetwork.Action.SELECT, ClientEclipse.selected, null, 0, 0, 0);
        }
        SoulLifeNetwork.request(SoulLifeNetwork.Action.VIEW, null, null, 1, 0, 0);
        m.m_91152_((Screen)new SoulLifeScreen(page));
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END && Minecraft.m_91087_().f_91074_ != null && Minecraft.m_91087_().f_91080_ instanceof SoulLifeScreen && ++ticks % 60 == 0) {
            SoulLifeNetwork.request(SoulLifeNetwork.Action.VIEW, null, null, 0, 0, 0);
        }
    }

    @SubscribeEvent
    public static void leave(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
        ticks = 0;
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class Setup {
        @SubscribeEvent
        public static void setup(FMLClientSetupEvent e) {
            e.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer((Block)((Block)SoulGates.SURFACE.get()), (RenderType)RenderType.m_110466_()));
        }
    }
}

