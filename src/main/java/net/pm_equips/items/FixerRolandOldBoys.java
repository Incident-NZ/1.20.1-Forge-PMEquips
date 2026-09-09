package net.pm_equips.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class FixerRolandOldBoys extends SwordItem {
    private static final float HEAL_AMOUNT = 5.0F;

    public FixerRolandOldBoys() {
        super(new CustomTier(), new Properties().durability(1000).attributes(SwordItem.createAttributes(new CustomTier(), 7, -2.5F)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            player.heal(HEAL_AMOUNT);
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



        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    }
}
