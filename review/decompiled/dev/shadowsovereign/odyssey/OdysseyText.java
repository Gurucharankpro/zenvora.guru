/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerPlayer
 */
package dev.shadowsovereign.odyssey;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

public final class OdysseyText {
    public static MutableComponent c(String key, Object ... args) {
        return Component.m_237110_((String)("odyssey.shadowsovereign." + key), (Object[])args);
    }

    public static void tell(ServerPlayer p, String key, Object ... args) {
        p.m_213846_((Component)OdysseyText.c(key, args).m_130940_(ChatFormatting.AQUA));
    }
}

