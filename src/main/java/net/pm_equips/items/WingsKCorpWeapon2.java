package net.pm_equips.items;

import net.minecraft.world.item.Item;


import net.pm_equips.energy.EnergyUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.pm_equips.PMERegistryItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WingsKCorpWeapon2 extends SwordItem {
    public static final int MAX_FE = 7500;
    public static final int FE_PER_HIT = 500;

    public WingsKCorpWeapon2() {
        super(new CustomTier(), new Properties().attributes(SwordItem.createAttributes(new CustomTier(), 9, -2.8f)));
    }

        @Override
    public boolean hurtEnemy(
            ItemStack stack,
            LivingEntity target,
            LivingEntity attacker
    ) {

        if(attacker.level().isClientSide)
        {
            return super.hurtEnemy(
                    stack,
                    target,
                    attacker
            );
        }

        EnergyUtils.get(stack).ifPresent(storage -> {

            if(storage.getEnergyStored() < FE_PER_HIT)
            {
                return;
            }

            /*
             * Tierダメージ(6)
             * エンチャント等は考慮しない
             */
            float estimatedDamage = 6.0F;

            float healAmount =
                    estimatedDamage * 0.5F;

            storage.extractEnergy(
                    FE_PER_HIT,
                    false
            );

            attacker.heal(
                    healAmount
            );
        });

        return super.hurtEnemy(
                stack,
                target,
                attacker
        );
    }

    @Override
    public boolean isBarVisible(
            ItemStack stack
    ) {
        return true;
    }

    @Override
    public int getBarWidth(
            ItemStack stack
    ) {

        return EnergyUtils.get(stack).map(storage ->
                Math.round(
                        13.0F *
                                storage.getEnergyStored()
                                / (float) storage.getMaxEnergyStored()
                )
        ).orElse(0);
    }

    @Override
    public int getBarColor(
            ItemStack stack
    ) {
        return 0xFF4040;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {

        EnergyUtils.get(stack).ifPresent(storage -> tooltip.add(
                Component.literal(
                        "FE: "
                                + storage.getEnergyStored()
                                + " / "
                                + storage.getMaxEnergyStored()
                )
        ));

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 2000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }

        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(PMERegistryItem.KCORP_AMPOULE.get()); }
    }
}
