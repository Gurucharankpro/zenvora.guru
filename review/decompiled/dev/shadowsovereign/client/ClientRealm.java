/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RenderGuiOverlayEvent$Post
 *  net.minecraftforge.client.event.ViewportEvent$ComputeFogColor
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.CosmicSky;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RealmScreen;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.network.RealmNetwork;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientRealm {
    public static CompoundTag data = new CompoundTag();
    private static int ticks;

    public static String t(String key, Object ... args) {
        return ModLanguage.user(LivingRealm.text(key, args).getString());
    }

    public static void accept(RealmNetwork.View v) {
        CompoundTag fresh;
        CompoundTag compoundTag = fresh = v.data() == null ? new CompoundTag() : v.data();
        if (!fresh.m_128441_("Bestiary")) {
            fresh.m_128365_("Bestiary", (Tag)data.m_128437_("Bestiary", 10).m_6426_());
        }
        data = fresh;
        if (v.open() && Minecraft.m_91087_().f_91074_ != null) {
            Minecraft.m_91087_().m_91152_((Screen)new RealmScreen(v.page()));
        }
    }

    public static void open() {
        RealmNetwork.request(RealmNetwork.Action.OPEN, 0, 0, null);
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
        ticks = 0;
        CosmicSky.release();
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft m = Minecraft.m_91087_();
        if (m.f_91074_ == null) {
            return;
        }
        if (m.f_91080_ instanceof RealmScreen && ++ticks % 60 == 0) {
            RealmNetwork.request(RealmNetwork.Action.VIEW, 0, 0, null);
        }
        if (m.f_91073_ == null || !m.f_91073_.m_46472_().equals(OdysseyRealm.KEY) || data.m_128471_("Reduced") || !data.m_128471_("Ready")) {
            return;
        }
        int r = data.m_128451_("Region");
        if (r < 0 || r > 3) {
            return;
        }
        if (ticks % 10 == 0) {
            m.f_91073_.m_7106_((ParticleOptions)(new SimpleParticleType[]{ParticleTypes.f_276452_, ParticleTypes.f_175828_, ParticleTypes.f_123809_, ParticleTypes.f_123746_})[r], m.f_91074_.m_20185_() + (double)m.f_91073_.f_46441_.m_188503_(12) - 6.0, m.f_91074_.m_20186_() + 2.0, m.f_91074_.m_20189_() + (double)m.f_91073_.f_46441_.m_188503_(12) - 6.0, 0.0, 0.01, 0.0);
        }
        if (ticks % 600 == 0) {
            m.f_91074_.m_5496_((new SoundEvent[]{SoundEvents.f_144243_, SoundEvents.f_144120_, SoundEvents.f_11713_, SoundEvents.f_12404_})[r], 0.12f, 0.8f);
        }
    }

    @SubscribeEvent
    public static void fog(ViewportEvent.ComputeFogColor e) {
        Minecraft m = Minecraft.m_91087_();
        if (m.f_91073_ == null || !m.f_91073_.m_46472_().equals(OdysseyRealm.KEY) || data.m_128471_("Reduced")) {
            return;
        }
        int r = data.m_128451_("Region");
        if (r < 0 || r > 3) {
            return;
        }
        float[][] colors = new float[][]{{0.24f, 0.39f, 0.33f}, {0.38f, 0.29f, 0.22f}, {0.28f, 0.27f, 0.45f}, {0.25f, 0.17f, 0.31f}};
        e.setRed(colors[r][0]);
        e.setGreen(colors[r][1]);
        e.setBlue(colors[r][2]);
    }

    @SubscribeEvent
    public static void hud(RenderGuiOverlayEvent.Post e) {
        if (!e.getOverlay().id().m_135815_().equals("hotbar")) {
            return;
        }
        Minecraft m = Minecraft.m_91087_();
        if (m.f_91074_ == null || m.f_91080_ != null || m.f_91066_.f_92062_ || m.f_91073_ == null || !m.f_91073_.m_46472_().equals(OdysseyRealm.KEY)) {
            return;
        }
        GuiGraphics g = e.getGuiGraphics();
        int w = m.m_91268_().m_85445_();
        int x = Math.max(8, w - 205);
        RoyalTheme.frame(g, x, 8, 197, !data.m_128471_("Ready") || !data.m_128471_("GloomReady") ? 59 : 43, -870902752);
        int r = data.m_128451_("Region");
        Optional biome = m.f_91073_.m_204166_(m.f_91074_.m_20183_()).m_203543_();
        String name = r >= 0 && r < 4 ? ClientRealm.t("region." + r, new Object[0]) : biome.map(k -> ModLanguage.user(Component.m_237115_((String)("biome." + k.m_135782_().m_135827_() + "." + k.m_135782_().m_135815_())).getString())).orElse(ClientRealm.t("home", new Object[0]));
        ModLanguage.draw(g, m.f_91062_, ModLanguage.fit(m.f_91062_, name, 180), x + 8, 16, -1981295, false);
        Object line = "X " + m.f_91074_.m_146903_() + " \u00b7 Z " + m.f_91074_.m_146907_();
        CompoundTag run = data.m_128469_("Run");
        if (!run.m_128456_()) {
            line = ClientRealm.t("enemies", run.m_128451_("Enemies"));
        }
        ModLanguage.draw(g, m.f_91062_, ModLanguage.fit(m.f_91062_, (String)line, 180), x + 8, 32, -988710, false);
        if (!data.m_128471_("Ready") || !data.m_128471_("GloomReady")) {
            int percent = data.m_128451_("Cursor") * 100 / 820512;
            int forest = Math.min(100, data.m_128451_("GloomCursor") * 100 / 838322);
            String status = ModLanguage.user(Component.m_237110_((String)"expanse.shadowsovereign.preparing", (Object[])new Object[]{percent, forest}).getString());
            ModLanguage.draw(g, m.f_91062_, ModLanguage.fit(m.f_91062_, status, 180), x + 8, 46, -4937283, false);
        }
    }
}

