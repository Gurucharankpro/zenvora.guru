/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.monster.WitherSkeleton
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.vigil;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.vigil.VigilContent;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilService;
import dev.shadowsovereign.vigil.VigilState;
import dev.shadowsovereign.vigil.VigilText;
import dev.shadowsovereign.warfront.WarfrontContent;
import dev.shadowsovereign.warfront.WarfrontService;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class VigilRuns {
    public static final AdventureData.Kind[] KINDS = new AdventureData.Kind[]{AdventureData.Kind.CONSTELLATION, AdventureData.Kind.ECHO_RHYTHM, AdventureData.Kind.ASTRAL_DICE, AdventureData.Kind.POCKET_MAZE, AdventureData.Kind.FERRYMAN};

    public static boolean is(AdventureData.Kind k) {
        return k != null && Arrays.asList(KINDS).contains((Object)k);
    }

    public static boolean abstractKind(AdventureData.Kind k) {
        return VigilRuns.is(k) && k != AdventureData.Kind.FERRYMAN;
    }

    public static AdventureData.Session session(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && VigilRuns.is(s.kind) ? s : null;
    }

    public static boolean near(ServerPlayer p, AdventureData.Session s) {
        return s.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) && p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) <= 1600.0 && p.m_284548_().m_46805_(s.anchor) && p.m_9236_().m_8055_(s.anchor).m_60713_((Block)WarfrontContent.BEACON.get());
    }

    public static boolean valid(AdventureData.Session s) {
        if (s == null || !VigilRuns.is(s.kind) || s.participants.size() > 4) {
            return false;
        }
        CompoundTag n = s.vigil;
        return switch (s.kind) {
            case AdventureData.Kind.CONSTELLATION -> {
                if (n.m_128465_("Tiles").length == 9) {
                    yield true;
                }
                yield false;
            }
            case AdventureData.Kind.ECHO_RHYTHM -> {
                if (n.m_128465_("Lanes").length == 12) {
                    yield true;
                }
                yield false;
            }
            case AdventureData.Kind.ASTRAL_DICE -> {
                if (n.m_128465_("Dice").length == 3 && n.m_128451_("Round") >= 0 && n.m_128451_("Round") <= 6) {
                    yield true;
                }
                yield false;
            }
            case AdventureData.Kind.POCKET_MAZE -> {
                if (n.m_128451_("At") >= 0 && n.m_128451_("At") < 49 && n.m_128454_("Floor") != 0L) {
                    yield true;
                }
                yield false;
            }
            case AdventureData.Kind.FERRYMAN -> {
                if (n.m_128403_("Boss") && !s.participants.isEmpty()) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    public static boolean start(ServerPlayer p, int mode, boolean hard, List<UUID> ids) {
        int i;
        if (!EclipseService.usable(p) || mode < 0 || mode >= 5 || AdventureService.journal((ServerPlayer)p).session != null || !WarfrontService.near(p) || VigilCrown.active(p) || ids.size() > 4 || new HashSet<UUID>(ids).size() != ids.size() || ids.stream().anyMatch(id -> VigilService.record(p, id) == null)) {
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = KINDS[mode];
        s.phase = AdventureData.Phase.ACTIVE;
        s.dimension = p.m_9236_().m_46472_().m_135782_().toString();
        s.anchor = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).warfront.beacon;
        CompoundTag n = s.vigil;
        long now = VigilService.now(p);
        n.m_128379_("Hard", hard);
        n.m_128356_("Started", now);
        n.m_128356_("Clock", now);
        n.m_128356_("Until", now + (long)(mode == 1 ? 440 : (hard ? 3600 : 12000)));
        n.m_128405_("Turn", 0);
        if (mode == 4) {
            List<UUID> party;
            if (p.m_9236_().m_46791_() == Difficulty.PEACEFUL || EclipseService.state((ServerPlayer)p).wins < 3) {
                return false;
            }
            List<UUID> list = party = ids.isEmpty() ? ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().filter(r -> VigilService.eligible(p, r.id) != null).limit(4L).map(r -> r.id).toList() : ids;
            if (party.isEmpty() || party.stream().anyMatch(id -> VigilService.eligible(p, id) == null)) {
                return false;
            }
            s.participants.addAll(party);
        } else if (mode == 2) {
            s.participants.addAll(ids.stream().limit(3L).toList());
        }
        if (mode == 0) {
            int[] tiles = new int[]{10, 10, 12, 5, 3, 5, 7, 5, 3};
            for (i = 0; i < 9; ++i) {
                int count = p.m_217043_().m_188503_(4);
                for (int r2 = 0; r2 < count; ++r2) {
                    tiles[i] = VigilRuns.rotate(tiles[i]);
                }
            }
            if (VigilRuns.connected(tiles)) {
                tiles[0] = VigilRuns.rotate(tiles[0]);
            }
            n.m_128385_("Tiles", tiles);
        }
        if (mode == 1) {
            int[] lanes = new int[12];
            for (i = 0; i < 12; ++i) {
                lanes[i] = p.m_217043_().m_188503_(4);
            }
            n.m_128385_("Lanes", lanes);
        }
        if (mode == 2) {
            n.m_128405_("YourHP", 40);
            n.m_128405_("EnemyHP", 36);
            VigilRuns.roll(p, n);
        }
        if (mode == 3) {
            long floor = VigilRuns.maze(p);
            n.m_128356_("Floor", floor);
            n.m_128356_("Seen", 256L);
            n.m_128405_("At", 8);
            n.m_128405_("KeyA", 12);
            n.m_128405_("KeyB", 36);
        }
        AdventureService.journal((ServerPlayer)p).session = s;
        if (mode == 4) {
            for (ShadowRecord r3 : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
                if (r3.activeEntity == null || r3.vigil.post || s.participants.contains(r3.id)) continue;
                ShadowService.recall(p, r3);
            }
            AdventureData.Npc npc = AdventureWorld.template(p, s, (EntityType<? extends Mob>)EntityType.f_20497_, AdventureData.Role.ENEMY);
            WitherSkeleton mob = (WitherSkeleton)EntityType.f_20497_.m_20615_((Level)p.m_284548_());
            mob.m_20258_(npc.snapshot.m_6426_());
            mob.m_21051_(Attributes.f_22276_).m_22100_(Math.min(800.0, (double)(160 + 40 * s.participants.size()) * (hard ? 1.25 : 1.0)));
            mob.m_21153_(mob.m_21233_());
            mob.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.f_42386_));
            mob.m_6593_((Component)VigilText.c("ferryman", new Object[0]));
            npc.snapshot = AdventureWorld.snapshot((Mob)mob);
            s.npcs.put(npc.id, npc);
            n.m_128362_("Boss", npc.id);
            n.m_128356_("NextStrike", now + 120L);
        }
        AdventureService.dirty(p);
        return true;
    }

    public static int rotate(int mask) {
        return mask << 1 & 0xF | mask >>> 3 & 1;
    }

    public static boolean connected(int[] tiles) {
        if (tiles.length != 9 || (tiles[0] & 8) == 0 || (tiles[8] & 2) == 0) {
            return false;
        }
        ArrayDeque<Integer> q = new ArrayDeque<Integer>();
        boolean[] seen = new boolean[9];
        q.add(0);
        seen[0] = true;
        int[] dx = new int[]{0, 1, 0, -1};
        int[] dy = new int[]{-1, 0, 1, 0};
        while (!q.isEmpty()) {
            int at = (Integer)q.remove();
            if (at == 8) {
                return true;
            }
            for (int d = 0; d < 4; ++d) {
                int x = at % 3 + dx[d];
                int y = at / 3 + dy[d];
                if (x < 0 || x >= 3 || y < 0 || y >= 3) continue;
                int to = y * 3 + x;
                if ((tiles[at] & 1 << d) == 0 || (tiles[to] & 1 << (d + 2) % 4) == 0 || seen[to]) continue;
                seen[to] = true;
                q.add(to);
            }
        }
        return false;
    }

    private static void roll(ServerPlayer p, CompoundTag n) {
        int[] dice = new int[3];
        for (int i = 0; i < 3; ++i) {
            dice[i] = 1 + p.m_217043_().m_188503_(6);
        }
        n.m_128385_("Dice", dice);
        n.m_128379_("Rerolled", false);
    }

    private static long maze(ServerPlayer p) {
        long floor = 256L;
        ArrayDeque<Integer> stack = new ArrayDeque<Integer>();
        HashSet<Integer> seen = new HashSet<Integer>();
        stack.push(8);
        seen.add(8);
        while (!stack.isEmpty()) {
            int at = (Integer)stack.peek();
            ArrayList<Integer> choices = new ArrayList<Integer>();
            for (int d : new int[]{-14, 14, -2, 2}) {
                int to = at + d;
                if (to / 7 < 1 || to / 7 > 5 || to % 7 < 1 || to % 7 > 5 || Math.abs(to % 7 - at % 7) + Math.abs(to / 7 - at / 7) != 2 || seen.contains(to)) continue;
                choices.add(to);
            }
            if (choices.isEmpty()) {
                stack.pop();
                continue;
            }
            int next = (Integer)choices.get(p.m_217043_().m_188503_(choices.size()));
            floor |= 1L << next;
            floor |= 1L << (next + at) / 2;
            seen.add(next);
            stack.push(next);
        }
        return floor;
    }

    public static boolean input(ServerPlayer p, int index, int value, int turn) {
        AdventureData.Session s = VigilRuns.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE || !VigilRuns.valid(s) || !VigilRuns.near(p, s) || turn != s.vigil.m_128451_("Turn")) {
            return false;
        }
        CompoundTag n = s.vigil;
        switch (s.kind) {
            case CONSTELLATION: {
                if (index < 0 || index >= 9) {
                    return false;
                }
                int[] tiles = n.m_128465_("Tiles");
                tiles[index] = VigilRuns.rotate(tiles[index]);
                n.m_128385_("Tiles", tiles);
                n.m_128405_("Moves", n.m_128451_("Moves") + 1);
                if (!VigilRuns.connected(tiles)) break;
                VigilRuns.win(p, s);
                break;
            }
            case ECHO_RHYTHM: {
                if (index < 0 || index >= 4) {
                    return false;
                }
                long elapsed = VigilService.now(p) - n.m_128454_("Started") - 40L;
                int beat = (int)Math.floorDiv(elapsed + 15L, 30);
                if (beat < 0 || beat >= 12 || (n.m_128451_("Attempted") & 1 << beat) != 0) {
                    return false;
                }
                int tolerance = n.m_128471_("Hard") ? 4 : 8;
                boolean hit = Math.abs(elapsed - (long)(beat * 30)) <= (long)tolerance && n.m_128465_("Lanes")[beat] == index;
                n.m_128405_("Attempted", n.m_128451_("Attempted") | 1 << beat);
                if (hit) {
                    n.m_128405_("Score", n.m_128451_("Score") + 1);
                }
                n.m_128379_("Hit", hit);
                p.m_6330_(hit ? (SoundEvent)SoundEvents.f_12211_.m_203334_() : (SoundEvent)SoundEvents.f_12209_.m_203334_(), SoundSource.PLAYERS, 0.7f, hit ? 1.4f : 0.7f);
                break;
            }
            case ASTRAL_DICE: {
                if (index == 2) {
                    if (value < 0 || value >= 3 || n.m_128471_("Rerolled")) {
                        return false;
                    }
                    int[] dice = n.m_128465_("Dice");
                    dice[value] = 1 + p.m_217043_().m_188503_(6);
                    n.m_128385_("Dice", dice);
                    n.m_128379_("Rerolled", true);
                    break;
                }
                if (index == 0 || index == 1) {
                    int total = Arrays.stream(n.m_128465_("Dice")).sum();
                    int dealt = index == 0 ? total : Math.max(1, total / 3);
                    n.m_128405_("EnemyHP", Math.max(0, n.m_128451_("EnemyHP") - dealt));
                    int incoming = 8 + p.m_217043_().m_188503_(5) - (index == 1 ? total / 2 : 0);
                    n.m_128405_("YourHP", Math.max(0, n.m_128451_("YourHP") - Math.max(0, incoming)));
                    n.m_128405_("Round", n.m_128451_("Round") + 1);
                    if (n.m_128451_("EnemyHP") == 0) {
                        VigilRuns.win(p, s);
                        break;
                    }
                    if (n.m_128451_("YourHP") == 0 || n.m_128451_("Round") >= 6) {
                        VigilRuns.finish(p, false);
                        return true;
                    }
                    VigilRuns.roll(p, n);
                    break;
                }
                return false;
            }
            case POCKET_MAZE: {
                if (index < 0 || index >= 49) {
                    return false;
                }
                int at = n.m_128451_("At");
                if (Math.abs(index % 7 - at % 7) + Math.abs(index / 7 - at / 7) != 1 || (n.m_128454_("Floor") & 1L << index) == 0L) {
                    return false;
                }
                n.m_128405_("At", index);
                n.m_128356_("Seen", n.m_128454_("Seen") | 1L << index);
                n.m_128405_("Moves", n.m_128451_("Moves") + 1);
                if (index == n.m_128451_("KeyA")) {
                    n.m_128405_("Keys", n.m_128451_("Keys") | 1);
                }
                if (index == n.m_128451_("KeyB")) {
                    n.m_128405_("Keys", n.m_128451_("Keys") | 2);
                }
                if (index != 40 || n.m_128451_("Keys") != 3) break;
                VigilRuns.win(p, s);
                break;
            }
            default: {
                return false;
            }
        }
        n.m_128405_("Turn", turn + 1);
        AdventureService.dirty(p);
        return true;
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        long beat;
        if (s.phase != AdventureData.Phase.ACTIVE) {
            return;
        }
        if (!VigilRuns.valid(s)) {
            VigilRuns.finish(p, false);
            return;
        }
        long now = VigilService.now(p);
        if (s.vigil.m_128454_("LastTick") == now) {
            return;
        }
        s.vigil.m_128356_("LastTick", now);
        s.vigil.m_128356_("Clock", now);
        if (!p.m_6084_()) {
            VigilRuns.finish(p, false);
            return;
        }
        if (!VigilRuns.near(p, s)) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            return;
        }
        CompoundTag n = s.vigil;
        if (s.kind == AdventureData.Kind.ECHO_RHYTHM && now - n.m_128454_("Started") >= 400L) {
            if (n.m_128451_("Score") >= 6) {
                VigilRuns.win(p, s);
            } else {
                VigilRuns.finish(p, false);
            }
            return;
        }
        if (now >= n.m_128454_("Until")) {
            VigilRuns.finish(p, false);
            return;
        }
        if (s.kind == AdventureData.Kind.ECHO_RHYTHM && (beat = Math.floorDiv(now - n.m_128454_("Started") - 40L, 30)) >= 0L && beat < 12L && n.m_128451_("BeatPlayed") < (int)beat + 1) {
            n.m_128405_("BeatPlayed", (int)beat + 1);
            p.m_6330_((SoundEvent)SoundEvents.f_12215_.m_203334_(), SoundSource.PLAYERS, 0.65f, 1.0f);
        }
        if (s.kind != AdventureData.Kind.FERRYMAN) {
            return;
        }
        if (s.participants.stream().noneMatch(id -> VigilService.eligible(p, id) != null)) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            return;
        }
        if (!s.npcs.containsKey(n.m_128342_("Boss"))) {
            VigilRuns.win(p, s);
            return;
        }
        for (AdventureData.Npc npc : s.npcs.values()) {
            Mob mob;
            Entity entity;
            Mob m;
            Mob mob2 = m = npc.active != null && (entity = p.m_284548_().m_8791_(npc.active)) instanceof Mob ? (mob = (Mob)entity) : null;
            if (m == null) {
                if (npc.active == null && AdventureWorld.spawn(p, s, npc)) continue;
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                return;
            }
            npc.snapshot = AdventureWorld.snapshot(m);
            npc.pos = m.m_20183_();
        }
        if (n.m_128441_("StrikeAt") && now >= n.m_128454_("StrikeAt")) {
            AdventureData.Npc bossNpc = s.npcs.get(n.m_128342_("Boss"));
            Entity boss = bossNpc != null && bossNpc.active != null ? p.m_284548_().m_8791_(bossNpc.active) : null;
            Vec3 at = new Vec3(n.m_128459_("X"), n.m_128459_("Y"), n.m_128459_("Z"));
            if (boss != null) {
                for (LivingEntity entity : p.m_9236_().m_6443_(LivingEntity.class, new AABB(at, at).m_82400_(2.0), e -> e == p || s.participants.stream().anyMatch(id -> {
                    ShadowRecord r = VigilService.record(p, id);
                    return r != null && e.m_20148_().equals(r.activeEntity);
                }))) {
                    entity.m_6469_(p.m_269291_().m_269104_(boss, boss), 4.0f);
                }
            }
            n.m_128473_("StrikeAt");
            n.m_128356_("NextStrike", now + 120L);
        } else if (!n.m_128441_("StrikeAt") && now >= n.m_128454_("NextStrike")) {
            n.m_128356_("StrikeAt", now + 40L);
            n.m_128347_("X", p.m_20185_());
            n.m_128347_("Y", p.m_20186_());
            n.m_128347_("Z", p.m_20189_());
            VigilText.tell(p, "ferryman_warning", new Object[0]);
        }
        AdventureService.dirty(p);
    }

    public static void win(ServerPlayer p, AdventureData.Session s) {
        if (s.phase != AdventureData.Phase.ACTIVE || s.vigil.m_128471_("Awarded")) {
            return;
        }
        GuideService.clear(p, s);
        s.vigil.m_128379_("Awarded", true);
        s.phase = AdventureData.Phase.READY;
        VigilState v = VigilService.state(p);
        switch (s.kind) {
            case CONSTELLATION: {
                v.circuitBest = Math.min(v.circuitBest, s.vigil.m_128451_("Moves"));
                break;
            }
            case ECHO_RHYTHM: {
                v.rhythmBest = Math.max(v.rhythmBest, s.vigil.m_128451_("Score"));
                break;
            }
            case ASTRAL_DICE: {
                v.diceBest = Math.max(v.diceBest, s.vigil.m_128451_("YourHP"));
                break;
            }
            case POCKET_MAZE: {
                v.mazeBest = Math.min(v.mazeBest, s.vigil.m_128451_("Moves"));
                break;
            }
            case FERRYMAN: {
                ++v.ferrymanWins;
                ++EclipseService.state((ServerPlayer)p).wins;
                EclipseService.state((ServerPlayer)p).marks = EclipseService.state((ServerPlayer)p).marks + (s.vigil.m_128471_("Hard") ? 4 : 3);
                ItemStack trophy = new ItemStack((ItemLike)VigilContent.TROPHY.get());
                if (!p.m_150109_().m_36054_(trophy)) {
                    p.m_36176_(trophy, false);
                }
                AdventureWorld.clearEntities(p.f_8924_, s);
                break;
            }
        }
        VigilService.dirty(p);
        AdventureService.dirty(p);
        VigilText.tell(p, "won", new Object[0]);
    }

    public static boolean finish(ServerPlayer p, boolean claim) {
        AdventureData.Session s = VigilRuns.session(p);
        if (s == null || claim && s.phase != AdventureData.Phase.READY) {
            return false;
        }
        AdventureWorld.clearEntities(p.f_8924_, s);
        AdventureService.journal((ServerPlayer)p).session = null;
        AdventureService.dirty(p);
        return true;
    }

    public static boolean resume(ServerPlayer p) {
        AdventureData.Session s = VigilRuns.session(p);
        if (s == null || s.phase != AdventureData.Phase.PAUSED || !VigilRuns.valid(s) || !VigilRuns.near(p, s) || !EclipseService.usable(p) || s.kind == AdventureData.Kind.FERRYMAN && s.participants.stream().anyMatch(id -> VigilService.eligible(p, id) == null)) {
            return false;
        }
        long delta = Math.max(0L, VigilService.now(p) - s.vigil.m_128454_("PausedAt"));
        for (String key : List.of("Started", "Until", "NextStrike", "StrikeAt", "GraceUntil")) {
            if (!s.vigil.m_128441_(key)) continue;
            s.vigil.m_128356_(key, s.vigil.m_128454_(key) + delta);
        }
        ++s.epoch;
        s.phase = AdventureData.Phase.ACTIVE;
        s.npcs.values().forEach(n -> {
            n.active = null;
        });
        s.vigil.m_128473_("LastTick");
        AdventureService.dirty(p);
        return true;
    }
}

