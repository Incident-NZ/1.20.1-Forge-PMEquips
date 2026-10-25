package net.pm_equips.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.pm_equips.MobEffectInit;

public class FixerColorRolandWeaponDurandal extends SwordItem {
    public FixerColorRolandWeaponDurandal() {
        super(new CustomTier(), new Properties().durability(1000).attributes(SwordItem.createAttributes(new CustomTier(), 8, -2.2f)));
    }

    private static class CustomTier implements Tier {

        @Override
        public int getUses() {
            return 1000;
        }

        @Override
        public float getSpeed() {
            return 4.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0f;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return null;
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) return;

        // 効力は利き手（main hand）に持っている間だけ
        ItemStack main = player.getMainHandItem();
        boolean has = main.getItem() instanceof FixerColorRolandWeaponDurandal;

        if (has) {
            // 攻撃力上昇 II (amplifier 1)
            MobEffectInstance mei = new MobEffectInstance(MobEffectInit.POWER, 12, 1, false, false, true);
            player.addEffect(mei);
        } else {
            if (player.hasEffect(MobEffectInit.POWER)) {
                player.removeEffect(MobEffectInit.POWER);
            }
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }
}
