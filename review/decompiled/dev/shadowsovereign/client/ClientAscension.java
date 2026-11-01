/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.joml.Vector3f
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.shadowsovereign.client.AscensionScreen;
import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.MenuTheme;
import dev.shadowsovereign.network.AscensionNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ClientAscension {
    public static CompoundTag data = new CompoundTag();
    public static final KeyMapping CLAIM = new KeyMapping("key.shadowsovereign.claim", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, 82, "key.categories.shadowsovereign");
    public static final KeyMapping INTERACT = new KeyMapping("key.shadowsovereign.objective", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, 71, "key.categories.shadowsovereign");

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent e) {
        e.register(CLAIM);
        e.register(INTERACT);
    }

    public static String mode() {
        String value = data.m_128461_("RecruitMode");
        return value.isEmpty() ? (ClientArmy.view.autoRecruit() ? "Manual" : "Off") : value;
    }

    public static void accept(AscensionNetwork.View view) {
        Screen screen;
        data = view.data().m_6426_();
        Minecraft mc = Minecraft.m_91087_();
        if (view.open() && mc.f_91074_ != null) {
            mc.m_91152_((Screen)new AscensionScreen());
        }
        if ((screen = mc.f_91080_) instanceof AscensionScreen) {
            AscensionScreen screen2 = (AscensionScreen)screen;
            screen2.updated();
        }
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
    public static final class AscensionInputEvents {
        @SubscribeEvent
        public static void key(InputEvent.Key e) {
            if (e.getAction() != 1) {
                return;
            }
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ == null || mc.f_91080_ != null) {
                return;
            }
            InputConstants.Key key = InputConstants.m_84827_((int)e.getKey(), (int)e.getScanCode());
            if (CLAIM.isActiveAndMatches(key)) {
                while (CLAIM.m_90859_()) {
                }
                AscensionNetwork.request(AscensionNetwork.Action.CLAIM_SOUL, 0, false);
            }
            if (INTERACT.isActiveAndMatches(key)) {
                while (INTERACT.m_90859_()) {
                }
                AscensionNetwork.request(AscensionNetwork.Action.INTERACT, 0, false);
            }
        }

        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) {
                return;
            }
            boolean claim = false;
            boolean interact = false;
            while (CLAIM.m_90859_()) {
                claim = true;
            }
            while (INTERACT.m_90859_()) {
                interact = true;
            }
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ != null && mc.f_91080_ == null) {
                if (claim) {
                    AscensionNetwork.request(AscensionNetwork.Action.CLAIM_SOUL, 0, false);
                }
                if (interact) {
                    AscensionNetwork.request(AscensionNetwork.Action.INTERACT, 0, false);
                }
            }
            if (mc.f_91073_ == null || mc.f_91074_ == null || MenuTheme.reducedMotion) {
                return;
            }
            if (mc.m_91104_() || mc.f_91073_.m_46467_() % 10L != 0L) {
                return;
            }
            int budget = 24;
            for (Tag t : data.m_128437_("Souls", 10)) {
                double z;
                double y;
                CompoundTag n = (CompoundTag)t;
                double x = n.m_128459_("X");
                if (mc.f_91074_.m_20275_(x, y = n.m_128459_("Y"), z = n.m_128459_("Z")) > 2304.0 || budget <= 0) continue;
                budget -= 3;
                for (int layer = 0; layer < 2; ++layer) {
                    mc.f_91073_.m_7106_((ParticleOptions)new DustParticleOptions(new Vector3f(0.65f, 0.12f, 1.0f), 1.1f), x, y + 0.2 + (double)layer * 0.3, z, 0.0, 0.015, 0.0);
                }
                mc.f_91073_.m_7106_((ParticleOptions)ParticleTypes.f_123789_, x, y + 0.8, z, 0.0, 0.02, 0.0);
            }
        }

        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
            data = new CompoundTag();
            MenuTheme.clear();
        }
    }
}

