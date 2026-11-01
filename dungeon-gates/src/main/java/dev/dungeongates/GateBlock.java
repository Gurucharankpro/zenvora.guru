package dev.dungeongates;

import javax.annotation.Nullable;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;

public final class GateBlock extends BaseEntityBlock {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public final GateKind kind;

  public GateBlock(GateKind k) {
    super(Properties.of().strength(-1, 3600000).noCollission().noOcclusion().lightLevel(s -> 12));
    kind = k;
    registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
    b.add(FACING);
  }

  public BlockState getStateForPlacement(BlockPlaceContext c) {
    return defaultBlockState().setValue(FACING, c.getHorizontalDirection());
  }

  public RenderShape getRenderShape(BlockState s) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  public VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
    return Shapes.empty();
  }

  public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
    return new GateAnchor(p, s);
  }

  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level l, BlockState s, BlockEntityType<T> t) {
    return l.isClientSide
        ? null
        : createTickerHelper(t, DGRegistry.GATE_BE.get(), GateAnchor::tick);
  }
}
