/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.GrimoireIndexScreen;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.LegendComparisonScreen;
import dev.shadowsovereign.client.LegendWorkbench;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.QuestPlannerScreen;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.network.OdysseyNetwork;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public abstract class ArtisanPage
extends Screen {
    protected final Screen parent;
    protected final String storage;
    protected int scroll;
    protected int max;
    protected int cursor;
    protected int bottom;
    protected String query = "";
    protected EditBox search;
    private boolean initialized;
    private boolean drag;
    private boolean thumb;
    private int ticks;
    private double downY;
    private double grab;
    private int downScroll;
    private int focus = -1;
    private Hit pending;
    private final List<Hit> hits = new ArrayList<Hit>();

    protected ArtisanPage(Screen parent, String storage, String title) {
        super((Component)ModLanguage.component(title));
        this.parent = ArtisanPage.safeParent(parent);
        this.storage = storage;
    }

    private static Screen safeParent(Screen p) {
        Screen cursor = p;
        int depth = 0;
        while (cursor instanceof ArtisanPage) {
            ArtisanPage page = (ArtisanPage)cursor;
            if (++depth >= 6) {
                return page.parent;
            }
            cursor = page.parent;
        }
        return p;
    }

    protected String on(boolean value) {
        return this.t(value ? "ON" : "OFF", value ? "\u0645\u0641\u0639\u0644" : "\u0645\u0639\u0637\u0644");
    }

    protected String t(String en, String ar) {
        return InterfaceStyle.text(en, ar);
    }

    protected abstract void content(GuiGraphics var1);

    protected String pageTitle() {
        return this.m_96636_().getString();
    }

    protected String footer() {
        return this.t("Scroll \u2022 swipe \u2022 Tab / Enter \u2022 Esc back", "\u0645\u0631\u0631 \u0623\u0648 \u0627\u0633\u062d\u0628 \u2022 Tab / Enter \u2022 Esc \u0631\u062c\u0648\u0639");
    }

    protected boolean searchable() {
        return true;
    }

    protected void m_7856_() {
        if (!this.initialized) {
            this.initialized = true;
            this.query = ClientPreferences.get(this.storage + ".query", "");
            try {
                this.scroll = Math.max(0, Integer.parseInt(ClientPreferences.get(this.storage + ".scroll", "0")));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        if (this.searchable()) {
            this.search = (EditBox)this.m_142416_((GuiEventListener)new EditBox(this.f_96547_, 18, 43, this.f_96543_ - 36, 23, (Component)ModLanguage.component(this.t("Search", "\u0628\u062d\u062b"))));
            this.search.m_94199_(96);
            this.search.m_257771_((Component)ModLanguage.component(this.t("Search names, details or notes\u2026", "\u0627\u0628\u062d\u062b \u0628\u0627\u0644\u0627\u0633\u0645 \u0623\u0648 \u0627\u0644\u062a\u0641\u0627\u0635\u064a\u0644 \u0623\u0648 \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0627\u062a\u2026")));
            this.search.m_94149_((s, i) -> FormattedCharSequence.m_13714_((String)ModLanguage.visual(this.f_96547_, ModLanguage.user(s)), (Style)ModLanguage.component(ModLanguage.user(s)).m_7383_()));
            this.search.m_94144_(this.query);
            this.search.m_94151_(s -> {
                this.query = s;
                this.scroll = 0;
                this.focus = -1;
            });
        }
    }

    protected boolean matches(String value) {
        String q = this.query.strip().toLowerCase(Locale.ROOT);
        return q.isBlank() || value.replace("\ue000", "").replace("\ue001", "").toLowerCase(Locale.ROOT).contains(q) || ModLanguage.tr(value).toLowerCase(Locale.ROOT).contains(q);
    }

    protected void text(GuiGraphics g, String s, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, color, false);
    }

    protected void row(GuiGraphics g, int icon, String title, String description, boolean enabled, Runnable action) {
        List lines = description.isBlank() ? List.of() : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(description), this.f_96543_ - 98);
        int h = Math.max(InterfaceStyle.flag("touch") ? 48 : 34, lines.isEmpty() ? 34 : 29 + lines.size() * 13);
        int y = this.cursor;
        this.cursor += h + 7;
        if (y + h < 72 || y >= this.bottom) {
            return;
        }
        RoyalTheme.frame(g, 18, y, this.f_96543_ - 48, h, enabled ? -266791900 : -267317224);
        FantasyIcons.draw(g, icon, 24, y + 6, 22);
        this.text(g, ModLanguage.fit(this.f_96547_, title, this.f_96543_ - 108), 53, y + 8, enabled ? InterfaceStyle.accent() : -4937283);
        int yy = y + 26;
        for (FormattedCharSequence line : lines) {
            ModLanguage.draw(g, this.f_96547_, line, 53, yy, -988710, false);
            yy += 13;
        }
        if (action != null && enabled && y >= 72 && y + h <= this.bottom) {
            this.hits.add(new Hit(18, y, this.f_96543_ - 48, h, action));
        }
    }

    protected void row(GuiGraphics g, int icon, String title, String description, Runnable action) {
        this.row(g, icon, title, description, true, action);
    }

    protected void meter(GuiGraphics g, String label, double value, double total) {
        this.row(g, 12, label, BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + " / " + BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(), true, null);
        int y = this.cursor - 11;
        g.m_280509_(54, y, this.f_96543_ - 40, y + 3, -13489344);
        g.m_280509_(54, y, 54 + (int)((double)(this.f_96543_ - 94) * Math.max(0.0, Math.min(1.0, value / Math.max(1.0, total)))), y + 3, InterfaceStyle.accent());
    }

    protected void heading(GuiGraphics g, int icon, String label) {
        this.row(g, icon, label, "", true, null);
    }

    protected void button(GuiGraphics g, int x, int y, int w, String label, Runnable action) {
        RoyalTheme.frame(g, x, y, w, 24, -265740231);
        FantasyIcons.label(g, this.f_96547_, label, x, y, w, 24, -988710);
        this.hits.add(new Hit(x, y, w, 24, action));
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        if (this.search != null) {
            this.search.m_257771_((Component)ModLanguage.component(this.t("Search names, details or notes\u2026", "\u0627\u0628\u062d\u062b \u0628\u0627\u0644\u0627\u0633\u0645 \u0623\u0648 \u0627\u0644\u062a\u0641\u0627\u0635\u064a\u0644 \u0623\u0648 \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0627\u062a\u2026")));
        }
        this.hits.clear();
        this.bottom = this.f_96544_ - 42;
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, 3);
        FantasyIcons.draw(g, 3, 16, 12, 20);
        this.text(g, ModLanguage.fit(this.f_96547_, this.pageTitle(), this.f_96543_ - 145), 42, 18, -1981295);
        this.button(g, this.f_96543_ - 92, 9, 74, this.t("Return", "\u0631\u062c\u0648\u0639"), this::m_7379_);
        if (!this.searchable()) {
            this.text(g, ModLanguage.fit(this.f_96547_, this.footer(), this.f_96543_ - 42), 18, 48, -4937283);
        }
        g.m_280588_(12, 72, this.f_96543_ - 24, this.bottom);
        this.cursor = 77 - this.scroll;
        this.content(g);
        g.m_280618_();
        this.max = Math.max(0, this.cursor + this.scroll - this.bottom + 4);
        this.scroll = Math.max(0, Math.min(this.max, this.scroll));
        if (this.max > 0) {
            int track = this.bottom - 72;
            int h = Math.max(20, track * track / (track + this.max));
            int y = 72 + (track - h) * this.scroll / this.max;
            g.m_280509_(this.f_96543_ - 19, 72, this.f_96543_ - 8, this.bottom, -14344908);
            g.m_280509_(this.f_96543_ - 18, y, this.f_96543_ - 9, y + h, InterfaceStyle.accent());
        }
        int w = (this.f_96543_ - 48) / 3;
        this.button(g, 16, this.f_96544_ - 32, w, this.t("Top", "\u0627\u0644\u0628\u062f\u0627\u064a\u0629"), () -> {
            this.scroll = 0;
        });
        this.button(g, 24 + w, this.f_96544_ - 32, w, "English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", ModLanguage::toggle);
        this.button(g, 32 + 2 * w, this.f_96544_ - 32, w, this.t("Clear filters", "\u0645\u0633\u062d \u0627\u0644\u0628\u062d\u062b"), () -> {
            this.query = "";
            if (this.search != null) {
                this.search.m_94144_("");
            }
            this.scroll = 0;
            this.resetFilters();
        });
        for (int i = 0; i < this.hits.size(); ++i) {
            Hit h = this.hits.get(i);
            if (!h.hit(mx, my) && this.focus != i) continue;
            g.m_280509_(h.x, h.y, h.x + 2, h.y + h.h, InterfaceStyle.accent());
        }
        super.m_88315_(g, mx, my, partial);
    }

    public void m_86600_() {
        if (++this.ticks % 60 == 0 && (this instanceof GrimoireIndexScreen || this instanceof LegendWorkbench || this instanceof LegendComparisonScreen)) {
            MythicNetwork.request(MythicNetwork.Action.VIEW);
        }
        if (this.ticks % 60 == 0 && this instanceof QuestPlannerScreen) {
            OdysseyNetwork.request(OdysseyNetwork.Action.VIEW, null, 0, 0, 0, "");
        }
    }

    protected void resetView() {
        this.query = "";
        if (this.search != null) {
            this.search.m_94144_("");
        }
        this.scroll = 0;
        this.resetFilters();
    }

    protected void resetFilters() {
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b != 0) {
            return super.m_6375_(x, y, b);
        }
        if (this.search != null && y >= 43.0 && y < 66.0) {
            return super.m_6375_(x, y, b);
        }
        this.m_7522_(null);
        this.downY = y;
        this.downScroll = this.scroll;
        this.drag = false;
        this.pending = null;
        boolean bl = this.thumb = x >= (double)(this.f_96543_ - 22) && y >= 72.0 && y < (double)this.bottom && this.max > 0;
        if (this.thumb) {
            this.grab = y;
            return true;
        }
        for (Hit h : this.hits) {
            if (!h.hit(x, y)) continue;
            this.pending = h;
            break;
        }
        return true;
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (b != 0) {
            return false;
        }
        if (this.thumb) {
            this.scroll = Math.max(0, Math.min(this.max, this.downScroll + (int)((y - this.grab) * (double)this.max / (double)Math.max(1, this.bottom - 72))));
            return true;
        }
        if (Math.abs(y - this.downY) > 5.0) {
            this.drag = true;
            this.scroll = Math.max(0, Math.min(this.max, this.downScroll + (int)(this.downY - y)));
            this.pending = null;
        }
        return true;
    }

    public boolean m_6348_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        if (!this.drag && !this.thumb && this.pending != null && this.pending.hit(x, y)) {
            this.pending.action.run();
        }
        this.pending = null;
        this.thumb = false;
        return true;
    }

    public boolean m_6050_(double x, double y, double a) {
        this.scroll = Math.max(0, Math.min(this.max, this.scroll - (int)(a * 32.0)));
        return true;
    }

    public boolean m_7933_(int k, int scan, int mods) {
        EditBox field;
        if (k == 70 && ArtisanPage.m_96637_() && this.search != null) {
            this.m_7522_((GuiEventListener)this.search);
            return true;
        }
        GuiEventListener guiEventListener = this.m_7222_();
        if (guiEventListener instanceof EditBox && (field = (EditBox)guiEventListener).m_93696_()) {
            if (k == 256) {
                this.m_7522_(null);
                return true;
            }
            return super.m_7933_(k, scan, mods);
        }
        if (k == 268) {
            this.scroll = 0;
            this.pending = null;
            return true;
        }
        if (k == 269) {
            this.scroll = this.max;
            this.pending = null;
            return true;
        }
        if (k == 267 || k == 266) {
            this.scroll = Math.max(0, Math.min(this.max, this.scroll + (k == 267 ? 1 : -1) * Math.max(32, this.bottom - 84)));
            this.pending = null;
            return true;
        }
        if (k == 258 && !this.hits.isEmpty()) {
            this.focus = Math.floorMod(this.focus + (ArtisanPage.m_96638_() ? -1 : 1), this.hits.size());
            return true;
        }
        if (k == 257 && this.focus >= 0 && this.focus < this.hits.size()) {
            this.hits.get((int)this.focus).action.run();
            return true;
        }
        return super.m_7933_(k, scan, mods);
    }

    public void m_7861_() {
        ClientPreferences.setAll(Map.of(this.storage + ".query", this.query, this.storage + ".scroll", Integer.toString(this.scroll)));
        super.m_7861_();
    }

    public void m_7379_() {
        this.f_96541_.m_91152_(this.parent);
    }

    public boolean m_7043_() {
        return false;
    }

    private record Hit(int x, int y, int w, int h, Runnable action) {
        boolean hit(double a, double b) {
            return a >= (double)this.x && a < (double)(this.x + this.w) && b >= (double)this.y && b < (double)(this.y + this.h);
        }
    }
}

