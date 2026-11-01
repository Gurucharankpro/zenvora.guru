/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.MissingMappingsEvent
 *  net.minecraftforge.registries.MissingMappingsEvent$Mapping
 */
package dev.shadowsovereign.expansion;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.MissingMappingsEvent;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class MigrationHooks {
    private MigrationHooks() {
    }

    @SubscribeEvent
    public static void missing(MissingMappingsEvent missingMappingsEvent) {
        for (Object e : missingMappingsEvent.getMappings(missingMappingsEvent.getKey(), "shadowsovereign")) {
            String string;
            MissingMappingsEvent.Mapping mapping = (MissingMappingsEvent.Mapping)e;
            ResourceLocation resourceLocation = mapping.getKey();
            if (!resourceLocation.m_135827_().equals("shadowsovereign") || !(string = resourceLocation.m_135815_()).equals("villager_knight") && !string.equals("villager_knight_spawn_egg") && !string.startsWith("knighthaven_knight_")) continue;
            mapping.ignore();
        }
    }
}

