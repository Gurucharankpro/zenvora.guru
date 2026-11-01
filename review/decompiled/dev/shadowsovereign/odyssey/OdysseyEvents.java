/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Snowball
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.ProjectileImpactEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$ItemCraftedEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 *  net.minecraftforge.event.level.BlockEvent$EntityPlaceEvent
 *  net.minecraftforge.event.server.ServerStoppedEvent
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureContent;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.expanse.ExpanseWorld;
import dev.shadowsovereign.network.OdysseyNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyBosses;
import dev.shadowsovereign.odyssey.OdysseyCamp;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.odyssey.OdysseyTrials;
import dev.shadowsovereign.odyssey.OdysseyVisitors;
import dev.shadowsovereign.realm.RealmEvents;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class OdysseyEvents {
    private static final ThreadLocal<BlockEvent.BreakEvent> authorizedBreak = new ThreadLocal();
    private static final LinkedHashMap<UUID, ResourceKey<Level>> items = new LinkedHashMap();

    static boolean mineVeto(BlockEvent.BreakEvent event) {
        authorizedBreak.set(event);
        try {
            boolean bl = MinecraftForge.EVENT_BUS.post((Event)event);
            return bl;
        }
        finally {
            authorizedBreak.remove();
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        OdysseyRuns.keepFrozen(event.getServer());
        for (ServerPlayer serverPlayer : event.getServer().m_6846_().m_11314_()) {
            if (GuideService.now(serverPlayer) % 20L == 0L) {
                GuideService.deliverBook(serverPlayer);
                GuideService.importEvidence(serverPlayer);
            }
            OdysseyRealm.portalTick(serverPlayer);
            OdysseyRealm.buildTick(serverPlayer);
            OdysseyRealm.arrivals(serverPlayer);
            OdysseyRealm.safety(serverPlayer);
            OdysseyRuns.resumeRequested(serverPlayer);
            AdventureData.Session activeRun = OdysseyRuns.session(serverPlayer);
            if (activeRun != null && activeRun.phase == AdventureData.Phase.ACTIVE) {
                OdysseyRuns.tick(serverPlayer, activeRun);
            }
            OdysseyCamp.tick(serverPlayer);
            OdysseyVisitors.tick(serverPlayer);
            OdysseyState state = GuideService.state(serverPlayer);
            if (GuideService.now(serverPlayer) % 20L == 0L && (state.viewUntil > GuideService.now(serverPlayer) || GuideService.now(serverPlayer) % 40L == 0L && (state.pinned >= 0 || OdysseyRuns.session(serverPlayer) != null))) {
                OdysseyNetwork.send(serverPlayer, false, 0);
            }
            if (!OdysseyRealm.inside(serverPlayer) || !state.plotReady || !OdysseyRealm.foreignPlot(serverPlayer, serverPlayer.m_20183_())) continue;
            OdysseyRealm.leave(serverPlayer);
        }
        for (Map.Entry entry : new ArrayList<Map.Entry<UUID, ResourceKey<Level>>>(items.entrySet())) {
            Entity entity;
            ServerLevel world = event.getServer().m_129880_((ResourceKey)entry.getValue());
            if (world == null || !((entity = world.m_8791_((UUID)entry.getKey())) instanceof ItemEntity)) continue;
            ItemEntity item = (ItemEntity)entity;
            if (!item.getPersistentData().m_128403_("OdysseyScene")) {
                items.remove(entry.getKey());
                continue;
            }
            boolean active = false;
            for (AdventureData.Journal journal : AdventureData.get((MinecraftServer)event.getServer()).owners.values()) {
                AdventureData.Session s = journal.session;
                if (s == null || s.kind != AdventureData.Kind.ODYSSEY || !s.id.equals(item.getPersistentData().m_128342_("OdysseyScene"))) continue;
                active = true;
                break;
            }
            if (!active) {
                item.m_8127_();
                item.m_32010_(0);
                item.m_32064_();
                item.getPersistentData().m_128473_("OdysseyScene");
            }
            items.remove(entry.getKey());
        }
    }

    @SubscribeEvent
    public static void join(EntityJoinLevelEvent e) {
        ItemEntity item;
        Entity entity;
        if (!e.getLevel().f_46443_ && e.getEntity().getPersistentData().m_128403_("OdysseyHost")) {
            UUID host = e.getEntity().getPersistentData().m_128342_("OdysseyHost");
            CompoundTag camp = AdventureData.get((MinecraftServer)e.getLevel().m_7654_()).journal((UUID)host).odyssey.camp;
            if (!(camp.m_128471_("VisitorActive") && camp.m_128403_("VisitorEntity") && camp.m_128342_("VisitorEntity").equals(e.getEntity().m_20148_()))) {
                e.setCanceled(true);
                e.getEntity().m_146870_();
                return;
            }
        }
        if (!e.getLevel().f_46443_ && (entity = e.getEntity()) instanceof ItemEntity && OdysseyRuns.reserved(item = (ItemEntity)entity) && items.size() < 1024) {
            items.put(item.m_20148_(), (ResourceKey<Level>)item.m_9236_().m_46472_());
        }
    }

    @SubscribeEvent
    public static void stop(ServerStoppedEvent e) {
        OdysseyBosses.clearBars();
        items.clear();
        OdysseyRealm.clearContacts();
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            GuideService.state((ServerPlayer)p).lastView = null;
            GuideService.state((ServerPlayer)p).camp.m_128379_("WelcomePending", GuideService.state(p).count("realm_enter") > 0L);
            GuideService.deliverBook(p);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            OdysseyRealm.clearContact(p.m_20148_());
            AdventureData.Session s = OdysseyRuns.session(p);
            if (s != null) {
                OdysseyBosses.clearBar(s.id);
                OdysseyRuns.pause(p);
            }
            GuideService.state((ServerPlayer)p).lastView = null;
            GuideService.state((ServerPlayer)p).viewUntil = 0L;
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void hurt(LivingHurtEvent e) {
        if (e.getEntity().m_9236_().f_46443_ || !AdventureWorld.isNpc((Entity)e.getEntity())) {
            return;
        }
        ServerPlayer p = AdventureWorld.owner((Entity)e.getEntity());
        AdventureData.Session s = AdventureWorld.session((Entity)e.getEntity());
        if (p != null && s != null && s.kind == AdventureData.Kind.ODYSSEY && OdysseyRuns.mode(s) != null) {
            CompoundTag data;
            Entity source = e.getSource().m_7639_();
            Entity direct = e.getSource().m_7640_();
            CompoundTag compoundTag = source != null && source.getPersistentData().m_128403_("ShadowSovereignRecord") ? source.getPersistentData() : (data = direct != null ? direct.getPersistentData() : new CompoundTag());
            if (data.m_128403_("ShadowSovereignRecord") && !s.participants.contains(data.m_128342_("ShadowSovereignRecord"))) {
                e.setCanceled(true);
                return;
            }
            if (OdysseyRuns.mode(s).boss()) {
                OdysseyBosses.damage(e, p, s);
            }
        }
    }

    @SubscribeEvent
    public static void impact(ProjectileImpactEvent e) {
        LivingEntity victim;
        Snowball snow;
        Entity entity;
        block5: {
            block4: {
                EntityHitResult hit;
                if (e.getEntity().m_9236_().f_46443_ || !((entity = e.getEntity()) instanceof Snowball) || !(snow = (Snowball)entity).getPersistentData().m_128403_("OdysseyChef") || !((entity = e.getRayTraceResult()) instanceof EntityHitResult) || !((entity = (hit = (EntityHitResult)entity).m_82443_()) instanceof LivingEntity)) break block4;
                victim = (LivingEntity)entity;
                entity = snow.m_19749_();
                if (entity instanceof Mob) break block5;
            }
            return;
        }
        Mob boss = (Mob)entity;
        ServerPlayer p = AdventureWorld.owner((Entity)boss);
        AdventureData.Session s = AdventureWorld.session((Entity)boss);
        if (p != null && s != null && (victim == p || p.m_20148_().equals(ShadowService.ownerId((Entity)victim)))) {
            victim.m_6469_(victim.m_269291_().m_269390_((Entity)snow, (Entity)boss), 4.0f);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void ignite(PlayerInteractEvent.RightClickBlock e) {
        ServerPlayer p;
        block9: {
            block8: {
                Player player = e.getEntity();
                if (!(player instanceof ServerPlayer)) break block8;
                p = (ServerPlayer)player;
                if (e.getUseBlock() != Event.Result.DENY && e.getUseItem() != Event.Result.DENY) break block9;
            }
            return;
        }
        ItemStack held = p.m_21120_(e.getHand());
        if (!held.m_150930_(Items.f_42409_) && !held.m_150930_((Item)AdventureContent.COMPASS.get())) {
            return;
        }
        BlockPos clicked = e.getPos();
        for (int y = -4; y <= 1; ++y) {
            for (int dx = -3; dx <= 3; ++dx) {
                for (int dz = -3; dz <= 3; ++dz) {
                    if (dx != 0 && dz != 0) continue;
                    BlockPos core = clicked.m_7918_(dx, y, dz);
                    if (!p.m_284548_().m_46805_(core) || !p.m_9236_().m_8055_(core).m_60713_((Block)OdysseyContent.STATIONS.get((Object)OdysseyContent.Station.GATE).get())) continue;
                    boolean activated = OdysseyRealm.activate(p, core);
                    if (activated && held.m_150930_(Items.f_42409_) && !p.m_7500_()) {
                        held.m_41622_(1, (LivingEntity)p, user -> user.m_21190_(e.getHand()));
                    }
                    e.setCanceled(true);
                    e.setCancellationResult(activated ? InteractionResult.SUCCESS : InteractionResult.CONSUME);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void crafted(PlayerEvent.ItemCraftedEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            ResourceLocation key = ForgeRegistries.ITEMS.getKey((Object)e.getCrafting().m_41720_());
            if (key != null && key.m_135827_().equals("shadowsovereign")) {
                GuideService.record(p, "craft." + key.m_135815_());
            }
        }
    }

    @SubscribeEvent
    public static void placed(BlockEvent.EntityPlaceEvent e) {
        ServerPlayer p;
        Entity entity = e.getEntity();
        if (entity instanceof ServerPlayer && OdysseyRealm.inside(p = (ServerPlayer)entity) && !ExpanseWorld.editable(p, e.getPos()) && !RealmEvents.authorizedPlace(e)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void broken(BlockEvent.BreakEvent e) {
        Player player = e.getPlayer();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer p = (ServerPlayer)player;
        BlockEntity blockEntity = e.getLevel().m_7702_(e.getPos());
        if (blockEntity instanceof OdysseyContent.CampEntity) {
            OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)blockEntity;
            if (be.owner != null && !p.m_20148_().equals(be.owner)) {
                e.setCanceled(true);
                return;
            }
        }
        if (OdysseyRealm.inside(p) && !ExpanseWorld.editable(p, e.getPos()) && authorizedBreak.get() != e && !RealmEvents.authorized(e)) {
            e.setCanceled(true);
            AdventureData.Session s = OdysseyRuns.session(p);
            if (s != null && OdysseyRuns.mode(s) == OdysseyCatalog.Mode.MINE) {
                for (int i = 0; i < 16; ++i) {
                    if (!OdysseyTrials.ore(s, i).equals((Object)e.getPos())) continue;
                    OdysseyRuns.input(p, 0, i, s.journey.m_128451_("Turn"));
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void interact(PlayerInteractEvent.EntityInteract e) {
        Player player;
        if (e.getTarget().getPersistentData().m_128403_("OdysseyHost") && (player = e.getEntity()) instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            e.setCanceled(true);
            if (p.m_20148_().equals(e.getTarget().getPersistentData().m_128342_("OdysseyHost"))) {
                OdysseyNetwork.open(p, 3);
            }
        }
    }
}

