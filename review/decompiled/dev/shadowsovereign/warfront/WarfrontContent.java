/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.warfront;

import dev.shadowsovereign.network.WarfrontNetwork;
import dev.shadowsovereign.warfront.WarfrontService;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class WarfrontContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    public static final RegistryObject<Block> BEACON = BLOCKS.register("warfront_beacon", () -> new Block(BlockBehaviour.Properties.m_284310_().m_60978_(4.0f).m_60953_(s -> 12).m_60955_()){
        private final VoxelShape shape = Shapes.m_83124_((VoxelShape)Block.m_49796_((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)3.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.m_49796_((double)2.0, (double)3.0, (double)2.0, (double)14.0, (double)5.0, (double)14.0), Block.m_49796_((double)5.0, (double)5.0, (double)5.0, (double)11.0, (double)16.0, (double)11.0)});

        public VoxelShape m_5940_(BlockState state, BlockGetter level, BlockPos at, CollisionContext context) {
            return this.shape;
        }

        public void m_6402_(Level l, BlockPos at, BlockState state, LivingEntity e, ItemStack stack) {
            super.m_6402_(l, at, state, e, stack);
            if (e instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)e;
                WarfrontService.bind(p, at);
            }
        }

        public InteractionResult m_6227_(BlockState state, Level l, BlockPos at, Player e, InteractionHand hand, BlockHitResult hit) {
            if (e instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)e;
                UUID owner = WarfrontService.beaconOwner(p.f_8924_, l.m_46472_().m_135782_().toString(), at);
                if (owner != null && !owner.equals(p.m_20148_())) {
                    WarfrontService.tell(p, "This Warfront Beacon belongs to another player.");
                    return InteractionResult.FAIL;
                }
                if (owner == null && !WarfrontService.bind(p, at)) {
                    return InteractionResult.FAIL;
                }
                WarfrontNetwork.send(p, true, null);
            }
            return InteractionResult.m_19078_((boolean)l.f_46443_);
        }

        public void m_6810_(BlockState s, Level l, BlockPos at, BlockState next, boolean moving) {
            if (!l.f_46443_ && !s.m_60713_(next.m_60734_())) {
                WarfrontService.removed(l, at);
            }
            super.m_6810_(s, l, at, next, moving);
        }
    });
    public static final RegistryObject<Item> BEACON_ITEM = ITEMS.register("warfront_beacon", () -> new BlockItem((Block)BEACON.get(), new Item.Properties().m_41497_(Rarity.EPIC)));

    public static void register(IEventBus b) {
        BLOCKS.register(b);
        ITEMS.register(b);
        b.addListener(WarfrontContent::creative);
    }

    private static void creative(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.f_256797_) {
            e.accept(BEACON_ITEM);
        }
    }
}

