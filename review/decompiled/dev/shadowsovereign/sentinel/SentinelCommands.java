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
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.sentinel;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.network.SentinelNetwork;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.sentinel.SentinelService;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class SentinelCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent e) {
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)Commands.m_82127_((String)"shadow").requires(s -> s.m_81373_() instanceof ServerPlayer);
        LiteralArgumentBuilder commands = (LiteralArgumentBuilder)Commands.m_82127_((String)"sentinel").executes(c -> {
            SentinelNetwork.open(((CommandSourceStack)c.getSource()).m_81375_(), null, null);
            return 1;
        });
        for (String action : List.of("normal", "close", "bodyguard", "retreat", "rally", "cease_fire", "save_post", "return_post", "pause", "resume", "summon", "withdraw", "pet", "talk", "praise", "comfort", "feed")) {
            commands.then(Commands.m_82127_((String)action).then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1, (int)1024)).executes(c -> {
                boolean ok;
                ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
                long now = GuideService.now(p);
                if (p.getPersistentData().m_128441_("SentinelAt") && now - p.getPersistentData().m_128454_("SentinelAt") < 4L) {
                    return 0;
                }
                p.getPersistentData().m_128356_("SentinelAt", now);
                ArrayList<ShadowRecord> roster = new ArrayList<ShadowRecord>(GuideService.army((ServerPlayer)p).shadows.values());
                int i = IntegerArgumentType.getInteger((CommandContext)c, (String)"slot") - 1;
                boolean bl = ok = i < roster.size() && SentinelService.command(p, roster.get(i), action.toUpperCase(Locale.ROOT));
                if (!ok) {
                    p.m_5661_((Component)Component.m_237115_((String)"sentinel.shadowsovereign.unavailable"), true);
                }
                return ok ? 1 : 0;
            })));
        }
        e.getDispatcher().register((LiteralArgumentBuilder)root.then((ArgumentBuilder)commands));
    }
}

