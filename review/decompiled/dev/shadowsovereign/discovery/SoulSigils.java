/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.server.ServerStoppedEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.discovery;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.ShadowSovereign;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.MythicState;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.soulforge.CompanionService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class SoulSigils {
    private static MinecraftServer server;
    private static final ArrayList<ResourceLocation> candidates;

    @SubscribeEvent
    public static void stopped(ServerStoppedEvent e) {
        if (server == e.getServer()) {
            server = null;
            candidates.clear();
        }
    }

    public static List<ResourceLocation> candidates(MinecraftServer current) {
        if (server != current) {
            server = current;
            candidates.clear();
            candidates.addAll(ForgeRegistries.ENTITY_TYPES.getKeys());
            candidates.sort(Comparator.naturalOrder());
        }
        return List.copyOf(candidates);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean use(ServerPlayer p) {
        if (!p.m_6084_() || p.m_5833_()) {
            return false;
        }
        SoulSigils.candidates(p.f_8924_);
        for (int attempt = 0; attempt < 128 && !candidates.isEmpty(); ++attempt) {
            int slot = p.m_217043_().m_188503_(candidates.size());
            ResourceLocation key = candidates.get(slot);
            if (((List)ShadowConfig.BLOCKLIST.get()).contains(key.toString())) {
                candidates.remove(slot);
                continue;
            }
            Entity probe = null;
            ShadowRecord reward = null;
            try {
                Mob m;
                EntityType type = (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(key);
                Entity entity = probe = type == null ? null : type.m_20615_((Level)p.m_284548_());
                if (!(probe instanceof Mob) || !Float.isFinite((m = (Mob)probe).m_21233_()) || m.m_21233_() <= 0.0f) {
                    candidates.remove(slot);
                    continue;
                }
                CompoundTag snap = ShadowService.snapshot(m, true);
                reward = new ShadowRecord(UUID.randomUUID(), key.toString(), m.m_7755_().getString(), snap, m.m_21233_());
            }
            catch (LinkageError | RuntimeException ex) {
                candidates.remove(key);
                ShadowSovereign.LOGGER.warn("Soul sigil skipped incompatible entity {}: {}", (Object)key, (Object)ex.toString());
            }
            finally {
                if (probe != null) {
                    probe.m_146870_();
                }
            }
            if (reward == null) continue;
            SoulSigils.award(p, reward);
            return true;
        }
        p.m_5661_((Component)Component.m_237115_((String)"discovery.shadowsovereign.retry"), false);
        return false;
    }

    public static void award(ServerPlayer p, ShadowRecord r) {
        boolean pending;
        CompanionService.awaken(p, r);
        CompanionService.acquired(p, r);
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        boolean bl = pending = army.shadows.size() >= (Integer)ShadowConfig.MAX_ARMY.get();
        if (pending) {
            LivingRealm.state((ServerPlayer)p).pending.put(r.id, r);
        } else {
            army.shadows.put(r.id, r);
        }
        SoulSigils.unlock(p, r);
        ArmyData.get(p.f_8924_).m_77762_();
        LivingRealm.dirty(p);
        if (!pending) {
            AdventureService.captured(p, r);
            try {
                ShadowService.summon(p, r, false);
            }
            catch (LinkageError | RuntimeException ex) {
                ShadowSovereign.LOGGER.warn("Soul {} retained in reserve after incompatible summon: {}", (Object)r.type, (Object)ex.toString());
            }
        }
        p.m_5661_((Component)Component.m_237110_((String)(pending ? "discovery.shadowsovereign.pending" : "discovery.shadowsovereign.awarded"), (Object[])new Object[]{r.name, Component.m_237115_((String)("soul_tier.shadowsovereign." + r.tier.name().toLowerCase(Locale.ROOT)))}), false);
        ShadowNetwork.sendArmy(p, false);
        if (SoulSigils.isMythic(r.type)) {
            MythicNetwork.send(p, false);
        }
    }

    private static boolean owned(ServerPlayer p, UUID id) {
        return id != null && (ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.containsKey(id) || LivingRealm.state((ServerPlayer)p).pending.containsKey(id));
    }

    private static boolean isMythic(String type) {
        if (type.equals("shadowsovereign:soul_devourer")) {
            return true;
        }
        for (String id : MythicExpedition.IDS) {
            if (!type.equals("shadowsovereign:" + id)) continue;
            return true;
        }
        return false;
    }

    private static void unlock(ServerPlayer p, ShadowRecord r) {
        if (!SoulSigils.isMythic(r.type)) {
            return;
        }
        int kind = -1;
        for (int i = 0; i < MythicExpedition.IDS.length; ++i) {
            if (!r.type.equals("shadowsovereign:" + MythicExpedition.IDS[i])) continue;
            kind = i;
        }
        AdventureData.Session session = MythicService.session(p);
        if (session != null && MythicExpedition.current(session) == kind) {
            MythicService.cancel(p);
        }
        if (kind < 0) {
            MythicState state = MythicService.state(p);
            state.status = 2;
            if (!SoulSigils.owned(p, state.soul)) {
                state.soul = r.id;
            }
        } else {
            CompoundTag state = MythicExpedition.state(p, kind);
            state.m_128405_("Status", 2);
            if (!state.m_128403_("Soul") || !SoulSigils.owned(p, state.m_128342_("Soul"))) {
                state.m_128362_("Soul", r.id);
            }
        }
    }

    static {
        candidates = new ArrayList();
    }
}

