package net.pm_equips.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.Finger2WeaponSP;
import net.pm_equips.network.NetworkPacketInit;
import net.pm_equips.network.ForgeFeaturePackets;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid=PMEquipsMain.MOD_ID,value=Dist.CLIENT)
public final class ForgeFeatureClient {
    private ForgeFeatureClient() {}
    @SubscribeEvent public static void onMiss(PlayerInteractEvent.LeftClickEmpty event){
        if(Finger2WeaponSP.form(event.getItemStack())!=0)NetworkPacketInit.INSTANCE.sendToServer(new ForgeFeaturePackets.CaduceusMissPacket());
    }
    public static void spawnThumbTrail(Vec3 start,Vec3 end,int mask){
        var level=Minecraft.getInstance().level;double distance=start.distanceTo(end);
        if(level==null||!Double.isFinite(distance)||distance>129||mask<=0||mask>31)return;
        Vector3f[] colors={new Vector3f(.9f,.9f,.9f),new Vector3f(1,.25f,.05f),new Vector3f(.35f,.8f,1),new Vector3f(1,.9f,.2f),new Vector3f(.8f,.3f,1)};
        List<DustParticleOptions> particles=new ArrayList<>();
        for(int mode=0;mode<5;mode++)if((mask&1<<mode)!=0)particles.add(new DustParticleOptions(colors[mode],.8f));
        int steps=Mth.clamp(Mth.ceil(distance*2),1,256);
        for(int i=0;i<=steps;i++){Vec3 pos=start.lerp(end,(double)i/steps);level.addParticle(particles.get(i%particles.size()),pos.x,pos.y,pos.z,0,0,0);}
    }
}
