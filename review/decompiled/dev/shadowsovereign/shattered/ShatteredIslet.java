/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.WorldGenLevel
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
 *  net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration
 */
package dev.shadowsovereign.shattered;

import dev.shadowsovereign.moonveil.MoonveilContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public final class ShatteredIslet
extends Feature<BlockStateConfiguration> {
    public ShatteredIslet() {
        super(BlockStateConfiguration.f_67546_);
    }

    public boolean m_142674_(FeaturePlaceContext<BlockStateConfiguration> c) {
        WorldGenLevel w = c.m_159774_();
        BlockPos at = c.m_159777_().m_6630_(14 + c.m_225041_().m_188503_(12));
        int radius = 4 + c.m_225041_().m_188503_(3);
        if (at.m_123342_() + 4 >= w.m_151558_()) {
            return false;
        }
        for (BlockPos p : BlockPos.m_121940_((BlockPos)at.m_7918_(-radius, -6, -radius), (BlockPos)at.m_7918_(radius, 3, radius))) {
            if (w.m_7232_(p.m_123341_() >> 4, p.m_123343_() >> 4) && w.m_8055_(p).m_60795_()) continue;
            return false;
        }
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                double q = (double)(x * x + z * z) / (double)(radius * radius);
                if (q > 1.0) continue;
                int depth = 1 + (int)((1.0 - q) * 5.0);
                for (int y = -depth; y <= 0; ++y) {
                    w.m_7731_(at.m_7918_(x, y, z), y == 0 ? MoonveilContent.block("shattered_skybound_grass").m_49966_() : (y == -1 ? MoonveilContent.block("shattered_floating_island_soil").m_49966_() : ((BlockStateConfiguration)c.m_159778_()).f_67547_), 2);
                }
                if (c.m_225041_().m_188503_(7) != 0) continue;
                w.m_7731_(at.m_7918_(x, 1, z), MoonveilContent.block("shattered_floating_crystal_cluster").m_49966_(), 2);
            }
        }
        return true;
    }
}

