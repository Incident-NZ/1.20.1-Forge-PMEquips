package net.pm_equips.items;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.client.screen.TooltipLines;

public class LobEGOWeapon5Twilight extends SwordItem {

    private static final ResourceLocation REACH_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "lobegoweapon5twilight_reach_uuid");
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, 1.0, AttributeModifier.Operation.ADD_VALUE);

    // 鈍足1

    public LobEGOWeapon5Twilight() {
        super(new CustomTier(), new Properties().rarity(Rarity.EPIC).durability(4000).attributes(SwordItem.createAttributes(new CustomTier(), 17, -2.5f)));
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

        stack.hurtAndBreak(1, attacker, (attacker.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
        return true;
    }

    private static void applySlowness(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffectInit.PARALYSIS, 15, 3, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffectInit.BLEED, 15, 3, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffectInit.BIND, 15, 3, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffectInit.FAIRY, 15, 3, false, false, false));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        boolean isHolding = selected && player.getMainHandItem() == stack;

        AttributeInstance reachAttr = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reachAttr != null) {
            if (isHolding && !reachAttr.hasModifier(REACH_MODIFIER.id())) {
                reachAttr.addTransientModifier(REACH_MODIFIER);
            } else if (!isHolding && reachAttr.hasModifier(REACH_MODIFIER.id())) {
                reachAttr.removeModifier(REACH_MODIFIER.id());
            }
        }
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 4000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(ItemInit.LCORP_EBOX.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.TWILIGHT_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
