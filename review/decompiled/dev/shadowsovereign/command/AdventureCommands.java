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
 *  net.minecraft.commands.arguments.UuidArgument
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
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
import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.network.AdventureNetwork;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class AdventureCommands {
    private static LiteralArgumentBuilder<CommandSourceStack> node(String name) {
        return Commands.m_82127_((String)name);
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)AdventureCommands.node("adventure").executes(c -> {
            AdventureNetwork.open(((CommandSourceStack)c.getSource()).m_81375_());
            return 1;
        });
        root.then(AdventureCommands.node("bind").executes(c -> {
            BlockHitResult hit;
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            HitResult patt1439$temp = p.m_19907_(6.0, 0.0f, false);
            return patt1439$temp instanceof BlockHitResult && AdventureService.bind(p, (hit = (BlockHitResult)patt1439$temp).m_82425_()) ? 1 : 0;
        }));
        root.then(AdventureCommands.node("scan").executes(c -> {
            AdventureService.scan(((CommandSourceStack)c.getSource()).m_81375_());
            return 1;
        }));
        root.then(AdventureCommands.node("status").executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            AdventureData.Journal j = AdventureService.journal(p);
            AdventureService.tell(p, j.completions + " adventures / " + j.discoveries.size() + " types / " + j.expeditions.size() + " expeditions. Use /shadow adventure to open your journal.");
            return 1;
        }));
        for (Map.Entry<String, AdventureNetwork.Action> entry : Map.of("escort", AdventureNetwork.Action.ESCORT, "resume", AdventureNetwork.Action.RESUME, "pause", AdventureNetwork.Action.PAUSE, "cancel", AdventureNetwork.Action.CANCEL_PHYSICAL, "claim", AdventureNetwork.Action.CLAIM_PHYSICAL, "hunt", AdventureNetwork.Action.HUNT, "claim_hunt", AdventureNetwork.Action.CLAIM_HUNT, "kin", AdventureNetwork.Action.NEMESIS_KIN, "claim_nemesis", AdventureNetwork.Action.CLAIM_NEMESIS, "dismiss_nemesis", AdventureNetwork.Action.DISMISS_NEMESIS).entrySet()) {
            root.then(AdventureCommands.node(entry.getKey()).executes(c -> AdventureCommands.run((CommandContext<CommandSourceStack>)c, (AdventureNetwork.Action)((Object)((Object)((Object)entry.getValue()))), null, 0, null, List.of())));
        }
        String[] tiers = new String[]{"scout", "veteran", "monarch"};
        for (int i = 0; i < 3; ++i) {
            int n = i;
            root.then(AdventureCommands.node("trial").then(AdventureCommands.node(tiers[i]).executes(c -> AdventureCommands.run((CommandContext<CommandSourceStack>)c, AdventureNetwork.Action.TRIAL, null, tier, null, List.of()))));
        }
        for (AdventureData.ContractKind k : AdventureData.ContractKind.values()) {
            root.then(AdventureCommands.node("contract").then(AdventureCommands.node(k.name().toLowerCase(Locale.ROOT)).executes(c -> AdventureCommands.run((CommandContext<CommandSourceStack>)c, AdventureNetwork.Action.ACCEPT_CONTRACT, null, k.ordinal(), null, List.of()))));
        }
        for (Map.Entry entry : Map.of("claim_expedition", AdventureNetwork.Action.CLAIM_EXPEDITION, "cancel_expedition", AdventureNetwork.Action.CANCEL_EXPEDITION, "claim_contract", AdventureNetwork.Action.CLAIM_CONTRACT, "abandon_contract", AdventureNetwork.Action.ABANDON_CONTRACT, "claim_cache", AdventureNetwork.Action.CLAIM_CACHE).entrySet()) {
            root.then(AdventureCommands.node((String)entry.getKey()).then(Commands.m_82129_((String)"id", (ArgumentType)UuidArgument.m_113850_()).executes(c -> AdventureCommands.run((CommandContext<CommandSourceStack>)c, (AdventureNetwork.Action)((Object)((Object)((Object)entry.getValue()))), UuidArgument.m_113853_((CommandContext)c, (String)"id"), 0, null, List.of()))));
        }
        root.then(AdventureCommands.node("bond").then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> AdventureCommands.run((CommandContext<CommandSourceStack>)c, AdventureNetwork.Action.BOND, null, 0, AdventureCommands.slot((CommandContext<CommandSourceStack>)c), List.of()))));
        AdventureCommands.equip((LiteralArgumentBuilder<CommandSourceStack>)root, "relic", AdventureNetwork.Action.RELIC, AdventureData.Relic.values());
        AdventureCommands.equip((LiteralArgumentBuilder<CommandSourceStack>)root, "trail", AdventureNetwork.Action.TRAIL, AdventureData.Trail.values());
        AdventureCommands.equip((LiteralArgumentBuilder<CommandSourceStack>)root, "title", AdventureNetwork.Action.TITLE, AdventureData.Title.values());
        root.then(AdventureCommands.node("dispatch").then(Commands.m_82129_((String)"tier", (ArgumentType)IntegerArgumentType.integer((int)1, (int)3)).then(Commands.m_82129_((String)"slots", (ArgumentType)StringArgumentType.greedyString()).executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            ArrayList<ShadowRecord> records = new ArrayList<ShadowRecord>(ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values());
            ArrayList<UUID> ids = new ArrayList<UUID>();
            try {
                String[] slots = StringArgumentType.getString((CommandContext)c, (String)"slots").trim().split("[ ,]+");
                if (slots.length > 3) {
                    throw new IllegalArgumentException();
                }
                for (String text : slots) {
                    int index = Integer.parseInt(text) - 1;
                    if (index < 0 || index >= records.size()) {
                        throw new IllegalArgumentException();
                    }
                    ids.add(records.get((int)index).id);
                }
            }
            catch (IllegalArgumentException ex) {
                AdventureService.tell(p, "Choose 1\u20133 existing reserve slots, for example /shadow adventure dispatch 1 2 3.");
                return 0;
            }
            return AdventureCommands.run((CommandContext<CommandSourceStack>)c, AdventureNetwork.Action.DISPATCH, null, IntegerArgumentType.getInteger((CommandContext)c, (String)"tier") - 1, null, ids);
        }))));
        event.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)AdventureCommands.node("shadow").requires(s -> s.m_81373_() instanceof ServerPlayer)).then((ArgumentBuilder)root));
    }

    private static void equip(LiteralArgumentBuilder<CommandSourceStack> root, String name, AdventureNetwork.Action action, Enum<?>[] values) {
        RequiredArgumentBuilder slot = Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1));
        for (Enum<?> value : values) {
            slot.then(AdventureCommands.node(value.name().toLowerCase(Locale.ROOT)).executes(c -> AdventureCommands.run((CommandContext<CommandSourceStack>)c, action, null, value.ordinal(), AdventureCommands.slot((CommandContext<CommandSourceStack>)c), List.of())));
        }
        root.then(AdventureCommands.node(name).then((ArgumentBuilder)slot));
    }

    private static UUID slot(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
        ArrayList<ShadowRecord> records = new ArrayList<ShadowRecord>(ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.values());
        int index = IntegerArgumentType.getInteger(c, (String)"slot") - 1;
        return index >= 0 && index < records.size() ? records.get((int)index).id : new UUID(0L, 0L);
    }

    private static int run(CommandContext<CommandSourceStack> c, AdventureNetwork.Action action, UUID target, int value, UUID companion, List<UUID> members) throws CommandSyntaxException {
        ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
        int result = AdventureNetwork.perform(p, new AdventureNetwork.Request(action, target, value, members, companion, 0));
        AdventureNetwork.send(p, false, companion, 0);
        if (result == 0) {
            AdventureService.tell(p, "Adventure action unavailable. Check your journal, ownership, level and Sanctuary distance.");
        }
        return result;
    }
}

