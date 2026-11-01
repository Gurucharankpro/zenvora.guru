/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 *  org.slf4j.Logger
 */
package dev.shadowsovereign;

import com.mojang.logging.LogUtils;
import dev.shadowsovereign.ModCreativeTab;
import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.adventure.AdventureContent;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.covenant.CovenantContent;
import dev.shadowsovereign.discovery.DiscoveryContent;
import dev.shadowsovereign.eclipse.EclipseContent;
import dev.shadowsovereign.expanse.ExpanseContent;
import dev.shadowsovereign.gloom.GloomContent;
import dev.shadowsovereign.monolith.MonolithContent;
import dev.shadowsovereign.moonveil.MoonveilContent;
import dev.shadowsovereign.mythic.MythicContent;
import dev.shadowsovereign.network.ShadowNetwork;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.portal.SoulGates;
import dev.shadowsovereign.realm.RealmContent;
import dev.shadowsovereign.royal.RoyalContent;
import dev.shadowsovereign.sentinel.SentinelContent;
import dev.shadowsovereign.soulforge.SoulforgeContent;
import dev.shadowsovereign.soullife.SoulLifeContent;
import dev.shadowsovereign.vigil.VigilContent;
import dev.shadowsovereign.warfront.WarfrontContent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(value="shadowsovereign")
public final class ShadowSovereign {
    public static final String ID = "shadowsovereign";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    public static final RegistryObject<Item> MONARCH_CREST = ITEMS.register("monarch_crest", () -> new Item(new Item.Properties().m_41497_(Rarity.EPIC)));
    public static final RegistryObject<Item> SIGIL = ITEMS.register("shadow_sigil", () -> new Item(new Item.Properties().m_41487_(1).m_41497_(Rarity.EPIC)){

        public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                ShadowService.sync(serverPlayer, true);
            }
            return InteractionResultHolder.m_19092_((Object)player.m_21120_(hand), (boolean)level.f_46443_);
        }

        public boolean m_5812_(ItemStack stack) {
            return true;
        }
    });

    public ShadowSovereign() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        SentinelContent.register(bus);
        DiscoveryContent.register(bus);
        GloomContent.register(bus);
        MoonveilContent.register(bus);
        MonolithContent.register(bus);
        CovenantContent.register(bus);
        ExpanseContent.register(bus);
        MythicContent.register(bus);
        RealmContent.register(bus);
        ModCreativeTab.register(bus);
        OdysseyContent.register(bus);
        SoulGates.register(bus);
        SoulLifeContent.register(bus);
        VigilContent.register(bus);
        RoyalContent.register(bus);
        EclipseContent.register(bus);
        WarfrontContent.register(bus);
        SoulforgeContent.register(bus);
        AdventureContent.register(bus);
        bus.addListener(this::creativeTab);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, (IConfigSpec)ShadowConfig.SPEC);
        ShadowNetwork.register();
    }

    private void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.f_256797_) {
            event.accept(SIGIL);
        }
    }
}

