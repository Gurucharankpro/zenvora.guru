package dev.soulboundqa;
import dev.shadowsovereign.army.*;
import dev.shadowsovereign.adventure.*;
import dev.shadowsovereign.client.*;
import dev.shadowsovereign.expansion.*;
import dev.shadowsovereign.mythic.*;
import dev.shadowsovereign.network.*;
import dev.shadowsovereign.odyssey.*;
import dev.shadowsovereign.gloom.*;
import dev.shadowsovereign.realm.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import java.util.*;
import java.util.concurrent.CompletableFuture;
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid="soulboundqa",value=net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class ClientReviewSmoke {
 static int ticks,stage,last,wait,retries;static boolean ready;static String save;static CompletableFuture<?> work;static UUID soul;static int kind;
 static void check(boolean ok,String why){if(!ok)throw new IllegalStateException("181 NATIVE CLIENT FAILED:"+why);}
 static void pressH(Minecraft m){try{var method=net.minecraftforge.fml.util.ObfuscationReflectionHelper.findMethod(net.minecraft.client.KeyboardHandler.class,"m_90893_",long.class,int.class,int.class,int.class,int.class);method.invoke(m.keyboardHandler,m.getWindow().getWindow(),72,0,1,0);method.invoke(m.keyboardHandler,m.getWindow().getWindow(),72,0,0,0);}catch(Exception e){throw new RuntimeException(e);}}
 static void next(int n){stage=n;last=ticks;wait=0;}
 static void job(Minecraft m,java.util.function.Consumer<ServerPlayer> f){work=m.getSingleplayerServer().submit(()->f.accept(m.getSingleplayerServer().getPlayerList().getPlayer(m.player.getUUID())));}
 static void shot(Minecraft m,String name){try(var png=Screenshot.takeScreenshot(m.getMainRenderTarget())){var dir=java.nio.file.Path.of(System.getProperty("soulboundqa.screenshots","screenshots"));java.nio.file.Files.createDirectories(dir);png.writeToFile(dir.resolve(name+".png"));}catch(Exception e){throw new RuntimeException(e);}}
 @SubscribeEvent public static void tick(net.minecraftforge.event.TickEvent.ClientTickEvent e){if(!Boolean.getBoolean("soulboundqa.smoke")||e.phase!=net.minecraftforge.event.TickEvent.Phase.END)return;var m=Minecraft.getInstance();ticks++;
  if(work!=null){if(!work.isDone())return;work.join();work=null;}
  if(m.player==null&&m.screen!=null){for(var child:m.screen.children())if(child instanceof net.minecraft.client.gui.components.Button button&&(button.getMessage().getString().equals("Proceed")||button.getMessage().getString().equals("Continue"))){button.onPress();return;}}
  if(ticks>20000)throw new IllegalStateException("181 CLIENT timeout stage"+stage);
  if(stage>0&&stage<20&&(m.player==null||m.level==null))return;
  if(ticks-last<35)return;
  switch(stage){
   case 0->{if(!System.getProperty("soulboundqa.resume","").isEmpty()&&m.screen instanceof TitleScreen&&m.getOverlay()==null){save=System.getProperty("soulboundqa.resume");next(1);m.createWorldOpenFlows().loadLevel(new TitleScreen(),save);return;}if(m.getOverlay()!=null||m.screen==null)return;if(!(m.screen instanceof TitleScreen)){for(var child:m.screen.children())if(child instanceof net.minecraft.client.gui.components.Button b&&b.getMessage().getString().equals("Continue")){b.onPress();break;}return;}m.options.pauseOnLostFocus=false;m.options.renderDistance().set(5);m.options.simulationDistance().set(5);m.options.framerateLimit().set(30);m.options.guiScale().set(2);m.resizeDisplay();save="soulbound-181-reviewed-"+UUID.randomUUID();var rules=new GameRules();rules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false,null);var settings=new LevelSettings("Soulbound1.8.1 native review",GameType.CREATIVE,false,Difficulty.NORMAL,true,rules,WorldDataConfiguration.DEFAULT);next(1);m.createWorldOpenFlows().createFreshLevel(save,settings,new WorldOptions(181,true,false),a->a.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions());}
   case 1->{m.setScreen(null);job(m,p->{p.setInvulnerable(true);GuideService.army(p).autoRecruit=GuideService.army(p).autoSummon=false;check(OdysseyRealm.assign(p),"Native owner allocation");GuideService.state(p).plotReady=true;GuideService.state(p).camp.putInt("AstralStairs",48);LivingRealm.state(p).ready=true;GloomRealm.state(p).putBoolean("Blocked",true);dev.shadowsovereign.monolith.MonolithWorld.state(p).putBoolean("Suspended",true);MythicService.state(p).generated=true;var at=OdysseyRealm.home(p).above();var w=p.server.getLevel(OdysseyRealm.KEY);for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){w.getChunk((at.getX()>>4)+x,(at.getZ()>>4)+z);w.setChunkForced((at.getX()>>4)+x,(at.getZ()>>4)+z,true);}for(int x=-14;x<=14;x++)for(int z=-14;z<=14;z++){w.setBlock(at.offset(x,-1,z),Blocks.STONE.defaultBlockState(),2);for(int y=0;y<7;y++)w.setBlock(at.offset(x,y,z),Blocks.AIR.defaultBlockState(),2);}p.teleportTo(w,at.getX()+.5,at.getY(),at.getZ()+.5,0,5);ShadowNetwork.sendArmy(p,false);});next(2);}
   case 2->{if(!m.level.dimension().equals(OdysseyRealm.KEY)||m.screen!=null)return;pressH(m);next(3);}
   case 3->{check(m.screen!=null,"Actual H mapping opens menu");shot(m,"h-army");m.setScreen(null);next(30);}
   case 30->{m.setScreen(null);job(m,p->{var at=p.blockPosition().south(4);ready=p.serverLevel().isPositionEntityTicking(at)&&p.serverLevel().areEntitiesLoaded(net.minecraft.world.level.ChunkPos.asLong(at));});next(31);}
   case 31->{if(!ready){check(++retries<80,"Native chunks become entity-ready after the asynchronous H response");next(30);return;}job(m,p->{kind=MythicExpedition.IDS.length-1;var at=p.blockPosition().south(4);NativeReviewTests.site(p,kind,at);boolean started=MythicExpedition.start(p,kind);if(!started){var probe=MythicContent.BEASTS.get(kind).get().create(p.serverLevel());boolean fits=probe!=null&&MythicSpawn.place(p.serverLevel(),probe,at,MythicSites.bounds(MythicExpedition.state(p,kind),at));String diag="fits="+fits+" ground="+p.level().getBlockState(at.below())+" alive="+p.isAlive()+" spectator="+p.isSpectator()+" difficulty="+p.level().getDifficulty()+" entities="+p.serverLevel().areEntitiesLoaded(net.minecraft.world.level.ChunkPos.asLong(at))+" tick="+p.serverLevel().isPositionEntityTicking(at)+" box="+(probe==null?"null":probe.getBoundingBox())+" state="+MythicExpedition.state(p,kind);if(probe!=null)probe.discard();check(false,diag);}var body=MythicExpedition.body(p,kind);body.setNoAi(true);body.animate(0);check(body.hurt(p.damageSources().playerAttack(p),1000000),"Real owner attack reaches latest guardian");var n=MythicExpedition.state(p,kind);check(n.getInt("Status")==2&&n.hasUUID("Soul"),"Actual death grants owned soul and collection page");soul=n.getUUID("Soul");check(GuideService.army(p).shadows.containsKey(soul)&&!OpenMythics.defeated(body,p.getUUID()),"One soul only, no repeated award");MythicNetwork.openGrimoire(p);});next(4);}
   case 4->{check(m.screen instanceof MythicGrimoireScreen,"Real earned book opens from server packet");((MythicGrimoireScreen)m.screen).selectPage(kind+1);next(5);}
   case 5->{shot(m,"earned-latest-legend");MythicNetwork.request(MythicNetwork.Action.SUMMON,kind+1);next(6);}
   case 6->{job(m,p->{var r=GuideService.army(p).shadows.get(soul);var body=ShadowService.active(p.server,r);check(body!=null&&body.isAlive(),"Real book network action summons earned latest companion");body.setNoAi(true);body.moveTo(p.position().add(0,0,5));p.setYRot(0);X1Hooks.emit(body,14);});m.setScreen(null);next(7);}
   case 7->{shot(m,"world-companion-and-fx");m.setScreen(new Gallery());next(8);}
   case 8->{shot(m,"latest-native-models");m.setScreen(null);job(m,p->{p.server.saveEverything(false,true,true);check(GuideService.army(p).shadows.get(soul).type.equals("shadowsovereign:"+MythicExpedition.IDS[kind]),"Correct saved companion identity");});m.setScreen(new IconGallery());next(80);}
   case 80->{shot(m,"unique-eggs-and-journey-badges");m.setScreen(null);next(9);}
   case 9->{m.level.disconnect();m.clearLevel();m.setScreen(new TitleScreen());next(20);}
   case 20->{if(m.getSingleplayerServer()!=null||m.getOverlay()!=null)return;m.createWorldOpenFlows().loadLevel(new TitleScreen(),save);next(21);}
   case 21->{if(m.player==null||m.getSingleplayerServer()==null)return;m.setScreen(null);job(m,p->{var r=GuideService.army(p).shadows.get(soul);check(r!=null&&r.type.equals("shadowsovereign:"+MythicExpedition.IDS[kind])&&MythicExpedition.state(p,kind).getInt("Status")==2,"Actual save/close/reopen retains earned soul and collection");long types=GuideService.army(p).shadows.values().stream().filter(v->v.type.equals("shadowsovereign:"+MythicExpedition.IDS[kind])).count();check(types==1,"No duplicate after true reload");});next(22);}
   case 22->{org.slf4j.LoggerFactory.getLogger("Soulbound181QA").info("181 NATIVE CLIENT PASS:actual H menu, real latest guardian kill/one-time capture, earned3D book, real summon packet, world companion/effects, three latest legends and Prism Jerboa, actual save-close-reopen retaining soul without duplication");m.level.disconnect();m.clearLevel();m.stop();next(99);}
  }
 }
 static final class IconGallery extends Screen {
  IconGallery(){super(Component.literal("Updated icons"));}
  @Override public boolean isPauseScreen(){return false;}
  @Override public void render(GuiGraphics g,int mx,int my,float partial){
   g.fill(0,0,width,height,0xFF0D1020);g.drawCenteredString(font,"Unique eggs and journey badges",width/2,12,0xFFE7D5AF);
   for(int i=0;i<20;i++){
    String id=i<4?new String[]{"dr_salmawy_spawn_egg","raven_seamstress_spawn_egg","clockwork_widow_spawn_egg","prism_jerboa_spawn_egg"}[i]:"x1_visual";
    Item item=net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation("shadowsovereign",id));check(item!=null,"Registered icon item "+id);ItemStack stack=new ItemStack(item);
    if(i>=4)stack.getOrCreateTag().putInt("CustomModelData",10000+i-4);
    g.pose().pushPose();g.pose().translate(width/2-200+(i%10)*40,45+(i/10)*65,0);g.pose().scale(2,2,2);g.renderItem(stack,0,0);g.pose().popPose();
   }
   super.render(g,mx,my,partial);
  }
 }
 static final class Gallery extends Screen {
  final java.util.List<Mob> mobs=new ArrayList<>();Gallery(){super(Component.literal("Latest legends"));}
  @Override protected void init(){for(int i=MythicExpedition.IDS.length-3;i<MythicExpedition.IDS.length;i++){var mob=MythicContent.BEASTS.get(i).get().create(minecraft.level);check(mob!=null,"Latest native model body");mob.setOnGround(true);mobs.add(mob);}var fauna=net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValue(new net.minecraft.resources.ResourceLocation("shadowsovereign:prism_jerboa")).create(minecraft.level);check(fauna instanceof Mob,"Prism Jerboa render fixture");mobs.add((Mob)fauna);}
  @Override public boolean isPauseScreen(){return false;}
  @Override public void tick(){mobs.forEach(m->m.tickCount++);}
  @Override public void render(GuiGraphics g,int mx,int my,float partial){g.fill(0,0,width,height,0xFF0D1020);g.drawCenteredString(font,"SOULBOUND1.8.1 · NATIVE MODEL CHECK",width/2,15,0xFFE7D5AF);for(int i=0;i<mobs.size();i++){var mob=mobs.get(i);int w=width/2,x=w*(i%2)+w/2,y=height/2*(i/2)+height/2-35;var model=(DevourerModel<?>)((net.minecraft.client.renderer.entity.LivingEntityRenderer<?,?>)minecraft.getEntityRenderDispatcher().getRenderer(mob)).getModel();int scale=(int)Math.max(1,.8*Math.min((height/2-65)/model.previewHeight,(w-16)/model.previewWidth));InventoryScreen.renderEntityInInventoryFollowsMouse(g,x,y,scale,0,0,mob);g.drawCenteredString(font,mob.getType().getDescription(),x,y+10,0xFFE2D1B2);}super.render(g,mx,my,partial);}
 }
}
