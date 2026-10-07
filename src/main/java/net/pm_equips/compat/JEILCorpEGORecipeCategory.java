package net.pm_equips.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.pm_equips.BlockInit; // 作業台ブロックの Item に合わせて変更
import net.pm_equips.PMEquipsMain;
import net.pm_equips.recipe.LobotomyEGORecipe;
import org.jetbrains.annotations.Nullable;

public class JEILCorpEGORecipeCategory implements IRecipeCategory<LobotomyEGORecipe> {

    public static final RecipeType<LobotomyEGORecipe> TYPE =
            RecipeType.create(PMEquipsMain.MOD_ID, "ego_craft", LobotomyEGORecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public JEILCorpEGORecipeCategory(IGuiHelper guiHelper) {
        // 176x85 程度の空白背景（テクスチャがあるなら blit 用に差し替え）
        this.background = guiHelper.createBlankDrawable(176, 85);
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(BlockInit.BlockItems.EGO_CRAFT_TABLE.get()) // 作業台アイテム
        );
    }

    @Override
    public RecipeType<LobotomyEGORecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.pm_equips.lobotomy_ego_craft_table.craft");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LobotomyEGORecipe recipe, IFocusGroup focuses) {
        // 材料（最大9個を3x3で表示する例）
        var ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size() && i < 9; i++) {
            int x = 10 + (i % 3) * 18;
            int y = 10 + (i / 3) * 18;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addItemStack(ingredients.get(i).asStack());
        }

        // 成果物
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 28)
                .addItemStack(recipe.getResult());
    }
}