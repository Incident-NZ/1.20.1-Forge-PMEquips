package net.pm_equips.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.pm_equips.PMEquipsMain;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@net.neoforged.fml.common.EventBusSubscriber(modid = PMEquipsMain.MOD_ID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.GAME)
public class RolandMookDropHandler {
    private static final Map<UUID, Boolean> EXTRA_DROP_ENTITIES = new ConcurrentHashMap<>();

    public static void markForExtraDrops(LivingEntity entity) {
        EXTRA_DROP_ENTITIES.put(entity.getUUID(), Boolean.TRUE);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) {
            return;
        }

        if (!Boolean.TRUE.equals(EXTRA_DROP_ENTITIES.remove(entity.getUUID()))) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof Player)) {
            return;
        }

        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            int count = stack.getCount();
            int newCount = Math.max(1, (int) Math.ceil(count * 1.5F));

            if (newCount != count) {
                stack.setCount(newCount);
                drop.setItem(stack);
            }
        }
    }
}
