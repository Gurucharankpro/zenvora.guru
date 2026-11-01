/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.TicketType
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.AmethystClusterBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.LanternBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.TorchBlock
 *  net.minecraft.world.level.block.VineBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.chunk.ChunkStatus
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.gloom;

import dev.shadowsovereign.gloom.GloomContent;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.RegistryObject;

public final class GloomRealm {
    public static final int WIDTH = 416;
    public static final int CELLS = 173056;
    public static final int LAND = 519168;
    public static final int BRIDGE = 2016;
    public static final int DECOR = 179200;
    public static final int HABITATS = 2738;
    public static final ResourceKey<Biome> BIOME = ResourceKey.m_135785_((ResourceKey)Registries.f_256952_, (ResourceLocation)new ResourceLocation("shadowsovereign", "gloomwood"));
    private static List<Piece> citadel;
    private static final TicketType<ChunkPos> TICKET;
    private static final Map<UUID, Set<Long>> held;
    private static UUID builder;
    private static long releaseAt;
    private static final PendingChunk PENDING;
    private static long budgetTick;
    private static int spent;

    public static void release(MinecraftServer server, UUID owner) {
        ServerLevel w = server.m_129880_(OdysseyRealm.KEY);
        Set<Long> chunks = held.remove(owner);
        if (w != null && chunks != null) {
            for (long key : chunks) {
                ChunkPos pos = new ChunkPos(key);
                w.m_7726_().m_8438_(TICKET, pos, 0, (Object)pos);
            }
        }
        if (owner.equals(builder)) {
            builder = null;
            releaseAt = 0L;
        }
    }

    public static void releaseAll(MinecraftServer server) {
        for (UUID id : List.copyOf(held.keySet())) {
            GloomRealm.release(server, id);
        }
    }

    private static boolean load(ServerLevel w, BlockPos at) {
        ChunkPos pos = new ChunkPos(at);
        if (builder != null && held.computeIfAbsent(builder, k -> new HashSet()).add(pos.m_45588_())) {
            w.m_7726_().m_8387_(TICKET, pos, 0, (Object)pos);
        }
        if (w.m_46805_(at)) {
            return true;
        }
        w.m_7726_().m_8431_(pos.f_45578_, pos.f_45579_, ChunkStatus.f_62326_, true);
        return false;
    }

    public static CompoundTag state(ServerPlayer p) {
        return MythicService.state((ServerPlayer)p).gloom;
    }

    public static BlockPos center(ServerPlayer p) {
        int q = GloomRealm.state(p).m_128451_("Corner");
        return OdysseyRealm.home(p).m_7918_((q & 1) == 0 ? 400 : -400, 0, (q & 2) == 0 ? 400 : -400);
    }

    public static BlockPos habitat(ServerPlayer p, int kind) {
        CompoundTag n = MythicExpedition.state(p, kind);
        if (n.m_128441_("Lair")) {
            return BlockPos.m_122022_((long)n.m_128454_("Lair"));
        }
        return GloomRealm.legacyHabitat(p, kind);
    }

    private static BlockPos legacyHabitat(ServerPlayer p, int kind) {
        return switch (kind) {
            case 0 -> GloomRealm.center(p).m_7918_(-110, 1, -100);
            case 1 -> GloomRealm.center(p).m_7918_(0, 3, 0);
            case 2 -> GloomRealm.center(p).m_7918_(115, 1, 95);
            default -> BlockPos.m_122022_((long)MythicExpedition.state(p, kind).m_128454_("Lair"));
        };
    }

    public static boolean inside(ServerPlayer p, BlockPos at) {
        BlockPos c = GloomRealm.center(p);
        long x = at.m_123341_() - c.m_123341_();
        long z = at.m_123343_() - c.m_123343_();
        return OdysseyRealm.inside(p) && GloomRealm.state(p).m_128471_("Ready") && x * x + z * z < 43264L;
    }

    public static boolean protectedArea(ServerPlayer p, BlockPos at) {
        BlockPos c = GloomRealm.center(p);
        if (Math.abs(at.m_123341_() - c.m_123341_()) <= 34 && Math.abs(at.m_123343_() - c.m_123343_()) <= 34) {
            return true;
        }
        for (int k : new int[]{0, 2}) {
            if (!(at.m_123331_((Vec3i)GloomRealm.legacyHabitat(p, k)) < 576.0)) continue;
            return true;
        }
        return false;
    }

    public static boolean editable(ServerPlayer p, BlockPos at) {
        return GloomRealm.inside(p, at) && !GloomRealm.protectedArea(p, at) && at.m_123342_() >= 72;
    }

    public static void request(ServerPlayer p) {
        CompoundTag n = GloomRealm.state(p);
        if (!n.m_128471_("Ready") && !n.m_128471_("Blocked")) {
            n.m_128379_("Requested", true);
            GloomRealm.dirty(p);
        }
    }

    public static void dirty(ServerPlayer p) {
        LivingRealm.dirty(p);
    }

    private static List<Piece> template() {
        List<Piece> list;
        block12: {
            if (citadel != null) {
                return citadel;
            }
            InputStream in = GloomRealm.class.getResourceAsStream("/data/shadowsovereign/structures/citadel_of_bound_souls.nbt");
            try {
                if (in == null) {
                    throw new IllegalStateException("Missing supplied citadel");
                }
                CompoundTag n = NbtIo.m_128939_((InputStream)in);
                ArrayList<BlockState> palette = new ArrayList<BlockState>();
                for (Tag t : n.m_128437_("palette", 10)) {
                    palette.add(NbtUtils.m_247651_((HolderGetter)BuiltInRegistries.f_256975_.m_255303_(), (CompoundTag)((CompoundTag)t)));
                }
                ArrayList<Piece> blocks = new ArrayList<Piece>();
                for (Tag t : n.m_128437_("blocks", 10)) {
                    CompoundTag b = (CompoundTag)t;
                    ListTag pos = b.m_128437_("pos", 3);
                    blocks.add(new Piece(new BlockPos(pos.m_128763_(0), pos.m_128763_(1), pos.m_128763_(2)), (BlockState)palette.get(b.m_128451_("state")), b.m_128425_("nbt", 10) ? b.m_128469_("nbt").m_6426_() : null));
                }
                blocks.sort(Comparator.comparingInt(p -> p.state.m_60795_() ? 0 : (p.state.m_60734_() instanceof BushBlock || p.state.m_60734_() instanceof LanternBlock || p.state.m_60734_() instanceof TorchBlock || p.state.m_60734_() instanceof VineBlock || p.state.m_60734_() instanceof AmethystClusterBlock ? 2 : 1)).thenComparingInt(p -> p.pos.m_123342_()));
                citadel = List.copyOf(blocks);
                list = citadel;
                if (in == null) break block12;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new IllegalStateException("Cannot read supplied citadel", e);
                }
            }
            in.close();
        }
        return list;
    }

    public static void clear() {
        citadel = null;
        budgetTick = -1L;
        spent = 0;
        held.clear();
        builder = null;
        releaseAt = 0L;
    }

    public static void tick(ServerPlayer p) {
        int cursor;
        int allowance;
        if (!OdysseyRealm.inside(p) || !GuideService.state((ServerPlayer)p).plotReady) {
            GloomRealm.release(p.f_8924_, p.m_20148_());
            return;
        }
        CompoundTag n = GloomRealm.state(p);
        if (!n.m_128471_("Requested") && !n.m_128471_("Blocked")) {
            GloomRealm.request(p);
        }
        if (!LivingRealm.state((ServerPlayer)p).ready) {
            return;
        }
        long now = p.f_8924_.m_129783_().m_46467_();
        if (n.m_128471_("Ready")) {
            if (p.m_20148_().equals(builder) && now >= releaseAt) {
                GloomRealm.release(p.f_8924_, p.m_20148_());
            }
            return;
        }
        if (n.m_128471_("Blocked")) {
            GloomRealm.release(p.f_8924_, p.m_20148_());
            return;
        }
        if (!n.m_128471_("Requested") && !n.m_128471_("Reserved") || builder != null && !builder.equals(p.m_20148_())) {
            return;
        }
        builder = p.m_20148_();
        if (now != budgetTick) {
            budgetTick = now;
            spent = 0;
        }
        if ((allowance = Math.min(512, 2048 - spent)) <= 0) {
            return;
        }
        spent += allowance;
        ServerLevel w = p.m_284548_();
        BlockPos c = GloomRealm.center(p);
        if (!n.m_128471_("Reserved")) {
            int scan;
            int end = Math.min(173056, scan + allowance);
            for (scan = Math.max(0, Math.min(173056, n.m_128451_("Scan"))); scan < end; ++scan) {
                int x = scan % 416 - 208;
                int z = scan / 416 - 208;
                if (x * x + z * z >= 43264) continue;
                BlockPos at = c.m_7918_(x, 0, z);
                if (!GloomRealm.load(w, at)) {
                    n.m_128405_("Scan", scan);
                    GloomRealm.dirty(p);
                    return;
                }
                if (w.m_6924_(Heightmap.Types.WORLD_SURFACE, at.m_123341_(), at.m_123343_()) <= 70) continue;
                int corner = n.m_128451_("Corner") + 1;
                n.m_128405_("Corner", corner);
                n.m_128405_("Scan", 0);
                if (corner >= 4) {
                    n.m_128379_("Blocked", true);
                    p.m_5661_((Component)Component.m_237115_((String)"gloom.shadowsovereign.no_space"), false);
                }
                GloomRealm.release(p.f_8924_, p.m_20148_());
                GloomRealm.dirty(p);
                return;
            }
            n.m_128405_("Scan", scan);
            if (scan >= 173056) {
                n.m_128379_("Reserved", true);
            }
            GloomRealm.dirty(p);
            return;
        }
        int total = 521184 + GloomRealm.template().size() + 179200 + 2738;
        try {
            int end = Math.min(total, cursor + allowance);
            for (cursor = Math.max(0, Math.min(total, n.m_128451_("Cursor"))); cursor < end; ++cursor) {
                if (cursor < 519168) {
                    int cell = cursor / 3;
                    int x = cell % 416 - 208;
                    int z = cell / 416 - 208;
                    int layer = cursor % 3;
                    if (x * x + z * z >= 43264) continue;
                    GloomRealm.put(w, c.m_7918_(x, -layer, z), ((Block)(layer == 0 ? GloomContent.GRASS : (layer == 1 ? GloomContent.DIRT : GloomContent.STONE)).get()).m_49966_(), true);
                    if (layer != 0 || x % 16 != 0 || z % 16 != 0) continue;
                    GloomRealm.biome(w, c.m_7918_(x, 0, z));
                    continue;
                }
                if (cursor < 521184) {
                    int cell = cursor - 519168;
                    int step = cell / 7 + 112;
                    int side = cell % 7 - 3;
                    int sx = c.m_123341_() > OdysseyRealm.home(p).m_123341_() ? 1 : -1;
                    int sz = c.m_123343_() > OdysseyRealm.home(p).m_123343_() ? 1 : -1;
                    BlockPos at = OdysseyRealm.home(p).m_7918_(step * sx + side, 0, step * sz - side);
                    GloomRealm.put(w, at, ((Block)GloomContent.PLANKS.get()).m_49966_(), true);
                    if (Math.abs(side) != 3 || step % 12 != 0) continue;
                    GloomRealm.put(w, at.m_7494_(), ((Block)GloomContent.LANTERN.get()).m_49966_(), true);
                    continue;
                }
                if (cursor < 521184 + GloomRealm.template().size()) {
                    Piece piece = GloomRealm.template().get(cursor - 519168 - 2016);
                    BlockPos at = c.m_7918_(-32, -7, -32).m_121955_((Vec3i)piece.pos);
                    if (GloomRealm.load(w, at)) {
                        w.m_7731_(at, piece.state, 2);
                        if (piece.nbt == null || piece.nbt.m_128456_() || w.m_7702_(at) == null) continue;
                        CompoundTag data = piece.nbt.m_6426_();
                        data.m_128405_("x", at.m_123341_());
                        data.m_128405_("y", at.m_123342_());
                        data.m_128405_("z", at.m_123343_());
                        w.m_7702_(at).m_142466_(data);
                        w.m_7702_(at).m_6596_();
                        continue;
                    }
                    break;
                }
                if (cursor < 521184 + GloomRealm.template().size() + 179200) {
                    GloomRealm.decorate(p, cursor - 519168 - 2016 - GloomRealm.template().size());
                    continue;
                }
                GloomRealm.habitatBlock(p, cursor - 519168 - 2016 - GloomRealm.template().size() - 179200);
            }
        }
        catch (PendingChunk pendingChunk) {
            // empty catch block
        }
        n.m_128405_("Cursor", cursor);
        if (cursor >= total) {
            n.m_128379_("Ready", true);
            releaseAt = now + 200L;
            p.m_5661_((Component)Component.m_237115_((String)"gloom.shadowsovereign.ready"), false);
        }
        GloomRealm.dirty(p);
    }

    private static void biome(ServerLevel w, BlockPos at) {
        if (!GloomRealm.load(w, at)) {
            throw PENDING;
        }
        LevelChunk chunk = w.m_46745_(at);
        Holder.Reference holder = w.m_9598_().m_175515_(Registries.f_256952_).m_246971_(BIOME);
        chunk.m_183442_((x, y, z, sampler) -> holder, w.m_7726_().m_214994_().m_224579_());
        chunk.m_8092_(true);
        ClientboundChunksBiomesPacket packet = ClientboundChunksBiomesPacket.m_274415_(List.of(chunk));
        for (ServerPlayer player : w.m_7726_().f_8325_.m_183262_(chunk.m_7697_(), false)) {
            player.f_8906_.m_9829_((Packet)packet);
        }
    }

    private static void put(ServerLevel w, BlockPos at, BlockState s, boolean airOnly) {
        if (!w.m_6857_().m_61937_(at)) {
            return;
        }
        if (!GloomRealm.load(w, at)) {
            throw PENDING;
        }
        if (!airOnly || w.m_8055_(at).m_60795_()) {
            w.m_7731_(at, s, 2);
        }
    }

    private static void decorate(ServerPlayer p, int index) {
        int z;
        int i = index / 175;
        int cell = index % 175;
        BlockPos c = GloomRealm.center(p);
        ServerLevel w = p.m_284548_();
        int x = i % 32 * 11 - 171 + Math.floorMod(i * 73, 7) - 3;
        if (x * x + (z = i / 32 * 11 - 171 + Math.floorMod(i * 137, 7) - 3) * z > 38416 || Math.abs(x) < 42 && Math.abs(z) < 42) {
            return;
        }
        for (int kind : new int[]{0, 2}) {
            if (!(c.m_7918_(x, 1, z).m_123331_((Vec3i)GloomRealm.legacyHabitat(p, kind)) < 576.0)) continue;
            return;
        }
        if ((x - 110) * (x - 110) + (z + 110) * (z + 110) < 1444) {
            return;
        }
        BlockPos base = c.m_7918_(x, 1, z);
        if (i % 3 != 0) {
            if (cell < 9) {
                int mix = Math.floorMod(i * 31 + cell * 17, 23);
                RegistryObject<Block> plant = cell == 0 && i % 11 == 0 ? GloomContent.GLOW_MUSHROOM : (mix == 0 ? GloomContent.BERRIES : (mix < 4 ? GloomContent.FLOWER : GloomContent.TUFT));
                GloomRealm.put(w, base.m_7918_(cell % 3 - 1, 0, cell / 3 - 1), ((Block)plant.get()).m_49966_(), true);
            }
            return;
        }
        int dx = cell % 5 - 2;
        int dz = cell / 5 % 5 - 2;
        int y = cell / 25;
        BlockState block = null;
        if (dx == 0 && dz == 0 && y < 6) {
            block = ((Block)GloomContent.LOG.get()).m_49966_();
        } else if (y >= 3 && dx * dx + dz * dz + (y - 4) * (y - 4) <= 9) {
            block = (BlockState)((Block)GloomContent.LEAVES.get()).m_49966_().m_61124_((Property)LeavesBlock.f_54418_, (Comparable)Integer.valueOf(1));
        }
        if (block != null) {
            GloomRealm.put(w, base.m_7918_(dx, y, dz), block, true);
        }
    }

    private static void habitatBlock(ServerPlayer p, int index) {
        int kind = index / 1369 == 0 ? 0 : 2;
        int cell = index % 1369;
        int x = cell % 37 - 18;
        int z = cell / 37 - 18;
        if (x * x + z * z > 324) {
            return;
        }
        GloomRealm.put(p.m_284548_(), GloomRealm.legacyHabitat(p, kind).m_7918_(x, -1, z), ((Block)(kind == 0 ? GloomContent.COBBLE : GloomContent.GRASS).get()).m_49966_(), false);
    }

    static {
        TICKET = TicketType.m_9462_((String)"soulbound_gloom_build", Comparator.comparingLong(ChunkPos::m_45588_));
        held = new HashMap<UUID, Set<Long>>();
        PENDING = new PendingChunk();
        budgetTick = -1L;
    }

    private record Piece(BlockPos pos, BlockState state, CompoundTag nbt) {
    }

    private static final class PendingChunk
    extends RuntimeException {
        private PendingChunk() {
            super(null, null, false, false);
        }
    }
}

