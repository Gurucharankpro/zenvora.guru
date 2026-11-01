/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.valueproviders.IntProvider
 *  net.minecraft.util.valueproviders.UniformInt
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties$Builder
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CompassItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BonemealableBlock
 *  net.minecraft.world.level.block.DropExperienceBlock
 *  net.minecraft.world.level.block.FenceBlock
 *  net.minecraft.world.level.block.FenceGateBlock
 *  net.minecraft.world.level.block.GlassBlock
 *  net.minecraft.world.level.block.LanternBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.WoodType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.expanse;

import dev.shadowsovereign.artisan.WorkshopShapes;
import dev.shadowsovereign.discovery.AtlasService;
import dev.shadowsovereign.expanse.ExpanseWorld;
import dev.shadowsovereign.gloom.GloomContent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class ExpanseContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    public static final Map<String, RegistryObject<Block>> blocks = new LinkedHashMap<String, RegistryObject<Block>>();
    public static final Map<String, RegistryObject<Item>> items = new LinkedHashMap<String, RegistryObject<Item>>();
    public static final RegistryObject<Block> LOG = ExpanseContent.block("ashen_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49999_)));
    public static final RegistryObject<Block> PLANKS = ExpanseContent.block("ashen_planks", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50705_)));
    public static final RegistryObject<Block> LEAVES = ExpanseContent.block("ashen_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50050_)));
    public static final RegistryObject<Block> SOIL = ExpanseContent.block("ashen_soil", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50493_)));
    public static final RegistryObject<Block> STONE = ExpanseContent.block("ashen_stone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
    public static final RegistryObject<Block> BRICKS = ExpanseContent.block("ashen_bricks", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50222_)));
    public static final RegistryObject<Block> MOSSY = ExpanseContent.block("mossy_ashen_bricks", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50223_)));
    public static final RegistryObject<Block> CRACKED = ExpanseContent.block("cracked_ashen_bricks", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50224_)));
    public static final RegistryObject<Block> GLASS = ExpanseContent.block("skyglass", () -> new GlassBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50058_).m_60955_()));
    public static final RegistryObject<Block> ORE = ExpanseContent.block("starlight_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_152468_).m_60953_(s -> 3), (IntProvider)UniformInt.m_146622_((int)2, (int)4)));
    public static final RegistryObject<Block> LANTERN = ExpanseContent.block("starlight_lantern", () -> new LanternBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50681_).m_60953_(s -> 15)));
    public static final RegistryObject<Block> FENCE = ExpanseContent.block("ashen_fence", () -> new FenceBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50132_)));
    public static final RegistryObject<Block> GATE = ExpanseContent.block("ashen_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50192_), WoodType.f_61830_));
    public static final RegistryObject<Block> STAIRS = ExpanseContent.block("ashen_stairs", () -> new StairBlock(() -> ((Block)PLANKS.get()).m_49966_(), BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50086_)));
    public static final RegistryObject<Block> SLAB = ExpanseContent.block("ashen_slab", () -> new SlabBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50398_)));
    public static final RegistryObject<Block> GRASS = ExpanseContent.block("silver_grass", () -> new GloomContent.GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50034_)));
    public static final RegistryObject<Block> FLOWER = ExpanseContent.block("moonlit_flower", () -> new GloomContent.GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(s -> 6)));
    public static final RegistryObject<Block> MUSHROOM = ExpanseContent.block("lantern_mushroom", () -> new GloomContent.GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(s -> 9)));
    public static final RegistryObject<Block> SAPLING = ExpanseContent.block("ashen_sapling", AshenSapling::new);
    public static final RegistryObject<Block> GRAVE = ExpanseContent.block("ancient_gravestone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50225_)){

        public VoxelShape m_5940_(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) {
            return WorkshopShapes.shape("ancient_gravestone", Direction.NORTH);
        }
    });
    public static final RegistryObject<Block> TRAVELSTONE = ExpanseContent.block("astral_travelstone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50729_).m_60953_(s -> 9)){

        public VoxelShape m_5940_(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) {
            return WorkshopShapes.shape("astral_travelstone", Direction.NORTH);
        }

        public InteractionResult m_6227_(BlockState s, Level w, BlockPos at, Player p, InteractionHand hand, BlockHitResult hit) {
            if (p instanceof ServerPlayer) {
                ServerPlayer owner = (ServerPlayer)p;
                ExpanseWorld.discover(owner, at);
            }
            return InteractionResult.m_19078_((boolean)w.f_46443_);
        }
    });
    public static final RegistryObject<Item> SHARD = ITEMS.register("starlight_shard", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPASS = ITEMS.register("explorers_compass", () -> new CompassItem(new Item.Properties().m_41487_(1)){

        public InteractionResultHolder<ItemStack> m_7203_(Level w, Player p, InteractionHand hand) {
            if (p instanceof ServerPlayer) {
                ServerPlayer owner = (ServerPlayer)p;
                AtlasService.open(owner);
            }
            return InteractionResultHolder.m_19092_((Object)p.m_21120_(hand), (boolean)w.f_46443_);
        }
    });
    public static final RegistryObject<Item> JAM = ExpanseContent.food("nightberry_jam", 6, 0.7f);
    public static final RegistryObject<Item> ROAST = ExpanseContent.food("roasted_lantern_mushroom", 5, 0.6f);
    public static final RegistryObject<Item> PIE = ExpanseContent.food("astral_pie", 10, 0.8f);

    private static RegistryObject<Block> block(String id, Supplier<Block> factory) {
        RegistryObject b = BLOCKS.register(id, factory);
        blocks.put(id, (RegistryObject<Block>)b);
        items.put(id, (RegistryObject<Item>)ITEMS.register(id, () -> new BlockItem((Block)b.get(), id.equals("lantern_mushroom") ? new Item.Properties().m_41489_(new FoodProperties.Builder().m_38760_(2).m_38758_(0.2f).m_38767_()) : new Item.Properties())));
        return b;
    }

    private static RegistryObject<Item> food(String id, int n, float saturation) {
        RegistryObject i = ITEMS.register(id, () -> new Item(new Item.Properties().m_41489_(new FoodProperties.Builder().m_38760_(n).m_38758_(saturation).m_38767_())));
        items.put(id, (RegistryObject<Item>)i);
        return i;
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    public static final class AshenSapling
    extends GloomContent.GroundPlant
    implements BonemealableBlock {
        public AshenSapling() {
            super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50746_));
        }

        public static boolean grow(ServerLevel w, BlockPos p) {
            int z;
            int x;
            int y;
            for (y = 1; y <= 9; ++y) {
                for (x = -3; x <= 3; ++x) {
                    for (z = -3; z <= 3; ++z) {
                        BlockPos at = p.m_7918_(x, y, z);
                        if (w.m_46805_(at) && (w.m_8055_(at).m_60795_() || w.m_8055_(at).m_247087_())) continue;
                        return false;
                    }
                }
            }
            for (y = 4; y <= 8; ++y) {
                for (x = -3; x <= 3; ++x) {
                    for (z = -3; z <= 3; ++z) {
                        if (Math.abs(x) + Math.abs(z) > 4 || y > 8 - (Math.abs(x) + Math.abs(z)) / 3) continue;
                        w.m_7731_(p.m_7918_(x, y, z), (BlockState)((Block)LEAVES.get()).m_49966_().m_61124_((Property)LeavesBlock.f_54418_, (Comparable)Integer.valueOf(1)), 3);
                    }
                }
            }
            for (y = 0; y < 8; ++y) {
                w.m_7731_(p.m_6630_(y), ((Block)LOG.get()).m_49966_(), 3);
            }
            for (int direction : new int[]{-1, 1}) {
                for (int x2 = 1; x2 <= 2; ++x2) {
                    w.m_7731_(p.m_7918_(x2 * direction, 5 + x2 / 2, 0), (BlockState)((Block)LOG.get()).m_49966_().m_61124_((Property)RotatedPillarBlock.f_55923_, (Comparable)Direction.Axis.X), 3);
                }
            }
            return true;
        }

        public void m_213898_(BlockState s, ServerLevel w, BlockPos p, RandomSource r) {
            if (r.m_188503_(7) == 0 && w.m_46803_(p.m_7494_()) >= 9) {
                AshenSapling.grow(w, p);
            }
        }

        public boolean m_7370_(LevelReader w, BlockPos p, BlockState s, boolean client) {
            return true;
        }

        public boolean m_214167_(Level w, RandomSource r, BlockPos p, BlockState s) {
            return true;
        }

        public void m_214148_(ServerLevel w, RandomSource r, BlockPos p, BlockState s) {
            AshenSapling.grow(w, p);
        }
    }
}

