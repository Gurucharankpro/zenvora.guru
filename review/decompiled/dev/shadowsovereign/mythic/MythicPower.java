/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.EvokerFangs
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.SoulDevourer;
import dev.shadowsovereign.odyssey.GuideService;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid="shadowsovereign")
public final class MythicPower {
    private static final UUID VITALITY = UUID.fromString("fe2a0120-0000-4000-8000-000000000001");

    public static boolean mythical(Entity e) {
        return e instanceof MythicBeast || e instanceof SoulDevourer;
    }

    public static void apply(Mob m, ShadowRecord r) {
        if (!MythicPower.mythical((Entity)m)) {
            return;
        }
        m.getPersistentData().m_128379_("MonolithBalance120", true);
        AttributeInstance a = m.m_21051_(Attributes.f_22276_);
        if (a == null || a.m_22111_(VITALITY) != null) {
            return;
        }
        float old = m.m_21233_();
        float hp = m.m_21223_();
        a.m_22125_(new AttributeModifier(VITALITY, "Monolith mythical vitality", 0.25, AttributeModifier.Operation.MULTIPLY_TOTAL));
        float ratio = m.m_21233_() / old;
        m.m_21153_(Math.min(m.m_21233_(), hp * ratio));
        if (r != null) {
            r.health = Math.min(m.m_21233_(), r.health * ratio);
            r.maxHealth = m.m_21233_();
            m.m_21153_(r.health);
            r.snapshot = ShadowService.snapshot(m, false);
            if (m.m_20194_() != null) {
                ArmyData.get(m.m_20194_()).m_77762_();
            }
        }
    }

    public static void upgrade(ServerPlayer p, ShadowRecord r) {
        Entity entity;
        EntityType type;
        ResourceLocation key;
        block7: {
            block6: {
                if (r == null || r.activeEntity != null || r.snapshot.m_128469_("ForgeData").m_128471_("MonolithBalance120")) {
                    return;
                }
                key = ResourceLocation.m_135820_((String)r.type);
                if (key == null || !key.m_135827_().equals("shadowsovereign")) break block6;
                if (key.m_135815_().equals("soul_devourer")) break block7;
                if (!Arrays.stream(MythicExpedition.IDS).noneMatch(key.m_135815_()::equals)) break block7;
            }
            return;
        }
        if ((type = (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(key)) != null && (entity = type.m_20615_((Level)p.m_284548_())) instanceof Mob) {
            Mob m = (Mob)entity;
            m.m_20258_(r.snapshot.m_6426_());
            MythicPower.apply(m, r);
            if (!r.snapshot.m_128425_("ForgeData", 10)) {
                r.snapshot.m_128365_("ForgeData", (Tag)new CompoundTag());
            }
            r.snapshot.m_128469_("ForgeData").m_128379_("MonolithBalance120", true);
            ArmyData.get(p.f_8924_).m_77762_();
        }
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            for (ShadowRecord r : GuideService.army((ServerPlayer)p).shadows.values()) {
                MythicPower.upgrade(p, r);
            }
        }
    }

    @SubscribeEvent
    public static void join(EntityJoinLevelEvent e) {
        Entity entity;
        if (!e.getLevel().f_46443_ && (entity = e.getEntity()) instanceof Mob) {
            Mob m = (Mob)entity;
            MythicPower.apply(m, ShadowService.record((Entity)m));
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public static void power(LivingHurtEvent e) {
        if (!e.getEntity().m_9236_().f_46443_ && MythicPower.mythical(e.getSource().m_7639_())) {
            Entity entity;
            float scale = 1.25f;
            if (e.getSource().m_7640_() instanceof EvokerFangs && (entity = e.getSource().m_7639_()) instanceof SoulDevourer) {
                SoulDevourer m = (SoulDevourer)entity;
                scale = (float)((double)scale * (m.m_21133_(Attributes.f_22281_) / 12.0));
            }
            e.setAmount(e.getAmount() * scale);
        }
    }
}

