package net.pm_equips.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.items.EGOW4Lamp;

@Mod.EventBusSubscriber(modid = "pm_equips")
public class ModCommonEvents {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity target = event.getEntity();
        float modified = EGOW4Lamp.applyExtraDamage(
                target,
                event.getSource(),
                event.getAmount()
        );
        event.setAmount(modified);
    }
}
