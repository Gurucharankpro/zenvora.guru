/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ContainerLevelAccess
 *  net.minecraft.world.inventory.EnchantmentMenu
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.AmethystClusterBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.GlassBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.SaplingBlock
 *  net.minecraft.world.level.block.grower.AbstractTreeGrower
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.feature.ConfiguredFeature
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fluids.FluidType
 *  net.minecraftforge.fluids.FluidType$Properties
 *  net.minecraftforge.fluids.ForgeFlowingFluid$Flowing
 *  net.minecraftforge.fluids.ForgeFlowingFluid$Properties
 *  net.minecraftforge.fluids.ForgeFlowingFluid$Source
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.ForgeRegistries$Keys
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.monolith;

import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.gloom.GloomContent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class MonolithContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.FLUIDS, (String)"shadowsovereign");
    private static final DeferredRegister<FluidType> TYPES = DeferredRegister.create((ResourceKey)ForgeRegistries.Keys.FLUID_TYPES, (String)"shadowsovereign");
    public static final Map<String, RegistryObject<Block>> blocks = new LinkedHashMap<String, RegistryObject<Block>>();
    public static final Map<String, RegistryObject<Item>> items = new LinkedHashMap<String, RegistryObject<Item>>();
    public static final RegistryObject<FluidType> SILVER_TYPE = TYPES.register("monolith_silver", () -> new FluidType(FluidType.Properties.create().density(1000).viscosity(1000).temperature(300).lightLevel(12).canSwim(true).canDrown(true).canHydrate(true).canExtinguish(true)){

        public void initializeClient(Consumer<IClientFluidTypeExtensions> c) {
            c.accept(new IClientFluidTypeExtensions(){

                public ResourceLocation getStillTexture() {
                    return MonolithContent.id("block/monolith_silver_still");
                }

                public ResourceLocation getFlowingTexture() {
                    return MonolithContent.id("block/monolith_silver_flow");
                }
            });
        }
    });
    public static final RegistryObject<FlowingFluid> SILVER = FLUIDS.register("monolith_silver", () -> new ForgeFlowingFluid.Source(MonolithContent.fluidProperties()));
    public static final RegistryObject<FlowingFluid> SILVER_FLOW = FLUIDS.register("flowing_monolith_silver", () -> new ForgeFlowingFluid.Flowing(MonolithContent.fluidProperties()));

    public static ResourceLocation id(String s) {
        return new ResourceLocation("shadowsovereign", s);
    }

    private static RegistryObject<Block> add(String n, Supplier<Block> f, boolean item) {
        RegistryObject b = BLOCKS.register("monolith_" + n, f);
        blocks.put(n, (RegistryObject<Block>)b);
        if (item) {
            items.put(n, (RegistryObject<Item>)ITEMS.register("monolith_" + n, () -> new BlockItem((Block)b.get(), new Item.Properties())));
        }
        return b;
    }

    private static void stone(String n, Block copy) {
        MonolithContent.add(n, () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)copy)), true);
    }

    public static Block b(String n) {
        return (Block)blocks.get(n).get();
    }

    private static ForgeFlowingFluid.Properties fluidProperties() {
        return new ForgeFlowingFluid.Properties(SILVER_TYPE, SILVER, SILVER_FLOW).block(() -> (LiquidBlock)MonolithContent.b("luminescent_silver_fluid")).bucket(() -> (Item)items.get("silver_bucket").get()).tickRate(5).slopeFindDistance(4).levelDecreasePerBlock(1);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TYPES.register(bus);
        FLUIDS.register(bus);
    }

    static {
        MonolithContent.stone("ashen_crystal_soil", Blocks.f_50493_);
        MonolithContent.stone("bleached_stone", Blocks.f_50069_);
        MonolithContent.stone("monolithic_slate", Blocks.f_152550_);
        MonolithContent.stone("frost_ash_turf", Blocks.f_50493_);
        MonolithContent.stone("polished_white_marble", Blocks.f_50333_);
        for (final String n : List.of("ashen_log", "stripped_ashen_log")) {
            MonolithContent.add(n, () -> new RotatedPillarBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49999_)), true);
        }
        MonolithContent.add("translucent_gray_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50050_).m_60953_(s -> 2)), true);
        MonolithContent.add("weeping_silver_vines", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_152548_).m_60953_(s -> 3)){

            public boolean m_7898_(BlockState s, LevelReader w, BlockPos p) {
                BlockState up = w.m_8055_(p.m_7494_());
                return up.m_60713_((Block)this) || up.m_204336_(BlockTags.f_13035_) || up.m_60783_((BlockGetter)w, p.m_7494_(), Direction.DOWN);
            }

            public BlockState m_7417_(BlockState s, Direction d, BlockState o, LevelAccessor w, BlockPos p, BlockPos q) {
                return d == Direction.UP && !s.m_60710_((LevelReader)w, p) ? Blocks.f_50016_.m_49966_() : super.m_7417_(s, d, o, w, p, q);
            }
        }, true);
        MonolithContent.stone("rooted_ash_cluster", Blocks.f_50011_);
        for (final String n : List.of("pale_spores", "ghost_grass", "crystal_fern")) {
            MonolithContent.add(n, () -> new GloomContent.GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50035_).m_60953_(s -> n.equals("crystal_fern") ? 4 : 0)){

                public void m_7892_(BlockState s, Level w, BlockPos p, Entity e) {
                    if (n.equals("pale_spores") && w.f_46443_ && w.f_46441_.m_188503_(20) == 0 && e.m_20184_().m_165925_() > 0.001) {
                        w.m_7106_((ParticleOptions)ParticleTypes.f_123810_, (double)p.m_123341_() + 0.5, (double)p.m_123342_() + 0.2, (double)p.m_123343_() + 0.5, 0.0, 0.015, 0.0);
                    }
                }
            }, true);
        }
        MonolithContent.add("ashen_bramble", () -> new GloomContent.GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50036_)){

            public void m_7892_(BlockState s, Level w, BlockPos p, Entity e) {
                if (e instanceof LivingEntity) {
                    LivingEntity l = (LivingEntity)e;
                    if (!ShadowService.isShadow(e)) {
                        e.m_7601_(s, new Vec3(0.8, 0.8, 0.8));
                        if (!w.f_46443_ && e.m_20184_().m_165925_() > 0.001) {
                            l.m_6469_(w.m_269111_().m_269555_(), 1.0f);
                        }
                    }
                }
            }
        }, true);
        MonolithContent.add("gloom_sprout", () -> new NightSprout(), true);
        MonolithContent.add("luminescent_silver_fluid", () -> new LiquidBlock(SILVER, BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49990_).m_60953_(s -> 12)), false);
        MonolithContent.add("glimmering_monolith_shard", () -> new AmethystClusterBlock(7, 3, BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_152492_).m_60953_(s -> 12)), true);
        MonolithContent.stone("engraved_slate_tile", Blocks.f_50222_);
        MonolithContent.add("chiseled_marble_pillar", () -> new RotatedPillarBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50283_)){

            public VoxelShape m_5940_(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) {
                return switch ((Direction.Axis)s.m_61143_((Property)f_55923_)) {
                    default -> throw new IncompatibleClassChangeError();
                    case Direction.Axis.Y -> Block.m_49796_((double)3.0, (double)0.0, (double)3.0, (double)13.0, (double)16.0, (double)13.0);
                    case Direction.Axis.X -> Block.m_49796_((double)0.0, (double)3.0, (double)3.0, (double)16.0, (double)13.0, (double)13.0);
                    case Direction.Axis.Z -> Block.m_49796_((double)3.0, (double)3.0, (double)0.0, (double)13.0, (double)13.0, (double)16.0);
                };
            }
        }, true);
        MonolithContent.add("ashen_veil_glass", () -> new GlassBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50058_).m_60955_()), true);
        MonolithContent.stone("ashen_planks", Blocks.f_50705_);
        MonolithContent.add("ashen_sapling", () -> new SaplingBlock(new AbstractTreeGrower(){

            protected ResourceKey<ConfiguredFeature<?, ?>> m_213888_(RandomSource r, boolean f) {
                return ResourceKey.m_135785_((ResourceKey)Registries.f_256911_, (ResourceLocation)MonolithContent.id("monolith_tree"));
            }
        }, BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50746_)), true);
        MonolithContent.add("enchanting_table", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50201_).m_60953_(s -> 4)){

            public VoxelShape m_5940_(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) {
                return Block.m_49796_((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)12.0, (double)16.0);
            }

            public InteractionResult m_6227_(BlockState s, final Level w, final BlockPos at, Player p, InteractionHand h, BlockHitResult hit) {
                if (!w.f_46443_) {
                    p.m_5893_((MenuProvider)new SimpleMenuProvider((menu, inv, user) -> new EnchantmentMenu(menu, inv, ContainerLevelAccess.m_39289_((Level)w, (BlockPos)at)){

                        public boolean m_6875_(Player u) {
                            return w.m_8055_(at).m_60713_(MonolithContent.b("enchanting_table")) && u.m_20275_((double)at.m_123341_() + 0.5, (double)at.m_123342_() + 0.5, (double)at.m_123343_() + 0.5) <= 64.0;
                        }
                    }, (Component)Component.m_237115_((String)"block.shadowsovereign.monolith_enchanting_table")));
                }
                return InteractionResult.m_19078_((boolean)w.f_46443_);
            }
        }, true);
        items.put("silver_bucket", (RegistryObject<Item>)ITEMS.register("monolith_silver_bucket", () -> new BucketItem(SILVER, new Item.Properties().m_41495_(Items.f_42446_).m_41487_(1))));
    }

    public static final class NightSprout
    extends GloomContent.GroundPlant {
        public static final BooleanProperty OPEN = BooleanProperty.m_61465_((String)"open");

        public NightSprout() {
            super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60977_().m_60953_(s -> (Boolean)s.m_61143_((Property)OPEN) != false ? 6 : 0));
            this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_((Property)OPEN, (Comparable)Boolean.valueOf(false)));
        }

        protected void m_7926_(StateDefinition.Builder<Block, BlockState> b) {
            b.m_61104_(new Property[]{OPEN});
        }

        public BlockState m_5573_(BlockPlaceContext c) {
            return (BlockState)this.m_49966_().m_61124_((Property)OPEN, (Comparable)Boolean.valueOf(NightSprout.night(c.m_43725_())));
        }

        private static boolean night(Level w) {
            long time = w.m_6042_().f_63854_().orElse(w.m_46468_()) % 24000L;
            return time >= 13000L && time < 23000L;
        }

        public void m_213898_(BlockState s, ServerLevel w, BlockPos p, RandomSource r) {
            boolean open = NightSprout.night((Level)w);
            if ((Boolean)s.m_61143_((Property)OPEN) != open) {
                w.m_7731_(p, (BlockState)s.m_61124_((Property)OPEN, (Comparable)Boolean.valueOf(open)), 3);
            }
        }
    }
}

