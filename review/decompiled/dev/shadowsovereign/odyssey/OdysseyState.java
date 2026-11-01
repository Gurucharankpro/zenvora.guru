/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 */
package dev.shadowsovereign.odyssey;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public final class OdysseyState {
    public boolean bookGiven;
    public boolean ended;
    public boolean pranks = true;
    public boolean welcomes = true;
    public boolean plotReady;
    public int plot = -1;
    public int buildCursor;
    public int seals;
    public int cycle;
    public int chapterClaims;
    public int selectedChapter;
    public int pinned = -1;
    public int artifact = -1;
    public int rep;
    public long seed;
    public long newestRead;
    public long voiceAt;
    public long aidAt;
    public UUID selected;
    public String returnDimension = "minecraft:overworld";
    public String observedKind = "";
    public BlockPos returnPos = BlockPos.f_121853_;
    public UUID observedSession;
    public final LinkedHashMap<String, Long> events = new LinkedHashMap();
    public final LinkedHashSet<String> earned = new LinkedHashSet();
    public final LinkedHashSet<String> grants = new LinkedHashSet();
    public final CompoundTag camp = new CompoundTag();
    public transient long viewUntil;
    public transient CompoundTag lastView;

    public long count(String key) {
        return this.events.getOrDefault(key, 0L);
    }

    public void add(String key, long count) {
        if (key.matches("[a-z0-9_.-]{1,80}") && (this.events.size() < 1024 || this.events.containsKey(key))) {
            this.events.put(key, Math.min(1000000000L, Math.max(0L, this.count(key) + count)));
        }
    }

    public void observe(String key, long value) {
        if (value > this.count(key)) {
            this.add(key, value - this.count(key));
        }
    }

    public boolean grant(String key) {
        return key.length() <= 100 && this.grants.size() < 8192 && this.grants.add(key);
    }

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128379_("BookGiven", this.bookGiven);
        n.m_128379_("Ended", this.ended);
        n.m_128379_("Pranks", this.pranks);
        n.m_128379_("Welcomes", this.welcomes);
        n.m_128379_("PlotReady", this.plotReady);
        n.m_128405_("Plot", this.plot);
        n.m_128405_("BuildCursor", this.buildCursor);
        n.m_128405_("Seals", this.seals);
        n.m_128405_("Cycle", this.cycle);
        n.m_128405_("Claims", this.chapterClaims);
        n.m_128405_("Chapter", this.selectedChapter);
        n.m_128405_("Pinned", this.pinned);
        n.m_128405_("Artifact", this.artifact);
        n.m_128405_("Reputation", this.rep);
        n.m_128356_("Seed", this.seed);
        n.m_128356_("Read", this.newestRead);
        n.m_128356_("VoiceAt", this.voiceAt);
        n.m_128356_("AidAt", this.aidAt);
        if (this.selected != null) {
            n.m_128362_("Selected", this.selected);
        }
        n.m_128359_("ReturnDimension", this.returnDimension);
        n.m_128356_("ReturnPos", this.returnPos.m_121878_());
        if (this.observedSession != null) {
            n.m_128362_("ObservedSession", this.observedSession);
        }
        n.m_128359_("ObservedKind", this.observedKind);
        CompoundTag es = new CompoundTag();
        this.events.forEach((arg_0, arg_1) -> ((CompoundTag)es).m_128356_(arg_0, arg_1));
        n.m_128365_("Events", (Tag)es);
        n.m_128365_("Earned", (Tag)OdysseyState.strings(this.earned));
        n.m_128365_("Grants", (Tag)OdysseyState.strings(this.grants));
        n.m_128365_("Camp", (Tag)this.camp.m_6426_());
        return n;
    }

    public static OdysseyState load(CompoundTag n) {
        OdysseyState s = new OdysseyState();
        s.bookGiven = n.m_128471_("BookGiven");
        s.ended = n.m_128471_("Ended");
        s.pranks = !n.m_128441_("Pranks") || n.m_128471_("Pranks");
        s.welcomes = !n.m_128441_("Welcomes") || n.m_128471_("Welcomes");
        s.plotReady = n.m_128471_("PlotReady");
        s.plot = n.m_128441_("Plot") ? OdysseyState.bound(n.m_128451_("Plot"), -1, 4095) : -1;
        s.buildCursor = OdysseyState.bound(n.m_128451_("BuildCursor"), 0, 1000000);
        s.seals = OdysseyState.bound(n.m_128451_("Seals"), 0, 1000000);
        s.cycle = OdysseyState.bound(n.m_128451_("Cycle"), 0, 1000000);
        s.chapterClaims = n.m_128451_("Claims") & 0xFFF;
        s.selectedChapter = OdysseyState.bound(n.m_128451_("Chapter"), 0, 11);
        s.pinned = n.m_128441_("Pinned") ? OdysseyState.bound(n.m_128451_("Pinned"), -1, 255) : -1;
        s.artifact = n.m_128441_("Artifact") ? OdysseyState.bound(n.m_128451_("Artifact"), -1, 23) : -1;
        s.rep = OdysseyState.bound(n.m_128451_("Reputation"), 0, 5);
        s.seed = n.m_128454_("Seed");
        s.newestRead = Math.max(0L, n.m_128454_("Read"));
        s.voiceAt = Math.max(0L, n.m_128454_("VoiceAt"));
        s.aidAt = Math.max(0L, n.m_128454_("AidAt"));
        s.selected = n.m_128403_("Selected") ? n.m_128342_("Selected") : null;
        String string = s.returnDimension = n.m_128461_("ReturnDimension").length() <= 256 ? n.m_128461_("ReturnDimension") : "minecraft:overworld";
        if (s.returnDimension.isBlank()) {
            s.returnDimension = "minecraft:overworld";
        }
        s.returnPos = BlockPos.m_122022_((long)n.m_128454_("ReturnPos"));
        s.observedSession = n.m_128403_("ObservedSession") ? n.m_128342_("ObservedSession") : null;
        s.observedKind = n.m_128461_("ObservedKind");
        CompoundTag es = n.m_128469_("Events");
        for (String key : es.m_128431_()) {
            if (s.events.size() >= 1024) continue;
            s.add(key, Math.max(0L, es.m_128454_(key)));
        }
        OdysseyState.read(n, "Earned", s.earned, 2048);
        OdysseyState.read(n, "Grants", s.grants, 8192);
        CompoundTag camp = n.m_128469_("Camp");
        for (String key : camp.m_128431_()) {
            if (s.camp.m_128440_() >= 128 || key.length() > 80) continue;
            s.camp.m_128365_(key, camp.m_128423_(key).m_6426_());
        }
        return s;
    }

    private static ListTag strings(Set<String> entries) {
        ListTag n = new ListTag();
        entries.forEach(s -> n.add((Object)StringTag.m_129297_((String)s)));
        return n;
    }

    private static void read(CompoundTag n, String key, Set<String> into, int max) {
        for (Tag tag : n.m_128437_(key, 8)) {
            if (into.size() >= max || !tag.m_7916_().matches("[a-zA-Z0-9_.:-]{1,100}")) continue;
            into.add(tag.m_7916_());
        }
    }

    public static int bound(int x, int min, int max) {
        return Math.max(min, Math.min(max, x));
    }

    public static final class Companion {
        public int story;
        public int club = -1;
        public int clubRank;
        public int gestures;
        public int job;
        public int house = -1;
        public int wins;
        public int clubTrips;
        public long giftAt;
        public long prankAt;
        public UUID leader;
        public BlockPos work = BlockPos.f_121853_;
        public final Set<String> gifts = new LinkedHashSet<String>();

        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128405_("Wins", this.wins);
            n.m_128405_("ClubTrips", this.clubTrips);
            n.m_128405_("Story", this.story);
            n.m_128405_("Club", this.club);
            n.m_128405_("ClubRank", this.clubRank);
            n.m_128405_("Gestures", this.gestures);
            n.m_128405_("Job", this.job);
            n.m_128405_("House", this.house);
            n.m_128356_("GiftAt", this.giftAt);
            n.m_128356_("PrankAt", this.prankAt);
            n.m_128356_("Work", this.work.m_121878_());
            if (this.leader != null) {
                n.m_128362_("Leader", this.leader);
            }
            n.m_128365_("Gifts", (Tag)OdysseyState.strings(this.gifts));
            return n;
        }

        public static Companion load(CompoundTag n) {
            Companion c = new Companion();
            c.wins = n.m_128451_("Wins") & 0x7FFFFFF;
            c.clubTrips = OdysseyState.bound(n.m_128451_("ClubTrips"), 0, 1000000000);
            c.story = OdysseyState.bound(n.m_128451_("Story"), 0, 3);
            c.club = n.m_128441_("Club") ? OdysseyState.bound(n.m_128451_("Club"), -1, 2) : -1;
            c.clubRank = OdysseyState.bound(n.m_128451_("ClubRank"), 0, 3);
            c.gestures = n.m_128451_("Gestures") & 7;
            c.job = OdysseyState.bound(n.m_128451_("Job"), 0, 3);
            c.house = n.m_128441_("House") ? OdysseyState.bound(n.m_128451_("House"), -1, 7) : -1;
            c.giftAt = Math.max(0L, n.m_128454_("GiftAt"));
            c.prankAt = Math.max(0L, n.m_128454_("PrankAt"));
            c.work = BlockPos.m_122022_((long)n.m_128454_("Work"));
            c.leader = n.m_128403_("Leader") ? n.m_128342_("Leader") : null;
            OdysseyState.read(n, "Gifts", c.gifts, 8);
            return c;
        }
    }
}

