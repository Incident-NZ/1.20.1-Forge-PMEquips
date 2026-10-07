package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.client.model.HumanoidModel;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pm_equips.MobEffectInit;
import net.pm_equips.client.renderer.EGOS2RedEyeR;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.function.Consumer;

public class EGOP2RedEye extends CorePageItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public EGOP2RedEye(ArmorMaterial material, ArmorItem.Type type, Properties props) {
        super(material, type, props);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private EGOS2RedEyeR renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity livingEntity,
                    ItemStack itemStack,
                    EquipmentSlot equipmentSlot,
                    HumanoidModel<?> original
            ) {
                if (this.renderer == null) {
                    this.renderer = new EGOS2RedEyeR();
                }
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(
                RawAnimation.begin().then("idle", Animation.LoopType.LOOP)
        );
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player) {
            // この防具を装備中 かつ HP満タン → 武器と同じ QUICK Lv5
            if (isThisArmorEquipped(player, stack) && isFullHealth(player)) {
                player.addEffect(new MobEffectInstance(
                        MobEffectInit.QUICK.get(),
                        2,      // 毎tick更新用の短い時間
                        4,      // amplifier 4 = レベル5
                        false,
                        false,
                        true
                ));
            }
        }
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    /** この ItemStack が防具スロットに入っているか */
    private static boolean isThisArmorEquipped(Player player, ItemStack stack) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor == stack) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFullHealth(Player player) {
        return player.getHealth() >= player.getMaxHealth();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.RED_EYE_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}