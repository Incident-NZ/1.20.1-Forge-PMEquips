package net.pm_equips.items;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.pm_equips.items.materials.ArmorEquips;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import net.pm_equips.client.renderer.RCorpArmor2R;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WingRCorpArmor2 extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public WingRCorpArmor2(ArmorEquips material, Type type, Properties properties) {
        super(material.asHolder(), Type.LEGGINGS, properties.durability(material.getDurabilityForType(Type.LEGGINGS)));
    }
    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private RCorpArmor2R renderer;
            @Override public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T entity, ItemStack stack,
                    EquipmentSlot slot, HumanoidModel<T> original) {
                if (renderer == null) renderer = new RCorpArmor2R();
                return renderer;
            }
        });
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.animations.idle"));
            return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public boolean isBarVisible(ItemStack stack) { return ClassEnergySupport.isBarVisible(stack); }
    @Override public int getBarWidth(ItemStack stack) { return ClassEnergySupport.getBarWidth(stack); }
    @Override public int getBarColor(ItemStack stack) { return 0x00FFFF; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ClassEnergySupport.appendEnergyTooltip(stack, tooltip);
        tooltip.add(Component.translatable("tooltip.pm_equips.rcorp_armor2"));
    }
}
