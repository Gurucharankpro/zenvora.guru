/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 */
package dev.shadowsovereign.shattered;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ShatteredIllustrations {
    private static final Map<String, int[]> SIZES = ShatteredIllustrations.read();

    /*
     * Enabled aggressive exception aggregation
     */
    private static Map<String, int[]> read() {
        try (InputStream in = ShatteredIllustrations.class.getResourceAsStream("/data/shadowsovereign/shattered/illustrations.json");){
            Map map;
            try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);){
                HashMap<String, int[]> out = new HashMap<String, int[]>();
                for (Map.Entry e : JsonParser.parseReader((Reader)reader).getAsJsonObject().entrySet()) {
                    JsonArray a = ((JsonElement)e.getValue()).getAsJsonArray();
                    out.put((String)e.getKey(), new int[]{a.get(0).getAsInt(), a.get(1).getAsInt()});
                }
                map = Map.copyOf(out);
            }
            return map;
        }
        catch (IOException e) {
            throw new IllegalStateException("Missing new dossier image metadata", e);
        }
    }

    public static int[] size(String id) {
        return SIZES.get(id);
    }

    private ShatteredIllustrations() {
    }
}

