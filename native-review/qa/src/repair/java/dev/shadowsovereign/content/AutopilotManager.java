package dev.shadowsovereign.content;

import dev.shadowsovereign.army.*;
import dev.shadowsovereign.gloom.GloomRealm;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** An overlay on native FOLLOW, never a replacement army/ownership system.
 * Only companions observed at the native follow-slot hook are eligible for
 * navigation changes; explicit work, guard, patrol and combat commands win.
 */
@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class AutopilotManager {
 private static final Map<UUID,Owner> OWNERS=new HashMap<>();
 private static final LinkedHashMap<UUID,Agent> AGENTS=new LinkedHashMap<>();
 private static final ArrayDeque<UUID> QUEUE=new ArrayDeque<>();
 private static int frame,paths,evaluations,skipped;
 private static final String SETTINGS="ContentAutopilot";
 private static boolean enabled(String key){return ContentConfig.flag(key);}
 private static int integer(String key){return ContentConfig.integer(key);}
 private static double number(String key){return ContentConfig.number(key);}
 private static final class Owner {
  int lastRequest=-10000,lastRegroup=-10000,lastNotice=-10000,lastBlockAction,pathCount,atmosphere;
  int flags=13; // enabled, combat, smart-follow; build/performance are off.
  Vec3 position=Vec3.ZERO,look=Vec3.ZERO;BlockPos buildingTarget;
  List<Mob> nearby=List.of();ContentNetwork.Status previous;
  boolean loaded;
 }
 private static final class Agent {
  final UUID owner,record,body;int born,seen,due,lastCheck,evaluatedAt,confirm,attempts,stage,stageSince,stuckSince=-1,lastPath=-10000,lastTeleport=-10000,failedUntil,lastTargetChange,formation,safeAt;
  Vec3 base,desired,progress,narrowStart,safeCached,support;boolean regroup,narrow;int priority=99;
  Agent(ServerPlayer p,ShadowRecord r,Mob mob,Vec3 base){owner=p.getUUID();record=r.id;body=mob.getUUID();born=frame;seen=frame;progress=mob.position();lastCheck=frame;this.base=base;formation=r.battle.formation.ordinal();}
 }
 private static Owner owner(ServerPlayer p){
  Owner o=OWNERS.computeIfAbsent(p.getUUID(),id->new Owner());
  if(!o.loaded){
   CompoundTag n=GloomRealm.state(p).getCompound(SETTINGS);
   o.flags=bit(n,"Enabled",1,true)|bit(n,"Building",2,false)|bit(n,"Combat",4,true)|bit(n,"Follow",8,true)|bit(n,"Performance",16,false);o.loaded=true;
  }return o;
 }
 private static int bit(CompoundTag n,String key,int flag,boolean fallback){return(n.contains(key)?n.getBoolean(key):fallback)?flag:0;}
 private static boolean flag(Owner o,int flag){return(o.flags&flag)!=0;}
 private static boolean performance(Owner o){return flag(o,16)||enabled("performance.forcePerformanceMode");}
 private static boolean eligible(ServerPlayer p,ShadowRecord r,Mob mob){
  return r!=null&&mob!=null&&mob.isAlive()&&mob.level()==p.level()&&p.getUUID().equals(ShadowService.ownerId(mob))&&ShadowService.valid(mob,r)&&r.order==Order.FOLLOW&&r.battle.commandedTarget==null&&!r.battle.paused&&!r.vigil.post;
 }
 /** Exact replacement of BattleService.followPosition at the native follow sites. */
 public static Vec3 followPosition(Mob mob,ShadowRecord r,ServerPlayer p){
  Vec3 normal=BattleService.followPosition(mob,r,p);
  if(!enabled("general.enabled")||!eligible(p,r,mob)||!flag(owner(p),1))return normal;
  Agent a=AGENTS.get(mob.getUUID());
  if(a==null){a=new Agent(p,r,mob,normal);AGENTS.put(a.body,a);QUEUE.addLast(a.body);}
  if(!a.owner.equals(p.getUUID())||!a.record.equals(r.id))return normal;
  a.base=normal;a.seen=frame;
  if(a.formation!=r.battle.formation.ordinal()){a.formation=r.battle.formation.ordinal();a.desired=null;a.narrow=false;a.regroup=false;}
  return a.desired==null?normal:a.desired;
 }
 public static void request(ServerPlayer p,int action){
  Owner o=owner(p);
  if(frame-o.lastRequest<integer("multiplayer.requestMinIntervalTicks"))return;
  o.lastRequest=frame;
  int mask=switch(action){case 1->2;case 2->4;case 3->16;case 4->8;case 6->1;default->0;};
  if(mask!=0){
   o.lastBlockAction=frame;
   o.flags^=mask;CompoundTag n=GloomRealm.state(p).getCompound(SETTINGS);
   n.putBoolean("Enabled",flag(o,1));n.putBoolean("Building",flag(o,2));n.putBoolean("Combat",flag(o,4));n.putBoolean("Follow",flag(o,8));n.putBoolean("Performance",flag(o,16));
   GloomRealm.state(p).put(SETTINGS,n);GloomRealm.dirty(p);
   for(Agent a:AGENTS.values())if(a.owner.equals(p.getUUID())){a.desired=null;a.stage=0;a.regroup=false;}
  }
  if(action==5&&frame-o.lastRegroup>=integer("status.regroupCooldownTicks")){
   o.lastRegroup=frame;
   // Never manufacture new intent for HOLD/GUARD/PATROL companions.
   for(Agent a:AGENTS.values())if(a.owner.equals(p.getUUID())){a.regroup=true;a.stage=2;a.stageSince=frame;a.stuckSince=frame;a.due=frame;}
   notice(p,o,"content.shadowsovereign.regroup_started");
  }
  sync(p,o,action==0);
 }
 @SubscribeEvent public static void commands(RegisterCommandsEvent e){
  e.getDispatcher().register(net.minecraft.commands.Commands.literal("soulboundautopilot").executes(c->{request(c.getSource().getPlayerOrException(),0);return 1;}));
 }
 @SubscribeEvent public static void tick(TickEvent.ServerTickEvent e){
  if(e.phase!=TickEvent.Phase.END)return;MinecraftServer server=e.getServer();frame=server.getTickCount();paths=0;evaluations=0;skipped=0;
  for(ServerPlayer p:server.getPlayerList().getPlayers()){
   Owner o=owner(p);o.pathCount=0;
   int autoOff=integer("buildingMode.buildAutoOffTicks");
   if(flag(o,2)&&autoOff>0&&frame-o.lastBlockAction>=autoOff)request(p,1);
   if(frame%20==Math.floorMod(p.getId(),20)){
    o.position=p.position();o.look=p.getLookAngle();
    HitResult hit=p.pick(number("buildingMode.buildReach"),0,false);o.buildingTarget=hit instanceof BlockHitResult b?b.getBlockPos():null;
    o.nearby=p.serverLevel().getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(number("combatAssist.assistRange")),Entity::isAlive).stream().limit(128).toList();
    o.atmosphere=AtmosphereServer.profile(p);
   }
   if(frame%integer("status.statusSyncTicks")==0)sync(p,o,false);
  }
  int budget=integer("performance.maxSmartUpdatesPerTick");if(enabled("performance.forcePerformanceMode"))budget=Math.max(1,budget/2);
  int scan=Math.min(QUEUE.size(),Math.max(96,budget*4));
  while(scan-->0&&!QUEUE.isEmpty()){
   UUID id=QUEUE.removeFirst();Agent a=AGENTS.get(id);if(a==null)continue;
   ServerPlayer p=server.getPlayerList().getPlayer(a.owner);
   ShadowRecord r=p==null?null:ArmyData.get(server).army(a.owner).shadows.get(a.record);
   Mob mob=r==null?null:ShadowService.active(server,r);
   if(p==null||r==null||mob==null||!mob.getUUID().equals(a.body)||frame-a.seen>200){AGENTS.remove(id);continue;}
   QUEUE.addLast(id);
   if(evaluations>=budget){skipped++;continue;}
   Owner o=owner(p);
   if(!enabled("general.enabled")||!flag(o,1)||!eligible(p,r,mob)||frame-a.seen>5){a.desired=null;a.stage=0;continue;}
   if(frame<a.due&&frame-a.evaluatedAt<integer("performance.maxStarveTicks"))continue;
   evaluations++;evaluate(p,o,r,mob,a);
  }
  int debug=integer("performance.debugLogIntervalSeconds");
  if(debug>0&&frame%(20*Math.max(5,debug))==0)org.apache.logging.log4j.LogManager.getLogger("SoulboundAutopilot").info("X4 evaluations={}, paths={}, skipped={}",evaluations,paths,skipped);
 }
 private static void evaluate(ServerPlayer p,Owner o,ShadowRecord r,Mob mob,Agent a){
  boolean combat=mob.getTarget()!=null&&mob.getTarget().isAlive();double distance=mob.distanceToSqr(p);
  a.evaluatedAt=frame;
  int interval=combat?integer("performance.combatInterval"):distance>32*32?integer("performance.farInterval"):mob.getDeltaMovement().lengthSqr()<.0025?integer("performance.idleInterval"):integer("performance.followInterval");
  if(!enabled("performance.enabled"))interval=1;
  else if(performance(o))interval=(int)Math.ceil(interval*number("performance.perfModeIntervalMultiplier"));
  a.due=frame+Math.max(1,interval);
  if(flag(o,4)&&enabled("combatAssist.enabled"))assist(p,o,r,mob,a);
  combat=mob.getTarget()!=null&&mob.getTarget().isAlive();
  Vec3 desired=a.support==null?a.base:a.support;
  if(!combat&&a.support==null&&flag(o,8)&&enabled("smartFollow.enabled")&&distance<=Math.pow(Math.max(14,number("smartFollow.followResume")),2)){
   Vec3 back=p.getLookAngle().multiply(1,0,1).normalize();Vec3 side=new Vec3(-back.z,0,back.x);
   boolean narrow=!p.level().noCollision(mob,mob.getBoundingBox().move(side.scale(1.2)))&&!p.level().noCollision(mob,mob.getBoundingBox().move(side.scale(-1.2)));
   if(narrow){a.narrow=true;a.narrowStart=p.position();}
   else if(a.narrow&&a.narrowStart.distanceToSqr(p.position())>Math.pow(integer("smartFollow.passageExitBlocks"),2))a.narrow=false;
   if(a.narrow){long rank=AGENTS.values().stream().filter(v->v.owner.equals(a.owner)&&v.record.compareTo(a.record)<0).limit(32).count();desired=p.position().subtract(back.scale(number("smartFollow.followClearance")+(rank+1)*Math.max(1,mob.getBbWidth()*number("smartFollow.sizeSpacingFactor"))));}
   else {
    Vec3 push=desired.subtract(p.position()).multiply(1,0,1);double minimum=number("smartFollow.followClearance")+mob.getBbWidth()/2;
    if(push.lengthSqr()<minimum*minimum){if(push.lengthSqr()<.01){double angle=(a.record.hashCode()&0xffff)*Math.PI/32768;push=new Vec3(Math.cos(angle),0,Math.sin(angle));}desired=p.position().add(push.normalize().scale(minimum));}
    Vec3 offset=Vec3.ZERO;int neighbours=0;
    for(Agent other:AGENTS.values())if(other!=a&&other.owner.equals(a.owner)&&other.base!=null&&other.record.compareTo(a.record)<0&&neighbours++<24){
     Vec3 delta=desired.subtract(other.desired==null?other.base:other.desired).multiply(1,0,1);double spacing=Math.max(.8,mob.getBbWidth()*number("smartFollow.sizeSpacingFactor"));
     if(delta.lengthSqr()<spacing*spacing){if(delta.lengthSqr()<.01)delta=new Vec3(1,0,0);offset=offset.add(delta.normalize().scale(spacing-Math.sqrt(delta.lengthSqr())));}
    }
    double max=number("smartFollow.maxSlotOffset");if(offset.lengthSqr()>max*max)offset=offset.normalize().scale(max);desired=desired.add(offset);
   }
  }
  if(!combat&&a.support==null&&flag(o,2)&&enabled("buildingMode.enabled")){
   double radius=number("buildingMode.buildRadius")+number("buildingMode.buildEdgeMargin")+mob.getBbWidth();
   Vec3 away=desired.subtract(p.position()).multiply(1,0,1);boolean inTarget=o.buildingTarget!=null&&new AABB(o.buildingTarget).inflate(number("buildingMode.buildTargetMargin")+mob.getBbWidth()).contains(desired);
   if(away.lengthSqr()<radius*radius||inTarget){if(away.lengthSqr()<.01)away=new Vec3(1,0,0);desired=p.position().add(away.normalize().scale(radius+number("buildingMode.buildTargetMargin")));}
  }
  if(mob.getTarget()!=null&&number("combatAssist.losConeDegrees")>0){
   Vec3 eye=p.getEyePosition(),toTarget=mob.getTarget().position().subtract(eye).normalize(),toSlot=desired.subtract(eye);
   if(toSlot.lengthSqr()<Math.pow(number("combatAssist.losConeLength"),2)&&toSlot.normalize().dot(toTarget)>Math.cos(Math.toRadians(number("combatAssist.losConeDegrees"))))desired=desired.add(-toTarget.z*(mob.getBbWidth()+1),0,toTarget.x*(mob.getBbWidth()+1));
  }
  if(!combat&&frame>=a.born+integer("general.postLoadGraceTicks")){
   boolean cached=a.safeCached!=null&&frame-a.safeAt<=integer("performance.safePosCacheTicks")&&a.safeCached.distanceToSqr(desired)<.5&&valid(mob,p.serverLevel(),a.safeCached);
   Vec3 safe=cached?a.safeCached:safe(mob,p.serverLevel(),desired,Math.min(3,integer("antiStuck.regroupSearchRadius")));
   a.safeCached=safe;if(!cached)a.safeAt=frame;
   a.desired=safe==null?a.base:safe;
  }else a.desired=null;
  if(!enabled("antiStuck.enabled")||combat||mob.isPassenger()||mob.isLeashed()||frame<a.born+integer("general.postLoadGraceTicks"))return;
  if(frame-a.lastCheck>=integer("antiStuck.stuckWindowTicks")){
   boolean moving=mob.distanceToSqr(a.desired==null?a.base:a.desired)>4;
   if(moving&&mob.position().distanceToSqr(a.progress)<Math.pow(number("antiStuck.stuckMinProgress"),2)){a.confirm++;if(a.stuckSince<0)a.stuckSince=frame;}
   else {a.confirm=0;a.attempts=0;a.stage=0;a.stuckSince=-1;a.regroup=false;}
   a.progress=mob.position();a.lastCheck=frame;
  }
  boolean separated=distance>Math.pow(number("antiStuck.separatedDistance"),2);
  if(!a.regroup&&!separated&&a.confirm<integer("antiStuck.stuckConfirmChecks"))return;
  if(frame<a.failedUntil||frame-a.lastPath<integer("antiStuck.repathCooldownTicks"))return;
  if(a.stage==0){a.stage=1;a.stageSince=frame;if(a.stuckSince<0)a.stuckSince=frame;}
  if(paths>=integer("performance.maxPathRequestsPerTick")||o.pathCount>=Math.max(1,integer("performance.maxPathRequestsPerOwnerPerTick")/(performance(o)?2:1)))return;
  Vec3 destination=a.stage==1?a.base:safe(mob,p.serverLevel(),a.base,integer("antiStuck.regroupSearchRadius"));
  paths++;o.pathCount++;a.lastPath=frame;
  if(destination!=null){Path path=mob.getNavigation().createPath(BlockPos.containing(destination),0);if(path!=null&&path.canReach()&&mob.getNavigation().moveTo(path,1.1))a.desired=destination;}
  if(a.stage==1){a.attempts++;if(a.attempts<=integer("antiStuck.repathAttempts"))return;a.stage=2;a.stageSince=frame;return;}
  if(frame-a.stageSince<integer("antiStuck.regroupTimeoutTicks"))return;
  if(enabled("antiStuck.allowEmergencyTeleport")&&frame-a.stuckSince<integer("antiStuck.teleportAfterTicks"))return;
  if(enabled("antiStuck.allowEmergencyTeleport")&&frame-a.lastTeleport>=integer("antiStuck.teleportCooldownTicks")&&p.tickCount-p.getLastHurtByMobTimestamp()>100&&mob.tickCount-mob.getLastHurtByMobTimestamp()>100){
   Vec3 at=safe(mob,p.serverLevel(),a.base,integer("antiStuck.regroupSearchRadius"));
   if(at!=null){var event=net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(mob,at.x,at.y,at.z);if(!event.isCanceled()){
    Vec3 approved=new Vec3(event.getTargetX(),event.getTargetY(),event.getTargetZ());
    if(valid(mob,p.serverLevel(),approved)){mob.teleportTo(approved.x,approved.y,approved.z);a.lastTeleport=frame;a.confirm=0;a.stage=0;return;}
   }}
  }
  a.stage=3;a.failedUntil=frame+integer("antiStuck.failedRetryTicks");notice(p,o,"content.shadowsovereign.recovery_failed");
 }
 private static Vec3 safe(Mob mob,ServerLevel level,Vec3 center,int radius){
  if(valid(mob,level,center))return center;
  int checks=0;
  for(int r=1;r<=Math.min(16,radius);r++)for(int axis=0;axis<8;axis++)for(int dy=0;dy<=integer("antiStuck.maxSafeFall");dy++){
   if(++checks>96)return null;double angle=axis*Math.PI/4;Vec3 v=new Vec3(Math.floor(center.x+Math.cos(angle)*r)+.5,Math.floor(center.y)-dy,Math.floor(center.z+Math.sin(angle)*r)+.5);
   if(valid(mob,level,v))return v;
  }return null;
 }
 private static boolean valid(Mob mob,ServerLevel level,Vec3 pos){
  BlockPos p=BlockPos.containing(pos);if(!level.hasChunkAt(p)||p.getY()<level.getMinBuildHeight()+1||pos.y+mob.getBbHeight()>=level.getMaxBuildHeight())return false;
  AABB box=mob.getBoundingBox().move(pos.subtract(mob.position()));
  boolean aquatic=mob instanceof dev.shadowsovereign.mythic.MythicBeast b&&dev.shadowsovereign.expansion.OpenMythics.isAquatic(b.kind());
  boolean flying=mob instanceof dev.shadowsovereign.mythic.MythicBeast b&&b.profile().flies();
  if(!level.getWorldBorder().isWithinBounds(box)||!level.noCollision(mob,box))return false;
  if(aquatic){if(!level.getFluidState(p).is(net.minecraft.tags.FluidTags.WATER))return false;}
  else if(level.containsAnyLiquid(box))return false;
  if(!aquatic&&!flying&&!level.getBlockState(p.below()).isFaceSturdy(level,p.below(),Direction.UP))return false;
  for(BlockPos b:BlockPos.betweenClosed(BlockPos.containing(box.minX,box.minY,box.minZ),BlockPos.containing(box.maxX,box.maxY,box.maxZ))){
   var state=level.getBlockState(b);if(state.is(Blocks.FIRE)||state.is(Blocks.SOUL_FIRE)||state.is(Blocks.CACTUS)||state.is(Blocks.MAGMA_BLOCK)||state.is(Blocks.NETHER_PORTAL)||state.is(Blocks.END_PORTAL)||state.is(Blocks.END_GATEWAY)||state.is(Blocks.SWEET_BERRY_BUSH))return false;
  }return true;
 }
 private static void assist(ServerPlayer p,Owner o,ShadowRecord r,Mob mob,Agent a){
  a.support=null;
  if(!r.aggressive||r.battle.commandedTarget!=null)return;
  LivingEntity selected=null;int priority=99;
  LivingEntity attacked=p.getLastHurtMob(),attacker=p.getLastHurtByMob();
  boolean protect=p.getHealth()/p.getMaxHealth()<number("combatAssist.protectHealthFraction")||p.tickCount-p.getLastHurtByMobTimestamp()<integer("combatAssist.protectWindowTicks");
  long closer=o.nearby.stream().filter(e->p.getUUID().equals(ShadowService.ownerId(e))&&e.distanceToSqr(p)<mob.distanceToSqr(p)).limit(integer("combatAssist.protectCount")).count();
  if(protect&&closer<integer("combatAssist.protectCount")&&attacker!=null&&validTarget(p,mob,attacker)){selected=attacker;priority=1;}
  else if(attacked!=null&&p.tickCount-p.getLastHurtMobTimestamp()<=integer("combatAssist.ownerTargetMemoryTicks")&&validTarget(p,mob,attacked)){selected=attacked;priority=2;}
  else if(attacker!=null&&validTarget(p,mob,attacker)){selected=attacker;priority=3;}
  LivingEntity current=mob.getTarget();
  if(selected==null&&validTarget(p,mob,current)){selected=current;priority=4;}
  if(selected==null){double nearest=number("combatAssist.assistRange")*number("combatAssist.assistRange");for(Mob e:o.nearby)if(e instanceof Enemy&&validTarget(p,mob,e)&&mob.distanceToSqr(e)<nearest){selected=e;nearest=mob.distanceToSqr(e);priority=5;}}
  if(selected==null)return;
  if(current!=null&&selected!=current&&validTarget(p,mob,current)&&priority>=a.priority){
   if(frame-a.lastTargetChange<integer("combatAssist.minTargetHoldTicks")||mob.distanceTo(current)-mob.distanceTo(selected)<number("combatAssist.switchDistanceAdvantage"))return;
  }
  int capacity=integer("combatAssist.maxAttackersPerTarget");long assigned=0;
  for(Agent other:AGENTS.values())if(other!=a&&other.owner.equals(a.owner)){
   ShadowRecord record=ArmyData.get(p.server).army(a.owner).shadows.get(other.record);Mob ally=record==null?null:ShadowService.active(p.server,record);
   if(ally!=null&&ally.getTarget()==selected&&(current!=selected||other.record.compareTo(a.record)<0))assigned++;
  }
  if(assigned>=capacity){Vec3 away=mob.position().subtract(selected.position()).multiply(1,0,1).normalize();if(away.lengthSqr()<.01)away=new Vec3(1,0,0);a.support=selected.position().add(away.scale(number("combatAssist.supportDistance")));if(current==selected)mob.setTarget(null);return;}
  if(current!=selected){mob.setTarget(selected);a.priority=priority;a.lastTargetChange=frame;}
 }
 @SubscribeEvent public static void leftClick(net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock e){touch(e.getEntity());}
 @SubscribeEvent public static void rightClick(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock e){touch(e.getEntity());}
 private static void touch(net.minecraft.world.entity.player.Player p){if(p instanceof ServerPlayer serverPlayer)owner(serverPlayer).lastBlockAction=frame;}
 private static boolean validTarget(ServerPlayer p,Mob mob,LivingEntity target){return target!=null&&target.isAlive()&&target.level()==mob.level()&&mob.distanceToSqr(target)<=Math.pow(number("combatAssist.dropRange"),2)&&ShadowService.canAttack(p,target);}
 private static void notice(ServerPlayer p,Owner o,String key){if(frame-o.lastNotice>=integer("status.notifyCooldownTicks")){p.displayClientMessage(Component.translatable(key),true);o.lastNotice=frame;}}
 private static void sync(ServerPlayer p,Owner o,boolean open){
  if(!enabled("status.enabled")&&!open)return;
  int active=0,stuck=0,separated=0,recovering=0,failed=0;
  for(ShadowRecord r:ArmyData.get(p.server).army(p.getUUID()).shadows.values()){
   Mob mob=ShadowService.active(p.server,r);if(mob==null||mob.level()!=p.level())continue;active++;
   if(mob.distanceToSqr(p)>Math.pow(number("antiStuck.separatedDistance"),2))separated++;
   Agent a=AGENTS.get(mob.getUUID());if(a==null)continue;if(a.confirm>=integer("antiStuck.stuckConfirmChecks"))stuck++;if(a.stage==1||a.stage==2)recovering++;if(a.stage==3)failed++;
  }
  var next=new ContentNetwork.Status(active,stuck,separated,recovering,failed,o.flags,o.atmosphere,open);
  if(open||!next.equals(o.previous)){ContentNetwork.send(p,next);o.previous=new ContentNetwork.Status(active,stuck,separated,recovering,failed,o.flags,o.atmosphere,false);}
 }
 private static void clear(UUID owner){OWNERS.remove(owner);AGENTS.values().removeIf(a->a.owner.equals(owner));QUEUE.removeIf(id->!AGENTS.containsKey(id));}
 @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent e){clear(e.getEntity().getUUID());}
 @SubscribeEvent public static void dimension(PlayerEvent.PlayerChangedDimensionEvent e){clear(e.getEntity().getUUID());}
 @SubscribeEvent public static void respawn(PlayerEvent.PlayerRespawnEvent e){clear(e.getEntity().getUUID());}
 @SubscribeEvent public static void stopped(ServerStoppedEvent e){OWNERS.clear();AGENTS.clear();QUEUE.clear();frame=0;}
}
