package dev.dungeongates;

import net.minecraftforge.common.ForgeConfigSpec;

public final class DGConfig {
  public static final ForgeConfigSpec SPEC;
  public static final ForgeConfigSpec.IntValue SPACING,
      EMERALD_WEIGHT,
      FROZEN_WEIGHT,
      MAX_INSTANCES;
  public static final ForgeConfigSpec.DoubleValue FREQUENCY, HEALTH, DAMAGE;

  static {
    var b = new ForgeConfigSpec.Builder();
    b.comment(
            "Applies only to newly generated Overworld chunks. Two supplied gates are active; no"
                + " placeholder gates.")
        .push("generation");
    SPACING = b.defineInRange("spacingChunks", 12, 4, 64);
    FREQUENCY = b.defineInRange("cellSpawnChance", 0.75, 0.01, 1);
    EMERALD_WEIGHT = b.defineInRange("emeraldWeight", 30, 1, 1000);
    FROZEN_WEIGHT = b.defineInRange("frozenWeight", 24, 1, 1000);
    b.pop();
    b.push("combat");
    HEALTH = b.defineInRange("healthMultiplier", 1.0, .25, 4);
    DAMAGE = b.defineInRange("damageMultiplier", 1.0, .25, 4);
    b.pop();
    MAX_INSTANCES = b.defineInRange("maximumPersistentInstances", 8192, 1, 262144);
    SPEC = b.build();
  }
}
