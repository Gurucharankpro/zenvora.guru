/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
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
import com.mojang.brigadier.context.CommandContext;
import dev.shadowsovereign.ascension.AscensionService;
import dev.shadowsovereign.ascension.SoulService;
import dev.shadowsovereign.network.AscensionNetwork;
import java.util.List;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class AscensionCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent e) {
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)Commands.m_82127_((String)"shadow").requires(c -> c.m_81373_() instanceof ServerPlayer);
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)"soul").then(Commands.m_82127_((String)"claim").executes(c -> SoulService.claim(((CommandSourceStack)c.getSource()).m_81375_()) ? 1 : 0))).then(Commands.m_82127_((String)"mode").executes(c -> {
            SoulService.cycleMode(((CommandSourceStack)c.getSource()).m_81375_());
            return 1;
        })));
        LiteralArgumentBuilder menu = (LiteralArgumentBuilder)Commands.m_82127_((String)"ascension").executes(c -> {
            AscensionNetwork.send(((CommandSourceStack)c.getSource()).m_81375_(), true);
            return 1;
        });
        String[] modes = new String[]{"heist", "domination", "mirror", "draft", "rush"};
        for (int i = 0; i < 5; ++i) {
            int index = i;
            LiteralArgumentBuilder n = (LiteralArgumentBuilder)Commands.m_82127_((String)modes[i]).executes(c -> AscensionService.start(((CommandSourceStack)c.getSource()).m_81375_(), index, false) ? 1 : 0);
            for (String difficulty : List.of("relaxed", "challenging")) {
                n.then(Commands.m_82127_((String)difficulty).executes(c -> AscensionService.start(((CommandSourceStack)c.getSource()).m_81375_(), index, difficulty.equals("challenging")) ? 1 : 0));
            }
            menu.then((ArgumentBuilder)n);
        }
        menu.then(Commands.m_82127_((String)"pick").then(Commands.m_82129_((String)"card", (ArgumentType)IntegerArgumentType.integer((int)1, (int)3)).executes(c -> AscensionService.pick(((CommandSourceStack)c.getSource()).m_81375_(), IntegerArgumentType.getInteger((CommandContext)c, (String)"card") - 1) ? 1 : 0)));
        for (AscensionNetwork.Action action : List.of(AscensionNetwork.Action.INTERACT, AscensionNetwork.Action.PAUSE, AscensionNetwork.Action.RESUME, AscensionNetwork.Action.CANCEL, AscensionNetwork.Action.CLAIM_REWARD)) {
            menu.then(Commands.m_82127_((String)(action == AscensionNetwork.Action.CLAIM_REWARD ? "claim" : action.name().toLowerCase(Locale.ROOT))).executes(c -> {
                ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
                int result = AscensionNetwork.perform(p, new AscensionNetwork.Request(action, 0, false));
                AscensionNetwork.send(p, false);
                return result;
            }));
        }
        root.then((ArgumentBuilder)menu);
        e.getDispatcher().register(root);
    }
}

