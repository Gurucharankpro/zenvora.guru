package dev.dungeongates.qa;

import com.google.gson.*;
import dev.dungeongates.*;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("dungeongatesqa")
public final class NativeQA {
  private MinecraftServer server;
  private int index, wait;
  private boolean initialized;
  private final int[] counts = new int[2];
  private final JsonArray generated = new JsonArray();
  private String failure;
  private long start;

  public NativeQA() {
    MinecraftForge.EVENT_BUS.register(this);
    net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(
        net.minecraftforge.api.distmarker.Dist.CLIENT,
        () -> () -> MinecraftForge.EVENT_BUS.register(new ClientQA()));
  }

  @SubscribeEvent
  public void started(ServerStartedEvent e) {
    if (!Boolean.getBoolean("dungeongates.qa")) return;
    server = e.getServer();
    start = System.currentTimeMillis();
  }

  void check(boolean b, String why) {
    if (!b) throw new AssertionError(why);
  }

  void checks() throws Exception {
    var l = server.getLevel(DungeonState.DIM);
    check(l != null, "shared dimension");
    var resources = server.getResourceManager();
    int palette = 0;
    for (String id : new String[] {"emerald_sanctuary", "frozen_crypt"}) {
      var r =
          resources
              .getResource(
                  new net.minecraft.resources.ResourceLocation(
                      "dungeongates", "structures/" + id + ".nbt"))
              .orElseThrow();
      CompoundTag tag;
      try (var stream = r.open()) {
        tag = NbtIo.readCompressed(stream);
      }
      for (var element : tag.getList("palette", 10)) {
        var state = (CompoundTag) element;
        var block =
            net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValue(
                new net.minecraft.resources.ResourceLocation(state.getString("Name")));
        check(block != null, "registered palette " + state);
        if (state.contains("Properties"))
          for (var name : state.getCompound("Properties").getAllKeys()) {
            var property = block.getStateDefinition().getProperty(name);
            check(
                property != null
                    && property
                        .getValue(state.getCompound("Properties").getString(name))
                        .isPresent(),
                "real palette property " + state);
          }
        palette++;
      }
      check(
          l.getStructureManager()
              .get(new net.minecraft.resources.ResourceLocation("dungeongates", id))
              .isPresent(),
          "native template decode");
    }
    for (int kind = 0; kind < 6; kind++) {
      var m = DGRegistry.MOBS.get(kind).get().create(l);
      m.initializeStats();
      check(
          m.getMaxHealth() > 0
              && m.getAttributeValue(
                      net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
                  > 0,
          "attributes");
      var t = new CompoundTag();
      m.save(t);
      var loaded = EntityType.loadEntityRecursive(t, l, x -> x);
      check(loaded instanceof DungeonMob && loaded.getType() == m.getType(), "entity save/reload");
    }
    var data = DungeonState.get(server);
    for (var gate : GateKind.values()) {
      var i = new DungeonState.Instance();
      i.id = UUID.randomUUID();
      i.owner = UUID.randomUUID();
      i.gate = gate.id;
      i.entryKey = gate.id;
      i.slot = gate.ordinal();
      data.instances.put(i.id, i);
      DungeonState.loadChunks(l, i, true);
      DungeonState.build(l, i);
      check(i.built && i.boss != null, "real assembly and boss UUID");
      check(
          l.getBlockEntity(i.origin().offset(14, 3, 7)) instanceof GateAnchor a
              && a.exit
              && !a.unlocked,
          "locked automatic exit");
    }
    var tag = data.save(new CompoundTag());
    var reloaded = DungeonState.load(tag);
    check(
        reloaded.instances.size() == 2 && reloaded.nextSlot >= 2, "persistent instance roundtrip");
    System.out.println(
        "DGATES_NATIVE_PALETTES="
            + palette
            + ";entities=6;templates=2;assembly=2;completion=pending");
  }

  @SubscribeEvent
  public void tick(TickEvent.ServerTickEvent e) {
    if (server == null || e.phase != TickEvent.Phase.END) return;
    if (!initialized) {
      initialized = true;
      try {
        DGConfig.SPACING.set(4);
        DGConfig.FREQUENCY.set(1.0);
        checks();
      } catch (Throwable t) {
        fail(t);
      }
      return;
    }
    if (failure != null) {
      finish();
      return;
    }
    if (++wait < 10) return;
    wait = 0;
    try {
      if (index >= 100 || index >= 36 && counts[0] > 0 && counts[1] > 0) {
        finishAssembly();
        check(
            counts[0] > 0 && counts[1] > 0,
            "Both real natural gate types must occur on this seed; actual counts="
                + Arrays.toString(counts));
        finish();
        return;
      }
      int cx = 514 + (index % 10) * 12, cz = 514 + (index / 10) * 12;
      var l = server.overworld();
      var chunk = l.getChunk(cx, cz);
      var report = new JsonObject();
      report.addProperty("chunk_x", cx);
      report.addProperty("chunk_z", cz);
      String found = "none";
      for (var position : chunk.getBlockEntitiesPos())
        if (chunk.getBlockEntity(position) instanceof GateAnchor g && !g.exit) {
          counts[g.kind().ordinal()]++;
          found = g.kind().id;
          check(g.getBlockState().getBlock() instanceof GateBlock, "real natural anchor");
        }
      var surface =
          l.getHeightmapPos(
              net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
              new BlockPos(cx * 16 + 8, 0, cz * 16 + 8));
      report.addProperty("surface_y", surface.getY());
      report.addProperty(
          "biome",
          l.getBiome(surface).unwrapKey().map(v -> v.location().toString()).orElse("unknown"));
      var feature =
          l.registryAccess()
              .registryOrThrow(net.minecraft.core.registries.Registries.PLACED_FEATURE)
              .get(new net.minecraft.resources.ResourceLocation("dungeongates", "floating_gates"));
      report.addProperty(
          "biome_has_gate_feature",
          feature != null
              && l.getBiome(surface).value().getGenerationSettings().hasFeature(feature));
      report.addProperty("gate", found);
      generated.add(report);
      index++;
      System.out.println("DGATES_NATURAL_PROGRESS " + index + " counts=" + Arrays.toString(counts));
    } catch (Throwable t) {
      fail(t);
    }
  }

  void finishAssembly() {
    var l = server.getLevel(DungeonState.DIM);
    for (var i : DungeonState.get(server).instances.values()) {
      var boss = (DungeonMob) l.getEntity(i.boss);
      check(boss != null, "entity-ready boss exists after native chunk readiness");
      for (var mob :
          l.getEntitiesOfClass(
              DungeonMob.class,
              new net.minecraft.world.phys.AABB(i.origin(), i.origin().offset(64, 28, 64))))
        check(
            l.noCollision(mob), "spawn body clearance " + mob.id() + " at " + mob.blockPosition());
      DungeonState.bossDefeated(boss);
      check(i.complete, "actual completion hook");
      DungeonState.bossDefeated(boss);
      check(i.complete, "idempotent completion");
    }
  }

  void fail(Throwable t) {
    failure = t.toString();
    t.printStackTrace();
  }

  void finish() {
    try {
      var o = new JsonObject();
      o.addProperty("passed", failure == null);
      if (failure != null) o.addProperty("failure", failure);
      o.addProperty("seed", server.overworld().getSeed());
      o.addProperty("candidate_chunks_requested", index);
      o.addProperty("test_spacing_chunks", 4);
      o.addProperty("test_cell_spawn_chance", 1);
      o.addProperty("emerald_occurrences", counts[0]);
      o.addProperty("frozen_occurrences", counts[1]);
      o.add("actual_generated_candidates", generated);
      o.add("feature_diagnostics", new Gson().toJsonTree(GateFeature.qaCounts()));
      o.add("recent_rejections", new Gson().toJsonTree(GateFeature.qaRecent()));
      o.addProperty("elapsed_ms", System.currentTimeMillis() - start);
      Files.writeString(
          Path.of("DGATES-NATIVE-RESULT.json"),
          new GsonBuilder().setPrettyPrinting().create().toJson(o));
      System.out.println(failure == null ? "DGATES_NATIVE_PASS" : "DGATES_NATIVE_FAIL");
      server.halt(false);
      server = null;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
