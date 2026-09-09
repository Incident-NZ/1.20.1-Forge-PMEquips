package net.pm_equips.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Optional;

public final class EnergyUtils {
    private EnergyUtils() {
    }

    public static Optional<IEnergyStorage> get(ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM));
    }
}
