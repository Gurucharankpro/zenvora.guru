package dev.shadowsovereign.content;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
public final class DesertTreeFeature extends Feature<NoneFeatureConfiguration> {
 private final boolean palm;
 public DesertTreeFeature(boolean palm){super(NoneFeatureConfiguration.CODEC);this.palm=palm;}
 @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> c){return grow(c.level(),c.origin(),c.random(),palm);}
 static boolean grow(WorldGenLevel world,BlockPos pos,net.minecraft.util.RandomSource random,boolean palm){
  if(!DesertPlant.ground(world.getBlockState(pos.below())))return false;
  for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)for(int y=1;y<=4;y++)if(!world.isEmptyBlock(pos.offset(x,y,z)))return false;
  String name=palm?(random.nextBoolean()?"mirage_palm_tall":"mirage_palm_short"):(random.nextFloat()<.4?"glasswood_tree_large":"glasswood_tree_small");
  var template=world.getLevel().getStructureManager().get(new ResourceLocation(PolishContent.NS,"polish/"+name));if(template.isEmpty())throw new IllegalStateException("Missing supplied tree "+name);
  var t=template.get();BlockPos origin=pos.offset(-t.getSize().getX()/2,0,-t.getSize().getZ()/2);
  // Preflight the whole canopy: never replace buildings, rock, water or other trees.
  for(int x=0;x<t.getSize().getX();x++)for(int z=0;z<t.getSize().getZ();z++)for(int y=1;y<t.getSize().getY();y++){
   var s=world.getBlockState(origin.offset(x,y,z));if(!s.isAir()&&!s.canBeReplaced())return false;
  }
  return t.placeInWorld(world,origin,origin,new StructurePlaceSettings().setRotation(Rotation.NONE).setIgnoreEntities(true),random,2);
 }
}
