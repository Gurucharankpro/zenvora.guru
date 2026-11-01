/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.army;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.sentinel.SentinelService;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilJobs;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.Vec3;

public final class ShadowTargetGoal
extends Goal {
    private final Mob mob;

    public ShadowTargetGoal(Mob mob) {
        this.mob = mob;
        this.m_7021_(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean m_8036_() {
        return ShadowService.isShadow((Entity)this.mob);
    }

    public boolean m_8045_() {
        return this.m_8036_();
    }

    public boolean m_183429_() {
        return true;
    }

    public void m_8037_() {
        LivingEntity target;
        block14: {
            Vec3 center;
            ServerPlayer owner;
            ShadowRecord r;
            block17: {
                block16: {
                    LivingEntity attacked;
                    block15: {
                        r = ShadowService.record((Entity)this.mob);
                        owner = ShadowService.owner((Entity)this.mob);
                        if (!ShadowService.valid(this.mob, r) || owner == null || r.order == Order.STAY && !VigilCrown.member(owner, r) && !OdysseyRuns.fighting(owner, r)) {
                            this.setTarget(null);
                            return;
                        }
                        if (OdysseyRuns.parked(owner, r)) {
                            this.setTarget(null);
                            return;
                        }
                        if (SoulPlay.moving(this.mob, r, owner) || OdysseyRuns.moving(this.mob, r, owner) || VigilCrown.member(owner, r) || VigilJobs.moving(this.mob, r, owner)) {
                            return;
                        }
                        center = (r.order == Order.GUARD || r.order == Order.PATROL) && !OdysseyRuns.fighting(owner, r) ? Vec3.m_82539_((Vec3i)r.anchor) : owner.m_20182_();
                        target = this.mob.m_5448_();
                        if (target != null && !this.eligible(owner, target, center, r, false)) {
                            target = null;
                        }
                        boolean commanded = false;
                        if (r.battle.commandedTarget != null) {
                            LivingEntity living;
                            Entity locked = ((ServerLevel)this.mob.m_9236_()).m_8791_(r.battle.commandedTarget);
                            if (locked instanceof LivingEntity && this.eligible(owner, living = (LivingEntity)locked, center, r, false)) {
                                target = living;
                                commanded = true;
                            } else {
                                r.battle.commandedTarget = null;
                                ArmyData.get(owner.f_8924_).m_77762_();
                            }
                        }
                        if (!commanded && r.battle.priority != BattleRules.Priority.DEFAULT && this.mob.f_19797_ % 10 == 0) {
                            ArrayList<LivingEntity> choices = new ArrayList<LivingEntity>();
                            LivingEntity revenge = owner.m_21188_();
                            LivingEntity attacked2 = owner.m_21214_();
                            if (owner.f_19797_ - owner.m_21213_() < 200 && this.eligible(owner, revenge, center, r, true)) {
                                choices.add(revenge);
                            }
                            if (owner.f_19797_ - owner.m_21215_() < 200 && this.eligible(owner, attacked2, center, r, true)) {
                                choices.add(attacked2);
                            }
                            if (r.aggressive || OdysseyRuns.fighting(owner, r)) {
                                choices.addAll(this.mob.m_9236_().m_6443_(LivingEntity.class, this.mob.m_20191_().m_82400_(16.0), e -> e instanceof Enemy && this.eligible(owner, (LivingEntity)e, center, r, true)));
                            }
                            Comparator<LivingEntity> compare = r.battle.priority == BattleRules.Priority.WEAKEST ? Comparator.comparingDouble(e -> e.m_21223_() / Math.max(1.0f, e.m_21233_())).thenComparingDouble(arg_0 -> ((Mob)this.mob).m_20280_(arg_0)) : Comparator.comparingDouble(arg_0 -> ((Mob)this.mob).m_20280_(arg_0));
                            target = r.battle.priority == BattleRules.Priority.OWNER_ATTACKER && choices.contains(revenge) ? revenge : (LivingEntity)choices.stream().min(compare.thenComparing(e -> e.m_20148_().toString())).orElse(null);
                        }
                        if (target != null || this.mob.f_19797_ % 10 != 0) break block14;
                        LivingEntity revenge = owner.m_21188_();
                        attacked = owner.m_21214_();
                        if (owner.f_19797_ - owner.m_21213_() >= 200 || !this.eligible(owner, revenge, center, r, true)) break block15;
                        target = revenge;
                        break block14;
                    }
                    if (owner.f_19797_ - owner.m_21215_() >= 200 || !this.eligible(owner, attacked, center, r, true)) break block16;
                    target = attacked;
                    break block14;
                }
                if (r.aggressive) break block17;
                if (!OdysseyRuns.fighting(owner, r)) break block14;
            }
            target = this.mob.m_9236_().m_6443_(LivingEntity.class, this.mob.m_20191_().m_82400_(16.0), e -> e instanceof Enemy && this.eligible(owner, (LivingEntity)e, center, r, true)).stream().min(Comparator.comparingDouble(arg_0 -> ((Mob)this.mob).m_20280_(arg_0))).orElse(null);
        }
        this.setTarget(target);
    }

    private void setTarget(LivingEntity target) {
        if (this.mob.m_5448_() != target) {
            this.mob.m_6710_(target);
        }
        if (this.mob.m_6274_().m_147339_().containsKey(MemoryModuleType.f_26372_)) {
            if (target == null) {
                this.mob.m_6274_().m_21936_(MemoryModuleType.f_26372_);
            } else {
                this.mob.m_6274_().m_21879_(MemoryModuleType.f_26372_, (Object)target);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean eligible(ServerPlayer owner, LivingEntity entity, Vec3 center, ShadowRecord r, boolean lineOfSight) {
        if (entity == null) return false;
        if (!SentinelService.targetAllowed(this.mob, r, owner, entity)) return false;
        if (!OdysseyRuns.fighting(owner, r) && r.order == Order.FOLLOW) {
            if (!(this.mob.m_20280_((Entity)owner) <= 196.0)) return false;
            if (!(entity.m_20280_((Entity)owner) <= 256.0)) return false;
        }
        if (!ShadowService.canAttack(owner, entity)) return false;
        double d = entity.m_20238_(center);
        int n = (r.order == Order.GUARD || r.order == Order.PATROL) && !OdysseyRuns.fighting(owner, r) ? 144 : 1024;
        if (!(d <= (double)n)) return false;
        if (!lineOfSight) return true;
        if (!this.mob.m_21574_().m_148306_((Entity)entity)) return false;
        return true;
    }
}

