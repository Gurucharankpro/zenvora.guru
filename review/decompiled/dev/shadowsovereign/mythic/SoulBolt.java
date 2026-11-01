/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.projectile.ThrowableItemProjectile
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraftforge.network.NetworkHooks
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public final class SoulBolt
extends ThrowableItemProjectile {
    private float damage;
    private int life;

    public SoulBolt(EntityType<? extends SoulBolt> type, Level level) {
        super(type, level);
        this.m_20242_(true);
    }

    public SoulBolt(MythicBeast caster, float amount) {
        this((EntityType<? extends SoulBolt>)((EntityType)MythicContent.BOLT.get()), caster.m_9236_());
        this.m_5602_((Entity)caster);
        this.damage = amount;
        this.m_6034_(caster.m_20185_(), caster.m_20188_() - 0.15, caster.m_20189_());
    }

    protected Item m_7881_() {
        return Items.f_42516_;
    }

    protected float m_7139_() {
        return 0.0f;
    }

    public void m_8119_() {
        MythicBeast m;
        Entity entity;
        super.m_8119_();
        if (!(this.m_9236_().f_46443_ || ++this.life < 60 && (entity = this.m_19749_()) instanceof MythicBeast && (m = (MythicBeast)entity).m_6084_())) {
            this.m_146870_();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected boolean m_5603_(Entity target) {
        if (!super.m_5603_(target)) return false;
        if (!(target instanceof LivingEntity)) return false;
        LivingEntity t = (LivingEntity)target;
        Entity entity = this.m_19749_();
        if (!(entity instanceof MythicBeast)) return false;
        MythicBeast m = (MythicBeast)entity;
        if (!m.allowed(t)) return false;
        return true;
    }

    protected void m_5790_(EntityHitResult hit) {
        Entity entity;
        if (!this.m_9236_().f_46443_ && (entity = hit.m_82443_()) instanceof LivingEntity) {
            MythicBeast m;
            LivingEntity t = (LivingEntity)entity;
            entity = this.m_19749_();
            if (entity instanceof MythicBeast && (m = (MythicBeast)entity).allowed(t)) {
                t.m_6469_(this.m_269291_().m_269390_((Entity)this, (Entity)m), this.damage);
            }
        }
        this.m_146870_();
    }

    protected void m_8060_(BlockHitResult hit) {
        super.m_8060_(hit);
        this.m_146870_();
    }

    public void m_7380_(CompoundTag n) {
        super.m_7380_(n);
        n.m_128350_("LegendDamage", this.damage);
        n.m_128405_("LegendLife", this.life);
    }

    public void m_7378_(CompoundTag n) {
        super.m_7378_(n);
        float v = n.m_128457_("LegendDamage");
        this.damage = Float.isFinite(v) ? Mth.m_14036_((float)v, (float)0.0f, (float)2048.0f) : 0.0f;
        this.life = Mth.m_14045_((int)n.m_128451_("LegendLife"), (int)0, (int)60);
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }
}

