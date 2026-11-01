package dev.dungeongates;

import net.minecraft.resources.ResourceLocation;

public enum GateKind {
  EMERALD("emerald_trial", "EMERALD TRIAL", 1, 30, 0x48F592),
  FROZEN("frozen_grave", "FROZEN GRAVE", 2, 24, 0x89DAFF);
  public final String id, title;
  public final int number, weight, color;

  GateKind(String id, String title, int number, int weight, int color) {
    this.id = id;
    this.title = title;
    this.number = number;
    this.weight = weight;
    this.color = color;
  }

  public ResourceLocation resource(String path) {
    return new ResourceLocation("dungeongates", path);
  }

  public String category() {
    return this == EMERALD ? "REGULAR I" : "REGULAR II";
  }

  public static GateKind read(String id) {
    for (var k : values()) if (k.id.equals(id)) return k;
    throw new IllegalArgumentException("Unknown gate " + id);
  }
}
