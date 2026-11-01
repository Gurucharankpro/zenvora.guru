/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.ShadowSovereign;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Properties;
import net.minecraft.client.Minecraft;

public final class ClientPreferences {
    private static Properties cached;

    private static Path path() {
        return Minecraft.m_91087_().f_91069_.toPath().resolve("soulbound-army-client.properties");
    }

    private static Properties load() {
        Properties p;
        block8: {
            p = new Properties();
            try {
                if (!Files.exists(ClientPreferences.path(), new LinkOption[0])) break block8;
                try (BufferedReader in = Files.newBufferedReader(ClientPreferences.path(), StandardCharsets.UTF_8);){
                    p.load(in);
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return p;
    }

    public static synchronized Properties read() {
        if (cached == null) {
            cached = ClientPreferences.load();
        }
        Properties copy = new Properties();
        copy.putAll((Map<?, ?>)cached);
        return copy;
    }

    public static synchronized String get(String key, String fallback) {
        if (cached == null) {
            cached = ClientPreferences.load();
        }
        return cached.getProperty(key, fallback);
    }

    public static synchronized void setAll(Map<String, String> values) {
        Properties p = ClientPreferences.read();
        values.forEach(p::setProperty);
        ClientPreferences.write(p);
    }

    public static synchronized void set(String key, String value) {
        Properties p = ClientPreferences.read();
        p.setProperty(key, value);
        ClientPreferences.write(p);
    }

    private static void write(Properties p) {
        try {
            Path tmp = ClientPreferences.path().resolveSibling(String.valueOf(ClientPreferences.path().getFileName()) + ".tmp");
            try (BufferedWriter out = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8, new OpenOption[0]);){
                p.store(out, "Soulbound Army local preferences");
            }
            try {
                Files.move(tmp, ClientPreferences.path(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException ex) {
                Files.move(tmp, ClientPreferences.path(), StandardCopyOption.REPLACE_EXISTING);
            }
            cached = new Properties();
            cached.putAll((Map<?, ?>)p);
        }
        catch (IOException ex) {
            ShadowSovereign.LOGGER.warn("Could not save Soulbound Army preferences", (Throwable)ex);
        }
    }
}

