/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.legacy;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.legacy.LegacyState;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class LegacyChallenges {
    public static final UUID FLEET = UUID.fromString("643c1611-1b5c-437e-bc37-6c3735af1e05");

    private LegacyChallenges() {
    }

    public static boolean legacy(AdventureData.Kind k) {
        return k == AdventureData.Kind.GAUNTLET || k == AdventureData.Kind.SIEGE || k == AdventureData.Kind.VAULT;
    }

    public static boolean startVault(ServerPlayer p) {
        AdventureData.Journal j = AdventureService.journal(p);
        long time = LegacyService.now(p);
        long day = time / 24000L;
        if (!LegacyService.available(p, 1) || !AdventureService.requireHub(p) || j.session != null || j.legacy.vaultClaimedDay == day) {
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = AdventureData.Kind.VAULT;
        s.phase = AdventureData.Phase.ACTIVE;
        s.anchor = j.sanctuary;
        s.dimension = j.dimension;
        s.legacy.day = day;
        s.legacy.seed = p.m_20148_().getMostSignificantBits() ^ p.m_20148_().getLeastSignificantBits() ^ day * 7919L;
        j.session = s;
        LegacyChallenges.round(p, s);
        LegacyService.notice(p, "Rune Vault: remember the symbols, then repeat them. One reward per saved day.");
        LegacyService.dirty(p);
        return true;
    }

    private static void round(ServerPlayer p, AdventureData.Session s) {
        long time = LegacyService.now(p);
        s.legacy.input = 0;
        s.legacy.showUntil = time + (long)(s.legacy.sequence(s.wave).length + 1) * 20L;
        s.legacy.deadline = s.legacy.showUntil + 600L;
        s.legacy.clock = time;
    }

    public static boolean retry(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || s.kind != AdventureData.Kind.VAULT || s.phase != AdventureData.Phase.ACTIVE) {
            return false;
        }
        s.wave = 0;
        LegacyChallenges.round(p, s);
        LegacyService.dirty(p);
        return true;
    }

    public static boolean symbol(ServerPlayer p, int value) {
        AdventureData.Journal j = AdventureService.journal(p);
        AdventureData.Session s = j.session;
        long time = LegacyService.now(p);
        if (s == null || s.kind != AdventureData.Kind.VAULT || s.phase != AdventureData.Phase.ACTIVE || !AdventureService.nearHub(p) || value < 0 || value > 3 || time < s.legacy.showUntil) {
            return false;
        }
        if (time > s.legacy.deadline) {
            LegacyChallenges.retry(p);
            return false;
        }
        int[] answer = s.legacy.sequence(s.wave);
        if (s.legacy.input >= answer.length || answer[s.legacy.input] != value) {
            LegacyChallenges.retry(p);
            LegacyService.notice(p, "The rune faded. Watch the same daily pattern and try again.");
            return false;
        }
        if (++s.legacy.input == answer.length) {
            ++s.wave;
            if (s.wave >= 3) {
                AdventureWorld.ready(p, s);
            } else {
                LegacyChallenges.round(p, s);
            }
        }
        LegacyService.dirty(p);
        return true;
    }

    public static boolean choose(ServerPlayer p, int choice) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || s.kind != AdventureData.Kind.GAUNTLET || s.phase != AdventureData.Phase.ACTIVE || !s.legacy.choosing || choice < 0 || choice > 2 || s.legacy.boons[choice] >= 3) {
            return false;
        }
        int n = choice;
        s.legacy.boons[n] = s.legacy.boons[n] + 1;
        s.legacy.choosing = false;
        s.nextWave = LegacyService.now(p) + 100L;
        LegacyService.dirty(p);
        return true;
    }

    public static boolean extract(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || s.kind != AdventureData.Kind.GAUNTLET || s.phase != AdventureData.Phase.ACTIVE || s.initialized || !s.npcs.isEmpty() || s.wave < 5) {
            return false;
        }
        AdventureWorld.ready(p, s);
        return true;
    }

    public static void tick(ServerPlayer p, AdventureData.Journal j, AdventureData.Session s) {
        long time;
        s.legacy.clock = time = LegacyService.now(p);
        if (s.kind == AdventureData.Kind.VAULT) {
            if (!AdventureService.nearHub(p)) {
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                return;
            }
            if (time > s.legacy.deadline) {
                LegacyChallenges.retry(p);
            }
            LegacyService.dirty(p);
            return;
        }
        LegacyChallenges.applyFleet(p, s);
        if (s.kind == AdventureData.Kind.SIEGE) {
            p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123810_, true, (double)s.anchor.m_123341_() + 0.5, (double)(s.anchor.m_123342_() + 2), (double)s.anchor.m_123343_() + 0.5, 7, 0.25, 0.4, 0.25, 0.005);
            for (AdventureData.Npc n : s.npcs.values()) {
                Mob enemy;
                Entity entity;
                if (n.active == null || !((entity = p.m_284548_().m_8791_(n.active)) instanceof Mob) || !(enemy = (Mob)entity).m_6084_() || enemy.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) > 9.0 || time < s.legacy.crystalAt.getOrDefault(n.id, 0L)) continue;
                s.legacy.crystalAt.put(n.id, time + 40L);
                s.legacy.integrity = Math.max(0, s.legacy.integrity - (enemy.m_6095_() == EntityType.f_20518_ ? 10 : 5));
                enemy.m_6674_(InteractionHand.MAIN_HAND);
                p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123746_, true, (double)s.anchor.m_123341_() + 0.5, (double)s.anchor.m_123342_() + 1.7, (double)s.anchor.m_123343_() + 0.5, 8, 0.3, 0.3, 0.3, 0.02);
            }
            if (s.legacy.integrity == 0) {
                LegacyChallenges.fail(p, "Soul crystal destroyed");
                return;
            }
        }
        if (s.npcs.isEmpty() && s.initialized) {
            s.wave = Math.min(0x7FFFFFFE, s.wave + 1);
            s.initialized = false;
            s.nextWave = time + 100L;
            s.legacy.wards.clear();
            s.legacy.crystalAt.clear();
            if (s.kind == AdventureData.Kind.GAUNTLET) {
                j.legacy.bestWave = Math.max(j.legacy.bestWave, s.wave);
                LegacyService.achievements(p);
                for (UUID id : s.participants) {
                    Mob m = LegacyService.eligible(p, id, 1, 32);
                    if (m == null || s.legacy.rank(1) <= 0) continue;
                    m.m_5634_((float)s.legacy.rank(1));
                }
                if (s.wave % 5 == 0 && Arrays.stream(s.legacy.boons).anyMatch(rank -> rank < 3)) {
                    s.legacy.choosing = true;
                    LegacyService.notice(p, "Choose a temporary Gauntlet upgrade, or extract and claim your reward.");
                }
            } else if (s.wave >= 6) {
                AdventureWorld.ready(p, s);
                return;
            }
        }
        if (!s.initialized && !s.legacy.choosing && time >= s.nextWave) {
            int wave = s.wave + 1;
            int count = s.kind == AdventureData.Kind.SIEGE ? 4 + wave : Math.min(12, 4 + s.wave / 2);
            int scaling = Math.min(19, s.wave);
            for (int i = 0; i < count; ++i) {
                AttributeInstance attack;
                boolean boss;
                boolean bl = i == count - 1 && (s.kind == AdventureData.Kind.SIEGE ? wave == 6 : wave % 5 == 0) ? true : (boss = false);
                EntityType type = boss ? EntityType.f_20518_ : (i % 3 == 0 ? EntityType.f_20501_ : (i % 3 == 1 ? EntityType.f_20524_ : EntityType.f_20479_));
                AdventureData.Npc n = AdventureWorld.template(p, s, (EntityType<? extends Mob>)type, AdventureData.Role.ENEMY);
                Mob template = (Mob)type.m_20615_((Level)p.m_284548_());
                template.m_20258_(n.snapshot);
                double health = s.kind == AdventureData.Kind.GAUNTLET ? 1.0 + (double)scaling * 0.08 : 1.0 + (double)s.wave * 0.1;
                AttributeInstance hp = template.m_21051_(Attributes.f_22276_);
                if (hp != null) {
                    hp.m_22100_(hp.m_22115_() * health);
                    template.m_21153_(template.m_21233_());
                }
                if ((attack = template.m_21051_(Attributes.f_22281_)) != null && s.kind == AdventureData.Kind.GAUNTLET) {
                    attack.m_22100_(attack.m_22115_() * (1.0 + (double)scaling * 0.03));
                }
                n.snapshot = AdventureWorld.snapshot(template);
                s.npcs.put(n.id, n);
            }
            s.initialized = true;
            for (UUID id : s.participants) {
                if (s.kind != AdventureData.Kind.GAUNTLET) continue;
                s.legacy.wards.put(id, Float.valueOf((float)s.legacy.rank(2) * 2.0f));
            }
            LegacyService.notice(p, (s.kind == AdventureData.Kind.GAUNTLET ? "Gauntlet" : "Siege") + " wave " + wave + (s.kind == AdventureData.Kind.SIEGE ? " / 6" : ""));
        }
        LegacyService.dirty(p);
    }

    public static float absorb(Mob mob, float damage) {
        ServerPlayer owner = ShadowService.owner((Entity)mob);
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (owner == null || r == null) {
            return damage;
        }
        AdventureData.Session s = AdventureService.journal((ServerPlayer)owner).session;
        if (s == null || s.kind != AdventureData.Kind.GAUNTLET || s.phase != AdventureData.Phase.ACTIVE || !s.initialized || !s.participants.contains(r.id) || LegacyService.eligible(owner, r.id, 1, 32) != mob) {
            return damage;
        }
        float ward = s.legacy.wards.getOrDefault(r.id, Float.valueOf(0.0f)).floatValue();
        float spent = Math.min(damage, ward);
        if (spent > 0.0f) {
            s.legacy.wards.put(r.id, Float.valueOf(ward - spent));
            LegacyService.dirty(owner);
        }
        return damage - spent;
    }

    private static void applyFleet(ServerPlayer p, AdventureData.Session s) {
        for (UUID id : s.participants) {
            AttributeInstance speed;
            ShadowRecord r = LegacyService.record(p, id);
            Mob mob = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (mob == null || (speed = mob.m_21051_(Attributes.f_22279_)) == null) continue;
            speed.m_22120_(FLEET);
            if (s.kind != AdventureData.Kind.GAUNTLET || !s.initialized || s.legacy.rank(0) <= 0 || LegacyService.eligible(p, id, 1, 32) != mob) continue;
            speed.m_22118_(new AttributeModifier(FLEET, "Soulbound Gauntlet Fleetfoot", (double)s.legacy.rank(0) * 0.1, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    public static void cleanup(MinecraftServer server, AdventureData.Session s) {
        if (!LegacyChallenges.legacy(s.kind)) {
            return;
        }
        for (ServerLevel level : server.m_129785_()) {
            for (Entity e : level.m_8583_()) {
                AttributeInstance speed;
                ShadowRecord r;
                Mob m;
                if (!(e instanceof Mob) || !ShadowService.isShadow((Entity)(m = (Mob)e)) || (r = ShadowService.record((Entity)m)) == null || !s.participants.contains(r.id) || (speed = m.m_21051_(Attributes.f_22279_)) == null) continue;
                speed.m_22120_(FLEET);
            }
        }
    }

    public static void paused(MinecraftServer server, AdventureData.Session s) {
        if (!LegacyChallenges.legacy(s.kind)) {
            return;
        }
        s.legacy.pausedAt = AdventureService.now(server);
        LegacyChallenges.cleanup(server, s);
    }

    public static void resumed(ServerPlayer p, AdventureData.Session s) {
        if (!LegacyChallenges.legacy(s.kind)) {
            return;
        }
        long shift = s.legacy.pausedAt >= 0L ? Math.max(0L, LegacyService.now(p) - s.legacy.pausedAt) : 0L;
        s.nextWave += shift;
        s.legacy.showUntil += shift;
        s.legacy.deadline += shift;
        s.legacy.crystalAt.replaceAll((id, time) -> time + shift);
        s.legacy.pausedAt = -1L;
        s.legacy.clock = LegacyService.now(p);
    }

    public static void completed(ServerPlayer p, AdventureData.Session s) {
        if (!LegacyChallenges.legacy(s.kind) || s.legacy.recorded) {
            return;
        }
        LegacyState l = LegacyService.state(p);
        s.legacy.recorded = true;
        if (s.kind == AdventureData.Kind.SIEGE) {
            l.increment(LegacyState.Stat.SIEGES);
        }
        if (s.kind == AdventureData.Kind.VAULT) {
            l.vaultClaimedDay = s.legacy.day;
            l.increment(LegacyState.Stat.VAULTS);
        }
        l.result(s.kind.name(), s.kind == AdventureData.Kind.GAUNTLET ? "EXTRACTED" : "VICTORY", s.wave, s.legacy.integrity, LegacyService.now(p));
        LegacyService.achievements(p);
        LegacyService.dirty(p);
    }

    public static void canceled(ServerPlayer p, AdventureData.Session s) {
        if (LegacyChallenges.legacy(s.kind) && !s.legacy.recorded) {
            s.legacy.recorded = true;
            LegacyService.state(p).result(s.kind.name(), "CANCELED", s.wave, s.legacy.integrity, LegacyService.now(p));
            LegacyService.dirty(p);
        }
    }

    public static void fail(ServerPlayer p, String why) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || !LegacyChallenges.legacy(s.kind) || s.phase == AdventureData.Phase.READY) {
            return;
        }
        if (!s.legacy.recorded) {
            s.legacy.recorded = true;
            LegacyService.state(p).result(s.kind.name(), "DEFEAT", s.wave, s.legacy.integrity, LegacyService.now(p));
        }
        AdventureWorld.cancel(p);
        LegacyService.notice(p, why + ". Your companions keep their normal soul respawns.");
    }

    public static void decorate(Mob mob) {
        AdventureData.Session s = AdventureWorld.session((Entity)mob);
        if (s == null || s.kind != AdventureData.Kind.SIEGE) {
            return;
        }
        mob.f_21345_.m_148105_().stream().filter(g -> g.m_26015_() instanceof CrystalAssault).toList().forEach(g -> mob.f_21345_.m_25363_(g.m_26015_()));
        mob.f_21345_.m_25352_(-20, (Goal)new CrystalAssault(mob));
    }

    private static final class CrystalAssault
    extends Goal {
        private final Mob mob;

        CrystalAssault(Mob m) {
            this.mob = m;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            return AdventureWorld.valid((Entity)this.mob) && AdventureWorld.session((Entity)this.mob).kind == AdventureData.Kind.SIEGE && this.mob.m_5448_() == null;
        }

        public boolean m_8045_() {
            return this.m_8036_();
        }

        public void m_8037_() {
            if (this.mob.f_19797_ % 10 != 0) {
                return;
            }
            AdventureData.Session s = AdventureWorld.session((Entity)this.mob);
            if (s != null) {
                this.mob.m_21573_().m_26519_((double)s.anchor.m_123341_() + 0.5, (double)(s.anchor.m_123342_() + 1), (double)s.anchor.m_123343_() + 0.5, 1.05);
            }
        }
    }
}

