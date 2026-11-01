/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.Item
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArtisanPage;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.expanse.ExpanseContent;
import dev.shadowsovereign.network.AtlasNetwork;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public final class AtlasScreen
extends ArtisanPage {
    private CompoundTag data;
    private final String selected;
    private boolean favoritesOnly;
    private boolean mythicsOnly;
    private final Set<String> favorites = new HashSet<String>();

    public AtlasScreen(CompoundTag data, String selected) {
        super(null, "soul-atlas", "Astral Atlas");
        this.data = data.m_6426_();
        this.selected = selected;
        String saved = ClientPreferences.get("atlas.favorites", "");
        if (!saved.isBlank()) {
            this.favorites.addAll(Arrays.asList(saved.split(",")));
        }
    }

    public static void accept(CompoundTag data, boolean open) {
        if (data == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (open) {
            if (mc.f_91074_ != null && AtlasScreen.holding()) {
                mc.m_91152_((Screen)new AtlasScreen(data, ""));
            }
        } else {
            Screen screen = mc.f_91080_;
            if (screen instanceof AtlasScreen) {
                AtlasScreen s = (AtlasScreen)screen;
                if (s.data.m_128342_("Session").equals(data.m_128342_("Session"))) {
                    s.data = data.m_6426_();
                }
            }
        }
    }

    private static boolean holding() {
        LocalPlayer p = Minecraft.m_91087_().f_91074_;
        return p != null && (p.m_21205_().m_150930_((Item)ExpanseContent.COMPASS.get()) || p.m_21206_().m_150930_((Item)ExpanseContent.COMPASS.get()));
    }

    @Override
    public void m_86600_() {
        if (!AtlasScreen.holding() || this.f_96541_.f_91074_ == null || !this.f_96541_.f_91074_.m_6084_()) {
            this.m_7379_();
        }
    }

    @Override
    protected boolean searchable() {
        return this.selected.isEmpty();
    }

    @Override
    protected String pageTitle() {
        return this.t("THE ASTRAL ATLAS", "\u0623\u0637\u0644\u0633 \u0627\u0644\u0646\u062c\u0648\u0645");
    }

    private String name(String id) {
        String key = "atlas.shadowsovereign.site." + id.substring(id.indexOf(58) + 1);
        if (I18n.m_118936_((String)key)) {
            return Component.m_237115_((String)key).getString();
        }
        return id.substring(id.indexOf(58) + 1).replace('_', ' ');
    }

    private String biome(CompoundTag site) {
        ArrayList<String> out = new ArrayList<String>();
        for (Tag v : site.m_128437_("Biomes", 8)) {
            String id = v.m_7916_();
            out.add(Component.m_237115_((String)("biome." + id.replace(':', '.'))).getString());
        }
        return String.join((CharSequence)" \u00b7 ", out);
    }

    private String msg() {
        String id;
        return this.t(switch (id = this.data.m_128461_("Message")) {
            case "dimension" -> "Travel to the selected destination\u2019s world before searching.";
            case "disabled" -> "Enable Generate Structures for this world.";
            case "cooldown" -> "The compass is recharging. Try again in a few seconds.";
            case "missing" -> "No accessible site within this search area. Travel farther and retry.";
            case "found" -> "Destination bound. Follow your compass.";
            case "cleared" -> "Destination cleared.";
            default -> "Choose a place. Let the stars show the way.";
        }, switch (id) {
            case "dimension" -> "\u0627\u0646\u062a\u0642\u0644 \u0625\u0644\u0649 \u0639\u0627\u0644\u0645 \u0627\u0644\u0648\u062c\u0647\u0629 \u0627\u0644\u0645\u062e\u062a\u0627\u0631\u0629 \u0642\u0628\u0644 \u0627\u0644\u0628\u062d\u062b.";
            case "disabled" -> "\u0641\u0639\u0651\u0644 \u062a\u0648\u0644\u064a\u062f \u0627\u0644\u0645\u0646\u0634\u0622\u062a \u0644\u0647\u0630\u0627 \u0627\u0644\u0639\u0627\u0644\u0645.";
            case "cooldown" -> "\u0627\u0644\u0628\u0648\u0635\u0644\u0629 \u062a\u0633\u062a\u0639\u064a\u062f \u0637\u0627\u0642\u062a\u0647\u0627. \u062d\u0627\u0648\u0644 \u0628\u0639\u062f \u062b\u0648\u0627\u0646\u064d.";
            case "missing" -> "\u0644\u0645 \u064a\u0648\u062c\u062f \u0645\u0648\u0642\u0639 \u0645\u062a\u0627\u062d \u0636\u0645\u0646 \u0646\u0637\u0627\u0642 \u0627\u0644\u0628\u062d\u062b. \u0627\u0628\u062a\u0639\u062f \u062b\u0645 \u062d\u0627\u0648\u0644 \u0645\u062c\u062f\u062f\u064b\u0627.";
            case "found" -> "\u062a\u0645 \u062a\u062d\u062f\u064a\u062f \u0627\u0644\u0648\u062c\u0647\u0629. \u0627\u062a\u0628\u0639 \u0627\u0644\u0628\u0648\u0635\u0644\u0629.";
            case "cleared" -> "\u062a\u0645 \u0645\u0633\u062d \u0627\u0644\u0648\u062c\u0647\u0629.";
            default -> "\u0627\u062e\u062a\u0631 \u0645\u0643\u0627\u0646\u064b\u0627\u060c \u0648\u062f\u0639 \u0627\u0644\u0646\u062c\u0648\u0645 \u062a\u0631\u0634\u062f\u0643 \u0625\u0644\u064a\u0647.";
        });
    }

    private void request(int action) {
        AtlasNetwork.request(this.data.m_128342_("Session"), action, this.selected);
    }

    private void favorite(String id) {
        if (!this.favorites.remove(id)) {
            this.favorites.add(id);
        }
        ClientPreferences.set("atlas.favorites", String.join((CharSequence)",", new TreeSet<String>(this.favorites)));
    }

    @Override
    protected void resetFilters() {
        this.favoritesOnly = false;
        this.mythicsOnly = false;
    }

    @Override
    protected void content(GuiGraphics g) {
        int y = this.cursor;
        boolean compact = this.f_96544_ < 340;
        int banner = compact ? 48 : 76;
        RoyalTheme.frame(g, 18, y, this.f_96543_ - 48, banner, -267512797);
        for (int i = 0; i < 35; ++i) {
            int x = 22 + Math.floorMod(i * 67, this.f_96543_ - 62);
            int sy = y + 5 + Math.floorMod(i * 31, banner - 10);
            g.m_280509_(x, sy, x + 1, sy + 1, i % 3 == 0 ? -3164173 : -12036754);
        }
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_(28.0f, (float)(y + (compact ? 7 : 18)), 0.0f);
        g.m_280168_().m_85841_(compact ? 2.0f : 3.0f, compact ? 2.0f : 3.0f, 1.0f);
        g.m_280480_(((Item)ExpanseContent.COMPASS.get()).m_7968_(), 0, 0);
        g.m_280168_().m_85849_();
        this.text(g, this.t("BEYOND THE VEIL", "\u0645\u0627 \u0648\u0631\u0627\u0621 \u0627\u0644\u062d\u062c\u0627\u0628"), compact ? 70 : 88, y + 12, -1981295);
        this.text(g, this.t(this.data.m_128437_("Sites", 10).size() + " places to discover", this.data.m_128437_("Sites", 10).size() + " \u0645\u0648\u0642\u0639\u064b\u0627 \u0644\u0644\u0627\u0633\u062a\u0643\u0634\u0627\u0641"), compact ? 70 : 88, y + 31, -988710);
        if (!compact) {
            this.text(g, this.t(this.data.m_128461_("CurrentDimension").equals("minecraft:overworld") ? "Overworld \u00b7 village lodge signal" : "Echo Realms \u00b7 dimension destinations", this.data.m_128461_("CurrentDimension").equals("minecraft:overworld") ? "\u0627\u0644\u0639\u0627\u0644\u0645 \u0627\u0644\u0639\u0627\u062f\u064a \u00b7 \u0625\u0634\u0627\u0631\u0629 \u0628\u064a\u062a \u0627\u0644\u0641\u0627\u0631\u0633" : "\u0639\u0627\u0644\u0645 \u0627\u0644\u0623\u0635\u062f\u0627\u0621 \u00b7 \u0648\u062c\u0647\u0627\u062a \u0627\u0644\u062f\u0627\u064a\u0645\u0646\u0634\u0646"), 88, y + 52, -6572586);
        }
        this.cursor += banner + 8;
        if (!this.selected.isEmpty() || !List.of("welcome", "cleared").contains(this.data.m_128461_("Message"))) {
            this.row(g, 3, this.t("COMPASS SIGNAL", "\u0625\u0634\u0627\u0631\u0629 \u0627\u0644\u0628\u0648\u0635\u0644\u0629"), this.msg(), true, null);
        }
        if (!this.selected.isEmpty()) {
            this.detail(g);
            return;
        }
        if (this.cursor >= 72 && this.cursor + 24 <= this.bottom) {
            int half = (this.f_96543_ - 56) / 2;
            this.button(g, 18, this.cursor, half, this.t("\u2605 Favorites: ", "\u2605 \u0627\u0644\u0645\u0641\u0636\u0644\u0629: ") + this.on(this.favoritesOnly), () -> {
                this.favoritesOnly = !this.favoritesOnly;
            });
            this.button(g, 26 + half, this.cursor, half, this.t("Legendary: ", "\u0627\u0644\u0623\u0633\u0637\u0648\u0631\u064a\u0629: ") + this.on(this.mythicsOnly), () -> {
                this.mythicsOnly = !this.mythicsOnly;
            });
        }
        this.cursor += 32;
        ArrayList<CompoundTag> sites = new ArrayList<CompoundTag>();
        for (Tag n2 : this.data.m_128437_("Sites", 10)) {
            sites.add((CompoundTag)n2);
        }
        sites.sort(Comparator.comparing(n -> this.name(n.m_128461_("Id"))));
        int count = 0;
        for (CompoundTag s : sites) {
            String id = s.m_128461_("Id");
            if (this.favoritesOnly && !this.favorites.contains(id) || this.mythicsOnly && !s.m_128471_("Mythic") || !this.matches(this.name(id) + " " + this.biome(s) + " " + id)) continue;
            ++count;
            this.row(g, s.m_128471_("Mythic") ? 6 : 3, (this.favorites.contains(id) ? "\u2605 " : "") + this.name(id), this.biome(s) + (id.equals(this.data.m_128461_("TargetId")) ? this.t(" \u2022 Tracking", " \u2022 \u0627\u0644\u0648\u062c\u0647\u0629 \u0627\u0644\u062d\u0627\u0644\u064a\u0629") : ""), () -> this.f_96541_.m_91152_((Screen)new AtlasScreen(this.data, id)));
        }
        if (count == 0) {
            this.row(g, 3, this.t("No matching destinations", "\u0644\u0627 \u062a\u0648\u062c\u062f \u0648\u062c\u0647\u0627\u062a \u0645\u0637\u0627\u0628\u0642\u0629"), this.t("Clear the search or filters.", "\u0627\u0645\u0633\u062d \u0627\u0644\u0628\u062d\u062b \u0623\u0648 \u0639\u0648\u0627\u0645\u0644 \u0627\u0644\u062a\u0635\u0641\u064a\u0629."), true, null);
        }
    }

    private void detail(GuiGraphics g) {
        CompoundTag site = null;
        for (Tag n : this.data.m_128437_("Sites", 10)) {
            if (!((CompoundTag)n).m_128461_("Id").equals(this.selected)) continue;
            site = (CompoundTag)n;
        }
        if (site == null) {
            return;
        }
        this.heading(g, site.m_128471_("Mythic") ? 6 : 3, this.name(this.selected));
        this.row(g, 4, this.t("WORLD", "\u0627\u0644\u0639\u0627\u0644\u0645"), site.m_128461_("Dimension").equals("minecraft:overworld") ? this.t("Overworld \u00b7 ordinary villages", "\u0627\u0644\u0639\u0627\u0644\u0645 \u0627\u0644\u0639\u0627\u062f\u064a \u00b7 \u0642\u0631\u0649 \u0627\u0644\u0642\u0631\u0648\u064a\u064a\u0646") : this.t("Echo Realms", "\u0639\u0627\u0644\u0645 \u0627\u0644\u0623\u0635\u062f\u0627\u0621"), true, null);
        this.row(g, 4, this.t("BIOME", "\u0627\u0644\u0628\u064a\u0648\u0645"), this.biome(site), true, null);
        this.row(g, 9, this.favorites.contains(this.selected) ? this.t("\u2605 Saved destination", "\u2605 \u0648\u062c\u0647\u0629 \u0645\u062d\u0641\u0648\u0638\u0629") : this.t("Save to favorites", "\u062d\u0641\u0638 \u0641\u064a \u0627\u0644\u0645\u0641\u0636\u0644\u0629"), this.t("Your bookmarks stay on this device.", "\u062a\u064f\u062d\u0641\u0638 \u0627\u0644\u0645\u0641\u0636\u0644\u0629 \u0639\u0644\u0649 \u0647\u0630\u0627 \u0627\u0644\u062c\u0647\u0627\u0632."), () -> this.favorite(this.selected));
        this.row(g, 3, this.t("Locate this structure", "\u062d\u062f\u062f \u0645\u0648\u0642\u0639 \u0647\u0630\u0647 \u0627\u0644\u0645\u0646\u0634\u0623\u0629"), this.t("Search nearby regions. No teleportation. Ten-second search cooldown.", "\u0627\u0628\u062d\u062b \u0641\u064a \u0627\u0644\u0645\u0646\u0627\u0637\u0642 \u0627\u0644\u0642\u0631\u064a\u0628\u0629. \u062f\u0648\u0646 \u0627\u0646\u062a\u0642\u0627\u0644 \u0641\u0648\u0631\u064a. \u0645\u0647\u0644\u0629 \u0639\u0634\u0631 \u062b\u0648\u0627\u0646\u064d \u0628\u064a\u0646 \u0639\u0645\u0644\u064a\u0627\u062a \u0627\u0644\u0628\u062d\u062b."), site.m_128461_("Dimension").equals(this.data.m_128461_("CurrentDimension")), () -> this.request(1));
        if (this.data.m_128441_("Target") && this.selected.equals(this.data.m_128461_("TargetId"))) {
            BlockPos at = BlockPos.m_122022_((long)this.data.m_128454_("Target"));
            BlockPos origin = BlockPos.m_122022_((long)this.data.m_128454_("Origin"));
            String coords = at.m_123341_() + ", " + at.m_123342_() + ", " + at.m_123343_();
            this.row(g, 3, this.t("BOUND DESTINATION", "\u0627\u0644\u0648\u062c\u0647\u0629 \u0627\u0644\u0645\u062d\u062f\u062f\u0629"), coords + " \u00b7 " + Math.round(Math.hypot(at.m_123341_() - origin.m_123341_(), at.m_123343_() - origin.m_123343_())) + this.t(" blocks away", " \u0628\u0644\u0648\u0643 \u062a\u0642\u0631\u064a\u0628\u064b\u0627"), true, null);
            this.row(g, 4, this.t("Copy coordinates", "\u0646\u0633\u062e \u0627\u0644\u0625\u062d\u062f\u0627\u062b\u064a\u0627\u062a"), coords, () -> this.f_96541_.f_91068_.m_90911_(coords));
            this.row(g, 3, this.t("Clear compass target", "\u0645\u0633\u062d \u0648\u062c\u0647\u0629 \u0627\u0644\u0628\u0648\u0635\u0644\u0629"), "", () -> this.request(2));
        }
        this.row(g, 3, this.t("Back to all destinations", "\u0627\u0644\u0639\u0648\u062f\u0629 \u0625\u0644\u0649 \u0643\u0644 \u0627\u0644\u0648\u062c\u0647\u0627\u062a"), "", () -> this.f_96541_.m_91152_((Screen)new AtlasScreen(this.data, "")));
    }
}

