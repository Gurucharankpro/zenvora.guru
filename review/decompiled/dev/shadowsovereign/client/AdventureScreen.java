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
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.adventure.AdventureContent;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.ClientAdventure;
import dev.shadowsovereign.client.ClientJourney;
import dev.shadowsovereign.client.ClientLegacy;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.JourneyScreen;
import dev.shadowsovereign.client.LegacyScreen;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.network.AdventureNetwork;
import dev.shadowsovereign.network.JourneyNetwork;
import dev.shadowsovereign.network.LegacyNetwork;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class AdventureScreen
extends Screen {
    private static final int W = 700;
    private static final int H = 430;
    private static final int TEXT = -1054465;
    private static final int MUTED = -5662019;
    private static final int GOLD = -1653119;
    private static final int BLUE = -7940617;
    private final List<Control> controls = new ArrayList<Control>();
    private final List<UUID> party = new ArrayList<UUID>();
    private UUID selected;
    private Page page = Page.SANCTUARY;
    private int ticks;
    private int bestiaryPage;
    private int scroll;
    private float scale;
    private float left;
    private float top;

    public AdventureScreen(UUID selected) {
        super((Component)ModLanguage.component("Soulbound Army \u2014 Adventure Journal"));
        this.selected = selected;
    }

    private CompoundTag d() {
        return ClientAdventure.data;
    }

    private List<CompoundTag> list(String key) {
        ArrayList<CompoundTag> result = new ArrayList<CompoundTag>();
        for (Tag t : this.d().m_128437_(key, 10)) {
            result.add((CompoundTag)t);
        }
        return result;
    }

    private CompoundTag ally() {
        return this.list("Roster").stream().filter(n -> n.m_128403_("Id") && n.m_128342_("Id").equals(this.selected)).findFirst().orElse(new CompoundTag());
    }

    private String allyName() {
        return this.ally().m_128456_() ? "No companion selected" : ModLanguage.user(this.ally().m_128461_("Name")) + " / Lv." + this.ally().m_128451_("Level");
    }

    public void show(Page next) {
        this.page = next;
        this.scroll = 0;
        this.build();
    }

    protected void m_7856_() {
        this.scale = Math.min(1.6f, Math.min(((float)this.f_96543_ - 14.0f) / 700.0f, ((float)this.f_96544_ - 14.0f) / 430.0f));
        this.left = ((float)this.f_96543_ / this.scale - 700.0f) / 2.0f;
        this.top = ((float)this.f_96544_ / this.scale - 430.0f) / 2.0f;
        this.updated();
    }

    public void updated() {
        List<CompoundTag> roster = this.list("Roster");
        if (roster.stream().noneMatch(n -> n.m_128403_("Id") && n.m_128342_("Id").equals(this.selected))) {
            this.selected = roster.isEmpty() ? null : roster.get(0).m_128342_("Id");
        }
        this.party.removeIf(id -> roster.stream().noneMatch(n -> n.m_128403_("Id") && n.m_128342_("Id").equals(id) && !n.m_128471_("Active") && !n.m_128471_("Pending") && !n.m_128471_("Busy")));
        if (this.f_96541_ != null) {
            this.build();
        }
    }

    private void add(int x, int y, int w, Supplier<String> label, BooleanSupplier enabled, Runnable click) {
        this.controls.add(new Control(x, y, w, label, enabled, click));
    }

    private void button(int x, int y, int w, String label, BooleanSupplier enabled, Runnable click) {
        this.add(x, y, w, () -> label, enabled, click);
    }

    private void send(AdventureNetwork.Action action, UUID target, int value) {
        ClientAdventure.send(new AdventureNetwork.Request(action, target, value, List.copyOf(this.party), this.selected, this.bestiaryPage));
    }

    private void cycle(int delta) {
        List<CompoundTag> roster = this.list("Roster");
        if (roster.isEmpty()) {
            return;
        }
        int index = 0;
        for (int i = 0; i < roster.size(); ++i) {
            if (!roster.get(i).m_128342_("Id").equals(this.selected)) continue;
            index = i;
        }
        this.selected = roster.get(Math.floorMod(index + delta, roster.size())).m_128342_("Id");
        this.build();
    }

    private boolean near() {
        return this.d().m_128471_("Near");
    }

    private boolean noSession() {
        return !this.d().m_128441_("Session");
    }

    private void build() {
        this.controls.clear();
        this.button(550, 82, 132, "Journey >", () -> true, () -> {
            ClientJourney.send(JourneyNetwork.Action.VIEW, this.selected, 0, false);
            this.f_96541_.m_91152_((Screen)new JourneyScreen(this.selected));
        });
        this.button(18, 86, 128, "Legacy >", () -> true, () -> {
            ClientLegacy.send(new LegacyNetwork.Request(LegacyNetwork.Action.VIEW, this.selected, null, 0, BattleRules.Squad.ALL, ""));
            this.f_96541_.m_91152_((Screen)new LegacyScreen(this.selected));
        });
        for (Page page : Page.values()) {
            this.button(18, 113 + page.ordinal() * 27, 128, AdventureService.pretty(page.name()), () -> true, () -> this.show(p));
        }
        this.button(18, 391, 128, "Back to army", () -> true, this::m_7379_);
        this.button(164, 391, 85, "Prev ally", () -> this.selected != null, () -> this.cycle(-1));
        this.button(255, 391, 85, "Next ally", () -> this.selected != null, () -> this.cycle(1));
        switch (this.page) {
            case SANCTUARY: {
                this.button(164, 285, 245, "Begin Scout Rift Trial", () -> this.near() && this.noSession(), () -> this.send(AdventureNetwork.Action.TRIAL, null, 0));
                this.button(419, 285, 263, "Begin an Echo Hunt", () -> this.near(), () -> this.send(AdventureNetwork.Action.HUNT, null, 0));
                this.button(164, 319, 245, "Open contracts", () -> true, () -> this.show(Page.CONTRACTS));
                this.button(419, 319, 263, "Plan an expedition", () -> true, () -> this.show(Page.EXPEDITIONS));
                break;
            }
            case TRIALS: {
                int tier;
                int i2;
                String[] names = new String[]{"Scout / 3 waves", "Veteran / 4 waves", "Monarch / 5 waves"};
                for (i2 = 0; i2 < 3; ++i2) {
                    tier = i2;
                    this.button(164 + i2 * 174, 222, 164, names[i2], () -> this.near() && this.noSession() && this.d().m_128451_("Discoveries") >= (tier == 0 ? 0 : (tier == 1 ? 5 : 15)), () -> this.send(AdventureNetwork.Action.TRIAL, null, tier));
                }
                this.physicalButtons();
                break;
            }
            case ESCORT: {
                this.button(164, 265, 260, "Search for a Lost Soul", () -> this.near() && this.noSession(), () -> this.send(AdventureNetwork.Action.ESCORT, null, 0));
                this.physicalButtons();
                break;
            }
            case EXPEDITIONS: {
                int i;
                int tier;
                int i2;
                this.add(164, 175, 518, () -> this.party.contains(this.selected) ? "Remove selected from party" : "Add selected to party (" + this.party.size() + " / 3)", () -> this.selected != null && (this.party.contains(this.selected) || this.party.size() < 3 && !this.ally().m_128471_("Active") && !this.ally().m_128471_("Pending") && !this.ally().m_128471_("Busy")), () -> {
                    if (!this.party.remove(this.selected)) {
                        this.party.add(this.selected);
                    }
                    this.build();
                });
                String[] tiers = new String[]{"Scout / Lv.1 / 3m", "Survey / Lv.4 / 6m", "Deep / Lv.7 / 10m"};
                for (i2 = 0; i2 < 3; ++i2) {
                    tier = i2;
                    this.button(164 + 174 * i2, 244, 164, tiers[i2], () -> this.near() && !this.party.isEmpty() && this.list("Expeditions").size() < 2, () -> this.send(AdventureNetwork.Action.DISPATCH, null, tier));
                }
                List<CompoundTag> jobs = this.list("Expeditions");
                for (i = 0; i < jobs.size(); ++i) {
                    CompoundTag compoundTag = jobs.get(i);
                    UUID id = compoundTag.m_128342_("Id");
                    int y = 298 + 42 * i;
                    this.button(492, y, 87, "Claim", () -> e.m_128471_("Ready") || ClientAdventure.now() >= e.m_128454_("End"), () -> this.send(AdventureNetwork.Action.CLAIM_EXPEDITION, id, 0));
                    this.button(588, y, 94, "Cancel", () -> true, () -> this.send(AdventureNetwork.Action.CANCEL_EXPEDITION, id, 0));
                }
                break;
            }
            case CONTRACTS: {
                int i;
                List<CompoundTag> offers = this.list("Offers");
                for (int i2 = 0; i2 < 3; ++i2) {
                    int kind = i2;
                    boolean bl = offers.stream().anyMatch(n -> n.m_128451_("Kind") == kind && n.m_128471_("Available"));
                    this.button(578, 142 + i2 * 31, 104, "Accept", () -> this.near() && available, () -> this.send(AdventureNetwork.Action.ACCEPT_CONTRACT, null, kind));
                }
                List<CompoundTag> entries = this.list("Contracts");
                for (i = 0; i < entries.size(); ++i) {
                    CompoundTag compoundTag = entries.get(i);
                    UUID id = compoundTag.m_128342_("Id");
                    int y = 265 + i * 36;
                    this.button(510, y, 78, "Claim", () -> c.m_128451_("Progress") >= c.m_128451_("Goal"), () -> this.send(AdventureNetwork.Action.CLAIM_CONTRACT, id, 0));
                    this.button(596, y, 86, "Abandon", () -> true, () -> this.send(AdventureNetwork.Action.ABANDON_CONTRACT, id, 0));
                }
                break;
            }
            case NEMESIS: {
                CompoundTag n2 = this.d().m_128469_("Nemesis");
                this.button(164, 267, 160, "Kin hunt alternative", () -> !n2.m_128456_() && !n2.m_128471_("Kin") && !n2.m_128471_("Ready"), () -> this.send(AdventureNetwork.Action.NEMESIS_KIN, null, 0));
                this.button(338, 267, 166, "Claim revenge", () -> n2.m_128471_("Ready"), () -> this.send(AdventureNetwork.Action.CLAIM_NEMESIS, null, 0));
                this.button(518, 267, 164, "Dismiss hunt", () -> !n2.m_128456_(), () -> this.send(AdventureNetwork.Action.DISMISS_NEMESIS, null, 0));
                break;
            }
            case BESTIARY: {
                this.button(164, 354, 150, "Previous page", () -> this.bestiaryPage > 0, () -> {
                    --this.bestiaryPage;
                    this.scroll = 0;
                    this.send(AdventureNetwork.Action.VIEW, null, 0);
                });
                this.button(324, 354, 150, "Next page", () -> this.d().m_128451_("Discoveries") > (this.bestiaryPage + 1) * 256, () -> {
                    ++this.bestiaryPage;
                    this.scroll = 0;
                    this.send(AdventureNetwork.Action.VIEW, null, 0);
                });
                break;
            }
            case TREASURE: {
                int i;
                boolean expired = this.d().m_128454_("CacheCycle") != ClientAdventure.now() / 24000L;
                List<CompoundTag> caches = this.list("Caches");
                this.button(164, 157, 250, "Begin today's Echo Hunt", () -> this.near() && (expired || caches.isEmpty()), () -> this.send(AdventureNetwork.Action.HUNT, null, 0));
                for (i = 0; i < caches.size(); ++i) {
                    CompoundTag compoundTag = caches.get(i);
                    UUID id = compoundTag.m_128342_("Id");
                    this.button(548, 205 + i * 35, 134, compoundTag.m_128471_("Claimed") ? "Discovered" : "Claim nearby", () -> !expired && !c.m_128471_("Claimed"), () -> this.send(AdventureNetwork.Action.CLAIM_CACHE, id, 0));
                }
                this.button(164, 330, 260, "Claim full hunt reward", () -> !expired && caches.size() == 3 && caches.stream().allMatch(c -> c.m_128471_("Claimed")) && !this.d().m_128471_("HuntClaimed"), () -> this.send(AdventureNetwork.Action.CLAIM_HUNT, null, 0));
                break;
            }
            case RELICS: {
                for (Enum enum_ : AdventureData.Relic.values()) {
                    int requirement = enum_ == AdventureData.Relic.NONE ? 0 : (enum_ == AdventureData.Relic.PATHFINDER ? 1 : (enum_ == AdventureData.Relic.SEEKER ? 3 : 5));
                    this.button(164, 160 + enum_.ordinal() * 41, 245, AdventureService.pretty(enum_.name()) + (this.ally().m_128451_("Relic") == enum_.ordinal() ? " [equipped]" : ""), () -> this.selected != null && this.d().m_128454_("Completions") >= (long)requirement, () -> this.lambda$build$62((AdventureData.Relic)enum_));
                }
                break;
            }
            case BONDS: {
                this.button(164, 235, 250, this.ally().m_128451_("Stage") == 0 ? "Begin this companion's story" : "Focus/resume this bond", () -> this.selected != null && this.ally().m_128451_("Stage") < 4, () -> this.send(AdventureNetwork.Action.BOND, null, 0));
                this.add(164, 278, 518, () -> "Title: " + AdventureService.pretty(AdventureData.Title.values()[Math.min(AdventureData.Title.values().length - 1, this.ally().m_128451_("Title"))].name()) + " (click to cycle unlocked)", () -> this.selected != null, () -> this.send(AdventureNetwork.Action.TITLE, null, this.nextUnlocked("Titles", AdventureData.Title.values(), this.ally().m_128451_("Title"))));
                this.add(164, 312, 518, () -> "Soul trail: " + AdventureService.pretty(AdventureData.Trail.values()[Math.min(AdventureData.Trail.values().length - 1, this.ally().m_128451_("Trail"))].name()) + " (click to cycle unlocked)", () -> this.selected != null, () -> this.send(AdventureNetwork.Action.TRAIL, null, this.nextUnlocked("Trails", AdventureData.Trail.values(), this.ally().m_128451_("Trail"))));
            }
        }
    }

    private int nextUnlocked(String key, Enum<?>[] values, int current) {
        HashSet<String> unlocked = new HashSet<String>();
        for (Tag t : this.d().m_128437_(key, 8)) {
            unlocked.add(t.m_7916_());
        }
        for (int i = 1; i <= values.length; ++i) {
            int next = Math.floorMod(current + i, values.length);
            if (!unlocked.contains(values[next].name())) continue;
            return next;
        }
        return 0;
    }

    private void physicalButtons() {
        CompoundTag s = this.d().m_128469_("Session");
        int phase = s.m_128451_("Phase");
        this.button(164, 342, 122, "Pause", () -> !s.m_128456_() && phase == AdventureData.Phase.ACTIVE.ordinal(), () -> this.send(AdventureNetwork.Action.PAUSE, null, 0));
        this.button(296, 342, 122, "Resume", () -> this.near() && !s.m_128456_() && phase == AdventureData.Phase.PAUSED.ordinal(), () -> this.send(AdventureNetwork.Action.RESUME, null, 0));
        this.button(428, 342, 122, "Cancel", () -> !s.m_128456_(), () -> this.send(AdventureNetwork.Action.CANCEL_PHYSICAL, null, 0));
        this.button(560, 342, 122, "Claim", () -> !s.m_128456_() && phase == AdventureData.Phase.READY.ordinal(), () -> this.send(AdventureNetwork.Action.CLAIM_PHYSICAL, null, 0));
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        if (++this.ticks % 20 == 0) {
            this.send(AdventureNetwork.Action.VIEW, null, 0);
        }
    }

    public void m_88315_(GuiGraphics g, int mouseX, int mouseY, float partial) {
        this.m_280273_(g);
        double mx = (float)mouseX / this.scale - this.left;
        double my = (float)mouseY / this.scale - this.top;
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.left, this.top, 0.0f);
        MenuTheme.panel(g, 700, 430, 2);
        AdventureScreen.border(g, 0, 0, 700, 430, -10137475);
        g.m_280509_(0, 0, 700, 2, -7940617);
        this.text(g, "S O U L B O U N D   A R M Y  /  1.0.7", 18, 16, -5662019);
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_(18.0f, 34.0f, 0.0f);
        g.m_280168_().m_85841_(2.0f, 2.0f, 1.0f);
        this.text(g, "ARMY ADVENTURES", 0, 0, -1054465);
        g.m_280168_().m_85849_();
        this.text(g, this.d().m_128454_("Completions") + " adventures completed", 430, 35, -1653119);
        this.text(g, this.d().m_128451_("Discoveries") + " soul types discovered", 430, 51, -5662019);
        g.m_280509_(18, 71, 682, 72, -12110759);
        g.m_280509_(154, 85, 155, 383, -13293505);
        this.text(g, AdventureService.pretty(this.page.name()).toUpperCase(Locale.ROOT), 164, 88, -7940617);
        this.renderPage(g);
        for (Control c : this.controls) {
            boolean enabled = c.enabled.getAsBoolean();
            boolean hover = c.hit(mx, my);
            boolean nav = c.x == 18 && c.y >= 113 && c.y < 383 && (c.y - 113) / 27 == this.page.ordinal();
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 23, enabled ? (hover ? -10469004 : (nav ? -12703145 : -13950919)) : -15133153);
            AdventureScreen.border(g, c.x, c.y, c.w, 23, hover && enabled ? -4744213 : (nav ? -7940617 : -11650463));
            FantasyIcons.label(g, this.f_96547_, c.label.get(), c.x, c.y, c.w, 23, enabled ? -1054465 : -9016701);
        }
        this.text(g, ModLanguage.fit(this.f_96547_, this.allyName(), 330), 352, 399, -1653119);
        this.text(g, "Owner-bound rewards / no combat currency", 164, 419, -5662019);
        g.m_280168_().m_85849_();
    }

    private void renderPage(GuiGraphics g) {
        switch (this.page) {
            case SANCTUARY: {
                g.m_280480_(new ItemStack((ItemLike)AdventureContent.SANCTUARY_ITEM.get()), 655, 105);
                g.m_280480_(new ItemStack((ItemLike)AdventureContent.COMPASS.get()), 655, 135);
                this.line(g, 0, (String)(this.d().m_128471_("Bound") ? "Active hub: " + AdventureScreen.position(this.d().m_128454_("Sanctuary")) : "Place and use a Soul Sanctuary to bind your hub."), -1054465);
                this.line(g, 1, this.d().m_128461_("Dimension"), -5662019);
                this.line(g, 3, this.near() ? "Sanctuary nearby \u2014 adventures available" : "Return within 8 blocks of your Sanctuary to start.", this.near() ? -7940617 : -1653119);
                this.line(g, 5, "Craft: Shadow Sigil + 4 obsidian + 4 amethyst shards.", -5662019);
                this.line(g, 7, "Relics unlock at 1 / 3 / 5 completed adventures.", -1653119);
                this.line(g, 9, "Keep your old army, combat skills and progression.", -5662019);
                break;
            }
            case TRIALS: {
                this.line(g, 0, "Bring an active ally. Clear waves together; claim cosmetics.", -1054465);
                this.line(g, 2, "Veteran: discover 5 types / Monarch: discover 15 types.", -1653119);
                this.line(g, 4, "No terrain destruction, recruit farming, loot or XP drops.", -5662019);
                this.line(g, 6, "Real companion kills still advance personal combat levels.", -5662019);
                this.sessionLines(g, 255);
                break;
            }
            case EXPEDITIONS: {
                this.line(g, 0, this.allyName(), -1054465);
                this.line(g, 1, "Only reserve allies; no reforming or already-busy members.", -5662019);
                this.line(g, 3, "Progress continues on a running server, even offline.", -7940617);
                String names = String.join((CharSequence)", ", this.party.stream().map(id -> this.list("Roster").stream().filter(n -> n.m_128342_("Id").equals(id)).map(n -> ModLanguage.user(n.m_128461_("Name"))).findFirst().orElse("Unknown")).toList());
                this.text(g, ModLanguage.fit(this.f_96547_, "Party: " + (names.isEmpty() ? "select an ally below, then Add" : names), 510), 164, 211, -1653119);
                List<CompoundTag> jobs = this.list("Expeditions");
                for (int i = 0; i < jobs.size(); ++i) {
                    CompoundTag e = jobs.get(i);
                    this.text(g, (new String[]{"Scout", "Survey", "Deep"})[e.m_128451_("Tier")] + " / " + AdventureScreen.time(e.m_128454_("End") - ClientAdventure.now()) + " / " + e.m_128437_("Members", 10).size() + " allies", 164, 306 + i * 42, e.m_128471_("Ready") ? -7940617 : -1054465);
                }
                break;
            }
            case CONTRACTS: {
                this.line(g, 0, "Three daily offers. Accepted progress persists across days.", -5662019);
                String[] descriptions = new String[]{"Hunt 5: " + this.d().m_128461_("OfferedTarget"), "Recruit 3 different mob types", "Explore 2 biomes with a nearby active ally"};
                for (int i = 0; i < 3; ++i) {
                    this.text(g, ModLanguage.fit(this.f_96547_, descriptions[i], 400), 164, 150 + i * 31, -1054465);
                }
                this.text(g, "YOUR ACTIVE CONTRACTS", 164, 241, -1653119);
                List<CompoundTag> cs = this.list("Contracts");
                for (int i = 0; i < cs.size(); ++i) {
                    CompoundTag c = cs.get(i);
                    this.text(g, ModLanguage.fit(this.f_96547_, String.valueOf((Object)AdventureData.ContractKind.values()[c.m_128451_("Kind")]) + (String)(c.m_128451_("Kind") == 0 ? " " + c.m_128461_("Target") : "") + "  " + c.m_128451_("Progress") + " / " + c.m_128451_("Goal"), 330), 164, 273 + i * 36, -1054465);
                }
                break;
            }
            case NEMESIS: {
                CompoundTag n2 = this.d().m_128469_("Nemesis");
                if (n2.m_128456_()) {
                    this.line(g, 1, "A natural hostile that defeats an ally can become a Nemesis.", -1054465);
                    this.line(g, 3, "Your companion still returns through its normal soul timer.", -7940617);
                    this.line(g, 5, "No revenge objectives for players or adventure NPCs.", -5662019);
                    break;
                }
                this.line(g, 0, n2.m_128461_("Type"), -1054465);
                this.line(g, 2, "Last seen: " + AdventureScreen.position(n2.m_128454_("Pos")), -1653119);
                this.line(g, 3, n2.m_128461_("Dimension"), -5662019);
                this.line(g, 5, (String)(n2.m_128471_("Ready") ? "REVENGE COMPLETE \u2014 claim your title" : (n2.m_128471_("Kin") ? "Kin vengeance: " + n2.m_128451_("Progress") + " / 3 natural mobs" : "Defeat that exact enemy, or choose kin vengeance.")), -7940617);
                this.line(g, 7, "Locations are last-known; missing chunks are not loaded.", -5662019);
                break;
            }
            case BESTIARY: {
                this.line(g, 0, "Collection milestones: 5 / 15 / 30 types unlock cosmetics.", -1653119);
                ListTag types = this.d().m_128437_("Types", 8);
                int max = Math.max(0, types.size() - 18);
                this.scroll = Math.max(0, Math.min(this.scroll, max));
                for (int i = 0; i < 18 && i + this.scroll < types.size(); ++i) {
                    String type = types.m_128778_(i + this.scroll);
                    long owned = this.list("Roster").stream().filter(n -> n.m_128461_("Type").equals(type)).count();
                    this.text(g, ModLanguage.fit(this.f_96547_, (owned > 0L ? "\u2726 " : "\u00b7 ") + type + "  / owned " + owned, 510), 164, 139 + i * 11, owned > 0L ? -1054465 : -5662019);
                }
                this.text(g, "Scroll entries / page " + (this.bestiaryPage + 1), 490, 362, -5662019);
                break;
            }
            case ESCORT: {
                this.line(g, 0, "A spectral Lost Soul waits nearby. Bring it home safely.", -1054465);
                this.line(g, 2, "Fight two ambushes. Quest souls cannot join your army.", -5662019);
                this.line(g, 4, "A nearby Warden relic saves it from one lethal hit.", -1653119);
                this.sessionLines(g, 205);
                break;
            }
            case TREASURE: {
                this.line(g, 0, "Craft Echo Compass: compass + amethyst + ender pearl.", -1054465);
                this.line(g, 2, "Use it for directions and nearby soul-spark hints.", -5662019);
                List<CompoundTag> caches = this.list("Caches");
                for (int i = 0; i < caches.size(); ++i) {
                    CompoundTag c = caches.get(i);
                    this.text(g, (c.m_128471_("Claimed") ? "FOUND  " : "ECHO ") + (i + 1) + "  " + (c.m_128471_("Claimed") ? AdventureScreen.position(c.m_128454_("Pos")) : "Follow your Echo Compass"), 164, 213 + i * 35, c.m_128471_("Claimed") ? -7940617 : -1054465);
                }
                this.text(g, "Claim within 3 blocks / 3 caches per saved server day", 164, 309, -5662019);
                break;
            }
            case RELICS: {
                this.line(g, 0, "One virtual relic slot per companion; native gear stays intact.", -1054465);
                String[] descriptions = new String[]{"Remove the equipped relic.", "1 completion / expeditions 15% faster.", "3 completions / cache hints reach 40 blocks.", "5 completions / one escort rescue per mission."};
                for (int i = 0; i < 4; ++i) {
                    ModLanguage.wrap(g, this.f_96547_, (Component)Component.m_237113_((String)descriptions[i]), 421, 165 + i * 41, 255, i == 0 ? -5662019 : -1653119);
                }
                this.lineAt(g, "Effects never stack and do not change base damage.", 164, 345, -5662019);
                break;
            }
            case BONDS: {
                this.line(g, 0, this.allyName(), -1054465);
                int stage = this.ally().m_128451_("Stage");
                String objective = switch (stage) {
                    case 0 -> "Choose a favorite companion and begin its story.";
                    case 1 -> "Stage 1: " + Math.min(5L, this.ally().m_128454_("BondKills")) + " / 5 new personal kills.";
                    case 2 -> "Stage 2: " + this.ally().m_128451_("Biomes") + " / 3 biomes discovered together.";
                    case 3 -> "Stage 3: win a Rift Trial with this ally alive nearby.";
                    default -> "STORY COMPLETE \u2014 Bound Vanguard title and Echo trail!";
                };
                this.line(g, 2, objective, -7940617);
                this.line(g, 4, "Only the focused bond gains exploration/story progress.", -5662019);
                this.line(g, 6, "Titles and trails are cosmetic; level cap remains 10.", -1653119);
            }
        }
    }

    private void sessionLines(GuiGraphics g, int y) {
        CompoundTag s = this.d().m_128469_("Session");
        if (s.m_128456_()) {
            this.text(g, "No physical adventure active.", 164, y, -5662019);
            return;
        }
        String state = AdventureData.Phase.values()[s.m_128451_("Phase")].name();
        this.text(g, String.valueOf((Object)AdventureData.Kind.values()[s.m_128451_("Kind")]) + " / " + state + " / " + Math.min(s.m_128451_("MaxWaves"), s.m_128451_("Wave") + 1) + " of " + s.m_128451_("MaxWaves") + " / enemies " + s.m_128451_("Enemies"), 164, y, -7940617);
        if (s.m_128441_("EscortPos")) {
            this.text(g, "Lost Soul: " + AdventureScreen.position(s.m_128454_("EscortPos")) + " / HP " + (int)s.m_128457_("EscortHealth"), 164, y + 17, -1054465);
        }
        this.text(g, "Leaving the area or logging out pauses this adventure.", 164, y + 36, -5662019);
    }

    private static String position(long packed) {
        BlockPos p = BlockPos.m_122022_((long)packed);
        return p.m_123341_() + ", " + p.m_123342_() + ", " + p.m_123343_();
    }

    private static String time(long ticks) {
        if (ticks <= 0L) {
            return "READY";
        }
        long seconds = (ticks + 19L) / 20L;
        return seconds / 60L + ":" + String.format(Locale.ROOT, "%02d", seconds % 60L);
    }

    private void line(GuiGraphics g, int row, String s, int color) {
        this.text(g, ModLanguage.fit(this.f_96547_, s, 518), 164, 116 + row * 13, color);
    }

    private void lineAt(GuiGraphics g, String s, int x, int y, int color) {
        this.text(g, ModLanguage.fit(this.f_96547_, s, 518), x, y, color);
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
        for (Control c : List.copyOf(this.controls)) {
            if (!c.hit(mx, my) || !c.enabled.getAsBoolean()) continue;
            c.click.run();
            return true;
        }
        return false;
    }

    public boolean m_6050_(double x, double y, double delta) {
        if (this.page == Page.BESTIARY) {
            this.scroll = Math.max(0, this.scroll - (int)Math.signum(delta) * 3);
            return true;
        }
        return false;
    }

    public void m_7379_() {
        this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected));
    }

    private /* synthetic */ void lambda$build$62(AdventureData.Relic r) {
        this.send(AdventureNetwork.Action.RELIC, null, r.ordinal());
    }

    public static enum Page {
        SANCTUARY,
        TRIALS,
        EXPEDITIONS,
        CONTRACTS,
        NEMESIS,
        BESTIARY,
        ESCORT,
        TREASURE,
        RELICS,
        BONDS;

    }

    private record Control(int x, int y, int w, Supplier<String> label, BooleanSupplier enabled, Runnable click) {
        boolean hit(double mx, double my) {
            return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + 23);
        }
    }
}

