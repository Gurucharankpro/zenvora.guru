/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.Holder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.entity.Entity
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.client.AdventureScreen;
import dev.shadowsovereign.client.AscensionScreen;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientAscension;
import dev.shadowsovereign.client.ClientLegacy;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.SentinelScreen;
import dev.shadowsovereign.client.SovereignScreen;
import dev.shadowsovereign.client.TacticsScreen;
import dev.shadowsovereign.network.AdventureNetwork;
import dev.shadowsovereign.network.AscensionNetwork;
import dev.shadowsovereign.network.LegacyNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;

public final class ArmyScreen
extends Screen {
    private static final int W = 820;
    private static final int H = 450;
    private static final int ROWS = 6;
    private static final int TEXT = -1251329;
    private static final int MUTED = -6515528;
    private static final int PURPLE = -5340417;
    private static final int GOLD = -1653119;
    private final List<Control> controls = new ArrayList<Control>();
    private float scale;
    private float originX;
    private float originY;
    private UUID selected;
    private UUID releaseArmed;
    private int scroll;
    private int ticks;
    private int releaseTicks;
    private int focus = -1;
    private String search = "";
    private boolean searching;
    private boolean draggingThumb;
    private boolean draggingRoster;
    private boolean reverse = Boolean.parseBoolean(ClientPreferences.get("army.reverse", "false"));
    private boolean help;
    private boolean contrast = Boolean.parseBoolean(ClientPreferences.get("army.contrast", "false"));
    private List<ShadowNetwork.Entry> cached = List.of();
    private int cacheTick = -1;
    private Object cacheView;
    private String cacheKey = "";
    private double thumbGrab;
    private double dragStartY;
    private int dragStartScroll;
    private int heldArrow;
    private int heldTicks;
    private RosterFilter rosterFilter = ArmyScreen.readChoice(RosterFilter.class, ClientPreferences.get("roster.filter", "ALL"), RosterFilter.ALL);
    private RosterSort rosterSort = ArmyScreen.readChoice(RosterSort.class, ClientPreferences.get("roster.sort", "ACQUIRED"), RosterSort.ACQUIRED);

    private String loc(String en, String ar) {
        return ModLanguage.arabic() ? ar : en;
    }

    private void select(ShadowNetwork.Entry e) {
        this.selected = e.id();
        this.releaseArmed = null;
    }

    private void page(int delta) {
        List<ShadowNetwork.Entry> list = this.filtered();
        if (list.isEmpty()) {
            return;
        }
        int index = 0;
        for (int i = 0; i < list.size(); ++i) {
            if (!list.get(i).id().equals(this.selected)) continue;
            index = i;
        }
        index = Math.max(0, Math.min(list.size() - 1, index + delta));
        this.select(list.get(index));
        this.scroll = Math.max(0, Math.min(index, Math.max(0, list.size() - 6)));
    }

    public void m_7861_() {
        if (this.selected != null) {
            ClientPreferences.set("army.selected", this.selected.toString());
        }
        super.m_7861_();
    }

    private static <E extends Enum<E>> E readChoice(Class<E> type, String value, E fallback) {
        try {
            return Enum.valueOf(type, value);
        }
        catch (IllegalArgumentException ex) {
            return fallback;
        }
    }

    public int rosterOffset() {
        return this.scroll;
    }

    private void clampScroll() {
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, this.filtered().size() - 6)));
    }

    private int thumbHeight() {
        return Math.max(26, 888 / Math.max(6, this.filtered().size()));
    }

    private int thumbTop() {
        return 138 + (148 - this.thumbHeight()) * this.scroll / Math.max(1, this.filtered().size() - 6);
    }

    private void dragThumb(double y) {
        int travel = 148 - this.thumbHeight();
        this.scroll = travel <= 0 ? 0 : (int)Math.round((y - 138.0 - this.thumbGrab) * (double)Math.max(0, this.filtered().size() - 6) / (double)travel);
        this.clampScroll();
    }

    public ArmyScreen() {
        super((Component)ModLanguage.component("Soulbound Army \u2014 Your Army"));
        try {
            this.selected = UUID.fromString(ClientPreferences.get("army.selected", ""));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
    }

    public ArmyScreen(UUID selected) {
        this();
        if (selected != null) {
            this.selected = selected;
        }
    }

    protected void m_7856_() {
        this.scale = Math.min(1.5f, Math.min(((float)this.f_96543_ - 14.0f) / 820.0f, ((float)this.f_96544_ - 14.0f) / 450.0f));
        this.originX = ((float)this.f_96543_ / this.scale - 820.0f) / 2.0f;
        this.originY = ((float)this.f_96544_ / this.scale - 450.0f) / 2.0f;
        this.controls.clear();
        this.add(300, 18, 116, () -> "Adventures", () -> true, () -> {
            if (this.f_96541_.f_91074_ != null) {
                AdventureNetwork.request(new AdventureNetwork.Request(AdventureNetwork.Action.VIEW, null, 0, List.of(), this.selected, 0));
                this.f_96541_.m_91152_((Screen)new AdventureScreen(this.selected));
            }
        }, false);
        this.add(300, 43, 116, () -> "Tactics / Skills", () -> true, () -> this.f_96541_.m_91152_((Screen)new TacticsScreen(this.selected)), false);
        this.add(426, 18, 154, () -> "Recruit: " + ClientAscension.mode(), () -> true, () -> this.send(ShadowNetwork.Action.AUTO_RECRUIT, null), false);
        this.add(426, 43, 154, () -> "Auto summon: " + this.onOff(ClientArmy.view.autoSummon()), () -> true, () -> this.send(ShadowNetwork.Action.AUTO_SUMMON, null), false);
        this.add(300, 203, 136, () -> this.chosen() != null && this.chosen().respawnTicks() > 0 ? "Reforming..." : "Summon", () -> this.chosen() != null && !this.chosen().active() && this.chosen().respawnTicks() <= 0, () -> this.selected(ShadowNetwork.Action.SUMMON), true);
        this.add(444, 203, 136, () -> "Recall", () -> this.chosen() != null && this.chosen().active(), () -> this.selected(ShadowNetwork.Action.RECALL), false);
        this.add(300, 230, 136, () -> "Follow me", () -> this.chosen() != null, () -> this.selected(ShadowNetwork.Action.FOLLOW), false);
        this.add(444, 230, 136, () -> "Stay / sit", () -> this.chosen() != null, () -> this.selected(ShadowNetwork.Action.STAY), false);
        this.add(300, 257, 136, () -> "Guard here", () -> this.chosen() != null, () -> this.selected(ShadowNetwork.Action.GUARD), false);
        this.add(444, 257, 136, () -> this.chosen() != null && this.chosen().aggressive() ? "Mode: Aggressive" : "Mode: Assist", () -> this.chosen() != null, () -> this.selected(this.chosen().aggressive() ? ShadowNetwork.Action.ASSIST : ShadowNetwork.Action.AGGRESSIVE), false);
        this.add(300, 287, 280, () -> this.selected != null && this.selected.equals(this.releaseArmed) ? "Click again to release forever" : "Release shadow", () -> this.chosen() != null, () -> {
            if (this.selected.equals(this.releaseArmed)) {
                this.selected(ShadowNetwork.Action.RELEASE);
                this.releaseArmed = null;
            } else {
                this.releaseArmed = this.selected;
                this.releaseTicks = 80;
            }
        }, false);
        this.add(20, 330, 108, () -> "Summon all", () -> !ClientArmy.view.entries().isEmpty(), () -> this.send(ShadowNetwork.Action.SUMMON, null), true);
        this.add(134, 330, 108, () -> "Recall all", () -> this.activeCount() > 0L, () -> this.send(ShadowNetwork.Action.RECALL, null), false);
        this.add(248, 330, 80, () -> "Follow all", () -> !ClientArmy.view.entries().isEmpty(), () -> this.send(ShadowNetwork.Action.FOLLOW, null), false);
        this.add(334, 330, 76, () -> "Stay all", () -> !ClientArmy.view.entries().isEmpty(), () -> this.send(ShadowNetwork.Action.STAY, null), false);
        this.add(416, 330, 76, () -> "Guard all", () -> !ClientArmy.view.entries().isEmpty(), () -> this.send(ShadowNetwork.Action.GUARD, null), false);
        this.add(498, 330, 82, () -> "Done", () -> true, () -> ((ArmyScreen)this).m_7379_(), false);
        this.add(600, 325, 200, () -> "Shadow Ascension", () -> true, () -> {
            AscensionNetwork.request(AscensionNetwork.Action.VIEW, 0, false);
            this.f_96541_.m_91152_((Screen)new AscensionScreen());
        }, true);
        this.add(600, 351, 200, () -> "Motion: " + (MenuTheme.reducedMotion ? "Reduced" : "Full"), () -> true, () -> MenuTheme.toggleMotion(), false);
        this.add(600, 273, 200, () -> "Equipment / Soul Forge", () -> this.chosen() != null, () -> this.selected(ShadowNetwork.Action.INVENTORY), true);
        this.add(600, 299, 200, () -> "Living Warfront", () -> true, () -> ClientWarfront.open(this.selected, 0), true);
        this.add(600, 18, 200, () -> ModLanguage.arabic() ? "\u0627\u0644\u0644\u063a\u0629: \u0627\u0644\u0639\u0631\u0628\u064a\u0629 / English" : "Language: English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", () -> true, () -> ModLanguage.toggle(), true);
        this.add(600, 44, 200, () -> this.loc("Orders / Patrol", "\u0627\u0644\u0623\u0648\u0627\u0645\u0631 \u0648\u0627\u0644\u062f\u0648\u0631\u064a\u0627\u062a"), () -> true, () -> this.f_96541_.m_91152_((Screen)new SentinelScreen(this, this.selected)), false);
        this.add(20, 386, 174, () -> "Filter: " + this.rosterFilter.name(), () -> true, () -> {
            this.rosterFilter = RosterFilter.values()[(this.rosterFilter.ordinal() + 1) % RosterFilter.values().length];
            ClientPreferences.set("roster.filter", this.rosterFilter.name());
            this.scroll = 0;
        }, false);
        this.add(204, 386, 174, () -> "Sort: " + this.rosterSort.name(), () -> true, () -> {
            this.rosterSort = RosterSort.values()[(this.rosterSort.ordinal() + 1) % RosterSort.values().length];
            ClientPreferences.set("roster.sort", this.rosterSort.name());
            this.scroll = 0;
        }, false);
        this.add(388, 386, 192, () -> this.chosen() != null && ClientLegacy.favorite(this.selected) ? "Unpin favorite" : "Pin favorite", () -> this.chosen() != null, () -> ClientLegacy.send(new LegacyNetwork.Request(LegacyNetwork.Action.FAVORITE, this.selected, null, ClientLegacy.favorite(this.selected) ? 0 : 1, BattleRules.Squad.ALL, "")), false);
        this.add(600, 386, 200, () -> "Sovereign Trails", () -> true, () -> this.f_96541_.m_91152_((Screen)new SovereignScreen(this.selected)), true);
        this.add(20, 410, 100, () -> this.loc("Reset view", "\u0625\u0639\u0627\u062f\u0629 \u0627\u0644\u0639\u0631\u0636"), () -> true, () -> {
            this.search = "";
            this.scroll = 0;
            this.rosterFilter = RosterFilter.ALL;
            this.rosterSort = RosterSort.ACQUIRED;
            this.reverse = false;
            ClientPreferences.set("roster.filter", "ALL");
            ClientPreferences.set("roster.sort", "ACQUIRED");
            ClientPreferences.set("army.reverse", "false");
        }, false);
        this.add(126, 410, 100, () -> this.loc("Reverse sort", "\u0639\u0643\u0633 \u0627\u0644\u062a\u0631\u062a\u064a\u0628"), () -> true, () -> {
            this.reverse = !this.reverse;
            ClientPreferences.set("army.reverse", Boolean.toString(this.reverse));
        }, false);
        this.add(232, 410, 110, () -> this.loc("Find selected", "\u0625\u0638\u0647\u0627\u0631 \u0627\u0644\u0645\u062d\u062f\u062f"), () -> this.chosen() != null, () -> {
            this.search = "";
            this.rosterFilter = RosterFilter.ALL;
            List<ShadowNetwork.Entry> l = this.filtered();
            for (int i = 0; i < l.size(); ++i) {
                if (!l.get(i).id().equals(this.selected)) continue;
                this.scroll = Math.max(0, Math.min(i, l.size() - 6));
            }
            this.clampScroll();
        }, false);
        this.add(348, 410, 110, () -> this.loc("High contrast", "\u062a\u0628\u0627\u064a\u0646 \u0639\u0627\u0644\u064d"), () -> true, () -> {
            this.contrast = !this.contrast;
            ClientPreferences.set("army.contrast", Boolean.toString(this.contrast));
        }, false);
        this.add(464, 410, 116, () -> this.loc("Help / shortcuts", "\u0645\u0633\u0627\u0639\u062f\u0629 \u0648\u0627\u062e\u062a\u0635\u0627\u0631\u0627\u062a"), () -> true, () -> {
            this.help = !this.help;
        }, false);
        this.add(600, 410, 95, () -> this.loc("Companies", "\u0627\u0644\u0641\u0631\u0642"), () -> true, () -> ClientRoyal.open(1), false);
        this.add(701, 410, 99, () -> this.loc("Storybook", "\u0643\u062a\u0627\u0628 \u0627\u0644\u0642\u0635\u0629"), () -> true, () -> ClientOdyssey.open(0), false);
        this.updated();
    }

    private void add(int x, int y, int w, Supplier<String> label, BooleanSupplier enabled, Runnable click, boolean accent) {
        this.controls.add(new Control(x, y, w, 22, label, enabled, click, accent));
    }

    private String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    private long activeCount() {
        return ClientArmy.view.entries().stream().filter(ShadowNetwork.Entry::active).count();
    }

    private ShadowNetwork.Entry chosen() {
        return ClientArmy.view.entries().stream().filter(e -> e.id().equals(this.selected)).findFirst().orElse(null);
    }

    private List<ShadowNetwork.Entry> filtered() {
        String key = this.search + String.valueOf((Object)this.rosterFilter) + String.valueOf((Object)this.rosterSort) + this.reverse;
        if (this.cacheTick == this.ticks && this.cacheView == ClientArmy.view && this.cacheKey.equals(key)) {
            return this.cached;
        }
        this.cacheTick = this.ticks;
        this.cacheView = ClientArmy.view;
        this.cacheKey = key;
        String query = this.search.toLowerCase(Locale.ROOT);
        Comparator<ShadowNetwork.Entry> order = switch (this.rosterSort) {
            case RosterSort.LEVEL -> Comparator.comparingInt(ShadowNetwork.Entry::level).reversed();
            case RosterSort.KILLS -> Comparator.comparingLong(ShadowNetwork.Entry::kills).reversed();
            case RosterSort.HEALTH -> Comparator.comparingDouble(e -> e.maxHealth() <= 0.0f ? 1.0 : (double)(e.health() / e.maxHealth()));
            case RosterSort.BOND -> Comparator.comparingInt(ShadowNetwork.Entry::bond).reversed();
            case RosterSort.TYPE -> Comparator.comparing(ShadowNetwork.Entry::type);
            case RosterSort.TIER -> Comparator.comparingInt(e -> e.tier().ordinal()).reversed();
            case RosterSort.NAME -> Comparator.comparing(ShadowNetwork.Entry::name, String.CASE_INSENSITIVE_ORDER);
            default -> (a, b) -> 0;
        };
        this.cached = ClientArmy.view.entries().stream().filter(e -> e.name().toLowerCase(Locale.ROOT).contains(query) || e.type().toLowerCase(Locale.ROOT).contains(query)).filter(e -> switch (this.rosterFilter) {
            default -> throw new IncompatibleClassChangeError();
            case RosterFilter.ALL -> true;
            case RosterFilter.ACTIVE -> e.active();
            case RosterFilter.RESERVE -> {
                if (!e.active() && e.respawnTicks() <= 0) {
                    yield true;
                }
                yield false;
            }
            case RosterFilter.REFORMING -> {
                if (e.respawnTicks() >= 0) {
                    yield true;
                }
                yield false;
            }
            case RosterFilter.FAVORITES -> ClientLegacy.favorite(e.id());
            case RosterFilter.WOUNDED -> e.health() < e.maxHealth();
        }).sorted(Comparator.comparing(e -> !ClientLegacy.favorite(e.id())).thenComparing(this.reverse ? order.reversed() : order).thenComparing(ShadowNetwork.Entry::id)).toList();
        return this.cached;
    }

    public void updated() {
        if (this.chosen() == null) {
            this.selected = ClientArmy.view.entries().isEmpty() ? null : ClientArmy.view.entries().get(0).id();
        }
        this.cacheTick = -1;
        this.scroll = Math.max(0, Math.min(this.scroll, this.filtered().size() - 6));
    }

    private void send(ShadowNetwork.Action action, UUID id) {
        if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
            ShadowNetwork.request(action, id);
        }
    }

    private void selected(ShadowNetwork.Action action) {
        if (this.selected != null) {
            this.send(action, this.selected);
        }
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        ++this.ticks;
        if (this.heldArrow != 0 && ++this.heldTicks > 8 && this.heldTicks % 4 == 0) {
            this.scroll += this.heldArrow;
            this.clampScroll();
        }
        if (this.ticks % 40 == 0) {
            this.send(ShadowNetwork.Action.REFRESH, null);
        }
        if (this.releaseTicks > 0 && --this.releaseTicks == 0) {
            this.releaseArmed = null;
        }
    }

    public void m_88315_(GuiGraphics g, int mouseX, int mouseY, float partial) {
        ShadowNetwork.Entry entry;
        this.m_280273_(g);
        double mx = (float)mouseX / this.scale - this.originX;
        double my = (float)mouseY / this.scale - this.originY;
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.originX, this.originY, 0.0f);
        g.m_280509_(-3, -3, 823, 453, 0x66000000);
        MenuTheme.panel(g, 820, 450, 0);
        if (this.contrast) {
            g.m_280509_(1, 2, 819, 449, -335018482);
        }
        ArmyScreen.border(g, 0, 0, 820, 450, -11452305);
        g.m_280509_(0, 0, 820, 2, -5340417);
        g.m_280024_(1, 2, 819, 76, 1430923369, 2233653);
        this.text(g, "S O U L B O U N D   A R M Y", 20, 18, -6515528);
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_(20.0f, 36.0f, 0.0f);
        g.m_280168_().m_85841_(2.5f, 2.5f, 1.0f);
        this.text(g, "ARISE.", 0, 0, -1251329);
        g.m_280168_().m_85849_();
        this.text(g, ClientArmy.view.entries().size() + " / " + ClientArmy.view.maxArmy() + " SOULS", 172, 38, -1653119);
        this.text(g, this.activeCount() + " / " + ClientArmy.view.maxActive() + " summoned", 172, 53, -6515528);
        g.m_280509_(20, 75, 580, 76, -13096118);
        g.m_280509_(20, 84, 280, 107, -15725286);
        ArmyScreen.border(g, 20, 84, 260, 23, this.searching ? -5340417 : -12832689);
        String query = this.search.isEmpty() && !this.searching ? "Search name or mod..." : ModLanguage.user(this.search + (this.searching && this.ticks % 20 < 10 ? "_" : ""));
        this.text(g, ModLanguage.fit(this.f_96547_, query, 236), 29, 92, this.search.isEmpty() && !this.searching ? -6515528 : -1251329);
        List<ShadowNetwork.Entry> list = this.filtered();
        for (int row = 0; row < 6; ++row) {
            boolean hover;
            int index = this.scroll + row;
            int y = 116 + row * 32;
            if (index >= list.size()) continue;
            ShadowNetwork.Entry entry2 = list.get(index);
            boolean chosen = entry2.id().equals(this.selected);
            boolean bl = hover = mx >= 20.0 && mx < 268.0 && my >= (double)y && my < (double)(y + 29);
            g.m_280509_(20, y, 268, y + 29, chosen ? -13294778 : (hover ? -14410702 : -15133664));
            if (chosen) {
                ArmyScreen.border(g, 20, y, 248, 29, -7839560);
                g.m_280509_(20, y, 23, y + 29, -5340417);
            }
            g.m_280509_(31, y + 11, 36, y + 16, entry2.active() ? -4879617 : -10135178);
            if (ClientLegacy.favorite(entry2.id())) {
                this.text(g, "*", 29, y + 2, -1653119);
            }
            float hp = entry2.maxHealth() <= 0.0f ? 0.0f : Math.max(0.0f, Math.min(1.0f, entry2.health() / entry2.maxHealth()));
            g.m_280509_(44, y + 27, 263, y + 29, -13294017);
            g.m_280509_(44, y + 27, 44 + (int)(219.0f * hp), y + 29, hp < 0.3f ? -1739917 : (hp < 0.65f ? -1653119 : -8792399));
            this.text(g, ModLanguage.fit(this.f_96547_, "Lv." + entry2.level() + "  " + ModLanguage.user(entry2.name()), 205), 44, y + 5, 0xFF000000 | entry2.tier().color.m_126665_());
            this.text(g, (String)(entry2.respawnTicks() >= 0 ? "Soul returns in " + ((long)entry2.respawnTicks() + 19L) / 20L + "s" : (entry2.active() ? entry2.order().label + "  /  " + (entry2.aggressive() ? "Aggressive" : "Assist") : "In reserve")), 44, y + 17, -6515528);
        }
        if (list.isEmpty()) {
            this.text(g, this.search.isEmpty() ? "YOUR ARMY STARTS HERE" : "No matching shadows", 37, 160, -5340417);
            if (this.search.isEmpty()) {
                this.text(g, "Defeat a mob to claim its shadow.", 37, 181, -6515528);
                this.text(g, "Vanilla and modded mobs can join.", 37, 196, -6515528);
                this.text(g, "Then summon and command it here.", 37, 211, -6515528);
            }
        }
        if (list.size() > 6) {
            g.m_280509_(274, 116, 294, 138, -11980194);
            g.m_280509_(274, 286, 294, 308, -11980194);
            this.text(g, "\u25b2", 280, 123, -1251329);
            this.text(g, "\u25bc", 280, 293, -1251329);
            g.m_280509_(274, 138, 294, 286, -13621699);
            g.m_280509_(275, this.thumbTop(), 293, this.thumbTop() + this.thumbHeight(), this.draggingThumb ? -1653119 : -5340417);
            this.text(g, "\u2261", 280, this.thumbTop() + this.thumbHeight() / 2 - 4, -14412498);
        }
        if ((entry = this.chosen()) == null) {
            this.text(g, "THE SHADOW MONARCH", 300, 96, -1653119);
            this.text(g, "A fallen enemy. A new ally.", 300, 121, -1251329);
            this.text(g, "Select a shadow to issue orders.", 300, 151, -6515528);
            this.text(g, ClientArmy.OPEN.m_90863_().getString() + " opens this army panel.", 300, 168, -6515528);
        } else {
            this.text(g, ModLanguage.fit(this.f_96547_, ModLanguage.user(entry.name()), 276), 300, 91, -1251329);
            this.text(g, ModLanguage.fit(this.f_96547_, entry.type(), 276), 300, 108, -6515528);
            this.text(g, (String)(entry.respawnTicks() >= 0 ? "SOUL RETURNS IN " + ((long)entry.respawnTicks() + 19L) / 20L + "s" : (entry.active() ? "\u25cf  SUMMONED" : "\u25c7  IN RESERVE")), 300, 129, entry.respawnTicks() >= 0 ? -9056513 : (entry.active() ? -5340417 : -1653119));
            this.text(g, "Lv." + entry.level() + "  |  " + entry.kills() + " kills  |  +" + (entry.level() - 1) * 10 + "% power", 300, 146, -1653119);
            String health = String.format(Locale.ROOT, "%.0f / %.0f HP", Float.valueOf(entry.health()), Float.valueOf(entry.maxHealth()));
            this.text(g, health, 300, 161, -1251329);
            g.m_280509_(300, 174, 580, 179, -13294017);
            float ratio = entry.maxHealth() <= 0.0f ? 0.0f : Math.max(0.0f, Math.min(1.0f, entry.health() / entry.maxHealth()));
            g.m_280509_(300, 174, 300 + (int)(280.0f * ratio), 179, -5340417);
            this.text(g, "Order: " + entry.order().label + "  |  " + (entry.aggressive() ? "Hunt hostiles" : "Defend & assist"), 300, 188, -6515528);
        }
        this.text(g, this.loc("Showing ", "\u0627\u0644\u0645\u0639\u0631\u0648\u0636 ") + list.size() + " / " + ClientArmy.view.entries().size(), 20, 311, -6515528);
        MenuTheme.art(g, 0, 600, 84, 200, 226);
        g.m_280509_(600, 84, 800, 310, 1711998742);
        if (entry != null) {
            UUID entityId = null;
            if (this.f_96541_.f_91073_ != null) {
                for (Entity e : this.f_96541_.f_91073_.m_104735_()) {
                    if (!ClientArmy.shadows.contains(e.m_20148_()) || !e.m_7755_().getString().contains(entry.name())) continue;
                    entityId = e.m_20148_();
                    break;
                }
            }
            MenuTheme.portrait(g, entry.type(), entityId, 700, 223, 32);
            this.text(g, ModLanguage.fit(this.f_96547_, ModLanguage.user(entry.name()), 180), 610, 96, -1653119);
            this.text(g, ModLanguage.fit(this.f_96547_, entry.tier().label + " \u2022 " + entry.tier().power + "\u00d7 base power", 180), 610, 111, 0xFF000000 | entry.tier().color.m_126665_());
            this.text(g, ModLanguage.fit(this.f_96547_, entry.trait().label + " \u2022 Bond " + entry.bond() + "/150", 180), 610, 125, -6515528);
        }
        if (entry != null) {
            int[] thresholds = new int[]{0, 5, 15, 30, 50, 75, 105, 140, 180, 225};
            int lv = Math.max(1, Math.min(10, entry.level()));
            long remaining = lv == 10 ? 0L : Math.max(0L, (long)thresholds[lv] - entry.kills());
            g.m_280509_(602, 226, 798, 270, -603322606);
            this.text(g, this.loc("Next level: ", "\u0627\u0644\u0645\u0633\u062a\u0648\u0649 \u0627\u0644\u062a\u0627\u0644\u064a: ") + (String)(lv == 10 ? this.loc("MAX", "\u0627\u0644\u0623\u0642\u0635\u0649") : remaining + this.loc(" kills", " \u0642\u062a\u0644\u0629")), 610, 232, -1653119);
            int start = thresholds[lv - 1];
            int end = lv == 10 ? 225 : thresholds[lv];
            float progress = lv == 10 ? 1.0f : Math.max(0.0f, Math.min(1.0f, (float)(entry.kills() - (long)start) / (float)(end - start)));
            g.m_280509_(610, 245, 790, 248, -13294017);
            g.m_280509_(610, 245, 610 + (int)(180.0f * progress), 248, -5340417);
            this.text(g, this.loc("Health: ", "\u0627\u0644\u0635\u062d\u0629: ") + Math.round(entry.maxHealth() <= 0.0f ? 0.0f : 100.0f * entry.health() / entry.maxHealth()) + "%", 610, 254, -1251329);
        }
        g.m_280509_(20, 318, 800, 319, -13096118);
        for (Control button : this.controls) {
            boolean hover;
            boolean enabled = button.enabled.getAsBoolean();
            boolean bl = hover = button.inside(mx, my) || this.controls.indexOf(button) == this.focus;
            int fill = !enabled ? -15133409 : (button.accent ? (hover ? -7777606 : -10011504) : (hover ? -12702382 : -14344910));
            g.m_280509_(button.x, button.y, button.x + button.w, button.y + button.h, fill);
            ArmyScreen.border(g, button.x, button.y, button.w, button.h, enabled && hover ? -5340417 : -11847585);
            FantasyIcons.label(g, this.f_96547_, button.label.get(), button.x, button.y, button.w, button.h, enabled ? -1251329 : -10134412);
        }
        this.text(g, "Drag the scrollbar or swipe the roster \u2022 Tap arrows to scroll \u2022 Esc to return", 20, 434, -6515528);
        if (my >= 257.0 && my <= 279.0 && mx >= 444.0 && mx <= 580.0) {
            g.m_280509_(294, 274, 586, 313, -99611623);
            ArmyScreen.border(g, 294, 274, 292, 39, -9875325);
            this.text(g, "Aggressive: hunts nearby hostile mobs.", 301, 281, -1251329);
            this.text(g, "Assist: fights when you attack or get hit.", 301, 297, -6515528);
        }
        if (this.help) {
            String[] stringArray;
            g.m_280509_(18, 110, 586, 311, -66451694);
            ArmyScreen.border(g, 18, 110, 568, 201, -5340417);
            if (ModLanguage.arabic()) {
                String[] stringArray2 = new String[10];
                stringArray2[0] = "\u0627\u0644\u062a\u062d\u0643\u0645 \u0628\u0627\u0644\u062c\u064a\u0634";
                stringArray2[1] = "Ctrl+F: \u0628\u062d\u062b \u2022 Esc: \u0645\u0633\u062d \u0627\u0644\u0628\u062d\u062b";
                stringArray2[2] = "Page Up / Down: \u0633\u062a\u0629 \u0631\u0641\u0627\u0642";
                stringArray2[3] = "Home / End: \u0623\u0648\u0644 \u0648\u0622\u062e\u0631 \u0631\u0641\u064a\u0642";
                stringArray2[4] = "Enter: \u0627\u0633\u062a\u062f\u0639\u0627\u0621 \u0623\u0648 \u0627\u0633\u062a\u0631\u062c\u0627\u0639 \u0627\u0644\u0645\u062d\u062f\u062f";
                stringArray2[5] = "F: \u0627\u062a\u0628\u0627\u0639 \u2022 G: \u062d\u0631\u0627\u0633\u0629 \u2022 S: \u062b\u0628\u0627\u062a";
                stringArray2[6] = "I: \u0645\u0639\u062f\u0627\u062a \u2022 R: \u062a\u062d\u062f\u064a\u062b \u0627\u0644\u0628\u064a\u0627\u0646\u0627\u062a";
                stringArray2[7] = "\u0627\u0644\u0623\u0648\u0627\u0645\u0631 \u0627\u0644\u062c\u0645\u0627\u0639\u064a\u0629 \u062a\u0624\u062b\u0631 \u0639\u0644\u0649 \u0627\u0644\u062c\u064a\u0634 \u0643\u0644\u0647";
                stringArray2[8] = "\u0627\u0644\u0625\u0637\u0644\u0627\u0642 \u0646\u0647\u0627\u0626\u064a \u0648\u064a\u062a\u0637\u0644\u0628 \u0636\u063a\u0637\u062a\u064a\u0646";
                stringArray = stringArray2;
                stringArray2[9] = "\u0627\u0636\u063a\u0637 \u0645\u0633\u0627\u0639\u062f\u0629 \u0644\u0625\u063a\u0644\u0627\u0642 \u0647\u0630\u0647 \u0627\u0644\u0644\u0648\u062d\u0629";
            } else {
                String[] stringArray3 = new String[10];
                stringArray3[0] = "ARMY FIELD GUIDE";
                stringArray3[1] = "Ctrl+F: search \u2022 Esc: clear search";
                stringArray3[2] = "Page Up / Down: move six companions";
                stringArray3[3] = "Home / End: first / last companion";
                stringArray3[4] = "Enter: summon / recall selected";
                stringArray3[5] = "F: follow \u2022 G: guard \u2022 S: stay";
                stringArray3[6] = "I: equipment \u2022 R: refresh";
                stringArray3[7] = "Bulk commands affect the entire army";
                stringArray3[8] = "Release is permanent: requires two clicks";
                stringArray = stringArray3;
                stringArray3[9] = "Tap Help again to close";
            }
            String[] lines = stringArray;
            for (int i = 0; i < lines.length; ++i) {
                this.text(g, lines[i], 30, 123 + i * 17, i == 0 ? -1653119 : -1251329);
            }
        }
        g.m_280168_().m_85849_();
    }

    private void text(GuiGraphics g, String text, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, text, x, y, color, false);
    }

    private static void border(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.m_280509_(x, y, x + w, y + 1, color);
        g.m_280509_(x, y + h - 1, x + w, y + h, color);
        g.m_280509_(x, y, x + 1, y + h, color);
        g.m_280509_(x + w - 1, y, x + w, y + h, color);
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        double mx = mouseX / (double)this.scale - (double)this.originX;
        double my = mouseY / (double)this.scale - (double)this.originY;
        boolean bl = this.searching = mx >= 20.0 && mx < 280.0 && my >= 84.0 && my < 107.0;
        if (this.searching) {
            return true;
        }
        if (mx >= 270.0 && mx < 296.0 && my >= 116.0 && my < 308.0 && this.filtered().size() > 6) {
            if (my < 138.0 || my >= 286.0) {
                this.heldArrow = my < 138.0 ? -1 : 1;
                this.heldTicks = 0;
                this.scroll += this.heldArrow;
                this.clampScroll();
            } else {
                this.draggingThumb = true;
                this.thumbGrab = my >= (double)this.thumbTop() && my < (double)(this.thumbTop() + this.thumbHeight()) ? my - (double)this.thumbTop() : (double)this.thumbHeight() / 2.0;
                this.dragThumb(my);
            }
            return true;
        }
        if (!this.help && mx >= 20.0 && mx < 268.0 && my >= 116.0 && my < 308.0) {
            this.draggingRoster = true;
            this.dragStartY = my;
            this.dragStartScroll = this.scroll;
            int row = (int)(my - 116.0) / 32 + this.scroll;
            List<ShadowNetwork.Entry> list = this.filtered();
            if (row < list.size()) {
                this.select(list.get(row));
                return true;
            }
        }
        for (Control control : this.controls) {
            if (!control.inside(mx, my) || !control.enabled.getAsBoolean()) continue;
            control.click.run();
            if (this.f_96541_ != null) {
                this.f_96541_.m_91106_().m_120367_((SoundInstance)SimpleSoundInstance.m_263171_((Holder)SoundEvents.f_12490_, (float)1.0f));
            }
            return true;
        }
        return false;
    }

    public boolean m_7979_(double x, double y, int button, double dx, double dy) {
        if (button != 0) {
            return false;
        }
        double localY = y / (double)this.scale - (double)this.originY;
        if (this.draggingThumb) {
            this.dragThumb(localY);
            return true;
        }
        if (this.draggingRoster) {
            this.scroll = this.dragStartScroll + (int)((this.dragStartY - localY) / 32.0);
            this.clampScroll();
            return true;
        }
        return super.m_7979_(x, y, button, dx, dy);
    }

    public boolean m_6348_(double x, double y, int button) {
        boolean handled = this.draggingThumb || this.draggingRoster || this.heldArrow != 0;
        this.draggingRoster = false;
        this.draggingThumb = false;
        this.heldArrow = 0;
        return handled || super.m_6348_(x, y, button);
    }

    public boolean m_6050_(double mx, double my, double delta) {
        this.scroll = Math.max(0, Math.min(Math.max(0, this.filtered().size() - 6), this.scroll - (int)Math.signum(delta)));
        return true;
    }

    public boolean m_5534_(char c, int modifiers) {
        if (this.searching && !Character.isISOControl(c) && this.search.length() < 64) {
            this.search = this.search + c;
            this.scroll = 0;
            return true;
        }
        return false;
    }

    public boolean m_7933_(int key, int scanCode, int modifiers) {
        if (key == 70 && ArmyScreen.m_96637_()) {
            this.searching = true;
            return true;
        }
        if (this.searching) {
            if (key == 259) {
                if (!this.search.isEmpty()) {
                    this.search = ArmyScreen.m_96637_() ? "" : this.search.substring(0, this.search.length() - 1);
                }
                this.scroll = 0;
                return true;
            }
            if (key == 256 || key == 257) {
                if (key == 256) {
                    this.search = "";
                    this.scroll = 0;
                }
                this.searching = false;
                return true;
            }
            if (ArmyScreen.m_96630_((int)key) && this.f_96541_ != null) {
                this.search = (this.search + this.f_96541_.f_91068_.m_90876_()).replaceAll("[\\p{Cntrl}]", "");
                if (this.search.length() > 64) {
                    this.search = this.search.substring(0, 64);
                }
                this.scroll = 0;
                return true;
            }
            return super.m_7933_(key, scanCode, modifiers);
        }
        if (key == 266 || key == 267) {
            this.page(key == 266 ? -6 : 6);
            return true;
        }
        if (key == 268 || key == 269) {
            this.page(key == 268 ? -100000 : 100000);
            return true;
        }
        if (key == 82) {
            this.send(ShadowNetwork.Action.REFRESH, null);
            return true;
        }
        if (key == 290) {
            this.help = !this.help;
            return true;
        }
        if (this.chosen() != null && !this.help) {
            ShadowNetwork.Action command;
            switch (key) {
                case 70: {
                    ShadowNetwork.Action action = ShadowNetwork.Action.FOLLOW;
                    break;
                }
                case 83: {
                    ShadowNetwork.Action action = ShadowNetwork.Action.STAY;
                    break;
                }
                case 71: {
                    ShadowNetwork.Action action = ShadowNetwork.Action.GUARD;
                    break;
                }
                case 73: {
                    ShadowNetwork.Action action = ShadowNetwork.Action.INVENTORY;
                    break;
                }
                default: {
                    ShadowNetwork.Action action = command = null;
                }
            }
            if (command != null) {
                this.selected(command);
                return true;
            }
        }
        if (key == 257 && this.focus < 0 && this.chosen() != null) {
            if (this.chosen().active()) {
                this.selected(ShadowNetwork.Action.RECALL);
            } else if (this.chosen().respawnTicks() <= 0) {
                this.selected(ShadowNetwork.Action.SUMMON);
            }
            return true;
        }
        if (key == 258) {
            for (int i = 0; i < this.controls.size(); ++i) {
                this.focus = Math.floorMod(this.focus + (ArmyScreen.m_96638_() ? -1 : 1), this.controls.size());
                if (this.controls.get((int)this.focus).enabled.getAsBoolean()) break;
            }
            return true;
        }
        if (key == 257 && this.focus >= 0 && this.controls.get((int)this.focus).enabled.getAsBoolean()) {
            this.controls.get((int)this.focus).click.run();
            return true;
        }
        if (ClientArmy.ORIGINAL.m_90832_(key, scanCode) || ClientArmy.OPEN.m_90832_(key, scanCode)) {
            this.m_7379_();
            return true;
        }
        if (key == 265 || key == 264) {
            List<ShadowNetwork.Entry> list = this.filtered();
            if (!list.isEmpty()) {
                int index = 0;
                for (int i = 0; i < list.size(); ++i) {
                    if (!list.get(i).id().equals(this.selected)) continue;
                    index = i;
                }
                index = Math.max(0, Math.min(list.size() - 1, index + (key == 265 ? -1 : 1)));
                this.selected = list.get(index).id();
                if (index < this.scroll) {
                    this.scroll = index;
                }
                if (index >= this.scroll + 6) {
                    this.scroll = index - 6 + 1;
                }
            }
            return true;
        }
        return super.m_7933_(key, scanCode, modifiers);
    }

    public static enum RosterFilter {
        ALL,
        ACTIVE,
        RESERVE,
        REFORMING,
        FAVORITES,
        WOUNDED;

    }

    public static enum RosterSort {
        ACQUIRED,
        LEVEL,
        KILLS,
        HEALTH,
        TIER,
        NAME,
        BOND,
        TYPE;

    }

    private record Control(int x, int y, int w, int h, Supplier<String> label, BooleanSupplier enabled, Runnable click, boolean accent) {
        boolean inside(double mx, double my) {
            return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + this.h);
        }
    }
}

