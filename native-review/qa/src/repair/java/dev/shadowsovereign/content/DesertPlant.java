package dev.shadowsovereign.content;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.phys.shapes.*;
public final class DesertPlant extends BushBlock {
 private final boolean mineral;
 public DesertPlant(BlockBehaviour.Properties p,boolean mineral){super(p);this.mineral=mineral;}
 public static boolean ground(BlockState s){var key=net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(s.getBlock());String id=key==null?"":key.getPath();return id.contains("sand")||id.equals("polished_mirage_stone")||id.equals("singing_crystal_block");}
 @Override protected boolean mayPlaceOn(BlockState s,BlockGetter l,BlockPos p){return mineral?s.isFaceSturdy(l,p,net.minecraft.core.Direction.UP):ground(s)||super.mayPlaceOn(s,l,p);}
 @Override public VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){return Block.box(2,0,2,14,14,14);}
}
