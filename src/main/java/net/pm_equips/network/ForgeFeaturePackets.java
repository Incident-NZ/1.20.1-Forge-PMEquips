package net.pm_equips.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.pm_equips.client.ArbiterArmorClient;
import net.pm_equips.client.ForgeFeatureClient;
import net.pm_equips.entity.MobNPC2;
import net.pm_equips.entity.MobNPC3;
import net.pm_equips.items.Finger2WeaponSP;

/** Forge 1.20.1 transport for feature data; all gameplay handlers run on the server thread. */
public final class ForgeFeaturePackets {
    private static boolean registered;
    private ForgeFeaturePackets() {}

    public static void register() {
        if(registered)return; registered=true;
        register(ArbiterArmorPacket.class,32,NetworkDirection.PLAY_TO_SERVER,
                (p,b)->b.writeByte(p.action()),b->new ArbiterArmorPacket(b.readByte()),(p,c)->{
                    if(c.get().getSender()!=null)ArbiterArmorPacket.process(c.get().getSender(),p.action());
                });
        register(ArbiterArmorPacket.FairyTrail.class,33,NetworkDirection.PLAY_TO_CLIENT,
                (p,b)->{writeVec(b,p.start());writeVec(b,p.end());},b->new ArbiterArmorPacket.FairyTrail(readVec(b),readVec(b)),
                (p,c)->DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->ArbiterArmorClient.spawnTrail(p.start(),p.end())));
        register(FingerShotTrail.class,34,NetworkDirection.PLAY_TO_CLIENT,
                (p,b)->{writeVec(b,p.start());writeVec(b,p.end());b.writeByte(p.mask());},b->new FingerShotTrail(readVec(b),readVec(b),b.readUnsignedByte()),
                (p,c)->DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->ForgeFeatureClient.spawnThumbTrail(p.start(),p.end(),p.mask())));
        register(CaduceusMissPacket.class,35,NetworkDirection.PLAY_TO_SERVER,(p,b)->{},b->new CaduceusMissPacket(),
                (p,c)->{if(c.get().getSender()!=null)Finger2WeaponSP.resetCombo(c.get().getSender());});
        register(MobNPC3.SkinCatalog.class,36,NetworkDirection.PLAY_TO_CLIENT,(p,b)->{
            b.writeVarInt(p.files().size());p.files().forEach((name,png)->{b.writeUtf(name,255);b.writeByteArray(png);});
        },b->{
            int count=b.readVarInt(),total=0;if(count<0||count>48)throw new IllegalArgumentException("NPC skin count");
            Map<String,byte[]> files=new HashMap<>();
            for(int i=0;i<count;i++){
                String name=b.readUtf(255);byte[] png=b.readByteArray(65536);total+=png.length;
                if(total>786432||!MobNPC3.validPNG(png)||name.contains("/")||name.contains("\\")||files.put(name,png)!=null)
                    throw new IllegalArgumentException("NPC skin payload");
            }
            return new MobNPC3.SkinCatalog(Map.copyOf(files));
        },(p,c)->DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->MobNPC2.acceptSkins(p.files())));
    }

    private static <T> void register(Class<T> type,int id,NetworkDirection direction,BiConsumer<T,FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf,T> decoder,BiConsumer<T,Supplier<NetworkEvent.Context>> handler) {
        NetworkPacketInit.INSTANCE.messageBuilder(type,id,direction).encoder(encoder).decoder(decoder)
                .consumerMainThread((packet,context)->{handler.accept(packet,context);context.get().setPacketHandled(true);}).add();
    }
    private static void writeVec(FriendlyByteBuf buffer,Vec3 vec){buffer.writeDouble(vec.x);buffer.writeDouble(vec.y);buffer.writeDouble(vec.z);}
    private static Vec3 readVec(FriendlyByteBuf buffer){return new Vec3(buffer.readDouble(),buffer.readDouble(),buffer.readDouble());}
    public static void sendTo(ServerPlayer player,Object packet){NetworkPacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(()->player),packet);}
    public static void sendNear(ServerLevel level,Vec3 center,double radius,Object packet){
        NetworkPacketInit.INSTANCE.send(PacketDistributor.NEAR.with(()->new PacketDistributor.TargetPoint(center.x,center.y,center.z,radius,level.dimension())),packet);
    }
    public record FingerShotTrail(Vec3 start,Vec3 end,int mask) {}
    public record CaduceusMissPacket() {}
}
