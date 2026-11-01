/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.MythicState;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class MythicLair {
    public static final int SIDE = 27;
    public static final int HEIGHT = 10;
    public static final int TOTAL = 7353;

    public static BlockPos center(ServerPlayer p) {
        MythicState s = MythicService.state(p);
        if (s.gloom.m_128441_("NaturalDevourer")) {
            return BlockPos.m_122022_((long)s.gloom.m_128454_("NaturalDevourer"));
        }
        if (s.site < 0) {
            s.site = Math.floorMod(Long.hashCode(p.f_8924_.m_129783_().m_7328_() ^ p.m_20148_().getMostSignificantBits() ^ p.m_20148_().getLeastSignificantBits()), 16);
            LivingRealm.dirty(p);
        }
        return OdysseyRealm.arena(p, s.site / 4).m_7918_((new int[]{-40, -16, 16, 40})[s.site % 4], 8, -100);
    }

    public static void tick(ServerPlayer p) {
        MythicState s = MythicService.state(p);
        if (s.generated || s.gloom.m_128441_("NaturalDevourer") || !OdysseyRealm.inside(p) || !LivingRealm.state((ServerPlayer)p).ready) {
            return;
        }
        BlockPos c = MythicLair.center(p);
        if (p.m_20183_().m_123331_((Vec3i)c) > 25600.0) {
            return;
        }
        s.requested = true;
        int placed = 0;
        while (s.cursor < 7353 && placed++ < 384) {
            BlockPos pos;
            int i = s.cursor;
            BlockState block = null;
            if (i < 7290) {
                boolean pillar;
                int x = i % 27 - 13;
                z = i / 27 % 27 - 13;
                int y = i / 729;
                pos = c.m_7918_(x, y, z);
                boolean edge = Math.abs(x) == 13 || Math.abs(z) == 13;
                boolean bl = pillar = Math.abs(x) == 10 && Math.abs(z) == 10 || Math.abs(x) == 10 && z == 0;
                if (y == 0) {
                    block = (edge ? Blocks.f_50735_ : (Math.abs(x) % 6 == 0 || Math.abs(z) % 6 == 0 ? Blocks.f_50737_ : Blocks.f_152555_)).m_49966_();
                } else if (pillar && y < 7) {
                    block = (y == 6 ? Blocks.f_50682_ : (y == 5 ? Blocks.f_152490_ : Blocks.f_50735_)).m_49966_();
                } else if (edge && y == 1 && (z != 13 || Math.abs(x) > 3)) {
                    block = Blocks.f_50740_.m_49966_();
                } else if (y == 1 && x * x + z * z >= 42 && x * x + z * z <= 50) {
                    block = Blocks.f_50682_.m_49966_();
                } else if (y == 8 && pillar) {
                    block = Blocks.f_50723_.m_49966_();
                }
            } else {
                int n = i - 7290;
                z = n / 7 + 14;
                int x = n % 7 - 3;
                pos = c.m_7918_(x, -(z - 13), z);
                block = Blocks.f_50735_.m_49966_();
            }
            if (!p.m_284548_().m_46805_(pos)) break;
            if (block != null && p.m_284548_().m_8055_(pos).m_60795_()) {
                p.m_284548_().m_7731_(pos, block, 3);
            }
            ++s.cursor;
        }
        if (s.cursor >= 7353) {
            s.generated = true;
        }
        LivingRealm.dirty(p);
    }
}

