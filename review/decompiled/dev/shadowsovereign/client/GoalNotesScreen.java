/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.client.StyledButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class GoalNotesScreen
extends Screen {
    private final Screen previous;
    private final String id;
    private final String title;
    private final String description;
    private EditBox note;
    private String draft;

    public GoalNotesScreen(Screen parent, String id, String title, String description) {
        super((Component)Component.m_237113_((String)title));
        this.previous = parent;
        this.id = id;
        this.title = title;
        this.description = description;
        this.draft = ClientPreferences.get("quest.note." + id, "");
    }

    private String t(String en, String ar) {
        return InterfaceStyle.text(en, ar);
    }

    protected void m_7856_() {
        this.note = (EditBox)this.m_142416_((GuiEventListener)new EditBox(this.f_96547_, 18, 70, this.f_96543_ - 36, 25, (Component)ModLanguage.component(this.t("Private note", "\u0645\u0644\u0627\u062d\u0638\u0629 \u0634\u062e\u0635\u064a\u0629"))));
        this.note.m_94149_((value, index) -> FormattedCharSequence.m_13714_((String)ModLanguage.visual(this.f_96547_, ModLanguage.user(value)), (Style)ModLanguage.component(ModLanguage.user(value)).m_7383_()));
        this.note.m_94199_(512);
        this.note.m_94144_(this.draft);
        this.note.m_94151_(s -> {
            this.draft = s;
        });
        int w = (this.f_96543_ - 42) / 2;
        this.m_142416_((GuiEventListener)new StyledButton(18, this.f_96544_ - 63, w, 24, this.t("Save & return", "\u062d\u0641\u0638 \u0648\u0631\u062c\u0648\u0639"), this::m_7379_));
        this.m_142416_((GuiEventListener)new StyledButton(24 + w, this.f_96544_ - 63, w, 24, this.t("Copy objective", "\u0646\u0633\u062e \u0627\u0644\u0645\u0647\u0645\u0629"), () -> this.f_96541_.f_91068_.m_90911_(ModLanguage.tr(this.title + "\n" + this.description))));
        this.m_142416_((GuiEventListener)new StyledButton(18, this.f_96544_ - 34, w, 24, this.t("Clear note", "\u0645\u0633\u062d \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0629"), () -> this.note.m_94144_("")));
        this.m_142416_((GuiEventListener)new StyledButton(24 + w, this.f_96544_ - 34, w, 24, this.t("Copy note", "\u0646\u0633\u062e \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0629"), () -> this.f_96541_.f_91068_.m_90911_(this.draft)));
    }

    public void m_88315_(GuiGraphics g, int x, int y, float p) {
        this.m_280273_(g);
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, 0);
        ModLanguage.draw(g, this.f_96547_, ModLanguage.fit(this.f_96547_, this.title, this.f_96543_ - 36), 18, 18, -1981295, false);
        ModLanguage.draw(g, this.f_96547_, this.t("Private \u2022 saved on this device only \u2022 512 characters", "\u062e\u0627\u0635\u0629 \u2022 \u0645\u062d\u0641\u0648\u0638\u0629 \u0639\u0644\u0649 \u0647\u0630\u0627 \u0627\u0644\u062c\u0647\u0627\u0632 \u0641\u0642\u0637 \u2022 512 \u062d\u0631\u0641\u064b\u0627"), 18, 43, -4937283, false);
        int yy = 107;
        for (FormattedCharSequence line : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(ModLanguage.user(this.draft)), this.f_96543_ - 40)) {
            if (yy > this.f_96544_ - 96) break;
            ModLanguage.draw(g, this.f_96547_, line, 18, yy, -988710, false);
            yy += 13;
        }
        ModLanguage.draw(g, this.f_96547_, this.draft.codePointCount(0, this.draft.length()) + " / 512", 18, this.f_96544_ - 80, -4937283, false);
        super.m_88315_(g, x, y, p);
    }

    public void m_7861_() {
        ClientPreferences.set("quest.note." + this.id, this.draft);
        super.m_7861_();
    }

    public void m_7379_() {
        ClientPreferences.set("quest.note." + this.id, this.draft);
        this.f_96541_.m_91152_(this.previous);
    }

    public boolean m_7043_() {
        return false;
    }
}

