/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.joml.Vector3f
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.SoulWorldRenderer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class EclipseWorldRenderer {
    public static final int[] COLORS = new int[]{11764719, 8309744, 15584636, 15304093, 9559200, 14604528};
    private static final Set<UUID> seen = new HashSet<UUID>();
    private static int ticks;

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null) {
            seen.clear();
            return;
        }
        if (mc.m_91104_() || ++ticks % 10 != 0) {
            return;
        }
        HashMap styles = new HashMap();
        ClientEclipse.roster().stream().filter(n -> n.m_128403_("Entity")).limit(24L).forEach(n -> styles.put(n.m_128342_("Entity"), n));
        seen.retainAll(styles.keySet());
        int budget = MenuTheme.reducedMotion ? 2 : 12;
        for (Entity entity : mc.f_91073_.m_104735_()) {
            if (budget <= 0) break;
            CompoundTag n2 = (CompoundTag)styles.get(entity.m_20148_());
            if (n2 == null || mc.f_91074_ == null || entity.m_20280_((Entity)mc.f_91074_) > 2304.0) continue;
            boolean first = seen.add(entity.m_20148_());
            if (n2.m_128451_("Seal") == 0 && n2.m_128451_("Palette") == 0) continue;
            int color = COLORS[Math.floorMod(n2.m_128451_("Palette"), COLORS.length)];
            DustParticleOptions dust = new DustParticleOptions(new Vector3f((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f), 0.7f);
            int amount = first ? Math.min(8, budget) : 2;
            for (int i = 0; i < amount; ++i) {
                double a = (double)ticks * 0.05 + (double)i * Math.PI * 2.0 / (double)amount;
                double radius = Math.max(0.4, (double)entity.m_20205_() * 0.7);
                double y = n2.m_128451_("Seal") == 2 ? (double)entity.m_20206_() * (0.5 + 0.35 * Math.sin(a)) : (n2.m_128451_("Seal") == 3 ? (double)entity.m_20206_() * 0.7 : 0.15);
                mc.f_91073_.m_7106_((ParticleOptions)dust, entity.m_20185_() + Math.cos(a) * radius, entity.m_20186_() + y, entity.m_20189_() + Math.sin(a) * radius, 0.0, 0.012, 0.0);
                --budget;
            }
        }
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent e) {
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || mc.f_91074_ == null) {
            return;
        }
        CompoundTag run = ClientEclipse.data.m_128469_("Run");
        if (!run.m_128456_() && run.m_128451_("Phase") == 0 && run.m_128461_("Dimension").equals(mc.f_91073_.m_46472_().m_135782_().toString())) {
            ListTag points = run.m_128437_("Points", 10);
            for (int i = 0; i < Math.min(3, points.size()); ++i) {
                String label;
                BlockPos at = BlockPos.m_122022_((long)points.m_128728_(i).m_128454_("Pos"));
                String kind = run.m_128461_("Kind");
                String string = kind.equals("REACTOR") ? ClientEclipse.t("vent", i + 1, ClientEclipse.t((run.m_128451_("Vents") & 1 << i) != 0 ? "open" : "closed", new Object[0])) : (kind.equals("INVESTIGATION") ? ClientEclipse.t("clue_marker", i + 1) : (label = kind.equals("CRYSTAL") && (run.m_128451_("Crystals") & 1 << i) != 0 ? ClientEclipse.t("crystal", i + 1) : ""));
                if (label.isEmpty()) continue;
                SoulWorldRenderer.label(e, label, (double)at.m_123341_() + 0.5, (double)at.m_123342_() + 1.7, (double)at.m_123343_() + 0.5, -1985281);
            }
            if (run.m_128441_("StrikeAt")) {
                Vec3 at = new Vec3(run.m_128459_("StrikeX"), run.m_128459_("StrikeY"), run.m_128459_("StrikeZ"));
                SoulWorldRenderer.label(e, "\u26a0", at.f_82479_, at.f_82480_ + 0.4, at.f_82481_, -38534);
                PoseStack pose = e.getPoseStack();
                pose.m_85836_();
                Vec3 camera = e.getCamera().m_90583_();
                pose.m_85837_(-camera.f_82479_, -camera.f_82480_, -camera.f_82481_);
                VertexConsumer buffer = mc.m_91269_().m_110104_().m_6299_(RenderType.m_110504_());
                for (int k = 0; k < 32; ++k) {
                    double a = (double)k * Math.PI / 16.0;
                    double b = (double)(k + 1) * Math.PI / 16.0;
                    LevelRenderer.m_109646_((PoseStack)pose, (VertexConsumer)buffer, (AABB)new AABB(at.f_82479_ + 3.0 * Math.cos(a), at.f_82480_ + 0.08, at.f_82481_ + 3.0 * Math.sin(a), at.f_82479_ + 3.0 * Math.cos(b), at.f_82480_ + 0.09, at.f_82481_ + 3.0 * Math.sin(b)), (float)1.0f, (float)0.2f, (float)0.35f, (float)0.9f);
                }
                pose.m_85849_();
            }
        }
        for (Tag tag : ClientEclipse.data.m_128437_("Scout", 10)) {
            CompoundTag n = (CompoundTag)tag;
            if (n.m_128454_("Until") <= ClientWarfront.now()) continue;
            SoulWorldRenderer.label(e, ModLanguage.user(n.m_128461_("Name")), n.m_128459_("X"), n.m_128459_("Y") + 0.4, n.m_128459_("Z"), -12669);
        }
        mc.m_91269_().m_110104_().m_109911_();
    }
}

