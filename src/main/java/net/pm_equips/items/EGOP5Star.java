package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pm_equips.client.renderer.EGOS5StarR;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class EGOP5Star extends CorePageItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public EGOP5Star(ArmorMaterial material, ArmorItem.Type type, Properties props) {
        super(material, type, props);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private EGOS5StarR renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {

                if (this.renderer == null)
                    this.renderer = new EGOS5StarR();

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
    public static class BlueStarEvents {
        private static final int COOLDOWN_TICKS = 200; // 10秒 = 200tick
        private static final Map<UUID, Integer> cooldowns = new HashMap<>();

        public static boolean BlueStarFullSet(Player player) {
            return player.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof EGOW5Star &&
                    player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof EGOP5Star;
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.STAR_ARMOR);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}

