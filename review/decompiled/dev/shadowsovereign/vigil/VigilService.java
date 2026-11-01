/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.Tags$EntityTypes
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.vigil;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.network.VigilNetwork;
import dev.shadowsovereign.sentinel.SentinelService;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilJobs;
import dev.shadowsovereign.vigil.VigilPosts;
import dev.shadowsovereign.vigil.VigilRuns;
import dev.shadowsovereign.vigil.VigilState;
import dev.shadowsovereign.vigil.VigilText;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public final class VigilService {
    public static boolean internalOrder;
    public static boolean internalRecall;

    public static VigilState state(ServerPlayer p) {
        return ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).vigil;
    }

    public static long now(ServerPlayer p) {
        return p.f_8924_.m_129783_().m_46467_();
    }

    public static void dirty(ServerPlayer p) {
        ArmyData.get(p.f_8924_).m_77762_();
    }

    public static ShadowRecord record(ServerPlayer p, UUID id) {
        return id == null ? null : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
    }

    public static String name(ServerPlayer p, ShadowRecord r) {
        return LegacyService.name(p, r);
    }

    public static boolean ready(ServerPlayer p, String key) {
        return EclipseService.usable(p) && VigilService.state((ServerPlayer)p).ready.getOrDefault(key, 0L) <= VigilService.now(p);
    }

    public static boolean combatOrder(ServerPlayer p, String key, int energy) {
        return VigilService.ready(p, key) && !VigilCrown.active(p) && !VigilRuns.abstractKind(AdventureService.journal((ServerPlayer)p).session == null ? null : AdventureService.journal((ServerPlayer)p).session.kind) && EclipseService.state((ServerPlayer)p).energy >= (float)energy;
    }

    public static void spend(ServerPlayer p, String key, int energy, int ticks) {
        EclipseService.state((ServerPlayer)p).energy -= (float)energy;
        VigilService.state((ServerPlayer)p).ready.put(key, VigilService.now(p) + (long)ticks);
        VigilService.dirty(p);
    }

    public static Mob eligible(ServerPlayer p, UUID id) {
        ShadowRecord r = VigilService.record(p, id);
        Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
        return m != null && m.m_6084_() && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 1024.0 && !AdventureService.busy(p.f_8924_, p.m_20148_(), id) ? m : null;
    }

    public static LivingEntity aimMob(ServerPlayer p, int range) {
        Vec3 from = p.m_146892_();
        Vec3 end = from.m_82549_(p.m_20154_().m_82490_((double)range));
        BlockHitResult wall = p.m_9236_().m_45547_(new ClipContext(from, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)p));
        Vec3 dest = end = wall.m_82450_();
        return p.m_9236_().m_6443_(LivingEntity.class, p.m_20191_().m_82369_(p.m_20154_().m_82490_((double)range)).m_82400_(1.0), e -> e != p && e.m_6084_() && e.m_20191_().m_82400_(0.3).m_82371_(from, dest).isPresent()).stream().min(Comparator.comparingDouble(arg_0 -> ((ServerPlayer)p).m_20280_(arg_0))).orElse(null);
    }

    public static boolean melee(Mob m, LivingEntity enemy) {
        return m.m_21051_(Attributes.f_22281_) != null ? m.m_7327_((Entity)enemy) : enemy.m_6469_(m.m_269291_().m_269333_((LivingEntity)m), CompanionService.fallbackDamage(m, enemy));
    }

    public static boolean bandage(ServerPlayer p) {
        if (!VigilService.ready(p, "bandage") || p.m_21223_() >= p.m_21233_()) {
            return false;
        }
        p.m_5634_(4.0f);
        VigilService.spend(p, "bandage", 0, 400);
        return true;
    }

    public static boolean feast(ServerPlayer p) {
        if (!VigilService.ready(p, "feast") || VigilService.now(p) - VigilService.state((ServerPlayer)p).lastThreatAt < 200L) {
            return false;
        }
        int healed = 0;
        for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            Mob m = VigilService.eligible(p, r.id);
            if (m == null || !(m.m_20280_((Entity)p) <= 64.0) || !(m.m_21223_() < m.m_21233_())) continue;
            m.m_5634_(4.0f);
            if (++healed < 8) continue;
            break;
        }
        if (healed == 0 && p.m_36324_().m_38702_() >= 20) {
            return false;
        }
        p.m_36324_().m_38707_(6, 0.6f);
        VigilService.spend(p, "feast", 0, 600);
        VigilText.tell(p, "feast_used", healed);
        return true;
    }

    public static boolean horn(ServerPlayer p) {
        if (!VigilService.ready(p, "horn") || VigilCrown.active(p)) {
            return false;
        }
        int count = 0;
        for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            if (VigilService.eligible(p, r.id) == null || r.vigil.post) continue;
            ShadowService.setOrder(p, r, Order.FOLLOW);
            ++count;
        }
        if (count == 0) {
            return false;
        }
        VigilService.spend(p, "horn", 0, 100);
        p.m_284548_().m_5594_(null, p.m_20183_(), (SoundEvent)((Holder.Reference)SoundEvents.f_215702_.get(0)).get(), SoundSource.PLAYERS, 0.8f, 1.0f);
        VigilText.tell(p, "horn_used", count);
        return true;
    }

    public static boolean inspect(ServerPlayer p) {
        Mob mob;
        LivingEntity target = VigilService.aimMob(p, 12);
        if (!(target instanceof Mob) || ShadowService.isShadow((Entity)(mob = (Mob)target))) {
            return false;
        }
        CompoundTag n = new CompoundTag();
        n.m_128359_("Name", target.m_7755_().getString());
        n.m_128359_("Type", String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey((Object)target.m_6095_())));
        n.m_128350_("HP", target.m_21223_());
        n.m_128350_("Max", target.m_21233_());
        n.m_128347_("Attack", target.m_21051_(Attributes.f_22281_) == null ? 0.0 : target.m_21133_(Attributes.f_22281_));
        n.m_128405_("Armor", target.m_21230_());
        n.m_128379_("Allowed", !((List)ShadowConfig.BLOCKLIST.get()).contains(n.m_128461_("Type")) && ((Boolean)ShadowConfig.RECRUIT_BOSSES.get() != false || !target.m_6095_().m_204039_(Tags.EntityTypes.BOSSES)));
        VigilService.state((ServerPlayer)p).lens = n;
        VigilNetwork.send(p, true, 5);
        return true;
    }

    public static boolean snare(ServerPlayer p) {
        Vec3 at = EclipsePowers.aim(p, 12);
        if (at == null || !VigilService.state((ServerPlayer)p).snare.m_128456_()) {
            return false;
        }
        CompoundTag n = VigilService.state((ServerPlayer)p).snare;
        n.m_128356_("Pos", BlockPos.m_274446_((Position)at).m_121878_());
        n.m_128359_("Dimension", p.m_9236_().m_46472_().m_135782_().toString());
        n.m_128356_("Until", VigilService.now(p) + 600L);
        return true;
    }

    public static boolean post(ServerPlayer p, UUID id) {
        ShadowRecord r = VigilService.record(p, id);
        Mob m = VigilService.eligible(p, id);
        if (r == null || m == null || VigilCrown.active(p) || AdventureService.journal((ServerPlayer)p).session != null) {
            return false;
        }
        ShadowService.setOrder(p, r, Order.GUARD);
        r.vigil.post = true;
        r.vigil.suspended = false;
        r.vigil.postPos = m.m_20183_().m_121878_();
        r.vigil.postDimension = p.m_9236_().m_46472_().m_135782_().toString();
        VigilService.dirty(p);
        VigilText.tell(p, "post_set", VigilService.name(p, r));
        return true;
    }

    public static void ordered(ServerPlayer p, ShadowRecord r, Order order) {
        if (internalOrder) {
            return;
        }
        if (order == Order.GUARD) {
            r.vigil.post = true;
            r.vigil.suspended = false;
            Mob m = ShadowService.active(p.f_8924_, r);
            r.vigil.postPos = (m == null ? r.anchor : m.m_20183_()).m_121878_();
            r.vigil.postDimension = m == null ? r.dimension : m.m_9236_().m_46472_().m_135782_().toString();
        } else if (order == Order.FOLLOW || order == Order.STAY || order == Order.PATROL) {
            r.vigil.post = false;
            r.vigil.suspended = false;
        }
    }

    public static void recalling(ServerPlayer p, ShadowRecord r) {
        if (!internalRecall && !SentinelService.internalRecall) {
            r.vigil.suspended = true;
            if (VigilCrown.member(p, r)) {
                VigilText.tell(p, "crown_locked", new Object[0]);
            }
        }
    }

    public static void returned(ServerPlayer p, ShadowRecord r) {
        VigilState.Companion c = r.vigil;
        if (VigilService.state((ServerPlayer)p).chatter && VigilService.now(p) >= VigilService.state((ServerPlayer)p).nextVoice) {
            VigilService.state((ServerPlayer)p).nextVoice = VigilService.now(p) + 200L;
            VigilText.tell(p, (String)(c.lastSeen < 0L ? "greeting_first" : "greeting." + Math.floorMod(r.id.hashCode(), 4)), VigilService.name(p, r));
        }
        c.lastSeen = VigilService.now(p);
        c.separatedAt = -1L;
    }

    public static void recallTraveling(ServerPlayer p) {
        for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            if (r.vigil.post && !r.vigil.suspended && !VigilCrown.member(p, r)) continue;
            internalRecall = true;
            try {
                ShadowService.recall(p, r);
            }
            finally {
                internalRecall = false;
            }
        }
    }

    public static void cancelJobs(ServerPlayer p) {
        VigilState v = VigilService.state(p);
        v.reply = null;
        if (v.job.m_128403_("A") && v.job.m_128403_("B")) {
            Mob b;
            ShadowRecord ar = VigilService.record(p, v.job.m_128342_("A"));
            ShadowRecord br = VigilService.record(p, v.job.m_128342_("B"));
            Mob a = ar == null ? null : ShadowService.active(p.f_8924_, ar);
            Mob mob = b = br == null ? null : ShadowService.active(p.f_8924_, br);
            if (a != null && a.m_20202_() == b) {
                a.m_8127_();
            }
        }
        v.job = new CompoundTag();
        v.meteor = new CompoundTag();
    }

    public static boolean compass(ServerPlayer p, ItemStack stack) {
        List<ShadowRecord> list = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().filter(r -> r.vigil.post && !r.vigil.suspended).toList();
        if (list.isEmpty()) {
            return false;
        }
        int at = 0;
        for (int i = 0; i < list.size(); ++i) {
            if (!list.get((int)i).id.equals(VigilService.state((ServerPlayer)p).ping)) continue;
            at = i + 1;
        }
        ShadowRecord r2 = list.get(at % list.size());
        VigilService.state((ServerPlayer)p).ping = r2.id;
        CompoundTag tag = stack.m_41784_();
        tag.m_128365_("LodestonePos", (Tag)NbtUtils.m_129224_((BlockPos)BlockPos.m_122022_((long)r2.vigil.postPos)));
        ResourceLocation.m_135820_((String)r2.vigil.postDimension);
        tag.m_128359_("LodestoneDimension", r2.vigil.postDimension);
        tag.m_128379_("LodestoneTracked", false);
        VigilService.dirty(p);
        VigilText.tell(p, "compass_bound", VigilService.name(p, r2));
        return true;
    }

    public static void tick(ServerPlayer p) {
        VigilCrown.tick(p);
        long now = VigilService.now(p);
        VigilState v = VigilService.state(p);
        if (now % 5L == 0L) {
            VigilJobs.tick(p);
        }
        if (now % 20L != 0L) {
            return;
        }
        int alarms = 0;
        if (v.reply != null && now >= v.replyAt) {
            ShadowRecord replying = VigilService.record(p, v.reply);
            if (v.chatter && replying != null && VigilService.eligible(p, v.reply) != null) {
                VigilText.tell(p, "battle_reply." + Math.floorMod(replying.id.hashCode(), 3), VigilService.name(p, replying));
            }
            v.reply = null;
        }
        for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            Vec3 safe;
            ShadowRecord buddy;
            LivingEntity target;
            Mob m = ShadowService.active(p.f_8924_, r);
            if (m == null || m.m_9236_() != p.m_9236_()) {
                if (m == null) {
                    VigilPosts.restore(p, r);
                }
                if (r.vigil.lastSeen < 0L || r.vigil.separatedAt >= 0L) continue;
                r.vigil.separatedAt = now;
                continue;
            }
            if (m.m_20280_((Entity)p) <= 256.0) {
                boolean reunion;
                boolean bl = reunion = r.vigil.separatedAt >= 0L && (now - r.vigil.separatedAt >= 2400L || r.vigil.separatedWall > 0L && System.currentTimeMillis() - r.vigil.separatedWall >= 120000L);
                if (reunion && v.chatter && now >= v.nextVoice) {
                    VigilText.tell(p, "greeting." + Math.floorMod(r.id.hashCode(), 4), VigilService.name(p, r));
                    v.nextVoice = now + 400L;
                    r.vigil.separatedAt = -1L;
                    r.vigil.separatedWall = 0L;
                } else if (!reunion || !v.chatter) {
                    r.vigil.separatedAt = -1L;
                    r.vigil.separatedWall = 0L;
                }
                r.vigil.lastSeen = now;
            } else if (r.vigil.separatedAt < 0L) {
                r.vigil.separatedAt = now;
            }
            if (r.vigil.post && !r.vigil.suspended && now >= r.vigil.nextAlarm && alarms < 1 && (target = m.m_5448_()) != null && ShadowService.canAttack(p, target) && m.m_142582_((Entity)target)) {
                r.vigil.nextAlarm = now + 600L;
                ++alarms;
                VigilText.tell(p, "alarm", VigilService.name(p, r), target.m_7755_().getString());
            }
            if (v.chatter && now >= v.nextVoice && m.m_5448_() != null && m.m_142582_((Entity)m.m_5448_()) && (buddy = (ShadowRecord)ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().filter(b -> !b.id.equals(r.id)).filter(b -> {
                Mob other = VigilService.eligible(p, b.id);
                return other != null && other.m_20280_((Entity)m) <= 144.0;
            }).findFirst().orElse(null)) != null) {
                VigilText.tell(p, "battle_call." + Math.floorMod(r.id.hashCode(), 3), VigilService.name(p, r), VigilService.name(p, buddy));
                v.nextVoice = now + 400L;
                v.reply = buddy.id;
                v.replyAt = now + 20L;
            }
            if (r.vigil.post && !VigilCrown.active(p) && r.order == Order.GUARD) {
                r.vigil.postPos = r.anchor.m_121878_();
            }
            if (r.order == Order.FOLLOW && m.m_20280_((Entity)p) > 25.0 && !VigilCrown.active(p) && !r.id.equals(VigilState.id(v.meteor, "Id"))) {
                if (r.battle.lastProgress == null || r.battle.lastProgress.m_82557_(m.m_20182_()) > 0.1) {
                    r.battle.lastProgress = m.m_20182_();
                    r.battle.stuckSince = now;
                } else if (now - r.battle.stuckSince >= 100L) {
                    Vec3 safe2 = ShadowService.safePosition(m, p.m_20182_(), p.m_284548_());
                    if (safe2 != null) {
                        m.m_6021_(safe2.f_82479_, safe2.f_82480_, safe2.f_82481_);
                        m.m_6710_(null);
                        m.m_21573_().m_26573_();
                    }
                    r.battle.stuckSince = now;
                }
            }
            if (r.order != Order.FOLLOW || r.id.equals(VigilState.id(v.meteor, "Id")) || VigilCrown.active(p) || !(m.m_20280_((Entity)p) > 196.0)) continue;
            m.m_6710_(null);
            m.m_6274_().m_21936_(MemoryModuleType.f_26372_);
            if (!(m.m_20280_((Entity)p) > 1024.0) || (safe = ShadowService.safePosition(m, p.m_20182_(), p.m_284548_())) == null) continue;
            m.m_6021_(safe.f_82479_, safe.f_82480_, safe.f_82481_);
        }
        if (!v.snare.m_128456_()) {
            if (v.snare.m_128454_("Until") <= now || !v.snare.m_128461_("Dimension").equals(p.m_9236_().m_46472_().m_135782_().toString())) {
                v.snare = new CompoundTag();
            } else {
                Vec3 at = Vec3.m_82512_((Vec3i)BlockPos.m_122022_((long)v.snare.m_128454_("Pos")));
                int hit = 0;
                for (LivingEntity enemy : p.m_9236_().m_6443_(LivingEntity.class, new AABB(at, at).m_82400_(2.0), e -> e instanceof Enemy && ShadowService.canAttack(p, e))) {
                    enemy.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 60, 1));
                    if (++hit < 6) continue;
                    break;
                }
                if (hit > 0) {
                    v.snare = new CompoundTag();
                }
            }
        }
        VigilService.dirty(p);
    }
}

