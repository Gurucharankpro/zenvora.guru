/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ProjectileWeaponItem
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.mastery;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mastery.MasteryCatalog;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.MythicSites;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.sentinel.MasteryRewards;
import dev.shadowsovereign.soulforge.CompanionInventory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.registries.ForgeRegistries;

public final class MasteryService {
    private static String key(String metric) {
        return "m." + metric.replace(':', '.');
    }

    private static void proof(ServerPlayer p, String metric, long value) {
        OdysseyState s = GuideService.state(p);
        long before = s.count(MasteryService.key(metric));
        if (value > before) {
            s.observe(MasteryService.key(metric), value);
            GuideService.dirty(p);
        }
    }

    private static boolean weapon(ItemStack s) {
        return !s.m_41619_() && (s.m_41720_() instanceof ProjectileWeaponItem || s.m_41638_(EquipmentSlot.MAINHAND).get((Object)Attributes.f_22281_).stream().anyMatch(v -> v.m_22218_() > 0.0));
    }

    public static boolean claimed(ServerPlayer p, String id) {
        return GuideService.state((ServerPlayer)p).grants.contains("mastery:" + id);
    }

    public static boolean available(ServerPlayer p, MasteryCatalog.Stage s) {
        return s.previous().isEmpty() || MasteryService.claimed(p, s.previous());
    }

    public static void located(ServerPlayer p, String structure) {
        MasteryService.proof(p, "located.structure." + structure.substring(structure.indexOf(58) + 1), 1L);
    }

    public static void observe(ServerPlayer p) {
        Object id;
        ArmyData.Army a = GuideService.army(p);
        OdysseyState state = GuideService.state(p);
        ArrayList<ShadowRecord> records = new ArrayList<ShadowRecord>(a.shadows.values());
        records.addAll(LivingRealm.state((ServerPlayer)p).pending.values());
        MasteryService.proof(p, "souls", records.size());
        MasteryService.proof(p, "active", a.activeCount());
        MasteryService.proof(p, "types", records.stream().map(r -> r.type).distinct().count());
        MasteryService.proof(p, "companies", Arrays.stream(a.royal.teams).filter(t -> !t.members.isEmpty()).count());
        MasteryService.proof(p, "realm", OdysseyRealm.inside(p) ? 1L : state.count("realm_enter"));
        p.m_9236_().m_204166_(p.m_20183_()).m_203543_().ifPresent(k -> {
            String metric = "biome." + k.m_135782_().toString().replace(':', '.');
            if (MasteryCatalog.STAGES.stream().anyMatch(stage -> stage.metric().equals(metric))) {
                MasteryService.proof(p, metric, 1L);
            }
        });
        HashMap<Object, Integer> counts = new HashMap<Object, Integer>();
        for (ItemStack stack : p.m_150109_().f_35974_) {
            if (stack.m_41619_() || (id = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_())) == null) continue;
            counts.merge("item." + id.toString().replace(':', '.'), stack.m_41613_(), Integer::sum);
        }
        counts.forEach((k, v) -> {
            if (MasteryCatalog.STAGES.stream().anyMatch(stage -> stage.metric().equals(k))) {
                MasteryService.proof(p, k, v.intValue());
            }
        });
        for (ShadowRecord r2 : records) {
            id = r2.type.substring(r2.type.indexOf(58) + 1);
            r2.inventory.initialize(r2.snapshot);
            MasteryService.proof(p, "level", r2.level());
            MasteryService.proof(p, "kills", r2.totalKills);
            MasteryService.proof(p, "bond", Math.max(r2.bond, r2.life.affection));
            MasteryService.proof(p, "fed", r2.life.meals > 0 || r2.nextCareAt > 0L ? 1L : 0L);
            int weapon = MasteryService.weapon((ItemStack)r2.inventory.items.get(0)) ? 1 : 0;
            boolean armor = false;
            for (int slot = 2; slot < 6; ++slot) {
                if (!((ItemStack)r2.inventory.items.get(slot)).m_41638_(CompanionInventory.SLOTS[slot]).get((Object)Attributes.f_22284_).stream().anyMatch(v -> v.m_22218_() > 0.0)) continue;
                armor = true;
            }
            boolean storage = r2.inventory.items.subList(6, 11).stream().anyMatch(s -> !s.m_41619_());
            MasteryService.proof(p, "weapon", weapon != 0 ? 1L : 0L);
            MasteryService.proof(p, "armor", armor ? 1L : 0L);
            MasteryService.proof(p, "storage", storage ? 1L : 0L);
            if (!r2.type.equals("shadowsovereign:" + (String)id) || !Arrays.asList(MasteryCatalog.CREATURES).contains(id)) continue;
            MasteryService.proof(p, "owned." + (String)id, 1L);
            MasteryService.proof(p, "weapon." + (String)id, weapon != 0 ? 1L : 0L);
            MasteryService.proof(p, "kills." + (String)id, r2.totalKills);
            MasteryService.proof(p, "bond." + (String)id, Math.max(r2.bond, r2.life.affection));
            MasteryService.proof(p, "legendlevel", r2.level());
            if (r2.activeEntity == null || ShadowService.active(p.f_8924_, r2) == null) continue;
            MasteryService.proof(p, "summoned." + (String)id, 1L);
            MasteryService.proof(p, "legendactive", 1L);
        }
        int obtained = 0;
        int locations = 0;
        for (String id2 : MasteryCatalog.CREATURES) {
            CompoundTag tag;
            int kind = Arrays.asList(MythicExpedition.IDS).indexOf(id2);
            boolean owned = kind < 0 ? MythicService.state((ServerPlayer)p).status == 2 : MythicExpedition.state(p, kind).m_128451_("Status") == 2;
            MythicSites.Site site = MythicSites.sites().stream().filter(v -> v.kind() == kind).findFirst().orElse(null);
            CompoundTag compoundTag = tag = kind < 0 ? MythicService.state((ServerPlayer)p).gloom : MythicExpedition.state(p, kind);
            if (owned) {
                MasteryService.proof(p, "owned." + id2, 1L);
            }
            if (MasteryService.value(p, "owned." + id2) > 0L) {
                ++obtained;
                MasteryService.proof(p, "located." + id2, 1L);
                MasteryService.proof(p, "visited." + id2, 1L);
            }
            if (site != null && MasteryService.value(p, "located.structure." + site.structure().m_135815_()) > 0L) {
                MasteryService.proof(p, "located." + id2, 1L);
            }
            if (!tag.m_128441_("NaturalAltar") && (!OdysseyRealm.inside(p) || !(kind < 0 ? MythicService.state((ServerPlayer)p).generated && MythicService.near(p, 24) : tag.m_128441_("Lair") && MythicExpedition.near(p, kind, 24)))) continue;
            MasteryService.proof(p, "visited." + id2, 1L);
        }
        for (ItemStack s2 : p.m_150109_().f_35974_) {
            if (!s2.m_41782_() || s2.m_41783_().m_128461_("AtlasTarget").isEmpty()) continue;
            MasteryService.located(p, s2.m_41783_().m_128461_("AtlasTarget"));
        }
        for (String k2 : state.events.keySet()) {
            if (!k2.startsWith("m.located.structure.") || state.count(k2) <= 0L) continue;
            ++locations;
        }
        MasteryService.proof(p, "legends", obtained);
        MasteryService.proof(p, "locations", locations);
    }

    public static long value(ServerPlayer p, String metric) {
        if (metric.startsWith("event.")) {
            return GuideService.state(p).count(metric.substring(6));
        }
        if (metric.equals("coreclaims")) {
            return MasteryCatalog.STAGES.stream().filter(s -> !s.chapter().startsWith("hunt.") && !s.chapter().equals("graduation") && MasteryService.claimed(p, s.id())).count();
        }
        if (metric.equals("huntclaims")) {
            return MasteryCatalog.STAGES.stream().filter(s -> s.chapter().startsWith("hunt.") && MasteryService.claimed(p, s.id())).count();
        }
        return GuideService.state(p).count(MasteryService.key(metric));
    }

    public static long value(ServerPlayer p, MasteryCatalog.Stage s) {
        return s.metric().equals("lesson") ? GuideService.state(p).count("m.read." + s.id()) : MasteryService.value(p, s.metric());
    }

    public static boolean read(ServerPlayer p, String id) {
        MasteryCatalog.Stage s = MasteryCatalog.get(id);
        if (s == null || !s.metric().equals("lesson") || !MasteryService.available(p, s)) {
            return false;
        }
        GuideService.state(p).observe("m.read." + id, 1L);
        GuideService.dirty(p);
        return true;
    }

    public static boolean claim(ServerPlayer p, String id) {
        MasteryCatalog.Stage s = MasteryCatalog.get(id);
        if (s == null || !MasteryService.available(p, s) || MasteryService.value(p, s) < (long)s.target() || !GuideService.state(p).grant("mastery:" + id)) {
            return false;
        }
        OdysseyState state = GuideService.state(p);
        state.seals = Math.min(1000000, state.seals + 1);
        MasteryRewards.enqueue(p, s);
        GuideService.dirty(p);
        return true;
    }

    public static int claimReady(ServerPlayer p) {
        MasteryService.observe(p);
        int n = 0;
        for (MasteryCatalog.Stage s : MasteryCatalog.STAGES) {
            if (!MasteryService.claim(p, s.id())) continue;
            ++n;
        }
        return n;
    }

    public static CompoundTag view(ServerPlayer p) {
        GuideService.importEvidence(p);
        CompoundTag out = new CompoundTag();
        ListTag list = new ListTag();
        int done = 0;
        String next = "";
        for (MasteryCatalog.Stage s : MasteryCatalog.STAGES) {
            CompoundTag n = new CompoundTag();
            boolean claimed = MasteryService.claimed(p, s.id());
            boolean open = MasteryService.available(p, s);
            n.m_128359_("Id", s.id());
            n.m_128379_("Done", claimed);
            n.m_128379_("Open", open);
            n.m_128356_("Value", Math.min((long)s.target(), MasteryService.value(p, s)));
            list.add((Object)n);
            if (claimed) {
                ++done;
                continue;
            }
            if (!open || !next.isEmpty()) continue;
            next = s.id();
        }
        out.m_128365_("Stages", (Tag)list);
        out.m_128405_("Completed", done);
        out.m_128405_("Total", MasteryCatalog.STAGES.size());
        out.m_128359_("Next", next);
        out.m_128405_("Seals", GuideService.state((ServerPlayer)p).seals);
        out.m_128359_("Pinned", GuideService.state((ServerPlayer)p).camp.m_128461_("MasteryPin"));
        MasteryRewards.reconcile(p);
        out.m_128405_("Mail", MasteryRewards.pending(p));
        out.m_128405_("Legends", (int)MasteryService.value(p, "legends"));
        return out;
    }
}

