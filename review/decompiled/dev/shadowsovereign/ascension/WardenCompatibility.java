/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Unit
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.monster.warden.Warden
 */
package dev.shadowsovereign.ascension;

import dev.shadowsovereign.army.ShadowService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;

public final class WardenCompatibility {
    public static void attach(Mob mob) {
        if (!(mob instanceof Warden)) {
            return;
        }
        Warden w = (Warden)mob;
        if (w.m_20089_() == Pose.DYING || w.m_20089_() == Pose.DIGGING || w.m_20089_() == Pose.EMERGING) {
            w.m_20124_(Pose.STANDING);
        }
        w.m_6274_().m_21879_(MemoryModuleType.f_217770_, (Object)Unit.INSTANCE);
        w.m_6274_().m_21936_(MemoryModuleType.f_217782_);
        ServerPlayer owner = ShadowService.owner((Entity)w);
        if (owner != null) {
            w.m_219428_((Entity)owner);
        }
    }

    public static void tick(Mob mob) {
        if (mob instanceof Warden) {
            ServerPlayer owner;
            Warden w = (Warden)mob;
            w.m_6274_().m_21879_(MemoryModuleType.f_217770_, (Object)Unit.INSTANCE);
            if (w.m_20089_() == Pose.DIGGING) {
                w.m_20124_(Pose.STANDING);
            }
            if ((owner = ShadowService.owner((Entity)w)) != null) {
                w.m_219428_((Entity)owner);
                w.m_219449_().m_219256_().filter(e -> !ShadowService.canAttack(owner, e)).ifPresent(arg_0 -> ((Warden)w).m_219428_(arg_0));
                w.m_6274_().m_21952_(MemoryModuleType.f_217782_).filter(e -> !ShadowService.canAttack(owner, e)).ifPresent(e -> {
                    w.m_6274_().m_21936_(MemoryModuleType.f_217782_);
                    if (w.m_20089_() == Pose.ROARING) {
                        w.m_20124_(Pose.STANDING);
                    }
                });
            }
        }
    }
}

