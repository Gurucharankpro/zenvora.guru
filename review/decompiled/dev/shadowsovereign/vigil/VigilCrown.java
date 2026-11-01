/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.memory.MemoryModuleType
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.monster.RangedAttackMob
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.EntityTeleportEvent$EnderEntity
 */
package dev.shadowsovereign.vigil;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.vigil.VigilRuns;
import dev.shadowsovereign.vigil.VigilService;
import dev.shadowsovereign.vigil.VigilState;
import dev.shadowsovereign.vigil.VigilText;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public final class VigilCrown {
    public static boolean active(ServerPlayer p) {
        return VigilService.state((ServerPlayer)p).crown.m_128471_("Active");
    }

    public static boolean member(ServerPlayer p, ShadowRecord r) {
        return VigilCrown.active(p) && VigilService.state((ServerPlayer)p).crown.m_128437_("Members", 10).stream().anyMatch(t -> r.id.equals(VigilState.id((CompoundTag)t, "Id")));
    }

    public static boolean start(ServerPlayer p) {
        if (!p.m_6084_() || p.m_5833_() || VigilCrown.active(p)) {
            return false;
        }
        SoulPlay.cancel(p, false);
        OdysseyRuns.pause(p);
        MythicService.pause(p.f_8924_, p.m_20148_());
        if (LivingRealm.session(p) != null) {
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
        }
        VigilState v = VigilService.state(p);
        ListTag members = new ListTag();
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        AdventureData.Session session = AdventureService.journal((ServerPlayer)p).session;
        Set<UUID> allowed = session != null && !VigilRuns.abstractKind(session.kind) ? session.participants : null;
        ArrayList<ShadowRecord> ordered = new ArrayList<ShadowRecord>(army.shadows.values());
        ordered.sort(Comparator.comparing(r -> r.activeEntity == null));
        for (ShadowRecord r2 : ordered) {
            if (members.size() >= 24 || allowed != null && !allowed.contains(r2.id)) continue;
            Mob m = ShadowService.active(p.f_8924_, r2);
            if (m == null && r2.activeEntity == null && !r2.pendingRespawn() && !AdventureService.busy(p.f_8924_, p.m_20148_(), r2.id) && ShadowService.summon(p, r2, false)) {
                m = ShadowService.active(p.f_8924_, r2);
            }
            if (m == null || !m.m_6084_() || m.m_9236_() != p.m_9236_()) continue;
            CompoundTag n = new CompoundTag();
            n.m_128362_("Id", r2.id);
            n.m_128359_("Order", r2.order.name());
            n.m_128356_("Anchor", r2.anchor.m_121878_());
            n.m_128379_("Aggressive", r2.aggressive);
            members.add((Object)n);
        }
        v.crown = new CompoundTag();
        v.crown.m_128379_("Active", true);
        v.crown.m_128365_("Members", (Tag)members);
        v.crown.m_128359_("Dimension", p.m_9236_().m_46472_().m_135782_().toString());
        VigilService.cancelJobs(p);
        VigilService.dirty(p);
        VigilText.tell(p, "crown_start", members.size());
        return true;
    }

    public static void stop(ServerPlayer p) {
        VigilState v = VigilService.state(p);
        ListTag remembered = v.crown.m_128437_("Members", 10).m_6426_();
        v.crown = new CompoundTag();
        for (Tag tag : remembered) {
            EntityTeleportEvent.EnderEntity event;
            Vec3 back;
            ServerLevel world;
            Level level;
            CompoundTag n = (CompoundTag)tag;
            ShadowRecord r = VigilService.record(p, VigilState.id(n, "Id"));
            if (r == null) continue;
            r.order = Order.parse(n.m_128461_("Order"));
            r.anchor = BlockPos.m_122022_((long)n.m_128454_("Anchor"));
            r.aggressive = n.m_128471_("Aggressive");
            Mob m = ShadowService.active(p.f_8924_, r);
            if (m == null) continue;
            m.m_6710_(null);
            m.m_21573_().m_26573_();
            BlockPos post = BlockPos.m_122022_((long)r.vigil.postPos);
            if (!r.vigil.post || r.vigil.suspended || !m.m_9236_().m_46472_().m_135782_().toString().equals(r.vigil.postDimension) || !((level = m.m_9236_()) instanceof ServerLevel) || !(world = (ServerLevel)level).m_46805_(post) || !(m.m_20238_(Vec3.m_82539_((Vec3i)post)) > 4.0) || (back = ShadowService.safePosition(m, Vec3.m_82539_((Vec3i)post), world)) == null || (event = ForgeEventFactory.onEnderTeleport((LivingEntity)m, (double)back.f_82479_, (double)back.f_82480_, (double)back.f_82481_)).isCanceled()) continue;
            m.m_6021_(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        }
        v.crown = new CompoundTag();
        VigilService.dirty(p);
        VigilText.tell(p, "crown_end", new Object[0]);
    }

    public static Vec3 slot(ServerPlayer p, int slot) {
        int[][] outer = new int[][]{{-2, -2}, {-1, -2}, {0, -2}, {1, -2}, {2, -2}, {2, -1}, {2, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2}, {-1, 2}, {-2, 2}, {-2, 1}, {-2, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}};
        int[] at = outer[Math.floorMod(slot, 24)];
        return p.m_20182_().m_82520_((double)at[0], 0.0, (double)at[1]);
    }

    public static boolean inside(ServerPlayer p, Mob m) {
        double margin = Math.max(0.0, 2.5 - (double)m.m_20205_() / 2.0);
        return Math.abs(m.m_20185_() - p.m_20185_()) <= margin + 0.02 && Math.abs(m.m_20189_() - p.m_20189_()) <= margin + 0.02;
    }

    public static boolean hold(Mob m, ShadowRecord r, ServerPlayer p) {
        LivingEntity target;
        EntityTeleportEvent.EnderEntity event;
        Vec3 safe;
        Object before;
        if (!VigilCrown.member(p, r)) {
            return false;
        }
        ListTag list = VigilService.state((ServerPlayer)p).crown.m_128437_("Members", 10);
        int index = 0;
        for (int i = 0; !(i >= list.size() || (before = VigilService.record(p, VigilState.id(list.m_128728_(i), "Id"))) != null && ((ShadowRecord)before).id.equals(r.id)); ++i) {
            Mob living;
            if (before == null || (living = ShadowService.active(p.f_8924_, (ShadowRecord)before)) == null || !living.m_6084_() || living.m_9236_() != p.m_9236_()) continue;
            ++index;
        }
        int total = 0;
        for (Tag tag : list) {
            ShadowRecord soul = VigilService.record(p, VigilState.id((CompoundTag)tag, "Id"));
            Mob living = soul == null ? null : ShadowService.active(p.f_8924_, soul);
            if (living == null || !living.m_6084_() || living.m_9236_() != p.m_9236_()) continue;
            ++total;
        }
        int placement = total <= 16 ? index * 16 / Math.max(1, total) : (index < 16 ? index : 16 + (index - 16) * 8 / Math.max(1, total - 16));
        Vec3 at = VigilCrown.slot(p, placement);
        double margin = Math.max(0.0, 2.5 - (double)m.m_20205_() / 2.0);
        at = new Vec3(Mth.m_14008_((double)at.f_82479_, (double)(p.m_20185_() - margin), (double)(p.m_20185_() + margin)), p.m_20186_(), Mth.m_14008_((double)at.f_82481_, (double)(p.m_20189_() - margin), (double)(p.m_20189_() + margin)));
        if (!(VigilCrown.inside(p, m) && !(m.m_20238_(at) > 2.25) || (safe = VigilCrown.safe(m, p, at)) == null || (event = ForgeEventFactory.onEnderTeleport((LivingEntity)m, (double)safe.f_82479_, (double)safe.f_82480_, (double)safe.f_82481_)).isCanceled())) {
            Vec3 vec3 = new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            if (vec3.m_82557_(safe) < 0.01) {
                m.m_6021_(safe.f_82479_, safe.f_82480_, safe.f_82481_);
            }
        }
        if (m.f_19797_ % 20 == 0) {
            m.m_7292_(new MobEffectInstance(MobEffects.f_19596_, 40, 1, false, false, true));
        }
        if ((target = m.m_5448_()) != null && (!ShadowService.canAttack(p, target) || target.m_20280_((Entity)p) > 25.0)) {
            target = null;
        }
        if (target == null && m.f_19797_ % 5 == 0) {
            target = p.m_9236_().m_6443_(LivingEntity.class, p.m_20191_().m_82400_(5.0), e -> ShadowService.canAttack(p, e) && e.m_20280_((Entity)p) <= 25.0 && m.m_142582_((Entity)e) && (e instanceof Enemy || e == p.m_21188_())).stream().min(Comparator.comparingDouble(arg_0 -> ((Mob)m).m_20280_(arg_0))).orElse(null);
        }
        m.m_6710_(target);
        if (m.m_6274_().m_147339_().containsKey(MemoryModuleType.f_26372_)) {
            if (target == null) {
                m.m_6274_().m_21936_(MemoryModuleType.f_26372_);
            } else {
                m.m_6274_().m_21879_(MemoryModuleType.f_26372_, (Object)target);
            }
        }
        if (m.m_20238_(at) > 1.0 && m.f_19797_ % 5 == 0) {
            m.m_21573_().m_26519_(at.f_82479_, at.f_82480_, at.f_82481_, 1.4);
        } else if (m.m_20238_(at) <= 1.0) {
            m.m_21573_().m_26573_();
        }
        if (target != null) {
            m.m_21563_().m_24960_((Entity)target, 30.0f, 30.0f);
            if (m.f_19797_ % 20 == 0 && m.m_142582_((Entity)target)) {
                double reach = Math.max(2.4, (double)(m.m_20205_() + target.m_20205_() / 2.0f));
                if (m.m_20280_((Entity)target) <= reach * reach) {
                    m.m_6674_(InteractionHand.MAIN_HAND);
                    VigilService.melee(m, target);
                } else if (m instanceof RangedAttackMob) {
                    RangedAttackMob ranged = (RangedAttackMob)m;
                    ranged.m_6504_(target, 1.0f);
                }
            }
        }
        Vec3 velocity = m.m_20184_();
        if (!VigilCrown.inside(p, m) || Math.abs(m.m_20185_() + velocity.f_82479_ - p.m_20185_()) > margin || Math.abs(m.m_20189_() + velocity.f_82481_ - p.m_20189_()) > margin) {
            m.m_20334_(0.0, velocity.f_82480_, 0.0);
        }
        return true;
    }

    private static Vec3 safe(Mob m, ServerPlayer p, Vec3 at) {
        for (Vec3 offset : new Vec3[]{Vec3.f_82478_, new Vec3(0.35, 0.0, 0.0), new Vec3(-0.35, 0.0, 0.0), new Vec3(0.0, 0.0, 0.35), new Vec3(0.0, 0.0, -0.35)}) {
            for (int dy : new int[]{0, 1, -1}) {
                Vec3 pos = at.m_82549_(offset).m_82520_(0.0, (double)dy, 0.0);
                double margin = Math.max(0.0, 2.5 - (double)m.m_20205_() / 2.0);
                BlockPos block = BlockPos.m_274446_((Position)pos);
                if (Math.abs(pos.f_82479_ - p.m_20185_()) > margin || Math.abs(pos.f_82481_ - p.m_20189_()) > margin || !p.m_284548_().m_46805_(block) || !p.m_9236_().m_45756_((Entity)m, m.m_20191_().m_82383_(pos.m_82546_(m.m_20182_()))) || p.m_9236_().m_8055_(block.m_7495_()).m_60812_((BlockGetter)p.m_9236_(), block.m_7495_()).m_83281_() || !p.m_9236_().m_6425_(block).m_76178_()) continue;
                return pos;
            }
        }
        return null;
    }

    public static void tick(ServerPlayer p) {
        VigilState v = VigilService.state(p);
        if (v.crownRequest > 0L) {
            v.crownRequest = 0L;
            if (p.m_6084_() && p.m_21223_() < 6.0f) {
                VigilCrown.start(p);
            }
        }
        if (!VigilCrown.active(p)) {
            return;
        }
        if (!p.m_6084_() || p.m_21223_() >= p.m_21233_() || !p.m_9236_().m_46472_().m_135782_().toString().equals(v.crown.m_128461_("Dimension"))) {
            VigilCrown.stop(p);
            return;
        }
        if (VigilService.now(p) % 20L == 0L) {
            ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
            AdventureData.Session journal = AdventureService.journal((ServerPlayer)p).session;
            for (ShadowRecord extra : army.shadows.values()) {
                if (VigilCrown.member(p, extra) || extra.pendingRespawn() || extra.activeEntity != null || army.activeCount() >= (long)Math.min(24, (Integer)ShadowConfig.MAX_ACTIVE.get()) || journal != null && !VigilRuns.abstractKind(journal.kind) && !journal.participants.contains(extra.id) || !ShadowService.summon(p, extra, false)) continue;
                CompoundTag add = new CompoundTag();
                add.m_128362_("Id", extra.id);
                add.m_128359_("Order", extra.order.name());
                add.m_128356_("Anchor", extra.anchor.m_121878_());
                add.m_128379_("Aggressive", extra.aggressive);
                v.crown.m_128437_("Members", 10).add((Object)add);
                VigilService.dirty(p);
            }
        }
        for (Tag tag : v.crown.m_128437_("Members", 10)) {
            ShadowRecord r = VigilService.record(p, VigilState.id((CompoundTag)tag, "Id"));
            if (r == null) continue;
            Mob m = ShadowService.active(p.f_8924_, r);
            if (m == null && r.activeEntity == null && !r.pendingRespawn()) {
                ShadowService.summon(p, r, false);
            }
            if ((m = ShadowService.active(p.f_8924_, r)) == null || m.m_9236_() != p.m_9236_()) continue;
            VigilCrown.hold(m, r, p);
        }
    }
}

