package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.pm_equips.client.renderer.EGOS5WhitenightR;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.function.Consumer;

public class LobEGOSuit5WhiteNight extends net.minecraft.world.item.ArmorItem implements software.bernie.geckolib.animatable.GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LobEGOSuit5WhiteNight(ArmorMaterial material, ArmorItem.Type type, Item.Properties props) {
        super(material, type, props);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private EGOS5WhitenightR renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new EGOS5WhitenightR();
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

    @Mod.EventBusSubscriber(modid = "pm_equips")
    public static class WhiteNightEvents{
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.WHITENIGHT_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
