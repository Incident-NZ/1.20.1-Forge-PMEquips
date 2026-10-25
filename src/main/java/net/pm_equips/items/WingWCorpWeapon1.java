package net.pm_equips.items;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.pm_equips.ItemInit;

public class WingWCorpWeapon1 extends SwordItem {

    public static final int MAX_FE = 5000;
    public static final int FE_PER_HIT = 200;

    public WingWCorpWeapon1() {
        super(new CustomTier(), new Properties().stacksTo(1).attributes(SwordItem.createAttributes(new CustomTier(), 6, -2.4F)));
    }

    @Override
    public boolean hurtEnemy(
            ItemStack stack,
            LivingEntity target,
            LivingEntity attacker
    ) {
        return super.hurtEnemy(
                stack,
                target,
                attacker
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        return ClassWCorpTeleport.useTeleport(level, player, hand, this, FE_PER_HIT);
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
        return 0x00FFFF;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {

        Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM)).ifPresent(storage ->

                tooltip.add(
                        Component.literal(
                                "FE: "
                                        + storage.getEnergyStored()
                                        + " / "
                                        + storage.getMaxEnergyStored()
                        )
                )
        );

        super.appendHoverText(
                stack,
                level,
                tooltip,
                flag
        );
    }

    private static class CustomTier
            implements Tier {

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
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_STONE_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {

            return Ingredient.of(
                    ItemInit.WCORP_BATTERY.get()
            );
        }
    }
}
