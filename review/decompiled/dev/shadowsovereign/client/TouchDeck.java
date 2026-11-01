/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.client.ClientBattle;
import dev.shadowsovereign.client.ClientPreferences;
import dev.shadowsovereign.network.BattleNetwork;
import dev.shadowsovereign.network.RoyalNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.network.WarfrontNetwork;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;

public final class TouchDeck {
    public static final Action[] slots = new Action[6];

    public static void cycle(int i) {
        TouchDeck.slots[i] = Action.values()[(slots[i].ordinal() + 1) % Action.values().length];
        ClientPreferences.set("deck." + i, slots[i].name());
    }

    public static void run(int i, UUID id) {
        if (i < 0 || i >= 6) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        Action a = slots[i];
        if (a.name().startsWith("TEAM_")) {
            RoyalNetwork.request(RoyalNetwork.Action.DEPLOY, a.ordinal() - Action.TEAM_1.ordinal(), 0, 0, "", List.of());
            return;
        }
        if (id == null) {
            return;
        }
        switch (a) {
            case TEAM_1: 
            case TEAM_2: 
            case TEAM_3: 
            case TEAM_4: 
            case TEAM_5: {
                break;
            }
            case FOLLOW: 
            case STAY: 
            case GUARD: 
            case RECALL: 
            case SUMMON: {
                ShadowNetwork.request(ShadowNetwork.Action.valueOf(a.name()), id);
                break;
            }
            case EQUIPMENT: {
                ShadowNetwork.request(ShadowNetwork.Action.INVENTORY, id);
                break;
            }
            case ART: {
                WarfrontNetwork.request(WarfrontNetwork.Action.CAST, 0, false, id);
                mc.m_91152_(null);
                break;
            }
            case MEND: 
            case BARRIER: {
                ClientBattle.send(BattleNetwork.Action.SKILL, ClientBattle.state.selected(), id, a == Action.MEND ? BattleRules.Skill.MEND.ordinal() : BattleRules.Skill.BARRIER.ordinal());
                mc.m_91152_(null);
                break;
            }
            case PLAN_1: 
            case PLAN_2: 
            case PLAN_3: {
                WarfrontNetwork.request(WarfrontNetwork.Action.PLAN_RUN, a.ordinal() - Action.PLAN_1.ordinal(), false, id);
                mc.m_91152_(null);
            }
        }
    }

    static {
        Action[] defaults = new Action[]{Action.FOLLOW, Action.STAY, Action.GUARD, Action.ART, Action.EQUIPMENT, Action.MEND};
        for (int i = 0; i < 6; ++i) {
            try {
                TouchDeck.slots[i] = Action.valueOf(ClientPreferences.get("deck." + i, defaults[i].name()));
                continue;
            }
            catch (IllegalArgumentException ex) {
                TouchDeck.slots[i] = defaults[i];
            }
        }
    }

    public static enum Action {
        FOLLOW("Follow me"),
        STAY("Stay / sit"),
        GUARD("Guard here"),
        RECALL("Recall"),
        SUMMON("Summon"),
        ART("Cast Soul Art"),
        EQUIPMENT("Equipment / Soul Forge"),
        MEND("Soul Mend"),
        BARRIER("Soul Barrier"),
        PLAN_1("Run Plan 1"),
        PLAN_2("Run Plan 2"),
        PLAN_3("Run Plan 3"),
        TEAM_1("Summon Team 1"),
        TEAM_2("Summon Team 2"),
        TEAM_3("Summon Team 3"),
        TEAM_4("Summon Team 4"),
        TEAM_5("Summon Team 5");

        public final String label;

        private Action(String label) {
            this.label = label;
        }
    }
}

