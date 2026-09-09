package net.pm_equips.items;

import net.minecraft.world.entity.LivingEntity;

import net.pm_equips.util.ItemStackData;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class FixerKaliMimicry extends SwordItem {
    private static final String ATTACK_COUNT_TAG = "mimicry_attack_count";
    private static final String DAMAGE_ACCUM_TAG = "mimicry_damage_accum";

    public FixerKaliMimicry() {
        super(new CustomTier(), new Item.Properties().durability(8000).attributes(SwordItem.createAttributes(new CustomTier(), 25, -2.2f)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) return;

        ItemStack main = player.getMainHandItem();
        boolean has = main.getItem() instanceof FixerKaliMimicry;

        if (has) {
            // 攻撃力上昇 III -> amplifier 2
            MobEffectInstance atk = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 12, 2, false, false, true);
            // 耐性 III -> amplifier 2
            MobEffectInstance res = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 12, 2, false, false, true);
            player.addEffect(atk);
            player.addEffect(res);
        } else {
            if (player.hasEffect(MobEffects.DAMAGE_BOOST)) player.removeEffect(MobEffects.DAMAGE_BOOST);
            if (player.hasEffect(MobEffects.DAMAGE_RESISTANCE)) player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
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
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    public static void onDamageDealt(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof FixerKaliMimicry)) return;

        var tag = ItemStackData.copy(stack);
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
        ItemStackData.set(stack, tag);
        tag.putFloat(DAMAGE_ACCUM_TAG, accum);
        ItemStackData.set(stack, tag);
    }

    public static void onPlayerAttacked(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof FixerKaliMimicry &&
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

        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    }
}


