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
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.control.FlyingMoveControl
 *  net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
 *  net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.expansion.CompanionFloatGoal;
import dev.shadowsovereign.expansion.OpenMythics;
import dev.shadowsovereign.expansion.X1Hooks;
import dev.shadowsovereign.mythic.LegendCombat;
import dev.shadowsovereign.mythic.MythicProfiles;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.odyssey.OdysseyRealm;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class MythicBeast
extends Monster {
    private static final EntityDataAccessor<Integer> COMPANION_FLAGS = SynchedEntityData.m_135353_(MythicBeast.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.m_135353_(MythicBeast.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Long> START = SynchedEntityData.m_135353_(MythicBeast.class, (EntityDataSerializer)EntityDataSerializers.f_244073_);
    private static final EntityDataAccessor<Float> STAMINA = SynchedEntityData.m_135353_(MythicBeast.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    public static final double[] HEALTH = MythicProfiles.ALL.stream().mapToDouble(MythicProfiles.Profile::health).toArray();
    public static final double[] DAMAGE = MythicProfiles.ALL.stream().mapToDouble(MythicProfiles.Profile::damage).toArray();
    public static final double[] ARMOR = MythicProfiles.ALL.stream().mapToDouble(MythicProfiles.Profile::armor).toArray();
    public static final float[] WIDTH = new float[HEALTH.length];
    public static final float[] HEIGHT = new float[HEALTH.length];
    public static final float[] RENDER_SCALE = new float[HEALTH.length];
    private final int kind;
    private UUID victim;
    private boolean hit;
    private int cooldown;
    private int flightTicks;
    private int vertical;
    private int sequence;
    private int specialSequence;
    private boolean boost;
    private double takeoffY;
    private long specialAt;
    private Vec3 gravityCenter;
    private int gravityTicks;

    public MythicBeast(EntityType<? extends MythicBeast> t, Level w, int k) {
        super(t, w);
        this.kind = k;
        this.f_21364_ = 35;
        this.m_21530_();
        if (this.profile().flies()) {
            this.f_21342_ = new FlyingMoveControl((Mob)this, 20, true);
            this.f_21344_ = new FlyingPathNavigation((Mob)this, w);
            this.m_20242_(true);
        }
        this.expansion$configureWaterNavigation();
    }

    public int kind() {
        return this.kind;
    }

    public MythicProfiles.Profile profile() {
        return MythicProfiles.get(this.kind);
    }

    public static AttributeSupplier.Builder attributes(int k) {
        MythicProfiles.Profile p = MythicProfiles.get(k);
        return Monster.m_33035_().m_22268_(Attributes.f_22276_, p.health()).m_22268_(Attributes.f_22281_, p.damage()).m_22268_(Attributes.f_22284_, p.armor()).m_22268_(Attributes.f_22279_, p.speed()).m_22268_(Attributes.f_22280_, 0.4).m_22268_(Attributes.f_22277_, 40.0).m_22268_(Attributes.f_22278_, 0.65);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(COMPANION_FLAGS, (Object)0);
        this.f_19804_.m_135372_(ACTION, (Object)0);
        this.f_19804_.m_135372_(START, (Object)0L);
        this.f_19804_.m_135372_(STAMINA, (Object)Float.valueOf(100.0f));
    }

    public int companionFlags() {
        return (Integer)this.f_19804_.m_135370_(COMPANION_FLAGS);
    }

    public int action() {
        return (Integer)this.f_19804_.m_135370_(ACTION);
    }

    public float stamina() {
        return ((Float)this.f_19804_.m_135370_(STAMINA)).floatValue();
    }

    public float animationRate() {
        if (this.action() == 1 || this.action() == 20 || this.action() == 22) {
            return 1.8f;
        }
        if (this.action() == 21) {
            return 3.0f;
        }
        MythicProfiles.Move move = this.currentMove();
        if (move == null) {
            return 1.0f;
        }
        return Math.max(1.6f, MythicProfiles.length(this.profile().id(), move.clip()) / (this.action() == 2 ? 1.0f : 2.1f));
    }

    public float actionAge(float partial) {
        return Math.max(0.0f, ((float)(this.m_9236_().m_46467_() - (Long)this.f_19804_.m_135370_(START)) + partial) / 20.0f) * this.animationRate();
    }

    public String idleClip() {
        if (this.kind == Integer.MIN_VALUE) {
            ShadowRecord r = ShadowService.record((Entity)this);
            return r != null && r.order == Order.STAY ? "sitting" : "idle";
        }
        if (this.kind == 8 && this.m_5448_() == null && this.f_19797_ % 240 > 160) {
            return "look_around";
        }
        return this.kind < 3 || this.kind == 18 ? "idle_breathing" : "idle";
    }

    public String clip() {
        if (this.kind == Integer.MIN_VALUE && this.action() == 23) {
            return "dragon_breath_enchanting";
        }
        if (this.kind == Integer.MIN_VALUE && this.action() == 24) {
            return "resurrecting";
        }
        return this.action() == 20 ? "take_damage" : (this.action() == 21 ? (this.kind == 10 ? "chakra_rod_ready" : "hammer_ready") : (this.action() == 22 ? "takeoff" : (this.action() == 1 ? "summoned_arrival" : (this.currentMove() != null ? this.currentMove().clip() : this.idleClip()))));
    }

    public String motion(boolean fast) {
        if (this.profile().flies() && !this.m_20096_()) {
            return this.m_20184_().m_165925_() < 0.015 && MythicProfiles.has(this.profile().id(), "glide") ? "glide" : "fly";
        }
        return fast ? this.profile().run() : this.profile().walk();
    }

    public String deathClip() {
        return this.kind == 2 ? "death_wither" : "death";
    }

    private MythicProfiles.Move currentMove() {
        int i = this.action() - 2;
        return i >= 0 && i < this.profile().moves().size() ? this.profile().moves().get(i) : null;
    }

    public void animate(int a) {
        if (this.m_9236_().f_46443_) {
            return;
        }
        this.f_19804_.m_135381_(ACTION, (Object)a);
        this.f_19804_.m_135381_(START, (Object)this.m_9236_().m_46467_());
        this.hit = false;
    }

    private boolean circle() {
        ServerPlayer p = ShadowService.owner((Entity)this);
        ShadowRecord r = ShadowService.record((Entity)this);
        return p != null && r != null && VigilCrown.member(p, r);
    }

    private boolean permittedMovement() {
        ServerPlayer p = ShadowService.owner((Entity)this);
        ShadowRecord r = ShadowService.record((Entity)this);
        return p != null && r != null && !this.circle() && (r.order != Order.STAY || OdysseyRuns.fighting(p, r));
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new CompanionFloatGoal((Mob)this));
        this.f_21345_.m_25352_(2, (Goal)new CombatGoal());
        this.f_21345_.m_25352_(7, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.7));
        this.f_21345_.m_25352_(8, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 12.0f));
        this.f_21345_.m_25352_(9, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]){

            public boolean m_8036_() {
                return !ShadowService.isShadow((Entity)MythicBeast.this) && !MythicBeast.this.getPersistentData().m_128403_("SoulboundMythicHost") && super.m_8036_();
            }
        });
    }

    public boolean allowed(LivingEntity t) {
        Player p;
        if (t == this || !t.m_6084_() || t.m_9236_() != this.m_9236_()) {
            return false;
        }
        if (ShadowService.isShadow((Entity)this)) {
            ServerPlayer p2 = ShadowService.owner((Entity)this);
            return p2 != null && ShadowService.canAttack(p2, t);
        }
        if (this.getPersistentData().m_128403_("SoulboundMythicHost")) {
            ServerPlayer p3 = MythicService.host((Entity)this);
            AdventureData.Session s = p3 == null ? null : OpenMythics.combatView(p3);
            return p3 != null && s != null && s.phase == AdventureData.Phase.ACTIVE && (t == p3 && !p3.m_5833_() || p3.m_20148_().equals(ShadowService.ownerId((Entity)t)) && ShadowService.record((Entity)t) != null && s.participants.contains(ShadowService.record((Entity)t).id));
        }
        if (this.kind == Integer.MIN_VALUE) {
            return false;
        }
        return t == this.m_21188_() || t instanceof Player && !(p = (Player)t).m_7500_() && !p.m_5833_();
    }

    public boolean m_6935_(Player p) {
        return this.kind == Integer.MIN_VALUE ? !ShadowService.isShadow((Entity)this) && this.m_5448_() == p : super.m_6935_(p);
    }

    public boolean m_7307_(Entity e) {
        ServerPlayer p = ShadowService.owner((Entity)this);
        return p != null && ShadowService.allied(p, e) || super.m_7307_(e);
    }

    public boolean m_7327_(Entity e) {
        LivingEntity t;
        if (!(e instanceof LivingEntity) || !this.allowed(t = (LivingEntity)e) || this.action() != 0 || this.cooldown > 0 || this.m_20160_()) {
            return false;
        }
        int move = 0;
        double d = this.m_20280_((Entity)t);
        if (!this.circle() && (this.sequence >= 2 || d > 25.0) && this.m_9236_().m_46467_() >= this.specialAt) {
            int count = this.profile().moves().size() - 1;
            for (int k = 0; k < count; ++k) {
                boolean eligible;
                int candidate = 1 + Math.floorMod(this.specialSequence + k, count);
                MythicProfiles.Move choice = this.profile().moves().get(candidate);
                boolean bl = choice.effect() == 5 || choice.effect() == 30 || choice.effect() == 35 ? (double)this.m_21223_() < (double)this.m_21233_() * 0.8 && d < 36.0 : (eligible = d <= (double)(choice.range() * choice.range()));
                if (!eligible) continue;
                move = candidate;
                this.specialSequence = candidate;
                this.specialAt = this.m_9236_().m_46467_() + 50L;
                this.sequence = 0;
                break;
            }
        }
        if (move == 0) {
            if (d > (double)(this.profile().moves().get(0).range() * this.profile().moves().get(0).range())) {
                return false;
            }
            ++this.sequence;
        }
        this.victim = t.m_20148_();
        this.animate(2 + move);
        this.cooldown = 3;
        return true;
    }

    public boolean m_6469_(DamageSource s, float amount) {
        boolean damaged;
        if (!this.m_9236_().f_46443_ && !ShadowService.isShadow((Entity)this) && this.getPersistentData().m_128403_("SoulboundMythicHost")) {
            LivingEntity l;
            if (this.action() == 1) {
                return false;
            }
            Entity entity = s.m_7639_();
            if (!(entity instanceof LivingEntity) || !this.allowed(l = (LivingEntity)entity)) {
                return false;
            }
        }
        if ((damaged = super.m_6469_(s, amount)) && !this.m_9236_().f_46443_ && this.m_6084_() && this.action() == 0 && MythicProfiles.has(this.profile().id(), "take_damage")) {
            this.animate(20);
        }
        return damaged;
    }

    public boolean m_6673_(DamageSource s) {
        return s.m_276093_(DamageTypes.f_268671_) || s.m_276093_(DamageTypes.f_268612_) || this.kind == 9 && (s.m_276093_(DamageTypes.f_268631_) || s.m_276093_(DamageTypes.f_268468_) || s.m_276093_(DamageTypes.f_268546_)) || super.m_6673_(s);
    }

    public void m_6043_() {
    }

    protected void m_6153_() {
        int n;
        if (this.kind < 13) {
            super.m_6153_();
            return;
        }
        ++this.f_20919_;
        if ((double)n >= Math.ceil(MythicProfiles.length(this.profile().id(), this.deathClip()) * 20.0f)) {
            this.f_20919_ = 19;
            super.m_6153_();
        }
    }

    public AABB m_6921_() {
        return super.m_6921_().m_82400_(this.kind == 27 ? 10.0 : (this.kind == 8 ? 6.0 : 4.0));
    }

    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.m_9236_().f_46443_ && ShadowService.isShadow((Entity)this)) {
            this.animate(1);
        }
    }

    public void m_8107_() {
        float end;
        this.expansion$restoreAquaticAir();
        super.m_8107_();
        if (this.m_9236_().f_46443_ || !this.m_6084_()) {
            return;
        }
        if (this.kind == Integer.MIN_VALUE) {
            this.f_19804_.m_135381_(COMPANION_FLAGS, (Object)0);
        }
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        if (this.gravityTicks > 0 && --this.gravityTicks % 5 == 0 && this.gravityCenter != null) {
            Level t2;
            for (Level t2 : this.m_9236_().m_6443_(LivingEntity.class, new AABB(this.gravityCenter, this.gravityCenter).m_82400_(8.0), this::allowed)) {
                if (!(t2.m_20182_().m_82557_(this.gravityCenter) < 64.0) || !this.m_142582_((Entity)t2)) continue;
                t2.m_20256_(t2.m_20184_().m_82490_(0.2).m_82549_(this.gravityCenter.m_82546_(t2.m_20182_()).m_82541_().m_82490_(0.32)));
                t2.f_19864_ = true;
            }
            t2 = this.m_9236_();
            if (t2 instanceof ServerLevel) {
                ServerLevel w = (ServerLevel)t2;
                w.m_8767_((ParticleOptions)ParticleTypes.f_123746_, this.gravityCenter.f_82479_, this.gravityCenter.f_82480_, this.gravityCenter.f_82481_, 4, 0.35, 0.35, 0.35, 0.01);
            }
        }
        if (this.profile().flies() && !this.m_20160_()) {
            this.m_20242_(true);
        }
        float age = this.actionAge(0.0f);
        MythicProfiles.Move move = this.currentMove();
        if (move != null && !this.hit && age >= move.impact()) {
            this.hit = true;
            this.strike(move);
        }
        float f = this.action() >= 20 ? MythicProfiles.length(this.profile().id(), this.clip()) : (this.action() == 1 ? MythicProfiles.length(this.profile().id(), "summoned_arrival") : (end = move != null ? MythicProfiles.length(this.profile().id(), move.clip()) : 0.0f));
        if (this.action() != 0 && (age >= end || this.action() == 1 && ShadowService.isShadow((Entity)this) && this.m_5448_() != null && this.m_9236_().m_46467_() - (Long)this.f_19804_.m_135370_(START) >= 10L)) {
            this.f_19804_.m_135381_(ACTION, (Object)0);
            this.victim = null;
        }
        if (this.m_20096_() && !this.m_20160_() && this.stamina() < 100.0f) {
            this.f_19804_.m_135381_(STAMINA, (Object)Float.valueOf(Math.min(100.0f, this.stamina() + 0.5f)));
        }
        if (!this.m_20160_() && !this.profile().flies() && this.m_20068_()) {
            this.m_20242_(false);
            this.flightTicks = 0;
        }
    }

    private void strike(MythicProfiles.Move move) {
        LivingEntity target;
        ServerLevel w;
        block21: {
            block20: {
                Level level = this.m_9236_();
                if (!(level instanceof ServerLevel)) break block20;
                w = (ServerLevel)level;
                if (this.victim != null && (level = w.m_8791_(this.victim)) instanceof LivingEntity && this.allowed(target = (LivingEntity)level) && (!this.circle() || this.action() == 2)) break block21;
            }
            return;
        }
        X1Hooks.attack(this, move);
        if (this.kind >= 13 && LegendCombat.strike(this, w, target, move)) {
            return;
        }
        if (move.effect() == 5) {
            this.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 30, 1));
            return;
        }
        if (move.effect() == 9) {
            this.gravityCenter = target.m_20182_().m_82520_(0.0, 1.5, 0.0);
            this.gravityTicks = 40;
        }
        float damage = (float)this.m_21133_(Attributes.f_22281_) * move.power();
        if (move.effect() == 0) {
            if (this.m_20280_((Entity)target) <= (double)(move.range() * move.range()) && this.m_142582_((Entity)target)) {
                if (move.power() == 1.0f) {
                    super.m_7327_((Entity)target);
                } else {
                    target.m_6469_(this.m_269291_().m_269333_((LivingEntity)this), damage);
                }
            }
            return;
        }
        block5: for (LivingEntity t : w.m_6443_(LivingEntity.class, this.m_20191_().m_82400_((double)move.range()), this::allowed)) {
            if (this.m_20280_((Entity)t) > (double)(move.range() * move.range()) || !this.m_142582_((Entity)t) || move.effect() == 4 && t.m_20182_().m_82546_(this.m_20182_()).m_82541_().m_82526_(target.m_20182_().m_82546_(this.m_20182_()).m_82541_()) < 0.65) continue;
            t.m_6469_(this.m_269291_().m_269333_((LivingEntity)this), damage);
            switch (move.effect()) {
                case 2: 
                case 9: {
                    t.m_20256_(t.m_20184_().m_82549_(this.m_20182_().m_82546_(t.m_20182_()).m_82541_().m_82490_(0.45)));
                    t.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 40, 0));
                    continue block5;
                }
                case 3: {
                    t.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 60, 1));
                    continue block5;
                }
                case 6: {
                    if (this.kind != 2) continue block5;
                    t.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 40, 0));
                    continue block5;
                }
            }
            t.m_147240_(0.65, this.m_20185_() - t.m_20185_(), this.m_20189_() - t.m_20189_());
        }
        if (move.effect() == 7) {
            this.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 40, 0));
        }
        if (move.effect() == 8) {
            this.m_7292_(new MobEffectInstance(MobEffects.f_19596_, 40, 0));
        }
        if (move.effect() == 4) {
            Vec3 from = this.m_146892_();
            Vec3 to = target.m_146892_();
            for (int i = 1; i <= 6; ++i) {
                Vec3 at = from.m_165921_(to, (double)i / 6.0);
                w.m_8767_((ParticleOptions)ParticleTypes.f_123744_, at.f_82479_, at.f_82480_, at.f_82481_, 2, 0.12, 0.12, 0.12, 0.01);
            }
        }
        w.m_8767_((ParticleOptions)(move.effect() == 4 ? ParticleTypes.f_123744_ : ParticleTypes.f_123746_), this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), 12, 0.6, 0.4, 0.6, 0.02);
    }

    protected InteractionResult m_6071_(Player user, InteractionHand hand) {
        ServerPlayer p;
        if (this.kind == Integer.MIN_VALUE) {
            InteractionResult result = null;
            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        if (this.profile().rideable() && user instanceof ServerPlayer && (p = (ServerPlayer)user).m_20148_().equals(ShadowService.ownerId((Entity)this)) && !p.m_36341_() && !this.m_20160_() && p.m_21205_().m_41619_() && AdventureService.journal((ServerPlayer)p).session == null && !VigilCrown.active(p)) {
            ShadowRecord r = ShadowService.record((Entity)this);
            if (r == null || VigilCrown.member(p, r)) {
                return InteractionResult.FAIL;
            }
            this.takeoffY = this.m_20186_();
            this.vertical = 0;
            this.m_21573_().m_26573_();
            this.m_6710_(null);
            return p.m_20329_((Entity)this) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return super.m_6071_(user, hand);
    }

    public LivingEntity m_6688_() {
        Player p;
        Entity entity;
        return this.profile().rideable() && (entity = this.m_146895_()) instanceof Player && (p = (Player)entity).m_20148_().equals(ShadowService.ownerId((Entity)this)) ? p : null;
    }

    public double m_6048_() {
        return (double)this.m_20206_() * 0.65;
    }

    public void flightInput(ServerPlayer p, int direction, boolean boosting) {
        if (!this.profile().rideable() || this.m_6688_() != p) {
            return;
        }
        this.vertical = Math.max(-1, Math.min(1, direction));
        this.boost = boosting;
        this.flightTicks = 10;
    }

    public boolean m_6109_() {
        return this.m_20160_() ? !this.m_9236_().f_46443_ : super.m_6109_();
    }

    public void m_7023_(Vec3 input) {
        double y;
        boolean air;
        ServerPlayer owner;
        LivingEntity livingEntity;
        if (!this.profile().rideable() || !((livingEntity = this.m_6688_()) instanceof Player)) {
            super.m_7023_(input);
            return;
        }
        Player p = (Player)livingEntity;
        if (this.m_9236_().f_46443_) {
            this.m_20256_(Vec3.f_82478_);
            return;
        }
        if (!(p instanceof ServerPlayer) || VigilCrown.active(owner = (ServerPlayer)p) || AdventureService.journal((ServerPlayer)owner).session != null) {
            this.m_20153_();
            this.m_20242_(false);
            return;
        }
        if (this.flightTicks-- <= 0) {
            this.vertical = 0;
            this.boost = false;
        }
        this.m_146922_(p.m_146908_());
        this.m_146926_(p.m_146909_() * 0.25f);
        this.f_20883_ = this.m_146908_();
        this.f_20885_ = this.m_146908_();
        this.m_21573_().m_26573_();
        this.m_6710_(null);
        if (this.kind == 9 && this.action() == 0 && this.m_20096_() && this.vertical > 0 && this.stamina() > 0.0f) {
            this.animate(22);
        }
        boolean bl = air = !this.m_20096_() || this.vertical > 0 && this.stamina() > 0.0f;
        if (air) {
            this.m_20242_(true);
            this.f_19804_.m_135381_(STAMINA, (Object)Float.valueOf(Math.max(0.0f, this.stamina() - (this.boost ? 0.2f : 0.1f))));
        } else {
            this.m_20242_(false);
            this.f_19804_.m_135381_(STAMINA, (Object)Float.valueOf(Math.min(100.0f, this.stamina() + 0.5f)));
            this.takeoffY = this.m_20186_();
        }
        double speed = this.boost && this.stamina() > 0.0f ? 0.55 : 0.35;
        double yaw = Math.toRadians(p.m_146908_());
        double x = (-Math.sin(yaw) * (double)p.f_20902_ + Math.cos(yaw) * (double)p.f_20900_) * speed;
        double z = (Math.cos(yaw) * (double)p.f_20902_ + Math.sin(yaw) * (double)p.f_20900_) * speed;
        double d = !air ? -0.04 : (y = this.stamina() <= 0.0f ? -0.12 : (double)this.vertical * 0.22);
        if (this.m_20186_() >= Math.min((double)(this.m_9236_().m_151558_() - 4), this.takeoffY + 48.0) && y > 0.0) {
            y = 0.0;
        }
        BlockPos next = this.m_20183_().m_7918_((int)Math.signum(x) * 2, 0, (int)Math.signum(z) * 2);
        if (!this.m_9236_().m_46805_(next) || !this.m_9236_().m_6857_().m_61937_(next)) {
            x = 0.0;
            z = 0.0;
        }
        if (OdysseyRealm.inside(owner) && OdysseyRealm.foreignPlot(owner, next)) {
            x = 0.0;
            z = 0.0;
        }
        this.m_20334_(x, y, z);
        this.m_6478_(MoverType.SELF, this.m_20184_());
        this.f_19789_ = 0.0f;
        this.m_267651_(false);
    }

    protected SoundEvent m_7515_() {
        return this.kind == Integer.MIN_VALUE ? null : super.m_7515_();
    }

    protected SoundEvent m_5592_() {
        return this.kind == Integer.MIN_VALUE ? null : super.m_5592_();
    }

    public void m_7380_(CompoundTag n) {
        super.m_7380_(n);
        n.m_128350_("MythicStamina", this.stamina());
    }

    public void m_7378_(CompoundTag n) {
        super.m_7378_(n);
        this.f_19804_.m_135381_(STAMINA, (Object)Float.valueOf(n.m_128441_("MythicStamina") ? Math.max(0.0f, Math.min(100.0f, n.m_128457_("MythicStamina"))) : 100.0f));
        this.f_19804_.m_135381_(ACTION, (Object)0);
        this.victim = null;
        this.gravityCenter = null;
        this.gravityTicks = 0;
        this.m_20242_(this.profile().flies());
    }

    static {
        for (int k = 0; k < HEALTH.length; ++k) {
            MythicProfiles.Profile p = MythicProfiles.get(k);
            MythicBeast.WIDTH[k] = p.width();
            MythicBeast.HEIGHT[k] = p.height();
            MythicBeast.RENDER_SCALE[k] = p.scale();
        }
    }

    private void expansion$restoreAquaticAir() {
        if (OpenMythics.isAquatic(this.kind())) {
            this.m_20301_(this.m_6062_());
        }
    }

    private void expansion$configureWaterNavigation() {
        if (OpenMythics.isAquatic(this.kind())) {
            this.f_21342_ = new SmoothSwimmingMoveControl((Mob)this, 85, 10, 0.1f, 0.5f, false);
            this.f_21344_ = new WaterBoundPathNavigation((Mob)this, this.m_9236_());
        }
    }

    private final class CombatGoal
    extends Goal {
        CombatGoal() {
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_183429_() {
            return true;
        }

        public boolean m_8036_() {
            return !MythicBeast.this.m_20160_() && MythicBeast.this.m_5448_() != null && MythicBeast.this.allowed(MythicBeast.this.m_5448_()) && (!ShadowService.isShadow((Entity)MythicBeast.this) || MythicBeast.this.permittedMovement());
        }

        public void m_8037_() {
            LivingEntity t = MythicBeast.this.m_5448_();
            if (t == null) {
                return;
            }
            MythicBeast.this.m_21563_().m_24960_((Entity)t, 30.0f, 30.0f);
            double d = MythicBeast.this.m_20280_((Entity)t);
            if (d > 10.0 && MythicBeast.this.f_19797_ % 5 == 0) {
                MythicBeast.this.m_21573_().m_26519_(t.m_20185_(), t.m_20186_() + (MythicBeast.this.profile().flies() ? 1.5 : 0.0), t.m_20189_(), 1.2);
            }
            if (MythicBeast.this.action() == 0 && MythicBeast.this.cooldown == 0 && MythicBeast.this.m_142582_((Entity)t) && d <= Math.max(16.0, MythicBeast.this.profile().moves().stream().mapToDouble(m -> m.range() * m.range()).max().orElse(16.0)) && (d <= (double)(MythicBeast.this.profile().moves().get(0).range() * MythicBeast.this.profile().moves().get(0).range()) || MythicBeast.this.sequence >= 2 || d > 25.0)) {
                MythicBeast.this.m_7327_((Entity)t);
            }
        }

        public void m_8056_() {
            if (MythicBeast.this.action() == 0 && (MythicBeast.this.kind == 10 || MythicBeast.this.kind == 12)) {
                MythicBeast.this.animate(21);
            }
        }

        public void m_8041_() {
            MythicBeast.this.m_21573_().m_26573_();
        }
    }
}

