package net.pm_equips.energy;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.pm_equips.PMERegistryItem;

public final class EnergyCapabilities {
    private EnergyCapabilities() {}

    public static void register(RegisterCapabilitiesEvent event) {
        registerAll(event, 7500, PMERegistryItem.KCORP_WEAPON_1, PMERegistryItem.KCORP_WEAPON_2,
                PMERegistryItem.KCORP_WEAPON_3, PMERegistryItem.KCORP_OFFICER_ARMOR_L1,
                PMERegistryItem.KCORP_OFFICER_ARMOR_L2, PMERegistryItem.KCORP_AGENT_ARMOR_L3,
                PMERegistryItem.KCORP_AGENT_ARMOR_L4, PMERegistryItem.KCORP_AGENT_ARMOR_L5);
        registerAll(event, 5000, PMERegistryItem.WCORP_WEAPON_1, PMERegistryItem.WCORP_WEAPON_2,
                PMERegistryItem.WCORP_WEAPON_3, PMERegistryItem.WCORP_ARMOR_L1, PMERegistryItem.WCORP_ARMOR_L2,
                PMERegistryItem.WCORP_ARMOR_L3, PMERegistryItem.WCORP_ARMOR_L4, PMERegistryItem.WCORP_ARMOR_L5);
        registerAll(event, 10000, PMERegistryItem.RCORP_RABBIT_RIFLE, PMERegistryItem.RCORP_RABBIT_KNIFE,
                PMERegistryItem.RCORP_RABBIT_ARMOR_L1, PMERegistryItem.RCORP_RABBIT_ARMOR_L2,
                PMERegistryItem.RCORP_RABBIT_ARMOR_L3, PMERegistryItem.RCORP_RABBIT_ARMOR_L4,
                PMERegistryItem.RCORP_RABBIT_ARMOR_L5);
    }

    @SafeVarargs
    private static void registerAll(RegisterCapabilitiesEvent event, int capacity,
                                    DeferredHolder<Item, Item>... holders) {
        for (DeferredHolder<Item, Item> holder : holders) {
            Item item = holder.get();
            event.registerItem(Capabilities.EnergyStorage.ITEM,
                    (ItemStack stack, Void context) -> new ItemStackEnergyStorage(stack, capacity), item);
        }
    }
}
