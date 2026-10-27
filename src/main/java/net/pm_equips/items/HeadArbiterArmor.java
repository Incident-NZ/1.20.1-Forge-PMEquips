package net.pm_equips.items;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.renderer.HeadArbiterArmorR;
import net.pm_equips.items.materials.ArmorEquips;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HeadArbiterArmor extends ArmorItem implements GeoItem {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.animations.idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<ItemAttributeModifiers> modifiers = Suppliers.memoize(() ->
            super.getDefaultAttributeModifiers().withModifierAdded(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "arbiter_explosion_resistance"),
                            1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS));

    public HeadArbiterArmor() {
        super(ArmorEquips.HEAD_ARBITER.asHolder(), Type.LEGGINGS, new Properties().stacksTo(1)
                .rarity(Rarity.EPIC).component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
    }

    public static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (Attributes.ARMOR.value() instanceof RangedAttribute armor) armor.maxValue = Math.max(50, armor.getMaxValue());
            if (Attributes.ARMOR_TOUGHNESS.value() instanceof RangedAttribute toughness) toughness.maxValue = Math.max(50, toughness.getMaxValue());
        });
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return modifiers.get();
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private HeadArbiterArmorR renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<T> original) {
                if (this.renderer == null)
                    this.renderer = new HeadArbiterArmorR();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            state.getController().setAnimation(IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.pm_equips.arbiter_controls"));
        tooltip.add(Component.translatable("tooltip.pm_equips.arbiter_fairy"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
