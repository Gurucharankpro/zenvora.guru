package dev.shadowsovereign.content;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.*;

/** Registrations compiled against the real Forge API, never generated ABI shims. */
public final class PolishContent {
 public static final String NS="shadowsovereign";
 public static final JsonObject DATA=read();
 private static final DeferredRegister<Block> BLOCKS=DeferredRegister.create(ForgeRegistries.BLOCKS,NS);
 private static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,NS);
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,NS);
 private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,NS);
 public static final Map<String,RegistryObject<Block>> BLOCK_MAP=new LinkedHashMap<>();
 public static final RegistryObject<EntityType<PrismJerboa>> JERBOA=ENTITIES.register("prism_jerboa",()->EntityType.Builder.of(PrismJerboa::new,MobCategory.CREATURE).sized(.55f,.7f).clientTrackingRange(8).build(NS+":prism_jerboa"));
 static {
  for(JsonElement e:DATA.getAsJsonArray("blocks")){
   JsonObject d=e.getAsJsonObject();String id=d.get("id").getAsString();
   var b=BLOCKS.register(id,()->new ContentDecorationBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion().noCollission().strength(d.get("hardness").getAsFloat()).lightLevel(s->d.get("light").getAsInt()),id));
   BLOCK_MAP.put(id,b);ITEMS.register(id,()->new BlockItem(b.get(),new Item.Properties()));
  }
  for(JsonElement e:DATA.getAsJsonArray("biome_blocks")){
   JsonObject d=e.getAsJsonObject();String id=d.get("id").getAsString(),type=d.get("type").getAsString();
   var b=BLOCKS.register(id,()->{
    var p=BlockBehaviour.Properties.copy(type.equals("log")?Blocks.OAK_LOG:type.equals("leaves")?Blocks.OAK_LEAVES:Blocks.SANDSTONE).strength(d.get("hard").getAsFloat()).lightLevel(s->d.get("light").getAsInt());
    if(d.get("tool").getAsString().startsWith("pickaxe"))p.requiresCorrectToolForDrops();
    if(type.equals("log"))return new RotatedPillarBlock(p);
    if(type.equals("leaves"))return new LeavesBlock(p.noOcclusion());
    if(id.endsWith("_sapling"))return new SaplingBlock(new AbstractTreeGrower(){@Override protected ResourceKey<ConfiguredFeature<?,?>> getConfiguredFeature(RandomSource r,boolean flowers){return ResourceKey.create(Registries.CONFIGURED_FEATURE,new ResourceLocation(NS,id.startsWith("glasswood")?"glasswood_tree":"mirage_palm_tree"));}},p.noCollission().noOcclusion()){
     @Override protected boolean mayPlaceOn(net.minecraft.world.level.block.state.BlockState s,net.minecraft.world.level.BlockGetter l,net.minecraft.core.BlockPos pos){return DesertPlant.ground(s)||super.mayPlaceOn(s,l,pos);}
    };
    if(type.equals("cross")||type.equals("complex"))return new DesertPlant(p.noCollission().noOcclusion());
    if(id.endsWith("_sand"))return new SandBlock(id.startsWith("violet")?0x9870b7:0xd9b44a,p);
    return new Block(p);
   });
   BLOCK_MAP.put(id,b);ITEMS.register(id,()->new BlockItem(b.get(),new Item.Properties()));
  }
  ITEMS.register("prism_jerboa_spawn_egg",()->new ForgeSpawnEggItem(JERBOA,0xd8b64a,0x8f60c4,new Item.Properties()));
  FEATURES.register("singing_desert_ecology",DesertEcologyFeature::new);
  FEATURES.register("glasswood_tree",()->new DesertTreeFeature(false));
  FEATURES.register("mirage_palm_tree",()->new DesertTreeFeature(true));
 }
 static JsonObject read(){try(var in=PolishContent.class.getResourceAsStream("/assets/"+NS+"/content/polish.json")){if(in==null)throw new IllegalStateException("Missing polish metadata");return JsonParser.parseReader(new InputStreamReader(in,StandardCharsets.UTF_8)).getAsJsonObject();}catch(IOException e){throw new UncheckedIOException(e);}}
 public static Block block(String id){return BLOCK_MAP.get(id).get();}
 public static void register(IEventBus bus){
  BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);FEATURES.register(bus);
  bus.addListener(PolishContent::attributes);bus.addListener(PolishContent::setup);
 }
 static void attributes(EntityAttributeCreationEvent event){event.put(JERBOA.get(),PrismJerboa.attributes().build());}
 static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->SpawnPlacements.register(JERBOA.get(),SpawnPlacements.Type.ON_GROUND,net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,PrismJerboa::canSpawn));}
}
