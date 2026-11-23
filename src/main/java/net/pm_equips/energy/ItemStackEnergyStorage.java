package net.pm_equips.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemStackEnergyStorage implements IEnergyStorage {

    private final ItemStack stack;
    private final int capacity;

    public ItemStackEnergyStorage(
            ItemStack stack,
            int capacity
    ) {
        this.stack = stack;
        this.capacity = capacity;
    }

    private CompoundTag tag() {
        return stack.getOrCreateTag();
    }

    private int getStored() {
        return Math.max(0, Math.min(capacity, tag().getInt("Energy")));
    }

    private void setStored(int amount) {

        tag().putInt(
                "Energy",
                Math.max(
                        0,
                        Math.min(capacity, amount)
                )
        );
    }

    @Override
    public int receiveEnergy(
            int maxReceive,
            boolean simulate
    ) {

        int stored = getStored();

        int accepted = Math.min(
                capacity - stored,
                Math.max(0, maxReceive)
        );

        if(!simulate) {
            setStored(stored + accepted);
        }

        return accepted;
    }

    @Override
    public int extractEnergy(
            int maxExtract,
            boolean simulate
    ) {

        int stored = getStored();

        int extracted = Math.min(
                stored,
                Math.max(0, maxExtract)
        );

        if(!simulate) {
            setStored(stored - extracted);
        }

        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return getStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
