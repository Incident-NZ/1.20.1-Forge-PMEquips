package net.pm_equips.items;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.pm_equips.PMERegistryBlock;

public class LCorpEGOR1WingBeat extends SwordItem {
    public LCorpEGOR1WingBeat() {
        super(new CustomTier(), new Properties().durability(800).attributes(SwordItem.createAttributes(new CustomTier(), 7, -3.0f)));
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 800; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }

        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(PMERegistryBlock.BlockItems.ZAYIN_PE_BOX.get()); }
    }
}