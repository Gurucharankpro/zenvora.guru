package dev.soulboundqa;
import com.google.gson.*;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/** Isolated production-server checks. This QA mod never enters the gameplay JAR. */
@Mod.EventBusSubscriber(modid="soulboundqa")
public final class PolishRuntimeCheck {
 @SubscribeEvent public static void started(ServerStartedEvent event)throws Exception{
  if(!Boolean.getBoolean("soulboundqa.polish"))return;
  var server=event.getServer();var level=server.overworld();var rm=server.getResourceManager();
  int blocks=0,states=0,templates=0,entities=0;
  List<String> errors=new ArrayList<>();
  JsonObject spec=(JsonObject)Class.forName("dev.shadowsovereign.content.PolishContent").getField("DATA").get(null);
  for(String group:List.of("blocks","biome_blocks"))for(JsonElement e:spec.getAsJsonArray(group)){
   var id=new ResourceLocation("shadowsovereign",e.getAsJsonObject().get("id").getAsString());
   if(!ForgeRegistries.BLOCKS.containsKey(id)||!ForgeRegistries.ITEMS.containsKey(id))throw new AssertionError("Missing block or item "+id);blocks++;
  }
  Set<String> paths=new TreeSet<>();
  for(var e:spec.getAsJsonObject("templates").entrySet())paths.add(e.getValue().getAsJsonObject().get("path").getAsString());
  for(var e:spec.getAsJsonArray("biome_structures"))paths.add(e.getAsJsonObject().get("path").getAsString());
  for(var e:spec.getAsJsonArray("decor_placements"))paths.add(e.getAsJsonObject().get("template").getAsString());
  for(String t:List.of("glasswood_tree_large","glasswood_tree_small","mirage_palm_tall","mirage_palm_short"))paths.add("data/shadowsovereign/structures/polish/"+t+".nbt");
  for(String path:paths){
   var id=new ResourceLocation("shadowsovereign",path.substring("data/shadowsovereign/".length()));
   CompoundTag n;
   try(var in=rm.getResource(id).orElseThrow().open()){n=NbtIo.readCompressed(in);}
   for(var raw:n.getList("palette",10)){
    CompoundTag state=(CompoundTag)raw;var key=new ResourceLocation(state.getString("Name"));
    if(!ForgeRegistries.BLOCKS.containsKey(key)){errors.add("Unknown actual block "+key+" in "+path);continue;}
    var block=ForgeRegistries.BLOCKS.getValue(key);var props=state.getCompound("Properties");
    for(String name:props.getAllKeys()){
     var prop=block.getStateDefinition().getProperty(name);
     if(prop==null||prop.getValue(props.getString(name)).isEmpty())errors.add("Invalid actual block property "+state+" in "+path);
    }states++;
   }
   String template=path.substring("data/shadowsovereign/structures/".length(),path.length()-4);
   if(server.overworld().getStructureManager().get(new ResourceLocation("shadowsovereign",template)).isEmpty())throw new AssertionError("Native structure decoder rejected "+path);templates++;
  }
  if(!errors.isEmpty())throw new AssertionError(new Gson().toJson(errors));
  if(server.registryAccess().registryOrThrow(Registries.BIOME).get(new ResourceLocation("shadowsovereign:singing_glass_desert"))==null)throw new AssertionError("Biome registry failed");
  for(String id:List.of("dr_salmawy","raven_seamstress","clockwork_widow","prism_jerboa")){
   EntityType<?> type=ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("shadowsovereign",id));
   if(type==null)throw new AssertionError("No entity "+id);var entity=type.create(level);
   if(!(entity instanceof net.minecraft.world.entity.Mob mob)||mob.getMaxHealth()<=0)throw new AssertionError("Invalid attributes "+id);
   var tag=new CompoundTag();if(!entity.save(tag))throw new AssertionError("Entity save failed "+id);
   if(EntityType.loadEntityRecursive(tag,level,x->x)==null)throw new AssertionError("Entity reload failed "+id);entities++;
  }
  JsonObject result=new JsonObject();result.addProperty("status","passed");result.addProperty("registered_blocks_items",blocks);result.addProperty("native_template_decodes",templates);result.addProperty("native_palette_states",states);result.addProperty("entity_attributes_save_reload",entities);result.addProperty("biome_registry",true);result.addProperty("graphical_client",false);
  result.add("location_criteria",locations(server));
  Files.writeString(Path.of("POLISH-RUNTIME.json"),new GsonBuilder().setPrettyPrinting().create().toJson(result));
  System.out.println("SOULBOUND_POLISH_NATIVE_PASS "+result);
 }
 static JsonObject locations(net.minecraft.server.MinecraftServer server){
  var manager=server.getAdvancements();var desertId=new ResourceLocation("shadowsovereign:journey/visit_singing_glass_desert");
  List<ResourceLocation> visits=new ArrayList<>();
  for(var a:manager.getAllAdvancements())if(a.getId().getNamespace().equals("shadowsovereign")&&a.getId().getPath().startsWith("journey/visit_"))visits.add(a.getId());
  if(visits.size()!=11)throw new AssertionError("Expected eleven visit advancements");
  var wrong=net.minecraftforge.common.util.FakePlayerFactory.get(server.overworld(),new com.mojang.authlib.GameProfile(UUID.randomUUID(),"WrongPredicateQA"));wrong.setPos(0,65,0);
  net.minecraft.advancements.CriteriaTriggers.LOCATION.trigger(wrong);
  List<ResourceLocation> restricted=new ArrayList<>(visits);restricted.add(new ResourceLocation("shadowsovereign:journey/welcome"));restricted.add(new ResourceLocation("shadowsovereign:journey/cartographer"));
  for(var id:restricted)if(wrong.getAdvancements().getOrStartProgress(manager.getAdvancement(id)).isDone())throw new AssertionError("Location reward granted in wrong dimension: "+id);
  var realm=server.getLevel(net.minecraft.resources.ResourceKey.create(Registries.DIMENSION,new ResourceLocation("shadowsovereign:echo_realms")));
  var biomeKey=net.minecraft.resources.ResourceKey.create(Registries.BIOME,new ResourceLocation("shadowsovereign:singing_glass_desert"));
  var found=realm.findClosestBiome3d(b->b.is(biomeKey),net.minecraft.core.BlockPos.ZERO,6400,32,64);
  if(found==null)throw new AssertionError("No desert biome found for positive predicate test");
  var noisePoint=found.getFirst();net.minecraft.core.BlockPos at=null;
  // /locate samples noise biomes; the final fuzzy biome zoom can select a
  // neighbouring biome at the returned block. Verify a real surface position.
  for(int ring=0;ring<=1&&at==null;ring++)for(int dx=-ring;dx<=ring&&at==null;dx++)for(int dz=-ring;dz<=ring&&at==null;dz++){
   if(ring>0&&Math.max(Math.abs(dx),Math.abs(dz))!=ring)continue;
   int cx=(noisePoint.getX()>>4)+dx,cz=(noisePoint.getZ()>>4)+dz;realm.getChunk(cx,cz);
   for(int x=cx*16;x<cx*16+16&&at==null;x++)for(int z=cz*16;z<cz*16+16&&at==null;z++){
    var candidate=realm.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,new net.minecraft.core.BlockPos(x,80,z));
    if(realm.getBiome(candidate).is(biomeKey))at=candidate;
   }
  }
  if(at==null)throw new AssertionError("Test setup: no actual surface desert block near noise-biome search result "+noisePoint);
  var right=net.minecraftforge.common.util.FakePlayerFactory.get(realm,new com.mojang.authlib.GameProfile(UUID.randomUUID(),"RealmPredicateQA"));right.setPos(at.getX()+.5,at.getY(),at.getZ()+.5);
  right.getAdvancements().reload(manager);
  net.minecraft.advancements.CriteriaTriggers.LOCATION.trigger(right);
  if(!right.getAdvancements().getOrStartProgress(manager.getAdvancement(desertId)).isDone())throw new AssertionError("Correct biome did not grant its visit advancement: dimension="+right.level().dimension()+", position="+right.blockPosition()+", biome="+right.level().getBiome(right.blockPosition()).unwrapKey()+", loaded="+right.serverLevel().isLoaded(right.blockPosition()));
  for(var id:visits)if(!id.equals(desertId)&&right.getAdvancements().getOrStartProgress(manager.getAdvancement(id)).isDone())throw new AssertionError("Visit granted for the wrong biome: "+id);
  if(right.getAdvancements().getOrStartProgress(manager.getAdvancement(new ResourceLocation("shadowsovereign:journey/cartographer"))).isDone())throw new AssertionError("Cartographer awarded without its ten biomes");
  if(!right.getAdvancements().getOrStartProgress(manager.getAdvancement(new ResourceLocation("shadowsovereign:journey/welcome"))).isDone())throw new AssertionError("Correct dimension did not grant welcome");
  int xp=right.totalExperience;net.minecraft.advancements.CriteriaTriggers.LOCATION.trigger(right);
  if(right.totalExperience!=xp)throw new AssertionError("Repeated location trigger duplicated XP rewards");
  JsonObject result=new JsonObject();result.addProperty("wrong_dimension_rejected",true);result.addProperty("wrong_biomes_rejected",10);result.addProperty("correct_desert_and_dimension_awarded",true);result.addProperty("repeat_reward_unchanged",true);return result;
 }
}
