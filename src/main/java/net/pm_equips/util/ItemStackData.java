package net.pm_equips.util;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/** Feature data uses the native 1.20.1 item tag and preserves unrelated item data. */
public final class ItemStackData {
    private ItemStackData() {}
    public static CompoundTag copy(ItemStack stack) { return stack.hasTag() ? stack.getTag().copy() : new CompoundTag(); }
    public static void set(ItemStack stack, CompoundTag tag) { stack.setTag(tag.isEmpty() ? null : tag); }
    public static void update(ItemStack stack, Consumer<CompoundTag> update) { CompoundTag tag=copy(stack);update.accept(tag);set(stack,tag); }
    public static int getInt(ItemStack stack,String key) { return stack.hasTag() ? stack.getTag().getInt(key) : 0; }
    public static long getLong(ItemStack stack,String key) { return stack.hasTag() ? stack.getTag().getLong(key) : 0; }
    public static boolean getBoolean(ItemStack stack,String key) { return stack.hasTag() && stack.getTag().getBoolean(key); }
    public static boolean contains(ItemStack stack,String key) { return stack.hasTag() && stack.getTag().contains(key); }
    public static boolean hasData(ItemStack stack) { return stack.hasTag(); }
    public static void putInt(ItemStack stack,String key,int value) { update(stack,tag->tag.putInt(key,value)); }
    public static void putLong(ItemStack stack,String key,long value) { update(stack,tag->tag.putLong(key,value)); }
    public static void putBoolean(ItemStack stack,String key,boolean value) { update(stack,tag->tag.putBoolean(key,value)); }
    public static void putUUID(ItemStack stack,String key,UUID value) { update(stack,tag->tag.putUUID(key,value)); }
}
