package net.pm_equips.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pm_equips.util.ItemStackData;

public final class ItemStackEnergyStorage implements IEnergyStorage {
    private final ItemStack stack;
    private final int capacity;

    public ItemStackEnergyStorage(ItemStack stack, int capacity) {
        this.stack = stack;
        this.capacity = Math.max(0, capacity);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int stored = getEnergyStored();
        int accepted = Math.clamp(maxReceive, 0, capacity - stored);
        if (!simulate && accepted > 0) ItemStackData.putInt(stack, "Energy", stored + accepted);
        return accepted;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int stored = getEnergyStored();
        int extracted = Math.clamp(maxExtract, 0, stored);
        if (!simulate && extracted > 0) ItemStackData.putInt(stack, "Energy", stored - extracted);
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return Math.clamp(ItemStackData.getInt(stack, "Energy"), 0, capacity);
    }

    @Override public int getMaxEnergyStored() { return capacity; }
    @Override public boolean canExtract() { return true; }
    @Override public boolean canReceive() { return true; }
}
