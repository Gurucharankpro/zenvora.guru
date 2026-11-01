package dev.dungeongates;
import net.minecraft.core.*;import net.minecraft.world.*;import net.minecraft.world.entity.player.Player;import net.minecraft.world.level.*;import net.minecraft.world.level.block.*;import net.minecraft.world.level.block.entity.*;import net.minecraft.world.level.block.state.*;import net.minecraft.world.level.block.state.properties.*;import net.minecraft.world.item.context.BlockPlaceContext;import net.minecraft.world.phys.BlockHitResult;import net.minecraft.network.chat.Component;
public final class CrateBlock extends BaseEntityBlock {
 public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;
 public CrateBlock(Properties p){super(p);registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));}
 protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState>b){b.add(FACING);}
 public BlockState getStateForPlacement(BlockPlaceContext c){return defaultBlockState().setValue(FACING,c.getHorizontalDirection().getOpposite());}
 public RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}
 public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new TreasureCrate(p,s);}
 public InteractionResult use(BlockState s,Level l,BlockPos p,Player player,InteractionHand h,BlockHitResult hit){if(l.isClientSide)return InteractionResult.SUCCESS;
  if(l instanceof net.minecraft.server.level.ServerLevel sl && !DungeonState.canLoot(sl,p)){player.displayClientMessage(Component.translatable("dg.reward_locked"),true);return InteractionResult.CONSUME;}
  if(l.getBlockEntity(p) instanceof TreasureCrate c)player.openMenu(c);return InteractionResult.CONSUME;}
 public void onRemove(BlockState a,Level l,BlockPos p,BlockState b,boolean moving){if(!a.is(b.getBlock())&&l.getBlockEntity(p) instanceof TreasureCrate c){Containers.dropContents(l,p,c);l.updateNeighbourForOutputSignal(p,this);}super.onRemove(a,l,p,b,moving);}
}
