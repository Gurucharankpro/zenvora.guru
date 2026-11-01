/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.monster.RangedAttackMob
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.army;

import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.BattleService;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseRoles;
import dev.shadowsovereign.expansion.X1Hooks;
import dev.shadowsovereign.journey.JourneyActivities;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.odyssey.OdysseyCamp;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.sentinel.PatrolTorches;
import dev.shadowsovereign.sentinel.SentinelService;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilJobs;
import dev.shadowsovereign.warfront.WarfrontBuilds;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.EnumSet;
import java.util.Locale;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.phys.Vec3;

public final class ShadowGoal
extends Goal {
    private final Mob mob;
    private final boolean nativeCombat;
    private int cooldown;

    public ShadowGoal(Mob mob) {
        this.mob = mob;
        this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.nativeCombat = mob.m_6274_().m_147339_().containsKey(MemoryModuleType.f_26372_) || mob.f_21345_.m_148105_().stream().anyMatch(g -> {
            String name = g.m_26015_().getClass().getSimpleName().toLowerCase(Locale.ROOT);
            return !(g.m_26015_() instanceof ShadowGoal) && (name.contains("attack") || name.contains("combat") || name.contains("swell") || name.contains("shoot"));
        });
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean m_8036_() {
        MythicBeast b;
        ShadowRecord r = ShadowService.record((Entity)this.mob);
        ServerPlayer owner = ShadowService.owner((Entity)this.mob);
        if (!ShadowService.valid(this.mob, r)) {
            return false;
        }
        if (owner == null) {
            return true;
        }
        if (LivingRealm.controlled(owner, r)) {
            return true;
        }
        if (OdysseyRuns.parked(owner, r)) {
            return true;
        }
        if (SoulPlay.controlled(owner, r)) return true;
        if (OdysseyRuns.controlled(owner, r)) return true;
        if (OdysseyCamp.controlled(owner, r)) {
            return true;
        }
        if (JourneyActivities.camping(this.mob)) {
            return true;
        }
        if (VigilCrown.member(owner, r)) return true;
        if (VigilJobs.moving(this.mob, r, owner)) {
            return true;
        }
        if (r.order == Order.FOLLOW && !OdysseyRuns.fighting(owner, r) && this.mob.m_20280_((Entity)owner) > 196.0) {
            return true;
        }
        if (r.order == Order.STAY && !OdysseyRuns.fighting(owner, r)) {
            return true;
        }
        if (this.mob.m_5448_() != null) {
            if (this.nativeCombat) return false;
            return true;
        }
        if (OdysseyRuns.fighting(owner, r)) {
            return true;
        }
        if (r.order == Order.PATROL) {
            return true;
        }
        Vec3 center = r.order == Order.GUARD && !OdysseyRuns.fighting(owner, r) ? Vec3.m_82539_((Vec3i)r.anchor) : BattleService.followPosition(this.mob, r, owner);
        Mob mob = this.mob;
        if (mob instanceof MythicBeast && (b = (MythicBeast)mob).profile().flies() && !this.mob.m_20160_()) {
            center = center.m_82520_(0.0, 2.0, 0.0);
        }
        if (this.mob.m_20280_((Entity)owner) > 1024.0) {
            if (r.order == Order.FOLLOW) return true;
        }
        double d = this.mob.m_20238_(center);
        double d2 = r.order == Order.GUARD ? 4.0 : (r.battle.formation == BattleRules.Formation.LOOSE ? 12.0 : 2.25);
        if (!(d > d2)) return false;
        return true;
    }

    public boolean m_8045_() {
        return this.m_8036_();
    }

    public boolean m_183429_() {
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    public void m_8037_() {
        double distance;
        Vec3 center;
        ShadowRecord r;
        block23: {
            ServerPlayer owner;
            block24: {
                MythicBeast b;
                r = ShadowService.record((Entity)this.mob);
                owner = ShadowService.owner((Entity)this.mob);
                if (!ShadowService.valid(this.mob, r)) {
                    this.mob.m_6710_(null);
                    this.mob.m_21573_().m_26573_();
                    return;
                }
                if (owner == null) {
                    if (PatrolTorches.tick(this.mob, r)) {
                        return;
                    }
                    this.mob.m_6710_(null);
                    this.mob.m_6274_().m_21936_(MemoryModuleType.f_26372_);
                    Vec3 post = Vec3.m_82539_((Vec3i)r.anchor);
                    if (r.vigil.post && !r.vigil.suspended && this.mob.m_20238_(post) > 4.0) {
                        if (this.mob.f_19797_ % 10 != 0) return;
                        this.mob.m_21573_().m_26519_(post.f_82479_, post.f_82480_, post.f_82481_, 1.15);
                        return;
                    }
                    this.mob.m_21573_().m_26573_();
                    return;
                }
                if (OdysseyRuns.parked(owner, r)) {
                    this.mob.m_6710_(null);
                    this.mob.m_21573_().m_26573_();
                    return;
                }
                if (VigilCrown.hold(this.mob, r, owner)) return;
                if (LivingRealm.moving(this.mob, r, owner)) return;
                if (SoulPlay.moving(this.mob, r, owner)) return;
                if (OdysseyRuns.moving(this.mob, r, owner)) return;
                if (OdysseyCamp.moving(this.mob, r, owner)) return;
                if (VigilJobs.moving(this.mob, r, owner)) {
                    return;
                }
                if (SentinelService.moving(this.mob, r, owner)) {
                    return;
                }
                if (r.order == Order.FOLLOW && !OdysseyRuns.fighting(owner, r) && this.mob.m_20280_((Entity)owner) > 196.0) {
                    this.mob.m_6710_(null);
                }
                if (this.cooldown > 0) {
                    --this.cooldown;
                }
                if (JourneyActivities.campMove(this.mob)) {
                    return;
                }
                if (r.order == Order.STAY && !OdysseyRuns.fighting(owner, r)) {
                    this.mob.m_6710_(null);
                    this.mob.m_21573_().m_26573_();
                    this.mob.m_6274_().m_21936_(MemoryModuleType.f_26370_);
                    return;
                }
                LivingEntity target = this.mob.m_5448_();
                if (r.order == Order.PATROL && target == null && !OdysseyRuns.fighting(owner, r)) {
                    if (PatrolTorches.tick(this.mob, r)) return;
                    BattleService.tickPatrol(this.mob, r, owner);
                    return;
                }
                center = r.order == Order.GUARD && !OdysseyRuns.fighting(owner, r) ? Vec3.m_82539_((Vec3i)r.anchor) : BattleService.followPosition(this.mob, r, owner);
                Mob mob = this.mob;
                if (mob instanceof MythicBeast && (b = (MythicBeast)mob).profile().flies() && !this.mob.m_20160_()) {
                    center = center.m_82520_(0.0, 2.0, 0.0);
                }
                if (target != null) {
                    this.mob.m_21563_().m_24960_((Entity)target, 30.0f, 30.0f);
                    double reach = Math.max(2.4, (double)this.mob.m_20205_() * 0.8 + (double)target.m_20205_() * 0.5 + 1.2);
                    if (this.mob.m_20280_((Entity)target) <= reach * reach && this.mob.m_21574_().m_148306_((Entity)target)) {
                        this.mob.m_21573_().m_26573_();
                        if (this.cooldown != 0) return;
                        this.mob.m_6674_(InteractionHand.MAIN_HAND);
                        if (this.mob.m_21051_(Attributes.f_22281_) != null) {
                            this.mob.m_7327_((Entity)target);
                        } else {
                            target.m_6469_(this.mob.m_269291_().m_269333_((LivingEntity)this.mob), CompanionService.fallbackDamage(this.mob, target));
                        }
                        this.cooldown = 20;
                        return;
                    }
                    Mob mob2 = this.mob;
                    if (mob2 instanceof RangedAttackMob) {
                        RangedAttackMob ranged = (RangedAttackMob)mob2;
                        if (this.mob.m_20280_((Entity)target) < 225.0 && this.mob.m_21574_().m_148306_((Entity)target)) {
                            this.mob.m_21573_().m_26573_();
                            if (this.cooldown != 0) return;
                            ranged.m_6504_(target, 1.0f);
                            this.cooldown = 35;
                            return;
                        }
                    }
                    if (this.mob.f_19797_ % 5 != 0) return;
                    this.mob.m_21573_().m_5624_((Entity)target, 1.25);
                    return;
                }
                distance = this.mob.m_20238_(center);
                if (r.order != Order.FOLLOW) break block23;
                if (this.mob.m_9236_() != owner.m_9236_()) break block24;
                double d = this.mob.m_20280_((Entity)owner);
                int n = WarfrontBuilds.rune(this.mob, r) == WarfrontState.Rune.TETHER ? 24 : 32;
                if (!(d > Math.pow(EclipseRoles.catchup(this.mob, r, n), 2.0))) break block23;
            }
            if (this.mob.m_9236_() != owner.m_9236_()) return;
            if (this.mob.f_19797_ % 20 != 0) return;
            Vec3 safe = ShadowService.safePosition(this.mob, owner.m_20182_(), owner.m_284548_());
            if (safe == null) return;
            this.mob.m_6021_(safe.f_82479_, safe.f_82480_, safe.f_82481_);
            X1Hooks.emit((Entity)this.mob, 10);
            this.mob.m_21573_().m_26573_();
            return;
        }
        double d = r.order == Order.GUARD ? 4.0 : (r.battle.formation == BattleRules.Formation.LOOSE ? 12.0 : 2.25);
        if (distance > d) {
            if (this.mob.f_19797_ % 10 != 0) return;
            this.mob.m_21573_().m_26519_(center.f_82479_, center.f_82480_, center.f_82481_, 1.15);
            return;
        }
        this.mob.m_21573_().m_26573_();
    }
}

