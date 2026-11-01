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
import dev.shadowsovereign.client.ClientAdventure;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.network.ShadowNetwork;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class AdventureNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(5, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(6, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Request r) {
        channel.sendToServer((Object)r);
    }

    public static void open(ServerPlayer p) {
        ShadowNetwork.sendArmy(p, false);
        AdventureNetwork.send(p, true, null, 0);
    }

    public static void send(ServerPlayer p, boolean open, UUID companion, int page) {
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(AdventureNetwork.snapshot(p, companion, page), open));
    }

    public static int perform(ServerPlayer p, Request r) {
        if (JourneyView.active(p) && r.action != Action.VIEW) {
            return 0;
        }
        if (r.page < 0 || r.page > 31 || r.members.size() > 3 || r.companion != null && !ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.containsKey(r.companion) || !p.m_6084_() && r.action != Action.VIEW) {
            return 0;
        }
        boolean success = switch (r.action) {
            default -> throw new IncompatibleClassChangeError();
            case Action.VIEW -> true;
            case Action.DISPATCH -> AdventureService.dispatch(p, r.value, r.members);
            case Action.CLAIM_EXPEDITION -> AdventureService.claimExpedition(p, r.target);
            case Action.CANCEL_EXPEDITION -> AdventureService.cancelExpedition(p, r.target);
            case Action.ACCEPT_CONTRACT -> AdventureService.acceptContract(p, r.value);
            case Action.CLAIM_CONTRACT -> AdventureService.claimContract(p, r.target);
            case Action.ABANDON_CONTRACT -> AdventureService.abandonContract(p, r.target);
            case Action.TRIAL -> AdventureWorld.start(p, AdventureData.Kind.TRIAL, r.value);
            case Action.ESCORT -> AdventureWorld.start(p, AdventureData.Kind.ESCORT, 0);
            case Action.RESUME -> AdventureWorld.resume(p);
            case Action.PAUSE -> {
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                yield true;
            }
            case Action.CANCEL_PHYSICAL -> AdventureWorld.cancel(p);
            case Action.CLAIM_PHYSICAL -> AdventureWorld.claim(p);
            case Action.HUNT -> AdventureService.startHunt(p);
            case Action.CLAIM_CACHE -> AdventureService.claimCache(p, r.target);
            case Action.CLAIM_HUNT -> AdventureService.claimHunt(p);
            case Action.NEMESIS_KIN -> AdventureService.kin(p);
            case Action.CLAIM_NEMESIS -> AdventureService.claimNemesis(p);
            case Action.DISMISS_NEMESIS -> AdventureService.dismissNemesis(p);
            case Action.BOND -> AdventureService.startBond(p, r.companion);
            case Action.RELIC -> AdventureService.equipRelic(p, r.companion, r.value);
            case Action.TRAIL -> AdventureService.equipTrail(p, r.companion, r.value);
            case Action.TITLE -> AdventureService.equipTitle(p, r.companion, r.value);
        };
        return success ? 1 : 0;
    }

    public static CompoundTag snapshot(ServerPlayer p, UUID selected, int page) {
        AdventureData.Journal j = AdventureService.journal(p);
        CompoundTag root = new CompoundTag();
        long time = AdventureService.now(p.f_8924_);
        root.m_128356_("Time", time);
        root.m_128356_("Completions", j.completions);
        root.m_128379_("Bound", j.bound);
        root.m_128359_("Dimension", j.dimension);
        root.m_128356_("Sanctuary", j.sanctuary.m_121878_());
        root.m_128379_("Near", AdventureService.nearHub(p));
        root.m_128405_("Discoveries", j.discoveries.size());
        ListTag types = new ListTag();
        j.discoveries.stream().sorted().skip((long)Math.max(0, Math.min(31, page)) * 256L).limit(256L).forEach(s -> types.add((Object)StringTag.m_129297_((String)s)));
        root.m_128365_("Types", (Tag)types);
        root.m_128405_("Page", Math.max(0, Math.min(31, page)));
        ListTag titles = new ListTag();
        j.titles.forEach(t -> titles.add((Object)StringTag.m_129297_((String)t.name())));
        root.m_128365_("Titles", (Tag)titles);
        ListTag trails = new ListTag();
        j.trails.forEach(t -> trails.add((Object)StringTag.m_129297_((String)t.name())));
        root.m_128365_("Trails", (Tag)trails);
        ListTag roster = new ListTag();
        for (ShadowRecord shadowRecord : ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values()) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.m_128362_("Id", shadowRecord.id);
            compoundTag.m_128359_("Name", shadowRecord.name);
            compoundTag.m_128359_("Type", shadowRecord.type);
            compoundTag.m_128405_("Level", shadowRecord.level());
            compoundTag.m_128356_("Kills", shadowRecord.totalKills);
            compoundTag.m_128379_("Active", shadowRecord.activeEntity != null);
            compoundTag.m_128379_("Pending", shadowRecord.pendingRespawn());
            compoundTag.m_128379_("Busy", AdventureService.busy(p.f_8924_, p.m_20148_(), shadowRecord.id));
            AdventureData.Lore lore = j.lore.get(shadowRecord.id);
            compoundTag.m_128405_("Relic", lore == null ? 0 : lore.relic.ordinal());
            compoundTag.m_128405_("Trail", lore == null ? 0 : lore.trail.ordinal());
            compoundTag.m_128405_("Title", lore == null ? 0 : lore.title.ordinal());
            compoundTag.m_128405_("Stage", lore == null ? 0 : lore.stage);
            compoundTag.m_128405_("Biomes", lore == null ? 0 : lore.biomes.size());
            compoundTag.m_128356_("BondKills", lore == null ? 0L : Math.max(0L, shadowRecord.totalKills - lore.killStart));
            roster.add((Object)compoundTag);
        }
        root.m_128365_("Roster", (Tag)roster);
        if (selected != null && ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.containsKey(selected)) {
            root.m_128362_("Selected", selected);
        }
        if (j.bond != null) {
            root.m_128362_("Bond", j.bond);
        }
        ListTag es = new ListTag();
        for (AdventureData.Expedition expedition : j.expeditions) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.m_128362_("Id", expedition.id);
            compoundTag.m_128405_("Tier", expedition.tier);
            compoundTag.m_128356_("End", expedition.end);
            compoundTag.m_128379_("Ready", expedition.ready || time >= expedition.end);
            ListTag members = new ListTag();
            expedition.members.forEach(id -> {
                CompoundTag a = new CompoundTag();
                a.m_128362_("Id", id);
                members.add((Object)a);
            });
            compoundTag.m_128365_("Members", (Tag)members);
            es.add((Object)compoundTag);
        }
        root.m_128365_("Expeditions", (Tag)es);
        ListTag listTag = new ListTag();
        for (AdventureData.Contract contract : j.contracts) {
            CompoundTag n3 = new CompoundTag();
            n3.m_128362_("Id", contract.id);
            n3.m_128405_("Kind", contract.kind.ordinal());
            n3.m_128359_("Target", contract.target);
            n3.m_128405_("Progress", contract.progress);
            n3.m_128405_("Goal", contract.goal());
            listTag.add((Object)n3);
        }
        root.m_128365_("Contracts", (Tag)listTag);
        root.m_128359_("OfferedTarget", AdventureService.offeredTarget(p));
        ListTag listTag2 = new ListTag();
        for (AdventureData.ContractKind k : AdventureData.ContractKind.values()) {
            CompoundTag n4 = new CompoundTag();
            n4.m_128405_("Kind", k.ordinal());
            n4.m_128379_("Available", j.contracts.stream().noneMatch(c -> c.kind == k) && !j.claimedOffers.contains(time / 24000L + ":" + k.name()));
            listTag2.add((Object)n4);
        }
        root.m_128365_("Offers", (Tag)listTag2);
        if (j.nemesis != null) {
            AdventureData.Nemesis nemesis = j.nemesis;
            CompoundTag a2 = new CompoundTag();
            a2.m_128362_("Id", nemesis.id);
            a2.m_128359_("Type", nemesis.type);
            a2.m_128359_("Dimension", nemesis.dimension);
            a2.m_128356_("Pos", nemesis.last.m_121878_());
            a2.m_128379_("Kin", nemesis.kin);
            a2.m_128379_("Ready", nemesis.ready);
            a2.m_128405_("Progress", nemesis.progress);
            root.m_128365_("Nemesis", (Tag)a2);
        }
        if (j.session != null) {
            AdventureData.Session session = j.session;
            CompoundTag n5 = new CompoundTag();
            n5.m_128362_("Id", session.id);
            n5.m_128405_("Kind", session.kind.ordinal());
            n5.m_128405_("Phase", session.phase.ordinal());
            n5.m_128405_("Tier", session.tier);
            n5.m_128405_("Wave", session.wave);
            n5.m_128405_("MaxWaves", session.maxWaves());
            n5.m_128356_("Anchor", session.anchor.m_121878_());
            n5.m_128359_("Dimension", session.dimension);
            n5.m_128405_("Enemies", (int)session.npcs.values().stream().filter(a -> a.role == AdventureData.Role.ENEMY).count());
            session.npcs.values().stream().filter(a -> a.role == AdventureData.Role.ESCORT).findFirst().ifPresent(a -> {
                n5.m_128356_("EscortPos", a.pos.m_121878_());
                n5.m_128350_("EscortHealth", a.snapshot.m_128457_("Health"));
            });
            n5.m_128379_("WardenUsed", session.wardenUsed);
            root.m_128365_("Session", (Tag)n5);
        }
        root.m_128356_("CacheCycle", j.cacheCycle);
        root.m_128359_("CacheDimension", j.cacheDimension);
        root.m_128379_("HuntClaimed", j.huntClaimed);
        ListTag listTag3 = new ListTag();
        for (AdventureData.Cache c3 : j.caches) {
            CompoundTag n6 = new CompoundTag();
            n6.m_128362_("Id", c3.id);
            if (c3.claimed) {
                n6.m_128356_("Pos", c3.pos.m_121878_());
            }
            n6.m_128379_("Claimed", c3.claimed);
            listTag3.add((Object)n6);
        }
        root.m_128365_("Caches", (Tag)listTag3);
        return root;
    }

    public record Request(Action action, UUID target, int value, List<UUID> members, UUID companion, int page) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.writeBoolean(this.target != null);
            if (this.target != null) {
                b.m_130077_(this.target);
            }
            b.m_130130_(this.value);
            b.m_130130_(this.members.size());
            this.members.forEach(arg_0 -> ((FriendlyByteBuf)b).m_130077_(arg_0));
            b.writeBoolean(this.companion != null);
            if (this.companion != null) {
                b.m_130077_(this.companion);
            }
            b.m_130130_(this.page);
        }

        public static Request decode(FriendlyByteBuf b) {
            Action action = (Action)b.m_130066_(Action.class);
            UUID target = b.readBoolean() ? b.m_130259_() : null;
            int value = b.m_130242_();
            int count = b.m_130242_();
            if (count < 0 || count > 3) {
                throw new IllegalArgumentException("Expedition party too large");
            }
            ArrayList<UUID> members = new ArrayList<UUID>();
            for (int i = 0; i < count; ++i) {
                members.add(b.m_130259_());
            }
            UUID companion = b.readBoolean() ? b.m_130259_() : null;
            int page = b.m_130242_();
            if (page < 0 || page > 31) {
                throw new IllegalArgumentException("Invalid bestiary page");
            }
            return new Request(action, target, value, List.copyOf(members), companion, page);
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            NetworkEvent.Context ctx = context.get();
            ctx.enqueueWork(() -> {
                int delay;
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                String key = this.action == Action.VIEW ? "SoulboundAdventureViewAt" : "SoulboundAdventureActionAt";
                long now = AdventureService.now(p.f_8924_);
                long previous = p.getPersistentData().m_128454_(key);
                int n = delay = this.action == Action.VIEW ? 10 : 2;
                if (p.getPersistentData().m_128441_(key) && now >= previous && now - previous < (long)delay) {
                    return;
                }
                p.getPersistentData().m_128356_(key, now);
                AdventureNetwork.perform(p, this);
                AdventureNetwork.send(p, false, this.companion, this.page);
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
                throw new IllegalArgumentException("Missing journal view");
            }
            return new View(n, b.readBoolean());
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientAdventure.accept(this)));
            context.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        DISPATCH,
        CLAIM_EXPEDITION,
        CANCEL_EXPEDITION,
        ACCEPT_CONTRACT,
        CLAIM_CONTRACT,
        ABANDON_CONTRACT,
        TRIAL,
        ESCORT,
        RESUME,
        PAUSE,
        CANCEL_PHYSICAL,
        CLAIM_PHYSICAL,
        HUNT,
        CLAIM_CACHE,
        CLAIM_HUNT,
        NEMESIS_KIN,
        CLAIM_NEMESIS,
        DISMISS_NEMESIS,
        BOND,
        RELIC,
        TRAIL,
        TITLE;

    }
}

