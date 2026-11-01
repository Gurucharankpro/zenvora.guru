/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.expansion.OpenMythics;
import dev.shadowsovereign.mythic.MythicBeast;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class MythicSpawn {
    public static boolean place(ServerLevel w, Mob mob, BlockPos altar) {
        return MythicSpawn.place(w, mob, altar, null);
    }

    public static boolean place(ServerLevel w, Mob mob, BlockPos altar, BoundingBox site) {
        for (int radius = 0; radius <= 6; ++radius) {
            for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    if (Math.max(Math.abs(x), Math.abs(z)) != radius) continue;
                    for (int dy : new int[]{0, 1, -1, 2, -2, 3, -3}) {
                        BlockState state;
                        VoxelShape shape;
                        BlockPos feet = altar.m_7918_(x, dy, z);
                        BlockPos ground = feet.m_7495_();
                        if (!w.m_46805_(ground) || !w.m_6857_().m_61937_(feet) || (shape = (state = w.m_8055_(ground)).m_60742_((BlockGetter)w, ground, CollisionContext.m_82750_((Entity)mob))).m_83281_() || !state.m_60819_().m_76178_()) continue;
                        double top = -1.0;
                        for (AABB box : shape.m_83299_()) {
                            if (!(box.f_82288_ <= 0.5) || !(box.f_82291_ >= 0.5) || !(box.f_82290_ <= 0.5) || !(box.f_82293_ >= 0.5)) continue;
                            top = Math.max(top, box.f_82292_);
                        }
                        if (top < 0.0) continue;
                        double targetX = (double)feet.m_123341_() + 0.5;
                        double targetY = (double)ground.m_123342_() + top;
                        double targetZ = (double)feet.m_123343_() + 0.5;
                        AABB bounds = mob.m_20191_().m_82386_(targetX - mob.m_20185_(), targetY - mob.m_20186_(), targetZ - mob.m_20189_());
                        BlockPos candidate = BlockPos.m_274561_((double)targetX, (double)targetY, (double)targetZ);
                        if (site != null && (bounds.f_82288_ < (double)site.m_162395_() || bounds.f_82291_ > (double)(site.m_162399_() + 1) || bounds.f_82290_ < (double)site.m_162398_() || bounds.f_82293_ > (double)(site.m_162401_() + 1) || bounds.f_82289_ < (double)site.m_162396_() || bounds.f_82292_ > (double)(site.m_162400_() + 1)) || bounds.f_82289_ < (double)w.m_141937_() || bounds.f_82292_ >= (double)w.m_151558_() || !w.m_46832_(BlockPos.m_274561_((double)bounds.f_82288_, (double)bounds.f_82289_, (double)bounds.f_82290_), BlockPos.m_274561_((double)bounds.f_82291_, (double)bounds.f_82292_, (double)bounds.f_82293_)) || !w.m_143340_(candidate) || !w.m_143319_(ChunkPos.m_151388_((BlockPos)candidate)) || !w.m_45756_((Entity)mob, bounds) || MythicSpawn.expansion$blockedFluid(w, bounds, mob)) continue;
                        mob.m_7678_(targetX, targetY, targetZ, 180.0f, 0.0f);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean expansion$blockedFluid(ServerLevel serverLevel, AABB aABB, Mob mob) {
        if (mob instanceof MythicBeast && OpenMythics.isAquatic(((MythicBeast)mob).kind())) {
            return false;
        }
        return serverLevel.m_46855_(aABB);
    }
}

