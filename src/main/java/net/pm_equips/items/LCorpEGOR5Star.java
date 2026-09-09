package net.pm_equips.items;

import net.minecraft.world.entity.LivingEntity;
import net.pm_equips.entity.EGOStarP;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LCorpEGOR5Star extends Item {
    public LCorpEGOR5Star() {
        super(new Properties().durability(4000));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            shootProjectile(level, player, 0.0f);
            shootProjectile(level, player, -10.0f);
            shootProjectile(level, player, 10.0f);

            stack.hurtAndBreak(1, player, hand == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void shootProjectile(Level level, Player player, float angleOffset) {
        EGOStarP projectile = new EGOStarP(level, player);

        projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot() + angleOffset, 0.0F, 2.0F, 1.0F);
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(2.0));

        level.addFreshEntity(projectile);
    }
}
