package net.pm_equips.items;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class ClassEnergySupport {
    private ClassEnergySupport() {}

    public static int getMaxEnergy(Item item) {
        if (item instanceof WingWCorpArmor || item instanceof WingWCorpWeapon1
                || item instanceof WingWCorpWeapon2 || item instanceof WingWCorpWeapon3) return 5000;
        if (item instanceof WingRCorp4thRabbitArmor || item instanceof WingRCorp4thRabbitWeapon1
                || item instanceof WingRCorp4thRabbitWeapon2) return 10000;
        if (item instanceof WingKCorpAgentArmor || item instanceof WingKCorpOfficerArmor
                || item instanceof WingKCorpWeapon1 || item instanceof WingKCorpWeapon2
                || item instanceof WingKCorpWeapon3) return 7500;
        return 0;
    }

    public static Optional<IEnergyStorage> energy(ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM));
    }

    public static boolean isBarVisible(ItemStack stack) {
        return energy(stack).map(storage -> storage.getMaxEnergyStored() > 0
                && storage.getEnergyStored() < storage.getMaxEnergyStored()).orElse(false);
    }

    public static int getBarWidth(ItemStack stack) {
        return energy(stack).map(storage -> storage.getMaxEnergyStored() == 0 ? 0
                : Math.round(13.0F * storage.getEnergyStored() / storage.getMaxEnergyStored())).orElse(0);
    }

    public static void appendEnergyTooltip(ItemStack stack, List<Component> tooltip) {
        energy(stack).ifPresent(storage -> tooltip.add(Component.literal(
                "FE: " + storage.getEnergyStored() + " / " + storage.getMaxEnergyStored())));
    }
}
