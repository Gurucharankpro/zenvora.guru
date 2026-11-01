/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.vigil;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.ShadowSovereign;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class VigilPosts {
    public static void restore(ServerPlayer p, ShadowRecord r) {
        if (!r.vigil.post || r.vigil.suspended || r.pendingRespawn() || r.activeEntity != null || VigilCrown.active(p) || ArmyData.get(p.f_8924_).army(p.m_20148_()).activeCount() >= (long)((Integer)ShadowConfig.MAX_ACTIVE.get()).intValue()) {
            return;
        }
        ResourceLocation key = ResourceLocation.m_135820_((String)r.vigil.postDimension);
        if (key == null) {
            return;
        }
        ResourceKey dimension = ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)key);
        ServerLevel world = p.f_8924_.m_129880_(dimension);
        BlockPos at = BlockPos.m_122022_((long)r.vigil.postPos);
        if (world == null || !world.m_46805_(at)) {
            return;
        }
        ResourceLocation entityKey = ResourceLocation.m_135820_((String)r.type);
        if (entityKey == null) {
            return;
        }
        EntityType type = (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(entityKey);
        if (type == null) {
            return;
        }
        Mob m = null;
        try {
            Entity entity = type.m_20615_((Level)world);
            if (!(entity instanceof Mob)) {
                return;
            }
            Mob created = (Mob)entity;
            m = created;
            if (m instanceof Enemy && world.m_46791_() == Difficulty.PEACEFUL) {
                return;
            }
            m.m_20258_(r.snapshot.m_6426_());
            m.m_20084_(UUID.randomUUID());
            CompanionService.apply(m, r);
            CompanionService.equip(m, r);
            m.m_21153_(Math.max(1.0f, Math.min(r.health, m.m_21233_())));
            Vec3 safe = ShadowService.safePosition(m, Vec3.m_82539_((Vec3i)at), world);
            if (safe == null) {
                return;
            }
            m.m_6027_(safe.f_82479_, safe.f_82480_, safe.f_82481_);
            m.getPersistentData().m_128362_("ShadowSovereignOwner", p.m_20148_());
            m.getPersistentData().m_128362_("ShadowSovereignRecord", r.id);
            r.activeEntity = m.m_20148_();
            r.dimension = r.vigil.postDimension;
            r.anchor = at;
            ShadowService.initialize(m);
            if (!world.m_7967_((Entity)m)) {
                r.activeEntity = null;
            }
            ArmyData.get(p.f_8924_).m_77762_();
        }
        catch (RuntimeException e) {
            r.activeEntity = null;
            if (m != null) {
                m.m_146870_();
            }
            ShadowSovereign.LOGGER.warn("Could not restore sentry {}", (Object)r.type, (Object)e);
        }
    }
}

