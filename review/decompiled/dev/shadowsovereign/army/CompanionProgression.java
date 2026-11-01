/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 */
package dev.shadowsovereign.army;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseRoles;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.soulforge.SoulTrait;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.sovereign.SovereignService;
import dev.shadowsovereign.sovereign.SovereignState;
import dev.shadowsovereign.warfront.WarfrontBuilds;
import dev.shadowsovereign.warfront.WarfrontState;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public final class CompanionProgression {
    private static final int[] REQUIRED_KILLS = new int[]{0, 5, 15, 30, 50, 75, 105, 140, 180, 225};
    public static final int HEAL_INTERVAL = 100;
    public static final int RESPAWN_DELAY = 600;

    private CompanionProgression() {
    }

    public static int level(long totalKills) {
        for (int i = REQUIRED_KILLS.length - 1; i >= 0; --i) {
            if (totalKills < (long)REQUIRED_KILLS[i]) continue;
            return i + 1;
        }
        return 1;
    }

    public static int requiredKills(int level) {
        return REQUIRED_KILLS[Math.max(1, Math.min(10, level)) - 1];
    }

    public static double multiplier(long kills) {
        return 1.0 + (double)(CompanionProgression.level(kills) - 1) * 0.1;
    }

    @Nullable
    public static Entity source(DamageSource damage) {
        Entity direct = damage.m_7640_();
        if (direct != null && ShadowService.isShadow(direct) && direct.getPersistentData().m_128403_("ShadowSovereignRecord")) {
            return direct;
        }
        Entity indirect = damage.m_7639_();
        return indirect != null && ShadowService.isShadow(indirect) ? indirect : null;
    }

    @Nullable
    public static ShadowRecord attacker(DamageSource damage) {
        Entity entity = CompanionProgression.source(damage);
        return entity == null ? null : ShadowService.record(entity);
    }

    public static void killed(LivingEntity victim, DamageSource damage) {
        if (victim.m_9236_().f_46443_ || victim instanceof Player || ShadowService.isShadow((Entity)victim)) {
            return;
        }
        ShadowRecord r = CompanionProgression.attacker(damage);
        Entity killer = CompanionProgression.source(damage);
        if (r == null || killer == null || victim.getPersistentData().m_128471_("SoulboundKillCounted")) {
            return;
        }
        victim.getPersistentData().m_128379_("SoulboundKillCounted", true);
        int previousLevel = r.level();
        if (r.totalKills < Long.MAX_VALUE) {
            ++r.totalKills;
        }
        CompanionService.killed(killer, r);
        SoulLifeService.killed(killer, r);
        EclipseRoles.killed(killer, r);
        SovereignService.progress(ShadowService.owner(killer), r.id, SovereignState.Goal.HUNT);
        ArmyData.get(killer.m_20194_()).m_77762_();
        if (r.level() != previousLevel) {
            ServerPlayer owner;
            Mob active = ShadowService.active(killer.m_20194_(), r);
            if (active != null) {
                ShadowService.updateName(active, r);
            }
            if ((owner = ShadowService.owner(killer)) != null) {
                ShadowService.tell(owner, r.name + " reached level " + r.level() + "! Power +" + (r.level() - 1) * 10 + "%.");
            }
        }
    }

    public static void damaged(Mob mob) {
        long now;
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (!ShadowService.valid(mob, r)) {
            return;
        }
        r.lastDamageAt = now = mob.m_20194_().m_129783_().m_46467_();
        r.nextHealAt = now + 100L;
        ArmyData.get(mob.m_20194_()).m_77762_();
    }

    public static void tickHealing(Mob mob, ShadowRecord r) {
        long now = mob.m_20194_().m_129783_().m_46467_();
        if (mob.m_21223_() < r.health && now - r.lastDamageAt > 1L) {
            CompanionProgression.damaged(mob);
        }
        if (r.nextHealAt < 0L) {
            r.nextHealAt = now + 100L;
            ArmyData.get(mob.m_20194_()).m_77762_();
        }
        if (now >= r.nextHealAt && (r.lastDamageAt < 0L || now - r.lastDamageAt >= 100L)) {
            if (mob.m_21223_() < mob.m_21233_()) {
                mob.m_5634_((r.trait == SoulTrait.VITAL ? 2.0f : 1.0f) + (float)(WarfrontBuilds.rune(mob, r) == WarfrontState.Rune.MERCY && mob.m_21223_() < mob.m_21233_() / 2.0f ? 1 : 0));
            }
            r.nextHealAt = now + 100L;
            ArmyData.get(mob.m_20194_()).m_77762_();
        }
        r.health = mob.m_21223_();
    }
}

