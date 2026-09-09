package net.pm_equips;

import net.pm_equips.menu.LobotomyEGOExtractionMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PMERegistryMenu {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, PMEquipsMain.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<LobotomyEGOExtractionMenu>> LOBOTOMY_EGO_EXTRACTION_TABLE = registerMenuType(
            LobotomyEGOExtractionMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(IContainerFactory<T> supplier) {
        return MENUS.register("lobotomy_ego_extraction_table", () -> new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS));
    }
}
