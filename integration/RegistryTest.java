import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.objectweb.asm.commons.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/** Executes the actual profile initializer and animation reader. Minecraft-free
 * catalogue checks only; entity registration and worldgen require Forge. */
public class RegistryTest {
    static URLClassLoader loader(Path jar,Path gson)throws Exception{
        return new URLClassLoader(new URL[]{jar.toUri().toURL(),gson.toUri().toURL()},ClassLoader.getPlatformClassLoader());
    }
    static class Isolated extends ClassLoader {
        Isolated(){super(ClassLoader.getPlatformClassLoader());}
        Class<?> define(byte[] code){return defineClass(null,code,0,code.length);}
    }
    public static void main(String[] args)throws Exception{
        Path base=Path.of(args[0]),jar=Path.of(args[1]),gson=Path.of(args[2]);
        try(var before=loader(base,gson);var after=loader(jar,gson);var zip=new ZipFile(jar.toFile())){
            int verifiedClasses=0,verifiedMethods=0;
            for(var entry:Collections.list(zip.entries())){
                if(!entry.getName().startsWith("dev/shadowsovereign/expansion/") || !entry.getName().endsWith(".class"))continue;
                var generated=new ClassNode();new ClassReader(zip.getInputStream(entry)).accept(generated,0);
                for(var method:generated.methods){
                    if((method.access&(Opcodes.ACC_ABSTRACT|Opcodes.ACC_NATIVE))==0){
                        new Analyzer<>(new BasicVerifier()).analyze(generated.name,method);verifiedMethods++;
                    }
                }
                verifiedClasses++;
            }
            if(verifiedClasses!=9)throw new AssertionError("Missing generated expansion classes: "+verifiedClasses);
            System.out.println("PASS: all "+verifiedClasses+" generated expansion classes / "+verifiedMethods+" methods passed bytecode stack/local checks (not game API linkage).");
            for(var entry:Collections.list(zip.entries())){
                if(!entry.getName().endsWith(".class"))continue;
                new ClassReader(zip.getInputStream(entry)).accept(new ClassRemapper(new ClassWriter(0),new Remapper(){
                    @Override public String map(String type){
                        if(type.startsWith("dev/shadowsovereign/") && zip.getEntry(type+".class")==null)
                            throw new AssertionError("Dangling mod class reference in "+entry.getName()+": "+type);
                        return type;
                    }
                }),0);
            }
            System.out.println("PASS: every mod-owned class reference resolves after retirement/relocation.");
            var helper=new ClassNode();new ClassReader(zip.getInputStream(zip.getEntry("dev/shadowsovereign/expansion/OpenMythics.class"))).accept(helper,0);
            int links=0;
            for(var method:helper.methods)for(var ins:method.instructions){
                String owner=null,member=null,descriptor=null;boolean field=false;
                if(ins instanceof MethodInsnNode mi){owner=mi.owner;member=mi.name;descriptor=mi.desc;}
                if(ins instanceof FieldInsnNode fi){owner=fi.owner;member=fi.name;descriptor=fi.desc;field=true;}
                if(owner==null || !owner.startsWith("dev/shadowsovereign/") || owner.equals(helper.name))continue;
                var entry=zip.getEntry(owner+".class");if(entry==null)throw new AssertionError("Missing mod class "+owner);
                var target=new ClassNode();new ClassReader(zip.getInputStream(entry)).accept(target,ClassReader.SKIP_CODE);
                final String n=member,d=descriptor;
                boolean found=field?target.fields.stream().anyMatch(f->f.name.equals(n)&&f.desc.equals(d)&&(f.access&Opcodes.ACC_PUBLIC)!=0):
                    target.methods.stream().anyMatch(m->m.name.equals(n)&&m.desc.equals(d)&&(m.access&Opcodes.ACC_PUBLIC)!=0);
                if(!found && target.superName!=null && target.superName.startsWith("net/minecraft/"))continue;
                if(!found)throw new AssertionError("Fixture/mod ABI mismatch: "+owner+"."+member+descriptor);
                links++;
            }
            System.out.println("PASS: "+links+" helper references match public members of the actual patched mod (Minecraft/Forge members remain unverified).");
            for(String className:List.of("MythicExpedition","MythicService")){
                var node=new ClassNode();new ClassReader(zip.getInputStream(zip.getEntry("dev/shadowsovereign/mythic/"+className+".class"))).accept(node,0);
                for(String methodName:List.of("start","tick","pause")){
                    var method=node.methods.stream().filter(m->m.name.equals(methodName)).findFirst().orElseThrow();
                    if(method.instructions.size()>8)throw new AssertionError("Exclusive encounter body still present: "+className+"."+methodName);
                    boolean routed=false;for(var ins:method.instructions)if(ins instanceof MethodInsnNode mi&&mi.owner.equals(helper.name))routed=true;
                    if(!routed)throw new AssertionError("Missing open encounter route");
                }
            }
            if(Collections.list(zip.entries()).stream().anyMatch(e->e.getName().startsWith("net/minecraft/")||e.getName().startsWith("net/minecraftforge/")))throw new AssertionError("ABI fixtures leaked into mod JAR");
            String name="dev.shadowsovereign.mythic.MythicProfiles";
            var oldClass=Class.forName(name,true,before);var newClass=Class.forName(name,true,after);
            var oldList=(List<?>)oldClass.getField("ALL").get(null);var newList=(List<?>)newClass.getField("ALL").get(null);
            if(oldList.size()!=38 || newList.size()!=47)throw new AssertionError("Profile count");
            for(int i=0;i<38;i++){
                if(i==34)continue;
                if(!oldList.get(i).toString().equals(newList.get(i<34?i:i-1).toString()))throw new AssertionError("Changed surviving profile "+i);
            }
            String idPath="dev/shadowsovereign/mythic/MythicExpedition.class";var c=new ClassNode();new ClassReader(zip.getInputStream(zip.getEntry(idPath))).accept(c,0);
            // Keep the real initializer, omitting methods with Minecraft types so
            // the catalogue can execute in an isolated JVM without game stubs.
            c.methods.removeIf(m->!m.name.equals("<clinit>"));c.fields.removeIf(f->!f.name.equals("IDS"));
            var w=new ClassWriter(0);c.accept(w);Class<?> idsClass=new Isolated().define(w.toByteArray());String[] ids=(String[])idsClass.getField("IDS").get(null);
            if(ids.length!=47)throw new AssertionError("ID count");
            var aquatic=new ClassNode();new ClassReader(zip.getInputStream(zip.getEntry(helper.name+".class"))).accept(aquatic,0);
            aquatic.methods.removeIf(m->!m.name.equals("isAquatic"));aquatic.fields.clear();
            var aw=new ClassWriter(0);aquatic.accept(aw);Class<?> aquaticType=((Isolated)idsClass.getClassLoader()).define(aw.toByteArray());
            var predicate=aquaticType.getMethod("isAquatic",int.class);
            for(int kind:new int[]{-1,0,37,40,41,42,43,44,45,46,999}){
                boolean result=(Boolean)predicate.invoke(null,kind);
                if(result!=(kind==41||kind==44))throw new AssertionError("Aquatic profile routing mismatch at "+kind);
            }
            var editing=new ClassNode();new ClassReader(zip.getInputStream(zip.getEntry("dev/shadowsovereign/expanse/ExpanseWorld.class"))).accept(editing,0);
            var editable=editing.methods.stream().filter(m->m.name.equals("editable")).findFirst().orElseThrow();
            boolean foreign=false,shrine=false,gloom=false;
            for(var ins:editable.instructions)if(ins instanceof MethodInsnNode mi){
                if(mi.name.equals("protectedSite"))throw new AssertionError("Boss site still blocks normal editing");
                foreign|=mi.name.equals("foreignPlot");shrine|=mi.name.equals("shrineAt");gloom|=mi.name.equals("editableGloom")&&mi.owner.equals(helper.name);
            }
            if(!foreign||!shrine||!gloom)throw new AssertionError("Edit protection routing changed unexpectedly");
            System.out.println("PASS: aquatic IDs 41/44 route correctly; boss-site edit vetoes removed while foreign-plot and quest-shrine checks remain.");
            int moves=0;
            for(int i=0;i<47;i++){
                Object profile=newList.get(i);Class<?> type=profile.getClass();String id=(String)type.getMethod("id").invoke(profile);
                if(!id.equals(ids[i]))throw new AssertionError("ID/profile mismatch at "+i);
                if(i<37)continue;
                for(Object move:(List<?>)type.getMethod("moves").invoke(profile)){
                    String clip=(String)move.getClass().getMethod("clip").invoke(move);
                    float duration=(Float)newClass.getMethod("length",String.class,String.class).invoke(null,id,clip);
                    boolean exists=(Boolean)newClass.getMethod("has",String.class,String.class).invoke(null,id,clip);
                    if(!exists || duration<=0)throw new AssertionError("Missing authored animation "+id+"/"+clip);moves++;
                }
            }
            if(newList.stream().anyMatch(p->p.toString().contains("villager_knight")))throw new AssertionError("Retired profile survived");
            if(!ids[45].equals("legendary_giant_zombie_refai")||!ids[46].equals("amin_el_nemr"))throw new AssertionError("Latest creatures misplaced/duplicated");
            System.out.println("PASS: 47 matching IDs/profiles; all 37 surviving original profiles unchanged; "+moves+" added combat clips loaded by the real animation reader.");
        }
    }
}
