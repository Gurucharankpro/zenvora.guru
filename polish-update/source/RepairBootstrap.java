import java.lang.invoke.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Repair only the two reported faults in the latest complete distribution. */
public class RepairBootstrap implements Opcodes {
 public static final String DESCRIPTOR;
 static {try { DESCRIPTOR=Type.getMethodDescriptor(LambdaMetafactory.class.getDeclaredMethod("metafactory",MethodHandles.Lookup.class,String.class,MethodType.class,MethodType.class,MethodHandle.class,MethodType.class)); }catch(ReflectiveOperationException e){throw new ExceptionInInitializerError(e);}}
 public static void main(String[] args)throws Exception{
  TreeMap<String,byte[]> files=new TreeMap<>();int bootstrap=0,config=0;
  try(ZipFile z=new ZipFile(args[0])){for(var e:Collections.list(z.entries()))if(!e.isDirectory())files.put(e.getName(),z.getInputStream(e).readAllBytes());}
  for(String name:List.of("dev/shadowsovereign/gloom/GloomContent.class","dev/shadowsovereign/content/ContentConfig.class")){
   ClassNode c=new ClassNode();new ClassReader(files.get(name)).accept(c,0);
   for(var m:c.methods)for(var i:m.instructions){
    if(i instanceof InvokeDynamicInsnNode d&&d.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")&&d.bsm.getName().equals("metafactory")&&!d.bsm.getDesc().equals(DESCRIPTOR)){
     d.bsm=new Handle(H_INVOKESTATIC,d.bsm.getOwner(),d.bsm.getName(),DESCRIPTOR,false);bootstrap++;
    }
    if(i instanceof LdcInsnNode l&&"performance.debugLogIntervalSeconds".equals(l.cst)){
     var def=i.getNext();var min=def.getNext();
     if(def.getOpcode()!=ICONST_0)continue;
     if(min.getOpcode()!=ICONST_5)throw new IllegalStateException("Unexpected real config operands");
     m.instructions.set(min,new InsnNode(ICONST_0));config++;
    }
   }
   ClassWriter w=new ClassWriter(0);c.accept(w);files.put(name,w.toByteArray());
  }
  if(bootstrap!=63||config!=1)throw new IllegalStateException("Repair match counts "+bootstrap+" / "+config);
  try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))){for(var e:files.entrySet()){ZipEntry p=new ZipEntry(e.getKey());p.setTime(0);z.putNextEntry(p);z.write(e.getValue());z.closeEntry();}}
  System.out.println("Repaired exactly 63 bootstrap descriptors and one config minimum using Java "+Runtime.version());
 }
}
