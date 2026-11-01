package dev.dungeongates;
import java.util.*;
import net.minecraftforge.registries.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.network.chat.Component;
public final class DGRegistry {
 public static final DeferredRegister<Block> BLOCKS=DeferredRegister.create(ForgeRegistries.BLOCKS,"dungeongates");
 public static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,"dungeongates");
 public static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,"dungeongates");
 public static final DeferredRegister<BlockEntityType<?>> BES=DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,"dungeongates");
 public static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(ForgeRegistries.FEATURES,"dungeongates");
 public static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,"dungeongates");
 public static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES=DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,"dungeongates");
 public static final Map<String,RegistryObject<net.minecraft.core.particles.SimpleParticleType>> SPARKS=new LinkedHashMap<>();
 public static final Map<String,RegistryObject<Block>> DECOR=new LinkedHashMap<>();
 public static final RegistryObject<Block> EMERALD_GATE=gate(GateKind.EMERALD),FROZEN_GATE=gate(GateKind.FROZEN);
 public static final String[] MOB_IDS={"mossbound_skulker","crystal_sentinel","emerald_root_warden","frostgrave_prowler","chainbound_sentinel","frozen_gravekeeper"};
 public static final List<RegistryObject<EntityType<DungeonMob>>> MOBS=new ArrayList<>();
 static {
  for(String id:new String[]{"frost_spark","crystal_shard","frost_snowflake","frost_mist","emerald_spark","green_mist","energy_strand","activation_burst","entry_vortex"})SPARKS.put(id,PARTICLES.register(id,()->new net.minecraft.core.particles.SimpleParticleType(false)));
  String[] names={"emerald_trial_stone","mossy_trial_bricks","cracked_emerald_bricks","polished_sanctuary_stone","glowing_emerald_crystal_block","emerald_crystal_cluster","ancient_root_column","emerald_hanging_vines","forgotten_garden_flower","sanctuary_lantern","guardian_rune_pillar","ancient_emerald_chest","frostgrave_stone","polished_frostgrave_stone","cracked_icebound_bricks","royal_frost_marble","glowing_blue_ice_crystal","frozen_crystal_cluster","silver_chain_column","winterveil_vines","ghostfrost_flower","royal_ice_lantern","frozen_memorial_statue","winter_royal_chest"};
  for(String name:names){var r=BLOCKS.register(name,()->makeDecor(name));DECOR.put(name,r);ITEMS.register(name,()->new BlockItem(r.get(),new Item.Properties()));}
  for(int i=0;i<MOB_IDS.length;i++){final int k=i;float w=i==2?1.6f:i==5?1.7f:i==0?.9f:1f;float h=i==2?3.5f:i==5?3.8f:i==0?1.1f:i==3?1.4f:2.2f;
   var r=ENTITIES.register(MOB_IDS[i],()->EntityType.Builder.<DungeonMob>of((t,l)->new DungeonMob(t,l,k),MobCategory.MONSTER).sized(w,h).clientTrackingRange(10).build("dungeongates:"+MOB_IDS[k]));MOBS.add(r);ITEMS.register(MOB_IDS[i]+"_spawn_egg",()->new ForgeSpawnEggItem(r,k<3?0x174D31:0x28435A,k<3?0x79FABC:0x91EEFF,new Item.Properties()));}
 }
 public static final RegistryObject<Item> EMERALD_TROPHY=ITEMS.register("emerald_wardens_crown",()->new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
 public static final RegistryObject<Item> FROZEN_TROPHY=ITEMS.register("frozen_kings_crown",()->new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
 public static final RegistryObject<BlockEntityType<GateAnchor>> GATE_BE=BES.register("gate_anchor",()->BlockEntityType.Builder.of(GateAnchor::new,EMERALD_GATE.get(),FROZEN_GATE.get()).build(null));
 public static final RegistryObject<BlockEntityType<TreasureCrate>> CRATE_BE=BES.register("treasure_crate",()->BlockEntityType.Builder.of(TreasureCrate::new,DECOR.get("ancient_emerald_chest").get(),DECOR.get("winter_royal_chest").get()).build(null));
 public static final RegistryObject<Feature<NoneFeatureConfiguration>> GATE_FEATURE=FEATURES.register("floating_gates",GateFeature::new);
 public static final RegistryObject<CreativeModeTab> TAB=TABS.register("gates",()->CreativeModeTab.builder().title(Component.translatable("itemGroup.dungeongates")).icon(()->new ItemStack(EMERALD_GATE.get())).displayItems((p,o)->ITEMS.getEntries().forEach(x->o.accept(x.get()))).build());
 static RegistryObject<Block> gate(GateKind k){var r=BLOCKS.register(k.id+"_gate",()->new GateBlock(k));ITEMS.register(k.id+"_gate",()->new BlockItem(r.get(),new Item.Properties().stacksTo(16)));return r;}
 static Block makeDecor(String n){var p=BlockBehaviour.Properties.of().strength(2.5f,6).sound(n.contains("crystal")?SoundType.AMETHYST:SoundType.STONE);
  if(n.contains("glowing")||n.contains("lantern"))p.lightLevel(s->13);if(n.contains("flower"))return new DecorBlocks.Flower(p.noCollission().noOcclusion());
  if(n.contains("vines"))return new VineBlock(p.noCollission().noOcclusion());
  if(n.contains("chest"))return new CrateBlock(p.noOcclusion());
  if(n.contains("lantern"))return new DecorBlocks.Lantern(p.noOcclusion());
  if(n.equals("frozen_crystal_cluster"))return new DecorBlocks.SixWay(p.noOcclusion());
  if(n.equals("frozen_memorial_statue")||n.equals("guardian_rune_pillar"))return new DecorBlocks.Horizontal(p.noOcclusion());
  if(n.contains("cluster")||n.contains("column"))return new DecorBlocks.Column(p.noOcclusion());
  return new Block(p);
 }
 public static Block block(String id){return DECOR.get(id).get();}
 public static void register(IEventBus b){BLOCKS.register(b);ITEMS.register(b);ENTITIES.register(b);BES.register(b);FEATURES.register(b);PARTICLES.register(b);TABS.register(b);}
}
