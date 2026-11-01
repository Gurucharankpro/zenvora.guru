/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.InterfaceStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class RoyalTheme {
    public static final int GOLD = -1981295;
    public static final int TEXT = -988710;
    public static final int MUTED = -4937283;
    public static final int INK = -15527397;
    public static final ResourceLocation ART = new ResourceLocation("shadowsovereign", "textures/gui/royal_chapters.png");
    public static final ResourceLocation ICONS = new ResourceLocation("shadowsovereign", "textures/gui/royal_icons.png");

    public static void art(GuiGraphics g, int tile, int x, int y, int w, int h) {
        if (InterfaceStyle.flag("plain")) {
            g.m_280509_(x, y, x + w, y + h, -15724519);
            return;
        }
        int t = Math.floorMod(tile, 8);
        float ratio = (float)w / (float)Math.max(1, h);
        int cw = 627;
        int ch = 313;
        if (ratio > 2.0f) {
            ch = Math.max(1, (int)(627.0f / ratio));
        } else {
            cw = Math.max(1, (int)(313.5 * (double)ratio));
        }
        g.m_280411_(ART, x, y, w, h, (float)(t % 2 * 627) + (float)(627 - cw) / 2.0f, (float)(t / 2) * 313.5f + (313.5f - (float)ch) / 2.0f, cw, ch, 1254, 1254);
        if (InterfaceStyle.flag("dim")) {
            g.m_280509_(x, y, x + w, y + h, -2013265920);
        }
    }

    public static void icon(GuiGraphics g, int tile, int x, int y, int size) {
        int t = Math.floorMod(tile, 24);
        g.m_280411_(ICONS, x, y, size, size, (float)(t % 6) * 256.0f, (float)(t / 6) * 256.0f, 256, 256, 1536, 1024);
    }

    public static void frame(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.m_280509_(x + 2, y + 2, x + w + 2, y + h + 2, 0x33000000);
        g.m_280509_(x, y, x + w, y + h, InterfaceStyle.flag("contrast") ? -16250865 : color);
        g.m_280509_(x, y, x + w, y + 1, InterfaceStyle.accent());
        g.m_280509_(x, y + h - 1, x + w, y + h, -11188685);
        g.m_280509_(x, y, x + 1, y + h, -11188685);
        g.m_280509_(x + w - 1, y, x + w, y + h, -11188685);
        if (!InterfaceStyle.flag("simple")) {
            for (int corner = 0; corner < 4; ++corner) {
                int xx = corner % 2 == 0 ? x : x + w - 7;
                int yy = corner < 2 ? y : y + h - 7;
                g.m_280509_(xx + 2, yy + 2, xx + 5, yy + 5, -1981295);
            }
        }
    }

    public static void book(GuiGraphics g, int w, int h, int tile) {
        g.m_280024_(0, 0, w, h, -15396063, -16250094);
        RoyalTheme.art(g, tile, 0, 0, w, 96);
        g.m_280509_(0, 0, w, 2, InterfaceStyle.accent());
        g.m_280024_(0, 0, w, 100, -1725950422, -15658469);
        RoyalTheme.frame(g, 5, 5, w - 10, h - 10, 404296736);
        g.m_280509_(10, 10, 11, h - 10, 1717126454);
        g.m_280509_(w - 11, 10, w - 10, h - 10, 1717126454);
    }
}

