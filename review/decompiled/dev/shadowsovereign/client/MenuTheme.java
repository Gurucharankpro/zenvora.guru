/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4fc
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.platform.Lighting;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.RoyalTheme;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;

public final class MenuTheme {
    public static final ResourceLocation ATLAS = new ResourceLocation("shadowsovereign", "textures/gui/ascension_atlas.png");
    public static boolean reducedMotion = MenuTheme.loadMotion();
    private static final Map<String, LivingEntity> previews = new LinkedHashMap<String, LivingEntity>();
    private static final Set<String> unsupported = new HashSet<String>();

    private static boolean loadMotion() {
        return Boolean.parseBoolean(ClientPreferences.get("reducedMotion", "false"));
    }

    public static void toggleMotion() {
        reducedMotion = !reducedMotion;
        ClientPreferences.set("reducedMotion", Boolean.toString(reducedMotion));
    }

    public static void art(GuiGraphics g, int tile, int x, int y, int w, int h) {
        RoyalTheme.art(g, tile, x, y, w, h);
    }

    public static void panel(GuiGraphics g, int w, int h, int tile) {
        RoyalTheme.book(g, w, h, tile);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void portrait(GuiGraphics g, String type, UUID activeId, int x, int bottom, int size) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || unsupported.contains(type)) {
            return;
        }
        LivingEntity entity = null;
        if (activeId != null) {
            for (Entity e : mc.f_91073_.m_104735_()) {
                LivingEntity living;
                if (!e.m_20148_().equals(activeId) || !(e instanceof LivingEntity)) continue;
                entity = living = (LivingEntity)e;
                break;
            }
        }
        try {
            if (entity == null && (entity = previews.get(type)) == null) {
                Entity entity2;
                EntityType t;
                ResourceLocation key = ResourceLocation.m_135820_((String)type);
                EntityType entityType = t = key == null ? null : (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(key);
                if (t == null || !((entity2 = t.m_20615_((Level)mc.f_91073_)) instanceof LivingEntity)) {
                    return;
                }
                LivingEntity living = (LivingEntity)entity2;
                entity = living;
                if (previews.size() >= 16) {
                    previews.remove(previews.keySet().iterator().next());
                }
                previews.put(type, entity);
            }
            int fit = Math.max(8, Math.min(size, (int)(135.0f / Math.max(1.0f, entity.m_20206_()))));
            GuiGraphics portraitGraphics = new GuiGraphics(mc, g.m_280091_());
            portraitGraphics.m_280168_().m_85850_().m_252922_().set((Matrix4fc)g.m_280168_().m_85850_().m_252922_());
            portraitGraphics.m_280168_().m_85850_().m_252943_().set((Matrix3fc)g.m_280168_().m_85850_().m_252943_());
            float yaw = entity.m_146908_();
            float pitch = entity.m_146909_();
            float body = entity.f_20883_;
            float head = entity.f_20885_;
            float headOld = entity.f_20886_;
            Component name = entity.m_7770_();
            boolean nameVisible = entity.m_20151_();
            entity.m_6593_(null);
            entity.m_20340_(false);
            try {
                InventoryScreen.m_274545_((GuiGraphics)portraitGraphics, (int)x, (int)bottom, (int)fit, (float)(reducedMotion ? 0.0f : 10.0f), (float)0.0f, (LivingEntity)entity);
            }
            finally {
                entity.m_6593_(name);
                entity.m_20340_(nameVisible);
                entity.m_146922_(yaw);
                entity.m_146926_(pitch);
                entity.f_20883_ = body;
                entity.f_20885_ = head;
                entity.f_20886_ = headOld;
                mc.m_91290_().m_114468_(true);
                Lighting.m_84931_();
            }
        }
        catch (RuntimeException ex) {
            unsupported.add(type);
        }
    }

    public static void clear() {
        previews.clear();
        unsupported.clear();
    }
}

