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
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.network;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.network.EclipseNetwork;
import dev.shadowsovereign.royal.RoyalBagMenu;
import dev.shadowsovereign.royal.RoyalContent;
import dev.shadowsovereign.royal.RoyalRuns;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.royal.RoyalSync;
import dev.shadowsovereign.royal.RoyalText;
import dev.shadowsovereign.soulforge.CompanionService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

public final class RoyalNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(19, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(20, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action a, int x, int y, int z, String text, List<UUID> ids) {
        channel.sendToServer((Object)new Request(a, x, y, z, text, List.copyOf(ids)));
    }

    public static int perform(ServerPlayer p, Request r) {
        if (r.action != Action.VIEW && !EclipseService.usable(p) || r.ids.size() > 24 || new HashSet<UUID>(r.ids).size() != r.ids.size()) {
            return 0;
        }
        RoyalState state = RoyalService.state(p);
        return switch (r.action) {
            default -> throw new IncompatibleClassChangeError();
            case Action.VIEW -> 1;
            case Action.SELECT_TEAM -> {
                if (r.first < 0 || r.first >= 5) {
                    yield 0;
                }
                state.selectedTeam = r.first;
                RoyalService.dirty(p);
                yield 1;
            }
            case Action.TEAM_SAVE -> {
                if (RoyalService.teamEdit(p, r.first, r.ids, r.text, r.second)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.DEPLOY -> RoyalService.deploy(p, r.first);
            case Action.RECALL -> RoyalService.recall(p, r.first);
            case Action.UPGRADE -> {
                if (RoyalService.upgrade(p, r.first, r.second)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.POLICY -> {
                if (RoyalService.policy(p, r.first, r.second, r.third)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.SUPPLY -> {
                if (RoyalService.supply(p, r.first)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.BAGS -> {
                if (RoyalBagMenu.open(p, r.first, r.second)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.WANTED -> {
                if (RoyalService.wanted(p, r.text)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.RALLY -> {
                if (RoyalService.rally(p, r.first)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.MUTATOR -> {
                if (r.first < -1 || r.first > 5) {
                    yield 0;
                }
                state.mutator = r.first;
                RoyalService.dirty(p);
                yield 1;
            }
            case Action.START -> {
                if (RoyalRuns.start(p, r.first, r.second == 1, r.ids)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PUZZLE -> {
                if (RoyalRuns.puzzle(p, r.first, r.second)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.PAUSE -> {
                if (RoyalRuns.session(p) == null) {
                    yield 0;
                }
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                yield 1;
            }
            case Action.RESUME -> {
                if (RoyalRuns.resume(p)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.ABANDON -> {
                if (RoyalRuns.finish(p, false)) {
                    yield 1;
                }
                yield 0;
            }
            case Action.CLAIM -> RoyalRuns.finish(p, true) ? 1 : 0;
        };
    }

    public static void send(ServerPlayer p, boolean open, int page) {
        RoyalState state = RoyalService.state(p);
        CompoundTag full = RoyalNetwork.view(p);
        CompoundTag payload = RoyalSync.delta(full, open ? null : state.lastRoyal);
        state.lastRoyal = full;
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(payload, open, Math.max(0, Math.min(13, page))));
    }

    public static CompoundTag view(ServerPlayer p) {
        RoyalState e = RoyalService.state(p);
        CompoundTag n = new CompoundTag();
        n.m_128356_("Time", RoyalService.now(p));
        n.m_128379_("Desk", RoyalContent.nearDesk(p));
        n.m_128405_("Chance", RoyalService.chance(p));
        AdventureData.Session other = AdventureService.journal((ServerPlayer)p).session;
        if (other != null && !RoyalRuns.is(other.kind)) {
            n.m_128359_("OtherKind", other.kind.name());
        }
        n.m_128405_("SelectedTeam", e.selectedTeam);
        n.m_128405_("Emblems", e.emblems);
        n.m_128405_("Mutator", e.mutator);
        n.m_128405_("PuzzleBest", e.puzzleBest);
        n.m_128356_("CourseBest", e.courseBest);
        n.m_128356_("CourseHardBest", e.courseHardBest);
        CompoundTag levels = new CompoundTag();
        for (RoyalState.Skill s : RoyalState.Skill.values()) {
            levels.m_128405_(s.name(), e.level(s));
        }
        n.m_128365_("Levels", (Tag)levels);
        ListTag teams = new ListTag();
        for (RoyalState.Team t : e.teams) {
            teams.add((Object)t.save());
        }
        n.m_128365_("Teams", (Tag)teams);
        CompoundTag resources = new CompoundTag();
        for (Item item : List.of(Items.f_42415_, Items.f_42616_, Items.f_42534_, Items.f_151049_, Items.f_42417_, Items.f_220224_, Items.f_42418_, Items.f_42686_)) {
            resources.m_128405_(ForgeRegistries.ITEMS.getKey((Object)item).m_135815_(), RoyalService.count(p, item));
        }
        n.m_128365_("Resources", (Tag)resources);
        ListTag notices = new ListTag();
        e.notices.forEach(v -> notices.add((Object)v.m_6426_()));
        n.m_128365_("Notices", (Tag)notices);
        ShadowRecord selected = EclipseService.record(p, EclipseService.state((ServerPlayer)p).selected);
        if (selected != null) {
            selected.inventory.initialize(selected.snapshot);
            Mob mob = ShadowService.active(p.f_8924_, selected);
            if (mob != null) {
                CompanionService.captureEquipment(mob, selected);
            }
            ListTag equipment = new ListTag();
            for (int slot = 0; slot < 6; ++slot) {
                equipment.add((Object)((ItemStack)selected.inventory.items.get(slot)).m_41739_(new CompoundTag()));
            }
            n.m_128365_("Equipment", (Tag)equipment);
        }
        ListTag hunts = new ListTag();
        LinkedHashSet<String> types = new LinkedHashSet<String>(AdventureService.journal((ServerPlayer)p).discoveries);
        types.addAll(e.hunts.keySet());
        ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values().forEach(r -> types.add(r.type));
        for (String type : types.stream().limit(4096L).toList()) {
            CompoundTag h = new CompoundTag();
            h.m_128359_("Type", type);
            long[] stats = e.hunts.getOrDefault(type, new long[2]);
            h.m_128356_("Attempts", stats[0]);
            h.m_128356_("Successes", stats[1]);
            h.m_128379_("Wanted", e.wanted.contains(type));
            hunts.add((Object)h);
        }
        n.m_128365_("Hunts", (Tag)hunts);
        ListTag memories = new ListTag();
        e.memories.forEach(m -> memories.add((Object)m.m_6426_()));
        n.m_128365_("Memories", (Tag)memories);
        ListTag banners = new ListTag();
        e.banners.forEach((index, at) -> {
            CompoundTag b = at.save();
            b.m_128405_("Team", index.intValue());
            banners.add((Object)b);
        });
        n.m_128365_("Banners", (Tag)banners);
        AdventureData.Session run = RoyalRuns.session(p);
        if (run != null && !RoyalRuns.valid(run)) {
            RoyalRuns.fail(p, "invalid_activity");
            run = null;
        }
        if (run != null) {
            CompoundTag v2 = run.royal.m_6426_();
            v2.m_128473_("Arrows");
            v2.m_128359_("Kind", run.kind.name());
            v2.m_128405_("Phase", run.phase.ordinal());
            v2.m_128359_("Dimension", run.dimension);
            v2.m_128356_("Anchor", run.anchor.m_121878_());
            long clock = run.phase == AdventureData.Phase.PAUSED ? v2.m_128454_("PausedAt") : RoyalService.now(p);
            v2.m_128356_("Remaining", Math.max(0L, v2.m_128454_("Deadline") - clock));
            v2.m_128356_("Elapsed", Math.max(0L, clock - v2.m_128454_("Started")));
            v2.m_128405_("Enemies", run.npcs.size());
            n.m_128365_("Run", (Tag)v2);
        }
        return n;
    }

    public record Request(Action action, int first, int second, int third, String text, List<UUID> ids) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.writeInt(this.first);
            b.writeInt(this.second);
            b.writeInt(this.third);
            b.m_130072_(this.text, 256);
            b.m_130130_(this.ids.size());
            this.ids.forEach(arg_0 -> ((FriendlyByteBuf)b).m_130077_(arg_0));
        }

        public static Request decode(FriendlyByteBuf b) {
            Action a = (Action)b.m_130066_(Action.class);
            int x = b.readInt();
            int y = b.readInt();
            int z = b.readInt();
            String text = b.m_130136_(256);
            int count = b.m_130242_();
            if (count < 0 || count > 24) {
                throw new IllegalArgumentException("Invalid team size");
            }
            ArrayList<UUID> ids = new ArrayList<UUID>();
            for (int i = 0; i < count; ++i) {
                ids.add(b.m_130259_());
            }
            return new Request(a, x, y, z, text, List.copyOf(ids));
        }

        public void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context context = c.get();
            context.enqueueWork(() -> {
                String key;
                ServerPlayer p = context.getSender();
                if (p == null) {
                    return;
                }
                long now = RoyalService.now(p);
                String string = key = this.action == Action.VIEW ? "RoyalViewAt" : "RoyalActionAt";
                if (p.getPersistentData().m_128441_(key) && now >= p.getPersistentData().m_128454_(key) && now - p.getPersistentData().m_128454_(key) < (long)(this.action == Action.VIEW ? 5 : 2)) {
                    return;
                }
                p.getPersistentData().m_128356_(key, now);
                RoyalService.state((ServerPlayer)p).royalViewUntil = now + 100L;
                if (this.action == Action.VIEW) {
                    p.getPersistentData().m_128356_("EclipseViewUntil", now + 100L);
                    if (this.second == 1) {
                        RoyalService.state((ServerPlayer)p).lastRoyal = null;
                    }
                    EclipseNetwork.send(p, false);
                }
                if (RoyalNetwork.perform(p, this) == 0 && this.action != Action.VIEW) {
                    RoyalText.tell(p, "unavailable", new Object[0]);
                }
                RoyalNetwork.send(p, false, 0);
            });
            context.setPacketHandled(true);
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
            c.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientRoyal.accept(this)));
            c.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        TEAM_SAVE,
        DEPLOY,
        RECALL,
        UPGRADE,
        POLICY,
        SUPPLY,
        BAGS,
        WANTED,
        RALLY,
        MUTATOR,
        START,
        PUZZLE,
        PAUSE,
        RESUME,
        ABANDON,
        CLAIM,
        SELECT_TEAM;

    }
}

