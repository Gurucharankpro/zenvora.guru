/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package dev.shadowsovereign.eclipse;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class EclipseState {
    public float energy = 100.0f;
    public long lastCast;
    public long energyClock;
    public long gateAt;
    public long drinkAt;
    public long merchantCycle = -1L;
    public int wins;
    public int marks;
    public int boardWins;
    public UUID selected;
    public final EnumMap<Power, Long> ready = new EnumMap(Power.class);
    public final LinkedHashMap<UUID, Build> builds = new LinkedHashMap();
    public final NonNullList<ItemStack> bank = NonNullList.m_122780_((int)27, (Object)ItemStack.f_41583_);
    public final List<Site> sites = new ArrayList<Site>();
    public final List<CompoundTag> reports = new ArrayList<CompoundTag>();
    public final Map<UUID, Metric> metrics = new LinkedHashMap<UUID, Metric>();
    public UUID reporting;
    public long reportStart;
    public String reportKind = "";
    public final int[] purchases = new int[6];
    public int palettes = 1;
    public transient long parryUntil;
    public transient long wellUntil;
    public transient long stormUntil;
    public transient long gateUntil;
    public transient Vec3 well;
    public transient Vec3 storm;
    public transient Vec3 gateStart;
    public transient Site gateDestination;
    public final transient Map<UUID, Long> relays = new LinkedHashMap<UUID, Long>();
    public final transient Set<UUID> deployed = new HashSet<UUID>();
    public final transient Map<UUID, Long> marksUntil = new LinkedHashMap<UUID, Long>();

    public Build build(UUID id) {
        return this.builds.computeIfAbsent(id, k -> new Build());
    }

    public boolean talent(UUID id, Talent t) {
        Build b = this.builds.get(id);
        return b != null && (b.talents & 1 << t.ordinal()) != 0;
    }

    public static int veteran(long kills) {
        return (int)Math.min(5L, Math.max(0L, kills - 225L) / 50L);
    }

    public static int points(int level) {
        return (level >= 3 ? 1 : 0) + (level >= 6 ? 1 : 0) + (level >= 10 ? 1 : 0);
    }

    public CompoundTag save() {
        CompoundTag n = new CompoundTag();
        n.m_128350_("Energy", this.energy);
        n.m_128356_("LastCast", this.lastCast);
        n.m_128356_("EnergyClock", this.energyClock);
        n.m_128356_("GateAt", this.gateAt);
        n.m_128356_("DrinkAt", this.drinkAt);
        n.m_128405_("Wins", this.wins);
        n.m_128405_("Marks", this.marks);
        n.m_128405_("BoardWins", this.boardWins);
        n.m_128405_("Palettes", this.palettes);
        n.m_128356_("MerchantCycle", this.merchantCycle);
        n.m_128385_("Purchases", this.purchases);
        if (this.selected != null) {
            n.m_128362_("Selected", this.selected);
        }
        CompoundTag cd = new CompoundTag();
        this.ready.forEach((p, t) -> cd.m_128356_(p.name(), t.longValue()));
        n.m_128365_("Ready", (Tag)cd);
        ListTag bs = new ListTag();
        this.builds.forEach((id, b) -> {
            CompoundTag e = b.save();
            e.m_128362_("Id", id);
            bs.add((Object)e);
        });
        n.m_128365_("Builds", (Tag)bs);
        ListTag inv = new ListTag();
        for (int i = 0; i < 27; ++i) {
            if (((ItemStack)this.bank.get(i)).m_41619_()) continue;
            CompoundTag item = ((ItemStack)this.bank.get(i)).m_41739_(new CompoundTag());
            item.m_128405_("Slot", i);
            inv.add((Object)item);
        }
        n.m_128365_("Bank", (Tag)inv);
        ListTag ss = new ListTag();
        this.sites.forEach(s -> ss.add((Object)s.save()));
        n.m_128365_("Sites", (Tag)ss);
        ListTag rs = new ListTag();
        this.reports.forEach(r -> rs.add((Object)r.m_6426_()));
        n.m_128365_("Reports", (Tag)rs);
        if (this.reporting != null) {
            n.m_128362_("Reporting", this.reporting);
        }
        n.m_128356_("ReportStart", this.reportStart);
        n.m_128359_("ReportKind", this.reportKind);
        ListTag ms = new ListTag();
        this.metrics.forEach((id, m) -> ms.add((Object)m.save((UUID)id)));
        n.m_128365_("Metrics", (Tag)ms);
        return n;
    }

    public static EclipseState load(CompoundTag n) {
        CompoundTag x;
        EclipseState s = new EclipseState();
        s.energy = n.m_128441_("Energy") ? EclipseState.finite(n.m_128457_("Energy"), 0.0f, 100.0f) : 100.0f;
        s.lastCast = Math.max(0L, n.m_128454_("LastCast"));
        s.energyClock = Math.max(0L, n.m_128454_("EnergyClock"));
        s.gateAt = Math.max(0L, n.m_128454_("GateAt"));
        s.drinkAt = Math.max(0L, n.m_128454_("DrinkAt"));
        s.wins = Math.max(0, Math.min(1000000, n.m_128451_("Wins")));
        s.marks = Math.max(0, Math.min(1000000, n.m_128451_("Marks")));
        s.boardWins = Math.max(0, Math.min(1000000, n.m_128451_("BoardWins")));
        s.palettes = n.m_128441_("Palettes") ? n.m_128451_("Palettes") & 0x3F : 1;
        s.palettes |= 1;
        s.merchantCycle = n.m_128441_("MerchantCycle") ? n.m_128454_("MerchantCycle") : -1L;
        s.selected = n.m_128403_("Selected") ? n.m_128342_("Selected") : null;
        for (Power p : Power.values()) {
            s.ready.put(p, Math.max(0L, n.m_128469_("Ready").m_128454_(p.name())));
        }
        int[] purchases = n.m_128465_("Purchases");
        for (int i = 0; i < Math.min(6, purchases.length); ++i) {
            s.purchases[i] = Math.max(0, Math.min(3, purchases[i]));
        }
        for (Tag t : n.m_128437_("Builds", 10)) {
            CompoundTag b;
            if (s.builds.size() >= 1024 || !(b = (CompoundTag)t).m_128403_("Id")) continue;
            s.builds.put(b.m_128342_("Id"), Build.load(b));
        }
        for (Tag t : n.m_128437_("Bank", 10)) {
            CompoundTag i = (CompoundTag)t;
            int slot = i.m_128451_("Slot");
            if (slot < 0 || slot >= 27) continue;
            ItemStack item = ItemStack.m_41712_((CompoundTag)i);
            item.m_41764_(Math.min(item.m_41613_(), item.m_41741_()));
            s.bank.set(slot, (Object)item);
        }
        for (Tag t : n.m_128437_("Sites", 10)) {
            Station kind;
            if (s.sites.size() >= 64 || (kind = (Station)EclipseState.read(Station.class, (x = (CompoundTag)t).m_128461_("Kind"), null)) == null || x.m_128461_("Dimension").length() >= 256) continue;
            s.sites.add(new Site(kind, BlockPos.m_122022_((long)x.m_128454_("Pos")), x.m_128461_("Dimension")));
        }
        for (Tag t : n.m_128437_("Reports", 10)) {
            if (s.reports.size() >= 20) continue;
            s.reports.add(((CompoundTag)t).m_6426_());
        }
        if (n.m_128403_("Reporting")) {
            s.reporting = n.m_128342_("Reporting");
        }
        s.reportStart = n.m_128454_("ReportStart");
        s.reportKind = n.m_128461_("ReportKind");
        for (Tag t : n.m_128437_("Metrics", 10)) {
            if (s.metrics.size() >= 24 || !(x = (CompoundTag)t).m_128403_("Id")) continue;
            Metric m = new Metric();
            m.damage = EclipseState.finite(x.m_128459_("Damage"), 0.0, 1.0E12);
            m.healing = EclipseState.finite(x.m_128459_("Healing"), 0.0, 1.0E12);
            m.kills = Math.max(0L, x.m_128454_("Kills"));
            m.arts = Math.max(0L, x.m_128454_("Arts"));
            s.metrics.put(x.m_128342_("Id"), m);
        }
        return s;
    }

    public static <E extends Enum<E>> E read(Class<E> t, String v, E fallback) {
        try {
            return Enum.valueOf(t, v);
        }
        catch (IllegalArgumentException ex) {
            return fallback;
        }
    }

    private static float finite(float f, float a, float b) {
        return Float.isFinite(f) ? Math.max(a, Math.min(b, f)) : a;
    }

    private static double finite(double f, double a, double b) {
        return Double.isFinite(f) ? Math.max(a, Math.min(b, f)) : a;
    }

    public static enum Power {
        DASH(15, 160, 0),
        WELL(25, 360, 1),
        LANCE(20, 240, 0),
        PARRY(10, 200, 2),
        STORM(50, 600, 3);

        public final int cost;
        public final int cooldown;
        public final int wins;

        private Power(int c, int d, int w) {
            this.cost = c;
            this.cooldown = d;
            this.wins = w;
        }
    }

    public static final class Build {
        public Role role = Role.NONE;
        public int talents;
        public int palette;
        public int seal;
        public int frame;
        public int hits;
        public long procAt;
        public long lastHitAt;
        public UUID lastTarget;
        public UUID backup;
        public final ListTag[] gear = new ListTag[3];

        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128359_("Role", this.role.name());
            n.m_128405_("Talents", this.talents);
            n.m_128405_("Palette", this.palette);
            n.m_128405_("Seal", this.seal);
            n.m_128405_("Frame", this.frame);
            n.m_128356_("ProcAt", this.procAt);
            if (this.backup != null) {
                n.m_128362_("Backup", this.backup);
            }
            for (int i = 0; i < 3; ++i) {
                if (this.gear[i] == null) continue;
                n.m_128365_("Gear" + i, (Tag)this.gear[i].m_6426_());
            }
            return n;
        }

        public static Build load(CompoundTag n) {
            Build b = new Build();
            b.role = EclipseState.read(Role.class, n.m_128461_("Role"), Role.NONE);
            b.talents = n.m_128451_("Talents") & 0x1FF;
            if (Integer.bitCount(b.talents) > 3) {
                b.talents = 0;
            }
            b.palette = Math.floorMod(n.m_128451_("Palette"), 6);
            b.seal = Math.max(0, Math.min(3, n.m_128451_("Seal")));
            b.frame = Math.max(0, Math.min(2, n.m_128451_("Frame")));
            b.procAt = Math.max(0L, n.m_128454_("ProcAt"));
            b.backup = n.m_128403_("Backup") ? n.m_128342_("Backup") : null;
            for (int i = 0; i < 3; ++i) {
                ListTag list = n.m_128437_("Gear" + i, 10);
                if (list.size() != 6) continue;
                b.gear[i] = list.m_6426_();
            }
            return b;
        }
    }

    public static enum Talent {
        FLEET,
        STEADFAST,
        DEEP_BOND,
        CLEAR_FOCUS,
        QUICK_ORDERS,
        RALLY_AID,
        VIGIL_PLUS,
        LIGHTER_CRAFT,
        CONSERVATION;

    }

    public static enum Station {
        VAULT,
        QUARTERMASTER,
        ALCHEMY,
        MERCHANT,
        GALLERY,
        WAYGATE;

    }

    public record Site(Station kind, BlockPos pos, String dimension) {
        public CompoundTag save() {
            CompoundTag n = new CompoundTag();
            n.m_128359_("Kind", this.kind.name());
            n.m_128356_("Pos", this.pos.m_121878_());
            n.m_128359_("Dimension", this.dimension);
            return n;
        }
    }

    public static final class Metric {
        public double damage;
        public double healing;
        public long kills;
        public long arts;

        public CompoundTag save(UUID id) {
            CompoundTag n = new CompoundTag();
            n.m_128362_("Id", id);
            n.m_128347_("Damage", this.damage);
            n.m_128347_("Healing", this.healing);
            n.m_128356_("Kills", this.kills);
            n.m_128356_("Arts", this.arts);
            return n;
        }
    }

    public static enum Card {
        FLEET,
        IRONBARK,
        VITALITY,
        SECOND_WIND,
        NIGHT_WARD,
        MARKSMAN,
        CLEAR_MIND,
        SOULFLOW;

    }

    public static enum Mode {
        EXPEDITION,
        REACTOR,
        INVESTIGATION,
        BOARD,
        CRYSTAL,
        CHRONOPHAGE;

    }

    public static enum Role {
        NONE,
        GUARDIAN,
        STRIKER,
        ASSASSIN,
        MEDIC;

    }
}

