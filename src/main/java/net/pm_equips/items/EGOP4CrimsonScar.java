package net.pm_equips.items;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pm_equips.client.renderer.EGOS4CrimsonScarR;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class EGOP4CrimsonScar extends CorePageItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("9dff473f-e3f0-43df-9bde-87ccb2829d4a");
    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            SPEED_MODIFIER_UUID,
            "Crimson Scar hostile awareness",
            0.3D,
            AttributeModifier.Operation.MULTIPLY_TOTAL
    );
    private static final double DETECTION_RADIUS = 32.0D;

    public EGOP4CrimsonScar(ArmorMaterial material, ArmorItem.Type type, Properties props) {
        super(material, type, props);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private EGOS4CrimsonScarR renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {

                if (this.renderer == null)
                    this.renderer = new EGOS4CrimsonScarR();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this,"controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            updateSpeedModifier(player);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            removeSpeedModifier(player);
        }
    }

    private static void updateSpeedModifier(Player player) {
        if (player.level().isClientSide) {
            return;
        }

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) {
            return;
        }

        if (hasNearbyHostile(player)) {
            if (!movementSpeed.hasModifier(SPEED_MODIFIER)) {
                movementSpeed.addTransientModifier(SPEED_MODIFIER);
            }
        } else {
            movementSpeed.removeModifier(SPEED_MODIFIER);
        }
    }

    private static void removeSpeedModifier(Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER);
        }
    }

    private static boolean hasNearbyHostile(Player player) {
        return !player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(DETECTION_RADIUS),
                entity -> entity != player && entity.isAlive() && entity instanceof Enemy
        ).isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.CRIMSON_SCAR_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
