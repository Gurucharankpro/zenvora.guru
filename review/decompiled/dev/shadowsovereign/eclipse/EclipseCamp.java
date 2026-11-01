/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.Container
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.EntityTeleportEvent$EnderEntity
 */
package dev.shadowsovereign.eclipse;

import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseContent;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.eclipse.EclipseText;
import dev.shadowsovereign.eclipse.ScoutingFlare;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public final class EclipseCamp {
    private EclipseCamp() {
    }

    public static boolean unlocked(ServerPlayer p, EclipseState.Station kind) {
        int needed = switch (kind) {
            default -> throw new IncompatibleClassChangeError();
            case EclipseState.Station.VAULT -> 0;
            case EclipseState.Station.QUARTERMASTER, EclipseState.Station.ALCHEMY -> 1;
            case EclipseState.Station.MERCHANT, EclipseState.Station.GALLERY -> 2;
            case EclipseState.Station.WAYGATE -> 3;
        };
        return p.m_7500_() || EclipseService.state((ServerPlayer)p).wins >= needed;
    }

    public static boolean bind(ServerPlayer p, EclipseState.Station kind, BlockPos at) {
        EclipseState e = EclipseService.state(p);
        String dim = p.m_9236_().m_46472_().m_135782_().toString();
        if (!EclipseCamp.unlocked(p, kind)) {
            return false;
        }
        EclipseState.Site existing = e.sites.stream().filter(s -> s.pos().equals((Object)at) && s.dimension().equals(dim)).findFirst().orElse(null);
        if (existing != null && existing.kind() == kind) {
            return true;
        }
        if (existing == null && e.sites.size() >= 64 || kind == EclipseState.Station.WAYGATE && e.sites.stream().filter(s -> s.kind() == kind && (!s.pos().equals((Object)at) || !s.dimension().equals(dim))).count() >= 4L) {
            return false;
        }
        e.sites.removeIf(s -> s.pos().equals((Object)at) && s.dimension().equals(dim));
        e.sites.add(new EclipseState.Site(kind, at.m_7949_(), dim));
        EclipseService.dirty(p);
        return true;
    }

    public static void remove(Level level, BlockPos at) {
        ArmyData data = ArmyData.get(level.m_7654_());
        for (ArmyData.Army a : data.owners().values()) {
            if (!a.eclipse.sites.removeIf(s -> s.pos().equals((Object)at) && s.dimension().equals(level.m_46472_().m_135782_().toString()))) continue;
            data.m_77762_();
        }
    }

    public static boolean near(ServerPlayer p, EclipseState.Station kind) {
        return EclipseService.state((ServerPlayer)p).sites.stream().anyMatch(s -> {
            if (s.kind() != kind) return false;
            if (!s.dimension().equals(p.m_9236_().m_46472_().m_135782_().toString())) return false;
            if (!(p.m_20238_(Vec3.m_82512_((Vec3i)s.pos())) <= 64.0)) return false;
            if (!p.m_284548_().m_46805_(s.pos())) return false;
            if (!p.m_9236_().m_8055_(s.pos()).m_60713_((Block)EclipseContent.STATIONS.get((Object)kind).get())) return false;
            BlockEntity patt2085$temp = p.m_9236_().m_7702_(s.pos());
            if (!(patt2085$temp instanceof EclipseContent.CampEntity)) return false;
            EclipseContent.CampEntity be = (EclipseContent.CampEntity)patt2085$temp;
            if (!p.m_20148_().equals(be.owner)) return false;
            return true;
        });
    }

    public static boolean openVault(final ServerPlayer p) {
        if (!EclipseCamp.near(p, EclipseState.Station.VAULT)) {
            return false;
        }
        p.m_5893_(new MenuProvider(){

            public Component m_5446_() {
                return EclipseText.c("station.vault", new Object[0]);
            }

            public AbstractContainerMenu m_7208_(int id, Inventory inv, Player player) {
                return new ChestMenu(MenuType.f_39959_, id, inv, (Container)new Bank(p), 3);
            }
        });
        return true;
    }

    public static boolean supply(ServerPlayer p, List<UUID> ids, boolean deposit) {
        if (!EclipseService.usable(p) || !EclipseCamp.near(p, EclipseState.Station.QUARTERMASTER) || ids.isEmpty() || ids.size() > 24 || ids.stream().anyMatch(id -> EclipseService.record(p, id) == null || AdventureService.busy(p.f_8924_, p.m_20148_(), id))) {
            return false;
        }
        EclipseState e = EclipseService.state(p);
        boolean changed = false;
        for (UUID id2 : new LinkedHashSet<UUID>(ids)) {
            ShadowRecord r = EclipseService.record(p, id2);
            r.inventory.initialize(r.snapshot);
            if (deposit) {
                for (int i = 6; i < 11; ++i) {
                    ItemStack item = (ItemStack)r.inventory.items.get(i);
                    ItemStack remain = EclipseService.insert(e.bank, item.m_41777_());
                    if (remain.m_41613_() == item.m_41613_()) continue;
                    r.inventory.items.set(i, (Object)remain);
                    changed = true;
                }
                continue;
            }
            for (ItemStack item : e.bank) {
                boolean food;
                if (item.m_41619_()) continue;
                boolean arrow = item.m_204117_(ItemTags.f_13161_);
                boolean bl = food = item.m_41614_() && item.getFoodProperties((LivingEntity)p) != null && item.getFoodProperties((LivingEntity)p).m_38749_().isEmpty();
                if (!arrow && !food) continue;
                int have = r.inventory.items.subList(6, 11).stream().filter(s -> ItemStack.m_150942_((ItemStack)s, (ItemStack)item)).mapToInt(ItemStack::m_41613_).sum();
                int count = Math.min(item.m_41613_(), Math.max(0, (arrow ? 16 : 8) - have));
                if (count <= 0) continue;
                ItemStack offered = item.m_41777_();
                offered.m_41764_(count);
                ItemStack remaining = CompanionService.store(r, offered);
                int moved = count - remaining.m_41613_();
                if (moved <= 0) continue;
                item.m_41774_(moved);
                changed = true;
            }
        }
        if (changed) {
            EclipseService.dirty(p);
        }
        return changed;
    }

    private static boolean ingredients(ServerPlayer p, Item[] items, int[] counts, boolean consume) {
        int i;
        for (i = 0; i < items.length; ++i) {
            if (p.m_150109_().m_18947_(items[i]) >= counts[i]) continue;
            return false;
        }
        if (consume) {
            for (i = 0; i < items.length; ++i) {
                int left = counts[i];
                for (int slot = 0; slot < p.m_150109_().m_6643_() && left > 0; ++slot) {
                    ItemStack s = p.m_150109_().m_8020_(slot);
                    if (!s.m_150930_(items[i])) continue;
                    int n = Math.min(left, s.m_41613_());
                    s.m_41774_(n);
                    left -= n;
                }
            }
        }
        return true;
    }

    public static Item consumable(int index) {
        return index == 0 ? (Item)EclipseContent.FOCUS.get() : (index == 1 ? (Item)EclipseContent.WARD.get() : (Item)EclipseContent.FLARE.get());
    }

    public static boolean craft(ServerPlayer p, int index) {
        int[] nArray;
        Item[] itemArray;
        if (!EclipseCamp.near(p, EclipseState.Station.ALCHEMY) || index < 0 || index > 2) {
            return false;
        }
        if (index == 0) {
            Item[] itemArray2 = new Item[3];
            itemArray2[0] = Items.f_42590_;
            itemArray2[1] = Items.f_151049_;
            itemArray = itemArray2;
            itemArray2[2] = Items.f_42451_;
        } else if (index == 1) {
            Item[] itemArray3 = new Item[3];
            itemArray3[0] = Items.f_42590_;
            itemArray3[1] = Items.f_151049_;
            itemArray = itemArray3;
            itemArray3[2] = Items.f_42416_;
        } else {
            Item[] itemArray4 = new Item[3];
            itemArray4[0] = Items.f_42398_;
            itemArray4[1] = Items.f_151049_;
            itemArray = itemArray4;
            itemArray4[2] = Items.f_42525_;
        }
        Item[] needs = itemArray;
        if (index == 0) {
            int[] nArray2 = new int[3];
            nArray2[0] = 1;
            nArray2[1] = 2;
            nArray = nArray2;
            nArray2[2] = 1;
        } else {
            int[] nArray3 = new int[3];
            nArray3[0] = 1;
            nArray3[1] = 1;
            nArray = nArray3;
            nArray3[2] = 1;
        }
        int[] costs = nArray;
        if (!EclipseCamp.ingredients(p, needs, costs, false) || !EclipseCamp.canAdd(p, new ItemStack((ItemLike)EclipseCamp.consumable(index)))) {
            return false;
        }
        EclipseCamp.ingredients(p, needs, costs, true);
        p.m_150109_().m_36054_(new ItemStack((ItemLike)EclipseCamp.consumable(index)));
        p.m_150109_().m_6596_();
        return true;
    }

    private static boolean canAdd(ServerPlayer p, ItemStack item) {
        ArrayList<ItemStack> copies = new ArrayList<ItemStack>();
        for (int i = 0; i < 36; ++i) {
            copies.add(p.m_150109_().m_8020_(i).m_41777_());
        }
        return EclipseService.insert(copies, item.m_41777_()).m_41619_();
    }

    public static int[] offers(ServerPlayer p) {
        int start = Math.floorMod((long)p.m_20148_().hashCode() + (long)EclipseService.state((ServerPlayer)p).wins * 17L, 6);
        return new int[]{start, (start + 2) % 6, (start + 4) % 6};
    }

    public static boolean buy(ServerPlayer p, int offer) {
        ItemStack item;
        int cost;
        int type;
        if (!EclipseCamp.near(p, EclipseState.Station.MERCHANT) || offer < 0 || offer > 2) {
            return false;
        }
        EclipseState e = EclipseService.state(p);
        if (e.merchantCycle != (long)e.wins) {
            Arrays.fill(e.purchases, 0);
            e.merchantCycle = e.wins;
        }
        int n = (type = EclipseCamp.offers(p)[offer]) <= 2 ? 1 : (cost = type <= 4 ? 2 : 4);
        if (e.marks < cost || e.purchases[type] >= 3) {
            return false;
        }
        ItemStack itemStack = type <= 2 ? new ItemStack((ItemLike)EclipseCamp.consumable(type)) : (type == 3 ? new ItemStack((ItemLike)Items.f_151049_, 4) : (item = type == 4 ? new ItemStack((ItemLike)Items.f_151052_, 4) : ItemStack.f_41583_));
        if (!item.m_41619_() && !EclipseCamp.canAdd(p, item)) {
            return false;
        }
        if (type == 5) {
            int locked;
            for (locked = 0; locked < 6 && (e.palettes & 1 << locked) != 0; ++locked) {
            }
            if (locked == 6) {
                return false;
            }
            e.palettes |= 1 << locked;
        } else {
            p.m_150109_().m_36054_(item);
        }
        e.marks -= cost;
        int n2 = type;
        e.purchases[n2] = e.purchases[n2] + 1;
        EclipseService.dirty(p);
        return true;
    }

    public static boolean consume(ServerPlayer p, int effect) {
        if (!EclipseService.usable(p) || effect < 0 || effect > 2) {
            return false;
        }
        EclipseState e = EclipseService.state(p);
        long now = EclipseService.now(p);
        if (effect == 0) {
            if (now < e.drinkAt || e.energy >= 100.0f) {
                return false;
            }
            e.energy = Math.min(100.0f, e.energy + 40.0f);
            e.drinkAt = now + 1200L;
        } else if (effect == 1) {
            Mob m;
            ShadowRecord r = EclipseService.record(p, e.selected);
            Mob mob = m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m == null || m.m_9236_() != p.m_9236_() || m.m_20280_((Entity)p) > 64.0) {
                return false;
            }
            m.getPersistentData().m_128350_("EclipsePhialShield", Math.max(3.0f, m.getPersistentData().m_128457_("EclipsePhialShield")));
            m.getPersistentData().m_128356_("EclipsePhialUntil", now + 200L);
        } else {
            Vec3 at = EclipsePowers.aim(p, 24);
            if (at == null) {
                return false;
            }
            ScoutingFlare flare = new ScoutingFlare(p.m_9236_(), (LivingEntity)p);
            flare.m_37251_((Entity)p, p.m_146909_(), p.m_146908_(), 0.0f, 1.2f, 0.3f);
            if (!p.m_284548_().m_7967_((Entity)flare)) {
                return false;
            }
            p.m_36335_().m_41524_((Item)EclipseContent.FLARE.get(), 20);
        }
        EclipseService.dirty(p);
        return true;
    }

    public static void reveal(ServerPlayer p, Vec3 at) {
        EclipseState e = EclipseService.state(p);
        e.marksUntil.clear();
        List<LivingEntity> targets = p.m_9236_().m_6443_(LivingEntity.class, new AABB(at, at).m_82400_(16.0), m -> m.m_20238_(at) <= 256.0 && ShadowService.canAttack(p, m)).stream().sorted(Comparator.comparingDouble(m -> m.m_20238_(at))).limit(8L).toList();
        for (LivingEntity target : targets) {
            e.marksUntil.put(target.m_20148_(), EclipseService.now(p) + 160L);
        }
        EclipsePowers.particles(p, at, ParticleTypes.f_123810_, 16);
        EclipseService.dirty(p);
    }

    public static List<EclipseState.Site> gates(ServerPlayer p) {
        return EclipseService.state((ServerPlayer)p).sites.stream().filter(s -> s.kind() == EclipseState.Station.WAYGATE && s.dimension().equals(p.m_9236_().m_46472_().m_135782_().toString())).limit(4L).toList();
    }

    public static boolean gate(ServerPlayer p, int index) {
        EclipseState e = EclipseService.state(p);
        List<EclipseState.Site> gates = EclipseCamp.gates(p);
        if (!EclipseService.usable(p) || VigilCrown.active(p) || !EclipseCamp.near(p, EclipseState.Station.WAYGATE) || AdventureService.journal((ServerPlayer)p).session != null || e.energy < 50.0f || EclipseService.now(p) < e.gateAt || index < 0 || index >= gates.size() || p.m_20238_(Vec3.m_82512_((Vec3i)gates.get(index).pos())) <= 16.0 || p.m_20159_()) {
            return false;
        }
        e.gateDestination = gates.get(index);
        e.gateStart = p.m_20182_();
        e.gateUntil = EclipseService.now(p) + 100L;
        return true;
    }

    public static void tickGate(ServerPlayer p) {
        Vec3 at;
        block14: {
            block13: {
                EclipseState.Site gate;
                block12: {
                    block11: {
                        BlockEntity blockEntity;
                        EclipseState e = EclipseService.state(p);
                        if (e.gateUntil <= 0L || e.gateDestination == null) {
                            return;
                        }
                        if (VigilCrown.active(p) || p.m_20238_(e.gateStart) > 0.1 || !EclipseCamp.near(p, EclipseState.Station.WAYGATE) || AdventureService.journal((ServerPlayer)p).session != null) {
                            e.gateUntil = 0L;
                            return;
                        }
                        if (EclipseService.now(p) < e.gateUntil) {
                            return;
                        }
                        gate = e.gateDestination;
                        e.gateUntil = 0L;
                        if (e.energy < 50.0f || !p.m_9236_().m_6857_().m_61937_(gate.pos())) {
                            return;
                        }
                        p.m_284548_().m_46745_(gate.pos());
                        if (!p.m_9236_().m_8055_(gate.pos()).m_60713_((Block)EclipseContent.STATIONS.get((Object)EclipseState.Station.WAYGATE).get()) || !((blockEntity = p.m_9236_().m_7702_(gate.pos())) instanceof EclipseContent.CampEntity)) break block11;
                        EclipseContent.CampEntity be = (EclipseContent.CampEntity)blockEntity;
                        if (p.m_20148_().equals(be.owner)) break block12;
                    }
                    return;
                }
                at = AdventureWorld.spot(p.m_284548_(), EntityType.f_20529_, gate.pos(), 1.0, 3.0, 0L);
                if (at == null) {
                    return;
                }
                EntityTeleportEvent.EnderEntity veto = ForgeEventFactory.onEnderTeleport((LivingEntity)p, (double)at.f_82479_, (double)at.f_82480_, (double)at.f_82481_);
                if (veto.isCanceled()) break block13;
                Vec3 vec3 = new Vec3(veto.getTargetX(), veto.getTargetY(), veto.getTargetZ());
                if (!(vec3.m_82557_(at) > 0.01)) break block14;
            }
            return;
        }
        p.m_6021_(at.f_82479_, at.f_82480_, at.f_82481_);
        e.energy -= 50.0f;
        e.gateAt = EclipseService.now(p) + 2400L;
        VigilService.recallTraveling(p);
        EclipseService.dirty(p);
    }

    public static void appearance(Mob mob, ShadowRecord r) {
        EclipseState.Build b = ArmyData.get((MinecraftServer)mob.m_20194_()).army((UUID)ShadowService.ownerId((Entity)mob)).eclipse.builds.get(r.id);
        if (b != null) {
            mob.getPersistentData().m_128405_("EclipsePalette", b.palette);
            mob.getPersistentData().m_128405_("EclipseSeal", b.seal);
            mob.getPersistentData().m_128405_("EclipseFrame", b.frame);
        }
    }

    private static final class Bank
    implements Container {
        private final ServerPlayer owner;

        Bank(ServerPlayer p) {
            this.owner = p;
        }

        private List<ItemStack> bank() {
            return EclipseService.state((ServerPlayer)this.owner).bank;
        }

        public int m_6643_() {
            return 27;
        }

        public boolean m_7983_() {
            return this.bank().stream().allMatch(ItemStack::m_41619_);
        }

        public ItemStack m_8020_(int i) {
            return i >= 0 && i < 27 ? this.bank().get(i) : ItemStack.f_41583_;
        }

        public ItemStack m_7407_(int i, int count) {
            ItemStack result = this.m_8020_(i).m_41620_(count);
            this.m_6596_();
            return result;
        }

        public ItemStack m_8016_(int i) {
            ItemStack item = this.m_8020_(i);
            this.m_6836_(i, ItemStack.f_41583_);
            return item;
        }

        public void m_6836_(int i, ItemStack item) {
            if (i >= 0 && i < 27) {
                this.bank().set(i, item);
                this.m_6596_();
            }
        }

        public void m_6596_() {
            EclipseService.dirty(this.owner);
        }

        public boolean m_6542_(Player p) {
            return p == this.owner && EclipseCamp.near(this.owner, EclipseState.Station.VAULT);
        }

        public void m_6211_() {
            for (int i = 0; i < 27; ++i) {
                this.bank().set(i, ItemStack.f_41583_);
            }
            this.m_6596_();
        }
    }
}

