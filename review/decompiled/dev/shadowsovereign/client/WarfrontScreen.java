/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.network.WarfrontNetwork;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public final class WarfrontScreen
extends Screen {
    private static final int W = 900;
    private static final int H = 520;
    private static final int TEXT = -988673;
    private static final int MUTED = -4675127;
    private static final int GOLD = -1522047;
    private static final int ACCENT = -4814081;
    private static final String[] TABS = new String[]{"Campaign Map", "Soul Arts", "Rune Workshop", "Battle Plans", "Rival Pacts"};
    private final List<Control> controls = new ArrayList<Control>();
    private float scale;
    private float left;
    private float top;
    private int page;
    private int node;
    private int plan;
    private int focus = -1;
    private int ticks;
    private boolean hard;
    private boolean nextArmed;
    private UUID selected;
    private final List<UUID> party = new ArrayList<UUID>();
    private final List<WarfrontState.Step> draft = new ArrayList<WarfrontState.Step>();
    private boolean loadedDraft;

    public WarfrontScreen(UUID id) {
        super((Component)ModLanguage.component("Soulbound Army \u2014 Living Warfront"));
        this.selected = id;
    }

    public void show(int page) {
        this.page = Math.max(0, Math.min(4, page));
        if (this.f_96541_ != null) {
            this.m_7856_();
        }
    }

    private CompoundTag d() {
        return ClientWarfront.data;
    }

    private CompoundTag run() {
        return this.d().m_128469_("Run");
    }

    private List<CompoundTag> roster() {
        return this.d().m_128437_("Roster", 10).stream().map((? super T t) -> (CompoundTag)t).toList();
    }

    private CompoundTag chosen() {
        return this.roster().stream().filter(e -> e.m_128403_("Id") && e.m_128342_("Id").equals(this.selected)).findFirst().orElse(new CompoundTag());
    }

    public void updated() {
        if (!this.roster().isEmpty() && this.chosen().m_128456_()) {
            this.selected = this.roster().get(0).m_128342_("Id");
        }
        ClientWarfront.selected = this.selected;
        if (!this.loadedDraft && this.d().m_128441_("Plans")) {
            this.loadPlan();
        }
        this.party.removeIf(id -> this.roster().stream().noneMatch(e -> e.m_128342_("Id").equals(id)));
    }

    private boolean has() {
        return this.selected != null && !this.chosen().m_128456_();
    }

    private boolean workshop() {
        return this.d().m_128471_("Workshop");
    }

    private boolean available() {
        return !this.d().m_128471_("Peaceful") && !this.d().m_128441_("Run") && !this.d().m_128471_("OtherActivity") && this.d().m_128471_("Near");
    }

    private boolean canArt() {
        return this.has() && this.workshop() && this.d().m_128471_("WonOnce") && this.chosen().m_128451_("Bond") >= 10;
    }

    private void send(WarfrontNetwork.Action a, int value, boolean flag) {
        if (this.f_96541_ != null && this.f_96541_.f_91074_ != null && this.f_96541_.m_91403_() != null) {
            WarfrontNetwork.request(new WarfrontNetwork.Request(a, value, flag, this.selected, List.copyOf(this.party), List.of()));
        }
    }

    private void add(int x, int y, int w, int h, Supplier<String> text, BooleanSupplier enabled, Runnable click) {
        this.controls.add(new Control(x, y, w, h, text, enabled, click));
    }

    private void add(int x, int y, int w, String text, BooleanSupplier enabled, Runnable click) {
        this.add(x, y, w, 23, () -> text, enabled, click);
    }

    private void choose(int offset) {
        List<CompoundTag> list = this.roster();
        if (list.isEmpty()) {
            return;
        }
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            if (!list.get(j).m_128342_("Id").equals(this.selected)) continue;
            i = j;
        }
        ClientWarfront.selected = this.selected = list.get(Math.floorMod(i + offset, list.size())).m_128342_("Id");
    }

    protected void m_7856_() {
        int x;
        int i;
        this.scale = Math.min(1.5f, Math.min(((float)this.f_96543_ - 12.0f) / 900.0f, ((float)this.f_96544_ - 12.0f) / 520.0f));
        this.left = ((float)this.f_96543_ / this.scale - 900.0f) / 2.0f;
        this.top = ((float)this.f_96544_ / this.scale - 520.0f) / 2.0f;
        this.controls.clear();
        this.updated();
        this.add(742, 18, 138, "Army", () -> true, () -> this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected)));
        for (i = 0; i < 5; ++i) {
            int tab = i;
            this.add(18, 95 + i * 48, 146, 38, () -> TABS[tab], () -> true, () -> {
                this.page = tab;
                this.m_7856_();
            });
        }
        this.add(184, 110, 28, "<", this::has, () -> this.choose(-1));
        this.add(218, 110, 28, ">", this::has, () -> this.choose(1));
        this.add(254, 110, 142, 23, () -> this.has() && this.party.contains(this.selected) ? "Remove from party" : "Add to party", () -> this.has() && this.chosen().m_128471_("Eligible") && (this.party.size() < 3 || this.party.contains(this.selected)), () -> {
            if (!this.party.remove(this.selected)) {
                this.party.add(this.selected);
            }
        });
        this.add(714, 110, 166, 23, () -> this.hard ? "Challenging" : "Relaxed", () -> !this.d().m_128441_("Run"), () -> {
            this.hard = !this.hard;
        });
        if (this.page == 0) {
            int[] xs = new int[]{190, 363, 363, 536, 536, 709};
            int[] ys = new int[]{246, 163, 329, 163, 329, 246};
            for (int i2 = 0; i2 < 6; ++i2) {
                int index = i2;
                this.add(xs[i2], ys[i2], 156, 68, () -> WarfrontService.TERRITORIES[index], () -> true, () -> {
                    this.node = index;
                });
            }
            this.add(742, 451, 138, 23, () -> this.nextArmed ? "Confirm next chapter" : "Next campaign", () -> this.d().m_128451_("Owned") == 63 && !this.d().m_128441_("Run") && this.d().m_128471_("Near"), () -> {
                if (!this.nextArmed && (this.d().m_128451_("Defeated") & ~this.d().m_128451_("PactClaimed")) != 0) {
                    this.nextArmed = true;
                } else {
                    this.send(WarfrontNetwork.Action.NEXT, 0, this.nextArmed);
                    this.nextArmed = false;
                }
            });
            this.add(742, 483, 138, "Start selected", () -> this.available() && this.d().m_128437_("Nodes", 10).m_128728_(this.node).m_128471_("Available"), () -> this.send(WarfrontNetwork.Action.START, this.node, this.hard));
        }
        if (this.page == 1) {
            for (i = 1; i < 4; ++i) {
                int art = i;
                this.add(190 + (i - 1) * 232, 350, 218, 23, () -> this.chosen().m_128461_("Art").equals(WarfrontState.Art.values()[art].name()) ? "Equipped" : "Equip " + WarfrontState.label(WarfrontState.Art.values()[art]), this::canArt, () -> this.send(WarfrontNetwork.Action.SET_ART, art, false));
            }
            this.add(714, 144, 166, "Unequip Art", () -> this.has() && this.workshop(), () -> this.send(WarfrontNetwork.Action.SET_ART, 0, false));
            this.add(190, 390, 218, 23, () -> this.seconds() > 0L ? this.seconds() + "s cooldown" : "Cast equipped Art", () -> this.has() && this.chosen().m_128471_("Eligible") && this.chosen().m_128451_("Bond") >= 10 && this.d().m_128471_("WonOnce") && !this.chosen().m_128461_("Art").equals("NONE") && this.seconds() == 0L, () -> {
                this.send(WarfrontNetwork.Action.CAST, 0, false);
                this.f_96541_.m_91152_(null);
            });
        }
        if (this.page == 2) {
            for (i = 1; i < 7; ++i) {
                int rune = i;
                x = 190 + (i - 1) % 3 * 232;
                int y = 168 + (i - 1) / 3 * 128;
                this.add(x + 8, y + 91, 202, 23, () -> (this.chosen().m_128451_("Crafted") & 1 << rune) != 0 ? "Equip " + WarfrontState.label(WarfrontState.Rune.values()[rune]) : "Craft & equip", () -> this.has() && this.workshop() && (this.d().m_128451_("Blueprints") & 1 << rune) != 0, () -> this.send(WarfrontNetwork.Action.SET_RUNE, rune, false));
            }
            this.add(714, 144, 166, "Remove rune", () -> this.has() && this.workshop(), () -> this.send(WarfrontNetwork.Action.SET_RUNE, 0, false));
        }
        if (this.page == 3) {
            for (i = 0; i < 3; ++i) {
                int slot = i;
                this.add(190 + i * 232, 155, 218, "Plan " + (i + 1), () -> true, () -> {
                    this.plan = slot;
                    this.loadPlan();
                });
            }
            for (i = 0; i < 4; ++i) {
                int step = i;
                this.add(190 + i * 174, 225, 160, 70, () -> step < this.draft.size() ? WarfrontState.label(this.draft.get(step)) : "+ Add step", () -> step <= this.draft.size(), () -> {
                    if (step == this.draft.size()) {
                        this.draft.add(WarfrontState.Step.RALLY);
                    } else {
                        this.draft.set(step, WarfrontState.Step.values()[(this.draft.get(step).ordinal() + 1) % WarfrontState.Step.values().length]);
                    }
                });
            }
            this.add(190, 331, 150, "Remove last step", () -> !this.draft.isEmpty(), () -> this.draft.remove(this.draft.size() - 1));
            this.add(352, 331, 150, "Save plan", () -> true, () -> {
                if (this.f_96541_.f_91074_ != null) {
                    WarfrontNetwork.request(new WarfrontNetwork.Request(WarfrontNetwork.Action.PLAN_SAVE, this.plan, false, null, List.of(), List.copyOf(this.draft)));
                }
            });
            this.add(514, 331, 150, "Run saved plan", () -> this.d().m_128437_("Plans", 10).m_128728_(this.plan).m_128437_("Steps", 8).size() > 0, () -> {
                this.send(WarfrontNetwork.Action.PLAN_RUN, this.plan, false);
                this.f_96541_.m_91152_(null);
            });
            this.add(676, 331, 202, "Stop & restore orders", () -> this.d().m_128451_("Running") >= 0, () -> this.send(WarfrontNetwork.Action.PLAN_STOP, 0, false));
            this.add(190, 376, 260, "Place rally marker: aim + G", () -> true, () -> {
                this.send(WarfrontNetwork.Action.MARKER, 0, false);
                this.f_96541_.m_91152_(null);
            });
        }
        if (this.page == 4) {
            for (i = 0; i < 3; ++i) {
                int rival = i;
                x = 190 + i * 232;
                this.add(x + 8, 321, 202, "Start pact rematch", () -> this.available() && (this.d().m_128451_("Defeated") & 1 << rival) != 0 && (this.d().m_128451_("PactReady") & 1 << rival) == 0 && (this.d().m_128451_("PactClaimed") & 1 << rival) == 0, () -> this.send(WarfrontNetwork.Action.PACT_START, rival, this.hard));
                this.add(x + 8, 354, 202, "Recruit commander", () -> this.workshop() && (this.d().m_128451_("PactReady") & 1 << rival) != 0 && (this.d().m_128451_("PactClaimed") & 1 << rival) == 0, () -> this.send(WarfrontNetwork.Action.PACT_CLAIM, rival, false));
            }
        }
        this.add(185, 483, 100, "Pause", () -> this.d().m_128441_("Run") && this.run().m_128451_("Phase") == 0, () -> this.send(WarfrontNetwork.Action.PAUSE, 0, false));
        this.add(292, 483, 100, "Resume", () -> this.d().m_128441_("Run") && this.run().m_128451_("Phase") == 1 && this.d().m_128471_("Near"), () -> this.send(WarfrontNetwork.Action.RESUME, 0, false));
        this.add(399, 483, 100, "Abandon", () -> this.d().m_128441_("Run") && this.run().m_128451_("Phase") != 2, () -> this.send(WarfrontNetwork.Action.ABANDON, 0, false));
        this.add(506, 483, 146, "Dismiss victory", () -> this.d().m_128441_("Run") && this.run().m_128451_("Phase") == 2, () -> this.send(WarfrontNetwork.Action.CLAIM, 0, false));
    }

    private void loadPlan() {
        this.draft.clear();
        for (Tag e : this.d().m_128437_("Plans", 10).m_128728_(this.plan).m_128437_("Steps", 8)) {
            this.draft.add(WarfrontState.read(WarfrontState.Step.class, e.m_7916_(), WarfrontState.Step.REGROUP));
        }
        this.loadedDraft = true;
    }

    private long seconds() {
        return Math.max(0L, (this.chosen().m_128454_("ArtAt") - ClientWarfront.now() + 19L) / 20L);
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        if (++this.ticks % 20 == 0) {
            this.send(WarfrontNetwork.Action.VIEW, 0, false);
        }
    }

    private void text(GuiGraphics g, String s, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, color, false);
    }

    private void wrap(GuiGraphics g, String s, int x, int y, int width, int color) {
        for (FormattedCharSequence line : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(s), width)) {
            ModLanguage.draw(g, this.f_96547_, line, x, y, color, false);
            y += 12;
        }
    }

    private void card(GuiGraphics g, int x, int y, int width, int height, int tile, boolean active) {
        g.m_280509_(x, y, x + width, y + height, -585691102);
        MenuTheme.art(g, tile, x, y, width, Math.min(76, height));
        g.m_280509_(x, y, x + width, y + Math.min(76, height), -1156379870);
        int color = active ? -1522047 : -10993554;
        g.m_280509_(x, y, x + width, y + 2, color);
        g.m_280509_(x, y + height - 1, x + width, y + height, color);
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        double x = (float)mx / this.scale - this.left;
        double y = (float)my / this.scale - this.top;
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.left, this.top, 0.0f);
        MenuTheme.panel(g, 900, 520, (new int[]{0, 6, 2, 1, 11})[this.page]);
        this.text(g, "L I V I N G   W A R F R O N T", 20, 18, -1522047);
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_(184.0f, 37.0f, 0.0f);
        g.m_280168_().m_85841_(1.8f, 1.8f, 1.0f);
        this.text(g, TABS[this.page], 0, 0, -988673);
        g.m_280168_().m_85849_();
        this.text(g, "Chapter " + Math.max(1L, this.d().m_128454_("Chapter")) + "  /  " + Integer.bitCount(this.d().m_128451_("Owned")) + " of 6 territories  /  " + this.d().m_128454_("Trophies") + " trophies", 184, 70, -4675127);
        this.text(g, this.has() ? ModLanguage.fit(this.f_96547_, ModLanguage.user(this.chosen().m_128461_("Name")) + " \u2022 " + this.chosen().m_128461_("Tier") + " \u2022 Bond " + this.chosen().m_128451_("Bond"), 430) : "Summon a companion to start your campaign.", 184, 92, -988673);
        if (this.has()) {
            MenuTheme.art(g, 0, 624, 42, 82, 62);
            g.m_280509_(624, 42, 706, 104, -2012346343);
            g.m_280588_((int)((this.left + 624.0f) * this.scale), (int)((this.top + 42.0f) * this.scale), (int)((this.left + 706.0f) * this.scale), (int)((this.top + 104.0f) * this.scale));
            MenuTheme.portrait(g, this.chosen().m_128461_("Type"), this.chosen().m_128403_("Entity") ? this.chosen().m_128342_("Entity") : null, 665, 102, 22);
            g.m_280618_();
        }
        this.text(g, (String)(this.party.isEmpty() ? "Party: first 3 eligible allies" : "Party: " + this.party.size() + " / 3 selected"), 410, 117, -1522047);
        this.wrap(g, this.d().m_128471_("Bound") ? "BEACON BOUND\nMissions use loaded terrain. No blocks are changed." : "CRAFT A BEACON\n4 obsidian corners, 4 amethyst sides, Echo Compass center.", 24, 354, 132, -4675127);
        this.wrap(g, "G: objectives / armed marker\nH: Army\nAll timers use saved game time.", 24, 434, 132, -4675127);
        if (this.page == 0) {
            this.map(g);
        }
        if (this.page == 1) {
            this.arts(g);
        }
        if (this.page == 2) {
            this.runes(g);
        }
        if (this.page == 3) {
            this.plans(g);
        }
        if (this.page == 4) {
            this.pacts(g);
        }
        if (this.d().m_128441_("Run")) {
            this.card(g, 184, 426, 548, 47, 4, false);
            String phase = (new String[]{"ACTIVE", "PAUSED", "VICTORY"})[Math.max(0, Math.min(2, this.run().m_128451_("Phase")))];
            this.text(g, phase + " \u2022 " + WarfrontService.MISSIONS[Math.max(0, Math.min(3, this.run().m_128451_("Mission")))] + " \u2022 " + this.run().m_128454_("Remaining") / 20L + "s \u2022 " + this.run().m_128451_("Enemies") + " enemies", 194, 434, -1522047);
            this.text(g, (String)(this.run().m_128451_("Mission") == 1 ? "Wards disabled: " + Integer.bitCount(this.run().m_128451_("Disabled")) + " / 3 \u2014 approach with an ally and press G" : (this.run().m_128451_("Mission") == 2 ? (this.run().m_128471_("Released") ? "Bring the freed Wayfarer within six blocks of your Beacon." : "Approach the Wayfarer, press G, and stay near it for six seconds.") : "Defeat the marked enemy. Dodge violet warning circles.")), 194, 453, -4675127);
        } else {
            this.wrap(g, this.d().m_128471_("OtherActivity") ? "Another adventure occupies the encounter slot. Finish or pause and abandon it first." : (this.page == 0 ? (this.nextArmed ? "Starting the next chapter leaves all unclaimed pacts behind. Click Confirm next chapter to accept." : "Choose a frontier. First victories unlock blueprints; commander victories reveal pact rematches.") : "Arts, crafted runes and plans persist independently of your campaign."), 190, 438, 530, -4675127);
        }
        for (int i = 0; i < this.controls.size(); ++i) {
            boolean hover;
            Control c = this.controls.get(i);
            boolean enabled = c.enabled.getAsBoolean();
            boolean bl = hover = c.contains(x, y) || this.focus == i;
            if (this.page == 0 && i >= 0 && c.h == 68) continue;
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + c.h, !enabled ? -585756131 : (hover ? -10010492 : -298835905));
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 1, hover ? -4814081 : -10731148);
            FantasyIcons.label(g, this.f_96547_, c.text.get(), c.x, c.y, c.w, c.h, enabled ? -988673 : -9083006);
        }
        g.m_280168_().m_85849_();
    }

    private void map(GuiGraphics g) {
        int[][] links;
        int[] xs = new int[]{190, 363, 363, 536, 536, 709};
        int[] ys = new int[]{246, 163, 329, 163, 329, 246};
        for (int[] link : links = new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 5}}) {
            int x1 = xs[link[0]] + 156;
            int y1 = ys[link[0]] + 34;
            int x2 = xs[link[1]];
            int y2 = ys[link[1]] + 34;
            for (int t = 0; t < 32; ++t) {
                int xx = x1 + (x2 - x1) * t / 32;
                int yy = y1 + (y2 - y1) * t / 32;
                g.m_280509_(xx, yy, xx + 2, yy + 2, -7969385);
            }
        }
        for (int i = 0; i < 6; ++i) {
            boolean held = (this.d().m_128451_("Owned") & 1 << i) != 0;
            boolean available = this.d().m_128437_("Nodes", 10).m_128728_(i).m_128471_("Available");
            this.card(g, xs[i], ys[i], 156, 68, 4 + i % 5, i == this.node);
            this.text(g, ModLanguage.fit(this.f_96547_, WarfrontService.TERRITORIES[i], 143), xs[i] + 7, ys[i] + 9, -988673);
            this.text(g, this.d().m_128451_("Contested") == i ? "CONTESTED" : (held ? "CONTROLLED" : (available ? "AVAILABLE" : "LOCKED")), xs[i] + 7, ys[i] + 26, held ? -5907285 : (available ? -1522047 : -4675127));
            this.text(g, ModLanguage.fit(this.f_96547_, WarfrontService.MISSIONS[this.d().m_128437_("Nodes", 10).m_128728_(i).m_128451_("Mission")], 143), xs[i] + 7, ys[i] + 46, -4675127);
        }
        CompoundTag rival = this.d().m_128437_("Rivals", 10).m_128728_(WarfrontState.rival(this.node));
        this.text(g, ModLanguage.fit(this.f_96547_, ModLanguage.user(rival.m_128461_("Name")) + " \u2022 Influence " + rival.m_128451_("Influence") + "/3 \u2022 " + rival.m_128461_("Adaptation"), 690), 190, 398, -1522047);
        this.text(g, switch (rival.m_128461_("Adaptation")) {
            case "WINDGUARD" -> "Projectile wards: 3s every 9s. Wait for a gap or switch to melee.";
            case "SCATTER" -> "Cluster counter: violet circles warn two seconds before impact. Spread out.";
            case "REPRISAL" -> "Melee guard: 3s every 9s. Use ranged attacks or wait for a gap.";
            default -> "No adaptation recorded. Your battle choices will shape the rival's next counter.";
        }, 190, 414, -4675127);
    }

    private void arts(GuiGraphics g) {
        String[] effects = new String[]{"An eight-second, one-hit phantom distracts eligible nearby enemies. Two illusions maximum.", "After four seconds, restore up to 8 HP lost by this same living companion. Cannot revive.", "Your companion's next hit in eight seconds reduces target armor by 4 for six seconds. No stacking."};
        for (int i = 0; i < 3; ++i) {
            int x = 190 + i * 232;
            this.card(g, x, 176, 218, 205, 6 + i, this.chosen().m_128461_("Art").equals(WarfrontState.Art.values()[i + 1].name()));
            this.text(g, WarfrontState.label(WarfrontState.Art.values()[i + 1]), x + 12, 190, -1522047);
            this.wrap(g, effects[i], x + 12, 225, 194, -988673);
            this.wrap(g, "45s shared cooldown. Equip near a Beacon or Sanctuary; bond 10 and first Warfront victory required.", x + 12, 285, 194, -4675127);
        }
    }

    private void runes(GuiGraphics g) {
        String[] effects = new String[]{"+10% movement outside combat.", "+1 regeneration HP below half health.", "Reduce one incoming hit by 1 HP every 15s.", "Privately mark an attacker for eight seconds.", "Follow catch-up threshold: 32 \u2192 24 blocks.", "Feeding grants 4 bond instead of 3."};
        for (int i = 1; i < 7; ++i) {
            int x = 190 + (i - 1) % 3 * 232;
            int y = 168 + (i - 1) / 3 * 128;
            this.card(g, x, y, 218, 120, 2 + i % 3, this.chosen().m_128461_("Rune").equals(WarfrontState.Rune.values()[i].name()));
            this.text(g, WarfrontState.label(WarfrontState.Rune.values()[i]), x + 10, y + 9, -1522047);
            this.wrap(g, effects[i - 1], x + 10, y + 29, 198, -988673);
            this.text(g, (this.d().m_128451_("Blueprints") & 1 << i) == 0 ? "Blueprint: conquer more territory" : ((this.chosen().m_128451_("Crafted") & 1 << i) != 0 ? "Crafted \u2022 free switching" : "Cost: 8 amethyst + 1 copper"), x + 10, y + 72, -4675127);
        }
    }

    private void plans(GuiGraphics g) {
        this.text(g, "Editing Plan " + (this.plan + 1) + " \u2022 click a step to cycle its action", 190, 195, -1522047);
        this.text(g, "Execution scope: " + this.d().m_128461_("Scope") + " \u2022 change squad in Tactics", 190, 309, -4675127);
        this.text(g, (String)(this.d().m_128441_("Marker") ? "Marker: " + BlockPos.m_122022_((long)this.d().m_128454_("Marker")).m_123344_() : "No rally marker. Place one before Rally or Guard."), 466, 384, -4675127);
        this.wrap(g, "Rally / Guard: marker (5s). Regroup: owner (5s). Focus: aimed enemy (10s).\nManual orders stop the plan and restore previous orders.", 190, 408, 685, -4675127);
    }

    private void pacts(GuiGraphics g) {
        for (int i = 0; i < 3; ++i) {
            int x = 190 + i * 232;
            CompoundTag rival = this.d().m_128437_("Rivals", 10).m_128728_(i);
            this.card(g, x, 162, 218, 243, 9 + i, (this.d().m_128451_("PactReady") & 1 << i) != 0);
            this.text(g, ModLanguage.fit(this.f_96547_, ModLanguage.user(rival.m_128461_("Name")), 196), x + 10, 178, -1522047);
            this.text(g, (this.d().m_128451_("PactClaimed") & 1 << i) != 0 ? "PACTBOUND ALLY" : ((this.d().m_128451_("PactReady") & 1 << i) != 0 ? "RECRUITMENT EARNED" : ((this.d().m_128451_("Defeated") & 1 << i) != 0 ? "REMATCH UNLOCKED" : "DEFEAT ITS STRONGHOLD")), x + 10, 202, -4814081);
            this.wrap(g, "Win the rematch without any participating companion dying. Guaranteed recruitment with normal tier and trait rolls; Chosen limits remain.", x + 10, 229, 196, -988673);
        }
    }

    public boolean m_6375_(double mx, double my, int button) {
        if (button != 0) {
            return false;
        }
        double x = mx / (double)this.scale - (double)this.left;
        double y = my / (double)this.scale - (double)this.top;
        for (Control c : this.controls) {
            if (!c.contains(x, y) || !c.enabled.getAsBoolean()) continue;
            c.click.run();
            return true;
        }
        return false;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (ClientArmy.OPEN.m_90832_(key, scan)) {
            this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected));
            return true;
        }
        if (key == 258) {
            for (int i = 0; i < this.controls.size(); ++i) {
                this.focus = Math.floorMod(this.focus + (WarfrontScreen.m_96638_() ? -1 : 1), this.controls.size());
                if (this.controls.get((int)this.focus).enabled.getAsBoolean()) break;
            }
            return true;
        }
        if (key == 257 && this.focus >= 0) {
            Control c = this.controls.get(this.focus);
            if (c.enabled.getAsBoolean()) {
                c.click.run();
            }
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    private record Control(int x, int y, int w, int h, Supplier<String> text, BooleanSupplier enabled, Runnable click) {
        boolean contains(double x, double y) {
            return x >= (double)this.x && x < (double)(this.x + this.w) && y >= (double)this.y && y < (double)(this.y + this.h);
        }
    }
}

