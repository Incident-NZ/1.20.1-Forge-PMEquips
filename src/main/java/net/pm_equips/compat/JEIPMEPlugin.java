package net.pm_equips.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.pm_equips.BlockInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.recipe.LobotomyEGORecipeManager;

import java.util.List;

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
                List.copyOf(LobotomyEGORecipeManager.getAll())
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

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEIEGORecipeSync.setRuntime(jeiRuntime);
        JEIEGORecipeSync.syncToJei();
    }
}
