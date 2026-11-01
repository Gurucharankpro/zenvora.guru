/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.network.NetworkHooks
 */
package dev.shadowsovereign.royal;

import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.royal.RoyalContent;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.royal.RoyalText;
import dev.shadowsovereign.soulforge.CompanionService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

public final class RoyalBagMenu
extends AbstractContainerMenu {
    public final int team;
    public final int page;
    public final List<String> names = new ArrayList<String>();
    private final NonNullList<ItemStack> clientItems = NonNullList.m_122780_((int)20, (Object)ItemStack.f_41583_);
    private final List<UUID> members;
    private final List<ShadowRecord> records;
    private final Player owner;

    public RoyalBagMenu(int id, Inventory inv, FriendlyByteBuf b) {
        this(id, inv, b.m_130242_(), b.m_130242_(), RoyalBagMenu.readIds(b));
        for (int i = 0; i < 4; ++i) {
            this.names.add(b.m_130136_(128));
        }
    }

    private static List<UUID> readIds(FriendlyByteBuf b) {
        int count = b.m_130242_();
        if (count < 0 || count > 4) {
            throw new IllegalArgumentException("Invalid backpack page");
        }
        ArrayList<UUID> ids = new ArrayList<UUID>();
        for (int i = 0; i < count; ++i) {
            ids.add(b.m_130259_());
        }
        return ids;
    }

    public RoyalBagMenu(int id, Inventory inv, int team, int page, List<UUID> ids) {
        super((MenuType)RoyalContent.BAGS.get(), id);
        int row;
        this.team = team;
        this.page = page;
        this.owner = inv.f_35978_;
        this.members = List.copyOf(ids);
        this.records = new ArrayList<ShadowRecord>();
        Player player = this.owner;
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            for (UUID uid : ids) {
                ShadowRecord r = EclipseService.record(p, uid);
                if (r == null) continue;
                r.inventory.initialize(r.snapshot);
                this.records.add(r);
                this.names.add(LegacyService.name(p, r));
            }
        }
        BagContainer container = new BagContainer();
        for (row = 0; row < 4; ++row) {
            for (int col = 0; col < 5; ++col) {
                int index = row * 5 + col;
                this.m_38897_(new Slot(container, index, 115 + col * 18, 26 + row * 26){

                    public boolean m_5857_(ItemStack s) {
                        return this.f_40219_ / 5 < RoyalBagMenu.this.members.size();
                    }

                    public boolean m_8010_(Player p) {
                        return this.f_40219_ / 5 < RoyalBagMenu.this.members.size() && RoyalBagMenu.this.m_6875_(p);
                    }
                });
            }
        }
        for (row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.m_38897_(new Slot((Container)inv, col + row * 9 + 9, 34 + col * 18, 139 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.m_38897_(new Slot((Container)inv, col, 34 + col * 18, 197));
        }
    }

    public boolean m_6875_(Player p) {
        if (p != this.owner || !p.m_6084_() || p.m_5833_()) {
            return false;
        }
        if (p instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)p;
            return this.records.size() == this.members.size() && this.records.stream().allMatch(r -> CompanionService.accessible(sp, r));
        }
        return true;
    }

    public ItemStack m_7648_(Player p, int index) {
        if (index < 0 || index >= this.f_38839_.size() || !this.m_6875_(p)) {
            return ItemStack.f_41583_;
        }
        Slot slot = (Slot)this.f_38839_.get(index);
        if (!slot.m_6657_()) {
            return ItemStack.f_41583_;
        }
        ItemStack item = slot.m_7993_();
        ItemStack copy = item.m_41777_();
        if (index < 20 ? !this.m_38903_(item, 20, this.f_38839_.size(), true) : !this.m_38903_(item, 0, this.members.size() * 5, false)) {
            return ItemStack.f_41583_;
        }
        if (item.m_41619_()) {
            slot.m_5852_(ItemStack.f_41583_);
        } else {
            slot.m_6654_();
        }
        slot.m_142406_(p, item);
        return copy;
    }

    public static boolean open(ServerPlayer p, final int team, final int page) {
        RoyalState.Team t = RoyalService.team(p, team);
        if (t == null || page < 0 || page >= 6 || !p.f_36096_.m_142621_().m_41619_()) {
            return false;
        }
        List<UUID> all = t.members.stream().filter(id -> CompanionService.accessible(p, EclipseService.record(p, id))).toList();
        final List ids = all.stream().skip((long)page * 4L).limit(4L).toList();
        if (ids.isEmpty()) {
            return false;
        }
        NetworkHooks.openScreen((ServerPlayer)p, (MenuProvider)new MenuProvider(){

            public Component m_5446_() {
                return RoyalText.c("bags", new Object[0]);
            }

            public AbstractContainerMenu m_7208_(int id, Inventory inv, Player player) {
                return new RoyalBagMenu(id, inv, team, page, ids);
            }
        }, b -> {
            b.m_130130_(team);
            b.m_130130_(page);
            b.m_130130_(ids.size());
            ids.forEach(arg_0 -> ((FriendlyByteBuf)b).m_130077_(arg_0));
            for (int i = 0; i < 4; ++i) {
                b.m_130072_(i < ids.size() ? LegacyService.name(p, EclipseService.record(p, (UUID)ids.get(i))) : "", 128);
            }
        });
        return true;
    }

    private final class BagContainer
    implements Container {
        private BagContainer() {
        }

        private List<ItemStack> list(int slot) {
            return slot >= 0 && slot / 5 < RoyalBagMenu.this.records.size() ? RoyalBagMenu.this.records.get((int)(slot / 5)).inventory.items : null;
        }

        public int m_6643_() {
            return 20;
        }

        public boolean m_7983_() {
            if (!(RoyalBagMenu.this.owner instanceof ServerPlayer)) {
                return RoyalBagMenu.this.clientItems.stream().allMatch(ItemStack::m_41619_);
            }
            return RoyalBagMenu.this.records.stream().allMatch(r -> r.inventory.items.subList(6, 11).stream().allMatch(ItemStack::m_41619_));
        }

        public ItemStack m_8020_(int slot) {
            if (!(RoyalBagMenu.this.owner instanceof ServerPlayer)) {
                return (ItemStack)RoyalBagMenu.this.clientItems.get(slot);
            }
            List<ItemStack> l = this.list(slot);
            return l == null ? ItemStack.f_41583_ : l.get(6 + slot % 5);
        }

        public ItemStack m_7407_(int slot, int count) {
            ItemStack s = this.m_8020_(slot).m_41620_(count);
            this.m_6596_();
            return s;
        }

        public ItemStack m_8016_(int slot) {
            ItemStack s = this.m_8020_(slot);
            this.m_6836_(slot, ItemStack.f_41583_);
            return s;
        }

        public void m_6836_(int slot, ItemStack s) {
            if (!(RoyalBagMenu.this.owner instanceof ServerPlayer)) {
                RoyalBagMenu.this.clientItems.set(slot, (Object)s);
                return;
            }
            List<ItemStack> l = this.list(slot);
            if (l != null) {
                l.set(6 + slot % 5, s);
                this.m_6596_();
            }
        }

        public void m_6596_() {
            Player player = RoyalBagMenu.this.owner;
            if (player instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)player;
                RoyalService.dirty(p);
            }
        }

        public boolean m_6542_(Player p) {
            return RoyalBagMenu.this.m_6875_(p);
        }

        public void m_6211_() {
            for (int i = 0; i < 20; ++i) {
                this.m_6836_(i, ItemStack.f_41583_);
            }
        }
    }
}

