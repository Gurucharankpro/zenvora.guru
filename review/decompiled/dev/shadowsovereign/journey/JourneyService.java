/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.journey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.journey.JourneyActivities;
import dev.shadowsovereign.journey.JourneyCombat;
import dev.shadowsovereign.journey.JourneyState;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.legacy.LegacyService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class JourneyService {
    public static final String[] BOSSES = new String[]{"Gravewarden", "Frost Regent", "Hollow Tyrant"};
    public static final String[] FISH = new String[]{"Moonfin", "Ember Eel", "Glass Carp", "Void Ray", "Crown Koi", "Star Minnow"};
    public static final String[] STORIES = new String[]{"First Awakening", "The Long Road", "A Narrow Escape", "An Unusual Ally", "Quiet Watch", "Tomorrow's Journey"};
    public static final String[] CHAPTERS = new String[]{"A Place to Return", "Voices by the Fire", "Beyond the Gate", "Paths through the Rift", "A Crown Challenged", "The Sovereign's Journey"};
    public static final AdventureData.Title[] CHAPTER_TITLES = new AdventureData.Title[]{AdventureData.Title.WAYFARER, AdventureData.Title.CAMPKEEPER, AdventureData.Title.TRAILBLAZER, AdventureData.Title.RIFT_CARTOGRAPHER, AdventureData.Title.KINGSLAYER, AdventureData.Title.DAWN_SOVEREIGN};

    public static boolean isJourney(AdventureData.Kind k) {
        return k.ordinal() >= AdventureData.Kind.BOSS.ordinal() && k.ordinal() <= AdventureData.Kind.CAMP.ordinal();
    }

    public static boolean peaceful(AdventureData.Kind k) {
        return k == AdventureData.Kind.RALLY || k == AdventureData.Kind.ANGLING || k == AdventureData.Kind.CAMP;
    }

    public static JourneyState state(ServerPlayer p) {
        return AdventureService.journal((ServerPlayer)p).journey;
    }

    public static long now(ServerPlayer p) {
        return AdventureService.now(p.f_8924_);
    }

    public static void dirty(ServerPlayer p) {
        AdventureService.dirty(p);
    }

    public static void tell(ServerPlayer p, String message) {
        LegacyService.notice(p, message);
    }

    public static boolean start(ServerPlayer p, AdventureData.Kind kind, int choice, boolean hard, UUID ally) {
        int required;
        if (!JourneyService.isJourney(kind) || !p.m_6084_() || p.m_5833_() || !AdventureService.requireHub(p)) {
            return false;
        }
        AdventureData.Journal j = AdventureService.journal(p);
        int n = kind == AdventureData.Kind.CAMP ? 0 : (kind == AdventureData.Kind.BOSS ? 3 : (required = kind == AdventureData.Kind.RIFT ? 5 : 1));
        if (j.session != null || j.completions < (long)required || !JourneyService.peaceful(kind) && p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            return false;
        }
        if (kind == AdventureData.Kind.BOSS && (choice < 0 || choice > 2) || kind == AdventureData.Kind.CAMP && (choice < 0 || choice > 5)) {
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = kind;
        s.phase = AdventureData.Phase.ACTIVE;
        s.dimension = j.dimension;
        s.anchor = j.sanctuary;
        CompoundTag n2 = s.journey;
        n2.m_128379_("Hard", hard);
        n2.m_128405_("Choice", choice);
        n2.m_128356_("Clock", JourneyService.now(p));
        n2.m_128356_("Started", JourneyService.now(p));
        n2.m_128356_("Day", JourneyService.now(p) / 24000L);
        for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            if (LegacyService.eligible(p, r.id, 1, 32) == null) continue;
            s.participants.add(r.id);
        }
        if (kind != AdventureData.Kind.ANGLING && s.participants.isEmpty()) {
            return false;
        }
        if (kind == AdventureData.Kind.RALLY) {
            ShadowRecord r = LegacyService.record(p, ally);
            Mob m = LegacyService.eligible(p, ally, 1, 32);
            if (r == null || m == null || r.order != Order.FOLLOW || !JourneyActivities.route(p, s, m)) {
                return false;
            }
            s.participants.clear();
            s.participants.add(ally);
            n2.m_128362_("Ally", ally);
            n2.m_128356_("Deadline", JourneyService.now(p) + 1800L);
        }
        if (kind == AdventureData.Kind.ANGLING) {
            BlockPos water = JourneyActivities.water(p);
            if (water == null) {
                JourneyService.tell(p, "Bring water within sixteen blocks of your Sanctuary, and stand within eight blocks of it.");
                return false;
            }
            n2.m_128356_("Water", water.m_121878_());
        }
        if (kind == AdventureData.Kind.CAMP) {
            BlockPos fire = JourneyActivities.campfire(p);
            if (fire == null) {
                JourneyService.tell(p, "Light a campfire within sixteen blocks of your Sanctuary.");
                return false;
            }
            n2.m_128356_("Fire", fire.m_121878_());
            s.participants.removeIf(id -> {
                Mob m = LegacyService.eligible(p, id, 1, 16);
                return m == null || m.m_5448_() != null;
            });
            while (s.participants.size() > 8) {
                s.participants.remove(s.participants.stream().reduce((a, b) -> b).orElseThrow());
            }
            if (s.participants.isEmpty()) {
                return false;
            }
            n2.m_128356_("Deadline", JourneyService.now(p) + 240L);
        }
        if (kind == AdventureData.Kind.RIFT) {
            n2.m_128379_("ChooseRoute", true);
        }
        j.session = s;
        JourneyService.dirty(p);
        JourneyService.tell(p, "Journey started: " + AdventureService.pretty(kind.name()) + " / " + (hard ? "Challenging" : "Relaxed"));
        return true;
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        s.journey.m_128356_("Clock", JourneyService.now(p));
        if (s.kind == AdventureData.Kind.BOSS || s.kind == AdventureData.Kind.RIFT) {
            JourneyCombat.tick(p, s);
        } else {
            JourneyActivities.tick(p, s);
        }
        JourneyService.dirty(p);
    }

    public static void paused(MinecraftServer server, AdventureData.Session s) {
        if (JourneyService.isJourney(s.kind)) {
            s.journey.m_128356_("PausedAt", AdventureService.now(server));
            JourneyService.cleanup(server, s);
        }
    }

    public static void resumed(ServerPlayer p, AdventureData.Session s) {
        if (!JourneyService.isJourney(s.kind)) {
            return;
        }
        CompoundTag n = s.journey;
        long shift = n.m_128441_("PausedAt") ? Math.max(0L, JourneyService.now(p) - n.m_128454_("PausedAt")) : 0L;
        for (String key : List.of("Started", "Deadline", "BiteAt", "BiteEnd", "SpecialAt", "StrikeAt")) {
            if (!n.m_128441_(key)) continue;
            n.m_128356_(key, n.m_128454_(key) + shift);
        }
        n.m_128473_("PausedAt");
        n.m_128356_("Clock", JourneyService.now(p));
        n.m_128473_("LastPlayer");
        n.m_128473_("LastAlly");
    }

    public static void cleanup(MinecraftServer server, AdventureData.Session s) {
        if (JourneyService.isJourney(s.kind)) {
            JourneyCombat.removeBar(s.id);
        }
    }

    public static void canceled(ServerPlayer p, AdventureData.Session s) {
        if (!JourneyService.isJourney(s.kind) || s.journey.m_128471_("Recorded")) {
            return;
        }
        s.journey.m_128379_("Recorded", true);
        AdventureService.journal((ServerPlayer)p).legacy.result(s.kind.name(), "CANCELED", s.wave, 0, JourneyService.now(p));
        JourneyService.dirty(p);
    }

    public static void fail(ServerPlayer p, String reason) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s == null || !JourneyService.isJourney(s.kind) || s.phase == AdventureData.Phase.READY) {
            return;
        }
        s.journey.m_128379_("Recorded", true);
        AdventureService.journal((ServerPlayer)p).legacy.result(s.kind.name(), "DEFEAT", s.wave, 0, JourneyService.now(p));
        AdventureWorld.cancel(p);
        JourneyService.tell(p, reason);
    }

    public static void completed(ServerPlayer p, AdventureData.Session s) {
        CompoundTag n = s.journey;
        if (n.m_128471_("Recorded")) {
            return;
        }
        n.m_128379_("Recorded", true);
        JourneyState st = JourneyService.state(p);
        long day = JourneyService.now(p) / 24000L;
        boolean reward = false;
        switch (s.kind) {
            case BOSS: {
                int boss = n.m_128451_("Choice");
                st.data.m_128405_("Bosses", st.data.m_128451_("Bosses") | 1 << boss);
                reward = st.daily("BossDay" + boss, day);
                if (!n.m_128471_("Hard")) break;
                JourneyService.unlock(p, (new AdventureData.Title[]{AdventureData.Title.GRAVE_CONQUEROR, AdventureData.Title.FROST_CONQUEROR, AdventureData.Title.HOLLOW_CONQUEROR})[boss]);
                break;
            }
            case RIFT: {
                st.increment("Rifts");
                reward = true;
                if (n.m_128471_("Hard")) {
                    JourneyService.unlock(p, AdventureData.Title.RIFT_VETERAN);
                }
                if (n.m_128451_("Risk") != 3) break;
                JourneyService.unlock(p, AdventureData.Title.RIFT_DAREDEVIL);
                break;
            }
            case RALLY: {
                st.increment("Rallies");
                reward = st.daily("RallyDay", day);
                UUID id = n.m_128342_("Ally");
                CompoundTag f = st.companion(id);
                f.m_128379_("Rally", true);
                String key = n.m_128471_("Hard") ? "BestHard" : "BestRelaxed";
                long elapsed = JourneyService.now(p) - n.m_128454_("Started");
                if (!f.m_128441_(key) || elapsed < f.m_128454_(key)) {
                    f.m_128356_(key, elapsed);
                }
                if (!n.m_128471_("Hard")) break;
                JourneyService.unlock(p, AdventureData.Title.RALLY_CHAMPION);
                break;
            }
            case CAMP: {
                int bit = 1 << n.m_128451_("Choice");
                st.data.m_128405_("Stories", st.data.m_128451_("Stories") | bit);
                for (UUID id : s.participants) {
                    Mob m = LegacyService.eligible(p, id, 1, 16);
                    if (m == null || m.m_5448_() != null) continue;
                    CompoundTag f = st.companion(id);
                    f.m_128405_("Stories", f.m_128451_("Stories") | bit);
                }
                if (Integer.bitCount(st.data.m_128451_("Stories")) < 3) break;
                JourneyService.unlock(p, AdventureData.Title.CAMPKEEPER);
                break;
            }
        }
        n.m_128379_("Reward", reward);
        AdventureService.journal((ServerPlayer)p).legacy.result(s.kind.name(), "VICTORY", s.wave, 0, JourneyService.now(p));
        JourneyService.dirty(p);
    }

    public static boolean claim(ServerPlayer p) {
        AdventureData.Journal j = AdventureService.journal(p);
        AdventureData.Session s = j.session;
        if (s == null || !JourneyService.isJourney(s.kind) || s.phase != AdventureData.Phase.READY) {
            return false;
        }
        j.session = null;
        if (s.journey.m_128471_("Reward")) {
            AdventureService.reward(p, j, s.id, "Journey reward claimed");
        } else {
            JourneyService.tell(p, "Journey recorded. This activity has no additional completion reward available.");
        }
        JourneyService.dirty(p);
        return true;
    }

    public static void unlock(ServerPlayer p, AdventureData.Title title) {
        if (AdventureService.journal((ServerPlayer)p).titles.add(title)) {
            JourneyService.tell(p, "Title unlocked: " + AdventureService.pretty(title.name()));
            JourneyService.dirty(p);
        }
    }

    public static boolean chapterReady(ServerPlayer p, int chapter) {
        AdventureData.Journal j = AdventureService.journal(p);
        CompoundTag n = JourneyService.state((ServerPlayer)p).data;
        return switch (chapter) {
            case 0 -> j.bound;
            case 1 -> {
                if (n.m_128451_("Stories") != 0) {
                    yield true;
                }
                yield false;
            }
            case 2 -> {
                if (n.m_128454_("Rallies") > 0L || n.m_128454_("Catches") >= 3L) {
                    yield true;
                }
                yield false;
            }
            case 3 -> {
                if (n.m_128454_("Rifts") > 0L) {
                    yield true;
                }
                yield false;
            }
            case 4 -> {
                if (n.m_128451_("Bosses") != 0) {
                    yield true;
                }
                yield false;
            }
            case 5 -> {
                if ((n.m_128451_("Bosses") & 7) == 7) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    public static boolean campaign(ServerPlayer p) {
        CompoundTag n = JourneyService.state((ServerPlayer)p).data;
        int chapter = n.m_128451_("Chapter");
        if (chapter < 0 || chapter >= 6 || !JourneyService.chapterReady(p, chapter)) {
            return false;
        }
        n.m_128405_("Chapter", chapter + 1);
        JourneyService.unlock(p, CHAPTER_TITLES[chapter]);
        JourneyService.dirty(p);
        return true;
    }

    public static boolean badge(ServerPlayer p, UUID id, int index) {
        JourneyState.Feat feat;
        ShadowRecord r = LegacyService.record(p, id);
        if (r == null || index < 0 || index >= JourneyState.Feat.values().length) {
            return false;
        }
        CompoundTag n = JourneyService.state(p).companion(id);
        if (!JourneyState.earned(n, feat = JourneyState.Feat.values()[index])) {
            return false;
        }
        n.m_128359_("Badge", feat.name());
        Mob m = ShadowService.active(p.f_8924_, r);
        if (m != null) {
            ShadowService.updateName(m, r);
        }
        JourneyService.dirty(p);
        return true;
    }

    public static String badge(Mob mob, ShadowRecord r) {
        AdventureData.Journal j = AdventureData.get((MinecraftServer)mob.m_20194_()).owners.get(ShadowService.ownerId((Entity)mob));
        if (j == null) {
            return "";
        }
        CompoundTag n = j.journey.companions.get(r.id);
        if (n == null) {
            return "";
        }
        JourneyState.Feat f = AdventureData.read(JourneyState.Feat.class, n.m_128461_("Badge"), JourneyState.Feat.NONE);
        return f == JourneyState.Feat.NONE || !JourneyState.earned(n, f) ? "" : " <" + AdventureService.pretty(f.name()) + ">";
    }

    public static void npcDied(Mob mob, AdventureData.Session s, AdventureData.Npc npc) {
        if (!JourneyService.isJourney(s.kind)) {
            return;
        }
        if (s.journey.m_128403_("BossNpc") && s.journey.m_128342_("BossNpc").equals(npc.id)) {
            s.journey.m_128379_("BossDead", true);
        }
    }

    public static void killed(LivingEntity victim, DamageSource source) {
        AdventureData.Session s;
        if (!(victim instanceof Mob) || ShadowService.isShadow((Entity)victim) || victim.getPersistentData().m_128471_("SoulboundJourneyCounted")) {
            return;
        }
        ShadowRecord r = CompanionProgression.attacker(source);
        Entity from = CompanionProgression.source(source);
        if (r == null || from == null) {
            return;
        }
        UUID owner = ShadowService.ownerId(from);
        if (owner == null) {
            return;
        }
        victim.getPersistentData().m_128379_("SoulboundJourneyCounted", true);
        AdventureData data = AdventureData.get(victim.m_20194_());
        JourneyState st = data.journal((UUID)owner).journey;
        CompoundTag f = st.companion(r.id);
        if (victim instanceof Enemy && !AdventureWorld.isNpc((Entity)victim)) {
            f.m_128356_("Kills", Math.min(0x7FFFFFFFFFFFFFFEL, Math.max(0L, f.m_128454_("Kills"))) + 1L);
        }
        if ((s = AdventureWorld.session((Entity)victim)) != null && s.kind == AdventureData.Kind.BOSS && s.journey.m_128403_("BossNpc") && victim.getPersistentData().m_128403_("SoulboundAdventureNpc") && s.journey.m_128342_("BossNpc").equals(victim.getPersistentData().m_128342_("SoulboundAdventureNpc"))) {
            f.m_128379_("Boss", true);
        }
        data.m_77762_();
    }

    private static boolean enemy(ServerPlayer p, LivingEntity e) {
        return (e instanceof Enemy || AdventureWorld.isNpc((Entity)e)) && ShadowService.canAttack(p, e);
    }

    public static boolean domain(ServerPlayer p) {
        JourneyState st = JourneyService.state(p);
        if (!LegacyService.available(p, 3) || JourneyService.now(p) < st.data.m_128454_("DomainAt")) {
            return false;
        }
        boolean ally = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().anyMatch(r -> LegacyService.eligible(p, r.id, 4, 32) != null);
        if (!ally || JourneyService.slow(p, p.m_20182_(), 8, 0, 40, 8) == 0) {
            return false;
        }
        st.data.m_128356_("DomainAt", JourneyService.now(p) + 1200L);
        st.data.m_128356_("DomainEnd", JourneyService.now(p) + 160L);
        st.data.m_128356_("DomainPos", p.m_20183_().m_121878_());
        st.data.m_128359_("DomainDimension", p.m_9236_().m_46472_().m_135782_().toString());
        JourneyService.dirty(p);
        return true;
    }

    private static int slow(ServerPlayer p, Vec3 at, int range, int amplifier, int duration, int cap) {
        int hits = 0;
        for (LivingEntity e2 : p.m_284548_().m_6443_(LivingEntity.class, new AABB(at, at).m_82400_((double)range), e -> e.m_20238_(at) <= (double)(range * range) && JourneyService.enemy(p, e)).stream().sorted(Comparator.comparingDouble(e -> e.m_20238_(at))).limit(cap).toList()) {
            if (!e2.m_7292_(new MobEffectInstance(MobEffects.f_19597_, duration, amplifier))) continue;
            ++hits;
        }
        return hits;
    }

    public static boolean chains(ServerPlayer p, UUID id) {
        Mob m = LegacyService.eligible(p, id, 3, 32);
        JourneyState st = JourneyService.state(p);
        if (!LegacyService.available(p, 1) || m == null || JourneyService.now(p) < st.data.m_128454_("ChainsAt")) {
            return false;
        }
        Vec3 eye = p.m_146892_();
        Vec3 look = p.m_20154_();
        LivingEntity primary = p.m_284548_().m_6443_(LivingEntity.class, m.m_20191_().m_82400_(16.0), e -> e.m_20280_((Entity)m) <= 256.0 && JourneyService.enemy(p, e) && m.m_142582_((Entity)e) && p.m_142582_((Entity)e) && e.m_20191_().m_82400_(0.5).m_82371_(eye, eye.m_82549_(look.m_82490_(48.0))).isPresent()).stream().min(Comparator.comparingDouble(arg_0 -> ((ServerPlayer)p).m_20280_(arg_0))).orElse(null);
        if (primary == null) {
            return false;
        }
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
        targets.add(primary);
        targets.addAll(p.m_284548_().m_6443_(LivingEntity.class, primary.m_20191_().m_82400_(4.0), e -> e != primary && JourneyService.enemy(p, e) && e.m_20280_((Entity)primary) <= 16.0 && m.m_142582_((Entity)e)).stream().sorted(Comparator.comparingDouble(arg_0 -> ((LivingEntity)primary).m_20280_(arg_0))).limit(2L).toList());
        int hits = 0;
        for (LivingEntity e2 : targets) {
            if (!e2.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 60, 1))) continue;
            ++hits;
            Vec3 a = m.m_20182_().m_82520_(0.0, 1.0, 0.0);
            Vec3 b = e2.m_20182_().m_82520_(0.0, 1.0, 0.0);
            for (int i = 0; i <= 12; ++i) {
                Vec3 point = a.m_165921_(b, (double)i / 12.0);
                p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123745_, point.f_82479_, point.f_82480_, point.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
        if (hits == 0) {
            return false;
        }
        st.data.m_128356_("ChainsAt", JourneyService.now(p) + 500L);
        JourneyService.dirty(p);
        return true;
    }

    public static void ownerTick(ServerPlayer p) {
        Vec3 at;
        CompoundTag n = JourneyService.state((ServerPlayer)p).data;
        if (JourneyService.now(p) % 20L == 0L && JourneyService.now(p) < n.m_128454_("DomainEnd") && p.m_9236_().m_46472_().m_135782_().toString().equals(n.m_128461_("DomainDimension")) && p.m_20238_(at = Vec3.m_82512_((Vec3i)BlockPos.m_122022_((long)n.m_128454_("DomainPos")))) <= 1024.0) {
            JourneyService.slow(p, at, 8, 0, 40, 8);
            for (int i = 0; i < 32; ++i) {
                double a = (double)i * Math.PI / 16.0;
                p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123745_, at.f_82479_ + Math.cos(a) * 8.0, at.f_82480_, at.f_82481_ + Math.sin(a) * 8.0, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
        JourneyView.tick(p);
    }
}

