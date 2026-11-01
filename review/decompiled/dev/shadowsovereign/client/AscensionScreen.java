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

import dev.shadowsovereign.ascension.AscensionService;
import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.ClientAscension;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.network.AscensionNetwork;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public final class AscensionScreen
extends Screen {
    private final List<Button> buttons = new ArrayList<Button>();
    private int page;
    private int ticks;
    private int focus = -1;
    private boolean hard;
    private boolean mirrorInitialized;
    private final UUID[] mirrors = new UUID[3];
    private float scale;
    private float left;
    private float top;
    private static final int W = 900;
    private static final int H = 520;
    private static final int GOLD = -1258618;
    private static final int WHITE = -791813;
    private static final int MUTED = -4805691;
    private static final String[] DETAILS = new String[]{"Infiltrate the sentinel patrols. Secure three relics, then return to your Sanctuary. Stay out of their forward vision. Damage raises the alarm!", "Capture three circles for eight seconds each. An enemy nearby contests the zone. Hold all three for twenty seconds. New attackers arrive every twenty seconds.", "Challenge echoes of up to three active companions. Their appearance and native abilities become your opposition. Your real companions keep all their progression.", "Draft three temporary allies from illustrated cards, then conquer three rooms ending with Gravewarden. Your permanent army waits safely in reserve.", "Match the requested rune to one of three caches. Bring a companion within five blocks and interact. Five correct rounds win; mistakes cost ten seconds."};

    public AscensionScreen() {
        super((Component)ModLanguage.component("Shadow Ascension"));
    }

    private CompoundTag d() {
        return ClientAscension.data;
    }

    private CompoundTag run() {
        return this.d().m_128469_("AscensionRun");
    }

    private CompoundTag session() {
        return this.d().m_128469_("Session");
    }

    private boolean free() {
        return this.session().m_128456_();
    }

    public void show(int index) {
        this.page = Math.floorMod(index, 5);
        this.build();
    }

    public void updated() {
        if (this.f_96541_ != null) {
            this.build();
        }
    }

    protected void m_7856_() {
        this.scale = Math.min(1.6f, Math.min(((float)this.f_96543_ - 14.0f) / 900.0f, ((float)this.f_96544_ - 14.0f) / 520.0f));
        this.left = ((float)this.f_96543_ / this.scale - 900.0f) / 2.0f;
        this.top = ((float)this.f_96544_ / this.scale - 520.0f) / 2.0f;
        this.build();
    }

    private void add(int x, int y, int w, String text, BooleanSupplier enabled, Runnable action) {
        this.buttons.add(new Button(x, y, w, text, enabled, action));
    }

    private void send(AscensionNetwork.Action a, int value) {
        if (this.f_96541_ != null && this.f_96541_.f_91074_ != null && this.f_96541_.m_91403_() != null) {
            AscensionNetwork.request(a, value, this.hard);
        }
    }

    private List<CompoundTag> active() {
        ArrayList<CompoundTag> list = new ArrayList<CompoundTag>();
        for (Tag tag : this.d().m_128437_("Roster", 10)) {
            CompoundTag n = (CompoundTag)tag;
            if (!n.m_128471_("Active") || n.m_128471_("Pending") || !n.m_128403_("Id")) continue;
            list.add(n);
        }
        return list;
    }

    private void mirrorNext(int slot) {
        List<CompoundTag> list = this.active();
        ArrayList ids = new ArrayList();
        ids.add(null);
        list.forEach(n -> ids.add(n.m_128342_("Id")));
        int index = ids.indexOf(this.mirrors[slot]);
        for (int i = 1; i <= ids.size(); ++i) {
            UUID next = (UUID)ids.get(Math.floorMod(index + i, ids.size()));
            boolean used = false;
            for (int a = 0; a < 3; ++a) {
                if (a == slot || next == null || !next.equals(this.mirrors[a])) continue;
                used = true;
            }
            if (used) continue;
            this.mirrors[slot] = next;
            break;
        }
        this.build();
    }

    private void build() {
        this.buttons.clear();
        this.add(24, 78, 196, "< Army", () -> true, () -> this.f_96541_.m_91152_((Screen)new ArmyScreen()));
        for (int i = 0; i < 5; ++i) {
            int index = i;
            this.add(24, 119 + i * 66, 196, AscensionService.NAMES[i], () -> true, () -> this.show(index));
        }
        this.add(24, 470, 196, "Motion: " + (MenuTheme.reducedMotion ? "Reduced" : "Full"), () -> true, () -> {
            MenuTheme.toggleMotion();
            this.build();
        });
        int unlock = this.page == 3 ? 5 : (this.page == 1 || this.page == 2 ? 3 : 1);
        this.add(250, 387, 626, (String)(this.free() ? "Begin " + AscensionService.NAMES[this.page] + " / needs " + unlock + " completions" : "Interactive activity already in progress"), () -> this.free() && this.d().m_128471_("HasAlly") && this.d().m_128471_("Near") && this.d().m_128454_("Completions") >= (long)unlock && (this.page != 2 || Arrays.stream(this.mirrors).anyMatch(Objects::nonNull)), () -> {
            if (this.page == 2) {
                AscensionNetwork.start(this.page, this.hard, Arrays.stream(this.mirrors).filter(Objects::nonNull).toList());
            } else {
                this.send(AscensionNetwork.Action.START, this.page);
            }
        });
        this.add(650, 78, 226, (this.free() ? this.hard : this.run().m_128471_("Hard")) ? "Difficulty: Challenging" : "Difficulty: Relaxed", this::free, () -> {
            this.hard = !this.hard;
            this.build();
        });
        this.add(250, 430, 145, "Pause", () -> !this.free() && this.session().m_128451_("Phase") == 0, () -> this.send(AscensionNetwork.Action.PAUSE, 0));
        this.add(410, 430, 145, "Resume", () -> !this.free() && this.session().m_128451_("Phase") == 1, () -> this.send(AscensionNetwork.Action.RESUME, 0));
        this.add(570, 430, 145, "Cancel / leave", () -> !this.free(), () -> this.send(AscensionNetwork.Action.CANCEL, 0));
        this.add(730, 430, 146, !this.run().m_128471_("Reward") && this.session().m_128451_("Phase") == 2 ? "Finish activity" : "Claim reward", () -> !this.free() && this.session().m_128451_("Phase") == 2, () -> this.send(AscensionNetwork.Action.CLAIM_REWARD, 0));
        if (this.page == 2 && this.free()) {
            int i;
            List<CompoundTag> available = this.active();
            if (!this.mirrorInitialized && !available.isEmpty()) {
                for (i = 0; i < Math.min(3, available.size()); ++i) {
                    this.mirrors[i] = available.get(i).m_128342_("Id");
                }
                this.mirrorInitialized = true;
            }
            for (i = 0; i < 3; ++i) {
                int slot = i;
                String label = "None";
                for (CompoundTag row : available) {
                    if (!row.m_128342_("Id").equals(this.mirrors[i])) continue;
                    label = row.m_128461_("Name");
                }
                this.add(250 + i * 210, 350, 202, "Echo: " + label + " >", () -> true, () -> this.mirrorNext(slot));
            }
        }
        if (!this.run().m_128456_() && this.run().m_128451_("Mode") == 3 && this.run().m_128451_("Picks") < 3 && this.session().m_128451_("Phase") == 0) {
            for (int i = 0; i < 3; ++i) {
                int card = i;
                int pool = this.run().m_128451_("Offer" + i);
                String name = AscensionService.POOL[Math.floorMod(pool, 6)].m_20676_().getString();
                this.add(250 + i * 210, 350, 202, "Draft " + name, () -> true, () -> this.send(AscensionNetwork.Action.DRAFT_PICK, card));
            }
        }
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        if (++this.ticks % 10 == 0) {
            this.send(AscensionNetwork.Action.VIEW, 0);
        }
    }

    private void text(GuiGraphics g, String value, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, value, x, y, color, false);
    }

    public void m_88315_(GuiGraphics g, int x, int y, float partial) {
        this.m_280273_(g);
        double mx = (float)x / this.scale - this.left;
        double my = (float)y / this.scale - this.top;
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.left, this.top, 0.0f);
        MenuTheme.panel(g, 900, 520, 4 + this.page);
        this.text(g, "SOULBOUND ARMY  /  1.2.0", 24, 20, -4805691);
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_(24.0f, 38.0f, 0.0f);
        g.m_280168_().m_85841_(2.0f, 2.0f, 1.0f);
        this.text(g, "SHADOW ASCENSION", 0, 0, -791813);
        g.m_280168_().m_85849_();
        this.text(g, this.d().m_128454_("Completions") + " adventures completed", 650, 31, -1258618);
        this.text(g, "Five new ways to command the night", 650, 49, -4805691);
        MenuTheme.art(g, 4 + this.page, 250, 114, 626, 150);
        g.m_280509_(250, 225, 876, 264, -1525675237);
        this.text(g, AscensionService.NAMES[this.page].toUpperCase(Locale.ROOT), 267, 242, -1258618);
        int lineY = 278;
        for (FormattedCharSequence line : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(DETAILS[this.page]), 610)) {
            ModLanguage.draw(g, this.f_96547_, line, 258, lineY, -791813, false);
            lineY += 13;
        }
        if (this.free()) {
            int need;
            int n = this.page == 3 ? 5 : (need = this.page == 1 || this.page == 2 ? 3 : 1);
            String hint = !this.d().m_128471_("Near") ? "Return near your bound Sanctuary to begin." : (!this.d().m_128471_("HasAlly") ? "Summon a companion within 32 blocks to begin." : (this.d().m_128454_("Completions") < (long)need ? "Complete " + need + " adventures to unlock this activity." : "Ready / one completion reward per activity per saved day."));
            this.text(g, hint, 258, 328, -1258618);
        }
        if (!this.run().m_128456_()) {
            Object status = "";
            int mode = this.run().m_128451_("Mode");
            switch (mode) {
                case 0: {
                    status = "Alarm " + this.run().m_128451_("Alarm") + "% / relics " + Integer.bitCount(this.run().m_128451_("Collected")) + " / 3";
                    break;
                }
                case 1: {
                    status = "Zones " + this.run().m_128451_("Capture0") / 20 + "s | " + this.run().m_128451_("Capture1") / 20 + "s | " + this.run().m_128451_("Capture2") / 20 + "s / hold " + this.run().m_128451_("Hold") / 20 + " / 20s";
                    break;
                }
                case 2: {
                    status = "Echoes remaining: " + this.session().m_128451_("Enemies");
                    break;
                }
                case 3: {
                    status = "Drafted " + this.run().m_128451_("Picks") + " / 3 / rooms cleared " + this.session().m_128451_("Wave") + " / 3";
                    break;
                }
                case 4: {
                    status = "Requested rune: " + AscensionService.RUNES[Math.floorMod(this.run().m_128451_("Rune"), 3)] + " / rounds " + this.session().m_128451_("Wave") + " / 5";
                }
            }
            this.text(g, ModLanguage.fit(this.f_96547_, (String)status, 610), 258, 328, -1258618);
            if (mode != 3) {
                this.text(g, "Time remaining: " + (this.run().m_128454_("Remaining") + 19L) / 20L + "s", 258, 351, -4805691);
            }
        }
        for (int i = 0; i < this.buttons.size(); ++i) {
            Button b = this.buttons.get(i);
            boolean nav = b.x == 24 && b.y >= 119 && b.y < 449;
            boolean hover = mx >= (double)b.x && mx < (double)(b.x + b.w) && my >= (double)b.y && my < (double)(b.y + 29) || this.focus == i;
            g.m_280509_(b.x, b.y, b.x + b.w, b.y + 29, b.enabled.getAsBoolean() ? (hover ? -10667144 : -13819589) : -14936283);
            if (nav) {
                int index = (b.y - 119) / 66;
                MenuTheme.art(g, 4 + index, b.x, b.y, b.w, 29);
                g.m_280509_(b.x, b.y, b.x + b.w, b.y + 29, this.page == index ? -2007749776 : -1341454570);
            }
            g.m_280509_(b.x, b.y, b.x + b.w, b.y + 1, hover ? -1258618 : -9614457);
            FantasyIcons.label(g, this.f_96547_, b.text, b.x, b.y, b.w, 29, b.enabled.getAsBoolean() ? -791813 : -7964526);
        }
        this.text(g, "In-world: " + ClientAscension.INTERACT.m_90863_().getString() + " interacts / " + ClientAscension.CLAIM.m_90863_().getString() + " claims souls / Esc returns", 250, 479, -4805691);
        this.text(g, "Private adventures / saved with your world / one daily reward per activity", 250, 499, -4805691);
        g.m_280168_().m_85849_();
    }

    public boolean m_6375_(double x, double y, int button) {
        if (button != 0) {
            return false;
        }
        double mx = x / (double)this.scale - (double)this.left;
        double my = y / (double)this.scale - (double)this.top;
        for (Button b : List.copyOf(this.buttons)) {
            if (!(mx >= (double)b.x) || !(mx < (double)(b.x + b.w)) || !(my >= (double)b.y) || !(my < (double)(b.y + 29)) || !b.enabled.getAsBoolean()) continue;
            b.action.run();
            return true;
        }
        return false;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (key == 258) {
            for (int i = 0; i < this.buttons.size(); ++i) {
                this.focus = Math.floorMod(this.focus + (AscensionScreen.m_96638_() ? -1 : 1), this.buttons.size());
                if (this.buttons.get((int)this.focus).enabled.getAsBoolean()) break;
            }
            return true;
        }
        if (key == 257 && this.focus >= 0 && this.buttons.get((int)this.focus).enabled.getAsBoolean()) {
            this.buttons.get((int)this.focus).action.run();
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    public void m_7379_() {
        this.f_96541_.m_91152_((Screen)new ArmyScreen());
    }

    private record Button(int x, int y, int w, String text, BooleanSupplier enabled, Runnable action) {
    }
}

