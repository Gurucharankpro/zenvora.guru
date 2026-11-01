package dev.dungeongates;

import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class GateFeature extends Feature<NoneFeatureConfiguration> {
  private static final boolean QA = Boolean.getBoolean("dungeongates.qa");
  private static final java.util.concurrent.ConcurrentHashMap<
          String, java.util.concurrent.atomic.LongAdder>
      COUNTS = new java.util.concurrent.ConcurrentHashMap<>();
  private static final java.util.List<String> RECENT =
      java.util.Collections.synchronizedList(new java.util.ArrayList<>());

  private static void note(String name) {
    if (QA)
      COUNTS.computeIfAbsent(name, k -> new java.util.concurrent.atomic.LongAdder()).increment();
  }

  public static java.util.Map<String, Long> qaCounts() {
    var out = new java.util.TreeMap<String, Long>();
    COUNTS.forEach((k, v) -> out.put(k, v.sum()));
    return out;
  }

  public static java.util.List<String> qaRecent() {
    synchronized (RECENT) {
      return java.util.List.copyOf(RECENT);
    }
  }

  public GateFeature() {
    super(NoneFeatureConfiguration.CODEC);
  }

  public static long mix(long x) {
    x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L;
    x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
    return x ^ (x >>> 31);
  }

  public static GateKind selected(long seed, int cx, int cz) {
    long v = mix(seed ^ cx * 341873128712L ^ cz * 132897987541L);
    int total = DGConfig.EMERALD_WEIGHT.get() + DGConfig.FROZEN_WEIGHT.get();
    return Math.floorMod(v, total) < DGConfig.EMERALD_WEIGHT.get()
        ? GateKind.EMERALD
        : GateKind.FROZEN;
  }

  public static boolean candidate(long seed, int cx, int cz) {
    int spacing = DGConfig.SPACING.get();
    if (Math.floorMod(cx, spacing) != spacing / 2 || Math.floorMod(cz, spacing) != spacing / 2)
      return false;
    return (mix(seed ^ cx * 519317L ^ cz * 78533L) >>> 11) * 0x1.0p-53 < DGConfig.FREQUENCY.get();
  }

  public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> c) {
    var l = c.level();
    note("feature_calls");
    if (!l.getLevel().dimension().equals(Level.OVERWORLD)) return false;
    int cx = c.origin().getX() >> 4, cz = c.origin().getZ() >> 4;
    if (!candidate(l.getSeed(), cx, cz)) return false;
    note("eligible_cells");
    for (var existing : l.getChunk(cx, cz).getBlockEntitiesPos())
      if (l.getBlockState(existing).getBlock() instanceof GateBlock) return false;
    var kind = selected(l.getSeed(), cx, cz);
    int[][] sites = {{8, 8}, {4, 4}, {12, 4}, {4, 12}, {12, 12}, {8, 4}, {4, 8}, {12, 8}, {8, 12}};
    int shift = Math.floorMod(mix(l.getSeed() ^ cx ^ cz), sites.length);
    for (int attempt = 0; attempt < sites.length; attempt++) {
      var at = sites[(attempt + shift) % sites.length];
      int bx = cx * 16 + at[0], bz = cz * 16 + at[1];
      int y = l.getMinBuildHeight();
      for (int x = -1; x <= 1; x++)
        for (int z = -1; z <= 1; z++)
          y = Math.max(y, l.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx + x, bz + z));
      var p = new BlockPos(bx, y, bz);
      note("sites_tested");
      if (!terrain(l, p)) {
        note("terrain_rejected");
        if (QA)
          synchronized (RECENT) {
            if (RECENT.size() >= 24) RECENT.remove(0);
            RECENT.add(p + " below=" + l.getBlockState(p.below()) + " feet=" + l.getBlockState(p));
          }
        continue;
      }
      note("terrain_accepted");
      // Only replace harmless anchor-cell vegetation, never solid terrain.
      var old = l.getBlockState(p);
      if (old.getBlock() instanceof net.minecraft.world.level.block.DoublePlantBlock) {
        var half =
            old.getValue(
                net.minecraft.world.level.block.state.properties.BlockStateProperties
                    .DOUBLE_BLOCK_HALF);
        l.setBlock(
            half == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER
                ? p.above()
                : p.below(),
            Blocks.AIR.defaultBlockState(),
            2);
      }
      var block = kind == GateKind.EMERALD ? DGRegistry.EMERALD_GATE : DGRegistry.FROZEN_GATE;
      boolean placed =
          l.setBlock(
              p,
              block
                  .get()
                  .defaultBlockState()
                  .setValue(
                      GateBlock.FACING,
                      Direction.from2DDataValue((int) (mix(l.getSeed() ^ cx ^ cz) & 3))),
              2);
      note(placed ? "placed" : "set_block_rejected");
      return placed;
    }
    return false;
  }

  private static boolean harmlessAir(WorldGenLevel l, BlockPos p) {
    var s = l.getBlockState(p);
    return s.getFluidState().isEmpty()
        && s.getCollisionShape(l, p).isEmpty()
        && !(s.getBlock() instanceof GateBlock)
        && !s.is(Blocks.FIRE)
        && !s.is(Blocks.SOUL_FIRE)
        && !s.is(Blocks.WITHER_ROSE)
        && !s.is(Blocks.SWEET_BERRY_BUSH)
        && !s.is(Blocks.COBWEB);
  }

  private static boolean footing(WorldGenLevel l, BlockPos p) {
    // A single-block slope is safe; no supporting pillars or terrain flattening.
    for (int down = 1; down <= 2; down++) {
      var q = p.below(down);
      var floor = l.getBlockState(q);
      if (!floor.getFluidState().isEmpty()) return false;
      if (floor.isFaceSturdy(l, q, Direction.UP))
        return !floor.is(net.minecraft.tags.BlockTags.LEAVES)
            && !floor.is(net.minecraft.tags.BlockTags.LOGS)
            && !floor.is(Blocks.MAGMA_BLOCK)
            && !floor.is(Blocks.CACTUS)
            && !floor.is(Blocks.CAMPFIRE)
            && !floor.is(Blocks.SOUL_CAMPFIRE);
      if (!harmlessAir(l, q)) return false;
    }
    return false;
  }

  public static boolean terrain(WorldGenLevel l, BlockPos p) {
    if (p.getY() < l.getMinBuildHeight() + 8 || p.getY() + 10 >= l.getMaxBuildHeight())
      return false;
    for (int x = -1; x <= 1; x++)
      for (int z = -1; z <= 1; z++) if (!footing(l, p.offset(x, 0, z))) return false;
    // Include the authored rune geometry and label, not only the contact panel.
    for (int x = -2; x <= 2; x++)
      for (int z = -2; z <= 2; z++)
        for (int y = 0; y < 10; y++) if (!harmlessAir(l, p.offset(x, y, z))) return false;
    return true;
  }
}
