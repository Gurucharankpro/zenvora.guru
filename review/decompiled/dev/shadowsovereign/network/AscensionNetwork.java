/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package dev.shadowsovereign.network;

import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.AscensionService;
import dev.shadowsovereign.ascension.SoulRecord;
import dev.shadowsovereign.ascension.SoulService;
import dev.shadowsovereign.client.ClientAscension;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.network.LegacyNetwork;
import dev.shadowsovereign.warfront.WarfrontService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class AscensionNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(11, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(12, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action a, int v, boolean hard) {
        channel.sendToServer((Object)new Request(a, v, hard));
    }

    public static void start(int mode, boolean hard, List<UUID> selected) {
        channel.sendToServer((Object)new Request(Action.START, mode, hard, selected));
    }

    public static void send(ServerPlayer p, boolean open) {
        if (channel != null) {
            channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(AscensionNetwork.snapshot(p), open));
        }
    }

    public static int perform(ServerPlayer p, Request r) {
        if (r.action != Action.VIEW && (!p.m_6084_() || p.m_5833_() || JourneyView.active(p))) {
            return 0;
        }
        return switch (r.action) {
            default -> throw new IncompatibleClassChangeError();
            case Action.VIEW -> 1;
            case Action.CLAIM_SOUL -> {
                if (SoulService.claim(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.START -> {
                if (AscensionService.start(p, r.value, r.hard, r.selected)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.INTERACT -> {
                if (EclipseRuns.session(p) != null ? EclipseRuns.interact(p) : (WarfrontService.state((ServerPlayer)p).placementUntil > WarfrontService.now(p) || WarfrontService.session(p) != null ? WarfrontService.interact(p) : AscensionService.interact(p))) {
                    yield 1;
                }
                yield 0;
            }
            case Action.DRAFT_PICK -> {
                if (AscensionService.pick(p, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PAUSE -> {
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                yield 1;
            }
            case Action.RESUME -> {
                if (AdventureWorld.resume(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CANCEL -> {
                if (AdventureWorld.cancel(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CLAIM_REWARD -> AdventureWorld.claim(p) ? 1 : 0;
        };
    }

    public static CompoundTag snapshot(ServerPlayer p) {
        CompoundTag root = LegacyNetwork.snapshot(p, null);
        root.m_128379_("HasAlly", ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().stream().anyMatch(r -> LegacyService.eligible(p, r.id, 1, 32) != null));
        root.m_128359_("RecruitMode", SoulService.mode(p));
        root.m_128359_("SoulDimension", p.m_9236_().m_46472_().m_135782_().toString());
        ListTag souls = new ListTag();
        for (SoulRecord s : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).souls.values()) {
            if (!SoulService.visible(p, s)) continue;
            CompoundTag n = new CompoundTag();
            n.m_128362_("Id", s.id);
            n.m_128359_("Name", s.companion.name);
            n.m_128347_("X", s.position.f_82479_);
            n.m_128347_("Y", s.position.f_82480_);
            n.m_128347_("Z", s.position.f_82481_);
            n.m_128356_("Expires", s.expires);
            souls.add((Object)n);
        }
        root.m_128365_("Souls", (Tag)souls);
        AscensionService.publicState(p, root);
        return root;
    }

    public record Request(Action action, int value, boolean hard, List<UUID> selected) {
        public Request(Action action, int value, boolean hard) {
            this(action, value, hard, List.of());
        }

        public Request {
            selected = List.copyOf(selected);
            if (selected.size() > 3) {
                throw new IllegalArgumentException("Too many mirrors");
            }
        }

        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.m_130130_(this.value);
            b.writeBoolean(this.hard);
            b.m_130130_(this.selected.size());
            this.selected.forEach(arg_0 -> ((FriendlyByteBuf)b).m_130077_(arg_0));
        }

        public static Request decode(FriendlyByteBuf b) {
            Action action = (Action)b.m_130066_(Action.class);
            int value = b.m_130242_();
            boolean hard = b.readBoolean();
            int count = b.m_130242_();
            if (count < 0 || count > 3) {
                throw new IllegalArgumentException("Invalid mirror selection");
            }
            ArrayList<UUID> selected = new ArrayList<UUID>();
            for (int i = 0; i < count; ++i) {
                selected.add(b.m_130259_());
            }
            return new Request(action, value, hard, selected);
        }

        public void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                String key = this.action == Action.VIEW ? "AscensionViewAt" : "AscensionActionAt";
                CompoundTag n = p.getPersistentData();
                long time = SoulService.now(p);
                if (n.m_128441_(key) && time >= n.m_128454_(key) && time - n.m_128454_(key) < (long)(this.action == Action.VIEW ? 5 : 2)) {
                    return;
                }
                n.m_128356_(key, time);
                int result = AscensionNetwork.perform(p, this);
                if (result == 0 && this.action != Action.VIEW && this.action != Action.CLAIM_SOUL) {
                    ShadowService.tell(p, "Action unavailable. Check your Sanctuary, nearby ally, selected objective and active session.");
                }
                AscensionNetwork.send(p, false);
            });
            ctx.setPacketHandled(true);
        }
    }

    public record View(CompoundTag data, boolean open) {
        public void encode(FriendlyByteBuf b) {
            b.m_130079_(this.data);
            b.writeBoolean(this.open);
        }

        public static View decode(FriendlyByteBuf b) {
            CompoundTag n = b.m_130260_();
            if (n == null) {
                throw new IllegalArgumentException("Missing view");
            }
            return new View(n, b.readBoolean());
        }

        public void handle(Supplier<NetworkEvent.Context> c) {
            c.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientAscension.accept(this)));
            c.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        CLAIM_SOUL,
        START,
        INTERACT,
        DRAFT_PICK,
        PAUSE,
        RESUME,
        CANCEL,
        CLAIM_REWARD;

    }
}

