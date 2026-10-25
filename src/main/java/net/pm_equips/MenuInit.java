package net.pm_equips;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pm_equips.menu.LobotomyEGOCraftMenu;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, PMEquipsMain.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<LobotomyEGOExtractionMenu>> LOBOTOMY_EGO_EXTRACTION_TABLE = registerMenuType(
            LobotomyEGOExtractionMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(IContainerFactory<T> supplier) {
        return MENUS.register("lobotomy_ego_extraction_table", () -> IMenuTypeExtension.create(supplier));
    }

    public static final DeferredHolder<MenuType<?>, MenuType<LobotomyEGOCraftMenu>> GUN_WORKBENCH =
            MENUS.register("gun_workbench", () -> IMenuTypeExtension.create(LobotomyEGOCraftMenu::new));
}
