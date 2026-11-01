/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.sentinel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;

public final class SentinelState {
    public Style style = Style.NORMAL;
    public boolean wanted;
    public boolean rally;
    public boolean torchRoute;
    public boolean circuit;
    public BlockPos last = BlockPos.f_121853_;
    public BlockPos home = BlockPos.f_121853_;
    public String homeDimension = "";
    public final List<UUID> nodes = new ArrayList<UUID>();
    public long waitUntil;
    public transient long retryAt;
    public transient long missingSince;
    public transient long progressAt;
    public transient Vec3 progress;

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128359_("Style", this.style.name());
        n.m_128379_("Wanted", this.wanted);
        n.m_128379_("Rally", this.rally);
        n.m_128379_("TorchRoute", this.torchRoute);
        n.m_128379_("Circuit", this.circuit);
        n.m_128356_("Last", this.last.m_121878_());
        n.m_128356_("Home", this.home.m_121878_());
        n.m_128359_("HomeDimension", this.homeDimension);
        ListTag list = new ListTag();
        for (UUID id : this.nodes) {
            CompoundTag row = new CompoundTag();
            row.m_128362_("Id", id);
            list.add((Object)row);
        }
        n.m_128365_("Nodes", (Tag)list);
        n.m_128356_("Wait", this.waitUntil);
        return n;
    }

    public static SentinelState load(CompoundTag n) {
        SentinelState s = new SentinelState();
        try {
            s.style = Style.valueOf(n.m_128461_("Style"));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        s.wanted = n.m_128471_("Wanted");
        s.rally = n.m_128471_("Rally");
        s.torchRoute = n.m_128471_("TorchRoute");
        s.circuit = n.m_128471_("Circuit");
        s.last = BlockPos.m_122022_((long)n.m_128454_("Last"));
        s.home = BlockPos.m_122022_((long)n.m_128454_("Home"));
        s.homeDimension = n.m_128461_("HomeDimension");
        for (Tag row : n.m_128437_("Nodes", 10)) {
            if (s.nodes.size() >= 8 || !((CompoundTag)row).m_128403_("Id")) continue;
            s.nodes.add(((CompoundTag)row).m_128342_("Id"));
        }
        s.waitUntil = Math.max(0L, n.m_128454_("Wait"));
        return s;
    }

    public static enum Style {
        NORMAL,
        CLOSE,
        BODYGUARD,
        RETREAT,
        CEASE_FIRE;

    }
}

