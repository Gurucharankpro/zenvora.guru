/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.ascension;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.SoulRecord;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.network.AscensionNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalText;
import dev.shadowsovereign.soulforge.CompanionService;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class SoulService {
    public static long now(ServerPlayer p) {
        return p.f_8924_.m_129783_().m_46467_();
    }

    public static String mode(ServerPlayer p) {
        ArmyData.Army a = ArmyData.get(p.f_8924_).army(p.m_20148_());
        return !a.autoRecruit ? "Off" : (a.manualRecruit ? "Manual" : "Automatic");
    }

    public static void cycleMode(ServerPlayer p) {
        ArmyData.Army a = ArmyData.get(p.f_8924_).army(p.m_20148_());
        if (!a.autoRecruit) {
            a.autoRecruit = true;
            a.manualRecruit = true;
        } else if (a.manualRecruit) {
            a.manualRecruit = false;
        } else {
            a.autoRecruit = false;
        }
        ArmyData.get(p.f_8924_).m_77762_();
        ShadowService.tell(p, "Recruitment: " + SoulService.mode(p));
    }

    public static void offer(ServerPlayer p, Vec3 pos, ShadowRecord r) {
        ArmyData data = ArmyData.get(p.f_8924_);
        ArmyData.Army a = data.army(p.m_20148_());
        SoulService.expire(p);
        while (a.souls.size() >= 128) {
            a.souls.remove(a.souls.keySet().iterator().next());
        }
        CompanionService.awaken(p, r);
        SoulRecord soul = new SoulRecord(UUID.randomUUID(), r, p.m_9236_().m_46472_().m_135782_().toString(), SoulService.ground(p, pos), SoulService.now(p) + 6000L);
        soul.roll = p.m_217043_().m_188503_(100);
        soul.chance = RoyalService.chance(p);
        a.souls.put(soul.id, soul);
        data.m_77762_();
        AscensionNetwork.send(p, false);
    }

    private static Vec3 ground(ServerPlayer p, Vec3 at) {
        if (at.f_82480_ < (double)p.m_9236_().m_141937_()) {
            return p.m_20182_();
        }
        for (int down = 0; down <= 32; ++down) {
            BlockPos pos = BlockPos.m_274446_((Position)at).m_6625_(down);
            if (!p.m_284548_().m_46805_(pos)) break;
            if (p.m_9236_().m_8055_(pos).m_60812_((BlockGetter)p.m_9236_(), pos).m_83281_() && p.m_9236_().m_6425_(pos).m_76178_()) continue;
            return new Vec3(at.f_82479_, (double)pos.m_123342_() + 1.02, at.f_82481_);
        }
        return at;
    }

    public static boolean visible(ServerPlayer p, SoulRecord s) {
        return s.expires > SoulService.now(p) && s.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) && p.m_20238_(s.position) <= 2304.0 && p.m_284548_().m_46805_(BlockPos.m_274446_((Position)s.position));
    }

    public static SoulRecord aimed(ServerPlayer p) {
        Vec3 from = p.m_146892_();
        Vec3 end = from.m_82549_(p.m_20154_().m_82490_(6.0));
        return ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).souls.values().stream().filter(s -> SoulService.visible(p, s) && new AABB(s.position.m_82520_(-0.5, 0.0, -0.5), s.position.m_82520_(0.5, 1.5, 0.5)).m_82371_(from, end).isPresent()).filter(s -> {
            BlockHitResult hit = p.m_9236_().m_45547_(new ClipContext(from, s.position.m_82520_(0.0, 0.7, 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)p));
            return hit.m_6662_() == HitResult.Type.MISS;
        }).min(Comparator.comparingDouble(s -> p.m_20238_(s.position))).orElse(null);
    }

    public static boolean claim(ServerPlayer p) {
        if (!p.m_6084_() || p.m_5833_() || JourneyView.active(p)) {
            return false;
        }
        SoulService.expire(p);
        SoulRecord s = SoulService.aimed(p);
        if (s == null) {
            ShadowService.tell(p, "Aim at your purple soul within six blocks, then press your Claim Soul key.");
            return false;
        }
        ArmyData data = ArmyData.get(p.f_8924_);
        ArmyData.Army a = data.army(p.m_20148_());
        ResourceLocation key = ResourceLocation.m_135820_((String)s.companion.type);
        if (a.shadows.size() >= (Integer)ShadowConfig.MAX_ARMY.get() || key == null || !ForgeRegistries.ENTITY_TYPES.containsKey(key) || ((List)ShadowConfig.BLOCKLIST.get()).contains(s.companion.type)) {
            ShadowService.tell(p, "Cannot claim now: army full or this mob is unavailable. Your attempt is preserved.");
            return false;
        }
        a.souls.remove(s.id);
        boolean success = s.roll < s.chance;
        RoyalService.state(p).attempted(s.companion.type, success);
        if (success) {
            CompanionService.acquired(p, s.companion);
            a.shadows.put(s.companion.id, s.companion);
            RoyalService.remember(p, s.companion, s.bound == 100 ? s.chance : 33, BlockPos.m_274446_((Position)s.position));
            AdventureService.captured(p, s.companion);
            if (a.autoSummon && a.activeCount() < (long)((Integer)ShadowConfig.MAX_ACTIVE.get()).intValue()) {
                ShadowService.summon(p, s.companion, false);
            }
        }
        p.m_6330_(success ? SoundEvents.f_144243_ : SoundEvents.f_11937_, SoundSource.PLAYERS, 0.7f, success ? 1.3f : 0.8f);
        data.m_77762_();
        RoyalText.tell(p, success ? "soul_answered" : "soul_faded", s.companion.name);
        p.m_284548_().m_8624_(p, (ParticleOptions)(success ? ParticleTypes.f_123746_ : ParticleTypes.f_123762_), true, s.position.f_82479_, s.position.f_82480_ + 0.7, s.position.f_82481_, 24, 0.4, 0.6, 0.4, 0.04);
        ShadowNetwork.sendArmy(p, false);
        return success;
    }

    public static void expire(ServerPlayer p) {
        ArmyData d = ArmyData.get(p.f_8924_);
        if (d.army((UUID)p.m_20148_()).souls.values().removeIf(s -> s.expires <= SoulService.now(p))) {
            d.m_77762_();
        }
    }
}

