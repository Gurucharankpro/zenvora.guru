/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.EclipseScreen;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.eclipse.EclipseText;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class EclipseHudScreen
extends Screen {
    private int dragging = -1;
    private int grabX;
    private int grabY;
    private int selected;

    public EclipseHudScreen() {
        super((Component)EclipseText.c("hud_editor", new Object[0]));
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        ModLanguage.draw(g, this.f_96547_, ClientEclipse.t("hud_help", new Object[0]), 8, 8, -1322605, false);
        for (int i = 0; i < 4; ++i) {
            int x = ClientEclipse.pref("hud." + i + ".x", 8 + i * 12, 0, Math.max(0, this.f_96543_ - ClientEclipse.panelWidth(i)));
            int y = ClientEclipse.pref("hud." + i + ".y", 40 + i * 38, 0, Math.max(0, this.f_96544_ - ClientEclipse.panelHeight(i) - 60));
            ClientEclipse.drawPanel(g, i, x, y, this.f_96543_, this.f_96544_);
            if (i != this.selected) continue;
            g.m_280509_(x, y, x + ClientEclipse.panelWidth(i), y + 1, -11393);
        }
        String[] buttons = new String[]{"toggle_panel", "smaller", "larger", "opacity", "preset_compact", "preset_balanced", "preset_cinematic", "back"};
        for (int i = 0; i < buttons.length; ++i) {
            int x = 8 + i % 4 * (this.f_96543_ - 16) / 4;
            int y = this.f_96544_ - 54 + i / 4 * 26;
            g.m_280509_(x, y, x + (this.f_96543_ - 24) / 4, y + 23, -297389995);
            ModLanguage.draw(g, this.f_96547_, ModLanguage.fit(this.f_96547_, ClientEclipse.t(buttons[i], new Object[0]), (this.f_96543_ - 32) / 4), x + 4, y + 7, -989185, false);
        }
    }

    public boolean m_6375_(double x, double y, int button) {
        if (button != 0) {
            return false;
        }
        if (y >= (double)(this.f_96544_ - 54)) {
            int col;
            int row = y >= (double)(this.f_96544_ - 28) ? 1 : 0;
            int index = row * 4 + (col = Math.min(3, Math.max(0, ((int)x - 8) / Math.max(1, (this.f_96543_ - 16) / 4))));
            if (index == 0) {
                ClientPreferences.set("eclipse.hud." + this.selected + ".show", Integer.toString(1 - ClientEclipse.pref("hud." + this.selected + ".show", this.selected < 2 ? 1 : 0, 0, 1)));
            }
            if (index == 1 || index == 2) {
                ClientPreferences.set("eclipse.hud.scale", Integer.toString(ClientEclipse.pref("hud.scale", 100, 70, 140) + (index == 1 ? -10 : 10)));
            }
            if (index == 3) {
                ClientPreferences.set("eclipse.hud.alpha", Integer.toString(ClientEclipse.pref("hud.alpha", 190, 80, 240) >= 240 ? 80 : ClientEclipse.pref("hud.alpha", 190, 80, 240) + 30));
            }
            if (index >= 4 && index <= 6) {
                for (int i = 0; i < 4; ++i) {
                    ClientPreferences.set("eclipse.hud." + i + ".x", Integer.toString(i == 2 ? 8 : (index == 4 ? 8 : Math.max(8, this.f_96543_ - 176))));
                    ClientPreferences.set("eclipse.hud." + i + ".y", Integer.toString(i == 2 ? Math.max(24, this.f_96544_ - 180) : 8 + (i == 3 ? 2 : i) * 38));
                    ClientPreferences.set("eclipse.hud." + i + ".show", i < 2 || index == 6 ? "1" : "0");
                }
                ClientPreferences.set("eclipse.hud.scale", index == 4 ? "80" : (index == 6 ? "120" : "100"));
            }
            if (index == 7) {
                this.m_7379_();
            }
            return true;
        }
        for (int i = 3; i >= 0; --i) {
            int xx = ClientEclipse.pref("hud." + i + ".x", 8 + i * 12, 0, Math.max(0, this.f_96543_ - ClientEclipse.panelWidth(i)));
            int yy = ClientEclipse.pref("hud." + i + ".y", 40 + i * 38, 0, Math.max(0, this.f_96544_ - ClientEclipse.panelHeight(i) - 60));
            if (!(x >= (double)xx) || !(x < (double)(xx + ClientEclipse.panelWidth(i))) || !(y >= (double)yy) || !(y < (double)(yy + ClientEclipse.panelHeight(i)))) continue;
            this.selected = this.dragging = i;
            this.grabX = (int)x - xx;
            this.grabY = (int)y - yy;
            return true;
        }
        return false;
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (this.dragging < 0) {
            return false;
        }
        ClientPreferences.set("eclipse.hud." + this.dragging + ".x", Integer.toString(Math.max(0, Math.min(this.f_96543_ - ClientEclipse.panelWidth(this.dragging), (int)x - this.grabX))));
        ClientPreferences.set("eclipse.hud." + this.dragging + ".y", Integer.toString(Math.max(24, Math.min(this.f_96544_ - 60 - ClientEclipse.panelHeight(this.dragging), (int)y - this.grabY))));
        return true;
    }

    public boolean m_6348_(double x, double y, int b) {
        this.dragging = -1;
        return true;
    }

    public void m_7379_() {
        this.f_96541_.m_91152_((Screen)new EclipseScreen(0));
    }
}

