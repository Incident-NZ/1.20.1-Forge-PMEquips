package net.pm_equips.items;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.renderer.EGOS4CrimsonScarR;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.items.materials.ArmorEquips;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class LobEGOSuit4CrimsonScar extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final ResourceLocation SPEED_MODIFIER_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "lobegosuit4crimsonscar_speed_modifier_uuid");
    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(SPEED_MODIFIER_UUID, 0.3D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    private static final double DETECTION_RADIUS = 32.0D;

    public LobEGOSuit4CrimsonScar(ArmorEquips material, ArmorItem.Type type, Properties props) {
        super(material.asHolder(), type, props.durability(material.getDurabilityForType(type)));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private EGOS4CrimsonScarR renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<T> original) {

                if (this.renderer == null)
                    this.renderer = new EGOS4CrimsonScarR();
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

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LobEGOSuit4CrimsonScar) {
                updateSpeedModifier(player);
            } else {
                removeSpeedModifier(player);
            }
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
            if (!movementSpeed.hasModifier(SPEED_MODIFIER.id())) {
                movementSpeed.addTransientModifier(SPEED_MODIFIER);
            }
        } else {
            movementSpeed.removeModifier(SPEED_MODIFIER.id());
        }
    }

    private static void removeSpeedModifier(Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER.id());
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
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.CRIMSON_SCAR_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
