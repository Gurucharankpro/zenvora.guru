package dev.shadowsovereign.expansion;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.MissingMappingsEvent;

/** Forge documents MissingMappingsEvent on the Forge bus. Ignore only the
 * explicitly removed knight identities, leaving all unrelated missing data to
 * the normal Forge handling. */
@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class MigrationHooks {
    private MigrationHooks() {}
    @SubscribeEvent
    @SuppressWarnings({"rawtypes","unchecked"})
    public static void missing(MissingMappingsEvent event) {
        for(Object item:event.getMappings((ResourceKey)event.getKey(),"shadowsovereign")){
            MissingMappingsEvent.Mapping<?> mapping=(MissingMappingsEvent.Mapping<?>)item;
            ResourceLocation key=mapping.getKey();
            if(!key.m_135827_().equals("shadowsovereign"))continue;
            String path=key.m_135815_();
            if(path.equals("villager_knight") || path.equals("villager_knight_spawn_egg") || path.startsWith("knighthaven_knight_"))mapping.ignore();
        }
    }
}
