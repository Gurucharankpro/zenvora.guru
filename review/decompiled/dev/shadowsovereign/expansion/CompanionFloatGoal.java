/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 */
package dev.shadowsovereign.expansion;

import dev.shadowsovereign.expansion.OpenMythics;
import dev.shadowsovereign.mythic.MythicBeast;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;

public class CompanionFloatGoal
extends FloatGoal {
    private final Mob companion;

    public CompanionFloatGoal(Mob mob) {
        super(mob);
        this.companion = mob;
    }

    public boolean m_8036_() {
        if (OpenMythics.isAquatic(((MythicBeast)this.companion).kind())) {
            return false;
        }
        return super.m_8036_();
    }
}

