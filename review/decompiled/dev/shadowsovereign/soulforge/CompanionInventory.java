/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ItemStack
 */
package dev.shadowsovereign.soulforge;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class CompanionInventory {
    public static final int EQUIPMENT = 6;
    public static final int SIZE = 11;
    public static final EquipmentSlot[] SLOTS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public final NonNullList<ItemStack> items = NonNullList.m_122780_((int)11, (Object)ItemStack.f_41583_);
    public boolean initialized;

    public void initialize(CompoundTag snapshot) {
        int i;
        if (this.initialized) {
            return;
        }
        ListTag hands = snapshot.m_128437_("HandItems", 10);
        ListTag armor = snapshot.m_128437_("ArmorItems", 10);
        for (i = 0; i < 2 && i < hands.size(); ++i) {
            this.items.set(i, (Object)ItemStack.m_41712_((CompoundTag)hands.m_128728_(i)));
        }
        for (i = 2; i < 6; ++i) {
            if (SLOTS[i].m_20749_() >= armor.size()) continue;
            this.items.set(i, (Object)ItemStack.m_41712_((CompoundTag)armor.m_128728_(SLOTS[i].m_20749_())));
        }
        this.initialized = true;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < 11; ++i) {
            if (((ItemStack)this.items.get(i)).m_41619_()) continue;
            CompoundTag entry = ((ItemStack)this.items.get(i)).m_41739_(new CompoundTag());
            entry.m_128344_("Slot", (byte)i);
            list.add((Object)entry);
        }
        tag.m_128365_("Items", (Tag)list);
        tag.m_128379_("Initialized", this.initialized);
        return tag;
    }

    public static CompanionInventory load(CompoundTag tag) {
        CompanionInventory inventory = new CompanionInventory();
        inventory.initialized = tag.m_128471_("Initialized");
        for (Tag item : tag.m_128437_("Items", 10)) {
            CompoundTag entry = (CompoundTag)item;
            int slot = entry.m_128445_("Slot") & 0xFF;
            if (slot >= 11) continue;
            inventory.items.set(slot, (Object)ItemStack.m_41712_((CompoundTag)entry));
        }
        return inventory;
    }
}

