package net.pm_equips.util;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Access to the mod's existing item data keys through the 1.21.1 component system. */
public final class ItemStackData {
    private ItemStackData() {}

    public static CompoundTag copy(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static void set(ItemStack stack, CompoundTag tag) {
        if (tag.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
        else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void update(ItemStack stack, Consumer<CompoundTag> update) {
        CompoundTag tag = copy(stack);
        update.accept(tag);
        set(stack, tag);
    }

    public static int getInt(ItemStack stack, String key) { return copy(stack).getInt(key); }
    public static long getLong(ItemStack stack, String key) { return copy(stack).getLong(key); }
    public static boolean getBoolean(ItemStack stack, String key) { return copy(stack).getBoolean(key); }
    public static boolean contains(ItemStack stack, String key) { return copy(stack).contains(key); }
    public static boolean hasData(ItemStack stack) { return !copy(stack).isEmpty(); }
    public static void putInt(ItemStack stack, String key, int value) { update(stack, tag -> tag.putInt(key, value)); }
    public static void putLong(ItemStack stack, String key, long value) { update(stack, tag -> tag.putLong(key, value)); }
    public static void putBoolean(ItemStack stack, String key, boolean value) { update(stack, tag -> tag.putBoolean(key, value)); }
    public static void putUUID(ItemStack stack, String key, UUID value) { update(stack, tag -> tag.putUUID(key, value)); }
}
