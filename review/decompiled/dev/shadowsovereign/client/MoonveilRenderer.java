/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.EyesLayer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.CarpetBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.VineBlock
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.DevourerModel;
import dev.shadowsovereign.moonveil.MoonveilContent;
import dev.shadowsovereign.moonveil.Nuna;
import dev.shadowsovereign.shattered.ShatteredCluster;
import dev.shadowsovereign.shattered.ShatteredVine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class MoonveilRenderer
extends MobRenderer<Nuna, DevourerModel<Nuna>> {
    public MoonveilRenderer(EntityRendererProvider.Context c) {
        super(c, new DevourerModel("nuna"), 0.3f);
        this.m_115326_((RenderLayer)new EyesLayer<Nuna, DevourerModel<Nuna>>((RenderLayerParent)this){

            public RenderType m_5708_() {
                return RenderType.m_110488_((ResourceLocation)new ResourceLocation("shadowsovereign", "textures/entity/nuna_glow.png"));
            }
        });
    }

    public ResourceLocation getTextureLocation(Nuna n) {
        return new ResourceLocation("shadowsovereign", "textures/entity/nuna.png");
    }

    @SubscribeEvent
    public static void renderer(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer((EntityType)MoonveilContent.NUNA.get(), MoonveilRenderer::new);
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> MoonveilContent.blocks.values().forEach(v -> {
            Block b = (Block)v.get();
            if (b instanceof ShatteredCluster || b instanceof ShatteredVine || b instanceof BushBlock || b instanceof LeavesBlock || b instanceof VineBlock || b instanceof CarpetBlock) {
                ItemBlockRenderTypes.setRenderLayer((Block)b, (RenderType)RenderType.m_110463_());
            }
        }));
    }
}

