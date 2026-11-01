/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 */
package dev.shadowsovereign.journey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public final class JourneyState {
    public final CompoundTag data;
    public final Map<UUID, CompoundTag> companions = new LinkedHashMap<UUID, CompoundTag>();

    public JourneyState() {
        this.data = new CompoundTag();
    }

    private JourneyState(CompoundTag n) {
        this.data = n.m_6426_();
        this.data.m_128473_("Companions");
        for (Tag t : n.m_128437_("Companions", 10)) {
            CompoundTag e;
            if (this.companions.size() >= 1024 || !(e = (CompoundTag)t).m_128403_("Id")) continue;
            this.companions.put(e.m_128342_("Id"), e.m_6426_());
        }
    }

    public CompoundTag companion(UUID id) {
        return this.companions.computeIfAbsent(id, k -> new CompoundTag());
    }

    public static boolean earned(CompoundTag n, Feat f) {
        return switch (f) {
            default -> throw new IncompatibleClassChangeError();
            case Feat.NONE -> true;
            case Feat.HUNTER -> {
                if (n.m_128454_("Kills") >= 25L) {
                    yield true;
                }
                yield false;
            }
            case Feat.BOSSBREAKER -> n.m_128471_("Boss");
            case Feat.TRAILRUNNER -> n.m_128471_("Rally");
            case Feat.STORYKEEPER -> Integer.bitCount(n.m_128451_("Stories") & 0x3F) >= 3;
        };
    }

    public CompoundTag save() {
        CompoundTag n = this.data.m_6426_();
        ListTag list = new ListTag();
        this.companions.forEach((id, v) -> {
            CompoundTag e = v.m_6426_();
            e.m_128362_("Id", id);
            list.add((Object)e);
        });
        n.m_128365_("Companions", (Tag)list);
        return n;
    }

    public static JourneyState load(CompoundTag n) {
        return new JourneyState(n);
    }

    public boolean daily(String key, long day) {
        if (this.data.m_128441_(key) && this.data.m_128454_(key) == day) {
            return false;
        }
        this.data.m_128356_(key, day);
        return true;
    }

    public void increment(String key) {
        this.data.m_128356_(key, Math.min(0x7FFFFFFFFFFFFFFEL, Math.max(0L, this.data.m_128454_(key))) + 1L);
    }

    public static enum Feat {
        NONE,
        HUNTER,
        BOSSBREAKER,
        TRAILRUNNER,
        STORYKEEPER;

    }
}

