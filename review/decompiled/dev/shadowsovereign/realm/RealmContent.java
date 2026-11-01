/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.realm;

import dev.shadowsovereign.network.RealmNetwork;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.realm.WildBosses;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class RealmContent {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    public static final List<RegistryObject<Item>> MATERIALS = new ArrayList<RegistryObject<Item>>();
    public static final RegistryObject<Item> LENS;
    public static final RegistryObject<Item> ROPE;
    public static final RegistryObject<Item> ATLAS;

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    static {
        for (String id : List.of("garden_amber", "copper_gear", "archive_crystal", "mask_fragment")) {
            MATERIALS.add((RegistryObject<Item>)ITEMS.register(id, () -> new Item(new Item.Properties())));
        }
        LENS = ITEMS.register("wildboss_lens", () -> new Item(new Item.Properties().m_41487_(1).m_41497_(Rarity.UNCOMMON)){

            public InteractionResultHolder<ItemStack> m_7203_(Level w, Player p, InteractionHand hand) {
                if (p instanceof ServerPlayer) {
                    ServerPlayer s = (ServerPlayer)p;
                    WildBosses.lens(s);
                }
                return InteractionResultHolder.m_19092_((Object)p.m_21120_(hand), (boolean)w.f_46443_);
            }
        });
        ROPE = ITEMS.register("explorers_rope", () -> new Item(new Item.Properties().m_41487_(16)){

            public InteractionResultHolder<ItemStack> m_7203_(Level w, Player p, InteractionHand hand) {
                ServerPlayer s;
                if (p instanceof ServerPlayer && LivingRealm.rope(s = (ServerPlayer)p) && !s.m_7500_()) {
                    s.m_21120_(hand).m_41774_(1);
                }
                return InteractionResultHolder.m_19092_((Object)p.m_21120_(hand), (boolean)w.f_46443_);
            }
        });
        ATLAS = ITEMS.register("living_realm_atlas", () -> new Item(new Item.Properties().m_41487_(1).m_41497_(Rarity.UNCOMMON)){

            public InteractionResultHolder<ItemStack> m_7203_(Level w, Player p, InteractionHand hand) {
                if (p instanceof ServerPlayer) {
                    ServerPlayer s = (ServerPlayer)p;
                    RealmNetwork.open(s, 0);
                }
                return InteractionResultHolder.m_19092_((Object)p.m_21120_(hand), (boolean)w.f_46443_);
            }
        });
    }
}

