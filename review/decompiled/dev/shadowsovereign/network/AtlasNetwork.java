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

import dev.shadowsovereign.client.AtlasScreen;
import dev.shadowsovereign.discovery.AtlasService;
import java.util.Optional;
import java.util.UUID;
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

public final class AtlasNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(32, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(33, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(UUID session, int action, String id) {
        channel.sendToServer((Object)new Request(session, action, id));
    }

    public static void view(ServerPlayer p, CompoundTag data, boolean open) {
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(data, open));
    }

    public record Request(UUID session, int action, String id) {
        void encode(FriendlyByteBuf b) {
            b.m_130077_(this.session);
            b.writeByte(this.action);
            b.m_130072_(this.id, 160);
        }

        static Request decode(FriendlyByteBuf b) {
            return new Request(b.m_130259_(), b.readByte(), b.m_130136_(160));
        }

        void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                if (p != null) {
                    AtlasService.request(p, this.session, this.action, this.id);
                }
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
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> AtlasScreen.accept(this.data, this.open)));
            ctx.setPacketHandled(true);
        }
    }
}

