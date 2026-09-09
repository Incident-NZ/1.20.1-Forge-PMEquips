package net.pm_equips.effects;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

@net.neoforged.fml.common.EventBusSubscriber(modid = "pm_equips", bus = net.neoforged.fml.common.EventBusSubscriber.Bus.GAME)
public class BlueScarEffectHandler {

    @SubscribeEvent
    public static void onEntityTick(PlayerTickEvent.Post event) {
        LivingEntity entity = event.getEntity();
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
