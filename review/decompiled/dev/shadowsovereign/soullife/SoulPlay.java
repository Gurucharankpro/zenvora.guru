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
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.FlyingMob
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.soullife;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.soullife.SoulLifeContent;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.soullife.SoulLifeState;
import dev.shadowsovereign.soullife.SoulLifeText;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilService;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class SoulPlay {
    public static void queueToy(ItemEntity item) {
        item.m_8127_();
        item.m_32010_(0);
        item.getPersistentData().m_128473_("SoulLifeGame");
    }

    public static void clearToyQueue() {
    }

    public static void cleanupToys(MinecraftServer server) {
    }

    public static boolean busy(ServerPlayer p) {
        return AdventureService.journal((ServerPlayer)p).session != null;
    }

    public static boolean session(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && s.kind == AdventureData.Kind.SOUL_PLAY;
    }

    public static boolean controlled(ServerPlayer p, ShadowRecord r) {
        CompoundTag n = SoulLifeService.owner((ServerPlayer)p).activity;
        if (n.m_128456_() || !SoulPlay.session(p)) {
            return false;
        }
        return n.m_128437_("Members", 10).stream().anyMatch(t -> ((CompoundTag)t).m_128403_("Id") && r.id.equals(((CompoundTag)t).m_128342_("Id")));
    }

    private static List<ShadowRecord> members(ServerPlayer p, CompoundTag n) {
        ArrayList<ShadowRecord> result = new ArrayList<ShadowRecord>();
        for (Tag tag : n.m_128437_("Members", 10)) {
            ShadowRecord r;
            CompoundTag row = (CompoundTag)tag;
            if (!row.m_128403_("Id") || (r = SoulLifeService.record(p, row.m_128342_("Id"))) == null) continue;
            result.add(r);
        }
        return result;
    }

    public static boolean start(ServerPlayer p, Game game, List<UUID> ids) {
        SoulLifeState.Owner owner = SoulLifeService.owner(p);
        if (game == Game.FETCH || ids.isEmpty() || ids.size() > 6 || new HashSet<UUID>(ids).size() != ids.size() || !owner.activity.m_128456_() || SoulPlay.busy(p) || SoulLifeService.now(p) < owner.playAt || VigilCrown.active(p) || !VigilService.state((ServerPlayer)p).job.m_128456_()) {
            return false;
        }
        for (UUID id : ids) {
            ShadowRecord r = SoulLifeService.record(p, id);
            Mob m = SoulLifeService.nearby(p, r, 8.0);
            if (m != null && SoulLifeService.quiet(p, r) && !m.m_20159_() && !m.m_20160_()) continue;
            return false;
        }
        if ((game == Game.REST || game == Game.STORY) && SoulLifeContent.near(p) == null || game == Game.SPAR && ids.size() != 2) {
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = AdventureData.Kind.SOUL_PLAY;
        s.phase = AdventureData.Phase.ACTIVE;
        s.dimension = p.m_9236_().m_46472_().m_135782_().toString();
        s.anchor = p.m_20183_();
        s.participants.addAll(ids);
        CompoundTag n = new CompoundTag();
        n.m_128362_("Id", s.id);
        n.m_128359_("Game", game.name());
        n.m_128359_("Dimension", s.dimension);
        n.m_128356_("Started", SoulLifeService.now(p));
        long l = SoulLifeService.now(p);
        n.m_128356_("Until", l + (switch (game) {
            default -> throw new IncompatibleClassChangeError();
            case Game.REST, Game.STORY -> 1200L;
            case Game.FETCH, Game.HIDE -> 2400L;
            case Game.TAG -> 600L;
            case Game.DANCE -> 160L;
            case Game.MELODY -> 100L;
            case Game.SPAR -> 600L;
        }));
        n.m_128356_("Origin", p.m_20183_().m_121878_());
        ListTag list = new ListTag();
        for (UUID id : ids) {
            CompoundTag row = new CompoundTag();
            row.m_128362_("Id", id);
            list.add((Object)row);
            SoulLifeService.ensure(p, SoulLifeService.record(p, id));
        }
        n.m_128365_("Members", (Tag)list);
        if (game == Game.REST || game == Game.STORY) {
            n.m_128356_("Hearth", SoulLifeContent.near(p).m_121878_());
        }
        if (game == Game.SPAR) {
            n.m_128405_("AHP", 20);
            n.m_128405_("BHP", 20);
            n.m_128405_("Rounds", 0);
        }
        owner.activity = n;
        owner.playAt = SoulLifeService.now(p) + 200L;
        AdventureService.journal((ServerPlayer)p).session = s;
        SoulLifeService.dirty(p);
        AdventureService.dirty(p);
        return true;
    }

    public static boolean restGroup(ServerPlayer p) {
        List<UUID> ids = SoulLifeService.army((ServerPlayer)p).shadows.values().stream().filter(r -> SoulLifeService.quiet(p, r)).limit(6L).map(r -> r.id).toList();
        return SoulPlay.start(p, Game.REST, ids);
    }

    public static boolean rest(ServerPlayer p, UUID id) {
        return id != null && SoulPlay.start(p, Game.REST, List.of(id));
    }

    public static boolean story(ServerPlayer p, UUID id, int choice, int expected) {
        CompoundTag n = SoulLifeService.owner((ServerPlayer)p).activity;
        if (n.m_128456_()) {
            return id != null && SoulPlay.start(p, Game.STORY, List.of(id));
        }
        if (!SoulPlay.session(p) || !n.m_128461_("Game").equals("STORY") || choice < 0 || choice > 2 || expected != n.m_128451_("Stage") || SoulLifeContent.near(p) == null) {
            return false;
        }
        List<ShadowRecord> records = SoulPlay.members(p, n);
        if (records.size() != 1 || !records.get((int)0).id.equals(id)) {
            return false;
        }
        int[] history = Arrays.copyOf(n.m_128465_("Choices"), expected + 1);
        history[expected] = choice;
        n.m_128385_("Choices", history);
        n.m_128405_("Stage", expected + 1);
        n.m_128405_("Choice", choice);
        if (expected >= 2) {
            ShadowRecord r = records.get(0);
            r.life.storyChoice = choice;
            r.life.storyHistory = history;
            ++r.life.stories;
            if (choice == r.life.personality % 3) {
                SoulLifeService.affection(p, r, 1);
            }
            SoulLifeService.memory(p, r, "story", Integer.toString(choice));
            SoulPlay.win(p);
        } else {
            SoulLifeService.dirty(p);
        }
        return true;
    }

    public static boolean hide(ServerPlayer p, UUID id) {
        ShadowRecord r = SoulLifeService.record(p, id);
        Mob m = SoulLifeService.nearby(p, r, 8.0);
        if (m == null) {
            return false;
        }
        Vec3 chosen = null;
        for (int i = 0; i < 16; ++i) {
            double angle = ((double)(p.m_146908_() + 90.0f) + (double)i * 137.5) * Math.PI / 180.0;
            Vec3 desired = p.m_20182_().m_82520_(Math.cos(angle) * 12.0, 0.0, Math.sin(angle) * 12.0);
            Vec3 at = ShadowService.safePosition(m, desired, p.m_284548_());
            if (at == null || !(p.m_20238_(at) >= 64.0) || !(p.m_20238_(at) <= 400.0)) continue;
            chosen = at;
            if (p.m_9236_().m_45547_(new ClipContext(p.m_146892_(), at.m_82520_(0.0, (double)m.m_20192_(), 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)p)).m_6662_() != HitResult.Type.MISS) break;
        }
        if (chosen == null || !SoulPlay.start(p, Game.HIDE, List.of(id))) {
            return false;
        }
        CompoundTag n = SoulLifeService.owner((ServerPlayer)p).activity;
        SoulPlay.putPosition(n, chosen);
        n.m_128356_("FindAfter", SoulLifeService.now(p) + 100L);
        return true;
    }

    public static boolean fetch(ServerPlayer p, ShadowRecord r, InteractionHand hand) {
        return false;
    }

    public static boolean reservedToy(MinecraftServer server, ItemEntity item) {
        if (server == null || !item.getPersistentData().m_128403_("SoulLifeGame")) {
            return false;
        }
        for (ServerPlayer p : server.m_6846_().m_11314_()) {
            CompoundTag n = SoulLifeService.owner((ServerPlayer)p).activity;
            if (!n.m_128403_("Toy") || !item.m_20148_().equals(n.m_128342_("Toy"))) continue;
            return true;
        }
        return false;
    }

    public static boolean clue(ServerPlayer p) {
        CompoundTag n = SoulLifeService.owner((ServerPlayer)p).activity;
        if (!SoulPlay.session(p) || !n.m_128461_("Game").equals("HIDE") || SoulLifeService.now(p) < n.m_128454_("ClueAt")) {
            return false;
        }
        List<ShadowRecord> records = SoulPlay.members(p, n);
        if (records.isEmpty()) {
            return false;
        }
        Mob m = SoulLifeService.nearby(p, records.get(0), 32.0);
        if (m == null) {
            return false;
        }
        n.m_128356_("ClueAt", SoulLifeService.now(p) + 200L);
        SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123746_, 8);
        SoulLifeText.tell(p, "clue", Math.round(p.m_20270_((Entity)m)));
        return true;
    }

    public static boolean moving(Mob m, ShadowRecord r, ServerPlayer p) {
        if (!SoulPlay.controlled(p, r)) {
            return false;
        }
        m.m_6710_(null);
        m.m_6274_().m_21936_(MemoryModuleType.f_26372_);
        return true;
    }

    private static void move(Mob m, Vec3 at, double speed) {
        m.m_21573_().m_26519_(at.f_82479_, at.f_82480_, at.f_82481_, speed);
    }

    private static Vec3 position(CompoundTag n) {
        return new Vec3(n.m_128459_("X"), n.m_128459_("Y"), n.m_128459_("Z"));
    }

    private static void putPosition(CompoundTag n, Vec3 at) {
        n.m_128347_("X", at.f_82479_);
        n.m_128347_("Y", at.f_82480_);
        n.m_128347_("Z", at.f_82481_);
    }

    /*
     * Unable to fully structure code
     */
    public static void tick(ServerPlayer p) {
        n = SoulLifeService.owner((ServerPlayer)p).activity;
        if (n.m_128456_()) {
            return;
        }
        records = SoulPlay.members(p, n);
        now = SoulLifeService.now(p);
        if (!SoulPlay.session(p) || !p.m_6084_() || p.m_5833_() || VigilCrown.active(p) || !n.m_128461_("Dimension").equals(p.m_9236_().m_46472_().m_135782_().toString()) || records.isEmpty() || records.size() > 6) {
            SoulPlay.cancel(p, false);
            return;
        }
        mobs = new ArrayList<Mob>();
        for (ShadowRecord r : records) {
            m = SoulLifeService.nearby(p, r, 48.0);
            if (m == null || r.lastDamageAt >= n.m_128454_("Started") && r.lastDamageAt >= 0L) {
                SoulPlay.cancel(p, true);
                return;
            }
            mobs.add(m);
        }
        if (VigilService.state((ServerPlayer)p).lastThreatAt > 0L && VigilService.state((ServerPlayer)p).lastThreatAt >= n.m_128454_("Started")) {
            SoulPlay.cancel(p, true);
            return;
        }
        if (now % 10L == 0L && !p.m_9236_().m_6443_(Mob.class, p.m_20191_().m_82400_(8.0), (Predicate<Mob>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, lambda$tick$3(net.minecraft.server.level.ServerPlayer net.minecraft.world.entity.Mob ), (Lnet/minecraft/world/entity/Mob;)Z)((ServerPlayer)p)).isEmpty()) {
            SoulPlay.cancel(p, true);
            return;
        }
        try {
            game = Game.valueOf(n.m_128461_("Game"));
        }
        catch (IllegalArgumentException e) {
            SoulPlay.cancel(p, false);
            return;
        }
        if (now >= n.m_128454_("Until")) {
            if (game == Game.TAG || game == Game.DANCE || game == Game.MELODY) {
                SoulPlay.win(p);
            } else {
                SoulPlay.cancel(p, true);
            }
            return;
        }
        m = (Mob)mobs.get(0);
        r = records.get(0);
        elapsed = now - n.m_128454_("Started");
        switch (1.$SwitchMap$dev$shadowsovereign$soullife$SoulPlay$Game[game.ordinal()]) {
            case 1: 
            case 2: {
                at = BlockPos.m_122022_((long)n.m_128454_("Hearth"));
                if (!p.m_284548_().m_46805_(at) || !((var13_13 = p.m_9236_().m_7702_(at)) instanceof SoulLifeContent.HearthEntity)) ** GOTO lbl51
                be = (SoulLifeContent.HearthEntity)var13_13;
                if (p.m_20148_().equals(be.owner) && !(p.m_20238_(Vec3.m_82512_((Vec3i)at)) > 100.0)) ** GOTO lbl54
lbl51:
                // 2 sources

                SoulPlay.cancel(p, true);
                return;
lbl54:
                // 1 sources

                settled = true;
                for (i = 0; i < mobs.size(); ++i) {
                    soul = records.get(i);
                    guest = (Mob)mobs.get(i);
                    angle = 6.283185307179586 * (double)i / (double)mobs.size();
                    spot = Vec3.m_82539_((Vec3i)at).m_82520_(Math.cos(angle) * 2.5, 0.0, Math.sin(angle) * 2.5);
                    if (guest.m_20238_(spot) > 6.25) {
                        settled = false;
                        if (now % 5L != 0L) continue;
                        SoulPlay.move(guest, spot, 0.9);
                        continue;
                    }
                    guest.m_21573_().m_26573_();
                    guest.m_21563_().m_24960_((Entity)p, 25.0f, 25.0f);
                    soul.life.restUntil = now + 60L;
                }
                if (!settled || game != Game.REST) break;
                n.m_128405_("Rested", n.m_128451_("Rested") + 1);
                if (n.m_128451_("Rested") < 200) break;
                for (ShadowRecord soul : records) {
                    ++soul.life.rests;
                    SoulLifeService.memory(p, soul, "rest", "");
                    SoulLifeService.wish(p, soul, SoulLifeState.Wish.REST);
                }
                SoulPlay.win(p);
                return;
            }
            case 3: {
                SoulPlay.cancel(p, false);
                return;
            }
            case 4: {
                at = SoulPlay.position(n);
                if (!p.m_284548_().m_46805_(BlockPos.m_274446_((Position)at))) {
                    SoulPlay.cancel(p, true);
                    return;
                }
                if (now % 5L == 0L) {
                    SoulPlay.move(m, at, 1.15);
                }
                if (now < n.m_128454_("FindAfter") || !(p.m_20280_((Entity)m) <= 9.0) || !p.m_142582_((Entity)m)) break;
                SoulLifeText.tell(p, "found", new Object[]{SoulLifeService.name(p, r)});
                SoulPlay.win(p);
                return;
            }
            case 5: {
                if (now % 5L == 0L) {
                    SoulPlay.move(m, p.m_20182_(), 1.0);
                }
                if (elapsed <= 60L || !(m.m_20280_((Entity)p) <= 4.0)) break;
                SoulLifeText.tell(p, "tagged", new Object[]{SoulLifeService.name(p, r)});
                SoulPlay.win(p);
                return;
            }
            case 6: {
                m.m_21573_().m_26573_();
                m.m_146922_(m.m_146908_() + 6.0f);
                m.m_5616_(m.m_146908_());
                m.m_146926_((float)(Math.sin((double)elapsed * 0.2) * 12.0));
                if (elapsed % 30L == 0L && m.m_20096_() && !m.m_20069_() && !(m instanceof FlyingMob)) {
                    m.m_20256_(m.m_20184_().m_82520_(0.0, 0.18, 0.0));
                    m.f_19864_ = true;
                }
                if (elapsed % 20L != 0L) break;
                SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123758_, 2);
                SoulPlay.note(p, r, (int)(elapsed / 20L) % 4);
                break;
            }
            case 7: {
                m.m_21573_().m_26573_();
                m.m_21563_().m_24960_((Entity)p, 40.0f, 40.0f);
                if (elapsed % 20L != 0L) break;
                SoulPlay.note(p, r, (int)(elapsed / 20L) % 4);
                SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123758_, 1);
                break;
            }
            case 8: {
                b = (Mob)mobs.get(1);
                if (m.m_20280_((Entity)b) > 6.25) {
                    if (now % 5L != 0L) break;
                    SoulPlay.move(m, b.m_20182_(), 1.0);
                    SoulPlay.move(b, m.m_20182_(), 1.0);
                    break;
                }
                m.m_21573_().m_26573_();
                b.m_21573_().m_26573_();
                m.m_21563_().m_24960_((Entity)b, 30.0f, 30.0f);
                b.m_21563_().m_24960_((Entity)m, 30.0f, 30.0f);
                if (elapsed % 40L != 0L) break;
                m.m_6674_(InteractionHand.MAIN_HAND);
                b.m_6674_(InteractionHand.MAIN_HAND);
                round = n.m_128451_("Rounds") + 1;
                n.m_128405_("Rounds", round);
                n.m_128405_("AHP", Math.max(0, n.m_128451_("AHP") - (2 + p.m_217043_().m_188503_(4))));
                n.m_128405_("BHP", Math.max(0, n.m_128451_("BHP") - (2 + p.m_217043_().m_188503_(4))));
                SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123797_, 3);
                SoulLifeService.particles(p, b, (ParticleOptions)ParticleTypes.f_123797_, 3);
                if (round < 5 && n.m_128451_("AHP") != 0 && n.m_128451_("BHP") != 0) break;
                SoulLifeService.friendProgress(p, r, records.get(1), 3);
                SoulPlay.win(p);
                return;
            }
        }
    }

    public static void note(ServerPlayer p, ShadowRecord r, int index) {
        int pitch = r.life.melody[Math.floorMod(index, 4)];
        Mob m = SoulLifeService.nearby(p, r, 48.0);
        Vec3 at = m == null ? p.m_20182_() : m.m_20182_();
        p.m_284548_().m_6263_(null, at.f_82479_, at.f_82480_, at.f_82481_, (SoundEvent)SoundEvents.f_12214_.m_203334_(), SoundSource.PLAYERS, 0.6f, (float)Math.pow(2.0, (double)(pitch * 2 - 12) / 12.0));
    }

    public static void win(ServerPlayer p) {
        CompoundTag n = SoulLifeService.owner((ServerPlayer)p).activity;
        if (n.m_128456_() || n.m_128471_("Awarded")) {
            return;
        }
        n.m_128379_("Awarded", true);
        String game = n.m_128461_("Game");
        OdysseyState od = GuideService.state(p);
        if (od.earned.add("social." + game)) {
            od.observe("social_types", od.earned.stream().filter(v -> v.startsWith("social.")).count());
            GuideService.dirty(p);
        }
        for (ShadowRecord r : SoulPlay.members(p, n)) {
            r.life.games = Math.min(1000000, r.life.games + 1);
            ++r.life.dayGames;
            r.life.happyUntil = SoulLifeService.now(p) + 1200L;
            if (SoulLifeService.now(p) >= r.life.rewardAt) {
                r.life.rewardAt = SoulLifeService.now(p) + 1200L;
                SoulLifeService.affection(p, r, game.equals(SoulLifeService.favoriteGame(r).name()) ? 4 : 3);
            }
            SoulLifeService.milestones(p, r);
            SoulLifeService.memory(p, r, "play", game);
            if (game.equals("REST") || game.equals("STORY")) continue;
            SoulLifeService.wish(p, r, SoulLifeState.Wish.PLAY);
        }
        SoulLifeText.tell(p, "play_done", new Object[0]);
        SoulPlay.cancel(p, false);
    }

    public static void onCommand(ServerPlayer p, ShadowRecord r) {
        if (SoulPlay.controlled(p, r)) {
            SoulPlay.cancel(p, false);
        }
    }

    public static boolean cancel(ServerPlayer p, boolean tell) {
        boolean had;
        SoulLifeState.Owner owner = SoulLifeService.owner(p);
        CompoundTag n = owner.activity;
        boolean bl = had = !n.m_128456_() || SoulPlay.session(p);
        if (n.m_128403_("Toy")) {
            for (ServerLevel world : p.f_8924_.m_129785_()) {
                Entity entity = world.m_8791_(n.m_128342_("Toy"));
                if (!(entity instanceof ItemEntity)) continue;
                ItemEntity toy = (ItemEntity)entity;
                toy.m_8127_();
                toy.m_32010_(0);
                toy.getPersistentData().m_128473_("SoulLifeGame");
            }
        }
        for (ShadowRecord r : SoulPlay.members(p, n)) {
            Mob m = ShadowService.active(p.f_8924_, r);
            if (m != null) {
                m.m_21573_().m_26573_();
                m.m_6710_(null);
                m.m_146926_(0.0f);
            }
            r.life.restUntil = n.m_128471_("Awarded") && n.m_128461_("Game").equals("REST") ? SoulLifeService.now(p) + 1200L : 0L;
        }
        owner.activity = new CompoundTag();
        if (SoulPlay.session(p)) {
            AdventureService.journal((ServerPlayer)p).session = null;
            AdventureService.dirty(p);
        }
        if (had) {
            SoulLifeService.dirty(p);
            if (tell) {
                SoulLifeText.tell(p, "play_stopped", new Object[0]);
            }
        }
        return had;
    }

    private static /* synthetic */ boolean lambda$tick$3(ServerPlayer p, Mob e) {
        return e instanceof Enemy && ShadowService.canAttack(p, (LivingEntity)e) && p.m_142582_((Entity)e);
    }

    public static enum Game {
        REST,
        STORY,
        FETCH,
        HIDE,
        TAG,
        DANCE,
        MELODY,
        SPAR;

    }
}

