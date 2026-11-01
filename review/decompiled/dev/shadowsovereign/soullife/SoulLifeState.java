/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.soullife;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class SoulLifeState {
    public int personality = -1;
    public int affection;
    public int spacing;
    public int crests;
    public int storyChoice = -1;
    public boolean showMood;
    public boolean firstAid;
    public boolean voidRescue;
    public String favorite = "";
    public ItemStack keepsake = ItemStack.f_41583_;
    public long happyUntil;
    public long calmUntil;
    public long proudUntil;
    public long restUntil;
    public long killAt = -1L;
    public long rewardAt;
    public long praiseKills = -1L;
    public long petAt;
    public long feedAt;
    public long talkAt;
    public long comfortAt;
    public long storyAt;
    public long expressionAt;
    public long rescueAt;
    public int lastLine = -1;
    public int games;
    public int pets;
    public int meals;
    public int rests;
    public int stories;
    public int helps;
    public double walked;
    public int[] melody = new int[]{0, 2, 4, 2};
    public int[] storyHistory = new int[0];
    public long wishDay = -1L;
    public int wish;
    public boolean wishDone;
    public long recapDay = -1L;
    public int dayCare;
    public int dayGames;
    public int dayKills;
    public int dayHelps;
    public double dayWalk;
    public final LinkedHashMap<UUID, Friend> friends = new LinkedHashMap();
    public final List<Memory> memories = new ArrayList<Memory>();
    public final LinkedHashSet<String> biomes = new LinkedHashSet();
    public transient Vec3 lastPlayer;
    public transient Vec3 lastMob;
    public transient String walkDimension = "";
    public transient long sampledAt = -1L;

    public static int clamp(int n, int min, int max) {
        return Math.max(min, Math.min(max, n));
    }

    private static double distance(double n) {
        return Double.isFinite(n) ? Math.max(0.0, Math.min(1.0E9, n)) : 0.0;
    }

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128405_("Personality", this.personality);
        n.m_128405_("Affection", this.affection);
        n.m_128405_("Spacing", this.spacing);
        n.m_128405_("Crests", this.crests);
        n.m_128405_("StoryChoice", this.storyChoice);
        n.m_128379_("ShowMood", this.showMood);
        n.m_128379_("FirstAid", this.firstAid);
        n.m_128379_("VoidRescue", this.voidRescue);
        n.m_128359_("Favorite", this.favorite);
        n.m_128365_("Keepsake", (Tag)this.keepsake.m_41739_(new CompoundTag()));
        n.m_128356_("Happy", this.happyUntil);
        n.m_128356_("Calm", this.calmUntil);
        n.m_128356_("Proud", this.proudUntil);
        n.m_128356_("Rest", this.restUntil);
        n.m_128356_("PraiseKills", this.praiseKills);
        n.m_128356_("KillAt", this.killAt);
        n.m_128356_("RewardAt", this.rewardAt);
        n.m_128356_("PetAt", this.petAt);
        n.m_128356_("FeedAt", this.feedAt);
        n.m_128356_("TalkAt", this.talkAt);
        n.m_128356_("ComfortAt", this.comfortAt);
        n.m_128356_("StoryAt", this.storyAt);
        n.m_128356_("ExpressionAt", this.expressionAt);
        n.m_128356_("RescueAt", this.rescueAt);
        n.m_128405_("LastLine", this.lastLine);
        n.m_128405_("Games", this.games);
        n.m_128405_("Pets", this.pets);
        n.m_128405_("Meals", this.meals);
        n.m_128405_("Rests", this.rests);
        n.m_128405_("Stories", this.stories);
        n.m_128405_("Helps", this.helps);
        n.m_128347_("Walked", this.walked);
        n.m_128385_("Melody", this.melody);
        n.m_128385_("StoryHistory", this.storyHistory);
        n.m_128356_("WishDay", this.wishDay);
        n.m_128405_("Wish", this.wish);
        n.m_128379_("WishDone", this.wishDone);
        n.m_128356_("RecapDay", this.recapDay);
        n.m_128405_("DayCare", this.dayCare);
        n.m_128405_("DayGames", this.dayGames);
        n.m_128405_("DayKills", this.dayKills);
        n.m_128405_("DayHelps", this.dayHelps);
        n.m_128347_("DayWalk", this.dayWalk);
        ListTag fs = new ListTag();
        this.friends.forEach((id, f) -> {
            CompoundTag t = f.save();
            t.m_128362_("Id", id);
            fs.add((Object)t);
        });
        n.m_128365_("Friends", (Tag)fs);
        ListTag ms = new ListTag();
        this.memories.forEach(m -> ms.add((Object)m.save()));
        n.m_128365_("Memories", (Tag)ms);
        ListTag bs = new ListTag();
        this.biomes.forEach(s -> bs.add((Object)StringTag.m_129297_((String)s)));
        n.m_128365_("Biomes", (Tag)bs);
        return n;
    }

    public static SoulLifeState load(CompoundTag n) {
        CompoundTag t;
        int[] history;
        SoulLifeState s = new SoulLifeState();
        s.personality = n.m_128441_("Personality") ? SoulLifeState.clamp(n.m_128451_("Personality"), -1, 5) : -1;
        s.affection = SoulLifeState.clamp(n.m_128451_("Affection"), 0, 100);
        s.spacing = SoulLifeState.clamp(n.m_128451_("Spacing"), 0, 3);
        s.crests = n.m_128451_("Crests") & 0x7F;
        s.storyChoice = SoulLifeState.clamp(n.m_128451_("StoryChoice"), -1, 2);
        s.showMood = n.m_128471_("ShowMood");
        s.firstAid = n.m_128471_("FirstAid");
        s.voidRescue = n.m_128471_("VoidRescue");
        s.favorite = n.m_128461_("Favorite");
        if (s.favorite.length() > 256) {
            s.favorite = "";
        }
        try {
            s.keepsake = ItemStack.m_41712_((CompoundTag)n.m_128469_("Keepsake"));
            if (!s.keepsake.m_41619_()) {
                s.keepsake.m_41764_(1);
            }
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        s.happyUntil = Math.max(0L, n.m_128454_("Happy"));
        s.calmUntil = Math.max(0L, n.m_128454_("Calm"));
        s.proudUntil = Math.max(0L, n.m_128454_("Proud"));
        s.restUntil = Math.max(0L, n.m_128454_("Rest"));
        s.praiseKills = n.m_128441_("PraiseKills") ? Math.max(-1L, n.m_128454_("PraiseKills")) : -1L;
        s.killAt = n.m_128441_("KillAt") ? n.m_128454_("KillAt") : -1L;
        s.rewardAt = Math.max(0L, n.m_128454_("RewardAt"));
        s.petAt = Math.max(0L, n.m_128454_("PetAt"));
        s.feedAt = Math.max(0L, n.m_128454_("FeedAt"));
        s.talkAt = Math.max(0L, n.m_128454_("TalkAt"));
        s.comfortAt = Math.max(0L, n.m_128454_("ComfortAt"));
        s.storyAt = Math.max(0L, n.m_128454_("StoryAt"));
        s.expressionAt = Math.max(0L, n.m_128454_("ExpressionAt"));
        s.rescueAt = Math.max(0L, n.m_128454_("RescueAt"));
        s.lastLine = SoulLifeState.clamp(n.m_128451_("LastLine"), -1, 63);
        s.games = SoulLifeState.clamp(n.m_128451_("Games"), 0, 1000000);
        s.pets = SoulLifeState.clamp(n.m_128451_("Pets"), 0, 1000000);
        s.meals = SoulLifeState.clamp(n.m_128451_("Meals"), 0, 1000000);
        s.rests = SoulLifeState.clamp(n.m_128451_("Rests"), 0, 1000000);
        s.stories = SoulLifeState.clamp(n.m_128451_("Stories"), 0, 1000000);
        s.helps = SoulLifeState.clamp(n.m_128451_("Helps"), 0, 1000000);
        s.walked = SoulLifeState.distance(n.m_128459_("Walked"));
        int[] melody = n.m_128465_("Melody");
        if (melody.length == 4) {
            for (int i2 = 0; i2 < 4; ++i2) {
                melody[i2] = SoulLifeState.clamp(melody[i2], 0, 7);
            }
            s.melody = melody;
        }
        if ((history = n.m_128465_("StoryHistory")).length <= 3 && Arrays.stream(history).allMatch(i -> i >= 0 && i <= 2)) {
            s.storyHistory = history;
        }
        s.wishDay = n.m_128441_("WishDay") ? n.m_128454_("WishDay") : -1L;
        s.wish = SoulLifeState.clamp(n.m_128451_("Wish"), 0, 3);
        s.wishDone = n.m_128471_("WishDone");
        s.recapDay = n.m_128441_("RecapDay") ? n.m_128454_("RecapDay") : -1L;
        s.dayCare = SoulLifeState.clamp(n.m_128451_("DayCare"), 0, 1000000);
        s.dayGames = SoulLifeState.clamp(n.m_128451_("DayGames"), 0, 1000000);
        s.dayKills = SoulLifeState.clamp(n.m_128451_("DayKills"), 0, 1000000);
        s.dayHelps = SoulLifeState.clamp(n.m_128451_("DayHelps"), 0, 1000000);
        s.dayWalk = SoulLifeState.distance(n.m_128459_("DayWalk"));
        for (Tag tag : n.m_128437_("Friends", 10)) {
            if (s.friends.size() >= 3 || !(t = (CompoundTag)tag).m_128403_("Id")) continue;
            s.friends.put(t.m_128342_("Id"), Friend.load(t));
        }
        for (Tag tag : n.m_128437_("Memories", 10)) {
            if (s.memories.size() >= 20) continue;
            t = (CompoundTag)tag;
            String k = t.m_128461_("Kind");
            String detail = t.m_128461_("Detail");
            if (!k.matches("[a-z_]{1,24}") || detail.length() > 256) continue;
            s.memories.add(new Memory(k, Math.max(0L, t.m_128454_("Time")), t.m_128461_("Dimension"), t.m_128454_("Pos"), detail));
        }
        for (Tag tag : n.m_128437_("Biomes", 8)) {
            if (s.biomes.size() >= 32 || tag.m_7916_().length() > 256) continue;
            s.biomes.add(tag.m_7916_());
        }
        return s;
    }

    public static final class Friend {
        public int points;
        public long separated = -1L;
        public long lastNear = -1L;
        public long nextReward;
        public long reunionAt;

        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128405_("Points", this.points);
            n.m_128356_("Separated", this.separated);
            n.m_128356_("LastNear", this.lastNear);
            n.m_128356_("NextReward", this.nextReward);
            n.m_128356_("ReunionAt", this.reunionAt);
            return n;
        }

        public static Friend load(CompoundTag n) {
            Friend f = new Friend();
            f.points = SoulLifeState.clamp(n.m_128451_("Points"), 0, 100);
            f.separated = n.m_128441_("Separated") ? n.m_128454_("Separated") : -1L;
            f.lastNear = n.m_128441_("LastNear") ? n.m_128454_("LastNear") : -1L;
            f.nextReward = Math.max(0L, n.m_128454_("NextReward"));
            f.reunionAt = Math.max(0L, n.m_128454_("ReunionAt"));
            return f;
        }
    }

    public record Memory(String kind, long time, String dimension, long pos, String detail) {
        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128359_("Kind", this.kind);
            n.m_128356_("Time", this.time);
            n.m_128359_("Dimension", this.dimension);
            n.m_128356_("Pos", this.pos);
            n.m_128359_("Detail", this.detail);
            return n;
        }
    }

    public static final class Owner {
        public CompoundTag home = new CompoundTag();
        public CompoundTag activity = new CompoundTag();
        public UUID selected;
        public long playAt;
        public long aidAt;
        public long nextVoice;
        public boolean expressions = true;
        public transient CompoundTag lastView;
        public transient long viewUntil;

        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            if (this.selected != null) {
                n.m_128362_("Selected", this.selected);
            }
            n.m_128365_("Home", (Tag)this.home.m_6426_());
            n.m_128365_("Activity", (Tag)this.activity.m_6426_());
            n.m_128356_("PlayAt", this.playAt);
            n.m_128356_("AidAt", this.aidAt);
            n.m_128356_("VoiceAt", this.nextVoice);
            n.m_128379_("Expressions", this.expressions);
            return n;
        }

        public static Owner load(CompoundTag n) {
            Owner o = new Owner();
            o.selected = n.m_128403_("Selected") ? n.m_128342_("Selected") : null;
            o.home = n.m_128469_("Home").m_6426_();
            o.activity = n.m_128469_("Activity").m_6426_();
            if (o.activity.m_128440_() > 32) {
                o.activity = new CompoundTag();
            }
            o.playAt = Math.max(0L, n.m_128454_("PlayAt"));
            o.aidAt = Math.max(0L, n.m_128454_("AidAt"));
            o.nextVoice = Math.max(0L, n.m_128454_("VoiceAt"));
            o.expressions = !n.m_128441_("Expressions") || n.m_128471_("Expressions");
            return o;
        }
    }

    public static enum Wish {
        FOOD,
        PLAY,
        WALK,
        REST;

    }

    public static enum Mood {
        CONTENT,
        WORRIED,
        PLAYFUL,
        PROUD,
        RESTFUL;

    }

    public static enum Personality {
        BRAVE,
        GENTLE,
        CURIOUS,
        PLAYFUL,
        WATCHFUL,
        SERENE;

    }
}

