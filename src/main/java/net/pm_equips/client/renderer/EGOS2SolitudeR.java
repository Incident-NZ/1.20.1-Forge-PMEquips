package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS2SolitudeM;
import net.pm_equips.items.LCorpEGOL2Solitude;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS2SolitudeR extends GeoArmorRenderer<LCorpEGOL2Solitude> {
    public EGOS2SolitudeR() {
        super(new EGOS2SolitudeM());
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
