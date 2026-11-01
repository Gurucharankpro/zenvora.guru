import com.google.gson.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/** Read catalogues from the final built JAR, not a shadow copy of its source. */
public class FinalCatalogueTest {
 static Map<String,byte[]> load(Path p)throws Exception{Map<String,byte[]> map=new HashMap<>();try(var z=new ZipFile(p.toFile())){for(var e:Collections.list(z.entries()))if(!e.isDirectory())map.put(e.getName(),z.getInputStream(e).readAllBytes());}return map;}
 public static void main(String[] args)throws Exception{
  Map<String,byte[]> before=load(Path.of(args[0])),after=load(Path.of(args[1])),gson=load(Path.of(args[2]));
  before.putAll(gson);after.putAll(gson);
  Path noOverlay=Files.createTempDirectory("catalogue-resources-empty");
  try{
   var oldLoader=new CatalogueTest.CatalogueLoader(before,noOverlay);var newLoader=new CatalogueTest.CatalogueLoader(after,noOverlay);
   String name="dev.shadowsovereign.mythic.MythicProfiles";
   var type=Class.forName(name,true,newLoader);var old=(List<?>)Class.forName(name,true,oldLoader).getField("ALL").get(null);var profiles=(List<?>)type.getField("ALL").get(null);
   if(old.size()!=54||profiles.size()!=57)throw new AssertionError("Profile count");
   for(int i=0;i<54;i++)if(!old.get(i).toString().equals(profiles.get(i).toString()))throw new AssertionError("Original profile changed "+i);
   for(int i=54;i<57;i++){
    Object p=profiles.get(i);String id=(String)p.getClass().getMethod("id").invoke(p);
    for(Object m:(List<?>)p.getClass().getMethod("moves").invoke(p)){
     String clip=(String)m.getClass().getMethod("clip").invoke(m);
     if(!(Boolean)type.getMethod("has",String.class,String.class).invoke(null,id,clip))throw new AssertionError("Missing packaged clip");
    }
   }
   var groups=Class.forName("dev.shadowsovereign.expansion.CreativeGroups",true,newLoader);var field=groups.getDeclaredField("DECOR");field.setAccessible(true);
   var ids=(Set<?>)field.get(null);if(ids.size()!=200)throw new AssertionError("Decor count");
   for(Object id:ids)if(!groups.getMethod("category",String.class,int.class,boolean.class).invoke(null,id,0,true).equals(2))throw new AssertionError("Decor routing "+id);
   System.out.println("PASS: final built catalogue contains all 54 unchanged old and 3 new profiles; packaged moves load; all 200 decor classify correctly. No engine API execution.");
  }finally{Files.deleteIfExists(noOverlay);}
 }
}
