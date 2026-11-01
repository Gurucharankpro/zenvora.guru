/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package dev.shadowsovereign.network;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.SoulService;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.network.AdventureNetwork;
import dev.shadowsovereign.network.AscensionNetwork;
import dev.shadowsovereign.network.AtlasNetwork;
import dev.shadowsovereign.network.BattleNetwork;
import dev.shadowsovereign.network.EclipseNetwork;
import dev.shadowsovereign.network.JourneyNetwork;
import dev.shadowsovereign.network.LegacyNetwork;
import dev.shadowsovereign.network.MasteryNetwork;
import dev.shadowsovereign.network.MythicFlightNetwork;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.network.OdysseyNetwork;
import dev.shadowsovereign.network.RealmNetwork;
import dev.shadowsovereign.network.RoyalNetwork;
import dev.shadowsovereign.network.SentinelNetwork;
import dev.shadowsovereign.network.SoulLifeNetwork;
import dev.shadowsovereign.network.SovereignNetwork;
import dev.shadowsovereign.network.VigilNetwork;
import dev.shadowsovereign.network.WarfrontNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.soulforge.CompanionMenu;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.soulforge.SoulTier;
import dev.shadowsovereign.soulforge.SoulTrait;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.warfront.WarfrontPlans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ShadowNetwork {
    private static final String VERSION = "37-shadow-awakening";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("shadowsovereign", "army"), () -> "37-shadow-awakening", "37-shadow-awakening"::equals, "37-shadow-awakening"::equals);

    public static void register() {
        CHANNEL.registerMessage(0, Request.class, Request::encode, Request::decode, Request::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(1, View.class, View::encode, View::decode, View::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(2, Appearance.class, Appearance::encode, Appearance::decode, Appearance::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        BattleNetwork.register(CHANNEL);
        AdventureNetwork.register(CHANNEL);
        LegacyNetwork.register(CHANNEL);
        JourneyNetwork.register(CHANNEL);
        AscensionNetwork.register(CHANNEL);
        WarfrontNetwork.register(CHANNEL);
        SovereignNetwork.register(CHANNEL);
        EclipseNetwork.register(CHANNEL);
        RoyalNetwork.register(CHANNEL);
        VigilNetwork.register(CHANNEL);
        SoulLifeNetwork.register(CHANNEL);
        OdysseyNetwork.register(CHANNEL);
        RealmNetwork.register(CHANNEL);
        MythicNetwork.register(CHANNEL);
        MythicFlightNetwork.register(CHANNEL);
        AtlasNetwork.register(CHANNEL);
        MasteryNetwork.register(CHANNEL);
        SentinelNetwork.register(CHANNEL);
    }

    public static boolean allowRequest(ServerPlayer p, Action action) {
        String key = switch (action) {
            case Action.OPEN -> "SoulboundLastArmyOpen";
            case Action.REFRESH -> "SoulboundLastArmyRefresh";
            default -> "ShadowLastPacket";
        };
        long now = p.f_8924_.m_129783_().m_46467_();
        long previous = p.getPersistentData().m_128454_(key);
        if (p.getPersistentData().m_128441_(key) && now >= previous && now - previous < 2L) {
            return false;
        }
        p.getPersistentData().m_128356_(key, now);
        return true;
    }

    public static void request(Action action, UUID id) {
        CHANNEL.sendToServer((Object)new Request(action, id));
    }

    public static void appearance(ServerPlayer player, Entity entity) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), (Object)new Appearance(entity.m_20148_()));
    }

    public static void sendArmy(ServerPlayer player, boolean open) {
        ArmyData.Army army = ArmyData.get(player.f_8924_).army(player.m_20148_());
        List<Entry> entries = army.shadows.values().stream().map(r -> {
            Mob mob = ShadowService.active(player.f_8924_, r);
            return new Entry(r.id, LegacyService.name(player, r), r.type, r.order, r.aggressive, r.activeEntity != null, mob != null ? mob.m_21223_() : r.health, mob != null ? mob.m_21233_() : r.maxHealth, r.totalKills, r.level(), r.pendingRespawn() ? (int)Math.min(Integer.MAX_VALUE, Math.max(0L, r.respawnAt - player.f_8924_.m_129783_().m_46467_())) : -1, r.tier, r.trait, r.bond);
        }).toList();
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), (Object)new View(entries, army.autoRecruit, army.autoSummon, (Integer)ShadowConfig.MAX_ARMY.get(), (Integer)ShadowConfig.MAX_ACTIVE.get(), open));
        BattleNetwork.send(player);
        LegacyNetwork.send(player, false, null);
        AscensionNetwork.send(player, false);
    }

    public static int perform(ServerPlayer player, Action action, UUID id) {
        List<ShadowRecord> records;
        ArmyData data = ArmyData.get(player.f_8924_);
        ArmyData.Army army = data.army(player.m_20148_());
        if (action == Action.OPEN || action == Action.REFRESH) {
            return 0;
        }
        if (action == Action.AUTO_RECRUIT) {
            SoulService.cycleMode(player);
            data.m_77762_();
            return 1;
        }
        if (action == Action.AUTO_SUMMON) {
            army.autoSummon = !army.autoSummon;
            data.m_77762_();
            return 1;
        }
        if (action == Action.INVENTORY) {
            return id != null && CompanionService.open(player, id) ? 1 : 0;
        }
        if (action == Action.RELEASE && id == null) {
            return 0;
        }
        if (action == Action.RECALL || action == Action.RELEASE) {
            EclipseService.cancelRelay(player, id);
        }
        List<ShadowRecord> list = id == null ? List.copyOf(army.shadows.values()) : (records = army.shadows.containsKey(id) ? List.of(army.shadows.get(id)) : List.of());
        if (action == Action.ATTACK) {
            WarfrontPlans.stop(player);
            return ShadowNetwork.attackLookingAt(player, records);
        }
        int changed = 0;
        for (ShadowRecord r : records) {
            boolean success = true;
            switch (action) {
                case SUMMON: {
                    if (army.activeCount() >= (long)((Integer)ShadowConfig.MAX_ACTIVE.get()).intValue()) {
                        ShadowService.tell(player, "Active limit reached: " + String.valueOf(ShadowConfig.MAX_ACTIVE.get()) + ". Other shadows remain in reserve.");
                        return changed;
                    }
                    success = ShadowService.summon(player, r, true);
                    break;
                }
                case RECALL: {
                    success = ShadowService.recall(player, r);
                    break;
                }
                case FOLLOW: {
                    ShadowService.setOrder(player, r, Order.FOLLOW);
                    break;
                }
                case STAY: {
                    ShadowService.setOrder(player, r, Order.STAY);
                    break;
                }
                case GUARD: {
                    ShadowService.setOrder(player, r, Order.GUARD);
                    break;
                }
                case ASSIST: 
                case AGGRESSIVE: {
                    r.aggressive = action == Action.AGGRESSIVE;
                    Mob mob = ShadowService.active(player.f_8924_, r);
                    if (mob == null) break;
                    mob.m_6710_(null);
                    break;
                }
                case RELEASE: {
                    AbstractContainerMenu abstractContainerMenu = player.f_36096_;
                    if (abstractContainerMenu instanceof CompanionMenu) {
                        CompanionMenu menu = (CompanionMenu)abstractContainerMenu;
                        if (menu.companionId.equals(r.id)) {
                            player.m_6915_();
                        }
                    }
                    AdventureService.beforeRelease(player, r.id);
                    ShadowService.recall(player, r);
                    CompanionService.returnItems(player, r);
                    SoulLifeService.released(player, r);
                    army.shadows.remove(r.id);
                    RoyalService.released(player, r.id);
                    army.eclipse.builds.remove(r.id);
                    army.eclipse.builds.values().forEach(b -> {
                        if (r.id.equals(b.backup)) {
                            b.backup = null;
                        }
                    });
                    if (!r.id.equals(army.eclipse.selected)) break;
                    army.eclipse.selected = null;
                    break;
                }
                default: {
                    success = false;
                }
            }
            if (!success) continue;
            ++changed;
            GuideService.record(player, (String)(action == Action.RECALL ? "recalled" : "order." + action.name().toLowerCase(Locale.ROOT)));
        }
        if (changed > 0) {
            data.m_77762_();
        }
        return changed;
    }

    public static int attackLookingAt(ServerPlayer player, Collection<ShadowRecord> records) {
        LivingEntity target;
        Entity entity2;
        WarfrontPlans.stop(player);
        Vec3 start = player.m_146892_();
        Vec3 end = start.m_82549_(player.m_20154_().m_82490_(48.0));
        EntityHitResult hit = ProjectileUtil.m_37287_((Entity)player, (Vec3)start, (Vec3)end, (AABB)player.m_20191_().m_82369_(end.m_82546_(start)).m_82400_(1.0), entity -> {
            LivingEntity living;
            return entity instanceof LivingEntity && ShadowService.canAttack(player, living = (LivingEntity)entity);
        }, (double)2304.0);
        if (hit == null || !((entity2 = hit.m_82443_()) instanceof LivingEntity) || !player.m_142582_((Entity)(target = (LivingEntity)entity2))) {
            ShadowService.tell(player, "Look at an enemy within 48 blocks, then use /shadow attack.");
            return 0;
        }
        int count = 0;
        for (ShadowRecord r : records) {
            Mob mob = ShadowService.active(player.f_8924_, r);
            if (mob == null || mob.m_9236_() != player.m_9236_()) continue;
            ShadowService.setOrder(player, r, Order.FOLLOW);
            r.battle.commandedTarget = target.m_20148_();
            mob.m_6710_(target);
            ++count;
        }
        ShadowService.tell(player, count + " shadows ordered to attack " + target.m_7755_().getString() + ".");
        return count;
    }

    public record Request(Action action, UUID shadow) {
        public void encode(FriendlyByteBuf b) {
            b.m_130068_((Enum)this.action);
            b.writeBoolean(this.shadow != null);
            if (this.shadow != null) {
                b.m_130077_(this.shadow);
            }
        }

        public static Request decode(FriendlyByteBuf b) {
            return new Request((Action)b.m_130066_(Action.class), b.readBoolean() ? b.m_130259_() : null);
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            NetworkEvent.Context ctx = context.get();
            ctx.enqueueWork(() -> {
                ServerPlayer p = ctx.getSender();
                if (p == null) {
                    return;
                }
                if (!ShadowNetwork.allowRequest(p, this.action)) {
                    return;
                }
                ShadowNetwork.perform(p, this.action, this.shadow);
                ShadowNetwork.sendArmy(p, this.action == Action.OPEN);
            });
            ctx.setPacketHandled(true);
        }
    }

    public record View(List<Entry> entries, boolean autoRecruit, boolean autoSummon, int maxArmy, int maxActive, boolean open) {
        public void encode(FriendlyByteBuf b) {
            b.m_130130_(this.entries.size());
            this.entries.forEach(e -> e.encode(b));
            b.writeBoolean(this.autoRecruit);
            b.writeBoolean(this.autoSummon);
            b.m_130130_(this.maxArmy);
            b.m_130130_(this.maxActive);
            b.writeBoolean(this.open);
        }

        public static View decode(FriendlyByteBuf b) {
            int size = b.m_130242_();
            if (size < 0 || size > 1024) {
                throw new IllegalArgumentException("Invalid army size");
            }
            ArrayList<Entry> entries = new ArrayList<Entry>(size);
            for (int i = 0; i < size; ++i) {
                entries.add(Entry.decode(b));
            }
            return new View(List.copyOf(entries), b.readBoolean(), b.readBoolean(), b.m_130242_(), b.m_130242_(), b.readBoolean());
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientArmy.accept(this)));
            context.get().setPacketHandled(true);
        }
    }

    public record Appearance(UUID entity) {
        public void encode(FriendlyByteBuf b) {
            b.m_130077_(this.entity);
        }

        public static Appearance decode(FriendlyByteBuf b) {
            return new Appearance(b.m_130259_());
        }

        public void handle(Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ClientArmy.shadows.add(this.entity)));
            context.get().setPacketHandled(true);
        }
    }

    public static enum Action {
        OPEN,
        REFRESH,
        SUMMON,
        RECALL,
        FOLLOW,
        STAY,
        GUARD,
        ASSIST,
        AGGRESSIVE,
        RELEASE,
        AUTO_RECRUIT,
        AUTO_SUMMON,
        ATTACK,
        INVENTORY;

    }

    public record Entry(UUID id, String name, String type, Order order, boolean aggressive, boolean active, float health, float maxHealth, long kills, int level, int respawnTicks, SoulTier tier, SoulTrait trait, int bond) {
        public Entry(UUID id, String name, String type, Order order, boolean aggressive, boolean active, float health, float maxHealth, long kills, int level, int respawnTicks) {
            this(id, name, type, order, aggressive, active, health, maxHealth, kills, level, respawnTicks, SoulTier.COMMON, SoulTrait.NONE, 0);
        }

        void encode(FriendlyByteBuf b) {
            b.m_130077_(this.id);
            b.m_130072_(this.name, 128);
            b.m_130072_(this.type, 256);
            b.m_130068_((Enum)this.order);
            b.writeBoolean(this.aggressive);
            b.writeBoolean(this.active);
            b.writeFloat(this.health);
            b.writeFloat(this.maxHealth);
            b.m_130103_(this.kills);
            b.m_130130_(this.level);
            b.writeInt(this.respawnTicks);
            b.m_130068_((Enum)this.tier);
            b.m_130068_((Enum)this.trait);
            b.m_130130_(this.bond);
        }

        static Entry decode(FriendlyByteBuf b) {
            return new Entry(b.m_130259_(), b.m_130136_(128), b.m_130136_(256), (Order)b.m_130066_(Order.class), b.readBoolean(), b.readBoolean(), b.readFloat(), b.readFloat(), b.m_130258_(), b.m_130242_(), b.readInt(), (SoulTier)b.m_130066_(SoulTier.class), (SoulTrait)b.m_130066_(SoulTrait.class), b.m_130242_());
        }
    }
}

