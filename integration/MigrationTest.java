import dev.shadowsovereign.expansion.*;
import net.minecraft.nbt.*;
import net.minecraftforge.registries.MissingMappingsEvent;

public class MigrationTest {
    static int checks;
    static void check(boolean value,String message){if(!value)throw new AssertionError(message);checks++;}
    static CompoundTag record(String type){var n=new CompoundTag();n.m_128359_("Type",type);return n;}
    static ListTag list(Tag... values){var l=new ListTag();java.util.Collections.addAll(l,values);return l;}
    public static void main(String[] args){
        String retired="shadowsovereign:villager_knight",keep="shadowsovereign:refai_el_desouky";
        var knight=record(retired);var survivor=record(keep);survivor.m_128405_("TotalKills",71);
        var soul=new CompoundTag();soul.m_128365_("Companion",record(retired));
        var otherSoul=new CompoundTag();otherSoul.m_128365_("Companion",record(keep));
        var owner=new CompoundTag();owner.m_128365_("Shadows",list(knight,survivor));
        owner.m_128365_("Souls",list(soul,otherSoul));owner.m_128365_("ChosenTypes",list(new StringTag(retired),new StringTag(keep)));
        var root=new CompoundTag();root.m_128365_("Armies",list(owner));RemovedContent.army(root);
        check(owner.m_128437_("Shadows",10).size()==1,"retired army record removed");
        check(owner.m_128437_("Shadows",10).get(0)==survivor&&survivor.m_128451_("TotalKills")==71,"surviving record and progress untouched");
        check(owner.m_128437_("Souls",10).equals(list(otherSoul)),"only retired soul offer removed");
        check(owner.m_128437_("ChosenTypes",8).size()==1&&owner.m_128437_("ChosenTypes",8).get(0).m_7916_().equals(keep),"chosen types cleaned narrowly");
        RemovedContent.army(root);check(owner.m_128437_("Shadows",10).size()==1,"army migration idempotent");
        var gloom=new CompoundTag();gloom.m_128365_("villager_knight",new CompoundTag());gloom.m_128365_("refai_el_desouky",survivor);
        var mythic=new CompoundTag();mythic.m_128365_("Gloom",gloom);
        var bestiary=new CompoundTag();bestiary.m_128365_(retired,knight);bestiary.m_128365_(keep,survivor);
        var realm=new CompoundTag();realm.m_128365_("Bestiary",bestiary);realm.m_128365_("Pending",list(knight,survivor));
        var journal=new CompoundTag();journal.m_128365_("Mythic",mythic);journal.m_128365_("LivingRealm",realm);
        var adventure=new CompoundTag();adventure.m_128365_("Journals",list(journal));RemovedContent.adventures(adventure);
        check(!gloom.m_128441_("villager_knight")&&gloom.m_128441_("refai_el_desouky"),"only retired journal state removed");
        check(realm.m_128437_("Pending",10).equals(list(survivor)),"pending rewards cleaned narrowly");
        check(!bestiary.m_128441_(retired)&&bestiary.m_128441_(keep),"bestiary preserves other creatures");
        var event=new MissingMappingsEvent();
        var entity=new MissingMappingsEvent.Mapping<>("shadowsovereign","villager_knight");
        var egg=new MissingMappingsEvent.Mapping<>("shadowsovereign","villager_knight_spawn_egg");
        var sound=new MissingMappingsEvent.Mapping<>("shadowsovereign","knighthaven_knight_idle");
        var foreign=new MissingMappingsEvent.Mapping<>("other","villager_knight");
        var other=new MissingMappingsEvent.Mapping<>("shadowsovereign","frostbound_snow_leopard");
        java.util.Collections.addAll(event.mappings,entity,egg,sound,foreign,other);MigrationHooks.missing(event);
        check(entity.ignored&&egg.ignored&&sound.ignored,"known removed registry identities ignored");
        check(!foreign.ignored&&!other.ignored,"unrelated missing mappings untouched");
        System.out.println("PASS: "+checks+" retirement/missing-mapping assertions against explicit API/NBT fixtures.");
    }
}
