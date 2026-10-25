package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS2RegletM;
import net.pm_equips.items.LobEGOSuit2Reglet;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS2RegletR extends GeoArmorRenderer<LobEGOSuit2Reglet> {
    public EGOS2RegletR() {
        super(new EGOS2RegletM());
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
