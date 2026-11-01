/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.ToolActions
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.level.BlockEvent$BlockToolModificationEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.covenant;

import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.covenant.CovenantContent;
import dev.shadowsovereign.expanse.ExpanseWorld;
import dev.shadowsovereign.gloom.GloomContent;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class CovenantWorld {
    public static ResourceLocation id(String s) {
        return new ResourceLocation("shadowsovereign", s);
    }

    public static ResourceKey<Structure> site(int kind) {
        return ResourceKey.m_135785_((ResourceKey)Registries.f_256944_, (ResourceLocation)CovenantWorld.id("covenant_" + MythicExpedition.IDS[kind]));
    }

    public static BlockPos altar(StructureStart start) {
        Object e;
        if (!start.m_73602_().isEmpty() && (e = start.m_73602_().get(0)) instanceof PoolElementStructurePiece) {
            PoolElementStructurePiece piece = (PoolElementStructurePiece)e;
            return StructureTemplate.m_74593_((BlockPos)new BlockPos(10, 1, 10), (Mirror)Mirror.NONE, (Rotation)piece.m_6830_(), (BlockPos)BlockPos.f_121853_).m_121955_((Vec3i)piece.m_72646_());
        }
        BoundingBox b = start.m_73601_();
        return new BlockPos((b.m_162395_() + b.m_162399_()) / 2, b.m_162396_() + 1, (b.m_162398_() + b.m_162401_()) / 2);
    }

    public static boolean protectedSite(ServerLevel w, BlockPos p) {
        for (int kind = 4; kind < MythicExpedition.IDS.length; ++kind) {
            Structure s = (Structure)w.m_9598_().m_175515_(Registries.f_256944_).m_6246_(CovenantWorld.site(kind));
            if (s == null || !w.m_215010_().m_220494_(p, s).m_73603_()) continue;
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void tool(BlockEvent.BlockToolModificationEvent e) {
        ServerPlayer owner;
        if (e.isCanceled()) {
            return;
        }
        Player p = e.getPlayer();
        if (p instanceof ServerPlayer && OdysseyRealm.inside(owner = (ServerPlayer)p) && !ExpanseWorld.editable(owner, e.getPos())) {
            return;
        }
        BlockState s = e.getState();
        if (e.getToolAction() == ToolActions.AXE_STRIP && s.m_60713_(CovenantContent.b("soul_wood_log"))) {
            e.setFinalState((BlockState)CovenantContent.b("stripped_soul_wood_log").m_49966_().m_61124_((Property)RotatedPillarBlock.f_55923_, (Comparable)((Direction.Axis)s.m_61143_((Property)RotatedPillarBlock.f_55923_))));
        }
        if (e.getToolAction() == ToolActions.HOE_TILL && (s.m_60713_(CovenantContent.b("soul_soil")) || s.m_60713_(CovenantContent.b("dark_soul_grass_block"))) && e.getLevel().m_8055_(e.getPos().m_7494_()).m_60795_()) {
            e.setFinalState(((Block)GloomContent.FARMLAND.get()).m_49966_());
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        ServerLevel w = e.getServer().m_129880_(OdysseyRealm.KEY);
        if (w == null) {
            return;
        }
        for (ServerPlayer p : w.m_6907_()) {
            if (p.f_19797_ % 40 != 0 || p.m_5833_() || OdysseyRealm.foreignPlot(p, p.m_20183_())) continue;
            for (int kind = 4; kind < MythicExpedition.IDS.length; ++kind) {
                StructureStart start;
                Structure s;
                CompoundTag n = MythicExpedition.state(p, kind);
                if (n.m_128451_("Status") == 2 || (s = (Structure)w.m_9598_().m_175515_(Registries.f_256944_).m_6246_(CovenantWorld.site(kind))) == null || !(start = w.m_215010_().m_220494_(p.m_20183_(), s)).m_73603_()) continue;
                BlockPos at = CovenantWorld.altar(start);
                if (AdventureService.journal((ServerPlayer)p).session != null || !(p.m_20238_(Vec3.m_82512_((Vec3i)at)) < 484.0)) continue;
                n.m_128356_("Lair", at.m_121878_());
                MythicExpedition.start(p, kind);
            }
        }
    }
}

