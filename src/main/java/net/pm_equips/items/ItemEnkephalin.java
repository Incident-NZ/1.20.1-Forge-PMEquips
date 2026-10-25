package net.pm_equips.items;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pm_equips.util.ItemStackData;

public class ItemEnkephalin extends Item {
    public static final int MAX_ENERGY = 100000;
    private static final int TRANSFER_RATE = 1000;

    public ItemEnkephalin(Properties properties) { super(properties); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack battery = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(battery);

        CompoundTag tag = ItemStackData.copy(battery);
        int stored = tag.contains("Energy") ? Math.clamp(tag.getInt("Energy"), 0, MAX_ENERGY) : MAX_ENERGY;
        for (ItemStack stack : player.getInventory().items) {
            if (stored <= 0) break;
            if (stack == battery) continue;
            IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy != null) stored -= energy.receiveEnergy(Math.min(stored, TRANSFER_RATE), false);
        }
        if (stored <= 0) {
            battery.shrink(1);
            tag.remove("Energy");
        } else {
            tag.putInt("Energy", stored);
        }
        if (!battery.isEmpty()) ItemStackData.set(battery, tag);
        return InteractionResultHolder.consume(battery);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = ItemStackData.copy(stack);
        int stored = tag.contains("Energy") ? Math.clamp(tag.getInt("Energy"), 0, MAX_ENERGY) : MAX_ENERGY;
        tooltip.add(Component.literal("Stored FE: " + stored + " / " + MAX_ENERGY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
