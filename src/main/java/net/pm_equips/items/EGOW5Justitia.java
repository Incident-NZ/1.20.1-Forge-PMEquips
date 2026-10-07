package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.pm_equips.BlockInit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EGOW5Justitia extends SwordItem {
    public EGOW5Justitia() {
        super(new CustomTier(), 19, -2.7f, new Properties().durability(4000));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            Vec3 look = player.getLookAngle();
            Vec3 origin = player.position().add(0, 1.0, 0);
            double range = 5.0;

            AABB box = new AABB(origin.add(-range, -1.5, -range), origin.add(range, 1.5, range));
            List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != player && e.isAlive());

            for (LivingEntity entity : entities) {
                Vec3 toTarget = entity.position().add(0, 1.0, 0).subtract(origin).normalize();
                double angle = Math.acos(look.dot(toTarget));
                if (angle < Math.toRadians(60)) {
                    entity.hurt(player.level().damageSources().magic(), 4.0f);
                }
            }

            boolean result = super.hurtEnemy(stack, target, attacker);

            if (result && !attacker.level().isClientSide()) {
                // Iフレーム無視の核心
                target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
                target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
            }
            return result;
        }

        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(attacker.getUsedItemHand()));
        return true;
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 4000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(BlockInit.BlockItems.ALEPH_PE_BOX.get()); }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.JUSTITIA_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
