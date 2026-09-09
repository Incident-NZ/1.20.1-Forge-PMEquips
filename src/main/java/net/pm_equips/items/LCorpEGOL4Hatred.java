package net.pm_equips.items;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class LCorpEGOL4Hatred extends GeoArmorItem {
    public LCorpEGOL4Hatred(Properties props) {
        super(props);
    }

    public static boolean hasFullSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof LCorpEGOL4Hatred &&
                player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LCorpEGOL4Hatred;
    }
}


