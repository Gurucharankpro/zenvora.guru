/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.BlockSnapshot
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.living.LivingConversionEvent$Pre
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.living.MobSpawnEvent$FinalizeSpawn
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 *  net.minecraftforge.event.level.BlockEvent$EntityPlaceEvent
 *  net.minecraftforge.event.level.ExplosionEvent$Detonate
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.realm;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.network.RealmNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.realm.RealmTerrain;
import dev.shadowsovereign.realm.WildBosses;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class RealmEvents {
    private static final ThreadLocal<BlockSnapshot> placement = new ThreadLocal();
    private static final ThreadLocal<BlockEvent.BreakEvent> authorized = new ThreadLocal();

    public static boolean authorizedPlace(BlockEvent.EntityPlaceEvent e) {
        return placement.get() == e.getBlockSnapshot();
    }

    public static boolean placeVeto(ServerPlayer p, BlockSnapshot s) {
        placement.set(s);
        try {
            boolean bl = ForgeEventFactory.onBlockPlace((Entity)p, (BlockSnapshot)s, (Direction)Direction.UP);
            return bl;
        }
        finally {
            placement.remove();
        }
    }

    public static boolean authorized(BlockEvent.BreakEvent e) {
        return authorized.get() == e;
    }

    public static boolean veto(BlockEvent.BreakEvent e) {
        authorized.set(e);
        try {
            boolean bl = MinecraftForge.EVENT_BUS.post((Event)e);
            return bl;
        }
        finally {
            authorized.remove();
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void spawn(MobSpawnEvent.FinalizeSpawn e) {
        if (!(e.isSpawnCancelled() || e.getSpawnType() != MobSpawnType.NATURAL && e.getSpawnType() != MobSpawnType.CHUNK_GENERATION)) {
            e.getEntity().getPersistentData().m_128359_("RealmSpawnReason", e.getSpawnType().name());
            e.getEntity().getPersistentData().m_128405_("RealmSpawnRoll", e.getEntity().m_217043_().m_188503_(100));
        }
    }

    @SubscribeEvent
    public static void join(EntityJoinLevelEvent e) {
        Projectile shot;
        Entity entity;
        if (!e.getLevel().f_46443_ && (entity = e.getEntity()) instanceof Mob) {
            Mob m = (Mob)entity;
            CompoundTag n = m.getPersistentData();
            if (n.m_128441_("RealmSpawnReason")) {
                try {
                    WildBosses.natural(m, MobSpawnType.valueOf((String)n.m_128461_("RealmSpawnReason")), n.m_128451_("RealmSpawnRoll"));
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
                n.m_128473_("RealmSpawnReason");
                n.m_128473_("RealmSpawnRoll");
            }
            WildBosses.install(m);
        }
        if (!e.getLevel().f_46443_ && (entity = e.getEntity()) instanceof Projectile && (shot = (Projectile)entity).m_19749_() != null && WildBosses.boss(shot.m_19749_())) {
            shot.getPersistentData().m_128379_("WildBossShot", true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void hurt(LivingHurtEvent e) {
        if (e.getEntity().m_9236_().f_46443_) {
            return;
        }
        Entity direct = e.getSource().m_7640_();
        if (direct instanceof Projectile && direct.getPersistentData().m_128471_("WildBossShot")) {
            e.setAmount(e.getAmount() * 2.0f);
        }
    }

    @SubscribeEvent
    public static void explosion(ExplosionEvent.Detonate e) {
        Entity direct = e.getExplosion().m_253049_();
        LivingEntity indirect = e.getExplosion().m_252906_();
        if (direct != null && WildBosses.boss(direct) || indirect != null && WildBosses.boss((Entity)indirect)) {
            e.getAffectedBlocks().clear();
        }
    }

    @SubscribeEvent
    public static void convert(LivingConversionEvent.Pre e) {
        if (WildBosses.boss((Entity)e.getEntity()) && !ShadowService.isShadow((Entity)e.getEntity())) {
            e.setCanceled(true);
            e.setConversionTimer(600);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        for (ServerPlayer p : e.getServer().m_6846_().m_11314_()) {
            LivingRealm.tick(p);
            if (GuideService.now(p) % 40L != 0L) continue;
            WildBosses.observe(p);
            RealmNetwork.send(p, false, 0);
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            LivingRealm.state((ServerPlayer)p).sites.m_128473_("ViewingUntil");
            AdventureWorld.pause(p.f_8924_, p.m_20148_());
        }
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent e) {
        e.getDispatcher().register((LiteralArgumentBuilder)Commands.m_82127_((String)"shadow").then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"kingdom").executes(c -> {
            RealmNetwork.open(((CommandSourceStack)c.getSource()).m_81375_(), 0);
            return 1;
        })).then(Commands.m_82127_((String)"bestiary").executes(c -> {
            RealmNetwork.open(((CommandSourceStack)c.getSource()).m_81375_(), 1);
            return 1;
        }))).then(Commands.m_82127_((String)"activity").executes(c -> {
            RealmNetwork.open(((CommandSourceStack)c.getSource()).m_81375_(), 3);
            return 1;
        }))));
    }

    @SubscribeEvent
    public static void interact(PlayerInteractEvent.RightClickBlock e) {
        ServerPlayer p;
        Player player = e.getEntity();
        if (player instanceof ServerPlayer && OdysseyRealm.inside(p = (ServerPlayer)player) && LivingRealm.state((ServerPlayer)p).ready) {
            for (int i = 0; i < 4; ++i) {
                if (!e.getPos().equals((Object)RealmTerrain.stone(p, i))) continue;
                LivingRealm.discover(p, i);
                e.setCanceled(true);
                e.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }
        }
    }
}

