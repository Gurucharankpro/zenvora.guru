/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 */
package dev.shadowsovereign.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.shadowsovereign.client.ClientMythic;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicProfiles;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

final class LegendLedger {
    private static final Map<String, String> BILINGUAL = LegendLedger.loadWords();

    LegendLedger() {
    }

    private static Map<String, String> loadWords() {
        HashMap<String, String> out = new HashMap<String, String>();
        for (String lang : List.of("en_us", "ar_sa")) {
            try {
                InputStream in = LegendLedger.class.getResourceAsStream("/assets/shadowsovereign/lang/" + lang + ".json");
                try {
                    JsonObject json = JsonParser.parseReader((Reader)new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8)).getAsJsonObject();
                    for (Map.Entry e : json.entrySet()) {
                        if (!((String)e.getKey()).startsWith("entity.shadowsovereign.") && !((String)e.getKey()).startsWith("grimoire.shadowsovereign.")) continue;
                        out.merge((String)e.getKey(), ((JsonElement)e.getValue()).getAsString(), (a, b) -> a + " " + b);
                    }
                }
                finally {
                    if (in == null) continue;
                    in.close();
                }
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return Map.copyOf(out);
    }

    static List<CompoundTag> entries() {
        return ClientMythic.data.m_128437_("Creatures", 10).stream().map(t -> (CompoundTag)t).toList();
    }

    static CompoundTag entry(int i) {
        List<CompoundTag> list = LegendLedger.entries();
        return i >= 0 && i < list.size() ? list.get(i) : new CompoundTag();
    }

    static String id(int i) {
        return i == 0 ? "soul_devourer" : MythicExpedition.IDS[Math.max(0, Math.min(MythicExpedition.IDS.length - 1, i - 1))];
    }

    static boolean known(int i) {
        return LegendLedger.entry(i).m_128451_("Status") == 2;
    }

    static String name(int i) {
        return LegendLedger.known(i) ? Component.m_237115_((String)("entity.shadowsovereign." + LegendLedger.id(i))).getString() : InterfaceStyle.text("Sealed legend \u2022 ?", "\u0623\u0633\u0637\u0648\u0631\u0629 \u0645\u062e\u062a\u0648\u0645\u0629 \u2022 \u061f");
    }

    static String alias(int i) {
        return ClientPreferences.get("legend.alias." + LegendLedger.id(i), "");
    }

    static String note(int i) {
        return ClientPreferences.get("quest.note.legend." + LegendLedger.id(i), "");
    }

    static String role(int i) {
        return ClientPreferences.get("legend.role." + LegendLedger.id(i), "");
    }

    static String roleArabic(String role) {
        return switch (role) {
            case "Frontline" -> "\u0627\u0644\u062e\u0637 \u0627\u0644\u0623\u0645\u0627\u0645\u064a";
            case "Support" -> "\u0627\u0644\u062f\u0639\u0645";
            case "Scout" -> "\u0627\u0644\u0627\u0633\u062a\u0637\u0644\u0627\u0639";
            case "Mount" -> "\u0645\u0637\u064a\u0629";
            case "Guard" -> "\u062d\u0631\u0627\u0633\u0629";
            case "Ranged" -> "\u0628\u0639\u064a\u062f \u0627\u0644\u0645\u062f\u0649";
            default -> "\u063a\u064a\u0631 \u0645\u062d\u062f\u062f";
        };
    }

    static String roleLabel(int i) {
        return ModLanguage.arabic() ? LegendLedger.roleArabic(LegendLedger.role(i)) : LegendLedger.role(i);
    }

    static boolean favorite(int i) {
        return Boolean.parseBoolean(ClientPreferences.get("legend.favorite." + LegendLedger.id(i), "false"));
    }

    static void favorite(int i, boolean b) {
        ClientPreferences.set("legend.favorite." + LegendLedger.id(i), Boolean.toString(b));
    }

    static boolean mount(int i) {
        return i > 0 && MythicProfiles.get(i - 1).rideable();
    }

    static double hp(int i) {
        return i == 0 ? 375.0 : MythicProfiles.get(i - 1).health() * 1.25;
    }

    static double attack(int i) {
        return i == 0 ? 15.0 : MythicProfiles.get(i - 1).damage() * 1.25;
    }

    static double armor(int i) {
        return i == 0 ? 6.0 : MythicProfiles.get(i - 1).armor();
    }

    static String n(double n) {
        return BigDecimal.valueOf(n).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    static String lore(int i) {
        return Component.m_237115_((String)("grimoire.shadowsovereign.lore." + LegendLedger.id(i))).getString();
    }

    static String abilities(int i) {
        return Component.m_237115_((String)("grimoire.shadowsovereign.abilities." + LegendLedger.id(i))).getString();
    }

    static String search(int i) {
        if (!LegendLedger.known(i)) {
            return "? sealed \u0645\u062e\u062a\u0648\u0645";
        }
        CompoundTag d = LegendLedger.entry(i);
        String key = "entity.shadowsovereign." + LegendLedger.id(i);
        return BILINGUAL.getOrDefault(key, "") + " " + BILINGUAL.getOrDefault("grimoire.shadowsovereign.lore." + LegendLedger.id(i), "") + " " + BILINGUAL.getOrDefault("grimoire.shadowsovereign.abilities." + LegendLedger.id(i), "") + " " + LegendLedger.id(i) + " " + LegendLedger.name(i) + " " + LegendLedger.lore(i) + " " + LegendLedger.abilities(i) + " " + LegendLedger.alias(i) + " " + LegendLedger.note(i) + " " + LegendLedger.role(i) + " " + LegendLedger.roleArabic(LegendLedger.role(i)) + " " + d.m_128461_("Tier");
    }

    static String sheet(int i) {
        CompoundTag d = LegendLedger.entry(i);
        return ModLanguage.tr(LegendLedger.name(i) + "\n" + LegendLedger.lore(i) + "\n" + LegendLedger.abilities(i) + "\nHP " + LegendLedger.n(LegendLedger.hp(i)) + " | ATK " + LegendLedger.n(LegendLedger.attack(i)) + " | ARM " + LegendLedger.n(LegendLedger.armor(i)) + "\nLevel " + d.m_128451_("Level") + " | Kills " + d.m_128454_("Kills") + "\n" + ModLanguage.user(LegendLedger.note(i)));
    }
}

