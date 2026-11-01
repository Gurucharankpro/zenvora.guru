package dev.dungeongates.qa;

import dev.dungeongates.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ClientQA {
  int stage, ticks, last;
  String save;
  CompletableFuture<?> work;
  UUID emerald, frozen;

  void check(boolean b, String m) {
    if (!b) throw new IllegalStateException("DGATES CLIENT FAILED " + m);
  }

  void next(int n) {
    stage = n;
    last = ticks;
    Minecraft.getInstance().getToasts().clear();
  }

  void job(Minecraft m, java.util.function.Consumer<ServerPlayer> c) {
    work =
        m.getSingleplayerServer()
            .submit(
                () ->
                    c.accept(
                        m.getSingleplayerServer().getPlayerList().getPlayer(m.player.getUUID())));
  }

  void shot(Minecraft m, String name) {
    try (var png = Screenshot.takeScreenshot(m.getMainRenderTarget())) {
      var dir = java.nio.file.Path.of("screenshots");
      java.nio.file.Files.createDirectories(dir);
      png.writeToFile(dir.resolve(name + ".png"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SubscribeEvent
  public void tick(net.minecraftforge.event.TickEvent.ClientTickEvent e) {
    if (e.phase != net.minecraftforge.event.TickEvent.Phase.END
        || !Boolean.getBoolean("dungeongates.clientqa")) return;
    var m = Minecraft.getInstance();
    ticks++;
    if (ticks % 200 == 0)
      System.out.println(
          "DGATES CLIENT STAGE "
              + stage
              + " screen="
              + (m.screen == null ? "none" : m.screen.getTitle().getString()));
    if (work != null) {
      if (!work.isDone()) return;
      work.join();
      work = null;
    }
    if (m.player == null && m.screen != null)
      for (var child : m.screen.children())
        if (child instanceof net.minecraft.client.gui.components.Button b
            && (b.getMessage().getString().equals("Proceed")
                || b.getMessage().getString().equals("Continue"))) {
          b.onPress();
          return;
        }
    if (ticks - last < 100) return;
    if (stage > 0 && stage < 20 && m.player == null) return;
    switch (stage) {
      case 0 -> {
        if (!(m.screen instanceof TitleScreen) || m.getOverlay() != null) return;
        m.options.pauseOnLostFocus = false;
        m.options.renderDistance().set(5);
        m.options.simulationDistance().set(5);
        m.options.framerateLimit().set(30);
        m.options.guiScale().set(2);
        m.resizeDisplay();
        save = "dungeon-gates-qa-" + UUID.randomUUID();
        var rules = new GameRules();
        rules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
        var settings =
            new LevelSettings(
                "Dungeon Gates isolated QA",
                GameType.CREATIVE,
                false,
                Difficulty.NORMAL,
                true,
                rules,
                WorldDataConfiguration.DEFAULT);
        next(1);
        m.createWorldOpenFlows()
            .createFreshLevel(
                save,
                settings,
                new WorldOptions(104729, true, false),
                a ->
                    a.registryOrThrow(Registries.WORLD_PRESET)
                        .getHolderOrThrow(WorldPresets.FLAT)
                        .value()
                        .createWorldDimensions());
      }
      case 1 -> {
        m.setScreen(null);
        job(
            m,
            p -> {
              var l = p.server.overworld();
              for (int x = -12; x <= 20; x++)
                for (int z = -8; z <= 20; z++) {
                  l.setBlock(new BlockPos(x, 64, z), Blocks.SMOOTH_STONE.defaultBlockState(), 18);
                  for (int y = 65; y < 74; y++)
                    l.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 18);
                }
              l.setBlock(
                  new BlockPos(0, 65, 0), DGRegistry.EMERALD_GATE.get().defaultBlockState(), 3);
              l.setBlock(
                  new BlockPos(8, 65, 0), DGRegistry.FROZEN_GATE.get().defaultBlockState(), 3);
              p.teleportTo(l, 4, 65, 13, 180, -10);
              p.setInvulnerable(true);
            });
        next(2);
      }
      case 2 -> {
        check(
            m.level.getBlockEntity(new BlockPos(0, 65, 0)) instanceof GateAnchor,
            "client receives Emerald anchor");
        check(
            m.level.getBlockEntity(new BlockPos(8, 65, 0)) instanceof GateAnchor,
            "client receives Frozen anchor");
        shot(m, "floating-gates-and-labels");
        job(m, p -> p.teleportTo(p.server.overworld(), .5, 65, .5, 180, 0));
        next(3);
      }
      case 3 -> {
        check(m.level.dimension().equals(DungeonState.DIM), "touch enters Emerald dimension");
        shot(m, "emerald-sanctuary-arrival");
        job(
            m,
            p -> {
              var d = DungeonState.get(p.server);
              var r = d.returns.get(p.getUUID());
              var i = d.instances.get(r.active);
              check(
                  i != null && i.gate.equals("emerald_trial") && !i.complete,
                  "correct private Emerald instance");
              emerald = i.id;
              var boss = (DungeonMob) p.serverLevel().getEntity(i.boss);
              check(boss != null, "actual Emerald boss");
              boss.setNoAi(true);
              check(
                  boss.hurt(p.damageSources().playerAttack(p), 1000000),
                  "actual Emerald lethal attack");
              check(i.complete, "Emerald completed from actual death event");
              var at = i.origin().offset(14, 3, 7);
              p.teleportTo(p.serverLevel(), at.getX() + .5, at.getY(), at.getZ() + .5, 180, 0);
            });
        next(4);
      }
      case 4 -> {
        check(m.level.dimension().equals(Level.OVERWORLD), "unlocked Emerald contact return");
        job(
            m,
            p -> {
              var d = DungeonState.get(p.server);
              var i = d.instances.get(emerald);
              check(
                  i.claimed && p.getInventory().countItem(DGRegistry.EMERALD_TROPHY.get()) == 1,
                  "one actual Emerald trophy");
              int xp = p.totalExperience;
              check(
                  !DungeonState.claim(p, i) && p.totalExperience == xp,
                  "duplicate reward rejected");
              p.teleportTo(p.server.overworld(), 8.5, 65, .5, 180, 0);
            });
        next(5);
      }
      case 5 -> {
        check(m.level.dimension().equals(DungeonState.DIM), "touch enters Frozen dimension");
        shot(m, "frozen-crypt-arrival");
        job(
            m,
            p -> {
              var d = DungeonState.get(p.server);
              var i = d.instances.get(d.returns.get(p.getUUID()).active);
              check(
                  i != null && i.gate.equals("frozen_grave") && !i.id.equals(emerald),
                  "distinct Frozen instance");
              frozen = i.id;
              var boss = (DungeonMob) p.serverLevel().getEntity(i.boss);
              check(boss != null, "actual Frozen boss");
              boss.setNoAi(true);
              check(
                  boss.hurt(p.damageSources().playerAttack(p), 1000000),
                  "actual Frozen lethal attack");
              check(i.complete, "Frozen death unlock");
              var at = i.origin().offset(14, 3, 7);
              p.teleportTo(p.serverLevel(), at.getX() + .5, at.getY(), at.getZ() + .5, 180, 0);
            });
        next(6);
      }
      case 6 -> {
        check(m.level.dimension().equals(Level.OVERWORLD), "Frozen contact return");
        job(
            m,
            p -> {
              check(
                  p.getInventory().countItem(DGRegistry.FROZEN_TROPHY.get()) == 1,
                  "one Frozen trophy");
              p.server.saveEverything(false, true, true);
            });
        m.setScreen(new Gallery());
        next(7);
      }
      case 7 -> {
        shot(m, "six-authored-creatures-and-trophies");
        m.setScreen(null);
        m.level.disconnect();
        m.clearLevel();
        m.setScreen(new TitleScreen());
        next(20);
      }
      case 20 -> {
        if (m.getSingleplayerServer() != null || m.getOverlay() != null) return;
        m.createWorldOpenFlows().loadLevel(new TitleScreen(), save);
        next(21);
      }
      case 21 -> {
        if (m.player == null || m.getSingleplayerServer() == null) return;
        m.setScreen(null);
        job(
            m,
            p -> {
              var d = DungeonState.get(p.server);
              for (var id : List.of(emerald, frozen)) {
                var i = d.instances.get(id);
                check(
                    i != null && i.built && i.complete && i.claimed && i.owner.equals(p.getUUID()),
                    "persistent correct completion after real reopen");
              }
              check(
                  p.getInventory().countItem(DGRegistry.EMERALD_TROPHY.get()) == 1
                      && p.getInventory().countItem(DGRegistry.FROZEN_TROPHY.get()) == 1,
                  "trophies retained without duplication");
            });
        next(22);
      }
      case 22 -> {
        System.out.println(
            "DGATES_CLIENT_PASS: actual labels, both contact entries, two distinct dungeons, actual"
                + " boss deaths, unlocked returns, exactly once trophies, actual"
                + " save-close-reopen");
        m.level.disconnect();
        m.clearLevel();
        m.stop();
        next(99);
      }
    }
  }

  static final class Gallery extends Screen {
    final List<DungeonMob> mobs = new ArrayList<>();

    Gallery() {
      super(Component.literal("Six authored creatures"));
    }

    public boolean isPauseScreen() {
      return false;
    }

    protected void init() {
      for (var type : DGRegistry.MOBS) mobs.add(type.get().create(minecraft.level));
    }

    public void render(GuiGraphics g, int x, int y, float partial) {
      g.fill(0, 0, width, height, 0xFF101828);
      g.drawCenteredString(
          font, "DUNGEON GATES: six supplied models + two trophies", width / 2, 8, 0xA9EDFF);
      for (int i = 0; i < 6; i++) {
        var m = mobs.get(i);
        int px = width / 3 * (i % 3) + width / 6, py = height / 2 * (i / 3) + height / 2 - 24;
        InventoryScreen.renderEntityInInventoryFollowsMouse(
            g, px, py, (int) (65 / m.getBbHeight()), 0, 0, m);
        g.drawCenteredString(font, m.getType().getDescription(), px, py + 3, 0xFFFFFF);
      }
      g.renderItem(new ItemStack(DGRegistry.EMERALD_TROPHY.get()), 8, 8);
      g.renderItem(new ItemStack(DGRegistry.FROZEN_TROPHY.get()), 30, 8);
      super.render(g, x, y, partial);
    }
  }
}
