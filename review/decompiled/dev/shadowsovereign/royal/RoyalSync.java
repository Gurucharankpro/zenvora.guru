/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 */
package dev.shadowsovereign.royal;

import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public final class RoyalSync {
    public static CompoundTag delta(CompoundTag full, CompoundTag previous) {
        if (previous == null) {
            return full.m_6426_();
        }
        CompoundTag patch = new CompoundTag();
        patch.m_128379_("_Delta", true);
        ListTag removed = new ListTag();
        for (String key : previous.m_128431_()) {
            if (full.m_128441_(key)) continue;
            removed.add((Object)StringTag.m_129297_((String)key));
        }
        if (!removed.isEmpty()) {
            patch.m_128365_("_Removed", (Tag)removed);
        }
        for (String key : full.m_128431_()) {
            if (Objects.equals(full.m_128423_(key), previous.m_128423_(key))) continue;
            if (key.equals("Roster") && RoyalSync.sameIds(full.m_128437_(key, 10), previous.m_128437_(key, 10))) {
                ListTag rows = new ListTag();
                ListTag a = full.m_128437_(key, 10);
                ListTag b = previous.m_128437_(key, 10);
                for (int i = 0; i < a.size(); ++i) {
                    CompoundTag row = RoyalSync.delta(a.m_128728_(i), b.m_128728_(i));
                    if (row.m_128431_().size() <= 1) continue;
                    row.m_128362_("Id", a.m_128728_(i).m_128342_("Id"));
                    rows.add((Object)row);
                }
                patch.m_128365_("_Rows", (Tag)rows);
                continue;
            }
            patch.m_128365_(key, full.m_128423_(key).m_6426_());
        }
        return patch;
    }

    private static boolean sameIds(ListTag a, ListTag b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); ++i) {
            if (a.m_128728_(i).m_128403_("Id") && b.m_128728_(i).m_128403_("Id") && a.m_128728_(i).m_128342_("Id").equals(b.m_128728_(i).m_128342_("Id"))) continue;
            return false;
        }
        return true;
    }

    public static CompoundTag apply(CompoundTag current, CompoundTag patch) {
        if (!patch.m_128471_("_Delta")) {
            return patch.m_6426_();
        }
        CompoundTag result = current.m_6426_();
        for (Tag t : patch.m_128437_("_Removed", 8)) {
            result.m_128473_(t.m_7916_());
        }
        for (String key : patch.m_128431_()) {
            if (key.startsWith("_")) continue;
            result.m_128365_(key, patch.m_128423_(key).m_6426_());
        }
        if (patch.m_128441_("_Rows")) {
            ListTag rows = result.m_128437_("Roster", 10);
            block2: for (Tag tag : patch.m_128437_("_Rows", 10)) {
                CompoundTag change = (CompoundTag)tag;
                for (int i = 0; i < rows.size(); ++i) {
                    if (!rows.m_128728_(i).m_128342_("Id").equals(change.m_128342_("Id"))) continue;
                    rows.set(i, (Tag)RoyalSync.apply(rows.m_128728_(i), change));
                    continue block2;
                }
            }
            result.m_128365_("Roster", (Tag)rows);
        }
        return result;
    }
}

