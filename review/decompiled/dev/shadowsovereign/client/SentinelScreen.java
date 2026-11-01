/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArtisanPage;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.OdysseyTheme;
import dev.shadowsovereign.client.TouchDeckScreen;
import dev.shadowsovereign.network.SentinelNetwork;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class SentinelScreen
extends ArtisanPage {
    private CompoundTag data = new CompoundTag();
    private UUID selected;
    private BlockPos node;
    private int ticks;

    public SentinelScreen(Screen parent, UUID selected) {
        super(parent, "sentinel.commands", "Soulbound Sentinel");
        this.selected = selected;
    }

    public static void accept(CompoundTag data, boolean open) {
        if (data == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (open && mc.f_91074_ != null) {
            SentinelScreen screen = new SentinelScreen(mc.f_91080_, data.m_128403_("Selected") ? data.m_128342_("Selected") : null);
            screen.update(data);
            mc.m_91152_((Screen)screen);
        } else {
            Screen screen = mc.f_91080_;
            if (screen instanceof SentinelScreen) {
                SentinelScreen screen2 = (SentinelScreen)screen;
                screen2.update(data);
            }
        }
    }

    private void update(CompoundTag n) {
        this.data = n;
        BlockPos blockPos = this.node = n.m_128441_("Node") ? BlockPos.m_122022_((long)n.m_128454_("Node")) : null;
        if (this.selected != null && this.entry().m_128456_()) {
            this.selected = null;
        }
    }

    @Override
    protected void m_7856_() {
        super.m_7856_();
        this.request(0, "");
    }

    private void request(int action, String command) {
        SentinelNetwork.request(action, this.selected, command, this.node);
    }

    @Override
    public void m_86600_() {
        if (++this.ticks % 60 == 0) {
            this.request(0, "");
        }
    }

    private CompoundTag entry() {
        if (this.selected == null) {
            return new CompoundTag();
        }
        for (Tag n : this.data.m_128437_("Souls", 10)) {
            if (!((CompoundTag)n).m_128342_("Id").equals(this.selected)) continue;
            return (CompoundTag)n;
        }
        return new CompoundTag();
    }

    private String order(String id) {
        return switch (id) {
            case "FOLLOW" -> this.t("Following", "\u0645\u062a\u0627\u0628\u0639\u0629");
            case "STAY" -> this.t("Holding position", "\u062b\u0628\u0627\u062a");
            case "GUARD" -> this.t("Guarding", "\u062d\u0631\u0627\u0633\u0629");
            case "PATROL" -> this.t("Patrolling", "\u062f\u0648\u0631\u064a\u0629");
            default -> id;
        };
    }

    private String style(String id) {
        return switch (id) {
            case "CLOSE" -> this.t("At your side", "\u0628\u062c\u0627\u0646\u0628\u0643");
            case "BODYGUARD" -> this.t("Protecting you", "\u064a\u062d\u0645\u064a\u0643");
            case "RETREAT" -> this.t("Falling back", "\u064a\u0646\u0633\u062d\u0628");
            case "CEASE_FIRE" -> this.t("Cease fire", "\u0625\u064a\u0642\u0627\u0641 \u0642\u062a\u0627\u0644");
            default -> this.t("Balanced", "\u0645\u062a\u0648\u0627\u0632\u0646");
        };
    }

    @Override
    protected String pageTitle() {
        return this.t("SOULBOUND SENTINEL", "\u062d\u0627\u0631\u0633 \u0627\u0644\u0623\u0631\u0648\u0627\u062d");
    }

    private void command(GuiGraphics g, int icon, String en, String ar, String helpEn, String helpAr, String command, boolean enabled) {
        this.row(g, icon, this.t(en, ar), this.t(helpEn, helpAr), enabled, () -> this.request(1, command));
    }

    @Override
    protected void content(GuiGraphics g) {
        CompoundTag r;
        int y = this.cursor;
        int h = this.f_96544_ < 340 ? 44 : 64;
        OdysseyTheme.art(g, 5, 18, y, this.f_96543_ - 48, h);
        g.m_280509_(18, y, this.f_96543_ - 30, y + h, -2012805357);
        this.text(g, this.t("A place to guard. A bond to keep.", "\u0645\u0643\u0627\u0646 \u062a\u062d\u0631\u0633\u0647. \u0648\u0639\u0647\u062f \u062a\u062d\u0641\u0638\u0647."), 28, y + 14, -1981295);
        if (h > 44) {
            this.text(g, this.t("Physical routes \u00b7 safer following \u00b7 meaningful orders", "\u062f\u0648\u0631\u064a\u0627\u062a \u0641\u0639\u0644\u064a\u0629 \u00b7 \u0645\u062a\u0627\u0628\u0639\u0629 \u0622\u0645\u0646\u0629 \u00b7 \u0623\u0648\u0627\u0645\u0631 \u0645\u0641\u064a\u062f\u0629"), 28, y + 34, -988710);
        }
        this.cursor += h + 8;
        if (this.node != null) {
            this.row(g, 3, this.t("Patrol torch", "\u0634\u0639\u0644\u0629 \u0627\u0644\u062f\u0648\u0631\u064a\u0629"), this.node.m_123341_() + ", " + this.node.m_123342_() + ", " + this.node.m_123343_() + " \u00b7 " + this.data.m_128451_("Nodes") + this.t(" connected nodes", " \u0646\u0642\u0627\u0637 \u0645\u0631\u062a\u0628\u0637\u0629"), true, null);
        }
        if ((r = this.entry()).m_128456_()) {
            for (Tag n : this.data.m_128437_("Souls", 10)) {
                CompoundTag s = (CompoundTag)n;
                if (!this.matches(s.m_128461_("Name") + " " + s.m_128461_("Type"))) continue;
                this.row(g, 6, ModLanguage.user(s.m_128461_("Name")), this.order(s.m_128461_("Order")) + " \u00b7 " + this.t(s.m_128471_("Active") ? (s.m_128471_("Loaded") ? "Present" : "Resting in a distant area") : "In reserve", s.m_128471_("Active") ? (s.m_128471_("Loaded") ? "\u0645\u0648\u062c\u0648\u062f" : "\u0645\u0648\u062c\u0648\u062f \u0641\u064a \u0645\u0646\u0637\u0642\u0629 \u0628\u0639\u064a\u062f\u0629") : "\u0641\u064a \u0627\u0644\u0627\u062d\u062a\u064a\u0627\u0637"), () -> {
                    this.selected = s.m_128342_("Id");
                    this.scroll = 0;
                });
            }
            if (this.data.m_128437_("Souls", 10).isEmpty()) {
                this.row(g, 6, this.t("No companions yet", "\u0644\u0627 \u064a\u0648\u062c\u062f \u0631\u0641\u0627\u0642 \u0628\u0639\u062f"), this.t("Obtain a soul, then return here.", "\u0627\u062d\u0635\u0644 \u0639\u0644\u0649 \u0631\u0648\u062d \u062b\u0645 \u0639\u062f \u0625\u0644\u0649 \u0647\u0646\u0627."), true, null);
            }
            return;
        }
        this.heading(g, 6, ModLanguage.user(r.m_128461_("Name")));
        this.row(g, 3, this.t("Current assignment", "\u0627\u0644\u062a\u0643\u0644\u064a\u0641 \u0627\u0644\u062d\u0627\u0644\u064a"), this.order(r.m_128461_("Order")) + " \u00b7 " + this.style(r.m_128461_("Style")) + "\n" + this.t(r.m_128471_("Wanted") ? "Stays summoned until you recall it" : "Currently recalled", r.m_128471_("Wanted") ? "\u064a\u0628\u0642\u0649 \u0645\u0633\u062a\u062f\u0639\u0649 \u062d\u062a\u0649 \u062a\u0633\u062d\u0628\u0647" : "\u0645\u0633\u062d\u0648\u0628 \u062d\u0627\u0644\u064a\u064b\u0627"), true, null);
        this.meter(g, this.t("Companion health", "\u0635\u062d\u0629 \u0627\u0644\u0631\u0641\u064a\u0642"), r.m_128457_("Health"), r.m_128457_("MaxHealth"));
        this.row(g, 9, this.t("Your bond", "\u0631\u0627\u0628\u0637\u062a\u0643\u0645\u0627"), this.t("Level ", "\u0627\u0644\u0645\u0633\u062a\u0648\u0649 ") + r.m_128451_("Level") + " \u00b7 " + r.m_128454_("Kills") + this.t(" personal kills", " \u0642\u062a\u0644\u0629 \u0634\u062e\u0635\u064a\u0629") + " \u00b7 " + this.t("Bond ", "\u0627\u0644\u0631\u0627\u0628\u0637\u0629 ") + r.m_128451_("Bond") + " \u00b7 " + ModLanguage.nativeText("soullife.shadowsovereign.personality." + r.m_128451_("Personality"), this.t("Getting to know each other", "\u062a\u062a\u0639\u0631\u0641\u0627\u0646 \u0639\u0644\u0649 \u0628\u0639\u0636\u0643\u0645\u0627")), true, null);
        if (r.m_128471_("Reforming")) {
            this.row(g, 3, this.t("Soul recovery", "\u0639\u0648\u062f\u0629 \u0627\u0644\u0631\u0648\u062d"), (String)(r.m_128451_("CemeteryFee") > 0 ? this.t("Visit the cemetery. Diamond cost: ", "\u0632\u0631 \u0627\u0644\u0645\u0642\u0628\u0631\u0629. \u062a\u0643\u0644\u0641\u0629 \u0627\u0644\u0623\u0644\u0645\u0627\u0633: ") + r.m_128451_("CemeteryFee") : this.t("Reforming after a real death; original timing still applies.", "\u062a\u062a\u0634\u0643\u0644 \u0628\u0639\u062f \u0645\u0648\u062a \u062d\u0642\u064a\u0642\u064a\u061b \u0645\u0647\u0644\u0629 \u0627\u0644\u0639\u0648\u062f\u0629 \u0627\u0644\u0623\u0635\u0644\u064a\u0629 \u0628\u0627\u0642\u064a\u0629.")), true, null);
        }
        this.row(g, 3, this.t("Original touch command deck", "\u0644\u0648\u062d\u0629 \u0627\u0644\u0623\u0648\u0627\u0645\u0631 \u0627\u0644\u0644\u0645\u0633\u064a\u0629 \u0627\u0644\u0623\u0635\u0644\u064a\u0629"), "", () -> this.f_96541_.m_91152_((Screen)new TouchDeckScreen(this.selected)));
        boolean free = r.m_128471_("Free");
        if (!free) {
            this.row(g, 3, this.t("An activity is controlling this companion", "\u064a\u0648\u062c\u062f \u0646\u0634\u0627\u0637 \u064a\u062a\u062d\u0643\u0645 \u0641\u064a \u0647\u0630\u0627 \u0627\u0644\u0631\u0641\u064a\u0642"), this.t("Finish the activity or emergency protection first.", "\u0623\u0646\u0647\u0650 \u0627\u0644\u0646\u0634\u0627\u0637 \u0623\u0648 \u062d\u0645\u0627\u064a\u0629 \u0627\u0644\u0637\u0648\u0627\u0631\u0626 \u0623\u0648\u0644\u064b\u0627."), true, null);
        }
        if (this.node != null) {
            this.row(g, 3, this.t("Assign this soul to the linked patrol", "\u0639\u064a\u0651\u0646 \u0627\u0644\u0631\u0648\u062d \u0644\u0644\u062f\u0648\u0631\u064a\u0629 \u0627\u0644\u0645\u0631\u062a\u0628\u0637\u0629"), this.t("Two to eight torches. The route repeats until you change the order. Link first torch, then second using the Soul Route Linker.", "\u0645\u0646 \u0634\u0639\u0644\u062a\u064a\u0646 \u0625\u0644\u0649 \u062b\u0645\u0627\u0646\u064d. \u064a\u062a\u0643\u0631\u0631 \u0627\u0644\u0645\u0633\u0627\u0631 \u062d\u062a\u0649 \u062a\u063a\u064a\u0651\u0631 \u0627\u0644\u0623\u0645\u0631. \u0627\u0631\u0628\u0637 \u0627\u0644\u0634\u0639\u0644\u0629 \u0627\u0644\u0623\u0648\u0644\u0649 \u062b\u0645 \u0627\u0644\u062b\u0627\u0646\u064a\u0629 \u0628\u0623\u062f\u0627\u0629 \u0631\u0628\u0637 \u0645\u0633\u0627\u0631 \u0627\u0644\u0623\u0631\u0648\u0627\u062d."), free && this.data.m_128451_("Nodes") >= 2, () -> this.request(2, ""));
        }
        this.command(g, 6, "Summon this companion", "\u0627\u0633\u062a\u062f\u0639\u0650 \u0647\u0630\u0627 \u0627\u0644\u0631\u0641\u064a\u0642", "Original limits, death recovery and cemetery fees apply.", "\u062a\u0637\u0628\u0642 \u062d\u062f\u0648\u062f \u0627\u0644\u062c\u064a\u0634 \u0648\u0645\u0647\u0644\u0629 \u0627\u0644\u0645\u0648\u062a \u0648\u062a\u0643\u0627\u0644\u064a\u0641 \u0627\u0644\u0645\u0642\u0628\u0631\u0629 \u0627\u0644\u0623\u0635\u0644\u064a\u0629.", "SUMMON", free && !r.m_128471_("Active"));
        this.command(g, 3, "Recall by my order", "\u0627\u0633\u062d\u0628 \u0628\u0623\u0645\u0631\u064a", "Withdraw and cancel automatic live recovery until summoned again.", "\u0627\u0633\u062d\u0628 \u0648\u0623\u0648\u0642\u0641 \u0627\u0644\u0627\u0633\u062a\u0639\u0627\u062f\u0629 \u0627\u0644\u062a\u0644\u0642\u0627\u0626\u064a\u0629 \u062d\u062a\u0649 \u062a\u0633\u062a\u062f\u0639\u064a\u0647 \u0645\u062c\u062f\u062f\u064b\u0627.", "WITHDRAW", free && r.m_128471_("Wanted"));
        this.command(g, 3, "Follow normally", "\u0627\u062a\u0628\u0639\u0646\u064a \u0643\u0627\u0644\u0645\u0639\u062a\u0627\u062f", "Safe catch-up preserves ownership and progression.", "\u0644\u062d\u0627\u0642 \u0622\u0645\u0646 \u0645\u0639 \u062d\u0641\u0638 \u0627\u0644\u0645\u0644\u0643\u064a\u0629 \u0648\u0627\u0644\u062a\u0642\u062f\u0645.", "NORMAL", free);
        this.command(g, 6, "Stay close", "\u0627\u0644\u0632\u0645 \u062c\u0627\u0646\u0628\u064a", "Close formation; avoid chasing distant targets.", "\u0627\u0628\u0642\u064e \u0642\u0631\u064a\u0628\u064b\u0627 \u0648\u062a\u062c\u0646\u0628 \u0645\u0637\u0627\u0631\u062f\u0629 \u0627\u0644\u0623\u0647\u062f\u0627\u0641 \u0627\u0644\u0628\u0639\u064a\u062f\u0629.", "CLOSE", free);
        this.command(g, 6, "Protect me", "\u0627\u062d\u0645\u0646\u064a", "Defend against attackers without starting unrelated fights.", "\u062a\u0635\u062f\u0651\u064e \u0644\u0644\u0645\u0647\u0627\u062c\u0645\u064a\u0646 \u062f\u0648\u0646 \u0628\u062f\u0621 \u0645\u0639\u0627\u0631\u0643 \u063a\u064a\u0631 \u0644\u0627\u0632\u0645\u0629.", "BODYGUARD", free);
        this.command(g, 8, "Rally, then hold", "\u062a\u0639\u0627\u0644 \u062b\u0645 \u0627\u062b\u0628\u062a", "Come to me once, then stay at the arrival position.", "\u062a\u0639\u0627\u0644 \u0625\u0644\u064a \u0645\u0631\u0629 \u0648\u0627\u062d\u062f\u0629 \u062b\u0645 \u0627\u062b\u0628\u062a \u0641\u064a \u0646\u0642\u0637\u0629 \u0627\u0644\u0648\u0635\u0648\u0644.", "RALLY", free);
        this.command(g, 3, "Fall back", "\u0627\u0646\u0633\u062d\u0628", "Follow without fighting until I give another order.", "\u0627\u062a\u0628\u0639\u0646\u064a \u062f\u0648\u0646 \u0642\u062a\u0627\u0644 \u062d\u062a\u0649 \u0623\u0639\u0637\u064a \u0623\u0645\u0631\u064b\u0627 \u0622\u062e\u0631.", "RETREAT", free);
        this.command(g, 3, "Cease fire", "\u0623\u0648\u0642\u0641 \u0627\u0644\u0642\u062a\u0627\u0644", "Keep your assignment, but stop attacking. Emergency protection has priority.", "\u0627\u062d\u062a\u0641\u0638 \u0628\u062a\u0643\u0644\u064a\u0641\u0643 \u0648\u062a\u0648\u0642\u0641 \u0639\u0646 \u0627\u0644\u0647\u062c\u0648\u0645\u061b \u062d\u0645\u0627\u064a\u0629 \u0627\u0644\u0637\u0648\u0627\u0631\u0626 \u0644\u0647\u0627 \u0627\u0644\u0623\u0648\u0644\u0648\u064a\u0629.", "CEASE_FIRE", free);
        this.command(g, 3, "Save this guard post", "\u0627\u062d\u0641\u0638 \u0646\u0642\u0637\u0629 \u0627\u0644\u062d\u0631\u0627\u0633\u0629", "Remember the companion\u2019s present position.", "\u0627\u062d\u0641\u0638 \u0645\u0648\u0642\u0639 \u0627\u0644\u0631\u0641\u064a\u0642 \u0627\u0644\u062d\u0627\u0644\u064a.", "SAVE_POST", free && r.m_128471_("Loaded"));
        this.command(g, 3, "Return to your post", "\u0639\u062f \u0644\u0646\u0642\u0637\u0629 \u062d\u0631\u0627\u0633\u062a\u0643", "Return safely to a saved, loaded post and guard it.", "\u0639\u062f \u0628\u0623\u0645\u0627\u0646 \u0625\u0644\u0649 \u0646\u0642\u0637\u0629 \u0645\u062d\u0641\u0648\u0638\u0629 \u0648\u0645\u062d\u0645\u0651\u0644\u0629 \u0648\u0627\u062d\u0631\u0633\u0647\u0627.", "RETURN_POST", free);
        this.command(g, 3, "Pause the patrol", "\u0623\u0648\u0642\u0641 \u0627\u0644\u062f\u0648\u0631\u064a\u0629 \u0645\u0624\u0642\u062a\u064b\u0627", "Keep the linked route saved.", "\u0627\u062d\u062a\u0641\u0638 \u0628\u0627\u0644\u0645\u0633\u0627\u0631 \u0627\u0644\u0645\u0631\u062a\u0628\u0637 \u0645\u062d\u0641\u0648\u0638\u064b\u0627.", "PAUSE", free && r.m_128461_("Order").equals("PATROL"));
        this.command(g, 3, "Resume the patrol", "\u0627\u0633\u062a\u0623\u0646\u0641 \u0627\u0644\u062f\u0648\u0631\u064a\u0629", "Continue the saved route.", "\u062a\u0627\u0628\u0639 \u0627\u0644\u0645\u0633\u0627\u0631 \u0627\u0644\u0645\u062d\u0641\u0648\u0638.", "RESUME", free && r.m_128451_("Route") >= 2);
        this.heading(g, 9, this.t("Time with your companion", "\u0648\u0642\u062a \u0645\u0639 \u0631\u0641\u064a\u0642\u0643"));
        boolean near = free && r.m_128471_("Near");
        this.command(g, 9, "Pet gently", "\u062f\u0627\u0639\u0628\u0647 \u0628\u0631\u0641\u0642", "Approach within four blocks. Original care cooldowns apply.", "\u0627\u0642\u062a\u0631\u0628 \u0644\u0623\u0631\u0628\u0639 \u0628\u0644\u0648\u0643\u0627\u062a. \u062a\u0628\u0642\u0649 \u0645\u0647\u0644 \u0627\u0644\u0639\u0646\u0627\u064a\u0629 \u0627\u0644\u0623\u0635\u0644\u064a\u0629.", "PET", near);
        this.command(g, 9, "Talk together", "\u062a\u062d\u062f\u062b \u0645\u0639\u0647", "A response shaped by your companion\u2019s personality.", "\u0631\u062f \u064a\u0646\u0627\u0633\u0628 \u0634\u062e\u0635\u064a\u0629 \u0631\u0641\u064a\u0642\u0643.", "TALK", near);
        this.command(g, 9, "Praise that victory", "\u0627\u0645\u062f\u062d \u0627\u0646\u062a\u0635\u0627\u0631\u0647", "Praise a newly earned victory, once.", "\u0627\u0645\u062f\u062d \u0627\u0646\u062a\u0635\u0627\u0631\u064b\u0627 \u062c\u062f\u064a\u062f\u064b\u0627 \u0645\u0631\u0629 \u0648\u0627\u062d\u062f\u0629.", "PRAISE", near);
        this.command(g, 9, "Offer comfort", "\u0648\u0627\u0633\u0650\u0647", "Comfort a worried or recently hurt companion.", "\u0648\u0627\u0633\u0650 \u0631\u0641\u064a\u0642\u064b\u0627 \u0642\u0644\u0642\u064b\u0627 \u0623\u0648 \u0645\u0635\u0627\u0628\u064b\u0627 \u0645\u0624\u062e\u0631\u064b\u0627.", "COMFORT", near);
        this.command(g, 9, "Share held food", "\u0642\u062f\u0651\u0645 \u0627\u0644\u0637\u0639\u0627\u0645 \u0627\u0644\u0645\u0645\u0633\u0648\u0643", "Hold suitable food before opening this menu from K.", "\u0623\u0645\u0633\u0643 \u0637\u0639\u0627\u0645\u064b\u0627 \u0645\u0646\u0627\u0633\u0628\u064b\u0627 \u0642\u0628\u0644 \u0641\u062a\u062d \u0627\u0644\u0642\u0627\u0626\u0645\u0629 \u0645\u0646 K.", "FEED", near);
        this.row(g, 6, this.t("Choose another companion", "\u0627\u062e\u062a\u0631 \u0631\u0641\u064a\u0642\u064b\u0627 \u0622\u062e\u0631"), "", () -> {
            this.selected = null;
            this.scroll = 0;
        });
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
    }
}

