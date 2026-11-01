/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerBossEvent
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.BossEvent$BossBarOverlay
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.projectile.Snowball
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 */
package dev.shadowsovereign.odyssey;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.adventure.AdventureWorld;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyCatalog;
import dev.shadowsovereign.odyssey.OdysseyContent;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.odyssey.OdysseyRuns;
import dev.shadowsovereign.odyssey.OdysseyState;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.odyssey.OdysseyTrials;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class OdysseyBosses {
    private static final Map<UUID, ServerBossEvent> bars = new HashMap<UUID, ServerBossEvent>();

    public static void clearBar(UUID id) {
        ServerBossEvent bar = bars.remove(id);
        if (bar != null) {
            bar.m_7706_();
        }
    }

    public static void clearBars() {
        bars.values().forEach(ServerBossEvent::m_7706_);
        bars.clear();
    }

    private static int bossIndex(AdventureData.Session s) {
        return OdysseyRuns.mode(s).ordinal() - 10;
    }

    public static void setup(ServerPlayer p, AdventureData.Session s) {
        Entity entity;
        CompoundTag n = s.journey;
        int index = OdysseyBosses.bossIndex(s);
        EntityType type = switch (index) {
            case 0 -> EntityType.f_20513_;
            case 1 -> EntityType.f_20512_;
            case 2 -> EntityType.f_20493_;
            case 3 -> EntityType.f_20554_;
            case 4 -> EntityType.f_20524_;
            default -> EntityType.f_20497_;
        };
        float hp = (float)((double)(index == 5 ? 220 : 140) * (n.m_128451_("Difficulty") == 0 ? 0.75 : (n.m_128451_("Difficulty") == 2 ? 1.25 : 1.0)) * (1.0 + (double)Math.min(3, GuideService.state((ServerPlayer)p).cycle) * 0.15));
        AdventureData.Npc boss = OdysseyRuns.add(p, s, (EntityType<? extends Mob>)type, AdventureData.Role.ENEMY, OdysseyRuns.at(s, 0, 1, -10), hp);
        n.m_128362_("Boss", boss.id);
        n.m_128350_("BossMax", hp);
        n.m_128405_("Phase", 0);
        n.m_128405_("NextCast", 80);
        if (boss.active != null && (entity = p.m_284548_().m_8791_(boss.active)) instanceof Mob) {
            Mob m = (Mob)entity;
            m.m_6593_((Component)OdysseyText.c("feature." + (index + 16), new Object[0]));
            m.m_20340_(true);
            if (index == 0) {
                m.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.f_42717_));
            }
            if (index == 4) {
                m.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.f_42411_));
            }
            boss.snapshot = AdventureWorld.snapshot(m);
        }
        if (index == 4) {
            for (int i = 0; i < 2; ++i) {
                Entity entity2;
                AdventureData.Npc voice = OdysseyRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20524_, AdventureData.Role.ENEMY, OdysseyRuns.at(s, -10 + i * 20, 1, -10), hp);
                if (voice.active != null && (entity2 = p.m_284548_().m_8791_(voice.active)) instanceof Mob) {
                    Mob m = (Mob)entity2;
                    m.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.f_42411_));
                    voice.snapshot = AdventureWorld.snapshot(m);
                }
                n.m_128362_("Voice." + (i + 1), voice.id);
            }
            n.m_128362_("Voice.0", boss.id);
        }
        if (index == 5) {
            int[] masks = new int[]{0, 1, 2, 3, 4};
            Random random = new Random(n.m_128454_("Seed"));
            for (int i = masks.length - 1; i > 0; --i) {
                int j = random.nextInt(i + 1);
                int old = masks[i];
                masks[i] = masks[j];
                masks[j] = old;
            }
            n.m_128385_("Masks", Arrays.copyOf(masks, 3));
        }
        OdysseyBosses.phase(p, s);
        OdysseyBosses.bar(p, s);
    }

    private static ServerBossEvent bar(ServerPlayer p, AdventureData.Session s) {
        ServerBossEvent bar = bars.computeIfAbsent(s.id, id -> new ServerBossEvent((Component)OdysseyText.c("feature." + (OdysseyBosses.bossIndex(s) + 16), new Object[0]), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS));
        bar.m_6543_(p);
        return bar;
    }

    private static Mob boss(ServerPlayer p, AdventureData.Session s) {
        Object object;
        AdventureData.Npc n = s.npcs.get(s.journey.m_128342_("Boss"));
        if (n != null && n.active != null && (object = p.m_284548_().m_8791_(n.active)) instanceof Mob) {
            Mob m = (Mob)object;
            return m;
        }
        if (OdysseyBosses.bossIndex(s) == 4) {
            for (AdventureData.Npc npc : s.npcs.values()) {
                Entity entity;
                if (npc.active == null || !((entity = p.m_284548_().m_8791_(npc.active)) instanceof Mob)) continue;
                Mob m = (Mob)entity;
                return m;
            }
        }
        return null;
    }

    public static int mask(AdventureData.Session s) {
        int index = OdysseyBosses.bossIndex(s);
        if (index != 5) {
            return index;
        }
        int[] masks = s.journey.m_128465_("Masks");
        return masks.length == 3 ? Math.max(0, Math.min(4, masks[Math.max(0, Math.min(2, s.journey.m_128451_("Phase")))])) : 0;
    }

    private static void phase(ServerPlayer p, AdventureData.Session s) {
        int i;
        CompoundTag n = s.journey;
        int mask = OdysseyBosses.mask(s);
        n.m_128405_("Runes", 0);
        n.m_128405_("Paid", 0);
        n.m_128405_("Cut", 0);
        n.m_128405_("Leader", 0);
        if (mask == 0 || mask == 3) {
            for (i = 0; i < (mask == 0 ? 3 : 4); ++i) {
                BlockPos at = OdysseyTrials.marker(s, i * 2);
                p.m_9236_().m_7731_(at, (mask == 0 ? Blocks.f_50729_ : Blocks.f_152490_).m_49966_(), 3);
            }
        }
        if (mask == 4 && OdysseyBosses.bossIndex(s) == 5) {
            for (i = 0; i < 2; ++i) {
                AdventureData.Npc voice = OdysseyRuns.add(p, s, (EntityType<? extends Mob>)EntityType.f_20524_, AdventureData.Role.ENEMY, OdysseyRuns.at(s, -8 + i * 16, 1, -14), Math.max(20.0f, n.m_128457_("BossMax") / 4.0f));
                n.m_128362_("MaskVoice." + i, voice.id);
            }
        }
        if (mask == 2) {
            for (i = 0; i < 6; ++i) {
                OdysseyRuns.token(p, s, OdysseyTrials.marker(s, i), n.m_128451_("Phase") * 10 + i);
            }
        }
        OdysseyText.tell(p, "boss_phase", n.m_128451_("Phase") + 1, OdysseyText.c("mask." + mask, new Object[0]));
    }

    public static void damage(LivingHurtEvent e, ServerPlayer p, AdventureData.Session s) {
        boolean protectedPhase;
        Mob m;
        LivingEntity livingEntity = e.getEntity();
        if (!(livingEntity instanceof Mob) || AdventureWorld.session((Entity)(m = (Mob)livingEntity)) != s) {
            return;
        }
        CompoundTag n = s.journey;
        int mask = OdysseyBosses.mask(s);
        boolean bl = protectedPhase = mask == 0 && n.m_128451_("Runes") != 7 || mask == 2 && n.m_128451_("Paid") < 3 || mask == 3 && n.m_128451_("Cut") != 15;
        if (OdysseyBosses.bossIndex(s) == 4) {
            UUID expected = null;
            for (int i = 0; i < 3; ++i) {
                UUID id;
                UUID uUID = id = n.m_128403_("Voice." + i) ? n.m_128342_("Voice." + i) : null;
                if (id == null || !s.npcs.containsKey(id)) continue;
                expected = id;
                break;
            }
            if (expected != null && !expected.equals(m.getPersistentData().m_128342_("SoulboundAdventureNpc"))) {
                protectedPhase = true;
            }
        }
        if (mask == 4 && OdysseyBosses.bossIndex(s) == 5) {
            for (int i = 0; i < 2; ++i) {
                UUID id;
                UUID uUID = id = n.m_128403_("MaskVoice." + i) ? n.m_128342_("MaskVoice." + i) : null;
                if (id == null || !s.npcs.containsKey(id)) continue;
                if (id.equals(m.getPersistentData().m_128342_("SoulboundAdventureNpc"))) break;
                protectedPhase = true;
                break;
            }
        }
        if (protectedPhase) {
            e.setCanceled(true);
            return;
        }
        e.setAmount(Math.min(e.getAmount(), m.m_21233_() * 0.18f));
    }

    public static void tick(ServerPlayer p, AdventureData.Session s) {
        List<LivingEntity> candidates;
        BlockPos at;
        int phase;
        CompoundTag n = s.journey;
        Mob m = OdysseyBosses.boss(p, s);
        if (m == null) {
            if (s.npcs.values().stream().noneMatch(v -> v.role == AdventureData.Role.ENEMY)) {
                OdysseyBosses.clearBar(s.id);
                OdysseyRuns.win(p, s);
            }
            return;
        }
        int n2 = m.m_21223_() <= m.m_21233_() / 3.0f ? 2 : (phase = m.m_21223_() <= m.m_21233_() * 2.0f / 3.0f ? 1 : 0);
        if (OdysseyBosses.bossIndex(s) != 4 && phase > n.m_128451_("Phase")) {
            n.m_128405_("Phase", phase);
            OdysseyBosses.phase(p, s);
        }
        ServerBossEvent bar = OdysseyBosses.bar(p, s);
        bar.m_142711_(Math.max(0.0f, Math.min(1.0f, m.m_21223_() / m.m_21233_())));
        int t = n.m_128451_("Elapsed");
        int mask = OdysseyBosses.mask(s);
        if (OdysseyBosses.bossIndex(s) == 4 && t % 20 == 0) {
            p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123758_, m.m_20185_(), m.m_20186_() + 2.0, m.m_20189_(), 2, 0.2, 0.2, 0.2, 0.01);
        }
        if (t == n.m_128451_("CastAt") && n.m_128471_("Casting")) {
            n.m_128379_("Casting", false);
            Vec3 point = new Vec3(n.m_128459_("CastX"), (double)(s.anchor.m_123342_() + 1), n.m_128459_("CastZ"));
            if (mask == 1) {
                for (int i = -1; i <= 1; ++i) {
                    Snowball snow = new Snowball(p.m_9236_(), (LivingEntity)m);
                    snow.m_37446_(new ItemStack((ItemLike)(i == 0 ? Items.f_42674_ : Items.f_42619_)));
                    Vec3 delta = point.m_82520_((double)(i * 2), 1.0, 0.0).m_82546_(m.m_146892_());
                    snow.m_6686_(delta.f_82479_, delta.f_82480_, delta.f_82481_, 1.0f, 1.0f);
                    snow.getPersistentData().m_128362_("OdysseyScene", s.id);
                    snow.getPersistentData().m_128362_("OdysseyChef", m.m_20148_());
                    p.m_284548_().m_7967_((Entity)snow);
                    OdysseyRuns.prop(s, (Entity)snow);
                }
            } else {
                for (LivingEntity target : OdysseyBosses.targets(p, s)) {
                    if (!(target.m_20238_(point) < 9.0)) continue;
                    target.m_6469_(target.m_269291_().m_269104_((Entity)m, (Entity)m), (float)(3 + n.m_128451_("Difficulty") + Math.min(3, GuideService.state((ServerPlayer)p).cycle)));
                }
                p.m_284548_().m_8767_((ParticleOptions)(mask == 3 ? ParticleTypes.f_175833_ : ParticleTypes.f_123746_), point.f_82479_, point.f_82480_, point.f_82481_, 12, 1.0, 0.2, 1.0, 0.03);
                if (mask == 3) {
                    for (int dx = -1; dx <= 1; ++dx) {
                        for (int dz = -1; dz <= 1; ++dz) {
                            at = BlockPos.m_274446_((Position)point).m_7918_(dx, 0, dz);
                            if (!p.m_9236_().m_8055_(at).m_60795_()) continue;
                            p.m_9236_().m_7731_(at, Blocks.f_50033_.m_49966_(), 3);
                            ListTag webs = n.m_128437_("Webs", 10);
                            CompoundTag row = new CompoundTag();
                            row.m_128356_("Pos", at.m_121878_());
                            row.m_128405_("Until", t + 100);
                            webs.add((Object)row);
                            n.m_128365_("Webs", (Tag)webs);
                        }
                    }
                }
            }
        }
        if (t >= n.m_128451_("NextCast") && !n.m_128471_("Casting") && !(candidates = OdysseyBosses.targets(p, s)).isEmpty()) {
            LivingEntity target = candidates.get(Math.floorMod(t / 80, candidates.size()));
            n.m_128347_("CastX", target.m_20185_());
            n.m_128347_("CastZ", target.m_20189_());
            n.m_128405_("CastAt", t + 30);
            n.m_128379_("Casting", true);
            n.m_128405_("NextCast", t + 100);
            OdysseyText.tell(p, "attack_warning", OdysseyText.c("mask." + mask, new Object[0]));
        }
        if (n.m_128471_("Casting") && t % 5 == 0) {
            double x = n.m_128459_("CastX");
            double z = n.m_128459_("CastZ");
            for (int i = 0; i < 12; ++i) {
                double angle = (double)i * Math.PI / 6.0;
                p.m_284548_().m_8767_((ParticleOptions)ParticleTypes.f_123745_, x + Math.cos(angle) * 3.0, (double)s.anchor.m_123342_() + 1.1, z + Math.sin(angle) * 3.0, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
        ListTag webs = n.m_128437_("Webs", 10);
        for (int i = webs.size() - 1; i >= 0; --i) {
            CompoundTag web = webs.m_128728_(i);
            if (t < web.m_128451_("Until")) continue;
            at = BlockPos.m_122022_((long)web.m_128454_("Pos"));
            if (p.m_9236_().m_8055_(at).m_60713_(Blocks.f_50033_)) {
                p.m_9236_().m_7731_(at, Blocks.f_50016_.m_49966_(), 3);
            }
            webs.remove(i);
        }
        n.m_128365_("Webs", (Tag)webs);
    }

    private static List<LivingEntity> targets(ServerPlayer p, AdventureData.Session s) {
        ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
        if (p.m_6084_()) {
            list.add((LivingEntity)p);
        }
        list.addAll(OdysseyRuns.allies(p, s));
        return list;
    }

    public static boolean input(ServerPlayer p, AdventureData.Session s, int action, int value) {
        if (action != 0) {
            return false;
        }
        CompoundTag n = s.journey;
        int mask = OdysseyBosses.mask(s);
        if (mask == 0 || mask == 3) {
            String key;
            int count;
            int n2 = count = mask == 0 ? 3 : 4;
            if (value < 0 || value >= count || p.m_20238_(Vec3.m_82512_((Vec3i)OdysseyTrials.marker(s, value * 2))) > 16.0) {
                return false;
            }
            String string = key = mask == 0 ? "Runes" : "Cut";
            if ((n.m_128451_(key) & 1 << value) != 0) {
                return false;
            }
            if (mask == 3 && !p.m_21205_().m_204117_(ItemTags.f_271388_) && !p.m_21205_().m_204117_(ItemTags.f_271207_)) {
                return false;
            }
            n.m_128405_(key, n.m_128451_(key) | 1 << value);
            p.m_9236_().m_7731_(OdysseyTrials.marker(s, value * 2), Blocks.f_50386_.m_49966_(), 3);
            if (mask == 3) {
                p.m_21205_().m_41622_(1, (LivingEntity)p, user -> user.m_21190_(InteractionHand.MAIN_HAND));
            }
            return true;
        }
        if (mask == 2 && p.m_20238_(Vec3.m_82512_((Vec3i)OdysseyTrials.marker(s, 5))) <= 16.0) {
            for (ItemStack stack : p.m_150109_().f_35974_) {
                if (!stack.m_150930_((Item)OdysseyContent.TOKEN.get()) || !stack.m_41782_() || !stack.m_41783_().m_128403_("OdysseySession") || !s.id.equals(stack.m_41783_().m_128342_("OdysseySession"))) continue;
                int needed = Math.max(0, 3 - n.m_128451_("Paid"));
                int paid = Math.min(needed, stack.m_41613_());
                if (paid == 0) {
                    return false;
                }
                stack.m_41774_(paid);
                n.m_128405_("Paid", n.m_128451_("Paid") + paid);
                p.m_150109_().m_6596_();
                return true;
            }
        }
        return false;
    }

    public static boolean coronate(ServerPlayer p) {
        GuideService.importEvidence(p);
        OdysseyState state = GuideService.state(p);
        if (state.ended || !OdysseyRealm.camp(p, p.m_20183_()) || OdysseyCatalog.goals().stream().filter(g -> !g.id().equals("ending")).anyMatch(g -> !GuideService.done(p, g))) {
            return false;
        }
        if (!OdysseyRuns.start(p, OdysseyCatalog.Mode.PARADE.ordinal(), 0, List.of())) {
            return false;
        }
        state.camp.m_128379_("CoronationPending", true);
        GuideService.dirty(p);
        return true;
    }

    public static boolean newCycle(ServerPlayer p) {
        OdysseyState state = GuideService.state(p);
        if (!state.ended || AdventureService.journal((ServerPlayer)p).session != null || !OdysseyRealm.camp(p, p.m_20183_())) {
            return false;
        }
        state.cycle = Math.min(1000000, state.cycle + 1);
        state.camp.m_128405_("CycleBosses", 0);
        GuideService.dirty(p);
        return true;
    }
}

