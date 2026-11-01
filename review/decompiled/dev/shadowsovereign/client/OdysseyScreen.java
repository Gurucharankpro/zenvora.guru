/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.entity.player.StackedContents
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.ShapedRecipe
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientMythic;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ClientRealm;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.ClientSoulLife;
import dev.shadowsovereign.client.ClientVigil;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.CodexJournal;
import dev.shadowsovereign.client.EclipseScreen;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.GoalNotesScreen;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.MasteryScreen;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.OdysseyBookCatalog;
import dev.shadowsovereign.client.OdysseyTheme;
import dev.shadowsovereign.client.QuestPlannerScreen;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.client.SentinelScreen;
import dev.shadowsovereign.client.VigilAlbumScreen;
import dev.shadowsovereign.client.WorkshopManualScreen;
import dev.shadowsovereign.mastery.MasteryCatalog;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.network.OdysseyNetwork;
import dev.shadowsovereign.network.RealmNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.odyssey.OdysseyText;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public final class OdysseyScreen
extends Screen {
    private static final String[] PAGES = new String[]{"home", "book", "atlas", "camp", "crew", "collection", "builder", "updates", "activity", "lessons", "recipes", "settings"};
    private final List<Control> controls = new ArrayList<Control>();
    private int page;
    private int scroll;
    private int maxScroll;
    private int cursor;
    private int bottom;
    private int focus = -1;
    private int inspected = -1;
    private int difficulty;
    private int filter = -1;
    private int paintColor = 1;
    private boolean body;
    private boolean thumb;
    private boolean swipe;
    private boolean editing;
    private boolean chapterPicker;
    private boolean searching;
    private double downY;
    private double grab;
    private int oldScroll;
    private Control pending;
    private String query = "";
    private String code = "";
    private String selectedRecipe = "";
    private List<OdysseyBookCatalog.Release> releases;
    private static final int BODY_TOP = 98;
    private Drawer drawer = Drawer.NONE;
    private final CodexJournal journal = new CodexJournal();
    private final Deque<Integer> back = new ArrayDeque<Integer>();
    private final Deque<Integer> forward = new ArrayDeque<Integer>();
    private final Set<String> folded = new HashSet<String>();
    private int drawerScroll;
    private int goalFilter;
    private int searchScroll;
    private boolean installedOnly;
    private boolean initialized;
    private boolean questTools;
    private boolean hideDone = Boolean.parseBoolean(ClientPreferences.get("quest.hideDone", "false"));
    private boolean unfinishedFirst = Boolean.parseBoolean(ClientPreferences.get("quest.unfinishedFirst", "false"));
    private boolean trackedOnly;
    private boolean notesOnly;
    private final LinkedHashSet<String> tracked = new LinkedHashSet();
    private List<Recipe<?>> recipeCache;
    private long recipeEpoch = -1L;
    private final Map<ResourceLocation, Boolean> recipeSupply = new HashMap<ResourceLocation, Boolean>();

    private String q(String en, String ar) {
        return InterfaceStyle.text(en, ar);
    }

    private boolean hasNote(OdysseyCatalog.Goal g) {
        return !ClientPreferences.get("quest.note." + g.id(), "").isBlank();
    }

    private void trackLocal(String id) {
        if (!this.tracked.remove(id) && this.tracked.size() < 5) {
            this.tracked.add(id);
        }
        ClientPreferences.set("quest.tracked", String.join((CharSequence)",", this.tracked));
    }

    private void chapterSummary() {
        StringBuilder out = new StringBuilder(this.t("chapter." + this.chapter(), new Object[0])).append("\n");
        for (int i = 0; i < OdysseyCatalog.goals().size(); ++i) {
            OdysseyCatalog.Goal g = OdysseyCatalog.goals().get(i);
            if (g.chapter() != this.chapter()) continue;
            out.append(this.goalDone(i) ? "[\u2713] " : "[ ] ").append(this.t("goal." + g.id(), new Object[0])).append(" \u2014 ").append(this.t("goal_help." + g.id(), new Object[0])).append("\n");
        }
        this.f_96541_.f_91068_.m_90911_(ModLanguage.tr(out.toString()));
    }

    private void exportJournal() {
        StringBuilder out = new StringBuilder();
        for (OdysseyCatalog.Goal goal : OdysseyCatalog.goals()) {
            String n = ClientPreferences.get("quest.note." + goal.id(), "");
            if (!this.tracked.contains(goal.id()) && n.isBlank()) continue;
            out.append(this.t("goal." + goal.id(), new Object[0])).append("\n").append(ModLanguage.user(n)).append("\n\n");
        }
        this.f_96541_.f_91068_.m_90911_(ModLanguage.tr(out.toString()));
    }

    private void nextUnfinishedChapter() {
        for (int j = 1; j <= 12; ++j) {
            int c = (this.chapter() + j) % 12;
            if (this.complete(c) >= OdysseyCatalog.chapter(c).size()) continue;
            this.chapterTo(c);
            return;
        }
    }

    private void nextReward() {
        for (int c = 0; c < 12; ++c) {
            int prior;
            int n = prior = c > 7 ? (1 << c - 7) - 1 << 7 : 0;
            if ((this.d().m_128451_("Claims") & 1 << c) != 0 || (this.d().m_128451_("Claims") & prior) != prior || this.complete(c) != OdysseyCatalog.chapter(c).size()) continue;
            this.chapterTo(c);
            return;
        }
    }

    private String j(String key, Object ... args) {
        return ModLanguage.user(Component.m_237110_((String)("chronicles.shadowsovereign.ui." + key), (Object[])args).getString());
    }

    private void remember() {
        if (this.drawer == Drawer.NONE) {
            this.journal.remember(this.page, this.scroll);
        }
    }

    private void navigate(int value, boolean history) {
        this.remember();
        int next = Math.floorMod(value, 12);
        if (history && next != this.page) {
            this.back.addLast(this.page);
            while (this.back.size() > 24) {
                this.back.removeFirst();
            }
            this.forward.clear();
        }
        this.page = next;
        this.drawer = Drawer.NONE;
        this.scroll = this.journal.position(this.page);
        this.searching = false;
        this.editing = false;
        this.query = "";
        this.focus = -1;
        this.inspected = -1;
    }

    private void history(boolean previous) {
        Deque<Integer> to;
        Deque<Integer> from = previous ? this.back : this.forward;
        Deque<Integer> deque = to = previous ? this.forward : this.back;
        if (from.isEmpty()) {
            return;
        }
        int target = from.removeLast();
        to.addLast(this.page);
        this.navigate(target, false);
    }

    private void drawer(Drawer value) {
        if (this.drawer == Drawer.NONE) {
            this.remember();
            this.drawerScroll = this.scroll;
        }
        boolean close = this.drawer == value;
        this.drawer = close ? Drawer.NONE : value;
        this.scroll = close ? this.drawerScroll : 0;
        this.query = "";
        this.searching = false;
        this.editing = false;
        this.focus = -1;
    }

    protected void m_7856_() {
        if (!this.initialized) {
            this.initialized = true;
            this.scroll = this.journal.position(this.page);
            MythicNetwork.request(MythicNetwork.Action.VIEW);
        }
    }

    public void m_7861_() {
        this.remember();
        this.journal.flush();
        if (this.page == 1) {
            ClientPreferences.set("quest.filter", Integer.toString(this.goalFilter));
            ClientPreferences.set("quest.query", this.query);
        }
        super.m_7861_();
    }

    public OdysseyScreen(int page) {
        super((Component)OdysseyText.c("title", new Object[0]));
        this.page = Math.max(0, Math.min(11, page));
        for (String id : ClientPreferences.get("quest.tracked", "").split(",")) {
            if (this.tracked.size() >= 5 || !OdysseyCatalog.goals().stream().anyMatch(g -> g.id().equals(id))) continue;
            this.tracked.add(id);
        }
        try {
            this.goalFilter = Math.floorMod(Integer.parseInt(ClientPreferences.get("quest.filter", "0")), 3);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        if (page == 1) {
            this.query = ClientPreferences.get("quest.query", "");
        }
    }

    public int currentPage() {
        return this.page;
    }

    public int scrollOffset() {
        return this.scroll;
    }

    private CompoundTag d() {
        return ClientOdyssey.data;
    }

    private CompoundTag run() {
        return this.d().m_128469_("Run");
    }

    private String t(String key, Object ... args) {
        return ClientOdyssey.t(key, args);
    }

    private UUID selected() {
        return this.d().m_128403_("Selected") ? this.d().m_128342_("Selected") : null;
    }

    private List<CompoundTag> roster() {
        return this.d().m_128437_("Roster", 10).stream().map(v -> (CompoundTag)v).toList();
    }

    private CompoundTag chosen() {
        return this.roster().stream().filter(v -> v.m_128342_("Id").equals(this.selected())).findFirst().orElse(new CompoundTag());
    }

    private void send(OdysseyNetwork.Action action, int x, int y) {
        OdysseyNetwork.request(action, this.selected(), x, y, this.run().m_128451_("Turn"), "");
    }

    private void act(OdysseyNetwork.Action action) {
        this.send(action, 0, 0);
    }

    private void show(int value) {
        this.navigate(value, true);
    }

    private void text(GuiGraphics g, String label, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, label, x, y, color, false);
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String label, boolean enabled, Runnable action) {
        if (this.body && (y < 98 || y + h > this.bottom)) {
            return;
        }
        RoyalTheme.frame(g, x, y, w, h, enabled ? (this.journal.contrast ? -15724776 : -232185034) : -535160799);
        boolean emblem = w >= 72;
        String fitted = ModLanguage.fit(this.f_96547_, label, w - (emblem ? 30 : 10));
        if (emblem) {
            FantasyIcons.button(g, label, x, y, w, h);
        }
        if (label.equals("\u2606") || label.equals("\u2605")) {
            this.bookmarkIcon(g, x + w / 2 - 4, y + h / 2 - 4, label.equals("\u2605"));
        } else {
            this.text(g, fitted, x + (w - ModLanguage.width(this.f_96547_, fitted)) / 2 + (emblem ? 8 : 0), y + (h - 9) / 2, enabled ? -1981295 : -4937283);
        }
        this.controls.add(new Control(x, y, w, h, label, label.equals("\u2606") || label.equals("\u2605") ? this.j("mark_toggle", new Object[0]) : label, enabled, action));
    }

    private void bookmarkIcon(GuiGraphics g, int x, int y, boolean filled) {
        String[] shape = new String[]{"....#....", "...###...", "#########", ".#######.", "..#####..", ".#######.", ".##...##.", "##.....##"};
        for (int row = 0; row < shape.length; ++row) {
            for (int col = 0; col < 9; ++col) {
                if (shape[row].charAt(col) != '#' || !filled && row != 0 && row != shape.length - 1 && col != 0 && col != 8 && shape[row - 1].charAt(col) != '.' && shape[row + 1].charAt(col) != '.' && shape[row].charAt(col - 1) != '.' && shape[row].charAt(col + 1) != '.') continue;
                g.m_280509_(x + col, y + row, x + col + 1, y + row + 1, filled ? -1981295 : -4937283);
            }
        }
    }

    private void row(GuiGraphics g, String title, String description, boolean enabled, Runnable action) {
        List lines;
        List list = description.isBlank() ? List.of() : (lines = this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(description), this.f_96543_ - (this.page == 5 ? 98 : 68)));
        int h = lines.isEmpty() ? (this.journal.touch || InterfaceStyle.flag("touch") ? 42 : 29) : Math.max(this.journal.touch || InterfaceStyle.flag("touch") ? 68 : 52, 29 + lines.size() * 13);
        int y = this.cursor;
        this.cursor += h + 7;
        if (y + h < 98 || y >= this.bottom) {
            return;
        }
        RoyalTheme.frame(g, 16, y, this.f_96543_ - 42, h, this.journal.contrast ? -16250349 : -383968729);
        this.text(g, ModLanguage.fit(this.f_96547_, title, this.f_96543_ - (this.page == 5 ? 110 : 86)), 25, y + 8, enabled ? -1981295 : -4937283);
        FantasyIcons.draw(g, FantasyIcons.forText(title), this.f_96543_ - 50, y + 5, 18);
        int yy = y + 26;
        for (FormattedCharSequence line : lines) {
            ModLanguage.draw(g, this.f_96547_, line, 25, yy, -988710, false);
            yy += 13;
        }
        if (action != null && y >= 98 && y + 25 <= this.bottom) {
            this.controls.add(new Control(16, y, this.f_96543_ - 42, y + h <= this.bottom ? h : 25, title, description, enabled, action));
        }
    }

    private void hero(GuiGraphics g, int tile, String title, String subtitle) {
        int h = this.f_96544_ < 300 ? 42 : 92;
        int y = this.cursor;
        this.cursor += h + 9;
        if (y + h < 98 || y >= this.bottom) {
            return;
        }
        OdysseyTheme.art(g, tile, 16, y, this.f_96543_ - 42, h);
        g.m_280024_(16, y, this.f_96543_ - 26, y + h, this.journal.dim ? -636482792 : 1141902104, -317583589);
        this.text(g, ModLanguage.fit(this.f_96547_, title, this.f_96543_ - 70), 25, y + h - 33, -1981295);
        this.text(g, ModLanguage.fit(this.f_96547_, subtitle, this.f_96543_ - 70), 25, y + h - 17, -988710);
    }

    private boolean goalDone(int index) {
        ListTag goals = this.d().m_128437_("Goals", 10);
        return index >= 0 && index < goals.size() && goals.m_128728_(index).m_128471_("Done");
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        this.controls.clear();
        this.bottom = this.f_96544_ - 33;
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, this.page % 8);
        this.text(g, ModLanguage.fit(this.f_96547_, this.t("title", new Object[0]), this.f_96543_ - 175), 17, 17, -1981295);
        this.button(g, this.f_96543_ - 155, 10, 116, 24, "English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", true, ModLanguage::toggle);
        this.button(g, this.f_96543_ - 34, 10, 23, 24, "\u00d7", true, () -> ((OdysseyScreen)this).m_7379_());
        this.button(g, 16, 38, 26, 24, "<", true, () -> this.show(this.page - 1));
        this.button(g, 48, 38, this.f_96543_ - 202, 24, this.t(PAGES[this.page], new Object[0]), true, () -> this.show(0));
        this.button(g, this.f_96543_ - 148, 38, 80, 24, this.t("army_key", ClientArmy.ORIGINAL.m_90863_().getString()), true, ClientArmy::openOriginal);
        this.button(g, this.f_96543_ - 62, 38, 24, 24, ">", true, () -> this.show(this.page + 1));
        this.button(g, this.f_96543_ - 34, 38, 24, 24, "\u2302", true, () -> this.show(0));
        int bw = (this.f_96543_ - 49) / 7;
        String[] nav = new String[]{this.j("index", new Object[0]), this.j("quick", new Object[0]), this.journal.marked(this.page) ? "\u2605" : "\u2606", this.j("marks", new Object[0]), this.j("history_back", new Object[0]), this.j("history_forward", new Object[0]), "?"};
        for (int i = 0; i < 7; ++i) {
            int n = i;
            this.button(g, 16 + i * (bw + 3), 68, bw, 24, nav[i], i != 4 && i != 5 || i == 4 && !this.back.isEmpty() || i == 5 && !this.forward.isEmpty(), () -> {
                switch (n) {
                    case 0: {
                        this.drawer(Drawer.INDEX);
                        break;
                    }
                    case 1: {
                        this.drawer(Drawer.QUICK);
                        break;
                    }
                    case 2: {
                        this.journal.bookmark(this.page);
                        break;
                    }
                    case 3: {
                        this.drawer(Drawer.MARKS);
                        break;
                    }
                    case 4: {
                        this.history(true);
                        break;
                    }
                    case 5: {
                        this.history(false);
                        break;
                    }
                    case 6: {
                        this.drawer(Drawer.HELP);
                    }
                }
            });
        }
        g.m_280588_(12, 98, this.f_96543_ - 22, this.bottom);
        this.body = true;
        this.cursor = 102 - this.scroll;
        if (this.drawer != Drawer.NONE) {
            this.drawDrawer(g);
        } else {
            switch (this.page) {
                case 0: {
                    this.home(g);
                    break;
                }
                case 1: {
                    this.book(g);
                    break;
                }
                case 2: {
                    this.atlas(g);
                    break;
                }
                case 3: {
                    this.camp(g);
                    break;
                }
                case 4: {
                    this.crew(g);
                    break;
                }
                case 5: {
                    this.collection(g);
                    break;
                }
                case 6: {
                    this.builder(g);
                    break;
                }
                case 7: {
                    this.updates(g);
                    break;
                }
                case 8: {
                    this.activity(g);
                    break;
                }
                case 9: {
                    this.lessons(g);
                    break;
                }
                case 10: {
                    this.recipes(g);
                    break;
                }
                case 11: {
                    this.settings(g);
                }
            }
        }
        this.body = false;
        g.m_280618_();
        this.maxScroll = Math.max(0, this.cursor + this.scroll - this.bottom + 4);
        this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll));
        if (this.maxScroll > 0) {
            int track = this.bottom - 98;
            int h = Math.max(24, track * track / (track + this.maxScroll));
            int y = 98 + (track - h) * this.scroll / this.maxScroll;
            g.m_280509_(this.f_96543_ - 18, 98, this.f_96543_ - 7, this.bottom, -14344145);
            g.m_280509_(this.f_96543_ - 17, y, this.f_96543_ - 8, y + h, -2836099);
        }
        int ratio = this.maxScroll == 0 ? 100 : this.scroll * 100 / this.maxScroll;
        g.m_280509_(16, this.f_96544_ - 31, this.f_96543_ - 26, this.f_96544_ - 29, -14081484);
        g.m_280509_(16, this.f_96544_ - 31, 16 + (this.f_96543_ - 42) * ratio / 100, this.f_96544_ - 29, -5928742);
        this.text(g, ModLanguage.fit(this.f_96547_, this.t("footer", this.d().m_128451_("Progress"), OdysseyCatalog.goals().size(), this.d().m_128451_("Seals")) + " \u00b7 " + ratio + "%", this.f_96543_ - 92), 16, this.f_96544_ - 18, this.journal.contrast ? -988710 : -4937283);
        this.button(g, this.f_96543_ - 80, this.f_96544_ - 25, 25, 19, "\u2191", this.scroll > 0, () -> {
            this.scroll = 0;
        });
        this.button(g, this.f_96543_ - 51, this.f_96544_ - 25, 25, 19, "\u2193", this.scroll < this.maxScroll, () -> {
            this.scroll = this.maxScroll;
        });
        Control hovered = null;
        for (Control c : this.controls) {
            if (!c.hit(mx, my)) continue;
            hovered = c;
            break;
        }
        if (hovered != null) {
            g.m_280509_(hovered.x, hovered.y, hovered.x + 2, hovered.y + hovered.h, -5403165);
            if (!(this.journal.touch || hovered.help.isBlank() || hovered.help.equals(hovered.label))) {
                g.m_280245_(this.f_96547_, this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(hovered.label + "\n" + hovered.help), Math.max(100, Math.min(280, this.f_96543_ - 40))), mx, my);
            }
        }
        if (this.focus >= 0 && this.focus < this.controls.size()) {
            Control c = this.controls.get(this.focus);
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 2, -1981295);
        }
        super.m_88315_(g, mx, my, partial);
    }

    private void meter(GuiGraphics g, String title, int done, int total, int tile) {
        int y = this.cursor;
        int h = this.journal.touch ? 54 : 46;
        this.cursor += h + 7;
        if (y + h < 98 || y >= this.bottom) {
            return;
        }
        RoyalTheme.frame(g, 16, y, this.f_96543_ - 42, h, this.journal.contrast ? -16250349 : -333768665);
        OdysseyTheme.icon(g, tile, 23, y + 8, 24);
        this.text(g, ModLanguage.fit(this.f_96547_, title, this.f_96543_ - 90), 55, y + 8, -1981295);
        this.text(g, done + " / " + total, 55, y + 23, -988710);
        g.m_280509_(23, y + h - 8, this.f_96543_ - 34, y + h - 5, -13358266);
        g.m_280509_(23, y + h - 8, 23 + (this.f_96543_ - 57) * Math.max(0, Math.min(total, done)) / Math.max(1, total), y + h - 5, -5338135);
    }

    private int chapter() {
        return Math.max(0, Math.min(11, this.d().m_128451_("Chapter")));
    }

    private int complete(int chapter) {
        int count = 0;
        for (int i = 0; i < OdysseyCatalog.goals().size(); ++i) {
            if (OdysseyCatalog.goals().get(i).chapter() != chapter || !this.goalDone(i)) continue;
            ++count;
        }
        return count;
    }

    private int nextGoal() {
        int i;
        for (i = 0; i < OdysseyCatalog.goals().size(); ++i) {
            if (OdysseyCatalog.goals().get(i).chapter() != this.chapter() || this.goalDone(i)) continue;
            return i;
        }
        for (i = 0; i < OdysseyCatalog.goals().size(); ++i) {
            if (this.goalDone(i)) continue;
            return i;
        }
        return -1;
    }

    private void inspectGoal(int i) {
        OdysseyCatalog.Goal goal = OdysseyCatalog.goals().get(i);
        this.show(1);
        this.send(OdysseyNetwork.Action.CHAPTER, goal.chapter(), 0);
        this.inspected = i;
        this.scroll = 0;
    }

    private void spotlight(GuiGraphics g) {
        int pinned;
        int n = pinned = this.d().m_128441_("Pinned") ? this.d().m_128451_("Pinned") : -1;
        if (pinned >= 0 && pinned < OdysseyCatalog.goals().size()) {
            OdysseyCatalog.Goal goal = OdysseyCatalog.goals().get(pinned);
            this.row(g, this.j("tracked", this.t("goal." + goal.id(), new Object[0])), this.t("goal_help." + goal.id(), new Object[0]), true, () -> this.inspectGoal(pinned));
        }
    }

    private void home(GuiGraphics g) {
        this.row(g, this.q("Companion orders & patrols", "\u0623\u0648\u0627\u0645\u0631 \u0627\u0644\u0631\u0641\u0627\u0642 \u0648\u0627\u0644\u062f\u0648\u0631\u064a\u0627\u062a"), this.q("Linked torches, guard posts and closer companionship.", "\u0634\u0639\u0644\u0627\u062a \u0645\u0631\u062a\u0628\u0637\u0629 \u0648\u0646\u0642\u0627\u0637 \u062d\u0631\u0627\u0633\u0629 \u0648\u062a\u0641\u0627\u0639\u0644 \u0623\u0642\u0631\u0628 \u0645\u0639 \u0627\u0644\u0631\u0641\u0627\u0642."), true, () -> this.f_96541_.m_91152_((Screen)new SentinelScreen(this, null)));
        this.row(g, this.q("The Sovereign\u2019s Mastery \u00b7 " + MasteryCatalog.STAGES.size() + " stages", "\u0625\u062a\u0642\u0627\u0646 \u0627\u0644\u0645\u0644\u0643 \u00b7 " + MasteryCatalog.STAGES.size() + " \u0645\u0631\u062d\u0644\u0629"), this.q("An illustrated, ordered education and an eight-step hunt for every legendary creature.", "\u062a\u0639\u0644\u064a\u0645 \u0645\u0635\u0648\u0631 \u0648\u0645\u0646\u0638\u0645 \u0648\u0645\u0633\u0627\u0631 \u0635\u064a\u062f \u0645\u0646 \u062b\u0645\u0627\u0646\u064a \u0645\u0631\u0627\u062d\u0644 \u0644\u0643\u0644 \u0645\u062e\u0644\u0648\u0642 \u0623\u0633\u0637\u0648\u0631\u064a."), true, () -> this.f_96541_.m_91152_((Screen)new MasteryScreen(this)));
        this.row(g, this.q("Artisan workshop manual", "\u062f\u0644\u064a\u0644 \u0627\u0644\u0648\u0631\u0634 \u0627\u0644\u0645\u0637\u0648\u0651\u0631\u0629"), this.q("New 3D stations, their purpose and actual recipes.", "\u0645\u062d\u0637\u0627\u062a \u062b\u0644\u0627\u062b\u064a\u0629 \u0627\u0644\u0623\u0628\u0639\u0627\u062f \u0648\u0648\u0638\u0627\u0626\u0641\u0647\u0627 \u0648\u0648\u0635\u0641\u0627\u062a\u0647\u0627 \u0627\u0644\u0641\u0639\u0644\u064a\u0629."), true, () -> this.f_96541_.m_91152_((Screen)new WorkshopManualScreen(this)));
        this.hero(g, 0, this.t("journey_welcome", new Object[0]), this.t("journey_subtitle", new Object[0]));
        int total = this.d().m_128441_("ArmyTotal") ? this.d().m_128451_("ArmyTotal") : this.roster().size();
        int active = this.d().m_128441_("ArmyActive") ? this.d().m_128451_("ArmyActive") : (int)this.roster().stream().filter(r -> r.m_128403_("Entity")).count();
        this.row(g, this.j("army_status", total, active), this.j("chapter_status", this.chapter() + 1, this.complete(this.chapter()), OdysseyCatalog.chapter(this.chapter()).size()), true, () -> this.show(4));
        this.meter(g, this.j("story_progress", new Object[0]), this.d().m_128451_("Progress"), OdysseyCatalog.goals().size(), 0);
        this.row(g, this.j("resume", this.t(PAGES[this.journal.last], new Object[0])), this.j("resume_help", new Object[0]), true, () -> this.show(this.journal.last));
        int next = this.nextGoal();
        if (next >= 0) {
            OdysseyCatalog.Goal goal = OdysseyCatalog.goals().get(next);
            this.row(g, this.j("next_goal", this.t("goal." + goal.id(), new Object[0])), this.t("goal_help." + goal.id(), new Object[0]), true, () -> this.inspectGoal(next));
        } else {
            this.row(g, this.j("story_complete", new Object[0]), this.j("story_complete_help", new Object[0]), true, () -> this.show(1));
        }
        this.spotlight(g);
        this.row(g, ClientMythic.t("title", new Object[0]), this.j("mythic_status", ClientMythic.data.m_128451_("Acquired"), MythicExpedition.IDS.length + 1), true, ClientMythic::open);
        this.row(g, ModLanguage.user(Component.m_237115_((String)"expanse.shadowsovereign.title").getString()), ModLanguage.user(Component.m_237115_((String)"expanse.shadowsovereign.automatic").getString()), true, () -> RealmNetwork.request(RealmNetwork.Action.OPEN, 6, 0, null));
        this.row(g, ClientRealm.t("title", new Object[0]), ClientRealm.t("edition_help", new Object[0]), true, ClientRealm::open);
        CompoundTag chosen = this.chosen();
        if (!chosen.m_128456_()) {
            this.row(g, ModLanguage.user(chosen.m_128461_("Name")), this.t("crew_line", chosen.m_128451_("Level"), chosen.m_128451_("Affection")), true, () -> this.show(4));
        }
        if (!this.run().m_128456_()) {
            this.row(g, this.t("resume_activity", new Object[0]), this.t("activity_hint", new Object[0]), true, () -> this.show(8));
        }
        String[] labels = new String[]{"book", "atlas", "camp", "crew", "collection", "builder", "updates", "recipes"};
        int[] destinations = new int[]{1, 2, 3, 4, 5, 6, 7, 10};
        int h = this.journal.touch ? 48 : (this.f_96544_ < 300 ? 33 : 42);
        int base = this.cursor;
        int w = (this.f_96543_ - 48) / 2;
        this.cursor += 4 * (h + 7);
        for (int i = 0; i < labels.length; ++i) {
            int target = destinations[i];
            int x = 16 + i % 2 * (this.f_96543_ - 36) / 2;
            int y = base + i / 2 * (h + 7);
            if (y < 98 || y + h > this.bottom) continue;
            OdysseyTheme.art(g, i % 16, x, y, w, h);
            this.button(g, x, y, w, h, this.t(labels[i], new Object[0]), true, () -> this.show(target));
        }
        this.row(g, this.t("codex", new Object[0]), this.t("old_systems", new Object[0]), true, () -> ClientRoyal.open(0));
        this.row(g, ClientSoulLife.t("title", new Object[0]), "", true, () -> ClientSoulLife.open(0));
        this.row(g, this.t("return_home", new Object[0]), this.t("return_help", new Object[0]), this.d().m_128471_("Realm"), () -> this.act(OdysseyNetwork.Action.RETURN));
    }

    private void chapterTo(int value) {
        this.send(OdysseyNetwork.Action.CHAPTER, Math.floorMod(value, 12), 0);
        this.scroll = 0;
        this.query = "";
        this.inspected = -1;
    }

    private void questTools(GuiGraphics g) {
        this.row(g, this.q("Quest workbench", "\u0623\u062f\u0648\u0627\u062a \u0627\u0644\u0645\u0647\u0645\u0627\u062a"), this.q("Tracking \u2022 notes \u2022 filters \u2022 chapter navigation", "\u062a\u062a\u0628\u0639 \u2022 \u0645\u0644\u0627\u062d\u0638\u0627\u062a \u2022 \u0641\u0644\u0627\u062a\u0631 \u2022 \u062a\u0646\u0642\u0644 \u0627\u0644\u0641\u0635\u0648\u0644"), true, () -> {
            this.questTools = !this.questTools;
        });
        if (!this.questTools) {
            return;
        }
        this.row(g, this.q("Tracked objectives: ", "\u0627\u0644\u0645\u0647\u0645\u0627\u062a \u0627\u0644\u0645\u062a\u062a\u0628\u0639\u0629: ") + this.tracked.size() + " / 5", this.q("Local checklist; completion is always verified by the server.", "\u0642\u0627\u0626\u0645\u0629 \u0634\u062e\u0635\u064a\u0629\u061b \u0625\u0643\u0645\u0627\u0644 \u0627\u0644\u0645\u0647\u0645\u0627\u062a \u064a\u062a\u062d\u0642\u0642 \u0645\u0646\u0647 \u0627\u0644\u0633\u064a\u0631\u0641\u0631 \u062f\u0627\u0626\u0645\u064b\u0627."), true, () -> {
            this.trackedOnly = !this.trackedOnly;
            this.scroll = 0;
        });
        for (int i = 0; i < OdysseyCatalog.goals().size(); ++i) {
            OdysseyCatalog.Goal goal = OdysseyCatalog.goals().get(i);
            if (!this.tracked.contains(goal.id())) continue;
            int index = i;
            this.row(g, (this.goalDone(i) ? "\u2713 " : "\u25c7 ") + this.t("goal." + goal.id(), new Object[0]), this.q("Open this tracked objective", "\u0627\u0641\u062a\u062d \u0627\u0644\u0645\u0647\u0645\u0629 \u0627\u0644\u0645\u062a\u062a\u0628\u0639\u0629"), true, () -> {
                this.inspected = index;
                this.send(OdysseyNetwork.Action.CHAPTER, goal.chapter(), 0);
                this.trackedOnly = true;
                this.query = "";
                this.scroll = 0;
                this.questTools = false;
            });
        }
        this.row(g, this.q("Hide completed: ", "\u0625\u062e\u0641\u0627\u0621 \u0627\u0644\u0645\u0643\u062a\u0645\u0644: ") + this.q(this.hideDone ? "ON" : "OFF", this.hideDone ? "\u0645\u0641\u0639\u0644" : "\u0645\u0639\u0637\u0644"), "", true, () -> {
            this.hideDone = !this.hideDone;
            ClientPreferences.set("quest.hideDone", Boolean.toString(this.hideDone));
        });
        this.row(g, this.q("Unfinished first: ", "\u063a\u064a\u0631 \u0627\u0644\u0645\u0643\u062a\u0645\u0644 \u0623\u0648\u0644\u064b\u0627: ") + this.q(this.unfinishedFirst ? "ON" : "OFF", this.unfinishedFirst ? "\u0645\u0641\u0639\u0644" : "\u0645\u0639\u0637\u0644"), "", true, () -> {
            this.unfinishedFirst = !this.unfinishedFirst;
            ClientPreferences.set("quest.unfinishedFirst", Boolean.toString(this.unfinishedFirst));
        });
        this.row(g, this.q("Only objectives with notes: ", "\u0627\u0644\u0645\u0647\u0645\u0627\u062a \u0630\u0627\u062a \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0627\u062a \u0641\u0642\u0637: ") + this.q(this.notesOnly ? "ON" : "OFF", this.notesOnly ? "\u0645\u0641\u0639\u0644" : "\u0645\u0639\u0637\u0644"), "", true, () -> {
            this.notesOnly = !this.notesOnly;
        });
        this.row(g, this.q("Tracked only: ", "\u0627\u0644\u0645\u062a\u062a\u0628\u0639\u0629 \u0641\u0642\u0637: ") + this.q(this.trackedOnly ? "ON" : "OFF", this.trackedOnly ? "\u0645\u0641\u0639\u0644" : "\u0645\u0639\u0637\u0644"), "", true, () -> {
            this.trackedOnly = !this.trackedOnly;
        });
        this.row(g, this.q("Next unfinished chapter", "\u0627\u0644\u0641\u0635\u0644 \u0627\u0644\u062a\u0627\u0644\u064a \u063a\u064a\u0631 \u0627\u0644\u0645\u0643\u062a\u0645\u0644"), "", true, this::nextUnfinishedChapter);
        this.row(g, this.q("Find claimable chapter", "\u0627\u0639\u062b\u0631 \u0639\u0644\u0649 \u0641\u0635\u0644 \u0645\u0643\u0627\u0641\u0623\u062a\u0647 \u062c\u0627\u0647\u0632\u0629"), this.q("Never claims rewards automatically.", "\u0644\u0627 \u064a\u0633\u062a\u0644\u0645 \u0623\u064a \u0645\u0643\u0627\u0641\u0623\u0629 \u062a\u0644\u0642\u0627\u0626\u064a\u064b\u0627."), true, this::nextReward);
        this.row(g, this.q("First unfinished objective", "\u0623\u0648\u0644 \u0645\u0647\u0645\u0629 \u063a\u064a\u0631 \u0645\u0643\u062a\u0645\u0644\u0629"), "", this.nextGoal() >= 0, () -> {
            int n = this.nextGoal();
            if (n >= 0) {
                this.inspectGoal(n);
                this.questTools = false;
            }
        });
        this.row(g, this.q("Copy chapter checklist", "\u0646\u0633\u062e \u0642\u0627\u0626\u0645\u0629 \u0645\u0647\u0645\u0627\u062a \u0627\u0644\u0641\u0635\u0644"), this.q("Includes completion status and instructions.", "\u064a\u0634\u0645\u0644 \u062d\u0627\u0644\u0629 \u0627\u0644\u0625\u0643\u0645\u0627\u0644 \u0648\u0627\u0644\u062a\u0639\u0644\u064a\u0645\u0627\u062a."), true, this::chapterSummary);
        this.row(g, this.q("Copy personal journal", "\u0646\u0633\u062e \u062f\u0641\u062a\u0631 \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0627\u062a"), this.q("Copies tracked objectives and private notes only.", "\u064a\u0646\u0633\u062e \u0627\u0644\u0645\u0647\u0645\u0627\u062a \u0627\u0644\u0645\u062a\u062a\u0628\u0639\u0629 \u0648\u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0627\u062a \u0627\u0644\u0634\u062e\u0635\u064a\u0629 \u0641\u0642\u0637."), true, this::exportJournal);
        this.row(g, this.q("Reset quest view", "\u0625\u0639\u0627\u062f\u0629 \u0639\u0631\u0636 \u0627\u0644\u0645\u0647\u0645\u0627\u062a"), "", true, () -> {
            this.query = "";
            this.goalFilter = 0;
            this.notesOnly = false;
            this.trackedOnly = false;
            this.unfinishedFirst = false;
            this.hideDone = false;
            this.scroll = 0;
            ClientPreferences.setAll(Map.of("quest.hideDone", "false", "quest.unfinishedFirst", "false", "quest.filter", "0", "quest.query", ""));
        });
    }

    private void book(GuiGraphics g) {
        boolean priorClaimed;
        boolean claimed;
        this.row(g, this.q("Quest planning desk", "\u0645\u0643\u062a\u0628 \u062a\u062e\u0637\u064a\u0637 \u0627\u0644\u0645\u0647\u0645\u0627\u062a"), this.q("Search all chapters, build a session plan and review your notes.", "\u0627\u0628\u062d\u062b \u0641\u064a \u0643\u0644 \u0627\u0644\u0641\u0635\u0648\u0644 \u0648\u062e\u0637\u0637 \u0644\u062c\u0644\u0633\u0629 \u0627\u0644\u0644\u0639\u0628 \u0648\u0631\u0627\u062c\u0639 \u0645\u0644\u0627\u062d\u0638\u0627\u062a\u0643."), true, () -> this.f_96541_.m_91152_((Screen)new QuestPlannerScreen(this)));
        this.row(g, this.q("Continue the illustrated mastery journey", "\u062a\u0627\u0628\u0639 \u0631\u062d\u0644\u0629 \u0627\u0644\u0625\u062a\u0642\u0627\u0646 \u0627\u0644\u0645\u0635\u0648\u0631\u0629"), this.q("Foundations, legendary hunts and graduation.", "\u0627\u0644\u062a\u0623\u0633\u064a\u0633 \u0648\u0635\u064a\u062f \u0627\u0644\u0623\u0633\u0627\u0637\u064a\u0631 \u0648\u0627\u0644\u062a\u062e\u0631\u062c."), true, () -> this.f_96541_.m_91152_((Screen)new MasteryScreen(this)));
        int chapter = this.chapter();
        this.hero(g, OdysseyTheme.chapter(chapter), this.t("chapter." + chapter, new Object[0]), this.t("chapter_help." + chapter, new Object[0]));
        this.meter(g, this.j("chapter_status", chapter + 1, this.complete(chapter), OdysseyCatalog.chapter(chapter).size()), this.complete(chapter), OdysseyCatalog.chapter(chapter).size(), chapter);
        this.questTools(g);
        int chapterTotal = OdysseyCatalog.chapter(chapter).size();
        this.row(g, this.q("Chapter completion: ", "\u0627\u0643\u062a\u0645\u0627\u0644 \u0627\u0644\u0641\u0635\u0644: ") + 100 * this.complete(chapter) / Math.max(1, chapterTotal) + "%", this.q("Remaining objectives: ", "\u0627\u0644\u0645\u0647\u0645\u0627\u062a \u0627\u0644\u0645\u062a\u0628\u0642\u064a\u0629: ") + (chapterTotal - this.complete(chapter)), true, null);
        int y = this.cursor;
        int w = (this.f_96543_ - 48) / 2;
        this.cursor += 34;
        this.button(g, 16, y, w, 27, this.j("previous_chapter", new Object[0]), true, () -> this.chapterTo(chapter - 1));
        this.button(g, 22 + w, y, w, 27, this.j("next_chapter", new Object[0]), true, () -> this.chapterTo(chapter + 1));
        this.row(g, this.t("choose_chapter", new Object[0]), this.j("chapters_help", new Object[0]), true, () -> {
            this.chapterPicker = !this.chapterPicker;
        });
        if (this.chapterPicker) {
            for (int i = 0; i < 12; ++i) {
                int target = i;
                int done = this.complete(i);
                claimed = (this.d().m_128451_("Claims") & 1 << i) != 0;
                this.row(g, (chapter == i ? "\u25c6 " : "") + (i + 1) + " \u00b7 " + this.t("chapter." + i, new Object[0]), this.j(claimed ? "claimed" : "chapter_status", i + 1, done, OdysseyCatalog.chapter(i).size()), true, () -> this.chapterTo(target));
                this.meter(g, this.t("chapter." + i, new Object[0]), done, OdysseyCatalog.chapter(i).size(), i);
            }
        }
        this.spotlight(g);
        this.search(g);
        this.row(g, this.j("objective_filter", this.j("filter." + this.goalFilter, new Object[0])), this.j("search_goals_help", new Object[0]), true, () -> {
            this.goalFilter = (this.goalFilter + 1) % 3;
        });
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < OdysseyCatalog.goals().size(); ++i) {
            indexes.add(i);
        }
        if (this.unfinishedFirst) {
            indexes.sort(Comparator.comparing(this::goalDone));
        }
        int found = 0;
        Iterator done = indexes.iterator();
        while (done.hasNext()) {
            int i = (Integer)done.next();
            OdysseyCatalog.Goal goal = OdysseyCatalog.goals().get(i);
            boolean done2 = this.goalDone(i);
            if (this.trackedOnly && !this.tracked.contains(goal.id()) || this.notesOnly && !this.hasNote(goal) || this.hideDone && done2 || this.query.isBlank() && !this.trackedOnly && !this.notesOnly && goal.chapter() != chapter || this.goalFilter == 1 && done2 || this.goalFilter == 2 && !done2 || !this.matches(this.t("goal." + goal.id(), new Object[0]) + " " + this.t("goal_help." + goal.id(), new Object[0]))) continue;
            ++found;
            int index = i;
            this.row(g, (done2 ? "\u2713 " : "\u25c7 ") + this.t("goal." + goal.id(), new Object[0]), (String)(this.query.isBlank() ? "" : this.t("chapter." + goal.chapter(), new Object[0]) + " \u00b7 ") + this.t("goal_help." + goal.id(), new Object[0]), true, () -> {
                this.inspected = this.inspected == index ? -1 : index;
            });
            if (this.inspected != i) continue;
            this.row(g, this.q(this.tracked.contains(goal.id()) ? "Remove from personal tracker" : "Track in personal checklist", this.tracked.contains(goal.id()) ? "\u0625\u0632\u0627\u0644\u0629 \u0645\u0646 \u0627\u0644\u062a\u062a\u0628\u0639 \u0627\u0644\u0634\u062e\u0635\u064a" : "\u0625\u0636\u0627\u0641\u0629 \u0644\u0644\u062a\u062a\u0628\u0639 \u0627\u0644\u0634\u062e\u0635\u064a"), this.q("Maximum five objectives; separate from the HUD pin.", "\u062e\u0645\u0633 \u0645\u0647\u0645\u0627\u062a \u0643\u062d\u062f \u0623\u0642\u0635\u0649\u061b \u0645\u0633\u062a\u0642\u0644 \u0639\u0646 \u062a\u062b\u0628\u064a\u062a \u0634\u0627\u0634\u0629 \u0627\u0644\u0644\u0639\u0628."), this.tracked.contains(goal.id()) || this.tracked.size() < 5, () -> this.trackLocal(goal.id()));
            String note = ClientPreferences.get("quest.note." + goal.id(), "");
            this.row(g, this.q("Private objective note", "\u0645\u0644\u0627\u062d\u0638\u0629 \u0634\u062e\u0635\u064a\u0629 \u0644\u0644\u0645\u0647\u0645\u0629"), note.isBlank() ? this.q("Add a plan or reminder.", "\u0623\u0636\u0641 \u062e\u0637\u0629 \u0623\u0648 \u062a\u0630\u0643\u064a\u0631\u064b\u0627.") : ModLanguage.user(note), true, () -> this.f_96541_.m_91152_((Screen)new GoalNotesScreen(this, goal.id(), this.t("goal." + goal.id(), new Object[0]), this.t("goal_help." + goal.id(), new Object[0]))));
            this.row(g, this.q("Copy objective instructions", "\u0646\u0633\u062e \u062a\u0639\u0644\u064a\u0645\u0627\u062a \u0627\u0644\u0645\u0647\u0645\u0629"), "", true, () -> this.f_96541_.f_91068_.m_90911_(ModLanguage.tr(this.t("goal." + goal.id(), new Object[0]) + "\n" + this.t("goal_help." + goal.id(), new Object[0]))));
            this.row(g, this.t("open_destination", new Object[0]), this.t("goal." + goal.id(), new Object[0]), true, () -> this.routeGoal(goal));
            this.row(g, this.t(this.d().m_128451_("Pinned") == index ? "unpin_step" : "track_step", new Object[0]), this.j("pin_help", new Object[0]), true, () -> this.send(OdysseyNetwork.Action.PIN, this.d().m_128451_("Pinned") == index ? -1 : index, 0));
            if (!goal.lesson()) continue;
            this.row(g, this.t("acknowledge_lesson", new Object[0]), this.t("lesson_requirements", new Object[0]), !done2, () -> this.send(OdysseyNetwork.Action.LESSON, index, 0));
        }
        this.row(g, this.q("Matching objectives: ", "\u0627\u0644\u0645\u0647\u0645\u0627\u062a \u0627\u0644\u0645\u0637\u0627\u0628\u0642\u0629: ") + found, "", true, null);
        if (found == 0) {
            this.row(g, this.j("no_results", new Object[0]), this.j("clear_search", new Object[0]), true, () -> {
                this.query = "";
                this.goalFilter = 0;
                this.notesOnly = false;
                this.trackedOnly = false;
                this.hideDone = false;
                this.scroll = 0;
            });
        }
        boolean done2 = this.complete(chapter) == OdysseyCatalog.chapter(chapter).size();
        claimed = (this.d().m_128451_("Claims") & 1 << chapter) != 0;
        int prior = chapter > 7 ? (1 << chapter - 7) - 1 << 7 : 0;
        boolean bl = priorClaimed = (this.d().m_128451_("Claims") & prior) == prior;
        String reason = this.j(claimed ? "reward_claimed" : (!done2 ? "reward_steps" : (!priorClaimed ? "reward_previous" : "reward_ready")), new Object[0]);
        this.row(g, this.j("reward_title", new Object[0]), this.t("chapter_reward", new Object[0]) + " \u00b7 " + reason, true, null);
        this.row(g, this.t("claim_chapter", new Object[0]), reason, done2 && !claimed && priorClaimed, () -> this.send(OdysseyNetwork.Action.CLAIM, chapter, 0));
        this.row(g, this.t("ending_title", new Object[0]), this.t("ending_help", new Object[0]), true, () -> this.act(OdysseyNetwork.Action.CORONATE));
        this.row(g, this.t("restore_book", new Object[0]), "", true, () -> this.act(OdysseyNetwork.Action.RESTORE));
    }

    private void routeGoal(OdysseyCatalog.Goal goal) {
        switch (goal.id()) {
            case "lesson.cemetery": {
                ClientVigil.open(2);
                break;
            }
            case "lesson.protection": {
                ClientVigil.open(1);
                break;
            }
            case "vigil_games": {
                ClientVigil.open(7);
                break;
            }
            case "companies_banners": {
                ClientRoyal.open(1);
                break;
            }
            case "mastery": {
                ClientRoyal.open(2);
                break;
            }
            case "royal_modes": {
                ClientRoyal.open(9);
                break;
            }
            default: {
                this.route(goal.route());
            }
        }
    }

    private void route(String route) {
        switch (route) {
            case "army": {
                ClientArmy.openOriginal();
                break;
            }
            case "equipment": {
                if (this.selected() == null) break;
                ShadowNetwork.request(ShadowNetwork.Action.INVENTORY, this.selected());
                break;
            }
            case "soullife": {
                ClientSoulLife.open(0);
                break;
            }
            case "warfront": {
                ClientWarfront.open(this.selected(), 0);
                break;
            }
            case "eclipse": {
                this.f_96541_.m_91152_((Screen)new EclipseScreen(0));
                break;
            }
            case "adventure": 
            case "journey": {
                ClientRoyal.open(3);
                break;
            }
            case "camp": {
                this.show(3);
                break;
            }
            case "bosses": 
            case "odyssey": {
                this.show(2);
                break;
            }
            default: {
                ClientRoyal.open(0);
            }
        }
    }

    private void atlas(GuiGraphics g) {
        OdysseyCatalog.Mode advice;
        this.hero(g, 2, this.t("atlas", new Object[0]), this.t("atlas_help", new Object[0]));
        this.row(g, this.t("party_heading", new Object[0]), this.t("party_help", new Object[0]), true, () -> this.show(4));
        this.search(g);
        CompoundTag camp = this.d().m_128469_("CampState");
        OdysseyCatalog.Mode mode = advice = camp.m_128441_("Recommended") ? OdysseyCatalog.Mode.read(camp.m_128451_("Recommended")) : null;
        if (advice != null) {
            this.row(g, this.t("council_route", new Object[0]), this.t("feature." + advice.feature, new Object[0]), this.d().m_128471_("Camp") && this.run().m_128456_(), () -> {
                this.send(OdysseyNetwork.Action.START, advice.ordinal(), this.difficulty);
                this.f_96541_.m_91152_(null);
            });
        }
        this.row(g, this.t("difficulty", this.difficulty), this.t("difficulty_help", new Object[0]), true, () -> {
            this.difficulty = (this.difficulty + 1) % 3;
        });
        this.row(g, this.t("filter", this.filter < 0 ? this.t("all", new Object[0]) : this.t("filter." + this.filter, new Object[0])), "", true, () -> {
            this.filter = this.filter >= 3 ? -1 : this.filter + 1;
        });
        for (OdysseyCatalog.Mode mode2 : OdysseyCatalog.Mode.values()) {
            int category;
            if (mode2.retired() || !this.matches(this.t("feature." + mode2.feature, new Object[0]))) continue;
            int n = mode2.boss() ? 1 : (mode2.game() ? 2 : (category = mode2.ordinal() >= 16 ? 3 : 0));
            if (this.filter >= 0 && this.filter != category) continue;
            this.hero(g, mode2.boss() ? mode2.ordinal() - 2 : mode2.region() + 4, this.t("feature." + mode2.feature, new Object[0]), this.t("feature_help." + mode2.feature, new Object[0]));
            this.row(g, this.t("start", new Object[0]), this.t("mode_requirements", new Object[0]), this.d().m_128471_("Camp") && this.run().m_128456_(), () -> {
                this.send(OdysseyNetwork.Action.START, mode2.ordinal(), this.difficulty);
                this.f_96541_.m_91152_(null);
            });
        }
    }

    private void camp(GuiGraphics g) {
        int i;
        this.hero(g, 14, this.t("camp_rank", this.d().m_128451_("Reputation")), this.t("camp_help", new Object[0]));
        CompoundTag camp = this.d().m_128469_("CampState");
        this.row(g, this.t("selected", ModLanguage.user(this.chosen().m_128461_("Name"))), this.t("select_help", new Object[0]), true, () -> this.show(4));
        for (i = 0; i < 3; ++i) {
            int plan = i;
            this.row(g, this.t("blueprint." + i, new Object[0]), this.t("blueprint_help." + i, new Object[0]), camp.m_128471_("ARCHITECT"), () -> this.send(OdysseyNetwork.Action.JOB, 1, plan));
        }
        this.row(g, this.t("stop_job", new Object[0]), "", this.selected() != null, () -> this.send(OdysseyNetwork.Action.JOB, 0, 0));
        this.row(g, this.t("garden_assign", new Object[0]), this.t("garden_help", new Object[0]), camp.m_128471_("GARDEN"), () -> this.send(OdysseyNetwork.Action.JOB, 2, 0));
        this.row(g, this.t("home_assign", new Object[0]), this.t("home_help", new Object[0]), camp.m_128471_("BENCH"), () -> this.send(OdysseyNetwork.Action.JOB, 3, 0));
        for (i = 0; i < 8; ++i) {
            int recipe = i;
            this.row(g, this.t("dish." + i, new Object[0]), this.t("dish_help." + i, new Object[0]), camp.m_128471_("KITCHEN"), () -> this.send(OdysseyNetwork.Action.COOK, recipe, 0));
        }
        for (i = 0; i < 4; ++i) {
            int item = i;
            this.row(g, this.t("furniture." + i, new Object[0]), this.t("furniture_help", new Object[0]), camp.m_128471_("WORKSHOP"), () -> this.send(OdysseyNetwork.Action.FURNITURE, item, 0));
        }
        this.row(g, this.t("invite_visitor", new Object[0]), this.t("visitor_help", new Object[0]), camp.m_128471_("VISITORS") && !camp.m_128471_("VisitorActive"), () -> this.act(OdysseyNetwork.Action.VISITOR));
        if (camp.m_128471_("VisitorActive")) {
            this.row(g, this.t("visitor." + camp.m_128451_("VisitorType"), new Object[0]), "", true, null);
            for (i = 0; i < 4; ++i) {
                int offer = i;
                this.row(g, this.t("trade." + i, new Object[0]), this.t("trade_help." + i, new Object[0]), camp.m_128471_("VISITORS") && (i == 3 || (camp.m_128451_("Trades") & 1 << i) == 0), () -> this.send(OdysseyNetwork.Action.TRADE, offer, 0));
            }
        }
        this.row(g, this.t("group_photo", new Object[0]), this.t("group_photo_help", new Object[0]), this.d().m_128471_("Camp"), () -> this.act(OdysseyNetwork.Action.PHOTO));
    }

    private void crew(GuiGraphics g) {
        int i;
        this.hero(g, 1, this.t("crew", new Object[0]), this.t("crew_help", new Object[0]));
        this.search(g);
        ListTag party = this.d().m_128469_("CampState").m_128437_("Party", 10);
        this.row(g, this.t("party_auto", new Object[0]), this.t("party_help", new Object[0]), true, () -> OdysseyNetwork.request(OdysseyNetwork.Action.PARTY, null, 0, 0, 0, ""));
        for (CompoundTag r : this.roster()) {
            if (!this.matches(ModLanguage.user(r.m_128461_("Name")))) continue;
            UUID id = r.m_128342_("Id");
            this.row(g, (id.equals(this.selected()) ? "\u25c6 " : "") + ModLanguage.user(r.m_128461_("Name")), this.t("crew_line", r.m_128451_("Level"), r.m_128451_("Affection")), true, () -> OdysseyNetwork.request(OdysseyNetwork.Action.SELECT, id, 0, 0, 0, ""));
        }
        CompoundTag r = this.chosen();
        if (r.m_128456_()) {
            return;
        }
        boolean inParty = party.stream().anyMatch(v -> ((CompoundTag)v).m_128403_("Id") && ((CompoundTag)v).m_128342_("Id").equals(this.selected()));
        this.row(g, this.t(inParty ? "party_remove" : "party_add", new Object[0]), this.t("party_count", party.size(), 8), true, () -> this.act(OdysseyNetwork.Action.PARTY));
        this.row(g, this.t("personal_stage", r.m_128451_("PersonalStage"), 3), this.t("personal_help." + Math.max(0, Math.min(3, r.m_128451_("PersonalStage"))), new Object[0]), true, null);
        for (i = 0; i < 3; ++i) {
            int choice = i;
            this.row(g, this.t("personal_choice." + i, new Object[0]), "", r.m_128471_("Loaded") && r.m_128451_("PersonalStage") < 3, () -> this.send(OdysseyNetwork.Action.PERSONAL, choice, 0));
        }
        this.row(g, this.t("club_status", this.t("club." + r.m_128451_("Club"), new Object[0]), r.m_128451_("ClubRank")), this.t("club_rank_help", new Object[0]), true, null);
        for (i = -1; i < 3; ++i) {
            int club = i;
            this.row(g, this.t("club." + i, new Object[0]), this.t("club_help", new Object[0]), this.d().m_128469_("CampState").m_128471_("CLUB"), () -> this.send(OdysseyNetwork.Action.CLUB, club, 0));
        }
        for (i = 0; i < 3; ++i) {
            int gesture = i;
            this.row(g, this.t("gesture_name." + i, new Object[0]), "", (r.m_128451_("Gestures") & 1 << i) != 0, () -> this.send(OdysseyNetwork.Action.GESTURE, gesture, 0));
        }
    }

    private void collection(GuiGraphics g) {
        this.hero(g, 5, this.t("collection", new Object[0]), this.t("collection_help", new Object[0]));
        if (this.f_96541_.f_91074_ != null) {
            for (Object stack : this.f_96541_.f_91074_.m_150109_().f_35974_) {
                if (!stack.m_150930_((Item)OdysseyContent.LETTER.get()) || !stack.m_41782_()) continue;
                CompoundTag n = stack.m_41783_();
                if (n.m_128403_("Companion")) {
                    this.row(g, this.t("letter_author", ModLanguage.user(n.m_128461_("Name"))), this.t("letter_ending." + Math.floorMod(n.m_128451_("Ending"), 3), new Object[0]), true, null);
                    continue;
                }
                if (!n.m_128441_("VisitorStory")) continue;
                this.row(g, this.t("visitor_letter", this.t("visitor." + Math.floorMod(n.m_128451_("VisitorStory"), 6), new Object[0])), this.t("visitor_story." + Math.floorMod(n.m_128451_("VisitorStory"), 6), new Object[0]), true, null);
            }
        }
        this.row(g, ClientVigil.t("album", new Object[0]), this.t("group_photo_help", new Object[0]), true, () -> this.f_96541_.m_91152_((Screen)new VigilAlbumScreen()));
        HashSet<String> earned = new HashSet<String>();
        for (Tag value : this.d().m_128437_("Earned", 8)) {
            earned.add(value.m_7916_());
        }
        for (int i = 0; i < 24; ++i) {
            if (i % 6 >= 4) continue;
            int index = i;
            int y = this.cursor;
            this.row(g, (this.d().m_128451_("Artifact") == i ? "\u25c6 " : (earned.contains("artifact." + i) ? "\u2713 " : "\u25c7 ")) + this.t("artifact." + i, new Object[0]), this.t("artifact_help." + i, new Object[0]), earned.contains("artifact." + i), () -> this.send(OdysseyNetwork.Action.ARTIFACT, index, 0));
            if (y < 98 || y + 22 >= this.bottom) continue;
            g.m_280480_(new ItemStack((ItemLike)OdysseyContent.ARTIFACTS.get(i).get()), this.f_96543_ - 48, y + 6);
        }
        this.row(g, this.t("unequip_artifact", new Object[0]), "", true, () -> this.send(OdysseyNetwork.Action.ARTIFACT, -1, 0));
        for (OdysseyCatalog.Mode mode : OdysseyCatalog.Mode.values()) {
            if (mode.retired()) continue;
            this.row(g, this.t("feature." + mode.feature, new Object[0]), this.t("medal", this.d().m_128469_("Medals").m_128451_(mode.id())), true, null);
        }
        this.row(g, this.t("new_cycle", new Object[0]), this.t("new_cycle_help", new Object[0]), this.d().m_128471_("Ended"), () -> this.f_96541_.m_91152_((Screen)new ConfirmScreen(yes -> {
            this.f_96541_.m_91152_((Screen)this);
            if (yes) {
                this.act(OdysseyNetwork.Action.CYCLE);
            }
        }, (Component)ModLanguage.component(this.t("new_cycle", new Object[0])), (Component)ModLanguage.component(this.t("new_cycle_help", new Object[0])))));
    }

    private void builder(GuiGraphics g) {
        this.hero(g, 6, this.t("builder", new Object[0]), this.t("builder_help", new Object[0]));
        this.row(g, this.t("challenge_code", new Object[0]), (String)(this.editing ? this.code + "_" : this.code), true, () -> {
            this.editing = true;
        });
        this.row(g, this.t("paste", new Object[0]), "", true, () -> {
            this.code = this.f_96541_.f_91068_.m_90876_().strip();
            if (this.code.length() > 2048) {
                this.code = this.code.substring(0, 2048);
            }
        });
        this.row(g, this.t("load_code", new Object[0]), "", !this.code.isBlank(), () -> OdysseyNetwork.request(OdysseyNetwork.Action.IMPORT, this.selected(), 0, 0, 0, this.code));
        CompoundTag c = this.d().m_128469_("CampState");
        this.row(g, this.t("challenge_mode", new Object[0]), "", true, () -> this.configure(this.nextMode(c.m_128451_("CustomMode")), c.m_128454_("CustomSeed"), c.m_128451_("CustomDifficulty"), c.m_128451_("CustomRule")));
        this.row(g, this.t("difficulty", c.m_128451_("CustomDifficulty")), "", true, () -> this.configure(c.m_128451_("CustomMode"), c.m_128454_("CustomSeed"), (c.m_128451_("CustomDifficulty") + 1) % 3, c.m_128451_("CustomRule")));
        this.row(g, this.t("challenge_seed", c.m_128454_("CustomSeed")), this.t("seed_help", new Object[0]), true, () -> this.configure(c.m_128451_("CustomMode"), c.m_128454_("CustomSeed") + 7919L, c.m_128451_("CustomDifficulty"), c.m_128451_("CustomRule")));
        this.row(g, this.t("challenge_rule", new Object[0]), this.t("rule." + c.m_128451_("CustomRule"), new Object[0]), true, () -> this.configure(c.m_128451_("CustomMode"), c.m_128454_("CustomSeed"), c.m_128451_("CustomDifficulty"), (c.m_128451_("CustomRule") + 1) % 4));
        OdysseyCatalog.Mode mode = OdysseyCatalog.Mode.read(c.m_128451_("CustomMode"));
        this.row(g, this.t("configured_challenge", new Object[0]), mode == null ? "" : this.t("feature." + mode.feature, new Object[0]), true, null);
        this.row(g, this.t("copy_code", new Object[0]), "", true, () -> this.f_96541_.f_91068_.m_90911_("SO115:" + c.m_128451_("CustomMode") + ":" + c.m_128454_("CustomSeed") + ":" + c.m_128451_("CustomDifficulty") + ":" + c.m_128451_("CustomRule")));
        this.row(g, this.t("start", new Object[0]), this.t("custom_safe", new Object[0]), this.d().m_128471_("Camp") && this.run().m_128456_(), () -> {
            this.act(OdysseyNetwork.Action.CHALLENGE);
            this.f_96541_.m_91152_(null);
        });
    }

    private void updates(GuiGraphics g) {
        this.hero(g, 7, this.t("release_title", "1.2.0"), this.t("release_help", new Object[0]));
        this.search(g);
        this.row(g, this.j("release_filter", this.installedOnly ? "1.2.0" : this.t("all", new Object[0])), this.j("release_filter_help", new Object[0]), true, () -> {
            this.installedOnly = !this.installedOnly;
            this.scroll = 0;
        });
        if (this.releases == null) {
            this.releases = OdysseyBookCatalog.read();
        }
        int count = 0;
        for (OdysseyBookCatalog.Release release : this.releases) {
            List<OdysseyBookCatalog.Entry> entries;
            if (this.installedOnly && !release.version().equals("1.2.0") || (entries = release.entries().stream().filter(e -> this.matches(Component.m_237115_((String)e.title()).getString() + " " + Component.m_237115_((String)e.help()).getString() + " " + release.version())).toList()).isEmpty()) continue;
            count += entries.size();
            this.row(g, (this.folded.contains(release.version()) ? "\u25b8 " : "\u25be ") + this.t("release_title", release.version()), this.j("release_count", entries.size()), true, () -> {
                if (!this.folded.remove(release.version())) {
                    this.folded.add(release.version());
                }
            });
            if (this.folded.contains(release.version()) && this.query.isBlank()) continue;
            for (OdysseyBookCatalog.Entry entry : entries) {
                String title = ModLanguage.user(Component.m_237115_((String)entry.title()).getString());
                String help = ModLanguage.user(Component.m_237115_((String)entry.help()).getString());
                this.row(g, title, help, true, () -> {
                    if (entry.title().startsWith("expanse.shadowsovereign.")) {
                        RealmNetwork.request(RealmNetwork.Action.OPEN, 6, 0, null);
                    } else if (entry.title().startsWith("mythic.shadowsovereign.")) {
                        ClientMythic.open();
                    } else {
                        this.show(entry.page());
                    }
                });
            }
        }
        if (count == 0) {
            this.row(g, this.j("no_results", new Object[0]), this.j("clear_search", new Object[0]), true, () -> {
                this.query = "";
                this.scroll = 0;
            });
        }
        this.row(g, this.t("old_systems", new Object[0]), this.t("history_help", new Object[0]), true, () -> ClientRoyal.open(13));
    }

    private boolean matches(String value) {
        return this.query.isBlank() || ModLanguage.searchTerms(value).toLowerCase(Locale.ROOT).contains(this.query.toLowerCase(Locale.ROOT));
    }

    private void search(GuiGraphics g) {
        int y = this.cursor;
        int h = this.journal.touch ? 42 : 30;
        this.cursor += h + 7;
        this.button(g, 16, y, this.f_96543_ - 80, h, this.query.isBlank() && !this.searching ? this.t("search", new Object[0]) : ModLanguage.user(this.query + (this.searching ? "_" : "")), true, () -> {
            this.searchScroll = this.scroll;
            this.searching = true;
            this.editing = false;
        });
        this.button(g, this.f_96543_ - 58, y, 32, h, "\u00d7", !this.query.isEmpty(), () -> {
            this.query = "";
            this.searching = false;
        });
    }

    private int nextMode(int current) {
        int next = current;
        do {
            next = (next + 1) % OdysseyCatalog.Mode.values().length;
        } while (OdysseyCatalog.Mode.values()[next].retired());
        return next;
    }

    private void configure(int mode, long seed, int difficulty, int rule) {
        OdysseyNetwork.request(OdysseyNetwork.Action.IMPORT, this.selected(), 0, 0, 0, "SO115:" + mode + ":" + seed + ":" + difficulty + ":" + rule);
    }

    private void input(int action, int value) {
        this.send(OdysseyNetwork.Action.INPUT, action, value);
    }

    private void activity(GuiGraphics g) {
        CompoundTag r = this.run();
        if (r.m_128456_()) {
            this.row(g, this.t("no_activity", new Object[0]), "", true, () -> this.show(2));
            return;
        }
        OdysseyCatalog.Mode mode = OdysseyCatalog.Mode.read(r.m_128451_("Mode"));
        if (mode == null) {
            return;
        }
        this.hero(g, mode.boss() ? mode.ordinal() - 2 : mode.region() + 4, this.t("feature." + mode.feature, new Object[0]), this.t("feature_help." + mode.feature, new Object[0]));
        this.row(g, this.t("activity_numbers", r.m_128451_("Stage"), r.m_128451_("Score"), r.m_128451_("Elapsed") / 20), this.t("activity_help." + mode.id(), new Object[0]), true, null);
        if (!r.m_128471_("Built")) {
            this.row(g, this.t("scene_building", r.m_128451_("Build") * 100 / 131072), "", false, null);
            return;
        }
        if (r.m_128471_("ResumeRequested")) {
            this.row(g, this.t("resuming_party", new Object[0]), "", false, null);
        }
        if (r.m_128451_("Phase") == 0) {
            switch (mode) {
                case MASK_KING: {
                    this.row(g, this.t("boss_mask", this.t("mask." + r.m_128451_("Mask"), new Object[0])), this.t("within_four", new Object[0]), true, null);
                    int mask = r.m_128451_("Mask");
                    if (mask == 0 || mask == 3) {
                        for (int i = 0; i < (mask == 0 ? 3 : 4); ++i) {
                            int value = i;
                            this.row(g, this.t("world_interact", i + 1), this.t("within_four", new Object[0]), true, () -> this.input(0, value));
                        }
                    }
                    if (mask != 2) break;
                    this.row(g, this.t("pay_tokens", r.m_128451_("Paid")), this.t("token_help", new Object[0]), true, () -> this.input(0, 0));
                    break;
                }
                case LIGHTHOUSE: 
                case PARADE: 
                case STATUES: {
                    this.row(g, this.t("automatic_objective", new Object[0]), this.t("feature_help." + mode.feature, new Object[0]), true, null);
                    break;
                }
                case LOOP: 
                case MIRRORS: 
                case ARCHIVIST: 
                case WEAVER: {
                    for (int i = 0; i < (mode == OdysseyCatalog.Mode.WEAVER || mode == OdysseyCatalog.Mode.MASK_KING ? 4 : 3); ++i) {
                        int value = i;
                        this.row(g, this.t("world_interact", i + 1), this.t("within_four", new Object[0]), true, () -> this.input(0, value));
                    }
                    break;
                }
                case MINE: {
                    this.row(g, this.t("ore_progress", r.m_128451_("Ore"), r.m_128451_("Stability")), "", true, null);
                    for (int i = 0; i < 16; ++i) {
                        int ore = i;
                        this.row(g, this.t("mine_ore", i + 1), this.t("within_four", new Object[0]), true, () -> this.input(0, ore));
                    }
                    this.row(g, this.t("leave_mine", new Object[0]), "", true, () -> this.input(1, 0));
                    break;
                }
                case TRAIN: {
                    this.row(g, this.t("board_train", new Object[0]), "", true, () -> this.input(0, 0));
                    this.row(g, this.t("repair_train", new Object[0]), this.t("copper_cost", new Object[0]), r.m_128471_("Repair"), () -> this.input(1, 0));
                    break;
                }
                case COMPANIES: {
                    for (int i = 0; i < 4; ++i) {
                        int order = i;
                        this.row(g, this.t("company_plate", i / 2 + 1, i % 2 + 1), "", true, () -> this.input(0, order));
                    }
                    break;
                }
                case MASQUERADE: {
                    for (int i = 0; i < 4; ++i) {
                        int guest = i;
                        this.row(g, this.t("ask_guest", i + 1), this.t("pumpkin_required", new Object[0]), true, () -> this.input(0, guest));
                        this.row(g, this.t("accuse_guest", i + 1), "", Integer.bitCount(r.m_128451_("Clues")) >= 3, () -> this.input(1, guest));
                    }
                    break;
                }
                case FOOTBALL: 
                case BOWLING: {
                    this.row(g, this.t("kick_ball", new Object[0]), this.t("kick_help", new Object[0]), true, () -> {
                        this.input(0, 0);
                        this.f_96541_.m_91152_(null);
                    });
                    break;
                }
                case POTATO: 
                case PARCEL: {
                    ListTag crew = r.m_128437_("Crew", 10);
                    for (int i = 0; i < crew.size() && i < 8; ++i) {
                        int recipient = i;
                        this.row(g, this.t("pass_named", ModLanguage.user(crew.m_128728_(i).m_128461_("Name"))), this.t("pass_help", new Object[0]), true, () -> this.input(0, recipient));
                    }
                    break;
                }
                case CHAIRS: {
                    for (int i = 0; i < 8; ++i) {
                        int seat = i;
                        this.row(g, this.t("sit_chair", i + 1), this.t("chair_help", new Object[0]), true, () -> this.input(0, seat));
                    }
                    break;
                }
                case TUG: {
                    this.row(g, this.t("pull", new Object[0]), this.t("pull_help", new Object[0]), true, () -> this.input(0, 0));
                    break;
                }
                case PAINT: {
                    this.row(g, this.t("paint_color", this.paintColor), this.t("paint_help", new Object[0]), true, () -> {
                        this.paintColor = (this.paintColor + 1) % 4;
                    });
                    int cell = Math.max(15, Math.min(32, (this.f_96543_ - 64) / 8));
                    int x = (this.f_96543_ - cell * 8) / 2;
                    int y = this.cursor;
                    this.cursor += cell * 8 + 8;
                    int[] pattern = r.m_128465_("Pattern");
                    for (int i = 0; i < 64; ++i) {
                        int tile = i;
                        int c = pattern.length == 64 ? pattern[i] : 0;
                        this.button(g, x + i % 8 * cell, y + i / 8 * cell, cell - 2, cell - 2, Integer.toString(c), true, () -> this.input(0, tile * 4 + this.paintColor));
                    }
                    break;
                }
                case COUNCIL: {
                    for (int i = 0; i < 3; ++i) {
                        int vote = i;
                        this.row(g, this.t("council_vote." + i, new Object[0]), "", true, () -> this.input(0, vote));
                    }
                    break;
                }
                case COLLECTOR: {
                    this.row(g, this.t("pay_tokens", r.m_128451_("Paid")), this.t("token_help", new Object[0]), true, () -> this.input(0, 0));
                    break;
                }
                case BAZAAR: {
                    this.row(g, this.t("auction_bid", r.m_128451_("Bid")), this.t("auction_help", new Object[0]), true, () -> this.input(0, 0));
                    break;
                }
                default: {
                    this.row(g, this.t("interact", new Object[0]), this.t("within_four", new Object[0]), true, () -> this.input(0, 0));
                }
            }
        }
        this.row(g, this.t("back_to_world", new Object[0]), "", true, () -> this.f_96541_.m_91152_(null));
        this.row(g, this.t("pause", new Object[0]), "", r.m_128451_("Phase") == 0, () -> this.act(OdysseyNetwork.Action.PAUSE));
        this.row(g, this.t("resume", new Object[0]), "", r.m_128451_("Phase") == 1, () -> this.act(OdysseyNetwork.Action.RESUME));
        this.row(g, this.t("claim", new Object[0]), "", r.m_128451_("Phase") == 2, () -> this.send(OdysseyNetwork.Action.FINISH, 1, 0));
        this.row(g, this.t("abandon", new Object[0]), this.t("abandon_help", new Object[0]), true, () -> this.send(OdysseyNetwork.Action.FINISH, 0, 0));
    }

    private void lessons(GuiGraphics g) {
        this.hero(g, 0, this.t("lessons", new Object[0]), this.t("lessons_help", new Object[0]));
        for (OdysseyCatalog.Goal goal : OdysseyCatalog.goals()) {
            this.row(g, this.t("goal." + goal.id(), new Object[0]), this.t("goal_help." + goal.id(), new Object[0]), true, () -> this.routeGoal(goal));
        }
    }

    private void recipes(GuiGraphics g) {
        this.hero(g, 3, this.t("recipes", new Object[0]), this.t("recipes_help", new Object[0]));
        this.search(g);
        if (this.f_96541_.f_91073_ == null || this.f_96541_.f_91074_ == null) {
            return;
        }
        if (this.recipeCache == null) {
            this.recipeCache = this.f_96541_.f_91073_.m_7465_().m_44051_().stream().filter(r -> r.m_6671_() == RecipeType.f_44107_ && ForgeRegistries.ITEMS.getKey((Object)r.m_8043_(this.f_96541_.f_91073_.m_9598_()).m_41720_()).m_135827_().equals("shadowsovereign")).sorted(Comparator.comparing(r -> r.m_6423_().toString())).toList();
        }
        StackedContents supply = new StackedContents();
        this.f_96541_.f_91074_.m_150109_().m_36010_(supply);
        long epoch = ClientOdyssey.now() / 10L;
        if (epoch != this.recipeEpoch) {
            this.recipeEpoch = epoch;
            this.recipeSupply.clear();
        }
        int found = 0;
        for (Recipe<?> recipe : this.recipeCache) {
            int n;
            ItemStack result = recipe.m_8043_(this.f_96541_.f_91073_.m_9598_());
            if (!this.matches(result.m_41786_().getString() + " " + String.valueOf(recipe.m_6423_()))) continue;
            ++found;
            boolean ready = this.recipeSupply.computeIfAbsent(recipe.m_6423_(), id -> supply.m_36475_(recipe, null));
            this.row(g, ModLanguage.user(result.m_41786_().getString()), this.j(ready ? "recipe_ready" : "recipe_missing", new Object[0]), true, () -> {
                this.selectedRecipe = this.selectedRecipe.equals(recipe.m_6423_().toString()) ? "" : recipe.m_6423_().toString();
            });
            if (!recipe.m_6423_().toString().equals(this.selectedRecipe)) continue;
            int y = this.cursor;
            int x = 24;
            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shaped = (ShapedRecipe)recipe;
                n = shaped.m_44220_();
            } else {
                n = 3;
            }
            int gw = n;
            this.cursor += 90;
            NonNullList ingredients = recipe.m_7527_();
            for (int i = 0; i < ingredients.size() && i < 9; ++i) {
                Ingredient ingredient = (Ingredient)ingredients.get(i);
                ItemStack[] items = ingredient.m_43908_();
                if (items.length == 0) continue;
                ItemStack item = items[(int)(ClientOdyssey.now() / 40L % (long)items.length)];
                int xx = x + i % gw * 26;
                int yy = y + i / gw * 26;
                int have = this.f_96541_.f_91074_.m_150109_().f_35974_.stream().filter(arg_0 -> ((Ingredient)ingredient).test(arg_0)).mapToInt(ItemStack::m_41613_).sum();
                if (yy < 98 || yy + 24 > this.bottom) continue;
                g.m_280480_(item, xx, yy);
                this.text(g, Integer.toString(Math.min(999, have)), xx + 13, yy + 15, have > 0 ? -8134219 : -876127);
            }
            if (y >= 98 && y + 20 <= this.bottom) {
                this.text(g, "\u2192", x + 89, y + 7, -1981295);
                g.m_280480_(result, x + 111, y + 3);
            }
            this.row(g, this.j("ingredient_counts", new Object[0]), this.j("ingredient_counts_help", new Object[0]), true, null);
        }
        this.row(g, this.q("Matching objectives: ", "\u0627\u0644\u0645\u0647\u0645\u0627\u062a \u0627\u0644\u0645\u0637\u0627\u0628\u0642\u0629: ") + found, "", true, null);
        if (found == 0) {
            this.row(g, this.j("no_results", new Object[0]), this.j("clear_search", new Object[0]), true, () -> {
                this.query = "";
                this.scroll = 0;
            });
        }
    }

    private void drawDrawer(GuiGraphics g) {
        switch (this.drawer) {
            case INDEX: {
                this.hero(g, 0, this.j("index_title", new Object[0]), this.j("index_help", new Object[0]));
                this.search(g);
                int found = 0;
                for (int i = 0; i < PAGES.length; ++i) {
                    int target = i;
                    if (!this.matches(this.t(PAGES[i], new Object[0]) + " " + this.j("section." + i, new Object[0]))) continue;
                    ++found;
                    this.row(g, this.t(PAGES[i], new Object[0]), this.j("section." + i, new Object[0]), true, () -> this.show(target));
                }
                if (found != 0) break;
                this.row(g, this.j("no_results", new Object[0]), this.j("clear_search", new Object[0]), true, () -> {
                    this.query = "";
                    this.scroll = 0;
                });
                break;
            }
            case QUICK: {
                this.hero(g, 1, this.j("quick_title", new Object[0]), this.j("quick_help", new Object[0]));
                this.row(g, this.t("army_key", ClientArmy.ORIGINAL.m_90863_().getString()), this.j("quick_army", new Object[0]), true, ClientArmy::openOriginal);
                this.row(g, ClientMythic.t("title", new Object[0]), this.j("quick_mythic", new Object[0]), true, ClientMythic::open);
                this.row(g, this.j("companies", new Object[0]), this.j("quick_companies", new Object[0]), true, () -> ClientRoyal.open(1));
                this.row(g, this.j("equipment", new Object[0]), this.j("quick_equipment", new Object[0]), this.selected() != null, () -> ShadowNetwork.request(ShadowNetwork.Action.INVENTORY, this.selected()));
                this.row(g, this.t("book", new Object[0]), this.j("next_hint", new Object[0]), true, () -> this.show(1));
                this.row(g, this.t("recipes", new Object[0]), this.j("recipe_missing", new Object[0]), true, () -> this.show(10));
                this.row(g, this.t("return_home", new Object[0]), this.t("return_help", new Object[0]), this.d().m_128471_("Realm"), () -> this.act(OdysseyNetwork.Action.RETURN));
                break;
            }
            case MARKS: {
                this.hero(g, 0, this.j("marks_title", new Object[0]), this.j("marks_help", new Object[0]));
                if (this.journal.marks().isEmpty()) {
                    this.row(g, this.j("marks_empty", new Object[0]), this.j("marks_help", new Object[0]), true, null);
                }
                for (int target : this.journal.marks()) {
                    this.row(g, "\u2605 " + this.t(PAGES[target], new Object[0]), this.j("section." + target, new Object[0]), true, () -> this.show(target));
                }
                this.row(g, this.j("mark_toggle", new Object[0]), this.t(PAGES[this.page], new Object[0]), true, () -> this.journal.bookmark(this.page));
                break;
            }
            case HELP: {
                this.hero(g, OdysseyTheme.chapter(this.chapter()), this.j("help_title", this.t(PAGES[this.page], new Object[0])), this.j("help_intro", new Object[0]));
                this.row(g, this.t(PAGES[this.page], new Object[0]), this.j("section." + this.page, new Object[0]), true, null);
                this.row(g, this.j("controls", new Object[0]), this.j("controls_help", new Object[0]), true, null);
                this.row(g, this.j("saved_progress", new Object[0]), this.j("saved_progress_help", new Object[0]), true, null);
                this.row(g, this.j("discovery_help_title", new Object[0]), this.j("discovery_help", new Object[0]), true, ClientMythic::open);
                this.row(g, this.j("settings_link", new Object[0]), this.j("setting_help.0", new Object[0]), true, () -> this.show(11));
                break;
            }
        }
        this.row(g, this.j("close_drawer", new Object[0]), this.t(PAGES[this.page], new Object[0]), true, () -> this.drawer(this.drawer));
    }

    private void settings(GuiGraphics g) {
        for (int i = 0; i < 3; ++i) {
            int option = i;
            boolean on = i == 0 ? this.journal.touch : (i == 1 ? this.journal.contrast : this.journal.dim);
            this.row(g, this.j("setting." + i, new Object[0]) + " \u00b7 " + this.t(on ? "on" : "off", new Object[0]), this.j("setting_help." + i, new Object[0]), true, () -> {
                this.journal.toggle(option);
                this.scroll = 0;
            });
        }
        this.row(g, this.t("home_preference", new Object[0]), this.t(Boolean.parseBoolean(ClientPreferences.get("odyssey.legacyHome", "false")) ? "old_home" : "new_home", new Object[0]), true, () -> ClientPreferences.set("odyssey.legacyHome", String.valueOf(!Boolean.parseBoolean(ClientPreferences.get("odyssey.legacyHome", "false")))));
        this.row(g, this.t("pranks", new Object[0]), this.t(this.d().m_128471_("Pranks") ? "on" : "off", new Object[0]), true, () -> this.act(OdysseyNetwork.Action.PRANKS));
        this.row(g, this.t("welcomes", new Object[0]), this.t(this.d().m_128471_("Welcomes") ? "on" : "off", new Object[0]), true, () -> this.act(OdysseyNetwork.Action.WELCOMES));
        this.row(g, this.t("motion", new Object[0]), "", true, MenuTheme::toggleMotion);
        this.row(g, this.t("codex", new Object[0]), "", true, () -> ClientRoyal.open(0));
        this.row(g, this.t("lessons", new Object[0]), "", true, () -> this.show(9));
        this.row(g, this.t("restore_book", new Object[0]), "", true, () -> this.act(OdysseyNetwork.Action.RESTORE));
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        this.pending = null;
        this.swipe = false;
        this.downY = y;
        this.oldScroll = this.scroll;
        if (x >= (double)(this.f_96543_ - 22) && y >= 98.0 && y < (double)this.bottom && this.maxScroll > 0) {
            int track = this.bottom - 98;
            int h = Math.max(24, track * track / (track + this.maxScroll));
            int top = 98 + (track - h) * this.scroll / this.maxScroll;
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
        return y >= 98.0 && y < (double)this.bottom;
    }

    private void drag(double y) {
        int track = this.bottom - 98;
        int h = Math.max(24, track * track / (track + this.maxScroll));
        this.scroll = Math.max(0, Math.min(this.maxScroll, (int)((y - 98.0 - this.grab) * (double)this.maxScroll / (double)Math.max(1, track - h))));
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (b != 0) {
            return false;
        }
        if (this.thumb) {
            this.drag(y);
            return true;
        }
        if (this.downY >= 98.0 && this.downY < (double)this.bottom && Math.abs(y - this.downY) > 5.0) {
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
        if (this.pending != null && !this.thumb && !this.swipe && this.pending.hit(x, y)) {
            this.pending.action.run();
        }
        this.pending = null;
        this.swipe = false;
        this.thumb = false;
        return true;
    }

    public boolean m_6050_(double x, double y, double amount) {
        this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll - (int)(amount * 28.0)));
        return true;
    }

    public boolean m_5534_(char ch, int mods) {
        if (this.searching && !Character.isISOControl(ch) && this.query.length() < 64) {
            this.query = this.query + ch;
            this.scroll = this.searchScroll;
            return true;
        }
        if (this.editing && !Character.isISOControl(ch) && this.code.length() < 2048) {
            this.code = this.code + ch;
            return true;
        }
        return super.m_5534_(ch, mods);
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (this.searching) {
            if (key == 256 || key == 257) {
                this.searching = false;
                return true;
            }
            if (key == 259) {
                if (!this.query.isEmpty()) {
                    this.query = this.query.substring(0, this.query.offsetByCodePoints(this.query.length(), -1));
                }
                return true;
            }
            if (OdysseyScreen.m_96630_((int)key)) {
                this.query = this.f_96541_.f_91068_.m_90876_();
                if (this.query.length() > 64) {
                    this.query = this.query.substring(0, 64);
                }
                this.scroll = this.searchScroll;
                return true;
            }
        }
        if (this.editing) {
            if (key == 256 || key == 257) {
                this.editing = false;
                return true;
            }
            if (key == 259) {
                if (!this.code.isEmpty()) {
                    this.code = this.code.substring(0, this.code.offsetByCodePoints(this.code.length(), -1));
                }
                return true;
            }
            if (OdysseyScreen.m_96630_((int)key)) {
                this.code = this.f_96541_.f_91068_.m_90876_();
                if (this.code.length() > 2048) {
                    this.code = this.code.substring(0, 2048);
                }
                return true;
            }
        }
        if (!this.editing && !this.searching && ClientArmy.ORIGINAL.m_90832_(key, scan)) {
            ClientArmy.openOriginal();
            return true;
        }
        if (!this.editing && !this.searching && ClientArmy.OPEN.m_90832_(key, scan)) {
            this.m_7379_();
            return true;
        }
        if (key == 258) {
            if (!this.controls.isEmpty()) {
                this.focus = Math.floorMod(this.focus + (OdysseyScreen.m_96638_() ? -1 : 1), this.controls.size());
            }
            return true;
        }
        if (key == 257 && this.focus >= 0 && this.focus < this.controls.size() && this.controls.get((int)this.focus).enabled) {
            this.controls.get((int)this.focus).action.run();
            return true;
        }
        if (!this.editing && !this.searching) {
            if (key == 256 && this.drawer != Drawer.NONE) {
                this.drawer(this.drawer);
                return true;
            }
            if (key == 268) {
                this.scroll = 0;
                return true;
            }
            if (key == 269) {
                this.scroll = this.maxScroll;
                return true;
            }
            if (key == 267 || key == 266) {
                this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll + (key == 267 ? 1 : -1) * (this.bottom - 98 - 12)));
                return true;
            }
            if (key == 70 && OdysseyScreen.m_96637_()) {
                this.drawer(Drawer.INDEX);
                this.searching = true;
                return true;
            }
        }
        return super.m_7933_(key, scan, mods);
    }

    private static enum Drawer {
        NONE,
        INDEX,
        QUICK,
        MARKS,
        HELP;

    }

    private record Control(int x, int y, int w, int h, String label, String help, boolean enabled, Runnable action) {
        boolean hit(double px, double py) {
            return px >= (double)this.x && px < (double)(this.x + this.w) && py >= (double)this.y && py < (double)(this.y + this.h);
        }
    }
}

