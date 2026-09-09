package net.pm_equips.items;

import net.pm_equips.util.ItemStackData;


import net.pm_equips.energy.EnergyUtils;
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

import java.util.List;

public class ItemEnkephalin extends Item {

    public static final int MAX_ENERGY = 100000;
    private static final int TRANSFER_RATE = 1000;

    public ItemEnkephalin(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {

        ItemStack battery =
                player.getItemInHand(hand);

        CompoundTag tag = ItemStackData.copy(battery);
        if (!tag.contains("Energy"))
        {
            tag.putInt(
                    "Energy",
                    MAX_ENERGY
            );
            ItemStackData.set(battery, tag);
        }

        if(level.isClientSide)
        {
            return InteractionResultHolder.success(battery);
        }

        int storedEnergy =
                tag.getInt("Energy");

        if (storedEnergy <= 0)
        {
            battery.shrink(1);

            return InteractionResultHolder.consume(
                    battery
            );
        }

        for (ItemStack stack :
                player.getInventory().items)
        {
            if (stack == battery)
                continue;

            if (storedEnergy <= 0)
                break;

            if (!EnergyUtils.get(stack).isPresent())
            {
                continue;
            }

            IEnergyStorage storage =
                    EnergyUtils.get(stack).orElse(null);

            if (storage == null)
                continue;

            int accepted =
                    storage.receiveEnergy(
                            Math.min(
                                    storedEnergy,
                                    TRANSFER_RATE
                            ),
                            false
                    );

            storedEnergy -= accepted;
        }

        tag.putInt(
                "Energy",
                storedEnergy
        );
        ItemStackData.set(battery, tag);

        if (storedEnergy <= 0)
        {
            battery.shrink(1);
        }

        return InteractionResultHolder.consume(
                battery
        );
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    )
    {
        CompoundTag tag = ItemStackData.copy(stack);
        if (!tag.contains("Energy"))
        {
            tag.putInt(
                    "Energy",
                    MAX_ENERGY
            );
            ItemStackData.set(stack, tag);
        }

        tooltip.add(
                Component.literal(
                        "Stored FE: "
                                + tag.getInt("Energy")
                                + " / "
                                + MAX_ENERGY
                )
        );

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }
}
