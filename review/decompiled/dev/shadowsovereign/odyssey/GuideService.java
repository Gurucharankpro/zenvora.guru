/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.legacy.LegacyState;
import dev.shadowsovereign.mastery.MasteryService;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.vigil.VigilGraves;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class GuideService {
    public static OdysseyState state(ServerPlayer p) {
        return AdventureService.journal((ServerPlayer)p).odyssey;
    }

    public static long now(ServerPlayer p) {
        return p.f_8924_.m_129783_().m_46467_();
    }

    public static ArmyData.Army army(ServerPlayer p) {
        return ArmyData.get(p.f_8924_).army(p.m_20148_());
    }

    public static void dirty(ServerPlayer p) {
        AdventureService.dirty(p);
    }

    public static void record(ServerPlayer p, String key) {
        GuideService.state(p).add(key, 1L);
        GuideService.dirty(p);
    }

    public static void clear(ServerPlayer p, AdventureData.Session s) {
        if (GuideService.state(p).grant("clear:" + String.valueOf(s.id))) {
            GuideService.record(p, "clear." + s.kind.name().toLowerCase(Locale.ROOT));
        }
    }

    public static boolean all(ServerPlayer p, String ... keys) {
        for (String key : keys) {
            if (GuideService.state(p).count(key) != 0L) continue;
            return false;
        }
        return true;
    }

    public static boolean kinds(ServerPlayer p, String ... keys) {
        for (String key : keys) {
            if (GuideService.state(p).count("clear." + key) != 0L) continue;
            return false;
        }
        return true;
    }

    private static void evidence(OdysseyState s, String key, boolean value) {
        if (value) {
            s.observe(key, 1L);
        }
    }

    public static void importEvidence(ServerPlayer p) {
        String[][] pairs;
        ArmyData.Army a = GuideService.army(p);
        AdventureData.Journal j = AdventureService.journal(p);
        OdysseyState s = GuideService.state(p);
        Collection<ShadowRecord> records = a.shadows.values();
        int previous = s.events.hashCode();
        GuideService.evidence(s, "first_soul", !records.isEmpty());
        GuideService.evidence(s, "summoned", a.activeCount() > 0L);
        GuideService.evidence(s, "sanctuary", j.bound);
        GuideService.evidence(s, "level_two", records.stream().anyMatch(r -> r.level() >= 2));
        GuideService.evidence(s, "equipped", records.stream().anyMatch(r -> {
            r.inventory.initialize(r.snapshot);
            return !((ItemStack)r.inventory.items.get(0)).m_41619_() && r.inventory.items.subList(1, 6).stream().anyMatch(v -> !v.m_41619_());
        }));
        GuideService.evidence(s, "backpack", records.stream().anyMatch(r -> r.inventory.items.subList(6, 11).stream().anyMatch(v -> !v.m_41619_())));
        GuideService.evidence(s, "tactics", a.readyAt.values().stream().anyMatch(v -> v > 0L));
        GuideService.evidence(s, "fed", records.stream().anyMatch(r -> r.nextCareAt > 0L || r.life.meals > 0));
        GuideService.evidence(s, "warfront_campaign", a.warfront.wonOnce || a.warfront.captured == 63 || a.warfront.owned == 63);
        GuideService.evidence(s, "art_rune", a.warfront.builds.values().stream().anyMatch(b -> b.art != WarfrontState.Art.NONE && b.rune != WarfrontState.Rune.NONE));
        GuideService.evidence(s, "battle_plan", a.warfront.plans.stream().anyMatch(v -> v.size() == 4));
        GuideService.evidence(s, "rival_pact", a.warfront.pactClaimed != 0 || a.warfront.builds.values().stream().anyMatch(b -> b.pactbound));
        GuideService.evidence(s, "sovereign_trail", a.sovereign.completed > 0);
        GuideService.evidence(s, "mastery", a.royal.levels.values().stream().anyMatch(v -> v > 1));
        GuideService.evidence(s, "companies_banners", !a.royal.banners.isEmpty() && Arrays.stream(a.royal.teams).anyMatch(t -> !t.members.isEmpty()));
        GuideService.evidence(s, "camp_stations", Arrays.stream(EclipseState.Station.values()).allMatch(k -> a.eclipse.sites.stream().anyMatch(v -> v.kind() == k)));
        GuideService.evidence(s, "care_friend_story", records.stream().anyMatch(r -> r.life.pets > 0 && r.life.meals > 0 && r.life.stories > 0 && !r.life.friends.isEmpty()));
        GuideService.evidence(s, "personal_story", records.stream().anyMatch(r -> r.odyssey.story == 3));
        GuideService.evidence(s, "club", records.stream().filter(r -> r.odyssey.club >= 0).count() >= 2L);
        GuideService.evidence(s, "camp_services", s.count("built.architect") > 0L && s.count("garden_harvest") > 0L && s.count("cooked") > 0L);
        GuideService.evidence(s, "festival_games", GuideService.all(p, "camp_placed", "equipped"));
        GuideService.evidence(s, "more_games", GuideService.all(p, "backpack", "fed"));
        GuideService.evidence(s, "regions", Integer.bitCount((int)s.count("regions_mask")) >= 4);
        GuideService.evidence(s, "homestead", s.count("camp_placed") >= 4L);
        GuideService.evidence(s, "first_odyssey", s.events.entrySet().stream().anyMatch(e -> ((String)e.getKey()).startsWith("win.") && (Long)e.getValue() > 0L));
        GuideService.evidence(s, "artifacts_four", s.earned.stream().filter(v -> v.startsWith("artifact.")).count() >= 4L);
        GuideService.evidence(s, "camp_rank_three", s.rep >= 3);
        GuideService.evidence(s, "crew_memories", records.stream().anyMatch(r -> r.life.affection >= 25 && r.odyssey.story == 3));
        GuideService.evidence(s, "ending", s.ended);
        GuideService.evidence(s, "legacy_powers", j.legacy.stat(LegacyState.Stat.EXCHANGES) > 0L && j.legacy.stat(LegacyState.Stat.BURSTS) > 0L && j.legacy.stat(LegacyState.Stat.DUOS) > 0L);
        GuideService.evidence(s, "legacy_challenges", j.legacy.stat(LegacyState.Stat.SIEGES) > 0L && j.legacy.stat(LegacyState.Stat.VAULTS) > 0L && j.legacy.bestWave >= 5);
        GuideService.evidence(s, "trial_escort", GuideService.kinds(p, "trial", "escort"));
        GuideService.evidence(s, "expedition_contract", s.count("expedition_claim") > 0L && (!j.claimedOffers.isEmpty() || s.count("contract_claim") > 0L));
        GuideService.evidence(s, "chronicle_bosses", j.journey.data.m_128451_("Bosses") == 7 || Integer.bitCount(j.journey.data.m_128451_("BossMask") & 7) == 3 || GuideService.kinds(p, "boss") && s.count("chronicle_types") >= 3L);
        GuideService.evidence(s, "rift_rally", GuideService.kinds(p, "rift", "rally") || j.journey.data.m_128454_("Rifts") > 0L && j.journey.data.m_128454_("Rallies") > 0L);
        GuideService.evidence(s, "angling_story", (GuideService.kinds(p, "angling") || j.journey.data.m_128454_("Catches") >= 3L) && j.journey.data.m_128451_("Stories") != 0);
        GuideService.evidence(s, "ascension_modes", Arrays.stream(new String[]{"HEIST", "DOMINATION", "MIRROR", "DRAFT", "RUSH"}).allMatch(k -> j.ascension.m_128454_("Wins" + k) > 0L));
        GuideService.evidence(s, "journey_campaign", j.journey.data.m_128451_("Chapter") >= 6 || j.journey.data.m_128451_("Campaign") >= 6);
        GuideService.evidence(s, "eclipse_modes", GuideService.kinds(p, "eclipse_run", "reactor", "investigation", "board", "crystal", "chronophage"));
        GuideService.evidence(s, "royal_modes", GuideService.kinds(p, "training", "skill_course", "seal_puzzle", "mimic"));
        GuideService.evidence(s, "vigil_games", GuideService.kinds(p, "constellation", "echo_rhythm", "astral_dice", "pocket_maze", "ferryman"));
        GuideService.evidence(s, "social_play", s.count("social_types") >= 3L);
        for (String[] pair : pairs = new String[][]{{"dungeon", "loop"}, {"train", "mirrors"}, {"companies", "mine"}, {"treasure", "masquerade"}, {"lighthouse", "bazaar"}}) {
            GuideService.evidence(s, "explore." + pair[0] + "_" + pair[1], GuideService.all(p, "win." + pair[0], "win." + pair[1]));
        }
        AdventureData.Session active = j.session;
        if (active != null) {
            if (!active.id.equals(s.observedSession)) {
                s.observedSession = active.id;
                s.observedKind = active.kind.name().toLowerCase(Locale.ROOT);
                GuideService.dirty(p);
            }
            if (active.phase == AdventureData.Phase.READY) {
                GuideService.clear(p, active);
            }
        }
        if (previous != s.events.hashCode()) {
            GuideService.dirty(p);
        }
        MasteryService.observe(p);
    }

    public static boolean done(ServerPlayer p, OdysseyCatalog.Goal goal) {
        return GuideService.state(p).count(goal.id()) >= (long)goal.target();
    }

    public static int completed(ServerPlayer p) {
        return (int)OdysseyCatalog.goals().stream().filter(g -> GuideService.done(p, g)).count();
    }

    public static boolean chapterReady(ServerPlayer p, int chapter) {
        return chapter >= 0 && chapter < 12 && OdysseyCatalog.chapter(chapter).stream().allMatch(g -> GuideService.done(p, g));
    }

    public static boolean claim(ServerPlayer p, int chapter) {
        int prior;
        GuideService.importEvidence(p);
        OdysseyState s = GuideService.state(p);
        if (!GuideService.chapterReady(p, chapter) || (s.chapterClaims & 1 << chapter) != 0) {
            return false;
        }
        if (chapter > 7 && (s.chapterClaims & (prior = (1 << chapter - 7) - 1 << 7)) != prior) {
            return false;
        }
        s.chapterClaims |= 1 << chapter;
        s.selectedChapter = Math.min(11, chapter + 1);
        s.seals = Math.min(1000000, s.seals + 3);
        GuideService.dirty(p);
        OdysseyText.tell(p, "chapter_claimed", OdysseyText.c("chapter." + chapter, new Object[0]));
        return true;
    }

    public static boolean lesson(ServerPlayer p, int goal) {
        if (goal < 0 || goal >= OdysseyCatalog.goals().size()) {
            return false;
        }
        OdysseyCatalog.Goal g = OdysseyCatalog.goals().get(goal);
        if (!g.lesson()) {
            return false;
        }
        if (g.id().equals("lesson.cemetery") && !VigilGraves.ownedNear(p)) {
            return false;
        }
        if (g.id().equals("lesson.protection") && GuideService.army((ServerPlayer)p).shadows.isEmpty()) {
            return false;
        }
        GuideService.record(p, g.id());
        return true;
    }

    public static void deliverBook(ServerPlayer p) {
        OdysseyState s = GuideService.state(p);
        if (s.bookGiven) {
            return;
        }
        for (ItemStack stack : p.m_150109_().f_35974_) {
            if (!stack.m_150930_((Item)OdysseyContent.BOOK.get())) continue;
            s.bookGiven = true;
            GuideService.dirty(p);
            return;
        }
        if (p.m_150109_().m_36062_() < 0) {
            return;
        }
        ItemStack book = new ItemStack((ItemLike)OdysseyContent.BOOK.get());
        if (p.m_150109_().m_36054_(book)) {
            s.bookGiven = true;
            GuideService.dirty(p);
            OdysseyText.tell(p, "book_welcome", new Object[0]);
        }
    }

    public static boolean restoreBook(ServerPlayer p) {
        if (p.m_150109_().m_18947_((Item)OdysseyContent.BOOK.get()) > 0 || p.m_150109_().m_36062_() < 0) {
            return false;
        }
        ItemStack book = new ItemStack((ItemLike)OdysseyContent.BOOK.get());
        if (!p.m_150109_().m_36054_(book)) {
            return false;
        }
        GuideService.state((ServerPlayer)p).bookGiven = true;
        GuideService.dirty(p);
        return true;
    }
}

