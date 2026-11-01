/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.ascension;

import dev.shadowsovereign.army.ShadowRecord;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;

public final class SoulRecord {
    public int roll;
    public int bound = 100;
    public int chance = 1;
    public final UUID id;
    public final ShadowRecord companion;
    public final String dimension;
    public final Vec3 position;
    public final long expires;

    public SoulRecord(UUID id, ShadowRecord companion, String dimension, Vec3 position, long expires) {
        this.id = id;
        this.companion = companion;
        this.dimension = dimension;
        this.position = position;
        this.expires = expires;
    }

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128362_("Id", this.id);
        n.m_128405_("Roll", this.roll);
        n.m_128405_("RollBound", this.bound);
        n.m_128405_("Chance", this.chance);
        n.m_128365_("Companion", (Tag)this.companion.save());
        n.m_128359_("Dimension", this.dimension);
        n.m_128347_("X", this.position.f_82479_);
        n.m_128347_("Y", this.position.f_82480_);
        n.m_128347_("Z", this.position.f_82481_);
        n.m_128356_("Expires", this.expires);
        return n;
    }

    public static SoulRecord load(CompoundTag n) {
        SoulRecord s = new SoulRecord(n.m_128342_("Id"), ShadowRecord.load(n.m_128469_("Companion")), n.m_128461_("Dimension"), new Vec3(n.m_128459_("X"), n.m_128459_("Y"), n.m_128459_("Z")), n.m_128454_("Expires"));
        s.bound = n.m_128451_("RollBound") == 100 ? 100 : 3;
        int n2 = s.chance = s.bound == 3 ? 1 : Math.max(1, Math.min(10, n.m_128451_("Chance")));
        int n3 = n.m_128441_("Roll") ? (s.bound == 3 ? Math.floorMod(n.m_128451_("Roll"), 3) : n.m_128451_("Roll")) : (s.roll = Math.floorMod(s.id.hashCode(), s.bound));
        if (s.roll < 0 || s.roll >= s.bound) {
            s.roll = s.bound - 1;
        }
        return s;
    }
}

