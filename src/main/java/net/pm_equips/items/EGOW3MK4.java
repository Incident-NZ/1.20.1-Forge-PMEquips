package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.BlockInit;
import net.minecraft.world.item.crafting.Ingredient;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EGOW3MK4 extends SwordItem {

    private static final float DAMAGE_PER_TICK = 3.0F; // 1ティックあたりのダメージ（調整）
    private static final double RANGE = 5.0D;         // 前方範囲
    private static final int MAX_USE_TIME = 72000;    // 長押し可能時間（ほぼ無限）

    public EGOW3MK4() {
        super(new CustomTier(), 17, -2.2f, new Properties().durability(2000));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand); // 長押し開始
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player) || level.isClientSide) return;

        // 前方のモブを探してダメージ
        findAndDamageFrontMobs(player, level);
    }

    private void findAndDamageFrontMobs(Player player, Level level) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle().scale(RANGE);
        Vec3 targetPos = eyePos.add(lookDir);

        // AABBで前方範囲のエンティティを取得
        AABB aabb = player.getBoundingBox().expandTowards(lookDir).inflate(1.0D);
        List<Entity> entities = level.getEntities(player, aabb, e -> e instanceof LivingEntity && e.isAlive() && !e.isPassengerOfSameVehicle(player));

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity target && isInFront(player, target)) {
                target.hurtTime = 0;
                target.invulnerableTime = 0;

                DamageSource source = player.damageSources().playerAttack(player);
                target.hurt(source, DAMAGE_PER_TICK);
            }
        }
    }

    private boolean isInFront(Player player, Entity target) {
        Vec3 look = player.getLookAngle();
        Vec3 toTarget = target.position().subtract(player.position()).normalize();
        return look.dot(toTarget) > 0.6; // 前方角度の閾値（調整）
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return MAX_USE_TIME;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW; // または NONE / BLOCK など好みで
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 3000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public int getLevel() { return 1; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(BlockInit.BlockItems.HE_PE_BOX.get()); }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.MK4_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
