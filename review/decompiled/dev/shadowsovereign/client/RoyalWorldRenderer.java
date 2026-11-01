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
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.client.SoulWorldRenderer;
import dev.shadowsovereign.royal.RoyalContent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class RoyalWorldRenderer {
    private static final List<LivingEntity> wishes = new ArrayList<LivingEntity>();
    private static int ticks;

    @SubscribeEvent
    public static void royalWishes(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || mc.f_91074_ == null) {
            wishes.clear();
            return;
        }
        if (mc.m_91104_() || ++ticks % 20 != 0) {
            return;
        }
        HashSet<String> types = new HashSet<String>();
        for (Tag tag : ClientRoyal.data.m_128437_("Hunts", 10)) {
            CompoundTag n = (CompoundTag)tag;
            if (!n.m_128471_("Wanted")) continue;
            types.add(n.m_128461_("Type"));
        }
        wishes.clear();
        if (types.isEmpty()) {
            return;
        }
        for (Entity entity : mc.f_91073_.m_104735_()) {
            LivingEntity living;
            if (wishes.size() >= 5 || !(entity instanceof LivingEntity) || !(living = (LivingEntity)entity).m_6084_() || !(living.m_20280_((Entity)mc.f_91074_) <= 2304.0) || !mc.f_91074_.m_142582_((Entity)living) || !types.contains(String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey((Object)living.m_6095_())))) continue;
            wishes.add(living);
        }
    }

    private static void ring(RenderLevelStageEvent e, Vec3 at, double radius) {
        Minecraft mc = Minecraft.m_91087_();
        Vec3 camera = e.getCamera().m_90583_();
        PoseStack pose = e.getPoseStack();
        pose.m_85836_();
        pose.m_85837_(-camera.f_82479_, -camera.f_82480_, -camera.f_82481_);
        VertexConsumer buffer = mc.m_91269_().m_110104_().m_6299_(RenderType.m_110504_());
        for (int i = 0; i < 24; ++i) {
            double a = (double)i * Math.PI / 12.0;
            double b = (double)(i + 1) * Math.PI / 12.0;
            LevelRenderer.m_109646_((PoseStack)pose, (VertexConsumer)buffer, (AABB)new AABB(at.f_82479_ + Math.cos(a) * radius, at.f_82480_ + 0.08, at.f_82481_ + Math.sin(a) * radius, at.f_82479_ + Math.cos(b) * radius, at.f_82480_ + 0.1, at.f_82481_ + Math.sin(b) * radius), (float)0.8f, (float)0.55f, (float)1.0f, (float)0.9f);
        }
        pose.m_85849_();
    }

    @SubscribeEvent
    public static void royalWorld(RenderLevelStageEvent e) {
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91073_ == null) {
            return;
        }
        CompoundTag run = ClientRoyal.data.m_128469_("Run");
        if (!run.m_128456_() && run.m_128451_("Phase") == 0 && run.m_128461_("Dimension").equals(mc.f_91073_.m_46472_().m_135782_().toString())) {
            if (run.m_128461_("Kind").equals("SKILL_COURSE")) {
                int stage = run.m_128451_("Stage");
                ListTag points = run.m_128437_("Points", 10);
                if (points.size() == 3) {
                    Vec3 at;
                    if (stage == 0) {
                        at = Vec3.m_82539_((Vec3i)BlockPos.m_122022_((long)points.m_128728_(0).m_128454_("Pos")));
                        RoyalWorldRenderer.ring(e, at, 1.5);
                        SoulWorldRenderer.label(e, ClientRoyal.t("skill.dash", new Object[0]), at.f_82479_, at.f_82480_ + 1.5, at.f_82481_, -1981295);
                    }
                    if (stage == 2) {
                        at = Vec3.m_82539_((Vec3i)BlockPos.m_122022_((long)points.m_128728_(1).m_128454_("Pos")));
                        SoulWorldRenderer.label(e, ClientRoyal.t("skill.parry", new Object[0]), at.f_82479_, at.f_82480_ + 2.0, at.f_82481_, -1981295);
                    }
                }
            }
            if (run.m_128461_("Kind").equals("MIMIC") && run.m_128441_("CastAt")) {
                Vec3 at = new Vec3(run.m_128459_("X"), run.m_128459_("Y"), run.m_128459_("Z"));
                RoyalWorldRenderer.ring(e, at, 3.0);
                SoulWorldRenderer.label(e, ClientRoyal.t("copy_warning", ClientEclipse.t("power." + run.m_128461_("Copy").toLowerCase(Locale.ROOT), new Object[0])), at.f_82479_, at.f_82480_ + 1.0, at.f_82481_, -21073);
            }
        }
        for (Tag tag : ClientRoyal.data.m_128437_("Banners", 10)) {
            BlockPos pos;
            CompoundTag banner = (CompoundTag)tag;
            if (!banner.m_128461_("Dimension").equals(mc.f_91073_.m_46472_().m_135782_().toString()) || mc.f_91074_.m_20238_(Vec3.m_82512_((Vec3i)(pos = BlockPos.m_122022_((long)banner.m_128454_("Pos"))))) > 2304.0 || !mc.f_91073_.m_46805_(pos) || !mc.f_91073_.m_8055_(pos).m_60713_((Block)RoyalContent.BANNER.get())) continue;
            CompoundTag company = ClientRoyal.data.m_128437_("Teams", 10).m_128728_(banner.m_128451_("Team"));
            String name = company.m_128461_("Name").isBlank() ? ClientRoyal.t("team_default", banner.m_128451_("Team") + 1) : ModLanguage.user(company.m_128461_("Name"));
            SoulWorldRenderer.label(e, name, (double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 1.8, (double)pos.m_123343_() + 0.5, -1981295);
        }
        for (LivingEntity wish : wishes) {
            if (!wish.m_6084_() || wish.m_9236_() != mc.f_91073_) continue;
            SoulWorldRenderer.label(e, "\u2605 " + wish.m_7755_().getString(), wish.m_20185_(), wish.m_20186_() + (double)wish.m_20206_() + 0.4, wish.m_20189_(), -10100);
        }
        mc.m_91269_().m_110104_().m_109911_();
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class Celebrations {
        @SubscribeEvent
        public static void royalOverlay(RegisterGuiOverlaysEvent e) {
            e.registerAboveAll("royal_awakening", (gui, g, partial, w, h) -> {
                Minecraft mc = Minecraft.m_91087_();
                if (mc.f_91074_ == null || mc.f_91080_ != null || mc.f_91066_.f_92062_ || ClientRoyal.celebrationUntil <= ClientWarfront.now()) {
                    return;
                }
                int width = Math.min(276, w - 24);
                int x = (w - width) / 2;
                RoyalTheme.frame(g, x, 52, width, 52, -283043794);
                RoyalTheme.icon(g, 7, x + 4, 56, 38);
                ModLanguage.draw(g, mc.f_91062_, ClientRoyal.t("awakening", new Object[0]), x + 47, 62, -1981295, false);
                ListTag memories = ClientRoyal.data.m_128437_("Memories", 10);
                if (!memories.isEmpty()) {
                    ModLanguage.draw(g, mc.f_91062_, ModLanguage.fit(mc.f_91062_, ModLanguage.user(memories.m_128728_(0).m_128461_("Name")), width - 55), x + 47, 80, -988710, false);
                }
            });
        }
    }
}

