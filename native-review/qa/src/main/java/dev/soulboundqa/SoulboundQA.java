package dev.soulboundqa;
@net.minecraftforge.fml.common.Mod("soulboundqa")
public final class SoulboundQA {
 public SoulboundQA(){
  org.slf4j.LoggerFactory.getLogger("SoulboundQA").info("QA bootstrap: smoke={}, distribution={}",System.getProperty("soulboundqa.smoke"),net.minecraftforge.fml.loading.FMLEnvironment.dist);
  net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,()->()->net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(ClientReviewSmoke.class));
 }
}
