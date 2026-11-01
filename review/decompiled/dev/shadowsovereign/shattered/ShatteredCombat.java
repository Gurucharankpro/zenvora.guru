/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.shattered;

import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicProfiles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public final class ShatteredCombat {
    public static boolean strike(MythicBeast m, ServerLevel w, LivingEntity target, MythicProfiles.Move move) {
        if (move.effect() != 47 && move.effect() != 48) {
            return false;
        }
        float damage = (float)m.m_21133_(Attributes.f_22281_) * move.power();
        if (move.effect() == 47) {
            if (!m.allowed(target) || m.m_20280_((Entity)target) > (double)(move.range() * move.range()) || !m.m_142582_((Entity)target)) {
                return true;
            }
            target.m_6469_(m.m_269291_().m_269333_((LivingEntity)m), damage);
            for (int i = 0; i < 12; ++i) {
                Vec3 p = m.m_146892_().m_165921_(target.m_146892_(), (double)i / 11.0);
                w.m_8767_((ParticleOptions)ParticleTypes.f_123810_, p.f_82479_, p.f_82480_, p.f_82481_, 1, 0.02, 0.02, 0.02, 0.0);
            }
            return true;
        }
        for (LivingEntity t : w.m_6443_(LivingEntity.class, m.m_20191_().m_82400_((double)move.range()), m::allowed)) {
            if (m.m_20280_((Entity)t) > (double)(move.range() * move.range()) || !m.m_142582_((Entity)t)) continue;
            t.m_6469_(m.m_269291_().m_269333_((LivingEntity)m), damage);
            t.m_147240_(0.5, m.m_20185_() - t.m_20185_(), m.m_20189_() - t.m_20189_());
        }
        w.m_8767_((ParticleOptions)ParticleTypes.f_175828_, m.m_20185_(), m.m_20186_() + 0.8, m.m_20189_(), 20, 1.4, 0.6, 1.4, 0.03);
        return true;
    }

    private ShatteredCombat() {
    }
}

