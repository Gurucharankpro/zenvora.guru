/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package dev.shadowsovereign.localization;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ModMessages {
    public static final String KEY = "message.shadowsovereign.text";

    private ModMessages() {
    }

    public static MutableComponent of(String text) {
        return Component.m_264642_((String)KEY, (String)"%s", (Object[])new Object[]{text});
    }
}

