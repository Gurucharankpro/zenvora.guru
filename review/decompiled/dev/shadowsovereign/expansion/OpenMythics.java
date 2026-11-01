/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerBossEvent
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.BossEvent$BossBarOverlay
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 */
package dev.shadowsovereign.expansion;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.gloom.GloomRealm;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicLair;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.MythicSites;
import dev.shadowsovereign.mythic.MythicState;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.WildBosses;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

public final class OpenMythics {
    private static final Map<UUID, UUID> shownBoss = new HashMap<UUID, UUID>();

    private OpenMythics() {
    }

    public static boolean isAquatic(int n) {
        return n >= 0 && n < MythicExpedition.IDS.length && (MythicExpedition.IDS[n].equals("pearl_drakeling") || MythicExpedition.IDS[n].equals("weeping_axolotl"));
    }

    public static boolean clearLegacy(AdventureData.Journal journal) {
        if (journal.session != null && journal.session.kind == AdventureData.Kind.MYTHIC_LAIR) {
            journal.session = null;
            return true;
        }
        return false;
    }

    private static void migrate(ServerPlayer serverPlayer) {
        if (OpenMythics.clearLegacy(AdventureService.journal(serverPlayer))) {
            GloomRealm.dirty(serverPlayer);
        }
    }

    public static boolean start(ServerPlayer serverPlayer, int n) {
        OpenMythics.migrate(serverPlayer);
        if (n < 0 || n >= MythicExpedition.IDS.length || !serverPlayer.m_6084_() || serverPlayer.m_5833_() || serverPlayer.m_9236_().m_46791_() == Difficulty.PEACEFUL || !MythicExpedition.near(serverPlayer, n, 24)) {
            return false;
        }
        CompoundTag compoundTag = MythicExpedition.state(serverPlayer, n);
        if (compoundTag.m_128451_("Status") == 2 || !compoundTag.m_128441_("Lair") && (n >= 3 || !GloomRealm.state(serverPlayer).m_128471_("Ready"))) {
            return false;
        }
        if (compoundTag.m_128403_("Active")) {
            return MythicExpedition.body(serverPlayer, n) != null;
        }
        if (!MythicExpedition.spawn(serverPlayer, n)) {
            return false;
        }
        compoundTag.m_128405_("Status", 1);
        GloomRealm.dirty(serverPlayer);
        return true;
    }

    public static boolean startDevourer(ServerPlayer serverPlayer) {
        OpenMythics.migrate(serverPlayer);
        MythicState mythicState = MythicService.state(serverPlayer);
        if (mythicState.status == 2 || !mythicState.generated && !mythicState.gloom.m_128441_("NaturalDevourer") || !serverPlayer.m_6084_() || serverPlayer.m_5833_() || serverPlayer.m_9236_().m_46791_() == Difficulty.PEACEFUL || !MythicService.near(serverPlayer, 25)) {
            return false;
        }
        if (mythicState.active != null) {
            return MythicService.body(serverPlayer) != null;
        }
        if (!MythicService.spawn(serverPlayer)) {
            return false;
        }
        mythicState.status = 1;
        GloomRealm.dirty(serverPlayer);
        return true;
    }

    public static AdventureData.Session combatView(ServerPlayer serverPlayer) {
        AdventureData.Session session = new AdventureData.Session();
        session.kind = AdventureData.Kind.MYTHIC_LAIR;
        session.phase = AdventureData.Phase.ACTIVE;
        session.participants.addAll(ArmyData.get((MinecraftServer)serverPlayer.f_8924_).army((UUID)serverPlayer.m_20148_()).shadows.keySet());
        return session;
    }

    public static AdventureData.Session liveSession(AdventureData.Journal journal) {
        if (journal.mythic.status == 2 || journal.mythic.active == null) {
            return null;
        }
        AdventureData.Session session = new AdventureData.Session();
        session.kind = AdventureData.Kind.MYTHIC_LAIR;
        session.phase = AdventureData.Phase.ACTIVE;
        return session;
    }

    public static void join(EntityJoinLevelEvent entityJoinLevelEvent) {
        boolean bl;
        MythicBeast mythicBeast;
        Entity entity;
        block6: {
            block5: {
                entity = entityJoinLevelEvent.getEntity();
                if (entityJoinLevelEvent.getLevel().f_46443_ || !(entity instanceof MythicBeast)) break block5;
                mythicBeast = (MythicBeast)entity;
                if (!ShadowService.isShadow(entity) && entity.getPersistentData().m_128403_("SoulboundMythicHost")) break block6;
            }
            return;
        }
        int n = mythicBeast.kind();
        boolean bl2 = bl = n >= 0 && n < MythicExpedition.IDS.length;
        if (bl) {
            UUID uUID = entity.getPersistentData().m_128342_("SoulboundMythicHost");
            CompoundTag compoundTag = AdventureData.get((MinecraftServer)entity.m_20194_()).journal((UUID)uUID).mythic.gloom.m_128469_(MythicExpedition.IDS[n]);
            boolean bl3 = bl = compoundTag.m_128451_("Status") != 2 && compoundTag.m_128403_("Active") && entity.m_20148_().equals(compoundTag.m_128342_("Active"));
        }
        if (!bl) {
            entityJoinLevelEvent.setCanceled(true);
            entity.m_146870_();
        }
    }

    public static boolean defeated(MythicBeast mythicBeast, UUID uUID) {
        boolean bl;
        if (!mythicBeast.getPersistentData().m_128403_("SoulboundMythicHost")) {
            return false;
        }
        UUID uUID2 = mythicBeast.getPersistentData().m_128342_("SoulboundMythicHost");
        int n = mythicBeast.kind();
        if (!uUID2.equals(uUID) || n < 0 || n >= MythicExpedition.IDS.length) {
            return false;
        }
        AdventureData adventureData = AdventureData.get(mythicBeast.m_20194_());
        AdventureData.Journal journal = adventureData.journal(uUID2);
        CompoundTag compoundTag = journal.mythic.gloom.m_128469_(MythicExpedition.IDS[n]);
        if (compoundTag.m_128451_("Status") == 2 || !compoundTag.m_128403_("Active") || !mythicBeast.m_20148_().equals(compoundTag.m_128342_("Active"))) {
            return false;
        }
        ServerPlayer serverPlayer = mythicBeast.m_20194_().m_6846_().m_11259_(uUID2);
        boolean bl2 = bl = serverPlayer == null ? WildBosses.captureOffline(mythicBeast.m_20194_(), uUID2, (Mob)mythicBeast) : WildBosses.capture(serverPlayer, (Mob)mythicBeast);
        if (!bl) {
            return false;
        }
        compoundTag.m_128405_("Status", 2);
        compoundTag.m_128362_("Soul", mythicBeast.m_20148_());
        compoundTag.m_128473_("Active");
        compoundTag.m_128473_("Snapshot");
        OpenMythics.clearLegacy(journal);
        adventureData.m_77762_();
        if (serverPlayer != null) {
            MythicNetwork.send(serverPlayer, false);
        }
        return true;
    }

    public static void pause(MinecraftServer minecraftServer, UUID uUID) {
        AdventureData adventureData = AdventureData.get(minecraftServer);
        AdventureData.Journal journal = adventureData.journal(uUID);
        if (OpenMythics.clearLegacy(journal)) {
            adventureData.m_77762_();
        }
        if (journal.mythic.bar != null) {
            journal.mythic.bar.m_7706_();
            journal.mythic.bar = null;
        }
        shownBoss.remove(uUID);
    }

    public static boolean cancel(ServerPlayer serverPlayer) {
        OpenMythics.pause(serverPlayer.f_8924_, serverPlayer.m_20148_());
        return true;
    }

    public static boolean editableGloom(ServerPlayer serverPlayer, BlockPos blockPos) {
        return GloomRealm.inside(serverPlayer, blockPos) && !OdysseyRealm.foreignPlot(serverPlayer, blockPos);
    }

    public static void fallback(ServerPlayer serverPlayer) {
        if (serverPlayer.f_19797_ % 40 != 0 || !GloomRealm.state(serverPlayer).m_128471_("Ready")) {
            return;
        }
        for (int i = 0; i < 3; ++i) {
            if (!MythicExpedition.near(serverPlayer, i, 22)) continue;
            OpenMythics.start(serverPlayer, i);
        }
    }

    public static void tick(ServerPlayer serverPlayer) {
        OpenMythics.migrate(serverPlayer);
        GloomRealm.tick(serverPlayer);
        MythicLair.tick(serverPlayer);
        MythicSites.tick(serverPlayer);
        OpenMythics.fallback(serverPlayer);
        MythicState mythicState = MythicService.state(serverPlayer);
        if (serverPlayer.f_19797_ % 40 == 0 && mythicState.status != 2 && MythicService.near(serverPlayer, 22)) {
            OpenMythics.startDevourer(serverPlayer);
        }
        if (serverPlayer.f_19797_ % 10 != 0) {
            return;
        }
        Monster monster = null;
        double d = 2916.0;
        for (int i = -1; i < MythicExpedition.IDS.length; ++i) {
            double d2;
            Monster monster2;
            if (i < 0 && mythicState.active == null || i >= 0 && (!mythicState.gloom.m_128425_(MythicExpedition.IDS[i], 10) || !mythicState.gloom.m_128469_(MythicExpedition.IDS[i]).m_128403_("Active"))) continue;
            Monster monster3 = monster2 = i < 0 ? MythicService.body(serverPlayer) : MythicExpedition.body(serverPlayer, i);
            if (monster2 == null || !monster2.m_6084_()) continue;
            boolean bl = monster2.m_9236_() == serverPlayer.m_9236_();
            double d3 = d2 = bl ? monster2.m_20280_((Entity)serverPlayer) : Double.MAX_VALUE;
            if (!serverPlayer.m_6084_() || serverPlayer.m_5833_() || d2 > 2916.0) {
                monster2.m_6710_(null);
                continue;
            }
            if (d2 < d) {
                monster = monster2;
                d = d2;
            }
            ServerPlayer serverPlayer2 = !serverPlayer.m_7500_() ? serverPlayer : null;
            double d4 = serverPlayer2 == null ? Double.MAX_VALUE : d2;
            for (ShadowRecord shadowRecord : ArmyData.get((MinecraftServer)serverPlayer.f_8924_).army((UUID)serverPlayer.m_20148_()).shadows.values()) {
                double d5;
                Mob mob = ShadowService.active(serverPlayer.f_8924_, shadowRecord);
                if (mob == null || !mob.m_6084_() || mob.m_9236_() != monster2.m_9236_() || !((d5 = monster2.m_20280_((Entity)mob)) < d4) || !(d5 < 2916.0)) continue;
                serverPlayer2 = mob;
                d4 = d5;
            }
            monster2.m_6710_((LivingEntity)serverPlayer2);
        }
        if (monster == null) {
            if (mythicState.bar != null) {
                mythicState.bar.m_7706_();
                mythicState.bar = null;
            }
            shownBoss.remove(serverPlayer.m_20148_());
            return;
        }
        if (mythicState.bar == null || !monster.m_20148_().equals(shownBoss.get(serverPlayer.m_20148_()))) {
            if (mythicState.bar != null) {
                mythicState.bar.m_7706_();
            }
            mythicState.bar = new ServerBossEvent(monster.m_7755_(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
            mythicState.bar.m_6543_(serverPlayer);
            shownBoss.put(serverPlayer.m_20148_(), monster.m_20148_());
        }
        mythicState.bar.m_142711_(Math.max(0.0f, Math.min(1.0f, monster.m_21223_() / monster.m_21233_())));
    }
}

