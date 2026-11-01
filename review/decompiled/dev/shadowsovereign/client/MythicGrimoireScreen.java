/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.Level
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientMythic;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ClientVigil;
import dev.shadowsovereign.client.DevourerModel;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.GrimoireIndexScreen;
import dev.shadowsovereign.client.GrimoirePresentationScreen;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.LegendLedger;
import dev.shadowsovereign.client.LegendWorkbench;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicContent;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicProfiles;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.soulforge.SoulTier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public final class MythicGrimoireScreen
extends Screen {
    private final List<Control> controls = new ArrayList<Control>();
    private int creature;
    private int scroll;
    private int max;
    private int cursor;
    private int bottom;
    private int previewKind = -1;
    private boolean index;
    private boolean all;
    private boolean drag;
    private boolean thumb;
    private double downY;
    private int downScroll;
    private Control pending;
    private Mob preview;

    public MythicGrimoireScreen() {
        super((Component)Component.m_237115_((String)"item.shadowsovereign.mythic_grimoire"));
        try {
            this.creature = Math.max(0, Math.min(MythicExpedition.IDS.length, Integer.parseInt(ClientPreferences.get("grimoire.page", "0"))));
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
    }

    private boolean option(String key) {
        return Boolean.parseBoolean(ClientPreferences.get("grimoire." + key, "false"));
    }

    private String t(String en, String ar) {
        return InterfaceStyle.text(en, ar);
    }

    private List<CompoundTag> entries() {
        return ClientMythic.data.m_128437_("Creatures", 10).stream().map(v -> (CompoundTag)v).toList();
    }

    private List<Integer> pages() {
        ArrayList<Integer> out = new ArrayList<Integer>();
        List<CompoundTag> e = this.entries();
        for (int i = 0; i < e.size(); ++i) {
            if (!this.all && e.get(i).m_128451_("Status") != 2 || this.option("favoritePages") && !LegendLedger.favorite(i)) continue;
            out.add(i);
        }
        return out;
    }

    private CompoundTag entry() {
        List<CompoundTag> e = this.entries();
        return this.creature >= 0 && this.creature < e.size() ? e.get(this.creature) : new CompoundTag();
    }

    private String id() {
        return this.creature == 0 ? "soul_devourer" : MythicExpedition.IDS[Math.min(this.creature - 1, MythicExpedition.IDS.length - 1)];
    }

    private String name() {
        return Component.m_237115_((String)("entity.shadowsovereign." + this.id())).getString();
    }

    private String num(double n) {
        return BigDecimal.valueOf(n).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private void text(GuiGraphics g, String s, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, color, false);
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String label, boolean enabled, Runnable action) {
        RoyalTheme.frame(g, x, y, w, h, enabled ? -266069198 : -535358435);
        boolean icon = FantasyIcons.button(g, label, x, y, w, h);
        this.text(g, ModLanguage.fit(this.f_96547_, label, w - (icon ? 30 : 12)), x + (icon ? 25 : 6), y + (h - 9) / 2, enabled ? -988710 : -4937283);
        if (enabled) {
            this.controls.add(new Control(x, y, w, h, label, action));
        }
    }

    private void row(GuiGraphics g, int icon, String heading, String value) {
        List lines = this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(value), this.f_96543_ - 92);
        int h = Math.max(this.option("compact") ? 36 : 44, 28 + lines.size() * 13);
        int y = this.cursor;
        this.cursor += h + 7;
        if (y + h < 54 || y >= this.bottom) {
            return;
        }
        RoyalTheme.frame(g, 20, y, this.f_96543_ - 52, h, -266923487);
        FantasyIcons.draw(g, icon, 27, y + 7, 22);
        this.text(g, ModLanguage.fit(this.f_96547_, heading, this.f_96543_ - 100), 56, y + 8, InterfaceStyle.accent());
        int yy = y + 25;
        for (FormattedCharSequence line : lines) {
            ModLanguage.draw(g, this.f_96547_, line, 56, yy, -988710, false);
            yy += 13;
        }
    }

    private void action(GuiGraphics g, String label, boolean enabled, Runnable a) {
        int y = this.cursor;
        this.cursor += 32;
        if (y >= 54 && y + 26 <= this.bottom) {
            this.button(g, 20, y, this.f_96543_ - 52, 26, label, enabled, a);
        }
    }

    public void selectPage(int value) {
        this.creature = Math.max(0, Math.min(MythicExpedition.IDS.length, value));
        this.scroll = 0;
        this.preview = null;
        this.pending = null;
    }

    private void turn(int direction) {
        this.pending = null;
        List<Integer> p = this.pages();
        if (p.isEmpty()) {
            return;
        }
        int at = p.indexOf(this.creature);
        this.creature = p.get(Math.floorMod(Math.max(0, at) + direction, p.size()));
        this.scroll = 0;
        this.preview = null;
    }

    protected void m_7856_() {
        MythicNetwork.request(MythicNetwork.Action.VIEW);
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        if (this.preview != null) {
            ++this.preview.f_19797_;
        }
    }

    public void m_7861_() {
        ClientPreferences.set("grimoire.page", Integer.toString(this.creature));
        this.preview = null;
        super.m_7861_();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void portrait(GuiGraphics g, int y, int h, int mx, int my) {
        LivingEntityRenderer living;
        EntityModel entityModel;
        if (this.option("hidePreview") || y < 54 || y + h > this.bottom) {
            return;
        }
        if (this.preview == null || this.previewKind != this.creature) {
            this.preview = this.creature == 0 ? (Mob)((EntityType)MythicContent.DEVOURER.get()).m_20615_((Level)this.f_96541_.f_91073_) : (Mob)((EntityType)MythicContent.BEASTS.get(this.creature - 1).get()).m_20615_((Level)this.f_96541_.f_91073_);
            this.previewKind = this.creature;
        }
        if (this.preview == null) {
            return;
        }
        int size = 20;
        EntityRenderer renderer = this.f_96541_.m_91290_().m_114382_((Entity)this.preview);
        if (renderer instanceof LivingEntityRenderer && (entityModel = (living = (LivingEntityRenderer)renderer).m_7200_()) instanceof DevourerModel) {
            DevourerModel model = (DevourerModel)entityModel;
            float scale = this.creature == 0 ? 0.65f : MythicBeast.RENDER_SCALE[this.creature - 1];
            size = (int)Math.max(1.0, 0.85 * Math.min((double)((float)(h - 22) / (model.previewHeight * scale)), (double)(this.f_96543_ - 100) / ((double)(model.previewWidth * scale) * 1.25)));
        }
        g.m_280588_(22, y + 2, this.f_96543_ - 34, y + h - 2);
        try {
            int zoom;
            try {
                zoom = Math.floorMod(Integer.parseInt(ClientPreferences.get("grimoire.zoom", "1")), 3);
            }
            catch (NumberFormatException e) {
                zoom = 1;
            }
            size = Math.max(1, (int)((double)size * (new double[]{0.75, 1.0, 1.25})[zoom]));
            InventoryScreen.m_274545_((GuiGraphics)g, (int)(this.f_96543_ / 2), (int)(y + h - 10), (int)size, (float)(MenuTheme.reducedMotion || this.option("fixedPreview") ? 0.0f : (float)(this.f_96543_ / 2 - mx)), (float)(MenuTheme.reducedMotion || this.option("fixedPreview") ? 0.0f : (float)(y + h / 2 - my)), (LivingEntity)this.preview);
        }
        finally {
            g.m_280618_();
        }
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        this.controls.clear();
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, 7);
        this.bottom = this.f_96544_ - 46;
        List<Integer> p = this.pages();
        if (!p.isEmpty() && !p.contains(this.creature)) {
            this.creature = p.get(0);
            this.preview = null;
            this.scroll = 0;
        }
        FantasyIcons.draw(g, 3, 16, 10, 24);
        this.text(g, ModLanguage.fit(this.f_96547_, Component.m_237115_((String)"item.shadowsovereign.mythic_grimoire").getString(), this.f_96543_ - 160), 44, 19, -1981295);
        this.button(g, this.f_96543_ - 106, 10, 64, 25, this.t("Index", "\u0627\u0644\u0641\u0647\u0631\u0633"), true, () -> this.f_96541_.m_91152_((Screen)new GrimoireIndexScreen(this)));
        this.button(g, this.f_96543_ - 36, 10, 23, 25, "\u00d7", true, () -> ((MythicGrimoireScreen)this).m_7379_());
        g.m_280588_(12, 54, this.f_96543_ - 22, this.bottom);
        this.cursor = 59 - this.scroll;
        if (this.entries().size() != MythicExpedition.IDS.length + 1) {
            this.row(g, 20, this.t("Reading the soul ledger\u2026", "\u062c\u0627\u0631\u064d \u0642\u0631\u0627\u0621\u0629 \u0633\u062c\u0644 \u0627\u0644\u0623\u0631\u0648\u0627\u062d\u2026"), this.t("Waiting for the server's collection data.", "\u0641\u064a \u0627\u0646\u062a\u0638\u0627\u0631 \u0628\u064a\u0627\u0646\u0627\u062a \u0627\u0644\u0645\u062c\u0645\u0648\u0639\u0629 \u0645\u0646 \u0627\u0644\u0633\u064a\u0631\u0641\u0631."));
        } else if (this.index) {
            this.row(g, 3, this.t("Your living bestiary", "\u0645\u0648\u0633\u0648\u0639\u0629 \u0631\u0641\u0627\u0642\u0643 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u064a\u064a\u0646"), this.t("Collected: ", "\u0627\u0644\u0645\u062c\u0645\u0648\u0639\u0629: ") + ClientMythic.data.m_128451_("Acquired") + " / " + this.entries().size());
            this.action(g, this.t(this.all ? "Show acquired only" : "Show sealed pages too", this.all ? "\u0639\u0631\u0636 \u0627\u0644\u0645\u0643\u062a\u0633\u0628 \u0641\u0642\u0637" : "\u0639\u0631\u0636 \u0627\u0644\u0635\u0641\u062d\u0627\u062a \u0627\u0644\u0645\u062e\u062a\u0648\u0645\u0629 \u0623\u064a\u0636\u064b\u0627"), true, () -> {
                this.all = !this.all;
                this.scroll = 0;
            });
            Iterator<Integer> iterator = p.iterator();
            while (iterator.hasNext()) {
                boolean known;
                int i;
                int k = i = iterator.next().intValue();
                boolean bl = known = this.entries().get(i).m_128451_("Status") == 2;
                String label = known ? Component.m_237115_((String)("entity.shadowsovereign." + (i == 0 ? "soul_devourer" : MythicExpedition.IDS[i - 1]))).getString() : this.t("Sealed legend \u2022 ?", "\u0623\u0633\u0637\u0648\u0631\u0629 \u0645\u062e\u062a\u0648\u0645\u0629 \u2022 \u061f");
                this.action(g, label, true, () -> {
                    this.creature = k;
                    this.index = false;
                    this.scroll = 0;
                    this.preview = null;
                });
            }
        } else if (p.isEmpty() || this.entry().m_128451_("Status") != 2) {
            this.row(g, 31, this.option("favoritePages") ? this.t("No favorite pages selected", "\u0644\u0627 \u062a\u0648\u062c\u062f \u0635\u0641\u062d\u0627\u062a \u0645\u0641\u0636\u0644\u0629 \u0645\u062d\u062f\u062f\u0629") : this.t("A legend has yet to answer", "\u0644\u0645 \u062a\u0633\u062a\u062c\u0628 \u0644\u0643 \u0623\u0633\u0637\u0648\u0631\u0629 \u0628\u0639\u062f"), this.t("Defeat a mythical guardian in its encounter. Its page is added automatically when its soul is awarded. This book cannot create unearned creatures.", "\u0627\u0647\u0632\u0645 \u062d\u0627\u0631\u0633\u064b\u0627 \u0623\u0633\u0637\u0648\u0631\u064a\u064b\u0627 \u0641\u064a \u0645\u0648\u0627\u062c\u0647\u062a\u0647. \u062a\u064f\u0636\u0627\u0641 \u0635\u0641\u062d\u062a\u0647 \u062a\u0644\u0642\u0627\u0626\u064a\u064b\u0627 \u0639\u0646\u062f \u0645\u0646\u062d\u0643 \u0631\u0648\u062d\u0647. \u0627\u0644\u0643\u062a\u0627\u0628 \u0644\u0627 \u064a\u0633\u062a\u062f\u0639\u064a \u0645\u062e\u0644\u0648\u0642\u0627\u062a \u0644\u0645 \u062a\u062d\u0635\u0644 \u0639\u0644\u064a\u0647\u0627."));
            this.action(g, this.t("Browse the collection index", "\u062a\u0635\u0641\u062d \u0641\u0647\u0631\u0633 \u0627\u0644\u0645\u062c\u0645\u0648\u0639\u0629"), true, () -> this.f_96541_.m_91152_((Screen)new GrimoireIndexScreen(this)));
            this.action(g, this.t("Clear favorite-page filter", "\u0625\u0644\u063a\u0627\u0621 \u0641\u0644\u062a\u0631 \u0627\u0644\u0635\u0641\u062d\u0627\u062a \u0627\u0644\u0645\u0641\u0636\u0644\u0629"), true, () -> {
                ClientPreferences.set("grimoire.favoritePages", "false");
                this.all = false;
                this.scroll = 0;
            });
            this.action(g, this.t("Book display options", "\u062e\u064a\u0627\u0631\u0627\u062a \u0639\u0631\u0636 \u0627\u0644\u0643\u062a\u0627\u0628"), true, () -> this.f_96541_.m_91152_((Screen)new GrimoirePresentationScreen(this, this.creature)));
        } else {
            int ph;
            CompoundTag d = this.entry();
            this.row(g, 4, this.name(), this.t("BOUND LEGEND \u2022 ", "\u0623\u0633\u0637\u0648\u0631\u0629 \u0645\u0642\u064a\u0651\u062f\u0629 \u2022 ") + (p.indexOf(this.creature) + 1) + " / " + p.size());
            int panel = this.cursor;
            int n = this.option("hidePreview") ? 0 : (this.f_96544_ < 300 ? 72 : (ph = this.option("compact") ? 112 : 150));
            if (ph > 0) {
                this.cursor += ph + 9;
                RoyalTheme.frame(g, 20, panel, this.f_96543_ - 52, ph, -284487653);
                FantasyIcons.draw(g, 17, this.f_96543_ / 2 - 40, panel + ph / 2 - 40, 80);
                g.m_280509_(20, panel, this.f_96543_ - 32, panel + ph, -2012542955);
                this.portrait(g, panel, ph, mx, my);
            }
            this.action(g, this.t("Detailed profile & equipment", "\u0627\u0644\u0645\u0644\u0641 \u0627\u0644\u062a\u0641\u0635\u064a\u0644\u064a \u0648\u0627\u0644\u0645\u0639\u062f\u0627\u062a"), true, () -> this.f_96541_.m_91152_((Screen)new LegendWorkbench((Screen)this, this.creature, 0)));
            this.action(g, this.t("Book display options", "\u062e\u064a\u0627\u0631\u0627\u062a \u0639\u0631\u0636 \u0627\u0644\u0643\u062a\u0627\u0628"), true, () -> this.f_96541_.m_91152_((Screen)new GrimoirePresentationScreen(this, this.creature)));
            if (!this.option("hideLore")) {
                this.row(g, 21, this.t("Legend & combat style", "\u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629 \u0648\u0623\u0633\u0644\u0648\u0628 \u0627\u0644\u0642\u062a\u0627\u0644"), Component.m_237115_((String)("grimoire.shadowsovereign.lore." + this.id())).getString());
            }
            double hp = this.creature == 0 ? 375.0 : MythicProfiles.get(this.creature - 1).health() * 1.25;
            double damage = this.creature == 0 ? 15.0 : MythicProfiles.get(this.creature - 1).damage() * 1.25;
            double armor = this.creature == 0 ? 6.0 : MythicProfiles.get(this.creature - 1).armor();
            this.row(g, 10, this.t("Original combat profile", "\u0627\u0644\u0625\u062d\u0635\u0627\u0626\u064a\u0627\u062a \u0627\u0644\u0623\u0633\u0627\u0633\u064a\u0629"), this.t("HP ", "\u0635\u062d\u0629 ") + this.num(hp) + " \u2022 " + this.t("Base attack ", "\u0647\u062c\u0648\u0645 \u0623\u0633\u0627\u0633\u064a ") + this.num(damage) + " \u2022 " + this.t("Armor ", "\u062f\u0631\u0639 ") + this.num(armor));
            if (!this.option("hideAbilities")) {
                this.row(g, 17, this.t("Abilities", "\u0627\u0644\u0642\u062f\u0631\u0627\u062a"), Component.m_237115_((String)("grimoire.shadowsovereign.abilities." + this.id())).getString());
            }
            if (d.m_128471_("Owned")) {
                this.row(g, 12, this.t("Your companion's condition", "\u062d\u0627\u0644\u0629 \u0631\u0641\u064a\u0642\u0643"), this.num(d.m_128457_("CurrentHealth") / (float)(this.option("hearts") ? 2 : 1)) + " / " + this.num(d.m_128457_("MaxHealth") / (float)(this.option("hearts") ? 2 : 1)) + (this.option("hearts") ? this.t(" hearts \u2022 ", " \u0642\u0644\u0628 \u2022 ") : this.t(" HP \u2022 ", " \u0635\u062d\u0629 \u2022 ")) + (d.m_128471_("Active") ? this.t("Summoned", "\u0645\u0633\u062a\u062f\u0639\u0649") : this.t("In reserve", "\u0641\u064a \u0627\u0644\u0627\u062d\u062a\u064a\u0627\u0637")));
                this.row(g, 13, this.t("Progression", "\u0627\u0644\u062a\u0642\u062f\u0645"), this.t("Level ", "\u0627\u0644\u0645\u0633\u062a\u0648\u0649 ") + d.m_128451_("Level") + " \u2022 " + d.m_128454_("Kills") + this.t(" kills \u2022 ", " \u0642\u062a\u0644\u0629 \u2022 ") + this.t("Bond ", "\u0627\u0644\u0631\u0627\u0628\u0637\u0629 ") + d.m_128451_("Bond") + " / 150");
                this.row(g, 16, this.t("Soul tier", "\u0646\u062f\u0631\u0629 \u0627\u0644\u0631\u0648\u062d"), SoulTier.valueOf((String)d.m_128461_((String)"Tier")).label + " \u2022 " + this.num(SoulTier.valueOf((String)d.m_128461_((String)"Tier")).power) + "\u00d7");
                this.row(g, 16, this.t("Power & equipment", "\u0627\u0644\u0642\u0648\u0629 \u0648\u0627\u0644\u0645\u0639\u062f\u0627\u062a"), this.t("Kill-level multiplier: ", "\u0645\u0636\u0627\u0639\u0641 \u0645\u0633\u062a\u0648\u0649 \u0627\u0644\u0642\u062a\u0644: ") + this.num(d.m_128459_("LevelMultiplier")) + this.t("\u00d7. Equipment, tier and special attack modifiers are additional; damage varies by ability.", "\u00d7. \u0627\u0644\u0645\u0639\u062f\u0627\u062a \u0648\u0627\u0644\u0646\u062f\u0631\u0629 \u0648\u062a\u0639\u062f\u064a\u0644\u0627\u062a \u0627\u0644\u0647\u062c\u0645\u0627\u062a \u0639\u0648\u0627\u0645\u0644 \u0625\u0636\u0627\u0641\u064a\u0629\u061b \u0627\u0644\u0636\u0631\u0631 \u064a\u062e\u062a\u0644\u0641 \u062d\u0633\u0628 \u0627\u0644\u0642\u062f\u0631\u0629."));
                boolean active = d.m_128471_("Active");
                boolean waiting = d.m_128454_("RespawnTicks") >= 0L;
                this.row(g, 23, this.t("Soul returns", "\u0639\u0648\u062f\u0629 \u0627\u0644\u0631\u0648\u062d"), this.t("Free returns left: ", "\u0627\u0644\u0639\u0648\u062f\u0627\u062a \u0627\u0644\u0645\u062c\u0627\u0646\u064a\u0629 \u0627\u0644\u0645\u062a\u0628\u0642\u064a\u0629: ") + d.m_128451_("FreeReturns") + (String)(waiting ? " \u2022 " + (d.m_128454_("RespawnTicks") + 19L) / 20L + this.t("s remaining", " \u062b\u0627\u0646\u064a\u0629 \u0645\u062a\u0628\u0642\u064a\u0629") : "") + (String)(d.m_128471_("NeedsPayment") ? " \u2022 " + d.m_128451_("ReviveFee") + this.t(" diamonds at your cemetery", " \u0623\u0644\u0645\u0627\u0633 \u0641\u064a \u0645\u0642\u0628\u0631\u062a\u0643") : ""));
                this.action(g, active ? this.t("Recall this legend", "\u0627\u0633\u062a\u0631\u062c\u0627\u0639 \u0647\u0630\u0647 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629") : this.t("Summon this legend", "\u0627\u0633\u062a\u062f\u0639\u0627\u0621 \u0647\u0630\u0647 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629"), active || !waiting, () -> MythicNetwork.request(active ? MythicNetwork.Action.RECALL : MythicNetwork.Action.SUMMON, this.creature));
                this.action(g, this.t("Follow me", "\u0627\u062a\u0628\u0639\u0646\u064a"), true, () -> MythicNetwork.request(MythicNetwork.Action.FOLLOW, this.creature));
                this.action(g, this.t("Stay here", "\u0627\u0628\u0642 \u0647\u0646\u0627"), true, () -> MythicNetwork.request(MythicNetwork.Action.STAY, this.creature));
                if (d.m_128403_("Soul")) {
                    this.action(g, this.t("Equipment & backpack", "\u0627\u0644\u0645\u0639\u062f\u0627\u062a \u0648\u0627\u0644\u062d\u0642\u064a\u0628\u0629"), true, () -> ShadowNetwork.request(ShadowNetwork.Action.INVENTORY, d.m_128342_("Soul")));
                }
                if (d.m_128471_("NeedsPayment")) {
                    this.action(g, this.t("Cemetery information", "\u0645\u0639\u0644\u0648\u0645\u0627\u062a \u0627\u0644\u0645\u0642\u0628\u0631\u0629"), true, () -> ClientVigil.open(2));
                }
            } else {
                this.row(g, 31, this.t("Recorded in history", "\u0645\u0633\u062c\u0644 \u0641\u064a \u0627\u0644\u062a\u0627\u0631\u064a\u062e"), d.m_128471_("Pending") ? this.t("Soul reward pending. Free army space, then claim it from the collection.", "\u0645\u0643\u0627\u0641\u0623\u0629 \u0627\u0644\u0631\u0648\u062d \u0645\u0639\u0644\u0642\u0629. \u0623\u0641\u0631\u063a \u0645\u0643\u0627\u0646\u064b\u0627 \u0641\u064a \u0627\u0644\u062c\u064a\u0634 \u062b\u0645 \u0627\u0633\u062a\u0644\u0645\u0647\u0627 \u0645\u0646 \u0627\u0644\u0645\u062c\u0645\u0648\u0639\u0629.") : this.t("This legend was released. Its discovery remains recorded; the book does not duplicate or re-grant it.", "\u062a\u0645 \u062a\u062d\u0631\u064a\u0631 \u0647\u0630\u0647 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629. \u0627\u0643\u062a\u0634\u0627\u0641\u0647\u0627 \u064a\u0628\u0642\u0649 \u0645\u0633\u062c\u0644\u064b\u0627\u060c \u0648\u0627\u0644\u0643\u062a\u0627\u0628 \u0644\u0627 \u064a\u0643\u0631\u0631\u0647\u0627 \u0623\u0648 \u064a\u0645\u0646\u062d\u0647\u0627 \u0645\u062c\u062f\u062f\u064b\u0627."));
            }
            if (d.m_128471_("Pending")) {
                this.action(g, this.t("Claim pending soul rewards", "\u0627\u0633\u062a\u0644\u0627\u0645 \u0645\u0643\u0627\u0641\u0622\u062a \u0627\u0644\u0623\u0631\u0648\u0627\u062d \u0627\u0644\u0645\u0639\u0644\u0642\u0629"), true, () -> MythicNetwork.request(MythicNetwork.Action.CLAIM, this.creature));
            }
            if (d.m_128441_("Lair") && !this.option("hideLocation")) {
                BlockPos at = BlockPos.m_122022_((long)d.m_128454_("Lair"));
                this.row(g, 7, this.t("Encounter location", "\u0645\u0648\u0642\u0639 \u0627\u0644\u0645\u0648\u0627\u062c\u0647\u0629"), at.m_123341_() + " / " + (at.m_123342_() + 1) + " / " + at.m_123343_() + this.t(" \u2022 Soulbound dimension", " \u2022 \u062f\u0627\u064a\u0645\u0646\u0634\u0646 \u0627\u0644\u0623\u0631\u0648\u0627\u062d"));
            }
            if (this.creature > 0 && MythicProfiles.get(this.creature - 1).rideable()) {
                this.row(g, 5, this.t("Mount", "\u0627\u0644\u0645\u0637\u064a\u0629"), this.t("Owner-only flying mount; stamina and safe-landing rules still apply.", "\u0645\u0637\u064a\u0629 \u0637\u0627\u0626\u0631\u0629 \u0644\u0635\u0627\u062d\u0628\u0647\u0627\u061b \u0642\u0648\u0627\u0639\u062f \u0627\u0644\u0637\u0627\u0642\u0629 \u0648\u0627\u0644\u0647\u0628\u0648\u0637 \u0627\u0644\u0622\u0645\u0646 \u0628\u0627\u0642\u064a\u0629."));
            }
        }
        g.m_280618_();
        this.max = Math.max(0, this.cursor + this.scroll - this.bottom + 4);
        this.scroll = Math.max(0, Math.min(this.max, this.scroll));
        if (this.max > 0) {
            int track = this.bottom - 54;
            int h = Math.max(22, track * track / (track + this.max));
            int y = 54 + (track - h) * this.scroll / this.max;
            g.m_280509_(this.f_96543_ - 19, 54, this.f_96543_ - 9, this.bottom, -14607826);
            g.m_280509_(this.f_96543_ - 18, y, this.f_96543_ - 10, y + h, InterfaceStyle.accent());
        }
        int w = (this.f_96543_ - 48) / 3;
        this.button(g, 16, this.f_96544_ - 36, w, 25, this.t("Previous", "\u0627\u0644\u0633\u0627\u0628\u0642"), !this.index && !p.isEmpty(), () -> this.turn(-1));
        this.button(g, 24 + w, this.f_96544_ - 36, w, 25, "English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", true, ModLanguage::toggle);
        this.button(g, 32 + 2 * w, this.f_96544_ - 36, w, 25, this.t("Next", "\u0627\u0644\u062a\u0627\u0644\u064a"), !this.index && !p.isEmpty(), () -> this.turn(1));
        super.m_88315_(g, mx, my, partial);
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        this.downY = y;
        this.downScroll = this.scroll;
        this.drag = false;
        this.thumb = x >= (double)(this.f_96543_ - 22) && y >= 54.0 && y < (double)this.bottom;
        this.pending = null;
        if (this.thumb) {
            this.scroll = Math.max(0, Math.min(this.max, (int)((y - 54.0) * (double)this.max / (double)Math.max(1, this.bottom - 54))));
            return true;
        }
        for (Control c : this.controls) {
            if (!c.hit(x, y)) continue;
            this.pending = c;
            break;
        }
        return true;
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (b != 0) {
            return false;
        }
        if (this.thumb) {
            this.scroll = Math.max(0, Math.min(this.max, (int)((y - 54.0) * (double)this.max / (double)Math.max(1, this.bottom - 54))));
        } else if (Math.abs(y - this.downY) > 5.0) {
            this.drag = true;
            this.scroll = Math.max(0, Math.min(this.max, this.downScroll + (int)(this.downY - y)));
        }
        return true;
    }

    public boolean m_6348_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        if (!this.drag && !this.thumb && this.pending != null && this.pending.hit(x, y)) {
            this.pending.action.run();
        }
        this.pending = null;
        this.thumb = false;
        return true;
    }

    public boolean m_6050_(double x, double y, double a) {
        this.scroll = Math.max(0, Math.min(this.max, this.scroll - (int)(a * 30.0)));
        return true;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (key == 262) {
            this.turn(1);
            return true;
        }
        if (key == 263) {
            this.turn(-1);
            return true;
        }
        if (key == 268) {
            this.scroll = 0;
            return true;
        }
        if (key == 269) {
            this.scroll = this.max;
            return true;
        }
        if (key == 267 || key == 266) {
            this.scroll = Math.max(0, Math.min(this.max, this.scroll + (key == 267 ? 1 : -1) * Math.max(30, this.bottom - 64)));
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    private record Control(int x, int y, int w, int h, String label, Runnable action) {
        boolean hit(double a, double b) {
            return a >= (double)this.x && a < (double)(this.x + this.w) && b >= (double)this.y && b < (double)(this.y + this.h);
        }
    }
}

