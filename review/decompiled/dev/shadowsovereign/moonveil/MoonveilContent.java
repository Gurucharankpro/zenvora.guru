/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.valueproviders.IntProvider
 *  net.minecraft.util.valueproviders.UniformInt
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.SpawnPlacements$Type
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CarpetBlock
 *  net.minecraft.world.level.block.DoublePlantBlock
 *  net.minecraft.world.level.block.DropExperienceBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.SaplingBlock
 *  net.minecraft.world.level.block.VineBlock
 *  net.minecraft.world.level.block.grower.AbstractTreeGrower
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.feature.ConfiguredFeature
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration
 *  net.minecraftforge.event.entity.EntityAttributeCreationEvent
 *  net.minecraftforge.event.entity.SpawnPlacementRegisterEvent
 *  net.minecraftforge.event.entity.SpawnPlacementRegisterEvent$Operation
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.moonveil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.shadowsovereign.PixelSpawnEgg;
import dev.shadowsovereign.gloom.GloomContent;
import dev.shadowsovereign.moonveil.MoonveilTree;
import dev.shadowsovereign.moonveil.Nuna;
import dev.shadowsovereign.shattered.ShatteredCluster;
import dev.shadowsovereign.shattered.ShatteredIslet;
import dev.shadowsovereign.shattered.ShatteredVine;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid="shadowsovereign", bus=Mod.EventBusSubscriber.Bus.MOD)
public final class MoonveilContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENTITY_TYPES, (String)"shadowsovereign");
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.FEATURES, (String)"shadowsovereign");
    public static final RegistryObject<Feature<BlockStateConfiguration>> TREE = FEATURES.register("moonveil_tree", MoonveilTree::new);
    public static final RegistryObject<Feature<BlockStateConfiguration>> SHATTERED_ISLET = FEATURES.register("shattered_islet", ShatteredIslet::new);
    public static final Map<String, RegistryObject<Block>> blocks = new LinkedHashMap<String, RegistryObject<Block>>();
    public static final RegistryObject<EntityType<Nuna>> NUNA;
    public static final RegistryObject<Item> NUNA_EGG;

    private static Block create(final String id, String kind, int light) {
        BlockBehaviour.Properties p = BlockBehaviour.Properties.m_60926_((BlockBehaviour)(kind.equals("log") ? Blocks.f_49999_ : (kind.equals("leaves") ? Blocks.f_50050_ : (kind.equals("soil") ? Blocks.f_50493_ : (kind.equals("ore") ? Blocks.f_49996_ : (kind.equals("stone") ? Blocks.f_50069_ : Blocks.f_50035_)))))).m_60953_(s -> light);
        return switch (kind) {
            case "cluster" -> new ShatteredCluster(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_152492_).m_60978_(1.5f).m_60953_(s -> light).m_60955_());
            case "hanging" -> new ShatteredVine(p.m_60910_().m_60955_());
            case "log" -> new RotatedPillarBlock(p);
            case "leaves" -> new LeavesBlock(p);
            case "vine" -> new VineBlock(p.m_60910_().m_60977_());
            case "carpet" -> new CarpetBlock(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_152543_).m_60953_(s -> light));
            case "plant" -> new GloomContent.GroundPlant(p.m_60910_());
            case "double" -> new DoublePlantBlock(p.m_60910_());
            case "sapling" -> new SaplingBlock(new AbstractTreeGrower(){

                protected ResourceKey<ConfiguredFeature<?, ?>> m_213888_(RandomSource r, boolean flowers) {
                    return ResourceKey.m_135785_((ResourceKey)Registries.f_256911_, (ResourceLocation)new ResourceLocation("shadowsovereign", id.replace("_sapling", "_tree")));
                }
            }, p.m_60910_().m_60977_()){};
            case "ore" -> new DropExperienceBlock(p, (IntProvider)UniformInt.m_146622_((int)2, (int)5));
            default -> new Block(p);
        };
    }

    public static Block block(String id) {
        return (Block)blocks.get(id).get();
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        FEATURES.register(bus);
        ENTITIES.register(bus);
    }

    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent e) {
        e.put((EntityType)NUNA.get(), Nuna.attributes().m_22265_());
    }

    @SubscribeEvent
    public static void spawns(SpawnPlacementRegisterEvent e) {
        e.register((EntityType)NUNA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, w, reason, at, r) -> w.m_8055_(at.m_7495_()).m_204336_(BlockTags.f_144274_), SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    static {
        try (InputStream in = MoonveilContent.class.getResourceAsStream("/data/shadowsovereign/moonveil/blocks.json");
             InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);){
            for (JsonElement el : JsonParser.parseReader((Reader)reader).getAsJsonArray()) {
                JsonObject o = el.getAsJsonObject();
                String id = o.get("id").getAsString();
                String kind = o.get("kind").getAsString();
                int light = o.get("light").getAsInt();
                RegistryObject b = BLOCKS.register(id, () -> MoonveilContent.create(id, kind, light));
                blocks.put(id, (RegistryObject<Block>)b);
                ITEMS.register(id, () -> new BlockItem((Block)b.get(), new Item.Properties()));
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Missing new biome block manifest", e);
        }
        NUNA = ENTITIES.register("nuna", () -> EntityType.Builder.m_20704_(Nuna::new, (MobCategory)MobCategory.CREATURE).m_20699_(0.65f, 0.95f).m_20702_(8).m_20712_("shadowsovereign:nuna"));
        NUNA_EGG = ITEMS.register("nuna_spawn_egg", () -> new PixelSpawnEgg((Supplier<? extends EntityType<? extends Mob>>)NUNA, 4473448, 11463935, new Item.Properties()));
    }
}

