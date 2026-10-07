package net.pm_equips.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class CustomMobEffects {

    public static class QuickEffect extends MobEffect {
        private static final UUID MOD_UUID = UUID.randomUUID();
        public QuickEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x66FF66);
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOD_UUID.toString(), 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    public static class BindEffect extends MobEffect {
        private static final UUID MOD_UUID = UUID.randomUUID();
        public BindEffect() {
            super(MobEffectCategory.HARMFUL, 0x336699);
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOD_UUID.toString(), -0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    public static class PowerEffect extends MobEffect {
        private static final UUID MOD_UUID = UUID.randomUUID();
        public PowerEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xFFCC33);
            this.addAttributeModifier(Attributes.ATTACK_DAMAGE, MOD_UUID.toString(), 1.0D, AttributeModifier.Operation.ADDITION);
        }
    }

    public static class WeakEffect extends MobEffect {
        private static final UUID MOD_UUID = UUID.randomUUID();
        public WeakEffect() {
            super(MobEffectCategory.HARMFUL, 0x9999AA);
            this.addAttributeModifier(Attributes.ATTACK_DAMAGE, MOD_UUID.toString(), -1.0D, AttributeModifier.Operation.ADDITION);
        }
    }

    public static class EnduranceEffect extends MobEffect {
        private static final UUID MOD_UUID = UUID.randomUUID();
        public EnduranceEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xCCCCCC);
            this.addAttributeModifier(Attributes.ARMOR, MOD_UUID.toString(), 1.0D, AttributeModifier.Operation.ADDITION);
        }
    }

    public static class DisarmEffect extends MobEffect {
        private static final UUID MOD_UUID = UUID.randomUUID();
        public DisarmEffect() {
            super(MobEffectCategory.HARMFUL, 0xAA4444);
            this.addAttributeModifier(Attributes.ARMOR, MOD_UUID.toString(), -1.0D, AttributeModifier.Operation.ADDITION);
        }
    }

    public static class BleedEffect extends MobEffect {
        private static final UUID MOD_UUID = UUID.randomUUID();
        public BleedEffect() {
            super(MobEffectCategory.HARMFUL, 0x880000);
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return true;
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return;
            int dmg = amplifier + 1;
            DamageSource src = entity.damageSources().magic(); // using magic damage source (does not bypass armor automatically)
            entity.hurt(src, dmg);
        }
    }

    public static class ParalysisEffect extends MobEffect {
        private static final UUID MOVE_UUID = UUID.randomUUID();
        private static final UUID ATTACK_UUID = UUID.randomUUID();

        public ParalysisEffect() {
            super(MobEffectCategory.HARMFUL, 0x225588);
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_UUID.toString(), -0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL);
                        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_UUID.toString(), -3.0D, AttributeModifier.Operation.ADDITION);
        }
    }

    public static class FairyEffect extends MobEffect {
        private static final UUID MOVE_UUID = UUID.randomUUID();
        private static final UUID ATTACK_UUID = UUID.randomUUID();
        private static final UUID ARMOR_UUID = UUID.randomUUID();

        public FairyEffect() {
            super(MobEffectCategory.HARMFUL, 0xFF99CC);
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_UUID.toString(), -0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL);
                        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_UUID.toString(), -1.0D, AttributeModifier.Operation.ADDITION);
                        this.addAttributeModifier(Attributes.ARMOR, ARMOR_UUID.toString(), -1.0D, AttributeModifier.Operation.ADDITION);
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            // every 40 ticks (2 seconds)
            int interval = 40;
            return duration % interval == 0;
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return;
            int dmg = amplifier + 1;
            DamageSource src = entity.damageSources().magic();
            entity.hurt(src, dmg);
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
