/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package dev.shadowsovereign.mastery;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.shadowsovereign.mythic.MythicExpedition;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MasteryCatalog {
    public static final List<Chapter> CHAPTERS = new ArrayList<Chapter>();
    public static final List<Stage> STAGES = new ArrayList<Stage>();
    public static final String[] CREATURES = new String[MythicExpedition.IDS.length + 1];

    private static Stage parse(JsonObject g, String chapter, String prev) {
        int target = g.get("target").getAsInt();
        if (target == -1) {
            target = CREATURES.length;
        }
        if (target == -2) {
            target = CREATURES.length * 8;
        }
        return new Stage(g.get("id").getAsString(), chapter, g.get("en").getAsString(), g.get("ar").getAsString(), g.get("helpEn").getAsString(), g.get("helpAr").getAsString(), g.get("metric").getAsString(), target, g.get("route").getAsString(), g.get("icon").getAsInt(), prev);
    }

    public static Stage get(String id) {
        return STAGES.stream().filter(s -> s.id().equals(id)).findFirst().orElse(null);
    }

    public static List<Stage> chapter(String id) {
        return STAGES.stream().filter(s -> s.chapter().equals(id)).toList();
    }

    static {
        MasteryCatalog.CREATURES[0] = "soul_devourer";
        System.arraycopy(MythicExpedition.IDS, 0, CREATURES, 1, MythicExpedition.IDS.length);
        try (InputStream in = MasteryCatalog.class.getResourceAsStream("/data/shadowsovereign/mastery/curriculum.json");
             InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);){
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            Object previous = "";
            for (JsonElement e : root.getAsJsonArray("chapters")) {
                JsonObject c = e.getAsJsonObject();
                String id = c.get("id").getAsString();
                if (c.has("requires")) {
                    previous = c.get("requires").getAsString();
                }
                CHAPTERS.add(new Chapter(id, c.get("en").getAsString(), c.get("ar").getAsString(), c.get("scene").getAsInt(), ""));
                for (JsonElement g : c.getAsJsonArray("goals")) {
                    Stage s = MasteryCatalog.parse(g.getAsJsonObject(), id, (String)previous);
                    STAGES.add(s);
                    previous = s.id();
                }
            }
            for (String id : CREATURES) {
                String chapter = "hunt." + id;
                CHAPTERS.add(new Chapter(chapter, "Hunt \u00b7 " + id, "\u0627\u0644\u0635\u064a\u062f \u00b7 " + id, 12, id));
                String[] en = new String[]{"Study the dossier", "Chart the habitat", "Reach the guardian\u2019s home", "Secure the legendary soul", "Summon your legend", "Equip its weapon slot", "Five personal victories", "Build a trusted bond"};
                String[] ar = new String[]{"\u0627\u062f\u0631\u0633 \u0645\u0644\u0641 \u0627\u0644\u0635\u064a\u062f", "\u062d\u062f\u062f \u0627\u0644\u0645\u0648\u0637\u0646", "\u0635\u0644 \u0625\u0644\u0649 \u0628\u064a\u062a \u0627\u0644\u062d\u0627\u0631\u0633", "\u0627\u062d\u0635\u0644 \u0639\u0644\u0649 \u0627\u0644\u0631\u0648\u062d \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u064a\u0629", "\u0627\u0633\u062a\u062f\u0639\u0650 \u0623\u0633\u0637\u0648\u0631\u062a\u0643", "\u062c\u0647\u0651\u0632 \u062e\u0627\u0646\u0629 \u0633\u0644\u0627\u062d\u0647", "\u062e\u0645\u0633 \u0642\u062a\u0644\u0627\u062a \u0634\u062e\u0635\u064a\u0629", "\u0627\u0628\u0646\u0650 \u0631\u0627\u0628\u0637\u0629 \u062b\u0642\u0629"};
                String[] metrics = new String[]{"lesson", "located.", "visited.", "owned.", "summoned.", "weapon.", "kills.", "bond."};
                int[] targets = new int[]{1, 1, 1, 1, 1, 1, 5, 10};
                previous = "awakening.8";
                for (int i = 0; i < 8; ++i) {
                    String helpAr;
                    String helpEn = switch (i) {
                        case 0 -> "Read this creature\u2019s lore, abilities and habitat below, then acknowledge the dossier. Knowledge prepares the fight; it does not grant a soul.";
                        case 1 -> "Hold the Explorer\u2019s Compass, choose this creature\u2019s structure and locate it inside Echo Realms. An already obtained soul also proves this step.";
                        case 2 -> "Travel to the guardian\u2019s actual altar. The server records the encounter site. Previously obtained souls retain their credit.";
                        case 3 -> "Defeat this guardian to receive its guaranteed once-per-owner soul. An Unbound Soul Sigil can also add it. Duplicate copies do not add collection types.";
                        case 4 -> "Summon this owned legendary type in a safe open space. The book remembers a verified summon.";
                        case 5 -> "Give this specific legendary companion a weapon through its equipment inventory. Original attacks and abilities remain.";
                        case 6 -> "Let this legendary companion personally earn five confirmed kills to reach level2. Owner kills and other companions\u2019 kills do not count.";
                        default -> "Care for this legendary companion until its bond or affection reaches10. Progress belongs to this type, not another companion.";
                    };
                    switch (i) {
                        case 0: {
                            String string = "\u0627\u0642\u0631\u0623 \u062d\u0643\u0627\u064a\u0629 \u0627\u0644\u0645\u062e\u0644\u0648\u0642 \u0648\u0642\u062f\u0631\u0627\u062a\u0647 \u0648\u0645\u0648\u0637\u0646\u0647 \u0623\u062f\u0646\u0627\u0647 \u062b\u0645 \u0623\u0643\u062f \u062f\u0631\u0627\u0633\u0629 \u0627\u0644\u0645\u0644\u0641. \u0627\u0644\u0645\u0639\u0631\u0641\u0629 \u062a\u062c\u0647\u0632\u0643 \u0644\u0644\u0645\u0639\u0631\u0643\u0629 \u0648\u0644\u0627 \u062a\u0645\u0646\u062d \u0631\u0648\u062d\u064b\u0627.";
                            break;
                        }
                        case 1: {
                            String string = "\u0623\u0645\u0633\u0643 \u0628\u0648\u0635\u0644\u0629 \u0627\u0644\u0645\u0633\u062a\u0643\u0634\u0641 \u0648\u0627\u062e\u062a\u0631 \u0645\u0646\u0634\u0623\u0629 \u0647\u0630\u0627 \u0627\u0644\u0645\u062e\u0644\u0648\u0642 \u0648\u062d\u062f\u062f\u0647\u0627 \u062f\u0627\u062e\u0644 \u0639\u0627\u0644\u0645 \u0627\u0644\u0623\u0635\u062f\u0627\u0621. \u062d\u064a\u0627\u0632\u0629 \u0631\u0648\u062d\u0647 \u0627\u0644\u0645\u0643\u062a\u0633\u0628\u0629 \u0633\u0627\u0628\u0642\u064b\u0627 \u062a\u062b\u0628\u062a \u0627\u0644\u0645\u0631\u062d\u0644\u0629 \u0623\u064a\u0636\u064b\u0627.";
                            break;
                        }
                        case 2: {
                            String string = "\u0633\u0627\u0641\u0631 \u0625\u0644\u0649 \u0645\u0630\u0628\u062d \u0627\u0644\u062d\u0627\u0631\u0633 \u0627\u0644\u0641\u0639\u0644\u064a\u061b \u064a\u0633\u062c\u0644 \u0627\u0644\u0633\u064a\u0631\u0641\u0631 \u0627\u0644\u0645\u0648\u0642\u0639. \u0627\u0644\u0623\u0631\u0648\u0627\u062d \u0627\u0644\u0645\u0643\u062a\u0633\u0628\u0629 \u0633\u0627\u0628\u0642\u064b\u0627 \u062a\u062d\u062a\u0641\u0638 \u0628\u0627\u062d\u062a\u0633\u0627\u0628\u0647\u0627.";
                            break;
                        }
                        case 3: {
                            String string = "\u0627\u0647\u0632\u0645 \u0627\u0644\u062d\u0627\u0631\u0633 \u0644\u0644\u062d\u0635\u0648\u0644 \u0639\u0644\u0649 \u0631\u0648\u062d\u0647 \u0627\u0644\u0645\u0636\u0645\u0648\u0646\u0629 \u0645\u0631\u0629 \u0644\u0643\u0644 \u0645\u0627\u0644\u0643. \u0642\u062f \u064a\u0645\u0646\u062d\u0647\u0627 \u062e\u062a\u0645 \u0627\u0644\u0631\u0648\u062d \u0627\u0644\u0645\u062c\u0647\u0648\u0644\u0629 \u0623\u064a\u0636\u064b\u0627. \u0627\u0644\u062a\u0643\u0631\u0627\u0631 \u0644\u0627 \u064a\u0632\u064a\u062f \u0623\u0646\u0648\u0627\u0639 \u0627\u0644\u0645\u062c\u0645\u0648\u0639\u0629.";
                            break;
                        }
                        case 4: {
                            String string = "\u0627\u0633\u062a\u062f\u0639\u0650 \u0647\u0630\u0627 \u0627\u0644\u0646\u0648\u0639 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u064a \u0627\u0644\u0645\u0645\u0644\u0648\u0643 \u0641\u064a \u0645\u0643\u0627\u0646 \u0622\u0645\u0646 \u0648\u0648\u0627\u0633\u0639\u061b \u064a\u062a\u0630\u0643\u0631 \u0627\u0644\u0643\u062a\u0627\u0628 \u0627\u0644\u0627\u0633\u062a\u062f\u0639\u0627\u0621 \u0627\u0644\u0645\u062a\u062d\u0642\u0642 \u0645\u0646\u0647.";
                            break;
                        }
                        case 5: {
                            String string = "\u0623\u0639\u0637\u0650 \u0647\u0630\u0627 \u0627\u0644\u0631\u0641\u064a\u0642 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u064a \u062a\u062d\u062f\u064a\u062f\u064b\u0627 \u0633\u0644\u0627\u062d\u064b\u0627 \u0645\u0646 \u0645\u062e\u0632\u0648\u0646\u0647. \u062a\u0628\u0642\u0649 \u0647\u062c\u0645\u0627\u062a\u0647 \u0648\u0642\u062f\u0631\u0627\u062a\u0647 \u0627\u0644\u0623\u0635\u0644\u064a\u0629.";
                            break;
                        }
                        case 6: {
                            String string = "\u062f\u0639 \u0647\u0630\u0627 \u0627\u0644\u0631\u0641\u064a\u0642 \u0627\u0644\u0623\u0633\u0637\u0648\u0631\u064a \u064a\u0642\u062a\u0644 \u062e\u0645\u0633\u0629 \u0623\u0639\u062f\u0627\u0621 \u0628\u0646\u0641\u0633\u0647 \u0644\u064a\u0628\u0644\u063a \u0627\u0644\u0645\u0633\u062a\u0648\u0649\u0662. \u0644\u0627 \u062a\u064f\u062d\u0633\u0628 \u0642\u062a\u0644\u0627\u062a\u0643 \u0623\u0648 \u0642\u062a\u0644\u0627\u062a \u0627\u0644\u0631\u0641\u0627\u0642 \u0627\u0644\u0622\u062e\u0631\u064a\u0646.";
                            break;
                        }
                        default: {
                            String string = helpAr = "\u0627\u0639\u062a\u0646\u0650 \u0628\u0647\u0630\u0627 \u0627\u0644\u0631\u0641\u064a\u0642 \u062d\u062a\u0649 \u062a\u0628\u0644\u063a \u0631\u0627\u0628\u0637\u062a\u0647 \u0623\u0648 \u0645\u0648\u062f\u062a\u0647\u0661\u0660. \u0627\u0644\u062a\u0642\u062f\u0645 \u062e\u0627\u0635 \u0628\u0647\u0630\u0627 \u0627\u0644\u0646\u0648\u0639\u060c \u0644\u0627 \u0628\u063a\u064a\u0631\u0647.";
                        }
                    }
                    if (id.equals("removed_companion")) {
                        if (i == 1) {
                            helpEn = "Hold the Explorer\u2019s Compass in the Overworld and locate the Village Knight Lodge, a real house inside an ordinary village.";
                            helpAr = "\u0623\u0645\u0633\u0643 \u0628\u0648\u0635\u0644\u0629 \u0627\u0644\u0645\u0633\u062a\u0643\u0634\u0641 \u0641\u064a \u0627\u0644\u0639\u0627\u0644\u0645 \u0627\u0644\u0639\u0627\u062f\u064a \u0648\u062d\u062f\u062f \u0628\u064a\u062a \u0641\u0627\u0631\u0633 \u0627\u0644\u0642\u0631\u064a\u0629\u060c \u0648\u0647\u0648 \u0628\u064a\u062a \u0641\u0639\u0644\u064a \u062f\u0627\u062e\u0644 \u0642\u0631\u064a\u0629 \u0642\u0631\u0648\u064a\u064a\u0646 \u0639\u0627\u062f\u064a\u0629.";
                        }
                        if (i == 3) {
                            helpEn = "Offer the village knight one emerald block to hire him, or defeat him to earn his soul. This legendary type is awarded once per player; the normal army and cemetery ledger apply.";
                            helpAr = "\u0642\u062f\u0645 \u0644\u0641\u0627\u0631\u0633 \u0627\u0644\u0642\u0631\u064a\u0629 \u0628\u0644\u0648\u0643 \u0632\u0645\u0631\u062f \u0648\u0627\u062d\u062f\u064b\u0627 \u0644\u062a\u062c\u0646\u064a\u062f\u0647\u060c \u0623\u0648 \u0627\u0647\u0632\u0645\u0647 \u0644\u0627\u0643\u062a\u0633\u0627\u0628 \u0631\u0648\u062d\u0647. \u064a\u064f\u0645\u0646\u062d \u0627\u0644\u0646\u0648\u0639 \u0645\u0631\u0629 \u0644\u0643\u0644 \u0644\u0627\u0639\u0628\u060c \u0645\u0639 \u0646\u0638\u0627\u0645 \u0627\u0644\u062c\u064a\u0634 \u0648\u0627\u0644\u0645\u0642\u0628\u0631\u0629 \u0627\u0644\u0645\u0639\u062a\u0627\u062f.";
                        }
                    }
                    String stageId = chapter + "." + (i + 1);
                    STAGES.add(new Stage(stageId, chapter, en[i], ar[i], helpEn, helpAr, (String)(i == 0 ? "lesson" : metrics[i] + id), targets[i], i == 1 || i == 2 ? "compass" : (i == 5 ? "equipment" : "grimoire"), i < 3 ? 3 : (i == 7 ? 9 : 6), (String)previous));
                    previous = stageId;
                }
            }
            CHAPTERS.add(new Chapter("graduation", "The Sovereign\u2019s Mastery", "\u0625\u062a\u0642\u0627\u0646 \u0627\u0644\u0645\u0644\u0643", 15, ""));
            previous = "awakening.8";
            for (JsonElement e : root.getAsJsonArray("graduation")) {
                Stage s = MasteryCatalog.parse(e.getAsJsonObject(), "graduation", (String)previous);
                STAGES.add(s);
                previous = s.id();
            }
            int core = 0;
            for (JsonElement c : root.getAsJsonArray("chapters")) {
                core += c.getAsJsonObject().getAsJsonArray("goals").size();
            }
            if (STAGES.size() != core + root.getAsJsonArray("graduation").size() + CREATURES.length * 8 || STAGES.stream().map(Stage::id).distinct().count() != (long)STAGES.size()) {
                throw new IllegalStateException("Invalid mastery curriculum");
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot load mastery curriculum", e);
        }
    }

    public record Stage(String id, String chapter, String en, String ar, String helpEn, String helpAr, String metric, int target, String route, int icon, String previous) {
    }

    public record Chapter(String id, String en, String ar, int scene, String creature) {
    }
}

