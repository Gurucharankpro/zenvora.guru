import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
/** Resolve every JDK bootstrap and handle argument against the running real JDK. */
public class JdkBootstrapTest implements Opcodes {
 static int checked;
 static void check(Handle h)throws Exception{
  if(!h.getOwner().startsWith("java/"))return;
  Class<?> c=Class.forName(h.getOwner().replace('/','.'));String name=h.getName();
  if(h.getTag()<=H_PUTSTATIC){
   Field f=c.getDeclaredField(name);if(!org.objectweb.asm.Type.getDescriptor(f.getType()).equals(h.getDesc()))throw new IllegalStateException("Field descriptor mismatch "+h);
  }else{
   MethodType t=MethodType.fromMethodDescriptorString(h.getDesc(),ClassLoader.getSystemClassLoader());
   if(name.equals("<init>"))c.getDeclaredConstructor(t.parameterArray());
   else {Method m=c.getDeclaredMethod(name,t.parameterArray());if(m.getReturnType()!=t.returnType()||Modifier.isStatic(m.getModifiers())!=(h.getTag()==H_INVOKESTATIC))throw new IllegalStateException("Access/return mismatch "+h);}
  }checked++;
 }
 static void value(Object v)throws Exception{if(v instanceof Handle h)check(h);if(v instanceof ConstantDynamic d){check(d.getBootstrapMethod());for(int i=0;i<d.getBootstrapMethodArgumentCount();i++)value(d.getBootstrapMethodArgument(i));}}
 public static void main(String[] args)throws Exception{
  try(ZipFile z=new ZipFile(args[0])){for(var e:Collections.list(z.entries()))if(e.getName().endsWith(".class")){
   ClassNode c=new ClassNode();new ClassReader(z.getInputStream(e).readAllBytes()).accept(c,0);
   for(var m:c.methods)for(var i:m.instructions){if(i instanceof InvokeDynamicInsnNode d){check(d.bsm);for(Object a:d.bsmArgs)value(a);}if(i instanceof LdcInsnNode l)value(l.cst);}
  }}System.out.println("PASS: "+checked+" JDK bootstrap/handle references resolved against real JDK "+Runtime.version());
 }
}
