/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.expansion.CrystalCombat;
import dev.shadowsovereign.mastery.MasteryCombat;
import dev.shadowsovereign.moonveil.MoonveilCombat;
import dev.shadowsovereign.mythic.ChronicleCombat;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicProfiles;
import dev.shadowsovereign.mythic.SoulBolt;
import dev.shadowsovereign.shattered.ShatteredCombat;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

public final class LegendCombat {
    private static final ItemParticleOption PAPER = new ItemParticleOption(ParticleTypes.f_123752_, new ItemStack((ItemLike)Items.f_42516_));

    public static boolean strike(MythicBeast m, ServerLevel w, LivingEntity target, MythicProfiles.Move move) {
        int effect = move.effect();
        if (effect < 10) {
            return false;
        }
        if (effect >= 49) {
            return CrystalCombat.strike(m, w, target, move);
        }
        if (effect >= 47) {
            return ShatteredCombat.strike(m, w, target, move);
        }
        if (effect >= 39) {
            return MasteryCombat.strike(m, w, target, move);
        }
        if (effect >= 30) {
            return MoonveilCombat.strike(m, w, target, move);
        }
        if (effect >= 19) {
            return ChronicleCombat.strike(m, w, target, move);
        }
        float damage = (float)m.m_21133_(Attributes.f_22281_) * move.power();
        if (effect == 10 || effect == 13) {
            if (!m.m_142582_((Entity)target) || m.m_20280_((Entity)target) > (double)(move.range() * move.range())) {
                return true;
            }
            int count = effect == 13 || m.kind() == 13 ? 3 : 1;
            Vec3 aim = target.m_146892_().m_82546_(m.m_146892_());
            for (int i = 0; i < count; ++i) {
                SoulBolt bolt = new SoulBolt(m, damage);
                Vec3 dir = aim.m_82541_().m_82524_(((float)i - (float)(count - 1) / 2.0f) * 0.1f);
                bolt.m_6686_(dir.f_82479_, dir.f_82480_, dir.f_82481_, 1.05f, 0.0f);
                w.m_7967_((Entity)bolt);
            }
            m.m_5496_(SoundEvents.f_11713_, 0.7f, m.kind() == 14 ? 0.65f : 1.25f);
            return true;
        }
        if (effect == 18) {
            if (m.m_20280_((Entity)target) <= (double)(move.range() * move.range()) && m.m_142582_((Entity)target)) {
                target.m_6469_(m.m_269291_().m_269333_((LivingEntity)m), damage);
                LegendCombat.trail(w, m.m_146892_(), target.m_146892_(), ParticleTypes.f_123809_);
            }
            return true;
        }
        for (LivingEntity t : w.m_6443_(LivingEntity.class, m.m_20191_().m_82400_((double)move.range()), m::allowed)) {
            if (m.m_20280_((Entity)t) > (double)(move.range() * move.range()) || !m.m_142582_((Entity)t)) continue;
            t.m_6469_(m.m_269291_().m_269333_((LivingEntity)m), damage);
            switch (effect) {
                case 11: 
                case 16: {
                    t.m_147240_(effect == 16 ? 1.0 : 0.6, m.m_20185_() - t.m_20185_(), m.m_20189_() - t.m_20189_());
                    break;
                }
                case 12: {
                    t.m_7292_(new MobEffectInstance(MobEffects.f_19613_, 60, 0));
                    t.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 40, 0));
                    break;
                }
                case 15: {
                    t.m_147240_(0.7, m.m_20185_() - t.m_20185_(), m.m_20189_() - t.m_20189_());
                    t.m_7292_(new MobEffectInstance(MobEffects.f_19613_, 50, 0));
                    break;
                }
                case 17: {
                    t.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 60, 0));
                }
            }
        }
        if (effect == 11) {
            w.m_8767_((ParticleOptions)PAPER, m.m_20185_(), m.m_20186_() + 1.0, m.m_20189_(), 24, 2.0, 0.6, 2.0, 0.1);
        } else {
            w.m_8767_((ParticleOptions)(effect == 17 ? ParticleTypes.f_123771_ : ParticleTypes.f_123746_), m.m_20185_(), m.m_20186_() + 1.0, m.m_20189_(), 16, 1.0, 0.4, 1.0, 0.03);
        }
        m.m_5496_(effect == 16 ? SoundEvents.f_11862_ : (effect == 15 ? SoundEvents.f_11744_ : SoundEvents.f_11713_), 0.7f, 0.8f);
        return true;
    }

    private static void trail(ServerLevel w, Vec3 from, Vec3 to, SimpleParticleType type) {
        for (int i = 1; i <= 8; ++i) {
            Vec3 at = from.m_165921_(to, (double)i / 8.0);
            w.m_8767_((ParticleOptions)type, at.f_82479_, at.f_82480_, at.f_82481_, 1, 0.02, 0.02, 0.02, 0.0);
        }
    }

    private LegendCombat() {
    }
}

