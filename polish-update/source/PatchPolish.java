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
public class PatchPolish implements Opcodes {
 static final String ROOT="dev/shadowsovereign/",P=ROOT+"mythic/MythicProfiles",C=ROOT+"content/",G=ROOT+"gloom/GloomContent";
 static final String B="net/minecraft/world/level/block/Block",PROPS="net/minecraft/world/level/block/state/BlockBehaviour$Properties";
 static final Handle META=new Handle(H_INVOKESTATIC,"java/lang/invoke/LambdaMetafactory","metafactory","(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",false);
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
  c=node(ROOT+"mythic/MythicExpedition");l=new InsnList();constant(l,57);call(l,INVOKESTATIC,"java/util/Arrays","copyOf","([Ljava/lang/Object;I)[Ljava/lang/Object;");l.add(new TypeInsnNode(CHECKCAST,"[Ljava/lang/String;"));
  int expected=54;for(var e:profiles){var p=e.getAsJsonObject();int index=p.get("index").getAsInt();if(index!=expected++)throw new IllegalStateException("Nonappend profile ID");l.add(new InsnNode(DUP));constant(l,index);constant(l,p.get("id").getAsString());l.add(new InsnNode(AASTORE));}
  if(expected!=57)throw new IllegalStateException("Missing profile");init(c).instructions.insertBefore(store(c,"IDS"),l);put(c);
 }
 static void decor(JsonArray blocks)throws Exception{
  ClassNode c;
  c=node(ROOT+"expansion/CreativeGroups");InsnList l=new InsnList();l.add(new TypeInsnNode(NEW,"java/util/HashSet"));l.add(new InsnNode(DUP_X1));l.add(new InsnNode(SWAP));call(l,INVOKESPECIAL,"java/util/HashSet","<init>","(Ljava/util/Collection;)V");
  for(var e:blocks){l.add(new InsnNode(DUP));constant(l,e.getAsJsonObject().get("id").getAsString());call(l,INVOKEVIRTUAL,"java/util/HashSet","add","(Ljava/lang/Object;)Z");l.add(new InsnNode(POP));}
  call(l,INVOKESTATIC,"java/util/Set","copyOf","(Ljava/util/Collection;)Ljava/util/Set;");init(c).instructions.insertBefore(store(c,"DECOR"),l);put(c);
 }
 static void hooks()throws Exception{
  // Preview framing changes only: no entity scale, attributes or hitbox edits.
  ClassNode c=node(ROOT+"client/MythicGrimoireScreen");int fits=0;
  for(var m:c.methods)if(m.name.equals("portrait"))for(var i:m.instructions)if(i instanceof LdcInsnNode l&&l.cst instanceof Double d&&d==.85){l.cst=.95;fits++;}
  if(fits!=1)throw new IllegalStateException("Unknown actual portrait fit code "+fits);put(c);
  // The real model has a private apply(String,float) player. Add the fauna
  // branch to its erased Mob setup method; all original branches are retained.
  c=node(ROOT+"client/DevourerModel");require(c.name,"apply","(Ljava/lang/String;F)V",false);
  var setup=c.methods.stream().filter(m->m.name.equals("setupAnim")&&m.desc.equals("(Lnet/minecraft/world/entity/Mob;FFFFF)V")).findFirst().orElseThrow();
  int branches=0;
  for(var i:setup.instructions.toArray())if(i.getOpcode()==RETURN){
   InsnList l=new InsnList();LabelNode end=new LabelNode();l.add(new VarInsnNode(ALOAD,1));l.add(new TypeInsnNode(INSTANCEOF,C+"PrismJerboa"));l.add(new JumpInsnNode(IFEQ,end));
   l.add(new VarInsnNode(ALOAD,0));l.add(new VarInsnNode(ALOAD,1));l.add(new TypeInsnNode(CHECKCAST,C+"PrismJerboa"));l.add(new VarInsnNode(FLOAD,3));call(l,INVOKEVIRTUAL,C+"PrismJerboa","clip","(F)Ljava/lang/String;");
   l.add(new VarInsnNode(ALOAD,1));l.add(new TypeInsnNode(CHECKCAST,C+"PrismJerboa"));l.add(new VarInsnNode(FLOAD,4));call(l,INVOKEVIRTUAL,C+"PrismJerboa","clipAge","(F)F");call(l,INVOKESPECIAL,c.name,"apply","(Ljava/lang/String;F)V");
   l.add(end);l.add(new FrameNode(F_SAME,0,null,0,null));setup.instructions.insertBefore(i,l);branches++;
  }if(branches!=1)throw new IllegalStateException("Unknown actual model setup exit");put(c);
  c=node(ROOT+"network/ShadowNetwork");int protocols=0;
  for(var f:c.fields)if(f.name.equals("VERSION"))f.value="39-polish-glass-desert";
  for(var m:c.methods)for(var i:m.instructions)if(i instanceof LdcInsnNode l&&"38-content-x2-x4".equals(l.cst)){l.cst="39-polish-glass-desert";protocols++;}
  if(protocols==0)throw new IllegalStateException("Latest protocol missing");put(c);
 }
 public static void main(String[] args)throws Exception{
  if(args.length!=5)throw new IllegalArgumentException("BASE.jar RESOURCES SPEC.json REAL_REOBF_OVERLAY.jar OUTPUT.jar");
  Path base=Path.of(args[0]),resources=Path.of(args[1]),overlay=Path.of(args[3]),output=Path.of(args[4]);
  if(!HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(base))).equals("f88cac49bae0d10961a277ab2277738892cb5ba4aaaae596fc5b0c17d64edb04"))throw new IllegalStateException("Baseline mismatch: analyze a newer user artifact instead of replaying this patch");
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
