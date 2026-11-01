/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 */
package dev.shadowsovereign.vigil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class VigilState {
    public int offerings;
    public int watchTeam;
    public int watchInterval = 1200;
    public boolean watch;
    public boolean chatter = true;
    public long nextWatch;
    public long nextVoice;
    public UUID selected;
    public UUID linkA;
    public UUID linkB;
    public UUID ping;
    public CompoundTag source = new CompoundTag();
    public CompoundTag destination = new CompoundTag();
    public CompoundTag crown = new CompoundTag();
    public int circuitBest = Integer.MAX_VALUE;
    public int rhythmBest;
    public int diceBest;
    public int mazeBest = Integer.MAX_VALUE;
    public int ferrymanWins;
    public final Map<String, Long> ready = new LinkedHashMap<String, Long>();
    public transient CompoundTag lastView;
    public transient CompoundTag job = new CompoundTag();
    public transient CompoundTag meteor = new CompoundTag();
    public transient CompoundTag snare = new CompoundTag();
    public transient CompoundTag lens = new CompoundTag();
    public transient UUID reply;
    public transient long replyAt;
    public transient long viewUntil;
    public transient long crownRequest;
    public transient long lastThreatAt;
    public transient UUID threat;

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128405_("Offerings", this.offerings);
        n.m_128379_("Watch", this.watch);
        n.m_128379_("Chatter", this.chatter);
        n.m_128405_("WatchTeam", this.watchTeam);
        n.m_128405_("WatchInterval", this.watchInterval);
        n.m_128356_("NextWatch", this.nextWatch);
        n.m_128356_("NextVoice", this.nextVoice);
        for (Map.Entry<String, Optional<UUID>> entry : Map.of("Selected", Optional.ofNullable(this.selected), "LinkA", Optional.ofNullable(this.linkA), "LinkB", Optional.ofNullable(this.linkB), "Ping", Optional.ofNullable(this.ping)).entrySet()) {
            entry.getValue().ifPresent(id -> n.m_128362_((String)entry.getKey(), id));
        }
        n.m_128365_("Source", (Tag)this.source.m_6426_());
        n.m_128365_("Destination", (Tag)this.destination.m_6426_());
        n.m_128365_("Crown", (Tag)this.crown.m_6426_());
        n.m_128405_("CircuitBest", this.circuitBest);
        n.m_128405_("RhythmBest", this.rhythmBest);
        n.m_128405_("DiceBest", this.diceBest);
        n.m_128405_("MazeBest", this.mazeBest);
        n.m_128405_("FerrymanWins", this.ferrymanWins);
        CompoundTag cds = new CompoundTag();
        this.ready.forEach((arg_0, arg_1) -> ((CompoundTag)cds).m_128356_(arg_0, arg_1));
        n.m_128365_("Ready", (Tag)cds);
        return n;
    }

    public static VigilState load(CompoundTag n) {
        VigilState v = new VigilState();
        v.offerings = Math.max(0, Math.min(4096, n.m_128451_("Offerings")));
        v.watch = n.m_128471_("Watch");
        v.chatter = !n.m_128441_("Chatter") || n.m_128471_("Chatter");
        v.watchTeam = Math.floorMod(n.m_128451_("WatchTeam"), 5);
        v.watchInterval = switch (n.m_128451_("WatchInterval")) {
            case 6000 -> 6000;
            case 12000 -> 12000;
            default -> 1200;
        };
        v.nextWatch = Math.max(0L, n.m_128454_("NextWatch"));
        v.nextVoice = Math.max(0L, n.m_128454_("NextVoice"));
        v.selected = VigilState.id(n, "Selected");
        v.linkA = VigilState.id(n, "LinkA");
        v.linkB = VigilState.id(n, "LinkB");
        v.ping = VigilState.id(n, "Ping");
        v.source = n.m_128469_("Source").m_6426_();
        v.destination = n.m_128469_("Destination").m_6426_();
        v.crown = n.m_128469_("Crown").m_6426_();
        if (v.crown.m_128437_("Members", 10).size() > 1024) {
            v.crown = new CompoundTag();
        }
        v.circuitBest = n.m_128441_("CircuitBest") ? Math.max(0, n.m_128451_("CircuitBest")) : Integer.MAX_VALUE;
        v.rhythmBest = Math.max(0, Math.min(120, n.m_128451_("RhythmBest")));
        v.diceBest = Math.max(0, Math.min(100, n.m_128451_("DiceBest")));
        v.mazeBest = n.m_128441_("MazeBest") ? Math.max(0, n.m_128451_("MazeBest")) : Integer.MAX_VALUE;
        v.ferrymanWins = Math.max(0, n.m_128451_("FerrymanWins"));
        CompoundTag cds = n.m_128469_("Ready");
        for (String key : List.of("bandage", "horn", "meteor", "bash", "volley", "feast", "link", "tow")) {
            if (!cds.m_128441_(key)) continue;
            v.ready.put(key, Math.max(0L, cds.m_128454_(key)));
        }
        return v;
    }

    public static UUID id(CompoundTag n, String key) {
        return n.m_128403_(key) ? n.m_128342_(key) : null;
    }

    public static final class Companion {
        public int freeUsed;
        public int deaths;
        public int fee = 1;
        public boolean post;
        public boolean suspended;
        public long postPos;
        public long lastSeen = -1L;
        public long separatedAt = -1L;
        public long separatedWall;
        public long nextAlarm;
        public String postDimension = "minecraft:overworld";

        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128405_("FreeUsed", this.freeUsed);
            n.m_128405_("Deaths", this.deaths);
            n.m_128405_("Fee", this.fee);
            n.m_128379_("Post", this.post);
            n.m_128379_("Suspended", this.suspended);
            n.m_128356_("PostPos", this.postPos);
            n.m_128359_("PostDimension", this.postDimension);
            n.m_128356_("LastSeen", this.lastSeen);
            n.m_128356_("SeparatedAt", this.separatedAt);
            n.m_128356_("SeparatedWall", this.separatedWall);
            n.m_128356_("NextAlarm", this.nextAlarm);
            return n;
        }

        public static Companion load(CompoundTag n) {
            Companion c = new Companion();
            c.freeUsed = Math.max(0, Math.min(5, n.m_128451_("FreeUsed")));
            c.deaths = Math.max(0, n.m_128451_("Deaths"));
            c.fee = Math.max(1, Math.min(1000000, n.m_128451_("Fee")));
            c.post = n.m_128471_("Post");
            c.suspended = n.m_128471_("Suspended");
            c.postPos = n.m_128454_("PostPos");
            c.postDimension = n.m_128441_("PostDimension") ? n.m_128461_("PostDimension") : "minecraft:overworld";
            c.lastSeen = n.m_128441_("LastSeen") ? n.m_128454_("LastSeen") : -1L;
            c.separatedAt = n.m_128441_("SeparatedAt") ? n.m_128454_("SeparatedAt") : -1L;
            c.separatedWall = Math.max(0L, n.m_128454_("SeparatedWall"));
            c.nextAlarm = Math.max(0L, n.m_128454_("NextAlarm"));
            return c;
        }
    }
}

