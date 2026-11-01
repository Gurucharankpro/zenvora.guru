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

import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.client.ClientSovereign;
import dev.shadowsovereign.sovereign.SovereignService;
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

public final class SovereignNetwork {
    private static SimpleChannel channel;

    public static int perform(ServerPlayer p, Request r) {
        if (r.action != Action.VIEW && (!p.m_6084_() || p.m_5833_())) {
            return 0;
        }
        return switch (r.action) {
            default -> throw new IncompatibleClassChangeError();
            case Action.VIEW -> 1;
            case Action.ACCEPT -> {
                if (SovereignService.accept(p, r.value, r.companion)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.ABANDON -> {
                if (SovereignService.abandon(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CLAIM -> {
                if (SovereignService.claim(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CREST -> SovereignService.crest(p, r.value) ? 1 : 0;
        };
    }

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(15, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(16, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action a, int v, UUID id) {
        channel.sendToServer((Object)new Request(a, v, id));
    }

    public static void send(ServerPlayer p) {
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(SovereignService.view(p)));
    }

    public record Request(Action action, int value, UUID companion) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.m_130130_(this.value);
            b.writeBoolean(this.companion != null);
            if (this.companion != null) {
                b.m_130077_(this.companion);
            }
        }

        public static Request decode(FriendlyByteBuf b) {
            return new Request((Action)b.m_130066_(Action.class), b.m_130242_(), b.readBoolean() ? b.m_130259_() : null);
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            NetworkEvent.Context c = context.get();
            c.enqueueWork(() -> {
                String key;
                ServerPlayer p = c.getSender();
                if (p == null) {
                    return;
                }
                long now = p.f_8924_.m_129783_().m_46467_();
                String string = key = this.action == Action.VIEW ? "SovereignView" : "SovereignAction";
                if (p.getPersistentData().m_128441_(key) && now - p.getPersistentData().m_128454_(key) < (long)(this.action == Action.VIEW ? 10 : 3)) {
                    return;
                }
                p.getPersistentData().m_128356_(key, now);
                if (SovereignNetwork.perform(p, this) == 0 && this.action != Action.VIEW) {
                    ShadowService.tell(p, "Trail action unavailable. Check the champion, unlocks and current objective.");
                }
                SovereignNetwork.send(p);
            });
            c.setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        ACCEPT,
        ABANDON,
        CLAIM,
        CREST;

    }

    public record View(CompoundTag data) {
        public void encode(FriendlyByteBuf b) {
            b.m_130079_(this.data);
        }

        public static View decode(FriendlyByteBuf b) {
            CompoundTag n = b.m_130260_();
            return new View(n == null ? new CompoundTag() : n);
        }

        public void handle(Supplier<NetworkEvent.Context> c) {
            c.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> {
                ClientSovereign.data = this.data;
            }));
            c.get().setPacketHandled(true);
        }
    }
}

