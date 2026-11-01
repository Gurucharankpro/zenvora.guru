/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.SpawnEggItem
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign;

import dev.shadowsovereign.ShadowSovereign;
import dev.shadowsovereign.expansion.CreativeGroups;
import dev.shadowsovereign.gloom.RetiredContent;
import java.util.Comparator;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTab {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create((ResourceKey)Registries.f_279569_, (String)"shadowsovereign");
    public static final RegistryObject<CreativeModeTab> ARMY = ModCreativeTab.tab("soulbound_army", 0);
    public static final RegistryObject<CreativeModeTab> CREATURES = ModCreativeTab.tab("creatures", 1);
    public static final RegistryObject<CreativeModeTab> NATURE = ModCreativeTab.tab("nature", 2);
    public static final RegistryObject<CreativeModeTab> WORKSHOPS = ModCreativeTab.tab("workshops", 3);

    public static int legacyCategory(Item item, String path) {
        if (item instanceof SpawnEggItem) {
            return 1;
        }
        if (path.startsWith("odyssey_") && !path.startsWith("odyssey_artifact_") || path.matches(".*(forge|workshop|cemetery|altar|station|anvil).*")) {
            return 3;
        }
        if (item instanceof BlockItem || item.m_41472_() || path.matches(".*(seed|sapling|wheat|carrot|potato|pepper|berry|tea|reed|spore).*")) {
            return 2;
        }
        return 0;
    }

    private static RegistryObject<CreativeModeTab> tab(String id, int category) {
        return TABS.register(id, () -> CreativeModeTab.builder().m_257941_((Component)Component.m_237115_((String)("itemGroup.shadowsovereign." + id))).m_257737_(() -> new ItemStack((ItemLike)ShadowSovereign.MONARCH_CREST.get())).withTabsBefore(new ResourceKey[]{CreativeModeTabs.f_256731_}).m_257501_((p, o) -> ForgeRegistries.ITEMS.getEntries().stream().filter(e -> ((ResourceKey)e.getKey()).m_135782_().m_135827_().equals("shadowsovereign") && e.getValue() != Items.f_41852_ && !RetiredContent.item(((ResourceKey)e.getKey()).m_135782_()) && ModCreativeTab.category((Item)e.getValue(), ((ResourceKey)e.getKey()).m_135782_().m_135815_()) == category).sorted(Comparator.comparing(e -> ((ResourceKey)e.getKey()).m_135782_().toString())).forEach(e -> o.m_246326_((ItemLike)e.getValue()))).m_257652_());
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }

    public static int category(Item item, String string) {
        return CreativeGroups.category(string, ModCreativeTab.legacyCategory(item, string), item instanceof BlockItem);
    }
}

