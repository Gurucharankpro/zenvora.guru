/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.npc.WanderingTrader
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.army.ShadowRecord;
import dev.shadowsovereign.army.ShadowService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyCamp;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.soullife.SoulLifeService;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class OdysseyVisitors {
    public static boolean invite(ServerPlayer p) {
        if (!OdysseyCamp.near(p, OdysseyContent.Station.VISITORS)) {
            return false;
        }
        CompoundTag c = GuideService.state((ServerPlayer)p).camp;
        if (c.m_128471_("VisitorActive")) {
            return false;
        }
        c.m_128379_("VisitorActive", true);
        c.m_128356_("VisitorVisit", c.m_128454_("VisitorVisit") + 1L);
        c.m_128405_("VisitorType", Math.floorMod((int)(GuideService.state((ServerPlayer)p).seed + c.m_128454_("VisitorVisit")), 6));
        c.m_128405_("Trades", 0);
        c.m_128473_("VisitorEntity");
        GuideService.record(p, "visitor_invited");
        OdysseyVisitors.spawn(p);
        return true;
    }

    private static void spawn(ServerPlayer p) {
        WanderingTrader mob;
        CompoundTag c = GuideService.state((ServerPlayer)p).camp;
        BlockPos board = OdysseyCamp.site(p, OdysseyContent.Station.VISITORS);
        if (board == null || !c.m_128471_("VisitorActive")) {
            return;
        }
        if (c.m_128403_("VisitorEntity")) {
            if (p.m_284548_().m_8791_(c.m_128342_("VisitorEntity")) != null) {
                c.m_128473_("VisitorMissingAt");
                return;
            }
            if (!p.m_284548_().m_143319_(ChunkPos.m_151388_((BlockPos)board))) {
                return;
            }
            if (!c.m_128441_("VisitorMissingAt")) {
                c.m_128356_("VisitorMissingAt", GuideService.now(p));
                GuideService.dirty(p);
                return;
            }
            if (GuideService.now(p) - c.m_128454_("VisitorMissingAt") < 100L) {
                return;
            }
        }
        if ((mob = (WanderingTrader)EntityType.f_20494_.m_20615_((Level)p.m_284548_())) == null) {
            return;
        }
        Vec3 at = ShadowService.safePosition((Mob)mob, Vec3.m_82539_((Vec3i)board).m_82520_(2.0, 0.0, 1.0), p.m_284548_());
        if (at == null) {
            return;
        }
        mob.m_20219_(at);
        mob.m_21557_(true);
        mob.m_21530_();
        mob.m_20331_(true);
        mob.getPersistentData().m_128362_("OdysseyHost", p.m_20148_());
        mob.getPersistentData().m_128379_("SoulboundExtractionOffered", true);
        mob.m_6593_((Component)OdysseyText.c("visitor." + c.m_128451_("VisitorType"), new Object[0]));
        mob.m_20340_(true);
        UUID previous = c.m_128403_("VisitorEntity") ? c.m_128342_("VisitorEntity") : null;
        c.m_128362_("VisitorEntity", mob.m_20148_());
        if (p.m_284548_().m_7967_((Entity)mob)) {
            c.m_128473_("VisitorMissingAt");
            GuideService.dirty(p);
        } else if (previous != null) {
            c.m_128362_("VisitorEntity", previous);
        } else {
            c.m_128473_("VisitorEntity");
        }
    }

    public static boolean trade(ServerPlayer p, int offer) {
        String grant;
        ItemStack result;
        if (!OdysseyCamp.near(p, OdysseyContent.Station.VISITORS) || offer < 0 || offer > 3) {
            return false;
        }
        OdysseyState state = GuideService.state(p);
        CompoundTag c = state.camp;
        if (!c.m_128471_("VisitorActive")) {
            return false;
        }
        if (offer == 3) {
            Entity entity;
            if (c.m_128403_("VisitorEntity") && (entity = p.m_284548_().m_8791_(c.m_128342_("VisitorEntity"))) instanceof Mob) {
                Mob m = (Mob)entity;
                m.m_146870_();
            }
            c.m_128379_("VisitorActive", false);
            c.m_128473_("VisitorEntity");
            GuideService.dirty(p);
            return true;
        }
        if ((c.m_128451_("Trades") & 1 << offer) != 0) {
            return false;
        }
        int[] costs = new int[]{2, 3, 4};
        if (state.seals < costs[offer]) {
            return false;
        }
        ItemStack itemStack = offer == 0 ? new ItemStack((ItemLike)Items.f_151049_, 4) : (result = offer == 1 ? new ItemStack((ItemLike)OdysseyContent.FEAST.get(), 2) : new ItemStack((ItemLike)OdysseyContent.LETTER.get()));
        if (offer == 2) {
            result.m_41784_().m_128405_("VisitorStory", c.m_128451_("VisitorType"));
            result.m_41714_((Component)OdysseyText.c("visitor_letter", OdysseyText.c("visitor." + c.m_128451_("VisitorType"), new Object[0])));
        }
        if (!state.grant(grant = "visitor:" + c.m_128454_("VisitorVisit") + ":" + offer)) {
            return false;
        }
        state.seals -= costs[offer];
        c.m_128405_("Trades", c.m_128451_("Trades") | 1 << offer);
        OdysseyCamp.deliver(p, result);
        GuideService.record(p, "visitor_trade");
        return true;
    }

    public static boolean photo(ServerPlayer p) {
        if (!OdysseyRealm.camp(p, p.m_20183_()) || !p.m_6084_() || AdventureService.journal((ServerPlayer)p).session != null || VigilCrown.active(p)) {
            return false;
        }
        List<ShadowRecord> members = GuideService.army((ServerPlayer)p).shadows.values().stream().filter(r -> SoulLifeService.quiet(p, r)).limit(6L).toList();
        if (members.size() < 2) {
            return false;
        }
        CompoundTag c = GuideService.state((ServerPlayer)p).camp;
        c.m_128356_("PhotoUntil", GuideService.now(p) + 200L);
        c.m_128356_("PhotoCenter", p.m_20183_().m_121878_());
        c.m_128350_("PhotoYaw", p.m_146908_());
        ListTag list = new ListTag();
        for (ShadowRecord r2 : members) {
            CompoundTag row = new CompoundTag();
            row.m_128362_("Id", r2.id);
            list.add((Object)row);
        }
        c.m_128365_("PhotoMembers", (Tag)list);
        GuideService.dirty(p);
        OdysseyText.tell(p, "photo_ready", new Object[0]);
        return true;
    }

    public static boolean photoControlled(ServerPlayer p, ShadowRecord r) {
        CompoundTag c = GuideService.state((ServerPlayer)p).camp;
        return c.m_128454_("PhotoUntil") > GuideService.now(p) && c.m_128437_("PhotoMembers", 10).stream().anyMatch(v -> ((CompoundTag)v).m_128403_("Id") && r.id.equals(((CompoundTag)v).m_128342_("Id")));
    }

    public static void tick(ServerPlayer p) {
        if (!OdysseyRealm.camp(p, p.m_20183_())) {
            return;
        }
        OdysseyState state = GuideService.state(p);
        CompoundTag c = state.camp;
        if (GuideService.now(p) % 40L == 0L) {
            Optional<ShadowRecord> crew;
            OdysseyVisitors.spawn(p);
            long last = c.m_128454_("LastCampSeen");
            if (state.welcomes && (c.m_128471_("WelcomePending") || last > 0L && GuideService.now(p) - last >= 1200L) && GuideService.now(p) >= c.m_128454_("NextWelcome") && AdventureService.journal((ServerPlayer)p).session == null && !VigilCrown.active(p) && (crew = GuideService.army((ServerPlayer)p).shadows.values().stream().filter(r -> SoulLifeService.quiet(p, r)).findFirst()).isPresent()) {
                ShadowRecord r2 = crew.get();
                Mob mob = ShadowService.active(p.f_8924_, r2);
                mob.m_21563_().m_24960_((Entity)p, 40.0f, 40.0f);
                mob.m_6674_(InteractionHand.MAIN_HAND);
                OdysseyText.tell(p, "home_welcome_named", SoulLifeService.name(p, r2));
                c.m_128356_("NextWelcome", GuideService.now(p) + 2400L);
                c.m_128379_("WelcomePending", false);
            }
            c.m_128356_("LastCampSeen", GuideService.now(p));
            GuideService.dirty(p);
        }
        if (c.m_128454_("PhotoUntil") > GuideService.now(p)) {
            Vec3 center = Vec3.m_82539_((Vec3i)BlockPos.m_122022_((long)c.m_128454_("PhotoCenter")));
            int i = 0;
            for (ShadowRecord r2 : GuideService.army((ServerPlayer)p).shadows.values()) {
                if (i >= 6) break;
                Mob m = ShadowService.active(p.f_8924_, r2);
                if (m == null || m.m_9236_() != p.m_9236_() || !SoulLifeService.quiet(p, r2)) continue;
                double angle = Math.toRadians(c.m_128457_("PhotoYaw"));
                Vec3 at = center.m_82520_(-Math.sin(angle) * 4.0 + Math.cos(angle) * (double)(i * 2 - Math.max(0, c.m_128437_("PhotoMembers", 10).size() - 1)), 0.0, Math.cos(angle) * 4.0 + Math.sin(angle) * (double)(i++ * 2 - Math.max(0, c.m_128437_("PhotoMembers", 10).size() - 1)));
                m.m_21573_().m_26519_(at.f_82479_, at.f_82480_, at.f_82481_, 0.8);
                m.m_21563_().m_24960_((Entity)p, 40.0f, 40.0f);
            }
        }
    }

    public static CompoundTag publicView(ServerPlayer p) {
        OdysseyState state = GuideService.state(p);
        CompoundTag c = state.camp;
        CompoundTag out = new CompoundTag();
        for (OdysseyContent.Station station : OdysseyContent.Station.values()) {
            out.m_128379_(station.name(), OdysseyCamp.near(p, station));
        }
        for (String key : List.of("Blueprint", "BuildStep", "VisitorType", "Trades", "CustomMode", "CustomDifficulty", "CustomRule", "CycleBosses")) {
            out.m_128405_(key, c.m_128451_(key));
        }
        out.m_128379_("VisitorActive", c.m_128471_("VisitorActive"));
        out.m_128356_("CustomSeed", c.m_128454_("CustomSeed"));
        out.m_128356_("PhotoUntil", c.m_128454_("PhotoUntil"));
        if (c.m_128441_("Recommended")) {
            out.m_128405_("Recommended", c.m_128451_("Recommended"));
        }
        out.m_128405_("MaterialSteps", 86);
        out.m_128365_("Party", (Tag)c.m_128437_("Party", 10).m_6426_());
        return out;
    }
}

