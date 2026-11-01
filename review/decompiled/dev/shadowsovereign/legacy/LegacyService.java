/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.EntityTeleportEvent$EnderEntity
 *  net.minecraftforge.eventbus.api.Event
 */
package dev.shadowsovereign.legacy;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.BattleService;
import dev.shadowsovereign.army.BattleState;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.legacy.LegacyState;
import dev.shadowsovereign.localization.ModMessages;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Event;

public final class LegacyService {
    private LegacyService() {
    }

    public static LegacyState state(ServerPlayer p) {
        return AdventureService.journal((ServerPlayer)p).legacy;
    }

    public static long now(ServerPlayer p) {
        return AdventureService.now(p.f_8924_);
    }

    public static void dirty(ServerPlayer p) {
        AdventureService.dirty(p);
    }

    public static void notice(ServerPlayer p, String text) {
        p.getPersistentData().m_128359_("SoulboundLegacyNotice", text);
        p.getPersistentData().m_128356_("SoulboundLegacyNoticeUntil", LegacyService.now(p) + 160L);
        ShadowService.tell(p, text);
    }

    public static String name(MinecraftServer server, UUID owner, ShadowRecord r) {
        AdventureData.Journal j = AdventureData.get((MinecraftServer)server).owners.get(owner);
        AdventureData.Lore lore = j == null ? null : j.lore.get(r.id);
        return lore == null || lore.nickname.isBlank() ? r.name : lore.nickname;
    }

    public static String name(ServerPlayer p, ShadowRecord r) {
        return LegacyService.name(p.f_8924_, p.m_20148_(), r);
    }

    public static boolean favorite(ServerPlayer p, ShadowRecord r) {
        AdventureData.Lore l = AdventureService.journal((ServerPlayer)p).lore.get(r.id);
        return l != null && l.favorite;
    }

    public static ShadowRecord record(ServerPlayer p, UUID id) {
        return id == null ? null : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
    }

    public static Mob eligible(ServerPlayer p, UUID id, int level, int range) {
        ShadowRecord r = LegacyService.record(p, id);
        Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
        return r != null && r.level() >= level && m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= (double)(range * range) ? m : null;
    }

    public static boolean available(ServerPlayer p, int completions) {
        return p.m_6084_() && !p.m_5833_() && AdventureService.journal((ServerPlayer)p).completions >= (long)completions;
    }

    public static boolean exchange(ServerPlayer p, UUID id) {
        LegacyState l = LegacyService.state(p);
        Mob m = LegacyService.eligible(p, id, 2, 24);
        long time = LegacyService.now(p);
        if (!LegacyService.available(p, 1) || m == null || time < l.exchangeAt || p.m_20159_() || p.m_20160_() || m.m_20159_() || m.m_20160_() || m.m_21523_()) {
            return false;
        }
        Vec3 a = p.m_20182_();
        Vec3 b = m.m_20182_();
        if (!LegacyService.safe((Entity)p, (Entity)m, b) || !LegacyService.safe((Entity)m, (Entity)p, a)) {
            return false;
        }
        EntityTeleportEvent.EnderEntity pe = new EntityTeleportEvent.EnderEntity((LivingEntity)p, b.f_82479_, b.f_82480_, b.f_82481_);
        EntityTeleportEvent.EnderEntity me = new EntityTeleportEvent.EnderEntity((LivingEntity)m, a.f_82479_, a.f_82480_, a.f_82481_);
        boolean veto = MinecraftForge.EVENT_BUS.post((Event)pe);
        if (!(!(veto |= MinecraftForge.EVENT_BUS.post((Event)me)) && pe.getTarget().equals((Object)b) && me.getTarget().equals((Object)a) && p.m_20182_().equals((Object)a) && m.m_20182_().equals((Object)b) && p.m_6084_() && m.m_6084_() && LegacyService.safe((Entity)p, (Entity)m, b) && LegacyService.safe((Entity)m, (Entity)p, a))) {
            return false;
        }
        p.m_6021_(b.f_82479_, b.f_82480_, b.f_82481_);
        m.m_6021_(a.f_82479_, a.f_82480_, a.f_82481_);
        p.m_20256_(Vec3.f_82478_);
        m.m_20256_(Vec3.f_82478_);
        m.f_19789_ = 0.0f;
        p.f_19789_ = 0.0f;
        ShadowRecord r = LegacyService.record(p, id);
        if (r.order == Order.STAY || r.order == Order.GUARD) {
            r.anchor = m.m_20183_();
        }
        ShadowService.updateSnapshot(m, r);
        l.resetWindow();
        l.exchangeAt = time + 600L;
        l.increment(LegacyState.Stat.EXCHANGES);
        LegacyService.achievements(p);
        LegacyService.dirty(p);
        ShadowService.soulParticles(m);
        p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123789_, p.m_20185_(), p.m_20186_() + 1.0, p.m_20189_(), 20, 0.4, 0.7, 0.4, 0.02);
        LegacyService.notice(p, "Shadow Exchange! Your companion's order is preserved.");
        return true;
    }

    public static boolean safe(Entity entity, Entity partner, Vec3 at) {
        Level level = entity.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel level2 = (ServerLevel)level;
        AABB box = entity.m_20191_().m_82383_(at.m_82546_(entity.m_20182_()));
        if (!level2.m_6857_().m_61935_(box) || box.f_82289_ < (double)level2.m_141937_() || box.f_82292_ > (double)level2.m_151558_()) {
            return false;
        }
        for (int cx = (int)Math.floor(box.f_82288_) >> 4; cx <= (int)Math.floor(box.f_82291_) >> 4; ++cx) {
            for (int cz = (int)Math.floor(box.f_82290_) >> 4; cz <= (int)Math.floor(box.f_82293_) >> 4; ++cz) {
                if (level2.m_46805_(new BlockPos(cx << 4, (int)box.f_82289_, cz << 4))) continue;
                return false;
            }
        }
        BlockPos feet = BlockPos.m_274446_((Position)at);
        if (!level2.m_8055_(feet.m_7495_()).m_60804_((BlockGetter)level2, feet.m_7495_()) || level2.m_8055_(feet.m_7495_()).m_60713_(Blocks.f_50450_) || level2.m_8055_(feet).m_60713_(Blocks.f_50083_) || level2.m_8055_(feet).m_60713_(Blocks.f_50084_) || level2.m_8055_(feet).m_60713_(Blocks.f_152499_)) {
            return false;
        }
        return !level2.m_46855_(box) && !level2.m_186434_(entity, box).iterator().hasNext() && level2.m_6249_(entity, box, e -> e != partner && e.m_6084_() && e.m_5829_()).isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean burst(ServerPlayer p, UUID id) {
        LegacyState l = LegacyService.state(p);
        Mob m = LegacyService.eligible(p, id, 5, 32);
        long time = LegacyService.now(p);
        if (!LegacyService.available(p, 5) || m == null || l.bursting || l.charge < 10 || time < l.burstAt) {
            return false;
        }
        List<LivingEntity> targets = p.m_284548_().m_6443_(LivingEntity.class, m.m_20191_().m_82400_(8.0), e -> e.m_20280_((Entity)m) <= 64.0 && ShadowService.canAttack(p, e) && m.m_142582_((Entity)e)).stream().sorted(Comparator.comparingDouble(arg_0 -> ((Mob)m).m_20280_(arg_0))).limit(8L).toList();
        if (targets.isEmpty()) {
            return false;
        }
        int hits = 0;
        l.bursting = true;
        try {
            for (LivingEntity target : targets) {
                if (!target.m_6469_(m.m_269291_().m_269104_((Entity)m, (Entity)m), 4.0f)) continue;
                ++hits;
            }
        }
        finally {
            l.bursting = false;
        }
        if (hits == 0) {
            return false;
        }
        l.charge -= 10;
        l.burstAt = time + 1200L;
        l.increment(LegacyState.Stat.BURSTS);
        LegacyService.achievements(p);
        LegacyService.dirty(p);
        for (int i = 0; i < 40; ++i) {
            double angle = (double)i * Math.PI / 20.0;
            p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123745_, m.m_20185_() + Math.cos(angle) * 4.0, m.m_20186_() + 0.8, m.m_20189_() + Math.sin(angle) * 4.0, 1, 0.0, 0.15, 0.0, 0.03);
        }
        LegacyService.notice(p, "Eclipse Burst struck " + hits + " enemies!");
        return true;
    }

    public static boolean link(ServerPlayer p, UUID first, UUID second) {
        ShadowRecord a = LegacyService.record(p, first);
        ShadowRecord b = LegacyService.record(p, second);
        if (!LegacyService.available(p, 3) || a == null || b == null || first.equals(second) || a.level() < 3 || b.level() < 3) {
            return false;
        }
        LegacyState l = LegacyService.state(p);
        l.first = first;
        l.second = second;
        l.resetWindow();
        LegacyService.dirty(p);
        LegacyService.notice(p, "Duo linked: " + LegacyService.name(p, a) + " + " + LegacyService.name(p, b) + ".");
        return true;
    }

    public static boolean unlink(ServerPlayer p) {
        LegacyState l = LegacyService.state(p);
        if (l.first == null) {
            return false;
        }
        l.second = null;
        l.first = null;
        l.resetWindow();
        LegacyService.dirty(p);
        return true;
    }

    public static void released(ServerPlayer p, UUID id) {
        LegacyState l = LegacyService.state(p);
        if (id.equals(l.first) || id.equals(l.second)) {
            LegacyService.unlink(p);
        }
    }

    public static void hit(LivingEntity victim, DamageSource source, float amount) {
        ServerPlayer p;
        if (amount <= 0.0f || victim.m_9236_().f_46443_ || ShadowService.isShadow((Entity)victim)) {
            return;
        }
        Entity attacker = CompanionProgression.source(source);
        ShadowRecord r = CompanionProgression.attacker(source);
        ServerPlayer serverPlayer = p = attacker == null ? null : ShadowService.owner(attacker);
        if (p == null || r == null || !ShadowService.canAttack(p, victim)) {
            return;
        }
        LegacyState l = LegacyService.state(p);
        if (l.bursting || l.first == null || l.second == null || LegacyService.now(p) < l.duoAt || !r.id.equals(l.first) && !r.id.equals(l.second)) {
            return;
        }
        Mob a = LegacyService.eligible(p, l.first, 3, 32);
        Mob b = LegacyService.eligible(p, l.second, 3, 32);
        if (a == null || b == null || a.m_20280_((Entity)b) > 256.0) {
            return;
        }
        long time = LegacyService.now(p);
        if (victim.m_20148_().equals(l.hitTarget) && l.hitMember != null && !l.hitMember.equals(r.id) && time - l.hitAt <= 60L && time >= l.hitAt) {
            victim.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 40, 0));
            l.duoAt = time + 200L;
            l.increment(LegacyState.Stat.DUOS);
            l.resetWindow();
            LegacyService.achievements(p);
            LegacyService.dirty(p);
            p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123809_, victim.m_20185_(), victim.m_20186_() + 1.0, victim.m_20189_(), 14, 0.4, 0.4, 0.4, 0.1);
        } else {
            l.hitTarget = victim.m_20148_();
            l.hitMember = r.id;
            l.hitAt = time;
        }
    }

    public static void killed(LivingEntity victim, DamageSource source) {
        if (victim.m_9236_().f_46443_ || !(victim instanceof Mob) || ShadowService.isShadow((Entity)victim) || victim.getPersistentData().m_128471_("SoulboundLegacyCounted")) {
            return;
        }
        Entity attacker = CompanionProgression.source(source);
        ShadowRecord r = CompanionProgression.attacker(source);
        if (attacker == null || r == null) {
            return;
        }
        victim.getPersistentData().m_128379_("SoulboundLegacyCounted", true);
        AdventureData data = AdventureData.get(victim.m_20194_());
        AdventureData.Journal j = data.journal(ShadowService.ownerId(attacker));
        LegacyState l = j.legacy;
        l.increment(LegacyState.Stat.KILLS);
        if (victim instanceof Enemy && !AdventureWorld.isNpc((Entity)victim) && !l.bursting && j.completions >= 5L) {
            l.charge = Math.min(10, l.charge + 1);
        }
        data.m_77762_();
        ServerPlayer p = ShadowService.owner(attacker);
        if (p != null && l.stat(LegacyState.Stat.KILLS) % 5L == 0L) {
            LegacyService.chatter(p, r, "kill");
        }
    }

    public static boolean nickname(ServerPlayer p, UUID id, String text) {
        ShadowRecord r = LegacyService.record(p, id);
        if (r == null || text.codePointCount(0, text.length()) > 24) {
            return false;
        }
        AdventureData.Lore l = AdventureService.journal(p).lore(id);
        l.nickname = LegacyState.plain(text, 24);
        Mob m = ShadowService.active(p.f_8924_, r);
        if (m != null) {
            ShadowService.updateName(m, r);
        }
        LegacyService.dirty(p);
        return true;
    }

    public static boolean setFavorite(ServerPlayer p, UUID id, boolean value) {
        if (LegacyService.record(p, id) == null) {
            return false;
        }
        AdventureService.journal((ServerPlayer)p).lore((UUID)id).favorite = value;
        LegacyService.dirty(p);
        return true;
    }

    public static boolean chatterToggle(ServerPlayer p, boolean enabled) {
        LegacyService.state((ServerPlayer)p).chatter = enabled;
        LegacyService.dirty(p);
        return true;
    }

    public static void chatter(ServerPlayer p, ShadowRecord r, String context) {
        LegacyState l = LegacyService.state(p);
        if (!l.chatter || LegacyService.now(p) < l.nextChatter || LegacyService.eligible(p, r.id, 1, 32) == null) {
            return;
        }
        l.nextChatter = LegacyService.now(p) + 1200L;
        LegacyService.dirty(p);
        LegacyState.Personality personality = LegacyState.Personality.of(r.id);
        String line = switch (personality) {
            default -> throw new IncompatibleClassChangeError();
            case LegacyState.Personality.BRAVE -> {
                if (context.equals("kill")) {
                    yield "Another shadow falls. I stand with you.";
                }
                yield "Your vanguard has returned.";
            }
            case LegacyState.Personality.CURIOUS -> {
                if (context.equals("kill")) {
                    yield "What secrets will the next path hold?";
                }
                yield "Where are we exploring next?";
            }
            case LegacyState.Personality.QUIET -> context.equals("kill") ? "Together." : "At your side.";
        };
        p.m_5661_((Component)ModMessages.of(LegacyService.name(p, r) + ": " + line).m_130940_(ChatFormatting.AQUA), true);
    }

    public static boolean emote(ServerPlayer p, int kind, BattleRules.Squad scope, UUID selected) {
        if (!p.m_6084_() || p.m_5833_() || kind < 0 || kind > 2 || LegacyService.now(p) < LegacyService.state((ServerPlayer)p).emoteAt || selected != null && LegacyService.record(p, selected) == null) {
            return false;
        }
        List<Mob> mobs = BattleService.records(p, scope, selected).stream().map(r -> LegacyService.eligible(p, r.id, 1, 16)).filter(Objects::nonNull).sorted(Comparator.comparingDouble(arg_0 -> ((ServerPlayer)p).m_20280_(arg_0))).limit(8L).toList();
        if (mobs.isEmpty()) {
            return false;
        }
        for (Mob m : mobs) {
            for (int i = 0; i < 24; ++i) {
                double angle = (double)i * Math.PI / 12.0;
                double radius = Math.max(0.4, Math.min(1.2, (double)m.m_20205_() * 0.6));
                double y = m.m_20186_() + (double)m.m_20206_() + 0.4;
                if (kind == 0) {
                    p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123810_, m.m_20185_() + Math.cos(angle) * radius, y + (i % 4 == 0 ? 0.3 : 0.0), m.m_20189_() + Math.sin(angle) * radius, 1, 0.0, 0.0, 0.0, 0.0);
                    continue;
                }
                if (kind == 1) {
                    if (i >= 6) continue;
                    p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123750_, m.m_20185_(), y, m.m_20189_(), 1, 0.4, 0.2, 0.4, 0.01);
                    continue;
                }
                p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123745_, m.m_20185_() + Math.cos(angle) * radius, y - 1.0 + (double)i * 0.04, m.m_20189_() + Math.sin(angle) * radius, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
        LegacyService.state((ServerPlayer)p).emoteAt = LegacyService.now(p) + 100L;
        LegacyService.dirty(p);
        return true;
    }

    public static boolean saveLoadout(ServerPlayer p, int slot, String name) {
        if (slot < 0 || slot > 2 || name.codePointCount(0, name.length()) > 24) {
            return false;
        }
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        CompoundTag n = new CompoundTag();
        n.m_128359_("Name", (String)(name.isBlank() ? "Loadout " + (slot + 1) : LegacyState.plain(name, 24)));
        n.m_128359_("Scope", army.selectedSquad.name());
        ListTag entries = new ListTag();
        for (ShadowRecord r : army.shadows.values()) {
            CompoundTag e = new CompoundTag();
            e.m_128362_("Id", r.id);
            e.m_128359_("Squad", r.battle.squad.name());
            e.m_128359_("Formation", r.battle.formation.name());
            e.m_128359_("Priority", r.battle.priority.name());
            e.m_128405_("Retreat", r.battle.retreat);
            entries.add((Object)e);
        }
        n.m_128365_("Entries", (Tag)entries);
        LegacyService.state((ServerPlayer)p).loadouts[slot] = n;
        LegacyService.dirty(p);
        return true;
    }

    public static boolean applyLoadout(ServerPlayer p, int slot) {
        if (slot < 0 || slot > 2) {
            return false;
        }
        CompoundTag profile = LegacyService.state((ServerPlayer)p).loadouts[slot];
        if (profile == null) {
            return false;
        }
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        for (Tag tag : profile.m_128437_("Entries", 10)) {
            ShadowRecord r;
            CompoundTag n = (CompoundTag)tag;
            if (!n.m_128403_("Id") || (r = LegacyService.record(p, n.m_128342_("Id"))) == null) continue;
            BattleState b = r.battle;
            b.squad = AdventureData.read(BattleRules.Squad.class, n.m_128461_("Squad"), BattleRules.Squad.UNASSIGNED);
            if (b.squad == BattleRules.Squad.ALL) {
                b.squad = BattleRules.Squad.UNASSIGNED;
            }
            b.formation = AdventureData.read(BattleRules.Formation.class, n.m_128461_("Formation"), BattleRules.Formation.LOOSE);
            b.priority = AdventureData.read(BattleRules.Priority.class, n.m_128461_("Priority"), BattleRules.Priority.DEFAULT);
            int retreat = n.m_128451_("Retreat");
            if (retreat != 0 && retreat != 10 && retreat != 25 && retreat != 50) continue;
            b.retreat = retreat;
        }
        army.selectedSquad = AdventureData.read(BattleRules.Squad.class, profile.m_128461_("Scope"), BattleRules.Squad.ALL);
        ArmyData.get(p.f_8924_).m_77762_();
        LegacyService.notice(p, "Tactical loadout applied. Orders, patrols and cooldowns stay intact.");
        return true;
    }

    public static void achievements(ServerPlayer p) {
        AdventureData.Journal j = AdventureService.journal(p);
        LegacyState l = j.legacy;
        for (LegacyState.Achievement a : LegacyState.Achievement.values()) {
            long value;
            long l2 = value = a.stat == null ? (long)l.bestWave : l.stat(a.stat);
            if (value < (long)a.goal || !l.achievements.add(a)) continue;
            j.titles.add(a.title);
            LegacyService.notice(p, "Chronicle achievement: " + a.label + "! A new title is unlocked.");
            LegacyService.dirty(p);
        }
    }
}

