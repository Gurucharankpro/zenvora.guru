/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.client.event.RenderGuiOverlayEvent$Post
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.network.OdysseyNetwork;
import dev.shadowsovereign.network.RealmNetwork;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class OdysseyHud {
    @SubscribeEvent
    public static void key(InputEvent.Key e) {
        Minecraft m = Minecraft.m_91087_();
        if (m.f_91074_ == null || m.f_91080_ != null || e.getAction() != 1 || !ClientOdyssey.INTERACT.isActiveAndMatches(InputConstants.m_84827_((int)e.getKey(), (int)e.getScanCode()))) {
            return;
        }
        while (ClientOdyssey.INTERACT.m_90859_()) {
        }
        CompoundTag run = ClientOdyssey.data.m_128469_("Run");
        if (run.m_128456_() && m.f_91073_ != null && m.f_91073_.m_46472_().equals(OdysseyRealm.KEY)) {
            RealmNetwork.request(RealmNetwork.Action.QUICK, 0, 0, null);
            return;
        }
        if (!run.m_128456_()) {
            OdysseyNetwork.request(OdysseyNetwork.Action.QUICK, null, 0, 0, run.m_128451_("Turn"), "");
        }
    }

    @SubscribeEvent
    public static void overlay(RenderGuiOverlayEvent.Post e) {
        if (!e.getOverlay().id().m_135815_().equals("hotbar")) {
            return;
        }
        Minecraft m = Minecraft.m_91087_();
        if (m.f_91074_ == null || m.f_91066_.f_92062_ || m.f_91080_ != null) {
            return;
        }
        CompoundTag d = ClientOdyssey.data;
        CompoundTag run = d.m_128469_("Run");
        int pinned = d.m_128451_("Pinned");
        if (run.m_128456_() && (pinned < 0 || pinned >= OdysseyCatalog.goals().size())) {
            return;
        }
        GuiGraphics g = e.getGuiGraphics();
        int width = Math.min(300, m.m_91268_().m_85445_() - 24);
        int y = m.m_91268_().m_85446_() - 102;
        RoyalTheme.frame(g, 12, y, width, 66, -854060512);
        if (!run.m_128456_()) {
            OdysseyCatalog.Mode mode = OdysseyCatalog.Mode.read(run.m_128451_("Mode"));
            if (mode == null) {
                return;
            }
            String title = ClientOdyssey.t("feature." + mode.feature, new Object[0]);
            String footer = ClientOdyssey.t("quick_key", ClientOdyssey.INTERACT.m_90863_().getString(), ClientArmy.OPEN.m_90863_().getString());
            ModLanguage.draw(g, m.f_91062_, ModLanguage.fit(m.f_91062_, title, width - 15), 20, y + 8, -1981295, false);
            ModLanguage.draw(g, m.f_91062_, ModLanguage.fit(m.f_91062_, footer, width - 15), 20, y + 46, -988710, false);
            long time = (long)run.m_128451_("Elapsed") + (run.m_128451_("Phase") == 0 ? Math.max(0L, ClientOdyssey.now() - d.m_128454_("Time")) : 0L);
            String state = ClientOdyssey.t("hud_clock", Math.max(0L, ((long)run.m_128451_("Duration") - time) / 20L), run.m_128451_("Score"));
            switch (mode) {
                case TUG: {
                    state = ClientOdyssey.t(time % 20L <= 5L ? "hud_pull_now" : "hud_wait_beat", String.format(Locale.ROOT, "%.1f", run.m_128459_("Rope")));
                    break;
                }
                case CHAIRS: {
                    state = ClientOdyssey.t(time - (long)run.m_128451_("RoundStarted") < (long)(120 - run.m_128451_("Difficulty") * 20) ? "hud_music" : "hud_take_seat", new Object[0]);
                    break;
                }
                case STATUES: {
                    state = ClientOdyssey.t(time % 120L < 80L ? "hud_green" : "hud_red", new Object[0]);
                    break;
                }
                case POTATO: {
                    state = ClientOdyssey.t("hud_potato", Math.max(0L, ((long)run.m_128451_("HotUntil") - time) / 20L));
                    break;
                }
                case LOOP: {
                    state = ClientOdyssey.t("hud_loop", (1800L - time % 1800L) / 20L, Integer.bitCount(run.m_128451_("Levers")));
                    break;
                }
                case MINE: {
                    state = ClientOdyssey.t("ore_progress", run.m_128451_("Ore"), run.m_128451_("Stability"));
                    break;
                }
                case DUNGEON: {
                    state = ClientOdyssey.t("hud_dungeon", run.m_128451_("Stage") + 1, Math.floorMod(run.m_128451_("Stage") + run.m_128451_("Layout"), 4) + 1);
                    break;
                }
            }
            ModLanguage.draw(g, m.f_91062_, ModLanguage.fit(m.f_91062_, state, width - 15), 20, y + 27, -988710, false);
        } else {
            OdysseyCatalog.Goal goal = OdysseyCatalog.goals().get(pinned);
            String title = ClientOdyssey.t("goal." + goal.id(), new Object[0]);
            ModLanguage.draw(g, m.f_91062_, ModLanguage.fit(m.f_91062_, title, width - 15), 20, y + 8, -1981295, false);
            ModLanguage.draw(g, m.f_91062_, ModLanguage.fit(m.f_91062_, ClientOdyssey.t("guide_key", ClientArmy.OPEN.m_90863_().getString()), width - 15), 20, y + 25, -988710, false);
        }
    }
}

