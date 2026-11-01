/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.Path
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.journey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.journey.JourneyService;
import dev.shadowsovereign.journey.JourneyState;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.legacy.LegacyState;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public final class JourneyActivities {
    public static BlockPos water(ServerPlayer p) {
        for (BlockPos at : BlockPos.m_121940_((BlockPos)p.m_20183_().m_7918_(-8, -3, -8), (BlockPos)p.m_20183_().m_7918_(8, 3, 8))) {
            if (!p.m_284548_().m_46805_(at) || !(p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 64.0) || !(at.m_123331_((Vec3i)AdventureService.journal((ServerPlayer)p).sanctuary) <= 256.0) || !p.m_9236_().m_6425_(at).m_205070_(FluidTags.f_13131_)) continue;
            return at.m_7949_();
        }
        return null;
    }

    public static BlockPos campfire(ServerPlayer p) {
        BlockPos hub = AdventureService.journal((ServerPlayer)p).sanctuary;
        for (BlockPos at : BlockPos.m_121940_((BlockPos)hub.m_7918_(-16, -3, -16), (BlockPos)hub.m_7918_(16, 3, 16))) {
            if (!(at.m_123331_((Vec3i)hub) <= 256.0) || !p.m_284548_().m_46805_(at) || !(p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 256.0) || !(p.m_9236_().m_8055_(at).m_60734_() instanceof CampfireBlock) || !((Boolean)p.m_9236_().m_8055_(at).m_61143_((Property)CampfireBlock.f_51227_)).booleanValue()) continue;
            return at.m_7949_();
        }
        return null;
    }

    public static boolean route(ServerPlayer p, AdventureData.Session s, Mob m) {
        ListTag list = new ListTag();
        for (int i = 0; i < 6; ++i) {
            Vec3 found = null;
            for (int attempt = 0; attempt < 32; ++attempt) {
                Path path;
                Vec3 at = AdventureWorld.spot(p.m_284548_(), m.m_6095_(), s.anchor, 8 + attempt % 5, 14.0, p.m_20148_().hashCode() + i * 137 + attempt * 71);
                if (at == null) continue;
                boolean duplicate = false;
                for (Tag t : list) {
                    if (!(BlockPos.m_122022_((long)((CompoundTag)t).m_128454_("Pos")).m_123331_((Vec3i)BlockPos.m_274446_((Position)at)) < 25.0)) continue;
                    duplicate = true;
                }
                if (duplicate || (path = m.m_21573_().m_7864_(BlockPos.m_274446_((Position)at), 0)) == null || !path.m_77403_()) continue;
                found = at;
                break;
            }
            if (found == null) {
                JourneyService.tell(p, "This companion needs a clear, navigable rally course around the Sanctuary.");
                return false;
            }
            CompoundTag n = new CompoundTag();
            n.m_128356_("Pos", BlockPos.m_274446_(found).m_121878_());
            list.add((Object)n);
        }
        s.journey.m_128365_("Checkpoints", (Tag)list);
        return true;
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        switch (s.kind) {
            case RALLY: {
                JourneyActivities.rally(p, s);
                break;
            }
            case ANGLING: {
                JourneyActivities.angling(p, s);
                break;
            }
            case CAMP: {
                JourneyActivities.camp(p, s);
                break;
            }
        }
    }

    private static void rally(ServerPlayer p, AdventureData.Session s) {
        CompoundTag n = s.journey;
        UUID id = n.m_128342_("Ally");
        Mob m = LegacyService.eligible(p, id, 1, 32);
        if (m == null || LegacyService.record((ServerPlayer)p, (UUID)id).order != Order.FOLLOW) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            JourneyService.tell(p, "Rally paused. Summon your selected companion and set Follow to resume.");
            return;
        }
        if (n.m_128471_("Hard") && JourneyService.now(p) > n.m_128454_("Deadline")) {
            JourneyService.fail(p, "Rally time expired. Try again or choose Relaxed.");
            return;
        }
        ListTag points = n.m_128437_("Checkpoints", 10);
        if (s.wave >= points.size()) {
            AdventureWorld.ready(p, s);
            return;
        }
        Vec3 at = Vec3.m_82512_((Vec3i)BlockPos.m_122022_((long)points.m_128728_(s.wave).m_128454_("Pos")));
        for (int i = 0; i < 16; ++i) {
            double a = (double)i * Math.PI / 8.0;
            p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123810_, true, at.f_82479_ + Math.cos(a) * 3.0, at.f_82480_, at.f_82481_ + Math.sin(a) * 3.0, 1, 0.0, 0.0, 0.0, 0.0);
        }
        if (p.m_20238_(at) <= 9.0 && m.m_20238_(at) <= 9.0) {
            ++s.wave;
            JourneyService.tell(p, "Rally checkpoint " + s.wave + " / 6");
            if (s.wave == 6) {
                AdventureWorld.ready(p, s);
            }
        }
    }

    public static boolean cast(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || s.kind != AdventureData.Kind.ANGLING || s.phase != AdventureData.Phase.ACTIVE || s.journey.m_128471_("Cast") || !JourneyActivities.nearWater(p, s)) {
            return false;
        }
        s.journey.m_128379_("Cast", true);
        s.journey.m_128405_("Bites", 0);
        JourneyActivities.nextBite(p, s);
        JourneyService.dirty(p);
        return true;
    }

    private static boolean nearWater(ServerPlayer p, AdventureData.Session s) {
        BlockPos at = BlockPos.m_122022_((long)s.journey.m_128454_("Water"));
        return p.m_284548_().m_46805_(at) && p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 64.0 && p.m_9236_().m_6425_(at).m_205070_(FluidTags.f_13131_);
    }

    private static void nextBite(ServerPlayer p, AdventureData.Session s) {
        long at = JourneyService.now(p) + 40L + (long)p.m_217043_().m_188503_(61);
        s.journey.m_128356_("BiteAt", at);
        s.journey.m_128356_("BiteEnd", at + (long)(s.journey.m_128471_("Hard") ? 20 : 40));
        s.journey.m_128379_("Bite", false);
    }

    public static boolean reel(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || s.kind != AdventureData.Kind.ANGLING || s.phase != AdventureData.Phase.ACTIVE || !s.journey.m_128471_("Cast") || !JourneyActivities.nearWater(p, s)) {
            return false;
        }
        CompoundTag n = s.journey;
        long time = JourneyService.now(p);
        if (time < n.m_128454_("BiteAt") || time > n.m_128454_("BiteEnd")) {
            n.m_128379_("Cast", false);
            n.m_128379_("Bite", false);
            JourneyService.tell(p, "The spirit slipped away. Cast again.");
            JourneyService.dirty(p);
            return false;
        }
        int bites = n.m_128451_("Bites") + 1;
        n.m_128405_("Bites", bites);
        if (bites < 3) {
            JourneyActivities.nextBite(p, s);
        } else {
            JourneyState st = JourneyService.state(p);
            int index = Math.floorMod(st.data.m_128454_("Catches") + (long)p.m_20148_().hashCode(), 6);
            st.increment("Catches");
            if (n.m_128471_("Hard")) {
                JourneyService.unlock(p, AdventureData.Title.MOONLIT_ANGLER);
            }
            st.data.m_128405_("Fish", st.data.m_128451_("Fish") | 1 << index);
            n.m_128379_("Cast", false);
            n.m_128379_("Bite", false);
            n.m_128359_("Catch", JourneyService.FISH[index]);
            JourneyService.tell(p, "Caught a " + JourneyService.FISH[index] + "!");
            if ((st.data.m_128451_("Fish") & 0x3F) == 63) {
                JourneyService.unlock(p, AdventureData.Title.SPIRIT_ANGLER);
            }
            long day = time / 24000L;
            if (!st.data.m_128441_("CatchDay") || st.data.m_128454_("CatchDay") != day) {
                st.data.m_128356_("CatchDay", day);
                st.data.m_128405_("DailyCatches", 0);
            }
            st.data.m_128405_("DailyCatches", st.data.m_128451_("DailyCatches") + 1);
            if (st.data.m_128451_("DailyCatches") >= 3 && st.daily("FishingRewardDay", day)) {
                AdventureService.reward(p, AdventureService.journal(p), UUID.randomUUID(), "Three spirit catches: daily adventure reward");
            }
        }
        JourneyService.dirty(p);
        return true;
    }

    private static void angling(ServerPlayer p, AdventureData.Session s) {
        if (!JourneyActivities.nearWater(p, s)) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            return;
        }
        CompoundTag n = s.journey;
        if (!n.m_128471_("Cast")) {
            return;
        }
        long time = JourneyService.now(p);
        if (time > n.m_128454_("BiteEnd")) {
            n.m_128379_("Cast", false);
            n.m_128379_("Bite", false);
            JourneyService.tell(p, "Missed the bite. Cast again.");
        } else if (time >= n.m_128454_("BiteAt")) {
            n.m_128379_("Bite", true);
            Vec3 at = Vec3.m_82512_((Vec3i)BlockPos.m_122022_((long)n.m_128454_("Water")));
            p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123746_, true, at.f_82479_, at.f_82480_ + 0.5, at.f_82481_, 12, 0.3, 0.3, 0.3, 0.03);
        }
    }

    public static String story(int index, LegacyState.Personality personality) {
        String[] scenes = new String[]{"I remember the first voice that called me back.", "The road seems shorter when our shadows travel together.", "That last battle was close. We found our way home.", "Strange faces, one army. There is room by this fire.", "Listen. For a moment, the world is quiet.", "Tomorrow has no map yet. We can draw one."};
        String string = scenes[Math.floorMod(index, 6)];
        return string + (switch (personality) {
            default -> throw new IncompatibleClassChangeError();
            case LegacyState.Personality.BRAVE -> " I will take the first watch.";
            case LegacyState.Personality.CURIOUS -> " What will we discover next?";
            case LegacyState.Personality.QUIET -> " I will be here.";
        });
    }

    private static void camp(ServerPlayer p, AdventureData.Session s) {
        CompoundTag n = s.journey;
        BlockPos fire = BlockPos.m_122022_((long)n.m_128454_("Fire"));
        if (!p.m_284548_().m_46805_(fire)) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            return;
        }
        BlockState block = p.m_9236_().m_8055_(fire);
        if (!(block.m_60734_() instanceof CampfireBlock) || !((Boolean)block.m_61143_((Property)CampfireBlock.f_51227_)).booleanValue()) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            return;
        }
        if (!n.m_128471_("Spoken")) {
            for (UUID id : s.participants) {
                Mob m = LegacyService.eligible(p, id, 1, 16);
                if (m == null || m.m_5448_() != null) continue;
                n.m_128359_("StoryText", LegacyService.name(p, LegacyService.record(p, id)) + ": " + JourneyActivities.story(n.m_128451_("Choice"), LegacyState.Personality.of(id)));
                JourneyService.tell(p, n.m_128461_("StoryText"));
                break;
            }
            n.m_128379_("Spoken", true);
        }
        if (JourneyService.now(p) >= n.m_128454_("Deadline")) {
            AdventureWorld.ready(p, s);
        }
    }

    public static boolean camping(Mob m) {
        ServerPlayer p = ShadowService.owner((Entity)m);
        ShadowRecord r = ShadowService.record((Entity)m);
        if (p == null || r == null || m.m_5448_() != null) {
            return false;
        }
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && s.kind == AdventureData.Kind.CAMP && s.phase == AdventureData.Phase.ACTIVE && s.participants.contains(r.id) && m.m_20280_((Entity)p) <= 256.0;
    }

    public static boolean campMove(Mob m) {
        ServerPlayer p = ShadowService.owner((Entity)m);
        ShadowRecord r = ShadowService.record((Entity)m);
        if (p == null || r == null || m.m_5448_() != null) {
            return false;
        }
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || s.kind != AdventureData.Kind.CAMP || s.phase != AdventureData.Phase.ACTIVE || !s.participants.contains(r.id) || m.m_20280_((Entity)p) > 256.0) {
            return false;
        }
        BlockPos fire = BlockPos.m_122022_((long)s.journey.m_128454_("Fire"));
        Vec3 at = AdventureWorld.spot(p.m_284548_(), m.m_6095_(), fire, 3.0, 5.0, r.id.hashCode());
        if (at != null && m.f_19797_ % 20 == 0) {
            m.m_21573_().m_26519_(at.f_82479_, at.f_82480_, at.f_82481_, 0.8);
        }
        return true;
    }
}

