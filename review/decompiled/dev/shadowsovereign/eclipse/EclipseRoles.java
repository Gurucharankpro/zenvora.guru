/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 */
package dev.shadowsovereign.eclipse;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class EclipseRoles {
    private static final UUID SPEED = UUID.fromString("11110000-0000-0000-0000-000000000001");
    private static final UUID RESIST = UUID.fromString("11110000-0000-0000-0000-000000000002");
    public static boolean proccing;

    public static int veteran(ShadowRecord r) {
        return EclipseState.veteran(r.totalKills);
    }

    public static boolean talent(Mob m, ShadowRecord r, EclipseState.Talent t) {
        UUID owner = ShadowService.ownerId((Entity)m);
        return owner != null && ArmyData.get((MinecraftServer)m.m_20194_()).army((UUID)owner).eclipse.talent(r.id, t);
    }

    private static void modifier(Mob m, Attribute attribute, UUID id, double amount, AttributeModifier.Operation op) {
        AttributeInstance a = m.m_21051_(attribute);
        if (a == null) {
            return;
        }
        AttributeModifier current = a.m_22111_(id);
        if (current != null && (Math.abs(current.m_22218_() - amount) > 1.0E-4 || amount == 0.0)) {
            a.m_22120_(id);
        }
        if (amount != 0.0 && a.m_22111_(id) == null) {
            a.m_22118_(new AttributeModifier(id, "Eclipse utility", amount, op));
        }
    }

    public static void tick(Mob m, ShadowRecord r) {
        double resist;
        boolean calm = m.m_5448_() == null && m.m_20194_().m_129783_().m_46467_() - r.lastDamageAt >= 100L;
        EclipseRoles.modifier(m, Attributes.f_22279_, SPEED, calm && EclipseRoles.talent(m, r, EclipseState.Talent.FLEET) ? 0.05 : 0.0, AttributeModifier.Operation.MULTIPLY_BASE);
        EclipseState.Build b = ArmyData.get((MinecraftServer)m.m_20194_()).army((UUID)ShadowService.ownerId((Entity)m)).eclipse.builds.get(r.id);
        double d = resist = EclipseRoles.talent(m, r, EclipseState.Talent.STEADFAST) ? 0.1 : 0.0;
        if (b != null && b.role == EclipseState.Role.GUARDIAN && m.getPersistentData().m_128454_("EclipseGuardianUntil") > m.m_20194_().m_129783_().m_46467_()) {
            resist += 0.2;
        }
        EclipseRoles.modifier(m, Attributes.f_22278_, RESIST, resist, AttributeModifier.Operation.ADDITION);
        EclipseRuns.boons(m, r);
    }

    public static void hurt(ServerPlayer p, ShadowRecord r, Mob mob) {
        EclipseState.Build b = EclipseService.state((ServerPlayer)p).builds.get(r.id);
        if (b != null && b.role == EclipseState.Role.GUARDIAN && EclipseService.now(p) >= b.procAt) {
            mob.getPersistentData().m_128356_("EclipseGuardianUntil", EclipseService.now(p) + 120L);
            b.procAt = EclipseService.now(p) + 240L - (long)(EclipseRoles.veteran(r) * 10);
            EclipseService.dirty(p);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void hit(ServerPlayer p, ShadowRecord r, Mob mob, LivingEntity target, boolean melee) {
        if (proccing) {
            return;
        }
        EclipseState.Build b = EclipseService.state((ServerPlayer)p).builds.get(r.id);
        if (b == null || !target.m_6084_()) {
            return;
        }
        long now = EclipseService.now(p);
        if (!target.m_20148_().equals(b.lastTarget) || now - b.lastHitAt > 100L) {
            b.hits = 0;
        }
        b.lastTarget = target.m_20148_();
        b.lastHitAt = now;
        ++b.hits;
        if (now < b.procAt) {
            return;
        }
        boolean success = false;
        if (b.role == EclipseState.Role.STRIKER && melee && b.hits >= 3) {
            b.hits = 0;
            proccing = true;
            try {
                int old = target.f_19802_;
                target.f_19802_ = 0;
                success = target.m_6469_(mob.m_269291_().m_269104_((Entity)mob, (Entity)mob), 2.0f);
                target.f_19802_ = Math.max(old, target.f_19802_);
            }
            finally {
                proccing = false;
            }
        }
        if (b.role == EclipseState.Role.ASSASSIN && (r.lastDamageAt < 0L || now - r.lastDamageAt >= 60L)) {
            success = target.m_147207_(new MobEffectInstance(MobEffects.f_19613_, 80, 0), (Entity)mob);
        }
        if (success) {
            b.procAt = now + 240L - (long)(EclipseRoles.veteran(r) * 10);
            EclipseService.dirty(p);
        }
    }

    public static void killed(Entity killer, ShadowRecord r) {
        Optional<Mob> injured;
        ServerPlayer p = ShadowService.owner(killer);
        if (p == null) {
            return;
        }
        EclipseState e = EclipseService.state(p);
        EclipseState.Metric metric = EclipseService.metric(p, r.id);
        if (metric != null) {
            ++metric.kills;
        }
        if (e.talent(r.id, EclipseState.Talent.CONSERVATION) && EclipseService.now(p) >= killer.getPersistentData().m_128454_("EclipseConserveAt")) {
            e.energy = Math.min(100.0f, e.energy + 1.0f);
            killer.getPersistentData().m_128356_("EclipseConserveAt", EclipseService.now(p) + 40L);
        }
        if (EclipseRuns.hasCard(p, EclipseState.Card.SOULFLOW)) {
            e.energy = Math.min(100.0f, e.energy + 2.0f);
        }
        EclipseState.Build b = e.builds.get(r.id);
        Mob m = ShadowService.active(p.f_8924_, r);
        if (b != null && m != null && b.role == EclipseState.Role.MEDIC && EclipseService.now(p) >= b.procAt && (injured = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().filter(x -> !x.id.equals(r.id)).map(x -> ShadowService.active(p.f_8924_, x)).filter(x -> x != null && x.m_6084_() && x.m_9236_() == m.m_9236_() && x.m_20280_((Entity)m) <= 64.0 && x.m_21223_() < x.m_21233_()).min(Comparator.comparingDouble(x -> x.m_21223_() / x.m_21233_()))).isPresent()) {
            injured.get().m_5634_(2.0f);
            b.procAt = EclipseService.now(p) + 240L - (long)(EclipseRoles.veteran(r) * 10);
        }
        EclipseService.dirty(p);
    }

    public static int feeding(ServerPlayer p, ShadowRecord r) {
        return EclipseService.state(p).talent(r.id, EclipseState.Talent.DEEP_BOND) ? 1 : 0;
    }

    public static int amethystCost(ServerPlayer p, ShadowRecord r) {
        return EclipseService.state(p).talent(r.id, EclipseState.Talent.LIGHTER_CRAFT) ? 7 : 8;
    }

    public static int catchup(Mob m, ShadowRecord r, int original) {
        return EclipseRoles.talent(m, r, EclipseState.Talent.QUICK_ORDERS) ? original - 4 : original;
    }

    public static int artCooldown(ServerPlayer p, UUID id) {
        double reduction;
        double d = reduction = EclipseService.state(p).talent(id, EclipseState.Talent.CLEAR_FOCUS) ? 0.1 : 0.0;
        if (EclipseRuns.hasCard(p, EclipseState.Card.CLEAR_MIND)) {
            reduction += 0.2;
        }
        return (int)Math.round(900.0 * (1.0 - reduction));
    }

    public static void planFinished(ServerPlayer p, Collection<UUID> participants) {
        for (UUID id : participants) {
            ShadowRecord r = EclipseService.record(p, id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m == null || !EclipseService.state(p).talent(id, EclipseState.Talent.RALLY_AID) || EclipseService.now(p) < m.getPersistentData().m_128454_("EclipseRallyAidAt")) continue;
            m.m_5634_(1.0f);
            m.getPersistentData().m_128356_("EclipseRallyAidAt", EclipseService.now(p) + 600L);
        }
    }
}

