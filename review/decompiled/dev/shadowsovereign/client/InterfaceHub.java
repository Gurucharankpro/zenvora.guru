/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientMythic;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ClientRealm;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.ClientSoulLife;
import dev.shadowsovereign.client.ClientVigil;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.GrimoireIndexScreen;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.QuestPlannerScreen;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.client.StyledButton;
import dev.shadowsovereign.client.WorkshopManualScreen;
import dev.shadowsovereign.network.MythicNetwork;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class InterfaceHub
extends Screen {
    private final Screen previous;
    private EditBox search;
    private String query = "";
    private int category;
    private int offset;
    private boolean favoritesOnly;
    private boolean recentOnly;
    private boolean settings;
    private final Set<String> favorites = new LinkedHashSet<String>();
    private final Deque<String> recent = new ArrayDeque<String>();

    private List<Route> routes() {
        return List.of(this.r("home", "Command home", "\u0627\u0644\u0631\u0626\u064a\u0633\u064a\u0629", 0, () -> ClientOdyssey.open(0)), this.r("army", "Army K", "\u0627\u0644\u062c\u064a\u0634 K", 0, ClientArmy::openOriginal), this.r("teams", "Five companies", "\u0627\u0644\u0641\u0631\u0642 \u0627\u0644\u062e\u0645\u0633", 0, () -> ClientRoyal.open(1)), this.r("skills", "Mastery & skills", "\u0627\u0644\u0625\u062a\u0642\u0627\u0646 \u0648\u0627\u0644\u0645\u0647\u0627\u0631\u0627\u062a", 0, () -> ClientRoyal.open(2)), this.r("commands", "Command presets", "\u0627\u0644\u0623\u0648\u0627\u0645\u0631 \u0627\u0644\u062c\u0627\u0647\u0632\u0629", 0, () -> ClientRoyal.open(4)), this.r("quests", "Quest book", "\u0643\u062a\u0627\u0628 \u0627\u0644\u0645\u0647\u0645\u0627\u062a", 1, () -> ClientOdyssey.open(1)), this.r("atlas", "Adventure atlas", "\u0623\u0637\u0644\u0633 \u0627\u0644\u0645\u063a\u0627\u0645\u0631\u0627\u062a", 1, () -> ClientOdyssey.open(2)), this.r("mythics", "Mythical creatures", "\u0627\u0644\u0645\u062e\u0644\u0648\u0642\u0627\u062a \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u064a\u0629", 1, ClientMythic::open), this.r("realm", "Dimension guide", "\u062f\u0644\u064a\u0644 \u0627\u0644\u062f\u0627\u064a\u0645\u0646\u0634\u0646", 1, ClientRealm::open), this.r("warfront", "Living warfront", "\u062c\u0628\u0647\u0629 \u0627\u0644\u062d\u0631\u0628", 1, () -> ClientWarfront.open(ClientEclipse.selected, 0)), this.r("camp", "Camp workshops", "\u0648\u0631\u0634 \u0627\u0644\u0645\u0639\u0633\u0643\u0631", 2, () -> ClientOdyssey.open(3)), this.r("crew", "Companion stories", "\u0642\u0635\u0635 \u0627\u0644\u0631\u0641\u0627\u0642", 2, () -> ClientOdyssey.open(4)), this.r("care", "Soul care", "\u0631\u0639\u0627\u064a\u0629 \u0627\u0644\u0623\u0631\u0648\u0627\u062d", 2, () -> ClientSoulLife.open(0)), this.r("cemetery", "Cemetery", "\u0627\u0644\u0645\u0642\u0628\u0631\u0629", 2, () -> ClientVigil.open(2)), this.r("collection", "Relic collection", "\u0645\u062c\u0645\u0648\u0639\u0629 \u0627\u0644\u0622\u062b\u0627\u0631", 2, () -> ClientOdyssey.open(5)), this.r("recipes", "Recipe library", "\u0645\u0643\u062a\u0628\u0629 \u0627\u0644\u0648\u0635\u0641\u0627\u062a", 3, () -> ClientOdyssey.open(10)), this.r("lessons", "Field manual", "\u062f\u0644\u064a\u0644 \u0627\u0644\u062a\u0639\u0644\u0645", 3, () -> ClientOdyssey.open(9)), this.r("supplies", "Supply management", "\u0625\u062f\u0627\u0631\u0629 \u0627\u0644\u0645\u0624\u0646", 3, () -> ClientRoyal.open(6)), this.r("updates", "Release archive", "\u0633\u062c\u0644 \u0627\u0644\u062a\u062d\u062f\u064a\u062b\u0627\u062a", 3, () -> ClientOdyssey.open(7)), this.r("options", "Mod options", "\u0625\u0639\u062f\u0627\u062f\u0627\u062a \u0627\u0644\u0645\u0648\u062f", 3, () -> ClientOdyssey.open(11)), this.r("legend_archive", "Legend archive", "\u0623\u0631\u0634\u064a\u0641 \u0627\u0644\u0623\u0633\u0627\u0637\u064a\u0631", 1, () -> {
            MythicNetwork.request(MythicNetwork.Action.VIEW);
            this.f_96541_.m_91152_((Screen)new GrimoireIndexScreen(this.previous));
        }), this.r("quest_planner", "Quest planning desk", "\u0645\u0643\u062a\u0628 \u062a\u062e\u0637\u064a\u0637 \u0627\u0644\u0645\u0647\u0645\u0627\u062a", 3, () -> this.f_96541_.m_91152_((Screen)new QuestPlannerScreen(this.previous))), this.r("workshop_manual", "Workshop manual", "\u062f\u0644\u064a\u0644 \u0627\u0644\u0648\u0631\u0634", 2, () -> this.f_96541_.m_91152_((Screen)new WorkshopManualScreen(this.previous))));
    }

    private Route r(String id, String en, String ar, int c, Runnable a) {
        return new Route(id, en, ar, c, a);
    }

    public InterfaceHub(Screen parent) {
        super((Component)Component.m_237113_((String)"Soulbound Navigator"));
        this.previous = parent;
        for (String s : ClientPreferences.get("ui.favorites", "").split(",")) {
            if (s.isBlank()) continue;
            this.favorites.add(s);
        }
        for (String s : ClientPreferences.get("ui.recent", "").split(",")) {
            if (s.isBlank() || this.recent.size() >= 8 || this.recent.contains(s)) continue;
            this.recent.add(s);
        }
    }

    private String t(String en, String ar) {
        return InterfaceStyle.text(en, ar);
    }

    private void rebuild() {
        this.query = this.search == null ? this.query : this.search.m_94155_();
        this.m_6575_(this.f_96541_, this.f_96543_, this.f_96544_);
    }

    private void button(int x, int y, int w, String label, Runnable a) {
        this.m_142416_((GuiEventListener)new StyledButton(x, y, w, 24, label, a));
    }

    private void visit(Route r) {
        this.recent.remove(r.id);
        this.recent.addFirst(r.id);
        while (this.recent.size() > 8) {
            this.recent.removeLast();
        }
        ClientPreferences.set("ui.recent", String.join((CharSequence)",", this.recent));
        r.action.run();
    }

    private List<Route> filtered() {
        String q = this.query.strip().toLowerCase(Locale.ROOT);
        List<Route> all = this.routes().stream().filter(r -> !(this.category != 0 && r.category != this.category - 1 || this.favoritesOnly && !this.favorites.contains(r.id) || this.recentOnly && !this.recent.contains(r.id) || !r.en.toLowerCase(Locale.ROOT).contains(q) && !r.ar.contains(q) && !r.id.contains(q))).toList();
        if (this.recentOnly) {
            return this.recent.stream().flatMap(id -> all.stream().filter(r -> r.id.equals(id))).toList();
        }
        return all;
    }

    protected void m_7856_() {
        int x = 18;
        int w = this.f_96543_ - 36;
        this.button(this.f_96543_ - 100, this.compact() ? 3 : 12, 82, this.t("Return", "\u0631\u062c\u0648\u0639"), this::m_7379_);
        this.search = (EditBox)this.m_142416_((GuiEventListener)new EditBox(this.f_96547_, x, this.compact() ? 3 : 44, this.compact() ? w - 90 : w, 24, (Component)ModLanguage.component(this.t("Search every destination", "\u0627\u0628\u062d\u062b \u0639\u0646 \u0623\u064a \u0648\u062c\u0647\u0629"))));
        this.search.m_257771_((Component)ModLanguage.component(this.t("Search destinations\u2026", "\u0627\u0628\u062d\u062b \u0639\u0646 \u0648\u062c\u0647\u0629\u2026")));
        this.search.m_94149_((value, index) -> FormattedCharSequence.m_13714_((String)ModLanguage.visual(this.f_96547_, ModLanguage.user(value)), (Style)ModLanguage.component(ModLanguage.user(value)).m_7383_()));
        this.search.m_94199_(64);
        this.search.m_94144_(this.query);
        this.search.m_94151_(v -> {
            this.query = v;
            this.offset = 0;
        });
        int cw = (w - 12) / 3;
        this.button(x, this.compact() ? 33 : 74, cw, this.t("Favorites", "\u0627\u0644\u0645\u0641\u0636\u0644\u0629") + (this.favoritesOnly ? " \u2713" : ""), () -> {
            this.favoritesOnly = !this.favoritesOnly;
            this.offset = 0;
            this.rebuild();
        });
        this.button(x + cw + 6, this.compact() ? 33 : 74, cw, this.t("Recent", "\u0627\u0644\u0623\u062e\u064a\u0631\u0629") + (this.recentOnly ? " \u2713" : ""), () -> {
            this.recentOnly = !this.recentOnly;
            this.offset = 0;
            this.rebuild();
        });
        this.button(x + 2 * (cw + 6), this.compact() ? 33 : 74, cw, this.t("Appearance", "\u0627\u0644\u0645\u0638\u0647\u0631"), () -> {
            this.settings = !this.settings;
            this.offset = 0;
            this.rebuild();
        });
        if (this.settings) {
            String[] en = new String[]{"High contrast", "Dim illustrations", "Plain backgrounds", "Simple frames", "Large touch rows", "Hide hints", "Show F8 badge", "Reduced motion"};
            String[] ar = new String[]{"\u062a\u0628\u0627\u064a\u0646 \u0639\u0627\u0644\u064d", "\u062a\u0639\u062a\u064a\u0645 \u0627\u0644\u0635\u0648\u0631", "\u062e\u0644\u0641\u064a\u0627\u062a \u0633\u0627\u062f\u0629", "\u0625\u0637\u0627\u0631\u0627\u062a \u0628\u0633\u064a\u0637\u0629", "\u0635\u0641\u0648\u0641 \u0644\u0645\u0633 \u0643\u0628\u064a\u0631\u0629", "\u0625\u062e\u0641\u0627\u0621 \u0627\u0644\u062a\u0644\u0645\u064a\u062d\u0627\u062a", "\u0625\u0638\u0647\u0627\u0631 \u0627\u062e\u062a\u0635\u0627\u0631 F8", "\u062d\u0631\u0643\u0629 \u0645\u062e\u0641\u0641\u0629"};
            String[] keys = new String[]{"contrast", "dim", "plain", "simple", "touch", "hints", "label", "motion"};
            int settingsTop = this.compact() ? 66 : 108;
            int count = Math.max(1, (this.f_96544_ - (this.compact() ? 62 : 76) - settingsTop) / 30);
            for (int i = this.offset = Math.min(this.offset, Math.max(0, keys.length - count)); i < Math.min(keys.length, this.offset + count); ++i) {
                int k = i;
                this.button(x, settingsTop + (i - this.offset) * 30, w, (ModLanguage.arabic() ? ar[i] : en[i]) + " : " + ((i == 7 ? MenuTheme.reducedMotion : InterfaceStyle.flag(keys[i])) ? this.t("ON", "\u0645\u0641\u0639\u0644") : this.t("OFF", "\u0645\u0639\u0637\u0644")), () -> {
                    if (k == 7) {
                        MenuTheme.toggleMotion();
                        ClientPreferences.set("ui.motion", Boolean.toString(MenuTheme.reducedMotion));
                    } else {
                        InterfaceStyle.toggle(keys[k]);
                    }
                    this.rebuild();
                });
            }
        }
        this.button(x, this.f_96544_ - (this.compact() ? 52 : 66), (w - 6) / 2, this.t("Previous page", "\u0627\u0644\u0635\u0641\u062d\u0629 \u0627\u0644\u0633\u0627\u0628\u0642\u0629"), () -> {
            this.offset = Math.max(0, this.offset - this.capacity());
            this.rebuild();
        });
        this.button(x + (w + 6) / 2, this.f_96544_ - (this.compact() ? 52 : 66), (w - 6) / 2, this.t("Next page", "\u0627\u0644\u0635\u0641\u062d\u0629 \u0627\u0644\u062a\u0627\u0644\u064a\u0629"), () -> {
            this.offset = Math.min(this.settings ? 7 : Math.max(0, this.filtered().size() - 1), this.offset + this.capacity());
            this.rebuild();
        });
        this.button(x, this.f_96544_ - (this.compact() ? 26 : 36), (w - 12) / 3, this.t("Language", "\u0627\u0644\u0644\u063a\u0629"), () -> {
            ModLanguage.toggle();
            this.rebuild();
        });
        this.button(x + (w - 12) / 3 + 6, this.f_96544_ - (this.compact() ? 26 : 36), (w - 12) / 3, this.t("Accent color", "\u0644\u0648\u0646 \u0627\u0644\u0648\u0627\u062c\u0647\u0629"), () -> {
            ClientPreferences.set("ui.palette", Integer.toString((InterfaceStyle.palette() + 1) % 3));
            this.rebuild();
        });
        this.button(x + 2 * ((w - 12) / 3 + 6), this.f_96544_ - (this.compact() ? 26 : 36), (w - 12) / 3, this.t("Clear search", "\u0645\u0633\u062d \u0627\u0644\u0628\u062d\u062b"), () -> {
            this.query = "";
            this.category = 0;
            this.recentOnly = false;
            this.favoritesOnly = false;
            this.offset = 0;
            this.rebuild();
        });
    }

    private boolean compact() {
        return this.f_96544_ < 330;
    }

    private int bodyTop() {
        return this.compact() ? 84 : 132;
    }

    private int categoryTop() {
        return this.compact() ? 61 : 104;
    }

    private int capacity() {
        return Math.max(1, (this.f_96544_ - (this.compact() ? 72 : 86) - this.bodyTop()) / (InterfaceStyle.flag("touch") ? 42 : 32));
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, 5);
        if (!this.compact()) {
            ModLanguage.draw(g, this.f_96547_, ModLanguage.fit(this.f_96547_, this.t("SOULBOUND \u2022 NAVIGATOR", "\u062c\u064a\u0634 \u0627\u0644\u0623\u0631\u0648\u0627\u062d \u2022 \u0627\u0644\u0645\u062a\u0635\u0641\u062d"), this.f_96543_ - 132), 18, 19, InterfaceStyle.accent(), false);
        }
        if (!this.settings) {
            String[] en = new String[]{"All", "Army", "Explore", "Camp", "Library"};
            String[] ar = new String[]{"\u0627\u0644\u0643\u0644", "\u0627\u0644\u062c\u064a\u0634", "\u0627\u0633\u062a\u0643\u0634\u0627\u0641", "\u0627\u0644\u0645\u0639\u0633\u0643\u0631", "\u0627\u0644\u0645\u0643\u062a\u0628\u0629"};
            int w = (this.f_96543_ - 40) / 5;
            for (int i = 0; i < 5; ++i) {
                int x = 18 + i * w;
                g.m_280509_(x, this.categoryTop(), x + w - 3, this.categoryTop() + 20, i == this.category ? InterfaceStyle.accent() : -13621698);
                ModLanguage.draw(g, this.f_96547_, ModLanguage.fit(this.f_96547_, ModLanguage.arabic() ? ar[i] : en[i], w - 8), x + 4, this.categoryTop() + 6, i == this.category ? -15725800 : -988710, false);
            }
            List<Route> list = this.filtered();
            this.offset = Math.max(0, Math.min(this.offset, Math.max(0, list.size() - 1)));
            int stride = InterfaceStyle.flag("touch") ? 42 : 32;
            for (int i = this.offset; i < Math.min(list.size(), this.offset + this.capacity()); ++i) {
                Route r = list.get(i);
                int y = this.bodyTop() + (i - this.offset) * stride;
                RoyalTheme.frame(g, 18, y, this.f_96543_ - 36, stride - 4, -283569119);
                String label = this.t(r.en, r.ar);
                FantasyIcons.draw(g, FantasyIcons.forText(label), 25, y + 4, 22);
                ModLanguage.draw(g, this.f_96547_, ModLanguage.fit(this.f_96547_, label, this.f_96543_ - 122), 54, y + 8, -988710, false);
                ModLanguage.draw(g, this.f_96547_, this.favorites.contains(r.id) ? "*" : "+", this.f_96543_ - 49, y + 8, InterfaceStyle.accent(), false);
                if (my < y || my >= y + stride - 4 || mx < 18 || mx >= this.f_96543_ - 18) continue;
                g.m_280509_(18, y, 20, y + stride - 4, InterfaceStyle.accent());
            }
            if (list.isEmpty()) {
                ModLanguage.centered(g, this.f_96547_, this.t("No results \u2022 clear filters below", "\u0644\u0627 \u0646\u062a\u0627\u0626\u062c \u2022 \u0627\u0645\u0633\u062d \u0627\u0644\u0641\u0644\u0627\u062a\u0631 \u0628\u0627\u0644\u0623\u0633\u0641\u0644"), this.f_96543_ / 2, this.bodyTop() + 13, -4937283);
            }
            ModLanguage.draw(g, this.f_96547_, this.t("Destinations: ", "\u0627\u0644\u0648\u062c\u0647\u0627\u062a: ") + list.size() + " \u2022 " + (list.isEmpty() ? 0 : this.offset + 1) + "\u2013" + Math.min(list.size(), this.offset + this.capacity()), 18, this.f_96544_ - (this.compact() ? 64 : 78), -4937283, false);
        }
        super.m_88315_(g, mx, my, partial);
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b == 0 && !this.settings) {
            int w = (this.f_96543_ - 40) / 5;
            if (y >= (double)this.categoryTop() && y < (double)(this.categoryTop() + 20) && x >= 18.0 && x < (double)(18 + 5 * w)) {
                this.category = Math.min(4, (int)(x - 18.0) / w);
                this.offset = 0;
                return true;
            }
            int stride = InterfaceStyle.flag("touch") ? 42 : 32;
            int index = this.offset + (int)(y - (double)this.bodyTop()) / stride;
            List<Route> l = this.filtered();
            if (x >= 18.0 && x < (double)(this.f_96543_ - 18) && y >= (double)this.bodyTop() && y < (double)(this.bodyTop() + this.capacity() * stride) && index < l.size()) {
                Route r = l.get(index);
                if (x > (double)(this.f_96543_ - 64)) {
                    if (!this.favorites.remove(r.id)) {
                        this.favorites.add(r.id);
                    }
                    ClientPreferences.set("ui.favorites", String.join((CharSequence)",", this.favorites));
                } else {
                    this.visit(r);
                }
                return true;
            }
        }
        return super.m_6375_(x, y, b);
    }

    public boolean m_6050_(double x, double y, double delta) {
        this.offset = Math.max(0, this.offset - (int)Math.signum(delta));
        if (this.settings) {
            this.rebuild();
        }
        return true;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (key == 297) {
            this.m_7379_();
            return true;
        }
        if (key == 70 && InterfaceHub.m_96637_()) {
            this.m_7522_((GuiEventListener)this.search);
            return true;
        }
        if (key == 257 && this.search.m_93696_() && !this.settings && !this.filtered().isEmpty()) {
            this.visit(this.filtered().get(Math.min(this.offset, this.filtered().size() - 1)));
            return true;
        }
        if (key == 268 && !this.search.m_93696_()) {
            this.offset = 0;
            if (this.settings) {
                this.rebuild();
            }
            return true;
        }
        if (key == 267 || key == 266) {
            this.offset = Math.max(0, this.offset + (key == 267 ? 1 : -1) * this.capacity());
            if (this.settings) {
                this.rebuild();
            }
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    public void m_7379_() {
        this.f_96541_.m_91152_(this.previous);
    }

    public boolean m_7043_() {
        return false;
    }

    private record Route(String id, String en, String ar, int category, Runnable action) {
    }
}

