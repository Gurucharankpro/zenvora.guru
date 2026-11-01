/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 */
package dev.shadowsovereign.royal;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.eclipse.EclipseRuns;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.royal.RoyalService;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class RoyalMutators {
    private static final UUID SPEED = UUID.fromString("11120000-0000-0000-0000-000000000001");
    private static final UUID ARMOR = UUID.fromString("11120000-0000-0000-0000-000000000002");

    public static void prepare(ServerPlayer p, AdventureData.Session s) {
        if (s.kind != AdventureData.Kind.ECLIPSE_RUN) {
            return;
        }
        int chosen = RoyalService.state((ServerPlayer)p).mutator;
        s.eclipse.m_128405_("RoyalMutator", chosen);
        if (chosen == 5) {
            s.eclipse.m_128356_("Deadline", RoyalService.now(p) + (s.eclipse.m_128454_("Deadline") - RoyalService.now(p)) * 3L / 4L);
        }
    }

    public static int selected(ServerPlayer p) {
        AdventureData.Session s = EclipseRuns.session(p);
        return s != null && s.kind == AdventureData.Kind.ECLIPSE_RUN && s.phase == AdventureData.Phase.ACTIVE && s.eclipse.m_128441_("RoyalMutator") ? s.eclipse.m_128451_("RoyalMutator") : -1;
    }

    public static int extra(AdventureData.Session s) {
        return s.kind == AdventureData.Kind.ECLIPSE_RUN && s.eclipse.m_128441_("RoyalMutator") && s.eclipse.m_128451_("RoyalMutator") == 2 ? 2 : 0;
    }

    public static int reward(AdventureData.Session s) {
        return s.kind == AdventureData.Kind.ECLIPSE_RUN && s.eclipse.m_128441_("RoyalMutator") && s.eclipse.m_128451_("RoyalMutator") >= 0 ? 2 : 0;
    }

    public static int regeneration(ServerPlayer p) {
        return RoyalMutators.selected(p) == 3 ? 3 : 4;
    }

    public static boolean permits(ServerPlayer p, EclipseState.Power power) {
        return RoyalMutators.selected(p) != 4 || power != EclipseState.Power.DASH;
    }

    public static void decorate(Mob m) {
        AdventureData.Session s = AdventureWorld.session((Entity)m);
        if (s == null || s.kind != AdventureData.Kind.ECLIPSE_RUN || !s.eclipse.m_128441_("RoyalMutator")) {
            return;
        }
        int rule = s.eclipse.m_128451_("RoyalMutator");
        AttributeInstance speed = m.m_21051_(Attributes.f_22279_);
        AttributeInstance armor = m.m_21051_(Attributes.f_22284_);
        if (rule == 0 && speed != null && speed.m_22111_(SPEED) == null) {
            speed.m_22118_(new AttributeModifier(SPEED, "Risk inscription", 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (rule == 1 && armor != null && armor.m_22111_(ARMOR) == null) {
            armor.m_22118_(new AttributeModifier(ARMOR, "Risk inscription", 4.0, AttributeModifier.Operation.ADDITION));
        }
    }
}

