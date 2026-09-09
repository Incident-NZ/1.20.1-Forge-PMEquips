package net.pm_equips.items;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;


public class LCorpEGOL5WhiteNight extends GeoArmorItem {
    public LCorpEGOL5WhiteNight(Properties props) {
        super(props);
    }

    @net.neoforged.fml.common.EventBusSubscriber(modid = "pm_equips")
    public static class WhiteNightEvents{
        @SubscribeEvent
        public static void onLivingHurt(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;

            // If player is wearing this item in any armor slot -> cancel damage <= 5
            boolean wearing = false;
            for (ItemStack s : player.getArmorSlots()) {
                if (s.getItem() instanceof LCorpEGOL5WhiteNight) { wearing = true; break; }
            }

            float damage = event.getAmount();

            if (wearing) {
                if (damage <= 5.0f) {
                    event.setCanceled(true);
                    return;
                }
            }

            // Set effect: if player has this armor AND has LCorpEGOR5WhiteNight in main hand, absorb damage <=10
            ItemStack main = player.getMainHandItem();
            boolean holdingWeapon = main.getItem().getClass().getSimpleName().equals("LCorpEGOR5WhiteNight") || main.getItem() instanceof net.pm_equips.items.LCorpEGOR5WhiteNight;

            if (wearing && holdingWeapon) {
                if (damage <= 10.0f) {
                    event.setCanceled(true);
                    // add absorption equal to damage
                    player.setAbsorptionAmount(player.getAbsorptionAmount() + damage);
                }
            }
        }
    }
}

