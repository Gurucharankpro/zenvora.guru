import com.google.gson.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;

/** Overlay only the declared new content onto the exact reviewed binary.
 * A reobfuscated, real-Forge compiled overlay is REQUIRED. Never use fixture code.
 */
public class PatchReviewed implements Opcodes {
 static final String ROOT="dev/shadowsovereign/",P=ROOT+"mythic/MythicProfiles",C=ROOT+"content/",G=ROOT+"gloom/GloomContent";
 static final String B="net/minecraft/world/level/block/Block",PROPS="net/minecraft/world/level/block/state/BlockBehaviour$Properties";
 static final Handle META=new Handle(H_INVOKESTATIC,"java/lang/invoke/LambdaMetafactory","metafactory","(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",false);
 static Map<String,byte[]> entries=new TreeMap<>();static Set<String> changedClasses=new TreeSet<>();
 static ClassNode node(String name){byte[] bytes=entries.get(name+".class");if(bytes==null)throw new IllegalArgumentException("Missing actual class "+name);ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);return c;}
 static void put(ClassNode c)throws Exception{
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);byte[] bytes=w.toByteArray();ClassNode read=new ClassNode();new ClassReader(bytes).accept(read,0);
  for(MethodNode m:read.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<>(new BasicVerifier()).analyze(read.name,m);
  entries.put(c.name+".class",bytes);changedClasses.add(c.name+".class");
 }
 static MethodNode init(ClassNode c){return c.methods.stream().filter(m->m.name.equals("<clinit>")).findFirst().orElseThrow();}
 static AbstractInsnNode store(ClassNode c,String field){
  List<AbstractInsnNode> points=new ArrayList<>();for(var i:init(c).instructions)if(i instanceof FieldInsnNode f&&f.getOpcode()==PUTSTATIC&&f.owner.equals(c.name)&&f.name.equals(field))points.add(i);
  if(points.size()!=1)throw new IllegalStateException("Ambiguous initializer "+c.name+"."+field);return points.get(0);
 }
 static void require(String owner,String name,String descriptor,boolean isStatic){
  var m=node(owner).methods.stream().filter(x->x.name.equals(name)&&x.desc.equals(descriptor)).findFirst().orElseThrow(()->new IllegalStateException("Missing actual method "+owner+"."+name+descriptor));
  if(((m.access&ACC_STATIC)!=0)!=isStatic)throw new IllegalStateException("Wrong static/instance access "+owner+"."+name);
 }
 static void constant(InsnList l,Object value){l.add(new LdcInsnNode(value));}
 static void call(InsnList l,int opcode,String owner,String name,String desc){l.add(new MethodInsnNode(opcode,owner,name,desc,opcode==INVOKEINTERFACE||owner.equals("java/util/List")||owner.equals("java/util/Set")));}
 static void profiles(JsonArray profiles)throws Exception{
  require(P+"$Profile","<init>","(Ljava/lang/String;DDDDFFFLjava/lang/String;Ljava/lang/String;ZZLjava/util/List;)V",false);
  require(P+"$Move","<init>","(Ljava/lang/String;FIFF)V",false);
  ClassNode c=node(P);InsnList l=new InsnList();
  l.add(new TypeInsnNode(NEW,"java/util/ArrayList"));l.add(new InsnNode(DUP_X1));l.add(new InsnNode(SWAP));call(l,INVOKESPECIAL,"java/util/ArrayList","<init>","(Ljava/util/Collection;)V");
  for(var element:profiles){var p=element.getAsJsonObject();l.add(new InsnNode(DUP));l.add(new TypeInsnNode(NEW,P+"$Profile"));l.add(new InsnNode(DUP));constant(l,p.get("id").getAsString());
   for(String key:List.of("health","damage","armor","speed"))constant(l,p.get(key).getAsDouble());
   for(String key:List.of("width","height","scale"))constant(l,p.get(key).getAsFloat());
   constant(l,p.get("walk").getAsString());constant(l,p.get("run").getAsString());constant(l,p.get("flies").getAsBoolean()?1:0);constant(l,0);
   l.add(new TypeInsnNode(NEW,"java/util/ArrayList"));l.add(new InsnNode(DUP));call(l,INVOKESPECIAL,"java/util/ArrayList","<init>","()V");
   for(var e:p.getAsJsonArray("moves")){var m=e.getAsJsonArray();l.add(new InsnNode(DUP));l.add(new TypeInsnNode(NEW,P+"$Move"));l.add(new InsnNode(DUP));
    constant(l,m.get(0).getAsString());constant(l,m.get(1).getAsFloat());constant(l,m.get(2).getAsInt());constant(l,m.get(3).getAsFloat());constant(l,m.get(4).getAsFloat());call(l,INVOKESPECIAL,P+"$Move","<init>","(Ljava/lang/String;FIFF)V");call(l,INVOKEVIRTUAL,"java/util/ArrayList","add","(Ljava/lang/Object;)Z");l.add(new InsnNode(POP));
   }
   call(l,INVOKESTATIC,"java/util/List","copyOf","(Ljava/util/Collection;)Ljava/util/List;");call(l,INVOKESPECIAL,P+"$Profile","<init>","(Ljava/lang/String;DDDDFFFLjava/lang/String;Ljava/lang/String;ZZLjava/util/List;)V");
   call(l,INVOKEVIRTUAL,"java/util/ArrayList","add","(Ljava/lang/Object;)Z");l.add(new InsnNode(POP));
  }
  call(l,INVOKESTATIC,"java/util/List","copyOf","(Ljava/util/Collection;)Ljava/util/List;");init(c).instructions.insertBefore(store(c,"ALL"),l);put(c);
  c=node(ROOT+"mythic/MythicExpedition");l=new InsnList();constant(l,54);call(l,INVOKESTATIC,"java/util/Arrays","copyOf","([Ljava/lang/Object;I)[Ljava/lang/Object;");l.add(new TypeInsnNode(CHECKCAST,"[Ljava/lang/String;"));
  int expected=51;for(var e:profiles){var p=e.getAsJsonObject();int index=p.get("index").getAsInt();if(index!=expected++)throw new IllegalStateException("Nonappend profile ID");l.add(new InsnNode(DUP));constant(l,index);constant(l,p.get("id").getAsString());l.add(new InsnNode(AASTORE));}
  if(expected!=54)throw new IllegalStateException("Missing profile");init(c).instructions.insertBefore(store(c,"IDS"),l);put(c);
 }
 static void decor(JsonArray blocks)throws Exception{
  require(G,"block","(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/RegistryObject;",true);
  require(C+"ContentDecorationBlock","<init>","(L"+PROPS+";Ljava/lang/String;)V",false);
  ClassNode c=node(G);InsnList registration=new InsnList();int index=0;
  for(var e:blocks){var b=e.getAsJsonObject();String id=b.get("id").getAsString(),name="content$block"+index++;
   if(c.methods.stream().anyMatch(m->m.name.equals(name)))throw new IllegalStateException("Already patched "+name);
   MethodNode factory=new MethodNode(ACC_PRIVATE|ACC_STATIC|ACC_SYNTHETIC,name,"()L"+B+";",null,null);InsnList l=factory.instructions;
   l.add(new TypeInsnNode(NEW,C+"ContentDecorationBlock"));l.add(new InsnNode(DUP));
   l.add(new FieldInsnNode(GETSTATIC,"net/minecraft/world/level/block/Blocks","f_50069_","L"+B+";"));
   call(l,INVOKESTATIC,PROPS,"m_60926_","(Lnet/minecraft/world/level/block/state/BlockBehaviour;)L"+PROPS+";");
   call(l,INVOKEVIRTUAL,PROPS,"m_60910_","()L"+PROPS+";");call(l,INVOKEVIRTUAL,PROPS,"m_60955_","()L"+PROPS+";");
   constant(l,b.get("hardness").getAsFloat());call(l,INVOKEVIRTUAL,PROPS,"m_60978_","(F)L"+PROPS+";");
   int light=b.get("light").getAsInt();if(light>0){String method=name+"$light";MethodNode helper=new MethodNode(ACC_PRIVATE|ACC_STATIC|ACC_SYNTHETIC,method,"(Lnet/minecraft/world/level/block/state/BlockState;)I",null,null);constant(helper.instructions,light);helper.instructions.add(new InsnNode(IRETURN));c.methods.add(helper);
    l.add(new InvokeDynamicInsnNode("applyAsInt","()Ljava/util/function/ToIntFunction;",META,Type.getMethodType("(Ljava/lang/Object;)I"),new Handle(H_INVOKESTATIC,G,method,helper.desc,false),Type.getMethodType(helper.desc)));call(l,INVOKEVIRTUAL,PROPS,"m_60953_","(Ljava/util/function/ToIntFunction;)L"+PROPS+";");
   }
   constant(l,id);call(l,INVOKESPECIAL,C+"ContentDecorationBlock","<init>","(L"+PROPS+";Ljava/lang/String;)V");l.add(new InsnNode(ARETURN));c.methods.add(factory);
   constant(registration,id);registration.add(new InvokeDynamicInsnNode("get","()Ljava/util/function/Supplier;",META,Type.getMethodType("()Ljava/lang/Object;"),new Handle(H_INVOKESTATIC,G,name,factory.desc,false),Type.getMethodType(factory.desc)));
   call(registration,INVOKESTATIC,G,"block","(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/RegistryObject;");registration.add(new InsnNode(POP));
  }
  int returns=0;for(var i:init(c).instructions.toArray())if(i.getOpcode()==RETURN){init(c).instructions.insertBefore(i,registration);returns++;}if(returns!=1)throw new IllegalStateException("Unexpected Gloom initializer returns");put(c);
  c=node(ROOT+"expansion/CreativeGroups");InsnList l=new InsnList();l.add(new TypeInsnNode(NEW,"java/util/HashSet"));l.add(new InsnNode(DUP_X1));l.add(new InsnNode(SWAP));call(l,INVOKESPECIAL,"java/util/HashSet","<init>","(Ljava/util/Collection;)V");
  for(var e:blocks){l.add(new InsnNode(DUP));constant(l,e.getAsJsonObject().get("id").getAsString());call(l,INVOKEVIRTUAL,"java/util/HashSet","add","(Ljava/lang/Object;)Z");l.add(new InsnNode(POP));}
  call(l,INVOKESTATIC,"java/util/Set","copyOf","(Ljava/util/Collection;)Ljava/util/Set;");init(c).instructions.insertBefore(store(c,"DECOR"),l);put(c);
 }
 static void hooks()throws Exception{
  String descriptor="(Lnet/minecraftforge/eventbus/api/IEventBus;)V";require(C+"ContentBootstrap","register",descriptor,true);
  String main=entries.keySet().stream().filter(n->n.endsWith(".class")&&n.startsWith(ROOT)).filter(n->{ClassNode c=node(n.substring(0,n.length()-6));return c.visibleAnnotations!=null&&c.visibleAnnotations.stream().anyMatch(a->a.desc.equals("Lnet/minecraftforge/fml/common/Mod;"));}).findFirst().orElseThrow();
  ClassNode c=node(main.substring(0,main.length()-6));int count=0;
  for(var m:c.methods)for(var ins:m.instructions.toArray())if(ins instanceof MethodInsnNode mi&&mi.owner.equals(G)&&mi.name.equals("register")&&mi.desc.equals(descriptor)){
   m.instructions.insertBefore(ins,new InsnNode(DUP));m.instructions.insert(ins,new MethodInsnNode(INVOKESTATIC,C+"ContentBootstrap","register",descriptor,false));count++;
  }if(count!=1)throw new IllegalStateException("Ambiguous bootstrap site "+count);put(c);
  descriptor="(Lnet/minecraft/world/entity/Mob;Ldev/shadowsovereign/army/ShadowRecord;Lnet/minecraft/server/level/ServerPlayer;)Lnet/minecraft/world/phys/Vec3;";
  require(ROOT+"army/BattleService","followPosition",descriptor,true);require(C+"AutopilotManager","followPosition",descriptor,true);
  c=node(ROOT+"army/ShadowGoal");count=0;
  for(var m:c.methods)for(var ins:m.instructions)if(ins instanceof MethodInsnNode mi&&mi.owner.equals(ROOT+"army/BattleService")&&mi.name.equals("followPosition")&&mi.desc.equals(descriptor)){mi.owner=C+"AutopilotManager";count++;}
  if(count!=2)throw new IllegalStateException("Unexpected follow hook count "+count);put(c);
  descriptor="(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;";
  require(ROOT+"client/DevourerModel","expansion$renderLayer",descriptor,true);require(C+"ContentRenderSupport","layer",descriptor,true);
  c=node(ROOT+"client/DevourerModel");
  var layer=c.methods.stream().filter(m->m.name.equals("expansion$renderLayer")&&m.desc.equals("(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;")).findFirst().orElseThrow();
  layer.instructions.clear();layer.tryCatchBlocks.clear();if(layer.localVariables!=null)layer.localVariables.clear();
  layer.instructions.add(new VarInsnNode(ALOAD,0));call(layer.instructions,INVOKESTATIC,C+"ContentRenderSupport","layer",descriptor);layer.instructions.add(new InsnNode(ARETURN));put(c);
  // Force a matching client/server content protocol while keeping mod version.
  c=node(ROOT+"network/ShadowNetwork");int protocols=0;
  for(var field:c.fields)if(field.name.equals("VERSION"))field.value="38-content-x2-x4";
  for(var m:c.methods)for(var ins:m.instructions)if(ins instanceof LdcInsnNode ldc&&"37-shadow-awakening".equals(ldc.cst)){ldc.cst="38-content-x2-x4";protocols++;}
  if(protocols==0)throw new IllegalStateException("Old content protocol not found");put(c);
 }
 public static void main(String[] args)throws Exception{
  if(args.length!=5)throw new IllegalArgumentException("BASE.jar RESOURCES SPEC.json REAL_REOBF_OVERLAY.jar OUTPUT.jar");
  Path base=Path.of(args[0]),resources=Path.of(args[1]),overlay=Path.of(args[3]),output=Path.of(args[4]);
  if(!HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(base))).equals("2b9f00f36e7d1228fe31838daf6c147b5f5c719211d3f8467fedac88eba4da26"))throw new IllegalStateException("Baseline mismatch: analyze a newer user artifact instead of replaying this patch");
  try(ZipFile z=new ZipFile(base.toFile())){for(var e:Collections.list(z.entries()))if(!e.isDirectory())entries.put(e.getName(),z.getInputStream(e).readAllBytes());}
  byte[] open=entries.get(ROOT+"expansion/OpenMythics.class"),x1=entries.get(ROOT+"expansion/X1Hooks.class"),metadata=entries.get("META-INF/mods.toml");
  int classes=0;
  try(ZipFile z=new ZipFile(overlay.toFile())){for(var e:Collections.list(z.entries()))if(e.getName().endsWith(".class")){
   if(e.getName().startsWith(C)){entries.put(e.getName(),z.getInputStream(e).readAllBytes());classes++;}
   else if(!e.getName().equals(ROOT+"expansion/OpenMythics.class")&&!e.getName().equals(ROOT+"expansion/X1Hooks.class"))throw new IllegalStateException("Unapproved overlay class "+e.getName());
  }}if(classes<10)throw new IllegalStateException("Incomplete real-API content overlay");
  var spec=JsonParser.parseString(Files.readString(Path.of(args[2]))).getAsJsonObject();profiles(spec.getAsJsonArray("profiles"));decor(spec.getAsJsonArray("blocks"));hooks();
  try(var paths=Files.walk(resources)){for(Path p:paths.filter(Files::isRegularFile).toList()){
   String name=resources.relativize(p).toString().replace('\\','/');if(!(name.startsWith("assets/")||name.startsWith("data/")))throw new IllegalStateException("Not a game resource "+name);
   entries.put(name,Files.readAllBytes(p));
  }}
  if(!Arrays.equals(open,entries.get(ROOT+"expansion/OpenMythics.class"))||!Arrays.equals(x1,entries.get(ROOT+"expansion/X1Hooks.class"))||!Arrays.equals(metadata,entries.get("META-INF/mods.toml")))throw new IllegalStateException("Protected reviewed code/version overwritten");
  for(var e:entries.entrySet())if(e.getKey().endsWith(".class")){
   if(e.getKey().startsWith("net/")||e.getKey().startsWith("dev/soulboundqa/"))throw new IllegalStateException("Foreign engine/QA class in release");
   if(e.getKey().startsWith(C)){var c=new ClassNode();new ClassReader(e.getValue()).accept(c,0);put(c);}
  }
  Files.createDirectories(output.toAbsolutePath().getParent());
  if(Files.exists(output))throw new IllegalStateException("Refusing to overwrite previous distribution");
  try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(output))){for(var e:entries.entrySet()){var name=new ZipEntry(e.getKey());name.setTime(0);z.putNextEntry(name);z.write(e.getValue());z.closeEntry();}}
  System.out.println("Wrote content overlay "+output+"; reviewed OpenMythics/X1Hooks/mods.toml retained byte-for-byte");
 }
}
