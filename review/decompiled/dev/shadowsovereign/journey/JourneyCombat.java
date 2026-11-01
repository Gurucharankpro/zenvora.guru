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
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerBossEvent
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.BossEvent$BossBarOverlay
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.journey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.journey.JourneyService;
import dev.shadowsovereign.legacy.LegacyService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class JourneyCombat {
    private static final Map<UUID, ServerBossEvent> bars = new HashMap<UUID, ServerBossEvent>();

    public static void removeBar(UUID id) {
        ServerBossEvent bar = bars.remove(id);
        if (bar != null) {
            bar.m_7706_();
        }
    }

    public static void clearBars() {
        bars.values().forEach(ServerBossEvent::m_7706_);
        bars.clear();
    }

    private static AdventureData.Npc add(ServerPlayer p, AdventureData.Session s, EntityType<? extends Mob> type) {
        AdventureData.Npc n = AdventureWorld.template(p, s, type, AdventureData.Role.ENEMY);
        if (!s.journey.m_128471_("Hard")) {
            Mob m = (Mob)type.m_20615_((Level)p.m_284548_());
            m.m_20258_(n.snapshot);
            m.m_21051_(Attributes.f_22276_).m_22100_((double)m.m_21233_() * 0.75);
            m.m_21153_(m.m_21233_());
            n.snapshot = AdventureWorld.snapshot(m);
        }
        s.npcs.put(n.id, n);
        return n;
    }

    public static void boss(ServerPlayer p, AdventureData.Session s, int choice) {
        EntityType type = choice == 0 ? EntityType.f_20460_ : (choice == 1 ? EntityType.f_20481_ : EntityType.f_20518_);
        AdventureData.Npc n = JourneyCombat.add(p, s, (EntityType<? extends Mob>)type);
        Mob m = (Mob)type.m_20615_((Level)p.m_284548_());
        m.m_20258_(n.snapshot);
        double hp = (double)(choice == 2 ? 160 : 100) * (s.journey.m_128471_("Hard") ? 1.0 : 0.75);
        m.m_21051_(Attributes.f_22276_).m_22100_(hp);
        m.m_21153_((float)hp);
        m.m_6593_((Component)Component.m_237113_((String)JourneyService.BOSSES[choice]));
        n.snapshot = AdventureWorld.snapshot(m);
        s.journey.m_128362_("BossNpc", n.id);
        s.journey.m_128405_("BossChoice", choice);
        s.journey.m_128356_("SpecialAt", JourneyService.now(p) + 240L);
        s.journey.m_128473_("StrikeAt");
        s.journey.m_128473_("BossDead");
        s.journey.m_128473_("Enraged");
    }

    public static boolean route(ServerPlayer p, int value) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || s.kind != AdventureData.Kind.RIFT || s.phase != AdventureData.Phase.ACTIVE || !AdventureService.nearHub(p) || !s.journey.m_128471_("ChooseRoute") || s.journey.m_128471_("ChooseBoon") || value < 0 || value > 1) {
            return false;
        }
        CompoundTag n = s.journey;
        n.m_128379_("ChooseRoute", false);
        if (value == 1) {
            n.m_128405_("Risk", n.m_128451_("Risk") + 1);
        }
        n.m_128405_("Route" + s.wave, value);
        if (s.wave == 2) {
            JourneyCombat.boss(p, s, value == 0 ? 0 : 2);
        } else {
            for (int i = 0; i < (value == 0 ? 4 : 6); ++i) {
                JourneyCombat.add(p, s, (EntityType<? extends Mob>)(value == 0 ? EntityType.f_20501_ : (i < 3 ? EntityType.f_20524_ : EntityType.f_20479_)));
            }
        }
        s.initialized = true;
        JourneyService.dirty(p);
        return true;
    }

    public static boolean boon(ServerPlayer p, int value) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || s.kind != AdventureData.Kind.RIFT || s.phase != AdventureData.Phase.ACTIVE || !AdventureService.nearHub(p) || !s.journey.m_128471_("ChooseBoon") || value < 0 || value > 1) {
            return false;
        }
        CompoundTag wards = new CompoundTag();
        for (UUID id : s.participants) {
            Mob m = LegacyService.eligible(p, id, 1, 32);
            if (m == null) continue;
            if (value == 0) {
                m.m_5634_(2.0f);
                continue;
            }
            wards.m_128350_(id.toString(), 2.0f);
        }
        s.journey.m_128365_("Wards", (Tag)wards);
        s.journey.m_128379_("ChooseBoon", false);
        JourneyService.dirty(p);
        return true;
    }

    public static float absorb(Mob m, float amount) {
        float spent;
        ServerPlayer p = ShadowService.owner((Entity)m);
        ShadowRecord r = ShadowService.record((Entity)m);
        if (p == null || r == null) {
            return amount;
        }
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || s.kind != AdventureData.Kind.RIFT || s.phase != AdventureData.Phase.ACTIVE || !s.initialized || LegacyService.eligible(p, r.id, 1, 32) != m) {
            return amount;
        }
        CompoundTag wards = s.journey.m_128469_("Wards");
        float stored = wards.m_128457_(r.id.toString());
        if (!Float.isFinite(stored)) {
            stored = 0.0f;
        }
        if ((spent = Math.min(amount, Math.max(0.0f, Math.min(2.0f, stored)))) > 0.0f) {
            wards.m_128350_(r.id.toString(), stored - spent);
            JourneyService.dirty(p);
        }
        return amount - spent;
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        Mob boss;
        Entity entity;
        CompoundTag n = s.journey;
        long now = JourneyService.now(p);
        if (!s.initialized && s.kind == AdventureData.Kind.BOSS) {
            JourneyCombat.boss(p, s, n.m_128451_("Choice"));
            s.initialized = true;
            return;
        }
        if (s.initialized && s.npcs.isEmpty()) {
            JourneyCombat.removeBar(s.id);
            s.initialized = false;
            ++s.wave;
            n.m_128473_("Wards");
            if (s.kind == AdventureData.Kind.BOSS || s.wave >= 3) {
                AdventureWorld.ready(p, s);
                return;
            }
            n.m_128379_("ChooseRoute", true);
            n.m_128379_("ChooseBoon", true);
            n.m_128473_("BossNpc");
            return;
        }
        if (!n.m_128403_("BossNpc")) {
            return;
        }
        AdventureData.Npc record = s.npcs.get(n.m_128342_("BossNpc"));
        if (record == null || record.active == null || !((entity = p.m_284548_().m_8791_(record.active)) instanceof Mob) || !(boss = (Mob)entity).m_6084_()) {
            JourneyCombat.removeBar(s.id);
            return;
        }
        int choice = n.m_128451_("BossChoice");
        ServerBossEvent bar = bars.computeIfAbsent(s.id, k -> new ServerBossEvent((Component)Component.m_237113_((String)JourneyService.BOSSES[choice]), choice == 1 ? BossEvent.BossBarColor.BLUE : BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS));
        bar.m_6543_(p);
        bar.m_142711_(Math.max(0.0f, Math.min(1.0f, boss.m_21223_() / boss.m_21233_())));
        if (boss.m_21223_() <= boss.m_21233_() / 2.0f && !n.m_128471_("Enraged")) {
            n.m_128379_("Enraged", true);
            JourneyService.tell(p, JourneyService.BOSSES[choice] + " enters its second phase!");
            if (choice == 1) {
                for (int i = 0; i < 2; ++i) {
                    JourneyCombat.add(p, s, (EntityType<? extends Mob>)EntityType.f_20524_);
                }
            }
        }
        if (n.m_128441_("StrikeAt")) {
            Vec3 center = Vec3.m_82539_((Vec3i)BlockPos.m_122022_((long)n.m_128454_("WarningPos")));
            double angle = n.m_128459_("WarningAngle");
            JourneyCombat.draw(p, choice, center, angle);
            if (now >= n.m_128454_("StrikeAt")) {
                for (LivingEntity target : p.m_284548_().m_6443_(LivingEntity.class, boss.m_20191_().m_82400_(20.0), e -> AdventureWorld.allowedTarget(boss, e))) {
                    Vec3 delta = target.m_20182_().m_82546_(center);
                    boolean bl = choice == 2 ? delta.f_82479_ * Math.cos(angle) + delta.f_82481_ * Math.sin(angle) >= 0.0 && delta.f_82479_ * Math.cos(angle) + delta.f_82481_ * Math.sin(angle) <= 8.0 && Math.abs(delta.f_82479_ * -Math.sin(angle) + delta.f_82481_ * Math.cos(angle)) <= 1.5 : delta.m_165925_() <= (double)(choice == 0 ? 36 : 9);
                    boolean inside = bl;
                    if (!inside || !(Math.abs(delta.f_82480_) < 3.0) || !boss.m_142582_((Entity)target)) continue;
                    target.m_6469_(boss.m_269291_().m_269104_((Entity)boss, (Entity)boss), choice == 0 ? 4.0f : (choice == 1 ? 3.0f : 6.0f));
                    if (choice != 1) continue;
                    target.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 40, 0));
                }
                n.m_128473_("StrikeAt");
                n.m_128356_("SpecialAt", now + (long)(n.m_128471_("Enraged") ? 160 : 240));
            }
        } else if (now >= n.m_128454_("SpecialAt")) {
            Vec3 at = choice == 1 ? p.m_20182_() : boss.m_20182_();
            n.m_128356_("WarningPos", BlockPos.m_274446_((Position)at).m_121878_());
            n.m_128347_("WarningAngle", Math.atan2(p.m_20189_() - boss.m_20189_(), p.m_20185_() - boss.m_20185_()));
            n.m_128356_("StrikeAt", now + 40L);
            JourneyCombat.draw(p, choice, at, n.m_128459_("WarningAngle"));
            JourneyService.tell(p, choice == 0 ? "Shockwave incoming \u2014 move beyond the ring!" : (choice == 1 ? "Frost gathers beneath you \u2014 move away!" : "The Tyrant prepares a strike \u2014 leave the marked line!"));
        }
    }

    private static void draw(ServerPlayer p, int choice, Vec3 at, double angle) {
        for (int i = 0; i < 32; ++i) {
            double z;
            double x;
            double a = (double)i * Math.PI / 16.0;
            if (choice == 2) {
                x = at.f_82479_ + Math.cos(angle) * ((double)i / 4.0);
                z = at.f_82481_ + Math.sin(angle) * ((double)i / 4.0);
            } else {
                x = at.f_82479_ + Math.cos(a) * (double)(choice == 0 ? 6 : 3);
                z = at.f_82481_ + Math.sin(a) * (double)(choice == 0 ? 6 : 3);
            }
            p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123745_, true, x, at.f_82480_ + 0.2, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}

