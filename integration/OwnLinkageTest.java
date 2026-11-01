import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.util.*;
import java.util.zip.*;
/** Check all owned call sites, including private/synthetic calls in patched
 * initializers. Minecraft/Forge inheritance remains an explicitly unknown edge. */
public class OwnLinkageTest {
 static Map<String,ClassNode> classes=new HashMap<>();
 static boolean resolves(String owner,String name,String desc,boolean constructor,Set<String> seen){
  if(!seen.add(owner))return false;
  ClassNode c=classes.get(owner);
  if(c==null){
   if(owner.startsWith("dev/shadowsovereign/"))throw new AssertionError("Missing owned class "+owner);
   if(owner.startsWith("net/minecraft/")||owner.startsWith("net/minecraftforge/"))return true;
   if(owner.startsWith("java/"))try{
    Class<?> type=Class.forName(owner.replace('/','.'));
    for(var m:type.getDeclaredMethods())if(m.getName().equals(name)&&Type.getMethodDescriptor(m).equals(desc))return true;
    if(type.getSuperclass()!=null&&resolves(Type.getInternalName(type.getSuperclass()),name,desc,false,seen))return true;
    for(var i:type.getInterfaces())if(resolves(Type.getInternalName(i),name,desc,false,seen))return true;
   }catch(ClassNotFoundException e){throw new AssertionError(e);}
   return false;
  }
  if(c.methods.stream().anyMatch(m->m.name.equals(name)&&m.desc.equals(desc)))return true;
  if(constructor)return false;
  if(c.superName!=null&&resolves(c.superName,name,desc,false,seen))return true;
  for(String i:c.interfaces)if(resolves(i,name,desc,false,seen))return true;
  return false;
 }
 public static void main(String[] args)throws Exception{
  try(var z=new ZipFile(args[0])){for(var e:Collections.list(z.entries()))if(e.getName().endsWith(".class")){var c=new ClassNode();new ClassReader(z.getInputStream(e)).accept(c,0);classes.put(c.name,c);}}
  int checked=0;
  for(var c:classes.values())for(var m:c.methods)for(var ins:m.instructions){
   if(ins instanceof MethodInsnNode mi && mi.owner.startsWith("dev/shadowsovereign/")){
    if(!resolves(mi.owner,mi.name,mi.desc,mi.name.equals("<init>"),new HashSet<>()))throw new AssertionError(c.name+"."+m.name+" calls missing "+mi.owner+"."+mi.name+mi.desc);
    checked++;
   }
   if(ins instanceof InvokeDynamicInsnNode indy)for(var a:indy.bsmArgs)if(a instanceof Handle h&&h.getOwner().startsWith("dev/shadowsovereign/")&&h.getTag()>=Opcodes.H_INVOKEVIRTUAL){
    if(!resolves(h.getOwner(),h.getName(),h.getDesc(),h.getName().equals("<init>"),new HashSet<>()))throw new AssertionError("Missing owned method handle "+h);
    checked++;
   }
  }
  var gloom=classes.get("dev/shadowsovereign/gloom/GloomContent");int factories=0;
  for(var m:gloom.methods)if(m.name.equals("<clinit>"))for(var ins:m.instructions)if(ins instanceof MethodInsnNode mi&&mi.owner.equals(gloom.name)&&mi.desc.equals("(Ljava/lang/String;IF)Lnet/minecraftforge/registries/RegistryObject;")){
   if(!mi.name.equals("food"))throw new AssertionError("Wrong Gloom factory: "+mi.name);factories++;
  }
  if(factories==0)throw new AssertionError("Missing food/item registration calls");
  System.out.println("PASS: "+checked+" owned method/handle references resolve, including private initializer factories. Minecraft/Forge inherited edges remain unverified.");
 }
}
