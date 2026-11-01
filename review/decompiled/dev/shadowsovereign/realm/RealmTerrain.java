/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.shadowsovereign.realm;

import dev.shadowsovereign.expanse.ExpanseWorld;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.realm.RealmState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public final class RealmTerrain {
    public static final int LAND = 786432;
    public static final int BRIDGES = 7200;
    public static final int DECOR = 6144;
    public static final int SCENERY = 20736;
    public static final int TOTAL = 820512;

    public static int region(ServerPlayer p, BlockPos pos) {
        for (int i = 0; i < 4; ++i) {
            BlockPos c = OdysseyRealm.arena(p, i);
            if (Math.abs(pos.m_123341_() - c.m_123341_()) >= 128 || Math.abs(pos.m_123343_() - c.m_123343_()) >= 128) continue;
            return i;
        }
        return -1;
    }

    public static BlockPos ruin(ServerPlayer p, int index) {
        BlockPos c = OdysseyRealm.arena(p, index / 3);
        int k = index % 3;
        return c.m_7918_((new int[]{-80, 80, 12})[k], 1, (new int[]{-72, -52, 88})[k]);
    }

    public static BlockPos stone(ServerPlayer p, int region) {
        return OdysseyRealm.arena(p, region).m_7918_(0, 1, 64);
    }

    public static BlockPos project(ServerPlayer p, int region) {
        return OdysseyRealm.arena(p, region).m_7918_(64, 1, 0);
    }

    public static BlockPos rescue(ServerPlayer p, int region) {
        return OdysseyRealm.arena(p, region).m_7918_(-64, 1, 0);
    }

    public static BlockPos node(ServerPlayer p, int region, int slot) {
        int x = slot % 2 == 0 ? -100 : 100;
        int z = slot < 2 ? 24 : -24;
        return OdysseyRealm.arena(p, region).m_7918_(x, RealmTerrain.height(region, x, z) - 79, z);
    }

    public static BlockPos landmark(ServerPlayer p, int index) {
        if (index < 0 || index > 23) {
            return OdysseyRealm.home(p).m_7494_();
        }
        return index < 12 ? RealmTerrain.ruin(p, index) : (index < 16 ? RealmTerrain.stone(p, index - 12) : (index < 20 ? RealmTerrain.project(p, index - 16) : RealmTerrain.rescue(p, index - 20)));
    }

    public static int height(int region, int x, int z) {
        if (Math.abs(x) <= 5 || Math.abs(z) <= 5 || Math.abs(x) <= 48 && Math.abs(z) <= 48) {
            return 80;
        }
        for (int sx : new int[]{-72, 72}) {
            for (int sz : new int[]{-72, 72}) {
                if (Math.abs(x - sx) > 5 || Math.abs(z - sz) > 5) continue;
                return 80;
            }
        }
        for (int k = 0; k < 3; ++k) {
            if (Math.abs(x - (new int[]{-80, 80, 12})[k]) > 12 || Math.abs(z - (new int[]{-72, -52, 88})[k]) > 12) continue;
            return 80;
        }
        if (Math.abs(x + 64) < 10 && Math.abs(z) < 10 || Math.abs(x - 64) < 10 && Math.abs(z) < 10 || Math.abs(x) < 10 && Math.abs(z - 64) < 10) {
            return 80;
        }
        return 80 + (int)Math.floor((Math.sin((double)(x + region * 17) * 0.045) + Math.cos((double)(z - region * 11) * 0.045)) * 2.0);
    }

    public static boolean island(int x, int z) {
        return x * x + z * z < 15876;
    }

    public static boolean safePut(ServerLevel world, BlockPos at, BlockState block) {
        if (!world.m_6857_().m_61937_(at) || !world.m_8055_(at).m_60795_()) {
            return false;
        }
        return world.m_7731_(at, block, 2);
    }

    public static boolean request(ServerPlayer p) {
        RealmState s = LivingRealm.state(p);
        if (!(OdysseyRealm.inside(p) && GuideService.state((ServerPlayer)p).plotReady && OdysseyRealm.camp(p, p.m_20183_()))) {
            return false;
        }
        if (s.ready || s.requested) {
            return true;
        }
        s.requested = true;
        LivingRealm.dirty(p);
        return true;
    }

    public static void tick(ServerPlayer p) {
        RealmState s = LivingRealm.state(p);
        if (!s.requested || s.ready || !OdysseyRealm.inside(p)) {
            return;
        }
        if (!ExpanseWorld.terrainBudget(p.m_284548_())) {
            return;
        }
        int end = Math.min(820512, s.cursor + 896);
        while (s.cursor < end) {
            block19: {
                int cz;
                int cx;
                Block block;
                BlockPos at;
                int i;
                block20: {
                    block23: {
                        int cell;
                        block21: {
                            block22: {
                                int region;
                                block18: {
                                    i = s.cursor;
                                    if (i >= 786432) break block18;
                                    region = i / 196608;
                                    cell = i / 3 % 65536;
                                    int layer = i % 3;
                                    int x = (cell & 0xFF) - 128;
                                    int z = (cell >> 8) - 128;
                                    if (!RealmTerrain.island(x, z) || Math.abs(x) < 48 && Math.abs(z) < 48 && Math.abs(x) > 4 && Math.abs(z) > 4) break block19;
                                    at = OdysseyRealm.arena(p, region).m_7918_(x, RealmTerrain.height(region, x, z) - 80 - layer, z);
                                    block = layer > 0 ? (new Block[]{Blocks.f_50493_, Blocks.f_152550_, Blocks.f_152497_, Blocks.f_50730_})[region] : (new Block[]{Blocks.f_50440_, Blocks.f_50222_, Blocks.f_50472_, Blocks.f_50735_})[region];
                                    break block20;
                                }
                                if (i >= 793632) break block21;
                                region = (i -= 786432) / 1800;
                                int step = i / 9 % 200 + 127;
                                int side = i % 9 - 4;
                                BlockPos c = OdysseyRealm.home(p);
                                at = c.m_7918_(region == 0 ? step : (region == 2 ? -step : side), 0, region == 1 ? step : (region == 3 ? -step : side));
                                if (Math.abs(side) != 4) break block22;
                                if (step % 8 != 0) break block19;
                                at = at.m_7494_();
                                block = Blocks.f_50682_;
                                break block20;
                            }
                            block = Blocks.f_50222_;
                            break block20;
                        }
                        if (i >= 799776) break block23;
                        int site = (i -= 793632) / 512;
                        cell = i % 512;
                        int x = cell % 16 - 8;
                        int z = cell / 16 % 16 - 8;
                        int y = cell / 256 + 1;
                        BlockPos center = RealmTerrain.ruin(p, site).m_7495_();
                        at = center.m_7918_(x, y, z);
                        if (Math.abs(x) != 7 && Math.abs(z) != 7 || Math.abs(x) <= 1 || Math.abs(z) <= 1) break block19;
                        block = y == 2 && cell % 4 == 0 ? Blocks.f_50225_ : (new Block[]{Blocks.f_50223_, Blocks.f_152510_, Blocks.f_50078_, Blocks.f_50736_})[site / 3];
                        break block20;
                    }
                    int cell = s.cursor - 786432 - 7200 - 6144;
                    int region = cell / 5184;
                    int tower = cell / 1296 % 4;
                    int local = cell % 1296;
                    int x = local % 9 - 4;
                    int z = local / 9 % 9 - 4;
                    int y = local / 81;
                    BlockPos base = OdysseyRealm.arena(p, region).m_7918_(tower % 2 == 0 ? -72 : 72, 1, tower / 2 == 0 ? -72 : 72);
                    at = base.m_7918_(x, y, z);
                    block = null;
                    if (region == 0) {
                        if (x == 0 && z == 0 && y <= 5) {
                            block = Blocks.f_49999_;
                        } else if (y >= 5 && y <= 8 && x * x + z * z + (y - 6) * (y - 6) <= 15) {
                            block = Blocks.f_152471_;
                        }
                    } else {
                        int height;
                        int n = region == 3 ? 14 : (height = region == 2 ? 8 : 10);
                        if (y < height && (Math.abs(x) == 4 || Math.abs(z) == 4) && (y >= 3 || Math.abs(x) > 1 && Math.abs(z) > 1)) {
                            Block block2 = region == 1 ? (y % 4 == 2 ? Blocks.f_152504_ : Blocks.f_50222_) : (region == 2 ? (y < 5 ? Blocks.f_50078_ : Blocks.f_50283_) : (block = Blocks.f_50735_));
                        }
                        if (y == height && Math.abs(x) <= 4 && Math.abs(z) <= 4) {
                            Block block3 = region == 1 ? Blocks.f_152510_ : (block = region == 2 ? Blocks.f_50472_ : Blocks.f_50730_);
                        }
                        if (y == height + 1 && Math.abs(x) == 4 && Math.abs(z) == 4) {
                            block = Blocks.f_50682_;
                        }
                    }
                    if (block == null) break block19;
                }
                if (i < 786432 && i % 3 == 0 && i / 196608 == 1 && ((cx = (i / 3 % 65536 & 0xFF) - 128) - 64) * (cx - 64) + ((cz = (i / 3 % 65536 >> 8) - 128) - 64) * (cz - 64) < 400) {
                    block = Blocks.f_50440_;
                }
                if (!ExpanseWorld.readyChunk(p.m_284548_(), at)) break;
                BlockState state = block.m_49966_();
                if (state.m_61138_((Property)BlockStateProperties.f_61447_)) {
                    state = (BlockState)state.m_61124_((Property)BlockStateProperties.f_61447_, (Comparable)Boolean.valueOf(true));
                }
                RealmTerrain.safePut(p.m_284548_(), at, state);
            }
            ++s.cursor;
        }
        if (s.cursor >= 820512) {
            RealmTerrain.decorate(p);
            s.ready = true;
            s.requested = false;
            LivingRealm.tell(p, "kingdom_ready", new Object[0]);
        }
        LivingRealm.dirty(p);
    }

    private static void decorate(ServerPlayer p) {
        for (int region = 0; region < 4; ++region) {
            int y;
            int x;
            BlockPos gate = RealmTerrain.ruin(p, region * 3 + 1).m_7918_(0, 0, -7);
            boolean clear = true;
            for (x = -1; x <= 1; ++x) {
                for (y = 0; y < 3; ++y) {
                    clear &= p.m_9236_().m_8055_(gate.m_7918_(x, y, 0)).m_60795_();
                }
            }
            if (clear) {
                for (x = -1; x <= 1; ++x) {
                    for (y = 0; y < 3; ++y) {
                        RealmTerrain.safePut(p.m_284548_(), gate.m_7918_(x, y, 0), Blocks.f_50183_.m_49966_());
                    }
                }
                LivingRealm.state((ServerPlayer)p).sites.m_128379_("GateCreated." + (region * 3 + 1), true);
            }
            RealmTerrain.safePut(p.m_284548_(), RealmTerrain.stone(p, region), Blocks.f_50729_.m_49966_());
            RealmTerrain.safePut(p.m_284548_(), RealmTerrain.stone(p, region).m_7494_(), Blocks.f_50139_.m_49966_());
            RealmTerrain.safePut(p.m_284548_(), RealmTerrain.project(p, region), Blocks.f_50225_.m_49966_());
            for (int k = 0; k < 4; ++k) {
                RealmTerrain.safePut(p.m_284548_(), RealmTerrain.node(p, region, k), (new Block[]{Blocks.f_152490_, Blocks.f_152599_, Blocks.f_50377_, Blocks.f_50706_})[region].m_49966_());
            }
            for (int j = 0; j < 8; ++j) {
                int x2 = -104 + j * 29;
                int z = 72;
                if (!RealmTerrain.island(x2, z)) continue;
                BlockPos at = OdysseyRealm.arena(p, region).m_7918_(x2, RealmTerrain.height(region, x2, z) - 79, z);
                RealmTerrain.safePut(p.m_284548_(), at, (new Block[]{j % 2 == 0 ? Blocks.f_152541_ : Blocks.f_152542_, Blocks.f_152587_, Blocks.f_152492_, Blocks.f_50682_})[region].m_49966_());
            }
        }
    }

    public static void openGate(ServerPlayer p, int index) {
        if (!LivingRealm.state((ServerPlayer)p).sites.m_128471_("GateCreated." + index)) {
            return;
        }
        BlockPos gate = RealmTerrain.ruin(p, index).m_7918_(0, 0, -7);
        for (int x = -1; x <= 1; ++x) {
            for (int y = 0; y < 3; ++y) {
                BlockPos at = gate.m_7918_(x, y, 0);
                if (!p.m_9236_().m_8055_(at).m_60713_(Blocks.f_50183_)) continue;
                p.m_9236_().m_7471_(at, false);
            }
        }
        LivingRealm.state((ServerPlayer)p).sites.m_128379_("GateOpen." + index, true);
    }

    public static boolean passable(ServerPlayer p, BlockPos at) {
        return p.m_284548_().m_46805_(at) && !p.m_9236_().m_8055_(at.m_7495_()).m_60812_((BlockGetter)p.m_9236_(), at.m_7495_()).m_83281_() && p.m_9236_().m_8055_(at).m_60812_((BlockGetter)p.m_9236_(), at).m_83281_() && p.m_9236_().m_8055_(at.m_7494_()).m_60812_((BlockGetter)p.m_9236_(), at.m_7494_()).m_83281_();
    }

    public static int sector(ServerPlayer p) {
        BlockPos home = OdysseyRealm.home(p);
        int x = Math.floorDiv(p.m_146903_() - home.m_123341_() + 700, 70);
        int z = Math.floorDiv(p.m_146907_() - home.m_123343_() + 700, 70);
        return x >= 0 && x < 20 && z >= 0 && z < 20 ? z * 20 + x : -1;
    }
}

