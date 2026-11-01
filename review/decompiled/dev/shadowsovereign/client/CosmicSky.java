/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  com.mojang.blaze3d.vertex.VertexBuffer$Usage
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.DimensionSpecialEffects
 *  net.minecraft.client.renderer.DimensionSpecialEffects$SkyType
 *  net.minecraft.client.renderer.FogRenderer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.material.FogType
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.joml.Matrix4f
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class CosmicSky
extends DimensionSpecialEffects {
    private static VertexBuffer dome;
    private static int textureId;
    private static final ResourceLocation TEXTURE;

    public CosmicSky() {
        super(Float.NaN, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
    }

    @SubscribeEvent
    public static void register(RegisterDimensionSpecialEffectsEvent e) {
        e.register(new ResourceLocation("shadowsovereign", "cosmic"), (DimensionSpecialEffects)new CosmicSky());
    }

    public Vec3 m_5927_(Vec3 c, float light) {
        return new Vec3(0.045, 0.061, 0.1);
    }

    public boolean m_5781_(int x, int z) {
        return false;
    }

    public boolean renderClouds(ClientLevel w, int ticks, float partial, PoseStack pose, double x, double y, double z, Matrix4f projection) {
        return true;
    }

    public boolean renderSky(ClientLevel w, int ticks, float partial, PoseStack pose, Camera camera, Matrix4f projection, boolean foggy, Runnable setupFog) {
        if (camera.m_167685_() != FogType.NONE) {
            return true;
        }
        if (dome == null) {
            BufferBuilder b = Tesselator.m_85913_().m_85915_();
            b.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
            for (int row = 0; row < 24; ++row) {
                for (int col = 0; col < 64; ++col) {
                    CosmicSky.point(b, col, row);
                    CosmicSky.point(b, col + 1, row);
                    CosmicSky.point(b, col + 1, row + 1);
                    CosmicSky.point(b, col, row + 1);
                }
            }
            dome = new VertexBuffer(VertexBuffer.Usage.STATIC);
            dome.m_85921_();
            dome.m_231221_(b.m_231175_());
            VertexBuffer.m_85931_();
        }
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableCull();
        RenderSystem.disableBlend();
        FogRenderer.m_109017_();
        RenderSystem.setShader(GameRenderer::m_172820_);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)TEXTURE);
        AbstractTexture texture = Minecraft.m_91087_().m_91097_().m_118506_(TEXTURE);
        if (texture.m_117963_() != textureId) {
            texture.m_117960_(true, false);
            textureId = texture.m_117963_();
        }
        RenderSystem.setShaderColor((float)0.8f, (float)0.85f, (float)1.0f, (float)1.0f);
        pose.m_85836_();
        pose.m_252781_(Axis.f_252436_.m_252977_(((float)ticks + partial) * 6.0E-4f));
        dome.m_85921_();
        dome.m_253207_(pose.m_85850_().m_252922_(), projection, GameRenderer.m_172820_());
        VertexBuffer.m_85931_();
        pose.m_85849_();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableCull();
        RenderSystem.depthMask((boolean)true);
        setupFog.run();
        return true;
    }

    private static void point(BufferBuilder b, int x, int y) {
        double longitude = (double)x * Math.PI * 2.0 / 64.0;
        double latitude = (double)y * Math.PI / 24.0;
        float shade = Math.max(0.0f, Math.min(1.0f, (float)(12 - y) / 2.0f));
        b.m_5483_(Math.sin(latitude) * Math.cos(longitude) * 100.0, Math.cos(latitude) * 100.0, Math.sin(latitude) * Math.sin(longitude) * 100.0).m_7421_((float)x / 64.0f, (float)y / 24.0f).m_85950_(shade, shade, shade, 1.0f).m_5752_();
    }

    public static void release() {
        if (dome != null) {
            dome.close();
            dome = null;
            textureId = -1;
        }
    }

    static {
        textureId = -1;
        TEXTURE = new ResourceLocation("shadowsovereign", "textures/environment/cosmic_sky.png");
    }
}

