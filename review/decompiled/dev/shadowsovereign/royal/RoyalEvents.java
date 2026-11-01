/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.royal;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.network.RoyalNetwork;
import dev.shadowsovereign.royal.RoyalContent;
import dev.shadowsovereign.royal.RoyalRuns;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.royal.RoyalText;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class RoyalEvents {
    private static boolean reflecting;

    @SubscribeEvent
    public static void royalTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        long now = e.getServer().m_129783_().m_46467_();
        for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
            RoyalState r = RoyalService.state(p);
            r.breach.entrySet().removeIf(v -> (Long)v.getValue() <= now);
            if (now % 20L != 0L || r.royalViewUntil <= now) continue;
            RoyalNetwork.send(p, false, 0);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void royalLogin(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            RoyalService.clearRuntime(p);
            RoyalNetwork.send(p, false, 0);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void royalLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            RoyalService.clearRuntime(p);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void royalDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            RoyalService.clearRuntime(p);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void royalAttack(LivingAttackEvent e) {
        ShadowRecord r;
        AdventureData.Session run;
        Entity source;
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        LivingEntity victim = e.getEntity();
        AdventureData.Session s = AdventureWorld.session((Entity)victim);
        if (s != null && RoyalRuns.is(s.kind)) {
            ServerPlayer owner = AdventureWorld.owner((Entity)victim);
            if (owner == null) {
                e.setCanceled(true);
                return;
            }
            if (s.kind == AdventureData.Kind.SKILL_COURSE && s.royal.m_128451_("Stage") == 1 && (RoyalService.casting != EclipseState.Power.LANCE || e.getSource().m_7639_() != owner)) {
                e.setCanceled(true);
                return;
            }
            ShadowRecord r2 = CompanionProgression.attacker(e.getSource());
            if (r2 != null && !s.participants.contains(r2.id)) {
                e.setCanceled(true);
                return;
            }
            AdventureData.Npc npc = AdventureWorld.npc((Entity)victim);
            if (s.kind == AdventureData.Kind.MIMIC && npc != null && npc.id.equals(s.royal.m_128342_("Boss")) && s.royal.m_128454_("GraceUntil") > RoyalService.now(owner)) {
                e.setCanceled(true);
            }
        }
        if ((source = e.getSource().m_7639_()) != null && AdventureWorld.isNpc(source) && (run = AdventureWorld.session(source)) != null && RoyalRuns.is(run.kind) && ShadowService.isShadow((Entity)victim) && ((r = ShadowService.record((Entity)victim)) == null || !run.participants.contains(r.id))) {
            e.setCanceled(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent(priority=EventPriority.LOW)
    public static void royalHurt(LivingHurtEvent e) {
        ServerPlayer p;
        if (e.getEntity().m_9236_().f_46443_ || e.getAmount() <= 0.0f) {
            return;
        }
        LivingEntity target = e.getEntity();
        DamageSource source = e.getSource();
        Entity actor = CompanionProgression.source(source);
        ShadowRecord r = CompanionProgression.attacker(source);
        if (r != null && actor != null && source.m_276093_(DamageTypes.f_268566_) && (p = ShadowService.owner(actor)) != null) {
            RoyalState data = RoyalService.state(p);
            if (data.breachAt <= RoyalService.now(p) && data.breach.getOrDefault(target.m_20148_(), 0L) > RoyalService.now(p)) {
                data.breach.remove(target.m_20148_());
                data.breachAt = RoyalService.now(p) + 240L;
                e.setAmount(e.getAmount() + Math.min(4.0f, e.getAmount() * 0.1f));
                RoyalService.dirty(p);
            }
        }
        AdventureData.Session run = AdventureWorld.session((Entity)target);
        ServerPlayer owner = AdventureWorld.owner((Entity)target);
        AdventureData.Npc npc = AdventureWorld.npc((Entity)target);
        if (run != null && run.kind == AdventureData.Kind.MIMIC && owner != null && npc != null && npc.id.equals(run.royal.m_128342_("Boss")) && run.royal.m_128454_("WardUntil") > RoyalService.now(owner)) {
            e.setAmount(e.getAmount() * 0.5f);
            Entity attacker = source.m_7639_();
            if (!reflecting && !run.royal.m_128471_("WardHit") && attacker instanceof LivingEntity) {
                LivingEntity living = (LivingEntity)attacker;
                if ((attacker == owner || owner.m_20148_().equals(ShadowService.ownerId(attacker))) && target.m_20280_((Entity)living) <= 64.0) {
                    run.royal.m_128379_("WardHit", true);
                    reflecting = true;
                    try {
                        living.m_6469_(target.m_269291_().m_269104_((Entity)target, (Entity)target), 2.0f);
                    }
                    finally {
                        reflecting = false;
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void royalDamage(LivingDamageEvent e) {
        AdventureData.Npc npc;
        AdventureData.Session safe;
        ServerPlayer player;
        if (e.getEntity().m_9236_().f_46443_ || e.getAmount() <= 0.0f) {
            return;
        }
        LivingEntity target = e.getEntity();
        AdventureData.Session run = AdventureWorld.session((Entity)target);
        ServerPlayer p = AdventureWorld.owner((Entity)target);
        if (run != null && p != null && run.kind == AdventureData.Kind.TRAINING) {
            RoyalService.acceptedTarget = target.m_20148_();
            RoyalRuns.trainingDamage(p, target, e.getAmount(), e.getSource().m_7639_());
            e.setAmount(0.0f);
            target.m_21153_(target.m_21233_());
            return;
        }
        if (run != null && p != null && run.kind == AdventureData.Kind.SKILL_COURSE && run.royal.m_128451_("Stage") == 1 && target instanceof Mob) {
            Mob mob = (Mob)target;
            RoyalService.acceptedTarget = target.m_20148_();
            e.setAmount(0.0f);
            if (RoyalService.casting == EclipseState.Power.LANCE && e.getSource().m_7639_() == p) {
                RoyalRuns.hitTarget(p, mob);
            }
            return;
        }
        ServerPlayer protectedOwner = target instanceof ServerPlayer ? (player = (ServerPlayer)target) : ShadowService.owner((Entity)target);
        AdventureData.Session session = safe = protectedOwner == null ? null : RoyalRuns.session(protectedOwner);
        if (!(safe == null || safe.kind != AdventureData.Kind.TRAINING && safe.kind != AdventureData.Kind.SKILL_COURSE || e.getSource().m_7639_() == null || AdventureWorld.session(e.getSource().m_7639_()) != safe || e.getSource().m_269533_(DamageTypeTags.f_268738_))) {
            e.setAmount(Math.max(0.0f, Math.min(e.getAmount(), target.m_21223_() - 1.0f)));
        }
        if (run != null && run.kind == AdventureData.Kind.MIMIC && p != null && (npc = AdventureWorld.npc((Entity)target)) != null && npc.id.equals(run.royal.m_128342_("Boss"))) {
            int phase = run.royal.m_128451_("BossPhase");
            float threshold = target.m_21233_() * (phase == 0 ? 0.65f : 0.35f);
            if (phase < 2 && target.m_21223_() - e.getAmount() <= threshold) {
                e.setAmount(Math.max(0.0f, target.m_21223_() - threshold));
                run.royal.m_128405_("BossPhase", phase + 1);
                run.royal.m_128356_("GraceUntil", RoyalService.now(p) + 40L);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void royalRescue(LivingDeathEvent e) {
        Mob mob;
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)livingEntity;
            if (RoyalRuns.session(p) != null) {
                RoyalRuns.fail(p, "defeat");
            }
            RoyalService.clearRuntime(p);
            return;
        }
        livingEntity = e.getEntity();
        if (!(livingEntity instanceof Mob) || !ShadowService.isShadow((Entity)(mob = (Mob)livingEntity)) || e.getSource().m_269533_(DamageTypeTags.f_268738_)) {
            return;
        }
        ServerPlayer p = ShadowService.owner((Entity)mob);
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (p == null || r == null || AdventureService.journal((ServerPlayer)p).session != null) {
            return;
        }
        RoyalState data = RoyalService.state(p);
        if (data.charmAt.getOrDefault(r.id, 0L) > RoyalService.now(p)) {
            return;
        }
        r.inventory.initialize(r.snapshot);
        for (int i = 6; i < 11; ++i) {
            ItemStack stack = (ItemStack)r.inventory.items.get(i);
            if (!stack.m_150930_((Item)RoyalContent.CHARM.get())) continue;
            stack.m_41774_(1);
            data.charmAt.put(r.id, RoyalService.now(p) + 6000L);
            mob.m_21153_(Math.max(1.0f, mob.m_21233_() * 0.25f));
            r.health = mob.m_21223_();
            r.lastDamageAt = RoyalService.now(p);
            r.nextHealAt = r.lastDamageAt + 100L;
            e.setCanceled(true);
            RoyalService.dirty(p);
            RoyalText.tell(p, "rescued", LegacyService.name(p, r));
            EclipsePowers.particles(p, mob.m_20182_().m_82520_(0.0, 1.0, 0.0), ParticleTypes.f_123767_, 16);
            break;
        }
    }
}

