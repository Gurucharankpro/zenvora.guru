/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerBossEvent
 */
package dev.shadowsovereign.mythic;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerBossEvent;

public final class MythicState {
    public CompoundTag gloom = new CompoundTag();
    public int status;
    public int cursor;
    public int site = -1;
    public boolean generated;
    public boolean requested;
    public UUID active;
    public UUID soul;
    public CompoundTag snapshot = new CompoundTag();
    public transient long viewing;
    public transient ServerBossEvent bar;

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128405_("Version", 2);
        n.m_128365_("Gloom", (Tag)this.gloom.m_6426_());
        n.m_128405_("Status", this.status);
        n.m_128405_("Cursor", this.cursor);
        n.m_128405_("Site", this.site);
        n.m_128379_("Generated", this.generated);
        n.m_128379_("Requested", this.requested);
        if (this.active != null) {
            n.m_128362_("Active", this.active);
        }
        if (this.soul != null) {
            n.m_128362_("Soul", this.soul);
        }
        n.m_128365_("Snapshot", (Tag)this.snapshot.m_6426_());
        return n;
    }

    public static MythicState load(CompoundTag n) {
        MythicState s = new MythicState();
        s.gloom = n.m_128469_("Gloom").m_6426_();
        s.status = Math.max(0, Math.min(2, n.m_128451_("Status")));
        s.cursor = Math.max(0, Math.min(7353, n.m_128451_("Cursor")));
        s.site = n.m_128441_("Site") ? Math.max(-1, Math.min(15, n.m_128451_("Site"))) : -1;
        s.generated = n.m_128471_("Generated");
        s.requested = n.m_128471_("Requested");
        s.active = n.m_128403_("Active") ? n.m_128342_("Active") : null;
        s.soul = n.m_128403_("Soul") ? n.m_128342_("Soul") : null;
        s.snapshot = n.m_128469_("Snapshot").m_6426_();
        return s;
    }
}

