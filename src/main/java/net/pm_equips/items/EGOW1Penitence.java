package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pm_equips.BlockInit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EGOW1Penitence extends SwordItem {

    public EGOW1Penitence() {
        super(new CustomTier(), 6, -2.8f, new Properties().durability(800));
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
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));

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

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(BlockInit.BlockItems.ZAYIN_PE_BOX.get()); // 任意で変更可能
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.PENITENCE_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}

