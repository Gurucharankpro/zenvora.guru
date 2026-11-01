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
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
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
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.network.WarfrontNetwork;
import dev.shadowsovereign.warfront.WarfrontPlans;
import dev.shadowsovereign.warfront.WarfrontService;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class WarfrontCommands {
    private static UUID id(ServerPlayer p, int slot) {
        ArrayList<UUID> list = new ArrayList<UUID>(ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.keySet());
        return slot > 0 && slot <= list.size() ? list.get(slot - 1) : null;
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder shadow = (LiteralArgumentBuilder)Commands.m_82127_((String)"shadow").requires(c -> c.m_81373_() instanceof ServerPlayer);
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)Commands.m_82127_((String)"warfront").executes(c -> {
            WarfrontNetwork.send(((CommandSourceStack)c.getSource()).m_81375_(), true, null);
            return 1;
        });
        root.then(Commands.m_82127_((String)"bind").executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            Vec3 from = p.m_146892_();
            BlockHitResult hit = p.m_9236_().m_45547_(new ClipContext(from, from.m_82549_(p.m_20154_().m_82490_(8.0)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)p));
            return hit.m_6662_() == HitResult.Type.BLOCK && WarfrontService.bind(p, hit.m_82425_()) ? 1 : 0;
        }));
        for (Map.Entry<String, WarfrontNetwork.Action> entry : Map.of("pause", WarfrontNetwork.Action.PAUSE, "resume", WarfrontNetwork.Action.RESUME, "abandon", WarfrontNetwork.Action.ABANDON, "claim", WarfrontNetwork.Action.CLAIM).entrySet()) {
            root.then(Commands.m_82127_((String)entry.getKey()).executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), (WarfrontNetwork.Action)((Object)((Object)entry.getValue())), 0, false, null)));
        }
        LiteralArgumentBuilder start = (LiteralArgumentBuilder)Commands.m_82127_((String)"start").then(((RequiredArgumentBuilder)Commands.m_82129_((String)"territory", (ArgumentType)IntegerArgumentType.integer((int)1, (int)6)).executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), WarfrontNetwork.Action.START, IntegerArgumentType.getInteger((CommandContext)c, (String)"territory") - 1, false, null))).then(Commands.m_82127_((String)"challenging").executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), WarfrontNetwork.Action.START, IntegerArgumentType.getInteger((CommandContext)c, (String)"territory") - 1, true, null))));
        root.then((ArgumentBuilder)start);
        root.then(((LiteralArgumentBuilder)Commands.m_82127_((String)"next").executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), WarfrontNetwork.Action.NEXT, 0, false, null))).then(Commands.m_82127_((String)"confirm").executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), WarfrontNetwork.Action.NEXT, 0, true, null))));
        shadow.then((ArgumentBuilder)root);
        shadow.then(Commands.m_82127_((String)"art").then(((RequiredArgumentBuilder)Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            UUID id = WarfrontCommands.id(p, IntegerArgumentType.getInteger((CommandContext)c, (String)"slot"));
            return id == null ? 0 : WarfrontCommands.perform(p, WarfrontNetwork.Action.CAST, 0, false, id);
        })).then(Commands.m_82129_((String)"art", (ArgumentType)StringArgumentType.word()).suggests((c, b) -> {
            for (WarfrontState.Art value : WarfrontState.Art.values()) {
                b.suggest(value.name().toLowerCase(Locale.ROOT));
            }
            return b.buildFuture();
        }).executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            UUID id = WarfrontCommands.id(p, IntegerArgumentType.getInteger((CommandContext)c, (String)"slot"));
            try {
                return id == null ? 0 : WarfrontCommands.perform(p, WarfrontNetwork.Action.SET_ART, WarfrontState.Art.valueOf(StringArgumentType.getString((CommandContext)c, (String)"art").toUpperCase(Locale.ROOT)).ordinal(), false, id);
            }
            catch (IllegalArgumentException e) {
                return 0;
            }
        }))));
        shadow.then(Commands.m_82127_((String)"rune").then(Commands.m_82129_((String)"slot", (ArgumentType)IntegerArgumentType.integer((int)1)).then(Commands.m_82129_((String)"rune", (ArgumentType)StringArgumentType.word()).suggests((c, b) -> {
            for (WarfrontState.Rune value : WarfrontState.Rune.values()) {
                b.suggest(value.name().toLowerCase(Locale.ROOT));
            }
            return b.buildFuture();
        }).executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            UUID id = WarfrontCommands.id(p, IntegerArgumentType.getInteger((CommandContext)c, (String)"slot"));
            try {
                return id == null ? 0 : WarfrontCommands.perform(p, WarfrontNetwork.Action.SET_RUNE, WarfrontState.Rune.valueOf(StringArgumentType.getString((CommandContext)c, (String)"rune").toUpperCase(Locale.ROOT)).ordinal(), false, id);
            }
            catch (IllegalArgumentException e) {
                return 0;
            }
        }))));
        LiteralArgumentBuilder plan = Commands.m_82127_((String)"plan");
        plan.then(Commands.m_82127_((String)"marker").executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), WarfrontNetwork.Action.MARKER, 0, false, null)));
        plan.then(Commands.m_82127_((String)"stop").executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), WarfrontNetwork.Action.PLAN_STOP, 0, false, null)));
        plan.then(((RequiredArgumentBuilder)Commands.m_82129_((String)"plan", (ArgumentType)IntegerArgumentType.integer((int)1, (int)3)).then(Commands.m_82127_((String)"run").executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), WarfrontNetwork.Action.PLAN_RUN, IntegerArgumentType.getInteger((CommandContext)c, (String)"plan") - 1, false, null)))).then(Commands.m_82127_((String)"save").then(Commands.m_82129_((String)"steps", (ArgumentType)StringArgumentType.greedyString()).executes(c -> {
            ServerPlayer p = ((CommandSourceStack)c.getSource()).m_81375_();
            try {
                List<WarfrontState.Step> steps = Arrays.stream(StringArgumentType.getString((CommandContext)c, (String)"steps").trim().split("\\s+")).map(s -> WarfrontState.Step.valueOf(s.toUpperCase(Locale.ROOT))).toList();
                boolean okay = WarfrontPlans.save(p, IntegerArgumentType.getInteger((CommandContext)c, (String)"plan") - 1, steps);
                WarfrontNetwork.send(p, false, null);
                return okay ? 1 : 0;
            }
            catch (IllegalArgumentException e) {
                return 0;
            }
        }))));
        shadow.then((ArgumentBuilder)plan);
        shadow.then(Commands.m_82127_((String)"pact").then(((RequiredArgumentBuilder)Commands.m_82129_((String)"rival", (ArgumentType)IntegerArgumentType.integer((int)1, (int)3)).then(Commands.m_82127_((String)"start").executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), WarfrontNetwork.Action.PACT_START, IntegerArgumentType.getInteger((CommandContext)c, (String)"rival") - 1, false, null)))).then(Commands.m_82127_((String)"claim").executes(c -> WarfrontCommands.perform(((CommandSourceStack)c.getSource()).m_81375_(), WarfrontNetwork.Action.PACT_CLAIM, IntegerArgumentType.getInteger((CommandContext)c, (String)"rival") - 1, false, null)))));
        event.getDispatcher().register(shadow);
    }

    private static int perform(ServerPlayer p, WarfrontNetwork.Action action, int value, boolean flag, UUID id) {
        int result = WarfrontNetwork.perform(p, new WarfrontNetwork.Request(action, value, flag, id, List.of(), List.of()));
        if (result == 0) {
            WarfrontService.tell(p, "Action unavailable; check the Warfront menu requirements.");
        }
        WarfrontNetwork.send(p, false, id);
        return result;
    }
}

