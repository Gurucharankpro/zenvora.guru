/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.monster.RangedAttackMob
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.vigil;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilService;
import dev.shadowsovereign.vigil.VigilState;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.phys.Vec3;

public final class VigilCombat {
    public static boolean link(ServerPlayer p, UUID a, UUID b) {
        if (a == null || b == null || a.equals(b) || VigilService.record(p, a) == null || VigilService.record(p, b) == null) {
            return false;
        }
        VigilState v = VigilService.state(p);
        v.linkA = a;
        v.linkB = b;
        VigilService.dirty(p);
        return true;
    }

    public static boolean bash(ServerPlayer p, UUID id) {
        Mob m = VigilService.eligible(p, id);
        LivingEntity enemy = VigilService.aimMob(p, 16);
        if (!(m != null && enemy != null && ShadowService.canAttack(p, enemy) && m.m_142582_((Entity)enemy) && !(m.m_20280_((Entity)enemy) > 16.0) && VigilService.combatOrder(p, "bash", 12) && VigilService.melee(m, enemy))) {
            return false;
        }
        enemy.m_147240_(1.2, m.m_20185_() - enemy.m_20185_(), m.m_20189_() - enemy.m_20189_());
        enemy.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 40, 0));
        VigilService.spend(p, "bash", 12, 300);
        return true;
    }

    public static boolean volley(ServerPlayer p) {
        LivingEntity enemy = VigilService.aimMob(p, 24);
        if (enemy == null || !ShadowService.canAttack(p, enemy) || !VigilService.combatOrder(p, "volley", 25)) {
            return false;
        }
        int count = 0;
        for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            Mob m = VigilService.eligible(p, r.id);
            if (!(m instanceof RangedAttackMob)) continue;
            RangedAttackMob ranged = (RangedAttackMob)m;
            if (!m.m_142582_((Entity)enemy)) continue;
            for (int i = 0; i < 3; ++i) {
                ranged.m_6504_(enemy, 1.0f);
            }
            if (++count < 4) continue;
            break;
        }
        if (count == 0) {
            return false;
        }
        VigilService.spend(p, "volley", 25, 900);
        return true;
    }

    public static boolean meteor(ServerPlayer p, UUID id) {
        Mob m = VigilService.eligible(p, id);
        Vec3 at = EclipsePowers.aim(p, 16);
        if (m == null || m.m_20159_() || m.m_20160_() || at == null || !VigilService.state((ServerPlayer)p).meteor.m_128456_() || !VigilService.combatOrder(p, "meteor", 20)) {
            return false;
        }
        Vec3 safe = ShadowService.safePosition(m, at, p.m_284548_());
        if (safe == null || safe.m_82557_(at) > 16.0) {
            return false;
        }
        CompoundTag v = VigilService.state((ServerPlayer)p).meteor;
        v.m_128362_("Id", id);
        v.m_128356_("Until", VigilService.now(p) + 100L);
        v.m_128356_("Started", VigilService.now(p));
        v.m_128347_("X", safe.f_82479_);
        v.m_128347_("Y", safe.f_82480_);
        v.m_128347_("Z", safe.f_82481_);
        m.m_20256_(safe.m_82546_(m.m_20182_()).m_82490_(0.12).m_82520_(0.0, 0.65, 0.0));
        m.f_19864_ = true;
        VigilService.spend(p, "meteor", 20, 600);
        return true;
    }

    public static void tick(ServerPlayer p) {
        VigilState v = VigilService.state(p);
        CompoundTag n = v.meteor;
        if (n.m_128456_()) {
            return;
        }
        Mob m = VigilService.eligible(p, VigilState.id(n, "Id"));
        if (m == null || VigilCrown.active(p) || n.m_128454_("Until") <= VigilService.now(p)) {
            v.meteor = new CompoundTag();
            return;
        }
        if (VigilService.now(p) - n.m_128454_("Started") >= 10L && m.m_20096_()) {
            for (LivingEntity target : EclipsePowers.targets(p, m.m_20182_(), 3.0, 6)) {
                target.m_6469_(m.m_269291_().m_269333_((LivingEntity)m), 4.0f);
            }
            EclipsePowers.particles(p, m.m_20182_(), ParticleTypes.f_123745_, 20);
            v.meteor = new CompoundTag();
        }
    }
}

