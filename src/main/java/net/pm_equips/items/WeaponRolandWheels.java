package net.pm_equips.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.MobEffectInit;
import net.minecraft.world.effect.MobEffectInstance;

public class WeaponRolandWheels extends SwordItem {

    public static final float COUNTER_DAMAGE = 8.0F;
    public WeaponRolandWheels() {
        super(new CustomTier(), 23, -3.2f, new Properties().durability(1000));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand); // 長押しガード開始
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // 離すまで継続
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    /** このスタックでガード中か（サーバー判定用） */
    public static boolean isPlayerGuarding(Player player) {
        if (!player.isUsingItem()) return false;
        ItemStack using = player.getUseItem();
        return using.getItem() instanceof WeaponRolandWheels;
    }

    /**
     * 被弾ガード成功時の処理
     * @return ダメージを無効化してよいか
     */
    public static boolean tryGuard(Player player, LivingEntity attacker) {
        if (!isPlayerGuarding(player)) return false;

        ItemStack stack = player.getUseItem();
        if (!(stack.getItem() instanceof WeaponRolandWheels)) return false;

        // 正面チェック（背面攻撃はガード不可）
        if (!isFrontalAttack(player, attacker)) return false;

        // カウンター
        attacker.invulnerableTime = 0;
        attacker.hurtTime = 0;
        attacker.hurt(player.damageSources().playerAttack(player), COUNTER_DAMAGE);

        Vec3 dir = attacker.position().subtract(player.position()).normalize();
        attacker.knockback(1.5D, -dir.x, -dir.z);

        attacker.addEffect(new MobEffectInstance(
                MobEffectInit.BIND.get(), 60, 9, false, false, false
        ));

        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));

        return true;
    }

    private static boolean isFrontalAttack(Player player, LivingEntity attacker) {
        Vec3 look = player.getLookAngle().normalize();
        Vec3 toAttacker = attacker.position().subtract(player.position()).normalize();
        // おおよそ前方 120° 以内（dot > 0 で前方半球）
        return look.dot(toAttacker) > 0.0D;
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 1000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    }
}