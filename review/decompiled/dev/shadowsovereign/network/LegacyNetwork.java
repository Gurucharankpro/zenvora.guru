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
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.client.ClientLegacy;
import dev.shadowsovereign.journey.JourneyView;
import dev.shadowsovereign.legacy.LegacyChallenges;
import dev.shadowsovereign.legacy.LegacyRun;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.legacy.LegacyState;
import dev.shadowsovereign.network.AdventureNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
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

public final class LegacyNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(7, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(8, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Request r) {
        channel.sendToServer((Object)r);
    }

    public static void open(ServerPlayer p) {
        LegacyNetwork.open(p, "POWERS");
    }

    public static void open(ServerPlayer p, String page) {
        ShadowNetwork.sendArmy(p, false);
        CompoundTag data = LegacyNetwork.snapshot(p, null);
        data.m_128359_("OpenPage", page);
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(data, true));
    }

    public static void send(ServerPlayer p, boolean open, UUID selected) {
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(LegacyNetwork.snapshot(p, selected), open));
    }

    public static int perform(ServerPlayer p, Request r) {
        int n;
        block46: {
            block45: {
                if (JourneyView.active(p) && r.action != Action.VIEW) {
                    return 0;
                }
                if (r.text == null || r.text.length() > 96 || r.companion != null && LegacyService.record(p, r.companion) == null || r.other != null && LegacyService.record(p, r.other) == null || r.action != Action.VIEW && (!p.m_6084_() || p.m_5833_())) {
                    return 0;
                }
                switch (r.action) {
                    default: {
                        throw new IncompatibleClassChangeError();
                    }
                    case VIEW: {
                        break;
                    }
                    case EXCHANGE: {
                        if (LegacyService.exchange(p, r.companion)) {
                            break;
                        }
                        break block45;
                    }
                    case BURST: {
                        if (LegacyService.burst(p, r.companion)) {
                            break;
                        }
                        break block45;
                    }
                    case LINK: {
                        if (LegacyService.link(p, r.companion, r.other)) {
                            break;
                        }
                        break block45;
                    }
                    case UNLINK: {
                        if (LegacyService.unlink(p)) {
                            break;
                        }
                        break block45;
                    }
                    case GAUNTLET: {
                        if (AdventureWorld.start(p, AdventureData.Kind.GAUNTLET, 0)) {
                            break;
                        }
                        break block45;
                    }
                    case SIEGE: {
                        if (AdventureWorld.start(p, AdventureData.Kind.SIEGE, 0)) {
                            break;
                        }
                        break block45;
                    }
                    case VAULT: {
                        if (LegacyChallenges.startVault(p)) {
                            break;
                        }
                        break block45;
                    }
                    case EXTRACT: {
                        if (LegacyChallenges.extract(p)) {
                            break;
                        }
                        break block45;
                    }
                    case BOON: {
                        if (LegacyChallenges.choose(p, r.value)) {
                            break;
                        }
                        break block45;
                    }
                    case SYMBOL: {
                        if (LegacyChallenges.symbol(p, r.value)) {
                            break;
                        }
                        break block45;
                    }
                    case RETRY: {
                        if (LegacyChallenges.retry(p)) {
                            break;
                        }
                        break block45;
                    }
                    case PAUSE: {
                        AdventureWorld.pause(p.f_8924_, p.m_20148_());
                        break;
                    }
                    case RESUME: {
                        if (AdventureWorld.resume(p)) {
                            break;
                        }
                        break block45;
                    }
                    case CANCEL: {
                        if (AdventureWorld.cancel(p)) {
                            break;
                        }
                        break block45;
                    }
                    case CLAIM: {
                        if (AdventureWorld.claim(p)) {
                            break;
                        }
                        break block45;
                    }
                    case NICKNAME: {
                        if (LegacyService.nickname(p, r.companion, r.text)) {
                            break;
                        }
                        break block45;
                    }
                    case FAVORITE: {
                        if (r.value >= 0 && r.value <= 1 && LegacyService.setFavorite(p, r.companion, r.value == 1)) {
                            break;
                        }
                        break block45;
                    }
                    case CHATTER: {
                        if (r.value >= 0 && r.value <= 1 && LegacyService.chatterToggle(p, r.value == 1)) {
                            break;
                        }
                        break block45;
                    }
                    case EMOTE: {
                        if (LegacyService.emote(p, r.value, r.scope, r.companion)) {
                            break;
                        }
                        break block45;
                    }
                    case SAVE_LOADOUT: {
                        if (LegacyService.saveLoadout(p, r.value, r.text)) {
                            break;
                        }
                        break block45;
                    }
                    case APPLY_LOADOUT: {
                        if (!LegacyService.applyLoadout(p, r.value)) break block45;
                    }
                }
                n = 1;
                break block46;
            }
            n = 0;
        }
        return n;
    }

    public static CompoundTag snapshot(ServerPlayer p, UUID selected) {
        CompoundTag n;
        CompoundTag root = AdventureNetwork.snapshot(p, selected, 0);
        AdventureData.Journal j = AdventureService.journal(p);
        LegacyState l = j.legacy;
        root.m_128365_("Legacy", (Tag)l.save());
        ListTag roster = root.m_128437_("Roster", 10);
        for (Tag tag : roster) {
            n = (CompoundTag)tag;
            UUID id = n.m_128342_("Id");
            ShadowRecord r = LegacyService.record(p, id);
            if (r == null) continue;
            n.m_128359_("OriginalName", r.name);
            n.m_128359_("Name", LegacyService.name(p, r));
            AdventureData.Lore lore = j.lore.get(id);
            n.m_128379_("Favorite", lore != null && lore.favorite);
            n.m_128359_("Nickname", lore == null ? "" : lore.nickname);
            n.m_128359_("Personality", LegacyState.Personality.of(id).name());
        }
        if (j.session != null) {
            AdventureData.Session s = j.session;
            LegacyRun r = s.legacy;
            n = root.m_128469_("Session");
            n.m_128405_("Integrity", r.integrity);
            n.m_128385_("Boons", r.boons);
            n.m_128379_("Choosing", r.choosing);
            n.m_128379_("Initialized", s.initialized);
            n.m_128379_("CanExtract", s.kind == AdventureData.Kind.GAUNTLET && s.wave >= 5 && !s.initialized && s.npcs.isEmpty() && s.phase == AdventureData.Phase.ACTIVE);
            if (s.kind == AdventureData.Kind.VAULT) {
                long time = r.pausedAt >= 0L ? r.pausedAt : LegacyService.now(p);
                n.m_128356_("Clock", time);
                n.m_128356_("ShowUntil", r.showUntil);
                n.m_128356_("Deadline", r.deadline);
                n.m_128405_("Input", r.input);
                n.m_128405_("Length", r.sequence(s.wave).length);
                n.m_128379_("Showing", time < r.showUntil);
                if (time < r.showUntil && s.phase == AdventureData.Phase.ACTIVE) {
                    n.m_128385_("Sequence", r.sequence(s.wave));
                }
            }
        }
        if (LegacyService.now(p) < p.getPersistentData().m_128454_("SoulboundLegacyNoticeUntil")) {
            root.m_128359_("Notice", p.getPersistentData().m_128461_("SoulboundLegacyNotice"));
        }
        return root;
    }

    public record Request(Action action, UUID companion, UUID other, int value, BattleRules.Squad scope, String text) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.writeBoolean(this.companion != null);
            if (this.companion != null) {
                b.m_130077_(this.companion);
            }
            b.writeBoolean(this.other != null);
            if (this.other != null) {
                b.m_130077_(this.other);
            }
            b.m_130130_(this.value);
            b.m_130068_((Enum)this.scope);
            b.m_130072_(this.text, 96);
        }

        public static Request decode(FriendlyByteBuf b) {
            return new Request((Action)b.m_130066_(Action.class), b.readBoolean() ? b.m_130259_() : null, b.readBoolean() ? b.m_130259_() : null, b.m_130242_(), (BattleRules.Squad)b.m_130066_(BattleRules.Squad.class), b.m_130136_(96));
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> {
                String key;
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                long time = LegacyService.now(p);
                String string = key = this.action == Action.VIEW ? "SoulboundLegacyViewAt" : "SoulboundLegacyActionAt";
                if (p.getPersistentData().m_128441_(key) && time >= p.getPersistentData().m_128454_(key) && time - p.getPersistentData().m_128454_(key) < (long)(this.action == Action.VIEW ? 10 : 2)) {
                    return;
                }
                p.getPersistentData().m_128356_(key, time);
                String old = p.getPersistentData().m_128461_("SoulboundLegacyNotice");
                long expiry = p.getPersistentData().m_128454_("SoulboundLegacyNoticeUntil");
                int result = LegacyNetwork.perform(p, this);
                if (result == 0 && this.action != Action.VIEW && old.equals(p.getPersistentData().m_128461_("SoulboundLegacyNotice")) && expiry == p.getPersistentData().m_128454_("SoulboundLegacyNoticeUntil")) {
                    LegacyService.notice(p, "Action unavailable. Check the unlock, cooldown, companion and Sanctuary requirements.");
                }
                if (this.action == Action.NICKNAME || this.action == Action.FAVORITE || this.action == Action.APPLY_LOADOUT) {
                    ShadowNetwork.sendArmy(p, false);
                }
                LegacyNetwork.send(p, false, this.companion);
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
                throw new IllegalArgumentException("Missing Legacy view");
            }
            return new View(n, b.readBoolean());
        }

        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientLegacy.accept(this)));
            ctx.setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        EXCHANGE,
        BURST,
        LINK,
        UNLINK,
        GAUNTLET,
        SIEGE,
        VAULT,
        EXTRACT,
        BOON,
        SYMBOL,
        RETRY,
        PAUSE,
        RESUME,
        CANCEL,
        CLAIM,
        NICKNAME,
        FAVORITE,
        CHATTER,
        EMOTE,
        SAVE_LOADOUT,
        APPLY_LOADOUT;

    }
}

