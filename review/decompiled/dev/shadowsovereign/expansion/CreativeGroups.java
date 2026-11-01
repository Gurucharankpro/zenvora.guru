/*
 * Decompiled with CFR 0.152.
 */
package dev.shadowsovereign.expansion;

import java.util.Set;

public final class CreativeGroups {
    static final Set<String> DECOR = Set.of("decor_soulfire_brazier", "decor_chained_spirit_pillar", "decor_summoning_circle_relic", "decor_broken_beast_idol", "decor_skull_spike_totem", "decor_hanging_soul_lantern", "decor_cursed_war_banner", "decor_bone_heap", "decor_soul_crystal_cluster", "decor_ritual_offering_table", "decor_cracked_obsidian_obelisk", "decor_rusted_spirit_cage", "decor_ancient_drum_ruin", "decor_moonstone_grave_marker", "decor_chained_arch_fragment", "decor_spectral_torch_stand", "decor_bone_fence_segment", "decor_soul_vessel_jar", "decor_ruined_stone_throne", "decor_floating_rune_ornament", "reaper_four_armed_reaper_statue", "reaper_reaper_throne", "reaper_soul_harvest_altar", "reaper_giant_scythe_monument", "reaper_hollow_spirit_lantern", "reaper_four_handed_candelabrum", "reaper_reapers_coffin", "reaper_chained_soul_obelisk", "reaper_cursed_hourglass", "reaper_spectral_grave_cross", "reaper_soul_collector_urn", "reaper_hanging_scythe_chandelier", "reaper_reaper_wall_banner", "reaper_ancient_bone_arch", "reaper_forgotten_funeral_bell", "reaper_soul_mirror", "reaper_reapers_book_pedestal", "reaper_eternal_mourning_fountain", "reaper_floating_soul_reliquary", "reaper_last_harvest_clock", "devourer_soul_devourer_monument", "devourer_grand_soul_prison_cage", "devourer_devourers_throne", "devourer_six_legged_guardian_totem", "devourer_abyssal_soul_altar", "devourer_caged_soul_lantern", "devourer_devourers_jaw_arch", "devourer_soul_extraction_obelisk", "devourer_ancient_beast_skull_relic", "devourer_six_claw_chandelier", "devourer_imprisoned_star_crystal", "devourer_devourers_war_banner", "devourer_soul_cage_sarcophagus", "devourer_abyssal_spine_pillar", "devourer_forgotten_feeding_basin", "devourer_soulbound_chain_rack", "devourer_devourers_ritual_brazier", "devourer_cursed_beast_footprint", "devourer_eternal_prison_gate", "devourer_heart_of_the_devourer", "butcher_cursed_butcher_statue", "butcher_abandoned_butchers_counter", "butcher_gravekeepers_soul_lantern", "butcher_haunted_gravekeeper_monument", "butcher_cursed_scarecrow", "butcher_haunted_harvest_wagon", "butcher_plague_doctor_display_statue", "butcher_forbidden_alchemy_cabinet", "butcher_executioners_axe_monument", "butcher_executioners_iron_throne", "butcher_haunted_marionette_display", "butcher_clockwork_puppet_workbench", "butcher_grand_cursed_bell", "butcher_soul_resonance_pillar", "butcher_supreme_butcher_war_statue", "butcher_crimson_overlord_throne", "butcher_black_stag_mask_monument", "butcher_haunted_hunters_weapon_rack", "butcher_gentlemans_tea_table", "butcher_secret_overlords_portrait", "devourer_v2_abyssal_soul_altar", "devourer_v2_abyssal_spine_pillar", "devourer_v2_ancient_beast_skull_relic", "devourer_v2_caged_soul_lantern", "devourer_v2_cursed_beast_footprint", "devourer_v2_devourers_jaw_arch", "devourer_v2_devourers_ritual_brazier", "devourer_v2_devourers_throne", "devourer_v2_devourers_war_banner", "devourer_v2_eternal_prison_gate", "devourer_v2_forgotten_feeding_basin", "devourer_v2_grand_soul_prison_cage", "devourer_v2_heart_of_the_devourer", "devourer_v2_imprisoned_star_crystal", "devourer_v2_six_claw_chandelier", "devourer_v2_six_legged_guardian_totem", "devourer_v2_soul_cage_sarcophagus", "devourer_v2_soul_devourer_monument", "devourer_v2_soul_extraction_obelisk", "devourer_v2_soulbound_chain_rack", "relic_crimson_raven_nest", "relic_fallen_blood_moon_statue", "relic_akatsuki_shadow_throne", "relic_forgotten_ninja_weapon_display", "relic_moonlit_fairy_fountain", "relic_sleeping_moon_fox_statue", "relic_moonveil_crystal_bed", "relic_forgotten_lunar_altar", "relic_fallen_sky_anchor", "relic_celestial_wind_chimes", "relic_starwatch_telescope", "relic_sky_guardian_monument", "relic_abyssal_crystal_skeleton", "relic_giant_bioluminescent_spore_pod", "relic_emerald_cathedral_organ", "relic_ancient_miners_crystal_drill", "relic_giant_pearl_clam", "relic_sunken_mermaid_fountain", "relic_royal_tide_throne", "relic_ancient_ocean_navigators_table");

    public static int category(String string, int n, boolean bl) {
        if (string.equals("x1_visual")) {
            return -1;
        }
        if (DECOR.contains(string)) {
            return 2;
        }
        if (n == 1) {
            return 1;
        }
        if (n == 3 || string.equals("monolith_enchanting_table") || bl && string.startsWith("eclipse_")) {
            return 3;
        }
        return 0;
    }
}

