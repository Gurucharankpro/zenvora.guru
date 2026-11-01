/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Mob
 */
package dev.shadowsovereign.journey;

import dev.shadowsovereign.legacy.LegacyService;
import dev.shadowsovereign.network.JourneyNetwork;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

public final class JourneyView {
    public static boolean start(ServerPlayer p, UUID id) {
        Mob m = LegacyService.eligible(p, id, 1, 32);
        if (m == null || !p.m_6084_() || p.m_5833_()) {
            return false;
        }
        CompoundTag n = p.getPersistentData();
        n.m_128362_("JourneyView", id);
        n.m_128347_("JourneyViewX", p.m_20185_());
        n.m_128347_("JourneyViewY", p.m_20186_());
        n.m_128347_("JourneyViewZ", p.m_20189_());
        JourneyNetwork.send(p, false);
        return true;
    }

    public static boolean active(ServerPlayer p) {
        return p.getPersistentData().m_128403_("JourneyView");
    }

    public static void exit(ServerPlayer p) {
        if (JourneyView.active(p)) {
            p.getPersistentData().m_128473_("JourneyView");
            JourneyNetwork.send(p, false);
        }
    }

    public static void tick(ServerPlayer p) {
        if (!JourneyView.active(p)) {
            return;
        }
        CompoundTag n = p.getPersistentData();
        Mob m = LegacyService.eligible(p, n.m_128342_("JourneyView"), 1, 32);
        if (!p.m_6084_() || m == null || p.m_20275_(n.m_128459_("JourneyViewX"), n.m_128459_("JourneyViewY"), n.m_128459_("JourneyViewZ")) > 0.01) {
            JourneyView.exit(p);
        } else if (p.f_19797_ % 10 == 0) {
            JourneyNetwork.send(p, false);
        }
    }
}

