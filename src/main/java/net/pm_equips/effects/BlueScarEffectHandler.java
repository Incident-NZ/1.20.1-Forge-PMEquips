package net.pm_equips.effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "pm_equips", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlueScarEffectHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        LivingEntity entity = event.player;
        if (entity.level().isClientSide) return;

        var tag = entity.getPersistentData();
        int ticks = tag.getInt("blue_scar_dot_ticks");

        if (ticks > 0) {
            tag.putInt("blue_scar_dot_ticks", ticks - 1);
            if (ticks % 20 == 0) { // 毎秒1回ダメージ
                entity.hurt(entity.damageSources().magic(), 2.0f); // 2ダメージ × 2秒 = 4
            }
        }
    }
}
