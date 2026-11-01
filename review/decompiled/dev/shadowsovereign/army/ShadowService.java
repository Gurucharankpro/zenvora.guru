/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.OwnableEntity
 *  net.minecraft.world.entity.TamableAnimal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.animal.horse.AbstractHorse
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.Tags$EntityTypes
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.joml.Vector3f
 */
package dev.shadowsovereign.army;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.ShadowSovereign;
import dev.shadowsovereign.adventure.AdventureCosmetics;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowGoal;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowTargetGoal;
import dev.shadowsovereign.ascension.SoulService;
import dev.shadowsovereign.ascension.WardenCompatibility;
import dev.shadowsovereign.eclipse.EclipseCamp;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.expansion.X1Hooks;
import dev.shadowsovereign.journey.JourneyService;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.localization.ModMessages;
import dev.shadowsovereign.mythic.MythicPower;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.royal.RoyalRuns;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.sentinel.SentinelService;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilGraves;
import dev.shadowsovereign.vigil.VigilRuns;
import dev.shadowsovereign.vigil.VigilService;
import dev.shadowsovereign.vigil.VigilText;
import dev.shadowsovereign.warfront.WarfrontActors;
import dev.shadowsovereign.warfront.WarfrontPlans;
import dev.shadowsovereign.warfront.WarfrontService;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

public final class ShadowService {
    public static final String OWNER = "ShadowSovereignOwner";
    public static final String RECORD = "ShadowSovereignRecord";
    private static final DustParticleOptions DUST = new DustParticleOptions(new Vector3f(0.55f, 0.18f, 0.92f), 1.1f);

    private ShadowService() {
    }

    public static boolean isShadow(Entity entity) {
        return entity.getPersistentData().m_128403_(OWNER);
    }

    @Nullable
    public static UUID ownerId(Entity entity) {
        return ShadowService.isShadow(entity) ? entity.getPersistentData().m_128342_(OWNER) : null;
    }

    @Nullable
    public static ServerPlayer owner(Entity entity) {
        UUID id = ShadowService.ownerId(entity);
        return entity.m_20194_() == null || id == null ? null : entity.m_20194_().m_6846_().m_11259_(id);
    }

    @Nullable
    public static ShadowRecord record(Entity entity) {
        if (!ShadowService.isShadow(entity) || entity.m_20194_() == null || !entity.getPersistentData().m_128403_(RECORD)) {
            return null;
        }
        return ArmyData.get((MinecraftServer)entity.m_20194_()).army((UUID)ShadowService.ownerId((Entity)entity)).shadows.get(entity.getPersistentData().m_128342_(RECORD));
    }

    public static boolean valid(Mob mob, ShadowRecord r) {
        return r != null && mob.m_20148_().equals(r.activeEntity);
    }

    public static CompoundTag snapshot(Mob mob, boolean heal) {
        CompoundTag tag = new CompoundTag();
        mob.m_20240_(tag);
        for (String key : List.of("UUID", "Pos", "Motion", "Rotation", "Passengers", "Leash", "Brain", "DeathTime", "HurtTime", "HurtByTimestamp", "FallDistance", "Fire", "PortalCooldown")) {
            tag.m_128473_(key);
        }
        CompoundTag forgeData = tag.m_128469_("ForgeData");
        forgeData.m_128473_("SoulboundMythicHost");
        forgeData.m_128473_(OWNER);
        forgeData.m_128473_(RECORD);
        forgeData.m_128473_("SoulboundTauntOwner");
        forgeData.m_128473_("SoulboundTauntTank");
        forgeData.m_128473_("SoulboundTauntUntil");
        forgeData.m_128473_("SoulboundAdventureCounted");
        forgeData.m_128473_("SoulboundExtractionOffered");
        forgeData.m_128473_("SoulboundLegacyCounted");
        forgeData.m_128473_("SoulboundJourneyCounted");
        tag.m_128379_("NoAI", false);
        tag.m_128379_("ignited", false);
        if (heal) {
            tag.m_128350_("Health", mob.m_21233_());
        }
        return tag;
    }

    public static void recruit(ServerPlayer player, Mob slain) {
        ArmyData data = ArmyData.get(player.f_8924_);
        ArmyData.Army army = data.army(player.m_20148_());
        ResourceLocation type = ForgeRegistries.ENTITY_TYPES.getKey((Object)slain.m_6095_());
        if (WarfrontActors.illusion((Entity)slain) || slain.getPersistentData().m_128471_("SoulboundExtractionOffered") || !army.autoRecruit || type == null || ShadowService.isShadow((Entity)slain) || AdventureWorld.isNpc((Entity)slain) || ((List)ShadowConfig.BLOCKLIST.get()).contains(type.toString())) {
            return;
        }
        if (!((Boolean)ShadowConfig.RECRUIT_BOSSES.get()).booleanValue() && slain.m_6095_().m_204039_(Tags.EntityTypes.BOSSES)) {
            return;
        }
        if (army.shadows.size() >= (Integer)ShadowConfig.MAX_ARMY.get()) {
            ShadowService.tell(player, "Your shadow reserve is full. Release a shadow to recruit more.");
            return;
        }
        try {
            String name = slain.m_7755_().getString();
            if (name.length() > 80) {
                name = name.substring(0, 80);
            }
            ShadowRecord r = new ShadowRecord(UUID.randomUUID(), type.toString(), name, ShadowService.snapshot(slain, true), slain.m_21233_());
            r.nextHealAt = player.f_8924_.m_129783_().m_46467_() + 100L;
            slain.getPersistentData().m_128379_("SoulboundExtractionOffered", true);
            if (army.manualRecruit) {
                SoulService.offer(player, ShadowService.mobPosition(slain), r);
                return;
            }
            if (!RoyalService.automaticRoll(player, r.type)) {
                return;
            }
            CompanionService.awaken(player, r);
            CompanionService.acquired(player, r);
            army.shadows.put(r.id, r);
            RoyalService.remember(player, r, RoyalService.chance(player), slain.m_20183_());
            AdventureService.captured(player, r);
            data.m_77762_();
            player.m_5661_((Component)ModMessages.of("ARISE  \u2022  " + name + " joined your army").m_130940_(ChatFormatting.LIGHT_PURPLE), true);
            if (army.autoSummon && army.activeCount() < (long)((Integer)ShadowConfig.MAX_ACTIVE.get()).intValue()) {
                ShadowService.summon(player, r, false);
            }
        }
        catch (RuntimeException e) {
            ShadowSovereign.LOGGER.warn("Could not capture mob {}", (Object)type, (Object)e);
        }
    }

    private static Vec3 mobPosition(Mob mob) {
        return mob.m_20182_();
    }

    public static boolean summon(ServerPlayer player, ShadowRecord r, boolean feedback) {
        return ShadowService.summonAt(player, r, feedback, player.m_284548_(), player.m_20182_());
    }

    public static boolean summonAt(ServerPlayer player, ShadowRecord r, boolean feedback, ServerLevel destination, Vec3 near) {
        boolean bl;
        EntityType type;
        AdventureData.Session mythicRun = MythicService.session(player);
        if (mythicRun != null && !mythicRun.participants.contains(r.id)) {
            return false;
        }
        AdventureData.Session fieldRun = LivingRealm.session(player);
        if (fieldRun != null && fieldRun.phase != AdventureData.Phase.READY && !fieldRun.participants.contains(r.id)) {
            return false;
        }
        AdventureData.Session odysseySession = OdysseyRuns.session(player);
        if (odysseySession != null && odysseySession.phase != AdventureData.Phase.READY && !odysseySession.participants.contains(r.id)) {
            return false;
        }
        AdventureData.Session royalSession = RoyalRuns.session(player);
        if (royalSession != null && royalSession.kind != AdventureData.Kind.SEAL_PUZZLE && !royalSession.participants.contains(r.id)) {
            return false;
        }
        AdventureData.Session eclipseSession = EclipseRuns.session(player);
        if (eclipseSession != null && !eclipseSession.participants.contains(r.id)) {
            return false;
        }
        AdventureData.Session vigilSession = VigilRuns.session(player);
        if (vigilSession != null && vigilSession.kind == AdventureData.Kind.FERRYMAN && vigilSession.phase != AdventureData.Phase.READY && !vigilSession.participants.contains(r.id)) {
            return false;
        }
        ArmyData data = ArmyData.get(player.f_8924_);
        if (r.activeEntity != null) {
            return false;
        }
        AdventureData.Session warfront = WarfrontService.session(player);
        if (warfront != null && warfront.phase != AdventureData.Phase.READY && !warfront.participants.contains(r.id)) {
            if (feedback) {
                ShadowService.tell(player, "Your Warfront party is locked until this mission ends. This companion remains safely in reserve.");
            }
            return false;
        }
        if (AdventureService.busy(player.f_8924_, player.m_20148_(), r.id)) {
            if (feedback) {
                ShadowService.tell(player, "This companion is away on an expedition. Cancel or finish it first.");
            }
            return false;
        }
        boolean respawning = r.pendingRespawn();
        if (!VigilGraves.allowed(player, r)) {
            if (feedback) {
                VigilText.tell(player, "cemetery_wait", r.name, r.vigil.fee);
            }
            return false;
        }
        if (respawning && player.f_8924_.m_129783_().m_46467_() < r.respawnAt) {
            if (feedback) {
                ShadowService.tell(player, r.name + " is reforming. Its soul returns after 30 seconds.");
            }
            return false;
        }
        if (data.army(player.m_20148_()).activeCount() >= (long)((Integer)ShadowConfig.MAX_ACTIVE.get()).intValue()) {
            if (feedback) {
                ShadowService.tell(player, "Active shadow limit reached (" + String.valueOf(ShadowConfig.MAX_ACTIVE.get()) + "). Recall a shadow first.");
            }
            return false;
        }
        ResourceLocation key = ResourceLocation.m_135820_((String)r.type);
        EntityType entityType = type = key == null ? null : (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(key);
        if (type == null) {
            if (feedback) {
                ShadowService.tell(player, "This mob's mod is missing: " + r.type);
            }
            return false;
        }
        Mob mob = null;
        try {
            Entity entity = type.m_20615_((Level)destination);
            if (!(entity instanceof Mob)) {
                return false;
            }
            Mob created = (Mob)entity;
            mob = created;
            if (mob instanceof Enemy && destination.m_46791_() == Difficulty.PEACEFUL) {
                if (feedback) {
                    ShadowService.tell(player, "Use Easy, Normal or Hard difficulty to summon hostile mob types.");
                }
                return false;
            }
            mob.m_20258_(r.snapshot.m_6426_());
            MythicPower.apply(mob, r);
            mob.m_20084_(UUID.randomUUID());
            mob.f_20919_ = 0;
            WardenCompatibility.attach(mob);
            CompanionService.apply(mob, r);
            CompanionService.equip(mob, r);
            mob.m_21153_(respawning ? mob.m_21233_() : Math.max(1.0f, Math.min(r.health, mob.m_21233_())));
            Vec3 safe = ShadowService.safePosition(mob, near, destination);
            if (safe == null) {
                if (feedback) {
                    ShadowService.tell(player, "No safe space nearby. Move somewhere with more room.");
                }
                return false;
            }
            mob.m_7678_(safe.f_82479_, safe.f_82480_, safe.f_82481_, player.m_146908_(), 0.0f);
            mob.getPersistentData().m_128362_(OWNER, player.m_20148_());
            mob.getPersistentData().m_128362_(RECORD, r.id);
            if (mob instanceof TamableAnimal) {
                TamableAnimal tame = (TamableAnimal)mob;
                tame.m_21816_(player.m_20148_());
                tame.m_7105_(true);
                tame.m_21839_(false);
            }
            if (mob instanceof AbstractHorse) {
                AbstractHorse horse = (AbstractHorse)mob;
                horse.m_30586_(player.m_20148_());
            }
            r.activeEntity = mob.m_20148_();
            r.dimension = destination.m_46472_().m_135782_().toString();
            r.anchor = mob.m_20183_();
            ShadowService.initialize(mob);
            if (!destination.m_7967_((Entity)mob)) {
                r.activeEntity = null;
                return false;
            }
            if (!mob.m_6084_() || !mob.m_20148_().equals(r.activeEntity)) {
                return false;
            }
            if (respawning) {
                r.respawnAt = -1L;
                r.lastDamageAt = -1L;
                r.nextHealAt = player.f_8924_.m_129783_().m_46467_() + 100L;
                r.health = mob.m_21223_();
                ShadowService.soulParticles(mob);
            } else {
                ShadowService.particles(mob, 18);
            }
            VigilService.returned(player, r);
            if (respawning) {
                VigilGraves.returned(player, r);
            }
            r.sentinel.wanted = true;
            r.sentinel.last = mob.m_20183_();
            data.m_77762_();
            bl = true;
        }
        catch (RuntimeException e) {
            r.activeEntity = null;
            if (mob != null) {
                mob.m_146870_();
            }
            ShadowSovereign.LOGGER.warn("Cannot summon shadow {} ({})", new Object[]{r.name, r.type, e});
            if (feedback) {
                ShadowService.tell(player, "This mob could not be summoned. Check the server log for compatibility details.");
            }
            return false;
        }
        X1Hooks.summoned(player, r);
        return bl;
    }

    public static void initialize(Mob mob) {
        mob.f_21345_.m_148105_().stream().filter(g -> g.m_26015_() instanceof ShadowGoal).toList().forEach(g -> mob.f_21345_.m_25363_(g.m_26015_()));
        mob.f_21346_.m_148105_().stream().filter(g -> g.m_26015_() instanceof ShadowTargetGoal).toList().forEach(g -> mob.f_21346_.m_25363_(g.m_26015_()));
        mob.f_21345_.m_25352_(-10, (Goal)new ShadowGoal(mob));
        mob.f_21346_.m_25352_(-10, (Goal)new ShadowTargetGoal(mob));
        mob.m_21557_(false);
        mob.m_21530_();
        WardenCompatibility.attach(mob);
        mob.m_21553_(false);
        mob.m_6710_(null);
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (r != null) {
            CompanionService.apply(mob, r);
            r.health = mob.m_21223_();
            r.maxHealth = mob.m_21233_();
            ShadowService.updateName(mob, r);
        }
    }

    public static void updateName(Mob mob, ShadowRecord r) {
        mob.m_6593_((Component)Component.m_237113_((String)("\u2726 " + (String)(r.soulAwakened ? "[" + r.tier.label + "] " : "") + LegacyService.name(mob.m_20194_(), ShadowService.ownerId((Entity)mob), r) + AdventureService.title(mob, r) + JourneyService.badge(mob, r) + (ArmyData.get((MinecraftServer)mob.m_20194_()).army((UUID)ShadowService.ownerId((Entity)mob)).warfront.builds.containsKey(r.id) && ArmyData.get((MinecraftServer)mob.m_20194_()).army((UUID)ShadowService.ownerId((Entity)mob)).warfront.build((UUID)r.id).pactbound ? " [Pactbound]" : "") + " [Lv. " + r.level() + "]")).m_130940_(r.soulAwakened ? r.tier.color : ChatFormatting.LIGHT_PURPLE));
        SoulLifeService.appendName(mob, r);
        mob.m_20340_(true);
        EclipseCamp.appearance(mob, r);
    }

    @Nullable
    public static Mob active(MinecraftServer server, ShadowRecord r) {
        if (r.activeEntity == null) {
            return null;
        }
        for (ServerLevel level : server.m_129785_()) {
            Entity entity = level.m_8791_(r.activeEntity);
            if (!(entity instanceof Mob)) continue;
            Mob mob = (Mob)entity;
            return mob;
        }
        return null;
    }

    public static boolean recall(ServerPlayer player, ShadowRecord r) {
        AdventureData.Session eclipseRun;
        AdventureData.Session fieldRun;
        AdventureData.Session mythic = MythicService.session(player);
        if (mythic != null && mythic.phase == AdventureData.Phase.ACTIVE && mythic.participants.contains(r.id)) {
            MythicService.pause(player.f_8924_, player.m_20148_());
        }
        if ((fieldRun = LivingRealm.session(player)) != null && fieldRun.phase == AdventureData.Phase.ACTIVE && fieldRun.participants.contains(r.id)) {
            AdventureWorld.pause(player.f_8924_, player.m_20148_());
        }
        if (VigilCrown.member(player, r) && player.m_6084_() && !VigilService.internalRecall) {
            VigilText.tell(player, "crown_locked", new Object[0]);
            return false;
        }
        AdventureData.Session odysseyRun = OdysseyRuns.session(player);
        if (odysseyRun != null && odysseyRun.phase == AdventureData.Phase.ACTIVE && odysseyRun.participants.contains(r.id)) {
            OdysseyRuns.pause(player);
        }
        SoulPlay.onCommand(player, r);
        VigilService.recalling(player, r);
        AdventureData.Session royalRun = RoyalRuns.session(player);
        if (royalRun != null && royalRun.phase == AdventureData.Phase.ACTIVE && royalRun.participants.contains(r.id)) {
            AdventureWorld.pause(player.f_8924_, player.m_20148_());
        }
        if ((eclipseRun = EclipseRuns.session(player)) != null && eclipseRun.phase == AdventureData.Phase.ACTIVE && eclipseRun.participants.contains(r.id)) {
            AdventureWorld.pause(player.f_8924_, player.m_20148_());
        }
        WarfrontPlans.stop(player);
        if (r.activeEntity == null) {
            if (!SentinelService.internalRecall && !VigilService.internalRecall && r.sentinel.wanted) {
                r.sentinel.wanted = false;
                r.sentinel.rally = false;
                ArmyData.get(player.f_8924_).m_77762_();
                return true;
            }
            return false;
        }
        Mob mob = ShadowService.active(player.f_8924_, r);
        if (mob != null && mob.m_6084_()) {
            ShadowService.updateSnapshot(mob, r);
        }
        r.activeEntity = null;
        if (!SentinelService.internalRecall && !VigilService.internalRecall) {
            r.sentinel.wanted = false;
        }
        if (mob != null) {
            Mob mob2 = mob;
            X1Hooks.recall(mob2, 14);
            ShadowService.particles(mob2, 14);
            mob.m_146870_();
        }
        ArmyData.get(player.f_8924_).m_77762_();
        GuideService.record(player, "recalled");
        return true;
    }

    public static void recallAll(ServerPlayer player) {
        ArmyData.get((MinecraftServer)player.f_8924_).army((UUID)player.m_20148_()).shadows.values().forEach(r -> ShadowService.recall(player, r));
    }

    public static void setOrder(ServerPlayer player, ShadowRecord r, Order order) {
        if (VigilCrown.member(player, r) && !VigilService.internalOrder) {
            VigilText.tell(player, "crown_locked", new Object[0]);
            return;
        }
        WarfrontPlans.stop(player);
        AdventureData.Session odysseyRun = OdysseyRuns.session(player);
        if (odysseyRun != null && odysseyRun.phase == AdventureData.Phase.ACTIVE && odysseyRun.participants.contains(r.id)) {
            OdysseyRuns.pause(player);
        }
        SoulPlay.onCommand(player, r);
        r.order = order;
        X1Hooks.command(player, r, order);
        GuideService.record(player, "order." + order.name().toLowerCase(Locale.ROOT));
        VigilService.ordered(player, r, order);
        r.battle.commandedTarget = null;
        Mob mob = ShadowService.active(player.f_8924_, r);
        if (mob != null) {
            r.anchor = mob.m_20183_();
            mob.m_6710_(null);
            mob.m_21573_().m_26573_();
        }
        SentinelService.onOrder(player, r, order);
        ArmyData.get(player.f_8924_).m_77762_();
    }

    public static void updateSnapshot(Mob mob, ShadowRecord r) {
        CompanionService.captureEquipment(mob, r);
        r.sentinel.last = mob.m_20183_();
        r.dimension = mob.m_9236_().m_46472_().m_135782_().toString();
        r.snapshot = ShadowService.snapshot(mob, false);
        r.health = mob.m_21223_();
        r.maxHealth = mob.m_21233_();
        ArmyData.get(mob.m_20194_()).m_77762_();
    }

    public static void shadowDied(Mob mob) {
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (!ShadowService.valid(mob, r)) {
            return;
        }
        r.sentinel.wanted = true;
        if (!r.pendingRespawn()) {
            r.vigil.deaths = Math.min(Integer.MAX_VALUE, r.vigil.deaths + 1);
        }
        r.vigil.fee = VigilGraves.price(mob.m_21233_());
        CompanionService.captureEquipment(mob, r);
        r.snapshot = ShadowService.snapshot(mob, true);
        r.health = r.maxHealth = mob.m_21233_();
        X1Hooks.emit((Entity)mob, 15);
        r.activeEntity = null;
        r.respawnAt = mob.m_20194_().m_129783_().m_46467_() + 600L;
        r.nextRespawnAttemptAt = 0L;
        r.lastDamageAt = -1L;
        r.nextHealAt = -1L;
        r.battle.shield = 0.0f;
        r.battle.commandedTarget = null;
        ArmyData.get(mob.m_20194_()).m_77762_();
        ServerPlayer player = ShadowService.owner((Entity)mob);
        if (player != null) {
            WarfrontService.companionDied(player, r.id);
            EclipseService.died(player, r);
        }
        if (player != null) {
            if (VigilGraves.needsPayment(r)) {
                VigilText.tell(player, "cemetery_wait", r.name, r.vigil.fee);
            } else {
                ShadowService.tell(player, r.name + " fell in battle. Its soul will return in 30 seconds.");
            }
        }
    }

    public static boolean allied(ServerPlayer owner, Entity entity) {
        if (entity == owner || owner.m_20148_().equals(ShadowService.ownerId(entity))) {
            return true;
        }
        if (entity instanceof OwnableEntity) {
            OwnableEntity pet = (OwnableEntity)entity;
            if (owner.m_20148_().equals(pet.m_21805_())) {
                return true;
            }
        }
        return owner.m_7307_(entity);
    }

    public static boolean canAttack(ServerPlayer owner, LivingEntity target) {
        if (target.getPersistentData().m_128403_("SoulboundMythicHost") && !owner.m_20148_().equals(target.getPersistentData().m_128342_("SoulboundMythicHost"))) {
            return false;
        }
        if (WarfrontActors.illusion((Entity)target)) {
            return false;
        }
        if (AdventureWorld.isNpc((Entity)target)) {
            return owner.m_20148_().equals(target.getPersistentData().m_128342_("SoulboundAdventureOwner")) && target.getPersistentData().m_128461_("SoulboundAdventureRole").equals("ENEMY") && AdventureWorld.valid((Entity)target) && target.m_6084_();
        }
        return target.m_6084_() && !(target instanceof Player) && !ShadowService.isShadow((Entity)target) && !target.m_20147_() && !ShadowService.allied(owner, (Entity)target);
    }

    @Nullable
    public static Vec3 safePosition(Mob mob, Vec3 center, ServerLevel level) {
        for (int i = 0; i < 40; ++i) {
            double angle = (double)i * 2.399963;
            double radius = 2.5 + (double)(i / 10) * 1.5;
            int x = (int)Math.floor(center.f_82479_ + Math.cos(angle) * radius);
            int z = (int)Math.floor(center.f_82481_ + Math.sin(angle) * radius);
            for (int dy : new int[]{0, 1, -1, 2, -2, 3, -3, 4, -4}) {
                BlockPos pos = new BlockPos(x, (int)Math.floor(center.f_82480_) + dy, z);
                if (!level.m_46805_(pos) || !level.m_6857_().m_61937_(pos) || !level.m_8055_(pos.m_7495_()).m_60804_((BlockGetter)level, pos.m_7495_()) && level.m_6425_(pos).m_76178_()) continue;
                Vec3 candidate = Vec3.m_82539_((Vec3i)pos);
                if (!level.m_45756_((Entity)mob, mob.m_20191_().m_82383_(candidate.m_82546_(mob.m_20182_())))) continue;
                return candidate;
            }
        }
        return null;
    }

    public static void particles(Mob mob, int count) {
        if (AdventureCosmetics.particles(mob, count)) {
            return;
        }
        Level level = mob.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            level2.m_8767_((ParticleOptions)DUST, mob.m_20185_(), mob.m_20186_() + 0.5, mob.m_20189_(), count, (double)mob.m_20205_() * 0.4, Math.min(1.2, (double)mob.m_20206_() * 0.4), (double)mob.m_20205_() * 0.4, 0.01);
        }
    }

    public static void soulParticles(Mob mob) {
        Level level = mob.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            level2.m_8767_((ParticleOptions)ParticleTypes.f_123746_, mob.m_20185_(), mob.m_20186_() + 0.8, mob.m_20189_(), 24, 0.5, 0.7, 0.5, 0.03);
            level2.m_8767_((ParticleOptions)ParticleTypes.f_123745_, mob.m_20185_(), mob.m_20186_() + 0.4, mob.m_20189_(), 24, 0.6, 0.4, 0.6, 0.015);
        }
    }

    public static void tickRespawns(MinecraftServer minecraftServer) {
        MinecraftServer server;
        X1Hooks.tick(minecraftServer);
        long now = server.m_129783_().m_46467_();
        ArmyData data = ArmyData.get(server);
        block0: for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!player.m_6084_()) continue;
            ArmyData.Army army = data.army(player.m_20148_());
            for (ShadowRecord r : army.shadows.values()) {
                if (VigilGraves.needsPayment(r) || !r.pendingRespawn() || r.activeEntity != null || now < r.respawnAt || now < r.nextRespawnAttemptAt) continue;
                if (army.activeCount() >= (long)((Integer)ShadowConfig.MAX_ACTIVE.get()).intValue()) continue block0;
                r.nextRespawnAttemptAt = now + 20L;
                ShadowService.summon(player, r, false);
            }
        }
    }

    public static void tell(ServerPlayer player, String text) {
        player.m_213846_((Component)ModMessages.of("\u2726 " + text).m_130940_(ChatFormatting.LIGHT_PURPLE));
    }

    public static void sync(ServerPlayer player, boolean open) {
        ShadowNetwork.sendArmy(player, open);
    }
}

