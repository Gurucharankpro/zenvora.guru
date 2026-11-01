/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package dev.shadowsovereign.royal;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.Order;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.eclipse.EclipseCamp;
import dev.shadowsovereign.eclipse.EclipseService;
import dev.shadowsovereign.eclipse.EclipseState;
import dev.shadowsovereign.legacy.LegacyState;
import dev.shadowsovereign.network.RoyalNetwork;
import dev.shadowsovereign.royal.RoyalContent;
import dev.shadowsovereign.royal.RoyalState;
import dev.shadowsovereign.royal.RoyalText;
import dev.shadowsovereign.soulforge.CompanionService;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class RoyalService {
    public static EclipseState.Power casting;
    public static UUID acceptedTarget;

    private RoyalService() {
    }

    public static RoyalState state(ServerPlayer p) {
        return ArmyData.get((MinecraftServer)p.f_8924_).army((UUID)p.m_20148_()).royal;
    }

    public static long now(ServerPlayer p) {
        return p.f_8924_.m_129783_().m_46467_();
    }

    public static void dirty(ServerPlayer p) {
        ArmyData.get(p.f_8924_).m_77762_();
    }

    public static int rank(ServerPlayer p, EclipseState.Power power) {
        return RoyalService.state(p).level(RoyalState.Skill.valueOf(power.name()));
    }

    public static int chance(ServerPlayer p) {
        return RoyalService.state(p).level(RoyalState.Skill.EXTRACT);
    }

    public static boolean succeeds(int roll, int chance) {
        return roll >= 0 && roll < 100 && roll < Math.max(1, Math.min(10, chance));
    }

    public static boolean automaticRoll(ServerPlayer p, String type) {
        boolean result = RoyalService.succeeds(p.m_217043_().m_188503_(100), RoyalService.chance(p));
        RoyalService.state(p).attempted(type, result);
        RoyalService.dirty(p);
        return result;
    }

    public static void remember(ServerPlayer p, ShadowRecord r, int chance, BlockPos at) {
        CompoundTag n = new CompoundTag();
        n.m_128362_("Id", r.id);
        n.m_128359_("Type", r.type);
        n.m_128359_("Name", r.name);
        n.m_128359_("Tier", r.tier.name());
        n.m_128359_("Trait", r.trait.name());
        n.m_128405_("Chance", chance);
        n.m_128356_("Day", Math.floorDiv(p.m_9236_().m_46468_(), 24000) + 1L);
        n.m_128359_("Dimension", p.m_9236_().m_46472_().m_135782_().toString());
        n.m_128359_("Biome", p.m_9236_().m_204166_(at).m_203543_().map(k -> k.m_135782_().toString()).orElse(""));
        List<CompoundTag> list = RoyalService.state((ServerPlayer)p).memories;
        list.add(0, n);
        while (list.size() > 20) {
            list.remove(20);
        }
        RoyalService.dirty(p);
        RoyalNetwork.send(p, false, 0);
    }

    public static RoyalState.Team team(ServerPlayer p, int index) {
        return index >= 0 && index < 5 ? RoyalService.state((ServerPlayer)p).teams[index] : null;
    }

    public static boolean teamEdit(ServerPlayer p, int index, List<UUID> ids, String name, int emblem) {
        RoyalState.Team t = RoyalService.team(p, index);
        if (t == null || ids.size() > 24 || new HashSet<UUID>(ids).size() != ids.size() || ids.stream().anyMatch(id -> EclipseService.record(p, id) == null) || name.codePointCount(0, name.length()) > 24 || emblem < 0 || emblem >= 12 || (RoyalService.state((ServerPlayer)p).emblems & 1 << emblem) == 0) {
            return false;
        }
        t.members.clear();
        t.members.addAll(ids);
        t.name = LegacyState.plain(name, 24);
        t.emblem = emblem;
        RoyalService.state((ServerPlayer)p).selectedTeam = index;
        RoyalService.dirty(p);
        return true;
    }

    public static int deploy(ServerPlayer p, int index) {
        RoyalState.Team t = RoyalService.team(p, index);
        if (t == null || !EclipseService.usable(p)) {
            return 0;
        }
        AdventureData.Session s = AdventureService.journal((ServerPlayer)p).session;
        if (s != null && s.kind != AdventureData.Kind.BOARD && s.kind != AdventureData.Kind.VAULT && s.kind != AdventureData.Kind.SEAL_PUZZLE) {
            RoyalText.tell(p, "finish_activity", new Object[0]);
            return 0;
        }
        int summoned = 0;
        int skipped = 0;
        for (UUID id : t.members) {
            ShadowRecord r = EclipseService.record(p, id);
            if (r == null) {
                ++skipped;
                continue;
            }
            if (r.activeEntity != null) continue;
            if (r.pendingRespawn() || AdventureService.busy(p.f_8924_, p.m_20148_(), id) || !ShadowService.summon(p, r, false)) {
                ++skipped;
                continue;
            }
            ShadowService.setOrder(p, r, Order.FOLLOW);
            ++summoned;
        }
        RoyalService.state((ServerPlayer)p).selectedTeam = index;
        RoyalService.dirty(p);
        RoyalText.tell(p, "deployed", summoned, skipped);
        return summoned;
    }

    public static int recall(ServerPlayer p, int index) {
        RoyalState.Team t = RoyalService.team(p, index);
        if (t == null) {
            return 0;
        }
        int count = 0;
        for (UUID id : t.members) {
            ShadowRecord r = EclipseService.record(p, id);
            if (r == null) continue;
            EclipseService.cancelRelay(p, id);
            if (!ShadowService.recall(p, r)) continue;
            ++count;
        }
        RoyalText.tell(p, "recalled", count);
        return count;
    }

    public static void released(ServerPlayer p, UUID id) {
        RoyalState e = RoyalService.state(p);
        for (RoyalState.Team t : e.teams) {
            t.members.remove(id);
        }
        e.charmAt.remove(id);
        e.breach.remove(id);
        RoyalService.dirty(p);
    }

    public static boolean policy(ServerPlayer p, int index, int food, int arrows) {
        RoyalState.Team t = RoyalService.team(p, index);
        if (t == null || food < 0 || food > 16 || arrows < 0 || arrows > 64) {
            return false;
        }
        t.food = food;
        t.arrows = arrows;
        RoyalService.dirty(p);
        return true;
    }

    public static boolean food(ItemStack item, ServerPlayer p) {
        return item.m_41614_() && item.getFoodProperties((LivingEntity)p) != null && item.getFoodProperties((LivingEntity)p).m_38749_().isEmpty();
    }

    public static boolean supply(ServerPlayer p, int index) {
        RoyalState.Team t = RoyalService.team(p, index);
        if (t == null || !EclipseCamp.near(p, EclipseState.Station.QUARTERMASTER)) {
            return false;
        }
        boolean changed = false;
        for (UUID id : t.members) {
            ShadowRecord r = EclipseService.record(p, id);
            if (r == null || AdventureService.busy(p.f_8924_, p.m_20148_(), id)) continue;
            r.inventory.initialize(r.snapshot);
            for (ItemStack stack : EclipseService.state((ServerPlayer)p).bank) {
                boolean arrows = stack.m_204117_(ItemTags.f_13161_);
                if (stack.m_41619_() || !arrows && !RoyalService.food(stack, p)) continue;
                int have = r.inventory.items.subList(6, 11).stream().filter(s -> arrows ? s.m_204117_(ItemTags.f_13161_) : RoyalService.food(s, p)).mapToInt(ItemStack::m_41613_).sum();
                int amount = Math.min(stack.m_41613_(), Math.max(0, (arrows ? t.arrows : t.food) - have));
                if (amount == 0) continue;
                ItemStack offer = stack.m_41777_();
                offer.m_41764_(amount);
                int moved = amount - CompanionService.store(r, offer).m_41613_();
                if (moved <= 0) continue;
                stack.m_41774_(moved);
                changed = true;
            }
        }
        if (changed) {
            RoyalService.dirty(p);
        } else {
            RoyalText.tell(p, "supply_empty", new Object[0]);
        }
        return changed;
    }

    public static boolean wanted(ServerPlayer p, String type) {
        RoyalState e = RoyalService.state(p);
        if (e.wanted.remove(type)) {
            RoyalService.dirty(p);
            return true;
        }
        ResourceLocation key = ResourceLocation.m_135820_((String)type);
        if (e.wanted.size() >= 5 || key == null || !ForgeRegistries.ENTITY_TYPES.containsKey(key)) {
            return false;
        }
        e.wanted.add(type);
        RoyalService.dirty(p);
        return true;
    }

    public static Price price(int target) {
        return switch (target) {
            case 2 -> new Price(2, 8, Items.f_42534_, 8);
            case 3 -> new Price(4, 12, Items.f_42534_, 16);
            case 4 -> new Price(6, 16, Items.f_151049_, 8);
            case 5 -> new Price(8, 24, Items.f_42417_, 8);
            case 6 -> new Price(12, 32, Items.f_151049_, 16);
            case 7 -> new Price(16, 40, Items.f_220224_, 2);
            case 8 -> new Price(20, 48, Items.f_42418_, 1);
            case 9 -> new Price(24, 56, Items.f_42418_, 2);
            case 10 -> new Price(32, 64, Items.f_42686_, 1);
            default -> null;
        };
    }

    public static int count(ServerPlayer p, Item item) {
        int count = 0;
        for (int i = 0; i < 36; ++i) {
            if (!p.m_150109_().m_8020_(i).m_150930_(item)) continue;
            count += p.m_150109_().m_8020_(i).m_41613_();
        }
        return count;
    }

    private static void consume(ServerPlayer p, Item item, int amount) {
        for (int i = 0; i < 36 && amount > 0; ++i) {
            ItemStack s = p.m_150109_().m_8020_(i);
            if (!s.m_150930_(item)) continue;
            int n = Math.min(amount, s.m_41613_());
            s.m_41774_(n);
            amount -= n;
        }
    }

    public static boolean upgrade(ServerPlayer p, int skill, int expected) {
        if (skill < 0 || skill >= RoyalState.Skill.values().length || !EclipseService.usable(p) || !RoyalContent.nearDesk(p)) {
            return false;
        }
        RoyalState.Skill s = RoyalState.Skill.values()[skill];
        RoyalState e = RoyalService.state(p);
        int level = e.level(s);
        if (level != expected || level >= 10) {
            return false;
        }
        AdventureData.Session run = AdventureService.journal((ServerPlayer)p).session;
        if (run != null && run.phase == AdventureData.Phase.ACTIVE) {
            RoyalText.tell(p, "finish_activity", new Object[0]);
            return false;
        }
        if (s != RoyalState.Skill.EXTRACT && EclipseService.state((ServerPlayer)p).wins < EclipseState.Power.valueOf((String)s.name()).wins) {
            return false;
        }
        Price cost = RoyalService.price(level + 1);
        for (AbstractMap.SimpleEntry<Item, Integer> entry : List.of(new AbstractMap.SimpleEntry<Item, Integer>(Items.f_42415_, cost.diamonds), new AbstractMap.SimpleEntry<Item, Integer>(Items.f_42616_, cost.emeralds), new AbstractMap.SimpleEntry<Item, Integer>(cost.extra, cost.count))) {
            int missing = entry.getValue() - RoyalService.count(p, entry.getKey());
            if (missing <= 0) continue;
            RoyalText.tell(p, "missing", missing, entry.getKey().m_41466_());
            return false;
        }
        RoyalService.consume(p, Items.f_42415_, cost.diamonds);
        RoyalService.consume(p, Items.f_42616_, cost.emeralds);
        RoyalService.consume(p, cost.extra, cost.count);
        e.levels.put(s, level + 1);
        p.m_150109_().m_6596_();
        p.f_36095_.m_38946_();
        RoyalService.dirty(p);
        RoyalText.tell(p, "upgraded", RoyalText.c("skill." + s.name().toLowerCase(Locale.ROOT), new Object[0]), level + 1);
        return true;
    }

    public static boolean bindBanner(ServerPlayer p, BlockPos at, int index) {
        RoyalContent.RoyalEntity be;
        block3: {
            block2: {
                BlockEntity blockEntity;
                if (RoyalService.team(p, index) == null || p.m_20238_(Vec3.m_82512_((Vec3i)at)) > 64.0 || !((blockEntity = p.m_9236_().m_7702_(at)) instanceof RoyalContent.RoyalEntity)) break block2;
                be = (RoyalContent.RoyalEntity)blockEntity;
                if (p.m_20148_().equals(be.owner) && p.m_9236_().m_8055_(at).m_60713_((Block)RoyalContent.BANNER.get())) break block3;
            }
            return false;
        }
        be.team = index;
        be.m_6596_();
        p.m_9236_().m_7260_(at, be.m_58900_(), be.m_58900_(), 3);
        RoyalService.state((ServerPlayer)p).banners.put(index, new RoyalState.Banner(at.m_7949_(), p.m_9236_().m_46472_().m_135782_().toString()));
        RoyalService.dirty(p);
        return true;
    }

    public static boolean rally(ServerPlayer p, int index) {
        RoyalState.Banner b;
        RoyalState.Team t;
        block6: {
            block5: {
                BlockEntity blockEntity;
                t = RoyalService.team(p, index);
                b = RoyalService.state((ServerPlayer)p).banners.get(index);
                if (t == null || b == null || !b.dimension().equals(p.m_9236_().m_46472_().m_135782_().toString()) || p.m_20238_(Vec3.m_82512_((Vec3i)b.pos())) > 4096.0 || !p.m_284548_().m_46805_(b.pos()) || !p.m_9236_().m_8055_(b.pos()).m_60713_((Block)RoyalContent.BANNER.get()) || !((blockEntity = p.m_9236_().m_7702_(b.pos())) instanceof RoyalContent.RoyalEntity)) break block5;
                RoyalContent.RoyalEntity be = (RoyalContent.RoyalEntity)blockEntity;
                if (p.m_20148_().equals(be.owner) && be.team == index) break block6;
            }
            return false;
        }
        boolean changed = false;
        for (UUID id : t.members) {
            ShadowRecord r = EclipseService.record(p, id);
            Mob m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m == null || m.m_9236_() != p.m_9236_() || !(m.m_20280_((Entity)p) <= 4096.0)) continue;
            ShadowService.setOrder(p, r, Order.GUARD);
            r.anchor = b.pos().m_7494_();
            changed = true;
        }
        if (changed) {
            RoyalService.dirty(p);
        }
        return changed;
    }

    public static void clearRuntime(ServerPlayer p) {
        RoyalState e = RoyalService.state(p);
        e.breach.clear();
        e.notices.clear();
        e.lastEclipse = null;
        e.lastRoyal = null;
        e.desk = null;
        e.deskScan = -100L;
        e.royalViewUntil = 0L;
    }

    public record Price(int diamonds, int emeralds, Item extra, int count) {
    }
}

