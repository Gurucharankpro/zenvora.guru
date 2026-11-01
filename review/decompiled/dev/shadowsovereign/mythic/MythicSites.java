/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Registry
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.StructureStart
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.mythic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.shadowsovereign.adventure.AdventureService;
import dev.shadowsovereign.monolith.MonolithWorld;
import dev.shadowsovereign.mythic.BelfryNests;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.mythic.MythicService;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.LivingRealm;
import dev.shadowsovereign.shattered.ShatteredSecrets;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

public final class MythicSites {
    private static final List<Site> SITES = MythicSites.read();

    public static List<Site> sites() {
        return SITES;
    }

    public static ResourceKey<Level> dimension(Site site) {
        return OdysseyRealm.KEY;
    }

    private static BlockPos pos(JsonArray a) {
        return new BlockPos(a.get(0).getAsInt(), a.get(1).getAsInt(), a.get(2).getAsInt());
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static List<Site> read() {
        try (InputStream in = MythicSites.class.getResourceAsStream("/data/shadowsovereign/mythic_sites.json");){
            List list;
            try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);){
                ArrayList<Site> out = new ArrayList<Site>();
                HashSet<Integer> kinds = new HashSet<Integer>();
                for (JsonElement el : JsonParser.parseReader((Reader)reader).getAsJsonArray()) {
                    JsonObject o = el.getAsJsonObject();
                    int kind = o.get("kind").getAsInt();
                    if (kind < -1 || kind >= MythicExpedition.IDS.length || !kinds.add(kind)) {
                        throw new IllegalStateException("Duplicate/invalid mythic habitat " + kind);
                    }
                    out.add(new Site(kind, new ResourceLocation("shadowsovereign", o.get("structure").getAsString()), MythicSites.pos(o.getAsJsonArray("altar")), MythicSites.pos(o.getAsJsonArray("rootOffset"))));
                }
                if (out.size() != MythicExpedition.IDS.length + 1) {
                    throw new IllegalStateException("Incomplete mythic habitat catalogue");
                }
                list = List.copyOf(out);
            }
            return list;
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot load mythic habitats", e);
        }
    }

    public static BlockPos altar(StructureStart start, Site site) {
        Object e;
        if (start.m_73602_().isEmpty() || !((e = start.m_73602_().get(0)) instanceof PoolElementStructurePiece)) {
            return null;
        }
        PoolElementStructurePiece piece = (PoolElementStructurePiece)e;
        return StructureTemplate.m_74593_((BlockPos)site.altar().m_121996_((Vec3i)site.rootOffset()), (Mirror)Mirror.NONE, (Rotation)piece.m_6830_(), (BlockPos)BlockPos.f_121853_).m_121955_((Vec3i)piece.m_72646_());
    }

    private static void remember(CompoundTag n, StructureStart start, BlockPos at) {
        BoundingBox box = start.m_73601_();
        n.m_128356_("NaturalAltar", at.m_121878_());
        n.m_128385_("NaturalBounds", new int[]{box.m_162395_(), box.m_162396_(), box.m_162398_(), box.m_162399_(), box.m_162400_(), box.m_162401_()});
    }

    public static BoundingBox bounds(CompoundTag n, BlockPos at) {
        if (!n.m_128441_("NaturalAltar") || n.m_128454_("NaturalAltar") != at.m_121878_()) {
            return null;
        }
        int[] b = n.m_128465_("NaturalBounds");
        return b.length == 6 && b[0] <= b[3] && b[1] <= b[4] && b[2] <= b[5] ? new BoundingBox(b[0], b[1], b[2], b[3], b[4], b[5]) : null;
    }

    public static boolean protectedSite(ServerLevel w, BlockPos at) {
        if (!w.m_46472_().equals(OdysseyRealm.KEY)) {
            return false;
        }
        for (Site site : SITES) {
            StructureStart start;
            Structure structure;
            if (!MythicSites.dimension(site).equals((Object)w.m_46472_()) || site.kind() > 2 && site.kind() < 23 || (structure = (Structure)w.m_9598_().m_175515_(Registries.f_256944_).m_7745_(site.structure())) == null || !(start = w.m_215010_().m_220494_(at, structure)).m_73603_() || ShatteredSecrets.openable(w, at, site.structure(), start)) continue;
            return true;
        }
        return false;
    }

    public static void tick(ServerPlayer p) {
        if (p.f_19797_ % 20 != 0 || !OdysseyRealm.inside(p) || p.m_5833_() || !p.m_6084_() || AdventureService.journal((ServerPlayer)p).session != null || OdysseyRealm.foreignPlot(p, p.m_20183_())) {
            return;
        }
        ServerLevel w = p.m_284548_();
        Registry registry = w.m_9598_().m_175515_(Registries.f_256944_);
        for (Site site : SITES) {
            BlockPos at;
            StructureStart start;
            Structure structure;
            if (!MythicSites.dimension(site).equals((Object)w.m_46472_()) || (site.kind() == -1 ? MythicService.state((ServerPlayer)p).status : MythicExpedition.state(p, site.kind()).m_128451_("Status")) == 2 || (structure = (Structure)registry.m_7745_(site.structure())) == null || !(start = w.m_215010_().m_220494_(p.m_20183_(), structure)).m_73603_() || (at = MythicSites.altar(start, site)) == null || p.m_20238_(Vec3.m_82512_((Vec3i)at)) >= 484.0) continue;
            if (site.kind() == -1) {
                MythicSites.remember(MythicService.state((ServerPlayer)p).gloom, start, at);
                MythicService.state((ServerPlayer)p).gloom.m_128356_("NaturalDevourer", at.m_7495_().m_121878_());
                MythicService.start(p);
            } else {
                if (site.kind() == 20) {
                    MonolithWorld.Site authored = MonolithWorld.sites().stream().filter(v -> v.kind() == 20).findFirst().orElseThrow();
                    BelfryNests.visit(p, authored, start, null, at);
                }
                MythicSites.remember(MythicExpedition.state(p, site.kind()), start, at);
                MythicExpedition.state(p, site.kind()).m_128356_("Lair", at.m_121878_());
                MythicExpedition.start(p, site.kind());
            }
            LivingRealm.dirty(p);
            return;
        }
    }

    public record Site(int kind, ResourceLocation structure, BlockPos altar, BlockPos rootOffset) {
    }
}

