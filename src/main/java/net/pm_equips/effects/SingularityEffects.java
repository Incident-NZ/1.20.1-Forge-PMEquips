package net.pm_equips.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class SingularityEffects {

    public static class KCorpSingularityEffect extends MobEffect {
        private static final ResourceLocation HEALTH_ID = id("kcorp_health");
        private static final ResourceLocation ARMOR_ID = id("kcorp_armor");

        public KCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x88CC88);
            addAttributeModifier(Attributes.MAX_HEALTH, HEALTH_ID, 20.0D, AttributeModifier.Operation.ADD_VALUE);
            addAttributeModifier(Attributes.ARMOR, ARMOR_ID, 10.0D, AttributeModifier.Operation.ADD_VALUE);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return duration % 60 == 0;
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return false;
            entity.heal(1.0F);
            if (entity instanceof Player player) {
                player.getFoodData().eat(1, 0.0F);
            }
            return true;
        }
    }

    public static class RCorpSingularityEffect extends MobEffect {
        private static final ResourceLocation MOVE_ID = id("rcorp_movement");
        private static final ResourceLocation ATTACK_ID = id("rcorp_attack");

        public RCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x8899FF);
            addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_ID, 0.50D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_ID, 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    }

    public static class WCorpSingularityEffect extends MobEffect {
        public WCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xFFDD88);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return duration % 200 == 0;
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return false;
            var tag = entity.getPersistentData();
            tag.putInt("wcorp_evade_charges", tag.getInt("wcorp_evade_charges") + 5);
            return true;
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", path);
    }
}
