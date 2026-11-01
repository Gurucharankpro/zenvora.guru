/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.item.ItemStack
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
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.client.ClientMythic;
import dev.shadowsovereign.gloom.GloomRealm;
import dev.shadowsovereign.monolith.MonolithWorld;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicLair;
import dev.shadowsovereign.mythic.MythicPower;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.MythicState;
import dev.shadowsovereign.mythic.SoulDevourer;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.realm.WildBosses;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.vigil.VigilGraves;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

public final class MythicNetwork {
    private static SimpleChannel channel;

    public static void register(SimpleChannel c) {
        channel = c;
        c.registerMessage(29, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        c.registerMessage(30, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void request(Action a) {
        MythicNetwork.request(a, 0);
    }

    public static void request(Action a, int creature) {
        channel.sendToServer((Object)new Request(a, creature));
    }

    public static boolean perform(ServerPlayer p, Action a) {
        MythicState s = MythicService.state(p);
        ShadowRecord r = s.soul == null ? null : GuideService.army((ServerPlayer)p).shadows.get(s.soul);
        return switch (a) {
            default -> throw new IncompatibleClassChangeError();
            case Action.OPEN -> {
                GloomRealm.request(p);
                yield true;
            }
            case Action.VIEW -> true;
            case Action.RESUME -> MythicService.resume(p);
            case Action.CANCEL -> MythicService.cancel(p);
            case Action.CLAIM -> {
                if (WildBosses.claimPending(p) > 0) {
                    yield true;
                }
                yield false;
            }
            case Action.SUMMON -> {
                if (r != null && ShadowService.summon(p, r, true)) {
                    yield true;
                }
                yield false;
            }
            case Action.RECALL -> {
                if (r != null && ShadowService.recall(p, r)) {
                    yield true;
                }
                yield false;
            }
            case Action.FOLLOW -> {
                if (r == null) {
                    yield false;
                }
                r.order = Order.FOLLOW;
                ArmyData.get(p.f_8924_).m_77762_();
                yield true;
            }
            case Action.STAY -> {
                if (r == null) {
                    yield false;
                }
                r.order = Order.STAY;
                ArmyData.get(p.f_8924_).m_77762_();
                yield true;
            }
        };
    }

    public static boolean perform(ServerPlayer p, Action a, int creature) {
        if (creature == 0) {
            return MythicNetwork.perform(p, a);
        }
        if (creature < 1 || creature > MythicExpedition.IDS.length) {
            return false;
        }
        CompoundTag n = MythicExpedition.state(p, creature - 1);
        ShadowRecord r = n.m_128403_("Soul") ? GuideService.army((ServerPlayer)p).shadows.get(n.m_128342_("Soul")) : null;
        return switch (a) {
            default -> throw new IncompatibleClassChangeError();
            case Action.OPEN -> {
                GloomRealm.request(p);
                yield true;
            }
            case Action.VIEW -> true;
            case Action.RESUME -> {
                if (MythicExpedition.current(MythicService.session(p)) == creature - 1 && MythicExpedition.resume(p)) {
                    yield true;
                }
                yield false;
            }
            case Action.CANCEL -> {
                if (MythicExpedition.current(MythicService.session(p)) == creature - 1 && MythicService.cancel(p)) {
                    yield true;
                }
                yield false;
            }
            case Action.CLAIM -> {
                if (WildBosses.claimPending(p) > 0) {
                    yield true;
                }
                yield false;
            }
            case Action.SUMMON -> {
                if (r != null && ShadowService.summon(p, r, true)) {
                    yield true;
                }
                yield false;
            }
            case Action.RECALL -> {
                if (r != null && ShadowService.recall(p, r)) {
                    yield true;
                }
                yield false;
            }
            case Action.FOLLOW, Action.STAY -> {
                if (r == null) {
                    yield false;
                }
                SoulPlay.onCommand(p, r);
                r.order = a == Action.FOLLOW ? Order.FOLLOW : Order.STAY;
                ArmyData.get(p.f_8924_).m_77762_();
                yield true;
            }
        };
    }

    private static CompoundTag creatureView(ServerPlayer p, int kind) {
        CompoundTag s = MythicExpedition.state(p, kind);
        CompoundTag n = new CompoundTag();
        int status = s.m_128451_("Status");
        n.m_128405_("Status", status);
        n.m_128405_("Creature", kind + 1);
        n.m_128379_("Generated", s.m_128441_("Lair") || kind < 3 && GloomRealm.state(p).m_128471_("Ready"));
        if (status >= 1) {
            n.m_128356_("Lair", GloomRealm.habitat(p, kind).m_121878_());
        }
        if (MythicExpedition.current(MythicService.session(p)) == kind) {
            n.m_128359_("Phase", MythicService.session((ServerPlayer)p).phase.name());
        }
        if (status == 2 && s.m_128403_("Soul")) {
            ShadowRecord r = GuideService.army((ServerPlayer)p).shadows.get(s.m_128342_("Soul"));
            n.m_128379_("Pending", LivingRealm.state((ServerPlayer)p).pending.containsKey(s.m_128342_("Soul")));
            if (r != null) {
                n.m_128379_("Owned", true);
                n.m_128379_("Active", r.activeEntity != null);
                n.m_128405_("Level", r.level());
                n.m_128356_("Kills", r.totalKills);
                n.m_128350_("Health", r.maxHealth);
                n.m_128359_("Order", r.order.name());
                n.m_128359_("Name", r.name);
                MythicNetwork.bookStats(p, n, r);
            }
        }
        return n;
    }

    public static CompoundTag view(ServerPlayer p) {
        for (ShadowRecord record : GuideService.army((ServerPlayer)p).shadows.values()) {
            MythicPower.upgrade(p, record);
        }
        MythicState s = MythicService.state(p);
        CompoundTag n = new CompoundTag();
        n.m_128405_("Status", s.status);
        n.m_128379_("Generated", s.generated || s.gloom.m_128441_("NaturalDevourer"));
        n.m_128405_("Cursor", s.cursor);
        AdventureData.Session run = MythicService.session(p);
        if (run != null && MythicExpedition.current(run) < 0) {
            n.m_128359_("Phase", run.phase.name());
        }
        if (s.status >= 1) {
            n.m_128356_("Lair", MythicLair.center(p).m_121878_());
            SoulDevourer m = MythicService.body(p);
            if (m != null) {
                n.m_128350_("BossHealth", m.m_21223_());
            }
        }
        if (s.status == 2) {
            ShadowRecord r = s.soul == null ? null : GuideService.army((ServerPlayer)p).shadows.get(s.soul);
            n.m_128379_("Pending", s.soul != null && LivingRealm.state((ServerPlayer)p).pending.containsKey(s.soul));
            if (r != null) {
                n.m_128379_("Owned", true);
                n.m_128379_("Active", r.activeEntity != null);
                n.m_128405_("Level", r.level());
                n.m_128356_("Kills", r.totalKills);
                n.m_128350_("Health", r.maxHealth);
                n.m_128359_("Order", r.order.name());
                n.m_128359_("Name", r.name);
                MythicNetwork.bookStats(p, n, r);
            }
        }
        ListTag entries = new ListTag();
        entries.add((Object)n.m_6426_());
        for (int k = 0; k < MythicExpedition.IDS.length; ++k) {
            entries.add((Object)MythicNetwork.creatureView(p, k));
        }
        n.m_128365_("Monolith", (Tag)MonolithWorld.view(p));
        n.m_128365_("Creatures", (Tag)entries);
        int acquired = 0;
        for (Tag t : entries) {
            if (((CompoundTag)t).m_128451_("Status") != 2) continue;
            ++acquired;
        }
        n.m_128405_("Acquired", acquired);
        n.m_128379_("GloomReady", GloomRealm.state(p).m_128471_("Ready"));
        return n;
    }

    public static void openGrimoire(ServerPlayer p) {
        MythicService.state((ServerPlayer)p).viewing = GuideService.now(p) + 120L;
        CompoundTag n = MythicNetwork.view(p);
        n.m_128379_("GrimoireOpen", true);
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(n, true));
    }

    private static void bookStats(ServerPlayer p, CompoundTag n, ShadowRecord r) {
        if (r == null) {
            return;
        }
        n.m_128362_("Soul", r.id);
        Mob mob = ShadowService.active(p.f_8924_, r);
        n.m_128350_("CurrentHealth", mob == null ? r.health : mob.m_21223_());
        n.m_128350_("MaxHealth", mob == null ? r.maxHealth : mob.m_21233_());
        n.m_128359_("Tier", r.tier.name());
        n.m_128405_("Bond", r.bond);
        n.m_128347_("LevelMultiplier", r.damageMultiplier());
        n.m_128356_("RespawnTicks", r.pendingRespawn() ? Math.max(0L, r.respawnAt - GuideService.now(p)) : -1L);
        n.m_128405_("FreeReturns", Math.max(0, 5 - r.vigil.freeUsed));
        n.m_128405_("ReviveFee", r.vigil.fee);
        n.m_128379_("NeedsPayment", VigilGraves.needsPayment(r));
        n.m_128405_("Deaths", r.vigil.deaths);
        n.m_128359_("Trait", r.trait.name());
        n.m_128379_("Aggressive", r.aggressive);
        n.m_128359_("Dimension", r.dimension);
        ListTag gear = new ListTag();
        r.inventory.initialize(r.snapshot);
        for (ItemStack stack : r.inventory.items) {
            CompoundTag item = new CompoundTag();
            if (!stack.m_41619_()) {
                ResourceLocation key = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
                if (key != null) {
                    item.m_128359_("Item", key.toString());
                }
                item.m_128405_("Count", stack.m_41613_());
                item.m_128405_("Damage", stack.m_41773_());
                item.m_128405_("MaxDamage", stack.m_41776_());
                item.m_128379_("Enchanted", stack.m_41793_());
                String label = stack.m_41786_().getString();
                item.m_128359_("Label", label.substring(0, Math.min(128, label.length())));
            }
            gear.add((Object)item);
        }
        n.m_128365_("Gear", (Tag)gear);
        if (mob != null) {
            n.m_128347_("X", mob.m_20185_());
            n.m_128347_("Y", mob.m_20186_());
            n.m_128347_("Z", mob.m_20189_());
            n.m_128359_("Dimension", mob.m_9236_().m_46472_().m_135782_().toString());
        }
    }

    public static void open(ServerPlayer p) {
        GloomRealm.request(p);
        MythicService.state((ServerPlayer)p).viewing = GuideService.now(p) + 120L;
        MythicNetwork.send(p, true);
    }

    public static void send(ServerPlayer p, boolean open) {
        channel.send(PacketDistributor.PLAYER.with(() -> p), (Object)new View(MythicNetwork.view(p), open));
    }

    public record Request(Action action, int creature) {
        public Request(Action action) {
            this(action, 0);
        }

        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.m_130130_(this.creature);
        }

        public static Request decode(FriendlyByteBuf b) {
            return new Request((Action)b.m_130066_(Action.class), b.m_130242_());
        }

        public void handle(Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> {
                int delay;
                ServerPlayer p = ctx.getSender();
                if (p == null || this.creature < 0 || this.creature > MythicExpedition.IDS.length) {
                    return;
                }
                long now = GuideService.now(p);
                CompoundTag data = p.getPersistentData();
                String clock = this.action == Action.VIEW ? "MythicViewAt" : (this.action == Action.OPEN ? "MythicOpenAt" : "MythicActionAt");
                int n = delay = this.action == Action.VIEW || this.action == Action.OPEN ? 2 : 5;
                if (data.m_128441_(clock) && now - data.m_128454_(clock) < (long)delay) {
                    return;
                }
                data.m_128356_(clock, now);
                MythicService.state((ServerPlayer)p).viewing = now + 120L;
                if (p.m_6084_() && !p.m_5833_()) {
                    MythicNetwork.perform(p, this.action, this.creature);
                }
                MythicNetwork.send(p, this.action == Action.OPEN);
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
            return new View(b.m_130260_(), b.readBoolean());
        }

        public void handle(Supplier<NetworkEvent.Context> c) {
            c.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientMythic.accept(this)));
            c.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        VIEW,
        OPEN,
        RESUME,
        CANCEL,
        SUMMON,
        RECALL,
        FOLLOW,
        STAY,
        CLAIM;

    }
}

