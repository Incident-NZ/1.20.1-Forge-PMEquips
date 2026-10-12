package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS3HarvestM;
import net.pm_equips.items.LobEGOSuit3Harvest;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS3HarvestR extends GeoArmorRenderer<LobEGOSuit3Harvest> {
    public EGOS3HarvestR() {
        super(new EGOS3HarvestM());
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot slot) {
        setAllVisible(false);

        if (slot == EquipmentSlot.LEGS) {
            setAllVisible(true);
            setAllBonesVisible(true);
        }
    }
}
