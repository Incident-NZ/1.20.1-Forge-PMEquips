package net.pm_equips.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.pm_equips.MobEffectInit;

public class WeaponRolandRanga extends SwordItem {

    public WeaponRolandRanga() {
        super(new CustomTier(), 6, -2.2F, new Properties().durability(1000));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player) {
            target.addEffect(new MobEffectInstance(MobEffectInit.BLEED.get(), 200, 0, false, false, false));
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    private static class CustomTier implements Tier {
        @Override
        public int getUses() {
            return 1000;
        }

        @Override
        public float getSpeed() {
            return 4.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    }
}
