/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArtisanPage;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.client.ClientRoyal;
import dev.shadowsovereign.client.ClientSoulLife;
import dev.shadowsovereign.client.ClientVigil;
import dev.shadowsovereign.client.ClientWarfront;
import dev.shadowsovereign.client.DevourerModel;
import dev.shadowsovereign.client.EclipseScreen;
import dev.shadowsovereign.client.FantasyIcons;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.MythicGrimoireScreen;
import dev.shadowsovereign.client.OdysseyTheme;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.client.SentinelScreen;
import dev.shadowsovereign.client.WorkshopManualScreen;
import dev.shadowsovereign.mastery.MasteryCatalog;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicContent;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicSites;
import dev.shadowsovereign.network.MasteryNetwork;
import dev.shadowsovereign.sentinel.MasteryRewards;
import dev.shadowsovereign.shattered.ShatteredIllustrations;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class MasteryScreen
extends ArtisanPage {
    private static CompoundTag data = new CompoundTag();
    private static final Map<String, CompoundTag> entries = new HashMap<String, CompoundTag>();
    private final String chapter;
    private String expanded = "";
    private boolean unfinished;
    private int ticks;
    private Mob preview;

    public MasteryScreen(Screen parent) {
        this(parent, "");
    }

    public MasteryScreen(Screen parent, String chapter) {
        super(parent, "mastery." + chapter, "The Sovereign\u2019s Mastery");
        this.chapter = chapter;
    }

    public static void accept(CompoundTag value) {
        if (value == null) {
            return;
        }
        data = value;
        entries.clear();
        for (Tag n : value.m_128437_("Stages", 10)) {
            CompoundTag c = (CompoundTag)n;
            entries.put(c.m_128461_("Id"), c);
        }
        if (value.m_128471_("Open") && Minecraft.m_91087_().f_91074_ != null) {
            Minecraft.m_91087_().m_91152_((Screen)new MasteryScreen(null));
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
        entries.clear();
    }

    @Override
    protected void m_7856_() {
        super.m_7856_();
        MasteryNetwork.request(0, "");
    }

    @Override
    public void m_86600_() {
        if (++this.ticks % 60 == 0) {
            MasteryNetwork.request(0, "");
        }
        if (this.preview != null) {
            ++this.preview.f_19797_;
        }
    }

    @Override
    protected String pageTitle() {
        return this.t("THE SOVEREIGN\u2019S MASTERY", "\u0625\u062a\u0642\u0627\u0646 \u0627\u0644\u0645\u0644\u0643");
    }

    private String title(MasteryCatalog.Chapter c) {
        return c.creature().isEmpty() ? this.t(c.en(), c.ar()) : this.t("Hunt \u00b7 ", "\u0627\u0644\u0635\u064a\u062f \u00b7 ") + ModLanguage.nativeText("entity.shadowsovereign." + c.creature(), c.creature());
    }

    private CompoundTag status(String id) {
        return entries.getOrDefault(id, new CompoundTag());
    }

    private boolean done(String id) {
        return this.status(id).m_128471_("Done");
    }

    private void request(int action, String id) {
        MasteryNetwork.request(action, id);
    }

    @Override
    protected void content(GuiGraphics g) {
        if (data.m_128451_("Total") != MasteryCatalog.STAGES.size()) {
            this.row(g, 3, this.t("Opening your field journal\u2026", "\u062c\u0627\u0631\u064d \u0641\u062a\u062d \u0633\u062c\u0644\u0643 \u0627\u0644\u0645\u064a\u062f\u0627\u0646\u064a\u2026"), this.t("Waiting for your server-verified progress.", "\u0641\u064a \u0627\u0646\u062a\u0638\u0627\u0631 \u0627\u0644\u062a\u0642\u062f\u0645 \u0627\u0644\u0645\u062a\u062d\u0642\u0642 \u0645\u0646\u0647 \u0639\u0644\u0649 \u0627\u0644\u0633\u064a\u0631\u0641\u0631."), true, null);
            return;
        }
        if (this.chapter.isEmpty()) {
            this.index(g);
            return;
        }
        MasteryCatalog.Chapter c = MasteryCatalog.CHAPTERS.stream().filter(x -> x.id().equals(this.chapter)).findFirst().orElse(null);
        if (c == null) {
            return;
        }
        this.banner(g, c);
        if (!c.creature().isEmpty()) {
            this.dossier(g, c);
        }
        int number = 0;
        for (MasteryCatalog.Stage s : MasteryCatalog.chapter(this.chapter)) {
            ++number;
            CompoundTag st = this.status(s.id());
            String name = this.t(s.en(), s.ar());
            if (!this.matches(s.en() + " " + s.ar() + " " + s.helpEn() + " " + s.helpAr()) || this.unfinished && this.done(s.id())) continue;
            boolean open = st.m_128471_("Open");
            boolean done = st.m_128471_("Done");
            String label = done ? "\u2713 " : (open ? "\u25c7 " : "\u2022 ");
            this.row(g, done ? 13 : s.icon(), label + number + " \u00b7 " + name, (open || done ? this.t(s.helpEn(), s.helpAr()) : this.t("Complete the previous stage first.", "\u0623\u0643\u0645\u0644 \u0627\u0644\u0645\u0631\u062d\u0644\u0629 \u0627\u0644\u0633\u0627\u0628\u0642\u0629 \u0623\u0648\u0644\u064b\u0627.")) + "\n" + this.t("Evidence: ", "\u0627\u0644\u062f\u0644\u064a\u0644: ") + st.m_128454_("Value") + " / " + s.target(), () -> {
                this.expanded = this.expanded.equals(s.id()) ? "" : s.id();
            });
            if (!this.expanded.equals(s.id())) continue;
            List<ItemStack> rewards = MasteryRewards.reward(s);
            if (!rewards.isEmpty()) {
                this.row(g, 9, this.t("Milestone reward", "\u062c\u0627\u0626\u0632\u0629 \u0627\u0644\u0625\u0646\u062c\u0627\u0632"), String.join((CharSequence)" \u00b7 ", rewards.stream().map(stack -> stack.m_41613_() + "\u00d7 " + stack.m_41786_().getString()).toList()), true, null);
            }
            if (s.metric().equals("lesson") && !done) {
                this.row(g, 3, this.t("I have read and understood", "\u0642\u0631\u0623\u062a \u0648\u0641\u0647\u0645\u062a"), this.t("Acknowledges this lesson only; it cannot grant combat or discovery evidence.", "\u064a\u0624\u0643\u062f \u0642\u0631\u0627\u0621\u0629 \u0647\u0630\u0627 \u0627\u0644\u062f\u0631\u0633 \u0641\u0642\u0637\u060c \u0648\u0644\u0627 \u064a\u0645\u0646\u062d \u0625\u062b\u0628\u0627\u062a \u0642\u062a\u0627\u0644 \u0623\u0648 \u0627\u0643\u062a\u0634\u0627\u0641."), open, () -> this.request(1, s.id()));
            }
            this.row(g, 13, done ? this.t("Stage recorded", "\u062a\u0645 \u062a\u0633\u062c\u064a\u0644 \u0627\u0644\u0645\u0631\u062d\u0644\u0629") : this.t("Claim verified stage \u00b7 +1 seal", "\u0627\u0639\u062a\u0645\u062f \u0627\u0644\u0645\u0631\u062d\u0644\u0629 \u0627\u0644\u0645\u062a\u062d\u0642\u0642\u0629 \u00b7 \u062e\u062a\u0645 \u0648\u0627\u062d\u062f"), this.t("Rewards are saved once per player.", "\u0627\u0644\u0645\u0643\u0627\u0641\u0623\u0629 \u0645\u062d\u0641\u0648\u0638\u0629 \u0645\u0631\u0629 \u0648\u0627\u062d\u062f\u0629 \u0644\u0643\u0644 \u0644\u0627\u0639\u0628."), open && !done && st.m_128454_("Value") >= (long)s.target(), () -> this.request(2, s.id()));
            this.row(g, 8, this.t("Make this my next objective", "\u0627\u062c\u0639\u0644 \u0647\u0630\u0647 \u0645\u0647\u0645\u062a\u064a \u0627\u0644\u062a\u0627\u0644\u064a\u0629"), "", () -> this.request(4, s.id()));
            this.row(g, 3, this.t("Open relevant controls", "\u0627\u0641\u062a\u062d \u0627\u0644\u062e\u064a\u0627\u0631\u0627\u062a \u0627\u0644\u0645\u0631\u062a\u0628\u0637\u0629"), s.route().equals("compass") ? this.t("Use the physical compass to open its atlas.", "\u0627\u0633\u062a\u062e\u062f\u0645 \u0627\u0644\u0628\u0648\u0635\u0644\u0629 \u0646\u0641\u0633\u0647\u0627 \u0644\u0641\u062a\u062d \u0627\u0644\u0623\u0637\u0644\u0633.") : this.t("Existing controls remain the source of real actions.", "\u0627\u0644\u062e\u064a\u0627\u0631\u0627\u062a \u0627\u0644\u0623\u0635\u0644\u064a\u0629 \u0647\u064a \u0645\u0635\u062f\u0631 \u0627\u0644\u0623\u0641\u0639\u0627\u0644 \u0627\u0644\u0641\u0639\u0644\u064a\u0629."), () -> this.route(s.route()));
        }
        this.row(g, 13, this.t("Record all currently verified stages", "\u0627\u0639\u062a\u0645\u062f \u0643\u0644 \u0627\u0644\u0645\u0631\u0627\u062d\u0644 \u0627\u0644\u0645\u062a\u062d\u0642\u0642\u0629 \u062d\u0627\u0644\u064a\u064b\u0627"), this.t("Only earned, unlocked steps are claimed. Unread lessons stay incomplete.", "\u062a\u064f\u0639\u062a\u0645\u062f \u0627\u0644\u062e\u0637\u0648\u0627\u062a \u0627\u0644\u0645\u0643\u062a\u0633\u0628\u0629 \u0648\u0627\u0644\u0645\u0641\u062a\u0648\u062d\u0629 \u0641\u0642\u0637\u061b \u0627\u0644\u062f\u0631\u0648\u0633 \u063a\u064a\u0631 \u0627\u0644\u0645\u0642\u0631\u0648\u0621\u0629 \u0644\u0627 \u062a\u0643\u062a\u0645\u0644."), () -> this.request(3, ""));
        this.row(g, 8, this.t("Unfinished stages only: ", "\u063a\u064a\u0631 \u0627\u0644\u0645\u0643\u062a\u0645\u0644 \u0641\u0642\u0637: ") + this.on(this.unfinished), "", () -> {
            this.unfinished = !this.unfinished;
        });
    }

    private void index(GuiGraphics g) {
        MasteryCatalog.Stage goal;
        String next;
        this.banner(g, new MasteryCatalog.Chapter("index", "Your army. Your education.", "\u062c\u064a\u0634\u0643. \u0631\u062d\u0644\u062a\u0643 \u0646\u062d\u0648 \u0627\u0644\u0625\u062a\u0642\u0627\u0646.", 0, ""));
        int py = this.cursor;
        int ph = this.f_96544_ < 340 ? 24 : 30;
        RoyalTheme.frame(g, 18, py, this.f_96543_ - 48, ph, -266791900);
        this.text(g, this.t("Story progress: ", "\u062a\u0642\u062f\u0645 \u0627\u0644\u0642\u0635\u0629: ") + data.m_128451_("Completed") + " / " + data.m_128451_("Total"), 27, py + 7, -988710);
        g.m_280509_(27, py + ph - 6, this.f_96543_ - 39, py + ph - 3, -13489344);
        g.m_280509_(27, py + ph - 6, 27 + (int)((double)((this.f_96543_ - 66) * data.m_128451_("Completed")) / (double)Math.max(1, data.m_128451_("Total"))), py + ph - 3, InterfaceStyle.accent());
        this.cursor += ph + 5;
        if (data.m_128451_("Mail") > 0) {
            this.row(g, 9, this.t("Collect quest rewards: ", "\u0627\u0633\u062a\u0644\u0627\u0645 \u062c\u0648\u0627\u0626\u0632 \u0627\u0644\u0645\u0647\u0645\u0627\u062a: ") + data.m_128451_("Mail"), this.t("Full inventory? Rewards remain in this saved mailbox.", "\u0627\u0644\u062d\u0642\u064a\u0628\u0629 \u0645\u0645\u062a\u0644\u0626\u0629\u061f \u0627\u0644\u062c\u0648\u0627\u0626\u0632 \u062a\u0628\u0642\u0649 \u0645\u062d\u0641\u0648\u0638\u0629 \u0641\u064a \u0628\u0631\u064a\u062f\u0643."), () -> this.request(5, ""));
        }
        if (MasteryCatalog.get(next = data.m_128461_("Pinned")) == null) {
            next = data.m_128461_("Next");
        }
        if ((goal = MasteryCatalog.get(next)) != null) {
            this.row(g, 8, this.t("Next: ", "\u0627\u0644\u062a\u0627\u0644\u064a: ") + this.t(goal.en(), goal.ar()), "", () -> this.f_96541_.m_91152_((Screen)new MasteryScreen(this.parent, goal.chapter())));
        }
        if (this.f_96544_ >= 340) {
            int half = (this.f_96543_ - 56) / 2;
            if (this.cursor >= 72 && this.cursor + 24 <= this.bottom) {
                this.button(g, 18, this.cursor, half, this.t("Original chapters", "\u0627\u0644\u0641\u0635\u0648\u0644 \u0627\u0644\u0623\u0635\u0644\u064a\u0629"), () -> ClientOdyssey.open(1));
                this.button(g, 26 + half, this.cursor, half, this.t("Claim ready stages", "\u0627\u0639\u062a\u0645\u062f \u0627\u0644\u062c\u0627\u0647\u0632"), () -> this.request(3, ""));
            }
            this.cursor += 32;
        }
        for (MasteryCatalog.Chapter c : MasteryCatalog.CHAPTERS) {
            String label;
            boolean match;
            List<MasteryCatalog.Stage> stages = MasteryCatalog.chapter(c.id());
            long finished = stages.stream().filter(s -> this.done(s.id())).count();
            if (this.unfinished && finished == (long)stages.size() || !(match = this.matches(label = this.title(c)) || stages.stream().anyMatch(s -> this.matches(s.en() + " " + s.ar() + " " + s.helpEn() + " " + s.helpAr())))) continue;
            this.row(g, c.creature().isEmpty() ? 3 : 6, label, (String)(this.f_96544_ < 340 ? "" : finished + " / " + stages.size() + " \u00b7 " + this.t(c.creature().isEmpty() ? "Practical lessons and verified progress" : "Find \u00b7 confront \u00b7 obtain \u00b7 train", c.creature().isEmpty() ? "\u062f\u0631\u0648\u0633 \u0639\u0645\u0644\u064a\u0629 \u0648\u062a\u0642\u062f\u0645 \u0645\u062a\u062d\u0642\u0642" : "\u0627\u0628\u062d\u062b \u00b7 \u0648\u0627\u062c\u0647 \u00b7 \u0627\u0645\u062a\u0644\u0643 \u00b7 \u062f\u0631\u0651\u0628")), () -> this.f_96541_.m_91152_((Screen)new MasteryScreen(this.parent, c.id())));
        }
        if (this.f_96544_ < 340) {
            this.row(g, 3, this.t("Original chapters", "\u0627\u0644\u0641\u0635\u0648\u0644 \u0627\u0644\u0623\u0635\u0644\u064a\u0629"), "", () -> ClientOdyssey.open(1));
            this.row(g, 13, this.t("Claim ready stages", "\u0627\u0639\u062a\u0645\u062f \u0627\u0644\u0645\u0631\u0627\u062d\u0644 \u0627\u0644\u062c\u0627\u0647\u0632\u0629"), "", () -> this.request(3, ""));
        }
        this.row(g, 8, this.t("Hide complete chapters: ", "\u0625\u062e\u0641\u0627\u0621 \u0627\u0644\u0641\u0635\u0648\u0644 \u0627\u0644\u0645\u0643\u062a\u0645\u0644\u0629: ") + this.on(this.unfinished), "", () -> {
            this.unfinished = !this.unfinished;
        });
    }

    private void banner(GuiGraphics g, MasteryCatalog.Chapter c) {
        int h = c.id().equals("index") ? (this.f_96544_ < 340 ? 40 : 64) : (this.f_96544_ < 340 ? 50 : 84);
        int y = this.cursor;
        OdysseyTheme.art(g, c.scene(), 18, y, this.f_96543_ - 48, h);
        g.m_280509_(18, y, this.f_96543_ - 30, y + h, -2012739565);
        FantasyIcons.draw(g, c.creature().isEmpty() ? 3 : 6, 27, y + 12, 26);
        this.text(g, ModLanguage.fit(this.f_96547_, this.title(c), this.f_96543_ - 115), 62, y + 15, -1981295);
        if (h >= 50) {
            this.text(g, this.t("Learn it. Do it. Make it yours.", "\u0627\u0641\u0647\u0645\u0647\u0627. \u0646\u0641\u0651\u0630\u0647\u0627. \u0627\u062c\u0639\u0644\u0647\u0627 \u0645\u0647\u0627\u0631\u062a\u0643."), 62, y + 33, -988710);
        }
        this.cursor += h + 9;
    }

    private void dossier(GuiGraphics g, MasteryCatalog.Chapter c) {
        LivingEntityRenderer living;
        EntityModel entityModel;
        String id = c.creature();
        int kind = Arrays.asList(MythicExpedition.IDS).indexOf(id);
        MythicSites.Site site = MythicSites.sites().stream().filter(s -> s.kind() == kind).findFirst().orElse(null);
        String home = site == null ? "" : ModLanguage.nativeText("atlas.shadowsovereign.site." + site.structure().m_135815_(), site.structure().m_135815_());
        this.row(g, 3, this.t("HABITAT", "\u0627\u0644\u0645\u0648\u0637\u0646"), home, true, null);
        this.illustration(g, id);
        this.row(g, 3, this.t("The legend\u2019s story", "\u062d\u0643\u0627\u064a\u0629 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u0629"), ModLanguage.nativeText("grimoire.shadowsovereign.lore." + id, id), true, null);
        this.row(g, 6, this.t("Watch its abilities", "\u0627\u0646\u062a\u0628\u0647 \u0644\u0642\u062f\u0631\u0627\u062a\u0647"), ModLanguage.nativeText("grimoire.shadowsovereign.abilities." + id, ""), true, null);
        int y = this.cursor;
        int h = this.f_96544_ < 340 ? 90 : 150;
        this.cursor += h + 8;
        if (y < 72 || y + h > this.bottom) {
            return;
        }
        RoyalTheme.frame(g, 18, y, this.f_96543_ - 48, h, -15723745);
        if (this.status(c.id() + ".4").m_128454_("Value") == 0L) {
            ModLanguage.centered(g, this.f_96547_, "?", this.f_96543_ / 2, y + h / 2 - 10, -1981295);
            this.text(g, this.t("Its portrait awakens when the soul becomes yours.", "\u062a\u0633\u062a\u064a\u0642\u0638 \u0635\u0648\u0631\u062a\u0647 \u0639\u0646\u062f\u0645\u0627 \u062a\u0635\u0628\u062d \u0627\u0644\u0631\u0648\u062d \u0645\u0644\u0643\u0643."), 28, y + h - 22, -4937283);
            return;
        }
        if (this.preview == null) {
            Mob mob = this.preview = kind < 0 ? (Mob)((EntityType)MythicContent.DEVOURER.get()).m_20615_((Level)this.f_96541_.f_91073_) : (Mob)((EntityType)MythicContent.BEASTS.get(kind).get()).m_20615_((Level)this.f_96541_.f_91073_);
        }
        if (this.preview == null) {
            return;
        }
        int size = 25;
        EntityRenderer renderer = this.f_96541_.m_91290_().m_114382_((Entity)this.preview);
        if (renderer instanceof LivingEntityRenderer && (entityModel = (living = (LivingEntityRenderer)renderer).m_7200_()) instanceof DevourerModel) {
            DevourerModel model = (DevourerModel)entityModel;
            float scale = kind < 0 ? 0.65f : MythicBeast.RENDER_SCALE[kind];
            size = (int)Math.max(1.0, 0.8 * Math.min((double)((float)(h - 12) / (model.previewHeight * scale)), (double)(this.f_96543_ - 100) / ((double)(model.previewWidth * scale) * 1.25)));
        }
        InventoryScreen.m_274545_((GuiGraphics)g, (int)(this.f_96543_ / 2), (int)(y + h - 8), (int)size, (float)0.0f, (float)0.0f, (LivingEntity)this.preview);
    }

    private void illustration(GuiGraphics g, String id) {
        int tw;
        int[] extra = ShatteredIllustrations.size(id);
        if (!(extra != null || id.equals("mad_farmer") || id.equals("plague_doctor") || id.equals("cursed_executioner"))) {
            return;
        }
        int w = this.f_96543_ - 48;
        int h = this.f_96544_ < 340 ? 100 : 160;
        int y = this.cursor;
        this.cursor += h + 8;
        if (y < 72 || y + h > this.bottom) {
            return;
        }
        RoyalTheme.frame(g, 18, y, w, h, -15723745);
        int n = extra != null ? extra[0] : (id.equals("mad_farmer") ? 2200 : (tw = id.equals("cursed_executioner") ? 730 : 460));
        int th = extra != null ? extra[1] : (id.equals("mad_farmer") ? 900 : (id.equals("cursed_executioner") ? 699 : 467));
        float scale = Math.min((float)(w - 8) / (float)tw, (float)(h - 8) / (float)th);
        int rw = (int)((float)tw * scale);
        int rh = (int)((float)th * scale);
        g.m_280411_(new ResourceLocation("shadowsovereign", "textures/gui/mastery/" + id + "_home.png"), 18 + (w - rw) / 2, y + (h - rh) / 2, rw, rh, 0.0f, 0.0f, tw, th, tw, th);
    }

    private void route(String route) {
        switch (route) {
            case "sentinel": {
                this.f_96541_.m_91152_((Screen)new SentinelScreen(this, null));
                break;
            }
            case "compass": {
                break;
            }
            case "equipment": 
            case "army": {
                ClientArmy.openOriginal();
                break;
            }
            case "grimoire": {
                MythicGrimoireScreen s = new MythicGrimoireScreen();
                MasteryCatalog.Chapter c = MasteryCatalog.CHAPTERS.stream().filter(v -> v.id().equals(this.chapter)).findFirst().orElse(null);
                if (c != null && !c.creature().isEmpty()) {
                    s.selectPage(Arrays.asList(MasteryCatalog.CREATURES).indexOf(c.creature()));
                }
                this.f_96541_.m_91152_((Screen)s);
                break;
            }
            case "workshops": 
            case "recipes": {
                this.f_96541_.m_91152_((Screen)new WorkshopManualScreen(this));
                break;
            }
            case "adventure": 
            case "journey": 
            case "legacy": 
            case "ascension": {
                ClientRoyal.open(3);
                break;
            }
            case "warfront": {
                ClientWarfront.open(null, 0);
                break;
            }
            case "sovereign": {
                ClientRoyal.open(4);
                break;
            }
            case "royal": {
                ClientRoyal.open(0);
                break;
            }
            case "eclipse": {
                this.f_96541_.m_91152_((Screen)new EclipseScreen(0));
                break;
            }
            case "care": {
                ClientSoulLife.open(0);
                break;
            }
            case "cemetery": {
                ClientVigil.open(2);
                break;
            }
            case "companies": {
                ClientRoyal.open(1);
                break;
            }
            case "camp": {
                ClientOdyssey.open(3);
                break;
            }
            default: {
                ClientOdyssey.open(0);
            }
        }
    }

    @Override
    public void m_7379_() {
        if (!this.chapter.isEmpty()) {
            this.f_96541_.m_91152_((Screen)new MasteryScreen(this.parent));
        } else {
            super.m_7379_();
        }
    }

    @Override
    public void m_7861_() {
        this.preview = null;
        super.m_7861_();
    }
}

