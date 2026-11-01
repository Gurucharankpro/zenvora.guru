/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.client.AdventureScreen;
import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.ClientJourney;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.journey.JourneyService;
import dev.shadowsovereign.journey.JourneyState;
import dev.shadowsovereign.network.JourneyNetwork;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public final class JourneyScreen
extends Screen {
    private static final int W = 740;
    private static final int H = 450;
    private static final int TEXT = -1445121;
    private static final int MUTED = -6180666;
    private static final int GOLD = -11126;
    private static final int BLUE = -8135445;
    private final List<Control> controls = new ArrayList<Control>();
    private UUID selected;
    private Page page = Page.CAMPAIGN;
    private boolean hard;
    private int ticks;
    private int focus = -1;
    private int story;
    private float scale;
    private float left;
    private float top;

    public JourneyScreen(UUID id) {
        super((Component)ModLanguage.component("Sovereign's Journey"));
        this.selected = id;
    }

    private CompoundTag d() {
        return ClientJourney.data;
    }

    private CompoundTag j() {
        return this.d().m_128469_("Journey");
    }

    private CompoundTag s() {
        return this.d().m_128469_("Session");
    }

    private CompoundTag run() {
        return this.s().m_128469_("JourneyRun");
    }

    private List<CompoundTag> roster() {
        ArrayList<CompoundTag> rows = new ArrayList<CompoundTag>();
        for (Tag t : this.d().m_128437_("Roster", 10)) {
            rows.add((CompoundTag)t);
        }
        rows.sort(Comparator.comparing(n -> !n.m_128471_("Favorite")));
        return rows;
    }

    private CompoundTag ally() {
        return this.roster().stream().filter(n -> n.m_128403_("Id") && n.m_128342_("Id").equals(this.selected)).findFirst().orElse(new CompoundTag());
    }

    private CompoundTag feats() {
        for (Tag t : this.j().m_128437_("Companions", 10)) {
            CompoundTag n = (CompoundTag)t;
            if (!n.m_128403_("Id") || !n.m_128342_("Id").equals(this.selected)) continue;
            return n;
        }
        return new CompoundTag();
    }

    public void show(Page value) {
        this.page = value;
        this.focus = -1;
        this.build();
    }

    public void updated() {
        List<CompoundTag> rows = this.roster();
        if (rows.stream().noneMatch(n -> n.m_128403_("Id") && n.m_128342_("Id").equals(this.selected))) {
            this.selected = rows.isEmpty() ? null : rows.get(0).m_128342_("Id");
        }
        ClientJourney.selected = this.selected;
        if (this.f_96541_ != null) {
            this.build();
        }
    }

    protected void m_7856_() {
        this.scale = Math.min(1.6f, Math.min(((float)this.f_96543_ - 14.0f) / 740.0f, ((float)this.f_96544_ - 14.0f) / 450.0f));
        this.left = ((float)this.f_96543_ / this.scale - 740.0f) / 2.0f;
        this.top = ((float)this.f_96544_ / this.scale - 450.0f) / 2.0f;
        this.updated();
    }

    private void button(int x, int y, int w, String text, BooleanSupplier enabled, Runnable action) {
        this.controls.add(new Control(x, y, w, text, enabled, action));
    }

    private void send(JourneyNetwork.Action action, int value) {
        ClientJourney.send(action, this.selected, value, this.hard);
    }

    private boolean free() {
        return this.s().m_128456_();
    }

    private boolean near() {
        return this.d().m_128471_("Near");
    }

    private boolean unlocked(int count) {
        return this.d().m_128454_("Completions") >= (long)count;
    }

    private boolean active(int level) {
        return this.selected != null && this.ally().m_128471_("Active") && !this.ally().m_128471_("Pending") && this.ally().m_128451_("Level") >= level;
    }

    private boolean kind(AdventureData.Kind k) {
        return !this.s().m_128456_() && this.s().m_128451_("Kind") == k.ordinal();
    }

    private boolean running() {
        return !this.s().m_128456_() && this.s().m_128451_("Phase") == AdventureData.Phase.ACTIVE.ordinal();
    }

    private void cycle(int delta) {
        List<CompoundTag> rows = this.roster();
        if (rows.isEmpty()) {
            return;
        }
        int index = 0;
        for (int i = 0; i < rows.size(); ++i) {
            if (!rows.get(i).m_128342_("Id").equals(this.selected)) continue;
            index = i;
        }
        ClientJourney.selected = this.selected = rows.get(Math.floorMod(index + delta, rows.size())).m_128342_("Id");
        this.build();
    }

    private void build() {
        this.controls.clear();
        this.button(18, 88, 135, "< Adventures", () -> true, this::m_7379_);
        for (Page page : Page.values()) {
            this.button(18, 116 + page.ordinal() * 28, 135, AdventureService.pretty(page.name()), () -> true, () -> this.show(p));
        }
        this.button(18, 412, 135, "Back to army", () -> true, () -> this.f_96541_.m_91152_((Screen)new ArmyScreen(this.selected)));
        this.button(172, 412, 85, "Prev ally", () -> this.selected != null, () -> this.cycle(-1));
        this.button(265, 412, 85, "Next ally", () -> this.selected != null, () -> this.cycle(1));
        if (this.page == Page.BOSSES || this.page == Page.RIFTS || this.page == Page.RALLY || this.page == Page.ANGLING) {
            this.button(500, 86, 220, (this.free() ? this.hard : this.run().m_128471_("Hard")) ? "Difficulty: Challenging" : "Difficulty: Relaxed", this::free, () -> {
                this.hard = !this.hard;
                this.build();
            });
        }
        switch (this.page) {
            case CAMPAIGN: {
                this.button(172, 352, 548, "Claim next chapter title", () -> this.j().m_128451_("Chapter") < 6 && this.d().m_128471_("ChapterReady"), () -> this.send(JourneyNetwork.Action.CAMPAIGN, 0));
                break;
            }
            case BOSSES: {
                for (int i = 0; i < 3; ++i) {
                    int boss = i;
                    this.button(172, 177 + i * 43, 548, "Challenge " + JourneyService.BOSSES[i], () -> this.near() && this.free() && this.unlocked(3), () -> this.send(JourneyNetwork.Action.BOSS, boss));
                }
                this.sessionButtons();
                break;
            }
            case RIFTS: {
                this.button(172, 160, 548, "Begin branching Rift Journey", () -> this.near() && this.free() && this.unlocked(5), () -> this.send(JourneyNetwork.Action.RIFT, 0));
                this.button(172, 228, 267, this.s().m_128451_("Wave") == 2 ? "Face Gravewarden" : "Quiet passage / 4 zombies", () -> this.kind(AdventureData.Kind.RIFT) && this.running() && this.run().m_128471_("ChooseRoute") && !this.run().m_128471_("ChooseBoon"), () -> this.send(JourneyNetwork.Action.ROUTE, 0));
                this.button(453, 228, 267, this.s().m_128451_("Wave") == 2 ? "Face Hollow Tyrant" : "Dangerous / 6 enemies", () -> this.kind(AdventureData.Kind.RIFT) && this.running() && this.run().m_128471_("ChooseRoute") && !this.run().m_128471_("ChooseBoon"), () -> this.send(JourneyNetwork.Action.ROUTE, 1));
                this.button(172, 272, 267, "Rest / heal living allies 2 HP", () -> this.kind(AdventureData.Kind.RIFT) && this.running() && this.run().m_128471_("ChooseBoon"), () -> this.send(JourneyNetwork.Action.BOON, 0));
                this.button(453, 272, 267, "Prepare / 2 HP next-room ward", () -> this.kind(AdventureData.Kind.RIFT) && this.running() && this.run().m_128471_("ChooseBoon"), () -> this.send(JourneyNetwork.Action.BOON, 1));
                this.sessionButtons();
                break;
            }
            case RALLY: {
                this.button(172, 177, 548, "Start rally with selected companion", () -> this.near() && this.free() && this.unlocked(1) && this.active(1), () -> this.send(JourneyNetwork.Action.RALLY, 0));
                this.sessionButtons();
                break;
            }
            case ANGLING: {
                this.button(172, 160, 548, "Open spirit angling", () -> this.near() && this.free() && this.unlocked(1), () -> this.send(JourneyNetwork.Action.FISH, 0));
                this.button(172, 209, 267, "Cast into the spirit water", () -> this.kind(AdventureData.Kind.ANGLING) && this.running() && !this.run().m_128471_("Cast"), () -> this.send(JourneyNetwork.Action.CAST, 0));
                this.button(453, 209, 267, this.run().m_128471_("Bite") ? "BITE! REEL NOW" : "Reel", () -> this.kind(AdventureData.Kind.ANGLING) && this.running() && this.run().m_128471_("Cast"), () -> this.send(JourneyNetwork.Action.REEL, 0));
                this.sessionButtons();
                break;
            }
            case CAMPFIRE: {
                this.button(172, 170, 548, "Story: " + JourneyService.STORIES[this.story] + "  >", this::free, () -> {
                    this.story = (this.story + 1) % 6;
                    this.build();
                });
                this.button(172, 211, 548, "Gather your companions by the fire", () -> this.near() && this.free() && this.selected != null, () -> this.send(JourneyNetwork.Action.CAMP, this.story));
                this.sessionButtons();
                break;
            }
            case POWERS: {
                this.button(172, 185, 267, "Monarch's Domain", () -> this.unlocked(3) && ClientJourney.now() >= this.j().m_128454_("DomainAt"), () -> this.send(JourneyNetwork.Action.DOMAIN, 0));
                this.button(453, 185, 267, "Soul Chains", () -> this.unlocked(1) && this.active(3) && ClientJourney.now() >= this.j().m_128454_("ChainsAt"), () -> this.send(JourneyNetwork.Action.CHAINS, 0));
                break;
            }
            case COMPANION_VIEW: {
                this.button(172, 215, 548, "See through your selected companion", () -> this.active(1), () -> this.send(JourneyNetwork.Action.CAMERA, 0));
                break;
            }
            case FEATS: {
                for (Enum enum_ : JourneyState.Feat.values()) {
                    int n = 151 + enum_.ordinal() * 39;
                    String string = enum_.name().equals(this.feats().m_128461_("Badge")) ? "[Equipped] " : "";
                    String string2 = AdventureService.pretty(enum_.name());
                    this.button(172, n, 548, string + string2 + (switch (1.$SwitchMap$dev$shadowsovereign$journey$JourneyState$Feat[enum_.ordinal()]) {
                        default -> throw new IncompatibleClassChangeError();
                        case 1 -> " / hide badge";
                        case 2 -> " / 25 natural hostile kills";
                        case 3 -> " / personally finish a Chronicle boss";
                        case 4 -> " / finish a rally";
                        case 5 -> " / hear three distinct stories";
                    }), () -> this.lambda$build$44((JourneyState.Feat)enum_), () -> this.lambda$build$45((JourneyState.Feat)enum_));
                }
                break;
            }
        }
    }

    private void sessionButtons() {
        this.button(172, 362, 126, "Pause", this::running, () -> this.send(JourneyNetwork.Action.PAUSE, 0));
        this.button(312, 362, 126, "Resume", () -> this.near() && !this.s().m_128456_() && this.s().m_128451_("Phase") == AdventureData.Phase.PAUSED.ordinal(), () -> this.send(JourneyNetwork.Action.RESUME, 0));
        this.button(453, 362, 126, "Cancel / leave", () -> !this.s().m_128456_(), () -> this.send(JourneyNetwork.Action.CANCEL, 0));
        this.button(594, 362, 126, "Claim", () -> !this.s().m_128456_() && this.s().m_128451_("Phase") == AdventureData.Phase.READY.ordinal(), () -> this.send(JourneyNetwork.Action.CLAIM, 0));
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        ++this.ticks;
        if (this.ticks % (this.page == Page.ANGLING ? 5 : 20) == 0) {
            this.send(JourneyNetwork.Action.VIEW, 0);
        }
    }

    private void text(GuiGraphics g, String value, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, value, x, y, color, false);
    }

    private void line(GuiGraphics g, String value, int y, int color) {
        this.text(g, ModLanguage.fit(this.f_96547_, value, 548), 172, y, color);
    }

    private void body(GuiGraphics g) {
        switch (this.page) {
            case CAMPAIGN: {
                this.line(g, "Six chapters. Your companions write this story with you.", 122, -1445121);
                String[] goals = new String[]{"Bind your Soul Sanctuary", "Hear a campfire story", "Finish a rally OR catch three spirits", "Complete a branching Rift Journey", "Defeat any Chronicle boss", "Defeat all three Chronicle bosses"};
                int claimed = this.j().m_128451_("Chapter");
                for (int i = 0; i < 6; ++i) {
                    this.line(g, (String)(i < claimed ? "DONE  " : i + 1 + ". ") + JourneyService.CHAPTERS[i], 151 + i * 31, i < claimed ? -11126 : -1445121);
                    this.line(g, goals[i], 163 + i * 31, -6180666);
                }
                break;
            }
            case BOSSES: {
                this.line(g, "Unlock: 3 completions. Bring a nearby active companion.", 122, -1445121);
                this.line(g, "Dodge the glowing warnings. Bosses change at half health.", 143, -6180666);
                this.line(g, "One completion per boss / saved day. Hard wins earn titles.", 317, -11126);
                this.status(g, 337);
                break;
            }
            case RIFTS: {
                this.line(g, "Unlock: 5 completions. Choose a route for each of 3 stages.", 122, -1445121);
                this.line(g, "Riskier routes lead to a special cosmetic achievement.", 140, -6180666);
                this.status(g, 195);
                this.line(g, "Between stages, choose rest or a temporary ward first.", 313, -6180666);
                break;
            }
            case RALLY: {
                this.line(g, "Unlock: 1 completion. Selected companion must Follow.", 122, -1445121);
                this.line(g, "Reach all 6 rings together. Teleports end the attempt.", 143, -6180666);
                this.status(g, 222);
                this.line(g, (String)(this.kind(AdventureData.Kind.RALLY) ? "Checkpoints: " + this.s().m_128451_("Wave") + " / 6" : "Relaxed: untimed / Challenging: 90 seconds"), 252, -8135445);
                if (this.kind(AdventureData.Kind.RALLY)) {
                    this.line(g, "Elapsed: " + this.best(this.run().m_128454_("Elapsed")) + (String)(this.run().m_128471_("Hard") ? " / remaining: " + (this.run().m_128454_("Remaining") + 19L) / 20L + "s" : " / untimed"), 276, -1445121);
                }
                this.line(g, "Best relaxed: " + this.best(this.feats().m_128454_("BestRelaxed")) + " / hard: " + this.best(this.feats().m_128454_("BestHard")), 302, -11126);
                this.line(g, "One completion per saved day. Native movement is retained.", 328, -6180666);
                break;
            }
            case ANGLING: {
                this.line(g, "Unlock: 1 completion. Stand near water by your Sanctuary.", 122, -1445121);
                this.line(g, "Wait for each bite. Three timely reels land one spirit.", 140, -6180666);
                this.line(g, this.run().m_128471_("Bite") ? "BITE! REEL NOW!" : (this.run().m_128471_("Cast") ? "Watch the water..." : "Ready to cast"), 253, this.run().m_128471_("Bite") ? -11126 : -8135445);
                this.line(g, "Bites: " + this.run().m_128451_("Bites") + " / 3   Last catch: " + this.run().m_128461_("Catch"), 275, -1445121);
                this.line(g, "Collection: " + Integer.bitCount(this.j().m_128451_("Fish") & 0x3F) + " / 6    Total catches: " + this.j().m_128454_("Catches"), 298, -11126);
                for (int row = 0; row < 2; ++row) {
                    StringJoiner names = new StringJoiner("   ");
                    for (int i = row * 3; i < row * 3 + 3; ++i) {
                        names.add(((this.j().m_128451_("Fish") & 1 << i) != 0 ? "[+] " : "[ ] ") + JourneyService.FISH[i]);
                    }
                    this.line(g, names.toString(), 314 + row * 13, -6180666);
                }
                this.line(g, "Three catches: one completion per saved day.", 343, -6180666);
                break;
            }
            case CAMPFIRE: {
                this.line(g, "Light a real campfire near your Sanctuary. Bring idle allies.", 122, -1445121);
                this.line(g, "A quiet gathering with up to eight companions.", 143, -6180666);
                String storyText = this.run().m_128461_("Story");
                int y = 259;
                for (FormattedCharSequence part : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(storyText.isEmpty() ? "Discover six stories with Brave, Curious and Quiet responses." : storyText), 548)) {
                    ModLanguage.draw(g, this.f_96547_, part, 172, y, -1445121, false);
                    y += 13;
                }
                this.line(g, "Stories heard: " + Integer.bitCount(this.j().m_128451_("Stories") & 0x3F) + " / 6", 331, -11126);
                break;
            }
            case POWERS: {
                this.line(g, "Control the battlefield without changing personal level damage.", 122, -1445121);
                this.line(g, "Aim at an enemy before opening this page to cast Chains.", 145, -6180666);
                this.line(g, "Domain: Lv.4 ally nearby / 3 completions / 60s cooldown.", 235, -8135445);
                this.line(g, "8-block field for 8s; slows up to 8 hostile enemies.", 254, -6180666);
                this.line(g, "Chains: selected Lv.3 ally / 1 completion / 25s cooldown.", 286, -8135445);
                this.line(g, "Slows your aimed target and up to two nearby enemies for 3s.", 305, -6180666);
                this.line(g, "Cooldowns: Domain " + this.remaining(this.j().m_128454_("DomainAt")) + " / Chains " + this.remaining(this.j().m_128454_("ChainsAt")), 341, -11126);
                break;
            }
            case COMPANION_VIEW: {
                this.line(g, "See the world through an active companion within 32 blocks.", 122, -1445121);
                this.line(g, "Your companion keeps acting naturally while you watch.", 150, -6180666);
                this.line(g, "Your body stays in place and remains vulnerable.", 271, -11126);
                this.line(g, "Esc, taking damage, losing your ally or moving ends viewing.", 297, -6180666);
                this.line(g, "No distant chunk loading or spectator mode is used.", 325, -6180666);
                break;
            }
            case FEATS: {
                this.line(g, "Earn personal badges; choose one to display beside the name.", 122, -1445121);
                this.line(g, "Hunter: " + Math.min(25L, this.feats().m_128454_("Kills")) + " / 25 natural hostile kills since 1.0.7", 351, -11126);
                this.line(g, "Storykeeper: " + Integer.bitCount(this.feats().m_128451_("Stories") & 0x3F) + " / 3 distinct stories", 372, -6180666);
            }
        }
    }

    private String remaining(long deadline) {
        return Math.max(0L, (deadline - ClientJourney.now() + 19L) / 20L) + "s";
    }

    private String best(long ticks) {
        return ticks <= 0L ? "--" : String.format(Locale.ROOT, "%.1fs", (double)ticks / 20.0);
    }

    private void status(GuiGraphics g, int y) {
        if (this.s().m_128456_()) {
            this.line(g, "No interactive adventure active.", y, -6180666);
            return;
        }
        this.line(g, String.valueOf((Object)AdventureData.Kind.values()[this.s().m_128451_("Kind")]) + " / " + String.valueOf((Object)AdventureData.Phase.values()[this.s().m_128451_("Phase")]) + " / cleared " + this.s().m_128451_("Wave") + " / enemies " + this.s().m_128451_("Enemies"), y, -8135445);
    }

    public void m_88315_(GuiGraphics g, int x, int y, float partial) {
        this.m_280273_(g);
        double mx = (float)x / this.scale - this.left;
        double my = (float)y / this.scale - this.top;
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.left, this.top, 0.0f);
        MenuTheme.panel(g, 740, 450, 3);
        g.m_280509_(0, 0, 740, 2, -11126);
        this.text(g, "S O U L B O U N D   A R M Y  /  1.0.7", 18, 17, -6180666);
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_(18.0f, 35.0f, 0.0f);
        g.m_280168_().m_85841_(2.0f, 2.0f, 1.0f);
        this.text(g, "SOVEREIGN'S JOURNEY", 0, 0, -1445121);
        g.m_280168_().m_85849_();
        this.text(g, this.d().m_128454_("Completions") + " adventures completed", 485, 40, -11126);
        this.text(g, "Adventure awaits your army.", 485, 57, -6180666);
        g.m_280509_(18, 75, 720, 76, -11377795);
        g.m_280509_(161, 88, 162, 397, -13352619);
        this.text(g, AdventureService.pretty(this.page.name()).toUpperCase(Locale.ROOT), 172, 94, -8135445);
        this.body(g);
        for (int i = 0; i < this.controls.size(); ++i) {
            Control c = this.controls.get(i);
            boolean enabled = c.enabled.getAsBoolean();
            boolean hover = c.hit(mx, my) || this.focus == i;
            boolean nav = c.x == 18 && c.y == 116 + this.page.ordinal() * 28;
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 23, enabled ? (hover ? -12427398 : (nav ? -13415063 : -14535605)) : -15261908);
            g.m_280509_(c.x, c.y, c.x + c.w, c.y + 1, nav ? -11126 : -11509126);
            if (this.page == Page.BOSSES && c.x == 172 && c.y >= 177 && c.y <= 263 && (c.y - 177) % 43 == 0) {
                MenuTheme.art(g, 9 + (c.y - 177) / 43, c.x + 1, c.y + 1, 43, 21);
            }
            FantasyIcons.label(g, this.f_96547_, c.label, c.x, c.y, c.w, 23, enabled ? -1445121 : -9601902);
        }
        if (this.d().m_128441_("Notice")) {
            this.line(g, this.d().m_128461_("Notice"), 395, -11126);
        }
        this.text(g, ModLanguage.fit(this.f_96547_, ModLanguage.user(this.ally().m_128461_("Name")) + " / Lv." + this.ally().m_128451_("Level"), 350), 365, 420, -11126);
        this.text(g, "Saved with your world / play offline / previous adventures retained", 172, 440, -6180666);
        g.m_280168_().m_85849_();
    }

    public boolean m_6375_(double x, double y, int button) {
        if (button != 0) {
            return false;
        }
        double mx = x / (double)this.scale - (double)this.left;
        double my = y / (double)this.scale - (double)this.top;
        this.focus = -1;
        for (Control c : List.copyOf(this.controls)) {
            if (!c.hit(mx, my) || !c.enabled.getAsBoolean()) continue;
            c.action.run();
            return true;
        }
        return false;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (key == 258) {
            for (int i = 0; i < this.controls.size(); ++i) {
                this.focus = Math.floorMod(this.focus + (JourneyScreen.m_96638_() ? -1 : 1), this.controls.size());
                if (this.controls.get((int)this.focus).enabled.getAsBoolean()) break;
            }
            return true;
        }
        if ((key == 257 || key == 32) && this.focus >= 0 && this.focus < this.controls.size() && this.controls.get((int)this.focus).enabled.getAsBoolean()) {
            this.controls.get((int)this.focus).action.run();
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    public void m_7379_() {
        this.f_96541_.m_91152_((Screen)new AdventureScreen(this.selected));
    }

    private /* synthetic */ void lambda$build$45(JourneyState.Feat feat) {
        this.send(JourneyNetwork.Action.BADGE, feat.ordinal());
    }

    private /* synthetic */ boolean lambda$build$44(JourneyState.Feat feat) {
        return this.selected != null && JourneyState.earned(this.feats(), feat);
    }

    public static enum Page {
        CAMPAIGN,
        BOSSES,
        RIFTS,
        RALLY,
        ANGLING,
        CAMPFIRE,
        POWERS,
        COMPANION_VIEW,
        FEATS;

    }

    private record Control(int x, int y, int w, String label, BooleanSupplier enabled, Runnable action) {
        boolean hit(double mx, double my) {
            return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + 23);
        }
    }
}

