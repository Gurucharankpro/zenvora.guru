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
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.ascension;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.SoulService;
import dev.shadowsovereign.ascension.WardenCompatibility;
import dev.shadowsovereign.journey.JourneyCombat;
import dev.shadowsovereign.legacy.LegacyService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class AscensionService {
    public static final AdventureData.Kind[] MODES = new AdventureData.Kind[]{AdventureData.Kind.HEIST, AdventureData.Kind.DOMINATION, AdventureData.Kind.MIRROR, AdventureData.Kind.DRAFT, AdventureData.Kind.RUSH};
    public static final String[] NAMES = new String[]{"Soul Heists", "Rift Domination", "Mirror Duels", "Shadow Draft", "Treasure Rush"};
    public static final EntityType<?>[] POOL = new EntityType[]{EntityType.f_20501_, EntityType.f_20524_, EntityType.f_20479_, EntityType.f_20458_, EntityType.f_20481_, EntityType.f_20460_};
    public static final String[] RUNES = new String[]{"Crown", "Flame", "Moon"};

    public static boolean activity(AdventureData.Kind k) {
        return Arrays.asList(MODES).contains((Object)k);
    }

    public static long now(ServerPlayer p) {
        return SoulService.now(p);
    }

    public static AdventureData.Session session(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && AscensionService.activity(s.kind) ? s : null;
    }

    private static void dirty(ServerPlayer p) {
        AdventureService.dirty(p);
    }

    public static boolean start(ServerPlayer p, int mode, boolean hard) {
        return AscensionService.start(p, mode, hard, List.of());
    }

    public static boolean start(ServerPlayer p, int mode, boolean hard, List<UUID> selected) {
        int i;
        if (mode < 0 || mode >= 5 || !p.m_6084_() || p.m_5833_() || !AdventureService.requireHub(p)) {
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        if (j.session != null || j.completions < (long)(mode == 3 ? 5 : (mode == 1 || mode == 2 ? 3 : 1))) {
            return false;
        }
        if (p.m_9236_().m_46791_() == Difficulty.PEACEFUL && mode != 4) {
            return false;
        }
        List<ShadowRecord> records = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().filter(r -> LegacyService.eligible(p, r.id, 1, 32) != null).toList();
        if (records.isEmpty()) {
            return false;
        }
        if (mode == 2 && !selected.isEmpty() && (new HashSet<UUID>(selected).size() != selected.size() || selected.stream().anyMatch(id -> records.stream().noneMatch(r -> r.id.equals(id))))) {
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = MODES[mode];
        s.phase = AdventureData.Phase.ACTIVE;
        s.anchor = j.sanctuary;
        s.dimension = j.dimension;
        records.forEach(r -> s.participants.add(r.id));
        CompoundTag n = s.journey;
        n.m_128379_("Hard", hard);
        n.m_128356_("Started", AscensionService.now(p));
        n.m_128356_("Clock", AscensionService.now(p));
        n.m_128405_("Mode", mode);
        ListTag spots = new ListTag();
        for (i = 0; i < 3; ++i) {
            Vec3 at = null;
            for (int a = 0; a < 32; ++a) {
                Vec3 v = AdventureWorld.spot(p.m_284548_(), EntityType.f_20529_, s.anchor, 10.0, 26.0, p.m_20148_().hashCode() + i * 177 + a * 73);
                if (v == null || !spots.stream().allMatch(t -> BlockPos.m_122022_((long)((CompoundTag)t).m_128454_("Pos")).m_123331_((Vec3i)BlockPos.m_274446_((Position)v)) >= 49.0)) continue;
                at = v;
                break;
            }
            if (at == null) {
                return false;
            }
            CompoundTag t2 = new CompoundTag();
            t2.m_128356_("Pos", BlockPos.m_274446_(at).m_121878_());
            spots.add((Object)t2);
        }
        n.m_128365_("Points", (Tag)spots);
        n.m_128356_("Deadline", AscensionService.now(p) + (long)(mode == 4 ? (hard ? 1800 : 3000) : (hard ? 3600 : 6000)));
        try {
            if (mode == 0) {
                for (i = 0; i < 4; ++i) {
                    AscensionService.add(p, s, EntityType.f_20524_, AdventureData.Role.ENEMY);
                }
            }
            if (mode == 1) {
                for (i = 0; i < 3; ++i) {
                    AscensionService.add(p, s, EntityType.f_20501_, AdventureData.Role.ENEMY);
                }
                n.m_128356_("NextSpawn", AscensionService.now(p) + 400L);
            }
            if (mode == 2) {
                for (ShadowRecord r2 : records.stream().filter(r -> selected.isEmpty() || selected.contains(r.id)).limit(3L).toList()) {
                    Mob source = ShadowService.active(p.f_8924_, r2);
                    Entity copy = source.m_6095_().m_20615_((Level)p.m_284548_());
                    if (!(copy instanceof Mob)) {
                        return false;
                    }
                    Mob m = (Mob)copy;
                    CompoundTag snap = ShadowService.snapshot(source, true);
                    snap.m_128473_("CustomName");
                    snap.m_128473_("anger");
                    snap.m_128359_("Pose", "STANDING");
                    m.m_20258_(snap);
                    WardenCompatibility.attach(m);
                    m.m_6593_((Component)Component.m_237113_((String)("Echo of " + r2.name)));
                    AscensionService.scale(m, hard);
                    AdventureData.Npc npc = new AdventureData.Npc();
                    npc.type = r2.type;
                    npc.snapshot = AdventureWorld.snapshot(m);
                    s.npcs.put(npc.id, npc);
                }
            }
        }
        catch (RuntimeException ex) {
            ShadowService.tell(p, "This entity cannot be safely mirrored. No activity was started.");
            return false;
        }
        s.initialized = mode != 3;
        j.session = s;
        if (mode == 3) {
            ShadowService.recallAll(p);
            AscensionService.offers(p, s);
            ShadowService.tell(p, "Your real army waits in reserve. Draft three temporary allies.");
        }
        if (mode == 4) {
            AscensionService.nextRune(p, s);
        }
        AscensionService.dirty(p);
        return true;
    }

    private static void scale(Mob m, boolean hard) {
        if (!hard) {
            m.m_21051_(Attributes.f_22276_).m_22100_((double)m.m_21233_() * 0.75);
            m.m_21153_(m.m_21233_());
        }
    }

    public static AdventureData.Npc add(ServerPlayer p, AdventureData.Session s, EntityType<?> type, AdventureData.Role role) {
        AdventureData.Npc npc = AdventureWorld.template(p, s, type, role);
        if (role == AdventureData.Role.ENEMY) {
            Mob m = (Mob)type.m_20615_((Level)p.m_284548_());
            m.m_20258_(npc.snapshot);
            AscensionService.scale(m, s.journey.m_128471_("Hard"));
            npc.snapshot = AdventureWorld.snapshot(m);
        }
        s.npcs.put(npc.id, npc);
        return npc;
    }

    public static Vec3 point(AdventureData.Session s, int i) {
        return Vec3.m_82539_((Vec3i)BlockPos.m_122022_((long)s.journey.m_128437_("Points", 10).m_128728_(i).m_128454_("Pos")));
    }

    private static void offers(ServerPlayer p, AdventureData.Session s) {
        int i;
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (i = 0; i < 6; ++i) {
            if (s.journey.m_128471_("Picked" + i)) continue;
            list.add(i);
        }
        Collections.shuffle(list, new Random(p.m_217043_().m_188505_()));
        for (i = 0; i < 3; ++i) {
            s.journey.m_128405_("Offer" + i, ((Integer)list.get(i)).intValue());
        }
    }

    public static boolean pick(ServerPlayer p, int card) {
        AdventureData.Session s = AscensionService.session(p);
        if (s == null || s.kind != AdventureData.Kind.DRAFT || s.phase != AdventureData.Phase.ACTIVE || s.initialized || card < 0 || card > 2 || !AdventureService.nearHub(p)) {
            return false;
        }
        int type = s.journey.m_128451_("Offer" + card);
        if (type < 0 || type >= 6 || s.journey.m_128471_("Picked" + type)) {
            return false;
        }
        s.journey.m_128379_("Picked" + type, true);
        AscensionService.add(p, s, POOL[type], AdventureData.Role.ALLY);
        int count = s.journey.m_128451_("Picks") + 1;
        s.journey.m_128405_("Picks", count);
        if (count == 3) {
            s.initialized = true;
            AscensionService.draftRoom(p, s);
        } else {
            AscensionService.offers(p, s);
        }
        AscensionService.dirty(p);
        return true;
    }

    private static void draftRoom(ServerPlayer p, AdventureData.Session s) {
        if (s.wave == 2) {
            JourneyCombat.boss(p, s, 0);
            return;
        }
        int count = s.wave == 0 ? 4 : 5;
        for (int i = 0; i < count; ++i) {
            AscensionService.add(p, s, s.wave == 0 ? EntityType.f_20501_ : (i < 3 ? EntityType.f_20524_ : EntityType.f_20479_), AdventureData.Role.ENEMY);
        }
    }

    private static void nextRune(ServerPlayer p, AdventureData.Session s) {
        s.journey.m_128405_("Rune", p.m_217043_().m_188503_(3));
        ArrayList<Integer> colors = new ArrayList<Integer>(List.of(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2)));
        Collections.shuffle(colors, new Random(p.m_217043_().m_188505_()));
        for (int i = 0; i < 3; ++i) {
            s.journey.m_128405_("Cache" + i, colors.get(i).intValue());
        }
    }

    public static boolean interact(ServerPlayer p) {
        AdventureData.Session s = AscensionService.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE || !p.m_9236_().m_46472_().m_135782_().toString().equals(s.dimension) || p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) > 1600.0) {
            return false;
        }
        CompoundTag n = s.journey;
        if (s.kind == AdventureData.Kind.DRAFT) {
            Vec3 start = p.m_146892_();
            Vec3 end = start.m_82549_(p.m_20154_().m_82490_(40.0));
            Entity target = s.npcs.values().stream().filter(a -> a.role == AdventureData.Role.ENEMY && a.active != null).map(a -> p.m_284548_().m_8791_(a.active)).filter(e -> e instanceof LivingEntity && e.m_20191_().m_82400_(0.5).m_82371_(start, end).isPresent() && p.m_142582_(e)).min(Comparator.comparingDouble(arg_0 -> ((ServerPlayer)p).m_20280_(arg_0))).orElse(null);
            if (!(target instanceof LivingEntity)) {
                return false;
            }
            LivingEntity living = (LivingEntity)target;
            n.m_128362_("Focus", target.m_20148_());
            AscensionService.dirty(p);
            return true;
        }
        if (s.kind != AdventureData.Kind.HEIST && s.kind != AdventureData.Kind.RUSH) {
            return false;
        }
        if (s.kind == AdventureData.Kind.HEIST && n.m_128451_("Collected") == 7 && p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) <= 16.0) {
            AdventureWorld.ready(p, s);
            return true;
        }
        for (int i = 0; i < 3; ++i) {
            if (!(p.m_20238_(AscensionService.point(s, i)) <= 9.0) || p.m_9236_().m_45547_(new ClipContext(p.m_146892_(), AscensionService.point(s, i).m_82520_(0.0, 0.5, 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)p)).m_6662_() != HitResult.Type.MISS) continue;
            if (s.kind == AdventureData.Kind.HEIST) {
                int bits = n.m_128451_("Collected");
                if ((bits & 1 << i) != 0) {
                    return false;
                }
                n.m_128405_("Collected", bits | 1 << i);
                ShadowService.tell(p, "Relic secured. Return to the Sanctuary after collecting all three.");
            } else {
                int index = i;
                boolean near = s.participants.stream().map(id -> LegacyService.eligible(p, id, 1, 32)).anyMatch(m -> m != null && m.m_20238_(AscensionService.point(s, index)) <= 25.0);
                if (!near) {
                    ShadowService.tell(p, "Bring a companion within five blocks of the cache.");
                    return false;
                }
                if (n.m_128451_("Cache" + i) == n.m_128451_("Rune")) {
                    ++s.wave;
                    if (s.wave == 5) {
                        AdventureWorld.ready(p, s);
                        return true;
                    }
                    ShadowService.tell(p, "Correct rune! " + s.wave + " / 5");
                } else {
                    n.m_128356_("Deadline", n.m_128454_("Deadline") - 200L);
                    ShadowService.tell(p, "Wrong rune: ten seconds lost.");
                }
                AscensionService.nextRune(p, s);
            }
            AscensionService.dirty(p);
            return true;
        }
        return false;
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        CompoundTag n = s.journey;
        n.m_128356_("Clock", AscensionService.now(p));
        if (s.kind != AdventureData.Kind.DRAFT && AscensionService.now(p) >= n.m_128454_("Deadline")) {
            AscensionService.fail(p, "Activity time expired.");
            return;
        }
        for (int i = 0; i < 3; ++i) {
            Vec3 at = AscensionService.point(s, i);
            p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123809_, true, at.f_82479_, at.f_82480_ + 0.7, at.f_82481_, 8, 0.5, 0.7, 0.5, 0.02);
        }
        switch (s.kind) {
            case HEIST: {
                AscensionService.heist(p, s);
                break;
            }
            case DOMINATION: {
                AscensionService.domination(p, s);
                break;
            }
            case MIRROR: {
                if (!s.npcs.isEmpty()) break;
                AdventureWorld.ready(p, s);
                break;
            }
            case DRAFT: {
                if (!s.initialized) {
                    return;
                }
                long allies = s.npcs.values().stream().filter(a -> a.role == AdventureData.Role.ALLY).count();
                long enemies = s.npcs.values().stream().filter(a -> a.role == AdventureData.Role.ENEMY).count();
                if (allies == 0L) {
                    AscensionService.fail(p, "Your drafted team fell. Your real army is safe in reserve.");
                    return;
                }
                if (enemies == 0L) {
                    ++s.wave;
                    if (s.wave == 3) {
                        AdventureWorld.ready(p, s);
                        return;
                    }
                    n.m_128473_("BossNpc");
                    AscensionService.draftRoom(p, s);
                    break;
                }
                if (s.wave != 2) break;
                JourneyCombat.tick(p, s);
                break;
            }
        }
        AscensionService.dirty(p);
    }

    private static void heist(ServerPlayer p, AdventureData.Session s) {
        if (s.journey.m_128451_("Alarm") >= 100) {
            AscensionService.fail(p, "Alarm raised! The heist is over.");
            return;
        }
        boolean seen = false;
        ArrayList<Object> actors = new ArrayList<Object>();
        actors.add(p);
        for (UUID id : s.participants) {
            Mob m = LegacyService.eligible(p, id, 1, 32);
            if (m == null) continue;
            actors.add(m);
        }
        block1: for (AdventureData.Npc npc : s.npcs.values()) {
            Object object;
            if (npc.active == null || !((object = p.m_284548_().m_8791_(npc.active)) instanceof Mob)) continue;
            Mob guard = (Mob)object;
            object = actors.iterator();
            while (object.hasNext()) {
                LivingEntity a = (LivingEntity)object.next();
                Vec3 direction = a.m_146892_().m_82546_(guard.m_146892_());
                if (!(direction.m_82556_() <= 100.0) || !guard.m_142582_((Entity)a) || !(guard.m_20154_().m_82541_().m_82526_(direction.m_82541_()) >= Math.cos(0.7853981633974483))) continue;
                seen = true;
                continue block1;
            }
        }
        int alarm = Math.max(0, Math.min(100, s.journey.m_128451_("Alarm") + (seen ? 10 : -5)));
        s.journey.m_128405_("Alarm", alarm);
        if (alarm >= 100) {
            AscensionService.fail(p, "Alarm raised! The heist is over.");
        }
    }

    private static void domination(ServerPlayer p, AdventureData.Session s) {
        int i;
        CompoundTag n = s.journey;
        for (int i2 = 0; i2 < 3; ++i2) {
            Vec3 at = AscensionService.point(s, i2);
            boolean enemy = s.npcs.values().stream().filter(a -> a.role == AdventureData.Role.ENEMY && a.active != null).map(a -> p.m_284548_().m_8791_(a.active)).anyMatch(e -> e != null && e.m_20238_(at) <= 36.0);
            boolean friendly = p.m_20238_(at) <= 9.0 || s.participants.stream().map(id -> LegacyService.eligible(p, id, 1, 32)).anyMatch(m -> m != null && m.m_20238_(at) <= 9.0);
            int capture = n.m_128451_("Capture" + i2);
            int contest = n.m_128451_("Contest" + i2);
            if (enemy) {
                if ((contest += 20) >= 100) {
                    capture = 0;
                }
            } else {
                contest = 0;
                if (friendly) {
                    capture = Math.min(160, capture + 20);
                }
            }
            n.m_128405_("Capture" + i2, capture);
            n.m_128405_("Contest" + i2, contest);
        }
        boolean all = true;
        for (i = 0; i < 3; ++i) {
            all &= n.m_128451_("Capture" + i) >= 160;
        }
        n.m_128405_("Hold", all ? n.m_128451_("Hold") + 20 : 0);
        if (n.m_128451_("Hold") >= 400) {
            AdventureWorld.ready(p, s);
            return;
        }
        if (AscensionService.now(p) >= n.m_128454_("NextSpawn")) {
            for (i = 0; i < 3 && s.npcs.size() < 9; ++i) {
                AscensionService.add(p, s, EntityType.f_20501_, AdventureData.Role.ENEMY);
            }
            n.m_128356_("NextSpawn", AscensionService.now(p) + 400L);
        }
    }

    public static void fail(ServerPlayer p, String reason) {
        AdventureWorld.cancel(p);
        ShadowService.tell(p, reason);
    }

    public static void completed(ServerPlayer p, AdventureData.Session s) {
        CompoundTag ledger = AdventureService.journal((ServerPlayer)p).ascension;
        String key = "Day" + s.kind.name();
        long day = AscensionService.now(p) / 24000L;
        boolean reward = !ledger.m_128441_(key) || ledger.m_128454_(key) != day;
        ledger.m_128356_(key, day);
        s.journey.m_128379_("Reward", reward);
        ledger.m_128356_("Wins" + s.kind.name(), Math.min(0x7FFFFFFFFFFFFFFEL, Math.max(0L, ledger.m_128454_("Wins" + s.kind.name()))) + 1L);
        if (s.journey.m_128471_("Hard")) {
            AdventureService.journal((ServerPlayer)p).titles.add((new AdventureData.Title[]{AdventureData.Title.GHOST_THIEF, AdventureData.Title.RIFT_COMMANDER, AdventureData.Title.MIRRORBREAKER, AdventureData.Title.DRAFT_SOVEREIGN, AdventureData.Title.FORTUNE_RUNNER})[s.journey.m_128451_("Mode")]);
        }
        JourneyCombat.removeBar(s.id);
        AscensionService.dirty(p);
    }

    public static boolean claim(ServerPlayer p) {
        AdventureData.Session s = AscensionService.session(p);
        if (s == null || s.phase != AdventureData.Phase.READY) {
            return false;
        }
        AdventureService.journal((ServerPlayer)p).session = null;
        if (s.journey.m_128471_("Reward")) {
            AdventureService.reward(p, AdventureService.journal(p), s.id, "Ascension reward claimed");
        } else {
            ShadowService.tell(p, "Victory recorded. Today's completion reward for this activity was already claimed.");
        }
        AscensionService.dirty(p);
        return true;
    }

    public static void paused(MinecraftServer server, AdventureData.Session s) {
        if (AscensionService.activity(s.kind)) {
            s.journey.m_128356_("PausedAt", AdventureService.now(server));
        }
        JourneyCombat.removeBar(s.id);
    }

    public static void resumed(ServerPlayer p, AdventureData.Session s) {
        if (!AscensionService.activity(s.kind)) {
            return;
        }
        long delta = Math.max(0L, AscensionService.now(p) - s.journey.m_128454_("PausedAt"));
        for (String key : List.of("Started", "Deadline", "NextSpawn", "SpecialAt", "StrikeAt")) {
            if (!s.journey.m_128441_(key)) continue;
            s.journey.m_128356_(key, s.journey.m_128454_(key) + delta);
        }
        s.journey.m_128473_("PausedAt");
    }

    public static void publicState(ServerPlayer p, CompoundTag root) {
        AdventureData.Session s = AscensionService.session(p);
        root.m_128365_("Ascension", (Tag)AdventureService.journal((ServerPlayer)p).ascension.m_6426_());
        if (s == null) {
            return;
        }
        CompoundTag run = new CompoundTag();
        run.m_128359_("Dimension", s.dimension);
        for (String key : List.of("Mode", "Hard", "Picks", "Offer0", "Offer1", "Offer2", "Alarm", "Collected", "Rune", "Cache0", "Cache1", "Cache2", "Capture0", "Capture1", "Capture2", "Hold", "Reward", "Points")) {
            if (!s.journey.m_128441_(key)) continue;
            run.m_128365_(key, s.journey.m_128423_(key).m_6426_());
        }
        long time = s.phase == AdventureData.Phase.PAUSED ? s.journey.m_128454_("PausedAt") : AscensionService.now(p);
        run.m_128356_("Remaining", Math.max(0L, s.journey.m_128454_("Deadline") - time));
        root.m_128365_("AscensionRun", (Tag)run);
    }
}

