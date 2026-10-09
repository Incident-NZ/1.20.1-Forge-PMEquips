package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.pm_equips.BlockInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EGOW5Twilight extends SwordItem {

    private static final UUID REACH_UUID = UUID.fromString("e7a1c2b3-4d5e-6f70-8192-a3b4c5d6e7f8");
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, "twilight_reach_bonus", 1.0, AttributeModifier.Operation.ADDITION);

    // 鈍足1

    public EGOW5Twilight() {
        super(new CustomTier(), 17, -2.5f, new Properties().rarity(Rarity.EPIC).durability(4000));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            Vec3 look = player.getLookAngle();
            Vec3 origin = player.position().add(0, 1.0, 0);
            double range = 8.0;

            AABB box = new AABB(
                    origin.add(-range, -1.5, -range),
                    origin.add(range, 1.5, range)
            );
            List<LivingEntity> entities = player.level().getEntitiesOfClass(
                    LivingEntity.class, box,
                    e -> e != player && e.isAlive()
            );

            for (LivingEntity entity : entities) {
                Vec3 toTarget = entity.position().add(0, 1.0, 0).subtract(origin).normalize();
                double angle = Math.acos(Math.clamp(look.dot(toTarget), -1.0, 1.0));
                if (angle < Math.toRadians(60)) {
                    entity.hurt(entity.damageSources().magic(), 18.0f);
                    entity.hurt(entity.damageSources().playerAttack(player), 18.0f);
                    entity.hurt(entity.damageSources().freeze(), 18.0f);
                    entity.hurt(entity.damageSources().inFire(), 18.0f);

                    // 薙ぎ払い対象にも鈍足1
                    applySlowness(entity);
                }
            }

            boolean result = super.hurtEnemy(stack, target, attacker);

            if (result) {
                target.hurtTime = 0;
                target.invulnerableTime = 0;
                applySlowness(target);
            }
            return result;
        }

        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(attacker.getUsedItemHand()));
        return true;
    }

    private static void applySlowness(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffectInit.PARALYSIS.get(), 15, 3, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffectInit.BLEED.get(), 15, 3, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffectInit.BIND.get(), 15, 3, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffectInit.FAIRY.get(), 15, 3, false, false, false));
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
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(BlockInit.BlockItems.ALEPH_PE_BOX.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.TWILIGHT_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}