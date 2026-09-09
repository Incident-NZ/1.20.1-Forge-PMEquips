package net.pm_equips.energy;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ItemEnergyStorage implements IEnergyStorage
{
    private final CompoundTag tag;

    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    public ItemEnergyStorage(
            CompoundTag tag,
            int capacity,
            int maxReceive,
            int maxExtract)
    {
        this.tag = tag;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int receiveEnergy(
            int maxReceive,
            boolean simulate)
    {
        int energy = getEnergyStored();

        int received = Math.min(
                capacity - energy,
                Math.min(this.maxReceive, maxReceive));

        if(!simulate)
        {
            setEnergyStored(energy + received);
        }

        return received;
    }

    @Override
    public int extractEnergy(
            int maxExtract,
            boolean simulate)
    {
        int energy = getEnergyStored();

        int extracted = Math.min(
                energy,
                Math.min(this.maxExtract, maxExtract));

        if(!simulate)
        {
            setEnergyStored(energy - extracted);
        }

        return extracted;
    }

    @Override
    public int getEnergyStored()
    {
        return tag.getInt("Energy");
    }

    public void setEnergyStored(int value)
    {
        tag.putInt(
                "Energy",
                Math.max(
                        0,
                        Math.min(capacity, value)
                )
        );
    }

    @Override
    public int getMaxEnergyStored()
    {
        return capacity;
    }

    @Override
    public boolean canExtract()
    {
        return true;
    }

    @Override
    public boolean canReceive()
    {
        return true;
    }
}
