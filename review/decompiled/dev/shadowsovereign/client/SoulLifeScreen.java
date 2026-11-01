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
 *  net.minecraft.world.item.Item
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.ClientSoulLife;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.network.SoulLifeNetwork;
import dev.shadowsovereign.soullife.SoulLifeText;
import java.util.ArrayList;
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
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class SoulLifeScreen
extends Screen {
    private static final String[] PAGES = new String[]{"journal", "care", "play", "friends", "hearth", "memories", "preferences", "discoveries"};
    private final List<Control> controls = new ArrayList<Control>();
    private int page;
    private int scroll;
    private int maxScroll;
    private int cursor;
    private int bottom;
    private int focus = -1;
    private boolean body;
    private boolean thumb;
    private boolean swipe;
    private boolean searching;
    private double downY;
    private double grab;
    private int oldScroll;
    private Control pending;
    private UUID partner;
    private String query = "";

    public SoulLifeScreen(int page) {
        super((Component)SoulLifeText.c("title", new Object[0]));
        this.page = Math.max(0, Math.min(7, page));
    }

    public int currentPage() {
        return this.page;
    }

    public int scrollOffset() {
        return this.scroll;
    }

    private String t(String key, Object ... args) {
        return ClientSoulLife.t(key, args);
    }

    private CompoundTag d() {
        return ClientSoulLife.data;
    }

    private CompoundTag profile() {
        return this.d().m_128469_("Profile");
    }

    private CompoundTag activity() {
        return this.d().m_128469_("Activity");
    }

    private UUID selected() {
        return this.d().m_128403_("Selected") ? this.d().m_128342_("Selected") : null;
    }

    private List<CompoundTag> roster() {
        return this.d().m_128437_("Roster", 10).stream().map(n -> (CompoundTag)n).toList();
    }

    private CompoundTag chosen() {
        return this.roster().stream().filter(n -> n.m_128342_("Id").equals(this.selected())).findFirst().orElse(new CompoundTag());
    }

    private void show(int p) {
        this.page = Math.floorMod(p, 8);
        this.scroll = 0;
        this.searching = false;
    }

    private void send(SoulLifeNetwork.Action action, int x, int y, int nonce) {
        SoulLifeNetwork.request(action, this.selected(), this.partner, x, y, nonce);
    }

    private void act(SoulLifeNetwork.Action action) {
        this.send(action, 0, 0, 0);
    }

    private void text(GuiGraphics g, String text, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, text, x, y, color, false);
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String label, boolean enabled, Runnable action) {
        if (this.body && (y < 68 || y + h > this.bottom)) {
            return;
        }
        RoyalTheme.frame(g, x, y, w, h, enabled ? -232119242 : -552135395);
        FantasyIcons.label(g, this.f_96547_, label, x, y, w, h, enabled ? -988710 : -4937283);
        this.controls.add(new Control(x, y, w, h, label, enabled, action));
    }

    private void row(GuiGraphics g, String label, String desc, boolean enabled, Runnable action) {
        List lines = desc.isBlank() ? List.of() : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(desc), this.f_96543_ - 66);
        int h = lines.isEmpty() ? 28 : Math.max(52, 29 + lines.size() * 13);
        int y = this.cursor;
        this.cursor += h + 7;
        if (y + h < 68 || y >= this.bottom) {
            return;
        }
        RoyalTheme.frame(g, 16, y, this.f_96543_ - 42, h, -383902935);
        this.text(g, ModLanguage.fit(this.f_96547_, label, this.f_96543_ - 62), 24, y + 8, enabled ? -1981295 : -4937283);
        int yy = y + 26;
        for (FormattedCharSequence line : lines) {
            ModLanguage.draw(g, this.f_96547_, line, 24, yy, -988710, false);
            yy += 13;
        }
        if (action != null && y >= 68 && y + 25 <= this.bottom) {
            this.controls.add(new Control(16, y, this.f_96543_ - 42, y + h <= this.bottom ? h : 25, label, enabled, action));
        }
    }

    private boolean available() {
        return this.selected() != null && this.profile().m_128471_("Quiet");
    }

    private String display(String value) {
        return ModLanguage.user(value);
    }

    private String itemName(String id) {
        ResourceLocation key = ResourceLocation.m_135820_((String)id);
        Item item = key == null ? null : (Item)ForgeRegistries.ITEMS.getValue(key);
        return item == null ? id : Component.m_237115_((String)item.m_5524_()).getString();
    }

    private void selection(GuiGraphics g) {
        this.row(g, this.t("selected", this.display(this.chosen().m_128461_("Name"))), this.t("change_companion", new Object[0]), true, () -> this.show(0));
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        this.controls.clear();
        this.bottom = this.f_96544_ - 27;
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, (new int[]{0, 1, 3, 2, 5, 7, 4, 6})[this.page]);
        this.text(g, ModLanguage.fit(this.f_96547_, this.t("title", new Object[0]), this.f_96543_ - 175), 17, 17, -1981295);
        this.button(g, this.f_96543_ - 155, 10, 116, 24, "English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", true, ModLanguage::toggle);
        this.button(g, this.f_96543_ - 34, 10, 23, 24, "\u00d7", true, () -> ((SoulLifeScreen)this).m_7379_());
        this.button(g, 16, 38, 26, 24, "<", true, () -> this.show(this.page - 1));
        this.button(g, 48, 38, this.f_96543_ - 202, 24, this.t(PAGES[this.page], new Object[0]), true, () -> this.show(0));
        this.button(g, this.f_96543_ - 148, 38, 80, 24, this.t("army", ClientArmy.ORIGINAL.m_90863_().getString()), true, ClientArmy::openOriginal);
        this.button(g, this.f_96543_ - 62, 38, 24, 24, ">", true, () -> this.show(this.page + 1));
        this.button(g, this.f_96543_ - 34, 38, 24, 24, "\u2302", true, () -> this.show(0));
        g.m_280588_(12, 68, this.f_96543_ - 22, this.bottom);
        this.body = true;
        this.cursor = 72 - this.scroll;
        switch (this.page) {
            case 0: {
                this.journal(g);
                break;
            }
            case 1: {
                this.care(g);
                break;
            }
            case 2: {
                this.play(g);
                break;
            }
            case 3: {
                this.friends(g);
                break;
            }
            case 4: {
                this.hearth(g);
                break;
            }
            case 5: {
                this.memories(g);
                break;
            }
            case 6: {
                this.preferences(g);
                break;
            }
            case 7: {
                this.discoveries(g);
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
        this.text(g, ModLanguage.fit(this.f_96547_, this.activity().m_128456_() ? this.t("footer", new Object[0]) : this.t("playing", this.t("game." + this.activity().m_128461_("Game").toLowerCase(Locale.ROOT), new Object[0]), Math.max(0L, (this.activity().m_128454_("Until") - ClientSoulLife.now() + 19L) / 20L)), this.f_96543_ - 32), 16, this.f_96544_ - 18, -4937283);
        if (this.focus >= 0 && this.focus < this.controls.size()) {
            Control c = this.controls.get(this.focus);
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 2, -1981295);
        }
        super.m_88315_(g, mx, my, partial);
    }

    private void journal(GuiGraphics g) {
        CompoundTag row = this.chosen();
        if (!row.m_128456_()) {
            int y = this.cursor;
            int h = this.f_96544_ < 300 ? 62 : 84;
            this.cursor += h + 8;
            RoyalTheme.art(g, 1, 16, y, this.f_96543_ - 42, h);
            g.m_280024_(16, y, this.f_96543_ - 26, y + h, 1141902617, -267317222);
            if (y >= 68 && y + h <= this.bottom) {
                MenuTheme.portrait(g, row.m_128461_("Type"), row.m_128403_("Entity") ? row.m_128342_("Entity") : null, this.f_96543_ - 70, y + h - 3, h / 3);
            }
            this.text(g, ModLanguage.fit(this.f_96547_, this.display(row.m_128461_("Name")), this.f_96543_ - 110), 25, y + 14, -1981295);
            this.text(g, ModLanguage.fit(this.f_96547_, this.t("personality." + row.m_128451_("Personality"), new Object[0]) + " \u00b7 " + this.t("mood." + row.m_128461_("Mood").toLowerCase(Locale.ROOT), new Object[0]), this.f_96543_ - 110), 25, y + 32, -988710);
            this.text(g, this.t("affection", row.m_128451_("Affection")), 25, y + 47, -1981295);
        }
        for (int i = 1; i < 7; ++i) {
            int target = i;
            this.row(g, this.t(PAGES[i], new Object[0]), "", this.selected() != null, () -> this.show(target));
        }
        this.row(g, this.t("search", new Object[0]), (String)(this.searching ? this.query + "_" : this.query), true, () -> {
            this.searching = true;
        });
        List<CompoundTag> list = this.roster().stream().filter(n -> this.query.isBlank() || (n.m_128461_("Name") + " " + n.m_128461_("Type")).toLowerCase(Locale.ROOT).contains(this.query.toLowerCase(Locale.ROOT))).toList();
        if (list.isEmpty()) {
            this.row(g, this.t("empty", new Object[0]), this.t("empty_help", new Object[0]), false, null);
        }
        for (CompoundTag n2 : list) {
            UUID id = n2.m_128342_("Id");
            String status = n2.m_128471_("PaidRequired") ? this.t("waiting_grave", new Object[0]) : (n2.m_128471_("Pending") ? this.t("reforming", new Object[0]) : (n2.m_128471_("Loaded") ? this.t("nearby", new Object[0]) : this.t("in_reserve", new Object[0])));
            this.row(g, (id.equals(this.selected()) ? "\u25c6 " : "") + this.display(n2.m_128461_("Name")), this.t("roster_line", this.t("personality." + n2.m_128451_("Personality"), new Object[0]), n2.m_128451_("Affection"), status), true, () -> {
                SoulLifeNetwork.request(SoulLifeNetwork.Action.SELECT, id, null, 0, 0, 0);
                ClientEclipse.select(id);
                this.scroll = 0;
                this.searching = false;
            });
        }
        this.row(g, this.t("discoveries", new Object[0]), this.t("discoveries_help", new Object[0]), true, () -> this.show(7));
        this.row(g, this.t("codex", new Object[0]), "", true, () -> ClientRoyal.open(0));
    }

    private void care(GuiGraphics g) {
        this.selection(g);
        this.row(g, this.t("favorite", this.itemName(this.profile().m_128461_("Favorite"))), this.t("feeding_help", new Object[0]), true, null);
        this.row(g, this.t("pet", new Object[0]), this.t("pet_help", new Object[0]), this.available(), () -> this.act(SoulLifeNetwork.Action.PET));
        this.row(g, this.t("feed", new Object[0]), this.t("feeding_help", new Object[0]), this.available(), () -> this.act(SoulLifeNetwork.Action.FEED));
        this.row(g, this.t("talk", new Object[0]), this.t("talk_help", new Object[0]), this.available(), () -> this.act(SoulLifeNetwork.Action.TALK));
        this.row(g, this.t("praise", new Object[0]), this.t("praise_help", new Object[0]), this.available(), () -> this.act(SoulLifeNetwork.Action.PRAISE));
        this.row(g, this.t("comfort", new Object[0]), this.t("comfort_help", new Object[0]), this.available(), () -> this.act(SoulLifeNetwork.Action.COMFORT));
        this.row(g, this.t("keepsake", this.profile().m_128471_("HasKeepsake") ? this.display(this.profile().m_128461_("KeepsakeName")) : this.t("none", new Object[0])), this.t("keepsake_help", new Object[0]), true, null);
        this.row(g, this.t("give", new Object[0]), "", this.available() && !this.profile().m_128471_("HasKeepsake"), () -> this.act(SoulLifeNetwork.Action.GIVE));
        this.row(g, this.t("take", new Object[0]), "", this.available() && this.profile().m_128471_("HasKeepsake"), () -> this.act(SoulLifeNetwork.Action.TAKE));
        this.row(g, this.t("wish", new Object[0]), this.t("wish." + this.profile().m_128451_("Wish"), new Object[0]) + (String)(this.profile().m_128471_("WishDone") ? " \u00b7 " + this.t("complete", new Object[0]) : ""), true, null);
    }

    private void play(GuiGraphics g) {
        this.selection(g);
        this.row(g, this.t("favorite_game", this.t("game." + this.profile().m_128461_("FavoriteGame").toLowerCase(Locale.ROOT), new Object[0])), "", true, null);
        if (!this.activity().m_128456_()) {
            this.row(g, this.t("playing", this.t("game." + this.activity().m_128461_("Game").toLowerCase(Locale.ROOT), new Object[0]), Math.max(0L, (this.activity().m_128454_("Until") - ClientSoulLife.now() + 19L) / 20L)), this.t("play_safety", new Object[0]), true, null);
            if (this.activity().m_128461_("Game").equals("SPAR")) {
                this.row(g, this.t("practice_hp", this.activity().m_128451_("AHP"), this.activity().m_128451_("BHP")), "", true, null);
            }
            if (this.activity().m_128461_("Game").equals("HIDE")) {
                this.row(g, this.t("clue_button", new Object[0]), "", true, () -> this.act(SoulLifeNetwork.Action.CLUE));
            }
            this.row(g, this.t("stop", new Object[0]), "", true, () -> this.act(SoulLifeNetwork.Action.CANCEL));
        }
        SoulLifeNetwork.Action[] actions = new SoulLifeNetwork.Action[]{SoulLifeNetwork.Action.HIDE, SoulLifeNetwork.Action.TAG, SoulLifeNetwork.Action.DANCE, SoulLifeNetwork.Action.MELODY};
        String[] keys = new String[]{"hide", "tag", "dance", "melody"};
        for (int i = 0; i < keys.length; ++i) {
            SoulLifeNetwork.Action action = actions[i];
            this.row(g, this.t("game." + keys[i], new Object[0]), this.t(keys[i] + "_help", new Object[0]), this.available(), () -> {
                this.act(action);
                if (action == SoulLifeNetwork.Action.FETCH || action == SoulLifeNetwork.Action.HIDE || action == SoulLifeNetwork.Action.TAG || action == SoulLifeNetwork.Action.DANCE) {
                    this.f_96541_.m_91152_(null);
                }
            });
        }
        this.partner(g);
        this.row(g, this.t("game.spar", new Object[0]), this.t("spar_help", new Object[0]), this.available() && this.partner != null && !this.partner.equals(this.selected()), () -> this.act(SoulLifeNetwork.Action.SPAR));
        int[] melody = this.profile().m_128465_("Melody");
        for (int i = 0; i < melody.length; ++i) {
            int index = i;
            this.row(g, this.t("note", i + 1, melody[i] + 1), this.t("note_help", new Object[0]), this.available(), () -> this.send(SoulLifeNetwork.Action.NOTE, index, (melody[index] + 1) % 8, 0));
        }
    }

    private void partner(GuiGraphics g) {
        List<CompoundTag> others = this.roster().stream().filter(n -> !n.m_128342_("Id").equals(this.selected())).toList();
        if (this.partner == null && !others.isEmpty()) {
            this.partner = others.get(0).m_128342_("Id");
        }
        CompoundTag n2 = others.stream().filter(v -> v.m_128342_("Id").equals(this.partner)).findFirst().orElse(new CompoundTag());
        this.row(g, this.t("partner", this.display(n2.m_128461_("Name"))), this.t("partner_help", new Object[0]), !others.isEmpty(), () -> {
            int index = 0;
            for (int i = 0; i < others.size(); ++i) {
                if (!((CompoundTag)others.get(i)).m_128342_("Id").equals(this.partner)) continue;
                index = i + 1;
            }
            this.partner = ((CompoundTag)others.get(index % others.size())).m_128342_("Id");
        });
    }

    private void friends(GuiGraphics g) {
        this.selection(g);
        this.partner(g);
        this.row(g, this.t("introduce", new Object[0]), this.t("friend_help", new Object[0]), this.available() && this.partner != null, () -> this.act(SoulLifeNetwork.Action.LINK));
        for (Tag tag : this.profile().m_128437_("Friends", 10)) {
            CompoundTag n = (CompoundTag)tag;
            this.row(g, this.display(n.m_128461_("Name")), this.t("friend_points", n.m_128451_("Points")), true, () -> {
                this.partner = n.m_128342_("Id");
            });
        }
        this.row(g, this.t("unfriend", new Object[0]), this.t("unfriend_help", new Object[0]), this.partner != null, () -> this.act(SoulLifeNetwork.Action.UNLINK));
        this.row(g, this.t("game.spar", new Object[0]), this.t("spar_help", new Object[0]), this.available() && this.partner != null, () -> this.act(SoulLifeNetwork.Action.SPAR));
    }

    private void hearth(GuiGraphics g) {
        this.selection(g);
        this.row(g, this.t("hearth", new Object[0]), this.t("hearth_help", new Object[0]), true, null);
        this.row(g, this.t(this.d().m_128471_("NearHearth") ? "hearth_ready" : "hearth_needed", new Object[0]), "", true, null);
        this.row(g, this.t("rest", new Object[0]), this.t("rest_help", new Object[0]), this.available() && this.d().m_128471_("NearHearth"), () -> this.act(SoulLifeNetwork.Action.REST));
        this.row(g, this.t("rest_group", new Object[0]), this.t("rest_group_help", new Object[0]), this.available() && this.d().m_128471_("NearHearth"), () -> this.send(SoulLifeNetwork.Action.REST, 1, 0, 0));
        CompoundTag n = this.activity();
        if (n.m_128461_("Game").equals("STORY")) {
            int part = n.m_128451_("Stage");
            this.row(g, this.t("story_title", new Object[0]), this.t("story." + this.profile().m_128451_("Personality") + "." + Math.min(2, part), new Object[0]) + (String)(part > 0 ? " " + this.t("story_branch." + n.m_128451_("Choice"), new Object[0]) : ""), true, null);
            for (int i = 0; i < 3; ++i) {
                int choice = i;
                this.row(g, this.t("choice." + i, new Object[0]), "", true, () -> this.send(SoulLifeNetwork.Action.STORY, choice, 0, part));
            }
        } else {
            this.row(g, this.t("story_title", new Object[0]), this.t("story_help", new Object[0]), this.available() && this.d().m_128471_("NearHearth"), () -> this.send(SoulLifeNetwork.Action.STORY, 0, 0, 0));
        }
        if (!n.m_128456_()) {
            this.row(g, this.t("stop", new Object[0]), "", true, () -> this.act(SoulLifeNetwork.Action.CANCEL));
        }
    }

    private void memories(GuiGraphics g) {
        this.selection(g);
        this.row(g, this.t("recap_title", new Object[0]), this.t("recap", this.display(this.chosen().m_128461_("Name")), this.profile().m_128451_("DayCare"), this.profile().m_128451_("DayGames"), (int)this.profile().m_128459_("DayWalk"), this.profile().m_128451_("DayKills"), this.profile().m_128451_("DayHelps")), true, () -> this.act(SoulLifeNetwork.Action.RECAP));
        this.row(g, this.t("lifetime", new Object[0]), this.t("lifetime_stats", this.profile().m_128451_("Games"), (int)this.profile().m_128459_("Walked"), this.profile().m_128451_("Pets"), this.profile().m_128451_("Meals"), this.profile().m_128451_("Rests")), true, null);
        for (int i = 0; i < 7; ++i) {
            if ((this.profile().m_128451_("Crests") & 1 << i) == 0) continue;
            this.row(g, this.t("crest." + i, new Object[0]), this.t("crest_help." + i, new Object[0]), true, null);
        }
        ListTag entries = this.profile().m_128437_("Memories", 10);
        for (int i = entries.size() - 1; i >= 0; --i) {
            CompoundTag n = entries.m_128728_(i);
            BlockPos pos = BlockPos.m_122022_((long)n.m_128454_("Pos"));
            this.row(g, this.t("memory." + n.m_128461_("Kind"), new Object[0]), this.t("memory_place", n.m_128454_("Time") / 24000L + 1L, pos.m_123341_(), pos.m_123342_(), pos.m_123343_(), n.m_128461_("Dimension")) + (String)(n.m_128461_("Detail").isEmpty() ? "" : " \u00b7 " + this.display(n.m_128461_("Detail"))), true, null);
        }
    }

    private void preferences(GuiGraphics g) {
        this.selection(g);
        this.row(g, this.t("mood_toggle", new Object[0]), this.t(this.profile().m_128471_("ShowMood") ? "on" : "off", new Object[0]), this.selected() != null, () -> this.act(SoulLifeNetwork.Action.MOOD));
        this.row(g, this.t("spacing", new Object[0]), this.t("space." + this.profile().m_128451_("Spacing"), new Object[0]), this.selected() != null, () -> this.send(SoulLifeNetwork.Action.SPACE, (this.profile().m_128451_("Spacing") + 1) % 4, 0, 0));
        this.row(g, this.t("aid_toggle", new Object[0]), this.t("aid_help", new Object[0]) + " \u00b7 " + this.t(this.profile().m_128471_("FirstAid") ? "on" : "off", new Object[0]), this.selected() != null, () -> this.act(SoulLifeNetwork.Action.AID));
        this.row(g, this.t("rescue_toggle", new Object[0]), this.t("rescue_help", new Object[0]) + " \u00b7 " + this.t(this.profile().m_128471_("VoidRescue") ? "on" : "off", new Object[0]), this.selected() != null, () -> this.act(SoulLifeNetwork.Action.RESCUE));
        this.row(g, this.t("expressions", new Object[0]), this.t(this.d().m_128471_("Expressions") ? "on" : "off", new Object[0]), true, () -> this.act(SoulLifeNetwork.Action.EXPRESSIONS));
        this.row(g, this.t("codex", new Object[0]), "", true, () -> ClientRoyal.open(0));
    }

    private void discoveries(GuiGraphics g) {
        for (int i = 1; i <= 30; ++i) {
            int feature = i;
            this.row(g, i + " \u00b7 " + this.t("feature." + i, new Object[0]), this.t("feature_help." + i, new Object[0]), true, () -> this.show(switch (feature) {
                case 1, 2, 3 -> 0;
                case 4, 5, 6, 7, 8, 9, 10, 27 -> 1;
                case 13, 28 -> 3;
                case 15, 16, 17 -> 4;
                case 12, 21, 25, 29 -> 5;
                case 11, 14, 26, 30 -> 6;
                default -> 2;
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

    public boolean m_6050_(double x, double y, double amount) {
        this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll - (int)(amount * 28.0)));
        return true;
    }

    public boolean m_5534_(char ch, int mods) {
        if (this.searching && !Character.isISOControl(ch) && this.query.length() < 80) {
            this.query = this.query + ch;
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
            if (SoulLifeScreen.m_96630_((int)key)) {
                String value = this.f_96541_.f_91068_.m_90876_().replaceAll("[\\p{Cntrl}]", "");
                this.query = (this.query + value).substring(0, Math.min(80, this.query.length() + value.length()));
                return true;
            }
        }
        if (!this.searching && ClientArmy.ORIGINAL.m_90832_(key, scan)) {
            ClientArmy.openOriginal();
            return true;
        }
        if (!this.searching && ClientArmy.OPEN.m_90832_(key, scan)) {
            this.m_7379_();
            return true;
        }
        if (key == 258) {
            if (!this.controls.isEmpty()) {
                this.focus = Math.floorMod(this.focus + (SoulLifeScreen.m_96638_() ? -1 : 1), this.controls.size());
            }
            return true;
        }
        if (key == 257 && this.focus >= 0 && this.focus < this.controls.size() && this.controls.get((int)this.focus).enabled && this.controls.get((int)this.focus).action != null) {
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

