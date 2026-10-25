package net.pm_equips.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.pm_equips.MobEffectInit;

/**
 * LCCB Baton
 * 攻撃力8 / 攻撃速度1.2 / 耐久3000
 * 的中時: PARALYSIS Amp0 を5秒付与
 */
public class LCCBBaton extends SwordItem {

    private static final int PARALYSIS_DURATION = 100; // 5秒
    private static final int PARALYSIS_AMPLIFIER = 0;  // Amp0

    public LCCBBaton() {
        // 攻撃力: 1 + 7 = 8 / 攻撃速度: 4.0 + (-2.8) = 1.2
        super(new CustomTier(), new Properties().durability(3000).attributes(SwordItem.createAttributes(new CustomTier(), 7, -2.8F)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (result && !attacker.level().isClientSide()) {
            target.hurtTime = 0;
            target.invulnerableTime = 0;

            target.addEffect(new MobEffectInstance(
                    MobEffectInit.PARALYSIS,
                    PARALYSIS_DURATION,
                    PARALYSIS_AMPLIFIER,
                    false,
                    true,
                    true
            ));
        }

        return result;
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 3000; }
        @Override public float getSpeed() { return 4.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }
        @Override public int getEnchantmentValue() { return 10; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(Items.REDSTONE); // 修理素材があれば Ingredient.of(...) に変更
        }
    }
}
