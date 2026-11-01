/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.mythic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.monolith.MonolithWorld;
import dev.shadowsovereign.mythic.ChimeMite;
import dev.shadowsovereign.mythic.MythicContent;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class BelfryNests
extends SavedData {
    private static final List<BlockPos> NEST = new ArrayList<BlockPos>();
    private static final List<BlockPos> VAULT = new ArrayList<BlockPos>();
    private final Map<Long, Integer> spawned = new HashMap<Long, Integer>();

    private static BelfryNests data(ServerPlayer p) {
        return (BelfryNests)p.f_8924_.m_129783_().m_8895_().m_164861_(BelfryNests::load, BelfryNests::new, "soulbound_belfry_nests");
    }

    private static BelfryNests load(CompoundTag n) {
        BelfryNests out = new BelfryNests();
        for (Tag row : n.m_128437_("Nests", 10)) {
            if (out.spawned.size() >= 8192) break;
            CompoundTag r = (CompoundTag)row;
            out.spawned.put(r.m_128454_("At"), r.m_128451_("Mask") & 0x1F);
        }
        return out;
    }

    public CompoundTag m_7176_(CompoundTag n) {
        ListTag list = new ListTag();
        this.spawned.forEach((at, mask) -> {
            CompoundTag row = new CompoundTag();
            row.m_128356_("At", at.longValue());
            row.m_128405_("Mask", mask.intValue());
            list.add((Object)row);
        });
        n.m_128365_("Nests", (Tag)list);
        return n;
    }

    private static BlockPos position(MonolithWorld.Site site, StructureStart start, BlockPos origin, BlockPos local) {
        Object e;
        if (start != null && start.m_73603_() && !start.m_73602_().isEmpty() && (e = start.m_73602_().get(0)) instanceof PoolElementStructurePiece) {
            PoolElementStructurePiece piece = (PoolElementStructurePiece)e;
            return StructureTemplate.m_74593_((BlockPos)local.m_121996_((Vec3i)site.rootOffset()), (Mirror)Mirror.NONE, (Rotation)piece.m_6830_(), (BlockPos)BlockPos.f_121853_).m_121955_((Vec3i)piece.m_72646_());
        }
        return origin.m_121955_((Vec3i)local);
    }

    public static void visit(ServerPlayer p, MonolithWorld.Site site, StructureStart start, BlockPos origin, BlockPos altar) {
        if (site.kind() != 20 || p.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            return;
        }
        BelfryNests saved = BelfryNests.data(p);
        long key = altar.m_121878_();
        if (!saved.spawned.containsKey(key) && saved.spawned.size() >= 8192) {
            return;
        }
        int mask = saved.spawned.getOrDefault(key, 0);
        for (int i = 0; i < NEST.size(); ++i) {
            double lift;
            ChimeMite mite;
            if ((mask & 1 << i) != 0) continue;
            BlockPos at = BelfryNests.position(site, start, origin, NEST.get(i));
            if (!p.m_284548_().m_143340_(at) || (mite = (ChimeMite)((EntityType)MythicContent.MITE.get()).m_20615_((Level)p.m_284548_())) == null) continue;
            VoxelShape shape = p.m_9236_().m_8055_(at).m_60812_((BlockGetter)p.m_9236_(), at);
            double d = lift = shape.m_83281_() ? 0.0 : shape.m_83297_(Direction.Axis.Y);
            if (lift > 0.5) continue;
            mite.m_6027_((double)at.m_123341_() + 0.5, (double)at.m_123342_() + lift, (double)at.m_123343_() + 0.5);
            if (!p.m_284548_().m_45786_((Entity)mite) || p.m_9236_().m_8055_(at.m_7495_()).m_60812_((BlockGetter)p.m_9236_(), at.m_7495_()).m_83281_() || !p.m_284548_().m_7967_((Entity)mite)) continue;
            saved.spawned.put(key, mask |= 1 << i);
            saved.m_77762_();
        }
    }

    @SubscribeEvent
    public static void vault(PlayerInteractEvent.RightClickBlock e) {
        ServerPlayer p;
        Player player = e.getEntity();
        if (!(player instanceof ServerPlayer) || !OdysseyRealm.inside(p = (ServerPlayer)player) || OdysseyRealm.foreignPlot(p, e.getPos()) || !p.m_9236_().m_8055_(e.getPos()).m_60713_(Blocks.f_152594_)) {
            return;
        }
        MonolithWorld.Site site = MonolithWorld.sites().stream().filter(s -> s.kind() == 20).findFirst().orElse(null);
        if (site == null) {
            return;
        }
        Structure structure = (Structure)p.f_8924_.m_206579_().m_175515_(Registries.f_256944_).m_7745_(new ResourceLocation("shadowsovereign", site.name()));
        StructureStart start = structure == null ? null : p.m_284548_().m_215010_().m_220494_(e.getPos(), structure);
        BlockPos origin = null;
        if (start == null || !start.m_73603_()) {
            for (Map.Entry<UUID, AdventureData.Journal> owner : AdventureData.get((MinecraftServer)p.f_8924_).owners.entrySet()) {
                CompoundTag claim = owner.getValue().mythic.gloom.m_128469_("Monolith").m_128469_(site.name());
                if (!claim.m_128441_("Origin")) continue;
                BlockPos at = BlockPos.m_122022_((long)claim.m_128454_("Origin"));
                if (!VAULT.stream().anyMatch(v -> at.m_121955_((Vec3i)v).equals((Object)e.getPos()))) continue;
                if (!owner.getKey().equals(p.m_20148_())) {
                    return;
                }
                origin = at;
                break;
            }
        }
        if (!(origin != null || start != null && start.m_73603_())) {
            return;
        }
        ArrayList<BlockPos> mapped = new ArrayList<BlockPos>();
        for (BlockPos local : VAULT) {
            mapped.add(BelfryNests.position(site, start, origin, local));
        }
        if (!mapped.contains(e.getPos())) {
            return;
        }
        e.setCanceled(true);
        e.setCancellationResult(InteractionResult.SUCCESS);
        if (MythicExpedition.state(p, 20).m_128451_("Status") != 2) {
            p.m_5661_((Component)Component.m_237115_((String)"chronicles.shadowsovereign.vault_locked"), true);
            return;
        }
        for (BlockPos at : mapped) {
            if (!p.m_9236_().m_8055_(at).m_60713_(Blocks.f_152594_)) continue;
            p.m_284548_().m_7731_(at, Blocks.f_50016_.m_49966_(), 3);
        }
        p.m_5661_((Component)Component.m_237115_((String)"chronicles.shadowsovereign.vault_open"), true);
    }

    static {
        try (InputStream in = BelfryNests.class.getResourceAsStream("/data/shadowsovereign/chronicles/belfry.json");
             InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);){
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            for (String key : List.of("nest", "vault")) {
                for (JsonElement row : root.getAsJsonArray(key)) {
                    JsonArray a = row.getAsJsonArray();
                    (key.equals("nest") ? NEST : VAULT).add(new BlockPos(a.get(0).getAsInt(), a.get(1).getAsInt(), a.get(2).getAsInt()));
                }
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException("Missing supplied belfry positions", ex);
        }
    }
}

