/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RenderHandEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.JourneyScreen;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.network.JourneyNetwork;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientJourney {
    public static CompoundTag data = new CompoundTag();
    public static UUID selected;
    private static long ticks;
    private static long received;
    private static UUID viewing;
    private static CameraType previous;

    public static long now() {
        return data.m_128454_("Time") + ticks - received;
    }

    public static void send(JourneyNetwork.Action action, UUID ally, int value, boolean hard) {
        if (Minecraft.m_91087_().f_91074_ != null) {
            JourneyNetwork.request(new JourneyNetwork.Request(action, ally, value, hard));
        }
    }

    public static void accept(JourneyNetwork.View packet) {
        Screen screen;
        data = packet.data().m_6426_();
        received = ticks;
        Minecraft mc = Minecraft.m_91087_();
        if (data.m_128403_("Camera") && mc.f_91074_ != null && mc.f_91073_ != null) {
            UUID id = data.m_128342_("Camera");
            Entity found = null;
            for (Entity e : mc.f_91073_.m_104735_()) {
                if (!e.m_20148_().equals(id)) continue;
                found = e;
                break;
            }
            if (found == null) {
                ClientJourney.exit(true);
                return;
            }
            if (viewing == null) {
                previous = mc.f_91066_.m_92176_();
            }
            viewing = id;
            mc.f_91066_.m_92157_(CameraType.FIRST_PERSON);
            mc.m_91118_(found);
            if (!(mc.f_91080_ instanceof ViewScreen)) {
                mc.m_91152_((Screen)new ViewScreen());
            }
        } else if (viewing != null) {
            ClientJourney.exit(false);
        }
        if (packet.open() && mc.f_91074_ != null && viewing == null) {
            mc.m_91152_((Screen)new JourneyScreen(selected));
        }
        if ((screen = mc.f_91080_) instanceof JourneyScreen) {
            JourneyScreen screen2 = (JourneyScreen)screen;
            screen2.updated();
        }
    }

    public static void exit(boolean notify) {
        Minecraft mc = Minecraft.m_91087_();
        if (viewing != null) {
            viewing = null;
            if (mc.f_91074_ != null) {
                mc.m_91118_((Entity)mc.f_91074_);
            }
            if (previous != null) {
                mc.f_91066_.m_92157_(previous);
            }
            previous = null;
            if (mc.f_91080_ instanceof ViewScreen) {
                mc.m_91152_(null);
            }
        }
        if (notify) {
            ClientJourney.send(JourneyNetwork.Action.EXIT_CAMERA, null, 0, false);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        ++ticks;
        if (viewing != null) {
            Minecraft mc = Minecraft.m_91087_();
            Entity camera = mc.m_91288_();
            if (mc.f_91074_ == null || mc.f_91073_ == null || camera == null || camera.m_213877_() || !camera.m_6084_() || camera.m_20280_((Entity)mc.f_91074_) > 1024.0 || !(mc.f_91080_ instanceof ViewScreen)) {
                ClientJourney.exit(true);
            }
        }
    }

    @SubscribeEvent
    public static void hand(RenderHandEvent e) {
        if (viewing != null) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        ClientJourney.exit(false);
        data = new CompoundTag();
        selected = null;
        received = 0L;
        ticks = 0L;
    }

    public static final class ViewScreen
    extends Screen {
        public ViewScreen() {
            super((Component)ModLanguage.component("Companion View"));
        }

        protected void m_7856_() {
            this.m_142416_((GuiEventListener)Button.m_253074_((Component)ModLanguage.component("Exit companion view [Esc]"), b -> this.m_7379_()).m_252987_(this.f_96543_ / 2 - 110, this.f_96544_ - 35, 220, 20).m_253136_());
        }

        public boolean m_7043_() {
            return false;
        }

        public void m_7379_() {
            ClientJourney.exit(true);
        }

        public void m_88315_(GuiGraphics g, int x, int y, float partial) {
            String name = "Companion";
            int level = 1;
            for (Tag t : data.m_128437_("Roster", 10)) {
                CompoundTag n = (CompoundTag)t;
                if (!n.m_128403_("Id") || !data.m_128403_("CameraRecord") || !n.m_128342_("Id").equals(data.m_128342_("CameraRecord"))) continue;
                name = ModLanguage.user(n.m_128461_("Name"));
                level = n.m_128451_("Level");
            }
            String stats = " / Lv." + level + " / " + Math.round(data.m_128457_("CameraHealth")) + " / " + Math.round(data.m_128457_("CameraMax")) + " HP";
            List warning = this.f_96547_.m_92923_((FormattedText)ModLanguage.logical("Your body remains in the world. Damage ends viewing."), Math.max(40, this.f_96543_ - 20));
            g.m_280509_(0, 0, this.f_96543_, 30 + warning.size() * 11, -1156573914);
            ModLanguage.centered(g, this.f_96547_, ModLanguage.fit(this.f_96547_, name, Math.max(20, this.f_96543_ - 20 - ModLanguage.width(this.f_96547_, stats))) + stats, this.f_96543_ / 2, 9, -2495489);
            int lineY = 25;
            for (FormattedCharSequence line : warning) {
                ModLanguage.draw(g, this.f_96547_, line, (this.f_96543_ - ModLanguage.width(this.f_96547_, line)) / 2, lineY, -5390125);
                lineY += 11;
            }
            super.m_88315_(g, x, y, partial);
        }
    }
}

