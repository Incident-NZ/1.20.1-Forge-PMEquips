package net.pm_equips.energy;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeaponEnergyProvider implements ICapabilityProvider {

    private final LazyOptional<IEnergyStorage> energy;

    public WeaponEnergyProvider(
            ItemStack stack,
            int capacity
    ) {

        this.energy = LazyOptional.of(
                () -> new ItemStackEnergyStorage(
                        stack,
                        capacity
                )
        );
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(
            @NotNull Capability<T> cap,
            @Nullable Direction side
    ) {

        return cap == ForgeCapabilities.ENERGY
                ? energy.cast()
                : LazyOptional.empty();
    }

    public void invalidate() {
        energy.invalidate();
    }
}
