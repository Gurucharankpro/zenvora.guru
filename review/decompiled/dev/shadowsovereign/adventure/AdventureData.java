/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.saveddata.SavedData
 */
package dev.shadowsovereign.adventure;

import dev.shadowsovereign.expansion.RemovedContent;
import dev.shadowsovereign.journey.JourneyState;
import dev.shadowsovereign.legacy.LegacyRun;
import dev.shadowsovereign.legacy.LegacyState;
import dev.shadowsovereign.mythic.MythicState;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.realm.RealmState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public final class AdventureData
extends SavedData {
    public final Map<UUID, Journal> owners = new LinkedHashMap<UUID, Journal>();

    public static AdventureData get(MinecraftServer server) {
        return (AdventureData)server.m_129783_().m_8895_().m_164861_(AdventureData::load, AdventureData::new, "shadowsovereign_adventures");
    }

    public Journal journal(UUID owner) {
        return this.owners.computeIfAbsent(owner, k -> new Journal());
    }

    public CompoundTag m_7176_(CompoundTag root) {
        ListTag entries = new ListTag();
        this.owners.forEach((id, j) -> {
            CompoundTag n = j.save();
            n.m_128362_("Owner", id);
            entries.add((Object)n);
        });
        root.m_128405_("Version", 12);
        root.m_128365_("Journals", (Tag)entries);
        return root;
    }

    public static AdventureData load(CompoundTag compoundTag) {
        CompoundTag root;
        RemovedContent.adventures(compoundTag);
        AdventureData d = new AdventureData();
        for (Tag t : root.m_128437_("Journals", 10)) {
            CompoundTag n = (CompoundTag)t;
            if (!n.m_128403_("Owner")) continue;
            d.owners.put(n.m_128342_("Owner"), Journal.load(n));
        }
        return d;
    }

    private static ListTag strings(Collection<String> values) {
        ListTag a = new ListTag();
        values.forEach(s -> a.add((Object)StringTag.m_129297_((String)s)));
        return a;
    }

    private static void readStrings(CompoundTag n, String key, Set<String> into, int max) {
        for (Tag t : n.m_128437_(key, 8)) {
            if (into.size() >= max || t.m_7916_().length() > 256) continue;
            into.add(t.m_7916_());
        }
    }

    public static <E extends Enum<E>> E read(Class<E> type, String name, E fallback) {
        try {
            return Enum.valueOf(type, name);
        }
        catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public static final class Journal {
        public MythicState mythic = new MythicState();
        public boolean bound;
        public String dimension = "minecraft:overworld";
        public BlockPos sanctuary = BlockPos.f_121853_;
        public long completions;
        public long cacheCycle = -1L;
        public boolean huntClaimed;
        public String cacheDimension = "";
        public final Set<String> discoveries = new LinkedHashSet<String>();
        public final EnumSet<Trail> trails = EnumSet.of(Trail.DEFAULT);
        public final EnumSet<Title> titles = EnumSet.of(Title.NONE);
        public final LinkedHashSet<String> claimedOffers = new LinkedHashSet();
        public final Map<UUID, Lore> lore = new LinkedHashMap<UUID, Lore>();
        public UUID bond;
        public final List<Expedition> expeditions = new ArrayList<Expedition>();
        public final List<Contract> contracts = new ArrayList<Contract>();
        public final List<Cache> caches = new ArrayList<Cache>();
        public final LinkedHashSet<UUID> rewarded = new LinkedHashSet();
        public LegacyState legacy = new LegacyState();
        public JourneyState journey = new JourneyState();
        public RealmState livingRealm = new RealmState();
        public OdysseyState odyssey = new OdysseyState();
        public CompoundTag ascension = new CompoundTag();
        public Nemesis nemesis;
        public Session session;

        public Lore lore(UUID id) {
            return this.lore.computeIfAbsent(id, k -> new Lore());
        }

        public boolean unlocked(Relic r) {
            return r == Relic.NONE || this.completions >= (long)(r == Relic.PATHFINDER ? 1 : (r == Relic.SEEKER ? 3 : 5));
        }

        public boolean reward(UUID id) {
            if (this.rewarded.contains(id)) {
                return false;
            }
            if (id.equals(this.odyssey.observedSession) && this.odyssey.grant("clear:" + String.valueOf(id))) {
                this.odyssey.add("clear." + this.odyssey.observedKind, 1L);
            }
            this.rewarded.add(id);
            while (this.rewarded.size() > 256) {
                this.rewarded.remove(this.rewarded.iterator().next());
            }
            if (this.completions < Long.MAX_VALUE) {
                ++this.completions;
            }
            if (this.completions >= 3L) {
                this.trails.add(Trail.GOLD);
            }
            if (this.completions >= 10L) {
                this.trails.add(Trail.VOID);
            }
            return true;
        }

        CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128379_("Bound", this.bound);
            n.m_128359_("Dimension", this.dimension);
            n.m_128356_("Sanctuary", this.sanctuary.m_121878_());
            n.m_128356_("Completions", this.completions);
            n.m_128356_("CacheCycle", this.cacheCycle);
            n.m_128379_("HuntClaimed", this.huntClaimed);
            n.m_128359_("CacheDimension", this.cacheDimension);
            n.m_128365_("Discoveries", (Tag)AdventureData.strings(this.discoveries));
            n.m_128365_("Trails", (Tag)AdventureData.strings(this.trails.stream().map(Enum::name).toList()));
            n.m_128365_("Titles", (Tag)AdventureData.strings(this.titles.stream().map(Enum::name).toList()));
            n.m_128365_("ClaimedOffers", (Tag)AdventureData.strings(this.claimedOffers));
            if (this.bond != null) {
                n.m_128362_("Bond", this.bond);
            }
            ListTag ls = new ListTag();
            this.lore.forEach((id, l) -> {
                CompoundTag a = l.save();
                a.m_128362_("Id", id);
                ls.add((Object)a);
            });
            n.m_128365_("Lore", (Tag)ls);
            ListTag es = new ListTag();
            this.expeditions.forEach(e -> es.add((Object)e.save()));
            n.m_128365_("Expeditions", (Tag)es);
            ListTag cs = new ListTag();
            this.contracts.forEach(c -> cs.add((Object)c.save()));
            n.m_128365_("Contracts", (Tag)cs);
            ListTag cache = new ListTag();
            this.caches.forEach(c -> cache.add((Object)c.save()));
            n.m_128365_("Caches", (Tag)cache);
            ListTag rs = new ListTag();
            this.rewarded.forEach(id -> {
                CompoundTag a = new CompoundTag();
                a.m_128362_("Id", id);
                rs.add((Object)a);
            });
            n.m_128365_("Rewarded", (Tag)rs);
            n.m_128365_("Mythic", (Tag)this.mythic.save());
            n.m_128365_("LivingRealm", (Tag)this.livingRealm.save());
            n.m_128365_("Odyssey", (Tag)this.odyssey.save());
            n.m_128365_("Legacy", (Tag)this.legacy.save());
            n.m_128365_("Journey", (Tag)this.journey.save());
            n.m_128365_("Ascension", (Tag)this.ascension.m_6426_());
            if (this.nemesis != null) {
                n.m_128365_("Nemesis", (Tag)this.nemesis.save());
            }
            if (this.session != null) {
                n.m_128365_("Session", (Tag)this.session.save());
            }
            return n;
        }

        static Journal load(CompoundTag n) {
            Journal j = new Journal();
            j.mythic = MythicState.load(n.m_128469_("Mythic"));
            j.livingRealm = RealmState.load(n.m_128469_("LivingRealm"));
            j.odyssey = OdysseyState.load(n.m_128469_("Odyssey"));
            j.ascension = n.m_128469_("Ascension").m_6426_();
            j.journey = JourneyState.load(n.m_128469_("Journey"));
            j.legacy = LegacyState.load(n.m_128469_("Legacy"));
            j.bound = n.m_128471_("Bound");
            j.dimension = n.m_128461_("Dimension");
            j.sanctuary = BlockPos.m_122022_((long)n.m_128454_("Sanctuary"));
            j.completions = Math.max(0L, n.m_128454_("Completions"));
            j.cacheCycle = n.m_128441_("CacheCycle") ? n.m_128454_("CacheCycle") : -1L;
            j.huntClaimed = n.m_128471_("HuntClaimed");
            j.cacheDimension = n.m_128461_("CacheDimension");
            AdventureData.readStrings(n, "Discoveries", j.discoveries, 8192);
            LinkedHashSet<String> strings = new LinkedHashSet<String>();
            AdventureData.readStrings(n, "Trails", strings, Trail.values().length);
            strings.forEach(v -> j.trails.add(AdventureData.read(Trail.class, v, Trail.DEFAULT)));
            strings.clear();
            AdventureData.readStrings(n, "Titles", strings, Title.values().length);
            strings.forEach(v -> j.titles.add(AdventureData.read(Title.class, v, Title.NONE)));
            if (n.m_128403_("Bond")) {
                j.bond = n.m_128342_("Bond");
            }
            AdventureData.readStrings(n, "ClaimedOffers", j.claimedOffers, 96);
            for (Tag t : n.m_128437_("Lore", 10)) {
                CompoundTag a;
                if (j.lore.size() >= 1024 || !(a = (CompoundTag)t).m_128403_("Id")) continue;
                j.lore.put(a.m_128342_("Id"), Lore.load(a));
            }
            for (Tag t : n.m_128437_("Expeditions", 10)) {
                if (j.expeditions.size() >= 2) continue;
                try {
                    j.expeditions.add(Expedition.load((CompoundTag)t));
                }
                catch (RuntimeException runtimeException) {}
            }
            for (Tag t : n.m_128437_("Contracts", 10)) {
                if (j.contracts.size() >= 3) continue;
                try {
                    j.contracts.add(Contract.load((CompoundTag)t));
                }
                catch (RuntimeException runtimeException) {}
            }
            for (Tag t : n.m_128437_("Caches", 10)) {
                if (j.caches.size() >= 3) continue;
                try {
                    j.caches.add(Cache.load((CompoundTag)t));
                }
                catch (RuntimeException runtimeException) {}
            }
            for (Tag t : n.m_128437_("Rewarded", 10)) {
                if (j.rewarded.size() >= 256 || !((CompoundTag)t).m_128403_("Id")) continue;
                j.rewarded.add(((CompoundTag)t).m_128342_("Id"));
            }
            if (n.m_128441_("Nemesis")) {
                try {
                    j.nemesis = Nemesis.load(n.m_128469_("Nemesis"));
                }
                catch (RuntimeException runtimeException) {
                    // empty catch block
                }
            }
            if (n.m_128441_("Session")) {
                try {
                    j.session = Session.load(n.m_128469_("Session"));
                }
                catch (RuntimeException runtimeException) {
                    // empty catch block
                }
            }
            return j;
        }
    }

    public static final class Session {
        public UUID id = UUID.randomUUID();
        public Kind kind = Kind.TRIAL;
        public Phase phase = Phase.PAUSED;
        public String dimension = "";
        public BlockPos anchor = BlockPos.f_121853_;
        public LegacyRun legacy = new LegacyRun();
        public CompoundTag journey = new CompoundTag();
        public CompoundTag warfront = new CompoundTag();
        public CompoundTag eclipse = new CompoundTag();
        public CompoundTag royal = new CompoundTag();
        public CompoundTag vigil = new CompoundTag();
        public int tier;
        public int wave;
        public int epoch = 1;
        public long nextWave;
        public boolean initialized;
        public boolean wardenUsed;
        public final Map<UUID, Npc> npcs = new LinkedHashMap<UUID, Npc>();
        public final Set<UUID> participants = new LinkedHashSet<UUID>();

        public int maxWaves() {
            return switch (this.kind) {
                case Kind.ESCORT -> 2;
                case Kind.GAUNTLET -> Integer.MAX_VALUE;
                case Kind.SIEGE -> 6;
                case Kind.VAULT, Kind.RIFT, Kind.DRAFT -> 3;
                case Kind.RALLY -> 6;
                case Kind.BOSS, Kind.CAMP, Kind.HEIST, Kind.DOMINATION, Kind.MIRROR, Kind.WARFRONT -> 1;
                case Kind.RUSH -> 5;
                case Kind.ANGLING -> 0;
                default -> this.tier + 3;
            };
        }

        CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128362_("Id", this.id);
            n.m_128365_("LegacyRun", (Tag)this.legacy.save());
            n.m_128365_("JourneyRun", (Tag)this.journey.m_6426_());
            n.m_128365_("WarfrontRun", (Tag)this.warfront.m_6426_());
            n.m_128365_("EclipseRun", (Tag)this.eclipse.m_6426_());
            n.m_128365_("RoyalRun", (Tag)this.royal.m_6426_());
            n.m_128365_("VigilRun", (Tag)this.vigil.m_6426_());
            n.m_128359_("Kind", this.kind.name());
            n.m_128359_("Phase", this.phase.name());
            n.m_128359_("Dimension", this.dimension);
            n.m_128356_("Anchor", this.anchor.m_121878_());
            n.m_128405_("Tier", this.tier);
            n.m_128405_("Wave", this.wave);
            n.m_128405_("Epoch", this.epoch);
            n.m_128356_("NextWave", this.nextWave);
            n.m_128379_("Initialized", this.initialized);
            n.m_128379_("WardenUsed", this.wardenUsed);
            ListTag entries = new ListTag();
            this.npcs.values().forEach(a -> entries.add((Object)a.save()));
            n.m_128365_("Npcs", (Tag)entries);
            ListTag ids = new ListTag();
            this.participants.forEach(id -> {
                CompoundTag t = new CompoundTag();
                t.m_128362_("Id", id);
                ids.add((Object)t);
            });
            n.m_128365_("Participants", (Tag)ids);
            return n;
        }

        static Session load(CompoundTag n) {
            Session s = new Session();
            s.journey = n.m_128469_("JourneyRun").m_6426_();
            s.warfront = n.m_128469_("WarfrontRun").m_6426_();
            s.eclipse = n.m_128469_("EclipseRun").m_6426_();
            s.royal = n.m_128469_("RoyalRun").m_6426_();
            s.vigil = n.m_128469_("VigilRun").m_6426_();
            s.legacy = LegacyRun.load(n.m_128469_("LegacyRun"));
            s.id = n.m_128342_("Id");
            s.kind = AdventureData.read(Kind.class, n.m_128461_("Kind"), Kind.TRIAL);
            s.phase = AdventureData.read(Phase.class, n.m_128461_("Phase"), Phase.PAUSED);
            s.dimension = n.m_128461_("Dimension");
            s.anchor = BlockPos.m_122022_((long)n.m_128454_("Anchor"));
            s.tier = Math.max(0, Math.min(2, n.m_128451_("Tier")));
            s.wave = Math.max(0, Math.min(s.maxWaves(), n.m_128451_("Wave")));
            s.epoch = Math.max(1, n.m_128451_("Epoch"));
            s.nextWave = n.m_128454_("NextWave");
            s.initialized = n.m_128471_("Initialized");
            s.wardenUsed = n.m_128471_("WardenUsed");
            for (Tag t : n.m_128437_("Npcs", 10)) {
                if (s.npcs.size() >= 13) continue;
                try {
                    Npc a2 = Npc.load((CompoundTag)t);
                    s.npcs.put(a2.id, a2);
                }
                catch (RuntimeException runtimeException) {}
            }
            for (Tag t : n.m_128437_("Participants", 10)) {
                if (s.participants.size() >= 128 || !((CompoundTag)t).m_128403_("Id")) continue;
                s.participants.add(((CompoundTag)t).m_128342_("Id"));
            }
            if (s.phase == Phase.ACTIVE) {
                if (!s.vigil.m_128456_()) {
                    s.vigil.m_128356_("PausedAt", s.vigil.m_128454_("Clock"));
                }
                if (!s.royal.m_128456_()) {
                    s.royal.m_128356_("PausedAt", s.royal.m_128454_("Clock"));
                }
                if (!s.journey.m_128456_() && !s.journey.m_128441_("PausedAt")) {
                    s.journey.m_128356_("PausedAt", s.journey.m_128454_("Clock"));
                }
                if (s.legacy.pausedAt < 0L) {
                    s.legacy.pausedAt = s.legacy.clock;
                }
                if (!s.warfront.m_128456_() && !s.warfront.m_128441_("PausedAt")) {
                    s.warfront.m_128356_("PausedAt", s.warfront.m_128454_("Clock"));
                }
                if (!s.eclipse.m_128456_()) {
                    s.eclipse.m_128356_("PausedAt", s.eclipse.m_128454_("Clock"));
                }
                s.phase = Phase.PAUSED;
                ++s.epoch;
                s.npcs.values().forEach(a -> {
                    a.active = null;
                });
            }
            return s;
        }
    }

    public static final class Npc {
        public UUID id = UUID.randomUUID();
        public UUID active;
        public String type = "minecraft:zombie";
        public Role role = Role.ENEMY;
        public CompoundTag snapshot = new CompoundTag();
        public BlockPos pos = BlockPos.f_121853_;

        CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128362_("Id", this.id);
            if (this.active != null) {
                n.m_128362_("Active", this.active);
            }
            n.m_128359_("Type", this.type);
            n.m_128359_("Role", this.role.name());
            n.m_128365_("Snapshot", (Tag)this.snapshot.m_6426_());
            n.m_128356_("Pos", this.pos.m_121878_());
            return n;
        }

        static Npc load(CompoundTag n) {
            Npc a = new Npc();
            a.id = n.m_128342_("Id");
            a.active = n.m_128403_("Active") ? n.m_128342_("Active") : null;
            a.type = n.m_128461_("Type");
            a.role = AdventureData.read(Role.class, n.m_128461_("Role"), Role.ENEMY);
            a.snapshot = n.m_128469_("Snapshot").m_6426_();
            a.pos = BlockPos.m_122022_((long)n.m_128454_("Pos"));
            return a;
        }
    }

    public static final class Cache {
        public UUID id = UUID.randomUUID();
        public BlockPos pos = BlockPos.f_121853_;
        public boolean claimed;

        CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128362_("Id", this.id);
            n.m_128356_("Pos", this.pos.m_121878_());
            n.m_128379_("Claimed", this.claimed);
            return n;
        }

        static Cache load(CompoundTag n) {
            Cache c = new Cache();
            c.id = n.m_128342_("Id");
            c.pos = BlockPos.m_122022_((long)n.m_128454_("Pos"));
            c.claimed = n.m_128471_("Claimed");
            return c;
        }
    }

    public static final class Nemesis {
        public UUID id = UUID.randomUUID();
        public UUID companion;
        public UUID target;
        public String type = "";
        public String dimension = "";
        public BlockPos last = BlockPos.f_121853_;
        public boolean kin;
        public boolean ready;
        public int progress;

        CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128362_("Id", this.id);
            if (this.companion != null) {
                n.m_128362_("Companion", this.companion);
            }
            if (this.target != null) {
                n.m_128362_("Target", this.target);
            }
            n.m_128359_("Type", this.type);
            n.m_128359_("Dimension", this.dimension);
            n.m_128356_("Last", this.last.m_121878_());
            n.m_128379_("Kin", this.kin);
            n.m_128379_("Ready", this.ready);
            n.m_128405_("Progress", this.progress);
            return n;
        }

        static Nemesis load(CompoundTag n) {
            Nemesis v = new Nemesis();
            v.id = n.m_128342_("Id");
            v.companion = n.m_128403_("Companion") ? n.m_128342_("Companion") : null;
            v.target = n.m_128403_("Target") ? n.m_128342_("Target") : null;
            v.type = n.m_128461_("Type");
            v.dimension = n.m_128461_("Dimension");
            v.last = BlockPos.m_122022_((long)n.m_128454_("Last"));
            v.kin = n.m_128471_("Kin");
            v.ready = n.m_128471_("Ready");
            v.progress = Math.max(0, Math.min(3, n.m_128451_("Progress")));
            return v;
        }
    }

    public static final class Contract {
        public UUID id = UUID.randomUUID();
        public ContractKind kind = ContractKind.HUNT;
        public String target = "minecraft:zombie";
        public int progress;
        public long offerCycle;
        public final Set<String> seen = new LinkedHashSet<String>();

        public int goal() {
            return this.kind == ContractKind.HUNT ? 5 : (this.kind == ContractKind.DIVERSITY ? 3 : 2);
        }

        CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128362_("Id", this.id);
            n.m_128359_("Kind", this.kind.name());
            n.m_128359_("Target", this.target);
            n.m_128405_("Progress", this.progress);
            n.m_128356_("OfferCycle", this.offerCycle);
            n.m_128365_("Seen", (Tag)AdventureData.strings(this.seen));
            return n;
        }

        static Contract load(CompoundTag n) {
            Contract c = new Contract();
            c.id = n.m_128342_("Id");
            c.kind = AdventureData.read(ContractKind.class, n.m_128461_("Kind"), ContractKind.HUNT);
            c.target = n.m_128461_("Target");
            c.progress = Math.max(0, Math.min(c.goal(), n.m_128451_("Progress")));
            c.offerCycle = n.m_128454_("OfferCycle");
            AdventureData.readStrings(n, "Seen", c.seen, 16);
            return c;
        }
    }

    public static final class Expedition {
        public UUID id = UUID.randomUUID();
        public final List<UUID> members = new ArrayList<UUID>();
        public int tier;
        public long start;
        public long end;
        public boolean ready;

        CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128362_("Id", this.id);
            ListTag ids = new ListTag();
            this.members.forEach(i -> {
                CompoundTag t = new CompoundTag();
                t.m_128362_("Id", i);
                ids.add((Object)t);
            });
            n.m_128365_("Members", (Tag)ids);
            n.m_128405_("Tier", this.tier);
            n.m_128356_("Start", this.start);
            n.m_128356_("End", this.end);
            n.m_128379_("Ready", this.ready);
            return n;
        }

        static Expedition load(CompoundTag n) {
            Expedition e = new Expedition();
            e.id = n.m_128342_("Id");
            for (Tag t : n.m_128437_("Members", 10)) {
                if (e.members.size() >= 3 || !((CompoundTag)t).m_128403_("Id")) continue;
                e.members.add(((CompoundTag)t).m_128342_("Id"));
            }
            e.tier = Math.max(0, Math.min(2, n.m_128451_("Tier")));
            e.start = n.m_128454_("Start");
            e.end = n.m_128454_("End");
            e.ready = n.m_128471_("Ready");
            return e;
        }
    }

    public static final class Lore {
        public String nickname = "";
        public boolean favorite;
        public int stage;
        public long killStart;
        public final Set<String> biomes = new LinkedHashSet<String>();
        public Relic relic = Relic.NONE;
        public Trail trail = Trail.DEFAULT;
        public Title title = Title.NONE;

        CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128359_("Nickname", this.nickname);
            n.m_128379_("Favorite", this.favorite);
            n.m_128405_("Stage", this.stage);
            n.m_128356_("KillStart", this.killStart);
            n.m_128365_("Biomes", (Tag)AdventureData.strings(this.biomes));
            n.m_128359_("Relic", this.relic.name());
            n.m_128359_("Trail", this.trail.name());
            n.m_128359_("Title", this.title.name());
            return n;
        }

        static Lore load(CompoundTag n) {
            Lore l = new Lore();
            l.nickname = LegacyState.plain(n.m_128461_("Nickname"), 24);
            l.favorite = n.m_128471_("Favorite");
            l.stage = Math.max(0, Math.min(4, n.m_128451_("Stage")));
            l.killStart = Math.max(0L, n.m_128454_("KillStart"));
            AdventureData.readStrings(n, "Biomes", l.biomes, 16);
            l.relic = AdventureData.read(Relic.class, n.m_128461_("Relic"), Relic.NONE);
            l.trail = AdventureData.read(Trail.class, n.m_128461_("Trail"), Trail.DEFAULT);
            l.title = AdventureData.read(Title.class, n.m_128461_("Title"), Title.NONE);
            return l;
        }
    }

    public static enum Role {
        ENEMY,
        ESCORT,
        ALLY;

    }

    public static enum Phase {
        ACTIVE,
        PAUSED,
        READY;

    }

    public static enum Kind {
        TRIAL,
        ESCORT,
        GAUNTLET,
        SIEGE,
        VAULT,
        BOSS,
        RIFT,
        RALLY,
        ANGLING,
        CAMP,
        HEIST,
        DOMINATION,
        MIRROR,
        DRAFT,
        RUSH,
        WARFRONT,
        ECLIPSE_RUN,
        REACTOR,
        INVESTIGATION,
        BOARD,
        CRYSTAL,
        CHRONOPHAGE,
        TRAINING,
        SKILL_COURSE,
        SEAL_PUZZLE,
        MIMIC,
        CONSTELLATION,
        ECHO_RHYTHM,
        ASTRAL_DICE,
        POCKET_MAZE,
        FERRYMAN,
        SOUL_PLAY,
        ODYSSEY,
        LIVING_REALM,
        MYTHIC_LAIR;

    }

    public static enum ContractKind {
        HUNT,
        DIVERSITY,
        EXPLORER;

    }

    public static enum Title {
        NONE,
        COLLECTOR,
        ARCHIVIST,
        AVENGER,
        GUARDIAN,
        RIFTWALKER,
        BOUND_VANGUARD,
        WANDERER,
        ECLIPSE_BEARER,
        TWIN_FANGS,
        UNDYING,
        BULWARK,
        RUNEKEEPER,
        GRAVE_CONQUEROR,
        FROST_CONQUEROR,
        HOLLOW_CONQUEROR,
        RIFT_DAREDEVIL,
        RALLY_CHAMPION,
        SPIRIT_ANGLER,
        CAMPKEEPER,
        WAYFARER,
        TRAILBLAZER,
        RIFT_CARTOGRAPHER,
        KINGSLAYER,
        DAWN_SOVEREIGN,
        RIFT_VETERAN,
        MOONLIT_ANGLER,
        GHOST_THIEF,
        RIFT_COMMANDER,
        MIRRORBREAKER,
        DRAFT_SOVEREIGN,
        FORTUNE_RUNNER,
        WAR_STRATEGIST,
        WAR_MARSHAL,
        RIVAL_SOVEREIGN,
        KINGDOM_KEEPER;

    }

    public static enum Trail {
        DEFAULT,
        AZURE,
        AMETHYST,
        ECHO,
        GOLD,
        VOID;

    }

    public static enum Relic {
        NONE,
        PATHFINDER,
        SEEKER,
        WARDEN;

    }
}

