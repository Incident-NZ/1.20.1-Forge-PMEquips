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
        private static final ResourceLocation HEALTH_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "kcorpsingularityeffect_health_uuid");
        private static final ResourceLocation ARMOR_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "kcorpsingularityeffect_armor_uuid");

        public KCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x88CC88);
            this.addAttributeModifier(Attributes.MAX_HEALTH, HEALTH_UUID, 20.0D, AttributeModifier.Operation.ADD_VALUE);
            this.addAttributeModifier(Attributes.ARMOR, ARMOR_UUID, 10.0D, AttributeModifier.Operation.ADD_VALUE);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            // every 60 ticks (3 seconds)
            return duration % 60 == 0;
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return true;
            // Heal 1 HP and restore hunger for players
            entity.heal(1.0F);
            if (entity instanceof Player player) {
                try {
                    player.getFoodData().eat(1, 0.0F);
                } catch (Exception ignored) {
                }
            }

            return true;
        }
    }

    public static class RCorpSingularityEffect extends MobEffect {
        private static final ResourceLocation MOVE_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "rcorpsingularityeffect_move_uuid");
        private static final ResourceLocation ATTACK_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "rcorpsingularityeffect_attack_uuid");

        public RCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x8899FF);
            // movement speed +50%
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_UUID, 0.50D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            // attack damage x2 => MULTIPLY_TOTAL with 1.0 (adds +100%)
            this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_UUID, 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    }

    public static class WCorpSingularityEffect extends MobEffect {

        public WCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xFFDD88);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            // every 200 ticks (10 seconds)
            return duration % 200 == 0;
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return true;
            var tag = entity.getPersistentData();
            int charges = tag.getInt("wcorp_evade_charges");
            charges += 5;
            tag.putInt("wcorp_evade_charges", charges);

            return true;
        }
    }
}
