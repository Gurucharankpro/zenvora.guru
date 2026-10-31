package dev.shadowsovereign.expansion;

import dev.shadowsovereign.adventure.*;
import dev.shadowsovereign.army.*;
import dev.shadowsovereign.gloom.GloomRealm;
import dev.shadowsovereign.mythic.*;
import dev.shadowsovereign.network.MythicNetwork;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import dev.shadowsovereign.realm.WildBosses;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import java.util.*;

/** Hosted bosses retain their per-creature UUID and acquisition record, but
 * no longer occupy the owner's exclusive physical-adventure session. */
public final class OpenMythics {
    private static final Map<UUID, UUID> shownBoss = new HashMap<>();
    private OpenMythics() {}
    public static boolean isAquatic(int kind) {
        return kind>=0 && kind<MythicExpedition.IDS.length &&
            (MythicExpedition.IDS[kind].equals("pearl_drakeling") || MythicExpedition.IDS[kind].equals("weeping_axolotl"));
    }

    public static boolean clearLegacy(AdventureData.Journal journal) {
        if (journal.session != null && journal.session.kind == AdventureData.Kind.MYTHIC_LAIR) {
            journal.session = null;
            return true;
        }
        return false;
    }

    private static void migrate(ServerPlayer p) {
        if (clearLegacy(AdventureService.journal(p))) GloomRealm.dirty(p);
    }

    public static boolean start(ServerPlayer p, int kind) {
        migrate(p);
        if (kind < 0 || kind >= MythicExpedition.IDS.length ||
            !p.m_6084_() || p.m_5833_() || p.m_9236_().m_46791_() == Difficulty.PEACEFUL ||
            !MythicExpedition.near(p,kind,24)) return false;
        CompoundTag n = MythicExpedition.state(p,kind);
        if (n.m_128451_("Status") == 2 ||
            (!n.m_128441_("Lair") && (kind >= 3 || !GloomRealm.state(p).m_128471_("Ready")))) return false;
        // An unloaded body still owns its UUID. Never create a duplicate just
        // because a chunk is temporarily absent from the server entity lookup.
        if (n.m_128403_("Active")) return MythicExpedition.body(p,kind) != null;
        if (!MythicExpedition.spawn(p,kind)) return false;
        n.m_128405_("Status",1);
        GloomRealm.dirty(p);
        return true;
    }

    public static boolean startDevourer(ServerPlayer p) {
        migrate(p);
        MythicState state=MythicService.state(p);
        if (state.status==2 || (!state.generated && !state.gloom.m_128441_("NaturalDevourer")) ||
            !p.m_6084_() || p.m_5833_() || p.m_9236_().m_46791_()==Difficulty.PEACEFUL ||
            !MythicService.near(p,25)) return false;
        if(state.active!=null)return MythicService.body(p)!=null;
        if(!MythicService.spawn(p))return false;
        state.status=1;GloomRealm.dirty(p);return true;
    }

    /** Local combat view only: no write to Journal.session and no fixed party. */
    public static AdventureData.Session combatView(ServerPlayer p) {
        AdventureData.Session view=new AdventureData.Session();
        view.kind=AdventureData.Kind.MYTHIC_LAIR;
        view.phase=AdventureData.Phase.ACTIVE;
        view.participants.addAll(ArmyData.get(p.f_8924_).army(p.m_20148_()).shadows.keySet());
        return view;
    }

    public static AdventureData.Session liveSession(AdventureData.Journal journal) {
        if(journal.mythic.status==2 || journal.mythic.active==null)return null;
        AdventureData.Session view=new AdventureData.Session();
        view.kind=AdventureData.Kind.MYTHIC_LAIR;view.phase=AdventureData.Phase.ACTIVE;
        return view;
    }

    public static void join(EntityJoinLevelEvent event) {
        Entity entity=event.getEntity();
        if(event.getLevel().f_46443_ || !(entity instanceof MythicBeast beast) ||
           ShadowService.isShadow(entity) || !entity.getPersistentData().m_128403_("SoulboundMythicHost"))return;
        int kind=beast.kind();
        boolean valid=kind>=0 && kind<MythicExpedition.IDS.length;
        if(valid){
            UUID owner=entity.getPersistentData().m_128342_("SoulboundMythicHost");
            CompoundTag n=AdventureData.get(entity.m_20194_()).journal(owner).mythic.gloom.m_128469_(MythicExpedition.IDS[kind]);
            valid=n.m_128451_("Status")!=2 && n.m_128403_("Active") && entity.m_20148_().equals(n.m_128342_("Active"));
        }
        if(!valid){event.setCanceled(true);entity.m_146870_();}
    }

    public static boolean defeated(MythicBeast beast,UUID killer) {
        if(!beast.getPersistentData().m_128403_("SoulboundMythicHost"))return false;
        UUID owner=beast.getPersistentData().m_128342_("SoulboundMythicHost");
        int kind=beast.kind();
        if(!owner.equals(killer) || kind<0 || kind>=MythicExpedition.IDS.length)return false;
        AdventureData data=AdventureData.get(beast.m_20194_());
        AdventureData.Journal journal=data.journal(owner);
        CompoundTag n=journal.mythic.gloom.m_128469_(MythicExpedition.IDS[kind]);
        if(n.m_128451_("Status")==2 || !n.m_128403_("Active") || !beast.m_20148_().equals(n.m_128342_("Active")))return false;
        ServerPlayer player=beast.m_20194_().m_6846_().m_11259_(owner);
        boolean awarded=player==null ? WildBosses.captureOffline(beast.m_20194_(),owner,beast) : WildBosses.capture(player,beast);
        if(!awarded)return false;
        n.m_128405_("Status",2);n.m_128362_("Soul",beast.m_20148_());
        n.m_128473_("Active");n.m_128473_("Snapshot");
        clearLegacy(journal);data.m_77762_();
        if(player!=null)MythicNetwork.send(player,false);
        return true;
    }

    /** Logging out, changing dimension or walking away never leaves PAUSED
     * behind. Bodies and their UUIDs remain ordinary persistent world entities. */
    public static void pause(MinecraftServer server,UUID owner) {
        AdventureData data=AdventureData.get(server);
        AdventureData.Journal journal=data.journal(owner);
        if(clearLegacy(journal))data.m_77762_();
        if(journal.mythic.bar!=null){journal.mythic.bar.m_7706_();journal.mythic.bar=null;}
        shownBoss.remove(owner);
    }

    public static boolean cancel(ServerPlayer p) {
        pause(p.f_8924_,p.m_20148_());return true;
    }

    public static boolean editableGloom(ServerPlayer p,BlockPos pos) {
        return GloomRealm.inside(p,pos) && !OdysseyRealm.foreignPlot(p,pos);
    }

    public static void fallback(ServerPlayer p) {
        if(p.f_19797_%40!=0 || !GloomRealm.state(p).m_128471_("Ready"))return;
        for(int kind=0;kind<3;kind++)if(MythicExpedition.near(p,kind,22))start(p,kind);
    }

    public static void tick(ServerPlayer p) {
        migrate(p);
        GloomRealm.tick(p);MythicLair.tick(p);MythicSites.tick(p);fallback(p);
        MythicState state=MythicService.state(p);
        if(p.f_19797_%40==0 && state.status!=2 && MythicService.near(p,22))startDevourer(p);
        if(p.f_19797_%10!=0)return;
        Mob closest=null;double closestDistance=54*54;
        for(int kind=-1;kind<MythicExpedition.IDS.length;kind++){
            if(kind<0 && state.active==null)continue;
            if(kind>=0 && (!state.gloom.m_128425_(MythicExpedition.IDS[kind],10) ||
                !state.gloom.m_128469_(MythicExpedition.IDS[kind]).m_128403_("Active")))continue;
            Mob body=kind<0?MythicService.body(p):MythicExpedition.body(p,kind);
            if(body==null || !body.m_6084_())continue;
            boolean sameWorld=body.m_9236_()==p.m_9236_();
            double distance=sameWorld?body.m_20280_(p):Double.MAX_VALUE;
            if(!p.m_6084_() || p.m_5833_() || distance>54*54){body.m_6710_(null);continue;}
            if(distance<closestDistance){closest=body;closestDistance=distance;}
            LivingEntity target=!p.m_7500_()?p:null;
            double targetDistance=target==null?Double.MAX_VALUE:distance;
            for(ShadowRecord record:ArmyData.get(p.f_8924_).army(p.m_20148_()).shadows.values()){
                Mob ally=ShadowService.active(p.f_8924_,record);
                if(ally==null || !ally.m_6084_() || ally.m_9236_()!=body.m_9236_())continue;
                double d=body.m_20280_(ally);
                if(d<targetDistance && d<54*54){target=ally;targetDistance=d;}
            }
            body.m_6710_(target);
        }
        if(closest==null){
            if(state.bar!=null){state.bar.m_7706_();state.bar=null;}shownBoss.remove(p.m_20148_());return;
        }
        if(state.bar==null || !closest.m_20148_().equals(shownBoss.get(p.m_20148_()))){
            if(state.bar!=null)state.bar.m_7706_();
            state.bar=new ServerBossEvent(closest.m_7755_(),BossEvent.BossBarColor.PURPLE,BossEvent.BossBarOverlay.PROGRESS);
            state.bar.m_6543_(p);shownBoss.put(p.m_20148_(),closest.m_20148_());
        }
        state.bar.m_142711_(Math.max(0,Math.min(1,closest.m_21223_()/closest.m_21233_())));
    }
}
