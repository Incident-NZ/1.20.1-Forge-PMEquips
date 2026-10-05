package net.pm_equips.items;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class LibrarySword extends SwordItem {
    public LibrarySword() {
        super(new CustomTier(), 5, -2.5f, new Properties().durability(800));
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 800; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(Items.IRON_INGOT); }
    }
}