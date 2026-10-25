package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS2BeakM;
import net.pm_equips.items.LobEGOSuit2Beak;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS2BeakR extends GeoArmorRenderer<LobEGOSuit2Beak> {
    public EGOS2BeakR() {
        super(new EGOS2BeakM());
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
