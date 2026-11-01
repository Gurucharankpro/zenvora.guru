/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.TouchDeck;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class TouchDeckScreen
extends Screen {
    private final UUID selected;
    private boolean editing;
    private float scale;
    private float left;
    private float top;
    private static final int W = 620;
    private static final int H = 360;

    public TouchDeckScreen(UUID id) {
        super((Component)ModLanguage.component("Touch Command Deck"));
        this.selected = id;
    }

    protected void m_7856_() {
        this.scale = Math.min(1.5f, Math.min(((float)this.f_96543_ - 14.0f) / 620.0f, ((float)this.f_96544_ - 14.0f) / 360.0f));
        this.left = ((float)this.f_96543_ / this.scale - 620.0f) / 2.0f;
        this.top = ((float)this.f_96544_ / this.scale - 360.0f) / 2.0f;
    }

    public boolean m_7043_() {
        return false;
    }

    private void text(GuiGraphics g, String s, int x, int y, int c) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, c, false);
    }

    public void m_88315_(GuiGraphics g, int x, int y, float partial) {
        this.m_280273_(g);
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.left, this.top, 0.0f);
        MenuTheme.panel(g, 620, 360, 1);
        this.text(g, "TOUCH COMMAND DECK", 20, 18, -1522047);
        ShadowNetwork.Entry ally = ClientArmy.view.entries().stream().filter(e -> e.id().equals(this.selected)).findFirst().orElse(null);
        this.text(g, (String)(ally == null ? "Select a companion in the army first." : "Selected: " + ModLanguage.user(ally.name())), 20, 42, -2767129);
        this.text(g, this.editing ? "Editing: tap a slot to cycle its action. Changes are saved." : "Tap an action. Server rules, levels and cooldowns still apply.", 20, 75, -4807483);
        for (int i = 0; i < 6; ++i) {
            int xx = 20 + i % 3 * 196;
            int yy = 106 + i / 3 * 89;
            MenuTheme.art(g, 1 + i, xx, yy, 184, 76);
            g.m_280509_(xx, yy, xx + 184, yy + 76, -1156117211);
            FantasyIcons.draw(g, FantasyIcons.forText(TouchDeck.slots[i].label), xx + 10, yy + 6, 22);
            ModLanguage.wrap(g, this.f_96547_, (Component)Component.m_237113_((String)TouchDeck.slots[i].label), xx + 10, yy + 31, 164, -988673);
        }
        g.m_280509_(20, 302, 302, 334, -13032373);
        g.m_280509_(314, 302, 600, 334, -13032373);
        this.text(g, this.editing ? "Done editing" : "Customize six actions", 30, 314, -988673);
        this.text(g, "Back to army", 328, 314, -988673);
        g.m_280168_().m_85849_();
    }

    public boolean m_6375_(double x, double y, int button) {
        if (button != 0) {
            return false;
        }
        double mx = x / (double)this.scale - (double)this.left;
        double my = y / (double)this.scale - (double)this.top;
        if (my >= 302.0 && my < 334.0) {
            if (mx >= 20.0 && mx < 302.0) {
                this.editing = !this.editing;
            } else if (mx >= 314.0 && mx < 600.0) {
                this.m_7379_();
            }
            return true;
        }
        for (int i = 0; i < 6; ++i) {
            int xx = 20 + i % 3 * 196;
            int yy = 106 + i / 3 * 89;
            if (!(mx >= (double)xx) || !(mx < (double)(xx + 184)) || !(my >= (double)yy) || !(my < (double)(yy + 76))) continue;
            if (this.editing) {
                TouchDeck.cycle(i);
            } else {
                TouchDeck.run(i, this.selected);
            }
            return true;
        }
        return false;
    }

    public void m_7379_() {
        this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected));
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (ClientArmy.OPEN.m_90832_(key, scan)) {
            this.m_7379_();
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }
}

