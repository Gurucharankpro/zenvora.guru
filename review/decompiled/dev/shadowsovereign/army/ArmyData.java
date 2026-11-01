/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.saveddata.SavedData
 */
package dev.shadowsovereign.army;

import dev.shadowsovereign.ShadowSovereign;
import dev.shadowsovereign.army.BattleRules;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.ascension.SoulRecord;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.expansion.RemovedContent;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.soulforge.SoulTier;
import dev.shadowsovereign.soullife.SoulLifeState;
import dev.shadowsovereign.sovereign.SovereignState;
import dev.shadowsovereign.vigil.VigilState;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public final class ArmyData
extends SavedData {
    private final Map<UUID, Army> armies = new HashMap<UUID, Army>();

    public static ArmyData get(MinecraftServer server) {
        return (ArmyData)server.m_129783_().m_8895_().m_164861_(ArmyData::load, ArmyData::new, "shadowsovereign_armies");
    }

    public Map<UUID, Army> owners() {
        return Collections.unmodifiableMap(this.armies);
    }

    public Army army(UUID player) {
        return this.armies.computeIfAbsent(player, id -> new Army());
    }

    public static ArmyData load(CompoundTag compoundTag) {
        CompoundTag root;
        RemovedContent.army(compoundTag);
        ArmyData data = new ArmyData();
        for (Tag item : root.m_128437_("Armies", 10)) {
            CompoundTag tag = (CompoundTag)item;
            if (!tag.m_128403_("Owner")) continue;
            Army army = data.army(tag.m_128342_("Owner"));
            army.warfront = WarfrontState.load(tag.m_128469_("Warfront"));
            army.sovereign = SovereignState.load(tag.m_128469_("Sovereign"));
            army.eclipse = EclipseState.load(tag.m_128469_("Eclipse"));
            army.life = SoulLifeState.Owner.load(tag.m_128469_("SoulLife"));
            army.vigil = VigilState.load(tag.m_128469_("Vigil"));
            army.royal = RoyalState.load(tag.m_128469_("Royal"));
            army.autoRecruit = !tag.m_128441_("AutoRecruit") || tag.m_128471_("AutoRecruit");
            army.manualRecruit = !tag.m_128441_("ManualRecruit") || tag.m_128471_("ManualRecruit");
            army.recruitmentNotice = !tag.m_128441_("ManualRecruit") && army.autoRecruit;
            for (Tag soul : tag.m_128437_("Souls", 10)) {
                if (army.souls.size() >= 128) continue;
                try {
                    SoulRecord r = SoulRecord.load((CompoundTag)soul);
                    army.souls.put(r.id, r);
                }
                catch (RuntimeException r) {}
            }
            army.autoSummon = !tag.m_128441_("AutoSummon") || tag.m_128471_("AutoSummon");
            for (Tag type : tag.m_128437_("ChosenTypes", 8)) {
                army.chosenTypes.add(type.m_7916_());
            }
            army.selectedSquad = BattleRules.read(BattleRules.Squad.class, tag.m_128461_("SelectedSquad"), BattleRules.Squad.ALL);
            CompoundTag cooldowns = tag.m_128469_("SkillReadyAt");
            for (BattleRules.Skill skill : BattleRules.Skill.values()) {
                army.readyAt.put(skill, Math.max(0L, cooldowns.m_128454_(skill.name())));
            }
            for (Tag entry : tag.m_128437_("Shadows", 10)) {
                try {
                    ShadowRecord r = ShadowRecord.load((CompoundTag)entry);
                    army.shadows.put(r.id, r);
                    if (r.tier != SoulTier.CHOSEN) continue;
                    army.chosenTypes.add(r.type);
                }
                catch (RuntimeException e) {
                    ShadowSovereign.LOGGER.warn("Skipped malformed shadow record", (Throwable)e);
                }
            }
        }
        return data;
    }

    public CompoundTag m_7176_(CompoundTag root) {
        ListTag list = new ListTag();
        this.armies.forEach((owner, army) -> {
            CompoundTag tag = new CompoundTag();
            tag.m_128362_("Owner", owner);
            tag.m_128365_("Warfront", (Tag)army.warfront.save());
            tag.m_128365_("Sovereign", (Tag)army.sovereign.save());
            tag.m_128365_("Eclipse", (Tag)army.eclipse.save());
            tag.m_128365_("SoulLife", (Tag)army.life.save());
            tag.m_128365_("Royal", (Tag)army.royal.save());
            tag.m_128365_("Vigil", (Tag)army.vigil.save());
            tag.m_128379_("AutoRecruit", army.autoRecruit);
            tag.m_128379_("AutoSummon", army.autoSummon);
            tag.m_128379_("ManualRecruit", army.manualRecruit);
            ListTag souls = new ListTag();
            army.souls.values().forEach(r -> souls.add((Object)r.save()));
            tag.m_128365_("Souls", (Tag)souls);
            tag.m_128359_("SelectedSquad", army.selectedSquad.name());
            CompoundTag cooldowns = new CompoundTag();
            army.readyAt.forEach((skill, at) -> cooldowns.m_128356_(skill.name(), at.longValue()));
            tag.m_128365_("SkillReadyAt", (Tag)cooldowns);
            ListTag entries = new ListTag();
            army.shadows.values().forEach(r -> entries.add((Object)r.save()));
            tag.m_128365_("Shadows", (Tag)entries);
            ListTag chosen = new ListTag();
            army.chosenTypes.stream().sorted().forEach(t -> chosen.add((Object)StringTag.m_129297_((String)t)));
            tag.m_128365_("ChosenTypes", (Tag)chosen);
            list.add((Object)tag);
        });
        root.m_128405_("Version", 13);
        root.m_128365_("Armies", (Tag)list);
        return root;
    }

    public static final class Army {
        public SoulLifeState.Owner life = new SoulLifeState.Owner();
        public VigilState vigil = new VigilState();
        public RoyalState royal = new RoyalState();
        public WarfrontState warfront = new WarfrontState();
        public SovereignState sovereign = new SovereignState();
        public EclipseState eclipse = new EclipseState();
        public boolean autoRecruit = true;
        public boolean manualRecruit = true;
        public boolean recruitmentNotice;
        public final LinkedHashMap<UUID, SoulRecord> souls = new LinkedHashMap();
        public boolean autoSummon = true;
        public BattleRules.Squad selectedSquad = BattleRules.Squad.ALL;
        public final EnumMap<BattleRules.Skill, Long> readyAt = new EnumMap(BattleRules.Skill.class);
        public final LinkedHashMap<UUID, ShadowRecord> shadows = new LinkedHashMap();
        public final Set<String> chosenTypes = new HashSet<String>();

        public long activeCount() {
            return this.shadows.values().stream().filter(r -> r.activeEntity != null).count();
        }
    }
}

