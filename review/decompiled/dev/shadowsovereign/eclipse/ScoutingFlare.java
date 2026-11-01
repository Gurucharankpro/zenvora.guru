/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.projectile.ThrowableItemProjectile
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.HitResult
 *  net.minecraftforge.network.NetworkHooks
 */
package dev.shadowsovereign.eclipse;

import dev.shadowsovereign.eclipse.EclipseCamp;
import dev.shadowsovereign.eclipse.EclipseContent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public final class ScoutingFlare
extends ThrowableItemProjectile {
    public ScoutingFlare(EntityType<? extends ScoutingFlare> type, Level level) {
        super(type, level);
    }

    public ScoutingFlare(Level level, LivingEntity owner) {
        super((EntityType)EclipseContent.FLARE_ENTITY.get(), owner, level);
    }

    protected Item m_7881_() {
        return (Item)EclipseContent.FLARE.get();
    }

    protected void m_6532_(HitResult hit) {
        super.m_6532_(hit);
        if (!this.m_9236_().f_46443_) {
            Entity entity = this.m_19749_();
            if (entity instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)entity;
                if (p.f_8924_.m_6846_().m_11259_(p.m_20148_()) == p && p.m_6084_() && p.m_9236_() == this.m_9236_() && p.m_20238_(this.m_20182_()) <= 4096.0) {
                    EclipseCamp.reveal(p, hit.m_82450_());
                }
            }
            this.m_146870_();
        }
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.f_19797_ > 100) {
            this.m_146870_();
        }
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }
}

