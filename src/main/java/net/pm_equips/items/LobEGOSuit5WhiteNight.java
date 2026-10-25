package net.pm_equips.items;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.pm_equips.client.renderer.EGOS5WhitenightR;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.items.materials.ArmorEquips;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class LobEGOSuit5WhiteNight extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LobEGOSuit5WhiteNight(ArmorEquips material, ArmorItem.Type type, Item.Properties props) {
        super(material.asHolder(), type, props.durability(material.getDurabilityForType(type)));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private EGOS5WhitenightR renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<T> original) {
                if (this.renderer == null)
                    this.renderer = new EGOS5WhitenightR();
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

    @EventBusSubscriber(modid = "pm_equips")
    public static class WhiteNightEvents{
        @SubscribeEvent
        public static void onLivingHurt(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;

            boolean wearing = player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LobEGOSuit5WhiteNight;

            float damage = event.getAmount();

            if (wearing) {
                if (damage <= 5.0f) {
                    event.setCanceled(true);
                    return;
                }
            }

            // Set effect: if player has this armor AND has LobEGOWeapon5WhiteNight in main hand, absorb damage <=10
            ItemStack main = player.getMainHandItem();
            boolean holdingWeapon = main.getItem().getClass().getSimpleName().equals("LobEGOWeapon5WhiteNight") || main.getItem() instanceof LobEGOWeapon5WhiteNight;

            if (wearing && holdingWeapon) {
                if (damage <= 10.0f) {
                    event.setCanceled(true);
                    // add absorption equal to damage
                    player.setAbsorptionAmount(player.getAbsorptionAmount() + damage);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.WHITENIGHT_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
