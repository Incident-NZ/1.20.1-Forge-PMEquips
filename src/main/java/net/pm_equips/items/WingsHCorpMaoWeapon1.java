package net.pm_equips.items;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.pm_equips.PMERegistryItem;

public class WingsHCorpMaoWeapon1 extends SwordItem {
    public WingsHCorpMaoWeapon1() {
        super(new CustomTier(), new Properties().attributes(SwordItem.createAttributes(new CustomTier(), 8, -2.6f)));
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 2000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }

        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(PMERegistryItem.HCORP_BOLUS_MAO.get()); }
    }
}