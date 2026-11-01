/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ModLanguage;
import java.util.Locale;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class FantasyIcons {
    public static final ResourceLocation ATLAS = new ResourceLocation("shadowsovereign", "textures/gui/fantasy_emblems.png");

    public static void draw(GuiGraphics g, int icon, int x, int y, int size) {
        int n = Math.floorMod(icon, 32);
        g.m_280411_(ATLAS, x, y, size, size, (float)(n % 8) * 32.0f, (float)(n / 8) * 32.0f, 32, 32, 256, 128);
    }

    public static int forText(String text) {
        String s = ModLanguage.tr(text).toLowerCase(Locale.ROOT);
        String[][] words = new String[][]{{"crown", "sovereign", "\u0633\u064a\u0627\u062f\u0629", "\u0645\u0644\u0643"}, {"attack", "combat", "warfront", "battle", "\u0647\u062c\u0648\u0645", "\u0642\u062a\u0627\u0644"}, {"guard", "stay", "assist", "army", "team", "companies", "\u0627\u0628\u0642 \u0647\u0646\u0627", "protect", "\u062d\u0631\u0627\u0633", "\u062d\u0645\u0627\u064a"}, {"book", "quest", "story", "\u0643\u062a\u0627\u0628", "\u0645\u0647\u0645"}, {"mythic", "creature", "\u0623\u0633\u0637\u0648\u0631", "\u0645\u062e\u0644\u0648\u0642"}, {"flight", "ride", "\u0637\u064a\u0631\u0627\u0646", "\u0645\u0637\u064a\u0629"}, {"realm", "dimension", "\u062f\u0627\u064a\u0645\u0646\u0634\u0646", "\u0628\u0648\u0627\u0628"}, {"atlas", "explor", "adventure", "journey", "trail", "\u0623\u0637\u0644\u0633", "\u0627\u0633\u062a\u0643\u0634\u0627\u0641"}, {"find", "sort", "filter", "touch", "follow", "\u0627\u062a\u0628\u0639", "search", "\u0628\u062d\u062b", "\u0627\u0644\u0645\u062d\u062f\u062f"}, {"workshop", "build", "forge", "\u0648\u0631\u0634", "\u0628\u0646\u0627\u0621", "\u0645\u0639\u0645\u0627\u0631\u064a"}, {"equipment", "armor", "\u0645\u0639\u062f\u0627\u062a", "\u062f\u0631\u0639"}, {"bag", "supplies", "\u062d\u0642\u064a\u0628", "\u0645\u0624\u0646"}, {"health", "care", "\u0635\u062d\u0629", "\u0631\u0639\u0627\u064a\u0629"}, {"favorite", "\u0645\u0641\u0636\u0644"}, {"fire", "\u0646\u0627\u0631"}, {"garden", "crop", "\u062d\u062f\u064a\u0642", "\u0645\u062d\u0635\u0648\u0644"}, {"tier", "\u0631\u062a\u0628\u0629", "\u0646\u062f\u0631\u0629"}, {"skill", "spell", "setting", "tactic", "contrast", "\u0645\u0647\u0627\u0631", "\u062a\u0639\u0648\u064a\u0630"}, {"chain", "\u0633\u0644\u0627\u0633\u0644"}, {"bell", "\u062c\u0631\u0633"}, {"recent", "time", "motion", "reset", "\u0627\u0644\u0623\u062e\u064a\u0631\u0629", "\u0648\u0642\u062a"}, {"note", "language", "\u0645\u0644\u0627\u062d\u0638"}, {"recipe", "potion", "\u0648\u0635\u0641", "\u062c\u0631\u0639"}, {"cemetery", "reviv", "\u0645\u0642\u0628\u0631\u0629", "\u0625\u062d\u064a\u0627\u0621"}, {"key", "\u0645\u0641\u062a\u0627\u062d"}, {"moon", "\u0642\u0645\u0631"}, {"ruin", "\u0623\u0637\u0644\u0627\u0644"}, {"temple", "\u0645\u0639\u0628\u062f"}, {"copy", "edit", "\u0646\u0633\u062e", "\u062a\u0639\u062f\u064a\u0644"}, {"summon", "\u0627\u0633\u062a\u062f\u0639\u0627\u0621"}, {"recall", "return", "previous", "next", "reverse", "\u0631\u062c\u0648\u0639", "\u0627\u0633\u062a\u0631\u062c\u0627\u0639"}, {"locked", "\u0645\u0642\u0641\u0644"}};
        for (int i = 0; i < words.length; ++i) {
            for (String w : words[i]) {
                if (!s.contains(w)) continue;
                return i;
            }
        }
        return 0;
    }

    public static boolean button(GuiGraphics g, String label, int x, int y, int w, int h) {
        if (w < 72 || h < 18) {
            return false;
        }
        FantasyIcons.draw(g, FantasyIcons.forText(label), x + 4, y + (h - 16) / 2, 16);
        return true;
    }

    public static void label(GuiGraphics g, Font font, String text, int x, int y, int w, int h, int color) {
        boolean icon = FantasyIcons.button(g, text, x, y, w, h);
        String fitted = ModLanguage.fit(font, text, w - (icon ? 30 : 10));
        ModLanguage.draw(g, font, fitted, x + (icon ? 24 : 5), y + (h - 9) / 2, color, false);
    }
}

