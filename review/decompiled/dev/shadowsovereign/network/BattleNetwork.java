/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package dev.shadowsovereign.network;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.BattleService;
import dev.shadowsovereign.army.BattleState;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.client.ClientBattle;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class BattleNetwork {
    private static SimpleChannel channel;

    public static boolean allowRequest(ServerPlayer p, Action action) {
        String key = action == Action.SELECT_SCOPE ? "SoulboundLastScopePacket" : "SoulboundLastBattlePacket";
        long now = p.f_8924_.m_129783_().m_46467_();
        long previous = p.getPersistentData().m_128454_(key);
        if (p.getPersistentData().m_128441_(key) && now >= previous && now - previous < 2L) {
            return false;
        }
        p.getPersistentData().m_128356_(key, now);
        return true;
    }

    public static void register(SimpleChannel ch) {
        channel = ch;
        ch.registerMessage(3, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        ch.registerMessage(4, State.class, State::encode, State::decode, State::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action action, BattleRules.Squad scope, UUID id, int value) {
        channel.sendToServer((Object)new Request(action, scope, id, value));
    }

    public static void send(ServerPlayer p) {
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        List<Entry> entries = army.shadows.values().stream().map(r -> {
            BattleState b = r.battle;
            Mob mob = ShadowService.active(p.f_8924_, r);
            boolean near = mob != null && mob.m_6084_() && mob.m_9236_() == p.m_9236_() && mob.m_20280_((Entity)p) <= 1024.0;
            return new Entry(r.id, b.squad, b.formation, b.priority, b.retreat, List.copyOf(b.route), b.routeDimension, b.waypoint, b.paused, b.shield, b.shieldUntil, near);
        }).toList();
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new State(entries, army.selectedSquad, Map.copyOf(army.readyAt), p.f_8924_.m_129783_().m_46467_()));
    }

    public static int perform(ServerPlayer p, Request request) {
        ShadowNetwork.Action legacy;
        Action action = request.action;
        BattleRules.Squad scope = request.scope;
        UUID id = request.companion;
        int value = request.value;
        if (id != null && BattleService.records(p, BattleRules.Squad.ALL, id).isEmpty()) {
            return 0;
        }
        switch (action) {
            case SELECT_SCOPE: {
                ArmyData data = ArmyData.get(p.f_8924_);
                data.army((UUID)p.m_20148_()).selectedSquad = scope;
                data.m_77762_();
                return 1;
            }
            case ASSIGN: {
                if (id != null && value > 0 && value < BattleRules.Squad.values().length) {
                    BattleService.assign(p, id, BattleRules.Squad.values()[value]);
                    return 1;
                }
                return 0;
            }
            case FORMATION: {
                return value >= 0 && value < BattleRules.Formation.values().length ? BattleService.setFormation(p, scope, id, BattleRules.Formation.values()[value]) : 0;
            }
            case PRIORITY: {
                return value >= 0 && value < BattleRules.Priority.values().length ? BattleService.setPriority(p, scope, id, BattleRules.Priority.values()[value]) : 0;
            }
            case RETREAT: {
                return BattleService.setRetreat(p, scope, id, value);
            }
            case PATROL_ADD: {
                return BattleService.addWaypoint(p, scope, id);
            }
            case PATROL_CLEAR: {
                return BattleService.clearRoute(p, scope, id);
            }
            case PATROL_START: {
                return BattleService.startPatrol(p, scope, id);
            }
            case SKILL: {
                return value >= 0 && value < BattleRules.Skill.values().length ? BattleService.cast(p, BattleRules.Skill.values()[value], scope, id) : 0;
            }
        }
        List<ShadowRecord> records = BattleService.records(p, scope, id);
        if (action == Action.ATTACK) {
            return ShadowNetwork.attackLookingAt(p, records);
        }
        switch (action) {
            case FOLLOW: {
                ShadowNetwork.Action action2 = ShadowNetwork.Action.FOLLOW;
                break;
            }
            case STAY: {
                ShadowNetwork.Action action2 = ShadowNetwork.Action.STAY;
                break;
            }
            case GUARD: {
                ShadowNetwork.Action action2 = ShadowNetwork.Action.GUARD;
                break;
            }
            case SUMMON: {
                ShadowNetwork.Action action2 = ShadowNetwork.Action.SUMMON;
                break;
            }
            case RECALL: {
                ShadowNetwork.Action action2 = ShadowNetwork.Action.RECALL;
                break;
            }
            default: {
                ShadowNetwork.Action action2 = legacy = null;
            }
        }
        if (legacy == null) {
            return 0;
        }
        int changed = 0;
        for (ShadowRecord r : records) {
            changed += ShadowNetwork.perform(p, legacy, r.id);
        }
        return changed;
    }

    public static enum Action {
        SELECT_SCOPE,
        ASSIGN,
        FORMATION,
        PRIORITY,
        RETREAT,
        PATROL_ADD,
        PATROL_CLEAR,
        PATROL_START,
        SKILL,
        FOLLOW,
        STAY,
        GUARD,
        ATTACK,
        SUMMON,
        RECALL;

    }

    public record Request(Action action, BattleRules.Squad scope, UUID companion, int value) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.m_130068_((Enum)this.scope);
            b.writeBoolean(this.companion != null);
            if (this.companion != null) {
                b.m_130077_(this.companion);
            }
            b.m_130130_(this.value);
        }

        public static Request decode(FriendlyByteBuf b) {
            return new Request((Action)b.m_130066_(Action.class), (BattleRules.Squad)b.m_130066_(BattleRules.Squad.class), b.readBoolean() ? b.m_130259_() : null, b.m_130242_());
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                if (!BattleNetwork.allowRequest(p, this.action)) {
                    return;
                }
                BattleNetwork.perform(p, this);
                ShadowNetwork.sendArmy(p, false);
            });
            ctx.setPacketHandled(true);
        }
    }

    public record State(List<Entry> entries, BattleRules.Squad selected, Map<BattleRules.Skill, Long> readyAt, long gameTime) {
        public void encode(FriendlyByteBuf b) {
            b.m_130130_(this.entries.size());
            this.entries.forEach(e -> e.encode(b));
            b.m_130068_((Enum)this.selected);
            for (BattleRules.Skill skill : BattleRules.Skill.values()) {
                b.writeLong(this.readyAt.getOrDefault((Object)skill, 0L).longValue());
            }
            b.writeLong(this.gameTime);
        }

        public static State decode(FriendlyByteBuf b) {
            int size = b.m_130242_();
            if (size < 0 || size > 1024) {
                throw new IllegalArgumentException("Invalid army size");
            }
            ArrayList<Entry> entries = new ArrayList<Entry>();
            for (int i = 0; i < size; ++i) {
                entries.add(Entry.decode(b));
            }
            BattleRules.Squad selected = (BattleRules.Squad)b.m_130066_(BattleRules.Squad.class);
            EnumMap<BattleRules.Skill, Long> cooldowns = new EnumMap<BattleRules.Skill, Long>(BattleRules.Skill.class);
            for (BattleRules.Skill skill : BattleRules.Skill.values()) {
                cooldowns.put(skill, b.readLong());
            }
            return new State(List.copyOf(entries), selected, Map.copyOf(cooldowns), b.readLong());
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientBattle.accept(this)));
            supplier.get().setPacketHandled(true);
        }
    }

    public record Entry(UUID id, BattleRules.Squad squad, BattleRules.Formation formation, BattleRules.Priority priority, int retreat, List<BlockPos> route, String dimension, int waypoint, boolean paused, float shield, long shieldUntil, boolean nearby) {
        void encode(FriendlyByteBuf b) {
            b.m_130077_(this.id);
            b.m_130068_((Enum)this.squad);
            b.m_130068_((Enum)this.formation);
            b.m_130068_((Enum)this.priority);
            b.m_130130_(this.retreat);
            b.m_130130_(this.route.size());
            this.route.forEach(arg_0 -> ((FriendlyByteBuf)b).m_130064_(arg_0));
            b.m_130072_(this.dimension, 256);
            b.m_130130_(this.waypoint);
            b.writeBoolean(this.paused);
            b.writeFloat(this.shield);
            b.writeLong(this.shieldUntil);
            b.writeBoolean(this.nearby);
        }

        static Entry decode(FriendlyByteBuf b) {
            UUID id = b.m_130259_();
            BattleRules.Squad squad = (BattleRules.Squad)b.m_130066_(BattleRules.Squad.class);
            BattleRules.Formation formation = (BattleRules.Formation)b.m_130066_(BattleRules.Formation.class);
            BattleRules.Priority priority = (BattleRules.Priority)b.m_130066_(BattleRules.Priority.class);
            int retreat = b.m_130242_();
            int size = b.m_130242_();
            if (size < 0 || size > 8) {
                throw new IllegalArgumentException("Invalid patrol size");
            }
            ArrayList<BlockPos> route = new ArrayList<BlockPos>();
            for (int i = 0; i < size; ++i) {
                route.add(b.m_130135_());
            }
            return new Entry(id, squad, formation, priority, retreat, List.copyOf(route), b.m_130136_(256), b.m_130242_(), b.readBoolean(), b.readFloat(), b.readLong(), b.readBoolean());
        }
    }
}

