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

public final class OdysseyTheme {
    private static final ResourceLocation ART = new ResourceLocation("shadowsovereign", "textures/gui/odyssey_chapters.png");
    private static final ResourceLocation ICONS = new ResourceLocation("shadowsovereign", "textures/item/odyssey_icons.png");

    public static void art(GuiGraphics g, int tile, int x, int y, int width, int height) {
        if (InterfaceStyle.flag("plain")) {
            g.m_280509_(x, y, x + width, y + height, -15724519);
            return;
        }
        tile = Math.floorMod(tile, 16);
        int col = tile % 4;
        int row = tile / 4;
        int tw = (col + 1) * 1254 / 4 - col * 1254 / 4;
        int th = (row + 1) * 1254 / 4 - row * 1254 / 4;
        float u = (float)(col * 1254) / 4.0f;
        float v = (float)(row * 1254) / 4.0f;
        double aspect = (double)width / (double)Math.max(1, height);
        if (aspect > 1.0) {
            int cropped = Math.max(1, (int)((double)tw / aspect));
            v += (float)(th - cropped) * 0.43f;
            th = cropped;
        } else {
            int cropped = Math.max(1, (int)((double)th * aspect));
            u += (float)(tw - cropped) / 2.0f;
            tw = cropped;
        }
        g.m_280411_(ART, x, y, width, height, u, v, tw, th, 1254, 1254);
        if (InterfaceStyle.flag("dim")) {
            g.m_280509_(x, y, x + width, y + height, -2013265920);
        }
    }

    public static void icon(GuiGraphics g, int tile, int x, int y, int size) {
        tile = Math.floorMod(tile, 32);
        int col = tile % 8;
        int row = tile / 8;
        g.m_280411_(ICONS, x, y, size, size, (float)(col * 1774) / 8.0f, (float)(row * 887) / 4.0f, 221, 221, 1774, 887);
    }

    public static int chapter(int chapter) {
        return (new int[]{0, 1, 2, 4, 5, 6, 14, 2, 3, 14, 12, 15})[Math.floorMod(chapter, 12)];
    }
}

