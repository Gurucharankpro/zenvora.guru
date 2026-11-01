/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientMythic;
import dev.shadowsovereign.client.ClientRealm;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.OdysseyScreen;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.network.RealmNetwork;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.realm.RealmContent;
import dev.shadowsovereign.realm.RealmTerrain;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public final class RealmScreen
extends Screen {
    private static final int[] LAND_COLORS = new int[]{-10187420, -5406377, -6648903, -9875336};
    private static final int[] DARK_COLORS = new int[]{-14271956, -12832730, -13291451, -13819079};
    private final Map<String, LivingEntity> previews = new HashMap<String, LivingEntity>();
    private final List<Control> controls = new ArrayList<Control>();
    private int page;
    private int scroll;
    private int max;
    private int cursor;
    private int bottom;
    private double startY;
    private int startScroll;
    private boolean dragged;
    private boolean thumb;
    private Control pending;
    private UUID selected;

    public RealmScreen(int page) {
        super(LivingRealm.text("title", new Object[0]));
        this.page = Math.max(0, Math.min(6, page));
    }

    private CompoundTag d() {
        return ClientRealm.data;
    }

    private String t(String k, Object ... a) {
        return ClientRealm.t(k, a);
    }

    private void send(RealmNetwork.Action a, int v, int o) {
        RealmNetwork.request(a, v, o, this.selected);
    }

    private void show(int p) {
        this.page = Math.floorMod(p, 7);
        this.scroll = 0;
    }

    private void draw(GuiGraphics g, String s, int x, int y, int color) {
        ModLanguage.draw(g, this.f_96547_, s, x, y, color, false);
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String s, Runnable a) {
        RoyalTheme.frame(g, x, y, w, h, -232119240);
        FantasyIcons.label(g, this.f_96547_, s, x, y, w, h, -1981295);
        this.controls.add(new Control(x, y, w, h, s, a));
    }

    private void row(GuiGraphics g, String title, String help, Runnable a) {
        List lines = this.f_96547_.m_92923_((FormattedText)ModLanguage.logical(help), this.f_96543_ - 74);
        int h = Math.max(30, 29 + 13 * lines.size());
        int y = this.cursor;
        this.cursor += h + 6;
        if (y + h < 68 || y > this.bottom) {
            return;
        }
        RoyalTheme.frame(g, 17, y, this.f_96543_ - 43, h, -400811736);
        this.draw(g, ModLanguage.fit(this.f_96547_, title, this.f_96543_ - 66), 25, y + 8, -1981295);
        int yy = y + 25;
        for (FormattedCharSequence line : lines) {
            ModLanguage.draw(g, this.f_96547_, line, 25, yy, -988710, false);
            yy += 13;
        }
        if (a != null && y >= 68 && y + 25 <= this.bottom) {
            this.controls.add(new Control(17, y, this.f_96543_ - 43, Math.min(h, this.bottom - y), title, a));
        }
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_88315_(GuiGraphics g, int mx, int my, float partial) {
        this.m_280273_(g);
        this.controls.clear();
        this.bottom = this.f_96544_ - 25;
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, 7);
        this.draw(g, ModLanguage.fit(this.f_96547_, this.t("title", new Object[0]), this.f_96543_ - 180), 17, 17, -1981295);
        this.button(g, this.f_96543_ - 155, 10, 116, 23, "English / \u0627\u0644\u0639\u0631\u0628\u064a\u0629", ModLanguage::toggle);
        this.button(g, this.f_96543_ - 33, 10, 22, 23, "\u00d7", () -> ((RealmScreen)this).m_7379_());
        this.button(g, 17, 38, 25, 24, "<", () -> this.show(this.page - 1));
        this.button(g, 48, 38, this.f_96543_ - 133, 24, this.t("page." + this.page, new Object[0]), () -> this.show(0));
        this.button(g, this.f_96543_ - 78, 38, 25, 24, ">", () -> this.show(this.page + 1));
        this.button(g, this.f_96543_ - 47, 38, 35, 24, "H", () -> this.f_96541_.m_91152_((Screen)new OdysseyScreen(0)));
        this.cursor = 72 - this.scroll;
        g.m_280588_(12, 68, this.f_96543_ - 22, this.bottom);
        switch (this.page) {
            case 0: {
                this.map(g);
                break;
            }
            case 1: {
                this.hunting(g);
                break;
            }
            case 2: {
                this.outings(g);
                break;
            }
            case 3: {
                this.activities(g);
                break;
            }
            case 4: {
                this.projects(g);
                break;
            }
            case 5: {
                this.guide(g);
                break;
            }
            case 6: {
                this.astral(g);
            }
        }
        g.m_280618_();
        this.max = Math.max(0, this.cursor + this.scroll - this.bottom);
        this.scroll = Math.max(0, Math.min(this.max, this.scroll));
        if (this.max > 0) {
            int track = this.bottom - 68;
            int h = Math.max(22, track * track / (track + this.max));
            int top = 68 + (track - h) * this.scroll / this.max;
            g.m_280509_(this.f_96543_ - 21, 68, this.f_96543_ - 11, this.bottom, -13950150);
            g.m_280509_(this.f_96543_ - 20, top, this.f_96543_ - 12, top + h, -1915507);
        }
        this.draw(g, ModLanguage.fit(this.f_96547_, this.t("footer", new Object[0]), this.f_96543_ - 28), 17, this.f_96544_ - 16, -4937283);
    }

    private BlockPos center(int r) {
        BlockPos h = BlockPos.m_122022_((long)this.d().m_128454_("Home"));
        return h.m_7918_((new int[]{384, 0, -384, 0})[r], 0, (new int[]{0, 384, 0, -384})[r]);
    }

    private void map(GuiGraphics g) {
        this.row(g, this.a("title", new Object[0]), this.a("automatic", new Object[0]), () -> this.show(6));
        this.row(g, this.t("welcome", new Object[0]), this.t("map_help", new Object[0]), null);
        if (!this.d().m_128471_("Ready")) {
            this.row(g, this.t("prepare", new Object[0]), this.t("generation", this.d().m_128451_("Cursor") * 100 / 820512), () -> this.send(RealmNetwork.Action.GENERATE, 0, 0));
            return;
        }
        int size = Math.min(Math.min(this.f_96543_ - 66, 230), Math.max(60, this.bottom - (this.cursor + this.scroll) - 8));
        int x = (this.f_96543_ - size) / 2;
        int y = this.cursor;
        this.cursor += size + 8;
        BlockPos home = BlockPos.m_122022_((long)this.d().m_128454_("Home"));
        BitSet surveyed = BitSet.valueOf(this.d().m_128467_("Surveyed"));
        g.m_280509_(x, y, x + size, y + size, -16183781);
        for (int zz = 0; zz < 100; ++zz) {
            for (int xx = 0; xx < 100; ++xx) {
                int relX = xx * 14 - 700;
                int relZ = zz * 14 - 700;
                int sector = zz / 5 * 20 + xx / 5;
                boolean seen = surveyed.get(sector);
                int color = -15986144;
                if (Math.abs(relX) < 128 && Math.abs(relZ) < 128) {
                    color = seen ? -9668510 : -13617107;
                }
                for (int r = 0; r < 4; ++r) {
                    int dz;
                    int dx = relX - (this.center(r).m_123341_() - home.m_123341_());
                    if (!RealmTerrain.island(dx, dz = relZ - (this.center(r).m_123343_() - home.m_123343_()))) continue;
                    color = seen ? LAND_COLORS[r] : DARK_COLORS[r];
                }
                if (Math.abs(relX) < 5 && Math.abs(relZ) < 384 || Math.abs(relZ) < 5 && Math.abs(relX) < 384) {
                    color = seen ? -3361398 : -11187396;
                }
                int ax = x + xx * size / 100;
                int az = y + zz * size / 100;
                g.m_280509_(ax, az, x + (xx + 1) * size / 100 + 1, y + (zz + 1) * size / 100 + 1, color);
            }
        }
        int px = x + (this.d().m_128451_("X") - home.m_123341_() + 700) * size / 1400;
        int pz = y + (this.d().m_128451_("Z") - home.m_123343_() + 700) * size / 1400;
        if (this.d().m_128471_("Realm")) {
            g.m_280509_(px - 2, pz - 2, px + 3, pz + 3, -1);
        }
        for (int r = 0; r < 4; ++r) {
            BlockPos c = this.center(r);
            int xx = x + (c.m_123341_() - home.m_123341_() + 700) * size / 1400;
            int zz = y + (c.m_123343_() - home.m_123343_() + 764) * size / 1400;
            g.m_280509_(xx - 2, zz - 2, xx + 3, zz + 3, (this.d().m_128451_("Waystones") & 1 << r) != 0 ? -11659 : -9870735);
        }
        this.row(g, this.t("survey_count", this.d().m_128451_("SurveyedCount")), this.t("map_legend", new Object[0]), null);
        for (int i = 0; i < 4; ++i) {
            int r = i;
            this.row(g, this.t("region." + i, new Object[0]), this.t("region_help." + i, new Object[0]), () -> this.send(RealmNetwork.Action.PIN, 12 + r, 0));
            this.row(g, this.t("discover", new Object[0]), this.t("within_six", new Object[0]), () -> this.send(RealmNetwork.Action.DISCOVER, r, 0));
            if ((this.d().m_128451_("Waystones") & 1 << i) == 0) continue;
            this.row(g, this.t("travel", new Object[0]), this.t("travel_help", new Object[0]), () -> this.send(RealmNetwork.Action.TRAVEL, r, 0));
        }
        this.row(g, this.t("unpin", new Object[0]), "", () -> this.send(RealmNetwork.Action.PIN, -1, 0));
    }

    private void hunting(GuiGraphics g) {
        this.row(g, ClientMythic.t("title", new Object[0]), ClientMythic.t("menu_help", new Object[0]), ClientMythic::open);
        this.row(g, this.t("bosses", new Object[0]), this.t("boss_help", new Object[0]), null);
        g.m_280480_(new ItemStack((ItemLike)RealmContent.LENS.get()), this.f_96543_ - 46, this.cursor - 28);
        this.row(g, this.t("pending", this.d().m_128451_("Pending")), this.t("pending_help", new Object[0]), () -> this.send(RealmNetwork.Action.CLAIM_SOULS, 0, 0));
        for (Tag tag : this.d().m_128437_("Bestiary", 10)) {
            CompoundTag e = (CompoundTag)tag;
            ResourceLocation id = ResourceLocation.m_135820_((String)e.m_128461_("Type"));
            EntityType type = id == null ? null : (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(id);
            String name = type == null ? e.m_128461_("Type") : ModLanguage.user(type.m_20676_().getString());
            int y = this.cursor;
            this.row(g, name + " Boss", this.t("boss_stats", (int)e.m_128457_("Health"), (int)e.m_128459_("Attack"), e.m_128451_("Kills")), null);
            LivingEntity living = type == null || this.f_96541_.f_91073_ == null ? null : this.previews.computeIfAbsent(e.m_128461_("Type"), id2 -> {
                LivingEntity le;
                Entity mob = type.m_20615_((Level)this.f_96541_.f_91073_);
                return mob instanceof LivingEntity ? (le = (LivingEntity)mob) : null;
            });
            if (living == null || y < 68 || y + 38 >= this.bottom) continue;
            InventoryScreen.m_274545_((GuiGraphics)g, (int)(this.f_96543_ - 53), (int)(y + 43), (int)18, (float)0.0f, (float)0.0f, (LivingEntity)living);
        }
    }

    private void outings(GuiGraphics g) {
        int i;
        this.row(g, this.t("outings", new Object[0]), this.t("outings_help", new Object[0]), null);
        for (i = 0; i < 12; ++i) {
            int site = i;
            this.row(g, ((this.d().m_128451_("Cleared") & 1 << i) != 0 ? "\u2713 " : "\u25c7 ") + this.t("ruin", this.t("region." + i / 3, new Object[0]), i % 3 + 1), this.t("ruin_help." + i % 3, new Object[0]), () -> this.send(RealmNetwork.Action.PIN, site, 0));
            this.row(g, this.t("start", new Object[0]), this.t("within_ten", new Object[0]), () -> this.send(RealmNetwork.Action.START, 0, site));
        }
        for (int r = 0; r < 4; ++r) {
            int region = r;
            this.row(g, this.t("caravan", this.t("region." + r, new Object[0])), this.t("caravan_help", new Object[0]), () -> this.send(RealmNetwork.Action.START, 1, region));
            this.row(g, this.t("rescue", this.t("region." + r, new Object[0])), this.t("rescue_help", new Object[0]), () -> this.send(RealmNetwork.Action.START, 2, region));
            this.row(g, this.t("pin_rescue", new Object[0]), "", () -> this.send(RealmNetwork.Action.PIN, 20 + region, 0));
        }
        this.row(g, this.t("contracts", new Object[0]), this.d().m_128451_("Contract") < 0 ? this.t("no_contract", new Object[0]) : this.t("contract_status", this.t("contract." + this.d().m_128451_("Contract"), new Object[0]), this.d().m_128451_("ContractProgress"), this.d().m_128451_("Contract") == 0 ? 4 : (this.d().m_128451_("Contract") == 3 ? 8 : 1)), null);
        for (i = 0; i < 4; ++i) {
            int kind = i;
            this.row(g, this.t("contract." + i, new Object[0]), "", () -> this.send(RealmNetwork.Action.CONTRACT, kind, 0));
        }
        this.row(g, this.t("claim_contract", new Object[0]), "", () -> this.send(RealmNetwork.Action.CONTRACT, 4, 0));
        this.row(g, this.t("cancel", new Object[0]), "", () -> this.send(RealmNetwork.Action.CONTRACT, -1, 0));
    }

    private void activities(GuiGraphics g) {
        CompoundTag run = this.d().m_128469_("Run");
        if (run.m_128456_()) {
            this.row(g, this.t("no_activity", new Object[0]), this.t("outings_help", new Object[0]), () -> this.show(2));
            return;
        }
        this.row(g, this.t("activity", new Object[0]), this.t("activity_status", this.t("phase." + run.m_128451_("Phase"), new Object[0]), run.m_128451_("Enemies"), String.format(Locale.ROOT, "%.1f", (double)run.m_128451_("Hold") / 20.0), Integer.bitCount(run.m_128451_("Switches"))), null);
        this.row(g, this.t("interact", new Object[0]), this.t("activity_help", new Object[0]), () -> this.send(RealmNetwork.Action.QUICK, 0, 0));
        if (run.m_128451_("Type") == 0 && run.m_128451_("Index") % 3 == 1 && run.m_128451_("Phase") == 0) {
            for (Tag tag : this.d().m_128437_("Roster", 10)) {
                CompoundTag r = (CompoundTag)tag;
                this.row(g, this.t("buddy", ModLanguage.user(r.m_128461_("Name"))), this.t("buddy_help", new Object[0]), () -> {
                    this.selected = r.m_128342_("Id");
                    this.send(RealmNetwork.Action.BUDDY, 0, 0);
                });
            }
        }
        if (run.m_128451_("Phase") == 1) {
            this.row(g, this.t("resume", new Object[0]), "", () -> this.send(RealmNetwork.Action.RESUME, 0, 0));
        }
        if (run.m_128451_("Phase") == 2) {
            this.row(g, this.t("claim", new Object[0]), "", () -> this.send(RealmNetwork.Action.CLAIM, 0, 0));
        }
        this.row(g, this.t("cancel", new Object[0]), this.t("cancel_help", new Object[0]), () -> this.send(RealmNetwork.Action.CANCEL, 0, 0));
    }

    private void projects(GuiGraphics g) {
        this.row(g, this.t("projects", new Object[0]), this.t("project_help", new Object[0]), null);
        for (int i = 0; i < 4; ++i) {
            int r = i;
            this.row(g, ((this.d().m_128451_("Projects") & 1 << i) != 0 ? "\u2713 " : "\u25c7 ") + this.t("project." + i, new Object[0]), this.t("project_cost", this.t("material." + i, new Object[0])), () -> this.send(RealmNetwork.Action.PIN, 16 + r, 0));
            this.row(g, this.t("build", new Object[0]), this.t("within_six", new Object[0]), () -> this.send(RealmNetwork.Action.QUICK, 0, 0));
            this.row(g, this.t("regional_trade", new Object[0]), this.t("trade_cost", new Object[0]), () -> this.send(RealmNetwork.Action.TRADE, r, 0));
        }
        this.row(g, this.t("heart", new Object[0]) + (this.d().m_128451_("Finale") == 2 ? " \u2713" : ""), this.t("heart_help", new Object[0]), () -> this.send(RealmNetwork.Action.START, 3, 0));
    }

    private void guide(GuiGraphics g) {
        this.row(g, this.t("edition", new Object[0]), this.t("edition_help", new Object[0]), null);
        for (int i = 1; i <= 20; ++i) {
            this.row(g, this.t("feature." + i, new Object[0]), this.t("feature_help." + i, new Object[0]), null);
        }
        this.row(g, this.t("effects", new Object[0]), this.t(this.d().m_128471_("Reduced") ? "reduced" : "full", new Object[0]), () -> this.send(RealmNetwork.Action.REDUCED, 0, 0));
        this.row(g, this.t("recipes", new Object[0]), this.t("recipes_help", new Object[0]), () -> this.f_96541_.m_91152_((Screen)new OdysseyScreen(10)));
    }

    private String a(String key, Object ... args) {
        return ModLanguage.user(Component.m_237110_((String)("expanse.shadowsovereign." + key), (Object[])args).getString());
    }

    private void astral(GuiGraphics g) {
        this.row(g, this.a("title", new Object[0]), this.a("automatic", new Object[0]), null);
        this.row(g, this.a("waypoints", new Object[0]), this.a("cost", new Object[0]), null);
        ListTag list = this.d().m_128437_("AstralWaypoints", 10);
        for (int i = 0; i < list.size(); ++i) {
            int index = i;
            BlockPos at = BlockPos.m_122022_((long)list.m_128728_(i).m_128454_("Pos"));
            this.row(g, this.a("travel", at.m_123341_(), at.m_123343_()), "", () -> this.send(RealmNetwork.Action.ASTRAL_TRAVEL, index, 0));
        }
        this.row(g, this.a("home", new Object[0]), this.a("cost", new Object[0]), () -> this.send(RealmNetwork.Action.ASTRAL_TRAVEL, -1, 0));
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        this.startY = y;
        this.startScroll = this.scroll;
        this.dragged = false;
        this.thumb = x >= (double)(this.f_96543_ - 23) && y >= 68.0 && y < (double)this.bottom;
        this.pending = null;
        if (this.thumb) {
            this.dragThumb(y);
            return true;
        }
        for (Control c : this.controls) {
            if (!c.hit(x, y)) continue;
            this.pending = c;
            break;
        }
        return true;
    }

    private void dragThumb(double y) {
        this.scroll = Math.max(0, Math.min(this.max, (int)((y - 68.0) * (double)this.max / (double)Math.max(1, this.bottom - 68))));
    }

    public boolean m_7979_(double x, double y, int b, double dx, double dy) {
        if (b != 0) {
            return false;
        }
        if (this.thumb) {
            this.dragThumb(y);
            return true;
        }
        if (Math.abs(y - this.startY) > 5.0) {
            this.dragged = true;
            this.scroll = Math.max(0, Math.min(this.max, this.startScroll + (int)(this.startY - y)));
        }
        return true;
    }

    public boolean m_6348_(double x, double y, int b) {
        if (b != 0) {
            return false;
        }
        if (!this.dragged && !this.thumb && this.pending != null && this.pending.hit(x, y)) {
            this.pending.action.run();
        }
        this.pending = null;
        this.thumb = false;
        return true;
    }

    public boolean m_6050_(double x, double y, double amount) {
        this.scroll = Math.max(0, Math.min(this.max, this.scroll - (int)(amount * 28.0)));
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

