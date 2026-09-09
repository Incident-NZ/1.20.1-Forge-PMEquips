package net.pm_equips.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CustomMobEffects {

    public static class QuickEffect extends MobEffect {
        private static final ResourceLocation MODIFIER_ID = id("quick_movement");

        public QuickEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x66FF66);
            addAttributeModifier(Attributes.MOVEMENT_SPEED, MODIFIER_ID, 0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    }

    public static class BindEffect extends MobEffect {
        private static final ResourceLocation MODIFIER_ID = id("bind_movement");

        public BindEffect() {
            super(MobEffectCategory.HARMFUL, 0x336699);
            addAttributeModifier(Attributes.MOVEMENT_SPEED, MODIFIER_ID, -0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    }

    public static class PowerEffect extends MobEffect {
        private static final ResourceLocation MODIFIER_ID = id("power_attack");

        public PowerEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xFFCC33);
            addAttributeModifier(Attributes.ATTACK_DAMAGE, MODIFIER_ID, 1.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class WeakEffect extends MobEffect {
        private static final ResourceLocation MODIFIER_ID = id("weak_attack");

        public WeakEffect() {
            super(MobEffectCategory.HARMFUL, 0x9999AA);
            addAttributeModifier(Attributes.ATTACK_DAMAGE, MODIFIER_ID, -1.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class EnduranceEffect extends MobEffect {
        private static final ResourceLocation MODIFIER_ID = id("endurance_armor");

        public EnduranceEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xCCCCCC);
            addAttributeModifier(Attributes.ARMOR, MODIFIER_ID, 1.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class DisarmEffect extends MobEffect {
        private static final ResourceLocation MODIFIER_ID = id("disarm_armor");

        public DisarmEffect() {
            super(MobEffectCategory.HARMFUL, 0xAA4444);
            addAttributeModifier(Attributes.ARMOR, MODIFIER_ID, -1.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class BleedEffect extends MobEffect {
        public BleedEffect() {
            super(MobEffectCategory.HARMFUL, 0x880000);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return true;
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return false;
            DamageSource source = entity.damageSources().magic();
            entity.hurt(source, amplifier + 1);
            return true;
        }
    }

    public static class ParalysisEffect extends MobEffect {
        private static final ResourceLocation MOVE_ID = id("paralysis_movement");
        private static final ResourceLocation ATTACK_ID = id("paralysis_attack");

        public ParalysisEffect() {
            super(MobEffectCategory.HARMFUL, 0x225588);
            addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_ID, -0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_ID, -3.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class FairyEffect extends MobEffect {
        private static final ResourceLocation MOVE_ID = id("fairy_movement");
        private static final ResourceLocation ATTACK_ID = id("fairy_attack");
        private static final ResourceLocation ARMOR_ID = id("fairy_armor");

        public FairyEffect() {
            super(MobEffectCategory.HARMFUL, 0xFF99CC);
            addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_ID, -0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_ID, -1.0D, AttributeModifier.Operation.ADD_VALUE);
            addAttributeModifier(Attributes.ARMOR, ARMOR_ID, -1.0D, AttributeModifier.Operation.ADD_VALUE);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return duration % 40 == 0;
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return false;
            entity.hurt(entity.damageSources().magic(), amplifier + 1);
            return true;
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", path);
    }
}
