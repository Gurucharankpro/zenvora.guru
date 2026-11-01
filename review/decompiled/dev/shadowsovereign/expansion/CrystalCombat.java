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
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.expansion;

import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicProfiles;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public final class CrystalCombat {
    public static boolean strike(MythicBeast m, ServerLevel w, LivingEntity target, MythicProfiles.Move move) {
        int effect = move.effect();
        if (effect < 49 || effect > 51) {
            return false;
        }
        float damage = (float)m.m_21133_(Attributes.f_22281_) * move.power();
        if (effect == 51) {
            m.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 60, 0));
            UUID owner = ShadowService.ownerId((Entity)m);
            if (owner != null) {
                for (Mob friend : w.m_6443_(Mob.class, m.m_20191_().m_82400_(10.0), t -> owner.equals(ShadowService.ownerId((Entity)t)))) {
                    friend.m_7292_(new MobEffectInstance(MobEffects.f_19596_, 60, 0));
                }
            }
            w.m_8767_((ParticleOptions)ParticleTypes.f_123809_, m.m_20185_(), m.m_20186_() + 1.0, m.m_20189_(), 12, 1.0, 0.6, 1.0, 0.02);
            return true;
        }
        Vec3 aim = target.m_20182_().m_82546_(m.m_20182_()).m_82541_();
        for (LivingEntity t2 : w.m_6443_(LivingEntity.class, m.m_20191_().m_82400_((double)move.range()), m::allowed)) {
            if (m.m_20280_((Entity)t2) > (double)(move.range() * move.range()) || !m.m_142582_((Entity)t2) || effect == 50 && t2.m_20182_().m_82546_(m.m_20182_()).m_82541_().m_82526_(aim) < 0.45) continue;
            t2.m_6469_(m.m_269291_().m_269333_((LivingEntity)m), damage);
            if (effect == 49) {
                t2.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 50, 1));
                continue;
            }
            t2.m_147240_(0.7, m.m_20185_() - t2.m_20185_(), m.m_20189_() - t2.m_20189_());
        }
        w.m_8767_((ParticleOptions)(effect == 49 ? ParticleTypes.f_175821_ : ParticleTypes.f_123758_), m.m_20185_(), m.m_20186_() + 0.7, m.m_20189_(), 18, 1.0, 0.6, 1.0, 0.02);
        return true;
    }

    private CrystalCombat() {
    }
}

