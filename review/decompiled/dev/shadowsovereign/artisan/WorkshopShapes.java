/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.shadowsovereign.artisan;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class WorkshopShapes {
    private static final Map<String, VoxelShape[]> SHAPES = WorkshopShapes.load();

    private static Map<String, VoxelShape[]> load() {
        HashMap<String, VoxelShape[]> out = new HashMap<String, VoxelShape[]>();
        try (InputStream in = WorkshopShapes.class.getResourceAsStream("/data/shadowsovereign/workshop_geometry.json");){
            JsonObject root = JsonParser.parseReader((Reader)new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry entry : root.entrySet()) {
                VoxelShape[] shapes = new VoxelShape[4];
                for (int r = 0; r < 4; ++r) {
                    VoxelShape shape = Shapes.m_83040_();
                    for (JsonElement value : ((JsonElement)entry.getValue()).getAsJsonArray()) {
                        JsonArray a = value.getAsJsonArray();
                        double x = a.get(0).getAsDouble();
                        double y = a.get(1).getAsDouble();
                        double z = a.get(2).getAsDouble();
                        double X = a.get(3).getAsDouble();
                        double Y = a.get(4).getAsDouble();
                        double Z = a.get(5).getAsDouble();
                        for (int j = 0; j < r; ++j) {
                            double ox = x;
                            double oX = X;
                            x = 16.0 - Z;
                            X = 16.0 - z;
                            z = ox;
                            Z = oX;
                        }
                        shape = Shapes.m_83110_((VoxelShape)shape, (VoxelShape)Block.m_49796_((double)x, (double)y, (double)z, (double)X, (double)Y, (double)Z));
                    }
                    shapes[r] = shape.m_83296_();
                }
                out.put((String)entry.getKey(), shapes);
            }
        }
        catch (Exception ex) {
            throw new IllegalStateException("Cannot read workshop collision geometry", ex);
        }
        return Map.copyOf(out);
    }

    public static VoxelShape shape(String id, Direction d) {
        VoxelShape voxelShape;
        VoxelShape[] a = SHAPES.get(id);
        if (a == null) {
            voxelShape = Shapes.m_83144_();
        } else {
            voxelShape = a[switch (d) {
                case Direction.EAST -> 1;
                case Direction.SOUTH -> 2;
                case Direction.WEST -> 3;
                default -> 0;
            }];
        }
        return voxelShape;
    }
}

