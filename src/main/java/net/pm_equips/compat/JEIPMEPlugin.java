package net.pm_equips.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.pm_equips.BlockInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.recipe.LobotomyEGORecipeManager;

@JeiPlugin
public class JEIPMEPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(PMEquipsMain.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new JEILCorpEGORecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
                JEILCorpEGORecipeCategory.TYPE,
                LobotomyEGORecipeManager.getAll().stream().toList()
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // JEIで「このブロックで作れる」と表示される触媒
        registration.addRecipeCatalyst(
                new ItemStack(BlockInit.BlockItems.EGO_CRAFT_TABLE.get()),
                JEILCorpEGORecipeCategory.TYPE
        );
    }
}
