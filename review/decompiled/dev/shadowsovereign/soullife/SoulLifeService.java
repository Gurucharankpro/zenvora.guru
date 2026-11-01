/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.soullife;

import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.soullife.SoulLifeState;
import dev.shadowsovereign.soullife.SoulLifeText;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.vigil.VigilContent;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilService;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class SoulLifeService {
    private static List<Item> foodCandidates;

    public static ArmyData.Army army(ServerPlayer p) {
        return ArmyData.get(p.f_8924_).army(p.m_20148_());
    }

    public static SoulLifeState.Owner owner(ServerPlayer p) {
        return SoulLifeService.army((ServerPlayer)p).life;
    }

    public static long now(ServerPlayer p) {
        return p.f_8924_.m_129783_().m_46467_();
    }

    public static void dirty(ServerPlayer p) {
        ArmyData.get(p.f_8924_).m_77762_();
    }

    public static ShadowRecord record(ServerPlayer p, UUID id) {
        return id == null ? null : SoulLifeService.army((ServerPlayer)p).shadows.get(id);
    }

    public static String name(ServerPlayer p, ShadowRecord r) {
        return LegacyService.name(p, r);
    }

    public static Mob nearby(ServerPlayer p, ShadowRecord r, double radius) {
        if (r == null || SoulLifeService.record(p, r.id) != r || !p.m_6084_() || p.m_5833_() || r.pendingRespawn() || AdventureService.busy(p.f_8924_, p.m_20148_(), r.id)) {
            return null;
        }
        Mob m = ShadowService.active(p.f_8924_, r);
        return m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= radius * radius ? m : null;
    }

    public static boolean quiet(ServerPlayer p, ShadowRecord r) {
        Mob m = SoulLifeService.nearby(p, r, 8.0);
        long now = SoulLifeService.now(p);
        return !(m == null || VigilCrown.active(p) || SoulPlay.busy(p) || !SoulLifeService.owner((ServerPlayer)p).activity.m_128456_() || !VigilService.state((ServerPlayer)p).job.m_128456_() || m.m_5448_() != null || r.lastDamageAt >= 0L && now - r.lastDamageAt < 100L || VigilService.state((ServerPlayer)p).lastThreatAt > 0L && now - VigilService.state((ServerPlayer)p).lastThreatAt < 100L);
    }

    public static boolean safeFood(ServerPlayer p, ItemStack stack) {
        FoodProperties food = stack.getFoodProperties((LivingEntity)p);
        return !stack.m_41619_() && food != null && food.m_38744_() > 0 && food.m_38749_().isEmpty();
    }

    public static void ensure(ServerPlayer p, ShadowRecord r) {
        long day;
        SoulLifeState s = r.life;
        boolean changed = false;
        if (s.personality < 0) {
            s.personality = Math.floorMod(r.id.hashCode(), 6);
            changed = true;
        }
        if (s.favorite.isEmpty()) {
            List<Item> foods;
            if (foodCandidates == null) {
                foodCandidates = ForgeRegistries.ITEMS.getValues().stream().filter(i -> i.m_41472_()).sorted(Comparator.comparing(i -> String.valueOf(ForgeRegistries.ITEMS.getKey(i)))).toList();
            }
            s.favorite = String.valueOf(ForgeRegistries.ITEMS.getKey((Object)((foods = foodCandidates.stream().filter(i -> SoulLifeService.safeFood(p, new ItemStack((ItemLike)i))).toList()).isEmpty() ? Items.f_42406_ : foods.get(Math.floorMod(r.id.hashCode() * 31 + r.type.hashCode(), foods.size())))));
            changed = true;
        }
        if (s.wishDay != (day = SoulLifeService.now(p) / 24000L)) {
            s.wishDay = day;
            s.wish = Math.floorMod((int)(day ^ r.id.getLeastSignificantBits()) + s.personality, 4);
            s.wishDone = false;
            changed = true;
        }
        if (s.recapDay != day) {
            if (s.recapDay >= 0L && (s.dayCare + s.dayGames + s.dayKills + s.dayHelps > 0 || s.dayWalk >= 1.0)) {
                SoulLifeService.memory(p, r, "day", s.dayCare + "," + s.dayGames + "," + s.dayKills + "," + (int)s.dayWalk + "," + s.dayHelps);
            }
            s.recapDay = day;
            s.dayHelps = 0;
            s.dayKills = 0;
            s.dayGames = 0;
            s.dayCare = 0;
            s.dayWalk = 0.0;
            changed = true;
        }
        if (changed) {
            SoulLifeService.dirty(p);
        }
    }

    public static SoulLifeState.Mood mood(ShadowRecord r, Mob m, long now) {
        SoulLifeState s = r.life;
        if (m != null && m.m_21223_() < m.m_21233_() * 0.35f && now >= s.calmUntil) {
            return SoulLifeState.Mood.WORRIED;
        }
        if (now < s.restUntil) {
            return SoulLifeState.Mood.RESTFUL;
        }
        if (now < s.proudUntil) {
            return SoulLifeState.Mood.PROUD;
        }
        if (now < s.happyUntil) {
            return SoulLifeState.Mood.PLAYFUL;
        }
        return SoulLifeState.Mood.CONTENT;
    }

    public static void memory(ServerPlayer p, ShadowRecord r, String kind, String detail) {
        if (detail.length() > 256) {
            detail = detail.substring(0, 256);
        }
        Mob m = ShadowService.active(p.f_8924_, r);
        r.life.memories.add(new SoulLifeState.Memory(kind, SoulLifeService.now(p), m == null ? r.dimension : m.m_9236_().m_46472_().m_135782_().toString(), m == null ? r.anchor.m_121878_() : m.m_20183_().m_121878_(), detail));
        while (r.life.memories.size() > 20) {
            r.life.memories.remove(0);
        }
        SoulLifeService.dirty(p);
    }

    public static void affection(ServerPlayer p, ShadowRecord r, int amount) {
        r.life.affection = SoulLifeState.clamp(r.life.affection + Math.max(0, amount), 0, 100);
        SoulLifeService.milestones(p, r);
        SoulLifeService.dirty(p);
    }

    public static void wish(ServerPlayer p, ShadowRecord r, SoulLifeState.Wish kind) {
        SoulLifeService.ensure(p, r);
        SoulLifeState s = r.life;
        if (!s.wishDone && s.wish == kind.ordinal()) {
            s.wishDone = true;
            SoulLifeService.affection(p, r, 3);
            SoulLifeService.memory(p, r, "wish", kind.name());
            SoulLifeText.tell(p, "wish_done", SoulLifeService.name(p, r));
        }
    }

    public static void milestones(ServerPlayer p, ShadowRecord r) {
        SoulLifeState s = r.life;
        int bits = (s.pets >= 1 ? 1 : 0) | (s.meals >= 3 ? 2 : 0) | (!s.friends.isEmpty() ? 4 : 0) | (s.games >= 3 ? 8 : 0) | (s.walked >= 200.0 ? 16 : 0) | (s.rests >= 1 ? 32 : 0) | (s.helps >= 1 ? 64 : 0);
        int earned = bits & ~s.crests;
        if (earned != 0) {
            s.crests |= bits;
            SoulLifeService.memory(p, r, "crest", Integer.toString(earned));
            Mob m = SoulLifeService.nearby(p, r, 16.0);
            if (m != null) {
                SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123748_, 5);
            }
        }
    }

    public static void particles(ServerPlayer p, Mob m, ParticleOptions particle, int count) {
        p.m_284548_().m_8767_(particle, m.m_20185_(), m.m_20186_() + Math.min(2.5, (double)m.m_20206_()), m.m_20189_(), count, 0.3, 0.2, 0.3, 0.02);
    }

    public static boolean pet(ServerPlayer p, ShadowRecord r) {
        if (!SoulLifeService.quiet(p, r) || SoulLifeService.nearby(p, r, 4.0) == null || SoulLifeService.now(p) < r.life.petAt) {
            return false;
        }
        SoulLifeService.ensure(p, r);
        Mob m = SoulLifeService.nearby(p, r, 4.0);
        r.life.petAt = SoulLifeService.now(p) + 200L;
        ++r.life.pets;
        ++r.life.dayCare;
        r.life.happyUntil = SoulLifeService.now(p) + 600L;
        m.m_21563_().m_24960_((Entity)p, 35.0f, 35.0f);
        SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123750_, 4);
        SoulLifeService.affection(p, r, 1);
        SoulLifeService.memory(p, r, "pet", "");
        return true;
    }

    public static boolean feed(ServerPlayer p, ShadowRecord r) {
        if (!SoulLifeService.quiet(p, r) || SoulLifeService.nearby(p, r, 4.0) == null || SoulLifeService.now(p) < r.life.feedAt) {
            return false;
        }
        ItemStack stack = p.m_21205_();
        if (!SoulLifeService.safeFood(p, stack)) {
            return false;
        }
        SoulLifeService.ensure(p, r);
        boolean favorite = r.life.favorite.equals(String.valueOf(ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_())));
        String detail = String.valueOf(ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_()));
        ItemStack gift = stack.m_41620_(1);
        ItemStack remainder = gift.getCraftingRemainingItem();
        if (!remainder.m_41619_() && !p.m_150109_().m_36054_(remainder)) {
            p.m_36176_(remainder, false);
        }
        p.m_150109_().m_6596_();
        Mob m = SoulLifeService.nearby(p, r, 4.0);
        m.m_5634_(2.0f);
        r.health = m.m_21223_();
        r.life.feedAt = SoulLifeService.now(p) + 1200L;
        ++r.life.meals;
        ++r.life.dayCare;
        r.life.happyUntil = SoulLifeService.now(p) + 1200L;
        CompanionService.bond(p, r, 1);
        SoulLifeService.affection(p, r, favorite ? 3 : 1);
        SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123750_, favorite ? 7 : 3);
        SoulLifeService.memory(p, r, "meal", detail);
        if (favorite) {
            SoulLifeService.wish(p, r, SoulLifeState.Wish.FOOD);
        }
        SoulLifeText.tell(p, favorite ? "favorite_reply" : "meal_reply", SoulLifeService.name(p, r));
        return true;
    }

    public static boolean talk(ServerPlayer p, ShadowRecord r) {
        if (!SoulLifeService.quiet(p, r) || SoulLifeService.nearby(p, r, 4.0) == null || SoulLifeService.now(p) < r.life.talkAt) {
            return false;
        }
        SoulLifeService.ensure(p, r);
        SoulLifeState s = r.life;
        int line = p.m_217043_().m_188503_(3);
        if (line == s.lastLine) {
            line = (line + 1) % 3;
        }
        s.lastLine = line;
        s.talkAt = SoulLifeService.now(p) + 200L;
        Mob m = SoulLifeService.nearby(p, r, 4.0);
        m.m_21563_().m_24960_((Entity)p, 40.0f, 40.0f);
        SoulLifeState.Mood mood = SoulLifeService.mood(r, m, SoulLifeService.now(p));
        if (mood == SoulLifeState.Mood.WORRIED) {
            SoulLifeText.tell(p, "talk.worried", SoulLifeService.name(p, r));
        } else {
            SoulLifeText.tell(p, "talk." + s.personality + "." + line, SoulLifeService.name(p, r));
        }
        SoulLifeService.memory(p, r, "talk", Integer.toString(line));
        return true;
    }

    public static boolean praise(ServerPlayer p, ShadowRecord r) {
        if (!SoulLifeService.quiet(p, r) || SoulLifeService.nearby(p, r, 4.0) == null || r.life.killAt < 0L || SoulLifeService.now(p) - r.life.killAt > 1200L || r.life.praiseKills >= r.totalKills) {
            return false;
        }
        r.life.praiseKills = r.totalKills;
        r.life.proudUntil = SoulLifeService.now(p) + 1200L;
        SoulLifeService.affection(p, r, 2);
        CompanionService.bond(p, r, 1);
        SoulLifeService.particles(p, SoulLifeService.nearby(p, r, 4.0), (ParticleOptions)ParticleTypes.f_123748_, 5);
        SoulLifeService.memory(p, r, "praise", Long.toString(r.totalKills));
        SoulLifeText.tell(p, "praise_reply", SoulLifeService.name(p, r));
        return true;
    }

    public static boolean comfort(ServerPlayer p, ShadowRecord r) {
        if (!SoulLifeService.quiet(p, r) || SoulLifeService.nearby(p, r, 4.0) == null || SoulLifeService.now(p) < r.life.comfortAt) {
            return false;
        }
        Mob m = SoulLifeService.nearby(p, r, 4.0);
        if (SoulLifeService.mood(r, m, SoulLifeService.now(p)) != SoulLifeState.Mood.WORRIED && (r.lastDamageAt < 0L || SoulLifeService.now(p) - r.lastDamageAt > 1200L)) {
            return false;
        }
        r.life.calmUntil = SoulLifeService.now(p) + 1200L;
        r.life.comfortAt = SoulLifeService.now(p) + 1200L;
        ++r.life.dayCare;
        SoulLifeService.affection(p, r, 1);
        SoulLifeService.memory(p, r, "comfort", "");
        SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123750_, 3);
        SoulLifeText.tell(p, "comfort_reply", SoulLifeService.name(p, r));
        return true;
    }

    public static boolean keepsake(ServerPlayer p, ShadowRecord r, boolean take) {
        if (!SoulLifeService.quiet(p, r) || SoulLifeService.nearby(p, r, 4.0) == null) {
            return false;
        }
        SoulLifeState s = r.life;
        if (take) {
            if (s.keepsake.m_41619_()) {
                return false;
            }
            ItemStack gift = s.keepsake.m_41777_();
            if (!p.m_150109_().m_36054_(gift)) {
                return false;
            }
            s.keepsake = ItemStack.f_41583_;
        } else {
            ItemStack held = p.m_21205_();
            if (!s.keepsake.m_41619_() || held.m_41619_()) {
                return false;
            }
            s.keepsake = held.m_41620_(1);
            SoulLifeService.memory(p, r, "keepsake", String.valueOf(ForgeRegistries.ITEMS.getKey((Object)s.keepsake.m_41720_())));
        }
        p.m_150109_().m_6596_();
        SoulLifeService.dirty(p);
        return true;
    }

    public static boolean friends(ServerPlayer p, UUID a, UUID b, boolean unlink) {
        ShadowRecord first = SoulLifeService.record(p, a);
        ShadowRecord second = SoulLifeService.record(p, b);
        if (first == null || second == null || a.equals(b)) {
            return false;
        }
        if (unlink) {
            first.life.friends.remove(b);
            second.life.friends.remove(a);
            SoulLifeService.dirty(p);
            return true;
        }
        if (!SoulLifeService.quiet(p, first) || !SoulLifeService.quiet(p, second) || SoulLifeService.nearby(p, first, 8.0).m_20280_((Entity)SoulLifeService.nearby(p, second, 8.0)) > 64.0 || first.life.friends.containsKey(b) || first.life.friends.size() >= 3 || second.life.friends.size() >= 3) {
            return false;
        }
        first.life.friends.put(b, new SoulLifeState.Friend());
        second.life.friends.put(a, new SoulLifeState.Friend());
        SoulLifeService.memory(p, first, "friend", SoulLifeService.name(p, second));
        SoulLifeService.memory(p, second, "friend", SoulLifeService.name(p, first));
        SoulLifeService.affection(p, first, 1);
        SoulLifeService.affection(p, second, 1);
        return true;
    }

    public static void released(ServerPlayer p, ShadowRecord r) {
        if (SoulPlay.controlled(p, r)) {
            SoulPlay.cancel(p, false);
        }
        ItemStack gift = r.life.keepsake;
        r.life.keepsake = ItemStack.f_41583_;
        if (!gift.m_41619_()) {
            p.m_150109_().m_36054_(gift);
            if (!gift.m_41619_()) {
                p.m_36176_(gift, false);
            }
        }
        for (ShadowRecord other : SoulLifeService.army((ServerPlayer)p).shadows.values()) {
            other.life.friends.remove(r.id);
        }
        r.life.friends.clear();
        if (r.id.equals(SoulLifeService.owner((ServerPlayer)p).selected)) {
            SoulLifeService.owner((ServerPlayer)p).selected = null;
        }
        p.m_150109_().m_6596_();
        SoulLifeService.dirty(p);
    }

    public static void friendProgress(ServerPlayer p, ShadowRecord a, ShadowRecord b, int points) {
        int next;
        SoulLifeState.Friend fa = a.life.friends.get(b.id);
        SoulLifeState.Friend fb = b.life.friends.get(a.id);
        if (fa == null || fb == null || SoulLifeService.now(p) < Math.max(fa.nextReward, fb.nextReward)) {
            return;
        }
        fa.points = fb.points = (next = Math.min(100, Math.min(fa.points, fb.points) + points));
        fa.nextReward = fb.nextReward = SoulLifeService.now(p) + 1200L;
        SoulLifeService.dirty(p);
    }

    public static SoulPlay.Game favoriteGame(ShadowRecord r) {
        return switch (Math.floorMod(r.life.personality < 0 ? r.id.hashCode() : r.life.personality, 6)) {
            case 0 -> SoulPlay.Game.SPAR;
            case 1 -> SoulPlay.Game.REST;
            case 2 -> SoulPlay.Game.HIDE;
            case 3 -> SoulPlay.Game.DANCE;
            case 4 -> SoulPlay.Game.TAG;
            default -> SoulPlay.Game.MELODY;
        };
    }

    public static void killed(Entity killer, ShadowRecord r) {
        if (killer.m_20194_() == null) {
            return;
        }
        SoulLifeState s = r.life;
        s.killAt = killer.m_20194_().m_129783_().m_46467_();
        ServerPlayer p = ShadowService.owner(killer);
        if (p != null) {
            SoulLifeService.ensure(p, r);
            ++s.dayKills;
            if (s.memories.isEmpty() || r.totalKills == 1L || r.totalKills == 5L || r.totalKills == 15L) {
                SoulLifeService.memory(p, r, "victory", Long.toString(r.totalKills));
            }
            SoulLifeService.dirty(p);
        } else {
            long day = s.killAt / 24000L;
            if (s.recapDay != day) {
                s.recapDay = day;
                s.dayHelps = 0;
                s.dayKills = 0;
                s.dayGames = 0;
                s.dayCare = 0;
                s.dayWalk = 0.0;
            }
            ++s.dayKills;
            s.memories.add(new SoulLifeState.Memory("victory", s.killAt, killer.m_9236_().m_46472_().m_135782_().toString(), killer.m_20183_().m_121878_(), Long.toString(r.totalKills)));
            while (s.memories.size() > 20) {
                s.memories.remove(0);
            }
            ArmyData.get(killer.m_20194_()).m_77762_();
        }
    }

    public static Vec3 personalSpace(Mob m, ShadowRecord r, ServerPlayer p) {
        if (r.life.spacing == 0) {
            return null;
        }
        Vec3 facing = new Vec3(p.m_20154_().f_82479_, 0.0, p.m_20154_().f_82481_).m_82541_();
        if (facing.m_82556_() < 0.1) {
            facing = new Vec3(0.0, 0.0, 1.0);
        }
        Vec3 vec3 = p.m_20182_();
        return vec3.m_82546_(facing.m_82490_(switch (r.life.spacing) {
            case 1 -> 2.5;
            case 2 -> 5.0;
            default -> 7.0;
        }));
    }

    public static void sampleWalk(ServerPlayer p, ShadowRecord r, Mob m) {
        Optional key;
        SoulLifeState s = r.life;
        long now = SoulLifeService.now(p);
        String dimension = p.m_9236_().m_46472_().m_135782_().toString();
        if (s.lastPlayer != null && s.lastMob != null && dimension.equals(s.walkDimension) && now - s.sampledAt <= 25L && m.m_20280_((Entity)p) <= 64.0) {
            double a = p.m_20182_().m_82554_(s.lastPlayer);
            double b = m.m_20182_().m_82554_(s.lastMob);
            if (a >= 0.15 && b >= 0.15 && a <= 8.0 && b <= 8.0) {
                double travelled = Math.min(a, b);
                s.walked = Math.min(1.0E9, s.walked + travelled);
                s.dayWalk = Math.min(1.0E9, s.dayWalk + travelled);
                if (s.dayWalk >= 100.0) {
                    SoulLifeService.wish(p, r, SoulLifeState.Wish.WALK);
                }
                if ((int)(s.walked / 200.0) > (int)((s.walked - travelled) / 200.0)) {
                    SoulLifeService.affection(p, r, 2);
                    SoulLifeService.memory(p, r, "walk", Integer.toString((int)s.walked));
                }
                SoulLifeService.milestones(p, r);
                SoulLifeService.dirty(p);
            }
        }
        s.lastPlayer = p.m_20182_();
        s.lastMob = m.m_20182_();
        s.walkDimension = dimension;
        s.sampledAt = now;
        if (s.biomes.size() < 32 && (key = p.m_9236_().m_204166_(m.m_20183_()).m_203543_()).isPresent() && s.biomes.add(((ResourceKey)key.get()).m_135782_().toString())) {
            if (s.biomes.size() > 1) {
                SoulLifeService.memory(p, r, "biome", ((ResourceKey)key.get()).m_135782_().toString());
            }
            SoulLifeService.dirty(p);
        }
    }

    public static void tick(ServerPlayer p) {
        SoulPlay.tick(p);
        long now = SoulLifeService.now(p);
        if (now % 20L != 0L) {
            return;
        }
        for (ShadowRecord r : SoulLifeService.army((ServerPlayer)p).shadows.values()) {
            Mob m = ShadowService.active(p.f_8924_, r);
            SoulLifeService.friendReunions(p, r, m);
            if (m == null || m.m_9236_() != p.m_9236_()) {
                r.life.lastMob = null;
                r.life.lastPlayer = null;
                r.life.sampledAt = -1L;
                continue;
            }
            if (m.m_20280_((Entity)p) <= 1024.0) {
                SoulLifeService.ensure(p, r);
                SoulLifeService.sampleWalk(p, r, m);
                if (r.life.showMood && now % 100L == 0L) {
                    ShadowService.updateName(m, r);
                }
            }
            if (SoulLifeService.owner((ServerPlayer)p).expressions && m.m_20280_((Entity)p) <= 144.0 && now >= r.life.expressionAt && m.m_5448_() == null && !VigilCrown.active(p)) {
                SoulLifeState.Mood mood = SoulLifeService.mood(r, m, now);
                if (mood != SoulLifeState.Mood.CONTENT) {
                    SoulLifeService.particles(p, m, (ParticleOptions)(mood == SoulLifeState.Mood.WORRIED ? ParticleTypes.f_123762_ : (mood == SoulLifeState.Mood.PROUD ? ParticleTypes.f_123748_ : ParticleTypes.f_123750_)), 2);
                }
                r.life.expressionAt = now + 400L;
            }
            SoulLifeService.firstAid(p, r, m);
        }
    }

    private static void firstAid(ServerPlayer p, ShadowRecord r, Mob m) {
        if (!r.life.firstAid || r.life.affection < 25 || SoulLifeService.now(p) < SoulLifeService.owner((ServerPlayer)p).aidAt || p.m_21223_() >= p.m_21233_() / 2.0f || m.m_20280_((Entity)p) > 9.0 || r.pendingRespawn()) {
            return;
        }
        r.inventory.initialize(r.snapshot);
        for (int i = 6; i < 11; ++i) {
            ItemStack stack = (ItemStack)r.inventory.items.get(i);
            if (!stack.m_150930_((Item)VigilContent.BANDAGE.get()) || !VigilService.bandage(p)) continue;
            stack.m_41774_(1);
            SoulLifeService.owner((ServerPlayer)p).aidAt = SoulLifeService.now(p) + 400L;
            ++r.life.helps;
            ++r.life.dayHelps;
            SoulLifeService.affection(p, r, 1);
            SoulLifeService.memory(p, r, "aid", "");
            SoulLifeText.tell(p, "aid_reply", SoulLifeService.name(p, r));
            SoulLifeService.dirty(p);
            return;
        }
    }

    private static void friendReunions(ServerPlayer p, ShadowRecord r, Mob m) {
        long now = SoulLifeService.now(p);
        for (Map.Entry<UUID, SoulLifeState.Friend> e : r.life.friends.entrySet()) {
            boolean near;
            ShadowRecord other = SoulLifeService.record(p, e.getKey());
            if (other == null || r.id.compareTo(other.id) >= 0) continue;
            SoulLifeState.Friend a = e.getValue();
            SoulLifeState.Friend b = other.life.friends.get(r.id);
            if (b == null) continue;
            Mob friend = ShadowService.active(p.f_8924_, other);
            boolean bl = near = m != null && friend != null && friend.m_9236_() == m.m_9236_() && friend.m_20280_((Entity)m) <= 36.0;
            if (!near) {
                if (a.separated >= 0L) continue;
                a.separated = b.separated = now;
                SoulLifeService.dirty(p);
                continue;
            }
            boolean due = a.points >= 10 && a.separated >= 0L && now - a.separated >= 2400L && now >= Math.max(a.reunionAt, b.reunionAt);
            a.lastNear = b.lastNear = now;
            if (due && VigilService.state((ServerPlayer)p).chatter) {
                if (m.m_9236_() != p.m_9236_() || m.m_20280_((Entity)p) > 144.0 || now < SoulLifeService.owner((ServerPlayer)p).nextVoice) continue;
                SoulLifeText.tell(p, "friend_reunion", SoulLifeService.name(p, r), SoulLifeService.name(p, other));
                SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123750_, 3);
                SoulLifeService.particles(p, friend, (ParticleOptions)ParticleTypes.f_123750_, 3);
                a.reunionAt = b.reunionAt = now + 24000L;
                SoulLifeService.owner((ServerPlayer)p).nextVoice = now + 400L;
                SoulLifeService.friendProgress(p, r, other, 1);
            }
            if (a.separated < 0L) continue;
            b.separated = -1L;
            a.separated = -1L;
            SoulLifeService.dirty(p);
        }
    }

    public static boolean voidRescue(ServerPlayer p, ShadowRecord r, Mob m) {
        if (!r.life.voidRescue || m.m_20186_() >= (double)(m.m_9236_().m_141937_() - 8) || SoulLifeService.now(p) < r.life.rescueAt || !m.m_6084_() || VigilCrown.active(p) || !SoulLifeService.owner((ServerPlayer)p).activity.m_128456_()) {
            return false;
        }
        r.inventory.initialize(r.snapshot);
        for (int i = 6; i < 11; ++i) {
            ItemStack pearl = (ItemStack)r.inventory.items.get(i);
            if (!pearl.m_150930_(Items.f_42584_)) continue;
            ItemStack paid = pearl.m_41620_(1);
            if (!ShadowService.recall(p, r)) {
                pearl.m_41769_(paid.m_41613_());
                return false;
            }
            r.life.rescueAt = SoulLifeService.now(p) + 2400L;
            SoulLifeService.memory(p, r, "rescue", "");
            SoulLifeText.tell(p, "rescue_reply", SoulLifeService.name(p, r));
            SoulLifeService.dirty(p);
            return true;
        }
        return false;
    }

    public static void appendName(Mob m, ShadowRecord r) {
        if (r.life.showMood && m.m_7770_() != null) {
            long now = m.m_20194_().m_129783_().m_46467_();
            m.m_6593_((Component)m.m_7770_().m_6881_().m_7220_((Component)SoulLifeText.c("head_mood", SoulLifeText.c("mood." + SoulLifeService.mood(r, m, now).name().toLowerCase(Locale.ROOT), new Object[0]))));
        }
    }
}

