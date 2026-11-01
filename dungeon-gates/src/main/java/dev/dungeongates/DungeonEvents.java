package dev.dungeongates;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class DungeonEvents {
  @SubscribeEvent
  public void death(LivingDeathEvent e) {
    if (e.getEntity() instanceof DungeonMob m && m.kind % 3 == 2) DungeonState.bossDefeated(m);
  }

  @SubscribeEvent
  public void projectile(ProjectileImpactEvent e) {
    var projectile = e.getProjectile();
    var tag = projectile.getPersistentData();
    if (projectile.level().isClientSide
        || !tag.contains("DungeonDamage")
        || !(e.getRayTraceResult() instanceof EntityHitResult hit)) return;
    if (hit.getEntity() instanceof net.minecraft.world.entity.LivingEntity l) {
      l.hurt(
          projectile.damageSources().thrown(projectile, projectile.getOwner()),
          tag.getFloat("DungeonDamage"));
      if (tag.getBoolean("DungeonFrost"))
        l.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60));
    }
  }

  @SubscribeEvent
  public void login(PlayerEvent.PlayerLoggedInEvent e) {
    if (e.getEntity() instanceof ServerPlayer p && p.level().dimension().equals(DungeonState.DIM)) {
      var d = DungeonState.get(p.server);
      var r = d.returns.get(p.getUUID());
      var i = r == null ? null : d.instances.get(r.active);
      if (i == null || i.retired || !i.built || !i.owner.equals(p.getUUID()))
        DungeonState.recover(p);
      else DungeonState.loadChunks(p.serverLevel(), i, true);
    }
  }

  @SubscribeEvent
  public void logout(PlayerEvent.PlayerLoggedOutEvent e) {
    if (e.getEntity() instanceof ServerPlayer p) {
      var d = DungeonState.get(p.server);
      var r = d.returns.get(p.getUUID());
      if (r != null) {
        var i = d.instances.get(r.active);
        if (i != null && p.server.getLevel(DungeonState.DIM) != null)
          DungeonState.loadChunks(p.server.getLevel(DungeonState.DIM), i, false);
      }
    }
  }

  @SubscribeEvent
  public void respawn(PlayerEvent.PlayerRespawnEvent e) {
    if (e.getEntity() instanceof ServerPlayer p) {
      var r = DungeonState.get(p.server).returns.get(p.getUUID());
      if (r != null && r.active != null) DungeonState.recover(p);
    }
  }

  @SubscribeEvent
  public void tick(TickEvent.PlayerTickEvent e) {
    if (e.phase != TickEvent.Phase.END
        || !(e.player instanceof ServerPlayer p)
        || !p.level().dimension().equals(DungeonState.DIM)
        || p.tickCount % 20 != 0) return;
    var d = DungeonState.get(p.server);
    var r = d.returns.get(p.getUUID());
    var i = r == null ? null : d.instances.get(r.active);
    if (i == null || i.retired || !i.owner.equals(p.getUUID())) DungeonState.recover(p);
    else if (!i.contains(p.blockPosition())) {
      var a = i.origin().offset(10, 3, 50);
      p.teleportTo(p.serverLevel(), a.getX() + .5, a.getY(), a.getZ() + .5, 180, 0);
      p.fallDistance = 0;
    }
  }

  @SubscribeEvent
  public void breaking(BlockEvent.BreakEvent e) {
    if (!(e.getLevel() instanceof ServerLevel l)
        || !l.dimension().equals(DungeonState.DIM)
        || e.getPlayer().isCreative()) return;
    var i = DungeonState.at(l, e.getPos());
    if (i == null
        || !i.owner.equals(e.getPlayer().getUUID())
        || e.getState().getBlock() instanceof CrateBlock && !i.complete) e.setCanceled(true);
  }

  @SubscribeEvent
  public void started(net.minecraftforge.event.server.ServerStartedEvent e) {
    var l = e.getServer().getLevel(DungeonState.DIM);
    if (l != null)
      for (var i : DungeonState.get(e.getServer()).instances.values())
        DungeonState.loadChunks(l, i, false);
  }

  @SubscribeEvent
  public void changedDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
    if (e.getEntity() instanceof ServerPlayer p
        && e.getFrom().equals(DungeonState.DIM)
        && !e.getTo().equals(DungeonState.DIM)) {
      var data = DungeonState.get(p.server);
      var ret = data.returns.get(p.getUUID());
      if (ret != null) {
        var i = data.instances.get(ret.active);
        var level = p.server.getLevel(DungeonState.DIM);
        if (i != null && level != null) DungeonState.loadChunks(level, i, false);
        ret.active = null;
        data.setDirty();
      }
    }
  }

  @SubscribeEvent
  public void commands(RegisterCommandsEvent e) {
    e.getDispatcher()
        .register(
            Commands.literal("dgates")
                .then(
                    Commands.literal("status")
                        .executes(
                            c -> {
                              var d = DungeonState.get(c.getSource().getServer());
                              c.getSource()
                                  .sendSuccess(
                                      () ->
                                          Component.literal(
                                              "Dungeon Gates 0.1.0: Emerald Trial + Frozen Grave; "
                                                  + d.instances.size()
                                                  + " persistent instances"),
                                      false);
                              return d.instances.size();
                            }))
                .then(
                    Commands.literal("verify_spawns")
                        .requires(s -> s.hasPermission(2))
                        .executes(
                            c -> {
                              var l = c.getSource().getServer().overworld();
                              int a = 0, b = 0;
                              for (int x = -8; x <= 8; x++)
                                for (int z = -8; z <= 8; z++) {
                                  int
                                      cx =
                                          (c.getSource().getPosition().x > 0
                                                      ? (int) c.getSource().getPosition().x
                                                      : 0)
                                                  / 16
                                              + x,
                                      cz = (int) c.getSource().getPosition().z / 16 + z;
                                  var ch = l.getChunkSource().getChunkNow(cx, cz);
                                  if (ch == null) continue;
                                  for (var be : ch.getBlockEntities().values())
                                    if (be instanceof GateAnchor g && !g.exit) {
                                      if (g.kind() == GateKind.EMERALD) a++;
                                      else b++;
                                    }
                                }
                              String message =
                                  "Loaded Overworld gates near source: Emerald="
                                      + a
                                      + ", Frozen="
                                      + b
                                      + ". This is an observation, not a claim about ungenerated"
                                      + " chunks. spacing="
                                      + DGConfig.SPACING.get()
                                      + ", chance="
                                      + DGConfig.FREQUENCY.get();
                              c.getSource().sendSuccess(() -> Component.literal(message), false);
                              return a + b;
                            }))
                .then(
                    Commands.literal("extract")
                        .requires(s -> s.hasPermission(2))
                        .executes(
                            c -> {
                              DungeonState.recover(c.getSource().getPlayerOrException());
                              return 1;
                            }))
                .then(
                    Commands.literal("rescue")
                        .requires(s -> s.hasPermission(2))
                        .then(
                            Commands.argument(
                                    "player",
                                    net.minecraft.commands.arguments.EntityArgument.player())
                                .executes(
                                    c -> {
                                      DungeonState.recover(
                                          net.minecraft.commands.arguments.EntityArgument.getPlayer(
                                              c, "player"));
                                      return 1;
                                    })))
                .then(
                    Commands.literal("repair")
                        .requires(source -> source.hasPermission(2))
                        .executes(
                            c -> {
                              var p = c.getSource().getPlayerOrException();
                              var data = DungeonState.get(p.server);
                              var ret = data.returns.get(p.getUUID());
                              var i = ret == null ? null : data.instances.get(ret.active);
                              if (i == null || i.retired || i.complete) {
                                c.getSource()
                                    .sendFailure(
                                        Component.literal(
                                            "Repair requires your active, incomplete private"
                                                + " dungeon."));
                                return 0;
                              }
                              i.built = false;
                              data.setDirty();
                              var l = p.server.getLevel(DungeonState.DIM);
                              DungeonState.loadChunks(l, i, true);
                              DungeonState.build(l, i);
                              var at = i.origin().offset(10, 3, 50);
                              p.teleportTo(l, at.getX() + .5, at.getY(), at.getZ() + .5, 180, 0);
                              c.getSource()
                                  .sendSuccess(
                                      () ->
                                          Component.literal(
                                              "Rebuilt incomplete dungeon; stale generation bodies"
                                                  + " are rejected."),
                                      false);
                              return 1;
                            }))
                .then(
                    Commands.literal("cleanup")
                        .requires(s -> s.hasPermission(2))
                        .executes(
                            c -> {
                              var d = DungeonState.get(c.getSource().getServer());
                              for (var i : d.instances.values())
                                if (i.complete && i.claimed && !i.retired) {
                                  var online =
                                      c.getSource().getServer().getPlayerList().getPlayer(i.owner);
                                  var r = d.returns.get(i.owner);
                                  if (online != null && r != null && i.id.equals(r.active))
                                    continue;
                                  var l = c.getSource().getServer().getLevel(DungeonState.DIM);
                                  DungeonState.loadChunks(l, i, true);
                                  for (var mob :
                                      l.getEntitiesOfClass(
                                          DungeonMob.class,
                                          new net.minecraft.world.phys.AABB(
                                              i.origin(), i.origin().offset(64, 28, 64))))
                                    if (i.id.equals(mob.instance)) mob.discard();
                                  for (var p :
                                      net.minecraft.core.BlockPos.betweenClosed(
                                          i.origin(), i.origin().offset(63, 27, 63)))
                                    l.setBlock(
                                        p,
                                        net.minecraft.world.level.block.Blocks.AIR
                                            .defaultBlockState(),
                                        18);
                                  DungeonState.loadChunks(l, i, false);
                                  i.retired = true;
                                  d.setDirty();
                                  c.getSource()
                                      .sendSuccess(
                                          () ->
                                              Component.literal(
                                                  "Cleaned completed instance "
                                                      + i.id
                                                      + "; slot is not reused."),
                                          false);
                                  return 1;
                                }
                              c.getSource()
                                  .sendSuccess(
                                      () ->
                                          Component.literal(
                                              "No eligible completed inactive instance."),
                                      false);
                              return 0;
                            })));
  }
}
