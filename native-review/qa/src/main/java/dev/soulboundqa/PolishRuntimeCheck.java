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
  Files.writeString(Path.of("POLISH-RUNTIME.json"),new GsonBuilder().setPrettyPrinting().create().toJson(result));
  System.out.println("SOULBOUND_POLISH_NATIVE_PASS "+result);
 }
}
