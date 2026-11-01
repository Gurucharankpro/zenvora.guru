/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.entity.ThrownItemRenderer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.EclipseCampRenderer;
import dev.shadowsovereign.client.EclipseScreen;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.eclipse.EclipseContent;
import dev.shadowsovereign.eclipse.EclipseText;
import dev.shadowsovereign.network.EclipseNetwork;
import dev.shadowsovereign.royal.RoyalSync;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientEclipse {
    public static CompoundTag data = new CompoundTag();
    public static UUID selected;
    private static int ticks;

    public static void accept(EclipseNetwork.View v) {
        if ((data = RoyalSync.apply(data, v.data())).m_128403_("Selected")) {
            selected = data.m_128342_("Selected");
        } else if (selected != null && ClientEclipse.roster().stream().noneMatch(n -> n.m_128342_("Id").equals(selected))) {
            selected = null;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (v.open() && mc.f_91074_ != null) {
            mc.m_91152_((Screen)new EclipseScreen(v.page()));
        }
    }

    public static void open(int page) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        EclipseNetwork.request(EclipseNetwork.Action.VIEW, 0, 0, 0, null, List.of());
        mc.m_91152_((Screen)new EclipseScreen(page));
    }

    public static String t(String key, Object ... args) {
        return ModLanguage.user(EclipseText.c(key, args).getString());
    }

    public static List<CompoundTag> roster() {
        return data.m_128437_("Roster", 10).stream().map(n -> (CompoundTag)n).toList();
    }

    public static CompoundTag chosen() {
        return ClientEclipse.roster().stream().filter(n -> n.m_128342_("Id").equals(selected)).findFirst().orElse(new CompoundTag());
    }

    public static void select(UUID id) {
        selected = id;
        EclipseNetwork.request(EclipseNetwork.Action.SELECT, 0, 0, 0, id, List.of());
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null && !mc.m_91104_() && ++ticks % 200 == 0) {
            EclipseNetwork.request(EclipseNetwork.Action.VIEW, 0, 0, 0, null, List.of());
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
        selected = null;
        ticks = 0;
    }

    public static int pref(String key, int fallback, int min, int max) {
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(ClientPreferences.get("eclipse." + key, Integer.toString(fallback)))));
        }
        catch (NumberFormatException ex) {
            return fallback;
        }
    }

    public static int panelWidth(int i) {
        return Math.round((float)((i == 2 ? 96 : 164) * ClientEclipse.pref("hud.scale", 100, 70, 140)) / 100.0f);
    }

    public static int panelHeight(int i) {
        return Math.round((float)((i == 2 ? 110 : 33) * ClientEclipse.pref("hud.scale", 100, 70, 140)) / 100.0f);
    }

    public static void drawPanel(GuiGraphics g, int index, int x, int y, int width, int height) {
        Minecraft mc = Minecraft.m_91087_();
        float size = (float)ClientEclipse.pref("hud.scale", 100, 70, 140) / 100.0f;
        int alpha = ClientEclipse.pref("hud.alpha", 190, 80, 240);
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_((float)x, (float)y, 0.0f);
        g.m_280168_().m_85841_(size, size, 1.0f);
        if (index == 2) {
            ClientEclipse.radar(g, 0, 0, 96, alpha);
            ModLanguage.draw(g, mc.f_91062_, ClientEclipse.t("radar", new Object[0]), 3, 98, -1322605, false);
            g.m_280168_().m_85849_();
            return;
        }
        int w = 164;
        g.m_280509_(0, 0, w, 33, alpha << 24 | 0x100D1B);
        g.m_280509_(0, 0, 2, 33, -4814603);
        String a = "";
        String b = "";
        if (index == 0) {
            a = ClientEclipse.t("energy", Math.round(data.m_128457_("Energy")));
            b = ClientEclipse.t("marks", data.m_128451_("Marks"));
        }
        if (index == 1) {
            CompoundTag run = data.m_128469_("Run");
            a = run.m_128456_() ? ClientEclipse.t("dashboard", new Object[0]) : ClientEclipse.t("mode." + run.m_128461_("Kind").toLowerCase(Locale.ROOT), new Object[0]);
            String string = b = run.m_128456_() ? ClientEclipse.t("open_hint", new Object[0]) : ClientEclipse.t("enemy_count", run.m_128451_("Enemies"));
        }
        if (index == 2) {
            a = ClientEclipse.t("radar", new Object[0]);
            b = ClientEclipse.t("radar_count", ClientEclipse.roster().stream().filter(n -> n.m_128471_("Active")).count());
        }
        if (index == 3) {
            a = ClientEclipse.t("selection", new Object[0]);
            b = ClientEclipse.chosen().m_128456_() ? ClientEclipse.t("select_ally", new Object[0]) : ModLanguage.user(ClientEclipse.chosen().m_128461_("Name"));
        }
        ModLanguage.draw(g, mc.f_91062_, ModLanguage.fit(mc.f_91062_, a, w - 10), 6, 5, -1322605, false);
        ModLanguage.draw(g, mc.f_91062_, ModLanguage.fit(mc.f_91062_, b, w - 10), 6, 19, -1516039, false);
        if (index == 0) {
            g.m_280509_(6, 30, 158, 32, -13950406);
            g.m_280509_(6, 30, 6 + (int)(152.0f * data.m_128457_("Energy") / 100.0f), 32, -4421651);
        }
        g.m_280168_().m_85849_();
    }

    public static void radar(GuiGraphics g, int x, int y, int size) {
        ClientEclipse.radar(g, x, y, size, 238);
    }

    public static void radar(GuiGraphics g, int x, int y, int size, int alpha) {
        g.m_280509_(x, y, x + size, y + size, alpha << 24 | 0x100D1B);
        g.m_280509_(x + size / 2, y, x + size / 2 + 1, y + size, 1715352916);
        g.m_280509_(x, y + size / 2, x + size, y + size / 2 + 1, 1715352916);
        List<CompoundTag> own = ClientEclipse.roster().stream().filter(n -> n.m_128471_("Active") && n.m_128459_("X") * n.m_128459_("X") + n.m_128459_("Z") * n.m_128459_("Z") <= 2304.0).sorted(Comparator.comparingDouble(n -> n.m_128459_("X") * n.m_128459_("X") + n.m_128459_("Z") * n.m_128459_("Z"))).limit(24L).toList();
        for (CompoundTag n2 : own) {
            int dx = (int)(n2.m_128459_("X") / 96.0 * (double)size);
            int dz = (int)(n2.m_128459_("Z") / 96.0 * (double)size);
            g.m_280509_(x + size / 2 + dx - 2, y + size / 2 + dz - 2, x + size / 2 + dx + 2, y + size / 2 + dz + 2, n2.m_128342_("Id").equals(selected) ? -11393 : -5004804);
        }
        for (Tag tag : data.m_128437_("Radar", 10)) {
            CompoundTag n3 = (CompoundTag)tag;
            int dx = (int)(n3.m_128459_("X") / 96.0 * (double)size);
            int dz = (int)(n3.m_128459_("Z") / 96.0 * (double)size);
            g.m_280509_(x + size / 2 + dx - 1, y + size / 2 + dz - 1, x + size / 2 + dx + 2, y + size / 2 + dz + 2, -1610111);
        }
        g.m_280509_(x + size / 2 - 2, y + size / 2 - 2, x + size / 2 + 2, y + size / 2 + 2, -1);
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class EclipseOverlayRegistration {
        @SubscribeEvent
        public static void overlay(RegisterGuiOverlaysEvent e) {
            e.registerAboveAll("eclipse_hud", (gui, g, partial, w, h) -> {
                Minecraft mc = Minecraft.m_91087_();
                if (mc.f_91074_ == null || mc.f_91080_ != null || mc.f_91066_.f_92062_ || data.m_128456_()) {
                    return;
                }
                for (int i = 0; i < 4; ++i) {
                    if (ClientEclipse.pref("hud." + i + ".show", i < 2 ? 1 : 0, 0, 1) == 0) continue;
                    int x = ClientEclipse.pref("hud." + i + ".x", i == 0 ? w - 176 : 8, 0, Math.max(0, w - ClientEclipse.panelWidth(i)));
                    int y = ClientEclipse.pref("hud." + i + ".y", i == 0 ? 8 : 52 + i * 38, 0, Math.max(0, h - ClientEclipse.panelHeight(i)));
                    ClientEclipse.drawPanel(g, i, x, y, w, h);
                }
            });
        }

        @SubscribeEvent
        public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
            e.registerBlockEntityRenderer((BlockEntityType)EclipseContent.ENTITY.get(), EclipseCampRenderer::new);
            e.registerEntityRenderer((EntityType)EclipseContent.FLARE_ENTITY.get(), ThrownItemRenderer::new);
        }
    }
}

