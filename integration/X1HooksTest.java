import dev.shadowsovereign.expansion.X1Hooks;
import dev.shadowsovereign.army.*;
import dev.shadowsovereign.soulforge.*;
import dev.shadowsovereign.mythic.*;
import net.minecraft.server.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
public class X1HooksTest {
 static int checks;
 static void check(boolean b,String why){if(!b)throw new AssertionError(why);checks++;}
 static boolean last(MinecraftServer s,String id){return s.commands.calls.get(s.commands.calls.size()-1).endsWith("/"+id);}
 public static void main(String[] args){
  var server=new MinecraftServer();var world=new ServerLevel();server.levels.add(world);
  var player=new ServerPlayer();player.f_8924_=server;player.server=server;player.level=world;
  var r=new ShadowRecord();var body=new Mob();body.server=server;body.level=world;r.body=body;
  X1Hooks.summoned(player,r);check(last(server,"02"),"ordinary summon");
  r.tier=SoulTier.LEGENDARY;X1Hooks.summoned(player,r);check(server.commands.calls.stream().anyMatch(c->c.endsWith("/03")),"legendary summon");
  r.tier=SoulTier.EPIC;X1Hooks.summoned(player,r);check(last(server,"08"),"finite elite aura burst");
  r.tier=SoulTier.CHOSEN;X1Hooks.summoned(player,r);check(last(server,"09"),"finite high-tier aura burst");
  var beast=new MythicBeast();beast.server=server;beast.level=world;r.body=beast;r.tier=SoulTier.COMMON;
  X1Hooks.summoned(player,r);check(last(server,"14"),"mythic awakening");
  X1Hooks.acquired(player);check(last(server,"01"),"extraction success");
  X1Hooks.recall(body,14);check(last(server,"04"),"recall event");
  X1Hooks.command(player,r,Order.FOLLOW);check(last(server,"06"),"follow rally");
  X1Hooks.command(player,r,Order.STAY);check(last(server,"07"),"confirmed command");
  int before=server.commands.calls.size();r.bond=29;X1Hooks.bond(player,r,0);check(server.commands.calls.size()==before,"no false rank promotion");
  r.bond=30;X1Hooks.bond(player,r,0);check(last(server,"05"),"confirmed bond-rank increase");
  beast.data.m_128362_("ShadowSovereignOwner",player.id);
  X1Hooks.attack(beast,new MythicProfiles.Move("guard",1,5,0,0));check(last(server,"13"),"existing shield presentation");
  X1Hooks.attack(beast,new MythicProfiles.Move("slash",1,0,3,1));check(last(server,"11_slash_diagonal"),"weapon strike presentation");
  int noImpact=server.commands.calls.size();X1Hooks.attack(beast,new MythicProfiles.Move("magic",1,6,12,1));check(server.commands.calls.size()==noImpact,"magic impact waits for confirmed damage event");
  var damage=new net.minecraftforge.event.entity.living.LivingDamageEvent();damage.victim=body;damage.source=new net.minecraft.world.damagesource.DamageSource();damage.source.caster=beast;damage.source.direct=beast;damage.amount=3;
  X1Hooks.damage(damage);check(last(server,"12"),"post-armor impact at victim");
  noImpact=server.commands.calls.size();damage.canceled=true;X1Hooks.damage(damage);check(server.commands.calls.size()==noImpact,"canceled hit has no impact");damage.canceled=false;damage.amount=0;X1Hooks.damage(damage);check(server.commands.calls.size()==noImpact,"zero damage has no impact");
  before=server.commands.calls.size();world.f_46443_=true;X1Hooks.emit(body,2);check(server.commands.calls.size()==before,"client duplicate suppressed");world.f_46443_=false;
  body.x=Double.NaN;X1Hooks.emit(body,2);check(server.commands.calls.size()==before,"invalid coordinate rejected");body.x=0;
  X1Hooks.emit(body,0);X1Hooks.emit(null,2);check(server.commands.calls.size()==before,"invalid event rejected");
  X1Hooks.tick(server);check(server.commands.calls.get(server.commands.calls.size()-1).contains("tick_world"),"all loaded dimensions tick");
  server.commands.fail=true;X1Hooks.emit(body,2);check(true,"visual command failures isolated");
  System.out.println("PASS: "+checks+" X1 visual-routing assertions using explicit command/world fixtures; no Forge execution.");
 }
}
