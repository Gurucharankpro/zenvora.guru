/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.HoeItem
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.common.util.BlockSnapshot
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.level.ChunkDataEvent$Save
 *  net.minecraftforge.event.server.ServerStoppedEvent
 *  net.minecraftforge.event.server.ServerStoppingEvent
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.gloom;

import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.expanse.ExpanseContent;
import dev.shadowsovereign.expanse.ExpanseWorld;
import dev.shadowsovereign.expansion.OpenMythics;
import dev.shadowsovereign.gloom.GloomContent;
import dev.shadowsovereign.gloom.GloomRealm;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.RealmEvents;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class GloomEvents {
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void saveChunk(ChunkDataEvent.Save e) {
        ServerLevel w;
        LevelAccessor levelAccessor = e.getLevel();
        if (levelAccessor instanceof ServerLevel && (w = (ServerLevel)levelAccessor).m_46472_() == OdysseyRealm.KEY) {
            CompoundTag data = e.getData();
            for (String key : List.copyOf(data.m_128431_())) {
                Tag tag = data.m_128423_(key);
                if (tag == null) continue;
                data.m_128365_(key, tag.m_6426_());
            }
        }
    }

    @SubscribeEvent
    public static void stopping(ServerStoppingEvent e) {
        GloomRealm.releaseAll(e.getServer());
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            GloomRealm.release(p.f_8924_, p.m_20148_());
        }
    }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            GloomRealm.release(p.f_8924_, p.m_20148_());
        }
    }

    @SubscribeEvent
    public static void stop(ServerStoppedEvent e) {
        GloomRealm.clear();
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void hoe(PlayerInteractEvent.RightClickBlock e) {
        ServerPlayer p;
        Player player;
        if (e.getUseBlock() == Event.Result.DENY || e.getUseItem() == Event.Result.DENY || !((player = e.getEntity()) instanceof ServerPlayer) || !((p = (ServerPlayer)player).m_21120_(e.getHand()).m_41720_() instanceof HoeItem) || e.getFace() == Direction.DOWN) {
            return;
        }
        ServerLevel w = p.m_284548_();
        BlockState s = w.m_8055_(e.getPos());
        if (!(s.m_60713_((Block)GloomContent.DIRT.get()) || s.m_60713_((Block)GloomContent.GRASS.get()) || s.m_60713_((Block)ExpanseContent.SOIL.get()))) {
            return;
        }
        if (!w.m_8055_(e.getPos().m_7494_()).m_60795_() || OdysseyRealm.inside(p) && !ExpanseWorld.editable(p, e.getPos())) {
            return;
        }
        BlockState target = ((Block)GloomContent.FARMLAND.get()).m_49966_();
        BlockSnapshot snapshot = BlockSnapshot.create((ResourceKey)w.m_46472_(), (LevelAccessor)w, (BlockPos)e.getPos());
        if (RealmEvents.placeVeto(p, snapshot)) {
            return;
        }
        w.m_46597_(e.getPos(), target);
        w.m_5594_(null, e.getPos(), SoundEvents.f_11955_, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (!p.m_7500_()) {
            p.m_21120_(e.getHand()).m_41622_(1, (LivingEntity)p, u -> u.m_21190_(e.getHand()));
        }
        e.setCanceled(true);
        e.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void join(EntityJoinLevelEvent entityJoinLevelEvent) {
        OpenMythics.join(entityJoinLevelEvent);
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void attack(LivingAttackEvent e) {
        MythicBeast b;
        Entity entity;
        if (!e.getEntity().m_9236_().f_46443_ && (entity = e.getSource().m_7639_()) instanceof MythicBeast && !(b = (MythicBeast)entity).allowed(e.getEntity())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void death(LivingDeathEvent e) {
        UUID id;
        MythicBeast m;
        LivingEntity livingEntity;
        if (e.getEntity().m_9236_().f_46443_ || !((livingEntity = e.getEntity()) instanceof MythicBeast) || ShadowService.isShadow((Entity)(m = (MythicBeast)livingEntity)) || !m.getPersistentData().m_128403_("SoulboundMythicHost")) {
            return;
        }
        Entity killer = e.getSource().m_7639_();
        if (killer instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)killer;
            v0 = p.m_20148_();
        } else {
            v0 = id = killer == null ? null : ShadowService.ownerId(killer);
        }
        if (id == null || !id.equals(m.getPersistentData().m_128342_("SoulboundMythicHost"))) {
            e.setCanceled(true);
            m.m_21153_(Math.max(1.0f, m.m_21233_() * 0.05f));
        }
    }
}

