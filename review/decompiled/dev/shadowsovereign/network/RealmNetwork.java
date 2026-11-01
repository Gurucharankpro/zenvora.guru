/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
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

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.client.ClientRealm;
import dev.shadowsovereign.expanse.ExpanseWorld;
import dev.shadowsovereign.gloom.GloomRealm;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.realm.RealmState;
import dev.shadowsovereign.realm.RealmTerrain;
import dev.shadowsovereign.realm.WildBosses;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class RealmNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(27, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(28, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action a, int v, int o, UUID id) {
        channel.sendToServer((Object)new Request(a, v, o, id));
    }

    public static boolean perform(ServerPlayer p, Request q) {
        if (q.action != Action.VIEW && (!p.m_6084_() || p.m_5833_())) {
            return false;
        }
        return switch (q.action) {
            default -> throw new IncompatibleClassChangeError();
            case Action.ASTRAL_TRAVEL -> ExpanseWorld.travel(p, q.value);
            case Action.VIEW, Action.OPEN -> true;
            case Action.GENERATE -> RealmTerrain.request(p);
            case Action.PIN -> LivingRealm.pin(p, q.value);
            case Action.DISCOVER -> LivingRealm.discover(p, q.value);
            case Action.TRAVEL -> LivingRealm.travel(p, q.value);
            case Action.START -> LivingRealm.start(p, q.value, q.other);
            case Action.RESUME -> LivingRealm.resume(p);
            case Action.CLAIM -> LivingRealm.finish(p, true);
            case Action.CANCEL -> LivingRealm.finish(p, false);
            case Action.BUDDY -> LivingRealm.buddy(p, q.selected);
            case Action.CONTRACT -> LivingRealm.contract(p, q.value);
            case Action.QUICK -> LivingRealm.quick(p);
            case Action.REDUCED -> {
                LivingRealm.state((ServerPlayer)p).reduced = !LivingRealm.state((ServerPlayer)p).reduced;
                LivingRealm.dirty(p);
                yield true;
            }
            case Action.CLAIM_SOULS -> {
                if (WildBosses.claimPending(p) > 0) {
                    yield true;
                }
                yield false;
            }
            case Action.TRADE -> LivingRealm.trade(p, q.value);
        };
    }

    public static void open(ServerPlayer p, int page) {
        LivingRealm.state((ServerPlayer)p).viewUntil = GuideService.now(p) + 100L;
        RealmNetwork.send(p, true, page);
    }

    public static CompoundTag view(ServerPlayer p) {
        return RealmNetwork.view(p, true);
    }

    private static CompoundTag view(ServerPlayer p, boolean details) {
        RealmState s = LivingRealm.state(p);
        CompoundTag n = new CompoundTag();
        n.m_128379_("Ready", s.ready);
        n.m_128379_("GloomReady", GloomRealm.state(p).m_128471_("Ready"));
        n.m_128405_("GloomCursor", GloomRealm.state(p).m_128451_("Cursor"));
        n.m_128365_("AstralWaypoints", (Tag)s.sites.m_128437_("AstralWaypoints", 10).m_6426_());
        n.m_128379_("Requested", s.requested);
        n.m_128405_("Cursor", s.cursor);
        n.m_128405_("Waystones", s.waystones);
        n.m_128405_("Projects", s.projects);
        n.m_128405_("Cleared", s.cleared);
        n.m_128405_("Finale", s.finale);
        n.m_128405_("Pinned", s.pinned);
        n.m_128405_("SurveyedCount", s.surveyed.cardinality());
        n.m_128388_("Surveyed", s.surveyed.toLongArray());
        n.m_128405_("Contract", s.contract);
        n.m_128405_("ContractProgress", s.contractProgress);
        n.m_128379_("Reduced", s.reduced);
        n.m_128379_("Realm", OdysseyRealm.inside(p));
        n.m_128405_("Region", OdysseyRealm.inside(p) ? RealmTerrain.region(p, p.m_20183_()) : -1);
        n.m_128356_("Time", GuideService.now(p));
        n.m_128356_("Home", OdysseyRealm.home(p).m_121878_());
        n.m_128405_("X", p.m_146903_());
        n.m_128405_("Z", p.m_146907_());
        n.m_128405_("Pending", s.pending.size());
        n.m_128405_("EscortsDone", s.sites.m_128451_("EscortsDone"));
        ListTag b = new ListTag();
        if (details) {
            s.bestiary.forEach((id, entry) -> {
                CompoundTag e = entry.m_6426_();
                e.m_128359_("Type", id);
                b.add((Object)e);
            });
        }
        n.m_128365_("Bestiary", (Tag)b);
        ListTag roster = new ListTag();
        if (details) {
            for (ShadowRecord r : GuideService.army((ServerPlayer)p).shadows.values()) {
                CompoundTag row = new CompoundTag();
                row.m_128362_("Id", r.id);
                row.m_128359_("Name", r.name);
                roster.add((Object)row);
            }
        }
        n.m_128365_("Roster", (Tag)roster);
        AdventureData.Session run = LivingRealm.session(p);
        if (run != null) {
            CompoundTag a = new CompoundTag();
            a.m_128405_("Type", run.journey.m_128451_("Type"));
            a.m_128405_("Index", run.journey.m_128451_("Index"));
            a.m_128405_("Phase", run.phase.ordinal());
            a.m_128405_("Hold", run.journey.m_128451_("Hold"));
            a.m_128405_("Switches", run.journey.m_128451_("Switches"));
            a.m_128405_("Enemies", (int)run.npcs.values().stream().filter(e -> e.role == AdventureData.Role.ENEMY).count());
            a.m_128356_("Anchor", run.anchor.m_121878_());
            n.m_128365_("Run", (Tag)a);
        }
        return n;
    }

    public static void send(ServerPlayer p, boolean open, int page) {
        if (!open && LivingRealm.state((ServerPlayer)p).viewUntil < GuideService.now(p) && !OdysseyRealm.inside(p)) {
            return;
        }
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(RealmNetwork.view(p, open || LivingRealm.state((ServerPlayer)p).viewUntil >= GuideService.now(p)), open, Math.max(0, Math.min(6, page))));
    }

    public record Request(Action action, int value, int other, UUID selected) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.writeInt(this.value);
            b.writeInt(this.other);
            b.writeBoolean(this.selected != null);
            if (this.selected != null) {
                b.m_130077_(this.selected);
            }
        }

        public static Request decode(FriendlyByteBuf b) {
            return new Request((Action)b.m_130066_(Action.class), b.readInt(), b.readInt(), b.readBoolean() ? b.m_130259_() : null);
        }

        public void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                long now = GuideService.now(p);
                String key = this.action == Action.VIEW ? "RealmViewAt" : "RealmActionAt";
                CompoundTag d = p.getPersistentData();
                if (d.m_128441_(key) && now - d.m_128454_(key) < (long)(this.action == Action.VIEW ? 10 : 3)) {
                    return;
                }
                d.m_128356_(key, now);
                LivingRealm.state((ServerPlayer)p).viewUntil = now + 100L;
                if (!RealmNetwork.perform(p, this) && this.action != Action.VIEW) {
                    LivingRealm.tell(p, "unavailable", new Object[0]);
                }
                RealmNetwork.send(p, this.action == Action.OPEN, Math.max(0, Math.min(6, this.value)));
            });
            ctx.setPacketHandled(true);
        }
    }

    public record View(CompoundTag data, boolean open, int page) {
        public void encode(FriendlyByteBuf b) {
            b.m_130079_(this.data);
            b.writeBoolean(this.open);
            b.m_130130_(this.page);
        }

        public static View decode(FriendlyByteBuf b) {
            return new View(b.m_130260_(), b.readBoolean(), b.m_130242_());
        }

        public void handle(Supplier<NetworkEvent.Context> c) {
            c.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientRealm.accept(this)));
            c.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        OPEN,
        GENERATE,
        PIN,
        DISCOVER,
        TRAVEL,
        START,
        RESUME,
        CLAIM,
        CANCEL,
        BUDDY,
        CONTRACT,
        QUICK,
        REDUCED,
        CLAIM_SOULS,
        TRADE,
        ASTRAL_TRAVEL;

    }
}

