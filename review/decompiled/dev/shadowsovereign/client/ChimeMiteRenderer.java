/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.EyesLayer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.resources.ResourceLocation
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.DevourerModel;
import dev.shadowsovereign.mythic.ChimeMite;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public final class ChimeMiteRenderer
extends MobRenderer<ChimeMite, DevourerModel<ChimeMite>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("shadowsovereign", "textures/entity/chime_mite.png");
    private static final ResourceLocation GLOW = new ResourceLocation("shadowsovereign", "textures/entity/chime_mite_glow.png");

    public ChimeMiteRenderer(EntityRendererProvider.Context context) {
        super(context, new DevourerModel("chime_mite"), 0.3f);
        this.m_115326_((RenderLayer)new EyesLayer<ChimeMite, DevourerModel<ChimeMite>>((RenderLayerParent)this){

            public RenderType m_5708_() {
                return RenderType.m_110488_((ResourceLocation)GLOW);
            }
        });
    }

    public ResourceLocation getTextureLocation(ChimeMite mob) {
        return TEXTURE;
    }
}

