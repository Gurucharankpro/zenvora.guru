/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.WorldGenLevel
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
 *  net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.moonveil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;

public final class MoonveilTree
extends Feature<BlockStateConfiguration> {
    public MoonveilTree() {
        super(BlockStateConfiguration.f_67546_);
    }

    public boolean m_142674_(FeaturePlaceContext<BlockStateConfiguration> c) {
        BlockPos ground;
        WorldGenLevel w = c.m_159774_();
        if (!w.m_8055_(ground = c.m_159777_().m_7495_()).m_204336_(BlockTags.f_144274_)) {
            return false;
        }
        String log = ForgeRegistries.BLOCKS.getKey((Object)((BlockStateConfiguration)c.m_159778_()).f_67547_.m_60734_()).m_135815_();
        boolean shattered = log.startsWith("shattered_");
        String tree = log.replace("shattered_", "").replace("moonveil_", "").replace("crimson_shadow_", "").replace("_log", "");
        boolean large = c.m_225041_().m_188503_(5) == 0;
        StructureTemplate template = w.m_6018_().m_215082_().m_230359_(new ResourceLocation("shadowsovereign", (shattered ? "shattered_trees/" : "moonveil_trees/") + tree + (shattered ? "_tree" : "") + (large ? "_large" : "_small")));
        Vec3i size = template.m_163801_();
        if (size.m_123341_() == 0 || c.m_159777_().m_123342_() + size.m_123342_() >= w.m_151558_()) {
            return false;
        }
        int anchorX = shattered && large && tree.equals("starfall") ? 5 : size.m_123341_() / 2;
        int anchorZ = shattered && large && tree.equals("starfall") ? 5 : size.m_123343_() / 2;
        BlockPos at = c.m_159777_().m_7918_(-anchorX, 0, -anchorZ);
        for (BlockPos p : BlockPos.m_121940_((BlockPos)at, (BlockPos)at.m_7918_(size.m_123341_() - 1, size.m_123342_() - 1, size.m_123343_() - 1))) {
            if (!w.m_7232_(p.m_123341_() >> 4, p.m_123343_() >> 4)) {
                return false;
            }
            BlockState state = w.m_8055_(p);
            if (state.m_60795_() || state.m_247087_() || state.m_204336_(BlockTags.f_13035_)) continue;
            return false;
        }
        return template.m_230328_((ServerLevelAccessor)w, at, at, new StructurePlaceSettings().m_74392_(true), c.m_225041_(), 2);
    }
}

