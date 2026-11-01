package dev.shadowsovereign.expansion;
import dev.shadowsovereign.army.*;
import dev.shadowsovereign.mythic.*;
import dev.shadowsovereign.soulforge.CompanionService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;

/** Server-side cosmetic bridge. Commands are static mod functions, never user text.
 * Every failure is isolated so optional visuals cannot fail gameplay operations. */
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid="shadowsovereign")
public final class X1Hooks {
 private X1Hooks(){}
 private static boolean warned,disabled;
 private static java.lang.ref.WeakReference<MinecraftServer> initialized=new java.lang.ref.WeakReference<>(null);
 private static void init(MinecraftServer server){if(initialized.get()!=server){run(server,"function shadowsovereign:x1/init");initialized=new java.lang.ref.WeakReference<>(server);}}
 private static void run(MinecraftServer server,String command){
  if(disabled)return;
  try{server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(),command);}catch(RuntimeException | LinkageError error){if(error instanceof LinkageError)disabled=true;if(!warned){warned=true;System.err.println("Soulbound X1 visual command unavailable: "+error);}}
 }
 public static void emit(Entity e,int effect){
  emitKey(e,effect<10?"0"+effect:""+effect,effect);
 }
 private static void emitKey(Entity e,String key,int effect){
  if(e==null || e.level().isClientSide || e.getServer()==null || effect<1 || effect>15)return;
  double x=e.getX(),y=e.getY(),z=e.getZ();
  if(!Double.isFinite(x)||!Double.isFinite(y)||!Double.isFinite(z))return;
  MinecraftServer server=e.getServer();
  init(server);
  String id=key;
  run(server,"execute in "+e.level().dimension().location()+" positioned "+x+" "+y+" "+z+" run function shadowsovereign:x1/event/"+id);
 }
 public static void tick(MinecraftServer server){
  if(server==null)return;
  init(server);
  for(var world:server.getAllLevels())run(server,"execute in "+world.dimension().location()+" if entity @e[type=minecraft:item_display,tag=sb_x1_fx,limit=1] run function shadowsovereign:x1/tick_world");
 }
 public static void summoned(ServerPlayer p,ShadowRecord r){
  Mob body=ShadowService.active(p.server,r);if(body==null)return;
  int tier=r.tier.ordinal();
  emit(body,body instanceof MythicBeast || body instanceof SoulDevourer?14:tier>=3?3:2);
  if(tier>=4)emit(body,9);else if(tier>=2)emit(body,8);
 }
 public static void acquired(ServerPlayer p){emit(p,1);}
 public static void recall(Mob body,int ignored){emit(body,4);}
 public static void command(ServerPlayer p,ShadowRecord r,Order order){
  Mob body=ShadowService.active(p.server,r);emit(body==null?p:body,order==Order.FOLLOW?6:7);
 }
 public static void bond(ServerPlayer p,ShadowRecord r,int previousRank){
  if(CompanionService.bondRank(r.bond)>previousRank){Mob b=ShadowService.active(p.server,r);emit(b==null?p:b,5);}
 }
 public static void attack(MythicBeast mob,MythicProfiles.Move move){
  if(!ShadowService.isShadow(mob))return;
  if(move.effect()==5)emit(mob,13);
  else if(move.effect()==0){String clip=move.clip();String shape=clip.contains("thrust")||clip.contains("stab")?"slash_thrust":clip.contains("slam")||clip.contains("hammer")?"slash_overhead":clip.contains("slash")?"slash_diagonal":"slash_horizontal";emitKey(mob,"11_"+shape,11);}
 }
 @net.minecraftforge.eventbus.api.SubscribeEvent(priority=net.minecraftforge.eventbus.api.EventPriority.LOWEST)
 public static void damage(net.minecraftforge.event.entity.living.LivingDamageEvent event){
  if(event.isCanceled()||event.getAmount()<=0||event.getEntity().level().isClientSide)return;
  Entity caster=event.getSource().getEntity();if(caster==null||!ShadowService.isShadow(caster))return;
  if(!(caster instanceof MythicBeast)&&event.getSource().getDirectEntity()==caster)emit(caster,11);
  emit(event.getEntity(),12);
 }
}
