package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.pm_equips.MobEffectInit;
import net.pm_equips.client.renderer.EGOS5TwilightR;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.PMEquipsMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class LobEGOSuit5Twilight extends net.minecraft.world.item.ArmorItem implements software.bernie.geckolib.animatable.GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LobEGOSuit5Twilight(ArmorMaterial material, ArmorItem.Type type, Properties props) {
        super(material, type, props);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
        private EGOS5TwilightR renderer;

        @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {

            if (this.renderer == null)
                this.renderer = new EGOS5TwilightR();

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

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || !(player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LobEGOSuit5Twilight)) {
            return;
        }
        Level level = player.level();
        if (!level.isClientSide) {
            boolean hasTwilightWeapon = player.getMainHandItem().getItem() instanceof LobEGOWeapon5Twilight;

            if (hasTwilightWeapon) {
                float missingHp = player.getMaxHealth() - player.getHealth();
                if (missingHp > 0) {
                    int amplifier = (int) Math.floor(missingHp); // 1HP減少ごとに+1段階
                    player.addEffect(new MobEffectInstance(MobEffectInit.POWER.get(), 40, amplifier, true, false, true));
                }
                if (player.tickCount % 100 == 0) { // 100tick = 5秒
                    List<LivingEntity> nearby = level.getEntitiesOfClass(
                            LivingEntity.class,
                            player.getBoundingBox().inflate(5.0D),
                            e -> e != player
                    );
                    for (LivingEntity target : nearby) {
                        target.invulnerableTime = 0;
                        target.hurt(level.damageSources().magic(), 6.0F);
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.TWILIGHT_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
