/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BonemealableBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.shadowsovereign.expansion;

import dev.shadowsovereign.gloom.GloomContent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class TidewoodSapling
extends GloomContent.GroundPlant
implements BonemealableBlock {
    public TidewoodSapling() {
        super(BlockBehaviour.Properties.m_60926_((BlockBehaviour)Blocks.f_50746_));
    }

    public void m_213898_(BlockState s, ServerLevel w, BlockPos p, RandomSource r) {
        if (r.m_188503_(7) == 0 && w.m_46803_(p.m_7494_()) >= 9) {
            TidewoodSapling.grow(w, p);
        }
    }

    public static boolean grow(ServerLevel w, BlockPos p) {
        int z;
        int x;
        int y;
        for (y = 1; y <= 7; ++y) {
            for (x = -2; x <= 2; ++x) {
                for (z = -2; z <= 2; ++z) {
                    BlockPos q = p.m_7918_(x, y, z);
                    if (w.m_46805_(q) && (w.m_8055_(q).m_60795_() || w.m_8055_(q).m_247087_())) continue;
                    return false;
                }
            }
        }
        for (y = 3; y <= 6; ++y) {
            for (x = -2; x <= 2; ++x) {
                for (z = -2; z <= 2; ++z) {
                    if (x * x + z * z + (y - 4) * (y - 4) > 9) continue;
                    w.m_7731_(p.m_7918_(x, y, z), (BlockState)((Block)GloomContent.blocks.get("sunken_tidewood_leaves").get()).m_49966_().m_61124_((Property)LeavesBlock.f_54418_, (Comparable)Integer.valueOf(1)), 3);
                }
            }
        }
        for (y = 0; y < 6; ++y) {
            w.m_7731_(p.m_6630_(y), ((Block)GloomContent.blocks.get("sunken_tidewood_log").get()).m_49966_(), 3);
        }
        return true;
    }

    public boolean m_7370_(LevelReader w, BlockPos p, BlockState s, boolean c) {
        return true;
    }

    public boolean m_214167_(Level w, RandomSource r, BlockPos p, BlockState s) {
        return true;
    }

    public void m_214148_(ServerLevel w, RandomSource r, BlockPos p, BlockState s) {
        TidewoodSapling.grow(w, p);
    }
}

