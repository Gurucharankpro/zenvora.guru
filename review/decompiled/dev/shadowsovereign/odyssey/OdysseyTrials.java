/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.Minecart
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.GlazedTerracottaBlock
 *  net.minecraft.world.level.block.PoweredRailBlock
 *  net.minecraft.world.level.block.RailBlock
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyCamp;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyEvents;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.soullife.SoulLifeService;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;

public final class OdysseyTrials {
    private static final int[][] MARKERS = new int[][]{{-20, -20}, {0, -20}, {20, -20}, {20, 0}, {20, 20}, {0, 20}, {-20, 20}, {-20, 0}};

    static BlockPos marker(AdventureData.Session s, int index) {
        int[] p = MARKERS[Math.floorMod(index, 8)];
        return OdysseyRuns.at(s, p[0], 1, p[1]);
    }

    private static void paint(ServerPlayer p, BlockPos at, Block block) {
        p.m_9236_().m_7731_(at, block.m_49966_(), 3);
    }

    private static boolean near(ServerPlayer p, BlockPos at) {
        return p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 16.0;
    }

    private static void wave(ServerPlayer p, AdventureData.Session s, int count) {
        for (int i = 0; i < count; ++i) {
            OdysseyRuns.add(p, s, (EntityType<? extends Mob>)(i % 2 == 0 ? EntityType.f_20501_ : EntityType.f_20524_), AdventureData.Role.ENEMY, OdysseyRuns.at(s, -8 + i * 4, 1, -8), 24 + s.journey.m_128451_("Difficulty") * 8);
        }
    }

    private static boolean enemies(AdventureData.Session s) {
        return s.npcs.values().stream().anyMatch(n -> n.role == AdventureData.Role.ENEMY);
    }

    public static void setup(ServerPlayer p, AdventureData.Session s) {
        int i;
        CompoundTag n = s.journey;
        for (i = 0; i < 8; ++i) {
            OdysseyTrials.paint(p, OdysseyTrials.marker(s, i).m_7495_(), i % 2 == 0 ? Blocks.f_50282_ : Blocks.f_50737_);
            p.m_9236_().m_7731_(OdysseyTrials.marker(s, i), Blocks.f_50139_.m_49966_(), 3);
        }
        switch (OdysseyRuns.mode(s)) {
            case DUNGEON: {
                int x;
                for (x = -24; x <= 24; x += 16) {
                    for (int z = -26; z <= 10; ++z) {
                        if (Math.abs(z + 8) <= 2) continue;
                        for (int y = 1; y <= 5; ++y) {
                            OdysseyTrials.paint(p, OdysseyRuns.at(s, x, y, z), Blocks.f_152589_);
                        }
                    }
                }
                n.m_128405_("Rooms", 4);
                n.m_128405_("Layout", Math.floorMod((int)n.m_128454_("Seed"), 4));
                OdysseyTrials.dungeonRoom(p, s);
                break;
            }
            case LOOP: {
                n.m_128385_("LeverTimes", new int[]{-1, -1, -1});
                n.m_128385_("GhostTimes", new int[]{-1, -1, -1});
                for (i = 0; i < 3; ++i) {
                    OdysseyTrials.paint(p, OdysseyTrials.marker(s, i), Blocks.f_50729_);
                }
                break;
            }
            case TRAIN: {
                int x;
                for (x = -24; x <= 24; ++x) {
                    OdysseyTrials.rail(p, s, x, -8, x == -24 ? RailShape.SOUTH_EAST : (x == 24 ? RailShape.SOUTH_WEST : RailShape.EAST_WEST));
                    OdysseyTrials.rail(p, s, x, 8, x == -24 ? RailShape.NORTH_EAST : (x == 24 ? RailShape.NORTH_WEST : RailShape.EAST_WEST));
                }
                for (int z = -7; z <= 7; ++z) {
                    OdysseyTrials.rail(p, s, -24, z, RailShape.NORTH_SOUTH);
                    OdysseyTrials.rail(p, s, 24, z, RailShape.NORTH_SOUTH);
                }
                Minecart cart = new Minecart(p.m_9236_(), (double)s.anchor.m_123341_() - 20.5, (double)s.anchor.m_123342_() + 1.1, (double)s.anchor.m_123343_() - 7.5);
                cart.m_6593_((Component)OdysseyText.c("ghost_train", new Object[0]));
                cart.m_20331_(true);
                p.m_284548_().m_7967_((Entity)cart);
                OdysseyRuns.prop(s, (Entity)cart);
                n.m_128362_("Train", cart.m_20148_());
                n.m_128405_("Station", 0);
                break;
            }
            case MIRRORS: {
                n.m_128385_("Mirrors", new int[]{0, 0, 0});
                for (i = 0; i < 3; ++i) {
                    BlockPos at = OdysseyTrials.mirror(s, i);
                    p.m_9236_().m_7731_(at, (BlockState)Blocks.f_50526_.m_49966_().m_61124_((Property)GlazedTerracottaBlock.f_54117_, (Comparable)Direction.EAST), 3);
                }
                OdysseyTrials.paint(p, OdysseyRuns.at(s, 16, 1, -24), Blocks.f_50261_);
                break;
            }
            case COMPANIES: {
                for (int x : new int[]{-12, 12}) {
                    OdysseyTrials.paint(p, OdysseyRuns.at(s, x, 1, -12), Blocks.f_50326_);
                }
                OdysseyTrials.wave(p, s, 4);
                break;
            }
            case MINE: {
                for (i = 0; i < 16; ++i) {
                    OdysseyTrials.paint(p, OdysseyTrials.ore(s, i), i % 3 == 0 ? Blocks.f_49996_ : (i % 3 == 1 ? Blocks.f_152505_ : Blocks.f_49997_));
                }
                n.m_128405_("Stability", 100);
                break;
            }
            case TREASURE: {
                for (i = 0; i < 8; ++i) {
                    OdysseyTrials.paint(p, OdysseyTrials.marker(s, i), (new Block[]{Blocks.f_152490_, Blocks.f_152504_, Blocks.f_152544_, Blocks.f_50568_})[i % 4]);
                }
                n.m_128405_("TreasureNext", Math.floorMod((int)n.m_128454_("Seed"), 8));
                break;
            }
            case MASQUERADE: {
                for (i = 0; i < 4; ++i) {
                    Entity entity;
                    AdventureData.Npc npc = OdysseyRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20492_, AdventureData.Role.ESCORT, OdysseyTrials.marker(s, i * 2), 20.0f);
                    npc.snapshot.m_128379_("NoAI", true);
                    if (npc.active != null && (entity = p.m_284548_().m_8791_(npc.active)) instanceof Mob) {
                        Mob m = (Mob)entity;
                        m.m_21557_(true);
                        m.m_6593_((Component)OdysseyText.c("guest." + i, new Object[0]));
                    }
                    n.m_128362_("Guest." + i, npc.id);
                }
                n.m_128405_("Saboteur", Math.floorMod((int)n.m_128454_("Seed"), 4));
                break;
            }
            case LIGHTHOUSE: {
                for (i = 0; i < 24; ++i) {
                    BlockPos at = OdysseyRuns.at(s, -24 + i, i + 1, 0);
                    p.m_9236_().m_7731_(at, (BlockState)Blocks.f_50739_.m_49966_().m_61124_((Property)StairBlock.f_56841_, (Comparable)Direction.EAST), 3);
                    for (int z = -2; z <= 2; ++z) {
                        if (z == 0) continue;
                        OdysseyTrials.paint(p, at.m_7918_(0, 0, z), Blocks.f_50735_);
                    }
                }
                n.m_128405_("Fog", 0);
                break;
            }
            case BAZAAR: {
                n.m_128405_("Offer", 0);
                n.m_128405_("Bid", 1);
                n.m_128405_("AuctionUntil", 400);
                n.m_128405_("OfferItem", OdysseyTrials.nextArtifact(p, 0));
                for (i = 0; i < 3; ++i) {
                    OdysseyRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20494_, AdventureData.Role.ESCORT, OdysseyRuns.at(s, -8 + i * 8, 1, -12), 20.0f);
                }
                break;
            }
            case THEATRE: 
            case COUNCIL: 
            case PARADE: {
                int x;
                for (x = -12; x <= 12; ++x) {
                    for (int z = -4; z <= 8; ++z) {
                        OdysseyTrials.paint(p, OdysseyRuns.at(s, x, 0, z), Blocks.f_50745_);
                    }
                }
                break;
            }
        }
    }

    public static BlockPos dungeonTarget(AdventureData.Session s) {
        return OdysseyTrials.marker(s, Math.floorMod(s.journey.m_128451_("Stage") + s.journey.m_128451_("Layout"), 4) * 2);
    }

    private static void dungeonDoor(ServerPlayer p, AdventureData.Session s, boolean closed) {
        BlockPos center = OdysseyTrials.dungeonTarget(s);
        for (int x = -1; x <= 1; ++x) {
            for (int y = 0; y < 3; ++y) {
                OdysseyTrials.paint(p, center.m_7918_(x, y, 0), closed ? Blocks.f_50183_ : Blocks.f_50016_);
            }
        }
    }

    private static void dungeonRoom(ServerPlayer p, AdventureData.Session s) {
        BlockPos target = OdysseyTrials.dungeonTarget(s);
        int stage = s.journey.m_128451_("Stage");
        s.journey.m_128379_("DoorOpen", false);
        for (int i = 0; i < 3 + stage; ++i) {
            OdysseyRuns.add(p, s, (EntityType<? extends Mob>)((stage + s.journey.m_128451_("Layout")) % 2 == 0 ? EntityType.f_20501_ : EntityType.f_20524_), AdventureData.Role.ENEMY, target.m_7918_(-5 + i * 2, 0, 5), 24 + s.journey.m_128451_("Difficulty") * 8);
        }
        OdysseyTrials.dungeonDoor(p, s, true);
        for (int x = -6; x <= 6; ++x) {
            for (int z = -5; z <= 7; ++z) {
                if (Math.abs(x) != 6 && z != -5 && z != 7) continue;
                OdysseyTrials.paint(p, target.m_7918_(x, -1, z), (new Block[]{Blocks.f_152593_, Blocks.f_50282_, Blocks.f_152490_, Blocks.f_152504_})[Math.floorMod(stage + s.journey.m_128451_("Layout"), 4)]);
            }
        }
    }

    private static void rail(ServerPlayer p, AdventureData.Session s, int x, int z, RailShape shape) {
        boolean straight = shape == RailShape.NORTH_SOUTH || shape == RailShape.EAST_WEST;
        BlockPos at = OdysseyRuns.at(s, x, 1, z);
        p.m_9236_().m_7731_(at, straight ? (BlockState)((BlockState)Blocks.f_50030_.m_49966_().m_61124_((Property)PoweredRailBlock.f_55214_, (Comparable)shape)).m_61124_((Property)PoweredRailBlock.f_55215_, (Comparable)Boolean.valueOf(true)) : (BlockState)Blocks.f_50156_.m_49966_().m_61124_((Property)RailBlock.f_55392_, (Comparable)shape), 3);
        if (straight) {
            OdysseyTrials.paint(p, at.m_7495_(), Blocks.f_50330_);
        }
    }

    public static BlockPos mirror(AdventureData.Session s, int i) {
        return switch (i) {
            case 0 -> OdysseyRuns.at(s, -8, 1, -16);
            case 1 -> OdysseyRuns.at(s, -8, 1, 8);
            default -> OdysseyRuns.at(s, 16, 1, 8);
        };
    }

    public static BlockPos ore(AdventureData.Session s, int i) {
        return OdysseyRuns.at(s, -15 + i % 4 * 8, 1, -15 + i / 4 * 8);
    }

    private static int nextArtifact(ServerPlayer p, int offset) {
        OdysseyState state = GuideService.state(p);
        for (int i = 0; i < 24; ++i) {
            int id = Math.floorMod(offset + i, 24);
            if (state.earned.contains("artifact." + id)) continue;
            return id;
        }
        return -1;
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        CompoundTag n = s.journey;
        int t = n.m_128451_("Elapsed");
        block0 : switch (OdysseyRuns.mode(s)) {
            case DUNGEON: {
                if (OdysseyTrials.enemies(s)) break;
                if (!n.m_128471_("DoorOpen")) {
                    OdysseyTrials.dungeonDoor(p, s, false);
                    n.m_128379_("DoorOpen", true);
                }
                if (t % 20 != 0) break;
                BlockPos exit = OdysseyTrials.dungeonTarget(s);
                p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123746_, (double)exit.m_123341_() + 0.5, (double)(exit.m_123342_() + 1), (double)exit.m_123343_() + 0.5, 5, 0.3, 0.3, 0.3, 0.02);
                break;
            }
            case LOOP: {
                int[] ghosts;
                int phase = t % 1800;
                if (phase == 0) {
                    int[] recorded = n.m_128465_("LeverTimes");
                    int[] replay = new int[]{-1, -1, -1};
                    int found = 0;
                    for (int i = 0; i < recorded.length && found < 2; ++i) {
                        if (recorded[i] < 0) continue;
                        replay[i] = 600 + found++ * 20;
                    }
                    n.m_128385_("GhostTimes", replay);
                    n.m_128385_("LeverTimes", new int[]{-1, -1, -1});
                    n.m_128405_("Levers", 0);
                    OdysseyText.tell(p, "loop_reset", new Object[0]);
                }
                if ((ghosts = n.m_128465_("GhostTimes")).length == 3) {
                    for (int i = 0; i < 3; ++i) {
                        if (ghosts[i] < 0 || ghosts[i] != phase) continue;
                        OdysseyTrials.lever(p, s, i);
                    }
                }
                if (n.m_128451_("Levers") != 7) break;
                OdysseyRuns.win(p, s);
                break;
            }
            case TRAIN: {
                Entity entity;
                Entity entity2 = entity = n.m_128403_("Train") ? p.m_284548_().m_8791_(n.m_128342_("Train")) : null;
                if (!(entity instanceof Minecart)) {
                    OdysseyRuns.pause(p);
                    return;
                }
                Minecart train = (Minecart)entity;
                if (n.m_128471_("Repair")) {
                    train.m_20256_(Vec3.f_82478_);
                    return;
                }
                if (train.m_20184_().m_165925_() < 0.01) {
                    train.m_20334_(0.23, 0.0, 0.0);
                }
                int station = n.m_128451_("Station");
                BlockPos at = OdysseyRuns.at(s, (new int[]{24, 24, -24, -24})[Math.min(3, station)], 1, (new int[]{-8, 8, 8, -8})[Math.min(3, station)]);
                if (p.m_20202_() != train || !(train.m_20238_(Vec3.m_82512_((Vec3i)at)) < 16.0)) break;
                n.m_128379_("Repair", true);
                n.m_128405_("RepairStation", station);
                if (station == 1 || station == 2) {
                    OdysseyTrials.wave(p, s, 3);
                }
                OdysseyText.tell(p, "train_station", station + 1);
                break;
            }
            case MIRRORS: {
                if (t % 10 != 0) {
                    return;
                }
                int[] dirs = n.m_128465_("Mirrors");
                if (dirs.length != 3) {
                    return;
                }
                Vec3 point = Vec3.m_82512_((Vec3i)OdysseyRuns.at(s, -24, 1, -16));
                Direction direction = Direction.EAST;
                for (int step = 0; step < 256; ++step) {
                    point = point.m_231075_(direction, 0.5);
                    p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123810_, point.f_82479_, point.f_82480_, point.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
                    boolean changed = false;
                    for (int i = 0; i < 3; ++i) {
                        if (!(point.m_82557_(Vec3.m_82512_((Vec3i)OdysseyTrials.mirror(s, i))) < 0.05)) continue;
                        direction = (new Direction[]{Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH})[dirs[i]];
                        changed = true;
                        break;
                    }
                    if (point.m_82557_(Vec3.m_82512_((Vec3i)OdysseyRuns.at(s, 16, 1, -24))) < 0.3) {
                        OdysseyRuns.win(p, s);
                        break block0;
                    }
                    if (Math.abs(point.f_82479_ - (double)s.anchor.m_123341_()) > 29.0 || Math.abs(point.f_82481_ - (double)s.anchor.m_123343_()) > 29.0) break block0;
                }
                break;
            }
            case COMPANIES: {
                List<Mob> allies = OdysseyRuns.allies(p, s);
                boolean left = false;
                boolean right = false;
                for (int i = 0; i < allies.size(); ++i) {
                    Mob m2 = allies.get(i);
                    int team = i % 2;
                    int target = n.m_128451_("Team." + team);
                    Vec3 pos = Vec3.m_82539_((Vec3i)OdysseyRuns.at(s, target == 1 ? -12 : (target == 2 ? 12 : (team == 0 ? -8 : 8)), 1, -12));
                    if (m2.m_5448_() == null) {
                        OdysseyRuns.move(m2, pos, 1.1);
                    }
                    if (m2.m_20238_(Vec3.m_82539_((Vec3i)OdysseyRuns.at(s, -12, 1, -12))) < 4.0) {
                        left = true;
                    }
                    if (!(m2.m_20238_(Vec3.m_82539_((Vec3i)OdysseyRuns.at(s, 12, 1, -12))) < 4.0)) continue;
                    right = true;
                }
                if (left && right && !OdysseyTrials.enemies(s)) {
                    n.m_128405_("Hold", n.m_128451_("Hold") + 1);
                }
                if (n.m_128451_("Hold") < 100 || !OdysseyTrials.near(p, OdysseyTrials.marker(s, 4))) break;
                OdysseyRuns.win(p, s);
                break;
            }
            case MINE: {
                if (!n.m_128441_("CollapseAt") || t < n.m_128451_("CollapseAt")) break;
                for (int x = -24; x <= 24; x += 3) {
                    for (int z = -24; z <= 24; z += 3) {
                        FallingBlockEntity block = FallingBlockEntity.m_201971_((Level)p.m_9236_(), (BlockPos)OdysseyRuns.at(s, x, 12, z), (BlockState)Blocks.f_49994_.m_49966_());
                        block.f_31943_ = false;
                        block.m_149656_(0.0f, 0);
                    }
                }
                OdysseyText.tell(p, "mine_collapsed", new Object[0]);
                OdysseyRuns.finish(p, false);
                break;
            }
            case TREASURE: {
                int index;
                BlockPos at;
                if (t % 20 != 0 || !OdysseyTrials.near(p, at = OdysseyTrials.marker(s, index = n.m_128451_("TreasureNext")))) break;
                p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123809_, (double)at.m_123341_() + 0.5, (double)(at.m_123342_() + 1), (double)at.m_123343_() + 0.5, 3, 0.2, 0.2, 0.2, 0.1);
                break;
            }
            case LIGHTHOUSE: {
                int fog = t / 100;
                if (t % 10 == 0) {
                    double fy = s.anchor.m_123342_() + Math.min(20, fog);
                    p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123796_, (double)(s.anchor.m_123341_() - 12), fy, (double)s.anchor.m_123343_(), 100, 14.0, 0.4, 5.0, 0.005);
                }
                n.m_128405_("Fog", fog);
                if (p.m_20186_() < (double)(s.anchor.m_123342_() + Math.min(20, fog)) && t > 300 && t % 40 == 0) {
                    p.m_6469_(p.m_269291_().m_269425_(), 1.0f);
                }
                if (!(p.m_20185_() >= (double)(s.anchor.m_123341_() - 2)) || !(p.m_20186_() >= (double)(s.anchor.m_123342_() + 24)) || !OdysseyRuns.allies(p, s).stream().anyMatch(m -> m.m_20186_() >= (double)(s.anchor.m_123342_() + 20))) break;
                OdysseyRuns.win(p, s);
                break;
            }
            case BAZAAR: {
                int until = n.m_128451_("AuctionUntil");
                if (t >= until) {
                    int offer;
                    if (n.m_128471_("YourBid") && n.m_128451_("Bid") <= GuideService.state((ServerPlayer)p).seals && n.m_128451_("OfferItem") >= 0 && GuideService.state(p).grant("auction:" + String.valueOf(s.id) + ":" + n.m_128451_("Offer"))) {
                        GuideService.state((ServerPlayer)p).seals -= n.m_128451_("Bid");
                        OdysseyCamp.earnArtifact(p, n.m_128451_("OfferItem"));
                        n.m_128405_("Purchases", n.m_128451_("Purchases") + 1);
                    }
                    if ((offer = n.m_128451_("Offer") + 1) >= 3) {
                        if (n.m_128451_("Purchases") > 0) {
                            OdysseyRuns.win(p, s);
                        } else {
                            OdysseyRuns.finish(p, false);
                        }
                        return;
                    }
                    n.m_128405_("Offer", offer);
                    n.m_128405_("Bid", 1);
                    n.m_128379_("YourBid", false);
                    n.m_128405_("OfferItem", OdysseyTrials.nextArtifact(p, offer * 7));
                    n.m_128405_("AuctionUntil", t + 400);
                    break;
                }
                if (t % 100 != 0 || !n.m_128471_("YourBid") || n.m_128451_("Bid") >= 4 + Math.floorMod((int)n.m_128454_("Seed"), 3)) break;
                n.m_128405_("Bid", n.m_128451_("Bid") + 1);
                n.m_128379_("YourBid", false);
                break;
            }
            case THEATRE: {
                List<Mob> allies = OdysseyRuns.allies(p, s);
                for (int i = 0; i < allies.size(); ++i) {
                    OdysseyRuns.move(allies.get(i), Vec3.m_82539_((Vec3i)OdysseyRuns.at(s, -8 + i % 3 * 8, 1, 2)), 0.9);
                }
                break;
            }
            case COUNCIL: {
                List<Mob> allies = OdysseyRuns.allies(p, s);
                for (int i = 0; i < allies.size(); ++i) {
                    OdysseyRuns.move(allies.get(i), Vec3.m_82539_((Vec3i)OdysseyRuns.at(s, -5 + i * 3, 1, 2)), 0.9);
                }
                break;
            }
            case PARADE: {
                int stage = n.m_128451_("Stage");
                Vec3 target = Vec3.m_82539_((Vec3i)OdysseyTrials.marker(s, stage * 2));
                boolean arrived = true;
                int i = 0;
                for (Mob m3 : OdysseyRuns.allies(p, s)) {
                    Vec3 at = target.m_82520_((double)(i++ % 2 * 2), 0.0, (double)(i / 2 * 2));
                    OdysseyRuns.move(m3, at, 0.85);
                    if (!(m3.m_20238_(at) > 9.0)) continue;
                    arrived = false;
                }
                if (!arrived || !(p.m_20238_(target) < 36.0)) break;
                n.m_128405_("Stage", stage + 1);
                if (stage < 3) break;
                OdysseyRuns.win(p, s);
                break;
            }
        }
    }

    private static void lever(ServerPlayer p, AdventureData.Session s, int index) {
        CompoundTag n = s.journey;
        int t = n.m_128451_("Elapsed");
        if (t - n.m_128451_("LeverWindow") > 60) {
            n.m_128405_("Levers", 0);
            n.m_128405_("LeverWindow", t);
        }
        if (n.m_128451_("Levers") == 0) {
            n.m_128405_("LeverWindow", t);
        }
        n.m_128405_("Levers", n.m_128451_("Levers") | 1 << index);
        OdysseyTrials.paint(p, OdysseyTrials.marker(s, index), Blocks.f_50386_);
    }

    public static boolean input(ServerPlayer p, AdventureData.Session s, int action, int value) {
        CompoundTag n = s.journey;
        switch (OdysseyRuns.mode(s)) {
            case DUNGEON: {
                int stage = n.m_128451_("Stage");
                if (action != 0 || OdysseyTrials.enemies(s) || !OdysseyTrials.near(p, OdysseyTrials.dungeonTarget(s))) {
                    return false;
                }
                n.m_128405_("Stage", stage + 1);
                if (stage >= 3) {
                    OdysseyRuns.win(p, s);
                } else {
                    OdysseyTrials.dungeonRoom(p, s);
                }
                return true;
            }
            case LOOP: {
                if (action != 0 || value < 0 || value > 2 || !OdysseyTrials.near(p, OdysseyTrials.marker(s, value))) {
                    return false;
                }
                int[] times = n.m_128465_("LeverTimes");
                if (times.length != 3) {
                    return false;
                }
                times[value] = n.m_128451_("Elapsed") % 1800;
                n.m_128385_("LeverTimes", times);
                OdysseyTrials.lever(p, s, value);
                return true;
            }
            case TRAIN: {
                Minecart cart;
                Entity entity;
                Entity entity2 = entity = n.m_128403_("Train") ? p.m_284548_().m_8791_(n.m_128342_("Train")) : null;
                if (!(entity instanceof Minecart) || p.m_20280_((Entity)(cart = (Minecart)entity)) > 25.0) {
                    return false;
                }
                if (action == 0) {
                    return p.m_7998_((Entity)cart, true);
                }
                if (action != 1 || !n.m_128471_("Repair") || OdysseyTrials.enemies(s) || !p.m_21205_().m_150930_(Items.f_151052_) || p.m_21205_().m_41613_() < 2) {
                    return false;
                }
                p.m_21205_().m_41774_(2);
                n.m_128379_("Repair", false);
                int stage = n.m_128451_("Station") + 1;
                n.m_128405_("Station", stage);
                if (stage >= 4) {
                    OdysseyRuns.win(p, s);
                } else {
                    cart.m_20334_(stage == 1 ? 0.0 : (stage == 2 ? -0.23 : 0.0), 0.0, stage == 1 ? 0.23 : (stage == 3 ? -0.23 : 0.0));
                }
                return true;
            }
            case MIRRORS: {
                if (action != 0 || value < 0 || value > 2 || !OdysseyTrials.near(p, OdysseyTrials.mirror(s, value))) {
                    return false;
                }
                int[] mirrors = n.m_128465_("Mirrors");
                mirrors[value] = (mirrors[value] + 1) % 4;
                n.m_128385_("Mirrors", mirrors);
                p.m_9236_().m_7731_(OdysseyTrials.mirror(s, value), (BlockState)Blocks.f_50526_.m_49966_().m_61124_((Property)GlazedTerracottaBlock.f_54117_, (Comparable)(new Direction[]{Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH})[mirrors[value]]), 3);
                return true;
            }
            case COMPANIES: {
                if (action != 0 || value < 0 || value > 3) {
                    return false;
                }
                n.m_128405_("Team." + value / 2, value % 2 + 1);
                return true;
            }
            case MINE: {
                if (action == 1) {
                    if (n.m_128451_("Ore") < 8 || !OdysseyTrials.near(p, OdysseyTrials.marker(s, 5))) {
                        return false;
                    }
                    OdysseyRuns.win(p, s);
                    return true;
                }
                if (action != 0 || value < 0 || value > 15 || (n.m_128451_("OreMask") & 1 << value) != 0 || !OdysseyTrials.near(p, OdysseyTrials.ore(s, value))) {
                    return false;
                }
                BlockPos at = OdysseyTrials.ore(s, value);
                BlockState block = p.m_9236_().m_8055_(at);
                if (!p.m_21205_().m_41735_(block)) {
                    return false;
                }
                BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(p.m_9236_(), at, block, (Player)p);
                if (OdysseyEvents.mineVeto(event)) {
                    return false;
                }
                List drops = Block.m_49874_((BlockState)block, (ServerLevel)p.m_284548_(), (BlockPos)at, (BlockEntity)p.m_9236_().m_7702_(at), (Entity)p, (ItemStack)p.m_21205_());
                p.m_9236_().m_7731_(at, Blocks.f_50016_.m_49966_(), 3);
                for (ItemStack stack : drops) {
                    OdysseyCamp.deliver(p, stack);
                }
                p.m_21205_().m_41622_(1, (LivingEntity)p, user -> user.m_21190_(InteractionHand.MAIN_HAND));
                n.m_128405_("OreMask", n.m_128451_("OreMask") | 1 << value);
                n.m_128405_("Ore", n.m_128451_("Ore") + 1);
                n.m_128405_("Stability", Math.max(0, 100 - n.m_128451_("Ore") * 6));
                if (n.m_128451_("Ore") >= 12 && !n.m_128441_("CollapseAt")) {
                    n.m_128405_("CollapseAt", n.m_128451_("Elapsed") + 200);
                    OdysseyText.tell(p, "mine_warning", new Object[0]);
                }
                return true;
            }
            case TREASURE: {
                int target = n.m_128451_("TreasureNext");
                int stage = n.m_128451_("Stage");
                if (action != 0 || !OdysseyTrials.near(p, OdysseyTrials.marker(s, target))) {
                    return false;
                }
                n.m_128405_("Stage", stage + 1);
                n.m_128405_("TreasureNext", Math.floorMod(target + 3 + (int)(n.m_128454_("Seed") >> stage), 8));
                if (stage >= 3) {
                    OdysseyRuns.win(p, s);
                }
                return true;
            }
            case MASQUERADE: {
                Entity mob;
                if (!p.m_6844_(EquipmentSlot.HEAD).m_150930_(Items.f_42047_) || value < 0 || value > 3) {
                    return false;
                }
                AdventureData.Npc npc = s.npcs.get(n.m_128342_("Guest." + value));
                Entity entity = mob = npc == null || npc.active == null ? null : p.m_284548_().m_8791_(npc.active);
                if (mob == null || p.m_20280_(mob) > 25.0) {
                    return false;
                }
                if (action == 0) {
                    n.m_128405_("Clues", n.m_128451_("Clues") | 1 << value);
                    OdysseyText.tell(p, "guest_clue." + Math.floorMod(value - n.m_128451_("Saboteur"), 4), new Object[0]);
                    return true;
                }
                if (action == 1 && Integer.bitCount(n.m_128451_("Clues")) >= 3) {
                    if (value == n.m_128451_("Saboteur")) {
                        OdysseyRuns.win(p, s);
                    } else {
                        n.m_128405_("Mistakes", n.m_128451_("Mistakes") + 1);
                        if (n.m_128451_("Mistakes") >= 3) {
                            OdysseyRuns.finish(p, false);
                        }
                    }
                    return true;
                }
                return false;
            }
            case BAZAAR: {
                if (action != 0 || n.m_128451_("OfferItem") < 0 || GuideService.state((ServerPlayer)p).seals < n.m_128451_("Bid") + 1 || p.m_20238_(Vec3.m_82512_((Vec3i)OdysseyRuns.at(s, 0, 1, -12))) > 144.0) {
                    return false;
                }
                n.m_128405_("Bid", n.m_128451_("Bid") + 1);
                n.m_128379_("YourBid", true);
                return true;
            }
            case THEATRE: {
                int stage = n.m_128451_("Stage");
                List<Mob> allies = OdysseyRuns.allies(p, s);
                if (action != 0 || stage >= 3 || allies.size() < 3) {
                    return false;
                }
                Mob m = allies.get(stage);
                if (m.m_20238_(Vec3.m_82539_((Vec3i)OdysseyRuns.at(s, -8 + stage * 8, 1, 2))) > 4.0 || p.m_20280_((Entity)m) > 25.0) {
                    return false;
                }
                m.m_6674_(InteractionHand.MAIN_HAND);
                SoulLifeService.particles(p, m, (ParticleOptions)ParticleTypes.f_123758_, 5);
                n.m_128405_("Stage", stage + 1);
                if (stage == 2) {
                    OdysseyRuns.win(p, s);
                }
                return true;
            }
            case COUNCIL: {
                if (action != 0 || value < 0 || value > 2 || n.m_128451_("Elapsed") < 100 || OdysseyRuns.allies(p, s).size() < 3) {
                    return false;
                }
                OdysseyCatalog.Mode[] modes = new OdysseyCatalog.Mode[]{OdysseyCatalog.Mode.DUNGEON, OdysseyCatalog.Mode.TREASURE, OdysseyCatalog.Mode.MIRRORS};
                GuideService.state((ServerPlayer)p).camp.m_128405_("Recommended", modes[value].ordinal());
                OdysseyRuns.win(p, s);
                return true;
            }
        }
        return false;
    }
}

