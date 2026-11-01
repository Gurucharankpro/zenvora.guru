/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.EclipseScreen;
import dev.shadowsovereign.client.OdysseyScreen;
import dev.shadowsovereign.client.RoyalScreen;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ClientArmy {
    public static final KeyMapping OPEN = new KeyMapping("key.shadowsovereign.army", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, 72, "key.categories.shadowsovereign");
    public static final KeyMapping ORIGINAL = new KeyMapping("key.shadowsovereign.original_army", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, 75, "key.categories.shadowsovereign");
    public static final Set<UUID> shadows = new HashSet<UUID>();
    public static ShadowNetwork.View view = new ShadowNetwork.View(List.of(), true, true, 256, 24, false);

    private static int ClientRoyalPage() {
        try {
            return Math.max(0, Math.min(13, Integer.parseInt(ClientPreferences.get("royal.page", "0"))));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent event) {
        event.register(OPEN);
        event.register(ORIGINAL);
    }

    public static void accept(ShadowNetwork.View next) {
        Screen screen;
        view = next;
        Minecraft mc = Minecraft.m_91087_();
        if (!(!next.open() || mc.f_91074_ == null || mc.f_91080_ instanceof ArmyScreen || mc.f_91080_ instanceof EclipseScreen || mc.f_91080_ instanceof RoyalScreen || mc.f_91080_ instanceof OdysseyScreen)) {
            ClientArmy.openHome();
        }
        if ((screen = mc.f_91080_) instanceof ArmyScreen) {
            ArmyScreen screen2 = (ArmyScreen)screen;
            screen2.updated();
        }
    }

    public static void openHome() {
        if (Boolean.parseBoolean(ClientPreferences.get("odyssey.legacyHome", "false"))) {
            ClientRoyal.open(0);
        } else {
            ClientOdyssey.open(0);
        }
    }

    public static void openOriginal() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        ShadowNetwork.request(ShadowNetwork.Action.REFRESH, null);
        mc.m_91152_((Screen)new ArmyScreen(ClientEclipse.selected));
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
    public static final class ArmyEvents {
        @SubscribeEvent
        public static void key(InputEvent.Key event) {
            if (event.getAction() == 1 && ORIGINAL.isActiveAndMatches(InputConstants.m_84827_((int)event.getKey(), (int)event.getScanCode()))) {
                Minecraft mc = Minecraft.m_91087_();
                if (mc.f_91074_ != null && mc.f_91080_ == null) {
                    while (ORIGINAL.m_90859_()) {
                    }
                    ClientArmy.openOriginal();
                }
                return;
            }
            if (event.getAction() == 1 && OPEN.isActiveAndMatches(InputConstants.m_84827_((int)event.getKey(), (int)event.getScanCode()))) {
                ArmyEvents.open();
            }
        }

        private static void open() {
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ == null || mc.f_91080_ != null) {
                return;
            }
            while (OPEN.m_90859_()) {
            }
            ClientArmy.openHome();
            ShadowNetwork.request(ShadowNetwork.Action.REFRESH, null);
        }

        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent event) {
            Minecraft mc;
            if (event.phase == TickEvent.Phase.END) {
                while (ORIGINAL.m_90859_()) {
                    mc = Minecraft.m_91087_();
                    if (mc.f_91074_ == null || mc.f_91080_ != null) continue;
                    ClientArmy.openOriginal();
                }
            }
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            mc = Minecraft.m_91087_();
            boolean pressed = false;
            while (OPEN.m_90859_()) {
                pressed = true;
            }
            if (pressed && mc.f_91074_ != null && mc.f_91080_ == null) {
                ArmyEvents.open();
            }
        }

        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
            shadows.clear();
            view = new ShadowNetwork.View(List.of(), true, true, 256, 24, false);
        }

        @SubscribeEvent
        public static void leave(EntityLeaveLevelEvent event) {
            if (event.getLevel().f_46443_) {
                shadows.remove(event.getEntity().m_20148_());
            }
        }
    }
}

