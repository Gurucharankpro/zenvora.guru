/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 */
package dev.shadowsovereign.legacy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public final class LegacyRun {
    public final int[] boons = new int[3];
    public final Map<UUID, Float> wards = new LinkedHashMap<UUID, Float>();
    public final Map<UUID, Long> crystalAt = new HashMap<UUID, Long>();
    public int integrity = 100;
    public int input;
    public boolean choosing;
    public boolean recorded;
    public long clock;
    public long day = -1L;
    public long seed;
    public long showUntil;
    public long deadline;
    public long pausedAt = -1L;

    public int rank(int kind) {
        return kind >= 0 && kind < 3 ? this.boons[kind] : 0;
    }

    public int[] sequence(int round) {
        Random random = new Random(this.seed + (long)round * 7919L);
        int[] answer = new int[3 + Math.max(0, Math.min(2, round))];
        for (int i = 0; i < answer.length; ++i) {
            answer[i] = random.nextInt(4);
        }
        return answer;
    }

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128356_("Clock", this.clock);
        n.m_128385_("Boons", this.boons);
        n.m_128405_("Integrity", this.integrity);
        n.m_128405_("Input", this.input);
        n.m_128379_("Choosing", this.choosing);
        n.m_128379_("Recorded", this.recorded);
        n.m_128356_("Day", this.day);
        n.m_128356_("Seed", this.seed);
        n.m_128356_("ShowUntil", this.showUntil);
        n.m_128356_("Deadline", this.deadline);
        n.m_128356_("PausedAt", this.pausedAt);
        ListTag ws = new ListTag();
        this.wards.forEach((id, hp) -> {
            CompoundTag a = new CompoundTag();
            a.m_128362_("Id", id);
            a.m_128350_("HP", hp.floatValue());
            ws.add((Object)a);
        });
        n.m_128365_("Wards", (Tag)ws);
        ListTag cs = new ListTag();
        this.crystalAt.forEach((id, time) -> {
            CompoundTag a = new CompoundTag();
            a.m_128362_("Id", id);
            a.m_128356_("Time", time.longValue());
            cs.add((Object)a);
        });
        n.m_128365_("CrystalAt", (Tag)cs);
        return n;
    }

    public static LegacyRun load(CompoundTag n) {
        CompoundTag w;
        LegacyRun r = new LegacyRun();
        r.clock = n.m_128454_("Clock");
        int[] a = n.m_128465_("Boons");
        for (int i = 0; i < 3 && i < a.length; ++i) {
            r.boons[i] = Math.max(0, Math.min(3, a[i]));
        }
        r.integrity = n.m_128441_("Integrity") ? Math.max(0, Math.min(100, n.m_128451_("Integrity"))) : 100;
        r.input = Math.max(0, Math.min(5, n.m_128451_("Input")));
        r.choosing = n.m_128471_("Choosing");
        r.recorded = n.m_128471_("Recorded");
        r.day = n.m_128441_("Day") ? n.m_128454_("Day") : -1L;
        r.seed = n.m_128454_("Seed");
        r.showUntil = n.m_128454_("ShowUntil");
        r.deadline = n.m_128454_("Deadline");
        r.pausedAt = n.m_128441_("PausedAt") ? n.m_128454_("PausedAt") : -1L;
        for (Tag tag : n.m_128437_("Wards", 10)) {
            if (r.wards.size() >= 128 || !(w = (CompoundTag)tag).m_128403_("Id") || !Float.isFinite(w.m_128457_("HP"))) continue;
            r.wards.put(w.m_128342_("Id"), Float.valueOf(Math.max(0.0f, Math.min(6.0f, w.m_128457_("HP")))));
        }
        for (Tag tag : n.m_128437_("CrystalAt", 10)) {
            if (r.crystalAt.size() >= 12 || !(w = (CompoundTag)tag).m_128403_("Id")) continue;
            r.crystalAt.put(w.m_128342_("Id"), w.m_128454_("Time"));
        }
        return r;
    }
}

