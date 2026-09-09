package net.pm_equips.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.pm_equips.PMERegistryBlockEntity;
import net.pm_equips.PMERegistryTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PEBoxGeneratorBlockEntity extends BlockEntity {
    private static final String STORED_ITEMS_TAG = "StoredItems";
    private final NonNullList<ItemStack> storedItems = NonNullList.create();

    public PEBoxGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(PMERegistryBlockEntity.EBOX_GEN.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PEBoxGeneratorBlockEntity blockEntity) {
        AABB inputArea = new AABB(pos.getX(), pos.getY() + 1.0D, pos.getZ(), pos.getX() + 1.0D, pos.getY() + 2.0D, pos.getZ() + 1.0D);
        boolean changed = false;

        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, inputArea, entity -> entity.isAlive() && !entity.getItem().isEmpty())) {
            int rolls = itemEntity.getItem().getCount();
            itemEntity.discard();
            blockEntity.generateRandomEBoxes(level, rolls);
            changed = true;
        }

        for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class, inputArea, Entity::isAlive)) {
            int rolls = Math.max(1, Mth.ceil(livingEntity.getMaxHealth()));
            livingEntity.kill();
            blockEntity.generateRandomEBoxes(level, rolls);
            changed = true;
        }

        if (changed) {
            blockEntity.markUpdated();
        }
    }

    public boolean giveStoredItemsTo(LivingEntity entity) {
        if (!(entity instanceof net.minecraft.world.entity.player.Player player) || storedItems.isEmpty()) {
            return false;
        }

        boolean movedAny = false;
        for (int i = storedItems.size() - 1; i >= 0; i--) {
            ItemStack stack = storedItems.get(i);
            if (stack.isEmpty()) {
                storedItems.remove(i);
                continue;
            }

            ItemStack remaining = stack.copy();
            if (player.getInventory().add(remaining)) {
                storedItems.remove(i);
                movedAny = true;
            } else if (remaining.getCount() != stack.getCount()) {
                storedItems.set(i, remaining);
                movedAny = true;
            }
        }

        if (movedAny) {
            markUpdated();
        }
        return movedAny;
    }

    public void dropStoredItems(Level level, BlockPos pos) {
        for (ItemStack stack : storedItems) {
            if (!stack.isEmpty()) {
                net.minecraft.world.Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack);
            }
        }
        storedItems.clear();
        markUpdated();
    }

    public ItemStack getDisplayStack() {
        return storedItems.stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst()
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    private void generateRandomEBoxes(Level level, int rolls) {
        List<Item> candidates = getTaggedItems(level, PMERegistryTag.Items.E_BOX);
        if (candidates.isEmpty()) {
            return;
        }

        for (int i = 0; i < rolls; i++) {
            addGeneratedItem(candidates.get(level.random.nextInt(candidates.size())), 1);
        }
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

    private void addGeneratedItem(Item item, int count) {
        ItemStack generated = new ItemStack(item, count);
        for (ItemStack stored : storedItems) {
            if (generated.isEmpty()) {
                return;
            }
            if (ItemStack.isSameItemSameComponents(stored, generated) && stored.getCount() < stored.getMaxStackSize()) {
                int moved = Math.min(generated.getCount(), stored.getMaxStackSize() - stored.getCount());
                stored.grow(moved);
                generated.shrink(moved);
            }
        }

        while (!generated.isEmpty()) {
            ItemStack split = generated.split(Math.min(generated.getCount(), generated.getMaxStackSize()));
            storedItems.add(split);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag items = new ListTag();
        for (ItemStack stack : storedItems) {
            if (!stack.isEmpty()) {
                items.add((CompoundTag) stack.save(registries));
            }
        }
        tag.put(STORED_ITEMS_TAG, items);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storedItems.clear();
        ListTag items = tag.getList(STORED_ITEMS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = ItemStack.parseOptional(registries, items.getCompound(i));
            if (!stack.isEmpty()) {
                storedItems.add(stack);
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            loadAdditional(tag, registries);
        }
    }

    private void markUpdated() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }
}
