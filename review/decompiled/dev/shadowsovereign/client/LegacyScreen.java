/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.client.AdventureScreen;
import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.ClientLegacy;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.legacy.LegacyState;
import dev.shadowsovereign.network.LegacyNetwork;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

public final class LegacyScreen
extends Screen {
    private static final int W = 700;
    private static final int H = 430;
    private static final int TEXT = -1054465;
    private static final int MUTED = -5662019;
    private static final int GOLD = -1653119;
    private static final int BLUE = -7940617;
    private final List<Control> controls = new ArrayList<Control>();
    private UUID selected;
    private UUID first;
    private UUID second;
    private Page page = Page.POWERS;
    private BattleRules.Squad scope = BattleRules.Squad.ALL;
    private boolean squadEmote;
    private boolean history;
    private boolean selectAll;
    private String nickname = "";
    private String preset = "";
    private int ticks;
    private int editing;
    private int focus = -1;
    private int scroll;
    private float scale;
    private float left;
    private float top;

    public LegacyScreen(UUID selected) {
        super((Component)ModLanguage.component("Soulbound Army \u2014 Monarch's Legacy"));
        this.selected = selected;
    }

    private CompoundTag d() {
        return ClientLegacy.data;
    }

    private CompoundTag l() {
        return this.d().m_128469_("Legacy");
    }

    private CompoundTag s() {
        return this.d().m_128469_("Session");
    }

    private List<CompoundTag> roster() {
        ArrayList<CompoundTag> result = new ArrayList<CompoundTag>();
        for (Tag t : this.d().m_128437_("Roster", 10)) {
            result.add((CompoundTag)t);
        }
        result.sort(Comparator.comparing(n -> !n.m_128471_("Favorite")));
        return result;
    }

    private CompoundTag ally() {
        return this.roster().stream().filter(n -> n.m_128403_("Id") && n.m_128342_("Id").equals(this.selected)).findFirst().orElse(new CompoundTag());
    }

    private String name(UUID id) {
        return this.roster().stream().filter(n -> n.m_128403_("Id") && n.m_128342_("Id").equals(id)).map(n -> ModLanguage.user(n.m_128461_("Name"))).findFirst().orElse("Choose a companion");
    }

    public void show(Page p) {
        this.page = p;
        this.editing = 0;
        this.focus = -1;
        this.scroll = 0;
        this.nickname = this.ally().m_128461_("Nickname");
        this.build();
    }

    public void updated() {
        List<CompoundTag> rows = this.roster();
        if (rows.stream().noneMatch(n -> n.m_128403_("Id") && n.m_128342_("Id").equals(this.selected))) {
            this.selected = rows.isEmpty() ? null : rows.get(0).m_128342_("Id");
        }
        ClientLegacy.selected = this.selected;
        if (this.first == null && this.l().m_128403_("First")) {
            this.first = this.l().m_128342_("First");
        }
        if (this.second == null && this.l().m_128403_("Second")) {
            this.second = this.l().m_128342_("Second");
        }
        if (this.editing != 1) {
            this.nickname = this.ally().m_128461_("Nickname");
        }
        if (this.f_96541_ != null) {
            this.build();
        }
    }

    protected void m_7856_() {
        this.scale = Math.min(1.6f, Math.min(((float)this.f_96543_ - 14.0f) / 700.0f, ((float)this.f_96544_ - 14.0f) / 430.0f));
        this.left = ((float)this.f_96543_ / this.scale - 700.0f) / 2.0f;
        this.top = ((float)this.f_96544_ / this.scale - 430.0f) / 2.0f;
        this.updated();
    }

    private void button(int x, int y, int w, String label, BooleanSupplier enabled, Runnable click) {
        this.add(x, y, w, 23, () -> label, enabled, click);
    }

    private void add(int x, int y, int w, int h, Supplier<String> label, BooleanSupplier enabled, Runnable click) {
        this.controls.add(new Control(x, y, w, h, label, enabled, click));
    }

    private boolean near() {
        return this.d().m_128471_("Near");
    }

    private boolean free() {
        return !this.d().m_128441_("Session");
    }

    private long completions() {
        return this.d().m_128454_("Completions");
    }

    private boolean active(int level) {
        return this.selected != null && this.ally().m_128471_("Active") && !this.ally().m_128471_("Pending") && this.ally().m_128451_("Level") >= level;
    }

    private boolean anyActive() {
        return this.roster().stream().anyMatch(n -> n.m_128471_("Active") && !n.m_128471_("Pending"));
    }

    private boolean kind(AdventureData.Kind k) {
        return !this.s().m_128456_() && this.s().m_128451_("Kind") == k.ordinal();
    }

    private boolean running() {
        return !this.s().m_128456_() && this.s().m_128451_("Phase") == AdventureData.Phase.ACTIVE.ordinal();
    }

    private void send(LegacyNetwork.Action action, int value, String text) {
        this.send(action, this.selected, null, value, text);
    }

    private void send(LegacyNetwork.Action action, UUID a, UUID b, int value, String text) {
        ClientLegacy.send(new LegacyNetwork.Request(action, a, b, value, this.scope, text));
    }

    private void cycle(int delta) {
        List<CompoundTag> rows = this.roster();
        if (rows.isEmpty()) {
            return;
        }
        int i = 0;
        for (int n = 0; n < rows.size(); ++n) {
            if (!rows.get(n).m_128342_("Id").equals(this.selected)) continue;
            i = n;
        }
        ClientLegacy.selected = this.selected = rows.get(Math.floorMod(i + delta, rows.size())).m_128342_("Id");
        this.editing = 0;
        this.nickname = this.ally().m_128461_("Nickname");
        this.build();
    }

    private void build() {
        this.controls.clear();
        this.button(18, 86, 128, "< Adventures", () -> true, this::m_7379_);
        for (Page p : Page.values()) {
            this.button(18, 113 + p.ordinal() * 27, 128, AdventureService.pretty(p.name()), () -> true, () -> this.show(p));
        }
        this.button(18, 391, 128, "Back to army", () -> true, () -> this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected)));
        this.button(164, 391, 85, "Prev ally", () -> this.selected != null, () -> this.cycle(-1));
        this.button(255, 391, 85, "Next ally", () -> this.selected != null, () -> this.cycle(1));
        switch (this.page) {
            case POWERS: {
                this.button(164, 165, 250, "Shadow Exchange", () -> this.completions() >= 1L && this.active(2) && ClientLegacy.now() >= this.l().m_128454_("ExchangeAt"), () -> this.send(LegacyNetwork.Action.EXCHANGE, 0, ""));
                this.button(425, 165, 257, "Eclipse Burst", () -> this.completions() >= 5L && this.active(5) && this.l().m_128451_("Charge") == 10 && ClientLegacy.now() >= this.l().m_128454_("BurstAt"), () -> this.send(LegacyNetwork.Action.BURST, 0, ""));
                break;
            }
            case DUO_LINKS: {
                this.add(164, 166, 518, 23, () -> "First: " + this.name(this.first) + "  / choose selected ally", () -> this.selected != null, () -> {
                    this.first = this.selected;
                    this.build();
                });
                this.add(164, 207, 518, 23, () -> "Second: " + this.name(this.second) + "  / choose selected ally", () -> this.selected != null, () -> {
                    this.second = this.selected;
                    this.build();
                });
                this.button(164, 252, 250, "Link this duo", () -> this.completions() >= 3L && this.first != null && this.second != null && !this.first.equals(this.second), () -> this.send(LegacyNetwork.Action.LINK, this.first, this.second, 0, ""));
                this.button(425, 252, 257, "Unlink current duo", () -> this.l().m_128403_("First"), () -> {
                    this.second = null;
                    this.first = null;
                    this.send(LegacyNetwork.Action.UNLINK, 0, "");
                });
                break;
            }
            case GAUNTLET: {
                this.button(164, 154, 250, "Enter Endless Gauntlet", () -> this.completions() >= 5L && this.near() && this.free() && this.anyActive(), () -> this.send(LegacyNetwork.Action.GAUNTLET, 0, ""));
                String[] names = new String[]{"Fleetfoot", "Second Wind", "Night Ward"};
                int[] ranks = this.s().m_128465_("Boons");
                for (int i = 0; i < 3; ++i) {
                    int value = i;
                    int rank = ranks.length > i ? ranks[i] : 0;
                    this.button(164 + i * 174, 250, 164, names[i] + " " + rank + " / 3", () -> this.kind(AdventureData.Kind.GAUNTLET) && this.running() && this.s().m_128471_("Choosing") && rank < 3, () -> this.send(LegacyNetwork.Action.BOON, value, ""));
                }
                this.button(164, 311, 518, "Extract and finish this run", () -> this.kind(AdventureData.Kind.GAUNTLET) && this.s().m_128471_("CanExtract"), () -> this.send(LegacyNetwork.Action.EXTRACT, 0, ""));
                this.physical();
                break;
            }
            case SIEGE: {
                this.button(164, 154, 518, "Defend the Soul Crystal", () -> this.completions() >= 3L && this.near() && this.free() && this.anyActive(), () -> this.send(LegacyNetwork.Action.SIEGE, 0, ""));
                this.physical();
                break;
            }
            case RUNE_VAULT: {
                int i;
                this.button(164, 151, 250, "Open today's Rune Vault", () -> this.completions() >= 1L && this.near() && this.free() && this.l().m_128454_("VaultClaimedDay") != ClientLegacy.now() / 24000L, () -> this.send(LegacyNetwork.Action.VAULT, 0, ""));
                this.button(425, 151, 257, "Restart / replay the pattern", () -> this.kind(AdventureData.Kind.VAULT) && this.running(), () -> this.send(LegacyNetwork.Action.RETRY, 0, ""));
                String[] runes = new String[]{"SUN", "MOON", "STAR", "VOID"};
                for (i = 0; i < 4; ++i) {
                    int rune = i;
                    this.add(164 + i * 130, 226, 122, 43, () -> runes[rune], () -> this.kind(AdventureData.Kind.VAULT) && this.running() && this.near() && ClientLegacy.now() >= this.s().m_128454_("ShowUntil") && ClientLegacy.now() <= this.s().m_128454_("Deadline"), () -> this.send(LegacyNetwork.Action.SYMBOL, rune, ""));
                }
                this.physical();
                break;
            }
            case COMPANIONS: {
                this.button(164, 216, 250, "Save nickname", () -> this.selected != null, () -> {
                    this.send(LegacyNetwork.Action.NICKNAME, 0, this.nickname);
                    this.editing = 0;
                });
                this.button(425, 216, 257, "Restore original name", () -> this.selected != null, () -> {
                    this.nickname = "";
                    this.send(LegacyNetwork.Action.NICKNAME, 0, "");
                    this.editing = 0;
                });
                this.add(164, 252, 518, 23, () -> this.ally().m_128471_("Favorite") ? "Favorite: ON  / click to unpin" : "Favorite: OFF  / click to pin", () -> this.selected != null, () -> this.send(LegacyNetwork.Action.FAVORITE, this.ally().m_128471_("Favorite") ? 0 : 1, ""));
                this.add(164, 288, 518, 23, () -> this.l().m_128471_("Chatter") ? "Personality chatter: ON" : "Personality chatter: OFF", () -> true, () -> this.send(LegacyNetwork.Action.CHATTER, this.l().m_128471_("Chatter") ? 0 : 1, ""));
                break;
            }
            case EMOTES: {
                int i;
                String[] names = new String[]{"Crown", "Heart", "Spiral"};
                for (i = 0; i < 3; ++i) {
                    int emote = i;
                    this.button(164 + i * 174, 190, 164, names[i], () -> ClientLegacy.now() >= this.l().m_128454_("EmoteAt") && (this.squadEmote ? this.anyActive() : this.active(1)), () -> this.send(LegacyNetwork.Action.EMOTE, this.squadEmote ? null : this.selected, null, emote, ""));
                }
                this.add(164, 237, 250, 23, () -> this.squadEmote ? "Target: current squad" : "Target: selected ally", () -> true, () -> {
                    this.squadEmote = !this.squadEmote;
                    this.build();
                });
                this.add(425, 237, 257, 23, () -> "Scope: " + this.scope.label, () -> this.squadEmote, () -> {
                    this.scope = BattleRules.Squad.values()[(this.scope.ordinal() + 1) % BattleRules.Squad.values().length];
                    this.build();
                });
                break;
            }
            case LOADOUTS: {
                for (int i = 0; i < 3; ++i) {
                    int slot = i;
                    boolean exists = this.l().m_128441_("Loadout" + i);
                    this.button(445, 182 + i * 56, 108, exists ? "Overwrite" : "Save", () -> !this.roster().isEmpty(), () -> this.send(LegacyNetwork.Action.SAVE_LOADOUT, slot, this.preset));
                    this.button(562, 182 + i * 56, 120, "Apply", () -> exists, () -> this.send(LegacyNetwork.Action.APPLY_LOADOUT, slot, ""));
                }
                break;
            }
            case CHRONICLE: {
                this.button(510, 82, 172, this.history ? "Show achievements" : "Show recent runs", () -> true, () -> {
                    this.history = !this.history;
                    this.scroll = 0;
                    this.build();
                });
            }
        }
    }

    private void physical() {
        this.button(164, 342, 122, "Pause", this::running, () -> this.send(LegacyNetwork.Action.PAUSE, 0, ""));
        this.button(296, 342, 122, "Resume", () -> this.near() && !this.s().m_128456_() && this.s().m_128451_("Phase") == AdventureData.Phase.PAUSED.ordinal(), () -> this.send(LegacyNetwork.Action.RESUME, 0, ""));
        this.button(428, 342, 122, "Cancel", () -> !this.s().m_128456_(), () -> this.send(LegacyNetwork.Action.CANCEL, 0, ""));
        this.button(560, 342, 122, "Claim", () -> !this.s().m_128456_() && this.s().m_128451_("Phase") == AdventureData.Phase.READY.ordinal(), () -> this.send(LegacyNetwork.Action.CLAIM, 0, ""));
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        if (++this.ticks % 20 == 0) {
            this.send(LegacyNetwork.Action.VIEW, 0, "");
        }
    }

    public void m_88315_(GuiGraphics g, int mouseX, int mouseY, float partial) {
        this.m_280273_(g);
        double mx = (float)mouseX / this.scale - this.left;
        double my = (float)mouseY / this.scale - this.top;
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.left, this.top, 0.0f);
        MenuTheme.panel(g, 700, 430, 0);
        LegacyScreen.border(g, 0, 0, 700, 430, -10137475);
        g.m_280509_(0, 0, 700, 2, -1653119);
        this.text(g, "S O U L B O U N D   A R M Y  /  1.0.7", 18, 16, -5662019);
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_(18.0f, 34.0f, 0.0f);
        g.m_280168_().m_85841_(2.0f, 2.0f, 1.0f);
        this.text(g, "MONARCH'S LEGACY", 0, 0, -1054465);
        g.m_280168_().m_85849_();
        this.text(g, this.completions() + " adventures completed", 430, 35, -1653119);
        this.text(g, "Your world. Your army. Your legend.", 430, 51, -5662019);
        g.m_280509_(18, 71, 682, 72, -12110759);
        g.m_280509_(154, 85, 155, 380, -13293505);
        this.text(g, AdventureService.pretty(this.page.name()).toUpperCase(Locale.ROOT), 164, 88, -7940617);
        this.renderPage(g);
        for (int i = 0; i < this.controls.size(); ++i) {
            Control c = this.controls.get(i);
            boolean enabled = c.enabled.getAsBoolean();
            boolean hover = c.hit(mx, my) || this.focus == i;
            boolean nav = c.x == 18 && c.y == 113 + this.page.ordinal() * 27;
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + c.h, enabled ? (hover ? -10469004 : (nav ? -12703145 : -13950919)) : -15133153);
            LegacyScreen.border(g, c.x, c.y, c.w, c.h, hover && enabled ? -4744213 : (nav ? -1653119 : -11650463));
            FantasyIcons.label(g, this.f_96547_, c.label.get(), c.x, c.y, c.w, c.h, enabled ? -1054465 : -9016701);
        }
        if (this.page == Page.RUNE_VAULT && this.kind(AdventureData.Kind.VAULT) && this.running() && ClientLegacy.now() < this.s().m_128454_("ShowUntil")) {
            int[] sequence = this.s().m_128465_("Sequence");
            long start = this.s().m_128454_("ShowUntil") - (long)(sequence.length + 1) * 20L;
            int index = (int)((ClientLegacy.now() - start) / 20L);
            if (index >= 0 && index < sequence.length) {
                int x = 164 + sequence[index] * 130;
                g.m_280509_(x + 2, 228, x + 120, 267, -9219424);
                this.text(g, (new String[]{"SUN", "MOON", "STAR", "VOID"})[sequence[index]], x + 43, 244, -1054465);
            }
        }
        if (this.d().m_128441_("Notice")) {
            this.text(g, ModLanguage.fit(this.f_96547_, this.d().m_128461_("Notice"), 518), 164, 375, -1653119);
        }
        this.text(g, ModLanguage.fit(this.f_96547_, this.name(this.selected) + " / Lv." + this.ally().m_128451_("Level"), 330), 352, 399, -1653119);
        this.text(g, "Saved with your world / no internet required", 164, 419, -5662019);
        g.m_280168_().m_85849_();
    }

    private void renderPage(GuiGraphics g) {
        switch (this.page) {
            case POWERS: {
                this.line(g, 0, this.name(this.selected) + " / Lv." + this.ally().m_128451_("Level"), -1054465);
                this.line(g, 2, "Exchange: 1 completion, Lv.2+, within 24 blocks.", -5662019);
                this.text(g, "30s cooldown / " + LegacyScreen.timer(this.l().m_128454_("ExchangeAt") - ClientLegacy.now()), 164, 197, -7940617);
                this.text(g, "60s cooldown / " + LegacyScreen.timer(this.l().m_128454_("BurstAt") - ClientLegacy.now()), 425, 197, -7940617);
                this.lineAt(g, "ECLIPSE CHARGE  " + this.l().m_128451_("Charge") + " / 10", 164, 232, -1653119);
                g.m_280509_(164, 250, 682, 259, -13359553);
                g.m_280509_(164, 250, 164 + Math.min(10, this.l().m_128451_("Charge")) * 518 / 10, 259, -7940617);
                this.lineAt(g, "Burst: 5 completions, Lv.5+, 10 natural hostile kills.", 164, 278, -5662019);
                this.lineAt(g, "Cast through your ally: 8-block radius, up to 8 enemies.", 164, 300, -1054465);
                this.lineAt(g, "Failed casts spend no charge or cooldown.", 164, 324, -5662019);
                break;
            }
            case DUO_LINKS: {
                this.line(g, 0, "Unlock: 3 completions / both companions Lv.3+.", -1653119);
                this.line(g, 2, (String)(this.l().m_128403_("First") ? "Linked: " + this.name(this.l().m_128342_("First")) + " + " + this.name(this.l().m_128342_("Second")) : "No duo linked yet. Select the two partners below."), -1054465);
                this.lineAt(g, "Alternate hits on one enemy within 3 seconds to slow it.", 164, 295, -1054465);
                this.lineAt(g, "Stay within 16 blocks of each other and 32 of you.", 164, 315, -5662019);
                this.lineAt(g, "Combo cooldown: " + LegacyScreen.timer(this.l().m_128454_("DuoAt") - ClientLegacy.now()), 164, 339, -7940617);
                break;
            }
            case GAUNTLET: {
                this.line(g, 0, "Unlock: 5 completions. Endless waves, maximum 12 enemies.", -1054465);
                this.line(g, 1, "Choose a temporary upgrade every 5 waves; extract after 5.", -5662019);
                this.text(g, "Best cleared wave: " + this.l().m_128451_("BestWave"), 425, 162, -1653119);
                this.session(g, 195);
                this.lineAt(g, "Fleetfoot: +10% speed / Second Wind: heal 1 HP per rank.", 164, 286, -5662019);
                this.lineAt(g, "Night Ward: 2 shield HP per rank at each wave start.", 164, 298, -5662019);
                break;
            }
            case SIEGE: {
                this.line(g, 0, "Unlock: 3 completions. Protect the crystal for six waves.", -1054465);
                this.line(g, 1, "Defeat attackers before they reach your Sanctuary.", -5662019);
                this.session(g, 203);
                if (this.kind(AdventureData.Kind.SIEGE)) {
                    this.text(g, "SOUL CRYSTAL  " + this.s().m_128451_("Integrity") + " / 100", 164, 253, -1653119);
                    g.m_280509_(164, 272, 682, 283, -13359553);
                    g.m_280509_(164, 272, 164 + 518 * this.s().m_128451_("Integrity") / 100, 283, -7940617);
                }
                this.lineAt(g, "The Sanctuary and terrain cannot be damaged by the siege.", 164, 309, -5662019);
                break;
            }
            case RUNE_VAULT: {
                this.line(g, 0, "Unlock: 1 completion. Remember, then repeat the symbols.", -1054465);
                this.line(g, 1, "3 rounds / 3, 4, 5 symbols / one reward per saved day.", -5662019);
                if (this.kind(AdventureData.Kind.VAULT)) {
                    long clock;
                    long l = clock = this.running() ? ClientLegacy.now() : this.s().m_128454_("Clock");
                    String state = !this.running() ? AdventureData.Phase.values()[this.s().m_128451_("Phase")].name() : (clock < this.s().m_128454_("ShowUntil") ? "WATCH THE PATTERN" : "YOUR TURN \u2014 " + LegacyScreen.timer(this.s().m_128454_("Deadline") - clock));
                    this.text(g, "Round " + Math.min(3, this.s().m_128451_("Wave") + 1) + " / 3  \u2014  " + state, 164, 195, -7940617);
                    this.text(g, "Correct symbols: " + this.s().m_128451_("Input") + " / " + this.s().m_128451_("Length"), 164, 287, -1653119);
                }
                this.lineAt(g, "Mistakes cost no items. Replay restarts the three rounds.", 164, 310, -5662019);
                break;
            }
            case COMPANIONS: {
                this.line(g, 0, "Original: " + this.ally().m_128461_("OriginalName"), -1054465);
                this.line(g, 1, "Personality: " + AdventureService.pretty(this.ally().m_128456_() ? "QUIET" : this.ally().m_128461_("Personality")), -7940617);
                this.line(g, 3, "Nickname (24 characters; blank restores original)", -5662019);
                this.field(g, this.nickname, 164, 179, this.editing == 1);
                this.lineAt(g, "Personalities add dialogue without changing native AI.", 164, 329, -5662019);
                this.lineAt(g, "Favorites pin the roster; command slots stay unchanged.", 164, 346, -5662019);
                break;
            }
            case EMOTES: {
                this.line(g, 0, "Celebrate with your shadows using native soul particles.", -1054465);
                this.line(g, 2, "Purely cosmetic: crown, hearts, or a spectral spiral.", -5662019);
                this.lineAt(g, "Affects up to 8 allies within 16 blocks.", 164, 287, -1054465);
                this.lineAt(g, "Emote cooldown: " + LegacyScreen.timer(this.l().m_128454_("EmoteAt") - ClientLegacy.now()), 164, 310, -7940617);
                break;
            }
            case LOADOUTS: {
                this.line(g, 0, "Preset name (optional / 24 characters)", -5662019);
                this.field(g, this.preset, 164, 134, this.editing == 2);
                for (int i = 0; i < 3; ++i) {
                    CompoundTag n = this.l().m_128469_("Loadout" + i);
                    this.text(g, ModLanguage.fit(this.f_96547_, i + 1 + "  " + (n.m_128456_() ? "Empty slot" : ModLanguage.user(n.m_128461_("Name"))), 270), 164, 190 + i * 56, n.m_128456_() ? -5662019 : -1054465);
                }
                this.lineAt(g, "Save squads, formation, priority, retreat and scope.", 164, 350, -5662019);
                break;
            }
            case CHRONICLE: {
                CompoundTag stats = this.l().m_128469_("Stats");
                String[] labels = new String[]{"Recorded kills", "Exchanges", "Bursts", "Duo combos", "Siege wins", "Vault wins"};
                for (int i = 0; i < 6; ++i) {
                    this.text(g, labels[i] + ": " + stats.m_128454_(LegacyState.Stat.values()[i].name()), 164 + i % 2 * 259, 116 + i / 2 * 22, i % 2 == 0 ? -1054465 : -7940617);
                }
                this.text(g, "Records begin with 1.0.5; existing army progression is retained.", 164, 184, -5662019);
                if (!this.history) {
                    int row = 0;
                    ListTag earned = this.l().m_128437_("Achievements", 8);
                    for (LegacyState.Achievement a : LegacyState.Achievement.values()) {
                        boolean done = false;
                        for (Tag t : earned) {
                            if (!t.m_7916_().equals(a.name())) continue;
                            done = true;
                        }
                        long value = a.stat == null ? (long)this.l().m_128451_("BestWave") : stats.m_128454_(a.stat.name());
                        this.text(g, (done ? "\u2726 " : "\u00b7 ") + a.label + "   " + Math.min((long)a.goal, value) + " / " + a.goal, 164, 213 + row++ * 23, done ? -1653119 : -5662019);
                    }
                    break;
                }
                ListTag runs = this.l().m_128437_("Results", 10);
                this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, runs.size() - 8)));
                for (int i = 0; i < 8 && i + this.scroll < runs.size(); ++i) {
                    CompoundTag n = runs.m_128728_(i + this.scroll);
                    this.text(g, ModLanguage.fit(this.f_96547_, n.m_128461_("Kind") + " / " + n.m_128461_("Outcome") + " / waves " + n.m_128451_("Waves"), 518), 164, 213 + i * 18, -1054465);
                }
                if (!runs.isEmpty()) break;
                this.text(g, "Complete a Legacy challenge to begin your chronicle.", 164, 220, -5662019);
            }
        }
    }

    private void session(GuiGraphics g, int y) {
        if (this.s().m_128456_()) {
            this.text(g, "No interactive adventure active.", 164, y, -5662019);
            return;
        }
        int kind = this.s().m_128451_("Kind");
        String total = kind == AdventureData.Kind.GAUNTLET.ordinal() ? "endless" : Integer.toString(this.s().m_128451_("MaxWaves"));
        this.text(g, String.valueOf((Object)AdventureData.Kind.values()[kind]) + " / " + String.valueOf((Object)AdventureData.Phase.values()[this.s().m_128451_("Phase")]) + " / cleared " + this.s().m_128451_("Wave") + " / " + total, 164, y, -7940617);
        this.text(g, "Enemies: " + this.s().m_128451_("Enemies") + " / leaving or logging out pauses the run.", 164, y + 18, -5662019);
    }

    private void field(GuiGraphics g, String value, int x, int y, boolean active) {
        g.m_280509_(x, y, x + 518, y + 23, -15593445);
        LegacyScreen.border(g, x, y, 518, 23, active ? -1653119 : -10140296);
        this.text(g, ModLanguage.fit(this.f_96547_, ModLanguage.user(value + (active && this.ticks % 20 < 10 ? "_" : "")), 500), x + 8, y + 8, active ? -1054465 : -5662019);
    }

    private static String timer(long ticks) {
        if (ticks <= 0L) {
            return "READY";
        }
        long sec = (ticks + 19L) / 20L;
        return sec / 60L + ":" + String.format(Locale.ROOT, "%02d", sec % 60L);
    }

    private void line(GuiGraphics g, int row, String value, int color) {
        this.lineAt(g, value, 164, 116 + row * 13, color);
    }

    private void lineAt(GuiGraphics g, String value, int x, int y, int color) {
        this.text(g, ModLanguage.fit(this.f_96547_, value, 518), x, y, color);
    }

    private void text(GuiGraphics g, String value, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, value, x, y, color, false);
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
        this.focus = -1;
        this.editing = 0;
        this.selectAll = false;
        if (mx >= 164.0 && mx < 682.0 && (this.page == Page.COMPANIONS && my >= 179.0 && my < 202.0 || this.page == Page.LOADOUTS && my >= 134.0 && my < 157.0)) {
            this.editing = this.page == Page.COMPANIONS ? 1 : 2;
            return true;
        }
        for (Control c : List.copyOf(this.controls)) {
            if (!c.hit(mx, my) || !c.enabled.getAsBoolean()) continue;
            c.click.run();
            return true;
        }
        return false;
    }

    public boolean m_5534_(char c, int mods) {
        Object value;
        if (this.editing == 0 || Character.isISOControl(c) || c == '\u00a7') {
            return false;
        }
        Object object = value = this.editing == 1 ? this.nickname : this.preset;
        if (this.selectAll) {
            value = "";
        }
        this.selectAll = false;
        if (((String)value).codePointCount(0, ((String)value).length()) < 24 || Character.isLowSurrogate(c) && !((String)value).isEmpty() && Character.isHighSurrogate(((String)value).charAt(((String)value).length() - 1))) {
            value = (String)value + c;
        }
        if (this.editing == 1) {
            this.nickname = value;
        } else {
            this.preset = value;
        }
        return true;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (this.editing != 0) {
            String value;
            String string = value = this.editing == 1 ? this.nickname : this.preset;
            if (key == 256 || key == 257) {
                if (key == 257 && this.editing == 1) {
                    this.send(LegacyNetwork.Action.NICKNAME, 0, this.nickname);
                }
                this.editing = 0;
                return true;
            }
            if (LegacyScreen.m_96637_() && key == 65) {
                this.selectAll = true;
                return true;
            }
            if (LegacyScreen.m_96637_() && key == 86) {
                value = LegacyState.plain((this.selectAll ? "" : value) + this.f_96541_.f_91068_.m_90876_(), 24);
                this.selectAll = false;
            } else if (key == 259) {
                value = this.selectAll || value.isEmpty() ? "" : value.substring(0, value.offsetByCodePoints(value.length(), -1));
                this.selectAll = false;
            } else if (key != 258) {
                return super.m_7933_(key, scan, mods);
            }
            if (this.editing == 1) {
                this.nickname = value;
            } else {
                this.preset = value;
            }
            if (key == 258) {
                this.editing = 0;
            }
            return true;
        }
        if (key == 258) {
            for (int i = 0; i < this.controls.size(); ++i) {
                this.focus = Math.floorMod(this.focus + (LegacyScreen.m_96638_() ? -1 : 1), this.controls.size());
                if (this.controls.get((int)this.focus).enabled.getAsBoolean()) break;
            }
            return true;
        }
        if ((key == 257 || key == 32) && this.focus >= 0 && this.focus < this.controls.size() && this.controls.get((int)this.focus).enabled.getAsBoolean()) {
            this.controls.get((int)this.focus).click.run();
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    public boolean m_6050_(double x, double y, double delta) {
        if (this.page == Page.CHRONICLE && this.history) {
            this.scroll = Math.max(0, this.scroll - (int)Math.signum(delta) * 2);
            return true;
        }
        return false;
    }

    public void m_7379_() {
        this.f_96541_.m_91152_((Screen)new AdventureScreen(this.selected));
    }

    public static enum Page {
        POWERS,
        DUO_LINKS,
        GAUNTLET,
        SIEGE,
        RUNE_VAULT,
        COMPANIONS,
        EMOTES,
        LOADOUTS,
        CHRONICLE;

    }

    private record Control(int x, int y, int w, int h, Supplier<String> label, BooleanSupplier enabled, Runnable click) {
        boolean hit(double mx, double my) {
            return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + this.h);
        }
    }
}

