package net.pm_equips.items;

import net.pm_equips.BlockInit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;
import net.pm_equips.MobEffectInit;

import java.util.List;

public class EGOW5Smile extends SwordItem {

    private static final String ATTACK_COUNT_TAG = "smile_attack_count";

    public EGOW5Smile() {
        super(new CustomTier(), 17, -2.1f, new Properties().durability(4000));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }

        if (!attacker.level().isClientSide && attacker instanceof Player player) {

            target.addEffect(new MobEffectInstance(MobEffectInit.BIND.get(), 100, 6)); // 30秒, Lv3
            var tag = stack.getOrCreateTag();
            int count = tag.getInt(ATTACK_COUNT_TAG) + 1;
            if (count >= 5) {
                count = 0;

                double radius = 16.0;
                AABB area = new AABB(
                        player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                        player.getX() + radius, player.getY() + radius, player.getZ() + radius
                );

                List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, area,
                        e -> e != player && e.isAlive());

                for (LivingEntity entity : entities) {
                    entity.addEffect(new MobEffectInstance(MobEffectInit.BIND.get(), 100, 6));
                }

                target.addEffect(new MobEffectInstance(MobEffectInit.BIND.get(), 60, 4));
            }

            tag.putInt(ATTACK_COUNT_TAG, count);
        }

        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(attacker.getUsedItemHand()));
        return true;
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 4000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0f; } // 攻撃力
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(BlockInit.BlockItems.ALEPH_PE_BOX.get()); }
    }
}

