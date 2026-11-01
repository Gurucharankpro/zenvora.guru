package dev.shadowsovereign.content;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
/** Bounded worldgen only: no tick scanning and no retroactive world edits. */
public final class DesertEcologyFeature extends Feature<NoneFeatureConfiguration> {
 public DesertEcologyFeature(){super(NoneFeatureConfiguration.CODEC);}
 @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> c){
  var w=c.level();var r=c.random();BlockPos start=c.origin();int placed=0;
  String[] plants={"whispering_desert_grass","golden_dune_fern","crystal_cactus","glass_thorn_bush","singing_crystal_cluster","moon_sand_mushroom"};
  int[] attempts={18,4,3,2,3,2};
  for(int k=0;k<plants.length;k++)for(int a=0;a<attempts[k];a++){
   BlockPos p=w.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG,start.offset(r.nextInt(16),0,r.nextInt(16)));
   if(!w.isEmptyBlock(p)||!DesertPlant.ground(w.getBlockState(p.below())))continue;
   if(k==5&&w.canSeeSky(p))continue;
   var s=PolishContent.block(plants[k]).defaultBlockState();if(s.canSurvive(w,p)){w.setBlock(p,s,2);placed++;}
  }
  BlockPos center=w.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG,start.offset(8,0,8));
  boolean clear=w.getLevel().structureManager().getAllStructuresAt(center).isEmpty();
  boolean oasis=false;
  if(clear&&r.nextInt(56)==0){
   boolean flat=true;int radius=5;
   for(int x=-radius;x<=radius;x++)for(int z=-radius;z<=radius;z++){
    BlockPos p=w.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG,center.offset(x,0,z));
    if(Math.abs(p.getY()-center.getY())>1||!DesertPlant.ground(w.getBlockState(p.below()))||!w.isEmptyBlock(p.above()))flat=false;
   }
   if(flat){
    oasis=true;
    for(int x=-radius;x<=radius;x++)for(int z=-radius;z<=radius;z++){
     int dist=x*x+z*z;if(dist>radius*radius)continue;
     BlockPos p=center.offset(x,-1,z);
     w.setBlock(p.below(2),PolishContent.block("ancient_desert_sandstone").defaultBlockState(),2);
     if(dist<(radius-1)*(radius-1)){
      // Remove only natural terrain/our vegetation above the new waterline;
      // slight height differences must not leave sand covering the pond.
      for(int dy=0;dy<=2;dy++){BlockPos air=center.offset(x,dy,z);var old=w.getBlockState(air);if(DesertPlant.ground(old)||old.getBlock() instanceof DesertPlant||old.canBeReplaced())w.setBlock(air,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),2);}
      w.setBlock(p.below(),net.minecraft.world.level.block.Blocks.WATER.defaultBlockState(),2);w.setBlock(p,net.minecraft.world.level.block.Blocks.WATER.defaultBlockState(),2);
     }else{
      w.setBlock(p,PolishContent.block("violet_dune_sand").defaultBlockState(),2);
      if(r.nextInt(4)==0&&w.isEmptyBlock(p.above()))w.setBlock(p.above(),PolishContent.block("mirage_lotus").defaultBlockState(),2);
     }
    }
    for(int[] v:new int[][]{{-6,0},{6,0},{0,-6},{0,6},{-5,5},{5,-5}}){BlockPos p=w.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG,center.offset(v[0],0,v[1]));DesertTreeFeature.grow(w,p,r,true);}
   }
  }
  if(clear&&!oasis&&r.nextFloat()<.35)DesertTreeFeature.grow(w,center,r,false);
  if(clear&&!oasis&&r.nextFloat()<.12)DesertTreeFeature.grow(w,center,r,true);
  // At most one bounded formation, entirely within this chunk's footprint.
  if(clear&&!oasis&&r.nextInt(3)==0&&DesertPlant.ground(w.getBlockState(center.below()))){
   int h=10+r.nextInt(15);boolean free=true;
   for(int y=0;y<h;y++)for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++)if(!w.getBlockState(center.offset(x,y,z)).isAir()&&!w.getBlockState(center.offset(x,y,z)).canBeReplaced())free=false;
   if(free){
    for(int y=0;y<h;y++){int radius=y>h-5?0:1;for(int x=-radius;x<=radius;x++)for(int z=-radius;z<=radius;z++)w.setBlock(center.offset(x,y,z),(x==0&&z==0&&y%4==0?PolishContent.block("singing_crystal_block"):net.minecraft.world.level.block.Blocks.YELLOW_STAINED_GLASS).defaultBlockState(),2);}
   }
  }
  return placed>0;
 }
}
