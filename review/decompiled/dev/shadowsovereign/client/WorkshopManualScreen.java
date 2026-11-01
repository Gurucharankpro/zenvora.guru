/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArtisanPage;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public final class WorkshopManualScreen
extends ArtisanPage {
    private boolean tools;
    private String selected = "";
    private int family;
    private boolean favoritesOnly;
    private boolean craftableOnly;
    private boolean preview = true;
    private static final List<String> IDS = List.of("odyssey_gate", "odyssey_store", "odyssey_architect", "odyssey_garden", "odyssey_kitchen", "odyssey_workshop", "odyssey_visitors", "odyssey_club", "odyssey_bench", "odyssey_music_stand", "odyssey_notice", "odyssey_lamp", "eclipse_alchemy", "eclipse_waygate", "eclipse_vault", "eclipse_merchant", "eclipse_quartermaster", "eclipse_gallery", "soul_cemetery", "supply_cache", "soul_hearth", "soul_sanctuary", "ancient_gravestone", "astral_travelstone");
    private static final String[] EN = new String[]{"Portal controller and return anchor", "Private camp storage", "Paid architectural blueprints", "Companion garden assignments", "Camp cooking recipes", "Camp furniture crafting", "Visitor invitation and trades", "Companion club membership", "Companion rest bench", "Companion melody activity", "Guide and camp notices", "Camp lighting", "Alchemy camp service", "Waygate station and portal activation", "Private station vault", "Merchant camp service", "Companion supply service", "Display your selected companion", "Paid soul returns after five free revivals", "Army supply cache", "Companion rest and story hearth", "Sanctuary binding and adventures", "Dimension building material", "Discover and record an astral waypoint"};
    private static final String[] AR = new String[]{"\u0645\u062a\u062d\u0643\u0645 \u0627\u0644\u0628\u0648\u0627\u0628\u0629 \u0648\u0646\u0642\u0637\u0629 \u0627\u0644\u0639\u0648\u062f\u0629", "\u0645\u062e\u0632\u0646 \u0645\u0639\u0633\u0643\u0631 \u062e\u0627\u0635", "\u0645\u062e\u0637\u0637\u0627\u062a \u0628\u0646\u0627\u0621 \u0645\u062f\u0641\u0648\u0639\u0629 \u0628\u0627\u0644\u0645\u0648\u0627\u062f", "\u062a\u0643\u0644\u064a\u0641 \u0627\u0644\u0631\u0641\u0627\u0642 \u0628\u0627\u0644\u0632\u0631\u0627\u0639\u0629", "\u0648\u0635\u0641\u0627\u062a \u0645\u0637\u0628\u062e \u0627\u0644\u0645\u0639\u0633\u0643\u0631", "\u0635\u0646\u0627\u0639\u0629 \u0623\u062b\u0627\u062b \u0627\u0644\u0645\u0639\u0633\u0643\u0631", "\u062f\u0639\u0648\u0629 \u0627\u0644\u0632\u0627\u0626\u0631 \u0648\u0627\u0644\u062a\u062c\u0627\u0631\u0629", "\u0639\u0636\u0648\u064a\u0629 \u0623\u0646\u062f\u064a\u0629 \u0627\u0644\u0631\u0641\u0627\u0642", "\u0645\u0642\u0639\u062f \u0631\u0627\u062d\u0629 \u0627\u0644\u0631\u0641\u064a\u0642", "\u0646\u0634\u0627\u0637 \u0644\u062d\u0646 \u0627\u0644\u0631\u0641\u064a\u0642", "\u0627\u0644\u062f\u0644\u064a\u0644 \u0648\u0625\u0639\u0644\u0627\u0646\u0627\u062a \u0627\u0644\u0645\u0639\u0633\u0643\u0631", "\u0625\u0636\u0627\u0621\u0629 \u0627\u0644\u0645\u0639\u0633\u0643\u0631", "\u062e\u062f\u0645\u0629 \u0643\u064a\u0645\u064a\u0627\u0621 \u0627\u0644\u0645\u0639\u0633\u0643\u0631", "\u0645\u062d\u0637\u0629 \u0627\u0644\u0627\u0646\u062a\u0642\u0627\u0644 \u0648\u062a\u0641\u0639\u064a\u0644 \u0627\u0644\u0628\u0648\u0627\u0628\u0629", "\u062e\u0632\u0646\u0629 \u0627\u0644\u0645\u062d\u0637\u0629 \u0627\u0644\u062e\u0627\u0635\u0629", "\u062e\u062f\u0645\u0629 \u062a\u0627\u062c\u0631 \u0627\u0644\u0645\u0639\u0633\u0643\u0631", "\u062e\u062f\u0645\u0629 \u0645\u0624\u0646 \u0627\u0644\u0631\u0641\u0627\u0642", "\u0639\u0631\u0636 \u0627\u0644\u0631\u0641\u064a\u0642 \u0627\u0644\u0645\u062d\u062f\u062f", "\u0639\u0648\u062f\u0629 \u0627\u0644\u0623\u0631\u0648\u0627\u062d \u0627\u0644\u0645\u062f\u0641\u0648\u0639\u0629 \u0628\u0639\u062f \u062e\u0645\u0633 \u0639\u0648\u062f\u0627\u062a \u0645\u062c\u0627\u0646\u064a\u0629", "\u0645\u062e\u0632\u0646 \u0645\u0624\u0646 \u0627\u0644\u062c\u064a\u0634", "\u0645\u0648\u0642\u062f \u0631\u0627\u062d\u0629 \u0627\u0644\u0631\u0641\u0627\u0642 \u0648\u0642\u0635\u0635\u0647\u0645", "\u0631\u0628\u0637 \u0627\u0644\u0645\u0644\u0627\u0630 \u0648\u0627\u0644\u0645\u063a\u0627\u0645\u0631\u0627\u062a", "\u062e\u0627\u0645\u0629 \u0628\u0646\u0627\u0621 \u0641\u064a \u0627\u0644\u062f\u0627\u064a\u0645\u0646\u0634\u0646", "\u0627\u0643\u062a\u0634\u0627\u0641 \u0646\u0642\u0637\u0629 \u0627\u0646\u062a\u0642\u0627\u0644 \u0646\u062c\u0645\u064a\u0629 \u0648\u062a\u0633\u062c\u064a\u0644\u0647\u0627"};

    @Override
    protected String pageTitle() {
        return this.t("Workshop field manual", "\u062f\u0644\u064a\u0644 \u0627\u0644\u0648\u0631\u0634 \u0627\u0644\u0645\u064a\u062f\u0627\u0646\u064a");
    }

    public WorkshopManualScreen(Screen p) {
        super(p, "artisan.workshops", InterfaceStyle.text("Workshop field manual", "\u062f\u0644\u064a\u0644 \u0627\u0644\u0648\u0631\u0634 \u0627\u0644\u0645\u064a\u062f\u0627\u0646\u064a"));
    }

    private Item item(String id) {
        return (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("shadowsovereign", id));
    }

    private List<Recipe<?>> recipes(Item i) {
        if (this.f_96541_.f_91073_ == null) {
            return List.of();
        }
        return this.f_96541_.f_91073_.m_7465_().m_44051_().stream().filter(r -> r.m_8043_(this.f_96541_.f_91073_.m_9598_()).m_150930_(i)).toList();
    }

    private boolean favorite(String id) {
        return Boolean.parseBoolean(ClientPreferences.get("workshop.favorite." + id, "false"));
    }

    @Override
    protected void content(GuiGraphics g) {
        if (!this.selected.isBlank()) {
            this.detail(g);
            return;
        }
        this.row(g, 8, this.t("Manual filters", "\u0641\u0644\u0627\u062a\u0631 \u0627\u0644\u062f\u0644\u064a\u0644"), "", () -> {
            this.tools = !this.tools;
        });
        if (this.tools) {
            this.row(g, 9, this.t("Workshop family: ", "\u0639\u0627\u0626\u0644\u0629 \u0627\u0644\u0648\u0631\u0634: ") + this.t((new String[]{"All", "Camp", "Eclipse", "Shrines"})[this.family], (new String[]{"\u0627\u0644\u0643\u0644", "\u0627\u0644\u0645\u0639\u0633\u0643\u0631", "\u0627\u0644\u0643\u0633\u0648\u0641", "\u0627\u0644\u0623\u0636\u0631\u062d\u0629"})[this.family]), "", () -> {
                this.family = (this.family + 1) % 4;
            });
            this.row(g, 13, this.t("Favorites only", "\u0627\u0644\u0645\u0641\u0636\u0644\u0629 \u0641\u0642\u0637"), this.on(this.favoritesOnly), () -> {
                this.favoritesOnly = !this.favoritesOnly;
            });
            this.row(g, 3, this.t("With crafting recipe only", "\u0630\u0627\u062a \u0648\u0635\u0641\u0629 \u062a\u0635\u0646\u064a\u0639 \u0641\u0642\u0637"), this.on(this.craftableOnly), () -> {
                this.craftableOnly = !this.craftableOnly;
            });
        }
        int found = 0;
        for (int n = 0; n < IDS.size(); ++n) {
            String id = IDS.get(n);
            Item item = this.item(id);
            if (item == null || !this.matches(item.m_41466_().getString() + " " + id + " " + EN[n] + " " + AR[n]) || this.family == 1 && !id.startsWith("odyssey_") || this.family == 2 && !id.startsWith("eclipse_") || this.family == 3 && (id.startsWith("odyssey_") || id.startsWith("eclipse_")) || this.favoritesOnly && !this.favorite(id) || this.craftableOnly && this.recipes(item).isEmpty()) continue;
            ++found;
            this.row(g, 9, (this.favorite(id) ? "* " : "") + item.m_41466_().getString(), ModLanguage.arabic() ? AR[n] : EN[n], () -> {
                this.selected = id;
                this.scroll = 0;
            });
        }
        this.row(g, 8, this.t("Results", "\u0627\u0644\u0646\u062a\u0627\u0626\u062c"), Integer.toString(found), true, null);
    }

    private void detail(GuiGraphics g) {
        Item item = this.item(this.selected);
        int n = IDS.indexOf(this.selected);
        if (item == null || n < 0) {
            this.selected = "";
            return;
        }
        this.row(g, 30, this.t("Back to workshops", "\u0639\u0648\u062f\u0629 \u0644\u0644\u0648\u0631\u0634"), "", () -> {
            this.selected = "";
            this.scroll = 0;
        });
        this.heading(g, 9, item.m_41466_().getString());
        this.row(g, 13, this.t(this.favorite(this.selected) ? "Unfavorite workshop" : "Favorite workshop", this.favorite(this.selected) ? "\u0625\u0632\u0627\u0644\u0629 \u062a\u0641\u0636\u064a\u0644 \u0627\u0644\u0648\u0631\u0634\u0629" : "\u062a\u0641\u0636\u064a\u0644 \u0627\u0644\u0648\u0631\u0634\u0629"), "", () -> ClientPreferences.set("workshop.favorite." + this.selected, Boolean.toString(!this.favorite(this.selected))));
        this.row(g, 4, this.t("3D model preview", "\u0645\u0639\u0627\u064a\u0646\u0629 \u0627\u0644\u0645\u062c\u0633\u0645 \u0627\u0644\u062b\u0644\u0627\u062b\u064a"), this.t(this.preview ? "ON" : "OFF", this.preview ? "\u0645\u0641\u0639\u0644" : "\u0645\u0639\u0637\u0644"), () -> {
            this.preview = !this.preview;
        });
        if (this.preview) {
            int y = this.cursor;
            this.cursor += 94;
            if (y >= 72 && y + 88 <= this.bottom) {
                RoyalTheme.frame(g, 18, y, this.f_96543_ - 48, 88, -267580651);
                g.m_280168_().m_85836_();
                g.m_280168_().m_252880_((float)(this.f_96543_ / 2 - 32), (float)(y + 10), 0.0f);
                g.m_280168_().m_85841_(4.0f, 4.0f, 1.0f);
                g.m_280480_(new ItemStack((ItemLike)item), 0, 0);
                g.m_280168_().m_85849_();
            }
        }
        this.row(g, 9, this.t("Purpose", "\u0627\u0644\u0648\u0638\u064a\u0641\u0629"), ModLanguage.arabic() ? AR[n] : EN[n], true, null);
        if (this.selected.startsWith("odyssey_") || this.selected.startsWith("eclipse_")) {
            this.row(g, 8, this.t("Placement", "\u0627\u0644\u0648\u0636\u0639 \u0648\u0627\u0644\u0627\u062a\u062c\u0627\u0647"), this.t("Faces you when placed. Outline and collision follow the static model. Rotating a structure rotates this station too.", "\u062a\u0648\u0627\u062c\u0647\u0643 \u0639\u0646\u062f \u0648\u0636\u0639\u0647\u0627. \u0627\u0644\u062a\u062d\u062f\u064a\u062f \u0648\u0627\u0644\u062a\u0635\u0627\u062f\u0645 \u064a\u062a\u0628\u0639\u0627\u0646 \u0627\u0644\u0645\u062c\u0633\u0645 \u0627\u0644\u062b\u0627\u0628\u062a. \u062a\u062f\u0648\u064a\u0631 \u0627\u0644\u0627\u0633\u062a\u0631\u0627\u0643\u0634\u0631 \u064a\u062f\u064a\u0631 \u0627\u0644\u0645\u062d\u0637\u0629 \u0645\u0639\u0647."), true, null);
        }
        this.row(g, 31, this.t("Ownership & use", "\u0627\u0644\u0645\u0644\u0643\u064a\u0629 \u0648\u0627\u0644\u0627\u0633\u062a\u062e\u062f\u0627\u0645"), n == 22 ? this.t("Decorative building material. It is not an interactive camp station.", "\u062e\u0627\u0645\u0629 \u0628\u0646\u0627\u0621 \u0648\u0632\u064a\u0646\u0629\u061b \u0644\u064a\u0633\u062a \u0645\u062d\u0637\u0629 \u0645\u0639\u0633\u0643\u0631 \u062a\u0641\u0627\u0639\u0644\u064a\u0629.") : (n == 23 ? this.t("Right-click nearby in the Soulbound dimension to discover this waypoint; foreign plots remain protected.", "\u0627\u0633\u062a\u062e\u062f\u0645\u0647 \u0645\u0646 \u0642\u0631\u0628 \u062f\u0627\u062e\u0644 \u062f\u0627\u064a\u0645\u0646\u0634\u0646 \u0627\u0644\u0623\u0631\u0648\u0627\u062d \u0644\u0627\u0643\u062a\u0634\u0627\u0641 \u0646\u0642\u0637\u0629 \u0627\u0644\u0627\u0646\u062a\u0642\u0627\u0644\u061b \u062a\u0628\u0642\u0649 \u0645\u0646\u0627\u0637\u0642 \u0627\u0644\u0644\u0627\u0639\u0628\u064a\u0646 \u0627\u0644\u0622\u062e\u0631\u064a\u0646 \u0645\u062d\u0645\u064a\u0629.") : this.t("Right-click to bind/use. Private stations keep owner checks. Existing progress and station conditions still apply.", "\u0627\u0633\u062a\u062e\u062f\u0645 \u064a\u0645\u064a\u0646 \u0627\u0644\u0645\u0627\u0648\u0633 \u0644\u0644\u0631\u0628\u0637 \u0623\u0648 \u0627\u0644\u0627\u0633\u062a\u062e\u062f\u0627\u0645. \u0627\u0644\u0645\u062d\u0637\u0627\u062a \u0627\u0644\u062e\u0627\u0635\u0629 \u062a\u062a\u062d\u0642\u0642 \u0645\u0646 \u0635\u0627\u062d\u0628\u0647\u0627\u060c \u0648\u062a\u0628\u0642\u0649 \u0634\u0631\u0648\u0637 \u0627\u0644\u0645\u062d\u0637\u0629 \u0648\u0627\u0644\u062a\u0642\u062f\u0645 \u0643\u0645\u0627 \u0647\u064a.")), true, null);
        List<Recipe<?>> recipes = this.recipes(item);
        if (recipes.isEmpty()) {
            this.row(g, 3, this.t("Recipe availability", "\u062a\u0648\u0641\u0631 \u0627\u0644\u0648\u0635\u0641\u0629"), this.t("No crafting recipe is currently registered for this item. Check its original discovery/reward or Creative access.", "\u0644\u0627 \u062a\u0648\u062c\u062f \u0648\u0635\u0641\u0629 \u062a\u0635\u0646\u064a\u0639 \u0645\u0633\u062c\u0644\u0629 \u0644\u0647\u0630\u0627 \u0627\u0644\u0639\u0646\u0635\u0631 \u062d\u0627\u0644\u064a\u064b\u0627. \u0631\u0627\u062c\u0639 \u0627\u0643\u062a\u0634\u0627\u0641\u0647 \u0623\u0648 \u0645\u0643\u0627\u0641\u0623\u062a\u0647 \u0627\u0644\u0623\u0635\u0644\u064a\u0629 \u0623\u0648 \u0627\u0644\u0643\u0631\u064a\u062a\u064a\u0641."), true, null);
        } else {
            for (Recipe<?> r : recipes) {
                StringBuilder out = new StringBuilder();
                int count = 0;
                for (Ingredient ing : r.m_7527_()) {
                    ItemStack[] stacks;
                    if (ing.m_43947_() || (stacks = ing.m_43908_()).length <= 0) continue;
                    if (count++ > 0) {
                        out.append(" \u2022 ");
                    }
                    out.append(stacks[0].m_41786_().getString());
                    if (stacks.length <= 1) continue;
                    out.append(this.t(" (alternatives allowed)", " (\u062a\u064f\u0642\u0628\u0644 \u0628\u062f\u0627\u0626\u0644)"));
                }
                this.row(g, 3, this.t("Registered recipe", "\u0627\u0644\u0648\u0635\u0641\u0629 \u0627\u0644\u0645\u0633\u062c\u0644\u0629"), out.toString(), true, null);
            }
        }
        this.row(g, 28, this.t("Copy workshop summary", "\u0646\u0633\u062e \u0645\u0644\u062e\u0635 \u0627\u0644\u0648\u0631\u0634\u0629"), "", () -> this.f_96541_.f_91068_.m_90911_(item.m_41466_().getString() + "\n" + (ModLanguage.arabic() ? AR[n] : EN[n])));
        this.row(g, 28, this.t("Copy item ID", "\u0646\u0633\u062e \u0645\u0639\u0631\u0651\u0641 \u0627\u0644\u0639\u0646\u0635\u0631"), "", () -> this.f_96541_.f_91068_.m_90911_("shadowsovereign:" + this.selected));
        this.row(g, 9, this.t("Camp services", "\u062e\u062f\u0645\u0627\u062a \u0627\u0644\u0645\u0639\u0633\u0643\u0631"), "", () -> ClientOdyssey.open(3));
    }

    @Override
    protected void resetFilters() {
        this.selected = "";
        this.family = 0;
        this.craftableOnly = false;
        this.favoritesOnly = false;
    }
}

