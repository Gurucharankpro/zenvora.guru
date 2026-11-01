package dev.shadowsovereign.content;
import net.minecraftforge.common.ForgeConfigSpec;
import java.util.*;
public final class ContentConfig {
 public static final ForgeConfigSpec SERVER,CLIENT;
 public static final Map<String,ForgeConfigSpec.ConfigValue<?>> X4=new LinkedHashMap<>();
 public enum Quality { LOW,MEDIUM,HIGH }
 public static final ForgeConfigSpec.EnumValue<Quality> QUALITY;
 public static final ForgeConfigSpec.BooleanValue ATMOSPHERE,FOG;
 static {var b=new ForgeConfigSpec.Builder();
  X4.put("general.enabled",b.define("general.enabled",true));
  X4.put("general.respectExplicitCommands",b.define("general.respectExplicitCommands",true, v -> Boolean.TRUE.equals(v)));
  X4.put("general.postLoadGraceTicks",b.defineInRange("general.postLoadGraceTicks",100,0,1200));
  X4.put("smartFollow.enabled",b.define("smartFollow.enabled",true));
  X4.put("smartFollow.followClearance",b.defineInRange("smartFollow.followClearance",2.5,1.0,8.0));
  X4.put("smartFollow.followResume",b.defineInRange("smartFollow.followResume",6.0,2.0,32.0));
  X4.put("smartFollow.sizeSpacingFactor",b.defineInRange("smartFollow.sizeSpacingFactor",1.2,1.0,3.0));
  X4.put("smartFollow.maxSlotOffset",b.defineInRange("smartFollow.maxSlotOffset",3.0,0.5,8.0));
  X4.put("smartFollow.passageExitBlocks",b.defineInRange("smartFollow.passageExitBlocks",3,1,10));
  X4.put("antiStuck.enabled",b.define("antiStuck.enabled",true));
  X4.put("antiStuck.stuckWindowTicks",b.defineInRange("antiStuck.stuckWindowTicks",60,20,400));
  X4.put("antiStuck.stuckMinProgress",b.defineInRange("antiStuck.stuckMinProgress",0.5,0.1,3.0));
  X4.put("antiStuck.stuckConfirmChecks",b.defineInRange("antiStuck.stuckConfirmChecks",2,1,6));
  X4.put("antiStuck.separatedDistance",b.defineInRange("antiStuck.separatedDistance",32.0,16.0,256.0));
  X4.put("antiStuck.repathAttempts",b.defineInRange("antiStuck.repathAttempts",2,0,6));
  X4.put("antiStuck.repathCooldownTicks",b.defineInRange("antiStuck.repathCooldownTicks",40,10,400));
  X4.put("antiStuck.regroupSearchRadius",b.defineInRange("antiStuck.regroupSearchRadius",6,2,16));
  X4.put("antiStuck.regroupTimeoutTicks",b.defineInRange("antiStuck.regroupTimeoutTicks",100,20,600));
  X4.put("antiStuck.allowEmergencyTeleport",b.define("antiStuck.allowEmergencyTeleport",false));
  X4.put("antiStuck.teleportAfterTicks",b.defineInRange("antiStuck.teleportAfterTicks",200,60,2400));
  X4.put("antiStuck.teleportCooldownTicks",b.defineInRange("antiStuck.teleportCooldownTicks",600,100,12000));
  X4.put("antiStuck.maxSafeFall",b.defineInRange("antiStuck.maxSafeFall",3,0,6));
  X4.put("antiStuck.failedRetryTicks",b.defineInRange("antiStuck.failedRetryTicks",600,100,12000));
  X4.put("buildingMode.enabled",b.define("buildingMode.enabled",true));
  X4.put("buildingMode.buildRadius",b.defineInRange("buildingMode.buildRadius",3.5,1.5,8.0));
  X4.put("buildingMode.buildReach",b.defineInRange("buildingMode.buildReach",6.0,3.0,8.0));
  X4.put("buildingMode.buildTargetMargin",b.defineInRange("buildingMode.buildTargetMargin",2.0,0.0,5.0));
  X4.put("buildingMode.buildEdgeMargin",b.defineInRange("buildingMode.buildEdgeMargin",1.0,0.0,4.0));
  X4.put("buildingMode.buildAutoOffTicks",b.defineInRange("buildingMode.buildAutoOffTicks",0,200,72000));
  X4.put("combatAssist.enabled",b.define("combatAssist.enabled",true));
  X4.put("combatAssist.assistRange",b.defineInRange("combatAssist.assistRange",16.0,4.0,48.0));
  X4.put("combatAssist.dropRange",b.defineInRange("combatAssist.dropRange",24.0,8.0,64.0));
  X4.put("combatAssist.ownerTargetMemoryTicks",b.defineInRange("combatAssist.ownerTargetMemoryTicks",100,20,600));
  X4.put("combatAssist.minTargetHoldTicks",b.defineInRange("combatAssist.minTargetHoldTicks",40,0,400));
  X4.put("combatAssist.switchDistanceAdvantage",b.defineInRange("combatAssist.switchDistanceAdvantage",3.0,0.0,16.0));
  X4.put("combatAssist.maxAttackersPerTarget",b.defineInRange("combatAssist.maxAttackersPerTarget",6,1,16));
  X4.put("combatAssist.supportDistance",b.defineInRange("combatAssist.supportDistance",4.0,2.0,10.0));
  X4.put("combatAssist.losConeDegrees",b.defineInRange("combatAssist.losConeDegrees",20,0,45));
  X4.put("combatAssist.losConeLength",b.defineInRange("combatAssist.losConeLength",12.0,4.0,24.0));
  X4.put("combatAssist.protectHealthFraction",b.defineInRange("combatAssist.protectHealthFraction",0.4,0.0,1.0));
  X4.put("combatAssist.protectWindowTicks",b.defineInRange("combatAssist.protectWindowTicks",60,0,400));
  X4.put("combatAssist.protectCount",b.defineInRange("combatAssist.protectCount",4,0,16));
  X4.put("performance.enabled",b.define("performance.enabled",true));
  X4.put("performance.combatInterval",b.defineInRange("performance.combatInterval",2,1,20));
  X4.put("performance.followInterval",b.defineInRange("performance.followInterval",5,2,40));
  X4.put("performance.idleInterval",b.defineInRange("performance.idleInterval",20,5,100));
  X4.put("performance.farInterval",b.defineInRange("performance.farInterval",40,10,200));
  X4.put("performance.maxSmartUpdatesPerTick",b.defineInRange("performance.maxSmartUpdatesPerTick",24,1,512));
  X4.put("performance.maxPathRequestsPerTick",b.defineInRange("performance.maxPathRequestsPerTick",4,1,64));
  X4.put("performance.maxPathRequestsPerOwnerPerTick",b.defineInRange("performance.maxPathRequestsPerOwnerPerTick",2,1,16));
  X4.put("performance.maxStarveTicks",b.defineInRange("performance.maxStarveTicks",200,40,1200));
  X4.put("performance.safePosCacheTicks",b.defineInRange("performance.safePosCacheTicks",20,0,200));
  X4.put("performance.perfModeIntervalMultiplier",b.defineInRange("performance.perfModeIntervalMultiplier",2.0,1.0,8.0));
  X4.put("performance.forcePerformanceMode",b.define("performance.forcePerformanceMode",false));
  X4.put("performance.debugLogIntervalSeconds",b.defineInRange("performance.debugLogIntervalSeconds",0,0,300));
  X4.put("status.enabled",b.define("status.enabled",true));
  X4.put("status.statusSyncTicks",b.defineInRange("status.statusSyncTicks",20,5,100));
  X4.put("status.notifyCooldownTicks",b.defineInRange("status.notifyCooldownTicks",100,20,1200));
  X4.put("status.regroupCooldownTicks",b.defineInRange("status.regroupCooldownTicks",40,10,400));
  X4.put("status.regroupIgnoresHold",b.define("status.regroupIgnoresHold",false, v -> Boolean.FALSE.equals(v)));
  X4.put("multiplayer.requestMinIntervalTicks",b.defineInRange("multiplayer.requestMinIntervalTicks",5,1,40));
  X4.put("multiplayer.teamShareAutopilot",b.define("multiplayer.teamShareAutopilot",false, v -> Boolean.FALSE.equals(v)));
  SERVER=b.build();b=new ForgeConfigSpec.Builder();
  QUALITY=b.defineEnum("quality",Quality.LOW);ATMOSPHERE=b.define("enabled",true);FOG=b.define("fog",true);CLIENT=b.build();
 }
 public static boolean flag(String key){return Boolean.TRUE.equals(X4.get(key).get());}
 public static int integer(String key){return ((Number)X4.get(key).get()).intValue();}
 public static double number(String key){return ((Number)X4.get(key).get()).doubleValue();}
}
