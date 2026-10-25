package net.pm_equips.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.pm_equips.client.renderer.EGOS5StarR;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.items.materials.ArmorEquips;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class LobEGOSuit5Star extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LobEGOSuit5Star(ArmorEquips material, ArmorItem.Type type, Properties props) {
        super(material.asHolder(), type, props.durability(material.getDurabilityForType(type)));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private EGOS5StarR renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<T> original) {

                if (this.renderer == null)
                    this.renderer = new EGOS5StarR();
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
    public static class BlueStarEvents {
        private static final int COOLDOWN_TICKS = 200; // 10秒 = 200tick
        private static final Map<UUID, Integer> cooldowns = new HashMap<>();

        public static boolean BlueStarFullSet(Player player) {
            return player.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof LobEGOWeapon5Star &&
                    player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LobEGOSuit5Star;
        }

        @SubscribeEvent
        public static void onLivingTick(EntityTickEvent.Post event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;

            // フルセット確認
            if (!BlueStarFullSet(player)) return;

            UUID uuid = player.getUUID();
            int ticks = cooldowns.getOrDefault(uuid, 0);

            if (ticks <= 0) {
                // === 効果発動 ===
                // 同じチャンクにいるプレイヤー取得
                ChunkPos chunkPos = new ChunkPos(player.blockPosition());
                List<? extends Player> playersInChunk = player.level().players().stream()
                        .filter(p -> new ChunkPos(p.blockPosition()).equals(chunkPos))
                        .toList();

                for (Player target : playersInChunk) {
                    target.heal(5.0f);
                }
                player.heal(5.0f);

                // サウンドエフェクトとかもここで鳴らせる
                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.0f);

                // クールダウンリセット
                cooldowns.put(uuid, COOLDOWN_TICKS);
            } else {
                cooldowns.put(uuid, ticks - 1);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.STAR_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
