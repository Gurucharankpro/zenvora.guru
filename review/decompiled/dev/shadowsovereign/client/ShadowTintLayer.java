/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$AddLayers
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.shadowsovereign.client.ClientArmy;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

public final class ShadowTintLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    public ShadowTintLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    public void render(PoseStack pose, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbAmount, float partial, float age, float yaw, float pitch) {
        if (entity.m_20145_() || !ClientArmy.shadows.contains(entity.m_20148_())) {
            return;
        }
        this.m_117386_().m_7695_(pose, buffers.m_6299_(RenderType.m_110473_((ResourceLocation)this.m_117347_((Entity)entity))), light, OverlayTexture.f_118083_, 0.18f, 0.045f, 0.3f, 0.86f);
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        @SubscribeEvent
        public static void layers(EntityRenderersEvent.AddLayers event) {
            for (EntityType type : ForgeRegistries.ENTITY_TYPES.getValues()) {
                try {
                    LivingEntityRenderer renderer = event.getRenderer(type);
                    if (renderer == null) continue;
                    renderer.m_115326_(new ShadowTintLayer(renderer));
                }
                catch (ClassCastException classCastException) {}
            }
        }
    }
}

