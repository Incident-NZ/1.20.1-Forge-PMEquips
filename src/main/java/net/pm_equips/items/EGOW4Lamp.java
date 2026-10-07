package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pm_equips.BlockInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EGOW4Lamp extends SwordItem {
    public EGOW4Lamp() {
        super(new CustomTier(), 27, -3.3f, new Properties().durability(3000));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }

        if (!(attacker instanceof Player player)) return super.hurtEnemy(stack, target, attacker);

        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));

        CompoundTag tag = target.getPersistentData();
        String id = "lamp_weapons_hits";
        int hits = tag.getInt(id);

        hits++;
        if (hits >= 4) {
            tag.putBoolean("lamp_weapons_buff", true);
            tag.putInt(id, 0);
        } else {
            tag.putInt(id, hits);
        }

        return true;
    }

    public static float applyExtraDamage(LivingEntity entity, DamageSource source, float originalDamage) {
        CompoundTag tag = entity.getPersistentData();
        if (tag.getBoolean("lamp_weapons_buff")) {
            tag.putBoolean("lamp_weapons_buff", false); // 一度だけ適用
            return originalDamage + 2;
        }
        return originalDamage;
    }

    private static class CustomTier implements Tier {
        @Override
        public int getUses() {
            return 3000;
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
        public int getLevel() {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(BlockInit.BlockItems.WAW_PE_BOX.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.LAMP_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
