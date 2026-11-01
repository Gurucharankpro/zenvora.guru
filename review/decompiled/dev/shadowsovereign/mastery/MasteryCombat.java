/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.mastery;

import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicProfiles;
import dev.shadowsovereign.mythic.SoulBolt;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public final class MasteryCombat {
    public static boolean strike(MythicBeast m, ServerLevel w, LivingEntity target, MythicProfiles.Move move) {
        int e = move.effect();
        if (e < 39 || e > 46) {
            return false;
        }
        float damage = (float)m.m_21133_(Attributes.f_22281_) * move.power();
        if (e == 46) {
            m.m_5634_(Math.min(12.0f, m.m_21233_() * 0.025f));
            w.m_8767_((ParticleOptions)ParticleTypes.f_123746_, m.m_20185_(), m.m_20186_() + 1.0, m.m_20189_(), 15, 0.4, 0.8, 0.4, 0.02);
            return true;
        }
        if (e == 42) {
            m.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 60, 0));
            w.m_8767_((ParticleOptions)ParticleTypes.f_123748_, m.m_20185_(), m.m_20186_() + 1.0, m.m_20189_(), 10, 0.6, 0.8, 0.6, 0.01);
            return true;
        }
        if (e == 43 || e == 45) {
            if (m.m_20280_((Entity)target) > (double)(move.range() * move.range()) || !m.m_142582_((Entity)target)) {
                return true;
            }
            SoulBolt b = new SoulBolt(m, damage);
            Vec3 aim = target.m_146892_().m_82546_(m.m_146892_());
            b.m_6686_(aim.f_82479_, aim.f_82480_, aim.f_82481_, 1.1f, 0.0f);
            w.m_7967_((Entity)b);
            return true;
        }
        for (LivingEntity t : w.m_6443_(LivingEntity.class, m.m_20191_().m_82400_((double)move.range()), m::allowed)) {
            if (m.m_20280_((Entity)t) > (double)(move.range() * move.range()) || !m.m_142582_((Entity)t)) continue;
            t.m_6469_(m.m_269291_().m_269333_((LivingEntity)m), damage);
            if (e == 41) {
                t.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 60, 1));
                continue;
            }
            if (e == 44) {
                t.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 60, 0));
                continue;
            }
            t.m_147240_(e == 40 ? 0.8 : 0.4, m.m_20185_() - t.m_20185_(), m.m_20189_() - t.m_20189_());
        }
        w.m_8767_((ParticleOptions)(e == 44 ? ParticleTypes.f_123763_ : ParticleTypes.f_123748_), m.m_20185_(), m.m_20186_() + 1.0, m.m_20189_(), 18, 1.0, 0.4, 1.0, 0.03);
        return true;
    }
}

