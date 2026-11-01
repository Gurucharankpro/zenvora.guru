/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.portal;

import dev.shadowsovereign.adventure.AdventureContent;
import dev.shadowsovereign.eclipse.EclipseCamp;
import dev.shadowsovereign.eclipse.EclipseContent;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.soullife.SoulLifeText;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class SoulGates {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"shadowsovereign");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"shadowsovereign");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCK_ENTITY_TYPES, (String)"shadowsovereign");
    public static final RegistryObject<Block> FRAME = BLOCKS.register("soul_gate_frame", () -> new Block(BlockBehaviour.Properties.m_284310_().m_60913_(30.0f, 1200.0f).m_60953_(s -> 4)));
    public static final RegistryObject<Item> FRAME_ITEM = ITEMS.register("soul_gate_frame", () -> new BlockItem((Block)FRAME.get(), new Item.Properties()));
    public static final RegistryObject<Surface> SURFACE = BLOCKS.register("soul_gate_surface", Surface::new);
    public static final RegistryObject<BlockEntityType<PortalEntity>> ENTITY = ENTITIES.register("soul_gate_surface", () -> BlockEntityType.Builder.m_155273_(PortalEntity::new, (Block[])new Block[]{(Block)SURFACE.get()}).m_58966_(null));
    private static final Set<String> removing = new HashSet<String>();
    private static final Set<String> activating = new HashSet<String>();

    private static String key(Level level, BlockPos core) {
        return String.valueOf(level.m_46472_().m_135782_()) + ":" + core.m_121878_();
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
    }

    private static boolean isFrame(BlockState state) {
        return state.m_60713_((Block)FRAME.get()) || state.m_60713_(Blocks.f_50080_) || state.m_60713_(Blocks.f_50723_);
    }

    public static Frame find(ServerLevel level, BlockPos core) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Frame frame = new Frame(core.m_7949_(), direction);
            if (frame.status(level) != Status.VALID) continue;
            return frame;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean activate(ServerPlayer p, BlockPos core) {
        PortalEntity portal;
        BlockEntity blockEntity;
        EclipseContent.CampEntity be;
        block14: {
            block13: {
                BlockEntity blockEntity2;
                if (p.m_20238_(Vec3.m_82512_((Vec3i)core)) > 64.0 || !p.m_284548_().m_46805_(core) || !((blockEntity2 = p.m_9236_().m_7702_(core)) instanceof EclipseContent.CampEntity)) break block13;
                be = (EclipseContent.CampEntity)blockEntity2;
                if (be.owner == null || p.m_20148_().equals(be.owner)) break block14;
            }
            return false;
        }
        Frame frame = SoulGates.find(p.m_284548_(), core);
        if (frame == null) {
            SoulLifeText.tell(p, "gate_frame_needed", new Object[0]);
            return false;
        }
        for (BlockPos at : frame.panels()) {
            if (!p.m_284548_().m_46805_(at) || !p.m_9236_().m_6857_().m_61937_(at)) {
                return false;
            }
            if (p.m_9236_().m_8055_(at).m_60795_()) continue;
            if (p.m_9236_().m_8055_(at).m_60713_((Block)SURFACE.get()) && (blockEntity = p.m_9236_().m_7702_(at)) instanceof PortalEntity) {
                portal = (PortalEntity)blockEntity;
                if (core.equals((Object)portal.core) && p.m_20148_().equals(portal.owner)) continue;
            }
            SoulLifeText.tell(p, "gate_clear", new Object[0]);
            return false;
        }
        if (!EclipseCamp.bind(p, EclipseState.Station.WAYGATE, core)) {
            SoulLifeText.tell(p, "gate_locked", new Object[0]);
            return false;
        }
        be.owner = p.m_20148_();
        be.m_6596_();
        activating.add(SoulGates.key(p.m_9236_(), core));
        try {
            for (BlockPos at : frame.panels()) {
                p.m_284548_().m_7731_(at, (BlockState)((Surface)((Object)SURFACE.get())).m_49966_().m_61124_((Property)BlockStateProperties.f_61364_, (Comparable)frame.direction.m_122434_()), 3);
                blockEntity = p.m_9236_().m_7702_(at);
                if (!(blockEntity instanceof PortalEntity)) continue;
                portal = (PortalEntity)blockEntity;
                portal.core = core.m_7949_();
                portal.direction = frame.direction;
                portal.owner = p.m_20148_();
                portal.m_6596_();
                p.m_9236_().m_7260_(at, portal.m_58900_(), portal.m_58900_(), 3);
            }
        }
        finally {
            activating.remove(SoulGates.key(p.m_9236_(), core));
        }
        SoulLifeText.tell(p, "gate_ready", new Object[0]);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void deactivate(ServerLevel level, Frame frame) {
        String key = SoulGates.key((Level)level, frame.core);
        if (!removing.add(key)) {
            return;
        }
        try {
            for (BlockPos at : frame.panels()) {
                BlockEntity blockEntity;
                if (!level.m_46805_(at) || !((blockEntity = level.m_7702_(at)) instanceof PortalEntity)) continue;
                PortalEntity portal = (PortalEntity)blockEntity;
                if (!frame.core.equals((Object)portal.core)) continue;
                level.m_7471_(at, false);
            }
        }
        finally {
            removing.remove(key);
        }
    }

    public static void enter(ServerPlayer p, PortalEntity portal) {
        if (portal.core == null || portal.owner == null || !p.m_20148_().equals(portal.owner) || p.m_6144_() || VigilCrown.active(p)) {
            return;
        }
        Frame frame = new Frame(portal.core, portal.direction);
        if (frame.status(p.m_284548_()) != Status.VALID) {
            return;
        }
        EclipseState state = EclipseService.state(p);
        if (state.gateUntil > 0L || EclipseService.now(p) < state.gateAt) {
            return;
        }
        List<EclipseState.Site> gates = EclipseCamp.gates(p);
        if (gates.size() < 2) {
            return;
        }
        int origin = -1;
        for (int i = 0; i < gates.size(); ++i) {
            if (!gates.get(i).pos().equals((Object)portal.core)) continue;
            origin = i;
        }
        if (origin < 0) {
            return;
        }
        for (int offset = 1; offset < gates.size(); ++offset) {
            int next = (origin + offset) % gates.size();
            EclipseState.Site site = gates.get(next);
            if (site.pos().equals((Object)portal.core)) continue;
            if (EclipseCamp.gate(p, next)) {
                SoulLifeText.tell(p, "gate_channel", new Object[0]);
            }
            return;
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void activateWithTool(PlayerInteractEvent.RightClickBlock e) {
        ServerPlayer p;
        block10: {
            block9: {
                Player player = e.getEntity();
                if (!(player instanceof ServerPlayer)) break block9;
                p = (ServerPlayer)player;
                if (e.getUseBlock() != Event.Result.DENY && e.getUseItem() != Event.Result.DENY) break block10;
            }
            return;
        }
        ItemStack held = p.m_21120_(e.getHand());
        if (!held.m_150930_(Items.f_42409_) && !held.m_150930_((Item)AdventureContent.COMPASS.get())) {
            return;
        }
        BlockPos clicked = e.getPos();
        boolean activated = false;
        boolean recognized = false;
        block0: for (int y = -4; y <= 1; ++y) {
            for (int dx = -3; dx <= 3; ++dx) {
                for (int dz = -3; dz <= 3; ++dz) {
                    if (dx != 0 && dz != 0) continue;
                    BlockPos at = clicked.m_7918_(dx, y, dz);
                    if (!p.m_284548_().m_46805_(at) || !p.m_9236_().m_8055_(at).m_60713_((Block)EclipseContent.STATIONS.get((Object)EclipseState.Station.WAYGATE).get()) || SoulGates.find(p.m_284548_(), at) == null) continue;
                    recognized = true;
                    activated = SoulGates.activate(p, at);
                    break block0;
                }
            }
        }
        if (recognized) {
            if (activated && held.m_150930_(Items.f_42409_) && !p.m_7500_()) {
                held.m_41622_(1, (LivingEntity)p, user -> user.m_21190_(e.getHand()));
            }
            e.setCancellationResult(activated ? InteractionResult.SUCCESS : InteractionResult.CONSUME);
            e.setCanceled(true);
        }
    }

    public record Frame(BlockPos core, Direction direction) {
        public List<BlockPos> panels() {
            ArrayList<BlockPos> result = new ArrayList<BlockPos>();
            for (int y = 1; y <= 3; ++y) {
                for (int x = 0; x <= 1; ++x) {
                    result.add(this.core.m_5484_(this.direction, x).m_6630_(y));
                }
            }
            return result;
        }

        public List<BlockPos> edges() {
            ArrayList<BlockPos> result = new ArrayList<BlockPos>();
            result.add(this.core.m_121945_(this.direction));
            for (int y = 1; y <= 3; ++y) {
                result.add(this.core.m_121945_(this.direction.m_122424_()).m_6630_(y));
                result.add(this.core.m_5484_(this.direction, 2).m_6630_(y));
            }
            result.add(this.core.m_6630_(4));
            result.add(this.core.m_121945_(this.direction).m_6630_(4));
            return result;
        }

        public Status status(ServerLevel level) {
            if (!level.m_46805_(this.core)) {
                return Status.UNLOADED;
            }
            for (BlockPos at : this.edges()) {
                if (level.m_46805_(at)) continue;
                return Status.UNLOADED;
            }
            if (!level.m_8055_(this.core).m_60713_((Block)EclipseContent.STATIONS.get((Object)EclipseState.Station.WAYGATE).get())) {
                return Status.BROKEN;
            }
            for (BlockPos at : this.edges()) {
                if (SoulGates.isFrame(level.m_8055_(at))) continue;
                return Status.BROKEN;
            }
            return Status.VALID;
        }
    }

    public static enum Status {
        VALID,
        BROKEN,
        UNLOADED;

    }

    public static final class PortalEntity
    extends BlockEntity {
        public UUID owner;
        public BlockPos core;
        public Direction direction = Direction.EAST;

        public PortalEntity(BlockPos at, BlockState state) {
            super((BlockEntityType)ENTITY.get(), at, state);
        }

        protected void m_183515_(CompoundTag n) {
            super.m_183515_(n);
            if (this.owner != null) {
                n.m_128362_("Owner", this.owner);
            }
            if (this.core != null) {
                n.m_128356_("Core", this.core.m_121878_());
            }
            n.m_128359_("Direction", this.direction.m_122433_());
        }

        public void m_142466_(CompoundTag n) {
            super.m_142466_(n);
            this.owner = n.m_128403_("Owner") ? n.m_128342_("Owner") : null;
            this.core = n.m_128441_("Core") ? BlockPos.m_122022_((long)n.m_128454_("Core")) : null;
            Direction parsed = Direction.m_122402_((String)n.m_128461_("Direction"));
            this.direction = parsed != null && parsed.m_122434_().m_122479_() ? parsed : Direction.EAST;
        }

        public CompoundTag m_5995_() {
            return this.m_187482_();
        }

        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.m_195640_((BlockEntity)this);
        }

        public void onLoad() {
            super.onLoad();
            if (this.f_58857_ != null && !this.f_58857_.f_46443_) {
                this.f_58857_.m_186460_(this.f_58858_, (Block)SURFACE.get(), 1);
            }
        }
    }

    public static final class Surface
    extends BaseEntityBlock {
        private Surface() {
            super(BlockBehaviour.Properties.m_284310_().m_60978_(-1.0f).m_60910_().m_60955_().m_60953_(s -> 11).m_222994_());
            this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_((Property)BlockStateProperties.f_61364_, (Comparable)Direction.Axis.X));
        }

        protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
            builder.m_61104_(new Property[]{BlockStateProperties.f_61364_});
        }

        public RenderShape m_7514_(BlockState state) {
            return RenderShape.MODEL;
        }

        public BlockEntity m_142194_(BlockPos at, BlockState state) {
            return new PortalEntity(at, state);
        }

        public VoxelShape m_5940_(BlockState state, BlockGetter level, BlockPos at, CollisionContext context) {
            return state.m_61143_((Property)BlockStateProperties.f_61364_) == Direction.Axis.X ? Block.m_49796_((double)0.0, (double)0.0, (double)7.0, (double)16.0, (double)16.0, (double)9.0) : Block.m_49796_((double)7.0, (double)0.0, (double)0.0, (double)9.0, (double)16.0, (double)16.0);
        }

        public void m_6807_(BlockState state, Level level, BlockPos at, BlockState old, boolean moving) {
            super.m_6807_(state, level, at, old, moving);
            if (!level.f_46443_) {
                level.m_186460_(at, (Block)this, 1);
            }
        }

        public void m_213897_(BlockState state, ServerLevel level, BlockPos at, RandomSource random) {
            PortalEntity portal;
            block6: {
                block5: {
                    BlockEntity blockEntity = level.m_7702_(at);
                    if (!(blockEntity instanceof PortalEntity)) break block5;
                    portal = (PortalEntity)blockEntity;
                    if (portal.core != null) break block6;
                }
                level.m_7471_(at, false);
                return;
            }
            Frame frame = new Frame(portal.core, portal.direction);
            if (frame.status(level) == Status.BROKEN) {
                SoulGates.deactivate(level, frame);
                return;
            }
            if (at.equals((Object)portal.core.m_7494_())) {
                level.m_186460_(at, (Block)this, 20);
            }
        }

        public void m_6861_(BlockState state, Level level, BlockPos at, Block from, BlockPos source, boolean moving) {
            if (level instanceof ServerLevel) {
                ServerLevel server = (ServerLevel)level;
                BlockEntity blockEntity = level.m_7702_(at);
                if (blockEntity instanceof PortalEntity) {
                    PortalEntity portal = (PortalEntity)blockEntity;
                    if (portal.core != null) {
                        Frame frame = new Frame(portal.core, portal.direction);
                        if (!activating.contains(SoulGates.key(level, portal.core)) && frame.status(server) == Status.BROKEN) {
                            SoulGates.deactivate(server, frame);
                        }
                    }
                }
            }
        }

        public void m_6810_(BlockState state, Level level, BlockPos at, BlockState replacement, boolean moving) {
            if (!state.m_60713_(replacement.m_60734_()) && level instanceof ServerLevel) {
                ServerLevel server = (ServerLevel)level;
                BlockEntity blockEntity = level.m_7702_(at);
                if (blockEntity instanceof PortalEntity) {
                    PortalEntity portal = (PortalEntity)blockEntity;
                    if (portal.core != null && !activating.contains(SoulGates.key(level, portal.core))) {
                        SoulGates.deactivate(server, new Frame(portal.core, portal.direction));
                    }
                }
            }
            super.m_6810_(state, level, at, replacement, moving);
        }

        public void m_7892_(BlockState state, Level level, BlockPos at, Entity entity) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)entity;
                BlockEntity blockEntity = level.m_7702_(at);
                if (blockEntity instanceof PortalEntity) {
                    PortalEntity portal = (PortalEntity)blockEntity;
                    SoulGates.enter(p, portal);
                }
            }
        }

        public void m_214162_(BlockState state, Level level, BlockPos at, RandomSource random) {
            for (int i = 0; i < 2; ++i) {
                level.m_7106_((ParticleOptions)ParticleTypes.f_123760_, (double)at.m_123341_() + random.m_188500_(), (double)at.m_123342_() + random.m_188500_(), (double)at.m_123343_() + random.m_188500_(), 0.0, 0.03, 0.0);
            }
        }
    }
}

