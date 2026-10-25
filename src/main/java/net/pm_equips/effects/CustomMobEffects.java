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
        private static final ResourceLocation MOD_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "quickeffect_mod_uuid");
        public QuickEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x66FF66);
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOD_UUID, 0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    }

    public static class BindEffect extends MobEffect {
        private static final ResourceLocation MOD_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "bindeffect_mod_uuid");
        public BindEffect() {
            super(MobEffectCategory.HARMFUL, 0x336699);
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOD_UUID, -0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    }

    public static class PowerEffect extends MobEffect {
        private static final ResourceLocation MOD_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "powereffect_mod_uuid");
        public PowerEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xFFCC33);
            this.addAttributeModifier(Attributes.ATTACK_DAMAGE, MOD_UUID, 1.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class WeakEffect extends MobEffect {
        private static final ResourceLocation MOD_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "weakeffect_mod_uuid");
        public WeakEffect() {
            super(MobEffectCategory.HARMFUL, 0x9999AA);
            this.addAttributeModifier(Attributes.ATTACK_DAMAGE, MOD_UUID, -1.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class EnduranceEffect extends MobEffect {
        private static final ResourceLocation MOD_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "enduranceeffect_mod_uuid");
        public EnduranceEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xCCCCCC);
            this.addAttributeModifier(Attributes.ARMOR, MOD_UUID, 1.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class DisarmEffect extends MobEffect {
        private static final ResourceLocation MOD_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "disarmeffect_mod_uuid");
        public DisarmEffect() {
            super(MobEffectCategory.HARMFUL, 0xAA4444);
            this.addAttributeModifier(Attributes.ARMOR, MOD_UUID, -1.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class BleedEffect extends MobEffect {
        private static final ResourceLocation MOD_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "bleedeffect_mod_uuid");
        public BleedEffect() {
            super(MobEffectCategory.HARMFUL, 0x880000);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return true;
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return true;
            int dmg = amplifier + 1;
            DamageSource src = entity.damageSources().magic(); // using magic damage source (does not bypass armor automatically)
            entity.hurt(src, dmg);

            return true;
        }
    }

    public static class ParalysisEffect extends MobEffect {
        private static final ResourceLocation MOVE_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "paralysiseffect_move_uuid");
        private static final ResourceLocation ATTACK_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "paralysiseffect_attack_uuid");

        public ParalysisEffect() {
            super(MobEffectCategory.HARMFUL, 0x225588);
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_UUID, -0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_UUID, -3.0D, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static class FairyEffect extends MobEffect {
        private static final ResourceLocation MOVE_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "fairyeffect_move_uuid");
        private static final ResourceLocation ATTACK_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "fairyeffect_attack_uuid");
        private static final ResourceLocation ARMOR_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "fairyeffect_armor_uuid");

        public FairyEffect() {
            super(MobEffectCategory.HARMFUL, 0xFF99CC);
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_UUID, -0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_UUID, -1.0D, AttributeModifier.Operation.ADD_VALUE);
                        this.addAttributeModifier(Attributes.ARMOR, ARMOR_UUID, -1.0D, AttributeModifier.Operation.ADD_VALUE);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            // every 40 ticks (2 seconds)
            int interval = 40;
            return duration % interval == 0;
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return true;
            int dmg = amplifier + 1;
            DamageSource src = entity.damageSources().magic();
            entity.hurt(src, dmg);

            return true;
        }
    }

    public static class BarrierEffect extends MobEffect {
        public BarrierEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x55C6FF);
        }

        public static float getNegateThreshold(int amplifier) {
            return 10.0F * (amplifier + 1);
        }
    }
}
