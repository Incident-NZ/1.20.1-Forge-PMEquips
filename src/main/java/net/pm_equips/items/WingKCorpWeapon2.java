package net.pm_equips.items;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.pm_equips.ItemInit;

public class WingKCorpWeapon2 extends AxeItem {
    public static final int MAX_FE = 7500;
    public static final int FE_PER_HIT = 500;

    public WingKCorpWeapon2() {
        super(new CustomTier(), new Properties().attributes(AxeItem.createAttributes(new CustomTier(), 9, -2.0f)));
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

        Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM)).ifPresent(storage -> {

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

        return Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM)).map(storage ->
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
    public void appendHoverText(ItemStack stack, TooltipContext level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {

        Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM)).ifPresent(storage -> tooltip.add(
                Component.literal(
                        "FE: "
                                + storage.getEnergyStored()
                                + " / "
                                + storage.getMaxEnergyStored()
                )
        ));

        super.appendHoverText(
                stack,
                level,
                tooltip,
                flag
        );
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 2000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_STONE_TOOL;
        }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(ItemInit.KCORP_AMPOULE.get()); }
    }
}
