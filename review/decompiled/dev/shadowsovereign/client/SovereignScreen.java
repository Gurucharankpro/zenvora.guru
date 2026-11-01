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
import dev.shadowsovereign.client.ClientSovereign;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.network.SovereignNetwork;
import dev.shadowsovereign.sovereign.SovereignState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SovereignScreen
extends Screen {
    private static final int W = 760;
    private static final int H = 430;
    private float scale;
    private float left;
    private float top;
    private UUID selected;
    private int ticks;
    private final List<Button> buttons = new ArrayList<Button>();

    public SovereignScreen(UUID id) {
        super((Component)ModLanguage.component("Sovereign Trails"));
        this.selected = id;
    }

    private SovereignState state() {
        return SovereignState.load(ClientSovereign.data);
    }

    private void send(SovereignNetwork.Action a, int value) {
        SovereignNetwork.request(a, value, this.selected);
    }

    protected void m_7856_() {
        this.scale = Math.min(1.5f, Math.min(((float)this.f_96543_ - 14.0f) / 760.0f, ((float)this.f_96544_ - 14.0f) / 430.0f));
        this.left = ((float)this.f_96543_ / this.scale - 760.0f) / 2.0f;
        this.top = ((float)this.f_96544_ / this.scale - 430.0f) / 2.0f;
        this.send(SovereignNetwork.Action.VIEW, 0);
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        if (++this.ticks % 20 == 0) {
            this.send(SovereignNetwork.Action.VIEW, 0);
        }
    }

    private void text(GuiGraphics g, String s, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, color, false);
    }

    private void wrap(GuiGraphics g, String s, int x, int y, int w, int color) {
        ModLanguage.wrap(g, this.f_96547_, (Component)Component.m_237113_((String)s), x, y, w, color);
    }

    private void add(GuiGraphics g, int x, int y, int w, String label, Runnable action) {
        this.buttons.add(new Button(x, y, w, label, action));
        g.m_280509_(x, y, x + w, y + 28, -13097913);
        FantasyIcons.label(g, this.f_96547_, label, x, y, w, 28, -988673);
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        int i;
        ShadowNetwork.Entry champion;
        this.m_280273_(g);
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.left, this.top, 0.0f);
        MenuTheme.panel(g, 760, 430, 11);
        this.buttons.clear();
        SovereignState s = this.state();
        this.text(g, "SOVEREIGN TRAILS", 20, 18, -1522047);
        this.text(g, "Three stages. One champion. A different path every cycle.", 20, 40, -2767129);
        this.add(g, 620, 16, 120, "Back to army", () -> this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected)));
        List<ShadowNetwork.Entry> roster = ClientArmy.view.entries();
        if (this.selected == null && !roster.isEmpty()) {
            this.selected = roster.get(0).id();
        }
        this.text(g, "Champion: " + ((champion = (ShadowNetwork.Entry)roster.stream().filter(r -> r.id().equals(this.selected)).findFirst().orElse(null)) == null ? "No companion selected" : ModLanguage.user(champion.name())), 20, 88, -988673);
        this.add(g, 610, 76, 130, "Next ally", () -> {
            if (!roster.isEmpty()) {
                int at = 0;
                for (int i = 0; i < roster.size(); ++i) {
                    if (!((ShadowNetwork.Entry)roster.get(i)).id().equals(this.selected)) continue;
                    at = i;
                }
                this.selected = ((ShadowNetwork.Entry)roster.get((at + 1) % roster.size())).id();
            }
        });
        if (s.active < 0) {
            for (i = 0; i < 3; ++i) {
                int slot = i;
                int x = 20 + i * 244;
                int offer = s.offer(i);
                g.m_280509_(x, 119, x + 232, 302, -585691102);
                MenuTheme.art(g, 3 + i, x, 119, 232, 46);
                this.text(g, ModLanguage.fit(this.f_96547_, SovereignState.NAMES[offer], 214), x + 9, 132, -1522047);
                for (int j = 0; j < 3; ++j) {
                    this.wrap(g, j + 1 + ". " + ClientSovereign.objective(SovereignState.ROUTES[offer][j]), x + 10, 178 + j * 28, 210, -2767129);
                }
                boolean arts = Arrays.asList(SovereignState.ROUTES[offer]).contains((Object)SovereignState.Goal.ART);
                if (arts && !ClientSovereign.data.m_128471_("ArtsUnlocked")) {
                    this.text(g, "Requires first Warfront victory", x + 8, 282, -4807483);
                    continue;
                }
                this.add(g, x + 10, 264, 212, "Accept trail", () -> this.send(SovereignNetwork.Action.ACCEPT, slot));
            }
        } else {
            g.m_280509_(20, 118, 740, 302, -585691102);
            this.text(g, SovereignState.NAMES[s.active], 32, 130, -1522047);
            this.text(g, "Bound champion: " + ModLanguage.user(ClientSovereign.data.m_128461_("ChampionName")), 32, 151, -2767129);
            for (int j = 0; j < 3; ++j) {
                String status = j < s.stage ? "COMPLETE" : (j == s.stage ? s.progress + " / " + s.target() : "UPCOMING");
                this.text(g, j + 1 + ". " + ClientSovereign.objective(SovereignState.ROUTES[s.active][j]) + " \u2022 " + status, 32, 177 + j * 24, j == s.stage ? -1522047 : -4807483);
            }
            this.add(g, 32, 264, 224, s.ready() ? "Claim trail completion" : "Abandon trail", () -> this.send(s.ready() ? SovereignNetwork.Action.CLAIM : SovereignNetwork.Action.ABANDON, 0));
            this.wrap(g, "Progress starts after acceptance and follows only the bound champion. Death and logout preserve progress.", 280, 264, 440, -4807483);
        }
        this.text(g, "Completed trails: " + s.completed + " \u2022 Crests are cosmetic and never increase damage.", 20, 320, -2767129);
        this.text(g, "Equipped crest: " + SovereignState.CRESTS[s.crest], 20, 341, -1522047);
        for (i = 0; i < SovereignState.CRESTS.length; ++i) {
            int crest = i;
            Object label = s.completed >= SovereignState.UNLOCKS[i] ? SovereignState.CRESTS[i] : "Locked: " + SovereignState.UNLOCKS[i];
            this.add(g, 20 + i * 104, 363, 98, (String)label, () -> this.send(SovereignNetwork.Action.CREST, crest));
        }
        this.text(g, "No daily lock or real-time deadline. Claiming offers three new paths.", 20, 409, -4807483);
        g.m_280168_().m_85849_();
    }

    public boolean m_6375_(double x, double y, int button) {
        if (button != 0) {
            return false;
        }
        double mx = x / (double)this.scale - (double)this.left;
        double my = y / (double)this.scale - (double)this.top;
        for (Button b : this.buttons) {
            if (!b.hit(mx, my)) continue;
            b.action.run();
            return true;
        }
        return false;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (ClientArmy.OPEN.m_90832_(key, scan)) {
            this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected));
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    public void m_7379_() {
        this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected));
    }

    private record Button(int x, int y, int w, String label, Runnable action) {
        boolean hit(double mx, double my) {
            return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + 28);
        }
    }
}

