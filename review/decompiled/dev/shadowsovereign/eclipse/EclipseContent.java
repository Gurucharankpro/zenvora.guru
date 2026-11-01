/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
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
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.eclipse;

import dev.shadowsovereign.adventure.AdventureContent;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.artisan.WorkshopShapes;
import dev.shadowsovereign.eclipse.EclipseCamp;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.eclipse.EclipseText;
import dev.shadowsovereign.eclipse.ScoutingFlare;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.network.EclipseNetwork;
import dev.shadowsovereign.portal.SoulGates;
import dev.shadowsovereign.soullife.SoulLifeText;
import java.util.EnumMap;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class EclipseContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<EntityType<?>> PROJECTILES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENTITY_TYPES, (String)"shadowsovereign");
    public static final RegistryObject<EntityType<ScoutingFlare>> FLARE_ENTITY = PROJECTILES.register("scouting_flare", () -> EntityType.Builder.m_20704_(ScoutingFlare::new, (MobCategory)MobCategory.MISC).m_20699_(0.25f, 0.25f).m_20702_(4).m_20717_(10).m_20712_("scouting_flare"));
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCK_ENTITY_TYPES, (String)"shadowsovereign");
    public static final EnumMap<EclipseState.Station, RegistryObject<CampBlock>> STATIONS = new EnumMap(EclipseState.Station.class);
    public static final EnumMap<EclipseState.Station, RegistryObject<Item>> STATION_ITEMS = new EnumMap(EclipseState.Station.class);
    public static final RegistryObject<Item> FOCUS = ITEMS.register("focus_draught", () -> new Consumable(0));
    public static final RegistryObject<Item> WARD = ITEMS.register("ward_phial", () -> new Consumable(1));
    public static final RegistryObject<Item> FLARE = ITEMS.register("scouting_flare", () -> new Consumable(2));
    public static final RegistryObject<BlockEntityType<CampEntity>> ENTITY;

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        PROJECTILES.register(bus);
        bus.addListener(e -> {
            if (e.getTabKey() == CreativeModeTabs.f_256797_) {
                STATION_ITEMS.values().forEach(arg_0 -> ((BuildCreativeModeTabContentsEvent)e).accept(arg_0));
                e.accept(FOCUS);
                e.accept(WARD);
                e.accept(FLARE);
            }
        });
    }

    static {
        for (EclipseState.Station kind : EclipseState.Station.values()) {
            String id = "eclipse_" + kind.name().toLowerCase(Locale.ROOT);
            RegistryObject block = BLOCKS.register(id, () -> new CampBlock(kind));
            STATIONS.put(kind, (RegistryObject<CampBlock>)block);
            STATION_ITEMS.put(kind, (RegistryObject<Item>)ITEMS.register(id, () -> new BlockItem((Block)block.get(), new Item.Properties())));
        }
        ENTITY = ENTITIES.register("eclipse_station", () -> BlockEntityType.Builder.m_155273_(CampEntity::new, (Block[])((Block[])STATIONS.values().stream().map(RegistryObject::get).toArray(Block[]::new))).m_58966_(null));
    }

    public static final class CampBlock
    extends BaseEntityBlock {
        public final EclipseState.Station kind;

        CampBlock(EclipseState.Station kind) {
            super(BlockBehaviour.Properties.m_284310_().m_60978_(3.0f).m_60955_().m_60953_(s -> kind == EclipseState.Station.WAYGATE ? 10 : 5));
            this.kind = kind;
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

        public RenderShape m_7514_(BlockState state) {
            return RenderShape.MODEL;
        }

        public VoxelShape m_5940_(BlockState state, BlockGetter world, BlockPos at, CollisionContext context) {
            return WorkshopShapes.shape("eclipse_" + this.kind.name().toLowerCase(Locale.ROOT), (Direction)state.m_61143_((Property)BlockStateProperties.f_61374_));
        }

        public BlockEntity m_142194_(BlockPos pos, BlockState state) {
            return new CampEntity(pos, state);
        }

        public void m_6402_(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)entity;
                BlockEntity blockEntity = level.m_7702_(pos);
                if (blockEntity instanceof CampEntity) {
                    CampEntity be = (CampEntity)blockEntity;
                    be.owner = p.m_20148_();
                    be.m_6596_();
                    level.m_7260_(pos, state, state, 3);
                    if (!EclipseCamp.bind(p, this.kind, pos)) {
                        SoulLifeText.tell(p, "station_waiting", EclipseText.c("station." + this.kind.name().toLowerCase(Locale.ROOT), new Object[0]));
                    }
                }
            }
        }

        public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player user, InteractionHand hand, BlockHitResult hit) {
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                BlockEntity blockEntity = level.m_7702_(pos);
                if (blockEntity instanceof CampEntity) {
                    CampEntity be = (CampEntity)blockEntity;
                    if (be.owner == null) {
                        be.owner = p.m_20148_();
                        be.m_6596_();
                    }
                    if (!p.m_20148_().equals(be.owner)) {
                        EclipseText.tell(p, "foreign_station", new Object[0]);
                        return InteractionResult.FAIL;
                    }
                    if (!EclipseCamp.bind(p, this.kind, pos)) {
                        SoulLifeText.tell(p, "station_waiting", EclipseText.c("station." + this.kind.name().toLowerCase(Locale.ROOT), new Object[0]));
                        return InteractionResult.CONSUME;
                    }
                    if (this.kind == EclipseState.Station.WAYGATE && p.m_21120_(hand).m_150930_((Item)AdventureContent.COMPASS.get())) {
                        SoulGates.activate(p, pos);
                        return InteractionResult.CONSUME;
                    }
                    if (this.kind == EclipseState.Station.VAULT) {
                        EclipseCamp.openVault(p);
                    } else if (this.kind == EclipseState.Station.GALLERY) {
                        ShadowRecord r = EclipseService.record(p, EclipseService.state((ServerPlayer)p).selected);
                        if (r == null) {
                            r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().findFirst().orElse(null);
                        }
                        if (r != null) {
                            be.type = r.type;
                            be.label = LegacyService.name(p, r);
                            be.level = r.level();
                            be.kills = r.totalKills;
                            be.m_6596_();
                            level.m_7260_(pos, state, state, 3);
                        }
                        EclipseNetwork.send(p, true, 7);
                    } else {
                        EclipseNetwork.send(p, true, 7);
                    }
                }
            }
            return InteractionResult.m_19078_((boolean)level.f_46443_);
        }

        public void m_6810_(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
            if (!state.m_60713_(next.m_60734_()) && !level.f_46443_) {
                EclipseCamp.remove(level, pos);
            }
            super.m_6810_(state, level, pos, next, moving);
        }
    }

    private static final class Consumable
    extends Item {
        private final int effect;

        Consumable(int effect) {
            super(new Item.Properties().m_41487_(16));
            this.effect = effect;
        }

        public InteractionResultHolder<ItemStack> m_7203_(Level level, Player user, InteractionHand hand) {
            ItemStack stack = user.m_21120_(hand);
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                if (!EclipseCamp.consume(p, this.effect)) {
                    return InteractionResultHolder.m_19100_((Object)stack);
                }
                stack.m_41774_(1);
            }
            return InteractionResultHolder.m_19092_((Object)stack, (boolean)level.f_46443_);
        }
    }

    public static final class CampEntity
    extends BlockEntity {
        public UUID owner;
        public String type = "";
        public String label = "";
        public int level = 1;
        public long kills;

        public CampEntity(BlockPos pos, BlockState state) {
            super((BlockEntityType)ENTITY.get(), pos, state);
        }

        protected void m_183515_(CompoundTag n) {
            super.m_183515_(n);
            if (this.owner != null) {
                n.m_128362_("Owner", this.owner);
            }
            n.m_128359_("Type", this.type);
            n.m_128359_("Label", this.label);
            n.m_128405_("Level", this.level);
            n.m_128356_("Kills", this.kills);
        }

        public void m_142466_(CompoundTag n) {
            super.m_142466_(n);
            this.owner = n.m_128403_("Owner") ? n.m_128342_("Owner") : null;
            this.type = n.m_128461_("Type");
            this.label = n.m_128461_("Label");
            this.level = Math.max(1, Math.min(10, n.m_128451_("Level")));
            this.kills = Math.max(0L, n.m_128454_("Kills"));
        }

        public CompoundTag m_5995_() {
            return this.m_187482_();
        }

        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.m_195640_((BlockEntity)this);
        }
    }
}

