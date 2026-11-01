package dev.dungeongates;
import net.minecraft.core.*;import net.minecraft.server.level.ServerLevel;import net.minecraft.world.level.*;import net.minecraft.world.level.levelgen.*;import net.minecraft.world.level.levelgen.feature.*;import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;import net.minecraft.world.level.block.Blocks;import net.minecraft.util.RandomSource;
public final class GateFeature extends Feature<NoneFeatureConfiguration> {
 public GateFeature(){super(NoneFeatureConfiguration.CODEC);}
 public static long mix(long x){x=(x^(x>>>30))*0xbf58476d1ce4e5b9L;x=(x^(x>>>27))*0x94d049bb133111ebL;return x^(x>>>31);}
 public static GateKind selected(long seed,int cx,int cz){long v=mix(seed ^ cx*341873128712L ^ cz*132897987541L);int total=DGConfig.EMERALD_WEIGHT.get()+DGConfig.FROZEN_WEIGHT.get();return Math.floorMod(v,total)<DGConfig.EMERALD_WEIGHT.get()?GateKind.EMERALD:GateKind.FROZEN;}
 public static boolean candidate(long seed,int cx,int cz){int spacing=DGConfig.SPACING.get();if(Math.floorMod(cx,spacing)!=spacing/2||Math.floorMod(cz,spacing)!=spacing/2)return false;return (mix(seed^cx*519317L^cz*78533L)>>>11)*0x1.0p-53<DGConfig.FREQUENCY.get();}
 public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> c){var l=c.level();if(!l.getLevel().dimension().equals(Level.OVERWORLD))return false;int cx=c.origin().getX()>>4,cz=c.origin().getZ()>>4;
  if(!candidate(l.getSeed(),cx,cz))return false;var kind=selected(l.getSeed(),cx,cz);BlockPos p=new BlockPos(cx*16+8,0,cz*16+8);int y=l.getHeight(Heightmap.Types.WORLD_SURFACE_WG,p.getX(),p.getZ());p=new BlockPos(p.getX(),y,p.getZ());if(!terrain(l,p))return false;
  var block=kind==GateKind.EMERALD?DGRegistry.EMERALD_GATE:DGRegistry.FROZEN_GATE;return l.setBlock(p,block.get().defaultBlockState().setValue(GateBlock.FACING,Direction.from2DDataValue((int)(mix(l.getSeed()^cx^cz)&3))),2);
 }
 public static boolean terrain(WorldGenLevel l,BlockPos p){if(p.getY()<l.getMinBuildHeight()+8||p.getY()+8>=l.getMaxBuildHeight())return false;
  for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){
   var q=p.offset(x,0,z);var floor=l.getBlockState(q.below());if(!floor.isFaceSturdy(l,q.below(),Direction.UP)||!floor.getFluidState().isEmpty()||floor.is(net.minecraft.tags.BlockTags.LEAVES)||floor.is(net.minecraft.tags.BlockTags.LOGS)||floor.is(Blocks.MAGMA_BLOCK)||floor.is(Blocks.CACTUS)||floor.is(Blocks.CAMPFIRE)||floor.is(Blocks.SOUL_CAMPFIRE))return false;
   for(int y=0;y<8;y++)if(!l.getBlockState(q.above(y)).isAir())return false;
  }return true;
 }
}
