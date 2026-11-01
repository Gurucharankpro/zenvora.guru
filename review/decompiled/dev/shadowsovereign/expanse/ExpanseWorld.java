/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.Util
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderLookup
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.TicketType
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.NoiseColumn
 *  net.minecraft.world.level.biome.BiomeResolver
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.VineBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.chunk.ChunkStatus
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.levelgen.FlatLevelSource
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
 *  net.minecraft.world.level.levelgen.NoiseGeneratorSettings
 *  net.minecraft.world.level.levelgen.RandomState
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.level.LevelEvent$Load
 *  net.minecraftforge.event.server.ServerStoppingEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.expanse;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.shadowsovereign.ShadowSovereign;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.expanse.ExpanseContent;
import dev.shadowsovereign.expansion.OpenMythics;
import dev.shadowsovereign.gloom.GloomContent;
import dev.shadowsovereign.gloom.GloomRealm;
import dev.shadowsovereign.monolith.MonolithWorld;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.network.RealmNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.realm.RealmState;
import dev.shadowsovereign.vigil.VigilCrown;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class ExpanseWorld {
    public static final ResourceKey<NoiseGeneratorSettings> SETTINGS = ResourceKey.m_135785_((ResourceKey)Registries.f_256932_, (ResourceLocation)ExpanseWorld.id("echo_expanses"));
    public static final ResourceKey<Structure> SHRINE = ResourceKey.m_135785_((ResourceKey)Registries.f_256944_, (ResourceLocation)ExpanseWorld.id("forgotten_stalker_shrine"));
    private static final TicketType<ChunkPos> TICKET = TicketType.m_9462_((String)"soulbound_legacy_build", Comparator.comparingLong(ChunkPos::m_45588_));
    private static final LinkedHashSet<Long> held = new LinkedHashSet();
    private static final Map<Long, CompletableFuture<?>> preparing = new HashMap();
    private static Long filling;
    private static CompletableFuture<BlockState[][]> columns;
    private static long terrainTick;
    private static long shrineTick;
    private static List<ShrineBlock> shrineBlocks;

    public static ResourceLocation id(String s) {
        return new ResourceLocation("shadowsovereign", s);
    }

    @SubscribeEvent
    public static void level(LevelEvent.Load e) {
        ServerLevel w;
        LevelAccessor levelAccessor = e.getLevel();
        if (!(levelAccessor instanceof ServerLevel) || !(w = (ServerLevel)levelAccessor).m_46472_().equals(OdysseyRealm.KEY)) {
            return;
        }
        ChunkGenerator previous = w.m_7726_().m_8481_();
        if (!(previous instanceof FlatLevelSource) && !(previous instanceof NoiseBasedChunkGenerator)) {
            return;
        }
        try (InputStream input = ((Resource)w.m_7654_().m_177941_().m_213713_(ExpanseWorld.id("dimension/echo_realms.json")).orElseThrow()).m_215507_();){
            JsonElement json = JsonParser.parseReader((Reader)new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject().get("generator");
            ChunkGenerator generator = (ChunkGenerator)ChunkGenerator.f_62136_.parse((DynamicOps)RegistryOps.m_255058_((DynamicOps)JsonOps.INSTANCE, (HolderLookup.Provider)w.m_9598_()), (Object)json).getOrThrow(false, s -> ShadowSovereign.LOGGER.error(s));
            RandomState random = RandomState.m_255302_((NoiseGeneratorSettings)((NoiseGeneratorSettings)w.m_9598_().m_175515_(Registries.f_256932_).m_246971_(SETTINGS).m_203334_()), (HolderGetter)w.m_9598_().m_255025_(Registries.f_256865_), (long)w.m_7328_());
            ChunkMap map = w.m_7726_().f_8325_;
            map.f_140136_ = generator;
            map.f_214834_ = random;
            map.f_254626_ = generator.m_255169_((HolderLookup)w.m_9598_().m_255025_(Registries.f_256998_), random, w.m_7328_());
            ShadowSovereign.LOGGER.info("ASTRAL UPGRADE: Echo Realms generator refreshed with Moonveil Hollow and Crimson Shadow; existing chunks retained.");
        }
        catch (Exception ex) {
            throw new IllegalStateException("Cannot safely upgrade legacy Echo Realms generation", ex);
        }
    }

    public static boolean readyChunk(ServerLevel w, BlockPos at) {
        if (w.m_46805_(at)) {
            return true;
        }
        ChunkPos pos = new ChunkPos(at);
        long key = pos.m_45588_();
        if (!held.contains(key)) {
            w.m_7726_().m_8387_(TICKET, pos, 0, (Object)pos);
            held.add(key);
            while (held.size() > 32) {
                long old = (Long)held.iterator().next();
                held.remove(old);
                ChunkPos cp = new ChunkPos(old);
                w.m_7726_().m_8438_(TICKET, cp, 0, (Object)cp);
                preparing.remove(old);
            }
        }
        preparing.computeIfAbsent(key, k -> w.m_7726_().m_8431_(pos.f_45578_, pos.f_45579_, ChunkStatus.f_62326_, true));
        return false;
    }

    public static void release(ServerLevel w) {
        Iterator iterator = held.iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            ChunkPos p = new ChunkPos(key);
            w.m_7726_().m_8438_(TICKET, p, 0, (Object)p);
        }
        held.clear();
        preparing.clear();
    }

    @SubscribeEvent
    public static void stop(ServerStoppingEvent e) {
        ServerLevel w = e.getServer().m_129880_(OdysseyRealm.KEY);
        if (w != null) {
            ExpanseWorld.release(w);
        }
        if (columns != null) {
            columns.cancel(false);
        }
        columns = null;
        filling = null;
        terrainTick = -1L;
        shrineTick = -1L;
        shrineBlocks = null;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        ServerLevel w = e.getServer().m_129880_(OdysseyRealm.KEY);
        if (w == null) {
            return;
        }
        List players = w.m_6907_();
        boolean legacy = false;
        for (ServerPlayer p : players) {
            if (GuideService.state((ServerPlayer)p).plot < 0 || !GuideService.state((ServerPlayer)p).plotReady) continue;
            RealmState s = LivingRealm.state(p);
            if (!s.ready && !s.requested) {
                s.requested = true;
                LivingRealm.dirty(p);
            }
            legacy |= !s.ready || GuideService.state((ServerPlayer)p).camp.m_128451_("AstralStairs") < 48 || GloomRealm.state(p).m_128471_("Ready") && !ExpanseWorld.privateShrineReady(p);
            ExpanseWorld.stairs(p);
            ExpanseWorld.privateShrine(p);
            if (s.ready) {
                GloomRealm.request(p);
            }
            if (p.f_19797_ % 40 != 0) continue;
            ExpanseWorld.discoverShrine(p);
            ExpanseWorld.retrofit(p);
        }
        if (!legacy && !held.isEmpty()) {
            ExpanseWorld.release(w);
        }
        if (filling != null) {
            ExpanseWorld.fill(w);
        }
    }

    public static void retrofit(ServerPlayer p) {
        ChunkGenerator chunkGenerator;
        if (filling != null || !((chunkGenerator = p.m_284548_().m_7726_().m_8481_()) instanceof NoiseBasedChunkGenerator)) {
            return;
        }
        NoiseBasedChunkGenerator gen = (NoiseBasedChunkGenerator)chunkGenerator;
        ServerLevel w = p.m_284548_();
        Data d = Data.get(w);
        ChunkPos c = p.m_146902_();
        for (int radius = 0; radius <= 2; ++radius) {
            for (int dx = -radius; dx <= radius; ++dx) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    int z;
                    int x;
                    LevelChunk chunk;
                    ChunkPos cp = new ChunkPos(c.f_45578_ + dx, c.f_45579_ + dz);
                    long key = cp.m_45588_();
                    if (d.done.contains(key) || !w.m_7232_(cp.f_45578_, cp.f_45579_) || !w.m_143319_(key) || (chunk = w.m_7726_().m_7131_(cp.f_45578_, cp.f_45579_)) == null) continue;
                    boolean natural = false;
                    boolean protectedBuild = false;
                    for (x = 0; !(d.cursors.containsKey(key) || x >= 16 || natural || protectedBuild); x += 4) {
                        for (z = 0; z < 16 && !natural && !protectedBuild; z += 4) {
                            BlockPos at = new BlockPos(cp.m_45604_() + x, 0, cp.m_45605_() + z);
                            if (chunk.m_8055_(at).m_60795_()) continue;
                            natural = true;
                        }
                    }
                    if (!natural && !d.cursors.containsKey(key)) {
                        for (x = 0; x < 16 && !protectedBuild; ++x) {
                            block6: for (z = 0; z < 16 && !protectedBuild; ++z) {
                                for (int y = -63; y < 70; ++y) {
                                    if (chunk.m_8055_(new BlockPos(cp.m_45604_() + x, y, cp.m_45605_() + z)).m_60795_()) continue;
                                    protectedBuild = true;
                                    continue block6;
                                }
                            }
                        }
                    }
                    if (natural) continue;
                    if (protectedBuild) {
                        d.done.add(key);
                        d.m_77762_();
                        continue;
                    }
                    filling = key;
                    RandomState random = w.m_7726_().m_214994_();
                    LevelHeightAccessor height = LevelHeightAccessor.m_186487_((int)w.m_141937_(), (int)w.m_141928_());
                    columns = CompletableFuture.supplyAsync(() -> {
                        BlockState[][] out = new BlockState[256][134];
                        for (int i = 0; i < 256; ++i) {
                            int top;
                            NoiseColumn col = gen.m_214184_(cp.m_45604_() + i % 16, cp.m_45605_() + i / 16, height, random);
                            for (int y = -64; y < 70; ++y) {
                                out[i][y + 64] = col.m_183556_(y);
                            }
                            for (top = 69; top > -63 && (out[i][top + 64].m_60795_() || !out[i][top + 64].m_60819_().m_76178_()); --top) {
                            }
                            boolean ash = gen.m_62218_().m_203407_(cp.m_45604_() + i % 16 >> 2, top >> 2, cp.m_45605_() + i / 16 >> 2, random.m_224579_()).m_203373_(ExpanseWorld.id("ashen_weald"));
                            if (top <= -60) continue;
                            out[i][top + 64] = ((Block)(ash ? ExpanseContent.SOIL : GloomContent.GRASS).get()).m_49966_();
                            for (int depth = 1; depth <= 3; ++depth) {
                                if (out[i][top + 64 - depth].m_60795_()) continue;
                                out[i][top + 64 - depth] = ((Block)GloomContent.DIRT.get()).m_49966_();
                            }
                        }
                        return out;
                    }, Util.m_183991_());
                    return;
                }
            }
        }
    }

    private static void fill(ServerLevel w) {
        int cursor;
        if (columns == null || !columns.isDone()) {
            return;
        }
        long key = filling;
        ChunkPos cp = new ChunkPos(key);
        if (!w.m_143319_(key)) {
            columns = null;
            filling = null;
            return;
        }
        Data d = Data.get(w);
        BlockState[][] states = columns.join();
        int end = Math.min(34304, cursor + 2048);
        for (cursor = d.cursors.getOrDefault(key, 0).intValue(); cursor < end; ++cursor) {
            BlockPos at;
            int col = cursor / 134;
            int y = cursor % 134 - 64;
            BlockState s = states[col][y + 64];
            if (s.m_60795_() || !w.m_8055_(at = new BlockPos(cp.m_45604_() + col % 16, y, cp.m_45605_() + col / 16)).m_60795_()) continue;
            w.m_7731_(at, s, 2);
        }
        d.cursors.put(key, cursor);
        if (cursor == 34304) {
            LevelChunk chunk = w.m_46745_(cp.m_45615_());
            chunk.m_183442_((BiomeResolver)w.m_7726_().m_8481_().m_62218_(), w.m_7726_().m_214994_().m_224579_());
            chunk.m_8092_(true);
            w.m_7726_().f_8325_.m_274524_(List.of(chunk));
            d.done.add(key);
            d.cursors.remove(key);
            ExpanseWorld.decorateRetrofit(w, cp);
            columns = null;
            filling = null;
        }
        d.m_77762_();
    }

    private static void decorateRetrofit(ServerLevel w, ChunkPos cp) {
        RandomSource r = RandomSource.m_216335_((long)(w.m_7328_() ^ cp.m_45588_()));
        for (int i = 0; i < 8; ++i) {
            Block b;
            BlockPos at = w.m_5452_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(cp.m_45604_() + 2 + r.m_188503_(12), 0, cp.m_45605_() + 2 + r.m_188503_(12)));
            if (at.m_123342_() >= 68 || at.m_123342_() < 34 || !w.m_8055_(at).m_60795_()) continue;
            Holder biome = w.m_204166_(at);
            Block block = b = biome.m_203373_(ExpanseWorld.id("ashen_weald")) ? (Block)ExpanseContent.GRASS.get() : (Block)GloomContent.TUFT.get();
            if (i == 0 && w.m_46805_(at.m_7918_(4, 0, 4)) && w.m_46805_(at.m_7918_(-4, 0, -4))) {
                if (biome.m_203373_(ExpanseWorld.id("ashen_weald"))) {
                    ExpanseContent.AshenSapling.grow(w, at);
                    continue;
                }
                GloomContent.DuskSapling.grow(w, at);
                continue;
            }
            if (!b.m_49966_().m_60710_((LevelReader)w, at)) continue;
            w.m_7731_(at, b.m_49966_(), 3);
        }
    }

    private static void stairs(ServerPlayer p) {
        CompoundTag n = GuideService.state((ServerPlayer)p).camp;
        int step = n.m_128451_("AstralStairs");
        if (step >= 48) {
            return;
        }
        BlockPos home = OdysseyRealm.home(p);
        BlockPos at = home.m_7918_(12, -step, -128 - step);
        if (!ExpanseWorld.readyChunk(p.m_284548_(), at)) {
            return;
        }
        if (!p.m_9236_().m_8055_(at).m_60812_((BlockGetter)p.m_9236_(), at).m_83281_() && step > 8) {
            n.m_128405_("AstralStairs", 48);
            GuideService.dirty(p);
            return;
        }
        for (int dx = -1; dx <= 1; ++dx) {
            BlockPos pos = at.m_7918_(dx, 0, 0);
            if (!p.m_9236_().m_8055_(pos).m_60795_()) continue;
            p.m_9236_().m_7731_(pos, (BlockState)((Block)GloomContent.STAIRS.get()).m_49966_().m_61124_((Property)StairBlock.f_56841_, (Comparable)Direction.SOUTH), 2);
        }
        n.m_128405_("AstralStairs", step + 1);
        GuideService.dirty(p);
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent e) {
        e.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"shadow").then(Commands.m_82127_((String)"wilderness").executes(c -> {
            RealmNetwork.open(((CommandSourceStack)c.getSource()).m_81375_(), 6);
            return 1;
        }))).then(Commands.m_82127_((String)"dimension").then(Commands.m_82127_((String)"status").executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            RealmState s = LivingRealm.state(p);
            ((CommandSourceStack)c.getSource()).m_288197_(() -> Component.m_237113_((String)("Native terrain: " + (p.f_8924_.m_129880_(OdysseyRealm.KEY).m_7726_().m_8481_() instanceof NoiseBasedChunkGenerator) + " | Islands: " + s.cursor + "/820512 | Gloomwood: " + GloomRealm.state(p).m_128451_("Cursor") + " | Ready: " + GloomRealm.state(p).m_128471_("Ready"))), false);
            return 1;
        }))));
    }

    public static BlockPos privateShrineOrigin(ServerPlayer p) {
        int site = GloomRealm.state(p).m_128451_("ShrineSite");
        return GloomRealm.center(p).m_7918_((new int[]{102, -118, 152, 57})[Math.floorMod(site, 4)], 15, (new int[]{-118, 102, -73, -168})[Math.floorMod(site, 4)]);
    }

    public static boolean privateShrineReady(ServerPlayer p) {
        return GloomRealm.state(p).m_128471_("ShrineReady");
    }

    public static BlockPos privateShrineAltar(ServerPlayer p) {
        return ExpanseWorld.privateShrineOrigin(p).m_7918_(8, 5, 5);
    }

    private static List<ShrineBlock> shrineBlocks() {
        List<ShrineBlock> list;
        block11: {
            if (shrineBlocks != null) {
                return shrineBlocks;
            }
            InputStream in = ExpanseWorld.class.getResourceAsStream("/data/shadowsovereign/structures/forgotten_stalker_shrine.nbt");
            try {
                CompoundTag n = NbtIo.m_128939_((InputStream)in);
                ArrayList<BlockState> palette = new ArrayList<BlockState>();
                for (Tag t : n.m_128437_("palette", 10)) {
                    palette.add(NbtUtils.m_247651_((HolderGetter)BuiltInRegistries.f_256975_.m_255303_(), (CompoundTag)((CompoundTag)t)));
                }
                ArrayList<ShrineBlock> blocks = new ArrayList<ShrineBlock>();
                for (Tag t : n.m_128437_("blocks", 10)) {
                    CompoundTag b2 = (CompoundTag)t;
                    ListTag v = b2.m_128437_("pos", 3);
                    blocks.add(new ShrineBlock(new BlockPos(v.m_128763_(0), v.m_128763_(1), v.m_128763_(2)), (BlockState)palette.get(b2.m_128451_("state"))));
                }
                blocks.sort(Comparator.comparingInt(b -> b.state.m_60795_() ? 0 : (b.state.m_60734_() instanceof BushBlock || b.state.m_60734_() instanceof VineBlock ? 2 : 1)).thenComparingInt(b -> b.pos.m_123342_()));
                shrineBlocks = List.copyOf(blocks);
                list = shrineBlocks;
                if (in == null) break block11;
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
                    throw new IllegalStateException(e);
                }
            }
            in.close();
        }
        return list;
    }

    private static void privateShrine(ServerPlayer p) {
        int cursor;
        if (!GloomRealm.state(p).m_128471_("Ready")) {
            return;
        }
        CompoundTag n = GloomRealm.state(p);
        if (ExpanseWorld.privateShrineReady(p)) {
            BlockPos at = ExpanseWorld.privateShrineAltar(p);
            if (p.f_19797_ % 40 == 0 && p.m_20238_(Vec3.m_82512_((Vec3i)at)) < 484.0 && AdventureService.journal((ServerPlayer)p).session == null && MythicExpedition.state(p, 3).m_128451_("Status") != 2) {
                MythicExpedition.state(p, 3).m_128356_("Lair", at.m_121878_());
                MythicExpedition.start(p, 3);
            }
            return;
        }
        if (p.m_284548_().m_46467_() == shrineTick) {
            return;
        }
        shrineTick = p.m_284548_().m_46467_();
        int site = n.m_128451_("ShrineSite");
        if (site >= 4) {
            return;
        }
        BlockPos origin = ExpanseWorld.privateShrineOrigin(p);
        if (!n.m_128471_("ShrineReserved")) {
            int z;
            int x;
            for (x = 0; x < 17; x += 16) {
                for (z = 0; z < 17; z += 16) {
                    if (ExpanseWorld.readyChunk(p.m_284548_(), origin.m_7918_(x, 0, z))) continue;
                    return;
                }
            }
            for (x = 0; x < 17; ++x) {
                for (z = 0; z < 17; ++z) {
                    for (int y = 0; y < 11; ++y) {
                        if (p.m_9236_().m_8055_(origin.m_7918_(x, y, z)).m_60795_()) continue;
                        n.m_128405_("ShrineSite", site + 1);
                        GloomRealm.dirty(p);
                        return;
                    }
                }
            }
            n.m_128379_("ShrineReserved", true);
        }
        List<ShrineBlock> list = ExpanseWorld.shrineBlocks();
        int total = list.size() + 57;
        int end = Math.min(total, cursor + 128);
        for (cursor = n.m_128451_("ShrineCursor"); cursor < end; ++cursor) {
            if (cursor < list.size()) {
                ShrineBlock b = list.get(cursor);
                BlockPos at = origin.m_121955_((Vec3i)b.pos);
                if (!ExpanseWorld.readyChunk(p.m_284548_(), at)) break;
                p.m_9236_().m_7731_(at, b.state, 2);
                continue;
            }
            int cell = cursor - list.size();
            int step = cell / 3;
            int dx = cell % 3 - 1;
            BlockPos at = origin.m_7918_(8 + dx, -15 + step, 34 - step);
            if (!ExpanseWorld.readyChunk(p.m_284548_(), at)) break;
            if (!p.m_9236_().m_8055_(at).m_60795_()) continue;
            p.m_9236_().m_7731_(at, (BlockState)((Block)GloomContent.STAIRS.get()).m_49966_().m_61124_((Property)StairBlock.f_56841_, (Comparable)Direction.NORTH), 2);
        }
        n.m_128405_("ShrineCursor", cursor);
        if (cursor >= total) {
            BlockPos at = origin.m_7918_(12, -14, 34);
            if (p.m_9236_().m_8055_(at).m_60795_()) {
                if (p.m_9236_().m_8055_(at.m_7495_()).m_60795_()) {
                    p.m_9236_().m_7731_(at.m_7495_(), ((Block)GloomContent.PLANKS.get()).m_49966_(), 2);
                }
                p.m_9236_().m_7731_(at, ((Block)ExpanseContent.TRAVELSTONE.get()).m_49966_(), 3);
            }
            at = origin.m_7918_(4, -14, 34);
            if (p.m_9236_().m_8055_(at).m_60795_()) {
                if (p.m_9236_().m_8055_(at.m_7495_()).m_60795_()) {
                    p.m_9236_().m_7731_(at.m_7495_(), ((Block)GloomContent.PLANKS.get()).m_49966_(), 2);
                }
                p.m_9236_().m_7731_(at, Blocks.f_50087_.m_49966_(), 3);
                BlockEntity blockEntity = p.m_9236_().m_7702_(at);
                if (blockEntity instanceof RandomizableContainerBlockEntity) {
                    final RandomizableContainerBlockEntity chest = (RandomizableContainerBlockEntity)blockEntity;
                    chest.m_142466_(new CompoundTag(){
                        {
                            this.m_128359_("id", "minecraft:chest");
                            this.m_128359_("LootTable", "shadowsovereign:chests/astral_cache");
                            this.m_128405_("x", chest.m_58899_().m_123341_());
                            this.m_128405_("y", chest.m_58899_().m_123342_());
                            this.m_128405_("z", chest.m_58899_().m_123343_());
                        }
                    });
                    chest.m_6596_();
                }
            }
            n.m_128379_("ShrineReady", true);
        }
        GloomRealm.dirty(p);
    }

    public static boolean terrainBudget(ServerLevel w) {
        long time = w.m_46467_();
        if (time == terrainTick) {
            return false;
        }
        terrainTick = time;
        return true;
    }

    public static boolean editable(ServerPlayer p, BlockPos at) {
        block11: {
            block10: {
                if (!OdysseyRealm.inside(p) || OdysseyRealm.foreignPlot(p, at)) {
                    return false;
                }
                p.m_284548_();
                if (false || ExpanseWorld.shrineAt(p.m_284548_(), at) != null) break block10;
                p.m_284548_();
                if (false) break block10;
                p.m_284548_();
                if (!false) break block11;
            }
            return false;
        }
        if (GloomRealm.state(p).m_128471_("ShrineReserved")) {
            BlockPos o = ExpanseWorld.privateShrineOrigin(p);
            if (at.m_123341_() >= o.m_123341_() && at.m_123341_() < o.m_123341_() + 17 && at.m_123343_() >= o.m_123343_() && at.m_123343_() < o.m_123343_() + 17 && at.m_123342_() >= o.m_123342_() && at.m_123342_() <= o.m_123342_() + 11) {
                return false;
            }
        }
        if (OdysseyRealm.camp(p, at)) {
            return true;
        }
        if (GloomRealm.inside(p, at)) {
            return OpenMythics.editableGloom(p, at);
        }
        if (OdysseyRealm.owns(p, at) && at.m_123342_() >= 70) {
            for (int i = 0; i < 4; ++i) {
                BlockPos c = OdysseyRealm.arena(p, i);
                if (Math.abs(c.m_123341_() - at.m_123341_()) >= 128 || Math.abs(c.m_123343_() - at.m_123343_()) >= 128) continue;
                return false;
            }
        }
        return true;
    }

    public static BlockPos shrineAt(ServerLevel w, BlockPos at) {
        Object e;
        Structure s = (Structure)w.m_9598_().m_175515_(Registries.f_256944_).m_6246_(SHRINE);
        if (s == null) {
            return null;
        }
        StructureStart start = w.m_215010_().m_220494_(at, s);
        if (!start.m_73603_()) {
            return null;
        }
        if (!start.m_73602_().isEmpty() && (e = start.m_73602_().get(0)) instanceof PoolElementStructurePiece) {
            PoolElementStructurePiece piece = (PoolElementStructurePiece)e;
            return StructureTemplate.m_74593_((BlockPos)new BlockPos(8, 5, 5), (Mirror)Mirror.NONE, (Rotation)piece.m_6830_(), (BlockPos)BlockPos.f_121853_).m_121955_((Vec3i)piece.m_72646_());
        }
        BoundingBox box = start.m_73601_();
        return new BlockPos(box.m_162395_() + 8, box.m_162396_() + 5, box.m_162398_() + 5);
    }

    private static void discoverShrine(ServerPlayer p) {
        if (p.m_5833_()) {
            return;
        }
        BlockPos at = ExpanseWorld.shrineAt(p.m_284548_(), p.m_20183_());
        if (at == null) {
            return;
        }
        CompoundTag n = MythicExpedition.state(p, 3);
        if (n.m_128451_("Status") == 2) {
            return;
        }
        if (!n.m_128441_("Lair") || MythicService.session(p) == null) {
            n.m_128356_("Lair", at.m_121878_());
            GloomRealm.dirty(p);
        }
        if (p.m_20238_(Vec3.m_82512_((Vec3i)at)) < 484.0 && AdventureService.journal((ServerPlayer)p).session == null) {
            MythicExpedition.start(p, 3);
        }
    }

    public static void discover(ServerPlayer p, BlockPos at) {
        if (!OdysseyRealm.inside(p) || p.m_20238_(Vec3.m_82512_((Vec3i)at)) > 64.0 || !p.m_9236_().m_8055_(at).m_60713_((Block)ExpanseContent.TRAVELSTONE.get()) || OdysseyRealm.foreignPlot(p, at)) {
            return;
        }
        CompoundTag n = LivingRealm.state((ServerPlayer)p).sites;
        ListTag list = n.m_128437_("AstralWaypoints", 10);
        if (!n.m_128441_("AstralWaypoints")) {
            n.m_128365_("AstralWaypoints", (Tag)list);
        }
        if (list.stream().noneMatch(t -> ((CompoundTag)t).m_128454_("Pos") == at.m_121878_()) && list.size() < 32) {
            CompoundTag e = new CompoundTag();
            e.m_128356_("Pos", at.m_121878_());
            list.add((Object)e);
            LivingRealm.dirty(p);
            p.m_5661_((Component)Component.m_237115_((String)"expanse.shadowsovereign.discovered"), false);
        }
        RealmNetwork.open(p, 6);
    }

    public static boolean travel(ServerPlayer p, int index) {
        if (!OdysseyRealm.inside(p) || AdventureService.journal((ServerPlayer)p).session != null || VigilCrown.active(p) || !p.m_6084_() || p.m_20159_()) {
            return false;
        }
        long now = GuideService.now(p);
        if (now < p.getPersistentData().m_128454_("AstralTravelAt")) {
            return false;
        }
        ListTag list = LivingRealm.state((ServerPlayer)p).sites.m_128437_("AstralWaypoints", 10);
        boolean source = false;
        for (Tag t : list) {
            BlockPos at = BlockPos.m_122022_((long)((CompoundTag)t).m_128454_("Pos"));
            if (!(p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 64.0) || !p.m_284548_().m_46805_(at) || !p.m_9236_().m_8055_(at).m_60713_((Block)ExpanseContent.TRAVELSTONE.get())) continue;
            source = true;
        }
        if (!source || !p.m_7500_() && EclipseService.state((ServerPlayer)p).energy < 10.0f) {
            return false;
        }
        if (index < -1 || index >= list.size()) {
            return false;
        }
        BlockPos at = index < 0 ? OdysseyRealm.home(p).m_7494_() : BlockPos.m_122022_((long)list.m_128728_(index).m_128454_("Pos")).m_7494_();
        ServerLevel w = p.m_284548_();
        w.m_46745_(at);
        if (index >= 0 && !w.m_8055_(at.m_7495_()).m_60713_((Block)ExpanseContent.TRAVELSTONE.get())) {
            return false;
        }
        Vec3 landing = null;
        for (int x = -3; x <= 3 && landing == null; ++x) {
            for (int z = -3; z <= 3 && landing == null; ++z) {
                BlockPos q = at.m_7918_(x, 0, z);
                if (OdysseyRealm.foreignPlot(p, q) || !w.m_6425_(q).m_76178_() || w.m_8055_(q.m_7495_()).m_60812_((BlockGetter)w, q.m_7495_()).m_83281_() || !w.m_45756_((Entity)p, p.m_6972_(p.m_20089_()).m_20393_(Vec3.m_82539_((Vec3i)q).m_82520_(0.5, 0.0, 0.5)))) continue;
                landing = Vec3.m_82539_((Vec3i)q).m_82520_(0.5, 0.0, 0.5);
            }
        }
        if (landing == null || !OdysseyRealm.transfer(p, w, landing)) {
            return false;
        }
        if (!p.m_7500_()) {
            EclipseService.state((ServerPlayer)p).energy -= 10.0f;
            EclipseService.dirty(p);
        }
        p.getPersistentData().m_128356_("AstralTravelAt", now + 100L);
        return true;
    }

    public static void locate(ServerPlayer p, ItemStack compass) {
        BlockPos monolith;
        BlockPos found;
        int focus;
        if (!OdysseyRealm.inside(p)) {
            p.m_5661_((Component)Component.m_237115_((String)"expanse.shadowsovereign.dimension_only"), false);
            return;
        }
        long now = GuideService.now(p);
        if (now < p.getPersistentData().m_128454_("AstralLocateAt")) {
            return;
        }
        p.getPersistentData().m_128356_("AstralLocateAt", now + 200L);
        int n = focus = compass.m_41782_() ? compass.m_41783_().m_128451_("MonolithFocus") : 0;
        if (focus >= 1 && focus <= MonolithWorld.focusKinds().size()) {
            BlockPos target = MonolithWorld.locate(p, MonolithWorld.focusKinds().get(focus - 1));
            if (target == null) {
                p.m_5661_((Component)Component.m_237115_((String)"monolith.shadowsovereign.preparing"), false);
                return;
            }
            CompoundTag data = compass.m_41784_();
            data.m_128365_("LodestonePos", (Tag)NbtUtils.m_129224_((BlockPos)target));
            data.m_128359_("LodestoneDimension", OdysseyRealm.KEY.m_135782_().toString());
            data.m_128379_("LodestoneTracked", false);
            p.m_5661_((Component)Component.m_237110_((String)"expanse.shadowsovereign.located", (Object[])new Object[]{target.m_123341_(), target.m_123343_()}), false);
            return;
        }
        TagKey tag = TagKey.m_203882_((ResourceKey)Registries.f_256944_, (ResourceLocation)ExpanseWorld.id("exploration_sites"));
        BlockPos blockPos = found = p.f_8924_.m_129910_().m_246337_().m_247749_() ? p.m_284548_().m_215011_(tag, p.m_20183_(), 32, false) : null;
        if (found != null && OdysseyRealm.foreignPlot(p, found)) {
            found = null;
        }
        if (ExpanseWorld.privateShrineReady(p) && (found == null || p.m_20183_().m_123331_((Vec3i)ExpanseWorld.privateShrineAltar(p)) < p.m_20183_().m_123331_((Vec3i)found))) {
            found = ExpanseWorld.privateShrineAltar(p);
        }
        if ((monolith = MonolithWorld.nearest(p)) != null && (found == null || p.m_20183_().m_123331_((Vec3i)monolith) < p.m_20183_().m_123331_((Vec3i)found))) {
            found = monolith;
        }
        if (found == null) {
            p.m_5661_((Component)Component.m_237115_((String)"expanse.shadowsovereign.no_site"), false);
            return;
        }
        CompoundTag n2 = compass.m_41784_();
        n2.m_128365_("LodestonePos", (Tag)NbtUtils.m_129224_((BlockPos)found));
        n2.m_128359_("LodestoneDimension", OdysseyRealm.KEY.m_135782_().toString());
        n2.m_128379_("LodestoneTracked", false);
        p.m_5661_((Component)Component.m_237110_((String)"expanse.shadowsovereign.located", (Object[])new Object[]{found.m_123341_(), found.m_123343_()}), false);
    }

    static {
        terrainTick = -1L;
        shrineTick = -1L;
    }

    public static final class Data
    extends SavedData {
        public final Set<Long> done = new HashSet<Long>();
        public final Map<Long, Integer> cursors = new HashMap<Long, Integer>();

        public static Data get(ServerLevel w) {
            return (Data)w.m_8895_().m_164861_(Data::load, Data::new, "soulbound_astral_retrofit");
        }

        public static Data load(CompoundTag n) {
            Data d = new Data();
            for (long key : n.m_128467_("Done")) {
                d.done.add(key);
            }
            Object object = n.m_128437_("Cursors", 10).iterator();
            while (object.hasNext()) {
                Tag t = (Tag)object.next();
                CompoundTag v = (CompoundTag)t;
                d.cursors.put(v.m_128454_("Chunk"), Math.max(0, Math.min(34304, v.m_128451_("Cursor"))));
            }
            return d;
        }

        public CompoundTag m_7176_(CompoundTag n) {
            n.m_128388_("Done", this.done.stream().mapToLong(Long::longValue).toArray());
            ListTag list = new ListTag();
            this.cursors.forEach((k, v) -> {
                CompoundTag t = new CompoundTag();
                t.m_128356_("Chunk", k.longValue());
                t.m_128405_("Cursor", v.intValue());
                list.add((Object)t);
            });
            n.m_128365_("Cursors", (Tag)list);
            return n;
        }
    }

    private record ShrineBlock(BlockPos pos, BlockState state) {
    }
}

