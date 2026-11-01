/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ScreenEvent$KeyPressed$Pre
 *  net.minecraftforge.client.event.ScreenEvent$MouseButtonPressed$Pre
 *  net.minecraftforge.client.event.ScreenEvent$Render$Post
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.GoalNotesScreen;
import dev.shadowsovereign.client.InterfaceHub;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.ModLanguage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class InterfaceAccess {
    private static boolean eligible(Screen s) {
        return s != null && !(s instanceof AbstractContainerScreen) && s.getClass().getPackageName().equals("dev.shadowsovereign.client") && !(s instanceof InterfaceHub) && !(s instanceof GoalNotesScreen);
    }

    @SubscribeEvent
    public static void key(ScreenEvent.KeyPressed.Pre e) {
        if (InterfaceAccess.eligible(e.getScreen()) && e.getKeyCode() == 297) {
            Minecraft.m_91087_().m_91152_((Screen)new InterfaceHub(e.getScreen()));
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void render(ScreenEvent.Render.Post e) {
        Screen s = e.getScreen();
        GuiGraphics g = e.getGuiGraphics();
        if (s.getClass().getPackageName().equals("dev.shadowsovereign.client")) {
            FantasyIcons.draw(g, FantasyIcons.forText(s.getClass().getSimpleName()), 1, 1, 12);
        }
        if (!InterfaceAccess.eligible(s)) {
            return;
        }
        int x = s.f_96543_ - 20;
        int y = s.f_96544_ - 20;
        g.m_280509_(x, y, x + 18, y + 18, -266858456);
        g.m_280509_(x, y, x + 18, y + 1, InterfaceStyle.accent());
        if (InterfaceStyle.flag("label")) {
            ModLanguage.draw(g, Minecraft.m_91087_().f_91062_, "F8", x + 3, y + 5, InterfaceStyle.accent(), false);
        } else {
            FantasyIcons.draw(g, 8, x + 1, y + 1, 16);
        }
        if (!InterfaceStyle.flag("hints") && e.getMouseX() >= x && e.getMouseY() >= y) {
            g.m_280557_(Minecraft.m_91087_().f_91062_, (Component)ModLanguage.component(InterfaceStyle.text("Navigator \u2022 F8", "\u0627\u0644\u0645\u062a\u0635\u0641\u062d \u2022 F8")), e.getMouseX(), e.getMouseY());
        }
    }

    @SubscribeEvent
    public static void click(ScreenEvent.MouseButtonPressed.Pre e) {
        Screen s = e.getScreen();
        if (InterfaceAccess.eligible(s) && e.getButton() == 0 && e.getMouseX() >= (double)(s.f_96543_ - 20) && e.getMouseX() < (double)(s.f_96543_ - 2) && e.getMouseY() >= (double)(s.f_96544_ - 20) && e.getMouseY() < (double)(s.f_96544_ - 2)) {
            Minecraft.m_91087_().m_91152_((Screen)new InterfaceHub(s));
            e.setCanceled(true);
        }
    }
}

