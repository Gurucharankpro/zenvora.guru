package dev.shadowsovereign.content;

import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid="shadowsovereign",value=Dist.CLIENT)
public final class ContentClient {
 public static final KeyMapping KEY=new KeyMapping("key.shadowsovereign.autopilot",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_U,"key.categories.shadowsovereign");
 public static ContentNetwork.Status STATUS=new ContentNetwork.Status(0,0,0,0,0,13,0,false);
 private static final Map<String,JsonObject> EFFECTS=new LinkedHashMap<>();
 private static final Map<String,JsonObject> BIOMES=new HashMap<>();
 private static ClientLevel current;private static int generation,alive,large,clock;private static double density=1;
 private static AmbientLoop ambience;
 static {
  for(JsonElement e:ContentBootstrap.ATMOSPHERE.getAsJsonArray("effects")){JsonObject d=e.getAsJsonObject();EFFECTS.put(d.get("id").getAsString(),d);}
  for(JsonElement e:ContentBootstrap.ATMOSPHERE.getAsJsonArray("biomes")){JsonObject d=e.getAsJsonObject();BIOMES.put(d.get("biomeRegistryName").getAsString(),d);}
 }
 public static void receive(ContentNetwork.Status state){
  STATUS=state;
  if(state.open())Minecraft.getInstance().setScreen(new ContentScreen());
 }
 @Mod.EventBusSubscriber(modid="shadowsovereign",value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
 public static final class Registration {
  @SubscribeEvent public static void keys(RegisterKeyMappingsEvent e){e.register(KEY);}
  @SubscribeEvent public static void particles(RegisterParticleProvidersEvent e){
   ContentBootstrap.EFFECTS.forEach((id,type)->e.registerSpriteSet(type.get(),sprites->(options,world,x,y,z,dx,dy,dz)->alive>=Math.min(150,preset().get("maxParticles").getAsInt())?null:new AtmosphericParticle(world,x,y,z,dx,dy,dz,sprites,EFFECTS.get(id))));
  }
 }
 private static JsonObject biome(){
  var mc=Minecraft.getInstance();if(mc.level==null||mc.player==null)return null;
  return mc.level.getBiome(mc.player.blockPosition()).unwrapKey().map(k->BIOMES.get(k.location().toString())).orElse(null);
 }
 private static JsonObject preset(){return ContentBootstrap.ATMOSPHERE.getAsJsonObject("performance").getAsJsonObject("presets").getAsJsonObject(ContentConfig.QUALITY.get().name().toLowerCase(Locale.ROOT));}
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){
  if(e.phase!=TickEvent.Phase.END)return;var mc=Minecraft.getInstance();
  if(mc.level!=current){if(ambience!=null)ambience.finish();ambience=null;current=mc.level;generation++;alive=large=clock=0;density=1;STATUS=new ContentNetwork.Status(0,0,0,0,0,13,0,false);}
  if(mc.player==null||mc.level==null||mc.isPaused())return;
  while(KEY.consumeClick())ContentNetwork.action(0);
  clock++;if(clock%20==0)ambientSound();if(!ContentConfig.ATMOSPHERE.get())return;
  JsonObject biome=biome();if(biome==null&&STATUS.atmosphere()==0)return;
  JsonObject quality=preset();
  if(clock%20==0){
   long mobs=mc.level.getEntitiesOfClass(Mob.class,mc.player.getBoundingBox().inflate(16),Mob::isAlive).size();
   double desired=mobs>10?.2:mobs>6?.35:mobs>3?.6:1;density+=(desired-density)*.2;
  }
  boolean combat=mc.player.hurtTime>0||mc.player.tickCount-mc.player.getLastHurtMobTimestamp()<100||STATUS.atmosphere()==5;
  int[] spawned={0};
  if(biome!=null){
   boolean day=mc.level.isDay();double intensity=biome.get(day?"daytimeParticleIntensity":"nighttimeParticleIntensity").getAsDouble();
   JsonObject rates=biome.getAsJsonObject("effectFrequency");
   for(JsonElement el:biome.getAsJsonArray("effects")){
    JsonObject effect=el.getAsJsonObject();String id=effect.get("id").getAsString();JsonObject rate=rates.getAsJsonObject(id);
    double chance=rate.get("spawnAttemptsPerSecondAtFullIntensityHigh").getAsDouble()/20*intensity*rate.get(day?"dayMultiplier":"nightMultiplier").getAsDouble();
    if(effect.get("number").getAsInt()%2==0)chance*=quality.get("secondaryEffectMul").getAsDouble();
    if(mc.level.isRaining())chance*=.6;if(mc.level.isThundering()&&id.contains("raven"))chance*=2;
    JsonObject spawn=biome.getAsJsonObject("recommendedSpawn").getAsJsonObject(id);JsonArray heights=spawn.getAsJsonArray("heightBlocksRelative");
    attempt(id,chance,quality,combat,spawned,heights.get(0).getAsDouble(),heights.get(1).getAsDouble());
   }
  }
  if(STATUS.atmosphere()>0&&STATUS.atmosphere()<=5){
   JsonObject structure=ContentBootstrap.ATMOSPHERE.getAsJsonArray("structures").get(STATUS.atmosphere()-1).getAsJsonObject();
   for(JsonElement el:structure.getAsJsonArray("effectsUsed")){
    JsonObject effect=el.getAsJsonObject();attempt(effect.get("id").getAsString(),effect.get("spawnsPerSecondAtHigh").getAsDouble()/20,quality,combat,spawned,.5,6);
   }
  }
 }
 private static void ambientSound(){
  var mc=Minecraft.getInstance();JsonObject b=ContentConfig.ATMOSPHERE.get()?biome():null;
  String id=b==null?null:String.format(Locale.ROOT,"x2_ambient_%02d_%s",b.get("biomeNumber").getAsInt(),b.get("biomeKey").getAsString());
  if(ambience!=null&&(id==null||!id.equals(ambience.id))){ambience.fadeOut=true;ambience=null;}
  if(id!=null&&ambience==null){var sound=net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("shadowsovereign",id));
   if(sound!=null){ambience=new AmbientLoop(sound,id);mc.getSoundManager().play(ambience);}
  }
 }
 private static final class AmbientLoop extends net.minecraft.client.resources.sounds.AbstractTickableSoundInstance {
  final String id;boolean fadeOut;final ClientLevel world;
  AmbientLoop(net.minecraft.sounds.SoundEvent sound,String id){super(sound,net.minecraft.sounds.SoundSource.AMBIENT,net.minecraft.util.RandomSource.create());this.id=id;world=Minecraft.getInstance().level;looping=true;delay=0;relative=true;volume=0;pitch=1;}
  void finish(){stop();}
  @Override public void tick(){
   if(world!=Minecraft.getInstance().level){stop();return;}
   if(fadeOut){volume=Math.max(0,volume-.01f);if(volume<=0)stop();}
   else volume=Math.min(.22f,volume+.01f);
  }
 }
 private static void attempt(String id,double chance,JsonObject quality,boolean combat,int[] spawned,double low,double high){
  var mc=Minecraft.getInstance();JsonObject effect=EFFECTS.get(id);if(effect==null)return;
  JsonObject particle=effect.getAsJsonObject("particle");double size=particle.get("recommendedQuadSizeBlocks").getAsDouble();
  boolean big=size>=1;
  if(combat&&big)return;
  int cap=Math.min(150,quality.get("maxParticles").getAsInt());
  if(alive>=cap||spawned[0]>=4||big&&large>=quality.get("largeQuadCap").getAsInt())return;
  chance*=quality.get("mul").getAsDouble()*density*(combat?.5:1)*(STATUS.atmosphere()==5?.25:1);
  if(mc.level.random.nextDouble()>=Math.min(1,chance))return;
  double radius=Math.min(quality.get("spawnRadius").getAsDouble(),mc.options.renderDistance().get()*8);
  double minimum=big?8:3;if(radius<=minimum)return;
  double angle=mc.level.random.nextDouble()*Math.PI*2,dist=minimum+mc.level.random.nextDouble()*(radius-minimum);
  Vec3 p=mc.player.position().add(Math.cos(angle)*dist,low+mc.level.random.nextDouble()*(high-low),Math.sin(angle)*dist);
  if(p.distanceToSqr(mc.player.position())<36&&p.subtract(mc.player.getEyePosition()).normalize().dot(mc.player.getLookAngle())>.7)return;
  BlockPos at=BlockPos.containing(p);if(!mc.level.hasChunkAt(at)||!mc.level.getBlockState(at).getCollisionShape(mc.level,at).isEmpty())return;
  double drift=particle.get("horizontalDriftBlocksPerTick").getAsDouble();
  var type=ContentBootstrap.EFFECTS.get(id);if(type==null)return;
  mc.level.addParticle(type.get(),p.x,p.y,p.z,Math.cos(angle)*drift,0,Math.sin(angle)*drift);spawned[0]++;
 }
 @SubscribeEvent public static void fogColor(ViewportEvent.ComputeFogColor e){
  if(!ContentConfig.ATMOSPHERE.get()||!ContentConfig.FOG.get()||e.getCamera().getFluidInCamera()!=FogType.NONE)return;
  JsonObject biome=biome();if(biome==null)return;
  var mc=Minecraft.getInstance();JsonArray rgb=biome.getAsJsonObject("fog").getAsJsonArray(mc.level.isDay()?"colorDayRGB01":"colorNightRGB01");
  float strength=ContentConfig.QUALITY.get()==ContentConfig.Quality.LOW?.15f:ContentConfig.QUALITY.get()==ContentConfig.Quality.MEDIUM?.35f:.5f;
  if(STATUS.atmosphere()==5)strength*=.5f;
  e.setRed(e.getRed()*(1-strength)+rgb.get(0).getAsFloat()*strength);e.setGreen(e.getGreen()*(1-strength)+rgb.get(1).getAsFloat()*strength);e.setBlue(e.getBlue()*(1-strength)+rgb.get(2).getAsFloat()*strength);
 }
 @SubscribeEvent public static void fog(ViewportEvent.RenderFog e){
  if(!ContentConfig.ATMOSPHERE.get()||!ContentConfig.FOG.get()||ContentConfig.QUALITY.get()==ContentConfig.Quality.LOW||e.getCamera().getFluidInCamera()!=FogType.NONE)return;
  JsonObject b=biome();if(b==null)return;JsonObject fog=b.getAsJsonObject("fog");
  float distance=Minecraft.getInstance().options.renderDistance().get()*16;
  // Never shorten a native nearer fog boundary or obscure a nearby fight.
  float end=Math.max(32,distance*fog.get("endFractionOfRenderDistance").getAsFloat());
  if(STATUS.atmosphere()==5)end=Math.max(end,distance*.95f);
  if(end<e.getFarPlaneDistance()){
   e.setNearPlaneDistance(Math.min(e.getNearPlaneDistance(),distance*fog.get("startFractionOfRenderDistance").getAsFloat()));e.setFarPlaneDistance(end);e.setCanceled(true);
  }
 }
 @SubscribeEvent public static void hud(RenderGuiEvent.Post e){
  var mc=Minecraft.getInstance();if(mc.player==null||mc.screen!=null||mc.options.hideGui||STATUS.active()==0)return;
  String value=net.minecraft.network.chat.Component.translatable("content.shadowsovereign.hud",STATUS.active(),STATUS.recovering(),STATUS.failed()).getString();
  GuiGraphics g=e.getGuiGraphics();int w=Math.min(mc.getWindow().getGuiScaledWidth()-8,mc.font.width(value)+12);
  g.fill(4,4,w+4,19,0xb0181028);g.drawString(mc.font,value,10,8,0xd5b7ff,false);
 }
 private static final class AtmosphericParticle extends TextureSheetParticle {
  private final SpriteSet sprites;private final int[] order;private final int ticksPerFrame,epoch;private final boolean fullbright,loop,big;private final float maximumAlpha;private boolean released;
  AtmosphericParticle(ClientLevel level,double x,double y,double z,double dx,double dy,double dz,SpriteSet sprites,JsonObject effect){
   super(level,x,y,z);this.sprites=sprites;epoch=generation;
   JsonObject p=effect.getAsJsonObject("particle"),animation=effect.getAsJsonObject("animation");
   JsonObject life=p.getAsJsonObject("lifetimeTicks");lifetime=life.get("min").getAsInt()+random.nextInt(Math.max(1,life.get("max").getAsInt()-life.get("min").getAsInt()+1));
   quadSize=p.get("recommendedQuadSizeBlocks").getAsFloat()/2;big=quadSize>=.5;gravity=p.get("gravity").getAsFloat();fullbright=p.get("fullbright").getAsBoolean();maximumAlpha=p.get("maxAlpha").getAsFloat();
   ticksPerFrame=animation.get("ticksPerFrame").getAsInt();loop=animation.get("playbackMode").getAsString().contains("loop");
   JsonArray frames=animation.getAsJsonArray("frameOrder");order=new int[frames.size()];for(int i=0;i<order.length;i++)order[i]=frames.get(i).getAsInt();
   xd=dx;yd=dy;zd=dz;hasPhysics=false;alpha=maximumAlpha;alive++;if(big)large++;sprite();
  }
  private void sprite(){int at=loop?(age/Math.max(1,ticksPerFrame))%order.length:Math.min(order.length-1,(int)((long)age*order.length/Math.max(1,lifetime)));int skip=preset().get("frameSkip").getAsInt();at=(at/skip)*skip;setSprite(sprites.get(order[at],order.length-1));}
  @Override public void tick(){
   super.tick();if(removed)return;sprite();
   Vec3 camera=Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();double d=camera.distanceTo(new Vec3(x,y,z));
   double cap=preset().get("cullDistance").getAsDouble();if(d>cap||epoch!=generation){remove();return;}
   float fade=(float)Math.min(1,Math.min(age/6.0,(lifetime-age)/8.0));
   alpha=maximumAlpha*Math.max(0,fade)*(float)Math.min(1,d/2.5)* (float)Math.min(1,(cap-d)/6);
   if(Minecraft.getInstance().player!=null&&Minecraft.getInstance().player.hurtTime>0)alpha=Math.min(alpha,.5f);
  }
  @Override public int getLightColor(float partial){return fullbright?15728880:super.getLightColor(partial);}
  @Override public ParticleRenderType getRenderType(){return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;}
  @Override public void remove(){if(!released){released=true;if(epoch==generation){alive=Math.max(0,alive-1);if(big)large=Math.max(0,large-1);}}super.remove();}
 }
}
