package net.pm_equips.items;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pm_equips.ItemInit;

public class WingRCorp4thRabbitWeapon2 extends SwordItem {

    public static final int MAX_ENERGY = 10000;

    public static final int ENERGY_PER_HIT = 10;

    public WingRCorp4thRabbitWeapon2() {
        super(new CustomTier(), new Properties().attributes(SwordItem.createAttributes(new CustomTier(), 11, -2.5F)));
    }

    public static int getEnergy(
            ItemStack stack)
    {
        return Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM))
                .map(
                        IEnergyStorage::getEnergyStored)
                .orElse(0);
    }

    public static boolean hasEnoughEnergy(
            ItemStack stack)
    {
        return getEnergy(stack)
                >= ENERGY_PER_HIT;
    }

    public static void consumeEnergy(
            ItemStack stack)
    {
        Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM))
                .ifPresent(storage ->
                        storage.extractEnergy(
                                ENERGY_PER_HIT,
                                false));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level,
            List<Component> tooltip,
            TooltipFlag flag)
    {
        tooltip.add(Component.literal(
                "FE: "
                        + getEnergy(stack)
                        + " / "
                        + MAX_ENERGY));

        super.appendHoverText(
                stack,
                level,
                tooltip,
                flag);
    }

    private static class CustomTier
            implements Tier {

        @Override
        public int getUses() {
            return 3000;
        }

        @Override
        public float getSpeed() {
            return 4.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_STONE_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(
                    ItemInit.RCORP_BATTERY.get()
            );
        }
    }
}
