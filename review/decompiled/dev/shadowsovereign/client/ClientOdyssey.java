/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.Block
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.OdysseyScreen;
import dev.shadowsovereign.client.VigilAlbumScreen;
import dev.shadowsovereign.network.OdysseyNetwork;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.royal.RoyalSync;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientOdyssey {
    public static CompoundTag data = new CompoundTag();
    private static int ticks;
    private static long received;
    private static boolean initialized;
    public static final KeyMapping INTERACT;

    public static String t(String key, Object ... args) {
        return ModLanguage.user(OdysseyText.c(key, args).getString());
    }

    public static long now() {
        return data.m_128454_("Time") + Math.max(0L, ClientWarfront.now() - received);
    }

    public static void accept(OdysseyNetwork.View view) {
        long previousPhoto = data.m_128469_("CampState").m_128454_("PhotoUntil");
        received = ClientWarfront.now();
        data = RoyalSync.apply(data, view.data());
        if (initialized && data.m_128469_("CampState").m_128454_("PhotoUntil") > previousPhoto && data.m_128469_("CampState").m_128454_("PhotoUntil") > ClientOdyssey.now()) {
            VigilAlbumScreen.captureAfter(100);
        }
        initialized = true;
        if (view.open() && Minecraft.m_91087_().f_91074_ != null) {
            Minecraft.m_91087_().m_91152_((Screen)new OdysseyScreen(view.page()));
        }
    }

    public static void open(int page) {
        Minecraft m = Minecraft.m_91087_();
        if (m.f_91074_ == null) {
            return;
        }
        OdysseyNetwork.request(OdysseyNetwork.Action.VIEW, null, 1, 0, 0, "");
        m.m_91152_((Screen)new OdysseyScreen(page));
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END && Minecraft.m_91087_().f_91074_ != null && Minecraft.m_91087_().f_91080_ instanceof OdysseyScreen && ++ticks % 60 == 0) {
            OdysseyNetwork.request(OdysseyNetwork.Action.VIEW, null, 0, 0, 0, "");
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
        ticks = 0;
        initialized = false;
    }

    static {
        INTERACT = new KeyMapping("key.shadowsovereign.odyssey_action", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, 66, "key.categories.shadowsovereign");
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class Setup {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent e) {
            e.register(INTERACT);
        }

        @SubscribeEvent
        public static void client(FMLClientSetupEvent e) {
            e.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer((Block)((Block)OdysseyContent.PORTAL.get()), (RenderType)RenderType.m_110466_()));
        }
    }
}

