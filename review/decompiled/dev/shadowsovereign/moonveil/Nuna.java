/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.AgeableMob
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.TamableAnimal
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.FollowOwnerGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.Level
 */
package dev.shadowsovereign.moonveil;

import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.moonveil.MoonveilContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public final class Nuna
extends TamableAnimal {
    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.m_135353_(Nuna.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Long> START = SynchedEntityData.m_135353_(Nuna.class, (EntityDataSerializer)EntityDataSerializers.f_244073_);

    public Nuna(EntityType<? extends Nuna> t, Level w) {
        super(t, w);
    }

    public static AttributeSupplier.Builder attributes() {
        return Nuna.m_21552_().m_22268_(Attributes.f_22276_, 24.0).m_22268_(Attributes.f_22279_, 0.27).m_22268_(Attributes.f_22281_, 3.0);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(ACTION, (Object)0);
        this.f_19804_.m_135372_(START, (Object)0L);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(1, (Goal)new SitWhenOrderedToGoal(this){

            public boolean m_8036_() {
                return !ShadowService.isShadow((Entity)Nuna.this) && super.m_8036_();
            }
        });
        this.f_21345_.m_25352_(3, (Goal)new FollowOwnerGoal(this, 1.1, 5.0f, 2.0f, false){

            public boolean m_8036_() {
                return !ShadowService.isShadow((Entity)Nuna.this) && super.m_8036_();
            }
        });
        this.f_21345_.m_25352_(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.7));
        this.f_21345_.m_25352_(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 8.0f));
        this.f_21345_.m_25352_(7, (Goal)new RandomLookAroundGoal((Mob)this));
    }

    public void animate(int value) {
        this.f_19804_.m_135381_(ACTION, (Object)value);
        this.f_19804_.m_135381_(START, (Object)this.m_9236_().m_46467_());
    }

    public String clip(float amount) {
        return this.m_21224_() ? "death" : ((Integer)this.f_19804_.m_135370_(ACTION) == 1 ? "summoned_arrival" : ((Integer)this.f_19804_.m_135370_(ACTION) == 2 ? "eat" : ((Integer)this.f_19804_.m_135370_(ACTION) == 3 ? "happy_jump" : (this.m_21825_() ? (this.m_9236_().m_46462_() ? "sleep" : "sit") : (amount > 0.02f ? (amount > 0.5f ? "run" : "walk") : (this.m_21824_() && this.m_9236_().m_46462_() ? "tail_wag" : "idle"))))));
    }

    public float clipAge(float partial) {
        return (Integer)this.f_19804_.m_135370_(ACTION) > 0 ? ((float)(this.m_9236_().m_46467_() - (Long)this.f_19804_.m_135370_(START)) + partial) / 20.0f : (float)this.f_19797_ / 20.0f;
    }

    public void m_8107_() {
        super.m_8107_();
        if (!this.m_9236_().f_46443_ && (Integer)this.f_19804_.m_135370_(ACTION) > 0 && this.m_9236_().m_46467_() - (Long)this.f_19804_.m_135370_(START) > 48L) {
            this.f_19804_.m_135381_(ACTION, (Object)0);
        }
    }

    public boolean m_6898_(ItemStack s) {
        return s.m_150930_(Items.f_42780_) || s.m_150930_(MoonveilContent.block("moonveil_moonbell_flower").m_5456_());
    }

    public InteractionResult m_6071_(Player p, InteractionHand hand) {
        ItemStack stack = p.m_21120_(hand);
        if (!this.m_21824_() && this.m_6898_(stack)) {
            if (!this.m_9236_().f_46443_) {
                this.m_21828_(p);
                if (!p.m_150110_().f_35937_) {
                    stack.m_41774_(1);
                }
                this.m_21530_();
                this.animate(3);
                this.m_9236_().m_7605_((Entity)this, (byte)7);
            }
            return InteractionResult.m_19078_((boolean)this.m_9236_().f_46443_);
        }
        if (this.m_21824_() && this.m_21830_((LivingEntity)p) && this.m_6898_(stack)) {
            if (!this.m_9236_().f_46443_) {
                this.m_5634_(6.0f);
                if (!p.m_150110_().f_35937_) {
                    stack.m_41774_(1);
                }
                this.animate(2);
            }
            return InteractionResult.m_19078_((boolean)this.m_9236_().f_46443_);
        }
        if (this.m_21830_((LivingEntity)p) && p.m_6144_() && stack.m_41619_() && !ShadowService.isShadow((Entity)this)) {
            if (!this.m_9236_().f_46443_) {
                this.m_21839_(!this.m_21827_());
                this.m_21573_().m_26573_();
            }
            return InteractionResult.m_19078_((boolean)this.m_9236_().f_46443_);
        }
        if (this.m_21824_() && this.m_6898_(stack)) {
            return InteractionResult.PASS;
        }
        return super.m_6071_(p, hand);
    }

    public AgeableMob m_142606_(ServerLevel w, AgeableMob other) {
        return null;
    }

    public void m_7380_(CompoundTag n) {
        super.m_7380_(n);
    }
}

