/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.AdventureScreen;
import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.AscensionScreen;
import dev.shadowsovereign.client.ClientAdventure;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.EclipseHudScreen;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.SovereignScreen;
import dev.shadowsovereign.client.TouchDeckScreen;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.eclipse.EclipseText;
import dev.shadowsovereign.network.AdventureNetwork;
import dev.shadowsovereign.network.EclipseNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public final class EclipseScreen
extends Screen {
    private static final String[] PAGES = new String[]{"dashboard", "roster", "radar", "companions", "powers", "missions", "board", "camp", "reports", "guide"};
    private static final int TEXT = -988673;
    private static final int MUTED = -4478259;
    private static final int GOLD = -1522047;
    private final List<Control> controls = new ArrayList<Control>();
    private final LinkedHashSet<UUID> group = new LinkedHashSet();
    private int page;
    private int scroll;
    private int maxScroll;
    private int cursor;
    private int focus = -1;
    private int ticks;
    private int report = -1;
    private int boardUnit = -1;
    private boolean hard;
    private boolean searching;
    private boolean thumb;
    private boolean swipe;
    private boolean contentDrawing;
    private double downY;
    private double grab;
    private int downScroll;
    private Control pending;
    private String query = "";
    private int body = 64;
    private int bottom;

    public EclipseScreen(int page) {
        super((Component)EclipseText.c("title", new Object[0]));
        this.page = Math.max(0, Math.min(9, page));
    }

    public int currentPage() {
        return this.page;
    }

    public int scrollOffset() {
        return this.scroll;
    }

    protected void m_7856_() {
        this.bottom = this.f_96544_ - 28;
        this.scroll = Math.max(0, Math.min(this.scroll, this.maxScroll));
        ClientPreferences.set("eclipse.page", Integer.toString(this.page));
    }

    private CompoundTag d() {
        return ClientEclipse.data;
    }

    private CompoundTag chosen() {
        return ClientEclipse.chosen();
    }

    private CompoundTag run() {
        return this.d().m_128469_("Run");
    }

    private String t(String key, Object ... args) {
        return ClientEclipse.t(key, args);
    }

    private void request(EclipseNetwork.Action a, int value, int extra, int nonce, UUID id, List<UUID> ids) {
        if (this.f_96541_.f_91074_ != null) {
            EclipseNetwork.request(a, value, extra, nonce, id, ids);
        }
    }

    private void send(EclipseNetwork.Action a, int value) {
        this.request(a, value, 0, 0, ClientEclipse.selected, List.of());
    }

    private void show(int next) {
        this.page = Math.floorMod(next, 10);
        this.scroll = 0;
        this.query = "";
        this.searching = false;
        this.report = -1;
        ClientPreferences.set("eclipse.page", Integer.toString(this.page));
    }

    private void text(GuiGraphics g, String s, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, color, false);
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String label, boolean enabled, Runnable action) {
        if (this.contentDrawing && (y < this.body || y + h > this.bottom)) {
            return;
        }
        g.m_280509_(x, y, x + w, y + h, enabled ? -264755122 : -602336225);
        g.m_280509_(x, y, x + w, y + 1, enabled ? -8825201 : -13096125);
        FantasyIcons.label(g, this.f_96547_, label, x, y, w, h, enabled ? -988673 : -8556916);
        this.controls.add(new Control(x, y, w, h, action, enabled, label));
    }

    private void row(GuiGraphics g, String title, String description, Runnable action, boolean enabled) {
        int y = this.cursor;
        List lines = description.isEmpty() ? List.of() : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(description), Math.max(40, this.f_96543_ - 58));
        int height = description.isEmpty() ? 25 : Math.max(48, 27 + lines.size() * 13);
        this.cursor += height + 7;
        if (y + height >= this.body && y < this.bottom) {
            if (!description.isEmpty()) {
                g.m_280509_(12, y, this.f_96543_ - 24, y + height, -719776989);
                this.text(g, ModLanguage.fit(this.f_96547_, title, this.f_96543_ - 48), 20, y + 7, -1522047);
                int yy = y + 24;
                for (FormattedCharSequence line : lines) {
                    if (yy + 10 > this.body && yy < this.bottom) {
                        ModLanguage.draw(g, this.f_96547_, line, 20, yy, -4478259, false);
                    }
                    yy += 13;
                }
            } else {
                this.button(g, 12, y, this.f_96543_ - 36, 25, title, enabled, action);
            }
            if (!description.isEmpty() && action != null && y >= this.body && y + height <= this.bottom) {
                this.controls.add(new Control(12, y, this.f_96543_ - 36, height, action, enabled, title));
            }
        }
    }

    private void allyPicker(GuiGraphics g) {
        List<CompoundTag> list = ClientEclipse.roster();
        if (ClientEclipse.selected == null && !list.isEmpty()) {
            ClientEclipse.selected = list.get(0).m_128342_("Id");
        }
        int y = this.cursor;
        this.cursor += 32;
        this.button(g, 12, y, 30, 24, "<", !list.isEmpty(), () -> this.cycleAlly(-1));
        this.button(g, this.f_96543_ - 60, y, 30, 24, ">", !list.isEmpty(), () -> this.cycleAlly(1));
        if (y >= this.body && y + 24 <= this.bottom) {
            this.text(g, ModLanguage.fit(this.f_96547_, (String)(this.chosen().m_128456_() ? this.t("select_ally", new Object[0]) : ModLanguage.user(this.chosen().m_128461_("Name")) + " \u2022 " + this.t("level", this.chosen().m_128451_("Level"))), this.f_96543_ - 112), 50, y + 7, -988673);
        }
    }

    private void cycleAlly(int delta) {
        List<CompoundTag> list = ClientEclipse.roster();
        if (list.isEmpty()) {
            return;
        }
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            if (!list.get(j).m_128342_("Id").equals(ClientEclipse.selected)) continue;
            i = j;
        }
        ClientEclipse.select(list.get(Math.floorMod(i + delta, list.size())).m_128342_("Id"));
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        if (++this.ticks % 20 == 0) {
            this.send(EclipseNetwork.Action.VIEW, 0);
        }
        this.group.removeIf(id -> ClientArmy.view.entries().stream().noneMatch(e -> e.id().equals(id)));
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        this.controls.clear();
        this.bottom = this.f_96544_ - 28;
        MenuTheme.panel(g, this.f_96543_, this.f_96544_, (new int[]{0, 1, 4, 6, 7, 9, 3, 2, 11, 8})[this.page]);
        g.m_280509_(0, 0, this.f_96543_, 60, -871363815);
        this.text(g, this.t("title", new Object[0]), 12, 9, -1522047);
        this.button(g, this.f_96543_ - 158, 5, 120, 22, ModLanguage.arabic() ? "\u0627\u0644\u0639\u0631\u0628\u064a\u0629 / English" : "English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", true, () -> ModLanguage.toggle());
        this.button(g, this.f_96543_ - 31, 5, 23, 22, "\u00d7", true, () -> ((EclipseScreen)this).m_7379_());
        this.button(g, 12, 32, 26, 23, "<", true, () -> this.show(this.page - 1));
        this.button(g, 44, 32, Math.max(105, this.f_96543_ - 208), 23, this.t(PAGES[this.page], new Object[0]), true, () -> this.show(0));
        this.button(g, this.f_96543_ - 158, 32, 26, 23, ">", true, () -> this.show(this.page + 1));
        this.button(g, this.f_96543_ - 126, 32, 116, 23, this.t("classic_army", new Object[0]), true, () -> this.f_96541_.m_91152_((Screen)new ArmyScreen(ClientEclipse.selected)));
        this.contentDrawing = true;
        g.m_280588_(8, this.body, this.f_96543_ - 21, this.bottom);
        this.cursor = this.body + 4 - this.scroll;
        switch (this.page) {
            case 0: {
                this.dashboard(g);
                break;
            }
            case 1: {
                this.roster(g);
                break;
            }
            case 2: {
                this.radar(g, mx, my);
                break;
            }
            case 3: {
                this.companions(g);
                break;
            }
            case 4: {
                this.powers(g);
                break;
            }
            case 5: {
                this.missions(g);
                break;
            }
            case 6: {
                this.board(g);
                break;
            }
            case 7: {
                this.camp(g);
                break;
            }
            case 8: {
                this.reports(g);
                break;
            }
            case 9: {
                this.guide(g);
            }
        }
        g.m_280618_();
        this.contentDrawing = false;
        this.maxScroll = Math.max(0, this.cursor + this.scroll - (this.bottom - 4));
        this.scroll = Math.max(0, Math.min(this.scroll, this.maxScroll));
        if (this.maxScroll > 0) {
            int h = this.bottom - this.body;
            int thumbHeight = Math.max(24, h * h / (h + this.maxScroll));
            int top = this.body + (h - thumbHeight) * this.scroll / Math.max(1, this.maxScroll);
            g.m_280509_(this.f_96543_ - 18, this.body, this.f_96543_ - 5, this.bottom, -14345937);
            g.m_280509_(this.f_96543_ - 17, top, this.f_96543_ - 6, top + thumbHeight, -4879387);
        }
        if (this.focus >= 0 && this.focus < this.controls.size()) {
            Control c = this.controls.get(this.focus);
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 2, -11393);
        }
        this.text(g, this.t("footer", Math.round(this.d().m_128457_("Energy")), this.d().m_128451_("Marks"), this.d().m_128451_("Wins")), 12, this.f_96544_ - 18, -4478259);
        super.m_88315_(g, mx, my, partial);
    }

    private void dashboard(GuiGraphics g) {
        String[] sections = new String[]{"roster", "companions", "powers", "missions", "camp", "guide"};
        int[] targets = new int[]{1, 3, 4, 5, 7, 9};
        int w = (this.f_96543_ - 44) / 2;
        for (int i = 0; i < 6; ++i) {
            int x = 12 + i % 2 * (w + 8);
            int y = this.cursor + i / 2 * 44;
            if (y < this.body || y + 38 > this.bottom) continue;
            MenuTheme.art(g, 2 + i, x, y, w, 38);
            g.m_280509_(x, y, x + w, y + 38, -1726739679);
            this.button(g, x, y, w, 38, this.t(sections[i], new Object[0]), true, () -> {});
            int at = targets[i];
            this.controls.set(this.controls.size() - 1, new Control(x, y, w, 38, () -> this.show(at), true, this.t(sections[i], new Object[0])));
            g.m_280411_(new ResourceLocation("shadowsovereign", "textures/gui/eclipse_icons.png"), x + 4, y + 3, 24, 24, (float)(i % 8) * 32.0f, 0.0f, 32, 32, 256, 128);
        }
        this.cursor += 140;
        this.allyPicker(g);
        if (!this.chosen().m_128456_()) {
            if (this.cursor >= this.body && this.cursor + 110 < this.bottom) {
                MenuTheme.portrait(g, this.chosen().m_128461_("Type"), null, this.f_96543_ / 2, this.cursor + 105, 35);
            }
            this.cursor += 112;
        }
        this.row(g, this.t("hud_editor", new Object[0]), "", () -> this.f_96541_.m_91152_((Screen)new EclipseHudScreen()), true);
        this.row(g, this.t("radar", new Object[0]), "", () -> this.show(2), true);
        this.row(g, this.t("reports", new Object[0]), "", () -> this.show(8), true);
        this.row(g, this.t("old_content", new Object[0]), this.t("old_content_desc", new Object[0]), null, true);
        this.row(g, "Living Warfront", "", () -> ClientWarfront.open(ClientEclipse.selected, 0), true);
        this.row(g, "Adventures / Legacy / Journey", "", () -> {
            ClientAdventure.send(new AdventureNetwork.Request(AdventureNetwork.Action.VIEW, null, 0, List.of(), ClientEclipse.selected, 0));
            this.f_96541_.m_91152_((Screen)new AdventureScreen(ClientEclipse.selected));
        }, true);
        this.row(g, "Shadow Ascension", "", () -> this.f_96541_.m_91152_((Screen)new AscensionScreen()), true);
        this.row(g, "Sovereign Trails", "", () -> this.f_96541_.m_91152_((Screen)new SovereignScreen(ClientEclipse.selected)), true);
        this.row(g, "Touch Command Deck", "", () -> this.f_96541_.m_91152_((Screen)new TouchDeckScreen(ClientEclipse.selected)), ClientEclipse.selected != null);
        this.row(g, "Motion: " + (MenuTheme.reducedMotion ? "Reduced" : "Full"), "", MenuTheme::toggleMotion, true);
    }

    private void search(GuiGraphics g) {
        int y = this.cursor;
        this.cursor += 32;
        if (y >= this.body && y + 24 <= this.bottom) {
            g.m_280509_(12, y, this.f_96543_ - 27, y + 24, -15593956);
            this.text(g, ModLanguage.fit(this.f_96547_, this.query.isEmpty() && !this.searching ? this.t("search", new Object[0]) : ModLanguage.user(this.query + (this.searching && this.ticks % 20 < 10 ? "_" : "")), this.f_96543_ - 60), 20, y + 7, -988673);
            this.controls.add(new Control(12, y, this.f_96543_ - 39, 24, () -> {
                this.searching = true;
            }, true));
        }
    }

    private void roster(GuiGraphics g) {
        this.search(g);
        int y = this.cursor;
        this.cursor += 32;
        for (int i = 0; i < 5; ++i) {
            int action = i;
            String[] names = new String[]{"summon", "recall", "follow", "stay", "guard"};
            this.button(g, 12 + i * (this.f_96543_ - 40) / 5, y, (this.f_96543_ - 48) / 5, 24, this.t(names[i], new Object[0]), !this.group.isEmpty(), () -> this.request(EclipseNetwork.Action.GROUP, action, 0, 0, null, List.copyOf(this.group)));
        }
        this.row(g, this.t("group_count", this.group.size()), "", () -> this.group.clear(), true);
        for (CompoundTag n : ClientEclipse.roster()) {
            String name = n.m_128461_("Name");
            if (!name.toLowerCase(Locale.ROOT).contains(this.query.toLowerCase(Locale.ROOT)) && !n.m_128461_("Type").toLowerCase(Locale.ROOT).contains(this.query.toLowerCase(Locale.ROOT))) continue;
            int yy = this.cursor;
            this.cursor += 34;
            UUID id = n.m_128342_("Id");
            this.button(g, 12, yy, 26, 27, this.group.contains(id) ? "\u2713" : "+", this.group.contains(id) || this.group.size() < 24, () -> {
                if (!this.group.remove(id)) {
                    this.group.add(id);
                }
            });
            this.button(g, 44, yy, this.f_96543_ - 74, 27, ModLanguage.user(name) + " \u2022 " + this.t("level", n.m_128451_("Level")), true, () -> {
                ClientEclipse.select(id);
                this.show(3);
            });
        }
    }

    private void radar(GuiGraphics g, int mx, int my) {
        int size = Math.min(this.f_96543_ - 54, Math.min(220, Math.max(96, this.bottom - this.body - 8)));
        int y = this.cursor;
        this.cursor += size + 8;
        if (y >= this.body && y + size <= this.bottom) {
            int x = (this.f_96543_ - size) / 2;
            ClientEclipse.radar(g, x, y, size);
            for (CompoundTag n : ClientEclipse.roster()) {
                if (!n.m_128471_("Active") || !(n.m_128459_("X") * n.m_128459_("X") + n.m_128459_("Z") * n.m_128459_("Z") <= 2304.0)) continue;
                int xx = x + size / 2 + (int)(n.m_128459_("X") / 96.0 * (double)size);
                int yy = y + size / 2 + (int)(n.m_128459_("Z") / 96.0 * (double)size);
                this.controls.add(new Control(xx - 9, yy - 9, 18, 18, () -> ClientEclipse.select(n.m_128342_("Id")), true));
            }
        }
        this.row(g, this.t("radar_help", new Object[0]), this.t("radar_description", new Object[0]), null, true);
        for (CompoundTag n : ClientEclipse.roster()) {
            if (!n.m_128471_("Active")) continue;
            this.row(g, ModLanguage.user(n.m_128461_("Name")), this.t("range", Math.round(Math.sqrt(n.m_128459_("X") * n.m_128459_("X") + n.m_128459_("Z") * n.m_128459_("Z")))), () -> ClientEclipse.select(n.m_128342_("Id")), true);
        }
    }

    private void companions(GuiGraphics g) {
        this.allyPicker(g);
        if (this.chosen().m_128456_()) {
            return;
        }
        CompoundTag n = this.chosen();
        this.row(g, this.t("mastery", n.m_128451_("Veteran"), n.m_128454_("Kills")), this.t("mastery_help", new Object[0]), null, true);
        for (EclipseState.Role role : EclipseState.Role.values()) {
            this.row(g, this.t("role." + role.name().toLowerCase(Locale.ROOT), new Object[0]) + (n.m_128461_("Role").equals(role.name()) ? " \u2713" : ""), this.t("role_desc." + role.name().toLowerCase(Locale.ROOT), new Object[0]), () -> this.send(EclipseNetwork.Action.ROLE, role.ordinal()), this.d().m_128471_("Workshop"));
        }
        this.row(g, this.t("talent_points", Integer.bitCount(n.m_128451_("Talents")), n.m_128451_("Points")), "", () -> this.send(EclipseNetwork.Action.TALENT, 9), this.d().m_128471_("Workshop"));
        for (Enum enum_ : EclipseState.Talent.values()) {
            this.row(g, ((n.m_128451_("Talents") & 1 << enum_.ordinal()) != 0 ? "\u2713 " : "+ ") + this.t("talent." + enum_.name().toLowerCase(Locale.ROOT), new Object[0]), this.t("talent_desc." + enum_.name().toLowerCase(Locale.ROOT), new Object[0]), () -> this.lambda$companions$28((EclipseState.Talent)enum_), this.d().m_128471_("Workshop"));
        }
        for (int i = 0; i < 3; ++i) {
            int slot = i;
            int y = this.cursor;
            this.cursor += 32;
            this.button(g, 12, y, (this.f_96543_ - 44) / 2, 25, this.t("gear_save", i + 1), true, () -> this.send(EclipseNetwork.Action.GEAR_SAVE, slot));
            this.button(g, this.f_96543_ / 2, y, (this.f_96543_ - 44) / 2, 25, this.t("gear_apply", i + 1), n.m_128441_("Gear" + i) && this.d().m_128471_("Workshop"), () -> this.send(EclipseNetwork.Action.GEAR_APPLY, slot));
        }
        this.row(g, this.t("backup", new Object[0]), this.t("backup_help", new Object[0]), () -> this.send(EclipseNetwork.Action.BACKUP, 0), true);
        for (CompoundTag r : ClientEclipse.roster()) {
            if (r.m_128342_("Id").equals(ClientEclipse.selected) || r.m_128471_("Active")) continue;
            this.row(g, ModLanguage.user(r.m_128461_("Name")), "", () -> this.request(EclipseNetwork.Action.BACKUP, 0, 0, 0, ClientEclipse.selected, List.of(r.m_128342_("Id"))), true);
        }
        for (int i = 0; i < 6; ++i) {
            int palette = i;
            this.row(g, this.t("palette", i + 1) + ((this.d().m_128451_("Palettes") & 1 << i) != 0 ? "" : " \ud83d\udd12"), "", () -> this.request(EclipseNetwork.Action.WARDROBE, palette, n.m_128451_("Seal"), n.m_128451_("Frame"), ClientEclipse.selected, List.of()), (this.d().m_128451_("Palettes") & 1 << i) != 0);
        }
        this.row(g, this.t("seal", n.m_128451_("Seal")), "", () -> this.request(EclipseNetwork.Action.WARDROBE, n.m_128451_("Palette"), (n.m_128451_("Seal") + 1) % (Math.min(3, this.d().m_128451_("Wins")) + 1), n.m_128451_("Frame"), ClientEclipse.selected, List.of()), true);
        this.row(g, this.t("frame", n.m_128451_("Frame")), "", () -> this.request(EclipseNetwork.Action.WARDROBE, n.m_128451_("Palette"), n.m_128451_("Seal"), (n.m_128451_("Frame") + 1) % (Math.min(2, this.d().m_128451_("Wins") / 2) + 1), ClientEclipse.selected, List.of()), true);
        this.row(g, "Equipment / Soul Forge", "", () -> ShadowNetwork.request(ShadowNetwork.Action.INVENTORY, ClientEclipse.selected), true);
    }

    private void powers(GuiGraphics g) {
        this.row(g, this.t("energy", Math.round(this.d().m_128457_("Energy"))), this.t("energy_help", new Object[0]), null, true);
        for (EclipseState.Power power : EclipseState.Power.values()) {
            long seconds = Math.max(0L, (this.d().m_128469_("Ready").m_128454_(power.name()) - ClientWarfront.now() + 19L) / 20L);
            this.row(g, this.t("power." + power.name().toLowerCase(Locale.ROOT), new Object[0]) + " \u2022 " + this.t("power_stats", power.cost, seconds > 0L ? seconds : (long)(power.cooldown / 20)), this.t("power_desc." + power.name().toLowerCase(Locale.ROOT), new Object[0]), () -> {
                this.send(EclipseNetwork.Action.CAST, power.ordinal());
                this.f_96541_.m_91152_(null);
            }, this.d().m_128451_("Wins") >= power.wins && this.d().m_128457_("Energy") >= (float)power.cost && seconds == 0L);
        }
    }

    private void missions(GuiGraphics g) {
        this.row(g, this.t("party_hint", this.group.size()), this.t("mission_help", new Object[0]), () -> this.show(1), true);
        this.row(g, this.hard ? this.t("challenging", new Object[0]) : this.t("relaxed", new Object[0]), "", () -> {
            this.hard = !this.hard;
        }, true);
        if (this.d().m_128441_("Run")) {
            int choice;
            int i;
            CompoundTag n = this.run();
            this.row(g, this.t("mode." + n.m_128461_("Kind").toLowerCase(Locale.ROOT), new Object[0]), this.t("mission_stats", n.m_128454_("Remaining") / 20L, n.m_128451_("Enemies")), null, true);
            if (n.m_128461_("Kind").equals("BOARD")) {
                this.row(g, this.t("board", new Object[0]), "", () -> this.show(6), true);
            }
            if (n.m_128461_("Kind").equals("REACTOR")) {
                this.row(g, this.t("reactor_status", n.m_128451_("Pressure"), n.m_128451_("Stable")), this.t("reactor_help", new Object[0]), () -> this.send(EclipseNetwork.Action.INTERACT, 0), n.m_128451_("Phase") == 0);
            }
            if (n.m_128461_("Kind").equals("INVESTIGATION")) {
                this.row(g, this.t("clue_status", Integer.bitCount(n.m_128451_("Clues"))), this.t("investigation_help", new Object[0]), () -> this.send(EclipseNetwork.Action.INTERACT, 0), n.m_128451_("Phase") == 0);
                for (int clue = 0; clue < 3; ++clue) {
                    if ((n.m_128451_("Clues") & 1 << clue) == 0) continue;
                    this.row(g, this.t("clue." + clue, n.m_128451_("Evidence" + clue)), "", null, false);
                }
                for (i = 0; i < 3; ++i) {
                    choice = i;
                    this.row(g, this.t("suspect", i + 1), "", () -> this.send(EclipseNetwork.Action.ACCUSE, choice), n.m_128451_("Clues") == 7 && !n.m_128471_("Accused"));
                }
            }
            if (n.m_128471_("Choosing")) {
                for (i = 0; i < 3; ++i) {
                    choice = i;
                    int card = n.m_128451_("Offer" + i);
                    this.row(g, this.t("card." + EclipseState.Card.values()[card].name().toLowerCase(Locale.ROOT), new Object[0]), this.t("card_desc." + EclipseState.Card.values()[card].name().toLowerCase(Locale.ROOT), new Object[0]), () -> this.send(EclipseNetwork.Action.CARD, choice), true);
                }
            }
            this.row(g, this.t("pause", new Object[0]), "", () -> this.send(EclipseNetwork.Action.PAUSE, 0), n.m_128451_("Phase") == 0);
            this.row(g, this.t("resume", new Object[0]), "", () -> this.send(EclipseNetwork.Action.RESUME, 0), n.m_128451_("Phase") == 1);
            this.row(g, this.t("abandon", new Object[0]), "", () -> this.send(EclipseNetwork.Action.ABANDON, 0), true);
            this.row(g, this.t("claim", new Object[0]), "", () -> this.send(EclipseNetwork.Action.CLAIM, 0), n.m_128451_("Phase") == 2);
            return;
        }
        for (int i = 0; i < 6; ++i) {
            int mode = i;
            this.row(g, this.t("mode." + EclipseRuns.KINDS[i].name().toLowerCase(Locale.ROOT), new Object[0]), this.t("mode_desc." + EclipseRuns.KINDS[i].name().toLowerCase(Locale.ROOT), new Object[0]), () -> this.request(EclipseNetwork.Action.START, mode, this.hard ? 1 : 0, 0, null, List.copyOf(this.group)), this.group.size() <= 4 && this.d().m_128471_("NearBeacon"));
        }
    }

    private void board(GuiGraphics g) {
        int i;
        CompoundTag n = this.run();
        if (!n.m_128461_("Kind").equals("BOARD")) {
            this.row(g, this.t("board", new Object[0]), this.t("board_help", new Object[0]), () -> this.show(5), true);
            return;
        }
        int size = Math.min(this.f_96543_ - 54, Math.max(144, this.bottom - this.body - 4));
        int cell = size / 8;
        int sizeReal = cell * 8;
        int x = (this.f_96543_ - sizeReal) / 2;
        int y = this.cursor;
        this.cursor += sizeReal + 8;
        ListTag units = n.m_128437_("Units", 10);
        if (y + sizeReal > this.body && y < this.bottom) {
            int yy;
            int xx;
            for (int cy = 0; cy < 8; ++cy) {
                for (int cx = 0; cx < 8; ++cx) {
                    int index = cy * 8 + cx;
                    xx = x + cx * cell;
                    yy = y + cy * cell;
                    g.m_280509_(xx, yy, xx + cell - 1, yy + cell - 1, (cx + cy) % 2 == 0 ? -12176556 : -14608084);
                    int target = index;
                    if (yy < this.body || yy + cell > this.bottom) continue;
                    this.controls.add(new Control(xx, yy, cell, cell, () -> this.boardCell(target), true, "cell." + target));
                }
            }
            for (i = 0; i < units.size(); ++i) {
                CompoundTag u = units.m_128728_(i);
                if (u.m_128451_("HP") <= 0) continue;
                int at = u.m_128451_("Cell");
                xx = x + at % 8 * cell;
                yy = y + at / 8 * cell;
                g.m_280509_(xx + 2, yy + 2, xx + cell - 3, yy + cell - 3, u.m_128451_("Side") == 0 ? -7970123 : -6666660);
                this.text(g, (i == this.boardUnit ? "\u25b6" : "") + u.m_128451_("HP"), xx + 3, yy + Math.max(1, (cell - 9) / 2), -988673);
            }
        }
        this.row(g, this.t("board_turn", n.m_128451_("Turn") + 1), this.t("board_help", new Object[0]), null, true);
        this.row(g, this.t("end_turn", new Object[0]), "", () -> this.request(EclipseNetwork.Action.BOARD, 0, -1, n.m_128451_("Turn"), null, List.of()), true);
        for (i = 0; i < 4 && i < units.size(); ++i) {
            int unit = i;
            CompoundTag u = units.m_128728_(i);
            this.row(g, this.t("unit", i + 1, this.t("role." + EclipseState.Role.values()[u.m_128451_("Role")].name().toLowerCase(Locale.ROOT), new Object[0]), u.m_128451_("HP")), "", () -> {
                this.boardUnit = unit;
            }, u.m_128451_("HP") > 0);
        }
    }

    private void boardCell(int cell) {
        CompoundTag n = this.run();
        ListTag units = n.m_128437_("Units", 10);
        int at = -1;
        for (int i = 0; i < units.size(); ++i) {
            if (units.m_128728_(i).m_128451_("HP") <= 0 || units.m_128728_(i).m_128451_("Cell") != cell) continue;
            at = i;
        }
        if (this.boardUnit < 0 && at >= 0 && at < 4) {
            this.boardUnit = at;
            return;
        }
        if (this.boardUnit >= 0) {
            this.request(EclipseNetwork.Action.BOARD, cell, this.boardUnit, n.m_128451_("Turn"), null, List.of());
            if (at >= 4 || at >= 0 && at < 4) {
                this.boardUnit = -1;
            }
        }
    }

    private void camp(GuiGraphics g) {
        CompoundTag near = this.d().m_128469_("Camp");
        this.row(g, this.t("camp_help", new Object[0]), this.t("camp_description", new Object[0]), null, true);
        for (EclipseState.Station s : EclipseState.Station.values()) {
            this.row(g, this.t("station." + s.name().toLowerCase(Locale.ROOT), new Object[0]) + (near.m_128471_(s.name()) ? " \u2713" : ""), this.t("station_desc." + s.name().toLowerCase(Locale.ROOT), new Object[0]), s == EclipseState.Station.VAULT ? () -> this.send(EclipseNetwork.Action.VAULT, 0) : null, near.m_128471_(s.name()));
        }
        this.row(g, this.t("supply", new Object[0]), "", () -> this.request(EclipseNetwork.Action.SUPPLY, 0, 0, 0, null, this.selection()), near.m_128471_("QUARTERMASTER"));
        this.row(g, this.t("deposit", new Object[0]), "", () -> this.request(EclipseNetwork.Action.DEPOSIT, 0, 0, 0, null, this.selection()), near.m_128471_("QUARTERMASTER"));
        for (int i = 0; i < 3; ++i) {
            int effect = i;
            this.row(g, this.t("consumable." + i, new Object[0]), this.t("recipe." + i, new Object[0]), () -> this.send(EclipseNetwork.Action.CRAFT, effect), near.m_128471_("ALCHEMY"));
        }
        int[] offers = this.d().m_128465_("Offers");
        for (int i = 0; i < offers.length; ++i) {
            int offer = i;
            this.row(g, this.t("offer", i + 1, this.t("trade." + offers[i], new Object[0])), "", () -> this.send(EclipseNetwork.Action.BUY, offer), near.m_128471_("MERCHANT"));
        }
        ListTag gates = this.d().m_128437_("Gates", 10);
        for (int i = 0; i < gates.size(); ++i) {
            int gate = i;
            this.row(g, this.t("gate", i + 1, BlockPos.m_122022_((long)gates.m_128728_(i).m_128454_("Pos")).m_123344_()), "", () -> {
                this.send(EclipseNetwork.Action.GATE, gate);
                this.f_96541_.m_91152_(null);
            }, near.m_128471_("WAYGATE"));
        }
    }

    private List<UUID> selection() {
        return this.group.isEmpty() && ClientEclipse.selected != null ? List.of(ClientEclipse.selected) : List.copyOf(this.group);
    }

    private String result(String key) {
        return this.t((String)(switch (key) {
            case "victory", "finished", "interrupted" -> "result." + key;
            default -> key;
        }), new Object[0]);
    }

    private void reports(GuiGraphics g) {
        ListTag list = this.d().m_128437_("Reports", 10);
        if (list.isEmpty()) {
            this.row(g, this.t("reports", new Object[0]), this.t("report_help", new Object[0]), null, true);
            return;
        }
        if (this.report >= 0 && this.report < list.size()) {
            CompoundTag r = list.m_128728_(this.report);
            this.row(g, this.t("back", new Object[0]), "", () -> {
                this.report = -1;
            }, true);
            this.row(g, r.m_128461_("Kind"), this.t("report_result", this.result(r.m_128461_("Outcome")), r.m_128454_("Duration") / 20L), null, true);
            for (Tag tag : r.m_128437_("Members", 10)) {
                CompoundTag m = (CompoundTag)tag;
                this.row(g, ModLanguage.user(m.m_128461_("Name")), this.t("report_metric", Math.round(m.m_128459_("Damage")), m.m_128454_("Kills"), Math.round(m.m_128459_("Healing")), m.m_128454_("Arts")), null, true);
            }
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            int index = i;
            CompoundTag r = list.m_128728_(i);
            this.row(g, i + 1 + " \u2022 " + r.m_128461_("Kind"), this.t("report_result", this.result(r.m_128461_("Outcome")), r.m_128454_("Duration") / 20L), () -> {
                this.report = index;
            }, true);
        }
    }

    private void guide(GuiGraphics g) {
        this.search(g);
        int step = ClientEclipse.roster().isEmpty() ? 1 : (ClientEclipse.roster().stream().noneMatch(n -> !n.m_128461_("Role").equals("NONE")) ? 7 : (this.d().m_128451_("Wins") == 0 ? 19 : (this.d().m_128451_("Wins") < 3 ? 25 : 30)));
        this.row(g, this.t("guide_next", this.t("feature." + step + ".name", new Object[0])), this.t("feature." + step + ".desc", new Object[0]), () -> this.show(EclipseScreen.featurePage(step)), true);
        for (int i = 1; i <= 30; ++i) {
            String description;
            int feature = i;
            String name = this.t("feature." + i + ".name", new Object[0]);
            if (!ModLanguage.tr(name + " " + (description = this.t("feature." + i + ".desc", new Object[0]))).toLowerCase(Locale.ROOT).contains(this.query.toLowerCase(Locale.ROOT))) continue;
            this.row(g, i + " \u2022 " + name, description, () -> {
                if (feature == 2) {
                    this.f_96541_.m_91152_((Screen)new EclipseHudScreen());
                } else {
                    this.show(EclipseScreen.featurePage(feature));
                }
            }, true);
        }
    }

    private static int featurePage(int i) {
        int n;
        if (i <= 6) {
            switch (i) {
                case 2: {
                    n = 0;
                    break;
                }
                case 3: {
                    n = 2;
                    break;
                }
                case 4: {
                    n = 1;
                    break;
                }
                case 5: {
                    n = 8;
                    break;
                }
                case 6: {
                    n = 9;
                    break;
                }
                default: {
                    n = 0;
                    break;
                }
            }
        } else {
            n = i <= 12 ? 3 : (i <= 18 ? 4 : (i <= 24 ? 5 : 7));
        }
        return n;
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        this.pending = null;
        this.swipe = false;
        this.downY = y;
        this.downScroll = this.scroll;
        if (x >= (double)(this.f_96543_ - 21) && y >= (double)this.body && y < (double)this.bottom && this.maxScroll > 0) {
            int h = this.bottom - this.body;
            int th = Math.max(24, h * h / (h + this.maxScroll));
            int top = this.body + (h - th) * this.scroll / this.maxScroll;
            this.grab = y >= (double)top && y < (double)(top + th) ? y - (double)top : (double)th / 2.0;
            this.thumb = true;
            this.dragThumb(y);
            return true;
        }
        for (int i = this.controls.size() - 1; i >= 0; --i) {
            Control c = this.controls.get(i);
            if (!c.enabled || !c.hit(x, y)) continue;
            this.pending = c;
            return true;
        }
        return y >= (double)this.body && y < (double)this.bottom;
    }

    private void dragThumb(double y) {
        int h = this.bottom - this.body;
        int th = Math.max(24, h * h / (h + this.maxScroll));
        this.scroll = Math.max(0, Math.min(this.maxScroll, (int)((y - (double)this.body - this.grab) * (double)this.maxScroll / (double)Math.max(1, h - th))));
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (b != 0) {
            return false;
        }
        if (this.thumb) {
            this.dragThumb(y);
            return true;
        }
        if (this.downY >= (double)this.body && this.downY < (double)this.bottom && Math.abs(y - this.downY) > 5.0) {
            this.swipe = true;
            this.scroll = Math.max(0, Math.min(this.maxScroll, this.downScroll + (int)(this.downY - y)));
            return true;
        }
        return false;
    }

    public boolean m_6348_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        if (this.pending != null && !this.thumb && !this.swipe && this.pending.hit(x, y)) {
            this.pending.click.run();
        }
        this.pending = null;
        this.swipe = false;
        this.thumb = false;
        return true;
    }

    public boolean m_6050_(double x, double y, double delta) {
        this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll - (int)(delta * 28.0)));
        return true;
    }

    public boolean m_5534_(char c, int mods) {
        if (this.searching && !Character.isISOControl(c) && this.query.length() < 80) {
            this.query = this.query + c;
            this.scroll = 0;
            return true;
        }
        return false;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (!this.searching && ClientArmy.OPEN.m_90832_(key, scan)) {
            this.m_7379_();
            return true;
        }
        if (this.searching) {
            if (key == 259) {
                if (!this.query.isEmpty()) {
                    this.query = this.query.substring(0, this.query.length() - 1);
                }
                this.scroll = 0;
                return true;
            }
            if (key == 257 || key == 256) {
                this.searching = false;
                return true;
            }
            if (EclipseScreen.m_96630_((int)key)) {
                this.query = (this.query + this.f_96541_.f_91068_.m_90876_()).replaceAll("[\\p{Cntrl}]", "");
                if (this.query.length() > 80) {
                    this.query = this.query.substring(0, 80);
                }
                this.scroll = 0;
                return true;
            }
        }
        if (key == 258) {
            for (int i = 0; i < this.controls.size(); ++i) {
                this.focus = Math.floorMod(this.focus + (EclipseScreen.m_96638_() ? -1 : 1), Math.max(1, this.controls.size()));
                if (this.controls.get((int)this.focus).enabled) break;
            }
            return true;
        }
        if (key == 257 && this.focus >= 0 && this.focus < this.controls.size() && this.controls.get((int)this.focus).enabled && this.controls.get((int)this.focus).click != null) {
            this.controls.get((int)this.focus).click.run();
            return true;
        }
        if (key == 267 || key == 266) {
            this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll + (key == 267 ? 1 : -1) * (this.bottom - this.body)));
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    private /* synthetic */ void lambda$companions$28(EclipseState.Talent talent) {
        this.send(EclipseNetwork.Action.TALENT, talent.ordinal());
    }

    private record Control(int x, int y, int w, int h, Runnable click, boolean enabled, String label) {
        Control(int x, int y, int w, int h, Runnable click, boolean enabled) {
            this(x, y, w, h, click, enabled, "");
        }

        boolean hit(double mx, double my) {
            return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + this.h);
        }
    }
}

