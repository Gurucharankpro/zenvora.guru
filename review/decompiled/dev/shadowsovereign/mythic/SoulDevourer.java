/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.EvokerFangs
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mythic.MythicLair;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.MythicSites;
import dev.shadowsovereign.mythic.MythicSpawn;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SoulDevourer
extends Monster {
    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.m_135353_(SoulDevourer.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Long> START = SynchedEntityData.m_135353_(SoulDevourer.class, (EntityDataSerializer)EntityDataSerializers.f_244073_);
    public static final String[] CLIPS = new String[]{"idle_breathing", "emerge_from_ground", "summoning_complete", "claw_attack_left", "claw_attack_right", "double_claw_attack", "horn_charge", "ground_slam", "tail_sweep", "bite_attack", "spectral_roar", "cage_energy_blast", "soul_absorption", "damaged_light", "enraged_transformation", "defensive_stance"};
    private static final int[] LENGTH = new int[]{0, 160, 60, 28, 28, 36, 48, 44, 40, 32, 48, 44, 56, 12, 90, 24};
    private UUID victim;
    private boolean hit;
    private boolean enraged;
    private int cooldown;

    public SoulDevourer(EntityType<? extends SoulDevourer> type, Level world) {
        super(type, world);
        this.f_21364_ = 35;
        this.m_21530_();
    }

    public static AttributeSupplier.Builder attributes() {
        return Monster.m_33035_().m_22268_(Attributes.f_22276_, 300.0).m_22268_(Attributes.f_22281_, 12.0).m_22268_(Attributes.f_22284_, 6.0).m_22268_(Attributes.f_22279_, 0.24).m_22268_(Attributes.f_22277_, 40.0).m_22268_(Attributes.f_22278_, 0.7);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(ACTION, (Object)0);
        this.f_19804_.m_135372_(START, (Object)0L);
    }

    public int action() {
        return (Integer)this.f_19804_.m_135370_(ACTION);
    }

    public long actionStart() {
        return (Long)this.f_19804_.m_135370_(START);
    }

    public float actionAge(float partial) {
        return Math.max(0.0f, ((float)(this.m_9236_().m_46467_() - this.actionStart()) + partial) / 20.0f) * 1.6f;
    }

    public void animate(int id) {
        if (this.m_9236_().f_46443_) {
            return;
        }
        this.f_19804_.m_135381_(ACTION, (Object)Math.max(0, Math.min(CLIPS.length - 1, id)));
        this.f_19804_.m_135381_(START, (Object)this.m_9236_().m_46467_());
        this.hit = false;
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(2, (Goal)new DevourerCombat());
        this.f_21345_.m_25352_(7, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.65));
        this.f_21345_.m_25352_(8, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 12.0f));
        this.f_21345_.m_25352_(9, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]){

            public boolean m_8036_() {
                return !ShadowService.isShadow((Entity)SoulDevourer.this) && !SoulDevourer.this.getPersistentData().m_128403_("SoulboundMythicHost") && super.m_8036_();
            }
        });
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal<Player>((Mob)this, Player.class, true){

            public boolean m_8036_() {
                return !ShadowService.isShadow((Entity)SoulDevourer.this) && !SoulDevourer.this.getPersistentData().m_128403_("SoulboundMythicHost") && super.m_8036_();
            }
        });
    }

    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.m_9236_().f_46443_ && ShadowService.isShadow((Entity)this)) {
            this.animate(2);
        }
    }

    public boolean m_7327_(Entity target) {
        LivingEntity living;
        if (!(target instanceof LivingEntity) || !this.allowed(living = (LivingEntity)target) || this.action() >= 3 && this.action() <= 12) {
            return false;
        }
        this.victim = target.m_20148_();
        ServerPlayer owner = ShadowService.owner((Entity)this);
        ShadowRecord record = ShadowService.record((Entity)this);
        boolean circle = owner != null && record != null && VigilCrown.member(owner, record);
        this.animate(circle ? 3 : (new int[]{3, 4, 5, 7, 8, 9, 10, 11})[this.f_19796_.m_188503_(8)]);
        this.cooldown = 3;
        return true;
    }

    public boolean allowed(LivingEntity target) {
        if (!target.m_6084_() || target == this || target.m_9236_() != this.m_9236_()) {
            return false;
        }
        if (ShadowService.isShadow((Entity)this)) {
            ServerPlayer p = ShadowService.owner((Entity)this);
            return p != null && ShadowService.canAttack(p, target);
        }
        if (!this.getPersistentData().m_128403_("SoulboundMythicHost")) {
            Player p;
            return target instanceof Player && !(p = (Player)target).m_7500_() && !p.m_5833_() || target == this.m_21188_();
        }
        ServerPlayer host = MythicService.host((Entity)this);
        return host != null && (target == host || host.m_20148_().equals(ShadowService.ownerId((Entity)target)));
    }

    public boolean m_7307_(Entity e) {
        ServerPlayer p;
        if (ShadowService.isShadow((Entity)this) && (p = ShadowService.owner((Entity)this)) != null && ShadowService.allied(p, e)) {
            return true;
        }
        return super.m_7307_(e);
    }

    public boolean m_6469_(DamageSource source, float amount) {
        boolean damaged;
        if (!this.m_9236_().f_46443_ && !ShadowService.isShadow((Entity)this) && this.getPersistentData().m_128403_("SoulboundMythicHost")) {
            LivingEntity living;
            if (this.action() == 1) {
                return false;
            }
            Entity attacker = source.m_7639_();
            if (attacker instanceof LivingEntity && !this.allowed(living = (LivingEntity)attacker)) {
                return false;
            }
        }
        if ((damaged = super.m_6469_(source, amount)) && !this.m_9236_().f_46443_ && this.m_6084_() && this.action() == 0) {
            this.animate(13);
        }
        return damaged;
    }

    public boolean m_6673_(DamageSource d) {
        return d.m_276093_(DamageTypes.f_268612_) || d.m_276093_(DamageTypes.f_268671_) || d.m_276093_(DamageTypes.f_268722_) || d.m_276093_(DamageTypes.f_268546_) || super.m_6673_(d);
    }

    public AABB m_6921_() {
        return super.m_6921_().m_82377_(2.0, 0.8, 2.0);
    }

    public void m_6043_() {
    }

    protected void m_6153_() {
        ++this.f_20919_;
        if (this.f_20919_ >= 80 && !this.m_9236_().f_46443_) {
            this.m_142687_(Entity.RemovalReason.KILLED);
        }
    }

    public void m_7380_(CompoundTag n) {
        super.m_7380_(n);
        n.m_128379_("DevourerEnraged", this.enraged);
    }

    public void m_7378_(CompoundTag n) {
        super.m_7378_(n);
        this.enraged = n.m_128471_("DevourerEnraged");
        if (!this.m_9236_().f_46443_) {
            this.f_19804_.m_135381_(ACTION, (Object)0);
            this.f_19804_.m_135381_(START, (Object)this.m_9236_().m_46467_());
        }
        this.victim = null;
        this.hit = false;
        this.cooldown = 3;
    }

    public void m_8107_() {
        ServerPlayer p;
        super.m_8107_();
        if (this.m_9236_().f_46443_ || !this.m_6084_()) {
            return;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        int a = this.action();
        int age = (int)((double)(this.m_9236_().m_46467_() - this.actionStart()) * 1.6);
        if (ShadowService.isShadow((Entity)this) && (a == 1 || a == 2) && this.m_5448_() != null && age >= 16) {
            this.f_19804_.m_135381_(ACTION, (Object)0);
            this.victim = null;
            return;
        }
        if (!ShadowService.isShadow((Entity)this) && !this.enraged && this.m_21223_() < this.m_21233_() / 2.0f) {
            this.enraged = true;
            this.animate(14);
            this.m_5496_(SoundEvents.f_215769_, 0.8f, 0.7f);
            return;
        }
        if (a >= 3 && a <= 12 && !this.hit && age >= (a == 6 ? 24 : (a == 7 ? 22 : 12))) {
            this.hit = true;
            this.perform(a);
        }
        if (a != 0 && age >= LENGTH[a]) {
            this.f_19804_.m_135381_(ACTION, (Object)0);
            this.victim = null;
        }
        if (!ShadowService.isShadow((Entity)this) && this.getPersistentData().m_128403_("SoulboundMythicHost") && (p = MythicService.host((Entity)this)) != null && this.m_20186_() < (double)(MythicLair.center(p).m_123342_() - 10)) {
            BlockPos at = MythicLair.center(p).m_7494_();
            if (MythicSpawn.place(p.m_284548_(), (Mob)this, at, MythicSites.bounds(MythicService.state((ServerPlayer)p).gloom, at))) {
                this.m_20256_(Vec3.f_82478_);
                this.f_19789_ = 0.0f;
            }
        }
    }

    private void perform(int a) {
        LivingEntity living;
        Entity target;
        if (ShadowService.isShadow((Entity)this)) {
            ServerPlayer p = ShadowService.owner((Entity)this);
            ShadowRecord r = ShadowService.record((Entity)this);
            if (p != null && r != null && VigilCrown.member(p, r) && a != 3 && a != 4 && a != 9) {
                return;
            }
        }
        Entity entity = target = this.victim == null ? null : ((ServerLevel)this.m_9236_()).m_8791_(this.victim);
        if (!(target instanceof LivingEntity) || !this.allowed(living = (LivingEntity)target)) {
            return;
        }
        if (a == 11) {
            Vec3 direction = living.m_20182_().m_82546_(this.m_20182_()).m_82541_();
            for (int i = 1; i <= 5; ++i) {
                Vec3 pos = this.m_20182_().m_82549_(direction.m_82490_((double)i));
                EvokerFangs f = new EvokerFangs(this.m_9236_(), pos.f_82479_, this.m_20186_(), pos.f_82481_, this.m_146908_(), i * 2, (LivingEntity)this);
                f.getPersistentData().m_128362_("MythicCaster", this.m_20148_());
                if (!ShadowService.isShadow((Entity)this) && this.getPersistentData().m_128403_("SoulboundMythicHost")) {
                    f.getPersistentData().m_128362_("SoulboundMythicHost", this.getPersistentData().m_128342_("SoulboundMythicHost"));
                }
                this.m_9236_().m_7967_((Entity)f);
            }
            this.m_5496_(SoundEvents.f_11862_, 0.8f, 0.65f);
            return;
        }
        if (a == 7 || a == 8 || a == 10) {
            for (LivingEntity e : this.m_9236_().m_6443_(LivingEntity.class, this.m_20191_().m_82400_(4.0), this::allowed)) {
                if (!this.m_142582_((Entity)e)) continue;
                e.m_6469_(this.m_269291_().m_269333_((LivingEntity)this), (float)this.m_21133_(Attributes.f_22281_) * (a == 10 ? 0.5f : 0.8333333f));
                e.m_147240_(0.4, this.m_20185_() - e.m_20185_(), this.m_20189_() - e.m_20189_());
            }
            Level level = this.m_9236_();
            if (level instanceof ServerLevel) {
                ServerLevel w = (ServerLevel)level;
                w.m_8767_((ParticleOptions)ParticleTypes.f_123746_, this.m_20185_(), this.m_20186_() + 0.2, this.m_20189_(), 35, 2.0, 0.25, 2.0, 0.03);
            }
            this.m_5496_(SoundEvents.f_215771_, 0.6f, 0.65f);
            return;
        }
        if (this.m_20280_((Entity)living) <= 25.0 && this.m_142582_((Entity)living)) {
            super.m_7327_((Entity)living);
        }
    }

    private final class DevourerCombat
    extends Goal {
        DevourerCombat() {
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            if (SoulDevourer.this.m_5448_() == null || !SoulDevourer.this.allowed(SoulDevourer.this.m_5448_())) {
                return false;
            }
            if (ShadowService.isShadow((Entity)SoulDevourer.this)) {
                ServerPlayer p = ShadowService.owner((Entity)SoulDevourer.this);
                ShadowRecord r = ShadowService.record((Entity)SoulDevourer.this);
                return p != null && r != null && !VigilCrown.member(p, r) && (r.order != Order.STAY || OdysseyRuns.fighting(p, r));
            }
            return true;
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            LivingEntity t = SoulDevourer.this.m_5448_();
            if (t == null) {
                return;
            }
            SoulDevourer.this.m_21563_().m_24960_((Entity)t, 25.0f, 25.0f);
            if (SoulDevourer.this.action() != 0) {
                SoulDevourer.this.m_21573_().m_26573_();
                if (SoulDevourer.this.action() == 6 && SoulDevourer.this.m_9236_().m_46467_() - SoulDevourer.this.actionStart() > 12L && SoulDevourer.this.m_20280_((Entity)t) > 9.0) {
                    SoulDevourer.this.m_21573_().m_5624_((Entity)t, 1.7);
                }
                return;
            }
            double d = SoulDevourer.this.m_20280_((Entity)t);
            if (SoulDevourer.this.cooldown > 0) {
                if (d > 12.0) {
                    SoulDevourer.this.m_21573_().m_5624_((Entity)t, 1.0);
                }
                return;
            }
            if (d > 100.0) {
                SoulDevourer.this.m_21573_().m_5624_((Entity)t, 1.1);
                return;
            }
            SoulDevourer.this.victim = t.m_20148_();
            int choice = SoulDevourer.this.f_19796_.m_188503_(7);
            SoulDevourer.this.animate(d > 25.0 ? (choice < 4 ? 6 : 11) : (choice < 2 ? 3 : (choice == 2 ? 4 : (choice == 3 ? 7 : (choice == 4 ? 8 : (choice == 5 ? 9 : 10))))));
            SoulDevourer.this.cooldown = 3;
            SoulDevourer.this.m_21573_().m_26573_();
        }

        public void m_8041_() {
            SoulDevourer.this.m_21573_().m_26573_();
        }
    }
}

