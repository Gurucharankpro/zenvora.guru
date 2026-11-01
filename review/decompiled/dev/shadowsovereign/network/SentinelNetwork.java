/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
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

import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.client.SentinelScreen;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.sentinel.PatrolTorches;
import dev.shadowsovereign.sentinel.SentinelContent;
import dev.shadowsovereign.sentinel.SentinelService;
import dev.shadowsovereign.vigil.VigilGraves;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class SentinelNetwork {
    private static SimpleChannel channel;

    public static boolean perform(ServerPlayer p, Request q) {
        ShadowRecord r;
        ShadowRecord shadowRecord = r = q.soul == null ? null : GuideService.army((ServerPlayer)p).shadows.get(q.soul);
        if (r == null) {
            return false;
        }
        return q.action == 2 ? PatrolTorches.assign(p, r, q.node) : q.action == 1 && SentinelService.command(p, r, q.command);
    }

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(36, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(37, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(int action, UUID id, String command, BlockPos node) {
        channel.sendToServer((Object)new Request(action, id, command, node));
    }

    public static void open(ServerPlayer p, BlockPos node, UUID id) {
        SentinelNetwork.send(p, node, id, true);
    }

    public static CompoundTag view(ServerPlayer p, BlockPos node, UUID id) {
        CompoundTag out = new CompoundTag();
        if (id != null && GuideService.army((ServerPlayer)p).shadows.containsKey(id)) {
            out.m_128362_("Selected", id);
        }
        if (PatrolTorches.nearNode(p, node)) {
            out.m_128356_("Node", node.m_121878_());
            List<SentinelContent.Node> nodes = PatrolTorches.route(p, node);
            out.m_128405_("Nodes", nodes.size());
        }
        ListTag list = new ListTag();
        for (ShadowRecord r : GuideService.army((ServerPlayer)p).shadows.values()) {
            CompoundTag n = new CompoundTag();
            Mob m = ShadowService.active(p.f_8924_, r);
            n.m_128362_("Id", r.id);
            n.m_128359_("Name", LegacyService.name(p, r));
            n.m_128359_("Type", r.type);
            n.m_128405_("Level", r.level());
            n.m_128356_("Kills", r.totalKills);
            n.m_128405_("Bond", r.bond);
            n.m_128405_("Affection", r.life.affection);
            n.m_128405_("Personality", r.life.personality);
            n.m_128350_("Health", m == null ? r.health : m.m_21223_());
            n.m_128350_("MaxHealth", m == null ? r.maxHealth : m.m_21233_());
            n.m_128356_("Position", (m == null ? r.sentinel.last : m.m_20183_()).m_121878_());
            n.m_128379_("Reforming", r.pendingRespawn());
            n.m_128405_("CemeteryFee", VigilGraves.needsPayment(r) ? r.vigil.fee : 0);
            n.m_128359_("Order", r.order.name());
            n.m_128359_("Style", r.sentinel.style.name());
            n.m_128379_("Active", r.activeEntity != null);
            n.m_128379_("Loaded", m != null);
            n.m_128379_("Near", m != null && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 16.0);
            n.m_128379_("Free", SentinelService.free(p, r));
            n.m_128379_("Paused", r.battle.paused);
            n.m_128405_("Route", r.battle.route.size());
            n.m_128379_("TorchRoute", r.sentinel.torchRoute);
            n.m_128359_("Dimension", r.dimension);
            n.m_128379_("Wanted", r.sentinel.wanted);
            list.add((Object)n);
        }
        out.m_128365_("Souls", (Tag)list);
        return out;
    }

    private static void send(ServerPlayer p, BlockPos node, UUID id, boolean open) {
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(SentinelNetwork.view(p, node, id), open));
    }

    public record Request(int action, UUID soul, String command, BlockPos node) {
        void encode(FriendlyByteBuf b) {
            b.writeByte(this.action);
            b.writeBoolean(this.soul != null);
            if (this.soul != null) {
                b.m_130077_(this.soul);
            }
            b.m_130072_(this.command, 32);
            b.writeBoolean(this.node != null);
            if (this.node != null) {
                b.m_130064_(this.node);
            }
        }

        static Request decode(FriendlyByteBuf b) {
            return new Request(b.readByte(), b.readBoolean() ? b.m_130259_() : null, b.m_130136_(32), b.readBoolean() ? b.m_130135_() : null);
        }

        void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> {
                boolean ok;
                ServerPlayer p = ctx.getSender();
                if (p == null || !p.m_6084_() || p.m_5833_() || this.action < 0 || this.action > 2) {
                    return;
                }
                long now = GuideService.now(p);
                if (p.getPersistentData().m_128441_("SentinelAt") && now - p.getPersistentData().m_128454_("SentinelAt") < (long)(this.action == 0 ? 10 : 4)) {
                    return;
                }
                p.getPersistentData().m_128356_("SentinelAt", now);
                boolean bl = ok = this.action == 0 || SentinelNetwork.perform(p, this);
                if (!ok) {
                    p.m_5661_((Component)Component.m_237115_((String)"sentinel.shadowsovereign.unavailable"), true);
                }
                SentinelNetwork.send(p, this.node, this.soul, false);
            });
            ctx.setPacketHandled(true);
        }
    }

    public record View(CompoundTag data, boolean open) {
        void encode(FriendlyByteBuf b) {
            b.m_130079_(this.data);
            b.writeBoolean(this.open);
        }

        static View decode(FriendlyByteBuf b) {
            return new View(b.m_130260_(), b.readBoolean());
        }

        void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> SentinelScreen.accept(this.data, this.open)));
            ctx.setPacketHandled(true);
        }
    }
}

