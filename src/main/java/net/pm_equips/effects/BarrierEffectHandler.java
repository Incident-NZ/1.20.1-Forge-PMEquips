package net.pm_equips.effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;

@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class BarrierEffectHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        var instance = entity.getEffect(MobEffectInit.BARRIER.get());
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