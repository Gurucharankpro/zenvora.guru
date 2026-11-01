/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package dev.shadowsovereign.eclipse;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class EclipseText {
    private EclipseText() {
    }

    public static MutableComponent c(String key, Object ... args) {
        return Component.m_237110_((String)("eclipse.shadowsovereign." + key), (Object[])args).m_130938_(style -> style.m_131150_(new ResourceLocation("shadowsovereign", "arabic")));
    }

    public static void tell(ServerPlayer p, String key, Object ... args) {
        p.m_213846_((Component)EclipseText.c(key, args).m_130940_(ChatFormatting.LIGHT_PURPLE));
    }

    public static String key(Enum<?> e) {
        return e.getClass().getSimpleName().toLowerCase(Locale.ROOT) + "." + e.name().toLowerCase(Locale.ROOT);
    }
}

