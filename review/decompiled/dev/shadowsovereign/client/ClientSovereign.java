/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.network.SovereignNetwork;
import dev.shadowsovereign.sovereign.SovereignState;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientSovereign {
    public static CompoundTag data = new CompoundTag();

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null && !mc.m_91104_() && mc.f_91074_.f_19797_ % 100 == 0) {
            SovereignNetwork.request(SovereignNetwork.Action.VIEW, 0, null);
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
    }

    public static boolean needsCare(UUID id) {
        SovereignState s = SovereignState.load(data);
        return s.active >= 0 && !s.ready() && id.equals(s.champion) && s.goal() == SovereignState.Goal.FEED;
    }

    public static String objective(SovereignState.Goal goal) {
        return switch (goal) {
            default -> throw new IncompatibleClassChangeError();
            case SovereignState.Goal.HUNT -> "Confirmed personal kills";
            case SovereignState.Goal.FEED -> "Feed your champion";
            case SovereignState.Goal.ART -> "Successful Soul Art casts";
            case SovereignState.Goal.PLAN -> "Complete a Battle Plan";
            case SovereignState.Goal.WARFRONT -> "Win a Warfront mission";
        };
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class SovereignOverlayRegistration {
        @SubscribeEvent
        public static void overlay(RegisterGuiOverlaysEvent e) {
            e.registerAboveAll("sovereign_trail", (gui, g, partial, w, h) -> {
                Minecraft mc = Minecraft.m_91087_();
                if (mc.f_91074_ == null || mc.f_91080_ != null || mc.f_91066_.f_92062_ || data.m_128456_()) {
                    return;
                }
                SovereignState s = SovereignState.load(data);
                if (s.active < 0 && s.crest == 0) {
                    return;
                }
                int width = Math.min(276, w - 16);
                int y = h - 86;
                g.m_280509_(8, y, 8 + width, y + 32, -821032166);
                ModLanguage.draw(g, mc.f_91062_, ModLanguage.fit(mc.f_91062_, s.crest > 0 ? SovereignState.CRESTS[s.crest] : "Sovereign Trails", width - 12), 14, y + 5, -1522047, false);
                ModLanguage.draw(g, mc.f_91062_, ModLanguage.fit(mc.f_91062_, (String)(s.ready() ? "Trail ready! Open H \u2192 Sovereign Trails" : (s.active >= 0 ? ClientSovereign.objective(s.goal()) + " " + s.progress + " / " + s.target() : "Crest equipped \u2022 cosmetic only")), width - 12), 14, y + 18, -2767129, false);
            });
        }
    }
}

