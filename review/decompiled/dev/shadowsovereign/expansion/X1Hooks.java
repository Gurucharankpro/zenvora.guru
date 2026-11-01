/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.expansion;

import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicProfiles;
import dev.shadowsovereign.mythic.SoulDevourer;
import dev.shadowsovereign.soulforge.CompanionService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class X1Hooks {
    private static boolean warned;
    private static boolean disabled;

    private X1Hooks() {
    }

    private static void run(MinecraftServer minecraftServer, String string) {
        block4: {
            if (disabled) {
                return;
            }
            try {
                minecraftServer.m_129892_().m_230957_(minecraftServer.m_129893_(), string);
            }
            catch (LinkageError | RuntimeException throwable) {
                if (throwable instanceof LinkageError) {
                    disabled = true;
                }
                if (warned) break block4;
                warned = true;
                System.err.println("Soulbound X1 visual command unavailable: " + String.valueOf(throwable));
            }
        }
    }

    public static void emit(Entity entity, int n) {
        X1Hooks.emitKey(entity, n < 10 ? "0" + n : "" + n, n);
    }

    private static void emitKey(Entity entity, String string, int n) {
        if (entity == null || entity.m_9236_().f_46443_ || entity.m_20194_() == null || n < 1 || n > 15) {
            return;
        }
        double d = entity.m_20185_();
        double d2 = entity.m_20186_();
        double d3 = entity.m_20189_();
        if (!(Double.isFinite(d) && Double.isFinite(d2) && Double.isFinite(d3))) {
            return;
        }
        MinecraftServer minecraftServer = entity.m_20194_();
        X1Hooks.run(minecraftServer, "function shadowsovereign:x1/init");
        String string2 = string;
        X1Hooks.run(minecraftServer, "execute in " + String.valueOf(entity.m_9236_().m_46472_().m_135782_()) + " positioned " + d + " " + d2 + " " + d3 + " run function shadowsovereign:x1/event/" + string2);
    }

    public static void tick(MinecraftServer minecraftServer) {
        if (minecraftServer == null) {
            return;
        }
        X1Hooks.run(minecraftServer, "function shadowsovereign:x1/init");
        for (ServerLevel serverLevel : minecraftServer.m_129785_()) {
            X1Hooks.run(minecraftServer, "execute in " + String.valueOf(serverLevel.m_46472_().m_135782_()) + " run function shadowsovereign:x1/tick_world");
        }
    }

    public static void summoned(ServerPlayer serverPlayer, ShadowRecord shadowRecord) {
        Mob mob = ShadowService.active(serverPlayer.f_8924_, shadowRecord);
        if (mob == null) {
            return;
        }
        int n = shadowRecord.tier.ordinal();
        X1Hooks.emit((Entity)mob, mob instanceof MythicBeast || mob instanceof SoulDevourer ? 14 : (n >= 3 ? 3 : 2));
        if (n >= 4) {
            X1Hooks.emit((Entity)mob, 9);
        } else if (n >= 2) {
            X1Hooks.emit((Entity)mob, 8);
        }
    }

    public static void acquired(ServerPlayer serverPlayer) {
        X1Hooks.emit((Entity)serverPlayer, 1);
    }

    public static void recall(Mob mob, int n) {
        X1Hooks.emit((Entity)mob, 4);
    }

    public static void command(ServerPlayer serverPlayer, ShadowRecord shadowRecord, Order order) {
        Mob mob = ShadowService.active(serverPlayer.f_8924_, shadowRecord);
        X1Hooks.emit((Entity)(mob == null ? serverPlayer : mob), order == Order.FOLLOW ? 6 : 7);
    }

    public static void bond(ServerPlayer serverPlayer, ShadowRecord shadowRecord, int n) {
        if (CompanionService.bondRank(shadowRecord.bond) > n) {
            Mob mob = ShadowService.active(serverPlayer.f_8924_, shadowRecord);
            X1Hooks.emit((Entity)(mob == null ? serverPlayer : mob), 5);
        }
    }

    public static void attack(MythicBeast mythicBeast, MythicProfiles.Move move) {
        if (!ShadowService.isShadow((Entity)mythicBeast)) {
            return;
        }
        if (move.effect() == 5) {
            X1Hooks.emit((Entity)mythicBeast, 13);
        } else if (move.effect() == 0) {
            String string = move.clip();
            String string2 = string.contains("thrust") || string.contains("stab") ? "slash_thrust" : (string.contains("slam") || string.contains("hammer") ? "slash_overhead" : (string.contains("slash") ? "slash_diagonal" : "slash_horizontal"));
            X1Hooks.emitKey((Entity)mythicBeast, "11_" + string2, 11);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void damage(LivingDamageEvent livingDamageEvent) {
        if (livingDamageEvent.isCanceled() || livingDamageEvent.getAmount() <= 0.0f || livingDamageEvent.getEntity().m_9236_().f_46443_) {
            return;
        }
        Entity entity = livingDamageEvent.getSource().m_7639_();
        if (entity == null || !ShadowService.isShadow(entity)) {
            return;
        }
        if (!(entity instanceof MythicBeast) && livingDamageEvent.getSource().m_7640_() == entity) {
            X1Hooks.emit(entity, 11);
        }
        X1Hooks.emit((Entity)livingDamageEvent.getEntity(), 12);
    }
}

