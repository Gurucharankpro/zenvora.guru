/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.vigil;

import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.vigil.VigilContent;
import dev.shadowsovereign.vigil.VigilService;
import dev.shadowsovereign.vigil.VigilState;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public final class VigilGraves {
    private static Payment payment;

    public static int price(float hp) {
        return Float.isFinite(hp) ? Math.max(1, (int)Math.ceil((double)hp / 50.0)) : 1;
    }

    public static boolean needsPayment(ShadowRecord r) {
        return r.pendingRespawn() && r.vigil.freeUsed >= 5;
    }

    public static boolean ownedNear(ServerPlayer p) {
        for (BlockPos at : BlockPos.m_121940_((BlockPos)p.m_20183_().m_7918_(-8, -5, -8), (BlockPos)p.m_20183_().m_7918_(8, 5, 8))) {
            BlockEntity blockEntity;
            if (!(p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 64.0) || !p.m_284548_().m_46805_(at) || !p.m_9236_().m_8055_(at).m_60713_((Block)VigilContent.GRAVE.get()) || !((blockEntity = p.m_9236_().m_7702_(at)) instanceof VigilContent.PostEntity)) continue;
            VigilContent.PostEntity be = (VigilContent.PostEntity)blockEntity;
            if (!p.m_20148_().equals(be.owner)) continue;
            return true;
        }
        return false;
    }

    public static boolean allowed(ServerPlayer p, ShadowRecord r) {
        return !VigilGraves.needsPayment(r) || payment != null && p.m_20148_().equals(VigilGraves.payment.owner) && r.id.equals(VigilGraves.payment.soul) && r.vigil.fee == VigilGraves.payment.fee && VigilGraves.ownedNear(p);
    }

    public static int diamonds(ServerPlayer p) {
        return RoyalService.count(p, Items.f_42415_);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean pay(ServerPlayer p, ShadowRecord r, int expected) {
        if (r == null || !VigilGraves.needsPayment(r) || expected != r.vigil.fee || !VigilGraves.ownedNear(p) || payment != null || VigilService.state((ServerPlayer)p).offerings + VigilGraves.diamonds(p) < expected || VigilService.now(p) < r.respawnAt) {
            return false;
        }
        Payment txn = new Payment();
        txn.owner = p.m_20148_();
        txn.soul = r.id;
        txn.fee = expected;
        txn.purse = Math.min(expected, VigilService.state((ServerPlayer)p).offerings);
        VigilService.state((ServerPlayer)p).offerings -= txn.purse;
        int left = expected - txn.purse;
        for (int i = 0; i < 36 && left > 0; ++i) {
            ItemStack stack = p.m_150109_().m_8020_(i);
            if (!stack.m_150930_(Items.f_42415_)) continue;
            int n = Math.min(left, stack.m_41613_());
            txn.stacks.add(stack.m_41620_(n));
            left -= n;
        }
        payment = txn;
        boolean success = false;
        try {
            boolean bl = success = ShadowService.summon(p, r, true);
            return bl;
        }
        finally {
            payment = null;
            if (!success || !txn.committed) {
                VigilService.state((ServerPlayer)p).offerings += txn.purse;
                for (ItemStack stack : txn.stacks) {
                    if (p.m_150109_().m_36054_(stack)) continue;
                    p.m_36176_(stack, false);
                }
            }
            p.m_150109_().m_6596_();
            p.f_36095_.m_38946_();
            VigilService.dirty(p);
        }
    }

    public static void returned(ServerPlayer p, ShadowRecord r) {
        if (r.vigil.freeUsed < 5) {
            ++r.vigil.freeUsed;
        } else if (payment != null && p.m_20148_().equals(VigilGraves.payment.owner) && r.id.equals(VigilGraves.payment.soul)) {
            VigilGraves.payment.committed = true;
        }
        VigilService.dirty(p);
    }

    public static boolean offerings(ServerPlayer p, int delta) {
        if (!VigilGraves.ownedNear(p) || delta == 0 || Math.abs((long)delta) > 64L) {
            return false;
        }
        VigilState v = VigilService.state(p);
        if (delta > 0) {
            int n = Math.min(delta, Math.min(4096 - v.offerings, VigilGraves.diamonds(p)));
            if (n <= 0) {
                return false;
            }
            int left = n;
            for (int i = 0; i < 36 && left > 0; ++i) {
                ItemStack stack = p.m_150109_().m_8020_(i);
                if (!stack.m_150930_(Items.f_42415_)) continue;
                int k = Math.min(left, stack.m_41613_());
                stack.m_41774_(k);
                left -= k;
            }
            v.offerings += n;
        } else {
            int n = Math.min(-delta, v.offerings);
            if (n <= 0) {
                return false;
            }
            ItemStack stack = new ItemStack((ItemLike)Items.f_42415_, n);
            p.m_150109_().m_36054_(stack);
            int moved = n - stack.m_41613_();
            v.offerings -= moved;
            if (moved == 0) {
                return false;
            }
        }
        p.m_150109_().m_6596_();
        p.f_36095_.m_38946_();
        VigilService.dirty(p);
        return true;
    }

    private static final class Payment {
        UUID owner;
        UUID soul;
        int fee;
        int purse;
        boolean committed;
        final List<ItemStack> stacks = new ArrayList<ItemStack>();

        private Payment() {
        }
    }
}

