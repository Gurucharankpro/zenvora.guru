package dev.dungeongates.client;

import dev.dungeongates.DungeonMob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public final class MobRenderer extends GeoEntityRenderer<DungeonMob> {
  public MobRenderer(EntityRendererProvider.Context c) {
    super(c, new Model());
    shadowRadius = .65f;
    addRenderLayer(new AutoGlowingGeoLayer<>(this));
  }

  static final class Model extends GeoModel<DungeonMob> {
    public ResourceLocation getModelResource(DungeonMob m) {
      return new ResourceLocation("dungeongates", "geo/entity/" + m.id() + ".geo.json");
    }

    public ResourceLocation getTextureResource(DungeonMob m) {
      return new ResourceLocation("dungeongates", "textures/entity/" + m.id() + ".png");
    }

    public ResourceLocation getAnimationResource(DungeonMob m) {
      return new ResourceLocation(
          "dungeongates", "animations/entity/" + m.id() + ".animation.json");
    }
  }
}
