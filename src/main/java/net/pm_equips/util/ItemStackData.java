package net.pm_equips.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

public final class ItemStackData {
    private ItemStackData() {
    }

    public static CompoundTag copy(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static int getInt(ItemStack stack, String key) {
        return copy(stack).getInt(key);
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        return copy(stack).getBoolean(key);
    }

    public static boolean contains(ItemStack stack, String key) {
        return copy(stack).contains(key);
    }

    public static void update(ItemStack stack, Consumer<CompoundTag> updater) {
        CompoundTag tag = copy(stack);
        updater.accept(tag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void putInt(ItemStack stack, String key, int value) {
        update(stack, tag -> tag.putInt(key, value));
    }

    public static void putBoolean(ItemStack stack, String key, boolean value) {
        update(stack, tag -> tag.putBoolean(key, value));
    }

    public static void set(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
