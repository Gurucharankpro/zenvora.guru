/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArtisanPage;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.LegendLedger;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public final class GrimoirePresentationScreen
extends ArtisanPage {
    private final int creature;

    @Override
    protected String pageTitle() {
        return this.t("Grimoire display studio", "\u0627\u0633\u062a\u0648\u062f\u064a\u0648 \u0639\u0631\u0636 \u0627\u0644\u0643\u062a\u0627\u0628");
    }

    public GrimoirePresentationScreen(Screen p, int i) {
        super(p, "legend.presentation", InterfaceStyle.text("Grimoire display studio", "\u0627\u0633\u062a\u0648\u062f\u064a\u0648 \u0639\u0631\u0636 \u0627\u0644\u0643\u062a\u0627\u0628"));
        this.creature = i;
    }

    @Override
    protected boolean searchable() {
        return false;
    }

    private void option(GuiGraphics g, int icon, String en, String ar, String key) {
        this.row(g, icon, this.t(en, ar), this.on(Boolean.parseBoolean(ClientPreferences.get("grimoire." + key, "false"))), () -> ClientPreferences.set("grimoire." + key, Boolean.toString(!Boolean.parseBoolean(ClientPreferences.get("grimoire." + key, "false")))));
    }

    @Override
    protected void content(GuiGraphics g) {
        int zoom;
        this.option(g, 4, "Hide 3D previews", "\u0625\u062e\u0641\u0627\u0621 \u0627\u0644\u0645\u0639\u0627\u064a\u0646\u0629 \u0627\u0644\u062b\u0644\u0627\u062b\u064a\u0629", "hidePreview");
        this.option(g, 8, "Lock preview facing", "\u062a\u062b\u0628\u064a\u062a \u0627\u062a\u062c\u0627\u0647 \u0627\u0644\u0645\u0639\u0627\u064a\u0646\u0629", "fixedPreview");
        this.option(g, 12, "Show health in hearts", "\u0639\u0631\u0636 \u0627\u0644\u0635\u062d\u0629 \u0628\u0627\u0644\u0642\u0644\u0648\u0628", "hearts");
        this.option(g, 3, "Compact page layout", "\u062a\u062e\u0637\u064a\u0637 \u0635\u0641\u062d\u0629 \u0645\u062e\u062a\u0635\u0631", "compact");
        this.option(g, 21, "Hide lore paragraph", "\u0625\u062e\u0641\u0627\u0621 \u0641\u0642\u0631\u0629 \u0627\u0644\u0642\u0635\u0629", "hideLore");
        this.option(g, 17, "Hide ability paragraph", "\u0625\u062e\u0641\u0627\u0621 \u0641\u0642\u0631\u0629 \u0627\u0644\u0642\u062f\u0631\u0627\u062a", "hideAbilities");
        this.option(g, 7, "Hide encounter coordinates", "\u0625\u062e\u0641\u0627\u0621 \u0625\u062d\u062f\u0627\u062b\u064a\u0627\u062a \u0627\u0644\u0645\u0648\u0627\u062c\u0647\u0629", "hideLocation");
        this.option(g, 13, "Turn through favorites only", "\u062a\u0642\u0644\u064a\u0628 \u0627\u0644\u0645\u0641\u0636\u0644\u0629 \u0641\u0642\u0637", "favoritePages");
        try {
            zoom = Math.floorMod(Integer.parseInt(ClientPreferences.get("grimoire.zoom", "1")), 3);
        }
        catch (NumberFormatException e) {
            zoom = 1;
        }
        int next = (zoom + 1) % 3;
        this.row(g, 4, this.t("Preview zoom", "\u062a\u0643\u0628\u064a\u0631 \u0627\u0644\u0645\u0639\u0627\u064a\u0646\u0629"), (new String[]{"75%", "100%", "125%"})[zoom], () -> ClientPreferences.set("grimoire.zoom", Integer.toString(next)));
        this.row(g, 13, this.t(LegendLedger.favorite(this.creature) ? "Unfavorite this page" : "Favorite this page", LegendLedger.favorite(this.creature) ? "\u0625\u0632\u0627\u0644\u0629 \u062a\u0641\u0636\u064a\u0644 \u0627\u0644\u0635\u0641\u062d\u0629" : "\u062a\u0641\u0636\u064a\u0644 \u0627\u0644\u0635\u0641\u062d\u0629"), "", LegendLedger.known(this.creature), () -> LegendLedger.favorite(this.creature, !LegendLedger.favorite(this.creature)));
        this.row(g, 30, this.t("Reset book display", "\u0625\u0639\u0627\u062f\u0629 \u0639\u0631\u0636 \u0627\u0644\u0643\u062a\u0627\u0628"), "", () -> ClientPreferences.setAll(Map.of("grimoire.hidePreview", "false", "grimoire.fixedPreview", "false", "grimoire.hearts", "false", "grimoire.compact", "false", "grimoire.hideLore", "false", "grimoire.hideAbilities", "false", "grimoire.hideLocation", "false", "grimoire.favoritePages", "false", "grimoire.zoom", "1")));
    }
}

