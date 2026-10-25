package net.pm_equips.effects;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.pm_equips.MobEffectInit;

@EventBusSubscriber(modid = "pm_equips", bus = EventBusSubscriber.Bus.GAME)
public class EffectsEventHandler {

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;
        LivingEntity target = event.getEntity();

        // W.Corp singularity evade handling
        if (target.hasEffect(MobEffectInit.WCORP_SIN)) {
            var tag = target.getPersistentData();
            int charges = tag.getInt("wcorp_evade_charges");
            if (charges > 0) {
                tag.putInt("wcorp_evade_charges", charges - 1);
                event.setCanceled(true);
                return;
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (!event.getEntity().level().isClientSide && event.getEffect().is(MobEffectInit.WCORP_SIN.getKey()))
            event.getEntity().getPersistentData().remove("wcorp_evade_charges");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        var effect = event.getEffectInstance();
        if (!event.getEntity().level().isClientSide && effect != null && effect.getEffect().is(MobEffectInit.WCORP_SIN.getKey()))
            event.getEntity().getPersistentData().remove("wcorp_evade_charges");
    }
}
