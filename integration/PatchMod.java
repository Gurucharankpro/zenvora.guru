import com.google.gson.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.objectweb.asm.commons.*;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;

/** Reproducible integration against the exact supplied binary, without
 * recompiling inferred Minecraft source. Surviving entity registry names stay
 * unchanged; the retired profile's internal catalogue slot is compacted. */
public class PatchMod implements Opcodes {
    static final String ROOT="dev/shadowsovereign/", PROFILES=ROOT+"mythic/MythicProfiles", GLOOM=ROOT+"gloom/GloomContent";
    static final String BLOCK="net/minecraft/world/level/block/Block", PROPS="net/minecraft/world/level/block/state/BlockBehaviour$Properties";
    static final String PROFILE=PROFILES+"$Profile", MOVE=PROFILES+"$Move";
    static final String OPEN=ROOT+"expansion/OpenMythics";
    static final Handle META=new Handle(H_INVOKESTATIC,"java/lang/invoke/LambdaMetafactory","metafactory",
        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",false);
    static void push(InsnList l, Object value) { l.add(new LdcInsnNode(value)); }
    static void call(InsnList l,int op,String owner,String name,String desc) {l.add(new MethodInsnNode(op,owner,name,desc,op==INVOKEINTERFACE || owner.equals("java/util/List")));}
    static MethodNode clinit(ClassNode c){return c.methods.stream().filter(m->m.name.equals("<clinit>")).findFirst().orElseThrow();}
    static AbstractInsnNode put(ClassNode c,String name){
        for(var n:clinit(c).instructions)if(n instanceof FieldInsnNode f && f.getOpcode()==PUTSTATIC && f.owner.equals(c.name) && f.name.equals(name))return n;
        throw new IllegalStateException("Missing field initializer "+name);
    }
    static InsnList listStart(){var l=new InsnList();l.add(new TypeInsnNode(NEW,"java/util/ArrayList"));l.add(new InsnNode(DUP));call(l,INVOKESPECIAL,"java/util/ArrayList","<init>","()V");return l;}
    static void listCopy(InsnList l){call(l,INVOKESTATIC,"java/util/List","copyOf","(Ljava/util/Collection;)Ljava/util/List;");}
    static void addProfiles(ClassNode c,JsonArray added) {
        var l=new InsnList(); // input stack: original immutable List
        l.add(new TypeInsnNode(NEW,"java/util/ArrayList"));l.add(new InsnNode(DUP_X1));l.add(new InsnNode(SWAP));
        call(l,INVOKESPECIAL,"java/util/ArrayList","<init>","(Ljava/util/Collection;)V");
        for(var element:added){var p=element.getAsJsonObject();l.add(new InsnNode(DUP));
            l.add(new TypeInsnNode(NEW,PROFILE));l.add(new InsnNode(DUP));push(l,p.get("id").getAsString());
            for(String key:List.of("health","damage","armor","speed"))push(l,p.get(key).getAsDouble());
            for(String key:List.of("width","height","scale"))push(l,p.get(key).getAsFloat());
            push(l,p.get("walk").getAsString());push(l,p.get("run").getAsString());push(l,p.get("flies").getAsBoolean()?1:0);push(l,0);
            l.add(listStart());
            for(var me:p.getAsJsonArray("moves")){var m=me.getAsJsonArray();l.add(new InsnNode(DUP));l.add(new TypeInsnNode(NEW,MOVE));l.add(new InsnNode(DUP));
                push(l,m.get(0).getAsString());push(l,m.get(1).getAsFloat());push(l,m.get(2).getAsInt());push(l,m.get(3).getAsFloat());push(l,m.get(4).getAsFloat());
                call(l,INVOKESPECIAL,MOVE,"<init>","(Ljava/lang/String;FIFF)V");call(l,INVOKEVIRTUAL,"java/util/ArrayList","add","(Ljava/lang/Object;)Z");l.add(new InsnNode(POP));}
            listCopy(l);
            call(l,INVOKESPECIAL,PROFILE,"<init>","(Ljava/lang/String;DDDDFFFLjava/lang/String;Ljava/lang/String;ZZLjava/util/List;)V");
            call(l,INVOKEVIRTUAL,"java/util/ArrayList","add","(Ljava/lang/Object;)Z");l.add(new InsnNode(POP));
        }
        l.add(new InsnNode(DUP));push(l,34);call(l,INVOKEVIRTUAL,"java/util/ArrayList","remove","(I)Ljava/lang/Object;");l.add(new InsnNode(POP));
        listCopy(l);clinit(c).instructions.insertBefore(put(c,"ALL"),l);
    }
    static void addIds(ClassNode c,JsonArray added){
        var l=new InsnList();push(l,38+added.size());call(l,INVOKESTATIC,"java/util/Arrays","copyOf","([Ljava/lang/Object;I)[Ljava/lang/Object;");l.add(new TypeInsnNode(CHECKCAST,"[Ljava/lang/String;"));
        for(var e:added){var p=e.getAsJsonObject();l.add(new InsnNode(DUP));push(l,p.get("index").getAsInt()+1);push(l,p.get("id").getAsString());l.add(new InsnNode(AASTORE));}
        int local=clinit(c).maxLocals;
        l.add(new VarInsnNode(ASTORE,local));l.add(new VarInsnNode(ALOAD,local));push(l,35);
        l.add(new VarInsnNode(ALOAD,local));push(l,34);push(l,38+added.size()-35);
        call(l,INVOKESTATIC,"java/lang/System","arraycopy","(Ljava/lang/Object;ILjava/lang/Object;II)V");
        l.add(new VarInsnNode(ALOAD,local));push(l,37+added.size());call(l,INVOKESTATIC,"java/util/Arrays","copyOf","([Ljava/lang/Object;I)[Ljava/lang/Object;");l.add(new TypeInsnNode(CHECKCAST,"[Ljava/lang/String;"));
        clinit(c).instructions.insertBefore(put(c,"IDS"),l);
    }
    static void factory(InsnList l,String owner,String name,String result){
        l.add(new InvokeDynamicInsnNode("get","()Ljava/util/function/Supplier;",META,
            Type.getMethodType("()Ljava/lang/Object;"),new Handle(H_INVOKESTATIC,owner,name,"()L"+result+";",false),Type.getMethodType("()L"+result+";")));
    }
    static void addBlocks(ClassNode c,JsonArray blocks){
        var register=new InsnList();int index=0;
        for(var entry:blocks){var b=entry.getAsJsonObject();String id=b.get("id").getAsString(),cat=b.get("category").getAsString();String name="expansion$block"+index++;
            String cls=id.equals("abyss_hanging_soul_roots")?ROOT+"expansion/HangingRoots":switch(cat){case "log"->"net/minecraft/world/level/block/RotatedPillarBlock";case "leaves"->"net/minecraft/world/level/block/LeavesBlock";case "sapling"->ROOT+"expansion/TidewoodSapling";case "plant"->GLOOM+"$GroundPlant";case "aquatic"->ROOT+"expansion/AquaticPlant";case "glass"->"net/minecraft/world/level/block/GlassBlock";case "decor"->ROOT+"expansion/DecorationBlock";default->BLOCK;};
            String vanilla=switch(cat){case "log"->"f_49999_";case "leaves"->"f_50050_";case "plant","sapling","aquatic","decor"->"f_50111_";default->"f_50069_";};
            var method=new MethodNode(ACC_PRIVATE|ACC_STATIC|ACC_SYNTHETIC,name,"()L"+BLOCK+";",null,null);var l=method.instructions;
            l.add(new TypeInsnNode(NEW,cls));l.add(new InsnNode(DUP));
            if(cat.equals("sapling")){
                call(l,INVOKESPECIAL,cls,"<init>","()V");l.add(new InsnNode(ARETURN));c.methods.add(method);
                push(register,id);factory(register,c.name,name,BLOCK);call(register,INVOKESTATIC,c.name,"block","(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/RegistryObject;");register.add(new InsnNode(POP));continue;
            }
            l.add(new FieldInsnNode(GETSTATIC,"net/minecraft/world/level/block/Blocks",vanilla,"L"+BLOCK+";"));
            call(l,INVOKESTATIC,PROPS,"m_60926_","(Lnet/minecraft/world/level/block/state/BlockBehaviour;)L"+PROPS+";");
            if(cat.equals("decor")){
                call(l,INVOKEVIRTUAL,PROPS,"m_60910_","()L"+PROPS+";");
                call(l,INVOKEVIRTUAL,PROPS,"m_60955_","()L"+PROPS+";");
                push(l,b.get("hardness").getAsFloat());call(l,INVOKEVIRTUAL,PROPS,"m_60978_","(F)L"+PROPS+";");
            }
            int light=b.get("light").getAsInt();
            if(light>0){String lightMethod=name+"$light";var lm=new MethodNode(ACC_PRIVATE|ACC_STATIC|ACC_SYNTHETIC,lightMethod,"(Lnet/minecraft/world/level/block/state/BlockState;)I",null,null);
                push(lm.instructions,light);lm.instructions.add(new InsnNode(IRETURN));c.methods.add(lm);
                l.add(new InvokeDynamicInsnNode("applyAsInt","()Ljava/util/function/ToIntFunction;",META,Type.getMethodType("(Ljava/lang/Object;)I"),new Handle(H_INVOKESTATIC,c.name,lightMethod,lm.desc,false),Type.getMethodType(lm.desc)));
                call(l,INVOKEVIRTUAL,PROPS,"m_60953_","(Ljava/util/function/ToIntFunction;)L"+PROPS+";");}
            call(l,INVOKESPECIAL,cls,"<init>","(L"+PROPS+";)V");l.add(new InsnNode(ARETURN));c.methods.add(method);
            push(register,id);factory(register,c.name,name,BLOCK);call(register,INVOKESTATIC,c.name,"block","(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/RegistryObject;");register.add(new InsnNode(POP));
        }
        for(var n:clinit(c).instructions.toArray())if(n.getOpcode()==RETURN)clinit(c).instructions.insertBefore(n,register);
    }
    static void aquaticSpawn(ClassNode c){
        // Retain the entire ground/collision/chunk/site validation path; only
        // exempt the aquatic profile from the final fluid rejection.
        MethodInsnNode original=null;
        for(var m:c.methods)if(m.name.equals("place") && m.desc.contains("BoundingBox")){
            for(var ins:m.instructions.toArray())if(ins instanceof MethodInsnNode call && call.name.equals("m_46855_")){
                original=call;m.instructions.insertBefore(ins,new VarInsnNode(ALOAD,1));
                m.instructions.set(ins,new MethodInsnNode(INVOKESTATIC,c.name,"expansion$blockedFluid",
                    "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/entity/Mob;)Z",false));
            }
        }
        if(original==null)throw new IllegalStateException("Spawn fluid validation not found");
        var m=new MethodNode(ACC_PRIVATE|ACC_STATIC,"expansion$blockedFluid",
            "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/entity/Mob;)Z",null,null);
        var l=m.instructions;var normal=new LabelNode();
        l.add(new VarInsnNode(ALOAD,2));l.add(new TypeInsnNode(INSTANCEOF,ROOT+"mythic/MythicBeast"));l.add(new JumpInsnNode(IFEQ,normal));
        l.add(new VarInsnNode(ALOAD,2));l.add(new TypeInsnNode(CHECKCAST,ROOT+"mythic/MythicBeast"));call(l,INVOKEVIRTUAL,ROOT+"mythic/MythicBeast","kind","()I");call(l,INVOKESTATIC,OPEN,"isAquatic","(I)Z");l.add(new JumpInsnNode(IFEQ,normal));
        l.add(new InsnNode(ICONST_0));l.add(new InsnNode(IRETURN));l.add(normal);l.add(new FrameNode(F_SAME,0,null,0,null));
        l.add(new VarInsnNode(ALOAD,0));l.add(new VarInsnNode(ALOAD,1));l.add(new MethodInsnNode(original.getOpcode(),original.owner,original.name,original.desc,original.itf));l.add(new InsnNode(IRETURN));c.methods.add(m);
    }
    static void aquaticAir(ClassNode c){
        var m=new MethodNode(ACC_PRIVATE,"expansion$restoreAquaticAir","()V",null,null);var l=m.instructions;var end=new LabelNode();
        l.add(new VarInsnNode(ALOAD,0));call(l,INVOKEVIRTUAL,c.name,"kind","()I");call(l,INVOKESTATIC,OPEN,"isAquatic","(I)Z");l.add(new JumpInsnNode(IFEQ,end));
        l.add(new VarInsnNode(ALOAD,0));l.add(new VarInsnNode(ALOAD,0));call(l,INVOKEVIRTUAL,c.name,"m_6062_","()I");call(l,INVOKEVIRTUAL,c.name,"m_20301_","(I)V");
        l.add(end);l.add(new FrameNode(F_SAME,0,null,0,null));l.add(new InsnNode(RETURN));c.methods.add(m);
        var tick=c.methods.stream().filter(method->method.name.equals("m_8107_")&&method.desc.equals("()V")).findFirst().orElseThrow();
        var before=new InsnList();before.add(new VarInsnNode(ALOAD,0));call(before,INVOKESPECIAL,c.name,m.name,m.desc);tick.instructions.insert(before);
        var swim=new MethodNode(ACC_PRIVATE,"expansion$configureWaterNavigation","()V",null,null);l=swim.instructions;end=new LabelNode();
        l.add(new VarInsnNode(ALOAD,0));call(l,INVOKEVIRTUAL,c.name,"kind","()I");call(l,INVOKESTATIC,OPEN,"isAquatic","(I)Z");l.add(new JumpInsnNode(IFEQ,end));
        String control="net/minecraft/world/entity/ai/control/SmoothSwimmingMoveControl",nav="net/minecraft/world/entity/ai/navigation/WaterBoundPathNavigation";
        l.add(new VarInsnNode(ALOAD,0));l.add(new TypeInsnNode(NEW,control));l.add(new InsnNode(DUP));l.add(new VarInsnNode(ALOAD,0));push(l,85);push(l,10);push(l,.1f);push(l,.5f);push(l,0);
        call(l,INVOKESPECIAL,control,"<init>","(Lnet/minecraft/world/entity/Mob;IIFFZ)V");l.add(new FieldInsnNode(PUTFIELD,c.name,"f_21342_","Lnet/minecraft/world/entity/ai/control/MoveControl;"));
        l.add(new VarInsnNode(ALOAD,0));l.add(new TypeInsnNode(NEW,nav));l.add(new InsnNode(DUP));l.add(new VarInsnNode(ALOAD,0));l.add(new VarInsnNode(ALOAD,0));call(l,INVOKEVIRTUAL,c.name,"m_9236_","()Lnet/minecraft/world/level/Level;");
        call(l,INVOKESPECIAL,nav,"<init>","(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/world/level/Level;)V");l.add(new FieldInsnNode(PUTFIELD,c.name,"f_21344_","Lnet/minecraft/world/entity/ai/navigation/PathNavigation;"));
        l.add(end);l.add(new FrameNode(F_SAME,0,null,0,null));l.add(new InsnNode(RETURN));c.methods.add(swim);
        for(var ctor:c.methods)if(ctor.name.equals("<init>"))for(var ins:ctor.instructions.toArray())if(ins.getOpcode()==RETURN){
            var init=new InsnList();init.add(new VarInsnNode(ALOAD,0));call(init,INVOKESPECIAL,c.name,swim.name,swim.desc);ctor.instructions.insertBefore(ins,init);
        }
        // The shared FloatGoal would otherwise force the aquatic companion to
        // the surface, taking priority over its underwater combat navigation.
        for(var goal:c.methods)if(goal.name.equals("m_8099_"))for(var ins:goal.instructions){
            if(ins instanceof TypeInsnNode t && t.getOpcode()==NEW && t.desc.equals("net/minecraft/world/entity/ai/goal/FloatGoal"))t.desc=ROOT+"expansion/CompanionFloatGoal";
            if(ins instanceof MethodInsnNode mi && mi.owner.equals("net/minecraft/world/entity/ai/goal/FloatGoal") && mi.name.equals("<init>"))mi.owner=ROOT+"expansion/CompanionFloatGoal";
        }
    }
    static byte[] floatGoal(){
        String name=ROOT+"expansion/CompanionFloatGoal",parent="net/minecraft/world/entity/ai/goal/FloatGoal",mob="net/minecraft/world/entity/Mob";
        var c=new ClassNode();c.version=V17;c.access=ACC_PUBLIC|ACC_SUPER;c.name=name;c.superName=parent;
        c.fields.add(new FieldNode(ACC_PRIVATE|ACC_FINAL,"companion","L"+mob+";",null,null));
        var ctor=new MethodNode(ACC_PUBLIC,"<init>","(L"+mob+";)V",null,null);var l=ctor.instructions;
        l.add(new VarInsnNode(ALOAD,0));l.add(new VarInsnNode(ALOAD,1));call(l,INVOKESPECIAL,parent,"<init>",ctor.desc);
        l.add(new VarInsnNode(ALOAD,0));l.add(new VarInsnNode(ALOAD,1));l.add(new FieldInsnNode(PUTFIELD,name,"companion","L"+mob+";"));l.add(new InsnNode(RETURN));c.methods.add(ctor);
        var method=new MethodNode(ACC_PUBLIC,"m_8036_","()Z",null,null);l=method.instructions;var normal=new LabelNode();
        l.add(new VarInsnNode(ALOAD,0));l.add(new FieldInsnNode(GETFIELD,name,"companion","L"+mob+";"));l.add(new TypeInsnNode(CHECKCAST,ROOT+"mythic/MythicBeast"));call(l,INVOKEVIRTUAL,ROOT+"mythic/MythicBeast","kind","()I");call(l,INVOKESTATIC,OPEN,"isAquatic","(I)Z");l.add(new JumpInsnNode(IFEQ,normal));
        l.add(new InsnNode(ICONST_0));l.add(new InsnNode(IRETURN));l.add(normal);l.add(new FrameNode(F_SAME,0,null,0,null));l.add(new VarInsnNode(ALOAD,0));call(l,INVOKESPECIAL,parent,"m_8036_","()Z");l.add(new InsnNode(IRETURN));c.methods.add(method);
        var w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);return w.toByteArray();
    }
    static byte[] tidewoodSapling(byte[] bytes){
        String oldName=GLOOM+"$DuskSapling",newName=ROOT+"expansion/TidewoodSapling";
        var c=new ClassNode();new ClassReader(bytes).accept(new ClassRemapper(c,new SimpleRemapper(oldName,newName)),0);
        c.nestHostClass=null;c.innerClasses.clear();
        for(var m:c.methods)for(var n:m.instructions.toArray())if(n instanceof FieldInsnNode f && f.getOpcode()==GETSTATIC && f.owner.equals(GLOOM) && Set.of("LOG","LEAVES").contains(f.name)){
            var l=new InsnList();l.add(new FieldInsnNode(GETSTATIC,GLOOM,"blocks","Ljava/util/Map;"));push(l,f.name.equals("LOG")?"sunken_tidewood_log":"sunken_tidewood_leaves");
            call(l,INVOKEINTERFACE,"java/util/Map","get","(Ljava/lang/Object;)Ljava/lang/Object;");l.add(new TypeInsnNode(CHECKCAST,"net/minecraftforge/registries/RegistryObject"));
            m.instructions.insertBefore(n,l);m.instructions.remove(n);
        }
        var w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);return w.toByteArray();
    }
    static byte[] blockSubclass(String name,String parent){
        var w=new ClassWriter(ClassWriter.COMPUTE_MAXS);w.visit(V17,ACC_PUBLIC|ACC_FINAL|ACC_SUPER,name,null,parent,null);
        var m=w.visitMethod(ACC_PUBLIC,"<init>","(L"+PROPS+";)V",null,null);m.visitCode();m.visitVarInsn(ALOAD,0);m.visitVarInsn(ALOAD,1);
        m.visitMethodInsn(INVOKESPECIAL,parent,"<init>","(L"+PROPS+";)V",false);m.visitInsn(RETURN);m.visitMaxs(0,0);m.visitEnd();w.visitEnd();return w.toByteArray();
    }
    static void redirect(ClassNode c,String name,String descriptor,String target){
        var m=c.methods.stream().filter(x->x.name.equals(name)&&x.desc.equals(descriptor)).findFirst().orElseThrow(()->new IllegalStateException(c.name+"."+name+descriptor));
        m.instructions.clear();m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();
        if(m.visibleLocalVariableAnnotations!=null)m.visibleLocalVariableAnnotations.clear();if(m.invisibleLocalVariableAnnotations!=null)m.invisibleLocalVariableAnnotations.clear();
        int slot=0;for(var t:Type.getArgumentTypes(descriptor)){m.instructions.add(new VarInsnNode(t.getOpcode(ILOAD),slot));slot+=t.getSize();}
        call(m.instructions,INVOKESTATIC,OPEN,target,descriptor);m.instructions.add(new InsnNode(Type.getReturnType(descriptor).getOpcode(IRETURN)));
    }
    static void openEncounters(ClassNode c){
        String player="Lnet/minecraft/server/level/ServerPlayer;",pause="(Lnet/minecraft/server/MinecraftServer;Ljava/util/UUID;)V";
        if(c.name.endsWith("/MythicService")){
            redirect(c,"start","("+player+")Z","startDevourer");redirect(c,"tick","("+player+")V","tick");
            redirect(c,"pause",pause,"pause");redirect(c,"cancel","("+player+")Z","cancel");
        }
        if(c.name.endsWith("/MythicExpedition")){
            redirect(c,"start","("+player+"I)Z","start");redirect(c,"tick","("+player+")V","fallback");redirect(c,"pause",pause,"pause");
            redirect(c,"defeated","(Ldev/shadowsovereign/mythic/MythicBeast;Ljava/util/UUID;)Z","defeated");
        }
        if(c.name.endsWith("/MythicExpedition")||c.name.endsWith("/MythicService"))for(var m:c.methods)if(m.name.equals("spawn"))m.access=(m.access&~ACC_PRIVATE)|ACC_PUBLIC;
        if(c.name.endsWith("/GloomEvents"))redirect(c,"join","(Lnet/minecraftforge/event/entity/EntityJoinLevelEvent;)V","join");
        if(c.name.endsWith("/MythicBeast"))for(var m:c.methods)if(m.name.equals("allowed"))for(var n:m.instructions)if(n instanceof MethodInsnNode mi&&mi.owner.equals(ROOT+"mythic/MythicService")&&mi.name.equals("session")){mi.owner=OPEN;mi.name="combatView";}
        if(c.name.endsWith("/MythicEvents"))for(var m:c.methods)if(m.name.equals("join"))for(var n:m.instructions.toArray())if(n instanceof FieldInsnNode f&&f.getOpcode()==GETFIELD&&f.owner.equals(ROOT+"adventure/AdventureData$Journal")&&f.name.equals("session")){
            m.instructions.set(n,new MethodInsnNode(INVOKESTATIC,OPEN,"liveSession","(Ldev/shadowsovereign/adventure/AdventureData$Journal;)Ldev/shadowsovereign/adventure/AdventureData$Session;",false));
        }
        if(c.name.endsWith("/ExpanseWorld"))for(var m:c.methods)if(m.name.equals("editable"))for(var n:m.instructions.toArray())if(n instanceof MethodInsnNode mi){
            if(mi.name.equals("protectedSite")&&Set.of(ROOT+"mythic/MythicSites",ROOT+"covenant/CovenantWorld",ROOT+"monolith/MonolithWorld").contains(mi.owner)){
                var l=new InsnList();l.add(new InsnNode(POP2));l.add(new InsnNode(ICONST_0));m.instructions.insertBefore(n,l);m.instructions.remove(n);
            }else if(mi.owner.equals(ROOT+"gloom/GloomRealm")&&mi.name.equals("editable")){mi.owner=OPEN;mi.name="editableGloom";}
        }
    }
    static void defaultCall(MethodNode m,MethodInsnNode mi){
        var l=new InsnList();var args=Type.getArgumentTypes(mi.desc);
        for(int i=args.length-1;i>=0;i--)l.add(new InsnNode(args[i].getSize()==2?POP2:POP));
        if(mi.getOpcode()!=INVOKESTATIC)l.add(new InsnNode(POP));
        var type=Type.getReturnType(mi.desc);
        if(type.getSort()!=Type.VOID)l.add(new InsnNode(switch(type.getSort()){
            case Type.BOOLEAN,Type.BYTE,Type.CHAR,Type.SHORT,Type.INT->ICONST_0;case Type.FLOAT->FCONST_0;case Type.DOUBLE->DCONST_0;case Type.LONG->LCONST_0;default->ACONST_NULL;}));
        m.instructions.insertBefore(mi,l);m.instructions.remove(mi);
    }
    static byte[] retireKnight(byte[] bytes,String name)throws Exception{
        String text=new String(bytes,java.nio.charset.StandardCharsets.ISO_8859_1);
        boolean special=Set.of(ROOT+"army/ArmyData",ROOT+"adventure/AdventureData",ROOT+"mythic/MythicSites",ROOT+"mythic/MythicContent").contains(name);
        if(!special && !text.contains("knighthaven/") && !text.contains("villager_knight") && !text.contains("KNIGHT_FLAGS") && !text.contains("knightFlags"))return bytes;
        var c=new ClassNode();new ClassReader(bytes).accept(c,0);
        for(var m:c.methods){
            for(var n:m.instructions.toArray()){
                if(n instanceof MethodInsnNode mi && mi.owner.startsWith(ROOT+"knighthaven/") && !mi.owner.endsWith("KnighthavenCombat"))defaultCall(m,mi);
                if(n instanceof FieldInsnNode f && f.getOpcode()==GETSTATIC && f.owner.startsWith(ROOT+"knighthaven/"))m.instructions.set(n,new InsnNode(ACONST_NULL));
                if(n instanceof LdcInsnNode ldc && ldc.cst instanceof String s && s.contains("villager_knight"))ldc.cst=s.replace("villager_knight","removed_companion");
                if(n instanceof IntInsnNode integer && integer.operand==34 && Set.of(ROOT+"mythic/MythicBeast",ROOT+"mythic/MythicContent",ROOT+"client/MythicBeastRenderer").contains(name)){
                    var after=n.getNext();while(after!=null&&after.getOpcode()<0)after=after.getNext();
                    if(after instanceof JumpInsnNode jump && (jump.getOpcode()==IF_ICMPNE || jump.getOpcode()==IF_ICMPEQ))m.instructions.set(n,new LdcInsnNode(Integer.MIN_VALUE));
                }
            }
            if(m.name.equals("load") && m.desc.startsWith("(Lnet/minecraft/nbt/CompoundTag;") && (name.endsWith("/ArmyData")||name.endsWith("/AdventureData"))){
                var before=new InsnList();before.add(new VarInsnNode(ALOAD,0));call(before,INVOKESTATIC,ROOT+"expansion/RemovedContent",name.endsWith("/ArmyData")?"army":"adventures","(Lnet/minecraft/nbt/CompoundTag;)V");m.instructions.insert(before);
            }
            if(name.endsWith("/MythicSites") && m.name.equals("dimension")){
                m.instructions.clear();m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();
                m.instructions.add(new FieldInsnNode(GETSTATIC,ROOT+"odyssey/OdysseyRealm","KEY","Lnet/minecraft/resources/ResourceKey;"));m.instructions.add(new InsnNode(ARETURN));
            }
        }
        var writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);
        var mappings=new HashMap<String,String>();
        mappings.put(ROOT+"knighthaven/KnighthavenCombat",ROOT+"expansion/CrystalCombat");
        mappings.put(ROOT+"mythic/MythicBeast.KNIGHT_FLAGS","COMPANION_FLAGS");
        mappings.put(ROOT+"mythic/MythicBeast.knightFlags()I","companionFlags");
        c.accept(new ClassRemapper(writer,new SimpleRemapper(mappings)));
        var checked=new ClassNode();new ClassReader(writer.toByteArray()).accept(checked,0);
        for(var m:checked.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<>(new BasicVerifier()).analyze(checked.name,m);
        return writer.toByteArray();
    }
    public static void main(String[] args)throws Exception{
        Path base=Path.of(args[0]),resources=Path.of(args[1]),output=Path.of(args[2]);
        JsonObject spec=JsonParser.parseString(Files.readString(resources.resolve("integration-spec.json"))).getAsJsonObject();
        String hash=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(base)));
        if(!hash.equals(spec.get("base_sha256").getAsString()))throw new IllegalArgumentException("Base JAR hash mismatch");
        if(!hash.equals("0bb69723f4e2f7327f02f8aa296a0dc64629b750423bccb90980c452d561af42"))throw new IllegalArgumentException("Unsupported original mod");
        var entries=new TreeMap<String,byte[]>();try(var z=new ZipFile(base.toFile())){var en=z.entries();while(en.hasMoreElements()){var e=en.nextElement();if(!e.isDirectory())entries.put(e.getName(),z.getInputStream(e).readAllBytes());}}
        entries.put(ROOT+"expansion/TidewoodSapling.class",tidewoodSapling(entries.get(GLOOM+"$DuskSapling.class")));
        entries.put(ROOT+"expansion/AquaticPlant.class",blockSubclass(ROOT+"expansion/AquaticPlant","net/minecraft/world/level/block/SeagrassBlock"));
        entries.put(ROOT+"expansion/HangingRoots.class",blockSubclass(ROOT+"expansion/HangingRoots","net/minecraft/world/level/block/HangingRootsBlock"));
        entries.put(ROOT+"expansion/CompanionFloatGoal.class",floatGoal());
        entries.put(ROOT+"expansion/DecorationBlock.class",blockSubclass(ROOT+"expansion/DecorationBlock","net/minecraft/world/level/block/GlazedTerracottaBlock"));
        for(String name:List.of(PROFILES,ROOT+"mythic/MythicExpedition",GLOOM,ROOT+"mythic/MythicSpawn",ROOT+"mythic/MythicBeast",ROOT+"mythic/MythicService",ROOT+"mythic/MythicEvents",ROOT+"gloom/GloomEvents",ROOT+"expanse/ExpanseWorld")){
            var c=new ClassNode();new ClassReader(entries.get(name+".class")).accept(c,0);
            if(name.equals(PROFILES))addProfiles(c,spec.getAsJsonArray("profiles"));
            else if(name.equals(GLOOM))addBlocks(c,spec.getAsJsonArray("blocks"));
            else if(name.endsWith("MythicSpawn"))aquaticSpawn(c);
            else if(name.endsWith("MythicBeast"))aquaticAir(c);
            else if(name.endsWith("MythicExpedition"))addIds(c,spec.getAsJsonArray("profiles"));
            openEncounters(c);
            var writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);byte[] bytes=writer.toByteArray();
            // BasicVerifier checks all control-flow stack/local types without
            // requiring a Minecraft classloader. It is not a game startup test.
            var checked=new ClassNode();new ClassReader(bytes).accept(checked,0);
            for(var m:checked.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<>(new BasicVerifier()).analyze(checked.name,m);
            entries.put(name+".class",bytes);
        }
        // Exact protocol match prevents old clients joining a server whose
        // entity registry and bestiary have been extended.
        String network=ROOT+"network/ShadowNetwork.class";var c=new ClassNode();new ClassReader(entries.get(network)).accept(c,0);
        for(var f:c.fields)if(f.name.equals("VERSION"))f.value="35-learning-paths";
        for(var m:c.methods)for(var ins:m.instructions){if(ins instanceof LdcInsnNode ldc && "29".equals(ldc.cst))ldc.cst="35-learning-paths";}
        var w=new ClassWriter(0);c.accept(w);entries.put(network,w.toByteArray());
        for(var entry:new ArrayList<>(entries.entrySet()))if(entry.getKey().endsWith(".class")){
            String className=entry.getKey().replace(".class","");
            if(className.startsWith(ROOT+"knighthaven/")&&!className.endsWith("/KnighthavenCombat")){entries.remove(entry.getKey());continue;}
            byte[] changed=retireKnight(entry.getValue(),className);
            if(className.endsWith("/KnighthavenCombat")){
                var relocated=new ClassNode();new ClassReader(changed).accept(relocated,0);relocated.sourceFile="CrystalCombat.java";
                var cw=new ClassWriter(0);relocated.accept(cw);entries.remove(entry.getKey());entries.put(ROOT+"expansion/CrystalCombat.class",cw.toByteArray());
            }else entries.put(entry.getKey(),changed);
        }
        for(var removed:spec.getAsJsonArray("removed_resources"))entries.remove(removed.getAsString());
        try(var paths=Files.walk(resources)){for(var path:paths.filter(Files::isRegularFile).toList()){
            String name=resources.relativize(path).toString().replace(File.separatorChar,'/');
            if(!name.equals("integration-spec.json"))entries.put(name,Files.readAllBytes(path));}}
        String metadata=new String(entries.get("META-INF/mods.toml"),java.nio.charset.StandardCharsets.UTF_8).replace("version=\"1.2.0\"","version=\"1.6.0-learning-paths-preview\"");
        entries.put("META-INF/mods.toml",metadata.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Files.createDirectories(output.toAbsolutePath().getParent());
        try(var zip=new ZipOutputStream(Files.newOutputStream(output))){for(var entry:entries.entrySet()){var e=new ZipEntry(entry.getKey());e.setTime(0);zip.putNextEntry(e);zip.write(entry.getValue());zip.closeEntry();}}
        System.out.println("Wrote "+output+" ("+entries.size()+" entries); patched method stack verification passed.");
    }
}
