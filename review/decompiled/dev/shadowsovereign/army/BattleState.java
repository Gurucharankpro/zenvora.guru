/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.army;

import dev.shadowsovereign.army.BattleRules;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public final class BattleState {
    public BattleRules.Squad squad = BattleRules.Squad.UNASSIGNED;
    public BattleRules.Formation formation = BattleRules.Formation.LOOSE;
    public BattleRules.Priority priority = BattleRules.Priority.DEFAULT;
    public int retreat;
    public final List<BlockPos> route = new ArrayList<BlockPos>();
    public String routeDimension = "";
    public int waypoint;
    public boolean paused;
    public float shield;
    public long shieldUntil;
    public UUID commandedTarget;
    public transient long stuckSince;
    public transient Vec3 lastProgress;
    public transient int failedWaypoints;

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128359_("Squad", this.squad.name());
        n.m_128359_("Formation", this.formation.name());
        n.m_128359_("Priority", this.priority.name());
        n.m_128405_("Retreat", this.retreat);
        n.m_128359_("RouteDimension", this.routeDimension);
        n.m_128388_("Route", this.route.stream().mapToLong(BlockPos::m_121878_).toArray());
        n.m_128405_("Waypoint", this.waypoint);
        n.m_128379_("Paused", this.paused);
        n.m_128350_("Shield", this.shield);
        n.m_128356_("ShieldUntil", this.shieldUntil);
        if (this.commandedTarget != null) {
            n.m_128362_("CommandedTarget", this.commandedTarget);
        }
        return n;
    }

    public static BattleState load(CompoundTag n) {
        BattleState b = new BattleState();
        b.squad = BattleRules.read(BattleRules.Squad.class, n.m_128461_("Squad"), BattleRules.Squad.UNASSIGNED);
        if (b.squad == BattleRules.Squad.ALL) {
            b.squad = BattleRules.Squad.UNASSIGNED;
        }
        b.formation = BattleRules.read(BattleRules.Formation.class, n.m_128461_("Formation"), BattleRules.Formation.LOOSE);
        b.priority = BattleRules.read(BattleRules.Priority.class, n.m_128461_("Priority"), BattleRules.Priority.DEFAULT);
        b.retreat = switch (n.m_128451_("Retreat")) {
            case 10, 25, 50 -> n.m_128451_("Retreat");
            default -> 0;
        };
        b.routeDimension = n.m_128461_("RouteDimension");
        Arrays.stream(n.m_128467_("Route")).limit(8L).mapToObj(BlockPos::m_122022_).forEach(b.route::add);
        b.waypoint = b.route.isEmpty() ? 0 : Math.floorMod(n.m_128451_("Waypoint"), b.route.size());
        b.paused = n.m_128471_("Paused");
        b.shield = Float.isFinite(n.m_128457_("Shield")) ? Math.max(0.0f, Math.min(4.0f, n.m_128457_("Shield"))) : 0.0f;
        b.shieldUntil = n.m_128454_("ShieldUntil");
        if (n.m_128403_("CommandedTarget")) {
            b.commandedTarget = n.m_128342_("CommandedTarget");
        }
        return b;
    }
}

