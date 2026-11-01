package dev.shadowsovereign.content;
import dev.shadowsovereign.adventure.AdventureService;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
/** Resolve real structure bounds on the server; never generate chunks for ambience. */
public final class AtmosphereServer {
 private static final String[][] GROUPS={
  {"legends_paper_shrine","expansion_shattered_porcelain_temple","expansion_temple_of_thousand_mirrors"},
  {"legends_emperor_palace","expansion_palace_of_silent_duchess","expansion_el_nemr_mansion"},
  {"chronicles_buried_belfry","expansion_cursed_bell_tower","mastery_plague_hospital"},
  {"moonveil_moon_burrow_village","crimson_akatsuki_shadow_hideout","expansion_morsi_mobile_shop"}
 };
 public static int profile(ServerPlayer p){
  if(!p.level().dimension().location().equals(new ResourceLocation("shadowsovereign","echo_realms")))return 0;
  if(AdventureService.journal(p).mythic.bar!=null)return 5;
  var registry=p.serverLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);
  for(int i=0;i<GROUPS.length;i++)for(String name:GROUPS[i]){
   var structure=registry.get(new ResourceLocation("shadowsovereign",name));
   if(structure!=null&&p.serverLevel().structureManager().getStructureAt(p.blockPosition(),structure).isValid())return i+1;
  }return 0;
 }
}
