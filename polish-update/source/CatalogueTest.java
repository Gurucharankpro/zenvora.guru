import com.google.gson.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/** Actual catalogue initializers/resources only, no Minecraft/Forge stand-ins. */
public class CatalogueTest {
 static class CatalogueLoader extends ClassLoader {
  final Map<String,byte[]> entries;final Path resources;
  CatalogueLoader(Map<String,byte[]> entries,Path resources){super(ClassLoader.getPlatformClassLoader());this.entries=entries;this.resources=resources;}
  @Override protected Class<?> findClass(String name)throws ClassNotFoundException{
   byte[] code=entries.get(name.replace('.','/')+".class");
   if(code==null)throw new ClassNotFoundException(name);return defineClass(name,code,0,code.length);
  }
  @Override public java.io.InputStream getResourceAsStream(String name){
   try{Path p=resources.resolve(name);if(Files.exists(p))return Files.newInputStream(p);}catch(Exception e){throw new RuntimeException(e);}
   byte[] b=entries.get(name);return b==null?null:new java.io.ByteArrayInputStream(b);
  }
 }
 public static void main(String[] args)throws Exception{
  Path base=Path.of(args[0]),spec=Path.of(args[1]),resources=Path.of(args[2]);
  try(ZipFile z=new ZipFile(base.toFile())){for(var e:Collections.list(z.entries()))if(!e.isDirectory())PatchReviewed.entries.put(e.getName(),z.getInputStream(e).readAllBytes());}
  // Gson is a real dependency, not an engine fixture.
  try(ZipFile z=new ZipFile(args[3])){for(var e:Collections.list(z.entries()))if(e.getName().endsWith(".class"))PatchReviewed.entries.put(e.getName(),z.getInputStream(e).readAllBytes());}
  var before=new CatalogueLoader(new HashMap<>(PatchReviewed.entries),resources);
  var old=(List<?>)Class.forName("dev.shadowsovereign.mythic.MythicProfiles",true,before).getField("ALL").get(null);
  PatchReviewed.profiles(JsonParser.parseString(Files.readString(spec)).getAsJsonObject().getAsJsonArray("profiles"));
  var after=new CatalogueLoader(new HashMap<>(PatchReviewed.entries),resources);
  var type=Class.forName("dev.shadowsovereign.mythic.MythicProfiles",true,after);var updated=(List<?>)type.getField("ALL").get(null);
  if(old.size()!=51||updated.size()!=54)throw new AssertionError("Wrong catalogue count");
  for(int i=0;i<51;i++)if(!old.get(i).toString().equals(updated.get(i).toString()))throw new AssertionError("Existing profile changed "+i);
  ClassNode ids=PatchReviewed.node("dev/shadowsovereign/mythic/MythicExpedition");ids.methods.removeIf(m->!m.name.equals("<clinit>"));ids.fields.removeIf(f->!f.name.equals("IDS"));
  ClassWriter w=new ClassWriter(0);ids.accept(w);Map<String,byte[]> only=new HashMap<>();only.put(ids.name+".class",w.toByteArray());
  String[] values=(String[])Class.forName(ids.name.replace('/','.'),true,new CatalogueLoader(only,resources)).getField("IDS").get(null);
  if(values.length!=54)throw new AssertionError("IDs count");
  for(int i=0;i<54;i++){
   Object p=updated.get(i);String id=(String)p.getClass().getMethod("id").invoke(p);if(!values[i].equals(id))throw new AssertionError("ID/profile alignment "+i);
   if(i>=51)for(Object move:(List<?>)p.getClass().getMethod("moves").invoke(p)){
    String clip=(String)move.getClass().getMethod("clip").invoke(move);
    if(!(Boolean)type.getMethod("has",String.class,String.class).invoke(null,id,clip))throw new AssertionError("Missing native clip "+id+"/"+clip);
    float duration=(Float)type.getMethod("length",String.class,String.class).invoke(null,id,clip);
    float hit=(Float)move.getClass().getMethod("impact").invoke(move);if(hit<=0||hit>duration)throw new AssertionError("Damage timing outside clip "+id+"/"+clip);
   }
  }
  System.out.println("PASS: actual appended catalogue/IDs have 54 aligned profiles; all 51 old definitions unchanged; new combat clips load through the real native animation reader. No game API execution.");
 }
}
