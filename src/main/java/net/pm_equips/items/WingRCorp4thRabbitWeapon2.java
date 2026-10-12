package net.pm_equips.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.pm_equips.ItemInit;
import net.pm_equips.energy.WeaponEnergyProvider;

import java.util.List;

public class WingRCorp4thRabbitWeapon2 extends SwordItem {

    public static final int MAX_ENERGY = 10000;

    public static final int ENERGY_PER_HIT = 10;

    public WingRCorp4thRabbitWeapon2() {
        super(
                new CustomTier(),
                11,
                -2.5F,
                new Properties()
        );
    }

    @Override
    public ICapabilityProvider initCapabilities(
            ItemStack stack,
            CompoundTag nbt)
    {
        return new WeaponEnergyProvider(
                stack,
                MAX_ENERGY
        );
    }

    public static int getEnergy(
            ItemStack stack)
    {
        return stack.getCapability(
                        ForgeCapabilities.ENERGY)
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
        stack.getCapability(
                        ForgeCapabilities.ENERGY)
                .ifPresent(storage ->
                        storage.extractEnergy(
                                ENERGY_PER_HIT,
                                false));
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Level level,
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
        public int getLevel() {
            return 1;
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
