/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 */
package dev.shadowsovereign.army;

import dev.shadowsovereign.army.BattleState;
import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.sentinel.SentinelState;
import dev.shadowsovereign.soulforge.CompanionInventory;
import dev.shadowsovereign.soulforge.SoulTier;
import dev.shadowsovereign.soulforge.SoulTrait;
import dev.shadowsovereign.soullife.SoulLifeState;
import dev.shadowsovereign.vigil.VigilState;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class ShadowRecord {
    public final UUID id;
    public final String type;
    public final String name;
    public CompoundTag snapshot;
    public UUID activeEntity;
    public String dimension = "minecraft:overworld";
    public Order order = Order.FOLLOW;
    public boolean aggressive = true;
    public BlockPos anchor = BlockPos.f_121853_;
    public float health;
    public float maxHealth;
    public long totalKills;
    public long lastDamageAt = -1L;
    public long nextHealAt = -1L;
    public long respawnAt = -1L;
    public long nextRespawnAttemptAt;
    public VigilState.Companion vigil = new VigilState.Companion();
    public SoulLifeState life = new SoulLifeState();
    public OdysseyState.Companion odyssey = new OdysseyState.Companion();
    public SentinelState sentinel = new SentinelState();
    public BattleState battle = new BattleState();
    public SoulTier tier = SoulTier.COMMON;
    public SoulTrait trait = SoulTrait.NONE;
    public boolean soulAwakened;
    public boolean soulAcquired;
    public boolean collectLoot;
    public int bond;
    public long nextCareAt;
    public CompanionInventory inventory = new CompanionInventory();

    public int level() {
        return CompanionProgression.level(this.totalKills);
    }

    public double damageMultiplier() {
        return CompanionProgression.multiplier(this.totalKills);
    }

    public boolean pendingRespawn() {
        return this.respawnAt >= 0L;
    }

    public ShadowRecord(UUID id, String type, String name, CompoundTag snapshot, float maxHealth) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.snapshot = snapshot;
        this.health = this.maxHealth = maxHealth;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.m_128362_("Id", this.id);
        tag.m_128359_("Type", this.type);
        tag.m_128359_("Name", this.name);
        tag.m_128365_("Snapshot", (Tag)this.snapshot.m_6426_());
        if (this.activeEntity != null) {
            tag.m_128362_("Active", this.activeEntity);
        }
        tag.m_128359_("Dimension", this.dimension);
        tag.m_128359_("Order", this.order.name());
        tag.m_128379_("Aggressive", this.aggressive);
        tag.m_128356_("Anchor", this.anchor.m_121878_());
        tag.m_128350_("Health", this.health);
        tag.m_128350_("MaxHealth", this.maxHealth);
        tag.m_128356_("TotalKills", this.totalKills);
        tag.m_128405_("Level", this.level());
        tag.m_128356_("LastDamageAt", this.lastDamageAt);
        tag.m_128356_("NextHealAt", this.nextHealAt);
        tag.m_128356_("RespawnAt", this.respawnAt);
        tag.m_128365_("Sentinel", (Tag)this.sentinel.save());
        tag.m_128365_("Odyssey", (Tag)this.odyssey.save());
        tag.m_128365_("SoulLife", (Tag)this.life.save());
        tag.m_128365_("Battle", (Tag)this.battle.save());
        tag.m_128365_("Vigil", (Tag)this.vigil.save());
        tag.m_128359_("SoulTier", this.tier.name());
        tag.m_128359_("SoulTrait", this.trait.name());
        tag.m_128379_("SoulAwakened", this.soulAwakened);
        tag.m_128379_("CollectLoot", this.collectLoot);
        tag.m_128379_("SoulAcquired", this.soulAcquired);
        tag.m_128405_("CompanionBond", this.bond);
        tag.m_128356_("NextCareAt", this.nextCareAt);
        this.inventory.initialize(this.snapshot);
        tag.m_128365_("CompanionInventory", (Tag)this.inventory.save());
        return tag;
    }

    public static ShadowRecord load(CompoundTag tag) {
        ShadowRecord r = new ShadowRecord(tag.m_128342_("Id"), tag.m_128461_("Type"), tag.m_128461_("Name"), tag.m_128469_("Snapshot").m_6426_(), tag.m_128457_("MaxHealth"));
        if (tag.m_128403_("Active")) {
            r.activeEntity = tag.m_128342_("Active");
        }
        r.dimension = tag.m_128461_("Dimension");
        r.order = Order.parse(tag.m_128461_("Order"));
        r.aggressive = tag.m_128471_("Aggressive");
        r.anchor = BlockPos.m_122022_((long)tag.m_128454_("Anchor"));
        r.health = tag.m_128457_("Health");
        r.totalKills = Math.max(0L, tag.m_128454_("TotalKills"));
        r.lastDamageAt = tag.m_128441_("LastDamageAt") ? tag.m_128454_("LastDamageAt") : -1L;
        r.nextHealAt = tag.m_128441_("NextHealAt") ? tag.m_128454_("NextHealAt") : -1L;
        long l = r.respawnAt = tag.m_128441_("RespawnAt") ? tag.m_128454_("RespawnAt") : -1L;
        if (r.pendingRespawn()) {
            r.activeEntity = null;
        }
        r.vigil = VigilState.Companion.load(tag.m_128469_("Vigil"));
        if (!tag.m_128441_("Vigil") && r.order == Order.GUARD) {
            r.vigil.post = true;
            r.vigil.postPos = r.anchor.m_121878_();
            r.vigil.postDimension = r.dimension;
            r.vigil.suspended = r.activeEntity == null;
        }
        r.life = SoulLifeState.load(tag.m_128469_("SoulLife"));
        r.odyssey = OdysseyState.Companion.load(tag.m_128469_("Odyssey"));
        r.battle = BattleState.load(tag.m_128469_("Battle"));
        r.sentinel = SentinelState.load(tag.m_128469_("Sentinel"));
        if (!tag.m_128441_("Sentinel")) {
            r.sentinel.wanted = r.activeEntity != null || r.pendingRespawn();
            r.sentinel.last = r.anchor;
            if (r.order == Order.GUARD) {
                r.sentinel.home = r.anchor;
                r.sentinel.homeDimension = r.dimension;
            }
        }
        r.tier = SoulTier.read(tag.m_128461_("SoulTier"));
        r.trait = SoulTrait.read(tag.m_128461_("SoulTrait"));
        r.soulAwakened = tag.m_128471_("SoulAwakened");
        r.collectLoot = tag.m_128471_("CollectLoot");
        r.soulAcquired = tag.m_128471_("SoulAcquired");
        r.bond = Math.max(0, Math.min(150, tag.m_128451_("CompanionBond")));
        r.nextCareAt = Math.max(0L, tag.m_128454_("NextCareAt"));
        r.inventory = CompanionInventory.load(tag.m_128469_("CompanionInventory"));
        r.inventory.initialize(r.snapshot);
        return r;
    }
}

