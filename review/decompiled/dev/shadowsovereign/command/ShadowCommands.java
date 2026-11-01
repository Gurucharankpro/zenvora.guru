/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.ascension.SoulService;
import dev.shadowsovereign.eclipse.EclipsePowers;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.localization.ModMessages;
import dev.shadowsovereign.network.EclipseNetwork;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.network.RoyalNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.network.SoulLifeNetwork;
import dev.shadowsovereign.network.VigilNetwork;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class ShadowCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"shadow").requires(s -> s.m_81373_() instanceof ServerPlayer)).executes(c -> {
            ShadowService.sync(((CommandSourceStack)c.getSource()).m_81375_(), true);
            return 1;
        });
        root.then(Commands.m_82127_((String)"mythical").executes(c -> {
            MythicNetwork.open(((CommandSourceStack)c.getSource()).m_81375_());
            return 1;
        }));
        root.then(Commands.m_82127_((String)"gui").executes(c -> {
            ShadowService.sync(((CommandSourceStack)c.getSource()).m_81375_(), true);
            return 1;
        }));
        root.then(Commands.m_82127_((String)"help").executes(c -> {
            ((CommandSourceStack)c.getSource()).m_288197_(() -> ModMessages.of("Soulbound Army: /shadow [gui|list|info|summon|recall|follow|stay|guard|assist|aggressive|attack|auto|autosummon]. Warfront: /shadow warfront. Builds: /shadow art, rune, plan, pact. Equipment: /shadow inventory <slot>, /shadow feed <slot>, /shadow tiers. Adventures: /shadow adventure opens your journal. /shadow legacy opens Monarch Legacy. Tactics: /shadow squad, formation, priority, retreat, patrol, skill. /shadow skill <step|taunt|barrier|mend> [scope]. Hold V for combat wheel (rebindable). Old orders affect all shadows; add a list slot for one. sit/set = stay; stand = follow. /shadow release <slot> permanently dismisses one. /arise summons your army."), false);
            return 1;
        }));
        root.then(Commands.m_82127_((String)"list").executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
            ShadowService.tell(p, "Army: " + army.shadows.size() + " souls, " + army.activeCount() + " summoned.");
            int i = 1;
            for (ShadowRecord r : army.shadows.values()) {
                ShadowService.tell(p, i++ + ". " + r.name + " | " + r.tier.label + " | Lv." + r.level() + " | " + r.totalKills + " kills | " + (r.pendingRespawn() ? "Soul reforming" : (r.activeEntity == null ? "Reserve" : r.order.label)) + " | " + (r.aggressive ? "Aggressive" : "Assist"));
            }
            return army.shadows.size();
        }));
        root.then(Commands.m_82127_((String)"info").then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            ArrayList<ShadowRecord> list = new ArrayList<ShadowRecord>(ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values());
            int slot = IntegerArgumentType.getInteger((CommandContext)c, (String)"slot");
            if (slot > list.size()) {
                ShadowService.tell(p, "That slot does not exist. Use /shadow list.");
                return 0;
            }
            ShadowRecord r = list.get(slot - 1);
            ShadowService.tell(p, r.name + " | Level " + r.level() + "/10 | Total kills " + r.totalKills + " | Power +" + (r.level() - 1) * 10 + "% | Owner " + String.valueOf(p.m_20148_()));
            ShadowService.tell(p, r.tier.label + " | " + r.tier.power + "\u00d7 base health/damage | Trait: " + r.trait.label + " | Bond " + CompanionService.bondName(r.bond) + " (" + r.bond + "/150)");
            ShadowService.tell(p, "Squad " + r.battle.squad.label + " | " + r.battle.formation.label + " | Priority " + r.battle.priority.label + " | Retreat " + r.battle.retreat + "% | Patrol " + r.battle.route.size() + " points" + (r.battle.paused ? " (paused)" : ""));
            WarfrontState.Build build = WarfrontService.state((ServerPlayer)p).builds.get(r.id);
            if (build != null) {
                ShadowService.tell(p, "Soul Art: " + WarfrontState.label(build.art) + " | Rune: " + WarfrontState.label(build.rune) + " | Art ready in " + Math.max(0L, (build.artAt - p.f_8924_.m_129783_().m_46467_() + 19L) / 20L) + "s");
            }
            if (r.pendingRespawn()) {
                ShadowService.tell(p, "Soul returns in " + Math.max(0L, (r.respawnAt - p.f_8924_.m_129783_().m_46467_() + 19L) / 20L) + " seconds (when you are online and space is available).");
            }
            return 1;
        })));
        root.then(Commands.m_82127_((String)"tiers").executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            ShadowService.tell(p, "Soul tiers: Common 81.4% (1\u00d7), Rare 12% (1.25\u00d7), Epic 5% (1.6\u00d7), Legendary 1% (2.25\u00d7), Crazy 0.5% (3.25\u00d7), Chosen One 0.1% (5\u00d7). Chosen is once per mob type per player for the life of this world.");
            return 1;
        }));
        for (String name2 : List.of("inventory", "feed")) {
            root.then(Commands.m_82127_((String)name2).then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> {
                ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
                ArrayList<ShadowRecord> list = new ArrayList<ShadowRecord>(ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values());
                int slot = IntegerArgumentType.getInteger((CommandContext)c, (String)"slot");
                if (slot > list.size()) {
                    ShadowService.tell(p, "That slot does not exist. Use /shadow list.");
                    return 0;
                }
                ShadowRecord r = list.get(slot - 1);
                if (!CompanionService.accessible(p, r)) {
                    return 0;
                }
                return (name2.equals("inventory") ? CompanionService.open(p, r.id) : CompanionService.care(p, r)) ? 1 : 0;
            })));
        }
        root.then(Commands.m_82127_((String)"companions").executes(c -> {
            SoulLifeNetwork.send(((CommandSourceStack)c.getSource()).m_81375_(), true, 0);
            return 1;
        }));
        root.then(Commands.m_82127_((String)"feelings").executes(c -> {
            SoulLifeNetwork.send(((CommandSourceStack)c.getSource()).m_81375_(), true, 0);
            return 1;
        }));
        root.then(Commands.m_82127_((String)"vigil").executes(c -> {
            VigilNetwork.send(((CommandSourceStack)c.getSource()).m_81375_(), true, 0);
            return 1;
        }));
        root.then(Commands.m_82127_((String)"cemetery").executes(c -> {
            VigilNetwork.send(((CommandSourceStack)c.getSource()).m_81375_(), true, 2);
            return 1;
        }));
        root.then(Commands.m_82127_((String)"teams").executes(c -> {
            RoyalNetwork.send(((CommandSourceStack)c.getSource()).m_81375_(), true, 1);
            return 1;
        }));
        root.then(Commands.m_82127_((String)"skills").executes(c -> {
            RoyalNetwork.send(((CommandSourceStack)c.getSource()).m_81375_(), true, 2);
            return 1;
        }));
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)"team").then(Commands.m_82127_((String)"summon").then(Commands.m_82129_((String)"team", (ArgumentType)IntegerArgumentType.integer((int)1, (int)5)).executes(c -> RoyalService.deploy(((CommandSourceStack)c.getSource()).m_81375_(), IntegerArgumentType.getInteger((CommandContext)c, (String)"team") - 1))))).then(Commands.m_82127_((String)"recall").then(Commands.m_82129_((String)"team", (ArgumentType)IntegerArgumentType.integer((int)1, (int)5)).executes(c -> RoyalService.recall(((CommandSourceStack)c.getSource()).m_81375_(), IntegerArgumentType.getInteger((CommandContext)c, (String)"team") - 1)))));
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)"eclipse").executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            EclipseNetwork.send(p, true);
            return 1;
        })).then(Commands.m_82127_((String)"cast").then(Commands.m_82129_((String)"power", (ArgumentType)StringArgumentType.word()).suggests((c, b) -> SharedSuggestionProvider.m_82981_(Arrays.stream(EclipseState.Power.values()).map(x -> x.name().toLowerCase(Locale.ROOT)), (SuggestionsBuilder)b)).executes(c -> {
            EclipseState.Power power = EclipseState.read(EclipseState.Power.class, StringArgumentType.getString((CommandContext)c, (String)"power").toUpperCase(Locale.ROOT), null);
            return power != null && EclipsePowers.cast(((CommandSourceStack)c.getSource()).m_81375_(), power) ? 1 : 0;
        }))));
        LinkedHashMap<String, ShadowNetwork.Action> orders = new LinkedHashMap<String, ShadowNetwork.Action>();
        orders.put("summon", ShadowNetwork.Action.SUMMON);
        orders.put("recall", ShadowNetwork.Action.RECALL);
        orders.put("follow", ShadowNetwork.Action.FOLLOW);
        orders.put("stand", ShadowNetwork.Action.FOLLOW);
        orders.put("stay", ShadowNetwork.Action.STAY);
        orders.put("sit", ShadowNetwork.Action.STAY);
        orders.put("set", ShadowNetwork.Action.STAY);
        orders.put("guard", ShadowNetwork.Action.GUARD);
        orders.put("assist", ShadowNetwork.Action.ASSIST);
        orders.put("passive", ShadowNetwork.Action.ASSIST);
        orders.put("aggressive", ShadowNetwork.Action.AGGRESSIVE);
        orders.put("attack", ShadowNetwork.Action.ATTACK);
        orders.forEach((name, action) -> root.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)name).executes(c -> ShadowCommands.run((CommandContext<CommandSourceStack>)c, action, null))).then(Commands.m_82127_((String)"all").executes(c -> ShadowCommands.run((CommandContext<CommandSourceStack>)c, action, null)))).then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> ShadowCommands.run((CommandContext<CommandSourceStack>)c, action, IntegerArgumentType.getInteger((CommandContext)c, (String)"slot"))))));
        root.then(Commands.m_82127_((String)"release").then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> ShadowCommands.run((CommandContext<CommandSourceStack>)c, ShadowNetwork.Action.RELEASE, IntegerArgumentType.getInteger((CommandContext)c, (String)"slot")))));
        for (String toggle : List.of("auto", "autosummon")) {
            ShadowNetwork.Action action2 = toggle.equals("auto") ? ShadowNetwork.Action.AUTO_RECRUIT : ShadowNetwork.Action.AUTO_SUMMON;
            root.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)toggle).executes(c -> ShadowCommands.toggle((CommandContext<CommandSourceStack>)c, action2, null))).then(Commands.m_82127_((String)"on").executes(c -> ShadowCommands.toggle((CommandContext<CommandSourceStack>)c, action2, true)))).then(Commands.m_82127_((String)"off").executes(c -> ShadowCommands.toggle((CommandContext<CommandSourceStack>)c, action2, false))));
        }
        event.getDispatcher().register(root);
        event.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"arise").requires(s -> s.m_81373_() instanceof ServerPlayer)).executes(c -> ShadowCommands.run((CommandContext<CommandSourceStack>)c, ShadowNetwork.Action.SUMMON, null)));
    }

    private static int run(CommandContext<CommandSourceStack> c, ShadowNetwork.Action action, Integer slot) throws CommandSyntaxException {
        ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
        UUID id = null;
        if (slot != null) {
            ArrayList<ShadowRecord> records = new ArrayList<ShadowRecord>(ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values());
            if (slot > records.size()) {
                ShadowService.tell(p, "That slot does not exist. Use /shadow list.");
                return 0;
            }
            id = ((ShadowRecord)records.get((int)(slot.intValue() - 1))).id;
        }
        int count = ShadowNetwork.perform(p, action, id);
        if (action != ShadowNetwork.Action.ATTACK) {
            ShadowService.tell(p, "Order " + action.name().toLowerCase(Locale.ROOT) + " applied to " + count + " shadow(s).");
        }
        ShadowService.sync(p, false);
        return count;
    }

    private static int toggle(CommandContext<CommandSourceStack> c, ShadowNetwork.Action action, Boolean desired) throws CommandSyntaxException {
        boolean current;
        ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
        ArmyData.Army army = ArmyData.get(p.f_8924_).army(p.m_20148_());
        if (action == ShadowNetwork.Action.AUTO_RECRUIT) {
            if (desired == null) {
                SoulService.cycleMode(p);
            } else {
                army.autoRecruit = desired;
                army.manualRecruit = false;
                ArmyData.get(p.f_8924_).m_77762_();
            }
            ShadowService.tell(p, "Recruitment: " + SoulService.mode(p));
            ShadowService.sync(p, false);
            return 1;
        }
        boolean bl = current = action == ShadowNetwork.Action.AUTO_RECRUIT ? army.autoRecruit : army.autoSummon;
        if (desired == null || desired != current) {
            ShadowNetwork.perform(p, action, null);
        }
        ShadowService.tell(p, (action == ShadowNetwork.Action.AUTO_RECRUIT ? "Automatic recruitment: " : "Automatic summoning: ") + ((action == ShadowNetwork.Action.AUTO_RECRUIT ? army.autoRecruit : army.autoSummon) ? "ON" : "OFF"));
        ShadowService.sync(p, false);
        return 1;
    }
}

