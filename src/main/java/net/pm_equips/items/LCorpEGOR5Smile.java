package net.pm_equips.items;

import net.pm_equips.util.ItemStackData;

import net.pm_equips.PMERegistryBlock;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LCorpEGOR5Smile extends SwordItem {

    private static final String ATTACK_COUNT_TAG = "smile_attack_count";

    public LCorpEGOR5Smile() {
        super(new CustomTier(), new Properties().durability(4000).attributes(SwordItem.createAttributes(new CustomTier(), 17, -2.1f)));
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

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30 * 20, 2)); // 30秒, Lv3
            var tag = ItemStackData.copy(stack);
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
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120 * 20, 2)); // 2分, Lv3
                }

                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120 * 20, 2));
            }

            tag.putInt(ATTACK_COUNT_TAG, count);
            ItemStackData.set(stack, tag);
        }

        stack.hurtAndBreak(1, attacker, attacker.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        return true;
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 4000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0f; } // 攻撃力

        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(PMERegistryBlock.BlockItems.ALEPH_PE_BOX.get()); }
    }
}

