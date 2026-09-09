package net.pm_equips.effects;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.pm_equips.PMERegistryMobEffect;

@net.neoforged.fml.common.EventBusSubscriber(modid = "pm_equips", bus = net.neoforged.fml.common.EventBusSubscriber.Bus.GAME)
public class EffectsEventHandler {

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;
        LivingEntity target = event.getEntity();

        // W.Corp singularity evade handling
        if (target.hasEffect(PMERegistryMobEffect.WCORP_SIN)) {
            var tag = target.getPersistentData();
            int charges = tag.getInt("wcorp_evade_charges");
            if (charges > 0) {
                tag.putInt("wcorp_evade_charges", charges - 1);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onMobEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect().value() == PMERegistryMobEffect.WCORP_SIN.get()) {
            event.getEntity().getPersistentData().remove("wcorp_evade_charges");
        }
    }

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().getEffect().value() == PMERegistryMobEffect.WCORP_SIN.get()) {
            event.getEntity().getPersistentData().remove("wcorp_evade_charges");
        }
    }
}
