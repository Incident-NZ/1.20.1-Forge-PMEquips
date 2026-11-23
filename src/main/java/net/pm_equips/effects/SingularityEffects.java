package net.pm_equips.effects;

import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class SingularityEffects {

    public static class TCorpSingularityEffect extends MobEffect {
        public TCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xC9A35F);
            addAttributeModifier(Attributes.ATTACK_DAMAGE, "5d990a30-d11e-4d29-9381-a7c50954c101", .10,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.MOVEMENT_SPEED, "5d990a30-d11e-4d29-9381-a7c50954c102", .20,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    public static class KCorpSingularityEffect extends MobEffect {
        private static final UUID HEALTH_UUID = UUID.randomUUID();
        private static final UUID ARMOR_UUID = UUID.randomUUID();

        public KCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x88CC88);
            this.addAttributeModifier(Attributes.MAX_HEALTH, HEALTH_UUID.toString(), 20.0D, AttributeModifier.Operation.ADDITION);
            this.addAttributeModifier(Attributes.ARMOR, ARMOR_UUID.toString(), 10.0D, AttributeModifier.Operation.ADDITION);
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            // every 60 ticks (3 seconds)
            return duration % 60 == 0;
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return;
            // Heal 1 HP and restore hunger for players
            entity.heal(1.0F);
            if (entity instanceof Player player) {
                try {
                    player.getFoodData().eat(1, 0.0F);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static class RCorpSingularityEffect extends MobEffect {
        private static final UUID MOVE_UUID = UUID.randomUUID();
        private static final UUID ATTACK_UUID = UUID.randomUUID();

        public RCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x8899FF);
            // movement speed +50%
            this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVE_UUID.toString(), 0.50D, AttributeModifier.Operation.MULTIPLY_TOTAL);
            // attack damage x2 => MULTIPLY_TOTAL with 1.0 (adds +100%)
            this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_UUID.toString(), 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    public static class WCorpSingularityEffect extends MobEffect {

        public WCorpSingularityEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xFFDD88);
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            // every 200 ticks (10 seconds)
            return duration % 200 == 0;
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            if (entity.level().isClientSide) return;
            var tag = entity.getPersistentData();
            int charges = tag.getInt("wcorp_evade_charges");
            charges += 5;
            tag.putInt("wcorp_evade_charges", charges);
        }

        @Override
        public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
            // clear stored charges when effect removed
            var tag = entity.getPersistentData();
            tag.remove("wcorp_evade_charges");
            super.removeAttributeModifiers(entity, attributeMap, amplifier);
        }
    }
}
