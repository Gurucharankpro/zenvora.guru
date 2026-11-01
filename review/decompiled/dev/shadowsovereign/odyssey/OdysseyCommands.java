/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.odyssey;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.shadowsovereign.network.OdysseyNetwork;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class OdysseyCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent e) {
        e.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"shadow").requires(s -> s.m_81373_() instanceof ServerPlayer)).then(Commands.m_82127_((String)"guide").executes(c -> {
            OdysseyNetwork.open(((CommandSourceStack)c.getSource()).m_81375_(), 1);
            return 1;
        }))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"odyssey").executes(c -> {
            OdysseyNetwork.open(((CommandSourceStack)c.getSource()).m_81375_(), 0);
            return 1;
        })).then(Commands.m_82127_((String)"return").executes(c -> OdysseyRealm.leave(((CommandSourceStack)c.getSource()).m_81375_()) ? 1 : 0))));
    }
}

