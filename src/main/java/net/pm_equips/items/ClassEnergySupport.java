package net.pm_equips.items;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.pm_equips.energy.WeaponEnergyProvider;
import org.jetbrains.annotations.Nullable;

public final class ClassEnergySupport {
    private ClassEnergySupport() {}

    public static int getMaxEnergy(Item item) {
        if (item instanceof WingWCorpArmor || item instanceof WingWCorpWeapon1
                || item instanceof WingWCorpWeapon2 || item instanceof WingWCorpWeapon3) {
            return 5000;
        }
        if (item instanceof WingRCorpArmor3) return 20000;
        if (item instanceof WingRCorpArmor1 || item instanceof WingRCorpArmor2
                || item instanceof WingRCorpWeapon1 || item instanceof WingRCorpWeapon2) {
            return 10000;
        }
        if (item instanceof WingKCorpArmor2 || item instanceof WingKCorpArmor1
                || item instanceof WingKCorpWeapon1 || item instanceof WingKCorpWeapon2 || item instanceof WingKCorpWeapon3) {
            return 7500;
        }
        return 0;
    }

    public static @Nullable ICapabilityProvider initCapabilities(ItemStack stack) {
        int maxEnergy = getMaxEnergy(stack.getItem());
        return maxEnergy > 0 ? new WeaponEnergyProvider(stack, maxEnergy) : null;
    }

    public static Optional<IEnergyStorage> energy(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).resolve();
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

    public static void appendEnergyTooltip(ItemStack stack, List<Component> tooltip) {
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(storage ->
                tooltip.add(Component.literal("FE: " + storage.getEnergyStored() + " / " + storage.getMaxEnergyStored())));
    }
}
