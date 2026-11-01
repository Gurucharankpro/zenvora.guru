/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
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
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.network.BattleNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
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
public final class BattleCommands {
    private static RequiredArgumentBuilder<CommandSourceStack, String> word(String name, String ... choices) {
        return Commands.m_82129_((String)name, (ArgumentType)StringArgumentType.word()).suggests((c, b) -> SharedSuggestionProvider.m_82967_((String[])choices, (SuggestionsBuilder)b));
    }

    private static String[] names(Class<? extends Enum<?>> type) {
        return (String[])Arrays.stream(type.getEnumConstants()).map(e -> e.name().toLowerCase(Locale.ROOT)).toArray(String[]::new);
    }

    /*
     * WARNING - void declaration
     */
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        void var5_14;
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)Commands.m_82127_((String)"shadow").requires(s -> s.m_81373_() instanceof ServerPlayer);
        root.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"squad").then(Commands.m_82127_((String)"assign").then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).then(BattleCommands.word("squad", BattleCommands.names(BattleRules.Squad.class)).executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            BattleRules.Squad squad = BattleCommands.parse(BattleRules.Squad.class, StringArgumentType.getString((CommandContext)c, (String)"squad"));
            ArrayList<ShadowRecord> records = new ArrayList<ShadowRecord>(ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values());
            int slot = IntegerArgumentType.getInteger((CommandContext)c, (String)"slot");
            if (squad == null || squad == BattleRules.Squad.ALL || slot > records.size()) {
                ShadowService.tell(p, "Use an existing slot and unassigned/alpha/beta/gamma/delta.");
                return 0;
            }
            return BattleCommands.run(p, new BattleNetwork.Request(BattleNetwork.Action.ASSIGN, BattleRules.Squad.ALL, records.get((int)(slot - 1)).id, squad.ordinal()));
        }))))).then(Commands.m_82127_((String)"select").then(BattleCommands.word("scope", BattleCommands.names(BattleRules.Squad.class)).executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            BattleRules.Squad scope = BattleCommands.scope((CommandContext<CommandSourceStack>)c);
            return scope == null ? 0 : BattleCommands.run(p, new BattleNetwork.Request(BattleNetwork.Action.SELECT_SCOPE, scope, null, 0));
        })))).then(Commands.m_82127_((String)"order").then(BattleCommands.word("scope", BattleCommands.names(BattleRules.Squad.class)).then(BattleCommands.word("order", "follow", "stay", "guard", "attack", "summon", "recall", "patrol").executes(c -> {
            BattleRules.Squad scope = BattleCommands.scope((CommandContext<CommandSourceStack>)c);
            BattleNetwork.Action action = BattleCommands.action(StringArgumentType.getString((CommandContext)c, (String)"order"));
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            if (scope == null || action == null) {
                ShadowService.tell(p, "Unknown squad or order.");
                return 0;
            }
            return BattleCommands.run(p, new BattleNetwork.Request(action, scope, null, 0));
        })))));
        for (BattleRules.Formation formation : BattleRules.Formation.values()) {
            root.then(Commands.m_82127_((String)"formation").then(((LiteralArgumentBuilder)Commands.m_82127_((String)formation.name().toLowerCase(Locale.ROOT)).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, BattleNetwork.Action.FORMATION, formation.ordinal(), BattleRules.Squad.ALL))).then(BattleCommands.word("scope", BattleCommands.names(BattleRules.Squad.class)).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, BattleNetwork.Action.FORMATION, formation.ordinal(), BattleCommands.scope((CommandContext<CommandSourceStack>)c))))));
        }
        for (BattleRules.Priority priority : BattleRules.Priority.values()) {
            root.then(Commands.m_82127_((String)"priority").then(((LiteralArgumentBuilder)Commands.m_82127_((String)priority.name().toLowerCase(Locale.ROOT)).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, BattleNetwork.Action.PRIORITY, priority.ordinal(), BattleRules.Squad.ALL))).then(BattleCommands.word("scope", BattleCommands.names(BattleRules.Squad.class)).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, BattleNetwork.Action.PRIORITY, priority.ordinal(), BattleCommands.scope((CommandContext<CommandSourceStack>)c))))));
        }
        for (int n : new int[]{0, 10, 25, 50}) {
            root.then(Commands.m_82127_((String)"retreat").then(((LiteralArgumentBuilder)Commands.m_82127_((String)(n == 0 ? "off" : Integer.toString(n))).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, BattleNetwork.Action.RETREAT, n, BattleRules.Squad.ALL))).then(BattleCommands.word("scope", BattleCommands.names(BattleRules.Squad.class)).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, BattleNetwork.Action.RETREAT, n, BattleCommands.scope((CommandContext<CommandSourceStack>)c))))));
        }
        Map<String, BattleNetwork.Action> map = Map.of("add", BattleNetwork.Action.PATROL_ADD, "clear", BattleNetwork.Action.PATROL_CLEAR, "start", BattleNetwork.Action.PATROL_START);
        map.forEach((name, action) -> root.then(Commands.m_82127_((String)"patrol").then(((LiteralArgumentBuilder)Commands.m_82127_((String)name).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, action, 0, BattleRules.Squad.ALL))).then(BattleCommands.word("scope", BattleCommands.names(BattleRules.Squad.class)).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, action, 0, BattleCommands.scope((CommandContext<CommandSourceStack>)c)))))));
        BattleRules.Skill[] skillArray = BattleRules.Skill.values();
        int n = skillArray.length;
        boolean bl = false;
        while (var5_14 < n) {
            BattleRules.Skill skill = skillArray[var5_14];
            root.then(Commands.m_82127_((String)"skill").then(((LiteralArgumentBuilder)Commands.m_82127_((String)skill.name().toLowerCase(Locale.ROOT)).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, BattleNetwork.Action.SKILL, skill.ordinal(), BattleRules.Squad.ALL))).then(BattleCommands.word("scope", BattleCommands.names(BattleRules.Squad.class)).executes(c -> BattleCommands.run((CommandContext<CommandSourceStack>)c, BattleNetwork.Action.SKILL, skill.ordinal(), BattleCommands.scope((CommandContext<CommandSourceStack>)c))))));
            ++var5_14;
        }
        event.getDispatcher().register(root);
    }

    private static BattleNetwork.Action action(String value) {
        if (value.equalsIgnoreCase("patrol")) {
            return BattleNetwork.Action.PATROL_START;
        }
        try {
            BattleNetwork.Action action = BattleNetwork.Action.valueOf(value.toUpperCase(Locale.ROOT));
            return switch (action) {
                case BattleNetwork.Action.FOLLOW, BattleNetwork.Action.STAY, BattleNetwork.Action.GUARD, BattleNetwork.Action.ATTACK, BattleNetwork.Action.SUMMON, BattleNetwork.Action.RECALL -> action;
                default -> null;
            };
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static <E extends Enum<E>> E parse(Class<E> type, String value) {
        try {
            return Enum.valueOf(type, value.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static BattleRules.Squad scope(CommandContext<CommandSourceStack> c) {
        return BattleCommands.parse(BattleRules.Squad.class, StringArgumentType.getString(c, (String)"scope"));
    }

    private static int run(CommandContext<CommandSourceStack> c, BattleNetwork.Action action, int value, BattleRules.Squad scope) throws CommandSyntaxException {
        ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
        if (scope == null) {
            ShadowService.tell(p, "Unknown scope. Use all, unassigned, alpha, beta, gamma or delta.");
            return 0;
        }
        return BattleCommands.run(p, new BattleNetwork.Request(action, scope, null, value));
    }

    private static int run(ServerPlayer p, BattleNetwork.Request request) {
        int changed = BattleNetwork.perform(p, request);
        ShadowNetwork.sendArmy(p, false);
        if (request.action() != BattleNetwork.Action.SKILL) {
            ShadowService.tell(p, "Tactical action applied to " + changed + " companion(s)/settings.");
        }
        return changed;
    }
}

