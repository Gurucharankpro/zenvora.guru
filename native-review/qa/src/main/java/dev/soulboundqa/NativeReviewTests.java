package dev.soulboundqa;
import dev.shadowsovereign.army.*;
import dev.shadowsovereign.adventure.*;
import dev.shadowsovereign.expansion.*;
import dev.shadowsovereign.mythic.*;
import dev.shadowsovereign.odyssey.*;
import dev.shadowsovereign.gloom.*;
import dev.shadowsovereign.realm.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.minecraftforge.gametest.*;
import java.util.*;
@GameTestHolder("soulboundqa") @PrefixGameTestTemplate(false)
public final class NativeReviewTests {
 static void fixture(GameTestHelper h,java.util.function.Consumer<ServerPlayer> action){
  var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);new io.netty.channel.embedded.EmbeddedChannel(connection);
  var p=new ServerPlayer(h.getLevel().getServer(),h.getLevel(),new com.mojang.authlib.GameProfile(UUID.randomUUID(),"sb181-test"));p.server.getPlayerList().placeNewPlayer(connection,p);p.setInvulnerable(true);p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);GuideService.army(p).autoRecruit=GuideService.army(p).autoSummon=false;
  h.assertTrue(OdysseyRealm.assign(p),"Owner fixture assigned");GuideService.state(p).plotReady=true;GuideService.state(p).camp.putInt("AstralStairs",48);LivingRealm.state(p).ready=true;GloomRealm.state(p).putBoolean("Blocked",true);dev.shadowsovereign.monolith.MonolithWorld.state(p).putBoolean("Suspended",true);MythicService.state(p).generated=true;
  var at=OdysseyRealm.home(p).above();var w=p.server.getLevel(OdysseyRealm.KEY);
  for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)w.setChunkForced((at.getX()>>4)+x,(at.getZ()>>4)+z,true);
  p.teleportTo(w,at.getX()+.5,at.getY(),at.getZ()+.5,0,0);
  for(int x=-16;x<=20;x++)for(int z=-16;z<=16;z++){w.setBlock(at.offset(x,-1,z),Blocks.STONE.defaultBlockState(),2);for(int y=0;y<8;y++)w.setBlock(at.offset(x,y,z),Blocks.AIR.defaultBlockState(),2);}
  h.runAfterDelay(35,()->action.accept(p));
 }
 static void finish(GameTestHelper h,ServerPlayer p){OpenMythics.pause(p.server,p.getUUID());ShadowService.recallAll(p);var at=OdysseyRealm.home(p);for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)p.serverLevel().setChunkForced((at.getX()>>4)+x,(at.getZ()>>4)+z,false);p.server.getPlayerList().remove(p);p.discard();h.succeed();}
 static void site(ServerPlayer p,int kind,BlockPos at){var n=kind<0?MythicService.state(p).gloom:MythicExpedition.state(p,kind);n.putLong("NaturalAltar",at.asLong());n.putIntArray("NaturalBounds",new int[]{at.getX()-10,at.getY()-1,at.getZ()-10,at.getX()+10,at.getY()+8,at.getZ()+10});if(kind<0)n.putLong("NaturalDevourer",at.below().asLong());else n.putLong("Lair",at.asLong());}
 @GameTest(templateNamespace="soulboundqa",template="arena",timeoutTicks=1600) public static void allLearningLessonsDecodeAndRealPlacedBlockCriteriaRespectBlockAndDimension(GameTestHelper h){fixture(h,p->{var manager=p.server.getAdvancements();long count=manager.getAllAdvancements().stream().filter(a->a.getId().getNamespace().equals("shadowsovereign")&&a.getId().getPath().startsWith("learning/")).count();h.assertTrue(count==500,"All500 lessons accepted by actual Minecraft advancement parser; got"+count);
  var advancement=manager.getAdvancement(new ResourceLocation("shadowsovereign:learning/place_abyss_abyss_reed"));h.assertTrue(advancement!=null,"Formerly invalid lesson is loaded");var block=net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValue(new ResourceLocation("shadowsovereign:abyss_abyss_reed"));var at=p.blockPosition().offset(3,0,0);
  p.level().setBlock(at,Blocks.STONE.defaultBlockState(),2);net.minecraft.advancements.CriteriaTriggers.PLACED_BLOCK.trigger(p,at,new ItemStack(Items.STONE));h.assertFalse(p.getAdvancements().getOrStartProgress(advancement).isDone(),"Wrong placed block never earns target lesson");
  var old=p.position();var ow=p.server.overworld();var over=new BlockPos(16,100,16);ow.getChunkAt(over);p.teleportTo(ow,16.5,101,16.5,0,0);ow.setBlock(over,block.defaultBlockState(),2);net.minecraft.advancements.CriteriaTriggers.PLACED_BLOCK.trigger(p,over,new ItemStack(block));h.assertFalse(p.getAdvancements().getOrStartProgress(advancement).isDone(),"Correct block in wrong dimension earns nothing");
  p.teleportTo(p.server.getLevel(OdysseyRealm.KEY),old.x,old.y,old.z,0,0);p.level().setBlock(at,block.defaultBlockState(),2);net.minecraft.advancements.CriteriaTriggers.PLACED_BLOCK.trigger(p,at,new ItemStack(block));h.assertTrue(p.getAdvancements().getOrStartProgress(advancement).isDone(),"Real native placement trigger grants correct block in Echo Realms");
  int xp=p.totalExperience;net.minecraft.advancements.CriteriaTriggers.PLACED_BLOCK.trigger(p,at,new ItemStack(block));h.assertTrue(p.totalExperience==xp,"No duplicated XP reward on another placement");finish(h,p);
 });}
 @GameTest(templateNamespace="soulboundqa",template="arena",timeoutTicks=1600) public static void retreatActuallyDiscardsRestoresHealthUnloadsSafelyAndKeepsArmyFree(GameTestHelper h){fixture(h,p->{var at=p.blockPosition();int first=MythicExpedition.IDS.length-1;site(p,first,at);h.assertTrue(MythicExpedition.start(p,first),"Actual latest guardian starts");var boss=MythicExpedition.body(p,first);h.assertTrue(boss!=null&&AdventureService.journal(p).session==null,"Open encounter uses no exclusive adventure lock");boss.setNoAi(true);boss.setHealth(20);var stale=AdventureWorld.snapshot(boss);var oldUUID=boss.getUUID();
  var cow=EntityType.COW.create(p.serverLevel());var record=new ShadowRecord(UUID.randomUUID(),"minecraft:cow","Review cow",ShadowService.snapshot(cow,true),cow.getMaxHealth());GuideService.army(p).shadows.put(record.id,record);cow.discard();h.assertTrue(ShadowService.summon(p,record,true),"Unrelated reserve soul summons during hosted fight");
  var breakAt=at.offset(7,0,0);p.level().setBlock(breakAt,Blocks.COBBLESTONE.defaultBlockState(),2);p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);h.assertTrue(p.gameMode.destroyBlock(breakAt),"Actual block breaking allowed during fight");p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
  p.teleportTo(at.getX()+14.5,at.getY(),at.getZ()+.5);OpenMythics.tick(p);h.assertTrue(boss.isRemoved()&&!MythicExpedition.state(p,first).hasUUID("Active")&&!MythicExpedition.state(p,first).contains("Snapshot")&&AdventureService.journal(p).session==null,"Actual boundary retreat frees body, UUID, damage and session");
  site(p,first-1,p.blockPosition());h.assertTrue(MythicExpedition.start(p,first-1),"Different guardian starts immediately after retreat");OpenMythics.pause(p.server,p.getUUID());p.teleportTo(at.getX()+.5,at.getY(),at.getZ()+.5);h.assertTrue(MythicExpedition.start(p,first),"Re-entry starts again");var again=MythicExpedition.body(p,first);h.assertTrue(again.getHealth()==again.getMaxHealth()&&!again.getUUID().equals(oldUUID),"Fresh canonical guardian at full actual maximum health");
  var duplicate=MythicContent.BEASTS.get(first).get().create(p.serverLevel());duplicate.load(stale);h.assertFalse(p.serverLevel().addFreshEntity(duplicate),"Late unloaded old body rejected, cannot duplicate");
  OpenMythics.pause(p.server,p.getUUID());site(p,-1,at);h.assertTrue(MythicService.start(p),"Devourer starts using its existing entitlement");var devourer=MythicService.body(p);devourer.setHealth(10);p.teleportTo(at.getX()+14.5,at.getY(),at.getZ()+.5);OpenMythics.tick(p);h.assertTrue(devourer.isRemoved()&&MythicService.state(p).active==null&&MythicService.state(p).snapshot.isEmpty(),"Devourer retreat resets its separate ledger too");finish(h,p);
 });}
 @GameTest(templateNamespace="soulboundqa",template="arena",timeoutTicks=1800) public static void actualCosmeticFunctionsAnimateExpireAndDoNotRemoveUnrelatedDisplays(GameTestHelper h){fixture(h,p->{var w=p.serverLevel();var unrelated=new net.minecraft.world.entity.Display.ItemDisplay(EntityType.ITEM_DISPLAY,w);unrelated.moveTo(p.position().add(2,0,0));w.addFreshEntity(unrelated);for(int effect=1;effect<=15;effect++)X1Hooks.emit(p,effect);
  var box=new AABB(p.blockPosition()).inflate(12);var fx=w.getEntitiesOfClass(net.minecraft.world.entity.Display.ItemDisplay.class,box,e->e.getTags().contains("sb_x1_fx"));h.assertTrue(!fx.isEmpty()&&fx.size()<=24,"Real event functions create finite tagged displays within cap");h.runAfterDelay(150,()->{h.assertTrue(w.getEntitiesOfClass(net.minecraft.world.entity.Display.ItemDisplay.class,box,e->e.getTags().contains("sb_x1_fx")).isEmpty(),"Actual server tick hooks expire all effects");h.assertTrue(unrelated.isAlive(),"Cleanup preserves unrelated display");unrelated.discard();finish(h,p);});
 });}
 @GameTest(templateNamespace="soulboundqa",template="arena",timeoutTicks=400) public static void sharedHabitatPlacementRetainsEveryLegendAndEnforcesCrossTypeSpacing(GameTestHelper h){var registry=h.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE_SET);var set=registry.get(new ResourceLocation("shadowsovereign:reviewed_mythic_habitats"));h.assertTrue(set!=null&&set.structures().size()==MythicExpedition.IDS.length+5,"Every surviving legend and all four desert landmarks share native placement");var placement=(net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement)set.placement();for(var row:set.structures())h.assertTrue(registry.stream().flatMap(s->s.structures().stream()).filter(v->v.structure().equals(row.structure())).count()==1,"No independent duplicate placement set");for(long seed:new long[]{0,-7,987654321})for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++){var a=placement.getPotentialStructureChunk(seed,x*48,z*48);var b=placement.getPotentialStructureChunk(seed,(x+1)*48,z*48);h.assertTrue(Math.abs(a.x-b.x)>=33,"Native candidates separated across negative/positive coordinates");}h.succeed();}
}
