/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.extensions.IForgeMenuType
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.royal;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.network.RoyalNetwork;
import dev.shadowsovereign.royal.RoyalBagMenu;
import dev.shadowsovereign.royal.RoyalService;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.royal.RoyalText;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class RoyalContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCK_ENTITY_TYPES, (String)"shadowsovereign");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.MENU_TYPES, (String)"shadowsovereign");
    public static final RegistryObject<RoyalBlock> DESK = BLOCKS.register("sovereign_desk", () -> new RoyalBlock(false));
    public static final RegistryObject<RoyalBlock> BANNER = BLOCKS.register("field_standard", () -> new RoyalBlock(true));
    public static final RegistryObject<Item> DESK_ITEM = ITEMS.register("sovereign_desk", () -> new BlockItem((Block)DESK.get(), new Item.Properties()));
    public static final RegistryObject<Item> BANNER_ITEM = ITEMS.register("field_standard", () -> new BlockItem((Block)BANNER.get(), new Item.Properties()));
    public static final RegistryObject<Item> CHARM = ITEMS.register("rescue_charm", () -> new Item(new Item.Properties().m_41487_(1).m_41497_(Rarity.RARE)));
    public static final RegistryObject<BlockEntityType<RoyalEntity>> ENTITY = ENTITIES.register("royal_station", () -> BlockEntityType.Builder.m_155273_(RoyalEntity::new, (Block[])new Block[]{(Block)DESK.get(), (Block)BANNER.get()}).m_58966_(null));
    public static final RegistryObject<MenuType<RoyalBagMenu>> BAGS = MENUS.register("team_bags", () -> IForgeMenuType.create(RoyalBagMenu::new));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        MENUS.register(bus);
        bus.addListener(e -> {
            if (e.getTabKey() == CreativeModeTabs.f_256797_) {
                e.accept(DESK_ITEM);
                e.accept(BANNER_ITEM);
                e.accept(CHARM);
            }
        });
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean ownsDesk(ServerPlayer p, BlockPos at) {
        if (at == null) return false;
        if (!(p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 64.0)) return false;
        if (!p.m_284548_().m_46805_(at)) return false;
        if (!p.m_9236_().m_8055_(at).m_60713_((Block)DESK.get())) return false;
        BlockEntity blockEntity = p.m_9236_().m_7702_(at);
        if (!(blockEntity instanceof RoyalEntity)) return false;
        RoyalEntity be = (RoyalEntity)blockEntity;
        if (!p.m_20148_().equals(be.owner)) return false;
        return true;
    }

    public static boolean nearDesk(ServerPlayer p) {
        RoyalState r = RoyalService.state(p);
        if (RoyalContent.ownsDesk(p, r.desk)) {
            return true;
        }
        long now = RoyalService.now(p);
        if (now - r.deskScan < 20L && now >= r.deskScan) {
            return false;
        }
        r.deskScan = now;
        for (BlockPos at : BlockPos.m_121940_((BlockPos)p.m_20183_().m_7918_(-8, -6, -8), (BlockPos)p.m_20183_().m_7918_(8, 6, 8))) {
            if (!RoyalContent.ownsDesk(p, at)) continue;
            r.desk = at.m_7949_();
            return true;
        }
        r.desk = null;
        return false;
    }

    public static final class RoyalEntity
    extends BlockEntity {
        public UUID owner;
        public int team;

        public RoyalEntity(BlockPos at, BlockState state) {
            super((BlockEntityType)ENTITY.get(), at, state);
        }

        protected void m_183515_(CompoundTag n) {
            super.m_183515_(n);
            if (this.owner != null) {
                n.m_128362_("Owner", this.owner);
            }
            n.m_128405_("Team", this.team);
        }

        public void m_142466_(CompoundTag n) {
            super.m_142466_(n);
            this.owner = n.m_128403_("Owner") ? n.m_128342_("Owner") : null;
            this.team = Math.max(0, Math.min(4, n.m_128451_("Team")));
        }

        public CompoundTag m_5995_() {
            return this.m_187482_();
        }

        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.m_195640_((BlockEntity)this);
        }
    }

    public static final class RoyalBlock
    extends BaseEntityBlock {
        public final boolean standard;

        RoyalBlock(boolean standard) {
            super(BlockBehaviour.Properties.m_284310_().m_60978_(3.0f).m_60955_().m_60953_(s -> 5));
            this.standard = standard;
        }

        public RenderShape m_7514_(BlockState state) {
            return RenderShape.MODEL;
        }

        public BlockEntity m_142194_(BlockPos pos, BlockState state) {
            return new RoyalEntity(pos, state);
        }

        public void m_6402_(Level l, BlockPos at, BlockState state, LivingEntity user, ItemStack item) {
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                BlockEntity blockEntity = l.m_7702_(at);
                if (blockEntity instanceof RoyalEntity) {
                    RoyalEntity be = (RoyalEntity)blockEntity;
                    be.owner = p.m_20148_();
                    be.team = RoyalService.state((ServerPlayer)p).selectedTeam;
                    be.m_6596_();
                    l.m_7260_(at, state, state, 3);
                    if (this.standard) {
                        RoyalService.bindBanner(p, at, be.team);
                    } else {
                        RoyalService.state((ServerPlayer)p).desk = at.m_7949_();
                    }
                }
            }
        }

        public InteractionResult m_6227_(BlockState state, Level l, BlockPos at, Player user, InteractionHand hand, BlockHitResult hit) {
            if (user instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)user;
                BlockEntity blockEntity = l.m_7702_(at);
                if (blockEntity instanceof RoyalEntity) {
                    RoyalEntity be = (RoyalEntity)blockEntity;
                    if (be.owner == null) {
                        be.owner = p.m_20148_();
                        be.m_6596_();
                    }
                    if (!p.m_20148_().equals(be.owner)) {
                        RoyalText.tell(p, "foreign_station", new Object[0]);
                        return InteractionResult.FAIL;
                    }
                    if (this.standard) {
                        RoyalService.bindBanner(p, at, RoyalService.state((ServerPlayer)p).selectedTeam);
                    }
                    RoyalNetwork.send(p, true, this.standard ? 1 : 2);
                }
            }
            return InteractionResult.m_19078_((boolean)l.f_46443_);
        }

        public void m_6810_(BlockState state, Level l, BlockPos at, BlockState next, boolean moving) {
            BlockEntity blockEntity;
            if (!state.m_60713_(next.m_60734_()) && !l.f_46443_ && (blockEntity = l.m_7702_(at)) instanceof RoyalEntity) {
                RoyalEntity be = (RoyalEntity)blockEntity;
                if (be.owner != null) {
                    ArmyData.Army a = ArmyData.get(l.m_7654_()).army(be.owner);
                    if (a.royal.banners.entrySet().removeIf(e -> ((RoyalState.Banner)e.getValue()).pos().equals((Object)at) && ((RoyalState.Banner)e.getValue()).dimension().equals(l.m_46472_().m_135782_().toString()))) {
                        ArmyData.get(l.m_7654_()).m_77762_();
                    }
                }
            }
            super.m_6810_(state, l, at, next, moving);
        }
    }
}

