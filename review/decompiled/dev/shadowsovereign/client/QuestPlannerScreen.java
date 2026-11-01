/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.ListTag
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ArtisanPage;
import dev.shadowsovereign.client.ClientOdyssey;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.client.GoalNotesScreen;
import dev.shadowsovereign.client.InterfaceStyle;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.WorkshopManualScreen;
import dev.shadowsovereign.network.OdysseyNetwork;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ListTag;

public final class QuestPlannerScreen
extends ArtisanPage {
    private boolean tools;
    private int chapter = -1;
    private int state;
    private int selected = -1;
    private boolean notesOnly;
    private boolean lessonsOnly;
    private boolean unfinishedFirst = true;
    private final Set<String> session = new LinkedHashSet<String>();

    @Override
    protected String pageTitle() {
        return this.t("Quest planning desk", "\u0645\u0643\u062a\u0628 \u062a\u062e\u0637\u064a\u0637 \u0627\u0644\u0645\u0647\u0645\u0627\u062a");
    }

    public QuestPlannerScreen(Screen p) {
        super(p, "artisan.planner", InterfaceStyle.text("Quest planning desk", "\u0645\u0643\u062a\u0628 \u062a\u062e\u0637\u064a\u0637 \u0627\u0644\u0645\u0647\u0645\u0627\u062a"));
        for (String s : ClientPreferences.get("planner.session", "").split(",")) {
            if (this.session.size() >= 12 || !OdysseyCatalog.goals().stream().anyMatch(g -> g.id().equals(s))) continue;
            this.session.add(s);
        }
    }

    @Override
    protected void m_7856_() {
        super.m_7856_();
        OdysseyNetwork.request(OdysseyNetwork.Action.VIEW, null, 1, 0, 0, "");
    }

    private boolean done(int i) {
        ListTag list = ClientOdyssey.data.m_128437_("Goals", 10);
        return i < list.size() && list.m_128728_(i).m_128471_("Done");
    }

    private String name(OdysseyCatalog.Goal g) {
        return ClientOdyssey.t("goal." + g.id(), new Object[0]);
    }

    private String help(OdysseyCatalog.Goal g) {
        return ClientOdyssey.t("goal_help." + g.id(), new Object[0]);
    }

    private String note(OdysseyCatalog.Goal g) {
        return ClientPreferences.get("quest.note." + g.id(), "");
    }

    private List<Integer> filtered() {
        ArrayList<Integer> out = new ArrayList<Integer>();
        for (int i = 0; i < OdysseyCatalog.goals().size(); ++i) {
            OdysseyCatalog.Goal g = OdysseyCatalog.goals().get(i);
            if (this.chapter >= 0 && g.chapter() != this.chapter || !(this.state == 0 || this.state == 1 && !this.done(i) || this.state == 2 && this.done(i)) && (this.state != 3 || !this.session.contains(g.id())) || this.notesOnly && this.note(g).isBlank() || this.lessonsOnly && !g.lesson() || !this.matches(this.name(g) + " " + this.help(g) + " " + g.route() + " " + this.note(g))) continue;
            out.add(i);
        }
        if (this.unfinishedFirst) {
            out.sort(Comparator.comparing(this::done));
        }
        return out;
    }

    @Override
    protected void content(GuiGraphics g) {
        this.row(g, 8, this.t("Planner filters", "\u0641\u0644\u0627\u062a\u0631 \u0627\u0644\u062a\u062e\u0637\u064a\u0637"), "", () -> {
            this.tools = !this.tools;
        });
        if (this.tools) {
            this.row(g, 3, this.t("Chapter: ", "\u0627\u0644\u0641\u0635\u0644: ") + (this.chapter < 0 ? this.t("All chapters", "\u0643\u0644 \u0627\u0644\u0641\u0635\u0648\u0644") : ClientOdyssey.t("chapter." + this.chapter, new Object[0])), "", () -> {
                this.chapter = this.chapter >= 11 ? -1 : this.chapter + 1;
            });
            this.row(g, 8, this.t("State: ", "\u0627\u0644\u062d\u0627\u0644\u0629: ") + this.t((new String[]{"All", "Unfinished", "Complete", "Session plan"})[this.state], (new String[]{"\u0627\u0644\u0643\u0644", "\u063a\u064a\u0631 \u0627\u0644\u0645\u0643\u062a\u0645\u0644", "\u0627\u0644\u0645\u0643\u062a\u0645\u0644", "\u062e\u0637\u0629 \u0627\u0644\u062c\u0644\u0633\u0629"})[this.state]), "", () -> {
                this.state = (this.state + 1) % 4;
            });
            this.row(g, 21, this.t("Only noted goals", "\u0627\u0644\u0645\u0647\u0645\u0627\u062a \u0630\u0627\u062a \u0627\u0644\u0645\u0644\u0627\u062d\u0638\u0627\u062a \u0641\u0642\u0637"), this.on(this.notesOnly), () -> {
                this.notesOnly = !this.notesOnly;
            });
            this.row(g, 3, this.t("Lessons only", "\u0627\u0644\u062f\u0631\u0648\u0633 \u0641\u0642\u0637"), this.on(this.lessonsOnly), () -> {
                this.lessonsOnly = !this.lessonsOnly;
            });
            this.row(g, 20, this.t("Unfinished first", "\u063a\u064a\u0631 \u0627\u0644\u0645\u0643\u062a\u0645\u0644 \u0623\u0648\u0644\u064b\u0627"), this.on(this.unfinishedFirst), () -> {
                this.unfinishedFirst = !this.unfinishedFirst;
            });
        }
        this.meter(g, this.t("Real story progress", "\u062a\u0642\u062f\u0645 \u0627\u0644\u0642\u0635\u0629 \u0627\u0644\u0641\u0639\u0644\u064a"), ClientOdyssey.data.m_128451_("Progress"), OdysseyCatalog.goals().size());
        List<Integer> list = this.filtered();
        this.row(g, 8, this.t("Matching goals", "\u0627\u0644\u0645\u0647\u0645\u0627\u062a \u0627\u0644\u0645\u0637\u0627\u0628\u0642\u0629"), list.size() + this.t(" found \u2022 Session slots ", " \u0646\u062a\u064a\u062c\u0629 \u2022 \u062e\u0627\u0646\u0627\u062a \u0627\u0644\u062c\u0644\u0633\u0629 ") + this.session.size() + " / 12", true, null);
        for (int i : list) {
            OdysseyCatalog.Goal goal = OdysseyCatalog.goals().get(i);
            int k = i;
            this.row(g, this.done(i) ? 13 : 3, (this.done(i) ? "\u2713 " : "") + this.name(goal), this.help(goal), () -> {
                this.selected = this.selected == k ? -1 : k;
            });
            if (this.selected != i) continue;
            this.row(g, 3, ClientOdyssey.t("chapter." + goal.chapter(), new Object[0]), this.t("Route: ", "\u0627\u0644\u0645\u0633\u0627\u0631: ") + ModLanguage.user(goal.route()), true, null);
            this.row(g, 13, this.t(this.session.contains(goal.id()) ? "Remove from session" : "Add to session plan", this.session.contains(goal.id()) ? "\u0625\u0632\u0627\u0644\u0629 \u0645\u0646 \u0627\u0644\u062c\u0644\u0633\u0629" : "\u0625\u0636\u0627\u0641\u0629 \u0644\u062e\u0637\u0629 \u0627\u0644\u062c\u0644\u0633\u0629"), this.t("Planning only; actual completion remains server-verified.", "\u0644\u0644\u062a\u062e\u0637\u064a\u0637 \u0641\u0642\u0637\u061b \u0627\u0644\u0625\u0643\u0645\u0627\u0644 \u0627\u0644\u0641\u0639\u0644\u064a \u064a\u062a\u062d\u0642\u0642 \u0645\u0646\u0647 \u0627\u0644\u0633\u064a\u0631\u0641\u0631."), this.session.contains(goal.id()) || this.session.size() < 12, () -> {
                if (!this.session.remove(goal.id())) {
                    this.session.add(goal.id());
                }
                ClientPreferences.set("planner.session", String.join((CharSequence)",", this.session));
            });
            this.row(g, 8, this.t("Track on HUD", "\u062a\u062a\u0628\u0639 \u0639\u0644\u0649 \u0634\u0627\u0634\u0629 \u0627\u0644\u0644\u0639\u0628"), "", () -> OdysseyNetwork.request(OdysseyNetwork.Action.PIN, null, k, 0, 0, ""));
            this.row(g, 21, this.t("Private note", "\u0645\u0644\u0627\u062d\u0638\u0629 \u062e\u0627\u0635\u0629"), ModLanguage.user(this.note(goal)), () -> this.f_96541_.m_91152_((Screen)new GoalNotesScreen(this, goal.id(), this.name(goal), this.help(goal))));
            this.row(g, 28, this.t("Copy goal & instructions", "\u0646\u0633\u062e \u0627\u0644\u0645\u0647\u0645\u0629 \u0648\u062a\u0639\u0644\u064a\u0645\u0627\u062a\u0647\u0627"), "", () -> this.f_96541_.f_91068_.m_90911_(ModLanguage.tr(this.name(goal) + "\n" + this.help(goal))));
            this.row(g, 3, this.t("Open original story chapter", "\u0627\u0641\u062a\u062d \u0641\u0635\u0644 \u0627\u0644\u0642\u0635\u0629 \u0627\u0644\u0623\u0635\u0644\u064a"), "", () -> {
                ClientOdyssey.open(1);
                OdysseyNetwork.request(OdysseyNetwork.Action.CHAPTER, null, goal.chapter(), 0, 0, "");
            });
        }
        this.row(g, 28, this.t("Copy filtered checklist", "\u0646\u0633\u062e \u0642\u0627\u0626\u0645\u0629 \u0627\u0644\u0646\u062a\u0627\u0626\u062c"), "", () -> {
            StringBuilder text = new StringBuilder();
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                OdysseyCatalog.Goal goal = OdysseyCatalog.goals().get(i);
                text.append(this.done(i) ? "[\u2713] " : "[ ] ").append(this.name(goal)).append("\n");
            }
            this.f_96541_.f_91068_.m_90911_(ModLanguage.tr(text.toString()));
        });
        this.row(g, 20, this.t("Refresh goals", "\u062a\u062d\u062f\u064a\u062b \u0627\u0644\u0645\u0647\u0645\u0627\u062a"), "", () -> OdysseyNetwork.request(OdysseyNetwork.Action.VIEW, null, 1, 0, 0, ""));
        this.row(g, 3, this.t("Workshop field manual", "\u062f\u0644\u064a\u0644 \u0627\u0644\u0648\u0631\u0634 \u0627\u0644\u0645\u064a\u062f\u0627\u0646\u064a"), "", () -> this.f_96541_.m_91152_((Screen)new WorkshopManualScreen(this)));
    }

    @Override
    protected void resetFilters() {
        this.chapter = -1;
        this.state = 0;
        this.lessonsOnly = false;
        this.notesOnly = false;
        this.unfinishedFirst = true;
    }
}

