/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package dev.shadowsovereign.royal;

import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalState;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class RoyalText {
    public static MutableComponent c(String key, Object ... args) {
        return Component.m_237110_((String)("royal.shadowsovereign." + key), (Object[])args).m_130938_(s -> s.m_131150_(new ResourceLocation("shadowsovereign", "arabic")));
    }

    public static void tell(ServerPlayer p, String key, Object ... args) {
        String limit = "RoyalNotice." + key;
        long now = p.f_8924_.m_129783_().m_46467_();
        if (p.getPersistentData().m_128454_(limit) > now) {
            return;
        }
        p.getPersistentData().m_128356_(limit, now + 40L);
        MutableComponent message = RoyalText.c(key, args);
        p.m_5661_((Component)message.m_130940_(ChatFormatting.LIGHT_PURPLE), true);
        RoyalState state = RoyalService.state(p);
        CompoundTag n = new CompoundTag();
        n.m_128359_("Text", Component.Serializer.m_130703_((Component)RoyalText.c(key, args)));
        n.m_128356_("At", now);
        n.m_128405_("Page", switch (key) {
            case "missing", "upgraded" -> 2;
            case "supply_empty" -> 6;
            case "deployed", "recalled" -> 1;
            case "course_requirement", "finish_activity", "activity_won", "defeat", "time_expired", "invalid_activity" -> 9;
            case "rescued" -> 5;
            default -> 13;
        });
        state.notices.add(0, n);
        while (state.notices.size() > 6) {
            state.notices.remove(6);
        }
    }
}

