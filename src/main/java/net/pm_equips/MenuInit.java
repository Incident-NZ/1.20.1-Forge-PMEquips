package net.pm_equips;

import net.pm_equips.menu.LobotomyEGOCraftMenu;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, PMEquipsMain.MOD_ID);

    public static final RegistryObject<MenuType<LobotomyEGOExtractionMenu>> LOBOTOMY_EGO_EXTRACTION_TABLE = registerMenuType(
            LobotomyEGOExtractionMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> supplier) {
        return MENUS.register("lobotomy_ego_extraction_table", () -> IForgeMenuType.create(supplier));
    }

    public static final RegistryObject<MenuType<LobotomyEGOCraftMenu>> GUN_WORKBENCH =
            MENUS.register("gun_workbench", () -> IForgeMenuType.create(LobotomyEGOCraftMenu::new));
}
