/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.network.chat.Component
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

final class StyledButton
extends Button {
    StyledButton(int x, int y, int w, int h, String text, Runnable action) {
        super(x, y, w, h, (Component)ModLanguage.component(text), b -> action.run(), f_252438_);
    }

    public void m_87963_(GuiGraphics g, int mx, int my, float partial) {
        boolean hover = this.m_198029_();
        int x = this.m_252754_();
        int y = this.m_252907_();
        int w = this.m_5711_();
        int h = this.m_93694_();
        RoyalTheme.frame(g, x, y, w, h, !this.f_93623_ ? -15396067 : (hover ? -13096883 : -266594520));
        if (hover && this.f_93623_) {
            g.m_280509_(x, y, x + 2, y + h, InterfaceStyle.accent());
        }
        Font font = Minecraft.m_91087_().f_91062_;
        FantasyIcons.label(g, font, this.m_6035_().getString(), x, y, w, h, !this.f_93623_ ? -4937283 : -988710);
    }
}

