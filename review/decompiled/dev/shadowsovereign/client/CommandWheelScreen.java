/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.lwjgl.glfw.GLFW
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.client.ArmyScreen;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientBattle;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.network.BattleNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class CommandWheelScreen
extends Screen {
    private static final String[] LABELS = new String[]{"Follow", "Stay", "Guard", "Attack", "Shadow Step", "Taunt", "Soul Barrier", "Soul Mend"};
    private BattleRules.Squad scope = ClientBattle.state.selected();
    private float scale;
    private float left;
    private float top;
    private int highlighted = -1;
    private int ticks;
    private boolean canceled;

    public CommandWheelScreen() {
        super((Component)ModLanguage.component("Soulbound Army \u2014 Combat Wheel"));
    }

    protected void m_7856_() {
        this.scale = Math.min(1.8f, Math.min(((float)this.f_96543_ - 16.0f) / 360.0f, ((float)this.f_96544_ - 16.0f) / 330.0f));
        this.left = ((float)this.f_96543_ / this.scale - 360.0f) / 2.0f;
        this.top = ((float)this.f_96544_ / this.scale - 330.0f) / 2.0f;
    }

    public static int sector(double dx, double dy) {
        return BattleRules.wheelSector(dx, dy);
    }

    private boolean enabled(int index) {
        return index < 4 ? ClientBattle.active(this.scope) > 0L : ClientBattle.seconds(BattleRules.Skill.values()[index - 4]) == 0 && ClientBattle.eligible(BattleRules.Skill.values()[index - 4], this.scope, null) > 0L;
    }

    private void commit() {
        if (!this.canceled && this.highlighted >= 0 && this.enabled(this.highlighted)) {
            if (this.highlighted < 4) {
                ClientBattle.send((new BattleNetwork.Action[]{BattleNetwork.Action.FOLLOW, BattleNetwork.Action.STAY, BattleNetwork.Action.GUARD, BattleNetwork.Action.ATTACK})[this.highlighted], this.scope, null, 0);
            } else {
                ClientBattle.send(BattleNetwork.Action.SKILL, this.scope, null, this.highlighted - 4);
            }
        }
        this.f_96541_.m_91152_(null);
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        boolean held;
        InputConstants.Key key;
        if (++this.ticks % 20 == 0 && this.f_96541_.f_91074_ != null) {
            ShadowNetwork.request(ShadowNetwork.Action.REFRESH, null);
        }
        boolean bl = (key = ClientBattle.WHEEL.getKey()).m_84868_() == InputConstants.Type.MOUSE ? GLFW.glfwGetMouseButton((long)this.f_96541_.m_91268_().m_85439_(), (int)key.m_84873_()) == 1 : (held = InputConstants.m_84830_((long)this.f_96541_.m_91268_().m_85439_(), (int)key.m_84873_()));
        if (!held && this.ticks > 1 && this.f_96541_.f_91074_ != null) {
            this.commit();
        }
    }

    public void m_88315_(GuiGraphics g, int mouseX, int mouseY, float partial) {
        int y;
        int x;
        this.m_280273_(g);
        double mx = (float)mouseX / this.scale - this.left;
        double my = (float)mouseY / this.scale - this.top;
        this.highlighted = CommandWheelScreen.sector(mx - 180.0, my - 162.0);
        g.m_280168_().m_85836_();
        g.m_280168_().m_85841_(this.scale, this.scale, 1.0f);
        g.m_280168_().m_252880_(this.left, this.top, 0.0f);
        g.m_280024_(0, 0, 360, 330, -316664786, -351532010);
        g.m_280509_(0, 0, 360, 2, -5010433);
        this.centered(g, "C O M B A T   C O M M A N D", 180, 12, -2638081);
        this.centered(g, "Release " + ClientBattle.WHEEL.m_90863_().getString() + " to issue / Esc cancels", 180, 28, -5069116);
        for (int angle = 0; angle < 360; angle += 4) {
            double a = Math.toRadians(angle);
            x = 180 + (int)(Math.cos(a) * 99.0);
            y = 162 + (int)(Math.sin(a) * 99.0);
            g.m_280509_(x - 1, y - 1, x + 2, y + 2, -9350269);
        }
        for (int i = 0; i < 8; ++i) {
            boolean hover;
            double angle = -1.5707963267948966 + (double)i * Math.PI / 4.0;
            x = 180 + (int)(Math.cos(angle) * 104.0) - 47;
            y = 162 + (int)(Math.sin(angle) * 104.0) - 15;
            boolean available = this.enabled(i);
            boolean bl = hover = this.highlighted == i;
            g.m_280509_(x, y, x + 94, y + 31, available ? (hover ? -8235859 : -13360315) : -14673111);
            int border = hover ? -2838785 : -10139528;
            g.m_280509_(x, y, x + 94, y + 1, border);
            g.m_280509_(x, y + 30, x + 94, y + 31, border);
            FantasyIcons.draw(g, FantasyIcons.forText(LABELS[i]), x + 3, y + 3, 14);
            this.centered(g, ModLanguage.fit(this.f_96547_, LABELS[i], 70), x + 54, y + 6, available ? -726017 : -6517591);
            Object detail = "ORDER";
            if (i >= 4) {
                BattleRules.Skill skill = BattleRules.Skill.values()[i - 4];
                int seconds = ClientBattle.seconds(skill);
                detail = seconds > 0 ? seconds + "s cooldown" : (ClientBattle.eligible(skill, this.scope, null) > 0L ? "READY" : "Lv." + skill.level + "+ needed");
            }
            this.centered(g, (String)detail, x + 47, y + 19, available ? -1653119 : -8097137);
        }
        this.centered(g, this.scope.label.toUpperCase(Locale.ROOT), 180, 150, -1653119);
        this.centered(g, ClientBattle.active(this.scope) + " active", 180, 166, -4215087);
        this.centered(g, this.highlighted < 0 ? "Point to an action" : LABELS[this.highlighted], 180, 289, -1780998);
        this.centered(g, "Scroll: squad / H: army / Tab: Soul Arts", 180, 310, -5596487);
        g.m_280168_().m_85849_();
    }

    private void centered(GuiGraphics g, String s, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, s, x - ModLanguage.width(this.f_96547_, s) / 2, y, color, false);
    }

    public boolean m_6050_(double x, double y, double delta) {
        this.scope = BattleRules.Squad.values()[Math.floorMod(this.scope.ordinal() + (delta < 0.0 ? 1 : -1), BattleRules.Squad.values().length)];
        ClientBattle.send(BattleNetwork.Action.SELECT_SCOPE, this.scope, null, 0);
        return true;
    }

    public boolean m_7920_(int key, int scan, int mods) {
        if (ClientBattle.WHEEL.m_90832_(key, scan)) {
            this.commit();
            return true;
        }
        return super.m_7920_(key, scan, mods);
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (key == 258) {
            this.canceled = true;
            ClientWarfront.open(ClientWarfront.selected, 1);
            return true;
        }
        if (ClientArmy.OPEN.m_90832_(key, scan)) {
            this.canceled = true;
            this.f_96541_.m_91152_((Screen)new ArmyScreen());
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    public void m_7379_() {
        this.canceled = true;
        this.f_96541_.m_91152_(null);
    }
}

