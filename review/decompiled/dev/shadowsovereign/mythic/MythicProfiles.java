/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 */
package dev.shadowsovereign.mythic;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class MythicProfiles {
    public static final List<Profile> ALL;
    private static final Map<String, Map<String, Float>> LENGTHS;

    private static Move m(String c, float t, int e, float r, float p) {
        return new Move(c, t, e, r, p);
    }

    public static float length(String id, String clip) {
        Map map = LENGTHS.computeIfAbsent(id, key -> {
            try (InputStream in = MythicProfiles.class.getResourceAsStream("/assets/shadowsovereign/mythic/" + key + ".json");){
                Map map;
                try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);){
                    HashMap out = new HashMap();
                    JsonParser.parseReader((Reader)reader).getAsJsonObject().getAsJsonObject("animations").entrySet().forEach(e -> out.put((String)e.getKey(), Float.valueOf(((JsonElement)e.getValue()).getAsJsonObject().get("length").getAsFloat())));
                    map = Map.copyOf(out);
                }
                return map;
            }
            catch (IOException ex) {
                throw new IllegalStateException("Missing authored animation " + key, ex);
            }
        });
        return map.getOrDefault(clip, Float.valueOf(1.0f)).floatValue();
    }

    public static boolean has(String id, String clip) {
        MythicProfiles.length(id, clip);
        return LENGTHS.get(id).containsKey(clip);
    }

    public static Profile get(int kind) {
        return ALL.get(kind);
    }

    static {
        ArrayList<Profile> arrayList = new ArrayList<Profile>(List.of(new Profile("umbral_wyngcat", 160.0, 8.0, 2.0, 0.3, 1.2f, 1.4f, 1.25f, "walk", "run", false, true, List.of(MythicProfiles.m("claw_attack_left", 0.45f, 0, 3.0f, 1.0f), MythicProfiles.m("wing_slash", 0.6f, 1, 3.0f, 0.8f), MythicProfiles.m("magical_hiss", 0.7f, 3, 14.0f, 0.75f))), new Profile("crimson_basilisk", 500.0, 16.0, 10.0, 0.23, 2.4f, 2.6f, 0.8f, "walk_normal", "run", false, false, List.of(MythicProfiles.m("bite_attack", 0.5f, 0, 5.0f, 1.0f), MythicProfiles.m("tail_sweep", 0.6f, 1, 5.0f, 0.8f), MythicProfiles.m("petrifying_gaze_release", 0.6f, 3, 14.0f, 0.75f))), new Profile("eclipse_bloom", 360.0, 10.0, 4.0, 0.2, 2.2f, 3.6f, 0.65f, "walk_normal", "walk_fast", false, false, List.of(MythicProfiles.m("vine_whip_left", 0.45f, 0, 5.0f, 1.0f), MythicProfiles.m("root_slam", 0.7f, 1, 5.0f, 0.8f), MythicProfiles.m("magical_eye_beam", 0.7f, 6, 14.0f, 0.75f))), new Profile("ruin_stalker", 240.0, 12.0, 6.0, 0.32, 1.0f, 1.6f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("bite_attack", 0.42f, 0, 3.2f, 1.0f), MythicProfiles.m("claw_attack", 0.38f, 1, 3.2f, 0.8f), MythicProfiles.m("tail_attack", 0.55f, 1, 4.0f, 0.8f))), new Profile("weeping_puppeteer", 300.0, 12.0, 4.0, 0.24, 0.8f, 3.2f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("puppet_strike", 0.55f, 0, 4.0f, 1.0f), MythicProfiles.m("crimson_threads", 1.0f, 3, 8.0f, 0.7f), MythicProfiles.m("puppet_control", 1.4f, 2, 12.0f, 0.8f))), new Profile("forgotten_astronomer", 340.0, 14.0, 4.0, 0.24, 0.75f, 2.4f, 0.9f, "walk", "run", false, false, List.of(MythicProfiles.m("celestial_strike", 0.5f, 0, 4.0f, 1.0f), MythicProfiles.m("gravitational_command", 1.0f, 2, 8.0f, 0.7f), MythicProfiles.m("eclipse_charge", 1.4f, 6, 12.0f, 0.8f))), new Profile("crownless_sovereign", 650.0, 20.0, 12.0, 0.23, 0.95f, 3.2f, 0.95f, "walk", "run", false, false, List.of(MythicProfiles.m("sovereign_strike", 0.55f, 0, 4.0f, 1.0f), MythicProfiles.m("royal_decree", 1.0f, 1, 6.0f, 0.7f), MythicProfiles.m("throne_manifest", 1.4f, 7, 10.0f, 0.8f))), new Profile("eclipse_stag", 280.0, 13.0, 6.0, 0.32, 1.6f, 3.0f, 0.75f, "walk", "run", false, false, List.of(MythicProfiles.m("antler_attack", 0.4f, 0, 4.0f, 1.0f), MythicProfiles.m("antler_attack", 0.4f, 1, 5.0f, 0.7f), MythicProfiles.m("celestial_charge", 1.0f, 8, 10.0f, 0.8f))), new Profile("reliquary_crawler", 720.0, 16.0, 14.0, 0.17, 1.8f, 4.8f, 0.65f, "crawl", "fast_crawl", false, false, List.of(MythicProfiles.m("head_strike", 0.55f, 0, 4.0f, 1.0f), MythicProfiles.m("shell_guard", 0.25f, 5, 0.0f, 0.0f), MythicProfiles.m("cathedral_charge", 1.3f, 6, 10.0f, 1.1f))), new Profile("infernal_phoenix", 320.0, 16.0, 4.0, 0.3, 1.2f, 2.2f, 0.9f, "walk", "walk", true, true, List.of(MythicProfiles.m("claw_attack", 0.35f, 0, 4.0f, 1.0f), MythicProfiles.m("flame_attack", 0.8f, 4, 12.0f, 1.1f))), new Profile("pain", 480.0, 20.0, 6.0, 0.29, 0.75f, 2.8f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("rod_attack", 0.5f, 0, 4.0f, 1.0f), MythicProfiles.m("shinra_tensei", 0.9f, 1, 7.0f, 1.0f), MythicProfiles.m("bansho_tenin", 0.7f, 2, 12.0f, 0.7f), MythicProfiles.m("chibaku_tensei", 1.2f, 9, 8.0f, 1.2f))), new Profile("grieving_seraph", 400.0, 16.0, 6.0, 0.28, 0.8f, 2.8f, 1.0f, "walk", "run", true, false, List.of(MythicProfiles.m("spear_attack", 0.45f, 0, 4.5f, 1.0f), MythicProfiles.m("celestial_charge", 0.9f, 6, 10.0f, 1.1f))), new Profile("ashen_juggernaut", 800.0, 20.0, 16.0, 0.22, 1.5f, 3.6f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("hammer_slam", 0.8f, 0, 4.0f, 1.2f), MythicProfiles.m("sweeping_attack", 0.65f, 1, 5.0f, 0.9f), MythicProfiles.m("defensive_stance", 0.2f, 5, 0.0f, 0.0f))), new Profile("paper_kunoichi", 360.0, 14.0, 4.0, 0.29, 0.75f, 2.5f, 0.9f, "walk", "run", true, false, List.of(MythicProfiles.m("paper_shuriken_attack", 0.65f, 10, 14.0f, 0.9f), MythicProfiles.m("paper_spear_attack", 1.4f, 0, 5.0f, 1.1f), MythicProfiles.m("paper_storm_attack", 2.65f, 11, 7.0f, 1.1f), MythicProfiles.m("paper_shield", 0.8f, 5, 0.0f, 0.0f))), new Profile("hollow_jester", 400.0, 14.0, 4.0, 0.3, 0.75f, 2.6f, 0.95f, "walk", "run", false, false, List.of(MythicProfiles.m("card_throw", 0.65f, 10, 14.0f, 1.0f), MythicProfiles.m("mask_switch", 0.85f, 12, 7.0f, 0.5f), MythicProfiles.m("card_shuffle", 1.2f, 13, 14.0f, 0.8f), MythicProfiles.m("jester_bow", 0.8f, 5, 0.0f, 0.0f))), new Profile("chained_emperor", 720.0, 20.0, 12.0, 0.22, 1.0f, 3.5f, 0.9f, "walk", "run", false, false, List.of(MythicProfiles.m("scepter_strike", 0.8f, 0, 4.5f, 1.0f), MythicProfiles.m("chain_attack", 1.1f, 2, 9.0f, 0.8f), MythicProfiles.m("royal_command", 1.6f, 15, 8.0f, 0.8f), MythicProfiles.m("throne_guard", 0.6f, 5, 0.0f, 0.0f))), new Profile("madara_uchiha", 640.0, 22.0, 10.0, 0.28, 0.85f, 2.6f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("gunbai_attack", 0.9f, 0, 4.5f, 1.0f), MythicProfiles.m("fire_release", 2.5f, 4, 14.0f, 1.15f), MythicProfiles.m("susanoo_activate", 1.8f, 5, 0.0f, 0.0f), MythicProfiles.m("susanoo_strike", 1.5f, 16, 8.0f, 1.5f))), new Profile("sasori", 480.0, 18.0, 8.0, 0.27, 0.85f, 2.5f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("blade_attack", 1.1f, 0, 4.2f, 1.0f), MythicProfiles.m("tail_attack", 1.1f, 17, 6.0f, 0.9f), MythicProfiles.m("puppet_attack", 1.6f, 18, 10.0f, 1.0f), MythicProfiles.m("puppet_control", 1.5f, 2, 10.0f, 0.5f), MythicProfiles.m("puppet_defense", 0.8f, 5, 0.0f, 0.0f))), new Profile("fallen_penitent", 400.0, 16.0, 4.0, 0.26, 0.75f, 2.5f, 1.0f, "walk_slow", "run_unstable", false, false, List.of(MythicProfiles.m("stabbing_attack", 0.55f, 0, 4.0f, 1.0f), MythicProfiles.m("knife_slash_left", 0.45f, 1, 4.5f, 0.8f), MythicProfiles.m("desperate_lunge", 0.65f, 19, 6.0f, 1.15f), MythicProfiles.m("grab_attack", 0.6f, 2, 5.0f, 0.65f))), new Profile("rainbound_oracle", 420.0, 15.0, 6.0, 0.25, 0.8f, 2.7f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("umbrella_strike", 0.65f, 0, 4.5f, 1.0f), MythicProfiles.m("rainfall_charge", 1.65f, 20, 10.0f, 0.85f), MythicProfiles.m("rain_barrier", 0.7f, 25, 6.0f, 0.0f), MythicProfiles.m("umbrella_open", 0.65f, 5, 0.0f, 0.0f))), new Profile("dread_bellkeeper", 800.0, 20.0, 14.0, 0.2, 3.2f, 3.2f, 0.8f, "walk", "run", false, false, List.of(MythicProfiles.m("leg_attack", 0.65f, 0, 5.0f, 1.0f), MythicProfiles.m("heavy_stomp", 0.9f, 1, 6.0f, 1.05f), MythicProfiles.m("bell_shockwave", 1.1f, 21, 10.0f, 1.0f), MythicProfiles.m("chain_attack", 1.1f, 2, 10.0f, 0.75f), MythicProfiles.m("defensive_stance", 0.6f, 5, 0.0f, 0.0f))), new Profile("blood_moon_monk", 520.0, 18.0, 8.0, 0.26, 0.75f, 2.7f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("staff_strike", 0.6f, 0, 4.5f, 1.0f), MythicProfiles.m("staff_spin", 1.3f, 1, 6.0f, 0.85f), MythicProfiles.m("lunar_charge", 1.85f, 22, 11.0f, 1.1f), MythicProfiles.m("moon_barrier", 0.8f, 25, 6.0f, 0.0f))), new Profile("itachi_uchiha", 560.0, 20.0, 6.0, 0.3, 0.7f, 2.2f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("kunai_attack", 0.65f, 0, 4.0f, 1.0f), MythicProfiles.m("genjutsu_cast", 1.8f, 23, 10.0f, 0.65f), MythicProfiles.m("fireball_jutsu", 2.2f, 4, 13.0f, 1.1f), MythicProfiles.m("crow_illusion", 1.8f, 24, 8.0f, 0.6f))), new Profile("deidara", 520.0, 18.0, 6.0, 0.29, 0.7f, 2.2f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("clay_bird_attack", 1.8f, 31, 16.0f, 0.95f), MythicProfiles.m("clay_spider_attack", 1.8f, 32, 12.0f, 0.8f), MythicProfiles.m("clay_dragon_attack", 2.0f, 33, 18.0f, 1.3f), MythicProfiles.m("explosive_clay_attack", 2.7f, 34, 12.0f, 1.6f), MythicProfiles.m("clay_creation", 2.5f, 30, 12.0f, 0.0f))), new Profile("veiled_whorl", 480.0, 19.0, 6.0, 0.32, 0.7f, 2.2f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("blade_attack", 0.46f, 0, 4.0f, 1.0f), MythicProfiles.m("phase_shift", 0.4f, 35, 10.0f, 0.0f), MythicProfiles.m("vortex_teleport", 1.3f, 36, 14.0f, 0.0f), MythicProfiles.m("fireball_attack", 1.2f, 37, 15.0f, 1.1f), MythicProfiles.m("chain_attack", 0.8f, 38, 8.0f, 0.8f))), new Profile("mad_farmer", 420.0, 15.0, 5.0, 0.28, 0.8f, 2.5f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("sickle_attack", 0.85f, 0, 4.0f, 1.0f), MythicProfiles.m("harvest_sweep", 0.9f, 39, 5.0f, 0.8f), MythicProfiles.m("ground_slam", 1.15f, 40, 6.0f, 1.1f), MythicProfiles.m("crop_magic", 1.3f, 41, 10.0f, 0.65f), MythicProfiles.m("scarecrow_pose", 1.1f, 42, 8.0f, 0.0f))), new Profile("plague_doctor", 460.0, 14.0, 4.0, 0.26, 0.8f, 2.4f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("staff_attack", 0.8f, 0, 4.0f, 1.0f), MythicProfiles.m("plague_orb_attack", 1.1f, 43, 15.0f, 1.0f), MythicProfiles.m("toxic_cloud_attack", 1.2f, 44, 6.0f, 0.6f), MythicProfiles.m("potion_throw", 0.9f, 45, 12.0f, 0.6f), MythicProfiles.m("dark_healing", 1.3f, 46, 8.0f, 0.0f))), new Profile("cursed_executioner", 780.0, 24.0, 14.0, 0.24, 1.5f, 3.9f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("axe_attack", 0.9f, 0, 5.0f, 1.0f), MythicProfiles.m("sweeping_attack", 1.1f, 1, 7.0f, 0.9f), MythicProfiles.m("ground_slam", 1.3f, 1, 8.0f, 1.25f), MythicProfiles.m("charging_attack", 2.16f, 8, 9.0f, 1.15f), MythicProfiles.m("intimidation", 1.4f, 3, 7.0f, 0.55f))), new Profile("void_weaver", 520.0, 18.0, 6.0, 0.29, 0.8f, 2.4f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("crescent_attack", 0.72f, 0, 4.5f, 1.0f), MythicProfiles.m("blade_orbit", 1.05f, 1, 6.0f, 0.85f), MythicProfiles.m("thread_control", 1.4f, 2, 10.0f, 0.65f), MythicProfiles.m("void_guard", 0.65f, 5, 0.0f, 0.0f))), new Profile("azure_skyling", 220.0, 10.0, 2.0, 0.3, 0.8f, 1.0f, 1.0f, "walk", "run", true, false, List.of(MythicProfiles.m("crystal_attack", 0.72f, 47, 12.0f, 1.0f))), new Profile("gentleman_overlord", 600.0, 20.0, 8.0, 0.25, 0.8f, 2.1f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("cane_strike", 0.95f, 0, 4.0f, 1.0f), MythicProfiles.m("finger_snap_attack", 1.15f, 1, 7.0f, 0.85f), MythicProfiles.m("invisible_force", 1.25f, 2, 9.0f, 0.7f), MythicProfiles.m("royal_decree", 1.55f, 3, 8.0f, 0.8f), MythicProfiles.m("polite_bow_counter", 1.3f, 5, 0.0f, 0.0f))), new Profile("forsaken_fawn", 280.0, 12.0, 4.0, 0.31, 0.8f, 1.6f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("antler_attack", 0.65f, 0, 4.0f, 1.0f), MythicProfiles.m("moonbeam_attack", 1.95f, 47, 14.0f, 1.1f), MythicProfiles.m("crystal_defense", 0.85f, 5, 0.0f, 0.0f))), new Profile("lonely_red_panda", 300.0, 12.0, 4.0, 0.28, 0.85f, 1.2f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("leaf_storm_attack", 1.2f, 48, 6.0f, 1.0f), MythicProfiles.m("lantern_shield", 1.1f, 5, 0.0f, 0.0f))), new Profile("cursed_puppet_maker", 600.0, 18.0, 6.0, 0.26, 1.0f, 2.7f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("puppet_attack", 0.55f, 0, 5.0f, 1.0f), MythicProfiles.m("string_attack", 0.65f, 2, 10.0f, 0.7f), MythicProfiles.m("mechanical_claw_attack", 0.48f, 0, 4.0f, 1.1f), MythicProfiles.m("puppet_spin", 0.8f, 1, 6.0f, 0.85f), MythicProfiles.m("puppet_shield", 0.6f, 5, 0.0f, 0.0f))), new Profile("removed_companion", 100.0, 12.0, 8.0, 0.35, 1.2f, 3.0f, 1.0f, "running", "running", false, false, List.of(MythicProfiles.m("chopping", 0.5f, 0, 4.0f, 1.0f), MythicProfiles.m("stabbing", 0.5f, 0, 4.5f, 1.15f), MythicProfiles.m("heavy_chopping", 0.6f, 1, 5.0f, 0.85f), MythicProfiles.m("charging", 2.0f, 8, 8.0f, 1.1f))), new Profile("frostbound_snow_leopard", 340.0, 14.0, 4.0, 0.32, 0.9f, 1.3f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("frost_pounce", 1.58f, 49, 5.0f, 1.0f), MythicProfiles.m("snowflake_shield", 0.8f, 5, 0.0f, 0.0f))), new Profile("the_last_lullaby", 380.0, 13.0, 5.0, 0.24, 1.2f, 1.9f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("celestial_trumpet", 1.65f, 50, 10.0f, 0.9f), MythicProfiles.m("lullaby_shield", 1.0f, 5, 0.0f, 0.0f))), new Profile("refai_el_desouky", 550.0, 22.0, 8.0, 0.3, 0.8f, 2.1f, 1.0f, "walk", "run", false, false, List.of(MythicProfiles.m("punch_combo", 1.15f, 0, 3.6f, 1.0f), MythicProfiles.m("power_stomp", 1.1f, 1, 7.0f, 0.9f), MythicProfiles.m("intimidation", 1.0f, 3, 8.0f, 0.4f), MythicProfiles.m("command_gang", 1.6f, 51, 10.0f, 0.0f)))));
        ArrayList<Move> arrayList2 = new ArrayList<Move>();
        arrayList2.add(new Move("punch_attack", 0.55f, 0, 3.6f, 1.0f));
        arrayList2.add(new Move("defensive_counter", 0.8f, 5, 0.0f, 0.0f));
        arrayList2.add(new Move("command", 1.2f, 51, 10.0f, 0.0f));
        arrayList.add(new Profile("mokhtar_el_desouky", 500.0, 18.0, 8.0, 0.28, 0.8f, 2.1f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList2)));
        ArrayList<Move> arrayList3 = new ArrayList<Move>();
        arrayList3.add(new Move("starfall_attack", 1.8f, 47, 14.0f, 1.0f));
        arrayList3.add(new Move("celestial_shield", 1.2f, 5, 0.0f, 0.0f));
        arrayList.add(new Profile("broken_star_owlet", 300.0, 14.0, 4.0, 0.3, 1.2f, 1.5f, 1.0f, "walk", "fly", true, false, List.copyOf(arrayList3)));
        ArrayList<Move> arrayList4 = new ArrayList<Move>();
        arrayList4.add(new Move("punch_combo", 1.15f, 0, 3.6f, 1.0f));
        arrayList4.add(new Move("counter_attack", 0.8f, 5, 0.0f, 0.0f));
        arrayList4.add(new Move("power_stomp", 1.1f, 1, 7.0f, 0.9f));
        arrayList4.add(new Move("intimidation", 1.0f, 3, 8.0f, 0.4f));
        arrayList.add(new Profile("essam_el_nemr", 600.0, 22.0, 10.0, 0.3, 0.8f, 2.1f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList4)));
        ArrayList<Move> arrayList5 = new ArrayList<Move>();
        arrayList5.add(new Move("playful_jump", 0.5f, 0, 3.2f, 1.0f));
        arrayList5.add(new Move("crystal_glow", 1.2f, 5, 0.0f, 0.0f));
        arrayList.add(new Profile("lumen_cub", 240.0, 10.0, 4.0, 0.3, 0.85f, 1.1f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList5)));
        ArrayList<Move> arrayList6 = new ArrayList<Move>();
        arrayList6.add(new Move("bubble_play", 1.3f, 47, 12.0f, 1.0f));
        arrayList6.add(new Move("pearl_glow", 1.3f, 5, 0.0f, 0.0f));
        arrayList.add(new Profile("pearl_drakeling", 280.0, 12.0, 4.0, 0.28, 0.85f, 1.2f, 1.0f, "swim", "swim_fast", false, false, List.copyOf(arrayList6)));
        ArrayList<Move> arrayList7 = new ArrayList<Move>();
        arrayList7.add(new Move("crystal_burst", 2.4f, 47, 14.0f, 1.0f));
        arrayList7.add(new Move("curl_up", 0.8f, 5, 0.0f, 0.0f));
        arrayList7.add(new Move("roll", 0.4f, 8, 8.0f, 1.1f));
        arrayList.add(new Profile("shattered_pangolin", 360.0, 14.0, 10.0, 0.28, 1.2f, 1.2f, 0.7f, "walk", "run", false, false, List.copyOf(arrayList7)));
        ArrayList<Move> arrayList8 = new ArrayList<Move>();
        arrayList8.add(new Move("bell_slam", 1.5f, 0, 5.0f, 1.2f));
        arrayList8.add(new Move("sonic_wave_attack", 1.7f, 21, 10.0f, 1.0f));
        arrayList8.add(new Move("chain_sweep", 1.5f, 1, 7.0f, 0.9f));
        arrayList8.add(new Move("resonance_burst", 2.0f, 1, 9.0f, 1.1f));
        arrayList.add(new Profile("cursed_bell_ringer", 900.0, 24.0, 14.0, 0.22, 2.2f, 4.0f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList8)));
        ArrayList<Move> arrayList9 = new ArrayList<Move>();
        arrayList9.add(new Move("heart_wave_attack", 1.4f, 47, 12.0f, 1.0f));
        arrayList9.add(new Move("bubble_shield", 1.2f, 5, 0.0f, 0.0f));
        arrayList.add(new Profile("weeping_axolotl", 220.0, 12.0, 4.0, 0.28, 0.9f, 0.7f, 0.5f, "walk", "swim_fast", false, false, List.copyOf(arrayList9)));
        ArrayList<Move> arrayList10 = new ArrayList<Move>();
        arrayList10.add(new Move("cleaver_attack", 1.6f, 0, 6.0f, 1.2f));
        arrayList10.add(new Move("sweep_attack", 1.7f, 1, 8.0f, 1.0f));
        arrayList10.add(new Move("ground_slam", 1.9f, 1, 10.0f, 1.25f));
        arrayList10.add(new Move("chain_rage", 2.0f, 2, 12.0f, 0.8f));
        arrayList.add(new Profile("legendary_giant_zombie_refai", 1200.0, 28.0, 16.0, 0.24, 2.4f, 4.4f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList10)));
        ArrayList<Move> arrayList11 = new ArrayList<Move>();
        arrayList11.add(new Move("punch_combo", 1.3f, 0, 3.8f, 1.0f));
        arrayList11.add(new Move("defensive_counter", 1.1f, 5, 0.0f, 0.0f));
        arrayList11.add(new Move("intimidation", 1.5f, 3, 8.0f, 0.4f));
        arrayList.add(new Profile("amin_el_nemr", 480.0, 18.0, 8.0, 0.3, 0.6f, 1.95f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList11)));
        ArrayList<Move> arrayList12 = new ArrayList<Move>();
        arrayList12.add(new Move("bad_joke_attack", 1.04f, 21, 9.0f, 0.65f));
        arrayList12.add(new Move("cheese_meteor", 2.02f, 47, 16.0f, 1.05f));
        arrayList12.add(new Move("royal_facepalm", 1.04f, 5, 0.0f, 0.0f));
        arrayList12.add(new Move("cringe_beam", 1.4f, 6, 16.0f, 1.0f));
        arrayList12.add(new Move("ultimate_punchline", 2.3f, 1, 11.0f, 1.25f));
        arrayList.add(new Profile("king_of_corny_memes", 1100.0, 24.0, 12.0, 0.25, 2.0f, 4.2f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList12)));
        ArrayList<Move> arrayList13 = new ArrayList<Move>();
        arrayList13.add(new Move("69_pose", 3.4f, 5, 0.0f, 0.0f));
        arrayList13.add(new Move("gravity_kick", 0.75f, 0, 5.0f, 1.0f));
        arrayList13.add(new Move("inverted_strike", 1.2f, 1, 6.0f, 1.0f));
        arrayList13.add(new Move("number_orbit", 0.8f, 47, 14.0f, 0.9f));
        arrayList13.add(new Move("ultimate_inversion", 2.2f, 1, 10.0f, 1.2f));
        arrayList.add(new Profile("the_69_pose", 750.0, 21.0, 10.0, 0.31, 1.2f, 2.8f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList13)));
        ArrayList<Move> arrayList14 = new ArrayList<Move>();
        arrayList14.add(new Move("defensive_push", 0.65f, 1, 5.0f, 0.7f));
        arrayList14.add(new Move("dodge", 0.3f, 5, 0.0f, 0.0f));
        arrayList14.add(new Move("protective_stance", 0.7f, 5, 0.0f, 0.0f));
        arrayList.add(new Profile("hanan", 400.0, 15.0, 6.0, 0.3, 0.65f, 1.9f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList14)));
        ArrayList<Move> arrayList15 = new ArrayList<Move>();
        arrayList15.add(new Move("crystal_katana_combo", 0.55f, 0, 5.0f, 1.1f));
        arrayList15.add(new Move("mirror_slash", 1.0f, 1, 7.0f, 0.9f));
        arrayList15.add(new Move("reflection_clone", 1.8f, 5, 0.0f, 0.0f));
        arrayList15.add(new Move("shattered_blade", 1.3f, 47, 16.0f, 1.0f));
        arrayList15.add(new Move("thousand_mirrors", 1.9f, 6, 14.0f, 1.2f));
        arrayList.add(new Profile("glass_ronin", 900.0, 25.0, 12.0, 0.31, 1.2f, 3.0f, 1.0f, "walk", "run", false, false, List.copyOf(arrayList15)));
        arrayList.remove(34);
        ALL = List.copyOf(arrayList);
        LENGTHS = new ConcurrentHashMap<String, Map<String, Float>>();
    }

    public record Move(String clip, float impact, int effect, float range, float power) {
    }

    public record Profile(String id, double health, double damage, double armor, double speed, float width, float height, float scale, String walk, String run, boolean flies, boolean rideable, List<Move> moves) {
    }
}

