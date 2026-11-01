/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerBossEvent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.BossEvent$BossBarOverlay
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.Level
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.expansion.OpenMythics;
import dev.shadowsovereign.mythic.MythicContent;
import dev.shadowsovereign.mythic.MythicEvents;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicLair;
import dev.shadowsovereign.mythic.MythicSites;
import dev.shadowsovereign.mythic.MythicSpawn;
import dev.shadowsovereign.mythic.MythicState;
import dev.shadowsovereign.mythic.SoulDevourer;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.realm.WildBosses;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public final class MythicService {
    public static final String HOST = "SoulboundMythicHost";

    public static MythicState state(ServerPlayer p) {
        return AdventureService.journal((ServerPlayer)p).mythic;
    }

    public static AdventureData.Session session(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && s.kind == AdventureData.Kind.MYTHIC_LAIR ? s : null;
    }

    public static ServerPlayer host(Entity e) {
        return e.m_20194_() != null && e.getPersistentData().m_128403_(HOST) ? e.m_20194_().m_6846_().m_11259_(e.getPersistentData().m_128342_(HOST)) : null;
    }

    public static SoulDevourer body(ServerPlayer p) {
        SoulDevourer m;
        Entity entity;
        ServerLevel w = p.f_8924_.m_129880_(OdysseyRealm.KEY);
        MythicState s = MythicService.state(p);
        return w != null && s.active != null && (entity = w.m_8791_(s.active)) instanceof SoulDevourer ? (m = (SoulDevourer)entity) : null;
    }

    public static boolean fighting(ServerPlayer p, ShadowRecord r) {
        AdventureData.Session s = MythicService.session(p);
        return s != null && s.phase == AdventureData.Phase.ACTIVE && s.participants.contains(r.id) && !VigilCrown.active(p);
    }

    public static Component text(String key, Object ... args) {
        return Component.m_237110_((String)("mythic.shadowsovereign." + key), (Object[])args);
    }

    private static void tell(ServerPlayer p, String k) {
        p.m_5661_(MythicService.text(k, new Object[0]), false);
    }

    public static boolean near(ServerPlayer p, int distance) {
        return OdysseyRealm.inside(p) && p.m_20183_().m_123331_((Vec3i)MythicLair.center(p)) <= (double)(distance * distance);
    }

    public static boolean start(ServerPlayer serverPlayer) {
        return OpenMythics.startDevourer(serverPlayer);
    }

    public static boolean spawn(ServerPlayer p) {
        MythicState st = MythicService.state(p);
        BlockPos at = MythicLair.center(p).m_7494_();
        ServerLevel w = p.m_284548_();
        if (!w.m_143340_(at) || !w.m_46805_(at)) {
            return false;
        }
        if (MythicService.body(p) != null) {
            return true;
        }
        if (st.active != null) {
            return false;
        }
        SoulDevourer m = (SoulDevourer)((EntityType)MythicContent.DEVOURER.get()).m_20615_((Level)w);
        if (m == null) {
            return false;
        }
        if (!st.snapshot.m_128456_()) {
            m.m_20258_(st.snapshot.m_6426_());
        }
        m.m_20084_(UUID.randomUUID());
        boolean fits = MythicSpawn.place(w, (Mob)m, at, MythicSites.bounds(st.gloom, at));
        if (!fits) {
            m.m_146870_();
            return false;
        }
        m.getPersistentData().m_128362_(HOST, p.m_20148_());
        m.getPersistentData().m_128379_("SoulboundWildBoss", true);
        m.getPersistentData().m_128347_("SoulboundBossAttack", 12.0);
        m.m_6593_(MythicService.text("devourer", new Object[0]));
        m.m_20340_(true);
        m.m_21530_();
        st.active = m.m_20148_();
        if (!w.m_7967_((Entity)m) || w.m_8791_(m.m_20148_()) != m) {
            m.m_146870_();
            st.active = null;
            return false;
        }
        if (st.bar != null) {
            st.bar.m_7706_();
        }
        st.bar = new ServerBossEvent(MythicService.text("devourer", new Object[0]), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
        st.bar.m_6543_(p);
        st.bar.m_142711_(m.m_21223_() / m.m_21233_());
        m.animate(st.snapshot.m_128456_() ? 1 : 2);
        LivingRealm.dirty(p);
        return true;
    }

    public static void pause(MinecraftServer minecraftServer, UUID uUID) {
        OpenMythics.pause(minecraftServer, uUID);
    }

    public static boolean resume(ServerPlayer p) {
        if (MythicExpedition.current(MythicService.session(p)) >= 0) {
            return MythicExpedition.resume(p);
        }
        AdventureData.Session s = MythicService.session(p);
        if (!p.m_6084_() || p.m_5833_() || s == null || s.phase != AdventureData.Phase.PAUSED || !MythicService.near(p, 25) || MythicService.state((ServerPlayer)p).status == 2 || VigilCrown.active(p) || p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            return false;
        }
        s.phase = AdventureData.Phase.ACTIVE;
        if (!MythicService.spawn(p)) {
            s.phase = AdventureData.Phase.PAUSED;
            return false;
        }
        LivingRealm.dirty(p);
        return true;
    }

    public static boolean cancel(ServerPlayer serverPlayer) {
        return OpenMythics.cancel(serverPlayer);
    }

    public static boolean defeated(SoulDevourer m, UUID killer) {
        ServerPlayer p;
        boolean captured;
        if (!m.getPersistentData().m_128403_(HOST)) {
            return false;
        }
        UUID owner = m.getPersistentData().m_128342_(HOST);
        AdventureData d = AdventureData.get(m.m_20194_());
        AdventureData.Journal j = d.journal(owner);
        MythicState st = j.mythic;
        if (!owner.equals(killer) || !m.m_20148_().equals(st.active) || st.status == 2) {
            return false;
        }
        if (j.session != null && j.session.kind == AdventureData.Kind.MYTHIC_LAIR) {
            j.session = null;
        }
        boolean bl = captured = (p = m.m_20194_().m_6846_().m_11259_(owner)) == null ? WildBosses.captureOffline(m.m_20194_(), owner, (Mob)m) : WildBosses.capture(p, (Mob)m);
        if (!captured) {
            return false;
        }
        MythicEvents.clearFangs((ServerLevel)m.m_9236_(), m.m_20148_());
        if (st.bar != null) {
            st.bar.m_7706_();
            st.bar = null;
        }
        st.status = 2;
        st.soul = m.m_20148_();
        st.active = null;
        st.snapshot = new CompoundTag();
        d.m_77762_();
        if (p != null) {
            MythicService.tell(p, "obtained");
            MythicNetwork.send(p, false);
        }
        return true;
    }

    public static void tick(ServerPlayer serverPlayer) {
        OpenMythics.tick(serverPlayer);
    }
}

