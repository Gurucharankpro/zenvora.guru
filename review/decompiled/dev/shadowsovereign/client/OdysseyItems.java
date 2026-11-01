/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.client.extensions.common.IClientItemExtensions
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.shadowsovereign.odyssey.OdysseyContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class OdysseyItems {
    private static final ResourceLocation ICONS = new ResourceLocation("shadowsovereign", "textures/item/odyssey_icons.png");

    public static IClientItemExtensions extension() {
        return new IClientItemExtensions(){
            private Renderer renderer;

            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new Renderer();
                }
                return this.renderer;
            }
        };
    }

    public static int index(ItemStack stack) {
        if (stack.m_150930_((Item)OdysseyContent.BOOK.get())) {
            return 0;
        }
        if (stack.m_150930_((Item)OdysseyContent.BALL.get())) {
            return 1;
        }
        if (stack.m_150930_((Item)OdysseyContent.PARCEL.get())) {
            return 2;
        }
        if (stack.m_150930_((Item)OdysseyContent.TOKEN.get())) {
            return 3;
        }
        if (stack.m_150930_((Item)OdysseyContent.LETTER.get())) {
            return 4;
        }
        if (stack.m_150930_((Item)OdysseyContent.FEAST.get())) {
            return 5;
        }
        for (int i = 0; i < 24; ++i) {
            if (!stack.m_150930_((Item)OdysseyContent.ARTIFACTS.get(i).get())) continue;
            return i + 8;
        }
        return 0;
    }

    public static void quad(PoseStack pose, MultiBufferSource buffers, int tile, int light, int overlay) {
        float u = ((float)(tile % 8) + 0.05f) / 8.0f;
        float v = ((float)(tile / 8) + 0.05f) / 4.0f;
        float u1 = ((float)(tile % 8) + 0.95f) / 8.0f;
        float v1 = ((float)(tile / 8) + 0.95f) / 4.0f;
        VertexConsumer out = buffers.m_6299_(RenderType.m_110473_((ResourceLocation)ICONS));
        Matrix4f matrix = pose.m_85850_().m_252922_();
        Matrix3f normal = pose.m_85850_().m_252943_();
        out.m_252986_(matrix, 0.0f, 0.0f, 0.5f).m_6122_(255, 255, 255, 255).m_7421_(u, v1).m_86008_(overlay).m_85969_(light).m_252939_(normal, 0.0f, 0.0f, 1.0f).m_5752_();
        out.m_252986_(matrix, 1.0f, 0.0f, 0.5f).m_6122_(255, 255, 255, 255).m_7421_(u1, v1).m_86008_(overlay).m_85969_(light).m_252939_(normal, 0.0f, 0.0f, 1.0f).m_5752_();
        out.m_252986_(matrix, 1.0f, 1.0f, 0.5f).m_6122_(255, 255, 255, 255).m_7421_(u1, v).m_86008_(overlay).m_85969_(light).m_252939_(normal, 0.0f, 0.0f, 1.0f).m_5752_();
        out.m_252986_(matrix, 0.0f, 1.0f, 0.5f).m_6122_(255, 255, 255, 255).m_7421_(u, v).m_86008_(overlay).m_85969_(light).m_252939_(normal, 0.0f, 0.0f, 1.0f).m_5752_();
    }

    private static final class Renderer
    extends BlockEntityWithoutLevelRenderer {
        Renderer() {
            super(Minecraft.m_91087_().m_167982_(), Minecraft.m_91087_().m_167973_());
        }

        public void m_108829_(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            OdysseyItems.quad(pose, buffers, OdysseyItems.index(stack), light, overlay);
        }
    }
}

