/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 */
package dev.shadowsovereign.sovereign;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;

public final class SovereignState {
    public static final String[] NAMES = new String[]{"The Loyal Blade", "Arcane Vanguard", "The Patient Guardian", "March of Shadows", "The Runebound Path", "A Crown of Echoes", "The Silent Strategist", "Bond Beyond Death", "The Twilight Hunt", "The Sovereign's Journey"};
    public static final Goal[][] ROUTES = new Goal[][]{{Goal.HUNT, Goal.FEED, Goal.HUNT}, {Goal.ART, Goal.HUNT, Goal.ART}, {Goal.FEED, Goal.HUNT, Goal.FEED}, {Goal.PLAN, Goal.HUNT, Goal.PLAN}, {Goal.HUNT, Goal.ART, Goal.PLAN}, {Goal.HUNT, Goal.WARFRONT, Goal.ART}, {Goal.PLAN, Goal.HUNT, Goal.WARFRONT}, {Goal.HUNT, Goal.FEED, Goal.ART}, {Goal.HUNT, Goal.HUNT, Goal.PLAN}, {Goal.FEED, Goal.ART, Goal.WARFRONT}};
    public static final String[] CRESTS = new String[]{"None", "Dusk Wanderer", "Ember Sentinel", "Tide Keeper", "Starforged", "Crowned Strategist", "Eclipse Sovereign"};
    public static final int[] UNLOCKS = new int[]{0, 1, 3, 5, 8, 12, 20};
    public long cycle;
    public int active = -1;
    public int stage;
    public int progress;
    public int completed;
    public int crest;
    public UUID champion;

    public int offer(int slot) {
        return Math.floorMod(this.cycle * 3L + (long)slot, NAMES.length);
    }

    public Goal goal() {
        return ROUTES[Math.max(0, Math.min(ROUTES.length - 1, this.active))][Math.max(0, Math.min(2, this.stage))];
    }

    public int target() {
        return switch (this.goal()) {
            default -> throw new IncompatibleClassChangeError();
            case Goal.HUNT -> 5 + this.stage * 2 + Math.min(10, this.completed / 3);
            case Goal.FEED, Goal.PLAN, Goal.WARFRONT -> 1;
            case Goal.ART -> 2;
        };
    }

    public boolean ready() {
        return this.active >= 0 && this.stage >= 3;
    }

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128356_("Cycle", this.cycle);
        n.m_128405_("Active", this.active);
        n.m_128405_("Stage", this.stage);
        n.m_128405_("Progress", this.progress);
        n.m_128405_("Completed", this.completed);
        n.m_128405_("Crest", this.crest);
        if (this.champion != null) {
            n.m_128362_("Champion", this.champion);
        }
        return n;
    }

    public static SovereignState load(CompoundTag n) {
        SovereignState s = new SovereignState();
        s.cycle = Math.max(0L, Math.min(0x1FFFFFFFFFFFFFFFL, n.m_128454_("Cycle")));
        s.completed = Math.max(0, n.m_128451_("Completed"));
        s.crest = Math.max(0, Math.min(CRESTS.length - 1, n.m_128451_("Crest")));
        if (s.completed < UNLOCKS[s.crest]) {
            s.crest = 0;
        }
        s.active = n.m_128441_("Active") ? n.m_128451_("Active") : -1;
        s.stage = Math.max(0, Math.min(3, n.m_128451_("Stage")));
        s.progress = Math.max(0, Math.min(1000, n.m_128451_("Progress")));
        UUID uUID = s.champion = n.m_128403_("Champion") ? n.m_128342_("Champion") : null;
        if (s.active < 0 || s.active >= NAMES.length || s.champion == null) {
            s.active = -1;
            s.progress = 0;
            s.stage = 0;
            s.champion = null;
        }
        return s;
    }

    public static enum Goal {
        HUNT,
        FEED,
        ART,
        PLAN,
        WARFRONT;

    }
}

