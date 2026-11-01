/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package dev.shadowsovereign.network;

import dev.shadowsovereign.client.MasteryScreen;
import dev.shadowsovereign.mastery.MasteryCatalog;
import dev.shadowsovereign.mastery.MasteryService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.sentinel.MasteryRewards;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class MasteryNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(34, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(35, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(int action, String id) {
        channel.sendToServer((Object)new Request(action, id));
    }

    public static void open(ServerPlayer p) {
        GuideService.record(p, "guide_open");
        CompoundTag data = MasteryService.view(p);
        data.m_128379_("Open", true);
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(data));
    }

    public static void send(ServerPlayer p) {
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(MasteryService.view(p)));
    }

    public record Request(int action, String id) {
        void encode(FriendlyByteBuf b) {
            b.writeByte(this.action);
            b.m_130072_(this.id, 96);
        }

        static Request decode(FriendlyByteBuf b) {
            return new Request(b.readByte(), b.m_130136_(96));
        }

        void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                if (p == null || !p.m_6084_() || p.m_5833_() || this.action < 0 || this.action > 5) {
                    return;
                }
                long now = GuideService.now(p);
                CompoundTag n = p.getPersistentData();
                if (n.m_128441_("MasteryRequestAt") && now - n.m_128454_("MasteryRequestAt") < (long)(this.action == 0 ? 10 : 3)) {
                    return;
                }
                n.m_128356_("MasteryRequestAt", now);
                if (this.action == 1) {
                    MasteryService.read(p, this.id);
                }
                if (this.action == 2) {
                    MasteryService.observe(p);
                    MasteryService.claim(p, this.id);
                }
                if (this.action == 3) {
                    MasteryService.claimReady(p);
                }
                if (this.action == 5) {
                    MasteryRewards.collect(p);
                }
                if (this.action == 4 && (this.id.isEmpty() || MasteryCatalog.get(this.id) != null)) {
                    GuideService.state((ServerPlayer)p).camp.m_128359_("MasteryPin", this.id);
                    GuideService.dirty(p);
                }
                MasteryNetwork.send(p);
            });
            ctx.setPacketHandled(true);
        }
    }

    public record View(CompoundTag data) {
        void encode(FriendlyByteBuf b) {
            b.m_130079_(this.data);
        }

        static View decode(FriendlyByteBuf b) {
            return new View(b.m_130260_());
        }

        void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> MasteryScreen.accept(this.data)));
            ctx.setPacketHandled(true);
        }
    }
}

