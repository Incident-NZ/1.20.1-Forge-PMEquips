package net.pm_equips;

import net.pm_equips.tabs.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TabInit {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PMEquipsMain.MOD_ID);

    public static final RegistryObject<CreativeModeTab> PM_LOBOTOMY_EGO = CREATIVE_TABS.register("pm_lobotomy_ego",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pm_lobotomy_ego"))
                    .icon(() -> ItemStack.EMPTY)
                    .displayItems((param, output) -> {
                        for (Item item : PMLCorpEGOTabs.items) {
                            output.accept(item);
                        }
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> PM_BASE = CREATIVE_TABS.register("pm_base",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pm_base"))
                    .icon(() -> ItemStack.EMPTY)
                    .displayItems((param, output) -> {
                        for (Item item : PMBaseTabs.items) {
                            output.accept(item);
                        }
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> PM_FIXERS = CREATIVE_TABS.register("pm_fixers",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pm_fixers"))
                    .icon(() -> ItemStack.EMPTY)
                    .displayItems((param, output) -> {
                        for (Item item : PMFixersTabs.items) {
                            output.accept(item);
                        }
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> PM_WCORP = CREATIVE_TABS.register("pm_wcorp",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pm_wcorp"))
                    .icon(() -> ItemStack.EMPTY)
                    .displayItems((param, output) -> {
                        for (Item item : PMWingsTabs.items) {
                            output.accept(item);
                        }
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> PM_LIMBUS = CREATIVE_TABS.register("pm_limbus",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pm_limbus"))
                    .icon(() -> ItemStack.EMPTY)
                    .displayItems((param, output) -> {
                        for (Item item : PMLimbusTabs.items) {
                            output.accept(item);
                        }
                    })
                    .build());
}
