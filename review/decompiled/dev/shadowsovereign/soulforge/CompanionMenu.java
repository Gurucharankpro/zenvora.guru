/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ClickType
 *  net.minecraft.world.inventory.ContainerData
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.SimpleContainerData
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ItemStack
 */
package dev.shadowsovereign.soulforge;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseRoles;
import dev.shadowsovereign.soulforge.CompanionInventory;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.soulforge.SoulTier;
import dev.shadowsovereign.soulforge.SoulTrait;
import dev.shadowsovereign.soulforge.SoulforgeContent;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public final class CompanionMenu
extends AbstractContainerMenu {
    public final UUID companionId;
    public final UUID entityId;
    public final String companionName;
    public final String type;
    public final SoulTier tier;
    public final SoulTrait trait;
    private final ShadowRecord record;
    private final Player owner;
    public final Container inventory;
    private final ContainerData state;

    public int bond() {
        return this.state.m_6413_(0);
    }

    public boolean collecting() {
        return this.state.m_6413_(1) != 0;
    }

    public int careSeconds() {
        return this.state.m_6413_(2);
    }

    public int feedingBond() {
        return this.state.m_6413_(3);
    }

    public CompanionMenu(int id, Inventory inv, FriendlyByteBuf b) {
        this(id, inv, null, b.m_130259_(), b.m_130136_(128), b.m_130136_(256), (SoulTier)b.m_130066_(SoulTier.class), (SoulTrait)b.m_130066_(SoulTrait.class), b.readBoolean() ? b.m_130259_() : null);
    }

    public CompanionMenu(int id, Inventory inv, ShadowRecord r) {
        this(id, inv, r, r.id, r.name, r.type, r.tier, r.trait, r.activeEntity);
    }

    private CompanionMenu(int id, Inventory inv, final ShadowRecord r, UUID companionId, String name, String type, SoulTier tier, SoulTrait trait, UUID entityId) {
        super((MenuType)SoulforgeContent.MENU.get(), id);
        int i;
        this.record = r;
        this.owner = inv.f_35978_;
        this.companionId = companionId;
        this.companionName = name;
        this.type = type;
        this.tier = tier;
        this.trait = trait;
        this.entityId = entityId;
        this.inventory = r == null ? new SimpleContainer(11) : new SoulContainer((ServerPlayer)this.owner, r);
        this.state = r == null ? new SimpleContainerData(4) : new ContainerData(){

            public int m_6413_(int i) {
                return switch (i) {
                    case 0 -> r.bond;
                    case 1 -> {
                        if (r.collectLoot) {
                            yield 1;
                        }
                        yield 0;
                    }
                    case 2 -> (int)Math.min(32767L, Math.max(0L, (r.nextCareAt - CompanionMenu.this.owner.m_20194_().m_129783_().m_46467_() + 19L) / 20L));
                    case 3 -> {
                        if (WarfrontService.state((ServerPlayer)((ServerPlayer)CompanionMenu.this.owner)).build((UUID)r.id).rune == WarfrontState.Rune.HEARTH) {
                            yield 4 + EclipseRoles.feeding((ServerPlayer)CompanionMenu.this.owner, r);
                        }
                        yield 3 + EclipseRoles.feeding((ServerPlayer)CompanionMenu.this.owner, r);
                    }
                    default -> 0;
                };
            }

            public void m_8050_(int i, int value) {
            }

            public int m_6499_() {
                return 4;
            }
        };
        this.m_38884_(this.state);
        for (i = 0; i < 6; ++i) {
            final int equipment = i;
            this.m_38897_(new Slot(this.inventory, i, 18 + i * 22, 75){

                public int m_6641_() {
                    return 1;
                }

                public boolean m_5857_(ItemStack stack) {
                    Player player;
                    Player player2;
                    if (stack.m_41619_()) {
                        return false;
                    }
                    if (equipment == 0) {
                        return true;
                    }
                    if (equipment == 1) {
                        return !(stack.m_41720_() instanceof ArmorItem);
                    }
                    if (r != null && (player2 = CompanionMenu.this.owner) instanceof ServerPlayer) {
                        ServerPlayer p = (ServerPlayer)player2;
                        player = ShadowService.active(p.f_8924_, r);
                    } else {
                        player = null;
                    }
                    Player mob = player;
                    return stack.canEquip(CompanionInventory.SLOTS[equipment], mob == null ? CompanionMenu.this.owner : mob);
                }
            });
        }
        for (i = 0; i < 5; ++i) {
            this.m_38897_(new Slot(this.inventory, 6 + i, 18 + i * 22, 119));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.m_38897_(new Slot((Container)inv, col + row * 9 + 9, 18 + col * 18, 153 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.m_38897_(new Slot((Container)inv, col, 18 + col * 18, 211));
        }
    }

    public boolean m_6875_(Player player) {
        ServerPlayer p;
        return player == this.owner && (this.record == null || player instanceof ServerPlayer && CompanionService.accessible(p = (ServerPlayer)player, this.record));
    }

    public boolean m_6366_(Player player, int button) {
        ServerPlayer p;
        block8: {
            block7: {
                if (!(player instanceof ServerPlayer)) break block7;
                p = (ServerPlayer)player;
                if (this.m_6875_(player) && this.record != null) break block8;
            }
            return false;
        }
        switch (button) {
            case 0: {
                this.record.collectLoot = !this.record.collectLoot;
                ArmyData.get(p.f_8924_).m_77762_();
                break;
            }
            case 1: {
                CompanionService.care(p, this.record);
                break;
            }
            case 2: {
                CompanionService.deposit(p, this.record);
                break;
            }
            default: {
                return false;
            }
        }
        this.m_38946_();
        return true;
    }

    public void m_150399_(int slot, int button, ClickType click, Player player) {
        if (this.m_6875_(player)) {
            super.m_150399_(slot, button, click, player);
        }
    }

    public ItemStack m_7648_(Player player, int index) {
        if (!this.m_6875_(player) || index < 0 || index >= this.f_38839_.size()) {
            return ItemStack.f_41583_;
        }
        Slot slot = (Slot)this.f_38839_.get(index);
        if (!slot.m_6657_() || !slot.m_8010_(player)) {
            return ItemStack.f_41583_;
        }
        ItemStack stack = slot.m_7993_();
        ItemStack original = stack.m_41777_();
        if (index < 11) {
            if (!this.m_38903_(stack, 11, this.f_38839_.size(), true)) {
                return ItemStack.f_41583_;
            }
        } else {
            boolean moved = false;
            for (int i = 2; i < 6 && !stack.m_41619_(); ++i) {
                if (((Slot)this.f_38839_.get(i)).m_6657_() || !((Slot)this.f_38839_.get(i)).m_5857_(stack)) continue;
                moved |= this.m_38903_(stack, i, i + 1, false);
            }
            if (!stack.m_41619_()) {
                moved |= this.m_38903_(stack, 6, 11, false);
            }
            if (!moved) {
                return ItemStack.f_41583_;
            }
        }
        if (stack.m_41619_()) {
            slot.m_269060_(ItemStack.f_41583_);
        } else {
            slot.m_6654_();
        }
        if (stack.m_41613_() == original.m_41613_()) {
            return ItemStack.f_41583_;
        }
        slot.m_142406_(player, stack);
        return original;
    }

    public void m_6877_(Player player) {
        super.m_6877_(player);
        if (this.record != null && player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            if (ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(this.record.id) == this.record) {
                Mob mob = ShadowService.active(p.f_8924_, this.record);
                if (mob != null) {
                    ShadowService.updateSnapshot(mob, this.record);
                }
                ArmyData.get(p.f_8924_).m_77762_();
            }
        }
    }

    private static final class SoulContainer
    implements Container {
        private final ServerPlayer owner;
        private final ShadowRecord record;

        SoulContainer(ServerPlayer owner, ShadowRecord r) {
            this.owner = owner;
            this.record = r;
            r.inventory.initialize(r.snapshot);
        }

        public int m_6643_() {
            return 11;
        }

        public boolean m_7983_() {
            for (int i = 0; i < this.m_6643_(); ++i) {
                if (this.m_8020_(i).m_41619_()) continue;
                return false;
            }
            return true;
        }

        public ItemStack m_8020_(int slot) {
            if (slot < 0 || slot >= this.m_6643_()) {
                return ItemStack.f_41583_;
            }
            Mob mob = ShadowService.active(this.owner.f_8924_, this.record);
            return slot < 6 && mob != null ? mob.m_6844_(CompanionInventory.SLOTS[slot]) : (ItemStack)this.record.inventory.items.get(slot);
        }

        public ItemStack m_7407_(int slot, int amount) {
            ItemStack stack = this.m_8020_(slot);
            ItemStack taken = stack.m_41620_(amount);
            this.m_6836_(slot, stack);
            return taken;
        }

        public ItemStack m_8016_(int slot) {
            ItemStack stack = this.m_8020_(slot);
            this.m_6836_(slot, ItemStack.f_41583_);
            return stack;
        }

        public void m_6836_(int slot, ItemStack stack) {
            this.record.inventory.items.set(slot, (Object)stack);
            Mob mob = ShadowService.active(this.owner.f_8924_, this.record);
            if (slot < 6 && mob != null) {
                mob.m_8061_(CompanionInventory.SLOTS[slot], stack);
                mob.m_21409_(CompanionInventory.SLOTS[slot], 0.0f);
            }
            this.m_6596_();
        }

        public void m_6596_() {
            Mob mob = ShadowService.active(this.owner.f_8924_, this.record);
            if (mob != null) {
                CompanionService.captureEquipment(mob, this.record);
            }
            ArmyData.get(this.owner.f_8924_).m_77762_();
        }

        public boolean m_6542_(Player p) {
            return p == this.owner && CompanionService.accessible(this.owner, this.record);
        }

        public void m_6211_() {
            for (int i = 0; i < this.m_6643_(); ++i) {
                this.m_6836_(i, ItemStack.f_41583_);
            }
        }
    }
}

