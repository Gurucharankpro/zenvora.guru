package dev.shadowsovereign.content;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
/** Preserve the previous renderer choice; only the supplied translucent new
 * creature atlases join Glass Ronin's existing translucent rendering path. */
public final class ContentRenderSupport {
 public static RenderType layer(ResourceLocation texture){
  String path=texture.getPath();
  if(path.contains("glass_ronin")||path.contains("yuta_okkotsu")||path.contains("porcelain_duchess"))return RenderType.entityTranslucent(texture);
  return RenderType.entityCutoutNoCull(texture);
 }
}
