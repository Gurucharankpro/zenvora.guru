/*
 * Decompiled with CFR 0.152.
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ModLanguage;

public final class InterfaceStyle {
    public static boolean flag(String key) {
        return Boolean.parseBoolean(ClientPreferences.get("ui." + key, "false"));
    }

    public static int palette() {
        try {
            return Math.floorMod(Integer.parseInt(ClientPreferences.get("ui.palette", "0")), 3);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int accent() {
        return (new int[]{-5076236, -9904424, -1981295})[InterfaceStyle.palette()];
    }

    public static void toggle(String key) {
        ClientPreferences.set("ui." + key, Boolean.toString(!InterfaceStyle.flag(key)));
    }

    public static String text(String en, String ar) {
        return ModLanguage.arabic() ? ar : en;
    }
}

