/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.entity.monster.Slime
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.eclipse;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseBoard;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.eclipse.EclipseText;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.royal.RoyalMutators;
import dev.shadowsovereign.warfront.WarfrontContent;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class EclipseRuns {
    public static final AdventureData.Kind[] KINDS = new AdventureData.Kind[]{AdventureData.Kind.ECLIPSE_RUN, AdventureData.Kind.REACTOR, AdventureData.Kind.INVESTIGATION, AdventureData.Kind.BOARD, AdventureData.Kind.CRYSTAL, AdventureData.Kind.CHRONOPHAGE};
    private static final UUID SPEED = UUID.fromString("11110000-0000-0000-0000-000000000010");
    private static final UUID ARMOR = UUID.fromString("11110000-0000-0000-0000-000000000011");
    private static final UUID HEALTH = UUID.fromString("11110000-0000-0000-0000-000000000012");

    public static boolean is(AdventureData.Kind k) {
        return Arrays.asList(KINDS).contains((Object)k);
    }

    public static AdventureData.Session session(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && EclipseRuns.is(s.kind) ? s : null;
    }

    public static boolean isBoard(ServerPlayer p) {
        AdventureData.Session s = EclipseRuns.session(p);
        return s != null && s.kind == AdventureData.Kind.BOARD;
    }

    public static boolean hasCard(ServerPlayer p, EclipseState.Card c) {
        AdventureData.Session s = EclipseRuns.session(p);
        return s != null && s.phase == AdventureData.Phase.ACTIVE && s.kind == AdventureData.Kind.ECLIPSE_RUN && (s.eclipse.m_128451_("Cards") & 1 << c.ordinal()) != 0;
    }

    public static boolean valid(AdventureData.Session s) {
        if (s == null || !EclipseRuns.is(s.kind) || s.eclipse.m_128451_("Mode") < 0 || s.eclipse.m_128451_("Mode") >= 6 || KINDS[s.eclipse.m_128451_("Mode")] != s.kind || s.participants.isEmpty() || s.participants.size() > 4) {
            return false;
        }
        CompoundTag n = s.eclipse;
        if (s.kind == AdventureData.Kind.BOARD) {
            ListTag units = n.m_128437_("Units", 10);
            if (units.size() != 8 || n.m_128451_("Turn") < 0) {
                return false;
            }
            HashSet<Integer> cells = new HashSet<Integer>();
            for (Tag t : units) {
                CompoundTag u = (CompoundTag)t;
                int cell = u.m_128451_("Cell");
                if (cell >= 0 && cell < 64 && u.m_128451_("Role") >= 1 && u.m_128451_("Role") <= 4 && u.m_128451_("HP") >= 0 && u.m_128451_("HP") <= 14 && (u.m_128451_("HP") <= 0 || cells.add(cell))) continue;
                return false;
            }
            return true;
        }
        if (n.m_128437_("Points", 10).size() != 3 || n.m_128454_("Deadline") < 0L) {
            return false;
        }
        if (!(s.kind != AdventureData.Kind.CRYSTAL && s.kind != AdventureData.Kind.CHRONOPHAGE || n.m_128403_("Boss"))) {
            return false;
        }
        if (s.kind == AdventureData.Kind.ECLIPSE_RUN && (n.m_128451_("Room") < 0 || n.m_128451_("Room") > 4)) {
            return false;
        }
        if (n.m_128471_("Choosing")) {
            for (int i = 0; i < 3; ++i) {
                if (n.m_128451_("Offer" + i) >= 0 && n.m_128451_("Offer" + i) <= 7) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean near(ServerPlayer p, AdventureData.Session s) {
        return s.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) && p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) <= 1600.0 && p.m_284548_().m_46805_(s.anchor) && p.m_284548_().m_8055_(s.anchor).m_60713_((Block)WarfrontContent.BEACON.get());
    }

    public static boolean start(ServerPlayer p, int mode, boolean hard, List<UUID> party) {
        List<UUID> chosen;
        int requirement;
        if (!EclipseService.usable(p) || mode < 0 || mode >= 6 || AdventureService.journal((ServerPlayer)p).session != null || !WarfrontService.near(p)) {
            return false;
        }
        EclipseState e = EclipseService.state(p);
        int n = mode == 1 ? 1 : (mode == 4 ? 2 : (requirement = mode == 5 ? 3 : 0));
        if (e.wins < requirement || mode != 3 && p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            return false;
        }
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        if (party.size() > 4 || new HashSet<UUID>(party).size() != party.size() || party.stream().anyMatch(id -> !army.shadows.containsKey(id))) {
            return false;
        }
        List<UUID> list = chosen = party.isEmpty() ? army.shadows.values().stream().filter(r -> {
            Mob m = ShadowService.active(p.f_8924_, r);
            return mode == 3 || m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 1024.0;
        }).limit(4L).map(r -> r.id).toList() : party;
        if (chosen.isEmpty() || mode != 3 && chosen.stream().anyMatch(id -> {
            Mob m = ShadowService.active(p.f_8924_, army.shadows.get(id));
            return m == null || !m.m_6084_() || m.m_9236_() != p.m_9236_() || m.m_20280_((Entity)p) > 1024.0;
        })) {
            return false;
        }
        WarfrontState w = army.warfront;
        AdventureData.Session s = new AdventureData.Session();
        s.kind = KINDS[mode];
        s.phase = AdventureData.Phase.ACTIVE;
        s.anchor = w.beacon;
        s.dimension = w.dimension;
        s.participants.addAll(chosen);
        CompoundTag n2 = s.eclipse;
        n2.m_128405_("Mode", mode);
        n2.m_128379_("Hard", hard);
        n2.m_128356_("Seed", p.m_217043_().m_188505_());
        n2.m_128356_("Clock", EclipseService.now(p));
        n2.m_128356_("Deadline", EclipseService.now(p) + (long)(mode == 0 ? 18000 : 12000));
        n2.m_128356_("NextSpawn", EclipseService.now(p));
        n2.m_128356_("Started", EclipseService.now(p));
        n2.m_128347_("Power", chosen.stream().map(army.shadows::get).mapToDouble(r -> r.tier.power * r.damageMultiplier()).sum());
        n2.m_128405_("Pressure", 50);
        n2.m_128405_("Suspect", Math.floorMod(n2.m_128454_("Seed"), 3));
        ListTag points = new ListTag();
        for (int i = 0; i < 3; ++i) {
            Vec3 at = AdventureWorld.spot(p.m_284548_(), EntityType.f_20501_, s.anchor, 6.0, 14.0, (long)(i * 7919) + s.anchor.m_121878_());
            if (at == null && mode != 3) {
                return false;
            }
            CompoundTag point = new CompoundTag();
            point.m_128356_("Pos", at == null ? s.anchor.m_121878_() : BlockPos.m_274446_((Position)at).m_121878_());
            points.add((Object)point);
        }
        n2.m_128365_("Points", (Tag)points);
        RoyalMutators.prepare(p, s);
        AdventureService.journal((ServerPlayer)p).session = s;
        for (ShadowRecord r2 : List.copyOf(army.shadows.values())) {
            if (r2.activeEntity == null || chosen.contains(r2.id)) continue;
            ShadowService.recall(p, r2);
        }
        if (mode == 3) {
            EclipseBoard.init(p, s);
        } else if (mode == 0) {
            EclipseRuns.room(p, s);
        } else if (mode >= 4) {
            EclipseRuns.boss(p, s);
        } else if (mode == 2) {
            EclipseRuns.spawnPack(p, s, 3);
        }
        EclipseService.beginReport(p, s.id, s.kind.name());
        EclipseService.dirty(p);
        return true;
    }

    public static Vec3 point(AdventureData.Session s, int i) {
        return Vec3.m_82539_((Vec3i)BlockPos.m_122022_((long)s.eclipse.m_128437_("Points", 10).m_128728_(i).m_128454_("Pos")));
    }

    public static void add(ServerPlayer p, AdventureData.Session s, EntityType<? extends Mob> type, boolean marked) {
        if (s.npcs.size() >= 10) {
            return;
        }
        AdventureData.Npc n = AdventureWorld.template(p, s, type, AdventureData.Role.ENEMY);
        Mob m = (Mob)type.m_20615_((Level)p.m_284548_());
        m.m_20258_(n.snapshot);
        double score = s.eclipse.m_128459_("Power");
        double hp = Math.min(marked ? 1600.0 : 320.0, (marked ? 260.0 + score * 55.0 : 20.0 + score * 7.0) * (s.eclipse.m_128471_("Hard") ? 1.15 : 0.85));
        if (marked && s.kind == AdventureData.Kind.INVESTIGATION && s.eclipse.m_128471_("Correct")) {
            hp *= 0.65;
        }
        m.m_21051_(Attributes.f_22276_).m_22100_(hp);
        m.m_21153_(m.m_21233_());
        if (type == EntityType.f_20497_) {
            m.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.f_42383_));
        }
        m.m_6593_((Component)EclipseText.c(marked ? (s.kind == AdventureData.Kind.CRYSTAL ? "boss.crystal" : (s.kind == AdventureData.Kind.CHRONOPHAGE ? "boss.chronophage" : (s.kind == AdventureData.Kind.INVESTIGATION ? "boss.investigation" : "boss.expedition"))) : "enemy", new Object[0]));
        n.snapshot = AdventureWorld.snapshot(m);
        s.npcs.put(n.id, n);
        if (marked) {
            s.eclipse.m_128362_("Boss", n.id);
        }
    }

    private static void spawnPack(ServerPlayer p, AdventureData.Session s, int count) {
        for (int i = 0; i < count; ++i) {
            EclipseRuns.add(p, s, (EntityType<? extends Mob>)(i % 3 == 0 ? EntityType.f_20501_ : (i % 3 == 1 ? EntityType.f_20524_ : EntityType.f_20479_)), false);
        }
    }

    private static void room(ServerPlayer p, AdventureData.Session s) {
        s.eclipse.m_128379_("Choosing", false);
        s.eclipse.m_128356_("NextSpawn", EclipseService.now(p) + 80L);
        EclipseRuns.spawnPack(p, s, Math.min(9, 3 + s.eclipse.m_128451_("Room") + RoyalMutators.extra(s)));
        if (s.eclipse.m_128451_("Room") == 4) {
            EclipseRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20518_, true);
        }
        for (UUID id : s.participants) {
            ShadowRecord r = EclipseService.record(p, id);
            Mob mob = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (mob == null) continue;
            if (EclipseRuns.hasCard(p, EclipseState.Card.SECOND_WIND)) {
                mob.m_5634_(2.0f);
            }
            if (!EclipseRuns.hasCard(p, EclipseState.Card.NIGHT_WARD)) continue;
            mob.getPersistentData().m_128350_("EclipseRoomShield", 3.0f);
        }
    }

    private static void boss(ServerPlayer p, AdventureData.Session s) {
        EclipseRuns.add(p, s, (EntityType<? extends Mob>)(s.kind == AdventureData.Kind.CRYSTAL ? EntityType.f_20518_ : EntityType.f_20497_), true);
        if (s.kind == AdventureData.Kind.CRYSTAL) {
            for (int i = 0; i < 3; ++i) {
                AdventureData.Npc n = AdventureWorld.template(p, s, (EntityType<? extends Mob>)EntityType.f_20526_, AdventureData.Role.ENEMY);
                Slime slime = (Slime)EntityType.f_20526_.m_20615_((Level)p.m_284548_());
                slime.m_7839_(1, true);
                slime.m_21557_(true);
                slime.m_146915_(true);
                slime.m_21051_(Attributes.f_22276_).m_22100_(12.0);
                slime.m_21153_(12.0f);
                slime.m_6593_((Component)EclipseText.c("crystal", i + 1));
                n.snapshot = AdventureWorld.snapshot((Mob)slime);
                n.pos = BlockPos.m_274446_((Position)EclipseRuns.point(s, i));
                s.npcs.put(n.id, n);
                s.eclipse.m_128362_("Crystal" + i, n.id);
            }
        }
        s.eclipse.m_128356_("NextStrike", EclipseService.now(p) + 120L);
    }

    public static boolean interact(ServerPlayer p) {
        AdventureData.Session s = EclipseRuns.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE || !EclipseRuns.near(p, s)) {
            return false;
        }
        CompoundTag n = s.eclipse;
        if (s.kind == AdventureData.Kind.REACTOR || s.kind == AdventureData.Kind.INVESTIGATION) {
            for (int i = 0; i < 3; ++i) {
                if (!(p.m_20238_(EclipseRuns.point(s, i)) <= 16.0) || !p.m_142582_((Entity)new MarkerEntityView((Level)p.m_284548_(), EclipseRuns.point(s, i)))) continue;
                if (s.kind == AdventureData.Kind.REACTOR) {
                    int pointIndex = i;
                    boolean ally = s.participants.stream().anyMatch(id -> {
                        ShadowRecord r = EclipseService.record(p, id);
                        Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
                        return m != null && m.m_20238_(EclipseRuns.point(s, pointIndex)) <= 25.0;
                    });
                    if (!ally || EclipseService.now(p) < n.m_128454_("InteractAt")) {
                        return false;
                    }
                    n.m_128405_("Vents", n.m_128451_("Vents") ^ 1 << i);
                    n.m_128356_("InteractAt", EclipseService.now(p) + 10L);
                } else {
                    int bit = 1 << i;
                    if ((n.m_128451_("Clues") & bit) != 0) continue;
                    n.m_128405_("Clues", n.m_128451_("Clues") | bit);
                    int evidence = i < 2 ? (n.m_128451_("Suspect") + i + 1) % 3 + 1 : n.m_128451_("Suspect") + 1;
                    n.m_128405_("Evidence" + i, evidence);
                    EclipseText.tell(p, "clue." + i, evidence);
                }
                EclipseService.dirty(p);
                return true;
            }
        }
        return false;
    }

    private static int finalIndex(int i) {
        return i;
    }

    public static boolean accuse(ServerPlayer p, int choice) {
        AdventureData.Session s = EclipseRuns.session(p);
        if (s == null || s.kind != AdventureData.Kind.INVESTIGATION || s.eclipse.m_128451_("Clues") != 7 || s.eclipse.m_128471_("Accused") || choice < 0 || choice > 2) {
            return false;
        }
        s.eclipse.m_128379_("Accused", true);
        s.eclipse.m_128379_("Correct", choice == s.eclipse.m_128451_("Suspect"));
        EclipseRuns.spawnPack(p, s, choice == s.eclipse.m_128451_("Suspect") ? 1 : 3);
        EclipseRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20513_, true);
        EclipseService.dirty(p);
        return true;
    }

    public static boolean pick(ServerPlayer p, int choice) {
        AdventureData.Session s = EclipseRuns.session(p);
        if (s == null || s.kind != AdventureData.Kind.ECLIPSE_RUN || !s.eclipse.m_128471_("Choosing") || choice < 0 || choice > 2) {
            return false;
        }
        int card = s.eclipse.m_128451_("Offer" + choice);
        if (card < 0 || card >= 8 || (s.eclipse.m_128451_("Cards") & 1 << card) != 0) {
            return false;
        }
        s.eclipse.m_128405_("Cards", s.eclipse.m_128451_("Cards") | 1 << card);
        s.eclipse.m_128405_("Room", s.eclipse.m_128451_("Room") + 1);
        EclipseRuns.room(p, s);
        EclipseService.dirty(p);
        return true;
    }

    private static void offers(AdventureData.Session s) {
        int i;
        ArrayList<Integer> pool = new ArrayList<Integer>();
        for (i = 0; i < 8; ++i) {
            if ((s.eclipse.m_128451_("Cards") & 1 << i) != 0) continue;
            pool.add(i);
        }
        Collections.shuffle(pool, new Random(s.eclipse.m_128454_("Seed") + (long)(s.eclipse.m_128451_("Room") * 37)));
        for (i = 0; i < 3; ++i) {
            s.eclipse.m_128405_("Offer" + i, ((Integer)pool.get(i)).intValue());
        }
        s.eclipse.m_128379_("Choosing", true);
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        if (!EclipseRuns.valid(s)) {
            EclipseRuns.fail(p, "invalid_run");
            return;
        }
        long now = EclipseService.now(p);
        CompoundTag n = s.eclipse;
        n.m_128356_("Clock", now);
        if (!EclipseService.usable(p)) {
            EclipseRuns.fail(p, "defeat");
            return;
        }
        if (!EclipseRuns.near(p, s)) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            return;
        }
        if (s.kind == AdventureData.Kind.BOARD) {
            return;
        }
        if (now >= n.m_128454_("Deadline")) {
            EclipseRuns.fail(p, "timeout");
            return;
        }
        if (s.participants.stream().noneMatch(id -> {
            ShadowRecord r = EclipseService.record(p, id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            return m != null && m.m_6084_();
        })) {
            if (s.participants.stream().allMatch(id -> {
                ShadowRecord r = EclipseService.record(p, id);
                return r == null || r.respawnAt > 0L;
            })) {
                EclipseRuns.fail(p, "defeat");
            } else {
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
            }
            return;
        }
        for (AdventureData.Npc npc : s.npcs.values()) {
            Mob m;
            Entity entity;
            Mob mob;
            Mob mob2 = mob = npc.active != null && (entity = p.m_284548_().m_8791_(npc.active)) instanceof Mob ? (m = (Mob)entity) : null;
            if (mob == null) {
                if (npc.active == null && AdventureWorld.spawn(p, s, npc)) continue;
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                return;
            }
            npc.snapshot = AdventureWorld.snapshot(mob);
            npc.pos = mob.m_20183_();
        }
        if (s.kind == AdventureData.Kind.ECLIPSE_RUN && s.npcs.isEmpty() && !n.m_128471_("Choosing")) {
            if (n.m_128451_("Room") >= 4) {
                EclipseRuns.win(p, s);
                return;
            }
            EclipseRuns.offers(s);
        }
        if (s.kind == AdventureData.Kind.INVESTIGATION && n.m_128471_("Accused") && s.npcs.isEmpty()) {
            EclipseRuns.win(p, s);
            return;
        }
        if (s.kind == AdventureData.Kind.REACTOR && p.f_19797_ % 20 == 0) {
            int pressure = Math.max(0, Math.min(100, n.m_128451_("Pressure") + 3 - 2 * Integer.bitCount(n.m_128451_("Vents"))));
            n.m_128405_("Pressure", pressure);
            if (pressure >= 100) {
                EclipseRuns.fail(p, "overheat");
                return;
            }
            if (pressure >= 20 && pressure <= 80) {
                n.m_128405_("Stable", n.m_128451_("Stable") + 1);
            }
            if (n.m_128451_("Stable") >= 120) {
                EclipseRuns.win(p, s);
                return;
            }
            if (now >= n.m_128454_("NextSpawn")) {
                EclipseRuns.spawnPack(p, s, Integer.bitCount(n.m_128451_("Vents")));
                n.m_128356_("NextSpawn", now + 200L);
            }
        }
        if (!(s.kind != AdventureData.Kind.CRYSTAL && s.kind != AdventureData.Kind.CHRONOPHAGE || s.npcs.containsKey(n.m_128342_("Boss")))) {
            EclipseRuns.win(p, s);
            return;
        }
        if ((s.kind == AdventureData.Kind.CRYSTAL || s.kind == AdventureData.Kind.CHRONOPHAGE) && p.f_19797_ % 5 == 0) {
            if (now >= n.m_128454_("NextStrike") && !n.m_128441_("StrikeAt")) {
                Vec3 target = s.kind == AdventureData.Kind.CHRONOPHAGE && n.m_128441_("EchoX") ? new Vec3(n.m_128459_("EchoX"), n.m_128459_("EchoY"), n.m_128459_("EchoZ")) : p.m_20182_();
                n.m_128347_("StrikeX", target.f_82479_);
                n.m_128347_("StrikeY", target.f_82480_);
                n.m_128347_("StrikeZ", target.f_82481_);
                n.m_128356_("StrikeAt", now + 40L);
            }
            if (n.m_128441_("StrikeAt")) {
                Vec3 at = new Vec3(n.m_128459_("StrikeX"), n.m_128459_("StrikeY"), n.m_128459_("StrikeZ"));
                EclipsePowers.particles(p, at, ParticleTypes.f_123745_, 8);
                if (now >= n.m_128454_("StrikeAt")) {
                    for (LivingEntity target : p.m_9236_().m_6443_(LivingEntity.class, new AABB(at, at).m_82400_(3.0), x -> x == p || p.m_20148_().equals(ShadowService.ownerId((Entity)x)))) {
                        target.m_6469_(p.m_269291_().m_269425_(), n.m_128451_("BossPhase") >= 1 ? 7.0f : 5.0f);
                    }
                    n.m_128473_("StrikeAt");
                    n.m_128356_("NextStrike", now + (long)(n.m_128451_("BossPhase") >= 1 ? 100 : 160));
                }
            }
            if (now - n.m_128454_("EchoAt") >= 40L) {
                n.m_128347_("EchoX", p.m_20185_());
                n.m_128347_("EchoY", p.m_20186_());
                n.m_128347_("EchoZ", p.m_20189_());
                n.m_128356_("EchoAt", now);
            }
        }
        EclipseService.dirty(p);
    }

    public static void win(ServerPlayer p, AdventureData.Session s) {
        if (s.phase != AdventureData.Phase.ACTIVE) {
            return;
        }
        GuideService.clear(p, s);
        EclipseState e = EclipseService.state(p);
        if (!s.eclipse.m_128471_("Awarded")) {
            if (s.kind == AdventureData.Kind.BOARD) {
                e.boardWins = Math.min(1000000, e.boardWins + 1);
            } else {
                e.wins = Math.min(1000000, e.wins + 1);
                e.marks = Math.min(1000000, e.marks + (s.kind == AdventureData.Kind.ECLIPSE_RUN ? 5 : 2) + (s.eclipse.m_128471_("Hard") ? 1 : 0) + RoyalMutators.reward(s));
                e.palettes |= 1 << Math.min(5, e.wins);
            }
            s.eclipse.m_128379_("Awarded", true);
        }
        s.phase = AdventureData.Phase.READY;
        AdventureWorld.clearEntities(p.f_8924_, s);
        EclipseRuns.removeBoons(p, s);
        EclipseService.endReport(p, "victory");
        EclipseService.dirty(p);
        EclipseText.tell(p, "victory", new Object[0]);
    }

    public static boolean finish(ServerPlayer p, boolean claim) {
        AdventureData.Session s = EclipseRuns.session(p);
        if (s == null || claim && s.phase != AdventureData.Phase.READY) {
            return false;
        }
        if (!claim && s.phase != AdventureData.Phase.READY) {
            EclipseRuns.fail(p, "abandoned");
        } else {
            EclipseRuns.removeBoons(p, s);
            AdventureWorld.clearEntities(p.f_8924_, s);
            AdventureService.journal((ServerPlayer)p).session = null;
            EclipseService.dirty(p);
        }
        return true;
    }

    public static void fail(ServerPlayer p, String reason) {
        AdventureData.Session s = EclipseRuns.session(p);
        if (s == null) {
            return;
        }
        s.phase = AdventureData.Phase.PAUSED;
        EclipseRuns.removeBoons(p, s);
        AdventureWorld.clearEntities(p.f_8924_, s);
        EclipseService.endReport(p, reason);
        AdventureService.journal((ServerPlayer)p).session = null;
        EclipseService.dirty(p);
        EclipseText.tell(p, "failed", EclipseText.c(reason, new Object[0]));
    }

    public static boolean resume(ServerPlayer p) {
        AdventureData.Session s = EclipseRuns.session(p);
        if (s == null || s.phase != AdventureData.Phase.PAUSED || !EclipseRuns.valid(s) || !EclipseService.usable(p) || !EclipseRuns.near(p, s) || s.kind != AdventureData.Kind.BOARD && p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            return false;
        }
        long delta = Math.max(0L, EclipseService.now(p) - s.eclipse.m_128454_("PausedAt"));
        for (String key : List.of("Deadline", "NextSpawn", "NextStrike", "StrikeAt", "EchoAt", "Started", "GraceUntil")) {
            if (!s.eclipse.m_128441_(key)) continue;
            s.eclipse.m_128356_(key, s.eclipse.m_128454_(key) + delta);
        }
        s.phase = AdventureData.Phase.ACTIVE;
        for (UUID id : s.participants) {
            ShadowRecord r = EclipseService.record(p, id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            String key = "Shield." + String.valueOf(id);
            if (m == null || !s.eclipse.m_128441_(key)) continue;
            m.getPersistentData().m_128350_("EclipseRoomShield", s.eclipse.m_128457_(key));
            s.eclipse.m_128473_(key);
        }
        ++s.epoch;
        s.npcs.values().forEach(n -> {
            n.active = null;
        });
        s.eclipse.m_128356_("Clock", EclipseService.now(p));
        EclipseService.dirty(p);
        return true;
    }

    private static void mod(Mob m, Attribute a, UUID id, double value) {
        AttributeInstance at = m.m_21051_(a);
        if (at == null) {
            return;
        }
        AttributeModifier old = at.m_22111_(id);
        if (old == null && value == 0.0 || old != null && Math.abs(old.m_22218_() - value) < 1.0E-4) {
            return;
        }
        float ratio = m.m_21223_() / Math.max(1.0f, m.m_21233_());
        if (old != null) {
            at.m_22120_(id);
        }
        if (value != 0.0) {
            at.m_22118_(new AttributeModifier(id, "Eclipse room card", value, a == Attributes.f_22284_ ? AttributeModifier.Operation.ADDITION : AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (a == Attributes.f_22276_) {
            m.m_21153_(Math.min(m.m_21233_(), ratio * m.m_21233_()));
            ShadowRecord r = ShadowService.record((Entity)m);
            if (r != null) {
                r.health = m.m_21223_();
                r.maxHealth = m.m_21233_();
            }
        }
    }

    public static void pauseBoons(ServerPlayer p, AdventureData.Session s) {
        for (UUID id : s.participants) {
            ShadowRecord r = EclipseService.record(p, id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m == null) continue;
            s.eclipse.m_128350_("Shield." + String.valueOf(id), m.getPersistentData().m_128457_("EclipseRoomShield"));
        }
        EclipseRuns.removeBoons(p, s);
    }

    public static void boons(Mob m, ShadowRecord r) {
        ServerPlayer p = ShadowService.owner((Entity)m);
        boolean eligible = p != null && EclipseRuns.session(p) != null && EclipseRuns.session((ServerPlayer)p).participants.contains(r.id);
        EclipseRuns.mod(m, Attributes.f_22279_, SPEED, eligible && EclipseRuns.hasCard(p, EclipseState.Card.FLEET) ? 0.1 : 0.0);
        EclipseRuns.mod(m, Attributes.f_22284_, ARMOR, eligible && EclipseRuns.hasCard(p, EclipseState.Card.IRONBARK) ? 2.0 : 0.0);
        EclipseRuns.mod(m, Attributes.f_22276_, HEALTH, eligible && EclipseRuns.hasCard(p, EclipseState.Card.VITALITY) ? 0.1 : 0.0);
        m.m_21153_(Math.min(m.m_21223_(), m.m_21233_()));
    }

    public static void removeBoons(ServerPlayer p, AdventureData.Session s) {
        for (UUID id : s.participants) {
            ShadowRecord r = EclipseService.record(p, id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m == null) continue;
            EclipseRuns.mod(m, Attributes.f_22279_, SPEED, 0.0);
            EclipseRuns.mod(m, Attributes.f_22284_, ARMOR, 0.0);
            EclipseRuns.mod(m, Attributes.f_22276_, HEALTH, 0.0);
            m.m_21153_(Math.min(m.m_21223_(), m.m_21233_()));
            m.getPersistentData().m_128473_("EclipseRoomShield");
        }
    }

    private static final class MarkerEntityView
    extends ArmorStand {
        MarkerEntityView(Level l, Vec3 p) {
            super(l, p.f_82479_, p.f_82480_, p.f_82481_);
        }
    }
}

