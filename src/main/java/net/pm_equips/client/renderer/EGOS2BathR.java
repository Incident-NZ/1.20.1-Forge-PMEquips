package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS2BathM;
import net.pm_equips.items.LobEGOSuit2Bath;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS2BathR extends GeoArmorRenderer<LobEGOSuit2Bath> {
    public EGOS2BathR() {
        super(new EGOS2BathM());
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
