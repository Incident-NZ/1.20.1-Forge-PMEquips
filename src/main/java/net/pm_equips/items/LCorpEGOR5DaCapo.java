package net.pm_equips.items;

import net.pm_equips.PMERegistryBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LCorpEGOR5DaCapo extends SwordItem {
    public LCorpEGOR5DaCapo() {
        super(new CustomTier(), new Properties().durability(4000).attributes(SwordItem.createAttributes(new CustomTier(), 24, -2.0f)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            Vec3 look = player.getLookAngle();
            Vec3 origin = player.position().add(0, 1.0, 0);
            double range = 8.0;

            AABB box = new AABB(origin.add(-range, -1.5, -range), origin.add(range, 1.5, range));
            List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != player && e.isAlive());

            for (LivingEntity entity : entities) {
                Vec3 toTarget = entity.position().add(0, 1.0, 0).subtract(origin).normalize();
                double angle = Math.acos(look.dot(toTarget));
                if (angle < Math.toRadians(60)) {
                    entity.hurt(entity.damageSources().playerAttack(player), 5.0f); // 薙ぎ払いダメージ
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

        stack.hurtAndBreak(1, attacker, attacker.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        return true;
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 4000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }

        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(PMERegistryBlock.BlockItems.ALEPH_PE_BOX.get());
        }
    }
}
