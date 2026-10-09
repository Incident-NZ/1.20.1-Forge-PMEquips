package net.pm_equips.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.items.EGOW4Lamp;
import net.pm_equips.items.WeaponRolandWheels;

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

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        // 攻撃者が LivingEntity のときだけカウンター対象
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            // 落下など無生物ダメージはガードしない（必要なら変更）
            return;
        }
        if (attacker == player) return;

        if (WeaponRolandWheels.tryGuard(player, attacker)) {
            event.setCanceled(true); // 被ダメージ無効
        }
    }
}
