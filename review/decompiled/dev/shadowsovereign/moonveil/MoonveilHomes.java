/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.level.saveddata.SavedData
 *  net.minecraftforge.common.ToolActions
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.level.BlockEvent$BlockToolModificationEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.moonveil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.shadowsovereign.expanse.ExpanseWorld;
import dev.shadowsovereign.gloom.GloomContent;
import dev.shadowsovereign.moonveil.MoonveilContent;
import dev.shadowsovereign.moonveil.Nuna;
import dev.shadowsovereign.mythic.MythicSpawn;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class MoonveilHomes
extends SavedData {
    private static final List<Home> HOMES = MoonveilHomes.read();
    private final Set<Long> done = new HashSet<Long>();

    /*
     * Enabled aggressive exception aggregation
     */
    private static List<Home> read() {
        try (InputStream in = MoonveilHomes.class.getResourceAsStream("/data/shadowsovereign/moonveil/sites.json");){
            List list;
            try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);){
                ArrayList<Home> out = new ArrayList<Home>();
                for (JsonElement v : JsonParser.parseReader((Reader)reader).getAsJsonArray()) {
                    JsonObject o = v.getAsJsonObject();
                    ArrayList<BlockPos> markers = new ArrayList<BlockPos>();
                    for (JsonElement el : o.getAsJsonArray("nuna")) {
                        JsonArray p = el.getAsJsonArray();
                        markers.add(new BlockPos(p.get(0).getAsInt(), p.get(1).getAsInt(), p.get(2).getAsInt()));
                    }
                    if (markers.isEmpty()) continue;
                    out.add(new Home(new ResourceLocation("shadowsovereign", o.get("name").getAsString()), List.copyOf(markers)));
                }
                list = List.copyOf(out);
            }
            return list;
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static MoonveilHomes data(ServerLevel w) {
        return (MoonveilHomes)w.m_8895_().m_164861_(MoonveilHomes::load, MoonveilHomes::new, "soulbound_moonveil_homes");
    }

    public static MoonveilHomes load(CompoundTag n) {
        MoonveilHomes v = new MoonveilHomes();
        for (long key : n.m_128467_("Homes")) {
            if (v.done.size() >= 8192) break;
            v.done.add(key);
        }
        return v;
    }

    public CompoundTag m_7176_(CompoundTag n) {
        n.m_128388_("Homes", this.done.stream().mapToLong(Long::longValue).toArray());
        return n;
    }

    public static void visit(ServerPlayer p) {
        if (!OdysseyRealm.inside(p) || p.m_5833_() || OdysseyRealm.foreignPlot(p, p.m_20183_())) {
            return;
        }
        ServerLevel w = p.m_284548_();
        MoonveilHomes ledger = MoonveilHomes.data(w);
        if (ledger.done.size() >= 8192) {
            return;
        }
        for (Home home : HOMES) {
            Object object;
            StructureStart start;
            Structure definition = (Structure)w.m_9598_().m_175515_(Registries.f_256944_).m_7745_(home.structure());
            if (definition == null || !(start = w.m_215010_().m_220494_(p.m_20183_(), definition)).m_73603_() || start.m_73602_().isEmpty() || !((object = start.m_73602_().get(0)) instanceof PoolElementStructurePiece)) continue;
            PoolElementStructurePiece piece = (PoolElementStructurePiece)object;
            object = home.markers().iterator();
            while (object.hasNext()) {
                Nuna pet;
                BlockPos local = (BlockPos)object.next();
                BlockPos at = StructureTemplate.m_74593_((BlockPos)local, (Mirror)Mirror.NONE, (Rotation)piece.m_6830_(), (BlockPos)BlockPos.f_121853_).m_121955_((Vec3i)piece.m_72646_());
                if (ledger.done.contains(at.m_121878_()) || p.m_20183_().m_123331_((Vec3i)at) > 576.0 || (pet = (Nuna)((EntityType)MoonveilContent.NUNA.get()).m_20615_((Level)w)) == null) continue;
                if (!MythicSpawn.place(w, (Mob)pet, at, start.m_73601_())) {
                    pet.m_146870_();
                    continue;
                }
                pet.m_21530_();
                if (w.m_7967_((Entity)pet) && w.m_8791_(pet.m_20148_()) == pet) {
                    pet.animate(1);
                    ledger.done.add(at.m_121878_());
                    ledger.m_77762_();
                    continue;
                }
                pet.m_146870_();
            }
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        ServerLevel w = e.getServer().m_129880_(OdysseyRealm.KEY);
        if (w != null) {
            for (ServerPlayer p : w.m_6907_()) {
                if (p.f_19797_ % 40 != 0) continue;
                MoonveilHomes.visit(p);
            }
        }
    }

    @SubscribeEvent
    public static void hoe(BlockEvent.BlockToolModificationEvent e) {
        Player player;
        if (e.isCanceled() || e.getToolAction() != ToolActions.HOE_TILL || !((player = e.getPlayer()) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer p = (ServerPlayer)player;
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey((Object)e.getState().m_60734_());
        if (!(id != null && id.m_135827_().equals("shadowsovereign") && (id.m_135815_().startsWith("moonveil_") || id.m_135815_().startsWith("crimson_shadow_")) && e.getState().m_204336_(BlockTags.f_144274_) && p.m_9236_().m_8055_(e.getPos().m_7494_()).m_60795_())) {
            return;
        }
        if (OdysseyRealm.inside(p) && !ExpanseWorld.editable(p, e.getPos())) {
            return;
        }
        e.setFinalState(((Block)GloomContent.FARMLAND.get()).m_49966_());
    }

    private record Home(ResourceLocation structure, List<BlockPos> markers) {
    }
}

