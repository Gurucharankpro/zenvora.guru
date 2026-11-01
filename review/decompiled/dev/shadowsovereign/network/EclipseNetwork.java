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
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
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
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.eclipse.EclipseBoard;
import dev.shadowsovereign.eclipse.EclipseCamp;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.eclipse.EclipseText;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.royal.RoyalSync;
import dev.shadowsovereign.warfront.WarfrontService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class EclipseNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(17, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(18, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action a, int value, int extra, int nonce, UUID id, List<UUID> ids) {
        channel.sendToServer((Object)new Request(a, value, extra, nonce, id, List.copyOf(ids)));
    }

    public static int perform(ServerPlayer p, Request r) {
        if (r.action != Action.VIEW && !EclipseService.usable(p)) {
            return 0;
        }
        if (r.ids.size() > 24 || new HashSet<UUID>(r.ids).size() != r.ids.size()) {
            return 0;
        }
        EclipseState e = EclipseService.state(p);
        return switch (r.action) {
            default -> throw new IncompatibleClassChangeError();
            case Action.VIEW -> 1;
            case Action.SELECT -> {
                if (EclipseService.record(p, r.companion) == null) {
                    yield 0;
                }
                e.selected = r.companion;
                EclipseService.dirty(p);
                yield 1;
            }
            case Action.CAST -> {
                if (r.value >= 0 && r.value < EclipseState.Power.values().length && EclipsePowers.cast(p, EclipseState.Power.values()[r.value])) {
                    yield 1;
                }
                yield 0;
            }
            case Action.ROLE -> {
                if (EclipseService.role(p, r.companion, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.TALENT -> {
                if (EclipseService.talent(p, r.companion, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.WARDROBE -> {
                if (EclipseService.wardrobe(p, r.companion, r.value, r.extra, r.nonce)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.BACKUP -> {
                if (EclipseService.backup(p, r.companion, r.ids.isEmpty() ? null : r.ids.get(0))) {
                    yield 1;
                }
                yield 0;
            }
            case Action.GROUP -> EclipseService.group(p, r.ids, r.value);
            case Action.GEAR_SAVE -> {
                if (EclipseService.saveGear(p, r.companion, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.GEAR_APPLY -> {
                if (EclipseService.applyGear(p, r.companion, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.START -> {
                if (EclipseRuns.start(p, r.value, r.extra == 1, r.ids)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PAUSE -> {
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                yield 1;
            }
            case Action.RESUME -> {
                if (EclipseRuns.resume(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.ABANDON -> {
                if (EclipseRuns.finish(p, false)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CLAIM -> {
                if (EclipseRuns.finish(p, true)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CARD -> {
                if (EclipseRuns.pick(p, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.INTERACT -> {
                if (EclipseRuns.interact(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.ACCUSE -> {
                if (EclipseRuns.accuse(p, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.BOARD -> {
                if (EclipseBoard.act(p, r.extra, r.value, r.nonce)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.VAULT -> {
                if (EclipseCamp.openVault(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.SUPPLY -> {
                if (EclipseCamp.supply(p, r.ids, false)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.DEPOSIT -> {
                if (EclipseCamp.supply(p, r.ids, true)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CRAFT -> {
                if (EclipseCamp.craft(p, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.BUY -> {
                if (EclipseCamp.buy(p, r.value)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.GATE -> EclipseCamp.gate(p, r.value) ? 1 : 0;
        };
    }

    public static void send(ServerPlayer p, boolean open) {
        EclipseNetwork.send(p, open, 0);
    }

    public static void send(ServerPlayer p, boolean open, int page) {
        RoyalState state = RoyalService.state(p);
        CompoundTag full = EclipseNetwork.view(p);
        CompoundTag payload = RoyalSync.delta(full, open ? null : state.lastEclipse);
        state.lastEclipse = full;
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(payload, open, page));
    }

    public static CompoundTag view(ServerPlayer p) {
        EclipseState e = EclipseService.state(p);
        CompoundTag n = new CompoundTag();
        n.m_128356_("Time", EclipseService.now(p));
        n.m_128350_("Energy", e.energy);
        n.m_128405_("Wins", e.wins);
        n.m_128405_("Marks", e.marks);
        n.m_128405_("BoardWins", e.boardWins);
        n.m_128405_("Palettes", e.palettes);
        if (e.selected != null) {
            n.m_128362_("Selected", e.selected);
        }
        n.m_128379_("Workshop", EclipseService.workshop(p));
        n.m_128379_("NearBeacon", WarfrontService.near(p));
        n.m_128356_("GateUntil", e.gateUntil);
        CompoundTag cds = new CompoundTag();
        e.ready.forEach((key, t) -> cds.m_128356_(key.name(), t.longValue()));
        n.m_128365_("Ready", (Tag)cds);
        ListTag roster = new ListTag();
        for (ShadowRecord r2 : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            CompoundTag row = e.builds.containsKey(r2.id) ? e.builds.get(r2.id).save() : new EclipseState.Build().save();
            for (int gear = 0; gear < 3; ++gear) {
                int saved = row.m_128441_("Gear" + gear);
                row.m_128473_("Gear" + gear);
                if (saved == 0) continue;
                row.m_128379_("Gear" + gear, true);
            }
            row.m_128362_("Id", r2.id);
            row.m_128359_("Name", LegacyService.name(p, r2));
            row.m_128359_("Type", r2.type);
            row.m_128359_("Squad", r2.battle.squad.name());
            row.m_128405_("Level", r2.level());
            row.m_128356_("Kills", r2.totalKills);
            row.m_128405_("Veteran", EclipseState.veteran(r2.totalKills));
            row.m_128405_("Points", EclipseState.points(r2.level()));
            Mob m = ShadowService.active(p.f_8924_, r2);
            if (m != null && m.m_9236_() == p.m_9236_()) {
                row.m_128362_("Entity", m.m_20148_());
                row.m_128379_("Active", true);
                row.m_128350_("HP", m.m_21223_());
                row.m_128350_("Max", m.m_21233_());
                row.m_128347_("X", m.m_20185_() - p.m_20185_());
                row.m_128347_("Z", m.m_20189_() - p.m_20189_());
            }
            roster.add((Object)row);
        }
        n.m_128365_("Roster", (Tag)roster);
        AdventureData.Session run = EclipseRuns.session(p);
        if (run != null && !EclipseRuns.valid(run)) {
            EclipseRuns.fail(p, "invalid_run");
            run = null;
        }
        if (run != null) {
            CompoundTag data = run.eclipse.m_6426_();
            data.m_128473_("Seed");
            data.m_128473_("Suspect");
            data.m_128473_("Power");
            data.m_128359_("Kind", run.kind.name());
            data.m_128359_("Dimension", run.dimension);
            int crystals = 0;
            for (int i = 0; i < 3; ++i) {
                if (!run.eclipse.m_128403_("Crystal" + i) || !run.npcs.containsKey(run.eclipse.m_128342_("Crystal" + i))) continue;
                crystals |= 1 << i;
            }
            data.m_128405_("Crystals", crystals);
            data.m_128405_("Phase", run.phase.ordinal());
            data.m_128405_("Enemies", run.npcs.size());
            data.m_128356_("Remaining", Math.max(0L, run.eclipse.m_128454_("Deadline") - (run.phase == AdventureData.Phase.PAUSED ? run.eclipse.m_128454_("PausedAt") : EclipseService.now(p))));
            data.m_128362_("Id", run.id);
            data.m_128356_("Anchor", run.anchor.m_121878_());
            n.m_128365_("Run", (Tag)data);
        }
        ListTag reports = new ListTag();
        e.reports.forEach(r -> reports.add((Object)r.m_6426_()));
        n.m_128365_("Reports", (Tag)reports);
        CompoundTag camp = new CompoundTag();
        for (EclipseState.Station k : EclipseState.Station.values()) {
            camp.m_128379_(k.name(), EclipseCamp.near(p, k));
        }
        n.m_128365_("Camp", (Tag)camp);
        n.m_128385_("Offers", EclipseCamp.offers(p));
        n.m_128385_("Bought", e.merchantCycle == (long)e.wins ? e.purchases : new int[6]);
        ListTag gates = new ListTag();
        EclipseCamp.gates(p).forEach(g -> gates.add((Object)g.save()));
        n.m_128365_("Gates", (Tag)gates);
        ListTag radar = new ListTag();
        for (LivingEntity m : EclipsePowers.targets(p, p.m_20182_(), 24.0, 12)) {
            CompoundTag tag = new CompoundTag();
            tag.m_128362_("Id", m.m_20148_());
            tag.m_128347_("X", m.m_20185_() - p.m_20185_());
            tag.m_128347_("Z", m.m_20189_() - p.m_20189_());
            radar.add((Object)tag);
        }
        n.m_128365_("Radar", (Tag)radar);
        ListTag marks = new ListTag();
        for (Map.Entry<UUID, Long> entry : e.marksUntil.entrySet()) {
            Entity entity;
            if (entry.getValue() <= EclipseService.now(p) || !((entity = p.m_284548_().m_8791_(entry.getKey())) instanceof LivingEntity)) continue;
            LivingEntity m = (LivingEntity)entity;
            CompoundTag t2 = new CompoundTag();
            t2.m_128359_("Name", m.m_7755_().getString());
            t2.m_128356_("Until", entry.getValue().longValue());
            t2.m_128347_("X", m.m_20185_());
            t2.m_128347_("Y", m.m_20186_() + (double)m.m_20206_());
            t2.m_128347_("Z", m.m_20189_());
            marks.add((Object)t2);
        }
        n.m_128365_("Scout", (Tag)marks);
        return n;
    }

    public record Request(Action action, int value, int extra, int nonce, UUID companion, List<UUID> ids) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.writeInt(this.value);
            b.writeInt(this.extra);
            b.writeInt(this.nonce);
            b.writeBoolean(this.companion != null);
            if (this.companion != null) {
                b.m_130077_(this.companion);
            }
            b.m_130130_(this.ids.size());
            this.ids.forEach(arg_0 -> ((FriendlyByteBuf)b).m_130077_(arg_0));
        }

        public static Request decode(FriendlyByteBuf b) {
            Action a = (Action)b.m_130066_(Action.class);
            int v = b.readInt();
            int x = b.readInt();
            int n = b.readInt();
            UUID id = b.readBoolean() ? b.m_130259_() : null;
            int count = b.m_130242_();
            if (count < 0 || count > 24) {
                throw new IllegalArgumentException("Invalid Eclipse selection");
            }
            ArrayList<UUID> list = new ArrayList<UUID>();
            for (int i = 0; i < count; ++i) {
                list.add(b.m_130259_());
            }
            return new Request(a, v, x, n, id, List.copyOf(list));
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context c = supplier.get();
            c.enqueueWork(() -> {
                ServerPlayer p = c.getSender();
                if (p == null) {
                    return;
                }
                String key = this.action == Action.VIEW ? "EclipseViewAt" : "EclipseActionAt";
                long now = EclipseService.now(p);
                if (p.getPersistentData().m_128441_(key) && now >= p.getPersistentData().m_128454_(key) && now - p.getPersistentData().m_128454_(key) < (long)(this.action == Action.VIEW ? 5 : 2)) {
                    return;
                }
                p.getPersistentData().m_128356_(key, now);
                p.getPersistentData().m_128356_("EclipseViewUntil", now + 100L);
                if (this.action == Action.VIEW) {
                    RoyalService.state((ServerPlayer)p).lastEclipse = null;
                }
                if (EclipseNetwork.perform(p, this) == 0 && this.action != Action.VIEW) {
                    EclipseText.tell(p, "unavailable", new Object[0]);
                }
                EclipseNetwork.send(p, false);
                if (this.action == Action.GROUP || this.action == Action.GEAR_APPLY) {
                    ShadowNetwork.sendArmy(p, false);
                }
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

        public void handle(Supplier<NetworkEvent.Context> c) {
            c.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientEclipse.accept(this)));
            c.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        CAST,
        ROLE,
        TALENT,
        WARDROBE,
        BACKUP,
        GROUP,
        GEAR_SAVE,
        GEAR_APPLY,
        START,
        PAUSE,
        RESUME,
        ABANDON,
        CLAIM,
        CARD,
        INTERACT,
        ACCUSE,
        BOARD,
        VAULT,
        SUPPLY,
        DEPOSIT,
        CRAFT,
        BUY,
        GATE,
        SELECT;

    }
}

