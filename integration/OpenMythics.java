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
            !p.isAlive() || p.isSpectator() || p.level().getDifficulty() == Difficulty.PEACEFUL ||
            !MythicExpedition.near(p,kind,24)) return false;
        CompoundTag n = MythicExpedition.state(p,kind);
        if (n.getInt("Status") == 2 ||
            (!n.contains("Lair") && (kind >= 3 || !GloomRealm.state(p).getBoolean("Ready")))) return false;
        // An unloaded body still owns its UUID. Never create a duplicate just
        // because a chunk is temporarily absent from the server entity lookup.
        if (n.hasUUID("Active")) return MythicExpedition.body(p,kind) != null;
        refresh(p,kind);
        if(!within(p,kind))return false;
        n.remove("Snapshot");
        if (!MythicExpedition.spawn(p,kind)) return false;
        var summoned=MythicExpedition.body(p,kind);if(summoned!=null)summoned.setHealth(summoned.getMaxHealth());
        n.putInt("Status",1);
        GloomRealm.dirty(p);
        return true;
    }

    public static boolean startDevourer(ServerPlayer p) {
        migrate(p);
        MythicState state=MythicService.state(p);
        if (state.status==2 || (!state.generated && !state.gloom.contains("NaturalDevourer")) ||
            !p.isAlive() || p.isSpectator() || p.level().getDifficulty()==Difficulty.PEACEFUL ||
            !MythicService.near(p,25)) return false;
        if(state.active!=null)return MythicService.body(p)!=null;
        refresh(p,-1);if(!within(p,-1))return false;state.snapshot=new CompoundTag();
        if(!MythicService.spawn(p))return false;
        var summoned=MythicService.body(p);if(summoned!=null)summoned.setHealth(summoned.getMaxHealth());
        state.status=1;GloomRealm.dirty(p);return true;
    }

    /** Local combat view only: no write to Journal.session and no fixed party. */
    public static AdventureData.Session combatView(ServerPlayer p) {
        AdventureData.Session view=new AdventureData.Session();
        view.kind=AdventureData.Kind.MYTHIC_LAIR;
        view.phase=AdventureData.Phase.ACTIVE;
        view.participants.addAll(ArmyData.get(p.server).army(p.getUUID()).shadows.keySet());
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
        if(event.getLevel().isClientSide || !(entity instanceof MythicBeast beast) ||
           ShadowService.isShadow(entity) || !entity.getPersistentData().hasUUID("SoulboundMythicHost"))return;
        int kind=beast.kind();
        boolean valid=kind>=0 && kind<MythicExpedition.IDS.length;
        if(valid){
            UUID owner=entity.getPersistentData().getUUID("SoulboundMythicHost");
            CompoundTag n=AdventureData.get(entity.getServer()).journal(owner).mythic.gloom.getCompound(MythicExpedition.IDS[kind]);
            valid=n.getInt("Status")!=2 && n.hasUUID("Active") && entity.getUUID().equals(n.getUUID("Active"));
        }
        if(!valid){event.setCanceled(true);entity.discard();}
    }

    public static boolean defeated(MythicBeast beast,UUID killer) {
        if(!beast.getPersistentData().hasUUID("SoulboundMythicHost"))return false;
        UUID owner=beast.getPersistentData().getUUID("SoulboundMythicHost");
        int kind=beast.kind();
        if(!owner.equals(killer) || kind<0 || kind>=MythicExpedition.IDS.length)return false;
        AdventureData data=AdventureData.get(beast.getServer());
        AdventureData.Journal journal=data.journal(owner);
        CompoundTag n=journal.mythic.gloom.getCompound(MythicExpedition.IDS[kind]);
        if(n.getInt("Status")==2 || !n.hasUUID("Active") || !beast.getUUID().equals(n.getUUID("Active")))return false;
        ServerPlayer player=beast.getServer().getPlayerList().getPlayer(owner);
        boolean awarded=player==null ? WildBosses.captureOffline(beast.getServer(),owner,beast) : WildBosses.capture(player,beast);
        if(!awarded)return false;
        n.putInt("Status",2);n.putUUID("Soul",beast.getUUID());
        n.remove("Active");n.remove("Snapshot");
        clearLegacy(journal);data.setDirty();
        if(player!=null)MythicNetwork.send(player,false);
        return true;
    }

    /** Release canonical records before discarding bodies; unloaded stale bodies fail join validation. */
    private static void release(MinecraftServer server,UUID owner,int kind){
        var data=AdventureData.get(server);var journal=data.journal(owner);var st=journal.mythic;
        CompoundTag n=kind<0?st.gloom:st.gloom.getCompound(MythicExpedition.IDS[kind]);
        UUID id=kind<0?st.active:n.hasUUID("Active")?n.getUUID("Active"):null;
        if(kind<0){st.active=null;st.snapshot=new CompoundTag();if(st.status!=2)st.status=1;}
        else{n.remove("Active");n.remove("Snapshot");if(n.getInt("Status")!=2)n.putInt("Status",1);}
        if(id!=null){var w=server.getLevel(OdysseyRealm.KEY);if(w!=null){
            var entity=w.getEntity(id);if(entity!=null&&!ShadowService.isShadow(entity)){entity.discard();}
            java.util.List<Entity> shots=new java.util.ArrayList<>();
            for(var e:w.getAllEntities())if(e instanceof net.minecraft.world.entity.projectile.Projectile shot&&shot.getOwner()!=null&&id.equals(shot.getOwner().getUUID())||e instanceof net.minecraft.world.entity.projectile.EvokerFangs&&e.getPersistentData().hasUUID("MythicCaster")&&id.equals(e.getPersistentData().getUUID("MythicCaster")))shots.add(e);
            shots.forEach(Entity::discard);
            for(var record:ArmyData.get(server).army(owner).shadows.values()){var ally=ShadowService.active(server,record);if(ally!=null&&ally.getTarget()!=null&&id.equals(ally.getTarget().getUUID())){ally.setTarget(null);ally.getNavigation().stop();}}
        }}
        clearLegacy(journal);if(st.bar!=null){st.bar.removeAllPlayers();st.bar=null;}shownBoss.remove(owner);data.setDirty();
    }
    private static boolean within(ServerPlayer p,int kind){
        if(!p.level().dimension().equals(OdysseyRealm.KEY))return false;
        var at=kind<0?MythicLair.center(p).above():GloomRealm.habitat(p,kind);
        var n=kind<0?MythicService.state(p).gloom:MythicExpedition.state(p,kind);var b=MythicSites.bounds(n,at);
        var pos=p.blockPosition();
        return b==null?pos.distSqr(at)<=32*32:pos.getX()>=b.minX()-3&&pos.getX()<=b.maxX()+3&&pos.getZ()>=b.minZ()-3&&pos.getZ()<=b.maxZ()+3&&pos.getY()>=b.minY()-8&&pos.getY()<=b.maxY()+16;
    }
    private static void refresh(ServerPlayer p,int kind){
        if(!p.level().dimension().equals(OdysseyRealm.KEY))return;
        var site=MythicSites.sites().stream().filter(v->v.kind()==kind).findFirst().orElse(null);if(site==null)return;
        var structure=p.serverLevel().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE).get(site.structure());if(structure==null)return;
        var start=p.serverLevel().structureManager().getStructureAt(p.blockPosition(),structure);if(!start.isValid())return;
        var at=MythicSites.altar(start,site);if(at==null)return;var box=start.getBoundingBox();var n=kind<0?MythicService.state(p).gloom:MythicExpedition.state(p,kind);
        n.putLong("NaturalAltar",at.asLong());n.putIntArray("NaturalBounds",new int[]{box.minX(),box.minY(),box.minZ(),box.maxX(),box.maxY(),box.maxZ()});
        if(kind<0)n.putLong("NaturalDevourer",at.below().asLong());else n.putLong("Lair",at.asLong());
    }
    public static void pause(MinecraftServer server,UUID owner){
        var journal=AdventureData.get(server).journal(owner);
        if(journal.mythic.active!=null)release(server,owner,-1);
        for(int kind=0;kind<MythicExpedition.IDS.length;kind++)if(journal.mythic.gloom.getCompound(MythicExpedition.IDS[kind]).hasUUID("Active"))release(server,owner,kind);
        if(clearLegacy(journal))AdventureData.get(server).setDirty();
        if(journal.mythic.bar!=null){journal.mythic.bar.removeAllPlayers();journal.mythic.bar=null;}shownBoss.remove(owner);
    }

    public static boolean cancel(ServerPlayer p) {
        pause(p.server,p.getUUID());return true;
    }

    public static boolean editableGloom(ServerPlayer p,BlockPos pos) {
        return GloomRealm.inside(p,pos) && !OdysseyRealm.foreignPlot(p,pos);
    }

    public static void fallback(ServerPlayer p) {
        if(p.tickCount%40!=0 || !GloomRealm.state(p).getBoolean("Ready"))return;
        for(int kind=0;kind<3;kind++)if(MythicExpedition.near(p,kind,22))start(p,kind);
    }

    public static void tick(ServerPlayer p) {
        migrate(p);
        boolean escaped=false;
        var existing=MythicService.state(p);
        if(existing.active!=null&&(!p.isAlive()||p.isSpectator()||!within(p,-1)||p.level().getDifficulty()==Difficulty.PEACEFUL)){release(p.server,p.getUUID(),-1);escaped=true;}
        for(int k=0;k<MythicExpedition.IDS.length;k++)if(existing.gloom.getCompound(MythicExpedition.IDS[k]).hasUUID("Active")&&(!p.isAlive()||p.isSpectator()||!within(p,k)||p.level().getDifficulty()==Difficulty.PEACEFUL)){release(p.server,p.getUUID(),k);escaped=true;}
        if(escaped){p.displayClientMessage(net.minecraft.network.chat.Component.translatable("review.shadowsovereign.retreat"),true);return;}
        GloomRealm.tick(p);MythicLair.tick(p);MythicSites.tick(p);fallback(p);
        MythicState state=MythicService.state(p);
        if(p.tickCount%40==0 && state.status!=2 && MythicService.near(p,22))startDevourer(p);
        if(p.tickCount%10!=0)return;
        Mob closest=null;double closestDistance=54*54;
        for(int kind=-1;kind<MythicExpedition.IDS.length;kind++){
            if(kind<0 && state.active==null)continue;
            if(kind>=0 && (!state.gloom.contains(MythicExpedition.IDS[kind],10) ||
                !state.gloom.getCompound(MythicExpedition.IDS[kind]).hasUUID("Active")))continue;
            Mob body=kind<0?MythicService.body(p):MythicExpedition.body(p,kind);
            if(body==null || !body.isAlive())continue;
            boolean sameWorld=body.level()==p.level();
            double distance=sameWorld?body.distanceToSqr(p):Double.MAX_VALUE;
            if(!p.isAlive() || p.isSpectator() || distance>54*54){body.setTarget(null);continue;}
            if(distance<closestDistance){closest=body;closestDistance=distance;}
            LivingEntity target=!p.isCreative()?p:null;
            double targetDistance=target==null?Double.MAX_VALUE:distance;
            for(ShadowRecord record:ArmyData.get(p.server).army(p.getUUID()).shadows.values()){
                Mob ally=ShadowService.active(p.server,record);
                if(ally==null || !ally.isAlive() || ally.level()!=body.level())continue;
                double d=body.distanceToSqr(ally);
                if(d<targetDistance && d<54*54){target=ally;targetDistance=d;}
            }
            body.setTarget(target);
        }
        if(closest==null){
            if(state.bar!=null){state.bar.removeAllPlayers();state.bar=null;}shownBoss.remove(p.getUUID());return;
        }
        if(state.bar==null || !closest.getUUID().equals(shownBoss.get(p.getUUID()))){
            if(state.bar!=null)state.bar.removeAllPlayers();
            state.bar=new ServerBossEvent(closest.getName(),BossEvent.BossBarColor.PURPLE,BossEvent.BossBarOverlay.PROGRESS);
            state.bar.addPlayer(p);shownBoss.put(p.getUUID(),closest.getUUID());
        }
        state.bar.setProgress(Math.max(0,Math.min(1,closest.getHealth()/closest.getMaxHealth())));
    }
}
