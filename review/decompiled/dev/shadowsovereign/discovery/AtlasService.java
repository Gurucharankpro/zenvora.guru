/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.levelgen.structure.Structure
 */
package dev.shadowsovereign.discovery;

import com.mojang.datafixers.util.Pair;
import dev.shadowsovereign.expanse.ExpanseContent;
import dev.shadowsovereign.mastery.MasteryService;
import dev.shadowsovereign.mythic.MythicSites;
import dev.shadowsovereign.network.AtlasNetwork;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class AtlasService {
    public static ItemStack held(ServerPlayer p) {
        if (p.m_21205_().m_150930_((Item)ExpanseContent.COMPASS.get())) {
            return p.m_21205_();
        }
        return p.m_21206_().m_150930_((Item)ExpanseContent.COMPASS.get()) ? p.m_21206_() : ItemStack.f_41583_;
    }

    public static void open(ServerPlayer p) {
        if (AtlasService.held(p).m_41619_()) {
            return;
        }
        CompoundTag n = p.getPersistentData();
        n.m_128362_("SoulAtlasSession", UUID.randomUUID());
        n.m_128356_("SoulAtlasUntil", p.f_8924_.m_129783_().m_46467_() + 12000L);
        AtlasService.send(p, true, "welcome");
    }

    public static boolean authorized(ServerPlayer p, UUID session) {
        CompoundTag n = p.getPersistentData();
        return p.m_6084_() && !AtlasService.held(p).m_41619_() && session != null && n.m_128403_("SoulAtlasSession") && session.equals(n.m_128342_("SoulAtlasSession")) && p.f_8924_.m_129783_().m_46467_() <= n.m_128454_("SoulAtlasUntil");
    }

    public static void request(ServerPlayer p, UUID session, int action, String id) {
        BlockPos at;
        Pair found;
        if (!AtlasService.authorized(p, session)) {
            return;
        }
        long now = p.f_8924_.m_129783_().m_46467_();
        CompoundTag n = p.getPersistentData();
        if (n.m_128441_("SoulAtlasAction") && now - n.m_128454_("SoulAtlasAction") < 4L) {
            return;
        }
        n.m_128356_("SoulAtlasAction", now);
        if (action == 0) {
            AtlasService.send(p, false, "welcome");
            return;
        }
        ItemStack compass = AtlasService.held(p);
        if (action == 2) {
            CompoundTag tag = compass.m_41784_();
            for (String key : List.of("LodestonePos", "LodestoneDimension", "LodestoneTracked", "AtlasTarget")) {
                tag.m_128473_(key);
            }
            AtlasService.send(p, false, "cleared");
            return;
        }
        if (action != 1) {
            return;
        }
        ResourceLocation key = ResourceLocation.m_135820_((String)id);
        Registry registry = p.m_284548_().m_9598_().m_175515_(Registries.f_256944_);
        if (key == null || !key.m_135827_().equals("shadowsovereign") || !registry.m_7804_(key)) {
            return;
        }
        ResourceKey<Level> destination = MythicSites.sites().stream().filter(s -> s.structure().equals((Object)key)).findFirst().map(MythicSites::dimension).orElse(OdysseyRealm.KEY);
        if (!p.m_9236_().m_46472_().equals(destination)) {
            AtlasService.send(p, false, "dimension");
            return;
        }
        if (!p.f_8924_.m_129910_().m_246337_().m_247749_()) {
            AtlasService.send(p, false, "disabled");
            return;
        }
        if (n.m_128441_("SoulAtlasLocate") && now - n.m_128454_("SoulAtlasLocate") < 200L) {
            AtlasService.send(p, false, "cooldown");
            return;
        }
        n.m_128356_("SoulAtlasLocate", now);
        Holder.Reference holder = registry.m_246971_(ResourceKey.m_135785_((ResourceKey)Registries.f_256944_, (ResourceLocation)key));
        Pair pair = found = key.equals(null) ? null : p.m_284548_().m_7726_().m_8481_().m_223037_(p.m_284548_(), (HolderSet)HolderSet.m_205809_((Holder[])new Holder[]{holder}), p.m_20183_(), 32, false);
        BlockPos blockPos = key.equals(null) ? null : (at = found == null ? null : (BlockPos)found.getFirst());
        if (at == null || OdysseyRealm.inside(p) && OdysseyRealm.foreignPlot(p, at)) {
            AtlasService.send(p, false, "missing");
            return;
        }
        CompoundTag tag = compass.m_41784_();
        tag.m_128365_("LodestonePos", (Tag)NbtUtils.m_129224_((BlockPos)at));
        tag.m_128359_("LodestoneDimension", destination.m_135782_().toString());
        tag.m_128379_("LodestoneTracked", false);
        tag.m_128359_("AtlasTarget", id);
        MasteryService.located(p, id);
        AtlasService.send(p, false, "found");
    }

    public static CompoundTag catalogue(ServerPlayer p) {
        CompoundTag t;
        CompoundTag out = new CompoundTag();
        ListTag rows = new ListTag();
        Registry registry = p.m_284548_().m_9598_().m_175515_(Registries.f_256944_);
        HashMap<ResourceLocation, Integer> myths = new HashMap<ResourceLocation, Integer>();
        for (MythicSites.Site s : MythicSites.sites()) {
            myths.put(s.structure(), s.kind());
        }
        registry.m_6566_().stream().filter(k -> k.m_135827_().equals("shadowsovereign")).sorted().limit(512L).forEach(k -> {
            CompoundTag row = new CompoundTag();
            row.m_128359_("Id", k.toString());
            row.m_128379_("Mythic", myths.containsKey(k));
            row.m_128359_("Dimension", k.equals(null) ? Level.f_46428_.m_135782_().toString() : OdysseyRealm.KEY.m_135782_().toString());
            ListTag biomes = new ListTag();
            ((Structure)registry.m_7745_(k)).m_226559_().m_203614_().forEach(h -> h.m_203543_().ifPresent(b -> biomes.add((Object)StringTag.m_129297_((String)b.m_135782_().toString()))));
            row.m_128365_("Biomes", (Tag)biomes);
            rows.add((Object)row);
        });
        out.m_128365_("Sites", (Tag)rows);
        out.m_128379_("Inside", OdysseyRealm.inside(p));
        out.m_128359_("CurrentDimension", p.m_9236_().m_46472_().m_135782_().toString());
        out.m_128356_("Origin", p.m_20183_().m_121878_());
        ItemStack stack = AtlasService.held(p);
        if (stack.m_41782_() && (t = stack.m_41783_()).m_128425_("LodestonePos", 10)) {
            out.m_128356_("Target", NbtUtils.m_129239_((CompoundTag)t.m_128469_("LodestonePos")).m_121878_());
            out.m_128359_("TargetId", t.m_128461_("AtlasTarget"));
        }
        return out;
    }

    private static void send(ServerPlayer p, boolean open, String message) {
        CompoundTag out = AtlasService.catalogue(p);
        out.m_128362_("Session", p.getPersistentData().m_128342_("SoulAtlasSession"));
        out.m_128359_("Message", message);
        AtlasNetwork.view(p, out, open);
    }
}

