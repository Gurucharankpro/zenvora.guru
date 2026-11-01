/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
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
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.network.LegacyNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class LegacyCommands {
    private static LiteralArgumentBuilder<CommandSourceStack> node(String value) {
        return Commands.m_82127_((String)value);
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent e) {
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)LegacyCommands.node("legacy").executes(c -> {
            LegacyNetwork.open(((CommandSourceStack)c.getSource()).m_81375_());
            return 1;
        });
        root.then(LegacyCommands.node("chronicle").executes(c -> {
            LegacyNetwork.open(((CommandSourceStack)c.getSource()).m_81375_(), "CHRONICLE");
            return 1;
        }));
        for (Map.Entry<String, LegacyNetwork.Action> entry : Map.of("exchange", LegacyNetwork.Action.EXCHANGE, "burst", LegacyNetwork.Action.BURST).entrySet()) {
            root.then(LegacyCommands.node(entry.getKey()).then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, (LegacyNetwork.Action)((Object)((Object)entry.getValue())), LegacyCommands.slot((CommandContext<CommandSourceStack>)c, "slot"), null, 0, "", BattleRules.Squad.ALL))));
        }
        root.then(LegacyCommands.node("link").then(Commands.m_82129_((String)"first", (ArgumentType)IntegerArgumentType.integer((int)1)).then(Commands.m_82129_((String)"second", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.LINK, LegacyCommands.slot((CommandContext<CommandSourceStack>)c, "first"), LegacyCommands.slot((CommandContext<CommandSourceStack>)c, "second"), 0, "", BattleRules.Squad.ALL)))));
        for (Map.Entry<String, LegacyNetwork.Action> entry : Map.of("unlink", LegacyNetwork.Action.UNLINK, "pause", LegacyNetwork.Action.PAUSE, "resume", LegacyNetwork.Action.RESUME, "cancel", LegacyNetwork.Action.CANCEL, "claim", LegacyNetwork.Action.CLAIM, "extract", LegacyNetwork.Action.EXTRACT, "retry", LegacyNetwork.Action.RETRY).entrySet()) {
            root.then(LegacyCommands.node(entry.getKey()).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, (LegacyNetwork.Action)((Object)((Object)entry.getValue())), null, null, 0, "", BattleRules.Squad.ALL)));
        }
        for (Map.Entry<String, LegacyNetwork.Action> entry : Map.of("gauntlet", LegacyNetwork.Action.GAUNTLET, "siege", LegacyNetwork.Action.SIEGE, "vault", LegacyNetwork.Action.VAULT).entrySet()) {
            LiteralArgumentBuilder n = (LiteralArgumentBuilder)LegacyCommands.node(entry.getKey()).then(LegacyCommands.node("start").executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, (LegacyNetwork.Action)((Object)((Object)entry.getValue())), null, null, 0, "", BattleRules.Squad.ALL)));
            if (entry.getValue() == LegacyNetwork.Action.GAUNTLET) {
                n.then(LegacyCommands.node("extract").executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.EXTRACT, null, null, 0, "", BattleRules.Squad.ALL)));
            }
            root.then((ArgumentBuilder)n);
        }
        String[] boons = new String[]{"fleetfoot", "second_wind", "night_ward"};
        for (int i = 0; i < 3; ++i) {
            int value = i;
            root.then(LegacyCommands.node("boon").then(LegacyCommands.node(boons[i]).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.BOON, null, null, value, "", BattleRules.Squad.ALL))));
        }
        String[] symbols = new String[]{"sun", "moon", "star", "void"};
        for (int i = 0; i < 4; ++i) {
            int value = i;
            root.then(LegacyCommands.node("symbol").then(LegacyCommands.node(symbols[i]).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.SYMBOL, null, null, value, "", BattleRules.Squad.ALL))));
        }
        root.then(LegacyCommands.node("nickname").then(((RequiredArgumentBuilder)Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.NICKNAME, LegacyCommands.slot((CommandContext<CommandSourceStack>)c, "slot"), null, 0, "", BattleRules.Squad.ALL))).then(Commands.m_82129_((String)"name", (ArgumentType)StringArgumentType.greedyString()).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.NICKNAME, LegacyCommands.slot((CommandContext<CommandSourceStack>)c, "slot"), null, 0, StringArgumentType.getString((CommandContext)c, (String)"name"), BattleRules.Squad.ALL)))));
        for (String mode : new String[]{"on", "off"}) {
            root.then(LegacyCommands.node("favorite").then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).then(LegacyCommands.node(mode).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.FAVORITE, LegacyCommands.slot((CommandContext<CommandSourceStack>)c, "slot"), null, mode.equals("on") ? 1 : 0, "", BattleRules.Squad.ALL)))));
            root.then(LegacyCommands.node("chatter").then(LegacyCommands.node(mode).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.CHATTER, null, null, mode.equals("on") ? 1 : 0, "", BattleRules.Squad.ALL))));
        }
        String[] emotes = new String[]{"crown", "heart", "spiral"};
        for (int i = 0; i < 3; ++i) {
            int value = i;
            LiteralArgumentBuilder n = (LiteralArgumentBuilder)LegacyCommands.node(emotes[i]).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.EMOTE, null, null, value, "", BattleRules.Squad.ALL));
            n.then(LegacyCommands.node("ally").then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.EMOTE, LegacyCommands.slot((CommandContext<CommandSourceStack>)c, "slot"), null, value, "", BattleRules.Squad.ALL))));
            for (BattleRules.Squad scope : BattleRules.Squad.values()) {
                n.then(LegacyCommands.node(scope.name().toLowerCase(Locale.ROOT)).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.EMOTE, null, null, value, "", scope)));
            }
            root.then(LegacyCommands.node("emote").then((ArgumentBuilder)n));
        }
        root.then(((LiteralArgumentBuilder)LegacyCommands.node("loadout").then(LegacyCommands.node("save").then(((RequiredArgumentBuilder)Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1, (int)3)).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.SAVE_LOADOUT, null, null, IntegerArgumentType.getInteger((CommandContext)c, (String)"slot") - 1, "", BattleRules.Squad.ALL))).then(Commands.m_82129_((String)"name", (ArgumentType)StringArgumentType.greedyString()).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.SAVE_LOADOUT, null, null, IntegerArgumentType.getInteger((CommandContext)c, (String)"slot") - 1, StringArgumentType.getString((CommandContext)c, (String)"name"), BattleRules.Squad.ALL)))))).then(LegacyCommands.node("apply").then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1, (int)3)).executes(c -> LegacyCommands.run((CommandContext<CommandSourceStack>)c, LegacyNetwork.Action.APPLY_LOADOUT, null, null, IntegerArgumentType.getInteger((CommandContext)c, (String)"slot") - 1, "", BattleRules.Squad.ALL)))));
        e.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)LegacyCommands.node("shadow").requires(s -> s.m_81373_() instanceof ServerPlayer)).then((ArgumentBuilder)root));
    }

    private static UUID slot(CommandContext<CommandSourceStack> c, String key) throws CommandSyntaxException {
        ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
        ArrayList<ShadowRecord> list = new ArrayList<ShadowRecord>(ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values());
        int index = IntegerArgumentType.getInteger(c, (String)key) - 1;
        return index >= 0 && index < list.size() ? list.get((int)index).id : new UUID(0L, 0L);
    }

    private static int run(CommandContext<CommandSourceStack> c, LegacyNetwork.Action action, UUID a, UUID b, int value, String text, BattleRules.Squad scope) throws CommandSyntaxException {
        ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
        int changed = LegacyNetwork.perform(p, new LegacyNetwork.Request(action, a, b, value, scope, text));
        if (changed == 0) {
            LegacyService.notice(p, "Legacy action unavailable. Check the unlock, cooldown, companion and Sanctuary requirements.");
        }
        LegacyNetwork.send(p, false, a);
        if (action == LegacyNetwork.Action.NICKNAME || action == LegacyNetwork.Action.FAVORITE || action == LegacyNetwork.Action.APPLY_LOADOUT) {
            ShadowNetwork.sendArmy(p, false);
        }
        return changed;
    }
}

