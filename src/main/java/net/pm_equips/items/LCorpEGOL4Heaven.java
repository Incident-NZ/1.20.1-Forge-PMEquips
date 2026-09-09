package net.pm_equips.items;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;


public class LCorpEGOL4Heaven extends GeoArmorItem {
    public LCorpEGOL4Heaven(Properties props) {
        super(props);
    }

    public static boolean hasFullSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof LCorpEGOL4Heaven &&
                player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LCorpEGOL4Heaven;
    }
}


