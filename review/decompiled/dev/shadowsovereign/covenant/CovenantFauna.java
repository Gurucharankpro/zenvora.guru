/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.control.FlyingMoveControl
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 */
package dev.shadowsovereign.covenant;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class CovenantFauna
extends PathfinderMob {
    private final int kind;

    public CovenantFauna(EntityType<? extends CovenantFauna> type, Level w, int kind) {
        super(type, w);
        this.kind = kind;
        if (kind == 1) {
            this.f_21342_ = new FlyingMoveControl((Mob)this, 20, true);
            this.m_20242_(true);
        }
    }

    public int kind() {
        return this.kind;
    }

    public static AttributeSupplier.Builder attributes(int kind) {
        return Mob.m_21552_().m_22268_(Attributes.f_22276_, (new double[]{10.0, 6.0, 28.0})[kind]).m_22268_(Attributes.f_22279_, (new double[]{0.3, 0.18, 0.12})[kind]).m_22268_(Attributes.f_22280_, 0.25).m_22268_(Attributes.f_22281_, (new double[]{2.0, 1.0, 3.0})[kind]).m_22268_(Attributes.f_22284_, kind == 2 ? 6.0 : 0.0);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(3, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.f_21345_.m_25352_(7, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 6.0f));
        this.f_21345_.m_25352_(8, (Goal)new RandomLookAroundGoal((Mob)this));
    }

    public void m_8107_() {
        super.m_8107_();
        if (this.kind == 1) {
            if (!(this.f_21344_ instanceof FlyingPathNavigation)) {
                this.f_21344_ = new FlyingPathNavigation((Mob)this, this.m_9236_());
                this.f_21342_ = new FlyingMoveControl((Mob)this, 20, true);
            }
            this.m_20242_(true);
            if (!this.m_9236_().f_46443_ && this.m_5448_() == null && this.f_19797_ % 60 == 0) {
                BlockPos at = this.m_20183_().m_7918_(this.f_19796_.m_188503_(9) - 4, this.f_19796_.m_188503_(5) - 2, this.f_19796_.m_188503_(9) - 4);
                if (this.m_9236_().m_46805_(at) && this.m_9236_().m_8055_(at).m_60795_()) {
                    this.m_21573_().m_26519_((double)at.m_123341_() + 0.5, (double)at.m_123342_() + 0.5, (double)at.m_123343_() + 0.5, 0.7);
                }
            }
        }
    }
}

