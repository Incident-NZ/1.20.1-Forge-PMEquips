package net.pm_equips.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

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

        CompoundTag tag =
                battery.getOrCreateTag();

        if (!tag.contains("Energy"))
        {
            tag.putInt(
                    "Energy",
                    MAX_ENERGY
            );
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

            if (!stack.getCapability(
                    ForgeCapabilities.ENERGY
            ).isPresent())
            {
                continue;
            }

            IEnergyStorage storage =
                    stack.getCapability(
                            ForgeCapabilities.ENERGY
                    ).orElse(null);

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
            Level level,
            List<Component> tooltip,
            TooltipFlag flag
    )
    {
        CompoundTag tag =
                stack.getOrCreateTag();

        if (!tag.contains("Energy"))
        {
            tag.putInt(
                    "Energy",
                    MAX_ENERGY
            );
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
                level,
                tooltip,
                flag
        );
    }
}
