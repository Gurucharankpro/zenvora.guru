/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 */
package dev.shadowsovereign.realm;

import dev.shadowsovereign.army.ShadowRecord;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public final class RealmState {
    public int cursor;
    public int waystones;
    public int restored;
    public int cleared;
    public int projects;
    public int contract = -1;
    public int contractProgress;
    public int contractCycle;
    public int finale;
    public boolean requested;
    public boolean ready;
    public boolean reduced;
    public long voiceAt;
    public long hazardAt;
    public long ecologyAt;
    public transient long viewUntil;
    public int pinned = -1;
    public UUID scout;
    public final BitSet surveyed = new BitSet(400);
    public final Map<String, CompoundTag> bestiary = new LinkedHashMap<String, CompoundTag>();
    public final LinkedHashMap<UUID, ShadowRecord> pending = new LinkedHashMap();
    public final CompoundTag sites = new CompoundTag();
    public final CompoundTag escort = new CompoundTag();

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128405_("Version", 1);
        n.m_128405_("Cursor", this.cursor);
        n.m_128379_("Requested", this.requested);
        n.m_128379_("Ready", this.ready);
        n.m_128379_("Reduced", this.reduced);
        n.m_128405_("Waystones", this.waystones);
        n.m_128405_("Restored", this.restored);
        n.m_128405_("Cleared", this.cleared);
        n.m_128405_("Projects", this.projects);
        n.m_128405_("Contract", this.contract);
        n.m_128405_("ContractProgress", this.contractProgress);
        n.m_128405_("ContractCycle", this.contractCycle);
        n.m_128405_("Finale", this.finale);
        n.m_128405_("Pinned", this.pinned);
        n.m_128356_("VoiceAt", this.voiceAt);
        n.m_128356_("HazardAt", this.hazardAt);
        n.m_128356_("EcologyAt", this.ecologyAt);
        n.m_128388_("Surveyed", this.surveyed.toLongArray());
        if (this.scout != null) {
            n.m_128362_("Scout", this.scout);
        }
        CompoundTag entries = new CompoundTag();
        this.bestiary.forEach((k, v) -> entries.m_128365_(k, (Tag)v.m_6426_()));
        n.m_128365_("Bestiary", (Tag)entries);
        ListTag souls = new ListTag();
        this.pending.values().forEach(r -> souls.add((Object)r.save()));
        n.m_128365_("Pending", (Tag)souls);
        n.m_128365_("Sites", (Tag)this.sites.m_6426_());
        n.m_128365_("Escort", (Tag)this.escort.m_6426_());
        return n;
    }

    public static RealmState load(CompoundTag n) {
        RealmState s = new RealmState();
        s.cursor = Math.max(0, Math.min(820512, n.m_128451_("Cursor")));
        s.requested = n.m_128471_("Requested");
        s.ready = n.m_128471_("Ready");
        s.reduced = n.m_128471_("Reduced");
        s.waystones = n.m_128451_("Waystones") & 0xF;
        s.restored = n.m_128451_("Restored") & 0xF;
        s.cleared = n.m_128451_("Cleared") & 0xFFF;
        s.projects = n.m_128451_("Projects") & 0xF;
        s.contract = n.m_128441_("Contract") ? Math.max(-1, Math.min(3, n.m_128451_("Contract"))) : -1;
        s.contractProgress = Math.max(0, Math.min(10000, n.m_128451_("ContractProgress")));
        s.contractCycle = Math.max(0, n.m_128451_("ContractCycle"));
        s.finale = Math.max(0, Math.min(2, n.m_128451_("Finale")));
        s.pinned = n.m_128441_("Pinned") ? Math.max(-1, Math.min(23, n.m_128451_("Pinned"))) : -1;
        s.voiceAt = Math.max(0L, n.m_128454_("VoiceAt"));
        s.hazardAt = Math.max(0L, n.m_128454_("HazardAt"));
        s.ecologyAt = Math.max(0L, n.m_128454_("EcologyAt"));
        if (n.m_128403_("Scout")) {
            s.scout = n.m_128342_("Scout");
        }
        s.surveyed.or(BitSet.valueOf(Arrays.copyOf(n.m_128467_("Surveyed"), Math.min(7, n.m_128467_("Surveyed").length))));
        s.surveyed.clear(400, Math.max(400, s.surveyed.length()));
        CompoundTag b = n.m_128469_("Bestiary");
        for (String k : b.m_128431_()) {
            if (s.bestiary.size() >= 256 || k.length() >= 128) continue;
            s.bestiary.put(k, b.m_128469_(k).m_6426_());
        }
        for (Tag tag : n.m_128437_("Pending", 10)) {
            try {
                ShadowRecord r = ShadowRecord.load((CompoundTag)tag);
                s.pending.putIfAbsent(r.id, r);
            }
            catch (RuntimeException runtimeException) {}
        }
        s.sites.m_128391_(n.m_128469_("Sites"));
        s.escort.m_128391_(n.m_128469_("Escort"));
        return s;
    }
}

