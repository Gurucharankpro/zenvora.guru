/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package dev.shadowsovereign.network;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.warfront.WarfrontBuilds;
import dev.shadowsovereign.warfront.WarfrontPlans;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class WarfrontNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(13, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(14, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Request r) {
        channel.sendToServer((Object)r);
    }

    public static void request(Action a, int value, boolean flag, UUID id) {
        WarfrontNetwork.request(new Request(a, value, flag, id, List.of(), List.of()));
    }

    public static int perform(ServerPlayer p, Request r) {
        if (r.action != Action.VIEW && (!p.m_6084_() || p.m_5833_() || JourneyView.active(p))) {
            return 0;
        }
        return switch (r.action) {
            default -> throw new IncompatibleClassChangeError();
            case Action.VIEW -> 1;
            case Action.START -> {
                if (WarfrontService.start(p, r.value, r.flag, r.party)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PACT_START -> {
                if (WarfrontService.pactMission(p, r.value, r.flag, r.party)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CAST -> {
                if (r.companion != null && WarfrontBuilds.cast(p, r.companion)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.SET_ART -> {
                if (r.companion != null && r.value >= 0 && r.value < WarfrontState.Art.values().length && WarfrontBuilds.art(p, r.companion, WarfrontState.Art.values()[r.value])) {
                    yield 1;
                }
                yield 0;
            }
            case Action.SET_RUNE -> {
                if (r.companion != null && r.value >= 0 && r.value < WarfrontState.Rune.values().length && WarfrontBuilds.rune(p, r.companion, WarfrontState.Rune.values()[r.value])) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PLAN_SAVE -> {
                if (WarfrontPlans.save(p, r.value, r.steps)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PLAN_RUN -> {
                if (WarfrontPlans.start(p, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PLAN_STOP -> {
                WarfrontPlans.stop(p);
                yield 1;
            }
            case Action.MARKER -> {
                if (WarfrontPlans.arm(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PAUSE -> {
                if (WarfrontService.session(p) == null) {
                    yield 0;
                }
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                yield 1;
            }
            case Action.RESUME -> {
                if (WarfrontService.session(p) != null && AdventureWorld.resume(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.ABANDON -> {
                if (WarfrontService.abandon(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CLAIM -> {
                if (WarfrontService.claim(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PACT_CLAIM -> {
                if (WarfrontService.claimPact(p, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.NEXT -> WarfrontService.next(p, r.flag) ? 1 : 0;
        };
    }

    public static CompoundTag snapshot(ServerPlayer p, UUID selected) {
        WarfrontState w = WarfrontService.state(p);
        CompoundTag n = new CompoundTag();
        n.m_128356_("Time", WarfrontService.now(p));
        n.m_128359_("PlayerDimension", p.m_9236_().m_46472_().m_135782_().toString());
        n.m_128379_("Bound", w.bound);
        n.m_128379_("Peaceful", p.m_9236_().m_46791_() == Difficulty.PEACEFUL);
        n.m_128379_("Near", WarfrontService.near(p));
        n.m_128379_("Workshop", WarfrontService.workshop(p));
        n.m_128359_("Dimension", w.dimension);
        n.m_128356_("Beacon", w.beacon.m_121878_());
        n.m_128356_("Chapter", w.chapter);
        n.m_128356_("Trophies", w.trophies);
        n.m_128379_("WonOnce", w.wonOnce);
        n.m_128405_("Owned", w.owned);
        n.m_128405_("Contested", w.contested);
        n.m_128405_("Blueprints", w.blueprints);
        n.m_128405_("Defeated", w.defeated);
        n.m_128405_("PactReady", w.pactReady);
        n.m_128405_("PactClaimed", w.pactClaimed);
        n.m_128359_("Scope", ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).selectedSquad.label);
        if (selected != null) {
            n.m_128362_("Selected", selected);
        }
        ListTag rivals = new ListTag();
        for (int i = 0; i < 3; ++i) {
            CompoundTag e2 = new CompoundTag();
            e2.m_128359_("Name", w.rivalName(i));
            e2.m_128405_("Influence", w.influence[i]);
            e2.m_128359_("Adaptation", w.adaptations[i].name());
            rivals.add((Object)e2);
        }
        n.m_128365_("Rivals", (Tag)rivals);
        ListTag nodes = new ListTag();
        for (int i = 0; i < 6; ++i) {
            CompoundTag e3 = new CompoundTag();
            e3.m_128379_("Available", w.available(i));
            e3.m_128405_("Mission", w.mission(i));
            nodes.add((Object)e3);
        }
        n.m_128365_("Nodes", (Tag)nodes);
        ListTag roster = new ListTag();
        for (ShadowRecord shadowRecord : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.m_128362_("Id", shadowRecord.id);
            compoundTag.m_128359_("Name", LegacyService.name(p, shadowRecord));
            compoundTag.m_128359_("Type", shadowRecord.type);
            if (shadowRecord.activeEntity != null) {
                compoundTag.m_128362_("Entity", shadowRecord.activeEntity);
            }
            compoundTag.m_128405_("Bond", shadowRecord.bond);
            compoundTag.m_128405_("Level", shadowRecord.level());
            compoundTag.m_128359_("Tier", shadowRecord.tier.label);
            compoundTag.m_128379_("Active", shadowRecord.activeEntity != null);
            compoundTag.m_128379_("Eligible", WarfrontService.a(p, shadowRecord.id) != null && WarfrontService.a(p, shadowRecord.id).m_20280_((Entity)p) <= 1024.0);
            WarfrontState.Build b = w.builds.get(shadowRecord.id);
            compoundTag.m_128359_("Art", b == null ? WarfrontState.Art.NONE.name() : b.art.name());
            compoundTag.m_128359_("Rune", b == null ? WarfrontState.Rune.NONE.name() : b.rune.name());
            compoundTag.m_128405_("Crafted", b == null ? 0 : b.crafted);
            compoundTag.m_128356_("ArtAt", b == null ? 0L : b.artAt);
            compoundTag.m_128379_("Pactbound", b != null && b.pactbound);
            roster.add((Object)compoundTag);
        }
        n.m_128365_("Roster", (Tag)roster);
        ListTag plans = new ListTag();
        for (List<WarfrontState.Step> list : w.plans) {
            CompoundTag e5 = new CompoundTag();
            ListTag steps = new ListTag();
            list.forEach(s -> steps.add((Object)StringTag.m_129297_((String)s.name())));
            e5.m_128365_("Steps", (Tag)steps);
            plans.add((Object)e5);
        }
        n.m_128365_("Plans", (Tag)plans);
        if (w.marker != null) {
            n.m_128356_("Marker", w.marker.m_121878_());
        }
        n.m_128359_("MarkerDimension", w.markerDimension);
        if (w.execution != null) {
            n.m_128405_("Running", w.execution.slot);
            n.m_128405_("Step", w.execution.index);
        } else {
            n.m_128405_("Running", -1);
        }
        AdventureData.Session session = WarfrontService.session(p);
        if (session != null) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.m_128405_("Phase", session.phase.ordinal());
            compoundTag.m_128405_("Node", session.warfront.m_128451_("Node"));
            compoundTag.m_128405_("Mission", session.warfront.m_128451_("Mission"));
            compoundTag.m_128405_("Pact", session.warfront.m_128451_("Pact"));
            compoundTag.m_128379_("Hard", session.warfront.m_128471_("Hard"));
            compoundTag.m_128379_("PhaseTwo", session.warfront.m_128471_("PhaseTwo"));
            compoundTag.m_128379_("Released", session.warfront.m_128471_("Released"));
            compoundTag.m_128405_("Disabled", session.warfront.m_128451_("Disabled"));
            compoundTag.m_128405_("Enemies", (int)session.npcs.values().stream().filter(e -> e.role == AdventureData.Role.ENEMY).count());
            long time = session.phase == AdventureData.Phase.PAUSED ? session.warfront.m_128454_("PausedAt") : WarfrontService.now(p);
            compoundTag.m_128356_("Remaining", Math.max(0L, session.warfront.m_128454_("Deadline") - time));
            compoundTag.m_128365_("Points", (Tag)session.warfront.m_128437_("Points", 10).m_6426_());
            n.m_128365_("Run", (Tag)compoundTag);
        }
        n.m_128379_("OtherActivity", AdventureService.journal((ServerPlayer)p).session != null && session == null);
        ListTag listTag = new ListTag();
        for (WarfrontState.Build b : w.builds.values()) {
            LivingEntity target;
            Entity entity;
            if (b.vigilTarget == null || b.vigilUntil <= WarfrontService.now(p) || !b.targetDimension.equals(p.m_9236_().m_46472_().m_135782_().toString()) || !((entity = p.m_284548_().m_8791_(b.vigilTarget)) instanceof LivingEntity) || !(target = (LivingEntity)entity).m_6084_()) continue;
            CompoundTag e6 = new CompoundTag();
            e6.m_128347_("X", target.m_20185_());
            e6.m_128347_("Y", target.m_20186_() + (double)target.m_20206_() + 0.4);
            e6.m_128347_("Z", target.m_20189_());
            e6.m_128359_("Name", target.m_7755_().getString());
            e6.m_128356_("Until", b.vigilUntil);
            listTag.add((Object)e6);
        }
        n.m_128365_("Vigil", (Tag)listTag);
        return n;
    }

    public static void send(ServerPlayer p, boolean open, UUID id) {
        if (channel != null) {
            channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(WarfrontNetwork.snapshot(p, id), open));
        }
    }

    public record Request(Action action, int value, boolean flag, UUID companion, List<UUID> party, List<WarfrontState.Step> steps) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.writeInt(this.value);
            b.writeBoolean(this.flag);
            b.writeBoolean(this.companion != null);
            if (this.companion != null) {
                b.m_130077_(this.companion);
            }
            b.m_130130_(this.party.size());
            this.party.forEach(arg_0 -> ((FriendlyByteBuf)b).m_130077_(arg_0));
            b.m_130130_(this.steps.size());
            this.steps.forEach(arg_0 -> ((FriendlyByteBuf)b).m_130068_(arg_0));
        }

        public static Request decode(FriendlyByteBuf b) {
            Action action = (Action)b.m_130066_(Action.class);
            int value = b.readInt();
            boolean flag = b.readBoolean();
            UUID id = b.readBoolean() ? b.m_130259_() : null;
            int count = b.m_130242_();
            if (count < 0 || count > 3) {
                throw new IllegalArgumentException("Invalid Warfront party");
            }
            ArrayList<UUID> ids = new ArrayList<UUID>();
            for (int i = 0; i < count; ++i) {
                ids.add(b.m_130259_());
            }
            count = b.m_130242_();
            if (count < 0 || count > 4) {
                throw new IllegalArgumentException("Invalid battle plan");
            }
            ArrayList<WarfrontState.Step> steps = new ArrayList<WarfrontState.Step>();
            for (int i = 0; i < count; ++i) {
                steps.add((WarfrontState.Step)b.m_130066_(WarfrontState.Step.class));
            }
            return new Request(action, value, flag, id, List.copyOf(ids), List.copyOf(steps));
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> {
                int changed;
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                String key = this.action == Action.VIEW ? "WarfrontLastView" : "WarfrontLastAction";
                long now = WarfrontService.now(p);
                long last = p.getPersistentData().m_128454_(key);
                if (p.getPersistentData().m_128441_(key) && now >= last && now - last < (long)(this.action == Action.VIEW ? 5 : 2)) {
                    return;
                }
                p.getPersistentData().m_128356_(key, now);
                if (this.action == Action.VIEW) {
                    p.getPersistentData().m_128356_("WarfrontViewUntil", now + 100L);
                }
                if ((changed = WarfrontNetwork.perform(p, this)) == 0 && this.action != Action.VIEW) {
                    WarfrontService.tell(p, "Action unavailable. Check the Beacon, companion, cooldown and mission requirements.");
                }
                WarfrontNetwork.send(p, this.action == Action.VIEW && this.flag, this.companion);
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
            CompoundTag data = b.m_130260_();
            return new View(data == null ? new CompoundTag() : data, b.readBoolean());
        }

        public void handle(Supplier<NetworkEvent.Context> s) {
            s.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientWarfront.accept(this)));
            s.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        START,
        PACT_START,
        CAST,
        SET_ART,
        SET_RUNE,
        PLAN_SAVE,
        PLAN_RUN,
        PLAN_STOP,
        MARKER,
        PAUSE,
        RESUME,
        ABANDON,
        CLAIM,
        PACT_CLAIM,
        NEXT;

    }
}

