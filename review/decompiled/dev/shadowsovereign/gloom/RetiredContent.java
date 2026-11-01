/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.gloom;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.odyssey.OdysseyState;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class RetiredContent {
    private static final Set<String> ITEMS = Set.of("soul_playball", "festival_ball", "festival_parcel", "odyssey_artifact_5", "odyssey_artifact_11", "odyssey_artifact_17", "odyssey_artifact_23");

    public static boolean item(ResourceLocation id) {
        return id != null && id.m_135827_().equals("shadowsovereign") && (ITEMS.contains(id.m_135815_()) || id.m_135815_().matches("odyssey_artifact_(4|5|10|11|16|17|22|23)"));
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            AdventureData.Session s;
            ServerPlayer p = (ServerPlayer)player;
            OdysseyState state = GuideService.state(p);
            if (state.artifact >= 0 && state.artifact % 6 >= 4) {
                state.artifact = -1;
                GuideService.dirty(p);
            }
            if ((s = OdysseyRuns.session(p)) != null && OdysseyRuns.mode(s) != null && OdysseyRuns.mode(s).retired()) {
                OdysseyRuns.finish(p, false);
            }
        }
    }
}

