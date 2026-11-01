package dev.dungeongates;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.MinecraftForge;

public final class DungeonState extends SavedData {
  public static final ResourceKey<Level> DIM =
      ResourceKey.create(Registries.DIMENSION, new ResourceLocation("dungeongates", "dungeons"));
  public final Map<UUID, Instance> instances = new LinkedHashMap<>();
  public final Map<UUID, ReturnPoint> returns = new HashMap<>();
  public int nextSlot;

  public static final class Instance {
    public UUID id, owner, boss;
    public String gate, entryKey;
    public int slot, generation;
    public boolean built, complete, claimed, retired;
    public long created;

    public BlockPos origin() {
      return new BlockPos((slot % 512) * 128, 48, (slot / 512) * 128);
    }

    public boolean contains(BlockPos p) {
      var o = origin();
      return p.getX() >= o.getX()
          && p.getX() < o.getX() + 64
          && p.getZ() >= o.getZ()
          && p.getZ() < o.getZ() + 64
          && p.getY() >= o.getY()
          && p.getY() < o.getY() + 28;
    }

    CompoundTag save() {
      var t = new CompoundTag();
      t.putUUID("Id", id);
      t.putUUID("Owner", owner);
      if (boss != null) t.putUUID("Boss", boss);
      t.putString("Gate", gate);
      t.putString("Entry", entryKey);
      t.putInt("Slot", slot);
      t.putInt("Generation", generation);
      t.putBoolean("Built", built);
      t.putBoolean("Complete", complete);
      t.putBoolean("Claimed", claimed);
      t.putBoolean("Retired", retired);
      t.putLong("Created", created);
      return t;
    }

    static Instance load(CompoundTag t) {
      var i = new Instance();
      i.id = t.getUUID("Id");
      i.owner = t.getUUID("Owner");
      i.boss = t.hasUUID("Boss") ? t.getUUID("Boss") : null;
      i.gate = t.getString("Gate");
      i.entryKey = t.getString("Entry");
      i.slot = t.getInt("Slot");
      i.generation = t.getInt("Generation");
      i.built = t.getBoolean("Built");
      i.complete = t.getBoolean("Complete");
      i.claimed = t.getBoolean("Claimed");
      i.retired = t.getBoolean("Retired");
      i.created = t.getLong("Created");
      GateKind.read(i.gate);
      if (i.slot < 0 || i.slot >= 262144)
        throw new IllegalArgumentException("Invalid instance slot");
      return i;
    }
  }

  public static final class ReturnPoint {
    public ResourceKey<Level> dimension;
    public double x, y, z;
    public float yaw;
    public UUID active;
    public long cooldown;

    CompoundTag save() {
      var t = new CompoundTag();
      t.putString("Dimension", dimension.location().toString());
      t.putDouble("X", x);
      t.putDouble("Y", y);
      t.putDouble("Z", z);
      t.putFloat("Yaw", yaw);
      if (active != null) t.putUUID("Active", active);
      t.putLong("Cooldown", cooldown);
      return t;
    }

    static ReturnPoint load(CompoundTag t) {
      var r = new ReturnPoint();
      r.dimension =
          ResourceKey.create(Registries.DIMENSION, new ResourceLocation(t.getString("Dimension")));
      r.x = t.getDouble("X");
      r.y = t.getDouble("Y");
      r.z = t.getDouble("Z");
      r.yaw = t.getFloat("Yaw");
      r.active = t.hasUUID("Active") ? t.getUUID("Active") : null;
      r.cooldown = t.getLong("Cooldown");
      return r;
    }
  }

  public static DungeonState get(MinecraftServer s) {
    return s.overworld()
        .getDataStorage()
        .computeIfAbsent(DungeonState::load, DungeonState::new, "dungeongates_instances");
  }

  public static DungeonState load(CompoundTag t) {
    var d = new DungeonState();
    d.nextSlot = t.getInt("NextSlot");
    for (var tag : t.getList("Instances", 10)) {
      var i = Instance.load((CompoundTag) tag);
      d.instances.put(i.id, i);
      d.nextSlot = Math.max(d.nextSlot, i.slot + 1);
    }
    for (var tag : t.getList("Returns", 10)) {
      var r = (CompoundTag) tag;
      d.returns.put(r.getUUID("Player"), ReturnPoint.load(r));
    }
    return d;
  }

  public CompoundTag save(CompoundTag t) {
    t.putInt("NextSlot", nextSlot);
    var list = new ListTag();
    for (var i : instances.values()) list.add(i.save());
    t.put("Instances", list);
    list = new ListTag();
    for (var e : returns.entrySet()) {
      var v = e.getValue().save();
      v.putUUID("Player", e.getKey());
      list.add(v);
    }
    t.put("Returns", list);
    return t;
  }

  public static void touch(ServerPlayer p, GateAnchor gate) {
    var server = p.server;
    var d = get(server);
    var r = d.returns.get(p.getUUID());
    long now = server.overworld().getGameTime();
    if (r != null && r.cooldown > now) return;
    if (gate.exit) {
      if (r == null || r.active == null || !r.active.equals(gate.instance)) return;
      var i = d.instances.get(gate.instance);
      if (i == null || !i.owner.equals(p.getUUID())) {
        recover(p);
        return;
      }
      if (!i.complete) {
        p.displayClientMessage(Component.translatable("dg.locked"), true);
        r.cooldown = now + 40;
        d.setDirty();
        return;
      }
      claim(p, i);
      if (!i.claimed)
        p.displayClientMessage(
            Component.literal(
                "Inventory full: your trophy remains available. Reenter this gate after making"
                    + " space."),
            false);
      recover(p);
      return;
    }
    if (!p.level().dimension().equals(Level.OVERWORLD)) return;
    String key = gate.kind().id + ":" + gate.getBlockPos().asLong();
    Instance i = null;
    for (var old : d.instances.values())
      if (!old.retired && old.owner.equals(p.getUUID()) && old.entryKey.equals(key)) {
        i = old;
        break;
      }
    if (i == null) {
      if (d.instances.values().stream().filter(v -> !v.retired).count()
              >= DGConfig.MAX_INSTANCES.get()
          || d.nextSlot >= 262144) {
        p.displayClientMessage(
            Component.literal(
                "Dungeon capacity reached; ask an administrator to clean completed instances."),
            true);
        return;
      }
      i = new Instance();
      i.id = UUID.randomUUID();
      i.owner = p.getUUID();
      i.gate = gate.kind().id;
      i.entryKey = key;
      i.slot = d.nextSlot++;
      i.created = now;
      d.instances.put(i.id, i);
      d.setDirty();
    }
    var level = server.getLevel(DIM);
    if (level == null) throw new IllegalStateException("Dungeon dimension is unavailable");
    loadChunks(level, i, true);
    try {
      if (!i.built) build(level, i);
    } catch (RuntimeException ex) {
      loadChunks(level, i, false);
      throw ex;
    }
    r = new ReturnPoint();
    r.dimension = Level.OVERWORLD;
    r.x = p.getX() + 4;
    r.y = p.getY();
    r.z = p.getZ();
    r.yaw = p.getYRot();
    r.active = i.id;
    r.cooldown = now + 100;
    d.returns.put(p.getUUID(), r);
    d.setDirty();
    var arrival = i.origin().offset(10, 3, 50);
    safeArrival(level, arrival);
    p.teleportTo(level, arrival.getX() + .5, arrival.getY(), arrival.getZ() + .5, 180, 0);
    p.fallDistance = 0;
    level.sendParticles(
        DGRegistry.SPARKS.get("entry_vortex").get(),
        arrival.getX() + .5,
        arrival.getY() + 1,
        arrival.getZ() + .5,
        12,
        .4,
        .8,
        .4,
        .03);
  }

  public static void loadChunks(ServerLevel l, Instance i, boolean forced) {
    var o = i.origin();
    for (int x = 0; x < 4; x++)
      for (int z = 0; z < 4; z++) {
        int cx = (o.getX() >> 4) + x, cz = (o.getZ() >> 4) + z;
        if (forced) l.getChunk(cx, cz);
        l.setChunkForced(cx, cz, forced);
      }
  }

  public static void safeArrival(ServerLevel l, BlockPos p) {
    for (int x = -1; x <= 1; x++)
      for (int z = -1; z <= 1; z++) {
        l.setBlock(p.offset(x, -1, z), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 18);
        for (int y = 0; y < 3; y++)
          l.setBlock(p.offset(x, y, z), Blocks.AIR.defaultBlockState(), 18);
      }
  }

  public static void build(ServerLevel l, Instance i) {
    i.generation++;
    var o = i.origin();
    for (var e :
        l.getEntitiesOfClass(
            DungeonMob.class, new net.minecraft.world.phys.AABB(o, o.offset(64, 28, 64))))
      if (i.id.equals(e.instance)) e.discard();
    String path = GateKind.read(i.gate) == GateKind.EMERALD ? "emerald_sanctuary" : "frozen_crypt";
    var template =
        l.getStructureManager()
            .get(new ResourceLocation("dungeongates", path))
            .orElseThrow(
                () -> new IllegalStateException("Missing actual dungeon template " + path));
    if (!template.placeInWorld(
        l,
        o,
        o,
        new StructurePlaceSettings().setIgnoreEntities(true).setKnownShape(true),
        l.random,
        18)) throw new IllegalStateException("Could not place dungeon " + path);
    var kind = GateKind.read(i.gate);
    placeExit(l, i, 10, 3, 57);
    placeExit(l, i, 14, 3, 7);
    int[][] spots =
        kind == GateKind.EMERALD
            ? new int[][] {
              {10, 3, 43, 0},
              {15, 3, 38, 0},
              {16, 3, 28, 0},
              {28, 3, 21, 0},
              {29, 3, 32, 0},
              {20, 3, 21, 0},
              {45, 3, 32, 1},
              {50, 3, 32, 1},
              {45, 3, 40, 1},
              {50, 3, 40, 1},
              {48, 3, 12, 2}
            }
            : new int[][] {
              {10, 3, 41, 3},
              {14, 3, 37, 3},
              {17, 3, 28, 3},
              {26, 3, 31, 3},
              {29, 3, 29, 3},
              {40, 3, 32, 4},
              {48, 3, 37, 4},
              {52, 3, 32, 4},
              {46, 3, 15, 5}
            };
    for (var spot : spots) {
      var mob = DGRegistry.MOBS.get(spot[3]).get().create(l);
      if (mob == null) throw new IllegalStateException("Missing registered creature");
      var p = o.offset(spot[0], spot[1], spot[2]);
      if (!l.getBlockState(p).getCollisionShape(l, p).isEmpty())
        l.setBlock(p, Blocks.AIR.defaultBlockState(), 18);
      for (int y = 1; y < Math.ceil(mob.getBbHeight()); y++)
        l.setBlock(p.above(y), Blocks.AIR.defaultBlockState(), 18);
      mob.moveTo(p.getX() + .5, p.getY(), p.getZ() + .5, 180, 0);
      mob.instance = i.id;
      mob.generation = i.generation;
      mob.setPersistenceRequired();
      mob.initializeStats();
      if (!l.addFreshEntity(mob)) throw new IllegalStateException("Failed to spawn creature");
      if (spot[3] % 3 == 2) i.boss = mob.getUUID();
    }
    i.built = true;
    get(l.getServer()).setDirty();
  }

  static void placeExit(ServerLevel l, Instance i, int x, int y, int z) {
    var p = i.origin().offset(x, y, z);
    var kind = GateKind.read(i.gate);
    var block =
        kind == GateKind.EMERALD ? DGRegistry.EMERALD_GATE.get() : DGRegistry.FROZEN_GATE.get();
    l.setBlock(p, block.defaultBlockState(), 18);
    if (l.getBlockEntity(p) instanceof GateAnchor a) a.configureExit(i.id);
  }

  public static void bossDefeated(DungeonMob boss) {
    if (boss.instance == null || !(boss.level() instanceof ServerLevel l)) return;
    var d = get(l.getServer());
    var i = d.instances.get(boss.instance);
    if (i == null || i.retired || i.complete || !boss.getUUID().equals(i.boss)) return;
    i.complete = true;
    for (var at : java.util.List.of(i.origin().offset(10, 3, 57), i.origin().offset(14, 3, 7)))
      if (l.getBlockEntity(at) instanceof GateAnchor anchor) {
        anchor.unlocked = true;
        anchor.setChanged();
        l.sendBlockUpdated(at, anchor.getBlockState(), anchor.getBlockState(), 3);
      }
    d.setDirty();
    var p = l.getServer().getPlayerList().getPlayer(i.owner);
    if (p != null) p.displayClientMessage(Component.translatable("dg.cleared"), false);
    MinecraftForge.EVENT_BUS.post(
        new dev.dungeongates.api.DungeonCompletedEvent(
            l.getServer(), i.id, i.owner, GateKind.read(i.gate)));
  }

  public static boolean claim(ServerPlayer p, Instance i) {
    if (!i.complete || i.claimed || !i.owner.equals(p.getUUID())) return false;
    var stack =
        new ItemStack(
            GateKind.read(i.gate) == GateKind.EMERALD
                ? DGRegistry.EMERALD_TROPHY.get()
                : DGRegistry.FROZEN_TROPHY.get());
    if (!p.getInventory().add(stack)) return false;
    i.claimed = true;
    get(p.server).setDirty();
    p.giveExperiencePoints(75 + (GateKind.read(i.gate) == GateKind.FROZEN ? 15 : 0));
    return true;
  }

  public static void recover(ServerPlayer p) {
    var d = get(p.server);
    var r = d.returns.get(p.getUUID());
    var target = r == null ? null : p.server.getLevel(r.dimension);
    if (target == null || target.dimension().equals(DIM)) target = p.server.overworld();
    BlockPos base = r == null ? target.getSharedSpawnPos() : BlockPos.containing(r.x, r.y, r.z);
    BlockPos safe = findSafe(target, base);
    if (safe == null) {
      target = p.server.overworld();
      safe = findSafe(target, target.getSharedSpawnPos());
    }
    if (safe == null) {
      var spawn = target.getSharedSpawnPos();
      int y =
          Math.min(
              target.getMaxBuildHeight() - 5,
              Math.max(
                      spawn.getY(),
                      target.getHeight(
                          net.minecraft.world.level.levelgen.Heightmap.Types
                              .MOTION_BLOCKING_NO_LEAVES,
                          spawn.getX(),
                          spawn.getZ()))
                  + 3);
      safe = new BlockPos(spawn.getX(), y, spawn.getZ());
      safeArrival(target, safe);
    }

    p.teleportTo(target, safe.getX() + .5, safe.getY(), safe.getZ() + .5, r == null ? 0 : r.yaw, 0);
    p.fallDistance = 0;
    if (r != null) {
      var i = d.instances.get(r.active);
      r.active = null;
      r.cooldown = p.server.overworld().getGameTime() + 100;
      if (i != null && p.server.getLevel(DIM) != null) loadChunks(p.server.getLevel(DIM), i, false);
      d.setDirty();
    }
  }

  public static BlockPos findSafe(ServerLevel l, BlockPos base) {
    for (int radius = 0; radius <= 8; radius++)
      for (int x = -radius; x <= radius; x++)
        for (int z = -radius; z <= radius; z++) {
          var p =
              l.getHeightmapPos(
                  net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                  base.offset(x, 0, z));
          if (l.getBlockState(p.below()).isFaceSturdy(l, p.below(), Direction.UP)
              && !l.getBlockState(p.below()).is(Blocks.MAGMA_BLOCK)
              && l.getFluidState(p).isEmpty()
              && l.getBlockState(p).isAir()
              && l.getBlockState(p.above()).isAir()) return p;
        }
    return null;
  }

  public static Instance at(ServerLevel l, BlockPos p) {
    if (!l.dimension().equals(DIM)) return null;
    for (var i : get(l.getServer()).instances.values()) if (!i.retired && i.contains(p)) return i;
    return null;
  }

  public static boolean canLoot(ServerLevel l, BlockPos p) {
    if (!l.dimension().equals(DIM)) return true;
    var i = at(l, p);
    return i != null && i.complete;
  }
}
