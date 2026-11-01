/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientSovereign;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.soulforge.CompanionMenu;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.soulforge.SoulTrait;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class CompanionScreen
extends AbstractContainerScreen<CompanionMenu> {
    private Button loot;
    private Button feed;

    public CompanionScreen(CompanionMenu menu, Inventory inv, Component title) {
        super((AbstractContainerMenu)menu, inv, title);
        this.f_97726_ = 360;
        this.f_97727_ = 236;
    }

    protected void m_7856_() {
        super.m_7856_();
        this.loot = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237119_(), b -> this.action(0)).m_252987_(this.f_97735_ + 198, this.f_97736_ + 177, 144, 17).m_253136_());
        this.feed = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237119_(), b -> this.action(1)).m_252987_(this.f_97735_ + 198, this.f_97736_ + 196, 144, 17).m_253136_());
        this.loot.m_257544_(Tooltip.m_257550_((Component)ModLanguage.component("Collects visible, unclaimed nearby drops into the five backpack slots. Owner must be within 16 blocks.")));
        this.feed.m_257544_(Tooltip.m_257550_((Component)ModLanguage.component("Stand within 8 blocks of your living companion. Uses one food without effects: bond and +4 HP, once per 60 seconds. Hearth increases feeding bond.")));
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)ModLanguage.component("Collect backpack"), b -> this.action(2)).m_252987_(this.f_97735_ + 198, this.f_97736_ + 215, 144, 17).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)ModLanguage.component("Arts / Runes"), b -> {
            this.f_96541_.f_91074_.m_6915_();
            ClientWarfront.open(((CompanionMenu)this.f_97732_).companionId, 1);
        }).m_252987_(this.f_97735_ + 234, this.f_97736_ + 8, 108, 17).m_253136_());
        this.updateButtons();
    }

    private void action(int id) {
        if (this.f_96541_.f_91072_ != null) {
            this.f_96541_.f_91072_.m_105208_(((CompanionMenu)this.f_97732_).f_38840_, id);
        }
    }

    private void updateButtons() {
        this.loot.m_93666_((Component)ModLanguage.component("Loot pickup: " + (((CompanionMenu)this.f_97732_).collecting() ? "ON" : "OFF")));
        this.feed.m_93666_((Component)ModLanguage.component((String)(((CompanionMenu)this.f_97732_).careSeconds() > 0 ? "Feed ready in " + ((CompanionMenu)this.f_97732_).careSeconds() + "s" : (((CompanionMenu)this.f_97732_).bond() >= 150 && !ClientSovereign.needsCare(((CompanionMenu)this.f_97732_).companionId) ? "Bond complete!" : "Feed (+" + ((CompanionMenu)this.f_97732_).feedingBond() + " bond)"))));
        this.feed.f_93623_ = ((CompanionMenu)this.f_97732_).careSeconds() == 0 && (((CompanionMenu)this.f_97732_).bond() < 150 || ClientSovereign.needsCare(((CompanionMenu)this.f_97732_).companionId));
    }

    protected void m_181908_() {
        super.m_181908_();
        this.updateButtons();
    }

    private static void border(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.m_280509_(x, y, x + w, y + 1, color);
        g.m_280509_(x, y + h - 1, x + w, y + h, color);
        g.m_280509_(x, y, x + 1, y + h, color);
        g.m_280509_(x + w - 1, y, x + w, y + h, color);
    }

    protected void m_7286_(GuiGraphics g, float partial, int mx, int my) {
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_((float)this.f_97735_, (float)this.f_97736_, 0.0f);
        MenuTheme.panel(g, this.f_97726_, this.f_97727_, 0);
        CompanionScreen.border(g, 0, 0, this.f_97726_, this.f_97727_, -8957789);
        g.m_280509_(0, 0, this.f_97726_, 2, -4355329);
        MenuTheme.art(g, 2, 198, 52, 144, 121);
        g.m_280509_(198, 52, 342, 173, -2012411880);
        g.m_280588_(this.f_97735_ + 198, this.f_97736_ + 52, this.f_97735_ + 342, this.f_97736_ + 173);
        MenuTheme.portrait(g, ((CompanionMenu)this.f_97732_).type, ((CompanionMenu)this.f_97732_).entityId, 270, 166, 32);
        g.m_280618_();
        for (int i = 0; i < 11; ++i) {
            int x = 18 + (i < 6 ? i : i - 6) * 22;
            int y = i < 6 ? 75 : 119;
            g.m_280509_(x - 1, y - 1, x + 17, y + 17, -15725539);
            CompanionScreen.border(g, x - 1, y - 1, 18, 18, i < 6 ? -7574095 : -11780504);
            if (i >= 6 || !((CompanionMenu)this.f_97732_).inventory.m_8020_(i).m_41619_()) continue;
            FantasyIcons.draw(g, i == 0 ? 1 : (i == 1 ? 2 : 10), x, y, 16);
        }
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 18 + col * 18;
                int y = row == 3 ? 211 : 153 + row * 18;
                g.m_280509_(x - 1, y - 1, x + 17, y + 17, -15462368);
                CompanionScreen.border(g, x - 1, y - 1, 18, 18, -12307374);
            }
        }
        g.m_280168_().m_85849_();
    }

    protected void m_280003_(GuiGraphics g, int mx, int my) {
        int tierColor = 0xFF000000 | (((CompanionMenu)this.f_97732_).tier.color.m_126665_() == null ? 11897087 : ((CompanionMenu)this.f_97732_).tier.color.m_126665_());
        ModLanguage.draw(g, this.f_96547_, "S O U L   F O R G E", 18, 9, -1717883, false);
        ModLanguage.draw(g, this.f_96547_, ModLanguage.fit(this.f_96547_, ModLanguage.user(((CompanionMenu)this.f_97732_).companionName), 208), 18, 23, -725761, false);
        ModLanguage.draw(g, this.f_96547_, ((CompanionMenu)this.f_97732_).tier.label + "  \u2022  " + ((CompanionMenu)this.f_97732_).tier.power + "\u00d7 health & damage", 18, 37, tierColor, false);
        ModLanguage.draw(g, this.f_96547_, "Trait: " + ((CompanionMenu)this.f_97732_).trait.label, 18, 51, -4220179, false);
        ModLanguage.draw(g, this.f_96547_, "EQUIPMENT", 18, 64, -3752485, false);
        ModLanguage.draw(g, this.f_96547_, "Bond: " + CompanionService.bondName(((CompanionMenu)this.f_97732_).bond()) + "  " + ((CompanionMenu)this.f_97732_).bond() + "/150", 18, 96, -1523572, false);
        g.m_280509_(18, 108, 172, 111, -13228481);
        g.m_280509_(18, 108, 18 + 154 * ((CompanionMenu)this.f_97732_).bond() / 150, 111, -2124873);
        ModLanguage.draw(g, this.f_96547_, "5-SLOT BACKPACK", 18, 139, -3752485, false);
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        super.m_88315_(g, mx, my, partial);
        this.m_280072_(g, mx, my);
        if (this.f_97734_ != null && this.f_97734_.f_40219_ < 6 && !this.f_97734_.m_6657_()) {
            String label = (new String[]{"Main-hand weapon", "Off-hand item", "Helmet", "Chestplate", "Leggings", "Boots"})[this.f_97734_.f_40219_];
            g.m_280557_(this.f_96547_, (Component)ModLanguage.component(label), mx, my);
        } else if (mx >= this.f_97735_ + 18 && mx < this.f_97735_ + 190 && my >= this.f_97736_ + 49 && my < this.f_97736_ + 61) {
            g.m_280666_(this.f_96547_, List.of(ModLanguage.component(((CompanionMenu)this.f_97732_).trait.label), ModLanguage.component(((CompanionMenu)this.f_97732_).trait.description)), mx, my);
        } else if (mx >= this.f_97735_ + 18 && mx < this.f_97735_ + 190 && my >= this.f_97736_ + 94 && my < this.f_97736_ + 112) {
            g.m_280666_(this.f_96547_, List.of(ModLanguage.component("Bond reduces incoming damage by " + CompanionService.bondRank(((CompanionMenu)this.f_97732_).bond()) * 5 + "%."), ModLanguage.component("Personal kills: +" + (((CompanionMenu)this.f_97732_).trait == SoulTrait.DEVOTED ? 2 : 1) + ". Feeding: +" + ((CompanionMenu)this.f_97732_).feedingBond() + " every 60s.")), mx, my);
        }
    }
}

