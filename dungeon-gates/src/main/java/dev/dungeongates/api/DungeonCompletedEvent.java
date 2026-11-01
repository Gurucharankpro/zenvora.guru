package dev.dungeongates.api;
import java.util.UUID;import net.minecraft.server.MinecraftServer;import net.minecraftforge.eventbus.api.Event;import dev.dungeongates.GateKind;
/** Server-thread, noncancelable, emitted exactly once per persisted instance. No soul-mod dependency. */
public final class DungeonCompletedEvent extends Event {
 public final MinecraftServer server;public final UUID instance,owner;public final GateKind gate;
 public DungeonCompletedEvent(MinecraftServer server,UUID instance,UUID owner,GateKind gate){this.server=server;this.instance=instance;this.owner=owner;this.gate=gate;}
}
