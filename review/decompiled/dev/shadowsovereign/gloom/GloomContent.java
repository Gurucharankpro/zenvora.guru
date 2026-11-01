/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties$Builder
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.BowlFoodItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemNameBlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BonemealableBlock
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.FarmBlock
 *  net.minecraft.world.level.block.FenceBlock
 *  net.minecraft.world.level.block.FenceGateBlock
 *  net.minecraft.world.level.block.GlassBlock
 *  net.minecraft.world.level.block.LanternBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.SugarCaneBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.WoodType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.common.IPlantable
 *  net.minecraftforge.common.PlantType
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.gloom;

import dev.shadowsovereign.expansion.AquaticPlant;
import dev.shadowsovereign.expansion.DecorationBlock;
import dev.shadowsovereign.expansion.HangingRoots;
import dev.shadowsovereign.expansion.TidewoodSapling;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class GloomContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    public static final Map<String, RegistryObject<Block>> blocks = new LinkedHashMap<String, RegistryObject<Block>>();
    public static final Map<String, RegistryObject<Item>> items = new LinkedHashMap<String, RegistryObject<Item>>();
    public static final RegistryObject<Block> DIRT = GloomContent.block("dark_dirt", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50493_)));
    public static final RegistryObject<Block> GRASS = GloomContent.block("dark_grass", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50493_).m_60918_(SoundType.f_56740_)));
    public static final RegistryObject<Block> STONE = GloomContent.block("dark_stone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
    public static final RegistryObject<Block> COBBLE = GloomContent.block("dark_cobblestone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50652_)));
    public static final RegistryObject<Block> LOG = GloomContent.block("dark_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49999_)));
    public static final RegistryObject<Block> PLANKS = GloomContent.block("dark_planks", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50705_)));
    public static final RegistryObject<Block> LEAVES = GloomContent.block("dark_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50050_)));
    public static final RegistryObject<Block> TUFT = GloomContent.block("dark_tall_grass", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50034_)));
    public static final RegistryObject<Block> FLOWER = GloomContent.block("glow_flower", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(s -> 7)));
    public static final RegistryObject<Block> GLOW_MUSHROOM = GloomContent.block("glow_mushroom_block", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50701_).m_60953_(s -> 12)));
    public static final RegistryObject<Block> FARMLAND = GloomContent.block("dark_farmland", DarkFarmland::new);
    public static final RegistryObject<Block> FENCE = GloomContent.block("dusk_fence", () -> new FenceBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50132_)));
    public static final RegistryObject<Block> GATE = GloomContent.block("dusk_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50192_), WoodType.f_61830_));
    public static final RegistryObject<Block> STAIRS = GloomContent.block("dusk_stairs", () -> new StairBlock(() -> ((Block)PLANKS.get()).m_49966_(), BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50086_)));
    public static final RegistryObject<Block> SLAB = GloomContent.block("dusk_slab", () -> new SlabBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50398_)));
    public static final RegistryObject<Block> LANTERN = GloomContent.block("dusk_lantern", () -> new LanternBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50681_).m_60953_(s -> 15)));
    public static final RegistryObject<Block> PUMPKIN = GloomContent.block("ghost_pumpkin", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50133_)));
    public static final RegistryObject<Block> SAPLING = GloomContent.block("dusk_sapling", DuskSapling::new);
    public static final RegistryObject<Block> MUSHROOM = GloomContent.block("moon_mushroom", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(s -> 3)));
    public static final RegistryObject<Block> REED = GloomContent.block("echo_reed", EchoReed::new);
    public static final RegistryObject<Block> BERRIES = GloomContent.plant("night_berry_bush", NightBerries::new);
    public static final RegistryObject<Item> WHEAT_SEEDS;
    public static final RegistryObject<Item> WHEAT;
    public static final RegistryObject<Item> CARROT;
    public static final RegistryObject<Item> POTATO;
    public static final RegistryObject<Item> PEPPER_SEEDS;
    public static final RegistryObject<Item> PEPPER;
    public static final RegistryObject<Item> HERB_SEEDS;
    public static final RegistryObject<Item> HERB;
    public static final RegistryObject<Item> PUMPKIN_SEEDS;
    public static final RegistryObject<Item> BERRY;
    public static final RegistryObject<Item> BREAD;
    public static final RegistryObject<Item> BAKED_POTATO;
    public static final RegistryObject<Item> ROASTED_PEPPER;
    public static final RegistryObject<Item> STEW;
    public static final RegistryObject<Item> TEA;

    private static RegistryObject<Block> block(String id, Supplier<Block> factory) {
        RegistryObject b = BLOCKS.register(id, factory);
        blocks.put(id, (RegistryObject<Block>)b);
        items.put(id, (RegistryObject<Item>)ITEMS.register(id, () -> new BlockItem((Block)b.get(), id.equals("moon_mushroom") ? new Item.Properties().m_41489_(new FoodProperties.Builder().m_38760_(2).m_38758_(0.3f).m_38767_()) : new Item.Properties())));
        return b;
    }

    private static RegistryObject<Item> food(String id, int hunger, float saturation) {
        RegistryObject i = ITEMS.register(id, () -> new Item(hunger > 0 ? new Item.Properties().m_41489_(new FoodProperties.Builder().m_38760_(hunger).m_38758_(saturation).m_38767_()) : new Item.Properties()));
        items.put(id, (RegistryObject<Item>)i);
        return i;
    }

    private static RegistryObject<Item> cropFood(String id, String crop, int hunger, float sat) {
        RegistryObject i = ITEMS.register(id, () -> new ItemNameBlockItem((Block)blocks.get(crop).get(), new Item.Properties().m_41489_(new FoodProperties.Builder().m_38760_(hunger).m_38758_(sat).m_38767_())));
        items.put(id, (RegistryObject<Item>)i);
        return i;
    }

    private static RegistryObject<Block> plant(String id, Supplier<Block> factory) {
        RegistryObject b = BLOCKS.register(id, factory);
        blocks.put(id, (RegistryObject<Block>)b);
        return b;
    }

    private static RegistryObject<Item> seed(String id, String crop) {
        RegistryObject i = ITEMS.register(id, () -> new ItemNameBlockItem((Block)blocks.get(crop).get(), new Item.Properties()));
        items.put(id, (RegistryObject<Item>)i);
        return i;
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    public static Item item(String id) {
        return (Item)items.get(id).get();
    }

    static {
        for (String id : List.of("soul_wheat", "dusk_carrot", "moon_potato", "shadow_pepper", "soul_herb", "ghost_pumpkin_stem")) {
            GloomContent.plant(id + "_crop", () -> new Crop(id));
        }
        WHEAT_SEEDS = GloomContent.seed("soul_wheat_seeds", "soul_wheat_crop");
        WHEAT = GloomContent.food("soul_wheat", 0, 0.0f);
        CARROT = GloomContent.cropFood("dusk_carrot", "dusk_carrot_crop", 3, 0.6f);
        POTATO = GloomContent.cropFood("moon_potato", "moon_potato_crop", 1, 0.3f);
        PEPPER_SEEDS = GloomContent.seed("shadow_pepper_seeds", "shadow_pepper_crop");
        PEPPER = GloomContent.food("shadow_pepper", 2, 0.3f);
        HERB_SEEDS = GloomContent.seed("soul_herb_seeds", "soul_herb_crop");
        HERB = GloomContent.food("soul_herb", 0, 0.0f);
        PUMPKIN_SEEDS = GloomContent.seed("ghost_pumpkin_seeds", "ghost_pumpkin_stem_crop");
        BERRY = GloomContent.cropFood("night_berries", "night_berry_bush", 2, 0.3f);
        BREAD = GloomContent.food("soul_bread", 6, 0.6f);
        BAKED_POTATO = GloomContent.food("baked_moon_potato", 6, 0.6f);
        ROASTED_PEPPER = GloomContent.food("roasted_shadow_pepper", 5, 0.5f);
        STEW = ITEMS.register("dark_forest_stew", () -> new BowlFoodItem(new Item.Properties().m_41487_(1).m_41489_(new FoodProperties.Builder().m_38760_(8).m_38758_(0.7f).m_38767_())));
        TEA = ITEMS.register("soul_tea", () -> new Item(new Item.Properties().m_41487_(16).m_41489_(new FoodProperties.Builder().m_38760_(1).m_38758_(0.1f).m_38765_().effect(() -> new MobEffectInstance(MobEffects.f_19605_, 200, 0), 1.0f).m_38767_())){

            public UseAnim m_6164_(ItemStack s) {
                return UseAnim.DRINK;
            }

            public ItemStack m_5922_(ItemStack stack, Level level, LivingEntity entity) {
                ItemStack result = super.m_5922_(stack, level, entity);
                if (entity instanceof Player) {
                    Player p = (Player)entity;
                    if (!p.m_150110_().f_35937_) {
                        if (result.m_41619_()) {
                            return new ItemStack((ItemLike)Items.f_42590_);
                        }
                        if (!p.m_150109_().m_36054_(new ItemStack((ItemLike)Items.f_42590_))) {
                            p.m_36176_(new ItemStack((ItemLike)Items.f_42590_), false);
                        }
                    }
                }
                return result;
            }
        });
        GloomContent.block("abyss_abyssal_stone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
        GloomContent.block("abyss_cracked_abyssal_stone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
        GloomContent.block("abyss_polished_abyssal_stone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
        GloomContent.block("abyss_abyss_moss", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_).m_60953_(blockState -> 7)));
        GloomContent.block("abyss_abyss_soil", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
        GloomContent.block("abyss_emerald_soul_ore", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_).m_60953_(blockState -> 5)));
        GloomContent.block("abyss_emerald_soul_crystal", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_).m_60953_(blockState -> 15)));
        GloomContent.block("abyss_gloomcap_stem", () -> new RotatedPillarBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49999_)));
        GloomContent.block("abyss_gloomcap_cap", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_).m_60953_(blockState -> 9)));
        GloomContent.block("abyss_gloomcap_spore", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 6)));
        GloomContent.block("abyss_voidshroom_stem", () -> new RotatedPillarBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49999_)));
        GloomContent.block("abyss_voidshroom_cap", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_).m_60953_(blockState -> 4)));
        GloomContent.block("abyss_voidshroom_spore", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 4)));
        GloomContent.block("abyss_abyss_grass", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 3)));
        GloomContent.block("abyss_hanging_soul_roots", () -> new HangingRoots(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 5)));
        GloomContent.block("abyss_emerald_lantern_flower", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 10)));
        GloomContent.block("abyss_void_fern", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_)));
        GloomContent.block("abyss_crystal_thorn", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 8)));
        GloomContent.block("abyss_abyss_reed", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_)));
        GloomContent.block("abyss_soul_glow_mushroom", () -> new GroundPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 10)));
        GloomContent.block("sunken_sunken_stone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
        GloomContent.block("sunken_cracked_sunken_stone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
        GloomContent.block("sunken_polished_sunken_stone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
        GloomContent.block("sunken_abyssal_sand", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_)));
        GloomContent.block("sunken_pearlstone", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_).m_60953_(blockState -> 12)));
        GloomContent.block("sunken_tide_crystal_ore", () -> new Block(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_).m_60953_(blockState -> 7)));
        GloomContent.block("sunken_tide_crystal_block", () -> new GlassBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50069_).m_60953_(blockState -> 15)));
        GloomContent.block("sunken_moon_coral", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 10)));
        GloomContent.block("sunken_crimson_fan_coral", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 4)));
        GloomContent.block("sunken_ghost_coral", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 12)));
        GloomContent.block("sunken_spirit_kelp", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 6)));
        GloomContent.block("sunken_deepwater_grass", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_)));
        GloomContent.block("sunken_luminous_anemone", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 13)));
        GloomContent.block("sunken_pearl_flower", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 8)));
        GloomContent.block("sunken_abyssal_reeds", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_)));
        GloomContent.block("sunken_floating_spore_plant", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 10)));
        GloomContent.block("sunken_tidewood_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_49999_)));
        GloomContent.block("sunken_tidewood_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50050_)));
        GloomContent.block("sunken_tidewood_sapling", () -> new TidewoodSapling());
        GloomContent.block("sunken_ancient_shell_cluster", () -> new AquaticPlant(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60953_(blockState -> 5)));
        GloomContent.block("decor_soulfire_brazier", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 13)));
        GloomContent.block("decor_chained_spirit_pillar", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 5)));
        GloomContent.block("decor_summoning_circle_relic", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 6)));
        GloomContent.block("decor_broken_beast_idol", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("decor_skull_spike_totem", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("decor_hanging_soul_lantern", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 12)));
        GloomContent.block("decor_cursed_war_banner", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 2)));
        GloomContent.block("decor_bone_heap", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f)));
        GloomContent.block("decor_soul_crystal_cluster", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 9)));
        GloomContent.block("decor_ritual_offering_table", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 6)));
        GloomContent.block("decor_cracked_obsidian_obelisk", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 7)));
        GloomContent.block("decor_rusted_spirit_cage", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 4)));
        GloomContent.block("decor_ancient_drum_ruin", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 2)));
        GloomContent.block("decor_moonstone_grave_marker", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 4)));
        GloomContent.block("decor_chained_arch_fragment", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("decor_spectral_torch_stand", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 12)));
        GloomContent.block("decor_bone_fence_segment", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f)));
        GloomContent.block("decor_soul_vessel_jar", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 6)));
        GloomContent.block("decor_ruined_stone_throne", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("decor_floating_rune_ornament", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 9)));
        GloomContent.block("reaper_four_armed_reaper_statue", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 4)));
        GloomContent.block("reaper_reaper_throne", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 5)));
        GloomContent.block("reaper_soul_harvest_altar", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("reaper_giant_scythe_monument", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 6)));
        GloomContent.block("reaper_hollow_spirit_lantern", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 13)));
        GloomContent.block("reaper_four_handed_candelabrum", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 13)));
        GloomContent.block("reaper_reapers_coffin", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 4)));
        GloomContent.block("reaper_chained_soul_obelisk", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 8)));
        GloomContent.block("reaper_cursed_hourglass", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("reaper_spectral_grave_cross", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 5)));
        GloomContent.block("reaper_soul_collector_urn", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 9)));
        GloomContent.block("reaper_hanging_scythe_chandelier", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 14)));
        GloomContent.block("reaper_reaper_wall_banner", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f)));
        GloomContent.block("reaper_ancient_bone_arch", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("reaper_forgotten_funeral_bell", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 4)));
        GloomContent.block("reaper_soul_mirror", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 7)));
        GloomContent.block("reaper_reapers_book_pedestal", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 6)));
        GloomContent.block("reaper_eternal_mourning_fountain", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 9)));
        GloomContent.block("reaper_floating_soul_reliquary", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 12)));
        GloomContent.block("reaper_last_harvest_clock", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 8)));
        GloomContent.block("devourer_soul_devourer_monument", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 6)));
        GloomContent.block("devourer_grand_soul_prison_cage", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 9)));
        GloomContent.block("devourer_devourers_throne", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 5)));
        GloomContent.block("devourer_six_legged_guardian_totem", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 5)));
        GloomContent.block("devourer_abyssal_soul_altar", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("devourer_caged_soul_lantern", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 13)));
        GloomContent.block("devourer_devourers_jaw_arch", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 5)));
        GloomContent.block("devourer_soul_extraction_obelisk", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 8)));
        GloomContent.block("devourer_ancient_beast_skull_relic", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 4)));
        GloomContent.block("devourer_six_claw_chandelier", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 14)));
        GloomContent.block("devourer_imprisoned_star_crystal", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 12)));
        GloomContent.block("devourer_devourers_war_banner", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f)));
        GloomContent.block("devourer_soul_cage_sarcophagus", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 5)));
        GloomContent.block("devourer_abyssal_spine_pillar", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 6)));
        GloomContent.block("devourer_forgotten_feeding_basin", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 4)));
        GloomContent.block("devourer_soulbound_chain_rack", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 2)));
        GloomContent.block("devourer_devourers_ritual_brazier", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 14)));
        GloomContent.block("devourer_cursed_beast_footprint", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f)));
        GloomContent.block("devourer_eternal_prison_gate", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_heart_of_the_devourer", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 12)));
        GloomContent.block("butcher_cursed_butcher_statue", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_abandoned_butchers_counter", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_gravekeepers_soul_lantern", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("butcher_haunted_gravekeeper_monument", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_cursed_scarecrow", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_haunted_harvest_wagon", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_plague_doctor_display_statue", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_forbidden_alchemy_cabinet", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_executioners_axe_monument", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_executioners_iron_throne", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_haunted_marionette_display", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_clockwork_puppet_workbench", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_grand_cursed_bell", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_soul_resonance_pillar", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_supreme_butcher_war_statue", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_crimson_overlord_throne", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_black_stag_mask_monument", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_haunted_hunters_weapon_rack", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_gentlemans_tea_table", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("butcher_secret_overlords_portrait", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_abyssal_soul_altar", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_abyssal_spine_pillar", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_ancient_beast_skull_relic", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_caged_soul_lantern", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("devourer_v2_cursed_beast_footprint", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_devourers_jaw_arch", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_devourers_ritual_brazier", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("devourer_v2_devourers_throne", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_devourers_war_banner", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_eternal_prison_gate", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_forgotten_feeding_basin", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_grand_soul_prison_cage", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_heart_of_the_devourer", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("devourer_v2_imprisoned_star_crystal", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("devourer_v2_six_claw_chandelier", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_six_legged_guardian_totem", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_soul_cage_sarcophagus", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_soul_devourer_monument", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_soul_extraction_obelisk", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("devourer_v2_soulbound_chain_rack", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_crimson_raven_nest", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_fallen_blood_moon_statue", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_akatsuki_shadow_throne", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_forgotten_ninja_weapon_display", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_moonlit_fairy_fountain", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_sleeping_moon_fox_statue", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_moonveil_crystal_bed", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("relic_forgotten_lunar_altar", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_fallen_sky_anchor", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_celestial_wind_chimes", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_starwatch_telescope", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_sky_guardian_monument", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_abyssal_crystal_skeleton", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("relic_giant_bioluminescent_spore_pod", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("relic_emerald_cathedral_organ", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_ancient_miners_crystal_drill", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("relic_giant_pearl_clam", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 10)));
        GloomContent.block("relic_sunken_mermaid_fountain", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_royal_tide_throne", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.block("relic_ancient_ocean_navigators_table", () -> new DecorationBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50111_).m_60910_().m_60955_().m_60978_(1.5f).m_60953_(blockState -> 3)));
        GloomContent.food("x1_visual", 0, 0.0f);
    }

    public static final class Crop
    extends CropBlock {
        final String id;

        public Crop(String id) {
            super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50092_));
            this.id = id;
        }

        public IntegerProperty m_7959_() {
            return BlockStateProperties.f_61407_;
        }

        public int m_7419_() {
            return 3;
        }

        public VoxelShape m_5940_(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) {
            return Block.m_49796_((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)(2.0 + (double)(14 * this.m_52305_(s)) / 3.0), (double)16.0);
        }

        protected void m_7926_(StateDefinition.Builder<Block, BlockState> b) {
            b.m_61104_(new Property[]{BlockStateProperties.f_61407_});
        }

        protected ItemLike m_6404_() {
            return GloomContent.item(switch (this.id) {
                case "soul_wheat" -> "soul_wheat_seeds";
                case "shadow_pepper" -> "shadow_pepper_seeds";
                case "soul_herb" -> "soul_herb_seeds";
                case "ghost_pumpkin_stem" -> "ghost_pumpkin_seeds";
                default -> this.id;
            });
        }

        protected boolean m_6266_(BlockState s, BlockGetter w, BlockPos p) {
            return s.m_60713_((Block)FARMLAND.get()) || super.m_6266_(s, w, p);
        }
    }

    public static class GroundPlant
    extends BushBlock {
        public GroundPlant(BlockBehaviour.Properties p) {
            super(p);
        }

        protected boolean m_6266_(BlockState s, BlockGetter w, BlockPos p) {
            return s.m_204336_(BlockTags.f_144274_) || s.m_60713_((Block)FARMLAND.get()) || super.m_6266_(s, w, p);
        }
    }

    public static final class DuskSapling
    extends GroundPlant
    implements BonemealableBlock {
        public DuskSapling() {
            super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50746_));
        }

        public void m_213898_(BlockState s, ServerLevel w, BlockPos p, RandomSource r) {
            if (r.m_188503_(7) == 0 && w.m_46803_(p.m_7494_()) >= 9) {
                DuskSapling.grow(w, p);
            }
        }

        public static boolean grow(ServerLevel w, BlockPos p) {
            int z;
            int x;
            int y;
            for (y = 1; y <= 7; ++y) {
                for (x = -2; x <= 2; ++x) {
                    for (z = -2; z <= 2; ++z) {
                        BlockPos q = p.m_7918_(x, y, z);
                        if (w.m_46805_(q) && (w.m_8055_(q).m_60795_() || w.m_8055_(q).m_247087_())) continue;
                        return false;
                    }
                }
            }
            for (y = 3; y <= 6; ++y) {
                for (x = -2; x <= 2; ++x) {
                    for (z = -2; z <= 2; ++z) {
                        if (x * x + z * z + (y - 4) * (y - 4) > 9) continue;
                        w.m_7731_(p.m_7918_(x, y, z), (BlockState)((Block)LEAVES.get()).m_49966_().m_61124_((Property)LeavesBlock.f_54418_, (Comparable)Integer.valueOf(1)), 3);
                    }
                }
            }
            for (y = 0; y < 6; ++y) {
                w.m_7731_(p.m_6630_(y), ((Block)LOG.get()).m_49966_(), 3);
            }
            return true;
        }

        public boolean m_7370_(LevelReader w, BlockPos p, BlockState s, boolean c) {
            return true;
        }

        public boolean m_214167_(Level w, RandomSource r, BlockPos p, BlockState s) {
            return true;
        }

        public void m_214148_(ServerLevel w, RandomSource r, BlockPos p, BlockState s) {
            DuskSapling.grow(w, p);
        }
    }

    public static final class EchoReed
    extends SugarCaneBlock {
        public EchoReed() {
            super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50130_));
        }
    }

    public static final class NightBerries
    extends GroundPlant
    implements BonemealableBlock {
        public NightBerries() {
            super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50685_));
            this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_((Property)BlockStateProperties.f_61406_, (Comparable)Integer.valueOf(0)));
        }

        protected void m_7926_(StateDefinition.Builder<Block, BlockState> b) {
            b.m_61104_(new Property[]{BlockStateProperties.f_61406_});
        }

        public boolean m_6724_(BlockState s) {
            return (Integer)s.m_61143_((Property)BlockStateProperties.f_61406_) < 2;
        }

        public void m_213898_(BlockState s, ServerLevel w, BlockPos p, RandomSource r) {
            if ((Integer)s.m_61143_((Property)BlockStateProperties.f_61406_) < 2 && r.m_188503_(5) == 0 && w.m_45524_(p.m_7494_(), 0) >= 9) {
                w.m_46597_(p, (BlockState)s.m_61122_((Property)BlockStateProperties.f_61406_));
            }
        }

        public InteractionResult m_6227_(BlockState s, Level w, BlockPos p, Player user, InteractionHand hand, BlockHitResult hit) {
            if ((Integer)s.m_61143_((Property)BlockStateProperties.f_61406_) < 2) {
                return InteractionResult.PASS;
            }
            if (!w.f_46443_) {
                NightBerries.m_49840_((Level)w, (BlockPos)p, (ItemStack)new ItemStack((ItemLike)BERRY.get(), 2 + w.f_46441_.m_188503_(2)));
                w.m_46597_(p, (BlockState)s.m_61124_((Property)BlockStateProperties.f_61406_, (Comparable)Integer.valueOf(1)));
            }
            return InteractionResult.m_19078_((boolean)w.f_46443_);
        }

        public boolean m_7370_(LevelReader w, BlockPos p, BlockState s, boolean client) {
            return (Integer)s.m_61143_((Property)BlockStateProperties.f_61406_) < 2;
        }

        public boolean m_214167_(Level w, RandomSource r, BlockPos p, BlockState s) {
            return true;
        }

        public void m_214148_(ServerLevel w, RandomSource r, BlockPos p, BlockState s) {
            w.m_46597_(p, (BlockState)s.m_61122_((Property)BlockStateProperties.f_61406_));
        }
    }

    public static final class DarkFarmland
    extends FarmBlock {
        public DarkFarmland() {
            super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50093_));
        }

        public boolean canSustainPlant(BlockState s, BlockGetter w, BlockPos p, Direction side, IPlantable plant) {
            PlantType type = plant.getPlantType(w, p.m_7494_());
            return side == Direction.UP && (type == PlantType.CROP || type == PlantType.PLAINS) || super.canSustainPlant(s, w, p, side, plant);
        }

        public boolean isFertile(BlockState s, BlockGetter w, BlockPos p) {
            return (Integer)s.m_61143_((Property)f_53243_) > 0;
        }

        public void m_213898_(BlockState s, ServerLevel w, BlockPos p, RandomSource r) {
            boolean wet = w.m_46758_(p.m_7494_());
            for (int x = -4; x <= 4 && !wet; ++x) {
                for (int z = -4; z <= 4 && !wet; ++z) {
                    for (int y = 0; y <= 1 && !wet; ++y) {
                        BlockPos at = p.m_7918_(x, y, z);
                        wet = w.m_46805_(at) && w.m_6425_(at).m_205070_(FluidTags.f_13131_);
                    }
                }
            }
            int moisture = (Integer)s.m_61143_((Property)f_53243_);
            if (wet && moisture < 7) {
                w.m_7731_(p, (BlockState)s.m_61124_((Property)f_53243_, (Comparable)Integer.valueOf(7)), 2);
            } else if (!wet && moisture > 0) {
                w.m_7731_(p, (BlockState)s.m_61124_((Property)f_53243_, (Comparable)Integer.valueOf(moisture - 1)), 2);
            } else if (!wet && !(w.m_8055_(p.m_7494_()).m_60734_() instanceof CropBlock)) {
                w.m_46597_(p, ((Block)DIRT.get()).m_49966_());
            }
        }

        public void m_142072_(Level w, BlockState s, BlockPos p, Entity e, float distance) {
            super.m_142072_(w, s, p, e, distance);
            if (!w.f_46443_ && w.m_8055_(p).m_60713_(Blocks.f_50493_)) {
                w.m_46597_(p, ((Block)DIRT.get()).m_49966_());
            }
        }

        public void m_213897_(BlockState s, ServerLevel w, BlockPos p, RandomSource r) {
            if (!s.m_60710_((LevelReader)w, p)) {
                w.m_46597_(p, ((Block)DIRT.get()).m_49966_());
            }
        }
    }
}

