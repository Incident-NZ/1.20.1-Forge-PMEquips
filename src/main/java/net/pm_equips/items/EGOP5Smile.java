package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.pm_equips.client.renderer.EGOS5SmileR;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.function.Consumer;

/**
 * 装備中にモブを倒し、そのドロップを拾うと体力全回復
 */
public class EGOP5Smile extends CorePageItem {

    private static final String TAG_SMILE_DROP = "EGOP5SmileDrop";
    private static boolean EVENT_REGISTERED = false;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public EGOP5Smile(ArmorMaterial material, ArmorItem.Type type, Properties props) {
        super(material, type, props);
        if (!EVENT_REGISTERED) {
            MinecraftForge.EVENT_BUS.register(SmileEvents.class);
            EVENT_REGISTERED = true;
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private EGOS5SmileR renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity livingEntity,
                    ItemStack itemStack,
                    EquipmentSlot equipmentSlot,
                    HumanoidModel<?> original
            ) {
                if (this.renderer == null) {
                    this.renderer = new EGOS5SmileR();
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

    /** この防具を1部位でも装備しているか */
    public static boolean isWearing(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof EGOP5Smile) {
                return true;
            }
        }
        return false;
    }

    public static class SmileEvents {

        /**
         * 装備者が倒したモブのドロップに印を付ける
         */
        @SubscribeEvent
        public static void onLivingDrops(LivingDropsEvent event) {
            if (event.getEntity().level().isClientSide) return;

            Entity source = event.getSource().getEntity();
            if (!(source instanceof Player player)) return;
            if (!isWearing(player)) return;

            for (ItemEntity drop : event.getDrops()) {
                drop.getPersistentData().putBoolean(TAG_SMILE_DROP, true);
            }
        }

        /**
         * 印付きドロップを拾ったら体力全回復
         */
        @SubscribeEvent
        public static void onItemPickup(EntityItemPickupEvent event) {
            Player player = event.getEntity();
            if (player.level().isClientSide) return;
            if (!isWearing(player)) return;

            ItemEntity itemEntity = event.getItem();
            if (itemEntity == null) return;
            if (!itemEntity.getPersistentData().getBoolean(TAG_SMILE_DROP)) return;

            // 全回復
            player.setHealth(player.getMaxHealth());

            // 同じドロップを重ね拾いしても何度も処理しないよう印を消す
            itemEntity.getPersistentData().remove(TAG_SMILE_DROP);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.SMILE_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}