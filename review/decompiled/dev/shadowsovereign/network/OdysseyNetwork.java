/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
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
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyBosses;
import dev.shadowsovereign.odyssey.OdysseyCamp;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyGames;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.odyssey.OdysseyVisitors;
import dev.shadowsovereign.royal.RoyalSync;
import dev.shadowsovereign.soullife.SoulLifeService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class OdysseyNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel value) {
        channel = value;
        channel.registerMessage(25, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        channel.registerMessage(26, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action action, UUID selected, int x, int y, int nonce, String text) {
        channel.sendToServer((Object)new Request(action, selected, x, y, nonce, text));
    }

    public static void open(ServerPlayer p, int page) {
        GuideService.record(p, "guide_open");
        GuideService.importEvidence(p);
        OdysseyNetwork.send(p, true, page);
    }

    public static boolean perform(ServerPlayer p, Request q) {
        if (q.action != Action.VIEW && (!p.m_6084_() || p.m_5833_())) {
            return false;
        }
        OdysseyState state = GuideService.state(p);
        switch (q.action) {
            case VIEW: {
                if (q.x == 1) {
                    GuideService.record(p, "guide_open");
                }
                return true;
            }
            case OPEN: {
                OdysseyNetwork.open(p, q.x);
                return true;
            }
            case CHAPTER: {
                if (q.x < 0 || q.x >= 12) {
                    return false;
                }
                state.selectedChapter = q.x;
                GuideService.dirty(p);
                return true;
            }
            case PIN: {
                if (q.x < -1 || q.x >= OdysseyCatalog.goals().size()) {
                    return false;
                }
                state.pinned = q.x;
                GuideService.dirty(p);
                return true;
            }
            case LESSON: {
                return GuideService.lesson(p, q.x);
            }
            case CLAIM: {
                return GuideService.claim(p, q.x);
            }
            case RESTORE: {
                return GuideService.restoreBook(p);
            }
            case SELECT: {
                if (q.selected == null || !GuideService.army((ServerPlayer)p).shadows.containsKey(q.selected)) {
                    return false;
                }
                state.selected = q.selected;
                GuideService.dirty(p);
                return true;
            }
            case PARTY: {
                return OdysseyRuns.party(p, q.selected);
            }
            case START: {
                return OdysseyRuns.start(p, q.x, q.y, List.of());
            }
            case INPUT: {
                return OdysseyRuns.input(p, q.x, q.y, q.nonce);
            }
            case PAUSE: {
                return OdysseyRuns.pause(p);
            }
            case RESUME: {
                return OdysseyRuns.resume(p);
            }
            case FINISH: {
                return OdysseyRuns.finish(p, q.x == 1);
            }
            case RETURN: {
                return OdysseyRealm.leave(p);
            }
            case JOB: {
                return OdysseyCamp.assign(p, q.x, q.y);
            }
            case COOK: {
                return OdysseyCamp.cook(p, q.x);
            }
            case FURNITURE: {
                return OdysseyCamp.furniture(p, q.x);
            }
            case PERSONAL: {
                return OdysseyCamp.personal(p, q.x);
            }
            case CLUB: {
                return OdysseyCamp.club(p, q.x);
            }
            case GESTURE: {
                return OdysseyCamp.gesture(p, q.x);
            }
            case ARTIFACT: {
                return OdysseyCamp.equipArtifact(p, q.x);
            }
            case PRANKS: {
                state.pranks = !state.pranks;
                GuideService.dirty(p);
                return true;
            }
            case WELCOMES: {
                state.welcomes = !state.welcomes;
                GuideService.dirty(p);
                return true;
            }
            case CHALLENGE: {
                return OdysseyRuns.custom(p);
            }
            case IMPORT: {
                return OdysseyRuns.importChallenge(p, q.text);
            }
            case CORONATE: {
                return OdysseyBosses.coronate(p);
            }
            case CYCLE: {
                return OdysseyBosses.newCycle(p);
            }
            case VISITOR: {
                return OdysseyVisitors.invite(p);
            }
            case TRADE: {
                return OdysseyVisitors.trade(p, q.x);
            }
            case PHOTO: {
                return OdysseyVisitors.photo(p);
            }
            case QUICK: {
                return OdysseyRuns.quick(p, q.nonce);
            }
        }
        return false;
    }

    public static CompoundTag view(ServerPlayer p) {
        CompoundTag n;
        OdysseyState state = GuideService.state(p);
        GuideService.importEvidence(p);
        CompoundTag out = new CompoundTag();
        out.m_128405_("Chapter", state.selectedChapter);
        out.m_128405_("Claims", state.chapterClaims);
        out.m_128405_("Pinned", state.pinned);
        out.m_128405_("Seals", state.seals);
        out.m_128405_("Cycle", state.cycle);
        out.m_128405_("Reputation", state.rep);
        out.m_128379_("Ended", state.ended);
        out.m_128379_("Pranks", state.pranks);
        out.m_128379_("Welcomes", state.welcomes);
        out.m_128379_("Realm", OdysseyRealm.inside(p));
        out.m_128379_("Camp", OdysseyRealm.camp(p, p.m_20183_()));
        out.m_128405_("Building", state.plotReady ? 65536 : state.buildCursor);
        out.m_128405_("Progress", GuideService.completed(p));
        out.m_128405_("Artifact", state.artifact);
        out.m_128359_("Version", "1.2.0");
        out.m_128356_("Time", GuideService.now(p));
        out.m_128405_("ArmyTotal", GuideService.army((ServerPlayer)p).shadows.size());
        out.m_128405_("ArmyActive", (int)GuideService.army(p).activeCount());
        ListTag goals = new ListTag();
        for (OdysseyCatalog.Goal goal : OdysseyCatalog.goals()) {
            n = new CompoundTag();
            n.m_128359_("Id", goal.id());
            n.m_128379_("Done", GuideService.done(p, goal));
            goals.add((Object)n);
        }
        out.m_128365_("Goals", (Tag)goals);
        ListTag roster = new ListTag();
        if (!GuideService.army((ServerPlayer)p).shadows.containsKey(state.selected)) {
            state.selected = GuideService.army((ServerPlayer)p).shadows.keySet().stream().findFirst().orElse(null);
        }
        if (state.selected != null) {
            out.m_128362_("Selected", state.selected);
        }
        for (ShadowRecord r : GuideService.army((ServerPlayer)p).shadows.values()) {
            CompoundTag row = new CompoundTag();
            row.m_128362_("Id", r.id);
            row.m_128359_("Name", SoulLifeService.name(p, r));
            row.m_128359_("Type", r.type);
            row.m_128405_("Level", r.level());
            row.m_128405_("Affection", r.life.affection);
            row.m_128405_("Personality", r.life.personality);
            row.m_128405_("PersonalStage", r.odyssey.story);
            row.m_128405_("Club", r.odyssey.club);
            row.m_128405_("ClubRank", r.odyssey.clubRank);
            row.m_128405_("Gestures", r.odyssey.gestures);
            row.m_128405_("Job", r.odyssey.job);
            Mob mob = ShadowService.active(p.f_8924_, r);
            row.m_128379_("Loaded", mob != null && mob.m_9236_() == p.m_9236_());
            if (mob != null) {
                row.m_128362_("Entity", mob.m_20148_());
            }
            roster.add((Object)row);
        }
        out.m_128365_("Roster", (Tag)roster);
        AdventureData.Session session = OdysseyRuns.session(p);
        if (session != null && OdysseyRuns.mode(session) != null) {
            n = new CompoundTag();
            n.m_128362_("Id", session.id);
            n.m_128405_("Mode", session.journey.m_128451_("Mode"));
            n.m_128405_("BossStage", session.journey.m_128451_("Phase"));
            if (OdysseyRuns.mode(session).boss()) {
                n.m_128405_("Mask", OdysseyBosses.mask(session));
            }
            n.m_128405_("Phase", session.phase.ordinal());
            n.m_128379_("Built", session.journey.m_128471_("Built"));
            for (String key : List.of("Difficulty", "Build", "Turn", "Stage", "Elapsed", "Duration", "Score", "OtherScore", "Shots", "Mistakes", "Station", "RepairStation", "Hold", "Ore", "Stability", "Clues", "Offer", "Bid", "AuctionUntil", "OfferItem", "Purchases", "Round", "RoundStarted", "HotUntil", "Pixel", "Color", "Runes", "Paid", "Cut", "TreasureNext", "Levers")) {
                if (!session.journey.m_128441_(key)) continue;
                n.m_128365_(key, session.journey.m_128423_(key).m_6426_());
            }
            ListTag crew = new ListTag();
            for (Mob mob : OdysseyRuns.allies(p, session)) {
                CompoundTag row = new CompoundTag();
                row.m_128362_("Id", mob.m_20148_());
                row.m_128359_("Name", mob.m_7755_().getString());
                crew.add((Object)row);
            }
            n.m_128365_("Crew", (Tag)crew);
            n.m_128405_("Rule", session.journey.m_128451_("Rule"));
            n.m_128405_("Layout", session.journey.m_128451_("Layout"));
            n.m_128405_("Beat", session.journey.m_128451_("Elapsed") % 20);
            n.m_128379_("ResumeRequested", session.journey.m_128471_("ResumeRequested"));
            n.m_128379_("Repair", session.journey.m_128471_("Repair"));
            n.m_128379_("YourBid", session.journey.m_128471_("YourBid"));
            n.m_128347_("Rope", session.journey.m_128459_("Rope"));
            if (OdysseyRuns.mode(session) == OdysseyCatalog.Mode.PAINT) {
                n.m_128385_("Pattern", OdysseyGames.pattern(session));
            }
            out.m_128365_("Run", (Tag)n);
        }
        ListTag earned = new ListTag();
        state.earned.forEach(v -> earned.add((Object)StringTag.m_129297_((String)v)));
        out.m_128365_("Earned", (Tag)earned);
        CompoundTag medals = new CompoundTag();
        for (OdysseyCatalog.Mode mode : OdysseyCatalog.Mode.values()) {
            medals.m_128405_(mode.id(), (int)state.count("medal." + mode.id()));
        }
        out.m_128365_("Medals", (Tag)medals);
        out.m_128365_("CampState", (Tag)OdysseyVisitors.publicView(p));
        return out;
    }

    public static void send(ServerPlayer p, boolean open, int page) {
        OdysseyState state = GuideService.state(p);
        CompoundTag full = OdysseyNetwork.view(p);
        CompoundTag delta = RoyalSync.delta(full, open ? null : state.lastView);
        state.lastView = full;
        if (open) {
            state.viewUntil = GuideService.now(p) + 100L;
        }
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(delta, open, Math.max(0, Math.min(11, page))));
    }

    public record Request(Action action, UUID selected, int x, int y, int nonce, String text) {
        public void encode(FriendlyByteBuf out) {
            out.m_130068_((Enum)this.action);
            out.writeBoolean(this.selected != null);
            if (this.selected != null) {
                out.m_130077_(this.selected);
            }
            out.writeInt(this.x);
            out.writeInt(this.y);
            out.writeInt(this.nonce);
            out.m_130072_(this.text, 2048);
        }

        public static Request decode(FriendlyByteBuf in) {
            return new Request((Action)in.m_130066_(Action.class), in.readBoolean() ? in.m_130259_() : null, in.readInt(), in.readInt(), in.readInt(), in.m_130136_(2048));
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                long now = GuideService.now(p);
                String key = this.action == Action.VIEW ? "OdysseyViewAt" : "OdysseyActionAt";
                CompoundTag data = p.getPersistentData();
                if (data.m_128441_(key) && now - data.m_128454_(key) >= 0L && now - data.m_128454_(key) < (long)(this.action == Action.VIEW ? 5 : 2)) {
                    return;
                }
                data.m_128356_(key, now);
                OdysseyState state = GuideService.state(p);
                state.viewUntil = now + 100L;
                if (this.action == Action.VIEW && this.x == 1) {
                    state.lastView = null;
                }
                if (!OdysseyNetwork.perform(p, this) && this.action != Action.VIEW) {
                    if (this.action == Action.INPUT || this.action == Action.QUICK) {
                        if (now >= data.m_128454_("OdysseyDeniedAt")) {
                            p.m_5661_((Component)OdysseyText.c("unavailable", new Object[0]), true);
                            data.m_128356_("OdysseyDeniedAt", now + 20L);
                        }
                    } else {
                        OdysseyText.tell(p, "unavailable", new Object[0]);
                    }
                }
                if (this.action != Action.OPEN) {
                    OdysseyNetwork.send(p, false, 0);
                }
            });
            ctx.setPacketHandled(true);
        }
    }

    public record View(CompoundTag data, boolean open, int page) {
        public void encode(FriendlyByteBuf out) {
            out.m_130079_(this.data);
            out.writeBoolean(this.open);
            out.m_130130_(this.page);
        }

        public static View decode(FriendlyByteBuf in) {
            CompoundTag n = in.m_130260_();
            return new View(n == null ? new CompoundTag() : n, in.readBoolean(), in.m_130242_());
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientOdyssey.accept(this)));
            supplier.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        OPEN,
        CHAPTER,
        PIN,
        LESSON,
        CLAIM,
        RESTORE,
        SELECT,
        START,
        INPUT,
        PAUSE,
        RESUME,
        FINISH,
        RETURN,
        JOB,
        COOK,
        FURNITURE,
        PERSONAL,
        CLUB,
        GESTURE,
        ARTIFACT,
        PRANKS,
        WELCOMES,
        CHALLENGE,
        IMPORT,
        CORONATE,
        CYCLE,
        VISITOR,
        TRADE,
        PHOTO,
        QUICK,
        PARTY;

    }
}

