/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.warfront;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseRoles;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.sovereign.SovereignService;
import dev.shadowsovereign.sovereign.SovereignState;
import dev.shadowsovereign.warfront.WarfrontActors;
import dev.shadowsovereign.warfront.WarfrontPlans;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class WarfrontBuilds {
    private static final UUID STRIDE = UUID.fromString("c3831030-61bd-4d4d-a109-000000000001");
    public static final UUID SUNDER = UUID.fromString("c3831030-61bd-4d4d-a109-000000000002");
    public static final String SUNDER_UNTIL = "SoulboundSunderUntil";

    private WarfrontBuilds() {
    }

    public static WarfrontState.Build build(Mob mob, ShadowRecord r) {
        return ArmyData.get((MinecraftServer)mob.m_20194_()).army((UUID)ShadowService.ownerId((Entity)mob)).warfront.build(r.id);
    }

    public static WarfrontState.Rune rune(Mob mob, ShadowRecord r) {
        WarfrontState.Build b = ArmyData.get((MinecraftServer)mob.m_20194_()).army((UUID)ShadowService.ownerId((Entity)mob)).warfront.builds.get(r.id);
        return b == null ? WarfrontState.Rune.NONE : b.rune;
    }

    public static boolean art(ServerPlayer p, UUID id, WarfrontState.Art art) {
        ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
        if (!(CompanionService.accessible(p, r) && WarfrontService.workshop(p) && (art == WarfrontState.Art.NONE || WarfrontService.state((ServerPlayer)p).wonOnce && r.bond >= 10))) {
            return false;
        }
        WarfrontService.state((ServerPlayer)p).build((UUID)id).art = art;
        WarfrontService.dirty(p);
        return true;
    }

    public static boolean rune(ServerPlayer p, UUID id, WarfrontState.Rune rune) {
        ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
        WarfrontState w = WarfrontService.state(p);
        if (!CompanionService.accessible(p, r) || !WarfrontService.workshop(p) || rune != WarfrontState.Rune.NONE && (w.blueprints & 1 << rune.ordinal()) == 0) {
            return false;
        }
        WarfrontState.Build b = w.build(id);
        int bit = 1 << rune.ordinal();
        if (rune != WarfrontState.Rune.NONE && (b.crafted & bit) == 0) {
            if (p.m_150109_().m_18947_(Items.f_151049_) < EclipseRoles.amethystCost(p, r) || p.m_150109_().m_18947_(Items.f_151052_) < 1) {
                WarfrontService.tell(p, "Crafting this companion's rune needs 8 amethyst shards and 1 copper ingot. Crafted runes switch freely.");
                return false;
            }
            int amethyst = EclipseRoles.amethystCost(p, r);
            int copper = 1;
            for (int i = 0; i < p.m_150109_().m_6643_(); ++i) {
                int count;
                ItemStack stack = p.m_150109_().m_8020_(i);
                if (stack.m_150930_(Items.f_151049_)) {
                    count = Math.min(amethyst, stack.m_41613_());
                    stack.m_41774_(count);
                    amethyst -= count;
                    continue;
                }
                if (!stack.m_150930_(Items.f_151052_)) continue;
                count = Math.min(copper, stack.m_41613_());
                stack.m_41774_(count);
                copper -= count;
            }
            b.crafted |= bit;
            p.m_150109_().m_6596_();
        }
        b.rune = rune;
        Mob mob = ShadowService.active(p.f_8924_, r);
        if (mob != null) {
            WarfrontBuilds.tick(mob, r);
        }
        WarfrontService.dirty(p);
        return true;
    }

    public static boolean cast(ServerPlayer p, UUID id) {
        boolean success;
        ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
        WarfrontState w = WarfrontService.state(p);
        if (JourneyView.active(p) || !CompanionService.accessible(p, r) || r.bond < 10 || !w.wonOnce) {
            return false;
        }
        Mob mob = WarfrontService.a(p, id);
        WarfrontState.Build b = w.build(id);
        if (mob == null || mob.m_20280_((Entity)p) > 1024.0 || b.art == WarfrontState.Art.NONE || WarfrontService.now(p) < b.artAt) {
            return false;
        }
        switch (b.art) {
            case PHANTOM_DOUBLE: {
                boolean bl = WarfrontActors.illusion(p, mob);
                break;
            }
            case TIME_STITCH: {
                b.stitchEntity = mob.m_20148_();
                b.stitchHealth = mob.m_21223_();
                b.stitchAt = WarfrontService.now(p) + 80L;
                boolean bl = true;
                break;
            }
            case SUNDER: {
                boolean bl;
                Vec3 from = p.m_146892_();
                Vec3 end = from.m_82549_(p.m_20154_().m_82490_(32.0));
                EntityHitResult hit = ProjectileUtil.m_37287_((Entity)p, (Vec3)from, (Vec3)end, (AABB)p.m_20191_().m_82369_(end.m_82546_(from)).m_82400_(1.0), e -> {
                    LivingEntity target;
                    return e instanceof LivingEntity && ShadowService.canAttack(p, target = (LivingEntity)e) && p.m_142582_(e);
                }, (double)1024.0);
                if (hit == null) {
                    bl = false;
                    break;
                }
                b.sunderTarget = hit.m_82443_().m_20148_();
                b.targetDimension = p.m_9236_().m_46472_().m_135782_().toString();
                b.sunderUntil = WarfrontService.now(p) + 160L;
                bl = true;
                break;
            }
            default: {
                boolean bl = success = false;
            }
        }
        if (success) {
            SovereignService.progress(p, r.id, SovereignState.Goal.ART);
            EclipseState.Metric em = EclipseService.metric(p, r.id);
            if (em != null) {
                ++em.arts;
            }
            b.artAt = WarfrontService.now(p) + (long)EclipseRoles.artCooldown(p, id);
            ShadowService.soulParticles(mob);
            WarfrontService.dirty(p);
            WarfrontService.tell(p, WarfrontState.label(b.art) + " activated for " + r.name + ".");
        }
        return success;
    }

    public static void tick(Mob mob, ShadowRecord r) {
        WarfrontState.Build b = WarfrontBuilds.build(mob, r);
        long now = mob.m_20194_().m_129783_().m_46467_();
        AttributeInstance speed = mob.m_21051_(Attributes.f_22279_);
        if (speed != null) {
            boolean active;
            AttributeModifier modifier = speed.m_22111_(STRIDE);
            boolean bl = active = b.rune == WarfrontState.Rune.STRIDE && mob.m_5448_() == null && (r.lastDamageAt < 0L || now - r.lastDamageAt >= 100L);
            if (active && modifier == null) {
                speed.m_22118_(new AttributeModifier(STRIDE, "Stride rune", 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            } else if (!active && modifier != null) {
                speed.m_22120_(STRIDE);
            }
        }
        if (b.stitchAt > 0L && now >= b.stitchAt) {
            if (mob.m_20148_().equals(b.stitchEntity) && mob.m_6084_()) {
                mob.m_5634_(Math.min(8.0f, Math.max(0.0f, b.stitchHealth - mob.m_21223_())));
            }
            r.health = mob.m_21223_();
            b.stitchAt = 0L;
            b.stitchEntity = null;
            ArmyData.get(mob.m_20194_()).m_77762_();
        }
    }

    public static void sunder(Mob attacker, ShadowRecord r, LivingEntity target) {
        WarfrontState.Build b = WarfrontBuilds.build(attacker, r);
        if (b.sunderTarget == null || !target.m_20148_().equals(b.sunderTarget) || b.sunderUntil < attacker.m_20194_().m_129783_().m_46467_() || !b.targetDimension.equals(target.m_9236_().m_46472_().m_135782_().toString())) {
            return;
        }
        AttributeInstance armor = target.m_21051_(Attributes.f_22284_);
        if (armor != null) {
            armor.m_22120_(SUNDER);
            armor.m_22118_(new AttributeModifier(SUNDER, "Sunder art", -4.0, AttributeModifier.Operation.ADDITION));
            target.getPersistentData().m_128356_(SUNDER_UNTIL, attacker.m_20194_().m_129783_().m_46467_() + 120L);
        }
        b.sunderTarget = null;
        ArmyData.get(attacker.m_20194_()).m_77762_();
    }

    public static void cleanupSunder(LivingEntity target) {
        if (target.m_9236_().f_46443_ || !target.getPersistentData().m_128441_(SUNDER_UNTIL)) {
            return;
        }
        AttributeInstance armor = target.m_21051_(Attributes.f_22284_);
        long until = target.getPersistentData().m_128454_(SUNDER_UNTIL);
        if (until <= target.m_20194_().m_129783_().m_46467_()) {
            if (armor != null) {
                armor.m_22120_(SUNDER);
            }
            target.getPersistentData().m_128473_(SUNDER_UNTIL);
        } else if (armor != null && armor.m_22111_(SUNDER) == null) {
            armor.m_22118_(new AttributeModifier(SUNDER, "Sunder art", -4.0, AttributeModifier.Operation.ADDITION));
        }
    }

    public static float ward(Mob mob, ShadowRecord r, float damage) {
        WarfrontState.Build b = WarfrontBuilds.build(mob, r);
        long now = mob.m_20194_().m_129783_().m_46467_();
        if (b.rune == WarfrontState.Rune.WARD && damage > 0.0f && now >= b.wardAt) {
            b.wardAt = now + 300L;
            ArmyData.get(mob.m_20194_()).m_77762_();
            return Math.max(0.0f, damage - 1.0f);
        }
        return damage;
    }

    public static void clearTransient(ServerPlayer p) {
        WarfrontState w = WarfrontService.state(p);
        w.placementUntil = 0L;
        for (WarfrontState.Build b : w.builds.values()) {
            b.stitchAt = 0L;
            b.stitchEntity = null;
            b.sunderTarget = null;
        }
        for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            Mob m = ShadowService.active(p.f_8924_, r);
            if (m == null || m.m_21051_(Attributes.f_22279_) == null) continue;
            m.m_21051_(Attributes.f_22279_).m_22120_(STRIDE);
        }
        WarfrontPlans.stop(p);
        WarfrontActors.clear(p);
        WarfrontService.dirty(p);
    }
}

