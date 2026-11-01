/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.item.ItemStack
 */
package dev.shadowsovereign.eclipse;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.royal.RoyalMutators;
import dev.shadowsovereign.soulforge.CompanionInventory;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.warfront.WarfrontService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public final class EclipseService {
    private EclipseService() {
    }

    public static EclipseState state(ServerPlayer p) {
        return ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).eclipse;
    }

    public static long now(ServerPlayer p) {
        return p.f_8924_.m_129783_().m_46467_();
    }

    public static void dirty(ServerPlayer p) {
        ArmyData.get(p.f_8924_).m_77762_();
    }

    public static boolean usable(ServerPlayer p) {
        return p.m_6084_() && !p.m_5833_() && !JourneyView.active(p);
    }

    public static ShadowRecord record(ServerPlayer p, UUID id) {
        return id == null ? null : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
    }

    public static boolean workshop(ServerPlayer p) {
        return WarfrontService.workshop(p);
    }

    public static boolean talent(ServerPlayer p, UUID id, int value) {
        ShadowRecord r = EclipseService.record(p, id);
        if (r == null || !EclipseService.workshop(p) || value < 0 || value > EclipseState.Talent.values().length) {
            return false;
        }
        EclipseState.Build b = EclipseService.state(p).build(id);
        if (value == EclipseState.Talent.values().length) {
            b.talents = 0;
            EclipseService.dirty(p);
            return true;
        }
        int bit = 1 << value;
        if ((b.talents & bit) != 0) {
            b.talents &= ~bit;
        } else {
            if (Integer.bitCount(b.talents) >= EclipseState.points(r.level())) {
                return false;
            }
            b.talents |= bit;
        }
        EclipseService.dirty(p);
        return true;
    }

    public static boolean role(ServerPlayer p, UUID id, int value) {
        ShadowRecord r = EclipseService.record(p, id);
        if (r == null || !EclipseService.workshop(p) || value < 0 || value >= EclipseState.Role.values().length) {
            return false;
        }
        if (value >= EclipseState.Role.ASSASSIN.ordinal() && EclipseService.state((ServerPlayer)p).wins < 1 && !ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).warfront.wonOnce) {
            return false;
        }
        Mob m = ShadowService.active(p.f_8924_, r);
        if (m != null && (m.m_5448_() != null || EclipseService.now(p) - r.lastDamageAt < 100L)) {
            return false;
        }
        EclipseService.state((ServerPlayer)p).build((UUID)id).role = EclipseState.Role.values()[value];
        EclipseService.dirty(p);
        return true;
    }

    public static boolean wardrobe(ServerPlayer p, UUID id, int palette, int seal, int frame) {
        ShadowRecord r = EclipseService.record(p, id);
        if (r == null || palette < 0 || palette >= 6 || seal < 0 || seal >= 4 || frame < 0 || frame >= 3 || (EclipseService.state((ServerPlayer)p).palettes & 1 << palette) == 0 || seal > Math.min(3, EclipseService.state((ServerPlayer)p).wins) || frame > Math.min(2, EclipseService.state((ServerPlayer)p).wins / 2)) {
            return false;
        }
        EclipseState.Build b = EclipseService.state(p).build(id);
        b.palette = palette;
        b.seal = seal;
        b.frame = frame;
        Mob mob = ShadowService.active(p.f_8924_, r);
        if (mob != null) {
            ShadowService.updateName(mob, r);
        }
        EclipseService.dirty(p);
        return true;
    }

    public static boolean backup(ServerPlayer p, UUID primary, UUID reserve) {
        ShadowRecord r = EclipseService.record(p, primary);
        ShadowRecord other = EclipseService.record(p, reserve);
        if (r == null) {
            return false;
        }
        if (reserve == null) {
            EclipseService.state((ServerPlayer)p).build((UUID)primary).backup = null;
            EclipseService.state((ServerPlayer)p).relays.remove(primary);
            EclipseService.dirty(p);
            return true;
        }
        if (other == null || primary.equals(reserve) || other.activeEntity != null || other.pendingRespawn() || EclipseService.state((ServerPlayer)p).builds.values().stream().anyMatch(b -> reserve.equals(b.backup))) {
            return false;
        }
        EclipseService.state((ServerPlayer)p).build((UUID)primary).backup = reserve;
        EclipseService.dirty(p);
        return true;
    }

    public static int group(ServerPlayer p, List<UUID> ids, int action) {
        if (!EclipseService.usable(p) || ids.isEmpty() || ids.size() > 24 || new HashSet<UUID>(ids).size() != ids.size() || ids.stream().anyMatch(id -> EclipseService.record(p, id) == null) || action < 0 || action > 4) {
            return 0;
        }
        ShadowNetwork.Action[] actions = new ShadowNetwork.Action[]{ShadowNetwork.Action.SUMMON, ShadowNetwork.Action.RECALL, ShadowNetwork.Action.FOLLOW, ShadowNetwork.Action.STAY, ShadowNetwork.Action.GUARD};
        int changed = 0;
        for (UUID id2 : ids) {
            changed += ShadowNetwork.perform(p, actions[action], id2);
        }
        return changed;
    }

    public static boolean saveGear(ServerPlayer p, UUID id, int slot) {
        ShadowRecord r = EclipseService.record(p, id);
        if (r == null || slot < 0 || slot > 2) {
            return false;
        }
        r.inventory.initialize(r.snapshot);
        Mob mob = ShadowService.active(p.f_8924_, r);
        if (mob != null) {
            CompanionService.captureEquipment(mob, r);
        }
        ListTag list = new ListTag();
        for (int i = 0; i < 6; ++i) {
            list.add((Object)((ItemStack)r.inventory.items.get(i)).m_41777_().m_41739_(new CompoundTag()));
        }
        EclipseService.state((ServerPlayer)p).build((UUID)id).gear[slot] = list;
        EclipseService.dirty(p);
        return true;
    }

    private static boolean matches(ItemStack a, ItemStack b) {
        if (a.m_41619_() || b.m_41619_()) {
            return a.m_41619_() && b.m_41619_();
        }
        ItemStack x = a.m_41777_();
        ItemStack y = b.m_41777_();
        if (x.m_41782_()) {
            x.m_41783_().m_128473_("Damage");
        }
        if (y.m_41782_()) {
            y.m_41783_().m_128473_("Damage");
        }
        return ItemStack.m_150942_((ItemStack)x, (ItemStack)y);
    }

    public static boolean applyGear(ServerPlayer p, UUID id, int slot) {
        int i;
        int i2;
        ShadowRecord r = EclipseService.record(p, id);
        if (r == null || slot < 0 || slot > 2 || !EclipseService.workshop(p) || AdventureService.busy(p.f_8924_, p.m_20148_(), id)) {
            return false;
        }
        ListTag templates = EclipseService.state((ServerPlayer)p).build((UUID)id).gear[slot];
        if (templates == null) {
            return false;
        }
        r.inventory.initialize(r.snapshot);
        Mob mob = ShadowService.active(p.f_8924_, r);
        if (mob != null) {
            CompanionService.captureEquipment(mob, r);
        }
        ArrayList<ItemStack> pool = new ArrayList<ItemStack>();
        for (i2 = 0; i2 < 6; ++i2) {
            pool.add(((ItemStack)r.inventory.items.get(i2)).m_41777_());
        }
        for (i2 = 0; i2 < 36; ++i2) {
            pool.add(p.m_150109_().m_8020_(i2).m_41777_());
        }
        ArrayList<ItemStack> equipped = new ArrayList<ItemStack>();
        for (int i3 = 0; i3 < 6; ++i3) {
            ItemStack wanted = ItemStack.m_41712_((CompoundTag)templates.m_128728_(i3));
            if (wanted.m_41619_()) {
                equipped.add(ItemStack.f_41583_);
                continue;
            }
            int found = -1;
            for (int j = 0; j < pool.size(); ++j) {
                if (!EclipseService.matches(wanted, (ItemStack)pool.get(j))) continue;
                found = j;
                break;
            }
            if (found < 0) {
                return false;
            }
            ItemStack actual = ((ItemStack)pool.get(found)).m_41620_(1);
            if (i3 >= 2 && !actual.canEquip(CompanionInventory.SLOTS[i3], (Entity)(mob == null ? p : mob))) {
                return false;
            }
            equipped.add(actual);
        }
        NonNullList bag = NonNullList.m_122780_((int)36, (Object)ItemStack.f_41583_);
        for (ItemStack item : pool) {
            if (EclipseService.insert((List<ItemStack>)bag, item.m_41777_()).m_41619_()) continue;
            return false;
        }
        for (i = 0; i < 36; ++i) {
            p.m_150109_().m_6836_(i, (ItemStack)bag.get(i));
        }
        for (i = 0; i < 6; ++i) {
            r.inventory.items.set(i, (Object)((ItemStack)equipped.get(i)));
            if (mob == null) continue;
            mob.m_8061_(CompanionInventory.SLOTS[i], (ItemStack)equipped.get(i));
        }
        p.m_150109_().m_6596_();
        p.f_36095_.m_38946_();
        EclipseService.dirty(p);
        return true;
    }

    public static ItemStack insert(List<ItemStack> into, ItemStack remaining) {
        for (int pass = 0; pass < 2 && !remaining.m_41619_(); ++pass) {
            for (int i = 0; i < into.size() && !remaining.m_41619_(); ++i) {
                ItemStack at = into.get(i);
                if (pass == 0 && !at.m_41619_() && ItemStack.m_150942_((ItemStack)at, (ItemStack)remaining)) {
                    int n = Math.min(remaining.m_41613_(), at.m_41741_() - at.m_41613_());
                    if (n <= 0) continue;
                    at.m_41769_(n);
                    remaining.m_41774_(n);
                    continue;
                }
                if (pass != 1 || !at.m_41619_()) continue;
                into.set(i, remaining.m_41620_(Math.min(remaining.m_41613_(), remaining.m_41741_())));
            }
        }
        return remaining;
    }

    public static void beginReport(ServerPlayer p, UUID session, String kind) {
        EclipseState e = EclipseService.state(p);
        if (session.equals(e.reporting)) {
            return;
        }
        if (e.reporting != null) {
            EclipseService.endReport(p, "interrupted");
        }
        e.reporting = session;
        e.reportStart = EclipseService.now(p);
        e.reportKind = kind;
        e.metrics.clear();
        EclipseService.dirty(p);
    }

    public static EclipseState.Metric metric(ServerPlayer p, UUID id) {
        EclipseState e = EclipseService.state(p);
        if (e.reporting == null || e.metrics.size() >= 24 && !e.metrics.containsKey(id)) {
            return null;
        }
        return e.metrics.computeIfAbsent(id, k -> new EclipseState.Metric());
    }

    public static void endReport(ServerPlayer p, String outcome) {
        EclipseState e = EclipseService.state(p);
        if (e.reporting == null) {
            return;
        }
        CompoundTag n = new CompoundTag();
        n.m_128362_("Id", e.reporting);
        n.m_128359_("Kind", e.reportKind);
        n.m_128359_("Outcome", outcome);
        n.m_128356_("Duration", Math.max(0L, EclipseService.now(p) - e.reportStart));
        ListTag list = new ListTag();
        e.metrics.forEach((id, m) -> {
            CompoundTag t = m.save((UUID)id);
            ShadowRecord r = EclipseService.record(p, id);
            t.m_128359_("Name", r == null ? "" : r.name);
            list.add((Object)t);
        });
        n.m_128365_("Members", (Tag)list);
        e.reports.add(0, n);
        while (e.reports.size() > 20) {
            e.reports.remove(20);
        }
        e.reporting = null;
        e.metrics.clear();
        EclipseService.dirty(p);
    }

    public static void died(ServerPlayer p, ShadowRecord r) {
        EclipseState e = EclipseService.state(p);
        EclipseState.Build b = e.builds.get(r.id);
        if (b != null && b.backup != null && AdventureService.journal((ServerPlayer)p).session == null) {
            e.relays.put(r.id, EclipseService.now(p) + 160L);
        }
    }

    public static void cancelRelay(ServerPlayer p, UUID id) {
        EclipseState e = EclipseService.state(p);
        if (id == null) {
            e.relays.clear();
            e.deployed.clear();
        } else {
            e.relays.remove(id);
            e.deployed.remove(id);
            e.relays.keySet().removeIf(k -> {
                EclipseState.Build b = e.builds.get(k);
                return b != null && id.equals(b.backup);
            });
        }
    }

    public static void tick(ServerPlayer p) {
        AdventureData.Session session;
        EclipseState e = EclipseService.state(p);
        long now = EclipseService.now(p);
        if (e.energyClock <= 0L || now < e.energyClock || now - e.energyClock > 40L) {
            e.energyClock = now;
        }
        if (now - e.energyClock >= 20L) {
            if (now - e.lastCast >= 60L) {
                e.energy = Math.min(100.0f, e.energy + (float)RoyalMutators.regeneration(p));
            }
            e.energyClock = now;
            EclipseService.dirty(p);
        }
        if ((session = AdventureService.journal((ServerPlayer)p).session) != null && session.phase != AdventureData.Phase.READY) {
            EclipseService.beginReport(p, session.id, session.kind.name());
        } else if (e.reporting != null) {
            EclipseService.endReport(p, session != null ? "victory" : "finished");
        }
        for (UUID id : List.copyOf(e.relays.keySet())) {
            ShadowRecord backup;
            ShadowRecord r = EclipseService.record(p, id);
            EclipseState.Build b = e.builds.get(id);
            ShadowRecord shadowRecord = backup = b == null ? null : EclipseService.record(p, b.backup);
            if (r == null || backup == null || session != null) {
                e.relays.remove(id);
                continue;
            }
            if (r.activeEntity != null) {
                if (e.deployed.remove(id)) {
                    ShadowService.recall(p, backup);
                }
                e.relays.remove(id);
                continue;
            }
            if (now >= r.respawnAt && e.deployed.contains(id)) {
                ShadowService.recall(p, backup);
                if (ShadowService.summon(p, r, false)) {
                    e.relays.remove(id);
                    e.deployed.remove(id);
                    continue;
                }
                ShadowService.summon(p, backup, false);
                continue;
            }
            if (now < e.relays.get(id) || !r.pendingRespawn() || backup.pendingRespawn() || backup.activeEntity != null || !p.m_6084_() || !ShadowService.summon(p, backup, false)) continue;
            e.deployed.add(id);
        }
        EclipsePowers.tick(p);
        e.marksUntil.entrySet().removeIf(x -> (Long)x.getValue() <= now);
    }

    public static void clearRuntime(ServerPlayer p) {
        EclipseState e = EclipseService.state(p);
        e.gateUntil = 0L;
        e.stormUntil = 0L;
        e.wellUntil = 0L;
        e.parryUntil = 0L;
        e.gateStart = null;
        e.storm = null;
        e.well = null;
        e.gateDestination = null;
        e.relays.clear();
        e.deployed.clear();
        e.marksUntil.clear();
        e.energyClock = EclipseService.now(p);
    }
}

