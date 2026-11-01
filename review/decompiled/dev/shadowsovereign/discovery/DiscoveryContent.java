/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.discovery;

import dev.shadowsovereign.discovery.SoulSigils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class DiscoveryContent {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    public static final RegistryObject<Item> SIGIL = ITEMS.register("unbound_soul_sigil", () -> new Item(new Item.Properties().m_41487_(16).m_41497_(Rarity.EPIC)){

        public boolean m_5812_(ItemStack s) {
            return true;
        }

        public void m_7373_(ItemStack s, Level w, List<Component> lines, TooltipFlag f) {
            lines.add((Component)Component.m_237115_((String)"discovery.shadowsovereign.sigil_tip"));
            lines.add((Component)Component.m_237115_((String)"discovery.shadowsovereign.sigil_tip2"));
        }

        public InteractionResultHolder<ItemStack> m_7203_(Level w, Player p, InteractionHand hand) {
            ItemStack stack = p.m_21120_(hand);
            if (p instanceof ServerPlayer) {
                ServerPlayer owner = (ServerPlayer)p;
                if (p.m_36335_().m_41519_((Item)this)) {
                    return InteractionResultHolder.m_19100_((Object)stack);
                }
                if (!SoulSigils.use(owner)) {
                    return InteractionResultHolder.m_19100_((Object)stack);
                }
                if (!p.m_150110_().f_35937_) {
                    stack.m_41774_(1);
                }
                p.m_36335_().m_41524_((Item)this, 40);
            }
            return InteractionResultHolder.m_19092_((Object)stack, (boolean)w.f_46443_);
        }
    });

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}

