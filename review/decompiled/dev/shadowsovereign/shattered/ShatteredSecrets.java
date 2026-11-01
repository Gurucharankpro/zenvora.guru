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
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.shattered;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;

public final class ShatteredSecrets {
    private static final Map<ResourceLocation, Panel> PANELS = ShatteredSecrets.read();

    /*
     * Enabled aggressive exception aggregation
     */
    private static Map<ResourceLocation, Panel> read() {
        try (InputStream in = ShatteredSecrets.class.getResourceAsStream("/data/shadowsovereign/shattered/secrets.json");){
            Map map;
            try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);){
                HashMap<ResourceLocation, Panel> out = new HashMap<ResourceLocation, Panel>();
                for (JsonElement e : JsonParser.parseReader((Reader)reader).getAsJsonArray()) {
                    JsonObject o = e.getAsJsonObject();
                    ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
                    for (JsonElement p : o.getAsJsonArray("positions")) {
                        JsonArray a = p.getAsJsonArray();
                        positions.add(new BlockPos(a.get(0).getAsInt(), a.get(1).getAsInt(), a.get(2).getAsInt()));
                    }
                    HashSet<ResourceLocation> blocks = new HashSet<ResourceLocation>();
                    for (JsonElement b : o.getAsJsonArray("blocks")) {
                        blocks.add(new ResourceLocation(b.getAsString()));
                    }
                    out.put(new ResourceLocation("shadowsovereign", o.get("structure").getAsString()), new Panel(List.copyOf(positions), Set.copyOf(blocks)));
                }
                map = Map.copyOf(out);
            }
            return map;
        }
        catch (IOException e) {
            throw new IllegalStateException("Missing authored secret panels", e);
        }
    }

    public static boolean openable(ServerLevel w, BlockPos at, ResourceLocation structure, StructureStart start) {
        PoolElementStructurePiece piece;
        Panel panel;
        block4: {
            block3: {
                Object object;
                panel = PANELS.get(structure);
                if (panel == null || start.m_73602_().isEmpty() || !((object = start.m_73602_().get(0)) instanceof PoolElementStructurePiece)) break block3;
                piece = (PoolElementStructurePiece)object;
                if (panel.blocks().contains(ForgeRegistries.BLOCKS.getKey((Object)w.m_8055_(at).m_60734_()))) break block4;
            }
            return false;
        }
        for (BlockPos local : panel.positions()) {
            if (!StructureTemplate.m_74593_((BlockPos)local, (Mirror)Mirror.NONE, (Rotation)piece.m_6830_(), (BlockPos)BlockPos.f_121853_).m_121955_((Vec3i)piece.m_72646_()).equals((Object)at)) continue;
            return true;
        }
        return false;
    }

    private ShatteredSecrets() {
    }

    private record Panel(List<BlockPos> positions, Set<ResourceLocation> blocks) {
    }
}

