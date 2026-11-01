/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.Container
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.BlockSnapshot
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 *  net.minecraftforge.eventbus.api.Event
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ArmyData;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.odyssey.OdysseyTrials;
import dev.shadowsovereign.odyssey.OdysseyVisitors;
import dev.shadowsovereign.soulforge.CompanionService;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.soullife.SoulPlay;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

public final class OdysseyCamp {
    public static boolean bind(ServerPlayer p, OdysseyContent.Station station, BlockPos at) {
        block3: {
            block2: {
                BlockEntity blockEntity;
                if (!OdysseyRealm.camp(p, at) || p.m_20238_(Vec3.m_82512_((Vec3i)at)) > 64.0 || !((blockEntity = p.m_9236_().m_7702_(at)) instanceof OdysseyContent.CampEntity)) break block2;
                OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)blockEntity;
                if (p.m_20148_().equals(be.owner)) break block3;
            }
            return false;
        }
        GuideService.state((ServerPlayer)p).camp.m_128356_("Site." + station.name(), at.m_121878_());
        GuideService.dirty(p);
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static BlockPos site(ServerPlayer p, OdysseyContent.Station station) {
        CompoundTag camp = GuideService.state((ServerPlayer)p).camp;
        String key = "Site." + station.name();
        if (!camp.m_128441_(key)) return null;
        if (!OdysseyRealm.inside(p)) {
            return null;
        }
        BlockPos at = BlockPos.m_122022_((long)camp.m_128454_(key));
        if (!p.m_284548_().m_46805_(at)) return null;
        if (!p.m_9236_().m_8055_(at).m_60713_((Block)OdysseyContent.STATIONS.get((Object)station).get())) return null;
        BlockEntity blockEntity = p.m_9236_().m_7702_(at);
        if (!(blockEntity instanceof OdysseyContent.CampEntity)) return null;
        OdysseyContent.CampEntity be = (OdysseyContent.CampEntity)blockEntity;
        if (!p.m_20148_().equals(be.owner)) return null;
        BlockPos blockPos = at;
        return blockPos;
    }

    public static boolean near(ServerPlayer p, OdysseyContent.Station station) {
        BlockPos at = OdysseyCamp.site(p, station);
        return at != null && p.m_20238_(Vec3.m_82512_((Vec3i)at)) <= 64.0;
    }

    public static OdysseyContent.CampEntity store(ServerPlayer p) {
        BlockPos at = OdysseyCamp.site(p, OdysseyContent.Station.STORE);
        return at == null ? null : (OdysseyContent.CampEntity)p.m_9236_().m_7702_(at);
    }

    public static void openStore(final ServerPlayer p, final BlockPos at) {
        if (!OdysseyCamp.near(p, OdysseyContent.Station.STORE)) {
            return;
        }
        final OdysseyContent.CampEntity be = OdysseyCamp.store(p);
        Container container = new Container(){

            public int m_6643_() {
                return 27;
            }

            public boolean m_7983_() {
                return be.items.stream().allMatch(ItemStack::m_41619_);
            }

            public ItemStack m_8020_(int i) {
                return (ItemStack)be.items.get(i);
            }

            public ItemStack m_7407_(int i, int count) {
                ItemStack out = ((ItemStack)be.items.get(i)).m_41620_(count);
                this.m_6596_();
                return out;
            }

            public ItemStack m_8016_(int i) {
                ItemStack out = (ItemStack)be.items.get(i);
                be.items.set(i, (Object)ItemStack.f_41583_);
                this.m_6596_();
                return out;
            }

            public void m_6836_(int i, ItemStack item) {
                be.items.set(i, (Object)item);
                this.m_6596_();
            }

            public void m_6596_() {
                be.m_6596_();
            }

            public boolean m_6542_(Player who) {
                return who == p && OdysseyCamp.near(p, OdysseyContent.Station.STORE) && p.m_9236_().m_7702_(at) == be;
            }

            public void m_6211_() {
                be.items.replaceAll(i -> ItemStack.f_41583_);
                this.m_6596_();
            }
        };
        p.m_5893_((MenuProvider)new SimpleMenuProvider((arg_0, arg_1, arg_2) -> OdysseyCamp.lambda$openStore$0(container, arg_0, arg_1, arg_2), (Component)OdysseyText.c("store", new Object[0])));
    }

    public static boolean materials(ServerPlayer p, Map<Item, Integer> costs, boolean spend) {
        OdysseyContent.CampEntity be = OdysseyCamp.store(p);
        if (be == null) {
            return false;
        }
        for (Map.Entry<Item, Integer> need : costs.entrySet()) {
            if (be.items.stream().filter(v -> v.m_150930_((Item)need.getKey())).mapToInt(ItemStack::m_41613_).sum() >= need.getValue()) continue;
            return false;
        }
        if (spend) {
            block1: for (Map.Entry<Item, Integer> need : costs.entrySet()) {
                int left = need.getValue();
                for (ItemStack stack : be.items) {
                    if (!stack.m_150930_(need.getKey())) continue;
                    int amount = Math.min(left, stack.m_41613_());
                    stack.m_41774_(amount);
                    if ((left -= amount) != 0) continue;
                    continue block1;
                }
            }
            be.m_6596_();
        }
        return true;
    }

    public static void deliver(ServerPlayer p, ItemStack stack) {
        if (stack.m_41619_()) {
            return;
        }
        p.m_150109_().m_36054_(stack);
        if (!stack.m_41619_()) {
            p.m_36176_(stack, false);
        }
        p.m_150109_().m_6596_();
    }

    private static ShadowRecord selected(ServerPlayer p) {
        return GuideService.army((ServerPlayer)p).shadows.get(GuideService.state((ServerPlayer)p).selected);
    }

    public static boolean assign(ServerPlayer p, int job, int variant) {
        BlockPos at;
        ShadowRecord r = OdysseyCamp.selected(p);
        if (r == null || job < 0 || job > 3 || !OdysseyRealm.camp(p, p.m_20183_()) || !SoulLifeService.quiet(p, r)) {
            return false;
        }
        OdysseyContent.Station station = job == 1 ? OdysseyContent.Station.ARCHITECT : (job == 2 ? OdysseyContent.Station.GARDEN : OdysseyContent.Station.BENCH);
        BlockPos blockPos = at = job == 0 ? p.m_20183_() : OdysseyCamp.site(p, station);
        if (at == null) {
            return false;
        }
        if (!(job != 1 || variant >= 0 && variant <= 2 && OdysseyCamp.near(p, station))) {
            return false;
        }
        r.odyssey.job = job;
        r.odyssey.work = at;
        r.odyssey.house = job == 3 ? Math.floorMod(variant, 8) : -1;
        CompoundTag camp = GuideService.state((ServerPlayer)p).camp;
        if (job == 1) {
            camp.m_128405_("Blueprint", variant);
            camp.m_128405_("BuildStep", 0);
            camp.m_128356_("BuildOrigin", at.m_7918_(5, 0, 0).m_121878_());
        }
        ArmyData.get(p.f_8924_).m_77762_();
        GuideService.dirty(p);
        return true;
    }

    public static void onCommand(ServerPlayer p, ShadowRecord r) {
        r.odyssey.job = 0;
        r.odyssey.house = -1;
    }

    public static boolean controlled(ServerPlayer p, ShadowRecord r) {
        Mob m = ShadowService.active(p.f_8924_, r);
        return !(r.odyssey.job <= 0 && !OdysseyVisitors.photoControlled(p, r) || m == null || !OdysseyRealm.camp(p, m.m_20183_()) || !(p.m_20280_((Entity)m) <= 4096.0) || !SoulLifeService.owner((ServerPlayer)p).activity.m_128456_() || !OdysseyCamp.AdventureDataSessionFree(p) || VigilCrown.active(p) || m.m_5448_() != null || r.lastDamageAt >= 0L && GuideService.now(p) - r.lastDamageAt < 100L);
    }

    private static boolean AdventureDataSessionFree(ServerPlayer p) {
        return AdventureService.journal((ServerPlayer)p).session == null;
    }

    public static boolean moving(Mob m, ShadowRecord r, ServerPlayer p) {
        return OdysseyCamp.controlled(p, r);
    }

    private static List<Placement> blueprint(int variant) {
        int z;
        Block floor;
        ArrayList<Placement> list = new ArrayList<Placement>();
        Block block = variant == 0 ? Blocks.f_50705_ : (floor = variant == 1 ? Blocks.f_50222_ : Blocks.f_50735_);
        Block post = variant == 0 ? Blocks.f_49999_ : (variant == 1 ? Blocks.f_50652_ : Blocks.f_152490_);
        for (int x = 0; x < 5; ++x) {
            for (z = 0; z < 5; ++z) {
                list.add(new Placement(new BlockPos(x, 0, z), floor.m_49966_()));
            }
        }
        int[] x = new int[]{0, 4};
        z = x.length;
        for (int i = 0; i < z; ++i) {
            int x2 = x[i];
            for (int z2 : new int[]{0, 4}) {
                for (int y = 1; y <= 3; ++y) {
                    list.add(new Placement(new BlockPos(x2, y, z2), post.m_49966_()));
                }
            }
        }
        for (int x3 = -1; x3 <= 5; ++x3) {
            for (z = -1; z <= 5; ++z) {
                list.add(new Placement(new BlockPos(x3, 4, z), floor.m_49966_()));
            }
        }
        return list;
    }

    public static Map<Item, Integer> blueprintCosts(int variant) {
        LinkedHashMap<Item, Integer> costs = new LinkedHashMap<Item, Integer>();
        for (Placement step : OdysseyCamp.blueprint(Math.floorMod(variant, 3))) {
            costs.merge(step.state.m_60734_().m_5456_(), 1, Integer::sum);
        }
        return costs;
    }

    private static void build(ServerPlayer p, ShadowRecord r, Mob m) {
        CompoundTag camp = GuideService.state((ServerPlayer)p).camp;
        List<Placement> plan = OdysseyCamp.blueprint(camp.m_128451_("Blueprint"));
        int index = camp.m_128451_("BuildStep");
        if (index >= plan.size()) {
            r.odyssey.job = 0;
            GuideService.record(p, "built.architect");
            return;
        }
        Placement step = plan.get(index);
        BlockPos at = BlockPos.m_122022_((long)camp.m_128454_("BuildOrigin")).m_121955_((Vec3i)step.offset);
        if (!OdysseyRealm.camp(p, at) || !p.m_284548_().m_46805_(at)) {
            return;
        }
        if (p.m_9236_().m_8055_(at).equals(step.state)) {
            camp.m_128405_("BuildStep", index + 1);
            GuideService.dirty(p);
            return;
        }
        if (!p.m_9236_().m_8055_(at).m_247087_() || p.m_20191_().m_82381_(new AABB(at))) {
            return;
        }
        Map<Item, Integer> cost = Map.of(step.state.m_60734_().m_5456_(), 1);
        if (!OdysseyCamp.materials(p, cost, false)) {
            return;
        }
        if (m.m_20191_().m_82381_(new AABB(at)) || m.m_20182_().m_82542_(1.0, 0.0, 1.0).m_82557_(Vec3.m_82512_((Vec3i)at).m_82542_(1.0, 0.0, 1.0)) > 36.0 || Math.abs(m.m_20186_() - (double)at.m_123342_()) > 5.0) {
            Vec3 work = OdysseyCamp.builderStand(p, m, at, BlockPos.m_122022_((long)camp.m_128454_("BuildOrigin")).m_123342_());
            if (work != null) {
                m.m_21573_().m_26519_(work.f_82479_, work.f_82480_, work.f_82481_, 1.0);
            }
            return;
        }
        m.m_21573_().m_26573_();
        BlockSnapshot snapshot = BlockSnapshot.create((ResourceKey)p.m_9236_().m_46472_(), (LevelAccessor)p.m_9236_(), (BlockPos)at);
        if (!p.m_9236_().m_7731_(at, step.state, 3)) {
            return;
        }
        if (ForgeEventFactory.onBlockPlace((Entity)p, (BlockSnapshot)snapshot, (Direction)Direction.UP)) {
            snapshot.restore(true, false);
            return;
        }
        OdysseyCamp.materials(p, cost, true);
        camp.m_128405_("BuildStep", index + 1);
        GuideService.dirty(p);
        m.m_6674_(InteractionHand.MAIN_HAND);
    }

    private static Vec3 builderStand(ServerPlayer p, Mob mob, BlockPos block, int floorY) {
        BlockPos origin = BlockPos.m_122022_((long)GuideService.state((ServerPlayer)p).camp.m_128454_("BuildOrigin"));
        Vec3 best = null;
        double distance = Double.MAX_VALUE;
        for (int[] offset : new int[][]{{-2, 2}, {6, 2}, {2, -2}, {2, 6}}) {
            for (int dy : new int[]{0, 1, -1}) {
                double d;
                BlockPos at = origin.m_7918_(offset[0], dy, offset[1]);
                Vec3 point = Vec3.m_82539_((Vec3i)at);
                AABB box = mob.m_6972_(mob.m_20089_()).m_20393_(point);
                if (point.m_82542_(1.0, 0.0, 1.0).m_82557_(Vec3.m_82512_((Vec3i)block).m_82542_(1.0, 0.0, 1.0)) > 16.0 || box.m_82381_(new AABB(block)) || !p.m_284548_().m_46805_(at) || p.m_9236_().m_8055_(at.m_7495_()).m_60812_((BlockGetter)p.m_9236_(), at.m_7495_()).m_83281_() || !p.m_9236_().m_45756_((Entity)mob, box) || !((d = mob.m_20238_(point)) < distance)) continue;
                best = point;
                distance = d;
            }
        }
        return best;
    }

    private static void garden(ServerPlayer p, ShadowRecord r, Mob m) {
        BlockPos center = r.odyssey.work;
        r.inventory.initialize(r.snapshot);
        ItemStack tool = m.m_21205_();
        if (!tool.m_204117_(ItemTags.f_271298_)) {
            return;
        }
        for (int i = 0; i < 81; ++i) {
            CropBlock crop;
            BlockState state;
            Block block;
            BlockPos at = center.m_7918_(i % 9 - 4, 0, i / 9 - 4);
            if (!OdysseyRealm.camp(p, at) || !p.m_284548_().m_46805_(at) || !((block = (state = p.m_9236_().m_8055_(at)).m_60734_()) instanceof CropBlock) || !(crop = (CropBlock)block).m_52307_(state)) continue;
            if (m.m_20238_(Vec3.m_82512_((Vec3i)at)) > 9.0) {
                m.m_21573_().m_26519_((double)at.m_123341_() + 0.5, (double)at.m_123342_(), (double)at.m_123343_() + 0.5, 1.0);
                return;
            }
            BlockEvent.BreakEvent denied = new BlockEvent.BreakEvent(p.m_9236_(), at, state, (Player)p);
            if (MinecraftForge.EVENT_BUS.post((Event)denied)) {
                return;
            }
            List drops = Block.m_49874_((BlockState)state, (ServerLevel)p.m_284548_(), (BlockPos)at, (BlockEntity)p.m_9236_().m_7702_(at), (Entity)m, (ItemStack)tool);
            ItemStack seed = crop.m_7397_((BlockGetter)p.m_9236_(), at, state);
            ItemStack replant = drops.stream().filter(v -> v.m_150930_(seed.m_41720_()) && !v.m_41619_()).findFirst().orElse(null);
            if (replant == null) {
                return;
            }
            BlockSnapshot before = BlockSnapshot.create((ResourceKey)p.m_9236_().m_46472_(), (LevelAccessor)p.m_9236_(), (BlockPos)at);
            p.m_9236_().m_7731_(at, crop.m_52289_(0), 3);
            if (ForgeEventFactory.onBlockPlace((Entity)p, (BlockSnapshot)before, (Direction)Direction.UP)) {
                before.restore(true, false);
                return;
            }
            replant.m_41774_(1);
            for (ItemStack stack : drops) {
                ItemStack rest = CompanionService.store(r, stack);
                if (rest.m_41619_()) continue;
                p.m_284548_().m_7967_((Entity)new ItemEntity(p.m_9236_(), (double)at.m_123341_() + 0.5, (double)at.m_123342_() + 0.5, (double)at.m_123343_() + 0.5, rest));
            }
            tool.m_41622_(1, (LivingEntity)m, actor -> actor.m_21166_(EquipmentSlot.MAINHAND));
            r.inventory.items.set(0, (Object)tool.m_41777_());
            GuideService.record(p, "garden_harvest");
            ArmyData.get(p.f_8924_).m_77762_();
            return;
        }
    }

    public static boolean cook(ServerPlayer p, int recipe) {
        if (recipe < 0 || recipe > 7 || !OdysseyCamp.near(p, OdysseyContent.Station.KITCHEN)) {
            return false;
        }
        Item[][] ingredients = new Item[][]{{Items.f_42406_, Items.f_42619_}, {Items.f_42674_, Items.f_42732_}, {Items.f_42410_, Items.f_42405_}, {Items.f_42582_, Items.f_42620_}, {Items.f_42530_, Items.f_42576_}, {Items.f_42575_, Items.f_42780_}, {Items.f_42580_, Items.f_42400_}, {Items.f_42687_, Items.f_42787_}};
        Map<Item, Integer> needs = Map.of(ingredients[recipe][0], 2, ingredients[recipe][1], 2);
        if (!OdysseyCamp.materials(p, needs, true)) {
            return false;
        }
        ItemStack out = new ItemStack((ItemLike)OdysseyContent.FEAST.get());
        out.m_41714_((Component)OdysseyText.c("dish." + recipe, new Object[0]));
        out.m_41784_().m_128405_("Recipe", recipe);
        OdysseyCamp.deliver(p, out);
        for (Item item : ingredients[recipe]) {
            ItemStack remainder = new ItemStack((ItemLike)item).getCraftingRemainingItem();
            if (remainder.m_41619_()) continue;
            remainder.m_41764_(2);
            OdysseyCamp.deliver(p, remainder);
        }
        GuideService.record(p, "cooked");
        return true;
    }

    public static boolean furniture(ServerPlayer p, int id) {
        if (id < 0 || id > 3 || !OdysseyCamp.near(p, OdysseyContent.Station.WORKSHOP) || !OdysseyCamp.materials(p, Map.of(Items.f_42647_, 4, Items.f_151049_, 1), true)) {
            return false;
        }
        OdysseyContent.Station station = (new OdysseyContent.Station[]{OdysseyContent.Station.BENCH, OdysseyContent.Station.MUSIC_STAND, OdysseyContent.Station.NOTICE, OdysseyContent.Station.LAMP})[id];
        OdysseyCamp.deliver(p, new ItemStack((ItemLike)((OdysseyContent.CampBlock)((Object)OdysseyContent.STATIONS.get((Object)station).get())).m_5456_()));
        GuideService.record(p, "furniture");
        return true;
    }

    public static boolean personal(ServerPlayer p, int choice) {
        OdysseyCatalog.Mode mode;
        ShadowRecord r = OdysseyCamp.selected(p);
        if (r == null || choice < 0 || choice > 2 || !SoulLifeService.quiet(p, r) || !OdysseyRealm.camp(p, p.m_20183_())) {
            return false;
        }
        OdysseyState state = GuideService.state(p);
        int stage = r.odyssey.story;
        if (stage == 0 && (r.life.pets < 1 || r.life.meals < 1 || r.life.walked < 100.0)) {
            return false;
        }
        if (stage == 1 && (r.odyssey.wins & 1 << (mode = (new OdysseyCatalog.Mode[]{OdysseyCatalog.Mode.ARCHIVIST, OdysseyCatalog.Mode.TREASURE, OdysseyCatalog.Mode.LOOP, OdysseyCatalog.Mode.BOWLING, OdysseyCatalog.Mode.COMPANIES, OdysseyCatalog.Mode.MIRRORS})[Math.floorMod(r.life.personality, 6)]).ordinal()) == 0 && state.count("crew." + String.valueOf(r.id) + "." + mode.id()) == 0L) {
            return false;
        }
        if (stage == 2) {
            if (r.life.friends.isEmpty() || !p.m_21205_().m_150930_(Items.f_42516_)) {
                return false;
            }
            p.m_21205_().m_41774_(1);
            ItemStack letter = new ItemStack((ItemLike)OdysseyContent.LETTER.get());
            CompoundTag n = letter.m_41784_();
            n.m_128362_("Companion", r.id);
            n.m_128359_("Name", SoulLifeService.name(p, r));
            n.m_128405_("Ending", choice);
            OdysseyCamp.deliver(p, letter);
            r.odyssey.gifts.add("story");
            r.odyssey.gestures |= 1 << choice;
        }
        if (stage >= 3) {
            return false;
        }
        ++r.odyssey.story;
        SoulLifeService.affection(p, r, 5);
        SoulLifeService.memory(p, r, "story", Integer.toString(choice));
        ArmyData.get(p.f_8924_).m_77762_();
        GuideService.record(p, "personal_stage");
        OdysseyText.tell(p, "personal_reply." + Math.floorMod(r.life.personality, 6) + "." + stage, SoulLifeService.name(p, r));
        return true;
    }

    public static boolean club(ServerPlayer p, int club) {
        ShadowRecord r = OdysseyCamp.selected(p);
        if (r == null || club < -1 || club > 2 || !OdysseyCamp.near(p, OdysseyContent.Station.CLUB) || !SoulLifeService.quiet(p, r)) {
            return false;
        }
        r.odyssey.club = club;
        ArmyData.get(p.f_8924_).m_77762_();
        return true;
    }

    public static boolean gesture(ServerPlayer p, int id) {
        ShadowRecord r = OdysseyCamp.selected(p);
        if (r == null || id < 0 || id > 2 || (r.odyssey.gestures & 1 << id) == 0 || !SoulLifeService.quiet(p, r)) {
            return false;
        }
        Mob m = ShadowService.active(p.f_8924_, r);
        m.m_21563_().m_24960_((Entity)p, 40.0f, 40.0f);
        m.m_6674_(InteractionHand.MAIN_HAND);
        SoulLifeService.particles(p, m, (ParticleOptions)(id == 0 ? ParticleTypes.f_123750_ : (id == 1 ? ParticleTypes.f_123758_ : ParticleTypes.f_123748_)), 5);
        OdysseyText.tell(p, "gesture." + id, SoulLifeService.name(p, r));
        return true;
    }

    public static boolean sit(ServerPlayer p) {
        ShadowRecord r = OdysseyCamp.selected(p);
        BlockPos at = OdysseyCamp.site(p, OdysseyContent.Station.BENCH);
        if (r == null || at == null || !OdysseyCamp.near(p, OdysseyContent.Station.BENCH) || !SoulLifeService.quiet(p, r)) {
            return false;
        }
        r.odyssey.job = 3;
        r.odyssey.work = at;
        ArmyData.get(p.f_8924_).m_77762_();
        return true;
    }

    public static boolean performMusic(ServerPlayer p) {
        ShadowRecord r = OdysseyCamp.selected(p);
        return r != null && OdysseyCamp.near(p, OdysseyContent.Station.MUSIC_STAND) && SoulPlay.start(p, SoulPlay.Game.MELODY, List.of(r.id));
    }

    public static boolean equipArtifact(ServerPlayer p, int id) {
        if (id >= 0 && id % 6 >= 4) {
            return false;
        }
        if (id >= 0 && GuideService.state((ServerPlayer)p).artifact == id) {
            return OdysseyCamp.useArtifact(p, id);
        }
        OdysseyState s = GuideService.state(p);
        if (id < -1 || id > 23 || id >= 0 && (!s.earned.contains("artifact." + id) || p.m_150109_().m_18947_((Item)OdysseyContent.ARTIFACTS.get(id).get()) == 0)) {
            return false;
        }
        s.artifact = id;
        GuideService.dirty(p);
        if (id >= 0) {
            OdysseyText.tell(p, "relic_equipped", OdysseyText.c("artifact." + id, new Object[0]));
        }
        return true;
    }

    public static boolean useArtifact(ServerPlayer p, int id) {
        OdysseyState state = GuideService.state(p);
        if (id < 0 || id >= 24 || id % 6 >= 4 || state.artifact != id || p.m_150109_().m_18947_((Item)OdysseyContent.ARTIFACTS.get(id).get()) == 0 || GuideService.now(p) < state.aidAt) {
            return false;
        }
        AdventureData.Session scene = OdysseyRuns.session(p);
        int family = id % 6;
        if (scene != null && scene.journey.m_128451_("Rule") == 3) {
            return false;
        }
        boolean used = false;
        if (family == 0 && scene != null && scene.journey.m_128471_("Built")) {
            BlockPos at = OdysseyTrials.marker(scene, scene.journey.m_128451_("Stage") * 2);
            if (OdysseyRuns.mode(scene) == OdysseyCatalog.Mode.TREASURE) {
                at = OdysseyTrials.marker(scene, scene.journey.m_128451_("TreasureNext"));
            }
            p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123746_, (double)at.m_123341_() + 0.5, (double)(at.m_123342_() + 1), (double)at.m_123343_() + 0.5, 10, 0.2, 0.2, 0.2, 0.03);
            OdysseyText.tell(p, "relic_clue", new Object[0]);
            used = true;
        }
        if (family == 2 && scene != null && scene.journey.m_128471_("Built")) {
            OdysseyCatalog.Mode type = OdysseyRuns.mode(scene);
            if (type == OdysseyCatalog.Mode.MIRRORS) {
                OdysseyText.tell(p, "mirror_clue", new Object[0]);
            } else if (type == OdysseyCatalog.Mode.LOOP) {
                OdysseyText.tell(p, "loop_clue", new Object[0]);
            } else if (type == OdysseyCatalog.Mode.MASQUERADE) {
                OdysseyText.tell(p, "mask_clue", new Object[0]);
            } else {
                OdysseyText.tell(p, "activity_help." + type.id(), new Object[0]);
            }
            used = true;
        }
        if (family == 1) {
            Mob m;
            ShadowRecord r = OdysseyCamp.selected(p);
            Mob mob = m = r == null ? null : ShadowService.active(p.f_8924_, r);
            if (m != null && m.m_9236_() == p.m_9236_() && m.m_20280_((Entity)p) <= 16.0 && m.m_21223_() < m.m_21233_() && OdysseyRealm.camp(p, p.m_20183_()) && OdysseyCamp.materials(p, Map.of(Items.f_42406_, 1), true)) {
                m.m_5634_(2.0f);
                r.health = m.m_21223_();
                ArmyData.get(p.f_8924_).m_77762_();
                used = true;
            }
        }
        if (family == 3 && OdysseyCamp.near(p, OdysseyContent.Station.ARCHITECT)) {
            for (Map.Entry<Item, Integer> entry : OdysseyCamp.blueprintCosts(state.camp.m_128451_("Blueprint")).entrySet()) {
                OdysseyText.tell(p, "builder_material", entry.getKey().m_41466_(), entry.getValue());
            }
            used = true;
        }
        if (used) {
            state.aidAt = GuideService.now(p) + (long)(family == 1 ? 1200 : 200);
            GuideService.dirty(p);
        }
        return used;
    }

    public static boolean festivalRelic(ServerPlayer p) {
        return false;
    }

    public static void earnArtifact(ServerPlayer p, int id) {
        if (id >= 0 && id % 6 >= 4) {
            id -= id % 6 - 3;
        }
        OdysseyState s = GuideService.state(p);
        if (id < 0 || id > 23 || !s.earned.add("artifact." + id)) {
            return;
        }
        OdysseyCamp.deliver(p, new ItemStack((ItemLike)OdysseyContent.ARTIFACTS.get(id).get()));
        GuideService.dirty(p);
    }

    public static void tick(ServerPlayer p) {
        List<ShadowRecord> candidates;
        if (!OdysseyRealm.inside(p) || !OdysseyRealm.camp(p, p.m_20183_()) || GuideService.now(p) % 10L != 0L) {
            return;
        }
        for (ShadowRecord r2 : GuideService.army((ServerPlayer)p).shadows.values()) {
            if (!OdysseyCamp.controlled(p, r2)) continue;
            Mob m = ShadowService.active(p.f_8924_, r2);
            if (r2.odyssey.job == 1) {
                OdysseyCamp.build(p, r2, m);
                continue;
            }
            if (r2.odyssey.job == 2 && GuideService.now(p) % 20L == 0L) {
                OdysseyCamp.garden(p, r2, m);
                continue;
            }
            if (r2.odyssey.job != 3) continue;
            Vec3 at = Vec3.m_82539_((Vec3i)r2.odyssey.work.m_7494_()).m_82520_(1.0, 0.0, 0.0);
            if (m.m_20238_(at) > 2.25) {
                m.m_21573_().m_26519_(at.f_82479_, at.f_82480_, at.f_82481_, 0.8);
                continue;
            }
            m.m_21573_().m_26573_();
            m.m_21563_().m_24960_((Entity)p, 25.0f, 25.0f);
            r2.life.restUntil = GuideService.now(p) + 40L;
        }
        OdysseyState s = GuideService.state(p);
        int score = (int)Math.min(10L, s.count("camp_placed")) + (int)Math.min(5L, s.count("cooked")) + (int)Math.min(5L, s.count("garden_harvest")) + (int)Math.min(5L, s.count("built.architect")) + (int)Math.min(5L, s.count("personal_stage"));
        int rep = Math.min(5, score / 5);
        if (rep > s.rep) {
            s.rep = rep;
            GuideService.dirty(p);
            OdysseyText.tell(p, "camp_rank", rep);
        }
        if (s.pranks && GuideService.now(p) >= s.voiceAt && OdysseyCamp.AdventureDataSessionFree(p) && !VigilCrown.active(p) && !(candidates = GuideService.army((ServerPlayer)p).shadows.values().stream().filter(r -> SoulLifeService.quiet(p, r) && r.life.affection >= 5 && GuideService.now(p) >= r.odyssey.prankAt).toList()).isEmpty()) {
            ShadowRecord r3 = candidates.get(p.m_217043_().m_188503_(candidates.size()));
            Mob m = ShadowService.active(p.f_8924_, r3);
            int joke = p.m_217043_().m_188503_(5);
            m.m_6674_(InteractionHand.MAIN_HAND);
            SoulLifeService.particles(p, m, (ParticleOptions)(joke == 0 ? ParticleTypes.f_123763_ : (joke == 1 ? ParticleTypes.f_123758_ : ParticleTypes.f_123748_)), 4);
            OdysseyText.tell(p, "prank." + joke, SoulLifeService.name(p, r3));
            s.voiceAt = GuideService.now(p) + 2400L;
            r3.odyssey.prankAt = GuideService.now(p) + 6000L;
            GuideService.dirty(p);
            ArmyData.get(p.f_8924_).m_77762_();
        }
    }

    private static /* synthetic */ AbstractContainerMenu lambda$openStore$0(1 container, int id, Inventory inventory, Player user) {
        return ChestMenu.m_39237_((int)id, (Inventory)inventory, (Container)container);
    }

    private record Placement(BlockPos offset, BlockState state) {
    }
}

