/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.storage.loot.LootPool
 *  net.minecraft.world.level.storage.loot.LootPool$Builder
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.level.storage.loot.entries.LootItem
 *  net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer$Builder
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
 *  net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
 *  net.minecraft.world.level.storage.loot.providers.number.ConstantValue
 *  net.minecraft.world.level.storage.loot.providers.number.NumberProvider
 *  net.minecraftforge.event.LootTableLoadEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.discovery;

import dev.shadowsovereign.discovery.DiscoveryContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class DiscoveryLoot {
    public static final String POOL = "shadowsovereign_unbound_soul";

    public static boolean eligible(ResourceLocation id, LootTable table) {
        return id.equals((Object)new ResourceLocation("chests/spawn_bonus_chest")) || table.m_79122_() == LootContextParamSets.f_81411_ || table.m_79122_() == LootContextParamSets.f_81420_ && (id.m_135815_().startsWith("chests/") || id.m_135815_().contains("/chests/") || id.m_135815_().startsWith("dungeons/"));
    }

    public static void inject(ResourceLocation id, LootTable table) {
        if (!DiscoveryLoot.eligible(id, table) || table.getPool(POOL) != null) {
            return;
        }
        LootPool.Builder pool = LootPool.m_79043_().name(POOL).m_165133_((NumberProvider)ConstantValue.m_165692_((float)1.0f)).m_79076_((LootPoolEntryContainer.Builder)LootItem.m_79579_((ItemLike)((ItemLike)DiscoveryContent.SIGIL.get())));
        if (!id.equals((Object)new ResourceLocation("chests/spawn_bonus_chest"))) {
            pool.m_79080_(LootItemRandomChanceCondition.m_81927_((float)0.01f));
        }
        table.addPool(pool.m_79082_());
    }

    @SubscribeEvent
    public static void loaded(LootTableLoadEvent e) {
        DiscoveryLoot.inject(e.getName(), e.getTable());
    }
}

