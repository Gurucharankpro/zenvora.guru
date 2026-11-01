/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraftforge.common.ForgeSpawnEggItem
 */
package dev.shadowsovereign;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;

public final class PixelSpawnEgg
extends ForgeSpawnEggItem {
    public PixelSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight, Item.Properties properties) {
        super(type, background, highlight, properties);
    }

    public int m_43211_(int layer) {
        return -1;
    }
}

