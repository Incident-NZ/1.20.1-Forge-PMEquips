package net.pm_equips.items;

import net.pm_equips.PMERegistryBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class LCorpEGOR1Penitence extends SwordItem {

    public LCorpEGOR1Penitence() {
        super(new CustomTier(), new Properties().durability(800).attributes(SwordItem.createAttributes(new CustomTier(), 6, -2.8f)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }

        if (attacker instanceof Player player) {
            stack.hurtAndBreak(1, player, player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);

            // HPを2回復（1ハート）
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(2.0F);
            }
        }

        return true;
    }

    private static class CustomTier implements Tier {
        @Override
        public int getUses() {
            return 800;
        }

        @Override
        public float getSpeed() {
            return 4.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0f;
        }



        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(PMERegistryBlock.BlockItems.ZAYIN_PE_BOX.get()); // 任意で変更可能
        }
    }
}

