/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArtisanPage;
import dev.shadowsovereign.client.ClientMythic;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.LegendComparisonScreen;
import dev.shadowsovereign.client.LegendLedger;
import dev.shadowsovereign.client.LegendWorkbench;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.soulforge.SoulTier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;

public final class GrimoireIndexScreen
extends ArtisanPage {
    private int filter;
    private int sort;
    private int first = -1;
    private boolean tools;
    private boolean compareMode;
    private boolean reverse;
    private boolean favoritesFirst;
    private static final String[] FILTER_EN = new String[]{"All pages", "Acquired", "Active", "Reserve", "Reforming", "Mounts", "Favorites", "Has notes", "Sealed", "Frontline", "Support", "Scout"};
    private static final String[] FILTER_AR = new String[]{"\u0643\u0644 \u0627\u0644\u0635\u0641\u062d\u0627\u062a", "\u0627\u0644\u0645\u0643\u062a\u0633\u0628", "\u0627\u0644\u0645\u0633\u062a\u062f\u0639\u0649", "\u0627\u0644\u0627\u062d\u062a\u064a\u0627\u0637", "\u0625\u0639\u0627\u062f\u0629 \u0627\u0644\u062a\u0634\u0643\u0651\u0644", "\u0627\u0644\u0645\u0637\u064a\u0627\u062a", "\u0627\u0644\u0645\u0641\u0636\u0644\u0629", "\u0644\u0647 \u0645\u0644\u0627\u062d\u0638\u0627\u062a", "\u0627\u0644\u0645\u062e\u062a\u0648\u0645", "\u0627\u0644\u062e\u0637 \u0627\u0644\u0623\u0645\u0627\u0645\u064a", "\u0627\u0644\u062f\u0639\u0645", "\u0627\u0644\u0627\u0633\u062a\u0637\u0644\u0627\u0639"};
    private static final String[] SORT_EN = new String[]{"Collection", "Name", "Level", "Kills", "Health %", "Max HP", "Base attack", "Armor", "Tier", "Bond"};
    private static final String[] SORT_AR = new String[]{"\u0627\u0644\u0645\u062c\u0645\u0648\u0639\u0629", "\u0627\u0644\u0627\u0633\u0645", "\u0627\u0644\u0645\u0633\u062a\u0648\u0649", "\u0627\u0644\u0642\u062a\u0644\u0627\u062a", "\u0646\u0633\u0628\u0629 \u0627\u0644\u0635\u062d\u0629", "\u0623\u0642\u0635\u0649 \u0635\u062d\u0629", "\u0627\u0644\u0647\u062c\u0648\u0645 \u0627\u0644\u0623\u0633\u0627\u0633\u064a", "\u0627\u0644\u062f\u0631\u0639", "\u0627\u0644\u0646\u062f\u0631\u0629", "\u0627\u0644\u0631\u0627\u0628\u0637\u0629"};

    @Override
    protected String pageTitle() {
        return this.t("Legendary Archive", "\u0623\u0631\u0634\u064a\u0641 \u0627\u0644\u0623\u0633\u0627\u0637\u064a\u0631");
    }

    public GrimoireIndexScreen(Screen parent) {
        super(parent, "artisan.index", InterfaceStyle.text("Legendary Archive", "\u0623\u0631\u0634\u064a\u0641 \u0627\u0644\u0623\u0633\u0627\u0637\u064a\u0631"));
        try {
            this.filter = Math.floorMod(Integer.parseInt(ClientPreferences.get("legend.filter", "0")), FILTER_EN.length);
            this.sort = Math.floorMod(Integer.parseInt(ClientPreferences.get("legend.sort", "0")), SORT_EN.length);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        this.reverse = Boolean.parseBoolean(ClientPreferences.get("legend.reverse", "false"));
        this.favoritesFirst = Boolean.parseBoolean(ClientPreferences.get("legend.favoritesFirst", "false"));
    }

    private boolean include(int i) {
        CompoundTag d = LegendLedger.entry(i);
        boolean known = LegendLedger.known(i);
        return switch (this.filter) {
            case 1 -> known;
            case 2 -> {
                if (known && d.m_128471_("Active")) {
                    yield true;
                }
                yield false;
            }
            case 3 -> {
                if (d.m_128471_("Owned") && !d.m_128471_("Active") && d.m_128454_("RespawnTicks") < 0L) {
                    yield true;
                }
                yield false;
            }
            case 4 -> {
                if (d.m_128471_("Owned") && d.m_128454_("RespawnTicks") >= 0L) {
                    yield true;
                }
                yield false;
            }
            case 5 -> {
                if (known && LegendLedger.mount(i)) {
                    yield true;
                }
                yield false;
            }
            case 6 -> LegendLedger.favorite(i);
            case 7 -> {
                if (known && !LegendLedger.note(i).isBlank()) {
                    yield true;
                }
                yield false;
            }
            case 8 -> {
                if (!known) {
                    yield true;
                }
                yield false;
            }
            case 9 -> {
                if (known && LegendLedger.role(i).equals("Frontline")) {
                    yield true;
                }
                yield false;
            }
            case 10 -> {
                if (known && LegendLedger.role(i).equals("Support")) {
                    yield true;
                }
                yield false;
            }
            case 11 -> {
                if (known && LegendLedger.role(i).equals("Scout")) {
                    yield true;
                }
                yield false;
            }
            default -> true;
        };
    }

    private double metric(int i) {
        CompoundTag d = LegendLedger.entry(i);
        return switch (this.sort) {
            case 2 -> d.m_128451_("Level");
            case 3 -> d.m_128454_("Kills");
            case 4 -> {
                if (d.m_128457_("MaxHealth") <= 0.0f) {
                    yield 0.0;
                }
                yield d.m_128457_("CurrentHealth") / d.m_128457_("MaxHealth");
            }
            case 5 -> {
                if (d.m_128441_("MaxHealth")) {
                    yield d.m_128457_("MaxHealth");
                }
                yield LegendLedger.hp(i);
            }
            case 6 -> LegendLedger.attack(i);
            case 7 -> LegendLedger.armor(i);
            case 8 -> SoulTier.read(d.m_128461_("Tier")).ordinal();
            case 9 -> d.m_128451_("Bond");
            default -> i;
        };
    }

    private List<Integer> filtered() {
        Comparator<Integer> order;
        ArrayList<Integer> out = new ArrayList<Integer>();
        for (int i2 = 0; i2 < LegendLedger.entries().size(); ++i2) {
            if (!this.include(i2) || !this.matches(LegendLedger.search(i2))) continue;
            out.add(i2);
        }
        Comparator<Integer> comparator = order = this.sort == 1 ? Comparator.comparing(LegendLedger::name, String.CASE_INSENSITIVE_ORDER) : Comparator.comparingDouble(this::metric);
        if (this.sort > 1) {
            order = order.reversed();
        }
        if (this.reverse) {
            order = order.reversed();
        }
        if (this.favoritesFirst) {
            order = Comparator.comparing(i -> !LegendLedger.favorite(i)).thenComparing(order);
        }
        out.sort(order.thenComparingInt(i -> i));
        return out;
    }

    @Override
    protected void content(GuiGraphics g) {
        this.row(g, 8, this.t("Catalogue tools", "\u0623\u062f\u0648\u0627\u062a \u0627\u0644\u0641\u0647\u0631\u0633"), "", () -> {
            this.tools = !this.tools;
        });
        if (this.tools) {
            this.row(g, 8, this.t("Filter: ", "\u0627\u0644\u0641\u0644\u062a\u0631: ") + (ModLanguage.arabic() ? FILTER_AR[this.filter] : FILTER_EN[this.filter]), this.t("Tap to cycle; filters use actual owned state.", "\u0627\u0636\u063a\u0637 \u0644\u0644\u062a\u0628\u062f\u064a\u0644\u061b \u0627\u0644\u0641\u0644\u0627\u062a\u0631 \u062a\u0642\u0631\u0623 \u0627\u0644\u0645\u0644\u0643\u064a\u0629 \u0627\u0644\u0641\u0639\u0644\u064a\u0629."), () -> {
                this.filter = (this.filter + 1) % FILTER_EN.length;
                ClientPreferences.set("legend.filter", Integer.toString(this.filter));
            });
            this.row(g, 20, this.t("Sort: ", "\u0627\u0644\u062a\u0631\u062a\u064a\u0628: ") + (ModLanguage.arabic() ? SORT_AR[this.sort] : SORT_EN[this.sort]), "", () -> {
                this.sort = (this.sort + 1) % SORT_EN.length;
                ClientPreferences.set("legend.sort", Integer.toString(this.sort));
            });
            this.row(g, 30, this.t("Reverse order", "\u0639\u0643\u0633 \u0627\u0644\u062a\u0631\u062a\u064a\u0628"), this.t("Current: ", "\u0627\u0644\u062d\u0627\u0644\u064a: ") + this.on(this.reverse), () -> {
                this.reverse = !this.reverse;
                ClientPreferences.set("legend.reverse", Boolean.toString(this.reverse));
            });
            this.row(g, 13, this.t("Favorites first", "\u0627\u0644\u0645\u0641\u0636\u0644\u0629 \u0623\u0648\u0644\u064b\u0627"), this.t("Current: ", "\u0627\u0644\u062d\u0627\u0644\u064a: ") + this.on(this.favoritesFirst), () -> {
                this.favoritesFirst = !this.favoritesFirst;
                ClientPreferences.set("legend.favoritesFirst", Boolean.toString(this.favoritesFirst));
            });
            this.row(g, 2, this.t("Compare selection mode", "\u0648\u0636\u0639 \u062a\u062d\u062f\u064a\u062f \u0627\u0644\u0645\u0642\u0627\u0631\u0646\u0629"), "", () -> {
                this.compareMode = !this.compareMode;
            });
        }
        List<Integer> list = this.filtered();
        long active = LegendLedger.entries().stream().filter(d -> d.m_128471_("Active")).count();
        this.row(g, 3, this.t("Archive summary", "\u0645\u0644\u062e\u0635 \u0627\u0644\u0623\u0631\u0634\u064a\u0641"), this.t("Found ", "\u0627\u0644\u0646\u062a\u0627\u0626\u062c ") + list.size() + " / " + LegendLedger.entries().size() + " \u2022 " + this.t("Acquired ", "\u0627\u0644\u0645\u0643\u062a\u0633\u0628 ") + ClientMythic.data.m_128451_("Acquired") + " \u2022 " + this.t("Active ", "\u0627\u0644\u0645\u0633\u062a\u062f\u0639\u0649 ") + active, true, null);
        if (this.first >= 0) {
            this.row(g, 2, this.t("Comparison selected: ", "\u0645\u062e\u062a\u0627\u0631 \u0644\u0644\u0645\u0642\u0627\u0631\u0646\u0629: ") + LegendLedger.name(this.first), this.t("Choose a second acquired page below.", "\u0627\u062e\u062a\u0631 \u0635\u0641\u062d\u0629 \u0645\u0643\u062a\u0633\u0628\u0629 \u062b\u0627\u0646\u064a\u0629 \u0645\u0646 \u0627\u0644\u0623\u0633\u0641\u0644."), () -> {
                this.first = -1;
            });
        }
        for (int i : list) {
            String status;
            CompoundTag d2 = LegendLedger.entry(i);
            boolean known = LegendLedger.known(i);
            String string = !known ? this.t("Sealed", "\u0645\u062e\u062a\u0648\u0645") : (d2.m_128471_("Active") ? this.t("Active", "\u0645\u0633\u062a\u062f\u0639\u0649") : (d2.m_128454_("RespawnTicks") >= 0L && d2.m_128471_("Owned") ? this.t("Reforming", "\u0625\u0639\u0627\u062f\u0629 \u062a\u0634\u0643\u064a\u0644") : (status = d2.m_128471_("Owned") ? this.t("Reserve", "\u0627\u062d\u062a\u064a\u0627\u0637") : this.t("Historical", "\u062a\u0627\u0631\u064a\u062e\u064a"))));
            String detail = status + (String)(known ? " \u2022 Lv." + d2.m_128451_("Level") + " \u2022 " + d2.m_128454_("Kills") + this.t(" kills", " \u0642\u062a\u0644\u0629") + (String)(LegendLedger.alias(i).isBlank() ? "" : " \u2022 " + ModLanguage.user(LegendLedger.alias(i))) : "");
            this.row(g, known ? (LegendLedger.mount(i) ? 5 : 4) : 31, (LegendLedger.favorite(i) ? "* " : "") + LegendLedger.name(i), detail, known, () -> this.f_96541_.m_91152_((Screen)new LegendWorkbench((Screen)this, i, 0)));
            if (!known || !this.compareMode) continue;
            int k = i;
            this.row(g, 2, this.t("Compare this legend", "\u0642\u0627\u0631\u0646 \u0647\u0630\u0647 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629"), "", () -> {
                if (this.first < 0) {
                    this.first = k;
                } else if (this.first != k) {
                    this.f_96541_.m_91152_((Screen)new LegendComparisonScreen((Screen)this, this.first, k));
                }
            });
        }
        if (list.isEmpty()) {
            this.row(g, 8, this.t("No matches", "\u0644\u0627 \u0646\u062a\u0627\u0626\u062c"), this.t("Clear the search or change filters.", "\u0627\u0645\u0633\u062d \u0627\u0644\u0628\u062d\u062b \u0623\u0648 \u063a\u064a\u0651\u0631 \u0627\u0644\u0641\u0644\u0627\u062a\u0631."), this::resetView);
        }
    }

    @Override
    protected void resetFilters() {
        this.sort = 0;
        this.filter = 0;
        this.favoritesFirst = false;
        this.reverse = false;
        ClientPreferences.setAll(Map.of("legend.filter", "0", "legend.sort", "0", "legend.reverse", "false", "legend.favoritesFirst", "false"));
    }
}

