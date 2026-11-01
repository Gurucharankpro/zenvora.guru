/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.TamableAnimal
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.animal.horse.AbstractHorse
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.monster.Slime
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.common.Tags$EntityTypes
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.realm;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mythic.MythicPower;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.realm.RealmState;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.soulforge.SoulTier;
import dev.shadowsovereign.soulforge.SoulTrait;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public final class WildBosses {
    public static final String MARK = "SoulboundWildBoss";
    public static final String ROLLED = "SoulboundWildRolled";
    public static final String CLAIMED = "SoulboundWildClaimed";
    public static final String BASE = "SoulboundBossAttack";
    private static final UUID HEALTH = UUID.fromString("ca116000-0000-4000-8000-000000000001");
    private static final UUID ATTACK = UUID.fromString("ca116000-0000-4000-8000-000000000002");

    public static boolean boss(Entity m) {
        return m.getPersistentData().m_128471_(MARK);
    }

    public static boolean rollSucceeds(int value) {
        return value == 0;
    }

    public static boolean eligible(Mob m) {
        AbstractHorse h;
        TamableAnimal t;
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)m.m_6095_());
        return !(id == null || !id.m_135827_().equals("minecraft") || m.m_6162_() || ShadowService.isShadow((Entity)m) || AdventureWorld.isNpc((Entity)m) || m instanceof TamableAnimal && (t = (TamableAnimal)m).m_21824_() || m instanceof AbstractHorse && (h = (AbstractHorse)m).m_30614_() || m.m_6095_().m_204039_(Tags.EntityTypes.BOSSES) || Set.of("warden", "elder_guardian", "ender_dragon", "wither", "villager", "wandering_trader", "iron_golem", "snow_golem").contains(id.m_135815_()) || m.m_8077_() || ((List)ShadowConfig.BLOCKLIST.get()).contains(id.toString()) || m.m_21051_(Attributes.f_22276_) == null);
    }

    public static boolean natural(Mob m, MobSpawnType reason, int roll) {
        if (m.getPersistentData().m_128471_(ROLLED) || reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) {
            return false;
        }
        m.getPersistentData().m_128379_(ROLLED, true);
        if (!WildBosses.eligible(m) || !WildBosses.rollSucceeds(roll)) {
            return false;
        }
        WildBosses.promote(m);
        return true;
    }

    public static void promote(Mob m) {
        CompoundTag data = m.getPersistentData();
        if (WildBosses.boss((Entity)m)) {
            return;
        }
        double hp = Math.max(100.0f, m.m_21233_() * 3.0f);
        double damage = Math.max(10.0, m.m_21051_(Attributes.f_22281_) == null ? (Double)ShadowConfig.FALLBACK_DAMAGE.get() * 2.0 : m.m_21133_(Attributes.f_22281_) * 2.0);
        if (m.m_6095_() == EntityType.f_20555_) {
            damage = 10.0;
        }
        data.m_128379_(MARK, true);
        data.m_128347_(BASE, damage);
        AttributeInstance health = m.m_21051_(Attributes.f_22276_);
        health.m_22120_(HEALTH);
        health.m_22125_(new AttributeModifier(HEALTH, "Wild boss vitality", hp - health.m_22135_(), AttributeModifier.Operation.ADDITION));
        AttributeInstance attack = m.m_21051_(Attributes.f_22281_);
        if (attack != null) {
            attack.m_22120_(ATTACK);
            attack.m_22125_(new AttributeModifier(ATTACK, "Wild boss attack", damage - attack.m_22135_(), AttributeModifier.Operation.ADDITION));
        }
        m.m_21153_(m.m_21233_());
        m.m_6593_((Component)Component.m_237115_((String)m.m_6095_().m_20675_()).m_130946_(" Boss"));
        m.m_20340_(true);
        m.m_21530_();
        WildBosses.install(m);
    }

    public static void install(Mob m) {
        if (!WildBosses.boss((Entity)m) || ShadowService.isShadow((Entity)m) || m.f_21345_.m_148105_().stream().anyMatch(g -> g.m_26015_() instanceof Retaliate)) {
            return;
        }
        if (!(m instanceof Enemy)) {
            m.f_21345_.m_25352_(-1, (Goal)new Retaliate(m));
        }
    }

    public static boolean capture(ServerPlayer p, Mob mob) {
        if (p.f_8924_.m_6846_().m_11259_(p.m_20148_()) != p) {
            return WildBosses.captureOffline(p.f_8924_, p.m_20148_(), mob);
        }
        if (!WildBosses.boss((Entity)mob) || ShadowService.isShadow((Entity)mob) || mob.getPersistentData().m_128471_(CLAIMED)) {
            return false;
        }
        mob.getPersistentData().m_128379_(CLAIMED, true);
        mob.getPersistentData().m_128379_("SoulboundExtractionOffered", true);
        if (GuideService.army((ServerPlayer)p).shadows.containsKey(mob.m_20148_()) || LivingRealm.state((ServerPlayer)p).pending.containsKey(mob.m_20148_())) {
            return true;
        }
        ShadowRecord r = new ShadowRecord(mob.m_20148_(), ForgeRegistries.ENTITY_TYPES.getKey((Object)mob.m_6095_()).toString(), mob.m_7755_().getString(), ShadowService.snapshot(mob, true), mob.m_21233_());
        r.tier = SoulTier.COMMON;
        r.trait = SoulTrait.NONE;
        r.soulAwakened = true;
        CompanionService.acquired(p, r);
        RealmState s = LivingRealm.state(p);
        s.pending.put(r.id, r);
        CompoundTag entry = s.bestiary.computeIfAbsent(r.type, k -> new CompoundTag());
        entry.m_128405_("Kills", entry.m_128451_("Kills") + 1);
        entry.m_128350_("Health", r.maxHealth);
        entry.m_128347_("Attack", mob.getPersistentData().m_128459_(BASE) * (MythicPower.mythical((Entity)mob) ? 1.25 : 1.0));
        entry.m_128379_("Seen", true);
        LivingRealm.dirty(p);
        WildBosses.claimPending(p);
        LivingRealm.tell(p, "boss_soul", mob.m_7755_().getString());
        if (mob instanceof Slime) {
            Slime slime = (Slime)mob;
            slime.m_7839_(1, false);
        }
        return true;
    }

    public static boolean captureOffline(MinecraftServer server, UUID owner, Mob mob) {
        if (!WildBosses.boss((Entity)mob) || ShadowService.isShadow((Entity)mob) || mob.getPersistentData().m_128471_(CLAIMED)) {
            return false;
        }
        mob.getPersistentData().m_128379_(CLAIMED, true);
        mob.getPersistentData().m_128379_("SoulboundExtractionOffered", true);
        AdventureData.Journal journal = AdventureData.get(server).journal(owner);
        if (ArmyData.get((MinecraftServer)server).army((UUID)owner).shadows.containsKey(mob.m_20148_()) || journal.livingRealm.pending.containsKey(mob.m_20148_())) {
            return true;
        }
        ShadowRecord r = new ShadowRecord(mob.m_20148_(), ForgeRegistries.ENTITY_TYPES.getKey((Object)mob.m_6095_()).toString(), mob.m_7755_().getString(), ShadowService.snapshot(mob, true), mob.m_21233_());
        r.soulAcquired = true;
        r.soulAwakened = true;
        r.inventory.initialize(r.snapshot);
        AdventureData data = AdventureData.get(server);
        RealmState state = data.journal((UUID)owner).livingRealm;
        state.pending.put(r.id, r);
        CompoundTag row = state.bestiary.computeIfAbsent(r.type, k -> new CompoundTag());
        row.m_128379_("Seen", true);
        row.m_128350_("Health", r.maxHealth);
        row.m_128347_("Attack", mob.getPersistentData().m_128459_(BASE));
        row.m_128405_("Kills", row.m_128451_("Kills") + 1);
        data.m_77762_();
        if (mob instanceof Slime) {
            Slime slime = (Slime)mob;
            slime.m_7839_(1, false);
        }
        return true;
    }

    public static int claimPending(ServerPlayer p) {
        ArmyData.Army a = GuideService.army(p);
        RealmState s = LivingRealm.state(p);
        int count = 0;
        Iterator<Map.Entry<UUID, ShadowRecord>> iterator = s.pending.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ShadowRecord> row = iterator.next();
            if (a.shadows.containsKey(row.getKey())) {
                iterator.remove();
                continue;
            }
            if (a.shadows.size() >= (Integer)ShadowConfig.MAX_ARMY.get()) break;
            ResourceLocation typeKey = ResourceLocation.m_135820_((String)row.getValue().type);
            if (typeKey == null || !ForgeRegistries.ENTITY_TYPES.containsKey(typeKey) || ((List)ShadowConfig.BLOCKLIST.get()).contains(row.getValue().type)) continue;
            a.shadows.put(row.getKey(), row.getValue());
            iterator.remove();
            if (a.autoSummon && a.activeCount() < (long)((Integer)ShadowConfig.MAX_ACTIVE.get()).intValue()) {
                ShadowService.summon(p, row.getValue(), false);
            }
            AdventureService.captured(p, row.getValue());
            ++count;
        }
        if (count > 0) {
            ArmyData.get(p.f_8924_).m_77762_();
            LivingRealm.dirty(p);
            ShadowNetwork.sendArmy(p, false);
        }
        return count;
    }

    public static double fallback(Mob mob, double normal) {
        return WildBosses.boss((Entity)mob) ? Math.max(10.0, mob.getPersistentData().m_128459_(BASE)) : normal;
    }

    public static void observe(ServerPlayer p) {
        for (Mob m2 : p.m_284548_().m_6443_(Mob.class, p.m_20191_().m_82400_(32.0), m -> WildBosses.boss((Entity)m) && !ShadowService.isShadow((Entity)m))) {
            String id;
            CompoundTag e;
            if (!p.m_142582_((Entity)m2) || (e = LivingRealm.state((ServerPlayer)p).bestiary.computeIfAbsent(id = ForgeRegistries.ENTITY_TYPES.getKey((Object)m2.m_6095_()).toString(), k -> new CompoundTag())).m_128471_("Seen")) continue;
            e.m_128379_("Seen", true);
            e.m_128350_("Health", m2.m_21233_());
            e.m_128347_("Attack", m2.getPersistentData().m_128459_(BASE));
            LivingRealm.tell(p, "boss_seen", m2.m_7755_().getString());
            LivingRealm.dirty(p);
        }
    }

    public static boolean lens(ServerPlayer p) {
        List mobs = p.m_284548_().m_6443_(Mob.class, p.m_20191_().m_82400_(128.0), m -> WildBosses.boss((Entity)m) && !ShadowService.isShadow((Entity)m) && m.m_6084_());
        Mob near = mobs.stream().min(Comparator.comparingDouble(m -> m.m_20280_((Entity)p))).orElse(null);
        if (near == null) {
            LivingRealm.tell(p, "no_boss", new Object[0]);
            return false;
        }
        BlockPos at = near.m_20183_();
        LivingRealm.tell(p, "lens", near.m_7755_().getString(), at.m_123341_(), at.m_123342_(), at.m_123343_(), (int)p.m_20270_((Entity)near));
        p.m_284548_().m_8624_(p, (ParticleOptions)ParticleTypes.f_123810_, true, near.m_20185_(), near.m_20186_() + 1.0, near.m_20189_(), 12, 0.3, 0.5, 0.3, 0.01);
        return true;
    }

    private static final class Retaliate
    extends Goal {
        private final Mob mob;
        private int hit;

        Retaliate(Mob m) {
            this.mob = m;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        private LivingEntity target() {
            Player p;
            LivingEntity t = this.mob.m_21188_();
            return t != null && t.m_6084_() && (!(t instanceof Player) || !(p = (Player)t).m_7500_() && !p.m_5833_()) && this.mob.m_20280_((Entity)t) < 1024.0 ? t : null;
        }

        public boolean m_8036_() {
            return this.target() != null && !ShadowService.isShadow((Entity)this.mob);
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            LivingEntity t = this.target();
            if (t == null) {
                return;
            }
            this.mob.m_21563_().m_24960_((Entity)t, 30.0f, 30.0f);
            this.mob.m_21573_().m_5624_((Entity)t, 1.25);
            if (this.hit > 0) {
                --this.hit;
            }
            double reach = Math.max(2.0f, this.mob.m_20205_() + t.m_20205_());
            if (this.hit == 0 && this.mob.m_20280_((Entity)t) < reach * reach && this.mob.m_142582_((Entity)t)) {
                if (this.mob.m_21051_(Attributes.f_22281_) != null) {
                    this.mob.m_7327_((Entity)t);
                } else {
                    t.m_6469_(this.mob.m_269291_().m_269333_((LivingEntity)this.mob), (float)WildBosses.fallback(this.mob, 10.0));
                }
                this.hit = 20;
            }
        }

        public void m_8041_() {
            this.mob.m_21573_().m_26573_();
        }
    }
}

