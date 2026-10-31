package dev.shadowsovereign.expansion;

import net.minecraft.nbt.*;

/** Narrow old-save cleanup. Registry identities of all surviving creatures stay
 * unchanged; only the explicitly retired type is removed from stored armies. */
public final class RemovedContent {
    private static final String TYPE="shadowsovereign:villager_knight";
    private RemovedContent() {}
    private static boolean retired(Tag value) {
        return value instanceof CompoundTag row && TYPE.equals(row.m_128461_("Type"));
    }
    public static void army(CompoundTag root) {
        for(Tag value:root.m_128437_("Armies",10)){
            CompoundTag owner=(CompoundTag)value;
            owner.m_128437_("Shadows",10).removeIf(RemovedContent::retired);
            owner.m_128437_("Souls",10).removeIf(t->t instanceof CompoundTag row && retired(row.m_128469_("Companion")));
            owner.m_128437_("ChosenTypes",8).removeIf(t->TYPE.equals(t.m_7916_()));
        }
    }
    public static void adventures(CompoundTag root) {
        for(Tag value:root.m_128437_("Journals",10)){
            CompoundTag journal=(CompoundTag)value;
            journal.m_128469_("Mythic").m_128469_("Gloom").m_128473_("villager_knight");
            CompoundTag realm=journal.m_128469_("LivingRealm");
            realm.m_128437_("Pending",10).removeIf(RemovedContent::retired);
            realm.m_128469_("Bestiary").m_128473_(TYPE);
        }
    }
}
