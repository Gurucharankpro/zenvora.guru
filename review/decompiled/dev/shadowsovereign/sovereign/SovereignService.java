/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 */
package dev.shadowsovereign.sovereign;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.sovereign.SovereignState;
import dev.shadowsovereign.warfront.WarfrontService;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class SovereignService {
    private SovereignService() {
    }

    public static SovereignState state(ServerPlayer p) {
        return ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).sovereign;
    }

    public static boolean needsCare(ServerPlayer p, UUID id) {
        SovereignState s = SovereignService.state(p);
        return s.active >= 0 && !s.ready() && id.equals(s.champion) && s.goal() == SovereignState.Goal.FEED;
    }

    public static boolean accept(ServerPlayer p, int slot, UUID id) {
        SovereignState s = SovereignService.state(p);
        ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
        if (!p.m_6084_() || p.m_5833_() || s.active >= 0 || slot < 0 || slot > 2 || r == null) {
            return false;
        }
        int offer = s.offer(slot);
        if (Arrays.asList(SovereignState.ROUTES[offer]).contains((Object)SovereignState.Goal.ART) && !WarfrontService.state((ServerPlayer)p).wonOnce) {
            return false;
        }
        s.active = offer;
        s.champion = id;
        s.progress = 0;
        s.stage = 0;
        SovereignService.dirty(p);
        return true;
    }

    public static boolean abandon(ServerPlayer p) {
        SovereignState s = SovereignService.state(p);
        if (s.active < 0) {
            return false;
        }
        s.active = -1;
        s.champion = null;
        s.progress = 0;
        s.stage = 0;
        SovereignService.dirty(p);
        return true;
    }

    public static boolean claim(ServerPlayer p) {
        SovereignState s = SovereignService.state(p);
        if (!s.ready()) {
            return false;
        }
        s.completed = Math.min(0x7FFFFFFE, s.completed + 1);
        s.cycle = Math.min(0x1FFFFFFFFFFFFFFFL, s.cycle + 1L);
        s.active = -1;
        s.champion = null;
        s.progress = 0;
        s.stage = 0;
        SovereignService.dirty(p);
        ShadowService.tell(p, "Sovereign trail complete! Choose a new trail or equip an earned crest.");
        return true;
    }

    public static boolean crest(ServerPlayer p, int value) {
        SovereignState s = SovereignService.state(p);
        if (value < 0 || value >= SovereignState.CRESTS.length || s.completed < SovereignState.UNLOCKS[value]) {
            return false;
        }
        s.crest = value;
        SovereignService.dirty(p);
        return true;
    }

    public static void progress(ServerPlayer p, UUID id, SovereignState.Goal goal) {
        if (p == null || id == null) {
            return;
        }
        SovereignState s = SovereignService.state(p);
        if (s.active < 0 || s.ready() || !id.equals(s.champion) || s.goal() != goal || !ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.containsKey(id)) {
            return;
        }
        if (++s.progress >= s.target()) {
            s.progress = 0;
            ++s.stage;
            ShadowService.tell(p, s.ready() ? "Sovereign trail ready! Claim your crest progress in the journal." : "Sovereign trail stage complete. Your next objective is ready.");
        }
        SovereignService.dirty(p);
    }

    public static void party(ServerPlayer p, Collection<UUID> party, SovereignState.Goal goal) {
        SovereignState s = SovereignService.state(p);
        if (s.champion != null && party.contains(s.champion)) {
            SovereignService.progress(p, s.champion, goal);
        }
    }

    public static CompoundTag view(ServerPlayer p) {
        CompoundTag n = SovereignService.state(p).save();
        ArmyData.Army a = ArmyData.get(p.f_8924_).army(p.m_20148_());
        ShadowRecord r = a.shadows.get(SovereignService.state((ServerPlayer)p).champion);
        n.m_128359_("ChampionName", r == null ? "No companion selected" : r.name);
        n.m_128379_("ArtsUnlocked", a.warfront.wonOnce);
        return n;
    }

    private static void dirty(ServerPlayer p) {
        ArmyData.get(p.f_8924_).m_77762_();
    }
}

