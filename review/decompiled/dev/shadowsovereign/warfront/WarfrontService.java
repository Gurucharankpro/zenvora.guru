/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.warfront;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.sovereign.SovereignService;
import dev.shadowsovereign.sovereign.SovereignState;
import dev.shadowsovereign.warfront.WarfrontContent;
import dev.shadowsovereign.warfront.WarfrontPlans;
import dev.shadowsovereign.warfront.WarfrontState;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class WarfrontService {
    public static final String[] TERRITORIES = new String[]{"Veil Outskirts", "Ash March", "Frost Hollow", "Ash Citadel", "Moon Keep", "Eclipse Throne"};
    public static final String[] MISSIONS = new String[]{"Lieutenant Hunt", "Ward Sabotage", "Prisoner Rescue", "Commander Stronghold"};
    public static final EntityType<?>[] COMMANDERS = new EntityType[]{EntityType.f_20501_, EntityType.f_20524_, EntityType.f_20458_};

    public static WarfrontState state(ServerPlayer p) {
        return ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).warfront;
    }

    public static long now(ServerPlayer p) {
        return p.f_8924_.m_129783_().m_46467_();
    }

    public static void dirty(ServerPlayer p) {
        ArmyData.get(p.f_8924_).m_77762_();
        AdventureData.get(p.f_8924_).m_77762_();
    }

    public static void tell(ServerPlayer p, String message) {
        ShadowService.tell(p, message);
    }

    public static AdventureData.Session session(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && s.kind == AdventureData.Kind.WARFRONT ? s : null;
    }

    public static UUID beaconOwner(MinecraftServer server, String dim, BlockPos at) {
        return ArmyData.get(server).owners().entrySet().stream().filter(e -> ((ArmyData.Army)e.getValue()).warfront.bound && ((ArmyData.Army)e.getValue()).warfront.dimension.equals(dim) && ((ArmyData.Army)e.getValue()).warfront.beacon.equals((Object)at)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public static boolean near(ServerPlayer p) {
        WarfrontState w = WarfrontService.state(p);
        return w.bound && w.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) && p.m_284548_().m_46805_(w.beacon) && p.m_9236_().m_8055_(w.beacon).m_60713_((Block)WarfrontContent.BEACON.get()) && p.m_20238_(Vec3.m_82512_((Vec3i)w.beacon)) <= 64.0;
    }

    public static boolean workshop(ServerPlayer p) {
        return WarfrontService.near(p) || AdventureService.nearHub(p);
    }

    public static boolean bind(ServerPlayer p, BlockPos at) {
        if (!p.m_6084_() || p.m_5833_() || !p.m_284548_().m_46805_(at) || !p.m_9236_().m_8055_(at).m_60713_((Block)WarfrontContent.BEACON.get())) {
            return false;
        }
        UUID owner = WarfrontService.beaconOwner(p.f_8924_, p.m_9236_().m_46472_().m_135782_().toString(), at);
        if (owner != null && !owner.equals(p.m_20148_())) {
            return false;
        }
        WarfrontState w = WarfrontService.state(p);
        if (WarfrontService.session(p) != null) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
        }
        w.bound = true;
        w.beacon = at.m_7949_();
        w.dimension = p.m_9236_().m_46472_().m_135782_().toString();
        if (w.seed == 0L) {
            w.seed = p.m_217043_().m_188505_() | 1L;
        }
        WarfrontService.dirty(p);
        WarfrontService.tell(p, "Warfront Beacon bound. Your campaign is ready.");
        return true;
    }

    public static void removed(Level level, BlockPos at) {
        MinecraftServer server = level.m_7654_();
        if (server == null) {
            return;
        }
        UUID owner = WarfrontService.beaconOwner(server, level.m_46472_().m_135782_().toString(), at);
        if (owner == null) {
            return;
        }
        WarfrontState w = ArmyData.get((MinecraftServer)server).army((UUID)owner).warfront;
        AdventureData.Journal journal = AdventureData.get((MinecraftServer)server).owners.get(owner);
        if (journal != null && journal.session != null && journal.session.kind == AdventureData.Kind.WARFRONT) {
            AdventureWorld.pause(server, owner);
        }
        w.bound = false;
        ArmyData.get(server).m_77762_();
    }

    public static boolean validSite(ServerPlayer p, AdventureData.Session s) {
        WarfrontState w = WarfrontService.state(p);
        return w.bound && s.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) && s.dimension.equals(w.dimension) && s.anchor.equals((Object)w.beacon) && p.m_284548_().m_46805_(w.beacon) && p.m_9236_().m_8055_(w.beacon).m_60713_((Block)WarfrontContent.BEACON.get()) && p.m_20238_(Vec3.m_82512_((Vec3i)w.beacon)) <= 1600.0;
    }

    public static boolean validRun(AdventureData.Session s) {
        CompoundTag n = s.warfront;
        return n.m_128441_("Pact") && n.m_128441_("Deadline") && n.m_128454_("Chapter") >= 1L && n.m_128451_("Node") >= 0 && n.m_128451_("Node") < 6 && n.m_128451_("Rival") >= 0 && n.m_128451_("Rival") < 3 && n.m_128451_("Mission") >= 0 && n.m_128451_("Mission") < 4 && n.m_128451_("Pact") >= -1 && n.m_128451_("Pact") < 3 && n.m_128437_("Points", 10).size() == 3 && (n.m_128451_("Mission") != 0 && n.m_128451_("Mission") != 3 || n.m_128403_("Marked"));
    }

    public static void reconcile(ServerPlayer p) {
        WarfrontState w = WarfrontService.state(p);
        AdventureData.Session s = WarfrontService.session(p);
        if (s != null && (!WarfrontService.validRun(s) || s.phase != AdventureData.Phase.READY && !s.id.equals(w.activeMission))) {
            WarfrontService.discard(p);
            WarfrontService.tell(p, "An interrupted encounter was cleared. Your campaign progress is safe.");
        } else if (s == null && w.activeMission != null) {
            w.activeMission = null;
            WarfrontService.dirty(p);
        }
    }

    public static boolean start(ServerPlayer p, int node, boolean hard, List<UUID> selected) {
        return WarfrontService.begin(p, node, hard, selected, -1);
    }

    public static boolean pactMission(ServerPlayer p, int rival, boolean hard, List<UUID> ids) {
        WarfrontState w = WarfrontService.state(p);
        if (rival < 0 || rival > 2 || (w.defeated & 1 << rival) == 0 || (w.pactReady & 1 << rival) != 0 || (w.pactClaimed & 1 << rival) != 0) {
            return false;
        }
        return WarfrontService.begin(p, rival == 0 ? 3 : (rival == 1 ? 4 : 5), hard, ids, rival);
    }

    private static boolean begin(ServerPlayer p, int node, boolean hard, List<UUID> selected, int pact) {
        WarfrontService.reconcile(p);
        WarfrontState w = WarfrontService.state(p);
        if (!p.m_6084_() || p.m_5833_() || !WarfrontService.near(p) || AdventureService.journal((ServerPlayer)p).session != null || p.m_9236_().m_46791_() == Difficulty.PEACEFUL || node < 0 || node > 5 || pact < 0 && !w.available(node)) {
            return false;
        }
        ArmyData.Army a = ArmyData.get(p.f_8924_).army(p.m_20148_());
        List<ShadowRecord> eligible = a.shadows.values().stream().filter(r -> {
            Mob m = ShadowService.active(p.f_8924_, r);
            return m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 1024.0 && !AdventureService.busy(p.f_8924_, p.m_20148_(), r.id);
        }).toList();
        if (selected.size() > 3 || new HashSet<UUID>(selected).size() != selected.size() || selected.stream().anyMatch(id -> eligible.stream().noneMatch(r -> r.id.equals(id)))) {
            return false;
        }
        List<ShadowRecord> party = eligible.stream().filter(r -> selected.isEmpty() || selected.contains(r.id)).limit(3L).toList();
        if (party.isEmpty()) {
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = AdventureData.Kind.WARFRONT;
        s.phase = AdventureData.Phase.ACTIVE;
        s.anchor = w.beacon;
        s.dimension = w.dimension;
        party.forEach(r -> s.participants.add(r.id));
        CompoundTag n = s.warfront;
        n.m_128405_("Node", node);
        n.m_128405_("Rival", pact >= 0 ? pact : WarfrontState.rival(node));
        n.m_128405_("Mission", pact >= 0 ? 3 : w.mission(node));
        n.m_128405_("Pact", pact);
        n.m_128379_("Hard", hard);
        n.m_128356_("Chapter", w.chapter);
        n.m_128356_("Started", WarfrontService.now(p));
        n.m_128356_("Clock", WarfrontService.now(p));
        n.m_128356_("Deadline", WarfrontService.now(p) + (long)(hard ? 7200 : 9600));
        n.m_128356_("NextSpawn", WarfrontService.now(p) + 400L);
        n.m_128356_("NextStrike", WarfrontService.now(p) + 200L);
        n.m_128359_("Adaptation", w.adaptations[n.m_128451_("Rival")].name());
        n.m_128347_("Power", party.stream().mapToDouble(r -> r.tier.power * r.damageMultiplier()).sum());
        ListTag points = new ListTag();
        for (int i = 0; i < 3; ++i) {
            Vec3 at = AdventureWorld.spot(p.m_284548_(), EntityType.f_20499_, s.anchor, 16.0, 26.0, w.seed + (long)(node * 71) + (long)(i * 137));
            if (at == null) {
                return false;
            }
            CompoundTag e = new CompoundTag();
            e.m_128356_("Pos", BlockPos.m_274446_((Position)at).m_121878_());
            points.add((Object)e);
        }
        n.m_128365_("Points", (Tag)points);
        if (AdventureWorld.spot(p.m_284548_(), EntityType.f_20501_, s.anchor, 6.0, 14.0, s.id.hashCode()) == null) {
            return false;
        }
        int mission = n.m_128451_("Mission");
        for (int i = 0; i < (mission == 3 ? 5 : 5); ++i) {
            WarfrontService.add(p, s, mission == 3 && i == 0 ? COMMANDERS[n.m_128451_("Rival")] : (i % 3 == 0 ? EntityType.f_20524_ : (i % 3 == 1 ? EntityType.f_20501_ : EntityType.f_20479_)), AdventureData.Role.ENEMY, i == 0);
        }
        if (mission == 2) {
            AdventureData.Npc e = WarfrontService.add(p, s, EntityType.f_20499_, AdventureData.Role.ESCORT, false);
            e.pos = BlockPos.m_274446_((Position)WarfrontService.point(s, 2));
        }
        for (ShadowRecord r2 : List.copyOf(a.shadows.values())) {
            if (s.participants.contains(r2.id)) continue;
            ShadowService.recall(p, r2);
        }
        WarfrontPlans.stop(p);
        AdventureService.journal((ServerPlayer)p).session = s;
        w.activeMission = s.id;
        WarfrontService.dirty(p);
        WarfrontService.tell(p, (pact >= 0 ? "Pact rematch: " : "Warfront: ") + TERRITORIES[node] + " \u2014 " + MISSIONS[mission] + ". G interacts. Other companions wait in reserve.");
        return true;
    }

    public static AdventureData.Npc add(ServerPlayer p, AdventureData.Session s, EntityType<?> type, AdventureData.Role role, boolean marked) {
        AdventureData.Npc npc = AdventureWorld.template(p, s, type, role);
        Mob mob = (Mob)type.m_20615_((Level)p.m_284548_());
        mob.m_20258_(npc.snapshot);
        if (role == AdventureData.Role.ENEMY) {
            double score = s.warfront.m_128459_("Power");
            double chapter = 1.0 + (double)Math.min(2L, WarfrontService.state((ServerPlayer)p).chapter - 1L) * 0.1;
            boolean boss = marked && s.warfront.m_128451_("Mission") == 3;
            double hp = Math.min(boss ? 1000.0 : 256.0, (boss ? 240.0 + score * 50.0 : 20.0 + score * 8.0) * chapter * (s.warfront.m_128471_("Hard") ? 1.15 : 0.85));
            mob.m_21051_(Attributes.f_22276_).m_22100_(hp);
            mob.m_21153_(mob.m_21233_());
            AttributeInstance attack = mob.m_21051_(Attributes.f_22281_);
            if (attack != null) {
                attack.m_22100_(attack.m_22115_() * chapter * (s.warfront.m_128471_("Hard") ? 1.0 : 0.75));
            }
            if (marked) {
                s.warfront.m_128362_("Marked", npc.id);
                mob.m_6593_((Component)Component.m_237113_((String)(boss ? WarfrontService.state(p).rivalName(s.warfront.m_128451_("Rival")) : "Veil Lieutenant")));
            }
        }
        if (role == AdventureData.Role.ESCORT) {
            mob.m_6593_((Component)Component.m_237113_((String)"Bound Wayfarer"));
            mob.m_21051_(Attributes.f_22276_).m_22100_(40.0);
            mob.m_21153_(40.0f);
        }
        npc.snapshot = AdventureWorld.snapshot(mob);
        s.npcs.put(npc.id, npc);
        return npc;
    }

    public static Vec3 point(AdventureData.Session s, int i) {
        return Vec3.m_82539_((Vec3i)BlockPos.m_122022_((long)s.warfront.m_128437_("Points", 10).m_128728_(Math.max(0, Math.min(2, i))).m_128454_("Pos")));
    }

    public static boolean interact(ServerPlayer p) {
        Mob m2;
        Entity entity;
        AdventureData.Npc escort;
        if (WarfrontService.state((ServerPlayer)p).placementUntil >= WarfrontService.now(p) && WarfrontService.state((ServerPlayer)p).placementUntil > 0L) {
            return WarfrontPlans.place(p);
        }
        AdventureData.Session s = WarfrontService.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE || !WarfrontService.validSite(p, s)) {
            return false;
        }
        CompoundTag n = s.warfront;
        if (n.m_128451_("Mission") == 2 && !n.m_128471_("Released") && (escort = (AdventureData.Npc)s.npcs.values().stream().filter(e -> e.role == AdventureData.Role.ESCORT).findFirst().orElse(null)) != null && escort.active != null && (entity = p.m_284548_().m_8791_(escort.active)) instanceof Mob && (m2 = (Mob)entity).m_20280_((Entity)p) <= 16.0 && p.m_142582_((Entity)m2)) {
            n.m_128356_("ReleaseAt", WarfrontService.now(p) + 120L);
            WarfrontService.tell(p, "Hold this position for six seconds to free the prisoner.");
            WarfrontService.dirty(p);
            return true;
        }
        if (n.m_128451_("Mission") == 1) {
            for (int i = 0; i < 3; ++i) {
                if ((n.m_128451_("Disabled") & 1 << i) != 0 || !(p.m_20238_(WarfrontService.point(s, i)) <= 9.0) || p.m_9236_().m_45547_(new ClipContext(p.m_146892_(), WarfrontService.point(s, i).m_82520_(0.0, 0.5, 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)p)).m_6662_() != HitResult.Type.MISS) continue;
                int index = i;
                boolean ally = s.participants.stream().map(id -> WarfrontService.a(p, id)).anyMatch(m -> m != null && m.m_20238_(WarfrontService.point(s, index)) <= 25.0);
                if (!ally) {
                    WarfrontService.tell(p, "Bring a companion within five blocks of this ward.");
                    return false;
                }
                n.m_128405_("Disabled", n.m_128451_("Disabled") | 1 << i);
                WarfrontService.dirty(p);
                WarfrontService.tell(p, "Enemy ward disabled: " + Integer.bitCount(n.m_128451_("Disabled")) + " / 3.");
                return true;
            }
        }
        return false;
    }

    public static Mob a(ServerPlayer p, UUID id) {
        ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
        Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
        return m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() ? m : null;
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        int mission;
        WarfrontState w = WarfrontService.state(p);
        CompoundTag n = s.warfront;
        if (!s.id.equals(w.activeMission) || n.m_128454_("Chapter") != w.chapter) {
            WarfrontService.discard(p);
            return;
        }
        n.m_128356_("Clock", WarfrontService.now(p));
        if (WarfrontService.now(p) >= n.m_128454_("Deadline")) {
            WarfrontService.fail(p, "Mission time expired.");
            return;
        }
        if (s.participants.stream().noneMatch(id -> WarfrontService.a(p, id) != null)) {
            boolean fallen = s.participants.stream().allMatch(id -> {
                ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
                return r == null || r.pendingRespawn();
            });
            if (fallen) {
                WarfrontService.fail(p, "Your participating army fell.");
            } else {
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                WarfrontService.tell(p, "Mission paused. Summon a selected companion before resuming.");
            }
            return;
        }
        n.m_128405_("Samples", Math.min(1000000, n.m_128451_("Samples") + 1));
        List<Mob> allies = s.participants.stream().map(id -> WarfrontService.a(p, id)).filter(Objects::nonNull).toList();
        if (allies.size() > 1 && allies.stream().allMatch(m -> m.m_20280_((Entity)allies.get(0)) <= 36.0)) {
            n.m_128405_("Clustered", n.m_128451_("Clustered") + 1);
        }
        if ((mission = n.m_128451_("Mission")) == 0 && n.m_128471_("MarkedDown") || mission == 1 && n.m_128451_("Disabled") == 7 || mission == 3 && n.m_128471_("MarkedDown")) {
            WarfrontService.win(p, s);
            return;
        }
        if (mission == 1) {
            int i;
            for (i = 0; i < 3; ++i) {
                if ((n.m_128451_("Disabled") & 1 << i) != 0) continue;
                Vec3 at = WarfrontService.point(s, i);
                p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123809_, true, at.f_82479_, at.f_82480_ + 1.0, at.f_82481_, 8, 0.5, 1.0, 0.5, 0.02);
            }
            if (WarfrontService.now(p) >= n.m_128454_("NextSpawn")) {
                for (i = 0; i < 2 && s.npcs.size() < 9; ++i) {
                    WarfrontService.add(p, s, EntityType.f_20501_, AdventureData.Role.ENEMY, false);
                }
                n.m_128356_("NextSpawn", WarfrontService.now(p) + 400L);
            }
        }
        if (mission == 2) {
            Entity entity;
            AdventureData.Npc npc = s.npcs.values().stream().filter(e -> e.role == AdventureData.Role.ESCORT).findFirst().orElse(null);
            if (npc == null) {
                WarfrontService.fail(p, "The prisoner was lost.");
                return;
            }
            Entity entity2 = entity = npc.active == null ? null : p.m_284548_().m_8791_(npc.active);
            if (entity instanceof Mob) {
                Mob escort = (Mob)entity;
                if (!n.m_128471_("Released") && n.m_128454_("ReleaseAt") > 0L) {
                    if (escort.m_20280_((Entity)p) > 16.0) {
                        n.m_128473_("ReleaseAt");
                    } else if (WarfrontService.now(p) >= n.m_128454_("ReleaseAt")) {
                        n.m_128379_("Released", true);
                        WarfrontService.tell(p, "Prisoner freed! Escort the Wayfarer to your Beacon.");
                    }
                }
                if (n.m_128471_("Released") && escort.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) <= 36.0 && p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) <= 64.0) {
                    WarfrontService.win(p, s);
                    return;
                }
            }
        }
        if (mission == 3) {
            WarfrontService.boss(p, s);
        }
        WarfrontService.dirty(p);
    }

    private static void boss(ServerPlayer p, AdventureData.Session s) {
        Entity entity;
        CompoundTag n = s.warfront;
        AdventureData.Npc npc = s.npcs.get(n.m_128342_("Marked"));
        if (npc == null || npc.active == null || !((entity = p.m_284548_().m_8791_(npc.active)) instanceof Mob)) {
            return;
        }
        Mob boss = (Mob)entity;
        boolean scatter = n.m_128461_("Adaptation").equals("SCATTER");
        long time = WarfrontService.now(p);
        if (time >= n.m_128454_("NextStrike") && !n.m_128441_("StrikeAt")) {
            Mob target = s.participants.stream().map(id -> WarfrontService.a(p, id)).filter(Objects::nonNull).findFirst().orElse(null);
            Vec3 at = scatter && target != null ? target.m_20182_() : p.m_20182_();
            n.m_128347_("StrikeX", at.f_82479_);
            n.m_128347_("StrikeY", at.f_82480_);
            n.m_128347_("StrikeZ", at.f_82481_);
            n.m_128356_("StrikeAt", time + 40L);
            WarfrontService.tell(p, scatter ? "Scatter! Leave the violet warning circle." : "Rival sigil charging \u2014 move out of the warning circle!");
        }
        if (n.m_128441_("StrikeAt")) {
            double x = n.m_128459_("StrikeX");
            double y = n.m_128459_("StrikeY");
            double z = n.m_128459_("StrikeZ");
            for (int i = 0; i < 20; ++i) {
                double angle = (double)i * Math.PI / 10.0;
                p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123771_, true, x + Math.cos(angle) * 3.0, y + 0.2, z + Math.sin(angle) * 3.0, 1, 0.0, 0.0, 0.0, 0.0);
            }
            if (time >= n.m_128454_("StrikeAt")) {
                ArrayList<ServerPlayer> targets = new ArrayList<ServerPlayer>();
                targets.add(p);
                s.participants.stream().map(id -> WarfrontService.a(p, id)).filter(Objects::nonNull).forEach(targets::add);
                for (LivingEntity livingEntity : targets) {
                    Vec3 vec3 = new Vec3(x, y, z);
                    if (!(livingEntity.m_20238_(vec3) <= 9.0)) continue;
                    livingEntity.m_6469_(boss.m_269291_().m_269104_((Entity)boss, (Entity)boss), n.m_128471_("PhaseTwo") ? 7.0f : 5.0f);
                }
                n.m_128473_("StrikeAt");
                n.m_128356_("NextStrike", time + (long)(n.m_128471_("PhaseTwo") ? 180 : 240));
            }
        }
        if (n.m_128471_("PhaseTwo") && !n.m_128471_("Reinforced")) {
            n.m_128379_("Reinforced", true);
            for (int i = 0; i < 2 && s.npcs.size() < 9; ++i) {
                WarfrontService.add(p, s, EntityType.f_20501_, AdventureData.Role.ENEMY, false);
            }
        }
    }

    public static void npcDied(Mob mob, AdventureData.Session s, AdventureData.Npc npc) {
        if (s.warfront.m_128403_("Marked") && s.warfront.m_128342_("Marked").equals(npc.id)) {
            s.warfront.m_128379_("MarkedDown", true);
        }
    }

    public static void companionDied(ServerPlayer p, UUID id) {
        AdventureData.Session s = WarfrontService.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE || !s.participants.contains(id)) {
            return;
        }
        s.warfront.m_128379_("AllyFell", true);
        if (s.warfront.m_128451_("Pact") >= 0) {
            WarfrontService.fail(p, "Pact condition failed: a participating companion fell.");
        } else if (s.participants.stream().allMatch(i -> {
            ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(i);
            return r == null || r.pendingRespawn();
        })) {
            WarfrontService.fail(p, "Your participating army fell.");
        }
        WarfrontService.dirty(p);
    }

    public static void adaptation(WarfrontState w, CompoundTag n) {
        int rival = n.m_128451_("Rival");
        double damage = n.m_128459_("TotalDamage");
        double ranged = n.m_128459_("RangedDamage");
        w.adaptations[rival] = damage > 0.0 && ranged / damage >= 0.6 ? WarfrontState.Adaptation.WINDGUARD : (n.m_128451_("Samples") > 0 && (double)n.m_128451_("Clustered") / (double)n.m_128451_("Samples") >= 0.6 ? WarfrontState.Adaptation.SCATTER : WarfrontState.Adaptation.REPRISAL);
    }

    private static void offer(ServerPlayer p, int rival) {
        WarfrontState w = WarfrontService.state(p);
        if (w.offers[rival] != null) {
            return;
        }
        EntityType<?> type = COMMANDERS[rival];
        Mob nativeMob = (Mob)type.m_20615_((Level)p.m_284548_());
        UUID id = UUID.nameUUIDFromBytes((String.valueOf(p.m_20148_()) + ":warfront:" + w.chapter + ":" + rival).getBytes(StandardCharsets.UTF_8));
        ShadowRecord r = new ShadowRecord(id, String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(type)), w.rivalName(rival), ShadowService.snapshot(nativeMob, true), nativeMob.m_21233_());
        CompanionService.awaken(p, r);
        w.offers[rival] = r;
    }

    public static void win(ServerPlayer p, AdventureData.Session s) {
        WarfrontState w = WarfrontService.state(p);
        if (s.phase != AdventureData.Phase.ACTIVE || !s.id.equals(w.activeMission)) {
            return;
        }
        CompoundTag n = s.warfront;
        WarfrontService.adaptation(w, n);
        int node = n.m_128451_("Node");
        int rival = n.m_128451_("Rival");
        int pact = n.m_128451_("Pact");
        if (pact >= 0) {
            if (n.m_128471_("AllyFell")) {
                WarfrontService.fail(p, "Pact condition failed.");
                return;
            }
            w.pactReady |= 1 << pact;
        } else {
            boolean first = (w.captured & 1 << node) == 0;
            w.owned |= 1 << node;
            w.captured |= 1 << node;
            if (w.contested == node) {
                w.contested = -1;
            }
            if (node < 4) {
                w.blueprints |= 1 << node + 3;
            }
            if (node >= 3) {
                if (first) {
                    w.trophies = Math.min(0x7FFFFFFFFFFFFFFEL, w.trophies + 1L);
                }
                w.defeated |= 1 << rival;
                WarfrontService.offer(p, rival);
            }
            w.wonOnce = true;
            if (w.trophies >= 3L) {
                AdventureService.journal((ServerPlayer)p).titles.add(AdventureData.Title.WAR_STRATEGIST);
            }
            if (w.trophies >= 9L) {
                AdventureService.journal((ServerPlayer)p).titles.add(AdventureData.Title.WAR_MARSHAL);
            }
            if (w.trophies >= 18L) {
                AdventureService.journal((ServerPlayer)p).titles.add(AdventureData.Title.RIVAL_SOVEREIGN);
            }
        }
        w.influence[rival] = Math.max(0, w.influence[rival] - 1);
        SovereignService.party(p, s.participants, SovereignState.Goal.WARFRONT);
        w.activeMission = null;
        s.phase = AdventureData.Phase.READY;
        AdventureWorld.clearEntities(p.f_8924_, s);
        WarfrontPlans.stop(p);
        WarfrontService.dirty(p);
        WarfrontService.tell(p, (String)(pact >= 0 ? "Pact earned! Claim your rival in the Pacts tab." : "Territory secured: " + TERRITORIES[node] + ". Dismiss this result to continue."));
    }

    public static boolean claim(ServerPlayer p) {
        AdventureData.Session s = WarfrontService.session(p);
        if (s == null || s.phase != AdventureData.Phase.READY) {
            return false;
        }
        AdventureService.journal((ServerPlayer)p).session = null;
        WarfrontService.dirty(p);
        return true;
    }

    public static void fail(ServerPlayer p, String reason) {
        AdventureData.Session s = WarfrontService.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE) {
            return;
        }
        WarfrontState w = WarfrontService.state(p);
        if (!s.id.equals(w.activeMission)) {
            WarfrontService.discard(p);
            return;
        }
        WarfrontService.adaptation(w, s.warfront);
        int rival = s.warfront.m_128451_("Rival");
        if (s.warfront.m_128451_("Node") == w.contested) {
            w.owned &= ~(1 << w.contested);
            w.contested = -1;
        }
        int n = rival;
        w.influence[n] = w.influence[n] + 1;
        if (w.influence[n] >= 3) {
            w.influence[rival] = 0;
            if (w.contested < 0) {
                for (int i = 0; i < 5; ++i) {
                    if ((w.owned & 1 << i) == 0) continue;
                    w.contested = i;
                    break;
                }
            }
        }
        WarfrontService.discard(p);
        WarfrontService.tell(p, reason + " Rival influence increased. Companion equipment and progression are safe.");
    }

    public static void discard(ServerPlayer p) {
        AdventureData.Session s = WarfrontService.session(p);
        if (s != null) {
            s.phase = AdventureData.Phase.PAUSED;
            AdventureWorld.clearEntities(p.f_8924_, s);
            AdventureService.journal((ServerPlayer)p).session = null;
        }
        WarfrontService.state((ServerPlayer)p).activeMission = null;
        WarfrontPlans.stop(p);
        WarfrontService.dirty(p);
    }

    public static boolean abandon(ServerPlayer p) {
        AdventureData.Session s = WarfrontService.session(p);
        if (s == null) {
            return false;
        }
        if (s.phase == AdventureData.Phase.READY) {
            return WarfrontService.claim(p);
        }
        if (s.phase == AdventureData.Phase.PAUSED) {
            s.phase = AdventureData.Phase.ACTIVE;
        }
        WarfrontService.fail(p, "Mission abandoned.");
        return true;
    }

    public static boolean resume(ServerPlayer p, AdventureData.Session s) {
        if (!WarfrontService.validRun(s)) {
            WarfrontService.discard(p);
            return false;
        }
        WarfrontState w = WarfrontService.state(p);
        if (!WarfrontService.near(p) || p.m_9236_().m_46791_() == Difficulty.PEACEFUL || s.phase != AdventureData.Phase.PAUSED || !s.id.equals(w.activeMission)) {
            return false;
        }
        if (s.participants.stream().noneMatch(id -> {
            Mob mob = WarfrontService.a(p, id);
            return mob != null && mob.m_20280_((Entity)p) <= 1024.0;
        })) {
            WarfrontService.tell(p, "Summon a selected companion within 32 blocks before resuming.");
            return false;
        }
        long delta = Math.max(0L, WarfrontService.now(p) - s.warfront.m_128454_("PausedAt"));
        for (String key : List.of("Started", "Deadline", "NextSpawn", "NextStrike", "StrikeAt", "ReleaseAt", "GraceUntil")) {
            if (!s.warfront.m_128441_(key)) continue;
            s.warfront.m_128356_(key, s.warfront.m_128454_(key) + delta);
        }
        Vec3 shift = Vec3.m_82528_((Vec3i)w.beacon.m_121996_((Vec3i)s.anchor));
        for (Tag tag : s.warfront.m_128437_("Points", 10)) {
            CompoundTag n = (CompoundTag)tag;
            n.m_128356_("Pos", BlockPos.m_122022_((long)n.m_128454_("Pos")).m_121955_((Vec3i)w.beacon.m_121996_((Vec3i)s.anchor)).m_121878_());
        }
        for (AdventureData.Npc npc : s.npcs.values()) {
            npc.pos = npc.pos.m_121955_((Vec3i)w.beacon.m_121996_((Vec3i)s.anchor));
        }
        if (s.warfront.m_128441_("StrikeAt")) {
            s.warfront.m_128347_("StrikeX", s.warfront.m_128459_("StrikeX") + shift.f_82479_);
            s.warfront.m_128347_("StrikeY", s.warfront.m_128459_("StrikeY") + shift.f_82480_);
            s.warfront.m_128347_("StrikeZ", s.warfront.m_128459_("StrikeZ") + shift.f_82481_);
        }
        s.anchor = w.beacon;
        s.dimension = w.dimension;
        s.warfront.m_128473_("PausedAt");
        s.warfront.m_128356_("Clock", WarfrontService.now(p));
        ++s.epoch;
        s.phase = AdventureData.Phase.ACTIVE;
        s.npcs.values().forEach(e -> {
            e.active = null;
        });
        WarfrontService.dirty(p);
        return true;
    }

    public static boolean claimPact(ServerPlayer p, int rival) {
        WarfrontState w = WarfrontService.state(p);
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        if (!p.m_6084_() || p.m_5833_() || !WarfrontService.workshop(p) || rival < 0 || rival > 2 || (w.pactReady & 1 << rival) == 0 || (w.pactClaimed & 1 << rival) != 0 || w.offers[rival] == null) {
            return false;
        }
        ShadowRecord r = w.offers[rival];
        if (army.shadows.size() >= (Integer)ShadowConfig.MAX_ARMY.get() && !army.shadows.containsKey(r.id)) {
            WarfrontService.tell(p, "Army full. Your pact and tier roll remain saved.");
            return false;
        }
        if (((List)ShadowConfig.BLOCKLIST.get()).contains(r.type)) {
            WarfrontService.tell(p, "This mob type is currently blocked; your reward is preserved.");
            return false;
        }
        if (!army.shadows.containsKey(r.id)) {
            CompanionService.acquired(p, r);
            army.shadows.put(r.id, r);
            w.build((UUID)r.id).pactbound = true;
            AdventureService.captured(p, r);
        }
        w.pactClaimed |= 1 << rival;
        WarfrontService.dirty(p);
        WarfrontService.tell(p, r.name + " joins your army as a Pactbound " + r.tier.label + " companion.");
        return true;
    }

    public static boolean next(ServerPlayer p, boolean discardOffers) {
        WarfrontState w = WarfrontService.state(p);
        if (!WarfrontService.near(p) || w.owned != 63 || WarfrontService.session(p) != null || w.chapter == Long.MAX_VALUE) {
            return false;
        }
        if (!discardOffers && (w.defeated & ~w.pactClaimed) != 0) {
            WarfrontService.tell(p, "Unclaimed pacts remain. Claim them or explicitly confirm starting the next campaign without them.");
            return false;
        }
        ++w.chapter;
        w.seed = p.m_217043_().m_188505_() | 1L;
        w.captured = 0;
        w.owned = 0;
        w.contested = -1;
        w.pactClaimed = 0;
        w.pactReady = 0;
        w.defeated = 0;
        Arrays.fill(w.influence, 0);
        Arrays.fill((Object[])w.adaptations, (Object)WarfrontState.Adaptation.NONE);
        Arrays.fill(w.offers, null);
        WarfrontService.dirty(p);
        return true;
    }
}

