/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CompassItem
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.ShieldItem
 *  net.minecraft.world.item.UseAnim
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
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.vigil;

import dev.shadowsovereign.artisan.WorkshopShapes;
import dev.shadowsovereign.network.VigilNetwork;
import dev.shadowsovereign.vigil.VigilService;
import dev.shadowsovereign.vigil.VigilState;
import dev.shadowsovereign.vigil.VigilText;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.UseAnim;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class VigilContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCK_ENTITY_TYPES, (String)"shadowsovereign");
    public static final RegistryObject<PostBlock> GRAVE = BLOCKS.register("soul_cemetery", () -> new PostBlock(true));
    public static final RegistryObject<PostBlock> CACHE = BLOCKS.register("supply_cache", () -> new PostBlock(false));
    public static final RegistryObject<Item> GRAVE_ITEM = ITEMS.register("soul_cemetery", () -> new BlockItem((Block)GRAVE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CACHE_ITEM = ITEMS.register("supply_cache", () -> new BlockItem((Block)CACHE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BANDAGE = ITEMS.register("field_bandage", () -> new ToolItem(0));
    public static final RegistryObject<Item> HORN = ITEMS.register("soul_horn", () -> new ToolItem(1));
    public static final RegistryObject<Item> LENS = ITEMS.register("echo_lens", () -> new ToolItem(2));
    public static final RegistryObject<Item> SNARE = ITEMS.register("shadow_snare", () -> new ToolItem(3));
    public static final RegistryObject<Item> FEAST = ITEMS.register("rally_feast", () -> new ToolItem(4));
    public static final RegistryObject<Item> COMPASS = ITEMS.register("soul_compass", () -> new CompassItem(new Item.Properties().m_41487_(1)){

        public InteractionResultHolder<ItemStack> m_7203_(Level l, Player user, InteractionHand hand) {
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                VigilService.compass(p, p.m_21120_(hand));
            }
            return InteractionResultHolder.m_19092_((Object)user.m_21120_(hand), (boolean)l.f_46443_);
        }
    });
    public static final RegistryObject<Item> SHIELD = ITEMS.register("prism_shield", () -> new ShieldItem(new Item.Properties().m_41503_(384)));
    public static final RegistryObject<Item> TROPHY = ITEMS.register("ferryman_seal", () -> new Item(new Item.Properties().m_41487_(1).m_41497_(Rarity.EPIC)));
    public static final RegistryObject<BlockEntityType<PostEntity>> ENTITY = ENTITIES.register("vigil_post", () -> BlockEntityType.Builder.m_155273_(PostEntity::new, (Block[])new Block[]{(Block)GRAVE.get(), (Block)CACHE.get()}).m_58966_(null));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(e -> {
            if (e.getTabKey() == CreativeModeTabs.f_256797_) {
                for (RegistryObject item : new RegistryObject[]{GRAVE_ITEM, CACHE_ITEM, BANDAGE, HORN, LENS, SNARE, FEAST, COMPASS, SHIELD, TROPHY}) {
                    e.m_246326_((ItemLike)((Item)item.get()));
                }
            }
        });
    }

    public static final class ToolItem
    extends Item {
        final int kind;

        ToolItem(int kind) {
            super(new Item.Properties().m_41487_(kind == 1 || kind == 2 ? 1 : 16));
            this.kind = kind;
        }

        public UseAnim m_6164_(ItemStack s) {
            return this.kind == 0 ? UseAnim.BOW : UseAnim.NONE;
        }

        public int m_8105_(ItemStack s) {
            return 32;
        }

        public InteractionResultHolder<ItemStack> m_7203_(Level l, Player user, InteractionHand hand) {
            ItemStack stack = user.m_21120_(hand);
            if (this.kind == 0) {
                if (user.m_21223_() >= user.m_21233_()) {
                    return InteractionResultHolder.m_19100_((Object)stack);
                }
                user.m_6672_(hand);
            } else if (user instanceof ServerPlayer) {
                boolean used;
                ServerPlayer p = (ServerPlayer)user;
                switch (this.kind) {
                    case 1: {
                        boolean bl = VigilService.horn(p);
                        break;
                    }
                    case 2: {
                        boolean bl = VigilService.inspect(p);
                        break;
                    }
                    case 3: {
                        boolean bl = VigilService.snare(p);
                        break;
                    }
                    case 4: {
                        boolean bl = VigilService.feast(p);
                        break;
                    }
                    default: {
                        boolean bl = used = false;
                    }
                }
                if (used && (this.kind == 3 || this.kind == 4) && !p.m_150110_().f_35937_) {
                    stack.m_41774_(1);
                }
            }
            return InteractionResultHolder.m_19092_((Object)stack, (boolean)l.f_46443_);
        }

        public ItemStack m_5922_(ItemStack stack, Level l, LivingEntity user) {
            ServerPlayer p;
            if (this.kind == 0 && user instanceof ServerPlayer && VigilService.bandage(p = (ServerPlayer)user) && !p.m_150110_().f_35937_) {
                stack.m_41774_(1);
            }
            return stack;
        }
    }

    public static final class PostBlock
    extends BaseEntityBlock {
        final boolean grave;

        public VoxelShape m_5940_(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) {
            return WorkshopShapes.shape(this.grave ? "soul_cemetery" : "supply_cache", Direction.NORTH);
        }

        PostBlock(boolean grave) {
            super(BlockBehaviour.Properties.m_284310_().m_60978_(3.0f).m_60955_().m_60953_(s -> 6));
            this.grave = grave;
        }

        public RenderShape m_7514_(BlockState s) {
            return RenderShape.MODEL;
        }

        public BlockEntity m_142194_(BlockPos p, BlockState s) {
            return new PostEntity(p, s);
        }

        public void m_6402_(Level l, BlockPos at, BlockState state, LivingEntity user, ItemStack item) {
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                BlockEntity blockEntity = l.m_7702_(at);
                if (blockEntity instanceof PostEntity) {
                    PostEntity be = (PostEntity)blockEntity;
                    be.owner = p.m_20148_();
                    be.m_6596_();
                    l.m_7260_(at, state, state, 3);
                }
            }
        }

        public InteractionResult m_6227_(BlockState s, Level l, BlockPos at, Player user, InteractionHand hand, BlockHitResult hit) {
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                BlockEntity blockEntity = l.m_7702_(at);
                if (blockEntity instanceof PostEntity) {
                    PostEntity be = (PostEntity)blockEntity;
                    if (be.owner == null) {
                        be.owner = p.m_20148_();
                        be.m_6596_();
                    }
                    if (!p.m_20148_().equals(be.owner)) {
                        VigilText.tell(p, "not_yours", new Object[0]);
                        return InteractionResult.FAIL;
                    }
                    if (this.grave) {
                        VigilNetwork.send(p, true, 2);
                    } else {
                        p.m_5893_((MenuProvider)new SimpleMenuProvider((id, inv, player) -> ChestMenu.m_39237_((int)id, (Inventory)inv, (Container)be), (Component)VigilText.c("cache", new Object[0])));
                    }
                }
            }
            return InteractionResult.m_19078_((boolean)l.f_46443_);
        }

        public void m_6810_(BlockState s, Level l, BlockPos at, BlockState next, boolean moving) {
            BlockEntity blockEntity;
            if (!s.m_60713_(next.m_60734_()) && !l.f_46443_ && (blockEntity = l.m_7702_(at)) instanceof PostEntity) {
                PostEntity be = (PostEntity)blockEntity;
                if (!this.grave) {
                    Containers.m_19002_((Level)l, (BlockPos)at, (Container)be);
                    be.m_6211_();
                }
            }
            super.m_6810_(s, l, at, next, moving);
        }
    }

    public static final class PostEntity
    extends BlockEntity
    implements Container {
        public UUID owner;
        public final NonNullList<ItemStack> items = NonNullList.m_122780_((int)27, (Object)ItemStack.f_41583_);

        public PostEntity(BlockPos at, BlockState state) {
            super((BlockEntityType)ENTITY.get(), at, state);
        }

        protected void m_183515_(CompoundTag n) {
            super.m_183515_(n);
            if (this.owner != null) {
                n.m_128362_("Owner", this.owner);
            }
            ContainerHelper.m_18973_((CompoundTag)n, this.items);
        }

        public void m_142466_(CompoundTag n) {
            super.m_142466_(n);
            this.owner = VigilState.id(n, "Owner");
            this.items.clear();
            ContainerHelper.m_18980_((CompoundTag)n, this.items);
        }

        public int m_6643_() {
            return 27;
        }

        public boolean m_7983_() {
            return this.items.stream().allMatch(ItemStack::m_41619_);
        }

        public ItemStack m_8020_(int i) {
            return (ItemStack)this.items.get(i);
        }

        public ItemStack m_7407_(int i, int n) {
            ItemStack s = ContainerHelper.m_18969_(this.items, (int)i, (int)n);
            this.m_6596_();
            return s;
        }

        public ItemStack m_8016_(int i) {
            return ContainerHelper.m_18966_(this.items, (int)i);
        }

        public void m_6836_(int i, ItemStack s) {
            this.items.set(i, (Object)s);
            if (s.m_41613_() > this.m_6893_()) {
                s.m_41764_(this.m_6893_());
            }
            this.m_6596_();
        }

        public void m_6211_() {
            this.items.clear();
            this.m_6596_();
        }

        public boolean m_6542_(Player p) {
            return p.m_6084_() && p.m_20148_().equals(this.owner) && this.f_58857_ != null && this.f_58857_.m_7702_(this.f_58858_) == this && p.m_20275_((double)this.f_58858_.m_123341_() + 0.5, (double)this.f_58858_.m_123342_() + 0.5, (double)this.f_58858_.m_123343_() + 0.5) <= 64.0;
        }
    }
}

