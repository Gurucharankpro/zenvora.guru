/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerPlayer
 */
package dev.shadowsovereign.eclipse;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

public final class EclipseBoard {
    private EclipseBoard() {
    }

    public static void init(ServerPlayer p, AdventureData.Session s) {
        ListTag units = new ListTag();
        ArrayList<UUID> ids = new ArrayList<UUID>(s.participants);
        for (int side = 0; side < 2; ++side) {
            for (int i = 0; i < 4; ++i) {
                EclipseState.Role role;
                CompoundTag n = new CompoundTag();
                ShadowRecord r = side == 0 && i < ids.size() ? EclipseService.record(p, ids.get(i)) : null;
                EclipseState.Role role2 = role = r != null ? EclipseService.state((ServerPlayer)p).build((UUID)r.id).role : EclipseState.Role.values()[i + 1];
                if (role == EclipseState.Role.NONE) {
                    role = EclipseState.Role.STRIKER;
                }
                n.m_128405_("Side", side);
                n.m_128405_("Role", role.ordinal());
                n.m_128405_("Cell", (side == 0 ? 7 : 0) * 8 + 1 + i * 2);
                n.m_128405_("HP", role == EclipseState.Role.GUARDIAN ? 14 : (role == EclipseState.Role.STRIKER ? 10 : 8));
                n.m_128405_("Max", n.m_128451_("HP"));
                n.m_128359_("Type", r == null ? "minecraft:allay" : r.type);
                if (r != null) {
                    n.m_128362_("Companion", r.id);
                }
                units.add((Object)n);
            }
        }
        s.eclipse.m_128365_("Units", (Tag)units);
        s.eclipse.m_128405_("Turn", 0);
        s.eclipse.m_128405_("Moved", -1);
    }

    public static int distance(int a, int b) {
        return Math.abs(a % 8 - b % 8) + Math.abs(a / 8 - b / 8);
    }

    private static int at(ListTag u, int cell) {
        for (int i = 0; i < u.size(); ++i) {
            CompoundTag n = u.m_128728_(i);
            if (n.m_128451_("HP") <= 0 || n.m_128451_("Cell") != cell) continue;
            return i;
        }
        return -1;
    }

    private static EclipseState.Role role(CompoundTag n) {
        return EclipseState.Role.values()[Math.max(1, Math.min(4, n.m_128451_("Role")))];
    }

    private static int attack(CompoundTag n) {
        return switch (EclipseBoard.role(n)) {
            case EclipseState.Role.GUARDIAN, EclipseState.Role.ASSASSIN -> 3;
            case EclipseState.Role.MEDIC -> 2;
            default -> 4;
        };
    }

    private static int range(CompoundTag n) {
        return EclipseBoard.role(n) == EclipseState.Role.MEDIC ? 2 : 1;
    }

    private static boolean path(ListTag units, int from, int to, int steps) {
        ArrayDeque<Integer> q = new ArrayDeque<Integer>();
        HashMap<Integer, Integer> seen = new HashMap<Integer, Integer>();
        q.add(from);
        seen.put(from, 0);
        while (!q.isEmpty()) {
            int at = (Integer)q.remove();
            int d = (Integer)seen.get(at);
            if (at == to) {
                return true;
            }
            if (d >= steps) continue;
            for (int next : new int[]{at - 8, at + 8, at - 1, at + 1}) {
                if (next < 0 || next >= 64 || EclipseBoard.distance(at, next) != 1 || seen.containsKey(next) || EclipseBoard.at(units, next) >= 0) continue;
                seen.put(next, d + 1);
                q.add(next);
            }
        }
        return false;
    }

    public static boolean act(ServerPlayer p, int unit, int cell, int turn) {
        AdventureData.Session s = EclipseRuns.session(p);
        if (s == null || s.kind != AdventureData.Kind.BOARD || s.phase != AdventureData.Phase.ACTIVE || turn != s.eclipse.m_128451_("Turn")) {
            return false;
        }
        ListTag u = s.eclipse.m_128437_("Units", 10);
        if (unit == -1) {
            EclipseBoard.end(p, s, u);
            return true;
        }
        if (unit < 0 || unit >= 4 || cell < 0 || cell >= 64) {
            return false;
        }
        CompoundTag n = u.m_128728_(unit);
        int moved = s.eclipse.m_128451_("Moved");
        if (n.m_128451_("HP") <= 0 || moved >= 0 && moved != unit) {
            return false;
        }
        int target = EclipseBoard.at(u, cell);
        if (target < 0) {
            if (moved >= 0 || !EclipseBoard.path(u, n.m_128451_("Cell"), cell, EclipseBoard.role(n) == EclipseState.Role.ASSASSIN ? 3 : 2)) {
                return false;
            }
            n.m_128405_("Cell", cell);
            s.eclipse.m_128405_("Moved", unit);
            EclipseService.dirty(p);
            return true;
        }
        CompoundTag t = u.m_128728_(target);
        int d = EclipseBoard.distance(n.m_128451_("Cell"), cell);
        if (d > EclipseBoard.range(n)) {
            return false;
        }
        if (t.m_128451_("Side") == 0) {
            if (EclipseBoard.role(n) != EclipseState.Role.MEDIC || t.m_128451_("HP") >= t.m_128451_("Max")) {
                return false;
            }
            t.m_128405_("HP", Math.min(t.m_128451_("Max"), t.m_128451_("HP") + 3));
        } else {
            t.m_128405_("HP", Math.max(0, t.m_128451_("HP") - EclipseBoard.attack(n)));
        }
        EclipseBoard.end(p, s, u);
        return true;
    }

    private static boolean eliminated(ListTag u, int side) {
        return u.stream().map(t -> (CompoundTag)t).noneMatch(n -> n.m_128451_("Side") == side && n.m_128451_("HP") > 0);
    }

    private static void end(ServerPlayer p, AdventureData.Session s, ListTag u) {
        if (EclipseBoard.eliminated(u, 1)) {
            EclipseRuns.win(p, s);
            return;
        }
        int bestUnit = -1;
        int bestCell = -1;
        double score = -1.0E9;
        for (int i = 4; i < 8; ++i) {
            CompoundTag n = u.m_128728_(i);
            if (n.m_128451_("HP") <= 0) continue;
            for (int cell = 0; cell < 64; ++cell) {
                if (cell != n.m_128451_("Cell") && (EclipseBoard.at(u, cell) >= 0 || !EclipseBoard.path(u, n.m_128451_("Cell"), cell, EclipseBoard.role(n) == EclipseState.Role.ASSASSIN ? 3 : 2))) continue;
                int near = 99;
                double value = 0.0;
                for (int j = 0; j < 4; ++j) {
                    CompoundTag t = u.m_128728_(j);
                    if (t.m_128451_("HP") <= 0) continue;
                    int dist = EclipseBoard.distance(cell, t.m_128451_("Cell"));
                    near = Math.min(near, dist);
                    if (dist > EclipseBoard.range(n)) continue;
                    value = Math.max(value, (double)(10 + EclipseBoard.attack(n) + (t.m_128451_("HP") <= EclipseBoard.attack(n) ? 30 : 0)));
                }
                if (!((value -= (double)near * 0.7) > score)) continue;
                score = value;
                bestUnit = i;
                bestCell = cell;
            }
        }
        if (bestUnit >= 0) {
            CompoundTag n = u.m_128728_(bestUnit);
            n.m_128405_("Cell", bestCell);
            int victim = -1;
            for (int i = 0; i < 4; ++i) {
                CompoundTag t = u.m_128728_(i);
                if (t.m_128451_("HP") <= 0 || EclipseBoard.distance(bestCell, t.m_128451_("Cell")) > EclipseBoard.range(n) || victim >= 0 && t.m_128451_("HP") >= u.m_128728_(victim).m_128451_("HP")) continue;
                victim = i;
            }
            if (victim >= 0) {
                CompoundTag t = u.m_128728_(victim);
                t.m_128405_("HP", Math.max(0, t.m_128451_("HP") - EclipseBoard.attack(n)));
            }
        }
        s.eclipse.m_128405_("Moved", -1);
        s.eclipse.m_128405_("Turn", s.eclipse.m_128451_("Turn") + 1);
        if (EclipseBoard.eliminated(u, 0)) {
            EclipseRuns.fail(p, "board_defeat");
        }
        EclipseService.dirty(p);
    }
}

