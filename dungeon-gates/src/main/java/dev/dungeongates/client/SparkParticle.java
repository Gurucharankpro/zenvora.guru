package dev.dungeongates.client;
import net.minecraft.client.particle.*;import net.minecraft.client.multiplayer.ClientLevel;import net.minecraft.core.particles.SimpleParticleType;
public final class SparkParticle extends TextureSheetParticle {
 private final SpriteSet sprites;
 SparkParticle(ClientLevel l,double x,double y,double z,double dx,double dy,double dz,SpriteSet s){super(l,x,y,z,dx,dy,dz);sprites=s;lifetime=24+random.nextInt(16);quadSize=.08f+random.nextFloat()*.12f;friction=.95f;setSpriteFromAge(s);}
 public ParticleRenderType getRenderType(){return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;}
 public int getLightColor(float partial){return 0xF000F0;}
 public void tick(){super.tick();setSpriteFromAge(sprites);alpha=1-(float)age/lifetime;}
 public static final class Provider implements ParticleProvider<SimpleParticleType>{final SpriteSet sprites;public Provider(SpriteSet s){sprites=s;}public Particle createParticle(SimpleParticleType type,ClientLevel l,double x,double y,double z,double dx,double dy,double dz){return new SparkParticle(l,x,y,z,dx,dy,dz,sprites);}}
}
