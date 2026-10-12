package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.pm_equips.energy.WeaponEnergyProvider;
import org.jetbrains.annotations.Nullable;

public final class ClassEnergySupport {
    private ClassEnergySupport() {}

    public static int getMaxEnergy(Item item) {
        if (item instanceof WingWCorpArmor) {
            return 5000;
        }
        if (item instanceof WingRCorp4thRabbitArmor) {
            return 10000;
        }
        if (item instanceof WingKCorpAgentArmor || item instanceof WingKCorpOfficerArmor) {
            return 7500;
        }
        return 0;
    }

    public static @Nullable ICapabilityProvider initCapabilities(ItemStack stack) {
        int maxEnergy = getMaxEnergy(stack.getItem());
        return maxEnergy > 0 ? new WeaponEnergyProvider(stack, maxEnergy) : null;
    }

    public static boolean isBarVisible(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY)
                .map(storage -> storage.getMaxEnergyStored() > 0
                        && storage.getEnergyStored() < storage.getMaxEnergyStored())
                .orElse(false);
    }

    public static int getBarWidth(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY)
                .map(storage -> storage.getMaxEnergyStored() == 0 ? 0
                        : Math.round(13.0F * storage.getEnergyStored() / storage.getMaxEnergyStored()))
                .orElse(0);
    }

    public static void appendEnergyTooltip(ItemStack stack, java.util.List<Component> tooltip) {
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(storage ->
                tooltip.add(Component.literal("FE: " + storage.getEnergyStored() + " / " + storage.getMaxEnergyStored())));
    }
}
