/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.TicketType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.chunk.ChunkStatus
 *  net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.ToolActions
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.level.BlockEvent$BlockToolModificationEvent
 *  net.minecraftforge.event.server.ServerStoppingEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.monolith;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.expanse.ExpanseWorld;
import dev.shadowsovereign.gloom.GloomContent;
import dev.shadowsovereign.monolith.MonolithContent;
import dev.shadowsovereign.mythic.BelfryNests;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.MythicSites;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class MonolithWorld {
    public static final ResourceLocation BIOME = MonolithContent.id("ashen_monolith");
    private static final List<Site> SITES = MonolithWorld.readSites();
    private static final Map<String, List<Cell>> PLANS = new HashMap<String, List<Cell>>();
    private static final TicketType<ChunkPos> TICKET = TicketType.m_9462_((String)"soulbound_monolith_build", Comparator.comparingLong(ChunkPos::m_45588_));
    private static final LinkedHashSet<Long> held = new LinkedHashSet();
    private static final Map<Long, CompletableFuture<?>> requests = new HashMap();
    private static int turn;

    private static BlockPos pos(JsonArray a) {
        return new BlockPos(a.get(0).getAsInt(), a.get(1).getAsInt(), a.get(2).getAsInt());
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static List<Site> readSites() {
        try (InputStream in = MonolithWorld.class.getResourceAsStream("/data/shadowsovereign/monolith/sites.json");){
            List list;
            try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);){
                ArrayList<Site> out = new ArrayList<Site>();
                for (JsonElement el : JsonParser.parseReader((Reader)reader).getAsJsonArray()) {
                    JsonObject o = el.getAsJsonObject();
                    out.add(new Site(o.get("name").getAsString(), o.get("kind").getAsInt(), MonolithWorld.pos(o.getAsJsonArray("size")), MonolithWorld.pos(o.getAsJsonArray("altar")), o.has("rootOffset") ? MonolithWorld.pos(o.getAsJsonArray("rootOffset")) : BlockPos.f_121853_, o.has("biome") ? new ResourceLocation(o.get("biome").getAsString()) : BIOME, o.has("foundation") ? new ResourceLocation(o.get("foundation").getAsString()) : MonolithContent.id("monolith_bleached_stone"), o.has("stairs") ? new ResourceLocation(o.get("stairs").getAsString()) : new ResourceLocation("minecraft:quartz_stairs")));
                }
                list = List.copyOf(out);
            }
            return list;
        }
        catch (IOException ex) {
            throw new IllegalStateException("Missing monolith site definitions", ex);
        }
    }

    public static List<Site> sites() {
        return SITES;
    }

    public static CompoundTag state(ServerPlayer p) {
        CompoundTag root = MythicService.state((ServerPlayer)p).gloom;
        if (!root.m_128425_("Monolith", 10)) {
            root.m_128365_("Monolith", (Tag)new CompoundTag());
        }
        return root.m_128469_("Monolith");
    }

    private static CompoundTag progress(ServerPlayer p, Site site) {
        CompoundTag n = MonolithWorld.state(p);
        if (!n.m_128425_(site.name(), 10)) {
            n.m_128365_(site.name(), (Tag)new CompoundTag());
        }
        return n.m_128469_(site.name());
    }

    private static void dirty(ServerPlayer p) {
        LivingRealm.dirty(p);
    }

    private static List<Cell> plan(Site site) {
        return PLANS.computeIfAbsent(site.name(), key -> {
            List list;
            block10: {
                InputStream in = MonolithWorld.class.getResourceAsStream("/data/shadowsovereign/structures/monolith_blueprints/" + key + ".nbt");
                try {
                    CompoundTag n = NbtIo.m_128939_((InputStream)Objects.requireNonNull(in));
                    ArrayList<BlockState> palette = new ArrayList<BlockState>();
                    for (Tag t : n.m_128437_("palette", 10)) {
                        palette.add(NbtUtils.m_247651_((HolderGetter)BuiltInRegistries.f_256975_.m_255303_(), (CompoundTag)((CompoundTag)t)));
                    }
                    ArrayList<Cell> out = new ArrayList<Cell>();
                    for (Tag t : n.m_128437_("blocks", 10)) {
                        CompoundTag b = (CompoundTag)t;
                        ListTag v = b.m_128437_("pos", 3);
                        out.add(new Cell(new BlockPos(v.m_128763_(0), v.m_128763_(1), v.m_128763_(2)), (BlockState)palette.get(b.m_128451_("state")), b.m_128425_("nbt", 10) ? b.m_128469_("nbt").m_6426_() : null));
                    }
                    out.sort(Comparator.comparingInt(c -> c.state().m_60734_() instanceof LiquidBlock ? 1 : 0).thenComparingInt(c -> c.pos().m_123343_() >> 4).thenComparingInt(c -> c.pos().m_123341_() >> 4).thenComparingInt(c -> c.pos().m_123342_()).thenComparingInt(c -> c.pos().m_123343_()).thenComparingInt(c -> c.pos().m_123341_()));
                    list = List.copyOf(out);
                    if (in == null) break block10;
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
                        throw new IllegalStateException("Invalid monolith blueprint " + key, e);
                    }
                }
                in.close();
            }
            return list;
        });
    }

    private static boolean load(ServerLevel w, BlockPos at) {
        if (w.m_46805_(at)) {
            return true;
        }
        ChunkPos cp = new ChunkPos(at);
        long key = cp.m_45588_();
        if (held.add(key)) {
            w.m_7726_().m_8387_(TICKET, cp, 0, (Object)cp);
            while (held.size() > 32) {
                long old = (Long)held.iterator().next();
                held.remove(old);
                ChunkPos p = new ChunkPos(old);
                w.m_7726_().m_8438_(TICKET, p, 0, (Object)p);
                requests.remove(old);
            }
        }
        requests.computeIfAbsent(key, k -> w.m_7726_().m_8431_(cp.f_45578_, cp.f_45579_, ChunkStatus.f_62326_, true));
        return false;
    }

    private static void release(ServerLevel w) {
        Iterator iterator = held.iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            ChunkPos c = new ChunkPos(key);
            w.m_7726_().m_8438_(TICKET, c, 0, (Object)c);
        }
        held.clear();
        requests.clear();
    }

    @SubscribeEvent
    public static void stopping(ServerStoppingEvent e) {
        ServerLevel w = e.getServer().m_129880_(OdysseyRealm.KEY);
        if (w != null) {
            MonolithWorld.release(w);
        }
        PLANS.clear();
        turn = 0;
    }

    private static boolean inside(BlockPos p, BlockPos origin, BlockPos size) {
        return p.m_123341_() >= origin.m_123341_() && p.m_123341_() < origin.m_123341_() + size.m_123341_() && p.m_123342_() >= origin.m_123342_() && p.m_123342_() < origin.m_123342_() + size.m_123342_() && p.m_123343_() >= origin.m_123343_() && p.m_123343_() < origin.m_123343_() + size.m_123343_();
    }

    public static BlockPos altar(StructureStart start, Site site) {
        Object e;
        if (!start.m_73602_().isEmpty() && (e = start.m_73602_().get(0)) instanceof PoolElementStructurePiece) {
            PoolElementStructurePiece p = (PoolElementStructurePiece)e;
            return StructureTemplate.m_74593_((BlockPos)site.altar().m_121996_((Vec3i)site.rootOffset()), (Mirror)Mirror.NONE, (Rotation)p.m_6830_(), (BlockPos)BlockPos.f_121853_).m_121955_((Vec3i)p.m_72646_());
        }
        return start.m_73601_().m_162394_();
    }

    public static boolean protectedSite(ServerLevel w, BlockPos at) {
        if (!w.m_46472_().equals(OdysseyRealm.KEY)) {
            return false;
        }
        for (Site site : SITES) {
            Structure s = (Structure)w.m_9598_().m_175515_(Registries.f_256944_).m_7745_(MonolithContent.id(site.name()));
            if (s == null || !w.m_215010_().m_220494_(at, s).m_73603_()) continue;
            return true;
        }
        for (AdventureData.Journal journal : AdventureData.get((MinecraftServer)w.m_7654_()).owners.values()) {
            CompoundTag n = journal.mythic.gloom.m_128469_("Monolith");
            for (Site site : SITES) {
                CompoundTag s = n.m_128469_(site.name());
                if (!s.m_128441_("Origin") || !MonolithWorld.inside(at, BlockPos.m_122022_((long)s.m_128454_("Origin")), site.size())) continue;
                return true;
            }
        }
        return false;
    }

    private static void encounters(ServerPlayer p) {
        if (p.f_19797_ % 10 != 0 || p.m_5833_() || AdventureService.journal((ServerPlayer)p).session != null || OdysseyRealm.foreignPlot(p, p.m_20183_())) {
            return;
        }
        ServerLevel w = p.m_284548_();
        for (Site site : SITES) {
            StructureStart start;
            Structure structure;
            if (MythicExpedition.state(p, site.kind()).m_128451_("Status") == 2) continue;
            BlockPos at = null;
            CompoundTag own = MonolithWorld.progress(p, site);
            if (own.m_128471_("Ready")) {
                at = BlockPos.m_122022_((long)own.m_128454_("Origin")).m_121955_((Vec3i)site.altar());
            }
            if ((structure = (Structure)w.m_9598_().m_175515_(Registries.f_256944_).m_7745_(MonolithContent.id(site.name()))) != null && (start = w.m_215010_().m_220494_(p.m_20183_(), structure)).m_73603_()) {
                at = MonolithWorld.altar(start, site);
            }
            if (at == null || !(p.m_20238_(Vec3.m_82512_((Vec3i)at)) < 484.0)) continue;
            if (site.kind() == 20) {
                start = structure == null ? null : w.m_215010_().m_220494_(p.m_20183_(), structure);
                BelfryNests.visit(p, site, start, own.m_128441_("Origin") ? BlockPos.m_122022_((long)own.m_128454_("Origin")) : null, at);
            }
            MythicExpedition.state(p, site.kind()).m_128356_("Lair", at.m_121878_());
            MythicExpedition.start(p, site.kind());
            return;
        }
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
        ArrayList<ServerPlayer> builders = new ArrayList<ServerPlayer>();
        for (ServerPlayer p : w.m_6907_()) {
            MonolithWorld.encounters(p);
            if (e.getServer().m_129910_().m_246337_().m_247749_() || MonolithWorld.state(p).m_128471_("Suspended") || !GuideService.state((ServerPlayer)p).plotReady || !SITES.stream().anyMatch(s -> !MonolithWorld.progress(p, s).m_128471_("Ready"))) continue;
            builders.add(p);
        }
        if (builders.isEmpty()) {
            if (!held.isEmpty()) {
                MonolithWorld.release(w);
            }
            return;
        }
        MonolithWorld.build((ServerPlayer)builders.get(Math.floorMod(turn++, builders.size())));
    }

    private static void build(ServerPlayer p) {
        ServerLevel w = p.m_284548_();
        Site site = SITES.stream().filter(s -> !MonolithWorld.progress(p, s).m_128471_("Ready")).findFirst().orElse(null);
        if (site == null) {
            return;
        }
        CompoundTag n = MonolithWorld.progress(p, site);
        if (n.m_128471_("Blocked")) {
            return;
        }
        if (!n.m_128441_("Origin")) {
            int i;
            BlockPos home = OdysseyRealm.home(p);
            int end = i + 8;
            for (i = n.m_128451_("Search"); i < end; ++i) {
                double angle = (double)i * 2.3999632297 + (double)(p.m_20148_().getLeastSignificantBits() & 0x3FFL) * 0.01 + (double)site.kind();
                double radius = 1024 + i / 16 * 256;
                int x = home.m_123341_() + (int)(Math.cos(angle) * radius);
                int z = home.m_123343_() + (int)(Math.sin(angle) * radius);
                BlockPos at = new BlockPos(x, 80, z);
                boolean occupied = false;
                for (Site s2 : SITES) {
                    CompoundTag other = MonolithWorld.progress(p, s2);
                    if (!other.m_128441_("Origin") || !(BlockPos.m_122022_((long)other.m_128454_("Origin")).m_123331_((Vec3i)at) < 32400.0)) continue;
                    occupied = true;
                }
                for (AdventureData.Journal journal : AdventureData.get((MinecraftServer)p.f_8924_).owners.values()) {
                    CompoundTag claimed = journal.mythic.gloom.m_128469_("Monolith");
                    for (Site otherSite : SITES) {
                        BlockPos o;
                        CompoundTag claim = claimed.m_128469_(otherSite.name());
                        if (!claim.m_128441_("Origin") || x >= (o = BlockPos.m_122022_((long)claim.m_128454_("Origin"))).m_123341_() + otherSite.size().m_123341_() + 12 || x + site.size().m_123341_() + 12 <= o.m_123341_() || z - 76 >= o.m_123343_() + otherSite.size().m_123343_() + 12 || z + site.size().m_123343_() + 12 <= o.m_123343_() - 76) continue;
                        occupied = true;
                    }
                }
                if (!w.m_6857_().m_61937_(at) || !w.m_6857_().m_61937_(at.m_7918_(site.size().m_123341_(), 0, site.size().m_123343_() + 64))) {
                    occupied = true;
                }
                if (occupied || OdysseyRealm.foreignPlot(p, at) || OdysseyRealm.foreignPlot(p, at.m_7918_(site.size().m_123341_(), 0, site.size().m_123343_())) || !w.m_7726_().m_8481_().m_62218_().m_203407_(x + site.size().m_123341_() / 2 >> 2, 10, z + site.size().m_123343_() / 2 >> 2, w.m_7726_().m_214994_().m_224579_()).m_203373_(site.biome())) continue;
                n.m_128356_("Origin", at.m_121878_());
                n.m_128405_("Scan", 0);
                n.m_128405_("ScanLayout", 1);
                p.m_5661_((Component)Component.m_237115_((String)"monolith.shadowsovereign.preparing"), false);
                break;
            }
            n.m_128405_("Search", i + 1);
            MonolithWorld.dirty(p);
            return;
        }
        BlockPos origin = BlockPos.m_122022_((long)n.m_128454_("Origin"));
        int volume = site.size().m_123341_() * site.size().m_123342_() * site.size().m_123343_();
        if (!n.m_128471_("Reserved") && n.m_128451_("ScanLayout") != 1) {
            n.m_128405_("Scan", 0);
            n.m_128405_("ScanLayout", 1);
        }
        if (!n.m_128471_("Reserved")) {
            int cursor;
            int end = Math.min(volume, cursor + 1024);
            for (cursor = Math.max(0, n.m_128451_("Scan")); cursor < end; ++cursor) {
                int z;
                int y = cursor % site.size().m_123342_();
                int column = cursor / site.size().m_123342_();
                int x = column % site.size().m_123341_();
                BlockPos at = origin.m_7918_(x, y, z = column / site.size().m_123341_());
                if (!MonolithWorld.load(w, at)) {
                    n.m_128405_("Scan", cursor);
                    MonolithWorld.dirty(p);
                    return;
                }
                if (w.m_8055_(at).m_60795_()) continue;
                n.m_128473_("Origin");
                n.m_128405_("Scan", 0);
                MonolithWorld.dirty(p);
                return;
            }
            n.m_128405_("Scan", cursor);
            if (cursor >= volume) {
                n.m_128379_("Reserved", true);
            }
            MonolithWorld.dirty(p);
            return;
        }
        if (n.m_128451_("CellOrder") != 1) {
            n.m_128405_("Cursor", 0);
            n.m_128405_("CellOrder", 1);
        }
        List<Cell> cells = MonolithWorld.plan(site);
        int foundation = site.size().m_123341_() * site.size().m_123343_() * 48;
        int cursor = Math.max(0, n.m_128451_("Cursor"));
        int total = cells.size() + foundation + 192;
        int writes = 0;
        for (int operations = 0; cursor < total && operations < 4096 && writes < 256; ++cursor, ++operations) {
            if (cursor < cells.size()) {
                Cell cell = cells.get(cursor);
                BlockPos at = origin.m_121955_((Vec3i)cell.pos());
                if (!MonolithWorld.load(w, at)) break;
                if (w.m_8055_(at).equals(cell.state())) continue;
                w.m_7731_(at, cell.state(), 2);
                ++writes;
                if (cell.nbt() == null || w.m_7702_(at) == null) continue;
                CompoundTag data = cell.nbt().m_6426_();
                data.m_128405_("x", at.m_123341_());
                data.m_128405_("y", at.m_123342_());
                data.m_128405_("z", at.m_123343_());
                w.m_7702_(at).m_142466_(data);
                w.m_7702_(at).m_6596_();
                continue;
            }
            if (cursor < cells.size() + foundation) {
                int c = cursor - cells.size();
                int column = c / 48;
                int depth = c % 48;
                int x = column % site.size().m_123341_();
                int z = column / site.size().m_123341_();
                if (x % 8 != 0 || z % 8 != 0) continue;
                BlockPos top = origin.m_7918_(x, -1, z);
                if (!MonolithWorld.load(w, top)) break;
                if (depth == 0) {
                    int air;
                    for (air = 0; air < 48 && w.m_8055_(top.m_6625_(air)).m_60795_(); ++air) {
                    }
                    n.m_128405_("SupportDepth", air);
                }
                if (depth >= n.m_128451_("SupportDepth") || !w.m_8055_(top.m_6625_(depth)).m_60795_()) continue;
                w.m_7731_(top.m_6625_(depth), ((Block)BuiltInRegistries.f_256975_.m_7745_(site.foundation())).m_49966_(), 2);
                ++writes;
                continue;
            }
            int c = cursor - cells.size() - foundation;
            int step = c / 3;
            int side = c % 3 - 1;
            BlockPos at = origin.m_7918_(site.size().m_123341_() / 2 + side, -step, -1 - step);
            if (!MonolithWorld.load(w, at)) break;
            if (n.m_128471_("StairsLanded")) continue;
            if (!w.m_8055_(at).m_60795_()) {
                n.m_128379_("StairsLanded", true);
                continue;
            }
            w.m_7731_(at, (BlockState)((Block)BuiltInRegistries.f_256975_.m_7745_(site.stairs())).m_49966_().m_61124_((Property)StairBlock.f_56841_, (Comparable)Direction.SOUTH), 2);
            ++writes;
        }
        n.m_128405_("Cursor", cursor);
        if (cursor >= total) {
            n.m_128379_("Ready", true);
            BlockPos at = origin.m_121955_((Vec3i)site.altar());
            MythicExpedition.state(p, site.kind()).m_128356_("Lair", at.m_121878_());
            MonolithWorld.dirty(p);
            p.m_5661_((Component)Component.m_237110_((String)"monolith.shadowsovereign.ready", (Object[])new Object[]{at.m_123341_(), at.m_123343_()}), false);
        }
        MonolithWorld.dirty(p);
    }

    public static List<Integer> focusKinds() {
        return Stream.concat(SITES.stream().map(Site::kind), MythicSites.sites().stream().filter(v -> v.kind() >= 23).map(MythicSites.Site::kind)).toList();
    }

    public static BlockPos locate(ServerPlayer p, int kind) {
        Site site = SITES.stream().filter(s -> s.kind() == kind).findFirst().orElse(null);
        if (site == null) {
            MythicSites.Site nativeSite = MythicSites.sites().stream().filter(v -> v.kind() == kind).findFirst().orElse(null);
            if (nativeSite == null || !p.f_8924_.m_129910_().m_246337_().m_247749_()) {
                return null;
            }
            ResourceKey nativeKey = ResourceKey.m_135785_((ResourceKey)Registries.f_256944_, (ResourceLocation)nativeSite.structure());
            Holder.Reference nativeHolder = p.f_8924_.m_206579_().m_175515_(Registries.f_256944_).m_246971_(nativeKey);
            Pair result = p.m_284548_().m_7726_().m_8481_().m_223037_(p.m_284548_(), (HolderSet)HolderSet.m_205809_((Holder[])new Holder[]{nativeHolder}), p.m_20183_(), 32, false);
            return result == null || OdysseyRealm.foreignPlot(p, (BlockPos)result.getFirst()) ? null : (BlockPos)result.getFirst();
        }
        if (!p.f_8924_.m_129910_().m_246337_().m_247749_()) {
            CompoundTag own = MonolithWorld.progress(p, site);
            return own.m_128471_("Ready") ? BlockPos.m_122022_((long)own.m_128454_("Origin")).m_121955_((Vec3i)site.altar()) : null;
        }
        ResourceKey key = ResourceKey.m_135785_((ResourceKey)Registries.f_256944_, (ResourceLocation)MonolithContent.id(site.name()));
        Holder.Reference holder = p.f_8924_.m_206579_().m_175515_(Registries.f_256944_).m_246971_(key);
        Pair found = p.m_284548_().m_7726_().m_8481_().m_223037_(p.m_284548_(), (HolderSet)HolderSet.m_205809_((Holder[])new Holder[]{holder}), p.m_20183_(), 32, false);
        return found == null || OdysseyRealm.foreignPlot(p, (BlockPos)found.getFirst()) ? null : (BlockPos)found.getFirst();
    }

    public static void cycleCompass(ServerPlayer p, ItemStack stack) {
        int focus = Math.floorMod(stack.m_41784_().m_128451_("MonolithFocus") + 1, MonolithWorld.focusKinds().size() + 1);
        stack.m_41784_().m_128405_("MonolithFocus", focus);
        p.m_5661_((Component)Component.m_237110_((String)"monolith.shadowsovereign.compass_focus", (Object[])new Object[]{focus == 0 ? Component.m_237115_((String)"monolith.shadowsovereign.all_sites") : Component.m_237115_((String)("entity.shadowsovereign." + MythicExpedition.IDS[MonolithWorld.focusKinds().get(focus - 1)]))}), false);
    }

    public static BlockPos nearest(ServerPlayer p) {
        BlockPos best = null;
        for (Site site : SITES) {
            CompoundTag n = MonolithWorld.progress(p, site);
            if (!n.m_128471_("Ready")) continue;
            BlockPos at = BlockPos.m_122022_((long)n.m_128454_("Origin")).m_121955_((Vec3i)site.altar());
            if (best != null && !(p.m_20183_().m_123331_((Vec3i)at) < p.m_20183_().m_123331_((Vec3i)best))) continue;
            best = at;
        }
        return best;
    }

    public static CompoundTag view(ServerPlayer p) {
        String phase;
        CompoundTag out = new CompoundTag();
        int ready = 0;
        Site active = null;
        for (Site site : SITES) {
            if (MonolithWorld.progress(p, site).m_128471_("Ready")) {
                ++ready;
                continue;
            }
            if (active != null) continue;
            active = site;
        }
        out.m_128405_("Ready", ready);
        out.m_128405_("Sites", SITES.size());
        String string = p.f_8924_.m_129910_().m_246337_().m_247749_() ? "native" : (phase = active == null ? "ready" : "search");
        if (active != null && !phase.equals("native")) {
            CompoundTag n = MonolithWorld.progress(p, active);
            if (n.m_128471_("Reserved")) {
                phase = "build";
                out.m_128405_("Current", n.m_128451_("Cursor"));
                out.m_128405_("Total", MonolithWorld.plan(active).size() + active.size().m_123341_() * active.size().m_123343_() * 48 + 192);
            } else if (n.m_128441_("Origin")) {
                phase = "scan";
                out.m_128405_("Current", n.m_128451_("Scan"));
                out.m_128405_("Total", active.size().m_123341_() * active.size().m_123342_() * active.size().m_123343_());
            }
        }
        out.m_128359_("Phase", phase);
        return out;
    }

    public static String status(ServerPlayer p) {
        int ready = 0;
        Object phase = "search";
        for (Site site : SITES) {
            CompoundTag n = MonolithWorld.progress(p, site);
            if (n.m_128471_("Ready")) {
                ++ready;
                continue;
            }
            if (n.m_128471_("Reserved")) {
                phase = "build " + n.m_128451_("Cursor") + "/" + (MonolithWorld.plan(site).size() + site.size().m_123341_() * site.size().m_123343_() * 48 + 192);
                continue;
            }
            if (!n.m_128441_("Origin")) continue;
            phase = "scan " + n.m_128451_("Scan");
        }
        return ready + "/" + SITES.size() + " \u00b7 " + (String)(ready == SITES.size() ? "ready" : phase);
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent e) {
        e.getDispatcher().register((LiteralArgumentBuilder)Commands.m_82127_((String)"shadow").then(Commands.m_82127_((String)"monolith").then(Commands.m_82127_((String)"status").executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            ((CommandSourceStack)c.getSource()).m_288197_(() -> Component.m_237110_((String)"monolith.shadowsovereign.status", (Object[])new Object[]{MonolithWorld.status(p)}), false);
            return 1;
        }))));
    }

    @SubscribeEvent
    public static void tools(BlockEvent.BlockToolModificationEvent e) {
        ServerPlayer s;
        if (e.isCanceled()) {
            return;
        }
        Player p = e.getPlayer();
        if (p instanceof ServerPlayer && OdysseyRealm.inside(s = (ServerPlayer)p) && !ExpanseWorld.editable(s, e.getPos())) {
            return;
        }
        BlockState b = e.getState();
        if (e.getToolAction() == ToolActions.AXE_STRIP && b.m_60713_(MonolithContent.b("ashen_log"))) {
            e.setFinalState((BlockState)MonolithContent.b("stripped_ashen_log").m_49966_().m_61124_((Property)RotatedPillarBlock.f_55923_, (Comparable)((Direction.Axis)b.m_61143_((Property)RotatedPillarBlock.f_55923_))));
        }
        if (e.getToolAction() == ToolActions.HOE_TILL && (b.m_60713_(MonolithContent.b("ashen_crystal_soil")) || b.m_60713_(MonolithContent.b("frost_ash_turf"))) && e.getLevel().m_8055_(e.getPos().m_7494_()).m_60795_()) {
            e.setFinalState(((Block)GloomContent.FARMLAND.get()).m_49966_());
        }
    }

    @SubscribeEvent
    public static void chest(PlayerInteractEvent.RightClickBlock e) {
        ServerPlayer p;
        Player player = e.getEntity();
        if (!(player instanceof ServerPlayer && OdysseyRealm.inside(p = (ServerPlayer)player) && p.m_9236_().m_8055_(e.getPos()).m_60713_(Blocks.f_50087_))) {
            return;
        }
        for (Site site : SITES) {
            if (site.kind() != 8 && site.kind() < 13) continue;
            boolean belongs = false;
            for (Map.Entry<UUID, AdventureData.Journal> owner : AdventureData.get((MinecraftServer)p.f_8924_).owners.entrySet()) {
                CompoundTag claim = owner.getValue().mythic.gloom.m_128469_("Monolith").m_128469_(site.name());
                if (!claim.m_128441_("Origin") || !MonolithWorld.inside(e.getPos(), BlockPos.m_122022_((long)claim.m_128454_("Origin")), site.size())) continue;
                if (!owner.getKey().equals(p.m_20148_())) {
                    e.setCanceled(true);
                    p.m_5661_((Component)Component.m_237115_((String)"monolith.shadowsovereign.foreign_site"), true);
                    return;
                }
                belongs = true;
            }
            Structure structure = (Structure)p.f_8924_.m_206579_().m_175515_(Registries.f_256944_).m_7745_(MonolithContent.id(site.name()));
            if (structure != null && p.m_284548_().m_215010_().m_220494_(e.getPos(), structure).m_73603_()) {
                belongs = true;
            }
            if (!belongs || MythicExpedition.state(p, site.kind()).m_128451_("Status") == 2) continue;
            e.setCanceled(true);
            p.m_5661_((Component)(site.kind() == 8 ? Component.m_237115_((String)"monolith.shadowsovereign.chest_locked") : Component.m_237110_((String)"monolith.shadowsovereign.legend_seal", (Object[])new Object[]{Component.m_237115_((String)("entity.shadowsovereign." + MythicExpedition.IDS[site.kind()]))})), true);
            return;
        }
    }

    public record Site(String name, int kind, BlockPos size, BlockPos altar, BlockPos rootOffset, ResourceLocation biome, ResourceLocation foundation, ResourceLocation stairs) {
        public Site(String name, int kind, BlockPos size, BlockPos altar, BlockPos rootOffset) {
            this(name, kind, size, altar, rootOffset, BIOME, MonolithContent.id("monolith_bleached_stone"), new ResourceLocation("minecraft:quartz_stairs"));
        }
    }

    private record Cell(BlockPos pos, BlockState state, CompoundTag nbt) {
    }
}

