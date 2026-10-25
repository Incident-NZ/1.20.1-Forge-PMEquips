package net.pm_equips.items;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.pm_equips.client.renderer.EGOS5SmileR;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.items.materials.ArmorEquips;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

/**
 * 装備中にモブを倒し、そのドロップを拾うと体力全回復
 */
public class LobEGOSuit5Smile extends ArmorItem implements GeoItem {

    private static final String TAG_SMILE_DROP = "EGOP5SmileDrop";
    private static boolean EVENT_REGISTERED = false;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LobEGOSuit5Smile(ArmorEquips material, ArmorItem.Type type, Properties props) {
        super(material.asHolder(), type, props.durability(material.getDurabilityForType(type)));
        if (!EVENT_REGISTERED) {
            NeoForge.EVENT_BUS.register(SmileEvents.class);
            EVENT_REGISTERED = true;
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private EGOS5SmileR renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<T> original) {
                if (this.renderer == null) {
                    this.renderer = new EGOS5SmileR();
                }
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
        return player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LobEGOSuit5Smile;
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
        public static void onItemPickup(ItemEntityPickupEvent.Post event) {
            Player player = event.getPlayer();
            if (player.level().isClientSide) return;
            if (!isWearing(player)) return;

            ItemEntity itemEntity = event.getItemEntity();
            if (itemEntity == null) return;
            if (!itemEntity.getPersistentData().getBoolean(TAG_SMILE_DROP)) return;

            // 全回復
            player.setHealth(player.getMaxHealth());

            // 同じドロップを重ね拾いしても何度も処理しないよう印を消す
            itemEntity.getPersistentData().remove(TAG_SMILE_DROP);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.SMILE_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
