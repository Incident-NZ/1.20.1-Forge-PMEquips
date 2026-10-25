package net.pm_equips.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.WingRCorp4thRabbitWeapon2;
import net.pm_equips.util.ItemStackData;

@EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID
)
public class CombatEvents {

    private static final String LAST_ATTACK_KEY =
            "LastEnergyAttackTick";

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onLivingDamage(
            LivingDamageEvent.Pre event)
    {
        if (!(event.getSource().getEntity()
                instanceof Player player))
        {
            return;
        }

        if (player instanceof FakePlayer)
        {
            return;
        }

        ItemStack weapon =
                player.getMainHandItem();

        if (!(weapon.getItem()
                instanceof WingRCorp4thRabbitWeapon2))
        {
            return;
        }

        if (!WingRCorp4thRabbitWeapon2
                .hasEnoughEnergy(weapon))
        {
            return;
        }

        CompoundTag tag =
                ItemStackData.copy(weapon);

        long currentTick =
                player.level().getGameTime();

        long lastTick =
                tag.getLong(
                        "LastEnergyAttackTick");

        if (lastTick == currentTick)
        {
            return;
        }

        tag.putLong(
                "LastEnergyAttackTick",
                currentTick);
        ItemStackData.set(weapon, tag);

        event.setNewDamage(
                event.getNewDamage() * 2.0F);

        WingRCorp4thRabbitWeapon2
                .consumeEnergy(weapon);
    }
}
