package dev.shadowsovereign.content;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.particles.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.*;

/** Loaded by the existing mod constructor; no replacement @Mod entry point. */
public final class ContentBootstrap {
 public static final String NS="shadowsovereign";
 public static final JsonObject ATMOSPHERE=read("x2.json");
 public static final JsonArray BLOCKS=read("blocks.json").getAsJsonArray("blocks");
 private static final DeferredRegister<ParticleType<?>> PARTICLES=DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,NS);
 private static final DeferredRegister<SoundEvent> SOUNDS=DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,NS);
 public static final Map<String,RegistryObject<SimpleParticleType>> EFFECTS=new LinkedHashMap<>();
 static {
  for(JsonElement entry:ATMOSPHERE.getAsJsonArray("effects")){
   String id=entry.getAsJsonObject().get("id").getAsString();
   if(EFFECTS.put(id,PARTICLES.register(id,()->new SimpleParticleType(false)))!=null)throw new IllegalStateException("Duplicate X2 effect "+id);
  }
  for(int i=1;i<=10;i++){
   String[] names={"crimson_shadow_forest","moonveil_hollow","shattered_sky","eternal_abyss","sunken_kingdom","singing_glass_desert","thunderfall_highlands","dreamroot_canopy","mirrorfall_marsh","titanbone_caverns"};
   String id=String.format(java.util.Locale.ROOT,"x2_ambient_%02d_%s",i,names[i-1]);
   SOUNDS.register(id,()->SoundEvent.createVariableRangeEvent(new ResourceLocation(NS,id)));
  }
 }
 private static JsonObject read(String name){
  String path="/assets/"+NS+"/content/"+name;
  try(InputStream stream=ContentBootstrap.class.getResourceAsStream(path)){
   if(stream==null)throw new IllegalStateException("Missing content metadata "+path);
   return JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
  }catch(IOException e){throw new UncheckedIOException(e);}
 }
 public static void register(IEventBus bus){
  PARTICLES.register(bus);SOUNDS.register(bus);
  ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,ContentConfig.SERVER,"shadowsovereign-autopilot-server.toml");
  ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,ContentConfig.CLIENT,"shadowsovereign-living-client.toml");
  ContentNetwork.register();
  PolishContent.register(bus);
 }
}
