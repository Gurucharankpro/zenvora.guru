package dev.shadowsovereign.content;
import com.google.gson.*;
import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.phys.shapes.*;

/** Actual authored outline, including tall props; collision is disabled by registration. */
public final class ContentDecorationBlock extends GlazedTerracottaBlock {
 private final VoxelShape[] outlines=new VoxelShape[4];
 public ContentDecorationBlock(BlockBehaviour.Properties properties,String id){
  super(properties);
  JsonObject info=null;
  for(JsonElement e:ContentBootstrap.BLOCKS)if(e.getAsJsonObject().get("id").getAsString().equals(id)){info=e.getAsJsonObject();break;}
  if(info==null)throw new IllegalStateException("Missing authored bounds for "+id);
  JsonArray b=info.getAsJsonArray("visual_bounds");double[] lo=new double[3],hi=new double[3];
  for(int i=0;i<3;i++){lo[i]=b.get(0).getAsJsonArray().get(i).getAsDouble();hi[i]=b.get(1).getAsJsonArray().get(i).getAsDouble();}
  outlines[0]=Shapes.box(lo[0]/16,lo[1]/16,lo[2]/16,hi[0]/16,hi[1]/16,hi[2]/16);
  outlines[1]=Shapes.box((16-hi[2])/16,lo[1]/16,lo[0]/16,(16-lo[2])/16,hi[1]/16,hi[0]/16);
  outlines[2]=Shapes.box((16-hi[0])/16,lo[1]/16,(16-hi[2])/16,(16-lo[0])/16,hi[1]/16,(16-lo[2])/16);
  outlines[3]=Shapes.box(lo[2]/16,lo[1]/16,(16-hi[0])/16,hi[2]/16,hi[1]/16,(16-lo[0])/16);
 }
 @Override public VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){
  return outlines[switch(state.getValue(FACING)){case EAST->1;case SOUTH->2;case WEST->3;default->0;}];
 }
}
