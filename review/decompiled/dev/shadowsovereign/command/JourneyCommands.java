/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
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
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.journey.JourneyService;
import dev.shadowsovereign.journey.JourneyState;
import dev.shadowsovereign.network.JourneyNetwork;
import java.util.ArrayList;
import java.util.List;
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
public final class JourneyCommands {
    private static LiteralArgumentBuilder<CommandSourceStack> node(String name) {
        return Commands.m_82127_((String)name);
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent e) {
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)JourneyCommands.node("journey").executes(c -> {
            JourneyNetwork.send(((CommandSourceStack)c.getSource()).m_81375_(), true);
            return 1;
        });
        root.then(JourneyCommands.node("help").executes(c -> {
            JourneyService.tell(((CommandSourceStack)c.getSource()).m_81375_(), "Journey: boss <gravewarden|frost_regent|hollow_tyrant> [relaxed|challenging], rift [difficulty], rally <ally slot> [difficulty], fish [difficulty], cast, reel, camp <1..6>, route <quiet|dangerous>, boon <rest|ward>, domain, chains <slot>, view <slot>, exit_view, feats <slot> <badge>, campaign, pause, resume, cancel, claim.");
            return 1;
        }));
        String[] bosses = new String[]{"gravewarden", "frost_regent", "hollow_tyrant"};
        for (int i = 0; i < 3; ++i) {
            int value = i;
            LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)JourneyCommands.node(bosses[i]).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.BOSS, null, value, false));
            for (String mode : List.of("relaxed", "challenging")) {
                literalArgumentBuilder.then(JourneyCommands.node(mode).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.BOSS, null, value, mode.equals("challenging"))));
            }
            root.then(JourneyCommands.node("boss").then((ArgumentBuilder)literalArgumentBuilder));
        }
        for (Map.Entry<String, JourneyNetwork.Action> entry : Map.of("rift", JourneyNetwork.Action.RIFT, "fish", JourneyNetwork.Action.FISH).entrySet()) {
            LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)JourneyCommands.node(entry.getKey()).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, (JourneyNetwork.Action)((Object)((Object)((Object)entry.getValue()))), null, 0, false));
            for (String mode : List.of("relaxed", "challenging")) {
                literalArgumentBuilder.then(JourneyCommands.node(mode).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, (JourneyNetwork.Action)((Object)((Object)((Object)entry.getValue()))), null, 0, mode.equals("challenging"))));
            }
            root.then((ArgumentBuilder)literalArgumentBuilder);
        }
        RequiredArgumentBuilder rally = (RequiredArgumentBuilder)Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.RALLY, JourneyCommands.slot((CommandContext<CommandSourceStack>)c), 0, false));
        for (String string : List.of("relaxed", "challenging")) {
            rally.then(JourneyCommands.node(string).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.RALLY, JourneyCommands.slot((CommandContext<CommandSourceStack>)c), 0, mode.equals("challenging"))));
        }
        root.then(JourneyCommands.node("rally").then((ArgumentBuilder)rally));
        for (Map.Entry entry : Map.of("chains", JourneyNetwork.Action.CHAINS, "view", JourneyNetwork.Action.CAMERA).entrySet()) {
            root.then(JourneyCommands.node((String)entry.getKey()).then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, (JourneyNetwork.Action)((Object)((Object)((Object)entry.getValue()))), JourneyCommands.slot((CommandContext<CommandSourceStack>)c), 0, false))));
        }
        root.then(JourneyCommands.node("camp").then(Commands.m_82129_((String)"story", (ArgumentType)IntegerArgumentType.integer((int)1, (int)6)).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.CAMP, null, IntegerArgumentType.getInteger((CommandContext)c, (String)"story") - 1, false))));
        for (Map.Entry entry : Map.of("cast", JourneyNetwork.Action.CAST, "reel", JourneyNetwork.Action.REEL, "domain", JourneyNetwork.Action.DOMAIN, "exit_view", JourneyNetwork.Action.EXIT_CAMERA, "campaign", JourneyNetwork.Action.CAMPAIGN, "pause", JourneyNetwork.Action.PAUSE, "resume", JourneyNetwork.Action.RESUME, "cancel", JourneyNetwork.Action.CANCEL, "claim", JourneyNetwork.Action.CLAIM).entrySet()) {
            root.then(JourneyCommands.node((String)entry.getKey()).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, (JourneyNetwork.Action)((Object)((Object)((Object)entry.getValue()))), null, 0, false)));
        }
        root.then(((LiteralArgumentBuilder)JourneyCommands.node("route").then(JourneyCommands.node("quiet").executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.ROUTE, null, 0, false)))).then(JourneyCommands.node("dangerous").executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.ROUTE, null, 1, false))));
        root.then(((LiteralArgumentBuilder)JourneyCommands.node("boon").then(JourneyCommands.node("rest").executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.BOON, null, 0, false)))).then(JourneyCommands.node("ward").executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.BOON, null, 1, false))));
        RequiredArgumentBuilder feats = Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1));
        for (JourneyState.Feat f : JourneyState.Feat.values()) {
            feats.then(JourneyCommands.node(f.name().toLowerCase(Locale.ROOT)).executes(c -> JourneyCommands.run((CommandContext<CommandSourceStack>)c, JourneyNetwork.Action.BADGE, JourneyCommands.slot((CommandContext<CommandSourceStack>)c), f.ordinal(), false)));
        }
        root.then(JourneyCommands.node("feats").then((ArgumentBuilder)feats));
        e.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)JourneyCommands.node("shadow").requires(s -> s.m_81373_() instanceof ServerPlayer)).then((ArgumentBuilder)root));
    }

    private static UUID slot(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
        ArrayList<UUID> list = new ArrayList<UUID>(ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.keySet());
        int i = IntegerArgumentType.getInteger(c, (String)"slot") - 1;
        return i >= 0 && i < list.size() ? list.get(i) : new UUID(0L, 0L);
    }

    private static int run(CommandContext<CommandSourceStack> c, JourneyNetwork.Action action, UUID id, int value, boolean hard) throws CommandSyntaxException {
        ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
        int result = JourneyNetwork.perform(p, new JourneyNetwork.Request(action, id, value, hard));
        if (result == 0) {
            JourneyService.tell(p, "Journey action unavailable. Check requirements in H > Adventures > Journey.");
        }
        JourneyNetwork.send(p, false);
        return result;
    }
}

