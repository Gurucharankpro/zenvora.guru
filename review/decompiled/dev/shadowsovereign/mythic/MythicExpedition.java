/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerBossEvent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.BossEvent$BossBarOverlay
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.level.Level
 */
package dev.shadowsovereign.mythic;

import dev.shadowsovereign.adventure.AdventureData;
import dev.shadowsovereign.expansion.OpenMythics;
import dev.shadowsovereign.expansion.X1Hooks;
import dev.shadowsovereign.gloom.GloomRealm;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicContent;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.mythic.MythicSites;
import dev.shadowsovereign.mythic.MythicSpawn;
import dev.shadowsovereign.mythic.MythicState;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.vigil.VigilCrown;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public final class MythicExpedition {
    public static final String[] IDS;

    public static CompoundTag state(ServerPlayer p, int kind) {
        return MythicExpedition.state(MythicService.state(p), kind);
    }

    private static CompoundTag state(MythicState s, int kind) {
        String key = IDS[kind];
        if (!s.gloom.m_128425_(key, 10)) {
            s.gloom.m_128365_(key, (Tag)new CompoundTag());
        }
        return s.gloom.m_128469_(key);
    }

    public static int current(AdventureData.Session s) {
        return s != null && s.kind == AdventureData.Kind.MYTHIC_LAIR && s.journey.m_128441_("MythicKind") ? s.journey.m_128451_("MythicKind") : -1;
    }

    public static MythicBeast body(ServerPlayer p, int kind) {
        MythicBeast m;
        Entity entity;
        CompoundTag n = MythicExpedition.state(p, kind);
        ServerLevel w = p.f_8924_.m_129880_(OdysseyRealm.KEY);
        return w != null && n.m_128403_("Active") && (entity = w.m_8791_(n.m_128342_("Active"))) instanceof MythicBeast ? (m = (MythicBeast)entity) : null;
    }

    public static boolean near(ServerPlayer p, int kind, int radius) {
        return OdysseyRealm.inside(p) && p.m_20183_().m_123331_((Vec3i)GloomRealm.habitat(p, kind)) <= (double)(radius * radius);
    }

    public static boolean start(ServerPlayer serverPlayer, int n) {
        return OpenMythics.start(serverPlayer, n);
    }

    public static boolean spawn(ServerPlayer p, int kind) {
        CompoundTag n = MythicExpedition.state(p, kind);
        BlockPos at = GloomRealm.habitat(p, kind);
        ServerLevel w = p.m_284548_();
        if (!w.m_143340_(at)) {
            return false;
        }
        if (MythicExpedition.body(p, kind) != null) {
            X1Hooks.emit((Entity)MythicExpedition.body(p, kind), 14);
            return true;
        }
        if (n.m_128403_("Active")) {
            return false;
        }
        MythicBeast m = (MythicBeast)((EntityType)MythicContent.BEASTS.get(kind).get()).m_20615_((Level)w);
        if (m == null) {
            return false;
        }
        if (n.m_128441_("Snapshot")) {
            m.m_20258_(n.m_128469_("Snapshot").m_6426_());
        }
        m.m_20084_(UUID.randomUUID());
        boolean fits = MythicSpawn.place(w, (Mob)m, at, MythicSites.bounds(n, at));
        if (!fits) {
            m.m_146870_();
            return false;
        }
        m.getPersistentData().m_128362_("SoulboundMythicHost", p.m_20148_());
        m.getPersistentData().m_128379_("SoulboundWildBoss", true);
        m.getPersistentData().m_128347_("SoulboundBossAttack", m.m_21133_(Attributes.f_22281_));
        m.m_6593_((Component)Component.m_237115_((String)("entity.shadowsovereign." + IDS[kind])));
        m.m_20340_(true);
        n.m_128362_("Active", m.m_20148_());
        if (!w.m_7967_((Entity)m) || w.m_8791_(m.m_20148_()) != m) {
            m.m_146870_();
            n.m_128473_("Active");
            return false;
        }
        MythicState st = MythicService.state(p);
        if (st.bar != null) {
            st.bar.m_7706_();
        }
        st.bar = new ServerBossEvent(m.m_7755_(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
        st.bar.m_6543_(p);
        m.animate(1);
        GloomRealm.dirty(p);
        X1Hooks.emit((Entity)MythicExpedition.body(p, kind), 14);
        return true;
    }

    public static void pause(MinecraftServer minecraftServer, UUID uUID) {
        OpenMythics.pause(minecraftServer, uUID);
    }

    public static boolean resume(ServerPlayer p) {
        AdventureData.Session s = MythicService.session(p);
        int kind = MythicExpedition.current(s);
        if (kind < 0 || s.phase != AdventureData.Phase.PAUSED || !MythicExpedition.near(p, kind, 30) || !p.m_6084_() || p.m_5833_() || p.m_9236_().m_46791_() == Difficulty.PEACEFUL || VigilCrown.active(p)) {
            return false;
        }
        s.phase = AdventureData.Phase.ACTIVE;
        if (!MythicExpedition.spawn(p, kind)) {
            s.phase = AdventureData.Phase.PAUSED;
            return false;
        }
        return true;
    }

    public static boolean defeated(MythicBeast mythicBeast, UUID uUID) {
        return OpenMythics.defeated(mythicBeast, uUID);
    }

    public static void tick(ServerPlayer serverPlayer) {
        OpenMythics.fallback(serverPlayer);
    }

    static {
        String[] stringArray = Arrays.copyOf(new String[]{"umbral_wyngcat", "crimson_basilisk", "eclipse_bloom", "ruin_stalker", "weeping_puppeteer", "forgotten_astronomer", "crownless_sovereign", "eclipse_stag", "reliquary_crawler", "infernal_phoenix", "pain", "grieving_seraph", "ashen_juggernaut", "paper_kunoichi", "hollow_jester", "chained_emperor", "madara_uchiha", "sasori", "fallen_penitent", "rainbound_oracle", "dread_bellkeeper", "blood_moon_monk", "itachi_uchiha", "deidara", "veiled_whorl", "mad_farmer", "plague_doctor", "cursed_executioner", "void_weaver", "azure_skyling", "gentleman_overlord", "forsaken_fawn", "lonely_red_panda", "cursed_puppet_maker", "removed_companion", "frostbound_snow_leopard", "the_last_lullaby", "refai_el_desouky"}, 52);
        stringArray[38] = "mokhtar_el_desouky";
        stringArray[39] = "broken_star_owlet";
        stringArray[40] = "essam_el_nemr";
        stringArray[41] = "lumen_cub";
        stringArray[42] = "pearl_drakeling";
        stringArray[43] = "shattered_pangolin";
        stringArray[44] = "cursed_bell_ringer";
        stringArray[45] = "weeping_axolotl";
        stringArray[46] = "legendary_giant_zombie_refai";
        stringArray[47] = "amin_el_nemr";
        stringArray[48] = "king_of_corny_memes";
        stringArray[49] = "the_69_pose";
        stringArray[50] = "hanan";
        stringArray[51] = "glass_ronin";
        String[] stringArray2 = stringArray;
        System.arraycopy(stringArray2, 35, stringArray2, 34, 17);
        IDS = Arrays.copyOf(stringArray2, 51);
    }
}

