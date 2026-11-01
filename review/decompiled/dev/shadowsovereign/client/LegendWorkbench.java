/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.army.CompanionProgression;
import dev.shadowsovereign.client.ArtisanPage;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.ClientVigil;
import dev.shadowsovereign.client.GoalNotesScreen;
import dev.shadowsovereign.client.LegendJournalScreen;
import dev.shadowsovereign.client.LegendLedger;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.MythicGrimoireScreen;
import dev.shadowsovereign.client.WorkshopManualScreen;
import dev.shadowsovereign.mythic.MythicProfiles;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.soulforge.SoulTrait;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public final class LegendWorkbench
extends ArtisanPage {
    private final int creature;
    private int tab;
    private boolean compact;
    private boolean tools;
    private boolean sections;
    private boolean ladder;
    private static final String[] EN = new String[]{"Overview", "Combat", "Progression", "Soul returns", "Equipment", "Field notes", "Location"};
    private static final String[] AR = new String[]{"\u0646\u0638\u0631\u0629 \u0639\u0627\u0645\u0629", "\u0627\u0644\u0642\u062a\u0627\u0644", "\u0627\u0644\u062a\u0642\u062f\u0645", "\u0639\u0648\u062f\u0629 \u0627\u0644\u0631\u0648\u062d", "\u0627\u0644\u0645\u0639\u062f\u0627\u062a", "\u0645\u0644\u0627\u062d\u0638\u0627\u062a \u0627\u0644\u0645\u064a\u062f\u0627\u0646", "\u0627\u0644\u0645\u0648\u0642\u0639"};

    @Override
    protected String pageTitle() {
        return LegendLedger.name(this.creature);
    }

    public LegendWorkbench(Screen p, int c, int tab) {
        super(p, "legend.detail." + LegendLedger.id(c), LegendLedger.name(c));
        this.creature = c;
        this.tab = Math.floorMod(tab, EN.length);
        this.compact = Boolean.parseBoolean(ClientPreferences.get("legend.compact", "false"));
    }

    @Override
    protected boolean searchable() {
        return false;
    }

    @Override
    protected String footer() {
        return this.t("Live collection \u2022 no stat changes", "\u0645\u062c\u0645\u0648\u0639\u0629 \u0641\u0639\u0644\u064a\u0629 \u2022 \u0644\u0627 \u062a\u0639\u062f\u064a\u0644 \u0644\u0644\u0625\u062d\u0635\u0627\u0626\u064a\u0627\u062a");
    }

    private void line(GuiGraphics g, int icon, String en, String ar, String value) {
        this.row(g, icon, this.t(en, ar), value, true, null);
    }

    @Override
    protected void content(GuiGraphics g) {
        if (!LegendLedger.known(this.creature)) {
            this.row(g, 31, this.t("Page sealed", "\u0627\u0644\u0635\u0641\u062d\u0629 \u0645\u062e\u062a\u0648\u0645\u0629"), this.t("Defeat this guardian to unlock its actual record.", "\u0627\u0647\u0632\u0645 \u0627\u0644\u062d\u0627\u0631\u0633 \u0644\u0641\u062a\u062d \u0633\u062c\u0644\u0647 \u0627\u0644\u0641\u0639\u0644\u064a."), true, null);
            return;
        }
        CompoundTag d = LegendLedger.entry(this.creature);
        this.row(g, 3, this.t("Chapter: ", "\u0627\u0644\u0642\u0633\u0645: ") + (ModLanguage.arabic() ? AR[this.tab] : EN[this.tab]), this.t("Choose a section", "\u0627\u062e\u062a\u0631 \u0642\u0633\u0645\u064b\u0627"), () -> {
            this.sections = !this.sections;
        });
        if (this.sections) {
            for (int i = 0; i < EN.length; ++i) {
                int k = i;
                this.row(g, i == 4 ? 10 : (i == 5 ? 21 : 3), ModLanguage.arabic() ? AR[i] : EN[i], "", () -> {
                    this.tab = k;
                    this.sections = false;
                    this.scroll = 0;
                });
            }
        }
        if (!d.m_128471_("Owned") && this.tab >= 2 && this.tab <= 4) {
            this.row(g, 31, this.t("No current companion record", "\u0644\u0627 \u064a\u0648\u062c\u062f \u0633\u062c\u0644 \u0631\u0641\u064a\u0642 \u062d\u0627\u0644\u064a"), this.t("This is a historical or pending page. Companion equipment and personal progress are shown only for a soul you currently own.", "\u0647\u0630\u0647 \u0635\u0641\u062d\u0629 \u062a\u0627\u0631\u064a\u062e\u064a\u0629 \u0623\u0648 \u0645\u0639\u0644\u0642\u0629. \u0627\u0644\u0645\u0639\u062f\u0627\u062a \u0648\u0627\u0644\u062a\u0642\u062f\u0645 \u0627\u0644\u0634\u062e\u0635\u064a \u064a\u0638\u0647\u0631\u0627\u0646 \u0644\u0631\u0648\u062d \u062a\u0645\u0644\u0643\u0647\u0627 \u062d\u0627\u0644\u064a\u064b\u0627 \u0641\u0642\u0637."), true, null);
            return;
        }
        switch (this.tab) {
            case 0: {
                this.line(g, 16, "Innate trait", "\u0627\u0644\u0635\u0641\u0629 \u0627\u0644\u0623\u0635\u0644\u064a\u0629", SoulTrait.read((String)d.m_128461_((String)"Trait")).label);
                this.line(g, 4, "Legend", "\u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629", LegendLedger.lore(this.creature));
                this.line(g, 17, "Abilities", "\u0627\u0644\u0642\u062f\u0631\u0627\u062a", LegendLedger.abilities(this.creature));
                this.line(g, 16, "Combat role", "\u0627\u0644\u062f\u0648\u0631 \u0627\u0644\u0645\u064a\u062f\u0627\u0646\u064a", LegendLedger.role(this.creature).isBlank() ? this.t("Unassigned planning tag", "\u0648\u0633\u0645 \u0627\u0644\u062a\u062e\u0637\u064a\u0637 \u063a\u064a\u0631 \u0645\u062d\u062f\u062f") : ModLanguage.user(LegendLedger.roleLabel(this.creature)));
                this.line(g, 10, "Original profile", "\u0627\u0644\u0625\u062d\u0635\u0627\u0626\u064a\u0627\u062a \u0627\u0644\u0623\u0633\u0627\u0633\u064a\u0629", "HP " + LegendLedger.n(LegendLedger.hp(this.creature)) + " \u2022 ATK " + LegendLedger.n(LegendLedger.attack(this.creature)) + " \u2022 ARM " + LegendLedger.n(LegendLedger.armor(this.creature)));
                if (this.compact) break;
                this.line(g, 5, "Mobility", "\u0627\u0644\u062d\u0631\u0643\u0629", LegendLedger.mount(this.creature) ? this.t("Owner flying mount", "\u0645\u0637\u064a\u0629 \u0637\u0627\u0626\u0631\u0629 \u0644\u0635\u0627\u062d\u0628\u0647\u0627") : (this.creature > 0 && MythicProfiles.get(this.creature - 1).flies() ? this.t("Flying combatant", "\u0645\u0642\u0627\u062a\u0644 \u0637\u0627\u0626\u0631") : this.t("Ground combatant", "\u0645\u0642\u0627\u062a\u0644 \u0623\u0631\u0636\u064a")));
                this.line(g, 31, "Ownership", "\u0627\u0644\u0645\u0644\u0643\u064a\u0629", d.m_128471_("Owned") ? this.t("Owned by you", "\u0645\u0644\u0643 \u0644\u0643") : (d.m_128471_("Pending") ? this.t("Reward pending army space", "\u0627\u0644\u0645\u0643\u0627\u0641\u0623\u0629 \u062a\u0646\u062a\u0638\u0631 \u0645\u0643\u0627\u0646\u064b\u0627 \u0641\u064a \u0627\u0644\u062c\u064a\u0634") : this.t("Released; historical page only", "\u0645\u062d\u0631\u0631\u061b \u0635\u0641\u062d\u0629 \u062a\u0627\u0631\u064a\u062e\u064a\u0629 \u0641\u0642\u0637")));
                break;
            }
            case 1: {
                this.line(g, 1, "Basic attack before equipment", "\u0627\u0644\u0647\u062c\u0648\u0645 \u0627\u0644\u0623\u0633\u0627\u0633\u064a \u0642\u0628\u0644 \u0627\u0644\u0645\u0639\u062f\u0627\u062a", LegendLedger.n(LegendLedger.attack(this.creature)));
                this.line(g, 2, "Base armor", "\u0627\u0644\u062f\u0631\u0639 \u0627\u0644\u0623\u0633\u0627\u0633\u064a", LegendLedger.n(LegendLedger.armor(this.creature)));
                this.line(g, 5, "Base movement attribute", "\u0642\u064a\u0645\u0629 \u0633\u0631\u0639\u0629 \u0627\u0644\u062d\u0631\u0643\u0629 \u0627\u0644\u0623\u0633\u0627\u0633\u064a\u0629", LegendLedger.n(this.creature == 0 ? 0.24 : MythicProfiles.get(this.creature - 1).speed()));
                this.line(g, 17, "Ability guide", "\u062f\u0644\u064a\u0644 \u0627\u0644\u0642\u062f\u0631\u0627\u062a", LegendLedger.abilities(this.creature));
                if (this.creature > 0 && !this.compact) {
                    List<MythicProfiles.Move> moves = MythicProfiles.get(this.creature - 1).moves();
                    for (int i = 0; i < moves.size(); ++i) {
                        MythicProfiles.Move m = moves.get(i);
                        this.line(g, 1, this.t("Move ", "\u0627\u0644\u0647\u062c\u0645\u0629 ") + (i + 1), this.t("\u0627\u0644\u0647\u062c\u0645\u0629 ", "\u0627\u0644\u0647\u062c\u0645\u0629 ") + (i + 1), this.t("Range ", "\u0627\u0644\u0645\u062f\u0649 ") + LegendLedger.n(m.range()) + "m \u2022 " + this.t("Impact ", "\u0627\u0644\u062a\u0623\u062b\u064a\u0631 ") + LegendLedger.n(m.impact()) + "s \u2022 " + this.t("Power coefficient ", "\u0645\u0639\u0627\u0645\u0644 \u0627\u0644\u0642\u0648\u0629 ") + LegendLedger.n(m.power()) + "\u00d7");
                    }
                }
                this.line(g, 21, "Damage reading", "\u0642\u0631\u0627\u0621\u0629 \u0627\u0644\u0636\u0631\u0631", this.t("Ability coefficients describe the authored move. Gear, tier, level, armor and effects also affect real damage.", "\u0645\u0639\u0627\u0645\u0644\u0627\u062a \u0627\u0644\u0642\u062f\u0631\u0627\u062a \u062a\u0635\u0641 \u0627\u0644\u0647\u062c\u0645\u0629 \u0627\u0644\u0623\u0635\u0644\u064a\u0629. \u0627\u0644\u0645\u0639\u062f\u0627\u062a \u0648\u0627\u0644\u0646\u062f\u0631\u0629 \u0648\u0627\u0644\u0645\u0633\u062a\u0648\u0649 \u0648\u0627\u0644\u062f\u0631\u0648\u0639 \u0648\u0627\u0644\u062a\u0623\u062b\u064a\u0631\u0627\u062a \u062a\u0624\u062b\u0631 \u0623\u064a\u0636\u064b\u0627 \u0639\u0644\u0649 \u0627\u0644\u0636\u0631\u0631 \u0627\u0644\u0641\u0639\u0644\u064a."));
                break;
            }
            case 2: {
                int lv = d.m_128451_("Level");
                long kills = d.m_128454_("Kills");
                this.line(g, 13, "Current level", "\u0627\u0644\u0645\u0633\u062a\u0648\u0649 \u0627\u0644\u062d\u0627\u0644\u064a", lv + " / 10");
                this.line(g, 1, "Personal confirmed kills", "\u0627\u0644\u0642\u062a\u0644\u0627\u062a \u0627\u0644\u0634\u062e\u0635\u064a\u0629 \u0627\u0644\u0645\u0624\u0643\u062f\u0629", Long.toString(kills));
                this.line(g, 17, "Additive level power", "\u0642\u0648\u0629 \u0627\u0644\u0645\u0633\u062a\u0648\u0649 \u0627\u0644\u0625\u0636\u0627\u0641\u064a\u0629", LegendLedger.n(d.m_128459_("LevelMultiplier")) + "\u00d7");
                int next = lv >= 10 ? 225 : CompanionProgression.requiredKills(lv + 1);
                this.line(g, 20, "Kills to next level", "\u0627\u0644\u0642\u062a\u0644\u0627\u062a \u0644\u0644\u0645\u0633\u062a\u0648\u0649 \u0627\u0644\u062a\u0627\u0644\u064a", lv >= 10 ? this.t("Maximum level reached", "\u0648\u0635\u0644 \u0644\u0623\u0642\u0635\u0649 \u0645\u0633\u062a\u0648\u0649") : Long.toString(Math.max(0L, (long)next - kills)));
                this.meter(g, this.t("Level progress", "\u062a\u0642\u062f\u0645 \u0627\u0644\u0645\u0633\u062a\u0648\u0649"), lv >= 10 ? 1.0 : (double)Math.max(0L, kills - (long)CompanionProgression.requiredKills(lv)), lv >= 10 ? 1.0 : (double)(next - CompanionProgression.requiredKills(lv)));
                this.meter(g, this.t("Companion bond", "\u0631\u0627\u0628\u0637\u0629 \u0627\u0644\u0631\u0641\u064a\u0642"), d.m_128451_("Bond"), 150.0);
                this.row(g, 13, this.t("Level ladder", "\u0633\u0644\u0645 \u0627\u0644\u0645\u0633\u062a\u0648\u064a\u0627\u062a"), "", () -> {
                    this.ladder = !this.ladder;
                });
                if (!this.ladder || this.compact) break;
                for (int n = 1; n <= 10; ++n) {
                    this.line(g, 13, this.t("Level ", "\u0627\u0644\u0645\u0633\u062a\u0648\u0649 ") + n, this.t("\u0627\u0644\u0645\u0633\u062a\u0648\u0649 ", "\u0627\u0644\u0645\u0633\u062a\u0648\u0649 ") + n, CompanionProgression.requiredKills(n) + this.t(" total kills \u2022 ", " \u0642\u062a\u0644\u0629 \u0625\u062c\u0645\u0627\u0644\u064a\u0629 \u2022 ") + LegendLedger.n(CompanionProgression.multiplier(CompanionProgression.requiredKills(n))) + "\u00d7");
                }
                break;
            }
            case 3: {
                this.meter(g, this.t("Current health", "\u0627\u0644\u0635\u062d\u0629 \u0627\u0644\u062d\u0627\u0644\u064a\u0629"), d.m_128457_("CurrentHealth"), d.m_128457_("MaxHealth"));
                this.line(g, 12, "Missing health", "\u0627\u0644\u0635\u062d\u0629 \u0627\u0644\u0645\u0641\u0642\u0648\u062f\u0629", LegendLedger.n(Math.max(0.0f, d.m_128457_("MaxHealth") - d.m_128457_("CurrentHealth"))));
                this.line(g, 23, "Recorded deaths", "\u0627\u0644\u0648\u0641\u064a\u0627\u062a \u0627\u0644\u0645\u0633\u062c\u0644\u0629", Integer.toString(d.m_128451_("Deaths")));
                this.line(g, 23, "Free returns remaining", "\u0627\u0644\u0639\u0648\u062f\u0627\u062a \u0627\u0644\u0645\u062c\u0627\u0646\u064a\u0629 \u0627\u0644\u0645\u062a\u0628\u0642\u064a\u0629", Integer.toString(d.m_128451_("FreeReturns")));
                this.line(g, 20, "Respawn timer", "\u0645\u0624\u0642\u062a \u0627\u0644\u0639\u0648\u062f\u0629", (String)(d.m_128454_("RespawnTicks") < 0L ? this.t("Not reforming", "\u0644\u0627 \u064a\u0639\u064a\u062f \u0627\u0644\u062a\u0634\u0643\u0651\u0644") : (d.m_128454_("RespawnTicks") + 19L) / 20L + "s"));
                this.line(g, 16, "Cemetery payment", "\u062f\u0641\u0639 \u0627\u0644\u0645\u0642\u0628\u0631\u0629", (String)(d.m_128471_("NeedsPayment") ? d.m_128451_("ReviveFee") + this.t(" diamonds at your cemetery", " \u0623\u0644\u0645\u0627\u0633 \u0641\u064a \u0645\u0642\u0628\u0631\u062a\u0643") : this.t("No payment currently required", "\u0644\u0627 \u062a\u0648\u062c\u062f \u062a\u0643\u0644\u0641\u0629 \u0645\u0637\u0644\u0648\u0628\u0629 \u062d\u0627\u0644\u064a\u064b\u0627")));
                this.row(g, 23, this.t("Open cemetery guide", "\u0627\u0641\u062a\u062d \u062f\u0644\u064a\u0644 \u0627\u0644\u0645\u0642\u0628\u0631\u0629"), "", () -> ClientVigil.open(2));
                break;
            }
            case 4: {
                ListTag gear = d.m_128437_("Gear", 10);
                String[] en = new String[]{"Main hand", "Off hand", "Helmet", "Chestplate", "Leggings", "Boots", "Storage 1", "Storage 2", "Storage 3", "Storage 4", "Storage 5"};
                String[] ar = new String[]{"\u0627\u0644\u064a\u062f \u0627\u0644\u0631\u0626\u064a\u0633\u064a\u0629", "\u0627\u0644\u064a\u062f \u0627\u0644\u0623\u062e\u0631\u0649", "\u0627\u0644\u062e\u0648\u0630\u0629", "\u062f\u0631\u0639 \u0627\u0644\u0635\u062f\u0631", "\u062f\u0631\u0639 \u0627\u0644\u0633\u0627\u0642\u064a\u0646", "\u0627\u0644\u062d\u0630\u0627\u0621", "\u062a\u062e\u0632\u064a\u0646 1", "\u062a\u062e\u0632\u064a\u0646 2", "\u062a\u062e\u0632\u064a\u0646 3", "\u062a\u062e\u0632\u064a\u0646 4", "\u062a\u062e\u0632\u064a\u0646 5"};
                int armor = 0;
                int slots = 0;
                for (int i = 0; i < Math.min(gear.size(), 11); ++i) {
                    CompoundTag item = gear.m_128728_(i);
                    if (item.m_128441_("Item")) {
                        if (i >= 2 && i < 6) {
                            ++armor;
                        }
                        if (i >= 6) {
                            ++slots;
                        }
                    }
                    int y = this.cursor;
                    String label = item.m_128441_("Item") ? ModLanguage.user(item.m_128461_("Label")) + " \u00d7" + item.m_128451_("Count") : this.t("Empty", "\u0641\u0627\u0631\u063a");
                    this.row(g, i < 6 ? 10 : 11, ModLanguage.arabic() ? ar[i] : en[i], label, true, null);
                    if (item.m_128441_("Item") && y >= 72 && y + 24 < this.bottom) {
                        Item type;
                        ResourceLocation id = ResourceLocation.m_135820_((String)item.m_128461_("Item"));
                        Item item2 = type = id == null ? null : (Item)ForgeRegistries.ITEMS.getValue(id);
                        if (type != null) {
                            g.m_280480_(new ItemStack((ItemLike)type), this.f_96543_ - 58, y + 5);
                        }
                    }
                    if (item.m_128471_("Enchanted") && (this.compact || item.m_128451_("MaxDamage") <= 0)) {
                        this.line(g, 17, "Enchanted item", "\u063a\u0631\u0636 \u0645\u0633\u062d\u0648\u0631", this.t("Yes", "\u0646\u0639\u0645"));
                    }
                    if (item.m_128451_("MaxDamage") <= 0 || this.compact) continue;
                    this.line(g, 9, "Durability remaining", "\u0627\u0644\u0645\u062a\u0627\u0646\u0629 \u0627\u0644\u0645\u062a\u0628\u0642\u064a\u0629", Math.max(0, item.m_128451_("MaxDamage") - item.m_128451_("Damage")) + " / " + item.m_128451_("MaxDamage") + (item.m_128471_("Enchanted") ? this.t(" \u2022 Enchanted", " \u2022 \u0645\u0633\u062d\u0648\u0631") : ""));
                }
                this.line(g, 10, "Armor slots filled", "\u062e\u0627\u0646\u0627\u062a \u0627\u0644\u062f\u0631\u0639 \u0627\u0644\u0645\u0645\u0644\u0648\u0621\u0629", armor + " / 4");
                this.line(g, 11, "Storage slots used", "\u062e\u0627\u0646\u0627\u062a \u0627\u0644\u062a\u062e\u0632\u064a\u0646 \u0627\u0644\u0645\u0633\u062a\u062e\u062f\u0645\u0629", slots + " / 5");
                if (!d.m_128403_("Soul")) break;
                this.row(g, 10, this.t("Open actual equipment", "\u0627\u0641\u062a\u062d \u0627\u0644\u0645\u0639\u062f\u0627\u062a \u0627\u0644\u0641\u0639\u0644\u064a\u0629"), "", () -> ShadowNetwork.request(ShadowNetwork.Action.INVENTORY, d.m_128342_("Soul")));
                break;
            }
            case 5: {
                this.line(g, 21, "Private field note", "\u0645\u0644\u0627\u062d\u0638\u0629 \u0645\u064a\u062f\u0627\u0646\u064a\u0629 \u062e\u0627\u0635\u0629", LegendLedger.note(this.creature).isBlank() ? this.t("No note yet", "\u0644\u0627 \u062a\u0648\u062c\u062f \u0645\u0644\u0627\u062d\u0638\u0629 \u0628\u0639\u062f") : ModLanguage.user(LegendLedger.note(this.creature)));
                this.line(g, 28, "Personal alias", "\u0627\u0644\u0627\u0633\u0645 \u0627\u0644\u0634\u062e\u0635\u064a", ModLanguage.user(LegendLedger.alias(this.creature)));
                this.row(g, 28, this.t("Edit alias & planning role", "\u062a\u0639\u062f\u064a\u0644 \u0627\u0644\u0627\u0633\u0645 \u0648\u062f\u0648\u0631 \u0627\u0644\u062a\u062e\u0637\u064a\u0637"), this.t("Local presentation; does not rename the actual mob or change its AI.", "\u0639\u0631\u0636 \u0634\u062e\u0635\u064a\u061b \u0644\u0627 \u064a\u063a\u064a\u0631 \u0627\u0633\u0645 \u0627\u0644\u0643\u0627\u0626\u0646 \u0627\u0644\u0641\u0639\u0644\u064a \u0623\u0648 \u0630\u0643\u0627\u0621\u0647."), () -> this.f_96541_.m_91152_((Screen)new LegendJournalScreen(this, this.creature)));
                this.row(g, 21, this.t("Edit field note", "\u062a\u0639\u062f\u064a\u0644 \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0629 \u0627\u0644\u0645\u064a\u062f\u0627\u0646\u064a\u0629"), "", () -> this.f_96541_.m_91152_((Screen)new GoalNotesScreen(this, "legend." + LegendLedger.id(this.creature), LegendLedger.name(this.creature), LegendLedger.sheet(this.creature))));
                this.row(g, 28, this.t("Copy private note", "\u0646\u0633\u062e \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0629 \u0627\u0644\u062e\u0627\u0635\u0629"), "", () -> this.f_96541_.f_91068_.m_90911_(LegendLedger.note(this.creature)));
                break;
            }
            case 6: {
                if (d.m_128441_("Lair")) {
                    BlockPos at = BlockPos.m_122022_((long)d.m_128454_("Lair"));
                    this.line(g, 7, "Original encounter", "\u0627\u0644\u0645\u0648\u0627\u062c\u0647\u0629 \u0627\u0644\u0623\u0635\u0644\u064a\u0629", at.m_123341_() + " / " + (at.m_123342_() + 1) + " / " + at.m_123343_());
                    this.row(g, 28, this.t("Copy encounter coordinates", "\u0646\u0633\u062e \u0625\u062d\u062f\u0627\u062b\u064a\u0627\u062a \u0627\u0644\u0645\u0648\u0627\u062c\u0647\u0629"), "", () -> this.f_96541_.f_91068_.m_90911_(at.m_123341_() + " " + (at.m_123342_() + 1) + " " + at.m_123343_()));
                }
                this.line(g, 6, "Companion dimension", "\u062f\u0627\u064a\u0645\u0646\u0634\u0646 \u0627\u0644\u0631\u0641\u064a\u0642", ModLanguage.user(d.m_128461_("Dimension")));
                if (d.m_128471_("Active")) {
                    this.line(g, 8, "Active position", "\u0645\u0648\u0642\u0639 \u0627\u0644\u0631\u0641\u064a\u0642 \u0627\u0644\u0645\u0633\u062a\u062f\u0639\u0649", Math.round(d.m_128459_("X")) + " / " + Math.round(d.m_128459_("Y")) + " / " + Math.round(d.m_128459_("Z")));
                    this.row(g, 28, this.t("Copy companion coordinates", "\u0646\u0633\u062e \u0625\u062d\u062f\u0627\u062b\u064a\u0627\u062a \u0627\u0644\u0631\u0641\u064a\u0642"), "", () -> this.f_96541_.f_91068_.m_90911_(d.m_128461_("Dimension") + " " + Math.round(d.m_128459_("X")) + " " + Math.round(d.m_128459_("Y")) + " " + Math.round(d.m_128459_("Z"))));
                }
                this.line(g, 2, "Current order", "\u0627\u0644\u0623\u0645\u0631 \u0627\u0644\u062d\u0627\u0644\u064a", ModLanguage.user(d.m_128461_("Order")));
                this.line(g, 1, "Combat mode", "\u0646\u0645\u0637 \u0627\u0644\u0642\u062a\u0627\u0644", d.m_128471_("Aggressive") ? this.t("Aggressive", "\u0647\u062c\u0648\u0645\u064a") : this.t("Assist", "\u0645\u0633\u0627\u0639\u062f\u0629"));
            }
        }
        this.row(g, 9, this.t("Profile tools", "\u0623\u062f\u0648\u0627\u062a \u0627\u0644\u0645\u0644\u0641"), "", () -> {
            this.tools = !this.tools;
        });
        if (this.tools) {
            this.row(g, 13, this.t(LegendLedger.favorite(this.creature) ? "Unfavorite" : "Favorite", LegendLedger.favorite(this.creature) ? "\u0625\u0632\u0627\u0644\u0629 \u0627\u0644\u0645\u0641\u0636\u0644\u0629" : "\u0625\u0636\u0627\u0641\u0629 \u0644\u0644\u0645\u0641\u0636\u0644\u0629"), "", () -> LegendLedger.favorite(this.creature, !LegendLedger.favorite(this.creature)));
            this.row(g, 20, this.t("Compact details", "\u062a\u0641\u0627\u0635\u064a\u0644 \u0645\u062e\u062a\u0635\u0631\u0629"), this.t(this.compact ? "ON" : "OFF", this.compact ? "\u0645\u0641\u0639\u0644" : "\u0645\u0639\u0637\u0644"), () -> {
                this.compact = !this.compact;
                ClientPreferences.set("legend.compact", Boolean.toString(this.compact));
            });
            this.row(g, 3, this.t("Open illustrated page", "\u0627\u0641\u062a\u062d \u0627\u0644\u0635\u0641\u062d\u0629 \u0627\u0644\u0645\u0635\u0648\u0651\u0631\u0629"), "", () -> {
                MythicGrimoireScreen book = new MythicGrimoireScreen();
                book.selectPage(this.creature);
                this.f_96541_.m_91152_((Screen)book);
            });
            this.row(g, 28, this.t("Copy profile", "\u0646\u0633\u062e \u0645\u0644\u0641 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629"), "", () -> this.f_96541_.f_91068_.m_90911_(LegendLedger.sheet(this.creature)));
            this.row(g, 20, this.t("Refresh from server", "\u062a\u062d\u062f\u064a\u062b \u0645\u0646 \u0627\u0644\u0633\u064a\u0631\u0641\u0631"), "", () -> MythicNetwork.request(MythicNetwork.Action.VIEW));
            this.row(g, 9, this.t("Workshop manual", "\u062f\u0644\u064a\u0644 \u0627\u0644\u0648\u0631\u0634"), "", () -> this.f_96541_.m_91152_((Screen)new WorkshopManualScreen(this)));
        }
    }
}

