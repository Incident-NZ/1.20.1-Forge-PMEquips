package net.pm_equips.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;


public class LCorpEGOL4CrimsonScar extends GeoArmorItem {
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("pm_equips", "ego_s4_crimson_scar_speed");
    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            SPEED_MODIFIER_ID,
            0.3D,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );
    private static final double DETECTION_RADIUS = 32.0D;

    public LCorpEGOL4CrimsonScar(Properties props) {
        super(props);
    }

    public static void updateSpeedModifier(Player player) {
        if (player.level().isClientSide) {
            return;
        }

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) {
            return;
        }

        if (hasNearbyHostile(player)) {
            if (!movementSpeed.hasModifier(SPEED_MODIFIER_ID)) {
                movementSpeed.addTransientModifier(SPEED_MODIFIER);
            }
        } else {
            movementSpeed.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    public static void removeSpeedModifier(Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    private static boolean hasNearbyHostile(Player player) {
        return !player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(DETECTION_RADIUS),
                entity -> entity != player && entity.isAlive() && entity instanceof Enemy
        ).isEmpty();
    }
}

