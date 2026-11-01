# Stable future integration surface
modid: dungeongates. No dependency or runtime references to shadowsovereign/soulbound.
GateKind stable IDs: emerald_trial, frozen_grave. Entity IDs are the six supplied
snake_case creature names. Do not reorder these identities when adding gate packs.
Listen on the Forge event bus for dev.dungeongates.api.DungeonCompletedEvent.
It is server-thread, noncancelable, once per persisted instance, with server,
instance UUID, owner UUID and gate identity. An integrator may grant its own reward
in its own mod; this mod neither extracts souls nor invokes another mod's APIs.
DungeonState persists owner, origin slot, entry portal identity, boss UUID, completion,
claim status and safe return position. Never wipe this file to implement an update.
Players own separate cells (128-block grid, 64-block dungeons); cells are not reused
on cleanup. No team/party system is claimed in this first chapter.
