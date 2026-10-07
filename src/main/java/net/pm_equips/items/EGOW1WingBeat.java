package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.pm_equips.BlockInit;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EGOW1WingBeat extends SwordItem {
    public EGOW1WingBeat() {
        super(new CustomTier(), 7, -3.0f, new Properties().durability(800));
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 800; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(BlockInit.BlockItems.ZAYIN_PE_BOX.get()); }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.WINGBEAT_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}