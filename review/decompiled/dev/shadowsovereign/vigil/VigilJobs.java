/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.vigil;

import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.royal.RoyalContent;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.vigil.VigilContent;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilService;
import dev.shadowsovereign.vigil.VigilState;
import dev.shadowsovereign.vigil.VigilText;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class VigilJobs {
    public static boolean bind(ServerPlayer p, boolean destination) {
        BlockHitResult b;
        block6: {
            block5: {
                BlockEntity blockEntity;
                HitResult hit = p.m_19907_(6.0, 0.0f, false);
                if (!(hit instanceof BlockHitResult)) break block5;
                b = (BlockHitResult)hit;
                if (!p.m_284548_().m_46805_(b.m_82425_()) || !((blockEntity = p.m_9236_().m_7702_(b.m_82425_())) instanceof VigilContent.PostEntity)) break block5;
                VigilContent.PostEntity be = (VigilContent.PostEntity)blockEntity;
                if (p.m_20148_().equals(be.owner) && be.m_58900_().m_60713_((Block)VigilContent.CACHE.get())) break block6;
            }
            return false;
        }
        CompoundTag n = new CompoundTag();
        n.m_128356_("Pos", b.m_82425_().m_121878_());
        n.m_128359_("Dimension", p.m_9236_().m_46472_().m_135782_().toString());
        if (destination) {
            VigilService.state((ServerPlayer)p).destination = n;
        } else {
            VigilService.state((ServerPlayer)p).source = n;
        }
        VigilService.dirty(p);
        return true;
    }

    private static VigilContent.PostEntity cache(ServerPlayer p, CompoundTag n) {
        VigilContent.PostEntity be;
        BlockEntity blockEntity;
        if (!n.m_128441_("Pos") || !n.m_128461_("Dimension").equals(p.m_9236_().m_46472_().m_135782_().toString())) {
            return null;
        }
        BlockPos at = BlockPos.m_122022_((long)n.m_128454_("Pos"));
        return p.m_284548_().m_46805_(at) && (blockEntity = p.m_9236_().m_7702_(at)) instanceof VigilContent.PostEntity && (be = (VigilContent.PostEntity)blockEntity).m_58900_().m_60713_((Block)VigilContent.CACHE.get()) && p.m_20148_().equals(be.owner) ? be : null;
    }

    public static boolean courier(ServerPlayer p, UUID id) {
        VigilState v = VigilService.state(p);
        Mob m = VigilService.eligible(p, id);
        VigilContent.PostEntity a = VigilJobs.cache(p, v.source);
        VigilContent.PostEntity b = VigilJobs.cache(p, v.destination);
        if (m == null || a == null || b == null || a == b || a.m_58899_().m_123331_((Vec3i)b.m_58899_()) > 4096.0 || !v.job.m_128456_() || VigilCrown.active(p) || AdventureService.journal((ServerPlayer)p).session != null) {
            return false;
        }
        v.job.m_128359_("Kind", "courier");
        v.job.m_128362_("A", id);
        v.job.m_128356_("Started", VigilService.now(p));
        return true;
    }

    public static boolean tow(ServerPlayer p, UUID injured, UUID rescuer) {
        Mob a = VigilService.eligible(p, injured);
        Mob b = VigilService.eligible(p, rescuer);
        VigilState v = VigilService.state(p);
        if (a == null || b == null || a == b || a.m_20159_() || b.m_20159_() || a.m_20160_() || b.m_20160_() || !v.job.m_128456_() || !VigilService.ready(p, "tow") || VigilCrown.active(p) || AdventureService.journal((ServerPlayer)p).session != null) {
            return false;
        }
        v.job.m_128359_("Kind", "tow");
        v.job.m_128362_("A", injured);
        v.job.m_128362_("B", rescuer);
        v.job.m_128356_("Started", VigilService.now(p));
        v.job.m_128356_("Destination", p.m_20183_().m_121878_());
        VigilService.spend(p, "tow", 0, 600);
        return true;
    }

    public static boolean moving(Mob m, ShadowRecord r, ServerPlayer p) {
        CompoundTag j = VigilService.state((ServerPlayer)p).job;
        if (!(r.id.equals(VigilState.id(j, "A")) || r.id.equals(VigilState.id(j, "B")) || r.id.equals(VigilState.id(VigilService.state((ServerPlayer)p).meteor, "Id")))) {
            return false;
        }
        m.m_6710_(null);
        m.m_6274_().m_21936_(MemoryModuleType.f_26372_);
        return true;
    }

    private static void path(Mob m, Vec3 at) {
        m.m_6710_(null);
        m.m_21573_().m_26519_(at.f_82479_, at.f_82480_, at.f_82481_, 1.15);
    }

    public static int insert(Container into, ItemStack stack) {
        int before = stack.m_41613_();
        for (int i = 0; i < into.m_6643_() && !stack.m_41619_(); ++i) {
            int moved;
            ItemStack present = into.m_8020_(i);
            if (!present.m_41619_() && !ItemStack.m_150942_((ItemStack)present, (ItemStack)stack) || (moved = Math.min(stack.m_41613_(), Math.min(into.m_6893_(), stack.m_41741_()) - present.m_41613_())) <= 0) continue;
            if (present.m_41619_()) {
                ItemStack copy = stack.m_41777_();
                copy.m_41764_(moved);
                into.m_6836_(i, copy);
            } else {
                present.m_41769_(moved);
                into.m_6596_();
            }
            stack.m_41774_(moved);
        }
        return before - stack.m_41613_();
    }

    public static void tick(ServerPlayer p) {
        block22: {
            RoyalState.Team incoming;
            RoyalState.Team old;
            int next;
            VigilState v;
            block24: {
                block23: {
                    Object object;
                    v = VigilService.state(p);
                    CompoundTag j = v.job;
                    if (!j.m_128456_()) {
                        if (VigilCrown.active(p) || VigilService.now(p) - j.m_128454_("Started") > 2400L) {
                            VigilService.cancelJobs(p);
                            return;
                        }
                        Mob a = VigilService.eligible(p, VigilState.id(j, "A"));
                        if (a == null) {
                            return;
                        }
                        if (j.m_128461_("Kind").equals("tow")) {
                            Mob b = VigilService.eligible(p, VigilState.id(j, "B"));
                            if (b == null) {
                                VigilService.cancelJobs(p);
                                return;
                            }
                            if (a.m_20202_() != b) {
                                VigilJobs.path(b, a.m_20182_());
                                if (b.m_20280_((Entity)a) <= 4.0) {
                                    a.m_7998_((Entity)b, true);
                                }
                            } else {
                                Vec3 at = Vec3.m_82539_((Vec3i)BlockPos.m_122022_((long)j.m_128454_("Destination")));
                                VigilJobs.path(b, at);
                                if (b.m_20238_(at) <= 4.0) {
                                    a.m_8127_();
                                    VigilService.cancelJobs(p);
                                    VigilText.tell(p, "tow_done", new Object[0]);
                                }
                            }
                        } else {
                            VigilContent.PostEntity source = VigilJobs.cache(p, v.source);
                            VigilContent.PostEntity dest = VigilJobs.cache(p, v.destination);
                            if (source == null || dest == null) {
                                return;
                            }
                            VigilContent.PostEntity endpoint = j.m_128471_("Loaded") ? dest : source;
                            Vec3 at = Vec3.m_82512_((Vec3i)endpoint.m_58899_());
                            if (p.m_20238_(at) > 4096.0) {
                                return;
                            }
                            VigilJobs.path(a, at);
                            if (a.m_20238_(at) <= 9.0) {
                                ShadowRecord r = VigilService.record(p, j.m_128342_("A"));
                                r.inventory.initialize(r.snapshot);
                                if (!j.m_128471_("Loaded")) {
                                    for (i = 0; i < 27; ++i) {
                                        ItemStack stack = source.m_8020_(i);
                                        if (stack.m_41619_()) continue;
                                        ItemStack remains = CompanionService.store(r, stack.m_41777_());
                                        int moved = stack.m_41613_() - remains.m_41613_();
                                        if (moved <= 0) continue;
                                        stack.m_41774_(moved);
                                        source.m_6596_();
                                    }
                                    j.m_128379_("Loaded", true);
                                } else {
                                    for (i = 6; i < 11; ++i) {
                                        VigilJobs.insert(dest, (ItemStack)r.inventory.items.get(i));
                                    }
                                    j.m_128379_("Loaded", false);
                                }
                                VigilService.dirty(p);
                            }
                        }
                    }
                    if (!v.watch || v.nextWatch > VigilService.now(p) || VigilCrown.active(p) || AdventureService.journal((ServerPlayer)p).session != null) break block22;
                    next = (v.watchTeam + 1) % 5;
                    old = RoyalService.team(p, v.watchTeam);
                    incoming = RoyalService.team(p, next);
                    RoyalState.Banner banner = RoyalService.state((ServerPlayer)p).banners.get(next);
                    if (banner == null || !p.m_284548_().m_46805_(banner.pos()) || !((object = p.m_9236_().m_7702_(banner.pos())) instanceof RoyalContent.RoyalEntity)) break block23;
                    RoyalContent.RoyalEntity be = (RoyalContent.RoyalEntity)((Object)object);
                    if (p.m_20148_().equals(be.owner) && be.team == next && banner.dimension().equals(p.m_9236_().m_46472_().m_135782_().toString()) && !(p.m_20238_(Vec3.m_82512_((Vec3i)banner.pos())) > 1024.0)) break block24;
                }
                return;
            }
            for (UUID id : old.members) {
                ShadowRecord r;
                if (incoming.members.contains(id) || (r = VigilService.record(p, id)) == null) continue;
                ShadowService.recall(p, r);
            }
            RoyalService.deploy(p, next);
            RoyalService.rally(p, next);
            v.watchTeam = next;
            v.nextWatch = VigilService.now(p) + (long)v.watchInterval;
            VigilService.dirty(p);
        }
    }
}

