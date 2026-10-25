package net.pm_equips.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.pm_equips.MenuInit;
import net.pm_equips.blockentity.LobotomyEGOExtractionTableBlockEntity;

public class LobotomyEGOExtractionMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT_START = LobotomyEGOExtractionTableBlockEntity.INPUT_SLOT_START;
    private static final int INPUT_SLOT_COUNT = LobotomyEGOExtractionTableBlockEntity.INPUT_SLOT_COUNT;
    private static final int INPUT_SLOT_END = INPUT_SLOT_START + INPUT_SLOT_COUNT;
    private static final int OUTPUT_SLOT = LobotomyEGOExtractionTableBlockEntity.OUTPUT_SLOT;
    private static final int OUTPUT_MENU_SLOT = INPUT_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_START = INPUT_SLOT_COUNT + 1;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;
    private final Container container;

    public LobotomyEGOExtractionMenu(int containerId, Inventory inventory, FriendlyByteBuf data) {
        this(containerId, inventory, getBlockEntity(inventory, data));
    }

    public LobotomyEGOExtractionMenu(int containerId, Inventory inventory, LobotomyEGOExtractionTableBlockEntity blockEntity) {
        super(MenuInit.LOBOTOMY_EGO_EXTRACTION_TABLE.get(), containerId);
        this.container = blockEntity;
        addExtractionSlots(blockEntity);
        addPlayerInventory(inventory);
    }

    private LobotomyEGOExtractionMenu(int containerId, Inventory inventory, Container container) {
        super(MenuInit.LOBOTOMY_EGO_EXTRACTION_TABLE.get(), containerId);
        this.container = container;
        addExtractionSlots(container);
        addPlayerInventory(inventory);
    }

    private static Container getBlockEntity(Inventory inventory, FriendlyByteBuf data) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof LobotomyEGOExtractionTableBlockEntity extractionTable) {
            return extractionTable;
        }
        return new SimpleContainer(LobotomyEGOExtractionTableBlockEntity.CONTAINER_SIZE);
    }

    private void addExtractionSlots(Container container) {
        addSlot(new Slot(container, INPUT_SLOT_START, 43, 83) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return container instanceof LobotomyEGOExtractionTableBlockEntity blockEntity
                        && blockEntity.canPlaceInput(stack);
            }
        });

        addSlot(new Slot(container, OUTPUT_SLOT, 116, 83) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 140 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 198));
        }
    }

    public void craft(Player player) {
        if (container instanceof LobotomyEGOExtractionTableBlockEntity blockEntity) {
            blockEntity.craft(player);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return result;
        }

        ItemStack stack = slot.getItem();
        result = stack.copy();
        if (index == OUTPUT_MENU_SLOT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, result);
        } else if (index == INPUT_SLOT_START) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_INVENTORY_START) {
            if (container instanceof LobotomyEGOExtractionTableBlockEntity blockEntity && blockEntity.canPlaceInput(stack)) {
                if (!moveItemStackTo(stack, INPUT_SLOT_START, INPUT_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < PLAYER_INVENTORY_END) {
                if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == result.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
}
