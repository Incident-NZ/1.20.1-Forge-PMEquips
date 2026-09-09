package net.pm_equips.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.pm_equips.PMERegistryBlockEntity;
import net.pm_equips.PMERegistryBlock;
import net.pm_equips.PMERegistryTag;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LobotomyEGOExtractionTableBlockEntity extends BlockEntity implements MenuProvider, net.minecraft.world.Container {
    public static final int INPUT_SLOT_START = 0;
    public static final int INPUT_SLOT_COUNT = 9;
    public static final int OUTPUT_SLOT = 9;
    public static final int CONTAINER_SIZE = 10;
    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    public LobotomyEGOExtractionTableBlockEntity(BlockPos pos, BlockState state) {
        super(PMERegistryBlockEntity.LOBOTOMY_EGO_EXTRACTION_TABLE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pm_equips.lobotomy_ego_extraction_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new LobotomyEGOExtractionMenu(containerId, inventory, this);
    }

    public boolean craft(Player player) {
        if (level == null || level.isClientSide) {
            return false;
        }

        ExtractionRule rule = findCraftableRule();
        if (rule == null || countInputs(rule.input()) < rule.cost()) {
            return false;
        }

        ItemStack generated = getRandomResult(level, rule.resultTag());
        if (generated.isEmpty() || !canMoveToOutput(generated)) {
            return false;
        }

        consumeInputs(rule.input(), rule.cost());
        moveToOutput(generated);
        setChanged();
        return true;
    }

    public boolean canPlaceInput(ItemStack stack) {
        return stack.is(PMERegistryTag.Items.E_BOX);
    }

    private static ItemStack getRandomResult(Level level, TagKey<Item> tagKey) {
        List<Item> candidates = getTaggedItems(level, tagKey);
        if (candidates.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(candidates.get(level.random.nextInt(candidates.size())));
    }

    private static List<Item> getTaggedItems(Level level, TagKey<Item> tagKey) {
        Optional<net.minecraft.core.HolderSet.Named<Item>> tag = level.registryAccess()
                .registryOrThrow(Registries.ITEM)
                .getTag(tagKey);
        if (tag.isEmpty()) {
            return List.of();
        }

        List<Item> items = new ArrayList<>();
        for (Holder<Item> holder : tag.get()) {
            items.add(holder.value());
        }
        return items;
    }

    private boolean canMoveToOutput(ItemStack generated) {
        ItemStack output = items.get(OUTPUT_SLOT);
        return output.isEmpty()
                || (ItemStack.isSameItemSameComponents(output, generated) && output.getCount() < output.getMaxStackSize());
    }

    private void moveToOutput(ItemStack generated) {
        ItemStack output = items.get(OUTPUT_SLOT);
        if (output.isEmpty()) {
            items.set(OUTPUT_SLOT, generated.copy());
        } else {
            output.grow(generated.getCount());
        }
    }

    private ExtractionRule findCraftableRule() {
        for (int slot = INPUT_SLOT_START; slot < INPUT_SLOT_START + INPUT_SLOT_COUNT; slot++) {
            ExtractionRule rule = ExtractionRule.forInput(items.get(slot));
            if (rule != null && countInputs(rule.input()) >= rule.cost()) {
                return rule;
            }
        }
        return null;
    }

    private int countInputs(Item item) {
        int count = 0;
        for (int slot = INPUT_SLOT_START; slot < INPUT_SLOT_START + INPUT_SLOT_COUNT; slot++) {
            ItemStack stack = items.get(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void consumeInputs(Item item, int amount) {
        int remaining = amount;
        for (int slot = INPUT_SLOT_START; slot < INPUT_SLOT_START + INPUT_SLOT_COUNT && remaining > 0; slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.is(item)) {
                continue;
            }

            int consumed = Math.min(stack.getCount(), remaining);
            stack.shrink(consumed);
            remaining -= consumed;
            if (stack.isEmpty()) {
                items.set(slot, ItemStack.EMPTY);
            }
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        net.minecraft.world.ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.clear();
        net.minecraft.world.ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public int getMaxStackSize() {
        return 999;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = net.minecraft.world.ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return net.minecraft.world.ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null
                && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    private record ExtractionRule(Item input, int cost, TagKey<Item> resultTag) {
        private static ExtractionRule forInput(ItemStack stack) {
            if (stack.is(PMERegistryBlock.BlockItems.ZAYIN_PE_BOX.get())) {
                return new ExtractionRule(PMERegistryBlock.BlockItems.ZAYIN_PE_BOX.get(), 15, PMERegistryTag.Items.EGO_T1_ZAYIN);
            }
            if (stack.is(PMERegistryBlock.BlockItems.TETH_PE_BOX.get())) {
                return new ExtractionRule(PMERegistryBlock.BlockItems.TETH_PE_BOX.get(), 30, PMERegistryTag.Items.EGO_T2_TETH);
            }
            if (stack.is(PMERegistryBlock.BlockItems.HE_PE_BOX.get())) {
                return new ExtractionRule(PMERegistryBlock.BlockItems.HE_PE_BOX.get(), 45, PMERegistryTag.Items.EGO_T3_HE);
            }
            if (stack.is(PMERegistryBlock.BlockItems.WAW_PE_BOX.get())) {
                return new ExtractionRule(PMERegistryBlock.BlockItems.WAW_PE_BOX.get(), 60, PMERegistryTag.Items.EGO_T4_WAW);
            }
            if (stack.is(PMERegistryBlock.BlockItems.ALEPH_PE_BOX.get())) {
                return new ExtractionRule(PMERegistryBlock.BlockItems.ALEPH_PE_BOX.get(), 120, PMERegistryTag.Items.EGO_T5_ALEPH);
            }
            return null;
        }
    }
}
