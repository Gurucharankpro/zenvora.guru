package dev.shadowsovereign.content;
import com.mojang.blaze3d.vertex.*;
import dev.shadowsovereign.client.DevourerModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid="shadowsovereign",bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class PolishClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerEntityRenderer(PolishContent.JERBOA.get(),Renderer::new);}
 static final class Renderer extends MobRenderer<PrismJerboa,DevourerModel<PrismJerboa>> {
  Renderer(EntityRendererProvider.Context c){super(c,new DevourerModel<>("prism_jerboa"),.25f);
   addLayer(new RenderLayer<PrismJerboa,DevourerModel<PrismJerboa>>(this){@Override public void render(PoseStack p,MultiBufferSource b,int light,PrismJerboa e,float limb,float amount,float partial,float age,float yaw,float pitch){getParentModel().renderToBuffer(p,b.getBuffer(RenderType.eyes(new ResourceLocation("shadowsovereign","textures/entity/prism_jerboa_glow.png"))),15728640,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,1,1,1,1);}});
  }
  @Override public ResourceLocation getTextureLocation(PrismJerboa e){return new ResourceLocation("shadowsovereign","textures/entity/prism_jerboa.png");}
 }
}
