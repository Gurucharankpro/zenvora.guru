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
 *  net.minecraft.world.entity.Mob
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package dev.shadowsovereign.network;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.client.ClientVigil;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.royal.RoyalSync;
import dev.shadowsovereign.vigil.VigilCombat;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilGraves;
import dev.shadowsovereign.vigil.VigilJobs;
import dev.shadowsovereign.vigil.VigilRuns;
import dev.shadowsovereign.vigil.VigilService;
import dev.shadowsovereign.vigil.VigilState;
import dev.shadowsovereign.vigil.VigilText;
import java.util.ArrayList;
import java.util.HashSet;
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
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class VigilNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel ch) {
        channel = ch;
        ch.registerMessage(21, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        ch.registerMessage(22, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action a, int x, int y, int nonce, UUID one, UUID two, List<UUID> ids) {
        channel.sendToServer((Object)new Request(a, x, y, nonce, one, two, List.copyOf(ids)));
    }

    public static boolean perform(ServerPlayer p, Request r) {
        if (r.action != Action.VIEW && !EclipseService.usable(p) || r.ids.size() > 4 || new HashSet<UUID>(r.ids).size() != r.ids.size()) {
            return false;
        }
        VigilState v = VigilService.state(p);
        return switch (r.action) {
            default -> throw new IncompatibleClassChangeError();
            case Action.VIEW -> true;
            case Action.PAY -> VigilGraves.pay(p, VigilService.record(p, r.a), r.x);
            case Action.OFFERINGS -> VigilGraves.offerings(p, r.x);
            case Action.POST -> VigilService.post(p, r.a);
            case Action.LINK -> VigilCombat.link(p, r.a, r.b);
            case Action.UNLINK -> {
                v.linkB = null;
                v.linkA = null;
                VigilService.dirty(p);
                yield true;
            }
            case Action.HORN -> VigilService.horn(p);
            case Action.BASH -> VigilCombat.bash(p, r.a);
            case Action.VOLLEY -> VigilCombat.volley(p);
            case Action.METEOR -> VigilCombat.meteor(p, r.a);
            case Action.TOW -> VigilJobs.tow(p, r.a, r.b);
            case Action.BIND_SOURCE -> VigilJobs.bind(p, false);
            case Action.BIND_DEST -> VigilJobs.bind(p, true);
            case Action.COURIER -> VigilJobs.courier(p, r.a);
            case Action.STOP_JOB -> {
                VigilService.cancelJobs(p);
                yield true;
            }
            case Action.WATCH -> {
                boolean v1 = v.watch = !v.watch;
                v.watchInterval = r.x == 6000 ? 6000 : (r.x == 12000 ? 12000 : 1200);
                v.nextWatch = VigilService.now(p) + (long)v.watchInterval;
                VigilService.dirty(p);
                yield true;
            }
            case Action.CHATTER -> {
                v.chatter = !v.chatter;
                LegacyService.chatterToggle(p, v.chatter);
                VigilService.dirty(p);
                yield true;
            }
            case Action.START -> VigilRuns.start(p, r.x, r.y == 1, r.ids);
            case Action.INPUT -> VigilRuns.input(p, r.x, r.y, r.nonce);
            case Action.PAUSE -> {
                if (VigilRuns.session(p) == null) {
                    yield false;
                }
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                yield true;
            }
            case Action.RESUME -> VigilRuns.resume(p);
            case Action.LEAVE -> VigilRuns.finish(p, false);
            case Action.CLAIM -> VigilRuns.finish(p, true);
            case Action.PING -> {
                if (VigilService.record(p, r.a) == null) {
                    yield false;
                }
                v.ping = r.a;
                VigilService.dirty(p);
                yield true;
            }
        };
    }

    public static void send(ServerPlayer p, boolean open, int page) {
        VigilState v = VigilService.state(p);
        CompoundTag full = VigilNetwork.view(p);
        CompoundTag patch = RoyalSync.delta(full, open ? null : v.lastView);
        v.lastView = full;
        if (open) {
            v.viewUntil = VigilService.now(p) + 100L;
        }
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(patch, open, Math.max(0, Math.min(11, page))));
    }

    public static CompoundTag view(ServerPlayer p) {
        VigilState v = VigilService.state(p);
        CompoundTag n = v.save();
        n.m_128356_("Time", VigilService.now(p));
        n.m_128379_("NearGrave", VigilGraves.ownedNear(p));
        n.m_128405_("Diamonds", VigilGraves.diamonds(p));
        n.m_128350_("HP", p.m_21223_());
        n.m_128350_("MaxHP", p.m_21233_());
        n.m_128379_("Protecting", VigilCrown.active(p));
        n.m_128365_("Lens", (Tag)v.lens.m_6426_());
        n.m_128365_("Job", (Tag)v.job.m_6426_());
        ListTag list = new ListTag();
        for (ShadowRecord r : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            CompoundTag row = r.vigil.save();
            row.m_128362_("Id", r.id);
            row.m_128359_("Name", VigilService.name(p, r));
            row.m_128359_("Type", r.type);
            row.m_128405_("Level", r.level());
            row.m_128359_("Order", r.order.name());
            row.m_128405_("Free", 5 - r.vigil.freeUsed);
            row.m_128379_("PaidRequired", VigilGraves.needsPayment(r));
            row.m_128379_("Pending", r.pendingRespawn());
            row.m_128356_("ReturnAt", r.respawnAt);
            Mob m = ShadowService.active(p.f_8924_, r);
            row.m_128379_("Loaded", m != null);
            row.m_128379_("Active", r.activeEntity != null);
            row.m_128350_("HP", m == null ? r.health : m.m_21223_());
            row.m_128350_("Max", m == null ? r.maxHealth : m.m_21233_());
            if (m != null) {
                row.m_128347_("X", m.m_20185_());
                row.m_128347_("Y", m.m_20186_());
                row.m_128347_("Z", m.m_20189_());
            }
            list.add((Object)row);
        }
        n.m_128365_("Roster", (Tag)list);
        AdventureData.Session s = VigilRuns.session(p);
        if (s != null) {
            if (!VigilRuns.valid(s)) {
                VigilRuns.finish(p, false);
            } else {
                CompoundTag run = s.vigil.m_6426_();
                run.m_128359_("Kind", s.kind.name());
                run.m_128405_("State", s.phase.ordinal());
                run.m_128356_("Now", s.phase == AdventureData.Phase.PAUSED ? run.m_128454_("PausedAt") : VigilService.now(p));
                run.m_128356_("Anchor", s.anchor.m_121878_());
                run.m_128359_("Dimension", s.dimension);
                n.m_128365_("Run", (Tag)run);
            }
        }
        return n;
    }

    public record Request(Action action, int x, int y, int nonce, UUID a, UUID b, List<UUID> ids) {
        public void encode(FriendlyByteBuf out) {
            out.m_130068_((Enum)this.action);
            out.writeInt(this.x);
            out.writeInt(this.y);
            out.writeInt(this.nonce);
            out.writeBoolean(this.a != null);
            if (this.a != null) {
                out.m_130077_(this.a);
            }
            out.writeBoolean(this.b != null);
            if (this.b != null) {
                out.m_130077_(this.b);
            }
            out.m_130130_(this.ids.size());
            this.ids.forEach(arg_0 -> ((FriendlyByteBuf)out).m_130077_(arg_0));
        }

        public static Request decode(FriendlyByteBuf in) {
            Action action = (Action)in.m_130066_(Action.class);
            int x = in.readInt();
            int y = in.readInt();
            int nonce = in.readInt();
            UUID a = in.readBoolean() ? in.m_130259_() : null;
            UUID b = in.readBoolean() ? in.m_130259_() : null;
            int count = in.m_130242_();
            if (count < 0 || count > 4) {
                throw new IllegalArgumentException("Invalid Vigil party");
            }
            ArrayList<UUID> ids = new ArrayList<UUID>();
            for (int i = 0; i < count; ++i) {
                ids.add(in.m_130259_());
            }
            return new Request(action, x, y, nonce, a, b, List.copyOf(ids));
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context c = ctx.get();
            c.enqueueWork(() -> {
                String key;
                ServerPlayer p = c.getSender();
                if (p == null) {
                    return;
                }
                long now = VigilService.now(p);
                String string = key = this.action == Action.VIEW ? "VigilViewAt" : "VigilActionAt";
                if (p.getPersistentData().m_128441_(key) && now - p.getPersistentData().m_128454_(key) >= 0L && now - p.getPersistentData().m_128454_(key) < (long)(this.action == Action.VIEW ? 5 : 2)) {
                    return;
                }
                p.getPersistentData().m_128356_(key, now);
                VigilState v = VigilService.state(p);
                v.viewUntil = now + 100L;
                if (this.action == Action.VIEW && this.y == 1) {
                    v.lastView = null;
                }
                if (!VigilNetwork.perform(p, this) && this.action != Action.VIEW) {
                    VigilText.tell(p, "unavailable", new Object[0]);
                }
                VigilNetwork.send(p, false, 0);
            });
            c.setPacketHandled(true);
        }
    }

    public record View(CompoundTag data, boolean open, int page) {
        public void encode(FriendlyByteBuf b) {
            b.m_130079_(this.data);
            b.writeBoolean(this.open);
            b.m_130130_(this.page);
        }

        public static View decode(FriendlyByteBuf b) {
            CompoundTag n = b.m_130260_();
            return new View(n == null ? new CompoundTag() : n, b.readBoolean(), b.m_130242_());
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientVigil.accept(this)));
            ctx.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        PAY,
        OFFERINGS,
        POST,
        LINK,
        UNLINK,
        HORN,
        BASH,
        VOLLEY,
        METEOR,
        TOW,
        BIND_SOURCE,
        BIND_DEST,
        COURIER,
        STOP_JOB,
        WATCH,
        CHATTER,
        START,
        INPUT,
        PAUSE,
        RESUME,
        LEAVE,
        CLAIM,
        PING;

    }
}

