package net.pm_equips.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.WingRCorp4thRabbitWeapon2;

@Mod.EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID
)
public class CombatEvents {

    private static final String LAST_ATTACK_KEY =
            "LastEnergyAttackTick";

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onLivingDamage(
            LivingDamageEvent event)
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
                weapon.getOrCreateTag();

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

        event.setAmount(
                event.getAmount() * 2.0F);

        WingRCorp4thRabbitWeapon2
                .consumeEnergy(weapon);
    }
}