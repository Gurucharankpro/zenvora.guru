/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.client.Minecraft
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 */
package dev.shadowsovereign.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class OdysseyBookCatalog {
    public static List<Release> read() {
        ArrayList<Release> result = new ArrayList<Release>();
        ResourceManager manager = Minecraft.m_91087_().m_91098_();
        for (Map.Entry row : manager.m_214159_("odyssey/releases", id -> id.m_135827_().equals("shadowsovereign") && id.m_135815_().endsWith(".json")).entrySet()) {
            if (result.size() >= 64) break;
            try {
                BufferedReader reader = ((Resource)row.getValue()).m_215508_();
                try {
                    JsonObject json = JsonParser.parseReader((Reader)reader).getAsJsonObject();
                    String version = json.get("version").getAsString();
                    if (!version.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) continue;
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    for (JsonElement value : json.getAsJsonArray("features")) {
                        if (entries.size() >= 128) break;
                        JsonObject n = value.getAsJsonObject();
                        String title = n.get("title").getAsString();
                        String help = n.get("help").getAsString();
                        if (title.length() > 256 || help.length() > 256 || Set.of("odyssey.shadowsovereign.feature.39", "odyssey.shadowsovereign.feature.40", "odyssey.shadowsovereign.feature.43", "odyssey.shadowsovereign.feature.45", "soullife.shadowsovereign.game.fetch").contains(title)) continue;
                        entries.add(new Entry(title, help, n.has("page") ? Math.max(0, Math.min(11, n.get("page").getAsInt())) : 1));
                    }
                    result.add(new Release(version, List.copyOf(entries)));
                }
                finally {
                    if (reader == null) continue;
                    reader.close();
                }
            }
            catch (IOException | RuntimeException exception) {}
        }
        result.sort(Comparator.comparingInt(r -> OdysseyBookCatalog.number(r.version())).reversed());
        return List.copyOf(result);
    }

    private static int number(String version) {
        String[] p = version.split("\\.");
        return Integer.parseInt(p[0]) * 1000000 + Integer.parseInt(p[1]) * 1000 + Integer.parseInt(p[2]);
    }

    private OdysseyBookCatalog() {
    }

    public record Entry(String title, String help, int page) {
    }

    public record Release(String version, List<Entry> entries) {
    }
}

