/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.shadowsovereign.shattered;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ShatteredCluster
extends Block {
    public ShatteredCluster(BlockBehaviour.Properties p) {
        super(p);
    }

    public VoxelShape m_5940_(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) {
        return Block.m_49796_((double)3.0, (double)1.0, (double)3.0, (double)13.0, (double)15.0, (double)13.0);
    }
}

