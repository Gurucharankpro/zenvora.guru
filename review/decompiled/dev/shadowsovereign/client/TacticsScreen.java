/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientBattle;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.network.BattleNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class TacticsScreen
extends Screen {
    private static final int W = 600;
    private static final int H = 380;
    private static final int TEXT = -1251329;
    private static final int MUTED = -6515528;
    private static final int GOLD = -1653119;
    private final List<Control> controls = new ArrayList<Control>();
    private UUID selected;
    private BattleRules.Squad scope;
    private boolean individual = true;
    private float scale;
    private float left;
    private float top;
    private int ticks;

    public TacticsScreen(UUID selected) {
        super((Component)ModLanguage.component("Soulbound Army \u2014 Combat Tactics"));
        this.selected = selected;
        this.scope = ClientBattle.state.selected();
    }

    private ShadowNetwork.Entry chosen() {
        return ClientArmy.view.entries().stream().filter(e -> e.id().equals(this.selected)).findFirst().orElse(null);
    }

    private UUID target() {
        return this.individual ? this.selected : null;
    }

    private BattleNetwork.Entry settings() {
        if (this.individual) {
            return ClientBattle.entry(this.selected);
        }
        return ClientBattle.state.entries().stream().filter(e -> this.scope == BattleRules.Squad.ALL || e.squad() == this.scope).findFirst().orElse(null);
    }

    private void send(BattleNetwork.Action action, int value) {
        ClientBattle.send(action, this.scope, this.target(), value);
    }

    private void add(int x, int y, int w, Supplier<String> label, BooleanSupplier enabled, Runnable run) {
        this.controls.add(new Control(x, y, w, label, enabled, run));
    }

    protected void m_7856_() {
        this.scale = Math.min(1.5f, Math.min(((float)this.f_96543_ - 14.0f) / 600.0f, ((float)this.f_96544_ - 14.0f) / 380.0f));
        this.left = ((float)this.f_96543_ / this.scale - 600.0f) / 2.0f;
        this.top = ((float)this.f_96544_ / this.scale - 380.0f) / 2.0f;
        if (this.chosen() == null && !ClientArmy.view.entries().isEmpty()) {
            this.selected = ClientArmy.view.entries().get(0).id();
        }
        this.controls.clear();
        this.add(20, 82, 272, () -> "Scope: " + this.scope.label, () -> true, () -> {
            this.scope = BattleRules.Squad.values()[(this.scope.ordinal() + 1) % BattleRules.Squad.values().length];
            ClientBattle.send(BattleNetwork.Action.SELECT_SCOPE, this.scope, null, 0);
        });
        this.add(20, 113, 272, () -> this.chosen() == null ? "No companions" : ModLanguage.fit(this.f_96547_, "Companion: " + ModLanguage.user(this.chosen().name()), 250), () -> this.chosen() != null, () -> this.cycle(1));
        this.add(20, 144, 272, () -> "Apply to: " + (this.individual ? "selected companion" : "current scope"), () -> true, () -> {
            this.individual = !this.individual;
        });
        this.add(20, 175, 272, () -> "Assign selected: " + (ClientBattle.entry(this.selected) == null ? "Unassigned" : ClientBattle.entry((UUID)this.selected).squad().label), () -> this.chosen() != null, () -> {
            int next;
            BattleNetwork.Entry b = ClientBattle.entry(this.selected);
            int n = next = b == null ? 2 : b.squad().ordinal() + 1;
            if (next >= BattleRules.Squad.values().length) {
                next = 1;
            }
            ClientBattle.send(BattleNetwork.Action.ASSIGN, this.scope, this.selected, next);
        });
        this.add(20, 212, 272, () -> "Formation: " + (this.settings() == null ? "Loose" : this.settings().formation().label), () -> this.settings() != null, () -> this.send(BattleNetwork.Action.FORMATION, (this.settings().formation().ordinal() + 1) % BattleRules.Formation.values().length));
        this.add(20, 243, 272, () -> "Target priority: " + (this.settings() == null ? "Default" : this.settings().priority().label), () -> this.settings() != null, () -> this.send(BattleNetwork.Action.PRIORITY, (this.settings().priority().ordinal() + 1) % BattleRules.Priority.values().length));
        this.add(20, 274, 272, () -> "Auto retreat: " + (String)(this.settings() == null || this.settings().retreat() == 0 ? "OFF" : this.settings().retreat() + "% HP"), () -> this.settings() != null, () -> {
            int current = this.settings().retreat();
            int next = current == 0 ? 10 : (current == 10 ? 25 : (current == 25 ? 50 : 0));
            this.send(BattleNetwork.Action.RETREAT, next);
        });
        for (BattleRules.Skill skill : BattleRules.Skill.values()) {
            this.add(310, 82 + skill.ordinal() * 33, 270, () -> {
                int remaining = ClientBattle.seconds(skill);
                long eligible = ClientBattle.eligible(skill, this.scope, this.target());
                return skill.label + "  /  " + (remaining > 0 ? remaining + "s" : (eligible > 0L ? "READY (" + eligible + ")" : "Lv." + skill.level + "+ required"));
            }, () -> ClientBattle.seconds(skill) == 0 && ClientBattle.eligible(skill, this.scope, this.target()) > 0L, () -> this.send(BattleNetwork.Action.SKILL, skill.ordinal()));
        }
        this.add(310, 227, 270, () -> "Add aimed waypoint (" + (this.settings() == null ? 0 : this.settings().route().size()) + " / 8)", () -> this.settings() != null, () -> this.send(BattleNetwork.Action.PATROL_ADD, 0));
        this.add(310, 258, 132, () -> "Start patrol", () -> this.settings() != null && this.settings().route().size() >= 2, () -> this.send(BattleNetwork.Action.PATROL_START, 0));
        this.add(448, 258, 132, () -> "Clear route", () -> this.settings() != null && !this.settings().route().isEmpty(), () -> this.send(BattleNetwork.Action.PATROL_CLEAR, 0));
        this.add(310, 289, 132, () -> "Summon scope", () -> !ClientArmy.view.entries().isEmpty(), () -> ClientBattle.send(BattleNetwork.Action.SUMMON, this.scope, null, 0));
        this.add(448, 289, 132, () -> "Recall scope", () -> ClientBattle.active(this.scope) > 0L, () -> ClientBattle.send(BattleNetwork.Action.RECALL, this.scope, null, 0));
        this.add(20, 333, 132, () -> "Previous ally", () -> this.chosen() != null, () -> this.cycle(-1));
        this.add(158, 333, 134, () -> "Next ally", () -> this.chosen() != null, () -> this.cycle(1));
        this.add(448, 333, 132, () -> "Back to army", () -> true, this::m_7379_);
    }

    private void cycle(int delta) {
        List<ShadowNetwork.Entry> list = ClientArmy.view.entries();
        if (list.isEmpty()) {
            return;
        }
        int index = 0;
        for (int i = 0; i < list.size(); ++i) {
            if (!list.get(i).id().equals(this.selected)) continue;
            index = i;
        }
        this.selected = list.get(Math.floorMod(index + delta, list.size())).id();
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        if (++this.ticks % 20 == 0 && this.f_96541_.f_91074_ != null) {
            ShadowNetwork.request(ShadowNetwork.Action.REFRESH, null);
        }
    }

    public void m_88315_(GuiGraphics g, int mouseX, int mouseY, float partial) {
        this.m_280273_(g);
        double mx = (float)mouseX / this.scale - this.left;
        double my = (float)mouseY / this.scale - this.top;
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.left, this.top, 0.0f);
        MenuTheme.panel(g, 600, 380, 1);
        TacticsScreen.border(g, 0, 0, 600, 380, -10137471);
        g.m_280509_(0, 0, 600, 2, -5340417);
        this.text(g, "S O U L B O U N D   A R M Y  /  1.0.7", 20, 17, -6515528);
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_(20.0f, 36.0f, 0.0f);
        g.m_280168_().m_85841_(2.0f, 2.0f, 1.0f);
        this.text(g, "BATTLE TACTICS", 0, 0, -1251329);
        g.m_280168_().m_85849_();
        this.text(g, "Skills unlock at levels 2 / 4 / 6 / 8", 310, 45, -1653119);
        g.m_280509_(20, 69, 580, 70, -11979937);
        for (Control c : this.controls) {
            boolean enabled = c.enabled.getAsBoolean();
            boolean hover = c.hit(mx, my);
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 23, enabled ? (hover ? -11455131 : -13819843) : -15199202);
            TacticsScreen.border(g, c.x, c.y, c.w, 23, enabled && hover ? -4746753 : -10993564);
            FantasyIcons.label(g, this.f_96547_, c.label.get(), c.x, c.y, c.w, 23, enabled ? -1251329 : -8950906);
        }
        this.text(g, "Aim before opening: Step 32 blocks / waypoint 64 blocks", 20, 312, -6515528);
        if (this.settings() != null && this.settings().paused()) {
            this.text(g, "Patrol paused", 310, 316, -1653119);
        }
        this.text(g, "In-world: hold " + ClientBattle.WHEEL.m_90863_().getString() + " for combat wheel.  Skills affect nearby eligible allies.", 20, 365, -6515528);
        g.m_280168_().m_85849_();
    }

    private void text(GuiGraphics g, String s, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, color, false);
    }

    private static void border(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.m_280509_(x, y, x + w, y + 1, color);
        g.m_280509_(x, y + h - 1, x + w, y + h, color);
        g.m_280509_(x, y, x + 1, y + h, color);
        g.m_280509_(x + w - 1, y, x + w, y + h, color);
    }

    public boolean m_6375_(double x, double y, int button) {
        if (button != 0) {
            return false;
        }
        double mx = x / (double)this.scale - (double)this.left;
        double my = y / (double)this.scale - (double)this.top;
        for (Control c : this.controls) {
            if (!c.hit(mx, my) || !c.enabled.getAsBoolean()) continue;
            c.click.run();
            return true;
        }
        return false;
    }

    public boolean m_7933_(int key, int scan, int modifiers) {
        if (key == 263) {
            this.cycle(-1);
            return true;
        }
        if (key == 262) {
            this.cycle(1);
            return true;
        }
        return super.m_7933_(key, scan, modifiers);
    }

    public void m_7379_() {
        this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected));
    }

    private record Control(int x, int y, int w, Supplier<String> label, BooleanSupplier enabled, Runnable click) {
        boolean hit(double mx, double my) {
            return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + 23);
        }
    }
}

