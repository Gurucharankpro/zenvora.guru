"""Compile the runtime helper against explicit ABI fixtures; never package them.

Fixtures allow deterministic JVM regression tests without claiming Forge startup.
Only the three production helper classes are copied to the resource overlay.
"""
import argparse,json,subprocess
from pathlib import Path

SOURCES={
'net/minecraft/nbt/Tag': 'public interface Tag {default String m_7916_(){return toString();}}',
'net/minecraft/nbt/ListTag': 'public class ListTag extends java.util.ArrayList<Tag> implements Tag {}',
'net/minecraft/nbt/StringTag': 'public class StringTag implements Tag {private final String value;public StringTag(String v){value=v;}public String m_7916_(){return value;}}',
'net/minecraft/nbt/CompoundTag': '''public class CompoundTag implements Tag {
 public java.util.Map<String,Object> values=new java.util.HashMap<>();
 public boolean m_128441_(String k){return values.containsKey(k);}
 public boolean m_128403_(String k){return values.get(k) instanceof java.util.UUID;}
 public boolean m_128471_(String k){return Boolean.TRUE.equals(values.get(k));}
 public int m_128451_(String k){return ((Number)values.getOrDefault(k,0)).intValue();}
 public void m_128405_(String k,int v){values.put(k,v);}
 public void m_128379_(String k,boolean v){values.put(k,v);}
 public void m_128362_(String k,java.util.UUID v){values.put(k,v);}
 public java.util.UUID m_128342_(String k){return (java.util.UUID)values.get(k);}
 public CompoundTag m_128469_(String k){return values.get(k) instanceof CompoundTag n?n:new CompoundTag();}
 public ListTag m_128437_(String k,int type){return values.get(k) instanceof ListTag n?n:new ListTag();}
 public String m_128461_(String k){return String.valueOf(values.getOrDefault(k,""));}
 public void m_128359_(String k,String v){values.put(k,v);}
 public void m_128365_(String k,Tag v){values.put(k,v);}
 public boolean m_128425_(String k,int type){return values.get(k) instanceof CompoundTag;}
 public void m_128473_(String k){values.remove(k);}
}''',
'net/minecraft/core/BlockPos':'public class BlockPos { public int x,y,z; public BlockPos(int x,int y,int z){this.x=x;this.y=y;this.z=z;} }',
'net/minecraft/network/chat/Component':'public class Component { public String text="test"; }',
'net/minecraft/world/Difficulty':'public enum Difficulty { PEACEFUL,EASY,NORMAL,HARD }',
'net/minecraft/world/BossEvent':'public class BossEvent { public enum BossBarColor {PURPLE,BLUE} public enum BossBarOverlay {PROGRESS} }',
'net/minecraft/world/level/Level':'''public class Level {public boolean f_46443_; public net.minecraft.world.Difficulty difficulty=net.minecraft.world.Difficulty.NORMAL; public net.minecraft.world.Difficulty m_46791_(){return difficulty;}}''',
'net/minecraft/server/players/PlayerList':'''public class PlayerList {public java.util.Map<java.util.UUID,net.minecraft.server.level.ServerPlayer> players=new java.util.HashMap<>();public net.minecraft.server.level.ServerPlayer m_11259_(java.util.UUID id){return players.get(id);}}''',
'net/minecraft/server/MinecraftServer':'''public class MinecraftServer {public net.minecraft.server.players.PlayerList players=new net.minecraft.server.players.PlayerList();public net.minecraft.server.players.PlayerList m_6846_(){return players;}}''',
'net/minecraft/server/level/ServerLevel':'public class ServerLevel extends net.minecraft.world.level.Level {}',
'net/minecraft/world/entity/Entity':'''public class Entity {
 public int f_19797_;public java.util.UUID id=java.util.UUID.randomUUID();public net.minecraft.server.MinecraftServer server;
 public net.minecraft.world.level.Level level;public boolean alive=true,removed=false;public double x,z;
 public net.minecraft.nbt.CompoundTag data=new net.minecraft.nbt.CompoundTag();
 public java.util.UUID m_20148_(){return id;}public net.minecraft.server.MinecraftServer m_20194_(){return server;}
 public net.minecraft.world.level.Level m_9236_(){return level;}public boolean m_6084_(){return alive;}
 public net.minecraft.nbt.CompoundTag getPersistentData(){return data;}public void m_146870_(){removed=true;}
 public double m_20280_(Entity other){return (x-other.x)*(x-other.x)+(z-other.z)*(z-other.z);}
 public net.minecraft.network.chat.Component m_7755_(){return new net.minecraft.network.chat.Component();}
}''',
'net/minecraft/world/entity/LivingEntity':'''public class LivingEntity extends Entity {public float hp=100,maxHp=100;public float m_21223_(){return hp;}public float m_21233_(){return maxHp;}}''',
'net/minecraft/world/entity/Mob':'''public class Mob extends LivingEntity {public LivingEntity target;public void m_6710_(LivingEntity t){target=t;}}''',
'net/minecraft/server/level/ServerPlayer':'''public class ServerPlayer extends net.minecraft.world.entity.LivingEntity {
 public net.minecraft.server.MinecraftServer f_8924_;public boolean spectator,creative,inside=true,foreign=false,nearDevourer=true;
 public java.util.Set<Integer> nearKinds=new java.util.HashSet<>();
 public boolean m_5833_(){return spectator;}public boolean m_7500_(){return creative;}
 public ServerLevel m_284548_(){return (ServerLevel)level;}
}''',
'net/minecraft/server/level/ServerBossEvent':'''public class ServerBossEvent extends net.minecraft.world.BossEvent {
 public boolean cleared;public float progress;
 public ServerBossEvent(net.minecraft.network.chat.Component c,BossBarColor color,BossBarOverlay overlay){}
 public void m_7706_(){cleared=true;}public void m_6543_(ServerPlayer p){}public void m_142711_(float value){progress=value;}
}''',
'net/minecraftforge/event/entity/EntityJoinLevelEvent':'''public class EntityJoinLevelEvent {
 public net.minecraft.world.entity.Entity entity;public boolean canceled;
 public EntityJoinLevelEvent(net.minecraft.world.entity.Entity entity){this.entity=entity;}
 public net.minecraft.world.entity.Entity getEntity(){return entity;}public net.minecraft.world.level.Level getLevel(){return entity.level;}
 public void setCanceled(boolean value){canceled=value;}
}''',
'dev/shadowsovereign/mythic/MythicState':'''public class MythicState {public int status;public boolean generated;public java.util.UUID active;public net.minecraft.nbt.CompoundTag gloom=new net.minecraft.nbt.CompoundTag();public net.minecraft.server.level.ServerBossEvent bar;}''',
'dev/shadowsovereign/adventure/AdventureData':'''public class AdventureData {
 public static java.util.Map<net.minecraft.server.MinecraftServer,AdventureData> worlds=new java.util.IdentityHashMap<>();
 public java.util.Map<java.util.UUID,Journal> owners=new java.util.HashMap<>();public int writes;
 public static AdventureData get(net.minecraft.server.MinecraftServer server){return worlds.computeIfAbsent(server,k->new AdventureData());}
 public Journal journal(java.util.UUID owner){return owners.computeIfAbsent(owner,k->new Journal());}public void m_77762_(){writes++;}
 public enum Kind {MYTHIC_LAIR,WARFRONT,ODYSSEY} public enum Phase {ACTIVE,PAUSED,READY}
 public static class Session {public Kind kind;public Phase phase;public java.util.Set<java.util.UUID> participants=new java.util.LinkedHashSet<>();}
 public static class Journal {public Session session;public dev.shadowsovereign.mythic.MythicState mythic=new dev.shadowsovereign.mythic.MythicState();}
}''',
'dev/shadowsovereign/adventure/AdventureService':'''public class AdventureService {public static AdventureData.Journal journal(net.minecraft.server.level.ServerPlayer p){return AdventureData.get(p.f_8924_).journal(p.id);}}''',
'dev/shadowsovereign/army/ShadowRecord':'''public class ShadowRecord {public java.util.UUID id=java.util.UUID.randomUUID();public net.minecraft.world.entity.Mob body;}''',
'dev/shadowsovereign/army/ArmyData':'''public class ArmyData {
 public static java.util.Map<net.minecraft.server.MinecraftServer,ArmyData> worlds=new java.util.IdentityHashMap<>();
 public java.util.Map<java.util.UUID,Army> armies=new java.util.HashMap<>();
 public static ArmyData get(net.minecraft.server.MinecraftServer s){return worlds.computeIfAbsent(s,k->new ArmyData());}
 public Army army(java.util.UUID id){return armies.computeIfAbsent(id,k->new Army());}
 public static class Army {public java.util.LinkedHashMap<java.util.UUID,ShadowRecord> shadows=new java.util.LinkedHashMap<>();}
}''',
'dev/shadowsovereign/army/ShadowService':'''public class ShadowService {
 public static boolean isShadow(net.minecraft.world.entity.Entity e){return e.getPersistentData().m_128403_("ShadowSovereignOwner");}
 public static net.minecraft.world.entity.Mob active(net.minecraft.server.MinecraftServer s,ShadowRecord r){return r.body;}
}''',
'dev/shadowsovereign/gloom/GloomRealm':'''public class GloomRealm {
 public static java.util.Map<java.util.UUID,net.minecraft.nbt.CompoundTag> states=new java.util.HashMap<>();
 public static net.minecraft.nbt.CompoundTag state(net.minecraft.server.level.ServerPlayer p){return states.computeIfAbsent(p.id,k->new net.minecraft.nbt.CompoundTag());}
 public static void dirty(net.minecraft.server.level.ServerPlayer p){dev.shadowsovereign.adventure.AdventureData.get(p.f_8924_).m_77762_();}
 public static void tick(net.minecraft.server.level.ServerPlayer p){}
 public static boolean inside(net.minecraft.server.level.ServerPlayer p,net.minecraft.core.BlockPos pos){return p.inside;}
}''',
'dev/shadowsovereign/odyssey/OdysseyRealm':'''public class OdysseyRealm {public static boolean foreignPlot(net.minecraft.server.level.ServerPlayer p,net.minecraft.core.BlockPos pos){return p.foreign;}}''',
'dev/shadowsovereign/mythic/MythicBeast':'''public class MythicBeast extends net.minecraft.world.entity.Mob {public int kind;public int kind(){return kind;}}''',
'dev/shadowsovereign/mythic/SoulDevourer':'public class SoulDevourer extends net.minecraft.world.entity.Mob {}',
'dev/shadowsovereign/mythic/MythicExpedition':'''public class MythicExpedition {
 public static final String[] IDS=new String[49];static {for(int i=0;i<IDS.length;i++)IDS[i]="kind_"+i;}
 public static java.util.Map<java.util.UUID,java.util.Map<Integer,MythicBeast>> bodies=new java.util.HashMap<>();public static int spawned;
 public static net.minecraft.nbt.CompoundTag state(net.minecraft.server.level.ServerPlayer p,int kind){var n=dev.shadowsovereign.adventure.AdventureService.journal(p).mythic.gloom;String key=IDS[kind];if(!n.values.containsKey(key))n.values.put(key,new net.minecraft.nbt.CompoundTag());return n.m_128469_(key);}
 public static MythicBeast body(net.minecraft.server.level.ServerPlayer p,int kind){return bodies.getOrDefault(p.id,java.util.Map.of()).get(kind);}
 public static boolean near(net.minecraft.server.level.ServerPlayer p,int kind,int radius){return p.inside && p.nearKinds.contains(kind);}
 public static boolean spawn(net.minecraft.server.level.ServerPlayer p,int kind){var b=new MythicBeast();b.kind=kind;b.server=p.f_8924_;b.level=p.level;b.data.m_128362_("SoulboundMythicHost",p.id);bodies.computeIfAbsent(p.id,k->new java.util.HashMap<>()).put(kind,b);state(p,kind).m_128362_("Active",b.id);spawned++;return true;}
}''',
'dev/shadowsovereign/mythic/MythicService':'''public class MythicService {
 public static java.util.Map<java.util.UUID,SoulDevourer> bodies=new java.util.HashMap<>();public static int spawned;
 public static MythicState state(net.minecraft.server.level.ServerPlayer p){return dev.shadowsovereign.adventure.AdventureService.journal(p).mythic;}
 public static SoulDevourer body(net.minecraft.server.level.ServerPlayer p){return bodies.get(p.id);}
 public static boolean near(net.minecraft.server.level.ServerPlayer p,int radius){return p.inside && p.nearDevourer;}
 public static boolean spawn(net.minecraft.server.level.ServerPlayer p){var b=new SoulDevourer();b.level=p.level;b.server=p.f_8924_;bodies.put(p.id,b);state(p).active=b.id;spawned++;return true;}
}''',
'dev/shadowsovereign/mythic/MythicLair':'public class MythicLair {public static void tick(net.minecraft.server.level.ServerPlayer p){}}',
'dev/shadowsovereign/mythic/MythicSites':'public class MythicSites {public static void tick(net.minecraft.server.level.ServerPlayer p){}}',
'dev/shadowsovereign/network/MythicNetwork':'public class MythicNetwork {public static void send(net.minecraft.server.level.ServerPlayer p,boolean open){}}',
'dev/shadowsovereign/realm/WildBosses':'''public class WildBosses {public static int captured;public static boolean enabled=true;
 public static boolean capture(net.minecraft.server.level.ServerPlayer p,net.minecraft.world.entity.Mob m){if(enabled)captured++;return enabled;}
 public static boolean captureOffline(net.minecraft.server.MinecraftServer s,java.util.UUID owner,net.minecraft.world.entity.Mob m){if(enabled)captured++;return enabled;}
}''',
}

SOURCES.update({
'net/minecraft/resources/ResourceKey':'public class ResourceKey<T> {}',
'net/minecraft/resources/ResourceLocation':'''public class ResourceLocation {private final String ns,path;public ResourceLocation(String n,String p){ns=n;path=p;}public String m_135827_(){return ns;}public String m_135815_(){return path;}}''',
'net/minecraftforge/eventbus/api/SubscribeEvent':'@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME) @java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD) public @interface SubscribeEvent {}',
'net/minecraftforge/fml/common/Mod':'''public class Mod {@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME) @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE) public @interface EventBusSubscriber {String modid();}}''',
'net/minecraftforge/registries/MissingMappingsEvent':'''public class MissingMappingsEvent {
 public java.util.List<Mapping<?>> mappings=new java.util.ArrayList<>();
 public net.minecraft.resources.ResourceKey<?> getKey(){return new net.minecraft.resources.ResourceKey<>();}
 public java.util.List<Mapping<?>> getMappings(net.minecraft.resources.ResourceKey key,String ns){return mappings;}
 public static class Mapping<T> {public boolean ignored;private final net.minecraft.resources.ResourceLocation key;
 public Mapping(String namespace,String path){key=new net.minecraft.resources.ResourceLocation(namespace,path);}
 public net.minecraft.resources.ResourceLocation getKey(){return key;}public void ignore(){ignored=true;}}
}''',
})

if __name__=='__main__':
    p=argparse.ArgumentParser();p.add_argument('build',type=Path);p.add_argument('resources',type=Path);args=p.parse_args()
    root=args.build/'abi-fixtures';classes=args.build/'helper-classes';classes.mkdir(exist_ok=True)
    files=[]
    for name,body in SOURCES.items():
        target=root/(name+'.java');target.parent.mkdir(parents=True,exist_ok=True)
        target.write_text('package '+name.rsplit('/',1)[0].replace('/','.')+';\n'+body+'\n');files.append(str(target))
    source=Path(__file__).parent
    helper_sources=['OpenMythics','RemovedContent','MigrationHooks','OpenMythicsTest','MigrationTest']
    subprocess.run(['java','-m','jdk.compiler/com.sun.tools.javac.Main','-source','17','-target','17','-d',str(classes),*files,*[str(source/(name+'.java')) for name in helper_sources]],check=True)
    subprocess.run(['java','-ea','-cp',str(classes),'OpenMythicsTest'],check=True)
    subprocess.run(['java','-ea','-cp',str(classes),'MigrationTest'],check=True)
    for name in helper_sources[:3]:
        destination=args.resources/f'dev/shadowsovereign/expansion/{name}.class';destination.parent.mkdir(parents=True,exist_ok=True)
        destination.write_bytes((classes/f'dev/shadowsovereign/expansion/{name}.class').read_bytes())
