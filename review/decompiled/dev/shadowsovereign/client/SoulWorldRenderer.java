/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.shadowsovereign.ascension.AscensionService;
import dev.shadowsovereign.client.ClientAscension;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class SoulWorldRenderer {
    private static long sortedAt = -100L;
    private static CompoundTag sortedData;
    private static List<CompoundTag> cachedSouls;

    @SubscribeEvent
    public static void render(RenderLevelStageEvent e) {
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || mc.f_91074_ == null || !mc.f_91073_.m_46472_().m_135782_().toString().equals(ClientAscension.data.m_128461_("SoulDimension"))) {
            return;
        }
        Vec3 camera = e.getCamera().m_90583_();
        PoseStack pose = e.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.m_91269_().m_110104_();
        long now = mc.f_91073_.m_46467_();
        if (sortedData != ClientAscension.data || now - sortedAt >= 10L || now < sortedAt) {
            sortedAt = now;
            sortedData = ClientAscension.data;
            cachedSouls = ClientAscension.data.m_128437_("Souls", 10).stream().map(t -> (CompoundTag)t).sorted(Comparator.comparingDouble(n -> mc.f_91074_.m_20275_(n.m_128459_("X"), n.m_128459_("Y"), n.m_128459_("Z")))).limit(16L).toList();
        }
        List<CompoundTag> souls = cachedSouls;
        int visibleSouls = 0;
        boolean hinted = false;
        for (CompoundTag t2 : souls) {
            boolean aimed;
            double z;
            double y;
            CompoundTag n2 = t2;
            double x = n2.m_128459_("X");
            if (mc.f_91074_.m_20275_(x, y = n2.m_128459_("Y"), z = n2.m_128459_("Z")) > 2304.0 || visibleSouls++ >= 16) continue;
            pose.m_85836_();
            pose.m_85837_(x - camera.f_82479_, y + 0.65 - camera.f_82480_, z - camera.f_82481_);
            pose.m_252781_(Axis.f_252436_.m_252977_(MenuTheme.reducedMotion ? 0.0f : ((float)mc.f_91073_.m_46467_() + e.getPartialTick()) * 2.0f));
            pose.m_85841_(0.8f, 0.8f, 0.8f);
            mc.m_91291_().m_269128_(new ItemStack((ItemLike)Items.f_42678_), ItemDisplayContext.GROUND, 0xF000F0, OverlayTexture.f_118083_, pose, (MultiBufferSource)buffers, (Level)mc.f_91073_, 0);
            pose.m_85849_();
            Vec3 to = new Vec3(x, y + 0.7, z).m_82546_(mc.f_91074_.m_146892_());
            boolean bl = aimed = !hinted && to.m_82556_() <= 36.0 && to.m_82541_().m_82526_(mc.f_91074_.m_20154_()) > 0.97;
            if (aimed) {
                hinted = true;
            }
            SoulWorldRenderer.label(e, ClientRoyal.t("arise", new Object[0]) + (String)(aimed ? " [" + ClientAscension.CLAIM.m_90863_().getString() + "]" : ""), x, y + 1.4, z, -2977793);
        }
        CompoundTag run = ClientAscension.data.m_128469_("AscensionRun");
        ListTag points = mc.f_91073_.m_46472_().m_135782_().toString().equals(run.m_128461_("Dimension")) ? run.m_128437_("Points", 10) : new ListTag();
        for (int i = 0; i < points.size(); ++i) {
            BlockPos at = BlockPos.m_122022_((long)points.m_128728_(i).m_128454_("Pos"));
            Object text = run.m_128451_("Mode") == 4 ? AscensionService.RUNES[Math.floorMod(run.m_128451_("Cache" + i), 3)] : (run.m_128451_("Mode") == 1 ? "Capture zone " + (i + 1) : "Relic " + (i + 1));
            SoulWorldRenderer.label(e, (String)text + " [" + ClientAscension.INTERACT.m_90863_().getString() + "]", (double)at.m_123341_() + 0.5, (double)at.m_123342_() + 1.5, (double)at.m_123343_() + 0.5, -11390);
        }
        buffers.m_109911_();
    }

    public static void label(RenderLevelStageEvent e, String text, double x, double y, double z, int color) {
        Minecraft mc = Minecraft.m_91087_();
        Vec3 camera = e.getCamera().m_90583_();
        PoseStack pose = e.getPoseStack();
        pose.m_85836_();
        pose.m_85837_(x - camera.f_82479_, y - camera.f_82480_, z - camera.f_82481_);
        pose.m_252781_(mc.m_91290_().m_253208_());
        pose.m_85841_(-0.025f, -0.025f, 0.025f);
        mc.f_91062_.m_272191_(FormattedCharSequence.m_13714_((String)ModLanguage.visual(mc.f_91062_, text), (Style)ModLanguage.component(text).m_7383_()), (float)(-ModLanguage.width(mc.f_91062_, text)) / 2.0f, 0.0f, color, false, pose.m_85850_().m_252922_(), (MultiBufferSource)mc.m_91269_().m_110104_(), Font.DisplayMode.NORMAL, -2012477419, 0xF000F0);
        pose.m_85849_();
    }

    static {
        cachedSouls = List.of();
    }
}

