package dev.dungeongates;

import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;

public final class DecorBlocks {
  public static class Column extends Block {
    public Column(Properties p) {
      super(p);
    }

    @Override
    public VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
      return box(3, 0, 3, 13, 16, 13);
    }
  }

  public static class Flower extends Column {
    public Flower(Properties p) {
      super(p);
    }

    @Override
    public VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
      return box(3, 0, 3, 13, 12, 13);
    }
  }

  public static class SixWay extends Column {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public SixWay(Properties p) {
      super(p);
      registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
      b.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
      return defaultBlockState().setValue(FACING, c.getClickedFace());
    }
  }

  public static class Horizontal extends Column {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public Horizontal(Properties p) {
      super(p);
      registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
      b.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
      return defaultBlockState().setValue(FACING, c.getHorizontalDirection().getOpposite());
    }
  }

  public static class Lantern extends Column {
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;

    public Lantern(Properties p) {
      super(p);
      registerDefaultState(stateDefinition.any().setValue(HANGING, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
      b.add(HANGING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
      return defaultBlockState().setValue(HANGING, c.getClickedFace() == Direction.DOWN);
    }
  }
}
