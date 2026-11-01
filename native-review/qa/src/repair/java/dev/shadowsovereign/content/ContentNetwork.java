package dev.shadowsovereign.content;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ContentNetwork {
 public static final SimpleChannel CHANNEL=NetworkRegistry.newSimpleChannel(new ResourceLocation(ContentBootstrap.NS,"content_controls"),()->"1","1"::equals,"1"::equals);
 public record Request(int action){}
 public record Status(int active,int stuck,int separated,int recovering,int failed,int flags,int atmosphere,boolean open){}
 public static void register(){
  CHANNEL.registerMessage(0,Request.class,(m,b)->b.writeVarInt(m.action()),b->new Request(b.readVarInt()),ContentNetwork::request,java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER));
  CHANNEL.registerMessage(1,Status.class,ContentNetwork::encode,ContentNetwork::decode,ContentNetwork::status,java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
 }
 private static void request(Request m,Supplier<NetworkEvent.Context> supplier){
  var ctx=supplier.get();ServerPlayer sender=ctx.getSender();
  if(sender!=null&&m.action()>=0&&m.action()<=6)ctx.enqueueWork(()->AutopilotManager.request(sender,m.action()));
  ctx.setPacketHandled(true);
 }
 private static void status(Status m,Supplier<NetworkEvent.Context> supplier){
  var ctx=supplier.get();ctx.enqueueWork(()->DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->ContentClient.receive(m)));ctx.setPacketHandled(true);
 }
 private static void encode(Status m,FriendlyByteBuf b){b.writeVarInt(m.active());b.writeVarInt(m.stuck());b.writeVarInt(m.separated());b.writeVarInt(m.recovering());b.writeVarInt(m.failed());b.writeVarInt(m.flags());b.writeVarInt(m.atmosphere());b.writeBoolean(m.open());}
 private static Status decode(FriendlyByteBuf b){return new Status(b.readVarInt(),b.readVarInt(),b.readVarInt(),b.readVarInt(),b.readVarInt(),b.readVarInt(),b.readVarInt(),b.readBoolean());}
 public static void send(ServerPlayer p,Status state){CHANNEL.send(PacketDistributor.PLAYER.with(()->p),state);}
 public static void action(int action){CHANNEL.sendToServer(new Request(action));}
}
