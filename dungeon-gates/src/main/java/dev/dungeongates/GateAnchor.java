package dev.dungeongates;

import java.util.UUID;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class GateAnchor extends BlockEntity {
  public UUID instance;
  public boolean exit, unlocked;

  public GateAnchor(BlockPos p, BlockState s) {
    super(DGRegistry.GATE_BE.get(), p, s);
  }

  public GateKind kind() {
    return ((GateBlock) getBlockState().getBlock()).kind;
  }

  public AABB surface() {
    boolean x = getBlockState().getValue(GateBlock.FACING).getAxis() == Direction.Axis.X;
    double cx = worldPosition.getX() + .5, cz = worldPosition.getZ() + .5;
    double halfX = x ? .12 : 1.35, halfZ = x ? 1.35 : .12;
    return new AABB(
        cx - halfX,
        worldPosition.getY() + .5,
        cz - halfZ,
        cx + halfX,
        worldPosition.getY() + 5,
        cz + halfZ);
  }

  public static void tick(Level l, BlockPos p, BlockState s, GateAnchor gate) {
    if (l.getGameTime() % 2 != 0) return;
    var sl = (ServerLevel) l;
    if (l.getGameTime() % 10 == 0) {
      var particle =
          DGRegistry.SPARKS
              .get(gate.kind() == GateKind.EMERALD ? "emerald_spark" : "frost_snowflake")
              .get();
      sl.sendParticles(
          particle, p.getX() + .5, p.getY() + 2.5, p.getZ() + .5, 2, .8, 1.7, .25, .01);
    }
    for (var player : sl.getEntitiesOfClass(ServerPlayer.class, gate.surface()))
      if (!player.isSpectator()) DungeonState.touch(player, gate);
  }

  public void configureExit(UUID id) {
    instance = id;
    exit = true;
    setChanged();
    if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
  }

  public void load(CompoundTag t) {
    super.load(t);
    exit = t.getBoolean("Exit");
    unlocked = t.getBoolean("Unlocked");
    instance = t.hasUUID("Instance") ? t.getUUID("Instance") : null;
  }

  protected void saveAdditional(CompoundTag t) {
    super.saveAdditional(t);
    t.putBoolean("Exit", exit);
    t.putBoolean("Unlocked", unlocked);
    if (instance != null) t.putUUID("Instance", instance);
  }

  public CompoundTag getUpdateTag() {
    return saveWithoutMetadata();
  }

  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  public AABB getRenderBoundingBox() {
    return new AABB(worldPosition.offset(-4, 0, -4), worldPosition.offset(5, 8, 5));
  }
}
