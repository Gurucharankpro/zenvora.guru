/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties$Builder
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.client.extensions.common.IClientItemExtensions
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.artisan.WorkshopShapes;
import dev.shadowsovereign.client.OdysseyItems;
import dev.shadowsovereign.network.MasteryNetwork;
import dev.shadowsovereign.network.OdysseyNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyCamp;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.odyssey.OdysseyText;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class OdysseyContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCK_ENTITY_TYPES, (String)"shadowsovereign");
    public static final RegistryObject<Item> BOOK = ITEMS.register("sovereign_journey", () -> new AtlasItem(new Item.Properties().m_41487_(1).m_41497_(Rarity.UNCOMMON)){

        public InteractionResultHolder<ItemStack> m_7203_(Level world, Player p, InteractionHand hand) {
            if (p instanceof ServerPlayer) {
                ServerPlayer user = (ServerPlayer)p;
                MasteryNetwork.open(user);
            }
            return InteractionResultHolder.m_19092_((Object)p.m_21120_(hand), (boolean)world.f_46443_);
        }
    });
    public static final RegistryObject<Item> BALL = ITEMS.register("festival_ball", () -> new AtlasItem(new Item.Properties().m_41487_(16)));
    public static final RegistryObject<Item> PARCEL = ITEMS.register("festival_parcel", () -> new AtlasItem(new Item.Properties().m_41487_(1)));
    public static final RegistryObject<Item> TOKEN = ITEMS.register("arena_token", () -> new AtlasItem(new Item.Properties()));
    public static final RegistryObject<Item> LETTER = ITEMS.register("memory_letter", () -> new AtlasItem(new Item.Properties().m_41487_(1)){

        public InteractionResultHolder<ItemStack> m_7203_(Level world, Player p, InteractionHand hand) {
            if (p instanceof ServerPlayer) {
                ServerPlayer user = (ServerPlayer)p;
                OdysseyNetwork.open(user, 5);
            }
            return InteractionResultHolder.m_19092_((Object)p.m_21120_(hand), (boolean)world.f_46443_);
        }
    });
    public static final RegistryObject<Item> FEAST = ITEMS.register("hearth_banquet", () -> new AtlasItem(new Item.Properties().m_41489_(new FoodProperties.Builder().m_38760_(10).m_38758_(0.8f).m_38767_())));
    public static final Map<Station, RegistryObject<CampBlock>> STATIONS = new EnumMap<Station, RegistryObject<CampBlock>>(Station.class);
    public static final List<RegistryObject<Item>> ARTIFACTS = new ArrayList<RegistryObject<Item>>();
    public static final RegistryObject<Block> PORTAL;
    public static final RegistryObject<BlockEntityType<CampEntity>> ENTITY;

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
    }

    static {
        for (Station kind : Station.values()) {
            String id = "odyssey_" + kind.name().toLowerCase(Locale.ROOT);
            RegistryObject block = BLOCKS.register(id, () -> new CampBlock(kind));
            STATIONS.put(kind, (RegistryObject<CampBlock>)block);
            ITEMS.register(id, () -> new BlockItem((Block)block.get(), new Item.Properties()));
        }
        for (int i = 0; i < 24; ++i) {
            final int index = i;
            ARTIFACTS.add((RegistryObject<Item>)ITEMS.register("odyssey_artifact_" + i, () -> new AtlasItem(new Item.Properties().m_41487_(1).m_41497_(Rarity.RARE)){

                public InteractionResultHolder<ItemStack> m_7203_(Level level, Player p, InteractionHand hand) {
                    if (p instanceof ServerPlayer) {
                        ServerPlayer user = (ServerPlayer)p;
                        OdysseyCamp.equipArtifact(user, index);
                    }
                    return InteractionResultHolder.m_19092_((Object)p.m_21120_(hand), (boolean)level.f_46443_);
                }
            }));
        }
        PORTAL = BLOCKS.register("journey_surface", OdysseyRealm.Surface::new);
        ENTITY = ENTITIES.register("odyssey_camp", () -> BlockEntityType.Builder.m_155273_(CampEntity::new, (Block[])((Block[])Stream.concat(STATIONS.values().stream().map(RegistryObject::get), Stream.of((Block)PORTAL.get())).toArray(Block[]::new))).m_58966_(null));
    }

    public static final class CampBlock
    extends BaseEntityBlock {
        public final Station station;

        CampBlock(Station station) {
            super(BlockBehaviour.Properties.m_284310_().m_60978_(3.0f).m_60955_().m_60953_(s -> station == Station.LAMP ? 13 : 5));
            this.station = station;
            this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_((Property)BlockStateProperties.f_61374_, (Comparable)Direction.NORTH));
        }

        protected void m_7926_(StateDefinition.Builder<Block, BlockState> b) {
            b.m_61104_(new Property[]{BlockStateProperties.f_61374_});
        }

        public BlockState m_5573_(BlockPlaceContext c) {
            return (BlockState)this.m_49966_().m_61124_((Property)BlockStateProperties.f_61374_, (Comparable)c.m_8125_().m_122424_());
        }

        public BlockState m_6843_(BlockState s, Rotation r) {
            return (BlockState)s.m_61124_((Property)BlockStateProperties.f_61374_, (Comparable)r.m_55954_((Direction)s.m_61143_((Property)BlockStateProperties.f_61374_)));
        }

        public BlockState m_6943_(BlockState s, Mirror m) {
            return this.m_6843_(s, m.m_54846_((Direction)s.m_61143_((Property)BlockStateProperties.f_61374_)));
        }

        public RenderShape m_7514_(BlockState s) {
            return RenderShape.MODEL;
        }

        public VoxelShape m_5940_(BlockState state, BlockGetter world, BlockPos at, CollisionContext context) {
            return WorkshopShapes.shape("odyssey_" + this.station.name().toLowerCase(Locale.ROOT), (Direction)state.m_61143_((Property)BlockStateProperties.f_61374_));
        }

        public void m_6810_(BlockState state, Level world, BlockPos at, BlockState next, boolean moving) {
            Object object;
            if (!state.m_60713_(next.m_60734_()) && !world.f_46443_ && (object = world.m_7702_(at)) instanceof CampEntity) {
                CampEntity be = (CampEntity)((Object)object);
                for (ItemStack stack : be.items) {
                    if (stack.m_41619_()) continue;
                    Containers.m_18992_((Level)world, (double)((double)at.m_123341_() + 0.5), (double)((double)at.m_123342_() + 0.5), (double)((double)at.m_123343_() + 0.5), (ItemStack)stack);
                }
                be.items.replaceAll(v -> ItemStack.f_41583_);
            }
            super.m_6810_(state, world, at, next, moving);
        }

        public BlockEntity m_142194_(BlockPos at, BlockState s) {
            return new CampEntity(at, s);
        }

        public void m_6402_(Level world, BlockPos at, BlockState s, LivingEntity p, ItemStack stack) {
            if (p instanceof ServerPlayer) {
                ServerPlayer user = (ServerPlayer)p;
                BlockEntity blockEntity = world.m_7702_(at);
                if (blockEntity instanceof CampEntity) {
                    CampEntity be = (CampEntity)blockEntity;
                    be.owner = p.m_20148_();
                    be.m_6596_();
                    world.m_7260_(at, s, s, 3);
                    if (OdysseyRealm.camp(user, at)) {
                        GuideService.record(user, "camp_placed");
                    }
                }
            }
        }

        public InteractionResult m_6227_(BlockState state, Level world, BlockPos at, Player p, InteractionHand hand, BlockHitResult hit) {
            if (p instanceof ServerPlayer) {
                ServerPlayer user = (ServerPlayer)p;
                BlockEntity blockEntity = world.m_7702_(at);
                if (blockEntity instanceof CampEntity) {
                    CampEntity be = (CampEntity)blockEntity;
                    if (be.owner == null) {
                        be.owner = user.m_20148_();
                        be.m_6596_();
                    }
                    if (!user.m_20148_().equals(be.owner)) {
                        OdysseyText.tell(user, "not_yours", new Object[0]);
                        return InteractionResult.FAIL;
                    }
                    if (this.station == Station.GATE) {
                        OdysseyRealm.activate(user, at);
                        return InteractionResult.CONSUME;
                    }
                    OdysseyCamp.bind(user, this.station, at);
                    if (this.station == Station.STORE) {
                        OdysseyCamp.openStore(user, at);
                    } else if (this.station == Station.BENCH) {
                        OdysseyCamp.sit(user);
                    } else if (this.station == Station.MUSIC_STAND) {
                        OdysseyCamp.performMusic(user);
                    } else {
                        OdysseyNetwork.open(user, this.station == Station.NOTICE ? 0 : 3);
                    }
                }
            }
            return InteractionResult.m_19078_((boolean)world.f_46443_);
        }
    }

    public static enum Station {
        GATE,
        STORE,
        ARCHITECT,
        GARDEN,
        KITCHEN,
        WORKSHOP,
        VISITORS,
        CLUB,
        BENCH,
        MUSIC_STAND,
        NOTICE,
        LAMP;

    }

    public static class AtlasItem
    extends Item {
        public AtlasItem(Item.Properties properties) {
            super(properties);
        }

        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            consumer.accept(OdysseyItems.extension());
        }
    }

    public static final class CampEntity
    extends BlockEntity {
        public UUID owner;
        public BlockPos core;
        public Direction direction = Direction.EAST;
        public final NonNullList<ItemStack> items = NonNullList.m_122780_((int)27, (Object)ItemStack.f_41583_);

        public CampEntity(BlockPos at, BlockState state) {
            super((BlockEntityType)ENTITY.get(), at, state);
        }

        protected void m_183515_(CompoundTag n) {
            super.m_183515_(n);
            if (this.owner != null) {
                n.m_128362_("Owner", this.owner);
            }
            ContainerHelper.m_18973_((CompoundTag)n, this.items);
            if (this.core != null) {
                n.m_128356_("Core", this.core.m_121878_());
                n.m_128359_("Direction", this.direction.m_122433_());
            }
        }

        public void m_142466_(CompoundTag n) {
            super.m_142466_(n);
            this.owner = n.m_128403_("Owner") ? n.m_128342_("Owner") : null;
            ContainerHelper.m_18980_((CompoundTag)n, this.items);
            this.core = n.m_128441_("Core") ? BlockPos.m_122022_((long)n.m_128454_("Core")) : null;
            Direction dir = Direction.m_122402_((String)n.m_128461_("Direction"));
            this.direction = dir != null && dir.m_122434_().m_122479_() ? dir : Direction.EAST;
        }
    }
}

