import dev.shadowsovereign.expansion.OpenMythics;
import dev.shadowsovereign.adventure.*;
import dev.shadowsovereign.army.*;
import dev.shadowsovereign.mythic.*;
import dev.shadowsovereign.realm.WildBosses;
import net.minecraft.server.*;
import net.minecraft.server.level.*;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

/** Executes the production helper against explicit fake world/registry APIs.
 * It tests state transitions, not Minecraft engine or Forge API compatibility. */
public class OpenMythicsTest {
    static int checks;
    static void check(boolean condition,String behavior){if(!condition)throw new AssertionError(behavior);checks++;}
    static ServerPlayer player(){var p=new ServerPlayer();p.f_8924_=new MinecraftServer();p.server=p.f_8924_;p.level=new ServerLevel();p.f_8924_.players.players.put(p.id,p);return p;}
    static void habitat(ServerPlayer p,int k){p.nearKinds.add(k);MythicExpedition.state(p,k).m_128405_("Lair",1);}
    static AdventureData.Session session(AdventureData.Kind kind,AdventureData.Phase phase){var s=new AdventureData.Session();s.kind=kind;s.phase=phase;return s;}
    public static void main(String[] args){
        var p=player();habitat(p,43);habitat(p,44);habitat(p,45);habitat(p,46);
        var j=AdventureService.journal(p);var first=MythicExpedition.state(p,43);
        j.session=session(AdventureData.Kind.MYTHIC_LAIR,AdventureData.Phase.PAUSED);
        first.m_128405_("Status",1);
        check(OpenMythics.clearLegacy(j)&&j.session==null,"old PAUSED encounter unlocks");
        check(first.m_128451_("Status")==1,"unlock does not grant a soul");
        j.session=session(AdventureData.Kind.MYTHIC_LAIR,AdventureData.Phase.ACTIVE);
        check(OpenMythics.start(p,43)&&j.session==null,"starting migrates legacy exclusive state");
        var boss=MythicExpedition.body(p,43);boss.hp=37;
        check(OpenMythics.start(p,44)&&!boss.removed,"another boss starts while first is alive");
        check(MythicExpedition.body(p,44)!=null,"second boss has independent live body");
        int spawned=MythicExpedition.spawned;
        check(OpenMythics.start(p,43)&&MythicExpedition.spawned==spawned&&boss.hp==37,"same boss is reused without duplication/healing");
        var oldView=OpenMythics.combatView(p);var ally=new ShadowRecord();ally.body=new net.minecraft.world.entity.Mob();ally.body.level=p.level;
        ArmyData.get(p.f_8924_).army(p.id).shadows.put(ally.id,ally);
        check(!oldView.participants.contains(ally.id)&&OpenMythics.combatView(p).participants.contains(ally.id),"new companions join combat without fixed party");
        check(j.session==null,"combat view never creates an exclusive session");
        p.x=100;p.f_19797_=10;OpenMythics.tick(p);
        check(j.session==null&&!boss.removed&&boss.hp==37&&boss.target==null,"walking away leaves boss alive without PAUSED lock or health reset");
        check(OpenMythics.start(p,45),"escaping first encounter does not block a third creature");
        first.m_128362_("Active",java.util.UUID.randomUUID());MythicExpedition.bodies.get(p.id).remove(43);
        spawned=MythicExpedition.spawned;
        check(!OpenMythics.start(p,43)&&spawned==MythicExpedition.spawned,"unloaded recorded UUID cannot create a duplicate");
        check(OpenMythics.start(p,46),"unloaded different boss does not block other kinds");
        var other=session(AdventureData.Kind.WARFRONT,AdventureData.Phase.ACTIVE);j.session=other;
        OpenMythics.pause(p.f_8924_,p.id);
        check(j.session==other,"non-mythic adventure survives logout/pause cleanup");
        var body=MythicExpedition.body(p,44);var join=new EntityJoinLevelEvent(body);OpenMythics.join(join);
        check(!join.canceled&&!body.removed,"recorded boss reloads without matching exclusive session");
        var impostor=new MythicBeast();impostor.kind=44;impostor.server=p.f_8924_;impostor.level=p.level;impostor.data.m_128362_("SoulboundMythicHost",p.id);
        var duplicate=new EntityJoinLevelEvent(impostor);OpenMythics.join(duplicate);
        check(duplicate.canceled&&impostor.removed,"stale or duplicate entity UUID is rejected");
        int rewards=WildBosses.captured;
        check(!OpenMythics.defeated(body,java.util.UUID.randomUUID())&&WildBosses.captured==rewards,"wrong killer cannot claim owned encounter");
        check(OpenMythics.defeated(body,p.id)&&WildBosses.captured==rewards+1,"kill grants reward without exclusive session");
        check(j.session==other,"reward leaves unrelated adventure intact");
        check(!OpenMythics.defeated(body,p.id)&&WildBosses.captured==rewards+1,"same death cannot grant a second soul");
        check(!OpenMythics.start(p,44),"acquired creature cannot respawn as unearned guardian");
        var fourth=MythicExpedition.body(p,46);p.f_8924_.players.players.remove(p.id);
        check(OpenMythics.defeated(fourth,p.id),"offline-owner reward path remains supported");
        p.f_8924_.players.players.put(p.id,p);j.mythic.generated=true;
        check(OpenMythics.startDevourer(p)&&j.session==other,"Soul Devourer also starts without taking exclusive session");
        check(OpenMythics.liveSession(j)!=null,"Devourer entity and projectiles have local validity view");
        p.nearKinds.add(-1);check(!OpenMythics.start(p,-1)&&!OpenMythics.start(p,999),"invalid kind boundaries rejected");
        habitat(p,34);check(OpenMythics.start(p,34),"compacted slot 34 remains available to a surviving creature");
        p.spectator=true;check(!OpenMythics.start(p,45),"spectator cannot trigger encounter");p.spectator=false;
        var pos=new BlockPos(0,0,0);
        check(OpenMythics.editableGloom(p,pos),"owned Gloom area editable");p.foreign=true;
        check(!OpenMythics.editableGloom(p,pos),"foreign player plot remains protected");p.foreign=false;p.inside=false;
        check(!OpenMythics.editableGloom(p,pos),"edit exception does not apply outside realm");
        check(OpenMythics.cancel(p)&&!boss.removed&&j.session==other,"cancel does not delete bodies or unrelated adventure");
        var royal=player();habitat(royal,47);habitat(royal,45);
        check(OpenMythics.start(royal,47),"new final-index king starts in his habitat");
        var king=MythicExpedition.body(royal,47);king.hp=63;
        royal.x=100;royal.f_19797_=10;OpenMythics.tick(royal);
        check(king.target==null&&king.hp==63&&!king.removed&&AdventureService.journal(royal).session==null,"escape preserves king without exclusive lock");
        check(OpenMythics.start(royal,45),"Refai starts while escaped king stays alive");
        royal.x=0;royal.f_19797_=20;OpenMythics.tick(royal);
        check(king.target==royal,"king resumes ordinary targeting on return");
        var kingReload=new EntityJoinLevelEvent(king);OpenMythics.join(kingReload);
        check(!kingReload.canceled,"king reload validates per-creature UUID");
        check(OpenMythics.defeated(king,royal.id)&&!OpenMythics.defeated(king,royal.id),"king grants capture only once");
        habitat(royal,48);check(OpenMythics.start(royal,48),"acrobat starts independently");
        check(OpenMythics.defeated(MythicExpedition.body(royal,48),royal.id),"acrobat grants native capture");
        System.out.println("PASS: "+checks+" open-encounter behavioral assertions against explicit ABI/world fixtures; Forge runtime is not exercised.");
    }
}
