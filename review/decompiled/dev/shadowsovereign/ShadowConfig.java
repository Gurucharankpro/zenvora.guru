/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$BooleanValue
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$ConfigValue
 *  net.minecraftforge.common.ForgeConfigSpec$DoubleValue
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 */
package dev.shadowsovereign;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ShadowConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MAX_ARMY;
    public static final ForgeConfigSpec.IntValue MAX_ACTIVE;
    public static final ForgeConfigSpec.DoubleValue FALLBACK_DAMAGE;
    public static final ForgeConfigSpec.BooleanValue RECRUIT_BOSSES;
    public static final ForgeConfigSpec.BooleanValue ENVIRONMENT_PROTECTION;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCKLIST;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("Shadow Sovereign \u2014 server rules, shared by all players").push("army");
        MAX_ARMY = b.comment("Maximum stored + summoned shadows per player.").defineInRange("maxArmySize", 256, 1, 1024);
        MAX_ACTIVE = b.comment("Maximum simultaneously summoned shadows per player.").defineInRange("maxActiveShadows", 24, 1, 128);
        FALLBACK_DAMAGE = b.comment("Melee damage for passive mobs without an attack-damage attribute.").defineInRange("passiveMobDamage", 6.0, 1.0, 1000.0);
        RECRUIT_BOSSES = b.comment("Allow mobs tagged as Forge bosses. Bespoke boss AI may ignore orders.").define("recruitBosses", true);
        ENVIRONMENT_PROTECTION = b.comment("Protect shadows from sunlight/fire, drowning, falling, suffocation and drying out.").define("environmentProtection", true);
        BLOCKLIST = b.comment("Registry IDs that cannot be recruited, e.g. modid:custom_boss.").defineListAllowEmpty("blockedEntities", List.of(), value -> {
            String s;
            return value instanceof String && ResourceLocation.m_135830_((String)(s = (String)value));
        });
        b.pop();
        SPEC = b.build();
    }
}

