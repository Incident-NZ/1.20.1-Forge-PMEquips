package net.pm_equips.events;

import net.pm_equips.util.ItemStackData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.WingsRCorp4thRabbitWeapon2;

@net.neoforged.fml.common.EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID
)
public class CombatEvents {

    private static final String LAST_ATTACK_KEY =
            "LastEnergyAttackTick";

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onLivingDamage(
            LivingIncomingDamageEvent event)
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
                instanceof WingsRCorp4thRabbitWeapon2))
        {
            return;
        }

        if (!WingsRCorp4thRabbitWeapon2
                .hasEnoughEnergy(weapon))
        {
            return;
        }

        CompoundTag tag = ItemStackData.copy(weapon);
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

        event.setAmount(
                event.getAmount() * 2.0F);

        WingsRCorp4thRabbitWeapon2
                .consumeEnergy(weapon);
    }
}
