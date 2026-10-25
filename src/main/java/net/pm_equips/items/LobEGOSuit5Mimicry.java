package net.pm_equips.items;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.pm_equips.client.renderer.EGOS5MimicryR;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.items.materials.ArmorEquips;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class LobEGOSuit5Mimicry extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LobEGOSuit5Mimicry(ArmorEquips mat, ArmorItem.Type type, Properties props) {
        super(mat.asHolder(), type, props.durability(mat.getDurabilityForType(type)));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private EGOS5MimicryR renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T lEnt, ItemStack stack, EquipmentSlot equip, HumanoidModel<T> og) {

                if (this.renderer == null)
                    this.renderer = new EGOS5MimicryR();
                return this.renderer;
            }
        });
    }

    private PlayState predicate(AnimationState state) {
        state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.MIMICRY_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
