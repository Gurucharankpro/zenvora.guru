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
package dev.shadowsovereign.royal;

import dev.shadowsovereign.legacy.LegacyState;
import dev.shadowsovereign.soulforge.SoulTier;
import dev.shadowsovereign.soulforge.SoulTrait;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public final class RoyalState {
    public final Team[] teams = new Team[]{new Team(), new Team(), new Team(), new Team(), new Team()};
    public final EnumMap<Skill, Integer> levels = new EnumMap(Skill.class);
    public final LinkedHashMap<String, long[]> hunts = new LinkedHashMap();
    public final LinkedHashSet<String> wanted = new LinkedHashSet();
    public final List<CompoundTag> memories = new ArrayList<CompoundTag>();
    public final Map<UUID, Long> charmAt = new LinkedHashMap<UUID, Long>();
    public final Map<Integer, Banner> banners = new LinkedHashMap<Integer, Banner>();
    public int selectedTeam;
    public int emblems = 255;
    public int mutator = -1;
    public long breachAt;
    public int puzzleBest = Integer.MAX_VALUE;
    public long courseBest = -1L;
    public long courseHardBest = -1L;
    public final transient List<CompoundTag> notices = new ArrayList<CompoundTag>();
    public transient CompoundTag lastEclipse;
    public transient CompoundTag lastRoyal;
    public transient BlockPos desk;
    public transient long deskScan = -100L;
    public transient long royalViewUntil;
    public final transient Map<UUID, Long> breach = new HashMap<UUID, Long>();

    public int level(Skill skill) {
        return this.levels.getOrDefault((Object)skill, 1);
    }

    public void attempted(String type, boolean success) {
        if (type.length() > 256 || !this.hunts.containsKey(type) && this.hunts.size() >= 4096) {
            return;
        }
        long[] a = this.hunts.computeIfAbsent(type, k -> new long[2]);
        a[0] = Math.min(0x7FFFFFFFFFFFFFFEL, a[0]) + 1L;
        if (success) {
            a[1] = Math.min(0x7FFFFFFFFFFFFFFEL, a[1]) + 1L;
        }
    }

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        CompoundTag ls = new CompoundTag();
        for (Skill s : Skill.values()) {
            ls.m_128405_(s.name(), this.level(s));
        }
        n.m_128365_("Levels", (Tag)ls);
        ListTag ts = new ListTag();
        for (Team t2 : this.teams) {
            ts.add((Object)t2.save());
        }
        n.m_128365_("Teams", (Tag)ts);
        n.m_128405_("SelectedTeam", this.selectedTeam);
        n.m_128405_("Emblems", this.emblems);
        n.m_128405_("Mutator", this.mutator);
        n.m_128356_("BreachAt", this.breachAt);
        n.m_128405_("PuzzleBest", this.puzzleBest);
        n.m_128356_("CourseBest", this.courseBest);
        n.m_128356_("CourseHardBest", this.courseHardBest);
        ListTag hs = new ListTag();
        this.hunts.forEach((type, count) -> {
            CompoundTag h = new CompoundTag();
            h.m_128359_("Type", type);
            h.m_128356_("Attempts", count[0]);
            h.m_128356_("Successes", count[1]);
            hs.add((Object)h);
        });
        n.m_128365_("Hunts", (Tag)hs);
        ListTag ws = new ListTag();
        this.wanted.forEach(t -> ws.add((Object)StringTag.m_129297_((String)t)));
        n.m_128365_("Wanted", (Tag)ws);
        ListTag ms = new ListTag();
        this.memories.forEach(m -> ms.add((Object)m.m_6426_()));
        n.m_128365_("Memories", (Tag)ms);
        ListTag cs = new ListTag();
        this.charmAt.forEach((id, at) -> {
            CompoundTag c = new CompoundTag();
            c.m_128362_("Id", id);
            c.m_128356_("At", at.longValue());
            cs.add((Object)c);
        });
        n.m_128365_("Charms", (Tag)cs);
        ListTag bs = new ListTag();
        this.banners.forEach((i, b) -> {
            CompoundTag v = b.save();
            v.m_128405_("Team", i.intValue());
            bs.add((Object)v);
        });
        n.m_128365_("Banners", (Tag)bs);
        return n;
    }

    public static RoyalState load(CompoundTag n) {
        RoyalState r = new RoyalState();
        for (Skill s : Skill.values()) {
            r.levels.put(s, RoyalState.clamp(n.m_128469_("Levels").m_128451_(s.name()), 1, 10));
        }
        ListTag ts = n.m_128437_("Teams", 10);
        for (int i = 0; i < Math.min(5, ts.size()); ++i) {
            r.teams[i] = Team.load(ts.m_128728_(i));
        }
        r.selectedTeam = RoyalState.clamp(n.m_128451_("SelectedTeam"), 0, 4);
        r.emblems = (n.m_128441_("Emblems") ? n.m_128451_("Emblems") : 255) & 0xFFF;
        r.emblems |= 0xFF;
        r.mutator = RoyalState.clamp(n.m_128441_("Mutator") ? n.m_128451_("Mutator") : -1, -1, 5);
        r.breachAt = Math.max(0L, n.m_128454_("BreachAt"));
        r.puzzleBest = n.m_128441_("PuzzleBest") ? Math.max(0, n.m_128451_("PuzzleBest")) : Integer.MAX_VALUE;
        r.courseBest = n.m_128441_("CourseBest") ? Math.max(-1L, n.m_128454_("CourseBest")) : -1L;
        r.courseHardBest = n.m_128441_("CourseHardBest") ? Math.max(-1L, n.m_128454_("CourseHardBest")) : -1L;
        for (Tag t : n.m_128437_("Hunts", 10)) {
            CompoundTag h;
            String type;
            if (r.hunts.size() >= 4096 || (type = (h = (CompoundTag)t).m_128461_("Type")).length() > 256) continue;
            long attempts = Math.max(0L, h.m_128454_("Attempts"));
            r.hunts.put(type, new long[]{attempts, Math.max(0L, Math.min(attempts, h.m_128454_("Successes")))});
        }
        for (Tag t : n.m_128437_("Wanted", 8)) {
            if (r.wanted.size() >= 5 || t.m_7916_().length() > 256) continue;
            r.wanted.add(t.m_7916_());
        }
        for (Tag t : n.m_128437_("Memories", 10)) {
            CompoundTag m;
            if (r.memories.size() >= 20 || !(m = (CompoundTag)t).m_128403_("Id") || m.m_128461_("Type").length() > 256) continue;
            CompoundTag safe = new CompoundTag();
            safe.m_128362_("Id", m.m_128342_("Id"));
            for (String key : List.of("Type", "Biome", "Dimension")) {
                safe.m_128359_(key, LegacyState.plain(m.m_128461_(key), 256));
            }
            safe.m_128359_("Name", LegacyState.plain(m.m_128461_("Name"), 128));
            safe.m_128359_("Tier", SoulTier.read(m.m_128461_("Tier")).name());
            safe.m_128359_("Trait", SoulTrait.read(m.m_128461_("Trait")).name());
            safe.m_128405_("Chance", RoyalState.clamp(m.m_128451_("Chance"), 1, 33));
            safe.m_128356_("Day", Math.max(1L, m.m_128454_("Day")));
            r.memories.add(safe);
        }
        for (Tag t : n.m_128437_("Charms", 10)) {
            if (r.charmAt.size() >= 1024 || !((CompoundTag)t).m_128403_("Id")) continue;
            r.charmAt.put(((CompoundTag)t).m_128342_("Id"), Math.max(0L, ((CompoundTag)t).m_128454_("At")));
        }
        for (Tag t : n.m_128437_("Banners", 10)) {
            CompoundTag b = (CompoundTag)t;
            int i = b.m_128451_("Team");
            if (i < 0 || i >= 5 || b.m_128461_("Dimension").length() >= 256) continue;
            r.banners.put(i, Banner.load(b));
        }
        return r;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public static final class Team {
        public String name = "";
        public int emblem;
        public int food = 8;
        public int arrows = 16;
        public final LinkedHashSet<UUID> members = new LinkedHashSet();

        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128359_("Name", this.name);
            n.m_128405_("Emblem", this.emblem);
            n.m_128405_("Food", this.food);
            n.m_128405_("Arrows", this.arrows);
            ListTag ids = new ListTag();
            for (UUID id : this.members) {
                CompoundTag v = new CompoundTag();
                v.m_128362_("Id", id);
                ids.add((Object)v);
            }
            n.m_128365_("Members", (Tag)ids);
            return n;
        }

        public static Team load(CompoundTag n) {
            Team t = new Team();
            t.name = LegacyState.plain(n.m_128461_("Name"), 24);
            t.emblem = RoyalState.clamp(n.m_128451_("Emblem"), 0, 11);
            t.food = n.m_128441_("Food") ? RoyalState.clamp(n.m_128451_("Food"), 0, 16) : 8;
            t.arrows = n.m_128441_("Arrows") ? RoyalState.clamp(n.m_128451_("Arrows"), 0, 64) : 16;
            for (Tag tag : n.m_128437_("Members", 10)) {
                if (t.members.size() >= 24 || !((CompoundTag)tag).m_128403_("Id")) continue;
                t.members.add(((CompoundTag)tag).m_128342_("Id"));
            }
            return t;
        }
    }

    public static enum Skill {
        EXTRACT,
        DASH,
        WELL,
        LANCE,
        PARRY,
        STORM;

    }

    public record Banner(BlockPos pos, String dimension) {
        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128356_("Pos", this.pos.m_121878_());
            n.m_128359_("Dimension", this.dimension);
            return n;
        }

        public static Banner load(CompoundTag n) {
            return new Banner(BlockPos.m_122022_((long)n.m_128454_("Pos")), n.m_128461_("Dimension"));
        }
    }
}

