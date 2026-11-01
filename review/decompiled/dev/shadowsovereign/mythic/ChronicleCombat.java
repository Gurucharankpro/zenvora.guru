/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicProfiles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class ChronicleCombat {
    public static boolean strike(MythicBeast caster, ServerLevel level, LivingEntity target, MythicProfiles.Move move) {
        int effect = move.effect();
        if (effect == 25) {
            caster.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 60, 0));
            caster.m_20095_();
            ServerPlayer owner = ShadowService.owner((Entity)caster);
            if (owner != null) {
                for (LivingEntity ally : level.m_6443_(LivingEntity.class, caster.m_20191_().m_82400_(6.0), e -> e == owner || owner.m_20148_().equals(ShadowService.ownerId((Entity)e)))) {
                    if (caster.m_20280_((Entity)ally) > 36.0 || !caster.m_142582_((Entity)ally)) continue;
                    ally.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 40, 0));
                    ally.m_20095_();
                }
            }
        } else {
            float damage = (float)caster.m_21133_(Attributes.f_22281_) * move.power();
            for (LivingEntity enemy : level.m_6443_(LivingEntity.class, caster.m_20191_().m_82400_((double)move.range()), caster::allowed)) {
                if (caster.m_20280_((Entity)enemy) > (double)(move.range() * move.range()) || !caster.m_142582_((Entity)enemy) || effect == 19 && enemy != target) continue;
                enemy.m_6469_(caster.m_269291_().m_269333_((LivingEntity)caster), damage);
                switch (effect) {
                    case 19: {
                        enemy.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 30, 0));
                        break;
                    }
                    case 20: {
                        enemy.m_20095_();
                        enemy.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 50, 1));
                        break;
                    }
                    case 21: {
                        enemy.m_147240_(0.9, caster.m_20185_() - enemy.m_20185_(), caster.m_20189_() - enemy.m_20189_());
                        enemy.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 40, 0));
                        break;
                    }
                    case 22: {
                        enemy.m_147240_(0.7, caster.m_20185_() - enemy.m_20185_(), caster.m_20189_() - enemy.m_20189_());
                        break;
                    }
                    case 23: {
                        enemy.m_7292_(new MobEffectInstance(MobEffects.f_19610_, 40, 0));
                        enemy.m_7292_(new MobEffectInstance(MobEffects.f_19613_, 60, 0));
                        enemy.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 40, 0));
                        break;
                    }
                    case 24: {
                        enemy.m_7292_(new MobEffectInstance(MobEffects.f_19613_, 40, 0));
                    }
                }
            }
            if (effect == 24) {
                caster.m_7292_(new MobEffectInstance(MobEffects.f_19596_, 50, 0));
            }
        }
        SimpleParticleType particle = effect == 20 || effect == 25 ? ParticleTypes.f_123769_ : (effect == 24 ? ParticleTypes.f_123762_ : ParticleTypes.f_123746_);
        level.m_8767_((ParticleOptions)particle, caster.m_20185_(), caster.m_20186_() + 1.0, caster.m_20189_(), 20, 1.5, 0.6, 1.5, 0.03);
        caster.m_5496_(effect == 21 ? SoundEvents.f_11699_ : (effect == 20 ? SoundEvents.f_12541_ : SoundEvents.f_11862_), 0.7f, effect == 21 ? 0.55f : 0.9f);
        return true;
    }

    private ChronicleCombat() {
    }
}

