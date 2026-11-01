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
package dev.shadowsovereign.soullife;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class SoulLifeText {
    public static MutableComponent c(String key, Object ... args) {
        return Component.m_237110_((String)("soullife.shadowsovereign." + key), (Object[])args).m_130938_(s -> s.m_131150_(new ResourceLocation("shadowsovereign", "arabic")));
    }

    public static void tell(ServerPlayer p, String key, Object ... args) {
        p.m_5661_((Component)SoulLifeText.c(key, args).m_130940_(ChatFormatting.AQUA), false);
    }
}

