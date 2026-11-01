/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 */
package dev.shadowsovereign.legacy;

import dev.shadowsovereign.adventure.AdventureData;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public final class LegacyState {
    public int charge;
    public int bestWave;
    public long exchangeAt;
    public long burstAt;
    public long duoAt;
    public long emoteAt;
    public long nextChatter;
    public long vaultClaimedDay = -1L;
    public UUID first;
    public UUID second;
    public boolean chatter = true;
    public final EnumMap<Stat, Long> stats = new EnumMap(Stat.class);
    public final EnumSet<Achievement> achievements = EnumSet.noneOf(Achievement.class);
    public final List<CompoundTag> results = new ArrayList<CompoundTag>();
    public final CompoundTag[] loadouts = new CompoundTag[3];
    public transient UUID hitTarget;
    public transient UUID hitMember;
    public transient long hitAt;
    public transient boolean bursting;

    public long stat(Stat s) {
        return this.stats.getOrDefault((Object)s, 0L);
    }

    public void increment(Stat s) {
        this.stats.put(s, this.stat(s) == Long.MAX_VALUE ? Long.MAX_VALUE : this.stat(s) + 1L);
    }

    public void resetWindow() {
        this.hitTarget = null;
        this.hitMember = null;
        this.hitAt = 0L;
    }

    public void result(String kind, String outcome, int waves, int integrity, long time) {
        CompoundTag n = new CompoundTag();
        n.m_128359_("Kind", kind);
        n.m_128359_("Outcome", outcome);
        n.m_128405_("Waves", waves);
        n.m_128405_("Integrity", integrity);
        n.m_128356_("Time", time);
        this.results.add(0, n);
        while (this.results.size() > 20) {
            this.results.remove(this.results.size() - 1);
        }
    }

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128405_("Charge", this.charge);
        n.m_128405_("BestWave", this.bestWave);
        n.m_128356_("ExchangeAt", this.exchangeAt);
        n.m_128356_("BurstAt", this.burstAt);
        n.m_128356_("DuoAt", this.duoAt);
        n.m_128356_("EmoteAt", this.emoteAt);
        n.m_128356_("NextChatter", this.nextChatter);
        n.m_128356_("VaultClaimedDay", this.vaultClaimedDay);
        n.m_128379_("Chatter", this.chatter);
        if (this.first != null) {
            n.m_128362_("First", this.first);
        }
        if (this.second != null) {
            n.m_128362_("Second", this.second);
        }
        CompoundTag counters = new CompoundTag();
        this.stats.forEach((k, v) -> counters.m_128356_(k.name(), v.longValue()));
        n.m_128365_("Stats", (Tag)counters);
        ListTag earned = new ListTag();
        this.achievements.forEach(a -> earned.add((Object)StringTag.m_129297_((String)a.name())));
        n.m_128365_("Achievements", (Tag)earned);
        ListTag history = new ListTag();
        this.results.forEach(a -> history.add((Object)a.m_6426_()));
        n.m_128365_("Results", (Tag)history);
        for (int i = 0; i < 3; ++i) {
            if (this.loadouts[i] == null) continue;
            n.m_128365_("Loadout" + i, (Tag)this.loadouts[i].m_6426_());
        }
        return n;
    }

    public static LegacyState load(CompoundTag n) {
        LegacyState s = new LegacyState();
        s.charge = Math.max(0, Math.min(10, n.m_128451_("Charge")));
        s.bestWave = Math.max(0, n.m_128451_("BestWave"));
        s.exchangeAt = Math.max(0L, n.m_128454_("ExchangeAt"));
        s.burstAt = Math.max(0L, n.m_128454_("BurstAt"));
        s.duoAt = Math.max(0L, n.m_128454_("DuoAt"));
        s.emoteAt = Math.max(0L, n.m_128454_("EmoteAt"));
        s.nextChatter = Math.max(0L, n.m_128454_("NextChatter"));
        s.vaultClaimedDay = n.m_128441_("VaultClaimedDay") ? n.m_128454_("VaultClaimedDay") : -1L;
        boolean bl = s.chatter = !n.m_128441_("Chatter") || n.m_128471_("Chatter");
        if (n.m_128403_("First") && n.m_128403_("Second") && !n.m_128342_("First").equals(n.m_128342_("Second"))) {
            s.first = n.m_128342_("First");
            s.second = n.m_128342_("Second");
        }
        for (Stat stat : Stat.values()) {
            s.stats.put(stat, Math.max(0L, n.m_128469_("Stats").m_128454_(stat.name())));
        }
        for (Tag tag : n.m_128437_("Achievements", 8)) {
            try {
                s.achievements.add(Achievement.valueOf(tag.m_7916_()));
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
        for (Tag tag : n.m_128437_("Results", 10)) {
            if (s.results.size() >= 20) continue;
            CompoundTag r = (CompoundTag)tag;
            String kind = r.m_128461_("Kind");
            String outcome = r.m_128461_("Outcome");
            if (kind.length() > 32 || outcome.length() > 32) continue;
            s.results.add(r.m_6426_());
        }
        for (int i = 0; i < 3; ++i) {
            if (!n.m_128425_("Loadout" + i, 10)) continue;
            CompoundTag source = n.m_128469_("Loadout" + i);
            CompoundTag profile = new CompoundTag();
            profile.m_128359_("Name", LegacyState.plain(source.m_128461_("Name"), 24));
            profile.m_128359_("Scope", source.m_128461_("Scope"));
            ListTag entries = new ListTag();
            for (Tag tag : source.m_128437_("Entries", 10)) {
                if (entries.size() >= 1024 || !((CompoundTag)tag).m_128403_("Id")) continue;
                entries.add((Object)((CompoundTag)tag).m_6426_());
            }
            profile.m_128365_("Entries", (Tag)entries);
            s.loadouts[i] = profile;
        }
        return s;
    }

    public static String plain(String input, int limit) {
        StringBuilder b = new StringBuilder();
        input.codePoints().filter(c -> !Character.isISOControl(c) && c != 167).limit(limit).forEach(b::appendCodePoint);
        return b.toString().strip();
    }

    public static enum Stat {
        KILLS,
        EXCHANGES,
        BURSTS,
        DUOS,
        SIEGES,
        VAULTS;

    }

    public static enum Achievement {
        WANDERER("Wanderer", Stat.EXCHANGES, 10, AdventureData.Title.WANDERER),
        ECLIPSE_BEARER("Eclipse Bearer", Stat.BURSTS, 10, AdventureData.Title.ECLIPSE_BEARER),
        TWIN_FANGS("Twin Fangs", Stat.DUOS, 25, AdventureData.Title.TWIN_FANGS),
        UNDYING("Undying", null, 10, AdventureData.Title.UNDYING),
        BULWARK("Bulwark", Stat.SIEGES, 1, AdventureData.Title.BULWARK),
        RUNEKEEPER("Runekeeper", Stat.VAULTS, 3, AdventureData.Title.RUNEKEEPER);

        public final String label;
        public final Stat stat;
        public final int goal;
        public final AdventureData.Title title;

        private Achievement(String label, Stat stat, int goal, AdventureData.Title title) {
            this.label = label;
            this.stat = stat;
            this.goal = goal;
            this.title = title;
        }
    }

    public static enum Personality {
        BRAVE,
        CURIOUS,
        QUIET;


        public static Personality of(UUID id) {
            return Personality.values()[Math.floorMod(id.hashCode(), Personality.values().length)];
        }
    }
}

