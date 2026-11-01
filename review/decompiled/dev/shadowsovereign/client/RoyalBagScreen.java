/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.network.RoyalNetwork;
import dev.shadowsovereign.royal.RoyalBagMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class RoyalBagScreen
extends AbstractContainerScreen<RoyalBagMenu> {
    public RoyalBagScreen(RoyalBagMenu menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
        this.f_97726_ = 250;
        this.f_97727_ = 222;
        this.f_97731_ = 127;
    }

    protected void m_7286_(GuiGraphics g, float partial, int mx, int my) {
        int row;
        RoyalTheme.frame(g, this.f_97735_, this.f_97736_, this.f_97726_, this.f_97727_, -15133407);
        FantasyIcons.draw(g, 11, this.f_97735_ + 162, this.f_97736_ + 5, 16);
        for (row = 0; row < 4; ++row) {
            String name = row < ((RoyalBagMenu)this.f_97732_).names.size() ? ((RoyalBagMenu)this.f_97732_).names.get(row) : "";
            ModLanguage.draw(g, this.f_96547_, ModLanguage.fit(this.f_96547_, ModLanguage.user(name), 91), this.f_97735_ + 12, this.f_97736_ + 31 + row * 26, -1981295, false);
            for (int col = 0; col < 5; ++col) {
                RoyalTheme.frame(g, this.f_97735_ + 114 + col * 18, this.f_97736_ + 25 + row * 26, 18, 18, -16184558);
            }
        }
        for (row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                RoyalTheme.frame(g, this.f_97735_ + 33 + col * 18, this.f_97736_ + 138 + row * 18, 18, 18, -16184558);
            }
        }
        for (int col = 0; col < 9; ++col) {
            RoyalTheme.frame(g, this.f_97735_ + 33 + col * 18, this.f_97736_ + 196, 18, 18, -16184558);
        }
        ModLanguage.draw(g, this.f_96547_, "<", this.f_97735_ + 186, this.f_97736_ + 10, -1981295, false);
        ModLanguage.draw(g, this.f_96547_, ">", this.f_97735_ + 223, this.f_97736_ + 10, -1981295, false);
    }

    protected void m_280003_(GuiGraphics g, int x, int y) {
        ModLanguage.draw(g, this.f_96547_, ClientRoyal.t("bags", new Object[0]) + " \u00b7 " + (((RoyalBagMenu)this.f_97732_).page + 1), 10, 10, -1981295, false);
        ModLanguage.draw(g, this.f_96547_, this.f_169604_, 34, 127, -4937283, false);
    }

    public void m_88315_(GuiGraphics g, int x, int y, float partial) {
        this.m_280273_(g);
        super.m_88315_(g, x, y, partial);
        this.m_280072_(g, x, y);
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b == 0 && y >= (double)(this.f_97736_ + 4) && y < (double)(this.f_97736_ + 25) && x >= (double)(this.f_97735_ + 178) && x < (double)(this.f_97735_ + 245)) {
            int next;
            if (((RoyalBagMenu)this.f_97732_).m_142621_().m_41619_() && (next = ((RoyalBagMenu)this.f_97732_).page + (x < (double)(this.f_97735_ + 213) ? -1 : 1)) >= 0 && next < 6) {
                RoyalNetwork.request(RoyalNetwork.Action.BAGS, ((RoyalBagMenu)this.f_97732_).team, next, 0, "", List.of());
            }
            return true;
        }
        return super.m_6375_(x, y, b);
    }
}

