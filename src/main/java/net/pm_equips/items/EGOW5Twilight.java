package net.pm_equips.items;


import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.pm_equips.BlockInit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EGOW5Twilight extends SwordItem {

    private static final UUID REACH_UUID = UUID.randomUUID();
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, "twilight_reach_bonus", 1.0, AttributeModifier.Operation.ADDITION);

    public EGOW5Twilight() {
        super(new CustomTier(), 17, -2.5f, new Properties().rarity(Rarity.EPIC).durability(4000));
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
                    entity.hurt(entity.damageSources().magic(), 18.0f);
                    entity.hurt(entity.damageSources().playerAttack(player), 18.0f);
                    entity.hurt(entity.damageSources().freeze(), 18.0f);
                    entity.hurt(entity.damageSources().onFire(), 18.0f); // 薙ぎ払いダメージ
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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        boolean isHolding = selected && player.getMainHandItem() == stack;

        AttributeInstance reachAttr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (reachAttr != null) {
            if (isHolding && !reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.addTransientModifier(REACH_MODIFIER);
            } else if (!isHolding && reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.removeModifier(REACH_MODIFIER);
            }
        }
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
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.TWILIGHT_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
