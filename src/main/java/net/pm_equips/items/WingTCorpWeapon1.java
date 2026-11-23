package net.pm_equips.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.pm_equips.ItemInit;

public class WingTCorpWeapon1 extends SwordItem {
    public WingTCorpWeapon1() {
        super(new ClockTier(2000), new Properties().stacksTo(1).attributes(SwordItem.createAttributes(new ClockTier(2000), 10, -2.9F)));
    }

    private static class ClockTier implements Tier {
        private final int uses;
        private ClockTier(int uses) { this.uses = uses; }
        @Override public int getUses() { return uses; }
        @Override public float getSpeed() { return 4.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(ItemInit.TCORP_CLOCK.get()); }
    }
}
