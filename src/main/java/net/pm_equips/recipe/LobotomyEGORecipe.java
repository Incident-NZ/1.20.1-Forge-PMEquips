package net.pm_equips.recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LobotomyEGORecipe {
    public record IngredientEntry(Item item, int count) {
        public ItemStack asStack() { return new ItemStack(item, count); }
    }

    private final ResourceLocation id;
    private final ItemStack result;
    private final List<IngredientEntry> ingredients;

    public LobotomyEGORecipe(ResourceLocation id, ItemStack result, List<IngredientEntry> ingredients) {
        if (result.isEmpty()) throw new IllegalArgumentException("Recipe output must not be empty: " + id);
        this.id = id;
        this.result = result.copy();
        Map<Item, Integer> totals = new LinkedHashMap<>();
        for (IngredientEntry ingredient : ingredients) {
            if (ingredient.count() <= 0) throw new IllegalArgumentException("Ingredient count must be positive: " + id);
            totals.merge(ingredient.item(), ingredient.count(), Math::addExact);
        }
        List<IngredientEntry> combined = new ArrayList<>(totals.size());
        totals.forEach((item, count) -> combined.add(new IngredientEntry(item, count)));
        this.ingredients = List.copyOf(combined);
    }

    public ResourceLocation getId() { return id; }
    public ItemStack getResult() { return result.copy(); }
    public List<IngredientEntry> getIngredients() { return ingredients; }

    public boolean matchesInventory(Inventory inventory) {
        for (IngredientEntry ingredient : ingredients) {
            int count = countItem(inventory.items, ingredient.item()) + countItem(inventory.offhand, ingredient.item());
            if (count < ingredient.count()) return false;
        }
        return true;
    }

    public boolean consume(Inventory inventory) {
        if (!matchesInventory(inventory)) return false;
        for (IngredientEntry ingredient : ingredients) {
            int remaining = consumeItem(inventory.items, ingredient.item(), ingredient.count());
            consumeItem(inventory.offhand, ingredient.item(), remaining);
        }
        inventory.setChanged();
        return true;
    }

    private static int countItem(List<ItemStack> stacks, Item item) {
        int total = 0;
        for (ItemStack stack : stacks) if (stack.is(item)) total += stack.getCount();
        return total;
    }

    private static int consumeItem(List<ItemStack> stacks, Item item, int remaining) {
        for (ItemStack stack : stacks) {
            if (remaining <= 0) break;
            if (!stack.is(item)) continue;
            int consumed = Math.min(remaining, stack.getCount());
            stack.shrink(consumed);
            remaining -= consumed;
        }
        return remaining;
    }
}
