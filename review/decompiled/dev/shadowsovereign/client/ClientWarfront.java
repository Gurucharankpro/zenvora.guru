/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.shadowsovereign.client.ClientAscension;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.WarfrontScreen;
import dev.shadowsovereign.network.WarfrontNetwork;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ClientWarfront {
    public static CompoundTag data = new CompoundTag();
    public static UUID selected;
    public static final KeyMapping ART;

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent e) {
        e.register(ART);
    }

    @SubscribeEvent
    public static void overlays(RegisterGuiOverlaysEvent e) {
        e.registerAboveAll("warfront_status", (gui, g, partial, width, height) -> {
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ == null || mc.f_91080_ != null || mc.f_91066_.f_92062_) {
                return;
            }
            CompoundTag run = data.m_128469_("Run");
            Object first = "";
            Object second = "";
            if (data.m_128441_("Run") && run.m_128451_("Phase") == 0) {
                first = "WARFRONT \u2022 " + WarfrontService.MISSIONS[Math.max(0, Math.min(3, run.m_128451_("Mission")))] + " \u2022 " + Math.max(0L, (run.m_128454_("Remaining") - (ClientWarfront.now() - data.m_128454_("Time"))) / 20L) + "s";
                second = run.m_128451_("Mission") == 1 ? "Wards " + Integer.bitCount(run.m_128451_("Disabled")) + " / 3 \u2022 approach with an ally and press " + ClientAscension.INTERACT.m_90863_().getString() : (run.m_128451_("Mission") == 2 ? (run.m_128471_("Released") ? "Escort the Wayfarer to your Beacon" : "Free the Wayfarer: approach and press " + ClientAscension.INTERACT.m_90863_().getString()) : (run.m_128471_("PhaseTwo") ? "Commander phase 2 \u2022 dodge violet warning circles" : "Defeat the marked enemy \u2022 watch for warning circles"));
            } else if (data.m_128451_("Running") >= 0 && data.m_128441_("Running")) {
                first = "BATTLE PLAN " + (data.m_128451_("Running") + 1);
                second = "Step " + (data.m_128451_("Step") + 1) + " \u2022 manual orders cancel";
            } else if (selected != null) {
                for (Tag tag : data.m_128437_("Roster", 10)) {
                    CompoundTag n = (CompoundTag)tag;
                    if (!n.m_128342_("Id").equals(selected) || n.m_128461_("Art").equals("NONE")) continue;
                    first = "SOUL ART \u2022 " + WarfrontState.label(WarfrontState.read(WarfrontState.Art.class, n.m_128461_("Art"), WarfrontState.Art.NONE));
                    long seconds = Math.max(0L, (n.m_128454_("ArtAt") - ClientWarfront.now() + 19L) / 20L);
                    second = seconds > 0L ? seconds + "s cooldown" : "Ready \u2022 V then Tab opens Soul Arts";
                    break;
                }
            }
            if (((String)first).isEmpty()) {
                return;
            }
            int w = Math.min(286, width - 16);
            g.m_280509_(8, 8, 8 + w, 41, -821032166);
            g.m_280509_(8, 8, 10, 41, -4421133);
            ModLanguage.draw(g, mc.f_91062_, ModLanguage.fit(mc.f_91062_, (String)first, w - 14), 15, 14, -1522047, false);
            ModLanguage.draw(g, mc.f_91062_, ModLanguage.fit(mc.f_91062_, (String)second, w - 14), 15, 28, -2767129, false);
        });
    }

    public static long now() {
        ClientLevel level = Minecraft.m_91087_().f_91073_;
        return level == null ? data.m_128454_("Time") : level.m_46467_();
    }

    public static void accept(WarfrontNetwork.View view) {
        Screen screen;
        data = view.data();
        if (data.m_128403_("Selected")) {
            selected = data.m_128342_("Selected");
        }
        Minecraft mc = Minecraft.m_91087_();
        if (view.open() && mc.f_91074_ != null) {
            mc.m_91152_((Screen)new WarfrontScreen(selected));
        }
        if ((screen = mc.f_91080_) instanceof WarfrontScreen) {
            WarfrontScreen s = (WarfrontScreen)screen;
            s.updated();
        }
    }

    public static void open(UUID id, int page) {
        Minecraft mc = Minecraft.m_91087_();
        selected = id;
        WarfrontNetwork.request(WarfrontNetwork.Action.VIEW, 0, false, id);
        WarfrontScreen screen = new WarfrontScreen(id);
        screen.show(page);
        mc.m_91152_((Screen)screen);
    }

    static {
        ART = new KeyMapping("key.shadowsovereign.art", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, -1, "key.categories.shadowsovereign");
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
    public static final class WarfrontInputEvents {
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft mc = Minecraft.m_91087_();
            while (ART.m_90859_()) {
                if (mc.f_91074_ == null || mc.f_91080_ != null || selected == null) continue;
                WarfrontNetwork.request(WarfrontNetwork.Action.CAST, 0, false, selected);
            }
        }

        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
            data = new CompoundTag();
            selected = null;
        }
    }
}

