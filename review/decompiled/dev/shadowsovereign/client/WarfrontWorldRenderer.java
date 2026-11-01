/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientAscension;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.SoulWorldRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class WarfrontWorldRenderer {
    @SubscribeEvent
    public static void render(RenderLevelStageEvent e) {
        CompoundTag run;
        BlockPos at;
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        CompoundTag d = ClientWarfront.data;
        if (mc.f_91073_ == null || mc.f_91074_ == null || !mc.f_91073_.m_46472_().m_135782_().toString().equals(d.m_128461_("PlayerDimension"))) {
            return;
        }
        if (d.m_128441_("Marker") && d.m_128461_("MarkerDimension").equals(d.m_128461_("PlayerDimension")) && mc.f_91074_.m_20238_(Vec3.m_82512_((Vec3i)(at = BlockPos.m_122022_((long)d.m_128454_("Marker"))))) <= 1600.0) {
            SoulWorldRenderer.label(e, "RALLY MARKER", (double)at.m_123341_() + 0.5, at.m_123342_() + 1, (double)at.m_123343_() + 0.5, -11390);
        }
        if ((run = d.m_128469_("Run")).m_128451_("Phase") == 0 && d.m_128461_("Dimension").equals(d.m_128461_("PlayerDimension")) && run.m_128451_("Mission") == 1) {
            ListTag points = run.m_128437_("Points", 10);
            for (int i = 0; i < points.size(); ++i) {
                BlockPos at2 = BlockPos.m_122022_((long)points.m_128728_(i).m_128454_("Pos"));
                SoulWorldRenderer.label(e, (String)((run.m_128451_("Disabled") & 1 << i) != 0 ? "WARD DISABLED" : "WARD " + (i + 1) + " [" + ClientAscension.INTERACT.m_90863_().getString() + "]"), (double)at2.m_123341_() + 0.5, (double)at2.m_123342_() + 1.4, (double)at2.m_123343_() + 0.5, -3043329);
            }
        }
        for (Tag tag : d.m_128437_("Vigil", 10)) {
            CompoundTag n = (CompoundTag)tag;
            if (n.m_128454_("Until") <= ClientWarfront.now()) continue;
            SoulWorldRenderer.label(e, "VIGIL \u2022 " + ModLanguage.user(n.m_128461_("Name")), n.m_128459_("X"), n.m_128459_("Y"), n.m_128459_("Z"), -16020);
        }
        mc.m_91269_().m_110104_().m_109911_();
    }
}

