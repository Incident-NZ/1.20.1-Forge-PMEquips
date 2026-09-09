package net.pm_equips;

import net.pm_equips.tabs.PMBaseTabs;
import net.pm_equips.tabs.PMFixerWeaponTabs;
import net.pm_equips.tabs.PMLobotomyEGOTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.pm_equips.tabs.PMWingsTabs;

public class PMERegistryTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PMEquipsMain.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> PM_LOBOTOMY_EGO = CREATIVE_TABS.register("pm_lobotomy_ego",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pm_lobotomy_ego"))
                    .icon(() -> ItemStack.EMPTY)
                    .displayItems((param, output) -> {
                        for (Item item : PMLobotomyEGOTabs.items) {
                            output.accept(item);
                        }
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> PM_BASE = CREATIVE_TABS.register("pm_base",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pm_base"))
                    .icon(() -> ItemStack.EMPTY)
                    .displayItems((param, output) -> {
                        for (Item item : PMBaseTabs.items) {
                            output.accept(item);
                        }
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> PM_FIXERS = CREATIVE_TABS.register("pm_fixers",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pm_fixers"))
                    .icon(() -> ItemStack.EMPTY)
                    .displayItems((param, output) -> {
                        for (Item item : PMFixerWeaponTabs.items) {
                            output.accept(item);
                        }
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> PM_WCORP = CREATIVE_TABS.register("pm_wcorp",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pm_wcorp"))
                    .icon(() -> ItemStack.EMPTY)
                    .displayItems((param, output) -> {
                        for (Item item : PMWingsTabs.items) {
                            output.accept(item);
                        }
                    })
                    .build());
}
