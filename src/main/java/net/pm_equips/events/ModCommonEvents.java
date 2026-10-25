package net.pm_equips.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.pm_equips.items.FixerColorRolandWeapon10;
import net.pm_equips.items.LobEGOWeapon4Lamp;

@EventBusSubscriber(modid = "pm_equips")
public class ModCommonEvents {
    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity target = event.getEntity();
        float modified = LobEGOWeapon4Lamp.applyExtraDamage(
                target,
                event.getSource(),
                event.getAmount()
        );
        event.setAmount(modified);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        // 攻撃者が LivingEntity のときだけカウンター対象
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            // 落下など無生物ダメージはガードしない（必要なら変更）
            return;
        }
        if (attacker == player) return;

        if (FixerColorRolandWeapon10.tryGuard(player, attacker)) {
            event.setCanceled(true); // 被ダメージ無効
        }
    }
}
