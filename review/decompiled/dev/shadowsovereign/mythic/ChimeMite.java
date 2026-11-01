/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 */
package dev.shadowsovereign.mythic;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class ChimeMite
extends Monster {
    private static final EntityDataAccessor<Integer> ATTACK = SynchedEntityData.m_135353_(ChimeMite.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);

    public ChimeMite(EntityType<? extends ChimeMite> type, Level level) {
        super(type, level);
        this.f_21364_ = 4;
        this.m_21530_();
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.m_33035_().m_22268_(Attributes.f_22276_, 18.0).m_22268_(Attributes.f_22281_, 3.0).m_22268_(Attributes.f_22284_, 4.0).m_22268_(Attributes.f_22279_, 0.25);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(ATTACK, (Object)0);
    }

    public int attackTicks() {
        return (Integer)this.f_19804_.m_135370_(ATTACK);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(2, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.1, false));
        this.f_21345_.m_25352_(7, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.7));
        this.f_21345_.m_25352_(8, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 8.0f));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
    }

    public boolean m_7327_(Entity target) {
        if ((Integer)this.f_19804_.m_135370_(ATTACK) > 0) {
            return false;
        }
        boolean hit = super.m_7327_(target);
        if (hit && !this.m_9236_().f_46443_) {
            this.f_19804_.m_135381_(ATTACK, (Object)14);
        }
        return hit;
    }

    public void m_8107_() {
        super.m_8107_();
        if (!this.m_9236_().f_46443_ && (Integer)this.f_19804_.m_135370_(ATTACK) > 0) {
            this.f_19804_.m_135381_(ATTACK, (Object)((Integer)this.f_19804_.m_135370_(ATTACK) - 1));
        }
    }
}

