/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.TooltipFlag$Default
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.ascension.AscensionService;
import dev.shadowsovereign.client.AdventureScreen;
import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.AscensionScreen;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.ClientSoulLife;
import dev.shadowsovereign.client.ClientVigil;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.EclipseHudScreen;
import dev.shadowsovereign.client.EclipseScreen;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.JourneyScreen;
import dev.shadowsovereign.client.LegacyScreen;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.client.TouchDeckScreen;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.network.EclipseNetwork;
import dev.shadowsovereign.network.RoyalNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.royal.RoyalRuns;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.royal.RoyalText;
import dev.shadowsovereign.soulforge.CompanionInventory;
import dev.shadowsovereign.soulforge.SoulTier;
import dev.shadowsovereign.soulforge.SoulTrait;
import dev.shadowsovereign.vigil.VigilRuns;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;

public final class RoyalScreen
extends Screen {
    private static final String[] PAGES = new String[]{"home", "teams", "skills", "atlas", "commands", "compare", "supplies", "bestiary", "memories", "activities", "puzzle", "training", "settings", "guide"};
    private final List<Control> controls = new ArrayList<Control>();
    private final LinkedHashSet<UUID> draft = new LinkedHashSet();
    private int page;
    private int team;
    private int emblem;
    private int scroll;
    private int maxScroll;
    private int cursor;
    private int bottom;
    private int focus = -1;
    private int confirmSkill = -1;
    private int filterSquad = -1;
    private boolean hard;
    private boolean searching;
    private boolean naming;
    private boolean thumb;
    private boolean swipe;
    private boolean bodyDrawing;
    private boolean loaded;
    private double downY;
    private double grab;
    private int downScroll;
    private Control pending;
    private String query = "";
    private String name = "";

    public RoyalScreen(int page) {
        super((Component)RoyalText.c("title", new Object[0]));
        this.page = Math.max(0, Math.min(13, page));
        this.team = Math.max(0, Math.min(4, ClientRoyal.data.m_128451_("SelectedTeam")));
    }

    public int currentPage() {
        return this.page;
    }

    public int scrollOffset() {
        return this.scroll;
    }

    private String t(String key, Object ... args) {
        return ClientRoyal.t(key, args);
    }

    private CompoundTag d() {
        return ClientRoyal.data;
    }

    private CompoundTag run() {
        return this.d().m_128469_("Run");
    }

    private CompoundTag teamData() {
        return this.d().m_128437_("Teams", 10).m_128728_(this.team);
    }

    private List<UUID> members(CompoundTag data) {
        return data.m_128437_("Members", 10).stream().map(v -> ((CompoundTag)v).m_128342_("Id")).toList();
    }

    private String teamName(int i) {
        CompoundTag data = this.d().m_128437_("Teams", 10).m_128728_(i);
        return data.m_128461_("Name").isBlank() ? this.t("team_default", i + 1) : ModLanguage.user(data.m_128461_("Name"));
    }

    private void loadTeam() {
        CompoundTag n = this.teamData();
        this.draft.clear();
        this.draft.addAll(this.members(n));
        this.name = n.m_128461_("Name");
        this.emblem = n.m_128451_("Emblem");
        this.loaded = true;
    }

    protected void m_7856_() {
        this.bottom = this.f_96544_ - 27;
        ClientPreferences.set("royal.page", Integer.toString(this.page));
        if (!this.loaded && this.d().m_128437_("Teams", 10).size() == 5) {
            this.loadTeam();
        }
        if (ClientEclipse.selected == null && !ClientEclipse.roster().isEmpty()) {
            ClientEclipse.select(ClientEclipse.roster().get(0).m_128342_("Id"));
        }
    }

    private void send(RoyalNetwork.Action action, int a, int b, int c, String text, List<UUID> ids) {
        RoyalNetwork.request(action, a, b, c, text, ids);
    }

    private void action(RoyalNetwork.Action action, int a) {
        this.send(action, a, 0, 0, "", List.of());
    }

    private void show(int p) {
        this.page = Math.floorMod(p, PAGES.length);
        this.scroll = 0;
        this.query = "";
        this.naming = false;
        this.searching = false;
        this.confirmSkill = -1;
        ClientPreferences.set("royal.page", Integer.toString(this.page));
    }

    private void text(GuiGraphics g, String value, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, value, x, y, color, false);
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String label, boolean enabled, Runnable run) {
        if (this.bodyDrawing && (y < 68 || y + h > this.bottom)) {
            return;
        }
        RoyalTheme.frame(g, x, y, w, h, enabled ? -248831177 : -803924709);
        FantasyIcons.label(g, this.f_96547_, label, x, y, w, h, enabled ? -988710 : -8949379);
        this.controls.add(new Control(x, y, w, h, label, run, enabled));
    }

    private void row(GuiGraphics g, String label, String description, boolean enabled, Runnable action) {
        List lines = description.isBlank() ? List.of() : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(description), this.f_96543_ - 64);
        int h = lines.isEmpty() ? 27 : Math.max(50, 29 + lines.size() * 13);
        int y = this.cursor;
        this.cursor += h + 7;
        if (y + h < 68 || y >= this.bottom) {
            return;
        }
        if (lines.isEmpty()) {
            this.button(g, 16, y, this.f_96543_ - 42, h, label, enabled, action);
            return;
        }
        RoyalTheme.frame(g, 16, y, this.f_96543_ - 42, h, -484632282);
        this.text(g, ModLanguage.fit(this.f_96547_, label, this.f_96543_ - 62), 24, y + 8, enabled ? -1981295 : -7306879);
        int yy = y + 26;
        for (FormattedCharSequence line : lines) {
            ModLanguage.draw(g, this.f_96547_, line, 24, yy, -4937283, false);
            yy += 13;
        }
        if (action != null && y >= 68 && y + 25 <= this.bottom) {
            this.controls.add(new Control(16, y, this.f_96543_ - 42, y + h <= this.bottom ? h : 25, label, action, enabled));
        }
    }

    private void hero(GuiGraphics g, int art, String title, String description) {
        int y = this.cursor;
        int h = 88;
        this.cursor += h + 10;
        if (y + h < 68 || y >= this.bottom) {
            return;
        }
        RoyalTheme.art(g, art, 16, y, this.f_96543_ - 42, h);
        g.m_280024_(16, y + 25, this.f_96543_ - 26, y + h, 1053210, -267382246);
        this.text(g, ModLanguage.fit(this.f_96547_, title, this.f_96543_ - 66), 26, y + h - 30, -1981295);
        this.text(g, ModLanguage.fit(this.f_96547_, description, this.f_96543_ - 66), 26, y + h - 15, -988710);
    }

    private void search(GuiGraphics g) {
        int y = this.cursor;
        this.cursor += 33;
        this.button(g, 16, y, this.f_96543_ - 42, 26, this.query.isBlank() && !this.searching ? this.t("search", new Object[0]) : ModLanguage.user(this.query + "_"), true, () -> {
            this.searching = true;
            this.naming = false;
        });
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        this.controls.clear();
        this.bottom = this.f_96544_ - 27;
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, (new int[]{0, 1, 2, 3, 3, 2, 1, 7, 7, 4, 6, 4, 2, 3})[this.page]);
        this.text(g, this.t("title", new Object[0]), 17, 15, -1981295);
        this.button(g, this.f_96543_ - 155, 10, 116, 24, ModLanguage.arabic() ? "\u0627\u0644\u0639\u0631\u0628\u064a\u0629 / English" : "English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", true, ModLanguage::toggle);
        this.button(g, this.f_96543_ - 34, 10, 23, 24, "\u00d7", true, () -> ((RoyalScreen)this).m_7379_());
        this.button(g, 16, 38, 26, 24, "<", true, () -> this.show(this.page - 1));
        this.button(g, 48, 38, this.f_96543_ - 282, 24, this.t(PAGES[this.page], new Object[0]), true, () -> this.show(0));
        this.button(g, this.f_96543_ - 230, 38, 76, 24, ClientOdyssey.t("book", new Object[0]), true, () -> ClientOdyssey.open(1));
        this.button(g, this.f_96543_ - 62, 38, 24, 24, ">", true, () -> this.show(this.page + 1));
        this.button(g, this.f_96543_ - 34, 38, 24, 24, "\u2302", true, () -> this.show(0));
        this.button(g, this.f_96543_ - 148, 38, 80, 24, ClientVigil.t("army_short", new Object[0]), true, ClientArmy::openOriginal);
        g.m_280588_(12, 68, this.f_96543_ - 22, this.bottom);
        this.bodyDrawing = true;
        this.cursor = 72 - this.scroll;
        if (!this.loaded && this.d().m_128437_("Teams", 10).size() == 5) {
            this.loadTeam();
        }
        if (this.d().m_128437_("Teams", 10).size() != 5) {
            this.hero(g, 0, this.t("welcome", new Object[0]), this.t("loading", new Object[0]));
        } else {
            switch (this.page) {
                case 0: {
                    this.home(g);
                    break;
                }
                case 1: {
                    this.teams(g);
                    break;
                }
                case 2: {
                    this.skills(g);
                    break;
                }
                case 3: {
                    this.atlas(g);
                    break;
                }
                case 4: {
                    this.commands(g);
                    break;
                }
                case 5: {
                    this.compare(g);
                    break;
                }
                case 6: {
                    this.supplies(g);
                    break;
                }
                case 7: {
                    this.bestiary(g);
                    break;
                }
                case 8: {
                    this.memories(g);
                    break;
                }
                case 9: {
                    this.activities(g);
                    break;
                }
                case 10: {
                    this.puzzle(g);
                    break;
                }
                case 11: {
                    this.training(g);
                    break;
                }
                case 12: {
                    this.settings(g);
                    break;
                }
                case 13: {
                    this.guide(g);
                }
            }
        }
        this.bodyDrawing = false;
        g.m_280618_();
        this.maxScroll = Math.max(0, this.cursor + this.scroll - (this.bottom - 4));
        this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll));
        if (this.maxScroll > 0) {
            int track = this.bottom - 68;
            int th = Math.max(24, track * track / (track + this.maxScroll));
            int at = 68 + (track - th) * this.scroll / this.maxScroll;
            g.m_280509_(this.f_96543_ - 18, 68, this.f_96543_ - 7, this.bottom, -14606806);
            g.m_280509_(this.f_96543_ - 17, at, this.f_96543_ - 8, at + th, -3954824);
        }
        this.text(g, ModLanguage.fit(this.f_96547_, this.d().m_128441_("Chance") ? this.t("footer", this.d().m_128451_("Chance"), Math.round(ClientEclipse.data.m_128457_("Energy")), this.teamName(this.team)) : this.t("loading", new Object[0]), this.f_96543_ - 32), 16, this.f_96544_ - 18, -4937283);
        if (this.page == 0) {
            for (Control c : this.controls) {
                if (!c.label.matches("[1-5]") || !c.hit(mx, my)) continue;
                g.m_280557_(this.f_96547_, (Component)ModLanguage.component(this.teamName(Integer.parseInt(c.label) - 1)), mx, my);
            }
        }
        if (this.focus >= 0 && this.focus < this.controls.size()) {
            Control c = this.controls.get(this.focus);
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 2, -1981295);
        }
        super.m_88315_(g, mx, my, partial);
    }

    private void home(GuiGraphics g) {
        int heroY = this.cursor;
        int heroHeight = this.f_96544_ < 300 ? 48 : 78;
        RoyalTheme.art(g, 0, 16, heroY, this.f_96543_ - 42, heroHeight);
        g.m_280024_(16, heroY, this.f_96543_ - 26, heroY + heroHeight, 856691226, -586149350);
        this.text(g, ModLanguage.fit(this.f_96547_, this.t("welcome", new Object[0]), this.f_96543_ - 100), 25, heroY + heroHeight - 20, -1981295);
        this.crest(g, this.teamData().m_128451_("Emblem"), this.f_96543_ - 74, heroY + 5, 36);
        CompoundTag selected = ClientEclipse.chosen();
        if (!selected.m_128456_()) {
            MenuTheme.portrait(g, selected.m_128461_("Type"), selected.m_128403_("Entity") ? selected.m_128342_("Entity") : null, this.f_96543_ - 113, heroY + heroHeight - 2, Math.max(13, heroHeight / 3));
        }
        this.cursor += heroHeight + 8;
        int y = this.cursor;
        int w = (this.f_96543_ - 44) / 5;
        this.cursor += 34;
        for (int i = 0; i < 5; ++i) {
            int index = i;
            this.button(g, 16 + i * w, y, w - 3, 27, Integer.toString(i + 1), true, () -> this.action(RoyalNetwork.Action.DEPLOY, index));
            if (y < 68 || y + 27 > this.bottom) continue;
            this.crest(g, this.d().m_128437_("Teams", 10).m_128728_(i).m_128451_("Emblem"), 18 + i * w, y + 4, 19);
        }
        this.row(g, ClientOdyssey.t("title", new Object[0]), "", true, () -> ClientOdyssey.open(0));
        this.row(g, ClientSoulLife.t("title", new Object[0]), "", true, () -> ClientSoulLife.open(0));
        this.row(g, ClientVigil.t("title", new Object[0]), "", true, () -> ClientVigil.open(0));
        String[] labels = new String[]{"teams", "skills", "atlas", "commands", "supplies", "bestiary", "activities", "memories"};
        int[] pages = new int[]{1, 2, 3, 4, 6, 7, 9, 8};
        int cardHeight = this.f_96544_ < 300 ? 32 : 38;
        int cardStride = cardHeight + 6;
        for (int i = 0; i < labels.length; ++i) {
            int target = pages[i];
            int xx = 16 + i % 2 * (this.f_96543_ - 36) / 2;
            int yy = this.cursor + i / 2 * cardStride;
            int ww = (this.f_96543_ - 48) / 2;
            if (yy < 68 || yy + cardHeight > this.bottom) continue;
            RoyalTheme.art(g, i, xx, yy, ww, cardHeight);
            g.m_280509_(xx, yy, xx + ww, yy + cardHeight, -1726935783);
            this.button(g, xx, yy, ww, cardHeight, this.t(labels[i], new Object[0]), true, () -> this.show(target));
            RoyalTheme.icon(g, i, xx + 4, yy + 6, 25);
        }
        this.cursor += 4 * cardStride;
        for (Tag tag : this.d().m_128437_("Notices", 10)) {
            CompoundTag notice = (CompoundTag)tag;
            MutableComponent component = Component.Serializer.m_130701_((String)notice.m_128461_("Text"));
            if (component == null) continue;
            this.row(g, component.getString(), this.t("notice_open", new Object[0]), true, () -> this.show(notice.m_128451_("Page")));
        }
        this.row(g, this.t("old_citadel", new Object[0]), this.t("old_citadel_help", new Object[0]), true, () -> this.f_96541_.m_91152_((Screen)new EclipseScreen(0)));
        this.row(g, this.t("original_army", new Object[0]), "", true, () -> this.f_96541_.m_91152_((Screen)new ArmyScreen(ClientEclipse.selected)));
        this.row(g, this.t("settings", new Object[0]), "", true, () -> this.show(12));
        this.row(g, this.t("guide", new Object[0]), "", true, () -> this.show(13));
    }

    private void crest(GuiGraphics g, int emblem, int x, int y, int size) {
        RoyalTheme.icon(g, (new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})[Math.floorMod(emblem, 12)], x, y, size);
    }

    private void teamPicker(GuiGraphics g) {
        int y = this.cursor;
        this.cursor += 34;
        int w = (this.f_96543_ - 44) / 5;
        for (int i = 0; i < 5; ++i) {
            int target = i;
            this.button(g, 16 + i * w, y, w - 3, 26, (i == this.team ? "\u25c6 " : "") + (i + 1), true, () -> {
                this.team = target;
                this.loadTeam();
                this.action(RoyalNetwork.Action.SELECT_TEAM, target);
            });
        }
    }

    private void teams(GuiGraphics g) {
        this.teamPicker(g);
        int crestY = this.cursor;
        this.cursor += 48;
        if (crestY >= 68 && crestY + 40 <= this.bottom) {
            this.crest(g, this.emblem, 19, crestY, 40);
            this.text(g, ModLanguage.fit(this.f_96547_, this.teamName(this.team), this.f_96543_ - 107), 68, crestY + 15, -1981295);
        }
        this.row(g, this.teamName(this.team), this.t("team_help", this.draft.size()), true, () -> {
            this.naming = true;
            this.searching = false;
        });
        if (this.naming) {
            this.row(g, this.t("name", this.name.isBlank() ? this.t("team_default", this.team + 1) : ModLanguage.user(this.name)), "", true, () -> {
                this.naming = true;
            });
        }
        this.row(g, this.t("save_team", new Object[0]), "", true, () -> {
            this.send(RoyalNetwork.Action.TEAM_SAVE, this.team, this.emblem, 0, this.name, List.copyOf(this.draft));
            this.naming = false;
        });
        this.row(g, this.t("deploy_team", new Object[0]), "", true, () -> this.action(RoyalNetwork.Action.DEPLOY, this.team));
        this.row(g, this.t("recall_team", new Object[0]), "", true, () -> this.action(RoyalNetwork.Action.RECALL, this.team));
        this.row(g, this.t("emblem", this.emblem + 1), "", true, () -> {
            do {
                this.emblem = (this.emblem + 1) % 12;
            } while ((this.d().m_128451_("Emblems") & 1 << this.emblem) == 0);
        });
        this.row(g, this.t("clear_selection", new Object[0]), "", true, this.draft::clear);
        String[] squads = new String[]{"ALL", "ALPHA", "BETA", "GAMMA", "DELTA"};
        this.row(g, this.t("import_squad", squads[this.filterSquad + 1]), this.t("import_help", new Object[0]), true, () -> {
            this.filterSquad = (this.filterSquad + 2) % 5 - 1;
        });
        this.search(g);
        for (CompoundTag n : ClientEclipse.roster()) {
            if (this.filterSquad >= 0 && !n.m_128461_("Squad").equals(squads[this.filterSquad + 1]) || !this.matches(n.m_128461_("Name") + " " + n.m_128461_("Type"))) continue;
            UUID id = n.m_128342_("Id");
            this.row(g, (this.draft.contains(id) ? "\u2713 " : "+ ") + ModLanguage.user(n.m_128461_("Name")), this.t("member", n.m_128451_("Level"), n.m_128454_("Kills"), n.m_128471_("Active") ? this.t("active", new Object[0]) : this.t("reserve", new Object[0])), this.draft.contains(id) || this.draft.size() < 24, () -> {
                if (!this.draft.remove(id)) {
                    this.draft.add(id);
                }
            });
        }
    }

    private void skills(GuiGraphics g) {
        this.hero(g, 2, this.t("skills", new Object[0]), this.t(this.d().m_128471_("Desk") ? "desk_ready" : "desk_needed", new Object[0]));
        for (RoyalState.Skill skill : RoyalState.Skill.values()) {
            RoyalService.Price cost;
            int level = this.d().m_128469_("Levels").m_128451_(skill.name());
            level = Math.max(1, level);
            String label = this.t("skill." + skill.name().toLowerCase(Locale.ROOT), new Object[0]);
            String string = this.t("skill_desc." + skill.name().toLowerCase(Locale.ROOT), new Object[0]);
            String string2 = this.t("rank", level);
            String string3 = "effective." + skill.name().toLowerCase(Locale.ROOT);
            Object[] objectArray = new Object[1];
            String string4 = skill == RoyalState.Skill.EXTRACT ? "%.0f" : "%.2f";
            Object[] objectArray2 = new Object[1];
            objectArray2[0] = switch (skill) {
                default -> throw new IncompatibleClassChangeError();
                case RoyalState.Skill.EXTRACT -> level;
                case RoyalState.Skill.DASH -> 6.0 + 0.25 * (double)(level - 1);
                case RoyalState.Skill.WELL -> 5.0 + 0.2 * (double)(level - 1);
                case RoyalState.Skill.LANCE -> 6.0 + 0.5 * (double)(level - 1);
                case RoyalState.Skill.PARRY -> 8.0 + 0.5 * (double)(level - 1);
                case RoyalState.Skill.STORM -> 2.0 + 0.1 * (double)(level - 1);
            };
            objectArray[0] = String.format(Locale.ROOT, string4, objectArray2);
            String desc = string + " " + string2 + "\n" + this.t(string3, objectArray);
            if (skill != RoyalState.Skill.EXTRACT && ClientEclipse.data.m_128451_("Wins") < EclipseState.Power.valueOf((String)skill.name()).wins) {
                desc = desc + "\n" + this.t("power_locked", EclipseState.Power.valueOf((String)skill.name()).wins);
            }
            if ((cost = RoyalService.price(level + 1)) != null) {
                desc = desc + "\n" + this.t("price", cost.diamonds(), cost.emeralds(), cost.count(), cost.extra().m_41466_().getString());
            }
            int expected = level;
            boolean unlocked = skill == RoyalState.Skill.EXTRACT || ClientEclipse.data.m_128451_("Wins") >= EclipseState.Power.valueOf((String)skill.name()).wins;
            this.row(g, label, desc, this.d().m_128471_("Desk") && level < 10 && unlocked, () -> {
                this.confirmSkill = skill.ordinal();
            });
            if (this.confirmSkill != skill.ordinal()) continue;
            this.row(g, this.t("confirm_upgrade", label, level + 1), this.t("purchase_help", new Object[0]), this.d().m_128471_("Desk") && level < 10 && unlocked, () -> {
                this.send(RoyalNetwork.Action.UPGRADE, skill.ordinal(), expected, 0, "", List.of());
                this.confirmSkill = -1;
            });
        }
        CompoundTag resources = this.d().m_128469_("Resources");
        this.row(g, this.t("your_materials", new Object[0]), this.t("materials", resources.m_128451_("diamond"), resources.m_128451_("emerald")), true, null);
    }

    private void oldAdventure(AdventureScreen.Page page) {
        AdventureScreen screen = new AdventureScreen(ClientEclipse.selected);
        this.f_96541_.m_91152_((Screen)screen);
        screen.show(page);
    }

    private void oldJourney(JourneyScreen.Page page) {
        JourneyScreen screen = new JourneyScreen(ClientEclipse.selected);
        this.f_96541_.m_91152_((Screen)screen);
        screen.show(page);
    }

    private void oldLegacy(LegacyScreen.Page page) {
        LegacyScreen screen = new LegacyScreen(ClientEclipse.selected);
        this.f_96541_.m_91152_((Screen)screen);
        screen.show(page);
    }

    private List<Destination> destinations() {
        int mode;
        int i;
        ArrayList<Destination> list = new ArrayList<Destination>();
        for (int i2 = 0; i2 < 6; ++i2) {
            list.add(new Destination(ClientEclipse.t("mode." + EclipseRuns.KINDS[i2].name().toLowerCase(Locale.ROOT), new Object[0]), i2 == 3 ? 3 : 0, i2, () -> this.f_96541_.m_91152_((Screen)new EclipseScreen(5)), ClientEclipse.t("mode_desc." + EclipseRuns.KINDS[i2].name().toLowerCase(Locale.ROOT), new Object[0])));
        }
        for (int i2 = 0; i2 < 4; ++i2) {
            list.add(new Destination(this.t("mode." + RoyalRuns.KINDS[i2].name().toLowerCase(Locale.ROOT), new Object[0]), i2 == 2 ? 3 : (i2 == 1 ? 2 : 0), i2 + 4, () -> this.show(9), this.t("mode_desc." + RoyalRuns.KINDS[i2].name().toLowerCase(Locale.ROOT), new Object[0])));
        }
        list.add(new Destination("Living Warfront", 0, 3, () -> ClientWarfront.open(ClientEclipse.selected, 0), this.t("beacon_required", new Object[0])));
        for (AdventureScreen.Page page : AdventureScreen.Page.values()) {
            if (page == AdventureScreen.Page.SANCTUARY || page == AdventureScreen.Page.BESTIARY || page == AdventureScreen.Page.RELICS || page == AdventureScreen.Page.BONDS) continue;
            list.add(new Destination(AdventureService.pretty(page.name()), page == AdventureScreen.Page.TRIALS ? 0 : 1, 3, () -> this.oldAdventure(page), this.t("sanctuary_required", new Object[0])));
        }
        for (Enum enum_ : JourneyScreen.Page.values()) {
            if (enum_ == JourneyScreen.Page.POWERS || enum_ == JourneyScreen.Page.COMPANION_VIEW || enum_ == JourneyScreen.Page.FEATS) continue;
            list.add(new Destination(AdventureService.pretty(enum_.name()), enum_ == JourneyScreen.Page.BOSSES || enum_ == JourneyScreen.Page.RIFTS ? 0 : (enum_ == JourneyScreen.Page.RALLY ? 2 : 1), 4, () -> this.lambda$destinations$32((JourneyScreen.Page)enum_), this.t("sanctuary_required", new Object[0])));
        }
        for (Enum enum_ : new LegacyScreen.Page[]{LegacyScreen.Page.GAUNTLET, LegacyScreen.Page.SIEGE, LegacyScreen.Page.RUNE_VAULT}) {
            list.add(new Destination(AdventureService.pretty(enum_.name()), enum_ == LegacyScreen.Page.RUNE_VAULT ? 3 : 0, 6, () -> this.lambda$destinations$33((LegacyScreen.Page)enum_), this.t("sanctuary_required", new Object[0])));
        }
        for (i = 0; i < 5; ++i) {
            mode = i;
            list.add(new Destination(AscensionService.NAMES[i], i == 0 || i == 4 ? 1 : 0, 4 + i, () -> {
                AscensionScreen screen = new AscensionScreen();
                this.f_96541_.m_91152_((Screen)screen);
                screen.show(mode);
            }, this.t("ascension_unlock", i == 3 ? 5 : (i == 1 || i == 2 ? 3 : 1))));
        }
        for (i = 0; i < 5; ++i) {
            mode = i;
            list.add(new Destination(ClientVigil.t("mode." + VigilRuns.KINDS[i].name().toLowerCase(Locale.ROOT), new Object[0]), 3, i, () -> ClientVigil.open(7), ClientVigil.t("game_requirements", new Object[0])));
        }
        return list;
    }

    private boolean matches(String s) {
        return (ModLanguage.searchTerms(s) + " " + ModLanguage.tr(s)).toLowerCase(Locale.ROOT).contains(this.query.toLowerCase(Locale.ROOT));
    }

    private void atlas(GuiGraphics g) {
        this.search(g);
        for (Destination a : this.destinations()) {
            if (!this.matches(a.title) && !this.matches(a.requirement)) continue;
            this.hero(g, a.tile, ModLanguage.tr(a.title), this.t("category." + a.category, new Object[0]));
            this.row(g, this.t("open", ModLanguage.tr(a.title)), a.requirement, true, a.open);
        }
    }

    private void commands(GuiGraphics g) {
        int i;
        this.search(g);
        record Entry(String id, String label, Runnable run) {
        }
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (i = 0; i < PAGES.length; ++i) {
            int p = i;
            entries.add(new Entry("page." + i, this.t(PAGES[i], new Object[0]), () -> this.show(p)));
        }
        for (i = 0; i < 5; ++i) {
            int n = i;
            entries.add(new Entry("team." + i, this.t("deploy_named", this.teamName(i)), () -> this.action(RoyalNetwork.Action.DEPLOY, n)));
        }
        for (EclipseState.Power power : EclipseState.Power.values()) {
            entries.add(new Entry("cast." + power.name(), ClientEclipse.t("power." + power.name().toLowerCase(Locale.ROOT), new Object[0]), () -> {
                EclipseNetwork.request(EclipseNetwork.Action.CAST, power.ordinal(), 0, 0, ClientEclipse.selected, List.of());
                this.f_96541_.m_91152_(null);
            }));
        }
        entries.sort(Comparator.comparing(e -> !Boolean.parseBoolean(ClientPreferences.get("royal.pin." + e.id, "false"))));
        for (Entry e2 : entries) {
            if (!this.matches(e2.label)) continue;
            boolean pin = Boolean.parseBoolean(ClientPreferences.get("royal.pin." + e2.id, "false"));
            this.row(g, (pin ? "\u2605 " : "") + e2.label, "", true, e2.run);
            this.row(g, this.t(pin ? "unpin" : "pin", e2.label), "", true, () -> {
                long count = entries.stream().filter(x -> Boolean.parseBoolean(ClientPreferences.get("royal.pin." + x.id, "false"))).count();
                if (pin || count < 8L) {
                    ClientPreferences.set("royal.pin." + e.id, Boolean.toString(!pin));
                }
            });
        }
    }

    private double modifier(ItemStack stack, EquipmentSlot slot, Attribute attribute) {
        return stack.m_41638_(slot).get((Object)attribute).stream().filter(m -> m.m_22217_() == AttributeModifier.Operation.ADDITION).mapToDouble(AttributeModifier::m_22218_).sum();
    }

    private void compare(GuiGraphics g) {
        CompoundTag r = ClientEclipse.chosen();
        if (r.m_128456_()) {
            this.row(g, this.t("select_companion", new Object[0]), "", true, () -> this.f_96541_.m_91152_((Screen)new EclipseScreen(3)));
            return;
        }
        this.row(g, ModLanguage.user(r.m_128461_("Name")), this.t("comparison_help", new Object[0]), true, () -> ShadowNetwork.request(ShadowNetwork.Action.INVENTORY, ClientEclipse.selected));
        ItemStack held = this.f_96541_.f_91074_.m_21205_();
        EquipmentSlot slot = LivingEntity.m_147233_((ItemStack)held);
        int index = 0;
        for (int i = 0; i < 6; ++i) {
            if (CompanionInventory.SLOTS[i] != slot) continue;
            index = i;
        }
        ItemStack equipped = ItemStack.m_41712_((CompoundTag)this.d().m_128437_("Equipment", 10).m_128728_(index));
        this.row(g, this.t("comparison_current", new Object[0]), equipped.m_41786_().getString() + " \u00b7 " + this.t("durability", Math.max(0, equipped.m_41776_() - equipped.m_41773_()), equipped.m_41776_()), true, null);
        this.row(g, this.t("comparison_new", new Object[0]), held.m_41786_().getString() + " \u00b7 " + this.t("durability", Math.max(0, held.m_41776_() - held.m_41773_()), held.m_41776_()), true, null);
        double damage = this.modifier(held, slot, Attributes.f_22281_) - this.modifier(equipped, slot, Attributes.f_22281_);
        double armor = this.modifier(held, slot, Attributes.f_22284_) - this.modifier(equipped, slot, Attributes.f_22284_);
        this.row(g, this.t("comparison_delta", String.format(Locale.ROOT, "%+.1f", damage)), this.t("attack_attribute", String.format(Locale.ROOT, "%+.1f", damage)) + " \u00b7 " + this.t("armor_attribute", String.format(Locale.ROOT, "%+.1f", armor)), true, null);
        this.row(g, this.t("comparison_current", new Object[0]), "", true, null);
        for (Component line : equipped.m_41651_((Player)this.f_96541_.f_91074_, (TooltipFlag)TooltipFlag.Default.f_256752_)) {
            this.row(g, ModLanguage.user(line.getString()), "", true, null);
        }
        this.row(g, this.t("comparison_new", new Object[0]), "", true, null);
        for (Component line : held.m_41651_((Player)this.f_96541_.f_91074_, (TooltipFlag)TooltipFlag.Default.f_256752_)) {
            this.row(g, ModLanguage.user(line.getString()), "", true, null);
        }
        this.row(g, this.t("real_test", new Object[0]), "", true, () -> this.show(11));
    }

    private void supplies(GuiGraphics g) {
        this.teamPicker(g);
        CompoundTag n = this.teamData();
        int food = n.m_128441_("Food") ? n.m_128451_("Food") : 8;
        int arrows = n.m_128441_("Arrows") ? n.m_128451_("Arrows") : 16;
        this.row(g, this.t("food_target", food), this.t("supply_help", new Object[0]), true, null);
        int y = this.cursor;
        this.cursor += 34;
        this.button(g, 16, y, (this.f_96543_ - 48) / 2, 26, "\u2212 2", true, () -> this.send(RoyalNetwork.Action.POLICY, this.team, Math.max(0, food - 2), arrows, "", List.of()));
        this.button(g, this.f_96543_ / 2, y, (this.f_96543_ - 48) / 2, 26, "+ 2", true, () -> this.send(RoyalNetwork.Action.POLICY, this.team, Math.min(16, food + 2), arrows, "", List.of()));
        this.row(g, this.t("arrow_target", arrows), "", true, null);
        y = this.cursor;
        this.cursor += 34;
        this.button(g, 16, y, (this.f_96543_ - 48) / 2, 26, "\u2212 8", true, () -> this.send(RoyalNetwork.Action.POLICY, this.team, food, Math.max(0, arrows - 8), "", List.of()));
        this.button(g, this.f_96543_ / 2, y, (this.f_96543_ - 48) / 2, 26, "+ 8", true, () -> this.send(RoyalNetwork.Action.POLICY, this.team, food, Math.min(64, arrows + 8), "", List.of()));
        this.row(g, this.t("supply_team", new Object[0]), "", ClientEclipse.data.m_128469_("Camp").m_128471_("QUARTERMASTER"), () -> this.action(RoyalNetwork.Action.SUPPLY, this.team));
        this.row(g, this.t("bags", new Object[0]), this.t("bags_help", new Object[0]), true, () -> this.send(RoyalNetwork.Action.BAGS, this.team, 0, 0, "", List.of()));
        this.row(g, this.t("rally", new Object[0]), this.t("rally_help", new Object[0]), true, () -> this.action(RoyalNetwork.Action.RALLY, this.team));
        this.row(g, this.t("camp", new Object[0]), "", true, () -> this.f_96541_.m_91152_((Screen)new EclipseScreen(7)));
    }

    private String typeName(String id) {
        try {
            EntityType type = (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
            return type == null ? id : type.m_20676_().getString();
        }
        catch (RuntimeException e) {
            return id;
        }
    }

    private void bestiary(GuiGraphics g) {
        this.search(g);
        this.row(g, this.t("wishlist_help", new Object[0]), this.t("bestiary_help", new Object[0]), true, null);
        for (Tag tag : this.d().m_128437_("Hunts", 10)) {
            CompoundTag n = (CompoundTag)tag;
            if (!this.matches(this.typeName(n.m_128461_("Type")) + " " + n.m_128461_("Type"))) continue;
            this.row(g, (n.m_128471_("Wanted") ? "\u2605 " : "") + this.typeName(n.m_128461_("Type")), this.t("hunt_stats", n.m_128454_("Attempts"), n.m_128454_("Successes")), true, () -> this.send(RoyalNetwork.Action.WANTED, 0, 0, 0, n.m_128461_("Type"), List.of()));
        }
    }

    private String biomeName(String id) {
        ResourceLocation key = ResourceLocation.m_135820_((String)id);
        if (key == null) {
            return id;
        }
        String translation = "biome." + key.m_135827_() + "." + key.m_135815_();
        String value = Component.m_237115_((String)translation).getString();
        return value.equals(translation) ? id : value;
    }

    private void memories(GuiGraphics g) {
        this.hero(g, 7, this.t("memories", new Object[0]), this.t("memories_help", new Object[0]));
        for (Tag tag : this.d().m_128437_("Memories", 10)) {
            CompoundTag m = (CompoundTag)tag;
            this.row(g, ModLanguage.user(m.m_128461_("Name")), this.t("memory", m.m_128454_("Day"), this.typeName(m.m_128461_("Type")), ModLanguage.tr(SoulTier.read((String)m.m_128461_((String)"Tier")).label), this.biomeName(m.m_128461_("Biome"))), true, null);
            this.row(g, ModLanguage.tr(SoulTrait.read((String)m.m_128461_((String)"Trait")).label), "", true, null);
        }
    }

    private List<UUID> party() {
        List<UUID> all = this.members(this.teamData());
        return ClientEclipse.roster().stream().filter(n -> n.m_128471_("Active") && (all.isEmpty() || all.contains(n.m_128342_("Id")))).limit(4L).map(n -> n.m_128342_("Id")).toList();
    }

    private void activities(GuiGraphics g) {
        int i;
        if (!this.d().m_128461_("OtherKind").isBlank()) {
            this.row(g, this.t("old_activity", new Object[0]), this.t("finish_activity", new Object[0]), true, () -> this.show(3));
            return;
        }
        CompoundTag n = this.run();
        if (!n.m_128456_()) {
            this.row(g, this.t("mode." + n.m_128461_("Kind").toLowerCase(Locale.ROOT), new Object[0]), this.t("activity_status", n.m_128454_("Elapsed") / 20L, n.m_128451_("Enemies")), true, null);
            if (n.m_128461_("Kind").equals("SEAL_PUZZLE")) {
                this.row(g, this.t("puzzle", new Object[0]), "", true, () -> this.show(10));
            }
            if (n.m_128461_("Kind").equals("TRAINING")) {
                this.row(g, this.t("training", new Object[0]), "", true, () -> this.show(11));
            }
            if (n.m_128461_("Kind").equals("SKILL_COURSE")) {
                this.row(g, this.t("course_stage", n.m_128451_("Stage") + 1), this.t("course_step." + n.m_128451_("Stage"), new Object[0]), true, null);
            }
            if (n.m_128461_("Kind").equals("MIMIC")) {
                this.row(g, this.t("boss_phase", n.m_128451_("BossPhase") + 1), n.m_128441_("CastAt") ? this.t("copy_warning", ClientEclipse.t("power." + n.m_128461_("Copy").toLowerCase(Locale.ROOT), new Object[0])) : this.t("mimic_help", new Object[0]), true, null);
            }
            this.row(g, this.t("pause", new Object[0]), "", n.m_128451_("Phase") == 0, () -> this.action(RoyalNetwork.Action.PAUSE, 0));
            this.row(g, this.t("resume", new Object[0]), "", n.m_128451_("Phase") == 1, () -> this.action(RoyalNetwork.Action.RESUME, 0));
            this.row(g, this.t("claim", new Object[0]), "", n.m_128451_("Phase") == 2, () -> this.action(RoyalNetwork.Action.CLAIM, 0));
            this.row(g, this.t("abandon", new Object[0]), "", true, () -> this.action(RoyalNetwork.Action.ABANDON, 0));
            return;
        }
        this.row(g, this.hard ? this.t("hard", new Object[0]) : this.t("relaxed", new Object[0]), "", true, () -> {
            this.hard = !this.hard;
        });
        for (i = 0; i < 4; ++i) {
            int mode = i;
            this.hero(g, i + 4, this.t("mode." + RoyalRuns.KINDS[i].name().toLowerCase(Locale.ROOT), new Object[0]), this.t("category." + (i == 2 ? 3 : 0), new Object[0]));
            this.row(g, this.t("start", this.t("mode." + RoyalRuns.KINDS[i].name().toLowerCase(Locale.ROOT), new Object[0])), this.t("mode_desc." + RoyalRuns.KINDS[i].name().toLowerCase(Locale.ROOT), new Object[0]), ClientEclipse.data.m_128471_("NearBeacon") && (mode != 1 || ClientEclipse.data.m_128451_("Wins") >= 2 && !this.f_96541_.f_91074_.m_150110_().f_35934_) && (mode != 3 || ClientEclipse.data.m_128451_("Wins") >= 3 && !this.party().isEmpty()), () -> this.send(RoyalNetwork.Action.START, mode, this.hard ? 1 : 0, 0, "", this.party()));
        }
        this.row(g, this.t("risk_title", new Object[0]), this.t("risk_help", new Object[0]), true, null);
        for (i = -1; i < 6; ++i) {
            int rule = i;
            this.row(g, this.t("risk." + i, new Object[0]) + (this.d().m_128451_("Mutator") == i ? " \u2713" : ""), "", true, () -> this.action(RoyalNetwork.Action.MUTATOR, rule));
        }
        this.row(g, this.t("old_expedition", new Object[0]), "", true, () -> this.f_96541_.m_91152_((Screen)new EclipseScreen(5)));
    }

    private void puzzle(GuiGraphics g) {
        CompoundTag n = this.run();
        if (!n.m_128461_("Kind").equals("SEAL_PUZZLE")) {
            this.row(g, this.t("puzzle", new Object[0]), this.t("puzzle_help", new Object[0]), true, () -> this.show(9));
            return;
        }
        this.row(g, this.t("moves", n.m_128451_("Moves")), "", true, null);
        int cell = Math.max(24, Math.min(38, Math.min((this.f_96543_ - 64) / 4, (this.bottom - 68 - 38) / 4)));
        int size = cell * 4;
        int x = (this.f_96543_ - size) / 2;
        int y = this.cursor;
        this.cursor += size + 8;
        for (int i = 0; i < 16; ++i) {
            int xx = x + i % 4 * cell;
            int yy = y + i / 4 * cell;
            int target = i;
            this.button(g, xx, yy, cell - 3, cell - 3, (n.m_128451_("Mask") & 1 << i) != 0 ? "\u25c6" : "\u25c7", n.m_128451_("Phase") == 0, () -> this.send(RoyalNetwork.Action.PUZZLE, target, n.m_128451_("Turn"), 0, "", List.of()));
        }
        this.row(g, this.t("activities", new Object[0]), "", true, () -> this.show(9));
    }

    private void training(GuiGraphics g) {
        CompoundTag n = this.run();
        if (!n.m_128461_("Kind").equals("TRAINING")) {
            this.row(g, this.t("training", new Object[0]), this.t("training_help", new Object[0]), true, () -> this.show(9));
            return;
        }
        long elapsed = Math.max(1L, n.m_128454_("Elapsed"));
        this.row(g, this.t("player_damage", Math.round(n.m_128459_("PlayerDamage"))), this.t("dps", String.format(Locale.ROOT, "%.1f", n.m_128459_("PlayerDamage") * 20.0 / (double)elapsed)), true, null);
        for (CompoundTag r : ClientEclipse.roster()) {
            double damage = n.m_128459_("Damage." + String.valueOf(r.m_128342_("Id")));
            if (!(damage > 0.0)) continue;
            this.row(g, ModLanguage.user(r.m_128461_("Name")), this.t("damage_dps", Math.round(damage), String.format(Locale.ROOT, "%.1f", damage * 20.0 / (double)elapsed)), true, null);
        }
        this.row(g, this.t("activities", new Object[0]), "", true, () -> this.show(9));
    }

    private void settings(GuiGraphics g) {
        this.row(g, this.t("celebration", new Object[0]), "", true, () -> ClientPreferences.set("royal.hideCelebration", Boolean.toString(!Boolean.parseBoolean(ClientPreferences.get("royal.hideCelebration", "false")))));
        this.row(g, this.t("motion", new Object[0]), "", true, MenuTheme::toggleMotion);
        this.row(g, this.t("hud", new Object[0]), "", true, () -> this.f_96541_.m_91152_((Screen)new EclipseHudScreen()));
        this.row(g, this.t("deck", new Object[0]), "", true, () -> this.f_96541_.m_91152_((Screen)new TouchDeckScreen(ClientEclipse.selected)));
        this.row(g, this.t("compare", new Object[0]), "", true, () -> this.show(5));
        this.row(g, this.t("original_army", new Object[0]), "", true, () -> this.f_96541_.m_91152_((Screen)new ArmyScreen(ClientEclipse.selected)));
        this.row(g, this.t("guide", new Object[0]), "", true, () -> this.show(13));
    }

    private void guide(GuiGraphics g) {
        this.search(g);
        for (int i = 1; i <= 18; ++i) {
            int feature = i;
            String label = this.t("feature." + i, new Object[0]);
            String description = this.t("feature_desc." + i, new Object[0]);
            if (!this.matches(label) && !this.matches(description)) continue;
            this.row(g, i + " \u00b7 " + label, description, true, () -> this.show(switch (feature) {
                case 1 -> 2;
                case 2 -> 0;
                case 3 -> 3;
                case 4 -> 4;
                case 5 -> 5;
                case 6, 7, 15 -> 6;
                case 8 -> 12;
                case 9 -> 7;
                case 10 -> 11;
                case 16 -> 8;
                case 17 -> 10;
                default -> 9;
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
        this.downScroll = this.scroll;
        if (x >= (double)(this.f_96543_ - 22) && y >= 68.0 && y < (double)this.bottom && this.maxScroll > 0) {
            int track = this.bottom - 68;
            int th = Math.max(24, track * track / (track + this.maxScroll));
            int top = 68 + (track - th) * this.scroll / this.maxScroll;
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
        return y >= 68.0 && y < (double)this.bottom;
    }

    private void dragThumb(double y) {
        int track = this.bottom - 68;
        int th = Math.max(24, track * track / (track + this.maxScroll));
        this.scroll = Math.max(0, Math.min(this.maxScroll, (int)((y - 68.0 - this.grab) * (double)this.maxScroll / (double)Math.max(1, track - th))));
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (b != 0) {
            return false;
        }
        if (this.thumb) {
            this.dragThumb(y);
            return true;
        }
        if (this.downY >= 68.0 && this.downY < (double)this.bottom && Math.abs(y - this.downY) > 5.0) {
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
        if (this.pending != null && !this.swipe && !this.thumb && this.pending.hit(x, y) && this.pending.action != null) {
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

    public boolean m_5534_(char c, int mods) {
        if (Character.isISOControl(c)) {
            return false;
        }
        if (this.naming && this.name.codePointCount(0, this.name.length()) < 24) {
            this.name = this.name + c;
            return true;
        }
        if (this.searching && this.query.length() < 80) {
            this.query = this.query + c;
            this.scroll = 0;
            return true;
        }
        return false;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (this.naming || this.searching) {
            if (key == 259) {
                if (this.naming && !this.name.isEmpty()) {
                    this.name = this.name.substring(0, this.name.offsetByCodePoints(this.name.length(), -1));
                }
                if (this.searching && !this.query.isEmpty()) {
                    this.query = this.query.substring(0, this.query.offsetByCodePoints(this.query.length(), -1));
                }
                this.scroll = 0;
                return true;
            }
            if (key == 257 || key == 256) {
                this.searching = false;
                this.naming = false;
                return true;
            }
            if (RoyalScreen.m_96630_((int)key)) {
                String add = this.f_96541_.f_91068_.m_90876_().replaceAll("[\\p{Cntrl}]", "");
                if (this.naming) {
                    String value = this.name + add;
                    this.name = value.substring(0, value.offsetByCodePoints(0, Math.min(24, value.codePointCount(0, value.length()))));
                } else {
                    this.query = (this.query + add).substring(0, Math.min(80, this.query.length() + add.length()));
                }
                return true;
            }
        }
        if (!this.naming && !this.searching && ClientArmy.ORIGINAL.m_90832_(key, scan)) {
            ClientArmy.openOriginal();
            return true;
        }
        if (!this.naming && !this.searching && ClientArmy.OPEN.m_90832_(key, scan)) {
            this.m_7379_();
            return true;
        }
        if (key == 258) {
            for (int i = 0; i < this.controls.size(); ++i) {
                this.focus = Math.floorMod(this.focus + (RoyalScreen.m_96638_() ? -1 : 1), Math.max(1, this.controls.size()));
                if (this.controls.get((int)this.focus).enabled) break;
            }
            return true;
        }
        if (key == 257 && this.focus >= 0 && this.focus < this.controls.size() && this.controls.get((int)this.focus).action != null && this.controls.get((int)this.focus).enabled) {
            this.controls.get((int)this.focus).action.run();
            return true;
        }
        if (key == 267 || key == 266) {
            this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll + (key == 267 ? 1 : -1) * (this.bottom - 68)));
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    private /* synthetic */ void lambda$destinations$33(LegacyScreen.Page page) {
        this.oldLegacy(page);
    }

    private /* synthetic */ void lambda$destinations$32(JourneyScreen.Page page) {
        this.oldJourney(page);
    }

    private record Control(int x, int y, int w, int h, String label, Runnable action, boolean enabled) {
        boolean hit(double x, double y) {
            return x >= (double)this.x && x < (double)(this.x + this.w) && y >= (double)this.y && y < (double)(this.y + this.h);
        }
    }

    private record Destination(String title, int category, int tile, Runnable open, String requirement) {
    }
}

