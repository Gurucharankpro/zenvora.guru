/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package dev.shadowsovereign.network;

import dev.shadowsovereign.mythic.MythicBeast;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public final class MythicFlightNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(31, Input.class, Input::encode, Input::decode, Input::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void send(int vertical, boolean boost) {
        channel.sendToServer((Object)new Input(vertical, boost));
    }

    public record Input(int vertical, boolean boost) {
        public void encode(FriendlyByteBuf b) {
            b.writeByte(this.vertical);
            b.writeBoolean(this.boost);
        }

        public static Input decode(FriendlyByteBuf b) {
            return new Input(b.readByte(), b.readBoolean());
        }

        public void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> {
                Entity patt765$temp;
                ServerPlayer p = ctx.getSender();
                if (p == null || this.vertical < -1 || this.vertical > 1 || !((patt765$temp = p.m_20202_()) instanceof MythicBeast)) {
                    return;
                }
                MythicBeast m = (MythicBeast)patt765$temp;
                long now = p.f_8924_.m_129783_().m_46467_();
                if (p.getPersistentData().m_128441_("MythicFlightAt") && now - p.getPersistentData().m_128454_("MythicFlightAt") < 2L) {
                    return;
                }
                p.getPersistentData().m_128356_("MythicFlightAt", now);
                m.flightInput(p, this.vertical, this.boost);
            });
            ctx.setPacketHandled(true);
        }
    }
}

