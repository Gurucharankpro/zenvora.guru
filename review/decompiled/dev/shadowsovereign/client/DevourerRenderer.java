/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.shadowsovereign.client.DevourerModel;
import dev.shadowsovereign.mythic.MythicContent;
import dev.shadowsovereign.mythic.SoulDevourer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class DevourerRenderer
extends MobRenderer<SoulDevourer, DevourerModel<SoulDevourer>> {
    public DevourerRenderer(EntityRendererProvider.Context c) {
        super(c, new DevourerModel(), 1.2f);
    }

    public ResourceLocation getTextureLocation(SoulDevourer mob) {
        return DevourerModel.TEXTURE;
    }

    protected void scale(SoulDevourer mob, PoseStack p, float partial) {
        p.m_85841_(0.65f, 0.65f, 0.65f);
    }

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer((EntityType)MythicContent.DEVOURER.get(), DevourerRenderer::new);
    }
}

