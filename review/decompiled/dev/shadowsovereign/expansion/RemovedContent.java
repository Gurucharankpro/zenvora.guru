/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 */
package dev.shadowsovereign.expansion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class RemovedContent {
    private static final String TYPE = "shadowsovereign:villager_knight";

    private RemovedContent() {
    }

    private static boolean retired(Tag tag) {
        CompoundTag compoundTag;
        return tag instanceof CompoundTag && TYPE.equals((compoundTag = (CompoundTag)tag).m_128461_("Type"));
    }

    public static void army(CompoundTag compoundTag) {
        for (Tag tag2 : compoundTag.m_128437_("Armies", 10)) {
            CompoundTag compoundTag2 = (CompoundTag)tag2;
            compoundTag2.m_128437_("Shadows", 10).removeIf(RemovedContent::retired);
            compoundTag2.m_128437_("Souls", 10).removeIf(tag -> {
                CompoundTag compoundTag;
                return tag instanceof CompoundTag && RemovedContent.retired((Tag)(compoundTag = (CompoundTag)tag).m_128469_("Companion"));
            });
            compoundTag2.m_128437_("ChosenTypes", 8).removeIf(tag -> TYPE.equals(tag.m_7916_()));
        }
    }

    public static void adventures(CompoundTag compoundTag) {
        for (Tag tag : compoundTag.m_128437_("Journals", 10)) {
            CompoundTag compoundTag2 = (CompoundTag)tag;
            compoundTag2.m_128469_("Mythic").m_128469_("Gloom").m_128473_("villager_knight");
            CompoundTag compoundTag3 = compoundTag2.m_128469_("LivingRealm");
            compoundTag3.m_128437_("Pending", 10).removeIf(RemovedContent::retired);
            compoundTag3.m_128469_("Bestiary").m_128473_(TYPE);
        }
    }
}

