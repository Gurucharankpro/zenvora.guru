/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.sentinel;

import dev.shadowsovereign.sentinel.SentinelContent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class SentinelEvents {
    @SubscribeEvent
    public static void broken(BlockEvent.BreakEvent e) {
        Player player = e.getPlayer();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            player = e.getLevel().m_7702_(e.getPos());
            if (player instanceof SentinelContent.Node) {
                SentinelContent.Node n = (SentinelContent.Node)player;
                if (n.owner != null && !n.owner.equals(p.m_20148_()) && !p.m_20310_(2)) {
                    e.setCanceled(true);
                }
            }
        }
    }
}

