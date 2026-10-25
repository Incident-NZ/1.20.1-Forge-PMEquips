package net.pm_equips.energy;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.pm_equips.ItemInit;
import net.pm_equips.items.ClassEnergySupport;

public final class WeaponEnergyProvider {
    private WeaponEnergyProvider() {}

    public static void register(RegisterCapabilitiesEvent event) {
        for (var holder : ItemInit.ITEMS.getEntries()) {
            Item item = holder.get();
            int capacity = ClassEnergySupport.getMaxEnergy(item);
            if (capacity > 0) {
                event.registerItem(Capabilities.EnergyStorage.ITEM,
                        (stack, context) -> new ItemStackEnergyStorage(stack, capacity), item);
            }
        }
    }
}
