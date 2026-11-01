package dev.dungeongates;

import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("dungeongates")
public final class DungeonGates {
  public DungeonGates() {
    var bus = FMLJavaModLoadingContext.get().getModEventBus();
    DGRegistry.register(bus);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DGConfig.SPEC);
    bus.addListener(this::attributes);
    MinecraftForge.EVENT_BUS.register(new DungeonEvents());
  }

  private void attributes(EntityAttributeCreationEvent e) {
    for (var t : DGRegistry.MOBS)
      e.put(
          t.get(),
          Monster.createMonsterAttributes()
              .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 30)
              .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, .25)
              .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 5)
              .add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE, 32)
              .build());
  }
}
