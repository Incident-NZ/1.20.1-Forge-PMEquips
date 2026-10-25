package net.pm_equips.effects;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class BarrierEffectHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        var instance = entity.getEffect(MobEffectInit.BARRIER);
        if (instance == null) return;

        float threshold = CustomMobEffects.BarrierEffect.getNegateThreshold(instance.getAmplifier());
        float amount = event.getAmount();

        // 一定以下なら完全無効
        if (amount <= threshold) {
            event.setCanceled(true);
            // 必要なら軽くパーティクルや音を出す
            // entity.level().playSound(...);
        }
    }
}
