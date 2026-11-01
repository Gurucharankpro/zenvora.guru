/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.sentinel;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.BattleState;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.sentinel.SentinelContent;
import dev.shadowsovereign.sentinel.SentinelService;
import dev.shadowsovereign.sentinel.SentinelState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public final class PatrolTorches {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean nearNode(ServerPlayer p, BlockPos pos) {
        if (pos == null) return false;
        if (!p.m_284548_().m_46805_(pos)) return false;
        if (!(p.m_20238_(Vec3.m_82512_((Vec3i)pos)) <= 64.0)) return false;
        BlockEntity blockEntity = p.m_284548_().m_7702_(pos);
        if (!(blockEntity instanceof SentinelContent.Node)) return false;
        SentinelContent.Node n = (SentinelContent.Node)blockEntity;
        if (!p.m_20148_().equals(n.owner)) return false;
        return true;
    }

    public static List<SentinelContent.Node> route(ServerPlayer p, BlockPos first) {
        ArrayList<SentinelContent.Node> nodes = new ArrayList<SentinelContent.Node>();
        HashSet<BlockPos> seen = new HashSet<BlockPos>();
        BlockPos at = first;
        UUID expected = null;
        while (at != null) {
            SentinelContent.Node n;
            block6: {
                block5: {
                    BlockEntity blockEntity;
                    if (seen.contains(at)) {
                        return at.equals((Object)first) ? nodes : List.of();
                    }
                    if (nodes.size() >= 8 || !p.m_284548_().m_46805_(at) || !((blockEntity = p.m_284548_().m_7702_(at)) instanceof SentinelContent.Node)) break block5;
                    n = (SentinelContent.Node)blockEntity;
                    if (p.m_20148_().equals(n.owner) && (expected == null || expected.equals(n.id))) break block6;
                }
                return List.of();
            }
            seen.add(at);
            nodes.add(n);
            expected = n.nextId;
            at = n.next;
        }
        return nodes;
    }

    public static boolean assign(ServerPlayer p, ShadowRecord r, BlockPos first) {
        if (!PatrolTorches.nearNode(p, first) || !SentinelService.free(p, r)) {
            return false;
        }
        List<SentinelContent.Node> nodes = PatrolTorches.route(p, first);
        if (nodes.size() < 2) {
            return false;
        }
        ShadowService.setOrder(p, r, Order.PATROL);
        r.battle.route.clear();
        r.sentinel.nodes.clear();
        for (SentinelContent.Node n : nodes) {
            r.battle.route.add(n.m_58899_().m_7949_());
            r.sentinel.nodes.add(n.id);
        }
        r.battle.routeDimension = p.m_9236_().m_46472_().m_135782_().toString();
        r.battle.waypoint = 0;
        r.battle.paused = false;
        r.sentinel.torchRoute = true;
        r.sentinel.circuit = false;
        r.sentinel.waitUntil = 0L;
        r.sentinel.home = first;
        r.sentinel.homeDimension = r.battle.routeDimension;
        Mob m = ShadowService.active(p.f_8924_, r);
        if (m == null && !r.pendingRespawn()) {
            ShadowService.summonAt(p, r, false, p.m_284548_(), Vec3.m_82539_((Vec3i)first));
        }
        GuideService.record(p, "sentinel.route");
        ArmyData.get(p.f_8924_).m_77762_();
        return true;
    }

    public static boolean tick(Mob mob, ShadowRecord r) {
        BlockPos at;
        SentinelState s;
        BattleState b;
        block17: {
            block16: {
                if (!r.sentinel.torchRoute || r.order != Order.PATROL) {
                    return false;
                }
                b = r.battle;
                s = r.sentinel;
                if (b.paused) {
                    mob.m_21573_().m_26573_();
                    return true;
                }
                if (b.route.size() < 2 || s.nodes.size() != b.route.size() || !b.routeDimension.equals(mob.m_9236_().m_46472_().m_135782_().toString())) {
                    b.paused = true;
                    mob.m_21573_().m_26573_();
                    ArmyData.get(mob.m_20194_()).m_77762_();
                    return true;
                }
                b.waypoint = Math.floorMod(b.waypoint, b.route.size());
                at = b.route.get(b.waypoint);
                if (!mob.m_9236_().m_46805_(at)) {
                    mob.m_21573_().m_26573_();
                    return true;
                }
                BlockEntity blockEntity = mob.m_9236_().m_7702_(at);
                if (!(blockEntity instanceof SentinelContent.Node)) break block16;
                SentinelContent.Node n = (SentinelContent.Node)blockEntity;
                if (Objects.equals(ShadowService.ownerId((Entity)mob), n.owner) && n.id.equals(s.nodes.get(b.waypoint))) break block17;
            }
            b.paused = true;
            mob.m_6710_(null);
            mob.m_21573_().m_26573_();
            ArmyData.get(mob.m_20194_()).m_77762_();
            return true;
        }
        long now = mob.m_20194_().m_129783_().m_46467_();
        r.anchor = mob.m_20183_();
        if (mob.m_20238_(Vec3.m_82539_((Vec3i)at)) <= 6.25) {
            mob.m_21573_().m_26573_();
            if (s.waitUntil == 0L) {
                if (b.waypoint == 0 && s.circuit) {
                    ServerPlayer owner = ShadowService.owner((Entity)mob);
                    if (owner != null) {
                        GuideService.record(owner, "sentinel.lap");
                    }
                    s.circuit = false;
                }
                s.waitUntil = now + 30L;
                ArmyData.get(mob.m_20194_()).m_77762_();
            }
            if (now >= s.waitUntil) {
                if (b.waypoint == 0) {
                    s.circuit = true;
                }
                b.waypoint = (b.waypoint + 1) % b.route.size();
                s.waitUntil = 0L;
                s.progressAt = now;
                s.progress = null;
                ArmyData.get(mob.m_20194_()).m_77762_();
            }
            return true;
        }
        if (s.progress == null || mob.m_20182_().m_82557_(s.progress) > 1.0) {
            s.progress = mob.m_20182_();
            s.progressAt = now;
        }
        if (now - s.progressAt > 200L) {
            mob.m_21573_().m_26573_();
            s.progressAt = now + 80L;
            s.progress = mob.m_20182_();
            return true;
        }
        if (mob.f_19797_ % 10 == 0) {
            mob.m_21573_().m_26519_((double)at.m_123341_() + 0.5, (double)at.m_123342_(), (double)at.m_123343_() + 0.5, 1.1);
        }
        return true;
    }
}

