/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.AABB
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.eclipse.EclipseContent;
import dev.shadowsovereign.eclipse.EclipseState;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

public final class EclipseCampRenderer
implements BlockEntityRenderer<EclipseContent.CampEntity> {
    private final Map<String, LivingEntity> cache = new LinkedHashMap<String, LivingEntity>();
    private final Set<String> unsupported = new HashSet<String>();
    private Level world;

    public EclipseCampRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(EclipseContent.CampEntity be, float partial, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        block15: {
            block14: {
                Block block = be.m_58900_().m_60734_();
                if (!(block instanceof EclipseContent.CampBlock)) break block14;
                EclipseContent.CampBlock b = (EclipseContent.CampBlock)block;
                if (b.kind == EclipseState.Station.GALLERY && !be.type.isEmpty() && be.m_58904_() != null) break block15;
            }
            return;
        }
        if (this.world != be.m_58904_()) {
            this.cache.clear();
            this.unsupported.clear();
            this.world = be.m_58904_();
        }
        if (this.unsupported.contains(be.type)) {
            EclipseCampRenderer.fallback(pose, buffers);
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        try {
            LivingEntity entity = this.cache.get(be.type);
            if (entity == null) {
                Entity entity2;
                EntityType type = (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.m_135820_((String)be.type));
                if (type == null || !((entity2 = type.m_20615_(be.m_58904_())) instanceof LivingEntity)) {
                    this.unsupported.add(be.type);
                    EclipseCampRenderer.fallback(pose, buffers);
                    return;
                }
                LivingEntity living = (LivingEntity)entity2;
                entity = living;
                entity.m_6593_(null);
                entity.m_20340_(false);
                if (this.cache.size() >= 16) {
                    this.cache.remove(this.cache.keySet().iterator().next());
                }
                this.cache.put(be.type, entity);
            }
            float scale = (float)Math.min(0.45, (double)(1.0f / Math.max(1.0f, entity.m_20206_())));
            pose.m_85836_();
            pose.m_85837_(0.5, 0.84, 0.5);
            pose.m_85841_(scale, scale, scale);
            pose.m_252781_(Axis.f_252436_.m_252977_(MenuTheme.reducedMotion ? 135.0f : ((float)be.m_58904_().m_46467_() + partial) * 1.2f));
            mc.m_91290_().m_114468_(false);
            try {
                mc.m_91290_().m_114384_((Entity)entity, 0.0, 0.0, 0.0, 0.0f, 0.0f, pose, buffers, 0xF000F0);
            }
            finally {
                mc.m_91290_().m_114468_(true);
                pose.m_85849_();
            }
        }
        catch (RuntimeException ex) {
            this.unsupported.add(be.type);
        }
    }

    private static void fallback(PoseStack pose, MultiBufferSource buffers) {
        LevelRenderer.m_109646_((PoseStack)pose, (VertexConsumer)buffers.m_6299_(RenderType.m_110504_()), (AABB)new AABB(0.3, 0.84, 0.3, 0.7, 1.64, 0.7), (float)0.75f, (float)0.55f, (float)1.0f, (float)1.0f);
    }

    public boolean shouldRenderOffScreen(EclipseContent.CampEntity e) {
        return false;
    }
}

