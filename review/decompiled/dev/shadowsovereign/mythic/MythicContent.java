/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.entity.EntityAttributeCreationEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.PixelSpawnEgg;
import dev.shadowsovereign.mythic.ChimeMite;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.SoulBolt;
import dev.shadowsovereign.mythic.SoulDevourer;
import dev.shadowsovereign.network.MythicNetwork;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid="shadowsovereign", bus=Mod.EventBusSubscriber.Bus.MOD)
public final class MythicContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENTITY_TYPES, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    public static final RegistryObject<EntityType<SoulDevourer>> DEVOURER = ENTITIES.register("soul_devourer", () -> EntityType.Builder.m_20704_(SoulDevourer::new, (MobCategory)MobCategory.MONSTER).m_20699_(2.2f, 3.8f).m_20702_(12).m_20717_(2).m_20719_().m_20712_("shadowsovereign:soul_devourer"));
    public static final RegistryObject<EntityType<SoulBolt>> BOLT = ENTITIES.register("legend_soul_bolt", () -> EntityType.Builder.m_20704_(SoulBolt::new, (MobCategory)MobCategory.MISC).m_20699_(0.25f, 0.25f).m_20702_(4).m_20717_(2).m_20712_("shadowsovereign:legend_soul_bolt"));
    public static final RegistryObject<EntityType<ChimeMite>> MITE = ENTITIES.register("chime_mite", () -> EntityType.Builder.m_20704_(ChimeMite::new, (MobCategory)MobCategory.MONSTER).m_20699_(0.6f, 0.5f).m_20702_(8).m_20712_("shadowsovereign:chime_mite"));
    public static final RegistryObject<Item> MITE_EGG = ITEMS.register("chime_mite_spawn_egg", () -> new PixelSpawnEgg((Supplier<? extends EntityType<? extends Mob>>)MITE, 2830389, 13939812, new Item.Properties()));
    public static final List<RegistryObject<EntityType<MythicBeast>>> BEASTS = new ArrayList<RegistryObject<EntityType<MythicBeast>>>();
    public static final RegistryObject<Item> GRIMOIRE;
    public static final RegistryObject<Item> EGG;

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        ITEMS.register(bus);
    }

    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent e) {
        e.put((EntityType)DEVOURER.get(), SoulDevourer.attributes().m_22265_());
        e.put((EntityType)MITE.get(), ChimeMite.attributes().m_22265_());
        for (int i = 0; i < BEASTS.size(); ++i) {
            e.put((EntityType)BEASTS.get(i).get(), MythicBeast.attributes(i).m_22265_());
        }
    }

    static {
        for (int k = 0; k < MythicExpedition.IDS.length; ++k) {
            int kind = k;
            String id = MythicExpedition.IDS[k];
            RegistryObject type = ENTITIES.register(id, () -> EntityType.Builder.m_20704_((t, w) -> new MythicBeast((EntityType<? extends MythicBeast>)t, w, kind), (MobCategory)(kind == Integer.MIN_VALUE ? MobCategory.CREATURE : MobCategory.MONSTER)).m_20699_(MythicBeast.WIDTH[kind], MythicBeast.HEIGHT[kind]).m_20702_(12).m_20717_(2).m_20712_("shadowsovereign:" + id));
            BEASTS.add((RegistryObject<EntityType<MythicBeast>>)type);
            ITEMS.register(id + "_spawn_egg", () -> new PixelSpawnEgg((Supplier<? extends EntityType<? extends Mob>>)type, 2168363, kind == 1 ? 14300763 : 10380271, new Item.Properties()));
        }
        GRIMOIRE = ITEMS.register("mythic_grimoire", () -> new Item(new Item.Properties().m_41487_(1).m_41497_(Rarity.EPIC)){

            public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
                if (player instanceof ServerPlayer) {
                    ServerPlayer p = (ServerPlayer)player;
                    MythicNetwork.openGrimoire(p);
                }
                return InteractionResultHolder.m_19092_((Object)player.m_21120_(hand), (boolean)level.f_46443_);
            }
        });
        EGG = ITEMS.register("soul_devourer_spawn_egg", () -> new PixelSpawnEgg((Supplier<? extends EntityType<? extends Mob>>)DEVOURER, 1511973, 4322286, new Item.Properties()));
    }
}

