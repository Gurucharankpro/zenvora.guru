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
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.LegendLedger;
import dev.shadowsovereign.client.LegendWorkbench;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;

public final class LegendComparisonScreen
extends ArtisanPage {
    private int a;
    private int b;

    @Override
    protected String pageTitle() {
        return this.t("Legend comparison", "\u0645\u0642\u0627\u0631\u0646\u0629 \u0627\u0644\u0623\u0633\u0627\u0637\u064a\u0631");
    }

    public LegendComparisonScreen(Screen p, int a, int b) {
        super(p, "legend.compare", InterfaceStyle.text("Legend comparison", "\u0645\u0642\u0627\u0631\u0646\u0629 \u0627\u0644\u0623\u0633\u0627\u0637\u064a\u0631"));
        this.a = a;
        this.b = b;
    }

    @Override
    protected boolean searchable() {
        return false;
    }

    private void pair(GuiGraphics g, int icon, String en, String ar, double x, double y) {
        this.row(g, icon, this.t(en, ar), LegendLedger.n(x) + "  |  " + LegendLedger.n(y) + "  \u2022 \u0394 " + LegendLedger.n(x - y), true, null);
    }

    @Override
    protected void content(GuiGraphics g) {
        if (!LegendLedger.known(this.a) || !LegendLedger.known(this.b)) {
            this.row(g, 31, this.t("Acquired pages only", "\u0627\u0644\u0635\u0641\u062d\u0627\u062a \u0627\u0644\u0645\u0643\u062a\u0633\u0628\u0629 \u0641\u0642\u0637"), "", true, null);
            return;
        }
        CompoundTag x = LegendLedger.entry(this.a);
        CompoundTag y = LegendLedger.entry(this.b);
        this.row(g, 2, LegendLedger.name(this.a) + " | " + LegendLedger.name(this.b), this.t("Left value | right value \u2022 difference is left minus right.", "\u0627\u0644\u0642\u064a\u0645\u0629 \u0627\u0644\u064a\u0633\u0631\u0649 | \u0627\u0644\u064a\u0645\u0646\u0649 \u2022 \u0627\u0644\u0641\u0631\u0642 \u064a\u0633\u0627\u0648\u064a \u0627\u0644\u064a\u0633\u0631\u0649 \u0646\u0627\u0642\u0635 \u0627\u0644\u064a\u0645\u0646\u0649."), true, null);
        this.row(g, 30, this.t("Swap sides", "\u062a\u0628\u062f\u064a\u0644 \u0627\u0644\u062c\u0627\u0646\u0628\u064a\u0646"), "", () -> {
            int n = this.a;
            this.a = this.b;
            this.b = n;
        });
        this.pair(g, 12, "Original HP", "\u0627\u0644\u0635\u062d\u0629 \u0627\u0644\u0623\u0635\u0644\u064a\u0629", LegendLedger.hp(this.a), LegendLedger.hp(this.b));
        this.pair(g, 1, "Base attack", "\u0627\u0644\u0647\u062c\u0648\u0645 \u0627\u0644\u0623\u0633\u0627\u0633\u064a", LegendLedger.attack(this.a), LegendLedger.attack(this.b));
        this.pair(g, 2, "Base armor", "\u0627\u0644\u062f\u0631\u0639 \u0627\u0644\u0623\u0633\u0627\u0633\u064a", LegendLedger.armor(this.a), LegendLedger.armor(this.b));
        if (x.m_128471_("Owned") && y.m_128471_("Owned")) {
            this.pair(g, 12, "Owned maximum HP", "\u0623\u0642\u0635\u0649 \u0635\u062d\u0629 \u0644\u0644\u0631\u0641\u064a\u0642", x.m_128457_("MaxHealth"), y.m_128457_("MaxHealth"));
            this.pair(g, 13, "Level", "\u0627\u0644\u0645\u0633\u062a\u0648\u0649", x.m_128451_("Level"), y.m_128451_("Level"));
            this.pair(g, 1, "Confirmed kills", "\u0627\u0644\u0642\u062a\u0644\u0627\u062a \u0627\u0644\u0645\u0624\u0643\u062f\u0629", x.m_128454_("Kills"), y.m_128454_("Kills"));
            this.pair(g, 12, "Bond", "\u0627\u0644\u0631\u0627\u0628\u0637\u0629", x.m_128451_("Bond"), y.m_128451_("Bond"));
            this.pair(g, 23, "Free returns left", "\u0627\u0644\u0639\u0648\u062f\u0627\u062a \u0627\u0644\u0645\u062c\u0627\u0646\u064a\u0629 \u0627\u0644\u0645\u062a\u0628\u0642\u064a\u0629", x.m_128451_("FreeReturns"), y.m_128451_("FreeReturns"));
        } else {
            this.row(g, 31, this.t("Live comparison unavailable", "\u0627\u0644\u0645\u0642\u0627\u0631\u0646\u0629 \u0627\u0644\u062d\u064a\u0629 \u063a\u064a\u0631 \u0645\u062a\u0627\u062d\u0629"), this.t("Both souls must currently be owned for personal-stat differences.", "\u064a\u062c\u0628 \u0627\u0645\u062a\u0644\u0627\u0643 \u0627\u0644\u0631\u0648\u062d\u064a\u0646 \u062d\u0627\u0644\u064a\u064b\u0627 \u0644\u0645\u0642\u0627\u0631\u0646\u0629 \u0625\u062d\u0635\u0627\u0626\u064a\u0627\u062a\u0647\u0645\u0627 \u0627\u0644\u0634\u062e\u0635\u064a\u0629."), true, null);
        }
        this.row(g, 17, this.t("Ability sets", "\u0645\u062c\u0645\u0648\u0639\u062a\u0627 \u0627\u0644\u0642\u062f\u0631\u0627\u062a"), LegendLedger.abilities(this.a) + "\n" + LegendLedger.abilities(this.b), true, null);
        this.row(g, 28, this.t("Copy comparison", "\u0646\u0633\u062e \u0627\u0644\u0645\u0642\u0627\u0631\u0646\u0629"), "", () -> this.f_96541_.f_91068_.m_90911_(LegendLedger.sheet(this.a) + "\n---\n" + LegendLedger.sheet(this.b)));
        this.row(g, 4, this.t("Open left profile", "\u0627\u0641\u062a\u062d \u0627\u0644\u0645\u0644\u0641 \u0627\u0644\u0623\u064a\u0633\u0631"), "", () -> this.f_96541_.m_91152_((Screen)new LegendWorkbench((Screen)this, this.a, 0)));
        this.row(g, 4, this.t("Open right profile", "\u0627\u0641\u062a\u062d \u0627\u0644\u0645\u0644\u0641 \u0627\u0644\u0623\u064a\u0645\u0646"), "", () -> this.f_96541_.m_91152_((Screen)new LegendWorkbench((Screen)this, this.b, 0)));
    }
}

