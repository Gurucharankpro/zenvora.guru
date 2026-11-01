package dev.dungeongates.client;
import dev.dungeongates.*;import net.minecraftforge.api.distmarker.Dist;import net.minecraftforge.fml.common.Mod;import net.minecraftforge.eventbus.api.SubscribeEvent;import net.minecraftforge.client.event.EntityRenderersEvent;import net.minecraftforge.client.event.ModelEvent;import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;import net.minecraft.client.renderer.ItemBlockRenderTypes;import net.minecraft.client.renderer.RenderType;
@Mod.EventBusSubscriber(modid="dungeongates",bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class DGClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(DGRegistry.GATE_BE.get(),GateRenderer::new);for(var t:DGRegistry.MOBS)e.registerEntityRenderer(t.get(),MobRenderer::new);}
 @SubscribeEvent public static void particles(net.minecraftforge.client.event.RegisterParticleProvidersEvent e){for(var t:DGRegistry.SPARKS.values())e.registerSpriteSet(t.get(),SparkParticle.Provider::new);}
 @SubscribeEvent public static void loaders(ModelEvent.RegisterGeometryLoaders e){e.register("authored_mesh",new AuthoredMesh.Loader());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->{for(var b:DGRegistry.DECOR.values())ItemBlockRenderTypes.setRenderLayer(b.get(),RenderType.cutout());});}
}
