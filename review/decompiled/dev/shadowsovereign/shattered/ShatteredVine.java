/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.shadowsovereign.shattered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class ShatteredVine
extends Block {
    public ShatteredVine(BlockBehaviour.Properties p) {
        super(p);
    }

    public boolean m_7898_(BlockState s, LevelReader w, BlockPos at) {
        BlockState above = w.m_8055_(at.m_7494_());
        return above.m_60713_((Block)this) || above.m_204336_(BlockTags.f_13035_) || above.m_204336_(BlockTags.f_13106_) || above.m_60783_((BlockGetter)w, at.m_7494_(), Direction.DOWN);
    }

    public BlockState m_7417_(BlockState s, Direction d, BlockState other, LevelAccessor w, BlockPos at, BlockPos adjacent) {
        return d == Direction.UP && !this.m_7898_(s, (LevelReader)w, at) ? Blocks.f_50016_.m_49966_() : super.m_7417_(s, d, other, w, at, adjacent);
    }
}

