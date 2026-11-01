/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.item.ItemProperties
 *  net.minecraft.client.renderer.item.ItemPropertyFunction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.client.VigilScreen;
import dev.shadowsovereign.network.VigilNetwork;
import dev.shadowsovereign.royal.RoyalSync;
import dev.shadowsovereign.vigil.VigilContent;
import dev.shadowsovereign.vigil.VigilText;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientVigil {
    public static CompoundTag data = new CompoundTag();
    private static int ticks;
    private static long received;

    public static long now() {
        return data.m_128454_("Time") + Math.max(0L, ClientWarfront.now() - received);
    }

    public static String t(String key, Object ... args) {
        return ModLanguage.user(VigilText.c(key, args).getString());
    }

    public static void accept(VigilNetwork.View view) {
        received = ClientWarfront.now();
        data = RoyalSync.apply(data, view.data());
        if (view.open() && Minecraft.m_91087_().f_91074_ != null) {
            Minecraft.m_91087_().m_91152_((Screen)new VigilScreen(view.page()));
        }
    }

    public static void open(int page) {
        Minecraft m = Minecraft.m_91087_();
        if (m.f_91074_ == null) {
            return;
        }
        VigilNetwork.request(VigilNetwork.Action.VIEW, 0, 1, 0, null, null, List.of());
        m.m_91152_((Screen)new VigilScreen(page));
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft m = Minecraft.m_91087_();
        if (m.f_91074_ == null || m.m_91104_()) {
            return;
        }
        if (m.f_91080_ instanceof VigilScreen && ++ticks % 60 == 0) {
            VigilNetwork.request(VigilNetwork.Action.VIEW, 0, 0, 0, null, null, List.of());
        }
    }

    @SubscribeEvent
    public static void leave(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
        ticks = 0;
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class Display {
        @SubscribeEvent
        public static void hud(RegisterGuiOverlaysEvent e) {
            e.registerAboveAll("vigil_crown", (gui, g, partial, w, h) -> {
                Minecraft m = Minecraft.m_91087_();
                if (m.f_91074_ == null || m.f_91080_ != null || m.f_91066_.f_92062_ || !data.m_128471_("Protecting")) {
                    return;
                }
                int ww = Math.min(280, w - 24);
                int x = (w - ww) / 2;
                RoyalTheme.frame(g, x, 8, ww, 34, -400418000);
                ModLanguage.draw(g, m.f_91062_, ModLanguage.fit(m.f_91062_, ClientVigil.t("crown_hud", Math.ceil((double)m.f_91074_.m_21223_() / 2.0), Math.ceil((double)m.f_91074_.m_21233_() / 2.0)), ww - 12), x + 6, 18, -1981295, false);
            });
        }

        @SubscribeEvent
        public static void setup(FMLClientSetupEvent e) {
            e.enqueueWork(() -> {
                ResourceLocation angle = new ResourceLocation("angle");
                ItemPropertyFunction prop = ItemProperties.m_117829_((Item)Items.f_42522_, (ResourceLocation)angle);
                if (prop != null) {
                    ItemProperties.register((Item)((Item)VigilContent.COMPASS.get()), (ResourceLocation)angle, (ItemPropertyFunction)prop);
                }
            });
        }
    }
}

