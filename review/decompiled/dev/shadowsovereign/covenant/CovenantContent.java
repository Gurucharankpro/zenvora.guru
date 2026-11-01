/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.valueproviders.IntProvider
 *  net.minecraft.util.valueproviders.UniformInt
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.SpawnPlacements$Type
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties$Builder
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemNameBlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CarpetBlock
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.DropExperienceBlock
 *  net.minecraft.world.level.block.FarmBlock
 *  net.minecraft.world.level.block.FenceBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.SaplingBlock
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.SweetBerryBushBlock
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.grower.AbstractTreeGrower
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockSetType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.feature.ConfiguredFeature
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.entity.EntityAttributeCreationEvent
 *  net.minecraftforge.event.entity.SpawnPlacementRegisterEvent
 *  net.minecraftforge.event.entity.SpawnPlacementRegisterEvent$Operation
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.covenant;

import dev.shadowsovereign.PixelSpawnEgg;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.covenant.CovenantFauna;
import dev.shadowsovereign.covenant.CovenantWorld;
import dev.shadowsovereign.gloom.GloomContent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class CovenantContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENTITY_TYPES, (String)"shadowsovereign");
    public static final Map<String, RegistryObject<Block>> blocks = new LinkedHashMap<String, RegistryObject<Block>>();
    public static final Map<String, RegistryObject<Item>> items = new LinkedHashMap<String, RegistryObject<Item>>();
    public static final List<RegistryObject<EntityType<CovenantFauna>>> FAUNA;
    public static final String[] ANIMALS;

    private static RegistryObject<Block> block(String name, Supplier<Block> factory, boolean item) {
        String id = "bound_" + name;
        RegistryObject b = BLOCKS.register(id, factory);
        blocks.put(name, (RegistryObject<Block>)b);
        if (item) {
            items.put(name, (RegistryObject<Item>)ITEMS.register(id, () -> new BlockItem((Block)b.get(), new Item.Properties())));
        }
        return b;
    }

    private static RegistryObject<Block> simple(String name, Block copy) {
        return CovenantContent.block(name, () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)copy)), true);
    }

    private static BlockBehaviour.Properties plant() {
        return BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50035_).m_60910_();
    }

    public static Block b(String n) {
        return (Block)blocks.get(n).get();
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(CovenantContent::attributes);
        bus.addListener(CovenantContent::placements);
    }

    private static void attributes(EntityAttributeCreationEvent e) {
        for (int i = 0; i < 3; ++i) {
            e.put((EntityType)FAUNA.get(i).get(), CovenantFauna.attributes(i).m_22265_());
        }
    }

    private static void placements(SpawnPlacementRegisterEvent e) {
        for (int k = 0; k < 3; ++k) {
            e.register((EntityType)FAUNA.get(k).get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (t, w, reason, p, r) -> w.m_8055_(p.m_7495_()).m_204336_(BlockTags.f_144274_), SpawnPlacementRegisterEvent.Operation.REPLACE);
        }
    }

    static {
        CovenantContent.simple("soul_soil", Blocks.f_50136_);
        CovenantContent.simple("dark_soul_grass_block", Blocks.f_50493_);
        for (String n : List.of("ancient_soul_stone", "cracked_soul_stone", "polished_soul_stone", "soul_stone_bricks", "mossy_soul_bricks", "soul_sandstone")) {
            CovenantContent.simple(n, Blocks.f_50222_);
        }
        for (String n : List.of("violet_crystal_ore", "cyan_crystal_ore")) {
            CovenantContent.block(n, () -> new DropExperienceBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49996_).m_60953_(s -> 5), (IntProvider)UniformInt.m_146622_((int)1, (int)4)), true);
        }
        for (String n : List.of("soul_wood_log", "stripped_soul_wood_log")) {
            CovenantContent.block(n, () -> new RotatedPillarBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49999_)), true);
        }
        CovenantContent.simple("soul_wood_planks", Blocks.f_50705_);
        CovenantContent.block("soul_wood_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50050_).m_60953_(s -> 7)), true);
        CovenantContent.block("mourning_willow_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50050_).m_60953_(s -> 3)), true);
        CovenantContent.block("soul_wood_stairs", () -> new StairBlock(() -> CovenantContent.b("soul_wood_planks").m_49966_(), BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50086_)), true);
        CovenantContent.block("soul_wood_slab", () -> new SlabBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50398_)), true);
        CovenantContent.block("soul_wood_fence", () -> new FenceBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50132_)), true);
        CovenantContent.block("soul_wood_door", () -> new DoorBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50154_), BlockSetType.f_271198_){}, true);
        CovenantContent.block("soul_wood_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50216_), BlockSetType.f_271198_){}, true);
        CovenantContent.block("glowing_moss", () -> new CarpetBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_152543_).m_60953_(s -> 6)), true);
        CovenantContent.block("soul_lantern_block", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50386_).m_60953_(s -> 15)), true);
        CovenantContent.block("ancient_rune_block", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50222_).m_60953_(s -> 8)), true);
        for (String n : List.of("spirit_mushroom", "soul_fern", "ghost_flower", "whispering_mushroom")) {
            CovenantContent.block(n, () -> new GloomContent.GroundPlant(CovenantContent.plant().m_60953_(s -> n.equals("soul_fern") ? 0 : 6)), true);
        }
        CovenantContent.block("violet_thorn_bush", () -> new GloomContent.GroundPlant(CovenantContent.plant()){

            public void m_7892_(BlockState s, Level w, BlockPos p, Entity e) {
                if (e instanceof LivingEntity) {
                    LivingEntity l = (LivingEntity)e;
                    if (!(e instanceof CovenantFauna) && !ShadowService.isShadow(e)) {
                        e.m_7601_(s, new Vec3(0.8, 0.75, 0.8));
                        if (!w.f_46443_ && (Math.abs(e.m_20185_() - e.f_19790_) > 0.003 || Math.abs(e.m_20189_() - e.f_19792_) > 0.003)) {
                            l.m_6469_(w.m_269111_().m_269555_(), 1.0f);
                        }
                    }
                }
            }
        }, true);
        CovenantContent.block("mourning_vine", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_152548_).m_60953_(s -> 2)){

            public boolean m_7898_(BlockState s, LevelReader w, BlockPos p) {
                BlockState up = w.m_8055_(p.m_7494_());
                return up.m_60713_((Block)this) || up.m_204336_(BlockTags.f_13035_) || up.m_60783_((BlockGetter)w, p.m_7494_(), Direction.DOWN);
            }

            public BlockState m_7417_(BlockState s, Direction d, BlockState other, LevelAccessor w, BlockPos p, BlockPos next) {
                return d == Direction.UP && !s.m_60710_((LevelReader)w, p) ? Blocks.f_50016_.m_49966_() : super.m_7417_(s, d, other, w, p, next);
            }
        }, true);
        for (final String tree : List.of("whispering_soul", "mourning_willow")) {
            CovenantContent.block(tree + "_sapling", () -> new SaplingBlock(new AbstractTreeGrower(){

                protected ResourceKey<ConfiguredFeature<?, ?>> m_213888_(RandomSource r, boolean flowers) {
                    return ResourceKey.m_135785_((ResourceKey)Registries.f_256911_, (ResourceLocation)CovenantWorld.id("bound_" + (tree.equals("whispering_soul") ? "whispering_soul_tree" : "mourning_willow")));
                }
            }, BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50746_)), true);
        }
        CovenantContent.block("soul_wheat_crop", () -> new CropBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50092_)){

            protected ItemLike m_6404_() {
                return (ItemLike)items.get("soul_wheat_seeds").get();
            }

            protected boolean m_6266_(BlockState s, BlockGetter w, BlockPos p) {
                return s.m_60734_() instanceof FarmBlock || s.m_204336_(BlockTags.f_144274_);
            }
        }, false);
        CovenantContent.block("crystal_berry_bush", CrystalBerries::new, false);
        for (String n : List.of("soul_wheat", "violet_crystal_shard", "cyan_crystal_shard")) {
            items.put(n, (RegistryObject<Item>)ITEMS.register("bound_" + n, () -> new Item(new Item.Properties())));
        }
        items.put("soul_wheat_seeds", (RegistryObject<Item>)ITEMS.register("bound_soul_wheat_seeds", () -> new ItemNameBlockItem(CovenantContent.b("soul_wheat_crop"), new Item.Properties())));
        items.put("crystal_berries", (RegistryObject<Item>)ITEMS.register("bound_crystal_berries", () -> new ItemNameBlockItem(CovenantContent.b("crystal_berry_bush"), new Item.Properties().m_41489_(new FoodProperties.Builder().m_38760_(3).m_38758_(0.3f).m_38767_()))));
        FAUNA = new ArrayList<RegistryObject<EntityType<CovenantFauna>>>();
        ANIMALS = new String[]{"soul_hare", "gloom_moth", "crystal_tortoise"};
        for (int k = 0; k < 3; ++k) {
            int kind = k;
            String id = "bound_" + ANIMALS[k];
            RegistryObject t = ENTITIES.register(id, () -> EntityType.Builder.m_20704_((type, w) -> new CovenantFauna((EntityType<? extends CovenantFauna>)type, w, kind), (MobCategory)MobCategory.CREATURE).m_20699_(kind == 0 ? 0.5f : (kind == 1 ? 0.4f : 0.9f), kind == 0 ? 0.7f : (kind == 1 ? 0.4f : 0.65f)).m_20702_(8).m_20712_("shadowsovereign:" + id));
            FAUNA.add((RegistryObject<EntityType<CovenantFauna>>)t);
            items.put(ANIMALS[k] + "_spawn_egg", (RegistryObject<Item>)ITEMS.register(id + "_spawn_egg", () -> new PixelSpawnEgg((Supplier<? extends EntityType<? extends Mob>>)t, 2695998, 6872047, new Item.Properties())));
        }
    }

    public static final class CrystalBerries
    extends SweetBerryBushBlock {
        public CrystalBerries() {
            super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50685_).m_60953_(s -> (Integer)s.m_61143_((Property)f_57244_) == 3 ? 6 : 0));
        }

        public void m_7892_(BlockState s, Level w, BlockPos p, Entity e) {
        }

        public InteractionResult m_6227_(BlockState s, Level w, BlockPos p, Player player, InteractionHand hand, BlockHitResult hit) {
            int age = (Integer)s.m_61143_((Property)f_57244_);
            if (age > 1) {
                if (!w.f_46443_) {
                    CrystalBerries.m_49840_((Level)w, (BlockPos)p, (ItemStack)new ItemStack((ItemLike)items.get("crystal_berries").get(), age == 3 ? 3 : 2));
                    w.m_7731_(p, (BlockState)s.m_61124_((Property)f_57244_, (Comparable)Integer.valueOf(1)), 2);
                    w.m_5594_(null, p, SoundEvents.f_12457_, SoundSource.BLOCKS, 1.0f, 0.9f);
                }
                return InteractionResult.m_19078_((boolean)w.f_46443_);
            }
            return player.m_21120_(hand).m_150930_(Items.f_42499_) ? InteractionResult.PASS : InteractionResult.CONSUME;
        }

        protected boolean m_6266_(BlockState s, BlockGetter w, BlockPos p) {
            return s.m_204336_(BlockTags.f_144274_) || s.m_60734_() instanceof FarmBlock;
        }

        public ItemStack m_7397_(BlockGetter w, BlockPos p, BlockState s) {
            return new ItemStack((ItemLike)items.get("crystal_berries").get());
        }
    }
}

