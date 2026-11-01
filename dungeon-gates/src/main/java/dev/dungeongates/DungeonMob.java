package dev.dungeongates;
import java.util.*;
import net.minecraft.nbt.CompoundTag;import net.minecraft.network.syncher.*;import net.minecraft.server.level.*;import net.minecraft.server.level.ServerBossEvent;import net.minecraft.world.BossEvent;import net.minecraft.network.chat.Component;import net.minecraft.world.entity.*;import net.minecraft.world.entity.ai.attributes.Attributes;import net.minecraft.world.entity.ai.goal.*;import net.minecraft.world.entity.ai.goal.target.*;import net.minecraft.world.entity.player.Player;import net.minecraft.world.entity.monster.Monster;import net.minecraft.world.entity.projectile.Snowball;import net.minecraft.world.item.*;import net.minecraft.world.level.*;import net.minecraft.world.damagesource.DamageSource;import net.minecraft.world.effect.*;import net.minecraft.core.particles.ParticleTypes;import net.minecraft.world.phys.*;
import software.bernie.geckolib.animatable.GeoEntity;import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;import software.bernie.geckolib.util.GeckoLibUtil;import software.bernie.geckolib.core.animation.*;
public final class DungeonMob extends Monster implements GeoEntity {
 public final int kind;public UUID instance;private boolean phase;private int cooldown=30,age,move=-1;private LivingEntity victim;
 private static final EntityDataAccessor<Integer> MOVE=SynchedEntityData.defineId(DungeonMob.class,EntityDataSerializers.INT);
 private final AnimatableInstanceCache cache=GeckoLibUtil.createInstanceCache(this);
 private final ServerBossEvent bar=new ServerBossEvent(Component.literal("Dungeon Guardian"),BossEvent.BossBarColor.GREEN,BossEvent.BossBarOverlay.PROGRESS);
 public record Move(String clip,int hit,int length,double reach,float power,int effect){}
 // effect 0 melee;1 sweeping AoE;2 projectile;3 guard;4 phase/roar;5 frost AoE.
 public static final Move[][] MOVES={
  {new Move("claw_attack",8,20,2.6,1,0),new Move("tail_sweep",12,26,3,1.1f,1)},
  {new Move("heavy_punch",12,28,3,1.2f,0),new Move("crystal_burst",16,40,6,1,1)},
  {new Move("heavy_slam",21,44,4.5,1.2f,1),new Move("root_sweep",19,40,6,1,1),new Move("emerald_projectile",25,48,16,1,2),new Move("crystal_shield",16,40,4,0,3),new Move("phase_transition",36,60,6,.65f,4)},
  {new Move("claw_attack",8,18,2.7,1,0),new Move("frost_bite",11,22,2.8,1.1f,0),new Move("ice_howl",18,40,6,.6f,5),new Move("jump",9,18,4,.8f,0),new Move("alert",10,24,5,0,4),new Move("spawn",-1,32,0,0,3)},
  {new Move("chain_swing",16,32,4,1,1),new Move("ice_pulse",20,36,6,.8f,5),new Move("heavy_strike",22,40,3.5,1.25f,0),new Move("guard",10,20,0,0,3),new Move("stagger",-1,24,0,0,3),new Move("spawn",-1,40,0,0,3)},
  {new Move("axe_slam",23,44,4.5,1.2f,1),new Move("frozen_sweep",23,48,6,1,5),new Move("ice_spike_attack",24,52,16,1,2),new Move("royal_guard",8,24,0,0,3),new Move("phase_transition",40,70,6,.6f,4),new Move("battle_roar",20,48,7,.5f,5)}
 };
 public DungeonMob(EntityType<? extends Monster> t,Level l,int k){super(t,l);kind=k;xpReward=k%3==2?75:4;bar.setName(getDisplayName());bar.setColor(k<3?BossEvent.BossBarColor.GREEN:BossEvent.BossBarColor.BLUE);}
 public String id(){return DGRegistry.MOB_IDS[kind];}
 protected void defineSynchedData(){super.defineSynchedData();entityData.define(MOVE,-1);}
 protected void registerGoals(){goalSelector.addGoal(0,new FloatGoal(this));goalSelector.addGoal(1,new Combat(this));goalSelector.addGoal(5,new WaterAvoidingRandomStrollGoal(this,.8));goalSelector.addGoal(6,new LookAtPlayerGoal(this,Player.class,10));targetSelector.addGoal(1,new HurtByTargetGoal(this));targetSelector.addGoal(2,new NearestAttackableTargetGoal<>(this,Player.class,10,true,false,p->instance==null||DungeonState.get(((ServerLevel)level()).getServer()).instances.get(instance)!=null&&DungeonState.get(((ServerLevel)level()).getServer()).instances.get(instance).owner.equals(p.getUUID())));}
 public void onAddedToWorld(){super.onAddedToWorld();if(!level().isClientSide&&(kind==3||kind==4)&&!getPersistentData().getBoolean("DungeonSpawnPlayed")){getPersistentData().putBoolean("DungeonSpawnPlayed",true);start(5);}}
 public void initializeStats(){double[] hp={24,36,150,28,40,180};double[] damage={4,5,8,5,6,9};getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp[kind]*DGConfig.HEALTH.get());getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage[kind]*DGConfig.DAMAGE.get());setHealth(getMaxHealth());}
 public SpawnGroupData finalizeSpawn(ServerLevelAccessor l,net.minecraft.world.DifficultyInstance d,MobSpawnType reason,SpawnGroupData data,CompoundTag nbt){var r=super.finalizeSpawn(l,d,reason,data,nbt);initializeStats();return r;}
 public void addAdditionalSaveData(CompoundTag t){super.addAdditionalSaveData(t);if(instance!=null)t.putUUID("DungeonInstance",instance);t.putBoolean("DungeonPhase",phase);}
 public void readAdditionalSaveData(CompoundTag t){super.readAdditionalSaveData(t);instance=t.hasUUID("DungeonInstance")?t.getUUID("DungeonInstance"):null;phase=t.getBoolean("DungeonPhase");move=-1;entityData.set(MOVE,-1);cooldown=35;}
 public boolean removeWhenFarAway(double d){return instance==null&&super.removeWhenFarAway(d);}
 public void startSeenByPlayer(ServerPlayer p){super.startSeenByPlayer(p);if(kind%3==2)bar.addPlayer(p);}
 public void stopSeenByPlayer(ServerPlayer p){super.stopSeenByPlayer(p);bar.removePlayer(p);}
 public void remove(RemovalReason r){bar.removeAllPlayers();super.remove(r);}
 public boolean hurt(DamageSource source,float damage){if(move>=0){var m=MOVES[kind][move];if(m.clip.contains("guard")||m.clip.contains("shield"))damage*=.25f;if(m.clip.equals("phase_transition")&&age<(kind==5?50:60))return false;}boolean hit=super.hurt(source,damage);if(hit&&kind==4&&move<0&&cooldown>10){start(4);cooldown=40;}return hit;}
 private void start(int m){move=m;age=0;victim=getTarget();entityData.set(MOVE,m);getNavigation().stop();}
 public void aiStep(){super.aiStep();if(level().isClientSide)return;bar.setProgress(Math.max(0,getHealth()/getMaxHealth()));if(!isAlive())return;
  if(instance!=null){var i=DungeonState.get(((ServerLevel)level()).getServer()).instances.get(instance);if(i==null||i.retired){discard();return;}if(!i.contains(blockPosition())){var o=i.origin().offset(kind%3==2?(kind==2?48:46):10,3,kind%3==2?(kind==2?12:15):50);moveTo(o.getX()+.5,o.getY(),o.getZ()+.5,getYRot(),0);}}
  if(kind%3==2&&!phase&&getHealth()<=getMaxHealth()*.5f&&move<0){phase=true;start(4);}
  if(move>=0){var m=MOVES[kind][move];age++;if(age==1&&m.clip.equals("jump"))setDeltaMovement(getDeltaMovement().add(0,.45,0));if(age==m.hit)impact(m);if(age>=m.length){move=-1;entityData.set(MOVE,-1);cooldown=phase?20:30;}}else if(cooldown>0)cooldown--;
 }
 private void impact(Move m){var l=(ServerLevel)level();l.sendParticles(kind<3?ParticleTypes.COMPOSTER:ParticleTypes.SNOWFLAKE,getX(),getY()+1,getZ(),24,m.reach*.35,.5,m.reach*.35,.02);if(m.effect==3||m.power==0)return;
  if(m.effect==2){if(victim!=null&&victim.isAlive()){var bolt=new Snowball(l,this);bolt.setItem(new ItemStack(kind<3?Items.EMERALD:Items.PRISMARINE_CRYSTALS));bolt.getPersistentData().putFloat("DungeonDamage",(float)getAttributeValue(Attributes.ATTACK_DAMAGE)*m.power);bolt.getPersistentData().putBoolean("DungeonFrost",kind>=3);bolt.shoot(victim.getX()-getX(),victim.getEyeY()-bolt.getY(),victim.getZ()-getZ(),1.2f,1);l.addFreshEntity(bolt);}return;}
  for(var p:l.getEntitiesOfClass(Player.class,getBoundingBox().inflate(m.reach))){if(p.isCreative()||p.isSpectator()||distanceToSqr(p)>m.reach*m.reach||!hasLineOfSight(p)||m.effect==0&&p!=victim)continue;
   if(p.hurt(damageSources().mobAttack(this),(float)getAttributeValue(Attributes.ATTACK_DAMAGE)*m.power)){p.knockback(.35,getX()-p.getX(),getZ()-p.getZ());if(kind>=3)p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,60,0));}}
 }
 protected void tickDeath(){deathTime++;if(deathTime>=(kind==2?80:kind==5?70:kind%3==1?44:28)&&!level().isClientSide){bar.removeAllPlayers();remove(RemovalReason.KILLED);}}
 private <T extends software.bernie.geckolib.core.animatable.GeoAnimatable> software.bernie.geckolib.core.object.PlayState animate(software.bernie.geckolib.core.animation.AnimationState<T> state){String clip;if(isDeadOrDying())clip="death";else{int m=entityData.get(MOVE);if(m>=0&&m<MOVES[kind].length)clip=MOVES[kind][m].clip;else clip=state.isMoving()?(kind<3?"move":getTarget()!=null?"run":"walk"):"idle";}
  var anim=RawAnimation.begin();if(clip.equals("idle")||clip.equals("move")||clip.equals("walk")||clip.equals("run"))anim.thenLoop("animation."+id()+"."+clip);else anim.thenPlay("animation."+id()+"."+clip);return state.setAndContinue(anim);}
 public void registerControllers(AnimatableManager.ControllerRegistrar c){c.add(new AnimationController<>(this,"state",2,this::animate));}
 public AnimatableInstanceCache getAnimatableInstanceCache(){return cache;}
 static final class Combat extends Goal {final DungeonMob mob;Combat(DungeonMob m){mob=m;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}public boolean canUse(){return mob.getTarget()!=null&&mob.getTarget().isAlive();}public void tick(){var t=mob.getTarget();if(t==null)return;mob.getLookControl().setLookAt(t,30,30);if(mob.move>=0)return;double d=mob.distanceToSqr(t);if(d>9)mob.getNavigation().moveTo(t,1.05);else mob.getNavigation().stop();if(mob.cooldown==0&&d<144&&mob.hasLineOfSight(t)){int count=mob.kind<2?2:mob.kind==2?4:mob.kind==3?5:mob.kind==4?4:6;int selected=mob.random.nextInt(count);if(mob.kind==5&&selected==4)selected=0;var m=MOVES[mob.kind][selected];if(d<=m.reach*m.reach||m.effect==3||m.effect==4)mob.start(selected);}}}
}
