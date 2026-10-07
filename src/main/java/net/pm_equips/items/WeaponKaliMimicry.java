package net.pm_equips.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.pm_equips.MobEffectInit;

public class WeaponKaliMimicry extends SwordItem {
    private static final String ATTACK_COUNT_TAG = "mimicry_attack_count";
    private static final String DAMAGE_ACCUM_TAG = "mimicry_damage_accum";

    public WeaponKaliMimicry() {
        super(new CustomTier(), 25, -2.2f, new Item.Properties().durability(8000));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) return;

        ItemStack main = player.getMainHandItem();
        boolean has = main.getItem() instanceof WeaponKaliMimicry;

        if (has) {
            // パワー3 -> 攻撃力30%増加
            MobEffectInstance atk = new MobEffectInstance(MobEffectInit.POWER.get(), 12, 2, false, false, true);
            player.addEffect(atk);
        } else {
            if (player.hasEffect(MobEffectInit.POWER.get())) player.removeEffect(MobEffectInit.POWER.get());
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @SubscribeEvent
    public static void onDamageDealt(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof WeaponKaliMimicry)) return;

        var tag = stack.getOrCreateTag();
        int count = tag.getInt(ATTACK_COUNT_TAG);
        float accum = tag.getFloat(DAMAGE_ACCUM_TAG);

        count++;
        accum += event.getAmount();

        if (count >= 5) {
            float heal = accum * 0.25f;
            player.heal(heal);
            count = 0;
            accum = 0;
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        tag.putInt(ATTACK_COUNT_TAG, count);
        tag.putFloat(DAMAGE_ACCUM_TAG, accum);
    }

    @SubscribeEvent
    public static void onPlayerAttacked(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof WeaponKaliMimicry &&
                    player.isUsingItem() &&
                    !player.getCooldowns().isOnCooldown(stack.getItem())) {

                event.setCanceled(true);
                player.level().playSound(null, player.blockPosition(), SoundEvents.ANVIL_FALL, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.getCooldowns().addCooldown(stack.getItem(), 20 * 15); // 15秒
                player.stopUsingItem();
            }
        }
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 5000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    }
}


