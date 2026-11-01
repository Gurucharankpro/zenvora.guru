/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.EntityTeleportEvent$EnderEntity
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.portal.SoulGates;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

public final class OdysseyRealm {
    public static final ResourceKey<Level> KEY = ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)new ResourceLocation("shadowsovereign", "echo_realms"));
    private static final Set<String> changing = new HashSet<String>();
    private static final Map<UUID, Contact> contacts = new HashMap<UUID, Contact>();

    public static boolean inside(ServerPlayer p) {
        return p.m_9236_().m_46472_().equals(KEY);
    }

    public static BlockPos home(ServerPlayer p) {
        int slot = GuideService.state((ServerPlayer)p).plot;
        return new BlockPos(Math.floorMod(slot, 64) * 2048 + 256, 80, Math.max(0, slot / 64) * 2048 + 256);
    }

    public static BlockPos arena(ServerPlayer p, int region) {
        return OdysseyRealm.home(p).m_7918_((new int[]{384, 0, -384, 0})[Math.floorMod(region, 4)], 0, (new int[]{0, 384, 0, -384})[Math.floorMod(region, 4)]);
    }

    public static boolean owns(ServerPlayer p, BlockPos at) {
        if (!OdysseyRealm.inside(p) || GuideService.state((ServerPlayer)p).plot < 0) {
            return false;
        }
        BlockPos home = OdysseyRealm.home(p);
        return Math.abs(at.m_123341_() - home.m_123341_()) <= 700 && Math.abs(at.m_123343_() - home.m_123343_()) <= 700;
    }

    public static boolean foreignPlot(ServerPlayer p, BlockPos at) {
        int x = Math.floorDiv(at.m_123341_() - 256 + 1024, 2048);
        int z = Math.floorDiv(at.m_123343_() - 256 + 1024, 2048);
        if (x < 0 || x >= 64 || z < 0 || z >= 64) {
            return false;
        }
        int slot = z * 64 + x;
        int cx = x * 2048 + 256;
        int cz = z * 2048 + 256;
        if (Math.abs(at.m_123341_() - cx) > 700 || Math.abs(at.m_123343_() - cz) > 700) {
            return false;
        }
        for (Map.Entry<UUID, Integer> e : Plots.get((MinecraftServer)p.f_8924_).owners.entrySet()) {
            if (e.getValue() != slot) continue;
            return !e.getKey().equals(p.m_20148_());
        }
        return false;
    }

    public static boolean camp(ServerPlayer p, BlockPos at) {
        if (!OdysseyRealm.owns(p, at)) {
            return false;
        }
        BlockPos home = OdysseyRealm.home(p);
        return Math.abs(at.m_123341_() - home.m_123341_()) < 128 && Math.abs(at.m_123343_() - home.m_123343_()) < 128;
    }

    public static boolean assign(ServerPlayer p) {
        int slot;
        OdysseyState s = GuideService.state(p);
        Plots plots = Plots.get(p.f_8924_);
        if (s.plot >= 0 && !plots.owners.containsKey(p.m_20148_()) && !plots.owners.containsValue(s.plot)) {
            plots.owners.put(p.m_20148_(), s.plot);
            plots.m_77762_();
        }
        if ((slot = plots.allocate(p.m_20148_())) != s.plot) {
            if (s.plot >= 0) {
                OdysseyText.tell(p, "realm_unavailable", new Object[0]);
                return false;
            }
            s.plot = slot;
            s.seed = p.m_20148_().getLeastSignificantBits() ^ p.f_8924_.m_129783_().m_7328_();
            GuideService.dirty(p);
        }
        return s.plot >= 0;
    }

    private static boolean frame(ServerLevel world, BlockPos core, Direction direction) {
        if (!world.m_8055_(core).m_60713_((Block)OdysseyContent.STATIONS.get((Object)OdysseyContent.Station.GATE).get())) {
            return false;
        }
        for (BlockPos at : new SoulGates.Frame(core, direction).edges()) {
            if (!world.m_46805_(at)) {
                return false;
            }
            BlockState block = world.m_8055_(at);
            if (block.m_60713_((Block)SoulGates.FRAME.get()) || block.m_60713_(Blocks.f_50080_) || block.m_60713_(Blocks.f_50723_)) continue;
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean activate(ServerPlayer p, BlockPos core) {
        block12: {
            block11: {
                Object object;
                if (p.m_20238_(Vec3.m_82512_((Vec3i)core)) > 64.0 || !((object = p.m_9236_().m_7702_(core)) instanceof OdysseyContent.CampEntity)) break block11;
                OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)((Object)object);
                if (p.m_20148_().equals(be.owner)) break block12;
            }
            return false;
        }
        if (!(p.m_7500_() || !GuideService.army((ServerPlayer)p).shadows.isEmpty() && AdventureService.journal((ServerPlayer)p).bound)) {
            OdysseyText.tell(p, "gate_requirements", new Object[0]);
            return false;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!OdysseyRealm.frame(p.m_284548_(), core, direction)) continue;
            SoulGates.Frame f = new SoulGates.Frame(core, direction);
            for (BlockPos at : f.panels()) {
                if (p.m_9236_().m_8055_(at).m_60795_() || p.m_9236_().m_8055_(at).m_60713_((Block)OdysseyContent.PORTAL.get())) continue;
                OdysseyText.tell(p, "gate_blocked", new Object[0]);
                return false;
            }
            String key = String.valueOf(p.m_9236_().m_46472_().m_135782_()) + ":" + core.m_121878_();
            changing.add(key);
            try {
                for (BlockPos at : f.panels()) {
                    p.m_9236_().m_7731_(at, (BlockState)((Block)OdysseyContent.PORTAL.get()).m_49966_().m_61124_((Property)BlockStateProperties.f_61364_, (Comparable)direction.m_122434_()), 3);
                    BlockEntity blockEntity = p.m_9236_().m_7702_(at);
                    if (!(blockEntity instanceof OdysseyContent.CampEntity)) continue;
                    OdysseyContent.CampEntity panel = (OdysseyContent.CampEntity)blockEntity;
                    panel.owner = p.m_20148_();
                    panel.core = core;
                    panel.direction = direction;
                    panel.m_6596_();
                    p.m_9236_().m_7260_(at, panel.m_58900_(), panel.m_58900_(), 3);
                }
            }
            finally {
                changing.remove(key);
            }
            OdysseyText.tell(p, "gate_awake", new Object[0]);
            return true;
        }
        OdysseyText.tell(p, "gate_frame", new Object[0]);
        return false;
    }

    public static void clearContact(UUID owner) {
        contacts.remove(owner);
    }

    public static void clearContacts() {
        contacts.clear();
    }

    public static void contact(ServerPlayer p, ServerLevel source, BlockPos panel, BlockPos core) {
        if (p.m_9236_() != source || GuideService.now(p) < p.getPersistentData().m_128454_("OdysseyGateAt") || contacts.size() >= 1024 && !contacts.containsKey(p.m_20148_())) {
            return;
        }
        contacts.put(p.m_20148_(), new Contact((ResourceKey<Level>)source.m_46472_(), core.m_7949_(), panel.m_7949_(), GuideService.now(p)));
    }

    public static void portalTick(ServerPlayer p) {
        Contact t;
        block5: {
            block4: {
                BlockEntity blockEntity;
                t = contacts.remove(p.m_20148_());
                if (t == null || !p.m_9236_().m_46472_().equals(t.dimension) || GuideService.now(p) - t.tick > 2L || GuideService.now(p) < p.getPersistentData().m_128454_("OdysseyGateAt") || !p.m_6084_() || p.m_5833_() || p.m_20159_() || p.m_20238_(Vec3.m_82512_((Vec3i)t.panel)) > 6.25) {
                    return;
                }
                if (!p.m_284548_().m_46805_(t.panel) || !((blockEntity = p.m_9236_().m_7702_(t.panel)) instanceof OdysseyContent.CampEntity)) break block4;
                OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)blockEntity;
                if (p.m_20148_().equals(be.owner) && be.core != null && be.core.equals((Object)t.core) && OdysseyRealm.frame(p.m_284548_(), be.core, be.direction)) break block5;
            }
            return;
        }
        OdysseyRealm.request(p, t.core);
    }

    public static boolean request(ServerPlayer p, BlockPos core) {
        OdysseyState s;
        block10: {
            block9: {
                BlockEntity blockEntity;
                if (OdysseyRealm.inside(p)) {
                    return OdysseyRealm.leave(p);
                }
                s = GuideService.state(p);
                if (AdventureService.journal((ServerPlayer)p).session != null || VigilCrown.active(p) || !p.m_6084_() || p.m_20159_() || GuideService.now(p) < p.getPersistentData().m_128454_("OdysseyGateAt")) {
                    return false;
                }
                if (p.m_20238_(Vec3.m_82512_((Vec3i)core)) > 64.0 || !((blockEntity = p.m_9236_().m_7702_(core)) instanceof OdysseyContent.CampEntity)) break block9;
                OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)blockEntity;
                if (p.m_20148_().equals(be.owner)) break block10;
            }
            return false;
        }
        if (!p.m_7500_() && EclipseService.state((ServerPlayer)p).energy < 20.0f || !OdysseyRealm.assign(p)) {
            return false;
        }
        ServerLevel world = p.f_8924_.m_129880_(KEY);
        if (world == null || !world.m_6857_().m_61937_(OdysseyRealm.home(p).m_7918_(700, 0, 700)) || !world.m_6857_().m_61937_(OdysseyRealm.home(p).m_7918_(-700, 0, -700))) {
            OdysseyText.tell(p, "realm_unavailable", new Object[0]);
            return false;
        }
        if (!s.plotReady) {
            if (s.camp.m_128471_("EntryPending") && s.camp.m_128454_("EntryGate") == core.m_121878_() && s.camp.m_128461_("EntryDimension").equals(p.m_9236_().m_46472_().m_135782_().toString())) {
                return true;
            }
            s.camp.m_128356_("EntryGate", core.m_121878_());
            s.camp.m_128359_("EntryDimension", p.m_9236_().m_46472_().m_135782_().toString());
            s.camp.m_128379_("EntryPending", true);
            GuideService.dirty(p);
            OdysseyText.tell(p, "preparing", new Object[0]);
            return true;
        }
        return OdysseyRealm.enter(p);
    }

    public static void buildTick(ServerPlayer p) {
        OdysseyState s = GuideService.state(p);
        if (s.plot < 0 || s.plotReady || !s.camp.m_128471_("EntryPending")) {
            return;
        }
        ServerLevel world = p.f_8924_.m_129880_(KEY);
        if (world == null) {
            return;
        }
        BlockPos home = OdysseyRealm.home(p);
        int end = Math.min(65536, s.buildCursor + 1024);
        for (int i = s.buildCursor; i < end; ++i) {
            int x = (i & 0xFF) - 128;
            int z = (i >> 8) - 128;
            BlockPos at = home.m_7918_(x, 0, z);
            world.m_46745_(at);
            world.m_7731_(at, Math.abs(x) == 127 || Math.abs(z) == 127 ? Blocks.f_50735_.m_49966_() : (Math.abs(x) < 20 && Math.abs(z) < 20 ? Blocks.f_50470_.m_49966_() : Blocks.f_50440_.m_49966_()), 2);
        }
        s.buildCursor = end;
        if (end == 65536) {
            BlockEntity blockEntity;
            OdysseyRealm.decorate(world, home, p.m_20148_());
            s.plotReady = true;
            s.camp.m_128379_("EntryPending", false);
            String dim = s.camp.m_128461_("EntryDimension");
            BlockPos at = BlockPos.m_122022_((long)s.camp.m_128454_("EntryGate"));
            if (p.m_9236_().m_46472_().m_135782_().toString().equals(dim) && p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 64.0 && (blockEntity = p.m_9236_().m_7702_(at)) instanceof OdysseyContent.CampEntity) {
                OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)blockEntity;
                if (p.m_20148_().equals(be.owner)) {
                    OdysseyRealm.enter(p);
                }
            }
        }
        GuideService.dirty(p);
    }

    private static void decorate(ServerLevel world, BlockPos home, UUID owner) {
        for (int x = -8; x <= 8; ++x) {
            for (int z = -8; z <= 8; ++z) {
                if (Math.abs(x) != 8 && Math.abs(z) != 8 || Math.abs(x) <= 2 || Math.abs(z) <= 2) continue;
                world.m_7731_(home.m_7918_(x, 1, z), Blocks.f_50740_.m_49966_(), 2);
            }
        }
        for (BlockPos at : List.of(home.m_7918_(-6, 1, -6), home.m_7918_(6, 1, -6), home.m_7918_(-6, 1, 6), home.m_7918_(6, 1, 6))) {
            world.m_7731_(at, Blocks.f_50737_.m_49966_(), 2);
            world.m_7731_(at.m_7494_(), Blocks.f_50682_.m_49966_(), 2);
        }
        for (int x = -28; x <= 28; ++x) {
            for (int z = -28; z <= 28; ++z) {
                if (Math.abs(x) > 2 && Math.abs(z) > 2 || Math.abs(x) + Math.abs(z) <= 9) continue;
                world.m_7731_(home.m_7918_(x, 0, z), Blocks.f_50222_.m_49966_(), 2);
            }
        }
        for (int x : new int[]{-14, 14}) {
            for (int z : new int[]{-14, 14}) {
                BlockPos base = home.m_7918_(x, 1, z);
                for (int y = 0; y < 5; ++y) {
                    world.m_7731_(base.m_6630_(y), (y == 0 || y == 4 ? Blocks.f_50737_ : Blocks.f_50735_).m_49966_(), 2);
                }
                world.m_7731_(base.m_6630_(5), Blocks.f_50682_.m_49966_(), 2);
                for (int dx = -3; dx <= 3; ++dx) {
                    for (int dz = -3; dz <= 3; ++dz) {
                        if (Math.abs(dx) != 3 && Math.abs(dz) != 3) continue;
                        world.m_7731_(home.m_7918_(x + dx, 0, z + dz), Blocks.f_152544_.m_49966_(), 2);
                    }
                }
            }
        }
        for (int x : new int[]{-22, 22}) {
            for (int z = -20; z <= 20; z += 4) {
                world.m_7731_(home.m_7918_(x, 0, z), Blocks.f_152544_.m_49966_(), 2);
                world.m_7731_(home.m_7918_(x, 1, z), (z % 8 == 0 ? Blocks.f_152541_ : Blocks.f_152542_).m_49966_(), 2);
            }
        }
        BlockPos gate = home.m_7918_(0, 1, 7);
        world.m_7731_(gate, ((OdysseyContent.CampBlock)((Object)OdysseyContent.STATIONS.get((Object)OdysseyContent.Station.GATE).get())).m_49966_(), 3);
        BlockEntity blockEntity = world.m_7702_(gate);
        if (blockEntity instanceof OdysseyContent.CampEntity) {
            OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)blockEntity;
            be.owner = owner;
            be.m_6596_();
        }
        SoulGates.Frame f = new SoulGates.Frame(gate, Direction.EAST);
        for (BlockPos at : f.edges()) {
            world.m_7731_(at, ((Block)SoulGates.FRAME.get()).m_49966_(), 2);
        }
        world.m_7731_(home.m_7918_(0, 1, -7), ((OdysseyContent.CampBlock)((Object)OdysseyContent.STATIONS.get((Object)OdysseyContent.Station.NOTICE).get())).m_49966_(), 3);
        BlockEntity blockEntity2 = world.m_7702_(home.m_7918_(0, 1, -7));
        if (blockEntity2 instanceof OdysseyContent.CampEntity) {
            OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)blockEntity2;
            be.owner = owner;
            be.m_6596_();
        }
    }

    public static void safety(ServerPlayer p) {
        if (!OdysseyRealm.inside(p) || !GuideService.state((ServerPlayer)p).plotReady) {
            return;
        }
        if (p.m_20186_() < (double)(p.m_9236_().m_141937_() + 8) && p.m_6084_()) {
            EntityTeleportEvent.EnderEntity event;
            ServerLevel world = p.m_284548_();
            BlockPos home = OdysseyRealm.home(p);
            world.m_46745_(home);
            Vec3 safe = OdysseyRealm.playerSpot(p, world, home.m_7494_());
            if (safe != null && !(event = ForgeEventFactory.onEnderTeleport((LivingEntity)p, (double)safe.f_82479_, (double)safe.f_82480_, (double)safe.f_82481_)).isCanceled()) {
                p.m_6021_(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                p.f_19789_ = 0.0f;
            }
        }
        for (ShadowRecord r : GuideService.army((ServerPlayer)p).shadows.values()) {
            EntityTeleportEvent.EnderEntity event;
            Vec3 safe;
            Mob mob = ShadowService.active(p.f_8924_, r);
            if (mob == null || mob.m_9236_() != p.m_9236_() || !(mob.m_20186_() < (double)(mob.m_9236_().m_141937_() + 8)) || (safe = ShadowService.safePosition(mob, p.m_20182_(), p.m_284548_())) == null || (event = ForgeEventFactory.onEnderTeleport((LivingEntity)mob, (double)safe.f_82479_, (double)safe.f_82480_, (double)safe.f_82481_)).isCanceled()) continue;
            mob.m_6021_(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            mob.f_19789_ = 0.0f;
        }
    }

    private static List<ShadowRecord> travelers(ServerPlayer p) {
        return GuideService.army((ServerPlayer)p).shadows.values().stream().filter(r -> {
            Mob m = ShadowService.active(p.f_8924_, r);
            return !r.vigil.post && r.order == Order.FOLLOW && m != null && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 1024.0;
        }).toList();
    }

    public static boolean transfer(ServerPlayer p, ServerLevel to, Vec3 at) {
        block7: {
            block6: {
                if (!ForgeHooks.onTravelToDimension((Entity)p, (ResourceKey)to.m_46472_()) || !to.m_6857_().m_61937_(BlockPos.m_274446_((Position)at))) {
                    return false;
                }
                EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport((LivingEntity)p, (double)at.f_82479_, (double)at.f_82480_, (double)at.f_82481_);
                if (event.isCanceled()) break block6;
                Vec3 vec3 = new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                if (!(vec3.m_82557_(at) > 0.01)) break block7;
            }
            return false;
        }
        List<ShadowRecord> crew = OdysseyRealm.travelers(p);
        HashMap<UUID, Order> orders = new HashMap<UUID, Order>();
        for (ShadowRecord r : crew) {
            orders.put(r.id, r.order);
            ShadowService.recall(p, r);
        }
        p.m_8999_(to, at.f_82479_, at.f_82480_, at.f_82481_, p.m_146908_(), p.m_146909_());
        p.f_19789_ = 0.0f;
        ListTag pending = new ListTag();
        for (ShadowRecord r : crew) {
            r.order = (Order)((Object)orders.get(r.id));
            CompoundTag row = new CompoundTag();
            row.m_128362_("Id", r.id);
            pending.add((Object)row);
        }
        GuideService.state((ServerPlayer)p).camp.m_128365_("TravelCrew", (Tag)pending);
        GuideService.dirty(p);
        ArmyData.get(p.f_8924_).m_77762_();
        p.getPersistentData().m_128356_("OdysseyGateAt", GuideService.now(p) + 200L);
        return true;
    }

    public static void arrivals(ServerPlayer p) {
        if (GuideService.now(p) % 10L != 0L) {
            return;
        }
        CompoundTag camp = GuideService.state((ServerPlayer)p).camp;
        ListTag pending = camp.m_128437_("TravelCrew", 10);
        if (pending.isEmpty() || !p.m_284548_().m_143319_(p.m_146902_().m_45588_())) {
            return;
        }
        for (int i = pending.size() - 1; i >= 0; --i) {
            ShadowRecord r;
            CompoundTag row = pending.m_128728_(i);
            ShadowRecord shadowRecord = r = row.m_128403_("Id") ? GuideService.army((ServerPlayer)p).shadows.get(row.m_128342_("Id")) : null;
            if (r != null && !r.pendingRespawn() && r.activeEntity == null && !ShadowService.summon(p, r, false)) continue;
            pending.remove(i);
        }
        if (pending.isEmpty()) {
            camp.m_128473_("TravelCrew");
        } else {
            camp.m_128365_("TravelCrew", (Tag)pending);
        }
        GuideService.dirty(p);
    }

    private static boolean enter(ServerPlayer p) {
        OdysseyState s = GuideService.state(p);
        if (!s.plotReady || !p.m_7500_() && EclipseService.state((ServerPlayer)p).energy < 20.0f || AdventureService.journal((ServerPlayer)p).session != null || VigilCrown.active(p)) {
            return false;
        }
        ServerLevel destination = p.f_8924_.m_129880_(KEY);
        if (destination == null) {
            return false;
        }
        String dimension = p.m_9236_().m_46472_().m_135782_().toString();
        BlockPos pos = p.m_20183_();
        destination.m_46745_(OdysseyRealm.home(p));
        Vec3 landing = OdysseyRealm.playerSpot(p, destination, OdysseyRealm.home(p).m_7494_());
        if (landing == null) {
            p.m_5661_((Component)Component.m_237115_((String)"gloom.shadowsovereign.unsafe_arrival"), false);
            return false;
        }
        if (!OdysseyRealm.transfer(p, destination, landing)) {
            return false;
        }
        s.returnDimension = dimension;
        s.returnPos = pos;
        if (!p.m_7500_()) {
            EclipseService.state((ServerPlayer)p).energy -= 20.0f;
        }
        EclipseService.dirty(p);
        GuideService.record(p, "realm_enter");
        return true;
    }

    private static Vec3 playerSpot(ServerPlayer p, ServerLevel world, BlockPos center) {
        for (int i = 0; i < 96; ++i) {
            double angle = (double)i * 2.399963229728653;
            double radius = 2 + i % 7;
            int x = (int)Math.floor((double)center.m_123341_() + 0.5 + Math.cos(angle) * radius);
            int z = (int)Math.floor((double)center.m_123343_() + 0.5 + Math.sin(angle) * radius);
            world.m_6325_(x >> 4, z >> 4);
            for (int dy : new int[]{0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5, 6, -6, 7, -7}) {
                BlockPos at = new BlockPos(x, center.m_123342_() + dy, z);
                Vec3 point = Vec3.m_82539_((Vec3i)at).m_82520_(0.5, 0.0, 0.5);
                AABB box = p.m_6972_(p.m_20089_()).m_20393_(point);
                if (!world.m_6857_().m_61937_(at) || !world.m_6425_(at).m_76178_() || world.m_8055_(at.m_7495_()).m_60812_((BlockGetter)world, at.m_7495_()).m_83281_() || !world.m_45756_((Entity)p, box) || world.m_8055_(at).m_60713_((Block)OdysseyContent.PORTAL.get())) continue;
                return point;
            }
        }
        return null;
    }

    public static boolean leave(ServerPlayer p) {
        ServerLevel destination;
        if (!OdysseyRealm.inside(p) || VigilCrown.active(p) || !p.m_6084_()) {
            return false;
        }
        OdysseyState s = GuideService.state(p);
        ResourceLocation key = ResourceLocation.m_135820_((String)s.returnDimension);
        ServerLevel serverLevel = destination = key == null ? null : p.f_8924_.m_129880_(ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)key));
        if (destination == null) {
            destination = p.f_8924_.m_129783_();
        }
        BlockPos at = s.returnPos;
        destination.m_46745_(at);
        Vec3 spot = OdysseyRealm.playerSpot(p, destination, at);
        if (spot == null) {
            at = destination.m_220360_();
            destination.m_46745_(at);
            spot = OdysseyRealm.playerSpot(p, destination, at);
        }
        if (spot == null) {
            return false;
        }
        LivingRealm.finish(p, false);
        OdysseyRuns.finish(p, false);
        if (!OdysseyRealm.transfer(p, destination, spot)) {
            return false;
        }
        GuideService.record(p, "gateway_return");
        return true;
    }

    public static final class Plots
    extends SavedData {
        private final LinkedHashMap<UUID, Integer> owners = new LinkedHashMap();

        static Plots get(MinecraftServer server) {
            return (Plots)server.m_129783_().m_8895_().m_164861_(Plots::load, Plots::new, "soulbound_odyssey_plots");
        }

        static Plots load(CompoundTag n) {
            Plots p = new Plots();
            for (Tag tag : n.m_128437_("Plots", 10)) {
                CompoundTag row = (CompoundTag)tag;
                int slot = row.m_128451_("Slot");
                if (!row.m_128403_("Owner") || slot < 0 || slot >= 4096 || p.owners.containsValue(slot)) continue;
                p.owners.put(row.m_128342_("Owner"), slot);
            }
            return p;
        }

        public CompoundTag m_7176_(CompoundTag n) {
            ListTag list = new ListTag();
            this.owners.forEach((id, slot) -> {
                CompoundTag row = new CompoundTag();
                row.m_128362_("Owner", id);
                row.m_128405_("Slot", slot.intValue());
                list.add((Object)row);
            });
            n.m_128365_("Plots", (Tag)list);
            return n;
        }

        int allocate(UUID id) {
            if (this.owners.containsKey(id)) {
                return this.owners.get(id);
            }
            for (int i = 0; i < 4096; ++i) {
                if (this.owners.containsValue(i)) continue;
                this.owners.put(id, i);
                this.m_77762_();
                return i;
            }
            return -1;
        }
    }

    private record Contact(ResourceKey<Level> dimension, BlockPos core, BlockPos panel, long tick) {
    }

    public static final class Surface
    extends BaseEntityBlock {
        Surface() {
            super(BlockBehaviour.Properties.m_284310_().m_60978_(-1.0f).m_60910_().m_60955_().m_60953_(s -> 12).m_222994_());
            this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_((Property)BlockStateProperties.f_61364_, (Comparable)Direction.Axis.X));
        }

        protected void m_7926_(StateDefinition.Builder<Block, BlockState> b) {
            b.m_61104_(new Property[]{BlockStateProperties.f_61364_});
        }

        public RenderShape m_7514_(BlockState state) {
            return RenderShape.MODEL;
        }

        public BlockEntity m_142194_(BlockPos at, BlockState state) {
            return new OdysseyContent.CampEntity(at, state);
        }

        public void m_6807_(BlockState state, Level world, BlockPos at, BlockState old, boolean moving) {
            super.m_6807_(state, world, at, old, moving);
            if (!world.f_46443_) {
                world.m_186460_(at, (Block)this, 20);
            }
        }

        public void m_213897_(BlockState state, ServerLevel world, BlockPos at, RandomSource rng) {
            OdysseyContent.CampEntity be;
            block7: {
                block6: {
                    BlockEntity blockEntity = world.m_7702_(at);
                    if (!(blockEntity instanceof OdysseyContent.CampEntity)) break block6;
                    be = (OdysseyContent.CampEntity)blockEntity;
                    if (be.core != null) break block7;
                }
                world.m_7471_(at, false);
                return;
            }
            if (!world.m_46805_(be.core)) {
                world.m_186460_(at, (Block)this, 20);
                return;
            }
            if (!OdysseyRealm.frame(world, be.core, be.direction)) {
                for (BlockPos edge : new SoulGates.Frame(be.core, be.direction).edges()) {
                    if (world.m_46805_(edge)) continue;
                    world.m_186460_(at, (Block)this, 20);
                    return;
                }
                world.m_7471_(at, false);
                return;
            }
            world.m_186460_(at, (Block)this, 20);
        }

        public void m_7892_(BlockState state, Level world, BlockPos at, Entity entity) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)entity;
                if (world instanceof ServerLevel) {
                    BlockEntity blockEntity;
                    ServerLevel source = (ServerLevel)world;
                    if (p.m_9236_() == source && (blockEntity = world.m_7702_(at)) instanceof OdysseyContent.CampEntity) {
                        OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)blockEntity;
                        if (p.m_20148_().equals(be.owner) && be.core != null && OdysseyRealm.frame(source, be.core, be.direction)) {
                            OdysseyRealm.contact(p, source, at, be.core);
                        }
                    }
                }
            }
        }

        public void m_214162_(BlockState state, Level world, BlockPos at, RandomSource random) {
            world.m_7106_((ParticleOptions)ParticleTypes.f_123789_, (double)at.m_123341_() + random.m_188500_(), (double)at.m_123342_() + random.m_188500_(), (double)at.m_123343_() + random.m_188500_(), 0.0, 0.01, 0.0);
        }
    }
}

