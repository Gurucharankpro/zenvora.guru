/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.locale.Language
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentContents
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.contents.TranslatableContents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientChatReceivedEvent$System
 *  net.minecraftforge.client.event.RenderNameTagEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.entity.player.ItemTooltipEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientArmy;
import dev.shadowsovereign.client.ClientEclipse;
import dev.shadowsovereign.client.EclipseWorldRenderer;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.SoulDevourer;
import dev.shadowsovereign.network.ShadowNetwork;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
public final class ClientLocalization {
    public static void installLanguage(boolean refresh) {
        Language current = Language.m_128107_();
        if (current instanceof ModScopedLanguage) {
            ModScopedLanguage scoped = (ModScopedLanguage)current;
            if (!refresh) {
                return;
            }
            current = scoped.base;
        }
        Language.m_128114_((Language)new ModScopedLanguage(current));
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            ClientLocalization.installLanguage(false);
        }
    }

    @SubscribeEvent
    public static void chat(ClientChatReceivedEvent.System e) {
        TranslatableContents t;
        ComponentContents componentContents = e.getMessage().m_214077_();
        if (componentContents instanceof TranslatableContents && ((t = (TranslatableContents)componentContents).m_237508_().equals("message.shadowsovereign.text") || t.m_237508_().startsWith("odyssey.shadowsovereign.") || t.m_237508_().startsWith("mythic.shadowsovereign.") || t.m_237508_().startsWith("gloom.shadowsovereign.") || t.m_237508_().startsWith("expanse.shadowsovereign.") || t.m_237508_().startsWith("monolith.shadowsovereign.") || t.m_237508_().startsWith("chronicles.shadowsovereign."))) {
            e.setMessage((Component)ModLanguage.component(e.getMessage()));
        }
    }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent e) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey((Object)e.getItemStack().m_41720_());
        if (key != null && key.m_135827_().equals("shadowsovereign")) {
            for (int i = 0; i < e.getToolTip().size(); ++i) {
                e.getToolTip().set(i, ModLanguage.component((Component)e.getToolTip().get(i)));
            }
        }
    }

    @SubscribeEvent
    public static void name(RenderNameTagEvent e) {
        if (ClientArmy.shadows.contains(e.getEntity().m_20148_()) || e.getEntity() instanceof SoulDevourer || e.getEntity() instanceof MythicBeast) {
            String text = e.getContent().getString();
            for (ShadowNetwork.Entry r : ClientArmy.view.entries()) {
                if (r.name().isEmpty() || !text.contains(r.name())) continue;
                text = text.replace(r.name(), ModLanguage.user(r.name()));
                break;
            }
            MutableComponent translated = ModLanguage.component(text);
            MutableComponent formatted = translated.m_130948_(translated.m_7383_().m_131146_(e.getContent().m_7383_()));
            for (CompoundTag row : ClientEclipse.roster()) {
                if (!row.m_128403_("Entity") || !row.m_128342_("Entity").equals(e.getEntity().m_20148_())) continue;
                if (row.m_128451_("Palette") > 0) {
                    formatted.m_130938_(style -> style.m_178520_(EclipseWorldRenderer.COLORS[Math.floorMod(row.m_128451_("Palette"), 6)]));
                }
                if (row.m_128451_("Frame") <= 0) break;
                formatted = Component.m_237113_((String)(row.m_128451_("Frame") == 1 ? "\u25c7 " : "\u2726 ")).m_7220_((Component)formatted).m_130946_(row.m_128451_("Frame") == 1 ? " \u25c7" : " \u2726");
                break;
            }
            e.setContent((Component)formatted);
        }
    }

    private static final class ModScopedLanguage
    extends Language {
        private final Language base;

        ModScopedLanguage(Language base) {
            this.base = base;
        }

        public String m_118919_(String key, String fallback) {
            return ModLanguage.nativeKey(key) ? ModLanguage.nativeText(key, fallback) : this.base.m_118919_(key, fallback);
        }

        public boolean m_6722_(String key) {
            return ModLanguage.nativeKey(key) || this.base.m_6722_(key);
        }

        public boolean m_6627_() {
            return this.base.m_6627_();
        }

        public FormattedCharSequence m_5536_(FormattedText text) {
            return this.base.m_5536_(text);
        }

        public Map<String, String> getLanguageData() {
            HashMap<String, String> data = new HashMap<String, String>(this.base.getLanguageData());
            for (String key : List.of("item.shadowsovereign.shadow_sigil", "block.shadowsovereign.soul_sanctuary", "item.shadowsovereign.echo_compass", "block.shadowsovereign.warfront_beacon", "item.shadowsovereign.warfront_beacon")) {
                data.put(key, this.m_118919_(key, key));
            }
            return data;
        }
    }
}

