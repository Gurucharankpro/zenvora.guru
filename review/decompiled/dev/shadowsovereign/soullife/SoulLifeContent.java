/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.soullife;

import dev.shadowsovereign.artisan.WorkshopShapes;
import dev.shadowsovereign.network.SoulLifeNetwork;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.soullife.SoulLifeText;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class SoulLifeContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCK_ENTITY_TYPES, (String)"shadowsovereign");
    public static final RegistryObject<Hearth> HEARTH = BLOCKS.register("soul_hearth", Hearth::new);
    public static final RegistryObject<Item> HEARTH_ITEM = ITEMS.register("soul_hearth", () -> new BlockItem((Block)HEARTH.get(), new Item.Properties()));
    public static final RegistryObject<Item> TOY = ITEMS.register("soul_playball", () -> new Item(new Item.Properties().m_41487_(16)));
    public static final RegistryObject<Item> BOOK = ITEMS.register("soul_storybook", () -> new Item(new Item.Properties().m_41487_(1)){

        public InteractionResultHolder<ItemStack> m_7203_(Level level, Player user, InteractionHand hand) {
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                SoulLifeNetwork.send(p, true, 4);
            }
            return InteractionResultHolder.m_19092_((Object)user.m_21120_(hand), (boolean)level.f_46443_);
        }
    });
    public static final RegistryObject<BlockEntityType<HearthEntity>> ENTITY = ENTITIES.register("soul_hearth", () -> BlockEntityType.Builder.m_155273_(HearthEntity::new, (Block[])new Block[]{(Block)HEARTH.get()}).m_58966_(null));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(e -> {
            if (e.getTabKey() == CreativeModeTabs.f_256869_) {
                e.m_246326_((ItemLike)HEARTH_ITEM.get());
                e.m_246326_((ItemLike)BOOK.get());
            }
        });
    }

    public static boolean bind(ServerPlayer p, BlockPos at) {
        block3: {
            block2: {
                BlockEntity blockEntity;
                if (!p.m_284548_().m_46805_(at) || p.m_20238_(Vec3.m_82512_((Vec3i)at)) > 64.0 || !((blockEntity = p.m_9236_().m_7702_(at)) instanceof HearthEntity)) break block2;
                HearthEntity be = (HearthEntity)blockEntity;
                if (p.m_20148_().equals(be.owner)) break block3;
            }
            return false;
        }
        CompoundTag home = SoulLifeService.owner((ServerPlayer)p).home;
        home.m_128356_("Pos", at.m_121878_());
        home.m_128359_("Dimension", p.m_9236_().m_46472_().m_135782_().toString());
        SoulLifeService.dirty(p);
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static BlockPos near(ServerPlayer p) {
        CompoundTag home = SoulLifeService.owner((ServerPlayer)p).home;
        if (!home.m_128441_("Pos")) return null;
        if (!home.m_128461_("Dimension").equals(p.m_9236_().m_46472_().m_135782_().toString())) {
            return null;
        }
        BlockPos at = BlockPos.m_122022_((long)home.m_128454_("Pos"));
        if (!p.m_284548_().m_46805_(at)) return null;
        if (!(p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 64.0)) return null;
        BlockEntity blockEntity = p.m_9236_().m_7702_(at);
        if (!(blockEntity instanceof HearthEntity)) return null;
        HearthEntity be = (HearthEntity)blockEntity;
        if (!p.m_20148_().equals(be.owner)) return null;
        BlockPos blockPos = at;
        return blockPos;
    }

    public static final class HearthEntity
    extends BlockEntity {
        public UUID owner;

        public HearthEntity(BlockPos at, BlockState state) {
            super((BlockEntityType)ENTITY.get(), at, state);
        }

        protected void m_183515_(CompoundTag n) {
            super.m_183515_(n);
            if (this.owner != null) {
                n.m_128362_("Owner", this.owner);
            }
        }

        public void m_142466_(CompoundTag n) {
            super.m_142466_(n);
            this.owner = n.m_128403_("Owner") ? n.m_128342_("Owner") : null;
        }
    }

    public static final class Hearth
    extends BaseEntityBlock {
        public VoxelShape m_5940_(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) {
            return WorkshopShapes.shape("soul_hearth", Direction.NORTH);
        }

        private Hearth() {
            super(BlockBehaviour.Properties.m_284310_().m_60978_(2.0f).m_60955_().m_60953_(s -> 12));
        }

        public RenderShape m_7514_(BlockState s) {
            return RenderShape.MODEL;
        }

        public BlockEntity m_142194_(BlockPos at, BlockState state) {
            return new HearthEntity(at, state);
        }

        public void m_6402_(Level level, BlockPos at, BlockState state, LivingEntity user, ItemStack stack) {
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                BlockEntity blockEntity = level.m_7702_(at);
                if (blockEntity instanceof HearthEntity) {
                    HearthEntity be = (HearthEntity)blockEntity;
                    be.owner = p.m_20148_();
                    be.m_6596_();
                    level.m_7260_(at, state, state, 3);
                }
            }
        }

        public InteractionResult m_6227_(BlockState state, Level level, BlockPos at, Player user, InteractionHand hand, BlockHitResult hit) {
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                BlockEntity blockEntity = level.m_7702_(at);
                if (blockEntity instanceof HearthEntity) {
                    HearthEntity be = (HearthEntity)blockEntity;
                    if (be.owner == null) {
                        be.owner = p.m_20148_();
                        be.m_6596_();
                    }
                    if (!p.m_20148_().equals(be.owner)) {
                        SoulLifeText.tell(p, "not_yours", new Object[0]);
                        return InteractionResult.FAIL;
                    }
                    SoulLifeContent.bind(p, at);
                    SoulLifeNetwork.send(p, true, 4);
                }
            }
            return InteractionResult.m_19078_((boolean)level.f_46443_);
        }
    }
}

