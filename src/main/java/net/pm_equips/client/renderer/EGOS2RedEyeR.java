package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS2RedEyeM;
import net.pm_equips.items.LCorpEGOL2RedEye;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS2RedEyeR extends GeoArmorRenderer<LCorpEGOL2RedEye> {
    public EGOS2RedEyeR() {
        super(new EGOS2RedEyeM());
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
