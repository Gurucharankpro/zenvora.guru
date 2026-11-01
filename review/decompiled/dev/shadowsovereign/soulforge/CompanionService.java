/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.EntityItemPickupEvent
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.network.NetworkHooks
 */
package dev.shadowsovereign.soulforge;

import dev.shadowsovereign.ShadowConfig;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseRoles;
import dev.shadowsovereign.expansion.X1Hooks;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.realm.WildBosses;
import dev.shadowsovereign.soulforge.CompanionInventory;
import dev.shadowsovereign.soulforge.CompanionMenu;
import dev.shadowsovereign.soulforge.SoulTier;
import dev.shadowsovereign.soulforge.SoulTrait;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.sovereign.SovereignService;
import dev.shadowsovereign.sovereign.SovereignState;
import dev.shadowsovereign.warfront.WarfrontBuilds;
import dev.shadowsovereign.warfront.WarfrontState;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.NetworkHooks;

public final class CompanionService {
    private static final UUID HEALTH = UUID.fromString("c3831030-61bd-4d4d-a108-000000000001");
    private static final UUID SPEED = UUID.fromString("c3831030-61bd-4d4d-a108-000000000002");
    private static final UUID ARMOR = UUID.fromString("c3831030-61bd-4d4d-a108-000000000003");
    private static final int[] BOND_THRESHOLDS = new int[]{0, 10, 30, 75, 150};
    private static final String[] BOND_NAMES = new String[]{"New Ally", "Trusted", "Loyal", "Soulbound", "Unbreakable"};

    private CompanionService() {
    }

    public static void awaken(ServerPlayer p, ShadowRecord r) {
        if (r.soulAwakened) {
            return;
        }
        ArmyData.Army a = ArmyData.get(p.f_8924_).army(p.m_20148_());
        r.tier = SoulTier.roll(p.m_217043_().m_188503_(10000), a.chosenTypes.contains(r.type));
        r.trait = SoulTrait.values()[1 + p.m_217043_().m_188503_(SoulTrait.values().length - 1)];
        r.soulAwakened = true;
    }

    public static void acquired(ServerPlayer p, ShadowRecord r) {
        if (r.soulAcquired) {
            return;
        }
        ArmyData.Army a = ArmyData.get(p.f_8924_).army(p.m_20148_());
        if (r.tier == SoulTier.CHOSEN && !a.chosenTypes.add(r.type)) {
            r.tier = SoulTier.CRAZY;
        }
        r.health *= (float)r.tier.power;
        r.maxHealth *= (float)r.tier.power;
        r.soulAcquired = true;
        X1Hooks.acquired(p);
        r.inventory.initialize(r.snapshot);
        ShadowService.tell(p, r.tier.label + " soul \u2022 " + r.name + " \u2022 " + r.trait.label + " \u2022 " + r.tier.power + "\u00d7 base power");
        ArmyData.get(p.f_8924_).m_77762_();
    }

    private static void modifier(Mob mob, Attribute attribute, UUID id, String name, double amount, AttributeModifier.Operation op) {
        AttributeInstance instance = mob.m_21051_(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier old = instance.m_22111_(id);
        if (old != null && old.m_22218_() == amount && old.m_22217_() == op) {
            return;
        }
        instance.m_22120_(id);
        if (amount != 0.0) {
            instance.m_22125_(new AttributeModifier(id, name, amount, op));
        }
    }

    public static void apply(Mob mob, ShadowRecord r) {
        CompanionService.modifier(mob, Attributes.f_22276_, HEALTH, "Soul tier vitality", r.tier.power - 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        CompanionService.modifier(mob, Attributes.f_22279_, SPEED, "Swift soul", r.trait == SoulTrait.SWIFT ? 0.15 : 0.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
        CompanionService.modifier(mob, Attributes.f_22284_, ARMOR, "Stalwart soul", r.trait == SoulTrait.STALWART ? 4.0 : 0.0, AttributeModifier.Operation.ADDITION);
        r.inventory.initialize(r.snapshot);
    }

    public static void equip(Mob mob, ShadowRecord r) {
        r.inventory.initialize(r.snapshot);
        for (int i = 0; i < 6; ++i) {
            mob.m_8061_(CompanionInventory.SLOTS[i], ((ItemStack)r.inventory.items.get(i)).m_41777_());
            mob.m_21409_(CompanionInventory.SLOTS[i], 0.0f);
        }
    }

    public static void captureEquipment(Mob mob, ShadowRecord r) {
        r.inventory.initialize(r.snapshot);
        for (int i = 0; i < 6; ++i) {
            r.inventory.items.set(i, (Object)mob.m_6844_(CompanionInventory.SLOTS[i]).m_41777_());
        }
    }

    public static float fallbackDamage(Mob mob, LivingEntity target) {
        double base = WildBosses.fallback(mob, (Double)ShadowConfig.FALLBACK_DAMAGE.get());
        double addition = 0.0;
        double total = 1.0;
        Collection modifiers = mob.m_21205_().m_41638_(EquipmentSlot.MAINHAND).get((Object)Attributes.f_22281_);
        for (AttributeModifier m : modifiers) {
            if (m.m_22217_() != AttributeModifier.Operation.ADDITION) continue;
            base += m.m_22218_();
        }
        for (AttributeModifier m : modifiers) {
            if (m.m_22217_() == AttributeModifier.Operation.MULTIPLY_BASE) {
                addition += base * m.m_22218_();
            }
            if (m.m_22217_() != AttributeModifier.Operation.MULTIPLY_TOTAL) continue;
            total *= 1.0 + m.m_22218_();
        }
        return (float)Math.max(0.0, (base + addition) * total + (double)EnchantmentHelper.m_44833_((ItemStack)mob.m_21205_(), (MobType)target.m_6336_()));
    }

    public static int bondRank(int points) {
        for (int i = BOND_THRESHOLDS.length - 1; i >= 0; --i) {
            if (points < BOND_THRESHOLDS[i]) continue;
            return i;
        }
        return 0;
    }

    public static String bondName(int points) {
        return BOND_NAMES[CompanionService.bondRank(points)];
    }

    public static int nextBond(int points) {
        return BOND_THRESHOLDS[Math.min(4, CompanionService.bondRank(points) + 1)];
    }

    public static float incoming(ShadowRecord r, float damage) {
        return damage * (1.0f - (float)CompanionService.bondRank(r.bond) * 0.05f);
    }

    public static void bond(ServerPlayer p, ShadowRecord r, int points) {
        int rank = CompanionService.bondRank(r.bond);
        r.bond = Math.min(150, r.bond + Math.max(0, points));
        if (CompanionService.bondRank(r.bond) > rank) {
            ShadowService.tell(p, r.name + " now trusts you: " + CompanionService.bondName(r.bond) + "! Damage taken \u2212" + CompanionService.bondRank(r.bond) * 5 + "%.");
        }
        ArmyData.get(p.f_8924_).m_77762_();
        X1Hooks.bond(p, r, rank);
    }

    public static void killed(Entity source, ShadowRecord r) {
        int points;
        MinecraftServer server = source.m_20194_();
        if (server == null) {
            return;
        }
        ServerPlayer p = ShadowService.owner(source);
        int n = points = r.trait == SoulTrait.DEVOTED ? 2 : 1;
        if (p != null) {
            CompanionService.bond(p, r, points);
        } else {
            r.bond = Math.min(150, r.bond + points);
            ArmyData.get(server).m_77762_();
        }
        Mob mob = ShadowService.active(server, r);
        if (r.trait == SoulTrait.VAMPIRIC && mob != null && mob.m_6084_()) {
            mob.m_5634_(2.0f);
            r.health = mob.m_21223_();
        }
    }

    public static boolean care(ServerPlayer p, ShadowRecord r) {
        long now = p.f_8924_.m_129783_().m_46467_();
        if (r.bond >= 150 && !SovereignService.needsCare(p, r.id) || now < r.nextCareAt || r.pendingRespawn()) {
            return false;
        }
        Mob mob = ShadowService.active(p.f_8924_, r);
        if (mob == null || !mob.m_6084_() || mob.m_9236_() != p.m_9236_() || mob.m_20280_((Entity)p) > 64.0) {
            ShadowService.tell(p, "Summon your companion and stand within eight blocks to feed it.");
            return false;
        }
        for (int i = 0; i < p.m_150109_().m_6643_(); ++i) {
            ItemStack food = p.m_150109_().m_8020_(i);
            FoodProperties properties = food.getFoodProperties((LivingEntity)p);
            if (properties == null || properties.m_38744_() <= 0 || !properties.m_38749_().isEmpty()) continue;
            food.m_41774_(1);
            p.m_150109_().m_6596_();
            r.nextCareAt = now + 1200L;
            int points = WarfrontBuilds.rune(mob, r) == WarfrontState.Rune.HEARTH ? 4 : 3;
            SovereignService.progress(p, r.id, SovereignState.Goal.FEED);
            CompanionService.bond(p, r, points += EclipseRoles.feeding(p, r));
            mob.m_5634_(4.0f);
            r.health = mob.m_21223_();
            p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123750_, mob.m_20185_(), mob.m_20186_() + (double)mob.m_20206_(), mob.m_20189_(), 5, 0.3, 0.2, 0.3, 0.01);
            ShadowService.tell(p, "Fed " + r.name + ": +" + points + " bond, +4 HP. Next feeding in 60 seconds.");
            return true;
        }
        ShadowService.tell(p, "Carry food without status effects to feed your companion.");
        return false;
    }

    public static boolean accessible(ServerPlayer p, ShadowRecord r) {
        return p.m_6084_() && !p.m_5833_() && r != null && ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(r.id) == r && !AdventureService.busy(p.f_8924_, p.m_20148_(), r.id);
    }

    public static boolean open(ServerPlayer p, UUID id) {
        final ShadowRecord r = ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).shadows.get(id);
        if (!CompanionService.accessible(p, r)) {
            ShadowService.tell(p, "This companion is unavailable or away on an expedition.");
            return false;
        }
        r.inventory.initialize(r.snapshot);
        Mob mob = ShadowService.active(p.f_8924_, r);
        if (mob != null) {
            CompanionService.captureEquipment(mob, r);
        }
        NetworkHooks.openScreen((ServerPlayer)p, (MenuProvider)new MenuProvider(){

            public Component m_5446_() {
                return Component.m_237113_((String)"Soul Forge");
            }

            public AbstractContainerMenu m_7208_(int window, Inventory inv, Player player) {
                return new CompanionMenu(window, inv, r);
            }
        }, b -> {
            b.m_130077_(r.id);
            b.m_130072_(r.name, 128);
            b.m_130072_(r.type, 256);
            b.m_130068_((Enum)r.tier);
            b.m_130068_((Enum)r.trait);
            b.writeBoolean(r.activeEntity != null);
            if (r.activeEntity != null) {
                b.m_130077_(r.activeEntity);
            }
        });
        return true;
    }

    public static ItemStack store(ShadowRecord r, ItemStack input) {
        ItemStack remaining = input.m_41777_();
        r.inventory.initialize(r.snapshot);
        for (int pass = 0; pass < 2 && !remaining.m_41619_(); ++pass) {
            for (int i = 6; i < 11; ++i) {
                ItemStack slot = (ItemStack)r.inventory.items.get(i);
                if (pass == 0 && !slot.m_41619_() && ItemStack.m_150942_((ItemStack)slot, (ItemStack)remaining)) {
                    int amount = Math.min(remaining.m_41613_(), Math.max(0, Math.min(64, slot.m_41741_()) - slot.m_41613_()));
                    slot.m_41769_(amount);
                    remaining.m_41774_(amount);
                    continue;
                }
                if (pass != 1 || !slot.m_41619_()) continue;
                r.inventory.items.set(i, (Object)remaining.m_41620_(Math.min(64, remaining.m_41741_())));
            }
        }
        return remaining;
    }

    public static void collect(Mob mob, ShadowRecord r) {
        ServerPlayer p = ShadowService.owner((Entity)mob);
        if (!r.collectLoot || p == null || !p.m_6084_() || p.m_9236_() != mob.m_9236_() || p.m_20280_((Entity)mob) > 256.0 || !mob.m_6084_()) {
            return;
        }
        for (ItemEntity item : mob.m_9236_().m_6443_(ItemEntity.class, mob.m_20191_().m_82400_(3.0), e -> e.m_6084_() && !e.m_32063_() && !SoulPlay.reservedToy(mob.m_20194_(), e) && !OdysseyRuns.reserved(e))) {
            ItemStack remaining;
            CompoundTag tag = new CompoundTag();
            item.m_7380_(tag);
            if (tag.m_128403_("Owner") && !tag.m_128342_("Owner").equals(p.m_20148_()) || tag.m_128403_("Thrower") && !tag.m_128342_("Thrower").equals(p.m_20148_())) continue;
            Vec3 from = mob.m_146892_();
            if (mob.m_9236_().m_45547_(new ClipContext(from, item.m_20182_().m_82520_(0.0, 0.1, 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)mob)).m_6662_() != HitResult.Type.MISS || MinecraftForge.EVENT_BUS.post((Event)new EntityItemPickupEvent((Player)p, item)) || (remaining = CompanionService.store(r, item.m_32055_())).m_41613_() == item.m_32055_().m_41613_()) continue;
            item.m_32045_(remaining);
            if (remaining.m_41619_()) {
                item.m_146870_();
            }
            ArmyData.get(p.f_8924_).m_77762_();
        }
    }

    public static void deposit(ServerPlayer p, ShadowRecord r) {
        for (int i = 6; i < 11; ++i) {
            ItemStack stack = (ItemStack)r.inventory.items.get(i);
            if (!stack.m_41619_()) {
                p.m_150109_().m_36054_(stack);
            }
            if (!stack.m_41619_()) continue;
            r.inventory.items.set(i, (Object)ItemStack.f_41583_);
        }
        p.m_150109_().m_6596_();
        ArmyData.get(p.f_8924_).m_77762_();
    }

    public static void returnItems(ServerPlayer p, ShadowRecord r) {
        r.inventory.initialize(r.snapshot);
        for (int i = 0; i < 11; ++i) {
            ItemStack stack = (ItemStack)r.inventory.items.get(i);
            r.inventory.items.set(i, (Object)ItemStack.f_41583_);
            if (stack.m_41619_()) continue;
            p.m_150109_().m_36054_(stack);
            if (stack.m_41619_()) continue;
            p.m_36176_(stack, false);
        }
        p.m_150109_().m_6596_();
    }
}

