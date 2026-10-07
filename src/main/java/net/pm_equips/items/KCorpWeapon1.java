package net.pm_equips.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.pm_equips.ItemInit;
import net.pm_equips.energy.WeaponEnergyProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KCorpWeapon1 extends SwordItem {

    public static final int MAX_FE = 7500;
    public static final int FE_PER_HIT = 500;

    public KCorpWeapon1() {
        super(
                new CustomTier(),
                6,
                -2.4F,
                new Properties()
                        .stacksTo(1)
        );
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(
            ItemStack stack,
            @Nullable CompoundTag nbt
    ) {
        return new WeaponEnergyProvider(
                stack,
                MAX_FE
        );
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

        stack.getCapability(
                ForgeCapabilities.ENERGY
        ).ifPresent(storage -> {

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

        return stack.getCapability(
                ForgeCapabilities.ENERGY
        ).map(storage ->
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
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {

        stack.getCapability(
                ForgeCapabilities.ENERGY
        ).ifPresent(storage -> tooltip.add(
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

        @Override
        public int getUses() {
            return 2000;
        }

        @Override
        public float getSpeed() {
            return 4.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }

        @Override
        public int getLevel() {
            return 1;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(
                    ItemInit.KCORP_AMPOULE.get()
            );
        }
    }
}
