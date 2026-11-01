import java.nio.file.*;
import java.net.*;
import com.google.gson.GsonBuilder;
/** Read the actual pure catalogue initializer, without substituting game stubs. */
public class ProfileDump {
 public static void main(String[] a)throws Exception{
  try(var loader=new URLClassLoader(new URL[]{Path.of(a[0]).toUri().toURL(),Path.of(a[1]).toUri().toURL()},ClassLoader.getPlatformClassLoader())){
   var profiles=Class.forName("dev.shadowsovereign.mythic.MythicProfiles",true,loader).getField("ALL").get(null);
   Files.writeString(Path.of(a[2]),new GsonBuilder().setPrettyPrinting().create().toJson(profiles));
  }
 }
}
