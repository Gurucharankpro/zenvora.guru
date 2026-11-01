/*
 * Decompiled with CFR 0.152.
 */
package dev.shadowsovereign.odyssey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class OdysseyCatalog {
    public static final String VERSION = "1.2.0";
    public static final List<String> FEATURES = List.of("guide", "realm", "campaign", "gateway", "command_hub", "dungeon", "loop", "train", "mirrors", "companies", "mine", "treasure", "masquerade", "lighthouse", "bazaar", "archivist", "spoon_king", "collector", "weaver", "choir", "mask_king", "personal_quests", "clubs", "pranks", "theatre", "memory_gifts", "gestures", "council", "group_photo", "parade", "homestead", "architect", "gardener", "kitchen", "furniture", "routines", "visitors", "camp_reputation", "football", "bowling", "tug", "chairs", "potato", "paint", "parcel", "statues", "mastery", "challenge_builder", "artifacts", "coronation");
    public static final String[] CHAPTERS = new String[]{"awakening", "army", "adventures", "journey", "warfront", "citadels", "living_army", "new_world", "explorers", "hearth_company", "crowns", "coronation"};
    private static final List<Goal> goals = new ArrayList<Goal>();

    private static void g(int chapter, String route, String ... ids) {
        for (String id : ids) {
            goals.add(new Goal(id, chapter, route, 1, id.startsWith("lesson.")));
        }
    }

    public static List<Goal> goals() {
        return Collections.unmodifiableList(goals);
    }

    public static List<Goal> chapter(int value) {
        return goals.stream().filter(g -> g.chapter == value).toList();
    }

    static {
        OdysseyCatalog.g(0, "army", "guide_open", "first_soul", "summoned", "order.follow", "recalled");
        OdysseyCatalog.g(1, "equipment", "equipped", "backpack", "fed", "level_two", "tactics");
        OdysseyCatalog.g(2, "adventure", "sanctuary", "trial_escort", "expedition_contract", "legacy_challenges", "legacy_powers");
        OdysseyCatalog.g(3, "journey", "chronicle_bosses", "rift_rally", "angling_story", "ascension_modes", "journey_campaign");
        OdysseyCatalog.g(4, "warfront", "warfront_campaign", "art_rune", "battle_plan", "rival_pact", "sovereign_trail");
        OdysseyCatalog.g(5, "eclipse", "eclipse_modes", "royal_modes", "mastery", "companies_banners", "camp_stations");
        OdysseyCatalog.g(6, "soullife", "lesson.cemetery", "lesson.protection", "vigil_games", "care_friend_story", "social_play");
        OdysseyCatalog.g(7, "odyssey", "realm_enter", "regions", "homestead", "gateway_return", "first_odyssey");
        OdysseyCatalog.g(8, "odyssey", "explore.dungeon_loop", "explore.train_mirrors", "explore.companies_mine", "explore.treasure_masquerade", "explore.lighthouse_bazaar");
        OdysseyCatalog.g(9, "camp", "personal_story", "club", "camp_services", "festival_games", "more_games");
        OdysseyCatalog.g(10, "bosses", "boss.archivist", "boss.spoon_king", "boss.collector", "boss.weaver", "boss.choir");
        OdysseyCatalog.g(11, "odyssey", "boss.mask_king", "artifacts_four", "camp_rank_three", "crew_memories", "ending");
    }

    public record Goal(String id, int chapter, String route, int target, boolean lesson) {
    }

    public static enum Mode {
        DUNGEON(6),
        LOOP(7),
        TRAIN(8),
        MIRRORS(9),
        COMPANIES(10),
        MINE(11),
        TREASURE(12),
        MASQUERADE(13),
        LIGHTHOUSE(14),
        BAZAAR(15),
        ARCHIVIST(16),
        SPOON_KING(17),
        COLLECTOR(18),
        WEAVER(19),
        CHOIR(20),
        MASK_KING(21),
        THEATRE(25),
        COUNCIL(28),
        PARADE(30),
        FOOTBALL(39),
        BOWLING(40),
        TUG(41),
        CHAIRS(42),
        POTATO(43),
        PAINT(44),
        PARCEL(45),
        STATUES(46);

        public final int feature;

        private Mode(int feature) {
            this.feature = feature;
        }

        public String id() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public boolean boss() {
            return this.ordinal() >= 10 && this.ordinal() <= 15;
        }

        public boolean retired() {
            return this.game();
        }

        public boolean game() {
            return this.ordinal() >= 19;
        }

        public boolean peaceful() {
            return this.game() || this == THEATRE || this == COUNCIL || this == PARADE || this == BAZAAR || this == LOOP || this == MIRRORS || this == TREASURE || this == MASQUERADE;
        }

        public int region() {
            return switch (this) {
                case DUNGEON, COMPANIES, MASQUERADE, MASK_KING, THEATRE, CHAIRS -> 3;
                case LOOP, MIRRORS, ARCHIVIST, CHOIR, COUNCIL, PAINT -> 2;
                case TRAIN, MINE, BAZAAR, COLLECTOR, BOWLING, PARCEL -> 1;
                default -> 0;
            };
        }

        public static Mode read(int value) {
            return value >= 0 && value < Mode.values().length ? Mode.values()[value] : null;
        }
    }
}

