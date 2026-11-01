/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3f
 */
package dev.shadowsovereign.adventure;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public final class AdventureCosmetics {
    private static final DustParticleOptions AMETHYST = new DustParticleOptions(new Vector3f(0.8f, 0.25f, 1.0f), 1.1f);
    private static final DustParticleOptions GOLD = new DustParticleOptions(new Vector3f(1.0f, 0.75f, 0.15f), 1.1f);

    private AdventureCosmetics() {
    }

    public static boolean particles(Mob mob, int count) {
        AdventureData.Lore lore;
        Level level = mob.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel level2 = (ServerLevel)level;
        ShadowRecord r = ShadowService.record((Entity)mob);
        if (r == null) {
            return false;
        }
        AdventureData.Journal j = AdventureData.get((MinecraftServer)mob.m_20194_()).owners.get(ShadowService.ownerId((Entity)mob));
        AdventureData.Lore lore2 = lore = j == null ? null : j.lore.get(r.id);
        if (lore == null || lore.trail == AdventureData.Trail.DEFAULT) {
            return false;
        }
        SimpleParticleType particle = switch (lore.trail) {
            case AdventureData.Trail.AZURE -> ParticleTypes.f_123745_;
            case AdventureData.Trail.AMETHYST -> AMETHYST;
            case AdventureData.Trail.ECHO -> ParticleTypes.f_123810_;
            case AdventureData.Trail.GOLD -> GOLD;
            case AdventureData.Trail.VOID -> ParticleTypes.f_123789_;
            default -> ParticleTypes.f_123746_;
        };
        level2.m_8767_((ParticleOptions)particle, mob.m_20185_(), mob.m_20186_() + 0.5, mob.m_20189_(), Math.min(count, 18), (double)mob.m_20205_() * 0.4, Math.min(1.2, (double)mob.m_20206_() * 0.4), (double)mob.m_20205_() * 0.4, 0.01);
        return true;
    }
}

