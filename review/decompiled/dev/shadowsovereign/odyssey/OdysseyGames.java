/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.odyssey.OdysseyCamp;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public final class OdysseyGames {
    public static ItemEntity ball(ServerPlayer p, AdventureData.Session s) {
        ItemEntity item;
        Entity entity;
        return s.journey.m_128403_("Ball") && (entity = p.m_284548_().m_8791_(s.journey.m_128342_("Ball"))) instanceof ItemEntity ? (item = (ItemEntity)entity) : null;
    }

    private static ArmorStand seat(ServerPlayer p, AdventureData.Session s, Vec3 at, boolean pin) {
        ArmorStand entity = new ArmorStand(p.m_9236_(), at.f_82479_, at.f_82480_, at.f_82481_);
        entity.m_20242_(true);
        entity.m_20331_(true);
        entity.m_6842_(!pin);
        CompoundTag small = new CompoundTag();
        entity.m_20240_(small);
        small.m_128379_("Small", true);
        entity.m_20258_(small);
        entity.m_31678_(true);
        if (pin) {
            entity.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)Blocks.f_50542_));
        }
        entity.getPersistentData().m_128362_("OdysseyScene", s.id);
        p.m_284548_().m_7967_((Entity)entity);
        OdysseyRuns.prop(s, (Entity)entity);
        return entity;
    }

    public static void setup(ServerPlayer p, AdventureData.Session s) {
        CompoundTag n = s.journey;
        OdysseyCatalog.Mode mode = OdysseyRuns.mode(s);
        for (Mob ally : OdysseyRuns.allies(p, s)) {
            ally.getPersistentData().m_128473_("OdysseyOut");
            ally.getPersistentData().m_128473_("OdysseyKickAt");
        }
        switch (mode) {
            case TUG: {
                for (int z = -12; z <= 12; ++z) {
                    p.m_9236_().m_7731_(OdysseyRuns.at(s, 0, 0, z), (Math.abs(z) == 8 ? Blocks.f_50108_ : Blocks.f_50041_).m_49966_(), 3);
                }
                break;
            }
            case CHAIRS: {
                OdysseyGames.chairs(p, s);
                break;
            }
            case PAINT: {
                for (int i = 0; i < 64; ++i) {
                    p.m_9236_().m_7731_(OdysseyGames.pixel(s, i), Blocks.f_50542_.m_49966_(), 3);
                }
                break;
            }
            case STATUES: {
                n.m_128347_("PlayerX", p.m_20185_());
                n.m_128347_("PlayerZ", p.m_20189_());
                for (int x = -24; x <= 24; ++x) {
                    p.m_9236_().m_7731_(OdysseyRuns.at(s, x, 0, -24), Blocks.f_50495_.m_49966_(), 3);
                }
                break;
            }
        }
    }

    private static void chairs(ServerPlayer p, AdventureData.Session s) {
        CompoundTag n = s.journey;
        for (Tag row : n.m_128437_("Seats", 10)) {
            Object object;
            CompoundTag tag = (CompoundTag)row;
            if (!tag.m_128403_("Id") || !((object = p.m_284548_().m_8791_(tag.m_128342_("Id"))) instanceof ArmorStand)) continue;
            ArmorStand old = (ArmorStand)object;
            object = List.copyOf(old.m_20197_()).iterator();
            while (object.hasNext()) {
                Entity rider = (Entity)object.next();
                rider.m_8127_();
            }
            if (p.m_9236_().m_8055_(old.m_20183_()).m_60713_(Blocks.f_50398_)) {
                p.m_9236_().m_7471_(old.m_20183_(), false);
            }
            old.m_146870_();
        }
        int count = Math.max(1, OdysseyRuns.allies(p, s).size() - n.m_128451_("Round"));
        ListTag list = new ListTag();
        for (int i = 0; i < count; ++i) {
            double angle = (double)(i * 2) * Math.PI / (double)count;
            ArmorStand stand = OdysseyGames.seat(p, s, Vec3.m_82539_((Vec3i)s.anchor.m_7494_()).m_82520_(Math.cos(angle) * 7.0, 0.0, Math.sin(angle) * 7.0), false);
            p.m_9236_().m_7731_(stand.m_20183_(), Blocks.f_50398_.m_49966_(), 3);
            CompoundTag tag = new CompoundTag();
            tag.m_128362_("Id", stand.m_20148_());
            list.add((Object)tag);
        }
        n.m_128365_("Seats", (Tag)list);
        n.m_128405_("RoundStarted", n.m_128451_("Elapsed"));
    }

    private static BlockPos pixel(AdventureData.Session s, int i) {
        return OdysseyRuns.at(s, i % 8 - 4, 0, i / 8 - 4);
    }

    private static int target(AdventureData.Session s, int i) {
        int x = i % 8;
        int z = i / 8;
        int pattern = Math.floorMod((int)s.journey.m_128454_("Seed"), 3);
        return pattern == 0 ? (x == z || x + z == 7 ? 1 : 0) : (pattern == 1 ? (x >= 2 && x <= 5 && z >= 2 && z <= 5 ? 2 : 0) : ((x / 2 + z / 2) % 2 == 0 ? 3 : 0));
    }

    private static Block color(int c) {
        return (new Block[]{Blocks.f_50542_, Blocks.f_50500_, Blocks.f_50074_, Blocks.f_50499_})[Math.floorMod(c, 4)];
    }

    /*
     * WARNING - void declaration
     */
    public static void tick(ServerPlayer p, AdventureData.Session s) {
        CompoundTag n = s.journey;
        OdysseyCatalog.Mode mode = OdysseyRuns.mode(s);
        int t = n.m_128451_("Elapsed");
        List<Mob> allies = OdysseyRuns.allies(p, s);
        ItemEntity item = OdysseyGames.ball(p, s);
        switch (mode) {
            case TUG: {
                double rope = n.m_128459_("Rope");
                if (t % 20 == 0) {
                    n.m_128347_("Rope", rope -= 0.35 + 0.1 * (double)n.m_128451_("Difficulty"));
                    p.m_6330_((SoundEvent)SoundEvents.f_12210_.m_203334_(), SoundSource.PLAYERS, 0.5f, 1.0f);
                }
                int i = 0;
                for (Mob mob : allies) {
                    OdysseyRuns.move(mob, Vec3.m_82539_((Vec3i)s.anchor.m_7494_()).m_82520_(i++ % 2 == 0 ? -3.0 : 3.0, 0.0, Math.max(-10.0, Math.min(10.0, rope))), 0.9);
                }
                if (rope >= 8.0 && allies.stream().anyMatch(m -> m.m_20189_() > (double)(s.anchor.m_123343_() + 5))) {
                    OdysseyRuns.win(p, s);
                    break;
                }
                if (!(rope <= -8.0)) break;
                OdysseyRuns.finish(p, false);
                break;
            }
            case CHAIRS: {
                int elapsed = t - n.m_128451_("RoundStarted");
                ArrayList<ArmorStand> seats = new ArrayList<ArmorStand>();
                for (Object row : n.m_128437_("Seats", 10)) {
                    Entity entity;
                    CompoundTag compoundTag = (CompoundTag)row;
                    if (!compoundTag.m_128403_("Id") || !((entity = p.m_284548_().m_8791_(compoundTag.m_128342_("Id"))) instanceof ArmorStand)) continue;
                    ArmorStand stand = (ArmorStand)entity;
                    seats.add(stand);
                }
                int index = 0;
                for (Mob mob : allies) {
                    if (mob.getPersistentData().m_128471_("OdysseyOut") || mob.m_20159_()) continue;
                    if (elapsed < 120 - n.m_128451_("Difficulty") * 20) {
                        double angle = (double)t * 0.025 + (double)(index++ * 2) * Math.PI / (double)Math.max(1, allies.size());
                        OdysseyRuns.move(mob, Vec3.m_82539_((Vec3i)s.anchor.m_7494_()).m_82520_(Math.cos(angle) * 11.0, 0.0, Math.sin(angle) * 11.0), 1.0);
                        continue;
                    }
                    if (t % 10 != 0) continue;
                    ArmorStand nearest = seats.stream().filter(v -> !v.m_20160_()).min(Comparator.comparingDouble(arg_0 -> ((Mob)mob).m_20280_(arg_0))).orElse(null);
                    if (nearest == null) continue;
                    OdysseyRuns.move(mob, nearest.m_20182_(), 0.8);
                    if (!(mob.m_20280_((Entity)nearest) < 2.0)) continue;
                    mob.m_7998_((Entity)nearest, true);
                }
                if (elapsed < 200 - n.m_128451_("Difficulty") * 30) break;
                if (!(p.m_20202_() instanceof ArmorStand) || !seats.contains(p.m_20202_())) {
                    OdysseyRuns.finish(p, false);
                    return;
                }
                int remaining = 0;
                for (Mob m4 : allies) {
                    if (!m4.m_20159_()) {
                        m4.getPersistentData().m_128379_("OdysseyOut", true);
                        m4.m_21573_().m_26573_();
                    } else {
                        ++remaining;
                    }
                    m4.m_8127_();
                }
                p.m_8127_();
                if (remaining == 0) {
                    OdysseyRuns.win(p, s);
                    return;
                }
                n.m_128405_("Round", n.m_128451_("Round") + 1);
                OdysseyGames.chairs(p, s);
                break;
            }
            case PAINT: {
                void var11_28;
                if (!n.m_128471_("Painting") || allies.isEmpty()) {
                    return;
                }
                int pixel = n.m_128451_("Pixel");
                int paint = n.m_128451_("Color");
                Mob m5 = allies.get(0);
                Vec3 at = Vec3.m_82539_((Vec3i)OdysseyGames.pixel(s, pixel).m_7494_());
                OdysseyRuns.move(m5, at, 0.9);
                if (!(m5.m_20238_(at) < 2.0)) break;
                p.m_9236_().m_7731_(OdysseyGames.pixel(s, pixel), OdysseyGames.color(paint).m_49966_(), 3);
                n.m_128379_("Painting", false);
                boolean bl = false;
                for (int i = 0; i < 64; ++i) {
                    if (!p.m_9236_().m_8055_(OdysseyGames.pixel(s, i)).m_60713_(OdysseyGames.color(OdysseyGames.target(s, i)))) continue;
                    ++var11_28;
                }
                n.m_128405_("Score", (int)var11_28);
                if (var11_28 != 64) break;
                OdysseyRuns.win(p, s);
                break;
            }
            case STATUES: {
                double d;
                double dx;
                boolean red;
                int phase = t % 120;
                boolean bl = red = phase >= 80;
                if (phase == 80) {
                    p.m_6330_((SoundEvent)SoundEvents.f_12209_.m_203334_(), SoundSource.PLAYERS, 0.7f, 0.8f);
                }
                if (red && phase >= 86 && (dx = p.m_20185_() - n.m_128459_("PlayerX")) * dx + (d = p.m_20189_() - n.m_128459_("PlayerZ")) * d > 0.015) {
                    int mistakes = n.m_128451_("Mistakes") + 1;
                    n.m_128405_("Mistakes", mistakes);
                    p.m_6021_((double)s.anchor.m_123341_() + 0.5, (double)(s.anchor.m_123342_() + 1), (double)s.anchor.m_123343_() + 24.5);
                    if (mistakes >= 3) {
                        OdysseyRuns.finish(p, false);
                        return;
                    }
                }
                n.m_128347_("PlayerX", p.m_20185_());
                n.m_128347_("PlayerZ", p.m_20189_());
                for (Mob m6 : allies) {
                    if (red) {
                        m6.m_21573_().m_26573_();
                        m6.m_20256_(m6.m_20184_().m_82542_(0.0, 1.0, 0.0));
                        continue;
                    }
                    OdysseyRuns.move(m6, Vec3.m_82539_((Vec3i)OdysseyRuns.at(s, 0, 1, -26)), 0.8);
                }
                if (!(p.m_20189_() < (double)(s.anchor.m_123343_() - 23)) || !allies.stream().anyMatch(m -> m.m_20189_() < (double)(s.anchor.m_123343_() - 23))) break;
                OdysseyRuns.win(p, s);
                break;
            }
        }
    }

    public static boolean input(ServerPlayer p, AdventureData.Session s, int action, int value) {
        CompoundTag n = s.journey;
        int t = n.m_128451_("Elapsed");
        ItemEntity item = OdysseyGames.ball(p, s);
        switch (OdysseyRuns.mode(s)) {
            case TUG: {
                if (action != 0 || p.m_20238_(Vec3.m_82512_((Vec3i)s.anchor)) > 144.0 || t - n.m_128451_("LastPull") < 6 || t % 20 > (OdysseyCamp.festivalRelic(p) ? 7 : 5)) {
                    return false;
                }
                n.m_128405_("LastPull", t);
                n.m_128347_("Rope", n.m_128459_("Rope") + 1.1);
                return true;
            }
            case CHAIRS: {
                if (action != 0 || t - n.m_128451_("RoundStarted") < 120 - n.m_128451_("Difficulty") * 20) {
                    return false;
                }
                ListTag seats = n.m_128437_("Seats", 10);
                if (value < 0 || value >= seats.size()) {
                    return false;
                }
                CompoundTag row = seats.m_128728_(value);
                Entity entity = row.m_128403_("Id") ? p.m_284548_().m_8791_(row.m_128342_("Id")) : null;
                return entity != null && !entity.m_20160_() && p.m_20280_(entity) < 9.0 && p.m_7998_(entity, true);
            }
            case PAINT: {
                if (action != 0 || value < 0 || value >= 256 || n.m_128471_("Painting") || p.m_20238_(Vec3.m_82512_((Vec3i)OdysseyGames.pixel(s, value / 4))) > 144.0) {
                    return false;
                }
                n.m_128405_("Pixel", value / 4);
                n.m_128405_("Color", value % 4);
                n.m_128379_("Painting", true);
                return true;
            }
        }
        return false;
    }

    public static int[] pattern(AdventureData.Session s) {
        int[] values = new int[64];
        for (int i = 0; i < 64; ++i) {
            values[i] = OdysseyGames.target(s, i);
        }
        return values;
    }
}

