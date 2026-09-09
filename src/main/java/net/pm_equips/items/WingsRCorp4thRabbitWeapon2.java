package net.pm_equips.items;

import net.minecraft.world.item.Item;


import net.pm_equips.energy.EnergyUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pm_equips.PMERegistryItem;

import java.util.List;

public class WingsRCorp4thRabbitWeapon2 extends SwordItem {

    public static final int MAX_ENERGY = 10000;

    public static final int ENERGY_PER_HIT = 10;

    public WingsRCorp4thRabbitWeapon2() {
        this(new CustomTier());
    }

    private WingsRCorp4thRabbitWeapon2(CustomTier tier) {
        super(tier, new Properties()
                .attributes(SwordItem.createAttributes(tier, 11, -2.5F)));
    }

        public static int getEnergy(
            ItemStack stack)
    {
        return EnergyUtils.get(stack)
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
        EnergyUtils.get(stack)
                .ifPresent(storage ->
                        storage.extractEnergy(
                                ENERGY_PER_HIT,
                                false));
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
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
                context,
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



        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(
                    PMERegistryItem.RCORP_BATTERY.get()
            );
        }
    }
}
