/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.minecraft.world.level.block.state.BlockBehaviour
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
package dev.shadowsovereign.sentinel;

import dev.shadowsovereign.network.SentinelNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

public final class SentinelContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCK_ENTITY_TYPES, (String)"shadowsovereign");
    public static final RegistryObject<Block> TORCH = BLOCKS.register("patrol_torch", () -> new BaseEntityBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50681_).m_60978_(2.0f).m_60953_(s -> 15).m_60955_()){

        public RenderShape m_7514_(BlockState s) {
            return RenderShape.MODEL;
        }

        public VoxelShape m_5940_(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) {
            return Block.m_49796_((double)5.0, (double)0.0, (double)5.0, (double)11.0, (double)16.0, (double)11.0);
        }

        public BlockEntity m_142194_(BlockPos p, BlockState s) {
            return new Node(p, s);
        }

        public void m_6402_(Level w, BlockPos at, BlockState s, LivingEntity e, ItemStack stack) {
            if (e instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)e;
                BlockEntity blockEntity = w.m_7702_(at);
                if (blockEntity instanceof Node) {
                    Node n = (Node)blockEntity;
                    n.owner = p.m_20148_();
                    n.m_6596_();
                }
            }
        }

        /*
         * Enabled aggressive block sorting
         */
        public InteractionResult m_6227_(BlockState s, Level w, BlockPos at, Player p, InteractionHand hand, BlockHitResult hit) {
            if (p.m_21120_(hand).m_150930_((Item)LINKER.get())) {
                return InteractionResult.PASS;
            }
            if (!(p instanceof ServerPlayer)) return InteractionResult.m_19078_((boolean)w.f_46443_);
            ServerPlayer owner = (ServerPlayer)p;
            BlockEntity blockEntity = w.m_7702_(at);
            if (blockEntity instanceof Node) {
                Node n = (Node)blockEntity;
                if (owner.m_20148_().equals(n.owner)) {
                    SentinelNetwork.open(owner, at, null);
                    return InteractionResult.m_19078_((boolean)w.f_46443_);
                }
            }
            owner.m_5661_((Component)Component.m_237115_((String)"sentinel.shadowsovereign.owner_only"), true);
            return InteractionResult.m_19078_((boolean)w.f_46443_);
        }
    });
    public static final RegistryObject<Item> TORCH_ITEM = ITEMS.register("patrol_torch", () -> new BlockItem((Block)TORCH.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<Node>> NODE = TILES.register("patrol_torch", () -> BlockEntityType.Builder.m_155273_(Node::new, (Block[])new Block[]{(Block)TORCH.get()}).m_58966_(null));
    public static final RegistryObject<Item> LINKER = ITEMS.register("soul_route_linker", () -> new Item(new Item.Properties().m_41487_(1).m_41497_(Rarity.UNCOMMON)){

        public boolean m_5812_(ItemStack s) {
            return s.m_41782_() && s.m_41783_().m_128441_("First");
        }

        public void m_7373_(ItemStack s, Level w, List<Component> lines, TooltipFlag f) {
            lines.add((Component)Component.m_237115_((String)"sentinel.shadowsovereign.linker_tip"));
        }

        public InteractionResult m_6225_(UseOnContext c) {
            block7: {
                Node a;
                BlockPos first;
                CompoundTag tag;
                Node node;
                ServerPlayer p;
                block9: {
                    block8: {
                        BlockEntity blockEntity;
                        Player player = c.m_43723_();
                        if (!(player instanceof ServerPlayer)) break block7;
                        p = (ServerPlayer)player;
                        player = c.m_43725_().m_7702_(c.m_8083_());
                        if (!(player instanceof Node)) break block7;
                        node = (Node)player;
                        if (!p.m_20148_().equals(node.owner)) {
                            p.m_5661_((Component)Component.m_237115_((String)"sentinel.shadowsovereign.owner_only"), true);
                            return InteractionResult.FAIL;
                        }
                        ItemStack stack = c.m_43722_();
                        tag = stack.m_41784_();
                        if (p.m_6144_()) {
                            node.next = null;
                            node.nextId = null;
                            node.m_6596_();
                            tag.m_128473_("First");
                            tag.m_128473_("FirstId");
                            p.m_5661_((Component)Component.m_237115_((String)"sentinel.shadowsovereign.unlinked"), true);
                            return InteractionResult.SUCCESS;
                        }
                        String dimension = p.m_9236_().m_46472_().m_135782_().toString();
                        if (!(tag.m_128441_("First") && dimension.equals(tag.m_128461_("Dimension")) && tag.m_128403_("FirstId"))) {
                            tag.m_128356_("First", c.m_8083_().m_121878_());
                            tag.m_128362_("FirstId", node.id);
                            tag.m_128359_("Dimension", dimension);
                            p.m_5661_((Component)Component.m_237115_((String)"sentinel.shadowsovereign.first"), true);
                            return InteractionResult.SUCCESS;
                        }
                        first = BlockPos.m_122022_((long)tag.m_128454_("First"));
                        if (first.equals((Object)c.m_8083_()) || first.m_123331_((Vec3i)c.m_8083_()) > 9216.0 || !c.m_43725_().m_46805_(first) || !((blockEntity = c.m_43725_().m_7702_(first)) instanceof Node)) break block8;
                        a = (Node)blockEntity;
                        if (p.m_20148_().equals(a.owner) && a.id.equals(tag.m_128342_("FirstId"))) break block9;
                    }
                    tag.m_128473_("First");
                    p.m_5661_((Component)Component.m_237115_((String)"sentinel.shadowsovereign.bad_link"), true);
                    return InteractionResult.FAIL;
                }
                a.next = c.m_8083_().m_7949_();
                a.nextId = node.id;
                a.m_6596_();
                for (int i = 0; i <= 32; ++i) {
                    Vec3 spot = Vec3.m_82512_((Vec3i)first).m_165921_(Vec3.m_82512_((Vec3i)a.next), (double)i / 32.0);
                    p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_175830_, spot.f_82479_, spot.f_82480_ + 0.4, spot.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
                }
                tag.m_128473_("First");
                tag.m_128473_("FirstId");
                GuideService.record(p, "sentinel.link");
                p.m_5661_((Component)Component.m_237115_((String)"sentinel.shadowsovereign.linked"), true);
                return InteractionResult.SUCCESS;
            }
            return c.m_43725_().f_46443_ ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        public InteractionResultHolder<ItemStack> m_7203_(Level w, Player p, InteractionHand h) {
            if (p instanceof ServerPlayer) {
                ServerPlayer owner = (ServerPlayer)p;
                SentinelNetwork.open(owner, null, null);
            }
            return InteractionResultHolder.m_19092_((Object)p.m_21120_(h), (boolean)w.f_46443_);
        }
    });

    public static void register(IEventBus b) {
        BLOCKS.register(b);
        ITEMS.register(b);
        TILES.register(b);
    }

    public static final class Node
    extends BlockEntity {
        public UUID id = UUID.randomUUID();
        public UUID owner;
        public UUID nextId;
        public BlockPos next;

        public Node(BlockPos p, BlockState s) {
            super((BlockEntityType)NODE.get(), p, s);
        }

        public void m_142466_(CompoundTag n) {
            super.m_142466_(n);
            if (n.m_128403_("Id")) {
                this.id = n.m_128342_("Id");
            }
            this.owner = n.m_128403_("Owner") ? n.m_128342_("Owner") : null;
            this.next = n.m_128441_("Next") ? BlockPos.m_122022_((long)n.m_128454_("Next")) : null;
            this.nextId = n.m_128403_("NextId") ? n.m_128342_("NextId") : null;
        }

        protected void m_183515_(CompoundTag n) {
            super.m_183515_(n);
            n.m_128362_("Id", this.id);
            if (this.owner != null) {
                n.m_128362_("Owner", this.owner);
            }
            if (this.next != null) {
                n.m_128356_("Next", this.next.m_121878_());
            }
            if (this.nextId != null) {
                n.m_128362_("NextId", this.nextId);
            }
        }
    }
}

