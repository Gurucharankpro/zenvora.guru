/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.ClientSoulLife;
import dev.shadowsovereign.client.ClientVigil;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.client.VigilAlbumScreen;
import dev.shadowsovereign.network.VigilNetwork;
import dev.shadowsovereign.vigil.VigilRuns;
import dev.shadowsovereign.vigil.VigilText;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public final class VigilScreen
extends Screen {
    private static final String[] PAGES = new String[]{"home", "crown", "cemetery", "posts", "logistics", "lens", "powers", "games", "board", "map", "settings", "guide"};
    private final List<Control> controls = new ArrayList<Control>();
    private int page;
    private int scroll;
    private int maxScroll;
    private int cursor;
    private int bottom;
    private int focus = -1;
    private UUID selected;
    private UUID buddy;
    private boolean hard;
    private boolean thumb;
    private boolean swipe;
    private boolean body;
    private double downY;
    private double grab;
    private int oldScroll;
    private Control pending;

    public VigilScreen(int page) {
        super((Component)VigilText.c("title", new Object[0]));
        this.page = Math.max(0, Math.min(11, page));
        this.selected = ClientEclipse.selected;
    }

    public int currentPage() {
        return this.page;
    }

    public int scrollOffset() {
        return this.scroll;
    }

    private String t(String key, Object ... args) {
        return ClientVigil.t(key, args);
    }

    private CompoundTag d() {
        return ClientVigil.data;
    }

    private CompoundTag run() {
        return this.d().m_128469_("Run");
    }

    private List<CompoundTag> roster() {
        return this.d().m_128437_("Roster", 10).stream().map((? super T t) -> (CompoundTag)t).toList();
    }

    private CompoundTag chosen() {
        return this.roster().stream().filter(n -> n.m_128342_("Id").equals(this.selected)).findFirst().orElse(new CompoundTag());
    }

    private void show(int p) {
        this.page = Math.floorMod(p, 12);
        this.scroll = 0;
    }

    protected void m_7856_() {
        List<CompoundTag> list = this.roster();
        if (!list.isEmpty() && (this.selected == null || list.stream().noneMatch(n -> n.m_128342_("Id").equals(this.selected)))) {
            this.selected = list.get(0).m_128342_("Id");
        }
        if (this.buddy == null && list.size() > 1) {
            this.buddy = list.get(1).m_128342_("Id");
        }
    }

    private void send(VigilNetwork.Action a, int x, int y, int nonce, UUID one, UUID two) {
        VigilNetwork.request(a, x, y, nonce, one, two, List.of());
    }

    private void action(VigilNetwork.Action a) {
        this.send(a, 0, 0, 0, this.selected, this.buddy);
    }

    private void text(GuiGraphics g, String s, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, color, false);
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String label, boolean enabled, Runnable action) {
        if (this.body && (y < 68 || y + h > this.bottom)) {
            return;
        }
        RoyalTheme.frame(g, x, y, w, h, enabled ? -265870797 : -535423715);
        FantasyIcons.label(g, this.f_96547_, label, x, y, w, h, enabled ? -988710 : -8358268);
        this.controls.add(new Control(x, y, w, h, label, enabled, action));
    }

    private void row(GuiGraphics g, String label, String desc, boolean enabled, Runnable action) {
        List lines = desc.isBlank() ? List.of() : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(desc), this.f_96543_ - 66);
        int h = lines.isEmpty() ? 27 : Math.max(50, 29 + lines.size() * 13);
        int y = this.cursor;
        this.cursor += h + 7;
        if (y + h < 68 || y >= this.bottom) {
            return;
        }
        RoyalTheme.frame(g, 16, y, this.f_96543_ - 42, h, -451143386);
        this.text(g, ModLanguage.fit(this.f_96547_, label, this.f_96543_ - 62), 24, y + 8, enabled ? -1981295 : -4937283);
        int at = y + 26;
        for (FormattedCharSequence l : lines) {
            ModLanguage.draw(g, this.f_96547_, l, 24, at, -988710, false);
            at += 13;
        }
        if (y >= 68 && y + 25 <= this.bottom && action != null) {
            this.controls.add(new Control(16, y, this.f_96543_ - 42, y + h <= this.bottom ? h : 25, label, enabled, action));
        }
    }

    private void picker(GuiGraphics g, boolean partner) {
        List<CompoundTag> list = this.roster();
        UUID id = partner ? this.buddy : this.selected;
        CompoundTag n = list.stream().filter(x -> x.m_128342_("Id").equals(id)).findFirst().orElse(new CompoundTag());
        this.row(g, this.t(partner ? "partner" : "selected", ModLanguage.user(n.m_128461_("Name"))), "", !list.isEmpty(), () -> {
            int at = 0;
            for (int i = 0; i < list.size(); ++i) {
                if (!((CompoundTag)list.get(i)).m_128342_("Id").equals(id)) continue;
                at = i + 1;
            }
            UUID next = ((CompoundTag)list.get(at % list.size())).m_128342_("Id");
            if (partner) {
                this.buddy = next;
            } else {
                this.selected = next;
                ClientEclipse.select(next);
            }
        });
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        this.controls.clear();
        this.bottom = this.f_96544_ - 27;
        if (this.selected == null) {
            this.m_7856_();
        }
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, this.page % 8);
        this.text(g, ModLanguage.fit(this.f_96547_, this.t("title", new Object[0]), this.f_96543_ - 175), 17, 17, -1981295);
        this.button(g, this.f_96543_ - 155, 10, 116, 24, "English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", true, ModLanguage::toggle);
        this.button(g, this.f_96543_ - 34, 10, 23, 24, "\u00d7", true, () -> ((VigilScreen)this).m_7379_());
        this.button(g, 16, 38, 26, 24, "<", true, () -> this.show(this.page - 1));
        this.button(g, 48, 38, this.f_96543_ - 202, 24, this.t(PAGES[this.page], new Object[0]), true, () -> this.show(0));
        this.button(g, this.f_96543_ - 148, 38, 80, 24, this.t("army_short", new Object[0]), true, ClientArmy::openOriginal);
        this.button(g, this.f_96543_ - 62, 38, 24, 24, ">", true, () -> this.show(this.page + 1));
        this.button(g, this.f_96543_ - 34, 38, 24, 24, "\u2302", true, () -> this.show(0));
        g.m_280588_(12, 68, this.f_96543_ - 22, this.bottom);
        this.body = true;
        this.cursor = 72 - this.scroll;
        switch (this.page) {
            case 0: {
                this.home(g);
                break;
            }
            case 1: {
                this.crown(g);
                break;
            }
            case 2: {
                this.cemetery(g);
                break;
            }
            case 3: {
                this.posts(g);
                break;
            }
            case 4: {
                this.logistics(g);
                break;
            }
            case 5: {
                this.lens(g);
                break;
            }
            case 6: {
                this.powers(g);
                break;
            }
            case 7: {
                this.games(g);
                break;
            }
            case 8: {
                this.board(g);
                break;
            }
            case 9: {
                this.map(g);
                break;
            }
            case 10: {
                this.settings(g);
                break;
            }
            case 11: {
                this.guide(g);
            }
        }
        this.body = false;
        g.m_280618_();
        this.maxScroll = Math.max(0, this.cursor + this.scroll - (this.bottom - 4));
        this.scroll = Math.max(0, Math.min(this.scroll, this.maxScroll));
        if (this.maxScroll > 0) {
            int track = this.bottom - 68;
            int h = Math.max(24, track * track / (track + this.maxScroll));
            int y = 68 + (track - h) * this.scroll / this.maxScroll;
            g.m_280509_(this.f_96543_ - 18, 68, this.f_96543_ - 7, this.bottom, -14344146);
            g.m_280509_(this.f_96543_ - 17, y, this.f_96543_ - 8, y + h, -2969736);
        }
        this.text(g, ModLanguage.fit(this.f_96547_, this.t("footer", this.d().m_128451_("Offerings"), this.d().m_128451_("Diamonds")), this.f_96543_ - 32), 16, this.f_96544_ - 18, -4937283);
        if (this.focus >= 0 && this.focus < this.controls.size()) {
            Control c = this.controls.get(this.focus);
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 2, -1981295);
        }
        super.m_88315_(g, mx, my, partial);
    }

    private void home(GuiGraphics g) {
        this.row(g, ClientSoulLife.t("title", new Object[0]), "", true, () -> ClientSoulLife.open(0));
        int y = this.cursor;
        this.cursor += 66;
        RoyalTheme.art(g, 1, 16, y, this.f_96543_ - 42, 58);
        this.text(g, ModLanguage.fit(this.f_96547_, this.t("welcome", new Object[0]), this.f_96543_ - 62), 24, y + 38, -1981295);
        for (int i = 1; i < 12; ++i) {
            int target = i;
            this.row(g, this.t(PAGES[i], new Object[0]), "", true, () -> this.show(target));
        }
        this.row(g, this.t("album", new Object[0]), "", true, () -> this.f_96541_.m_91152_((Screen)new VigilAlbumScreen()));
        this.row(g, this.t("codex", new Object[0]), "", true, () -> ClientRoyal.open(0));
    }

    private void crown(GuiGraphics g) {
        this.row(g, this.t(this.d().m_128471_("Protecting") ? "protecting" : "ready", new Object[0]), this.t("crown_help", new Object[0]), true, null);
        this.row(g, this.t("crown_health", Float.valueOf(this.d().m_128457_("HP")), Float.valueOf(this.d().m_128457_("MaxHP"))), "", true, null);
        this.row(g, this.t("horn", new Object[0]), this.t("horn_help", new Object[0]), !this.d().m_128471_("Protecting"), () -> this.action(VigilNetwork.Action.HORN));
        this.row(g, this.t("bandage", new Object[0]), this.t("bandage_help", new Object[0]), true, null);
        this.row(g, this.t("feast", new Object[0]), this.t("feast_help", new Object[0]), true, null);
    }

    private void cemetery(GuiGraphics g) {
        this.row(g, this.t("cemetery", new Object[0]), this.t("grave_help", new Object[0]), true, null);
        this.row(g, this.t(this.d().m_128471_("NearGrave") ? "grave_ready" : "grave_needed", new Object[0]), "", true, null);
        for (CompoundTag n : this.roster().stream().filter(r -> r.m_128471_("PaidRequired")).toList()) {
            UUID id = n.m_128342_("Id");
            this.row(g, ModLanguage.user(n.m_128461_("Name")), this.t("return_stats", n.m_128451_("Free"), n.m_128451_("Deaths"), n.m_128451_("Fee")), true, () -> {
                this.selected = id;
            });
            if (!n.m_128471_("PaidRequired")) continue;
            this.row(g, this.t("pay", n.m_128451_("Fee"), ModLanguage.user(n.m_128461_("Name"))), "", this.d().m_128471_("NearGrave") && this.d().m_128451_("Diamonds") + this.d().m_128451_("Offerings") >= n.m_128451_("Fee") && this.d().m_128454_("Time") >= n.m_128454_("ReturnAt"), () -> this.send(VigilNetwork.Action.PAY, n.m_128451_("Fee"), 0, 0, id, null));
        }
        this.row(g, this.t("deposit", new Object[0]), this.t("offering_help", new Object[0]), this.d().m_128471_("NearGrave"), () -> this.send(VigilNetwork.Action.OFFERINGS, 1, 0, 0, null, null));
        this.row(g, this.t("withdraw", new Object[0]), "", this.d().m_128471_("NearGrave") && this.d().m_128451_("Offerings") > 0, () -> this.send(VigilNetwork.Action.OFFERINGS, -1, 0, 0, null, null));
    }

    private void posts(GuiGraphics g) {
        this.picker(g, false);
        this.row(g, this.t("set_post", new Object[0]), this.t("post_help", new Object[0]), this.selected != null, () -> this.action(VigilNetwork.Action.POST));
        this.row(g, this.t("compass", new Object[0]), this.t("compass_help", new Object[0]), true, null);
        for (CompoundTag n : this.roster()) {
            if (!n.m_128471_("Post")) continue;
            BlockPos at = BlockPos.m_122022_((long)n.m_128454_("PostPos"));
            UUID id = n.m_128342_("Id");
            this.row(g, ModLanguage.user(n.m_128461_("Name")), this.t("post_at", at.m_123341_(), at.m_123342_(), at.m_123343_(), n.m_128471_("Loaded") ? this.t("loaded", new Object[0]) : this.t("resting", new Object[0])), true, () -> this.send(VigilNetwork.Action.PING, 0, 0, 0, id, null));
        }
        this.row(g, this.t("watch", new Object[0]), this.t("watch_help", new Object[0]), true, () -> this.send(VigilNetwork.Action.WATCH, 1200, 0, 0, null, null));
    }

    private void logistics(GuiGraphics g) {
        this.picker(g, false);
        this.picker(g, true);
        this.row(g, this.t("source", new Object[0]), this.t("cache_help", new Object[0]), true, () -> this.action(VigilNetwork.Action.BIND_SOURCE));
        this.row(g, this.t("destination", new Object[0]), "", true, () -> this.action(VigilNetwork.Action.BIND_DEST));
        this.row(g, this.t("courier", new Object[0]), this.t("courier_help", new Object[0]), this.selected != null, () -> this.action(VigilNetwork.Action.COURIER));
        this.row(g, this.t("tow", new Object[0]), this.t("tow_help", new Object[0]), this.selected != null && this.buddy != null, () -> this.action(VigilNetwork.Action.TOW));
        this.row(g, this.t("stop_job", new Object[0]), "", !this.d().m_128469_("Job").m_128456_(), () -> this.action(VigilNetwork.Action.STOP_JOB));
    }

    private void lens(GuiGraphics g) {
        CompoundTag n = this.d().m_128469_("Lens");
        this.row(g, this.t("lens", new Object[0]), this.t("lens_help", new Object[0]), true, null);
        if (!n.m_128456_()) {
            this.row(g, ModLanguage.user(n.m_128461_("Name")), this.t("lens_stats", Float.valueOf(n.m_128457_("HP")), Float.valueOf(n.m_128457_("Max")), n.m_128459_("Attack"), n.m_128451_("Armor")), true, null);
            this.row(g, this.t(n.m_128471_("Allowed") ? "recruitable" : "blocked", new Object[0]), "", true, null);
        }
    }

    private void powers(GuiGraphics g) {
        this.picker(g, false);
        this.picker(g, true);
        this.row(g, this.t("link", new Object[0]), this.t("link_help", new Object[0]), this.selected != null && this.buddy != null, () -> this.action(VigilNetwork.Action.LINK));
        this.row(g, this.t("unlink", new Object[0]), "", true, () -> this.action(VigilNetwork.Action.UNLINK));
        this.row(g, this.t("bash", new Object[0]), this.t("bash_help", new Object[0]), true, () -> {
            this.action(VigilNetwork.Action.BASH);
            this.f_96541_.m_91152_(null);
        });
        this.row(g, this.t("volley", new Object[0]), this.t("volley_help", new Object[0]), true, () -> {
            this.action(VigilNetwork.Action.VOLLEY);
            this.f_96541_.m_91152_(null);
        });
        this.row(g, this.t("meteor", new Object[0]), this.t("meteor_help", new Object[0]), this.selected != null, () -> {
            this.action(VigilNetwork.Action.METEOR);
            this.f_96541_.m_91152_(null);
        });
        this.row(g, this.t("prism", new Object[0]), this.t("prism_help", new Object[0]), true, null);
        this.row(g, this.t("snare", new Object[0]), this.t("snare_help", new Object[0]), true, null);
    }

    private void games(GuiGraphics g) {
        CompoundTag n = this.run();
        if (!n.m_128456_()) {
            this.row(g, this.t("mode." + n.m_128461_("Kind").toLowerCase(Locale.ROOT), new Object[0]), "", true, () -> this.show(8));
            this.row(g, this.t("pause", new Object[0]), "", n.m_128451_("State") == 0, () -> this.action(VigilNetwork.Action.PAUSE));
            this.row(g, this.t("resume", new Object[0]), "", n.m_128451_("State") == 1, () -> this.action(VigilNetwork.Action.RESUME));
            this.row(g, this.t("claim", new Object[0]), "", n.m_128451_("State") == 2, () -> this.action(VigilNetwork.Action.CLAIM));
            this.row(g, this.t("leave", new Object[0]), "", true, () -> this.action(VigilNetwork.Action.LEAVE));
            return;
        }
        this.row(g, this.t(this.hard ? "hard" : "relaxed", new Object[0]), "", true, () -> {
            this.hard = !this.hard;
        });
        for (int i = 0; i < 5; ++i) {
            int mode = i;
            this.row(g, this.t("mode." + VigilRuns.KINDS[i].name().toLowerCase(Locale.ROOT), new Object[0]), this.t("mode_help." + i, new Object[0]), true, () -> VigilNetwork.request(VigilNetwork.Action.START, mode, this.hard ? 1 : 0, 0, null, null, mode == 4 ? this.roster().stream().filter(r -> r.m_128471_("Loaded")).limit(4L).map((? super T r) -> r.m_128342_("Id")).toList() : List.of()));
        }
    }

    private void board(GuiGraphics g) {
        boolean enabled;
        CompoundTag n = this.run();
        if (n.m_128456_()) {
            this.row(g, this.t("games", new Object[0]), this.t("game_requirements", new Object[0]), true, () -> this.show(7));
            return;
        }
        String kind = n.m_128461_("Kind");
        int turn = n.m_128451_("Turn");
        this.row(g, this.t("mode." + kind.toLowerCase(Locale.ROOT), new Object[0]), this.t("turn", turn), true, null);
        boolean bl = enabled = n.m_128451_("State") == 0;
        if (kind.equals("CONSTELLATION")) {
            int[] tiles = n.m_128465_("Tiles");
            if (tiles.length == 9) {
                int cell = Math.max(28, Math.min(44, (this.f_96543_ - 60) / 3));
                int x = (this.f_96543_ - cell * 3) / 2;
                int y = this.cursor;
                this.cursor += cell * 3 + 8;
                String[] pipes = new String[]{"\u00b7", "\u2575", "\u2576", "\u2514", "\u2577", "\u2502", "\u250c", "\u251c", "\u2574", "\u2518", "\u2500", "\u2534", "\u2510", "\u2524", "\u252c", "\u253c"};
                for (int i = 0; i < 9; ++i) {
                    int index = i;
                    this.button(g, x + i % 3 * cell, y + i / 3 * cell, cell - 3, cell - 3, pipes[tiles[i] & 0xF], enabled, () -> this.send(VigilNetwork.Action.INPUT, index, 0, turn, null, null));
                }
            }
        } else if (kind.equals("ECHO_RHYTHM")) {
            long elapsed = (n.m_128451_("State") == 0 ? ClientVigil.now() : n.m_128454_("Now")) - n.m_128454_("Started") - 40L;
            int beat = (int)Math.floorDiv(elapsed + 15L, 30);
            int[] lanes = n.m_128465_("Lanes");
            this.row(g, this.t("rhythm_score", n.m_128451_("Score"), Math.max(0, Math.min(12, beat + 1))), "", true, null);
            int y = this.cursor;
            int ww = (this.f_96543_ - 48) / 4;
            this.cursor += 40;
            for (int i = 0; i < 4; ++i) {
                int lane = i;
                this.button(g, 16 + i * ww, y, ww - 3, 32, (beat >= 0 && beat < 12 && lanes.length == 12 && lanes[beat] == i ? "\u25c6 " : "") + (i + 1), enabled, () -> this.send(VigilNetwork.Action.INPUT, lane, 0, turn, null, null));
            }
            this.row(g, this.t("rhythm_help", new Object[0]), "", true, null);
        } else if (kind.equals("ASTRAL_DICE")) {
            this.row(g, this.t("dice_hp", n.m_128451_("YourHP"), n.m_128451_("EnemyHP")), this.t("dice_round", n.m_128451_("Round") + 1), true, null);
            int[] dice = n.m_128465_("Dice");
            for (int i = 0; i < dice.length; ++i) {
                int die = i;
                this.row(g, this.t("die", i + 1, dice[i]), "", enabled && !n.m_128471_("Rerolled"), () -> this.send(VigilNetwork.Action.INPUT, 2, die, turn, null, null));
            }
            this.row(g, this.t("strike", new Object[0]), "", enabled, () -> this.send(VigilNetwork.Action.INPUT, 0, 0, turn, null, null));
            this.row(g, this.t("defend", new Object[0]), "", enabled, () -> this.send(VigilNetwork.Action.INPUT, 1, 0, turn, null, null));
        } else if (kind.equals("POCKET_MAZE")) {
            this.row(g, this.t("maze_keys", Integer.bitCount(n.m_128451_("Keys"))), "", true, null);
            int cell = 26;
            int x = (this.f_96543_ - cell * 7) / 2;
            int y = this.cursor;
            this.cursor += cell * 7 + 8;
            long floor = n.m_128454_("Floor");
            for (int i = 0; i < 49; ++i) {
                int at = i;
                String label = n.m_128451_("At") == i ? "\u25c6" : (i == 40 ? "\u2605" : (i == 12 && (n.m_128451_("Keys") & 1) == 0 || i == 36 && (n.m_128451_("Keys") & 2) == 0 ? "\u25c7" : ((floor & 1L << i) != 0L ? "\u00b7" : "\u25a0")));
                this.button(g, x + i % 7 * cell, y + i / 7 * cell, cell - 2, cell - 2, label, enabled && (floor & 1L << i) != 0L, () -> this.send(VigilNetwork.Action.INPUT, at, 0, turn, null, null));
            }
        } else if (kind.equals("FERRYMAN")) {
            this.row(g, this.t("ferryman_phase", n.m_128451_("Phase") + 1), n.m_128441_("StrikeAt") ? this.t("ferryman_warning", new Object[0]) : this.t("ferryman_help", new Object[0]), true, null);
        }
        this.row(g, this.t("games", new Object[0]), "", true, () -> this.show(7));
    }

    private void map(GuiGraphics g) {
        List<CompoundTag> posts = this.roster().stream().filter(n -> n.m_128471_("Post")).toList();
        this.row(g, this.t("map", new Object[0]), this.t("map_help", new Object[0]), true, null);
        int y = this.cursor;
        int h = 142;
        this.cursor += h + 8;
        RoyalTheme.frame(g, 16, y, this.f_96543_ - 42, h, -485421030);
        List<CompoundTag> same = posts.stream().filter(n -> this.f_96541_.f_91073_ != null && n.m_128461_("PostDimension").equals(this.f_96541_.f_91073_.m_46472_().m_135782_().toString())).toList();
        if (this.f_96541_.f_91074_ != null && y >= 68 && y + h <= this.bottom) {
            double range = same.stream().mapToDouble(n -> {
                BlockPos b = BlockPos.m_122022_((long)n.m_128454_("PostPos"));
                return Math.max(Math.abs((double)b.m_123341_() - this.f_96541_.f_91074_.m_20185_()), Math.abs((double)b.m_123343_() - this.f_96541_.f_91074_.m_20189_()));
            }).max().orElse(32.0);
            range = Math.max(32.0, range + 8.0);
            int cx = (this.f_96543_ - 10) / 2;
            int cy = y + h / 2;
            g.m_280509_(cx - 2, cy - 2, cx + 3, cy + 3, -1);
            for (CompoundTag n2 : same) {
                BlockPos at = BlockPos.m_122022_((long)n2.m_128454_("PostPos"));
                int xx = cx + (int)(((double)at.m_123341_() - this.f_96541_.f_91074_.m_20185_()) / range * (double)(this.f_96543_ - 60) / 2.0);
                int yy = cy + (int)(((double)at.m_123343_() - this.f_96541_.f_91074_.m_20189_()) / range * 60.0);
                g.m_280509_(xx - 3, yy - 3, xx + 3, yy + 3, n2.m_128471_("Loaded") ? -1981295 : -6518599);
            }
        }
        for (CompoundTag n3 : posts) {
            BlockPos at = BlockPos.m_122022_((long)n3.m_128454_("PostPos"));
            this.row(g, ModLanguage.user(n3.m_128461_("Name")), this.t("post_at", at.m_123341_(), at.m_123342_(), at.m_123343_(), n3.m_128471_("Loaded") ? this.t("loaded", new Object[0]) : this.t("resting", new Object[0])), true, () -> this.send(VigilNetwork.Action.PING, 0, 0, 0, n3.m_128342_("Id"), null));
        }
    }

    private void settings(GuiGraphics g) {
        this.row(g, this.t("chatter", new Object[0]), this.t(this.d().m_128471_("Chatter") ? "on" : "off", new Object[0]), true, () -> this.action(VigilNetwork.Action.CHATTER));
        this.row(g, this.t("album", new Object[0]), this.t("album_help", new Object[0]), true, () -> this.f_96541_.m_91152_((Screen)new VigilAlbumScreen()));
        this.row(g, this.t("codex", new Object[0]), "", true, () -> ClientRoyal.open(0));
        this.row(g, this.t("original", new Object[0]), "", true, ClientArmy::openOriginal);
    }

    private void guide(GuiGraphics g) {
        for (int i = 1; i <= 30; ++i) {
            int target = i;
            this.row(g, i + " \u00b7 " + this.t("feature." + i, new Object[0]), this.t("feature_help." + i, new Object[0]), true, () -> this.show(switch (target) {
                case 1 -> 0;
                case 2, 3, 8, 9, 27 -> 1;
                case 4, 5, 6, 22, 23, 24 -> 3;
                case 7, 25 -> 2;
                case 13, 20 -> 4;
                case 15 -> 5;
                case 14, 16, 17, 18, 19, 26 -> 6;
                case 28 -> 10;
                case 29 -> 9;
                default -> 7;
            }));
        }
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        this.pending = null;
        this.swipe = false;
        this.downY = y;
        this.oldScroll = this.scroll;
        if (x >= (double)(this.f_96543_ - 22) && y >= 68.0 && y < (double)this.bottom && this.maxScroll > 0) {
            int track = this.bottom - 68;
            int h = Math.max(24, track * track / (track + this.maxScroll));
            int top = 68 + (track - h) * this.scroll / this.maxScroll;
            this.grab = y >= (double)top && y < (double)(top + h) ? y - (double)top : (double)h / 2.0;
            this.thumb = true;
            this.drag(y);
            return true;
        }
        for (int i = this.controls.size() - 1; i >= 0; --i) {
            if (!this.controls.get((int)i).enabled || !this.controls.get(i).hit(x, y)) continue;
            this.pending = this.controls.get(i);
            return true;
        }
        return y >= 68.0 && y < (double)this.bottom;
    }

    private void drag(double y) {
        int track = this.bottom - 68;
        int h = Math.max(24, track * track / (track + this.maxScroll));
        this.scroll = Math.max(0, Math.min(this.maxScroll, (int)((y - 68.0 - this.grab) * (double)this.maxScroll / (double)Math.max(1, track - h))));
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (b != 0) {
            return false;
        }
        if (this.thumb) {
            this.drag(y);
            return true;
        }
        if (this.downY >= 68.0 && this.downY < (double)this.bottom && Math.abs(y - this.downY) > 5.0) {
            this.swipe = true;
            this.scroll = Math.max(0, Math.min(this.maxScroll, this.oldScroll + (int)(this.downY - y)));
            return true;
        }
        return false;
    }

    public boolean m_6348_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        if (this.pending != null && !this.thumb && !this.swipe && this.pending.hit(x, y) && this.pending.action != null) {
            this.pending.action.run();
        }
        this.pending = null;
        this.swipe = false;
        this.thumb = false;
        return true;
    }

    public boolean m_6050_(double x, double y, double n) {
        this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll - (int)(n * 28.0)));
        return true;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (ClientArmy.ORIGINAL.m_90832_(key, scan)) {
            ClientArmy.openOriginal();
            return true;
        }
        if (key == 258) {
            if (!this.controls.isEmpty()) {
                this.focus = Math.floorMod(this.focus + (VigilScreen.m_96638_() ? -1 : 1), this.controls.size());
            }
            return true;
        }
        if (key == 257 && this.focus >= 0 && this.focus < this.controls.size() && this.controls.get((int)this.focus).enabled) {
            this.controls.get((int)this.focus).action.run();
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    private record Control(int x, int y, int w, int h, String label, boolean enabled, Runnable action) {
        boolean hit(double a, double b) {
            return a >= (double)this.x && a < (double)(this.x + this.w) && b >= (double)this.y && b < (double)(this.y + this.h);
        }
    }
}

