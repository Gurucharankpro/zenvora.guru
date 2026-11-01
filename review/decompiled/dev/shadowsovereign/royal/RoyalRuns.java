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
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.monster.Slime
 *  net.minecraft.world.entity.projectile.AbstractArrow$Pickup
 *  net.minecraft.world.entity.projectile.Arrow
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.EntityTeleportEvent$EnderEntity
 */
package dev.shadowsovereign.royal;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.royal.RoyalText;
import dev.shadowsovereign.warfront.WarfrontContent;
import dev.shadowsovereign.warfront.WarfrontService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public final class RoyalRuns {
    public static final AdventureData.Kind[] KINDS = new AdventureData.Kind[]{AdventureData.Kind.TRAINING, AdventureData.Kind.SKILL_COURSE, AdventureData.Kind.SEAL_PUZZLE, AdventureData.Kind.MIMIC};

    public static boolean is(AdventureData.Kind k) {
        return Arrays.asList(KINDS).contains((Object)k);
    }

    public static AdventureData.Session session(ServerPlayer p) {
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        return s != null && RoyalRuns.is(s.kind) ? s : null;
    }

    public static boolean near(ServerPlayer p, AdventureData.Session s) {
        return s.dimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) && p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) <= 1600.0 && p.m_284548_().m_46805_(s.anchor) && p.m_9236_().m_8055_(s.anchor).m_60713_((Block)WarfrontContent.BEACON.get());
    }

    public static boolean valid(AdventureData.Session s) {
        if (s == null || !RoyalRuns.is(s.kind) || s.participants.size() > 4) {
            return false;
        }
        CompoundTag n = s.royal;
        int mode = n.m_128451_("Mode");
        if (mode < 0 || mode >= 4 || KINDS[mode] != s.kind) {
            return false;
        }
        if (s.kind == AdventureData.Kind.SEAL_PUZZLE) {
            return n.m_128451_("Mask") >= 0 && n.m_128451_("Mask") <= 65535 && n.m_128451_("Moves") >= 0;
        }
        if (n.m_128437_("Points", 10).size() != 3 || n.m_128454_("Deadline") < 0L) {
            return false;
        }
        if (s.kind == AdventureData.Kind.MIMIC) {
            return !s.participants.isEmpty() && n.m_128403_("Boss") && n.m_128451_("BossPhase") >= 0 && n.m_128451_("BossPhase") <= 2;
        }
        if (s.kind == AdventureData.Kind.SKILL_COURSE) {
            return n.m_128451_("Stage") >= 0 && n.m_128451_("Stage") <= 2;
        }
        return true;
    }

    public static boolean start(ServerPlayer p, int mode, boolean hard, List<UUID> ids) {
        List party;
        if (!EclipseService.usable(p) || mode < 0 || mode >= 4 || AdventureService.journal((ServerPlayer)p).session != null || !WarfrontService.near(p) || ids.size() > 4 || new HashSet<UUID>(ids).size() != ids.size()) {
            return false;
        }
        if (mode == 1 && (EclipseService.state((ServerPlayer)p).wins < 2 || p.m_150110_().f_35934_)) {
            RoyalText.tell(p, "course_requirement", new Object[0]);
            return false;
        }
        if (mode == 3 && EclipseService.state((ServerPlayer)p).wins < 3) {
            return false;
        }
        if (mode != 2 && p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            return false;
        }
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        if (ids.stream().anyMatch(id -> !army.shadows.containsKey(id))) {
            return false;
        }
        List<Object> list = mode == 1 || mode == 2 ? List.of() : (party = ids.isEmpty() ? army.shadows.values().stream().filter(r -> {
            Mob m = ShadowService.active(p.f_8924_, r);
            return m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 1024.0;
        }).limit(4L).map(r -> r.id).toList() : List.copyOf(ids));
        if (mode == 3 && party.isEmpty() || party.stream().anyMatch(id -> {
            Mob m = ShadowService.active(p.f_8924_, army.shadows.get(id));
            return m == null || !m.m_6084_() || m.m_9236_() != p.m_9236_() || m.m_20280_((Entity)p) > 1024.0;
        })) {
            return false;
        }
        AdventureData.Session s = new AdventureData.Session();
        s.kind = KINDS[mode];
        s.phase = AdventureData.Phase.ACTIVE;
        s.anchor = army.warfront.beacon;
        s.dimension = army.warfront.dimension;
        s.participants.addAll(party);
        CompoundTag n = s.royal;
        long now = RoyalService.now(p);
        n.m_128405_("Mode", mode);
        n.m_128379_("Hard", hard);
        n.m_128356_("Clock", now);
        n.m_128356_("Started", now);
        n.m_128356_("Deadline", now + (mode == 0 ? 1200L : (mode == 1 && !hard ? 0x1FFFFFFFFFFFFFFFL : (mode == 1 ? 1800L : 12000L))));
        n.m_128356_("NextCopy", now + 160L);
        n.m_128356_("NextShot", now + 60L);
        ListTag points = new ListTag();
        for (int i = 0; i < 3; ++i) {
            Vec3 at = AdventureWorld.spot(p.m_284548_(), EntityType.f_20501_, i == 0 && mode == 1 ? p.m_20183_() : s.anchor, i == 0 && mode == 1 ? 3.0 : 5.0, i == 0 && mode == 1 ? 5.0 : 12.0, (long)i * 137L + (long)p.m_20148_().hashCode());
            if (at == null && mode != 2) {
                return false;
            }
            CompoundTag point = new CompoundTag();
            point.m_128356_("Pos", at == null ? s.anchor.m_121878_() : BlockPos.m_274446_((Position)at).m_121878_());
            points.add((Object)point);
        }
        n.m_128365_("Points", (Tag)points);
        if (mode != 2) {
            for (ShadowRecord r2 : List.copyOf(army.shadows.values())) {
                if (r2.activeEntity == null || party.contains(r2.id)) continue;
                ShadowService.recall(p, r2);
            }
        }
        AdventureService.journal((ServerPlayer)p).session = s;
        if (mode == 0) {
            AdventureData.Npc npc = RoyalRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20501_, true, 0);
            n.m_128362_("Dummy", npc.id);
        }
        if (mode == 2) {
            int mask;
            do {
                mask = 0;
                for (int i = 0; i < 12; ++i) {
                    mask = RoyalRuns.toggle(mask, p.m_217043_().m_188503_(16));
                }
            } while (mask == 0);
            n.m_128405_("Mask", mask);
            n.m_128405_("Turn", 0);
        }
        if (mode == 3) {
            AdventureData.Npc boss = RoyalRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20459_, false, 0);
            n.m_128362_("Boss", boss.id);
        }
        EclipseService.beginReport(p, s.id, s.kind.name());
        AdventureService.dirty(p);
        return true;
    }

    private static AdventureData.Npc add(ServerPlayer p, AdventureData.Session s, EntityType<? extends Mob> type, boolean fixed, int point) {
        AdventureData.Npc npc = AdventureWorld.template(p, s, type, AdventureData.Role.ENEMY);
        Mob mob = (Mob)type.m_20615_((Level)p.m_284548_());
        mob.m_20258_(npc.snapshot.m_6426_());
        if (mob instanceof Slime) {
            Slime slime = (Slime)mob;
            slime.m_7839_(1, true);
        }
        if (fixed) {
            mob.m_21557_(true);
            mob.m_20242_(true);
            mob.m_146915_(true);
        }
        double score = s.participants.stream().map(id -> EclipseService.record(p, id)).filter(Objects::nonNull).mapToDouble(r -> r.tier.power * r.damageMultiplier()).sum();
        double health = s.kind == AdventureData.Kind.TRAINING ? 1024.0 : (s.kind == AdventureData.Kind.MIMIC ? Math.min(1600.0, 240.0 + 40.0 * score) * (s.royal.m_128471_("Hard") ? 1.15 : 1.0) : 20.0);
        mob.m_21051_(Attributes.f_22276_).m_22100_(health);
        mob.m_21153_(mob.m_21233_());
        mob.m_6593_((Component)RoyalText.c(s.kind == AdventureData.Kind.TRAINING ? "training_dummy" : (s.kind == AdventureData.Kind.MIMIC ? "mimic_name" : (type == EntityType.f_20524_ ? "course_archer" : "course_target")), new Object[0]));
        npc.snapshot = AdventureWorld.snapshot(mob);
        npc.pos = BlockPos.m_274446_((Position)RoyalRuns.point(s, point));
        s.npcs.put(npc.id, npc);
        return npc;
    }

    public static Vec3 point(AdventureData.Session s, int index) {
        return Vec3.m_82539_((Vec3i)BlockPos.m_122022_((long)s.royal.m_128437_("Points", 10).m_128728_(index).m_128454_("Pos")));
    }

    public static int toggle(int mask, int cell) {
        if (cell < 0 || cell >= 16) {
            return mask;
        }
        for (int at : new int[]{cell, cell - 4, cell + 4, cell - 1, cell + 1}) {
            if (at < 0 || at >= 16 || Math.abs(at % 4 - cell % 4) + Math.abs(at / 4 - cell / 4) > 1) continue;
            mask ^= 1 << at;
        }
        return mask;
    }

    public static boolean puzzle(ServerPlayer p, int cell, int turn) {
        AdventureData.Session s = RoyalRuns.session(p);
        if (s == null || s.kind != AdventureData.Kind.SEAL_PUZZLE || s.phase != AdventureData.Phase.ACTIVE || !RoyalRuns.near(p, s) || cell < 0 || cell >= 16 || turn != s.royal.m_128451_("Turn")) {
            return false;
        }
        CompoundTag n = s.royal;
        n.m_128405_("Mask", RoyalRuns.toggle(n.m_128451_("Mask"), cell));
        n.m_128405_("Moves", Math.min(1000000, n.m_128451_("Moves") + 1));
        n.m_128405_("Turn", turn + 1);
        if (n.m_128451_("Mask") == 0) {
            RoyalRuns.win(p, s);
        }
        AdventureService.dirty(p);
        return true;
    }

    public static void power(ServerPlayer p, EclipseState.Power power) {
        AdventureData.Session s = RoyalRuns.session(p);
        if (s == null || s.phase != AdventureData.Phase.ACTIVE) {
            return;
        }
        if (s.kind == AdventureData.Kind.MIMIC) {
            s.royal.m_128359_("LastPower", power.name());
        }
        if (s.kind == AdventureData.Kind.SKILL_COURSE && s.royal.m_128451_("Stage") == 0 && power == EclipseState.Power.DASH && p.m_20238_(RoyalRuns.point(s, 0)) <= 9.0) {
            s.royal.m_128405_("Stage", 1);
            for (int i = 0; i < 3; ++i) {
                RoyalRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20526_, true, i);
            }
            AdventureService.dirty(p);
        }
    }

    public static boolean courseTarget(ServerPlayer p, LivingEntity target) {
        AdventureData.Session s = RoyalRuns.session(p);
        return s != null && s.kind == AdventureData.Kind.SKILL_COURSE && s.royal.m_128451_("Stage") == 1 && AdventureWorld.session((Entity)target) == s;
    }

    public static void hitTarget(ServerPlayer p, Mob mob) {
        AdventureData.Session s = RoyalRuns.session(p);
        if (s == null || !RoyalRuns.courseTarget(p, (LivingEntity)mob)) {
            return;
        }
        AdventureData.Npc npc = AdventureWorld.npc((Entity)mob);
        if (npc == null) {
            return;
        }
        s.npcs.remove(npc.id);
        mob.getPersistentData().m_128405_("SoulboundAdventureEpoch", -1);
        mob.m_146870_();
        EclipsePowers.particles(p, mob.m_20182_(), ParticleTypes.f_123810_, 8);
        if (s.npcs.isEmpty()) {
            s.royal.m_128405_("Stage", 2);
            AdventureData.Npc shooter = RoyalRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20524_, true, 1);
            s.royal.m_128362_("Shooter", shooter.id);
            s.royal.m_128356_("NextShot", RoyalService.now(p) + 60L);
        }
        AdventureService.dirty(p);
    }

    public static void parried(ServerPlayer p, Entity source) {
        AdventureData.Session s = RoyalRuns.session(p);
        if (s != null && s.kind == AdventureData.Kind.SKILL_COURSE && s.royal.m_128451_("Stage") == 2 && source != null && AdventureWorld.session(source) == s) {
            RoyalRuns.win(p, s);
        }
    }

    public static void trainingDamage(ServerPlayer p, LivingEntity target, float amount, Entity actor) {
        String key;
        AdventureData.Session s = RoyalRuns.session(p);
        if (s == null || s.kind != AdventureData.Kind.TRAINING || AdventureWorld.session((Entity)target) != s) {
            return;
        }
        CompoundTag n = s.royal;
        String string = actor == p ? "PlayerDamage" : (key = actor != null && ShadowService.record(actor) != null ? "Damage." + String.valueOf(ShadowService.record((Entity)actor).id) : "");
        if (!key.isEmpty()) {
            n.m_128347_(key, Math.min(1.0E12, n.m_128459_(key) + (double)Math.max(0.0f, amount)));
        }
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        Mob m;
        if (!RoyalRuns.valid(s)) {
            RoyalRuns.fail(p, "invalid_activity");
            return;
        }
        CompoundTag n = s.royal;
        long now = RoyalService.now(p);
        n.m_128356_("Clock", now);
        if (!p.m_6084_()) {
            RoyalRuns.fail(p, "defeat");
            return;
        }
        if (!RoyalRuns.near(p, s)) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
            return;
        }
        if (s.kind == AdventureData.Kind.SEAL_PUZZLE) {
            return;
        }
        if (s.kind == AdventureData.Kind.TRAINING && now >= n.m_128454_("Deadline")) {
            RoyalRuns.win(p, s);
            return;
        }
        if (now >= n.m_128454_("Deadline")) {
            RoyalRuns.fail(p, "time_expired");
            return;
        }
        RoyalRuns.cleanArrows(p, s, false);
        for (AdventureData.Npc npc : s.npcs.values()) {
            Mob mob;
            Entity entity;
            Mob mob2 = m = npc.active != null && (entity = p.m_284548_().m_8791_(npc.active)) instanceof Mob ? (mob = (Mob)entity) : null;
            if (m == null) {
                if (npc.active == null && AdventureWorld.spawn(p, s, npc)) continue;
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                return;
            }
            if (s.kind == AdventureData.Kind.TRAINING) {
                m.m_21153_(m.m_21233_());
            }
            npc.snapshot = AdventureWorld.snapshot(m);
            npc.pos = m.m_20183_();
        }
        if (s.kind == AdventureData.Kind.SKILL_COURSE && n.m_128451_("Stage") == 2 && now >= n.m_128454_("NextShot")) {
            Mob shooter;
            AdventureData.Npc npc = s.npcs.get(n.m_128342_("Shooter"));
            if (npc != null && npc.active != null && (m = p.m_284548_().m_8791_(npc.active)) instanceof Mob && (shooter = m).m_142582_((Entity)p)) {
                Arrow arrow = new Arrow(p.m_9236_(), (LivingEntity)shooter);
                arrow.m_36781_(2.0);
                arrow.f_36705_ = AbstractArrow.Pickup.DISALLOWED;
                arrow.getPersistentData().m_128379_("RoyalCourseArrow", true);
                Vec3 d = p.m_146892_().m_82546_(arrow.m_20182_());
                arrow.m_6686_(d.f_82479_, d.f_82480_, d.f_82481_, 1.2f, 0.0f);
                p.m_284548_().m_7967_((Entity)arrow);
                ListTag arrows = n.m_128437_("Arrows", 10);
                CompoundTag entry = new CompoundTag();
                entry.m_128362_("Id", arrow.m_20148_());
                entry.m_128356_("Born", now);
                arrows.add((Object)entry);
                n.m_128365_("Arrows", (Tag)arrows);
            }
            n.m_128356_("NextShot", now + 60L);
        }
        if (s.kind == AdventureData.Kind.MIMIC) {
            if (s.participants.stream().noneMatch(id -> {
                ShadowRecord r = EclipseService.record(p, id);
                Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
                return m != null && m.m_6084_();
            })) {
                RoyalRuns.fail(p, "defeat");
                return;
            }
            if (!s.npcs.containsKey(n.m_128342_("Boss"))) {
                RoyalRuns.win(p, s);
                return;
            }
            if (now >= n.m_128454_("NextCopy") && !n.m_128441_("CastAt")) {
                String last = n.m_128461_("LastPower");
                if (last.isEmpty()) {
                    last = n.m_128471_("Fallback") ? "WELL" : "LANCE";
                }
                n.m_128379_("Fallback", !n.m_128471_("Fallback"));
                n.m_128359_("Copy", last);
                n.m_128356_("CastAt", now + 40L);
                n.m_128347_("X", p.m_20185_());
                n.m_128347_("Y", p.m_20186_());
                n.m_128347_("Z", p.m_20189_());
            }
            if (n.m_128441_("CastAt") && now >= n.m_128454_("CastAt")) {
                RoyalRuns.copy(p, s);
                n.m_128473_("CastAt");
                n.m_128356_("NextCopy", now + 160L - (long)(n.m_128451_("BossPhase") * 20));
            }
            if (n.m_128454_("FieldUntil") > now && p.f_19797_ % 10 == 0) {
                Vec3 at = new Vec3(n.m_128459_("X"), n.m_128459_("Y"), n.m_128459_("Z"));
                for (LivingEntity t : RoyalRuns.targets(p, s, at, 3.0)) {
                    if (n.m_128461_("Field").equals("WELL")) {
                        t.m_147240_(0.3, t.m_20185_() - at.f_82479_, t.m_20189_() - at.f_82481_);
                        continue;
                    }
                    t.m_6469_(p.m_269291_().m_269104_((Entity)RoyalRuns.boss(p, s), (Entity)RoyalRuns.boss(p, s)), 1.0f);
                }
                EclipsePowers.particles(p, at, ParticleTypes.f_123745_, 8);
            }
        }
        AdventureService.dirty(p);
    }

    private static Mob boss(ServerPlayer p, AdventureData.Session s) {
        Mob m;
        Entity entity;
        AdventureData.Npc n = s.npcs.get(s.royal.m_128342_("Boss"));
        return n != null && n.active != null && (entity = p.m_284548_().m_8791_(n.active)) instanceof Mob ? (m = (Mob)entity) : null;
    }

    private static List<LivingEntity> targets(ServerPlayer p, AdventureData.Session s, Vec3 at, double radius) {
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        if (p.m_6084_() && p.m_20238_(at) <= radius * radius) {
            list.add((LivingEntity)p);
        }
        for (UUID id : s.participants) {
            ShadowRecord r = EclipseService.record(p, id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m == null || !m.m_6084_() || m.m_9236_() != p.m_9236_() || !(m.m_20238_(at) <= radius * radius)) continue;
            list.add((LivingEntity)m);
        }
        return list;
    }

    private static void copy(ServerPlayer p, AdventureData.Session s) {
        Mob b = RoyalRuns.boss(p, s);
        if (b == null) {
            return;
        }
        CompoundTag n = s.royal;
        long now = RoyalService.now(p);
        Vec3 at = new Vec3(n.m_128459_("X"), n.m_128459_("Y"), n.m_128459_("Z"));
        switch (n.m_128461_("Copy")) {
            case "DASH": {
                EntityTeleportEvent.EnderEntity event;
                Vec3 direction = at.m_82546_(b.m_20182_());
                Vec3 next = ShadowService.safePosition(b, b.m_20182_().m_82549_(direction.m_82541_().m_82490_(Math.min(4.0, direction.m_82553_()))), p.m_284548_());
                if (next == null || (event = ForgeEventFactory.onEnderTeleport((LivingEntity)b, (double)next.f_82479_, (double)next.f_82480_, (double)next.f_82481_)).isCanceled()) break;
                Vec3 vec3 = new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                if (!(vec3.m_82557_(next) < 0.01)) break;
                b.m_6021_(next.f_82479_, next.f_82480_, next.f_82481_);
                break;
            }
            case "PARRY": {
                n.m_128356_("WardUntil", now + 40L);
                n.m_128473_("WardHit");
                break;
            }
            case "WELL": 
            case "STORM": {
                n.m_128359_("Field", n.m_128461_("Copy"));
                n.m_128356_("FieldUntil", now + (long)(n.m_128461_("Copy").equals("WELL") ? 40 : 60));
                break;
            }
            default: {
                Vec3 start = b.m_146892_();
                Vec3 line = at.m_82520_(0.0, 1.0, 0.0).m_82546_(start);
                BlockHitResult ray = p.m_9236_().m_45547_(new ClipContext(start, start.m_82549_(line.m_82541_().m_82490_(Math.min(24.0, line.m_82553_()))), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)b));
                Vec3 end = ray.m_82450_();
                for (LivingEntity target : RoyalRuns.targets(p, s, start, 28.0)) {
                    if (!target.m_20191_().m_82400_(0.3).m_82371_(start, end).isPresent() || !b.m_142582_((Entity)target)) continue;
                    target.m_6469_(p.m_269291_().m_269104_((Entity)b, (Entity)b), 4.0f);
                }
                for (int i = 0; i < 16; ++i) {
                    EclipsePowers.particles(p, start.m_165921_(end, (double)i / 16.0), ParticleTypes.f_123745_, 1);
                }
            }
        }
    }

    private static void cleanArrows(ServerPlayer p, AdventureData.Session s, boolean all) {
        ListTag arrows = s.royal.m_128437_("Arrows", 10);
        for (int i = arrows.size() - 1; i >= 0; --i) {
            Entity entity;
            CompoundTag n = arrows.m_128728_(i);
            if (!all && RoyalService.now(p) - n.m_128454_("Born") < 100L) continue;
            if (n.m_128403_("Id") && (entity = p.m_284548_().m_8791_(n.m_128342_("Id"))) instanceof Arrow) {
                Arrow a = (Arrow)entity;
                a.m_146870_();
            }
            arrows.remove(i);
        }
        s.royal.m_128365_("Arrows", (Tag)arrows);
    }

    public static void win(ServerPlayer p, AdventureData.Session s) {
        if (s.phase != AdventureData.Phase.ACTIVE || s.royal.m_128471_("Awarded")) {
            return;
        }
        GuideService.clear(p, s);
        RoyalState e = RoyalService.state(p);
        s.royal.m_128379_("Awarded", true);
        if (s.kind == AdventureData.Kind.MIMIC) {
            EclipseState base = EclipseService.state(p);
            base.wins = Math.min(1000000, base.wins + 1);
            base.marks = Math.min(1000000, base.marks + 3 + (s.royal.m_128471_("Hard") ? 1 : 0));
            base.palettes |= 1 << Math.min(5, base.wins);
            e.emblems |= 0x100;
        } else if (s.kind == AdventureData.Kind.TRAINING) {
            e.emblems |= 0x800;
        } else if (s.kind == AdventureData.Kind.SEAL_PUZZLE) {
            e.emblems |= 0x400;
            e.puzzleBest = Math.min(e.puzzleBest, s.royal.m_128451_("Moves"));
        } else {
            e.emblems |= 0x200;
            long elapsed = RoyalService.now(p) - s.royal.m_128454_("Started");
            if (s.royal.m_128471_("Hard")) {
                e.courseHardBest = e.courseHardBest < 0L ? elapsed : Math.min(e.courseHardBest, elapsed);
            } else {
                e.courseBest = e.courseBest < 0L ? elapsed : Math.min(e.courseBest, elapsed);
            }
        }
        RoyalRuns.cleanArrows(p, s, true);
        s.phase = AdventureData.Phase.READY;
        AdventureWorld.clearEntities(p.f_8924_, s);
        EclipseService.endReport(p, "victory");
        RoyalService.dirty(p);
        AdventureService.dirty(p);
        RoyalText.tell(p, "activity_won", new Object[0]);
    }

    public static boolean finish(ServerPlayer p, boolean claim) {
        AdventureData.Session s = RoyalRuns.session(p);
        if (s == null || claim && s.phase != AdventureData.Phase.READY) {
            return false;
        }
        RoyalRuns.cleanArrows(p, s, true);
        AdventureWorld.clearEntities(p.f_8924_, s);
        EclipseService.endReport(p, claim || s.phase == AdventureData.Phase.READY ? "victory" : "abandoned");
        AdventureService.journal((ServerPlayer)p).session = null;
        AdventureService.dirty(p);
        return true;
    }

    public static void fail(ServerPlayer p, String why) {
        RoyalRuns.finish(p, false);
        RoyalText.tell(p, why, new Object[0]);
    }

    public static void paused(ServerPlayer p, AdventureData.Session s) {
        RoyalRuns.cleanArrows(p, s, true);
        s.royal.m_128356_("PausedAt", RoyalService.now(p));
        RoyalService.state((ServerPlayer)p).breach.clear();
    }

    public static boolean resume(ServerPlayer p) {
        AdventureData.Session s = RoyalRuns.session(p);
        if (s == null || s.phase != AdventureData.Phase.PAUSED || !RoyalRuns.valid(s) || !RoyalRuns.near(p, s) || !p.m_6084_() || p.m_5833_() || s.kind != AdventureData.Kind.SEAL_PUZZLE && p.m_9236_().m_46791_() == Difficulty.PEACEFUL || s.kind == AdventureData.Kind.SKILL_COURSE && p.m_150110_().f_35934_) {
            return false;
        }
        if (s.kind == AdventureData.Kind.MIMIC && s.participants.stream().anyMatch(id -> {
            ShadowRecord r = EclipseService.record(p, id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            return m == null || !m.m_6084_() || m.m_9236_() != p.m_9236_() || m.m_20280_((Entity)p) > 1024.0;
        })) {
            return false;
        }
        long delta = Math.max(0L, RoyalService.now(p) - s.royal.m_128454_("PausedAt"));
        for (String key : List.of("Deadline", "Started", "NextCopy", "CastAt", "NextShot", "FieldUntil", "WardUntil", "GraceUntil")) {
            if (!s.royal.m_128441_(key)) continue;
            s.royal.m_128356_(key, s.royal.m_128454_(key) + delta);
        }
        ++s.epoch;
        s.phase = AdventureData.Phase.ACTIVE;
        s.npcs.values().forEach(n -> {
            n.active = null;
        });
        AdventureService.dirty(p);
        return true;
    }
}

