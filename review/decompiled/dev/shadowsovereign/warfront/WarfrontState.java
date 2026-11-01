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
package dev.shadowsovereign.warfront;

import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.warfront.WarfrontPlans;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public final class WarfrontState {
    public boolean bound;
    public boolean wonOnce;
    public BlockPos beacon = BlockPos.f_121853_;
    public String dimension = "minecraft:overworld";
    public long seed;
    public long chapter = 1L;
    public long trophies;
    public int captured;
    public int owned;
    public int contested = -1;
    public int blueprints = 1 << Rune.STRIDE.ordinal() | 1 << Rune.MERCY.ordinal();
    public int defeated;
    public int pactReady;
    public int pactClaimed;
    public final int[] influence = new int[3];
    public final Adaptation[] adaptations = new Adaptation[]{Adaptation.NONE, Adaptation.NONE, Adaptation.NONE};
    public final ShadowRecord[] offers = new ShadowRecord[3];
    public UUID activeMission;
    public final Map<UUID, Build> builds = new LinkedHashMap<UUID, Build>();
    public final List<List<Step>> plans = new ArrayList<ArrayList<List<Step>>>(List.of(new ArrayList(), new ArrayList(), new ArrayList()));
    public BlockPos marker;
    public String markerDimension = "";
    public transient long placementUntil;
    public transient WarfrontPlans.Execution execution;
    public final Map<UUID, Long> illusions = new LinkedHashMap<UUID, Long>();

    public static String label(Enum<?> e) {
        String s = e.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static <E extends Enum<E>> E read(Class<E> type, String value, E fallback) {
        try {
            return Enum.valueOf(type, value);
        }
        catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public Build build(UUID id) {
        return this.builds.computeIfAbsent(id, k -> new Build());
    }

    public int mission(int node) {
        return node >= 3 ? 3 : Math.floorMod(this.seed + this.chapter * 7919L + (long)(node * 137), 3);
    }

    public static int rival(int node) {
        return node == 1 || node == 3 ? 0 : (node == 2 || node == 4 ? 1 : 2);
    }

    public boolean available(int node) {
        if (node < 0 || node > 5) {
            return false;
        }
        if (node == this.contested) {
            return true;
        }
        if ((this.owned & 1 << node) != 0) {
            return false;
        }
        return switch (node) {
            case 0 -> true;
            case 1, 2 -> {
                if ((this.owned & 1) != 0) {
                    yield true;
                }
                yield false;
            }
            case 3 -> {
                if ((this.owned & 2) != 0) {
                    yield true;
                }
                yield false;
            }
            case 4 -> {
                if ((this.owned & 4) != 0) {
                    yield true;
                }
                yield false;
            }
            default -> (this.owned & 0x18) == 24;
        };
    }

    public String rivalName(int rival) {
        String[] first = new String[]{"Ashen", "Moonlit", "Hollow", "Iron", "Silent", "Crimson", "Frostbound", "Veiled"};
        String[] last = new String[]{"Regent", "Seer", "Marshal", "Keeper", "Warden", "Reaver"};
        long salt = this.seed + this.chapter * 31L + (long)(rival * 137);
        return first[Math.floorMod(salt, 8)] + " " + last[Math.floorMod(salt / 8L, 6)];
    }

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128379_("Bound", this.bound);
        n.m_128379_("WonOnce", this.wonOnce);
        n.m_128356_("Beacon", this.beacon.m_121878_());
        n.m_128359_("Dimension", this.dimension);
        n.m_128356_("Seed", this.seed);
        n.m_128356_("Chapter", this.chapter);
        n.m_128356_("Trophies", this.trophies);
        n.m_128405_("Owned", this.owned);
        n.m_128405_("Captured", this.captured);
        n.m_128405_("Contested", this.contested);
        n.m_128405_("Blueprints", this.blueprints);
        n.m_128405_("Defeated", this.defeated);
        n.m_128405_("PactReady", this.pactReady);
        n.m_128405_("PactClaimed", this.pactClaimed);
        n.m_128385_("Influence", this.influence);
        if (this.activeMission != null) {
            n.m_128362_("ActiveMission", this.activeMission);
        }
        ListTag rivals = new ListTag();
        for (int i = 0; i < 3; ++i) {
            CompoundTag r = new CompoundTag();
            r.m_128359_("Adaptation", this.adaptations[i].name());
            if (this.offers[i] != null) {
                r.m_128365_("Offer", (Tag)this.offers[i].save());
            }
            rivals.add((Object)r);
        }
        n.m_128365_("Rivals", (Tag)rivals);
        ListTag list = new ListTag();
        this.builds.forEach((id, b) -> {
            CompoundTag e = b.save();
            e.m_128362_("Id", id);
            list.add((Object)e);
        });
        n.m_128365_("Builds", (Tag)list);
        ListTag ps = new ListTag();
        for (List<Step> plan : this.plans) {
            CompoundTag e = new CompoundTag();
            ListTag steps = new ListTag();
            plan.forEach(s -> steps.add((Object)StringTag.m_129297_((String)s.name())));
            e.m_128365_("Steps", (Tag)steps);
            ps.add((Object)e);
        }
        n.m_128365_("Plans", (Tag)ps);
        if (this.marker != null) {
            n.m_128356_("Marker", this.marker.m_121878_());
        }
        n.m_128359_("MarkerDimension", this.markerDimension);
        ListTag ghosts = new ListTag();
        this.illusions.forEach((id, at) -> {
            CompoundTag e = new CompoundTag();
            e.m_128362_("Id", id);
            e.m_128356_("Until", at.longValue());
            ghosts.add((Object)e);
        });
        n.m_128365_("Illusions", (Tag)ghosts);
        return n;
    }

    public static WarfrontState load(CompoundTag n) {
        WarfrontState s = new WarfrontState();
        s.bound = n.m_128471_("Bound");
        s.wonOnce = n.m_128471_("WonOnce");
        s.beacon = BlockPos.m_122022_((long)n.m_128454_("Beacon"));
        if (n.m_128441_("Dimension")) {
            s.dimension = n.m_128461_("Dimension");
        }
        s.seed = n.m_128454_("Seed");
        s.chapter = Math.max(1L, n.m_128454_("Chapter"));
        s.trophies = Math.max(0L, n.m_128454_("Trophies"));
        s.owned = n.m_128451_("Owned") & 0x3F;
        s.captured = n.m_128441_("Captured") ? n.m_128451_("Captured") & 0x3F : s.owned;
        s.contested = n.m_128441_("Contested") ? Math.max(-1, Math.min(5, n.m_128451_("Contested"))) : -1;
        s.blueprints |= n.m_128451_("Blueprints") & 0x7E;
        s.defeated = n.m_128451_("Defeated") & 7;
        s.pactReady = n.m_128451_("PactReady") & 7;
        s.pactClaimed = n.m_128451_("PactClaimed") & 7;
        int[] influences = n.m_128465_("Influence");
        for (int i = 0; i < Math.min(3, influences.length); ++i) {
            s.influence[i] = Math.max(0, Math.min(2, influences[i]));
        }
        s.activeMission = n.m_128403_("ActiveMission") ? n.m_128342_("ActiveMission") : null;
        ListTag rivals = n.m_128437_("Rivals", 10);
        for (int i = 0; i < Math.min(3, rivals.size()); ++i) {
            CompoundTag r = rivals.m_128728_(i);
            s.adaptations[i] = WarfrontState.read(Adaptation.class, r.m_128461_("Adaptation"), Adaptation.NONE);
            if (!r.m_128441_("Offer")) continue;
            try {
                s.offers[i] = ShadowRecord.load(r.m_128469_("Offer"));
                continue;
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        for (Tag tag : n.m_128437_("Builds", 10)) {
            Object e;
            if (s.builds.size() >= 1024 || !(e = (CompoundTag)tag).m_128403_("Id")) continue;
            s.builds.put(e.m_128342_("Id"), Build.load((CompoundTag)e));
        }
        ListTag ps = n.m_128437_("Plans", 10);
        for (int i = 0; i < Math.min(3, ps.size()); ++i) {
            for (Tag tag : ps.m_128728_(i).m_128437_("Steps", 8)) {
                if (s.plans.get(i).size() >= 4) continue;
                s.plans.get(i).add(WarfrontState.read(Step.class, tag.m_7916_(), Step.REGROUP));
            }
        }
        if (n.m_128441_("Marker")) {
            s.marker = BlockPos.m_122022_((long)n.m_128454_("Marker"));
        }
        s.markerDimension = n.m_128461_("MarkerDimension");
        for (Tag tag : n.m_128437_("Illusions", 10)) {
            CompoundTag e;
            if (s.illusions.size() >= 2 || !(e = (CompoundTag)tag).m_128403_("Id")) continue;
            s.illusions.put(e.m_128342_("Id"), e.m_128454_("Until"));
        }
        return s;
    }

    public static enum Rune {
        NONE,
        STRIDE,
        MERCY,
        WARD,
        VIGIL,
        TETHER,
        HEARTH;

    }

    public static enum Adaptation {
        NONE,
        WINDGUARD,
        SCATTER,
        REPRISAL;

    }

    public static final class Build {
        public Art art = Art.NONE;
        public Rune rune = Rune.NONE;
        public int crafted;
        public long artAt;
        public long wardAt;
        public long stitchAt;
        public UUID stitchEntity;
        public float stitchHealth;
        public UUID sunderTarget;
        public UUID vigilTarget;
        public String targetDimension = "";
        public long sunderUntil;
        public long vigilUntil;
        public boolean pactbound;

        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128359_("Art", this.art.name());
            n.m_128359_("Rune", this.rune.name());
            n.m_128405_("Crafted", this.crafted);
            n.m_128356_("ArtAt", this.artAt);
            n.m_128356_("WardAt", this.wardAt);
            n.m_128356_("StitchAt", this.stitchAt);
            n.m_128350_("StitchHealth", this.stitchHealth);
            if (this.stitchEntity != null) {
                n.m_128362_("StitchEntity", this.stitchEntity);
            }
            if (this.sunderTarget != null) {
                n.m_128362_("SunderTarget", this.sunderTarget);
            }
            if (this.vigilTarget != null) {
                n.m_128362_("VigilTarget", this.vigilTarget);
            }
            n.m_128359_("TargetDimension", this.targetDimension);
            n.m_128356_("SunderUntil", this.sunderUntil);
            n.m_128356_("VigilUntil", this.vigilUntil);
            n.m_128379_("Pactbound", this.pactbound);
            return n;
        }

        static Build load(CompoundTag n) {
            Build b = new Build();
            b.art = WarfrontState.read(Art.class, n.m_128461_("Art"), Art.NONE);
            b.rune = WarfrontState.read(Rune.class, n.m_128461_("Rune"), Rune.NONE);
            b.crafted = n.m_128451_("Crafted") & 0x7E;
            b.artAt = Math.max(0L, n.m_128454_("ArtAt"));
            b.wardAt = Math.max(0L, n.m_128454_("WardAt"));
            b.stitchAt = Math.max(0L, n.m_128454_("StitchAt"));
            b.stitchHealth = Math.max(0.0f, n.m_128457_("StitchHealth"));
            b.stitchEntity = n.m_128403_("StitchEntity") ? n.m_128342_("StitchEntity") : null;
            b.sunderTarget = n.m_128403_("SunderTarget") ? n.m_128342_("SunderTarget") : null;
            b.vigilTarget = n.m_128403_("VigilTarget") ? n.m_128342_("VigilTarget") : null;
            b.targetDimension = n.m_128461_("TargetDimension");
            b.sunderUntil = n.m_128454_("SunderUntil");
            b.vigilUntil = n.m_128454_("VigilUntil");
            b.pactbound = n.m_128471_("Pactbound");
            return b;
        }
    }

    public static enum Step {
        RALLY,
        FOCUS,
        GUARD,
        REGROUP;

    }

    public static enum Art {
        NONE,
        PHANTOM_DOUBLE,
        TIME_STITCH,
        SUNDER;

    }
}

