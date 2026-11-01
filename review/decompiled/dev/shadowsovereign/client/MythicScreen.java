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
 *  net.minecraft.nbt.ListTag
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

import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientMythic;
import dev.shadowsovereign.client.DevourerModel;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.OdysseyScreen;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicContent;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicProfiles;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.network.MythicNetwork;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public final class MythicScreen
extends Screen {
    private final List<Control> controls = new ArrayList<Control>();
    private Mob preview;
    private int creature;
    private int scroll;
    private int max;
    private int bottom;
    private int cursor;
    private double down;
    private int start;
    private boolean drag;
    private boolean thumb;
    private boolean insideBody;
    private Control pending;

    public MythicScreen() {
        super(MythicService.text("title", new Object[0]));
    }

    private double previewScale(int panelHeight) {
        LivingEntityRenderer living;
        EntityModel entityModel;
        if (this.creature < 9) {
            return Math.max(10.0, (double)(panelHeight - 34) / 4.5) * (this.creature == 1 ? 1.7 : (this.creature == 2 ? 1.8 : (this.creature == 4 ? 1.7 : 1.0)));
        }
        EntityRenderer renderer = this.f_96541_.m_91290_().m_114382_((Entity)this.preview);
        if (renderer instanceof LivingEntityRenderer && (entityModel = (living = (LivingEntityRenderer)renderer).m_7200_()) instanceof DevourerModel) {
            DevourerModel model = (DevourerModel)entityModel;
            float scale = MythicBeast.RENDER_SCALE[this.creature - 1];
            return Math.max(5.0, Math.min((double)((float)(panelHeight - 42) / (model.previewHeight * scale)), (double)(this.f_96543_ - 92) / ((double)(model.previewWidth * scale) * 1.2)));
        }
        return 16.0;
    }

    private String number(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private String t(String k, Object ... a) {
        return ClientMythic.t(k, a);
    }

    private void draw(GuiGraphics g, String s, int x, int y, int c) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, c, false);
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String label, Runnable action) {
        if (this.insideBody && (y < 100 || y + h > this.bottom)) {
            return;
        }
        RoyalTheme.frame(g, x, y, w, h, -282450888);
        FantasyIcons.label(g, this.f_96547_, label, x, y, w, h, -1981295);
        this.controls.add(new Control(x, y, w, h, label, action));
    }

    private void action(GuiGraphics g, String key, MythicNetwork.Action a) {
        this.button(g, 24, this.cursor, this.f_96543_ - 65, 24, this.t(key, new Object[0]), () -> MythicNetwork.request(a, this.creature));
        this.cursor += 30;
    }

    private void line(GuiGraphics g, String text, int color) {
        for (FormattedCharSequence v : this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(text), this.f_96543_ - 72)) {
            ModLanguage.draw(g, this.f_96547_, v, 28, this.cursor, color, false);
            this.cursor += 14;
        }
        this.cursor += 6;
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_86600_() {
        if (this.preview != null) {
            ++this.preview.f_19797_;
            this.preview.f_20883_ = 180.0f;
        }
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        this.controls.clear();
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, 7);
        this.draw(g, ModLanguage.fit(this.f_96547_, this.t("title", new Object[0]), this.f_96543_ - 180), 18, 19, -1981295);
        this.button(g, this.f_96543_ - 155, 10, 116, 25, "English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", ModLanguage::toggle);
        this.button(g, this.f_96543_ - 33, 10, 22, 25, "\u00d7", () -> ((MythicScreen)this).m_7379_());
        this.button(g, 18, 41, this.f_96543_ - 137, 24, this.t("back", new Object[0]), () -> this.f_96541_.m_91152_((Screen)new OdysseyScreen(0)));
        this.button(g, this.f_96543_ - 108, 41, 89, 24, this.t("new_arrivals", new Object[0]), () -> {
            this.creature = this.creature < 24 || this.creature >= MythicExpedition.IDS.length ? 24 : this.creature + 1;
            this.preview = null;
            this.scroll = 0;
        });
        this.button(g, 24, 70, (this.f_96543_ - 72) / 2, 23, this.t("previous", new Object[0]), () -> {
            this.creature = (this.creature + MythicExpedition.IDS.length) % (MythicExpedition.IDS.length + 1);
            this.preview = null;
            this.scroll = 0;
        });
        this.button(g, 36 + (this.f_96543_ - 72) / 2, 70, (this.f_96543_ - 72) / 2, 23, this.t("next", new Object[0]), () -> {
            this.creature = (this.creature + 1) % (MythicExpedition.IDS.length + 1);
            this.preview = null;
            this.scroll = 0;
        });
        this.bottom = this.f_96544_ - 24;
        this.cursor = 106 - this.scroll;
        g.m_280588_(15, 100, this.f_96543_ - 24, this.bottom);
        this.insideBody = true;
        CompoundTag root = ClientMythic.data;
        ListTag list = root.m_128437_("Creatures", 10);
        CompoundTag d = list.size() == MythicExpedition.IDS.length + 1 ? list.m_128728_(this.creature) : root;
        int status = d.m_128451_("Status");
        this.line(g, this.t("count", root.m_128451_("Acquired")), -1981295);
        int panel = this.cursor;
        int panelHeight = Math.max(48, Math.min(155, this.bottom - (this.cursor + this.scroll) - 4));
        RoyalTheme.frame(g, 24, panel, this.f_96543_ - 65, panelHeight, -267578080);
        if (status < 2) {
            this.draw(g, "?", this.f_96543_ / 2, panel + panelHeight / 3, -10622494);
            this.draw(g, ModLanguage.fit(this.f_96547_, status == 0 ? this.t("unknown", new Object[0]) : (this.creature == 0 ? this.t("devourer", new Object[0]) : Component.m_237115_((String)("entity.shadowsovereign." + MythicExpedition.IDS[this.creature - 1])).getString()), this.f_96543_ - 92), 35, panel + panelHeight - 24, -1981295);
            this.preview = null;
        } else {
            if (this.preview == null && this.f_96541_.f_91073_ != null) {
                Mob mob = this.preview = this.creature == 0 ? (Mob)((EntityType)MythicContent.DEVOURER.get()).m_20615_((Level)this.f_96541_.f_91073_) : (Mob)((EntityType)MythicContent.BEASTS.get(this.creature - 1).get()).m_20615_((Level)this.f_96541_.f_91073_);
            }
            if (this.preview != null && panel + 20 >= 100 && panel + panelHeight <= this.bottom) {
                InventoryScreen.m_274545_((GuiGraphics)g, (int)(this.f_96543_ / 2), (int)(panel + panelHeight - 28), (int)((int)this.previewScale(panelHeight)), (float)(this.f_96543_ / 2 - mx + (this.creature == 9 ? 80 : 0)), (float)(panel + panelHeight / 2 - my), (LivingEntity)this.preview);
            }
            this.draw(g, ModLanguage.fit(this.f_96547_, this.t("obtained", new Object[0]), this.f_96543_ - 92), 35, panel + panelHeight - 17, -9313078);
        }
        this.cursor += panelHeight + 12;
        if (this.creature >= 9) {
            CompoundTag build = root.m_128469_("Monolith");
            if (build.m_128461_("Phase").equals("native")) {
                this.line(g, this.t("monolith_native", new Object[0]), -4937283);
            } else if (build.m_128461_("Phase").equals("ready")) {
                this.line(g, this.t("sites_ready", build.m_128451_("Ready"), build.m_128451_("Sites")), -4937283);
            } else {
                this.line(g, this.t("monolith_build", build.m_128451_("Ready"), build.m_128451_("Sites"), this.t("monolith_stage." + build.m_128461_("Phase"), new Object[0]), build.m_128451_("Current"), build.m_128451_("Total")), -4937283);
            }
        }
        this.line(g, this.t(this.creature >= 5 && status == 2 ? "covenant_bond_help" : (status == 0 ? "hidden_help" : (status == 1 ? "fight_help" : "bond_help")), new Object[0]), -988710);
        if (status >= 1) {
            BlockPos at = BlockPos.m_122022_((long)d.m_128454_("Lair"));
            this.line(g, this.t(this.creature >= 14 ? "legends_location" : (this.creature >= 9 ? "monolith_location" : (this.creature >= 5 ? "covenant_location" : "location")), at.m_123341_(), at.m_123342_() + 1, at.m_123343_()), -4937283);
        }
        if (status == 2) {
            this.line(g, this.creature == 0 ? this.t("stats", new Object[0]) : Component.m_237115_((String)("entity.shadowsovereign." + MythicExpedition.IDS[this.creature - 1])).getString(), -1981295);
            if (this.creature > 0) {
                this.line(g, this.t("beast_stats", this.number(MythicBeast.HEALTH[this.creature - 1] * 1.25), this.number(MythicBeast.DAMAGE[this.creature - 1] * 1.25), (int)MythicBeast.ARMOR[this.creature - 1]), -988710);
            }
            if (this.creature >= 9) {
                this.line(g, this.t("ability." + MythicExpedition.IDS[this.creature - 1], new Object[0]), -4937283);
            }
            if (this.creature > 0 && MythicProfiles.get(this.creature - 1).rideable()) {
                this.line(g, this.t("flight", new Object[0]), -988710);
            }
            if (d.m_128471_("Owned")) {
                this.line(g, this.t("progress", d.m_128451_("Level"), d.m_128454_("Kills")), -988710);
                this.action(g, d.m_128471_("Active") ? "recall" : "summon", d.m_128471_("Active") ? MythicNetwork.Action.RECALL : MythicNetwork.Action.SUMMON);
                this.action(g, "follow", MythicNetwork.Action.FOLLOW);
                this.action(g, "stay", MythicNetwork.Action.STAY);
            } else if (d.m_128471_("Pending")) {
                this.line(g, this.t("pending", new Object[0]), -988710);
                this.action(g, "claim", MythicNetwork.Action.CLAIM);
            } else {
                this.line(g, this.t("released", new Object[0]), -4937283);
            }
        }
        if (d.m_128441_("Phase")) {
            this.line(g, this.t("phase." + d.m_128461_("Phase"), new Object[0]), -1981295);
            if (d.m_128461_("Phase").equals("PAUSED")) {
                this.action(g, "resume", MythicNetwork.Action.RESUME);
            }
            this.action(g, "leave", MythicNetwork.Action.CANCEL);
        }
        this.line(g, this.t(this.creature >= 19 ? "chronicles_help" : (this.creature >= 14 ? "legends_help" : (this.creature >= 9 ? "monolith_help" : (this.creature >= 5 ? "covenant_help" : (this.creature == 0 ? "structure_help" : "gloom_help")))), new Object[0]), -4937283);
        this.insideBody = false;
        g.m_280618_();
        this.max = Math.max(0, this.cursor + this.scroll - this.bottom);
        this.scroll = Math.max(0, Math.min(this.max, this.scroll));
        if (this.max > 0) {
            int track = this.bottom - 100;
            int h = Math.max(24, track * track / (track + this.max));
            g.m_280509_(this.f_96543_ - 21, 100, this.f_96543_ - 11, this.bottom, -13950150);
            int y = 100 + (track - h) * this.scroll / this.max;
            g.m_280509_(this.f_96543_ - 20, y, this.f_96543_ - 12, y + h, -9906221);
        }
        this.draw(g, ModLanguage.fit(this.f_96547_, this.t("footer", new Object[0]), this.f_96543_ - 30), 18, this.f_96544_ - 16, -4937283);
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        this.down = y;
        this.start = this.scroll;
        this.drag = false;
        this.thumb = x >= (double)(this.f_96543_ - 23) && y >= 100.0 && y < (double)this.bottom;
        this.pending = null;
        if (this.thumb) {
            this.moveThumb(y);
            return true;
        }
        for (Control c : this.controls) {
            if (!c.hit(x, y) || !(y >= 100.0 && y < (double)this.bottom) && !(y < 96.0)) continue;
            this.pending = c;
            break;
        }
        return true;
    }

    private void moveThumb(double y) {
        this.scroll = Math.max(0, Math.min(this.max, (int)((y - 100.0) * (double)this.max / (double)Math.max(1, this.bottom - 100))));
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (b != 0) {
            return false;
        }
        if (this.thumb) {
            this.moveThumb(y);
        } else if (Math.abs(y - this.down) > 5.0) {
            this.drag = true;
            this.scroll = Math.max(0, Math.min(this.max, this.start + (int)(this.down - y)));
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

    public boolean m_6050_(double x, double y, double amount) {
        this.scroll = Math.max(0, Math.min(this.max, this.scroll - (int)(amount * 30.0)));
        return true;
    }

    public boolean m_7933_(int key, int scan, int mods) {
        if (ClientArmy.ORIGINAL.m_90832_(key, scan)) {
            ClientArmy.openOriginal();
            return true;
        }
        if (ClientArmy.OPEN.m_90832_(key, scan)) {
            this.m_7379_();
            return true;
        }
        return super.m_7933_(key, scan, mods);
    }

    private record Control(int x, int y, int w, int h, String text, Runnable action) {
        boolean hit(double xx, double yy) {
            return xx >= (double)this.x && xx < (double)(this.x + this.w) && yy >= (double)this.y && yy < (double)(this.y + this.h);
        }
    }
}

