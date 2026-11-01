/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class OdysseyScenery {
    public static void decorate(ServerPlayer p, AdventureData.Session s, int region) {
        Block pillar = (new Block[]{Blocks.f_50223_, Blocks.f_152510_, Blocks.f_50283_, Blocks.f_50735_})[region];
        Block cap = (new Block[]{Blocks.f_152471_, Blocks.f_152504_, Blocks.f_152490_, Blocks.f_50706_})[region];
        for (int x : new int[]{-28, 28}) {
            for (int z : new int[]{-28, -12, 12, 28}) {
                int dy;
                int dx;
                int height = region == 3 ? 10 : (region == 2 ? 8 : 6);
                for (int y = 1; y <= height; ++y) {
                    OdysseyScenery.put(p, OdysseyRuns.at(s, x, y, z), y == height ? cap : pillar);
                }
                OdysseyScenery.put(p, OdysseyRuns.at(s, x, height + 1, z), region == 2 ? Blocks.f_50386_ : Blocks.f_50682_);
                if (region == 0) {
                    for (dx = -2; dx <= 2; ++dx) {
                        for (int dz = -2; dz <= 2; ++dz) {
                            if (dx * dx + dz * dz > 5) continue;
                            OdysseyScenery.put(p, OdysseyRuns.at(s, x + dx, height, z + dz), cap);
                        }
                    }
                }
                if (region == 2) {
                    for (dy = 1; dy <= 3; ++dy) {
                        for (int dx2 = -2; dx2 <= 2; ++dx2) {
                            if (dx2 == 0) continue;
                            OdysseyScenery.put(p, OdysseyRuns.at(s, x + dx2, dy, z), Blocks.f_50078_);
                        }
                    }
                }
                if (region == 1) {
                    for (dy = 2; dy <= 4; ++dy) {
                        OdysseyScenery.put(p, OdysseyRuns.at(s, x - Integer.signum(x), dy, z), Blocks.f_50183_);
                    }
                }
                if (region != 3) continue;
                for (dx = -2; dx <= 2; ++dx) {
                    if (dx % 2 != 0) continue;
                    OdysseyScenery.put(p, OdysseyRuns.at(s, x + dx, height + 1, z), pillar);
                }
            }
        }
        for (int z : new int[]{-29, 29}) {
            for (int x = -24; x <= 24; x += 8) {
                OdysseyScenery.put(p, OdysseyRuns.at(s, x, 1, z), cap);
                OdysseyScenery.put(p, OdysseyRuns.at(s, x, 2, z), Blocks.f_50682_);
            }
        }
    }

    private static void put(ServerPlayer p, BlockPos at, Block block) {
        p.m_9236_().m_7731_(at, block.m_49966_(), 2);
    }

    private OdysseyScenery() {
    }
}

