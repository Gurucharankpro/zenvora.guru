/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.LegacyScreen;
import dev.shadowsovereign.network.LegacyNetwork;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientLegacy {
    public static CompoundTag data = new CompoundTag();
    public static UUID selected;
    private static long ticks;
    private static long received;
    private static final Set<UUID> favorites;

    private ClientLegacy() {
    }

    public static void accept(LegacyNetwork.View view) {
        Screen screen;
        data = view.data().m_6426_();
        received = ticks;
        favorites.clear();
        for (Tag tag : data.m_128437_("Roster", 10)) {
            CompoundTag n = (CompoundTag)tag;
            if (!n.m_128403_("Id") || !n.m_128471_("Favorite")) continue;
            favorites.add(n.m_128342_("Id"));
        }
        Minecraft mc = Minecraft.m_91087_();
        if (view.open() && mc.f_91074_ != null && !(mc.f_91080_ instanceof LegacyScreen)) {
            mc.m_91152_((Screen)new LegacyScreen(selected));
        }
        if ((screen = mc.f_91080_) instanceof LegacyScreen) {
            LegacyScreen screen2 = (LegacyScreen)screen;
            screen2.updated();
            if (view.open()) {
                screen2.show(ClientLegacy.AdventureDataReadPage(data.m_128461_("OpenPage")));
            }
        }
    }

    private static LegacyScreen.Page AdventureDataReadPage(String text) {
        try {
            return LegacyScreen.Page.valueOf(text);
        }
        catch (IllegalArgumentException e) {
            return LegacyScreen.Page.POWERS;
        }
    }

    public static boolean favorite(UUID id) {
        return favorites.contains(id);
    }

    public static long now() {
        return data.m_128454_("Time") + ticks - received;
    }

    public static void send(LegacyNetwork.Request request) {
        if (Minecraft.m_91087_().f_91074_ != null) {
            LegacyNetwork.request(request);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            ++ticks;
        }
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        data = new CompoundTag();
        selected = null;
        favorites.clear();
        received = 0L;
        ticks = 0L;
    }

    static {
        favorites = new HashSet<UUID>();
    }
}

