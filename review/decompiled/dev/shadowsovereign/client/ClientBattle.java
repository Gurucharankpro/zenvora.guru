/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.CommandWheelScreen;
import dev.shadowsovereign.network.BattleNetwork;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ClientBattle {
    public static final KeyMapping WHEEL = new KeyMapping("key.shadowsovereign.wheel", InputConstants.Type.KEYSYM, 86, "key.categories.shadowsovereign");
    public static BattleNetwork.State state = ClientBattle.empty();
    private static long ticks;
    private static long received;

    private static BattleNetwork.State empty() {
        return new BattleNetwork.State(List.of(), BattleRules.Squad.ALL, Map.of(BattleRules.Skill.STEP, 0L, BattleRules.Skill.TAUNT, 0L, BattleRules.Skill.BARRIER, 0L, BattleRules.Skill.MEND, 0L), 0L);
    }

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent event) {
        event.register(WHEEL);
    }

    public static void accept(BattleNetwork.State next) {
        state = next;
        received = ticks;
    }

    public static long now() {
        return state.gameTime() + ticks - received;
    }

    public static int seconds(BattleRules.Skill skill) {
        return (int)Math.max(0L, (state.readyAt().getOrDefault((Object)skill, 0L) - ClientBattle.now() + 19L) / 20L);
    }

    public static BattleNetwork.Entry entry(UUID id) {
        return state.entries().stream().filter(e -> e.id().equals(id)).findFirst().orElse(null);
    }

    public static long eligible(BattleRules.Skill skill, BattleRules.Squad scope, UUID id) {
        return ClientArmy.view.entries().stream().filter(e -> e.active() && e.health() > 0.0f && e.level() >= skill.level && (id == null || e.id().equals(id))).filter(e -> {
            BattleNetwork.Entry b = ClientBattle.entry(e.id());
            return b != null && b.nearby() && (id != null || scope == BattleRules.Squad.ALL || b.squad() == scope);
        }).count();
    }

    public static long active(BattleRules.Squad scope) {
        return ClientArmy.view.entries().stream().filter(e -> e.active()).filter(e -> scope == BattleRules.Squad.ALL || ClientBattle.entry(e.id()) != null && ClientBattle.entry(e.id()).squad() == scope).count();
    }

    public static void send(BattleNetwork.Action action, BattleRules.Squad scope, UUID id, int value) {
        if (Minecraft.m_91087_().f_91074_ != null) {
            BattleNetwork.request(action, scope, id, value);
        }
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
    public static final class BattleInputEvents {
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            ++ticks;
            Minecraft mc = Minecraft.m_91087_();
            while (WHEEL.m_90859_()) {
                if (mc.f_91074_ == null || mc.f_91080_ != null) continue;
                ShadowNetwork.request(ShadowNetwork.Action.REFRESH, null);
                mc.m_91152_((Screen)new CommandWheelScreen());
            }
        }

        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
            state = ClientBattle.empty();
            ticks = 0L;
            received = 0L;
        }
    }
}

