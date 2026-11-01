/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
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
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.client.ClientJourney;
import dev.shadowsovereign.journey.JourneyActivities;
import dev.shadowsovereign.journey.JourneyCombat;
import dev.shadowsovereign.journey.JourneyService;
import dev.shadowsovereign.journey.JourneyState;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.network.LegacyNetwork;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class JourneyNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(9, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(10, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Request r) {
        channel.sendToServer((Object)r);
    }

    public static void send(ServerPlayer p, boolean open) {
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(JourneyNetwork.snapshot(p), open));
    }

    public static int perform(ServerPlayer p, Request r) {
        if (r.action == Action.EXIT_CAMERA) {
            JourneyView.exit(p);
            return 1;
        }
        if (r.companion != null && LegacyService.record(p, r.companion) == null || r.action != Action.VIEW && (!p.m_6084_() || p.m_5833_() || JourneyView.active(p))) {
            return 0;
        }
        boolean ok = switch (r.action) {
            default -> throw new IncompatibleClassChangeError();
            case Action.VIEW -> true;
            case Action.BOSS -> JourneyService.start(p, AdventureData.Kind.BOSS, r.value, r.hard, null);
            case Action.RIFT -> JourneyService.start(p, AdventureData.Kind.RIFT, 0, r.hard, null);
            case Action.RALLY -> JourneyService.start(p, AdventureData.Kind.RALLY, 0, r.hard, r.companion);
            case Action.FISH -> JourneyService.start(p, AdventureData.Kind.ANGLING, 0, r.hard, null);
            case Action.CAMP -> JourneyService.start(p, AdventureData.Kind.CAMP, r.value, false, null);
            case Action.ROUTE -> JourneyCombat.route(p, r.value);
            case Action.BOON -> JourneyCombat.boon(p, r.value);
            case Action.CAST -> JourneyActivities.cast(p);
            case Action.REEL -> JourneyActivities.reel(p);
            case Action.DOMAIN -> JourneyService.domain(p);
            case Action.CHAINS -> JourneyService.chains(p, r.companion);
            case Action.CAMERA -> JourneyView.start(p, r.companion);
            case Action.BADGE -> JourneyService.badge(p, r.companion, r.value);
            case Action.CAMPAIGN -> JourneyService.campaign(p);
            case Action.PAUSE -> {
                AdventureWorld.pause(p.f_8924_, p.m_20148_());
                yield true;
            }
            case Action.RESUME -> AdventureWorld.resume(p);
            case Action.CANCEL -> AdventureWorld.cancel(p);
            case Action.CLAIM -> AdventureWorld.claim(p);
            case Action.EXIT_CAMERA -> true;
        };
        return ok ? 1 : 0;
    }

    public static CompoundTag snapshot(ServerPlayer p) {
        UUID id;
        Mob m;
        CompoundTag root = LegacyNetwork.snapshot(p, null);
        JourneyState st = JourneyService.state(p);
        CompoundTag publicData = st.save();
        publicData.m_128473_("DomainPos");
        publicData.m_128473_("DomainDimension");
        root.m_128365_("Journey", (Tag)publicData);
        root.m_128379_("ChapterReady", JourneyService.chapterReady(p, st.data.m_128451_("Chapter")));
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s != null && JourneyService.isJourney(s.kind)) {
            CompoundTag n = new CompoundTag();
            for (String key : List.of("Hard", "Choice", "Started", "Deadline", "ChooseRoute", "ChooseBoon", "Risk", "Bites", "Cast", "Catch", "Spoken", "Reward")) {
                if (!s.journey.m_128441_(key)) continue;
                n.m_128365_(key, s.journey.m_128423_(key).m_6426_());
            }
            long clock = s.phase == AdventureData.Phase.PAUSED ? s.journey.m_128454_("PausedAt") : (s.phase == AdventureData.Phase.READY ? s.journey.m_128454_("Clock") : JourneyService.now(p));
            n.m_128356_("Elapsed", Math.max(0L, clock - s.journey.m_128454_("Started")));
            n.m_128356_("Remaining", Math.max(0L, s.journey.m_128454_("Deadline") - clock));
            n.m_128379_("Bite", s.phase == AdventureData.Phase.ACTIVE && s.journey.m_128471_("Cast") && JourneyService.now(p) >= s.journey.m_128454_("BiteAt") && JourneyService.now(p) <= s.journey.m_128454_("BiteEnd"));
            if (s.kind == AdventureData.Kind.CAMP) {
                n.m_128359_("Story", s.journey.m_128461_("StoryText"));
            }
            root.m_128469_("Session").m_128365_("JourneyRun", (Tag)n);
        }
        if (JourneyView.active(p) && (m = LegacyService.eligible(p, id = p.getPersistentData().m_128342_("JourneyView"), 1, 32)) != null) {
            root.m_128362_("Camera", m.m_20148_());
            root.m_128362_("CameraRecord", id);
            root.m_128350_("CameraHealth", m.m_21223_());
            root.m_128350_("CameraMax", m.m_21233_());
        }
        return root;
    }

    public record Request(Action action, UUID companion, int value, boolean hard) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.writeBoolean(this.companion != null);
            if (this.companion != null) {
                b.m_130077_(this.companion);
            }
            b.m_130130_(this.value);
            b.writeBoolean(this.hard);
        }

        public static Request decode(FriendlyByteBuf b) {
            return new Request((Action)b.m_130066_(Action.class), b.readBoolean() ? b.m_130259_() : null, b.m_130242_(), b.readBoolean());
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                long time = JourneyService.now(p);
                String key = this.action == Action.VIEW ? "JourneyViewAt" : "JourneyActionAt";
                CompoundTag n = p.getPersistentData();
                if (this.action != Action.EXIT_CAMERA && n.m_128441_(key) && time >= n.m_128454_(key) && time - n.m_128454_(key) < (long)(this.action == Action.VIEW ? 5 : 2)) {
                    return;
                }
                n.m_128356_(key, time);
                if (JourneyNetwork.perform(p, this) == 0 && this.action != Action.VIEW) {
                    JourneyService.tell(p, "Journey action unavailable. Check its unlock, range, cooldown and active activity.");
                }
                JourneyNetwork.send(p, false);
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
                throw new IllegalArgumentException("Missing Journey view");
            }
            return new View(n, b.readBoolean());
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientJourney.accept(this)));
            ctx.setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        BOSS,
        RIFT,
        RALLY,
        FISH,
        CAMP,
        ROUTE,
        BOON,
        CAST,
        REEL,
        DOMAIN,
        CHAINS,
        CAMERA,
        EXIT_CAMERA,
        BADGE,
        CAMPAIGN,
        PAUSE,
        RESUME,
        CANCEL,
        CLAIM;

    }
}

