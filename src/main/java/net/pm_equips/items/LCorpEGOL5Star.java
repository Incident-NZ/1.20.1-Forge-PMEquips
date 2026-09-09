package net.pm_equips.items;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LCorpEGOL5Star extends GeoArmorItem {
    public LCorpEGOL5Star(Properties props) {
        super(props);
    }

    @net.neoforged.fml.common.EventBusSubscriber(modid = "pm_equips")
    public static class BlueStarEvents {
        private static final int COOLDOWN_TICKS = 200; // 10秒 = 200tick
        private static final Map<UUID, Integer> cooldowns = new HashMap<>();

        public static boolean BlueStarFullSet(Player player) {
            return player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof LCorpEGOL5Star &&
                    player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LCorpEGOL5Star;
        }

        @SubscribeEvent
        public static void onLivingTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
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
                    target.heal(6.0f);
                }
                player.heal(6.0f);

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
}

