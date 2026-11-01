package dev.shadowsovereign.content;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.network.syncher.*;
import net.minecraft.world.damagesource.DamageSource;
/** Small passive fauna; participates in the existing generic soul capture path. */
public final class PrismJerboa extends PathfinderMob {
 private static final EntityDataAccessor<Integer> ACTION=SynchedEntityData.defineId(PrismJerboa.class,EntityDataSerializers.INT);
 private static final EntityDataAccessor<Integer> START=SynchedEntityData.defineId(PrismJerboa.class,EntityDataSerializers.INT);
 private int cooldown;
 private boolean grounded=true;
 public PrismJerboa(EntityType<? extends PrismJerboa> type,Level level){super(type,level);}
 public static AttributeSupplier.Builder attributes(){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,12).add(Attributes.MOVEMENT_SPEED,.32).add(Attributes.FOLLOW_RANGE,16);}
 @Override protected void registerGoals(){goalSelector.addGoal(0,new FloatGoal(this));goalSelector.addGoal(1,new PanicGoal(this,1.5));goalSelector.addGoal(2,new TemptGoal(this,1.1,Ingredient.of(Items.CARROT,Items.WHEAT_SEEDS),false));goalSelector.addGoal(4,new WaterAvoidingRandomStrollGoal(this,.8));goalSelector.addGoal(5,new LookAtPlayerGoal(this,Player.class,6));goalSelector.addGoal(6,new RandomLookAroundGoal(this));}
 public static boolean canSpawn(EntityType<PrismJerboa> type,ServerLevelAccessor level,MobSpawnType reason,BlockPos pos,RandomSource random){return DesertPlant.ground(level.getBlockState(pos.below()))&&level.getBlockState(pos).isAir()&&level.getFluidState(pos).isEmpty();}
 @Override protected void defineSynchedData(){super.defineSynchedData();entityData.define(ACTION,1);entityData.define(START,0);}
 private void action(int value){entityData.set(ACTION,value);entityData.set(START,tickCount);}
 @Override public boolean hurt(DamageSource source,float damage){
  boolean shield=entityData.get(ACTION)==3;
  boolean result=super.hurt(source,shield?damage*.6f:damage);
  if(result&&!level().isClientSide&&!shield)action(3);
  return result;
 }
 @Override public void aiStep(){
  super.aiStep();if(level().isClientSide)return;
  if(cooldown>0)cooldown--;
  int a=entityData.get(ACTION),elapsed=tickCount-entityData.get(START);
  if(a==0&&grounded&&!onGround()&&!isInWater())action(4);
  if(a==4&&onGround())action(0);
  grounded=onGround();
  if(a==2&&elapsed==24){
   level().playSound(null,blockPosition(),net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME,net.minecraft.sounds.SoundSource.NEUTRAL,.5f,1.4f);
   var foe=getTarget();if(foe!=null&&foe.isAlive()&&distanceToSqr(foe)<25&&!isAlliedTo(foe)&&canAttack(foe)){
    foe.hurt(damageSources().mobAttack(this),4);foe.knockback(.4,getX()-foe.getX(),getZ()-foe.getZ());
   }
  }
  if(a>0&&elapsed>(a==1?60:a==2?48:a==4?40:80))action(0);
  if(a==0&&cooldown==0&&getTarget()!=null&&distanceToSqr(getTarget())<25){action(2);cooldown=100;}
 }
 public String clip(float amount){
  int a=entityData.get(ACTION);
  return isDeadOrDying()?"death":a==1?"summoned_arrival":a==2?"crystal_chime_attack":a==3?"prism_shield":a==4?"giant_jump":hurtTime>0?"take_damage":amount>.5?"run":amount>.02?"walk":level().isNight()?"sleep":(tickCount/160)%3==0?"sit":"idle";
 }
 public float clipAge(float age){return isDeadOrDying()?deathTime/20f:entityData.get(ACTION)>0?(age-entityData.get(START))/20f:age/20f;}
 @Override protected void tickDeath(){deathTime++;if(deathTime>=44&&!level().isClientSide&&!isRemoved()){level().broadcastEntityEvent(this,(byte)60);remove(Entity.RemovalReason.KILLED);}}
}
