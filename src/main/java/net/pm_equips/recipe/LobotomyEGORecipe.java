package net.pm_equips.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LobotomyEGORecipe {

    public record IngredientEntry(Item item, int count) {
        public ItemStack asStack() {
            return new ItemStack(item, count);
        }
    }

    private final ResourceLocation id;
    private final ItemStack result;
    private final List<IngredientEntry> ingredients;

    public LobotomyEGORecipe(ResourceLocation id, ItemStack result, List<IngredientEntry> ingredients) {
        this.id = id;
        this.result = result;
        this.ingredients = List.copyOf(ingredients);
    }

    public ResourceLocation getId() {
        return id;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public List<IngredientEntry> getIngredients() {
        return ingredients;
    }

    /** プレイヤーインベントリに材料が足りるか */
    public boolean matchesInventory(net.minecraft.world.entity.player.Inventory inv) {
        for (IngredientEntry entry : ingredients) {
            if (countItem(inv, entry.item()) < entry.count()) {
                return false;
            }
        }
        return true;
    }

    /** 材料を消費（足りる前提で呼ぶ） */
    public void consume(net.minecraft.world.entity.player.Inventory inv) {
        for (IngredientEntry entry : ingredients) {
            int need = entry.count();
            for (int i = 0; i < inv.items.size() && need > 0; i++) {
                ItemStack stack = inv.items.get(i);
                if (stack.is(entry.item())) {
                    int take = Math.min(need, stack.getCount());
                    stack.shrink(take);
                    need -= take;
                }
            }
        }
    }

    private static int countItem(net.minecraft.world.entity.player.Inventory inv, Item item) {
        int total = 0;
        for (ItemStack stack : inv.items) {
            if (stack.is(item)) {
                total += stack.getCount();
            }
        }
        // オフハンドも見る場合
        if (inv.offhand.getFirst().is(item)) {
            total += inv.offhand.getFirst().getCount();
        }
        return total;
    }
}
