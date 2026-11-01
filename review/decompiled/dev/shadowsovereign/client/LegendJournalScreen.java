/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArtisanPage;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.GoalNotesScreen;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.LegendLedger;
import dev.shadowsovereign.client.ModLanguage;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class LegendJournalScreen
extends ArtisanPage {
    private final int creature;
    private String alias;
    private int role;
    private EditBox aliasBox;
    private static final String[] ROLES = new String[]{"", "Frontline", "Support", "Scout", "Mount", "Guard", "Ranged"};
    private static final String[] AR = new String[]{"\u063a\u064a\u0631 \u0645\u062d\u062f\u062f", "\u0627\u0644\u062e\u0637 \u0627\u0644\u0623\u0645\u0627\u0645\u064a", "\u0627\u0644\u062f\u0639\u0645", "\u0627\u0644\u0627\u0633\u062a\u0637\u0644\u0627\u0639", "\u0645\u0637\u064a\u0629", "\u062d\u0631\u0627\u0633\u0629", "\u0628\u0639\u064a\u062f \u0627\u0644\u0645\u062f\u0649"};

    @Override
    protected String pageTitle() {
        return this.t("Personal legend labels", "\u062a\u0633\u0645\u064a\u0627\u062a \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629 \u0627\u0644\u0634\u062e\u0635\u064a\u0629");
    }

    public LegendJournalScreen(Screen p, int i) {
        super(p, "legend.journal." + LegendLedger.id(i), InterfaceStyle.text("Personal legend labels", "\u062a\u0633\u0645\u064a\u0627\u062a \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629 \u0627\u0644\u0634\u062e\u0635\u064a\u0629"));
        this.creature = i;
        this.alias = LegendLedger.alias(i);
        for (int n = 0; n < ROLES.length; ++n) {
            if (!ROLES[n].equals(LegendLedger.role(i))) continue;
            this.role = n;
        }
    }

    @Override
    protected boolean searchable() {
        return false;
    }

    @Override
    protected void m_7856_() {
        super.m_7856_();
        this.aliasBox = (EditBox)this.m_142416_((GuiEventListener)new EditBox(this.f_96547_, 18, 43, this.f_96543_ - 36, 23, (Component)ModLanguage.component(this.t("Personal alias", "\u0627\u0633\u0645 \u0634\u062e\u0635\u064a"))));
        this.aliasBox.m_257771_((Component)ModLanguage.component(this.t("Private alias\u2026", "\u0627\u0633\u0645 \u0634\u062e\u0635\u064a\u2026")));
        this.aliasBox.m_94199_(32);
        this.aliasBox.m_94144_(this.alias);
        this.aliasBox.m_94149_((s, n) -> FormattedCharSequence.m_13714_((String)ModLanguage.visual(this.f_96547_, ModLanguage.user(s)), (Style)ModLanguage.component(ModLanguage.user(s)).m_7383_()));
        this.aliasBox.m_94151_(s -> {
            this.alias = s;
        });
    }

    @Override
    protected void content(GuiGraphics g) {
        this.row(g, 28, this.t("Personal alias", "\u0627\u0644\u0627\u0633\u0645 \u0627\u0644\u0634\u062e\u0635\u064a"), this.t("32 characters. Only this device's catalogue display changes.", "32 \u062d\u0631\u0641\u064b\u0627. \u064a\u062a\u063a\u064a\u0631 \u0627\u0644\u0639\u0631\u0636 \u0641\u064a \u0623\u0631\u0634\u064a\u0641 \u0647\u0630\u0627 \u0627\u0644\u062c\u0647\u0627\u0632 \u0641\u0642\u0637."), true, null);
        this.row(g, 2, this.t("Planning role: ", "\u062f\u0648\u0631 \u0627\u0644\u062a\u062e\u0637\u064a\u0637: ") + (this.role == 0 ? this.t("Unassigned", "\u063a\u064a\u0631 \u0645\u062d\u062f\u062f") : (ModLanguage.arabic() ? AR[this.role] : ROLES[this.role])), this.t("A local label for your search filters, not a combat order.", "\u0648\u0633\u0645 \u0645\u062d\u0644\u064a \u0644\u0641\u0644\u0627\u062a\u0631 \u0627\u0644\u0628\u062d\u062b\u060c \u0648\u0644\u064a\u0633 \u0623\u0645\u0631 \u0642\u062a\u0627\u0644."), () -> {
            this.role = (this.role + 1) % ROLES.length;
        });
        this.row(g, 28, this.t("Clear alias", "\u0645\u0633\u062d \u0627\u0644\u0627\u0633\u0645 \u0627\u0644\u0634\u062e\u0635\u064a"), "", () -> {
            this.alias = "";
            this.aliasBox.m_94144_("");
        });
        this.row(g, 21, this.t("Open private note", "\u0627\u0641\u062a\u062d \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0629 \u0627\u0644\u062e\u0627\u0635\u0629"), "", () -> this.f_96541_.m_91152_((Screen)new GoalNotesScreen(this, "legend." + LegendLedger.id(this.creature), LegendLedger.name(this.creature), LegendLedger.sheet(this.creature))));
        this.row(g, 3, this.t("Save and return", "\u062d\u0641\u0638 \u0648\u0631\u062c\u0648\u0639"), "", this::m_7379_);
    }

    @Override
    public boolean m_6375_(double x, double y, int b) {
        if (y >= 43.0 && y < 66.0) {
            this.m_7522_((GuiEventListener)this.aliasBox);
            return this.aliasBox.m_6375_(x, y, b);
        }
        this.aliasBox.m_93692_(false);
        return super.m_6375_(x, y, b);
    }

    @Override
    public void m_7861_() {
        ClientPreferences.setAll(Map.of("legend.alias." + LegendLedger.id(this.creature), this.alias, "legend.role." + LegendLedger.id(this.creature), ROLES[this.role]));
        super.m_7861_();
    }
}

