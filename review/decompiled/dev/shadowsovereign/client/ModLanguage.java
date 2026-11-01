/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.shadowsovereign.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.shadowsovereign.ShadowSovereign;
import dev.shadowsovereign.client.ClientLocalization;
import dev.shadowsovereign.client.ClientPreferences;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.CallSite;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public final class ModLanguage {
    private static final ResourceLocation ARABIC_FONT = new ResourceLocation("shadowsovereign", "arabic");
    private static boolean arabic = ClientPreferences.get("language", "en").equals("ar");
    private static final Map<String, String> AR = ModLanguage.load();
    private static final Map<String, String> EN_KEYS = ModLanguage.keys("en_us");
    private static final Map<String, String> AR_KEYS = ModLanguage.keys("ar_sa");
    private static final Pattern TOKENS = Pattern.compile(AR.keySet().stream().filter(s -> !s.isBlank()).sorted(Comparator.comparingInt(String::length).reversed()).map(ModLanguage::dummy).reduce((a, b) -> a + "|" + b).orElse("(?!)"), 2);
    private static final Map<String, String> CACHE = new LinkedHashMap<String, String>(256, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> e) {
            return this.size() > 2048;
        }
    };
    private static final Pattern CODE = Pattern.compile("[a-z0-9_.-]+:[a-z0-9_./-]+|/(?:shadow|arise)(?: +(?>gui|list|info|summon|recall|follow|stay|guard|assist|aggressive|attack|auto|autosummon|warfront|art|rune|plan|pact|inventory|feed|tiers|adventure|legacy|journey|ascension|squad|formation|priority|retreat|patrol|skill|help|start|resume|pause|abandon|claim|bind|next|confirm|save|run|stop|marker|all|step|taunt|barrier|mend|on|off|none|stride|mercy|ward|vigil|tether|hearth|phantom_double|time_stitch|sunder|[0-9]+|<[^>]+>|\\[[^]]+\\]))*");
    private static final Map<String, String> SEARCH_ALIASES = ModLanguage.searchAliases();

    private static Map<String, String> searchAliases() {
        HashMap<String, CallSite> map = new HashMap<String, CallSite>();
        for (String key : EN_KEYS.keySet()) {
            String en2 = EN_KEYS.get(key);
            String ar2 = AR_KEYS.getOrDefault(key, en2);
            String both = en2 + " " + ar2;
            map.put(en2.toLowerCase(Locale.ROOT), (CallSite)((Object)both));
            map.put(ar2.toLowerCase(Locale.ROOT), (CallSite)((Object)both));
        }
        AR.forEach((en, ar) -> {
            map.putIfAbsent((String)en, (CallSite)((Object)(en + " " + ar)));
            map.putIfAbsent(ar.toLowerCase(Locale.ROOT), (CallSite)((Object)(en + " " + ar)));
        });
        return Map.copyOf(map);
    }

    public static String searchTerms(String source) {
        String raw = source.replace("\ue000", "").replace("\ue001", "");
        return raw + " " + SEARCH_ALIASES.getOrDefault(raw.toLowerCase(Locale.ROOT), "");
    }

    private ModLanguage() {
    }

    private static String dummy(String s) {
        return (Character.isLetterOrDigit(s.charAt(0)) ? "(?<![A-Za-z0-9_])" : "") + Pattern.quote(s) + (Character.isLetterOrDigit(s.charAt(s.length() - 1)) ? "(?![A-Za-z0-9_])" : "");
    }

    private static Map<String, String> load() {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        try (InputStream in = ModLanguage.class.getResourceAsStream("/assets/shadowsovereign/localization/ar.json");){
            if (in != null) {
                JsonObject object = JsonParser.parseReader((Reader)new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
                object.entrySet().forEach(e -> result.put(((String)e.getKey()).toLowerCase(Locale.ROOT), ((JsonElement)e.getValue()).getAsString()));
            }
        }
        catch (Exception ex) {
            ShadowSovereign.LOGGER.error("Could not load bundled Arabic translation", (Throwable)ex);
        }
        return result;
    }

    private static Map<String, String> keys(String locale) {
        HashMap result = new HashMap();
        try (InputStream in = ModLanguage.class.getResourceAsStream("/assets/shadowsovereign/lang/" + locale + ".json");){
            if (in != null) {
                JsonParser.parseReader((Reader)new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(e -> result.put((String)e.getKey(), ((JsonElement)e.getValue()).getAsString()));
            }
        }
        catch (IOException ex) {
            ShadowSovereign.LOGGER.warn("Could not load mod language keys", (Throwable)ex);
        }
        return Map.copyOf(result);
    }

    public static String nativeText(String key, String fallback) {
        return (arabic ? AR_KEYS : EN_KEYS).getOrDefault(key, fallback);
    }

    public static boolean nativeKey(String key) {
        return EN_KEYS.containsKey(key);
    }

    public static boolean arabic() {
        return arabic;
    }

    public static void toggle() {
        arabic = !arabic;
        CACHE.clear();
        ClientPreferences.set("language", arabic ? "ar" : "en");
        ClientLocalization.installLanguage(true);
    }

    public static void setArabic(boolean value) {
        if (arabic != value) {
            ModLanguage.toggle();
        }
    }

    public static String user(String text) {
        return "\ue000" + text.replace("\ue000", "").replace("\ue001", "") + "\ue001";
    }

    public static String tr(String source) {
        if (source == null) {
            return "";
        }
        if (!arabic) {
            return source.replace("\ue000", "").replace("\ue001", "");
        }
        String cached = CACHE.get(source);
        if (cached != null) {
            return cached;
        }
        StringBuilder out = new StringBuilder();
        int cursor = 0;
        while (cursor < source.length()) {
            int start = source.indexOf(57344, cursor);
            if (start < 0) {
                out.append(ModLanguage.segment(source.substring(cursor)));
                break;
            }
            out.append(ModLanguage.segment(source.substring(cursor, start)));
            int end = source.indexOf(57345, start + 1);
            if (end < 0) {
                end = source.length();
            }
            out.append(source, start + 1, end);
            cursor = Math.min(source.length(), end + 1);
        }
        String result = out.toString();
        CACHE.put(source, result);
        return result;
    }

    private static String segment(String s) {
        String exact = AR.get(s.toLowerCase(Locale.ROOT));
        return exact != null ? exact : ModLanguage.words(s);
    }

    private static String words(String s) {
        Matcher code = CODE.matcher(s);
        ArrayList<int[]> ranges = new ArrayList<int[]>();
        while (code.find()) {
            ranges.add(new int[]{code.start(), code.end()});
        }
        Matcher m = TOKENS.matcher(s);
        StringBuffer out = new StringBuffer();
        while (m.find()) {
            boolean protectedCode = false;
            for (int[] range : ranges) {
                if (m.start() < range[0] || m.end() > range[1]) continue;
                protectedCode = true;
                break;
            }
            String replacement = protectedCode ? m.group() : AR.getOrDefault(m.group().toLowerCase(Locale.ROOT), m.group());
            m.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(out);
        return out.toString();
    }

    private static Style style(String s) {
        return s.codePoints().anyMatch(c -> c >= 1536 && c <= 1791 || c >= 64336 && c <= 65023 || c >= 65136 && c <= 65279) ? Style.f_131099_.m_131150_(ARABIC_FONT) : Style.f_131099_;
    }

    public static String visual(Font f, String s) {
        return (s = ModLanguage.tr(s)).codePoints().anyMatch(c -> c >= 1536 && c <= 1791) ? f.m_92801_(s) : s;
    }

    public static String fit(Font f, String s, int width) {
        return ModLanguage.user(f.m_92854_((FormattedText)ModLanguage.logical(s), width).getString());
    }

    public static int width(Font f, FormattedCharSequence s) {
        return f.m_92724_(s);
    }

    public static int width(Font f, String s) {
        String v = ModLanguage.visual(f, s);
        return f.m_92724_(FormattedCharSequence.m_13714_((String)v, (Style)ModLanguage.style(v)));
    }

    public static Component logical(String s) {
        String t = ModLanguage.tr(s);
        return Component.m_237113_((String)t).m_130948_(ModLanguage.style(t));
    }

    public static MutableComponent component(String s) {
        String t = ModLanguage.tr(s);
        return Component.m_237113_((String)t).m_130948_(ModLanguage.style(t));
    }

    public static MutableComponent component(Component c) {
        MutableComponent result = ModLanguage.component(c.getString());
        return result.m_130948_(result.m_7383_().m_131146_(c.m_7383_()));
    }

    public static int draw(GuiGraphics g, Font f, String s, int x, int y, int color, boolean shadow) {
        return g.m_280649_(f, FormattedCharSequence.m_13714_((String)ModLanguage.visual(f, s), (Style)ModLanguage.style(ModLanguage.tr(s))), x, y, color, shadow);
    }

    public static int draw(GuiGraphics g, Font f, Component s, int x, int y, int color, boolean shadow) {
        return g.m_280649_(f, FormattedCharSequence.m_13714_((String)ModLanguage.visual(f, s.getString()), (Style)ModLanguage.style(ModLanguage.tr(s.getString())).m_131146_(s.m_7383_())), x, y, color, shadow);
    }

    public static int draw(GuiGraphics g, Font f, FormattedCharSequence seq, int x, int y, int color, boolean shadow) {
        return g.m_280649_(f, seq, x, y, color, shadow);
    }

    public static int draw(GuiGraphics g, Font f, FormattedCharSequence s, int x, int y, int color) {
        return ModLanguage.draw(g, f, s, x, y, color, true);
    }

    public static int draw(GuiGraphics g, Font f, String s, int x, int y, int color) {
        return ModLanguage.draw(g, f, s, x, y, color, true);
    }

    public static void centered(GuiGraphics g, Font f, String s, int x, int y, int color) {
        ModLanguage.draw(g, f, s, x - ModLanguage.width(f, s) / 2, y, color, true);
    }

    public static void centered(GuiGraphics g, Font f, Component s, int x, int y, int color) {
        ModLanguage.centered(g, f, s.getString(), x, y, color);
    }

    public static void wrap(GuiGraphics g, Font f, Component text, int x, int y, int width, int color) {
        for (FormattedCharSequence line : f.m_92923_((FormattedText)ModLanguage.logical(text.getString()), width)) {
            int n;
            ModLanguage.draw(g, f, line, x, y, color, false);
            if (arabic) {
                n = 13;
            } else {
                Objects.requireNonNull(f);
                n = 9;
            }
            y += n;
        }
    }
}

