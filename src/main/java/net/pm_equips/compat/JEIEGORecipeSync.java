package net.pm_equips.compat;

import java.util.List;
import mezz.jei.api.runtime.IJeiRuntime;
import net.neoforged.fml.ModList;
import net.pm_equips.recipe.LobotomyEGORecipeManager;

public class JEIEGORecipeSync {
    private static IJeiRuntime runtime;

    public static void setRuntime(IJeiRuntime r) {
        runtime = r;
    }

    public static void syncToJei() {
        if (runtime == null) return;
        if (!ModList.get().isLoaded("jei")) return;

        var manager = runtime.getRecipeManager();
        // いったん消して入れ直す
        manager.hideRecipes(
                JEILCorpEGORecipeCategory.TYPE,
                List.copyOf(manager.createRecipeLookup(JEILCorpEGORecipeCategory.TYPE)
                        .get().toList())
        );
        manager.addRecipes(
                JEILCorpEGORecipeCategory.TYPE,
                LobotomyEGORecipeManager.getClientRecipes()
        );
    }
}
