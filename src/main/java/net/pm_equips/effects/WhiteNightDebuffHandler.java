package net.pm_equips.effects;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.LobEGOWeapon5WhiteNight;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class WhiteNightDebuffHandler {

    /** 食料/ポーション使用直後の回復を短時間ブロック */
    private static final Map<UUID, Integer> BLOCK_HEAL_TICKS = new ConcurrentHashMap<>();

    private static boolean hasWeapon(Player player) {
        return LobEGOWeapon5WhiteNight.hasInInventory(player);
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!hasWeapon(player)) return;

        ItemStack stack = event.getItem();
        boolean isFood = stack.getFoodProperties(player) != null;
        boolean isPotion = stack.getItem() instanceof PotionItem;

        if (isFood || isPotion) {
            // 数tickの間 LivingHealEvent をキャンセル（再生系ポーション対策）
            BLOCK_HEAL_TICKS.put(player.getUUID(), 40);
        }
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!hasWeapon(player)) return;

        Integer ticks = BLOCK_HEAL_TICKS.get(player.getUUID());
        if (ticks != null && ticks > 0) {
            event.setCanceled(true);
        }
    }

    /** 即時回復ポーション（治癒）を無効化 */
    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasWeapon(player)) return;

        var effect = event.getEffectInstance().getEffect();
        if (effect == MobEffects.HEAL || effect == MobEffects.REGENERATION) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    /** ブロック残り時間の更新（プレイヤーtick） */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) return;

        UUID id = event.getEntity().getUUID();
        Integer ticks = BLOCK_HEAL_TICKS.get(id);
        if (ticks == null) return;

        if (ticks <= 1) {
            BLOCK_HEAL_TICKS.remove(id);
        } else {
            BLOCK_HEAL_TICKS.put(id, ticks - 1);
        }
    }
}
