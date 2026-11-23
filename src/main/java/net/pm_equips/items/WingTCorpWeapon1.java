package net.pm_equips.items;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.pm_equips.ItemInit;

public class WingTCorpWeapon1 extends SwordItem {
    public WingTCorpWeapon1() {
        super(new ClockTier(2000), 10, -2.9F, new Properties().stacksTo(1));
    }

    private static class ClockTier implements Tier {
        private final int uses;
        private ClockTier(int uses) { this.uses = uses; }
        @Override public int getUses() { return uses; }
        @Override public float getSpeed() { return 4.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override public int getLevel() { return 1; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(ItemInit.TCORP_CLOCK.get()); }
    }
}
