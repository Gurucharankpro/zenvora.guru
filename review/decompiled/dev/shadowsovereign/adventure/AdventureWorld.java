/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.animal.Wolf
 *  net.minecraft.world.entity.monster.AbstractSkeleton
 *  net.minecraft.world.entity.monster.Zombie
 *  net.minecraft.world.entity.raid.Raider
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.adventure;

import dev.shadowsovereign.adventure.AdventureContent;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.AscensionActors;
import dev.shadowsovereign.ascension.AscensionService;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.journey.JourneyCombat;
import dev.shadowsovereign.journey.JourneyService;
import dev.shadowsovereign.legacy.LegacyChallenges;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.royal.RoyalMutators;
import dev.shadowsovereign.royal.RoyalRuns;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.vigil.VigilRuns;
import dev.shadowsovereign.warfront.WarfrontActors;
import dev.shadowsovereign.warfront.WarfrontPlans;
import dev.shadowsovereign.warfront.WarfrontService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class AdventureWorld {
    public static final String OWNER = "SoulboundAdventureOwner";
    public static final String SESSION = "SoulboundAdventureSession";
    public static final String NPC = "SoulboundAdventureNpc";
    public static final String EPOCH = "SoulboundAdventureEpoch";
    public static final String ROLE = "SoulboundAdventureRole";

    private AdventureWorld() {
    }

    public static boolean isNpc(Entity e) {
        return e.getPersistentData().m_128403_(OWNER);
    }

    @Nullable
    public static AdventureData.Session session(Entity e) {
        if (!AdventureWorld.isNpc(e) || e.m_20194_() == null) {
            return null;
        }
        AdventureData.Journal j = AdventureData.get((MinecraftServer)e.m_20194_()).owners.get(e.getPersistentData().m_128342_(OWNER));
        return j == null ? null : j.session;
    }

    @Nullable
    public static AdventureData.Npc npc(Entity e) {
        AdventureData.Session s = AdventureWorld.session(e);
        return s == null || !e.getPersistentData().m_128403_(NPC) ? null : s.npcs.get(e.getPersistentData().m_128342_(NPC));
    }

    public static boolean valid(Entity e) {
        if (!e.getPersistentData().m_128403_(SESSION) || !e.getPersistentData().m_128403_(NPC)) {
            return false;
        }
        AdventureData.Session s = AdventureWorld.session(e);
        AdventureData.Npc n = AdventureWorld.npc(e);
        return s != null && n != null && s.phase == AdventureData.Phase.ACTIVE && s.id.equals(e.getPersistentData().m_128342_(SESSION)) && s.epoch == e.getPersistentData().m_128451_(EPOCH) && e.m_20148_().equals(n.active);
    }

    @Nullable
    public static ServerPlayer owner(Entity e) {
        return AdventureWorld.isNpc(e) && e.m_20194_() != null ? e.m_20194_().m_6846_().m_11259_(e.getPersistentData().m_128342_(OWNER)) : null;
    }

    @Nullable
    private static ServerLevel level(MinecraftServer server, String dimension) {
        ResourceLocation key = ResourceLocation.m_135820_((String)dimension);
        return key == null ? null : server.m_129880_(ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)key));
    }

    @Nullable
    public static Vec3 spot(ServerLevel level, EntityType<?> type, BlockPos center, double min, double max, long salt) {
        Entity dummy = type.m_20615_((Level)level);
        if (dummy == null) {
            return null;
        }
        for (int i = 0; i < 96; ++i) {
            Vec3 found;
            BlockPos base;
            double angle = (double)((long)i + (salt & 0x3FFL)) * 2.399963229728653;
            double radius = min + (max - min) * (double)(i % 13) / 12.0;
            int x = (int)Math.floor((double)center.m_123341_() + 0.5 + Math.cos(angle) * radius);
            int z = (int)Math.floor((double)center.m_123343_() + 0.5 + Math.sin(angle) * radius);
            double radial = (double)(x - center.m_123341_()) * (double)(x - center.m_123341_()) + (double)(z - center.m_123343_()) * (double)(z - center.m_123343_());
            if (radial < min * min || radial > max * max || !level.m_46805_(base = new BlockPos(x, center.m_123342_() + 1, z))) continue;
            for (int dy : new int[]{0, 1, -1, 2, -2, 3, -3, 4, -4, 6, -6, 8, -8}) {
                Vec3 found2 = AdventureWorld.fit(level, dummy, base.m_7918_(0, dy, 0));
                if (found2 == null) continue;
                return found2;
            }
            int surface = level.m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (Math.abs(surface - center.m_123342_()) > 24 || (found = AdventureWorld.fit(level, dummy, new BlockPos(x, surface, z))) == null) continue;
            return found;
        }
        return null;
    }

    @Nullable
    private static Vec3 fit(ServerLevel level, Entity dummy, BlockPos pos) {
        if (!(level.m_46805_(pos) && level.m_6857_().m_61937_(pos) && level.m_8055_(pos.m_7495_()).m_60804_((BlockGetter)level, pos.m_7495_()) && level.m_6425_(pos).m_76178_())) {
            return null;
        }
        if (level.m_8055_(pos).m_60713_(Blocks.f_50083_) || level.m_8055_(pos).m_60713_(Blocks.f_50084_) || level.m_8055_(pos).m_60713_(Blocks.f_152499_) || level.m_8055_(pos.m_7495_()).m_60713_(Blocks.f_50450_)) {
            return null;
        }
        Vec3 v = Vec3.m_82539_((Vec3i)pos);
        AABB box = dummy.m_20191_().m_82383_(v.m_82546_(dummy.m_20182_()));
        if (!(level.m_46805_(BlockPos.m_274561_((double)box.f_82288_, (double)box.f_82289_, (double)box.f_82290_)) && level.m_46805_(BlockPos.m_274561_((double)box.f_82291_, (double)box.f_82289_, (double)box.f_82293_)) && level.m_6857_().m_61935_(box))) {
            return null;
        }
        return level.m_45756_(dummy, box) ? v : null;
    }

    public static List<BlockPos> cacheSpots(ServerLevel level, BlockPos center, UUID owner, long cycle) {
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        for (int i = 0; i < 12 && result.size() < 3; ++i) {
            Vec3 p = AdventureWorld.spot(level, EntityType.f_20529_, center, 24.0, 96.0, (long)owner.hashCode() + cycle * 31L + (long)(i * 7919));
            if (p == null || !result.stream().allMatch(old -> old.m_123331_((Vec3i)BlockPos.m_274446_((Position)p)) >= 144.0)) continue;
            result.add(BlockPos.m_274446_((Position)p));
        }
        return result;
    }

    public static boolean start(ServerPlayer p, AdventureData.Kind kind, int tier) {
        if (kind == AdventureData.Kind.LIVING_REALM || kind == AdventureData.Kind.MYTHIC_LAIR) {
            return false;
        }
        if (VigilRuns.is(kind) || RoyalRuns.is(kind) || EclipseRuns.is(kind) || kind == AdventureData.Kind.WARFRONT || JourneyService.isJourney(kind) || AscensionService.activity(kind)) {
            return false;
        }
        if (kind == AdventureData.Kind.VAULT) {
            return LegacyChallenges.startVault(p);
        }
        if (LegacyChallenges.legacy(kind) && !LegacyService.available(p, kind == AdventureData.Kind.GAUNTLET ? 5 : 3)) {
            return false;
        }
        if (!AdventureService.requireHub(p) || tier < 0 || tier > 2) {
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.session != null) {
            AdventureService.tell(p, "Claim or cancel your existing physical adventure first.");
            return false;
        }
        if (kind == AdventureData.Kind.TRIAL && j.discoveries.size() < (tier == 0 ? 0 : (tier == 1 ? 5 : 15))) {
            AdventureService.tell(p, "Veteran needs 5 discovered types; Monarch needs 15.");
            return false;
        }
        if (p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            AdventureService.tell(p, "Physical adventures require Easy, Normal or Hard difficulty.");
            return false;
        }
        List<UUID> participants = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().filter(r -> {
            Mob m = ShadowService.active(p.f_8924_, r);
            return m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 1024.0;
        }).map(r -> r.id).toList();
        if (participants.isEmpty()) {
            AdventureService.tell(p, "Bring at least one active companion within 32 blocks.");
            return false;
        }
        if (AdventureWorld.spot(p.m_284548_(), kind == AdventureData.Kind.ESCORT ? EntityType.f_20499_ : EntityType.f_20501_, j.sanctuary, kind == AdventureData.Kind.ESCORT ? 32.0 : 5.0, kind == AdventureData.Kind.ESCORT ? 48.0 : 14.0, 0L) == null) {
            AdventureService.tell(p, "There is not enough safe loaded terrain for this adventure.");
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = kind;
        s.tier = tier;
        s.anchor = j.sanctuary;
        s.dimension = j.dimension;
        s.phase = AdventureData.Phase.ACTIVE;
        s.participants.addAll(participants);
        j.session = s;
        if (kind == AdventureData.Kind.ESCORT) {
            AdventureData.Npc n = AdventureWorld.template(p, s, (EntityType<? extends Mob>)EntityType.f_20499_, AdventureData.Role.ESCORT);
            s.npcs.put(n.id, n);
            AdventureWorld.spawn(p, s, n);
        }
        AdventureService.dirty(p);
        AdventureService.tell(p, LegacyChallenges.legacy(kind) ? "Legacy challenge opened. Stay near your Sanctuary." : (kind == AdventureData.Kind.TRIAL ? "Rift Trial opened. Stay within 40 blocks of your Sanctuary." : "Find the Lost Soul nearby and escort it home. Clear both ambushes!"));
        return true;
    }

    public static AdventureData.Npc template(ServerPlayer p, AdventureData.Session s, EntityType<? extends Mob> type, AdventureData.Role role) {
        Mob mob = (Mob)type.m_20615_((Level)p.m_284548_());
        if (mob == null) {
            throw new IllegalStateException("Missing quest entity " + String.valueOf(type));
        }
        mob.m_21530_();
        mob.m_21553_(false);
        if (mob instanceof Zombie) {
            EquipmentSlot[] zombie = (EquipmentSlot[])mob;
            zombie.m_6863_(false);
        }
        if (mob instanceof AbstractSkeleton) {
            mob.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.f_42411_));
        }
        if (role == AdventureData.Role.ENEMY && (mob instanceof Zombie || mob instanceof AbstractSkeleton)) {
            mob.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)Items.f_42468_));
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            mob.m_21409_(slot, 0.0f);
        }
        if (mob instanceof Raider) {
            Raider raider = (Raider)mob;
            raider.m_37897_(false);
        }
        if (role == AdventureData.Role.ESCORT && mob instanceof Wolf) {
            Wolf wolf = (Wolf)mob;
            wolf.m_7105_(true);
            wolf.m_21816_(p.m_20148_());
            wolf.m_21839_(false);
            wolf.m_21153_(wolf.m_21233_());
        }
        if (type == EntityType.f_20518_ && s.tier == 2) {
            mob.m_21051_(Attributes.f_22276_).m_22100_((double)mob.m_21233_() * 1.5);
            mob.m_21153_(mob.m_21233_());
        }
        mob.m_6593_((Component)Component.m_237113_((String)(role == AdventureData.Role.ESCORT ? "Lost Soul" : "Rift " + mob.m_7755_().getString())));
        mob.m_20340_(true);
        AdventureData.Npc n = new AdventureData.Npc();
        n.type = String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(type));
        n.role = role;
        n.snapshot = AdventureWorld.snapshot(mob);
        n.pos = s.anchor;
        return n;
    }

    public static CompoundTag snapshot(Mob mob) {
        CompoundTag n = new CompoundTag();
        mob.m_20240_(n);
        for (String key : List.of("UUID", "Pos", "Motion", "Rotation", "Passengers", "Leash", "Brain", "DeathTime", "HurtTime")) {
            n.m_128473_(key);
        }
        CompoundTag data = n.m_128469_("ForgeData");
        for (String key : List.of(OWNER, SESSION, NPC, EPOCH, ROLE)) {
            data.m_128473_(key);
        }
        return n;
    }

    public static boolean spawn(ServerPlayer p, AdventureData.Session s, AdventureData.Npc n) {
        Entity entity;
        EntityType type;
        ResourceLocation key = ResourceLocation.m_135820_((String)n.type);
        EntityType entityType = type = key == null ? null : (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(key);
        if (type == null || !((entity = type.m_20615_((Level)p.m_284548_())) instanceof Mob)) {
            return false;
        }
        Mob mob = (Mob)entity;
        mob.m_20258_(n.snapshot.m_6426_());
        mob.m_20084_(UUID.randomUUID());
        mob.f_20919_ = 0;
        BlockPos center = s.kind == AdventureData.Kind.ODYSSEY || s.kind == AdventureData.Kind.LIVING_REALM || s.kind == AdventureData.Kind.WARFRONT || (EclipseRuns.is(s.kind) || RoyalRuns.is(s.kind)) && n.snapshot.m_128471_("NoAI") ? n.pos : (s.kind == AdventureData.Kind.ESCORT && n.role == AdventureData.Role.ENEMY && n.pos.m_123331_((Vec3i)s.anchor) < 4096.0 ? n.pos : s.anchor);
        Vec3 at = AdventureWorld.spot(p.m_284548_(), type, center, s.kind == AdventureData.Kind.ODYSSEY || s.kind == AdventureData.Kind.LIVING_REALM || s.kind == AdventureData.Kind.WARFRONT && n.role == AdventureData.Role.ESCORT || (EclipseRuns.is(s.kind) || RoyalRuns.is(s.kind)) && n.snapshot.m_128471_("NoAI") ? 0.0 : (n.role == AdventureData.Role.ESCORT && s.wave == 0 ? 32.0 : 5.0), s.kind == AdventureData.Kind.ODYSSEY || s.kind == AdventureData.Kind.LIVING_REALM || s.kind == AdventureData.Kind.WARFRONT && n.role == AdventureData.Role.ESCORT || (EclipseRuns.is(s.kind) || RoyalRuns.is(s.kind)) && n.snapshot.m_128471_("NoAI") ? 2.0 : (n.role == AdventureData.Role.ESCORT && s.wave == 0 ? 48.0 : 14.0), n.id.hashCode());
        if (at == null) {
            return false;
        }
        mob.m_7678_(at.f_82479_, at.f_82480_, at.f_82481_, 0.0f, 0.0f);
        mob.getPersistentData().m_128362_(OWNER, p.m_20148_());
        mob.getPersistentData().m_128362_(SESSION, s.id);
        mob.getPersistentData().m_128362_(NPC, n.id);
        mob.getPersistentData().m_128405_(EPOCH, s.epoch);
        mob.getPersistentData().m_128359_(ROLE, n.role.name());
        n.active = mob.m_20148_();
        n.pos = mob.m_20183_();
        if (!p.m_284548_().m_7967_((Entity)mob)) {
            n.active = null;
            return false;
        }
        AdventureWorld.decorate(mob, n.role);
        return true;
    }

    public static void decorate(Mob mob, AdventureData.Role role) {
        if (role == AdventureData.Role.ENEMY) {
            mob.f_21346_.m_148105_().stream().filter(g -> g.m_26015_() instanceof QuestTarget).toList().forEach(g -> mob.f_21346_.m_25363_(g.m_26015_()));
            mob.f_21346_.m_25352_(-20, (Goal)new QuestTarget(mob));
        }
        if (role == AdventureData.Role.ESCORT) {
            mob.f_21345_.m_148105_().stream().filter(g -> g.m_26015_() instanceof EscortFollow).toList().forEach(g -> mob.f_21345_.m_25363_(g.m_26015_()));
            mob.f_21345_.m_25352_(-20, (Goal)new EscortFollow(mob));
        }
        LegacyChallenges.decorate(mob);
        AscensionActors.decorate(mob);
        WarfrontActors.decorate(mob);
        RoyalMutators.decorate(mob);
        mob.m_21530_();
        mob.m_21553_(false);
        if (mob instanceof Wolf) {
            Wolf wolf = (Wolf)mob;
            wolf.m_21839_(false);
            wolf.m_21837_(false);
        }
    }

    public static boolean allowedTarget(Mob actor, LivingEntity target) {
        ServerPlayer p = AdventureWorld.owner((Entity)actor);
        AdventureData.Session s = AdventureWorld.session((Entity)actor);
        if (p == null || s == null || target == null || !target.m_6084_() || target.m_9236_() != actor.m_9236_()) {
            return false;
        }
        UUID owner = p.m_20148_();
        if (!(s.kind != AdventureData.Kind.ODYSSEY && s.kind != AdventureData.Kind.LIVING_REALM && s.kind != AdventureData.Kind.FERRYMAN && !EclipseRuns.is(s.kind) && !RoyalRuns.is(s.kind) || !ShadowService.isShadow((Entity)target) || target.getPersistentData().m_128403_("ShadowSovereignRecord") && s.participants.contains(target.getPersistentData().m_128342_("ShadowSovereignRecord")))) {
            return false;
        }
        if (s.kind == AdventureData.Kind.WARFRONT) {
            return WarfrontActors.allowed(actor, target);
        }
        if (AscensionService.activity(s.kind)) {
            return AscensionActors.allowed(actor, target);
        }
        return (target == p || owner.equals(ShadowService.ownerId((Entity)target)) || AdventureWorld.isNpc((Entity)target) && owner.equals(target.getPersistentData().m_128342_(OWNER)) && AdventureWorld.npc((Entity)target) != null && AdventureWorld.npc((Entity)target).role == AdventureData.Role.ESCORT) && target.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) <= 1600.0;
    }

    public static void tick(ServerPlayer p, AdventureData.Journal j) {
        AdventureData.Session s = j.session;
        if (s == null || s.phase != AdventureData.Phase.ACTIVE) {
            return;
        }
        if (s.kind == AdventureData.Kind.ODYSSEY || s.kind == AdventureData.Kind.LIVING_REALM || s.kind == AdventureData.Kind.MYTHIC_LAIR) {
            return;
        }
        if (s.kind == AdventureData.Kind.SOUL_PLAY) {
            return;
        }
        if (VigilRuns.is(s.kind)) {
            VigilRuns.tick(p, s);
            return;
        }
        if (RoyalRuns.is(s.kind)) {
            RoyalRuns.tick(p, s);
            return;
        }
        if (EclipseRuns.is(s.kind)) {
            EclipseRuns.tick(p, s);
            return;
        }
        if (s.kind == AdventureData.Kind.WARFRONT && !WarfrontService.validRun(s)) {
            WarfrontService.discard(p);
            return;
        }
        if (s.kind == AdventureData.Kind.WARFRONT ? !WarfrontService.validSite(p, s) : !j.bound || !s.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) || !p.m_284548_().m_46805_(j.sanctuary) || !p.m_284548_().m_8055_(j.sanctuary).m_60713_((Block)AdventureContent.SANCTUARY.get()) || p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) > 1600.0) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            AdventureService.tell(p, "Adventure paused. Return to your Sanctuary to resume.");
            return;
        }
        for (AdventureData.Npc n2 : s.npcs.values()) {
            Mob m;
            Entity entity;
            Mob mob;
            Mob mob2 = mob = n2.active != null && (entity = p.m_284548_().m_8791_(n2.active)) instanceof Mob ? (m = (Mob)entity) : null;
            if (mob == null) {
                if (n2.active != null) {
                    AdventureWorld.pause(p.f_8924_, p.m_20148_());
                    return;
                }
                if (AdventureWorld.spawn(p, s, n2)) continue;
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                AdventureService.tell(p, "Adventure paused: free safe space around the Sanctuary.");
                return;
            }
            n2.snapshot = AdventureWorld.snapshot(mob);
            n2.pos = mob.m_20183_();
            if (n2.role == AdventureData.Role.ESCORT && mob instanceof Wolf) {
                Wolf wolf = (Wolf)mob;
                wolf.m_21839_(false);
                wolf.m_21837_(false);
            }
            if (n2.role != AdventureData.Role.ESCORT) continue;
            p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123745_, mob.m_20185_(), mob.m_20186_() + 0.7, mob.m_20189_(), 2, 0.2, 0.2, 0.2, 0.01);
        }
        if (s.kind == AdventureData.Kind.WARFRONT) {
            WarfrontService.tick(p, s);
            return;
        }
        if (AscensionService.activity(s.kind)) {
            AscensionService.tick(p, s);
            return;
        }
        if (JourneyService.isJourney(s.kind)) {
            JourneyService.tick(p, s);
            return;
        }
        if (LegacyChallenges.legacy(s.kind)) {
            LegacyChallenges.tick(p, j, s);
            return;
        }
        long time = AdventureService.now(p.f_8924_);
        long enemies = s.npcs.values().stream().filter(n -> n.role == AdventureData.Role.ENEMY).count();
        if (s.kind == AdventureData.Kind.TRIAL) {
            if (enemies == 0L && s.initialized) {
                ++s.wave;
                s.initialized = false;
                s.nextWave = time + 100L;
                if (s.wave >= s.maxWaves()) {
                    AdventureWorld.ready(p, s);
                    return;
                }
            }
            if (!s.initialized && time >= s.nextWave) {
                int count = Math.min(12, 3 + s.tier + (s.participants.size() + 2) / 3);
                for (int i = 0; i < count; ++i) {
                    EntityType type = s.wave == s.maxWaves() - 1 && i == count - 1 ? EntityType.f_20518_ : (i % 3 == 0 ? EntityType.f_20501_ : (i % 3 == 1 ? EntityType.f_20524_ : EntityType.f_20479_));
                    AdventureData.Npc n3 = AdventureWorld.template(p, s, (EntityType<? extends Mob>)type, AdventureData.Role.ENEMY);
                    s.npcs.put(n3.id, n3);
                }
                s.initialized = true;
                AdventureService.tell(p, "Rift wave " + (s.wave + 1) + " / " + s.maxWaves());
            }
        } else {
            AdventureData.Npc escort = s.npcs.values().stream().filter(n -> n.role == AdventureData.Role.ESCORT).findFirst().orElse(null);
            if (escort == null) {
                AdventureWorld.cancel(p);
                AdventureService.tell(p, "The Lost Soul was lost. Your companions are safe; try another escort.");
                return;
            }
            if (enemies == 0L && s.initialized) {
                ++s.wave;
                s.initialized = false;
            }
            double distance = escort.pos.m_123331_((Vec3i)s.anchor);
            if (s.wave >= 2 && enemies == 0L && distance <= 36.0 && p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) <= 64.0) {
                AdventureWorld.ready(p, s);
                return;
            }
            if (!s.initialized && s.wave < 2) {
                int n4 = s.wave == 0 ? 1600 : 400;
                if (distance <= (double)n4) {
                    for (int i = 0; i < 3; ++i) {
                        AdventureData.Npc n5 = AdventureWorld.template(p, s, (EntityType<? extends Mob>)(i == 2 ? EntityType.f_20479_ : EntityType.f_20501_), AdventureData.Role.ENEMY);
                        n5.pos = escort.pos;
                        s.npcs.put(n5.id, n5);
                    }
                    s.initialized = true;
                    AdventureService.tell(p, "Lost Soul ambush " + (s.wave + 1) + " / 2!");
                }
            }
        }
        AdventureService.dirty(p);
    }

    public static void ready(ServerPlayer p, AdventureData.Session s) {
        GuideService.clear(p, s);
        s.phase = AdventureData.Phase.READY;
        AdventureWorld.clearEntities(p.f_8924_, s);
        if (AscensionService.activity(s.kind)) {
            AscensionService.completed(p, s);
        } else if (JourneyService.isJourney(s.kind)) {
            JourneyService.completed(p, s);
        } else if (LegacyChallenges.legacy(s.kind)) {
            LegacyChallenges.completed(p, s);
        } else if (s.kind == AdventureData.Kind.TRIAL) {
            AdventureService.trialWon(p, s);
        } else {
            AdventureData.Journal j = AdventureService.journal(p);
            j.titles.add(AdventureData.Title.GUARDIAN);
            j.trails.add(AdventureData.Trail.AZURE);
        }
        AdventureService.dirty(p);
        AdventureService.tell(p, "Adventure complete! Claim your reward in the journal.");
    }

    public static boolean claim(ServerPlayer p) {
        if (MythicService.session(p) != null) {
            return false;
        }
        if (LivingRealm.session(p) != null) {
            return LivingRealm.finish(p, true);
        }
        if (OdysseyRuns.session(p) != null) {
            return OdysseyRuns.finish(p, true);
        }
        if (SoulPlay.session(p)) {
            return false;
        }
        if (VigilRuns.session(p) != null) {
            return VigilRuns.finish(p, true);
        }
        if (RoyalRuns.session(p) != null) {
            return RoyalRuns.finish(p, true);
        }
        if (EclipseRuns.session(p) != null) {
            return EclipseRuns.finish(p, true);
        }
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.session == null || j.session.phase != AdventureData.Phase.READY) {
            return false;
        }
        if (j.session.kind == AdventureData.Kind.WARFRONT) {
            return WarfrontService.claim(p);
        }
        if (AscensionService.activity(j.session.kind)) {
            return AscensionService.claim(p);
        }
        if (JourneyService.isJourney(j.session.kind)) {
            return JourneyService.claim(p);
        }
        UUID id = j.session.id;
        j.session = null;
        AdventureService.reward(p, j, id, "Adventure reward claimed");
        return true;
    }

    public static boolean cancel(ServerPlayer p) {
        if (MythicService.session(p) != null) {
            return MythicService.cancel(p);
        }
        if (LivingRealm.session(p) != null) {
            return LivingRealm.finish(p, false);
        }
        if (OdysseyRuns.session(p) != null) {
            return OdysseyRuns.finish(p, false);
        }
        if (SoulPlay.session(p)) {
            return SoulPlay.cancel(p, false);
        }
        if (VigilRuns.session(p) != null) {
            return VigilRuns.finish(p, false);
        }
        if (RoyalRuns.session(p) != null) {
            return RoyalRuns.finish(p, false);
        }
        if (EclipseRuns.session(p) != null) {
            return EclipseRuns.finish(p, false);
        }
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.session == null) {
            return false;
        }
        AdventureData.Session s = j.session;
        if (s.kind == AdventureData.Kind.WARFRONT) {
            return WarfrontService.abandon(p);
        }
        JourneyService.canceled(p, s);
        LegacyChallenges.canceled(p, s);
        s.phase = AdventureData.Phase.PAUSED;
        AdventureWorld.clearEntities(p.f_8924_, s);
        j.session = null;
        AdventureService.dirty(p);
        return true;
    }

    public static void pause(MinecraftServer server, UUID owner) {
        AdventureData data = AdventureData.get(server);
        AdventureData.Journal j = data.owners.get(owner);
        if (j == null || j.session == null || j.session.phase != AdventureData.Phase.ACTIVE) {
            return;
        }
        AdventureData.Session s = j.session;
        if (s.kind == AdventureData.Kind.MYTHIC_LAIR) {
            MythicService.pause(server, owner);
            return;
        }
        if (s.kind == AdventureData.Kind.ODYSSEY) {
            ServerPlayer op = server.m_6846_().m_11259_(owner);
            if (op != null) {
                OdysseyRuns.pause(op);
            } else {
                s.phase = AdventureData.Phase.PAUSED;
                AdventureWorld.clearEntities(server, s);
                data.m_77762_();
            }
            return;
        }
        if (s.kind == AdventureData.Kind.SOUL_PLAY) {
            ServerPlayer sp = server.m_6846_().m_11259_(owner);
            if (sp != null) {
                SoulPlay.cancel(sp, false);
            } else {
                s.phase = AdventureData.Phase.PAUSED;
                data.m_77762_();
            }
            return;
        }
        if (VigilRuns.is(s.kind)) {
            s.vigil.m_128356_("PausedAt", AdventureService.now(server));
        }
        if (RoyalRuns.is(s.kind)) {
            ServerPlayer rp = server.m_6846_().m_11259_(owner);
            s.royal.m_128356_("PausedAt", AdventureService.now(server));
            if (rp != null) {
                RoyalRuns.paused(rp, s);
            }
        }
        if (EclipseRuns.is(s.kind)) {
            s.eclipse.m_128356_("PausedAt", AdventureService.now(server));
            ServerPlayer ownerPlayer = server.m_6846_().m_11259_(owner);
            if (ownerPlayer != null) {
                EclipseRuns.pauseBoons(ownerPlayer, s);
            }
        }
        if (s.kind == AdventureData.Kind.WARFRONT) {
            s.warfront.m_128356_("PausedAt", AdventureService.now(server));
            ServerPlayer p = server.m_6846_().m_11259_(owner);
            if (p != null) {
                WarfrontPlans.stop(p);
            }
        }
        AscensionService.paused(server, s);
        JourneyService.paused(server, s);
        LegacyChallenges.paused(server, s);
        s.phase = AdventureData.Phase.PAUSED;
        ++s.epoch;
        AdventureWorld.clearEntities(server, s);
        data.m_77762_();
    }

    public static void clearEntities(MinecraftServer server, AdventureData.Session s) {
        if (AscensionService.activity(s.kind)) {
            JourneyCombat.removeBar(s.id);
        }
        JourneyService.cleanup(server, s);
        LegacyChallenges.cleanup(server, s);
        ServerLevel level = AdventureWorld.level(server, s.dimension);
        for (AdventureData.Npc n : s.npcs.values()) {
            Mob m;
            Entity entity;
            Mob mob;
            Mob mob2 = mob = level != null && n.active != null && (entity = level.m_8791_(n.active)) instanceof Mob ? (m = (Mob)entity) : null;
            if (mob != null && mob.m_21223_() > 0.0f) {
                n.snapshot = AdventureWorld.snapshot(mob);
                n.pos = mob.m_20183_();
            }
            n.active = null;
            if (mob == null) continue;
            mob.m_146870_();
        }
        if (level != null) {
            ArrayList<Entity> projectiles = new ArrayList<Entity>();
            level.m_8583_().forEach(e -> {
                if (!(e instanceof Mob) && AdventureWorld.isNpc(e) && e.getPersistentData().m_128403_(SESSION) && s.id.equals(e.getPersistentData().m_128342_(SESSION))) {
                    projectiles.add((Entity)e);
                }
            });
            projectiles.forEach(Entity::m_146870_);
        }
    }

    public static boolean resume(ServerPlayer p) {
        if (MythicService.session(p) != null) {
            return MythicService.resume(p);
        }
        if (LivingRealm.session(p) != null) {
            return LivingRealm.resume(p);
        }
        if (OdysseyRuns.session(p) != null) {
            return OdysseyRuns.resume(p);
        }
        if (SoulPlay.session(p)) {
            return false;
        }
        if (VigilRuns.session(p) != null) {
            return VigilRuns.resume(p);
        }
        if (RoyalRuns.session(p) != null) {
            return RoyalRuns.resume(p);
        }
        if (EclipseRuns.session(p) != null) {
            return EclipseRuns.resume(p);
        }
        AdventureData.Session warfront = WarfrontService.session(p);
        if (warfront != null) {
            return WarfrontService.resume(p, warfront);
        }
        if (!AdventureService.requireHub(p)) {
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        AdventureData.Session s = j.session;
        if (s != null && !JourneyService.peaceful(s.kind) && s.kind != AdventureData.Kind.RUSH && s.kind != AdventureData.Kind.VAULT && p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            return false;
        }
        if (s == null || s.phase != AdventureData.Phase.PAUSED) {
            return false;
        }
        AscensionService.resumed(p, s);
        JourneyService.resumed(p, s);
        LegacyChallenges.resumed(p, s);
        s.anchor = j.sanctuary;
        s.dimension = j.dimension;
        ++s.epoch;
        s.phase = AdventureData.Phase.ACTIVE;
        s.npcs.values().forEach(n -> {
            n.active = null;
        });
        AdventureService.dirty(p);
        return true;
    }

    public static void npcDied(Mob mob) {
        AdventureData.Session s = AdventureWorld.session((Entity)mob);
        AdventureData.Npc n = AdventureWorld.npc((Entity)mob);
        if (!AdventureWorld.valid((Entity)mob) || s == null || n == null) {
            return;
        }
        if (s.kind == AdventureData.Kind.ODYSSEY || s.kind == AdventureData.Kind.LIVING_REALM) {
            s.npcs.remove(n.id);
            AdventureData.get(mob.m_20194_()).m_77762_();
            return;
        }
        if (s.kind == AdventureData.Kind.WARFRONT) {
            ServerPlayer p;
            WarfrontService.npcDied(mob, s, n);
            s.npcs.remove(n.id);
            AdventureData.get(mob.m_20194_()).m_77762_();
            if (n.role == AdventureData.Role.ESCORT && (p = AdventureWorld.owner((Entity)mob)) != null) {
                WarfrontService.fail(p, "The prisoner fell.");
            }
            return;
        }
        JourneyService.npcDied(mob, s, n);
        s.npcs.remove(n.id);
        AdventureData.get(mob.m_20194_()).m_77762_();
        if (n.role == AdventureData.Role.ESCORT) {
            ServerPlayer p = AdventureWorld.owner((Entity)mob);
            if (p != null) {
                AdventureWorld.cancel(p);
                AdventureService.tell(p, "Escort failed. Your army and previous progress remain safe.");
            } else {
                AdventureData.Journal j = AdventureData.get(mob.m_20194_()).journal(mob.getPersistentData().m_128342_(OWNER));
                s.phase = AdventureData.Phase.PAUSED;
                AdventureWorld.clearEntities(mob.m_20194_(), s);
                j.session = null;
            }
        }
    }

    public static void npcLeft(Mob mob) {
        if (!AdventureWorld.valid((Entity)mob)) {
            return;
        }
        AdventureData.Npc n = AdventureWorld.npc((Entity)mob);
        if (n != null && mob.m_21223_() > 0.0f) {
            n.snapshot = AdventureWorld.snapshot(mob);
            n.pos = mob.m_20183_();
        }
        AdventureWorld.pause(mob.m_20194_(), mob.getPersistentData().m_128342_(OWNER));
    }

    public static boolean rescue(Mob mob) {
        AdventureData.Npc n = AdventureWorld.npc((Entity)mob);
        AdventureData.Session s = AdventureWorld.session((Entity)mob);
        ServerPlayer p = AdventureWorld.owner((Entity)mob);
        if (!AdventureWorld.valid((Entity)mob) || n == null || n.role != AdventureData.Role.ESCORT || s == null || s.wardenUsed || p == null) {
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        boolean warden = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().anyMatch(r -> {
            AdventureData.Lore lore = j.lore.get(r.id);
            Mob ally = ShadowService.active(p.f_8924_, r);
            return lore != null && lore.relic == AdventureData.Relic.WARDEN && ally != null && ally.m_6084_() && ally.m_9236_() == mob.m_9236_() && ally.m_20280_((Entity)mob) <= 256.0;
        });
        if (!warden) {
            return false;
        }
        s.wardenUsed = true;
        mob.m_21153_(1.0f);
        n.snapshot = AdventureWorld.snapshot(mob);
        AdventureService.dirty(p);
        AdventureService.tell(p, "Warden saved the Lost Soul! This escort's rescue has been spent.");
        return true;
    }

    private static final class QuestTarget
    extends Goal {
        private final Mob mob;

        QuestTarget(Mob mob) {
            this.mob = mob;
            this.m_7021_(EnumSet.of(Goal.Flag.TARGET));
        }

        public boolean m_8036_() {
            return AdventureWorld.valid((Entity)this.mob);
        }

        public boolean m_8045_() {
            return this.m_8036_();
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            if (this.mob.f_19797_ % 10 != 0) {
                return;
            }
            ServerPlayer p = AdventureWorld.owner((Entity)this.mob);
            if (p == null) {
                return;
            }
            ArrayList<Object> choices = new ArrayList<Object>();
            if (AdventureWorld.allowedTarget(this.mob, (LivingEntity)p) && !p.m_7500_() && !p.m_5833_()) {
                choices.add(p);
            }
            for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
                Mob ally = ShadowService.active(p.f_8924_, r);
                if (ally == null || !AdventureWorld.allowedTarget(this.mob, (LivingEntity)ally)) continue;
                choices.add(ally);
            }
            AdventureData.Session s = AdventureWorld.session((Entity)this.mob);
            if (s != null) {
                for (AdventureData.Npc n : s.npcs.values()) {
                    LivingEntity escort;
                    ServerLevel level;
                    Level level2;
                    if (n.role != AdventureData.Role.ESCORT || n.active == null || !((level2 = this.mob.m_9236_()) instanceof ServerLevel) || !((level2 = (level = (ServerLevel)level2).m_8791_(n.active)) instanceof LivingEntity) || !AdventureWorld.allowedTarget(this.mob, escort = (LivingEntity)level2)) continue;
                    choices.add(escort);
                }
            }
            if (s != null && s.kind == AdventureData.Kind.SIEGE) {
                choices.removeIf(e -> e.m_20280_((Entity)this.mob) > 36.0);
            }
            if (s != null && s.kind == AdventureData.Kind.WARFRONT) {
                for (Mob e2 : this.mob.m_9236_().m_45976_(Mob.class, this.mob.m_20191_().m_82400_(8.0))) {
                    if (!WarfrontActors.illusion((Entity)e2) || !AdventureWorld.allowedTarget(this.mob, (LivingEntity)e2)) continue;
                    choices.add(e2);
                }
            }
            LivingEntity target = choices.stream().min(Comparator.comparingDouble(arg_0 -> ((Mob)this.mob).m_20280_(arg_0))).orElse(null);
            this.mob.m_6710_(target);
            if (this.mob.m_6274_().m_147339_().containsKey(MemoryModuleType.f_26372_)) {
                if (target == null) {
                    this.mob.m_6274_().m_21936_(MemoryModuleType.f_26372_);
                } else {
                    this.mob.m_6274_().m_21879_(MemoryModuleType.f_26372_, (Object)target);
                }
            }
        }
    }

    private static final class EscortFollow
    extends Goal {
        private final Mob mob;

        EscortFollow(Mob mob) {
            this.mob = mob;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            return AdventureWorld.valid((Entity)this.mob);
        }

        public boolean m_8045_() {
            return this.m_8036_();
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            ServerPlayer p = AdventureWorld.owner((Entity)this.mob);
            if (p == null) {
                return;
            }
            if (AdventureWorld.session((Entity)this.mob) != null && AdventureWorld.session((Entity)this.mob).kind == AdventureData.Kind.LIVING_REALM) {
                return;
            }
            double distance = this.mob.m_20280_((Entity)p);
            if (distance > 9.0 && distance <= 1024.0) {
                if (this.mob.f_19797_ % 10 == 0) {
                    this.mob.m_21573_().m_5624_((Entity)p, 1.1);
                }
                this.mob.m_21563_().m_24960_((Entity)p, 20.0f, 20.0f);
            } else {
                this.mob.m_21573_().m_26573_();
            }
        }
    }
}

