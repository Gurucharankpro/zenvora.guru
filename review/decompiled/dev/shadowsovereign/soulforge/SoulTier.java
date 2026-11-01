/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 */
package dev.shadowsovereign.soulforge;

import net.minecraft.ChatFormatting;

public enum SoulTier {
    COMMON("Common", 1.0, 8140, ChatFormatting.GRAY),
    RARE("Rare", 1.25, 9340, ChatFormatting.AQUA),
    EPIC("Epic", 1.6, 9840, ChatFormatting.LIGHT_PURPLE),
    LEGENDARY("Legendary", 2.25, 9940, ChatFormatting.GOLD),
    CRAZY("Crazy!!", 3.25, 9990, ChatFormatting.RED),
    CHOSEN("Chosen One", 5.0, 10000, ChatFormatting.YELLOW);

    public final String label;
    public final double power;
    public final ChatFormatting color;
    private final int ceiling;

    private SoulTier(String label, double power, int ceiling, ChatFormatting color) {
        this.label = label;
        this.power = power;
        this.ceiling = ceiling;
        this.color = color;
    }

    public static SoulTier roll(int ticket, boolean alreadyChosen) {
        if (ticket < 0 || ticket >= 10000) {
            throw new IllegalArgumentException("Soul ticket outside 0..9999");
        }
        for (SoulTier tier : SoulTier.values()) {
            if (ticket >= tier.ceiling) continue;
            return tier == CHOSEN && alreadyChosen ? CRAZY : tier;
        }
        throw new AssertionError();
    }

    public static SoulTier read(String name) {
        try {
            return SoulTier.valueOf(name);
        }
        catch (IllegalArgumentException ignored) {
            return COMMON;
        }
    }
}

