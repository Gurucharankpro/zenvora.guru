/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.MenuScreens
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.world.inventory.MenuType
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
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalBagScreen;
import dev.shadowsovereign.client.RoyalScreen;
import dev.shadowsovereign.network.EclipseNetwork;
import dev.shadowsovereign.network.RoyalNetwork;
import dev.shadowsovereign.royal.RoyalContent;
import dev.shadowsovereign.royal.RoyalSync;
import dev.shadowsovereign.royal.RoyalText;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.inventory.MenuType;
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
public final class ClientRoyal {
    public static CompoundTag data = new CompoundTag();
    private static int ticks;
    private static UUID memory;
    private static boolean initialized;
    public static long celebrationUntil;

    public static String t(String key, Object ... args) {
        return ModLanguage.user(RoyalText.c(key, args).getString());
    }

    public static void accept(RoyalNetwork.View v) {
        UUID next;
        ListTag list = (data = RoyalSync.apply(data, v.data())).m_128437_("Memories", 10);
        UUID uUID = next = list.isEmpty() ? null : list.m_128728_(0).m_128342_("Id");
        if (initialized && !Objects.equals(memory, next) && next != null && !Boolean.parseBoolean(ClientPreferences.get("royal.hideCelebration", "false"))) {
            celebrationUntil = ClientWarfront.now() + 60L;
        }
        memory = next;
        initialized = true;
        Minecraft mc = Minecraft.m_91087_();
        if (v.open() && mc.f_91074_ != null) {
            mc.m_91152_((Screen)new RoyalScreen(v.page()));
        }
    }

    public static void open(int page) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        RoyalNetwork.request(RoyalNetwork.Action.VIEW, page, 1, 0, "", List.of());
        EclipseNetwork.request(EclipseNetwork.Action.VIEW, 0, 0, 0, null, List.of());
        mc.m_91152_((Screen)new RoyalScreen(page));
    }

    @SubscribeEvent
    public static void royalClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.m_91104_()) {
            return;
        }
        for (int i = 0; i < 5; ++i) {
            while (Keys.TEAMS[i].m_90859_()) {
                if (mc.f_91080_ != null) continue;
                RoyalNetwork.request(RoyalNetwork.Action.DEPLOY, i, 0, 0, "", List.of());
            }
        }
        if (mc.f_91080_ instanceof RoyalScreen && ++ticks % 60 == 0) {
            RoyalNetwork.request(RoyalNetwork.Action.VIEW, 0, 0, 0, "", List.of());
        }
    }

    @SubscribeEvent
    public static void royalLeave(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
        memory = null;
        initialized = false;
        celebrationUntil = 0L;
        ticks = 0;
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class Keys {
        public static final KeyMapping[] TEAMS = new KeyMapping[5];

        @SubscribeEvent
        public static void registerRoyalKeys(RegisterKeyMappingsEvent e) {
            for (KeyMapping key : TEAMS) {
                e.register(key);
            }
        }

        @SubscribeEvent
        public static void registerRoyalScreens(FMLClientSetupEvent e) {
            e.enqueueWork(() -> MenuScreens.m_96206_((MenuType)((MenuType)RoyalContent.BAGS.get()), RoyalBagScreen::new));
        }

        static {
            for (int i = 0; i < 5; ++i) {
                Keys.TEAMS[i] = new KeyMapping("key.shadowsovereign.team_" + (i + 1), (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, -1, "key.categories.shadowsovereign");
            }
        }
    }
}

