/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Mob
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package dev.shadowsovereign.network;

import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.client.ClientSoulLife;
import dev.shadowsovereign.royal.RoyalSync;
import dev.shadowsovereign.soullife.SoulLifeContent;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.soullife.SoulLifeState;
import dev.shadowsovereign.soullife.SoulLifeText;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.vigil.VigilCrown;
import dev.shadowsovereign.vigil.VigilGraves;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class SoulLifeNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel ch) {
        channel = ch;
        ch.registerMessage(23, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        ch.registerMessage(24, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action action, UUID a, UUID b, int x, int y, int nonce) {
        channel.sendToServer((Object)new Request(action, a, b, x, y, nonce));
    }

    public static boolean perform(ServerPlayer p, Request q) {
        if (q.action != Action.VIEW && (!p.m_6084_() || p.m_5833_())) {
            return false;
        }
        SoulLifeState.Owner o = SoulLifeService.owner(p);
        ShadowRecord r = SoulLifeService.record(p, q.a);
        if (q.action == Action.VIEW) {
            return true;
        }
        if (q.action == Action.CANCEL) {
            return SoulPlay.cancel(p, false);
        }
        if (q.action == Action.EXPRESSIONS) {
            o.expressions = !o.expressions;
            SoulLifeService.dirty(p);
            return true;
        }
        if (q.action == Action.CLUE) {
            return SoulPlay.clue(p);
        }
        if (r == null) {
            return false;
        }
        SoulLifeService.ensure(p, r);
        return switch (q.action) {
            case Action.SELECT -> {
                o.selected = r.id;
                SoulLifeService.dirty(p);
                yield true;
            }
            case Action.PET -> SoulLifeService.pet(p, r);
            case Action.FEED -> SoulLifeService.feed(p, r);
            case Action.TALK -> SoulLifeService.talk(p, r);
            case Action.PRAISE -> SoulLifeService.praise(p, r);
            case Action.COMFORT -> SoulLifeService.comfort(p, r);
            case Action.GIVE -> SoulLifeService.keepsake(p, r, false);
            case Action.TAKE -> SoulLifeService.keepsake(p, r, true);
            case Action.MOOD -> {
                r.life.showMood = !r.life.showMood;
                Mob m = ShadowService.active(p.f_8924_, r);
                if (m != null) {
                    ShadowService.updateName(m, r);
                }
                SoulLifeService.dirty(p);
                yield true;
            }
            case Action.SPACE -> {
                if (q.x < 0 || q.x > 3) {
                    yield false;
                }
                r.life.spacing = q.x;
                SoulLifeService.dirty(p);
                yield true;
            }
            case Action.LINK -> SoulLifeService.friends(p, r.id, q.b, false);
            case Action.UNLINK -> SoulLifeService.friends(p, r.id, q.b, true);
            case Action.REST -> {
                if (q.x == 1) {
                    yield SoulPlay.restGroup(p);
                }
                yield SoulPlay.rest(p, r.id);
            }
            case Action.STORY -> SoulPlay.story(p, r.id, q.x, q.nonce);
            case Action.DANCE -> SoulPlay.start(p, SoulPlay.Game.DANCE, List.of(r.id));
            case Action.HIDE -> SoulPlay.hide(p, r.id);
            case Action.TAG -> SoulPlay.start(p, SoulPlay.Game.TAG, List.of(r.id));
            case Action.SPAR -> {
                if (q.b != null && SoulPlay.start(p, SoulPlay.Game.SPAR, List.of(r.id, q.b))) {
                    yield true;
                }
                yield false;
            }
            case Action.MELODY -> SoulPlay.start(p, SoulPlay.Game.MELODY, List.of(r.id));
            case Action.NOTE -> {
                if (q.x < 0 || q.x >= 4 || q.y < 0 || q.y > 7 || !SoulLifeService.quiet(p, r)) {
                    yield false;
                }
                r.life.melody[q.x] = q.y;
                SoulLifeService.dirty(p);
                SoulPlay.note(p, r, q.x);
                yield true;
            }
            case Action.AID -> {
                r.life.firstAid = !r.life.firstAid;
                SoulLifeService.dirty(p);
                yield true;
            }
            case Action.RESCUE -> {
                r.life.voidRescue = !r.life.voidRescue;
                SoulLifeService.dirty(p);
                yield true;
            }
            case Action.FETCH -> SoulPlay.fetch(p, r, InteractionHand.MAIN_HAND);
            case Action.RECAP -> {
                SoulLifeText.tell(p, "recap", SoulLifeService.name(p, r), r.life.dayCare, r.life.dayGames, (int)r.life.dayWalk, r.life.dayKills, r.life.dayHelps);
                yield true;
            }
            default -> false;
        };
    }

    public static void send(ServerPlayer p, boolean open, int page) {
        SoulLifeState.Owner o = SoulLifeService.owner(p);
        CompoundTag full = SoulLifeNetwork.view(p);
        CompoundTag delta = RoyalSync.delta(full, open ? null : o.lastView);
        o.lastView = full;
        if (open) {
            o.viewUntil = SoulLifeService.now(p) + 100L;
        }
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(delta, open, Math.max(0, Math.min(7, page))));
    }

    public static CompoundTag view(ServerPlayer p) {
        SoulLifeState.Owner o = SoulLifeService.owner(p);
        if (SoulLifeService.record(p, o.selected) == null) {
            o.selected = SoulLifeService.army((ServerPlayer)p).shadows.keySet().stream().findFirst().orElse(null);
        }
        CompoundTag n = new CompoundTag();
        n.m_128356_("Time", SoulLifeService.now(p));
        n.m_128379_("Expressions", o.expressions);
        n.m_128379_("NearHearth", SoulLifeContent.near(p) != null);
        n.m_128379_("Emergency", VigilCrown.active(p));
        n.m_128356_("PlayAt", o.playAt);
        n.m_128365_("Activity", (Tag)o.activity.m_6426_());
        ListTag roster = new ListTag();
        for (ShadowRecord r : SoulLifeService.army((ServerPlayer)p).shadows.values()) {
            CompoundTag row = new CompoundTag();
            row.m_128362_("Id", r.id);
            row.m_128359_("Name", SoulLifeService.name(p, r));
            row.m_128359_("Type", r.type);
            row.m_128405_("Affection", r.life.affection);
            row.m_128405_("Personality", r.life.personality < 0 ? Math.floorMod(r.id.hashCode(), 6) : r.life.personality);
            row.m_128405_("Level", r.level());
            Mob m = ShadowService.active(p.f_8924_, r);
            if (m != null) {
                row.m_128362_("Entity", m.m_20148_());
            }
            row.m_128379_("Loaded", m != null && m.m_9236_() == p.m_9236_());
            row.m_128379_("Pending", r.pendingRespawn());
            row.m_128379_("PaidRequired", VigilGraves.needsPayment(r));
            row.m_128350_("HP", m == null ? r.health : m.m_21223_());
            row.m_128350_("Max", m == null ? r.maxHealth : m.m_21233_());
            row.m_128359_("Mood", SoulLifeService.mood(r, m, SoulLifeService.now(p)).name());
            roster.add((Object)row);
        }
        n.m_128365_("Roster", (Tag)roster);
        ShadowRecord r = SoulLifeService.record(p, o.selected);
        if (r != null) {
            SoulLifeService.ensure(p, r);
            n.m_128362_("Selected", r.id);
            CompoundTag profile = r.life.save();
            profile.m_128473_("Keepsake");
            profile.m_128379_("HasKeepsake", !r.life.keepsake.m_41619_());
            profile.m_128359_("KeepsakeName", r.life.keepsake.m_41619_() ? "" : r.life.keepsake.m_41786_().getString());
            profile.m_128359_("FavoriteGame", SoulLifeService.favoriteGame(r).name());
            profile.m_128379_("Near", SoulLifeService.nearby(p, r, 4.0) != null);
            profile.m_128379_("Quiet", SoulLifeService.quiet(p, r));
            ListTag friends = new ListTag();
            r.life.friends.forEach((id, f) -> {
                ShadowRecord other = SoulLifeService.record(p, id);
                if (other != null) {
                    CompoundTag row = f.save();
                    row.m_128362_("Id", id);
                    row.m_128359_("Name", SoulLifeService.name(p, other));
                    friends.add((Object)row);
                }
            });
            profile.m_128365_("Friends", (Tag)friends);
            n.m_128365_("Profile", (Tag)profile);
        }
        return n;
    }

    public record Request(Action action, UUID a, UUID b, int x, int y, int nonce) {
        public void encode(FriendlyByteBuf out) {
            out.m_130068_((Enum)this.action);
            out.writeBoolean(this.a != null);
            if (this.a != null) {
                out.m_130077_(this.a);
            }
            out.writeBoolean(this.b != null);
            if (this.b != null) {
                out.m_130077_(this.b);
            }
            out.writeInt(this.x);
            out.writeInt(this.y);
            out.writeInt(this.nonce);
        }

        public static Request decode(FriendlyByteBuf in) {
            return new Request((Action)in.m_130066_(Action.class), in.readBoolean() ? in.m_130259_() : null, in.readBoolean() ? in.m_130259_() : null, in.readInt(), in.readInt(), in.readInt());
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            NetworkEvent.Context ctx = context.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                long now = SoulLifeService.now(p);
                String key = this.action == Action.VIEW ? "SoulLifeViewAt" : "SoulLifeActionAt";
                CompoundTag tag = p.getPersistentData();
                if (tag.m_128441_(key) && now - tag.m_128454_(key) >= 0L && now - tag.m_128454_(key) < (long)(this.action == Action.VIEW ? 5 : 2)) {
                    return;
                }
                tag.m_128356_(key, now);
                SoulLifeState.Owner owner = SoulLifeService.owner(p);
                owner.viewUntil = now + 100L;
                if (this.action == Action.VIEW && this.x == 1) {
                    owner.lastView = null;
                }
                if (!SoulLifeNetwork.perform(p, this) && this.action != Action.VIEW) {
                    SoulLifeText.tell(p, "unavailable", new Object[0]);
                }
                SoulLifeNetwork.send(p, false, 0);
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

        public void handle(Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientSoulLife.accept(this)));
            context.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        SELECT,
        PET,
        FEED,
        TALK,
        PRAISE,
        COMFORT,
        GIVE,
        TAKE,
        MOOD,
        SPACE,
        LINK,
        UNLINK,
        REST,
        STORY,
        DANCE,
        HIDE,
        TAG,
        SPAR,
        MELODY,
        NOTE,
        CANCEL,
        AID,
        RESCUE,
        EXPRESSIONS,
        FETCH,
        RECAP,
        CLUE;

    }
}

