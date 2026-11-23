package net.pm_equips.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.effect.MobEffectInstance;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;

public class WingTCorpWeapon4 extends SwordItem {
    public WingTCorpWeapon4() {
        super(new ClockTier(), new Properties().attributes(SwordItem.createAttributes(new ClockTier(), 16, -2.2F)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!attacker.level().isClientSide)
            target.addEffect(new MobEffectInstance(MobEffectInit.BIND, 60, 1), attacker);
        return result;
    }

    private static class ClockTier implements Tier {
        @Override public int getUses() { return 3000; }
        @Override public float getSpeed() { return 4.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(ItemInit.TCORP_CLOCK.get()); }
    }
}
