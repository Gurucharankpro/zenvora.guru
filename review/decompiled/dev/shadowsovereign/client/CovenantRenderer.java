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
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.DevourerModel;
import dev.shadowsovereign.covenant.CovenantContent;
import dev.shadowsovereign.covenant.CovenantFauna;
import java.util.Map;
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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class CovenantRenderer
extends MobRenderer<CovenantFauna, DevourerModel<CovenantFauna>> {
    private final ResourceLocation texture;

    public CovenantRenderer(EntityRendererProvider.Context c, int kind) {
        super(c, new DevourerModel("bound_" + CovenantContent.ANIMALS[kind]), 0.3f);
        final String id = "bound_" + CovenantContent.ANIMALS[kind];
        this.texture = new ResourceLocation("shadowsovereign", "textures/entity/" + id + ".png");
        this.m_115326_((RenderLayer)new EyesLayer<CovenantFauna, DevourerModel<CovenantFauna>>((RenderLayerParent)this){

            public RenderType m_5708_() {
                return RenderType.m_110488_((ResourceLocation)new ResourceLocation("shadowsovereign", "textures/entity/" + id + "_glowmask.png"));
            }
        });
    }

    public ResourceLocation getTextureLocation(CovenantFauna mob) {
        return this.texture;
    }

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers e) {
        for (int k = 0; k < 3; ++k) {
            int kind = k;
            e.registerEntityRenderer((EntityType)CovenantContent.FAUNA.get(k).get(), c -> new CovenantRenderer(c, kind));
        }
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            for (Map.Entry<String, RegistryObject<Block>> entry : CovenantContent.blocks.entrySet()) {
                Block b = (Block)entry.getValue().get();
                if (!(b instanceof BushBlock) && !(b instanceof LeavesBlock) && !(b instanceof DoorBlock) && !(b instanceof TrapDoorBlock) && !entry.getKey().equals("mourning_vine")) continue;
                ItemBlockRenderTypes.setRenderLayer((Block)b, (RenderType)RenderType.m_110463_());
            }
        });
    }
}

