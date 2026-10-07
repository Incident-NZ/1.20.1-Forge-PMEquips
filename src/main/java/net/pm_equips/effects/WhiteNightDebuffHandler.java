package net.pm_equips.effects;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.EGOW5WhiteNight;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class WhiteNightDebuffHandler {

    /** 食料/ポーション使用直後の回復を短時間ブロック */
    private static final Map<UUID, Integer> BLOCK_HEAL_TICKS = new ConcurrentHashMap<>();

    private static boolean hasWeapon(Player player) {
        return EGOW5WhiteNight.hasInInventory(player);
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!hasWeapon(player)) return;

        ItemStack stack = event.getItem();
        boolean isFood = stack.getItem().isEdible();
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
            event.setResult(Event.Result.DENY);
        }
    }

    /** ブロック残り時間の更新（プレイヤーtick） */
    @SubscribeEvent
    public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;

        UUID id = event.player.getUUID();
        Integer ticks = BLOCK_HEAL_TICKS.get(id);
        if (ticks == null) return;

        if (ticks <= 1) {
            BLOCK_HEAL_TICKS.remove(id);
        } else {
            BLOCK_HEAL_TICKS.put(id, ticks - 1);
        }
    }
}
