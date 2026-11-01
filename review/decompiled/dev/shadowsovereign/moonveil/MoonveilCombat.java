/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.moonveil;

import dev.shadowsovereign.gloom.GloomRealm;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicProfiles;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.MythicSites;
import dev.shadowsovereign.mythic.MythicSpawn;
import dev.shadowsovereign.mythic.SoulBolt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

public final class MoonveilCombat {
    public static boolean strike(MythicBeast m, ServerLevel w, LivingEntity target, MythicProfiles.Move move) {
        int effect = move.effect();
        float damage = (float)m.m_21133_(Attributes.f_22281_) * move.power();
        if (effect == 30 || effect == 35) {
            m.m_7292_(new MobEffectInstance(MobEffects.f_19606_, effect == 35 ? 35 : 60, effect == 35 ? 2 : 0));
            if (effect == 35) {
                m.m_7292_(new MobEffectInstance(MobEffects.f_19596_, 40, 0));
            }
        } else if (effect == 36) {
            if (m.m_20280_((Entity)target) > (double)(move.range() * move.range()) || !m.m_142582_((Entity)target)) {
                return true;
            }
            Vec3 direction = target.m_20182_().m_82546_(m.m_20182_()).m_82541_();
            BlockPos at = target.m_20183_().m_7918_((int)Math.round(direction.f_82479_ * 2.0), 0, (int)Math.round(direction.f_82481_ * 2.0));
            BoundingBox bounds = null;
            ServerPlayer host = MythicService.host((Entity)m);
            if (host != null) {
                bounds = MythicSites.bounds(MythicExpedition.state(host, m.kind()), GloomRealm.habitat(host, m.kind()));
            }
            if (MythicSpawn.place(w, (Mob)m, at, bounds)) {
                m.m_20256_(Vec3.f_82478_);
                m.f_19789_ = 0.0f;
                m.m_21573_().m_26573_();
                m.animate(1);
            }
        } else if (effect == 31 || effect == 32 || effect == 33 || effect == 37) {
            if (!m.m_142582_((Entity)target) || m.m_20280_((Entity)target) > (double)(move.range() * move.range())) {
                return true;
            }
            int count = effect == 32 ? 3 : (effect == 33 ? 2 : 1);
            Vec3 aim = target.m_146892_().m_82546_(m.m_146892_()).m_82541_();
            for (int i = 0; i < count; ++i) {
                SoulBolt bolt = new SoulBolt(m, damage);
                bolt.m_37446_(new ItemStack((ItemLike)(effect == 37 ? Items.f_42613_ : Items.f_42461_)));
                Vec3 dir = aim.m_82524_(((float)i - (float)(count - 1) / 2.0f) * 0.12f);
                bolt.m_6686_(dir.f_82479_, dir.f_82480_, dir.f_82481_, effect == 32 ? 0.75f : 1.2f, 0.0f);
                w.m_7967_((Entity)bolt);
            }
        } else {
            if (effect != 34) {
                if (effect != 38) {
                    return false;
                }
            }
            for (LivingEntity enemy : w.m_6443_(LivingEntity.class, m.m_20191_().m_82400_((double)move.range()), m::allowed)) {
                if (m.m_20280_((Entity)enemy) > (double)(move.range() * move.range()) || !m.m_142582_((Entity)enemy)) continue;
                enemy.m_6469_(m.m_269291_().m_269333_((LivingEntity)m), damage);
                if (effect == 38) {
                    enemy.m_20256_(enemy.m_20184_().m_82549_(m.m_20182_().m_82546_(enemy.m_20182_()).m_82541_().m_82490_(0.5)));
                    enemy.f_19864_ = true;
                    enemy.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 45, 0));
                    continue;
                }
                enemy.m_147240_(0.8, m.m_20185_() - enemy.m_20185_(), m.m_20189_() - enemy.m_20189_());
            }
        }
        w.m_8767_((ParticleOptions)(effect == 34 ? ParticleTypes.f_123813_ : (effect == 35 || effect == 36 ? ParticleTypes.f_123760_ : ParticleTypes.f_123797_)), m.m_20185_(), m.m_20186_() + 1.0, m.m_20189_(), effect == 34 ? 3 : 16, 0.7, 0.5, 0.7, 0.025);
        m.m_5496_(effect == 34 ? SoundEvents.f_11913_ : (effect == 38 ? SoundEvents.f_11744_ : (effect == 36 ? SoundEvents.f_11852_ : SoundEvents.f_11862_)), 0.65f, 1.1f);
        return true;
    }
}

