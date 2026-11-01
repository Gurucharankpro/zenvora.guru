/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.army;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.BattleState;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.soullife.SoulLifeService;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class BattleService {
    public static final String TAUNT_OWNER = "SoulboundTauntOwner";
    public static final String TAUNT_TANK = "SoulboundTauntTank";
    public static final String TAUNT_UNTIL = "SoulboundTauntUntil";

    private BattleService() {
    }

    public static List<ShadowRecord> records(ServerPlayer player, BattleRules.Squad scope, @Nullable UUID id) {
        ArmyData.Army army = ArmyData.get(player.f_8924_).army(player.m_20148_());
        if (id != null) {
            return army.shadows.containsKey(id) ? List.of(army.shadows.get(id)) : List.of();
        }
        return army.shadows.values().stream().filter(r -> scope == BattleRules.Squad.ALL || r.battle.squad == scope).toList();
    }

    private static void dirty(ServerPlayer p) {
        ArmyData.get(p.f_8924_).m_77762_();
    }

    public static void assign(ServerPlayer p, UUID id, BattleRules.Squad squad) {
        if (squad == BattleRules.Squad.ALL) {
            return;
        }
        for (ShadowRecord r : BattleService.records(p, BattleRules.Squad.ALL, id)) {
            r.battle.squad = squad;
        }
        BattleService.dirty(p);
    }

    public static int setFormation(ServerPlayer p, BattleRules.Squad scope, UUID id, BattleRules.Formation formation) {
        List<ShadowRecord> list = BattleService.records(p, scope, id);
        list.forEach(r -> {
            r.battle.formation = formation;
        });
        BattleService.dirty(p);
        return list.size();
    }

    public static int setPriority(ServerPlayer p, BattleRules.Squad scope, UUID id, BattleRules.Priority priority) {
        List<ShadowRecord> list = BattleService.records(p, scope, id);
        list.forEach(r -> {
            r.battle.priority = priority;
        });
        BattleService.dirty(p);
        return list.size();
    }

    public static int setRetreat(ServerPlayer p, BattleRules.Squad scope, UUID id, int percent) {
        if (percent != 0 && percent != 10 && percent != 25 && percent != 50) {
            return 0;
        }
        List<ShadowRecord> list = BattleService.records(p, scope, id);
        list.forEach(r -> {
            r.battle.retreat = percent;
        });
        BattleService.dirty(p);
        return list.size();
    }

    public static int addWaypoint(ServerPlayer p, BattleRules.Squad scope, UUID id) {
        Vec3 from = p.m_146892_();
        Vec3 to = from.m_82549_(p.m_20154_().m_82490_(64.0));
        BlockHitResult hit = p.m_9236_().m_45547_(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)p));
        if (hit.m_6662_() != HitResult.Type.BLOCK) {
            ShadowService.tell(p, "Look at loaded ground within 64 blocks to record a waypoint.");
            return 0;
        }
        return BattleService.addWaypoint(p, scope, id, hit.m_82425_().m_7494_());
    }

    public static int addWaypoint(ServerPlayer p, BattleRules.Squad scope, UUID id, BlockPos pos) {
        if (p.m_20238_(Vec3.m_82512_((Vec3i)pos)) > 4096.0 || !p.m_284548_().m_46805_(pos) || !p.m_9236_().m_6857_().m_61937_(pos)) {
            return 0;
        }
        String dimension = p.m_9236_().m_46472_().m_135782_().toString();
        int changed = 0;
        for (ShadowRecord r : BattleService.records(p, scope, id)) {
            BattleState b = r.battle;
            if (b.route.size() >= 8 || b.route.contains(pos) || !b.route.isEmpty() && !dimension.equals(b.routeDimension)) continue;
            b.routeDimension = dimension;
            b.route.add(pos.m_7949_());
            b.paused = false;
            ++changed;
        }
        if (changed == 0) {
            ShadowService.tell(p, "Route is full, belongs to another dimension, or no companions were selected. Clear it to start again.");
        }
        BattleService.dirty(p);
        return changed;
    }

    public static int clearRoute(ServerPlayer p, BattleRules.Squad scope, UUID id) {
        List<ShadowRecord> list = BattleService.records(p, scope, id);
        for (ShadowRecord r : list) {
            r.battle.route.clear();
            r.battle.waypoint = 0;
            r.battle.paused = false;
            if (r.order != Order.PATROL) continue;
            ShadowService.setOrder(p, r, Order.STAY);
        }
        BattleService.dirty(p);
        return list.size();
    }

    public static int startPatrol(ServerPlayer p, BattleRules.Squad scope, UUID id) {
        int changed = 0;
        for (ShadowRecord r : BattleService.records(p, scope, id)) {
            if (r.battle.route.size() < 2 || !r.battle.routeDimension.equals(p.m_9236_().m_46472_().m_135782_().toString())) continue;
            r.battle.waypoint = 0;
            r.battle.paused = false;
            r.battle.stuckSince = 0L;
            r.battle.failedWaypoints = 0;
            ShadowService.setOrder(p, r, Order.PATROL);
            ++changed;
        }
        if (changed == 0) {
            ShadowService.tell(p, "Patrol needs 2\u20138 waypoints in your current dimension.");
        }
        BattleService.dirty(p);
        return changed;
    }

    public static void tickPatrol(Mob mob, ShadowRecord r, ServerPlayer owner) {
        BattleState b = r.battle;
        long now = owner.f_8924_.m_129783_().m_46467_();
        if (b.route.size() < 2 || !b.routeDimension.equals(mob.m_9236_().m_46472_().m_135782_().toString())) {
            if (!b.paused) {
                ShadowService.tell(owner, r.name + ": patrol paused; check route dimension and waypoints.");
            }
            b.paused = true;
            mob.m_21573_().m_26573_();
            BattleService.dirty(owner);
            return;
        }
        if (b.paused) {
            mob.m_21573_().m_26573_();
            return;
        }
        b.waypoint = Math.floorMod(b.waypoint, b.route.size());
        BlockPos waypoint = b.route.get(b.waypoint);
        if (!mob.m_9236_().m_46805_(waypoint)) {
            mob.m_21573_().m_26573_();
            b.stuckSince = 0L;
            return;
        }
        r.anchor = mob.m_20183_();
        Vec3 destination = Vec3.m_82539_((Vec3i)waypoint);
        if (mob.m_20238_(destination) <= 6.25) {
            b.waypoint = (b.waypoint + 1) % b.route.size();
            b.stuckSince = 0L;
            b.failedWaypoints = 0;
            BattleService.dirty(owner);
            return;
        }
        if (b.stuckSince == 0L || b.lastProgress == null || mob.m_20182_().m_82557_(b.lastProgress) > 1.0) {
            b.stuckSince = now;
            b.lastProgress = mob.m_20182_();
        } else if (now - b.stuckSince >= 200L) {
            b.waypoint = (b.waypoint + 1) % b.route.size();
            b.stuckSince = 0L;
            ++b.failedWaypoints;
            if (b.failedWaypoints >= b.route.size()) {
                b.paused = true;
                mob.m_21573_().m_26573_();
                ShadowService.tell(owner, r.name + ": patrol paused because every waypoint was unreachable.");
            }
            BattleService.dirty(owner);
            return;
        }
        if (mob.f_19797_ % 10 == 0) {
            mob.m_21573_().m_26519_(destination.f_82479_, destination.f_82480_, destination.f_82481_, 1.05);
        }
    }

    public static Vec3 followPosition(Mob mob, ShadowRecord r, ServerPlayer owner) {
        Vec3 offset;
        Vec3 personal = SoulLifeService.personalSpace(mob, r, owner);
        if (personal != null) {
            return personal;
        }
        if (r.battle.formation == BattleRules.Formation.LOOSE) {
            return owner.m_20182_();
        }
        List<ShadowRecord> members = ArmyData.get((MinecraftServer)owner.f_8924_).army((UUID)owner.m_20148_()).shadows.values().stream().filter(s -> s.activeEntity != null && s.order == Order.FOLLOW && s.battle.squad == r.battle.squad && s.battle.formation == r.battle.formation).sorted(Comparator.comparing(s -> s.id.toString())).toList();
        int index = members.indexOf(r);
        int count = members.size();
        if (index < 0 || count == 0) {
            return owner.m_20182_();
        }
        double spacing = Math.max(3.0, (double)mob.m_20205_() * 1.5);
        for (ShadowRecord other : members) {
            Mob entity = ShadowService.active(owner.f_8924_, other);
            if (entity == null) continue;
            spacing = Math.max(spacing, (double)entity.m_20205_() * 1.5);
        }
        double side = 0.0;
        double back = 3.0;
        switch (r.battle.formation) {
            case LINE: {
                int cols = Math.min(8, count);
                side = ((double)(index % 8) - (double)(cols - 1) / 2.0) * spacing;
                back = 3.0 + (double)(index / 8) * spacing;
                break;
            }
            case WEDGE: {
                int rank = index / 2 + 1;
                side = (double)((index % 2 == 0 ? -1 : 1) * rank) * spacing;
                back = (double)rank * spacing;
                break;
            }
            case RING: {
                double angle = Math.PI * 2 * (double)index / (double)count;
                double radius = Math.max(3.0, (double)count * spacing / (Math.PI * 2));
                side = Math.cos(angle) * radius;
                back = Math.sin(angle) * radius;
                break;
            }
        }
        switch (r.battle.squad) {
            case ALPHA: {
                side -= 6.0;
                break;
            }
            case BETA: {
                side += 6.0;
                break;
            }
            case GAMMA: {
                back += 6.0;
                break;
            }
            case DELTA: {
                back -= 6.0;
                break;
            }
        }
        Vec3 facing = new Vec3(owner.m_20154_().f_82479_, 0.0, owner.m_20154_().f_82481_).m_82541_();
        if (facing.m_82556_() < 0.1) {
            facing = new Vec3(0.0, 0.0, 1.0);
        }
        if ((offset = new Vec3(facing.f_82481_, 0.0, -facing.f_82479_).m_82490_(side).m_82546_(facing.m_82490_(back))).m_82553_() > 24.0) {
            offset = offset.m_82541_().m_82490_(24.0);
        }
        return owner.m_20182_().m_82549_(offset);
    }

    @Nullable
    public static LivingEntity aimedEnemy(ServerPlayer p) {
        LivingEntity living;
        Entity entity;
        Vec3 to;
        Vec3 from = p.m_146892_();
        EntityHitResult hit = ProjectileUtil.m_37287_((Entity)p, (Vec3)from, (Vec3)(to = from.m_82549_(p.m_20154_().m_82490_(32.0))), (AABB)p.m_20191_().m_82369_(to.m_82546_(from)).m_82400_(1.0), e -> {
            LivingEntity living;
            return e instanceof LivingEntity && ShadowService.canAttack(p, living = (LivingEntity)e);
        }, (double)1024.0);
        return hit != null && (entity = hit.m_82443_()) instanceof LivingEntity && p.m_142582_((Entity)(living = (LivingEntity)entity)) ? living : null;
    }

    public static int cast(ServerPlayer p, BattleRules.Skill skill, BattleRules.Squad scope, UUID id) {
        long ready;
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        long now = p.f_8924_.m_129783_().m_46467_();
        if (now < (ready = army.readyAt.getOrDefault((Object)skill, 0L).longValue())) {
            ShadowService.tell(p, skill.label + " is ready in " + (ready - now + 19L) / 20L + "s.");
            return 0;
        }
        List<ShadowRecord> eligible = BattleService.records(p, scope, id).stream().filter(r -> r.level() >= skill.level).filter(r -> {
            Mob mob = ShadowService.active(p.f_8924_, r);
            return mob != null && mob.m_6084_() && mob.m_9236_() == p.m_9236_() && mob.m_20280_((Entity)p) <= 1024.0;
        }).toList();
        if (eligible.isEmpty()) {
            ShadowService.tell(p, skill.label + " needs an active level " + skill.level + "+ companion within 32 blocks.");
            return 0;
        }
        int affected = 0;
        switch (skill) {
            case STEP: {
                LivingEntity target = BattleService.aimedEnemy(p);
                if (target == null) {
                    ShadowService.tell(p, "Look directly at an enemy within 32 blocks.");
                    return 0;
                }
                for (ShadowRecord r2 : eligible) {
                    Mob mob = ShadowService.active(p.f_8924_, r2);
                    Vec3 safe = ShadowService.safePosition(mob, target.m_20182_(), p.m_284548_());
                    if (safe == null) continue;
                    ShadowService.particles(mob, 8);
                    mob.m_6021_(safe.f_82479_, safe.f_82480_, safe.f_82481_);
                    ShadowService.setOrder(p, r2, Order.FOLLOW);
                    r2.battle.commandedTarget = target.m_20148_();
                    mob.m_6710_(target);
                    ShadowService.soulParticles(mob);
                    ++affected;
                }
                break;
            }
            case TAUNT: {
                Mob chosen = eligible.stream().map(r -> ShadowService.active(p.f_8924_, r)).sorted(Comparator.comparingDouble(LivingEntity::m_21223_).reversed().thenComparing(m -> m.m_20148_().toString())).findFirst().orElseThrow();
                List<Mob> enemies = p.m_9236_().m_6443_(Mob.class, chosen.m_20191_().m_82400_(12.0), e -> e instanceof Enemy && ShadowService.canAttack(p, (LivingEntity)e) && e.m_20280_((Entity)chosen) <= 144.0).stream().sorted(Comparator.comparingDouble(arg_0 -> ((Mob)chosen).m_20280_(arg_0))).limit(8L).toList();
                for (Mob enemy : enemies) {
                    enemy.m_6710_((LivingEntity)chosen);
                    if (enemy.m_5448_() != chosen) continue;
                    enemy.getPersistentData().m_128362_(TAUNT_OWNER, p.m_20148_());
                    enemy.getPersistentData().m_128362_(TAUNT_TANK, chosen.m_20148_());
                    enemy.getPersistentData().m_128356_(TAUNT_UNTIL, now + 100L);
                    BattleService.setBrainTarget(enemy, (LivingEntity)chosen);
                    ++affected;
                }
                if (affected <= 0) break;
                ShadowService.soulParticles(chosen);
                break;
            }
            case BARRIER: {
                for (ShadowRecord r3 : eligible) {
                    if (r3.battle.shield > 0.0f && r3.battle.shieldUntil > now) continue;
                    r3.battle.shield = 4.0f;
                    r3.battle.shieldUntil = now + 160L;
                    ShadowService.soulParticles(ShadowService.active(p.f_8924_, r3));
                    ++affected;
                }
                break;
            }
            case MEND: {
                for (ShadowRecord r4 : eligible) {
                    Mob mob = ShadowService.active(p.f_8924_, r4);
                    float before = mob.m_21223_();
                    if (before >= mob.m_21233_()) continue;
                    mob.m_5634_(4.0f);
                    if (!(mob.m_21223_() > before)) continue;
                    r4.health = mob.m_21223_();
                    p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123750_, mob.m_20185_(), mob.m_20186_() + (double)mob.m_20206_(), mob.m_20189_(), 4, 0.3, 0.2, 0.3, 0.0);
                    ++affected;
                }
                break;
            }
        }
        if (affected > 0) {
            army.readyAt.put(skill, now + (long)skill.cooldown);
            BattleService.dirty(p);
            ShadowService.tell(p, skill.label + " activated (" + affected + ").");
        } else {
            ShadowService.tell(p, skill.label + ": no valid effect; cooldown was not spent.");
        }
        return affected;
    }

    public static float absorb(Mob mob, float amount) {
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (!ShadowService.valid(mob, r) || amount <= 0.0f) {
            return amount;
        }
        long now = mob.m_20194_().m_129783_().m_46467_();
        if (r.battle.shieldUntil <= now || r.battle.shield <= 0.0f) {
            return amount;
        }
        float taken = Math.min(amount, r.battle.shield);
        r.battle.shield -= taken;
        ArmyData.get(mob.m_20194_()).m_77762_();
        return amount - taken;
    }

    public static void tickOwner(ServerPlayer p) {
        long now = p.f_8924_.m_129783_().m_46467_();
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        for (ShadowRecord r : army.shadows.values()) {
            Mob mob;
            if (r.battle.shield > 0.0f && now >= r.battle.shieldUntil) {
                r.battle.shield = 0.0f;
                BattleService.dirty(p);
            }
            if (r.battle.retreat <= 0 || (mob = ShadowService.active(p.f_8924_, r)) == null || !mob.m_6084_() || !(mob.m_21223_() > 0.0f) || !(mob.m_21223_() / mob.m_21233_() * 100.0f <= (float)r.battle.retreat)) continue;
            ShadowService.recall(p, r);
            ShadowService.tell(p, r.name + " retreated to reserve at low health.");
        }
    }

    @Nullable
    public static Mob tauntTarget(Mob enemy) {
        Mob tank;
        CompoundTag n = enemy.getPersistentData();
        if (!n.m_128403_(TAUNT_TANK) || !n.m_128403_(TAUNT_OWNER) || enemy.m_20194_() == null || enemy.m_20194_().m_129783_().m_46467_() >= n.m_128454_(TAUNT_UNTIL)) {
            return null;
        }
        ServerPlayer owner = enemy.m_20194_().m_6846_().m_11259_(n.m_128342_(TAUNT_OWNER));
        Entity entity = ((ServerLevel)enemy.m_9236_()).m_8791_(n.m_128342_(TAUNT_TANK));
        return owner != null && entity instanceof Mob && (tank = (Mob)entity).m_6084_() && owner.m_20148_().equals(ShadowService.ownerId((Entity)tank)) && ShadowService.canAttack(owner, (LivingEntity)enemy) ? tank : null;
    }

    public static void tickTaunt(Mob enemy) {
        CompoundTag n = enemy.getPersistentData();
        if (!n.m_128403_(TAUNT_TANK)) {
            return;
        }
        Mob tank = BattleService.tauntTarget(enemy);
        if (tank == null) {
            UUID previous = n.m_128342_(TAUNT_TANK);
            n.m_128473_(TAUNT_TANK);
            n.m_128473_(TAUNT_OWNER);
            n.m_128473_(TAUNT_UNTIL);
            if (enemy.m_5448_() != null && enemy.m_5448_().m_20148_().equals(previous)) {
                enemy.m_6710_(null);
                BattleService.setBrainTarget(enemy, null);
            }
        } else if (enemy.f_19797_ % 10 == 0) {
            enemy.m_6710_((LivingEntity)tank);
            if (enemy.m_5448_() == tank) {
                BattleService.setBrainTarget(enemy, (LivingEntity)tank);
            }
        }
    }

    private static void setBrainTarget(Mob mob, LivingEntity target) {
        if (mob.m_6274_().m_147339_().containsKey(MemoryModuleType.f_26372_)) {
            if (target == null) {
                mob.m_6274_().m_21936_(MemoryModuleType.f_26372_);
            } else {
                mob.m_6274_().m_21879_(MemoryModuleType.f_26372_, (Object)target);
            }
        }
    }
}

