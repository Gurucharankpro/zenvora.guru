/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.adventure;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.artisan.WorkshopShapes;
import dev.shadowsovereign.network.AdventureNetwork;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class AdventureContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    public static final RegistryObject<Block> SANCTUARY = BLOCKS.register("soul_sanctuary", () -> new Block(BlockBehaviour.Properties.m_284310_().m_284180_(MapColor.f_283889_).m_60978_(3.5f).m_60953_(s -> 10).m_60955_()){

        public VoxelShape m_5940_(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
            return WorkshopShapes.shape("soul_sanctuary", Direction.NORTH);
        }

        public void m_6402_(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
            super.m_6402_(level, pos, state, placer, stack);
            if (placer instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)placer;
                AdventureService.bind(p, pos);
            }
        }

        public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (player instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)player;
                UUID owner = AdventureService.hubOwner(p.f_8924_, level.m_46472_().m_135782_().toString(), pos);
                if (owner == null) {
                    if (!AdventureService.bind(p, pos)) {
                        return InteractionResult.FAIL;
                    }
                } else if (!owner.equals(p.m_20148_())) {
                    AdventureService.tell(p, "This Sanctuary belongs to another player.");
                    return InteractionResult.FAIL;
                }
                AdventureNetwork.open(p);
            }
            return InteractionResult.m_19078_((boolean)level.f_46443_);
        }

        public void m_6810_(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
            if (!state.m_60713_(replacement.m_60734_()) && !level.f_46443_) {
                UUIDHolder.removed(level, pos);
            }
            super.m_6810_(state, level, pos, replacement, moving);
        }

        public void m_214162_(BlockState state, Level level, BlockPos pos, RandomSource random) {
            for (int i = 0; i < 2; ++i) {
                level.m_7106_((ParticleOptions)ParticleTypes.f_123745_, (double)pos.m_123341_() + 0.5 + (random.m_188500_() - 0.5) * 0.6, (double)pos.m_123342_() + 1.05 + random.m_188500_() * 0.5, (double)pos.m_123343_() + 0.5 + (random.m_188500_() - 0.5) * 0.6, 0.0, 0.01, 0.0);
            }
        }
    });
    public static final RegistryObject<Item> SANCTUARY_ITEM = ITEMS.register("soul_sanctuary", () -> new BlockItem((Block)SANCTUARY.get(), new Item.Properties().m_41497_(Rarity.EPIC)));
    public static final RegistryObject<Item> COMPASS = ITEMS.register("echo_compass", () -> new Item(new Item.Properties().m_41487_(1).m_41497_(Rarity.RARE)){

        public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
            if (player instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)player;
                AdventureService.scan(p);
            }
            return InteractionResultHolder.m_19092_((Object)player.m_21120_(hand), (boolean)level.f_46443_);
        }

        public boolean m_5812_(ItemStack stack) {
            return true;
        }
    });

    private AdventureContent() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        bus.addListener(AdventureContent::creative);
    }

    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.f_256797_) {
            event.accept(SANCTUARY_ITEM);
            event.accept(COMPASS);
        }
    }

    private static final class UUIDHolder {
        private UUIDHolder() {
        }

        static void removed(Level level, BlockPos pos) {
            MinecraftServer server = level.m_7654_();
            if (server == null) {
                return;
            }
            UUID owner = AdventureService.hubOwner(server, level.m_46472_().m_135782_().toString(), pos);
            if (owner == null) {
                return;
            }
            AdventureWorld.pause(server, owner);
            AdventureData data = AdventureData.get(server);
            data.journal((UUID)owner).bound = false;
            data.m_77762_();
            ServerPlayer player = server.m_6846_().m_11259_(owner);
            if (player != null) {
                AdventureService.tell(player, "Sanctuary removed. Your progress is safe; bind another hub to resume adventures.");
            }
        }
    }
}

