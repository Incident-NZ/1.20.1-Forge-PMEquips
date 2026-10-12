package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS3BloodM;
import net.pm_equips.items.LobEGOSuit3Blood;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS3BloodR extends GeoArmorRenderer<LobEGOSuit3Blood> {
    public EGOS3BloodR() {
        super(new EGOS3BloodM());
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
